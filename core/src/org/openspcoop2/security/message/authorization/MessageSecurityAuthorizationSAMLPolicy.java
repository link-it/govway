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
    	
    	Busta busta = request.getBusta();
    	org.openspcoop2.security.message.MessageSecurityContext messageSecurityContext = request.getMessageSecurityContext();
    	OpenSPCoop2Message message = request.getMessage();
    	IDServizio idServizio = new IDServizio(busta.getTipoDestinatario(), busta.getDestinatario(), 
    			busta.getTipoServizio(), busta.getServizio());
    	
    	// Proprieta' WSSecurity
    	String actor = messageSecurityContext.getActor();
		if("".equals(messageSecurityContext.getActor()))
			actor = null;
    	
    	// Localizzo Policy
    	List<byte[]> policies = new ArrayList<>();
    	try{
	    	AccordoServizioParteSpecifica asps = RegistroServiziManager.getInstance().getAccordoServizioParteSpecifica(idServizio, null, true);
	    	// TODO
	    	for (int i = 0; i < asps.sizeSpecificaSicurezzaList(); i++) {
				Documento d = asps.getSpecificaSicurezza(i);
				if(TipiDocumentoSicurezza.WSPOLICY.getNome().equals(d.getTipo())){
					policies.add(d.getByteContenuto());	
				}
			}
    	}catch(Exception e){
    		throw new SecurityException("Errore durante la ricerca delle policies: "+e.getMessage(),e);
    	}
    	if(policies.size()<=0){
    		throw new SecurityException("Nessuna policy trovata per il servizio "+idServizio.toString());
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
    	
    	// Caricamento in PdD vedendo che la policy non sia gia stata caricata ....
    	// TODO...
    	
    	MessageSecurityAuthorizationResult result = new MessageSecurityAuthorizationResult();
    	result.setAuthorized(true);
    	result.setErrorMessage(null);
    	return result;
    }
    
}
