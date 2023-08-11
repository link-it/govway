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


package org.openspcoop2.example.pdd.client.jmsreceiver;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.jms.BytesMessage;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageEOFException;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueConnectionFactory;
import jakarta.jms.QueueReceiver;
import jakarta.jms.QueueSession;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import jakarta.jms.TopicConnection;
import jakarta.jms.TopicConnectionFactory;
import jakarta.jms.TopicSession;
import jakarta.jms.TopicSubscriber;

/**
 * ReceiverUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReceiverUtilities {
	
	private ReceiverUtilities() {}

	public static void receive(String as, String username, String password, String queueName, boolean bytes) throws NamingException, JMSException {

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
		
		receive(queue, username, password, destination, qcf, tcf, bytes);

	}
	
	private static void receive(boolean queue, String username, String password, Destination destination,
			QueueConnectionFactory qcf, TopicConnectionFactory tcf, boolean bytes) throws JMSException {
		if(queue) {
			QueueConnection qc = null;
			if(username!=null){
				qc = qcf.createQueueConnection(username,password);
			}else{
				qc = qcf.createQueueConnection();
			}
			receive(qc, destination, bytes);
		}
		else {
			TopicConnection qc = null;
			if(username!=null){
				qc = tcf.createTopicConnection(username,password);
			}else{
				qc = tcf.createTopicConnection();
			}
			receive(qc, destination, bytes);
		}
	}
	
	private static void receive(QueueConnection qc, Destination destination, boolean bytes) throws JMSException {
		try (QueueSession qs = qc.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
				QueueReceiver receiver = qs.createReceiver((Queue)destination);
				){

				qc.start();

				receive(receiver, bytes);

			} finally {
				qc.stop();
				qc.close();
			}
	}
	
	private static void receive(TopicConnection qc, Destination destination, boolean bytes) throws JMSException {
		try (TopicSession qs = qc.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
				TopicSubscriber receiver = qs.createSubscriber((Topic)destination);
				){

				qc.start();

				receive(receiver, bytes);

			} finally {
				qc.stop();
				qc.close();
			}
	}
	
	private static void receive(MessageConsumer receiver, boolean bytes) throws JMSException {
		Utilities.info("Attesa di un messaggio....");
		
		Message received = receiver.receive();

		try {
			String proper = received.getStringProperty("tipoMitt");
			Utilities.info("Ricevuto tipoMitt ["+proper+"]");
		}catch(Exception e){Utilities.info("TipoMitt non presente"); }
		try {
			String proper = received.getStringProperty("mitt");
			Utilities.info("Ricevuto Mitt ["+proper+"]");
		}catch(Exception e){Utilities.info("Mitt non presente"); }	
		try {
			String proper = received.getStringProperty("tipoSP");
			Utilities.info("Ricevuto tipoSP ["+proper+"]");
		}catch(Exception e){Utilities.info("TipoSP non presente"); }
		try {
			String proper = received.getStringProperty("SP");
			Utilities.info("Ricevuto SP ["+proper+"]");
		}catch(Exception e){Utilities.info("SP non presente"); }	
		try {
			String proper = received.getStringProperty("servizio");
			Utilities.info("Ricevuto servizio ["+proper+"]");
		}catch(Exception e){Utilities.info("servizio non presente"); }
		try {
			String proper = received.getStringProperty("tipoServizio");
			Utilities.info("Ricevuto tipo servizio ["+proper+"]");
		}catch(Exception e){Utilities.info("tipo servizio non presente"); }	
		try {
			String proper = received.getStringProperty("azione");
			Utilities.info("Ricevuto azione ["+proper+"]");
		}catch(Exception e){Utilities.info("azione non presente"); }


		if(bytes) {
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			boolean endStream = false;
			while(!endStream){
				try  {
					content.write(((BytesMessage)received).readByte());
				}catch(MessageEOFException  end) { endStream = true;}
			}
			Utilities.info("Ricevuto xml ["+content.toString()+"]");
		}
		else {
			String receivedString = ((TextMessage)received).getText();
			Utilities.info("Ricevuto xml ["+receivedString+"]");
		}
	}
}


