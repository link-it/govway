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
package org.openspcoop2.testsuite.core;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageEOFException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.GestoreJNDI;

/**
 * Utility per la lettura dei messaggi pubblicati da GovWay tramite connettore JMS, condivise tra le testsuite dei protocolli.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JMSTestUtilities {

	private Properties jndiContext;
	private String connectionFactory;
	private String username;
	private String password;

	public JMSTestUtilities(Properties jndiContext, String connectionFactory, String username, String password) {
		this.jndiContext = new Properties();
		if(jndiContext!=null) {
			this.jndiContext.putAll(jndiContext);
		}
		this.connectionFactory = connectionFactory;
		this.username = username;
		this.password = password;
		if(this.username!=null && this.password!=null){
			this.jndiContext.put(Context.SECURITY_PRINCIPAL, this.username);
			this.jndiContext.put(Context.SECURITY_CREDENTIALS, this.password);
		}
	}



	/**
	 * Sottoscrizione ad una destinazione (coda o topic).
	 * Per un topic la sottoscrizione va creata prima della pubblicazione del messaggio, altrimenti il messaggio non viene ricevuto.
	 */
	public JMSConsumer createConsumer(String destinationJndiName) throws UtilsException, JMSException {
		GestoreJNDI gestoreJNDI = new GestoreJNDI(this.jndiContext);
		Destination d = (Destination) gestoreJNDI.lookup(destinationJndiName);
		ConnectionFactory cf = (ConnectionFactory) gestoreJNDI.lookup(this.connectionFactory);
		Connection c = null;
		if(this.username!=null && this.password!=null){
			c = cf.createConnection(this.username,this.password);
		}
		else{
			c = cf.createConnection();
		}
		JMSConsumer consumer = new JMSConsumer(destinationJndiName, c);
		try {
			consumer.session = c.createSession(false,Session.AUTO_ACKNOWLEDGE);
			consumer.consumer = consumer.session.createConsumer(d);
			c.start();
		}catch(JMSException e) {
			consumer.close();
			throw e;
		}
		return consumer;
	}

	/**
	 * Legge il primo messaggio presente sulla coda.
	 *
	 * @return il messaggio letto o null se entro il timeout non è disponibile alcun messaggio
	 */
	public Message receive(String queueJndiName, long timeoutMs) throws UtilsException, JMSException {
		try(JMSConsumer consumer = createConsumer(queueJndiName)){
			return consumer.receive(timeoutMs);
		}
	}

	/**
	 * Elimina gli eventuali messaggi residui sulla coda (es. di un'esecuzione precedente interrotta o fallita).
	 * I test su coda consumano il primo messaggio disponibile: un messaggio residuo verrebbe letto al posto di quello prodotto dal test,
	 * facendo fallire a cascata tutti i test successivi sulla stessa coda.
	 *
	 * @param idMessaggioProperty nome della proprietà JMS che contiene l'identificativo del messaggio, utilizzato solo per il log (può essere null)
	 * @return numero di messaggi eliminati
	 */
	public int svuotaCoda(String queueJndiName, String idMessaggioProperty) throws UtilsException, JMSException {
		int eliminati = 0;
		try(JMSConsumer consumer = createConsumer(queueJndiName)){
			Message msg = null;
			while( (msg = consumer.receive(500)) != null ){
				eliminati++;
				String id = null;
				if(idMessaggioProperty!=null) {
					try{
						id = msg.getStringProperty(idMessaggioProperty);
					}catch(JMSException e){
						// ignore
					}
				}
				System.out.println("WARNING: eliminato messaggio residuo dalla coda '"+queueJndiName+"' (id: "+id+")");
			}
		}
		if(eliminati>0){
			System.out.println("WARNING: eliminati "+eliminati+" messaggi residui dalla coda '"+queueJndiName+"'");
		}
		return eliminati;
	}



	/**
	 * Ritorna il contenuto del messaggio come bytes, verificando che il tipo del messaggio sia quello atteso.
	 */
	public static byte[] readContent(Message msg, boolean textMessageAtteso) throws TestSuiteException, JMSException {
		if(msg==null) {
			throw new TestSuiteException("Messaggio non presente");
		}
		if(textMessageAtteso) {
			if(!(msg instanceof TextMessage)) {
				throw new TestSuiteException("Atteso un TextMessage, ricevuto un messaggio di tipo '"+msg.getClass().getName()+"' (id: "+msg.getJMSMessageID()+")");
			}
			String text = ((TextMessage)msg).getText();
			return text!=null ? text.getBytes(StandardCharsets.UTF_8) : null;
		}
		else {
			if(!(msg instanceof BytesMessage)) {
				throw new TestSuiteException("Atteso un BytesMessage, ricevuto un messaggio di tipo '"+msg.getClass().getName()+"' (id: "+msg.getJMSMessageID()+")");
			}
			BytesMessage bytesMessage = (BytesMessage) msg;
			// riposiziona il body all'inizio: il contenuto di un BytesMessage è uno stream e va riletto se già letto in precedenza
			bytesMessage.reset();
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			boolean endStream = false;
			while(!endStream){
				try  {
					content.write(bytesMessage.readByte());
				}catch(MessageEOFException  end) {
					endStream = true;
				}
			}
			return content.toByteArray();
		}
	}

	/**
	 * Verifica che la proprietà JMS sia presente con il valore atteso.
	 */
	public static void checkProperty(Message msg, String key, String valoreAtteso) throws TestSuiteException {
		String value = null;
		try{
			value = msg.getStringProperty(key);
		}catch(JMSException e){
			throw new TestSuiteException("Proprieta' ["+key+"] non riscontrata nell'header di trasporto JMS: "+e.getMessage());
		}
		if(valoreAtteso==null) {
			if(value!=null) {
				throw new TestSuiteException("Proprieta' ["+key+"] presente nell'header di trasporto JMS ["+value+"] non attesa");
			}
		}
		else if(!valoreAtteso.equals(value)){
			throw new TestSuiteException("Proprieta' ["+key+"] presente nell'header di trasporto JMS ["+value+"] con un valore diverso da quello atteso["+valoreAtteso+"]");
		}
	}



	/**
	 * Consumer JMS su una destinazione; da chiudere al termine dell'utilizzo.
	 */
	public static class JMSConsumer implements AutoCloseable {

		private String destination;
		private Connection connection;
		private Session session;
		private MessageConsumer consumer;

		private JMSConsumer(String destination, Connection connection) {
			this.destination = destination;
			this.connection = connection;
		}

		public String getDestination() {
			return this.destination;
		}

		public Message receive(long timeoutMs) throws JMSException {
			return this.consumer.receive(timeoutMs);
		}

		@Override
		public void close() {
			try{
				if(this.consumer!=null) {
					this.consumer.close();
				}
			}catch(JMSException eClose){
				// ignore
			}
			try{
				if(this.session!=null) {
					this.session.close();
				}
			}catch(JMSException eClose){
				// ignore
			}
			try{
				if(this.connection!=null) {
					this.connection.stop();
				}
			}catch(JMSException eClose){
				// ignore
			}
			try{
				if(this.connection!=null) {
					this.connection.close();
				}
			}catch(JMSException eClose){
				// ignore
			}
		}
	}
}
