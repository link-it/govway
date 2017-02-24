/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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



package org.openspcoop2.security.message.authorization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.apache.wss4j.common.saml.builder.SAML2Constants;
import org.apache.wss4j.dom.util.WSSecurityUtil;
import org.herasaf.xacml.core.SyntaxException;
import org.herasaf.xacml.core.WritingException;
import org.herasaf.xacml.core.context.RequestMarshaller;
import org.herasaf.xacml.core.context.ResponseMarshaller;
import org.herasaf.xacml.core.context.impl.ActionType;
import org.herasaf.xacml.core.context.impl.AttributeType;
import org.herasaf.xacml.core.context.impl.AttributeValueType;
import org.herasaf.xacml.core.context.impl.DecisionType;
import org.herasaf.xacml.core.context.impl.ObjectFactory;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.context.impl.ResourceType;
import org.herasaf.xacml.core.context.impl.ResponseType;
import org.herasaf.xacml.core.context.impl.ResultType;
import org.herasaf.xacml.core.context.impl.SubjectType;
import org.herasaf.xacml.core.dataTypeAttribute.impl.StringDataTypeAttribute;
import org.herasaf.xacml.core.policy.Evaluatable;
import org.herasaf.xacml.core.policy.PolicyMarshaller;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.xml.DynamicNamespaceContextFactory;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.saml.SAMLConstants;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.xacml.CachedMapBasedSimplePolicyRepository;
import org.openspcoop2.utils.xacml.PolicyDecisionPoint;
import org.openspcoop2.utils.xacml.PolicyException;
import org.openspcoop2.utils.xacml.ResultCombining;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.JaxbUtils;
import org.openspcoop2.utils.xml.XPathException;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathNotValidException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.slf4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Implementazione dell'interfaccia Authorization basata su token SAML
 *
 * @author Bussu Giovanni <bussu@link.it>
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 */

public class MessageSecurityAuthorizationSAMLPolicy  implements IMessageSecurityAuthorization{

	public static final String SAML_20_ISSUER = "urn:oasis:names:tc:SAML:2.0:assertion:Issuer";
	public static final String SAML_20_SUBJECT_NAME = "urn:oasis:names:tc:SAML:2.0:assertion:Subject:NameID";
	public static final String SAML_20_ATTRIBUTE_NAME = "urn:oasis:names:tc:SAML:2.0:assertion:AttributeStatement:Attribute:Name:";
	
	public static final String SAML_11_ISSUER = "urn:oasis:names:tc:SAML:2.0:assertion:Issuer";
	public static final String SAML_11_AUTHENTICATION_SUBJECT_NAME = "urn:oasis:names:tc:SAML:1.0:assertion:AuthenticationStatement:Subject:NameIdentifier";
	public static final String SAML_11_AUTHORIZATION_SUBJECT_NAME = "urn:oasis:names:tc:SAML:1.0:assertion:AttributeStatement:Subject:NameIdentifier";
	public static final String SAML_11_ATTRIBUTE_NAME = "urn:oasis:names:tc:SAML:1.0:assertion:AttributeStatement:Attribute:Name:";
	
	

	private static PolicyDecisionPoint pdp;
	private static synchronized void initPdD(Logger log) throws PolicyException{
    	if(MessageSecurityAuthorizationSAMLPolicy.pdp == null) {
    		MessageSecurityAuthorizationSAMLPolicy.pdp = new PolicyDecisionPoint(log);
    	}
	}
	
	
	private ObjectFactory factory;
	private AbstractXPathExpressionEngine xpathExpressionEngine;

    public MessageSecurityAuthorizationSAMLPolicy()  {
		this.factory = new ObjectFactory();
		PolicyDecisionPoint.runInitializers(); //necessario per far inizializzare gli unmarshaller in caso di pdp remoto
		this.xpathExpressionEngine = new XPathExpressionEngine();
    }


