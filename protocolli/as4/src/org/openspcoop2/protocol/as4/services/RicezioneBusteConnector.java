/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.HashMap;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.pdd.services.connector.ConnectorUtils;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.services.service.RicezioneBusteService;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.services.message.AS4ConnectorInMessage;
import org.openspcoop2.protocol.as4.services.message.AS4ConnectorOutMessage;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.threads.IRunnableInstance;
import org.openspcoop2.utils.threads.RunnableLogger;
import org.openspcoop2.utils.transport.jms.ExceptionListenerJMS;

/**
 * RicezioneBusteConnector
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteConnector implements IRunnableInstance{

	private RunnableLogger log;
	private AS4Properties asProperties;

	// JMS
	private QueueReceiver receiver = null;
	private Queue queue = null;
	private QueueConnectionFactory qcf = null;
	private QueueConnection qc = null;
	private QueueSession qs = null;
	private ExceptionListenerJMS exceptionListenerJMS = new ExceptionListenerJMS();
	private boolean riconnessioneConErrore = false;
	
	public RicezioneBusteConnector(RunnableLogger runnableLog,AS4Properties asProperties) throws Exception {
		this.log = runnableLog;
		this.asProperties = asProperties;
		
		this.initJMS();
	}
	
	
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
		
			Message received = (Message) this.receiver.receive(1); // sblocco immediatamente se non e' presente un messsaggio
			if(received==null) {
				this.log.debug("Non sono presenti messaggi in coda");
				return;
			}
			
			if(received instanceof MapMessage) {
				MapMessage map = (MapMessage) received;
				
				RicezioneBusteConnettoreUtils utils = new RicezioneBusteConnettoreUtils(this.log);
				
				if(this.asProperties.isDomibusGatewayJMS_debug()) {
					utils.debug(map);
				}
				
				HashMap<String, byte[]> content = new HashMap<String, byte[]>();
				UserMessage userMessage = new UserMessage();
				utils.fillUserMessage(map, userMessage, content);
				
								
				AS4ConnectorInMessage as4In = null;
				try{
					as4In = new AS4ConnectorInMessage(userMessage, content);
				}catch(Exception e){
					ConnectorUtils.getErrorLog().error("AS4ConnectorInMessage init error: "+e.getMessage(),e);
					throw new ServletException(e.getMessage(),e);
				}
				
				IProtocolFactory<?> protocolFactory = null;
				try{
					protocolFactory = as4In.getProtocolFactory();
				}catch(Throwable e){}
				
				AS4ConnectorOutMessage as4Out = null;
				try{
					as4Out = new AS4ConnectorOutMessage();
				}catch(Exception e){
					ConnectorUtils.getErrorLog().error("AS4ConnectorOutMessage init error: "+e.getMessage(),e);
					throw new ServletException(e.getMessage(),e);
				}
					
				RicezioneBusteExternalErrorGenerator generatoreErrore = null;
				try{
					generatoreErrore = 
							new RicezioneBusteExternalErrorGenerator(protocolFactory.getLogger(),
									org.openspcoop2.pdd.services.connector.RicezioneBusteConnector.ID_MODULO, as4In.getRequestInfo(), null);
				}catch(Exception e){
					throw new Exception("Inizializzazione Generatore Errore fallita: "+Utilities.readFirstErrorValidMessageFromException(e),e);
				}
					
				
				RicezioneBusteService ricezioneBuste = new RicezioneBusteService(generatoreErrore);
				
				try{
					ricezioneBuste.process(as4In, as4Out);
				}catch(Exception e){
					ConnectorUtils.getErrorLog().error("RicezioneContenutiApplicativi.process error: "+e.getMessage(),e);
					throw new ServletException(e.getMessage(),e);
				}
				
				if(as4Out.getResponseStatus()!=200 && as4Out.getResponseStatus()!=202) {
					throw new Exception("Servizio Ricezione Buste terminato con codice: "+as4Out.getResponseStatus());
				}
				if(as4Out.getMessage()!=null) {
					OpenSPCoop2SoapMessage soapMsg = as4Out.getMessage().castAsSoap();
					if(soapMsg.getSOAPBody()!=null && soapMsg.getSOAPBody().hasFault()) {
						throw new Exception("Servizio Ricezione Buste terminato con un soapFault: "+
								SoapUtils.toString(soapMsg.getSOAPBody().getFault()));
					}
				}
				
				this.qs.commit();
			}
			else {
				throw new Exception("Tipo di messaggio ["+received.getClass().getName()+"] non atteso");
			}
			
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
		
		Properties pJNDI = this.asProperties.getDomibusGatewayJMS_jndiContext();
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
		
		this.queue = (Queue) ctx.lookup(this.asProperties.getDomibusGatewayJMS_queueReceiver());
		
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
