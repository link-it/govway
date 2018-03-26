/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.basic.diagnostica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoException;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;


/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione dei msg diagnostici su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DiagnosticProducer extends BasicComponentFactory implements IDiagnosticProducer{

	/** Properties */
	protected Properties appenderProperties;
		
	/** DataSource dove attingere connessioni */
	protected DataSource ds = null;
	protected String datasource = null;
   
	/** Connessione diretta via JDBC */
	protected String connectionViaJDBC_url = null;
	protected String connectionViaJDBC_driverJDBC = null;
	protected String connectionViaJDBC_username = null;
	protected String connectionViaJDBC_password = null;

    /** SingleConnection */
	protected boolean singleConnection = false;
	protected static Connection singleConnection_connection = null; 
	protected static String singleConnection_source = null;
	
	/** TipoDatabase */
	protected String tipoDatabase = null; 
    	    
    /** writeCorrelazione */
	protected static boolean writeCorrelazione = true;
    
	/** OpenSPCoop Connection */
	protected boolean openspcoopConnection = false;
    
	/** Emit debug info */
	protected boolean debug = false;

	/** forceIndex */
	protected boolean forceIndex = false;
	public void setForceIndex(boolean forceIndex) {
		this.forceIndex = forceIndex;
	}
	
	/** IProtocolConfiguration */
	protected IProtocolConfiguration protocolConfiguration;
	
	public DiagnosticProducer(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
	}
	
	

	protected synchronized void initConnection(DataSource dsA,String dataSourceS)throws Exception{
    	if(DiagnosticProducer.singleConnection_connection==null){
    		try{
    			DiagnosticProducer.singleConnection_source = dataSourceS;
    			DiagnosticProducer.singleConnection_connection = dsA.getConnection();
    		}catch(Exception e){
    			 throw new Exception("Inizializzazione single connection (via datasource) non riuscita",e);
    		}
    	}
    }
	protected synchronized void initConnection(String jdbcUrl,String jdbcDriver,String username, String password)throws Exception{
    	if(DiagnosticProducer.singleConnection_connection==null){
    		try{
    			DiagnosticProducer.singleConnection_source = "url:"+jdbcUrl+" driver:"+jdbcDriver+" username:"+username+" password:"+password;
    			if(username==null){
    				DiagnosticProducer.singleConnection_connection = DriverManager.getConnection(jdbcUrl);
    			}
    			else{
    				DiagnosticProducer.singleConnection_connection = DriverManager.getConnection(jdbcUrl,username,password);
    			}
    		}catch(Exception e){
    			 throw new Exception("Inizializzazione single connection (via jdbc connection) non riuscita",e);
    		}
    	}
    }
	protected Connection getConnectionViaJDBC() throws SQLException{
    	if(this.connectionViaJDBC_username==null){
			return DriverManager.getConnection(this.connectionViaJDBC_url);
		}
		else{
			return DriverManager.getConnection(this.connectionViaJDBC_url,
					this.connectionViaJDBC_username,this.connectionViaJDBC_password);
		}
    }
	
	protected DiagnosticConnectionResult getConnection(Connection conOpenSPCoopPdD,String methodName) throws Exception{
		Connection con = null;
		DiagnosticConnectionResult cr = new DiagnosticConnectionResult();
		cr.setReleaseConnection(false);
		if(this.debug){
			this.log.info("@@ ["+methodName+"] SINGLE CONNECTION["+this.singleConnection+"] OPEN["+this.openspcoopConnection+"]");
		}
		if(this.singleConnection==false){
			if(this.openspcoopConnection && conOpenSPCoopPdD!=null){
				//System.out.println("["+methodName+"]@GET_CONNECTION@ USE CONNECTION OPENSPCOOP");
				if(this.debug){
					this.log.info("@@ ["+methodName+"] GET_CONNECTION, USE CONNECTION OPENSPCOOP");
				}
				con = conOpenSPCoopPdD;
			}else{
				if(this.ds!=null){
					try{
						con = this.ds.getConnection();
						if(con == null)
							throw new Exception("Connessione non fornita");
						cr.setReleaseConnection(true);
						//System.out.println("["+methodName+"]@GET_CONNECTION@ USE CONNECTION FROM DATASOURCE");
						if(this.debug){
							this.log.info("@@ ["+methodName+"] GET_CONNECTION, USE CONNECTION FROM DATASOURCE");
						}
					}catch(Exception e){
						throw new Exception("Errore durante il recupero di una connessione dal datasource ["+this.datasource+"]: "+e.getMessage());
					}
				}
				else{
					try{
						con = this.getConnectionViaJDBC();
						if(con == null)
							throw new Exception("Connessione non fornita");
						cr.setReleaseConnection(true);
						//System.out.println("["+methodName+"]@GET_CONNECTION@ USE CONNECTION VIA JDBC");
						if(this.debug){
							this.log.info("@@ ["+methodName+"] GET_CONNECTION, USE CONNECTION VIA JDBC");
						}
					}catch(Exception e){
						throw new Exception("Errore durante il recupero di una connessione via jdbc url["+this.connectionViaJDBC_url+"] driver["+
								this.connectionViaJDBC_driverJDBC+"] username["+this.connectionViaJDBC_username+"] password["+this.connectionViaJDBC_password
								+"]: "+e.getMessage());
					}
				}
			}
		}else{
			con = DiagnosticProducer.singleConnection_connection;
			if(con == null){
				throw new Exception("Connessione (singleConnection enabled) non fornita dalla sorgente ["+DiagnosticProducer.singleConnection_source+"]");
			}
			//System.out.println("["+methodName+"]@GET_CONNECTION@ SINGLE CONNECTION");
			if(this.debug){
				this.log.info("@@ ["+methodName+"] GET_CONNECTION, SINGLE CONNECTION");
			}
		}
		cr.setConnection(con);
		return cr;
	}
	protected void releaseConnection(DiagnosticConnectionResult connectionResult,String methodName) throws SQLException{
		if(this.debug){
			this.log.info("@@ ["+methodName+"] RELEASE_CONNECTION ["+connectionResult.isReleaseConnection()+"]?");
		}
		if(connectionResult.isReleaseConnection()){
			//System.out.println("["+methodName+"]@RELEASE_CONNECTION@");
			if(this.debug){
				this.log.info("@@ ["+methodName+"] RELEASE_CONNECTION effettuato");
			}
			connectionResult.getConnection().close();
		}
	}
	

	
    

    
    /**
	 * Inizializza l'engine di un appender per la registrazione
	 * di un msg Diagnostico emesso da una porta di dominio.
	 * 
	 * @param appenderProperties Proprieta' dell'appender
	 * @throws MsgDiagnosticoException
	 */
	@Override
	public void initializeAppender(OpenspcoopAppender appenderProperties) throws MsgDiagnosticoException{
		
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
					this.connectionViaJDBC_url = this.appenderProperties.getProperty("connectionUrl");
					if(this.connectionViaJDBC_url==null){
						throw new MsgDiagnosticoException("Proprietà 'datasource' e 'connectionUrl' non definite (almeno una delle due è obbligatoria)");
					}
				}
				
				this.tipoDatabase=this.appenderProperties.getProperty("tipoDatabase");
				// non obbligatorio in msg diagnostico per retrocompatibilità
//				if(this.tipoDatabase==null){
//					throw new MsgDiagnosticoException("Proprieta' 'tipoDatabase' non definita");
//				}
				if(this.tipoDatabase!=null){
					if(!TipiDatabase.isAMember(this.tipoDatabase)){
						throw new MsgDiagnosticoException("Proprieta' 'tipoDatabase' presenta un tipo ["+this.tipoDatabase+"] non supportato");
					}
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
				throw new MsgDiagnosticoException("Proprietà 'datasource' e 'connectionUrl' non definite (almeno una delle due è obbligatoria)");
			}
			
			// Datasource
			if(this.datasource!=null){
				
				java.util.Properties ctx= new java.util.Properties();
				ctx = Utilities.readProperties("context-", this.appenderProperties);
			
				GestoreJNDI jndi = new GestoreJNDI(ctx);
				if(this.singleConnection){
					if(DiagnosticProducer.singleConnection_connection==null){
						DataSource ds = (DataSource) jndi.lookup(this.datasource);
						initConnection(ds,this.datasource);
					}
				}else{
					this.ds = (DataSource) jndi.lookup(this.datasource);
				}
				
			}
			
			
			// connectionViaJDBC
			if(this.connectionViaJDBC_url!=null){
				
				this.connectionViaJDBC_driverJDBC = this.appenderProperties.getProperty("connectionDriver");
				if(this.connectionViaJDBC_driverJDBC==null){
					throw new MsgDiagnosticoException("Proprietà 'connectionDriver' non definita (obbligatoria nella modalita' 'connection via jdbc')");
				}
				
				this.connectionViaJDBC_username = this.appenderProperties.getProperty("connectionUsername");
				if(this.connectionViaJDBC_username!=null){
					this.connectionViaJDBC_password = this.appenderProperties.getProperty("connectionPassword");
					if(this.connectionViaJDBC_password==null){
						throw new MsgDiagnosticoException("Proprietà 'connectionPassword' non definita (obbligatoria nella modalita' 'connection via jdbc' se viene definita la proprieta' 'connectionUsername')");
					}
				}
				
				Class.forName(this.connectionViaJDBC_driverJDBC);
				
				if(this.singleConnection){
					if(DiagnosticProducer.singleConnection_connection==null){
						initConnection(this.connectionViaJDBC_url,this.connectionViaJDBC_driverJDBC,this.connectionViaJDBC_username,this.connectionViaJDBC_password);
					}
				}
			}
			
			
			// correlazione
			String correlazioneString = this.appenderProperties.getProperty("correlazione");
			if(correlazioneString!=null && "false".equalsIgnoreCase(correlazioneString)){
				DiagnosticProducer.writeCorrelazione = false;
			}

			
			// debug info
			String debug = this.appenderProperties.getProperty("debug");
			if(debug!=null){
				debug = debug.trim();
				if("true".equals(debug)){
					this.debug = true;
				}
			}
			
			
			// Protocol Configuration
			this.protocolConfiguration = this.protocolFactory.createProtocolConfiguration();
			
		}catch(Exception e){
			throw new MsgDiagnosticoException("Errore durante l'inizializzazione dell'appender: "+e.getMessage(),e);
		}
	}

	private String getSQLStringValue(String value){
		if(value!=null && ("".equals(value)==false)){
			return value;
		}
		else{
			return null;
		}
	}
	
	/**
	 * Registra un msg Diagnostico emesso da una porta di dominio,
	 * utilizzando le informazioni definite dalla specifica SPC.
	 * 
	 * @param msgDiagnostico Messaggio diagnostico
	 * @throws MsgDiagnosticoException
	 */
	@Override
	public void log(Connection conOpenSPCoopPdD,MsgDiagnostico msgDiagnostico) throws MsgDiagnosticoException{
		PreparedStatement stmt = null;
		Connection con = null;
		String messaggio = msgDiagnostico.getMessaggio();
		DiagnosticConnectionResult cr = null;
		try{
			
			Date gdo = msgDiagnostico.getGdo();
			IDSoggetto idPorta = msgDiagnostico.getIdSoggetto();
			String idFunzione = msgDiagnostico.getIdFunzione();
			int severita = msgDiagnostico.getSeverita();
			String idBusta = msgDiagnostico.getIdBusta();
			String idBustaRisposta = msgDiagnostico.getIdBustaRisposta();
			String codiceDiagnostico = msgDiagnostico.getCodice();
			
			String idTransazione = msgDiagnostico.getProperty(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
			
			if(this.debug){
				this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] ....");
			}
			
			TipiDatabase tipo = null;
			if(this.tipoDatabase!=null){
				if(!TipiDatabase.isAMember(this.tipoDatabase)){
					throw new MsgDiagnosticoException("Tipo database ["+this.tipoDatabase+"] non supportato");
				}
				tipo = TipiDatabase.toEnumConstant(this.tipoDatabase);
			}
			
			//	Connessione al DB
			cr = this.getConnection(conOpenSPCoopPdD,"log");
			con = cr.getConnection();

			if(this.debug){
				this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] (getConnection finished) ....");
			}
			
			if(tipo==null){
			
				// Inserimento della traccia nel DB in modalità retro compatibile
				// in questa versione non viene recuperato l'id long
				
				if(this.debug){
					this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] (inserimentoDiagnostico BackwardCompatible) ....");
				}
				
				String updateString = "INSERT INTO "+CostantiDB.MSG_DIAGNOSTICI+" ("+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_CODICE+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_TIPO_SOGGETTO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_NOME_SOGGETTO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_SEVERITA+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO_RISPOSTA+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_PROTOCOLLO+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE+", "+
						CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE+""+
				") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?)";
				int index = 1;
				stmt = con.prepareStatement(updateString);
				if(gdo!=null)
					stmt.setTimestamp(index++, new java.sql.Timestamp(gdo.getTime()));
				else
					stmt.setTimestamp(index++,null);
				JDBCUtilities.setSQLStringValue(stmt,index++, idPorta.getCodicePorta());
				JDBCUtilities.setSQLStringValue(stmt,index++, idPorta.getTipo());
				JDBCUtilities.setSQLStringValue(stmt,index++, idPorta.getNome());
				JDBCUtilities.setSQLStringValue(stmt,index++, idFunzione);
				stmt.setInt(index++, severita);
				JDBCUtilities.setSQLStringValue(stmt,index++, messaggio);
				JDBCUtilities.setSQLStringValue(stmt,index++, idBusta);
				JDBCUtilities.setSQLStringValue(stmt,index++, idBustaRisposta);
				JDBCUtilities.setSQLStringValue(stmt,index++,msgDiagnostico.getProtocollo());
				JDBCUtilities.setSQLStringValue(stmt,index++, codiceDiagnostico);
				JDBCUtilities.setSQLStringValue(stmt,index++, idTransazione);
				stmt.executeUpdate();
				stmt.close();
				
				if(this.debug){
					this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] (inserimentoDiagnostico BackwardCompatible terminato) ....");
				}
				
			}
			else{
				
				// Modalità di inserimento dove viene recuperato l'id long
				
				if(this.debug){
					this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] (inserimentoDiagnostico) ....");
				}
				
				List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
				java.sql.Timestamp gdoT = null;
				if(gdo!=null)
					gdoT =  new java.sql.Timestamp(gdo.getTime());
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO, gdoT , InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_CODICE, getSQLStringValue(idPorta.getCodicePorta()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_TIPO_SOGGETTO, getSQLStringValue(idPorta.getTipo()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PDD_NOME_SOGGETTO, getSQLStringValue(idPorta.getNome()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDFUNZIONE, getSQLStringValue(idFunzione), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_SEVERITA, severita, InsertAndGeneratedKeyJDBCType.INT) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO, getSQLStringValue(messaggio), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO, getSQLStringValue(idBusta), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO_RISPOSTA, getSQLStringValue(idBustaRisposta), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_PROTOCOLLO, getSQLStringValue(msgDiagnostico.getProtocollo()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE, getSQLStringValue(codiceDiagnostico), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE, getSQLStringValue(idTransazione), InsertAndGeneratedKeyJDBCType.STRING) );
								
				// ** Insert and return generated key
				long iddiagnostico = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, tipo, 
						new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGNOSTICI, CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID, CostantiDB.MSG_DIAGNOSTICI_SEQUENCE, CostantiDB.MSG_DIAGNOSTICI_TABLE_FOR_ID),
						listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
				if(iddiagnostico<=0){
					throw new Exception("ID autoincrementale non ottenuto");
				}
				msgDiagnostico.setId(iddiagnostico);
				
				if(this.debug){
					this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] (inserimentoDiagnostico terminato) ....");
				}
			}
			
			if(this.debug){
				this.log.debug("@@ log idTransazione["+idTransazione+"] idBusta["+idBusta+"] completato");
			}
			
		}catch(Exception e){
			throw new MsgDiagnosticoException("Errore durante la registrazione del msg diagnostico: "+e.getMessage()+"\nIl messaggio era: "+messaggio,e);
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(Exception e){}
			try{
				this.releaseConnection(cr, "log");
			}catch(Exception e){}
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
		// Verifico la connessione
		Connection con = null;
		Statement stmtTest = null;
		DiagnosticConnectionResult cr = null;
		try {
			// Connessione al DB
			cr = this.getConnection(null,"isAlive");
			con = cr.getConnection();
			
			// test:
			stmtTest = con.createStatement();
			stmtTest.execute("SELECT * from db_info");
		} catch (Exception e) {
			throw new CoreException("Connessione al database MsgDiagnostici non disponibile: "+e.getMessage(),e);

		}finally{
			try{
				if(stmtTest!=null)
					stmtTest.close();
			}catch(Exception e){}
			try{
				this.releaseConnection(cr, "isAlive");
			}catch(Exception e){}
		}
	}

}
