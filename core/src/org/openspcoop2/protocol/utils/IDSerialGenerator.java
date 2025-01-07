/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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
import org.openspcoop2.utils.jdbc.JDBCUtilities;

/**
 * IDSerialGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGenerator {

	// ** STATIC INIT *** //

	private static DataSource datasourcePdD = null;
	public static synchronized void init(DataSource ds){
		if(IDSerialGenerator.datasourcePdD==null){
			IDSerialGenerator.datasourcePdD = ds;
		}
	}
	/**	public static DataSource getDatasourcePdD() {
			return IDCounterGenerator.datasourcePdD;
		}
		public static void setDatasourcePdD(DataSource datasourcePdD) {
			IDCounterGenerator.datasourcePdD = datasourcePdD;
		}*/
	private static Connection getConnectionPdD() throws SQLException, ProtocolException{
		if(IDSerialGenerator.datasourcePdD==null){
			throw new ProtocolException("IDSerialGenerator non inizializzato");
		}
		return IDSerialGenerator.datasourcePdD.getConnection();
	}
	private static void releaseConnectionPdD(Connection con) throws SQLException{
		JDBCUtilities.closeConnection(IDSerialGenerator.getCheckLogger(), con, IDSerialGenerator.isCheckAutocommit(), IDSerialGenerator.isCheckIsClosed());
	}

	static Logger checkLogger = null;
	static boolean checkIsClosed = true;
	static boolean checkAutocommit = true;
	public static boolean isCheckIsClosed() {
		return checkIsClosed;
	}
	public static void setCheckIsClosed(boolean checkIsClosed) {
		IDSerialGenerator.checkIsClosed = checkIsClosed;
	}
	public static boolean isCheckAutocommit() {
		return checkAutocommit;
	}
	public static void setCheckAutocommit(boolean checkAutocommit) {
		IDSerialGenerator.checkAutocommit = checkAutocommit;
	}
	public static Logger getCheckLogger() {
		return checkLogger;
	}
	public static void setCheckLogger(Logger checkLogger) {
		IDSerialGenerator.checkLogger = checkLogger;
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
			if(param!=null && IDSerialGeneratorType.ALFANUMERICO.equals(param.getTipo())){
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
			if(this.state instanceof StateMessage){
				StateMessage stateMsg = (StateMessage) this.state;
				if(stateMsg.getConnectionDB()!=null && !stateMsg.getConnectionDB().isClosed()){
					/**System.out.println("PRESA DALLO STATO");*/
					conDB = stateMsg.getConnectionDB();
				}
			}
			if(conDB==null){
				/**System.out.println("PRESA DAL DATASOURCE");*/
				conDB = IDSerialGenerator.getConnectionPdD();
				conDBFromDatasource = true;
			}
		}catch(Exception e){
			throw new ProtocolException("Connessione non disponibile: "+e.getMessage(),e);
		}
		checkConnection(conDB);

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
				if(this.log!=null) {
					this.log.error("Rilascio connessione non riuscito: "+e.getMessage(),e);
				}
				/**throw new ProtocolException("Rilascio connessione non riuscito: "+e.getMessage(),e);*/
			}

		}

	}

	private void checkConnection(Connection conDB) throws ProtocolException {
		if(conDB == null){
			throw new ProtocolException("Connessione non disponibile");
		}
	}

}
