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




package org.openspcoop2.pdd.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;


/**
 * Classe utilizzata per rappresentare un messaggio Soap nel contesto della libreria.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public class SoapMessage implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** Logger utilizzato per debug. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Identificativo del Messaggio */
	private String idMessaggio;
	/** Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX */
	private String box;
	/** Indicazione se il messaggio deve essere registrato su file System o su DB */
	private boolean saveOnFS;
	/** Identificativo del Messaggio passato sotto una funziona HASH */
	private String keyMsg;
	/** Indica la directory dove effettuare salvataggi */
	private String workDir;
	/** AdapterJDBC */
	private IJDBCAdapter adapter;


	private IOpenSPCoopState openspcoopstate;

	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();




	/* ********  C O S T R U T T O R E  ******** */
	/**
	 * Costruttore. 
	 *
	 * @param idMsg ID del Messaggio
	 * @param openspcoopstate state Oggetto che rappresenta lo stato di una busta
	 * @param box Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX
	 * @param workDir Directory dove effettuare salvataggi se il messaggio deve essere registrato su FileSystem, null se il messaggio deve essere registrato su DB
	 * 
	 */
	public SoapMessage(String idMsg, IOpenSPCoopState openspcoopstate, String box, String workDir,Logger alog) throws UtilsException{
		this(idMsg, openspcoopstate ,box,workDir,null,alog);
	}

	/**
	 * Costruttore. 
	 *
	 * @param idMsg ID del Messaggio
	 * @param openspcoopstate state Oggetto che rappresenta lo stato di una busta
	 * @param box Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX
	 * @param adapterJDBC  JDBCAdapter se il messaggio deve essere registrato su DB, null se il messaggio deve essere registrato su file System 
	 * 
	 */
	public SoapMessage(String idMsg, IOpenSPCoopState openspcoopstate, String box, IJDBCAdapter adapterJDBC,Logger alog) throws UtilsException{
		this(idMsg, openspcoopstate ,box,null,adapterJDBC,alog);
	}


	/**
	 * Costruttore. 
	 *
	 * @param idMsg ID del Messaggio
	 * @param openspcoopstate state Oggetto che rappresenta lo stato di una busta
	 * @param box Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX
	 * @param workDir Directory dove effettuare salvataggi se il messaggio deve essere registrato su FileSystem, null se il messaggio deve essere registrato su DB
	 * @param adapterJDBC  JDBCAdapter se il messaggio deve essere registrato su DB, null se il messaggio deve essere registrato su file System 
	 * 
	 */
	public SoapMessage(String idMsg, IOpenSPCoopState openspcoopstate, String box, String workDir, 
			IJDBCAdapter adapterJDBC,Logger alog) throws UtilsException{
		this.idMessaggio = idMsg;
		this.box = box;
		this.openspcoopstate = openspcoopstate;
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(SoapMessage.class);
		}
		try{
			this.keyMsg = this.hash(idMsg);
			if(this.keyMsg == null){
				throw new Exception("Codifica hash non riuscita: keyMsg is null");
			}
		}catch(Exception e){
			String errorMsg = "SOAP_MESSAGE, costructor error (CodificaHash): "+box+"/"+idMsg+": "+e.getMessage();		
			this.log.error(errorMsg);
			throw new UtilsException(errorMsg,e);
		}

		if(adapterJDBC==null){
			this.saveOnFS = true;
			this.workDir = workDir;
		}else{
			this.saveOnFS = false;
			this.adapter = adapterJDBC;
		}
	}

	/**
	 * Ritorna un intero che rappresenta la chiave di una stringa.
	 *
	 * @param key Stringa su cui effettuare la traduzione.
	 * @return hash della stringa.
	 * 
	 */
	private String hash(String key) throws UtilsException{
		try{
			StringBuffer returnKey = new StringBuffer();
			for(int i=0; i<key.length();i++){
				if( (key.charAt(i) != '_') && (key.charAt(i) != '-') &&
						(key.charAt(i) != '.') && (key.charAt(i) != ':') )
					returnKey.append(key.charAt(i));
			}

			//log.info("Costruito ["+returnKey.toString()+"]");

			return returnKey.toString();

		} catch (java.lang.Exception e) {
			throw new UtilsException("Utilities.hash error "+e.getMessage(),e);
		}
	}








	/* ********  U T I L I T Y  ******** */

	/**
	 * Ritorna la directory base su cui effettuare salvataggi 
	 *
	 * @return Ritorna la directory base su cui effettuare salvataggi 
	 * 
	 */
	public String getBaseDir() throws UtilsException{

		// Controllo esistenza directory fornita per salvare i messaggi
		File dir = new File(this.workDir);
		if(dir.exists() == false){
			String errorMsg = "SOAP_MESSAGE, getBaseDir: "+this.box+"/"+this.idMessaggio+": directory di lavoro inesistente ("+this.workDir+").";		
			this.log.error(errorMsg);
			throw new UtilsException(errorMsg);
		}
		String baseDir = this.workDir;
		if (!baseDir.endsWith(File.separator))
			baseDir = baseDir + File.separator;

		// Seleziono INBOX/OUTBOX
		if(Costanti.INBOX.equals(this.box)){
			baseDir = baseDir + Costanti.INBOX;
		}else if (Costanti.OUTBOX.equals(this.box)){
			baseDir = baseDir + Costanti.OUTBOX;
		}else{
			String errorMsg = "SOAP_MESSAGE, getBaseDir: "+this.box+"/"+this.idMessaggio+": box non valido? .";		
			this.log.error(errorMsg);
			throw new UtilsException(errorMsg);
		}

		// Controllo esistenza di INBUX/OUTBOX
		File dirINOUT = new File(baseDir);
		if(dirINOUT.exists() == false){
			if(dirINOUT.mkdir()==false){
				String errorMsg = "SOAP_MESSAGE, getBaseDir: "+this.box+"/"+this.idMessaggio+": directory di lavoro ("+this.workDir+") non permette la creazione di sottodirectory INBOX/OUTBOX.";		
				this.log.error(errorMsg);
				throw new UtilsException(errorMsg);
			}
		}

		return (baseDir+ File.separator);

	}

	/**
	 * Ritorna il codice del Messaggio
	 *
	 * @return Codice.
	 * 
	 */
	public String getIdMessaggio(){
		return this.idMessaggio;
	}

	/**
	 *Aggiorna lo stato
	 */
	public void updateState(IOpenSPCoopState openspcoopstate){
		this.openspcoopstate = openspcoopstate;
	}


	/* ********  S A V E  ******** */
	
	/**
	 * Dato un messaggio come parametro, si occupa di salvarlo nel filesystem/DB. 
	 * Il SoapEnvelope viene salvato nel FileSystem associandoci l'informazione strutturale <var>idMessaggio</var>.
	 *
	 *
	 * @param msg Messaggio.
	 * 
	 */
	public void save(OpenSPCoop2Message msg, boolean isRichiesta, boolean portaDiTipoStateless) throws UtilsException{
		
//		//NOTA: Faccio la save per refreshare il ContentType. Necessario nel caso di attachment
//		try{
//	        if( msg.saveRequired() ) {
//	        	msg.saveChanges();
//	        }
//		}catch(Exception e){
//			String errorMsg = "SOAP_MESSAGE, save: "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
//			this.log.error(errorMsg);
//			throw new UtilsException(errorMsg,e);
//		}
        
		// Find SoapAction
		String soapAction = null;
		try{
			soapAction = SoapUtils.getSoapAction(msg);
		}catch(Exception e){
			String errorMsg = "SOAP_MESSAGE, save (soapAction): "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
			this.log.error(errorMsg);
			throw new UtilsException(errorMsg,e);
		}
			
		if( !portaDiTipoStateless ) {
			StateMessage stateMsg = (isRichiesta) ?  
					(StateMessage)this.openspcoopstate.getStatoRichiesta() :
						(StateMessage)this.openspcoopstate.getStatoRisposta();
					Connection connectionDB = stateMsg.getConnectionDB();

					PreparedStatement pstmt = null;
					try{
						// Save proprieta' msg
						StringBuffer query = new StringBuffer();
						query.append("INSERT INTO  ");
						query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
						if(this.saveOnFS)
							query.append(" (ID_MESSAGGIO,TIPO,SOAP_ACTION,CONTENT_TYPE,CONTENT_LOCATION) VALUES ( ? , ? , ? , ? , ?)");
						else
							query.append(" (ID_MESSAGGIO,TIPO,SOAP_ACTION,CONTENT_TYPE,CONTENT_LOCATION,MSG_BYTES) VALUES ( ? , ? , ? , ? , ? , ?)");
						
						pstmt = connectionDB.prepareStatement(query.toString());
						pstmt.setString(1,this.idMessaggio);
						if(Costanti.INBOX.equals(this.box))
							pstmt.setString(2,Costanti.INBOX);
						else
							pstmt.setString(2,Costanti.OUTBOX);		
						pstmt.setString(3,soapAction);
						
						//Sposto il set del contentType dopo la writeTo del messaggio 
						//cosi nel caso di attachment lo trovo corretto.
						
						
						pstmt.setString(5,msg.getSOAPPart().getContentLocation());

						// Save bytes
						if(this.saveOnFS){
							// READ FROM FILE SYSTEM
							String saveDir = getBaseDir();
							if(saveDir==null){
								String errorMsg = "WorkDir non correttamente inizializzata";		
								throw new UtilsException(errorMsg);
							}
							String path = saveDir + this.keyMsg;
							SoapUtils.saveMessage(path,msg);
						}else{
							// Scrittura su DB
							java.io.ByteArrayOutputStream msgByte = new java.io.ByteArrayOutputStream();
							msg.writeTo(msgByte,true);
							msgByte.flush();
							msgByte.close();
							//System.out.println("---------SALVO RISPOSTA: "+msgByte.toString());
							this.adapter.setBinaryData(pstmt,6,msgByte.toByteArray());
						}
						
						// Set del contentType nella query
						pstmt.setString(4,msg.getContentType());

						// Add PreparedStatement
						stateMsg.getPreparedStatement().put("INSERT (MSG_OP_STEP1a) saveMessage["+this.keyMsg+","+this.box+"]",pstmt);
					
					}catch(Exception e){
						try{
							if( pstmt != null )
								pstmt.close();
						} catch(Exception err) {}
						String errorMsg = "SOAP_MESSAGE, save : "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
						this.log.error(errorMsg,e);
						throw new UtilsException(errorMsg,e);
					}
		}else if (portaDiTipoStateless){
			
			try{
				if(soapAction!=null)
					msg.setProperty(org.openspcoop2.message.Costanti.SOAP_ACTION,soapAction);
			}catch(Exception e){
				String errorMsg = "SOAP_MESSAGE, set (soapAction): "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
				this.log.error(errorMsg);
				throw new UtilsException(errorMsg,e);
			}
			
			if (isRichiesta) ((OpenSPCoopStateless)this.openspcoopstate).setRichiestaMsg(msg);
			else ((OpenSPCoopStateless)this.openspcoopstate).setRispostaMsg(msg);
			
			
		}else{
			throw new UtilsException("Metodo invocato con OpenSPCoopState non valido");
		}

	}     




	/* ********  R E A D  ******** */

	/**
	 * Ritorna un messaggio che era stata precedentemente salvata nel filesystem/DB. 
	 *
	 * @return il messaggio precedentemente salvato
	 * 
	 */
	public OpenSPCoop2Message read(boolean isRichiesta, boolean portaDiTipoStateless) throws UtilsException {

		if( !portaDiTipoStateless ) {

			@SuppressWarnings("resource")
			Connection connectionDB = (isRichiesta) ? 
					((StateMessage)this.openspcoopstate.getStatoRichiesta()).getConnectionDB() :
						((StateMessage)this.openspcoopstate.getStatoRisposta()).getConnectionDB();

					OpenSPCoop2Message msg = null;
					PreparedStatement pstmt = null;
					InputStream is = null;
					ResultSet rs = null;
					try{

						// Leggo proprieta' messaggio
						StringBuffer query = new StringBuffer();
						if(this.saveOnFS)
							query.append("select SOAP_ACTION,CONTENT_TYPE,CONTENT_LOCATION ");
						else
							query.append("select SOAP_ACTION,CONTENT_TYPE,CONTENT_LOCATION,MSG_BYTES ");
						query.append("from ");
						query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
						query.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
						pstmt =  connectionDB.prepareStatement(query.toString());
						pstmt.setString(1,this.idMessaggio);
						if(Costanti.INBOX.equals(this.box))
							pstmt.setString(2,Costanti.INBOX);
						else
							pstmt.setString(2,Costanti.OUTBOX);
						rs = pstmt.executeQuery();
						if(rs==null){
							String errorMsg = "ResultSet is null?";		
							throw new UtilsException(errorMsg);
						}
						if(rs.next()==false){
							String errorMsg = "Messaggio non esistente";		
							throw new UtilsException(errorMsg);
						}

						// ContentType e ContentLocation
						String contentType = rs.getString("CONTENT_TYPE");
						String contentLocation = rs.getString("CONTENT_LOCATION");

						// InputStream
						if(this.saveOnFS){
							// READ FROM FILE SYSTEM
							String saveDir = getBaseDir();
							if(saveDir==null){
								String errorMsg = "WorkDir non correttamente inizializzata";		
								throw new UtilsException(errorMsg);
							}
							String path = saveDir + this.keyMsg;

							File fileCheck = new File(path);
							if(fileCheck.exists() == false){
								String errorMsg = "Il messaggio non risulta gia' registrato ("+path+").";		
								throw new UtilsException(errorMsg);
							}	   
							is = new FileInputStream(path);
						}else{
							// Lettura da DB]
							is = new java.io.ByteArrayInputStream(this.adapter.getBinaryData(rs,4));
						}

						// CostruzioneMessaggio
						
						OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
						NotifierInputStreamParams notifierInputStreamParams = null; // Non dovrebbe servire, un eventuale handler attaccato, dovrebbe gia aver ricevuto tutto il contenuto una volta serializzato il messaggio su database.
						OpenSPCoop2MessageParseResult pr = mf.createMessage(is,notifierInputStreamParams,false,contentType,contentLocation, this.openspcoopProperties.isFileCacheEnable(), this.openspcoopProperties.getAttachmentRepoDir(), this.openspcoopProperties.getFileThreshold());
						msg = pr.getMessage_throwParseException();
								
						// SoapAction
						try{
							String soapAction = rs.getString("SOAP_ACTION");
							if(soapAction!=null)
								msg.setProperty(org.openspcoop2.message.Costanti.SOAP_ACTION,soapAction);
						}catch(Exception e){
							throw new UtilsException(e.getMessage(),e);
						}

						// chiusura risorse DB
						rs.close();
						pstmt.close();

					}catch(Exception e){
						try{
							if( rs != null )
								rs.close();
						} catch(Exception er) {}
						try{
							if( pstmt != null )
								pstmt.close();
						} catch(Exception er) {}
						try{
							if( is != null )
								is.close();
						} catch(Exception er) {}
						String errorMsg = "SOAP_MESSAGE, read: "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
						this.log.error(errorMsg,e);
						throw new UtilsException(errorMsg,e);
					}

					return msg;
		}else if ( portaDiTipoStateless ){
			if (isRichiesta) return	((OpenSPCoopStateless)this.openspcoopstate).getRichiestaMsg();
			else return	((OpenSPCoopStateless)this.openspcoopstate).getRispostaMsg();
		}else{
			throw new UtilsException("Metodo invocato con IState non valido");

		}
	}










	/* ********  D E L E T E  ******** */

	/**
	 * Elimina un messaggio completamente, sia dal filesystem che dal db 
	 * 
	 * 
	 */
	public void delete(boolean isRichiesta,boolean onewayVersione11) throws UtilsException{
		if((this.openspcoopstate instanceof OpenSPCoopStateful) || onewayVersione11) {
			StateMessage stateMSG = (isRichiesta) ?  (StateMessage)this.openspcoopstate.getStatoRichiesta() 
					: (StateMessage)this.openspcoopstate.getStatoRisposta();
			Connection connectionDB = stateMSG.getConnectionDB();

			PreparedStatement pstmt = null;
			try{
				// Eliminazione da FileSystem
				if(this.saveOnFS){
					String saveDir = getBaseDir();
					if(saveDir==null){
						String errorMsg = "WorkDir non correttamente inizializzata";		
						throw new UtilsException(errorMsg);
					}
					String path = saveDir + this.keyMsg;
					File fileDelete = new File(path);
					if(fileDelete.exists()){
						fileDelete.delete();
					}	
				}

				//	Eliminazione from DB.
				StringBuffer query = new StringBuffer();
				query.append("DELETE from ");
				query.append(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
				query.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");	    
				pstmt= connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,this.idMessaggio);
				if(Costanti.INBOX.equals(this.box))
					pstmt.setString(2,Costanti.INBOX);
				else
					pstmt.setString(2,Costanti.OUTBOX);
				pstmt.execute();
				pstmt.close();

			}catch(Exception e){
				try{
					if( pstmt != null )
						pstmt.close();
				} catch(Exception er) {}
				String errorMsg = "SOAP_MESSAGE, delete: "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
				this.log.error(errorMsg,e);
				throw new UtilsException(errorMsg,e);
			}
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			// NOP
		}else{
			throw new UtilsException("Metodo invocato con IState non valido");
		}
	}

	/**
	 * Elimina un messaggio completamente, sia dal filesystem che dal db 
	 * 
	 * 
	 */
	public void deleteMessageFromFileSystem() {


		try{
			// Eliminazione da FileSystem
			if(this.saveOnFS){
				String saveDir = getBaseDir();
				if(saveDir==null){
					String errorMsg = "WorkDir non correttamente inizializzata";		
					throw new UtilsException(errorMsg);
				}
				String path = saveDir + this.keyMsg;
				File fileDelete = new File(path);
				if(fileDelete.exists()){
					fileDelete.delete();
				}	
			}

		}catch(Exception e){
			String errorMsg = "SOAP_MESSAGE, deleteMessageFromFileSystem: "+this.box+"/"+this.idMessaggio+": "+e.getMessage();		
			this.log.error(errorMsg,e);
		}
	}

}





