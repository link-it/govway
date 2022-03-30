/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import java.sql.Statement;

import javax.sql.DataSource;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.UtilsAlreadyExistsException;
import org.openspcoop2.utils.datasource.DataSourceFactory;
import org.openspcoop2.utils.datasource.DataSourceParams;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.slf4j.Logger;

/**     
 * DBTransazioniManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DBStatisticheManager implements IMonitoraggioRisorsa {

	private static DBStatisticheManager staticInstanceDBStatisticheManager;
	public static synchronized void init(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase) throws Exception {
		if(staticInstanceDBStatisticheManager==null) {
			staticInstanceDBStatisticheManager = new DBStatisticheManager(dbManagerRuntimePdD, alog, tipoDatabase);
		}
	}
	public static synchronized void init(DBTransazioniManager dbManagerTransazioni,Logger alog,String tipoDatabase) throws Exception {
		if(staticInstanceDBStatisticheManager==null) {
			staticInstanceDBStatisticheManager = new DBStatisticheManager(dbManagerTransazioni, alog, tipoDatabase);
		}
	}
	public static synchronized void init(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws Exception {
		if(staticInstanceDBStatisticheManager==null) {
			staticInstanceDBStatisticheManager = new DBStatisticheManager(nomeDataSource, context, alog, tipoDatabase, useOp2UtilsDatasource, bindJMX);
		}
	}
	public static DBStatisticheManager getInstance() {
		return staticInstanceDBStatisticheManager;
	}
	
	
	/** Informazione sui proprietari che hanno richiesto una connessione */
	private static java.util.concurrent.ConcurrentHashMap<String,Resource> risorseInGestione = new java.util.concurrent.ConcurrentHashMap<String,Resource>();
	
	public static String[] getStatoRisorse() throws Exception{	
		return DBManager.getStatoRisorse(DBStatisticheManager.risorseInGestione);
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
	private DBTransazioniManager dbManagerTransazioni;
	public boolean useTransazioni() {
		return this.dbManagerTransazioni!=null;
	}
	private DataSource datasourceStatistiche;
	
	public DBStatisticheManager(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase) throws Exception {
		this.dbManagerRuntimePdD = dbManagerRuntimePdD;
		this.log = alog;
		this.tipoDatabase = tipoDatabase;
	}
	public DBStatisticheManager(DBTransazioniManager dbManagerTransazioni,Logger alog,String tipoDatabase) throws Exception {
		this.dbManagerTransazioni = dbManagerTransazioni;
		this.log = alog;
		this.tipoDatabase = tipoDatabase;
	}
	public DBStatisticheManager(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws Exception {
		try {
			this.log = alog;
			this.tipoDatabase = tipoDatabase;
			
			if(useOp2UtilsDatasource){
				DataSourceParams dsParams = Costanti.getDataSourceParamsPdD(bindJMX, tipoDatabase);
				try{
					this.datasourceStatistiche = DataSourceFactory.newInstance(nomeDataSource, context, dsParams);
				}catch(UtilsAlreadyExistsException exists){
					this.datasourceStatistiche = DataSourceFactory.getInstance(nomeDataSource); 
					if(this.datasourceStatistiche==null){
						throw new Exception("Lookup datasource non riuscita ("+exists.getMessage()+")",exists);
					}
				}
			}
			else{
				GestoreJNDI gestoreJNDI = new GestoreJNDI(context);
				this.datasourceStatistiche = (DataSource) gestoreJNDI.lookup(nomeDataSource);
			}
		} catch (Exception ne) {
			this.log.error("Impossibile instanziare il manager: " + ne.getMessage(),ne);
			throw ne;
		}
	}
	
	
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione) throws Exception {
		return this.getResource(idPDD, modulo, idTransazione, true);
	}
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione, boolean logError) throws Exception {
		if(this.dbManagerRuntimePdD!=null) {
			return this.dbManagerRuntimePdD.getResource(idPDD, modulo, idTransazione, logError);
		}
		else if(this.dbManagerTransazioni!=null) {
			return this.dbManagerTransazioni.getResource(idPDD, modulo, idTransazione, logError);
		}
		else {
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
					bf.append("DBStatisticheManager");
				}
				Resource risorsa = DBManager.buildResource("DBStatisticsManager", this.getConnectionFromDatasource(bf.toString(), idTransazione),
						idPDD, modulo, idTransazione);	
				
				DBStatisticheManager.risorseInGestione.put(risorsa.getId(), risorsa);
				
				return risorsa;
			}
			catch(Exception e) {
				this.log.error("Errore durante l'ottenimento di una connessione: "+e.getMessage(),e);
				throw e;
			}
		}
	}
	
	private Connection getConnectionFromDatasource(String methodName, String idTransazione) throws Exception{
		if(this.datasourceStatistiche instanceof org.openspcoop2.utils.datasource.DataSource){
			return ((org.openspcoop2.utils.datasource.DataSource)this.datasourceStatistiche).getWrappedConnection(idTransazione, "DBStatisticheManager."+methodName);
		}
		else{
			return this.datasourceStatistiche.getConnection();
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
			else if(this.dbManagerTransazioni!=null) {
				this.dbManagerTransazioni.releaseResource(idPDD, modulo, resource, logError);
			}
			else {
				if(resource!=null){
					
					if(resource.getResource()!=null){
						Connection connectionDB = (Connection) resource.getResource();
						if(connectionDB != null && (connectionDB.isClosed()==false)){
							connectionDB.close();
						}
					}	
					
					if(DBStatisticheManager.risorseInGestione.containsKey(resource.getId()))
						DBStatisticheManager.risorseInGestione.remove(resource.getId());
				}
			}

		}
		catch(Exception e) {
			this.log.error("Errore durante il rilascio di una risorsa: "+e.getMessage(),e);
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
		else if(this.dbManagerTransazioni!=null) {
			this.dbManagerTransazioni.isAlive();
		}
		else {
			// Verifico la connessione
			Resource resource = null;
			Statement stmtTest = null;
			IDSoggetto idSoggettAlive = new IDSoggetto();
			idSoggettAlive.setCodicePorta("DBStatisticheManager");
			idSoggettAlive.setTipo("DBStatisticheManager");
			idSoggettAlive.setNome("DBStatisticheManager");
			try {
				try{
					resource = this.getResource(idSoggettAlive, "CheckIsAlive", null);
				}catch(Exception e){
					throw e;
				}
				if(resource == null)
					throw new Exception("Resource is null");
				if(resource.getResource() == null)
					throw new Exception("Connessione is null");
				Connection con = (Connection) resource.getResource();
				// test:
				stmtTest = con.createStatement();
				stmtTest.execute("SELECT * from "+CostantiDB.DB_INFO);
						
			} catch (Exception e) {
				throw new CoreException("Connessione al database 'GovWay - Statistiche' non disponibile: "+e.getMessage(),e);
	
			}finally{
				try{
					if(stmtTest!=null)
						stmtTest.close();
				}catch(Exception e){}
				try{
					this.releaseResource(idSoggettAlive, "CheckIsAlive", resource);
				}catch(Exception e){}
			}
		}
	}
}
