/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.protocol.basic.diagnostica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.diagnostica.IMsgDiagnosticoOpenSPCoopAppender;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazioneApplicativa;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazioneServizioApplicativo;
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
public class MsgDiagnosticoOpenSPCoopAppenderDB implements IMsgDiagnosticoOpenSPCoopAppender{

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
	
	/** AddIdTransazione */
	protected boolean addIdTransazione = false;
	
	/** AddGdoInfoAllTables */
	protected boolean addGdoForAllTables = false;
    
	/** Logger utilizzato per info. */
	protected Logger log = null;
	
	/** ProtocolFactory */
    protected IProtocolFactory protocolFactory;
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}
	/** forceIndex */
	protected boolean forceIndex = false;
	public void setForceIndex(boolean forceIndex) {
		this.forceIndex = forceIndex;
	}
	
	
	public MsgDiagnosticoOpenSPCoopAppenderDB(IProtocolFactory factory){
		this.log = factory.getLogger();
		this.protocolFactory = factory;
	}
	
	

	protected synchronized void initConnection(DataSource dsA,String dataSourceS)throws Exception{
    	if(MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_connection==null){
    		try{
    			MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_source = dataSourceS;
    			MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_connection = dsA.getConnection();
    		}catch(Exception e){
    			 throw new Exception("Inizializzazione single connection (via datasource) non riuscita",e);
    		}
    	}
    }
	protected synchronized void initConnection(String jdbcUrl,String jdbcDriver,String username, String password)throws Exception{
    	if(MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_connection==null){
    		try{
    			MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_source = "url:"+jdbcUrl+" driver:"+jdbcDriver+" username:"+username+" password:"+password;
    			if(username==null){
    				MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_connection = DriverManager.getConnection(jdbcUrl);
    			}
    			else{
    				MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_connection = DriverManager.getConnection(jdbcUrl,username,password);
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
	
	protected MsgDiagnosticoConnectionResult getConnection(Connection conOpenSPCoopPdD,String methodName) throws Exception{
		Connection con = null;
		MsgDiagnosticoConnectionResult cr = new MsgDiagnosticoConnectionResult();
		cr.setReleaseConnection(false);
		if(this.singleConnection==false){
			if(this.openspcoopConnection && conOpenSPCoopPdD!=null){
				//System.out.println("["+methodName+"]@GET_CONNECTION@ USE CONNECTION OPENSPCOOP");
				con = conOpenSPCoopPdD;
			}else{
				if(this.ds!=null){
					try{
						con = this.ds.getConnection();
						if(con == null)
							throw new Exception("Connessione non fornita");
						cr.setReleaseConnection(true);
						//System.out.println("["+methodName+"]@GET_CONNECTION@ USE CONNECTION FROM DATASOURCE");
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
					}catch(Exception e){
						throw new Exception("Errore durante il recupero di una connessione via jdbc url["+this.connectionViaJDBC_url+"] driver["+
								this.connectionViaJDBC_driverJDBC+"] username["+this.connectionViaJDBC_username+"] password["+this.connectionViaJDBC_password
								+"]: "+e.getMessage());
					}
				}
			}
		}else{
			con = MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_connection;
			if(con == null){
				throw new Exception("Connessione (singleConnection enabled) non fornita dalla sorgente ["+MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_source+"]");
			}
			//System.out.println("["+methodName+"]@GET_CONNECTION@ SINGLE CONNECTION");
		}
		cr.setConnection(con);
		return cr;
	}
	protected void releaseConnection(MsgDiagnosticoConnectionResult connectionResult,String methodName) throws SQLException{
		if(connectionResult.isReleaseConnection()){
			//System.out.println("["+methodName+"]@RELEASE_CONNECTION@");
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
					if(MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_connection==null){
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
					if(MsgDiagnosticoOpenSPCoopAppenderDB.singleConnection_connection==null){
						initConnection(this.connectionViaJDBC_url,this.connectionViaJDBC_driverJDBC,this.connectionViaJDBC_username,this.connectionViaJDBC_password);
					}
				}
			}
			
			
			// correlazione
			String correlazioneString = this.appenderProperties.getProperty("correlazione");
			if(correlazioneString!=null && "false".equalsIgnoreCase(correlazioneString)){
				MsgDiagnosticoOpenSPCoopAppenderDB.writeCorrelazione = false;
			}
			
			
			// scrittura id
			String addIdTransazioneString = this.appenderProperties.getProperty("addIdTransazione");
			if(addIdTransazioneString!=null){
				addIdTransazioneString = addIdTransazioneString.trim();
				if("true".equals(addIdTransazioneString)){
					this.addIdTransazione = true;
				}
			}
			
			
			// add gdo for all tables
			String addGdoForAllTablesString = this.appenderProperties.getProperty("addGdoForAllTables");
			if(addGdoForAllTablesString!=null){
				addGdoForAllTablesString = addGdoForAllTablesString.trim();
				if("true".equals(addGdoForAllTablesString)){
					this.addGdoForAllTables = true;
				}
			}
				
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
		MsgDiagnosticoConnectionResult cr = null;
		try{
			
			Date gdo = msgDiagnostico.getGdo();
			IDSoggetto idPorta = msgDiagnostico.getIdSoggetto();
			String idFunzione = msgDiagnostico.getIdFunzione();
			int severita = msgDiagnostico.getSeverita();
			String idBusta = msgDiagnostico.getIdBusta();
			String idBustaRisposta = msgDiagnostico.getIdBustaRisposta();
			String codiceDiagnostico = msgDiagnostico.getCodice();
			
			String idTransazione = msgDiagnostico.getProperty(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
			
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

			String codiceDiagnosticoColumnName = "";
			String codiceDiagnosticoColumnValue = "";
			if(codiceDiagnostico!=null){
				if(tipo==null){
					codiceDiagnosticoColumnName = " , codice";
				}
				else{
					codiceDiagnosticoColumnName = "codice";
				}
				codiceDiagnosticoColumnValue = " , ?";
			}
			
			String idTransazioneColumnName = "";
			String idTransazioneColumnValue = "";
			if(this.addIdTransazione){
				if(tipo==null){
					idTransazioneColumnName = " , id_transazione";
				}
				else{
					idTransazioneColumnName = "id_transazione";
				}
				idTransazioneColumnValue = " , ?";
			}
			
			if(tipo==null){
			
				// Inserimento della traccia nel DB in modalità retro compatibile
				// in questa versione non viene recuperato l'id long
				
				String updateString = "INSERT INTO "+CostantiDB.MSG_DIAGNOSTICI+" (gdo, pdd_codice, pdd_tipo_soggetto, pdd_nome_soggetto, idfunzione, severita, messaggio, idmessaggio, idmessaggio_risposta, protocollo"+codiceDiagnosticoColumnName+idTransazioneColumnName+") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?"+codiceDiagnosticoColumnValue+idTransazioneColumnValue+")";
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
				if(codiceDiagnostico!=null){
					JDBCUtilities.setSQLStringValue(stmt,index++, codiceDiagnostico);
				}
				if(this.addIdTransazione){
					JDBCUtilities.setSQLStringValue(stmt,index++, idTransazione);
				}
				stmt.executeUpdate();
				stmt.close();
				
			}
			else{
				
				// Modalità di inserimento dove viene recuperato l'id long
				
				List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
				java.sql.Timestamp gdoT = null;
				if(gdo!=null)
					gdoT =  new java.sql.Timestamp(gdo.getTime());
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("gdo", gdoT , InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("pdd_codice", getSQLStringValue(idPorta.getCodicePorta()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("pdd_tipo_soggetto", getSQLStringValue(idPorta.getTipo()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("pdd_nome_soggetto", getSQLStringValue(idPorta.getNome()), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("idfunzione", getSQLStringValue(idFunzione), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("severita", severita, InsertAndGeneratedKeyJDBCType.INT) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("messaggio", getSQLStringValue(messaggio), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("idmessaggio", getSQLStringValue(idBusta), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("idmessaggio_risposta", getSQLStringValue(idBustaRisposta), InsertAndGeneratedKeyJDBCType.STRING) );
				listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("protocollo", getSQLStringValue(msgDiagnostico.getProtocollo()), InsertAndGeneratedKeyJDBCType.STRING) );
				if(codiceDiagnostico!=null){
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(codiceDiagnosticoColumnName, getSQLStringValue(codiceDiagnostico), InsertAndGeneratedKeyJDBCType.STRING) );
				}
				if(this.addIdTransazione){
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(idTransazioneColumnName, getSQLStringValue(idTransazione), InsertAndGeneratedKeyJDBCType.STRING) );
				}
				
				// ** Insert and return generated key
				long iddiagnostico = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, tipo, 
						new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGNOSTICI, CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID, CostantiDB.MSG_DIAGNOSTICI_SEQUENCE, CostantiDB.MSG_DIAGNOSTICI_TABLE_FOR_ID),
						listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
				if(iddiagnostico<=0){
					throw new Exception("ID autoincrementale non ottenuto");
				}
				msgDiagnostico.setId(iddiagnostico);
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
		
	
	/**
	 * Creazione di un entry che permette di effettuare una correlazione con i msg diagnostici
	 * 
	 * @param msgDiagCorrelazione Informazioni di correlazione
	 * @throws MsgDiagnosticoException
	 */
	@Override
	public void logCorrelazione(Connection conOpenSPCoopPdD,MsgDiagnosticoCorrelazione msgDiagCorrelazione) throws MsgDiagnosticoException{
		
		if(MsgDiagnosticoOpenSPCoopAppenderDB.writeCorrelazione){
			PreparedStatement stmt = null;
			Connection con = null;
			MsgDiagnosticoConnectionResult cr = null;
			try{
				
				String idBusta = msgDiagCorrelazione.getIdBusta();
				Date gdo = msgDiagCorrelazione.getGdo();
				String porta = msgDiagCorrelazione.getNomePorta();
				boolean delegata = msgDiagCorrelazione.isDelegata();
				IDSoggetto idPorta = msgDiagCorrelazione.getIdSoggetto();
				IDSoggetto fruitore = null;
				IDServizio servizio = null;
				if(msgDiagCorrelazione.getInformazioniProtocollo()!=null){
					fruitore = msgDiagCorrelazione.getInformazioniProtocollo().getFruitore();
					servizio = new IDServizio();
					servizio.setAzione(msgDiagCorrelazione.getInformazioniProtocollo().getAzione());
					servizio.setServizio(msgDiagCorrelazione.getInformazioniProtocollo().getServizio());
					servizio.setSoggettoErogatore(msgDiagCorrelazione.getInformazioniProtocollo().getErogatore());
					servizio.setTipoServizio(msgDiagCorrelazione.getInformazioniProtocollo().getTipoServizio());
					servizio.setVersioneServizio(msgDiagCorrelazione.getInformazioniProtocollo().getVersioneServizio()+"");
				}
				String idCorrelazioneApplicativa = msgDiagCorrelazione.getCorrelazioneApplicativa();
				
				String idTransazione = msgDiagCorrelazione.getProperty(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
				
				TipiDatabase tipo = null;
				if(this.tipoDatabase!=null){
					if(!TipiDatabase.isAMember(this.tipoDatabase)){
						throw new MsgDiagnosticoException("Tipo database ["+this.tipoDatabase+"] non supportato");
					}
					tipo = TipiDatabase.toEnumConstant(this.tipoDatabase);
				}
				
				//	Connessione al DB
				cr = this.getConnection(conOpenSPCoopPdD,"logCorrelazione");
				con = cr.getConnection();
				
				String idTransazioneColumnName = "";
				String idTransazioneColumnValue = "";
				if(this.addIdTransazione){
					if(tipo==null){
						idTransazioneColumnName = " , id_transazione";
					}
					else{
						idTransazioneColumnName = "id_transazione";
					}
					idTransazioneColumnValue = " , ?";
				}
				
				if(tipo==null){
					
					// Inserimento della traccia nel DB in modalità retro compatibile
					// in questa versione non viene recuperato l'id long
				
					String updateString = "INSERT INTO "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE
					+" (idmessaggio, pdd_codice, pdd_tipo_soggetto, pdd_nome_soggetto, gdo, porta, delegata, tipo_fruitore, fruitore, tipo_erogatore, erogatore, tipo_servizio, servizio, versione_servizio, azione, id_correlazione_applicativa, protocollo"+idTransazioneColumnName+")"+
					" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ? , ? , ? , ?, ?"+idTransazioneColumnValue+")";
					stmt = con.prepareStatement(updateString);
					int index = 1;
					if(idBusta!=null)
						JDBCUtilities.setSQLStringValue(stmt,index++, idBusta);
					else
						throw new Exception("Identificativo messaggio non definito");
					if(idPorta!=null){
						if(idPorta.getCodicePorta()!=null)
							JDBCUtilities.setSQLStringValue(stmt,index++, idPorta.getCodicePorta());
						else
							throw new Exception("IdentificativoPorta.codice non definito");
						if(idPorta.getTipo()!=null)
							JDBCUtilities.setSQLStringValue(stmt,index++, idPorta.getTipo());
						else
							throw new Exception("IdentificativoPorta.codice non definito");
						if(idPorta.getNome()!=null)
							JDBCUtilities.setSQLStringValue(stmt,index++, idPorta.getNome());
						else
							throw new Exception("IdentificativoPorta.codice non definito");
					}else
						throw new Exception("IdentificativoPorta non definito");
					if(gdo!=null)
						stmt.setTimestamp(index++, new java.sql.Timestamp(gdo.getTime()));
					else
						throw new Exception("Data di registrazione non definita");
					JDBCUtilities.setSQLStringValue(stmt,index++, porta);
					if(delegata)
						stmt.setInt(index++, 1);
					else
						stmt.setInt(index++, 0);
					if(fruitore!=null){
						if(fruitore.getTipo()!=null)
							JDBCUtilities.setSQLStringValue(stmt,index++, fruitore.getTipo());
						else
							throw new Exception("Tipo fruitore non definito");
						if(fruitore.getNome()!=null)
							JDBCUtilities.setSQLStringValue(stmt,index++, fruitore.getNome());
						else
							throw new Exception("Nome fruitore non definito");
					}else
						throw new Exception("Fruitore non definito");
					if(servizio!=null){
						if(servizio.getSoggettoErogatore()!=null){
							if(servizio.getSoggettoErogatore().getTipo()!=null)
								JDBCUtilities.setSQLStringValue(stmt,index++, servizio.getSoggettoErogatore().getTipo());
							else
								throw new Exception("Tipo soggetto erogatore non definito");
							if(servizio.getSoggettoErogatore().getNome()!=null)
								JDBCUtilities.setSQLStringValue(stmt,index++, servizio.getSoggettoErogatore().getNome());
							else
								throw new Exception("Nome soggetto erogatore non definito");
						}else
							throw new Exception("Soggetto erogatore non definito");
						if(servizio.getTipoServizio()!=null)
							JDBCUtilities.setSQLStringValue(stmt,index++, servizio.getTipoServizio());
						else
							throw new Exception("Tipo servizio non definito");
						if(servizio.getServizio()!=null)
							JDBCUtilities.setSQLStringValue(stmt,index++, servizio.getServizio());
						else
							throw new Exception("Nome servizio non definito");
						if(servizio.getVersioneServizio()!=null)
							stmt.setInt(index++, Integer.parseInt(servizio.getVersioneServizio()));
						else
							throw new Exception("Versione servizio non definita");
						JDBCUtilities.setSQLStringValue(stmt,index++, servizio.getAzione());
					}else
						throw new Exception("IDServizio non definito");
					JDBCUtilities.setSQLStringValue(stmt,index++, idCorrelazioneApplicativa);
					JDBCUtilities.setSQLStringValue(stmt,index++, msgDiagCorrelazione.getProtocollo());
					if(this.addIdTransazione){
						JDBCUtilities.setSQLStringValue(stmt,index++, idTransazione);
					}
					stmt.executeUpdate();
					stmt.close();
				
				}
				else{
					
					List<InsertAndGeneratedKeyObject> listInsertAndGeneratedKeyObject = new ArrayList<InsertAndGeneratedKeyObject>();
					
					if(idBusta!=null)
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("idmessaggio", getSQLStringValue(idBusta), InsertAndGeneratedKeyJDBCType.STRING) );
					else
						throw new Exception("Identificativo messaggio non definito");
					if(idPorta!=null){
						if(idPorta.getCodicePorta()!=null)
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("pdd_codice", getSQLStringValue(idPorta.getCodicePorta()), InsertAndGeneratedKeyJDBCType.STRING) );
						else
							throw new Exception("IdentificativoPorta.codice non definito");
						if(idPorta.getTipo()!=null)
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("pdd_tipo_soggetto", getSQLStringValue(idPorta.getTipo()), InsertAndGeneratedKeyJDBCType.STRING) );
						else
							throw new Exception("IdentificativoPorta.codice non definito");
						if(idPorta.getNome()!=null)
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("pdd_nome_soggetto", getSQLStringValue(idPorta.getNome()), InsertAndGeneratedKeyJDBCType.STRING) );
						else
							throw new Exception("IdentificativoPorta.codice non definito");
					}else
						throw new Exception("IdentificativoPorta non definito");
					if(gdo!=null)
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("gdo", new java.sql.Timestamp(gdo.getTime()) , InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
					else
						throw new Exception("Data di registrazione non definita");
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("porta", getSQLStringValue(porta), InsertAndGeneratedKeyJDBCType.STRING) );
					if(delegata)
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("delegata", 1, InsertAndGeneratedKeyJDBCType.INT) );
					else
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("delegata", 0, InsertAndGeneratedKeyJDBCType.INT) );
					if(fruitore!=null){
						if(fruitore.getTipo()!=null)
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_fruitore", getSQLStringValue(fruitore.getTipo()), InsertAndGeneratedKeyJDBCType.STRING) );
						else
							throw new Exception("Tipo fruitore non definito");
						if(fruitore.getNome()!=null)
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("fruitore", getSQLStringValue(fruitore.getNome()), InsertAndGeneratedKeyJDBCType.STRING) );
						else
							throw new Exception("Nome fruitore non definito");
					}else
						throw new Exception("Fruitore non definito");
					if(servizio!=null){
						if(servizio.getSoggettoErogatore()!=null){
							if(servizio.getSoggettoErogatore().getTipo()!=null)
								listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_erogatore", getSQLStringValue(servizio.getSoggettoErogatore().getTipo()), InsertAndGeneratedKeyJDBCType.STRING) );
							else
								throw new Exception("Tipo soggetto erogatore non definito");
							if(servizio.getSoggettoErogatore().getNome()!=null)
								listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("erogatore", getSQLStringValue(servizio.getSoggettoErogatore().getNome()), InsertAndGeneratedKeyJDBCType.STRING) );
							else
								throw new Exception("Nome soggetto erogatore non definito");
						}else
							throw new Exception("Soggetto erogatore non definito");
						if(servizio.getTipoServizio()!=null)
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("tipo_servizio", getSQLStringValue(servizio.getTipoServizio()), InsertAndGeneratedKeyJDBCType.STRING) );
						else
							throw new Exception("Tipo servizio non definito");
						if(servizio.getServizio()!=null)
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("servizio", getSQLStringValue(servizio.getServizio()), InsertAndGeneratedKeyJDBCType.STRING) );
						else
							throw new Exception("Nome servizio non definito");
						if(servizio.getVersioneServizio()!=null)
							listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("versione_servizio", Integer.parseInt(servizio.getVersioneServizio()), InsertAndGeneratedKeyJDBCType.INT) );
						else
							throw new Exception("Versione servizio non definita");
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("azione", getSQLStringValue(servizio.getAzione()), InsertAndGeneratedKeyJDBCType.STRING) );
					}else
						throw new Exception("IDServizio non definito");
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("id_correlazione_applicativa", getSQLStringValue(idCorrelazioneApplicativa), InsertAndGeneratedKeyJDBCType.STRING) );
					listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject("protocollo", getSQLStringValue(msgDiagCorrelazione.getProtocollo()), InsertAndGeneratedKeyJDBCType.STRING) );
					if(this.addIdTransazione){
						listInsertAndGeneratedKeyObject.add( new InsertAndGeneratedKeyObject(idTransazioneColumnName, getSQLStringValue(idTransazione), InsertAndGeneratedKeyJDBCType.STRING) );
					}
					
					// ** Insert and return generated key
					long iddiagnostico = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, tipo, 
							new CustomKeyGeneratorObject(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE, CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_ID, 
									CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SEQUENCE, CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_TABLE_FOR_ID),
							listInsertAndGeneratedKeyObject.toArray(new InsertAndGeneratedKeyObject[1]));
					if(iddiagnostico<=0){
						throw new Exception("ID autoincrementale non ottenuto");
					}
					msgDiagCorrelazione.setId(iddiagnostico);
					
				}
				
			}catch(Exception e){
				throw new MsgDiagnosticoException("Errore durante la registrazione del msg diagnostico di correlazione: "+e.getMessage(),e);
			}finally{
				try{
					if(stmt!=null)
						stmt.close();
				}catch(Exception e){}
				try{
					this.releaseConnection(cr, "logCorrelazione");
				}catch(Exception e){}
			}
		}
	}
	
	
	/**
	 * Creazione di una correlazione applicativa tra messaggi diagnostici e servizi applicativi.
	 * 
	 * @param msgDiagCorrelazioneSA Informazioni necessarie alla registrazione del servizio applicativo
	 */
	@Override
	public void logCorrelazioneServizioApplicativo(Connection conOpenSPCoopPdD,MsgDiagnosticoCorrelazioneServizioApplicativo msgDiagCorrelazioneSA)throws MsgDiagnosticoException{
		
		if(MsgDiagnosticoOpenSPCoopAppenderDB.writeCorrelazione){
			PreparedStatement stmt = null;
			Connection con = null;
			ResultSet rs = null;
			MsgDiagnosticoConnectionResult cr = null;
			try{
				// Connessione al DB
				cr = this.getConnection(conOpenSPCoopPdD,"logCorrelazioneServizioApplicativo");
				con = cr.getConnection();
	
				// Prendo id di correlazione
				StringBuffer selectString = new StringBuffer("SELECT ");
				if(this.forceIndex){
					selectString.append(" /*+ index("+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+" "+CostantiDB.MSG_CORR_INDEX+") */ ");
				}
				selectString.append(" id,gdo FROM "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+" WHERE idmessaggio=? AND delegata=?");
				stmt = con.prepareStatement(selectString.toString());
				String id = msgDiagCorrelazioneSA.getIdBusta();
				if(id!=null)
					JDBCUtilities.setSQLStringValue(stmt,1, id);
				else
					throw new Exception("Identificativo Messaggio non definito");
				if(msgDiagCorrelazioneSA.isDelegata())
					stmt.setInt(2, 1);
				else
					stmt.setInt(2, 0);
				long idCorrelazione = -1;
				Timestamp gdoT = null;
				int tentativi = 0;
				while(tentativi<10){
					
					// Succedeva un errore a causa della lentezza del db, per cui non era subito immediatamente disponibile la correlazione
					
					rs = stmt.executeQuery();
					if(rs.next()){
						idCorrelazione = rs.getLong("id");
						if(idCorrelazione==-1)
							throw new Exception("ID Correlazione non esistente");
						gdoT = rs.getTimestamp("gdo");
					}
					rs.close();
					
					if(idCorrelazione>0){
						break;
					}
					
					try{
						Thread.sleep(50);
					}catch(Exception e){}
					
					tentativi++;
					
				}
				stmt.close();
				if(idCorrelazione<=0){
					throw new Exception("Non esiste una correlazione con l'id["+id+"] e delegata["+msgDiagCorrelazioneSA.isDelegata()+"]");
				}
				
				String gdoColumnName = "";
				String gdoColumnValue = "";
				if(this.addGdoForAllTables){
					gdoColumnName = " , gdo";
					gdoColumnValue = " , ?";
				}
				
				// Controllo che non esista già.
				// Può succedere (succede in ORACLE)
				selectString = new StringBuffer("SELECT ");
				selectString.append(" * FROM "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA+" WHERE id_correlazione=? AND servizio_applicativo=?");
				stmt = con.prepareStatement(selectString.toString());
				int index = 1;
				stmt.setLong(index++, idCorrelazione);
				JDBCUtilities.setSQLStringValue(stmt,index++, msgDiagCorrelazioneSA.getServizioApplicativo());
				rs = stmt.executeQuery();
				boolean existsAlready = rs.next();
				rs.close();
				stmt.close();
								
				if(existsAlready==false){
							
					String updateString = "INSERT INTO "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA
					+" (id_correlazione, servizio_applicativo"+gdoColumnName+")"+
					" VALUES (?, ?"+gdoColumnValue+")";
					stmt = con.prepareStatement(updateString);
					index = 1;
					stmt.setLong(index++, idCorrelazione);
					JDBCUtilities.setSQLStringValue(stmt,index++, msgDiagCorrelazioneSA.getServizioApplicativo());
					if(this.addGdoForAllTables){
						stmt.setTimestamp(index++, gdoT);
					}
					stmt.executeUpdate();
					stmt.close();
					
				}
				
				
			}catch(Exception e){
				throw new MsgDiagnosticoException("Errore durante la registrazione del msg diagnostico di correlazione: "+e.getMessage(),e);
			}finally{
				try{
					if(rs!=null)
						rs.close();
				}catch(Exception e){}
				try{
					if(stmt!=null)
						stmt.close();
				}catch(Exception e){}
				try{
					this.releaseConnection(cr, "logCorrelazioneServizioApplicativo");
				}catch(Exception e){}
			}
		}
	}
	
	
	/**
	 * Registrazione dell'identificativo di correlazione applicativa della risposta
	 * 
	 * @param msgDiagCorrelazioneApplicativa Informazioni necessarie alla registrazione della correlazione
	 * @throws MsgDiagnosticoException
	 */
	@Override
	public void logCorrelazioneApplicativaRisposta(Connection conOpenSPCoopPdD,MsgDiagnosticoCorrelazioneApplicativa msgDiagCorrelazioneApplicativa) throws MsgDiagnosticoException{
		if(MsgDiagnosticoOpenSPCoopAppenderDB.writeCorrelazione){
			PreparedStatement stmt = null;
			Connection con = null;
			ResultSet rs = null;
			MsgDiagnosticoConnectionResult cr = null;
			try{
				// Connessione al DB
				cr = this.getConnection(conOpenSPCoopPdD,"logCorrelazioneApplicativaRisposta");
				con = cr.getConnection();
	
				// Prendo id di correlazione
				StringBuffer selectString = new StringBuffer("SELECT ");
				if(this.forceIndex){
					selectString.append(" /*+ index("+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+" "+CostantiDB.MSG_CORR_INDEX+") */ ");
				}
				selectString.append(" id FROM "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+" WHERE idmessaggio=? AND delegata=?");
				stmt = con.prepareStatement(selectString.toString());
				String idBusta = msgDiagCorrelazioneApplicativa.getIdBusta();
				if(idBusta!=null)
					JDBCUtilities.setSQLStringValue(stmt,1, idBusta);
				else
					throw new Exception("Identificativo Messaggio non definito");
				if(msgDiagCorrelazioneApplicativa.isDelegata())
					stmt.setInt(2, 1);
				else
					stmt.setInt(2, 0);
				long idCorrelazione = -1;
				rs = stmt.executeQuery();
				if(rs.next()){
					idCorrelazione = rs.getLong("id");
					if(idCorrelazione==-1)
						throw new Exception("ID Correlazione non esistente");
				}else{
					throw new Exception("Non esiste una correlazione con l'id["+idBusta+"] e delegata["+msgDiagCorrelazioneApplicativa.isDelegata()+"]");
				}
				rs.close();
				stmt.close();
				
				String updateString = "UPDATE "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE
				+" set id_correlazione_risposta=? WHERE id=?";
				stmt = con.prepareStatement(updateString);
				JDBCUtilities.setSQLStringValue(stmt,1, msgDiagCorrelazioneApplicativa.getCorrelazione());
				stmt.setLong(2, idCorrelazione);
				stmt.executeUpdate();
				stmt.close();
								
			}catch(Exception e){
				throw new MsgDiagnosticoException("Errore durante la registrazione del msg diagnostico di correlazione (id applicativo della risposta): "+e.getMessage(),e);
			}finally{
				try{
					if(rs!=null)
						rs.close();
				}catch(Exception e){}
				try{
					if(stmt!=null)
						stmt.close();
				}catch(Exception e){}
				try{
					this.releaseConnection(cr, "logCorrelazioneApplicativaRisposta");
				}catch(Exception e){}
			}
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
		MsgDiagnosticoConnectionResult cr = null;
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
