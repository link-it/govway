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



package org.openspcoop2.protocol.registry;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.uddi.DriverRegistroServiziUDDI;
import org.openspcoop2.core.registry.driver.web.DriverRegistroServiziWEB;
import org.openspcoop2.core.registry.driver.ws.DriverRegistroServiziWS;
import org.openspcoop2.core.registry.driver.xml.DriverRegistroServiziXML;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapperUtilities;
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

public class RegistroServizi  {

	/** Fonti su cui effettuare le query:
	 * - CACHE
	 * - Registri deployati
	 */
	private Cache cache = null;
	private java.util.Hashtable<String,IDriverRegistroServiziGet> driverRegistroServizi;

	/** Indicazione se usare la connessione della PdD */
	private Hashtable<String, String> mappingNomeRegistroToTipiDatabase = new Hashtable<String, String>();
	private Hashtable<String, Boolean> mappingNomeRegistroToUseConnectionPdD = new Hashtable<String, Boolean>();
	
	/** Eventuale RegistriXML da cui prelevare le definizioni dei connettori */
	private Vector<DriverRegistroServiziXML> registriXML;

	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** Indicazione sulla gestione della gerarchia dei registri */
	private boolean raggiungibilitaTotale = false;
	
	/** Indicazione se la porta di dominio deve processare gli accordi di servizio, i servizi e i fruitori ancora in stato di bozza */
	private boolean readObjectStatoBozza = false;
//	public boolean isReadObjectStatoBozza() {
//		return this.readObjectStatoBozza;
//	}

	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		
	
