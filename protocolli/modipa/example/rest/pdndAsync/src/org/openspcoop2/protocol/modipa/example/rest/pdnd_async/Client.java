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

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Client di esempio: invoca l'e-service tramite la fruizione GovWay.
 * 
 * Operazioni: start | getResource | confirmation | catalog | health
 * L'identificativo dell'interazione restituito da GovWay nella fase start (header GovWay-Conversation-ID)
 * deve essere fornito nelle fasi successive.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Client {

	public static void main(String[] args) throws Exception {
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		String operation = args.length>0 && !args[0].startsWith("$") ? args[0] : "start";
		String conversationId = args.length>1 && !args[1].startsWith("$") ? args[1] : null;
		
		ExampleProperties properties = new ExampleProperties("Client.properties");
		String baseUrl = properties.getRequired("eservice.url");
		if(baseUrl.endsWith("/")) {
			baseUrl = baseUrl.substring(0, baseUrl.length()-1);
		}
		String headerConversationId = properties.getOptional("header.conversationId", "govway-conversation-id");
		
		HttpRequest.Builder builder = null;
		switch (operation) {
		case "start":
			ObjectNode request = ProblemUtils.MAPPER.createObjectNode();
			request.put("subject", properties.getOptional("start.subject", "Richiesta di esempio"));
			request.put("entities", properties.getInt("start.entities", 10));
			builder = HttpRequest.newBuilder(URI.create(addUrlCallbackQuery(baseUrl+"/requests", properties)))
					.header("content-type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(request.toString(), StandardCharsets.UTF_8));
			addUrlCallbackHeader(builder, properties);
			break;
		case "getResource":
			builder = HttpRequest.newBuilder(URI.create(baseUrl+"/results?offset="+properties.getInt("getResource.offset", 0)+"&limit="+properties.getInt("getResource.limit", 10))).GET();
			break;
		case "confirmation":
			builder = HttpRequest.newBuilder(URI.create(baseUrl+"/results/confirmation")).POST(HttpRequest.BodyPublishers.noBody());
			break;
		case "catalog":
			builder = HttpRequest.newBuilder(URI.create(baseUrl+"/catalog")).GET();
			break;
		case "health":
			builder = HttpRequest.newBuilder(URI.create(baseUrl+"/health")).GET();
			break;
		default:
			throw new IllegalArgumentException("Operazione '"+operation+"' sconosciuta (attese: start, getResource, confirmation, catalog, health)");
		}
		if(conversationId!=null) {
			builder.header(headerConversationId, conversationId);
		}
		String username = properties.getOptional("username", null);
		String password = properties.getOptional("password", null);
		if(username!=null && password!=null) {
			builder.header("authorization", "Basic "+Base64.getEncoder().encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8)));
		}
		builder.timeout(Duration.ofSeconds(60));
		
		HttpResponse<String> response = HttpClient.newHttpClient().send(builder.build(), HttpResponse.BodyHandlers.ofString());
		System.out.println("Operazione '"+operation+"': HTTP "+response.statusCode());
		response.headers().firstValue("GovWay-Conversation-ID").ifPresent(v -> System.out.println("GovWay-Conversation-ID: "+v));
		response.headers().firstValue("GovWay-Transaction-ID").ifPresent(v -> System.out.println("GovWay-Transaction-ID: "+v));
		System.out.println(response.body());
	}

	/**
	 * URL di callback fornita dal client nella fase start_interaction (fruizione configurata con "URL di Callback" = "Fornita dal client"):
	 * viene inviata nell'header HTTP o nel parametro della URL indicati, con la codifica configurata nella fruizione (none, base64, hex)
	 */
	private static String getUrlCallbackValue(ExampleProperties properties) {
		String urlCallback = properties.getOptional("start.urlCallback", null);
		if(urlCallback==null || "".equals(urlCallback.trim())) {
			return null;
		}
		String codifica = properties.getOptional("start.urlCallback.codifica", "none");
		byte[] bytes = urlCallback.trim().getBytes(StandardCharsets.UTF_8);
		if("base64".equals(codifica)) {
			return Base64.getEncoder().encodeToString(bytes);
		}
		if("hex".equals(codifica)) {
			return java.util.HexFormat.of().formatHex(bytes);
		}
		return urlCallback.trim();
	}
	private static boolean isUrlCallbackQuery(ExampleProperties properties) {
		return "query".equals(properties.getOptional("start.urlCallback.modalita", "header"));
	}
	private static String getUrlCallbackNome(ExampleProperties properties) {
		return properties.getOptional("start.urlCallback.nome", isUrlCallbackQuery(properties) ? "govway_pdnd_url_callback" : "govway-pdnd-url-callback");
	}
	private static String addUrlCallbackQuery(String url, ExampleProperties properties) {
		String value = getUrlCallbackValue(properties);
		if(value==null || !isUrlCallbackQuery(properties)) {
			return url;
		}
		return url+(url.contains("?") ? "&" : "?")+getUrlCallbackNome(properties)+"="+java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
	}
	private static void addUrlCallbackHeader(HttpRequest.Builder builder, ExampleProperties properties) {
		String value = getUrlCallbackValue(properties);
		if(value!=null && !isUrlCallbackQuery(properties)) {
			builder.header(getUrlCallbackNome(properties), value);
		}
	}
}
