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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Invocazione della callback (fase callback_invocation) tramite la fruizione GovWay dell'API di callback.
 * 
 * L'identificativo dell'interazione e il numero di entità vengono forniti a GovWay tramite header HTTP;
 * GovWay negozia il voucher PDND (scope 'callback_invocation') e inoltra la richiesta alla URL di callback del fruitore.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CallbackNotifier {

	private final String url;
	private final int delaySeconds;
	private final String headerConversationId;
	private final String headerEntityNumber;
	private final String username;
	private final String password;
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
	
	public CallbackNotifier(ExampleProperties properties) {
		this.url = properties.getRequired("eservice.callback.url");
		this.delaySeconds = properties.getInt("eservice.callback.delaySeconds", 5);
		this.headerConversationId = properties.getOptional("eservice.callback.header.conversationId", "govway-conversation-id");
		this.headerEntityNumber = properties.getOptional("eservice.callback.header.entityNumber", "govway-pdnd-entity-number");
		this.username = properties.getOptional("eservice.callback.username", null);
		this.password = properties.getOptional("eservice.callback.password", null);
	}
	
	public void schedule(InteractionStore.Interaction interaction) {
		System.out.println("[callback] Invocazione della callback per l'interazione '"+interaction.getId()+"' fra "+this.delaySeconds+" secondi");
		this.executor.schedule(() -> invoke(interaction), this.delaySeconds, TimeUnit.SECONDS);
	}
	
	/**
	 * Invoca la callback e ritorna il codice HTTP della risposta (-1 se l'invocazione fallisce)
	 */
	public int invoke(InteractionStore.Interaction interaction) {
		try {
			ObjectNode notification = ProblemUtils.MAPPER.createObjectNode();
			notification.put("status", "READY");
			notification.put("entities", interaction.getEntities());
			notification.put("message", "Dati disponibili per '"+interaction.getSubject()+"'");
			
			HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(this.url))
					.timeout(Duration.ofSeconds(60))
					.header("content-type", "application/json")
					.header(this.headerConversationId, interaction.getId())
					.header(this.headerEntityNumber, String.valueOf(interaction.getEntities()))
					.POST(HttpRequest.BodyPublishers.ofString(notification.toString(), StandardCharsets.UTF_8));
			if(this.username!=null && this.password!=null) {
				builder.header("authorization", "Basic "+Base64.getEncoder().encodeToString((this.username+":"+this.password).getBytes(StandardCharsets.UTF_8)));
			}
			HttpResponse<String> response = this.client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
			System.out.println("[callback] Interazione '"+interaction.getId()+"': risposta HTTP "+response.statusCode()+" "+response.body());
			return response.statusCode();
		}catch(Exception e) {
			System.err.println("[callback] Interazione '"+interaction.getId()+"': invocazione fallita: "+e.getMessage());
			return -1;
		}
	}
}
