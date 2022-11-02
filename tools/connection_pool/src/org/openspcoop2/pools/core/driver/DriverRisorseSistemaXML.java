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


package org.openspcoop2.pools.core.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.serializer.AbstractDeserializer;
import org.openspcoop2.pools.core.ConnectionFactory;
import org.openspcoop2.pools.core.Datasource;
import org.openspcoop2.pools.core.IdleObjectEviction;
import org.openspcoop2.pools.core.Jndi;
import org.openspcoop2.pools.core.PoolSize;
import org.openspcoop2.pools.core.Validation;
import org.openspcoop2.pools.core.WhenExhausted;
import org.openspcoop2.pools.core.commons.Costanti;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;


/**
 * Implementazione della classe {@link IDriverRisorseSistemaGet}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRisorseSistemaXML implements IDriverRisorseSistemaGet {


	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Indicazione di una corretta creazione */
	public boolean create = false;

	/** Contesto di Unmarshall. */
	private AbstractDeserializer deserializer;
	private Class<org.openspcoop2.pools.core.Openspcoop2> cXmlRoot;
	/** Path dove si trova il file xml che realizza la configurazione delle risorse esterne. */
	private String openspcoop2_pools_path;
	/** 'Root' della configurazione delle risorse esterne. */
	private org.openspcoop2.pools.core.Openspcoop2 openspcoop2Pools;



	/** Variabile che mantiene uno stato di ultima modifica della configurazione delle risorse esterne xml
        od una variabile temporale che servira' per fare il refresh della configurazione 
	una volta scaduto un timeout, nel caso il file sia posto in remoto (HTTP) .*/
	private long lastModified = 0;
	/** Logger utilizzato per info. */
	private Logger log = null;
	/** Indica ogni quanto deve essere effettuato il refresh della configurazione delle risorse esterne, 
	nel caso sia posto in remoto (secondi)*/
	private static final int timeoutRefresh = 30;




	/** ************* parsing XML ************** */
	private void parsingXMLOpenSPCoop2PoolsConfig() throws DriverRisorseSistemaException{

		/* ---- InputStream ---- */
		InputStream iStream = null;
		HttpURLConnection httpConn = null;
		if(this.openspcoop2_pools_path.startsWith("http://") || this.openspcoop2_pools_path.startsWith("file://")){
			try{ 
				URL url = new URL(this.openspcoop2_pools_path);
				URLConnection connection = url.openConnection();
				httpConn = (HttpURLConnection) connection;
				httpConn.setRequestMethod("GET");
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				iStream = httpConn.getInputStream();
			}catch(Exception e) {
				try{  
					if(iStream!=null)
						iStream.close();
					if(httpConn !=null)
						httpConn.disconnect();
				} catch(Exception ef) {
					// close
				}
				throw new DriverRisorseSistemaException("Riscontrato errore durante la creazione dell'inputStream della configurazione delle risorse esterne (HTTP) : \n\n"+e.getMessage());
			}
			this.lastModified = System.currentTimeMillis();
		}else{
			try{  
				iStream = new FileInputStream(this.openspcoop2_pools_path);
			}catch(java.io.FileNotFoundException e) {
				throw new DriverRisorseSistemaException("Riscontrato errore durante la creazione dell'inputStream della configurazione delle risorse esterne (FILE) : \n\n"+e.getMessage());
			}
			try{
				this.lastModified = (new File(this.openspcoop2_pools_path)).lastModified();
			}catch(Exception e){
				try{  
					if(iStream!=null)
						iStream.close();
				} catch(java.io.IOException ef) {}
				throw new DriverRisorseSistemaException("Riscontrato errore durante la lettura del file dove e' allocato la configurazione delle risorse esterne: "+e.getMessage());
			}
		}



		/* ---- Unmarshall del file di configurazione ---- */
		try{  
			this.openspcoop2Pools = (org.openspcoop2.pools.core.Openspcoop2) this.deserializer.xmlToObj(iStream, this.cXmlRoot);
		} catch(Exception e) {
			try{  
				if(iStream!=null)
					iStream.close();
			} catch(Exception ef) {}
			try{ 
				if(httpConn !=null)
					httpConn.disconnect();
			} catch(Exception ef) {
				// close
			}
			throw new DriverRisorseSistemaException("Riscontrato errore durante l'unmarshall del file di configurazione: "+e.getMessage());
		}

		try{  
			// Chiusura dello Stream
			if(iStream!=null)
				iStream.close();
		} catch(Exception e) {
			throw new DriverRisorseSistemaException("Riscontrato errore durante la chiusura dell'Input Stream: "+e.getMessage());
		}
		try{
			// Chiusura dell'eventuale connessione HTTP
			if(httpConn !=null)
				httpConn.disconnect();
		} catch(Exception e) {
			throw new DriverRisorseSistemaException("Riscontrato errore durante la chiusura dell'Input Stream (http): "+e.getMessage());
		}
	}




	/* ********  COSTRUTTORI e METODI DI RELOAD  ******** */

	/**
	 * Costruttore.  Viene effettuato l'unmarshall del file di configurazione.
	 *
	 * @param path path dove si trova il file xml che realizza la configurazione delle risorse esterne.
	 * 
	 */
	public DriverRisorseSistemaXML(String path,Logger alog){
		
		if(alog!=null)
			this.log = alog;
		else
			this.log =  LoggerWrapperFactory.getLogger("Configuratore Risorse Esterne (poolConfig) XML");

		if(path == null){
			this.log.error("DriverRisorseSistema: Riscontrato errore durante la creazione: url/path is null");
			this.create=false;
			return;
		}
		this.openspcoop2_pools_path = path;


		/* ---- Inizializzazione del contesto di unmarshall ---- */
		this.cXmlRoot = org.openspcoop2.pools.core.Openspcoop2.class;
		this.deserializer = new  org.openspcoop2.generic_project.serializer.JaxbDeserializer();

		try{
			this.parsingXMLOpenSPCoop2PoolsConfig();
		}catch(DriverRisorseSistemaException e){
			this.log.error("DriverRisorseSistema: "+e.getMessage());
			this.create=false;
			return;
		}
		this.create = true;
	}


	/**
	 * Metodo che si occupa di effettuare il refresh dell'unmarshall del file di configurazione
	 * se il file di configurazione e' stato modificato dall'ultimo unmarshall.
	 *
	 * 
	 */
	private synchronized void refreshConfigurazioneXML() throws DriverRisorseSistemaException{

		File fTest = null;
		boolean refresh = false;
		if(this.openspcoop2_pools_path.startsWith("http://") || this.openspcoop2_pools_path.startsWith("file://")){
			long now = System.currentTimeMillis();
			if( (now-this.lastModified) > (DriverRisorseSistemaXML.timeoutRefresh*1000) ){
				refresh=true;
			}
		}else{
			fTest = new File(this.openspcoop2_pools_path);
			if(this.lastModified != fTest.lastModified()){
				refresh = true;
			}
		}
		if(refresh){

			try{
				this.parsingXMLOpenSPCoop2PoolsConfig();
			}catch(DriverRisorseSistemaException e){
				this.log.error("DriverRisorseSistema refreshError: "+e.getMessage());
				throw new DriverRisorseSistemaException("DriverRisorseSistema refreshError: "+e.getMessage());
			}
			if(this.openspcoop2_pools_path.startsWith("http://")==false && this.openspcoop2_pools_path.startsWith("file://")==false){
				this.log.warn("Reloaded poolConfig context.");
			}

		}

		if(this.openspcoop2Pools == null){
			this.log.error("DriverRisorseSistema refreshError: istanza poolConfig is null dopo il refresh");
			throw new DriverRisorseSistemaException("DriverRisorseSistema refreshError: istanza poolConfig is null dopo il refresh");
		}

	}







	/* ********  INTERFACCIA IDriverRisorseSistemaGet ******** */
	
	
	
	
	
	/* ******** JNDI TREE ******** */

	/**
	 * Restituisce le proprieta' che identificano un albero JNDI su cui registrare le risorse definite
	 * nel poolConfig.xml
	 *
	 * @return proprieta' di contesto JNDI.
	 * 
	 */
	@Override
	public java.util.Properties getJNDIContext() throws DriverRisorseSistemaException{

		refreshConfigurazioneXML();

		java.util.Properties properties = new java.util.Properties();
		Jndi jndi = this.openspcoop2Pools.getJndi();
		if(jndi.getContextProperty()!=null) {
			for(int i=0; i<jndi.getContextProperty().size();i++){
				properties.put(jndi.getContextProperty().get(i).getName(),
						jndi.getContextProperty().get(i).getValue());
			}
		}

		return properties;
	}








	/* ********  DataSource  ******** */

	/**
	 * Restituisce il numero di DataSource definiti nel file poolConfig.xml 
	 *
	 * @return Il numero di DataSource definiti nel file poolConfig.xml
	 * 
	 */
	@Override
	public int dataSourceListSize() throws DriverRisorseSistemaException{
		
		refreshConfigurazioneXML();

		if(this.openspcoop2Pools.getDatasource()!=null) {
			return this.openspcoop2Pools.getDatasource().size();
		}
		else {
			return 0;
		}
	}

	/**
	 * Restituisce il DataSource con indice <var>index</var> definito nel file poolConfig.xml
	 *  
	 * @param index Indice del DataSource da ritornare
	 * @return la configurazione {@link org.openspcoop2.pools.core.Datasource} di un DataSource con indice <var>index</var>
	 * 
	 */
	@Override
	public Datasource getDataSource(int index) throws DriverRisorseSistemaException{
		
		refreshConfigurazioneXML();
		
		int sizeDataSource = 0;
		if(this.openspcoop2Pools.getDatasource()!=null) {
			sizeDataSource = this.openspcoop2Pools.getDatasource().size();
		}
		
		if(index>=sizeDataSource)
			throw new DriverRisorseSistemaException("[getDataSource] Configurazione richiesta non esistente");

		Datasource dsXML = this.openspcoop2Pools.getDatasource().get(index);	
	
		// Default TransactionIsolation is READ_COMMITED
		if(dsXML.getTransactionIsolation()!=null){
			if(Costanti.TRANSACTION_ISOLATION_NONE.equalsIgnoreCase(dsXML.getTransactionIsolation())==false &&
					Costanti.TRANSACTION_ISOLATION_READ_COMMITTED.equalsIgnoreCase(dsXML.getTransactionIsolation())==false &&
					Costanti.TRANSACTION_ISOLATION_READ_UNCOMMITTED.equalsIgnoreCase(dsXML.getTransactionIsolation())==false &&
					Costanti.TRANSACTION_ISOLATION_REPEATABLE_READ.equalsIgnoreCase(dsXML.getTransactionIsolation())==false &&
					Costanti.TRANSACTION_ISOLATION_SERIALIZABLE.equalsIgnoreCase(dsXML.getTransactionIsolation())==false){
				dsXML.setTransactionIsolation(Costanti.TRANSACTION_ISOLATION_READ_COMMITTED);		
			}	
		}else{
			dsXML.setTransactionIsolation(Costanti.TRANSACTION_ISOLATION_READ_COMMITTED);		
		}
		//	whenExhausted: default is Block
		if(dsXML.getWhenExhausted() != null){
			WhenExhausted when = dsXML.getWhenExhausted();
			// default comportamento is block
			if(when.getAction() == null){
				dsXML.getWhenExhausted().setAction(Costanti.WHEN_EXHAUSTED_BLOCK); 
			}else if(Costanti.WHEN_EXHAUSTED_BLOCK.equalsIgnoreCase(when.getAction())==false && 
					Costanti.WHEN_EXHAUSTED_GROW.equalsIgnoreCase(when.getAction())==false &&
					Costanti.WHEN_EXHAUSTED_FAIL.equalsIgnoreCase(when.getAction())==false ){
				dsXML.getWhenExhausted().setAction(Costanti.WHEN_EXHAUSTED_BLOCK); 
			}
			//	blocking timeout default: 120000
			if(when.getBlockingTimeout()==null)
				dsXML.getWhenExhausted().setBlockingTimeout("120000");
			else{
				try{
					Long.parseLong(when.getBlockingTimeout());
				}catch(Exception e){
					dsXML.getWhenExhausted().setBlockingTimeout("120000");
				}
			}
		}else{
			WhenExhausted when = new WhenExhausted();
			when.setAction(Costanti.WHEN_EXHAUSTED_BLOCK); 
			when.setBlockingTimeout("120000");
			dsXML.setWhenExhausted(when);
		}
		// validation: default tutto a false
		if(dsXML.getValidation()==null){
			Validation valid = new Validation();
			valid.setTestOnGet(false);
			valid.setTestOnRelease(false);
			dsXML.setValidation(valid);
		}
		// idle-object-eviction
		if(dsXML.getIdleObjectEviction() != null){
			IdleObjectEviction idleObj = dsXML.getIdleObjectEviction();
			// time-between-eviction-runs default: 300000
			if(idleObj.getTimeBetweenEvictionRuns()==null){
				dsXML.getIdleObjectEviction().setTimeBetweenEvictionRuns("300000");
			}else{
				try{
					Long.parseLong(idleObj.getTimeBetweenEvictionRuns());
				}catch(Exception e){
					dsXML.getIdleObjectEviction().setTimeBetweenEvictionRuns("300000");
				}
			}
			// idle-object-timeout default: 1800000
			if(idleObj.getIdleObjectTimeout()==null){
				dsXML.getIdleObjectEviction().setIdleObjectTimeout("1800000");
			}else{
				try{
					Long.parseLong(idleObj.getIdleObjectTimeout());
				}catch(Exception e){
					dsXML.getIdleObjectEviction().setIdleObjectTimeout("1800000");
				}
			}
		}
		// Pool Size
		if(dsXML.getPoolSize()==null){
			PoolSize ps = new PoolSize();
			ps.setInitial(new BigInteger("0")); //default 0
			ps.setMax(new BigInteger("8")); // default 8
			ps.setMin(new BigInteger("3")); // default 3
		}else{
			if(dsXML.getPoolSize().getInitial()==null)
				dsXML.getPoolSize().setInitial(new BigInteger("0")); //default 0
			if(dsXML.getPoolSize().getMax()==null)
				dsXML.getPoolSize().setMax(new BigInteger("8")); // default 8
			if(dsXML.getPoolSize().getMin()==null)
				dsXML.getPoolSize().setMin(new BigInteger("3")); // default 3
		}

		return dsXML;
	}

	/**
	 * Restituisce un vector contenente tutti i DataSource definiti nel file poolConfig.xml
	 *  
	 * @return List di {@link org.openspcoop2.pools.core.Datasource}
	 * 
	 */
	@Override
	public List<Datasource> getDataSources() throws DriverRisorseSistemaException{

		refreshConfigurazioneXML();
		
		int sizeDataSource = 0;
		if(this.openspcoop2Pools.getDatasource()!=null) {
			sizeDataSource = this.openspcoop2Pools.getDatasource().size();
		}
		
		List<Datasource> v = new ArrayList<Datasource>();
		for(int i=0; i<sizeDataSource;i++)
			v.add(getDataSource(i));
		return v;
	}







	/* ********  ConnectionFactory  ******** */

	/**
	 * Restituisce il numero di ConnectionFactory definiti nel file poolConfig.xml 
	 *
	 * @return Il numero di ConnectionFactory definiti nel file poolConfig.xml
	 * 
	 */
	@Override
	public int connectionFactoryListSize() throws DriverRisorseSistemaException{
		
		refreshConfigurazioneXML();
		
		if(this.openspcoop2Pools.getConnectionFactory()!=null) {
			return this.openspcoop2Pools.getConnectionFactory().size();
		}
		else {
			return 0;
		}

	}

	/**
	 * Restituisce la ConnectionFactory con indice <var>index</var> definito nel file poolConfig.xml
	 *  
	 * @param index Indice della ConnectionFactory da ritornare
	 * @return la configurazione {@link org.openspcoop2.pools.core.ConnectionFactory} di una ConnectionFactory 
	 *         con indice <var>index</var>
	 * 
	 */
	@Override
	public ConnectionFactory getConnectionFactory(int index) throws DriverRisorseSistemaException{

		refreshConfigurazioneXML();
		
		int sizeCF = 0;
		if(this.openspcoop2Pools.getConnectionFactory()!=null) {
			sizeCF = this.openspcoop2Pools.getConnectionFactory().size();
		}
		
		if(index>=sizeCF)
			throw new DriverRisorseSistemaException("[getConnectionFactory] Configurazione richiesta non esistente");

		ConnectionFactory qfXML = this.openspcoop2Pools.getConnectionFactory().get(index);	
		// Acknowledgmente type: default is AUTO_ACKNOWLEDGE
		if(qfXML.getAcknowledgmentType()==null){
			qfXML.setAcknowledgmentType(Costanti.ACKNOWLEDGMENT_AUTO);
		}else{
			if(Costanti.ACKNOWLEDGMENT_AUTO.equalsIgnoreCase(qfXML.getAcknowledgmentType())==false &&
					Costanti.ACKNOWLEDGMENT_CLIENT.equalsIgnoreCase(qfXML.getAcknowledgmentType())==false &&
					Costanti.ACKNOWLEDGMENT_DUPS_OK.equalsIgnoreCase(qfXML.getAcknowledgmentType())==false){
				qfXML.setAcknowledgmentType(Costanti.ACKNOWLEDGMENT_AUTO);
			}
		}
		// whenExhausted: default is Block
		if(qfXML.getWhenExhausted() != null){
			WhenExhausted when = qfXML.getWhenExhausted();
			//	default comportamento is block
			if(when.getAction() == null){
				qfXML.getWhenExhausted().setAction(Costanti.WHEN_EXHAUSTED_BLOCK); 
			}else if(Costanti.WHEN_EXHAUSTED_BLOCK.equalsIgnoreCase(when.getAction())==false && 
					Costanti.WHEN_EXHAUSTED_GROW.equalsIgnoreCase(when.getAction())==false &&
					Costanti.WHEN_EXHAUSTED_FAIL.equalsIgnoreCase(when.getAction())==false ){
				qfXML.getWhenExhausted().setAction(Costanti.WHEN_EXHAUSTED_BLOCK); 
			}
			// blocking timeout default: 120000
			if(when.getBlockingTimeout()==null)
				qfXML.getWhenExhausted().setBlockingTimeout("120000");
			else{
				try{
					Long.parseLong(when.getBlockingTimeout());
				}catch(Exception e){
					qfXML.getWhenExhausted().setBlockingTimeout("120000");
				}
			}
		}else{
			WhenExhausted when = new WhenExhausted();
			when.setAction(Costanti.WHEN_EXHAUSTED_BLOCK); 
			when.setBlockingTimeout("120000");
			qfXML.setWhenExhausted(when);
		}
		//		 validation:
		// Nota: La non presenza indica una validazione assent
		// idle-object-eviction
		if(qfXML.getIdleObjectEviction() != null){
			IdleObjectEviction idleObj = qfXML.getIdleObjectEviction();
			// time-between-eviction-runs default: 300000
			if(idleObj.getTimeBetweenEvictionRuns()==null){
				qfXML.getIdleObjectEviction().setTimeBetweenEvictionRuns("300000");
			}else{
				try{
					Long.parseLong(idleObj.getTimeBetweenEvictionRuns());
				}catch(Exception e){
					qfXML.getIdleObjectEviction().setTimeBetweenEvictionRuns("300000");
				}
			}
			// idle-object-timeout default: 1800000
			if(idleObj.getIdleObjectTimeout()==null){
				qfXML.getIdleObjectEviction().setIdleObjectTimeout("1800000");
			}else{
				try{
					Long.parseLong(idleObj.getIdleObjectTimeout());
				}catch(Exception e){
					qfXML.getIdleObjectEviction().setIdleObjectTimeout("1800000");
				}
			}
		}
		// Pool Size
		if(qfXML.getPoolSize()==null){
			PoolSize ps = new PoolSize();
			ps.setInitial(new BigInteger("0")); //default 0
			ps.setMax(new BigInteger("8")); // default 8
			ps.setMin(new BigInteger("3")); // default 3
		}else{
			if(qfXML.getPoolSize().getInitial()==null)
				qfXML.getPoolSize().setInitial(new BigInteger("0")); //default 0
			if(qfXML.getPoolSize().getMax()==null)
				qfXML.getPoolSize().setMax(new BigInteger("8")); // default 8
			if(qfXML.getPoolSize().getMin()==null)
				qfXML.getPoolSize().setMin(new BigInteger("3")); // default 3
		}
		
		return qfXML;

	}

	/**
	 * Restituisce un vector contenente tutti le ConnectionFactory definite nel file poolConfig.xml
	 *  
	 * @return List di {@link org.openspcoop2.pools.core.ConnectionFactory}
	 * 
	 */
	@Override
	public List<ConnectionFactory> getConnectionFactories() throws DriverRisorseSistemaException{
		
		refreshConfigurazioneXML();
		
		int sizeCF = 0;
		if(this.openspcoop2Pools.getConnectionFactory()!=null) {
			sizeCF = this.openspcoop2Pools.getConnectionFactory().size();
		}
		
		List<ConnectionFactory> v = new ArrayList<ConnectionFactory>();
		for(int i=0; i<sizeCF;i++)
			v.add(getConnectionFactory(i));
		return v;
	}
}
