/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.api.DefaultApi;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.model.ErrorMessage;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.model.MResponseType;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.model.MType;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.model.TaskStatus;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.model.TaskStatus.StatusEnum;
import org.openspcoop2.utils.id.UUIDUtilsGenerator;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;


/**
 * ServerImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerImpl implements DefaultApi {
	
	@Context 
	protected HttpServletResponse servletResponse;
	
	@Override
	public void pushMessage(@Valid MType body) {
		
		System.out.println("Ricevuta richiesta di elaborazione PULL");

    	if(body==null) {
    		String descrizione = "Dati richiesta non forniti";
    		Response response = Response.status(400).entity(toFault(400, descrizione)).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build();
    		throw new javax.ws.rs.WebApplicationException(descrizione,response);
    	}
    	
    	System.out.println("Parametri richiesta:");
    	System.out.println("\tA1-List: "+(body.getA()!=null ? body.getA().getA1s() : null));
    	System.out.println("\tA2: "+(body.getA()!=null ? body.getA().getA2() : null));
    	System.out.println("\tB: "+body.getB());
		
		try {
			this.servletResponse.setHeader("Location","/tasks/queue/"+ UUIDUtilsGenerator.newUUID());
		}catch(Exception e) {
    		String descrizione = "Generazione ID Correlazione fallita: "+e.getMessage();
    		Response response = Response.status(500).entity(toFault(500, descrizione)).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build();
    		throw new javax.ws.rs.WebApplicationException(descrizione,e,response);
		}
    	
    	this.servletResponse.setStatus(202);
    	
    	return;
		
	}
	
	@Override
	public TaskStatus checkStatus(String idOperazione) {
		
		System.out.println("Ricevuta richiesta stato PULL per idOperazione '"+idOperazione+"'");
		
		if(idOperazione==null) {
    		String descrizione = "IdOperazione non fornito";
    		Response response = Response.status(400).entity(toFault(400, descrizione)).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build();
    		throw new javax.ws.rs.WebApplicationException(descrizione,response);
    	}
		
		boolean risorsaNonAncoraPronta = idOperazione.contains("NOT_READY");
			
		TaskStatus status = new TaskStatus();
		
		if(risorsaNonAncoraPronta) {
			status.setStatus(StatusEnum.PENDING);
			status.setMessage("Richiesta in fase di processamento");
		}
		else {
			status.setStatus(StatusEnum.COMPLETED);
			status.setMessage("Processamento completo");
			this.servletResponse.setHeader("Location","/tasks/result/"+ idOperazione);
			this.servletResponse.setStatus(303);
		}
		
		return status;
		
	}

	@Override
	public MResponseType retriveResource(String idOperazione) {
		System.out.println("Ricevuta richiesta per ottenere la risposta con idOperazione '"+idOperazione+"'");
		
		if(idOperazione==null) {
    		String descrizione = "IdOperazione non fornito";
    		Response response = Response.status(400).entity(toFault(400, descrizione)).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build();
    		throw new javax.ws.rs.WebApplicationException(descrizione,response);
    	}
		
		MResponseType response = new MResponseType();
		response.setC("Risultato C");
		return response;
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

