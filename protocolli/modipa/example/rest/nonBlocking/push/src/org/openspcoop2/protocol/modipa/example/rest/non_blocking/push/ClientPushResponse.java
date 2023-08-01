/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.io.FileInputStream;
import java.io.InputStream;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.Level;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.client.model.ACKMessage;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.client.model.ErrorMessage;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.client.model.MResponseType;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.jaxrs.JacksonJsonProviderCustomized;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.service.authentication.filter.HTTPBasicAuthFilter;

/**
 * Server
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientPushResponse {

	public static void main(String[] args) throws Exception {
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Client.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String address = reader.getProperty("push.response.endpoint");
		if(address==null){
			throw new Exception("Property [endpoint] not definded");
		}
		else{
			address = address.trim();
		}
		if(address.endsWith("/")==false) {
			address = address + "/";
		}
		
		String idCorrelazione = null;
		if(args!=null && args.length>0 && !"${idCorrelazione}".equals(args[0])) {
			idCorrelazione = args[0];
		}
		String idCorrelazioneInput = reader.getProperty("push.response.correlationId");
		if(idCorrelazioneInput!=null) {
			idCorrelazione = idCorrelazioneInput.trim();
		}
//		if(idCorrelazione==null) {
//			throw new Exception("Property [correlationId] not definded");
//		}
		String path = "MResponse";
				
		System.out.println("Spedisco richiesta all'indirizzo (correlationId '"+idCorrelazione+"'): "+address+path);
		
		String username = reader.getProperty("push.response.username");
		if(username!=null){
			username = username.trim();
		}
		String password = reader.getProperty("push.response.password");
		if(password!=null){
			password = password.trim();
		}
		
		jakarta.ws.rs.client.Client client = ClientBuilder.newClient().register(JacksonJsonProviderCustomized.class);
		if(username!=null && password!=null){
			System.out.println("Basic Auth username["+username+"] password["+password+"]");
			client.register(new HTTPBasicAuthFilter(username, password));
		}
		WebTarget target = client.target(address);
		target = target.path(path);
		if(idCorrelazione==null) {
			// provo ad usare meccanismo di integrazione govway
			target = target.queryParam("govway_conversation_id", "XXGWXX");
			//target = target.queryParam("govway_relates_to", "XXGWXX");
		}
		 
		Invocation.Builder builder = target.request();
		
		if(idCorrelazione!=null) {
			builder = builder.header("X-Correlation-ID", idCorrelazione);
		}
				
		MResponseType rispostaAsincrona = new MResponseType();
		rispostaAsincrona.setC("OK");
		
		Response response = builder.post(Entity.entity(rispostaAsincrona, MediaType.APPLICATION_JSON_TYPE));
		System.out.println("Response code: "+response.getStatus());
		if(response.getEntity()!=null) {
			if(response.getStatus()==202) {
				System.out.println("ERRORE: Ricevuta risposta ok '202' non attesa");
			}
			else if(response.getStatus()==200) {
				ACKMessage ack =  response.readEntity(ACKMessage.class);
				System.out.println("Ricevuto ack: "+ack.getOutcome());
			}
			else {
				try {
					ErrorMessage m =  response.readEntity(ErrorMessage.class);
					System.out.println("Ricevuta risposta ko ("+m.getStatus()+"): "+m.getDetail());
				}catch(Exception e) {
					System.out.println("Ricevuta risposta ko: "+response.getEntity());
					if(response.getEntity() instanceof InputStream) {
						System.out.println(Utilities.getAsString((InputStream) response.getEntity(), Charset.UTF_8.getValue()));
					}
				}
			}
		}
		
	}
}