    @Override
    public MessageSecurityAuthorizationResult authorize(MessageSecurityAuthorizationRequest request) throws SecurityException{

    	String principalWSS = request.getWssSecurityPrincipal();
    	Busta busta = request.getBusta();
    	org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext = request.getMessageSecurityContext();
    	OpenSPCoop2Message msg = request.getMessage();
    	

    	IDServizio idServizio = null;
    	try {
        	idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
        			busta.getTipoDestinatario(), busta.getDestinatario(), busta.getVersioneServizio());
        
        	OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
        	
	    	// ****** Proprieta' WSSecurity ********
	    	
	    	String actor = messageSecurityContext.getActor();
			if("".equals(messageSecurityContext.getActor()))
				actor = null;
	    	
			String pdpLocalString = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.AUTH_PDP_LOCAL);
			boolean pdpLocal = true;
			if(pdpLocalString==null || "".equals(pdpLocalString.trim())){
				pdpLocal = true;
			}
			else if("true".equalsIgnoreCase(pdpLocalString.trim())){
				pdpLocal = true;
			}
			else if("false".equalsIgnoreCase(pdpLocalString.trim())){
				pdpLocal = false;
			}
			else{
				throw new SecurityException("Property '"+SecurityConstants.AUTH_PDP_LOCAL+"' with wrong value ["+pdpLocalString.trim()+"]");
			}
			
			String remotePdD_url = null;
			Integer remotePdD_connectionTimeout = HttpUtilities.HTTP_CONNECTION_TIMEOUT;
			Integer remotePdD_readConnectionTimeout = HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
			if(!pdpLocal){
				remotePdD_url = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.AUTH_PDP_REMOTE_URL);
				if(pdpLocalString==null || "".equals(pdpLocalString.trim())){
					throw new SecurityException("Property '"+SecurityConstants.AUTH_PDP_REMOTE_URL+"' not found (required with "+SecurityConstants.AUTH_PDP_LOCAL+"=false)");
				}
				pdpLocalString = pdpLocalString.trim();
				try{
					(new URI(remotePdD_url)).toString();
				}catch(Exception e){
					throw new SecurityException("Property '"+SecurityConstants.AUTH_PDP_REMOTE_URL+"' with wrong value ["+remotePdD_url+"]: "+e.getMessage(),e);
				}
				
