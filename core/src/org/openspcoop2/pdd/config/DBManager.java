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
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import javax.naming.RefAddr;
import javax.sql.DataSource;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.UniqueIdentifierException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.slf4j.Logger;


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

	private static final String ID_MODULO = "DBManager";
	
	/** DBManager */
	private static DBManager manager = null;
	/** TransactionIsolation level */
	private static volatile int transactionIsolationLevel = -1;
	public static void setTransactionIsolationLevel(int transactionIsolationLevel) {
		DBManager.transactionIsolationLevel = transactionIsolationLevel;
	}

	/** NomeJNDIC DataSource dove attingere connessioni */
	private String dataSourceJndiName = null;
	/** DataSource dove attingere connessioni */
	private DataSource dataSource = null;
	/** MsgDiagnostico */
	private MsgDiagnostico msgDiag = null;

	/** Informazione sui proprietari che hanno richiesto una connessione */
	private static java.util.concurrent.ConcurrentHashMap<String,Resource> risorseInGestione = new java.util.concurrent.ConcurrentHashMap<>();
	
	/** Informazioni sui check */
	private static volatile boolean getConnectionCheckAutoCommitDisabled;
	private static volatile boolean getConnectionCheckTransactionIsolation;
	private static volatile int getConnectionCheckTransactionIsolationExpected;
	
	/** Stato di inizializzazione del manager */
	private static volatile boolean initialized = false;

	public static String[] getStatoRisorse() {	
		return getStatoRisorse(DBManager.risorseInGestione);
	}
	public static String[] getStatoRisorse(java.util.concurrent.ConcurrentMap<String,Resource> risorseInGestione) {	
		String[] sNull = null;
		Object[] o = risorseInGestione.values().toArray(new Resource[0]);
		if(! (o instanceof Resource[]))
			return sNull;
		Resource[] resources = (Resource[]) o;
		if(resources.length<=0)
			return sNull;
	
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
	 * @throws SQLException 
	 *
	 * 
	 */
	private DBManager(String jndiName,java.util.Properties context) throws UtilsException, SQLException {

		this.msgDiag = MsgDiagnostico.newInstance(ID_MODULO);

		GestoreJNDI jndi = new GestoreJNDI(context);
		Object oSearch = jndi.lookup(jndiName);
		if(oSearch==null){
			throw new UtilsException("Lookup jndiResource ["+jndiName+"] not found");
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
						RefAddr refAddr = enR.nextElement();
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
			throw new UtilsException("lookup failed (object class: "+oSearch.getClass().getName()+")"+bf.toString()+": "+t.getMessage(),t);
		}
		/**this.jndiName = jndiName;*/
		
		// Prelevo livello di transaction isolation
		try(java.sql.Connection connectionTest = this.dataSource.getConnection();){
			DBManager.setTransactionIsolationLevel(connectionTest.getTransactionIsolation());
		}	    

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
	 * @throws SQLException 
	 * @throws UtilsException 
	 * 
	 */
	public static void initialize(String jndiName,java.util.Properties context) throws UtilsException, SQLException {
		DBManager.manager = new DBManager(jndiName,context);	
		DBManager.setInitialized(true);
		
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		if(properties!=null) {
			DBManager.getConnectionCheckAutoCommitDisabled = properties.isDataSourceGetConnectionCheckAutoCommitDisabled();
			DBManager.getConnectionCheckTransactionIsolation = properties.isDataSourceGetConnectionCheckTransactionIsolationLevel();
			if(DBManager.getConnectionCheckTransactionIsolation) {
				DBManager.getConnectionCheckTransactionIsolationExpected = properties.getDataSourceGetConnectionCheckTransactionIsolationLevelExpected();
			}
		}
	}

	/**
	 * Ritorna l'istanza di questo DBManager
	 *
	 * @return Istanza di DBManager
	 * 
	 */
	public static DBManager getInstance(){
		if(DBManager.manager==null) {
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (DBManager.class) {
				return DBManager.manager;
			}
		}
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
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione) throws UtilsException {
		return this.getResource(idPDD, modulo, idTransazione, true);
	}
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione, boolean logError) throws UtilsException {

		if(this.dataSource == null)
			throw new UtilsException("Datasource non istanziato");

		Resource risorsa = null;
		try {
			risorsa = DBManager.buildResource(this.dataSourceJndiName, this.dataSource, idPDD, modulo, idTransazione);
				
			DBManager.risorseInGestione.put(risorsa.getId(), risorsa);
			
			/**if(this.dataSource instanceof SharedPoolDataSource)
				System.out.println("IDLE["+((SharedPoolDataSource)this.dataSource).getNumIdle()+"] ACTIVE["+((SharedPoolDataSource)this.dataSource).getNumActive()+"]");*/
						
		}
		catch(Exception e) {
			if(logError) {
				this.msgDiag.aggiornaFiltri();
				this.msgDiag.setDominio(idPDD);
				this.msgDiag.setFunzione("DBManager."+modulo);
				this.msgDiag.logFatalError(e, "Richiesta connessione al datasource");
			}
			throw new UtilsException(e.getMessage(),e);
		}
		
		return risorsa;
	}

	public static Resource buildResource(String dataSourceJndiName, DataSource dataSource, IDSoggetto idPDD,String modulo,String idTransazione) throws UtilsException, SQLException, UniqueIdentifierException {
		if(dataSourceJndiName!=null) {
			// log
		}
		/**System.out.println("### buildResource ["+dataSourceJndiName+"] modulo:"+modulo+" idTransazione:"+idTransazione+" ...");*/
		Connection connectionDB = dataSource.getConnection();
		checkConnection(connectionDB);
		/**System.out.println("### buildResource ["+dataSourceJndiName+"] modulo:"+modulo+" idTransazione:"+idTransazione+" OK");*/
		return buildResource("DBRuntimeManager", connectionDB, idPDD, modulo, idTransazione);
	}
	private static void checkConnection(Connection connectionDB) throws UtilsException {
		if(connectionDB==null)
			throw new UtilsException("is null");
	}
	public static Resource buildResource(String managerId, Connection connectionDB, IDSoggetto idPDD,String modulo,String idTransazione) throws SQLException, UniqueIdentifierException {
		
		/**if(modulo==null || !modulo.contains("Timer") || modulo.contains("TimerConsegnaContenutiApplicativi")) {
			System.out.println("### buildResource managerId["+managerId+"] modulo:"+modulo+" idTransazione:"+idTransazione+" ...");
		}*/
		
		if(connectionDB!=null) {
			if(DBManager.getConnectionCheckAutoCommitDisabled &&
				!connectionDB.getAutoCommit()) {
				Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopResources(); 
				if(log!=null) {
					String msg = getPrefixLog(managerId, idPDD,modulo,idTransazione)+" Connessione ottenuta possiede autoCommit enabled ?";
					log.error(msg);
				}
			}
			if(DBManager.getConnectionCheckTransactionIsolation &&
				DBManager.getConnectionCheckTransactionIsolationExpected != connectionDB.getTransactionIsolation()) {
				Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopResources(); 
				if(log!=null) {
					String msg = getPrefixLog(managerId, idPDD,modulo,idTransazione)+" Connessione ottenuta possiede un transaction isolation level '"+connectionDB.getTransactionIsolation()+"' differente da quello atteso '"+DBManager.getConnectionCheckTransactionIsolationExpected+"'";
					log.error(msg);
				}
			}
		}
		
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
	private static String getPrefixLog(String managerId, IDSoggetto idPDD,String modulo,String idTransazione) {
		StringBuilder sb = new StringBuilder("");
		if(managerId!=null) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("[").append(managerId).append("]");
		}
		if(idTransazione!=null) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("<").append(idTransazione).append(">");
		}
		if(modulo!=null) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("modulo:").append(modulo);
		}
		if(idPDD!=null) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("soggetto:").append(idPDD.toString());
		}
		return sb.toString();
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
					Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopResources()!=null ? OpenSPCoop2Logger.getLoggerOpenSPCoopResources() : LoggerWrapperFactory.getLogger(DBManager.class);
					boolean checkAutocommit = (OpenSPCoop2Properties.getInstance()==null) || OpenSPCoop2Properties.getInstance().isJdbcCloseConnectionCheckAutocommit();
					boolean checkIsClosed = (OpenSPCoop2Properties.getInstance()==null) || OpenSPCoop2Properties.getInstance().isJdbcCloseConnectionCheckIsClosed();
					JDBCUtilities.closeConnection(log, connectionDB, checkAutocommit, checkIsClosed);
				}
				if(DBManager.risorseInGestione.containsKey(resource.getId()))
					DBManager.risorseInGestione.remove(resource.getId());
				
				/**System.out.println("### releaseResource ["+this.dataSourceJndiName+"] modulo:"+modulo+" idTransazione:"+resource.getId()+" OK");
				if(this.dataSource instanceof SharedPoolDataSource)
					System.out.println("CLOSE IDLE["+((SharedPoolDataSource)this.dataSource).getNumIdle()+"] ACTIVE["+((SharedPoolDataSource)this.dataSource).getNumActive()+"]");*/
									
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
	 * @throws CoreException eccezione che contiene il motivo della mancata connessione
	 */
	@Override
	public void isAlive() throws CoreException{
		// Verifico la connessione
		Resource resource = null;
		Statement stmtTest = null;
		IDSoggetto idSoggettAlive = new IDSoggetto();
		idSoggettAlive.setCodicePorta(ID_MODULO);
		idSoggettAlive.setTipo(ID_MODULO);
		idSoggettAlive.setNome(ID_MODULO);
		try {
			resource = getResource(idSoggettAlive);
			if(resource == null)
				throw new CoreException("Resource is null");
			if(resource.getResource() == null)
				throw new CoreException("Connessione is null");
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
	private Resource getResource(IDSoggetto idSoggettAlive) throws CoreException {
		try{
			return this.getResource(idSoggettAlive, "CheckIsAlive", null,
					false); // verra' loggato nel servizio di check, altrimenti ad ogni test viene registrato l'errore
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}


	public DataSource getDataSource() {
		return this.dataSource;
	}
}

