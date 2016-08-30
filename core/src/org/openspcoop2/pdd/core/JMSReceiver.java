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



package org.openspcoop2.pdd.core;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.MessageConsumer;
import javax.jms.Queue;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.JMSObject;
import org.openspcoop2.pdd.config.QueueManager;
import org.openspcoop2.pdd.config.Resource;

/**
 * Classe utilizzata per ricevere messaggi JMS dai componenti dell'architettura di OpenSPCoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JMSReceiver {

	
	/** QueueManager */
	private QueueManager qmanager;
	/** Indicazione sul modulo che utilizza il Sender  */
	private String idModulo = null;
	/** Indicazione sul codice porta del Sender */
	private IDSoggetto codicePorta = null;

	/** Object Ricevuto */
	private Object received;
	/** Proprieta' Ricevute */
	private Hashtable<String,java.io.Serializable> propertiesReceived;
	/** motivo di un eventuale errore */
	private String errore;
	/** ID JMS presente nell'header del messaggio ricevuto */
	private String idHeaderJMS;
	
	/** Indicazione se deve essere utilizzata una singola connessione JMS */
	private boolean singleConnection;
	
	/** Logger */
	private Logger log;

	/** IDTransazione */
	private String idTransazione;
	

	/**
	 * Costruttore
	 *
	 * @param aCodicePorta Codice del dominio che sta gestendo la richiesta.
	 * @param aIDModulo Identificativo del Receiver.
	 * @param singleConnection Indicazione se deve essere utilizzata una singola connessione JMS
	 * 
	 */
	public JMSReceiver(IDSoggetto aCodicePorta,String aIDModulo,boolean singleConnection,Logger log,String idTransazione) throws Exception {
		this.codicePorta = aCodicePorta;
		this.idModulo = "JMSReceiver."+aIDModulo;
		this.singleConnection = singleConnection;
		this.qmanager = QueueManager.getInstance();
		this.log = log;
		this.idTransazione = idTransazione;
	}

	/**
	 * Ricezione di un messaggio  (Serve per filtrare una coda con un messaggio)
	 *
	 * @param destinatario Nodo destinatario per cui effettuare la ricezione. 
	 * @param msgSelector Filtro da utilizzare per la ricezione
	 * @return true se la ricezione JMS e' andata a buon fine, false altrimenti.
	 * 
	 */
	public boolean clean(String destinatario,String msgSelector){
		return receive(destinatario,msgSelector,1,1);
	}
	
	/**
	 * Ricezione di un messaggio  
	 *
	 * @param destinatario Nodo destinatario per cui effettuare la ricezione. 
	 * @param timeout Timeout sulla ricezione
	 * @param checkInterval Intervallo di check sulla coda
	 * @return true se la ricezione JMS e' andata a buon fine, false altrimenti.
	 * 
	 */
	public boolean receive(String destinatario,long timeout,long checkInterval){
		return receive(destinatario,null,timeout,checkInterval);
	}
	/**
	 * Ricezione di un messaggio  
	 *
	 * @param destinatario Nodo destinatario per cui effettuare la ricezione. 
	 * @param msgSelector Filtro da utilizzare per la ricezione
	 * @param timeout Timeout sulla ricezione
	 * @param checkInterval Intervallo di check sulla coda
	 * @return true se la ricezione JMS e' andata a buon fine, false altrimenti.
	 * 
	 */
	public boolean receive(String destinatario,String msgSelector,long timeout,long checkInterval){

		Resource resource = null;
		MessageConsumer receiver = null;
		try{

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
				return false;
			}
			
			if(destinatario == null){
				this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
				return false; // non deve essere ricevuto nulla ??.
			}
				
			// Get Coda
			Queue queue = this.qmanager.getQueue(destinatario);
			if(queue == null){
				this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
				this.errore="La coda ["+destinatario+"] non e' tra quelle registrate per OpenSPCoop";
				return false;
			}

			//	Start connection
			try{
				jmsObject.getConnection().start();
			}catch(javax.jms.JMSException e){
				this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
				this.errore = "Riscontrato errore durante lo start della connessione ["+destinatario+"] :"+e.getMessage();
				return false;
			}
			
			// Receiver
			try{
				if(msgSelector != null)
					receiver = jmsObject.getSession().createConsumer(queue,msgSelector);
				else
					receiver = jmsObject.getSession().createConsumer(queue);
			}catch(javax.jms.JMSException e){
				this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
				this.log.error("Riscontrato errore durante la creazione del receiver ["+destinatario+"] :"+e.getMessage(),e);
				this.errore = "Riscontrato errore durante la creazione del receiver ["+destinatario+"] :"+e.getMessage();
				return false;
			}

			// Ricezione Oggetto
			ObjectMessage receivedMsg = null;
			long attesa = 0;
			while(attesa<timeout){
				
				//System.out.println("WHILE ["+this.idModulo+"]");
				
				receivedMsg = (ObjectMessage) receiver.receive(checkInterval);
				attesa = attesa + checkInterval;
				if(receivedMsg==null){
					
					//	rilascio e riprendo la connessione JMS ogni checkInterval fino ad un timeout o alla ricezione di un oggetto
					if(this.singleConnection==false){
						//System.out.println("Rilascio ["+this.idModulo+"]");
						receiver.close();
						this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
					}
					
					try{
						Thread.sleep(checkInterval);
					}catch(Exception e){}
					attesa = attesa + checkInterval;
					
					if(this.singleConnection==false){
						try{
							resource = this.qmanager.getResource(this.codicePorta,this.idModulo,this.idTransazione);
							if(resource == null)
								throw new JMSException("Resource is null");
							if(resource.getResource() == null)
								throw new JMSException("JMSObject is null");
							jmsObject = (JMSObject) resource.getResource();
						}catch(Exception e){
							this.log.error("JMSObject non ottenibile dal Pool: "+e.getMessage(),e);
							this.errore ="JMSObject non ottenibile dal Pool: "+e.getMessage();
							return false;
						}
						//System.out.println("Inizializzo ["+this.idModulo+"] ["+msgSelector+"]");
						//	Start connection
						try{
							jmsObject.getConnection().start();
						}catch(javax.jms.JMSException e){
							this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
							this.log.error("Riscontrato errore durante lo start della connessione ["+destinatario+"] :"+e.getMessage(),e);
							this.errore = "Riscontrato errore durante lo start della connessione ["+destinatario+"] :"+e.getMessage();
							return false;
						}	
						// Reinizializzo Receiver
						try{
							if(msgSelector != null){
								//System.out.println("MSG SELECTOR ["+msgSelector+"]");
								receiver = jmsObject.getSession().createConsumer(queue,msgSelector);
							}else{
								receiver = jmsObject.getSession().createConsumer(queue);
							}
						}catch(javax.jms.JMSException e){
							this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
							this.log.error("Riscontrato errore durante la creazione del receiver ["+destinatario+"] :"+e.getMessage(),e);
							this.errore = "Riscontrato errore durante la creazione del receiver ["+destinatario+"] :"+e.getMessage();
							return false;
						}
					}
				}else{
					//System.out.println("TROVATO ["+this.idModulo+"]");
					break;
				}
			}
			if(receivedMsg == null){
				this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
				this.errore = "Riscontrato errore durante ricezione del messaggio: Messaggio non ricevuto";
				receiver.close();
				return false;
			}

			// LetturaOggetto interno
			this.received = receivedMsg.getObject();
			this.idHeaderJMS = receivedMsg.getJMSMessageID();

			// Lettura Proprieta'
			this.propertiesReceived = new Hashtable<String,java.io.Serializable>();
			try{
				// IDMessaggio
				String idMessaggio = receivedMsg.getStringProperty("ID");
				this.propertiesReceived.put("ID",idMessaggio);
			}	catch(javax.jms.JMSException e){}
			try{
				// ContenutoRispostaPresente
				boolean contenutoRisposta = receivedMsg.getBooleanProperty("ContenutoRispostaPresente");
				this.propertiesReceived.put("ContenutoRispostaPresente",new Boolean(contenutoRisposta));
			}	catch(javax.jms.JMSException e){}
			// ......
			
			// Rilascio producer
			receiver.close();
		
			this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
			return true;

		}catch(Exception e){
			//	Rilascio producer
			try{
				if(receiver!=null)
					receiver.close();
			}catch(Exception eClose){}
			if(resource!=null){
				try{
					this.qmanager.releaseResource(this.codicePorta,this.idModulo,resource);
				}catch(Exception eClose){}	
			}
			this.log.error("Riscontrato errore durante la ricezione da una coda :"+e.getMessage(),e);
			this.errore = "Riscontrato errore durante la ricezione da una coda :"+e.getMessage();	
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
	/**
	 * In caso di ricezione con successo, questo metodo ritorna l'oggetto ricevuto.
	 *
	 * @return Oggetto ricevuto.
	 * 
	 */
	public Object getObjectReceived(){
		return this.received;
	}
	/**
	 * In caso di ricezione con successo, questo metodo ritorna le proprieta' ricevute.
	 *
	 * @return Proprieta' ricevute.
	 * 
	 */
	public Hashtable<String,java.io.Serializable> getPropertiesReceived(){
		return this.propertiesReceived;
	}

	/**
	 * ID JMS presente nell'header del messaggio ricevuto
	 *
	 * @return ID JMS presente nell'header del messaggio ricevuto
	 * 
	 */
	public String getIdHeaderJMS(){
		return this.idHeaderJMS;
	}
}





