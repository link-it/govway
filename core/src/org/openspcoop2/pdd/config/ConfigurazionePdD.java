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



package org.openspcoop2.pdd.config;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.utils.FiltroRicercaAllarmi;
import org.openspcoop2.core.byok.IDriverBYOKConfig;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.AccessoDatiAttributeAuthority;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoDatiRichieste;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.ProprietaOggetto;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.PluginCostanti;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.monitor.engine.dynamic.IRegistroPluginsReader;
import org.openspcoop2.monitor.sdk.alarm.AlarmStatus;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.byok.DriverBYOK;
import org.openspcoop2.pdd.core.byok.DriverBYOKUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.config.PolicyConfiguration;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.pdd.core.dynamic.TemplateSource;
import org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione;
import org.openspcoop2.protocol.engine.ConfigurazioneFiltroServiziApplicativi;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.cache.CacheResponse;
import org.openspcoop2.utils.cache.CacheType;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.FileSystemUtilities;
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

public class ConfigurazionePdD  {
	
	/** Fonti su cui effettuare le query:
	 * - CACHE
	 * - configurazione
	 */
	private Cache cache = null;
	public Cache getCache() {
		return this.cache;
	}

	private IDriverConfigurazioneGet driverConfigurazionePdD;

	private boolean useConnectionPdD = false;
	private String tipoDatabase = null;

	/** Logger utilizzato per debug. */
	private Logger logger = null;
	private void logDebug(String msg) {
		this.logDebug(msg, null);
	}
	private void logDebug(String msg, Throwable e) {
		if(this.logger!=null) {
			if(e!=null) {
				this.logger.debug(msg, e);
			}
			else {
				this.logger.debug(msg);
			}
		}
	}
	private void logInfo(String msg) {
		this.logInfo(msg, null);
	}
	private void logInfo(String msg, Throwable e) {
		if(this.logger!=null) {
			if(e!=null) {
				this.logger.info(msg, e);
			}
			else {
				this.logger.info(msg);
			}
		}
	}
	private void logWarn(String msg) {
		this.logWarn(msg, null);
	}
	private void logWarn(String msg, Throwable e) {
		if(this.logger!=null) {
			if(e!=null) {
				this.logger.warn(msg, e);
			}
			else {
				this.logger.warn(msg);
			}
		}
	}
	private void logError(String msg) {
		this.logError(msg, null);
	}
	private void logError(String msg, Throwable e) {
		if(this.logger!=null) {
			if(e!=null) {
				this.logger.error(msg, e);
			}
			else {
				this.logger.error(msg);
			}
		}
	}

	private static String notFoundClassName = DriverConfigurazioneNotFound.class.getName()+"";
	private static String excClassName = DriverConfigurazioneException.class.getName()+"";

	/** Variabili statiche contenenti le configurazioni
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
	public static void setAccessoRegistro(AccessoRegistro accessoRegistro) {
		ConfigurazionePdD.accessoRegistro = accessoRegistro;
	}
	private static AccessoConfigurazione accessoConfigurazione = null;
	public static void setAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) {
		ConfigurazionePdD.accessoConfigurazione = accessoConfigurazione;
	}
	private static AccessoDatiAutorizzazione accessoDatiAutorizzazione = null;
	public static void setAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) {
		ConfigurazionePdD.accessoDatiAutorizzazione = accessoDatiAutorizzazione;
	}
	private static AccessoDatiAutenticazione accessoDatiAutenticazione = null;
	public static void setAccessoDatiAutenticazione(AccessoDatiAutenticazione accessoDatiAutenticazione) {
		ConfigurazionePdD.accessoDatiAutenticazione = accessoDatiAutenticazione;
	}
	private static AccessoDatiGestioneToken accessoDatiGestioneToken = null;
	public static void setAccessoDatiGestioneToken(AccessoDatiGestioneToken accessoDatiGestioneToken) {
		ConfigurazionePdD.accessoDatiGestioneToken = accessoDatiGestioneToken;
	}
	private static AccessoDatiAttributeAuthority accessoDatiAttributeAuthority = null;
	public static void setAccessoDatiAttributeAuthority(AccessoDatiAttributeAuthority accessoDatiAttributeAuthority) {
		ConfigurazionePdD.accessoDatiAttributeAuthority = accessoDatiAttributeAuthority;
	}
	private static AccessoDatiKeystore accessoDatiKeystore = null;
	public static void setAccessoDatiKeystore(AccessoDatiKeystore accessoDatiKeystore) {
		ConfigurazionePdD.accessoDatiKeystore = accessoDatiKeystore;
	}
	private static AccessoDatiRichieste accessoDatiRichieste = null;
	public static void setAccessoDatiRichieste(AccessoDatiRichieste accessoDatiRichieste) {
		ConfigurazionePdD.accessoDatiRichieste = accessoDatiRichieste;
	}

	/** ConfigurazioneDinamica */
	private boolean configurazioneDinamica = false;

	/** OpenSPCoop Properties */
	private OpenSPCoop2Properties openspcoopProperties = null;

	/** ConfigLocalProperties */
	private ConfigLocalProperties configLocalProperties = null;
	
	/** ConfigurazionePdD_controlloTraffico */
	private ConfigurazionePdD_controlloTraffico configurazionePdD_controlloTraffico = null;
	
	/** ConfigurazionePdD_plugins */
	private ConfigurazionePdD_plugins configurazionePdD_plugins = null;
	private ConfigurazionePdD_registroPlugins configurazionePdD_registroPlugins = null;

	/** ConfigurazionePdD_allarmi */
	private ConfigurazionePdD_allarmi configurazionePdD_allarmi = null;
	
	/** Configurazione token con https */
	private Boolean _tokenWithHttpsEnabledInitialized = null;
	private boolean _tokenWithHttpsEnabled = false;
	private synchronized void initializeTokenWithHttpsEnabled()throws DriverConfigurazioneException {
		if(this._tokenWithHttpsEnabledInitialized==null) {
			// Inizializzo parametri relativi al protocollo
			// basta un protocollo che lo supporta per doverli cercare anche con la funzionalita' abilitata
			try {
				this._tokenWithHttpsEnabled = false;
				for(IProtocolFactory<?> protocolFactory: ProtocolFactoryManager.getInstance().getProtocolFactories().values()) {
					if(protocolFactory.createProtocolConfiguration().isSupportatoAutenticazioneApplicativiHttpsConToken()) {
						this._tokenWithHttpsEnabled = true;
						break;
					}
				}
				this._tokenWithHttpsEnabledInitialized = true;
			}catch(Throwable t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
	}
	private boolean isTokenWithHttpsEnabled() throws DriverConfigurazioneException {
		if(this._tokenWithHttpsEnabledInitialized==null) {
			initializeTokenWithHttpsEnabled();
		}
		return this._tokenWithHttpsEnabled;
	}


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
				this.cache = new Cache(CacheType.JCS, CostantiConfigurazione.CACHE_CONFIGURAZIONE_PDD); // lascio JCS come default abilitato via jmx
				this.cache.build();
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
	}
	public void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond, CryptConfig config) throws DriverConfigurazioneException{
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
				initCacheConfigurazione(CacheType.JCS, configurazioneCache, null, false, config); // lascio JCS come default abilitato via jmx
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
	public List<String> keysCache() throws DriverConfigurazioneException{
		if(this.cache!=null){
			try{
				return this.cache.keys();
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
	public Object getRawObjectCache(String key) throws DriverConfigurazioneException{
		if(this.cache!=null){
			try{
				Object o = this.cache.get(key);
				if(o!=null){
					if(o instanceof CacheResponse) {
						CacheResponse cR = (CacheResponse) o;
						if(cR.getObject()!=null) {
							o = cR.getObject();
						}
						else if(cR.getException()!=null) {
							o = cR.getException();
						}
					}
					return o;
				}else{
					return null;
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
			String jndiNameDatasourcePdD, boolean forceDisableCache, boolean useOp2UtilsDatasource, boolean bindJMX, 
			boolean prefillCache, CryptConfig configApplicativi,
			CacheType cacheType)throws DriverConfigurazioneException{

		try{ 
			// Inizializzo OpenSPCoopProperties
			this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
			this.configurazioneDinamica = this.openspcoopProperties.isConfigurazioneDinamica();

			if(alog!=null)
				this.logger = alog;
			else
				this.logger = LoggerWrapperFactory.getLogger(ConfigurazionePdD.class);

			String msg = "Leggo configurazione di tipo["+accessoConfigurazione.getTipo()+"]   location["+accessoConfigurazione.getLocation()+"]";
			this.logInfo(msg);
			if(alogConsole!=null)
				alogConsole.info(msg);
			
			// inizializzazione XML
			if(CostantiConfigurazione.CONFIGURAZIONE_XML.equalsIgnoreCase(accessoConfigurazione.getTipo())){
				this.driverConfigurazionePdD = new DriverConfigurazioneXML(accessoConfigurazione.getLocation(),this.logger);
				if(this.driverConfigurazionePdD ==null || ((DriverConfigurazioneXML)this.driverConfigurazionePdD).create==false){
					throw new DriverConfigurazioneException("Riscontrato errore durante l'inizializzazione della configurazione di tipo "+
							accessoConfigurazione.getTipo()+" con location: "+accessoConfigurazione.getLocation());
				}
			} 

			// inizializzazione DB
			else if(CostantiConfigurazione.CONFIGURAZIONE_DB.equalsIgnoreCase(accessoConfigurazione.getTipo())){			
				this.driverConfigurazionePdD = new DriverConfigurazioneDB(accessoConfigurazione.getLocation(),accessoConfigurazione.getContext(),
						this.logger,accessoConfigurazione.getTipoDatabase(),accessoConfigurazione.isCondivisioneDatabasePddRegistro(),
						useOp2UtilsDatasource, bindJMX);
				if(this.driverConfigurazionePdD ==null || ((DriverConfigurazioneDB)this.driverConfigurazionePdD).create==false){
					throw new DriverConfigurazioneException("Riscontrato errore durante l'inizializzazione della configurazione di tipo "+
							accessoConfigurazione.getTipo()+" con location: "+accessoConfigurazione.getLocation());
				}
				this.useConnectionPdD = jndiNameDatasourcePdD.equals(accessoConfigurazione.getLocation());
				this.tipoDatabase = accessoConfigurazione.getTipoDatabase();
				
				this.configurazionePdD_controlloTraffico = new ConfigurazionePdD_controlloTraffico(this.openspcoopProperties, 
						((DriverConfigurazioneDB)this.driverConfigurazionePdD),
						this.useConnectionPdD);
				
				if(this.openspcoopProperties.isConfigurazionePluginsEnabled()) {
					this.configurazionePdD_plugins = new ConfigurazionePdD_plugins(this.openspcoopProperties, 
							((DriverConfigurazioneDB)this.driverConfigurazionePdD),
							this.useConnectionPdD);
					this.configurazionePdD_registroPlugins = new ConfigurazionePdD_registroPlugins(this.openspcoopProperties, 
							((DriverConfigurazioneDB)this.driverConfigurazionePdD),
							this.useConnectionPdD);
				}
				
				if(this.openspcoopProperties.isAllarmiEnabled()) {
					this.configurazionePdD_allarmi = new ConfigurazionePdD_allarmi(this.openspcoopProperties, 
							((DriverConfigurazioneDB)this.driverConfigurazionePdD),
							this.useConnectionPdD);
				}
			} 

			// tipo di configurazione non conosciuto
			else{
				throw new DriverConfigurazioneException("Riscontrato errore durante l'inizializzazione della configurazione di tipo sconosciuto "+
						accessoConfigurazione.getTipo()+" con location: "+accessoConfigurazione.getLocation());
			}

			
			// Inizializzazione ConfigLocalProperties
			this.configLocalProperties = new ConfigLocalProperties(this.logger, this.openspcoopProperties.getRootDirectory(),localProperties);
			

			// Inizializzazione della Cache
			AccessoConfigurazione accessoDatiConfigurazione = null;
			try{
				accessoDatiConfigurazione = this.driverConfigurazionePdD.getAccessoConfigurazione();
			}catch(DriverConfigurazioneNotFound notFound){
				// ignore
			}
			if(accessoDatiConfigurazione!=null && accessoDatiConfigurazione.getCache()!=null){
				if(forceDisableCache==false){
					initCacheConfigurazione(cacheType, accessoDatiConfigurazione.getCache(),alogConsole, 
							prefillCache, configApplicativi);
				}
			}

			if(this.driverConfigurazionePdD instanceof IDriverBYOKConfig) {
				DriverBYOK driverBYOK = DriverBYOKUtilities.newInstanceDriverBYOKRuntimeNode(this.logger, false, true);
				if(driverBYOK!=null) {
					IDriverBYOKConfig c = (IDriverBYOKConfig) this.driverConfigurazionePdD;
					c.initialize(driverBYOK, false, true);
				}
			}
			
		}catch(Exception e){
			String msg = "Riscontrato errore durante l'inizializzazione della configurazione di OpenSPCoop: "+e.getMessage();
			this.logError(msg,e);
			if(alogConsole!=null)
				alogConsole.info(msg);
			throw new DriverConfigurazioneException("Riscontrato errore durante l'inizializzazione della configurazione di OpenSPCoop: "+e.getMessage());
		}
	}

	private void initCacheConfigurazione(CacheType cacheType, org.openspcoop2.core.config.Cache configurazioneCache,Logger alogConsole, 
			boolean prefillCache, CryptConfig configApplicativi)throws Exception{
		this.cache = new Cache(cacheType, CostantiConfigurazione.CACHE_CONFIGURAZIONE_PDD);

		String msg = null;
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
					msg = "Dimensione della cache (ConfigurazionePdD) impostata al valore: "+dimensione;
					if(prefillCache){
						msg = "[Prefill Enabled] " + msg;
					}
					if(prefillCache){
						this.logWarn(msg);
					}
					else{
						this.logInfo(msg);
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
						msg = "[Prefill Enabled] Dimensione della cache (ConfigurazionePdD) impostata al valore: "+dimensione;
						this.logWarn(msg);
						if(alogConsole!=null){
							alogConsole.warn(msg);
						}
						this.cache.setCacheSize(dimensione);
					}
				}
			}catch(Exception error){
				throw new DriverConfigurazioneException("Parametro errato per la dimensione della cache (ConfigurazionePdD): "+error.getMessage());
			}
			
			if( configurazioneCache.getAlgoritmo() != null ){
				msg = "Algoritmo di cache (ConfigurazionePdD) impostato al valore: "+configurazioneCache.getAlgoritmo();
				this.logInfo(msg);
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
					msg = "Attributo 'IdleTime' (ConfigurazionePdD) impostato al valore: "+itemIdleTime;
					if(prefillCache){
						msg = "[Prefill Enabled] " + msg;
					}
					if(prefillCache){
						this.logWarn(msg);
					}
					else{
						this.logInfo(msg);
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
						msg = "[Prefill Enabled] Attributo 'IdleTime' (ConfigurazionePdD) impostato al valore: "+itemIdleTime;
						this.logWarn(msg);
						if(alogConsole!=null){
							alogConsole.warn(msg);
						}
						this.cache.setItemIdleTime(itemIdleTime);
					}
				}
			}catch(Exception error){
				throw new DriverConfigurazioneException("Parametro errato per l'attributo 'IdleTime' (ConfigurazionePdD): "+error.getMessage());
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
					msg = "Attributo 'MaxLifeSecond' (ConfigurazionePdD) impostato al valore: "+itemLifeSecond;
					if(prefillCache){
						msg = "[Prefill Enabled] " + msg;
					}
					if(prefillCache){
						this.logWarn(msg);
					}
					else{
						this.logInfo(msg);
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
						msg = "Attributo 'MaxLifeSecond' (ConfigurazionePdD) impostato al valore: "+itemLifeSecond;
						this.logWarn(msg);
						if(alogConsole!=null){
							alogConsole.warn(msg);
						}
						this.cache.setItemLifeTime(itemLifeSecond);
					}
				}
			}catch(Exception error){
				throw new DriverConfigurazioneException("Parametro errato per l'attributo 'MaxLifeSecond' (ConfigurazionePdD): "+error.getMessage());
			}

		}
		
		this.cache.build();
		
		if(prefillCache){
			this.prefillCache(null,alogConsole, configApplicativi);
		}
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	public void disableSyncronizedGet() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		this.cache.disableSyncronizedGet();
	}
	@SuppressWarnings("deprecation")
	@Deprecated
	public boolean isDisableSyncronizedGet() throws UtilsException {
		if(this.cache==null) {
			throw new UtilsException("Cache disabled");
		}
		return this.cache.isDisableSyncronizedGet();
	}
	

	protected IDriverConfigurazioneGet getDriverConfigurazionePdD() {
		return this.driverConfigurazionePdD;
	} 


	
	public void prefillCache(Connection connectionPdD,Logger alogConsole, CryptConfig configApplicativi){
		
		String msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD) in corso ...";
		this.logInfo(msg);
		if(alogConsole!=null){
			alogConsole.info(msg);
		}
		
		try{
			this.cache.remove(_getKey_isForwardProxyEnabled());
			this.isForwardProxyEnabled();
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		
		// *** Soggetti ***
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), recupero soggetti ...";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		FiltroRicercaSoggetti filtroRicercaSoggetti = new FiltroRicercaSoggetti();
		List<IDSoggetto> idSoggetti = null;
		try{
			idSoggetti = this.driverConfigurazionePdD.getAllIdSoggetti(filtroRicercaSoggetti);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(DriverConfigurazioneException e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), recuperati "+(idSoggetti!=null ? idSoggetti.size() : 0)+" soggetti";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		if(idSoggetti!=null && idSoggetti.size()>0){
			
			msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura di "+idSoggetti.size()+" soggetti ...";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			for (IDSoggetto idSoggetto : idSoggetti) {
				
				try{
					this.cache.remove(_getKey_getSoggettoByID(idSoggetto));
					this.getSoggetto(connectionPdD, idSoggetto);
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
			}
			
			msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura di "+idSoggetti.size()+" soggetti completata";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
		}
		
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), recupero porte delegate ...";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		FiltroRicercaPorteDelegate filtroPorteDelegate = new FiltroRicercaPorteDelegate();
		List<IDPortaDelegata> idPDs = null;
		try{
			idPDs = this.driverConfigurazionePdD.getAllIdPorteDelegate(filtroPorteDelegate);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(DriverConfigurazioneException e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), recuperate "+(idPDs!=null ? idPDs.size() : 0)+" porte delegate";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		if(idPDs!=null && idPDs.size()>0){
			
			msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura di "+idPDs.size()+" porte delegate ...";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			for (IDPortaDelegata idPortaDelegata : idPDs) {
				
				msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura dati della porta delegata ["+idPortaDelegata+"] ...";
				this.logDebug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				try{
					this.cache.remove(_getKey_getIDPortaDelegata(idPortaDelegata.getNome()));
					this.getIDPortaDelegata(connectionPdD, idPortaDelegata.getNome());
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
				try{
					this.cache.remove(_getKey_getPortaDelegata(idPortaDelegata));
					this.getPortaDelegata(connectionPdD, idPortaDelegata);
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
				PortaDelegata pd = null;
				try{
					pd = this.driverConfigurazionePdD.getPortaDelegata(idPortaDelegata);
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(DriverConfigurazioneException e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
				if(pd!=null){
					
					IDSoggetto idSoggettoProprietario = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
										
					IDServizioApplicativo idSA_anonimo = new IDServizioApplicativo();
					idSA_anonimo.setIdSoggettoProprietario(idSoggettoProprietario);
					idSA_anonimo.setNome(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO);
					try{
						this.cache.remove(_getKey_getServizioApplicativo(idSA_anonimo));
						this.getServizioApplicativo(connectionPdD, idSA_anonimo);
					}
					catch(DriverConfigurazioneNotFound notFound){
						// ignore
					}
					catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
					
					if(pd.sizeServizioApplicativoList()>0){
						for (PortaDelegataServizioApplicativo saPD : pd.getServizioApplicativoList()) {
							
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setIdSoggettoProprietario(idSoggettoProprietario);
							idSA.setNome(saPD.getNome());
							
							try{
								this.cache.remove(_getKey_getServizioApplicativo(idSA));
								this.getServizioApplicativo(connectionPdD, idSA);
							}
							catch(DriverConfigurazioneNotFound notFound){
								// ignore
							}
							catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
							
							ServizioApplicativo sa = null;
							try{
								sa = this.driverConfigurazionePdD.getServizioApplicativo(idSA);
							}
							catch(DriverConfigurazioneNotFound notFound){
								// ignore
							}
							catch(DriverConfigurazioneException e){this.logError("[prefill] errore"+e.getMessage(),e);}
									
							if(sa!=null){
								if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>0){
									for (Credenziali credenziale : sa.getInvocazionePorta().getCredenzialiList()) {
										if(CredenzialeTipo.BASIC.equals(credenziale.getTipo())){
											try{
												this.cache.remove(_getKey_getServizioApplicativoByCredenzialiBasic(credenziale.getUser(), credenziale.getPassword(), null));
												this.getServizioApplicativoByCredenzialiBasic(connectionPdD, credenziale.getUser(), credenziale.getPassword(), configApplicativi);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}				
										}
										else if(CredenzialeTipo.APIKEY.equals(credenziale.getTipo())){
											try{
												this.cache.remove(_getKey_getServizioApplicativoByCredenzialiApiKey(credenziale.getUser(), credenziale.getPassword(), credenziale.isCertificateStrictVerification(), null));
												this.getServizioApplicativoByCredenzialiApiKey(connectionPdD, credenziale.getUser(), credenziale.getPassword(), credenziale.isCertificateStrictVerification(), configApplicativi);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}				
										}
										else if(CredenzialeTipo.SSL.equals(credenziale.getTipo())){
											if(credenziale.getSubject()!=null) {
												try{
													if(Costanti.MODIPA_PROTOCOL_NAME.equals(sa.getTipoSoggettoProprietario())) {
														ConfigurazioneFiltroServiziApplicativi filtroFirma = ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiModIFirma();
														this.cache.remove(_getKey_getServizioApplicativoByCredenzialiSsl(credenziale.getSubject(), credenziale.getIssuer(), 
																filtroFirma.getTipiSoggetti(), filtroFirma.isIncludiApplicativiNonModI(), filtroFirma.isIncludiApplicativiModIEsterni(), filtroFirma.isIncludiApplicativiModIInterni()));
														this.getServizioApplicativoByCredenzialiSsl(connectionPdD, credenziale.getSubject(), credenziale.getIssuer(), 
																filtroFirma.getTipiSoggetti(), filtroFirma.isIncludiApplicativiNonModI(), filtroFirma.isIncludiApplicativiModIEsterni(), filtroFirma.isIncludiApplicativiModIInterni());
													}
												} catch(DriverConfigurazioneNotFound notFound){
													// ignore
												}
												catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}	
												try {
													ConfigurazioneFiltroServiziApplicativi filtroHttps = ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps();
													this.cache.remove(_getKey_getServizioApplicativoByCredenzialiSsl(credenziale.getSubject(), credenziale.getIssuer(), 
															filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni()));
													this.getServizioApplicativoByCredenzialiSsl(connectionPdD, credenziale.getSubject(), credenziale.getIssuer(), 
															filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni());
												}
												catch(DriverConfigurazioneNotFound notFound){
													// ignore
												}
												catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}	
											}
											if(credenziale.getCertificate()!=null) {
												try{
													CertificateInfo certificato = ArchiveLoader.load(ArchiveType.CER, credenziale.getCertificate(), 0, null).getCertificate();
													if(Costanti.MODIPA_PROTOCOL_NAME.equals(sa.getTipoSoggettoProprietario())) {
														ConfigurazioneFiltroServiziApplicativi filtroFirma = ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiModIFirma();
														this.cache.remove(_getKey_getServizioApplicativoByCredenzialiSsl(certificato, credenziale.isCertificateStrictVerification(), 
																filtroFirma.getTipiSoggetti(), filtroFirma.isIncludiApplicativiNonModI(), filtroFirma.isIncludiApplicativiModIEsterni(), filtroFirma.isIncludiApplicativiModIInterni()));
														this.getServizioApplicativoByCredenzialiSsl(connectionPdD, certificato, credenziale.isCertificateStrictVerification(), 
																filtroFirma.getTipiSoggetti(), filtroFirma.isIncludiApplicativiNonModI(), filtroFirma.isIncludiApplicativiModIEsterni(), filtroFirma.isIncludiApplicativiModIInterni());
													}
												}
												catch(DriverConfigurazioneNotFound notFound){
													// ignore
												}
												catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}	
												try {
													CertificateInfo certificato = ArchiveLoader.load(ArchiveType.CER, credenziale.getCertificate(), 0, null).getCertificate();
													ConfigurazioneFiltroServiziApplicativi filtroHttps = ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps();
													this.cache.remove(_getKey_getServizioApplicativoByCredenzialiSsl(certificato, credenziale.isCertificateStrictVerification(), 
															filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni()));
													this.getServizioApplicativoByCredenzialiSsl(connectionPdD, certificato, credenziale.isCertificateStrictVerification(), 
															filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni());
												}
												catch(DriverConfigurazioneNotFound notFound){
													// ignore
												}
												catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}	
											}			
										}
										else if(CredenzialeTipo.PRINCIPAL.equals(credenziale.getTipo())){
											try{
												this.cache.remove(_getKey_getServizioApplicativoByCredenzialiPrincipal(credenziale.getUser(), null));
												this.getServizioApplicativoByCredenzialiPrincipal(connectionPdD, credenziale.getUser());
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}				
										}
										else if(CredenzialeTipo.TOKEN.equals(credenziale.getTipo())){
											try{
												this.cache.remove(_getKey_getServizioApplicativoByCredenzialiToken(credenziale.getTokenPolicy(), credenziale.getUser(), null));
												this.getServizioApplicativoByCredenzialiToken(connectionPdD, credenziale.getTokenPolicy(), credenziale.getUser());
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}				
										}
									}
								}
							}
						}
					}
										
					try{
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pd.getServizio().getTipo(), pd.getServizio().getNome(),
								pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario(),
								pd.getServizio().getVersione());
						
						this.cache.remove(_getKey_MappingFruizionePortaDelegataList(idSoggettoProprietario, idServizio, true));
						this.getMappingFruizionePortaDelegataList(idSoggettoProprietario, idServizio, connectionPdD);
					}
					catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
					
					this.prefillElencoPolicyAttive(alogConsole, connectionPdD, true, TipoPdD.DELEGATA, pd.getNome());	
					
				}
				
				msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura dati della porta delegata ["+idPortaDelegata+"] completata";
				this.logDebug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			}
			
			msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura di "+idPDs.size()+" porte delegate completata";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
		}
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura di router/soggettiVirtuali ...";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		try{
			this.cache.remove(_getKey_getRouter());
			this.getRouter(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(_getKey_getSoggettiVirtuali());
			this.getSoggettiVirtuali(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(getKeyMethodGetServiziSoggettiVirtuali());
			this.getServiziSoggettiVirtuali(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura di router/soggettiVirtuali completata";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		// *** PorteDelegate ***
		// getPortaDelegata invocata precedentemente con i soggetti
		
		// *** PorteApplicative ***
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), recupero porte applicative ...";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		FiltroRicercaPorteApplicative filtroPorteApplicative = new FiltroRicercaPorteApplicative();
		List<IDPortaApplicativa> idPAs = null;
		try{
			idPAs = this.driverConfigurazionePdD.getAllIdPorteApplicative(filtroPorteApplicative);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(DriverConfigurazioneException e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), recuperate "+(idPAs!=null ? idPAs.size() : 0)+" porte applicative";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		if(idPAs!=null && idPAs.size()>0){
			
			msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura di "+idPAs.size()+" porte applicative ...";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			for (IDPortaApplicativa idPA : idPAs) {
						
				msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura dati della porta applicativa ["+idPA+"] ...";
				this.logDebug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				try{
					this.cache.remove(_getKey_getIDPortaApplicativa(idPA.getNome()));
					this.getIDPortaApplicativa(connectionPdD, idPA.getNome());
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
				try{
					this.cache.remove(_getKey_getPortaApplicativa(idPA));
					this.getPortaApplicativa(connectionPdD, idPA);
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
				PortaApplicativa pa  = null;
				try{
					pa  = this.driverConfigurazionePdD.getPortaApplicativa(idPA);
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(DriverConfigurazioneException e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
				if(pa!=null){
					
					IDSoggetto idSoggettoProprietario = new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario());
											
					if(pa.sizeServizioApplicativoList()>0){
						for (PortaApplicativaServizioApplicativo saPA : pa.getServizioApplicativoList()) {
							try{
								IDServizioApplicativo idSA = new IDServizioApplicativo();
								idSA.setIdSoggettoProprietario(idSoggettoProprietario);
								idSA.setNome(saPA.getNome());
								this.cache.remove(_getKey_getServizioApplicativo(idSA));
								this.getServizioApplicativo(connectionPdD, idSA);
							}
							catch(DriverConfigurazioneNotFound notFound){
								// ignore
							}
							catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
						}
					}
					
					try{
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(),
								idSoggettoProprietario,
								pa.getServizio().getVersione());
						
						this.cache.remove(_getKey_MappingErogazionePortaApplicativaList(idServizio, true));
						this.getMappingErogazionePortaApplicativaList(idServizio, connectionPdD);
					}
					catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
					
					this.prefillElencoPolicyAttive(alogConsole, connectionPdD, true, TipoPdD.APPLICATIVA, pa.getNome());		
					
				}
				
				msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura dati della porta applicativa ["+idPA+"] completata";
				this.logDebug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
			}
			
			msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura di "+idPAs.size()+" porte applicative completata";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
		}
		
		// getPorteApplicativeSoggettiVirtuali non inizializzabile
		// getPorteApplicativeVirtuali non inizializzabile
		
		// *** ServiziApplicativi ***
		// getServizioApplicativo(PD) invocata precedentemente con i soggetti
		// getServizioApplicativo(PA) invocata precedentemente con le porte applicative
		// getServizioApplicativoAutenticatoBasic(PD) invocata precedentemente con i soggetti
		// getServizioApplicativoAutenticatoSsl(PD) invocata precedentemente con i soggetti
		// getServizioApplicativoAutenticatoPrincipal(PD) invocata precedentemente con i soggetti
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), recupero servizi applicativi ...";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		FiltroRicercaServiziApplicativi filtroServiziApplicativi = new FiltroRicercaServiziApplicativi();
		List<IDServizioApplicativo> idSAs = null;
		try{
			idSAs = this.driverConfigurazionePdD.getAllIdServiziApplicativi(filtroServiziApplicativi);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(DriverConfigurazioneException e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), recuperati "+(idSAs!=null ? idSAs.size() : 0)+" servizi applicativi";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		if(idSAs!=null && idSAs.size()>0){
			
			msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura di "+idSAs.size()+" servizi applicativi ...";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			for (IDServizioApplicativo idSA : idSAs) {
				ServizioApplicativo sa = null;
				try{
					sa = this.driverConfigurazionePdD.getServizioApplicativo(idSA);
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(DriverConfigurazioneException e){this.logError("[prefill] errore"+e.getMessage(),e);}
						
				if(sa!=null){
					if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>0){
						for (Credenziali credenziale : sa.getInvocazionePorta().getCredenzialiList()) {
							if(CredenzialeTipo.BASIC.equals(credenziale.getTipo())){
								try{
									this.cache.remove(_getKey_getServizioApplicativoByCredenzialiBasic(credenziale.getUser(), credenziale.getPassword(), null));
									this.getServizioApplicativoByCredenzialiBasic(connectionPdD, credenziale.getUser(), credenziale.getPassword(), configApplicativi);
								}
								catch(DriverConfigurazioneNotFound notFound){
									// ignore
								}
								catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}				
							}
							else if(CredenzialeTipo.APIKEY.equals(credenziale.getTipo())){
								try{
									this.cache.remove(_getKey_getServizioApplicativoByCredenzialiApiKey(credenziale.getUser(), credenziale.getPassword(), credenziale.isCertificateStrictVerification(), null));
									this.getServizioApplicativoByCredenzialiApiKey(connectionPdD, credenziale.getUser(), credenziale.getPassword(), credenziale.isCertificateStrictVerification(), configApplicativi);
								}
								catch(DriverConfigurazioneNotFound notFound){
									// ignore
								}
								catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}				
							}
							else if(CredenzialeTipo.SSL.equals(credenziale.getTipo())){
								if(credenziale.getSubject()!=null) {
									try{
										if(Costanti.MODIPA_PROTOCOL_NAME.equals(sa.getTipoSoggettoProprietario())) {
											ConfigurazioneFiltroServiziApplicativi filtroFirma = ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiModIFirma();
											this.cache.remove(_getKey_getServizioApplicativoByCredenzialiSsl(credenziale.getSubject(), credenziale.getIssuer(), 
													filtroFirma.getTipiSoggetti(), filtroFirma.isIncludiApplicativiNonModI(), filtroFirma.isIncludiApplicativiModIEsterni(), filtroFirma.isIncludiApplicativiModIInterni()));
											this.getServizioApplicativoByCredenzialiSsl(connectionPdD, credenziale.getSubject(), credenziale.getIssuer(), 
													filtroFirma.getTipiSoggetti(), filtroFirma.isIncludiApplicativiNonModI(), filtroFirma.isIncludiApplicativiModIEsterni(), filtroFirma.isIncludiApplicativiModIInterni());
										}
									} catch(DriverConfigurazioneNotFound notFound){
										// ignore
									}
									catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}	
									try {
										ConfigurazioneFiltroServiziApplicativi filtroHttps = ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps();
										this.cache.remove(_getKey_getServizioApplicativoByCredenzialiSsl(credenziale.getSubject(), credenziale.getIssuer(), 
												filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni()));
										this.getServizioApplicativoByCredenzialiSsl(connectionPdD, credenziale.getSubject(), credenziale.getIssuer(), 
												filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni());
									}
									catch(DriverConfigurazioneNotFound notFound){
										// ignore
									}
									catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}	
								}
								if(credenziale.getCertificate()!=null) {
									try{
										CertificateInfo certificato = ArchiveLoader.load(ArchiveType.CER, credenziale.getCertificate(), 0, null).getCertificate();
										if(Costanti.MODIPA_PROTOCOL_NAME.equals(sa.getTipoSoggettoProprietario())) {
											ConfigurazioneFiltroServiziApplicativi filtroFirma = ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiModIFirma();
											this.cache.remove(_getKey_getServizioApplicativoByCredenzialiSsl(certificato, credenziale.isCertificateStrictVerification(), 
													filtroFirma.getTipiSoggetti(), filtroFirma.isIncludiApplicativiNonModI(), filtroFirma.isIncludiApplicativiModIEsterni(), filtroFirma.isIncludiApplicativiModIInterni()));
											this.getServizioApplicativoByCredenzialiSsl(connectionPdD, certificato, credenziale.isCertificateStrictVerification(), 
													filtroFirma.getTipiSoggetti(), filtroFirma.isIncludiApplicativiNonModI(), filtroFirma.isIncludiApplicativiModIEsterni(), filtroFirma.isIncludiApplicativiModIInterni());
										}
									}
									catch(DriverConfigurazioneNotFound notFound){
										// ignore
									}
									catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}	
									try {
										CertificateInfo certificato = ArchiveLoader.load(ArchiveType.CER, credenziale.getCertificate(), 0, null).getCertificate();
										ConfigurazioneFiltroServiziApplicativi filtroHttps = ConfigurazioneFiltroServiziApplicativi.getFiltroApplicativiHttps();
										this.cache.remove(_getKey_getServizioApplicativoByCredenzialiSsl(certificato, credenziale.isCertificateStrictVerification(), 
												filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni()));
										this.getServizioApplicativoByCredenzialiSsl(connectionPdD, certificato, credenziale.isCertificateStrictVerification(), 
												filtroHttps.getTipiSoggetti(), filtroHttps.isIncludiApplicativiNonModI(), filtroHttps.isIncludiApplicativiModIEsterni(), filtroHttps.isIncludiApplicativiModIInterni());
									}
									catch(DriverConfigurazioneNotFound notFound){
										// ignore
									}
									catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}	
								}
							}
							else if(CredenzialeTipo.PRINCIPAL.equals(credenziale.getTipo())){
								try{
									this.cache.remove(_getKey_getServizioApplicativoByCredenzialiPrincipal(credenziale.getUser(), null));
									this.getServizioApplicativoByCredenzialiPrincipal(connectionPdD, credenziale.getUser());
								}
								catch(DriverConfigurazioneNotFound notFound){
									// ignore
								}
								catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}				
							}
							else if(CredenzialeTipo.TOKEN.equals(credenziale.getTipo())){
								try{
									this.cache.remove(_getKey_getServizioApplicativoByCredenzialiToken(credenziale.getTokenPolicy(), credenziale.getUser(), null));
									this.getServizioApplicativoByCredenzialiToken(connectionPdD, credenziale.getTokenPolicy(), credenziale.getUser());
								}
								catch(DriverConfigurazioneNotFound notFound){
									// ignore
								}
								catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}				
							}
						}
					}
				}
			}
			
			msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura di "+idSAs.size()+" servizi applicativi completata";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
		}
		
		// *** Configurazione ***
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura della configurazione della porta ...";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		List<String> code = this.openspcoopProperties.getTimerConsegnaContenutiApplicativiCode();
		for (String coda : code) {
			try{
				this.cache.remove(getKey_getConnettoriConsegnaNotifichePrioritarie(coda));
				this.getConnettoriConsegnaNotifichePrioritarie(connectionPdD, coda);
			}
			catch(DriverConfigurazioneNotFound notFound){
				// ignore
			}
			catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		}
		
		try{
			this.cache.remove(_getKey_getRoutingTable());
			this.getRoutingTable(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(getKeyMethodGetAccessoRegistro());
			this.getAccessoRegistro(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(getKeyMethodGetAccessoConfigurazione());
			this.getAccessoConfigurazione(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(getKeyMethodGetAccessoDatiAutorizzazione());
			this.getAccessoDatiAutorizzazione(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(getMethodGetAccessoDatiAutenticazione());
			this.getAccessoDatiAutenticazione(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(getKeyMethodGetAccessoDatiGestioneToken());
			this.getAccessoDatiGestioneToken(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(getKeyMethodGetAccessoDatiAttributeAuthority());
			this.getAccessoDatiAttributeAuthority(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(getKeyMethodGetAccessoDatiKeystore());
			this.getAccessoDatiKeystore(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(getKeyMethodGetAccessoDatiRichieste());
			this.getAccessoDatiRichieste(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(_getKey_getGestioneErrore(false));
			this.getGestioneErroreComponenteIntegrazione(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		try{
			this.cache.remove(_getKey_getGestioneErrore(true));
			this.getGestioneErroreComponenteCooperazione(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}

		try{
			this.cache.remove(_getKey_getConfigurazioneGenerale());
			this.getConfigurazioneGenerale(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		List<GenericProperties> listGenericProperties = null;
		try{
			listGenericProperties = this.driverConfigurazionePdD.getGenericProperties();
		}catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		if(listGenericProperties!=null && listGenericProperties.size()>0) {
			for (GenericProperties genericProperties : listGenericProperties) {
				
				try{
					this.cache.remove(_getKey_getGenericProperties(genericProperties.getTipologia()));
					this.getGenericProperties(connectionPdD, genericProperties.getTipologia());
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}	
				
				try{
					this.cache.remove(_getKey_getGenericProperties(genericProperties.getTipologia(), genericProperties.getNome()));
					this.getGenericProperties(connectionPdD, genericProperties.getTipologia(), genericProperties.getNome());
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}	
			}
		}
		
		try{
			this.cache.remove(_getKey_getConfigurazioneWithOnlyExtendedInfo());
			this.getConfigurazioneWithOnlyExtendedInfo(connectionPdD);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		

		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD) completata";
		this.logInfo(msg);
		if(alogConsole!=null){
			alogConsole.info(msg);
		}

		// *** Controllo Traffico ***
		
		if(this.openspcoopProperties.isControlloTrafficoEnabled()) {
		
			msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura della configurazione ...";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			try{
				this.cache.remove(_getKey_ConfigurazioneControlloTraffico());
				this.getConfigurazioneControlloTraffico(connectionPdD);
			}
			catch(DriverConfigurazioneNotFound notFound){
				// ignore
			}
			catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
			
			msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura della configurazione completata";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			
			
			msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura della configurazione delle policy globali di rate limiting ...";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			try{
				this.cache.remove(_getKey_getConfigurazionePolicyRateLimitingGlobali());
				this.getConfigurazionePolicyRateLimitingGlobali(connectionPdD);
			}
			catch(DriverConfigurazioneNotFound notFound){
				// ignore
			}
			catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
			
			msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura della configurazione delle policy globali di rate limiting completata";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			
			
			
			msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura delle policy configurate ...";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			ElencoIdPolicy elencoPolicyConfigurate = null;
			try{
				this.cache.remove(_getKey_ElencoIdPolicy());
				elencoPolicyConfigurate = this.getElencoIdPolicy(connectionPdD, true);
			}
			catch(DriverConfigurazioneNotFound notFound){
				// ignore
			}
			catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
			
			if(elencoPolicyConfigurate!=null && elencoPolicyConfigurate.sizeIdPolicyList()>0) {
				
				msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura di "+elencoPolicyConfigurate.sizeIdPolicyList()+" policy configurate ...";
				this.logDebug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				for (IdPolicy idPolicy : elencoPolicyConfigurate.getIdPolicyList()) {
				
					try{
						this.cache.remove(_getKey_ConfigurazionePolicy(idPolicy.getNome()));
						this.getConfigurazionePolicy(connectionPdD, true, idPolicy.getNome());
					}
					catch(DriverConfigurazioneNotFound notFound){
						// ignore
					}
					catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
					
				}
				
				msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura di "+elencoPolicyConfigurate.sizeIdPolicyList()+" policy configurate completata";
				this.logDebug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
			}
			
			msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura delle policy configurate completata";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			
			this.prefillElencoPolicyAttive(alogConsole, connectionPdD, false, null, null);		

			
			
			if(this.openspcoopProperties.isConfigurazionePluginsEnabled()) {
			
				msg = "[Prefill] Inizializzazione cache (Plugins), lettura dei pluins configurati ...";
				this.logDebug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			
				int count = -1;
				try {
					count = this.configurazionePdD_plugins.countPlugins(connectionPdD);
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
				if(count>-1) {
					msg = "[Prefill] Inizializzazione cache (Plugins), trovati "+count+" plugins";
					this.logDebug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
					
					int readed = 0;
					while(readed<count) {
					
						int offset = readed;
						int limit = 100;
						
						msg = "[Prefill] Inizializzazione cache (Plugins), inizializzazione (offset:"+offset+" limit:"+limit+") in corso ...";
						this.logDebug(msg);
						if(alogConsole!=null){
							alogConsole.debug(msg);
						}
						
						List<IdPlugin> list = null;
						try {
							list = this.configurazionePdD_plugins.findAllPluginIds(connectionPdD, offset, limit);
							if(list!=null && !list.isEmpty()) {
								for (IdPlugin idPlugin : list) {
									
									TipoPlugin tipoPlugin = TipoPlugin.toEnumConstant(idPlugin.getTipoPlugin());
									if(tipoPlugin!=null) {
										if(TipoPlugin.AUTENTICAZIONE.equals(tipoPlugin) || 
												TipoPlugin.AUTORIZZAZIONE.equals(tipoPlugin) ||
												TipoPlugin.AUTORIZZAZIONE_CONTENUTI.equals(tipoPlugin) ||
												TipoPlugin.INTEGRAZIONE.equals(tipoPlugin)) {
											
											NameValue nvFruizione = new NameValue(PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_VALORE_FRUIZIONE);
											try{
												this.cache.remove(_getKey_PluginClassNameByFilter(idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvFruizione));
												this.getPluginClassNameByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvFruizione);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
											try{
												this.cache.remove(_getKey_PluginTipoByFilter(idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvFruizione));
												this.getPluginTipoByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvFruizione);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}

											NameValue nvErogazione = new NameValue(PluginCostanti.FILTRO_RUOLO_NOME, PluginCostanti.FILTRO_RUOLO_VALORE_EROGAZIONE);
											try{
												this.cache.remove(_getKey_PluginClassNameByFilter(idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvErogazione));
												this.getPluginClassNameByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvErogazione);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
											try{
												this.cache.remove(_getKey_PluginTipoByFilter(idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvErogazione));
												this.getPluginTipoByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvErogazione);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
										}
										else if(TipoPlugin.MESSAGE_HANDLER.equals(tipoPlugin)){
										
											List<String> filtri = PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RICHIESTA;
											for (String valoreFiltro : filtri) {
												NameValue nvRuolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RICHIESTA);
												NameValue nv = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, valoreFiltro);
												try{
													this.cache.remove(_getKey_PluginClassNameByFilter(idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvRuolo,nv));
													this.getPluginClassNameByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvRuolo,nv);
												}
												catch(DriverConfigurazioneNotFound notFound){
													// ignore
												}
												catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
												try{
													this.cache.remove(_getKey_PluginTipoByFilter(idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvRuolo,nv));
													this.getPluginTipoByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvRuolo,nv);
												}
												catch(DriverConfigurazioneNotFound notFound){
													// ignore
												}
												catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
											}
											filtri = PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_VALORI_RISPOSTA;
											for (String valoreFiltro : filtri) {
												NameValue nvRuolo = new NameValue(PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_NOME, PluginCostanti.FILTRO_RUOLO_MESSAGE_HANDLER_VALORE_RISPOSTA);
												NameValue nv = new NameValue(PluginCostanti.FILTRO_FASE_MESSAGE_HANDLER_NOME, valoreFiltro);
												try{
													this.cache.remove(_getKey_PluginClassNameByFilter(idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvRuolo,nv));
													this.getPluginClassNameByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvRuolo,nv);
												}
												catch(DriverConfigurazioneNotFound notFound){
													// ignore
												}
												catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
												try{
													this.cache.remove(_getKey_PluginTipoByFilter(idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvRuolo,nv));
													this.getPluginTipoByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvRuolo,nv);
												}
												catch(DriverConfigurazioneNotFound notFound){
													// ignore
												}
												catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
											}
											
										}
										else if(TipoPlugin.SERVICE_HANDLER.equals(tipoPlugin)){
											
											List<String> filtri = PluginCostanti.FILTRO_SERVICE_HANDLER_VALORI_CON_INTEGRATION_MANAGER;
											for (String valoreFiltro : filtri) {
												NameValue nv = new NameValue(PluginCostanti.FILTRO_SERVICE_HANDLER_NOME, valoreFiltro);
												try{
													this.cache.remove(_getKey_PluginClassNameByFilter(idPlugin.getTipoPlugin(),idPlugin.getTipo(),nv));
													this.getPluginClassNameByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getTipo(),nv);
												}
												catch(DriverConfigurazioneNotFound notFound){
													// ignore
												}
												catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
												try{
													this.cache.remove(_getKey_PluginTipoByFilter(idPlugin.getTipoPlugin(),idPlugin.getClassName(),nv));
													this.getPluginTipoByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getClassName(),nv);
												}
												catch(DriverConfigurazioneNotFound notFound){
													// ignore
												}
												catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
											}

										}
										else if(TipoPlugin.ALLARME.equals(tipoPlugin) && this.openspcoopProperties.isAllarmiEnabled()) {
											
											NameValue nvFruizione = new NameValue(PluginCostanti.FILTRO_APPLICABILITA_NOME, PluginCostanti.FILTRO_APPLICABILITA_VALORE_FRUIZIONE);
											try{
												this.cache.remove(_getKey_PluginClassNameByFilter(idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvFruizione));
												this.getPluginClassNameByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvFruizione);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
											try{
												this.cache.remove(_getKey_PluginTipoByFilter(idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvFruizione));
												this.getPluginTipoByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvFruizione);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}

											NameValue nvErogazione = new NameValue(PluginCostanti.FILTRO_APPLICABILITA_NOME, PluginCostanti.FILTRO_APPLICABILITA_VALORE_EROGAZIONE);
											try{
												this.cache.remove(_getKey_PluginClassNameByFilter(idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvErogazione));
												this.getPluginClassNameByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvErogazione);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
											try{
												this.cache.remove(_getKey_PluginTipoByFilter(idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvErogazione));
												this.getPluginTipoByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvErogazione);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
											
											NameValue nvConfigurazione = new NameValue(PluginCostanti.FILTRO_APPLICABILITA_NOME, PluginCostanti.FILTRO_APPLICABILITA_VALORE_CONFIGURAZIONE);
											try{
												this.cache.remove(_getKey_PluginClassNameByFilter(idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvConfigurazione));
												this.getPluginClassNameByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getTipo(),nvConfigurazione);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
											try{
												this.cache.remove(_getKey_PluginTipoByFilter(idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvConfigurazione));
												this.getPluginTipoByFilter(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getClassName(),nvConfigurazione);
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
										}
										else {
											try{
												this.cache.remove(_getKey_PluginClassName(idPlugin.getTipoPlugin(),idPlugin.getTipo()));
												this.getPluginClassName(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getTipo());
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
											
											try{
												this.cache.remove(_getKey_PluginTipo(idPlugin.getTipoPlugin(),idPlugin.getClassName()));
												this.getPluginTipo(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getClassName());
											}
											catch(DriverConfigurazioneNotFound notFound){
												// ignore
											}
											catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
										}
									}
									else {
										try{
											this.cache.remove(_getKey_PluginClassName(idPlugin.getTipoPlugin(),idPlugin.getTipo()));
											this.getPluginClassName(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getTipo());
										}
										catch(DriverConfigurazioneNotFound notFound){
											// ignore
										}
										catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
										try{
											this.cache.remove(_getKey_PluginTipo(idPlugin.getTipoPlugin(),idPlugin.getClassName()));
											this.getPluginTipo(connectionPdD, idPlugin.getTipoPlugin(),idPlugin.getClassName());
										}
										catch(DriverConfigurazioneNotFound notFound){
											// ignore
										}
										catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
									}
								}
								readed = readed + list.size();
							}
							else {
								readed = count; // condizione per uscire da while
							}
						}
						catch(Exception e){
							this.logError("[prefill] errore"+e.getMessage(),e);
							readed = count; // condizione per uscire da while
						}
						
					}
				}
				
				msg = "[Prefill] Inizializzazione cache (Plugins), lettura dei pluins configurati completata";
				this.logDebug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			}
		}
	}
	
	private void prefillElencoPolicyAttive(Logger alogConsole, Connection connectionPdD, boolean api, TipoPdD tipoPdD, String nomePorta) {
		
		String tipo = "API";
		if(!api) {
			tipo = "globali";
		}
		
		String msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura delle policy "+tipo+" attive ...";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
		
		Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttive = null;
		try{
			if(api) {
				this.cache.remove(getKeyMethodElencoIdPolicyAttiveAPI(tipoPdD, nomePorta));
				mapPolicyAttive = this.getElencoIdPolicyAttiveAPI(connectionPdD, true, tipoPdD, nomePorta);
			}
			else {
				this.cache.remove(getKeyMethodElencoIdPolicyAttiveGlobali());
				mapPolicyAttive = this.getElencoIdPolicyAttiveGlobali(connectionPdD, true);
			}
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		if(mapPolicyAttive!=null && !mapPolicyAttive.isEmpty()) {
		
			Iterator<TipoRisorsaPolicyAttiva> it = mapPolicyAttive.keySet().iterator();
			while (it.hasNext()) {
				TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = (TipoRisorsaPolicyAttiva) it.next();
				ElencoIdPolicyAttive elencoPolicyAttive = mapPolicyAttive.get(tipoRisorsaPolicyAttiva);
			
				if(elencoPolicyAttive!=null && elencoPolicyAttive.sizeIdActivePolicyList()>0) {
					
					msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura di "+elencoPolicyAttive.sizeIdActivePolicyList()+" policy "+tipo+" attive (risorsa: "+tipoRisorsaPolicyAttiva+") ...";
					this.logDebug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
					
					for (IdActivePolicy idActivePolicy : elencoPolicyAttive.getIdActivePolicyList()) {
					
						try{
							String id = UniqueIdentifierUtilities.getUniqueId(idActivePolicy);
							this.cache.remove(_getKey_AttivazionePolicy(id));
							this.getAttivazionePolicy(connectionPdD, true, id);
						}
						catch(DriverConfigurazioneNotFound notFound){
							// ignore
						}
						catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
						
					}
					
					msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura di "+elencoPolicyAttive.sizeIdActivePolicyList()+" policy "+tipo+" attive (risorsa: "+tipoRisorsaPolicyAttiva+") completata";
					this.logDebug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
					
				}
			}
						
		}
		
		Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttiveDimensioneMessaggio = null;
		try{
			if(api) {
				this.cache.remove(getKeyMethodElencoIdPolicyAttiveAPIDimensioneMessaggio(tipoPdD, nomePorta));
				mapPolicyAttiveDimensioneMessaggio = this.getElencoIdPolicyAttiveAPIDimensioneMessaggio(connectionPdD, true, tipoPdD, nomePorta);
			}
			else {
				this.cache.remove(getKeyMethodElencoIdPolicyAttiveGlobaliDimensioneMessaggio());
				mapPolicyAttiveDimensioneMessaggio = this.getElencoIdPolicyAttiveGlobaliDimensioneMessaggio(connectionPdD, true);
			}
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		if(mapPolicyAttiveDimensioneMessaggio!=null && !mapPolicyAttiveDimensioneMessaggio.isEmpty()) {
		
			Iterator<TipoRisorsaPolicyAttiva> it = mapPolicyAttiveDimensioneMessaggio.keySet().iterator();
			while (it.hasNext()) {
				TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = (TipoRisorsaPolicyAttiva) it.next();
				ElencoIdPolicyAttive elencoPolicyAttive = mapPolicyAttiveDimensioneMessaggio.get(tipoRisorsaPolicyAttiva);
			
				if(elencoPolicyAttive!=null && elencoPolicyAttive.sizeIdActivePolicyList()>0) {
					
					msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura di "+elencoPolicyAttive.sizeIdActivePolicyList()+" policy "+tipo+" attive (risorsa: "+tipoRisorsaPolicyAttiva+") ...";
					this.logDebug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
					
					for (IdActivePolicy idActivePolicy : elencoPolicyAttive.getIdActivePolicyList()) {
					
						try{
							String id = UniqueIdentifierUtilities.getUniqueId(idActivePolicy);
							this.cache.remove(_getKey_AttivazionePolicy(id));
							this.getAttivazionePolicy(connectionPdD, true, id);
						}
						catch(DriverConfigurazioneNotFound notFound){
							// ignore
						}
						catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
						
					}
					
					msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura di "+elencoPolicyAttive.sizeIdActivePolicyList()+" policy "+tipo+" attive (risorsa: "+tipoRisorsaPolicyAttiva+") completata";
					this.logDebug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
					
				}
			}
						
		}
		
		msg = "[Prefill] Inizializzazione cache (ControlloTraffico), lettura delle policy "+tipo+" attive completata";
		this.logDebug(msg);
		if(alogConsole!=null){
			alogConsole.debug(msg);
		}
	}
	
	public void prefillCacheConInformazioniRegistro(Logger alogConsole){
		
		String msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD-RegistroServizi) in corso ...";
		this.logInfo(msg);
		if(alogConsole!=null){
			alogConsole.info(msg);
		}
		
		RegistroServiziReader registroServiziReader = RegistroServiziManager.getInstance().getRegistroServiziReader();
		
		
		org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti filtroSoggetti = new org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti();
		@SuppressWarnings("unused")
		List<IDSoggetto> listSoggetti = null;
		try{
			listSoggetti = registroServiziReader.getAllIdSoggetti_noCache(filtroSoggetti,null);
		}
		catch(DriverRegistroServiziNotFound notFound){
			// ignore
		}
		catch(DriverRegistroServiziException e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		org.openspcoop2.core.registry.driver.FiltroRicercaServizi filtroServizi = new org.openspcoop2.core.registry.driver.FiltroRicercaServizi();
		List<IDServizio> listIdServizi = null;
		try{
			listIdServizi = registroServiziReader.getAllIdServizi_noCache(filtroServizi,null);
		}
		catch(DriverRegistroServiziNotFound notFound){
			// ignore
		}
		catch(DriverRegistroServiziException e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		List<IDSoggetto> soggettiVirtuali = null;
		try{
			this.cache.remove(_getKey_getSoggettiVirtuali());
			soggettiVirtuali = this.getSoggettiVirtuali(null);
		}
		catch(DriverConfigurazioneNotFound notFound){
			// ignore
		}
		catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
		
		if(listIdServizi!=null && listIdServizi.size()>0){
		
			msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD-RegistroServizi), lettura di "+listIdServizi.size()+" servizi ...";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
			
			for (IDServizio idServizio : listIdServizi) {
			
				msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura dati registro correlati al servizio ["+idServizio+"] ...";
				this.logDebug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
				
				try{
					this.cache.remove(_getKey_getPorteApplicative(idServizio,false));
					this.getPorteApplicative(null, idServizio, false);
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
				try{
					this.cache.remove(_getKey_getPorteApplicative(idServizio,true));
					this.getPorteApplicative(null, idServizio, true);
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
				try{
					this.cache.remove(getKeyMethodGetPorteApplicativeSoggettiVirtuali(idServizio));
					this.getPorteApplicativeSoggettiVirtuali(null, idServizio, null, false);
				}
				catch(DriverConfigurazioneNotFound notFound){
					// ignore
				}
				catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
				
				if(soggettiVirtuali!=null && soggettiVirtuali.size()>0) {
					
					msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura dati registro correlati al servizio per soggetti virtuali ("+soggettiVirtuali.size()+") ["+idServizio+"] ...";
					this.logDebug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
					
					for (IDSoggetto idSoggetto : soggettiVirtuali) {
						
						try{
							this.cache.remove(_getKey_getPorteApplicativeVirtuali(idSoggetto, idServizio,false));
							this.getPorteApplicativeVirtuali(null, idSoggetto, idServizio, false);
						}
						catch(DriverConfigurazioneNotFound notFound){
							// ignore
						}
						catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
						
						try{
							this.cache.remove(_getKey_getPorteApplicativeVirtuali(idSoggetto, idServizio,true));
							this.getPorteApplicativeVirtuali(null, idSoggetto, idServizio, true);
						}
						catch(DriverConfigurazioneNotFound notFound){
							// ignore
						}
						catch(Exception e){this.logError("[prefill] errore"+e.getMessage(),e);}
						
					}
						
					msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura dati registro correlati al servizio per soggetti virtuali ("+soggettiVirtuali.size()+") ["+idServizio+"] completata";
					this.logDebug(msg);
					if(alogConsole!=null){
						alogConsole.debug(msg);
					}
				}
				
				msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD), lettura dati registro correlati al servizio ["+idServizio+"] completata";
				this.logDebug(msg);
				if(alogConsole!=null){
					alogConsole.debug(msg);
				}
			}
			msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD-RegistroServizi), lettura di "+listIdServizi.size()+" servizi completata";
			this.logDebug(msg);
			if(alogConsole!=null){
				alogConsole.debug(msg);
			}
		}
		
		msg = "[Prefill] Inizializzazione cache (ConfigurazionePdD-RegistroServizi) completata";
		this.logInfo(msg);
		if(alogConsole!=null){
			alogConsole.info(msg);
		}
	}
	
	
	
	
	
	/**
	 * Si occupa di effettuare una ricerca nella configurazione, e di inserire la ricerca in cache
	 */
	public Object getObjectCache(String keyCache,String methodName,
			Connection connectionPdD,
			ConfigurazionePdDType tipoConfigurazione,
			Object ... instances) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		Class<?>[] classArgoments = null;
		Object[] values = null;
		if(instances!=null && instances.length>0){
			classArgoments = new Class<?>[instances.length];
			values = new Object[instances.length];
			for (int i = 0; i < instances.length; i++) {
				classArgoments[i] = instances[i].getClass();
				if(classArgoments[i].getPackage()!=null &&
						classArgoments[i].getPackage().getName()!=null &&
						classArgoments[i].getPackage().getName().equals("org.openspcoop2.protocol.sdk.registry")) {
					try {
						if(classArgoments[i].getSimpleName().startsWith("ProtocolFiltro")) {
							classArgoments[i] = Class.forName("org.openspcoop2.core.config.driver."+classArgoments[i].getSimpleName().replace("ProtocolFiltro", "Filtro"));
						}
						else {
							classArgoments[i] = Class.forName("org.openspcoop2.core.config.driver."+classArgoments[i].getSimpleName());
						}
					}catch(Exception e) {
						throw new DriverConfigurazioneException(e.getMessage(),e);
					}
				}
				if("getServizioApplicativoByCredenzialiApiKey".equals(methodName) && i==2) {
					classArgoments[i] = boolean.class;
				}
				else if("getServizioApplicativoByCredenzialiToken".equals(methodName) && i==2) {
					classArgoments[i] = boolean.class;
				}
				else if("instanceAllarmi".equals(methodName) && i==0) {
					classArgoments[i] = List.class;
				}
				values[i] = instances[i];
			}
		}
		return getObjectCache(keyCache, methodName, connectionPdD, tipoConfigurazione, classArgoments, values);
	}
	private static org.openspcoop2.utils.Semaphore semaphore_getObjectCache = new org.openspcoop2.utils.Semaphore("ConfigurazionePdD_Object");
	public Object getObjectCache(String keyCache,String methodName,
			Connection connectionPdD,
			ConfigurazionePdDType tipoConfigurazione,
			Class<?>[] classArgoments, Object[] values) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		SemaphoreLock lock = semaphore_getObjectCache.acquireThrowRuntime("getObjectCache");
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
						this.logDebug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						return response.getObject();
					}else if(response.getException()!=null){
						this.logDebug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						throw (Exception) response.getException();
					}else{
						this.logError("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.logDebug("oggetto con chiave ["+keyCache+"] (methodo:"+methodName+") ricerco nella configurazione...");
			try{
				obj = getObject(methodName,connectionPdD,tipoConfigurazione,classArgoments,values);
			}catch(DriverConfigurazioneNotFound e){
				dNotFound = e;
			}

			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				if(dNotFound!=null){
					this.logInfo("Aggiungo eccezione not found ["+keyCache+"] in cache");
				}else if(obj!=null){
					this.logInfo("Aggiungo oggetto ["+keyCache+"] in cache");
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
					this.logError("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
				}
			}

		}catch(DriverConfigurazioneException e){
			throw e;
		}catch(DriverConfigurazioneNotFound e){
			throw e;
		}catch(Exception e){
			if(notFoundClassName.equals(e.getClass().getName()))
				throw (DriverConfigurazioneNotFound) e;
			else
				throw new DriverConfigurazioneException("Configurazione, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}finally {
			semaphore_getObjectCache.release(lock, "getObjectCache");
		}

		if(dNotFound!=null){
			throw dNotFound;
		}else
			return obj;
	}

	/**
	 * Si occupa di effettuare una ricerca nella configurazione
	 */
	public Object getObject(String methodNameParam,
			Connection connectionPdD,
			ConfigurazionePdDType tipoConfigurazione,
			Object ... instances) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		Class<?>[] classArgoments = null;
		Object[] values = null;
		if(instances!=null && instances.length>0){
			classArgoments = new Class<?>[instances.length];
			values = new Object[instances.length];
			for (int i = 0; i < instances.length; i++) {
				classArgoments[i] = instances[i].getClass();
				if(classArgoments[i].getPackage()!=null &&
						classArgoments[i].getPackage().getName()!=null &&
						classArgoments[i].getPackage().getName().equals("org.openspcoop2.protocol.sdk.registry")) {
					try {
						if(classArgoments[i].getSimpleName().startsWith("ProtocolFiltro")) {
							classArgoments[i] = Class.forName("org.openspcoop2.core.config.driver."+classArgoments[i].getSimpleName().replace("ProtocolFiltro", "Filtro"));
						}
						else {
							classArgoments[i] = Class.forName("org.openspcoop2.core.config.driver."+classArgoments[i].getSimpleName());
						}
					}catch(Exception e) {
						throw new DriverConfigurazioneException(e.getMessage(),e);
					}
				}
				if("getServizioApplicativoByCredenzialiApiKey".equals(methodNameParam) && i==2) {
					classArgoments[i] = boolean.class;
				}
				else if("getServizioApplicativoByCredenzialiToken".equals(methodNameParam) && i==2) {
					classArgoments[i] = boolean.class;
				}
				else if("instanceAllarmi".equals(methodNameParam) && i==0) {
					classArgoments[i] = List.class;
				}
				values[i] = instances[i];
			}
		}
		return getObject(methodNameParam, connectionPdD, tipoConfigurazione, classArgoments, values);
	}
	public Object getObject(String methodNameParam,
			Connection connectionPdD,
			ConfigurazionePdDType tipoConfigurazione,
			Class<?>[] classArgoments, Object[] values) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

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
		this.logDebug("Cerco nella configurazione");
		try{
			switch (tipoConfigurazione) {
			case controlloTraffico:
			{
				if(this.configurazionePdD_controlloTraffico==null) {
					throw new Exception("Driver ControlloTraffico non istanziato");
				}
				this.logDebug("invocazione metodo ["+methodName+"]...");
				if(classArgoments==null || classArgoments.length==0){
					Method method =  this.configurazionePdD_controlloTraffico.getClass().getMethod(methodName, Connection.class);
					obj = method.invoke(this.configurazionePdD_controlloTraffico, connectionPdD);
				}else if(classArgoments.length==1){
					Method method =  this.configurazionePdD_controlloTraffico.getClass().getMethod(methodName, Connection.class, classArgoments[0]);
					obj = method.invoke(this.configurazionePdD_controlloTraffico, connectionPdD,values[0]);
				}else if(classArgoments.length==2){
					Method method =  this.configurazionePdD_controlloTraffico.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1]);
					obj = method.invoke(this.configurazionePdD_controlloTraffico, connectionPdD,values[0],values[1]);
				}else if(classArgoments.length==3){
					Method method =  this.configurazionePdD_controlloTraffico.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2]);
					obj = method.invoke(this.configurazionePdD_controlloTraffico, connectionPdD,values[0],values[1],values[2]);
				}else if(classArgoments.length==4){
					Method method =  this.configurazionePdD_controlloTraffico.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3]);
					obj = method.invoke(this.configurazionePdD_controlloTraffico, connectionPdD,values[0],values[1],values[2],values[3]);
				}else if(classArgoments.length==5){
					Method method =  this.configurazionePdD_controlloTraffico.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3],classArgoments[4]);
					obj = method.invoke(this.configurazionePdD_controlloTraffico, connectionPdD,values[0],values[1],values[2],values[3],values[4]);
				}else if(classArgoments.length==6){
					Method method =  this.configurazionePdD_controlloTraffico.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3],classArgoments[4],classArgoments[5]);
					obj = method.invoke(this.configurazionePdD_controlloTraffico, connectionPdD,values[0],values[1],values[2],values[3],values[4],values[5]);
				}else
					throw new Exception("Troppi argomenti per gestire la chiamata del metodo");
				break;
			}
			case plugins: {
				if(this.configurazionePdD_plugins==null) {
					throw new Exception("Driver Plugins non istanziato");
				}
				this.logDebug("invocazione metodo ["+methodName+"]...");
				if(classArgoments==null || classArgoments.length==0){
					Method method =  this.configurazionePdD_plugins.getClass().getMethod(methodName, Connection.class);
					obj = method.invoke(this.configurazionePdD_plugins, connectionPdD);
				}else if(classArgoments.length==1){
					Method method =  this.configurazionePdD_plugins.getClass().getMethod(methodName, Connection.class, classArgoments[0]);
					obj = method.invoke(this.configurazionePdD_plugins, connectionPdD,values[0]);
				}else if(classArgoments.length==2){
					Method method =  this.configurazionePdD_plugins.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1]);
					obj = method.invoke(this.configurazionePdD_plugins, connectionPdD,values[0],values[1]);
				}else if(classArgoments.length==3){
					Method method =  this.configurazionePdD_plugins.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2]);
					obj = method.invoke(this.configurazionePdD_plugins, connectionPdD,values[0],values[1],values[2]);
				}else if(classArgoments.length==4){
					Method method =  this.configurazionePdD_plugins.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3]);
					obj = method.invoke(this.configurazionePdD_plugins, connectionPdD,values[0],values[1],values[2],values[3]);
				}else if(classArgoments.length==5){
					Method method =  this.configurazionePdD_plugins.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3],classArgoments[4]);
					obj = method.invoke(this.configurazionePdD_plugins, connectionPdD,values[0],values[1],values[2],values[3],values[4]);
				}else if(classArgoments.length==6){
					Method method =  this.configurazionePdD_plugins.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3],classArgoments[4],classArgoments[5]);
					obj = method.invoke(this.configurazionePdD_plugins, connectionPdD,values[0],values[1],values[2],values[3],values[4],values[5]);
				}else
					throw new Exception("Troppi argomenti per gestire la chiamata del metodo");
				break;
			}
			case allarmi:
			{
				if(this.configurazionePdD_allarmi==null) {
					throw new Exception("Driver Allarmi non istanziato");
				}
				this.logDebug("invocazione metodo ["+methodName+"]...");
				if(classArgoments==null || classArgoments.length==0){
					Method method =  this.configurazionePdD_allarmi.getClass().getMethod(methodName, Connection.class);
					obj = method.invoke(this.configurazionePdD_allarmi, connectionPdD);
				}else if(classArgoments.length==1){
					Method method =  this.configurazionePdD_allarmi.getClass().getMethod(methodName, Connection.class, classArgoments[0]);
					obj = method.invoke(this.configurazionePdD_allarmi, connectionPdD,values[0]);
				}else if(classArgoments.length==2){
					Method method =  this.configurazionePdD_allarmi.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1]);
					obj = method.invoke(this.configurazionePdD_allarmi, connectionPdD,values[0],values[1]);
				}else if(classArgoments.length==3){
					Method method =  this.configurazionePdD_allarmi.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2]);
					obj = method.invoke(this.configurazionePdD_allarmi, connectionPdD,values[0],values[1],values[2]);
				}else if(classArgoments.length==4){
					Method method =  this.configurazionePdD_allarmi.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3]);
					obj = method.invoke(this.configurazionePdD_allarmi, connectionPdD,values[0],values[1],values[2],values[3]);
				}else if(classArgoments.length==5){
					Method method =  this.configurazionePdD_allarmi.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3],classArgoments[4]);
					obj = method.invoke(this.configurazionePdD_allarmi, connectionPdD,values[0],values[1],values[2],values[3],values[4]);
				}else if(classArgoments.length==6){
					Method method =  this.configurazionePdD_allarmi.getClass().getMethod(methodName, Connection.class, classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3],classArgoments[4],classArgoments[5]);
					obj = method.invoke(this.configurazionePdD_allarmi, connectionPdD,values[0],values[1],values[2],values[3],values[4],values[5]);
				}else
					throw new Exception("Troppi argomenti per gestire la chiamata del metodo");
				break;
			}
			case config: {
				org.openspcoop2.core.config.driver.IDriverConfigurazioneGet driver = getDriver(connectionPdD);
				this.logDebug("invocazione metodo ["+methodName+"]...");
				if(classArgoments==null || classArgoments.length==0){
					Method method =  driver.getClass().getMethod(methodName);
					obj = method.invoke(driver);
				}else if(classArgoments.length==1){
					Method method =  driver.getClass().getMethod(methodName,classArgoments[0]);
					obj = method.invoke(driver,values[0]);
				}else if(classArgoments.length==2){
					Method method =  driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1]);
					obj = method.invoke(driver,values[0],values[1]);
				}else if(classArgoments.length==3){
					Method method =  driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],classArgoments[2]);
					obj = method.invoke(driver,values[0],values[1],values[2]);
				}else if(classArgoments.length==4){
					Method method =  driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3]);
					obj = method.invoke(driver,values[0],values[1],values[2],values[3]);
				}else if(classArgoments.length==5){
					Method method =  driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3],classArgoments[4]);
					obj = method.invoke(driver,values[0],values[1],values[2],values[3],values[4]);
				}else if(classArgoments.length==6){
					Method method =  driver.getClass().getMethod(methodName,classArgoments[0],classArgoments[1],classArgoments[2],classArgoments[3],classArgoments[4],classArgoments[5]);
					obj = method.invoke(driver,values[0],values[1],values[2],values[3],values[4],values[5]);
				}else
					throw new Exception("Troppi argomenti per gestire la chiamata del metodo");
				break;
			}
			}
		}catch(DriverConfigurazioneNotFound e){
			this.logDebug("Ricerca nella configurazione non riuscita (metodo ["+methodName+"]): "+e.getMessage());
			notFound=e;
		}
		catch(java.lang.reflect.InvocationTargetException e){
			if(e.getTargetException()!=null){
				if(notFoundClassName.equals(e.getTargetException().getClass().getName())){
					// Non presente
					this.logDebug("Ricerca nella configurazione non riuscita [NotFound] (metodo ["+methodName+"]): "+e.getTargetException().getMessage());
					notFound=new DriverConfigurazioneNotFound(e.getTargetException().getMessage(),e.getTargetException());
				}else if(excClassName.equals(e.getTargetException().getClass().getName())){
					// Non presente
					this.logDebug("Ricerca nella configurazione non riuscita [DriverException] (metodo ["+methodName+"]): "+e.getTargetException().getMessage(),e.getTargetException());
					throw new DriverConfigurazioneException(e.getTargetException().getMessage(),e.getTargetException());
				}else{
					this.logDebug("Ricerca nella configurazione non riuscita [InvTargetExcp.getTarget] (metodo ["+methodName+"]), "+e.getTargetException().getMessage(),e.getTargetException());
					throw new DriverConfigurazioneException(e.getTargetException().getMessage(),e.getTargetException());
				}
			}else{
				this.logDebug("Ricerca nella configurazione non riuscita [InvTargetExcp] (metodo ["+methodName+"]), "+e.getMessage(),e);
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		catch(Exception e){
			this.logDebug("ricerca nella configurazione non riuscita (metodo ["+methodName+"]), "+e.getMessage(),e);
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
				this.logDebug("Refresh nella configurazione locale non riuscita (metodo ["+methodName+"]): "+e.getMessage());
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
				else if("getAccessoDatiAutenticazione".equals(methodName)){
					obj = this.configLocalProperties.updateAccessoDatiAutenticazione((AccessoDatiAutenticazione)obj);
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
				this.logDebug("Refresh nella configurazione locale non riuscita (metodo ["+methodName+"]): "+e.getMessage());
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}

			try{
				if("getConfigurazioneWithOnlyExtendedInfo".equals(methodNameParam)){
					Configurazione config = (Configurazione) obj;
					Configurazione c = new Configurazione();
					if(config!=null) {
						for (Object object : config.getExtendedInfoList()) {
							c.addExtendedInfo(object);
						}
					}
					obj = c;
				}
			}catch(Exception e){
				this.logDebug("Aggiornamento Configurazione Extended non riuscita (metodo ["+methodNameParam+"]): "+e.getMessage());
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}

		this.logDebug("invocazione metodo ["+methodName+"] completata.");
		return obj;

	}


	private org.openspcoop2.core.config.driver.IDriverConfigurazioneGet getDriver(Connection connectionPdD) throws DriverConfigurazioneException{

		org.openspcoop2.core.config.driver.IDriverConfigurazioneGet driver = this.driverConfigurazionePdD;
		if((driver instanceof DriverConfigurazioneDB)){
			if(connectionPdD!=null && this.useConnectionPdD){
				//System.out.println("[CONFIG] USE CONNECTION VERSIONE!!!!!");
				driver = new DriverConfigurazioneDB(connectionPdD, this.logger, this.tipoDatabase);
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

	protected static String _getKey_getSoggettoByID(IDSoggetto aSoggetto){
		return "getSoggetto_" + aSoggetto.getTipo() + aSoggetto.getNome();
	}
	public Soggetto getSoggetto(Connection connectionPdD,IDSoggetto aSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Raccolta dati
		if(aSoggetto == null || aSoggetto.getNome()==null || aSoggetto.getTipo()==null)
			throw new DriverConfigurazioneException("[getSoggetto]: Parametro non definito");	

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_getSoggettoByID(aSoggetto);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			sogg = (Soggetto) this.getObjectCache(key,"getSoggetto",connectionPdD,ConfigurazionePdDType.config,aSoggetto);
		}else{
			sogg = (Soggetto) this.getObject("getSoggetto",connectionPdD,ConfigurazionePdDType.config,aSoggetto);
		}

		if(sogg!=null)
			return sogg;
		else
			throw new DriverConfigurazioneNotFound("[getSoggetto] Soggetto non trovato");
	}

	protected static String _getKey_getRouter(){
		return "getRouter";
	}
	public Soggetto getRouter(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_getRouter();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			sogg = (Soggetto) this.getObjectCache(key,"getRouter",connectionPdD,ConfigurazionePdDType.config);
		}else{
			sogg = (Soggetto) this.getObject("getRouter",connectionPdD,ConfigurazionePdDType.config);
		}

		if(sogg!=null)
			return sogg;
		else
			throw new DriverConfigurazioneNotFound("[getRouter] Soggetto non trovato");
	}

	protected static String _getKey_getSoggettiVirtuali(){
		return "getSoggettiVirtuali";
	}
	@SuppressWarnings(value = "unchecked")
	public List<IDSoggetto> getSoggettiVirtuali(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_getSoggettiVirtuali();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((List<IDSoggetto>) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		List<IDSoggetto> lista = null;
		if(this.cache!=null){
			lista = (List<IDSoggetto>) this.getObjectCache(key,"getSoggettiVirtuali",connectionPdD,ConfigurazionePdDType.config);
		}else{
			lista = (List<IDSoggetto>) this.getObject("getSoggettiVirtuali",connectionPdD,ConfigurazionePdDType.config);
		}

		if(lista!=null)
			return lista;
		else
			throw new DriverConfigurazioneNotFound("[getSoggettiVirtuali] Lista Soggetti Virtuali non costruita");
	}

	private static final String KEY_METHOD_GET_SERVIZI_SOGGETTI_VIRTUALI = "getServiziSoggettiVirtuali";
	protected static String getKeyMethodGetServiziSoggettiVirtuali(){
		return KEY_METHOD_GET_SERVIZI_SOGGETTI_VIRTUALI;
	}
	@SuppressWarnings(value = "unchecked")
	public List<IDServizio> getServiziSoggettiVirtuali(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			 key = getKeyMethodGetServiziSoggettiVirtuali();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((List<IDServizio>) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		List<IDServizio> lista = null;
		if(this.cache!=null){
			lista = (List<IDServizio>) this.getObjectCache(key,KEY_METHOD_GET_SERVIZI_SOGGETTI_VIRTUALI,connectionPdD,ConfigurazionePdDType.config);
		}else{
			lista = (List<IDServizio>) this.getObject(KEY_METHOD_GET_SERVIZI_SOGGETTI_VIRTUALI,connectionPdD,ConfigurazionePdDType.config);
		}

		if(lista!=null)
			return lista;
		else
			throw new DriverConfigurazioneNotFound("["+KEY_METHOD_GET_SERVIZI_SOGGETTI_VIRTUALI+"] Lista Servizi erogati da Soggetti Virtuali non costruita");

	}





	// PORTA DELEGATA

	protected static String _getKey_getIDPortaDelegata(String nome){
		 return "getIDPortaDelegata_" + nome;
	}
	public IDPortaDelegata getIDPortaDelegata(Connection connectionPdD,String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(nome==null)
			throw new DriverConfigurazioneException("[getIDPortaDelegata]: Parametro non definito (nome)");

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_getIDPortaDelegata(nome);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((IDPortaDelegata) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		IDPortaDelegata idPD = null;
		if(this.cache!=null){
			idPD = (IDPortaDelegata) this.getObjectCache(key,"getIDPortaDelegata",connectionPdD,ConfigurazionePdDType.config,nome);
		}else{
			idPD = (IDPortaDelegata) this.getObject("getIDPortaDelegata",connectionPdD,ConfigurazionePdDType.config,nome);
		}

		if(idPD!=null)
			return idPD;
		else
			throw new DriverConfigurazioneNotFound("Porta Delegata ["+nome+"] non esistente");
	} 
	
	protected static String _getKey_getPortaDelegata(IDPortaDelegata idPD){
		 return "getPortaDelegata_" + idPD.getNome();
	}
	public PortaDelegata getPortaDelegata(Connection connectionPdD,IDPortaDelegata idPD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(idPD==null)
			throw new DriverConfigurazioneException("[getPortaDelegata]: Parametro non definito (idPD)");
		if(idPD.getNome()==null)
			throw new DriverConfigurazioneException("[getPortaDelegata]: Parametro non definito (nome)");

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_getPortaDelegata(idPD);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			pd = (PortaDelegata) this.getObjectCache(key,"getPortaDelegata",connectionPdD,ConfigurazionePdDType.config,idPD);
		}else{
			pd = (PortaDelegata) this.getObject("getPortaDelegata",connectionPdD,ConfigurazionePdDType.config,idPD);
		}

		if(pd!=null)
			return pd;
		else
			throw new DriverConfigurazioneNotFound("Porta Delegata ["+idPD.getNome()+"] non esistente");
	} 
	
	public void updateStatoPortaDelegata(Connection connectionPdD,IDPortaDelegata idPD, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(this.cache==null){
			return;
		}
		
		// Raccolta dati
		if(idPD==null)
			throw new DriverConfigurazioneException("[updateStatoPortaDelegataInCache]: Parametro non definito (idPD)");
		if(idPD.getNome()==null)
			throw new DriverConfigurazioneException("[updateStatoPortaDelegataInCache]: Parametro non definito (nome)");

		// se e' attiva una cache provo ad utilizzarla
		String key = _getKey_getPortaDelegata(idPD);
		org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
		if(response != null){
			if(response.getException()==null && response.getObject()!=null){
				PortaDelegata pd = (PortaDelegata)  response.getObject();
				pd.setStato(stato);
			}
		}
		
		PortaDelegata pd = (PortaDelegata) this.getObject("getPortaDelegata",connectionPdD,ConfigurazionePdDType.config,idPD);
		if(!stato.equals(pd.getStato())) {
			org.openspcoop2.core.config.driver.IDriverConfigurazioneGet driver = getDriver(connectionPdD);
			if(driver instanceof DriverConfigurazioneDB) {
				DriverConfigurazioneDB db = (DriverConfigurazioneDB) driver;
				IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
				oldIDPortaDelegataForUpdate.setNome(idPD.getNome());
				pd.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
				pd.setStato(stato);
				db.updatePortaDelegata(pd);
			}
		}
	}
	
	private static org.openspcoop2.utils.Semaphore semaphore_getTemplateTrasformazionePD = new org.openspcoop2.utils.Semaphore("ConfigurazionePdD_TemplateTrasformazionePD");
	protected static String getKey_getPortaDelegataTemplate(IDPortaDelegata idPD, TemplateSource source, String identificativo){
		String prefixPD = _getKey_getPortaDelegata(idPD);
		return prefixPD+"_getTemplate_"+source.name()+"_"+identificativo;
	}
	public Template getTemplateTrasformazioneRichiesta(Connection connectionPdD,IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		TipoTrasformazione tipoTrasformazione = null;
		if(richiesta.getConversioneTipo()!=null) {
			tipoTrasformazione = TipoTrasformazione.toEnumConstant(richiesta.getConversioneTipo());
		}
		if(tipoTrasformazione==null || (!tipoTrasformazione.isTemplateFreemarker() && !tipoTrasformazione.isTemplateVelocity())) {
			return new Template(nomeTrasformazione, richiesta.getConversioneTemplate());
		}
		return _getTemplate(connectionPdD, idPD, "getTemplateTrasformazioneRichiesta", TemplateSource.TRASFORMAZIONE_RICHIESTA, nomeTrasformazione, richiesta.getConversioneTemplate(),
				requestInfo);
	}
	public Template getTemplateTrasformazioneSoapRichiesta(Connection connectionPdD,IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		TipoTrasformazione tipoTrasformazione = null;
		if(richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo()!=null) {
			tipoTrasformazione = TipoTrasformazione.toEnumConstant(richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo());
		}
		if(tipoTrasformazione==null || (!tipoTrasformazione.isTemplateFreemarker() && !tipoTrasformazione.isTemplateVelocity())) {
			return new Template(nomeTrasformazione, richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate());
		}
		return _getTemplate(connectionPdD, idPD, "getTemplateTrasformazioneSoapRichiesta", TemplateSource.TRASFORMAZIONE_SOAP_RICHIESTA, nomeTrasformazione, richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate(),
				requestInfo);
	}
	public Template getTemplateTrasformazioneRisposta(Connection connectionPdD,IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		TipoTrasformazione tipoTrasformazione = null;
		if(risposta.getConversioneTipo()!=null) {
			tipoTrasformazione = TipoTrasformazione.toEnumConstant(risposta.getConversioneTipo());
		}
		if(tipoTrasformazione==null || (!tipoTrasformazione.isTemplateFreemarker() && !tipoTrasformazione.isTemplateVelocity())) {
			return new Template(nomeTrasformazione, risposta.getConversioneTemplate());
		}
		return _getTemplate(connectionPdD, idPD, "getTemplateTrasformazioneRisposta", TemplateSource.TRASFORMAZIONE_RISPOSTA, nomeTrasformazione+"@@"+risposta.getNome(), risposta.getConversioneTemplate(),
				requestInfo);
	}
	public Template getTemplateTrasformazioneSoapRisposta(Connection connectionPdD,IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		TipoTrasformazione tipoTrasformazione = null;
		if(risposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo()!=null) {
			tipoTrasformazione = TipoTrasformazione.toEnumConstant(risposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo());
		}
		if(tipoTrasformazione==null || (!tipoTrasformazione.isTemplateFreemarker() && !tipoTrasformazione.isTemplateVelocity())) {
			return new Template(nomeTrasformazione, risposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate());
		}
		return _getTemplate(connectionPdD, idPD, "getTemplateTrasformazioneSoapRisposta", TemplateSource.TRASFORMAZIONE_SOAP_RISPOSTA, nomeTrasformazione+"@@"+risposta.getNome(), risposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate(),
				requestInfo);
	}

	public Template getTemplateCorrelazioneApplicativaRichiesta(Connection connectionPdD,IDPortaDelegata idPD, String nomeRegola, byte[] template,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, idPD, "getTemplateCorrelazioneApplicativaRichiesta", TemplateSource.CORRELAZIONE_APPLICATIVA_RICHIESTA, (nomeRegola!=null ? nomeRegola : "___default___"), template,
				requestInfo);
	}
	public Template getTemplateCorrelazioneApplicativaRisposta(Connection connectionPdD,IDPortaDelegata idPD, String nomeRegola, byte[] template,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, idPD, "getTemplateCorrelazioneApplicativaRisposta", TemplateSource.CORRELAZIONE_APPLICATIVA_RISPOSTA, (nomeRegola!=null ? nomeRegola : "___default___"), template,
				requestInfo);
	}
	
	private Template _getTemplate(Connection connectionPdD,IDPortaDelegata idPD, String nomeMetodo, TemplateSource templateSource, String identificativo, byte[] templateBytes,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		String key = getKey_getPortaDelegataTemplate(idPD, templateSource, identificativo);
		
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getTemplate(key);
			if(o!=null && o instanceof Template) {
				return (Template) o;
			}
		}
		
		Template template = _getTemplate(connectionPdD,idPD, nomeMetodo, templateSource, identificativo, templateBytes, key);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addTemplate(key, template, 
					requestInfo.getIdTransazione());
		}
		return template;
	}
	private Template _getTemplate(Connection connectionPdD,IDPortaDelegata idPD, String nomeMetodo, TemplateSource templateSource, String identificativo, byte[] templateBytes,
			String key) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(idPD == null)
			throw new DriverConfigurazioneException("["+nomeMetodo+"]: Parametro non definito (idPD is null)");
		if(idPD.getNome()==null)
			throw new DriverConfigurazioneException("["+nomeMetodo+"]: Parametro non definito (idPD.getNome() is null)");

		// se e' attiva una cache provo ad utilizzarla	
		if(this.cache!=null) {
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					Template template = (Template) response.getObject();
					if(template!=null){
						return template;
					}
				}
			}
		}

		Template template = null;
		
		SemaphoreLock lock = semaphore_getTemplateTrasformazionePD.acquireThrowRuntime(nomeMetodo);
		try {
			
			if(this.cache!=null) {
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
				if(response != null){
					if(response.getException()!=null){
						if(notFoundClassName.equals(response.getException().getClass().getName()))
							throw (DriverConfigurazioneNotFound) response.getException();
						else
							throw (DriverConfigurazioneException) response.getException();
					}else{
						template = (Template) response.getObject();
						if(template!=null){
							return template;
						}
					}
				}
			}
			
			template = new Template(key, templateBytes);
			
			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				this.logInfo("Aggiungo oggetto ["+key+"] in cache");
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject((java.io.Serializable)template);
					this.cache.put(key,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+key+"]: "+e.getMessage());
				}
			}
			
		}finally {
			semaphore_getTemplateTrasformazionePD.release(lock, nomeMetodo);
		}

		return template;

	}  
	
	
	public Template getTemplateIntegrazione(Connection connectionPdD,IDPortaDelegata idPD, File file,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, idPD, "getTemplateIntegrazione", TemplateSource.INTEGRAZIONE, file,
				requestInfo);
	}
	private Template _getTemplate(Connection connectionPdD,IDPortaDelegata idPD, String nomeMetodo, TemplateSource templateSource, File file,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		String key = getKey_getPortaDelegataTemplate(idPD, templateSource, file.getAbsolutePath());
		
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getTemplate(key);
			if(o!=null && o instanceof Template) {
				return (Template) o;
			}
		}
		
		Template template = _getTemplate(connectionPdD, idPD, nomeMetodo, templateSource, file, key);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addTemplate(key, template, 
					requestInfo.getIdTransazione());
		}
		return template;
	}
	private Template _getTemplate(Connection connectionPdD,IDPortaDelegata idPD, String nomeMetodo, TemplateSource templateSource, File file,
			String key) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// se e' attiva una cache provo ad utilizzarla
		if(this.cache!=null) {
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					Template template = (Template) response.getObject();
					if(template!=null){
						return template;
					}
				}
			}
		}

		Template template = null;
		
		SemaphoreLock lock = semaphore_getTemplateTrasformazionePD.acquireThrowRuntime(nomeMetodo);
		try {
			
			if(this.cache!=null) {
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
				if(response != null){
					if(response.getException()!=null){
						if(notFoundClassName.equals(response.getException().getClass().getName()))
							throw (DriverConfigurazioneNotFound) response.getException();
						else
							throw (DriverConfigurazioneException) response.getException();
					}else{
						template = (Template) response.getObject();
						if(template!=null){
							return template;
						}
					}
				}
			}
			
			ContentFile cf = getContentFileEngine(file);
			if(cf.isExists()) {
				template = new Template(key, cf.getContent());
			}
			else {
				throw new DriverConfigurazioneException("File '"+file.getAbsolutePath()+"' cannot exists");
			}
			
			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				this.logInfo("Aggiungo oggetto ["+key+"] in cache");
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject((java.io.Serializable)template);
					this.cache.put(key,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+key+"]: "+e.getMessage());
				}
			}
			
		}finally {
			semaphore_getTemplateTrasformazionePD.release(lock, nomeMetodo);
		}

		return template;

	} 
	
	
	
	
	// PORTA APPLICATIVA
	
	protected static String _getKey_getIDPortaApplicativa(String nome){
		 return "getIDPortaApplicativa_" + nome;
	}
	public IDPortaApplicativa getIDPortaApplicativa(Connection connectionPdD,String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(nome==null)
			throw new DriverConfigurazioneException("[getIDPortaApplicativa]: Parametro non definito (nome)");

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_getIDPortaApplicativa(nome);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((IDPortaApplicativa) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		IDPortaApplicativa idPA = null;
		if(this.cache!=null){
			idPA = (IDPortaApplicativa) this.getObjectCache(key,"getIDPortaApplicativa",connectionPdD,ConfigurazionePdDType.config,nome);
		}else{
			idPA = (IDPortaApplicativa) this.getObject("getIDPortaApplicativa",connectionPdD,ConfigurazionePdDType.config,nome);
		}

		if(idPA!=null)
			return idPA;
		else
			throw new DriverConfigurazioneNotFound("Porta Applicativa ["+nome+"] non esistente");
	} 
	
	protected static String _getKey_getPortaApplicativa(IDPortaApplicativa idPA){
		 return "getPortaApplicativa_" + idPA.getNome();
	}
	public PortaApplicativa getPortaApplicativa(Connection connectionPdD,IDPortaApplicativa idPA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(idPA == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (idPA is null)");
		if(idPA.getNome()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (idPA.getNome() is null)");

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_getPortaApplicativa(idPA);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					PortaApplicativa pa = (PortaApplicativa) response.getObject();
					if(pa!=null){
						return pa;
					}
				}
			}
		}

		// Algoritmo CACHE
		PortaApplicativa pa = null;
		if(this.cache!=null){
			pa = (PortaApplicativa) this.getObjectCache(key,"getPortaApplicativa",connectionPdD,ConfigurazionePdDType.config,idPA);
		}else{
			pa = (PortaApplicativa) this.getObject("getPortaApplicativa",connectionPdD,ConfigurazionePdDType.config,idPA);
		}

		if(pa!=null){
			return pa;
		}
		else{
			throw new DriverConfigurazioneNotFound("Porta Applicativa ["+idPA.getNome()+"] non esistente");
		}
	} 
	
	public void updateStatoPortaApplicativa(Connection connectionPdD,IDPortaApplicativa idPA, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(this.cache==null){
			return;
		}
		
		// Raccolta dati
		if(idPA==null)
			throw new DriverConfigurazioneException("[updateStatoPortaApplicativa]: Parametro non definito (idPA)");
		if(idPA.getNome()==null)
			throw new DriverConfigurazioneException("[updateStatoPortaApplicativa]: Parametro non definito (nome)");

		// se e' attiva una cache provo ad utilizzarla
		String key = _getKey_getPortaApplicativa(idPA);
		org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
		if(response != null){
			if(response.getException()==null && response.getObject()!=null){
				PortaApplicativa pa = (PortaApplicativa)  response.getObject();
				pa.setStato(stato);
			}
		}
		
		
		PortaApplicativa pa = (PortaApplicativa) this.getObject("getPortaApplicativa",connectionPdD,ConfigurazionePdDType.config,idPA);
		if(!stato.equals(pa.getStato())) {
			org.openspcoop2.core.config.driver.IDriverConfigurazioneGet driver = getDriver(connectionPdD);
			if(driver instanceof DriverConfigurazioneDB) {
				DriverConfigurazioneDB db = (DriverConfigurazioneDB) driver;
				IDPortaApplicativa oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
				oldIDPortaApplicativaForUpdate.setNome(idPA.getNome());
				pa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);
				pa.setStato(stato);
				db.updatePortaApplicativa(pa);
			}
		}
	}
	
	public String updateStatoConnettoreMultiplo(Connection connectionPdD,IDPortaApplicativa idPA, String nomeConnettore, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return updateStatoConnettoreMultiploEngine(connectionPdD,idPA, nomeConnettore, null, stato, false);
	}
	public String updateStatoConnettoreMultiplo(Connection connectionPdD,IDPortaApplicativa idPA, String nomeConnettore, String user, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return updateStatoConnettoreMultiploEngine(connectionPdD,idPA, nomeConnettore, user, stato, false);
	}
	public String updateSchedulingConnettoreMultiplo(Connection connectionPdD,IDPortaApplicativa idPA, String nomeConnettore, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return updateStatoConnettoreMultiploEngine(connectionPdD,idPA, nomeConnettore, null,stato,  true);
	}
	public String updateSchedulingConnettoreMultiplo(Connection connectionPdD,IDPortaApplicativa idPA, String nomeConnettore, String user, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return updateStatoConnettoreMultiploEngine(connectionPdD,idPA, nomeConnettore, user, stato, true);
	}
	private String updateStatoConnettoreMultiploEngine(Connection connectionPdD,IDPortaApplicativa idPA, String nomeConnettore, String user, StatoFunzionalita stato, boolean scheduling) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
			
		if(this.cache==null){
			return null;
		}
		
		// Raccolta dati
		if(idPA==null)
			throw new DriverConfigurazioneException("[updateStatoPortaApplicativa]: Parametro non definito (idPA)");
		if(idPA.getNome()==null)
			throw new DriverConfigurazioneException("[updateStatoPortaApplicativa]: Parametro non definito (nome)");
		if(nomeConnettore==null)
			throw new DriverConfigurazioneException("[updateStatoPortaApplicativa]: Parametro non definito (nomeConnettore)");

		// se e' attiva una cache provo ad utilizzarla
		String key = _getKey_getPortaApplicativa(idPA);
		org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
		if(response != null){
			if(response.getException()==null && response.getObject()!=null){
				PortaApplicativa pa = (PortaApplicativa)  response.getObject();
				if(pa.sizeServizioApplicativoList()>0) {
					for (PortaApplicativaServizioApplicativo paSA : pa.getServizioApplicativoList()) {
						String nomePaSA = paSA.getDatiConnettore()!= null ? paSA.getDatiConnettore().getNome() : CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
						if(nomeConnettore.equals(nomePaSA)) {
							if(paSA.getDatiConnettore()==null) {
								paSA.setDatiConnettore(new PortaApplicativaServizioApplicativoConnettore());
							}
							if(scheduling) {
								paSA.getDatiConnettore().setScheduling(stato);
							}
							else {
								paSA.getDatiConnettore().setStato(stato);
							}
							break;
						}
					}
				}
			}
		}
		
		
		PortaApplicativa pa = (PortaApplicativa) this.getObject("getPortaApplicativa",connectionPdD,ConfigurazionePdDType.config,idPA);
		boolean update = false;
		String nomeSA = null;
		if(pa.sizeServizioApplicativoList()>0) {
			for (PortaApplicativaServizioApplicativo paSA : pa.getServizioApplicativoList()) {
				String nomePaSA = paSA.getDatiConnettore()!= null ? paSA.getDatiConnettore().getNome() : CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
				if(nomeConnettore.equals(nomePaSA)) {
					nomeSA = paSA.getNome();
					StatoFunzionalita check = null;
					if(paSA.getDatiConnettore()!=null) {
						check = paSA.getDatiConnettore().getStato();
					}
					if(!stato.equals(check)) {
						update = true;
						if(paSA.getDatiConnettore()==null) {
							paSA.setDatiConnettore(new PortaApplicativaServizioApplicativoConnettore());
						}
						if(scheduling) {
							paSA.getDatiConnettore().setScheduling(stato);
						}
						else {
							paSA.getDatiConnettore().setStato(stato);
						}
						
						if(user!=null) {
							if(paSA.getDatiConnettore().getProprietaOggetto()==null) {
								paSA.getDatiConnettore().setProprietaOggetto(new ProprietaOggetto());
							}
							paSA.getDatiConnettore().getProprietaOggetto().setUtenteUltimaModifica(user);
							paSA.getDatiConnettore().getProprietaOggetto().setDataUltimaModifica(DateManager.getDate());
						}
					}
					break;
				}
			}
		}
		if(update) {
			org.openspcoop2.core.config.driver.IDriverConfigurazioneGet driver = getDriver(connectionPdD);
			if(driver instanceof DriverConfigurazioneDB) {
				DriverConfigurazioneDB db = (DriverConfigurazioneDB) driver;
				IDPortaApplicativa oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
				oldIDPortaApplicativaForUpdate.setNome(idPA.getNome());
				pa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);
				db.updatePortaApplicativa(pa);
			}
		}
		return nomeSA;
	}
	
	protected static String _toKey_getPorteApplicativePrefix(IDServizio idServizio, boolean ricercaPuntuale){
		String key = "getPorteApplicative_ricercaPuntuale("+ricercaPuntuale+")_" + 
				idServizio.getSoggettoErogatore().getTipo()+idServizio.getSoggettoErogatore().getNome() + "_" + 
				idServizio.getTipo() + idServizio.getNome()+ ":" + idServizio.getVersione();
		return key;
	}
	private String _getKey_getPorteApplicative(IDServizio idServizio, boolean ricercaPuntuale){
		String key = _toKey_getPorteApplicativePrefix(idServizio, ricercaPuntuale);
		if(idServizio.getAzione()!=null)
			key = key + "_" + idServizio.getAzione();
		return key;
	}
	@SuppressWarnings("unchecked")
	public List<PortaApplicativa> getPorteApplicative(Connection connectionPdD,IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(idServizio == null || idServizio.getNome()==null || idServizio.getTipo()==null || idServizio.getVersione()==null ||
				idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null)
			throw new DriverConfigurazioneException("[getPorteApplicative]: Parametri non definito");	
		

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_getPorteApplicative(idServizio, ricercaPuntuale);
			
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					List<PortaApplicativa> list = (List<PortaApplicativa>) response.getObject();
					if(list!=null){
						return list;
					}
				}
			}
		}

		Class<?> [] classArgoments = {IDServizio.class,boolean.class};
		Object [] values = {idServizio,ricercaPuntuale};
		
		// Algoritmo CACHE
		List<PortaApplicativa> list = null;
		if(this.cache!=null){
			list = (List<PortaApplicativa>) this.getObjectCache(key,"getPorteApplicative",connectionPdD,ConfigurazionePdDType.config,classArgoments,values);
		}else{
			list = (List<PortaApplicativa>) this.getObject("getPorteApplicative",connectionPdD,ConfigurazionePdDType.config,classArgoments,values);
		}

		if(list!=null){
			return list;
		}
		else{
			throw new DriverConfigurazioneNotFound("Porte Applicative non trovate");
		}
	} 
	
	protected static String _toKey_getPorteApplicativeVirtualiPrefix(boolean ricercaPuntuale) {
		return "getPorteApplicativeVirtuali_ricercaPuntuale("+ricercaPuntuale+")_";
	}
	protected static String _toKey_getPorteApplicativeVirtuali_idSoggettoVirtuale(IDSoggetto soggettoVirtuale) {
		return soggettoVirtuale.getTipo()+soggettoVirtuale.getNome();
	}
	protected static String _toKey_getPorteApplicativeVirtuali_idServizio(IDServizio idServizio) {
		return idServizio.getSoggettoErogatore().getTipo()+idServizio.getSoggettoErogatore().getNome() + "_" + 
				idServizio.getTipo() + idServizio.getNome()+":"+idServizio.getVersione();
	}
	private String _getKey_getPorteApplicativeVirtuali(IDSoggetto soggettoVirtuale,IDServizio idServizio, boolean ricercaPuntuale){
		String key = _toKey_getPorteApplicativeVirtualiPrefix(ricercaPuntuale) + 
				_toKey_getPorteApplicativeVirtuali_idSoggettoVirtuale(soggettoVirtuale)+
				"_"+
				_toKey_getPorteApplicativeVirtuali_idServizio(idServizio);
		if(idServizio.getAzione()!=null)
			key = key + "_" + idServizio.getAzione();
		return key;
	}
	@SuppressWarnings("unchecked")
	public List<PortaApplicativa> getPorteApplicativeVirtuali(Connection connectionPdD,IDSoggetto soggettoVirtuale,IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(soggettoVirtuale==null || soggettoVirtuale.getTipo()==null || soggettoVirtuale.getNome()==null){
			throw new DriverConfigurazioneException("[getPorteApplicative]: Parametri (SoggettoVirtuale) non definito");	
		}
		if(idServizio == null || idServizio.getNome()==null || idServizio.getTipo()==null || idServizio.getVersione()==null ||
				idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null)
			throw new DriverConfigurazioneException("[getPorteApplicative]: Parametri (IDServizio) non definito");	
		

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getPorteApplicativeVirtuali(soggettoVirtuale, idServizio, ricercaPuntuale);
			
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					List<PortaApplicativa> list = (List<PortaApplicativa>) response.getObject();
					if(list!=null){
						return list;
					}
				}
			}
		}

		Class<?> [] classArgoments = {IDSoggetto.class,IDServizio.class,boolean.class};
		Object [] values = {soggettoVirtuale,idServizio,ricercaPuntuale};
		
		// Algoritmo CACHE
		List<PortaApplicativa> list = null;
		if(this.cache!=null){
			list = (List<PortaApplicativa>) this.getObjectCache(key,"getPorteApplicativeVirtuali",connectionPdD,ConfigurazionePdDType.config,classArgoments,values);
		}else{
			list = (List<PortaApplicativa>) this.getObject("getPorteApplicativeVirtuali",connectionPdD,ConfigurazionePdDType.config,classArgoments,values);
		}

		if(list!=null){
			return list;
		}
		else{
			throw new DriverConfigurazioneNotFound("Porte Applicative non trovate");
		}
	} 
	
	private static final String METHOD_GET_PORTE_APPLICATIVE_SOGGETTI_VIRTUALI = "getPorteApplicativeSoggettiVirtuali";
	protected static String toKeyMethodGetPorteApplicativeSoggettiVirtualiPrefix(IDServizio idServizio){
		return METHOD_GET_PORTE_APPLICATIVE_SOGGETTI_VIRTUALI+"_" + idServizio.getSoggettoErogatore().getTipo()+idServizio.getSoggettoErogatore().getNome() + "_" + 
				idServizio.getTipo() + idServizio.getNome()+":"+idServizio.getVersione();
	}
	private String getKeyMethodGetPorteApplicativeSoggettiVirtuali(IDServizio idServizio){
		String key = toKeyMethodGetPorteApplicativeSoggettiVirtualiPrefix(idServizio);
		if(idServizio.getAzione()!=null)
			key = key + "_" + idServizio.getAzione();
		return key;
	}
	@SuppressWarnings(value = "unchecked")
	public Map<IDSoggetto,PortaApplicativa> getPorteApplicativeSoggettiVirtuali(Connection connectionPdD,IDServizio idServizio,
			Map<String, String> proprietaPresentiBustaRicevuta,boolean useFiltroProprieta)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(idServizio == null || idServizio.getNome()==null || idServizio.getTipo()==null || idServizio.getVersione()==null ||
				idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null)
			throw new DriverConfigurazioneException("["+METHOD_GET_PORTE_APPLICATIVE_SOGGETTI_VIRTUALI+"]: Parametro non definito");	

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			this.logDebug("Search porte applicative soggetti virtuali in cache...");
			key = this.getKeyMethodGetPorteApplicativeSoggettiVirtuali(idServizio);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					if(response.getObject()!=null){
						this.logDebug("Oggetto in cache trovato. Analizzo porte virtuali trovate rispetto alle proprieta' presenti nella busta arrivata: "+this.toStringFiltriProprieta(proprietaPresentiBustaRicevuta));
						Map<IDSoggetto,PortaApplicativa> pa = (Map<IDSoggetto,PortaApplicativa>) response.getObject();
						Map<IDSoggetto,PortaApplicativa> paChecked = new java.util.HashMap<>();
						Iterator<IDSoggetto> it = pa.keySet().iterator();
						while (it.hasNext()) {
							IDSoggetto idS = it.next();
							PortaApplicativa paC = pa.get(idS);
							this.logDebug("Analizzo pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]...");
							if(useFiltroProprieta){
								boolean ricezioneAutorizzata = checkProprietaFiltroPortaApplicativa(paC,proprietaPresentiBustaRicevuta);
								if(ricezioneAutorizzata){
									this.logDebug("Filtri matchano le protocol properties della pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
									paChecked.put(idS,paC);
								}else{
									this.logDebug("Filtri non matchano le protocol properties della pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
								}
							}else{
								this.logDebug("Invocazione metodo senza la richiesta di filtro per proprieta, aggiunta pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
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
		Map<IDSoggetto,PortaApplicativa> pa = null;
		if(this.cache!=null){
			pa = (Map<IDSoggetto,PortaApplicativa>) this.getObjectCache(key,METHOD_GET_PORTE_APPLICATIVE_SOGGETTI_VIRTUALI,connectionPdD,ConfigurazionePdDType.config,idServizio);
		}else{
			pa = (Map<IDSoggetto,PortaApplicativa>) this.getObject(METHOD_GET_PORTE_APPLICATIVE_SOGGETTI_VIRTUALI,connectionPdD,ConfigurazionePdDType.config,idServizio);
		}

		if(pa!=null){
			this.logDebug("Oggetto trovato. Analizzo porte virtuali trovate rispetto alle proprieta' presenti nella busta arrivata: "+this.toStringFiltriProprieta(proprietaPresentiBustaRicevuta));
			Map<IDSoggetto,PortaApplicativa> paChecked = new java.util.HashMap<>();
			Iterator<IDSoggetto> it = pa.keySet().iterator();
			while (it.hasNext()) {
				IDSoggetto idS = it.next();
				PortaApplicativa paC = pa.get(idS);
				this.logDebug("Analizzo pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]...");
				if(useFiltroProprieta){
					boolean ricezioneAutorizzata = checkProprietaFiltroPortaApplicativa(paC,proprietaPresentiBustaRicevuta);
					if(ricezioneAutorizzata){
						this.logDebug("Filtri matchano le protocol properties della pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
						paChecked.put(idS,paC);
					}else{
						this.logDebug("Filtri non matchano le protocol properties della pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
					}
				}else{
					this.logDebug("Invocazione metodo senza la richiesta di filtro per proprieta, aggiunta pa ["+paC.getNome()+"] del soggetto ["+idS.toString()+"]");
					paChecked.put(idS,paC);
				}
			}
			if(paChecked.size()>0)
				return paChecked;
		}

		throw new DriverConfigurazioneNotFound("["+METHOD_GET_PORTE_APPLICATIVE_SOGGETTI_VIRTUALI+"] PA_SoggettiVirtuali non trovati");
	} 
	
	private String toStringFiltriProprieta(Map<String, String> filtroProprietaPorteApplicative){
		StringBuilder bf = new StringBuilder();
		if(filtroProprietaPorteApplicative==null || filtroProprietaPorteApplicative.size()<=0){
			bf.append("Non presenti");
		}else{
			Iterator<String> it = filtroProprietaPorteApplicative.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				bf.append("\n");
				bf.append(key+" = "+filtroProprietaPorteApplicative.get(key));
			}
		}
		return bf.toString();
	}
	private boolean checkProprietaFiltroPortaApplicativa(PortaApplicativa pa,Map<String, String> proprietaPresentiBustaRicevuta){
		boolean filtriPAPresenti = false;
		//System.out.println("Entro.... PROPRIETA' PRESENTI["+pa.sizeSetProtocolPropertyList()+"] JMS["+filtroProprietaPorteApplicative.size()+"]");

		/*Enumeration<String> en = filtroProprietaPorteApplicative.keys();
		while (en.hasMoreElements()){
			String key = en.nextElement();
			String value = filtroProprietaPorteApplicative.get(key);
			System.out.println("Proprieta' JMS: ["+key+"]=["+value+"]");
		}*/

		if(proprietaPresentiBustaRicevuta!=null && (proprietaPresentiBustaRicevuta.size()>0) ){

			for(int i=0; i<pa.sizeProprietaList(); i++){
				String nome = pa.getProprieta(i).getNome();
				String value = pa.getProprieta(i).getValore();
				this.logDebug("ProprietaProtocollo della PA["+pa.getNome()+"] nome["+nome+"] valore["+value+"]");

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

						this.logDebug("ProtocolProperty della PA["+pa.getNome()+"] nome["+nome+"] valore["+value+"] interpretata come ["+nome+"]["+operatoreP+"]["+valoreP+"]. Viene utilizzata per la proprieta' della busta con valore ["+proprietaArrivata+"]");

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
								this.logError("[checkProprietaFiltroPortaApplicativa] Operator ["+operatoreP+"] non supportato per le protocol properties...");
							}
						}
					}
				}
			}

		}else{

			// Proprieta' non presenti nella busta ricevuta
			// Controllo se la PA richiedeva un filtro per protocol properties.
			for(int i=0; i<pa.sizeProprietaList(); i++){
				String nome = pa.getProprieta(i).getNome();
				String value = pa.getProprieta(i).getValore();
				this.logDebug("ProtocolProperty della PA["+pa.getNome()+"] nome["+nome+"] valore["+value+"]");

				//System.out.println("Esamino ["+nome+"]["+value+"]");
				if(value.indexOf("!#!")!=-1){
					//System.out.println("FILTRIPAPresenti");
					this.logDebug("ProtocolProperty della PA["+pa.getNome()+"] nome["+nome+"] valore["+value+"] contiene una richiesta di filtro, siccome la busta arrivata non contiene proprieta', la PA non ha diritto a riceverla.");
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
 
	private static org.openspcoop2.utils.Semaphore semaphore_getTemplateTrasformazionePA = new org.openspcoop2.utils.Semaphore("ConfigurazionePdD_TemplateTrasformazionePA");
	protected static String getKey_getPortaApplicativaTemplate(IDPortaApplicativa idPA, TemplateSource source, String identificativo){
		String prefixPA = _getKey_getPortaApplicativa(idPA);
		return prefixPA+"_getTemplate_"+source.name()+"_"+identificativo;
	}
	public Template getTemplateTrasformazioneRichiesta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		TipoTrasformazione tipoTrasformazione = null;
		if(richiesta.getConversioneTipo()!=null) {
			tipoTrasformazione = TipoTrasformazione.toEnumConstant(richiesta.getConversioneTipo());
		}
		if(tipoTrasformazione==null || (!tipoTrasformazione.isTemplateFreemarker() && !tipoTrasformazione.isTemplateVelocity())) {
			return new Template(nomeTrasformazione, richiesta.getConversioneTemplate());
		}
		return _getTemplate(connectionPdD, idPA, "getTemplateTrasformazioneRichiesta", TemplateSource.TRASFORMAZIONE_RICHIESTA, nomeTrasformazione, richiesta.getConversioneTemplate(),
				requestInfo);
	}
	public Template getTemplateTrasformazioneSoapRichiesta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		TipoTrasformazione tipoTrasformazione = null;
		if(richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo()!=null) {
			tipoTrasformazione = TipoTrasformazione.toEnumConstant(richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo());
		}
		if(tipoTrasformazione==null || (!tipoTrasformazione.isTemplateFreemarker() && !tipoTrasformazione.isTemplateVelocity())) {
			return new Template(nomeTrasformazione, richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate());
		}
		return _getTemplate(connectionPdD, idPA, "getTemplateTrasformazioneSoapRichiesta", TemplateSource.TRASFORMAZIONE_SOAP_RICHIESTA, nomeTrasformazione, richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate(),
				requestInfo);
	}
	public Template getTemplateTrasformazioneRisposta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		TipoTrasformazione tipoTrasformazione = null;
		if(risposta.getConversioneTipo()!=null) {
			tipoTrasformazione = TipoTrasformazione.toEnumConstant(risposta.getConversioneTipo());
		}
		if(tipoTrasformazione==null || (!tipoTrasformazione.isTemplateFreemarker() && !tipoTrasformazione.isTemplateVelocity())) {
			return new Template(nomeTrasformazione, risposta.getConversioneTemplate());
		}
		return _getTemplate(connectionPdD, idPA, "getTemplateTrasformazioneRisposta", TemplateSource.TRASFORMAZIONE_RISPOSTA, nomeTrasformazione+"@@"+risposta.getNome(), risposta.getConversioneTemplate(),
				requestInfo);
	}
	public Template getTemplateTrasformazioneSoapRisposta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		TipoTrasformazione tipoTrasformazione = null;
		if(risposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo()!=null) {
			tipoTrasformazione = TipoTrasformazione.toEnumConstant(risposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo());
		}
		if(tipoTrasformazione==null || (!tipoTrasformazione.isTemplateFreemarker() && !tipoTrasformazione.isTemplateVelocity())) {
			return new Template(nomeTrasformazione, risposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate());
		}
		return _getTemplate(connectionPdD, idPA, "getTemplateTrasformazioneSoapRisposta", TemplateSource.TRASFORMAZIONE_SOAP_RISPOSTA, nomeTrasformazione+"@@"+risposta.getNome(), risposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate(),
				requestInfo);
	}
	
	public Template getTemplateConnettoreMultiploSticky(Connection connectionPdD,IDPortaApplicativa idPA, byte[] template,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, idPA, "getTemplateConnettoreMultiploSticky", TemplateSource.CONNETTORI_MULTIPLI_STICKY, "sticky", template,
				requestInfo);
	}
	public Template getTemplateConnettoreMultiploCondizionale(Connection connectionPdD,IDPortaApplicativa idPA, String nomeRegola, byte[] template,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, idPA, "getTemplateConnettoreMultiploCondizionale", TemplateSource.CONNETTORI_MULTIPLI_CONSEGNA_CONDIZIONALE, (nomeRegola!=null ? nomeRegola : "___default___"), template,
				requestInfo);
	}
	
	public Template getTemplateCorrelazioneApplicativaRichiesta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeRegola, byte[] template,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, idPA, "getTemplateCorrelazioneApplicativaRichiesta", TemplateSource.CORRELAZIONE_APPLICATIVA_RICHIESTA, (nomeRegola!=null ? nomeRegola : "___default___"), template,
				requestInfo);
	}
	public Template getTemplateCorrelazioneApplicativaRisposta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeRegola, byte[] template,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, idPA, "getTemplateCorrelazioneApplicativaRisposta", TemplateSource.CORRELAZIONE_APPLICATIVA_RISPOSTA, (nomeRegola!=null ? nomeRegola : "___default___"), template,
				requestInfo);
	}
	
	private Template _getTemplate(Connection connectionPdD,IDPortaApplicativa idPA, String nomeMetodo, TemplateSource templateSource, String identificativo, byte[] templateBytes,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		String key = getKey_getPortaApplicativaTemplate(idPA, templateSource, identificativo);
	
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getTemplate(key);
			if(o!=null && o instanceof Template) {
				return (Template) o;
			}
		}
	
		Template template = _getTemplate(connectionPdD, idPA, nomeMetodo, templateSource, identificativo, templateBytes, key);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addTemplate(key, template, 
					requestInfo.getIdTransazione());
		}
		return template;
	}
	private Template _getTemplate(Connection connectionPdD,IDPortaApplicativa idPA, String nomeMetodo, TemplateSource templateSource, String identificativo, byte[] templateBytes,
		String key) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(idPA == null)
			throw new DriverConfigurazioneException("["+nomeMetodo+"]: Parametro non definito (idPA is null)");
		if(idPA.getNome()==null)
			throw new DriverConfigurazioneException("["+nomeMetodo+"]: Parametro non definito (idPA.getNome() is null)");

		// se e' attiva una cache provo ad utilizzarla
		if(this.cache!=null) {
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					Template template = (Template) response.getObject();
					if(template!=null){
						return template;
					}
				}
			}
		}

		Template template = null;
		
		SemaphoreLock lock = semaphore_getTemplateTrasformazionePA.acquireThrowRuntime(nomeMetodo);
		try {
			
			if(this.cache!=null) {
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
				if(response != null){
					if(response.getException()!=null){
						if(notFoundClassName.equals(response.getException().getClass().getName()))
							throw (DriverConfigurazioneNotFound) response.getException();
						else
							throw (DriverConfigurazioneException) response.getException();
					}else{
						template = (Template) response.getObject();
						if(template!=null){
							return template;
						}
					}
				}
			}
			
			template = new Template(key, templateBytes);
			
			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				this.logInfo("Aggiungo oggetto ["+key+"] in cache");
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject((java.io.Serializable)template);
					this.cache.put(key,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+key+"]: "+e.getMessage());
				}
			}
			
		}finally {
			semaphore_getTemplateTrasformazionePA.release(lock, nomeMetodo);
		}

		return template;

	}  
	
	public Template getTemplateIntegrazione(Connection connectionPdD,IDPortaApplicativa idPA, File file,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, idPA, "getTemplateIntegrazione", TemplateSource.INTEGRAZIONE, file,
				requestInfo);
	}
	private Template _getTemplate(Connection connectionPdD,IDPortaApplicativa idPA, String nomeMetodo, TemplateSource templateSource, File file,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
			
		String key = getKey_getPortaApplicativaTemplate(idPA, templateSource, file.getAbsolutePath());
		
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getTemplate(key);
			if(o!=null && o instanceof Template) {
				return (Template) o;
			}
		}
		
		Template template = _getTemplate(connectionPdD, idPA, nomeMetodo, templateSource, file, key);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addTemplate(key, template, 
					requestInfo.getIdTransazione());
		}
		return template;
	}
	private Template _getTemplate(Connection connectionPdD,IDPortaApplicativa idPA, String nomeMetodo, TemplateSource templateSource, File file,
			String key) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// se e' attiva una cache provo ad utilizzarla	
		if(this.cache!=null) { 
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					Template template = (Template) response.getObject();
					if(template!=null){
						return template;
					}
				}
			}
		}

		Template template = null;
		
		SemaphoreLock lock = semaphore_getTemplateTrasformazionePA.acquireThrowRuntime(nomeMetodo);
		try {
			
			if(this.cache!=null) {
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
				if(response != null){
					if(response.getException()!=null){
						if(notFoundClassName.equals(response.getException().getClass().getName()))
							throw (DriverConfigurazioneNotFound) response.getException();
						else
							throw (DriverConfigurazioneException) response.getException();
					}else{
						template = (Template) response.getObject();
						if(template!=null){
							return template;
						}
					}
				}
			}
			
			ContentFile cf = getContentFileEngine(file);
			if(cf.isExists()) {
				template = new Template(key, cf.getContent());
			}
			else {
				throw new DriverConfigurazioneException("File '"+file.getAbsolutePath()+"' cannot exists");
			}
			
			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				this.logInfo("Aggiungo oggetto ["+key+"] in cache");
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject((java.io.Serializable)template);
					this.cache.put(key,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+key+"]: "+e.getMessage());
				}
			}
			
		}finally {
			semaphore_getTemplateTrasformazionePA.release(lock, nomeMetodo);
		}

		return template;

	} 
	
	
	 
	
	
	// SERVIZIO APPLICATIVO
	
	protected static String _getKey_getServizioApplicativo(IDServizioApplicativo idServizioApplicativo){
		String key = "getServizioApplicativo_" + idServizioApplicativo.getIdSoggettoProprietario().getTipo() + idServizioApplicativo.getIdSoggettoProprietario().getNome()+"_"+
				idServizioApplicativo.getNome();
		return key;
	}
	public ServizioApplicativo getServizioApplicativo(Connection connectionPdD,IDServizioApplicativo idServizioApplicativo)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(idServizioApplicativo == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (idServizioApplicativo)");	
		if(idServizioApplicativo.getNome()==null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (nome)");
		if(idServizioApplicativo.getIdSoggettoProprietario()==null || idServizioApplicativo.getIdSoggettoProprietario().getTipo()==null || idServizioApplicativo.getIdSoggettoProprietario().getNome()==null )
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (soggetto proprietario)");
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_getServizioApplicativo(idServizioApplicativo);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativo",connectionPdD,ConfigurazionePdDType.config,idServizioApplicativo);
		}else{
			s = (ServizioApplicativo) this.getObject("getServizioApplicativo",connectionPdD,ConfigurazionePdDType.config,idServizioApplicativo);
		}

		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("Servizio Applicativo non trovato");
	} 
	
	protected static String _toKey_getServizioApplicativoByCredenzialiBasicPrefix(){
		return "getServizioApplicativoByCredenzialiBasic";
	}
	private String _getKey_getServizioApplicativoByCredenzialiBasic(String aUser,String aPassword,
    		List<String> tipiSoggetto){
		String key = _toKey_getServizioApplicativoByCredenzialiBasicPrefix();
		key = key +"_"+aUser+"_"+aPassword;
		if(tipiSoggetto!=null && !tipiSoggetto.isEmpty()) {
			key = key +"_"+tipiSoggetto.toString();
		}
		return key;
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(Connection connectionPdD,String aUser,String aPassword, CryptConfig config)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getServizioApplicativoByCredenzialiBasic(connectionPdD, aUser, aPassword, config, null);
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(Connection connectionPdD,String aUser,String aPassword, CryptConfig config,
    		List<String> tipiSoggetto)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(aUser == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (username)");	
		if(aPassword == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (password)");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getServizioApplicativoByCredenzialiBasic(aUser, aPassword, tipiSoggetto);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
		if(tipiSoggetto!=null) {
			if(this.cache!=null){
				s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoByCredenzialiBasic",connectionPdD,ConfigurazionePdDType.config,aUser,aPassword, config, tipiSoggetto);
			}else{
				s = (ServizioApplicativo) this.getObject("getServizioApplicativoByCredenzialiBasic",connectionPdD,ConfigurazionePdDType.config,aUser,aPassword, config, tipiSoggetto);
			}
		}
		else {
			if(this.cache!=null){
				s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoByCredenzialiBasic",connectionPdD,ConfigurazionePdDType.config,aUser,aPassword, config);
			}else{
				s = (ServizioApplicativo) this.getObject("getServizioApplicativoByCredenzialiBasic",connectionPdD,ConfigurazionePdDType.config,aUser,aPassword, config);
			}
		}

		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("Servizio Applicativo non trovato");
	} 
	
	protected static String _toKey_getServizioApplicativoByCredenzialiApiKeyPrefix(boolean appId){
		return (appId ? "getServizioApplicativoByCredenzialiMultipleApiKey_" : "getServizioApplicativoByCredenzialiApiKey_");
	}
	private String _getKey_getServizioApplicativoByCredenzialiApiKey(String aUser,String aPassword, boolean appId,
    		List<String> tipiSoggetto){
		String key = _toKey_getServizioApplicativoByCredenzialiApiKeyPrefix(appId);
		key = key +"_"+aUser+"_"+aPassword;
		if(tipiSoggetto!=null && !tipiSoggetto.isEmpty()) {
			key = key +"_"+tipiSoggetto.toString();
		}
		return key;
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiApiKey(Connection connectionPdD,String aUser,String aPassword, boolean appId, CryptConfig config)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getServizioApplicativoByCredenzialiApiKey(connectionPdD, aUser, aPassword, appId, config, null);
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiApiKey(Connection connectionPdD,String aUser,String aPassword, boolean appId, CryptConfig config,
    		List<String> tipiSoggetto)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(aUser == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (username)");	
		if(aPassword == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (password)");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getServizioApplicativoByCredenzialiApiKey(aUser, aPassword, appId, tipiSoggetto);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
		if(tipiSoggetto!=null) {
			if(this.cache!=null){
				s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoByCredenzialiApiKey",connectionPdD,ConfigurazionePdDType.config,aUser,aPassword, appId, config, tipiSoggetto);
			}else{
				s = (ServizioApplicativo) this.getObject("getServizioApplicativoByCredenzialiApiKey",connectionPdD,ConfigurazionePdDType.config,aUser,aPassword, appId, config, tipiSoggetto);
			}
		}
		else {
			if(this.cache!=null){
				s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoByCredenzialiApiKey",connectionPdD,ConfigurazionePdDType.config,aUser,aPassword, appId, config);
			}else{
				s = (ServizioApplicativo) this.getObject("getServizioApplicativoByCredenzialiApiKey",connectionPdD,ConfigurazionePdDType.config,aUser,aPassword, appId, config);
			}
		}

		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("Servizio Applicativo non trovato");
	} 
	
	protected static String _toKey_getServizioApplicativoByCredenzialiSslPrefix(boolean separator){
		return "getServizioApplicativoByCredenzialiSsl"+(separator?"_":"");
	}
	private String _getKey_getServizioApplicativoByCredenzialiSsl(String aSubject, String Issuer,
    		List<String> tipiSoggetto, 
			boolean includiApplicativiNonModI, boolean includiApplicativiModIEsterni, boolean includiApplicativiModIInterni){
		String key = _toKey_getServizioApplicativoByCredenzialiSslPrefix(false);
		key = key +"_subject:"+aSubject;
		if(Issuer!=null) {
			key = key +"_issuer:"+Issuer;
		}
		else {
			key = key +"_issuer:nonDefinito";
		}
		if(tipiSoggetto!=null && !tipiSoggetto.isEmpty()) {
			key = key +"_"+tipiSoggetto.toString();
			key = key +"_noModi:"+includiApplicativiNonModI;
			key = key +"_modiExt:"+includiApplicativiModIEsterni;
			key = key +"_modiInt:"+includiApplicativiModIInterni;
		}
		return key;
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(Connection connectionPdD,String aSubject, String aIssuer)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getServizioApplicativoByCredenzialiSsl(connectionPdD, aSubject, aIssuer, 
				null);
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(Connection connectionPdD,String aSubject, String aIssuer,
    		List<String> tipiSoggetto)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getServizioApplicativoByCredenzialiSsl(connectionPdD, aSubject, aIssuer, 
				tipiSoggetto, 
				false, false, false);
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(Connection connectionPdD,String aSubject, String aIssuer,
    		List<String> tipiSoggetto, 
			boolean includiApplicativiNonModI, boolean includiApplicativiModIEsterni, boolean includiApplicativiModIInterni)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(aSubject == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (subject)");		
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getServizioApplicativoByCredenzialiSsl(aSubject, aIssuer, tipiSoggetto, 
					includiApplicativiNonModI, includiApplicativiModIEsterni, includiApplicativiModIInterni);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ServizioApplicativo) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		Class<?>[] classArguments = null;
		Object[]values = null;
		boolean driverXml = this.driverConfigurazionePdD instanceof DriverConfigurazioneXML;
		if(tipiSoggetto!=null && !driverXml) {
			if(includiApplicativiNonModI || includiApplicativiModIEsterni || includiApplicativiModIInterni) {
				classArguments = new Class[] {String.class, String.class, List.class, boolean.class, boolean.class, boolean.class};
				values = new Object[] {aSubject , aIssuer, tipiSoggetto, includiApplicativiNonModI, includiApplicativiModIEsterni, includiApplicativiModIInterni}; // passo gli argomenti tramite array poich' aIssuer puo' essere null
			}
			else {
				classArguments = new Class[] {String.class, String.class, List.class};
				values = new Object[] {aSubject , aIssuer, tipiSoggetto}; // passo gli argomenti tramite array poich' aIssuer puo' essere null
			}
		}
		else {
			classArguments = new Class[] {String.class, String.class};
			values = new Object[] {aSubject , aIssuer}; // passo gli argomenti tramite array poich' aIssuer puo' essere null
		}
		ServizioApplicativo s = null;
		if(this.cache!=null){
			s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoByCredenzialiSsl",connectionPdD,ConfigurazionePdDType.config,classArguments, values);
		}else{
			s = (ServizioApplicativo) this.getObject("getServizioApplicativoByCredenzialiSsl",connectionPdD,ConfigurazionePdDType.config,classArguments, values);
		}

		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("Servizio Applicativo non trovato");
	} 
	
	protected static String _toKey_getServizioApplicativoByCredenzialiSslCertPrefix(boolean separator){
		return "getServizioApplicativoByCredenzialiSslCert"+(separator?"_":"");
	}
	private String _getKey_getServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier,
    		List<String> tipiSoggetto, 
			boolean includiApplicativiNonModI, boolean includiApplicativiModIEsterni, boolean includiApplicativiModIInterni) throws DriverConfigurazioneException{
		try {
			String key = _toKey_getServizioApplicativoByCredenzialiSslCertPrefix(false);
			key = key +"_cert:"+certificate.digestBase64Encoded();
			key = key +"_strictVerifier:"+strictVerifier;
			if(tipiSoggetto!=null && !tipiSoggetto.isEmpty()) {
				key = key +"_"+tipiSoggetto.toString();
				key = key +"_noModi:"+includiApplicativiNonModI;
				key = key +"_modiExt:"+includiApplicativiModIEsterni;
				key = key +"_modiInt:"+includiApplicativiModIInterni;
			}
			return key;
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(Connection connectionPdD, CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getServizioApplicativoByCredenzialiSsl(connectionPdD, certificate, strictVerifier, 
				null);
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(Connection connectionPdD, CertificateInfo certificate, boolean strictVerifier,
    		List<String> tipiSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getServizioApplicativoByCredenzialiSsl(connectionPdD, certificate, strictVerifier, 
				tipiSoggetto, 
				false, false, false);
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(Connection connectionPdD, CertificateInfo certificate, boolean strictVerifier,
    		List<String> tipiSoggetto, 
			boolean includiApplicativiNonModI, boolean includiApplicativiModIEsterni, boolean includiApplicativiModIInterni) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(certificate == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (certificate)");		
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getServizioApplicativoByCredenzialiSsl(certificate, strictVerifier, tipiSoggetto, 
					includiApplicativiNonModI, includiApplicativiModIEsterni, includiApplicativiModIInterni);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ServizioApplicativo) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		Class<?>[] classArguments = null;
		Object[]values = null;
		boolean driverXml = this.driverConfigurazionePdD instanceof DriverConfigurazioneXML;
		if(tipiSoggetto!=null && !driverXml) {
			if(includiApplicativiNonModI || includiApplicativiModIEsterni || includiApplicativiModIInterni) {
				classArguments = new Class[] {CertificateInfo.class, boolean.class, List.class, boolean.class, boolean.class, boolean.class};
				values = new Object[] {certificate , strictVerifier, tipiSoggetto, includiApplicativiNonModI, includiApplicativiModIEsterni, includiApplicativiModIInterni};
			}
			else {
				classArguments = new Class[] {CertificateInfo.class, boolean.class, List.class};
				values = new Object[] {certificate , strictVerifier, tipiSoggetto};
			}
		}
		else {
			classArguments = new Class[] {CertificateInfo.class, boolean.class};
			values = new Object[] {certificate , strictVerifier};
		}
		ServizioApplicativo s = null;
		if(this.cache!=null){
			s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoByCredenzialiSsl",connectionPdD,ConfigurazionePdDType.config, classArguments, values);
		}else{
			s = (ServizioApplicativo) this.getObject("getServizioApplicativoByCredenzialiSsl",connectionPdD,ConfigurazionePdDType.config, classArguments, values);
		}

		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("Servizio Applicativo non trovato");
	}
	
	protected static String _toKey_getServizioApplicativoByCredenzialiPrincipalPrefix(){
		return "getServizioApplicativoByCredenzialiPrincipal";
	}
	private String _getKey_getServizioApplicativoByCredenzialiPrincipal(String principal,
    		List<String> tipiSoggetto){
		String key = _toKey_getServizioApplicativoByCredenzialiPrincipalPrefix();
		key = key +"_"+principal;
		if(tipiSoggetto!=null && !tipiSoggetto.isEmpty()) {
			key = key +"_"+tipiSoggetto.toString();
		}
		return key;
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(Connection connectionPdD,String principal)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getServizioApplicativoByCredenzialiPrincipal(connectionPdD, principal, null);
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(Connection connectionPdD,String principal,
    		List<String> tipiSoggetto)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(principal == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (principal)");		
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getServizioApplicativoByCredenzialiPrincipal(principal, tipiSoggetto);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
		if(tipiSoggetto!=null) {
			if(this.cache!=null){
				s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoByCredenzialiPrincipal",connectionPdD,ConfigurazionePdDType.config,principal,tipiSoggetto);
			}else{
				s = (ServizioApplicativo) this.getObject("getServizioApplicativoByCredenzialiPrincipal",connectionPdD,ConfigurazionePdDType.config,principal,tipiSoggetto);
			}
		}
		else {
			if(this.cache!=null){
				s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoByCredenzialiPrincipal",connectionPdD,ConfigurazionePdDType.config,principal);
			}else{
				s = (ServizioApplicativo) this.getObject("getServizioApplicativoByCredenzialiPrincipal",connectionPdD,ConfigurazionePdDType.config,principal);
			}
		}

		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("Servizio Applicativo non trovato");
	}
	
	protected static String _toKey_getServizioApplicativoByCredenzialiTokenPrefix(){
		return "getServizioApplicativoByCredenzialiToken";
	}
	private String _getKey_getServizioApplicativoByCredenzialiToken(String tokenPolicy, String tokenClientId,
    		List<String> tipiSoggetto) throws DriverConfigurazioneException{
		String key = _toKey_getServizioApplicativoByCredenzialiTokenPrefix();
		key = key +"_"+tokenPolicy+"@[https:"+this.isTokenWithHttpsEnabled()+"]"+tokenClientId;
		if(tipiSoggetto!=null && !tipiSoggetto.isEmpty()) {
			key = key +"_"+tipiSoggetto.toString();
		}
		return key;
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiToken(Connection connectionPdD,String tokenPolicy, String tokenClientId)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getServizioApplicativoByCredenzialiToken(connectionPdD, tokenPolicy, tokenClientId, null);
	}
	public ServizioApplicativo getServizioApplicativoByCredenzialiToken(Connection connectionPdD,String tokenPolicy, String tokenClientId,
    		List<String> tipiSoggetto)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Raccolta dati
		if(tokenPolicy == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (tokenPolicy)");		
		if(tokenClientId == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo]: Parametro non definito (tokenClientId)");	
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getServizioApplicativoByCredenzialiToken(tokenPolicy, tokenClientId,tipiSoggetto);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
		if(tipiSoggetto!=null) {
			if(this.cache!=null){
				s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoByCredenzialiToken",connectionPdD,ConfigurazionePdDType.config,tokenPolicy, tokenClientId, this.isTokenWithHttpsEnabled(), tipiSoggetto);
			}else{
				s = (ServizioApplicativo) this.getObject("getServizioApplicativoByCredenzialiToken",connectionPdD,ConfigurazionePdDType.config,tokenPolicy, tokenClientId, this.isTokenWithHttpsEnabled(), tipiSoggetto);
			}
		}
		else {
			if(this.cache!=null){
				s = (ServizioApplicativo) this.getObjectCache(key,"getServizioApplicativoByCredenzialiToken",connectionPdD,ConfigurazionePdDType.config,tokenPolicy, tokenClientId, this.isTokenWithHttpsEnabled());
			}else{
				s = (ServizioApplicativo) this.getObject("getServizioApplicativoByCredenzialiToken",connectionPdD,ConfigurazionePdDType.config,tokenPolicy, tokenClientId, this.isTokenWithHttpsEnabled());
			}
		}

		if(s!=null)
			return s;
		else
			throw new DriverConfigurazioneNotFound("Servizio Applicativo non trovato");
	}
	
	public static String getKey_getConnettoriConsegnaNotifichePrioritarie(String queue){
		String key = "getConnettoriConsegnaNotifichePrioritarie";
		if(queue!=null) {
			key = key +"_"+queue;
		}
		return key;
	}
	@SuppressWarnings("unchecked")
	public List<IDConnettore> getConnettoriConsegnaNotifichePrioritarie(Connection connectionPdD, String queue) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = getKey_getConnettoriConsegnaNotifichePrioritarie(queue);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((List<IDConnettore>) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		List<IDConnettore> l = null;
		if(this.cache!=null){
			l = (List<IDConnettore>) this.getObjectCache(key,"getConnettoriConsegnaNotifichePrioritarie",connectionPdD,ConfigurazionePdDType.config,queue);
		}else{
			l = (List<IDConnettore>) this.getObject("getConnettoriConsegnaNotifichePrioritarie",connectionPdD,ConfigurazionePdDType.config,queue);
		}

		if(l!=null)
			return l;
		else
			throw new DriverConfigurazioneNotFound("Connettori non trovati");
	}
	public void resetConnettoriConsegnaNotifichePrioritarie(Connection connectionPdD, String queue) throws DriverConfigurazioneException{
		org.openspcoop2.core.config.driver.IDriverConfigurazioneGet driver = getDriver(connectionPdD);
		if((driver instanceof DriverConfigurazioneDB)){
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			driverDB.resetConnettoriConsegnaNotifichePrioritarie(queue);
		}
	}
	

	


	
	// CONFIGURAZIONE

	private String _getKey_getRoutingTable(){
		return "getRoutingTable";
	}
	public RoutingTable getRoutingTable(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Se e' attiva una configurazione statica, la utilizzo.
		if(this.configurazioneDinamica==false){
			if(ConfigurazionePdD.routingTable!=null)
				return ConfigurazionePdD.routingTable;
		}

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getRoutingTable();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			r = (RoutingTable) this.getObjectCache(key,"getRoutingTable",connectionPdD,ConfigurazionePdDType.config);
		}else{
			r = (RoutingTable) this.getObject("getRoutingTable",connectionPdD,ConfigurazionePdDType.config);
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

	private static final String METHOD_GET_ACCESSO_DATI_REGISTRO = "getAccessoRegistro";
	private String getKeyMethodGetAccessoRegistro(){
		return METHOD_GET_ACCESSO_DATI_REGISTRO;
	}
	public AccessoRegistro getAccessoRegistro(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Se e' attiva una configurazione statica, la utilizzo.
		if(!this.configurazioneDinamica &&
			ConfigurazionePdD.accessoRegistro!=null) {
			return ConfigurazionePdD.accessoRegistro;
		}

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = getKeyMethodGetAccessoRegistro();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			ar = (AccessoRegistro) this.getObjectCache(key,METHOD_GET_ACCESSO_DATI_REGISTRO,connectionPdD,ConfigurazionePdDType.config);
		}else{
			ar = (AccessoRegistro) this.getObject(METHOD_GET_ACCESSO_DATI_REGISTRO,connectionPdD,ConfigurazionePdDType.config);
		}

		if(ar!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(!this.configurazioneDinamica){
				ConfigurazionePdD.setAccessoRegistro(ar);
			}
			return ar;
		}
		else{
			throw new DriverConfigurazioneNotFound("["+METHOD_GET_ACCESSO_DATI_REGISTRO+"] Configurazione di accesso ai registri non trovata");
		}
	} 

	private static final String METHOD_GET_ACCESSO_DATI_CONFIGURAZIONE = "getAccessoConfigurazione";
	private String getKeyMethodGetAccessoConfigurazione(){
		return METHOD_GET_ACCESSO_DATI_CONFIGURAZIONE;
	}
	public AccessoConfigurazione getAccessoConfigurazione(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Se e' attiva una configurazione statica, la utilizzo.
		if(!this.configurazioneDinamica &&
			ConfigurazionePdD.accessoConfigurazione!=null) {
			return ConfigurazionePdD.accessoConfigurazione;
		}

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this.getKeyMethodGetAccessoConfigurazione();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			object = (AccessoConfigurazione) this.getObjectCache(key,METHOD_GET_ACCESSO_DATI_CONFIGURAZIONE,connectionPdD,ConfigurazionePdDType.config);
		}else{
			object = (AccessoConfigurazione) this.getObject(METHOD_GET_ACCESSO_DATI_CONFIGURAZIONE,connectionPdD,ConfigurazionePdDType.config);
		}

		if(object!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(!this.configurazioneDinamica){
				ConfigurazionePdD.setAccessoConfigurazione(object);
			}
			return object;
		}
		else{
			throw new DriverConfigurazioneNotFound("["+METHOD_GET_ACCESSO_DATI_CONFIGURAZIONE+"] Configurazione di accesso alla configurazione non trovata");
		}
	} 

	private static final String METHOD_GET_ACCESSO_DATI_AUTORIZZAZIONE = "getAccessoDatiAutorizzazione";
	private String getKeyMethodGetAccessoDatiAutorizzazione(){
		return METHOD_GET_ACCESSO_DATI_AUTORIZZAZIONE;
	}
	public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Se e' attiva una configurazione statica, la utilizzo.
		if(!this.configurazioneDinamica &&
			ConfigurazionePdD.accessoDatiAutorizzazione!=null) {
			return ConfigurazionePdD.accessoDatiAutorizzazione;
		}

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this.getKeyMethodGetAccessoDatiAutorizzazione();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			object = (AccessoDatiAutorizzazione) this.getObjectCache(key,METHOD_GET_ACCESSO_DATI_AUTORIZZAZIONE,connectionPdD,ConfigurazionePdDType.config);
		}else{
			object = (AccessoDatiAutorizzazione) this.getObject(METHOD_GET_ACCESSO_DATI_AUTORIZZAZIONE,connectionPdD,ConfigurazionePdDType.config);
		}

		if(object!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(!this.configurazioneDinamica){
				ConfigurazionePdD.setAccessoDatiAutorizzazione(object);
			}
			return object;
		}
		else{
			throw new DriverConfigurazioneNotFound("["+METHOD_GET_ACCESSO_DATI_AUTORIZZAZIONE+"] Configurazione di accesso ai dati di autorizzazione non trovata");
		}
	} 
	
	private static final String METHOD_GET_ACCESSO_DATI_AUTENTICAZIONE = "getAccessoDatiAutenticazione";
	private String getMethodGetAccessoDatiAutenticazione(){
		return METHOD_GET_ACCESSO_DATI_AUTENTICAZIONE;
	}
	public AccessoDatiAutenticazione getAccessoDatiAutenticazione(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(!this.configurazioneDinamica &&
			ConfigurazionePdD.accessoDatiAutenticazione!=null) {
			return ConfigurazionePdD.accessoDatiAutenticazione;
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = getMethodGetAccessoDatiAutenticazione();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((AccessoDatiAutenticazione) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		AccessoDatiAutenticazione object = null;
		if(this.cache!=null){
			object = (AccessoDatiAutenticazione) this.getObjectCache(key,METHOD_GET_ACCESSO_DATI_AUTENTICAZIONE,connectionPdD,ConfigurazionePdDType.config);
		}else{
			object = (AccessoDatiAutenticazione) this.getObject(METHOD_GET_ACCESSO_DATI_AUTENTICAZIONE,connectionPdD,ConfigurazionePdDType.config);
		}
		
		if(object!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(!this.configurazioneDinamica){
				ConfigurazionePdD.setAccessoDatiAutenticazione(object);
			}
			return object;
		}
		else{
			throw new DriverConfigurazioneNotFound("["+METHOD_GET_ACCESSO_DATI_AUTENTICAZIONE+"] Configurazione di accesso ai dati di autenticazione non trovata");
		}
	} 
	
	
	private static final String METHOD_GET_ACCESSO_DATI_GESTIONE_TOKEN = "getAccessoDatiGestioneToken";
	private String getKeyMethodGetAccessoDatiGestioneToken(){
		return METHOD_GET_ACCESSO_DATI_GESTIONE_TOKEN;
	}
	public AccessoDatiGestioneToken getAccessoDatiGestioneToken(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(!this.configurazioneDinamica &&
			ConfigurazionePdD.accessoDatiGestioneToken!=null) {
			return ConfigurazionePdD.accessoDatiGestioneToken;
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = getKeyMethodGetAccessoDatiGestioneToken();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((AccessoDatiGestioneToken) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		AccessoDatiGestioneToken object = null;
		if(this.cache!=null){
			object = (AccessoDatiGestioneToken) this.getObjectCache(key,METHOD_GET_ACCESSO_DATI_GESTIONE_TOKEN,connectionPdD,ConfigurazionePdDType.config);
		}else{
			object = (AccessoDatiGestioneToken) this.getObject(METHOD_GET_ACCESSO_DATI_GESTIONE_TOKEN,connectionPdD,ConfigurazionePdDType.config);
		}
		
		if(object!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(!this.configurazioneDinamica){
				ConfigurazionePdD.setAccessoDatiGestioneToken(object);
			}
			return object;
		}
		else{
			throw new DriverConfigurazioneNotFound("["+METHOD_GET_ACCESSO_DATI_GESTIONE_TOKEN+"] Configurazione di accesso ai dati di gestione token non trovata");
		}
	} 
	
	
	
	private static final String METHOD_GET_ACCESSO_DATI_ATTRIBUTE_AUTHORITY = "getAccessoDatiAttributeAuthority";
	private String getKeyMethodGetAccessoDatiAttributeAuthority(){
		return METHOD_GET_ACCESSO_DATI_ATTRIBUTE_AUTHORITY;
	}
	public AccessoDatiAttributeAuthority getAccessoDatiAttributeAuthority(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(!this.configurazioneDinamica &&
			ConfigurazionePdD.accessoDatiAttributeAuthority!=null) {
			return ConfigurazionePdD.accessoDatiAttributeAuthority;
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = getKeyMethodGetAccessoDatiAttributeAuthority();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((AccessoDatiAttributeAuthority) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		AccessoDatiAttributeAuthority object = null;
		if(this.cache!=null){
			object = (AccessoDatiAttributeAuthority) this.getObjectCache(key,METHOD_GET_ACCESSO_DATI_ATTRIBUTE_AUTHORITY,connectionPdD,ConfigurazionePdDType.config);
		}else{
			object = (AccessoDatiAttributeAuthority) this.getObject(METHOD_GET_ACCESSO_DATI_ATTRIBUTE_AUTHORITY,connectionPdD,ConfigurazionePdDType.config);
		}
		
		if(object!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(!this.configurazioneDinamica){
				ConfigurazionePdD.setAccessoDatiAttributeAuthority(object);
			}
			return object;
		}
		else{
			throw new DriverConfigurazioneNotFound("["+METHOD_GET_ACCESSO_DATI_ATTRIBUTE_AUTHORITY+"] Configurazione di accesso ai dati recuperati tramite attribute authority non trovata");
		}
	} 

	
	private static final String METHOD_GET_ACCESSO_DATI_KEYSTORE = "getAccessoDatiKeystore";
	private String getKeyMethodGetAccessoDatiKeystore(){
		return METHOD_GET_ACCESSO_DATI_KEYSTORE;
	}
	public AccessoDatiKeystore getAccessoDatiKeystore(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(!this.configurazioneDinamica &&
			ConfigurazionePdD.accessoDatiKeystore!=null) {
			return ConfigurazionePdD.accessoDatiKeystore;
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = getKeyMethodGetAccessoDatiKeystore();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((AccessoDatiKeystore) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		AccessoDatiKeystore object = null;
		if(this.cache!=null){
			object = (AccessoDatiKeystore) this.getObjectCache(key,METHOD_GET_ACCESSO_DATI_KEYSTORE,connectionPdD,ConfigurazionePdDType.config);
		}else{
			object = (AccessoDatiKeystore) this.getObject(METHOD_GET_ACCESSO_DATI_KEYSTORE,connectionPdD,ConfigurazionePdDType.config);
		}
		
		if(object!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(!this.configurazioneDinamica){
				ConfigurazionePdD.setAccessoDatiKeystore(object);
			}
			return object;
		}
		else{
			throw new DriverConfigurazioneNotFound("["+METHOD_GET_ACCESSO_DATI_KEYSTORE+"] Configurazione di accesso ai dati di gestione keystore non trovata");
		}
	} 
	
	
	private static final String METHOD_GET_ACCESSO_DATI_RICHIESTE = "getAccessoDatiRichieste";
	private String getKeyMethodGetAccessoDatiRichieste(){
		return METHOD_GET_ACCESSO_DATI_RICHIESTE;
	}
	public AccessoDatiRichieste getAccessoDatiRichieste(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// Se e' attiva una configurazione statica, la utilizzo.
		if(!this.configurazioneDinamica &&
			ConfigurazionePdD.accessoDatiRichieste!=null) {
			return ConfigurazionePdD.accessoDatiRichieste;
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = getKeyMethodGetAccessoDatiRichieste();
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((AccessoDatiRichieste) response.getObject());
				}
			}
		}
			
		// Algoritmo CACHE
		AccessoDatiRichieste object = null;
		if(this.cache!=null){
			object = (AccessoDatiRichieste) this.getObjectCache(key,METHOD_GET_ACCESSO_DATI_RICHIESTE,connectionPdD,ConfigurazionePdDType.config);
		}else{
			object = (AccessoDatiRichieste) this.getObject(METHOD_GET_ACCESSO_DATI_RICHIESTE,connectionPdD,ConfigurazionePdDType.config);
		}
		
		if(object!=null){
			// Se e' attiva una configurazione statica, la utilizzo.
			if(!this.configurazioneDinamica){
				ConfigurazionePdD.setAccessoDatiRichieste(object);
			}
			return object;
		}
		else{
			throw new DriverConfigurazioneNotFound("["+METHOD_GET_ACCESSO_DATI_RICHIESTE+"] Configurazione di accesso ai dati di gestione delle richieste non trovata");
		}
	} 



	public GestioneErrore getGestioneErroreComponenteCooperazione(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.getGestioneErrore(connectionPdD,true);
	}

	public GestioneErrore getGestioneErroreComponenteIntegrazione(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.getGestioneErrore(connectionPdD,false);
	}

	private String _getKey_getGestioneErrore(boolean cooperazione){
		String key = "getGestioneErrore";
		if(cooperazione){
			key = key + "ComponenteCooperazione";
		}else{
			key = key + "ComponenteIntegrazione";
		}
		return key;
	}
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
			key = this._getKey_getGestioneErrore(cooperazione);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
				ge = (GestioneErrore) this.getObjectCache(key,nomeMetodo,connectionPdD,ConfigurazionePdDType.config);
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
			ge = (GestioneErrore) this.getObject(nomeMetodo,connectionPdD,ConfigurazionePdDType.config);
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


	private String _getKey_getConfigurazioneGenerale(){
		return "getConfigurazioneGenerale";
	}
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
			key = this._getKey_getConfigurazioneGenerale();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			c = (Configurazione) this.getObjectCache(key,"getConfigurazioneGenerale",connectionPdD,ConfigurazionePdDType.config);
		}else{
			c = (Configurazione) this.getObject("getConfigurazioneGenerale",connectionPdD,ConfigurazionePdDType.config);
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
	
	public List<String> getEncryptedSystemPropertiesPdD() throws DriverConfigurazioneException{
		return this.driverConfigurazionePdD.getEncryptedSystemPropertiesPdD();
	}
	public SystemProperties getSystemPropertiesPdD(boolean forceDisableBYOKUse) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(forceDisableBYOKUse && this.driverConfigurazionePdD instanceof DriverConfigurazioneDB) {
			return ((DriverConfigurazioneDB)this.driverConfigurazionePdD).getSystemPropertiesPdDWithoutBIOK();
		}
		return this.driverConfigurazionePdD.getSystemPropertiesPdD();
	}
	public void updateSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		if(this.driverConfigurazionePdD instanceof DriverConfigurazioneDB){
			((DriverConfigurazioneDB)this.driverConfigurazionePdD).updateSystemPropertiesPdD(systemProperties);
		}
	}

	private String _getKey_getSystemPropertiesPdDCached(){
		String key = "getSystemPropertiesPdD";
		return key;
	}
	public SystemProperties getSystemPropertiesPdDCached(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getSystemPropertiesPdDCached();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((SystemProperties) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		SystemProperties sp = null;
		if(this.cache!=null){
			try{
				sp = (SystemProperties) this.getObjectCache(key,"getSystemPropertiesPdD",connectionPdD,ConfigurazionePdDType.config);
			}catch(DriverConfigurazioneException e){
				throw e;
			}
		}else{
			sp = (SystemProperties) this.getObject("getSystemPropertiesPdD",connectionPdD,ConfigurazionePdDType.config);
		}

		if(sp!=null){
			return sp;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getSystemPropertiesPdD] GestioneErrore non trovato");
		}
	} 
	
	
	protected static String _getKey_getGenericProperties(String tipologia, String nome){
		String key = "getGenericProperties";
		key = key + "_"+tipologia;
		key = key + "_"+nome;
		return key;
	}
	public GenericProperties getGenericProperties(Connection connectionPdD,String tipologia, String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return  getGenericProperties(connectionPdD, false, tipologia, nome);
	}
	public GenericProperties getGenericProperties(Connection connectionPdD,boolean forceNoCache, String tipologia, String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(!forceNoCache && this.cache!=null){
			key = _getKey_getGenericProperties(tipologia, nome);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((GenericProperties) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		GenericProperties gp = null;
		if(!forceNoCache && this.cache!=null){
			try{
				gp = (GenericProperties) this.getObjectCache(key,"getGenericProperties",connectionPdD,ConfigurazionePdDType.config, tipologia, nome);
			}catch(DriverConfigurazioneException e){
				throw e;
			}
		}else{
			gp = (GenericProperties) this.getObject("getGenericProperties",connectionPdD,ConfigurazionePdDType.config, tipologia, nome);
		}

		if(gp!=null){
			return gp;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getGenericProperties] GestioneErrore non trovato");
		}
	} 
	
	protected static String _getKey_getGenericProperties(String tipologia){
		String key = "getGenericPropertiesList";
		key = key + "_"+tipologia;
		return key;
	}
	public List<GenericProperties> getGenericProperties(Connection connectionPdD,String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return  getGenericProperties(connectionPdD, false, tipologia);
	}
	@SuppressWarnings("unchecked")
	public List<GenericProperties> getGenericProperties(Connection connectionPdD,boolean forceNoCache, String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(!forceNoCache && this.cache!=null){
			key = _getKey_getGenericProperties(tipologia);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((List<GenericProperties>) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		List<GenericProperties> gp = null;
		if(!forceNoCache && this.cache!=null){
			try{
				gp = (List<GenericProperties>) this.getObjectCache(key,"getGenericProperties",connectionPdD,ConfigurazionePdDType.config, tipologia);
			}catch(DriverConfigurazioneException e){
				throw e;
			}
		}else{
			gp = (List<GenericProperties>) this.getObject("getGenericProperties",connectionPdD,ConfigurazionePdDType.config, tipologia);
		}

		if(gp!=null){
			return gp;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getGenericProperties] GestioneErrore non trovato");
		}
	} 
	

	private String _getKey_getConfigurazioneWithOnlyExtendedInfo(){
		return "getConfigurazioneWithOnlyExtendedInfo";
	}
	public Configurazione getConfigurazioneWithOnlyExtendedInfo(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getConfigurazioneWithOnlyExtendedInfo();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			c = (Configurazione) this.getObjectCache(key,"getConfigurazioneWithOnlyExtendedInfo",connectionPdD,ConfigurazionePdDType.config);
		}else{
			c = (Configurazione) this.getObject("getConfigurazioneWithOnlyExtendedInfo",connectionPdD,ConfigurazionePdDType.config);
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
					if(notFoundClassName.equals(response.getException().getClass().getName()))
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
			c = this.getObjectCache(key,"getConfigurazioneExtended",connectionPdD,ConfigurazionePdDType.config, configurazioneGenerale, id);
		}else{
			c = this.getObject("getConfigurazioneExtended",connectionPdD,ConfigurazionePdDType.config, configurazioneGenerale, id);
		}

		if(c!=null){
			return c;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getSingleExtendedInfoConfigurazione] Configurazione Generale non trovata");
		}

	}

	private static org.openspcoop2.utils.Semaphore semaphore_getTemplateTrasformazione = new org.openspcoop2.utils.Semaphore("ConfigurazionePdD_TemplateTrasformazioneConfig");
	protected static String getKey_getTemplate(TemplateSource source, String identificativo){
		return "configurazioneGW_getTemplate_"+source.name()+"_"+identificativo;
	}
	
	public Template getTemplatePolicyNegoziazioneRequest(Connection connectionPdD, String policyName, byte[] template,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, "getTemplatePolicyNegoziazioneRequest", TemplateSource.POLICY_NEGOZIAZIONE_REQUEST, policyName, template,
				requestInfo);
	}
	
	public Template getTemplateAttributeAuthorityRequest(Connection connectionPdD, String attributeAuthorityName, byte[] template,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, "getTemplateAttributeAuthorityRequest", TemplateSource.ATTRIBUTE_AUTHORITY_REQUEST, attributeAuthorityName, template,
				requestInfo);
	}
	
	private Template _getTemplate(Connection connectionPdD, String nomeMetodo, TemplateSource templateSource, String identificativo, byte[] templateBytes,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
				
		String key = getKey_getTemplate(templateSource, identificativo);
			
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getTemplate(key);
			if(o!=null && o instanceof Template) {
				return (Template) o;
			}
		}
			
		Template template = _getTemplate(connectionPdD, nomeMetodo, templateSource, identificativo, templateBytes, key);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addTemplate(key, template, 
					requestInfo.getIdTransazione());
		}
		return template;
	}
	private Template _getTemplate(Connection connectionPdD, String nomeMetodo, TemplateSource templateSource, String identificativo, byte[] templateBytes,
			String key) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// se e' attiva una cache provo ad utilizzarla
		if(this.cache!=null) {
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					Template template = (Template) response.getObject();
					if(template!=null){
						return template;
					}
				}
			}
		}

		Template template = null;
		
		SemaphoreLock lock = semaphore_getTemplateTrasformazione.acquireThrowRuntime(nomeMetodo);
		try {
			
			if(this.cache!=null) {
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
				if(response != null){
					if(response.getException()!=null){
						if(notFoundClassName.equals(response.getException().getClass().getName()))
							throw (DriverConfigurazioneNotFound) response.getException();
						else
							throw (DriverConfigurazioneException) response.getException();
					}else{
						template = (Template) response.getObject();
						if(template!=null){
							return template;
						}
					}
				}
			}
			
			template = new Template(key, templateBytes);
			
			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				this.logInfo("Aggiungo oggetto ["+key+"] in cache");
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject((java.io.Serializable)template);
					this.cache.put(key,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+key+"]: "+e.getMessage());
				}
			}
			
		}finally {
			semaphore_getTemplateTrasformazione.release(lock, nomeMetodo);
		}

		return template;

	}  
	
	public Template getTemplateIntegrazione(Connection connectionPdD,File file,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return _getTemplate(connectionPdD, "getTemplateIntegrazione", TemplateSource.INTEGRAZIONE, file,
				requestInfo);
	}
	private Template _getTemplate(Connection connectionPdD,String nomeMetodo, TemplateSource templateSource, File file,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
				
		String key = getKey_getTemplate(templateSource, file.getAbsolutePath());
				
		boolean useRequestInfo = requestInfo!=null && requestInfo.getRequestConfig()!=null;
		if(useRequestInfo) {
			Object o = requestInfo.getRequestConfig().getTemplate(key);
			if(o!=null && o instanceof Template) {
				return (Template) o;
			}
		}
				
		Template template = _getTemplate(connectionPdD, nomeMetodo, templateSource, file, key);
		if(useRequestInfo && requestInfo!=null) {
			requestInfo.getRequestConfig().addTemplate(key, template, 
					requestInfo.getIdTransazione());
		}
		return template;
	}
	private Template _getTemplate(Connection connectionPdD,String nomeMetodo, TemplateSource templateSource, File file,
			String key) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// se e' attiva una cache provo ad utilizzarla
		if(this.cache!=null) {
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					Template template = (Template) response.getObject();
					if(template!=null){
						return template;
					}
				}
			}
		}

		Template template = null;
		
		SemaphoreLock lock = semaphore_getTemplateTrasformazione.acquireThrowRuntime(nomeMetodo);
		try {
			
			if(this.cache!=null) {
				org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
				if(response != null){
					if(response.getException()!=null){
						if(notFoundClassName.equals(response.getException().getClass().getName()))
							throw (DriverConfigurazioneNotFound) response.getException();
						else
							throw (DriverConfigurazioneException) response.getException();
					}else{
						template = (Template) response.getObject();
						if(template!=null){
							return template;
						}
					}
				}
			}
			
			ContentFile cf = getContentFileEngine(file);
			if(cf.isExists()) {
				template = new Template(key, cf.getContent());
			}
			else {
				throw new DriverConfigurazioneException("File '"+file.getAbsolutePath()+"' cannot exists");
			}
			
			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				this.logInfo("Aggiungo oggetto ["+key+"] in cache");
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject((java.io.Serializable)template);
					this.cache.put(key,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+key+"]: "+e.getMessage());
				}
			}
			
		}finally {
			semaphore_getTemplateTrasformazione.release(lock, nomeMetodo);
		}

		return template;

	} 
	
	
	
	
	/* ********  R I C E R C A  I D   E L E M E N T I   P R I M I T I V I  ******** */
	
	protected static String _toKey_getAllIdPorteDelegate_method(){
		return "getAllIdPorteDelegate";
	}
	@SuppressWarnings("unchecked")
	public List<IDPortaDelegata> getAllIdPorteDelegate(FiltroRicercaPorteDelegate filtroRicerca,Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return (List<IDPortaDelegata>) _getAllIdEngine(connectionPdD, filtroRicerca, _toKey_getAllIdPorteDelegate_method());
	}
	
	protected static String _toKey_getAllIdPorteApplicative_method(){
		return "getAllIdPorteApplicative";
	}
	@SuppressWarnings("unchecked")
	public List<IDPortaApplicativa> getAllIdPorteApplicative(FiltroRicercaPorteApplicative filtroRicerca,Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return (List<IDPortaApplicativa>) _getAllIdEngine(connectionPdD, filtroRicerca, _toKey_getAllIdPorteApplicative_method());
	}
	
	protected static String _toKey_getAllIdServiziApplicativi_method(){
		return "getAllIdServiziApplicativi";
	}
	@SuppressWarnings("unchecked")
	public List<IDServizioApplicativo> getAllIdServiziApplicativi(FiltroRicercaServiziApplicativi filtroRicerca,Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return (List<IDServizioApplicativo>) _getAllIdEngine(connectionPdD, filtroRicerca, _toKey_getAllIdServiziApplicativi_method());
	}
	
	private List<?> _getAllIdEngine(Connection connectionPdD,Object filtroRicerca,String nomeMetodo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		// Raccolta dati
		if(filtroRicerca == null)
			throw new DriverConfigurazioneException("["+nomeMetodo+"]: Parametro non definito");	

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = nomeMetodo+"_" + filtroRicerca.toString();   
			org.openspcoop2.utils.cache.CacheResponse response = 
				(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((List<?>) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		List<?> list = null;
		if(this.cache!=null){
			list = (List<?>) this.getObjectCache(key,nomeMetodo,connectionPdD,ConfigurazionePdDType.config,filtroRicerca);
		}else{
			list = (List<?>) this.getObject(nomeMetodo,connectionPdD,ConfigurazionePdDType.config,filtroRicerca);
		}

		if(list!=null)
			return list;
		else
			throw new DriverConfigurazioneException("["+nomeMetodo+"] Elementi non trovati");
	}
	
	
	
	
	
	
	/* ******** CONTROLLO TRAFFICO ******** */
	
	private String _getKey_ConfigurazioneControlloTraffico(){
		return "ConfigurazioneControlloTraffico";
	}
	public ConfigurazioneGenerale getConfigurazioneControlloTraffico(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_ConfigurazioneControlloTraffico();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ConfigurazioneGenerale) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		ConfigurazioneGenerale conf = null;
		if(this.cache!=null){
			conf = (ConfigurazioneGenerale) this.getObjectCache(key,"getConfigurazioneControlloTraffico",connectionPdD, ConfigurazionePdDType.controlloTraffico);
		}else{
			conf = (ConfigurazioneGenerale) this.getObject("getConfigurazioneControlloTraffico",connectionPdD, ConfigurazionePdDType.controlloTraffico);
		}

		if(conf!=null)
			return conf;
		else
			throw new DriverConfigurazioneNotFound("[getConfigurazioneControlloTraffico] Configurazione non trovata");
	}
	
	
	private String _getKey_getConfigurazionePolicyRateLimitingGlobali(){
		return "ConfigurazionePolicyRateLimitingGlobali";
	}
	public PolicyConfiguration getConfigurazionePolicyRateLimitingGlobali(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = this._getKey_getConfigurazionePolicyRateLimitingGlobali();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((PolicyConfiguration) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		PolicyConfiguration c = null;
		if(this.cache!=null){
			c = (PolicyConfiguration) this.getObjectCache(key,"getConfigurazionePolicyRateLimitingGlobali",connectionPdD,ConfigurazionePdDType.controlloTraffico);
		}else{
			c = (PolicyConfiguration) this.getObject("getConfigurazionePolicyRateLimitingGlobali",connectionPdD,ConfigurazionePdDType.controlloTraffico);
		}

		if(c!=null){
			return c;
		}
		else{
			throw new DriverConfigurazioneNotFound("[getConfigurazionePolicyRateLimitingGlobali] Configurazione Generale non trovata");
		}
	}
	
	private static final String METHOD_ELENCO_ID_POLICY_ATTIVE_API = "getElencoIdPolicyAttiveAPI";
	public static String getKeyMethodElencoIdPolicyAttiveAPI(TipoPdD tipoPdD, String nomePorta){ // usato anche per resettare la cache puntualmente via jmx, tramite la govwayConsole
		return METHOD_ELENCO_ID_POLICY_ATTIVE_API+"_"+tipoPdD.getTipo()+"_"+nomePorta;
	}
	private static final String METHOD_ELENCO_ID_POLICY_ATTIVE_GLOBALI = "getElencoIdPolicyAttiveGlobali";
	public static String getKeyMethodElencoIdPolicyAttiveGlobali(){ // usato anche per resettare la cache puntualmente via jmx, tramite la govwayConsole
		return METHOD_ELENCO_ID_POLICY_ATTIVE_GLOBALI;
	}
	private static final String SUFFIX_METHOD_DIMENSIONE_MESSAGGIO = "DimensioneMessaggio";
	private static final String METHOD_ELENCO_ID_POLICY_ATTIVE_API_DIMENSIONE_MESSAGGIO = METHOD_ELENCO_ID_POLICY_ATTIVE_API+SUFFIX_METHOD_DIMENSIONE_MESSAGGIO;
	public static String getKeyMethodElencoIdPolicyAttiveAPIDimensioneMessaggio(TipoPdD tipoPdD, String nomePorta){ // usato anche per resettare la cache puntualmente via jmx, tramite la govwayConsole
		return METHOD_ELENCO_ID_POLICY_ATTIVE_API_DIMENSIONE_MESSAGGIO+"_"+tipoPdD.getTipo()+"_"+nomePorta;
	}
	private static final String METHOD_ELENCO_ID_POLICY_ATTIVE_GLOBALI_DIMENSIONE_MESSAGGIO = METHOD_ELENCO_ID_POLICY_ATTIVE_GLOBALI+SUFFIX_METHOD_DIMENSIONE_MESSAGGIO;
	public static String getKeyMethodElencoIdPolicyAttiveGlobaliDimensioneMessaggio(){ // usato anche per resettare la cache puntualmente via jmx, tramite la govwayConsole
		return METHOD_ELENCO_ID_POLICY_ATTIVE_GLOBALI_DIMENSIONE_MESSAGGIO;
	}
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveAPI(Connection connectionPdD, boolean useCache, TipoPdD tipoPdD, String nomePorta) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this._getElencoIdPolicyAttive(connectionPdD, useCache, tipoPdD, nomePorta, true,
				false);
	}
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveGlobali(Connection connectionPdD, boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this._getElencoIdPolicyAttive(connectionPdD, useCache, null, null, false,
				false);
	}
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveAPIDimensioneMessaggio(Connection connectionPdD, boolean useCache, TipoPdD tipoPdD, String nomePorta) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this._getElencoIdPolicyAttive(connectionPdD, useCache, tipoPdD, nomePorta, true,
				true);
	}
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveGlobaliDimensioneMessaggio(Connection connectionPdD, boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this._getElencoIdPolicyAttive(connectionPdD, useCache, null, null, false,
				true);
	}
	@SuppressWarnings("unchecked")
	private Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> _getElencoIdPolicyAttive(Connection connectionPdD, boolean useCache,
			TipoPdD tipoPdD, String nomePorta, boolean api, 
			boolean policyDimensioneMessaggio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		String prefix = "";
		if(policyDimensioneMessaggio) {
			prefix = " [DimensioneMessaggio]";
		}
		
		if(api) {
			if(tipoPdD==null) {
				throw new DriverConfigurazioneException("Tipo PdD non fornito; richiesto per una policy API"+prefix);
			}
			if(nomePorta==null) {
				throw new DriverConfigurazioneException("Nome Porta non fornito; richiesto per una policy API"+prefix);
			}
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null && useCache){
			if(api) {
				if(policyDimensioneMessaggio) {
					key = getKeyMethodElencoIdPolicyAttiveAPIDimensioneMessaggio(tipoPdD, nomePorta);
				}
				else {
					key = getKeyMethodElencoIdPolicyAttiveAPI(tipoPdD, nomePorta);
				}
			}
			else {
				if(policyDimensioneMessaggio) {
					key = getKeyMethodElencoIdPolicyAttiveGlobaliDimensioneMessaggio();
				}
				else {
					key = getKeyMethodElencoIdPolicyAttiveGlobali();
				}
			}
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive>) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		String nomeMetodo = api? METHOD_ELENCO_ID_POLICY_ATTIVE_API : METHOD_ELENCO_ID_POLICY_ATTIVE_GLOBALI;
		if(policyDimensioneMessaggio) {
			nomeMetodo = nomeMetodo + SUFFIX_METHOD_DIMENSIONE_MESSAGGIO;
		}
		Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> elenco = null;
		if(this.cache!=null  && useCache){
			if(api) {
				elenco = (Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive>) this.getObjectCache(key,nomeMetodo,connectionPdD, ConfigurazionePdDType.controlloTraffico, tipoPdD, nomePorta);
			}
			else {
				elenco = (Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive>) this.getObjectCache(key,nomeMetodo,connectionPdD, ConfigurazionePdDType.controlloTraffico);
			}
		}else{
			if(api) {
				elenco = (Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive>) this.getObject(nomeMetodo,connectionPdD, ConfigurazionePdDType.controlloTraffico, tipoPdD, nomePorta);
			}
			else {
				elenco = (Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive>) this.getObject(nomeMetodo,connectionPdD, ConfigurazionePdDType.controlloTraffico);
			}
		}

		if(elenco!=null)
			return elenco;
		else
			throw new DriverConfigurazioneNotFound("["+nomeMetodo+"] Policy non trovate");
	}
	
	
	protected static String _toKey_AttivazionePolicyPrefix() {
		return "AttivazionePolicy_";
	}
	public static String _getKey_AttivazionePolicy(String id){ // usato anche per resettare la cache puntualmente via jmx, tramite la govwayConsole
		return _toKey_AttivazionePolicyPrefix()+id;
	}
	public AttivazionePolicy getAttivazionePolicy(Connection connectionPdD, boolean useCache, String id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null && useCache){
			key = _getKey_AttivazionePolicy(id);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((AttivazionePolicy) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		AttivazionePolicy policy = null;
		if(this.cache!=null  && useCache){
			policy = (AttivazionePolicy) this.getObjectCache(key,"getAttivazionePolicy",connectionPdD, ConfigurazionePdDType.controlloTraffico, id);
		}else{
			policy = (AttivazionePolicy) this.getObject("getAttivazionePolicy",connectionPdD, ConfigurazionePdDType.controlloTraffico, id);
		}

		if(policy!=null)
			return policy;
		else
			throw new DriverConfigurazioneNotFound("[getAttivazionePolicy] Policy non trovata");
	}
	
	
	public static String _getKey_ElencoIdPolicy(){ // utilizzabile per resettare la cache puntualmente via jmx, tramite la govwayConsole
		return "ElencoIdPolicy";
	}
	public ElencoIdPolicy getElencoIdPolicy(Connection connectionPdD, boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null && useCache){
			key = _getKey_ElencoIdPolicy();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ElencoIdPolicy) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		ElencoIdPolicy elenco = null;
		if(this.cache!=null  && useCache){
			elenco = (ElencoIdPolicy) this.getObjectCache(key,"getElencoIdPolicy",connectionPdD, ConfigurazionePdDType.controlloTraffico);
		}else{
			elenco = (ElencoIdPolicy) this.getObject("getElencoIdPolicy",connectionPdD, ConfigurazionePdDType.controlloTraffico);
		}

		if(elenco!=null)
			return elenco;
		else
			throw new DriverConfigurazioneNotFound("[getElencoIdPolicy] Policy non trovate");
	}
	
	
	public static String _getKey_ConfigurazionePolicy(String id){ // utilizzabile per resettare la cache puntualmente via jmx, tramite la govwayConsole
		return "ConfigurazionePolicy_"+id;
	}
	public ConfigurazionePolicy getConfigurazionePolicy(Connection connectionPdD, boolean useCache, String id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null && useCache){
			key = _getKey_AttivazionePolicy(id);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((ConfigurazionePolicy) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		ConfigurazionePolicy policy = null;
		if(this.cache!=null  && useCache){
			policy = (ConfigurazionePolicy) this.getObjectCache(key,"getConfigurazionePolicy",connectionPdD, ConfigurazionePdDType.controlloTraffico, id);
		}else{
			policy = (ConfigurazionePolicy) this.getObject("getConfigurazionePolicy",connectionPdD, ConfigurazionePdDType.controlloTraffico, id);
		}

		if(policy!=null)
			return policy;
		else
			throw new DriverConfigurazioneNotFound("[getConfigurazionePolicy] Policy non trovata");
	}
	
	
	
	
	
	
	/* ******** PLUGINS ******** */
	
	public IRegistroPluginsReader getRegistroPluginsReader() {
		return this.configurazionePdD_registroPlugins;
	}
	
	public static String _getKey_PluginClassName(String tipoPlugin, String tipo){
		return "PluginClassName_"+tipoPlugin+"#"+tipo;
	}
	public String getPluginClassName(Connection connectionPdD, String tipoPlugin, String tipo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_PluginClassName(tipoPlugin, tipo);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((String) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		String className = null;
		if(this.cache!=null){
			className = (String) this.getObjectCache(key,"getPluginClassName",connectionPdD, ConfigurazionePdDType.plugins, tipoPlugin, tipo);
		}else{
			className = (String) this.getObject("getPluginClassName",connectionPdD, ConfigurazionePdDType.plugins, tipoPlugin, tipo);
		}

		if(className!=null)
			return className;
		else
			throw new DriverConfigurazioneNotFound("[getPluginClassName] plugin non trovato");
	}
	
	
	public static String _getKey_PluginClassNameByFilter(String tipoPlugin, String tipo, NameValue ... filtri){
		StringBuilder sb = new StringBuilder();
		sb.append("PluginClassNameByFilter_"+tipoPlugin+"#"+tipo);
		
		if(filtri!=null && filtri.length>0) {
			for (int i = 0; i < filtri.length; i++) {
				NameValue filtro = filtri[i];
				sb.append(".F").append(i).append("_").append(filtro.getName()).append("=").append(filtro.getValue());
			}
		}
		
		return sb.toString();
	}
	public String getPluginClassNameByFilter(Connection connectionPdD, String tipoPlugin, String tipo, NameValue ... filtri) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_PluginClassNameByFilter(tipoPlugin, tipo, filtri);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((String) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		String className = null;
		if(this.cache!=null){
			className = (String) this.getObjectCache(key,"getPluginClassNameByFilter",connectionPdD, ConfigurazionePdDType.plugins, tipoPlugin, tipo, filtri);
		}else{
			className = (String) this.getObject("getPluginClassNameByFilter",connectionPdD, ConfigurazionePdDType.plugins, tipoPlugin, tipo, filtri);
		}

		if(className!=null)
			return className;
		else
			throw new DriverConfigurazioneNotFound("[getPluginClassNameByFilter] plugin non trovato");
	}
	
	public static String _getKey_PluginTipo(String tipoPlugin, String className){
		return "PluginTipo_"+tipoPlugin+"#"+className;
	}
	public String getPluginTipo(Connection connectionPdD, String tipoPlugin, String className) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_PluginTipo(tipoPlugin, className);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((String) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		String tipo = null;
		if(this.cache!=null){
			tipo = (String) this.getObjectCache(key,"getPluginTipo",connectionPdD, ConfigurazionePdDType.plugins, tipoPlugin, className);
		}else{
			tipo = (String) this.getObject("getPluginTipo",connectionPdD, ConfigurazionePdDType.plugins, tipoPlugin, className);
		}

		if(tipo!=null)
			return tipo;
		else
			throw new DriverConfigurazioneNotFound("[getPluginTipo] plugin non trovato");
	}
	
	
	public static String _getKey_PluginTipoByFilter(String tipoPlugin, String className, NameValue ... filtri){
		StringBuilder sb = new StringBuilder();
		sb.append("PluginTipoByFilter_"+tipoPlugin+"#"+className);
		
		if(filtri!=null && filtri.length>0) {
			for (int i = 0; i < filtri.length; i++) {
				NameValue filtro = filtri[i];
				sb.append(".F").append(i).append("_").append(filtro.getName()).append("=").append(filtro.getValue());
			}
		}
		
		return sb.toString();
	}
	public String getPluginTipoByFilter(Connection connectionPdD, String tipoPlugin, String className, NameValue ... filtri) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_PluginTipoByFilter(tipoPlugin, className, filtri);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((String) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		String tipo = null;
		if(this.cache!=null){
			tipo = (String) this.getObjectCache(key,"getPluginTipoByFilter",connectionPdD, ConfigurazionePdDType.plugins, tipoPlugin, className, filtri);
		}else{
			tipo = (String) this.getObject("getPluginTipoByFilter",connectionPdD, ConfigurazionePdDType.plugins, tipoPlugin, className, filtri);
		}

		if(tipo!=null)
			return tipo;
		else
			throw new DriverConfigurazioneNotFound("[getPluginTipoByFilter] plugin non trovato");
	}
	
	
	
	
	
	/* ******** ALLARMI ******** */
	
	private String _getKey_getAllarme(String nomeAllarme){
		return "allarme_"+nomeAllarme;
	}
	public Allarme getAllarme(Connection connectionPdD, String nomeAllarme, boolean searchInCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
	
		if(nomeAllarme==null) {
			throw new DriverConfigurazioneException("Nome allarme non fornito");
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(searchInCache && this.cache!=null){
			key = this._getKey_getAllarme(nomeAllarme);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return (Allarme) response.getObject();
				}
			}
		}

		// Algoritmo CACHE
		Allarme allarme = null;
		if(searchInCache && this.cache!=null){
			allarme = (Allarme) this.getObjectCache(key,"getAllarme",connectionPdD, ConfigurazionePdDType.allarmi, nomeAllarme);
		}else{
			allarme = (Allarme) this.getObject("getAllarme",connectionPdD, ConfigurazionePdDType.allarmi, nomeAllarme);
		}

		if(allarme!=null)
			return allarme;
		else
			throw new DriverConfigurazioneNotFound("[getAllarme] Allarme '"+nomeAllarme+"' non trovato");
		
	}
	
	private String _getKey_searchAllarmi(FiltroRicercaAllarmi filtroRicerca){
		return "searchAllarmi_"+filtroRicerca.toString("_");
	}
	@SuppressWarnings("unchecked")
	public List<Allarme> searchAllarmi(Connection connectionPdD, FiltroRicercaAllarmi filtroRicerca, boolean searchInCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
	
		if(filtroRicerca==null) {
			throw new DriverConfigurazioneException("Filtro ricerca non fornito");
		}
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(searchInCache && this.cache!=null){
			key = this._getKey_searchAllarmi(filtroRicerca);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					if(notFoundClassName.equals(response.getException().getClass().getName()))
						throw (DriverConfigurazioneNotFound) response.getException();
					else
						throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((List<Allarme>) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		List<Allarme> list = null;
		if(searchInCache && this.cache!=null){
			list = (List<Allarme>) this.getObjectCache(key,"searchAllarmi",connectionPdD, ConfigurazionePdDType.allarmi, filtroRicerca);
		}else{
			list = (List<Allarme>) this.getObject("searchAllarmi",connectionPdD, ConfigurazionePdDType.allarmi, filtroRicerca);
		}

		if(list!=null && !list.isEmpty())
			return list;
		else
			throw new DriverConfigurazioneNotFound("[searchAllarmi] Allarmi non trovati");
		
	}
	
	@SuppressWarnings("unchecked")
	public List<IAlarm> instanceAllarmi(Connection connectionPdD, List<Allarme> listAllarmi) throws DriverConfigurazioneException {
		try {
			return (List<IAlarm>) this.getObject("instanceAllarmi",connectionPdD, ConfigurazionePdDType.allarmi, listAllarmi);
		}catch(DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneException(e.getMessage(), e);
		}
	}
	
	public void changeStatus(Connection connectionPdD, IAlarm alarm, AlarmStatus nuovoStatoAllarme) throws DriverConfigurazioneException {
		try {
			this.getObject("changeStatus",connectionPdD, ConfigurazionePdDType.allarmi, alarm, nuovoStatoAllarme);
		}catch(DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneException(e.getMessage(), e);
		}
	}
	
	
	
	
	/* ******** MAPPING ******** */
	
	public static String _getKey_MappingErogazionePortaApplicativaList(IDServizio idServizio, boolean includiAzione){ 
		return "MappingErogazionePA_"+idServizio.toString(includiAzione);
	}
	@SuppressWarnings("unchecked")
	public List<MappingErogazionePortaApplicativa> getMappingErogazionePortaApplicativaList(IDServizio idServizio,Connection connectionPdD) throws DriverConfigurazioneException{
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_MappingErogazionePortaApplicativaList(idServizio, true);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((List<MappingErogazionePortaApplicativa>) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		List<MappingErogazionePortaApplicativa> list = null;
		if(this.cache!=null){
			list = this.getMappingErogazionePortaApplicativaListCache(key, connectionPdD, idServizio);
		}else{
			list = _getMappingErogazionePortaApplicativaList(idServizio, connectionPdD);
		}

		return list;
	} 
	private static org.openspcoop2.utils.Semaphore semaphore_getMappingErogazionePortaApplicativaListCache = new org.openspcoop2.utils.Semaphore("ConfigurazionePdD_MappingErogazionePortaApplicativa");
	@SuppressWarnings("unchecked")
	public List<MappingErogazionePortaApplicativa> getMappingErogazionePortaApplicativaListCache(String keyCache,
			Connection connectionPdD,
			IDServizio idServizio) throws DriverConfigurazioneException{

		SemaphoreLock lock = semaphore_getMappingErogazionePortaApplicativaListCache.acquireThrowRuntime("getMappingErogazionePortaApplicativaListCache");
		List<MappingErogazionePortaApplicativa> obj = null;
		try{

			//			System.out.println("@"+keyCache+"@ INFO CACHE: "+this.cache.toString());
			//			System.out.println("@"+keyCache+"@ KEYS: \n\t"+this.cache.printKeys("\n\t"));

			String methodName = "getMappingErogazionePortaApplicativaList";
			
			// Raccolta dati
			if(keyCache == null)
				throw new DriverConfigurazioneException("["+methodName+"]: KeyCache non definita");	

			// se e' attiva una cache provo ad utilizzarla
			if(this.cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.logDebug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						return (List<MappingErogazionePortaApplicativa>) response.getObject();
					}else if(response.getException()!=null){
						this.logDebug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						throw (Exception) response.getException();
					}else{
						this.logError("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.logDebug("oggetto con chiave ["+keyCache+"] (methodo:"+methodName+") ricerco nella configurazione...");
			obj = _getMappingErogazionePortaApplicativaList(idServizio, connectionPdD);

			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				if(obj!=null){
					this.logInfo("Aggiungo oggetto ["+keyCache+"] in cache");
				}else{
					throw new Exception("Metodo ("+methodName+") ha ritornato un valore null");
				}
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject((java.io.Serializable)obj);
					this.cache.put(keyCache,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
				}
			}

		}catch(DriverConfigurazioneException e){
			throw e;
		}catch(Exception e){
			throw new DriverConfigurazioneException("Configurazione, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}finally {
			semaphore_getMappingErogazionePortaApplicativaListCache.release(lock, "getMappingErogazionePortaApplicativaListCache");
		}

		return obj;
	}
	
	public List<MappingErogazionePortaApplicativa> _getMappingErogazionePortaApplicativaList(IDServizio idServizio,Connection connectionPdD) throws DriverConfigurazioneException{
		
		if(!(this.driverConfigurazionePdD instanceof DriverConfigurazioneDB)) {
			return new ArrayList<>();
		}
		
		Connection con = connectionPdD;
		boolean release = false;
		DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazionePdD;
		try {
			if(con==null) {
				con = driver.getConnection("getMappingErogazionePortaApplicativaList");
				release = true;
			}
			return DBMappingUtils.mappingErogazionePortaApplicativaList(con, this.tipoDatabase, idServizio, false);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		finally {
			if(release) {
				driver.releaseConnection(con);
			}
		}
	}
	
	
	
	public static String _getKey_MappingFruizionePortaDelegataList(IDSoggetto idFruitore, IDServizio idServizio, boolean includiAzione){ 
		return "MappingFruizionePD_"+idFruitore.toString()+"_"+idServizio.toString(includiAzione);
	}
	@SuppressWarnings("unchecked")
	public List<MappingFruizionePortaDelegata> getMappingFruizionePortaDelegataList(IDSoggetto idFruitore, IDServizio idServizio,Connection connectionPdD) throws DriverConfigurazioneException{
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_MappingFruizionePortaDelegataList(idFruitore, idServizio, true);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					throw (DriverConfigurazioneException) response.getException();
				}else{
					return ((List<MappingFruizionePortaDelegata>) response.getObject());
				}
			}
		}

		// Algoritmo CACHE
		List<MappingFruizionePortaDelegata> list = null;
		if(this.cache!=null){
			list = this.getMappingFruizionePortaDelegataListCache(key, connectionPdD, idFruitore, idServizio);
		}else{
			list = _getMappingFruizionePortaDelegataList(idFruitore, idServizio, connectionPdD);
		}

		return list;
		
	} 
	private static org.openspcoop2.utils.Semaphore semaphore_getMappingFruizionePortaDelegataListCache = new org.openspcoop2.utils.Semaphore("ConfigurazionePdD_MappingFruizionePortaDelegata");
	@SuppressWarnings("unchecked")
	public List<MappingFruizionePortaDelegata> getMappingFruizionePortaDelegataListCache(String keyCache,
			Connection connectionPdD,
			IDSoggetto idFruitore, IDServizio idServizio) throws DriverConfigurazioneException{

		SemaphoreLock lock = semaphore_getMappingFruizionePortaDelegataListCache.acquireThrowRuntime("getMappingFruizionePortaDelegataListCache");
		List<MappingFruizionePortaDelegata> obj = null;
		try{

			//			System.out.println("@"+keyCache+"@ INFO CACHE: "+this.cache.toString());
			//			System.out.println("@"+keyCache+"@ KEYS: \n\t"+this.cache.printKeys("\n\t"));

			String methodName = "getMappingFruizionePortaDelegataList";
			
			// Raccolta dati
			if(keyCache == null)
				throw new DriverConfigurazioneException("["+methodName+"]: KeyCache non definita");	

			// se e' attiva una cache provo ad utilizzarla
			if(this.cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.logDebug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						return (List<MappingFruizionePortaDelegata>) response.getObject();
					}else if(response.getException()!=null){
						this.logDebug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						throw (Exception) response.getException();
					}else{
						this.logError("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.logDebug("oggetto con chiave ["+keyCache+"] (methodo:"+methodName+") ricerco nella configurazione...");
			obj = _getMappingFruizionePortaDelegataList(idFruitore, idServizio, connectionPdD);

			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				if(obj!=null){
					this.logInfo("Aggiungo oggetto ["+keyCache+"] in cache");
				}else{
					throw new Exception("Metodo ("+methodName+") ha ritornato un valore null");
				}
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject((java.io.Serializable)obj);
					this.cache.put(keyCache,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
				}
			}

		}catch(DriverConfigurazioneException e){
			throw e;
		}catch(Exception e){
			throw new DriverConfigurazioneException("Configurazione, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}finally {
			semaphore_getMappingFruizionePortaDelegataListCache.release(lock, "getMappingFruizionePortaDelegataListCache");
		}

		return obj;
	}
	
	public List<MappingFruizionePortaDelegata> _getMappingFruizionePortaDelegataList(IDSoggetto idFruitore, IDServizio idServizio,Connection connectionPdD) throws DriverConfigurazioneException{
		
		if(!(this.driverConfigurazionePdD instanceof DriverConfigurazioneDB)) {
			return new ArrayList<>();
		}
		
		Connection con = connectionPdD;
		boolean release = false;
		DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.driverConfigurazionePdD;
		try {
			if(con==null) {
				con = driver.getConnection("getMappingFruizionePortaDelegataList");
				release = true;
			}
			return DBMappingUtils.mappingFruizionePortaDelegataList(con, this.tipoDatabase, idFruitore, idServizio, false);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		finally {
			if(release) {
				driver.releaseConnection(con);
			}
		}
	}
	
	
	
	
	/* ******** FORWARD PROXY ******** */
		
	public static String _getKey_isForwardProxyEnabled(){ 
		String key = "isForwardProxyEnabled";
		return key;
	}
	
	public boolean isForwardProxyEnabled() {
		try {
			return this._isForwardProxyEnabled();
		}catch(Exception e) {
			this.logError("isForwardProxyEnabled error: "+e.getMessage(),e); // non dovrebbe mai succedere
			return false;
		}
	}
	public boolean _isForwardProxyEnabled() throws DriverConfigurazioneException{
			
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_isForwardProxyEnabled();
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					throw (DriverConfigurazioneException) response.getException();
				}else{
					return (Boolean) response.getObject();
				}
			}
		}

		// Algoritmo CACHE
		boolean v = false;
		if(this.cache!=null){
			v = isForwardProxyEnabledCache(key);
		}else{
			v = isForwardProxyEnabledEngine();
		}

		return v;
		
	} 
	
	private static org.openspcoop2.utils.Semaphore semaphore_isForwardProxyEnabledCache = new org.openspcoop2.utils.Semaphore("ConfigurazionePdD_ForwardProxy");
	private boolean isForwardProxyEnabledCache(String keyCache) throws DriverConfigurazioneException{

		SemaphoreLock lock = semaphore_isForwardProxyEnabledCache.acquireThrowRuntime("isForwardProxyEnabledCache");
		boolean obj = false;
		try{

			//			System.out.println("@"+keyCache+"@ INFO CACHE: "+this.cache.toString());
			//			System.out.println("@"+keyCache+"@ KEYS: \n\t"+this.cache.printKeys("\n\t"));

			String methodName = "isForwardProxyEnabledCache";
			
			// Raccolta dati
			if(keyCache == null)
				throw new DriverConfigurazioneException("["+methodName+"]: KeyCache non definita");	

			// se e' attiva una cache provo ad utilizzarla
			if(this.cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.logDebug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						return (Boolean) response.getObject();
					}else if(response.getException()!=null){
						this.logDebug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						throw (Exception) response.getException();
					}else{
						this.logError("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.logDebug("oggetto con chiave ["+keyCache+"] (methodo:"+methodName+") ricerco nella configurazione...");
			obj = isForwardProxyEnabledEngine();

			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				this.logInfo("Aggiungo oggetto ["+keyCache+"] in cache");
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					Boolean v = obj;
					responseCache.setObject((java.io.Serializable)v);
					this.cache.put(keyCache,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
				}
			}

		}catch(DriverConfigurazioneException e){
			throw e;
		}catch(Exception e){
			throw new DriverConfigurazioneException("Configurazione, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}finally {
			semaphore_isForwardProxyEnabledCache.release(lock, "isForwardProxyEnabledCache");
		}

		return obj;
	}
	
	private boolean isForwardProxyEnabledEngine() throws DriverConfigurazioneException{
		
		try {
			return ForwardProxy.isProxyEnabled();
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
	}
	
	
	
	
	public ForwardProxy getForwardProxyConfigFruizione(IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy, RequestInfo requestInfo) throws DriverConfigurazioneException{
		return getForwardProxyConfig(true, dominio, idServizio, policy, requestInfo);
	}
	public ForwardProxy getForwardProxyConfigErogazione(IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy, RequestInfo requestInfo) throws DriverConfigurazioneException{
		return getForwardProxyConfig(false, dominio, idServizio, policy, requestInfo);
	}
	
	protected static String PREFIX_FORWARD_PROXY = "ForwardProxy_";
	protected static String _toKey_ForwardProxyConfigPrefix(boolean fruizione) {
		return PREFIX_FORWARD_PROXY+(fruizione?"Fruizione":"Erogazione");
	}
	protected static String _toKey_ForwardProxyConfigSuffix(IDServizio idServizio) {
		return idServizio.toString(false);
	}
	public static String _toKey_ForwardProxyConfigSuffix(IDGenericProperties policy) {
		return "policy_"+policy.getTipologia()+"_"+policy.getNome();
	}
	public static String _getKey_ForwardProxyConfig(boolean fruizione, IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy){ 
		String key = _toKey_ForwardProxyConfigPrefix(fruizione);
		key = key +"_"+dominio.toString();
		key = key +"_"+_toKey_ForwardProxyConfigSuffix(idServizio);
		if(policy!=null) {
			key = key +"_"+_toKey_ForwardProxyConfigSuffix(policy);
		}
		return key;
	}
	private ForwardProxy getForwardProxyConfig(boolean fruizione, IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy, RequestInfo requestInfo) throws DriverConfigurazioneException{
		
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_ForwardProxyConfig(fruizione, dominio, idServizio, policy);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					throw (DriverConfigurazioneException) response.getException();
				}else{
					return (ForwardProxy) response.getObject();
				}
			}
		}

		// Algoritmo CACHE
		ForwardProxy config = null;
		if(this.cache!=null){
			config = getForwardProxyConfigCache(key, fruizione, dominio, idServizio, policy, requestInfo);
		}else{
			config = getForwardProxyConfigEngine(fruizione, dominio, idServizio, policy, requestInfo);
		}

		return config;
		
	} 
	
	private static org.openspcoop2.utils.Semaphore semaphore_getForwardProxyConfigCache = new org.openspcoop2.utils.Semaphore("ConfigurazionePdD_ForwardProxyConfig");
	private ForwardProxy getForwardProxyConfigCache(String keyCache,boolean fruizione, IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy, RequestInfo requestInfo) throws DriverConfigurazioneException{

		SemaphoreLock lock = semaphore_getForwardProxyConfigCache.acquireThrowRuntime("getForwardProxyConfigCache");
		ForwardProxy obj = null;
		try{

			//			System.out.println("@"+keyCache+"@ INFO CACHE: "+this.cache.toString());
			//			System.out.println("@"+keyCache+"@ KEYS: \n\t"+this.cache.printKeys("\n\t"));

			String methodName = "getForwardProxyConfigCache";
			
			// Raccolta dati
			if(keyCache == null)
				throw new DriverConfigurazioneException("["+methodName+"]: KeyCache non definita");	

			// se e' attiva una cache provo ad utilizzarla
			if(this.cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.logDebug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						return (ForwardProxy) response.getObject();
					}else if(response.getException()!=null){
						this.logDebug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						throw (Exception) response.getException();
					}else{
						this.logError("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.logDebug("oggetto con chiave ["+keyCache+"] (methodo:"+methodName+") ricerco nella configurazione...");
			obj = getForwardProxyConfigEngine(fruizione, dominio, idServizio, policy, requestInfo);

			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				if(obj!=null){
					this.logInfo("Aggiungo oggetto ["+keyCache+"] in cache");
				}else{
					throw new Exception("Metodo ("+methodName+") ha ritornato un valore null");
				}
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject((java.io.Serializable)obj);
					this.cache.put(keyCache,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
				}
			}

		}catch(DriverConfigurazioneException e){
			throw e;
		}catch(Exception e){
			throw new DriverConfigurazioneException("Configurazione, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}finally {
			semaphore_getForwardProxyConfigCache.release(lock, "getForwardProxyConfigCache");
		}

		return obj;
	}
	
	private ForwardProxy getForwardProxyConfigEngine(boolean fruizione, IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy, RequestInfo requestInfo) throws DriverConfigurazioneException{
		
		try {
			return ForwardProxy.getProxyConfigurazione(fruizione, dominio, idServizio, policy, requestInfo);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
	}
	
	
	
	
	
	
	/* ********  GENERIC FILE  ******** */

	public static String _getKey_ContentFile(File file){ 
		String key = "ContentFile_"+file.getAbsolutePath();
		return key;
	}
	
	public ContentFile getContentFile(File file) throws DriverConfigurazioneException{
			
		// se e' attiva una cache provo ad utilizzarla
		String key = null;	
		if(this.cache!=null){
			key = _getKey_ContentFile(file);
			org.openspcoop2.utils.cache.CacheResponse response = 
					(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(key);
			if(response != null){
				if(response.getException()!=null){
					throw (DriverConfigurazioneException) response.getException();
				}else{
					return (ContentFile) response.getObject();
				}
			}
		}

		// Algoritmo CACHE
		ContentFile content = null;
		if(this.cache!=null){
			content = getContentFileCache(key, file);
		}else{
			content = getContentFileEngine(file);
		}

		return content;
		
	} 
	
	private static org.openspcoop2.utils.Semaphore semaphore_getContentFileCache = new org.openspcoop2.utils.Semaphore("ConfigurazionePdD_ContentFile");
	private ContentFile getContentFileCache(String keyCache, File file) throws DriverConfigurazioneException{

		SemaphoreLock lock = semaphore_getContentFileCache.acquireThrowRuntime("getContentFileCache");
		ContentFile obj = null;
		try{

			//			System.out.println("@"+keyCache+"@ INFO CACHE: "+this.cache.toString());
			//			System.out.println("@"+keyCache+"@ KEYS: \n\t"+this.cache.printKeys("\n\t"));

			String methodName = "getContentFileCache";
			
			// Raccolta dati
			if(keyCache == null)
				throw new DriverConfigurazioneException("["+methodName+"]: KeyCache non definita");	

			// se e' attiva una cache provo ad utilizzarla
			if(this.cache!=null){
				org.openspcoop2.utils.cache.CacheResponse response = 
						(org.openspcoop2.utils.cache.CacheResponse) this.cache.get(keyCache);
				if(response != null){
					if(response.getObject()!=null){
						this.logDebug("Oggetto (tipo:"+response.getObject().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						return (ContentFile) response.getObject();
					}else if(response.getException()!=null){
						this.logDebug("Eccezione (tipo:"+response.getException().getClass().getName()+") con chiave ["+keyCache+"] (methodo:"+methodName+") in cache.");
						throw (Exception) response.getException();
					}else{
						this.logError("In cache non e' presente ne un oggetto ne un'eccezione.");
					}
				}
			}

			// Effettuo le query nella mia gerarchia di registri.
			this.logDebug("oggetto con chiave ["+keyCache+"] (methodo:"+methodName+") ricerco nella configurazione...");
			obj = getContentFileEngine(file);

			// Aggiungo la risposta in cache (se esiste una cache)	
			// Se ho una eccezione aggiungo in cache solo una not found
			if( this.cache!=null ){ 	
				//if(obj!=null){
				this.logInfo("Aggiungo oggetto ["+keyCache+"] in cache");
				//}else{
				//	throw new Exception("Metodo ("+methodName+") ha ritornato un valore null");
				//}
				try{	
					org.openspcoop2.utils.cache.CacheResponse responseCache = new org.openspcoop2.utils.cache.CacheResponse();
					responseCache.setObject((java.io.Serializable)obj);
					this.cache.put(keyCache,responseCache);
				}catch(UtilsException e){
					this.logError("Errore durante l'inserimento in cache ["+keyCache+"]: "+e.getMessage());
				}
			}

		}catch(DriverConfigurazioneException e){
			throw e;
		}catch(Exception e){
			throw new DriverConfigurazioneException("Configurazione, Algoritmo di Cache fallito: "+e.getMessage(),e);
		}finally {
			semaphore_getContentFileCache.release(lock, "getContentFileCache");
		}

		return obj;
	}
	
	private ContentFile getContentFileEngine(File file) throws DriverConfigurazioneException{
		
		try {
			ContentFile cf = new ContentFile();
			
			if(file.exists()) {
				cf.setExists(true);
				if(!file.canRead()) {
					throw new Exception("File '"+file.getAbsolutePath()+"' cannot read");
				}
				if(!file.isFile()) {
					throw new Exception("File '"+file.getAbsolutePath()+"' is not file");
				}
				cf.setContent(FileSystemUtilities.readBytesFromFile(file));
			}
			else {
				cf.setExists(false);	
			}
			return cf;

		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
	}
}
