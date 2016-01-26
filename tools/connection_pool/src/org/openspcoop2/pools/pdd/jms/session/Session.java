/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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



package org.openspcoop2.pools.pdd.jms.session;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

/**
 * Session
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Session implements javax.jms.Session{

	private javax.jms.Session session = null;
	
	public Session(javax.jms.Session session){
		this.session = session;
	}

	@Override
	public void close() throws JMSException {
		// Non bisogna fare niente, viene gestita dal pool
	}

	@Override
	public void commit() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		this.session.commit();
	}

	@Override
	public QueueBrowser createBrowser(Queue arg0) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createBrowser(arg0);
	}

	@Override
	public QueueBrowser createBrowser(Queue arg0, String arg1) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createBrowser(arg0,arg1);
	}

	@Override
	public BytesMessage createBytesMessage() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createBytesMessage();
	}

	@Override
	public MessageConsumer createConsumer(Destination arg0) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createConsumer(arg0);
	}

	@Override
	public MessageConsumer createConsumer(Destination arg0, String arg1) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createConsumer(arg0, arg1);
	}

	@Override
	public MessageConsumer createConsumer(Destination arg0, String arg1, boolean arg2) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createConsumer(arg0, arg1, arg2);
	}

	@Override
	public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createDurableSubscriber(arg0, arg1);
	}

	@Override
	public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1, String arg2, boolean arg3) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createDurableSubscriber(arg0, arg1, arg2, arg3);
	}

	@Override
	public MapMessage createMapMessage() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createMapMessage();
	}

	@Override
	public Message createMessage() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createMessage();
	}

	@Override
	public ObjectMessage createObjectMessage() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createObjectMessage();
	}

	@Override
	public ObjectMessage createObjectMessage(Serializable arg0) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createObjectMessage(arg0);
	}

	@Override
	public MessageProducer createProducer(Destination arg0) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createProducer(arg0);
	}

	@Override
	public Queue createQueue(String arg0) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createQueue(arg0);
	}

	@Override
	public StreamMessage createStreamMessage() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createStreamMessage();
	}

	@Override
	public TemporaryQueue createTemporaryQueue() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createTemporaryQueue();
	}

	@Override
	public TemporaryTopic createTemporaryTopic() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createTemporaryTopic();
	}

	@Override
	public TextMessage createTextMessage() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createTextMessage();
	}

	@Override
	public TextMessage createTextMessage(String arg0) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createTextMessage(arg0);
	}

	@Override
	public Topic createTopic(String arg0) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.createTopic(arg0);
	}

	@Override
	public int getAcknowledgeMode() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.getAcknowledgeMode();
	}

	@Override
	public MessageListener getMessageListener() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.getMessageListener();
	}

	@Override
	public boolean getTransacted() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		return this.session.getTransacted();
	}

	@Override
	public void recover() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		this.session.recover();
	}

	@Override
	public void rollback() throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		this.session.rollback();
	}

	@Override
	public void run() {
		if(this.session != null){
			this.session.run();
		}
	}

	@Override
	public void setMessageListener(MessageListener arg0) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		this.session.setMessageListener(arg0);
	}

	@Override
	public void unsubscribe(String arg0) throws JMSException {
		if(this.session == null){
			throw new JMSException("Session is null");
		}
		this.session.unsubscribe(arg0);
	}
	
	
}
