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



package org.openspcoop2.pools.pdd;

import java.util.Vector;


import org.slf4j.Logger;
import org.openspcoop2.pools.core.commons.Costanti;
import org.openspcoop2.pools.core.driver.DriverRisorseSistemaException;
import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.openspcoop2.pools.core.Datasource;
import org.openspcoop2.pools.pdd.db.DBInfo;
import org.openspcoop2.pools.pdd.db.DBPool;
import org.openspcoop2.pools.pdd.jms.JMSInfo;
import org.openspcoop2.pools.pdd.jms.JMSInfoContextProperty;
import org.openspcoop2.utils.resources.GestoreJNDI;





/**
 * Classe utilizzata per ottenere informazioni che interessano ad OpenSPCoop
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreRisorseSistema {

	/* istanza di RegistroServiziReader */
	private static  GestoreRisorseSistema gestoreRisorseSistema;
	/* informazione sull'inizializzazione dell'istanza */
	private static boolean initialize = false;

	/* RisorseSistema */
	private RisorseSistema risorseSistema;

	/** Variabile che contiene i DataSource costruiti da OpenSPCoop */
	private Vector<String> jndi_ds = new Vector<String>();
	private Vector<SharedPoolDataSource> dsList = new Vector<SharedPoolDataSource>(); 

	/** Variabile che contiene i ConnectionSessionJMSPool costruiti da OpenSPCoop */
	private Vector<String> jndi_qm = new Vector<String>();
	private Vector<javax.jms.ConnectionFactory> qmList = new Vector<javax.jms.ConnectionFactory>(); 

	/** Logger utilizzato per debug. */
	private Logger logCore = null;
	private Logger logConsole = null;






	/*   -------------- Metodi di inizializzazione -----------------  */

	/**
	 * Si occupa di inizializzare l'engine 
	 * L'engine inizializzato sara' diverso a seconda del <var>tipo</var> :
	 * <ul>
	 * <li> {@link org.openspcoop2.pools.core.driver.DriverRisorseSistemaXML}, interroga una configurazione realizzata tramite un file xml.
	 * </ul>
	 *
	 * @param tipo Tipo di configurazione.
	 * @param location Location della configurazione
	 * @return true se l'inizializzazione ha successo, false altrimenti.
	 */
	public static boolean initialize(String tipo,String location){

		try {
			GestoreRisorseSistema.gestoreRisorseSistema = new GestoreRisorseSistema(tipo,location);	
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

	/**
	 * Ritorna lo stato dell'engine 
	 *
	 * @return true se l'inizializzazione all'engine e' stata precedentemente effettuata, false altrimenti.
	 */
	public static boolean isInitialize(){
		return GestoreRisorseSistema.initialize;
	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza del Gestore
	 */
	public static GestoreRisorseSistema getInstance(){
		return GestoreRisorseSistema.gestoreRisorseSistema;
	}












	/*   -------------- Costruttore -----------------  */ 

	/**
	 * Inizializza il gestore
	 *
	 * @param tipo Tipo di configurazione.
	 * @param location Location della configurazione
	 */
	public GestoreRisorseSistema(String tipo,String location)throws DriverRisorseSistemaException{
		try{
			this.logCore = OpenSPCoop2PoolsStartup.getLogger();
			this.logConsole = OpenSPCoop2PoolsStartup.getLoggerConsole();
			this.risorseSistema = new RisorseSistema(tipo,location,this.logCore);
			GestoreRisorseSistema.initialize = true;
		}catch(Exception e){
			GestoreRisorseSistema.initialize = false;
		}
	}



	private void logInfo(String msg){
		if(this.logCore!=null)
			this.logCore.info(msg);
		if(this.logConsole!=null)
			this.logConsole.info(msg);
	}

	private void logError(String msg){
		if(this.logCore!=null)
			this.logCore.error(msg);
		if(this.logConsole!=null)
			this.logConsole.error(msg);
	}
	
	private void logError(String msg, Exception e){
		if(this.logCore!=null)
			this.logCore.error(msg,e);
		if(this.logConsole!=null)
			this.logConsole.error(msg,e);
	}




	/* ------------- Inizializzo Risorse --------------- */

	public void createRisorseSistema()throws DriverRisorseSistemaException{

		// Inizializzazione Engine JNDI
		GestoreJNDI jndi = null;
		try{
			jndi = new GestoreJNDI(this.risorseSistema.getJNDIContext());
		}catch(Exception e){
			throw new DriverRisorseSistemaException("[GestoreRisorseSistema] Riscontrato errore durante l'inizializzazione: "
					+e.getMessage());
		}

		if(jndi!=null){

			// Dimensione vector di risorse
			int sizeDataSource = 0;
			int sizeConnectionFactory = 0;
			try{
				sizeDataSource = this.risorseSistema.dataSourceListSize();
				sizeConnectionFactory = this.risorseSistema.connectionFactoryListSize();
			}catch(Exception e){
				throw new DriverRisorseSistemaException("[GestoreRisorseSistema] Riscontrato errore durante la lettura del numero di risorse di sistema: "+e.toString());
			}

			//	Allocazione DataSource
			for(int i=0; i<sizeDataSource;i++){
				// GetConfigurazione
				Datasource dataSourceConfigurazione = null;
				try{
					dataSourceConfigurazione = this.risorseSistema.getDataSource(i);
				}catch(Exception e){
					logError("[GestoreRisorseSistema] Riscontrato errore durante la lettura della configurazione per un datasource: "+e.toString(),e);
					continue;
				}
				// CreazioneDBInfo
				DBInfo dbInfo = new DBInfo();
				dbInfo.setJndiName(dataSourceConfigurazione.getJndiName());
				dbInfo.setConnectionUrl(dataSourceConfigurazione.getConnectionUrl());
				dbInfo.setDriverClass(dataSourceConfigurazione.getDriverClass());
				dbInfo.setUsername(dataSourceConfigurazione.getUsername());
				dbInfo.setPassword(dataSourceConfigurazione.getPassword());
				dbInfo.setPreparedStatementPool(dataSourceConfigurazione.getPreparedStatementPool());
				dbInfo.setAutoCommit(dataSourceConfigurazione.getAutoCommit());
				dbInfo.setReadOnly(dataSourceConfigurazione.getReadOnly());
				dbInfo.setTransactionIsolation(dataSourceConfigurazione.getTransactionIsolation());
				if(dataSourceConfigurazione.getPoolSize()!=null){
					if(dataSourceConfigurazione.getPoolSize().getInitial()!=null){
						dbInfo.setPool_initial(dataSourceConfigurazione.getPoolSize().getInitial().intValue());
					}
					if(dataSourceConfigurazione.getPoolSize().getMax()!=null){
						dbInfo.setPool_max(dataSourceConfigurazione.getPoolSize().getMax().intValue());
					}
					if(dataSourceConfigurazione.getPoolSize().getMin()!=null){
						dbInfo.setPool_min(dataSourceConfigurazione.getPoolSize().getMin().intValue());
					}
				}
				if(dataSourceConfigurazione.getValidation()!=null){
					dbInfo.setValidation_abilitato(true);
					if(dataSourceConfigurazione.getValidation().getOperation()!=null){
						dbInfo.setValidation_operation(dataSourceConfigurazione.getValidation().getOperation());
					}
					dbInfo.setValidation_testOnGet(dataSourceConfigurazione.getValidation().getTestOnGet());
					dbInfo.setValidation_testOnRelease(dataSourceConfigurazione.getValidation().getTestOnRelease());
				}
				if(dataSourceConfigurazione.getWhenExhausted()!=null){
					if(dataSourceConfigurazione.getWhenExhausted().getAction()!=null){
						dbInfo.setWhen_exhausted_action(dataSourceConfigurazione.getWhenExhausted().getAction());
					}
					if(dataSourceConfigurazione.getWhenExhausted().getBlockingTimeout()!=null){
						try{
							int value = 0;
							value = Integer.parseInt(dataSourceConfigurazione.getWhenExhausted().getBlockingTimeout());
							dbInfo.setWhen_exhausted_blockingTimeout(value);
						}catch(Exception e){}
					}
				}
				if(dataSourceConfigurazione.getIdleObjectEviction()!=null){
					dbInfo.setIdle_abilitato(true);
					if(dataSourceConfigurazione.getIdleObjectEviction().getTimeBetweenEvictionRuns()!=null){
						try{
							int value = 0;
							value = Integer.parseInt(dataSourceConfigurazione.getIdleObjectEviction().getTimeBetweenEvictionRuns());
							dbInfo.setIdle_timeBetweenEvictionRuns(value);
						}catch(Exception e){}
					}
					if(dataSourceConfigurazione.getIdleObjectEviction().getNumTestsPerEvictionRun()!=null){
						dbInfo.setIdle_numTestsPerEvictionRun(dataSourceConfigurazione.getIdleObjectEviction().getNumTestsPerEvictionRun().intValue());
					}
					if(dataSourceConfigurazione.getIdleObjectEviction().getIdleObjectTimeout()!=null){
						try{
							int value = 0;
							value = Integer.parseInt(dataSourceConfigurazione.getIdleObjectEviction().getIdleObjectTimeout());
							dbInfo.setIdle_idleObjectTimeout(value);
						}catch(Exception e){}
					}
					dbInfo.setIdle_validateObject(dataSourceConfigurazione.getIdleObjectEviction().getValidateObject());
				}
				
				// Creazione CommonsPoolingDataSource
				DriverAdapterCPDS cpds = null;
				String jndiNameConnectionPoolDataSource = DBPool.buildConnectionPoolDataSourceJNDIName(dataSourceConfigurazione.getJndiName());
				try{
					cpds = DBPool.buildConnectionPoolDataSource(dbInfo);
					logInfo("[GestoreRisorseSistema] Inizializzazione del connectionPoolDataSource["+jndiNameConnectionPoolDataSource+"] effettuata con successo");

				}catch(Exception e){
					logError("[GestoreRisorseSistema] Riscontrato errore durante l'inizializzazione del connectionPoolDataSource["+jndiNameConnectionPoolDataSource
							+"]: "+e.getMessage(),e);
					continue;
				}
				//	Bind JNDI ConnectionPoolingDataSource
				if(cpds!=null){
					try{
						if( jndi.bind(jndiNameConnectionPoolDataSource,cpds,this.logCore,this.logConsole) == false ){
							logError("[GestoreRisorseSistema] Riscontrato errore durante la registrazione JNDI del connectionPoolDataSource["
									+jndiNameConnectionPoolDataSource+"]");
						}
					}catch(Exception e){
						logError("[GestoreRisorseSistema] Riscontrato errore durante la registrazione JNDI del connectionPoolDataSource["
								+jndiNameConnectionPoolDataSource+"]: "+e.getMessage(),e);
						continue;
					}
				}
				// Creazione DataSource
				SharedPoolDataSource ds = null;
				try{
					ds = DBPool.buildDataSource(cpds,dbInfo);
					logInfo("[GestoreRisorseSistema] Inizializzazione del pool["+dataSourceConfigurazione.getJndiName()+"] effettuata con successo:\n"
							+DBPool.getStatoConnessioni(ds,dataSourceConfigurazione.getJndiName()));
				}catch(Exception e){
					logError("[GestoreRisorseSistema] Riscontrato errore durante l'inizializzazione del pool["+dataSourceConfigurazione.getJndiName()
							+"]: "+e.getMessage(),e);
					continue;
				}
				// Bind JNDI
				if(ds!=null){
					try{
						if( jndi.bind(dataSourceConfigurazione.getJndiName(),ds,this.logCore,this.logConsole) == false ){
							logError("[GestoreRisorseSistema] Riscontrato errore durante la registrazione JNDI del DataSource ["
									+dataSourceConfigurazione.getJndiName()+"]");
						}
					}catch(Exception e){
						logError("[GestoreRisorseSistema] Riscontrato errore durante la registrazione JNDI del DataSource ["
								+dataSourceConfigurazione.getJndiName()+"]: "+e.getMessage(),e);
						continue;
					}
				}
				// Aggiungo come risorsa registrata
				if(ds!=null){
					this.jndi_ds.add(dataSourceConfigurazione.getJndiName());
					this.dsList.add(ds);
				}
			}



			// Allocazione ConnectionFactory
			for(int i=0; i<sizeConnectionFactory;i++){
				// GetConfigurazione
				org.openspcoop2.pools.core.ConnectionFactory qfConf = null;
				try{
					qfConf = this.risorseSistema.getConnectionFactory(i);
				}catch(Exception e){
					logError("[GestoreRisorseSistema] Riscontrato errore durante la lettura della configurazione per una connection factory: "+e.toString(),e);
					continue;
				}
				
				// readConfigurazione
				boolean transactedSession = !qfConf.getAutoCommit();
				String poolAck = "";
				if(qfConf.getAutoCommit()){
					poolAck =  " acknowledge mode:";
					if(Costanti.ACKNOWLEDGMENT_AUTO.equals(qfConf.getAcknowledgmentType()))
						poolAck = poolAck +"AUTO_ACKNOWLEDGE";
					else if(Costanti.ACKNOWLEDGMENT_CLIENT.equals(qfConf.getAcknowledgmentType()))
						poolAck = poolAck +"CLIENT_ACKNOWLEDGE";
					else if(Costanti.ACKNOWLEDGMENT_DUPS_OK.equals(qfConf.getAcknowledgmentType()))
						poolAck = poolAck+"DUPS_OK_ACKNOWLEDGE";
					else
						poolAck = poolAck + "Valore non definito nello standard ("+qfConf.getAutoCommit()+")";
				}
				
				// CreazioneJMSInfo
				JMSInfo jmsInfo = new JMSInfo();
				jmsInfo.setJndiName(qfConf.getJndiName());
				jmsInfo.setConnectionFactory(qfConf.getConnectionFactory());
				jmsInfo.setUsername(qfConf.getUsername());
				jmsInfo.setPassword(qfConf.getPassword());
				jmsInfo.setClientId(qfConf.getClientId());
				jmsInfo.setAutoCommit(qfConf.getAutoCommit());
				jmsInfo.setAcknowledgmentType(qfConf.getAcknowledgmentType());
				jmsInfo.setSingleConnectionWithSessionPool(qfConf.getSingleConnectionWithSessionPool());
				for(int K=0; K<qfConf.sizeContextPropertyList(); K++){
					JMSInfoContextProperty cp = new JMSInfoContextProperty();
					cp.setName(qfConf.getContextProperty(K).getName());
					cp.setValue(qfConf.getContextProperty(K).getValue());
					jmsInfo.addContextProperty(cp);
				}
				if(qfConf.getPoolSize()!=null){
					if(qfConf.getPoolSize().getInitial()!=null){
						jmsInfo.setPool_initial(qfConf.getPoolSize().getInitial().intValue());
					}
					if(qfConf.getPoolSize().getMax()!=null){
						jmsInfo.setPool_max(qfConf.getPoolSize().getMax().intValue());
					}
					if(qfConf.getPoolSize().getMin()!=null){
						jmsInfo.setPool_min(qfConf.getPoolSize().getMin().intValue());
					}
				}
				if(qfConf.getValidation()!=null){
					jmsInfo.setValidation_abilitato(true);
					if(qfConf.getValidation().getOperation()!=null){
						jmsInfo.setValidation_operation(qfConf.getValidation().getOperation());
					}
					jmsInfo.setValidation_testOnGet(qfConf.getValidation().getTestOnGet());
					jmsInfo.setValidation_testOnRelease(qfConf.getValidation().getTestOnRelease());
				}
				if(qfConf.getWhenExhausted()!=null){
					if(qfConf.getWhenExhausted().getAction()!=null){
						jmsInfo.setWhen_exhausted_action(qfConf.getWhenExhausted().getAction());
					}
					if(qfConf.getWhenExhausted().getBlockingTimeout()!=null){
						try{
							long value = 0;
							value = Long.parseLong(qfConf.getWhenExhausted().getBlockingTimeout());
							jmsInfo.setWhen_exhausted_blockingTimeout(value);
						}catch(Exception e){}
					}
				}
				if(qfConf.getIdleObjectEviction()!=null){
					jmsInfo.setIdle_abilitato(true);
					if(qfConf.getIdleObjectEviction().getTimeBetweenEvictionRuns()!=null){
						try{
							long value = 0;
							value = Long.parseLong(qfConf.getIdleObjectEviction().getTimeBetweenEvictionRuns());
							jmsInfo.setIdle_timeBetweenEvictionRuns(value);
						}catch(Exception e){}
					}
					if(qfConf.getIdleObjectEviction().getNumTestsPerEvictionRun()!=null){
						jmsInfo.setIdle_numTestsPerEvictionRun(qfConf.getIdleObjectEviction().getNumTestsPerEvictionRun().intValue());
					}
					if(qfConf.getIdleObjectEviction().getIdleObjectTimeout()!=null){
						try{
							long value = 0;
							value = Long.parseLong(qfConf.getIdleObjectEviction().getIdleObjectTimeout());
							jmsInfo.setIdle_idleObjectTimeout(value);
						}catch(Exception e){}
					}
					jmsInfo.setIdle_validateObject(qfConf.getIdleObjectEviction().getValidateObject());
				}
				
				// Creazione ConnectionFactory
				javax.jms.ConnectionFactory qm = null;
				if( Costanti.ABILITATO.equals(qfConf.getSingleConnectionWithSessionPool()) ){
					// sessione
					try{
						qm = new org.openspcoop2.pools.pdd.jms.session.ConnectionFactory(jmsInfo);
						logInfo("[GestoreRisorseSistema] Inizializzazione del session pool["+qfConf.getJndiName()+"] (transacted:"+transactedSession+poolAck+") effettuata con successo:\n"
								+((org.openspcoop2.pools.pdd.jms.session.ConnectionFactory)qm).getStatoConnessioni());
					}catch(Exception e){
						logError("[GestoreRisorseSistema] Riscontrato errore durante l'inizializzazione del pool["+qfConf.getJndiName()
								+"]: "+e.getMessage(),e);
						continue;
					}
				}else{
					// connectionsessione
					try{
						qm = new org.openspcoop2.pools.pdd.jms.connectionsession.ConnectionFactory(jmsInfo);
						logInfo("[GestoreRisorseSistema] Inizializzazione del connection-session pool["+qfConf.getJndiName()+"] (transacted:"+transactedSession+poolAck+") effettuata con successo:\n"
								+((org.openspcoop2.pools.pdd.jms.connectionsession.ConnectionFactory)qm).getStatoConnessioni());
					}catch(Exception e){
						logError("[GestoreRisorseSistema] Riscontrato errore durante l'inizializzazione del pool["+qfConf.getJndiName()
								+"]: "+e.getMessage(),e);
						continue;
					}
				}
				// Bind JNDI
				if(qm!=null){
					try{
						if( jndi.bind(qfConf.getJndiName(),qm,this.logCore,this.logConsole) == false ){
							logError("[GestoreRisorseSistema] Riscontrato errore durante la registrazione JNDI del DataSource ["
									+qfConf.getJndiName()+"]");
						}
					}catch(Exception e){
						logError("[GestoreRisorseSistema] Riscontrato errore durante la registrazione JNDI del DataSource ["
								+qfConf.getJndiName()+"]: "+e.getMessage(),e);
						continue;
					}
				}
				// Aggiungo come risorsa registrata
				if(qm!=null){
					this.jndi_qm.add(qfConf.getJndiName());
					this.qmList.add(qm);
				}
			}
		}
	}


	/**
	 * Unbind risorse di OpenSPCoop dall'albero JNDI
	 *
	 * 
	 */
	public void deleteRisorseSistema() {
		try {

			GestoreJNDI jndi = new GestoreJNDI();

			// DataSource
			if(this.dsList!=null && this.jndi_ds!=null){
				while(this.dsList.size()>0){
					SharedPoolDataSource dsUnbind = this.dsList.remove(0);
					String jndidsUnbind = this.jndi_ds.remove(0);
					try{
						dsUnbind.close();
						jndi.unbind(jndidsUnbind,this.logCore,this.logConsole);
						logInfo("Rilasciata (unbind) DataSource ["+jndidsUnbind+"]");
					}catch(Exception e){
						logError("Operazione di UNBIND del DataSource ["+jndidsUnbind+"] non riuscita: "+e.getMessage(),e);
					}
					String jndiNameConnectionPoolDataSource = DBPool.buildConnectionPoolDataSourceJNDIName(jndidsUnbind);
					try{
						jndi.unbind(jndiNameConnectionPoolDataSource,this.logCore,this.logConsole);
						logInfo("Rilasciata (unbind) ConnectionPoolDataSource ["+jndiNameConnectionPoolDataSource+".CommonsPoolDataSource"+"]");
					}catch(Exception e){
						logError("Operazione di UNBIND del ConnectionPoolDataSource ["+jndiNameConnectionPoolDataSource+"] non riuscita: "+e.getMessage(),e);
					}
				}
			}

			// ConnectionFactory
			if(this.qmList!=null && this.jndi_qm!=null){
				while(this.qmList.size()>0){
					javax.jms.ConnectionFactory qmUnbind = this.qmList.remove(0);
					String jndiqmUnbind = this.jndi_qm.remove(0);
					try{
						if(qmUnbind instanceof org.openspcoop2.pools.pdd.jms.connectionsession.ConnectionFactory){
							((org.openspcoop2.pools.pdd.jms.connectionsession.ConnectionFactory)qmUnbind).close();
						}else if(qmUnbind instanceof org.openspcoop2.pools.pdd.jms.session.ConnectionFactory){
							((org.openspcoop2.pools.pdd.jms.session.ConnectionFactory)qmUnbind).close();
						}else{
							throw new Exception("Tipo di connectionFactory("+qmUnbind.getClass().getName()+") non conosciuto");
						}

						jndi.unbind(jndiqmUnbind,this.logCore,this.logConsole);
						logInfo("Rilasciata (unbind) ConnectionFactory ["+jndiqmUnbind+"]");
					}catch(Exception e){
						logError("Operazione di UNBIND della ConnectionFactory ["+jndiqmUnbind+"] non riuscita: "+e.getMessage(),e);
					}
				}
			}

		}catch(Exception e){
			logError("Operazione di UNBIND dei pool per le risorse di sistema non riuscita: "+e.getMessage(),e);
		}
	}
	
	
	
	public static String getStatoRisorseDBPool(String jndiName)throws Exception{
		GestoreRisorseSistema gestore = GestoreRisorseSistema.getInstance();
		if ( gestore.jndi_ds.contains(jndiName)==false ){
			throw new Exception("Risorsa jndi non gestita dal Gestore Connessioni (Pool) OpenSPCoop2");
		}else{
			int indice = gestore.jndi_ds.indexOf(jndiName);
			return DBPool.getStatoConnessioni(gestore.dsList.get(indice),jndiName);
		}
	}
	
	public static String getStatoRisorseJMSPool(String jndiName)throws Exception{
		GestoreRisorseSistema gestore = GestoreRisorseSistema.getInstance();
		if ( gestore.jndi_qm.contains(jndiName)==false ){
			throw new Exception("Risorsa jndi non gestita dal Gestore Connessioni (Pool) OpenSPCoop2");
		}else{
			int indice = gestore.jndi_qm.indexOf(jndiName);
			Object qm = gestore.qmList.get(indice);
			if(qm instanceof org.openspcoop2.pools.pdd.jms.connectionsession.ConnectionFactory){
				return ((org.openspcoop2.pools.pdd.jms.connectionsession.ConnectionFactory)qm).getStatoConnessioni();
			}else if(qm instanceof org.openspcoop2.pools.pdd.jms.session.ConnectionFactory){
				return ((org.openspcoop2.pools.pdd.jms.session.ConnectionFactory)qm).getStatoConnessioni();
			}else{
				throw new Exception("Tipo di connectionFactory("+qm.getClass().getName()+") non conosciuto");
			}
		}
	}
	
	
	protected static String[] getJNDINameDBPool(){
		GestoreRisorseSistema gestore = GestoreRisorseSistema.getInstance();
		if(gestore.jndi_ds!=null && gestore.jndi_ds.size()>0){
			String[]ret = new String[1];
			return gestore.jndi_ds.toArray(ret);
		}else
			return null;
	}
	protected static String[] getJNDINameJMSPool(){
		GestoreRisorseSistema gestore = GestoreRisorseSistema.getInstance();
		if(gestore.jndi_qm!=null && gestore.jndi_qm.size()>0){
			String[]ret = new String[1];
			return gestore.jndi_qm.toArray(ret);
		}else
			return null;
	}
}
