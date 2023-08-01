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

package org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.logging.log4j.Level;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.model.AComplexType;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.model.ErrorMessage;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.model.MResponseType;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.model.MType;
import org.openspcoop2.protocol.modipa.example.rest.non_blocking.pull.model.TaskStatus;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.jaxrs.JacksonJsonProviderCustomized;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.service.authentication.filter.HTTPBasicAuthFilter;

/**
 * Client
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Client {

	public static void main(String[] args) throws Exception {
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		java.util.Properties reader = new java.util.Properties();
		try{  
			reader.load(new FileInputStream("Client.properties")); 
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String address = reader.getProperty("endpoint");
		if(address==null){
			throw new Exception("Property [endpoint] not definded");
		}
		else{
			address = address.trim();
		}
		if(address.endsWith("/")==false) {
			address = address + "/";
		}
			
		URL url = new URL(address);
		String basePath = url.getPath();
		
		String username = reader.getProperty("username");
		if(username!=null){
			username = username.trim();
		}
		String password = reader.getProperty("password");
		if(password!=null){
			password = password.trim();
		}
		
		jakarta.ws.rs.client.Client client = ClientBuilder.newClient().register(JacksonJsonProviderCustomized.class);
		if(username!=null && password!=null){
			System.out.println("Basic Auth username["+username+"] password["+password+"]");
			client.register(new HTTPBasicAuthFilter(username, password));
		}
		WebTarget target = client.target(address);
		
		String tipo = reader.getProperty("tipo");
		if(tipo==null){
			throw new Exception("Property [tipo] not definded");
		}
		else{
			tipo = tipo.trim();
		}
		
		String idOperazione = null;
		
		if("richiesta".equals(tipo) || "all".equals(tipo)) {
			
			System.out.println("\n===========================================");
			
			String path = "tasks/queue";

			System.out.println("Genero nuova richiesta applicativa (path:"+path+") ...");
			
			target = target.path(path);
			 
			Invocation.Builder builder = target.request();
			MType request = new MType();
			request.setA(new AComplexType());
			ArrayList<String> l = new ArrayList<>();
			l.add("1");
			l.add("2");
			request.getA().setA1s( l);
			request.getA().setA2("RGFuJ3MgVG9vbHMgYXJlIGNvb2wh");
			request.setB("Stringa di esempio");
			Response response = builder.post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE));
			System.out.println("Response code: "+response.getStatus());
			if(response.getStatus()==200) {
				System.out.println("ERRORE: Ricevuta risposta ok '200' non attesa");
			}
			else if(response.getStatus()==202) {
				System.out.println("Ricevuta risposta ok");
				
				idOperazione = response.getHeaderString("Location");
				System.out.println("Ricevuto id dell'operazione per recuperare lo stato: "+idOperazione);
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
		
		if("richiestaStato".equals(tipo) || "all".equals(tipo)) {
			
			String path = null;
			if("richiestaStato".equals(tipo)) {
				if(args!=null && args.length>0 && !"${idOperazione}".equals(args[0])) {
					idOperazione = args[0];
				}
				String codiceInput = reader.getProperty("idOperazione");
				if(codiceInput!=null) {
					idOperazione = codiceInput.trim();
				}
				if(idOperazione==null){
					throw new Exception("Property [idOperazione] not definded");
				}
				path = "tasks/queue/"+idOperazione;
			}
			else {
				if(idOperazione==null) {
					return;
				}
				if(idOperazione.startsWith(basePath)) {
					idOperazione = idOperazione.substring(basePath.length());
				}
				path = idOperazione;
			}
			

			// 1. verifico risorsa non ancora pronta prima e poi risorsa effettiva
			
			for (int i = 0; i < 2; i++) {
				
				System.out.println("\n===========================================");
								
				target = client.target(address);
				String pathFinale = path;
				if(i==0) {
					pathFinale = path+"_NOT_READY";
				}
				
				target = target.path(pathFinale);
								
				System.out.println("Genero nuova richiesta stato (path:"+pathFinale+") ...");
				
				Invocation.Builder builder = target.request();
				Response response = builder.get();
				System.out.println("Response code: "+response.getStatus());
				if(response.getEntity()!=null) {
					if(response.getStatus()==200) {
						System.out.println("Ricevuta esito che indica risposta non ancora pronta");
						TaskStatus stato =  response.readEntity(TaskStatus.class);
						System.out.println("Ricevuto stato '"+stato.getStatus()+"': "+stato.getMessage());
					}
					else if(response.getStatus()==303) {
						System.out.println("Ricevuta esito che indica risposta pronta");
						TaskStatus stato =  response.readEntity(TaskStatus.class);
						System.out.println("Ricevuto stato '"+stato.getStatus()+"': "+stato.getMessage());
						idOperazione = response.getHeaderString("Location");
						System.out.println("Ricevuto id dell'operazione per recuperare la risposta: "+idOperazione);
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
		
		
		if("risposta".equals(tipo) || "all".equals(tipo)) {
			
			System.out.println("\n===========================================");
						
			String path = null;
			if("risposta".equals(tipo)) {
				if(args!=null && args.length>0 && !"${idOperazione}".equals(args[0])) {
					idOperazione = args[0];
				}
				String codiceInput = reader.getProperty("idOperazione");
				if(codiceInput!=null) {
					idOperazione = codiceInput.trim();
				}
				if(idOperazione==null){
					throw new Exception("Property [idOperazione] not definded");
				}
				path = "tasks/result/"+idOperazione;
			}
			else {
				if(idOperazione==null) {
					return;
				}
				if(idOperazione.startsWith(basePath)) {
					idOperazione = idOperazione.substring(basePath.length());
				}
				path = idOperazione;
			}
			
			target = client.target(address);
						
			target = target.path(path);
			
			System.out.println("Recupero risposta applicativa (path:"+path+") ...");
			
			Invocation.Builder builder = target.request();
			Response response = builder.get();
			System.out.println("Response code: "+response.getStatus());
			if(response.getEntity()!=null) {
				if(response.getStatus()==200) {
					MResponseType stato =  response.readEntity(MResponseType.class);
					System.out.println("Ricevuto risposta: "+stato.getC());
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
}
