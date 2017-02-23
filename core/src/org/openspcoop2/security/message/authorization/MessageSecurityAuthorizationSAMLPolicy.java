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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPElement;

import org.apache.wss4j.dom.util.WSSecurityUtil;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.resources.HttpResponseBody;
import org.openspcoop2.utils.resources.HttpUtilities;


/**
 * Implementazione esempio dell'interfaccia Authorization
 *
 * @author Montebove Luciano <L.Montebove@finsiel.it>
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 */

public class MessageSecurityAuthorizationSAMLPolicy  implements IMessageSecurityAuthorization{


    public MessageSecurityAuthorizationSAMLPolicy() {
    }


    @Override
    public MessageSecurityAuthorizationResult authorize(MessageSecurityAuthorizationRequest request) throws SecurityException{
    	
    	String principalWSS = request.getWssSecurityPrincipal();
    	Busta busta = request.getBusta();
    	org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext = request.getMessageSecurityContext();
    	OpenSPCoop2Message message = request.getMessage();
    	IDServizio idServizio = new IDServizio(busta.getTipoDestinatario(), busta.getDestinatario(), 
    			busta.getTipoServizio(), busta.getServizio());
    	
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
		
		
    	// Localizzo Policy
    	List<byte[]> policies = new ArrayList<>();
    	if(pdpLocal){
	    	try{
		    	AccordoServizioParteSpecifica asps = RegistroServiziManager.getInstance().getAccordoServizioParteSpecifica(idServizio, null, true);
		    	for (int i = 0; i < asps.sizeSpecificaSicurezzaList(); i++) {
					Documento d = asps.getSpecificaSicurezza(i);
					if(TipiDocumentoSicurezza.XACML_POLICY.getNome().equals(d.getTipo())){
						policies.add(d.getByteContenuto());	
					}
				}
	    	}catch(Exception e){
	    		throw new SecurityException("Errore durante la ricerca delle policies xacml per il servizio "+idServizio.toString()+": "+e.getMessage(),e);
	    	}
	    	if(policies.size()<=0){
	    		throw new SecurityException("Nessuna xacml policy trovata trovata per il servizio "+idServizio.toString());
	    	}
	    	
	    	// Caricamento in PdD vedendo che la policy non sia gia stata caricata ....
	    	// TODO...
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
    	// TODO...
    	messageSecurityContext.getLog().debug("TODO log xml request costruito");
    	
    	// Creare un <Action> che contiene una url cosi realizzata: http://<tipoSoggettoErogatore>_<nomeSoggettoErogatore>.openspcoop2.org/servizi/<tipoServizio>_<nomeServizio>/<nomeAzione>
    	// I valori erogatore servizio e azioni si possono prendere dalla busta. L'azione può essere null
    	
    	// Creare un Subject che contiene nel subject id:
    	// come urn:oasis:names:tc:xacml:1.0:subject:subject-id il valore del principal presente nella richiesta (principalWSS)
    	// inoltre creare altri attribute del subject che contengano:
    	// - AttributeId="urn:oasis:names:tc:SAML:2.0:assertion/Subject/NameID il valore del NameID all'interno dell'elemento Subject 
    	// - AttributeId="urn:oasis:names:tc:SAML:2.0:assertion/Issuer il valore dell'elemento Issuer
    	// - AttributeId="urn:oasis:names:tc:SAML:2.0:assertion/AttributeStatement/Attribute/Name/<Nome> se il NameFormat non è una uri altrimenti direttamente il nome come attribute Id
    	
    	if(pdpLocal){
    		// TODO...
    	}
    	else{
    		try{
    			HttpResponseBody response = HttpUtilities.getHTTPResponse(remotePdD_url, remotePdD_readConnectionTimeout, remotePdD_connectionTimeout);
    			if(response.getResultHTTPOperation()==200){
    				byte[] res = response.getResponse();
    				// TODO analizzare xml risposta
    			}
    			else{
    				throw new Exception("invocazione fallita (http-return-code: "+response.getResultHTTPOperation()+")");
    			}
    		}catch(Exception e){
	    		throw new SecurityException("Errore avvenuto durante l'interrogazione del PdD remoto ["+remotePdD_url+"] per l'autorizzazione del servizio "+idServizio.toString()+": "+e.getMessage(),e);
	    	}
    	}
    	
    	MessageSecurityAuthorizationResult result = new MessageSecurityAuthorizationResult();
    	result.setAuthorized(true);
    	result.setErrorMessage(null);
    	return result;
    }
    
}
