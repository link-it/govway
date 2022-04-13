/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.pdd.services;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;

import org.apache.commons.io.output.NullOutputStream;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapperUtilities;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.config.CachedConfigIntegrationReader;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.CORSFilter;
import org.openspcoop2.pdd.core.CORSWrappedHttpServletResponse;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazione;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.ConnectorUtils;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.protocol.basic.registry.ServiceIdentificationReader;
import org.openspcoop2.protocol.registry.CachedRegistryReader;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.InformazioniErroriInfrastrutturali;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.LimitExceededIOException;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.TimeoutIOException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.CORSRequestType;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.WrappedHttpServletResponse;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.slf4j.Logger;


/**
 * Libreria utilizzata dalle servlet contenente metodi che implementano 
 * le funzioni precedentemente implementate negli Handler.
 * 
 *
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServicesUtils {
	
	public static ServiceIdentificationReader getServiceIdentificationReader(Logger logCore, RequestInfo requestInfo,
			RegistroServiziManager registroServiziManager, ConfigurazionePdDManager configurazionePdDManager) throws Exception{
		IRegistryReader registryReader = new CachedRegistryReader(logCore, requestInfo.getProtocolFactory(), registroServiziManager, requestInfo);
		IConfigIntegrationReader configIntegrationReader = new CachedConfigIntegrationReader(logCore, requestInfo.getProtocolFactory(), configurazionePdDManager, requestInfo);
		return new ServiceIdentificationReader(registryReader, configIntegrationReader, requestInfo.getProtocolFactory(), logCore);
	}
	
	
	public static boolean isConnessioneClientNonDisponibile(Throwable t){
		if(t instanceof java.net.SocketException){
			return true;
		}
		else if(Utilities.existsInnerException(t, java.net.SocketException.class)){
			return true;
		}
		else if(Utilities.existsInnerException(t, java.io.IOException.class)){
			Throwable tInner = Utilities.getInnerException(t, java.io.IOException.class);
			if(tInner!=null && tInner.getMessage()!=null && tInner.getMessage().contains("Broken pipe")) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isConnessioneServerReadTimeout(Throwable t){
		
		try {
			java.net.SocketTimeoutException e = null;
			if(t instanceof java.net.SocketTimeoutException){
				e = (java.net.SocketTimeoutException) t;
			}
			else if(Utilities.existsInnerException(t, java.net.SocketTimeoutException.class)){
				e = (java.net.SocketTimeoutException) Utilities.getInnerInstanceException(t, java.net.SocketTimeoutException.class, true);
			}
			
			if(e!=null && e.getMessage()!=null && OpenSPCoop2Properties.getInstance().isServiceUnavailable_ReadTimedOut(e.getMessage())){
				return true;
			}
			else if(TimeoutIOException.isTimeoutIOException(t) && t.getMessage()!=null && t.getMessage().startsWith(CostantiPdD.PREFIX_TIMEOUT_RESPONSE)) {
				return true;
			}
			
		}catch(Throwable tIgnore) {}
		
		return false;
	}
	
	public static boolean isResponsePayloadTooLarge(Throwable t){
		try {
			if(LimitExceededIOException.isLimitExceededIOException(t) && t.getMessage()!=null && t.getMessage().startsWith(CostantiPdD.PREFIX_LIMITED_RESPONSE)) {
				return true;
			}
		}catch(Throwable tIgnore) {}
		
		return false;
	}
	
	public static InformazioniErroriInfrastrutturali readInformazioniErroriInfrastrutturali(PdDContext pddContext){
		
		InformazioniErroriInfrastrutturali informazioniErrori = new InformazioniErroriInfrastrutturali();
		
		boolean erroreUtilizzoConnettore = false;
		if(pddContext!=null){
			Object o = pddContext.getObject(org.openspcoop2.core.constants.Costanti.ERRORE_UTILIZZO_CONNETTORE);
			if(o!=null && (o instanceof Boolean)){
				erroreUtilizzoConnettore = (Boolean) o;
			}
		}
		informazioniErrori.setErroreUtilizzoConnettore(erroreUtilizzoConnettore);

		boolean erroreSOAPFaultServerPortaDelegata = false;
		if(pddContext!=null){
			Object o = pddContext.getObject(org.openspcoop2.core.constants.Costanti.ERRORE_SOAP_FAULT_SERVER);
			if(o!=null && (o instanceof Boolean)){
				erroreSOAPFaultServerPortaDelegata = (Boolean) o;
			}
		}
		informazioniErrori.setRicevutoSoapFaultServerPortaDelegata(erroreSOAPFaultServerPortaDelegata);
		
		boolean erroreContenutoRichiestaNonRiconosciuto = false;
		if(pddContext!=null){
			Object o = pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO);
			if(o!=null && (o instanceof Boolean)){
				erroreContenutoRichiestaNonRiconosciuto = (Boolean) o;
			}
		}
		informazioniErrori.setContenutoRichiestaNonRiconosciuto(erroreContenutoRichiestaNonRiconosciuto);
		
		boolean erroreContenutoRispostaNonRiconosciuto = false;
		if(pddContext!=null){
			Object o = pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO);
			if(o!=null && (o instanceof Boolean)){
				erroreContenutoRispostaNonRiconosciuto = (Boolean) o;
			}
		}
		informazioniErrori.setContenutoRispostaNonRiconosciuto(erroreContenutoRispostaNonRiconosciuto);
		
		return informazioniErrori;
	}
	
	


	public static String checkMustUnderstand(OpenSPCoop2SoapMessage message,IProtocolFactory<?> protocolFactory) throws MessageException{
		SOAPEnvelope envelope = null;
		SOAPHeader header = null;
		try {
			OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();

			if(openspcoopProperties!=null){
				if(openspcoopProperties.isBypassFilterMustUnderstandEnabledForAllHeaders()){
					return null;
				}else{
					
					envelope = message.getSOAPPart().getEnvelope();
					header = envelope.getHeader();
					
					//Se non c'e' l'header il controllo e' inutile
					if(header == null) return null;
					
					List<NameValue> filtri = openspcoopProperties.getBypassFilterMustUnderstandProperties(protocolFactory.getProtocol());
					if(filtri!=null && filtri.size()>0){
						return ServicesUtils.checkMustUnderstandHeaderElement(message.getMessageType(),header,filtri);
					}
				}
			}

		} catch (Exception ex) {
			
			Throwable t = ParseExceptionUtils.getParseException(ex);
			if(t!=null){
				throw new MessageException(ex);
			}
			
			if(Utilities.existsInnerException(ex, "com.ctc.wstx.exc.WstxUnexpectedCharException") || Utilities.existsInnerException(ex, "com.ctc.wstx.exc.WstxParsingException"))
				throw new MessageException(ex);
			else
				throw new MessageException("BypassMustUnderstand, errore durante il set processed degli header con mustUnderstand='1' e actor non presente: "+ex.getMessage(), 
					ex);
		}  finally{
			// *** GB ***
			header = null;
			envelope = null;
			// *** GB ***
		}     	
		return null;
	}
	private static String checkMustUnderstandHeaderElement(MessageType messageType, SOAPHeader header,List<NameValue> filtri) throws UtilsException{
		
		try{
			StringBuilder bfError = new StringBuilder();
			if(SoapUtils.checkMustUnderstandHeaderElement(messageType, header, filtri, bfError)==false){
				return bfError.toString();
			}
			return null;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		
	}
	
	public static String checkSOAPEnvelopeNamespace(OpenSPCoop2SoapMessage message, MessageType messageType) throws MessageException{
		try {
			if(SoapUtils.checkSOAPEnvelopeNamespace(message, messageType)==false){
				
				OpenSPCoop2MessageSoapStreamReader reader = message.getSoapReader();
				String envelopeNamespace = null;
				if(reader!=null) {
					envelopeNamespace = reader.getNamespace();
				}
				if(envelopeNamespace==null) {
					return message.getSOAPPart().getEnvelope().getNamespaceURI();
				}
				else {
					return envelopeNamespace;
				}
				
			}
			return null;
		} catch (Exception ex) {
			throw new MessageException("CheckSoapEnvelopeNamespace, errore durante il controllo del namespace del soap envelope: "+ex.getMessage(),ex);
		} 
	}

	
	public static void checkCharset(String contentType, List<String> ctDefault, MsgDiagnostico msgDiag, boolean request, TipoPdD tipoPdD) throws MessageException {
		try {
			if(contentType!=null && contentType.contains(HttpConstants.CONTENT_TYPE_PARAMETER_CHARSET)){
				String charset = null;
				if(ContentTypeUtilities.isMultipart(contentType)) {
					String ct = ContentTypeUtilities.getInternalMultipartContentType(charset);
					if(ct!=null) {
						charset = ContentTypeUtilities.readCharsetFromContentType(ct);
					}
				}
				else {
					charset = ContentTypeUtilities.readCharsetFromContentType(contentType);
				}
				if(charset.startsWith("\"") && charset.length()>1) {
					charset = charset.substring(1);
				}
				if(charset.endsWith("\"") && charset.length()>1) {
					charset = charset.substring(0,charset.length()-1);
				}
				boolean find = false;
				for (String def : ctDefault) {
					if(def.toLowerCase().equals(charset.toLowerCase())) {
						find = true;
						break;
					}
				}
				if(!find) {
					msgDiag.addKeyword(CostantiPdD.KEY_CONTENT_TYPE, contentType);
					StringBuilder sb = new StringBuilder();
					for (String def : ctDefault) {
						if(sb.length()>0) {
							sb.append(",");
						}
						sb.append(def);
					}
					msgDiag.addKeyword(CostantiPdD.KEY_CHARSET_DEFAULT, sb.toString());
					String idModuloFunzionale = TipoPdD.DELEGATA.equals(tipoPdD) ? MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI : MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE;
					String idDiagnostico = request ? "richiesta.warningCharsetDifferenteDefault" : "risposta.warningCharsetDifferenteDefault";
					msgDiag.logPersonalizzato(idModuloFunzionale, idDiagnostico);
				}
			}
		} catch (Exception ex) {
			throw new MessageException(ex.getMessage(),ex);
		}
	}

	
	public static boolean verificaRispostaRelazioneCodiceTrasporto202(IProtocolFactory<?> protocolFactory,OpenSPCoop2Properties openSPCoopProperties,
			OpenSPCoop2Message responseMessage,boolean gestioneLatoPortaDelegata) throws Exception{
		
		if(responseMessage==null){
			return false;
		}
		
		IProtocolManager protocolManager = protocolFactory.createProtocolManager(); 
		
		boolean rispostaPresente = true;
		if(protocolManager.isHttpEmptyResponseOneWay()){
			if(ServiceBinding.SOAP.equals(responseMessage.getServiceBinding())){
				OpenSPCoop2SoapMessage soap = responseMessage.castAsSoap();
				
				if(soap.hasAttachments()) {
					rispostaPresente = true;
				}
				else {
					if(!soap.isSOAPBodyEmpty()) {
						rispostaPresente = true;
					}
					else {
					
						//potenziale msg inutile.
						Object h = null;
						SOAPHeader header = null;
						h = soap.getSOAPHeader();
						if(h!=null){
							header = (SOAPHeader) h;
						}
						if(h==null || header==null ||  SoapUtils.getFirstNotEmptyChildNode(responseMessage.getFactory(), header,false)==null ){
							//System.out.println("MESSAGGIO INUTILE");
							rispostaPresente = false;
						}else{
							if(gestioneLatoPortaDelegata){
								if( protocolManager.isHttpOneWay_PD_HTTPEmptyResponse() == false ) {
									// E' possibile impostare una opzione che non torni nulla anche in questo caso
									rispostaPresente = false;
								}
							}
						}

						
						// *** GB ***
						header = null;
						h = null;
						// *** GB ***
					}
				}
			}
			else{
//				OpenSPCoop2RestMessage<?> rest = responseMessage.castAsRest();
//				if(rest.hasContent()==false){
//					//System.out.println("MESSAGGIO VUOTO");
//					rispostaPresente = false;
//				}
				rispostaPresente = true; // La gestione del return code in REST è indipendente dal fatto se vi è o meno un contenuto
			}
		}
		
		return rispostaPresente;
	}
		
	
	
	public static void setTransferLength(TransferLengthModes transferLengthMode,
			ConnectorInMessage connectorInMessage, ConnectorOutMessage connectorOutMessage,
			OpenSPCoop2Message message) throws Exception{
		String requestProtocoll = connectorInMessage.getProtocol();
		if(requestProtocoll!=null && requestProtocoll.endsWith("1.1")){
			if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(transferLengthMode)){
				connectorOutMessage.setHeader(HttpConstants.TRANSFER_ENCODING,HttpConstants.TRANSFER_ENCODING_VALUE_CHUNCKED);
			}
			else if(TransferLengthModes.CONTENT_LENGTH.equals(transferLengthMode)){
				if(message!=null){
					message.writeTo(NullOutputStream.NULL_OUTPUT_STREAM, false);
					connectorOutMessage.setContentLength((int)message.getOutgoingMessageContentLength());
				}
			}
		}
	}
	public static void setTransferLength(TransferLengthModes transferLengthMode,
			ConnectorInMessage connectorInMessage, ConnectorOutMessage connectorOutMessage,
			Long length) throws Exception{
		String requestProtocoll = connectorInMessage.getProtocol();
		if(requestProtocoll!=null && requestProtocoll.endsWith("1.1")){
			if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(transferLengthMode)){
				connectorOutMessage.setHeader(HttpConstants.TRANSFER_ENCODING,HttpConstants.TRANSFER_ENCODING_VALUE_CHUNCKED);
			}
			else if(TransferLengthModes.CONTENT_LENGTH.equals(transferLengthMode)){
				if(length!=null){
					connectorOutMessage.setContentLength(length.intValue());
				}
			}
		}
	}
	
	
	public static void setContentType(OpenSPCoop2Message message, ConnectorOutMessage connectorOutMessage) throws ConnectorException{
		try{
			String contentTypeRisposta = message.getContentType();
			if (contentTypeRisposta != null) {
				connectorOutMessage.setContentType(contentTypeRisposta);
			}else {
				if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
					throw new MessageException("Risposta errore senza Content-type");
				}
				else{
					if(message.castAsRest().hasContent()){
						throw new MessageException("Risposta errore senza Content-type");
					}
				}
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	
	public static void setGovWayHeaderResponse(OpenSPCoop2Message msg, OpenSPCoop2Properties openspcoopProperties,
			Map<String, List<String>> propertiesTrasporto, Logger logCore, boolean portaDelegata, PdDContext pddContext, RequestInfo requestInfo) {
				
		try {						
			UtilitiesIntegrazione utilitiesIntegrazione = null;
			if(portaDelegata) {
				utilitiesIntegrazione = UtilitiesIntegrazione.getInstancePDResponse(logCore);
			}
			else {
				utilitiesIntegrazione = UtilitiesIntegrazione.getInstancePAResponse(logCore);
			}
			
			List<String> idTransazioneValues = TransportUtils.getRawObject(propertiesTrasporto, Costanti.ID_TRANSAZIONE.getValue());
			
			if(idTransazioneValues==null || idTransazioneValues.isEmpty()) {
				String idTransazione = (String) pddContext.getObject(Costanti.ID_TRANSAZIONE);
				HeaderIntegrazione hdr = new HeaderIntegrazione(idTransazione);
				utilitiesIntegrazione.setTransportProperties(hdr,propertiesTrasporto,null);
			}
			else {
				utilitiesIntegrazione.setInfoProductTransportProperties(propertiesTrasporto);
			}
		}catch(Exception e){
			logCore.error("Set header di integrazione fallito: "+e.getMessage(),e);
		}
		if(pddContext.containsKey(CostantiControlloTraffico.PDD_CONTEXT_HEADER_RATE_LIMITING)) {
			Properties pHeaderRateLimiting = (Properties) pddContext.getObject(CostantiControlloTraffico.PDD_CONTEXT_HEADER_RATE_LIMITING);
			if(pHeaderRateLimiting.size()>0) {
				java.util.Enumeration<?> en = pHeaderRateLimiting.keys();
		    	while(en.hasMoreElements()){
		    		String key = (String) en.nextElement();
		    		TransportUtils.setHeader(propertiesTrasporto,key, pHeaderRateLimiting.getProperty(key));
		    	}	
			}
		}
				
		setCORSAllowOrigin(propertiesTrasporto, logCore, portaDelegata, pddContext, requestInfo);
				
		try {
			if(propertiesTrasporto!=null && !propertiesTrasporto.isEmpty()) {
				
				OpenSPCoop2MessageProperties forwardHeader = null;
				if(msg!=null &&
						openspcoopProperties!=null // Puo' non essere inizializzato
						) {
					if(ServiceBinding.REST.equals(msg.getServiceBinding())) {
						forwardHeader = msg.getForwardTransportHeader(openspcoopProperties.getRESTServicesHeadersForwardConfig(false));
					}
					else {
						forwardHeader = msg.getForwardTransportHeader(openspcoopProperties.getSOAPServicesHeadersForwardConfig(false));
					}
				}
				if(forwardHeader!=null && forwardHeader.size()>0) {
					for (String key : propertiesTrasporto.keySet()) {
						forwardHeader.removePropertyValues(key);
					}
				}
			}
		}catch(Exception e){
			logCore.error("Pulizia forward header, rispetto a quelli generati da GovWay, fallito: "+e.getMessage(),e);
		}
	}
	
	
	
	private static void setCORSAllowOrigin(Map<String, List<String>> propertiesTrasporto, Logger logCore, boolean portaDelegata, PdDContext pddContext, RequestInfo requestInfo) {
		try {
			
			Object nomePortaObject = pddContext.getObject(CostantiPdD.NOME_PORTA_INVOCATA);
			String nomePorta = null;
			if(nomePortaObject!=null && nomePortaObject instanceof String) {
				nomePorta = (String) nomePortaObject;
			}
			
			CorsConfigurazione cors = null;
			HttpServletRequest httpServletRequest = null;
			URLProtocolContext protocolContext = null;
			if(requestInfo!=null) {
				protocolContext = requestInfo.getProtocolContext();
			}
			if(protocolContext!=null) {
				httpServletRequest = protocolContext.getHttpServletRequest();	
				if(nomePorta==null) {
					nomePorta = protocolContext.getInterfaceName();
				}
			}
			
			if(httpServletRequest!=null) {
				ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
				if(nomePorta!=null) {
					if(portaDelegata) {
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(nomePorta);
						PortaDelegata pdDefault = configurazionePdDManager.getPortaDelegata_SafeMethod(idPD, requestInfo);
						cors = configurazionePdDManager.getConfigurazioneCORS(pdDefault);
					}
					else {
						IDPortaApplicativa idPA = new IDPortaApplicativa();
						idPA.setNome(nomePorta);
						PortaApplicativa paDefault = configurazionePdDManager.getPortaApplicativa_SafeMethod(idPA, requestInfo);
						cors = configurazionePdDManager.getConfigurazioneCORS(paDefault);						
					}
				}
				if(cors==null) {
					cors = configurazionePdDManager.getConfigurazioneCORS();
				}
			}
			else {
				cors = new CorsConfigurazione();
				cors.setStato(StatoFunzionalita.DISABILITATO);
			}
			
			if(StatoFunzionalita.ABILITATO.equals(cors.getStato()) && TipoGestioneCORS.GATEWAY.equals(cors.getTipo())) {
				// verifico che non siamo in una preflight già gestita
				boolean preflightRequest = pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY);
				if(!preflightRequest) {
					CORSFilter corsFilter = new CORSFilter(logCore, cors);
					CORSWrappedHttpServletResponse res = new CORSWrappedHttpServletResponse(false);
					corsFilter.doCORS(httpServletRequest, res, CORSRequestType.ACTUAL, true);
					propertiesTrasporto.putAll(res.getHeadersValues());
				}
			}
			
		}catch(Exception e){
			logCore.error("Set cors origin: "+e.getMessage(),e);
		}

	}
	
	
	public static boolean isRequestWsdl(ConnectorInMessage reqParam, Logger logCore) {
		
		try {
		
			if(reqParam==null || reqParam.getURLProtocolContext()==null || reqParam.getURLProtocolContext().getHttpServletRequest()==null) {
				return false;
			}
			HttpServletRequest req = reqParam.getURLProtocolContext().getHttpServletRequest();
			
			if(HttpRequestMethod.GET.equals(req.getMethod())){
				Enumeration<?> parameters = req.getParameterNames();
				while(parameters.hasMoreElements()){
					String key = (String) parameters.nextElement();
					String value = req.getParameter(key);
					if("wsdl".equalsIgnoreCase(key) && (value==null || "".equals(value)) ){
						return true;
					}
				}
			}
			
		}catch(Exception e){
			logCore.error("IsRequestWsdl fallita: "+e.getMessage(),e);
		}
		
		return false;
	}
	
	public static void writeWsdl(ConnectorOutMessage response,RequestInfo requestInfo, 
			IDService idService, ServiceIdentificationReader serviceIdentificationReader, Logger logCore) throws ConnectorException {
		try {
			
			boolean generazioneWsdlEnabled = false;
			if(IDService.PORTA_APPLICATIVA.equals(idService)) {
				generazioneWsdlEnabled = OpenSPCoop2Properties.getInstance().isGenerazioneWsdlPortaApplicativaEnabled();
			}
			else {
				generazioneWsdlEnabled = OpenSPCoop2Properties.getInstance().isGenerazioneWsdlPortaDelegataEnabled();
			}
			if(!generazioneWsdlEnabled) {
				response.setStatus(404);
				response.sendResponse(DumpByteArrayOutputStream.newInstance(ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeWsdlUnsupported(idService)).getBytes()));
				return;
			}
			
			byte [] wsdl = null;
			if(requestInfo!=null && requestInfo.getIdServizio()!=null) {
				AccordoServizioParteSpecifica asps = null;
				AccordoServizioParteComune aspc = null;
				byte [] wsdlLogico = null;
				try {
					asps = serviceIdentificationReader.getRegistryReader().getAccordoServizioParteSpecifica(requestInfo.getIdServizio(), false);
				}catch(RegistryNotFound notFound) {}
				if(asps!=null) {
					try {
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
						aspc = serviceIdentificationReader.getRegistryReader().getAccordoServizioParteComune(idAccordo, true);
					}catch(RegistryNotFound notFound) {}
				}
				if(aspc!=null) {
					if(TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio())){
						wsdlLogico = aspc.getByteWsdlLogicoFruitore();
					}
					else {
						wsdlLogico = aspc.getByteWsdlLogicoErogatore();
					}
				}
				if(wsdlLogico!=null) {
					// wsdl logico utilizzato dentro wsdlWrapperUtilities
					XMLUtils xmlUtils = XMLUtils.DEFAULT;
					WSDLUtilities wsdlUtilities = new WSDLUtilities(xmlUtils);
					AccordoServizioWrapperUtilities wsdlWrapperUtilities = new AccordoServizioWrapperUtilities(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), logCore);
					wsdlWrapperUtilities.setAccordoServizio(new AccordoServizioWrapper());
					wsdlWrapperUtilities.getAccordoServizioWrapper().setAccordoServizio(aspc);
					javax.wsdl.Definition wsdlDefinition = null;
					if(TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio())){
						wsdlWrapperUtilities.getAccordoServizioWrapper().setBytesWsdlImplementativoFruitore(asps.getByteWsdlImplementativoFruitore());
						wsdlDefinition = wsdlWrapperUtilities.buildWsdlFruitoreFromBytes();
					}
					else {
						wsdlWrapperUtilities.getAccordoServizioWrapper().setBytesWsdlImplementativoErogatore(asps.getByteWsdlImplementativoErogatore());
						wsdlDefinition = wsdlWrapperUtilities.buildWsdlErogatoreFromBytes();
					}
					
					if(asps.getPortType()!=null && requestInfo.getProtocolContext()!=null) {
						
						UrlInvocazioneAPI urlInvocazioneApi = ConfigurazionePdDManager.getInstance().getConfigurazioneUrlInvocazione(requestInfo.getProtocolFactory(), 
								 IDService.PORTA_APPLICATIVA.equals(idService) ? RuoloContesto.PORTA_APPLICATIVA : RuoloContesto.PORTA_DELEGATA,
								 requestInfo.getIntegrationServiceBinding(),
								 requestInfo.getProtocolContext().getInterfaceName(),
								 requestInfo.getIdentitaPdD(),
								 aspc, 
								 requestInfo);		 
						String prefixGatewayUrl = urlInvocazioneApi.getBaseUrl();
						String contesto = urlInvocazioneApi.getContext();
						prefixGatewayUrl = Utilities.buildUrl(prefixGatewayUrl, contesto);
						DefinitionWrapper wrapper = new DefinitionWrapper(wsdlDefinition, xmlUtils);
						PortType ptWSDL = wrapper.getPortType(asps.getPortType());
						if(ptWSDL!=null && ptWSDL.getQName()!=null && ptWSDL.getQName().getLocalPart()!=null) {
							Binding bindingWSDL = wrapper.getBindingByPortType(ptWSDL.getQName().getLocalPart());
							if(bindingWSDL!=null && bindingWSDL.getQName()!=null && bindingWSDL.getQName().getLocalPart()!=null) {
								Port portWSDL = wrapper.getServicePortByBindingName(bindingWSDL.getQName().getLocalPart());
								if(portWSDL!=null) {
									wrapper.updateLocation(portWSDL, prefixGatewayUrl);
								}
							}
						}
					}
					
					
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					wsdlUtilities.writeWsdlTo(wsdlDefinition, bout);
					bout.flush();
					bout.close();
					wsdl = bout.toByteArray();
				}
			}
			
			if(wsdl!=null) {
				HttpUtilities.setOutputFile(new ConnectorHttpServletResponse(response), true, "interface.wsdl");	
				response.setStatus(200);
				response.sendResponse(DumpByteArrayOutputStream.newInstance(wsdl));
			}
			else {
				response.setStatus(404);
				response.sendResponse(DumpByteArrayOutputStream.newInstance(ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeWsdlNotDefined(idService)).getBytes()));
			}
			
		}catch(Exception e){
			logCore.error("Lettura wsdl fallita: "+e.getMessage(),e);
			throw new ConnectorException("Lettura wsdl fallita: "+e.getMessage(),e);
		}
	}
}

class ConnectorHttpServletResponse extends WrappedHttpServletResponse {

	@Override
	public void setContentType(String type) {
		try {
			this.outMessage.setContentType(type);
		}catch(Exception e) {
			new RuntimeException(e.getMessage(),e);
		}
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		try {
			this.outMessage.setHeader(arg0, arg1+"");
		}catch(Exception e) {
			new RuntimeException(e.getMessage(),e);
		}
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		try {
			this.outMessage.setHeader(arg0, arg1);
		}catch(Exception e) {
			new RuntimeException(e.getMessage(),e);
		}
	}
	
	@Override
	public void addHeader(String arg0, String arg1) {
		try {
			this.outMessage.addHeader(arg0, arg1);
		}catch(Exception e) {
			new RuntimeException(e.getMessage(),e);
		}
	}

	private ConnectorOutMessage outMessage;
	
	public ConnectorHttpServletResponse(ConnectorOutMessage outMessage) {
		super(null);
		this.outMessage = outMessage;
	}
	
	

}
