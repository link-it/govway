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



package org.openspcoop2.pools.pdd.db;

import java.sql.Connection;
import java.util.HashSet;

import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.openspcoop2.pools.core.commons.Costanti;
import org.openspcoop2.pools.core.commons.OpenSPCoopFactoryException;
import org.openspcoop2.utils.resources.GestoreJNDI;


/**
 * Contiene la definizione di un DataSource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class DBPool {	

	public static final String EXTENSION_CONNECTION_POOL_DATASOURCE = ".CommonsPoolDataSource";
	
	
	/** Livello di isolamento: TRANSACTION_NONE.
    A constant indicating that transactions are not supported. */
	public static final int TRANSACTION_NONE = Connection.TRANSACTION_NONE;
	/** Livello di isolamento: TRANSACTION_READ_COMMITTED.
    A constant indicating that dirty reads are prevented; non-repeatable reads and phantom reads can occur. */
	public static final int TRANSACTION_READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;
	/** Livello di isolamento: TRANSACTION_READ_UNCOMMITTED.
    A constant indicating that dirty reads, non-repeatable reads and phantom reads can occur.*/
	public static final int TRANSACTION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;
	/** Livello di isolamento: TRANSACTION_REPEATABLE_READ.
    A constant indicating that dirty reads and non-repeatable reads are prevented; phantom reads can occur.*/
	public static final int TRANSACTION_REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;
	/** Livello di isolamento: TRANSACTION_SERIALIZABLE.
    A constant indicating that dirty reads, non-repeatable reads and phantom reads are prevented. */
	public static final int TRANSACTION_SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;


	/** Hash Pool utilizzato ed indirizzato con il nome JNDI */
	private static HashSet<String> nomiJNDI = new HashSet<String>();
	


	/**
	 * Costruisce un CommonsPoolDataSource
	 *
	 * 
	 */
	public static DriverAdapterCPDS buildConnectionPoolDataSource(DBInfo configuration) throws OpenSPCoopFactoryException {

		try{
			
			//	Controllo che venga fornita la configurazione di un DataSource
			if(configuration==null){
				throw new Exception("Errore: Configurazione del CommonsPoolDataSource non fornita");
			}

			// Controllo che non esista gia' un pool registrato con il nome JNDI
			if(configuration.getJndiName()==null){
				throw new Exception("Errore: Nome JNDI, da associare al pool, non fornito");
			}
			String jndiNameConnectionPoolDataSource = DBPool.buildConnectionPoolDataSourceJNDIName(configuration.getJndiName());
			if (DBPool.nomiJNDI.contains(jndiNameConnectionPoolDataSource)==true){
				throw new Exception("Errore: Nome JNDI, da associare al pool, gia registrato come oggetto ConnectionPoolDataSource");
			}
			
			//	 CommonsPoolDataSource
			DriverAdapterCPDS commonsPoolDataSource = new DriverAdapterCPDS();
			
			// instantiate the JDBC driver class. 
			commonsPoolDataSource.setDriver(configuration.getDriverClass());

			/* Autenticazione */
			if(configuration.getUsername()!=null &&
					configuration.getPassword()!=null){
				commonsPoolDataSource.setUser(configuration.getUsername());
				commonsPoolDataSource.setPassword(configuration.getPassword());
			}

			/* connection url */
			commonsPoolDataSource.setUrl(configuration.getConnectionUrl());

			/* utilizzo o meno di una cache per le PreparedStatement */
			commonsPoolDataSource.setPoolPreparedStatements(configuration.isPreparedStatementPool());

			// controls the maximum number of objects that can sit idle in the pool at any time. 
			// When negative, there is no limit to the number of objects that may be idle at one time.
			commonsPoolDataSource.setMaxIdle(configuration.getPool_min());  // Default: 8   Serve per fissare la minima dimensione del Pool

			// Returns the minimum number of objects allowed in the pool before the evictor thread (if active) spawns new objects. 
			// (Note no objects are created when: numActive + numIdle >= maxActive)
			//commonsPoolDataSource.setMinIdle(2);  // Default: 0  

			// Optionally, one may configure the pool to examine and possibly evict objects as they sit idle in the pool. 
			// This is performed by an "idle object eviction" thread, which runs asychronously. 
			// The idle object eviction thread may be configured using the following attributes:
			if( configuration.isIdle_abilitato()){
				// - timeBetweenEvictionRunsMillis,  indicates how long the eviction thread should sleep before 
				// "runs" of examining idle objects. 
				//   When non-positive, no eviction thread will be launched.
				commonsPoolDataSource.setTimeBetweenEvictionRunsMillis(configuration.getIdle_timeBetweenEvictionRuns()); //Default: -1L
				// - numTestsPerEvictionRun, The default number of objects to examine per run in the idle object evictor.
				commonsPoolDataSource.setNumTestsPerEvictionRun(configuration.getIdle_numTestsPerEvictionRun()); //Default: 3
				// - minEvictableIdleTimeMillis,  specifies the minimum amount of time that an object may sit idle in the pool 
				// before it is eligable for eviction due to idle time. 
				// When non-positive, no object will be dropped from the pool due to idle time alone.
				commonsPoolDataSource.setMinEvictableIdleTimeMillis(configuration.getIdle_idleObjectTimeout()); //Default: 1800000L
			}
			
			return commonsPoolDataSource;
						
		}catch(Exception e){
			String msgError = "Riscontrato errore durante la creazione del buildCommonsPoolDataSource ["+configuration.getJndiName()+"]: "+e.getMessage();
			throw new OpenSPCoopFactoryException(msgError,e);
		}

	}
	
	/**
	 * Costruisce un SharedPoolDataSource
	 *
	 * 
	 */
	public static SharedPoolDataSource buildDataSource(DriverAdapterCPDS cpds,DBInfo configuration) throws OpenSPCoopFactoryException {

		try{

			// Controllo che venga fornita la configurazione di un DataSource
			if(configuration==null){
				throw new Exception("Errore: Configurazione del DataSource non fornita");
			}

			// Controllo che non esista gia' un pool registrato con il nome JNDI
			if(configuration.getJndiName()==null){
				throw new Exception("Errore: Nome JNDI, da associare al pool, non fornito");
			}else if (DBPool.nomiJNDI.contains(configuration.getJndiName())==true){
				throw new Exception("Errore: Nome JNDI, da associare al pool, gia registrato come oggetto OpenSPCoopDataSource");
			}
			
			//	DataSource 
			SharedPoolDataSource ds = new SharedPoolDataSource();

			// NomeJNDI del connectionPoolDataSource
			if(configuration.getJndiName().startsWith(GestoreJNDI.LOCAL_TREE_JNDI_PREFIX)){
				//System.out.println("------------------------------------------------------- Associazione Locale");
				ds.setConnectionPoolDataSource(cpds);
			}
			else{
				String jndiNameConnectionPoolDataSource = DBPool.buildConnectionPoolDataSourceJNDIName(configuration.getJndiName());
				ds.setDataSourceName(jndiNameConnectionPoolDataSource);
			}
			
			//ds.setJndiEnvironment("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
			//ds.setJndiEnvironment("java.naming.factory.url.pkgs","org.jnp.interfaces");
			//ds.setJndiEnvironment("java.naming.provider.url","127.0.0.1");
			
			
			/* utilizzo o meno di una validazione sql */
			if(configuration.isValidation_abilitato() &&
					configuration.getValidation_operation()!=null){
				ds.setValidationQuery(configuration.getValidation_operation());
			}

			/* Read Only */
			ds.setDefaultReadOnly(configuration.isReadOnly());

			/* Auto Commit */
			ds.setDefaultAutoCommit(configuration.isAutoCommit());

			/* Transaction Isolation */
			if(configuration.getTransactionIsolation()!=null){
				if(Costanti.TRANSACTION_ISOLATION_NONE.equalsIgnoreCase(configuration.getTransactionIsolation())==false){
					if(Costanti.TRANSACTION_ISOLATION_READ_COMMITTED.equalsIgnoreCase(configuration.getTransactionIsolation()))
						ds.setDefaultTransactionIsolation(DBPool.TRANSACTION_READ_COMMITTED);
					else if(Costanti.TRANSACTION_ISOLATION_READ_UNCOMMITTED.equalsIgnoreCase(configuration.getTransactionIsolation()))
						ds.setDefaultTransactionIsolation(DBPool.TRANSACTION_READ_UNCOMMITTED);
					else if(Costanti.TRANSACTION_ISOLATION_REPEATABLE_READ.equalsIgnoreCase(configuration.getTransactionIsolation()))
						ds.setDefaultTransactionIsolation(DBPool.TRANSACTION_REPEATABLE_READ);
					else if(Costanti.TRANSACTION_ISOLATION_SERIALIZABLE.equalsIgnoreCase(configuration.getTransactionIsolation()))
						ds.setDefaultTransactionIsolation(DBPool.TRANSACTION_SERIALIZABLE);
					else
						ds.setDefaultTransactionIsolation(DBPool.TRANSACTION_READ_COMMITTED);
				}
			}else{
				ds.setDefaultTransactionIsolation(DBPool.TRANSACTION_READ_COMMITTED);
			}

			// controls the maximum number of objects that can be borrowed from the pool at one time. 
			// When non-positive, there is no limit to the number of objects that may be active at one time. 
			// When maxActive is exceeded, the pool is said to be exhausted.
			ds.setMaxTotal(configuration.getPool_max()); // Default:8   Serve per fissare la Max dimensione del Pool
			ds.setDefaultMaxTotal(configuration.getPool_max());
			
			// controls the maximum number of objects that can sit idle in the pool at any time. 
			// When negative, there is no limit to the number of objects that may be idle at one time.
			ds.setDefaultMaxIdle(configuration.getPool_min());  // Default: 8   Serve per fissare la minima dimensione del Pool

			// Returns the minimum number of objects allowed in the pool before the evictor thread (if active) spawns new objects. 
			// (Note no objects are created when: numActive + numIdle >= maxActive)
			//ds.setMinIdle(2);  // Default: 0  

			// whenExhaustedAction specifies the behaviour of the borrowObject() method when the pool is exhausted:
			// * When whenExhaustedAction is WHEN_EXHAUSTED_FAIL, borrowObject() will throw a NoSuchElementException
			// * When whenExhaustedAction is WHEN_EXHAUSTED_GROW, 
			//   borrowObject() will create a new object and return it(essentially making maxActive meaningless.)
			//* When whenExhaustedAction is WHEN_EXHAUSTED_BLOCK, borrowObject() will block 
			//  (invoke Object.wait(long) until a new or idle object is available. 
			//  If a positive maxWait value is supplied, the borrowObject() will block for at most that many milliseconds, 
			//  after which a NoSuchElementException will be thrown. 
			//  If maxWait is non-positive, the borrowObject() method will block indefinitely.
			/*if(configuration.getWhenExhausted().getAction() == null){
			ds.se
			configPool.whenExhaustedAction = CostantiPdD.EXAUSTED_BEHAVIOUR_BLOCK; 
		 }else if(CostantiRisorseSistema.WHEN_EXHAUSTED_BLOCK.equalsIgnoreCase(configuration.getWhenExhausted().getAction()) ){
			 configPool.whenExhaustedAction =CostantiPdD.EXAUSTED_BEHAVIOUR_BLOCK; 
		 }else if(CostantiRisorseSistema.WHEN_EXHAUSTED_GROW.equalsIgnoreCase(configuration.getWhenExhausted().getAction()) ){
			 configPool.whenExhaustedAction =CostantiPdD.EXAUSTED_BEHAVIOUR_GROW; 
		 }else if(CostantiRisorseSistema.WHEN_EXHAUSTED_FAIL.equalsIgnoreCase(configuration.getWhenExhausted().getAction()) ){
			 configPool.whenExhaustedAction =CostantiPdD.EXAUSTED_BEHAVIOUR_FAIL; 
		 }else{
			 configPool.whenExhaustedAction =CostantiPdD.EXAUSTED_BEHAVIOUR_BLOCK; 
		 }
		// default: 1
		// public static final byte 	WHEN_EXHAUSTED_BLOCK 	1
		// public static final byte 	WHEN_EXHAUSTED_FAIL 	0
		// public static final byte 	WHEN_EXHAUSTED_GROW 	2
			 */
			if(configuration.getWhen_exhausted_blockingTimeout()>0)
				ds.setDefaultMaxWaitMillis(configuration.getWhen_exhausted_blockingTimeout()); //Default: -1L
			
			// When testOnBorrow is set, the pool will attempt to validate each object before it is returned from the borrowObject() method. 
			// (Using the provided factory's PoolableObjectFactory.validateObject(java.lang.Object) method.) 
			// Objects that fail to validate will be dropped from the pool, and a different object will be borrowed.
			ds.setDefaultTestOnBorrow(configuration.isValidation_testOnGet()); //Default: false

			// When testOnReturn is set, the pool will attempt to validate each object before it is returned to the pool 
			// in the returnObject(java.lang.Object) method. 
			// (Using the provided factory's PoolableObjectFactory.validateObject(java.lang.Object)  method.) 
			// Objects that fail to validate will be dropped from the pool.
			ds.setDefaultTestOnReturn(configuration.isValidation_testOnRelease()); //Default: false
			
			
			// Optionally, one may configure the pool to examine and possibly evict objects as they sit idle in the pool. 
			// This is performed by an "idle object eviction" thread, which runs asychronously. 
			// The idle object eviction thread may be configured using the following attributes:
			if( configuration.isIdle_abilitato() ){
				// - testWhileIdle, indicates whether or not idle objects should be validated using the factory's 
				// PoolableObjectFactory.validateObject(java.lang.Object) method. Objects that fail to validate will be dropped from the pool.
				ds.setDefaultTestWhileIdle(configuration.isIdle_validateObject()); //Default: false
			}

			/*
			  initialize the pool 
				 */
				try{
					java.sql.Connection [] c = new java.sql.Connection[configuration.getPool_initial()];
					for (int ix = 0 ; ix < configuration.getPool_initial() ; ++ix ) {
						c[ix] = ds.getConnection();
					}
					for (int ix = 0 ; ix < configuration.getPool_initial() ; ++ix ) {
						c[ix].close();
					}
				}catch(Exception e){
					throw new OpenSPCoopFactoryException("Riscontrato errore durante l'inizializzazione del Pool["
							+configuration.getJndiName()+"]: "+ e.getMessage(),e);
				}
			
			return ds;
		}catch(OpenSPCoopFactoryException e){
			throw e;
		}	catch(Exception e){
			String msgError = "Riscontrato errore durante la creazione del datasource ["+configuration.getJndiName()+"]: "+e.getMessage();
			throw new OpenSPCoopFactoryException(msgError,e);
		}

	}
	
	/**
     * Ritorna il nome jndi di un ConnectionPoolDataSource associato ad un datasource
     *
     * 
     */
	public static String buildConnectionPoolDataSourceJNDIName(String jndiDataSourceName){
		if(jndiDataSourceName==null)
			return null;
		else
			return jndiDataSourceName + DBPool.EXTENSION_CONNECTION_POOL_DATASOURCE;
	}
	
	/**
     * Ritorna lo stato del Pool al db
     *
     * 
     */
    public static String getStatoConnessioni(SharedPoolDataSource ds,String jndiName) {	
	return "Pool ["+jndiName+"] attivo con "
	    +ds.getNumIdle() + " connessioni esistenti nel pool e "+
	    +ds.getNumActive()+" connessioni fornite.";
    }
}

