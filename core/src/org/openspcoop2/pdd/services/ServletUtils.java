/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.pdd.services;

import java.util.Enumeration;
import java.util.List;

import javax.mail.internet.ContentType;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.io.output.NullOutputStream;
import org.slf4j.Logger;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageException;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.services.connector.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.ConnectorOutMessage;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.InformazioniErroriInfrastrutturali;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.utils.Identity;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;


/**
 * Libreria utilizzata dalle servlet contenente metodi che implementano 
 * le funzioni precedentemente implementate negli Handler.
 * 
 *
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServletUtils {

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
	

	public static boolean isContentTypeSupported(SOAPVersion soapVersion, IProtocolFactory protocolFactory) throws ProtocolException {
		if(soapVersion==null)
			return false;
		if (soapVersion.equals(SOAPVersion.SOAP11) && protocolFactory.createProtocolConfiguration().isSupportoSOAP11()) return true;
		if (soapVersion.equals(SOAPVersion.SOAP12) && protocolFactory.createProtocolConfiguration().isSupportoSOAP12()) return true;
		return false;
	}
	
	public static SOAPVersion getVersioneSoap(Logger log, String cType) {
		return SOAPVersion.getVersioneSoap(log, cType, true);
	}

	
	public static String readContentTypeFromHeader(HttpServletRequest req) throws Exception{
		return readContentTypeFromHeader(req, true);
	}
	public static String readContentTypeFromHeader(HttpServletRequest req, boolean returnMsgErroreIfNotFound) throws Exception{

		java.util.Enumeration<?> enTrasporto = req.getHeaderNames();
		while(enTrasporto.hasMoreElements()){
			String nomeProperty = (String)enTrasporto.nextElement();
			if(CostantiPdD.CONTENT_TYPE.equalsIgnoreCase(nomeProperty)){
				//System.out.println("TROVATO CONTENT_TYPE: "+req.getHeader(nomeProperty));
				String ct = req.getHeader(nomeProperty);
				if(ct==null){
					if(returnMsgErroreIfNotFound)
						return CostantiPdD.CONTENT_TYPE_NON_VALORIZZATO;
					else
						return null;
				}
				return ct;
			}
		}

		if(returnMsgErroreIfNotFound)
			return CostantiPdD.CONTENT_TYPE_NON_PRESENTE;
		else
			return null;
	}
	
	/**
	 * Metodo che si occupa della raccolta delle credenziali
	 *
	 * @param req HttpServletRequest
	 * 
	 * @return Credenziali 
	 * 
	 */
	public static Credenziali getCredenziali(HttpServletRequest req,Logger log) throws Exception {

		OpenSPCoop2Properties prop = OpenSPCoop2Properties.getInstance();
		boolean printCert = false;
		if(prop!=null && prop.isPrintInfoCertificate()){
			printCert = true;
		}
		
		Identity identity = null;
		if(printCert){
			identity = new Identity(req,log);
		}else{
			identity = new Identity(req);
		}
		
		Credenziali credenziali = new Credenziali();

		// Basic (HTTP-Based)
		credenziali.setUsername(identity.getUsername());
		credenziali.setPassword(identity.getPassword());
		
		// SSL (HTTPS)
		credenziali.setSubject(identity.getSubject());	
		credenziali.setCertificati(identity.getCerts());

		return credenziali;

	}


	/**
	 * Metodo che si occupa di costruire la location del mittente
	 *
	 * @param req HttpServletRequest
	 * @param credenziali Credenziali
	 * @return location String 
	 * 
	 */

	public static String getLocation(HttpServletRequest req, Credenziali credenziali){
		String protocollo = "http";
		if(credenziali.getSubject()!=null)
			protocollo = "https";
		String ip = req.getRemoteAddr();
		String port = ""+req.getRemotePort();
		String user = "";
		if(credenziali.getUsername()!=null || credenziali.getSubject()!=null){
			user = CostantiPdD.TRACCIAMENTO_SEPARATOR_LOCATION;
			if(credenziali.getSubject()!=null)
				user=user+credenziali.getSubject();
			else
				user=user+credenziali.getUsername();
		}
		return protocollo+CostantiPdD.TRACCIAMENTO_SEPARATOR_LOCATION+
		ip+CostantiPdD.TRACCIAMENTO_SEPARATOR_LOCATION+port+user;
	}


	public static String getSoapAction(HttpServletRequest req, SOAPVersion soapVersion, String contentType) throws Exception{

		if(SOAPVersion.SOAP11.equals(soapVersion)){
		
			java.util.Enumeration<?> enTrasporto = req.getHeaderNames();
			while(enTrasporto.hasMoreElements()){
				String nomeProperty = (String)enTrasporto.nextElement();
				if(SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION.equalsIgnoreCase(nomeProperty)){
					//System.out.println("TROVATO SOAP ACTION: "+req.getHeader(nomeProperty));
					String soapAction = req.getHeader(nomeProperty);
					if(soapAction==null){
						throw new Exception("Header http '"+SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non valorizzato (null)");
					}
					soapAction = soapAction.trim();
					if(soapAction.startsWith("\"")){
						if(!soapAction.endsWith("\"")){
							throw new Exception("Header http '"+SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non valorizzato correttamente (action quotata? Non è stato trovato il carattere di chiusura \" ma è presente quello di apertura)");
						}	
					}
					if(soapAction.endsWith("\"")){
						if(!soapAction.startsWith("\"")){
							throw new Exception("Header http '"+SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non valorizzato correttamente (action quotata? Non è stato trovato il carattere di apertura \" ma è presente quello di chiusura)");
						}	
					}
					return soapAction;
				}
			}
	
			throw new Exception("Header http '"+SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' non presente");
			
		}
		else{
			// The SOAP 1.1 mandatory SOAPAction HTTP header has been removed in SOAP 1.2. In its place is an optional action parameter on the application/soap+xml media type.
			ContentType ct = new ContentType(contentType);
			if(ct.getParameterList()!=null && ct.getParameterList().size()>0){
				Enumeration<?> names = ct.getParameterList().getNames();
				while (names.hasMoreElements()) {
					String name = (String) names.nextElement();
					if(SOAPVersion.SOAP12_OPTIONAL_CONTENT_TYPE_PARAMETER_SOAP_ACTION.equals(name)){
						return ct.getParameterList().get(name);
					}	
				}
			}
			
			return null;
		}
		
	}

	public static void checkSoapActionQuotedString(String soapAction,SOAPVersion soapVersion) throws Exception{
		if(SOAPVersion.SOAP11.equals(soapVersion)){
			// WS-I BasicProfile 1.1: R1109   The value of the SOAPAction HTTP header field in a HTTP request MESSAGE MUST be a quoted string.
			if(!soapAction.startsWith("\"") || !soapAction.endsWith("\"")){
				throw new Exception("Header http '"+SOAPVersion.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+"' valorizzato tramite una stringa non quotata (WSI-BP-1.1 R1109)");
			}
		}
	}

	public static URLProtocolContext getParametriInvocazionePorta(HttpServletRequest req,Logger logCore) throws ProtocolException, UtilsException{

		return new URLProtocolContext(req, logCore);
		
	}

		/* ------------  Parametri della porta delegata invocata ------------- */

	public static String checkMustUnderstand(SOAPMessage message,IProtocolFactory protocolFactory) throws OpenSPCoop2MessageException{
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
						return ServletUtils.checkMustUnderstandHeaderElement(header,filtri);
					}
				}
			}

		} catch (Exception ex) {
			if(Utilities.existsInnerException(ex, "com.ctc.wstx.exc.WstxUnexpectedCharException") || Utilities.existsInnerException(ex, "com.ctc.wstx.exc.WstxParsingException"))
				throw new OpenSPCoop2MessageException(ex);
			else
				throw new OpenSPCoop2MessageException("BypassMustUnderstand, errore durante il set processed degli header con mustUnderstand='1' e actor non presente: ", 
					ex);
		}  finally{
			// *** GB ***
			header = null;
			envelope = null;
			// *** GB ***
		}     	
		return null;
	}
	
	public static String checkSOAPEnvelopeNamespace(SOAPMessage message, SOAPVersion versioneSoap) throws OpenSPCoop2MessageException{
		SOAPEnvelope envelope = null;
		try {
			envelope = message.getSOAPPart().getEnvelope();
			if(versioneSoap.equals(SOAPVersion.SOAP11) &&  Costanti.SOAP_ENVELOPE_NAMESPACE.equals(envelope.getNamespaceURI())) {
				return null;
			}
			
			if(versioneSoap.equals(SOAPVersion.SOAP12) &&  Costanti.SOAP12_ENVELOPE_NAMESPACE.equals(envelope.getNamespaceURI())) {
				return null;
			}
			
			return envelope.getNamespaceURI();

		} catch (Exception ex) {
			throw new OpenSPCoop2MessageException("CheckSoapEnvelopeNamespace, errore durante il controllo del namespace del soap envelope: ", 
					ex);
		}  finally{
			// *** GB ***
			envelope = null;
			// *** GB ***
		}     	
	}

	public static String checkMustUnderstandHeaderElement(SOAPHeader header,List<NameValue> filtri)throws UtilsException{
		
		
		String error = null;
		try{
			java.util.Iterator<?> headers = header.getChildElements();
			while (headers.hasNext()) {
				Object element = headers.next();
				if(!(element instanceof SOAPHeaderElement)){
					continue;
				}
				
				SOAPHeaderElement elementHeader = (SOAPHeaderElement) element;
				
				// Prendo gli headers con MustUnderstand="1" senza Actor
				if(elementHeader.getActor()==null&&elementHeader.getMustUnderstand()==true){
					boolean checked = false;
					if(filtri!=null && filtri.size()>0){
						for (NameValue nameValue : filtri) {
							String localName = nameValue.getName();
							String namespaceURI = nameValue.getValue();
							if(localName.equals(elementHeader.getLocalName()) && namespaceURI.equals(elementHeader.getNamespaceURI())){
								// Ok si bypassa.
								checked = true;
								break;
							}
						}
					}
					
					// Controllo se abbiamo bypassato l'header
					if(!checked){
						// Abbiamo un MustUnderstand="1" senza Actor che non appare nella lista da Bypassare!

						if(error == null)
							error = "{"+elementHeader.getNamespaceURI()+"}"+elementHeader.getLocalName();
						else
							error += ", " + "{"+elementHeader.getNamespaceURI()+"}"+elementHeader.getLocalName();
					}
				}
			}
		}catch(java.lang.Exception e) {
			if(Utilities.existsInnerException(e, "com.ctc.wstx.exc.WstxUnexpectedCharException"))
				throw new UtilsException(e);
			else
				throw new UtilsException("Utilities.bypassMustUnderstandHeaderElement Riscontrato errore durante l'applicazione del bypassFilter: "+e.getMessage(),e);
		}  
		return error;
	}

	
	public static boolean verificaRispostaRelazioneCodiceTrasporto202(IProtocolFactory protocolFactory,OpenSPCoop2Properties openSPCoopProperties,
			OpenSPCoop2Message responseMessage,boolean gestioneLatoPortaDelegata) throws Exception{
		
		if(responseMessage==null){
			return false;
		}
		
		IProtocolManager protocolManager = protocolFactory.createProtocolManager(); 
		
		boolean rispostaPresente = true;
		if(protocolManager.isHttpEmptyResponseOneWay()){
			Object b = responseMessage.getSOAPBody();
			SOAPBody body = null;
			if(b != null){
				body = (SOAPBody) b;
			}
			Object h = null;
			SOAPHeader header = null;
			if(b==null || body==null || body.getFirstChild()==null ){
				//potenziale msg inutile.
				h = responseMessage.getSOAPHeader();
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
		
		return rispostaPresente;
	}
		
	
	
	public static void setTransferLength(TransferLengthModes transferLengthMode,
			ConnectorInMessage connectorInMessage, ConnectorOutMessage connectorOutMessage,
			OpenSPCoop2Message message) throws Exception{
		String requestProtocoll = connectorInMessage.getProtocol();
		if(requestProtocoll!=null && requestProtocoll.endsWith("1.1")){
			if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(transferLengthMode)){
				connectorOutMessage.setHeader(Costanti.TRANSFER_ENCODING,Costanti.TRANSFER_ENCODING_CHUNCKED_VALUE);
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
				connectorOutMessage.setHeader(Costanti.TRANSFER_ENCODING,Costanti.TRANSFER_ENCODING_CHUNCKED_VALUE);
			}
			else if(TransferLengthModes.CONTENT_LENGTH.equals(transferLengthMode)){
				if(length!=null){
					connectorOutMessage.setContentLength(length.intValue());
				}
			}
		}
	}
	
}
