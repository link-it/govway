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



package org.openspcoop2.protocol.as4.services;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.threads.IRunnableInstance;
import org.openspcoop2.utils.threads.RunnableLogger;
import org.openspcoop2.utils.transport.jms.ExceptionListenerJMS;

/**
 * AbstractRicezioneConnector
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractRicezioneConnector implements IRunnableInstance{

	private boolean ricezioneNotifiche;
	
	protected RunnableLogger log;
	protected AS4Properties asProperties;

	// JMS
	private QueueReceiver receiver = null;
	private Queue queue = null;
	private QueueConnectionFactory qcf = null;
	private QueueConnection qc = null;
	private QueueSession qs = null;
	private ExceptionListenerJMS exceptionListenerJMS = new ExceptionListenerJMS();
	private boolean riconnessioneConErrore = false;
	
	public AbstractRicezioneConnector(RunnableLogger runnableLog,AS4Properties asProperties, boolean ricezioneNotifiche) throws Exception {
		this.log = runnableLog;
		this.asProperties = asProperties;
		this.ricezioneNotifiche = ricezioneNotifiche;
		
		try {
			this.initJMS();
		} catch (Exception er) {
			this.log.error(": Re-Inizializzazione Receiver non effettuata:" + er.toString());
			this.riconnessioneConErrore = true;
		}
	}
	
	
	protected abstract void check(Message map) throws Exception;
	
	@Override
	public void check() throws UtilsException {
		
		try {

			// riconnessione precedente non riuscita.....
			if (this.riconnessioneConErrore) {
				throw new JMSException("RiconnessioneJMS non riuscita...");
			}

			// Controllo ExceptionListenerJMS
			if (this.exceptionListenerJMS.isConnessioneCorrotta()) {
				this.log.error("ExceptionJMSListener ha rilevato una connessione jms corrotta", this.exceptionListenerJMS.getException());
				throw new JMSException("ExceptionJMSListener ha rilevato una connessione jms corrotta: " + this.exceptionListenerJMS.getException().getMessage());
			}
		
			Message received = (Message) this.receiver.receive(1); // sblocco immediatamente se non e' presente un messaggio
			if(received==null) {
				this.log.debug("Non sono presenti messaggi in coda");
				return;
			}
			
			if(this.asProperties.isDomibusGatewayJMS_debug()) {
				BaseConnettoreUtils utils = new BaseConnettoreUtils(this.log);
				utils.debug(received);
			}
			
			this.check(received);
				
			this.qs.commit();
			
		} catch (JMSException e) {
			try {
				this.qs.rollback();
			} catch (Exception er) {
			}

			this.log.error("Riscontrato errore JMS durante la gestione di una richiesta: " + e.toString(),e);
			try {
				Utilities.sleep(5000);
				this.log.debug(": Re-Inizializzazione Receiver ...");
				
				this.closeJMS();
				
				// Ripristino stato Exception Listener
				if (this.exceptionListenerJMS.isConnessioneCorrotta()) {
					this.exceptionListenerJMS.setConnessioneCorrotta(false);
					this.exceptionListenerJMS.setException(null);
				}
				this.initJMS();
				
				this.log.debug(": Re-Inizializzazione Receiver effettuata.");
				this.riconnessioneConErrore = false;
			} catch (Exception er) {
				this.log.error(": Re-Inizializzazione Receiver non effettuata:" + er.toString());
				this.riconnessioneConErrore = true;
			}
		} catch (Exception e) {
			try {
				this.qs.rollback();
			} catch (Exception er) {
			}
			this.log.error("Riscontrato errore durante la gestione di una richiesta: " + e.toString(),e);
		}
			
	}
	
	
	private void initJMS() throws Exception {
		
		Properties pJNDI = null;
		if(this.ricezioneNotifiche) {
			pJNDI = this.asProperties.getDomibusGatewayJMS_jndiContextForAck();
		}
		else {
			pJNDI = this.asProperties.getDomibusGatewayJMS_jndiContext();
		}
		String username = this.asProperties.getDomibusGatewayJMS_username();
		String password = this.asProperties.getDomibusGatewayJMS_password();
		if(username!=null && password!=null){
			this.log.debug("Set autenticazione ["+username+"]["+password+"]");
			pJNDI.put(Context.SECURITY_PRINCIPAL, username);
			pJNDI.put(Context.SECURITY_CREDENTIALS, password);
		}
		
		InitialContext ctx = new InitialContext(pJNDI);

		this.qcf = (QueueConnectionFactory) ctx.lookup(this.asProperties.getDomibusGatewayJMS_connectionFactory());
		
		if(username!=null){
			this.qc = this.qcf.createQueueConnection(username,password);
		}else{
			this.qc = this.qcf.createQueueConnection();
		}
		this.qc.setExceptionListener(this.exceptionListenerJMS);
		
		this.queue = null;
		String nomeQueue = null;
		if(this.ricezioneNotifiche) {
			nomeQueue = this.asProperties.getDomibusGatewayJMS_queueSender();
		}else {
			nomeQueue = this.asProperties.getDomibusGatewayJMS_queueReceiver();
		}
		this.queue = (Queue) ctx.lookup(nomeQueue);
		
		this.qs = this.qc.createQueueSession(true, -1);
		this.receiver = this.qs.createReceiver(this.queue);
		this.qc.start();
	}
	
	private void closeJMS() throws Exception {
		try {
			this.receiver.close();
		} catch (Exception eclose) {
		}
		try {
			this.qs.close();
		} catch (Exception eclose) {
		}
		try {
			this.qc.stop();
		} catch (Exception eclose) {
		}
		try {
			this.qc.close();
		} catch (Exception eclose) {
		}
	}
}
