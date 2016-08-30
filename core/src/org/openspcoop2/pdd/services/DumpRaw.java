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

package org.openspcoop2.pdd.services;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.utils.Identity;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

/**
 * DumpRaw
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpRaw {

	private static final String format = "yyyy-MM-dd_HH:mm:ss.SSS";
	
	private StringBuffer bfContext = new StringBuffer();
	private StringBuffer bfRequest = new StringBuffer();
	private StringBuffer bfResponse = new StringBuffer();
	private Logger log;
	private boolean pd;
	private String idTransaction;
	private Logger logDump;
	
	public DumpRaw(Logger log,boolean isPD) throws ConnectorException{
		this.log = log;
		this.pd = isPD;
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
		
	public void serializeContext(AbstractContext context,String protocol){
		try{
			Date dataIngresso = null;
			String idTransazione = null;
			IDService serviceType = context.getIdModuloAsIDService();
			TipoPdD tipoPdD = context.getTipoPorta();
			if(context.getDataIngressoRichiesta()!=null){
				dataIngresso = context.getDataIngressoRichiesta();
			}
			if(context.getPddContext()!=null){
				Object tmp = context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
				if(tmp!=null){
					idTransazione = (String) tmp;
				}
			}
			this.serializeContext(dataIngresso, idTransazione, serviceType, tipoPdD, protocol);
			
		}catch(Throwable t){
			this.bfContext.append("SerializeContext (AbstractContext) error: "+t.getMessage()+"\n");
			this.log.error("SerializeContext (AbstractContext) error: "+t.getMessage(),t);
		}
	}
	public void serializeContext(Date dataIngresso, String idTransazione, IDService serviceType, TipoPdD tipoPdD,String protocol){
		
		this.bfContext.append("------ RequestContext ("+idTransazione+") ------\n");
		this.idTransaction = idTransazione;
		
		try{
			if(dataIngresso!=null){
				SimpleDateFormat dateformat = new SimpleDateFormat (format); // SimpleDateFormat non e' thread-safe
				this.bfContext.append("Date: ");
				this.bfContext.append(dateformat.format(dataIngresso));
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
		
		Identity identity = null;
		try{
			identity = req.getIdentity();
		}catch(Throwable t){
			this.bfRequest.append("Request.getIdentity error: "+t.getMessage()+"\n");
			this.log.error("Request.getIdentity error: "+t.getMessage(),t);
		}
		
		URLProtocolContext urlProtocolContext = null;
		try{
			urlProtocolContext = req.getURLProtocolContext();
		}catch(Throwable t){
			this.bfRequest.append("Request.getURLProtocolContext error: "+t.getMessage()+"\n");
			this.log.error("Request.getURLProtocolContext error: "+t.getMessage(),t);
		}
		
		try{
			// forzo la scrittura nel buffer dell'oggetto DumpRawConnectorInMessage
			if(buildOpenSPCoopMessage){
				req.getRequest(notifierInputStreamParams, contentType);
			}
			else{
				req.getRequest();
			}
		}catch(Throwable t){
			this.bfRequest.append("Request.getURLProtocolContext error: "+t.getMessage()+"\n");
			this.log.error("Request.getURLProtocolContext error: "+t.getMessage(),t);
		}
		
		this.serializeRequest(contentType, contentLength, identity, urlProtocolContext, req.getRequestAsString(),
				req.getParsingRequestErrorAsString());
	}
	
	public void serializeRequest(String contentType, Integer contentLength, Identity identity, URLProtocolContext urlProtocolContext, String rawMessage,
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
			if(identity!=null){
			
				String principal = identity.getPrincipal();
				if(principal!=null){
					this.bfRequest.append("Principal: ");
					this.bfRequest.append(principal);
					this.bfRequest.append("\n");
				}
				
				String username = identity.getUsername();
				if(username!=null){
					this.bfRequest.append("Username: ");
					this.bfRequest.append(username);
					this.bfRequest.append("\n");
				}
				
				String password = identity.getPassword();
				if(password!=null){
					this.bfRequest.append("Password: ");
					this.bfRequest.append(password);
					this.bfRequest.append("\n");
				}
				
				String subjectX509 = identity.getSubject();
				if(subjectX509!=null){
					this.bfRequest.append("X509.Subject: ");
					this.bfRequest.append(subjectX509);
					this.bfRequest.append("\n");
				}
				
				X509Certificate[] certificates = identity.getCerts();
				if(certificates!=null && certificates.length>0){
					Identity.printCertificate(this.bfRequest, certificates);
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
				
				Properties transportHeader = urlProtocolContext.getParametersTrasporto();
				if(transportHeader!=null && transportHeader.size()>0){
					this.bfRequest.append("TransportHeaders: "+transportHeader.size()+"\n");
					Enumeration<?> keys = transportHeader.keys();
					while(keys.hasMoreElements()){
						String key = (String) keys.nextElement();
						Object value = transportHeader.getProperty(key);
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
				
				Properties parameterUrl = urlProtocolContext.getParametersFormBased();
				if(parameterUrl!=null && parameterUrl.size()>0){
					this.bfRequest.append("URLParameters: "+parameterUrl.size()+"\n");
					Enumeration<?> keys = parameterUrl.keys();
					while(keys.hasMoreElements()){
						String key = (String) keys.nextElement();
						Object value = parameterUrl.getProperty(key);
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
		
		this.serializeResponse(res.getResponseAsString(),res.getParsingResponseErrorAsString(),
				res.getTrasporto(),res.getContentLenght(),res.getContentType(),res.getStatus());
		
	}
	
	public void serializeResponse(String rawMessage,String parsingError,Properties transportHeader,Integer contentLength, String contentType, Integer status) {
		
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
			Enumeration<?> keys = transportHeader.keys();
			while(keys.hasMoreElements()){
				String key = (String) keys.nextElement();
				Object value = transportHeader.getProperty(key);
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
