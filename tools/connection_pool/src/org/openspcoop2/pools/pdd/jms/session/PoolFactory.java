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

import java.util.Vector;

import org.slf4j.Logger;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openspcoop2.pools.core.commons.OpenSPCoopFactoryException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.GestoreJNDI;

import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.ConnectionFactory;



/**
 * PoolFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PoolFactory extends BasePooledObjectFactory<org.openspcoop2.pools.pdd.jms.session.Connection>{	

	/** ConnectionFactory */
	private ConnectionFactory qcf;
	/** ConnectionFactory */
	private String conFactory;
	/** context */
	private java.util.Properties context;

	/** Connection */
	private javax.jms.Connection con;
	protected javax.jms.JMSException validationExceptionListener = null;
	
	/** Informazione sull'autenticazione */
	private String username;
	private String password;
	private String clientID;

	/** Numeri di sessioni allocate */
	//private long attesaAttivaSessioniAllocate = 30000l;
	//private int checkIntervalSessioniAllocate = 500;
	/**
	 * @param attesaAttivaSessioniAllocate the attesaAttivaSessioniAllocate to set
	 */
	/*public void setAttesaAttivaSessioniAllocate(long attesaAttivaSessioniAllocate) {
		this.attesaAttivaSessioniAllocate = attesaAttivaSessioniAllocate;
	}*/
	/**
	 * @param checkIntervalSessioniAllocate the checkIntervalSessioniAllocate to set
	 */
	/*public void setCheckIntervalSessioniAllocate(int checkIntervalSessioniAllocate) {
		this.checkIntervalSessioniAllocate = checkIntervalSessioniAllocate;
	}*/
	
	
	/*private Integer numeroSessioniAllocate = 0;
	private void incrementaNumeroSessioniAllocate(){
		synchronized(this.numeroSessioniAllocate){
			this.numeroSessioniAllocate++;
		}
	}
	private void decrementaNumeroSessioniAllocate(){
		synchronized(this.numeroSessioniAllocate){
			this.numeroSessioniAllocate--;
		}
	}*/
	
	
	
	/** Sessioni rilasciate */
	Vector<Session> sessioni = new Vector<Session>();
	
	/** Informazione sul tipo di sessione */
	private boolean autoCommit;
	private int acknowledgeMode;

	/** Informazione se effettuare o meno la validazione di una sessione/connessione */
	private boolean validationJMSObject;

	/** Coda di test per la validazione della sessione/connessione */
	private String codaTestValidazione = null;
	private Destination destinazioneTestValidazione = null;
	
	/** JNDIName */
	private String jndiName = null;
	
	/** Logger */
	private Logger logger = null;
	
	private void initConnection() throws OpenSPCoopFactoryException{
		this.logger.debug("INIT CONNECTION pool ["+this.jndiName+"]...");
		try{
			if(this.con==null){
				this.logger.debug("INIT CONNECTION pool ["+this.jndiName+"] creo connessione...");
				//	check ConnectionFactory
				if(this.qcf == null){
					GestoreJNDI jndi = new GestoreJNDI(this.context);
					this.qcf = (ConnectionFactory) jndi.lookup(this.conFactory);
				}
	
				// Connessione
				if(this.username!=null && this.password!=null)
					this.con = this.qcf.createConnection(this.username,this.password);
				else
					this.con = this.qcf.createConnection();
				if(this.con == null)
					throw new OpenSPCoopFactoryException("ConnessioneNonDisponibile");
				
				// ClientID
				if(this.clientID!=null){
					if( !this.clientID.equals(this.con.getClientID()) ){
						this.con.setClientID(this.clientID);
					}else{
						this.logger.debug("ClientID gia impostato pool ["+this.jndiName+"]");
					}
				}
				
				// ExceptionListener
				this.validationExceptionListener = null;
				this.con.setExceptionListener(new ExceptionListener(this,this.logger,this.jndiName));
				
				// Avvio connessione
				this.con.start();
				
				this.logger.debug("INIT CONNECTION pool ["+this.jndiName+"] creata connessione");
			}			
		}catch(Exception e){
			this.logger.error("InitConnection pool ["+this.jndiName+"] error",e);
			throw new OpenSPCoopFactoryException(e);
		}
	} 
	
	private void initCodaTestValidazione() throws OpenSPCoopFactoryException{
		if(this.codaTestValidazione!=null){
			this.logger.debug("INIT CodaTestValidazione pool ["+this.jndiName+"]");
			
			GestoreJNDI jndi = null;
			try{
				jndi = new GestoreJNDI(this.context);
				this.destinazioneTestValidazione = (Destination) jndi.lookup(this.codaTestValidazione);
				if(this.destinazioneTestValidazione==null)
					throw new OpenSPCoopFactoryException("Coda di test is null");
			}catch(Exception e){
				this.logger.error("initCodaTestValidazione pool ["+this.jndiName+"] error",e);
				throw new OpenSPCoopFactoryException(e);
			}
		}
	}
	
	
	public void releaseSessions() {
		for(int i=0;i<this.sessioni.size();i++){
				try{
					this.sessioni.get(i).close();
				}catch(Exception e){}
		}
		this.sessioni.clear();
	}
	
	
	/**
	 * Costruttore
	 *
	 * @param conFactory ConnectionFactory
	 * @param context Contesto JNDI dove localizzare la connection factory
	 * @param clientID clientID
	 * @param validation Indicazione se effettuare o meno la validazione di una sessione/connessione
	 * @param autoCommit Indicazione se si desidera una sessione transazionale o meno
	 * @param acknowledgeMode Modalita' di acknowledgment per sessioni non transazionali
	 * 
	 */
	public PoolFactory(String conFactory,java.util.Properties context,String clientID,
			boolean validation,boolean autoCommit,int acknowledgeMode,String jndiName,String codaTestValidazione) throws OpenSPCoopFactoryException{
		this(conFactory,context,null,null,clientID,validation,autoCommit,acknowledgeMode,jndiName,codaTestValidazione);
	}

	/**
	 * Costruttore
	 *
	 * @param conFactory ConnectionFactory
	 * @param context Contesto JNDI dove localizzare la connection factory
	 * @param user Username
	 * @param passw Password
	 * @param clientID clientID
	 * @param validation Indicazione se effettuare o meno la validazione di una sessione/connessione
	 * @param autoCommit Indicazione se si desidera una sessione transazionale o meno
	 * @param acknowledgeMode Modalita' di acknowledgment per sessioni non transazionali
	 * 
	 */
	public PoolFactory(String conFactory,java.util.Properties context,String user,String passw,String clientID,
			boolean validation,boolean autoCommit,int acknowledgeMode,String jndiName,String codaTestValidazione) throws OpenSPCoopFactoryException{

		// Logger
		try{
//			java.util.Properties loggerProperties = new java.util.Properties();
//			loggerProperties.load(PoolFactory.class.getResourceAsStream("/openspcoop2_pools.log4j2.properties"));
//			LoggerWrapperFactory.setLogConfiguration(loggerProperties);
			this.logger = LoggerWrapperFactory.getLogger("openspcoop2Pools");
		}catch(Exception e){
			this.logger = LoggerWrapperFactory.getLogger("openspcoop2Pools");
		}
		
		if(context==null)
			context = new java.util.Properties();

		GestoreJNDI jndi = new GestoreJNDI(context);
		this.conFactory = conFactory;
		this.context = context;
		try{
			this.qcf = (ConnectionFactory) jndi.lookup(conFactory);
		}catch(Exception e){}

		// Tipo di Sessione 
		this.autoCommit = autoCommit;
		this.acknowledgeMode = acknowledgeMode;

		// Validazione
		this.validationJMSObject = validation;

		// user/Password
		this.username = user;
		this.password = passw;
		this.clientID = clientID;
		
		// jndiName
		this.jndiName = jndiName;
		
		// coda di test
		this.codaTestValidazione = codaTestValidazione;
		
		// Crea connessione
		try{
			this.initConnection();
		}catch(Exception e){
			this.con = null;
			this.logger.error("InitConnectionPool non riuscita");
		}
		
		// initCodaTestValidazione
		this.initCodaTestValidazione();
		
	}
	/**
	 * Costruttore
	 *
	 * @param conFactory ConnectionFactory
	 * @param context Contesto JNDI dove localizzare la connection factory
	 * @param validation Indicazione se effettuare o meno la validazione di una sessione/connessione
	 * 
	 */
	public PoolFactory(String conFactory,java.util.Properties context,
			boolean validation,String jndiName,String codaTestValidazione) throws OpenSPCoopFactoryException{
		this(conFactory,context,null,validation,true,Session.AUTO_ACKNOWLEDGE,jndiName,codaTestValidazione);
	}



	// Il test e' sincronizzato per non far incasinare il broker, visto che viene creato un producer sulla solita coda di test.
	public synchronized void testSessione(Session s) throws OpenSPCoopFactoryException{
		
		if(this.destinazioneTestValidazione!=null){
			MessageProducer producerTest = null;
			//MessageConsumer consumerTest = null;
			this.logger.debug("Validazione Sessione (Producer) con coda di test["+this.codaTestValidazione+"] ["+this.jndiName+"]...");
			try{
				producerTest = s.createProducer(this.destinazioneTestValidazione);
			}catch(Exception e){
				throw new OpenSPCoopFactoryException("Test producer non riuscito:"+e.getMessage(),e);
			}finally{
				try{
					this.logger.debug("Chiusura producer...");
					if(producerTest!=null)
						producerTest.close();
				}catch(Exception e){
					this.logger.error("Chiusura producer non riuscita",e);
				}
			}
			this.logger.debug("Validazione Sessione (Producer) con coda di test["+this.codaTestValidazione+"] ["+this.jndiName+"] effettuata");
			/*
			 * CON GRANDE NUMERO DI OGGETTI NEL POOL LA VALIDAZIONE DEL CONSUMER FA CRASHARE IL BROKER
			this.logger.debug("Validazione Sessione (Consumer) con coda di test["+this.codaTestValidazione+"] ["+this.jndiName+"]...");
			try{
				consumerTest = s.createConsumer(this.destinazioneTestValidazione);
			}catch(Exception e){
				throw new OpenSPCoopFactoryException("Test consumer non riuscito:"+e.getMessage(),e);
			}finally{
				try{
					consumerTest.close();
				}catch(Exception e){}
			}*/
		}
	}
	


	@Override
	public org.openspcoop2.pools.pdd.jms.session.Connection create() throws Exception {
		Session s = null;
		this.logger.debug("CREATE OBJECT pool ["+this.jndiName+"]");
		try{

			// check connessione
			if(this.con == null){
				this.initConnection();
			}
			
			// La connessione deve essere utilizzata da uno alla volta
			synchronized(this.con){
			
				try{
					// test connessione
					if(this.validationExceptionListener!=null)
						throw this.validationExceptionListener;
					
					// Sessione
					if(this.autoCommit)
						s = this.con.createSession(false,this.acknowledgeMode);
					else
						s = this.con.createSession(true,-1);
					
					//testSessione
					testSessione(s);
					
				}catch(Exception e){
					// Puo' darsi che la connessione non sia piu' valida
					// Provo a re-inizializzarla
					this.logger.error("Errore durante la MAKE OBJECT, possibile che la connessione non sia piu' utilizzabile, provo a reinizializzarla.",e);
					
					// Attendo (30 secondi) che le sessioni vengano restituite prima di chiuderle.
					/*long scadenzaWhile = System.currentTimeMillis() + this.attesaAttivaSessioniAllocate; 
					while( System.currentTimeMillis() < scadenzaWhile ){
						this.logger.debug("MAKE OBJECT pool, attendo rilascio sessioni ("+this.numeroSessioniAllocate+" ancora allocate) prima di chiudere le sessioni e la connessione... (attendo ancora "+( (scadenzaWhile-System.currentTimeMillis())/1000 )+" secondi) ["+this.jndiName+"]");
						if(this.numeroSessioniAllocate==0){
							break;
						}
						try{
							Thread.sleep(this.checkIntervalSessioniAllocate);
						}catch(Exception eSleep){}
					}	*/				
					
					// Rilascio risorse
					this.releaseSessions();
					try{
						this.con.stop();
					}catch(Exception eClose){}
					try{
						this.con.close();
					}catch(Exception eClose){}
				
					// Re-inizializzo la connessione
					this.con=null;
					this.initConnection();
					
					s = null;
				}
				
				if(s==null){
					// Provo a costruire la sessione, stavolto non rinegozio
					if(this.autoCommit)
						s = this.con.createSession(false,this.acknowledgeMode);
					else
						s = this.con.createSession(true,-1);
					
					//testSessione
					testSessione(s);
				}
				
			}
			if(s == null){
				throw new OpenSPCoopFactoryException("SessioneNonDisponibile");
			}

			// Object JMS
			org.openspcoop2.pools.pdd.jms.session.Connection jms = 
				new org.openspcoop2.pools.pdd.jms.session.Connection(this.con,s);
			
			// Gestione gestita
			this.sessioni.add(s);
			
			//this.incrementaNumeroSessioniAllocate();
			
			return jms;

		}catch(Exception e){
			this.logger.error("CreateObject pool ["+this.jndiName+"] error["+e.getMessage()+"]",e);
			try{
				if(s!=null)
					s.close();
			}catch(Exception eClose){}
			throw new OpenSPCoopFactoryException("CreateObject["+e.getMessage()+"]");
		}
	}
	
	@Override
	public PooledObject<org.openspcoop2.pools.pdd.jms.session.Connection> wrap(
			org.openspcoop2.pools.pdd.jms.session.Connection jms) {
		this.logger.debug("WRAP OBJECT pool ["+this.jndiName+"]");
		return new DefaultPooledObject<org.openspcoop2.pools.pdd.jms.session.Connection>(jms);
	}

	/**
	 * makeObject
	 *
	 * @return Un oggetto JMSObject contenente una connessione e sessione appena creata
	 * 
	 */
	@Override 
	public PooledObject<org.openspcoop2.pools.pdd.jms.session.Connection> makeObject() throws Exception{	
		this.logger.debug("MAKE OBJECT pool ["+this.jndiName+"]");
		return wrap(this.create());
	}

	/**
	 * activateObject
	 *
	 * @param param Un oggetto JMSObject contenente una connessione e sessione appena creata
	 * 
	 */
	@Override 
	public void activateObject(PooledObject<org.openspcoop2.pools.pdd.jms.session.Connection> param) throws Exception{ 
		this.logger.debug("ACTIVATE OBJECT pool ["+this.jndiName+"]");
		try{
			// Controllo dell'oggetto ricevuto
			if(param == null){
				throw new OpenSPCoopFactoryException("ParamNull");
			}
			org.openspcoop2.pools.pdd.jms.session.Connection jms = param.getObject();
			if(jms == null){
				throw new OpenSPCoopFactoryException("JMSObjectNull");
			}
			if(jms.getConnection()==null){
				throw new OpenSPCoopFactoryException("ConnectionNull");
			}
			if(jms.getSession()==null){
				throw new OpenSPCoopFactoryException("SessionNull");
			}

		}catch(Exception e){
			this.logger.error("ActivateObject pool ["+this.jndiName+"] error["+e.getMessage()+"]",e);
			throw new OpenSPCoopFactoryException("ActivateObject["+e.getMessage()+"]");
		}
	}


	/**
	 * passivateObject
	 *
	 * @param param Un oggetto JMSObject contenente una connessione e sessione attiva
	 * 
	 */
	@Override 
	public void passivateObject(PooledObject<org.openspcoop2.pools.pdd.jms.session.Connection> param) throws Exception {
		this.logger.debug("PASSIVATE OBJECT pool ["+this.jndiName+"]");
		try{
			// Controllo dell'oggetto ricevuto
			if(param == null){
				throw new OpenSPCoopFactoryException("ParamNull");
			}
			org.openspcoop2.pools.pdd.jms.session.Connection jms = param.getObject();
			if(jms == null){
				throw new OpenSPCoopFactoryException("JMSObjectNull");
			}
			if(jms.getConnection()==null){
				throw new OpenSPCoopFactoryException("ConnectionNull");
			}
			if(jms.getSession()==null){
				throw new OpenSPCoopFactoryException("SessionNull");
			}

			// Faccio il roolback, se e' transazionale
			// Se uno aveva correttamente chiamato il commit, l'operazione e' corretta.
			if(this.autoCommit==false){
				try{
					jms.getSession().rollback();
				}catch(Exception e){
					this.logger.error("PassivateObject pool ["+this.jndiName+"] error sessione.rollback["+e.getMessage()+"]",e);
				}
			}

		}catch(Exception e){
			this.logger.error("PassivateObject pool ["+this.jndiName+"] error["+e.getMessage()+"]",e);
			throw new OpenSPCoopFactoryException("PassivateObject["+e.getMessage()+"]");
		}
	}


	/**
	 * validateObject
	 *
	 * @param param Un oggetto JMSObject contenente una connessione e sessione attiva
	 * 
	 */
	@Override 
	public boolean validateObject(PooledObject<org.openspcoop2.pools.pdd.jms.session.Connection> param){
		this.logger.debug("VALIDATE OBJECT pool ["+this.jndiName+"]");
		if(this.validationJMSObject){
			try{
				// Controllo dell'oggetto ricevuto
				if(param == null){
					this.logger.error("VALIDAZIONE non riuscita pool ["+this.jndiName+"]: parameter is null");
					return false;
				}
				org.openspcoop2.pools.pdd.jms.session.Connection jms = param.getObject();
				if(jms == null){
					this.logger.error("VALIDAZIONE non riuscita pool ["+this.jndiName+"]: risorsa is null");
					return false;
				}
				if(jms.getConnection()==null){
					this.logger.error("VALIDAZIONE non riuscita pool ["+this.jndiName+"]: connessione is null");
					return false;
				}
				if(jms.getSession()==null){
					this.logger.error("VALIDAZIONE non riuscita pool ["+this.jndiName+"]: sessione is null");
					return false;
				}

				// test oggetto non piu' valido (perche' la connessione non era piu' valida)
				if(this.sessioni.contains(jms.getSession())==false){
					this.logger.debug("VALIDAZIONE sessione non riuscita pool ["+this.jndiName+"]: sessione non piu' valida poiche' creata con una precedente connessione");
					return false;
				}
				
				// test connessione
				if(this.validationExceptionListener!=null){
					this.logger.error("VALIDAZIONE connessione (exception Listener) non riuscita pool ["+this.jndiName+"]: "+this.validationExceptionListener.getMessage(),this.validationExceptionListener);
					return false;
				}
				
				// Validazione Sessione
				try{
					// crezione messaggio
					jms.getSession().createMessage();
				}catch(Exception ee){
					this.logger.error("VALIDAZIONE sessione non riuscita pool ["+this.jndiName+"]: "+ee.getMessage(),ee);
					return false;
				}
				try{
					//testSessione
					testSessione(jms.getSession());
				}catch(Exception ee){
					this.logger.error("VALIDAZIONE sessione consumer/producer non riuscita pool ["+this.jndiName+"]: "+ee.getMessage(),ee);
					return false;
				}
				
				return true;

			}catch(Exception e){
				this.logger.error("VALIDAZIONE non riuscita pool ["+this.jndiName+"]: "+e.getMessage(),e);
				return false;
			}
		}else{
			return true;
		}
	}

	@Override 
	public void destroyObject(PooledObject<org.openspcoop2.pools.pdd.jms.session.Connection> param) throws Exception{
		this.logger.debug("DESTROY OBJECT pool ["+this.jndiName+"]");
		try{
			StringBuffer eccezioni = new StringBuffer();
			
			// Controllo dell'oggetto ricevuto
			if(param == null){
				throw new OpenSPCoopFactoryException("ParamNull");
			}
			org.openspcoop2.pools.pdd.jms.session.Connection jms = param.getObject();
			if(jms == null){
				throw new OpenSPCoopFactoryException("JMSObjectNull");
			}
			
			if(jms.getConnection()==null){
				throw new OpenSPCoopFactoryException("ConnectionNull");
			}
			
			// Rilascio delle risorse
			// Sessione
			try{
				if(this.autoCommit==false){
					jms.getSession().rollback();
				}
			}catch(Exception e){
				eccezioni.append(" RollbackError["+e.getMessage()+"] ");
			}
			try{
				jms.getSession().close();
			}catch(Exception e){
				eccezioni.append(" Session.Close["+e.getMessage()+"] ");
			}
			
			
			// Stampo eccezioni o elimino dalle sessioni solo se l'oggetto e' ancora valido (perche' la connessione e' ancora valida)
			if( this.sessioni.contains(jms.getSession()) ){
			
				if(eccezioni.length()>0){
					this.logger.error("DestroyObject chiusure JMS non riuscite pool ["+this.jndiName+"] ["+eccezioni.toString()+"]");
				}
				this.sessioni.remove(jms.getSession());
				
			}
			
			//this.decrementaNumeroSessioniAllocate();

			jms = null;
			
		}catch(Exception e){
			this.logger.error("DestroyObject pool ["+this.jndiName+"] error["+e.getMessage()+"]",e);
			throw new OpenSPCoopFactoryException("DestroyObject["+e.getMessage()+"]");
		}
	}


	
	public void closeConnection(){
		try{
			this.con.stop();
		}catch(Exception eClose){}
		try{
			this.con.close();
		}catch(Exception eClose){}
	}
	
}
