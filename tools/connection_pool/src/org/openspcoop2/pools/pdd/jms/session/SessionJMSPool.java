/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.pools.pdd.jms.session;

import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Session;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.openspcoop2.pools.core.commons.Costanti;
import org.openspcoop2.pools.core.commons.OpenSPCoopFactoryException;
import org.openspcoop2.pools.pdd.jms.JMSInfo;
import org.openspcoop2.utils.LoggerWrapperFactory;


/**
 * SessionJMSPool
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SessionJMSPool implements java.io.Serializable  {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** Comportamento dell'acknowledgment: AUTO_ACKNOWLEDGE.
    With this acknowledgment mode, the session automatically acknowledges a client's receipt of a message 
    either when the session has successfully returned from a call to receive or 
    when the message listener the session has called to process the message successfully returns. */
	public static final int AUTO_ACKNOWLEDGE = Session.AUTO_ACKNOWLEDGE;
	/** Comportamento dell'acknowledgment: CLIENT_ACKNOWLEDGE.
    With this acknowledgment mode, the client acknowledges a consumed message 
    by calling the message's acknowledge method. */
	public static final int CLIENT_ACKNOWLEDGE = Session.CLIENT_ACKNOWLEDGE;
	/** Comportamento dell'acknowledgment: DUPS_OK_ACKNOWLEDGE.
    This acknowledgment mode instructs the session to lazily acknowledge the delivery of messages. */
	public static final int DUPS_OK_ACKNOWLEDGE = Session.DUPS_OK_ACKNOWLEDGE;

	
	/** Hash Pool utilizzato ed indirizzato con il nome JNDI */
	private static Map<String,GenericObjectPool<org.openspcoop2.pools.pdd.jms.session.Connection>> pool = 
			new ConcurrentHashMap<String,GenericObjectPool<org.openspcoop2.pools.pdd.jms.session.Connection>>();
	private static Map<String,PoolFactory> poolFactory = new ConcurrentHashMap<String,PoolFactory>();
	/** NomeJNDI di questo oggetto */
	private String jndiName;

	/** Logger */
	private static Logger staticLogger = null;
	
	/**
	 * Viene chiamato in causa per istanziare il datasource
	 *
	 * 
	 */
	public SessionJMSPool(JMSInfo configuration) throws OpenSPCoopFactoryException {

		// Logger
		if(SessionJMSPool.staticLogger==null){
			try{
//				java.util.Properties loggerProperties = new java.util.Properties();
//				loggerProperties.load(SessionJMSPool.class.getResourceAsStream("/openspcoop2_pools.log4j2.properties"));
//				LoggerWrapperFactory.setLogConfiguration(loggerProperties);
				SessionJMSPool.staticLogger = LoggerWrapperFactory.getLogger("govwayPools");
			}catch(Exception e){
				SessionJMSPool.staticLogger = LoggerWrapperFactory.getLogger("govwayPools");
			}
		}

		
		// Controllo che venga fornita la configurazione di un OpenSPCoopQueueManager
		if(configuration==null){
			throw new OpenSPCoopFactoryException("Errore: Configurazione dell'OpenSPCoopQueueManager non fornita");
		}

		// Controllo che non esista gia' un pool registrato con il nome JNDI
		if(configuration.getJndiName()==null){
			throw new OpenSPCoopFactoryException("Errore: Nome JNDI, da associare al pool, non fornito");
		}else if (SessionJMSPool.pool.containsKey(configuration.getJndiName())==true){
			throw new OpenSPCoopFactoryException("Errore: Nome JNDI, da associare al pool, gia registrato come oggetto OpenSPCoopQueueManager");
		}
		this.jndiName = configuration.getJndiName();

		// Configurazione del Pool
		GenericObjectPoolConfig<org.openspcoop2.pools.pdd.jms.session.Connection> configPool = new GenericObjectPoolConfig<org.openspcoop2.pools.pdd.jms.session.Connection>();
		setConfigurazione(configPool,configuration);

		// User e Password
		String user = configuration.getUsername();
		String password = configuration.getPassword();

		// jndiContext
		java.util.Properties jndiContext = new java.util.Properties();
		for(int i=0; i<configuration.sizeContextPropertyList();i++)
			jndiContext.put(configuration.getContextProperty(i).getName(),configuration.getContextProperty(i).getValue());

		// Validazione Attiva
		boolean validazioneAttiva = configuration.isValidation_abilitato();

		// Acknowledgement
		int acknowledgmentType = SessionJMSPool.AUTO_ACKNOWLEDGE;
		if(configuration.getAcknowledgmentType()!=null){
			if(Costanti.ACKNOWLEDGMENT_AUTO.equalsIgnoreCase(configuration.getAcknowledgmentType()))
				acknowledgmentType = SessionJMSPool.AUTO_ACKNOWLEDGE;
			else if(Costanti.ACKNOWLEDGMENT_CLIENT.equalsIgnoreCase(configuration.getAcknowledgmentType()))
				acknowledgmentType = SessionJMSPool.CLIENT_ACKNOWLEDGE;
			else if(Costanti.ACKNOWLEDGMENT_DUPS_OK.equalsIgnoreCase(configuration.getAcknowledgmentType()))
				acknowledgmentType = SessionJMSPool.DUPS_OK_ACKNOWLEDGE;
			else
				acknowledgmentType = SessionJMSPool.AUTO_ACKNOWLEDGE;
		}

		// ConnectionFactory
		PoolFactory pf = 
			new PoolFactory(
					configuration.getConnectionFactory(), // ConnectionFactory
					jndiContext, // Context JNDI
					user,password, //user e password
					configuration.getClientId(), // client ID
					validazioneAttiva, // Validazione connessione/sessione
					configuration.isAutoCommit(), // AutoCommit
					acknowledgmentType, // tipo di acknowledgment 
					// (AUTO_ACKNOWLEDGE,CLIENT_ACKNOWLEDGE,DUPS_OK_ACKNOWLEDGE)
					this.jndiName, //jndiname
					configuration.getValidation_operation()
			); 
		/*for(int i=0; i<configuration.sizeContextPropertyList(); i++){
			if("attesaAttivaRilascioSessioniAllocate".equals(configuration.getContextProperty(i).getName())){
				try{
					long v = Long.parseLong(configuration.getContextProperty(i).getValue());
					pf.setAttesaAttivaSessioniAllocate(v);
				}catch(Exception e){}
			}else  if("intervalloCheckRilascioSessioniAllocate".equals(configuration.getContextProperty(i).getName())){
				try{
					int v = Integer.parseInt(configuration.getContextProperty(i).getValue());
					pf.setCheckIntervalSessioniAllocate(v);
				}catch(Exception e){}
			}
		}*/


		/*
	  As the name says, this is a generic pool; it returns
	  basic Object-class objects.
		 */
		SessionJMSPool.pool.put(this.jndiName,new GenericObjectPool<org.openspcoop2.pools.pdd.jms.session.Connection>(pf,configPool));
		SessionJMSPool.poolFactory.put(this.jndiName,pf);

		/*
	  initialize the pool 
		 */
		try{
			for (int ix = 0 ; ix < configuration.getPool_initial() ; ++ix ) {
				SessionJMSPool.pool.get(this.jndiName).addObject() ;
			}	
		}catch(Exception e){
			LoggerWrapperFactory.getLogger("SessionJMSPool").error("(OpenSPCoopQueueManager_"+this.jndiName+") Riscontrato errore durante l'inizializzazione del Pool["
					+this.jndiName+"]: "+ e.getMessage());
		}

	}


	/**
	 * Ritorna un JMSObject che contiene una connessione/sessione al JMS Broker
	 *
	 * @return JMSObject.
	 * 
	 */
	public synchronized org.openspcoop2.pools.pdd.jms.session.Connection getResource() throws OpenSPCoopFactoryException{

		try {
			SessionJMSPool.staticLogger.debug("getResource pool ["+this.jndiName+"]");
			
			Object obj = SessionJMSPool.pool.get(this.jndiName).borrowObject();
			
			if(obj ==null)
				throw new Exception("is null");
			org.openspcoop2.pools.pdd.jms.session.Connection jms = (org.openspcoop2.pools.pdd.jms.session.Connection) obj;
			jms.setJndiNamePool(this.jndiName);
			
			return jms;
		}
		catch(Exception e) {
			SessionJMSPool.staticLogger.error("Riscontrato errore durante la getResource pool ["+this.jndiName+"]:"
					+e.getMessage());
			throw new OpenSPCoopFactoryException("getResource pool ["+this.jndiName+"]: "+e.getMessage());
		}
	}

	/**
	 * Restituisce un JMSObject al pool
	 *
	 * @param resource JMSObject.
	 * 
	 */
	public static synchronized void releaseResource(org.openspcoop2.pools.pdd.jms.session.Connection resource) throws OpenSPCoopFactoryException{

		
		try {
			
			if(resource!=null){
				
				if(resource.getJndiNamePool()==null){
					throw new Exception("JNDI Name is null");
				}
				
				SessionJMSPool.staticLogger.debug("releaseResource pool ["+resource.getJndiNamePool()+"]");
				
				SessionJMSPool.pool.get(resource.getJndiNamePool()).returnObject(resource);
			}
		}
		catch(Exception e) {
			if(resource.getJndiNamePool()==null){
				SessionJMSPool.staticLogger.error("Riscontrato errore durante la releaseResource:"
						+e.getMessage());
				throw new OpenSPCoopFactoryException("releaseResource: "+e.getMessage());
			}else{
				SessionJMSPool.staticLogger.error("Riscontrato errore durante la releaseResource pool ["+resource.getJndiNamePool()+"]:"
						+e.getMessage());
				throw new OpenSPCoopFactoryException("releaseResource pool ["+resource.getJndiNamePool()+"]: "+e.getMessage());
			}
		}
	}


	/**
	 * Effettua la configurazione di un Pool, passato i parametri di configurazione come parametro.
	 *
	 * 
	 */
	public void setConfigurazione(GenericObjectPoolConfig<org.openspcoop2.pools.pdd.jms.session.Connection> configPool,JMSInfo configuration) {	
		// controls the maximum number of objects that can be borrowed from the pool at one time. 
		// When non-positive, there is no limit to the number of objects that may be active at one time. 
		// When maxActive is exceeded, the pool is said to be exhausted.
		configPool.setMaxTotal(configuration.getPool_max()); // Default:8   Serve per fissare la Max dimensione del Pool

		// controls the maximum number of objects that can sit idle in the pool at any time. 
		// When negative, there is no limit to the number of objects that may be idle at one time.
		configPool.setMaxIdle(configuration.getPool_min());  // Default: 8   Serve per fissare la minima dimensione del Pool

		// Returns the minimum number of objects allowed in the pool before the evictor thread (if active) spawns new objects. 
		// (Note no objects are created when: numActive + numIdle >= maxActive)
		//configPool.minIdle=2;  // Default: 0  

		// whenExhaustedAction specifies the behaviour of the borrowObject() method when the pool is exhausted:
		// * When whenExhaustedAction is WHEN_EXHAUSTED_FAIL, borrowObject() will throw a NoSuchElementException
		// * When whenExhaustedAction is WHEN_EXHAUSTED_GROW, 
		//   borrowObject() will create a new object and return it(essentially making maxActive meaningless.)
		//* When whenExhaustedAction is WHEN_EXHAUSTED_BLOCK, borrowObject() will block 
		//  (invoke Object.wait(long) until a new or idle object is available. 
		//  If a positive maxWait value is supplied, the borrowObject() will block for at most that many milliseconds, 
		//  after which a NoSuchElementException will be thrown. 
		//  If maxWait is non-positive, the borrowObject() method will block indefinitely.
		if(configuration.getWhen_exhausted_action() == null){
			configPool.setBlockWhenExhausted(true);
		}else if(Costanti.WHEN_EXHAUSTED_BLOCK.equalsIgnoreCase(configuration.getWhen_exhausted_action()) ){
			configPool.setBlockWhenExhausted(true);
		}else if(Costanti.WHEN_EXHAUSTED_FAIL.equalsIgnoreCase(configuration.getWhen_exhausted_action()) ){
			configPool.setBlockWhenExhausted(false);
		}else{
			configPool.setBlockWhenExhausted(true);
		}
		if(configuration.getWhen_exhausted_blockingTimeout()>0){
			configPool.setMaxWaitMillis(configuration.getWhen_exhausted_blockingTimeout()); //Default: -1L
		}

		// When testOnBorrow is set, the pool will attempt to validate each object before it is returned from the borrowObject() method. 
		// (Using the provided factory's PoolableObjectFactory.validateObject(java.lang.Object) method.) 
		// Objects that fail to validate will be dropped from the pool, and a different object will be borrowed.
		configPool.setTestOnBorrow(configuration.isValidation_testOnGet()); //Default: false

		// When testOnReturn is set, the pool will attempt to validate each object before it is returned to the pool 
		// in the returnObject(java.lang.Object) method. 
		// (Using the provided factory's PoolableObjectFactory.validateObject(java.lang.Object)  method.) 
		// Objects that fail to validate will be dropped from the pool.
		configPool.setTestOnReturn(configuration.isValidation_testOnRelease()); //Default: false

		// Optionally, one may configure the pool to examine and possibly evict objects as they sit idle in the pool. 
		// This is performed by an "idle object eviction" thread, which runs asychronously. 
		// The idle object eviction thread may be configured using the following attributes:
		if( configuration.isIdle_abilitato() ){
			// - timeBetweenEvictionRunsMillis,  indicates how long the eviction thread should sleep before 
			// "runs" of examining idle objects. 
			//   When non-positive, no eviction thread will be launched.
			configPool.setTimeBetweenEvictionRunsMillis(configuration.getIdle_timeBetweenEvictionRuns()); //Default: -1L
			// - numTestsPerEvictionRun, The default number of objects to examine per run in the idle object evictor.
			configPool.setNumTestsPerEvictionRun(configuration.getIdle_numTestsPerEvictionRun()); //Default: 3
			// - minEvictableIdleTimeMillis,  specifies the minimum amount of time that an object may sit idle in the pool 
			// before it is eligable for eviction due to idle time. 
			// When non-positive, no object will be dropped from the pool due to idle time alone.
			configPool.setMinEvictableIdleTimeMillis(configuration.getIdle_idleObjectTimeout()); //Default: 1800000L
			// - testWhileIdle, indicates whether or not idle objects should be validated using the factory's 
			// PoolableObjectFactory.validateObject(java.lang.Object) method. Objects that fail to validate will be dropped from the pool.
			configPool.setTestWhileIdle(configuration.isIdle_validateObject()); //Default: false
		}
	}



	/**
	 * Rilascia le risorse allocate per il Pool
	 *
	 * 
	 */
	public void close() throws Exception {	
		SessionJMSPool.pool.get(this.jndiName).close();
		SessionJMSPool.pool.remove(this.jndiName);
		SessionJMSPool.poolFactory.get(this.jndiName).closeConnection();
		SessionJMSPool.poolFactory.remove(this.jndiName);
	}


	/**
	 * Ritorna lo stato del Pool
	 *
	 * 
	 */
	public String getStatoConnessioni() {	
		return "Pool ["+this.jndiName+"] attivo con "
		+SessionJMSPool.pool.get(this.jndiName).getNumIdle() + " connessioni esistenti nel pool e "+
		+SessionJMSPool.pool.get(this.jndiName).getNumActive()+" connessioni fornite.";
	}
}
