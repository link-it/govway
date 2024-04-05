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

package org.openspcoop2.example.pdd.client.jmsreceiver;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.jms.BytesMessage;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueConnectionFactory;
import jakarta.jms.QueueSender;
import jakarta.jms.QueueSession;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import jakarta.jms.TopicConnection;
import jakarta.jms.TopicConnectionFactory;
import jakarta.jms.TopicPublisher;
import jakarta.jms.TopicSession;

/**
 * SenderUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SenderUtilities {
	
	private SenderUtilities() {}

	public static void send(String as, String username, String password, String queueName, boolean bytes) throws NamingException, JMSException {

		Properties properties = Utilities.initProperties(as, username, password);
			
		InitialContext ctx = new InitialContext(properties);

		Destination destination = (Destination) ctx.lookup(queueName);
		boolean queue = queueName.startsWith("queue/");

		ConnectionFactory cf = Utilities.getConnectionFactory(as, ctx);
		QueueConnectionFactory qcf = null;
		TopicConnectionFactory tcf = null;
		if(queue) {
			qcf = (QueueConnectionFactory) cf;
		}
		else {
			tcf = (TopicConnectionFactory) cf;
		}
		
		send(queue, username, password, destination, qcf, tcf, bytes);

	}
	
	private static void send(boolean queue, String username, String password, Destination destination,
			QueueConnectionFactory qcf, TopicConnectionFactory tcf, boolean bytes) throws JMSException {
		if(queue) {
			QueueConnection qc = null;
			if(username!=null){
				qc = qcf.createQueueConnection(username,password);
			}else{
				qc = qcf.createQueueConnection();
			}
			send(qc, destination, bytes);
		}
		else {
			TopicConnection qc = null;
			if(username!=null){
				qc = tcf.createTopicConnection(username,password);
			}else{
				qc = tcf.createTopicConnection();
			}
			send(qc, destination, bytes);
		}
	}
	
	private static void send(QueueConnection qc, Destination destination, boolean bytes) throws JMSException {
		try (QueueSession qs = qc.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
				QueueSender sender = qs.createSender((Queue)destination);
				){

				qc.start();

				send(qs, sender, bytes);

			} finally {
				qc.stop();
				qc.close();
			}
	}
	
	private static void send(TopicConnection qc, Destination destination, boolean bytes) throws JMSException {
		try (TopicSession qs = qc.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
				TopicPublisher sender = qs.createPublisher((Topic)destination);
				){

				qc.start();

				send(qs, sender, bytes);

			} finally {
				qc.stop();
				qc.close();
			}
	}
	
	private static void send(Session qs, MessageProducer sender, boolean bytes) throws JMSException {
		Utilities.info("Spedizione di un messaggio....");
		
		Message send = null;
		String xml = "<prova>CIAO</prova>";
		if(bytes) {
			send = qs.createBytesMessage();
			((BytesMessage)send).writeBytes(xml.getBytes());
		}
		else {
			send = qs.createTextMessage();
			((TextMessage)send).setText(xml);
		}
		
		try {
			send.setStringProperty("tipoMitt","SPC");
		}catch(Exception e){Utilities.info("TipoMitt non impostato"); }
		try {
			send.setStringProperty("mitt","SoggettoMittente");
		}catch(Exception e){Utilities.info("Mitt non impostato"); }
		
		try {
			send.setStringProperty("tipoSP","SPC");
		}catch(Exception e){Utilities.info("tipoSP non impostato"); }
		try {
			send.setStringProperty("SP","SoggettoDestinatario");
		}catch(Exception e){Utilities.info("SP non impostato"); }
		
		try {
			send.setStringProperty("tipoServizio","SPC");
		}catch(Exception e){Utilities.info("tipoServizio non impostato"); }
		try {
			send.setStringProperty("servizio","ServizioProva");
		}catch(Exception e){Utilities.info("servizio non impostato"); }
		
		try {
			send.setStringProperty("azione","AzioneProva");
		}catch(Exception e){Utilities.info("azione non impostato"); }

		sender.send(send);
		
		Utilities.info("Spedito xml ["+xml+"]");
		
	}
}


