/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import java.util.ArrayList;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.Level;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.server.model.ACKMessage;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.server.model.AComplexType;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.server.model.ErrorMessage;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.push.server.model.MType;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.jaxrs.JacksonJsonProvider;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.service.authentication.filter.HTTPBasicAuthFilter;

/**
 * Server
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientPushRequest {

	/*
	 *  Comando CURL: curl -X POST -H "Content-Type: application/json" --data @/tmp/request.json -v http://127.0.0.1:9000/resources/222/M
	 *  
	 *  dove /tmp/request.json:
	 *  O
	 *  {
			"a": {
				"a1s": [1,2],
				"a2": "RGFuJ3MgVG9vbHMgYXJlIGNvb2wh"
			},
			"b": "Stringa di esempio"
		}
	 *  
	 */

	public static void main(String[] args) throws Exception {
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Client.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String address = reader.getProperty("push.request.endpoint");
		if(address==null){
			throw new Exception("Property [endpoint] not definded");
		}
		else{
			address = address.trim();
		}
		if(address.endsWith("/")==false) {
			address = address + "/";
		}
		
		int codice = 222;
		if(args!=null && args.length>0 && !"${idResource}".equals(args[0])) {
			codice = Integer.valueOf(args[0]);
		}
		String codiceInput = reader.getProperty("push.request.idResource");
		if(codiceInput!=null) {
			codice = Integer.valueOf(codiceInput.trim());
		}
		String path = "resources/"+codice+"/M";
		
		System.out.println("Spedisco richiesta all'indirizzo: "+address+path);
		
		String username = reader.getProperty("push.request.username");
		if(username!=null){
			username = username.trim();
		}
		String password = reader.getProperty("push.request.password");
		if(password!=null){
			password = password.trim();
		}
		
		javax.ws.rs.client.Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
		if(username!=null && password!=null){
			System.out.println("Basic Auth username["+username+"] password["+password+"]");
			client.register(new HTTPBasicAuthFilter(username, password));
		}
		WebTarget target = client.target(address);
		target = target.path(path);
		 
		Invocation.Builder builder = target.request();
		
		String replyTo = reader.getProperty("push.request.replyTo");
		if(replyTo!=null){
			replyTo = replyTo.trim();
			builder = builder.header("X-ReplyTo", replyTo);
		}
		
		MType request = new MType();
		request.setA(new AComplexType());
		ArrayList<Integer> l = new ArrayList<Integer>();
		l.add(1);
		l.add(2);
		request.getA().setA1s( l);
		request.getA().setA2("RGFuJ3MgVG9vbHMgYXJlIGNvb2wh");
		request.setB("Stringa di esempio");
		Response response = builder.post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
		System.out.println("Response code: "+response.getStatus());
		if(response.getEntity()!=null) {
			if(response.getStatus()==200) {
				System.out.println("ERRORE: Ricevuta risposta ok '200' non attesa");
			}
			else if(response.getStatus()==202) {
				ACKMessage ack =  response.readEntity(ACKMessage.class);
				System.out.println("Ricevuto ack: "+ack.getOutcome());
				
				System.out.println("Ricevuto id di correlazione: "+response.getHeaderString("X-Correlation-ID"));
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
