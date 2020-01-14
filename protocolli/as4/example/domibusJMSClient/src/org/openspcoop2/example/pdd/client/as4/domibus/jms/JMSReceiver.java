/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.example.pdd.client.as4.domibus.jms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;


/**
 * QueueBytesReceiver
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JMSReceiver {


	public static void main(String[] args) throws Exception {

		java.util.Properties reader = new java.util.Properties();
		try{  
			InputStreamReader isr = new InputStreamReader(
				    new FileInputStream("DomibusClient.properties"), "UTF-8");
			reader.load(isr);
		}catch(java.io.IOException e) {
			System.err.println("ERROR : "+e.toString());
			return;
		}
		
		String tipoOperazione = reader.getProperty("as4.tipoOperazione");
		if(tipoOperazione == null){
			System.err.println("ERROR : Tipo Operazione non definita all'interno del file 'DomibusClient.properties'");
			return;
		}
		tipoOperazione = tipoOperazione.trim();
		
		String connectionFactory = reader.getProperty("as4.connectionFactory");
		if(connectionFactory == null){
			System.err.println("ERROR : ConnectionFactory non definita all'interno del file 'DomibusClient.properties'");
			return;
		}
		connectionFactory = connectionFactory.trim();
		
		String queueName = reader.getProperty("as4.queue");
		if(queueName == null){
			System.err.println("ERROR : Queue non definita all'interno del file 'DomibusClient.properties'");
			return;
		}
		queueName = queueName.trim();
		
		String consumeString = reader.getProperty("as4.consumeMessage");
		if(consumeString == null){
			System.err.println("ERROR : Opzione consumeMessage non definita all'interno del file 'DomibusClient.properties'");
			return;
		}
		boolean consume = Boolean.parseBoolean(consumeString.trim());
		
		
		
		Properties propertiesCtxJndi = readProperties("as4.jndi.", reader);
		
		String username =reader.getProperty("as4.username");
		if(username!=null){
			username = username.trim();
		}
		String password =reader.getProperty("as4.password");
		if(password!=null){
			password = password.trim();
		}
		if(username!=null && password!=null){
			System.out.println("Set autenticazione ["+username+"]["+password+"]");
			propertiesCtxJndi.put(Context.SECURITY_PRINCIPAL, username);
			propertiesCtxJndi.put(Context.SECURITY_CREDENTIALS, password);
		}
			
		InitialContext ctx = new InitialContext(propertiesCtxJndi);

		QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup(connectionFactory);

		QueueConnection qc = null;
		if(username!=null){
			qc = qcf.createQueueConnection(username,password);
		}else{
			qc = qcf.createQueueConnection();
		}
		
		Queue queue = (Queue) ctx.lookup(queueName);

		QueueSession qs = null;
		QueueReceiver receiver = null;
		try {

			if(consume) {
				qs = qc.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
			}
			else {
				qs = qc.createQueueSession(true,Session.CLIENT_ACKNOWLEDGE);
			}
			receiver = qs.createReceiver(queue);

			qc.start();

			System.out.println("Attesa di un messaggio from ["+queueName+"]["+queue+"]....");
			Message received = (Message) receiver.receive(5000);
			if(received==null) {
				System.out.println("Nessun messaggio presente in coda");
				return;
			}
			if(received instanceof MapMessage) {
				MapMessage map = (MapMessage) received;
				Enumeration<?> mapNames = map.getMapNames();
				while (mapNames.hasMoreElements()) {
					Object name = (Object) mapNames.nextElement();
					if(name instanceof String) {
						String key = (String) name;
						Object value = received.getObjectProperty(key);
						if(value!=null) {
							System.out.println("\t-Map["+key+"]("+value.getClass().getName()+"): "+value);
						}
						else {
							byte[] bytes = map.getBytes(key);
							if(bytes!=null) {
								File f = File.createTempFile("content", ".bin");
								FileOutputStream fos =new FileOutputStream(f);
								fos.write(bytes);
								fos.flush();
								fos.close();
								System.out.println("\t-Map["+key+"] scritto in "+f.getAbsolutePath());
							}
							else {
								System.out.println("\t-Map["+key+"]: "+value);
							}
						}
					}
					else {
						System.out.println("\t-Map con key diverso dal tipo String: "+name);
					}
				}
			}
			else if(received instanceof TextMessage) {
				TextMessage text = (TextMessage) received;
				if(text.getText()!=null) {
					System.out.println("ACK:" +text.getText());
				}
			}
			
			System.out.println("Ricevuto msg: "+received.getJMSMessageID());
			System.out.println("Ricevuto msg: "+received.getClass().getName());
			Enumeration<?> en = received.getPropertyNames();
			while (en.hasMoreElements()) {
				Object name = (Object) en.nextElement();
				if(name instanceof String) {
					String key = (String) name;
					Object value = received.getObjectProperty(key);
					if(value!=null) {
						System.out.println("\t-Property["+key+"]("+value.getClass().getName()+"): "+value);
					}
					else {
						System.out.println("\t-Property["+key+"]: "+value);
					}
				}
				else {
					System.out.println("\t-Property con key diverso dal tipo String: "+name);
				}
			}
			
		} finally {
			if(consume==false) {
				try {
					if(qs!=null) {
						qs.rollback();
					}
				}catch(Exception e) {
					System.out.println("Errore rollback receiver "+e.getMessage());
				}
			}
			try {
				receiver.close();
			}catch(Exception e) {
				System.out.println("Errore chiusura receiver "+e.getMessage());
			}
			try {
				qs.close();
			}catch(Exception e) {
				System.out.println("Errore chiusura qs "+e.getMessage());
			}
			try {
				qc.stop();
			}catch(Exception e) {
				System.out.println("Errore chiusura qc "+e.getMessage());
			}
			try {
				qc.close();
			}catch(Exception e) {
				System.out.println("Errore chiusura qc "+e.getMessage());
			}

		}
	}
	
	private static java.util.Properties readProperties (String prefix,java.util.Properties sorgente)throws Exception{
		java.util.Properties prop = new java.util.Properties();
		try{ 
			java.util.Enumeration<?> en = sorgente.propertyNames();
			for (; en.hasMoreElements() ;) {
				String property = (String) en.nextElement();
				if(property.startsWith(prefix)){
					String key = (property.substring(prefix.length()));
					if(key != null)
						key = key.trim();
					String value = sorgente.getProperty(property);
					if(value!=null)
						value = value.trim();
					if(key!=null && value!=null)
						prop.setProperty(key,value);
				}
			}
			return prop;
		}catch(java.lang.Exception e) {
			throw new Exception("Riscontrato errore durante la lettura delle proprieta' con prefisso ["+prefix+"]: "+e.getMessage(),e);
		}  
	}
}


