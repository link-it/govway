/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
 * DBConsegneMessageBoxManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DBConsegneMessageBoxManager implements IMonitoraggioRisorsa {

	
	private static DBConsegneMessageBoxManager staticInstanceDBRuntimeManager;
	public static synchronized void initRuntime(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase) throws Exception {
		if(staticInstanceDBRuntimeManager==null) {
			staticInstanceDBRuntimeManager = new DBConsegneMessageBoxManager(dbManagerRuntimePdD, alog, tipoDatabase);
		}
	}
	public static synchronized void initRuntime(DBConsegnePreseInCaricoManager dbConsegnePreseInCaricoManager,Logger alog,String tipoDatabase) throws Exception {
		if(staticInstanceDBRuntimeManager==null) {
			staticInstanceDBRuntimeManager = new DBConsegneMessageBoxManager(dbConsegnePreseInCaricoManager, alog, tipoDatabase,false);
		}
	}
	public static synchronized void initRuntime(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws Exception {
		if(staticInstanceDBRuntimeManager==null) {
			staticInstanceDBRuntimeManager = new DBConsegneMessageBoxManager(nomeDataSource, context, alog, tipoDatabase, useOp2UtilsDatasource, bindJMX,
					false);
		}
	}
	public static DBConsegneMessageBoxManager getInstanceRuntime() {
		return staticInstanceDBRuntimeManager;
	}
	
	
	private static DBConsegneMessageBoxManager staticInstanceDBTransazioniManager;
	public static synchronized void initTransazioni(DBTransazioniManager dbManagerTransazioni,Logger alog,String tipoDatabase) throws Exception {
		if(staticInstanceDBTransazioniManager==null) {
			staticInstanceDBTransazioniManager = new DBConsegneMessageBoxManager(dbManagerTransazioni, alog, tipoDatabase);
		}
	}
	public static synchronized void initTransazioni(DBConsegnePreseInCaricoManager dbConsegnePreseInCaricoManager,Logger alog,String tipoDatabase) throws Exception {
		if(staticInstanceDBTransazioniManager==null) {
			staticInstanceDBTransazioniManager = new DBConsegneMessageBoxManager(dbConsegnePreseInCaricoManager, alog, tipoDatabase, true);
		}
	}
	public static synchronized void initTransazioni(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws Exception {
		if(staticInstanceDBTransazioniManager==null) {
			staticInstanceDBTransazioniManager = new DBConsegneMessageBoxManager(nomeDataSource, context, alog, tipoDatabase, useOp2UtilsDatasource, bindJMX,
					true);
		}
	}
	public static DBConsegneMessageBoxManager getInstanceTransazioni() {
		return staticInstanceDBTransazioniManager;
	}
	
	
	/** Informazione sui proprietari che hanno richiesto una connessione */
	private static java.util.concurrent.ConcurrentHashMap<String,Resource> risorseInGestione_runtime = new java.util.concurrent.ConcurrentHashMap<String,Resource>();
	private static java.util.concurrent.ConcurrentHashMap<String,Resource> risorseInGestione_transazioni = new java.util.concurrent.ConcurrentHashMap<String,Resource>();
	
	public static String[] getStatoRisorse_runtime() throws Exception{	
		return DBManager.getStatoRisorse(DBConsegneMessageBoxManager.risorseInGestione_runtime);
	}
	public static String[] getStatoRisorse_transazioni() throws Exception{	
		return DBManager.getStatoRisorse(DBConsegneMessageBoxManager.risorseInGestione_transazioni);
	}
	
	
	private String tipoDatabase;
	public String getTipoDatabase() {
		return this.tipoDatabase;
	}

	private Logger log;
	
	private DBManager dbManagerRuntimePdD;
	private DBTransazioniManager dbManagerTransazioni;
	private DBConsegnePreseInCaricoManager dbConsegnePreseInCaricoManager;
	public boolean useDefaultManager() {
		return this.dbManagerRuntimePdD!=null || this.dbManagerTransazioni!=null;
	}
	public boolean useConsegnePreseInCaricoManager() {
		return this.dbConsegnePreseInCaricoManager!=null;
	}
	public boolean useRuntimePdD() {
		if(useDefaultManager()) {
			return this.dbManagerRuntimePdD!=null || this.dbManagerTransazioni.useRuntimePdD();
		}
		else if(useConsegnePreseInCaricoManager()) {
			return this.dbConsegnePreseInCaricoManager.useRuntimePdD();
		}
		return false;
	}
	private DataSource datasourceDedicato;
	private String identita;
	
	private DBConsegneMessageBoxManager(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase) throws Exception {
		this(alog, tipoDatabase, false);
		this.dbManagerRuntimePdD = dbManagerRuntimePdD;
	}
	private DBConsegneMessageBoxManager(DBTransazioniManager dbManagerTransazioni,Logger alog,String tipoDatabase) throws Exception {
		this(alog, tipoDatabase, true);
		this.dbManagerTransazioni = dbManagerTransazioni;
	}
	private DBConsegneMessageBoxManager(DBConsegnePreseInCaricoManager dbConsegnePreseInCaricoManager,Logger alog,String tipoDatabase, boolean tracce) throws Exception {
		this(alog, tipoDatabase, tracce);
		this.dbConsegnePreseInCaricoManager = dbConsegnePreseInCaricoManager;
	}

	private DBConsegneMessageBoxManager(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX,
			boolean tracce) throws Exception {
		
		this(alog, tipoDatabase, tracce);
		
		try {
			
			if(useOp2UtilsDatasource){
				DataSourceParams dsParams = Costanti.getDataSourceParamsPdD(bindJMX, tipoDatabase);
				try{
					this.datasourceDedicato = DataSourceFactory.newInstance(nomeDataSource, context, dsParams);
				}catch(UtilsAlreadyExistsException exists){
					this.datasourceDedicato = DataSourceFactory.getInstance(nomeDataSource); 
					if(this.datasourceDedicato==null){
						throw new Exception("Lookup datasource non riuscita ("+exists.getMessage()+")",exists);
					}
				}
			}
			else{
				GestoreJNDI gestoreJNDI = new GestoreJNDI(context);
				this.datasourceDedicato = (DataSource) gestoreJNDI.lookup(nomeDataSource);
			}
		} catch (Exception ne) {
			this.log.error("Impossibile instanziare il manager: " + ne.getMessage(),ne);
			throw ne;
		}
	}
	
	private DBConsegneMessageBoxManager(Logger alog,String tipoDatabase,boolean tracce) throws Exception {
		this.log = alog;
		this.tipoDatabase = tipoDatabase;
		if(tracce) {
			this.identita = "DBConsegneMessageBoxTransazioniManager";
		}
		else {
			this.identita = "DBConsegneMessageBoxRuntimeManager";
		}
			
	}
	private boolean isIamRuntimeManager() {
		return "DBConsegneMessageBoxRuntimeManager".equals(this.identita);
	}
	private boolean isIamTransazioniManager() {
		return "DBConsegneMessageBoxTransazioniManager".equals(this.identita);
	}
	
	
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione) throws Exception {
		return this.getResource(idPDD, modulo, idTransazione, true);
	}
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione, boolean logError) throws Exception {
		if(this.dbManagerRuntimePdD!=null) {
			//System.out.println("id ["+idTransazione+"] prendo da dbManager");
			return this.dbManagerRuntimePdD.getResource(idPDD, modulo, idTransazione, logError);
		}
		else if(this.dbManagerTransazioni!=null) {
			//System.out.println("id ["+idTransazione+"] prendo da dbManagerTransazioni");
			return this.dbManagerTransazioni.getResource(idPDD, modulo, idTransazione, logError);
		}
		else if(this.dbConsegnePreseInCaricoManager!=null) {
			//System.out.println("id ["+idTransazione+"] prendo da dbManagerTransazioni");
			return this.dbConsegnePreseInCaricoManager.getResource(idPDD, modulo, idTransazione, logError);
		}
		else {
			//System.out.println("id ["+idTransazione+"] negozio");
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
					bf.append(this.identita);
				}
				Resource risorsa = DBManager.buildResource(this.identita, this.getConnectionFromDatasource(bf.toString(), idTransazione),
						idPDD, modulo, idTransazione);	
				
				if(this.isIamRuntimeManager()) {
					DBConsegneMessageBoxManager.risorseInGestione_runtime.put(risorsa.getId(), risorsa);
				}
				else if(this.isIamTransazioniManager()) {
					DBConsegneMessageBoxManager.risorseInGestione_transazioni.put(risorsa.getId(), risorsa);
				}
				
				return risorsa;
			}
			catch(Exception e) {
				this.log.error("Errore durante l'ottenimento di una connessione: "+e.getMessage(),e);
				throw e;
			}
		}
	}
	
	private Connection getConnectionFromDatasource(String methodName, String idTransazione) throws Exception{
		if(this.datasourceDedicato instanceof org.openspcoop2.utils.datasource.DataSource){
			return ((org.openspcoop2.utils.datasource.DataSource)this.datasourceDedicato).getWrappedConnection(idTransazione, this.identita+"."+methodName);
		}
		else{
			return this.datasourceDedicato.getConnection();
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
			else if(this.dbConsegnePreseInCaricoManager!=null) {
				this.dbConsegnePreseInCaricoManager.releaseResource(idPDD, modulo, resource, logError);
			}
			else {
				if(resource!=null){
					
					if(resource.getResource()!=null){
						Connection connectionDB = (Connection) resource.getResource();
						if(connectionDB != null && (connectionDB.isClosed()==false)){
							connectionDB.close();
						}
					}	
					
					if(this.isIamRuntimeManager()) {
						if(DBConsegneMessageBoxManager.risorseInGestione_runtime.containsKey(resource.getId())) {
							DBConsegneMessageBoxManager.risorseInGestione_runtime.remove(resource.getId());
						}
					}
					else if(this.isIamTransazioniManager()) {
						if(DBConsegneMessageBoxManager.risorseInGestione_transazioni.containsKey(resource.getId())) {
							DBConsegneMessageBoxManager.risorseInGestione_transazioni.remove(resource.getId());
						}
					}

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
		else if(this.dbConsegnePreseInCaricoManager!=null) {
			this.dbConsegnePreseInCaricoManager.isAlive();
		}
		else {
			// Verifico la connessione
			Resource resource = null;
			Statement stmtTest = null;
			IDSoggetto idSoggettAlive = new IDSoggetto();
			idSoggettAlive.setCodicePorta(this.identita);
			idSoggettAlive.setTipo(this.identita);
			idSoggettAlive.setNome(this.identita);
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
				throw new CoreException("Connessione al database 'GovWay' ("+this.identita+") non disponibile: "+e.getMessage(),e);
	
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
}
