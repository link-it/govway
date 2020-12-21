/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.registry;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaAzioni;
import org.openspcoop2.core.registry.driver.FiltroRicercaFruizioniServizio;
import org.openspcoop2.core.registry.driver.FiltroRicercaOperations;
import org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes;
import org.openspcoop2.core.registry.driver.FiltroRicercaResources;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.uddi.DriverRegistroServiziUDDI;
import org.openspcoop2.core.registry.driver.utils.DriverRegistroServiziWSInitUtilities;
import org.openspcoop2.core.registry.driver.web.DriverRegistroServiziWEB;
import org.openspcoop2.core.registry.driver.xml.DriverRegistroServiziXML;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapperUtilities;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.slf4j.Logger;




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
	private List<DriverRegistroServiziXML> registriXML;

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
	public void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond, CryptConfig cryptConfigSoggetti) throws DriverRegistroServiziException{
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
				initCacheRegistriServizi(configurazioneCache,null,false, cryptConfigSoggetti);
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
			String jndiNameDatasourcePdD, boolean useOp2UtilsDatasource, boolean bindJMX, 
			boolean prefillCache, CryptConfig cryptConfigSoggetti)throws DriverRegistroServiziException{

		try{ 
			this.driverRegistroServizi = new java.util.Hashtable<String,IDriverRegistroServiziGet>();
			this.registriXML = new ArrayList<DriverRegistroServiziXML>();

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
					try{
						driver = DriverRegistroServiziWSInitUtilities.newInstance(registro.getLocation(), 
								registro.getUser(),
								registro.getPassword(), this.log);
						this.driverRegistroServizi.put(nomeRegistro,driver);
					}catch(Throwable e){
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
				initCacheRegistriServizi(accessoRegistro.getCache(),alogConsole,prefillCache, cryptConfigSoggetti);
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
	
	// Costruttore per implementare classe org.openspcoop2.protocol.basic.registry.RegistryReader
	public RegistroServizi(IDriverRegistroServiziGet driverRegistroServiziGET, Logger alog, boolean readObjectStatoBozza, String tipoRegistro)throws DriverRegistroServiziException{

		try{ 
			this.driverRegistroServizi = new java.util.Hashtable<String,IDriverRegistroServiziGet>();
			this.registriXML = new ArrayList<DriverRegistroServiziXML>();

			if(alog!=null)
				this.log = alog;
			else
				this.log = LoggerWrapperFactory.getLogger(RegistroServizi.class);

			this.raggiungibilitaTotale = true;

			this.readObjectStatoBozza = readObjectStatoBozza;
			
			IDriverRegistroServiziGet driver = driverRegistroServiziGET;
			String nomeRegistro = "Registro_"+tipoRegistro;
			
			this.mappingNomeRegistroToTipiDatabase.put(nomeRegistro, tipoRegistro);
			this.mappingNomeRegistroToUseConnectionPdD.put(nomeRegistro, false);
			this.driverRegistroServizi.put(nomeRegistro,driver);

		}catch(Exception e){
			String msg = "Riscontrato errore durante l'inizializzazione del registro: "+e.getMessage();
			this.log.error(msg);
			throw new DriverRegistroServiziException("Riscontrato errore durante l'inizializzazione del registro: "+e.getMessage());
		}

		if( this.driverRegistroServizi.size() == 0 )
			throw new DriverRegistroServiziException("Non e' stato possibile inizializzare nessuna sorgente di lettura per un Registro dei Servizi");
	}




	private void initCacheRegistriServizi(org.openspcoop2.core.config.Cache configurazioneCache,Logger alogConsole, boolean prefillCache, CryptConfig cryptConfigSoggetti) throws Exception{
		this.cache = new Cache(CostantiRegistroServizi.CACHE_REGISTRO_SERVIZI);

		if( (configurazioneCache.getDimensione()!=null) ||
				(configurazioneCache.getAlgoritmo() != null) ){

			try{
				if( configurazioneCache.getDimensione()!=null ){
					int dimensione = -1;				
					if(prefillCache){
						dimensione = Integer.MAX_VALUE;
					}
					else{
						dimensione = Integer.parseInt(configurazioneCache.getDimensione());
					}
					String msg = "Dimensione della cache (RegistroServizi) impostata al valore: "+dimensione;
					if(prefillCache){
						msg = "[Prefill Enabled] " + msg;
					}
					if(prefillCache){
						this.log.warn(msg);
					}
					else{
						this.log.info(msg);
					}
					if(alogConsole!=null){
						if(prefillCache){
							alogConsole.warn(msg);
						}
						else{
							alogConsole.info(msg);
						}
					}
					this.cache.setCacheSize(dimensione);
				}
				else{
					if(prefillCache){
						int dimensione = Integer.MAX_VALUE;
						String msg = "[Prefill Enabled] Dimensione della cache (RegistroServizi) impostata al valore: "+dimensione;
						this.log.warn(msg);
						if(alogConsole!=null){
							alogConsole.warn(msg);
						}
						this.cache.setCacheSize(dimensione);
					}
				}
			}catch(Exception error){
				String msg = "Parametro errato per la dimensione della cache (RegistroServizi): "+error.getMessage();
				this.log.error(msg);
				if(alogConsole!=null)
					alogConsole.info(msg);
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

			try{
				if( configurazioneCache.getItemIdleTime() != null  ){
					int itemIdleTime = -1;				
					if(prefillCache){
						itemIdleTime = -1;
					}
					else{
						itemIdleTime = Integer.parseInt(configurazioneCache.getItemIdleTime());
					}
					String msg = "Attributo 'IdleTime' (RegistroServizi) impostato al valore: "+itemIdleTime;
					if(prefillCache){
						msg = "[Prefill Enabled] " + msg;
					}
					if(prefillCache){
						this.log.warn(msg);
					}
					else{
						this.log.info(msg);
					}
					if(alogConsole!=null){
						if(prefillCache){
							alogConsole.warn(msg);
						}
						else{
							alogConsole.info(msg);
						}
					}
					this.cache.setItemIdleTime(itemIdleTime);
				}
				else{
					if(prefillCache){
						int itemIdleTime = -1;
						String msg = "[Prefill Enabled] Attributo 'IdleTime' (RegistroServizi) impostato al valore: "+itemIdleTime;
						this.log.warn(msg);
						if(alogConsole!=null){
							alogConsole.warn(msg);
						}
						this.cache.setItemIdleTime(itemIdleTime);
					}
				}
			}catch(Exception error){
				String msg = "Parametro errato per l'attributo 'IdleTime' (RegistroServizi): "+error.getMessage();
				this.log.error(msg);
				if(alogConsole!=null)
					alogConsole.info(msg);
			}
			
			try{
				if( configurazioneCache.getItemLifeSecond() != null  ){
					int itemLifeSecond = -1;				
					if(prefillCache){
						itemLifeSecond = -1;
					}
					else{
						itemLifeSecond = Integer.parseInt(configurazioneCache.getItemLifeSecond());
					}
					String msg = "Attributo 'MaxLifeSecond' (RegistroServizi) impostato al valore: "+itemLifeSecond;
					if(prefillCache){
						msg = "[Prefill Enabled] " + msg;
					}
					if(prefillCache){
						this.log.warn(msg);
					}
					else{
						this.log.info(msg);
					}
					if(alogConsole!=null){
						if(prefillCache){
							alogConsole.warn(msg);
						}
						else{
							alogConsole.info(msg);
						}
					}
					this.cache.setItemLifeTime(itemLifeSecond);
				}
				else{
					if(prefillCache){
						int itemLifeSecond = -1;
						String msg = "Attributo 'MaxLifeSecond' (RegistroServizi) impostato al valore: "+itemLifeSecond;
						this.log.warn(msg);
						if(alogConsole!=null){
							alogConsole.warn(msg);
						}
						this.cache.setItemLifeTime(itemLifeSecond);
					}
				}
			}catch(Exception error){
				String msg = "Parametro errato per l'attributo 'MaxLifeSecond' (RegistroServizi): "+error.getMessage();
				this.log.error(msg);
				if(alogConsole!=null)
					alogConsole.info(msg);
			}

		}
		
		if(prefillCache){
			this.prefillCache(null,alogConsole, cryptConfigSoggetti);
		}
	}

	public void disableSyncronizedGet() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		this.cache.disableSyncronizedGet();
	}
	public boolean isDisableSyncronizedGet() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		return this.cache.isDisableSyncronizedGet();
	}



	protected java.util.Hashtable<String, IDriverRegistroServiziGet> getDriverRegistroServizi() {
		return this.driverRegistroServizi;
	}

	public void prefillCache(Connection connectionPdD,Logger alogConsole,
			CryptConfig cryptConfigSoggetti){
		
		String msg = "[Prefill] Inizializzazione cache (RegistroServizi) in corso ...";
		this.log.info(msg);
		if(alogConsole!=null){
			alogConsole.info(msg);
		}
		
		Set<String> registri = this.driverRegistroServizi.keySet();
				
		for (String nomeRegistro : registri) {
		
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"]";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			
			/* ********  R I C E R C A    S E R V I Z I  (utilizzo dei driver) ******** */
			

			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero accordi di servizio parte comune ...";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			FiltroRicercaAccordi filtroAccordi = new FiltroRicercaAccordi();
			List<IDAccordo> listAccordi = null;
			try{
				listAccordi = this.driverRegistroServizi.get(nomeRegistro).getAllIdAccordiServizioParteComune(filtroAccordi);
			}
			catch(DriverRegistroServiziNotFound notFound){}
			catch(DriverRegistroServiziException e){this.log.error("[prefill] errore"+e.getMessage(),e);}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recuperati "+(listAccordi!=null ? listAccordi.size() : 0)+" accordi di servizio parte comune";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			if(listAccordi!=null && listAccordi.size()>0){
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listAccordi.size()+" accordi di servizio parte comune ...";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				for (IDAccordo idAccordo : listAccordi) {
					
					msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero dati per l'accordo di servizio parte comune ["+idAccordo+"] ...";
					this.log.debug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
					
					try{
						this.cache.remove(_getKey_getAccordoServizioParteComune(idAccordo, null));
						this.getAccordoServizioParteComune(connectionPdD, nomeRegistro, idAccordo);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
					try{
						this.cache.remove(_getKey_getAccordoServizioParteComune(idAccordo, false));
						this.getAccordoServizioParteComune(connectionPdD, nomeRegistro, idAccordo, false);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
					AccordoServizioParteComune as = null;
					try{
						this.cache.remove(_getKey_getAccordoServizioParteComune(idAccordo, true));
						as = this.getAccordoServizioParteComune(connectionPdD, nomeRegistro, idAccordo, true);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
										
					boolean serviziCorrelati = false;
					if(as!=null) {
						ProfiloCollaborazione profiloAS = as.getProfiloCollaborazione();
						for (Azione az : as.getAzioneList()) {
							ProfiloCollaborazione profiloAZ = profiloAS;
							if (CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(az.getProfAzione())) {
								profiloAZ = az.getProfiloCollaborazione();
							}
							if(ProfiloCollaborazione.ASINCRONO_ASIMMETRICO.equals(profiloAZ) || 
									ProfiloCollaborazione.ASINCRONO_SIMMETRICO.equals(profiloAZ)) {
								serviziCorrelati = true;
								break;
							} 
						}
						if(!serviziCorrelati) {
							for (PortType pt : as.getPortTypeList()) {
								ProfiloCollaborazione profiloPT = profiloAS;
								if (CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT())) {
									profiloPT = pt.getProfiloCollaborazione();
								}
								for (Operation az : pt.getAzioneList()) {
									ProfiloCollaborazione profiloAZ = profiloPT;
									if (CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(az.getProfAzione())) {
										profiloAZ = az.getProfiloCollaborazione();
									}
									if(ProfiloCollaborazione.ASINCRONO_ASIMMETRICO.equals(profiloAZ) || 
											ProfiloCollaborazione.ASINCRONO_SIMMETRICO.equals(profiloAZ)) {
										serviziCorrelati = true;
										break;
									} 
								}
								if(serviziCorrelati) {
									break;
								}
							}
						}
					}
					
					msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero dati per l'accordo di servizio parte comune ["+idAccordo+"] completato";
					this.log.debug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
					
					if(serviziCorrelati) {
					
						msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], (analisi profilo asincrono) recupero servizi che implementano l'accordo di servizio parte comune ["+idAccordo+"] ...";
						this.log.debug(msg);
						if(alogConsole!=null){
							alogConsole.debug(msg);
						}
					
						FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
						filtroServizi.setIdAccordoServizioParteComune(idAccordo);
						List<IDServizio> listServizi = null;
						try{
							listServizi = this.driverRegistroServizi.get(nomeRegistro).getAllIdServizi(filtroServizi);
						}
						catch(DriverRegistroServiziNotFound notFound){}
						catch(DriverRegistroServiziException e){this.log.error("[prefill] errore"+e.getMessage(),e);}

						msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], (analisi profilo asincrono) recuperati "+(listServizi!=null ? listServizi.size() : 0)+" servizi che implementano l'accordo di servizio parte comune ["+idAccordo+"]";
						this.log.debug(msg);
						if(alogConsole!=null){
							alogConsole.debug(msg);
						}
						
						if(listServizi!=null && listServizi.size()>0) {
							for (IDServizio idServizio : listServizi) {
								
								IDSoggetto idSoggetto = idServizio.getSoggettoErogatore();
								
								if(org.openspcoop2.core.constants.TipologiaServizio.CORRELATO.equals(idServizio.getTipologia())) {
									
									msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+
											"], recupero dati asincroni per l'accordo di servizio parte comune ["+idAccordo+"] e soggetto ["+idSoggetto+"] ...";
									this.log.debug(msg);
									if(alogConsole!=null){
										alogConsole.debug(msg);
									}
																		
									try{
										this.cache.remove(_getKey_getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto, idAccordo,null));
										this.getAccordoServizioParteSpecifica_ServizioCorrelato(connectionPdD, nomeRegistro, idSoggetto, idAccordo);
									}
									catch(DriverRegistroServiziNotFound notFound){}
									catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
									
									try{
										this.cache.remove(_getKey_getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto, idAccordo,false));
										this.getAccordoServizioParteSpecifica_ServizioCorrelato(connectionPdD, nomeRegistro, idSoggetto, idAccordo, false);
									}
									catch(DriverRegistroServiziNotFound notFound){}
									catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
									
									try{
										this.cache.remove(_getKey_getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto, idAccordo,true));
										this.getAccordoServizioParteSpecifica_ServizioCorrelato(connectionPdD, nomeRegistro, idSoggetto, idAccordo, true);
									}
									catch(DriverRegistroServiziNotFound notFound){}
									catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
									
									msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+
											"], recupero dati asincroni per l'accordo di servizio parte comune ["+idAccordo+"] e soggetto ["+idSoggetto+"] completato";
									this.log.debug(msg);
									if(alogConsole!=null){
										alogConsole.debug(msg);
									}
									
								}
								
								else {
									msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+
											"], recupero dati asincroni per l'accordo di servizio parte comune ["+idAccordo+"] e soggetto ["+idSoggetto+"] non effettuata essendo il servizio non di tipologia correlata";
									this.log.debug(msg);
									if(alogConsole!=null){
										alogConsole.debug(msg);
									}
								}
							}
						}
					}

				}
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listAccordi.size()+" accordi di servizio parte comune completato";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero porte di dominio ...";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			FiltroRicerca filtroPdd = new FiltroRicerca();
			List<String> listPdd = null;
			try{
				listPdd = this.driverRegistroServizi.get(nomeRegistro).getAllIdPorteDominio(filtroPdd);
			}
			catch(DriverRegistroServiziNotFound notFound){}
			catch(DriverRegistroServiziException e){this.log.error("[prefill] errore"+e.getMessage(),e);}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recuperate "+(listPdd!=null ? listPdd.size() : 0)+" porte di dominio";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			if(listPdd!=null && listPdd.size()>0){
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listPdd.size()+" porte di dominio ...";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				for (String idPdd : listPdd) {
					
					try{
						this.cache.remove(_getKey_getPortaDominio(idPdd));
						this.getPortaDominio(connectionPdD, nomeRegistro, idPdd);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
				}
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listPdd.size()+" porte di dominio completata";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero ruoli ...";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			FiltroRicercaRuoli filtroRuoli = new FiltroRicercaRuoli();
			List<IDRuolo> listRuoli = null;
			try{
				listRuoli = this.driverRegistroServizi.get(nomeRegistro).getAllIdRuoli(filtroRuoli);
			}
			catch(DriverRegistroServiziNotFound notFound){}
			catch(DriverRegistroServiziException e){this.log.error("[prefill] errore"+e.getMessage(),e);}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recuperati "+(listRuoli!=null ? listRuoli.size() : 0)+" ruoli";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			if(listRuoli!=null && listRuoli.size()>0){
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listRuoli.size()+" ruoli ...";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				for (IDRuolo idRuolo : listRuoli) {
					
					try{
						this.cache.remove(_getKey_getRuolo(idRuolo.getNome()));
						this.getRuolo(connectionPdD, nomeRegistro, idRuolo.getNome());
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
				}
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listRuoli.size()+" ruoli completata";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			}
			
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero scope ...";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			FiltroRicercaScope filtroScope = new FiltroRicercaScope();
			List<IDScope> listScope = null;
			try{
				listScope = this.driverRegistroServizi.get(nomeRegistro).getAllIdScope(filtroScope);
			}
			catch(DriverRegistroServiziNotFound notFound){}
			catch(DriverRegistroServiziException e){this.log.error("[prefill] errore"+e.getMessage(),e);}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recuperati "+(listScope!=null ? listScope.size() : 0)+" scope";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			if(listScope!=null && listScope.size()>0){
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listScope.size()+" scope ...";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				for (IDScope idScope : listScope) {
					
					try{
						this.cache.remove(_getKey_getScope(idScope.getNome()));
						this.getScope(connectionPdD, nomeRegistro, idScope.getNome());
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
				}
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listScope.size()+" scope completata";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			}
			
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero soggetti ...";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
			List<IDSoggetto> listSoggetti = null;
			try{
				listSoggetti = this.driverRegistroServizi.get(nomeRegistro).getAllIdSoggetti(filtroSoggetti);
			}
			catch(DriverRegistroServiziNotFound notFound){}
			catch(DriverRegistroServiziException e){this.log.error("[prefill] errore"+e.getMessage(),e);}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recuperati "+(listSoggetti!=null ? listSoggetti.size() : 0)+" soggetti";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			if(listSoggetti!=null && listSoggetti.size()>0){
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listSoggetti.size()+" soggetti ...";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				for (IDSoggetto idSoggetto : listSoggetti) {
					
					try{
						this.cache.remove(_getKey_getSoggetto(idSoggetto));
						this.getSoggetto(connectionPdD, nomeRegistro, idSoggetto);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
					Soggetto soggetto = null;
					try{
						soggetto = this.driverRegistroServizi.get(nomeRegistro).getSoggetto(idSoggetto);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(DriverRegistroServiziException e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
					if(soggetto!=null){
						if(soggetto.getCredenziali()!=null && soggetto.getCredenziali().getTipo()!=null){
							if(CredenzialeTipo.BASIC.equals(soggetto.getCredenziali().getTipo())){
								try{
									this.cache.remove(_getKey_getSoggettoByCredenzialiBasic(soggetto.getCredenziali().getUser(), soggetto.getCredenziali().getPassword()));
									this.getSoggettoByCredenzialiBasic(connectionPdD, nomeRegistro, soggetto.getCredenziali().getUser(), soggetto.getCredenziali().getPassword(), cryptConfigSoggetti);
								}
								catch(DriverRegistroServiziNotFound notFound){}
								catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
							}
							else if(CredenzialeTipo.APIKEY.equals(soggetto.getCredenziali().getTipo())){
								try{
									this.cache.remove(_getKey_getSoggettoByCredenzialiApiKey(soggetto.getCredenziali().getUser(), soggetto.getCredenziali().getPassword(), soggetto.getCredenziali().isCertificateStrictVerification()));
									this.getSoggettoByCredenzialiApiKey(connectionPdD, nomeRegistro, soggetto.getCredenziali().getUser(), soggetto.getCredenziali().getPassword(), soggetto.getCredenziali().isCertificateStrictVerification(), cryptConfigSoggetti);
								}
								catch(DriverRegistroServiziNotFound notFound){}
								catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
							}
							else if(CredenzialeTipo.SSL.equals(soggetto.getCredenziali().getTipo())){
								if(soggetto.getCredenziali().getSubject()!=null) {
									try{
										this.cache.remove(_getKey_getSoggettoByCredenzialiSsl(soggetto.getCredenziali().getSubject(),soggetto.getCredenziali().getIssuer()));
										this.getSoggettoByCredenzialiSsl(connectionPdD, nomeRegistro, soggetto.getCredenziali().getSubject(),soggetto.getCredenziali().getIssuer());
									}
									catch(DriverRegistroServiziNotFound notFound){}
									catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
								}
								if(soggetto.getCredenziali().getCertificate()!=null) {
									try{
										CertificateInfo certificato = ArchiveLoader.load(ArchiveType.CER, soggetto.getCredenziali().getCertificate(), 0, null).getCertificate();
										this.cache.remove(_getKey_getSoggettoByCredenzialiSsl(certificato, soggetto.getCredenziali().isCertificateStrictVerification()));
										this.getSoggettoByCredenzialiSsl(connectionPdD, nomeRegistro, certificato, soggetto.getCredenziali().isCertificateStrictVerification());
									}
									catch(DriverRegistroServiziNotFound notFound){}
									catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
								}
							}
							else if(CredenzialeTipo.PRINCIPAL.equals(soggetto.getCredenziali().getTipo())){
								try{
									this.cache.remove(_getKey_getSoggettoByCredenzialiPrincipal(soggetto.getCredenziali().getUser()));
									this.getSoggettoByCredenzialiPrincipal(connectionPdD, nomeRegistro, soggetto.getCredenziali().getUser());
								}
								catch(DriverRegistroServiziNotFound notFound){}
								catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
							}
						}
					}
				}
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listSoggetti.size()+" soggetti completata";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero servizi ...";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
			List<IDServizio> listServizi = null;
			try{
				listServizi = this.driverRegistroServizi.get(nomeRegistro).getAllIdServizi(filtroServizi);
			}
			catch(DriverRegistroServiziNotFound notFound){}
			catch(DriverRegistroServiziException e){this.log.error("[prefill] errore"+e.getMessage(),e);}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recuperati "+(listServizi!=null ? listServizi.size() : 0)+" servizi";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			if(listServizi!=null && listServizi.size()>0){
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listServizi.size()+" servizi ...";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				for (IDServizio idServizio : listServizi) {
					
					msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero dati per l'accordo di servizio parte specifica ["+idServizio+"] ...";
					this.log.debug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
					
					try{
						this.cache.remove(_getKey_getAccordoServizioParteSpecifica(idServizio,null));
						this.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
										
					try{
						this.cache.remove(_getKey_getAccordoServizioParteSpecifica(idServizio,false));
						this.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio, false);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
					try{
						this.cache.remove(_getKey_getAccordoServizioParteSpecifica(idServizio,true));
						this.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio, true);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
				
					AccordoServizioParteSpecifica asps = null;
					try{
						asps = this.driverRegistroServizi.get(nomeRegistro).getAccordoServizioParteSpecifica(idServizio);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(DriverRegistroServiziException e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
					try{
						if(asps!=null && asps.getAccordoServizioParteComune()!=null){
							IDAccordo idAPC = this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune());
							AccordoServizioParteComune apc = this.getAccordoServizioParteComune(connectionPdD,nomeRegistro,idAPC); // già aggiornato precedentemente
							if(apc!=null){
								if(asps.getPortType()!=null && !"".equals(asps.getPortType())){
									if(apc.sizePortTypeList()>0){
										for (PortType pt : apc.getPortTypeList()) {
											if(pt.getNome().equals(asps.getPortType())){
												if(pt.sizeAzioneList()>0){
													for (Operation azione : pt.getAzioneList()) {
														idServizio.setAzione(azione.getNome());
														try{
															this.cache.remove(_getKey_getAccordoServizioParteSpecifica(idServizio,null));
															this.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio);
														}
														catch(DriverRegistroServiziNotFound notFound){}
														catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
														
														try{
															this.cache.remove(_getKey_getAccordoServizioParteSpecifica(idServizio,false));
															this.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio, false);
														}
														catch(DriverRegistroServiziNotFound notFound){}
														catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
														
														try{
															this.cache.remove(_getKey_getAccordoServizioParteSpecifica(idServizio,true));
															this.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio, true);
														}
														catch(DriverRegistroServiziNotFound notFound){}
														catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
													}
												}
											}
										}
									}
								}
								else{
									if(apc.sizeAzioneList()>0){
										for (Azione azione : apc.getAzioneList()) {
											idServizio.setAzione(azione.getNome());
											try{
												this.cache.remove(_getKey_getAccordoServizioParteSpecifica(idServizio,null));
												this.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio);
											}
											catch(DriverRegistroServiziNotFound notFound){}
											catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
											
											try{
												this.cache.remove(_getKey_getAccordoServizioParteSpecifica(idServizio,false));
												this.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio, false);
											}
											catch(DriverRegistroServiziNotFound notFound){}
											catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
											
											try{
												this.cache.remove(_getKey_getAccordoServizioParteSpecifica(idServizio,true));
												this.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio, true);
											}
											catch(DriverRegistroServiziNotFound notFound){}
											catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
										}
									}
								}
							}
						}
					}catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
					// Correlato implementato nella parte comune
					
					msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero dati per l'accordo di servizio parte specifica ["+idServizio+"] completata";
					this.log.debug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
				}
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listServizi.size()+" servizi completata";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recupero accordi di cooperazione ...";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			FiltroRicercaAccordi filtroAccordiCooperazione = new FiltroRicercaAccordi();
			List<IDAccordoCooperazione> listAccordiCooperazione = null;
			try{
				listAccordiCooperazione = this.driverRegistroServizi.get(nomeRegistro).getAllIdAccordiCooperazione(filtroAccordiCooperazione);
			}
			catch(DriverRegistroServiziNotFound notFound){}
			catch(DriverRegistroServiziException e){this.log.error("[prefill] errore"+e.getMessage(),e);}
			
			msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], recuperati "+(listAccordiCooperazione!=null ? listAccordiCooperazione.size() : 0)+" accordi di cooperazione";
			this.log.debug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			if(listAccordiCooperazione!=null && listAccordiCooperazione.size()>0){
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listAccordiCooperazione.size()+" accordi di cooperazione ...";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				for (IDAccordoCooperazione idAccordoCooperazione : listAccordiCooperazione) {
					
					try{
						this.cache.remove(_getKey_getAccordoCooperazione(idAccordoCooperazione, null));
						this.getAccordoCooperazione(connectionPdD, nomeRegistro, idAccordoCooperazione);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
					try{
						this.cache.remove(_getKey_getAccordoCooperazione(idAccordoCooperazione, false));
						this.getAccordoCooperazione(connectionPdD, nomeRegistro, idAccordoCooperazione, false);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}
					
					try{
						this.cache.remove(_getKey_getAccordoCooperazione(idAccordoCooperazione, true));
						this.getAccordoCooperazione(connectionPdD, nomeRegistro, idAccordoCooperazione, true);
					}
					catch(DriverRegistroServiziNotFound notFound){}
					catch(Exception e){this.log.error("[prefill] errore"+e.getMessage(),e);}

				}
				
				msg = "[Prefill] Inizializzazione cache (RegistroServizi) per il registro ["+nomeRegistro+"], lettura di "+listAccordiCooperazione.size()+" accordi di cooperazione completata";
				this.log.debug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			}
			
			
			
			/* ********  R I C E R C A  I D   E L E M E N T I   P R I M I T I V I  ******** */
			
			// Dipende dal filtro, non è possibile pre-inizializzarlo
		}
		
		
		msg = "[Prefill] Inizializzazione cache (RegistroServizi) completata";
		this.log.info(msg);
		if(alogConsole!=null){
			alogConsole.info(msg);
		}
	}




	/**
	 * Si occupa di effettuare una ricerca nei registri, e di inserire la ricerca in cache
	 */
	public Object getObjectCache(String keyCache,String methodName,
			String nomeRegistro,
			Boolean readContenutoAllegati,
			Connection connectionPdD,
			Object ... instances) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Class<?>[] classArgoments = null;
		Object[] values = null;
		if(instances!=null && instances.length>0){
			classArgoments = new Class<?>[instances.length];
			values = new Object[instances.length];
			for (int i = 0; i < instances.length; i++) {
				classArgoments[i] = instances[i].getClass();
				values[i] = instances[i];
			}
		}
		return getObjectCache(keyCache, methodName, nomeRegistro, readContenutoAllegati, connectionPdD, classArgoments, values);
	}
	public synchronized Object getObjectCache(String keyCache,String methodName,
			String nomeRegistro,
			Boolean readContenutoAllegati,
			Connection connectionPdD,
			Class<?>[] classArgoments, Object[] values) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

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
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:"+methodName+") nel registro["+nomeRegistro+"] in cache.");
						return response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:"+methodName+") nel registro["+nomeRegistro+"] in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.log.debug("oggetto con chiave ["+keyCache+"] (method:"+methodName+") nel registro["+nomeRegistro+"] non in cache, ricerco nel registro...");
			try{
				obj = getObject(methodName,nomeRegistro,readContenutoAllegati,connectionPdD,classArgoments,values);
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
	 */
	public Object getObject(String methodName,
			String nomeRegistro,
			Boolean readContenutoAllegati,
			Connection connectionPdD,
			Object ... instances) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Class<?>[] classArgoments = null;
		Object[] values = null;
		if(instances!=null && instances.length>0){
			classArgoments = new Class<?>[instances.length];
			values = new Object[instances.length];
			for (int i = 0; i < instances.length; i++) {
				classArgoments[i] = instances[i].getClass();
				values[i] = instances[i];
			}
		}
		return getObject(methodName, nomeRegistro, readContenutoAllegati, connectionPdD, classArgoments, values);
	}
	public Object getObject(String methodName,
			String nomeRegistro,
			Boolean readContenutoAllegati,
			Connection connectionPdD,
			Class<?>[] classArgoments, Object[] values) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{


		// Effettuo le query nella mia gerarchia di registri.
		Object obj = null;
		//Recupero la classe specificata dal parametro passato
		if(nomeRegistro!=null){
			this.log.debug("Cerco nel registro ["+nomeRegistro+"]");
			try{
				//org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.driverRegistroServizi.get(nomeRegistro);
				org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.getDriver(connectionPdD, nomeRegistro);
				this.log.debug("invocazione metodo ["+methodName+"]...");
				if(classArgoments==null || classArgoments.length==0){
					Method method =  null;
					if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
						method = driver.getClass().getMethod(methodName,boolean.class);
						obj = method.invoke(driver,readContenutoAllegati.booleanValue());
					}else{
						method = driver.getClass().getMethod(methodName);
						obj = method.invoke(driver);
					}
				}else if(classArgoments.length==1){
					Method method =  null;
					if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
						method = driver.getClass().getMethod(methodName,classArgoments[0],boolean.class);
						obj = method.invoke(driver,values[0],readContenutoAllegati.booleanValue());
					}else{
						method = driver.getClass().getMethod(methodName,classArgoments[0]);
						obj = method.invoke(driver,values[0]);
					}
				}else if(classArgoments.length==2){
					Method method =  null;
					if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
						method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],boolean.class);
						obj = method.invoke(driver,values[0],values[1],readContenutoAllegati.booleanValue());
					}else{
						method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1]);
						obj = method.invoke(driver,values[0],values[1]);
					}
				}else if(classArgoments.length==3){
					Method method =  null;
					if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
						method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],classArgoments[2],boolean.class);
						obj = method.invoke(driver,values[0],values[1],values[2],readContenutoAllegati.booleanValue());
					}else{
						method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],classArgoments[2]);
						obj = method.invoke(driver,values[0],values[1],values[2]);
					}
				}else if(classArgoments.length==4){
					
					Class<?> cArg2 = classArgoments[2];
					if("getSoggettoByCredenzialiApiKey".equals(methodName)) {
						cArg2 = boolean.class;
					}
					
					Method method =  null;
					if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
						method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],cArg2,classArgoments[3],boolean.class);
						obj = method.invoke(driver,values[0],values[1],values[2],values[3],readContenutoAllegati.booleanValue());
					}else{
						method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],cArg2,classArgoments[3]);
						obj = method.invoke(driver,values[0],values[1],values[2],values[3]);
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
							List<Fruitore> fruitoriConStatoNonBozza = new ArrayList<Fruitore>();
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
			StringBuilder notFoundProblem = new StringBuilder();
			StringBuilder exceptionProblem = new StringBuilder();
			boolean find = false;
			for (Enumeration<?> en = this.driverRegistroServizi.keys() ; en.hasMoreElements() ;) {
				String nomeRegInLista= (String) en.nextElement();
				this.log.debug("Cerco nel registro con nome["+nomeRegInLista+"]");
				try{
					//org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.driverRegistroServizi.get(nomeRegInLista);
					org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.getDriver(connectionPdD, nomeRegInLista);
					this.log.debug("invocazione metodo ["+methodName+"] nel registro["+nomeRegInLista+"]...");
					if(classArgoments==null || classArgoments.length==0){
						Method method =  null;
						if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
							method = driver.getClass().getMethod(methodName,boolean.class);
							obj = method.invoke(driver,readContenutoAllegati.booleanValue());
						}else{
							method = driver.getClass().getMethod(methodName);
							obj = method.invoke(driver);
						}
						find = true;
					}else if(classArgoments.length==1){
						Method method =  null;
						if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
							method = driver.getClass().getMethod(methodName,classArgoments[0],boolean.class);
							obj = method.invoke(driver,values[0],readContenutoAllegati.booleanValue());
						}else{
							method = driver.getClass().getMethod(methodName,classArgoments[0]);
							obj = method.invoke(driver,values[0]);
						}
						find = true;
					}else if(classArgoments.length==2){
						Method method =  null;
						if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
							method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],boolean.class);
							obj = method.invoke(driver,values[0],values[1],readContenutoAllegati.booleanValue());
						}else{
							method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1]);
							obj = method.invoke(driver,values[0],values[1]);
						}
						find = true;
					}else if(classArgoments.length==3){
						Method method =  null;
						if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
							method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],classArgoments[2],boolean.class);
							obj = method.invoke(driver,values[0],values[1],values[2],readContenutoAllegati.booleanValue());
						}else{
							method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],classArgoments[2]);
							obj = method.invoke(driver,values[0],values[1],values[2]);
						}
						find = true;
					}else if(classArgoments.length==4){
						
						Class<?> cArg2 = classArgoments[2];
						if("getSoggettoByCredenzialiApiKey".equals(methodName)) {
							cArg2 = boolean.class;
						}
						
						Method method =  null;
						if((driver instanceof DriverRegistroServiziDB) && (readContenutoAllegati!=null) ){
							method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],cArg2,classArgoments[3],boolean.class);
							obj = method.invoke(driver,values[0],values[1],values[2],values[3],readContenutoAllegati.booleanValue());
						}else{
							method = driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],cArg2,classArgoments[3]);
							obj = method.invoke(driver,values[0],values[1],values[2],values[3]);
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
								List<Fruitore> fruitoriConStatoNonBozza = new ArrayList<Fruitore>();
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
	
	private String _getKey_getAccordoServizioParteComune(IDAccordo idAccordo,Boolean readContenutiAllegati) throws DriverRegistroServiziException{
		String uriAccordoServizio = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
		String key = "getAccordoServizio_" + uriAccordoServizio;
		if(readContenutiAllegati!=null){
			key = key + "_READ-ALLEGATI("+readContenutiAllegati.booleanValue()+")";
		}
		return key;
	}
	public AccordoServizioParteComune getAccordoServizioParteComune(Connection connectionPdD,String nomeRegistro,IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getAccordoServizioParteComune(connectionPdD, nomeRegistro, idAccordo, null);
	}
	public AccordoServizioParteComune getAccordoServizioParteComune(Connection connectionPdD,String nomeRegistro,IDAccordo idAccordo,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune]: Parametro non definito");	
		if(idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune]: Nome accordo di servizio non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getAccordoServizioParteComune(idAccordo, readContenutiAllegati);
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
			throw new DriverRegistroServiziNotFound("[getAccordoServizio] Accordo di Servizio ["+idAccordo+"] non trovato");



	}

	
	private String _getKey_getPortaDominio(String nomePdD) throws DriverRegistroServiziException{
		return "getPortaDominio_" + nomePdD;
	}
	public org.openspcoop2.core.registry.PortaDominio getPortaDominio(Connection connectionPdD,String nomeRegistro,String nomePdD) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		// Raccolta dati
		if(nomePdD == null)
			throw new DriverRegistroServiziException("[getPortaDominio]: Parametro non definito");	

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getPortaDominio(nomePdD);
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
	
	
	private String _getKey_getRuolo(String nomeRuolo) throws DriverRegistroServiziException{
		return "getRuolo_" + nomeRuolo;
	}
	public org.openspcoop2.core.registry.Ruolo getRuolo(Connection connectionPdD,String nomeRegistro,String nomeRuolo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		// Raccolta dati
		if(nomeRuolo == null)
			throw new DriverRegistroServiziException("[getRuolo]: Parametro non definito");	
		IDRuolo idRuolo = new IDRuolo(nomeRuolo);
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getRuolo(nomeRuolo);
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((Ruolo) response.getObject());
				}
			}
		}


		// Algoritmo CACHE
		Ruolo ruolo = null;
		if(this.cache!=null){
			ruolo = (Ruolo) this.getObjectCache(key,"getRuolo",nomeRegistro,null,connectionPdD,idRuolo);
		}else{
			ruolo = (Ruolo) this.getObject("getRuolo",nomeRegistro,null,connectionPdD,idRuolo);
		}

		if(ruolo!=null)
			return ruolo;
		else
			throw new DriverRegistroServiziNotFound("[getRuolo] Ruolo ["+nomeRuolo+"] non trovato");

	}
	
	
	private String _getKey_getScope(String nomeScope) throws DriverRegistroServiziException{
		return "getScope_" + nomeScope;
	}
	public org.openspcoop2.core.registry.Scope getScope(Connection connectionPdD,String nomeRegistro,String nomeScope) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		// Raccolta dati
		if(nomeScope == null)
			throw new DriverRegistroServiziException("[getScope]: Parametro non definito");	
		IDScope idScope = new IDScope(nomeScope);
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getScope(nomeScope);
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((Scope) response.getObject());
				}
			}
		}


		// Algoritmo CACHE
		Scope scope = null;
		if(this.cache!=null){
			scope = (Scope) this.getObjectCache(key,"getScope",nomeRegistro,null,connectionPdD,idScope);
		}else{
			scope = (Scope) this.getObject("getScope",nomeRegistro,null,connectionPdD,idScope);
		}

		if(scope!=null)
			return scope;
		else
			throw new DriverRegistroServiziNotFound("[getScope] Scope ["+nomeScope+"] non trovato");

	}
	
	private String _getKey_getSoggetto(IDSoggetto idSoggetto) throws DriverRegistroServiziException{
		return "getSoggetto_" + idSoggetto.getTipo() +"/" + idSoggetto.getNome();
	}
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
			key = this._getKey_getSoggetto(idSoggetto);
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
			throw new DriverRegistroServiziNotFound("[getSoggetto] Soggetto non trovato");

	}

	private String _getKey_getSoggettoByCredenzialiBasic(String user,String password) throws DriverRegistroServiziException{
		return "getSoggettoByCredenzialiBasic_" + user +"_" + password;
	}
	public Soggetto getSoggettoByCredenzialiBasic(Connection connectionPdD,String nomeRegistro,String user,String password, CryptConfig config) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(user==null)
			throw new DriverRegistroServiziException("[getSoggettoByCredenzialiBasic] Parametro user Non Valido");
		if(password==null)
			throw new DriverRegistroServiziException("[getSoggettoByCredenzialiBasic] Parametro password Non Valido");

		//	se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getSoggettoByCredenzialiBasic(user, password);
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
			soggetto = (Soggetto) this.getObjectCache(key,"getSoggettoByCredenzialiBasic",nomeRegistro,null,connectionPdD,user,password,config);
		}else{
			soggetto = (Soggetto) this.getObject("getSoggettoByCredenzialiBasic",nomeRegistro,null,connectionPdD,user,password,config);
		}

		if(soggetto!=null)
			return soggetto;
		else
			throw new DriverRegistroServiziNotFound("[getSoggettoByCredenzialiBasic] Soggetto non Trovato");

	}
	
	private String _getKey_getSoggettoByCredenzialiApiKey(String user,String password, boolean appId) throws DriverRegistroServiziException{
		return (appId ? "getSoggettoByCredenzialiMultipleApiKey_" : "getSoggettoByCredenzialiApiKey_") + user +"_" + password;
	}
	public Soggetto getSoggettoByCredenzialiApiKey(Connection connectionPdD,String nomeRegistro,String user,String password, boolean appId, CryptConfig config) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(user==null)
			throw new DriverRegistroServiziException("[getSoggettoByCredenzialiApiKey] Parametro user Non Valido");
		if(password==null)
			throw new DriverRegistroServiziException("[getSoggettoByCredenzialiApiKey] Parametro password Non Valido");

		//	se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getSoggettoByCredenzialiApiKey(user, password, appId);
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
			soggetto = (Soggetto) this.getObjectCache(key,"getSoggettoByCredenzialiApiKey",nomeRegistro,null,connectionPdD,user,password,appId,config);
		}else{
			soggetto = (Soggetto) this.getObject("getSoggettoByCredenzialiApiKey",nomeRegistro,null,connectionPdD,user,password,appId,config);
		}

		if(soggetto!=null)
			return soggetto;
		else
			throw new DriverRegistroServiziNotFound("[getSoggettoByCredenzialiApiKey] Soggetto non Trovato");

	}
	
	private String _getKey_getSoggettoByCredenzialiSsl(String aSubject, String Issuer){
		String key = "getSoggettoByCredenzialiSsl";
		key = key +"_subject:"+aSubject;
		if(Issuer!=null) {
			key = key +"_issuer:"+Issuer;
		}
		else {
			key = key +"_issuer:nonDefinito";
		}
		return key;
	}
	public Soggetto getSoggettoByCredenzialiSsl(Connection connectionPdD,String nomeRegistro,String subject, String issuer) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(subject==null)
			throw new DriverRegistroServiziException("[getSoggettoByCredenzialiSsl] Parametro subject Non Valido");

		//	se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getSoggettoByCredenzialiSsl(subject, issuer);
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
		Class<?>[] classArguments = new Class[] {String.class, String.class};
		Object[]values = new Object[] {subject,issuer}; // passo gli argomenti tramite array poich' aIssuer puo' essere null
		Soggetto soggetto = null;
		if(this.cache!=null){
			soggetto = (Soggetto) this.getObjectCache(key,"getSoggettoByCredenzialiSsl",nomeRegistro,null,connectionPdD,classArguments, values);
		}else{
			soggetto = (Soggetto) this.getObject("getSoggettoByCredenzialiSsl",nomeRegistro,null,connectionPdD,classArguments, values);
		}

		if(soggetto!=null)
			return soggetto;
		else
			throw new DriverRegistroServiziNotFound("[getSoggettoByCredenzialiSsl] Soggetto non Trovato");

	}
	
	private String _getKey_getSoggettoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws DriverRegistroServiziException{
		try {
			String key = "getSoggettoByCredenzialiSslCert";
			key = key +"_cert:"+certificate.digestBase64Encoded();
			key = key +"_strictVerifier:"+strictVerifier;
			return key;
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	public Soggetto getSoggettoByCredenzialiSsl(Connection connectionPdD,String nomeRegistro,CertificateInfo certificate, boolean strictVerifier) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(certificate==null)
			throw new DriverRegistroServiziException("[getSoggettoByCredenzialiSsl] Parametro certificate Non Valido");

		//	se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getSoggettoByCredenzialiSsl(certificate, strictVerifier);
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
		Class<?>[] classArguments = new Class[] {CertificateInfo.class, boolean.class};
		Object[]values = new Object[] {certificate , strictVerifier};
		Soggetto soggetto = null;
		if(this.cache!=null){
			soggetto = (Soggetto) this.getObjectCache(key,"getSoggettoByCredenzialiSsl",nomeRegistro,null,connectionPdD, classArguments, values);
		}else{
			soggetto = (Soggetto) this.getObject("getSoggettoByCredenzialiSsl",nomeRegistro,null,connectionPdD, classArguments, values);
		}

		if(soggetto!=null)
			return soggetto;
		else
			throw new DriverRegistroServiziNotFound("[getSoggettoByCredenzialiSsl] Soggetto non Trovato");

	}
	
	private String _getKey_getSoggettoByCredenzialiPrincipal(String principal) throws DriverRegistroServiziException{
		return "getSoggettoByCredenzialiPrincipal_" + principal;
	}
	public Soggetto getSoggettoByCredenzialiPrincipal(Connection connectionPdD,String nomeRegistro,String principal) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(principal==null)
			throw new DriverRegistroServiziException("[getSoggettoByCredenzialiPrincipal] Parametro principal Non Valido");

		//	se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getSoggettoByCredenzialiPrincipal(principal);
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
			soggetto = (Soggetto) this.getObjectCache(key,"getSoggettoByCredenzialiPrincipal",nomeRegistro,null,connectionPdD,principal);
		}else{
			soggetto = (Soggetto) this.getObject("getSoggettoByCredenzialiPrincipal",nomeRegistro,null,connectionPdD,principal);
		}

		if(soggetto!=null)
			return soggetto;
		else
			throw new DriverRegistroServiziNotFound("[getSoggettoByCredenzialiPrincipal] Soggetto non Trovato");

	}
	
	private String _getKey_getAccordoServizioParteSpecifica(IDServizio idService,Boolean readContenutiAllegati) throws DriverRegistroServiziException{
		String servizio = idService.getNome();
		String tipoServizio = idService.getTipo();
		Integer versioneServizio = idService.getVersione();
		String azione = idService.getAzione();
		String tipoSogg = idService.getSoggettoErogatore().getTipo();
		String nomeSogg = idService.getSoggettoErogatore().getNome();
		
		String key = "getServizio_"+ tipoSogg +"/" + nomeSogg +
				"_" + tipoServizio + "/" + servizio+"/"+versioneServizio.intValue();
		if(azione !=null)
			key = key + "_" + azione;	   
		if(readContenutiAllegati!=null){
			key = key + "_READ-ALLEGATI("+readContenutiAllegati.booleanValue()+")";
		}
		return key;
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(Connection connectionPdD,String nomeRegistro,IDServizio idService) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getAccordoServizioParteSpecifica(connectionPdD,nomeRegistro, idService, null);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(Connection connectionPdD,String nomeRegistro,IDServizio idService,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idService == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametro Non Valido");
		String servizio = idService.getNome();
		String tipoServizio = idService.getTipo();
		Integer versioneServizio = idService.getVersione();
		if(servizio == null || tipoServizio == null || versioneServizio==null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametri (Servizio) Non Validi");
		String tipoSogg = idService.getSoggettoErogatore().getTipo();
		String nomeSogg = idService.getSoggettoErogatore().getNome();
		if(tipoSogg == null || nomeSogg == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametri (Soggetto) Non Validi");

		//	se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getAccordoServizioParteSpecifica(idService, readContenutiAllegati);
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
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica] Servizio non trovato");
	}

	
	private String _getKey_getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto, IDAccordo idAccordo,Boolean readContenutiAllegati) throws DriverRegistroServiziException{
		String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
		String key = "getServizioCorrelato(RicercaPerAccordo)_"+ idSoggetto.getTipo() +"/" + idSoggetto.getNome() +"_" + uriAccordo;
		if(readContenutiAllegati!=null){
			key = key + "_READ-ALLEGATI("+readContenutiAllegati.booleanValue()+")";
		}
		return key;
	}
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

		//	se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto, idAccordo, readContenutiAllegati);
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
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] ServizioCorrelato non trovato");
	}
	
	private String _getKey_getAccordoCooperazione(IDAccordoCooperazione idAccordo,Boolean readContenutiAllegati) throws DriverRegistroServiziException{
		String uriAccordoCooperazione = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo);

		String key = "getAccordoCooperazione_" + uriAccordoCooperazione;   
		if(readContenutiAllegati!=null){
			key = key + "_READ-ALLEGATI("+readContenutiAllegati.booleanValue()+")";
		}
		return key;
	}
	public AccordoCooperazione getAccordoCooperazione(Connection connectionPdD,String nomeRegistro,IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getAccordoCooperazione(connectionPdD,nomeRegistro, idAccordo, null);
	}
	public AccordoCooperazione getAccordoCooperazione(Connection connectionPdD,String nomeRegistro,IDAccordoCooperazione idAccordo,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione]: Parametro non definito");	
		if(idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione]: Nome accordo di servizio non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getAccordoCooperazione(idAccordo, readContenutiAllegati);
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
			throw new DriverRegistroServiziNotFound("[getAccordoCooperazione] Accordo non trovato");
	}

	
	
	
	
	/* ********  R I C E R C A  I D   E L E M E N T I   P R I M I T I V I  ******** */
	
	@SuppressWarnings("unchecked")
	public List<String> getAllIdPorteDominio(Connection connectionPdD,String nomeRegistro,FiltroRicerca filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return (List<String>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdPorteDominio");
	}
	
	@SuppressWarnings("unchecked")
	public List<IDRuolo> getAllIdRuoli(Connection connectionPdD,String nomeRegistro,FiltroRicercaRuoli filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return (List<IDRuolo>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdRuoli");
	}
	
	@SuppressWarnings("unchecked")
	public List<IDScope> getAllIdScope(Connection connectionPdD,String nomeRegistro,FiltroRicercaScope filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return (List<IDScope>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdScope");
	}

	@SuppressWarnings("unchecked")
	public List<IDSoggetto> getAllIdSoggetti(Connection connectionPdD,String nomeRegistro, FiltroRicercaSoggetti filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return (List<IDSoggetto>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdSoggetti");
	}
	
	@SuppressWarnings("unchecked")
	public List<IDAccordoCooperazione> getAllIdAccordiCooperazione(Connection connectionPdD,String nomeRegistro, FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return (List<IDAccordoCooperazione>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdAccordiCooperazione");
	}
	
	@SuppressWarnings("unchecked")
	public List<IDAccordo> getAllIdAccordiServizioParteComune(Connection connectionPdD,String nomeRegistro, FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return (List<IDAccordo>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdAccordiServizioParteComune");
	}
	
	@SuppressWarnings("unchecked")
	public List<IDPortType> getAllIdPortType(Connection connectionPdD,String nomeRegistro, FiltroRicercaPortTypes filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return (List<IDPortType>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdPortType");
	}
	
	@SuppressWarnings("unchecked")
	public List<IDPortTypeAzione> getAllIdAzionePortType(Connection connectionPdD, String nomeRegistro, FiltroRicercaOperations filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return (List<IDPortTypeAzione>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdAzionePortType");
	}
	
	@SuppressWarnings("unchecked")
	public List<IDAccordoAzione> getAllIdAzioneAccordo(Connection connectionPdD, String nomeRegistro, FiltroRicercaAzioni filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return (List<IDAccordoAzione>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdAzioneAccordo");
	}
	
	@SuppressWarnings("unchecked")
	public List<IDResource> getAllIdResource(Connection connectionPdD, String nomeRegistro, FiltroRicercaResources filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return (List<IDResource>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdResource");
	}
	
	@SuppressWarnings("unchecked")
	public List<IDServizio> getAllIdServizi(Connection connectionPdD, String nomeRegistro, FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return (List<IDServizio>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdServizi");
	}
	
	@SuppressWarnings("unchecked")
	public List<IDFruizione> getAllIdFruizioniServizio(Connection connectionPdD, String nomeRegistro, FiltroRicercaFruizioniServizio filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return (List<IDFruizione>) _getAllIdEngine(connectionPdD, nomeRegistro, filtroRicerca, "getAllIdFruizioniServizio");
	}
	
	private List<?> _getAllIdEngine(Connection connectionPdD,String nomeRegistro,Object filtroRicerca,String nomeMetodo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		
		// Raccolta dati
		if(filtroRicerca == null)
			throw new DriverRegistroServiziException("["+nomeMetodo+"]: Parametro non definito");	

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = nomeMetodo+"_" + filtroRicerca.toString();   
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((List<?>) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		List<?> list = null;
		if(this.cache!=null){
			list = (List<?>) this.getObjectCache(key,nomeMetodo,nomeRegistro,null,connectionPdD,filtroRicerca);
		}else{
			list = (List<?>) this.getObject(nomeMetodo,nomeRegistro,null,connectionPdD,filtroRicerca);
		}

		if(list!=null)
			return list;
		else
			throw new DriverRegistroServiziNotFound("["+nomeMetodo+"] Elementi non trovati");
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
			IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchemaXSD)
	throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idService == null)
			throw new DriverRegistroServiziException("[getWsdlAccordoServizio] Parametro Non Valido");
		String servizio = idService.getNome();
		String tipoServizio = idService.getTipo();
		Integer versioneServizio = idService.getVersione();
		if(servizio == null || tipoServizio == null || versioneServizio==null)
			throw new DriverRegistroServiziException("[getWsdlAccordoServizio] Parametri (Servizio) Non Validi");
		String tipoSogg = idService.getSoggettoErogatore().getTipo();
		String nomeSogg = idService.getSoggettoErogatore().getNome();
		if(tipoSogg == null || nomeSogg == null)
			throw new DriverRegistroServiziException("[getWsdlAccordoServizio] Parametri (Soggetto) Non Validi");

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getWsdlAccordoServizio_"+infoWsdlSource.name()+"_"+ tipoSogg +"/" + nomeSogg +
			"_" + tipoServizio + "/" + servizio+"/"+versioneServizio.intValue()
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
			wsdlAS = this.getAccordoServizioSoapCache(key, idService, infoWsdlSource, nomeRegistro, connectionPdD,buildSchemaXSD);
		}else{
			wsdlAS = this.getAccordoServizioSoapEngine(idService, infoWsdlSource, nomeRegistro, connectionPdD,buildSchemaXSD);
		}

		if(wsdlAS!=null)
			return wsdlAS;
		else
			throw new DriverRegistroServiziNotFound("[getWsdlAccordoServizio] WSDLAccordoServizio non trovato");
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
	private synchronized org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getAccordoServizioSoapCache(String keyCache,IDServizio idService,
			InformationApiSource infoWsdlSource,String nomeRegistro,Connection connectionPdD,boolean buildSchemaXSD) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

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
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:getWSDLAccordoServizio) nel registro["+nomeRegistro+"] in cache.");
						return (org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper) response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:getWSDLAccordoServizio) nel registro["+nomeRegistro+"] in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.log.debug("oggetto con chiave ["+keyCache+"] (method:WSDLAccordoServizio) nel registro["+nomeRegistro+"] non in cache, ricerco nel registro...");
			try{
				obj = getAccordoServizioSoapEngine(idService, infoWsdlSource,nomeRegistro,connectionPdD,buildSchemaXSD);
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
	private org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getAccordoServizioSoapEngine(IDServizio idService,InformationApiSource infoWsdlSource,
			String nomeRegistro,Connection connectionPdD,boolean buildSchemaXSD) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		_ASWrapperDati asWrapper = this.buildASWrapperDati(nomeRegistro, idService, connectionPdD);
		
		// Effettuo le query nella mia gerarchia di registri.
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio = asWrapper.servizio;
		org.openspcoop2.core.registry.AccordoServizioParteComune as = asWrapper.as;

		//Recupero la classe specificata dal parametro passato
		boolean registroServiziDB = asWrapper.registroServiziDB;



		// Costruisco oggetto
		org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper accordoServizioWrapper = new org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper();

		IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(as);
		accordoServizioWrapper.setIdAccordoServizio(idAccordo);
		
		accordoServizioWrapper.setNomePortType(servizio.getPortType());
		
		accordoServizioWrapper.setTipologiaServizio(servizio.getTipologiaServizio());
		
		accordoServizioWrapper.setAccordoServizio(as);
		
		accordoServizioWrapper.setLocationWsdlImplementativoErogatore(servizio.getWsdlImplementativoErogatore());
		accordoServizioWrapper.setLocationWsdlImplementativoFruitore(servizio.getWsdlImplementativoFruitore());
		accordoServizioWrapper.setBytesWsdlImplementativoErogatore(servizio.getByteWsdlImplementativoErogatore());
		accordoServizioWrapper.setBytesWsdlImplementativoFruitore(servizio.getByteWsdlImplementativoFruitore());
		
		OpenSPCoop2MessageFactory defaultMessageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		AccordoServizioWrapperUtilities wsdlWrapperUtilities = new AccordoServizioWrapperUtilities(defaultMessageFactory, this.log);
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
			if(InformationApiSource.SPECIFIC.equals(infoWsdlSource)){
				
				this.log.debug("Costruisco WSDLAccordoServizio from WSDL...");
				_loadFromWsdl(servizio, registroServiziDB, wsdlWrapperUtilities);
				
			}else if(InformationApiSource.REGISTRY.equals(infoWsdlSource)){
				
				this.log.debug("Costruisco WSDLAccordoServizio from AccordoServizio...");
				wsdlWrapperUtilities.buildAccordoServizioWrapperFromOpenSPCoopAS(as);
				
			}
			else if(InformationApiSource.SPECIFIC_REGISTRY.equals(infoWsdlSource) || 
					InformationApiSource.SAFE_SPECIFIC_REGISTRY.equals(infoWsdlSource)){
			
				this.log.debug("Costruisco WSDLAccordoServizio from WSDL (Step1)...");
				try{
					_loadFromWsdl(servizio, registroServiziDB, wsdlWrapperUtilities);
				}catch(DriverRegistroServiziException e){
					if(InformationApiSource.SPECIFIC_REGISTRY.equals(infoWsdlSource)){
						throw e;
					}
				}
				
				this.log.debug("Costruisco WSDLAccordoServizio from AccordoServizio (Step2)...");
				AccordoServizioWrapperUtilities wsdlWrapperUtilitiesStep2 = new AccordoServizioWrapperUtilities(defaultMessageFactory, this.log);
				AccordoServizioWrapper accordoServizioWrapperStep2 = accordoServizioWrapper.clone(false);
				wsdlWrapperUtilitiesStep2.setAccordoServizio(accordoServizioWrapperStep2);
				try{
					wsdlWrapperUtilitiesStep2.buildAccordoServizioWrapperFromOpenSPCoopAS(as);
				}catch(DriverRegistroServiziException e){
					if(InformationApiSource.SPECIFIC_REGISTRY.equals(infoWsdlSource)){
						throw e;
					}
				}
				
				this.log.debug("Costruisco WSDLAccordoServizio merge WSDL e AccordoServizio (Step3)...");
				_merge(accordoServizioWrapper, accordoServizioWrapperStep2);
				
			}
			else if(InformationApiSource.REGISTRY_SPECIFIC.equals(infoWsdlSource) || 
					InformationApiSource.SAFE_REGISTRY_SPECIFIC.equals(infoWsdlSource)){
				
				this.log.debug("Costruisco WSDLAccordoServizio from AccordoServizio (Step1)...");
				try{
					wsdlWrapperUtilities.buildAccordoServizioWrapperFromOpenSPCoopAS(as);
				}catch(DriverRegistroServiziException e){
					if(InformationApiSource.REGISTRY_SPECIFIC.equals(infoWsdlSource)){
						throw e;
					}
				}
				
				this.log.debug("Costruisco WSDLAccordoServizio from WSDL (Step2)...");
				AccordoServizioWrapperUtilities wsdlWrapperUtilitiesStep2 = new AccordoServizioWrapperUtilities(defaultMessageFactory, this.log);
				AccordoServizioWrapper accordoServizioWrapperStep2 = accordoServizioWrapper.clone(false);
				wsdlWrapperUtilitiesStep2.setAccordoServizio(accordoServizioWrapperStep2);
				try{
					_loadFromWsdl(servizio, registroServiziDB, wsdlWrapperUtilitiesStep2);
				}catch(DriverRegistroServiziException e){
					if(InformationApiSource.REGISTRY_SPECIFIC.equals(infoWsdlSource)){
						throw e;
					}
				}
					
				this.log.debug("Costruisco WSDLAccordoServizio merge  AccordoServizio e WSDL (Step3)...");
				_merge(accordoServizioWrapper, accordoServizioWrapperStep2);
								
			}
		}catch(DriverRegistroServiziException e){
			if(TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio())){
				throw new DriverRegistroServiziException("[WSDL-FRUITORE] "+e.getMessage(),e);
			}else{
				throw new DriverRegistroServiziException("[WSDL-EROGATORE] "+e.getMessage(),e);
			}
		}

		this.log.debug("invocazione metodo getWSDLAccordoServizio completata.");
		return accordoServizioWrapper;
	}

	private void _loadFromWsdl(AccordoServizioParteSpecifica servizio, boolean registroServiziDB, AccordoServizioWrapperUtilities wsdlWrapperUtilities) throws DriverRegistroServiziException{
		if(TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio())){
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Si occupa di ritornare le informazioni sulla specifica REST di un servizio
	 * 
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.rest.AccordoServizioWrapper} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 */
	public org.openspcoop2.core.registry.rest.AccordoServizioWrapper getRestAccordoServizio(Connection connectionPdD,String nomeRegistro,
			IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchemaXSD, boolean processIncludeForOpenApi)
	throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idService == null)
			throw new DriverRegistroServiziException("[getRestAccordoServizio] Parametro Non Valido");
		String servizio = idService.getNome();
		String tipoServizio = idService.getTipo();
		Integer versioneServizio = idService.getVersione();
		if(servizio == null || tipoServizio == null || versioneServizio==null)
			throw new DriverRegistroServiziException("[getRestAccordoServizio] Parametri (Servizio) Non Validi");
		String tipoSogg = idService.getSoggettoErogatore().getTipo();
		String nomeSogg = idService.getSoggettoErogatore().getNome();
		if(tipoSogg == null || nomeSogg == null)
			throw new DriverRegistroServiziException("[getRestAccordoServizio] Parametri (Soggetto) Non Validi");

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getRestAccordoServizio_"+infoWsdlSource.name()+"_"+ tipoSogg +"/" + nomeSogg +
			"_" + tipoServizio + "/" + servizio+"/"+versioneServizio.intValue()
			+"_schema_"+buildSchemaXSD
			+"_processInclude_"+processIncludeForOpenApi;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((org.openspcoop2.core.registry.rest.AccordoServizioWrapper) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		org.openspcoop2.core.registry.rest.AccordoServizioWrapper restAS = null;
		if(this.cache!=null){
			restAS = this.getAccordoServizioRestCache(key, idService, infoWsdlSource, nomeRegistro, connectionPdD,buildSchemaXSD, processIncludeForOpenApi);
		}else{
			restAS = this.getAccordoServizioRestEngine(idService, infoWsdlSource, nomeRegistro, connectionPdD,buildSchemaXSD, processIncludeForOpenApi);
		}

		if(restAS!=null)
			return restAS;
		else
			throw new DriverRegistroServiziNotFound("[getRestAccordoServizio] API non trovata");
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
	private synchronized org.openspcoop2.core.registry.rest.AccordoServizioWrapper getAccordoServizioRestCache(String keyCache,IDServizio idService,
			InformationApiSource infoWsdlSource,String nomeRegistro,Connection connectionPdD,boolean buildSchemaXSD, boolean processIncludeForOpenApi) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		DriverRegistroServiziNotFound dNotFound = null;
		org.openspcoop2.core.registry.rest.AccordoServizioWrapper obj = null;
		try{

//			System.out.println("@"+keyCache+"@ INFO CACHE: "+this.cache.toString());
//			System.out.println("@"+keyCache+"@ KEYS: \n\t"+this.cache.printKeys("\n\t"));
			
			// Raccolta dati
			if(keyCache == null)
				throw new DriverRegistroServiziException("[getRestAccordoServizio]: KeyCache non definita");	

			// se e' attiva una cache provo ad utilizzarla
			if(this.cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:getRestAccordoServizio) nel registro["+nomeRegistro+"] in cache.");
						return (org.openspcoop2.core.registry.rest.AccordoServizioWrapper) response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:getRestAccordoServizio) nel registro["+nomeRegistro+"] in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.log.debug("oggetto con chiave ["+keyCache+"] (method:getRestAccordoServizio) nel registro["+nomeRegistro+"] non in cache, ricerco nel registro...");
			try{
				obj = getAccordoServizioRestEngine(idService, infoWsdlSource,nomeRegistro,connectionPdD,buildSchemaXSD, processIncludeForOpenApi);
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
					throw new Exception("Metodo (getRestAccordoServizio) nel registro["+nomeRegistro+"] ha ritornato un valore null");
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
	private org.openspcoop2.core.registry.rest.AccordoServizioWrapper getAccordoServizioRestEngine(IDServizio idService,InformationApiSource infoWsdlSource,
			String nomeRegistro,Connection connectionPdD,boolean buildSchemi,boolean processIncludeForOpenApi) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{


		_ASWrapperDati asWrapper = this.buildASWrapperDati(nomeRegistro, idService, connectionPdD);
		
		// Effettuo le query nella mia gerarchia di registri.
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio = asWrapper.servizio;
		org.openspcoop2.core.registry.AccordoServizioParteComune as = asWrapper.as;

		//Recupero la classe specificata dal parametro passato
		boolean registroServiziDB = asWrapper.registroServiziDB;



		// Costruisco oggetto
		org.openspcoop2.core.registry.rest.AccordoServizioWrapper accordoServizioWrapper = new org.openspcoop2.core.registry.rest.AccordoServizioWrapper();

		IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(as);
		accordoServizioWrapper.setIdAccordoServizio(idAccordo);
		
		accordoServizioWrapper.setAccordoServizio(as);
		boolean processInclude = buildSchemi;
		if(buildSchemi && as!=null && FormatoSpecifica.OPEN_API_3.equals(as.getFormatoSpecifica())) {
			processInclude = processIncludeForOpenApi;
		}
		
		accordoServizioWrapper.setLocationSpecifica(as.getWsdlConcettuale());
		accordoServizioWrapper.setBytesSpecifica(as.getByteWsdlConcettuale());
		
		accordoServizioWrapper.setRegistroServiziDB(asWrapper.registroServiziDB);
		
		OpenSPCoop2MessageFactory defaultMessageFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		
		org.openspcoop2.core.registry.rest.AccordoServizioWrapperUtilities wsdlWrapperUtilities = 
				new org.openspcoop2.core.registry.rest.AccordoServizioWrapperUtilities(defaultMessageFactory, this.log, accordoServizioWrapper);
			

		try{
			if(InformationApiSource.SPECIFIC.equals(infoWsdlSource)){
				
				this.log.debug("Costruisco API tramite la specifica...");
				wsdlWrapperUtilities.buildApiFromSpecific(registroServiziDB, buildSchemi, processInclude);
				
			}else if(InformationApiSource.REGISTRY.equals(infoWsdlSource)){
				
				this.log.debug("Costruisco API tramite il registro...");
				wsdlWrapperUtilities.buildApiFromRegistry(registroServiziDB, buildSchemi);
				
			}
			else if(InformationApiSource.SPECIFIC_REGISTRY.equals(infoWsdlSource) || 
					InformationApiSource.SAFE_SPECIFIC_REGISTRY.equals(infoWsdlSource)){
			
				this.log.debug("Costruisco API tramite la specifica (Step1)...");
				try{
					wsdlWrapperUtilities.buildApiFromSpecific(registroServiziDB, buildSchemi, processInclude);
				}catch(DriverRegistroServiziException e){
					if(InformationApiSource.SPECIFIC_REGISTRY.equals(infoWsdlSource)){
						throw e;
					}
					else {
						String errorMsg = "Costruisco API tramite il registro (Step1) error: "+e.getMessage();
						if(org.openspcoop2.core.registry.rest.AccordoServizioWrapperUtilities.API_SENZA_SPECIFICA.equals(e.getMessage())) {
							this.log.warn(errorMsg,e);
						}
						else {
							this.log.error(errorMsg,e);
						}
					}
				}
				
				this.log.debug("Costruisco API tramite il registro (Step2)...");
				org.openspcoop2.core.registry.rest.AccordoServizioWrapper accordoServizioWrapperStep2 = accordoServizioWrapper.clone();
				org.openspcoop2.core.registry.rest.AccordoServizioWrapperUtilities wsdlWrapperUtilitiesStep2 = 
						new org.openspcoop2.core.registry.rest.AccordoServizioWrapperUtilities(defaultMessageFactory, this.log, accordoServizioWrapperStep2);
				try{
					wsdlWrapperUtilitiesStep2.buildApiFromRegistry(registroServiziDB, buildSchemi);
				}catch(DriverRegistroServiziException e){
					if(InformationApiSource.SPECIFIC_REGISTRY.equals(infoWsdlSource)){
						throw e;
					}
					else {
						String errorMsg = "Costruisco API tramite il registro (Step2) error: "+e.getMessage();
						if(org.openspcoop2.core.registry.rest.AccordoServizioWrapperUtilities.API_SENZA_SPECIFICA.equals(e.getMessage())) {
							this.log.warn(errorMsg,e);
						}
						else {
							this.log.error(errorMsg,e);
						}
					}
				}
				
				this.log.debug("Costruisco API merge specifica e registro (Step3)...");
				_merge(accordoServizioWrapper, accordoServizioWrapperStep2);
				
			}
			else if(InformationApiSource.REGISTRY_SPECIFIC.equals(infoWsdlSource) || 
					InformationApiSource.SAFE_REGISTRY_SPECIFIC.equals(infoWsdlSource)){
				
				this.log.debug("Costruisco API tramite il registro (Step1)...");
				try{
					wsdlWrapperUtilities.buildApiFromRegistry(registroServiziDB, buildSchemi);
				}catch(DriverRegistroServiziException e){
					if(InformationApiSource.REGISTRY_SPECIFIC.equals(infoWsdlSource)){
						throw e;
					}
					else {
						String errorMsg = "Costruisco API tramite il registro (Step1) error: "+e.getMessage();
						if(org.openspcoop2.core.registry.rest.AccordoServizioWrapperUtilities.API_SENZA_SPECIFICA.equals(e.getMessage())) {
							this.log.warn(errorMsg,e);
						}
						else {
							this.log.error(errorMsg,e);
						}
					}
				}
				
				this.log.debug("Costruisco API tramite la specifica (Step2)...");
				org.openspcoop2.core.registry.rest.AccordoServizioWrapper accordoServizioWrapperStep2 = accordoServizioWrapper.clone();
				org.openspcoop2.core.registry.rest.AccordoServizioWrapperUtilities wsdlWrapperUtilitiesStep2 = 
						new org.openspcoop2.core.registry.rest.AccordoServizioWrapperUtilities(defaultMessageFactory, this.log, accordoServizioWrapperStep2);
				try{
					wsdlWrapperUtilitiesStep2.buildApiFromSpecific(registroServiziDB, buildSchemi, processInclude);
				}catch(DriverRegistroServiziException e){
					if(InformationApiSource.REGISTRY_SPECIFIC.equals(infoWsdlSource)){
						throw e;
					}
					else {
						String errorMsg = "Costruisco API tramite il registro (Step2) error: "+e.getMessage();
						if(org.openspcoop2.core.registry.rest.AccordoServizioWrapperUtilities.API_SENZA_SPECIFICA.equals(e.getMessage())) {
							this.log.warn(errorMsg,e);
						}
						else {
							this.log.error(errorMsg,e);
						}
					}
				}
					
				this.log.debug("Costruisco WSDLAccordoServizio merge  AccordoServizio e WSDL (Step3)...");
				_merge(accordoServizioWrapper, accordoServizioWrapperStep2);
								
			}
		}catch(DriverRegistroServiziException e){
			if(TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio())){
				throw new DriverRegistroServiziException("[CORRELATO] "+e.getMessage(),e);
			}else{
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}

		this.log.debug("invocazione metodo getWSDLAccordoServizio completata.");
		return accordoServizioWrapper;
	}
	
	private void _merge(org.openspcoop2.core.registry.rest.AccordoServizioWrapper asAwrapper, org.openspcoop2.core.registry.rest.AccordoServizioWrapper asBwrapper){
		AccordoServizioParteComune asA = asAwrapper.getAccordoServizio();
		AccordoServizioParteComune asB = asBwrapper.getAccordoServizio();
		for (int i = 0; i < asB.sizeResourceList(); i++) {
			Resource resourceB = asB.getResource(i);
			boolean exists = false;
			for (int j = 0; j < asA.sizeResourceList(); j++) {
				Resource resourceA = asA.getResource(j);
				if(resourceB.getNome().equals(resourceA.getNome())){
					
					exists = true;
					break;
				}
			}
			if(!exists){
				//System.out.println("ADD PT ["+portTypeB.getNome()+"]");
				asA.addResource(resourceB);
			}
		}
		
		// merge API
		if(asAwrapper.getApi()==null) {
			asAwrapper.setApi(asBwrapper.getApi());
		}
		else {
			if(asBwrapper.getApi()!=null) {
				for (int i = 0; i < asBwrapper.getApi().sizeOperations(); i++) {
					ApiOperation apiOpB = asBwrapper.getApi().getOperation(i);
					boolean exists = false;
					for (int j = 0; j < asAwrapper.getApi().sizeOperations(); j++) {
						ApiOperation apiOpA = asAwrapper.getApi().getOperation(j);
						
						if(apiOpA.getPath()==null) {
							if(apiOpB.getPath()!=null) {
								continue;
							}
						}
						else if(!apiOpA.getPath().equals(apiOpB.getPath())) {
							continue;
						}
						
						if(apiOpA.getHttpMethod()==null) {
							if(apiOpB.getHttpMethod()!=null) {
								continue;
							}
						}
						else {
							if(apiOpB.getHttpMethod()==null) {
								continue;
							}
							if(!apiOpA.getHttpMethod().equals(apiOpB.getHttpMethod())) {
								continue;
							}
						}
						exists = true;
						break;
					}
					if(!exists){
						//System.out.println("ADD OP ["+apiOpB.getHttpMethod()+"] ["+apiOpB.getPath()+"]");
						asAwrapper.getApi().addOperation(apiOpB);
					}
				}
			}
		}
	}
	
	
	
	
	
	
	/**
	 * Si occupa di ritornare il tipo di service binding del servizio
	 * 
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.constants.ServiceBinding} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 */
	public org.openspcoop2.core.registry.constants.ServiceBinding getServiceBinding(Connection connectionPdD,String nomeRegistro,IDServizio idService)
	throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(idService == null)
			throw new DriverRegistroServiziException("[getServiceBinding] Parametro Non Valido");
		String servizio = idService.getNome();
		String tipoServizio = idService.getTipo();
		Integer versioneServizio = idService.getVersione();
		if(servizio == null || tipoServizio == null || versioneServizio==null)
			throw new DriverRegistroServiziException("[getServiceBinding] Parametri (Servizio) Non Validi");
		String tipoSogg = idService.getSoggettoErogatore().getTipo();
		String nomeSogg = idService.getSoggettoErogatore().getNome();
		if(tipoSogg == null || nomeSogg == null)
			throw new DriverRegistroServiziException("[getServiceBinding] Parametri (Soggetto) Non Validi");

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getServiceBinding_"+tipoSogg +"/" + nomeSogg +
			"_" + tipoServizio + "/" + servizio+"/"+versioneServizio.intValue();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((org.openspcoop2.core.registry.constants.ServiceBinding) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		org.openspcoop2.core.registry.constants.ServiceBinding sb = null;
		if(this.cache!=null){
			sb = this.getServiceBindingCache(key, idService, nomeRegistro, connectionPdD);
		}else{
			sb = this.getServiceBindingEngine(idService, nomeRegistro, connectionPdD);
		}

		if(sb!=null)
			return sb;
		else
			throw new DriverRegistroServiziNotFound("[getServiceBinding] API non trovata");
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
	private synchronized org.openspcoop2.core.registry.constants.ServiceBinding getServiceBindingCache(String keyCache,IDServizio idService,
			String nomeRegistro,Connection connectionPdD) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		DriverRegistroServiziNotFound dNotFound = null;
		org.openspcoop2.core.registry.constants.ServiceBinding obj = null;
		try{

//			System.out.println("@"+keyCache+"@ INFO CACHE: "+this.cache.toString());
//			System.out.println("@"+keyCache+"@ KEYS: \n\t"+this.cache.printKeys("\n\t"));
			
			// Raccolta dati
			if(keyCache == null)
				throw new DriverRegistroServiziException("[getServiceBinding]: KeyCache non definita");	

			// se e' attiva una cache provo ad utilizzarla
			if(this.cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.log.debug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (method:getServiceBinding) nel registro["+nomeRegistro+"] in cache.");
						return (org.openspcoop2.core.registry.constants.ServiceBinding) response.getObject();
					}else if(response.getException()!=null){
						this.log.debug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (method:getServiceBinding) nel registro["+nomeRegistro+"] in cache.");
						throw (Exception) response.getException();
					}else{
						this.log.error("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.log.debug("oggetto con chiave ["+keyCache+"] (method:getServiceBinding) nel registro["+nomeRegistro+"] non in cache, ricerco nel registro...");
			try{
				obj = getServiceBindingEngine(idService, nomeRegistro,connectionPdD);
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
					throw new Exception("Metodo (getServiceBinding) nel registro["+nomeRegistro+"] ha ritornato un valore null");
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
	
	private org.openspcoop2.core.registry.constants.ServiceBinding getServiceBindingEngine(IDServizio idService, String nomeRegistro,Connection connectionPdD) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		_ASWrapperDati asWrapper = this.buildASWrapperDati(nomeRegistro, idService, connectionPdD);
		return asWrapper.as.getServiceBinding();
	}
	
	
	
	
	
	
	private _ASWrapperDati buildASWrapperDati(String nomeRegistro, IDServizio idService, Connection connectionPdD) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
			// Logger log, IDServizio idService, Connection connectionPdD, IDAccordoFactory idAccordoFactory) {
		
		_ASWrapperDati asWrapper = new _ASWrapperDati();
		
		if(nomeRegistro!=null){
			this.log.debug("Cerco wsdl nel registro ["+nomeRegistro+"]");
			try{
				String uriServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idService);
				
				//org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.driverRegistroServizi.get(nomeRegistro);
				org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.getDriver(connectionPdD, nomeRegistro);
				this.log.debug("invocazione metodo getWSDLAccordoServizio (search servizio)...");
				// ricerca servizio richiesto
				try{
					if(driver instanceof DriverRegistroServiziDB)
						asWrapper.servizio = ((DriverRegistroServiziDB)driver).getAccordoServizioParteSpecifica(idService,true); // leggo contenuto allegati
					else
						asWrapper.servizio = driver.getAccordoServizioParteSpecifica(idService);	
				}catch(DriverRegistroServiziNotFound e){}
				if(asWrapper.servizio == null){
					throw new DriverRegistroServiziNotFound("Servizio ["+uriServizio+"] non definito");
				}
				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(asWrapper.servizio.getAccordoServizioParteComune());
				this.log.debug("invocazione metodo getWSDLAccordoServizio (search accordo)...");
				if(driver instanceof DriverRegistroServiziDB)
					asWrapper.as = ((DriverRegistroServiziDB)driver).getAccordoServizioParteComune(idAccordo,true); // leggo contenuto allegati
				else
					asWrapper.as = driver.getAccordoServizioParteComune(idAccordo);
				if (asWrapper.as == null){
					throw new DriverRegistroServiziNotFound("Accordo di servizio ["+idAccordo+"] associato al servizio ["+uriServizio+"] non presente nel registro");
				}
				if(asWrapper.servizio.getPortType()!=null && ("".equals(asWrapper.servizio.getPortType())==false)){
					// verifico presenza portType in accordo di servizio
					boolean findPortType = false;
					for(int l=0;l<asWrapper.as.sizePortTypeList();l++){
						if(asWrapper.servizio.getPortType().equals(asWrapper.as.getPortType(l).getNome())){
							findPortType = true;
							break;
						}
					}
					if(findPortType==false){
						throw new DriverRegistroServiziNotFound("PortType["+asWrapper.servizio.getPortType()+"] associato al servizio ["+uriServizio+"] non presente nell'Accordo di servizio ["+idAccordo+"]");
					}
				}

				// trovato!
				asWrapper.registroServiziDB = (driver instanceof DriverRegistroServiziDB);
				
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
			StringBuilder notFoundProblem = new StringBuilder();
			StringBuilder exceptionProblem = new StringBuilder();
			boolean find = false;
			for (Enumeration<?> en = this.driverRegistroServizi.keys() ; en.hasMoreElements() ;) {
				String nomeRegInLista= (String) en.nextElement();
				this.log.debug("Cerco nel registro con nome["+nomeRegInLista+"]");
				try{
					String uriServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idService);
					
					//org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.driverRegistroServizi.get(nomeRegInLista);
					org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet driver = this.getDriver(connectionPdD, nomeRegInLista);
					this.log.debug("invocazione metodo getWSDLAccordoServizio (search servizio) nel registro["+nomeRegInLista+"]...");
					// ricerca servizio richiesto
					try{
						if(driver instanceof DriverRegistroServiziDB)
							asWrapper.servizio = ((DriverRegistroServiziDB)driver).getAccordoServizioParteSpecifica(idService,true); // leggo contenuto allegati
						else
							asWrapper.servizio = driver.getAccordoServizioParteSpecifica(idService);	
					}catch(DriverRegistroServiziNotFound e){}
					if(asWrapper.servizio == null){
						throw new DriverRegistroServiziNotFound("Servizio ["+uriServizio+"] non definito");
					}
					IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(asWrapper.servizio.getAccordoServizioParteComune());
					this.log.debug("invocazione metodo getWSDLAccordoServizio (search accordo) nel registro["+nomeRegInLista+"]...");
					if(driver instanceof DriverRegistroServiziDB)
						asWrapper.as = ((DriverRegistroServiziDB)driver).getAccordoServizioParteComune(idAccordo,true); // leggo contenuto allegati
					else
						asWrapper.as = driver.getAccordoServizioParteComune(idAccordo);
					if (asWrapper.as == null){
						throw new DriverRegistroServiziNotFound("Accordo di servizio ["+idAccordo+"] associato al servizio ["+uriServizio+"] non presente nel registro");
					}
					if(asWrapper.servizio.getPortType()!=null && ("".equals(asWrapper.servizio.getPortType())==false)){
						// verifico presenza portType in accordo di servizio
						boolean findPortType = false;
						for(int l=0;l<asWrapper.as.sizePortTypeList();l++){
							if(asWrapper.servizio.getPortType().equals(asWrapper.as.getPortType(l).getNome())){
								findPortType = true;
								break;
							}
						}
						if(findPortType==false){
							throw new DriverRegistroServiziNotFound("PortType["+asWrapper.servizio.getPortType()+"] associato al servizio ["+uriServizio+"] non presente nell'Accordo di servizio ["+idAccordo+"]");
						}
					}
					
					// trovato!
					asWrapper.registroServiziDB = (driver instanceof DriverRegistroServiziDB);
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
		
		return asWrapper;
	}
	
	
	
	
	
	
	
	public Serializable pushGenericObject(String keyObject, Serializable object) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if(this.cache!=null){
			return this._pushGenericObject(keyObject, object);
		}
		return object;
	}
	
	private synchronized Serializable _pushGenericObject(String keyObject, Serializable object) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		// Raccolta dati
		if(keyObject == null)
			throw new DriverRegistroServiziException("[getGenericObject]: Parametro non definito");	
		
		// se e' attiva una cache provo ad utilizzarla per vedere se un altro thread lo ha già aggiunto
		String key = null;	
		key = "getGenericObject_" + keyObject;
		org.openspcoop2.utils.cache.CacheResponse response = 
			(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
		if(response != null){
			if(response.getException()!=null){
				if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
					throw (DriverRegistroServiziNotFound) response.getException();
				else
					throw (DriverRegistroServiziException) response.getException();
			}else{
				return ((Serializable) response.getObject());
			}
		}
		
		// Aggiungo la risposta in cache
		try{	
			org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
			responseCache.setObject(object);
			this.cache.put(key,responseCache);
		}catch(UtilsException e){
			this.log.error("Errore durante l'inserimento in cache ["+key+"]: "+e.getMessage());
		}
		
		return object; 
		
	}
	
	public Serializable getGenericObject(String keyObject) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// Raccolta dati
		if(keyObject == null)
			throw new DriverRegistroServiziException("[getGenericObject]: Parametro non definito");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = "getGenericObject_" + keyObject;
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(DriverRegistroServiziNotFound.class.getName().equals(response.getException().getClass().getName()))
						throw (DriverRegistroServiziNotFound) response.getException();
					else
						throw (DriverRegistroServiziException) response.getException();
				}else{
					return ((Serializable) response.getObject());
				}
			}
		}

		throw new DriverRegistroServiziNotFound("GenericObject with key ["+keyObject+"] not found");

	}

}

class _ASWrapperDati {
	
	// Effettuo le query nella mia gerarchia di registri.
	org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio = null;
	org.openspcoop2.core.registry.AccordoServizioParteComune as = null;

	//Recupero la classe specificata dal parametro passato
	boolean registroServiziDB = false;
	
	
}
