/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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




package org.openspcoop2.pdd.core.connettori;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.utils.resources.Loader;

/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * la pubblicazione su queue/topic. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreJMS extends ConnettoreBase {

	private static final String QUEUE = "queue";
	private static final String TOPIC = "topic";
	private static final String TEXT_MESSAGE = "TextMessage";
	private static final String BYTES_MESSAGE = "BytesMessage";
	private static final String LOCATIONS_CACHE_ABILITATA = "abilitata";
	private static final String LOCATIONS_CACHE_DISABILITATA = "disabilitata";


	/** Cache per le locations */
	private static Hashtable<String,Destination> locations = new Hashtable<String,Destination>();

	/** Logger utilizzato per debug. */
	private org.apache.log4j.Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */




	/** Msg di richiesta */
	private OpenSPCoop2Message requestMsg;
	/** Proprieta' del connettore */
	private java.util.Hashtable<String,String> properties;
	/** Indicazione su di un eventuale sbustamento SOAP */
	private boolean sbustamentoSoap;
	/** Proprieta' del trasporto che deve gestire il connettore */
	private java.util.Properties propertiesTrasporto;
	/** Busta */
	private Busta busta;
	/** Tipo di Connettore */
	private String tipoConnettore;

	/** Identificativo */
	private String idMessaggio;
	
	/** Tipo di Autenticazione */
	//private String tipoAutenticazione;
	/** Credenziali per l'autenticazione */
	private Credenziali credenziali;
	/** Indicazione se siamo in modalita' debug */
	private boolean debug = false;

	/** acknowledgeModeSessione */
	private int acknowledgeModeSessione = javax.jms.Session.AUTO_ACKNOWLEDGE;

	/** Loader loader */
	private Loader loader = null;
	

	/**
	 * Si occupa di effettuare la consegna.
	 *
	 * @param request Messaggio da Consegnare
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	@Override
	public boolean send(ConnettoreMsg request){

		if(request==null){
			this.errore = "Messaggio da consegnare is Null (ConnettoreMsg)";
			return false;
		}

		// Context per invocazioni handler
		this.outRequestContext = request.getOutRequestContext();
		this.msgDiagnostico = request.getMsgDiagnostico();
		
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.loader = Loader.getInstance();
		
		// Raccolta parametri
		try{
			this.requestMsg =  request.getRequestMessage();
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.log.error("Errore durante la lettura del messaggio da consegnare: "+e.getMessage(),e);
			this.errore = "Errore durante la lettura del messaggio da consegnare: "+e.getMessage();
			return false;
		}
		this.properties = request.getConnectorProperties();
		this.sbustamentoSoap = request.isSbustamentoSOAP();
		this.propertiesTrasporto = request.getPropertiesTrasporto();
		this.busta = request.getBusta();
		if(this.busta!=null)
			this.idMessaggio=this.busta.getID();

		//this.tipoAutenticazione = request.getAutenticazione();
		this.credenziali = request.getCredenziali();

		// analisi messaggio da spedire
		if(this.requestMsg==null){
			this.errore = "Messaggio da consegnare is Null (Msg)";
			return false;
		}

		// analsi i parametri specifici per il connettore
		if(this.properties == null)
			this.errore = "Proprieta' del connettore non definite";
		if(this.properties.size() == 0)
			this.errore = "Proprieta' del connettore non definite";

		// tipoConnetore
		this.tipoConnettore = request.getTipoConnettore();

		// location
		if(this.properties.get("location")==null){
			this.errore = "Proprieta' 'location' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}


		// tipo
		if(this.properties.get("tipo")==null){
			this.errore = "Proprieta' 'tipo' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}
		if( (this.properties.get("tipo").equalsIgnoreCase(ConnettoreJMS.QUEUE) == false) && 
				(this.properties.get("tipo").equalsIgnoreCase(ConnettoreJMS.TOPIC) == false)){
			this.errore = "Proprieta' 'tipo' non correttamente fornita per il connettore ["+request.getTipoConnettore()+"] \n(Valori possibili sono 'queue' o 'topic')";
			return false;
		}

		// locations cache
		if(this.properties.get("locations-cache")!=null){
			if( (this.properties.get("locations-cache").equalsIgnoreCase(ConnettoreJMS.LOCATIONS_CACHE_ABILITATA) == false) && 
					(this.properties.get("locations-cache").equalsIgnoreCase(ConnettoreJMS.LOCATIONS_CACHE_DISABILITATA) == false)){
				this.errore = "Proprieta' 'locations-cache' non correttamente fornita per il connettore ["+request.getTipoConnettore()+"] \n(Valori possibili sono 'abilitata' o 'disabilitata')";
				return false;
			}
		}


		// connection-factory
		if(this.properties.get("connection-factory")==null){
			this.errore = "Proprieta' 'connection-factory' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}

		// send-as
		if(this.properties.get("send-as")==null){
			this.errore = "Proprieta' 'send-as' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}
		if( (this.properties.get("send-as").equalsIgnoreCase(ConnettoreJMS.TEXT_MESSAGE) == false) && 
				(this.properties.get("send-as").equalsIgnoreCase(ConnettoreJMS.BYTES_MESSAGE) == false)){
			this.errore = "Proprieta' 'send-as' non correttamente fornita per il connettore ["+request.getTipoConnettore()+"] \n(Valori possibili sono 'TextMessage' o 'BytesMessage')";
			return false;
		}

		// acknowledgeModeSessione
		if(this.properties.get("acknowledgeMode")!=null){
			if(CostantiConfigurazione.AUTO_ACKNOWLEDGE.equals(this.properties.get("acknowledgeMode")))
				this.acknowledgeModeSessione = javax.jms.Session.AUTO_ACKNOWLEDGE;
			else if(CostantiConfigurazione.CLIENT_ACKNOWLEDGE.equals(this.properties.get("acknowledgeMode")))
				this.acknowledgeModeSessione = javax.jms.Session.CLIENT_ACKNOWLEDGE;
			else if(CostantiConfigurazione.DUPS_OK_ACKNOWLEDGE.equals(this.properties.get("acknowledgeMode")))
				this.acknowledgeModeSessione = javax.jms.Session.DUPS_OK_ACKNOWLEDGE;
			else
				this.log.error("Tipo di acknowledgeModeSessione non conosciuto (viene utilizzato il default:AUTO_ACKNOWLEDGE)");
		}

		// Debug mode
		if(this.properties.get("debug")!=null){
			if("true".equalsIgnoreCase(this.properties.get("debug").trim()))
				this.debug = true;
		}

		return sendJMS();

	}




	/**
	 * Si occupa di effettuare la consegna.
	 *
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	private boolean sendJMS(){

		// Risorse ottenute 
		Connection qc = null;
		Session qs = null;

		// ConnectionFactory
		ConnectionFactory qcf = null;

		// Sender
		MessageProducer sender = null;

		// Code
		Queue queueDestination = null;
		Topic topicDestination =null;

		InitialContext ctxJMS = null;
		InitialContext ctxLocalPool = null;
		try{

			/* ----- Trasformazione del messaggio in byte ----- */
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] trasformazione del messaggio in byte...");
			byte [] consegna = null;
			if(this.sbustamentoSoap){
				// richiesto sbustamento
				consegna = SoapUtils.sbustamentoMessaggio(this.requestMsg);
			}else{
				// consegna dell'intero soap envelope
				ByteArrayOutputStream reqBuffer = new ByteArrayOutputStream();
				this.requestMsg.writeTo(reqBuffer, true);
				consegna = reqBuffer.toByteArray();
				reqBuffer.close();
			}
			if(consegna == null){
				this.errore = "Errore avvenuto durante la consegna JMS: Trasformazione del messaggio in byte[] non riuscita";
				return false;
			}
			if(this.debug){
				try{
					String contentTypeRichiesta = this.requestMsg.getContentType();
					this.log.debug("["+this.idMessaggio+"] Messaggio inviato (ContentType:"+contentTypeRichiesta+") :\n"+new String(consegna));
				}catch(Exception e){
					this.log.error("["+this.idMessaggio+"] DebugMode, log del messaggio inviato non riuscito",e);
				}
			}

			/* ------ Raccolta credenziali ------ */
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] raccolta credenziali...");
			String user = null;
			String password = null;
			if(this.credenziali!=null){
				//log.info("credenziali");
				user = this.credenziali.getUsername();
				password = this.credenziali.getPassword();
			}else{
				//log.info("propertiesa");
				user = this.properties.get("user");
				password = this.properties.get("password");
			}


			/* ------ Creazione contesto  ed eventuale pool-local-context ------- */
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] creazione jndi context ed eventuale pool-local-context...");
			java.util.Properties propertiesJMS = new java.util.Properties();
			java.util.Properties propertiesLocalPool = new java.util.Properties();
			if(user!=null && password!=null){
				
				propertiesJMS.put(Context.SECURITY_PRINCIPAL, user);
				propertiesJMS.put(Context.SECURITY_CREDENTIALS, password);
				
				propertiesLocalPool.put(Context.SECURITY_PRINCIPAL, user);
				propertiesLocalPool.put(Context.SECURITY_CREDENTIALS, password);
			}
			
			// Properties da interfaccia
			Enumeration<?> enumCTX = this.properties.keys();
			while( enumCTX.hasMoreElements() ) {
				String key = (String) enumCTX.nextElement();
				String value = this.properties.get(key);
				if(key.startsWith("context-")){
					key = key.substring("context-".length());
					propertiesJMS.put(key,value);
				}else if(key.startsWith("pool-")){
					key = key.substring("pool-".length());
					propertiesLocalPool.put(key,value);
				}else if(key.startsWith("lookupDestination-")){
					key = key.substring("lookupDestination-".length());
					if(key.indexOf("#Azione")!=-1 || value.indexOf("#Azione")!=-1){
						if(this.busta == null || this.busta.getAzione()== null){
							throw new Exception("Proprieta' 'location' definita dinamicamente (#Azione), ma busta e Azione non definita per questo Connettore["+this.tipoConnettore+"]");
						}
					}
					if(key.indexOf("#Servizio")!=-1 || value.indexOf("#Servizio")!=-1){
						if(this.busta == null || this.busta.getServizio()== null){
							throw new Exception("Proprieta' 'location' definita dinamicamente (#Servizio), ma busta e Servizio non definito per questo Connettore["+this.tipoConnettore+"]");
						}
					}
					if(key.indexOf("#TipoServizio")!=-1 || value.indexOf("#TipoServizio")!=-1){
						if(this.busta == null || this.busta.getTipoServizio()== null){
							throw new Exception("Proprieta' 'location' definita dinamicamente (#TipoServizio), ma busta e TipoServizio non definito per questo Connettore["+this.tipoConnettore+"]");
						}
					}
					if(this.busta.getAzione()!=null)
						key = key.replace("#Azione",this.busta.getAzione());
					if(this.busta.getServizio()!=null)
						key = key.replace("#Servizio",this.busta.getServizio());
					if(this.busta.getTipoServizio()!=null)
						key = key.replace("#TipoServizio",this.busta.getTipoServizio());
					if(this.busta.getAzione()!=null)
						value = value.replace("#Azione",this.busta.getAzione());
					if(this.busta.getServizio()!=null)
						value = value.replace("#Servizio",this.busta.getServizio());
					if(this.busta.getTipoServizio()!=null)
						value = value.replace("#TipoServizio",this.busta.getTipoServizio());
					propertiesJMS.put(key,value);
				}
			}

			// Overwrite da file properties.
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] creazione jndi context ed eventuale pool-local-context, Overwrite da file properties...");
			ConnettoreJMSProperties overwriteProperties = ConnettoreJMSProperties.getInstance();
			if (overwriteProperties!=null){
				Hashtable<String,IDServizio> idServizi = overwriteProperties.getIDServizi_Pubblicazione();
				String indiceServizio = null;
				java.util.Enumeration<String> e = idServizi.keys();
				while(e.hasMoreElements()){
					if(this.busta !=null){
						String keyElement = e.nextElement();
						IDServizio match = idServizi.get(keyElement);
						if(match !=null && match.getServizio()!=null && match.getTipoServizio()!=null && match.getSoggettoErogatore()!=null && match.getSoggettoErogatore().getNome()!=null && match.getSoggettoErogatore().getTipo()!=null)
						{
							if( (match.getServizio().equals(this.busta.getServizio())) &&
									(match.getTipoServizio().equals(this.busta.getTipoServizio())) &&	
									(match.getSoggettoErogatore().getNome().equals(this.busta.getDestinatario())) &&
									(match.getSoggettoErogatore().getTipo().equals(this.busta.getTipoDestinatario())) ){

								indiceServizio	=keyElement;
								break;
							}
						}
					}	
				}
				if(indiceServizio!=null){
					// overwrite
					java.util.Properties propertiesContextOver = overwriteProperties.getJNDIContext_Configurazione(indiceServizio);
					if(propertiesContextOver!=null){
						java.util.Enumeration<?> el = propertiesContextOver.keys();
						while(el.hasMoreElements()){
							String keyP = (String) el.nextElement();
							propertiesJMS.setProperty(keyP,propertiesContextOver.getProperty(keyP));

						}
					}
					java.util.Properties propertiesPoolOver = overwriteProperties.getJNDIPool_Configurazione(indiceServizio);
					if(propertiesPoolOver!=null){
						java.util.Enumeration<?> el = propertiesPoolOver.keys();
						while(el.hasMoreElements()){
							String keyP = (String) el.nextElement();
							propertiesLocalPool.setProperty(keyP,propertiesPoolOver.getProperty(keyP));
						}
					}
				}
			}

			// Impostazione Contesto
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] impostazione contesto...");
			ctxJMS = new InitialContext(propertiesJMS);
			if(propertiesLocalPool.size() > 0){
				ctxLocalPool = new InitialContext(propertiesLocalPool);
			}


			/* ------ Ottengo Risorsa sorgente (ConnectionFactory/OpenSPCoopQueueManager) -------- */
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] lookup connection factory...");
			String connectionFactory = this.properties.get("connection-factory");
			if(connectionFactory!=null)
				connectionFactory = connectionFactory.trim();
			if(ctxLocalPool!=null){
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] lookup connection factory da ctxLocalPool...");
				qcf = (ConnectionFactory) ctxLocalPool.lookup( connectionFactory );
			}
			else{
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] lookup connection factory da ctxJMS...");
				qcf = (ConnectionFactory) ctxJMS.lookup( connectionFactory );
			}


			/* --------- Ottengo Coda/Topic --------- */
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] lookup coda/topic...");
			// Costruzione location
			this.location = this.properties.get("location");

			boolean findDestinationInCache = false;
			// cerco nella cache
			if(ConnettoreJMS.LOCATIONS_CACHE_ABILITATA.equals(this.properties.get("locations-cache")) ){
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] lookup coda/topic in cache ["+(ConnettoreJMS.locations.containsKey(this.location)==true)+"]...");
				if(ConnettoreJMS.locations.containsKey(this.location)==true){
					//System.out.println("PRELEVO DALLA CACHE");
					if(ConnettoreJMS.QUEUE.equalsIgnoreCase(this.properties.get("tipo"))){
						// Lookup Coda
						queueDestination = (Queue) ConnettoreJMS.locations.get(this.location);
					}else{
						// Lookup Topic
						topicDestination = (Topic) ConnettoreJMS.locations.get(this.location);
					}
					findDestinationInCache = true;
					if(this.debug)
						this.log.debug("["+this.idMessaggio+"] lookup coda/topic in cache effettuata con successo...");
				}
			}
			// lookup location nel JNDI
			if(findDestinationInCache == false){
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] lookup coda/topic (non e' presente in cache, o cache non abilitata)...");
				//System.out.println("PRELEVO NORMALE");
				if(ConnettoreJMS.QUEUE.equalsIgnoreCase(this.properties.get("tipo"))){
					// Lookup Coda
					if(this.debug)
						this.log.debug("["+this.idMessaggio+"] lookup coda...");
					queueDestination = (Queue) ctxJMS.lookup( this.location );
					if(this.debug)
						this.log.debug("["+this.idMessaggio+"] lookup coda effettuata...");
				}else{
					// Lookup Topic
					if(this.debug)
						this.log.debug("["+this.idMessaggio+"] lookup topic...");
					topicDestination = (Topic) ctxJMS.lookup( this.location );
					if(this.debug)
						this.log.debug("["+this.idMessaggio+"] lookup topic effettuato...");
				}
			}
			// aggiungo in cache se non c'era e se la cache e' attiva
			if( (findDestinationInCache == false) &&  ConnettoreJMS.LOCATIONS_CACHE_ABILITATA.equals(this.properties.get("locations-cache")) ){
				//System.out.println("AGGIUNGO IN CACHE");
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] aggiunto coda/topic in cache...");
				if(ConnettoreJMS.QUEUE.equalsIgnoreCase(this.properties.get("tipo")))
					putDestination(this.location,queueDestination);
				else
					putDestination(this.location,topicDestination);
			}



			/* -------- Ottengo Risorse Connessione/Sessione ---------- */
			// ConnectionFactory
			if( user!=null && password!=null  ){
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] create connection (user:"+user+" password:"+password+")...");
				qc = qcf.createConnection(user,password);
			}
			else{
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] create connection...");
				qc = qcf.createConnection();
			}
			if(qc==null)
				throw new Exception("Connessione JMS non ritornata (is null) dalla ConnectionFactory["+connectionFactory+"]");
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] create sessione ["+this.acknowledgeModeSessione+"]...");
			qs = qc.createSession(false, this.acknowledgeModeSessione);
			if(qs==null)
				throw new Exception("Sessione JMS non ritornata (is null) dalla Connessione creata con la ConnectionFactory["+connectionFactory+"]");


			/* ---------- Crezione Sender ------------*/
			if(ConnettoreJMS.QUEUE.equalsIgnoreCase(this.properties.get("tipo"))){
				// Queue
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] create queue producer...");
				sender = qs.createProducer(queueDestination);
			}else{
				// Topic
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] create topic producer...");
				sender = qs.createProducer(topicDestination);
			}
			if(sender==null)
				throw new Exception("Sender JMS non creato (is null) attraverso la Sessione creata con la Connessione presa dalla ConnectionFactory["+connectionFactory+"]");


			/* ---------- Creazione messaggio ----------- */
			javax.jms.Message messageJMS = null;
			if( ConnettoreJMS.BYTES_MESSAGE.equalsIgnoreCase( this.properties.get("send-as")  ) ){
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] create BytesMessage...");
				messageJMS = qs.createBytesMessage();
				((BytesMessage)messageJMS).writeBytes(consegna);
			}else{
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] create TextMessage...");
				messageJMS =  qs.createTextMessage(new String(consegna));
			}
			if(messageJMS==null)
				throw new Exception("Messaggio JMS non creato (is null) attraverso la Sessione creata con la Connessione presa dalla ConnectionFactory["+connectionFactory+"]");



			/* ---------- Impostazione Proprieta del trasporto ----------- */
			if(this.propertiesTrasporto != null){
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] set proprieta' jms...");
				Enumeration<?> enumSPC = this.propertiesTrasporto.keys();
				while( enumSPC.hasMoreElements() ) {
					String key = (String) enumSPC.nextElement();
					String value = (String) this.propertiesTrasporto.get(key);
					// Replace X--Value con Value
					key = key.replace("X-", "");
					key = key.replaceAll("-", "");
					if(this.debug)
						this.log.debug("["+this.idMessaggio+"] set proprieta' ["+key+"]=["+value+"]...");
					messageJMS.setStringProperty(key,value);
				}
			}

			/* ---------- Impostazione Proprieta' Dinamiche ------------- */
			if(overwriteProperties!=null){
				if(this.debug)
					this.log.debug("["+this.idMessaggio+"] set proprieta' dinamiche...");
				String[]classiJMSSetProprieties = overwriteProperties.getClassNameSetPropertiesJMS();
				if(classiJMSSetProprieties!=null){
					for(int i=0;i<classiJMSSetProprieties.length;i++){
						if(this.debug)
							this.log.debug("["+this.idMessaggio+"] set proprieta' dinamiche con classe["+classiJMSSetProprieties[i]+"]...");
						try{
							IConnettoreJMSSetProperties setProperties = (IConnettoreJMSSetProperties) this.loader.newInstance(classiJMSSetProprieties[i]);
							setProperties.setProperties(this.requestMsg,messageJMS);
						}catch(Exception e){
							this.log.error("Setting delle proprieta' JMS tramite classe ["+classiJMSSetProprieties[i]+"] non riuscita",e);
						}
					}
				}
			}


			/* ---------- Pubblicazione ----------- */
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] send message...");
			sender.send(messageJMS); 
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] send message effettuata...");




			/* ---------- Rilascio Risorse ----------- */
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] sender.close ...");
			sender.close();
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] session.close ...");
			qs.close();  
			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] connection.close ...");
			qc.close();

			// codice di consegna per forza uguale a OK
			this.codice = 200;

			if(this.debug)
				this.log.debug("["+this.idMessaggio+"] connettore jms ha pubblicato con successo");
			
			
			
			
			
			
			
			
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
//			/* ------------  PreInResponseHandler ------------- */
//			this.preInResponse();
//			
//			// Lettura risposta parametri NotifierInputStream per la risposta
//			org.openspcoop.utils.io.notifier.NotifierInputStreamParams notifierInputStreamParams = null;
//			if(this.preInResponseContext!=null){
//				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
//			}
			
			
			
			
			
			
			return true;
		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			this.log.error("Errore avvenuto durante la consegna JMS",e);
			this.errore = "Errore avvenuto durante la consegna JMS: "+e.getMessage();
			try{
				// Rilascio Risorse
				sender.close();
			}catch(Exception ec){}
			try{
				if(qs!=null)
					qs.close();	
			}catch(Exception ec){}
			try{
				if(qc!=null)
					qc.close();

			}catch(Exception ec){}
			return false;
		}finally{
			try{
				if(ctxJMS!=null)
					ctxJMS.close();
			}catch(Exception close){}
			try{
				if(ctxLocalPool!=null)
					ctxLocalPool.close();
			}catch(Exception close){}
		}

	}



	/**
	 * Aggiunge una location in Cache
	 *
	 * @param key Chiave per la memorizzazione in cache
	 * @param destination Destinazione da inserire in cache
	 * 
	 */
	public synchronized void putDestination(String key,Destination destination){
		try{
			ConnettoreJMS.locations.put(key,destination);
		}catch(Exception e){
			this.log.error("ERROR INSERT CODA IN CACHE: "+e.getMessage(),e);
			//possibile inserimento della stessa coda....
		}
	}

}





