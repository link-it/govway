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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.apache.soap.encoding.soapenc.Base64;
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
import org.herasaf.xacml.core.simplePDP.initializers.InitializerExecutor;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.message.DynamicNamespaceContextFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.HttpBodyParameters;
import org.openspcoop2.utils.resources.HttpResponseBody;
import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.utils.xacml.CachedMapBasedSimplePolicyRepository;
import org.openspcoop2.utils.xacml.PolicyDecisionPoint;
import org.openspcoop2.utils.xacml.ResultCombining;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathException;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathNotValidException;
import org.openspcoop2.utils.xml.XPathReturnType;
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


	private static PolicyDecisionPoint pdp;
	private ObjectFactory factory;

    public MessageSecurityAuthorizationSAMLPolicy()  {
		this.factory = new ObjectFactory();
		PolicyDecisionPoint.runInitializers(); //necessario per far inizializzare gli unmarshaller in caso di pdp remoto
    }


    @Override
    public MessageSecurityAuthorizationResult authorize(MessageSecurityAuthorizationRequest request) throws SecurityException{

    	String principalWSS = request.getWssSecurityPrincipal();
    	Busta busta = request.getBusta();
    	org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext = request.getMessageSecurityContext();
    	OpenSPCoop2Message message = request.getMessage();
    	IDServizio idServizio = new IDServizio(busta.getTipoDestinatario(), busta.getDestinatario(), 
    			busta.getTipoServizio(), busta.getServizio());
    	try {	    	
	    	// Proprieta' WSSecurity
	    	
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
			
			String tipoSoggettoErogatore = busta.getTipoDestinatario();
			String nomeSoggettoErogatore = busta.getDestinatario();
			String tipoServizio = busta.getTipoServizio();
			String nomeServizio = busta.getServizio();
			String azione = busta.getAzione() != null ? busta.getAzione() : "";
	
			String servizioKey = "http://"+tipoSoggettoErogatore+"_"+nomeSoggettoErogatore+".openspcoop2.org/servizi/"+tipoServizio+"_"+nomeServizio;
			String azioneKey = servizioKey+"/" + azione;
			
	    	if(pdpLocal){
	    		
	    		byte[] policy = null; 
		    	try{
			    	AccordoServizioParteSpecifica asps = RegistroServiziManager.getInstance().getAccordoServizioParteSpecifica(idServizio, null, true);
			    	for (int i = 0; i < asps.sizeSpecificaSicurezzaList(); i++) {
						Documento d = asps.getSpecificaSicurezza(i);
						if(TipiDocumentoSicurezza.XACML_POLICY.getNome().equals(d.getTipo())){
							if(policy == null)
								policy = d.getByteContenuto();	
							else
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
		    		MessageSecurityAuthorizationSAMLPolicy.pdp = new PolicyDecisionPoint(messageSecurityContext.getLog());
		    	}
		    	
				// Caricamento in PdP vedendo che la policy non sia gia stata caricata ....
		    	MessageSecurityAuthorizationSAMLPolicy.pdp.addPolicy(unmarshallPolicy(policy), servizioKey);
	    	}
	    	
	    	
	    	// Localizzo Header SAML
	    	SOAPElement security = null;
	    	try{
	    		security = (SOAPElement) WSSecurityUtil.getSecurityHeader(message.getSOAPPart(), actor);
	    	}catch(Exception e){
	    		throw new SecurityException("Errore durante la ricerca dell'header WSSecurity (actor:"+actor+") "+e.getMessage(),e);
	    	}
	    	if(security==null){
	     		throw new SecurityException("Header WSSecurity (actor:"+actor+") contenente una SAML non trovato");
	     	}
	  	
	    	// Produzione XACMLRequest a partire dalla SAML
	
	    	RequestType xacmlRequest = this.factory.createRequestType();
	
			
	    	// Creare un <Action> che contiene una url cosi realizzata: http://<tipoSoggettoErogatore>_<nomeSoggettoErogatore>.openspcoop2.org/servizi/<tipoServizio>_<nomeServizio>/<nomeAzione>
	    	// I valori erogatore servizio e azioni si possono prendere dalla busta. L'azione può essere null
	    	
			ActionType action =  this.factory.createActionType();
	
			action.getAttributes().add(createAttribute("urn:oasis:names:tc:xacml:1.0:action:action-id", azioneKey));
			xacmlRequest.setAction(action);
	
			SubjectType roleSubject = this.factory.createSubjectType();
	
	    	// Creare un Subject che contiene nel subject id:
	    	// come urn:oasis:names:tc:xacml:1.0:subject:subject-id il valore del principal presente nella richiesta (principalWSS)
	
			
			if(principalWSS != null) {
				roleSubject.getAttributes().add(
						createAttribute("urn:oasis:names:tc:xacml:1.0:subject:subject-id", principalWSS));
			}
			
	    	// inoltre creare altri attribute del subject che contengano:
			
	    	// - AttributeId="urn:oasis:names:tc:SAML:2.0:assertion/Subject/NameID il valore del NameID all'interno dell'elemento Subject 
	
			SAMLAttributes saml = new SAMLAttributes(security, message);
	
			roleSubject.getAttributes().add(
					createAttribute("urn:oasis:names:tc:SAML:2.0:assertion/Subject/NameID", saml.nameID));
	
	    	// - AttributeId="urn:oasis:names:tc:SAML:2.0:assertion/Issuer il valore dell'elemento Issuer
	
			roleSubject.getAttributes().add(
					createAttribute("urn:oasis:names:tc:SAML:2.0:assertion/Issuer", saml.issuer));
			
	    	// - AttributeId="urn:oasis:names:tc:SAML:2.0:assertion/AttributeStatement/Attribute/Name/<Nome> se il NameFormat non è una uri altrimenti direttamente il nome come attribute Id
	
			for(String keyAttribute: saml.customAttributes.keySet()) {
				
				roleSubject.getAttributes().add(
						createAttribute(keyAttribute, saml.customAttributes.get(keyAttribute)));
			}
	
	
			xacmlRequest.getSubjects().add(roleSubject);
			xacmlRequest.setEnvironment(this.factory.createEnvironmentType());
	
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

	    			HttpResponseBody response = post(remotePdD_url, xacmlBytes, remotePdD_readConnectionTimeout, remotePdD_connectionTimeout, null, null);
	    			if(response.getResultHTTPOperation()==200){
	    				byte[] res = response.getResponse();
	    				results = unmarshall(res);
	    			}
	    			else{
	    				throw new Exception("invocazione fallita (http-return-code: "+response.getResultHTTPOperation()+")");
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
	        	result.setAuthorized(false);
	
	    		for(ResultType res: results) {
	    	    	if(DecisionType.DENY.equals(res.getDecision()) && res.getStatus() != null) {
	    	        	result.setErrorMessage(res.getStatus().getStatusMessage());
	    	    	}
	    	    }
	    	}
	    	return result;
    	} catch(SecurityException e) {
    		messageSecurityContext.getLog().error("Errore di sicurezza durante la gestione della richiesta per il servizio: " + idServizio.toString(), e);
    		throw e;
    	} catch(Exception e) {
    		messageSecurityContext.getLog().error("Errore generico durante la gestione della richiesta per il servizio: " + idServizio.toString(), e);
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
		public String nameID;
		public String issuer;
		public Map<String, List<String>> customAttributes;
		
		public SAMLAttributes(SOAPElement security, OpenSPCoop2Message message) throws Exception {
			this.nameID = find(security, message, "//{urn:oasis:names:tc:SAML:2.0:assertion}:Assertion/{urn:oasis:names:tc:SAML:2.0:assertion}:Subject/{urn:oasis:names:tc:SAML:2.0:assertion}:NameID/text()");
			this.issuer = find(security, message, "//{urn:oasis:names:tc:SAML:2.0:assertion}:Assertion/{urn:oasis:names:tc:SAML:2.0:assertion}:Issuer/text()");
			this.customAttributes = findNameAttributes(security, message);
		}

		private String find(SOAPElement security, OpenSPCoop2Message message,
				String xpath) throws SAXException, SOAPException,
				XPathException, XPathNotValidException, Exception {
			DynamicNamespaceContext dnc = null;
			if(message.getVersioneSoap().equals(SOAPVersion.SOAP11)) {
				dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContextFromSoapEnvelope11(message.getSOAPPart().getEnvelope());
			} else {
				dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContextFromSoapEnvelope12(message.getSOAPPart().getEnvelope());
			}
			
			String match = null;
			try {
				match = (String) new XPathExpressionEngine().getMatchPattern(security, dnc, xpath, XPathReturnType.STRING);
			} catch(XPathNotFoundException e) {
				throw new Exception("Impossibile trovare l'xPath ["+xpath+"]");
			}
			return match;
		}

		private Map<String, List<String>> findNameAttributes(SOAPElement security, OpenSPCoop2Message message) throws SAXException, SOAPException,
				XPathException, XPathNotValidException, Exception {
			DynamicNamespaceContext dnc = null;
			if(message.getVersioneSoap().equals(SOAPVersion.SOAP11)) {
				dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContextFromSoapEnvelope11(message.getSOAPPart().getEnvelope());
			} else {
				dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContextFromSoapEnvelope12(message.getSOAPPart().getEnvelope());
			}
			
			Map<String, List<String>> nameAttributes = new HashMap<String, List<String>>();

			String xpath = "//{urn:oasis:names:tc:SAML:2.0:assertion}:Assertion/{urn:oasis:names:tc:SAML:2.0:assertion}:AttributeStatement/{urn:oasis:names:tc:SAML:2.0:assertion}:Attribute";
			try {
				NodeList attributes = (NodeList) new XPathExpressionEngine().getMatchPattern(security, dnc, xpath, XPathReturnType.NODESET);
				if(attributes != null) {
					for(int i = 0; i < attributes.getLength(); i++) {
						org.w3c.dom.Node attribute = attributes.item(i);
						Node nameAttribute = attribute.getAttributes().getNamedItem("Name");
						Node friendlyNameAttribute = attribute.getAttributes().getNamedItem("FriendlyName");
						Node nameFormatAttribute = attribute.getAttributes().getNamedItem("NameFormat");
						boolean uriNameFormat = nameFormatAttribute != null && nameFormatAttribute.getNodeValue().equals("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

						String key = null;
						if(friendlyNameAttribute != null) {
							key = "urn:oasis:names:tc:SAML:2.0:assertion/AttributeStatement/Attribute/Name/" + friendlyNameAttribute.getNodeValue();
						} else {
							if(uriNameFormat) {
								key = nameAttribute.getNodeValue();
							} else {
								key = "urn:oasis:names:tc:SAML:2.0:assertion/AttributeStatement/Attribute/Name/" + nameAttribute.getNodeValue();
							}
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
								if(attributeValue != null && "AttributeValue".equals(attributeValue.getLocalName()) && "urn:oasis:names:tc:SAML:2.0:assertion".equals(attributeValue.getNamespaceURI())) {
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
	private HttpResponseBody post(String path,byte[] body, int readTimeout,int connectTimeout,String username,String password) throws UtilsException{
		InputStream is = null;
		ByteArrayOutputStream outResponse = null;
		try{
			URL url = new URL(path);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;

			httpConn.setConnectTimeout(connectTimeout);
			httpConn.setReadTimeout(readTimeout);
			
			if(username!=null && password!=null){
				String authentication = username + ":" + password;
				authentication = "Basic " + 
				Base64.encode(authentication.getBytes());
				httpConn.setRequestProperty("Authorization",authentication);
			}
			
			
			
			setStream(httpConn, "POST", "text/xml");

			HttpBodyParameters httpContent = new  HttpBodyParameters("POST", "text/xml");
			// Spedizione byte
			if(httpContent.isDoOutput() && body != null){
				OutputStream out = httpConn.getOutputStream();
				out.write(body);
				out.flush();
				out.close();
			}
			

			int resultHTTPOperation = httpConn.getResponseCode();
			if(resultHTTPOperation==404){
				throw new UtilsException("404");
			}

			// Ricezione Risposta
			outResponse = new ByteArrayOutputStream();
			if(resultHTTPOperation>399){
				is = httpConn.getErrorStream();
				if(is==null){
					is = httpConn.getInputStream();
				}
			}else{
				is = httpConn.getInputStream();
				if(is==null){
					is = httpConn.getErrorStream();
				}
			}
			byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
			int readByte = 0;
			while((readByte = is.read(readB))!= -1){
				outResponse.write(readB,0,readByte);
			}
			is.close();
			// fine HTTP.
			httpConn.disconnect();

			byte[] xmlottenuto = outResponse.toByteArray();
			outResponse.close();
			
			HttpResponseBody response = new HttpResponseBody();
			response.setResponse(xmlottenuto);
			response.setResultHTTPOperation(resultHTTPOperation);
			return response;
		}catch(Exception e){
			try{
				if(is!=null)
					is.close();
			}catch(Exception eis){}
			try{
				if(outResponse!=null)
					outResponse.close();
			}catch(Exception eis){}
			if(e.getMessage()!=null && e.getMessage().contains("404"))
				throw new UtilsException("404");
			else
				throw new UtilsException("Utilities.requestHTTPFile error "+e.getMessage(),e);
		}
	}
	
	public static void setStream(HttpURLConnection httpConn, String httpMethod, String contentType) throws UtilsException{
		try{
			HttpBodyParameters params = new HttpBodyParameters(httpMethod, contentType);
						
			httpConn.setRequestMethod(httpMethod);
			if(params.isDoOutput()){
				httpConn.setDoOutput(params.isDoOutput());
			}
			if(params.isDoInput()){
				httpConn.setDoInput(params.isDoInput());
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	} 

    
}
