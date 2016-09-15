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



package org.openspcoop2.pools.pdd.jms.connectionsession;

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
import javax.jms.Connection;
import javax.jms.ConnectionFactory;



/**
 * PoolFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PoolFactory extends BasePooledObjectFactory<org.openspcoop2.pools.pdd.jms.connectionsession.Connection>{	

	/** ConnectionFactory */
	private ConnectionFactory qcf;
	/** ConnectionFactory */
	private String conFactory;
	/** context */
	private java.util.Properties context;

	/** Informazione sull'autenticazione */
	private String username;
	private String password;
	private String clientID;

	/** Informazione sul tipo di sessione */
	private boolean autoCommit;
	private int acknowledgeMode;

	/** Informazione se effettuare o meno la validazione di una sessione/connessione */
	private boolean validationJMSObject;

	/** Coda di test per la validazione della sessione/connessione */
	private String codaTestValidazione = null;
	private Destination destinazioneTestValidazione = null;
	
	/** Logger */
	private Logger logger = null;

	/** JNDIName */
	private String jndiName = null;
	
	
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
	public org.openspcoop2.pools.pdd.jms.connectionsession.Connection create() throws Exception {
		Connection con = null;
		Session s = null;
		this.logger.debug("CREATE OBJECT pool ["+this.jndiName+"]");
		try{

			// check ConnectionFactory
			if(this.qcf == null){
				GestoreJNDI jndi = new GestoreJNDI(this.context);
				this.qcf = (ConnectionFactory) jndi.lookup(this.conFactory);
			}

			// Connessione
			if(this.username!=null && this.password!=null)
				con = this.qcf.createConnection(this.username,this.password);
			else
				con = this.qcf.createConnection();
			if(con == null)
				throw new OpenSPCoopFactoryException("ConnessioneNonDisponibile");

			// ClientID
			if(this.clientID!=null){
				if( !this.clientID.equals(con.getClientID()) ){
					con.setClientID(this.clientID);
				}else{
					this.logger.debug("ClientID gia impostato pool ["+this.jndiName+"]");
				}
			}
						
			// Sessione
			if(this.autoCommit)
				s = con.createSession(false,this.acknowledgeMode);
			else
				s = con.createSession(true,-1);
			if(s == null){
				con.close();
				con = null;
				throw new OpenSPCoopFactoryException("SessioneNonDisponibile");
			}
			
			//testSessione
			testSessione(s);

			// Object JMS
			org.openspcoop2.pools.pdd.jms.connectionsession.Connection jms = 
				new org.openspcoop2.pools.pdd.jms.connectionsession.Connection(con,s);
			
			// ExceptionListener
			con.setExceptionListener(new ExceptionListener(jms,this.logger,this.jndiName));
			
			return jms;

		}catch(Exception e){
			this.logger.error("Create pool ["+this.jndiName+"] error["+e.getMessage()+"]",e);
			try{
				if(s!=null)
					s.close();
			}catch(Exception eClose){}
			try{
				if(con!=null)
					con.close();
			}catch(Exception eClose){}
			throw new OpenSPCoopFactoryException("CreateObject["+e.getMessage()+"]");
		}
	}

	@Override
	public PooledObject<org.openspcoop2.pools.pdd.jms.connectionsession.Connection> wrap(
			org.openspcoop2.pools.pdd.jms.connectionsession.Connection jms) {
		this.logger.debug("WRAP OBJECT pool ["+this.jndiName+"]");
		return new DefaultPooledObject<org.openspcoop2.pools.pdd.jms.connectionsession.Connection>(jms);
	}

	/**
	 * makeObject
	 *
	 * @return Un oggetto JMSObject contenente una connessione e sessione appena creata
	 * 
	 */
	@Override 
	public PooledObject<org.openspcoop2.pools.pdd.jms.connectionsession.Connection> makeObject() throws Exception{	
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
	public void activateObject(PooledObject<org.openspcoop2.pools.pdd.jms.connectionsession.Connection> param) throws Exception{ 
		this.logger.debug("ACTIVATE OBJECT pool ["+this.jndiName+"]");
		try{
			// Controllo dell'oggetto ricevuto
			if(param == null){
				throw new OpenSPCoopFactoryException("ParamNull");
			}
			org.openspcoop2.pools.pdd.jms.connectionsession.Connection jms = param.getObject();
			if(jms == null){
				throw new OpenSPCoopFactoryException("JMSObjectNull");
			}
			if(jms.getConnection()==null){
				throw new OpenSPCoopFactoryException("ConnectionNull");
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
	public void passivateObject(PooledObject<org.openspcoop2.pools.pdd.jms.connectionsession.Connection> param) throws Exception {
		this.logger.debug("PASSIVATE OBJECT pool ["+this.jndiName+"]");
		try{
			// Controllo dell'oggetto ricevuto
			if(param == null){
				throw new OpenSPCoopFactoryException("ParamNull");
			}
			org.openspcoop2.pools.pdd.jms.connectionsession.Connection jms = param.getObject();
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
			
			// Stop della connessione per la ricezione di messaggi se qualcuno l'ha attivata.
			try{
				jms.getConnection().stop();
			}catch(Exception e){
				this.logger.error("PassivateObject pool ["+this.jndiName+"] error connection.stop["+e.getMessage()+"]",e);
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
	public boolean validateObject(PooledObject<org.openspcoop2.pools.pdd.jms.connectionsession.Connection> param){
		this.logger.debug("VALIDATE OBJECT pool ["+this.jndiName+"]");
		if(this.validationJMSObject){
			try{
				// Controllo dell'oggetto ricevuto
				if(param == null){
					this.logger.error("VALIDAZIONE non riuscita pool ["+this.jndiName+"]: parameter is null");
					return false;
				}
				org.openspcoop2.pools.pdd.jms.connectionsession.Connection jms = param.getObject();
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

				// test connessione
				if(jms.getValidationExceptionListener()!=null){
					this.logger.error("VALIDAZIONE connessione (exception Listener) non riuscita pool ["+this.jndiName+"]: "+
							jms.getValidationExceptionListener().getMessage(),jms.getValidationExceptionListener());
					return false;
				}
				
				// Validazione Sessione
				try{
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

				// Validazione Connessione
				Session sessionTest = null;
				try{
					sessionTest = jms.getConnection().createSession(false,this.acknowledgeMode);
				}catch(Exception ee){	
					this.logger.error("VALIDAZIONE connessione non riuscita pool ["+this.jndiName+"]: "+ee.getMessage(),ee);
					return false;
				}finally{
					try{
						if(sessionTest!=null)
							sessionTest.close();
					}catch(Exception eClose){}
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
	public void destroyObject(PooledObject<org.openspcoop2.pools.pdd.jms.connectionsession.Connection> param) throws Exception{
		this.logger.debug("DESTROY OBJECT pool ["+this.jndiName+"]");
		try{
			StringBuffer eccezioni = new StringBuffer();
			
			// Controllo dell'oggetto ricevuto
			if(param == null){
				throw new OpenSPCoopFactoryException("ParamNull");
			}
			org.openspcoop2.pools.pdd.jms.connectionsession.Connection jms = param.getObject();
			if(jms == null){
				throw new OpenSPCoopFactoryException("JMSObjectNull");
			}

			if(jms.getConnection()==null){
				throw new OpenSPCoopFactoryException("ConnectionNull");
			}
			
			if(jms.getSession()==null){
				// Connessione
				try{
					jms.getConnection().stop();
				}catch(Exception e){
					eccezioni.append(" Connection.Stop["+e.getMessage()+"] ");
				}	
				try{
					jms.getConnection().close();
				}catch(Exception e){
					eccezioni.append(" Connection.Close["+e.getMessage()+"] ");
				}	
				throw new OpenSPCoopFactoryException("SessionNull "+eccezioni.toString());
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
			// Connessione
			try{
				jms.getConnection().stop();
			}catch(Exception e){
				eccezioni.append(" Connection.Stop["+e.getMessage()+"] ");
			}	
			try{
				jms.getConnection().close();
			}catch(Exception e){
				eccezioni.append(" Connection.Close["+e.getMessage()+"] ");
			}	

			jms = null;
			if(eccezioni.length()>0){
				this.logger.error("DestroyObject chiusure JMS non riuscite pool ["+this.jndiName+"] ["+eccezioni.toString()+"]");
			}
			
		}catch(Exception e){
			this.logger.error("DestroyObject pool ["+this.jndiName+"] error["+e.getMessage()+"]",e);
			throw new OpenSPCoopFactoryException("DestroyObject["+e.getMessage()+"]");
		}
	}


}
