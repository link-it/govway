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




package org.openspcoop2.pdd.core.connettori;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;



/**
 * Classe utilizzata per effettuare consegne di messaggi su file system
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreFILE extends ConnettoreBaseWithResponse {

	
	/* ********  F I E L D S  P R I V A T I  ******** */

	public ByteArrayOutputStream outByte = new ByteArrayOutputStream();

	/** File */
	private File outputFile = null;
	private File outputFileHeaders = null;
	private boolean outputFileAutoCreateParentDirectory = false;
	private boolean outputFileOverwriteIfExists = false;
	private boolean generateResponse = false;
	private File inputFile = null;
	private File inputFileHeaders = null;
	private boolean inputFileDeleteAfterRead = false;
	private Integer inputFileWaitTimeIfNotExists;
	
	private ByteArrayOutputStream boutFileOutputHeaders;
	
	
	/* Costruttori */
	public ConnettoreFILE(){
		
	}

	
	
	/* ********  METODI  ******** */

	public String buildLocation(ConnettoreMsg request) throws ConnettoreException{
		
		this.properties = request.getConnectorProperties();
		
		// Costruisco Mappa per dynamic name
		// NOTA: Lasciare la costruzione qua, poiche' altrimenti la classe File lancia eccezioni se trova i threshold
		Map<String, Object> dynamicMap = this.buildDynamicMap(request);
		
		// OutputFile
		String tmp = this.getDynamicProperty(request.getTipoConnettore(), true, CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE, dynamicMap);
		File f = new File(tmp);
		String l = f.getAbsolutePath();
		
		// OutputFile-Headers
		tmp = this.getDynamicProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS, dynamicMap);
		if(tmp!=null){
			l = l + " [headers: "+(new File(tmp)).getAbsolutePath()+"]";
		}
		
		return l;
	}
	

	@Override
	protected boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request) {
		
		if(this.initialize(request, true, responseCachingConfig)==false){
			return false;
		}
		
		return true;
		
	}
	
	@Override
	protected boolean send(ConnettoreMsg request) {

		// Lettura Parametri
		try{
	
			this.checkContentType = true;
			if(this.idModulo!=null){
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
					this.checkContentType = this.openspcoopProperties.isControlloContentTypeAbilitatoRicezioneBuste();
				}else{
					this.checkContentType = this.openspcoopProperties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi();
				}
			}
			
			
			// Costruisco Mappa per dynamic name
			Map<String, Object> dynamicMap = new Hashtable<String, Object>();
			DynamicInfo dInfo = new DynamicInfo(request, this.getPddContext());
			DynamicUtils.fillDynamicMap(this.logger.getLogger(),dynamicMap, dInfo);
			
			
			// Identificativo modulo
			this.idModulo = request.getIdModulo();
					
			// Context per invocazioni handler
			this.outRequestContext = request.getOutRequestContext();
			this.msgDiagnostico = request.getMsgDiagnostico();
			
			
			// analsi i parametri specifici per il connettore
			
			// OutputFile
			String tmp = this.getDynamicProperty(request.getTipoConnettore(), true, CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE, dynamicMap);
			if(tmp==null){
				return false;
			}
			this.outputFile = new File(tmp);
			
			// OutputFileHeader
			tmp = this.getDynamicProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS, dynamicMap);
			if(tmp!=null){
				this.outputFileHeaders = new File(tmp);
			}

			// Location
			this.location = this.outputFile.getAbsolutePath();
			if(this.outputFileHeaders!=null){
				this.location = this.location + " [headers: "+this.outputFileHeaders.getAbsolutePath()+"]";
			}
			
			// AutoCreateDir
			this.outputFileAutoCreateParentDirectory = this.isBooleanProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			
			// OverwriteFileIfExists
			this.outputFileOverwriteIfExists = this.isBooleanProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE);
			
			// GenerazioneRisposta
			this.generateResponse = this.isBooleanProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			
			if(this.generateResponse){
				
				// InputFile
				tmp = this.getDynamicProperty(request.getTipoConnettore(), true, CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE, dynamicMap);
				if(tmp==null){
					return false;
				}
				this.inputFile = new File(tmp);
				
				// InputFileHeader
				tmp = this.getDynamicProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS, dynamicMap);
				if(tmp!=null){
					this.inputFileHeaders = new File(tmp);
				}
				
				// DeleteAfterRead
				this.inputFileDeleteAfterRead = this.isBooleanProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ);
				
				// inputFileWaitTimeIfNotExists
				this.inputFileWaitTimeIfNotExists = this.getIntegerProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
			}
			
		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			this.errore = "Errore avvenuto durante la consegna su FileSystem: "+this.readExceptionMessageFromException(e);
			this.logger.error("Errore avvenuto durante la consegna su FileSystem: "+this.readExceptionMessageFromException(e),e);
			return false;
		} 
			
			
			
		// Gestione File
		try{

			/* ------------  Request ------------- */
			
			
			// OutputFile
			this.checkOutputFile(this.outputFile, "Request");
			
			// OutputFile Headers
			if(this.outputFileHeaders!=null){
				this.checkOutputFile(this.outputFileHeaders, "Request-Headers");
			}
			
			
			// Tipologia di servizio
			OpenSPCoop2SoapMessage soapMessageRequest = null;
			if(this.debug)
				this.logger.debug("Tipologia Servizio: "+this.requestMsg.getServiceBinding());
			if(this.isSoap){
				soapMessageRequest = this.requestMsg.castAsSoap();
			}
			
			
			
			// Alcune implementazioni richiedono di aggiornare il Content-Type
			this.requestMsg.updateContentType();
			
			
			// Properties per serializzazione header file
			this.boutFileOutputHeaders = new ByteArrayOutputStream();
			
			
			// Collezione header di trasporto per dump
			Map<String, String> propertiesTrasportoDebug = null;
			if(this.isDumpBinario()) {
				propertiesTrasportoDebug = new HashMap<String, String>();
			}
			
			
			// Impostazione Content-Type
			String contentTypeRichiesta = null;
			if(this.debug)
				this.logger.debug("Impostazione content type...");
			if(this.isSoap){
				if(this.sbustamentoSoap && soapMessageRequest.countAttachments()>0 && TunnelSoapUtils.isTunnelOpenSPCoopSoap(soapMessageRequest)){
					contentTypeRichiesta = TunnelSoapUtils.getContentTypeTunnelOpenSPCoopSoap(soapMessageRequest.getSOAPBody());
				}else{
					contentTypeRichiesta = this.requestMsg.getContentType();
				}
				if(contentTypeRichiesta==null){
					throw new Exception("Content-Type del messaggio da spedire non definito");
				}
			}
			else{
				contentTypeRichiesta = this.requestMsg.getContentType();
				// Content-Type non obbligatorio in REST
			}
			if(this.debug)
				this.logger.info("Impostazione Content-Type ["+contentTypeRichiesta+"]",false);
			if(contentTypeRichiesta!=null){
				setRequestHeader(HttpConstants.CONTENT_TYPE, contentTypeRichiesta, this.logger, propertiesTrasportoDebug);
			}
						

			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			if(this.isSoap && this.sbustamentoSoap == false){
				if(this.debug)
					this.logger.debug("Impostazione soap action...");
				this.soapAction = soapMessageRequest.getSoapAction();
				if(this.soapAction==null){
					this.soapAction="\"OpenSPCoop\"";
				}
				if(MessageType.SOAP_11.equals(this.requestMsg.getMessageType())){
					// NOTA non quotare la soap action, per mantenere la trasparenza della PdD
					setRequestHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,this.soapAction, this.logger, propertiesTrasportoDebug);
				}
				if(this.debug)
					this.logger.info("SOAP Action inviata ["+this.soapAction+"]",false);
			}
			
					
			// Impostazione Proprieta del trasporto
			if(this.debug)
				this.logger.debug("Impostazione header di trasporto...");
			if(this.propertiesTrasporto != null){
				Iterator<String> keys = this.propertiesTrasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					String value = (String) this.propertiesTrasporto.get(key);
					setRequestHeader(key, value, this.logger, propertiesTrasportoDebug);
				}
			}
			
					
			// Spedizione byte
			boolean consumeRequestMessage = true;
			if(this.debug)
				this.logger.debug("Serializzazione ["+this.outputFile.getAbsolutePath()+"] (consume-request-message:"+consumeRequestMessage+")...");
			OutputStream out = new FileOutputStream(this.outputFile);
			if(this.isDumpBinario()) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				if(this.isSoap && this.sbustamentoSoap){
					this.logger.debug("Sbustamento...");
					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,bout);
				}else{
					this.requestMsg.writeTo(bout, consumeRequestMessage);
				}
				bout.flush();
				bout.close();
				out.write(bout.toByteArray());
				bout.close();
				
				this.dumpBinarioRichiestaUscita(bout, contentTypeRichiesta, this.location, propertiesTrasportoDebug);
			}else{
				if(this.isSoap && this.sbustamentoSoap){
					if(this.debug)
						this.logger.debug("Sbustamento...");
					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,out);
				}else{
					this.requestMsg.writeTo(out, consumeRequestMessage);
				}
			}
			out.flush();
			out.close();
			if(this.debug)
				this.logger.debug("Serializzazione ["+this.outputFile.getAbsolutePath()+"] effettuata");
			
			
			// Spedizione File Headers
			this.boutFileOutputHeaders.flush();
			this.boutFileOutputHeaders.close();
			if(this.outputFileHeaders!=null){
				if(this.debug)
					this.logger.debug("Serializzazione File Output Headers ["+this.outputFileHeaders.getAbsolutePath()+"]...");
				out = new FileOutputStream(this.outputFileHeaders);
				if(this.debug){
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					bout.write(this.boutFileOutputHeaders.toByteArray());
					bout.flush();
					bout.close();
					out.write(bout.toByteArray());
					this.logger.info("File Header Serializzato:\n"+bout.toString(),false);
				}else{
					out.write(this.boutFileOutputHeaders.toByteArray());
				}
				out.flush();
				out.close();
				if(this.debug)
					this.logger.debug("Serializzazione File Output Headers ["+this.outputFileHeaders.getAbsolutePath()+"] effettuata");
			}
			

			
			
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			
			// return code
			this.codice = 200; // codice deve essere impostato prima delle invocazioni dei successivi metodi nel caso di gestione response, altrimenti cmq ho finito
			

			
			if(this.generateResponse){

				
				
				/* ------------  PreInResponseHandler ------------- */
				this.preInResponse();
				
				// Lettura risposta parametri NotifierInputStream per la risposta
				this.notifierInputStreamParams = null;
				if(this.preInResponseContext!=null){
					this.notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
				}

				
				
				
				
				
				/* ------------  Response ------------- */
				
				// InputFile
				this.checkInputFile(this.inputFile, "Response");
				
				// InputFile Header
				if(this.inputFileHeaders!=null){
					this.checkInputFile(this.inputFileHeaders, "Response-Headers");
					this.propertiesTrasportoRisposta = new HashMap<String, String>();
					FileInputStream fin = new FileInputStream(this.inputFileHeaders);
					try{
						Properties pTmp = new Properties();
						pTmp.load(fin);
						if(pTmp.size()>0) {
							Enumeration<Object> en = pTmp.keys();
							while (en.hasMoreElements()) {
								Object oKey = (Object) en.nextElement();
								if(oKey instanceof String) {
									String key = (String) oKey;
									this.propertiesTrasportoRisposta.put(key, pTmp.getProperty(key));				
								}
							}
							
						}
					}
					catch(Exception e){
						throw new Exception("Input File (Response-Headers) ["+this.inputFileHeaders.getAbsolutePath()+"] with wrong format: "+e.getMessage(),e);
					}
					finally{
						try{
							if(fin!=null){
								fin.close();
							}
						}catch(Exception eClose){}
					}
				}
		
				
				// Dati Risposta
				if(this.debug)
					this.logger.debug("Analisi risposta...");
				if(this.propertiesTrasportoRisposta!=null && this.propertiesTrasportoRisposta.size()>0){			
					this.tipoRisposta = TransportUtils.get(this.propertiesTrasportoRisposta, HttpConstants.CONTENT_TYPE); 
				}
				if(this.tipoRisposta==null || "".equals(this.tipoRisposta)) {
					this.tipoRisposta = contentTypeRichiesta;
					if(this.propertiesTrasportoRisposta==null) {
						this.propertiesTrasportoRisposta = new HashMap<String, String>();
					}
					this.propertiesTrasportoRisposta.put(HttpConstants.CONTENT_TYPE, this.tipoRisposta);
				}

								
				// Parametri di imbustamento
				if(this.isSoap){
					this.imbustamentoConAttachment = false;
					if(this.propertiesTrasportoRisposta!=null && this.propertiesTrasportoRisposta.size()>0){
						if("true".equals(this.propertiesTrasportoRisposta.get(this.openspcoopProperties.getTunnelSOAPKeyWord_headerTrasporto()))){
							this.imbustamentoConAttachment = true;
						}
						this.mimeTypeAttachment = this.propertiesTrasportoRisposta.get(this.openspcoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
						if(this.mimeTypeAttachment==null)
							this.mimeTypeAttachment = HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
						//System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");
					}
				}
				
				
				// Gestione risposta
				
				this.isResponse = new FileInputStream(this.inputFile);
								
				this.normalizeInputStreamResponse();
				
				this.initCheckContentTypeConfiguration();
				
				if(this.isDumpBinario()){
					this.dumpResponse(this.propertiesTrasportoRisposta);
				}
								
				if(this.isRest){
					
					if(this.doRestResponse()==false){
						return false;
					}
					
				}
				else{
				
					if(this.doSoapResponse()==false){
						return false;
					}
					
				}
				
			}


			if(this.debug)
				this.logger.info("Gestione consegna su file effettuata con successo",false);
			
			return true;

		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			String msgErrore = this.readExceptionMessageFromException(e);
			if(this.generateErrorWithConnectorPrefix) {
				this.errore = "Errore avvenuto durante la consegna su FileSystem: "+msgErrore;
			}
			else {
				this.errore = msgErrore;
			}
			this.logger.error("Errore avvenuto durante la consegna su FileSystem: "+msgErrore,e);
			return false;
		} 
	}
	
    /**
     * Effettua la disconnessione 
     */
    @Override
	public void disconnect() throws ConnettoreException{
    	try{
    	
			// Gestione finale
    		
	    	if(this.isResponse!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura inputStream ["+this.inputFile.getAbsolutePath()+"] ...");
	    		try{
					this.isResponse.close();
					if(this.debug && this.logger!=null)
		    			this.logger.debug("Chiusura inputStream ["+this.inputFile.getAbsolutePath()+"] effettuata con successo");
	    		}catch(Exception e){
	    			if(this.logger!=null)
		    			this.logger.error("Chiusura inputStream ["+this.inputFile.getAbsolutePath()+"] non riuscita");
	    		}
			}
    		
	    	if(this.inputFile!=null && this.inputFileDeleteAfterRead && this.inputFile.exists()){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Eliminazione File ["+this.inputFile.getAbsolutePath()+"] ...");
				if(this.inputFile.delete()){
					if(this.debug && this.logger!=null)
		    			this.logger.debug("Eliminazione File ["+this.inputFile.getAbsolutePath()+"] effettuata con successo");
				}
				else{
					if(this.logger!=null)
		    			this.logger.error("Eliminazione File ["+this.inputFile.getAbsolutePath()+"] non riuscita");
				}
			}
	    	
			// Gestione finale della connessione
	    	if(this.inputFileHeaders!=null && this.inputFileDeleteAfterRead && this.inputFileHeaders.exists()){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Eliminazione File ["+this.inputFileHeaders.getAbsolutePath()+"] ...");
				if(this.inputFileHeaders.delete()){
					if(this.debug && this.logger!=null)
		    			this.logger.debug("Eliminazione File ["+this.inputFileHeaders.getAbsolutePath()+"] effettuata con successo");
				}
				else{
					if(this.logger!=null)
		    			this.logger.error("Eliminazione File ["+this.inputFileHeaders.getAbsolutePath()+"] non riuscita");
				}
			}
	

	    	
	    	// super.disconnect (Per risorse base)
	    	super.disconnect();
			
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura non riuscita: "+e.getMessage(),e);
    	}
    }

    
    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation(){
    	return this.location;
    }


    private void setRequestHeader(String key, String value, ConnettoreLogger logger, Map<String, String> propertiesTrasportoDebug) throws Exception {
    	
    	if(this.debug)
			this.logger.info("Set proprietà trasporto ["+key+"]=["+value+"]",false);
    	setRequestHeader(key,value, propertiesTrasportoDebug);
    	
    }
    
    @Override
   	protected void setRequestHeader(String key,String value) throws Exception{
    	this.boutFileOutputHeaders.write((key+"="+value+"\n").getBytes()); 
    }
    
    
    private void checkOutputFile(File file, String tipo) throws Exception{
    	if(this.debug){
			this.logger.debug("Check file output ("+tipo+") ["+file.getAbsolutePath()+"]...");
		}
		if(file.exists()){
			if(this.debug){
				this.logger.debug("File output ("+tipo+") ["+file.getAbsolutePath()+"] exists canRead["+file.canRead()+
					"] canWrite["+file.canWrite()+"] canExcute["+file.canExecute()+"] isDirectory["+
					file.isDirectory()+"] isFile["+file.isFile()+"] isHidden["+file.isHidden()+"]");	
			}
			if(this.outputFileOverwriteIfExists==false){
				throw new Exception("Output File ("+tipo+") ["+file.getAbsolutePath()+"] already exists (overwrite not permitted)");
			}
			if(file.isFile()==false){
				throw new Exception("Output File ("+tipo+") ["+file.getAbsolutePath()+"] already exists with uncorrect type (is Directory?)");
			}
			if(file.canWrite()==false){
				throw new Exception("Output File ("+tipo+") ["+file.getAbsolutePath()+"] already exists (cannot rewrite)");
			}
		}
		if(this.debug){
			this.logger.debug("("+tipo+") Check parent directory ...");
		}
		if(file.getParentFile()!=null){
			if(this.debug){
				this.logger.debug("("+tipo+") Check parent directory ["+file.getParentFile().getAbsolutePath()+"] ...");
			}
			if(file.getParentFile().exists()==false){
				if(this.outputFileAutoCreateParentDirectory==false){
					throw new Exception("Parent Directory Output File ("+tipo+") ["+file.getParentFile().getAbsolutePath()+"] not exists (auto create not permitted)");
				}
				FileSystemUtilities.mkdirParentDirectory(file);
			}
			else{
				if(this.debug){
					this.logger.debug("Parent Directory Output File ("+tipo+") ["+file.getParentFile().getAbsolutePath()+"] exists canRead["+file.getParentFile().canRead()+
						"] canWrite["+file.getParentFile().canWrite()+"] canExcute["+file.getParentFile().canExecute()+"] isDirectory["+
						file.getParentFile().isDirectory()+"] isFile["+file.getParentFile().isFile()+"] isHidden["+file.getParentFile().isHidden()+"]");	
				}
				if(file.getParentFile().isDirectory()==false){
					throw new Exception("Parent Directory Output File ("+tipo+") ["+file.getParentFile().getAbsolutePath()+"] already exists with uncorrect type (is File?)");
				}
				if(file.getParentFile().canWrite()==false){
					throw new Exception("Parent Directory Output File ("+tipo+") ["+file.getParentFile().getAbsolutePath()+"] already exists (cannot rewrite)");
				}
			}
		}
    }
    
    private void checkInputFile(File file, String tipo) throws Exception{
    	if(this.debug){
			this.logger.debug("Check file input ("+tipo+") ["+file.getAbsolutePath()+"]...");
		}
		if(file.exists()){
			this._checkInputFile(file, tipo);
		}
		else{
			
			if(this.inputFileWaitTimeIfNotExists!=null && this.inputFileWaitTimeIfNotExists>0){
				
				int millisecond = this.inputFileWaitTimeIfNotExists;
				int tempoSingoloSleepMs = 250;
				if(millisecond>tempoSingoloSleepMs){
					int count = millisecond/tempoSingoloSleepMs;
					int resto = millisecond%tempoSingoloSleepMs;
					this.logger.info("Wait "+millisecond+"ms ...", false);
					for (int i = 0; i < count; i++) {
						Utilities.sleep(tempoSingoloSleepMs);
						if(file.exists()){
							break;
						}
					}
					if(!file.exists()){
						Utilities.sleep(resto);
					}
					this.logger.info("Wait "+millisecond+"ms terminated", false);
				}else{
					this.logger.info("Wait "+millisecond+"ms ...", false);
					Utilities.sleep(tempoSingoloSleepMs);
					this.logger.info("Wait "+millisecond+"ms terminated", false);
				}
				
				if(file.exists()){
					this._checkInputFile(file, tipo);
				}
				else{
					throw new Exception("Input File ("+tipo+") ["+file.getAbsolutePath()+"] not exists (wait "+millisecond+"ms)");
				}
			}
			else{
				throw new Exception("Input File ("+tipo+") ["+file.getAbsolutePath()+"] not exists");
			}
		}
    }
    private void _checkInputFile(File file, String tipo) throws Exception{
    	if(this.debug){
			this.logger.debug("File input ("+tipo+") ["+file.getAbsolutePath()+"] exists canRead["+file.canRead()+
				"] canWrite["+file.canWrite()+"] canExcute["+file.canExecute()+"] isDirectory["+
				file.isDirectory()+"] isFile["+file.isFile()+"] isHidden["+file.isHidden()+"] lenght["+file.length()+"]");	
		}
		if(file.isFile()==false){
			throw new Exception("Input File ("+tipo+") ["+file.getAbsolutePath()+"] with uncorrect type (is Directory?)");
		}
		if(file.canRead()==false){
			throw new Exception("Input File ("+tipo+") ["+file.getAbsolutePath()+"] cannot read");
		}
		if(file.length()<=0){
			throw new Exception("Input File ("+tipo+") ["+file.getAbsolutePath()+"] empty");
		}
		if(this.inputFileDeleteAfterRead){
			if(file.canWrite()==false){
				throw new Exception("Input File ("+tipo+") ["+file.getAbsolutePath()+"] cannot write (delete after read required)");
			}
		}
    }
        
}






