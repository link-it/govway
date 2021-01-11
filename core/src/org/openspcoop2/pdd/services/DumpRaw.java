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

package org.openspcoop2.pdd.services;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.DumpRawConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.DumpRawConnectorOutMessage;
import org.openspcoop2.pdd.services.core.AbstractContext;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.Credential;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**
 * DumpRaw
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpRaw {

	private StringBuilder bfContext = new StringBuilder();
	private StringBuilder bfRequest = new StringBuilder();
	private StringBuilder bfResponse = new StringBuilder();
	private Logger log;
	private boolean pd;
	private String idTransaction;
	private Logger logDump;
	private Dump dump;
	private boolean onlyLogFileTrace = false;
	private URLProtocolContext urlProtocolContext;
	
	private IDSoggetto dominio;
	private String modulo;
	private TipoPdD tipoPdD;
	
	public DumpRaw(Logger log,IDSoggetto dominio,String modulo,TipoPdD tipoPdD, boolean onlyLogFileTrace) throws ConnectorException{
		this.log = log;
		
		switch (tipoPdD) {
		case DELEGATA:
			this.pd = true;
			break;
		default:
			this.pd = false;
			break;
		}
		
		this.onlyLogFileTrace = onlyLogFileTrace;
		if(!this.onlyLogFileTrace) {
			if(this.pd){
				this.logDump = OpenSPCoop2Logger.getLoggerOpenSPCoopDumpBinarioPD();
			}
			else{
				this.logDump = OpenSPCoop2Logger.getLoggerOpenSPCoopDumpBinarioPA();
			}
			if(this.logDump==null){
				throw new ConnectorException("Logger per la registrazione dei dati binari non inizializzato");
			}
		}
		
		this.dominio = dominio;
		this.modulo = modulo;
		this.tipoPdD = tipoPdD;
	}
	
	public void setPddContext(String interfaceName, PdDContext pddContext) throws DumpException {
		if(OpenSPCoop2Properties.getInstance().isDumpBinario_registrazioneDatabase() || this.onlyLogFileTrace) {
			this.dump = new Dump(this.dominio, this.modulo, this.tipoPdD, interfaceName, pddContext);
		}	
	}
		
	public void serializeContext(AbstractContext context,String protocol){
		try{
			Date dataAccettazioneRichiesta = context.getDataAccettazioneRichiesta();
			Date dataIngressoRichiesta = context.getDataIngressoRichiesta();
			String idTransazione = null;
			IDService serviceType = context.getIdModuloAsIDService();
			TipoPdD tipoPdD = context.getTipoPorta();
			if(context.getPddContext()!=null){
				Object tmp = context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
				if(tmp!=null){
					idTransazione = (String) tmp;
				}
			}
			this.serializeContext(dataAccettazioneRichiesta, dataIngressoRichiesta, idTransazione, serviceType, tipoPdD, protocol);
			
		}catch(Throwable t){
			this.bfContext.append("SerializeContext (AbstractContext) error: "+t.getMessage()+"\n");
			this.log.error("SerializeContext (AbstractContext) error: "+t.getMessage(),t);
		}
	}
	public void serializeContext(Date dataAccettazioneRichiesta,Date dataIngressoRichiesta, String idTransazione, IDService serviceType, TipoPdD tipoPdD,String protocol){
		
		if(this.onlyLogFileTrace) {
			return;
		}
		
		this.bfContext.append("------ RequestContext ("+idTransazione+") ------\n");
		this.idTransaction = idTransazione;
		
		try{
			if(dataAccettazioneRichiesta!=null){
				SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
				this.bfContext.append("Date (Accept Request): ");
				this.bfContext.append(dateformat.format(dataAccettazioneRichiesta));
				this.bfContext.append("\n");
			}
			if(dataIngressoRichiesta!=null){
				SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
				this.bfContext.append("Date (Received Request): ");
				this.bfContext.append(dateformat.format(dataIngressoRichiesta));
				this.bfContext.append("\n");
			}
			if(idTransazione!=null){
				this.bfContext.append("IdTransaction: ");
				this.bfContext.append(idTransazione);
				this.bfContext.append("\n");
			}
			if(protocol!=null){
				this.bfContext.append("Protocol: ");
				this.bfContext.append(protocol);
				this.bfContext.append("\n");
			}
			if(serviceType!=null){
				this.bfContext.append("Service: ");
				this.bfContext.append(serviceType.getValue());
				this.bfContext.append("\n");
			}
			if(tipoPdD!=null){
				this.bfContext.append("PddType: ");
				this.bfContext.append(tipoPdD.getTipo());
				this.bfContext.append("\n");
			}
		}catch(Throwable t){
			this.bfContext.append("SerializeContext error: "+t.getMessage()+"\n");
			this.log.error("SerializeContext error: "+t.getMessage(),t);
		}
		
		this.bfContext.append("------ End-RequestContext ("+this.idTransaction+") ------\n\n");
		
		this.logDump.info(this.bfContext.toString());
		
	}
	
	public void serializeRequest(DumpRawConnectorInMessage req, boolean buildOpenSPCoopMessage, NotifierInputStreamParams notifierInputStreamParams) {
		
		String contentType = null;
		try{
			contentType = req.getContentType();
		}catch(Throwable t){
			this.bfRequest.append("Request.getContentType error: "+t.getMessage()+"\n");
			this.log.error("Request.getContentType error: "+t.getMessage(),t);
		}
		
		Integer contentLength =null;
		try{
			contentLength = req.getContentLength();
		}catch(Throwable t){
			this.bfRequest.append("Request.getContentLength error: "+t.getMessage()+"\n");
			this.log.error("Request.getContentLength error: "+t.getMessage(),t);
		}
		
		Credential credential = null;
		try{
			credential = req.getCredential();
		}catch(Throwable t){
			this.bfRequest.append("Request.getIdentity error: "+t.getMessage()+"\n");
			this.log.error("Request.getIdentity error: "+t.getMessage(),t);
		}
		
		this.urlProtocolContext = null;
		try{
			this.urlProtocolContext = req.getURLProtocolContext();
		}catch(Throwable t){
			this.bfRequest.append("Request.getURLProtocolContext error: "+t.getMessage()+"\n");
			this.log.error("Request.getURLProtocolContext error: "+t.getMessage(),t);
		}
		
		try{
			// forzo la scrittura nel buffer dell'oggetto DumpRawConnectorInMessage
			if(buildOpenSPCoopMessage){
				req.getRequest(notifierInputStreamParams);
			}
			else{
				req.getRequest();
			}
		}catch(Throwable t){
			this.bfRequest.append("Request.getURLProtocolContext error: "+t.getMessage()+"\n");
			this.log.error("Request.getURLProtocolContext error: "+t.getMessage(),t);
		}
		
		if(!this.onlyLogFileTrace) {
			this.serializeRequest(contentType, contentLength, credential, this.urlProtocolContext, req.getRequestAsString(),
					req.getParsingRequestErrorAsString());
		}
		
		if(this.dump!=null) {
			byte [] rawMessage = req.getRequestAsByte();
			//if(rawMessage!=null){ // devono essere registrati anche solamente gli header
			try {
				this.dump.dumpBinarioRichiestaIngresso(this.onlyLogFileTrace, rawMessage, this.urlProtocolContext);
			}catch(Throwable t){
				this.log.error("Log DumpBinarioRichiestaIngresso error: "+t.getMessage(),t);
			}
			//}
		}
	}
	
	public void serializeRequest(String contentType, Integer contentLength, Credential credential, URLProtocolContext urlProtocolContext, String rawMessage,
			String parsingError) {
		

		this.bfRequest.append("------ Request ("+this.idTransaction+") ------\n");
		
		try{
			if(contentType!=null){
				this.bfRequest.append("ContentType: ");
				this.bfRequest.append(contentType);
				this.bfRequest.append("\n");
			}
		}catch(Throwable t){
			this.bfRequest.append("Request.getContentType error: "+t.getMessage());
			this.log.error("Request.getContentType error: "+t.getMessage(),t);
		}
		
		try{
			if(contentLength!=null && contentLength>=0){
				this.bfRequest.append("ContentLength: ");
				this.bfRequest.append(contentLength);
				this.bfRequest.append("\n");
			}
		}catch(Throwable t){
			this.bfRequest.append("Request.getContentLength error: "+t.getMessage()+"\n");
			this.log.error("Request.getContentLength error: "+t.getMessage(),t);
		}
		
		try{
			if(credential!=null){
			
				String principal = credential.getPrincipal();
				if(principal!=null){
					this.bfRequest.append("Principal: ");
					this.bfRequest.append(principal);
					this.bfRequest.append("\n");
				}
				
				String username = credential.getUsername();
				if(username!=null){
					this.bfRequest.append("Username: ");
					this.bfRequest.append(username);
					this.bfRequest.append("\n");
				}
				
				String password = credential.getPassword();
				if(password!=null){
					this.bfRequest.append("Password: ");
					this.bfRequest.append(password);
					this.bfRequest.append("\n");
				}
				
				String subjectX509 = credential.getSubject();
				if(subjectX509!=null){
					this.bfRequest.append("X509.Subject: ");
					this.bfRequest.append(subjectX509);
					this.bfRequest.append("\n");
				}
				
				if(credential.getCertificate()!=null){
					List<X509Certificate> certificates = new ArrayList<>();
					if(credential.getCertificate().getCertificate()!=null && credential.getCertificate().getCertificate().getCertificate()!=null) {
						certificates.add(credential.getCertificate().getCertificate().getCertificate());
					}
					if(credential.getCertificate().getCertificateChain()!=null && credential.getCertificate().getCertificateChain().size()>0) {
						for (CertificateInfo cInfo : credential.getCertificate().getCertificateChain()) {
							if(cInfo.getCertificate()!=null) {
								certificates.add(cInfo.getCertificate());
							}
						}
					}
					if(!certificates.isEmpty()) {
						CertificateUtils.printCertificate(this.bfRequest, certificates);
					}
				}
			}
			
		}catch(Throwable t){
			this.bfRequest.append("Request.getIdentity error: "+t.getMessage()+"\n");
			this.log.error("Request.getIdentity error: "+t.getMessage(),t);
		}
		
		try{
			if(urlProtocolContext!=null){
			
				String urlInvocazione = urlProtocolContext.getUrlInvocazione_formBased();
				if(urlInvocazione!=null){
					this.bfRequest.append("URLInvocazione: ");
					this.bfRequest.append(urlInvocazione);
					this.bfRequest.append("\n");
				}
				
				String function = urlProtocolContext.getFunction();
				if(function!=null){
					this.bfRequest.append("Function: ");
					this.bfRequest.append(function);
					this.bfRequest.append("\n");
				}
				
//				String webContext = urlProtocolContext.getWebContext();
//				if(webContext!=null){
//					this.bfRequest.append("WEBContext: ");
//					this.bfRequest.append(function);
//					this.bfRequest.append("\n");
//				}
				
				Map<String, String> transportHeader = urlProtocolContext.getParametersTrasporto();
				if(transportHeader!=null && transportHeader.size()>0){
					this.bfRequest.append("TransportHeaders: "+transportHeader.size()+"\n");
					Iterator<String> keys = transportHeader.keySet().iterator();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						Object value = transportHeader.get(key);
						if(value instanceof String){
							this.bfRequest.append("\t"+key+"="+value+"\n");
						}
						else{
							this.bfRequest.append("\t"+key+"=ObjectType("+value.getClass().getName()+")\n");
						}
					}
				}
				else{
					this.bfRequest.append("TransportHeaders: "+0+"\n");
				}
				
				Map<String, String> parameterUrl = urlProtocolContext.getParametersFormBased();
				if(parameterUrl!=null && parameterUrl.size()>0){
					this.bfRequest.append("URLParameters: "+parameterUrl.size()+"\n");
					Iterator<String> keys = parameterUrl.keySet().iterator();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						Object value = parameterUrl.get(key);
						if(value instanceof String){
							this.bfRequest.append("\t"+key+"="+value+"\n");
						}
						else{
							this.bfRequest.append("\t"+key+"=ObjectType("+value.getClass().getName()+")\n");
						}
					}
				}
				else{
					this.bfRequest.append("URLParameters: "+0+"\n");
				}
				
			}
			
		}catch(Throwable t){
			this.bfRequest.append("Request.getURLProtocolContext error: "+t.getMessage()+"\n");
			this.log.error("Request.getURLProtocolContext error: "+t.getMessage(),t);
		}
				
		if(rawMessage!=null){
			this.bfRequest.append("Binary Request Length: ");
			this.bfRequest.append(rawMessage.length());
			this.bfRequest.append("\n");
			
			this.bfRequest.append("Binary Request: \n");
			this.bfRequest.append(rawMessage);
			this.bfRequest.append("\n");
			
		}
		else if(parsingError!=null){
			this.bfRequest.append("Parsing Request Exception: \n");
			this.bfRequest.append(parsingError);
			this.bfRequest.append("\n");
		}
		else{
			this.bfRequest.append("Binary Request Length: ");
			this.bfRequest.append(0);
			this.bfRequest.append("\n");
			
			this.bfRequest.append("Binary Request: \n");
			this.bfRequest.append("Nessuna contenuto presente nel payload");
			this.bfRequest.append("\n");
		}
		
		this.bfRequest.append("------ End-Request ("+this.idTransaction+") ------\n\n");
		
		this.logDump.info(this.bfRequest.toString());
				
	}
	
	public void serializeResponse(DumpRawConnectorOutMessage res) {
		
		if(!this.onlyLogFileTrace) {
			this.serializeResponse(res.getResponseAsString(),res.getParsingResponseErrorAsString(),
					res.getTrasporto(),res.getContentLenght(),res.getContentType(),res.getStatus());
		}
		
		if(this.dump!=null) {
			byte [] rawMessage = res.getResponseAsByte();
			//if(rawMessage!=null){ // devono essere registrati anche solamente gli header
			try {
				if(res.getContentType()!=null) {
					res.getTrasporto().put(HttpConstants.CONTENT_TYPE,res.getContentType());
				}
				this.dump.dumpBinarioRispostaUscita(this.onlyLogFileTrace, rawMessage, this.urlProtocolContext, res.getTrasporto());
			}catch(Throwable t){
				this.log.error("Log DumpBinarioRichiestaIngresso error: "+t.getMessage(),t);
			}
			//}
		}
	}
	
	public void serializeResponse(String rawMessage,String parsingError,Map<String, String> transportHeader,Integer contentLength, String contentType, Integer status) {
		
		this.bfResponse.append("------ Response ("+this.idTransaction+") ------\n");
		
		try{
			if(contentType!=null){
				this.bfResponse.append("ContentType: ");
				this.bfResponse.append(contentType);
				this.bfResponse.append("\n");
			}
		}catch(Throwable t){
			this.bfResponse.append("Response.contentType error: "+t.getMessage());
			this.log.error("Response.contentType error: "+t.getMessage(),t);
		}
		
		try{
			if(contentLength!=null && contentLength>=0){
				this.bfResponse.append("ContentLength: ");
				this.bfResponse.append(contentLength);
				this.bfResponse.append("\n");
			}
		}catch(Throwable t){
			this.bfResponse.append("Response.contentLength error: "+t.getMessage()+"\n");
			this.log.error("Response.contentLength error: "+t.getMessage(),t);
		}
		
		try{
			if(status!=null){
				this.bfResponse.append("Status: ");
				this.bfResponse.append(status);
				this.bfResponse.append("\n");
			}
		}catch(Throwable t){
			this.bfResponse.append("Response.status error: "+t.getMessage()+"\n");
			this.log.error("Response.status error: "+t.getMessage(),t);
		}
		
		if(transportHeader!=null && transportHeader.size()>0){
			this.bfResponse.append("TransportHeaders: "+transportHeader.size()+"\n");
			Iterator<String> keys = transportHeader.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				Object value = transportHeader.get(key);
				if(value instanceof String){
					this.bfResponse.append("\t"+key+"="+value+"\n");
				}
				else{
					this.bfResponse.append("\t"+key+"=ObjectType("+value.getClass().getName()+")\n");
				}
			}
		}
		else{
			this.bfResponse.append("TransportHeaders: "+0+"\n");
		}
		
		if(rawMessage!=null){
			this.bfResponse.append("Binary Response Length: ");
			this.bfResponse.append(rawMessage.length());
			this.bfResponse.append("\n");
			
			this.bfResponse.append("Binary Response: \n");
			this.bfResponse.append(rawMessage);
			this.bfResponse.append("\n");
			
		}
		else if(parsingError!=null){
			this.bfRequest.append("Parsing Response Exception: \n");
			this.bfRequest.append(parsingError);
			this.bfRequest.append("\n");
		}
		else{
			this.bfResponse.append("Binary Response Length: ");
			this.bfResponse.append(0);
			this.bfResponse.append("\n");
			
			this.bfResponse.append("Binary Response: \n");
			this.bfResponse.append("Nessuna contenuto presente nel payload");
			this.bfResponse.append("\n");
		}
		
		this.bfResponse.append("------ End-Response ("+this.idTransaction+") ------\n\n");
		
		this.logDump.info(this.bfResponse.toString());
	}
	
}
