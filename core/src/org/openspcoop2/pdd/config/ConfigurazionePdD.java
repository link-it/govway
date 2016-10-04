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



package org.openspcoop2.pdd.config;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;



/**
 * Classe utilizzata per effettuare query a registri di servizio di openspcoop.
 * E' possibile indicare piu' di una sorgente e/o una cache
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConfigurazionePdD  {

    /** Fonti su cui effettuare le query:
     * - CACHE
     * - configurazione
     */
	private Cache cache = null;
	private IDriverConfigurazioneGet driverConfigurazionePdD;
	
	private boolean useConnectionPdD = false;
	private String tipoDatabase = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	
	/** Variabili statiche contenenti le configurazioni della Porta di Dominio
	 *  - ConfigurazioneGenerale
	 *  - GestioneErrore
	 *  - RoutingTable
	 *  - AccessoRegistro
	 */
	private static Configurazione configurazioneGeneralePdD = null;
	private static GestioneErrore gestioneErroreComponenteCooperazione = null;
	private static GestioneErrore gestioneErroreComponenteIntegrazione = null;
	private static RoutingTable routingTable = null;
	private static AccessoRegistro accessoRegistro = null;
	private static AccessoConfigurazione accessoConfigurazione = null;
	private static AccessoDatiAutorizzazione accessoDatiAutorizzazione = null;
	/** ConfigurazioneDinamica */
	private boolean configurazioneDinamica = false;
	
	/** OpenSPCoop Properties */
	private OpenSPCoop2Properties openspcoopProperties = null;
	
	/** ConfigLocalProperties */
	private ConfigLocalProperties configLocalProperties = null;
	
	
	
	/* --------------- Cache --------------------*/
	public boolean isCacheAbilitata(){
		return this.cache!=null;
	}
	public void resetCache() throws DriverConfigurazioneException{
		if(this.cache!=null){
			try{
				this.cache.clear();
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
	}
	public String printStatsCache(String separator) throws DriverConfigurazioneException{
		if(this.cache!=null){
			try{
				return this.cache.printStats(separator);
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}else{
			throw new DriverConfigurazioneException("Cache non abilitata");
		}
	}
	public void abilitaCache() throws DriverConfigurazioneException{
		if(this.cache!=null)
			throw new DriverConfigurazioneException("Cache gia' abilitata");
		else{
			try{
				this.cache = new Cache(CostantiConfigurazione.CACHE_CONFIGURAZIONE_PDD);
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
	}
	public void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws DriverConfigurazioneException{
		if(this.cache!=null)
			throw new DriverConfigurazioneException("Cache gia' abilitata");
		else{
			try{
				org.openspcoop2.core.config.Cache configurazioneCache = new org.openspcoop2.core.config.Cache();
				if(dimensioneCache!=null){
					configurazioneCache.setDimensione(dimensioneCache+"");
				}
				if(algoritmoCacheLRU!=null){
					if(algoritmoCacheLRU)
						configurazioneCache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						configurazioneCache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);
				}
				if(itemIdleTime!=null){
					configurazioneCache.setItemIdleTime(itemIdleTime+"");
				}
				if(itemLifeSecond!=null){
					configurazioneCache.setItemLifeSecond(itemLifeSecond+"");
				}
				initCacheConfigurazione(configurazioneCache, null);
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
	}
	public void disabilitaCache() throws DriverConfigurazioneException{
		if(this.cache==null)
			throw new DriverConfigurazioneException("Cache gia' disabilitata");
		else{
			try{
				this.cache.clear();
				this.cache = null;
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
	}
	public String listKeysCache(String separator) throws DriverConfigurazioneException{
		if(this.cache!=null){
			try{
				return this.cache.printKeys(separator);
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}else{
			throw new DriverConfigurazioneException("Cache non abilitata");
		}
	}
	public String getObjectCache(String key) throws DriverConfigurazioneException{
		if(this.cache!=null){
			try{
				Object o = this.cache.get(key);
				if(o!=null){
					return o.toString();
				}else{
					return "oggetto con chiave ["+key+"] non presente";
				}
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}else{
			throw new DriverConfigurazioneException("Cache non abilitata");
		}
	}
	public void removeObjectCache(String key) throws DriverConfigurazioneException{
		if(this.cache!=null){
			try{
				this.cache.remove(key);
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}else{
			throw new DriverConfigurazioneException("Cache non abilitata");
		}
	}
	
	
	
	/*   -------------- Costruttore -----------------  */ 

	/**
	 * Si occupa di inizializzare l'engine che permette di effettuare
	 * query alla configurazione di OpenSPCoop.
	 * L'engine inizializzato sara' diverso a seconda del <var>tipo</var> di configurazione :
	 * <ul>
	 * <li> {@link DriverConfigurazioneXML}, interroga una configurazione realizzata tramite un file xml.
     * <li> {@link DriverConfigurazioneDB}, interroga una configurazione realizzata tramite un database relazionale.
     * </ul>
	 *
	 * @param accessoConfigurazione Informazioni per accedere alla configurazione della PdD OpenSPCoop.
	 */
	public ConfigurazionePdD(AccessoConfigurazionePdD accessoConfigurazione,Logger alog,Logger alogConsole,Properties localProperties, 
			String jndiNameDatasourcePdD, boolean forceDisableCache, boolean useOp2UtilsDatasource, boolean bindJMX)throws DriverConfigurazioneException{

		try{ 
			// Inizializzo OpenSPCoopProperties
			this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
			this.configurazioneDinamica = this.openspcoopProperties.isConfigurazioneDinamica();
			
			if(alog!=null)
				this.log = alog;
			else
				this.log = LoggerWrapperFactory.getLogger(ConfigurazionePdD.class);
			
			String msg = "Leggo configurazione di tipo["+accessoConfigurazione.getTipo()+"]   location["+accessoConfigurazione.getLocation()+"]";
			this.log.info(msg);
			if(alogConsole!=null)
				alogConsole.info(msg);
			
			// inizializzazione XML
			if(CostantiConfigurazione.CONFIGURAZIONE_XML.equalsIgnoreCase(accessoConfigurazione.getTipo())){
				this.driverConfigurazionePdD = new DriverConfigurazioneXML(accessoConfigurazione.getLocation(),this.log);
				if(this.driverConfigurazionePdD ==null || ((DriverConfigurazioneXML)this.driverConfigurazionePdD).create==false){
					throw new DriverConfigurazioneException("Riscontrato errore durante l'inizializzazione della configurazione di tipo "+
							accessoConfigurazione.getTipo()+" con location: "+accessoConfigurazione.getLocation());
				}
			} 
			
			// inizializzazione DB
			else if(CostantiConfigurazione.CONFIGURAZIONE_DB.equalsIgnoreCase(accessoConfigurazione.getTipo())){			
				this.driverConfigurazionePdD = new DriverConfigurazioneDB(accessoConfigurazione.getLocation(),accessoConfigurazione.getContext(),
						this.log,accessoConfigurazione.getTipoDatabase(),accessoConfigurazione.isCondivisioneDatabasePddRegistro(),
						useOp2UtilsDatasource, bindJMX);
				if(this.driverConfigurazionePdD ==null || ((DriverConfigurazioneDB)this.driverConfigurazionePdD).create==false){
					throw new DriverConfigurazioneException("Riscontrato errore durante l'inizializzazione della configurazione di tipo "+
							accessoConfigurazione.getTipo()+" con location: "+accessoConfigurazione.getLocation());
				}
				this.useConnectionPdD = jndiNameDatasourcePdD.equals(accessoConfigurazione.getLocation());
				this.tipoDatabase = accessoConfigurazione.getTipoDatabase();
			} 
			
			// tipo di configurazione non conosciuto
			else{
				throw new DriverConfigurazioneException("Riscontrato errore durante l'inizializzazione della configurazione di tipo sconosciuto "+
							accessoConfigurazione.getTipo()+" con location: "+accessoConfigurazione.getLocation());
			}


			// Inizializzazione della Cache
			AccessoConfigurazione accessoDatiConfigurazione = null;
			try{
				accessoDatiConfigurazione = this.driverConfigurazionePdD.getAccessoConfigurazione();
			}catch(DriverConfigurazioneNotFound notFound){}
			if(accessoDatiConfigurazione!=null && accessoDatiConfigurazione.getCache()!=null){
				if(forceDisableCache==false){
					initCacheConfigurazione(accessoDatiConfigurazione.getCache(),alogConsole);
				}
			}
			
			// Inizializzazione ConfigLocalProperties
			this.configLocalProperties = new ConfigLocalProperties(this.log, this.openspcoopProperties.getRootDirectory(),localProperties);

		}catch(Exception e){
			String msg = "Riscontrato errore durante l'inizializzazione della configurazione di OpenSPCoop: "+e.getMessage();
			this.log.error(msg,e);
			if(alogConsole!=null)
				alogConsole.info(msg);
			throw new DriverConfigurazioneException("Riscontrato errore durante l'inizializzazione della configurazione di OpenSPCoop: "+e.getMessage());
		}
	}
	
	private void initCacheConfigurazione(org.openspcoop2.core.config.Cache configurazioneCache,Logger alogConsole)throws Exception{
		this.cache = new Cache(CostantiConfigurazione.CACHE_CONFIGURAZIONE_PDD);
		
		String msg = null;
		if( (configurazioneCache.getDimensione()!=null) ||
				(configurazioneCache.getAlgoritmo() != null) ){
	     
			if( configurazioneCache.getDimensione()!=null ){
				int dimensione = -1;
				try{
					dimensione = Integer.parseInt(configurazioneCache.getDimensione());
					msg = "Dimensione della cache (ConfigurazionePdD) impostata al valore: "+dimensione;
					this.log.info(msg);
					if(alogConsole!=null)
						alogConsole.info(msg);
					this.cache.setCacheSize(dimensione);
				}catch(Exception error){
					throw new DriverConfigurazioneException("Parametro errato per la dimensione della cache (ConfigurazionePdD): "+error.getMessage());
				}
			}
			if( configurazioneCache.getAlgoritmo() != null ){
				msg = "Algoritmo di cache (ConfigurazionePdD) impostato al valore: "+configurazioneCache.getAlgoritmo();
				this.log.info(msg);
				if(alogConsole!=null)
					alogConsole.info(msg);
				if(CostantiConfigurazione.CACHE_MRU.equals(configurazioneCache.getAlgoritmo()))
					this.cache.setCacheAlgoritm(CacheAlgorithm.MRU);
				else
					this.cache.setCacheAlgoritm(CacheAlgorithm.LRU);
			}
			
		}
		
		if( (configurazioneCache.getItemIdleTime() != null) ||
				(configurazioneCache.getItemLifeSecond() != null) ){

			if( configurazioneCache.getItemIdleTime() != null  ){
				int itemIdleTime = -1;
				try{
					itemIdleTime = Integer.parseInt(configurazioneCache.getItemIdleTime());
					msg = "Attributo 'IdleTime' (ConfigurazionePdD) impostato al valore: "+itemIdleTime;
					this.log.info(msg);
					if(alogConsole!=null)
						alogConsole.info(msg);
					this.cache.setItemIdleTime(itemIdleTime);
				}catch(Exception error){
					throw new DriverConfigurazioneException("Parametro errato per l'attributo 'IdleTime' (ConfigurazionePdD): "+error.getMessage());
				}
			}
			if( configurazioneCache.getItemLifeSecond() != null  ){
				int itemLifeSecond = -1;
				try{
					itemLifeSecond = Integer.parseInt(configurazioneCache.getItemLifeSecond());
					msg = "Attributo 'MaxLifeSecond' (ConfigurazionePdD) impostato al valore: "+itemLifeSecond;
					this.log.info(msg);
					if(alogConsole!=null)
						alogConsole.info(msg);
					this.cache.setItemLifeTime(itemLifeSecond);
				}catch(Exception error){
					throw new DriverConfigurazioneException("Parametro errato per l'attributo 'MaxLifeSecond' (ConfigurazionePdD): "+error.getMessage());
				}
			}
			
		}
	}
	
	
	protected IDriverConfigurazioneGet getDriverConfigurazionePdD() {
		return this.driverConfigurazionePdD;
	} 
	
	
	
	
	
	
	/**
	 * Si occupa di effettuare una ricerca nella configurazione, e di inserire la ricerca in cache
	 *
	 * @param keyCache Chiave di ricerca in cache
	 * @param methodName Nome del metodo da invocare
	 * @param arguments Argomenti da passare al metodo
	 * @return l'oggetto se trovato, null altrimenti.
	 * 
	 */
	public synchronized Object getObjectCache(String keyCache,String methodName,
			Connection connectionPdD,
			Object... arguments) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
				
		
		DriverConfigurazioneNotFound dNotFound = null;
		Object obj = null;
		try{
			
//			System.out.println("@"+keyCache+"@ INFO CACHE: "+this.cache.toString());
//			System.out.println("@"+keyCache+"@ KEYS: \n\t"+this.cache.printKeys("\n\t"));

			// Raccolta dati
			if(keyCache == null)
				throw new DriverConfigurazioneException("["+methodName+"]: KeyCache non definita");	

			// se e' attiva una cache provo ad utilizzarla
			if(this.cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						return response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.log.debug("oggetto con chiave ["+keyCache+"] (methodo:"+methodName+") ricerco nella configurazione...");
			try{
				obj = getObject(methodName,connectionPdD,arguments);
			}catch(DriverConfigurazioneNotFound e){
				dNotFound = e;
			}
					
			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				if(dNotFound!=null){
					this.log.info("Aggiungo eccezione not found ["+keyCache+"] in cache");
				}else if(obj!=null){
					this.log.info("Aggiungo oggetto ["+keyCache+"] in cache");
				}else{
					throw new Exception("Metodo ("+methodName+") ha ritornato un valore null");
				}
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					if(dNotFound!=null){
						responseCache.setException(dNotFound);
					}else{
						responseCache.setObject((java.io.Serializable)obj);
					}
					this.cache.put(keyCache,responseCache);
				}catch(UtilsException e){
					this.log.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
				}
			}

		}catch(DriverConfigurazioneException e){
			throw e;
		}catch(DriverConfigurazioneNotFound e){
			throw e;
		}catch(Exception e){
			if(DriverConfigurazioneNotFound.class.getName().equals(e.getClass().getName()))
				throw (DriverConfigurazioneNotFound) e;
			else
				throw new DriverConfigurazioneException("Configurazione, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}
		
		if(dNotFound!=null){
			throw dNotFound;
		}else
			return obj;
	}
	
	/**
	 * Si occupa di effettuare una ricerca nella configurazione
	 *
	 * @param methodNameParam Nome del metodo da invocare
	 * @param arguments Argomenti da passare al metodo
	 * @return l'oggetto se trovato, null altrimenti.
	 * 
	 */
	public Object getObject(String methodNameParam,
			Connection connectionPdD,
			Object... arguments) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		String methodName = null;
		if("getConfigurazioneWithOnlyExtendedInfo".equals(methodNameParam)){
			methodName = "getConfigurazioneGenerale";
		}
		else{
			methodName = methodNameParam+"";
		}
		
		// Effettuo le query nella mia gerarchia di registri.
		Object obj = null;
		DriverConfigurazioneNotFound notFound = null;
		this.log.debug("Cerco nella configurazione");
		try{
			org.openspcoop2.core.config.driver.IDriverConfigurazioneGet driver = getDriver(connectionPdD);
			this.log.debug("invocazione metodo ["+methodName+"]...");
			if(arguments.length==0){
				Method method =  driver.getClass().getMethod(methodName);
				obj = method.invoke(driver);
			}else if(arguments.length==1){
				Method method =  driver.getClass().getMethod(methodName,arguments[0].getClass());
				obj = method.invoke(driver,arguments[0]);
			}else if(arguments.length==2){
				Method method =  driver.getClass().getMethod(methodName,arguments[0].getClass(),arguments[1].getClass());
				obj = method.invoke(driver,arguments[0],arguments[1]);
			}else if(arguments.length==3){
				Method method =  driver.getClass().getMethod(methodName,arguments[0].getClass(),arguments[1].getClass(),arguments[2].getClass());
				obj = method.invoke(driver,arguments[0],arguments[1],arguments[2]);
			}else
				throw new Exception("Troppi argomenti per gestire la chiamata del metodo");
		}catch(DriverConfigurazioneNotFound e){
			this.log.debug("Ricerca nella configurazione non riuscita (metodo ["+methodName+"]): "+e.getMessage());
			notFound=e;
		}
		catch(java.lang.reflect.InvocationTargetException e){
			if(e.getTargetException()!=null){
				if(DriverConfigurazioneNotFound.class.getName().equals(e.getTargetException().getClass().getName())){
					// Non presente
					this.log.debug("Ricerca nella configurazione non riuscita [NotFound] (metodo ["+methodName+"]): "+e.getTargetException().getMessage());
					notFound=new DriverConfigurazioneNotFound(e.getTargetException().getMessage(),e.getTargetException());
				}else if(DriverConfigurazioneException.class.getName().equals(e.getTargetException().getClass().getName())){
					// Non presente
					this.log.debug("Ricerca nella configurazione non riuscita [DriverException] (metodo ["+methodName+"]): "+e.getTargetException().getMessage(),e.getTargetException());
					throw new DriverConfigurazioneException(e.getTargetException().getMessage(),e.getTargetException());
				}else{
					this.log.debug("Ricerca nella configurazione non riuscita [InvTargetExcp.getTarget] (metodo ["+methodName+"]), "+e.getTargetException().getMessage(),e.getTargetException());
					throw new DriverConfigurazioneException(e.getTargetException().getMessage(),e.getTargetException());
				}
			}else{
				this.log.debug("Ricerca nella configurazione non riuscita [InvTargetExcp] (metodo ["+methodName+"]), "+e.getMessage(),e);
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		catch(Exception e){
			this.log.debug("ricerca nella configurazione non riuscita (metodo ["+methodName+"]), "+e.getMessage(),e);
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		// Refresh configurazione locale
		if(notFound!=null){
			try{
				if("getGestioneErroreComponenteCooperazione".equals(methodName)){
					obj = this.configLocalProperties.updateGestioneErroreCooperazione(null);
				}
				else if("getGestioneErroreComponenteIntegrazione".equals(methodName)){
					obj = this.configLocalProperties.updateGestioneErroreIntegrazione(null);
				}				
			}catch(Exception e){
				this.log.debug("Refresh nella configurazione locale non riuscita (metodo ["+methodName+"]): "+e.getMessage());
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
			if(obj==null){
				throw notFound;
			}
		}
		else{		
			try{
				if("getRoutingTable".equals(methodName)){
					obj = this.configLocalProperties.updateRouting((RoutingTable)obj);
				}
				else if("getAccessoRegistro".equals(methodName)){
					obj = this.configLocalProperties.updateAccessoRegistro((AccessoRegistro)obj);
				}
				else if("getAccessoConfigurazione".equals(methodName)){
					obj = this.configLocalProperties.updateAccessoConfigurazione((AccessoConfigurazione)obj);
				}
				else if("getAccessoDatiAutorizzazione".equals(methodName)){
					obj = this.configLocalProperties.updateAccessoDatiAutorizzazione((AccessoDatiAutorizzazione)obj);
				}
				else if("getGestioneErroreComponenteCooperazione".equals(methodName)){
					obj = this.configLocalProperties.updateGestioneErroreCooperazione((GestioneErrore)obj);
				}
				else if("getGestioneErroreComponenteIntegrazione".equals(methodName)){
					obj = this.configLocalProperties.updateGestioneErroreIntegrazione((GestioneErrore)obj);
				}
				else if("getConfigurazioneGenerale".equals(methodName)){
					obj = this.configLocalProperties.updateConfigurazione((Configurazione)obj);
				}
			}catch(Exception e){
				this.log.debug("Refresh nella configurazione locale non riuscita (metodo ["+methodName+"]): "+e.getMessage());
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
			
			try{
				if("getConfigurazioneWithOnlyExtendedInfo".equals(methodNameParam)){
					Configurazione config = (Configurazione) obj;
					Configurazione c = new Configurazione();
					for (Object object : config.getExtendedInfoList()) {
						c.addExtendedInfo(object);
					}
					obj = c;
				}
			}catch(Exception e){
				this.log.debug("Aggiornamento Configurazione Extended non riuscita (metodo ["+methodNameParam+"]): "+e.getMessage());
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		
		this.log.debug("invocazione metodo ["+methodName+"] completata.");
		return obj;
		
	}
	
	
	private org.openspcoop2.core.config.driver.IDriverConfigurazioneGet getDriver(Connection connectionPdD) throws DriverConfigurazioneException{
		
		org.openspcoop2.core.config.driver.IDriverConfigurazioneGet driver = this.driverConfigurazionePdD;
		if((driver instanceof DriverConfigurazioneDB)){
			if(connectionPdD!=null && this.useConnectionPdD){
				//System.out.println("[CONFIG] USE CONNECTION VERSIONE!!!!!");
				driver = new DriverConfigurazioneDB(connectionPdD, this.log, this.tipoDatabase);
				if( ((DriverConfigurazioneDB)driver).create == false){
					throw new DriverConfigurazioneException("Inizializzazione DriverConfigurazioneDB con connessione PdD non riuscita");
				}
			}
			else{
				//System.out.println("[CONFIG] USE DATASOURCE VERSIONE!!!!!");
			}
		}
		
		return driver;
	}	
	
	
	
	
	
	
	
	

	
	// SOGGETTO 
	/**
	 * Restituisce Il soggetto identificato da <var>idSoggetto</var>
	 *
	 * @param aSoggetto Identificatore di un soggetto
	 * @return Il Soggetto identificato dal parametro.
	 */
	public Soggetto getSoggetto(Connection connectionPdD,IDSoggetto aSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Raccolta dati
		if(aSoggetto == null || aSoggetto.getNome()==null || aSoggetto.getTipo()==null)
			throw new DriverConfigurazioneException("[getSoggetto]: Parametro non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getSoggetto_" + aSoggetto.getTipo() + aSoggetto.getNome();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((Soggetto) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		Soggetto sogg = null;
		if(this.cache!=null){
			sogg = (Soggetto) this.getObjectCache(key,"getSoggetto",connectionPdD,aSoggetto);
		}else{
			sogg = (Soggetto) this.getObject("getSoggetto",connectionPdD,aSoggetto);
		}
				
		if(sogg!=null)
			return sogg;
		else
			throw new DriverConfigurazioneNotFound("[getSoggetto] Soggetto non Trovato");
	}

	/**
	 * Restituisce Il soggetto che include la porta delegata identificata da <var>location</var>
	 *
	 * @param location Location che identifica una porta delegata
	 * @return Il Soggetto che include la porta delegata fornita come parametro.
	 */
	public Soggetto getSoggetto(Connection connectionPdD,String location) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		
		// Raccolta dati
		if(location == null)
			throw new DriverConfigurazioneException("[getSoggetto(Location)]: Parametro non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getSoggetto(location)_" + location;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((Soggetto) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		Soggetto sogg = null;
		if(this.cache!=null){
			sogg = (Soggetto) this.getObjectCache(key,"getSoggetto",connectionPdD,location);
		}else{
			sogg = (Soggetto) this.getObject("getSoggetto",connectionPdD,location);
		}
				
		if(sogg!=null)
			return sogg;
		else
			throw new DriverConfigurazioneNotFound("[getSoggetto(location)] Soggetto non Trovato");
	}
	
	/**
	 * Restituisce il soggetto configurato come router, se esiste nella Porta di Dominio un soggetto registrato come Router
	 * 
	 * @return il soggetto configurato come router, se esiste nella Porta di Dominio un soggetto registrato come Router
	 */
	public Soggetto getRouter(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getRouter";
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((Soggetto) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		Soggetto sogg = null;
		if(this.cache!=null){
			sogg = (Soggetto) this.getObjectCache(key,"getRouter",connectionPdD);
		}else{
			sogg = (Soggetto) this.getObject("getRouter",connectionPdD);
		}
				
		if(sogg!=null)
			return sogg;
		else
			throw new DriverConfigurazioneNotFound("[getRouter] Soggetto non Trovato");
	}

	/**
	 * Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * 
	 * @return Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * @throws DriverConfigurazioneException
	 */
	 @SuppressWarnings(value = "unchecked")
	public HashSet<String> getSoggettiVirtuali(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getSoggettiVirtuali";
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((HashSet<String>) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		HashSet<String> lista = null;
		if(this.cache!=null){
			lista = (HashSet<String>) this.getObjectCache(key,"getSoggettiVirtuali",connectionPdD);
		}else{
			lista = (HashSet<String>) this.getObject("getSoggettiVirtuali",connectionPdD);
		}
				
		if(lista!=null)
			return lista;
		else
			throw new DriverConfigurazioneNotFound("[getSoggettiVirtuali] Lista Soggetti Virtuali non costruita");
	}
	
	 /**
	  * Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	  * 
	  * @return Restituisce la lista dei servizi associati a soggetti virtuali gestiti dalla PdD
	  * @throws DriverConfigurazioneException
	  */
	 @SuppressWarnings(value = "unchecked")
	 public HashSet<IDServizio> getServizi_SoggettiVirtuali(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		 // se e' attiva una cache provo ad utilizzarla
		 String key = null;	
		 if(this.cache!=null){
			 key = "getServizi_SoggettiVirtuali";
			 org.openspcoop2.utils.cache.CacheResponse response = 
				 (org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			 if(response != null){
				 if(response.getException()!=null){
					 if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						 throw (DriverConfigurazioneNotFound) response.getException();
					 else
						 throw (DriverConfigurazioneException) response.getException();
				 }else{
					 return ((HashSet<IDServizio>) response.getObject());
				 }
			 }
		 }
			
		// Algoritmo CACHE
		HashSet<IDServizio> lista = null;
		if(this.cache!=null){
			lista = (HashSet<IDServizio>) this.getObjectCache(key,"getServizi_SoggettiVirtuali",connectionPdD);
		}else{
			lista = (HashSet<IDServizio>) this.getObject("getServizi_SoggettiVirtuali",connectionPdD);
		}
					
		 if(lista!=null)
			 return lista;
		 else
			 throw new DriverConfigurazioneNotFound("[getServizi_SoggettiVirtuali] Lista Servizi erogati da Soggetti Virtuali non costruita");
		
	 }
		
	
	
	
	// PORTA DELEGATA
	/**
	 * Restituisce la porta delegata identificato da <var>idPD</var>
	 *
	 * @param idPD Identificatore della Porta Delegata
	 * @return La porta delegata.
	 * 
	 */
	public PortaDelegata getPortaDelegata(Connection connectionPdD,IDPortaDelegata idPD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(idPD==null)
			throw new DriverConfigurazioneException("[getPortaDelegata]: Parametro non definito (idPD)");
		if(idPD.getLocationPD()==null)
			throw new DriverConfigurazioneException("[getPortaDelegata]: Parametro non definito (location)");
		if(idPD.getSoggettoFruitore()==null)
			throw new DriverConfigurazioneException("[getPortaDelegata]: Parametro non definito (soggettoFruitore is null)");
		if(idPD.getSoggettoFruitore().getTipo()==null || idPD.getSoggettoFruitore().getNome()==null)
			throw new DriverConfigurazioneException("[getPortaDelegata]: Parametro non definito (soggettoFruitore is null tipo["+
					idPD.getSoggettoFruitore().getTipo()+"] nome["+idPD.getSoggettoFruitore().getNome()+"])");
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getPortaDelegata_" + idPD.getLocationPD() +"_" + idPD.getSoggettoFruitore().getTipo() + idPD.getSoggettoFruitore().getNome();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((PortaDelegata) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		PortaDelegata pd = null;
		if(this.cache!=null){
			pd = (PortaDelegata) this.getObjectCache(key,"getPortaDelegata",connectionPdD,idPD);
		}else{
			pd = (PortaDelegata) this.getObject("getPortaDelegata",connectionPdD,idPD);
		}
				
		if(pd!=null)
			return pd;
		else
			throw new DriverConfigurazioneNotFound("[getPortaDelegata] PD non trovata");
	} 
	
	
	
	// PORTA APPLICATIVA
	private String toStringFiltriProprieta(Hashtable<String, String> filtroProprietaPorteApplicative){
		StringBuffer bf = new StringBuffer();
		if(filtroProprietaPorteApplicative==null || filtroProprietaPorteApplicative.size()<=0){
			bf.append("Non presenti");
		}else{
			Enumeration<String> keys = filtroProprietaPorteApplicative.keys();
			while(keys.hasMoreElements()){
				bf.append("\n");
				String key = keys.nextElement();
				bf.append(key+" = "+filtroProprietaPorteApplicative.get(key));
			}
		}
		return bf.toString();
	}
	/**
	 * @param pa
	 * @param proprietaPresentiBustaRicevuta
	 * @return se ritorna true la porta applicativa e' autorizzata a ricevere il messaggio, in base ai filtri impostati
	 */
	private boolean checkProprietaFiltroPortaApplicativa(PortaApplicativa pa,Hashtable<String, String> proprietaPresentiBustaRicevuta){
		boolean filtriPAPresenti = false;
		//System.out.println("Entro.... PROPRIETA' PRESENTI["+pa.sizeSetProtocolPropertyList()+"] JMS["+filtroProprietaPorteApplicative.size()+"]");
		
		/*Enumeration<String> en = filtroProprietaPorteApplicative.keys();
		while (en.hasMoreElements()){
			String key = en.nextElement();
			String value = filtroProprietaPorteApplicative.get(key);
			System.out.println("Proprieta' JMS: ["+key+"]=["+value+"]");
		}*/
		
		if(proprietaPresentiBustaRicevuta!=null && (proprietaPresentiBustaRicevuta.size()>0) ){
		
			for(int i=0; i<pa.sizeProprietaProtocolloList(); i++){
				String nome = pa.getProprietaProtocollo(i).getNome();
				String value = pa.getProprietaProtocollo(i).getValore();
				this.log.debug("ProprietaProtocollo della PA["+pa.getNome()+"] nome["+nome+"] valore["+value+"]");
				
				//System.out.println("Esamino ["+nome+"]["+value+"]");
				if(value.indexOf("!#!")!=-1){
					//System.out.println("FILTRIPAPresenti");
					filtriPAPresenti = true;
					String [] split = value.split("!#!");
					if(split!=null && split.length==2){
						// operator come filtro
						// I filtri nelle Protocol Properties sono in OR
						//System.out.println("prelevo proprrieta...");
						
						String operatoreP = split[1].trim();
						String valoreP = split[0].trim();
						
						String proprietaArrivata = proprietaPresentiBustaRicevuta.get(nome);
						
						this.log.debug("ProtocolProperty della PA["+pa.getNome()+"] nome["+nome+"] valore["+value+"] interpretata come ["+nome+"]["+operatoreP+"]["+valoreP+"]. Viene utilizzata per la proprieta' della busta con valore ["+proprietaArrivata+"]");
						
						if(proprietaArrivata!=null){
							
							proprietaArrivata = proprietaArrivata.trim();
							//System.out.println("NOMEP["+nome+"] OPERATORE["+operatoreP+"] PROPRIETA' ARRIVATA["+proprietaArrivata+"] VALORE["+split[0].trim()+"]");
														
							if("=".equals(operatoreP)){
								if(proprietaArrivata.equals(valoreP)){
									return true;
								}
							}else if(">".equals(operatoreP)){
								if(proprietaArrivata.compareTo(valoreP)>0){
									return true;
								}
							}else if(">=".equals(operatoreP)){
								if(proprietaArrivata.compareTo(valoreP)>=0){
									return true;
								}
							}else if("<".equals(operatoreP)){
								if(proprietaArrivata.compareTo(valoreP)<0){
									return true;
								}
							}else if("<=".equals(operatoreP)){
								if(proprietaArrivata.compareTo(valoreP)<=0){
									return true;
								}
							}else if("<>".equals(operatoreP)){
								if(!proprietaArrivata.equals(valoreP)){
									return true;
								}
							}else if("like".equalsIgnoreCase(operatoreP)){
								String valoreMatch = valoreP.replaceAll("%", "");
								int indexExpr = proprietaArrivata.indexOf(valoreMatch);
								if(indexExpr>=0){
									return true;
								}
							}
							else{
								this.log.error("[checkProprietaFiltroPortaApplicativa] Operator ["+operatoreP+"] non supportato per le protocol properties...");
							}
						}
					}
				}
			}
		
		}else{
			
			// Proprieta' non presenti nella busta ricevuta
			// Controllo se la PA richiedeva un filtro per protocol properties.
			for(int i=0; i<pa.sizeProprietaProtocolloList(); i++){
				String nome = pa.getProprietaProtocollo(i).getNome();
				String value = pa.getProprietaProtocollo(i).getValore();
				this.log.debug("ProtocolProperty della PA["+pa.getNome()+"] nome["+nome+"] valore["+value+"]");
				
				//System.out.println("Esamino ["+nome+"]["+value+"]");
				if(value.indexOf("!#!")!=-1){
					//System.out.println("FILTRIPAPresenti");
					this.log.debug("ProtocolProperty della PA["+pa.getNome()+"] nome["+nome+"] valore["+value+"] contiene una richiesta di filtro, siccome la busta arrivata non contiene proprieta', la PA non ha diritto a riceverla.");
					filtriPAPresenti = true;
					break;
				}
			}
		}

		
		//System.out.println("FILTRI PA["+filtriPAPresenti+"]");
		if(filtriPAPresenti)
			return false;
		else
			return true;
	}
	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 *
	 * @param idPA Identificatore di una Porta Applicativa
	 * @return La porta applicativa
	 * 
	 */
	public PortaApplicativa getPortaApplicativa(Connection connectionPdD,IDPortaApplicativa idPA,Hashtable<String, String> proprietaPresentiBustaRicevuta) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(idPA == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (idPA is null)");
		if(idPA.getIDServizio()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (idPA.getIDServizio() is null)");
		if(idPA.getIDServizio().getServizio()==null || idPA.getIDServizio().getTipoServizio()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (tipoServizio=["+idPA.getIDServizio().getTipoServizio()+"] servizio=["+idPA.getIDServizio().getServizio()+"])");
		if(idPA.getIDServizio().getSoggettoErogatore()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (idPA.getIDServizio().getSoggettoErogatore() is null)");
		if(idPA.getIDServizio().getSoggettoErogatore().getTipo()==null || idPA.getIDServizio().getSoggettoErogatore().getNome()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (tipoSoggetto=["+idPA.getIDServizio().getSoggettoErogatore().getTipo()+"] servizio=["+idPA.getIDServizio().getSoggettoErogatore().getNome()+"])");
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getPortaApplicativa_" + idPA.getIDServizio().getSoggettoErogatore().getTipo()+idPA.getIDServizio().getSoggettoErogatore().getNome() + "_" + idPA.getIDServizio().getTipoServizio() + idPA.getIDServizio().getServizio();
			if(idPA.getIDServizio().getAzione()!=null)
				key = key + "_" + idPA.getIDServizio().getAzione();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					PortaApplicativa pa = (PortaApplicativa) response.getObject();
					if(pa!=null){
						// non viene passato in tutti i metodi le proprietaPresentiInBusta, e quindi non funziona!
						// if(checkProprietaFiltroPortaApplicativa(pa,proprietaPresentiBustaRicevuta))
						return pa;
					}
				}
			}
		}
			
		// Algoritmo CACHE
		PortaApplicativa pa = null;
		if(this.cache!=null){
			pa = (PortaApplicativa) this.getObjectCache(key,"getPortaApplicativa",connectionPdD,idPA);
		}else{
			pa = (PortaApplicativa) this.getObject("getPortaApplicativa",connectionPdD,idPA);
		}
		
		if(pa!=null){
			// non viene passato in tutti i metodi le proprietaPresentiInBusta, e quindi non funziona!
			// if(checkProprietaFiltroPortaApplicativa(pa,proprietaPresentiBustaRicevuta))
			return pa;
		}
		
		throw new DriverConfigurazioneNotFound("[getPortaApplicativa] PA non trovata");
	} 

	public PortaApplicativa getPortaApplicativa(Connection connectionPdD,String nomePorta, IDSoggetto soggettoProprietario) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		// Raccolta dati
		if(nomePorta == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (nomePorta is null)");
		if(soggettoProprietario.getNome()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (soggettoProprietario.getNome() is null)");
		if(soggettoProprietario.getTipo()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (soggettoProprietario.getTipo() is null)");
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getPortaApplicativa_" + nomePorta +"_"+soggettoProprietario.toString();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					PortaApplicativa pa = (PortaApplicativa) response.getObject();
					if(pa!=null){
						// non viene passato in tutti i metodi le proprietaPresentiInBusta, e quindi non funziona!
						// if(checkProprietaFiltroPortaApplicativa(pa,proprietaPresentiBustaRicevuta))
						return pa;
					}
				}
			}
		}
//			
//		// Algoritmo CACHE
		PortaApplicativa pa = null;
		if(this.cache!=null){
			pa = (PortaApplicativa) this.getObjectCache(key,"getPortaApplicativa",connectionPdD,nomePorta,soggettoProprietario);
		}else{
			pa = (PortaApplicativa) this.getObject("getPortaApplicativa",connectionPdD,nomePorta,soggettoProprietario);
		}
		
		if(pa!=null){
			// non viene passato in tutti i metodi le proprietaPresentiInBusta, e quindi non funziona!
			// if(checkProprietaFiltroPortaApplicativa(pa,proprietaPresentiBustaRicevuta))
			return pa;
		}
		
		throw new DriverConfigurazioneNotFound("[getPortaApplicativa] PA non trovata");
	} 
		
	/**
	 * Restituisce un array di soggetti reali (e associata porta applicativa) 
	 * che possiedono il soggetto SoggettoVirtuale identificato da <var>idPA</var>
	 *
	 * @param idPA Identificatore di una Porta Applicativa con soggetto Virtuale
	 * @return una porta applicativa
	 * 
	 */
	 @SuppressWarnings(value = "unchecked")
	public Hashtable<IDSoggetto,PortaApplicativa> getPorteApplicative_SoggettiVirtuali(Connection connectionPdD,IDPortaApplicativa idPA,
			Hashtable<String, String> proprietaPresentiBustaRicevuta,boolean useFiltroProprieta)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(idPA == null || idPA.getIDServizio()==null || idPA.getIDServizio().getServizio()==null || idPA.getIDServizio().getTipoServizio()==null || idPA.getIDServizio().getSoggettoErogatore()==null || idPA.getIDServizio().getSoggettoErogatore().getTipo()==null || idPA.getIDServizio().getSoggettoErogatore().getNome()==null)
			throw new DriverConfigurazioneException("[getPorteApplicative_SoggettiVirtuali]: Parametro non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			this.log.debug("Search porte applicative soggetti virtuali in cache...");
			key = "getPorteApplicative_SoggettiVirtuali_" + idPA.getIDServizio().getSoggettoErogatore().getTipo()+idPA.getIDServizio().getSoggettoErogatore().getNome() + "_" + idPA.getIDServizio().getTipoServizio() + idPA.getIDServizio().getServizio();
			if(idPA.getIDServizio().getAzione()!=null)
				key = key + "_" + idPA.getIDServizio().getAzione();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					if(response.getObject()!=null){
						this.log.debug("Oggetto in cache trovato. Analizzo porte virtuali trovate rispetto alle proprieta' presenti nella busta arrivata: "+this.toStringFiltriProprieta(proprietaPresentiBustaRicevuta));
						Hashtable<IDSoggetto,PortaApplicativa> pa = (Hashtable<IDSoggetto,PortaApplicativa>) response.getObject();
						Hashtable<IDSoggetto,PortaApplicativa> paChecked = new Hashtable<IDSoggetto,PortaApplicativa>();
						Enumeration<IDSoggetto> enumer = pa.keys();
						while(enumer.hasMoreElements()){
							IDSoggetto idS = enumer.nextElement();
							PortaApplicativa paC = pa.get(idS);
							this.log.debug("Analizzo pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]...");
							if(useFiltroProprieta){
								boolean ricezioneAutorizzata = checkProprietaFiltroPortaApplicativa(paC,proprietaPresentiBustaRicevuta);
								if(ricezioneAutorizzata){
									this.log.debug("Filtri matchano le protocol properties della pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
									paChecked.put(idS,paC);
								}else{
									this.log.debug("Filtri non matchano le protocol properties della pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
								}
							}else{
								this.log.debug("Invocazione metodo senza la richiesta di filtro per proprieta, aggiunta pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
								paChecked.put(idS,paC);
							}
						}
						if(paChecked.size()>0)
							return paChecked;
					}
				}
			}
		}
		
		// Algoritmo CACHE
		Hashtable<IDSoggetto,PortaApplicativa> pa = null;
		if(this.cache!=null){
			pa = (Hashtable<IDSoggetto,PortaApplicativa>) this.getObjectCache(key,"getPorteApplicative_SoggettiVirtuali",connectionPdD,idPA);
		}else{
			pa = (Hashtable<IDSoggetto,PortaApplicativa>) this.getObject("getPorteApplicative_SoggettiVirtuali",connectionPdD,idPA);
		}
		
		if(pa!=null){
			this.log.debug("Oggetto trovato. Analizzo porte virtuali trovate rispetto alle proprieta' presenti nella busta arrivata: "+this.toStringFiltriProprieta(proprietaPresentiBustaRicevuta));
			Hashtable<IDSoggetto,PortaApplicativa> paChecked = new Hashtable<IDSoggetto,PortaApplicativa>();
			Enumeration<IDSoggetto> enumer = pa.keys();
			while(enumer.hasMoreElements()){
				IDSoggetto idS = enumer.nextElement();
				PortaApplicativa paC = pa.get(idS);
				this.log.debug("Analizzo pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]...");
				if(useFiltroProprieta){
					boolean ricezioneAutorizzata = checkProprietaFiltroPortaApplicativa(paC,proprietaPresentiBustaRicevuta);
					if(ricezioneAutorizzata){
						this.log.debug("Filtri matchano le protocol properties della pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
						paChecked.put(idS,paC);
					}else{
						this.log.debug("Filtri non matchano le protocol properties della pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
					}
				}else{
					this.log.debug("Invocazione metodo senza la richiesta di filtro per proprieta, aggiunta pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
					paChecked.put(idS,paC);
				}
			}
			if(paChecked.size()>0)
				return paChecked;
		}
		
		throw new DriverConfigurazioneNotFound("[getPorteApplicative_SoggettiVirtuali] PA_SoggettiVirtuali non trovati");
	} 

	/**
	 * Restituisce la lista delle porte applicative con il nome fornito da parametro.
	 * Possono esistere piu' porte applicative con medesimo nome, che appartengono a soggetti differenti.
	 * Se indicati i parametri sui soggetti vengono utilizzati come filtro per localizzare in maniera piu' precisa la PA
	 * 
	 * @param nomePA Nome di una Porta Applicativa
	 * @param tipoSoggettoProprietario Tipo del Soggetto Proprietario di una Porta Applicativa
	 * @param nomeSoggettoProprietario Nome del Soggetto Proprietario di una Porta Applicativa
	 * @return La lista di porte applicative
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<PortaApplicativa> getPorteApplicative(
			Connection connectionPdD,String nomePA,String tipoSoggettoProprietario,String nomeSoggettoProprietario) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(nomePA == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (nomePA is null)");
					
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getPorteApplicativeAsList_" + nomePA;
			if(tipoSoggettoProprietario!=null)
				key = key + "_" + tipoSoggettoProprietario;
			if(nomeSoggettoProprietario!=null)
				key = key + "_" + nomeSoggettoProprietario;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					List<PortaApplicativa> listPA = (List<PortaApplicativa>) response.getObject();
					if(listPA!=null){
						// non viene passato in tutti i metodi le proprietaPresentiInBusta, e quindi non funziona!
						// if(checkProprietaFiltroPortaApplicativa(pa,proprietaPresentiBustaRicevuta))
						return listPA;
					}
				}
			}
		}
			
		// Algoritmo CACHE
		List<PortaApplicativa> listPA = null;
		String tmpTipoSoggetto = tipoSoggettoProprietario;
		if(tmpTipoSoggetto==null){
			tmpTipoSoggetto = "";
		}
		String tmpNomeSoggetto = nomeSoggettoProprietario;
		if(tmpNomeSoggetto==null){
			tmpNomeSoggetto = "";
		}
		if(this.cache!=null){
			listPA = (List<PortaApplicativa>) this.getObjectCache(key,"getPorteApplicative",connectionPdD,nomePA,tmpTipoSoggetto,tmpNomeSoggetto);
		}else{
			listPA = (List<PortaApplicativa>) this.getObject("getPorteApplicative",connectionPdD,nomePA,tmpTipoSoggetto,tmpNomeSoggetto);
		}
		
		if(listPA!=null){
			return listPA;
		}
		
		throw new DriverConfigurazioneNotFound("[getPortaApplicativa] PA non trovata");
	} 
 
	 
	 
	
	
	
	
	// SERVIZIO APPLICATIVO
	/**
	 * Restituisce il servizio applicativo, cercandolo prima nella porta delegata <var>location</var>.
	 * Se nella porta delegata non vi e' viene cercato 
	 * poi in un specifico soggetto se specificato con <var>aSoggetto</var>, altrimenti in ogni soggetto. 
	 *
	 * @param idPD Identificatore della porta delegata.
	 * @param servizioApplicativo Servizio Applicativo
	 * @return Il Servizio Applicativo.
	 * 
	 */
	public ServizioApplicativo getServizioApplicativo(Connection connectionPdD,IDPortaDelegata idPD,String servizioApplicativo)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(idPD == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (idPD)");	
		if(idPD.getLocationPD()==null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (location)");
		if(idPD.getSoggettoFruitore()==null || idPD.getSoggettoFruitore().getTipo()==null || idPD.getSoggettoFruitore().getNome()==null )
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (soggetto fruitore)");
		if(servizioApplicativo == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (servizioApplicativo)");			
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getServizioApplicativo_" + idPD.getLocationPD() +"_" + idPD.getSoggettoFruitore().getTipo() + idPD.getSoggettoFruitore().getNome()+"_"+servizioApplicativo;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ServizioApplicativo) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		ServizioApplicativo s = null;
		if(this.cache!=null){
			s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativo",connectionPdD,idPD,servizioApplicativo);
		}else{
			s = (ServizioApplicativo) this.getObject("getServizioApplicativo",connectionPdD,idPD,servizioApplicativo);
		}
				
		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("[getServizioApplicativo] Servizio Applicativo non trovato");
	} 

	/**
	 * Restituisce il servizio applicativo, cercandolo prima nella porta applicativa <var>location</var>
	 * e poi nel soggetto <var>aSoggetto</var>. 
	 *
	 * @param idPA Identificatore della porta applicativa.
	 * @param servizioApplicativo Servizio Applicativo
	 * @return Il Servizio Applicativo.
	 * 
	 */
	public ServizioApplicativo getServizioApplicativo(Connection connectionPdD,IDPortaApplicativa idPA,String servizioApplicativo)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(idPA == null || idPA.getIDServizio()==null || idPA.getIDServizio().getServizio()==null || idPA.getIDServizio().getTipoServizio()==null || idPA.getIDServizio().getSoggettoErogatore()==null || idPA.getIDServizio().getSoggettoErogatore().getTipo()==null || idPA.getIDServizio().getSoggettoErogatore().getNome()==null || servizioApplicativo==null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getServizioApplicativo_" + idPA.getIDServizio().getSoggettoErogatore().getTipo()+idPA.getIDServizio().getSoggettoErogatore().getNome() + "_" + idPA.getIDServizio().getTipoServizio() + idPA.getIDServizio().getServizio();
			if(idPA.getIDServizio().getAzione()!=null)
				key = key + "_" + idPA.getIDServizio().getAzione();
			key = key + "_SA:" + servizioApplicativo;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ServizioApplicativo) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		ServizioApplicativo s = null;
		if(this.cache!=null){
			s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativo",connectionPdD ,idPA,servizioApplicativo);
		}else{
			s = (ServizioApplicativo) this.getObject("getServizioApplicativo",connectionPdD, idPA,servizioApplicativo);
		}
		
		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("[getServizioApplicativo] Servizio Applicativo non trovato");
	}
	
	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param idPD Identificatore della porta delegata.
	 * @param aUser User utilizzato nell'header HTTP Authentication.
	 * @param aPassword Password utilizzato nell'header HTTP Authentication.
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	public ServizioApplicativo getServizioApplicativoAutenticato(Connection connectionPdD,IDPortaDelegata idPD, String aUser,String aPassword)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(aUser==null || aPassword==null)
			throw new DriverConfigurazioneException("[getServizioApplicativoAutenticato]: Parametro non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getServizioApplicativoAutenticatoBASIC";
			if(idPD!=null && idPD.getLocationPD()!=null)
				key = key+ "_" + idPD.getLocationPD();
			if(idPD!=null && idPD.getSoggettoFruitore()!=null)
				key = key +"_" + idPD.getSoggettoFruitore().getTipo() + idPD.getSoggettoFruitore().getNome();
			key = key +"_"+aUser+"_"+aPassword;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ServizioApplicativo) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		ServizioApplicativo s = null;
		if(this.cache!=null){
			s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoAutenticato",connectionPdD, idPD,aUser,aPassword);
		}else{
			s = (ServizioApplicativo) this.getObject("getServizioApplicativoAutenticato",connectionPdD, idPD,aUser,aPassword);
		}
				
		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("[getServizioApplicativoAutenticato] Servizio Applicativo non trovato");
	} 
	
	public ServizioApplicativo getServizioApplicativoAutenticato(Connection connectionPdD,String aUser,String aPassword)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(aUser==null || aPassword==null)
			throw new DriverConfigurazioneException("[getServizioApplicativoAutenticato]: Parametro non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getServizioApplicativoAutenticatoBASIC";
			key = key +"_"+aUser+"_"+aPassword;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ServizioApplicativo) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		ServizioApplicativo s = null;
		if(this.cache!=null){
			s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoAutenticato",connectionPdD, aUser,aPassword);
		}else{
			s = (ServizioApplicativo) this.getObject("getServizioApplicativoAutenticato",connectionPdD, aUser,aPassword);
		}
				
		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("[getServizioApplicativoAutenticato] Servizio Applicativo non trovato");
	} 

	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param idPD Identificatore della porta delegata.
	 * @param aSubject Subject utilizzato nella connessione HTTPS.
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	public ServizioApplicativo getServizioApplicativoAutenticato(Connection connectionPdD,IDPortaDelegata idPD, String aSubject) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(aSubject==null)
			throw new DriverConfigurazioneException("[getServizioApplicativoAutenticato]: Parametro non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getServizioApplicativoAutenticatoSSL";
			if(idPD!=null && idPD.getLocationPD()!=null)
				key = key+ "_" + idPD.getLocationPD();
			if(idPD!=null && idPD.getSoggettoFruitore()!=null)
				key = key +"_" + idPD.getSoggettoFruitore().getTipo() + idPD.getSoggettoFruitore().getNome();
			key = key +"_"+aSubject;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ServizioApplicativo) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		ServizioApplicativo s = null;
		if(this.cache!=null){
			s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoAutenticato",connectionPdD, idPD,aSubject);
		}else{
			s = (ServizioApplicativo) this.getObject("getServizioApplicativoAutenticato",connectionPdD, idPD,aSubject);
		}
			
		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("[getServizioApplicativoAutenticato] Servizio Applicativo non trovato");
	}

	public ServizioApplicativo getServizioApplicativoAutenticato(Connection connectionPdD,String aSubject) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(aSubject==null)
			throw new DriverConfigurazioneException("[getServizioApplicativoAutenticato]: Parametro non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getServizioApplicativoAutenticatoSSL";
			key = key +"_"+aSubject;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ServizioApplicativo) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		ServizioApplicativo s = null;
		if(this.cache!=null){
			s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoAutenticato",connectionPdD, aSubject);
		}else{
			s = (ServizioApplicativo) this.getObject("getServizioApplicativoAutenticato",connectionPdD, aSubject);
		}
			
		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("[getServizioApplicativoAutenticato] Servizio Applicativo non trovato");
	}
	
	
	
	
	
	
	
	// CONFIGURAZIONE
	/**
	 * Restituisce la RoutingTable definita nella Porta di Dominio 
	 *
	 * @return RoutingTable
	 * 
	 */
	public RoutingTable getRoutingTable(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(this.configurazioneDinamica==false){
			if(ConfigurazionePdD.routingTable!=null)
				return ConfigurazionePdD.routingTable;
		}
				
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getRoutingTable";
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((RoutingTable) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		RoutingTable r = null;
		if(this.cache!=null){
			r = (RoutingTable) this.getObjectCache(key,"getRoutingTable",connectionPdD);
		}else{
			r = (RoutingTable) this.getObject("getRoutingTable",connectionPdD);
		}
			
		if(r!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(this.configurazioneDinamica==false){
				ConfigurazionePdD.routingTable= r;
			}
			return r;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getRoutingTable] RoutingTable non trovata");
		}
	} 
	
	/**
	 * Restituisce l'accesso al registro definito nella Porta di Dominio 
	 *
	 * @return AccessoRegistro
	 * 
	 */
	public AccessoRegistro getAccessoRegistro(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(this.configurazioneDinamica==false){
			if(ConfigurazionePdD.accessoRegistro!=null)
				return ConfigurazionePdD.accessoRegistro;
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getAccessoRegistro";
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((AccessoRegistro) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		AccessoRegistro ar = null;
		if(this.cache!=null){
			ar = (AccessoRegistro) this.getObjectCache(key,"getAccessoRegistro",connectionPdD);
		}else{
			ar = (AccessoRegistro) this.getObject("getAccessoRegistro",connectionPdD);
		}
		
		if(ar!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(this.configurazioneDinamica==false){
				ConfigurazionePdD.accessoRegistro = ar;
			}
			return ar;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getAccessoRegistro] Configurazione di accesso ai registri non trovata");
		}
	} 
	
	public AccessoConfigurazione getAccessoConfigurazione(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(this.configurazioneDinamica==false){
			if(ConfigurazionePdD.accessoConfigurazione!=null)
				return ConfigurazionePdD.accessoConfigurazione;
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getAccessoConfigurazione";
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((AccessoConfigurazione) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		AccessoConfigurazione object = null;
		if(this.cache!=null){
			object = (AccessoConfigurazione) this.getObjectCache(key,"getAccessoConfigurazione",connectionPdD);
		}else{
			object = (AccessoConfigurazione) this.getObject("getAccessoConfigurazione",connectionPdD);
		}
		
		if(object!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(this.configurazioneDinamica==false){
				ConfigurazionePdD.accessoConfigurazione = object;
			}
			return object;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getAccessoConfigurazione] Configurazione di accesso alla configurazione non trovata");
		}
	} 
	
	public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(this.configurazioneDinamica==false){
			if(ConfigurazionePdD.accessoDatiAutorizzazione!=null)
				return ConfigurazionePdD.accessoDatiAutorizzazione;
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getAccessoDatiAutorizzazione";
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((AccessoDatiAutorizzazione) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		AccessoDatiAutorizzazione object = null;
		if(this.cache!=null){
			object = (AccessoDatiAutorizzazione) this.getObjectCache(key,"getAccessoDatiAutorizzazione",connectionPdD);
		}else{
			object = (AccessoDatiAutorizzazione) this.getObject("getAccessoDatiAutorizzazione",connectionPdD);
		}
		
		if(object!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(this.configurazioneDinamica==false){
				ConfigurazionePdD.accessoDatiAutorizzazione = object;
			}
			return object;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getAccessoDatiAutorizzazione] Configurazione di accesso ai dati di autorizzazione non trovata");
		}
	} 


	

	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di cooperazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	public GestioneErrore getGestioneErroreComponenteCooperazione(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.getGestioneErrore(connectionPdD,true);
	}
	
	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di integrazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	public GestioneErrore getGestioneErroreComponenteIntegrazione(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.getGestioneErrore(connectionPdD,false);
	}
	
	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio 
	 *
	 * @return  La gestione dell'errore
	 * 
	 */
	private GestioneErrore getGestioneErrore(Connection connectionPdD,boolean cooperazione) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(this.configurazioneDinamica==false){
			if(cooperazione){
				if(ConfigurazionePdD.gestioneErroreComponenteCooperazione!=null)
					return ConfigurazionePdD.gestioneErroreComponenteCooperazione;
			}else{
				if(ConfigurazionePdD.gestioneErroreComponenteIntegrazione!=null)
					return ConfigurazionePdD.gestioneErroreComponenteIntegrazione;
			}
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getGestioneErrore";
			if(cooperazione){
				key = key + "ComponenteCooperazione";
			}else{
				key = key + "ComponenteIntegrazione";
			}
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((GestioneErrore) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		GestioneErrore ge = null;
		String nomeMetodo = "getGestioneErrore";
		if(cooperazione){
			nomeMetodo = nomeMetodo + "ComponenteCooperazione";
		}else{
			nomeMetodo = nomeMetodo + "ComponenteIntegrazione";
		}
		if(this.cache!=null){
			try{
				ge = (GestioneErrore) this.getObjectCache(key,nomeMetodo,connectionPdD);
			}catch(DriverConfigurazioneException e){
				String erroreCooperazione= "Configurazione, Algoritmo di Cache fallito: Metodo (getGestioneErroreComponenteCooperazione) ha ritornato un valore null";
				String erroreIntegrazione= "Configurazione, Algoritmo di Cache fallito: Metodo (getGestioneErroreComponenteIntegrazione) ha ritornato un valore null";
				if(e.getMessage()!=null 
						&& !erroreCooperazione.equals(e.getMessage())
						&& !erroreIntegrazione.equals(e.getMessage())){
					throw e;
				}
			}
		}else{
			ge = (GestioneErrore) this.getObject(nomeMetodo,connectionPdD);
		}
		
		if(ge!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(this.configurazioneDinamica==false){
				if(cooperazione)
					ConfigurazionePdD.gestioneErroreComponenteCooperazione = ge;
				else
					ConfigurazionePdD.gestioneErroreComponenteIntegrazione = ge;
			}
			return ge;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getGestioneErrore] GestioneErrore non trovato");
		}
	} 
	
	
	/**
	 * Restituisce la configurazione generale della Porta di Dominio 
	 *
	 * @return Configurazione
	 * 
	 */
	public Configurazione getConfigurazioneGenerale(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(this.configurazioneDinamica==false){
			if(ConfigurazionePdD.configurazioneGeneralePdD!=null){
				return ConfigurazionePdD.configurazioneGeneralePdD;
			}
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getConfigurazioneGenerale";
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((Configurazione) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		Configurazione c = null;
		if(this.cache!=null){
			c = (Configurazione) this.getObjectCache(key,"getConfigurazioneGenerale",connectionPdD);
		}else{
			c = (Configurazione) this.getObject("getConfigurazioneGenerale",connectionPdD);
		}
		
		if(c!=null){
            // Se e' attiva una configurazione statica, la utilizzo.
			if(this.configurazioneDinamica==false){
				ConfigurazionePdD.configurazioneGeneralePdD= c;
			}
			return c;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getConfigurazioneGenerale] Configurazione Generale non trovata");
		}
	}


	public StatoServiziPdd getStatoServiziPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.driverConfigurazionePdD.getStatoServiziPdD();
	}
	public void updateStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		if(this.driverConfigurazionePdD instanceof DriverConfigurazioneDB){
			((DriverConfigurazioneDB)this.driverConfigurazionePdD).updateStatoServiziPdD(servizi);
		}
	} 
	
	public SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.driverConfigurazionePdD.getSystemPropertiesPdD();
	}
	public void updateSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		if(this.driverConfigurazionePdD instanceof DriverConfigurazioneDB){
			((DriverConfigurazioneDB)this.driverConfigurazionePdD).updateSystemPropertiesPdD(systemProperties);
		}
	}
	

	public Configurazione getConfigurazioneWithOnlyExtendedInfo(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getConfigurazioneWithOnlyExtendedInfo";
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((Configurazione) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		Configurazione c = null;
		if(this.cache!=null){
			c = (Configurazione) this.getObjectCache(key,"getConfigurazioneWithOnlyExtendedInfo",connectionPdD);
		}else{
			c = (Configurazione) this.getObject("getConfigurazioneWithOnlyExtendedInfo",connectionPdD);
		}
		
		if(c!=null){
			return c;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getConfigurazioneWithOnlyExtendedInfo] Configurazione Generale non trovata");
		}
	}
	
	public static final String prefixKey_getSingleExtendedInfoConfigurazione = "getSingleExtendedInfoConfigurazione_";
	
	public Object getSingleExtendedInfoConfigurazione(String id, Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = prefixKey_getSingleExtendedInfoConfigurazione+id;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverConfigurazioneNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return response.getObject();
				}
			}
		}
			
		// Algoritmo CACHE
		Object c = null;
		Configurazione configurazioneGenerale = this.getConfigurazioneGenerale(connectionPdD);
		if(this.cache!=null){
			c = this.getObjectCache(key,"getConfigurazioneExtended",connectionPdD, configurazioneGenerale, id);
		}else{
			c = this.getObject("getConfigurazioneExtended",connectionPdD, configurazioneGenerale, id);
		}
		
		if(c!=null){
			return c;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getSingleExtendedInfoConfigurazione] Configurazione Generale non trovata");
		}
		
	}
	
}
