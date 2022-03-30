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
package org.openspcoop2.protocol.modipa.example.rest.non_blocking.push;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.server.api.DefaultApi;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.server.model.ACKMessage;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.server.model.ErrorMessage;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.server.model.MType;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;


/**
 * ServerPushRequestImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerPushRequestImpl implements DefaultApi {
	
	@Context 
	protected HttpServletResponse servletResponse;
	
	private boolean generateCorrelationId = false;
	private String correlationId = "X-Correlation-ID";
	public void setGenerateCorrelationId(boolean generateCorrelationId) {
		this.generateCorrelationId = generateCorrelationId;
	}
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
		
	@Override
	public ACKMessage pushMessage(Integer idResource, @Valid MType body, String xReplyTo) {
		
		System.out.println("Ricevuta richiesta per risorsa '"+idResource+"'");
		System.out.println("Risposta da ritornare all'indirizzo replyTo: '"+xReplyTo+"'");
		
		if(idResource==null || idResource<=0) {
    		String descrizione = "IdRisorsa non fornito";
    		Response response = Response.status(400).entity(toFault(400, descrizione)).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build();
    		throw new javax.ws.rs.WebApplicationException(descrizione,response);
    	}
    	if(body==null) {
    		String descrizione = "Dati richiesta non forniti";
    		Response response = Response.status(400).entity(toFault(400, descrizione)).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build();
    		throw new javax.ws.rs.WebApplicationException(descrizione,response);
    	}
    	
    	System.out.println("Parametri richiesta:");
    	System.out.println("\tA1-List: "+(body.getA()!=null ? body.getA().getA1s() : null));
    	System.out.println("\tA2: "+(body.getA()!=null ? body.getA().getA2() : null));
    	System.out.println("\tB: "+body.getB());
		
    	ACKMessage ackMessage = new ACKMessage();
    	ackMessage.setOutcome("ACK");
    	
    	if(this.generateCorrelationId) {
    		try {
    			this.servletResponse.setHeader(this.correlationId, UUIDUtilsGenerator.newUUID());
    		}catch(Exception e) {
        		String descrizione = "Generazione ID Correlazione fallita: "+e.getMessage();
        		Response response = Response.status(500).entity(toFault(500, descrizione)).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build();
        		throw new javax.ws.rs.WebApplicationException(descrizione,e,response);
    		}
    	}
    	
    	this.servletResponse.setStatus(202);
    	
    	return ackMessage;
	}
	
	public ErrorMessage toFault(int code, String descrizione) {
		ErrorMessage problem = new ErrorMessage();
		problem.setType("https://httpstatuses.com/"+code);
		problem.setStatus(code);
		problem.setTitle(HttpUtilities.getHttpReason(code));
		problem.setDetail(descrizione);
		return problem;
	}


}

