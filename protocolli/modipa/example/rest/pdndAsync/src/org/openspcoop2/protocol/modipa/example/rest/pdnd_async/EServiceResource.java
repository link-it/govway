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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Implementazione dell'e-service di esempio (versioni compatta ed estesa: la versione compatta utilizza un sottoinsieme delle risorse)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@Path("/")
public class EServiceResource {

	private static final String OUTCOME = "outcome";
	
	@Context
	private HttpHeaders headers;
	
	private final String headerConversationId;
	private final String headerUrlCallback;
	private final CallbackNotifier callbackNotifier;
	private final Map<String, String> catalog = new ConcurrentHashMap<>();
	
	public EServiceResource(String headerConversationId, String headerUrlCallback, CallbackNotifier callbackNotifier) {
		this.headerConversationId = headerConversationId;
		this.headerUrlCallback = headerUrlCallback;
		this.callbackNotifier = callbackNotifier;
		this.catalog.put("A01", "Elemento di esempio A01");
		this.catalog.put("B02", "Elemento di esempio B02");
	}
	
	
	/* **** Scambio di dati asincrono **** */
	
	@POST
	@Path("requests")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response startInteraction(String body) {
		String id = getConversationId("start_interaction");
		String urlCallback = this.headers.getHeaderString(this.headerUrlCallback);
		JsonNode request = readJson(body);
		String subject = request.hasNonNull("subject") ? request.get("subject").asText() : null;
		if(subject==null) {
			throw ProblemUtils.newProblem(400, "Bad Request", "Field 'subject' is required");
		}
		int entities = request.hasNonNull("entities") ? request.get("entities").asInt() : 10;
		
		InteractionStore.Interaction interaction = new InteractionStore.Interaction(id, subject, entities, urlCallback);
		InteractionStore.put(interaction);
		System.out.println("[start_interaction] Interazione '"+id+"' avviata (subject: '"+subject+"', entità: "+entities+", urlCallback: '"+urlCallback+"')");
		
		if(this.callbackNotifier!=null) {
			this.callbackNotifier.schedule(interaction);
		}
		
		ObjectNode outcome = ProblemUtils.MAPPER.createObjectNode();
		outcome.put(OUTCOME, "ACCEPTED");
		return Response.status(202).entity(outcome.toString()).build();
	}
	
	@GET
	@Path("results")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getResource(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit) {
		InteractionStore.Interaction interaction = getInteraction("get_resource");
		int o = offset!=null ? offset : 0;
		int l = limit!=null ? limit : 10;
		ObjectNode result = ProblemUtils.MAPPER.createObjectNode();
		result.put("total", interaction.getEntities());
		result.put("offset", o);
		ArrayNode items = result.putArray("items");
		for (int i = o; i < Math.min(o+l, interaction.getEntities()); i++) {
			ObjectNode item = items.addObject();
			item.put("id", i+1);
			item.put("value", interaction.getSubject()+" #"+(i+1));
		}
		System.out.println("[get_resource] Interazione '"+interaction.getId()+"': restituite "+items.size()+" entità (offset "+o+")");
		return Response.ok(result.toString()).build();
	}
	
	@POST
	@Path("results/confirmation")
	@Produces(MediaType.APPLICATION_JSON)
	public Response confirmation() {
		InteractionStore.Interaction interaction = getInteraction("confirmation");
		interaction.setConfirmed(true);
		System.out.println("[confirmation] Interazione '"+interaction.getId()+"' confermata");
		ObjectNode outcome = ProblemUtils.MAPPER.createObjectNode();
		outcome.put(OUTCOME, "CONFIRMED");
		return Response.ok(outcome.toString()).build();
	}
	
	
	/* **** Risorse sincrone (versione estesa) **** */
	
	@GET
	@Path("catalog")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findCatalogItems() {
		ArrayNode items = ProblemUtils.MAPPER.createArrayNode();
		this.catalog.forEach((code, description) -> items.addObject().put("code", code).put("description", description));
		return Response.ok(items.toString()).build();
	}
	
	@GET
	@Path("catalog/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCatalogItem(@PathParam("code") String code) {
		String description = this.catalog.get(code);
		if(description==null) {
			throw ProblemUtils.newProblem(404, "Not Found", "Catalog item '"+code+"' not found");
		}
		ObjectNode item = ProblemUtils.MAPPER.createObjectNode();
		item.put("code", code);
		item.put("description", description);
		return Response.ok(item.toString()).build();
	}
	
	@PUT
	@Path("catalog/{code}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateCatalogItem(@PathParam("code") String code, String body) {
		JsonNode item = readJson(body);
		this.catalog.put(code, item.hasNonNull("description") ? item.get("description").asText() : "");
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("catalog/{code}")
	public Response deleteCatalogItem(@PathParam("code") String code) {
		if(this.catalog.remove(code)==null) {
			throw ProblemUtils.newProblem(404, "Not Found", "Catalog item '"+code+"' not found");
		}
		return Response.noContent().build();
	}
	
	@GET
	@Path("health")
	@Produces(MediaType.APPLICATION_JSON)
	public Response health() {
		return Response.ok("{\"status\":\"UP\"}").build();
	}
	
	
	/* **** Utilities **** */
	
	private String getConversationId(String fase) {
		String id = this.headers.getHeaderString(this.headerConversationId);
		if(id==null || "".equals(id)) {
			throw ProblemUtils.newProblem(400, "Bad Request", "Header '"+this.headerConversationId+"' (interactionId) not provided for phase '"+fase+"'");
		}
		return id;
	}
	private InteractionStore.Interaction getInteraction(String fase) {
		String id = getConversationId(fase);
		InteractionStore.Interaction interaction = InteractionStore.get(id);
		if(interaction==null) {
			throw ProblemUtils.newProblem(404, "Not Found", "Interaction '"+id+"' not found");
		}
		return interaction;
	}
	private static JsonNode readJson(String body) {
		try {
			return ProblemUtils.MAPPER.readTree(body!=null ? body : "{}");
		}catch(Exception e) {
			throw ProblemUtils.newProblem(400, "Bad Request", "Invalid JSON: "+e.getMessage());
		}
	}
}
