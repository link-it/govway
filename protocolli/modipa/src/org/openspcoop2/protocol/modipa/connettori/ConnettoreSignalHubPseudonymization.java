/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.connettori;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.message.xml.MessageDynamicNamespaceContextFactory;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.DigestServiceParams;
import org.openspcoop2.pdd.config.DigestServiceParamsDriver;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.Utilities;
import org.openspcoop2.pdd.core.connettori.ConnettoreBaseWithResponse;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.SignalHubUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.digest.DigestType;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.rest.problem.JsonSerializer;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathException;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathNotValidException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreSignalHubPseudonymization extends ConnettoreBaseWithResponse {
	
	@Override
	public String getProtocollo() {
    	return "http";
    }
	
	public static final String LOCATION = "govway://signalHubPseudonymization";
    
	private DumpByteArrayOutputStream requestBout = null;

	
	private DigestServiceParams initCryptoInfo(DigestServiceParamsDriver driver, PdDContext context) throws UtilsException, DriverConfigurazioneException, ProtocolException {
		RequestInfo reqInfo = (RequestInfo) context.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		IDServizio idServizio = reqInfo.getIdServizio();
		DigestServiceParams param = null;
		
		List<ProtocolProperty> protocolProperty = SignalHubUtils.obtainSignalHubProtocolProperty(context);
		
		// provo ad inizializzare il db
		driver.acquireLock(reqInfo.getIdTransazione());
		try {
			
			// se le informazioni crittografiche esistono gia le ritorno altrimenti le genero
			param = driver.getLastEntry(idServizio);
			if (param != null)
				return param;
			
			param = SignalHubUtils.generateDigestServiceParams(idServizio, protocolProperty, null);
			driver.addNewEntry(param);
		} finally {
			driver.releaseLock();
		}
		
		return param;
	}
	
	private String getSignalHubNS() throws ProtocolException {
		List<Proprieta> props = null;
		if (this.pa != null)
			props = this.pa.getProprieta();
		if (this.pd != null)
			props = this.pd.getProprieta();
		return CostantiProprieta.getPdndSignalHubNamespace(
				props, ModIProperties.getInstance().getSignalHubSoapNamespace());
	}
	
	private String getSoapNS() {
		return this.messageTypeResponse == MessageType.SOAP_11 ?
				Costanti.SOAP_ENVELOPE_NAMESPACE : Costanti.SOAP12_ENVELOPE_NAMESPACE;
	}
	
	private void setResponseOk(boolean isSoap, DigestType digest, byte[] seed) throws UtilsException, IOException, SAXException, ParserConfigurationException, XMLException, TransformerException, ProtocolException {
		if (isSoap) {
			//serializzo il messsaggio SOAP
			XMLUtils xmlUtils = XMLUtils.getInstance();
			
			String signalHubNS = getSignalHubNS();
			Document doc = xmlUtils.newDocument();
			Element response = doc.createElementNS(signalHubNS, "cr:pseudonymizationResponse");
			Element seedElement = doc.createElementNS(signalHubNS, "cr:seed");
			seedElement.appendChild(doc.createTextNode(new String(seed)));
			
			Element digestElement = doc.createElementNS(signalHubNS, "cr:cryptoHashFunction");
			digestElement.appendChild(doc.createTextNode(digest.toString()));
			
			response.appendChild(digestElement);
			response.appendChild(seedElement);
			
			String envNS = getSoapNS();
			this.isResponse = new ByteArrayInputStream(("<?xml version='1.0' encoding='UTF-8'?>"
				+ "<soapenv:Envelope xmlns:soapenv=\""+envNS+"\">"
				+ "<soapenv:Body>"
				+ xmlUtils.toString(response, true)
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>").getBytes());
			
			this.tipoRisposta = this.messageTypeResponse == MessageType.SOAP_11 ?
				HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2;
			this.codice = 200;			
		} else {
			// serializzo il messsaggio JSON
			JSONUtils jsonUtils = JSONUtils.getInstance();
			ObjectNode node = jsonUtils.newObjectNode();
			node.put("seed", new String(seed));
			node.put("cryptoHashFunction",digest.toString());
			
			this.isResponse = new ByteArrayInputStream(jsonUtils.toByteArray(node));
			this.tipoRisposta = HttpConstants.CONTENT_TYPE_JSON;
			this.codice = 200;
		}
	}
	
	private void setResponse404(boolean isSoap) throws UtilsException {
		String detail = "Informazioni di pseudoanonimizzazione non trovate";
		
		if (isSoap) {		
			this.responseMsg = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(this.requestMsg.getMessageType(), true, detail);
			
		} else {
			JsonSerializer jsonSerializer = new JsonSerializer();
			
			ProblemRFC7807 problemRFC7807 = new ProblemRFC7807();
			problemRFC7807.setStatus(404);
			problemRFC7807.setDetail(detail);
			problemRFC7807.setTitle("Not Found");
			problemRFC7807.setType("https://httpstatuses.com/404");
			
			this.isResponse = new ByteArrayInputStream(jsonSerializer.toByteArray(problemRFC7807));
			this.tipoRisposta = HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807;
			this.codice = 404;
			this.responseMsg = null;			
		}
	}
	
	private Long retrieveSignalId(boolean isSoap, RequestInfo reqInfo) throws MessageException, MessageNotSupportedException, ProtocolException, XPathException, XPathNotValidException {
		String signalId = null;
		if (isSoap) {
			String signalHubNS = getSignalHubNS();
			String soapNS = getSoapNS();
			
			DynamicNamespaceContext nsc = MessageDynamicNamespaceContextFactory
					.getInstance(this.requestMsg.getFactory())
					.getNamespaceContext(this.requestMsg.castAsSoap().getSOAPPart());
			
			if (nsc.getPrefix(signalHubNS) == null || nsc.getPrefix(signalHubNS).isBlank()) {
				this.responseMsg = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createFaultMessage(this.requestMsg.getMessageType(), true, "namespace per signal hub (" + signalHubNS + ") non individuato");
				return null;
			}
			
			XPathExpressionEngine engine = new XPathExpressionEngine();
			String query = String.format("/{%1$s}:Envelope/{%1$s}:Body/{%2$s}:pseudonymization/{%2$s}:signalId/text()", soapNS, signalHubNS);
			
			try {
				signalId = (String) engine.getMatchPattern(this.requestMsg.castAsSoap().getSOAPPart(), nsc, query, XPathReturnType.STRING);
			} catch (XPathNotFoundException e) {
				// ignore
			}
		} else {
			signalId = reqInfo.getProtocolContext().getParameterFirstValue(ModICostanti.MODIPA_SIGNAL_HUB_ID_SIGNAL_ID);
		}
		
		return signalId != null && !signalId.trim().isBlank() ? Long.parseLong(signalId) : null;
	}
	
	private void retrieveCryptoInfo(boolean isSoap, PdDContext pddContext) throws UtilsException, DriverConfigurazioneException, ProtocolException, ConnettoreException, IOException, SAXException, ParserConfigurationException, XMLException, TransformerException {
		
		RequestInfo reqInfo = (RequestInfo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
		IDServizio idServizio = reqInfo.getIdServizio();
		
		// ottengo il driver di configurazione
		Object db = ConfigurazionePdDReader.getDriverConfigurazionePdD();
		if (!(db instanceof DriverConfigurazioneDB))
			throw new UtilsException("driver trovato non di tipo db");
		
		
		// cerco di ottenere l'id del segnale per fare una ricerca puntuale delle informazioni crittografiche
		DigestServiceParams param = null;
		
		try {
			Long signalId = retrieveSignalId(isSoap, reqInfo);
			if (this.responseMsg != null)
				return;
			
			DigestServiceParamsDriver driver = new DigestServiceParamsDriver((DriverConfigurazioneDB) db);
			
			// se signalId risulta uguale a null ritornero le informazioni critoogtafiche piu recenti
			param = driver.getEntry(idServizio, signalId);
			
			// se non esistno informazioni crittografiche nel db lo inizializzo
			if (param == null && signalId == null) {
				param = this.initCryptoInfo(driver, pddContext);
			}
			
		} catch (NumberFormatException | MessageException | MessageNotSupportedException | XPathException | XPathNotValidException e) {
			throw new ConnettoreException("signalId params non int64");
		}
		
		
		// se non ho trovato alcuna informazione crittografica ritorno 404
		if (param == null) {
			this.setResponse404(isSoap);
			return;
		}
		
		this.setResponseOk(isSoap, param.getDigestAlgorithm(), param.getSeed());
	}
	
	/* ********  METODI  ******** */

	@Override
	protected boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request) {
		return this.initialize(request, false, responseCachingConfig);
	}
	
	
	@Override
	protected boolean send(ConnettoreMsg request) {
		PdDContext pddContext = request.getOutRequestContext().getPddContext();
				
		this.codice = 200;
		
		try{
			
			// Tipologia di servizio
			MessageType requestMessageType = this.requestMsg.getMessageType();
			OpenSPCoop2SoapMessage soapMessageRequest = null;
			if(this.debug)
				this.logger.debug("Tipologia Servizio: "+this.requestMsg.getServiceBinding());
			if(this.isSoap){
				soapMessageRequest = this.requestMsg.castAsSoap();
			}
			
			
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
					throw new ConnettoreException("Content-Type del messaggio da spedire non definito");
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
			int readConnectionTimeout = -1;
			boolean readConnectionTimeoutConfigurazioneGlobale = true;
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
					String key = keys.next();
					List<String> values = this.propertiesTrasporto.get(key);
					if(this.debug && values!=null && !values.isEmpty()) {
			        		for (String value : values) {
			        			this.logger.info("Set proprietà trasporto ["+key+"]=["+value+"]",false);
			        		}
			    		}
					setRequestHeader(key, values, this.logger, propertiesTrasportoDebug);
				}
			}
			
			
			
			// Aggiunga del SoapAction Header in caso di richiesta SOAP
			// spostato sotto il forwardHeader per consentire alle trasformazioni di modificarla
			if(this.isSoap && !this.sbustamentoSoap){
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
					setRequestHeader(Costanti.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION,this.soapAction, propertiesTrasportoDebug);
				}
				if(this.debug)
					this.logger.info("SOAP Action inviata ["+this.soapAction+"]",false);
			}
			
			
			
			// SIMULAZIONE WRITE_TO
			boolean consumeRequestMessage = false;
			if(this.debug)
				this.logger.debug("Serializzazione (consume-request-message:"+consumeRequestMessage+")...");
			if(this.isDumpBinarioRichiesta()) {
				this.requestBout = new DumpByteArrayOutputStream(this.dumpBinario_soglia, this.dumpBinario_repositoryFile, this.idTransazione, 
						"NullEcho-"+TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.getValue());
				
				this.emitDiagnosticStartDumpBinarioRichiestaUscita();
				
				if(this.isSoap && this.sbustamentoSoap){
					this.logger.debug("Sbustamento...");
					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,this.requestBout);
				}else{
					this.requestMsg.writeTo(this.requestBout, consumeRequestMessage);
				}
				this.requestBout.flush();
				this.requestBout.close();
									
				this.dataRichiestaInoltrata = DateManager.getDate();
				
				this.dumpBinarioRichiestaUscita(this.requestBout, requestMessageType, contentTypeRichiesta, this.location, propertiesTrasportoDebug);
			}
			else {
				this.requestBout = new DumpByteArrayOutputStream(this.dumpBinario_soglia, this.dumpBinario_repositoryFile, this.idTransazione, 
						"NullEcho-"+TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO.getValue()); 
				if(this.isSoap && this.sbustamentoSoap){
					this.logger.debug("Sbustamento...");
					TunnelSoapUtils.sbustamentoMessaggio(soapMessageRequest,this.requestBout);
				}else{
					this.requestMsg.writeTo(this.requestBout, consumeRequestMessage);
				}
				this.requestBout.flush();
				this.requestBout.close();
				
				this.dataRichiestaInoltrata = DateManager.getDate();
			}
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			/* ------------  PreInResponseHandler ------------- */
			this.preInResponse();
			
			// Lettura risposta parametri NotifierInputStream per la risposta
			org.openspcoop2.utils.io.notifier.NotifierInputStreamParams notifierInputStreamParams = null;
			if(this.preInResponseContext!=null){
				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
			}
			
			// operazione di gestione delle informaizoni crittografiche
			this.messageTypeResponse = this.requestMsg.getMessageType();
			this.responseMsg = null;
			this.retrieveCryptoInfo(this.isSoap, pddContext);
			

			this.normalizeInputStreamResponse(readConnectionTimeout, readConnectionTimeoutConfigurazioneGlobale);
			
			this.initCheckContentTypeConfiguration();
			
			
			if(this.isDumpBinarioRisposta()){
				this.dumpResponse(this.propertiesTrasportoRisposta);
			}
			
			if(this.isResponse!=null) {
				this.emitDiagnosticResponseRead(this.isResponse);
			}
			
			OpenSPCoop2MessageFactory messageFactory = Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo, MessageRole.RESPONSE);
			OpenSPCoop2MessageParseResult pr = null;
			
			if (this.isResponse != null) {
				
				TransportResponseContext responseContext = new TransportResponseContext(this.logger.getLogger());
				responseContext.setCodiceTrasporto(this.codice+"");
				responseContext.setContentLength(this.contentLength);
				TransportUtils.addHeader(this.propertiesTrasportoRisposta, HttpConstants.CONTENT_TYPE, this.tipoRisposta);
				responseContext.setHeaders(this.propertiesTrasportoRisposta);
				
				pr = messageFactory.createMessage(this.messageTypeResponse, responseContext,
						this.isResponse,notifierInputStreamParams,
						this.openspcoopProperties.getAttachmentsProcessingMode());
			} else if (this.responseMsg == null){
				TransportResponseContext responseContext = new TransportResponseContext(this.logger.getLogger());
				responseContext.setContentLength(0);
				
				pr = messageFactory.createMessage(this.messageTypeResponse,responseContext,
						this.isResponse,notifierInputStreamParams,
						this.openspcoopProperties.getAttachmentsProcessingMode());
			}
			
			if(pr != null && pr.getParseException()!=null){
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
			}
			
			if (this.responseMsg == null && pr != null)
				this.responseMsg = pr.getMessage_throwParseException();
			
			// content length
			if(this.responseMsg != null){
				this.contentLength = this.responseMsg.getIncomingMessageContentLength();
			}
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			String msgErrore = this.readExceptionMessageFromException(e);
			if(this.generateErrorWithConnectorPrefix) {
				this.errore = "rilevata anomalia; "+msgErrore;
			}
			else {
				this.errore = msgErrore;
			}
			this.logger.error("Rilevata anomalia: "+msgErrore,e);
			return false;
		}
		
		return true;
	}
    
    private void setRequestHeader(String key, String value, ConnettoreLogger logger, Map<String, List<String>> propertiesTrasportoDebug) throws Exception {
    	List<String> list = new ArrayList<>(); 
    	list.add(value);
    	this.setRequestHeader(key, list, logger, propertiesTrasportoDebug);
    }
    
    private void setRequestHeader(String key, List<String> values, ConnettoreLogger logger, Map<String, List<String>> propertiesTrasportoDebug) throws Exception {
    	
    	if(this.debug && values!=null && !values.isEmpty()) {
        	for (String value : values) {
        		logger.info("Set proprietà trasporto ["+key+"]=["+value+"]",false);		
        	}
    	}
    	setRequestHeader(key, values, propertiesTrasportoDebug);
    	
    }
    @Override
	protected void setRequestHeader(String key,List<String> values) throws Exception {
    	this.propertiesTrasportoRisposta.put(key, values);
    }
	
    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation() throws ConnettoreException {
    	return LOCATION;
    }

    @Override
	public void disconnect() throws ConnettoreException {
    	try {
			if(this.requestBout!=null) {
				this.requestBout.clearResources();
			}
		}catch(Exception e) {
			this.logger.error("Release resources failed: "+e.getMessage(), e);
		}
	}
}





