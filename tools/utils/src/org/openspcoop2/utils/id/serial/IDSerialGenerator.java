/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.id.serial;

import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;

/**
 * IDSerialGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGenerator {

	private InfoStatistics infoStatistics;
	public IDSerialGenerator(InfoStatistics infoStatistics){
		this.infoStatistics = infoStatistics;
	}
	public IDSerialGenerator(){
	}
	
	public void clearBuffer() {
		IDSerialGeneratorBuffer.clearBuffer();
	}
	public void clearBuffer(IDSerialGeneratorParameter param) {
		IDSerialGeneratorType tipo = param.getTipo();
		if ( IDSerialGeneratorType.ALFANUMERICO.equals(tipo) || IDSerialGeneratorType.NUMERIC.equals(tipo) || IDSerialGeneratorType.DEFAULT.equals(tipo) ) {
			if ( IDSerialGeneratorType.NUMERIC.equals(tipo) || IDSerialGeneratorType.DEFAULT.equals(tipo) )
				IDSerialGeneratorBuffer.clearBuffer(IDSerialGenerator_numeric.class);
			else
				IDSerialGeneratorBuffer.clearBuffer(IDSerialGenerator_alphanumeric.class);
		}
	}
	
	public long buildIDAsNumber(IDSerialGeneratorParameter param, Connection con, TipiDatabase tipoDatabase, Logger log) throws UtilsException {
		try{
			if(IDSerialGeneratorType.ALFANUMERICO.equals(param)){
				throw new UtilsException("IDSerialGeneratorType["+param.getTipo()+"] prevede anche caratteri alfanumerici");
			}
			return Long.parseLong(this.buildID(param,con,tipoDatabase,log));
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public String buildID(IDSerialGeneratorParameter param, Connection con, TipiDatabase tipoDatabase, Logger log) throws UtilsException {

		IDSerialGeneratorType tipo = param.getTipo();

		// Check connessione
		if(con == null){
			throw new UtilsException("Connessione non fornita");
		}
		try{
			if(con.isClosed()){
				throw new UtilsException("Connessione risulta già chiusa");
			}
		}catch(Exception e){
			throw new UtilsException("Test Connessione non riuscito: "+e.getMessage(),e);
		}

		boolean originalConnectionAutocommit = false;
		boolean autoCommitModificato = false;
		try{
			originalConnectionAutocommit = con.getAutoCommit();
		}catch(Exception e){
			throw new UtilsException("Verifica AutoCommit Connessione non riuscito: "+e.getMessage(),e); 
		}
		if(originalConnectionAutocommit==false){
			throw new UtilsException("Creazione serial ["+tipo.name()+"] non riuscita (Non e' possibile fornire una connessione con autocommit disabilitato poiche' l'utility ha necessita' di effettuare operazioni di commit/rollback)");		
		}
		
		int originalConnectionTransactionIsolation = -1;
		boolean transactionIsolationModificato = false;
		try{
			originalConnectionTransactionIsolation = con.getTransactionIsolation();
		}catch(Exception e){
			throw new UtilsException("Lettura livello di isolamento transazione della Connessione non riuscito: "+e.getMessage(),e); 
		}
		
		String identificativoUnivoco = null;
		try{

			if ( IDSerialGeneratorType.MYSQL.equals(tipo) ) {
				
				identificativoUnivoco = IDSerialGenerator_mysql.generate(con, param, log, this.infoStatistics);
			} 

			else if ( IDSerialGeneratorType.ALFANUMERICO.equals(tipo) || IDSerialGeneratorType.NUMERIC.equals(tipo) || IDSerialGeneratorType.DEFAULT.equals(tipo) ) {

				// DEFAULT: numeric
				try{				

					//System.out.println("SET TRANSACTION SERIALIZABLE ("+conDB.getTransactionIsolation()+","+conDB.getAutoCommit()+")");
					// Il rollback, non servirebbe, pero le WrappedConnection di JBoss hanno un bug, per cui alcune risorse non vengono rilasciate.
					// Con il rollback tali risorse vengono rilasciate, e poi effettivamente la ConnectionSottostante emette una eccezione.
					try{
						con.rollback();
					}catch(Exception e){
						//System.out.println("ROLLBACK ERROR: "+e.getMessage());
					}
					
					JDBCUtilities.setTransactionIsolationSerializable(tipoDatabase, con);
					transactionIsolationModificato = true;
					
					if(originalConnectionAutocommit){
						con.setAutoCommit(false);
						autoCommitModificato = true;
					}
					
				} catch(Exception er) {
					log.error("Creazione serial non riuscita (impostazione transazione): "+er.getMessage(),er);
					throw new UtilsException("Creazione serial non riuscita (impostazione transazione): "+er.getMessage(),er);		
				}

				if ( IDSerialGeneratorType.NUMERIC.equals(tipo) || IDSerialGeneratorType.DEFAULT.equals(tipo) )
					identificativoUnivoco = IDSerialGenerator_numeric.generate(con, tipoDatabase, param, log, this.infoStatistics);
				else
					identificativoUnivoco = IDSerialGenerator_alphanumeric.generate(con, tipoDatabase, param, log, this.infoStatistics);

			}

			else {

				throw new UtilsException("Tipo di generazione ["+tipo+"] non supportata");

			}

			return identificativoUnivoco;
		}
		finally{

			// Ripristino Transazione
			try{
				if(transactionIsolationModificato){
					con.setTransactionIsolation(originalConnectionTransactionIsolation);
				}
				if(autoCommitModificato){
					con.setAutoCommit(originalConnectionAutocommit);
				}
			} catch(Exception er) {
				//System.out.println("ERROR UNSET:"+er.getMessage());
				log.error("Creazione serial non riuscita (ripristino transazione): "+er.getMessage());
				throw new UtilsException("Creazione serial non riuscita (ripristino transazione): "+er.getMessage());
			}
		}

	}



}