				String tmp = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.AUTH_PDP_REMOTE_CONNECTION_TIMEOUT);
				if(tmp!=null && !"".equals(tmp)){
					tmp = tmp.trim();
					try{
						remotePdD_connectionTimeout = Integer.parseInt(tmp);
					}catch(Exception e){
						throw new SecurityException("Property '"+SecurityConstants.AUTH_PDP_REMOTE_CONNECTION_TIMEOUT+"' with wrong value ["+tmp+"]: "+e.getMessage(),e);
					}
				}
				
				tmp = (String) messageSecurityContext.getIncomingProperties().get(SecurityConstants.AUTH_PDP_REMOTE_READ_CONNECTION_TIMEOUT);
				if(tmp!=null && !"".equals(tmp)){
					tmp = tmp.trim();
					try{
						remotePdD_readConnectionTimeout = Integer.parseInt(tmp);
					}catch(Exception e){
						throw new SecurityException("Property '"+SecurityConstants.AUTH_PDP_REMOTE_READ_CONNECTION_TIMEOUT+"' with wrong value ["+tmp+"]: "+e.getMessage(),e);
					}
				}
			}
			
			
			
			
			
			// ****** Raccolta Dati ********
			
			String tipoSoggettoErogatore = busta.getTipoDestinatario();
			String nomeSoggettoErogatore = busta.getDestinatario();
			String tipoServizio = busta.getTipoServizio();
			String nomeServizio = busta.getServizio();
			String azione = busta.getAzione() != null ? busta.getAzione() : "";
	
			String servizioKey = "http://"+tipoSoggettoErogatore+nomeSoggettoErogatore+".openspcoop2.org/servizi/"+tipoServizio+nomeServizio;
			String azioneKey = servizioKey+"/" + azione;
			
	    	if(pdpLocal){
	    		
	    		byte[] policy = null; 
		    	try{
			    	AccordoServizioParteSpecifica asps = RegistroServiziManager.getInstance().getAccordoServizioParteSpecifica(idServizio, null, true);
			    	for (int i = 0; i < asps.sizeSpecificaSicurezzaList(); i++) {
						Documento d = asps.getSpecificaSicurezza(i);
						if(TipiDocumentoSicurezza.XACML_POLICY.getNome().equals(d.getTipo())){
							if(policy == null){
								if(d.getByteContenuto()!=null){
									policy = d.getByteContenuto();	
								}
								else if(d.getFile()!=null){
									if(d.getFile().startsWith("http://") || d.getFile().startsWith("file://")){
										URL url = new URL(d.getFile());
										policy = HttpUtilities.requestHTTPFile(url.toString());
									}
									else{
										File f = new File(d.getFile());
										policy = FileSystemUtilities.readBytesFromFile(f);
									}
								}
							}else
					    		throw new SecurityException("Piu di una xacml policy trovata trovata per il servizio "+idServizio.toString());
						}
					}
		    	}catch(Exception e){
		    		throw new SecurityException("Errore durante la ricerca delle policies xacml per il servizio "+idServizio.toString()+": "+e.getMessage(),e);
		    	}
		    	if(policy== null){
		    		throw new SecurityException("Nessuna xacml policy trovata trovata per il servizio "+idServizio.toString());
		    	}
		    	
		    	if(MessageSecurityAuthorizationSAMLPolicy.pdp == null) {
		    		MessageSecurityAuthorizationSAMLPolicy.initPdD(messageSecurityContext.getLog());
		    	}
		    	
				// Caricamento in PdP vedendo che la policy non sia gia stata caricata ....
		    	MessageSecurityAuthorizationSAMLPolicy.pdp.addPolicy(unmarshallPolicy(policy), servizioKey);
	    	}
	    	
	    	
	    	
	    	
	    	// ****** Header WSSecurity con SAML ********
	    	
	    	SOAPElement security = null;
	    	try{
	    		security = (SOAPElement) WSSecurityUtil.getSecurityHeader(soapMessage.getSOAPPart(), actor);
	    	}catch(Exception e){
	    		throw new SecurityException("Errore durante la ricerca dell'header WSSecurity (actor:"+actor+") "+e.getMessage(),e);
	    	}
	    	if(security==null){
	     		throw new SecurityException("Header WSSecurity (actor:"+actor+") contenente una SAML non trovato");
	     	}
	    	
			DynamicNamespaceContext dnc = null;
			if(MessageType.SOAP_11.equals(soapMessage.getMessageType())) {
				dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContextFromSoapEnvelope11(soapMessage.getSOAPPart().getEnvelope());
			} else {
				dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContextFromSoapEnvelope12(soapMessage.getSOAPPart().getEnvelope());
			}
	    	
			boolean SAML_2_0 = false;
			try {
				Object o = this.xpathExpressionEngine.getMatchPattern(security, dnc, SAMLConstants.XPATH_SAML_20_ASSERTION, XPathReturnType.NODE);
				SAML_2_0 = (o!=null);
			} catch(XPathNotFoundException e) {
				SAML_2_0 = false;
			}

			
			
			
			
			// ****** Produzione XACMLRequest a partire dalla SAML ********
	  	
	    	RequestType xacmlRequest = this.factory.createRequestType();
			
	    	// action
			ActionType action =  this.factory.createActionType();	
			action.getAttributes().add(createAttribute("urn:oasis:names:tc:xacml:1.0:action:action-id", azioneKey));
			xacmlRequest.setAction(action);
	
			// subject
			SubjectType roleSubject = this.factory.createSubjectType();
	
	    	// Creare un Subject che contiene nel subject id:
	    	// come urn:oasis:names:tc:xacml:1.0:subject:subject-id il valore del principal presente nella richiesta (principalWSS)
			if(principalWSS != null) {
				roleSubject.getAttributes().add(
						createAttribute("urn:oasis:names:tc:xacml:1.0:subject:subject-id", principalWSS));
			}
			
	    	// inoltre creare altri attribute del subject che contengano:
			SAMLAttributes saml = new SAMLAttributes(security, dnc, SAML_2_0, this.xpathExpressionEngine);

			// il valore del NameID all'interno dell'elemento Subject 
			if(SAML_2_0){
				if(saml.nameIDAuthorization!=null){
					roleSubject.getAttributes().add(
							createAttribute(SAML_20_SUBJECT_NAME, saml.nameIDAuthorization));
				}
			}
			else{
				if(saml.nameIDAuthorization!=null){
					roleSubject.getAttributes().add(
							createAttribute(SAML_11_AUTHORIZATION_SUBJECT_NAME, saml.nameIDAuthorization));
				}
				if(saml.nameIDAuthentication!=null){
					roleSubject.getAttributes().add(
							createAttribute(SAML_11_AUTHENTICATION_SUBJECT_NAME, saml.nameIDAuthentication));
				}
			}
	
	    	// il valore dell'elemento Issuer
			if(SAML_2_0){
				roleSubject.getAttributes().add(
						createAttribute(SAML_20_ISSUER, saml.issuer));
			}
			else{
				roleSubject.getAttributes().add(
						createAttribute(SAML_11_ISSUER, saml.issuer));
			}
			
	    	// - AttributeId="urn:oasis:names:tc:SAML:XX:assertion/AttributeStatement/Attribute/Name/<Nome> se il NameFormat non è una uri altrimenti direttamente il nome come attribute Id
			for(String keyAttribute: saml.customAttributes.keySet()) {
				roleSubject.getAttributes().add(
						createAttribute(keyAttribute, saml.customAttributes.get(keyAttribute)));
			}
	
			xacmlRequest.getSubjects().add(roleSubject);
			xacmlRequest.setEnvironment(this.factory.createEnvironmentType());
	
			
			
			
			// ****** Valutazione XACMLRequest con PdD ********
			
			List<ResultType> results = null;
	    	ResourceType resource = this.factory.createResourceType();
			if(pdpLocal){
	    		try {
	    			
	    			//solo in caso di pdp locale inizializzo la resource __resource_id___ con il nome del servizio in modo da consentire l'identificazione della policy
	    			
	    			resource.getAttributes().add(createAttribute(CachedMapBasedSimplePolicyRepository.RESOURCE_ATTRIBUTE_ID_TO_MATCH, servizioKey));
	    					
	    			xacmlRequest.getResources().add(resource);
	    	    	messageSecurityContext.getLog().debug("----XACML Request locale begin ---");
	    	    	messageSecurityContext.getLog().debug(new String(marshallRequest(xacmlRequest)));
	    	    	messageSecurityContext.getLog().debug("----XACML Request locale end ---");
	    	    	
	    			results = MessageSecurityAuthorizationSAMLPolicy.pdp.evaluate(xacmlRequest);
	    	    	messageSecurityContext.getLog().debug("----XACML Results begin ---");
	    			for(ResultType result: results) {
		    	    	messageSecurityContext.getLog().debug("Decision: "+result.getDecision().toString());
	    			}
	    	    	messageSecurityContext.getLog().debug("----XACML Results end ---");

	    		} catch(Exception e) {
	    			throw new SecurityException("Errore avvenuto durante l'interrogazione del PdP locale per l'autorizzazione del servizio "+idServizio.toString()+": "+e.getMessage(),e);
	    		}
	    	}
	    	else{
	    		try{
	    			xacmlRequest.getResources().add(resource);	    			
	    			byte[] xacmlBytes = marshallRequest(xacmlRequest);
	    	    	messageSecurityContext.getLog().debug("----XACML Request remota begin ---");
	    	    	messageSecurityContext.getLog().debug(new String(xacmlBytes));
	    	    	messageSecurityContext.getLog().debug("----XACML Request remota end ---");

	    	    	HttpRequest httpRequest = new HttpRequest();
	    	    	httpRequest.setUrl(remotePdD_url);
	    	    	httpRequest.setContent(xacmlBytes);
	    	    	httpRequest.setMethod(HttpRequestMethod.POST);
	    	    	httpRequest.setContentType(HttpConstants.CONTENT_TYPE_TEXT_XML);
	    	    	httpRequest.setReadTimeout(remotePdD_readConnectionTimeout);
	    	    	httpRequest.setConnectTimeout(remotePdD_connectionTimeout);
	    	    	HttpResponse httpResponse = HttpUtilities.httpInvoke(httpRequest);
	    			if(httpResponse.getResultHTTPOperation()==200){
	    				byte[] res = httpResponse.getContent();
	    				results = unmarshall(res);
	    			}
	    			else{
	    				throw new Exception("invocazione fallita (http-return-code: "+httpResponse.getResultHTTPOperation()+")");
	    			}
	    		}catch(Exception e){
		    		throw new SecurityException("Errore avvenuto durante l'interrogazione del PdP remoto ["+remotePdD_url+"] per l'autorizzazione del servizio "+idServizio.toString()+": "+e.getMessage(),e);
		    	}
	    	}
	    	
	    	DecisionType decision = ResultCombining.combineDenyOverrides(results);
	
	    	MessageSecurityAuthorizationResult result = new MessageSecurityAuthorizationResult();
	
	    	if(DecisionType.PERMIT.equals(decision)) {
	        	result.setAuthorized(true);
	        	result.setErrorMessage(null);
	    	} else {
	    		
	    		String url = "";
	    		String tipo = "XACML-Policy";
    			if(!pdpLocal){
    				url = " url["+remotePdD_url+"]";
    				tipo = "XACML-Policy-RemotePdp";
    			}
	    		try{
	    			StringBuffer bfPolicy = new StringBuffer();
	    			for (int i = 0; i < results.size(); i++) {
		        		ResultType res = results.get(i);
	    				if(bfPolicy.length()>0){
	    					bfPolicy.append("\n");
	    				}
	    				bfPolicy.append("Result["+(i+1)+"]: ");
	    				ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    				JaxbUtils.objToXml(bout, res.getClass(), res);
	    				bout.flush();
	    				bout.close();
	    				bfPolicy.append(bout.toString());
	    			}
	    			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione con XACMLPolicy fallita pddLocal["+pdpLocal+"]"+url+"; results (size:"+results.size()+"): \n"+bfPolicy.toString());
	    		}catch(Throwable e){
	    			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione con XACMLPolicy fallita pddLocal["+pdpLocal+"]"+url+". Serializzazione risposta non riuscita",e);
	    		}
	    		
	        	result.setAuthorized(false);
		        	
	        	StringBuffer bf = new StringBuffer();
	        	for (int i = 0; i < results.size(); i++) {
	        		ResultType res = results.get(i);
	        		
	        		boolean check = false;
		        	if(DecisionType.DENY.equals(decision)) {
		        		check = DecisionType.DENY.equals(res.getDecision());
		        	}
		        	else{
		        		check = DecisionType.DENY.equals(res.getDecision()) || DecisionType.INDETERMINATE.equals(res.getDecision()) || DecisionType.NOT_APPLICABLE.equals(res.getDecision());
		        	}
	        		
	    	    	if(check) {
	    	    		if(bf.length()>0){
	    	    			bf.append(" - ");
	    				}
	    	    		bf.append("(result-"+(i+1)+" "+res.getDecision().name());
	    	    		if( res.getStatus() != null ){
		    	    		if(res.getStatus().getStatusCode() != null){
		    	    			bf.append(" code:").append(res.getStatus().getStatusCode().getValue());
		    	    		}
		    	    		if(res.getStatus().getStatusMessage() != null){
		    	    			bf.append(" ").append(res.getStatus().getStatusMessage());
		    	    		}
	    	    		}
	    	    		bf.append(")");
	    	        	
	    	    	}
	    	    }
	        	if(bf.length()>0){
	        		result.setErrorMessage(tipo+" "+bf.toString());
	        	}
	        	else{
	        		result.setErrorMessage(tipo);
	        	}
	    	}
	    	return result;
	    	
    	} catch(SecurityException e) {
    		messageSecurityContext.getLog().error("Errore di sicurezza durante la gestione della richiesta per il servizio: " + idServizio, e);
    		throw e;
    	} catch(Exception e) {
    		messageSecurityContext.getLog().error("Errore generico durante la gestione della richiesta per il servizio: " + idServizio, e);
    		throw new SecurityException(e);
    	}
    }

    private List<ResultType> unmarshall(byte[] res) throws SyntaxException {
		
    	InputStream inputStream = null;
    	try{
    		inputStream = new ByteArrayInputStream(res);
    		ResponseType response = ResponseMarshaller.unmarshal(inputStream);
    		return response.getResults();
    	} finally {
    		if(inputStream != null) {
    			try {
    				inputStream.close();
    			} catch(Exception e) {}
    		}
    	}
	}

    private Evaluatable unmarshallPolicy(byte[] res) throws SyntaxException {
		
    	InputStream inputStream = null;
    	try{
    		inputStream = new ByteArrayInputStream(res);
    		Evaluatable response = PolicyMarshaller.unmarshal(inputStream);
    		return response;
    	} finally {
    		if(inputStream != null) {
    			try {
    				inputStream.close();
    			} catch(Exception e) {}
    		}
    	}
	}


	private byte[] marshallRequest(RequestType request) throws SecurityException {
		ByteArrayOutputStream baos = null;
		try{

			baos = new ByteArrayOutputStream();
			RequestMarshaller.marshal(request, baos);
			
			return baos.toByteArray();
		} catch (WritingException e) {
			throw new SecurityException(e);
		} finally {
			if(baos != null) {
				try{
					baos.flush();
					baos.close();
				} catch (Exception e) {}
			}
		}
    }
	
	private class SAMLAttributes {
		public String nameIDAuthentication;
		public String nameIDAuthorization;
		public String issuer;
		public Map<String, List<String>> customAttributes;
		
		private DynamicNamespaceContext dnc;
		private boolean saml20;
		private AbstractXPathExpressionEngine xpathExpressionEngine;
		
		public SAMLAttributes(SOAPElement security, DynamicNamespaceContext dnc, boolean saml20, AbstractXPathExpressionEngine xpathExpressionEngine) throws Exception {
			this.dnc = dnc;
			this.saml20 = saml20;
			this.xpathExpressionEngine = xpathExpressionEngine;
			if(this.saml20){
				this.nameIDAuthorization = find(security, SAMLConstants.XPATH_SAML_20_ASSERTION_SUBJECT_NAMEID,false);
				this.issuer = find(security,  SAMLConstants.XPATH_SAML_20_ASSERTION_ISSUER,true);
			}
			else{
				this.nameIDAuthorization = find(security, SAMLConstants.XPATH_SAML_11_ASSERTION_ATTRIBUTESTATEMENT_SUBJECT_NAMEID,false);
				this.nameIDAuthentication = find(security, SAMLConstants.XPATH_SAML_11_ASSERTION_AUTHENTICATIONSTATEMENT_SUBJECT_NAMEID,false);
				this.issuer = find(security,  SAMLConstants.XPATH_SAML_11_ASSERTION_ISSUER,true);
			}
			this.customAttributes = findNameAttributes(security);
		}

		private String find(SOAPElement security,String xpath, boolean required) throws SAXException, SOAPException,
				XPathException, XPathNotValidException, Exception {
			
			String match = null;
			try {
				match = (String) this.xpathExpressionEngine.getMatchPattern(security, this.dnc, xpath, XPathReturnType.STRING);
			} catch(XPathNotFoundException e) {
				if(required)
					throw new Exception(e.getMessage(),e);
				else
					return null;
			}
			return match;
		}

		private Map<String, List<String>> findNameAttributes(SOAPElement security) throws SAXException, SOAPException,
				XPathException, XPathNotValidException, Exception {
			
			Map<String, List<String>> nameAttributes = new HashMap<String, List<String>>();

			String xpath = null;
			if(this.saml20){
				xpath = SAMLConstants.XPATH_SAML_20_ASSERTION_ATTRIBUTESTATEMENT_ATTRIBUTE;
			}
			else{
				xpath = SAMLConstants.XPATH_SAML_11_ASSERTION_ATTRIBUTESTATEMENT_ATTRIBUTE;
			}
			try {
				NodeList attributes = (NodeList) this.xpathExpressionEngine.getMatchPattern(security, this.dnc, xpath, XPathReturnType.NODESET);
				if(attributes != null) {
					for(int i = 0; i < attributes.getLength(); i++) {
						org.w3c.dom.Node attribute = attributes.item(i);
						String name = null;
						String friendlyName = null;
						boolean uriNameFormat = false;
						if(this.saml20){
							Node nameAttribute = attribute.getAttributes().getNamedItem("Name");
							name = nameAttribute.getNodeValue();
							Node friendlyNameAttribute = attribute.getAttributes().getNamedItem("FriendlyName");
							if(friendlyNameAttribute!=null){
								friendlyName = friendlyNameAttribute.getNodeValue();
							}
							Node nameFormatAttribute = attribute.getAttributes().getNamedItem("NameFormat");
							if(nameFormatAttribute!=null){
								uriNameFormat = SAML2Constants.ATTRNAME_FORMAT_URI.equals(nameFormatAttribute.getNodeValue());
							}	
						}
						else{
							Node nameAttribute = attribute.getAttributes().getNamedItem("AttributeNamespace");
							name = nameAttribute.getNodeValue();
							Node friendlyNameAttribute = attribute.getAttributes().getNamedItem("AttributeName");
							friendlyName = friendlyNameAttribute.getNodeValue(); // obbligatorio in saml11
						}
						if(uriNameFormat==false){
							if(friendlyName==null){
								if(name.startsWith("urn:") || name.startsWith("url:") || name.startsWith("http:") || name.startsWith("htts:")){
									uriNameFormat = true;
								}
							}
						}

						String key = null;
						if(friendlyName != null) {
							if(this.saml20){
								key = SAML_20_ATTRIBUTE_NAME + friendlyName;
							}
							else{
								key = SAML_11_ATTRIBUTE_NAME + friendlyName;
							}
						} else {
							if(uriNameFormat) {
								key = name;
							} else {
								if(this.saml20){
									key = SAML_20_ATTRIBUTE_NAME + name;
								}
								else{
									key = SAML_11_ATTRIBUTE_NAME + name;
								}
							}
						}

						String samlNamespace = null;
						if(this.saml20){
							samlNamespace = SAMLConstants.SAML_20_NAMESPACE;
						}else{
							samlNamespace = SAMLConstants.SAML_11_NAMESPACE;
						}
						
						List<String> values = null;
						if(nameAttributes.containsKey(key)) {
							values = nameAttributes.remove(key);
						} else {
							values = new ArrayList<String>();
						}
						NodeList attributeValues = attribute.getChildNodes();
						if(attributeValues != null) {
							for(int j = 0; j < attributeValues.getLength(); j++) {
								org.w3c.dom.Node attributeValue = attributeValues.item(j);
								if(attributeValue != null && "AttributeValue".equals(attributeValue.getLocalName()) && 
										samlNamespace.equals(attributeValue.getNamespaceURI())) {
									values.add(attributeValue.getTextContent());
								}
							}
						}
						nameAttributes.put(key, values);
					}
				}
			} catch(XPathNotFoundException e) {
				throw new Exception("Impossibile trovare l'xPath ["+xpath+"]");
			}
			return nameAttributes;
		}
	}

	private AttributeType createAttribute(String name, List<String> values) {

		AttributeType attribute = this.factory.createAttributeType();
		
		for(String value: values) {
			AttributeValueType value1 = new AttributeValueType();
			value1.getContent().add(value);
			attribute.getAttributeValues().add(value1);
		}

		attribute.setAttributeId(name);
		attribute.setDataType(new StringDataTypeAttribute());		
		return attribute;
		
	}

	private AttributeType createAttribute(String name, String value) {

		List<String> lst = new ArrayList<String>();
		lst.add(value);
		return createAttribute(name, lst);
		
	}
    
}
