/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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




package org.openspcoop2.pdd.core.connettori;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.jms.BytesMessage;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.transport.TransportUtils;

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

    @Override
	public String getProtocollo() {
    	return "JMS";
    }

	/** Cache per le locations */
	private static Map<String,Destination> locations = new ConcurrentHashMap<String,Destination>();

	/** acknowledgeModeSessione */
	private int acknowledgeModeSessione = jakarta.jms.Session.AUTO_ACKNOWLEDGE;



	@Override
	protected boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request) {
		
		if(this.initialize(request, true, responseCachingConfig)==false){
			return false;
		}
		
		return true;
		
	}
	
	@Override
	protected boolean send(ConnettoreMsg request) {

		// location
		if(this.properties.get(CostantiConnettori.CONNETTORE_LOCATION)==null){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_LOCATION+"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}


		// tipo
		if(this.properties.get(CostantiConnettori.CONNETTORE_JMS_TIPO)==null){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_JMS_TIPO+"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}
		if( (this.properties.get(CostantiConnettori.CONNETTORE_JMS_TIPO).equalsIgnoreCase(CostantiConnettori.CONNETTORE_JMS_TIPO_QUEUE) == false) && 
				(this.properties.get(CostantiConnettori.CONNETTORE_JMS_TIPO).equalsIgnoreCase(CostantiConnettori.CONNETTORE_JMS_TIPO_TOPIC) == false)){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_JMS_TIPO+"' non correttamente fornita per il connettore ["+
				request.getTipoConnettore()+"] \n(Valori possibili sono '"+CostantiConnettori.CONNETTORE_JMS_TIPO_QUEUE+"' o '"+CostantiConnettori.CONNETTORE_JMS_TIPO_TOPIC+"')";
			return false;
		}

		// locations cache
		if(this.properties.get(CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE)!=null){
			if( (this.properties.get(CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE).equalsIgnoreCase(CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE_ABILITATA) == false) && 
					(this.properties.get(CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE).equalsIgnoreCase(CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE_DISABILITATA) == false)){
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE
						+"' non correttamente fornita per il connettore ["+request.getTipoConnettore()+"] \n(Valori possibili sono '"+
						CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE_ABILITATA+"' o '"+CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE_DISABILITATA+"')";
				return false;
			}
		}


		// connection-factory
		if(this.properties.get(CostantiConnettori.CONNETTORE_JMS_CONNECTION_FACTORY)==null){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_JMS_CONNECTION_FACTORY+
					"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}

		// send-as
		if(this.properties.get(CostantiConnettori.CONNETTORE_JMS_SEND_AS)==null){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_JMS_SEND_AS+"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}
		if( (this.properties.get(CostantiConnettori.CONNETTORE_JMS_SEND_AS).equalsIgnoreCase(CostantiConnettori.CONNETTORE_JMS_SEND_AS_TEXT_MESSAGE) == false) && 
				(this.properties.get(CostantiConnettori.CONNETTORE_JMS_SEND_AS).equalsIgnoreCase(CostantiConnettori.CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE) == false)){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_JMS_SEND_AS+"' non correttamente fornita per il connettore ["+request.getTipoConnettore()+
					"] \n(Valori possibili sono '"+CostantiConnettori.CONNETTORE_JMS_SEND_AS_TEXT_MESSAGE+"' o '"+CostantiConnettori.CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE+"')";
			return false;
		}

		// acknowledgeModeSessione
		if(this.properties.get(CostantiConnettori.CONNETTORE_JMS_ACKNOWLEDGE_MODE)!=null){
			if(CostantiConfigurazione.AUTO_ACKNOWLEDGE.equals(this.properties.get(CostantiConnettori.CONNETTORE_JMS_ACKNOWLEDGE_MODE)))
				this.acknowledgeModeSessione = jakarta.jms.Session.AUTO_ACKNOWLEDGE;
			else if(CostantiConfigurazione.CLIENT_ACKNOWLEDGE.equals(this.properties.get(CostantiConnettori.CONNETTORE_JMS_ACKNOWLEDGE_MODE)))
				this.acknowledgeModeSessione = jakarta.jms.Session.CLIENT_ACKNOWLEDGE;
			else if(CostantiConfigurazione.DUPS_OK_ACKNOWLEDGE.equals(this.properties.get(CostantiConnettori.CONNETTORE_JMS_ACKNOWLEDGE_MODE)))
				this.acknowledgeModeSessione = jakarta.jms.Session.DUPS_OK_ACKNOWLEDGE;
			else
				this.logger.error("Tipo di acknowledgeModeSessione non conosciuto (viene utilizzato il default:AUTO_ACKNOWLEDGE)");
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
				this.logger.debug("Trasformazione del messaggio in byte...");
			byte [] consegna = null;
			if(this.isSoap && this.sbustamentoSoap){
				// richiesto sbustamento
				consegna = TunnelSoapUtils.sbustamentoMessaggio(this.requestMsg);
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
			if(this.isDumpBinarioRichiesta()) {
				try{
					this.emitDiagnosticStartDumpBinarioRichiestaUscita();
					
					MessageType requestMessageType = this.requestMsg.getMessageType();
					String contentTypeRichiesta = this.requestMsg.getContentType();
					DumpByteArrayOutputStream bout = DumpByteArrayOutputStream.newInstance(consegna);
					bout.write(consegna);
					bout.flush();
					bout.close();
					this.dumpBinarioRichiestaUscita(bout, requestMessageType, contentTypeRichiesta, this.location, this.propertiesTrasporto);
				}catch(Exception e){
					this.logger.error("DebugMode, log del messaggio inviato non riuscito",e);
				}
			}

			/* ------ Raccolta credenziali ------ */
			if(this.debug)
				this.logger.debug("Raccolta credenziali...");
			String user = null;
			String password = null;
			if(this.credenziali!=null){
				//log.info("credenziali");
				user = this.credenziali.getUser();
				password = this.credenziali.getPassword();
			}else{
				//log.info("propertiesa");
				user = this.properties.get(CostantiConnettori.CONNETTORE_USERNAME);
				password = this.properties.get(CostantiConnettori.CONNETTORE_PASSWORD);
			}


			/* ------ Creazione contesto  ed eventuale pool-local-context ------- */
			if(this.debug)
				this.logger.debug("Creazione jndi context ed eventuale pool-local-context...");
			java.util.Properties propertiesJMS = new java.util.Properties();
			java.util.Properties propertiesLocalPool = new java.util.Properties();
			if(user!=null && password!=null){
				
				propertiesJMS.put(Context.SECURITY_PRINCIPAL, user);
				propertiesJMS.put(Context.SECURITY_CREDENTIALS, password);
				
				propertiesLocalPool.put(Context.SECURITY_PRINCIPAL, user);
				propertiesLocalPool.put(Context.SECURITY_CREDENTIALS, password);
			}
			
			// Properties da interfaccia
			Iterator<String> enumCTX = this.properties.keySet().iterator();
			while( enumCTX.hasNext() ) {
				String key = (String) enumCTX.next();
				String value = this.properties.get(key);
				if(key.startsWith(CostantiConnettori.CONNETTORE_JMS_CONTEXT_PREFIX)){
					key = key.substring(CostantiConnettori.CONNETTORE_JMS_CONTEXT_PREFIX.length());
					propertiesJMS.put(key,value);
				}else if(key.startsWith(CostantiConnettori.CONNETTORE_JMS_POOL_PREFIX)){
					key = key.substring(CostantiConnettori.CONNETTORE_JMS_POOL_PREFIX.length());
					propertiesLocalPool.put(key,value);
				}else if(key.startsWith(CostantiConnettori.CONNETTORE_JMS_LOOKUP_DESTINATION_PREFIX)){
					key = key.substring(CostantiConnettori.CONNETTORE_JMS_LOOKUP_DESTINATION_PREFIX.length());
					if(key.indexOf(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_AZIONE)!=-1 || value.indexOf(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_AZIONE)!=-1){
						if(this.busta == null || this.busta.getAzione()== null){
							throw new Exception("Proprieta' '"+CostantiConnettori.CONNETTORE_LOCATION+"' definita dinamicamente ("+CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_AZIONE+"), ma busta e Azione non definita per questo Connettore["+this.tipoConnettore+"]");
						}
					}
					if(key.indexOf(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_NOME_SERVIZIO)!=-1 || value.indexOf(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_NOME_SERVIZIO)!=-1){
						if(this.busta == null || this.busta.getServizio()== null){
							throw new Exception("Proprieta' '"+CostantiConnettori.CONNETTORE_LOCATION+"' definita dinamicamente ("+CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_NOME_SERVIZIO+"), ma busta e Servizio non definito per questo Connettore["+this.tipoConnettore+"]");
						}
					}
					if(key.indexOf(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_TIPO_SERVIZIO)!=-1 || value.indexOf(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_TIPO_SERVIZIO)!=-1){
						if(this.busta == null || this.busta.getTipoServizio()== null){
							throw new Exception("Proprieta' '"+CostantiConnettori.CONNETTORE_LOCATION+"' definita dinamicamente ("+CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_TIPO_SERVIZIO+"), ma busta e TipoServizio non definito per questo Connettore["+this.tipoConnettore+"]");
						}
					}
					if(this.busta.getAzione()!=null)
						key = key.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_AZIONE,this.busta.getAzione());
					if(this.busta.getServizio()!=null)
						key = key.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_NOME_SERVIZIO,this.busta.getServizio());
					if(this.busta.getTipoServizio()!=null)
						key = key.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_TIPO_SERVIZIO,this.busta.getTipoServizio());
					if(this.busta.getAzione()!=null)
						value = value.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_AZIONE,this.busta.getAzione());
					if(this.busta.getServizio()!=null)
						value = value.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_NOME_SERVIZIO,this.busta.getServizio());
					if(this.busta.getTipoServizio()!=null)
						value = value.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_TIPO_SERVIZIO,this.busta.getTipoServizio());
					propertiesJMS.put(key,value);
				}
			}

			// Overwrite da file properties.
			if(this.debug)
				this.logger.debug("Creazione jndi context ed eventuale pool-local-context, Overwrite da file properties...");
			ConnettoreJMSProperties overwriteProperties = ConnettoreJMSProperties.getInstance();
			if (overwriteProperties!=null){
				Map<String,IDServizio> idServizi = overwriteProperties.getIDServiziPubblicazione();
				String indiceServizio = null;
				if(this.busta !=null){
					for (String keyElement : idServizi.keySet()) {
						IDServizio match = idServizi.get(keyElement);
						if(match !=null && match.getNome()!=null && match.getTipo()!=null && match.getVersione()!=null && 
								match.getSoggettoErogatore()!=null && match.getSoggettoErogatore().getNome()!=null && match.getSoggettoErogatore().getTipo()!=null)
						{
							if( (match.getNome().equals(this.busta.getServizio())) &&
									(match.getTipo().equals(this.busta.getTipoServizio())) &&	
									(match.getVersione().intValue() == this.busta.getVersioneServizio().intValue()) &&	
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
					java.util.Properties propertiesContextOver = overwriteProperties.getJNDIContextConfigurazione(indiceServizio);
					if(propertiesContextOver!=null){
						java.util.Enumeration<?> el = propertiesContextOver.keys();
						while(el.hasMoreElements()){
							String keyP = (String) el.nextElement();
							propertiesJMS.setProperty(keyP,propertiesContextOver.getProperty(keyP));

						}
					}
					java.util.Properties propertiesPoolOver = overwriteProperties.getJNDIPoolConfigurazione(indiceServizio);
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
				this.logger.debug("Impostazione contesto...");
			ctxJMS = new InitialContext(propertiesJMS);
			if(propertiesLocalPool.size() > 0){
				ctxLocalPool = new InitialContext(propertiesLocalPool);
			}


			/* ------ Ottengo Risorsa sorgente (ConnectionFactory/OpenSPCoopQueueManager) -------- */
			if(this.debug)
				this.logger.debug("Lookup connection factory...");
			String connectionFactory = this.properties.get(CostantiConnettori.CONNETTORE_JMS_CONNECTION_FACTORY);
			if(connectionFactory!=null)
				connectionFactory = connectionFactory.trim();
			if(ctxLocalPool!=null){
				if(this.debug)
					this.logger.debug("Lookup connection factory da ctxLocalPool...");
				qcf = (ConnectionFactory) ctxLocalPool.lookup( connectionFactory );
			}
			else{
				if(this.debug)
					this.logger.debug("Lookup connection factory da ctxJMS...");
				qcf = (ConnectionFactory) ctxJMS.lookup( connectionFactory );
			}


			/* --------- Ottengo Coda/Topic --------- */
			if(this.debug)
				this.logger.debug("Lookup coda/topic...");
			// Costruzione location
			this.location = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);

			boolean findDestinationInCache = false;
			// cerco nella cache
			if(CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE_ABILITATA.equals(this.properties.get(CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE)) ){
				if(this.debug)
					this.logger.debug("Lookup coda/topic in cache ["+(ConnettoreJMS.locations.containsKey(this.location)==true)+"]...");
				if(ConnettoreJMS.locations.containsKey(this.location)==true){
					//System.out.println("PRELEVO DALLA CACHE");
					if(CostantiConnettori.CONNETTORE_JMS_TIPO_QUEUE.equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_JMS_TIPO))){
						// Lookup Coda
						queueDestination = (Queue) ConnettoreJMS.locations.get(this.location);
					}else{
						// Lookup Topic
						topicDestination = (Topic) ConnettoreJMS.locations.get(this.location);
					}
					findDestinationInCache = true;
					if(this.debug)
						this.logger.debug("Lookup coda/topic in cache effettuata con successo...");
				}
			}
			// lookup location nel JNDI
			if(findDestinationInCache == false){
				if(this.debug)
					this.logger.debug("Lookup coda/topic (non e' presente in cache, o cache non abilitata)...");
				//System.out.println("PRELEVO NORMALE");
				if(CostantiConnettori.CONNETTORE_JMS_TIPO_QUEUE.equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_JMS_TIPO))){
					// Lookup Coda
					if(this.debug)
						this.logger.debug("Lookup coda...");
					queueDestination = (Queue) ctxJMS.lookup( this.location );
					if(this.debug)
						this.logger.info("Lookup coda ["+this.location+"] effettuata...",false);
				}else{
					// Lookup Topic
					if(this.debug)
						this.logger.debug("Lookup topic...");
					topicDestination = (Topic) ctxJMS.lookup( this.location );
					if(this.debug)
						this.logger.info("Lookup topic ["+this.location+"] effettuato...",false);
				}
			}
			// aggiungo in cache se non c'era e se la cache e' attiva
			if( (findDestinationInCache == false) &&  CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE_ABILITATA.equals(this.properties.get(CostantiConnettori.CONNETTORE_JMS_LOCATIONS_CACHE)) ){
				//System.out.println("AGGIUNGO IN CACHE");
				if(this.debug)
					this.logger.debug("Aggiunto coda/topic in cache...");
				if(CostantiConnettori.CONNETTORE_JMS_TIPO_QUEUE.equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_JMS_TIPO)))
					putDestination(this.location,queueDestination);
				else
					putDestination(this.location,topicDestination);
			}



			/* -------- Ottengo Risorse Connessione/Sessione ---------- */
			// ConnectionFactory
			if( user!=null && password!=null  ){
				if(this.debug)
					this.logger.info("Create connection (user:"+user+" password:"+password+")...",false);
				qc = qcf.createConnection(user,password);
			}
			else{
				if(this.debug)
					this.logger.info("Create connection...",false);
				qc = qcf.createConnection();
			}
			if(qc==null)
				throw new Exception("Connessione JMS non ritornata (is null) dalla ConnectionFactory["+connectionFactory+"]");
			if(this.debug)
				this.logger.info("Create sessione ["+this.acknowledgeModeSessione+"]...",false);
			qs = qc.createSession(false, this.acknowledgeModeSessione);
			if(qs==null)
				throw new Exception("Sessione JMS non ritornata (is null) dalla Connessione creata con la ConnectionFactory["+connectionFactory+"]");


			/* ---------- Crezione Sender ------------*/
			if(CostantiConnettori.CONNETTORE_JMS_TIPO_QUEUE.equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_JMS_TIPO))){
				// Queue
				if(this.debug)
					this.logger.info("Create queue producer...",false);
				sender = qs.createProducer(queueDestination);
			}else{
				// Topic
				if(this.debug)
					this.logger.info("Create topic producer...",false);
				sender = qs.createProducer(topicDestination);
			}
			if(sender==null)
				throw new Exception("Sender JMS non creato (is null) attraverso la Sessione creata con la Connessione presa dalla ConnectionFactory["+connectionFactory+"]");


			/* ---------- Creazione messaggio ----------- */
			jakarta.jms.Message messageJMS = null;
			if( CostantiConnettori.CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE.equalsIgnoreCase( this.properties.get(CostantiConnettori.CONNETTORE_JMS_SEND_AS)  ) ){
				if(this.debug)
					this.logger.info("Create BytesMessage...",false);
				messageJMS = qs.createBytesMessage();
				((BytesMessage)messageJMS).writeBytes(consegna);
			}else{
				if(this.debug)
					this.logger.info("Create TextMessage...",false);
				messageJMS =  qs.createTextMessage(new String(consegna));
			}
			if(messageJMS==null)
				throw new Exception("Messaggio JMS non creato (is null) attraverso la Sessione creata con la Connessione presa dalla ConnectionFactory["+connectionFactory+"]");



			/* ---------- Impostazione Proprieta del trasporto ----------- */
			if(this.propertiesTrasporto != null){
				if(this.debug)
					this.logger.debug("Set proprieta' jms...");
				Iterator<String> keys = this.propertiesTrasporto.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					List<String> values = this.propertiesTrasporto.get(key);
					String value = TransportUtils.getFirstValue(values);
					// Replace X--Value con Value
					key = key.replace("X-", "");
					key = key.replaceAll("-", "");
					if(this.debug)
						this.logger.info("Set proprieta' ["+key+"]=["+value+"]...",false);
					messageJMS.setStringProperty(key,value);
				}
			}

			/* ---------- Impostazione Proprieta' Dinamiche ------------- */
			if(overwriteProperties!=null){
				if(this.debug)
					this.logger.debug("Set proprieta' dinamiche...");
				String[]classiJMSSetProprieties = overwriteProperties.getClassNameSetPropertiesJMS();
				if(classiJMSSetProprieties!=null){
					for(int i=0;i<classiJMSSetProprieties.length;i++){
						if(this.debug)
							this.logger.debug("Set proprieta' dinamiche con classe["+classiJMSSetProprieties[i]+"]...");
						try{
							IConnettoreJMSSetProperties setProperties = (IConnettoreJMSSetProperties) this.loader.newInstance(classiJMSSetProprieties[i]);
							setProperties.setProperties(this.requestMsg,messageJMS);
						}catch(Exception e){
							this.logger.error("Setting delle proprieta' JMS tramite classe ["+classiJMSSetProprieties[i]+"] non riuscita",e);
						}
					}
				}
			}


			/* ---------- Pubblicazione ----------- */
			if(this.debug)
				this.logger.debug("Send message...");
			sender.send(messageJMS); 
			if(this.debug)
				this.logger.info("Send message effettuata",false);




			/* ---------- Rilascio Risorse ----------- */
			if(this.debug)
				this.logger.debug("Sender.close ...");
			sender.close();
			if(this.debug)
				this.logger.debug("Session.close ...");
			qs.close();  
			if(this.debug)
				this.logger.debug("Connection.close ...");
			qc.close();

			// codice di consegna per forza uguale a OK
			this.codice = 200;

			if(this.debug)
				this.logger.debug("Connettore jms ha pubblicato con successo");
			
			this.dataRichiestaInoltrata = DateManager.getDate();
			
			
			
			
			
			
			
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
			String msgErrore = this.readExceptionMessageFromException(e);
			if(this.generateErrorWithConnectorPrefix) {
				this.errore = "Errore avvenuto durante la consegna JMS: "+msgErrore;
			}
			else {
				this.errore = msgErrore;
			}
			this.logger.error("Errore avvenuto durante la consegna JMS: "+msgErrore,e);
			try{
				// Rilascio Risorse
				if(sender!=null) {
					sender.close();
				}
			}catch(Exception ec){
				// close
			}
			try{
				if(qs!=null)
					qs.close();	
			}catch(Exception ec){
				// close
			}
			try{
				if(qc!=null)
					qc.close();

			}catch(Exception ec){
				// close
			}
			return false;
		}finally{
			try{
				if(ctxJMS!=null)
					ctxJMS.close();
			}catch(Exception close){
				// close
			}
			try{
				if(ctxLocalPool!=null)
					ctxLocalPool.close();
			}catch(Exception close){
				// close
			}
		}

	}



	/**
	 * Aggiunge una location in Cache
	 *
	 * @param key Chiave per la memorizzazione in cache
	 * @param destination Destinazione da inserire in cache
	 * 
	 */
	private static org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("ConnettoreJMS");
	public void putDestination(String key,Destination destination){
		SemaphoreLock lock = semaphore.acquireThrowRuntime("putDestination");
		try{
			ConnettoreJMS.locations.put(key,destination);
		}catch(Exception e){
			this.logger.error("ERROR INSERT CODA IN CACHE: "+e.getMessage(),e);
			//possibile inserimento della stessa coda....
		}finally {
			semaphore.release(lock, "putDestination");
		}
	}

}





