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




package org.openspcoop2.pdd.core.connettori;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;



/**
 * Classe utilizzata per effettuare consegne di messaggi su file system
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreFILE extends ConnettoreBaseWithResponse {

    @Override
	public String getProtocollo() {
    	return "FILE";
    }
	
	/* ********  F I E L D S  P R I V A T I  ******** */

	public ByteArrayOutputStream outByte = new ByteArrayOutputStream();

	/** File */
	private ConnettoreFile_outputConfig outputFile = null;
	private ConnettoreFile_outputConfig outputFileHeaders = null;
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
			Map<String, Object> dynamicMap = new HashMap<>();
			DynamicInfo dInfo = new DynamicInfo(request, this.getPddContext());
			DynamicUtils.fillDynamicMap(this.logger.getLogger(),dynamicMap, dInfo);
			
			
			// Identificativo modulo
			this.idModulo = request.getIdModulo();
					
			// Context per invocazioni handler
			this.outRequestContext = request.getOutRequestContext();
			this.msgDiagnostico = request.getMsgDiagnostico();
			
			
			// analsi i parametri specifici per il connettore
			
			// OutputFile
			this.outputFile = readOutputConfig(request.getTipoConnettore(), true, 
					CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE, 
					CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_PERMISSIONS);
			if(this.outputFile==null || this.outputFile.getOutputFile()==null){
				return false;
			}
			
			// OutputFileHeader
			this.outputFileHeaders = readOutputConfig(request.getTipoConnettore(), false, 
					CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS, 
					CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS_PERMISSIONS);

			// Location
			this.location = this.outputFile.getOutputFile().getAbsolutePath();
			if(this.outputFileHeaders!=null){
				this.location = this.location + " [headers: "+this.outputFileHeaders.getOutputFile().getAbsolutePath()+"]";
			}
			
			// AutoCreateDir
			this.outputFileAutoCreateParentDirectory = this.isBooleanProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			
			// OverwriteFileIfExists
			this.outputFileOverwriteIfExists = this.isBooleanProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE);
			
			// GenerazioneRisposta
			this.generateResponse = this.isBooleanProperty(request.getTipoConnettore(), false, CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			
			if(this.generateResponse){
				
				// InputFile
				String tmp = this.getDynamicProperty(request.getTipoConnettore(), true, CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE, dynamicMap);
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
		int readConnectionTimeout = -1;
		boolean readConnectionTimeoutConfigurazioneGlobale = true;
		try{

			/* ------------  Request ------------- */
			
			
			// OutputFile
			this.checkOutputFile(this.outputFile, "Request");
			
			// OutputFile Headers
			if(this.outputFileHeaders!=null){
				this.checkOutputFile(this.outputFileHeaders, "Request-Headers");
			}
			
			
			// Tipologia di servizio
			MessageType requestMessageType = this.requestMsg.getMessageType();
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
			Map<String, List<String>> propertiesTrasportoDebug = null;
			if(this.isDumpBinarioRichiesta()) {
				propertiesTrasportoDebug = new HashMap<>();
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
			
			
			
			// Impostazione timeout
			if(this.debug)
				this.logger.debug("Impostazione timeout...");
			if(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT)!=null){
				try{
					readConnectionTimeout = Integer.parseInt(this.properties.get(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT));
					readConnectionTimeoutConfigurazioneGlobale = this.properties.containsKey(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT_GLOBALE);
				}catch(Exception e){
					this.logger.error("Parametro "+CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT+" errato",e);
				}
			}
			if(readConnectionTimeout==-1){
				readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			}
			if(this.debug)
				this.logger.info("Impostazione read timeout ["+readConnectionTimeout+"]",false);
			
			

				
			// Impostazione Proprieta del trasporto
			if(this.debug)
				this.logger.debug("Impostazione header di trasporto...");
			this.forwardHttpRequestHeader();
			if(this.propertiesTrasporto != null){
				Iterator<String> keys = this.propertiesTrasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					List<String> values = this.propertiesTrasporto.get(key);
					if(this.debug) {
			    		if(values!=null && !values.isEmpty()) {
			        		for (String value : values) {
			        			this.logger.info("Set Transport Header ["+key+"]=["+value+"]",false);
			        		}
			    		}
			    	}
					
					setRequestHeader(key, values, this.logger, propertiesTrasportoDebug);
				}
			}
			
			
			
			
			
			// Aggiunta del SoapAction Header in caso di richiesta SOAP
			// spostato sotto il forwardHeader per consentire alle trasformazioni di modificarla
			if(this.isSoap && this.sbustamentoSoap == false){
				if(this.debug)
					this.logger.debug("Impostazione soap action...");
				boolean existsTransportProperties = false;
				if(TransportUtils.containsKey(this.propertiesTrasporto, Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION)){
					this.soapAction = TransportUtils.getFirstValue(this.propertiesTrasporto, Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION);
					existsTransportProperties = (this.soapAction!=null);
				}
				if(!existsTransportProperties) {
					this.soapAction = soapMessageRequest.getSoapAction();
				}
				if(this.soapAction==null){
					this.soapAction="\"OpenSPCoop\"";
				}
				if(MessageType.SOAP_11.equals(this.requestMsg.getMessageType()) && !existsTransportProperties){
					// NOTA non quotare la soap action, per mantenere la trasparenza della PdD
					setRequestHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,this.soapAction, this.logger, propertiesTrasportoDebug);
				}
				if(this.debug)
					this.logger.info("SOAP Action inviata ["+this.soapAction+"]",false);
			}
			
			
			
			
					
			// Spedizione byte
			boolean consumeRequestMessage = true;
			if(this.debug)
				this.logger.debug("Serializzazione ["+this.outputFile.getOutputFile().getAbsolutePath()+"] (consume-request-message:"+consumeRequestMessage+")...");
			OutputStream out = new FileOutputStream(this.outputFile.getOutputFile());
			if(this.isDumpBinarioRichiesta()) {
				DumpByteArrayOutputStream bout = new DumpByteArrayOutputStream(this.dumpBinario_soglia, this.dumpBinario_repositoryFile, this.idTransazione, 
						TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.getValue());
				try {
					this.emitDiagnosticStartDumpBinarioRichiestaUscita();
					
					if(this.isSoap && this.sbustamentoSoap){
						this.logger.debug("Sbustamento...");
						TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,bout);
					}else{
						this.requestMsg.writeTo(bout, consumeRequestMessage);
					}
					bout.flush();
					bout.close();
					
					if(bout.isSerializedOnFileSystem()) {
						try(FileInputStream fin = new FileInputStream(bout.getSerializedFile())) {
							Utilities.copy(fin, out);
						}
					}
					else {
						out.write(bout.toByteArray());
					}
					
					out.flush();
					out.close();
					
					this.dataRichiestaInoltrata = DateManager.getDate();
					
					this.dumpBinarioRichiestaUscita(bout, requestMessageType, contentTypeRichiesta, this.location, propertiesTrasportoDebug);
				}finally {
					try {
						bout.clearResources();
					}catch(Throwable t) {
						this.logger.error("Release resources failed: "+t.getMessage(),t);
					}
				}
			}else{
				if(this.isSoap && this.sbustamentoSoap){
					if(this.debug)
						this.logger.debug("Sbustamento...");
					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,out);
				}else{
					this.requestMsg.writeTo(out, consumeRequestMessage);
				}
				
				out.flush();
				out.close();
				
				this.dataRichiestaInoltrata = DateManager.getDate();
			}

			if(this.debug)
				this.logger.debug("Serializzazione ["+this.outputFile.getOutputFile().getAbsolutePath()+"] effettuata");
			
			if(this.outputFile.isPermission()) {
				if(this.debug)
					this.logger.debug("Modifica diritti del file ["+this.outputFile.getOutputFile().getAbsolutePath()+"] ("+this.outputFile.getPermissionAsString()+") ...");
				setPermission(this.outputFile);
				if(this.debug)
					this.logger.debug("Modifica diritti del file ["+this.outputFile.getOutputFile().getAbsolutePath()+"] ("+this.outputFile.getPermissionAsString()+") effettuata");
			}
			
			
			// Spedizione File Headers
			this.boutFileOutputHeaders.flush();
			this.boutFileOutputHeaders.close();
			if(this.outputFileHeaders!=null){
				if(this.debug)
					this.logger.debug("Serializzazione File Output Headers ["+this.outputFileHeaders.getOutputFile().getAbsolutePath()+"]...");
				out = new FileOutputStream(this.outputFileHeaders.getOutputFile());
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
					this.logger.debug("Serializzazione File Output Headers ["+this.outputFileHeaders.getOutputFile().getAbsolutePath()+"] effettuata");
				
				if(this.outputFileHeaders.isPermission()) {
					if(this.debug)
						this.logger.debug("Modifica diritti del file ["+this.outputFileHeaders.getOutputFile().getAbsolutePath()+"] ("+this.outputFileHeaders.getPermissionAsString()+") ...");
					setPermission(this.outputFileHeaders);
					if(this.debug)
						this.logger.debug("Modifica diritti del file ["+this.outputFileHeaders.getOutputFile().getAbsolutePath()+"] ("+this.outputFileHeaders.getPermissionAsString()+") effettuata");
				}
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
					this.propertiesTrasportoRisposta = new HashMap<>();
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
									String value = pTmp.getProperty(key);
									TransportUtils.addHeader(this.propertiesTrasportoRisposta, key, value);				
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
						}catch(Exception eClose){
							// close
						}
					}
				}
		
				
				// Dati Risposta
				if(this.debug)
					this.logger.debug("Analisi risposta...");
				if(this.propertiesTrasportoRisposta!=null && this.propertiesTrasportoRisposta.size()>0){			
					this.tipoRisposta = TransportUtils.getFirstValue(this.propertiesTrasportoRisposta, HttpConstants.CONTENT_TYPE); 
				}
				if(this.tipoRisposta==null || "".equals(this.tipoRisposta)) {
					this.tipoRisposta = contentTypeRichiesta;
					if(this.propertiesTrasportoRisposta==null) {
						this.propertiesTrasportoRisposta = new HashMap<>();
					}
					TransportUtils.setHeader(this.propertiesTrasportoRisposta, HttpConstants.CONTENT_TYPE, this.tipoRisposta);
				}

								
				// Parametri di imbustamento
				if(this.isSoap){
					this.imbustamentoConAttachment = false;
					if(this.propertiesTrasportoRisposta!=null && this.propertiesTrasportoRisposta.size()>0){
						if("true".equals(TransportUtils.getObjectAsString(this.propertiesTrasportoRisposta, this.openspcoopProperties.getTunnelSOAPKeyWord_headerTrasporto()))){
							this.imbustamentoConAttachment = true;
						}
						this.mimeTypeAttachment = TransportUtils.getFirstValue(this.propertiesTrasportoRisposta, this.openspcoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
						if(this.mimeTypeAttachment==null)
							this.mimeTypeAttachment = HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
						//System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");
					}
				}
				
				
				// Gestione risposta
				
				this.isResponse = new FileInputStream(this.inputFile);
								
				this.normalizeInputStreamResponse(readConnectionTimeout, readConnectionTimeoutConfigurazioneGlobale);
				
				this.initCheckContentTypeConfiguration();
				
				if(this.isDumpBinarioRisposta()){
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
			
			/**this.processConnectionTimeoutException(connectionTimeout, e, msgErrore);*/
			
			this.processReadTimeoutException(readConnectionTimeout, readConnectionTimeoutConfigurazioneGlobale, e, msgErrore);
			
			return false;
		} 
	}
	
    /**
     * Effettua la disconnessione 
     */
    @Override
	public void disconnect() throws ConnettoreException{
    	List<Throwable> listExceptionChiusura = new ArrayList<Throwable>();
		try{
    	
			// Gestione finale
    		
	    	if(this.isResponse!=null){
	    		if(this.debug && this.logger!=null)
	    			this.logger.debug("Chiusura inputStream ["+this.inputFile.getAbsolutePath()+"] ...");
	    		try{
					this.isResponse.close();
					if(this.debug && this.logger!=null)
		    			this.logger.debug("Chiusura inputStream ["+this.inputFile.getAbsolutePath()+"] effettuata con successo");
	    		}catch(Throwable e){
	    			if(this.logger!=null)
		    			this.logger.error("Chiusura inputStream ["+this.inputFile.getAbsolutePath()+"] non riuscita");
	    			listExceptionChiusura.add(e);
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
	    	try {
	    		super.disconnect();
	    	}catch(Throwable t) {
    			this.logger.debug("Chiusura risorse fallita: "+t.getMessage(),t);
    			listExceptionChiusura.add(t);
    		}
			
    	}catch(Exception e){
    		throw new ConnettoreException("Chiusura non riuscita: "+e.getMessage(),e);
    	}
		
		if(listExceptionChiusura!=null && !listExceptionChiusura.isEmpty()) {
			org.openspcoop2.utils.UtilsMultiException multiException = new org.openspcoop2.utils.UtilsMultiException(listExceptionChiusura.toArray(new Throwable[1]));
			throw new ConnettoreException("Chiusura connessione non riuscita: "+multiException.getMessage(),multiException);
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


    private void setRequestHeader(String key, String value, ConnettoreLogger logger, Map<String, List<String>> propertiesTrasportoDebug) throws Exception {
    	List<String> list = new ArrayList<>(); 
    	list.add(value);
    	this.setRequestHeader(key, list, logger, propertiesTrasportoDebug);
    }
    private void setRequestHeader(String key, List<String> values, ConnettoreLogger logger, Map<String, List<String>> propertiesTrasportoDebug) throws Exception {
    	
    	if(this.debug) {
    		if(values!=null && !values.isEmpty()) {
        		for (String value : values) {
        			this.logger.info("Set proprietà trasporto ["+key+"]=["+value+"]",false);		
        		}
    		}
    	}
    	setRequestHeader(key, values, propertiesTrasportoDebug);
    	
    }
    
    @Override
   	protected void setRequestHeader(String key,List<String> values) throws Exception{
    	if(values!=null && !values.isEmpty()) {
    		for (String value : values) {
    	    	this.boutFileOutputHeaders.write((key+"="+value+"\n").getBytes());		
			}
    	} 
    }
    
    
    private void checkOutputFile(ConnettoreFile_outputConfig fileConfig, String tipo) throws Exception{
    	File file = fileConfig.getOutputFile();
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
				FileSystemUtilities.mkdirParentDirectory(file,
						fileConfig.getReadable(), fileConfig.getReadable_ownerOnly(),
						fileConfig.getWritable(), fileConfig.getWritable_ownerOnly(),
						fileConfig.getExecutable(), fileConfig.getExecutable_ownerOnly());
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

    private ConnettoreFile_outputConfig readOutputConfig(String tipoConnettore,boolean required, String file, String permission) throws ConnettoreException {
    	String tmp = this.getDynamicProperty(tipoConnettore, required, file, this.dynamicMap);
    	if(tmp==null){
			return null;
		}
    	ConnettoreFile_outputConfig c = new ConnettoreFile_outputConfig();
    	c.setOutputFile(new File(tmp));
    	
    	String p = this.getDynamicProperty(tipoConnettore, false, permission, this.dynamicMap);
    	if(p!=null) {
    		 validatePermission(p, c);
    	}
    	return c;
    }
    
    public static void validatePermission(String p, ConnettoreFile_outputConfig c) throws ConnettoreException {
    	Scanner scanner = new Scanner(p);
		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line==null || line.trim().equals("") || line.trim().startsWith("#")) {
					continue;
				}
				_validateSinglePermission(line,c);
			}
		}finally {
			scanner.close();
		}
    }
    
    public static int getNumPermission(String p) throws ConnettoreException {
    	Scanner scanner = new Scanner(p);
		int num = 0;
    	try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line==null || line.trim().equals("") || line.trim().startsWith("#")) {
					continue;
				}
				num++;
			}
			return num;
		}finally {
			scanner.close();
		}
    }
    
    public static final String PERMESSI_FORMATO = "[o/a]+/-rwx";
    private static void _validateSinglePermission(String p, ConnettoreFile_outputConfig config) throws ConnettoreException {
    	String errorMsg = "Wrong permission format ("+p+"); expected "+PERMESSI_FORMATO;
    	if(p==null || StringUtils.isEmpty(p) || (!p.contains("+") && !p.contains("-")) || p.length()<=1) {
    		throw new ConnettoreException(errorMsg);
    	}
    	
    	String permission = null;
    	boolean add = false;
    	Boolean owner = null;
    	if(p.startsWith("+")) {
    		permission = p.substring(1);
    		add = true;
    	}
    	else if(p.startsWith("-")) {
    		permission = p.substring(1);
    		add = false;
    	}
    	else if(p.contains("+")) {
    		add = true;
    		int indexOf = p.indexOf("+");
    		String ownerS = p.substring(0, indexOf);
    		if(!"o".equals(ownerS) && !"a".equals(ownerS)) {
    			throw new ConnettoreException(errorMsg);
    		}
    		if("o".equals(ownerS)) {
    			owner = true;
    		}
    		else if("a".equals(ownerS)) {
    			owner = false;
    		}
    		if(indexOf == (p.length()-1)) {
    			throw new ConnettoreException(errorMsg);
    		}
    		permission = p.substring(indexOf+1, p.length());
    	}
    	else if(p.contains("-")) {
    		add = false;
    		int indexOf = p.indexOf("-");
    		String ownerS = p.substring(0, indexOf);
    		if(!"o".equals(ownerS) && !"a".equals(ownerS)) {
    			throw new ConnettoreException(errorMsg);
    		}
    		if("o".equals(ownerS)) {
    			owner = true;
    		}
    		else if("a".equals(ownerS)) {
    			owner = false;
    		}
    		if(indexOf == (p.length()-1)) {
    			throw new ConnettoreException(errorMsg);
    		}
    		permission = p.substring(indexOf+1, p.length());
    	}
    	
    	if(permission!=null) {
	    	for (int i = 0; i < permission.length(); i++) {
				char c = permission.charAt(i);
				if(c == 'r' || c == 'R') {
					config.setReadable(add);
					if(owner!=null) {
						config.setReadable_ownerOnly(owner);
					}
				}
				else if(c == 'w' || c == 'W') {
					config.setWritable(add);
					if(owner!=null) {
						config.setWritable_ownerOnly(owner);
					}
				}
				else if(c == 'x' || c == 'X') {
					config.setExecutable(add);
					if(owner!=null) {
						config.setExecutable_ownerOnly(owner);
					}
				}
				else {
					throw new ConnettoreException(errorMsg);
				}
			}
    	}
    	
    }
    
    private void setPermission(ConnettoreFile_outputConfig config) throws ConnettoreException {
    	if(config.getReadable()!=null) {
			if(config.getReadable_ownerOnly()!=null) {
				if(!config.getOutputFile().setReadable(config.getReadable(), config.getReadable_ownerOnly())) {
					// ignore
				}
			}
			else {
				if(!config.getOutputFile().setReadable(config.getReadable())) {
					// ignore
				}
			}
		}
		if(config.getWritable()!=null) {
			if(config.getWritable_ownerOnly()!=null) {
				if(!config.getOutputFile().setWritable(config.getWritable(), config.getWritable_ownerOnly())) {
					// ignore
				}
			}
			else {
				if(!config.getOutputFile().setWritable(config.getWritable())) {
					// ignore
				}
			}
		}
		if(config.getExecutable()!=null) {
			if(config.getExecutable_ownerOnly()!=null) {
				if(!config.getOutputFile().setExecutable(config.getExecutable(), config.getExecutable_ownerOnly())) {
					// ignore
				}
			}
			else {
				if(!config.getOutputFile().setExecutable(config.getExecutable())) {
					// ignore
				}
			}
		}
    }
}






