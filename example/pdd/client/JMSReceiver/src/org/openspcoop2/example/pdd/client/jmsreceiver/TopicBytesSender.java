/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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


package org.openspcoop2.example.pdd.client.jmsreceiver;

import java.util.Properties;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * QueueBytesReceiver
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TopicBytesSender {


	public static void main(String[] args) throws Exception {


		Properties properties = new Properties();
		if("jboss7".equals(args[0])){
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			properties.put(Context.PROVIDER_URL, "remote://127.0.0.1:4447");
		}
		else if(args[0].startsWith("wildfly")){
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			properties.put(Context.PROVIDER_URL, "http-remoting://127.0.0.1:8080");
		}
		else{
			properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			properties.put(Context.URL_PKG_PREFIXES, "org.jnp.interfaces");
			properties.put(Context.PROVIDER_URL, "127.0.0.1");
		}
		
		String username = null;
		String password = null;
		if(args.length>2 && !"${username}".equals(args[1].trim()) && !"${password}".equals(args[1].trim())){
			username = args[1].trim();
			password = args[2].trim();
			System.out.println("Set autenticazione ["+username+"]["+password+"]");
			properties.put(Context.SECURITY_PRINCIPAL, username);
			properties.put(Context.SECURITY_CREDENTIALS, password);
		}
			
		InitialContext ctx = new InitialContext(properties);

		Topic queue = (Topic) ctx.lookup("topic/testOpenSPCoop2Topic");

		TopicConnectionFactory qcf = null;
		if("jboss7".equals(args[0])){
			qcf = (TopicConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
		}
		else if(args[0].startsWith("wildfly")){
			qcf = (TopicConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
		}
		else{
			qcf = (TopicConnectionFactory) ctx.lookup("ConnectionFactory");
		}
		
		TopicConnection qc = null;
		if(username!=null){
			qc = qcf.createTopicConnection(username,password);
		}else{
			qc = qcf.createTopicConnection();
		}

		TopicSession qs = null;
		TopicPublisher sender = null;
		try {

			qs = qc.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
			sender = qs.createPublisher(queue);

			System.out.println("Spedizione di un messaggio....");
			BytesMessage send = qs.createBytesMessage();
			
			String xml = "<prova>CIAO</prova>";
			send.writeBytes(xml.getBytes());
			
			try {
				send.setStringProperty("tipoMitt","SPC");
			}catch(Exception e){System.out.println("TipoMitt non impostato"); }
			try {
				send.setStringProperty("mitt","SoggettoMittente");
			}catch(Exception e){System.out.println("Mitt non impostato"); }
			
			try {
				send.setStringProperty("tipoSP","SPC");
			}catch(Exception e){System.out.println("tipoSP non impostato"); }
			try {
				send.setStringProperty("SP","SoggettoDestinatario");
			}catch(Exception e){System.out.println("SP non impostato"); }
			
			try {
				send.setStringProperty("tipoServizio","SPC");
			}catch(Exception e){System.out.println("tipoServizio non impostato"); }
			try {
				send.setStringProperty("servizio","ServizioProva");
			}catch(Exception e){System.out.println("servizio non impostato"); }
			
			try {
				send.setStringProperty("azione","AzioneProva");
			}catch(Exception e){System.out.println("azione non impostato"); }

			sender.send(send);
			
			System.out.println("Spedito xml ["+xml+"]");
			

		} finally {
			if(sender!=null) {
				sender.close();
			}
			if(qs!=null) {
				qs.close();
			}
			qc.close();
		}
	}
}


