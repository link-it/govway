/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.protocol.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;

/**
 * IDSerialGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGenerator {

	// ** STATIC INIT *** //

	static private DataSource datasourcePdD = null;
	static public synchronized void init(DataSource ds){
		if(IDSerialGenerator.datasourcePdD==null){
			IDSerialGenerator.datasourcePdD = ds;
		}
	}
	//	public static DataSource getDatasourcePdD() {
	//		return IDCounterGenerator.datasourcePdD;
	//	}
	//	public static void setDatasourcePdD(DataSource datasourcePdD) {
	//		IDCounterGenerator.datasourcePdD = datasourcePdD;
	//	}
	private static Connection getConnectionPdD() throws SQLException, ProtocolException{
		if(IDSerialGenerator.datasourcePdD==null){
			throw new ProtocolException("IDSerialGenerator non inizializzato");
		}
		return IDSerialGenerator.datasourcePdD.getConnection();
	}
	private static void releaseConnectionPdD(Connection con) throws SQLException{
		if(con!=null)
			con.close();
	}

	// ** STATIC INIT *** //



	private IState state;
	public IDSerialGenerator(IState state){
		this.state = state;
	}

	public long buildIDAsNumber(IDSerialGeneratorParameter param) throws ProtocolException {
		try{
			if(IDSerialGeneratorType.ALFANUMERICO.equals(param)){
				throw new ProtocolException("IDSerialGeneratorType["+param.getTipo()+"] prevede anche caratteri alfanumerici");
			}
			return Long.parseLong(this.buildID(param));
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public String buildID(IDSerialGeneratorParameter param) throws ProtocolException {

		IDSerialGeneratorType tipo = param.getTipo();
		IProtocolFactory pf = param.getProtocolFactory();
		ConfigurazionePdD config = pf.getConfigurazionePdD();
		Logger log = config.getLog();

		// Check connessione
		Connection conDB = null;
		boolean conDBFromDatasource = false;
		try{
			if(this.state!=null){
				if(this.state instanceof StateMessage){
					StateMessage state = (StateMessage) this.state;
					if(state.getConnectionDB()!=null && state.getConnectionDB().isClosed()==false){
						//System.out.println("PRESA DALLO STATO");
						conDB = state.getConnectionDB();
					}
				}
			}
			if(conDB==null){
				//System.out.println("PRESA DAL DATASOURCE");
				conDB = IDSerialGenerator.getConnectionPdD();
				conDBFromDatasource = true;
			}
		}catch(Exception e){
			throw new ProtocolException("Connessione non disponibile: "+e.getMessage(),e);
		}
		if(conDB == null){
			throw new ProtocolException("Connessione non disponibile");
		}

		String identificativoUnivoco = null;
		int oldTransactionIsolation = -1;
		try{

			if ( IDSerialGeneratorType.MYSQL.equals(tipo) ) {

				try{
					oldTransactionIsolation = conDB.getTransactionIsolation();
					conDB.setAutoCommit(true);
				} catch(Exception er) {
					log.error("Creazione serial ["+tipo.name()+"] non riuscita (impostazione transazione): "+er.getMessage());
					throw new ProtocolException("Creazione serial ["+tipo.name()+"] non riuscita (impostazione transazione): "+er.getMessage());		
				}

				identificativoUnivoco = IDSerialGenerator_mysql.generate(conDB, param);
			} 

			else if ( IDSerialGeneratorType.ALFANUMERICO.equals(tipo) || IDSerialGeneratorType.NUMERIC.equals(tipo) || IDSerialGeneratorType.DEFAULT.equals(tipo) ) {

				// DEFAULT: numeric
				try{				
					oldTransactionIsolation = conDB.getTransactionIsolation();
					//System.out.println("SET TRANSACTION_SERIALIZABLE ("+conDB.getTransactionIsolation()+","+conDB.getAutoCommit()+")");
					// Il rollback, non servirebbe, pero le WrappedConnection di JBoss hanno un bug, per cui alcune risorse non vengono rilasciate.
					// Con il rollback tali risorse vengono rilasciate, e poi effettivamente la ConnectionSottostante emette una eccezione.
					try{
						conDB.rollback();
					}catch(Exception e){
						//System.out.println("ROLLBACK ERROR: "+e.getMessage());
					}
					conDB.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					conDB.setAutoCommit(false);
				} catch(Exception er) {
					log.error("Creazione serial non riuscita (impostazione transazione): "+er.getMessage(),er);
					throw new ProtocolException("Creazione serial non riuscita (impostazione transazione): "+er.getMessage(),er);		
				}

				if ( IDSerialGeneratorType.NUMERIC.equals(tipo) || IDSerialGeneratorType.DEFAULT.equals(tipo) )
					identificativoUnivoco = IDSerialGenerator_numeric.generate(conDB, param, config);
				else
					identificativoUnivoco = IDSerialGenerator_alphanumeric.generate(conDB, param, config);

			}

			else {

				throw new ProtocolException("Tipo di generazione ["+tipo+"] non supportata");

			}

			return identificativoUnivoco;
		}
		finally{

			// Ripristino Transazione
			try{
				conDB.setTransactionIsolation(oldTransactionIsolation);
				conDB.setAutoCommit(true);
			} catch(Exception er) {
				//System.out.println("ERROR UNSET:"+er.getMessage());
				log.error("Creazione serial non riuscita (ripristino transazione): "+er.getMessage());
				throw new ProtocolException("Creazione serial non riuscita (ripristino transazione): "+er.getMessage());
			}finally{

				try{
					if(conDBFromDatasource)
						IDSerialGenerator.releaseConnectionPdD(conDB);	
				}catch(Exception e){
					throw new ProtocolException("Rilascio connessione non riuscito: "+e.getMessage(),e);
				}

			}
		}

	}



}