	/* --------------- Cache --------------------*/
	public void resetCache() throws DriverRegistroServiziException{
		if(this.cache!=null){
			try{
				this.cache.clear();
			}catch(Exception e){
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
	}
	public String printStatsCache(String separator) throws DriverRegistroServiziException{
		if(this.cache!=null){
			try{
				return this.cache.printStats(separator);
			}catch(Exception e){
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}else{
			throw new DriverRegistroServiziException("Cache non abilitata");
		}
	}
	public void abilitaCache() throws DriverRegistroServiziException{
		if(this.cache!=null)
			throw new DriverRegistroServiziException("Cache gia' abilitata");
		else{
			try{
				this.cache = new Cache(CostantiRegistroServizi.CACHE_REGISTRO_SERVIZI);
			}catch(Exception e){
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
	}
	public void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws DriverRegistroServiziException{
		if(this.cache!=null)
			throw new DriverRegistroServiziException("Cache gia' abilitata");
		else{
			try{
				org.openspcoop2.core.config.Cache configurazioneCache = new org.openspcoop2.core.config.Cache();
				if(dimensioneCache!=null)
					configurazioneCache.setDimensione(dimensioneCache+"");
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
				initCacheRegistriServizi(configurazioneCache,null);
			}catch(Exception e){
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
	}
	public void disabilitaCache() throws DriverRegistroServiziException{
		if(this.cache==null)
			throw new DriverRegistroServiziException("Cache gia' disabilitata");
		else{
			try{
				this.cache.clear();
				this.cache = null;
			}catch(Exception e){
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
	}
	public String listKeysCache(String separator) throws DriverRegistroServiziException{
		if(this.cache!=null){
			try{
				return this.cache.printKeys(separator);
			}catch(Exception e){
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}else{
			throw new DriverRegistroServiziException("Cache non abilitata");
		}
	}
	public String getObjectCache(String key) throws DriverRegistroServiziException{
		if(this.cache!=null){
			try{
				Object o = this.cache.get(key);
				if(o!=null){
					return o.toString();
				}else{
					return "oggetto con chiave ["+key+"] non presente";
				}
			}catch(Exception e){
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}else{
			throw new DriverRegistroServiziException("Cache non abilitata");
		}
	}
	public void removeObjectCache(String key) throws DriverRegistroServiziException{
		if(this.cache!=null){
			try{
				this.cache.remove(key);
			}catch(Exception e){
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}else{
			throw new DriverRegistroServiziException("Cache non abilitata");
		}
	}



	/*   -------------- Costruttore -----------------  */ 

	/**
	 * Si occupa di inizializzare l'engine che permette di effettuare
	 * query al registro dei servizi.
	 * L'engine inizializzato sara' diverso a seconda del <var>tipo</var> di registro :
	 * <ul>
	 * <li> {@link DriverRegistroServiziUDDI}, interroga un registro dei servizi UDDI.
	 * <li> {@link DriverRegistroServiziXML}, interroga un registro dei servizi realizzato tramite un file xml.
	 * <li> {@link DriverRegistroServiziWEB}, interroga un registro dei servizi realizzato come un WEB Server.
	 * <li> {@link DriverRegistroServiziDB}, interroga un registro dei servizi realizzato come un Database relazionale.
	 * </ul>
	 *
	 * @param accessoRegistro Informazioni per accedere al registro Servizi.
	 */
	public RegistroServizi(AccessoRegistro accessoRegistro,Logger alog,
			Logger alogConsole,boolean raggiungibilitaTotale, boolean readObjectStatoBozza, 
			String jndiNameDatasourcePdD, boolean useOp2UtilsDatasource, boolean bindJMX)throws DriverRegistroServiziException{

		try{ 
			this.driverRegistroServizi = new java.util.Hashtable<String,IDriverRegistroServiziGet>();
			this.registriXML = new Vector<DriverRegistroServiziXML>();

			if(alog!=null)
				this.log = alog;
			else
				this.log = LoggerWrapperFactory.getLogger(RegistroServizi.class);

			this.raggiungibilitaTotale = raggiungibilitaTotale;

			this.readObjectStatoBozza = readObjectStatoBozza;
			
			for(int i=0; i<accessoRegistro.sizeRegistroList(); i++){	

				IDriverRegistroServiziGet driver = null;
				AccessoRegistroRegistro registro = accessoRegistro.getRegistro(i);
				String nomeRegistro = registro.getNome();
				if(nomeRegistro == null)
					nomeRegistro = "Registro"+i+registro.getTipo();


				String path = registro.getLocation();
				if(CostantiConfigurazione.REGISTRO_XML.equals(registro.getTipo())){
					//if( (path.startsWith("http://")==false) && (path.startsWith("file://")==false) ){
					if(path.indexOf("${")!=-1){
						while (path.indexOf("${")!=-1){
							int indexStart = path.indexOf("${");
							int indexEnd = path.indexOf("}");
							if(indexEnd==-1){
								this.log.error("errore durante l'interpretazione del path ["+path+"]: ${ utilizzato senza la rispettiva chiusura }");
								continue;
							}
							String nameSystemProperty = path.substring(indexStart+"${".length(),indexEnd);
							String valueSystemProperty = System.getProperty(nameSystemProperty);
							if(valueSystemProperty==null){
								this.log.error("errore durante l'interpretazione del path ["+path+"]: variabile di sistema ${"+nameSystemProperty+"} non esistente");
								continue;
							}
							path = path.replace("${"+nameSystemProperty+"}", valueSystemProperty);
						}
					}
					//}
				}


				String msg = "carico registro "+nomeRegistro+" di tipo["+registro.getTipo()+"]   location["+path+"]";
				this.log.info(msg);
				if(alogConsole!=null)
					alogConsole.info(msg);


				// inizializzazione XML
				if(CostantiConfigurazione.REGISTRO_XML.equals(registro.getTipo())){

					driver = new DriverRegistroServiziXML(path,this.log);
					if( ((DriverRegistroServiziXML)driver).create ){
						this.driverRegistroServizi.put(nomeRegistro,driver);
						this.registriXML.add((DriverRegistroServiziXML)driver);
					}else{
						msg ="Riscontrato errore durante l'inizializzazione del registro di tipo "+
						registro.getTipo()+" con location: "+registro.getLocation();
						this.log.error(msg);
						if(alogConsole!=null)
							alogConsole.info(msg);
					}
				} 

				// inizializzazione UDDI
				else if(CostantiConfigurazione.REGISTRO_UDDI.equals(registro.getTipo())){
					if( (registro.getUser()!=null) && (registro.getPassword()!=null))
						driver = new DriverRegistroServiziUDDI(path,
								registro.getUser(),
								registro.getPassword(),this.log);
					else
						driver = new DriverRegistroServiziUDDI(registro.getLocation(),this.log);
					if( ((DriverRegistroServiziUDDI)driver).create ){
						this.driverRegistroServizi.put(nomeRegistro,driver);
					}else{
						msg = "Riscontrato errore durante l'inizializzazione del registro di tipo "+
						registro.getTipo()+" con location: "+registro.getLocation();
						this.log.error(msg);
						if(alogConsole!=null)
							alogConsole.info(msg);
					}
				}

				// inizializzazione WEB
				else if(CostantiConfigurazione.REGISTRO_WEB.equals(registro.getTipo())){
					driver = new DriverRegistroServiziWEB(path,this.log);
					if( ((DriverRegistroServiziWEB)driver).create ){
						this.driverRegistroServizi.put(nomeRegistro,driver);
					}else{
						msg ="Riscontrato errore durante l'inizializzazione del registro di tipo "+
						registro.getTipo()+" con location: "+registro.getLocation();
						this.log.error(msg);
						if(alogConsole!=null)
							alogConsole.info(msg);
					}
				}

				// inizializzazione DB
				else if(CostantiConfigurazione.REGISTRO_DB.equals(registro.getTipo())){
					driver = new DriverRegistroServiziDB(path,null,this.log,registro.getTipoDatabase(),
							useOp2UtilsDatasource,bindJMX);
					this.mappingNomeRegistroToTipiDatabase.put(nomeRegistro, registro.getTipoDatabase());
					this.mappingNomeRegistroToUseConnectionPdD.put(nomeRegistro, path.equals(jndiNameDatasourcePdD));
					if( ((DriverRegistroServiziDB)driver).create ){
						this.driverRegistroServizi.put(nomeRegistro,driver);
					}else{
						msg ="Riscontrato errore durante l'inizializzazione del registro di tipo "+
						registro.getTipo()+" con location: "+registro.getLocation();
						this.log.error(msg);
						if(alogConsole!=null)
							alogConsole.info(msg);
					}
				}

				// inizializzazione WS
				else if(CostantiConfigurazione.REGISTRO_WS.equals(registro.getTipo())){
					if( (registro.getUser()!=null) && (registro.getPassword()!=null))
						driver = new DriverRegistroServiziWS(path,
								registro.getUser(),
								registro.getPassword(),this.log);
					else
						driver = new DriverRegistroServiziWS(registro.getLocation(),this.log);
					if( ((DriverRegistroServiziWS)driver).create ){
						this.driverRegistroServizi.put(nomeRegistro,driver);
					}else{
						msg ="Riscontrato errore durante l'inizializzazione del registro di tipo "+
						registro.getTipo()+" con location: "+registro.getLocation();
						this.log.error(msg);
						if(alogConsole!=null)
							alogConsole.info(msg);
					}
				}

				// tipo di registro non conosciuto
				else{
					msg = "Riscontrato errore durante l'inizializzazione del registro di tipo sconosciuto "+
					registro.getTipo()+" con location: "+registro.getLocation();
					this.log.error(msg);
					if(alogConsole!=null)
						alogConsole.info(msg);
				}
			}


			// Inizializzazione della Cache
			if(accessoRegistro.getCache()!=null){
				initCacheRegistriServizi(accessoRegistro.getCache(),alogConsole);
			}

		}catch(Exception e){
			String msg = "Riscontrato errore durante l'inizializzazione del registro: "+e.getMessage();
			this.log.error(msg);
			if(alogConsole!=null)
				alogConsole.info(msg);
			throw new DriverRegistroServiziException("Riscontrato errore durante l'inizializzazione del registro: "+e.getMessage());
		}

		if( this.driverRegistroServizi.size() == 0 )
			throw new DriverRegistroServiziException("Non e' stato possibile inizializzare nessuna sorgente di lettura per un Registro dei Servizi");
	}




	private void initCacheRegistriServizi(org.openspcoop2.core.config.Cache configurazioneCache,Logger alogConsole) throws Exception{
		this.cache = new Cache(CostantiRegistroServizi.CACHE_REGISTRO_SERVIZI);

		if( (configurazioneCache.getDimensione()!=null) ||
				(configurazioneCache.getAlgoritmo() != null) ){

			if( configurazioneCache.getDimensione()!=null ){
				int dimensione = -1;
				try{
					dimensione = Integer.parseInt(configurazioneCache.getDimensione());
					String msg = "Dimensione della cache (RegistroServizi) impostata al valore: "+dimensione;
					this.log.info(msg);
					if(alogConsole!=null)
						alogConsole.info(msg);
					this.cache.setCacheSize(dimensione);
				}catch(Exception error){
					String msg = "Parametro errato per la dimensione della cache (RegistroServizi): "+error.getMessage();
					this.log.error(msg);
					if(alogConsole!=null)
						alogConsole.info(msg);
				}

			}
			if( configurazioneCache.getAlgoritmo() != null ){
				String msg = "Algoritmo di cache (RegistroServizi) impostato al valore: "+configurazioneCache.getAlgoritmo();
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
					String msg = "Attributo 'IdleTime' (RegistroServizi) impostato al valore: "+itemIdleTime;
					this.log.info(msg);
					if(alogConsole!=null)
						alogConsole.info(msg);
					this.cache.setItemIdleTime(itemIdleTime);
				}catch(Exception error){
					String msg = "Parametro errato per l'attributo 'IdleTime' (RegistroServizi): "+error.getMessage();
					this.log.error(msg);
					if(alogConsole!=null)
						alogConsole.info(msg);
				}
			}
			if( configurazioneCache.getItemLifeSecond() != null  ){
				int itemLifeSecond = -1;
				try{
					itemLifeSecond = Integer.parseInt(configurazioneCache.getItemLifeSecond());
					String msg = "Attributo 'MaxLifeSecond' (RegistroServizi) impostato al valore: "+itemLifeSecond;
					this.log.info(msg);
					if(alogConsole!=null)
						alogConsole.info(msg);
					this.cache.setItemLifeTime(itemLifeSecond);
				}catch(Exception error){
					String msg = "Parametro errato per l'attributo 'MaxLifeSecond' (RegistroServizi): "+error.getMessage();
					this.log.error(msg);
					if(alogConsole!=null)
						alogConsole.info(msg);
				}
			}

		}
	}




	protected java.util.Hashtable<String, IDriverRegistroServiziGet> getDriverRegistroServizi() {
		return this.driverRegistroServizi;
	}






	/**
	 * Si occupa di effettuare una ricerca nei registri, e di inserire la ricerca in cache
	 *
	 * @param keyCache Chiave di ricerca in cache
	 * @param methodName Nome del metodo da invocare
	 * @param nomeRegistro Nome del registro su cui effettuare la ricerca (null per tutti i registri)
	 * @param arguments Argomenti da passare al metodo
	 * @return l'oggetto se trovato, null altrimenti.
	 * 
	 */
	public synchronized Object getObjectCache(String keyCache,String methodName,
			String nomeRegistro,
			Boolean readContenutoAllegati,
			Connection connectionPdD,
			Object... arguments) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		DriverRegistroServiziNotFound dNotFound = null;
		Object obj = null;
		try{

//			System.out.println("@"+keyCache+"@ INFO CACHE: "+this.cache.toString());
//			System.out.println("@"+keyCache+"@ KEYS: \n\t"+this.cache.printKeys("\n\t"));
			
			// Raccolta dati
			if(keyCache == null)
				throw new DriverRegistroServiziException("["+methodName+"]: KeyCache non definita");	

			// se e' attiva una cache provo ad utilizzarla
			if(this.cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") nel registro["+nomeRegistro+"] in cache.");
						return response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") nel registro["+nomeRegistro+"] in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.log.debug("oggetto con chiave ["+keyCache+"] (methodo:"+methodName+") nel registro["+nomeRegistro+"] non in cache, ricerco nel registro...");
			try{
				obj = getObject(methodName,nomeRegistro,readContenutoAllegati,connectionPdD,arguments);
			}catch(DriverRegistroServiziNotFound e){
				dNotFound = e;
			}

			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				if(dNotFound!=null){
					this.log.info("Aggiungo eccezione ["+keyCache+"] in cache");
				}else if(obj!=null){
					this.log.info("Aggiungo oggetto ["+keyCache+"] in cache");
				}else{
					throw new Exception("Metodo ("+methodName+") nel registro["+nomeRegistro+"] ha ritornato un valore null");
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

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}
		catch(Exception e){
			if(DriverRegistroServiziNotFound.class.getName().equals(e.getClass().getName()))
				throw (DriverRegistroServiziNotFound) e;
			else
				throw new DriverRegistroServiziException("RegistroServizi, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}

		if(dNotFound!=null){
			throw dNotFound;
		}else
			return obj;
	}


	/**
	 * Si occupa di effettuare una ricerca nei registri
	 *
	 * @param methodName Nome del metodo da invocare
	 * @param nomeRegistro Nome del registro su cui effettuare la ricerca (null per tutti i registri)
	 * @param arguments Argomenti da passare al metodo
	 * @return l'oggetto se trovato, null altrimenti.
	 * 
	 */
	public Object getObject(String methodName,
			String nomeRegistro,
			Boolean readContenutoAllegati,
			Connection connectionPdD,
			Object... arguments) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{


		// Effettuo le query nella mia gerarchia di registri.
		Object obj = null;
		//Recupero la classe specificata dal parametro passato
		if(nomeRegistro!=null){
			this.log.debug("Cerco nel registro ["+nomeRegistro+"]");
			try{
				//org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.driverRegistroServizi.get(nomeRegistro);
				org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.getDriver(connectionPdD, nomeRegistro);
				this.log.debug("invocazione metodo ["+methodName+"]...");
				if(arguments.length==0){
					Method method =  null;
					if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
						method = driver.getClass().getMethod(methodName,boolean.class);
						obj = method.invoke(driver,readContenutoAllegati.booleanValue());
					}else{
						method = driver.getClass().getMethod(methodName);
						obj = method.invoke(driver);
					}
				}else if(arguments.length==1){
					Method method =  null;
					if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
						method = driver.getClass().getMethod(methodName,arguments[0].getClass(),boolean.class);
						obj = method.invoke(driver,arguments[0],readContenutoAllegati.booleanValue());
					}else{
						method = driver.getClass().getMethod(methodName,arguments[0].getClass());
						obj = method.invoke(driver,arguments[0]);
					}
				}else if(arguments.length==2){
					Method method =  null;
					if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
						method = driver.getClass().getMethod(methodName,arguments[0].getClass(),arguments[1].getClass(),boolean.class);
						obj = method.invoke(driver,arguments[0],arguments[1],readContenutoAllegati.booleanValue());
					}else{
						method = driver.getClass().getMethod(methodName,arguments[0].getClass(),arguments[1].getClass());
						obj = method.invoke(driver,arguments[0],arguments[1]);
					}
				}else
					throw new Exception("Troppi argomenti per gestire la chiamata del metodo");
				
				// Check stato
				if( (!this.readObjectStatoBozza) && (driver instanceof DriverRegistroServiziDB) ){
					if(obj instanceof AccordoCooperazione){
						AccordoCooperazione ac = (AccordoCooperazione) obj;
						if(StatiAccordo.bozza.toString().equals(ac.getStatoPackage())){
							obj = null;
							throw new DriverRegistroServiziNotFound("Accordo presente in uno stato bozza");
						}
					}else if(obj instanceof AccordoServizioParteComune){
						AccordoServizioParteComune as = (AccordoServizioParteComune) obj;
						if(StatiAccordo.bozza.toString().equals(as.getStatoPackage())){
							obj = null;
							throw new DriverRegistroServiziNotFound("Accordo presente in uno stato bozza");
						}
					}else if(obj instanceof AccordoServizioParteSpecifica){
						AccordoServizioParteSpecifica s = (AccordoServizioParteSpecifica) obj;
						if(StatiAccordo.bozza.toString().equals(s.getStatoPackage())){
							obj = null;
							throw new DriverRegistroServiziNotFound("Accordo presente in uno stato bozza");
						}else{
							// Check fruitori
							Vector<Fruitore> fruitoriConStatoNonBozza = new Vector<Fruitore>();
							while(s.sizeFruitoreList()>0){
								Fruitore tmpF = s.removeFruitore(0);
								if(StatiAccordo.bozza.toString().equals(tmpF.getStatoPackage())==false){
									fruitoriConStatoNonBozza.add(tmpF);
								}
							}
							while(fruitoriConStatoNonBozza.size()>0){
								s.addFruitore(fruitoriConStatoNonBozza.remove(0));
							}
						}
					}
				}
				
			}catch(DriverRegistroServiziNotFound e){
				// Non presente
				this.log.debug("Ricerca nel registro non riuscita (metodo ["+methodName+"]): "+e.getMessage());
				throw e;
			}
			catch(java.lang.reflect.InvocationTargetException e){
				if(e.getTargetException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(e.getTargetException().getClass().getName())){
						// Non presente
						this.log.debug("Ricerca nel registro ["+nomeRegistro+"] non riuscita [NotFound] (metodo ["+methodName+"]): "+e.getTargetException().getMessage());
						throw new DriverRegistroServiziNotFound(e.getTargetException().getMessage(),e.getTargetException());
					}else if(org.openspcoop2.core.registry.driver.DriverRegistroServiziException.class.getName().equals(e.getTargetException().getClass().getName())){
						// Non presente
						this.log.debug("Ricerca nel registro ["+nomeRegistro+"] non riuscita [DriverException] (metodo ["+methodName+"]): "+e.getTargetException().getMessage(),e.getTargetException());
						throw new DriverRegistroServiziException(e.getTargetException().getMessage(),e.getTargetException());
					}else{
						this.log.debug("Ricerca nel registro ["+nomeRegistro+"] non riuscita [InvTargetExcp.getTarget] (metodo ["+methodName+"]), "+e.getTargetException().getMessage(),e.getTargetException());
						throw new DriverRegistroServiziException(e.getTargetException().getMessage(),e.getTargetException());
					}
				}else{
					this.log.debug("Ricerca nel registro ["+nomeRegistro+"] non riuscita [InvTargetExcp] (metodo ["+methodName+"]), "+e.getMessage(),e);
					throw new DriverRegistroServiziException(e.getMessage(),e);
				}
			}
			catch(Exception e){
				// Non presente
				this.log.debug("Ricerca nel registro ["+nomeRegistro+"] non riuscita, "+e.getMessage(),e);
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}

			this.log.debug("invocazione metodo ["+methodName+"] completata.");
			return obj;

		}else{
			this.log.debug("Cerco nella mia gerarchia di registri");
			StringBuffer notFoundProblem = new StringBuffer();
			StringBuffer exceptionProblem = new StringBuffer();
			boolean find = false;
			for (Enumeration<?> en = this.driverRegistroServizi.keys() ; en.hasMoreElements() ;) {
				String nomeRegInLista= (String) en.nextElement();
				this.log.debug("Cerco nel registro con nome["+nomeRegInLista+"]");
				try{
					//org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.driverRegistroServizi.get(nomeRegInLista);
					org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.getDriver(connectionPdD, nomeRegInLista);
					this.log.debug("invocazione metodo ["+methodName+"] nel registro["+nomeRegInLista+"]...");
					if(arguments.length==0){
						Method method =  null;
						if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
							method = driver.getClass().getMethod(methodName,boolean.class);
							obj = method.invoke(driver,readContenutoAllegati.booleanValue());
						}else{
							method = driver.getClass().getMethod(methodName);
							obj = method.invoke(driver);
						}
						find = true;
					}else if(arguments.length==1){
						Method method =  null;
						if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
							method = driver.getClass().getMethod(methodName,arguments[0].getClass(),boolean.class);
							obj = method.invoke(driver,arguments[0],readContenutoAllegati.booleanValue());
						}else{
							method = driver.getClass().getMethod(methodName,arguments[0].getClass());
							obj = method.invoke(driver,arguments[0]);
						}
						find = true;
					}else if(arguments.length==2){
						Method method =  null;
						if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
							method = driver.getClass().getMethod(methodName,arguments[0].getClass(),arguments[1].getClass(),boolean.class);
							obj = method.invoke(driver,arguments[0],arguments[1],readContenutoAllegati.booleanValue());
						}else{
							method = driver.getClass().getMethod(methodName,arguments[0].getClass(),arguments[1].getClass());
							obj = method.invoke(driver,arguments[0],arguments[1]);
						}
						find = true;
					}else
						throw new Exception("Troppi argomenti per gestire la chiamata del metodo");
					
					// Check stato
					if((!this.readObjectStatoBozza) && (driver instanceof DriverRegistroServiziDB) ){
						if(obj instanceof AccordoCooperazione){
							AccordoCooperazione ac = (AccordoCooperazione) obj;
							if(StatiAccordo.bozza.toString().equals(ac.getStatoPackage())){
								obj = null;
								find = false;
								throw new DriverRegistroServiziNotFound("Accordo presente in uno stato bozza");
							}
						}else if(obj instanceof AccordoServizioParteComune){
							AccordoServizioParteComune as = (AccordoServizioParteComune) obj;
							if(StatiAccordo.bozza.toString().equals(as.getStatoPackage())){
								obj = null;
								find = false;
								throw new DriverRegistroServiziNotFound("Accordo presente in uno stato bozza");
							}
						}else if(obj instanceof AccordoServizioParteSpecifica){
							AccordoServizioParteSpecifica s = (AccordoServizioParteSpecifica) obj;
							if(StatiAccordo.bozza.toString().equals(s.getStatoPackage())){
								obj = null;
								find = false;
								throw new DriverRegistroServiziNotFound("Accordo presente in uno stato bozza");
							}else{
								// Check fruitori
								Vector<Fruitore> fruitoriConStatoNonBozza = new Vector<Fruitore>();
								while(s.sizeFruitoreList()>0){
									Fruitore tmpF = s.removeFruitore(0);
									if(StatiAccordo.bozza.toString().equals(tmpF.getStatoPackage())==false){
										fruitoriConStatoNonBozza.add(tmpF);
									}
								}
								while(fruitoriConStatoNonBozza.size()>0){
									s.addFruitore(fruitoriConStatoNonBozza.remove(0));
								}
							}
						}
					}
					
				}catch(DriverRegistroServiziNotFound ed){
					// Non presente
					this.log.debug("Ricerca nel registro ["+nomeRegInLista+"] non riuscita (metodo ["+methodName+"]): "+ed.getMessage());
					notFoundProblem.append("\nRegistro["+nomeRegInLista+"], ricerca fallita: "+ed.getMessage());
				}
				catch(java.lang.reflect.InvocationTargetException e){
					if(e.getTargetException()!=null){
						if(DriverRegistroServiziNotFound.class.getName().equals(e.getTargetException().getClass().getName())){
							// Non presente
							this.log.debug("Ricerca nel registro ["+nomeRegInLista+"] non riuscita [NotFound] (metodo ["+methodName+"]): "+e.getTargetException().getMessage());
							notFoundProblem.append("\nRegistro["+nomeRegInLista+"], ricerca fallita: "+e.getTargetException().getMessage());
						}else if(org.openspcoop2.core.registry.driver.DriverRegistroServiziException.class.getName().equals(e.getTargetException().getClass().getName())){
							// Non presente
							this.log.debug("Ricerca nel registro ["+nomeRegInLista+"] non riuscita [DriverException] (metodo ["+methodName+"]): "+e.getTargetException().getMessage(),e.getTargetException());
							if(this.raggiungibilitaTotale)
								throw new DriverRegistroServiziException(e.getTargetException().getMessage(),e.getTargetException());
							else
								exceptionProblem.append("\nRegistro["+nomeRegInLista+"], accesso non riuscito: "+e.getTargetException().getMessage());
						}else{
							this.log.debug("Ricerca nel registro ["+nomeRegInLista+"] non riuscita [InvTargetExcp.getTarget] (metodo ["+methodName+"]), "+e.getTargetException().getMessage(),e.getTargetException());
							if(this.raggiungibilitaTotale)
								throw new DriverRegistroServiziException(e.getTargetException().getMessage(),e.getTargetException());
							else
								exceptionProblem.append("\nRegistro["+nomeRegInLista+"], accesso non riuscito: "+e.getTargetException().getMessage());
						}
					}else{
						this.log.debug("Ricerca nel registro ["+nomeRegInLista+"] non riuscita [InvTargetExcp] (metodo ["+methodName+"]), "+e.getMessage(),e);
						if(this.raggiungibilitaTotale)
							throw new DriverRegistroServiziException(e.getMessage(),e);
						else
							exceptionProblem.append("\nRegistro["+nomeRegInLista+"], accesso non riuscito: "+e.getMessage());
					}
				}catch(Exception e){
					// Non presente
					this.log.debug("Ricerca nel registro ["+nomeRegInLista+"] non riuscita, "+e.getMessage(),e);
					if(this.raggiungibilitaTotale)
						throw new DriverRegistroServiziException(e.getMessage(),e);
					else
						exceptionProblem.append("\nRegistro["+nomeRegInLista+"], accesso non riuscito: "+e.getMessage());
				}
				if(find)
					break;
			}

			this.log.debug("invocazione metodo ["+methodName+"] completata.");
			if(find){
				return obj;
			}else if(exceptionProblem.length()>0 && notFoundProblem.length()>0){
				throw new DriverRegistroServiziException("["+methodName+"]:"+exceptionProblem.toString()+notFoundProblem.toString());
			}else if(exceptionProblem.length()>0){
				throw new DriverRegistroServiziException("["+methodName+"]:"+exceptionProblem.toString());
			}else if(notFoundProblem.length()>0){
				throw new DriverRegistroServiziNotFound("["+methodName+"]:"+notFoundProblem.toString());
			}else{
				throw new DriverRegistroServiziException("["+methodName+"]: ricerca non riuscita in tutti i registri");
			}
		}


	}


	private org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet getDriver(Connection connectionPdD,String nomeRegistro) throws DriverRegistroServiziException{
		
		org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.driverRegistroServizi.get(nomeRegistro);
		if((driver instanceof DriverRegistroServiziDB)){
			Boolean useConnectionPdD = this.mappingNomeRegistroToUseConnectionPdD.get(nomeRegistro);
			if(connectionPdD!=null && useConnectionPdD){
				//System.out.println("[REGISTRY] USE CONNECTION VERSIONE!!!!!");
				driver = new DriverRegistroServiziDB(connectionPdD, this.log, this.mappingNomeRegistroToTipiDatabase.get(nomeRegistro));
				if( ((DriverRegistroServiziDB)driver).create == false){
					throw new DriverRegistroServiziException("Inizializzazione DriverRegistroServizi("+nomeRegistro+") con connessione PdD non riuscita");
				}
			}
			else{
				//System.out.println("[REGISTRY] USE DATASOURCE VERSIONE!!!!!");
			}
		}
		
		return driver;
	}














	/* ********  R I C E R C A    S E R V I Z I  (utilizzo dei driver) ******** */ 

	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteComune}, 
	 * identificato grazie al parametro 
	 * <var>idAccordo</var> 
	 *
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca
	 * @param idAccordo ID dell'accordo di Servizio
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteComune} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 * 
	 */
	public AccordoServizioParteComune getAccordoServizioParteComune(Connection connectionPdD,String nomeRegistro,IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getAccordoServizioParteComune(connectionPdD, nomeRegistro, idAccordo, null);
	}
	public AccordoServizioParteComune getAccordoServizioParteComune(Connection connectionPdD,String nomeRegistro,IDAccordo idAccordo,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune]: Parametro non definito");	
		if(idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune]: Nome accordo di servizio non definito");	
		
		String uriAccordoServizio = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getAccordoServizio_" + uriAccordoServizio;
			if(readContenutiAllegati!=null){
				key = key + "_READ-ALLEGATI("+readContenutiAllegati.booleanValue()+")";
			}
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((AccordoServizioParteComune) response.getObject());
				}
			}
		}


		// Algoritmo CACHE
		AccordoServizioParteComune as = null;
		if(this.cache!=null){
			as = (AccordoServizioParteComune) this.getObjectCache(key,"getAccordoServizioParteComune",nomeRegistro,readContenutiAllegati,connectionPdD,idAccordo);
		}else{
			as = (AccordoServizioParteComune) this.getObject("getAccordoServizioParteComune",nomeRegistro,readContenutiAllegati,connectionPdD,idAccordo);
		}

		if(as!=null)
			return as;
		else
			throw new DriverRegistroServiziNotFound("[getAccordoServizio] Accordo di Servizio ["+idAccordo+"] non Trovato");



	}

	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.PortaDominio}, 
	 * identificato grazie al parametro 
	 * <var>nomePdD</var> 
	 *
	 * @param nomePdD Nome della Porta di Dominio
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.PortaDominio}.
	 * 
	 */
	public org.openspcoop2.core.registry.PortaDominio getPortaDominio(Connection connectionPdD,String nomeRegistro,String nomePdD) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		// Raccolta dati
		if(nomePdD == null)
			throw new DriverRegistroServiziException("[getPortaDominio]: Parametro non definito");	

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getPortaDominio" + nomePdD;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((PortaDominio) response.getObject());
				}
			}
		}


		// Algoritmo CACHE
		PortaDominio pd = null;
		if(this.cache!=null){
			pd = (PortaDominio) this.getObjectCache(key,"getPortaDominio",nomeRegistro,null,connectionPdD,nomePdD);
		}else{
			pd = (PortaDominio) this.getObject("getPortaDominio",nomeRegistro,null,connectionPdD,nomePdD);
		}

		if(pd!=null)
			return pd;
		else
			throw new DriverRegistroServiziNotFound("[getPortaDominio] Porta di dominio ["+nomePdD+"] non Trovata");



	}
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.Soggetto}, 
	 * identificato grazie al parametro 
	 * <var>idSoggetto</var> di tipo {@link org.openspcoop2.core.id.IDSoggetto}. 
	 *
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.Soggetto} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 * 
	 */
	public Soggetto getSoggetto(Connection connectionPdD,String nomeRegistro,IDSoggetto idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idSoggetto==null)
			throw new DriverRegistroServiziException("[getSoggetto] Parametro Non Valido");
		String tipoSP = idSoggetto.getTipo();
		String codiceSP = idSoggetto.getNome();
		if(tipoSP == null || codiceSP == null)
			throw new DriverRegistroServiziException("[getSoggetto] Parametri Non Validi");

		//	se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getSoggetto_" + tipoSP +"/" + codiceSP;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((Soggetto) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		Soggetto soggetto = null;
		if(this.cache!=null){
			soggetto = (Soggetto) this.getObjectCache(key,"getSoggetto",nomeRegistro,null,connectionPdD,idSoggetto);
		}else{
			soggetto = (Soggetto) this.getObject("getSoggetto",nomeRegistro,null,connectionPdD,idSoggetto);
		}

		if(soggetto!=null)
			return soggetto;
		else
			throw new DriverRegistroServiziNotFound("[getSoggetto] Soggetto non Trovato");

	}

	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * contenente le informazioni sulle funzionalita' associate
	 * al servizio  identificato grazie ai fields Soggetto,
	 * 'Servizio','TipoServizio' e 'Azione' impostati
	 * all'interno del parametro <var>idService</var> di tipo {@link org.openspcoop2.core.id.IDServizio}. 
	 *
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 * 
	 */
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(Connection connectionPdD,String nomeRegistro,IDServizio idService) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getAccordoServizioParteSpecifica(connectionPdD,nomeRegistro, idService, null);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(Connection connectionPdD,String nomeRegistro,IDServizio idService,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idService == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametro Non Valido");
		String servizio = idService.getServizio();
		String tipoServizio = idService.getTipoServizio();
		String azione = idService.getAzione();
		if(servizio == null || tipoServizio == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametri (Servizio) Non Validi");
		String tipoSogg = idService.getSoggettoErogatore().getTipo();
		String nomeSogg = idService.getSoggettoErogatore().getNome();
		if(tipoSogg == null || nomeSogg == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametri (Soggetto) Non Validi");

		//	se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getServizio_"+ tipoSogg +"/" + nomeSogg +
			"_" + tipoServizio + "/" + servizio;
			if(azione !=null)
				key = key + "_" + azione;	   
			if(readContenutiAllegati!=null){
				key = key + "_READ-ALLEGATI("+readContenutiAllegati.booleanValue()+")";
			}
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((AccordoServizioParteSpecifica) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		AccordoServizioParteSpecifica serv = null;
		if(this.cache!=null){
			serv = (AccordoServizioParteSpecifica) this.getObjectCache(key,"getAccordoServizioParteSpecifica",nomeRegistro,readContenutiAllegati,connectionPdD,idService);
		}else{
			serv = (AccordoServizioParteSpecifica) this.getObject("getAccordoServizioParteSpecifica",nomeRegistro,readContenutiAllegati,connectionPdD,idService);
		}

		if(serv!=null)
			return serv;
		else
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica] Servizio non Trovato");
	}

	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * contenente le informazioni sulle funzionalita' associate
	 * al servizio  correlato identificato grazie ai fields Soggetto
	 * e nomeAccordo
	 * 
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @param idAccordo ID dell'accordo che deve implementare il servizio correlato
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 * 
	 */
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(Connection connectionPdD,String nomeRegistro,IDSoggetto idSoggetto, IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getAccordoServizioParteSpecifica_ServizioCorrelato(connectionPdD,nomeRegistro, idSoggetto, idAccordo,null);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(Connection connectionPdD,String nomeRegistro,
			IDSoggetto idSoggetto, IDAccordo idAccordo,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idSoggetto == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Parametro Non Valido");
		String nome = idSoggetto.getNome();
		String tipo = idSoggetto.getTipo();
		if(tipo == null || nome == null || idAccordo==null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Parametri Non Validi");

		String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
		
		//	se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getServizioCorrelato(RicercaPerAccordo)_"+ tipo +"/" + nome +"_" + uriAccordo;
			if(readContenutiAllegati!=null){
				key = key + "_READ-ALLEGATI("+readContenutiAllegati.booleanValue()+")";
			}
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((AccordoServizioParteSpecifica) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		AccordoServizioParteSpecifica servCorrelato = null;
		if(this.cache!=null){
			servCorrelato = (AccordoServizioParteSpecifica) this.getObjectCache(key,"getAccordoServizioParteSpecifica_ServizioCorrelato",nomeRegistro,readContenutiAllegati,connectionPdD,idSoggetto,idAccordo);
		}else{
			servCorrelato = (AccordoServizioParteSpecifica) this.getObject("getAccordoServizioParteSpecifica_ServizioCorrelato",nomeRegistro,readContenutiAllegati,connectionPdD,idSoggetto,idAccordo);
		}

		if(servCorrelato!=null)
			return servCorrelato;
		else
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] ServizioCorrelato non Trovato");
	}
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoCooperazione}
	 * contenente le informazioni sulle funzionalita' associate
	 * 
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca
	 * @param idAccordo Identificatore dell'Accordo di tipo {@link org.openspcoop2.core.id.IDAccordoCooperazione}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoCooperazione} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 * 
	 */
	public AccordoCooperazione getAccordoCooperazione(Connection connectionPdD,String nomeRegistro,IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getAccordoCooperazione(connectionPdD,nomeRegistro, idAccordo, null);
	}
	public AccordoCooperazione getAccordoCooperazione(Connection connectionPdD,String nomeRegistro,IDAccordoCooperazione idAccordo,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione]: Parametro non definito");	
		if(idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione]: Nome accordo di servizio non definito");	
		
		String uriAccordoCooperazione = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo);

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getAccordoCooperazione_" + uriAccordoCooperazione;   
			if(readContenutiAllegati!=null){
				key = key + "_READ-ALLEGATI("+readContenutiAllegati.booleanValue()+")";
			}
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((AccordoCooperazione) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		AccordoCooperazione ac = null;
		if(this.cache!=null){
			ac = (AccordoCooperazione) this.getObjectCache(key,"getAccordoCooperazione",nomeRegistro,readContenutiAllegati,connectionPdD,idAccordo);
		}else{
			ac = (AccordoCooperazione) this.getObject("getAccordoCooperazione",nomeRegistro,readContenutiAllegati,connectionPdD,idAccordo);
		}

		if(ac!=null)
			return ac;
		else
			throw new DriverRegistroServiziNotFound("[getAccordoCooperazione] Servizio non Trovato");
	}


	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.RegistroServizi}, 
	 * identificato grazie al parametro 
	 * <var>idSoggetto</var> di tipo {@link org.openspcoop2.core.id.IDSoggetto}. 
	 * Il connettore viene ricercato come definizione esterna, al soggetto (xml nel registro direttamente)
	 *
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.RegistroServizi} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 * 
	 */
	public org.openspcoop2.core.registry.RegistroServizi[] getRegistriServiziXML(){
		if(this.registriXML.size()>0){
			org.openspcoop2.core.registry.RegistroServizi[] r = new org.openspcoop2.core.registry.RegistroServizi[this.registriXML.size()];
			for(int i=0; i<this.registriXML.size(); i++){
				r[i] = this.registriXML.get(i).getRegistroXML();
			}
			return r;
		}else{
			return null;
		}
	}











	/**
	 * Si occupa di ritornare le informazioni sui wsdl di un servizio
	 * 
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 */
	public org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getWsdlAccordoServizio(Connection connectionPdD,String nomeRegistro,
			IDServizio idService,InformationWsdlSource infoWsdlSource,boolean buildSchemaXSD)
	throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idService == null)
			throw new DriverRegistroServiziException("[getWsdlAccordoServizio] Parametro Non Valido");
		String servizio = idService.getServizio();
		String tipoServizio = idService.getTipoServizio();
		if(servizio == null || tipoServizio == null)
			throw new DriverRegistroServiziException("[getWsdlAccordoServizio] Parametri (Servizio) Non Validi");
		String tipoSogg = idService.getSoggettoErogatore().getTipo();
		String nomeSogg = idService.getSoggettoErogatore().getNome();
		if(tipoSogg == null || nomeSogg == null)
			throw new DriverRegistroServiziException("[getWsdlAccordoServizio] Parametri (Soggetto) Non Validi");

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getWsdlAccordoServizio_"+infoWsdlSource.name()+"_"+ tipoSogg +"/" + nomeSogg +
			"_" + tipoServizio + "/" + servizio
			+"_schema_"+buildSchemaXSD;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper wsdlAS = null;
		if(this.cache!=null){
			wsdlAS = this.getAccordoServizioCache(key, idService, infoWsdlSource, nomeRegistro, connectionPdD,buildSchemaXSD);
		}else{
			wsdlAS = this.getAccordoServizioEngine(idService, infoWsdlSource, nomeRegistro, connectionPdD,buildSchemaXSD);
		}

		if(wsdlAS!=null)
			return wsdlAS;
		else
			throw new DriverRegistroServiziNotFound("[getWsdlAccordoServizio] WSDLAccordoServizio non Trovato");
	}



	/**
	 * Si occupa di effettuare una ricerca nei registri, e di inserire la ricerca in cache
	 *
	 * @param keyCache Chiave di ricerca in cache
	 * @param idService ID del Servizio
	 * @param nomeRegistro Nome del registro su cui effettuare la ricerca (null per tutti i registri)
	 * @return l'oggetto se trovato, null altrimenti.
	 * 
	 */
	private synchronized org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getAccordoServizioCache(String keyCache,IDServizio idService,
			InformationWsdlSource infoWsdlSource,String nomeRegistro,Connection connectionPdD,boolean buildSchemaXSD) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		DriverRegistroServiziNotFound dNotFound = null;
		org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper obj = null;
		try{

//			System.out.println("@"+keyCache+"@ INFO CACHE: "+this.cache.toString());
//			System.out.println("@"+keyCache+"@ KEYS: \n\t"+this.cache.printKeys("\n\t"));
			
			// Raccolta dati
			if(keyCache == null)
				throw new DriverRegistroServiziException("[getWSDLAccordoServizio]: KeyCache non definita");	

			// se e' attiva una cache provo ad utilizzarla
			if(this.cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (methodo:getWSDLAccordoServizio) nel registro["+nomeRegistro+"] in cache.");
						return (org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper) response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (methodo:getWSDLAccordoServizio) nel registro["+nomeRegistro+"] in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.log.debug("oggetto con chiave ["+keyCache+"] (methodo:WSDLAccordoServizio) nel registro["+nomeRegistro+"] non in cache, ricerco nel registro...");
			try{
				obj = getAccordoServizioEngine(idService, infoWsdlSource,nomeRegistro,connectionPdD,buildSchemaXSD);
			}catch(DriverRegistroServiziNotFound e){
				dNotFound = e;
			}

			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				if(dNotFound!=null){
					this.log.info("Aggiungo eccezione ["+keyCache+"] in cache");
				}else if(obj!=null){
					this.log.info("Aggiungo oggetto ["+keyCache+"] in cache");
				}else{
					throw new Exception("Metodo (WSDLAccordoServizio) nel registro["+nomeRegistro+"] ha ritornato un valore null");
				}
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					if(dNotFound!=null){
						responseCache.setException(dNotFound);
					}else{
						responseCache.setObject(obj);
					}
					this.cache.put(keyCache,responseCache);
				}catch(UtilsException e){
					this.log.error("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
				}
			}

		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}
		catch(Exception e){
			if(DriverRegistroServiziNotFound.class.getName().equals(e.getClass().getName()))
				throw (DriverRegistroServiziNotFound) e;
			else
				throw new DriverRegistroServiziException("RegistroServizi, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}

		if(dNotFound!=null){
			throw dNotFound;
		}else
			return obj;
	}


	/**
	 * Si occupa di effettuare una ricerca nei registri
	 *
	 * @param idService ID del Servizio
	 * @param nomeRegistro Nome del registro su cui effettuare la ricerca (null per tutti i registri)
	 * @return l'oggetto se trovato, null altrimenti.
	 * 
	 */
	private org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getAccordoServizioEngine(IDServizio idService,InformationWsdlSource infoWsdlSource,
			String nomeRegistro,Connection connectionPdD,boolean buildSchemaXSD) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{


		// Effettuo le query nella mia gerarchia di registri.
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio = null;
		org.openspcoop2.core.registry.AccordoServizioParteComune as = null;

		//Recupero la classe specificata dal parametro passato
		boolean registroServiziDB = false;
		if(nomeRegistro!=null){
			this.log.debug("Cerco wsdl nel registro ["+nomeRegistro+"]");
			try{
				//org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.driverRegistroServizi.get(nomeRegistro);
				org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.getDriver(connectionPdD, nomeRegistro);
				this.log.debug("invocazione metodo getWSDLAccordoServizio (search servizio)...");
				// ricerca servizio richiesto
				try{
					if(driver instanceof DriverRegistroServiziDB)
						servizio = ((DriverRegistroServiziDB)driver).getAccordoServizioParteSpecifica(idService,true); // leggo contenuto allegati
					else
						servizio = driver.getAccordoServizioParteSpecifica(idService);	
				}catch(DriverRegistroServiziNotFound e){}
				if(servizio == null){
					throw new DriverRegistroServiziNotFound("Servizio ["+idService.getTipoServizio()+idService.getServizio()+"] non definito");
				}
				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(servizio.getAccordoServizioParteComune());
				this.log.debug("invocazione metodo getWSDLAccordoServizio (search accordo)...");
				if(driver instanceof DriverRegistroServiziDB)
					as = ((DriverRegistroServiziDB)driver).getAccordoServizioParteComune(idAccordo,true); // leggo contenuto allegati
				else
					as = driver.getAccordoServizioParteComune(idAccordo);
				if (as == null){
					throw new DriverRegistroServiziNotFound("Accordo di servizio ["+idAccordo+"] associato al servizio ["+idService.getTipoServizio()+idService.getServizio()+"] non presente nel registro");
				}
				if(servizio.getPortType()!=null && ("".equals(servizio.getPortType())==false)){
					// verifico presenza portType in accordo di servizio
					boolean findPortType = false;
					for(int l=0;l<as.sizePortTypeList();l++){
						if(servizio.getPortType().equals(as.getPortType(l).getNome())){
							findPortType = true;
							break;
						}
					}
					if(findPortType==false){
						throw new DriverRegistroServiziNotFound("PortType["+servizio.getPortType()+"] associato al servizio ["+idService.getTipoServizio()+idService.getServizio()+"] non presente nell'Accordo di servizio ["+idAccordo+"]");
					}
				}

				// trovato!
				registroServiziDB = (driver instanceof DriverRegistroServiziDB);
				
			}catch(DriverRegistroServiziNotFound e){
				// Non presente
				this.log.debug("Ricerca nel registro non riuscita (metodo getWSDLAccordoServizio): "+e.getMessage());
				throw e;
			}
			catch(Exception e){
				// Non presente
				this.log.debug("Ricerca nel registro ["+nomeRegistro+"] non riuscita, "+e.getMessage(),e);
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}

		}else{
			this.log.debug("Cerco nella mia gerarchia di registri");
			StringBuffer notFoundProblem = new StringBuffer();
			StringBuffer exceptionProblem = new StringBuffer();
			boolean find = false;
			for (Enumeration<?> en = this.driverRegistroServizi.keys() ; en.hasMoreElements() ;) {
				String nomeRegInLista= (String) en.nextElement();
				this.log.debug("Cerco nel registro con nome["+nomeRegInLista+"]");
				try{
					//org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.driverRegistroServizi.get(nomeRegInLista);
					org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.getDriver(connectionPdD, nomeRegInLista);
					this.log.debug("invocazione metodo getWSDLAccordoServizio (search servizio) nel registro["+nomeRegInLista+"]...");
					// ricerca servizio richiesto
					try{
						if(driver instanceof DriverRegistroServiziDB)
							servizio = ((DriverRegistroServiziDB)driver).getAccordoServizioParteSpecifica(idService,true); // leggo contenuto allegati
						else
							servizio = driver.getAccordoServizioParteSpecifica(idService);	
					}catch(DriverRegistroServiziNotFound e){}
					if(servizio == null){
						throw new DriverRegistroServiziNotFound("Servizio ["+idService.getTipoServizio()+idService.getServizio()+"] non definito");
					}
					IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(servizio.getAccordoServizioParteComune());
					this.log.debug("invocazione metodo getWSDLAccordoServizio (search accordo) nel registro["+nomeRegInLista+"]...");
					if(driver instanceof DriverRegistroServiziDB)
						as = ((DriverRegistroServiziDB)driver).getAccordoServizioParteComune(idAccordo,true); // leggo contenuto allegati
					else
						as = driver.getAccordoServizioParteComune(idAccordo);
					if (as == null){
						throw new DriverRegistroServiziNotFound("Accordo di servizio ["+idAccordo+"] associato al servizio ["+idService.getTipoServizio()+idService.getServizio()+"] non presente nel registro");
					}
					if(servizio.getPortType()!=null && ("".equals(servizio.getPortType())==false)){
						// verifico presenza portType in accordo di servizio
						boolean findPortType = false;
						for(int l=0;l<as.sizePortTypeList();l++){
							if(servizio.getPortType().equals(as.getPortType(l).getNome())){
								findPortType = true;
								break;
							}
						}
						if(findPortType==false){
							throw new DriverRegistroServiziNotFound("PortType["+servizio.getPortType()+"] associato al servizio ["+idService.getTipoServizio()+idService.getServizio()+"] non presente nell'Accordo di servizio ["+idAccordo+"]");
						}
					}
					
					// trovato!
					registroServiziDB = (driver instanceof DriverRegistroServiziDB);
					find=true;

				}catch(DriverRegistroServiziNotFound ed){
					// Non presente
					this.log.debug("Ricerca nel registro ["+nomeRegInLista+"] non riuscita (metodo getWSDLAccordoServizio): "+ed.getMessage());
					notFoundProblem.append("\nRegistro["+nomeRegInLista+"], ricerca fallita: "+ed.getMessage());
				}catch(Exception e){
					// Non presente
					this.log.debug("Ricerca nel registro ["+nomeRegInLista+"] non riuscita, "+e.getMessage(),e);
					if(this.raggiungibilitaTotale)
						throw new DriverRegistroServiziException(e.getMessage(),e);
					else
						exceptionProblem.append("\nRegistro["+nomeRegInLista+"], accesso non riuscito: "+e.getMessage());
				}
				if(find)
					break;
			}

			this.log.debug("invocazione metodo getWSDLAccordoServizio completata.");
			if(find==false){
				if(exceptionProblem.length()>0 && notFoundProblem.length()>0){
					throw new DriverRegistroServiziException("getWSDLAccordoServizio:"+exceptionProblem.toString()+notFoundProblem.toString());
				}else if(exceptionProblem.length()>0){
					throw new DriverRegistroServiziException("getWSDLAccordoServizio:"+exceptionProblem.toString());
				}else if(notFoundProblem.length()>0){
					throw new DriverRegistroServiziNotFound("getWSDLAccordoServizio:"+notFoundProblem.toString());
				}else{
					throw new DriverRegistroServiziException("getWSDLAccordoServizio: ricerca non riuscita in tutti i registri");
				}
			}
		}



		// Costruisco oggetto
		org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper accordoServizioWrapper = new org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper();

		IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(as);
		accordoServizioWrapper.setIdAccordoServizio(idAccordo);
		
		accordoServizioWrapper.setNomePortType(servizio.getPortType());
		
		accordoServizioWrapper.setTipologiaServizio(servizio.getServizio().getTipologiaServizio());
		
		accordoServizioWrapper.setAccordoServizio(as);
		
		accordoServizioWrapper.setLocationWsdlImplementativoErogatore(servizio.getWsdlImplementativoErogatore());
		accordoServizioWrapper.setLocationWsdlImplementativoFruitore(servizio.getWsdlImplementativoFruitore());
		accordoServizioWrapper.setBytesWsdlImplementativoErogatore(servizio.getByteWsdlImplementativoErogatore());
		accordoServizioWrapper.setBytesWsdlImplementativoFruitore(servizio.getByteWsdlImplementativoFruitore());
		
		AccordoServizioWrapperUtilities wsdlWrapperUtilities = new AccordoServizioWrapperUtilities(this.log);
		wsdlWrapperUtilities.setAccordoServizio(accordoServizioWrapper);
			
		if(buildSchemaXSD){
			try{
				boolean buildFromBytes = registroServiziDB;
				// buildFromBytes=false: Costruzione attraverso Location (versione XML,WEB,UDDI)
				// buildFromBytes=true:  Costruzione attraverso bytes registrati sul DB
				wsdlWrapperUtilities.buildSchema(buildFromBytes);
				
			}catch(DriverRegistroServiziException e){
				throw new DriverRegistroServiziException("[SchemaXSD] "+e.getMessage(),e);
			}
		}

		try{
			if(InformationWsdlSource.WSDL.equals(infoWsdlSource)){
				
				this.log.debug("Costruisco WSDLAccordoServizio from WSDL...");
				_loadFromWsdl(servizio, registroServiziDB, wsdlWrapperUtilities);
				
			}else if(InformationWsdlSource.REGISTRY.equals(infoWsdlSource)){
				
				this.log.debug("Costruisco WSDLAccordoServizio from AccordoServizio...");
				wsdlWrapperUtilities.buildAccordoServizioWrapperFromOpenSPCoopAS(as);
				
			}
			else if(InformationWsdlSource.WSDL_REGISTRY.equals(infoWsdlSource) || 
					InformationWsdlSource.SAFE_WSDL_REGISTRY.equals(infoWsdlSource)){
			
				this.log.debug("Costruisco WSDLAccordoServizio from WSDL (Step1)...");
				try{
					_loadFromWsdl(servizio, registroServiziDB, wsdlWrapperUtilities);
				}catch(DriverRegistroServiziException e){
					if(InformationWsdlSource.WSDL_REGISTRY.equals(infoWsdlSource)){
						throw e;
					}
				}
				
				this.log.debug("Costruisco WSDLAccordoServizio from AccordoServizio (Step2)...");
				AccordoServizioWrapperUtilities wsdlWrapperUtilitiesStep2 = new AccordoServizioWrapperUtilities(this.log);
				AccordoServizioWrapper accordoServizioWrapperStep2 = accordoServizioWrapper.clone(false);
				wsdlWrapperUtilitiesStep2.setAccordoServizio(accordoServizioWrapperStep2);
				try{
					wsdlWrapperUtilitiesStep2.buildAccordoServizioWrapperFromOpenSPCoopAS(as);
				}catch(DriverRegistroServiziException e){
					if(InformationWsdlSource.WSDL_REGISTRY.equals(infoWsdlSource)){
						throw e;
					}
				}
				
				this.log.debug("Costruisco WSDLAccordoServizio merge WSDL e AccordoServizio (Step3)...");
				_merge(accordoServizioWrapper, accordoServizioWrapperStep2);
				
			}
			else if(InformationWsdlSource.REGISTRY_WSDL.equals(infoWsdlSource) || 
					InformationWsdlSource.SAFE_REGISTRY_WSDL.equals(infoWsdlSource)){
				
				this.log.debug("Costruisco WSDLAccordoServizio from AccordoServizio (Step1)...");
				try{
					wsdlWrapperUtilities.buildAccordoServizioWrapperFromOpenSPCoopAS(as);
				}catch(DriverRegistroServiziException e){
					if(InformationWsdlSource.REGISTRY_WSDL.equals(infoWsdlSource)){
						throw e;
					}
				}
				
				this.log.debug("Costruisco WSDLAccordoServizio from WSDL (Step2)...");
				AccordoServizioWrapperUtilities wsdlWrapperUtilitiesStep2 = new AccordoServizioWrapperUtilities(this.log);
				AccordoServizioWrapper accordoServizioWrapperStep2 = accordoServizioWrapper.clone(false);
				wsdlWrapperUtilitiesStep2.setAccordoServizio(accordoServizioWrapperStep2);
				try{
					_loadFromWsdl(servizio, registroServiziDB, wsdlWrapperUtilitiesStep2);
				}catch(DriverRegistroServiziException e){
					if(InformationWsdlSource.REGISTRY_WSDL.equals(infoWsdlSource)){
						throw e;
					}
				}
					
				this.log.debug("Costruisco WSDLAccordoServizio merge  AccordoServizio e WSDL (Step3)...");
				_merge(accordoServizioWrapper, accordoServizioWrapperStep2);
								
			}
		}catch(DriverRegistroServiziException e){
			if(TipologiaServizio.CORRELATO.equals(servizio.getServizio().getTipologiaServizio())){
				throw new DriverRegistroServiziException("[WSDL-FRUITORE] "+e.getMessage(),e);
			}else{
				throw new DriverRegistroServiziException("[WSDL-EROGATORE] "+e.getMessage(),e);
			}
		}

		this.log.debug("invocazione metodo getWSDLAccordoServizio completata.");
		return accordoServizioWrapper;
	}

	private void _loadFromWsdl(AccordoServizioParteSpecifica servizio, boolean registroServiziDB, AccordoServizioWrapperUtilities wsdlWrapperUtilities) throws DriverRegistroServiziException{
		if(TipologiaServizio.CORRELATO.equals(servizio.getServizio().getTipologiaServizio())){
			javax.wsdl.Definition wsdlFruitore = null;
			if(registroServiziDB){
				wsdlFruitore = wsdlWrapperUtilities.buildWsdlFruitoreFromBytes();
			}else{
				wsdlFruitore = wsdlWrapperUtilities.buildWsdlFruitore();
			}
			boolean readParteImplementativa = 
				( (servizio.getWsdlImplementativoFruitore()!=null) && ("".equals(servizio.getWsdlImplementativoFruitore())==false) )
				||
				(servizio.getByteWsdlImplementativoFruitore()!=null) 
				||
				(wsdlFruitore.getAllBindings()!=null && wsdlFruitore.getAllBindings().size()>0) 
				;
			wsdlWrapperUtilities.buildAccordoServizioWrapperFromWsdl(wsdlFruitore,readParteImplementativa);
		}else{
			javax.wsdl.Definition wsdlErogatore = null;
			if(registroServiziDB){
				wsdlErogatore = wsdlWrapperUtilities.buildWsdlErogatoreFromBytes();
			}else{
				wsdlErogatore = wsdlWrapperUtilities.buildWsdlErogatore();
			}
			boolean readParteImplementativa =	 
				( (servizio.getWsdlImplementativoErogatore()!=null) && ("".equals(servizio.getWsdlImplementativoErogatore())==false) )
				||
				(servizio.getByteWsdlImplementativoErogatore()!=null) 
				||
				(wsdlErogatore.getAllBindings()!=null && wsdlErogatore.getAllBindings().size()>0) 
				;
			wsdlWrapperUtilities.buildAccordoServizioWrapperFromWsdl(wsdlErogatore,readParteImplementativa);
		}
	}
	
	private void _merge(AccordoServizioWrapper ptA, AccordoServizioWrapper ptB){
		for (int i = 0; i < ptB.sizePortTypeList(); i++) {
			PortType portTypeB = ptB.getPortType(i);
			boolean exists = false;
			for (int j = 0; j < ptA.sizePortTypeList(); j++) {
				PortType portTypeA = ptA.getPortType(j);
				if(portTypeB.getNome().equals(portTypeA.getNome())){
					
					// verifico le operation interne
					for (Operation opB : portTypeB.getAzioneList()) {
						boolean existsOperation = false;
						for (Operation opA : portTypeA.getAzioneList()) {
							if(opB.getNome().equals(opA.getNome())){
								existsOperation = true;
								break;
							}
						}
						if(!existsOperation){
							//System.out.println("ADD OP ["+opB.getNome()+"]");
							portTypeA.addAzione(opB);
						}
					}
					
					exists = true;
					break;
				}
			}
			if(!exists){
				//System.out.println("ADD PT ["+portTypeB.getNome()+"]");
				ptA.addPortType(portTypeB);
			}
		}
		
	}
}
