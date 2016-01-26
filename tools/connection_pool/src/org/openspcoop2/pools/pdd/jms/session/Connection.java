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

import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Topic;

/**
 * Connection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Connection implements javax.jms.Connection {

	
	private String jndiNamePool = null;
	private javax.jms.Connection connection = null;
	private javax.jms.Session sessionJMXOriginale = null;
	private org.openspcoop2.pools.pdd.jms.session.Session sessionOpenSPCoop = null;
	
	
	public Connection(String jndiNamePool,javax.jms.Connection connection,javax.jms.Session session){
		this.jndiNamePool = jndiNamePool;
		this.connection = connection;
		this.sessionJMXOriginale = session;
		this.sessionOpenSPCoop = new  org.openspcoop2.pools.pdd.jms.session.Session(session);
	}
	public Connection(javax.jms.Connection connection,javax.jms.Session session){
		this(null,connection,session);
	}
	
	
	/**
	 * @return the jndiNamePool
	 */
	protected String getJndiNamePool() {
		return this.jndiNamePool;
	}
	/**
	 * @param jndiNamePool the jndiNamePool to set
	 */
	protected void setJndiNamePool(String jndiNamePool) {
		this.jndiNamePool = jndiNamePool;
	}
	
	/**
	 * @return the connection
	 */
	protected javax.jms.Connection getConnection() {
		return this.connection;
	}
	/**
	 * @return the session
	 */
	protected javax.jms.Session getSession() {
		return this.sessionJMXOriginale;
	}
	
	public void close_ForzaRilascioRisorse() throws JMSException {
		try{
			this.sessionOpenSPCoop.close();
			this.sessionJMXOriginale.close();
			this.sessionOpenSPCoop = null;
			this.sessionJMXOriginale = null;
			SessionJMSPool.releaseResource(this);
		}catch(Exception e){
			throw new JMSException(e.getMessage());
		}
	}
	
	@Override
	public void close() throws JMSException {
		try{
			SessionJMSPool.releaseResource(this);
		}catch(Exception e){
			throw new JMSException(e.getMessage());
		}
	}

	@Override
	public ConnectionConsumer createConnectionConsumer(Destination arg0,
			String arg1, ServerSessionPool arg2, int arg3) throws JMSException {
		if(this.connection == null){
			throw new JMSException("Connection is null");
		}
		return this.connection.createConnectionConsumer(arg0, arg1, arg2, arg3);
	}

	@Override
	public ConnectionConsumer createDurableConnectionConsumer(Topic arg0,
			String arg1, String arg2, ServerSessionPool arg3, int arg4)
			throws JMSException {
		if(this.connection == null){
			throw new JMSException("Connection is null");
		}
		return this.connection.createDurableConnectionConsumer(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public javax.jms.Session createSession(boolean arg0, int arg1) throws JMSException {
		if(this.sessionOpenSPCoop == null){
			throw new JMSException("Session is null");
		}
		if(arg0){
			if(this.sessionOpenSPCoop.getTransacted()==false){
				throw new JMSException("Pool e' stato configurato per contenere sessioni con transazione automatica");
			}
		}else{
			if(this.sessionOpenSPCoop.getTransacted()){
				throw new JMSException("Pool e' stato configurato per contenere sessioni senza transazione automatica");
			}
		}
		
		if(this.sessionOpenSPCoop.getTransacted()==false){
			if(this.sessionOpenSPCoop.getAcknowledgeMode()!=arg1){
				
				String poolAck = "Valore non definito nello standard ("+this.sessionOpenSPCoop.getAcknowledgeMode()+")";
				if(javax.jms.Session.AUTO_ACKNOWLEDGE == this.sessionOpenSPCoop.getAcknowledgeMode())
					poolAck = "AUTO_ACKNOWLEDGE";
				else if(javax.jms.Session.CLIENT_ACKNOWLEDGE == this.sessionOpenSPCoop.getAcknowledgeMode())
					poolAck = "CLIENT_ACKNOWLEDGE";
				else if(javax.jms.Session.DUPS_OK_ACKNOWLEDGE == this.sessionOpenSPCoop.getAcknowledgeMode())
					poolAck = "DUPS_OK_ACKNOWLEDGE";
				
				String requestAck = "Valore non definito nello standard ("+arg1+")";
				if(javax.jms.Session.AUTO_ACKNOWLEDGE == arg1)
					requestAck = "AUTO_ACKNOWLEDGE";
				else if(javax.jms.Session.CLIENT_ACKNOWLEDGE == arg1)
					requestAck = "CLIENT_ACKNOWLEDGE";
				else if(javax.jms.Session.DUPS_OK_ACKNOWLEDGE == arg1)
					requestAck = "DUPS_OK_ACKNOWLEDGE";
				
				throw new JMSException("Pool e' stato configurato per contenere sessioni con AcknowledgeMode="+
						poolAck+" , la tua richiesta prevede sessioni con AcknowledgeMode="+requestAck+" \n");
			}
		}
		return this.sessionOpenSPCoop;
	}

	@Override
	public String getClientID() throws JMSException {
		if(this.connection == null){
			throw new JMSException("Connection is null");
		}
		return this.connection.getClientID();
	}

	@Override
	public ExceptionListener getExceptionListener() throws JMSException {
		if(this.connection == null){
			throw new JMSException("Connection is null");
		}
		return this.connection.getExceptionListener();
	}

	@Override
	public ConnectionMetaData getMetaData() throws JMSException {
		if(this.connection == null){
			throw new JMSException("Connection is null");
		}
		return this.connection.getMetaData();
	}

	@Override
	public void setClientID(String arg0) throws JMSException {
		throw new JMSException("Operazione non permessa dal session pool di OpenSPCoop");
	}

	@Override
	public void setExceptionListener(ExceptionListener arg0)
			throws JMSException {
		if(this.connection == null){
			throw new JMSException("Connection is null");
		}
		this.connection.setExceptionListener(arg0);
	}

	@Override
	public void start() throws JMSException {
		throw new JMSException("Operazione non permessa dal session pool di OpenSPCoop");
	}

	@Override
	public void stop() throws JMSException {
		throw new JMSException("Operazione non permessa dal session pool di OpenSPCoop");
	}

}
