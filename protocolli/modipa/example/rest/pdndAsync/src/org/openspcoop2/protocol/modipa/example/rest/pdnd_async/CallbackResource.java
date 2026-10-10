/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.example.rest.pdnd_async;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Implementazione dell'API di callback di esempio (versioni compatta ed estesa)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@Path("/")
public class CallbackResource {

	private static final String ACK = "{\"outcome\":\"ACK\"}";
	
	@Context
	private HttpHeaders headers;
	
	private final String headerConversationId;
	
	public CallbackResource(String headerConversationId) {
		this.headerConversationId = headerConversationId;
	}
	
	@POST
	@Path("notifications")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response callbackInvocation(String body) {
		String id = this.headers.getHeaderString(this.headerConversationId);
		System.out.println("[callback_invocation] Notifica ricevuta per l'interazione '"+id+"': "+body);
		System.out.println("[callback_invocation] I dati possono essere ottenuti con: ant runClient -Doperation=getResource -DconversationId="+id);
		return Response.ok(ACK).build();
	}
	
	@POST
	@Path("notifications/errors")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response notifyError(String body) {
		System.out.println("[errors] Segnalazione ricevuta: "+body);
		return Response.ok(ACK).build();
	}
	
	@GET
	@Path("health")
	@Produces(MediaType.APPLICATION_JSON)
	public Response health() {
		return Response.ok("{\"status\":\"UP\"}").build();
	}
}
