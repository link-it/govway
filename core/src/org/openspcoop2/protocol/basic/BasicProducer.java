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



package org.openspcoop2.protocol.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoException;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;


/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicProducer extends BasicComponentFactory implements IMonitoraggioRisorsa {
	
	/** Properties */
	protected Properties appenderProperties;
		
	/** DataSource dove attingere connessioni */
	protected DataSource ds = null;
	protected String datasource = null;
   
	/** Connessione diretta via JDBC */
	protected String connectionViaJDBCUrl = null;
	protected String connectionViaJDBCDriverJDBC = null;
	protected String connectionViaJDBCUsername = null;
	protected String connectionViaJDBCPassword = null;

    /** SingleConnection */
	protected volatile boolean singleConnection = false;
	protected HashMap<BasicProducerType, Connection> singleConnectionConnectionMap = new HashMap<>(); 
	protected HashMap<BasicProducerType, String> singleConnectionSourceMap = new HashMap<>(); 
	
	/** TipoDatabase */
	protected String tipoDatabase = null; 

	/** OpenSPCoop Connection */
	protected volatile boolean openspcoopConnection = false;
    
	/** Emit debug info */
	protected volatile boolean debug = false;

	/** forceIndex */
	protected boolean forceIndex = false;
	public void setForceIndex(boolean forceIndex) {
		this.forceIndex = forceIndex;
	}
	
	/** IProtocolConfiguration */
	protected IProtocolConfiguration protocolConfiguration;
	
	/** ISAlive */
	protected volatile boolean isAlive = true;
	
	private BasicProducerType producerType;
	
	public BasicProducer(IProtocolFactory<?> factory, BasicProducerType producerType) throws ProtocolException{
		super(factory);
		this.producerType = producerType;
	}
	
	

	protected synchronized void initConnection(DataSource dsA,String dataSourceS)throws ProtocolException{
		Connection singleConnectionConnection = this.singleConnectionConnectionMap.get(this.producerType);
    	if(singleConnectionConnection==null){
    		try{
    			String singleConnectionSource = dataSourceS;
    			singleConnectionConnection = dsA.getConnection();
    			this.singleConnectionConnectionMap.put(this.producerType, singleConnectionConnection);
    			this.singleConnectionSourceMap.put(this.producerType, singleConnectionSource);
    		}catch(Exception e){
    			 throw new ProtocolException("Inizializzazione single connection (via datasource) non riuscita",e);
    		}
    	}
    }
	protected synchronized void initConnection(String jdbcUrl,String jdbcDriver,String username, String password)throws ProtocolException{
		Connection singleConnectionConnection = this.singleConnectionConnectionMap.get(this.producerType);
    	if(singleConnectionConnection==null){
    		try{
    			String singleConnectionSource = "url:"+jdbcUrl+" driver:"+jdbcDriver+" username:"+username+" password:"+password;
    			if(username==null){
    				singleConnectionConnection = DriverManager.getConnection(jdbcUrl);
    			}
    			else{
    				singleConnectionConnection = DriverManager.getConnection(jdbcUrl,username,password);
    			}
    			this.singleConnectionConnectionMap.put(this.producerType, singleConnectionConnection);
    			this.singleConnectionSourceMap.put(this.producerType, singleConnectionSource);
    		}catch(Exception e){
    			 throw new ProtocolException("Inizializzazione single connection (via jdbc connection) non riuscita",e);
    		}
    	}
    }
	protected Connection getConnectionViaJDBC() throws SQLException{
    	if(this.connectionViaJDBCUsername==null){
			return DriverManager.getConnection(this.connectionViaJDBCUrl);
		}
		else{
			return DriverManager.getConnection(this.connectionViaJDBCUrl,
					this.connectionViaJDBCUsername,this.connectionViaJDBCPassword);
		}
    }
	
	protected BasicConnectionResult getConnection(Connection conOpenSPCoopPdD,String methodName) throws ProtocolException{
		Connection con = null;
		BasicConnectionResult cr = new BasicConnectionResult();
		cr.setReleaseConnection(false);
		if(this.debug){
			this.logInfo("@@ ["+methodName+"] SINGLE CONNECTION["+this.singleConnection+"] OPEN["+this.openspcoopConnection+"]");
		}
		if(!this.singleConnection){
			if(this.openspcoopConnection && conOpenSPCoopPdD!=null){
				/**System.out.println("["+methodName+"]@GET_CONNECTION@ USE CONNECTION OPENSPCOOP");*/
				if(this.debug){
					this.logInfo("@@ ["+methodName+"] GET_CONNECTION, USE CONNECTION OPENSPCOOP");
				}
				con = conOpenSPCoopPdD;
			}else{
				if(this.ds!=null){
					try{
						con = this.ds.getConnection();
						/**if(con == null)
							throw new Exception("Connessione non fornita");*/
						cr.setReleaseConnection(true);
						/**System.out.println("["+methodName+"]@GET_CONNECTION@ USE CONNECTION FROM DATASOURCE");*/
						if(this.debug){
							this.logInfo("@@ ["+methodName+"] GET_CONNECTION, USE CONNECTION FROM DATASOURCE");
						}
					}catch(Exception e){
						throw new ProtocolException("Errore durante il recupero di una connessione dal datasource ["+this.datasource+"]: "+e.getMessage());
					}
				}
				else{
					try{
						con = this.getConnectionViaJDBC();
						if(con == null)
							throw new ProtocolException("Connessione non fornita");
						cr.setReleaseConnection(true);
						/**System.out.println("["+methodName+"]@GET_CONNECTION@ USE CONNECTION VIA JDBC");*/
						if(this.debug){
							this.logInfo("@@ ["+methodName+"] GET_CONNECTION, USE CONNECTION VIA JDBC");
						}
					}catch(Exception e){
						throw new ProtocolException("Errore durante il recupero di una connessione via jdbc url["+this.connectionViaJDBCUrl+"] driver["+
								this.connectionViaJDBCDriverJDBC+"] username["+this.connectionViaJDBCUsername+"] password["+this.connectionViaJDBCPassword
								+"]: "+e.getMessage());
					}
				}
			}
		}else{
			con = this.singleConnectionConnectionMap.get(this.producerType);
			if(con == null){
				throw new ProtocolException("Connessione (singleConnection enabled) non fornita dalla sorgente ["+this.singleConnectionSourceMap.get(this.producerType)+"]");
			}
			/**System.out.println("["+methodName+"]@GET_CONNECTION@ SINGLE CONNECTION");*/
			if(this.debug){
				this.logInfo("@@ ["+methodName+"] GET_CONNECTION, SINGLE CONNECTION");
			}
		}
		cr.setConnection(con);
		return cr;
	}
	protected void releaseConnection(BasicConnectionResult connectionResult,String methodName) throws SQLException{
		if(this.debug){
			this.logInfo("@@ ["+methodName+"] RELEASE_CONNECTION ["+connectionResult.isReleaseConnection()+"]?");
		}
		if(connectionResult.isReleaseConnection()){
			/**System.out.println("["+methodName+"]@RELEASE_CONNECTION@");*/
			if(this.debug){
				this.logInfo("@@ ["+methodName+"] RELEASE_CONNECTION effettuato");
			}
			JDBCUtilities.closeConnection(BasicComponentFactory.getCheckLogger(), connectionResult.getConnection(), BasicComponentFactory.isCheckAutocommit(), BasicComponentFactory.isCheckIsClosed());
		}
	}
	

	
    

    
    /**
	 * Inizializza l'engine di un appender per la registrazione
	 * 
	 * @param appenderProperties Proprieta' dell'appender
	 * @throws MsgDiagnosticoException
	 */
	public void initializeAppender(OpenspcoopAppender appenderProperties, boolean tipoDatabaseRequired) throws ProtocolException{
		
		try{
		
			// Lettura modalita' di gestione
			this.appenderProperties = new Properties();
			if(appenderProperties.sizePropertyList()>0){
				
				for(int i=0; i<appenderProperties.sizePropertyList(); i++){
					this.appenderProperties.put(appenderProperties.getProperty(i).getNome(),
							appenderProperties.getProperty(i).getValore());
				}
				
				this.datasource = this.appenderProperties.getProperty("datasource");
				if(this.datasource==null){
					this.connectionViaJDBCUrl = this.appenderProperties.getProperty("connectionUrl");
					if(this.connectionViaJDBCUrl==null){
						String tmp = this.appenderProperties.getProperty("checkProperties");
						if(tmp == null || Boolean.valueOf(tmp.trim())) {
							throw new ProtocolException("Proprietà 'datasource' e 'connectionUrl' non definite (almeno una delle due è obbligatoria)");
						}
						else {
							this.isAlive = false;
						}
					}
				}
				
				this.tipoDatabase=this.appenderProperties.getProperty("tipoDatabase");
				// non obbligatorio in msg diagnostico per retrocompatibilità
				if(this.tipoDatabase==null && tipoDatabaseRequired){
					throw new ProtocolException("Proprieta' 'tipoDatabase' non definita");
				}
				if(this.tipoDatabase!=null &&
					!TipiDatabase.isAMember(this.tipoDatabase)){
					throw new ProtocolException("Proprieta' 'tipoDatabase' presenta un tipo ["+this.tipoDatabase+"] non supportato");
				}
				
				String singleConnectionString = this.appenderProperties.getProperty("singleConnection");
				if(singleConnectionString!=null){
					singleConnectionString = singleConnectionString.trim();
					if("true".equals(singleConnectionString)){
						this.singleConnection = true;
					}
				}
				
				String openspcoopConnectionString = this.appenderProperties.getProperty("usePdDConnection");
				if(openspcoopConnectionString!=null){
					openspcoopConnectionString = openspcoopConnectionString.trim();
					if("true".equals(openspcoopConnectionString)){
						this.openspcoopConnection = true;
					}
				}
				
			}else{
				throw new ProtocolException("Proprietà 'datasource' e 'connectionUrl' non definite (almeno una delle due è obbligatoria)");
			}
			
			// Datasource
			if(this.datasource!=null){
				
				java.util.Properties ctx = Utilities.readProperties("context-", this.appenderProperties);
			
				GestoreJNDI jndi = new GestoreJNDI(ctx);
				if(this.singleConnection){
					if(this.singleConnectionConnectionMap.get(this.producerType)==null){
						DataSource dsL = (DataSource) jndi.lookup(this.datasource);
						initConnection(dsL,this.datasource);
					}
				}else{
					this.ds = (DataSource) jndi.lookup(this.datasource);
				}
				
			}
			
			
			// connectionViaJDBC
			if(this.connectionViaJDBCUrl!=null){
				
				this.connectionViaJDBCDriverJDBC = this.appenderProperties.getProperty("connectionDriver");
				if(this.connectionViaJDBCDriverJDBC==null){
					throw new ProtocolException("Proprietà 'connectionDriver' non definita (obbligatoria nella modalita' 'connection via jdbc')");
				}
				
				this.connectionViaJDBCUsername = this.appenderProperties.getProperty("connectionUsername");
				if(this.connectionViaJDBCUsername!=null){
					this.connectionViaJDBCPassword = this.appenderProperties.getProperty("connectionPassword");
					if(this.connectionViaJDBCPassword==null){
						throw new ProtocolException("Proprietà 'connectionPassword' non definita (obbligatoria nella modalita' 'connection via jdbc' se viene definita la proprieta' 'connectionUsername')");
					}
				}
				
				Class.forName(this.connectionViaJDBCDriverJDBC);
				
				if(this.singleConnection &&
					this.singleConnectionConnectionMap.get(this.producerType)==null){
					initConnection(this.connectionViaJDBCUrl,this.connectionViaJDBCDriverJDBC,this.connectionViaJDBCUsername,this.connectionViaJDBCPassword);
				}
			}
			
			// debug info
			String debugS = this.appenderProperties.getProperty("debug");
			if(debugS!=null){
				debugS = debugS.trim();
				if("true".equals(debugS)){
					this.debug = true;
				}
			}
			
			
			// Protocol Configuration
			this.protocolConfiguration = this.protocolFactory.createProtocolConfiguration();
			
		}catch(Exception e){
			throw new ProtocolException("Errore durante l'inizializzazione dell'appender: "+e.getMessage(),e);
		}
	}

	protected String getSQLStringValue(String value){
		if(value!=null && (!"".equals(value))){
			return value;
		}
		else{
			return null;
		}
	}
	
	
	
	public String getTipoDatabase() {
		return this.tipoDatabase;
	}

	public void setTipoDatabase(String tipoDatabase) {
		this.tipoDatabase = tipoDatabase;
	}
	

	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws DriverException eccezione che contiene il motivo della mancata connessione
	 */
	@Override
	public void isAlive() throws CoreException{
		if(!this.isAlive) {
			return;
		}
		// Verifico la connessione
		Connection con = null;
		Statement stmtTest = null;
		BasicConnectionResult cr = null;
		try {
			// Connessione al DB
			cr = this.getConnection(null,"isAlive");
			con = cr.getConnection();
			
			// test:
			stmtTest = con.createStatement();
			stmtTest.execute("SELECT * from db_info");
		} catch (Exception e) {
			throw new CoreException("Connessione al database '"+this.producerType+"' non disponibile: "+e.getMessage(),e);

		}finally{
			try{
				if(stmtTest!=null)
					stmtTest.close();
			}catch(Exception e){
				// close
			}
			try{
				this.releaseConnection(cr, "isAlive");
			}catch(Exception e){
				// close
			}
		}
	}

}


