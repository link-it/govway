/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.pdd.core;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.JMSObject;
import org.openspcoop2.pdd.config.QueueManager;
import org.openspcoop2.pdd.config.Resource;

/**
 * Classe utilizzata per spedire messaggi JMS ai componenti dell'architettura di OpenSPCoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JMSSender {

	/** QueueManager */
	private QueueManager qmanager;
	/** Indicazione sul modulo che utilizza il Sender  */
	private String idModulo = null;
	/** Indicazione sul codice porta del Sender */
	private IDSoggetto codicePorta = null;


	/** motivo di un eventuale errore */
	private String errore;
	/** eventuale eccezione */
	private Exception exception;


	/** Logger */
	private Logger log;
	
	/** IDTransazione */
	private String idTransazione;
	

	/**
	 * Costruttore
	 *
	 * @param aCodicePorta Codice del dominio che sta gestendo la richiesta.
	 * @param aIDModulo Identificativo del Sender.
	 * 
	 */
	public JMSSender(IDSoggetto aCodicePorta,String aIDModulo,Logger log,String idTransazione) throws Exception {
		this.codicePorta = aCodicePorta;
		this.idModulo = "JMSSender."+aIDModulo;
		this.qmanager = QueueManager.getInstance();
		this.log = log;
		this.idTransazione = idTransazione;
	}


	/**
	 * Spedizione di un messaggio  
	 *
	 * @param destinatario Nodo destinatario a cui spedire il messaggio. 
	 * @param objectToSend Oggetto da spedire.
	 * @param idBusta Identificativo da impostare come proprieta;
	 * @return true se la spedizione JMS e' andata a buon fine, false altrimenti.
	 * 
	 */
	public boolean send(String destinatario,java.io.Serializable objectToSend,String idBusta){
		java.util.Properties prop = new java.util.Properties();
		prop.put("ID",idBusta);
		return send(destinatario,objectToSend,prop);
	}
	/**
	 * Spedizione di un messaggio  
	 *
	 * @param destinatario Nodo destinatario a cui spedire il messaggio. 
	 * @param objectToSend Oggetto da spedire.
	 * @param properties Proprieta' da impostare.
	 * @return true se la spedizione JMS e' andata a buon fine, false altrimenti.
	 * 
	 */
	public boolean send(String destinatario,java.io.Serializable objectToSend,java.util.Properties properties){

		Resource resource = null;
		MessageProducer sender = null;
		try{

			if(destinatario == null)
				return true; // non deve essere spedito nulla.
			
			JMSObject jmsObject = null;
			try{
				resource = this.qmanager.getResource(this.codicePorta,this.idModulo,this.idTransazione);
				if(resource == null)
					throw new JMSException("Resource is null");
				if(resource.getResource() == null)
					throw new JMSException("JMSObject is null");
				jmsObject = (JMSObject) resource.getResource();
				if(jmsObject.getConnection()==null)
					throw new Exception("Connessione is null");
				if(jmsObject.getSession()==null)
					throw new Exception("Sessione is null");
			}catch(Exception e){
				this.log.error("JMSObject non ottenibile dal Pool: "+e.getMessage(),e);
				this.errore ="JMSObject non ottenibile dal Pool: "+e.getMessage();
				this.exception = e;
				return false;
			}


			// Get Coda
			Queue queue = this.qmanager.getQueue(destinatario);
			if(queue == null){
				this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
				this.errore="La coda ["+destinatario+"] non e' tra quelle registrate per OpenSPCoop";
				return false;
			}

			// Sender
			try{
				sender = jmsObject.getSession().createProducer(queue);
			}catch(javax.jms.JMSException e){
				this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
				this.log.error("Riscontrato errore durante la creazione del sender ["+destinatario+"] :"+e.getMessage(),e);
				this.errore = "Riscontrato errore durante la creazione del sender ["+destinatario+"] :"+e.getMessage();
				this.exception = e;
				return false;
			}

			// Oggetto da Spedire
			ObjectMessage message = jmsObject.getSession().createObjectMessage(objectToSend);
			java.util.Enumeration<?> en = properties.propertyNames();
			for (; en.hasMoreElements() ;) {
				String key = (String) en.nextElement();
				String value = properties.getProperty(key);
				// Controllo boolean
				if( "true".equalsIgnoreCase(value) ){
					message.setBooleanProperty(key,true);
				}else if("false".equalsIgnoreCase(value)){
					message.setBooleanProperty(key,false);
				}else{
					Long testLong = null;
					try{
						testLong = new Long(value);
					}catch(Exception e){
						testLong = null;
					}
					// controllo long
					if(testLong != null)
						message.setLongProperty(key,testLong.longValue());
					// string
					else{
						message.setStringProperty(key,value);
					}
				}
			}

			// Send Message
			sender.send(message);
			
			//	Rilascio producer
			sender.close();

			this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
			return true;

		}catch(Exception e){
			//	Rilascio producer
			try{
				if(sender!=null)
					sender.close();
			}catch(Exception eClose){}
			if(resource!=null){
				try{
					this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
				}catch(Exception eClose){}	
			}
			this.log.error("Riscontrato errore durante la creazione/spedizione dell'oggetto :"+e.getMessage(),e);
			this.errore = "Riscontrato errore durante la creazione/spedizione dell'oggetto :"+e.getMessage();
			this.exception = e;
			return false;
		}

	}

	/**
	 * In caso di avvenuto errore in fase di consegna, questo metodo ritorna il motivo dell'errore.
	 *
	 * @return motivo dell'errore (se avvenuto in fase di consegna).
	 * 
	 */
	public String getErrore(){
		return this.errore;
	}

	
	public Exception getException() {
		return this.exception;
	}
}





