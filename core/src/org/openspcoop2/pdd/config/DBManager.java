/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import javax.naming.RefAddr;
import javax.sql.DataSource;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.resources.GestoreJNDI;
//import org.apache.commons.dbcp.datasources.SharedPoolDataSource;


/**
 * Contiene la gestione delle connessioni ad un Database.
 * Il nome della risorsa JNDI da cui e' possibili attingere connessioni verso il Database, 
 * viene selezionato attraverso le impostazioni lette dal file 'govway.properties'
 * e gestite attraverso l'utilizzo della classe  {@link org.openspcoop2.pdd.config.OpenSPCoop2Properties}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DBManager implements IMonitoraggioRisorsa {

	/** DBManager */
	private static DBManager manager = null;
	/** TransactionIsolation level */
	private static int transactionIsolationLevel = -1;

	/** NomeJNDIC DataSource dove attingere connessioni */
	private String dataSourceJndiName = null;
	/** DataSource dove attingere connessioni */
	private DataSource dataSource = null;
	/** MsgDiagnostico */
	private MsgDiagnostico msgDiag = null;

	/** Informazione sui proprietari che hanno richiesto una connessione */
	private static java.util.concurrent.ConcurrentHashMap<String,Resource> risorseInGestione = new java.util.concurrent.ConcurrentHashMap<String,Resource>();
	
	/** Stato di inizializzazione del manager */
	private static boolean initialized = false;

	public static String[] getStatoRisorse() throws Exception{	
		return getStatoRisorse(DBManager.risorseInGestione);
	}
	public static String[] getStatoRisorse(java.util.concurrent.ConcurrentHashMap<String,Resource> risorseInGestione) throws Exception{	
		Object[] o = risorseInGestione.values().toArray(new Resource[0]);
		if(o==null)
			return null;
		Resource[] resources = (Resource[]) o;
		if(resources==null || resources.length<=0)
			return null;
	
		String [] r = new String[resources.length];
		for(int i=0; i<resources.length; i++){
			Resource rr = resources[i];
			r[i] = rr.getIdentificativoPorta()+"."+rr.getModuloFunzionale();
			if(rr.getIdTransazione()!=null){
				r[i] = r[i] +"."+rr.getIdTransazione();
			}
			SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
			r[i] = r[i] +" ("+dateformat.format(rr.getDate())+")";
		}
		return r;
		
	}


	/**
	 * Viene chiamato in causa per istanziare il datasource
	 *
	 * 
	 */
	public DBManager(String jndiName,java.util.Properties context) throws Exception {

		this.msgDiag = MsgDiagnostico.newInstance("DBManager");

		GestoreJNDI jndi = new GestoreJNDI(context);
		Object oSearch = jndi.lookup(jndiName);
		if(oSearch==null){
			throw new Exception("Lookup jndiResource ["+jndiName+"] not found");
		}
		try{
			this.dataSourceJndiName = jndiName;
			this.dataSource = (DataSource) oSearch;
		}catch(Throwable t){
			StringBuilder bf = new StringBuilder();
			if(oSearch instanceof javax.naming.Reference){
				javax.naming.Reference r = (javax.naming.Reference) oSearch;
				bf.append(" (Factory=");
				bf.append(r.getFactoryClassName());
				bf.append(" FactoryLocation=");
				bf.append(r.getFactoryClassLocation());
				Enumeration<RefAddr> enR = r.getAll();
				if(enR!=null){
					while (enR.hasMoreElements()) {
						RefAddr refAddr = (RefAddr) enR.nextElement();
						bf.append(" [").
							append("type=").
							append(refAddr.getType()).
							append(" content=").
							append(refAddr.getContent()).
							append("]");
					}
				}
				bf.append(")");
			}
			throw new Exception("lookup failed (object class: "+oSearch.getClass().getName()+")"+bf.toString()+": "+t.getMessage(),t);
		}
		//this.jndiName = jndiName;
		
		// Prelevo livello di transaction isolation
		java.sql.Connection connectionTest = this.dataSource.getConnection();
		DBManager.transactionIsolationLevel = connectionTest.getTransactionIsolation();
		connectionTest.close();	    

	}
	
	
    public static boolean isInitialized() {
    	return DBManager.initialized;
    }

    private static void setInitialized(boolean initialized) {
        DBManager.initialized = initialized;        
    }



	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader del QueueManager 
	 *
	 * @param jndiName Nome JNDI del Datasource
	 * @param context Contesto JNDI da utilizzare
	 * 
	 */
	public static void initialize(String jndiName,java.util.Properties context) throws Exception{
		DBManager.manager = new DBManager(jndiName,context);	
		DBManager.setInitialized(true);
	}

	/**
	 * Ritorna l'istanza di questo DBManager
	 *
	 * @return Istanza di DBManager
	 * 
	 */
	public static DBManager getInstance(){
		return DBManager.manager;
	}

	/**
	 * Ritorna il livello di isolamento di default del DataSource
	 *
	 * @return livello di isolamento di default del DataSource
	 * 
	 */
	public static int getTransactionIsolationLevel(){
		return DBManager.transactionIsolationLevel;
	}







	/**
	 * Viene chiamato in causa per ottenere una connessione al DB
	 *
	 * @param idPDD Identificatore della porta di dominio.
	 * @param modulo Modulo che richiede una connessione al database.
	 * @return java.sql.Connection aperta sul database.
	 * 
	 */
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione) throws Exception {
		return this.getResource(idPDD, modulo, idTransazione, true);
	}
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione, boolean logError) throws Exception {

		if(this.dataSource == null)
			throw new Exception("Datasource non istanziato");

		Resource risorsa = null;
		try {
			risorsa = DBManager.buildResource(this.dataSourceJndiName, this.dataSource, idPDD, modulo, idTransazione);
				
			DBManager.risorseInGestione.put(risorsa.getId(), risorsa);
			
			//if(this.dataSource instanceof SharedPoolDataSource)
			//	System.out.println("IDLE["+((SharedPoolDataSource)this.dataSource).getNumIdle()+"] ACTIVE["+((SharedPoolDataSource)this.dataSource).getNumActive()+"]");
						
		}
		catch(Exception e) {
			if(logError) {
				this.msgDiag.aggiornaFiltri();
				this.msgDiag.setDominio(idPDD);
				this.msgDiag.setFunzione("DBManager."+modulo);
				this.msgDiag.logFatalError(e, "Richiesta connessione al datasource");
			}
			throw e;
		}
		
		return risorsa;
	}

	public static Resource buildResource(String dataSourceJndiName, DataSource dataSource, IDSoggetto idPDD,String modulo,String idTransazione) throws Exception {
		//System.out.println("### buildResource ["+dataSourceJndiName+"] modulo:"+modulo+" idTransazione:"+idTransazione+" ...");
		Connection connectionDB = dataSource.getConnection();
		if(connectionDB==null)
			throw new Exception("is null");
		//System.out.println("### buildResource ["+dataSourceJndiName+"] modulo:"+modulo+" idTransazione:"+idTransazione+" OK");
		return buildResource(connectionDB, idPDD, modulo, idTransazione);
	}
	public static Resource buildResource(Connection connectionDB, IDSoggetto idPDD,String modulo,String idTransazione) throws Exception {
		
		Resource risorsa = new Resource();
		String idUnivoco = Resource.generaIdentificatoreUnivoco(idPDD, modulo);
		risorsa.setId(idUnivoco);
		risorsa.setDate(DateManager.getDate());
		risorsa.setIdentificativoPorta(idPDD);
		risorsa.setModuloFunzionale(modulo);
		risorsa.setResource(connectionDB);
		risorsa.setResourceType(Connection.class.getName());
		risorsa.setIdTransazione(idTransazione);
		
		return risorsa;

	}




	/**
	 * Viene chiamato in causa per rilasciare una connessione al DB, effettuando precedentemente un commit
	 *
	 * @param idPDD Identificatore della porta di dominio.
	 * @param modulo Modulo che richiede il rilascio di una connessione al database.
	 * @param resource Connessione da rilasciare.
	 * 
	 */
	public void releaseResource(IDSoggetto idPDD,String modulo,Resource resource){
		this.releaseResource(idPDD, modulo, resource, true);
	}
	public void releaseResource(IDSoggetto idPDD,String modulo,Resource resource, boolean logError){
		try {
			if(resource!=null){
				
				if(resource.getResource()!=null){
					Connection connectionDB = (Connection) resource.getResource();
					if(connectionDB != null && (connectionDB.isClosed()==false)){
						connectionDB.close();
					}
				}
				if(DBManager.risorseInGestione.containsKey(resource.getId()))
					DBManager.risorseInGestione.remove(resource.getId());
				
				//System.out.println("### releaseResource ["+this.dataSourceJndiName+"] modulo:"+modulo+" idTransazione:"+resource.getId()+" OK");
				//if(this.dataSource instanceof SharedPoolDataSource)
				//	System.out.println("CLOSE IDLE["+((SharedPoolDataSource)this.dataSource).getNumIdle()+"] ACTIVE["+((SharedPoolDataSource)this.dataSource).getNumActive()+"]");
									
			}

		}
		catch(SQLException e) {
			if(logError) {
				this.msgDiag.aggiornaFiltri();
				this.msgDiag.setDominio(idPDD);
				this.msgDiag.setFunzione("DBManager."+modulo);
				this.msgDiag.logFatalError(e, "Rilasciata connessione al datasource");
			}
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
		// Verifico la connessione
		Resource resource = null;
		Statement stmtTest = null;
		IDSoggetto idSoggettAlive = new IDSoggetto();
		idSoggettAlive.setCodicePorta("DBManager");
		idSoggettAlive.setTipo("DBManager");
		idSoggettAlive.setNome("DBManager");
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
			throw new CoreException("Connessione al database GovWay non disponibile: "+e.getMessage(),e);

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


	public DataSource getDataSource() {
		return this.dataSource;
	}
}

