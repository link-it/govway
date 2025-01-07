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

package org.openspcoop2.pdd.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsAlreadyExistsException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.datasource.DataSourceFactory;
import org.openspcoop2.utils.datasource.DataSourceParams;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.slf4j.Logger;

/**     
 * DBTransazioniManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DBTransazioniManager implements IMonitoraggioRisorsa {

	private static final String ID_MODULO = "DBTransazioniManager";
	
	private static DBTransazioniManager staticInstanceDBTransazioniManager;
	public static synchronized void init(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase) {
		if(staticInstanceDBTransazioniManager==null) {
			staticInstanceDBTransazioniManager = new DBTransazioniManager(dbManagerRuntimePdD, alog, tipoDatabase);
		}
	}
	public static synchronized void init(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws UtilsException {
		if(staticInstanceDBTransazioniManager==null) {
			staticInstanceDBTransazioniManager = new DBTransazioniManager(nomeDataSource, context, alog, tipoDatabase, useOp2UtilsDatasource, bindJMX);
		}
	}
	public static DBTransazioniManager getInstance() {
		// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
		if (staticInstanceDBTransazioniManager == null) {
	        synchronized (DBTransazioniManager.class) {
	            if (staticInstanceDBTransazioniManager == null) {
	                return null;
	            }
	        }
	    }
		return staticInstanceDBTransazioniManager;
	}
	
	
	/** Informazione sui proprietari che hanno richiesto una connessione */
	private static java.util.concurrent.ConcurrentHashMap<String,Resource> risorseInGestione = new java.util.concurrent.ConcurrentHashMap<>();
	
	public static String[] getStatoRisorse() {	
		return DBManager.getStatoRisorse(DBTransazioniManager.risorseInGestione);
	}
	
	
	private String tipoDatabase;
	public String getTipoDatabase() {
		return this.tipoDatabase;
	}

	private Logger log;
	
	private DBManager dbManagerRuntimePdD;
	public boolean useRuntimePdD() {
		return this.dbManagerRuntimePdD!=null;
	}
	private DataSource datasourceTransazioni;
	
	private DBTransazioniManager(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase) {
		this.dbManagerRuntimePdD = dbManagerRuntimePdD;
		this.log = alog;
		this.tipoDatabase = tipoDatabase;
	}
	private DBTransazioniManager(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws UtilsException {
		try {
			this.log = alog;
			this.tipoDatabase = tipoDatabase;
			
			if(useOp2UtilsDatasource){
				DataSourceParams dsParams = Costanti.getDataSourceParamsPdD(bindJMX, tipoDatabase);
				initDatasource(nomeDataSource, context, dsParams);
			}
			else{
				GestoreJNDI gestoreJNDI = new GestoreJNDI(context);
				this.datasourceTransazioni = (DataSource) gestoreJNDI.lookup(nomeDataSource);
			}
		} catch (Exception ne) {
			String msg = "Impossibile instanziare il manager: " + ne.getMessage();
			doError(msg, ne);
		}
	}
	private void initDatasource(String nomeDataSource, java.util.Properties context, DataSourceParams dsParams) throws UtilsException {
		try{
			this.datasourceTransazioni = DataSourceFactory.newInstance(nomeDataSource, context, dsParams);
		}catch(UtilsAlreadyExistsException exists){
			this.datasourceTransazioni = DataSourceFactory.getInstance(nomeDataSource); 
			if(this.datasourceTransazioni==null){
				throw new UtilsException("Lookup datasource non riuscita ("+exists.getMessage()+")",exists);
			}
		}
	}
	
	private void doError(String msg, Exception e) throws UtilsException {
		this.log.error(msg,e);
		throw new UtilsException(msg,e);
	}
	
	
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione) throws UtilsException {
		return this.getResource(idPDD, modulo, idTransazione, true);
	}
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione, boolean logError) throws UtilsException {
		if(this.dbManagerRuntimePdD!=null) {
			/**System.out.println("id ["+idTransazione+"] prendo da dbManager");*/
			return this.dbManagerRuntimePdD.getResource(idPDD, modulo, idTransazione, logError);
		}
		else {
			/**System.out.println("id ["+idTransazione+"] negozio");*/
			try {
				StringBuilder bf = new StringBuilder();
				if(idPDD!=null) {
					bf.append(idPDD.toString());
					if(modulo!=null) {
						bf.append(".");
					}
				}
				if(modulo!=null) {
					bf.append(modulo);
				}
				if(bf.length()<=0) {
					bf.append(ID_MODULO);
				}
				Resource risorsa = DBManager.buildResource("DBTransactionManager", this.getConnectionFromDatasource(bf.toString(), idTransazione),
						idPDD, modulo, idTransazione);	
				
				DBTransazioniManager.risorseInGestione.put(risorsa.getId(), risorsa);
				
				return risorsa;
			}
			catch(Exception e) {
				String msg = "Errore durante l'ottenimento di una connessione: "+e.getMessage();
				doError(msg, e);
				throw new UtilsException(msg,e);
			}
		}
	}
	
	private Connection getConnectionFromDatasource(String methodName, String idTransazione) throws SQLException{
		if(this.datasourceTransazioni instanceof org.openspcoop2.utils.datasource.DataSource){
			return ((org.openspcoop2.utils.datasource.DataSource)this.datasourceTransazioni).getWrappedConnection(idTransazione, "DBTransazioniManager."+methodName);
		}
		else{
			return this.datasourceTransazioni.getConnection();
		}
	}
	
	public void releaseResource(IDSoggetto idPDD,String modulo,Resource resource){
		this.releaseResource(idPDD, modulo, resource, true);
	}
	public void releaseResource(IDSoggetto idPDD,String modulo,Resource resource, boolean logError){
		try {
			if(this.dbManagerRuntimePdD!=null) {
				this.dbManagerRuntimePdD.releaseResource(idPDD, modulo, resource, logError);
			}
			else {
				releaseConnection(resource);
			}
		}
		catch(Exception e) {
			this.log.error("Errore durante il rilascio di una risorsa: "+e.getMessage(),e);
		}
	}
	private void releaseConnection(Resource resource) throws SQLException {
		if(resource!=null){
			
			if(resource.getResource()!=null){
				Connection connectionDB = (Connection) resource.getResource();
				Logger logResource = OpenSPCoop2Logger.getLoggerOpenSPCoopResources()!=null ? OpenSPCoop2Logger.getLoggerOpenSPCoopResources() : LoggerWrapperFactory.getLogger(DBTransazioniManager.class);
				boolean checkAutocommit = (OpenSPCoop2Properties.getInstance()==null) || OpenSPCoop2Properties.getInstance().isJdbcCloseConnectionCheckAutocommit();
				boolean checkIsClosed = (OpenSPCoop2Properties.getInstance()==null) || OpenSPCoop2Properties.getInstance().isJdbcCloseConnectionCheckIsClosed();
				JDBCUtilities.closeConnection(logResource, connectionDB, checkAutocommit, checkIsClosed);
			}	
			
			if(DBTransazioniManager.risorseInGestione.containsKey(resource.getId()))
				DBTransazioniManager.risorseInGestione.remove(resource.getId());
		}
	}
	
	
	
	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws DriverException eccezione che contiene il motivo della mancata connessione
	 */
	@Override
	public void isAlive() throws CoreException{
		if(this.dbManagerRuntimePdD!=null) {
			this.dbManagerRuntimePdD.isAlive();
		}
		else {
			// Verifico la connessione
			Resource resource = null;
			Statement stmtTest = null;
			IDSoggetto idSoggettAlive = new IDSoggetto();
			idSoggettAlive.setCodicePorta(ID_MODULO);
			idSoggettAlive.setTipo(ID_MODULO);
			idSoggettAlive.setNome(ID_MODULO);
			try {
				resource = getResource(idSoggettAlive);
				Connection con = (Connection) resource.getResource();
				// test:
				stmtTest = con.createStatement();
				stmtTest.execute("SELECT * from "+CostantiDB.DB_INFO);
						
			} catch (Exception e) {
				throw new CoreException("Connessione al database 'GovWay - Transazioni' non disponibile: "+e.getMessage(),e);
	
			}finally{
				try{
					if(stmtTest!=null)
						stmtTest.close();
				}catch(Exception e){
					// close
				}
				try{
					this.releaseResource(idSoggettAlive, "CheckIsAlive", resource);
				}catch(Exception e){
					// close
				}
			}
		}
	}
	private Resource getResource(IDSoggetto idSoggettAlive) throws UtilsException {
		Resource resource = null;
		try{
			resource = this.getResource(idSoggettAlive, "CheckIsAlive", null);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		if(resource == null)
			throw new UtilsException("Resource is null");
		if(resource.getResource() == null)
			throw new UtilsException("Connessione is null");
		return resource;
	}
}
