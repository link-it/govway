/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;

import org.apache.commons.io.output.NullOutputStream;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.pdd.config.CachedConfigIntegrationReader;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CORSFilter;
import org.openspcoop2.pdd.core.CORSWrappedHttpServletResponse;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazione;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.protocol.basic.registry.ServiceIdentificationReader;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.registry.CachedRegistryReader;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.InformazioniErroriInfrastrutturali;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.CORSRequestType;
import org.openspcoop2.utils.transport.http.HttpConstants;
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

	
	public static ServiceIdentificationReader getServiceIdentificationReader(Logger logCore, RequestInfo requestInfo) throws Exception{
		IRegistryReader registryReader = new CachedRegistryReader(logCore, requestInfo.getProtocolFactory());
		IConfigIntegrationReader configIntegrationReader = new CachedConfigIntegrationReader(logCore, requestInfo.getProtocolFactory());
		return new ServiceIdentificationReader(registryReader, configIntegrationReader, requestInfo.getProtocolFactory(), logCore);
	}
	
	
	public static boolean isConnessioneClientNonDisponibile(Throwable t){
		if(t instanceof java.net.SocketException){
			return true;
		}
		else if(Utilities.existsInnerException(t, java.net.SocketException.class)){
			return true;
		}
		
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
			envelope = message.getSOAPPart().getEnvelope();
			header = envelope.getHeader();
			
			//Se non c'e' l'header il controllo e' inutile
			if(header == null) return null;
			
			OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();

			if(openspcoopProperties!=null){
				if(openspcoopProperties.isBypassFilterMustUnderstandEnabledForAllHeaders()){
					return null;
				}else{
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
			StringBuffer bfError = new StringBuffer();
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
				return message.getSOAPPart().getEnvelope().getNamespaceURI();
			}
			return null;
		} catch (Exception ex) {
			throw new MessageException("CheckSoapEnvelopeNamespace, errore durante il controllo del namespace del soap envelope: "+ex.getMessage(),ex);
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
				Object b = soap.getSOAPBody();
				SOAPBody body = null;
				if(b != null){
					body = (SOAPBody) b;
				}
				Object h = null;
				SOAPHeader header = null;
				if(b==null || body==null || body.getFirstChild()==null ){
					//potenziale msg inutile.
					h = soap.getSOAPHeader();
					if(h!=null){
						header = (SOAPHeader) h;
					}
					if(h==null || header==null || header.getFirstChild()==null ){
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
				}
				
				// *** GB ***
				body = null;
				b = null;
				header = null;
				h = null;
				// *** GB ***
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
					message.writeTo(new NullOutputStream(), false);
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
	
	
	public static void setGovWayHeaderResponse(Properties propertiesTrasporto, Logger logCore, boolean portaDelegata, PdDContext pddContext, URLProtocolContext protocolContext) {
		try {
			UtilitiesIntegrazione utilitiesIntegrazione = null;
			if(portaDelegata) {
				utilitiesIntegrazione = UtilitiesIntegrazione.getInstancePDResponse(logCore);
			}
			else {
				utilitiesIntegrazione = UtilitiesIntegrazione.getInstancePAResponse(logCore);
			}
			
			String idTransazione = propertiesTrasporto.getProperty(Costanti.ID_TRANSAZIONE);
			if(idTransazione==null) {
				idTransazione = propertiesTrasporto.getProperty(Costanti.ID_TRANSAZIONE.toLowerCase());
			}
			if(idTransazione==null) {
				idTransazione = propertiesTrasporto.getProperty(Costanti.ID_TRANSAZIONE.toUpperCase());
			}
			
			if(idTransazione==null) {
				idTransazione = (String) pddContext.getObject(Costanti.ID_TRANSAZIONE);
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
		    		propertiesTrasporto.put(key, pHeaderRateLimiting.getProperty(key));
		    	}	
			}
		}
		setCORSAllowOrigin(propertiesTrasporto, logCore, portaDelegata, pddContext, protocolContext);
	}
	
	
	
	private static void setCORSAllowOrigin(Properties propertiesTrasporto, Logger logCore, boolean portaDelegata, PdDContext pddContext, URLProtocolContext protocolContext) {
		try {
			
			Object nomePortaObject = pddContext.getObject(CostantiPdD.NOME_PORTA_INVOCATA);
			String nomePorta = null;
			if(nomePortaObject!=null && nomePortaObject instanceof String) {
				nomePorta = (String) nomePortaObject;
			}
			
			CorsConfigurazione cors = null;
			HttpServletRequest httpServletRequest = null;
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
						PortaDelegata pdDefault = configurazionePdDManager.getPortaDelegata_SafeMethod(idPD);
						cors = configurazionePdDManager.getConfigurazioneCORS(pdDefault);
					}
					else {
						IDPortaApplicativa idPA = new IDPortaApplicativa();
						idPA.setNome(nomePorta);
						PortaApplicativa paDefault = configurazionePdDManager.getPortaApplicativa_SafeMethod(idPA);
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
					corsFilter.doCORS(httpServletRequest, res, CORSRequestType.ACTUAL);
					propertiesTrasporto.putAll(res.getHeader());
				}
			}
			
		}catch(Exception e){
			logCore.error("Set cors origin: "+e.getMessage(),e);
		}

	}
	
}
