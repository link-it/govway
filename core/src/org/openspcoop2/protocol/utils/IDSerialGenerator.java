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

package org.openspcoop2.protocol.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.id.serial.IDSerialGeneratorParameter;
import org.openspcoop2.utils.id.serial.IDSerialGeneratorType;

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
	private Logger log;
	private TipiDatabase tipoDatabase;
	public IDSerialGenerator(Logger log, IState state, TipiDatabase tipoDatabase){
		this.log = log;
		this.state = state;
		this.tipoDatabase = tipoDatabase;
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
		try{

			org.openspcoop2.utils.id.serial.IDSerialGenerator generator = new org.openspcoop2.utils.id.serial.IDSerialGenerator();
			identificativoUnivoco = generator.buildID(param, conDB, this.tipoDatabase, this.log);
			return identificativoUnivoco;
		}
		catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		finally{

			try{
				if(conDBFromDatasource)
					IDSerialGenerator.releaseConnectionPdD(conDB);	
			}catch(Exception e){
				throw new ProtocolException("Rilascio connessione non riuscito: "+e.getMessage(),e);
			}

		}

	}



}
