/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
 * DBConsegnePreseInCaricoManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DBConsegnePreseInCaricoManager implements IMonitoraggioRisorsa {

	private static DBConsegnePreseInCaricoManager staticInstanceDBSmistatoreManager;
	public static synchronized void initSmistatore(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase) throws Exception {
		if(staticInstanceDBSmistatoreManager==null) {
			staticInstanceDBSmistatoreManager = new DBConsegnePreseInCaricoManager(dbManagerRuntimePdD, alog, tipoDatabase,true);
		}
	}
	public static synchronized void initSmistatore(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws Exception {
		if(staticInstanceDBSmistatoreManager==null) {
			staticInstanceDBSmistatoreManager = new DBConsegnePreseInCaricoManager(nomeDataSource, context, alog, tipoDatabase, useOp2UtilsDatasource, bindJMX,
					true, false);
		}
	}
	public static DBConsegnePreseInCaricoManager getInstanceSmistatore() {
		return staticInstanceDBSmistatoreManager;
	}
	
	
	private static DBConsegnePreseInCaricoManager staticInstanceDBRuntimeManager;
	public static synchronized void initRuntime(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase) throws Exception {
		if(staticInstanceDBRuntimeManager==null) {
			staticInstanceDBRuntimeManager = new DBConsegnePreseInCaricoManager(dbManagerRuntimePdD, alog, tipoDatabase,false);
		}
	}
	public static synchronized void initRuntime(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws Exception {
		if(staticInstanceDBRuntimeManager==null) {
			staticInstanceDBRuntimeManager = new DBConsegnePreseInCaricoManager(nomeDataSource, context, alog, tipoDatabase, useOp2UtilsDatasource, bindJMX,
					false, false);
		}
	}
	public static DBConsegnePreseInCaricoManager getInstanceRuntime() {
		return staticInstanceDBRuntimeManager;
	}
	
	
	private static DBConsegnePreseInCaricoManager staticInstanceDBTransazioniManager;
	public static synchronized void initTransazioni(DBTransazioniManager dbManagerTransazioni,Logger alog,String tipoDatabase) throws Exception {
		if(staticInstanceDBTransazioniManager==null) {
			staticInstanceDBTransazioniManager = new DBConsegnePreseInCaricoManager(dbManagerTransazioni, alog, tipoDatabase);
		}
	}
	public static synchronized void initTransazioni(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws Exception {
		if(staticInstanceDBTransazioniManager==null) {
			staticInstanceDBTransazioniManager = new DBConsegnePreseInCaricoManager(nomeDataSource, context, alog, tipoDatabase, useOp2UtilsDatasource, bindJMX,
					false, true);
		}
	}
	public static DBConsegnePreseInCaricoManager getInstanceTransazioni() {
		return staticInstanceDBTransazioniManager;
	}
	
	
	/** Informazione sui proprietari che hanno richiesto una connessione */
	private static java.util.concurrent.ConcurrentHashMap<String,Resource> risorseInGestione_smistatore = new java.util.concurrent.ConcurrentHashMap<String,Resource>();
	private static java.util.concurrent.ConcurrentHashMap<String,Resource> risorseInGestione_runtime = new java.util.concurrent.ConcurrentHashMap<String,Resource>();
	private static java.util.concurrent.ConcurrentHashMap<String,Resource> risorseInGestione_transazioni = new java.util.concurrent.ConcurrentHashMap<String,Resource>();
	
	public static String[] getStatoRisorse_smistatore() throws Exception{	
		return DBManager.getStatoRisorse(DBConsegnePreseInCaricoManager.risorseInGestione_smistatore);
	}
	public static String[] getStatoRisorse_runtime() throws Exception{	
		return DBManager.getStatoRisorse(DBConsegnePreseInCaricoManager.risorseInGestione_runtime);
	}
	public static String[] getStatoRisorse_transazioni() throws Exception{	
		return DBManager.getStatoRisorse(DBConsegnePreseInCaricoManager.risorseInGestione_transazioni);
	}
	
	private String tipoDatabase;
	public String getTipoDatabase() {
		return this.tipoDatabase;
	}

	private Logger log;
	
	private DBManager dbManagerRuntimePdD;
	private DBTransazioniManager dbManagerTransazioni;
	public boolean useDefaultManager() {
		return this.dbManagerRuntimePdD!=null || this.dbManagerTransazioni!=null;
	}
	public boolean useRuntimePdD() {
		if(useDefaultManager()) {
			return this.dbManagerRuntimePdD!=null || this.dbManagerTransazioni.useRuntimePdD();
		}
		return false;
	}
	private DataSource datasourceDedicato;
	private String identita;
	
	private DBConsegnePreseInCaricoManager(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase,boolean smistatore) throws Exception {
		this(alog, tipoDatabase, smistatore, false);
		this.dbManagerRuntimePdD = dbManagerRuntimePdD;
	}
	private DBConsegnePreseInCaricoManager(DBTransazioniManager dbManagerTransazioni,Logger alog,String tipoDatabase) throws Exception {
		this(alog, tipoDatabase, false, true);
		this.dbManagerTransazioni = dbManagerTransazioni;
	}

	private DBConsegnePreseInCaricoManager(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX,
			boolean smistatore, boolean tracce) throws Exception {
		
		this(alog, tipoDatabase, smistatore, tracce);
		
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
	
	private DBConsegnePreseInCaricoManager(Logger alog,String tipoDatabase,boolean smistatore, boolean tracce) throws Exception {
		this.log = alog;
		this.tipoDatabase = tipoDatabase;
		if(smistatore) {
			this.identita = "DBConsegnePreseInCaricoSmistatoreManager";
		}
		else {
			if(tracce) {
				this.identita = "DBConsegnePreseInCaricoTransazioniManager";
			}
			else {
				this.identita = "DBConsegnePreseInCaricoRuntimeManager";
			}
		}	
	}
	private boolean isIamSmistatoreManager() {
		return "DBConsegnePreseInCaricoSmistatoreManager".equals(this.identita);
	}
	private boolean isIamRuntimeManager() {
		return "DBConsegnePreseInCaricoRuntimeManager".equals(this.identita);
	}
	private boolean isIamTransazioniManager() {
		return "DBConsegnePreseInCaricoTransazioniManager".equals(this.identita);
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
				
				if(this.isIamSmistatoreManager()) {
					DBConsegnePreseInCaricoManager.risorseInGestione_smistatore.put(risorsa.getId(), risorsa);
				}
				else if(this.isIamRuntimeManager()) {
					DBConsegnePreseInCaricoManager.risorseInGestione_runtime.put(risorsa.getId(), risorsa);
				}
				else if(this.isIamTransazioniManager()) {
					DBConsegnePreseInCaricoManager.risorseInGestione_transazioni.put(risorsa.getId(), risorsa);
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
			else {
				if(resource!=null){
					
					if(resource.getResource()!=null){
						Connection connectionDB = (Connection) resource.getResource();
						if(connectionDB != null && (connectionDB.isClosed()==false)){
							connectionDB.close();
						}
					}	
					
					if(this.isIamSmistatoreManager()) {
						if(DBConsegnePreseInCaricoManager.risorseInGestione_smistatore.containsKey(resource.getId())) {
							DBConsegnePreseInCaricoManager.risorseInGestione_smistatore.remove(resource.getId());
						}
					}
					else if(this.isIamRuntimeManager()) {
						if(DBConsegnePreseInCaricoManager.risorseInGestione_runtime.containsKey(resource.getId())) {
							DBConsegnePreseInCaricoManager.risorseInGestione_runtime.remove(resource.getId());
						}
					}
					else if(this.isIamTransazioniManager()) {
						if(DBConsegnePreseInCaricoManager.risorseInGestione_transazioni.containsKey(resource.getId())) {
							DBConsegnePreseInCaricoManager.risorseInGestione_transazioni.remove(resource.getId());
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
