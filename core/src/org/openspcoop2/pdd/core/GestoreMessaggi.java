/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateless;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.Imbustamento;
import org.openspcoop2.pdd.mdb.ImbustamentoRisposte;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.mdb.Sbustamento;
import org.openspcoop2.pdd.mdb.SbustamentoRisposte;
import org.openspcoop2.pdd.services.core.RicezioneBuste;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.services.skeleton.IdentificativoIM;
import org.openspcoop2.pdd.services.skeleton.MessaggioIM;
import org.openspcoop2.pdd.timers.TimerConsegnaContenutiApplicativiThread;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerLock;
import org.openspcoop2.pdd.timers.TimerLockNotAvailableException;
import org.openspcoop2.pdd.timers.TipoLock;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.driver.Riscontri;
import org.openspcoop2.protocol.engine.driver.RollbackRepositoryBuste;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.cache.Cache;
import org.openspcoop2.utils.cache.CacheAlgorithm;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreLogEventGenerator;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.threads.RunnableLogger;
import org.slf4j.Logger;


/**
 * Classe utilizzata per la gestione dei messaggi in OpenSPCoop
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class GestoreMessaggi  {

	/** Logger utilizzato per debug. */
	private Logger log = null;

	private IOpenSPCoopState openspcoopstate=null;
	public IOpenSPCoopState getOpenspcoopstate() {
		return this.openspcoopstate;
	}
	private boolean isRichiesta=false;
	private boolean readyForDrop = false; // usato solo nello stateless, indica se il messaggio puo' essere cancellato dal db
	private boolean portaDiTipoStateless = false;
	/** Indicazione se siamo in modalita di routing stateless */
	private boolean routingStateless;
	private boolean oneWayVersione11 = false;




	/* ********  F I E L D S    S T A T I C    P U B L I C  ******** */
	/** Tabella che mantiene i messaggi da consegnare ai servizi applicativi*/
	public static final String MESSAGGI  = "MESSAGGI";
	/* Colonne dei Messaggi */
	public static final String MESSAGGI_COLUMN_ID_MESSAGGIO  = "ID_MESSAGGIO";
	public static final String MESSAGGI_COLUMN_TIPO_MESSAGGIO  = "TIPO";
	public static final String MESSAGGI_COLUMN_RIFERIMENTO_MSG  = "RIFERIMENTO_MSG";
	public static final String MESSAGGI_COLUMN_ORA_REGISTRAZIONE  = "ORA_REGISTRAZIONE";
	public static final String MESSAGGI_COLUMN_PROPRIETARIO  = "PROPRIETARIO";
	public static final String MESSAGGI_COLUMN_PROTOCOLLO  = "PROTOCOLLO";
	public static final String MESSAGGI_COLUMN_ID_TRANSAZIONE  = "id_transazione";
	public static final String MESSAGGI_COLUMN_CORRELAZIONE_APPLICATIVA_RICHIESTA  = "CORRELAZIONE_APPLICATIVA";
	public static final String MESSAGGI_COLUMN_CORRELAZIONE_APPLICATIVA_RISPOSTA  = "CORRELAZIONE_RISPOSTA";

	/** Tabella che mantiene i servizi applicativi che devono ancora ricevere un dato messaggio */
	public static final String MSG_SERVIZI_APPLICATIVI  = "MSG_SERVIZI_APPLICATIVI";
	/* Colonne dei Messaggi Servizi Applicativi */
	public static final String MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO  = "ID_MESSAGGIO";
	public static final String MSG_SERVIZI_APPLICATIVI_COLUMN_SERVIZIO_APPLICATIVO  = "SERVIZIO_APPLICATIVO";
	public static final String MSG_SERVIZI_APPLICATIVI_COLUMN_ORA_REGISTRAZIONE  = "ORA_REGISTRAZIONE";

	/** Tabella che definisce il contenuto di un messaggio */
	public static final String DEFINIZIONE_MESSAGGI = "DEFINIZIONE_MESSAGGI";
	/* Colonne della tabella contenente i bytes dei messaggi */
	public static final String DEFINIZIONE_MESSAGGI_COLUMN_ID_MESSAGGIO = "ID_MESSAGGIO";
	public static final String DEFINIZIONE_MESSAGGI_COLUMN_TIPO_MESSAGGIO = "TIPO";
	public static final String DEFINIZIONE_MESSAGGI_COLUMN_ORA_REGISTRAZIONE  = "ORA_REGISTRAZIONE";

	/** Tipo di consegna verso un servizio applicativo: tramite Connettore*/
	public static final String CONSEGNA_TRAMITE_CONNETTORE  = "Connettore";
	/** Tipo di consegna verso un servizio applicativo: tramite ConnectionReply*/
	public static final String CONSEGNA_TRAMITE_CONNECTION_REPLY  = "ConnectionReply";
	/** Tipo di consegna verso un servizio applicativo: solo tramite IntegrationManager*/
	public static final String CONSEGNA_TRAMITE_INTEGRATION_MANAGER  = "IntegrationManager";


	/** MessaggioDiagnostico */
	private MsgDiagnostico msgDiag;
	/** Identificativo del Messaggio */
	private String idBusta;




	/** Indica la directory dove effettuare salvataggi */
	private String workDir;
	/** Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX */
	private String tipo;
	/** Properties reader */
	private OpenSPCoop2Properties propertiesReader;


	/** Chiave della cache per i Gestore Messaggi  */
	private static final String GESTORE_MESSAGGI_CACHE_NAME = "gestoreMessaggi";
	/** GestoreMessaggi Cache */
	private static Cache cacheMappingGestoreMessaggi = null;

	private static String cluster_id = null;

	private PdDContext pddContext = null;
//	public PdDContext getPddContext() { Non scommentare, c'è piu' in basso il metodo che legge dallo stato
//		return this.pddContext;
//	}
	private IProtocolFactory<?> protocolFactory = null;
	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}
	
	/** ID Transazione */
	private String idTransazione;

	/* --------------- Cache --------------------*/
	public static void resetCache() throws GestoreMessaggiException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			try{
				GestoreMessaggi.cacheMappingGestoreMessaggi.clear();
			}catch(Exception e){
				throw new GestoreMessaggiException(e.getMessage(),e);
			}
		}
	}
	public static String printStatsCache(String separator) throws GestoreMessaggiException{
		try{
			if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
				return GestoreMessaggi.cacheMappingGestoreMessaggi.printStats(separator);
			}
			else{
				throw new Exception("Cache non abilitata");
			}
		}catch(Exception e){
			throw new GestoreMessaggiException("Visualizzazione Statistiche riguardante la cache dei messaggi gestiti non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws GestoreMessaggiException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null)
			throw new GestoreMessaggiException("Cache gia' abilitata");
		else{
			try{
				GestoreMessaggi.cacheMappingGestoreMessaggi = new Cache(GestoreMessaggi.GESTORE_MESSAGGI_CACHE_NAME);
			}catch(Exception e){
				throw new GestoreMessaggiException(e.getMessage(),e);
			}
		}
	}
	public static void abilitaCache(long dimensioneCache,boolean algoritmoCacheLRU,long itemIdleTime,long itemLifeSecond) throws GestoreMessaggiException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null)
			throw new GestoreMessaggiException("Cache gia' abilitata");
		else{
			try{
				String algoritmoCache = null;
				if(algoritmoCacheLRU)
					algoritmoCache = CostantiConfigurazione.CACHE_LRU.toString() ;
				else
					algoritmoCache = CostantiConfigurazione.CACHE_MRU.toString();
				GestoreMessaggi.initCacheGestoreMessaggi((int)dimensioneCache,algoritmoCache,itemIdleTime,itemLifeSecond,null,null);
			}catch(Exception e){
				throw new GestoreMessaggiException(e.getMessage(),e);
			}
		}
	}
	public static void disabilitaCache() throws GestoreMessaggiException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi==null)
			throw new GestoreMessaggiException("Cache gia' disabilitata");
		else{
			try{
				GestoreMessaggi.cacheMappingGestoreMessaggi.clear();
				GestoreMessaggi.cacheMappingGestoreMessaggi = null;
			}catch(Exception e){
				throw new GestoreMessaggiException(e.getMessage(),e);
			}
		}
	}
	public static boolean isCacheAbilitata(){
		return GestoreMessaggi.cacheMappingGestoreMessaggi != null;
	}
	public static String listKeysCache(String separator) throws GestoreMessaggiException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			try{
				return GestoreMessaggi.cacheMappingGestoreMessaggi.printKeys(separator);
			}catch(Exception e){
				throw new GestoreMessaggiException(e.getMessage(),e);
			}
		}else{
			throw new GestoreMessaggiException("Cache non abilitata");
		}
	}
	public static String getObjectCache(String key) throws GestoreMessaggiException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			try{
				Object o = GestoreMessaggi.cacheMappingGestoreMessaggi.get(key);
				if(o!=null){
					return o.toString();
				}else{
					return "oggetto con chiave ["+key+"] non presente";
				}
			}catch(Exception e){
				throw new GestoreMessaggiException(e.getMessage(),e);
			}
		}else{
			throw new GestoreMessaggiException("Cache non abilitata");
		}
	}
	public static void removeObjectCache(String key) throws GestoreMessaggiException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			try{
				GestoreMessaggi.cacheMappingGestoreMessaggi.remove(key);
			}catch(Exception e){
				throw new GestoreMessaggiException(e.getMessage(),e);
			}
		}else{
			throw new GestoreMessaggiException("Cache non abilitata");
		}
	}



	/** Inizializzazione della Cache, se abilitata */
	public static void initialize() throws Exception{
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		if(properties.isAbilitataCacheGestoreMessaggi()){
			Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			Logger logConsole = OpenSPCoop2Logger.getLoggerOpenSPCoopConsole();
			int dimensioneCache = properties.getDimensioneCacheGestoreMessaggi();
			String algoritmoCache = properties.getAlgoritmoCacheGestoreMessaggi();
			long itemIdleTime = properties.getItemIdleTimeCacheGestoreMessaggi();
			long itemLifeSecond = properties.getItemLifeSecondCacheGestoreMessaggi();
			GestoreMessaggi.initCacheGestoreMessaggi(dimensioneCache,algoritmoCache,itemIdleTime,itemLifeSecond,log,logConsole);
		}
	}

	public static void initCacheGestoreMessaggi(int dimensioneCache,String algoritmoCache,long itemIdleTime,long itemLifeSecond,Logger log,Logger logConsole) throws Exception{

		if(log!=null)
			log.info("Inizializzazione cache gestoreMessaggi");
		if(logConsole!=null)
			logConsole.info("Inizializzazione cache gestoreMessaggi");

		GestoreMessaggi.cacheMappingGestoreMessaggi = new Cache(GestoreMessaggi.GESTORE_MESSAGGI_CACHE_NAME);

		String msg = null;
		if( (dimensioneCache>0) ||
				(algoritmoCache != null) ){

			if( dimensioneCache>0 ){
				try{
					msg = "Dimensione della cache (Gestore Messaggi) impostata al valore: "+dimensioneCache;
					if(log!=null)
						log.info(msg);
					if(logConsole!=null)
						logConsole.info(msg);
					GestoreMessaggi.cacheMappingGestoreMessaggi.setCacheSize(dimensioneCache);
				}catch(Exception error){
					throw new DriverConfigurazioneException("Parametro errato per la dimensione della cache (Gestore Messaggi): "+error.getMessage());
				}
			}
			if(algoritmoCache != null ){
				msg = "Algoritmo di cache (Gestore Messaggi) impostato al valore: "+algoritmoCache;
				if(log!=null)
					log.info(msg);
				if(logConsole!=null)
					logConsole.info(msg);
				if(CostantiConfigurazione.CACHE_MRU.toString().equalsIgnoreCase(algoritmoCache))
					GestoreMessaggi.cacheMappingGestoreMessaggi.setCacheAlgoritm(CacheAlgorithm.MRU);
				else
					GestoreMessaggi.cacheMappingGestoreMessaggi.setCacheAlgoritm(CacheAlgorithm.LRU);
			}

		}

		if( itemIdleTime > 0  ){
			try{
				msg = "Attributo 'IdleTime' (Gestore Messaggi) impostato al valore: "+itemIdleTime;
				if(log!=null)
					log.info(msg);
				if(logConsole!=null)
					logConsole.info(msg);
				GestoreMessaggi.cacheMappingGestoreMessaggi.setItemIdleTime(itemIdleTime);
			}catch(Exception error){
				throw new DriverConfigurazioneException("Parametro errato per l'attributo 'IdleTime' (Gestore Messaggi): "+error.getMessage());
			}
		}

		try{
			msg = "Attributo 'MaxLifeSecond' (Gestore Messaggi) impostato al valore: "+itemLifeSecond;
			if(log!=null)
				log.info(msg);
			if(logConsole!=null)
				logConsole.info(msg);
			GestoreMessaggi.cacheMappingGestoreMessaggi.setItemLifeTime(itemLifeSecond);
		}catch(Exception error){
			throw new DriverConfigurazioneException("Parametro errato per l'attributo 'MaxLifeSecond' (Gestore Messaggi): "+error.getMessage());
		}

	}
	
	
	public static void disableSyncronizedGet() throws UtilsException {
		if(GestoreMessaggi.cacheMappingGestoreMessaggi==null) {
			throw new UtilsException("Cache disabled");
		}
		GestoreMessaggi.cacheMappingGestoreMessaggi.disableSyncronizedGet();
	}
	public static boolean isDisableSyncronizedGet() throws UtilsException {
		if(GestoreMessaggi.cacheMappingGestoreMessaggi==null) {
			throw new UtilsException("Cache disabled");
		}
		return GestoreMessaggi.cacheMappingGestoreMessaggi.isDisableSyncronizedGet();
	}
	



	/** Elementi in cache 
	 * @throws UtilsException */
	public static String cacheToString() throws UtilsException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			StringBuilder bf = new StringBuilder();
			bf.append("Cache GestoreMessaggi: "+GestoreMessaggi.cacheMappingGestoreMessaggi.printStats(" "));
			return bf.toString();
		}else
			return "Cache non abilitata";
	}

	/** Registrazione Mapping rifMsg-id */
	public static void addIntoCache_MappingRifMsgToId(MsgDiagnostico msgDiag, String tipoMessaggio,String riferimentoMessaggio, String idBusta) throws UtilsException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			String key = "mappingRifMsgToId_"+tipoMessaggio+"_"+riferimentoMessaggio;
			GestoreMessaggi.cacheMappingGestoreMessaggi.put(key, idBusta);
			/* PUT SAFE COSTOSO
			try{
				GestoreMessaggi.cacheMappingGestoreMessaggi.putSafe(key, idBusta);
			}catch(ObjectExistsException ex){
				//String oldValue = (String) GestoreMessaggi.cacheMappingGestoreMessaggi.get(key);
				//msgDiag.highDebug("RiferimentoMessaggio ["+tipoMessaggio+"]/["+riferimentoMessaggio+"] gia registrato, con il valore "+oldValue + " (nuovo valore:"+idBusta+")");
				msgDiag.highDebug("RiferimentoMessaggio ["+tipoMessaggio+"]/["+riferimentoMessaggio+"] gia registrato (nuovo valore:"+idBusta+")");
				GestoreMessaggi.cacheMappingGestoreMessaggi.remove(key);
				GestoreMessaggi.cacheMappingGestoreMessaggi.put(key, idBusta);
			}
			 */
			//System.out.println("AGGIORNO CACHE RIFERIMENTO RIF["+tipoMessaggio+"_"+riferimentoMessaggio+"] ID["+idBusta+"]");
		}
	}
	/** Get RiferimentoMessaggio */
	public static String getFromCache_idFromRifMsgMapping(String tipoMessaggio,String riferimentoMessaggio){
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			Object obj = GestoreMessaggi.cacheMappingGestoreMessaggi.get("mappingRifMsgToId_"+tipoMessaggio+"_"+riferimentoMessaggio);
			// System.out.println("GET FROM CACHE RIFERIMENTO RIF["+tipoMessaggio+"_"+riferimentoMessaggio+"] ID TROVATO: ["+obj+"]");
			if(obj!=null)
				return (String) obj;
			else
				return null;
		}else
			return null;
	}
	/** delete exists */
	public static void deleteFromCache_idFromRifMsgMapping(String tipoMessaggio,String riferimentoMessaggio){
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			String key = "mappingRifMsgToId_"+tipoMessaggio+"_"+riferimentoMessaggio;
			try{
				GestoreMessaggi.cacheMappingGestoreMessaggi.remove(key);
				//System.out.println("DELETE FROM CACHE RIF MSG KEY["+key+"]");
			}catch(Exception e){
				//System.out.println("DELETE FROM CACHE RIF MSG KEY["+key+"] ERROR:"+e.getMessage());
			}
		}
	}

	/** Registrazione proprietario */
	public static void addIntoCache_Proprietario(MsgDiagnostico msgDiag, String tipoMessaggio,String idBusta, String proprietario) throws UtilsException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){

			String prop = proprietario;
			if(prop.startsWith(RicezioneContenutiApplicativi.ID_MODULO)){
				prop = RicezioneContenutiApplicativi.ID_MODULO;
			}else if(prop.startsWith(RicezioneBuste.ID_MODULO)){
				prop = RicezioneBuste.ID_MODULO;
			}

			String key = "proprietario_to"+prop+"_("+tipoMessaggio+"_"+idBusta+")";
			GestoreMessaggi.cacheMappingGestoreMessaggi.put(key, proprietario);
			/* PUT SAFE COSTOSO
			try{
				GestoreMessaggi.cacheMappingGestoreMessaggi.putSafe(key, proprietario);
			}catch(ObjectExistsException ex){
				//String oldValue = (String) GestoreMessaggi.cacheMappingGestoreMessaggi.get(key);
				//msgDiag.highDebug("Proprietario ["+tipoMessaggio+"]/["+idBusta+"] gia registrato, con il valore "+oldValue + " (nuovo valore:"+proprietario+")");
				msgDiag.highDebug("Proprietario ["+tipoMessaggio+"]/["+idBusta+"] gia registrato, (nuovo valore:"+proprietario+")");
				GestoreMessaggi.cacheMappingGestoreMessaggi.remove(key);
				GestoreMessaggi.cacheMappingGestoreMessaggi.put(key, proprietario);
			}
			 */
			//System.out.println("AGGIORNO CACHE PROPRIETARIO KEY["+key+"] ID["+proprietario+"]");
		}
	}
	/** Get proprietario */
	public static String getFromCache_Proprietario(String idModulo,String tipoMessaggio,String idBusta){
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){

			String prop = idModulo;
			if(prop.startsWith(RicezioneContenutiApplicativi.ID_MODULO)){
				prop = RicezioneContenutiApplicativi.ID_MODULO;
			}else if(prop.startsWith(RicezioneBuste.ID_MODULO)){
				prop = RicezioneBuste.ID_MODULO;
			}

			String key = "proprietario_to"+prop+"_("+tipoMessaggio+"_"+idBusta+")";
			Object obj = GestoreMessaggi.cacheMappingGestoreMessaggi.get(key);
			//System.out.println("GET FROM CACHE PROPRIETARIO KEY["+key+"] ID TROVATO: ["+obj+"]");
			if(obj!=null)
				return (String) obj;
			else
				return null;
		}else
			return null;
	}
	/** delete proprietario */
	public static void deleteFromCache_Proprietario(String modulo,String tipoMessaggio,String idBusta){
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			String key = "proprietario_to"+modulo+"_("+tipoMessaggio+"_"+idBusta+")";
			try{
				GestoreMessaggi.cacheMappingGestoreMessaggi.remove(key);
				//System.out.println("DELETE FROM CACHE Proprietario KEY["+key+"]");
			}catch(Exception e){
				//System.out.println("DELETE FROM CACHE Proprietario KEY["+key+"] ERROR:"+e.getMessage());
			}
		}
	}
	/** Registrazione messaggio */
	public static void addIntoCache_existsMessage(MsgDiagnostico msgDiag,String tipoMessaggio,String idBusta) throws UtilsException{
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			String key = "existsMessage_"+tipoMessaggio+"_"+idBusta;
			GestoreMessaggi.cacheMappingGestoreMessaggi.put(key, true);
			/* PUT SAFE COSTOSO
			try{
				GestoreMessaggi.cacheMappingGestoreMessaggi.putSafe(key, true);
			}catch(ObjectExistsException ex){
				msgDiag.highDebug("Messaggio (existsMessage) ["+tipoMessaggio+"]/["+idBusta+"] gia registrato");
				GestoreMessaggi.cacheMappingGestoreMessaggi.remove(key);
				GestoreMessaggi.cacheMappingGestoreMessaggi.put(key, true);
			}*/
			//System.out.println("AGGIORNO CACHE EXISTS MESSAGGIO ID["+tipoMessaggio+"_"+idBusta+"]");
		}
	}
	/** exists messaggio */
	public static boolean getFromCache_existsMessage(String tipoMessaggio,String idBusta){
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			Object obj = GestoreMessaggi.cacheMappingGestoreMessaggi.get("existsMessage_"+tipoMessaggio+"_"+idBusta);
			//System.out.println("GET FROM CACHE EXISTS MESSAGGIO["+tipoMessaggio+"_"+idBusta+"] ID TROVATO: ["+obj+"]");
			if(obj!=null)
				return (Boolean) obj;
			else
				return false;
		}else
			return false;
	}
	/** delete exists */
	public static void deleteFromCache_existsMessage(String tipoMessaggio,String idBusta){
		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
			String key = "existsMessage_"+tipoMessaggio+"_"+idBusta;
			try{
				GestoreMessaggi.cacheMappingGestoreMessaggi.remove(key);
				//System.out.println("DELETE FROM CACHE EXISTS MESSAGE KEY["+key+"]");
			}catch(Exception e){
				//System.out.println("DELETE FROM CACHE EXISTS MESSAGE KEY["+key+"] ERROR: "+e.getMessage());
			}
		}
	}



	/** Tabella Hash contenente il mapping tra tipo-id e proprietario */
	private Hashtable<String,String> tableProprietariMessaggiGestiti;

	/**
	 * Aggiunge il proprietario di un idGestito.
	 * 
	 * @param tipo
	 * @param idBusta
	 * @param proprietario
	 */
	private void addProprietarioIntoTable(String tipo,String idBusta,String proprietario){
		this.tableProprietariMessaggiGestiti.put(tipo+"@"+idBusta, proprietario);
	}
	/*
	private String getProprietarioFromTable(String tipo,String idBusta){
		return this.tableProprietariMessaggiGestiti.get(tipo+"@"+idBusta);
	}
	 */
	public void addProprietariMsgGestitiFromTable(Hashtable<String,String> t)throws UtilsException{
		this.tableProprietariMessaggiGestiti.putAll(t);
	}
	public Hashtable<String, String> getTableProprietariMessaggiGestiti() {
		return this.tableProprietariMessaggiGestiti;
	}
	public void addProprietariIntoCache_readFromTable(String modulo,String descrizione,String idBustaRichiestaCorrelata,boolean router){

		if (this.openspcoopstate instanceof OpenSPCoopStateless) return; // NOP

		Enumeration<String> keys = this.tableProprietariMessaggiGestiti.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			String idTipo [] = key.split("@");
			String value = this.tableProprietariMessaggiGestiti.get(key);
			try{
				//System.out.println("Aggiornamento proprietario ["+descrizione+"] (Modulo:"+modulo+") per ["+idTipo[0]+"/"+idTipo[1]+"] a ["+value+"] ...");
				this.msgDiag.highDebug("Aggiornamento proprietario ["+descrizione+"] (Modulo:"+modulo+") per ["+idTipo[0]+"/"+idTipo[1]+"] a ["+value+"] ...");
				GestoreMessaggi.addIntoCache_Proprietario(this.msgDiag,idTipo[0],idTipo[1],value);
				this.msgDiag.highDebug("Aggiornamento proprietario ["+descrizione+"] (Modulo:"+modulo+") per ["+idTipo[0]+"/"+idTipo[1]+"] a ["+value+"] effettuata");

				if(TimerGestoreMessaggi.ID_MODULO.equals(value)){
					//	System.out.println("Gestione eliminazione messaggio");
					this.puliziaCache(modulo, this.idBusta, this.tipo, idBustaRichiestaCorrelata, router);
				}else{
					//System.out.println("Pulizia vecchi proprietari");
					this.puliziaCacheAltriProprietari(modulo, this.idBusta, this.tipo);
				}

			}catch(Exception e){
				//System.out.println("Aggiornamento proprietario ["+descrizione+"] (Modulo:"+modulo+") per ["+idTipo[0]+"/"+idTipo[1]+"] a ["+value+"] ERROR");
				this.msgDiag.logErroreGenerico(e,"GestoreMessaggi.addIntoCache_Proprietario(Descr:"+descrizione+",Modulo:"+modulo+",Per:"+idTipo[0]+"/"+idTipo[1]+",Value:"+value+"");
				this.log.error("Aggiornamento proprietario ["+descrizione+"] (Modulo:"+modulo+") per ["+idTipo[0]+"/"+idTipo[1]+"] a ["+value+"] non riuscita",e);
			}
		}
	}
	public void puliziaCacheAltriProprietari(String moduloVecchio,String idBusta,String tipo){
		//System.out.println("OLD");
		if(moduloVecchio.indexOf(RicezioneContenutiApplicativi.ID_MODULO)>=0){
			GestoreMessaggi.deleteFromCache_Proprietario(RicezioneContenutiApplicativi.ID_MODULO,tipo, idBusta);
		}
		else if(moduloVecchio.indexOf(Imbustamento.ID_MODULO)>=0){
			GestoreMessaggi.deleteFromCache_Proprietario(Imbustamento.ID_MODULO,tipo, idBusta);
		}
		else if(moduloVecchio.indexOf(InoltroBuste.ID_MODULO)>=0){
			GestoreMessaggi.deleteFromCache_Proprietario(InoltroBuste.ID_MODULO,tipo, idBusta);
		}
		else if(moduloVecchio.indexOf(SbustamentoRisposte.ID_MODULO)>=0){
			GestoreMessaggi.deleteFromCache_Proprietario(SbustamentoRisposte.ID_MODULO,tipo, idBusta);
		}
		else if(moduloVecchio.indexOf(RicezioneBuste.ID_MODULO)>=0){
			GestoreMessaggi.deleteFromCache_Proprietario(RicezioneBuste.ID_MODULO,tipo, idBusta);
		}
		else if(moduloVecchio.indexOf(Sbustamento.ID_MODULO)>=0){
			GestoreMessaggi.deleteFromCache_Proprietario(Sbustamento.ID_MODULO,tipo, idBusta);
		}
		else if(moduloVecchio.indexOf(ConsegnaContenutiApplicativi.ID_MODULO)>=0){
			GestoreMessaggi.deleteFromCache_Proprietario(ConsegnaContenutiApplicativi.ID_MODULO,tipo, idBusta);
		}
		else if(moduloVecchio.indexOf(InoltroRisposte.ID_MODULO)>=0){
			GestoreMessaggi.deleteFromCache_Proprietario(InoltroRisposte.ID_MODULO,tipo, idBusta);
		}
		else if(moduloVecchio.indexOf(ImbustamentoRisposte.ID_MODULO)>=0){
			GestoreMessaggi.deleteFromCache_Proprietario(ImbustamentoRisposte.ID_MODULO,tipo, idBusta);
		}
	}
	public void puliziaCache(String modulo,String idBusta,String tipo,String idBustaRichiesta,boolean router){
		try{
			//System.out.println("delete existsMessage....");
			// Pulizia esistenza Messaggio
			GestoreMessaggi.deleteFromCache_existsMessage(tipo, idBusta);

			// Pulizia riferimentoMessaggio
			if(idBustaRichiesta!=null){
				//System.out.println("delete riferimentoMsg....");
				GestoreMessaggi.deleteFromCache_idFromRifMsgMapping(tipo, idBustaRichiesta);
			}

			// Pulizia proprietari
			if(router){
				if(idBustaRichiesta==null){
					// delete Richiesta
					//System.out.println("router richiesta....");
					GestoreMessaggi.deleteFromCache_Proprietario(InoltroBuste.ID_MODULO,tipo, idBusta);
				}else{
					// delete Risposta
					//System.out.println("router risposta....");
					GestoreMessaggi.deleteFromCache_Proprietario(RicezioneBuste.ID_MODULO,tipo, idBusta);
				}
			}else{

				if(idBustaRichiesta==null){
					if(Costanti.OUTBOX.equals(tipo)){
						// portaDelegata richiesta
						//System.out.println("pd richiesta....");
						GestoreMessaggi.deleteFromCache_Proprietario(Imbustamento.ID_MODULO,tipo, idBusta);
						GestoreMessaggi.deleteFromCache_Proprietario(InoltroBuste.ID_MODULO,tipo, idBusta);
					}else{
						// portaApplicativa richiesta
						//System.out.println("pa richiesta....");
						GestoreMessaggi.deleteFromCache_Proprietario(Sbustamento.ID_MODULO,tipo, idBusta);
						GestoreMessaggi.deleteFromCache_Proprietario(ConsegnaContenutiApplicativi.ID_MODULO,tipo, idBusta);
					}
				}else{
					if(Costanti.INBOX.equals(tipo)){
						// portaDelegata risposta
						//System.out.println("pd risposta....");
						GestoreMessaggi.deleteFromCache_Proprietario(SbustamentoRisposte.ID_MODULO,tipo, idBusta);
						GestoreMessaggi.deleteFromCache_Proprietario(RicezioneContenutiApplicativi.ID_MODULO,tipo, idBusta);
					}else{
						// portaApplicativa risposta
						//System.out.println("pa risposta....");
						GestoreMessaggi.deleteFromCache_Proprietario(ImbustamentoRisposte.ID_MODULO,tipo, idBusta);
						// nota: solo una delle due delete sara' effettiva, poiche' il msg di sblocco dato a RicezioneBuste e il msg reale dato a InoltroRisposte hanno id diverso
						GestoreMessaggi.deleteFromCache_Proprietario(RicezioneBuste.ID_MODULO,tipo, idBusta);
						GestoreMessaggi.deleteFromCache_Proprietario(InoltroRisposte.ID_MODULO,tipo, idBusta);
					}
				}
			}
			//System.out.println("fine");

		}catch(Exception e){
			this.msgDiag.logErroreGenerico(e,"GestoreMessaggi.puliziaCache(Modulo:"+modulo+",Per:"+idBusta+"/"+tipo+",Router:"+router+"");
			this.log.error("Pulizia della cache non riuscita (Modulo:"+modulo+") per ["+idBusta+"/"+tipo+"] router["+router+"]",e);
		}
	}




	/** Tabella Hash contenente l'informazione sull'esistenza di un messaggio */
	private Hashtable<String,Boolean> tableMessaggiGestiti;

	/**
	 * Regitra l'id di un msg gestito
	 * 
	 * @param tipo
	 * @param idBusta
	 */
	private void addMessaggioIntoTable(String tipo,String idBusta){
		this.tableMessaggiGestiti.put(tipo+"@"+idBusta,true);
	}
	/*
	public boolean getMessaggioFromTable(String tipo,String idBusta){
		Object v = this.tableMessaggiGestiti.get(tipo+"@"+idBusta);
		if(v!=null)
			return (Boolean )v;
		else
			return false;

	}
	 */
	public void addMsgGestitiFromTable(Hashtable<String,Boolean> t)throws UtilsException{
		this.tableMessaggiGestiti.putAll(t);
	}
	public Hashtable<String, Boolean> getTableMessaggiGestiti() {
		return this.tableMessaggiGestiti;
	}
	public void addMessaggiIntoCache_readFromTable(String modulo,String descrizione){

		if (this.openspcoopstate instanceof OpenSPCoopStateless) return; //NOP

		Enumeration<String> keys = this.tableMessaggiGestiti.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			String idTipo [] = key.split("@");
			Boolean value = this.tableMessaggiGestiti.get(key);
			try{
				this.msgDiag.highDebug("Aggiornamento existsMessage ["+descrizione+"] (Modulo:"+modulo+") per ["+idTipo[0]+"/"+idTipo[1]+"] a ["+value+"] ...");
				GestoreMessaggi.addIntoCache_existsMessage(this.msgDiag,idTipo[0],idTipo[1]);
				this.msgDiag.highDebug("Aggiornamento existsMessage ["+descrizione+"] (Modulo:"+modulo+") per ["+idTipo[0]+"/"+idTipo[1]+"] a ["+value+"] effettuata");
			}catch(Exception e){
				this.msgDiag.logErroreGenerico(e,"GestoreMessaggi.addIntoCache_existsMessage(Descr:"+descrizione+",Modulo:"+modulo+",Per:"+idTipo[0]+"/"+idTipo[1]+",Value:"+value+"");
				this.log.error("Aggiornamento existsMessage ["+descrizione+"] (Modulo:"+modulo+") per ["+idTipo[0]+"/"+idTipo[1]+"] a ["+value+"] non riuscita",e);
			}
		}
	}



	/* ********  S T A T I C  JDBC ADAPTER  ******** */

	/** adapterJDBC di OpenSPCoop di OpenSPCoop */
	private static IJDBCAdapter adapter=null;
	private static boolean adapterInitialized = false;
	private static synchronized void initAdapter(Logger log,OpenSPCoop2Properties propertiesReader){
		if(GestoreMessaggi.adapterInitialized==false){
			if(propertiesReader.isRepositoryOnFS() == false){
				try{
					String jdbcAdapter = propertiesReader.getRepositoryJDBCAdapter();
					if(propertiesReader.getDatabaseType()!=null && TipiDatabase.DEFAULT.equals(jdbcAdapter)){
						//System.out.println("PASSO DA FACTORY GESTORE MESSAGGI");
						GestoreMessaggi.adapter = JDBCAdapterFactory.createJDBCAdapter(propertiesReader.getDatabaseType());
					}
					else{
						ClassNameProperties classNameProperties = ClassNameProperties.getInstance();
						//		Ricerco connettore
						String adapterClass = classNameProperties.getJDBCAdapter(jdbcAdapter);
						if(adapterClass == null){
							log.error("Inizializzione GestoreMessaggi non riuscita: AdapterClass non registrata ["+propertiesReader.getRepositoryJDBCAdapter()+"]");
						}
						Loader loader = Loader.getInstance();
						GestoreMessaggi.adapter = (IJDBCAdapter) loader.newInstance(adapterClass);
					}
				}catch(Exception e){
					log.error("Inizializzione GestoreMessaggi non riuscita: AdapterClass non trovata ["+propertiesReader.getRepositoryJDBCAdapter()+"]:"+e.getMessage(),e);
				}
			}
			GestoreMessaggi.adapterInitialized = true;
		}
	}



	/* ********  S T A T I C  PDDCONTEXT SERIALIZER  ******** */

	/** PdDContextSerializer */
	private static IPdDContextSerializer pddContextSerializer = null;
	private static boolean pddContextSerializerInitialized = false;
	private static synchronized void initPddContextSerializer(Logger log,OpenSPCoop2Properties propertiesReader){
		if(GestoreMessaggi.pddContextSerializerInitialized==false){
			String pddContextSerializerClass = propertiesReader.getPddContextSerializer();
			if(pddContextSerializerClass!=null && !CostantiConfigurazione.NONE.equals(pddContextSerializerClass)){
				try{
					Loader loader = Loader.getInstance();
					GestoreMessaggi.pddContextSerializer = (IPdDContextSerializer) loader.newInstance(pddContextSerializerClass);
				}catch(Exception e){
					log.error("Inizializzione GestoreMessaggi non riuscita: IPdDContextSerializer non trovata ["+pddContextSerializerClass+"]:"+e.getMessage(),e);
				}
			}
			GestoreMessaggi.pddContextSerializerInitialized = true;
		}
	}



	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * @param openspcoopstate Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	public GestoreMessaggi(IOpenSPCoopState openspcoopstate, boolean isRichiesta,MsgDiagnostico msgDiag,PdDContext pddContext){
		this(openspcoopstate, isRichiesta, null,null,OpenSPCoop2Logger.getLoggerOpenSPCoopCore(),msgDiag,pddContext);
	}
	/**
	 * Costruttore. 
	 *
	 * @param openspcoopstate Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	public GestoreMessaggi(IOpenSPCoopState openspcoopstate, boolean isRichiesta,Logger alog,MsgDiagnostico msgDiag,PdDContext pddContext){
		this(openspcoopstate, isRichiesta,null,null,alog,msgDiag,pddContext);
	}

	/**
	 * Costruttore. 
	 * @param openspcoopstate Oggetto che rappresenta lo stato di una busta 
	 * @param idBusta Identificativo del Messaggio
	 * @param tipo Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX
	 * 
	 */
	public GestoreMessaggi(IOpenSPCoopState openspcoopstate, boolean isRichiesta,String idBusta, String tipo,MsgDiagnostico msgDiag,PdDContext pddContext){
		this(openspcoopstate, isRichiesta, idBusta,tipo,Configurazione.getLibraryLog(),msgDiag,pddContext);
	}
	/**
	 * Costruttore. 
	 *
	 * @param openspcoopstate Oggetto che rappresenta lo stato di una busta
	 * @param idBusta Identificativo del Messaggio
	 * @param tipo Indicazione se il messaggio sara' salvato nella INBOX dei messaggi, o nella OUTBOX
	 * 
	 */
	public GestoreMessaggi(IOpenSPCoopState openspcoopstate, boolean isRichiesta, String idBusta, String tipo,Logger alog,MsgDiagnostico msgDiag,PdDContext pddContext){
		this.openspcoopstate = openspcoopstate;
		this.isRichiesta = isRichiesta;
		//state = (isRichiesta) ? openspcoopstate.getStatoRichiesta() : openspcoopstate.getStatoRisposta();
		// new  Hashtable<String,PreparedStatement>(); in stateful ?? inizializzo ?
		this.tableProprietariMessaggiGestiti = new  Hashtable<String,String>();
		this.tableMessaggiGestiti = new Hashtable<String, Boolean>();
		this.idBusta = idBusta;
		this.tipo = tipo;
		this.propertiesReader = OpenSPCoop2Properties.getInstance();
		this.msgDiag = msgDiag;
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(GestoreMessaggi.class);
		}

		if(this.propertiesReader.isRepositoryOnFS() == false){
			if(GestoreMessaggi.adapterInitialized==false){
				GestoreMessaggi.initAdapter(this.log,this.propertiesReader);
			}
		}else{
			this.workDir = this.propertiesReader.getRepositoryDirectory();
		}

		if(GestoreMessaggi.pddContextSerializerInitialized==false){
			GestoreMessaggi.initPddContextSerializer(this.log,this.propertiesReader);
		}

		GestoreMessaggi.cluster_id = this.propertiesReader.getClusterId(false);

		this.pddContext = pddContext;
		try{
			if(this.pddContext!=null) {
				this.idTransazione = (String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
				this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME));
			}
		}catch(Exception e){
			this.log.error("Inizializzione GestoreMessaggi non riuscita [ProtocoFactoryManager.getInstance]:"+e.getMessage(),e);
		}

	}












	/* ********  U T I L I T Y  ******** */

	/**
	 * Imposta lo stateMessage
	 *
	 * @param openspcoopstate oggetto che rappresenta lo stato della busta
	 * 
	 */
	public void updateOpenSPCoopState(IOpenSPCoopState openspcoopstate){
		this.openspcoopstate = openspcoopstate;
	}




	/**
	 * Restituisce il nome della colonna associata al nodo con identificativo <var>nomeNodo</var>.
	 *
	 * @param idNodo Identificatore del nodo.
	 * 
	 */
	private String getColonnaSQL(String idNodo) throws SQLException{
		if(idNodo.startsWith(RicezioneContenutiApplicativi.ID_MODULO))
			return "MOD_RICEZ_CONT_APPLICATIVI";
		else if(ConsegnaContenutiApplicativi.ID_MODULO.equals(idNodo))
			return "MOD_CONSEGNA_CONT_APPLICATIVI";
		else if(idNodo.startsWith(RicezioneBuste.ID_MODULO))
			return "MOD_RICEZ_BUSTE";
		else if(InoltroBuste.ID_MODULO.equals(idNodo))
			return "MOD_INOLTRO_BUSTE";
		else if(InoltroRisposte.ID_MODULO.equals(idNodo))
			return "MOD_INOLTRO_RISPOSTE";
		else if(Imbustamento.ID_MODULO.equals(idNodo))
			return "MOD_IMBUSTAMENTO";
		else if(ImbustamentoRisposte.ID_MODULO.equals(idNodo))
			return "MOD_IMBUSTAMENTO_RISPOSTE";
		else if(Sbustamento.ID_MODULO.equals(idNodo))
			return "MOD_SBUSTAMENTO";
		else if(SbustamentoRisposte.ID_MODULO.equals(idNodo))
			return "MOD_SBUSTAMENTO_RISPOSTE";
		else
			throw new SQLException("Traduzione IDModulo->ColonnaSQL per modulo ["+idNodo+"] non possibile");
	}





















	/* ********  C R E A Z I O N E  ******** */
	/**
	 * Crea lo stato di un messaggio gestito da OpenSPCoop, salvandolo anche su fileSystem/DB. 
	 *
	 * @param message Messaggio.
	 * 
	 */
	public void registraMessaggio(OpenSPCoop2Message message,String correlazioneApplicativaRichiesta) throws GestoreMessaggiException{
		registraMessaggio(message,null, false,correlazioneApplicativaRichiesta,null);
	}
	public void registraMessaggio(OpenSPCoop2Message message,String correlazioneApplicativaRichiesta,String correlazioneApplicativaRisposta) throws GestoreMessaggiException{
		registraMessaggio(message,null, false,correlazioneApplicativaRichiesta,correlazioneApplicativaRisposta);
	}

	public void registraMessaggio(OpenSPCoop2Message message,Date oraRegistrazione,String correlazioneApplicativaRichiesta) throws GestoreMessaggiException{
		registraMessaggio(message,oraRegistrazione, false,correlazioneApplicativaRichiesta,null);
	}
	public void registraMessaggio(OpenSPCoop2Message message,Date oraRegistrazione,String correlazioneApplicativaRichiesta,String correlazioneApplicativaRisposta) throws GestoreMessaggiException{
		registraMessaggio(message,oraRegistrazione, false,correlazioneApplicativaRichiesta,correlazioneApplicativaRisposta);
	}

	/**
	 * Crea lo stato di un messaggio gestito da OpenSPCoop, salvandolo anche su fileSystem/DB. 
	 *
	 * @param message Messaggio.
	 * 
	 */
	public void registraMessaggio(OpenSPCoop2Message message, Date oraRegistrazione, boolean salvaNelloStateless,
			String correlazioneApplicativaRichiesta) throws GestoreMessaggiException {
		registraMessaggio(message,oraRegistrazione,salvaNelloStateless,correlazioneApplicativaRichiesta,null);
	}
	public void registraMessaggio(OpenSPCoop2Message message, Date oraRegistrazione, boolean salvaNelloStateless,
			String correlazioneApplicativaRichiesta,String correlazioneApplicativaRisposta) throws GestoreMessaggiException {

		// OraRegistrazione
		java.sql.Timestamp oraRegistrazioneT = null;
		if (oraRegistrazione == null) {
			oraRegistrazioneT = DateManager.getTimestamp();
		} else {
			oraRegistrazioneT = new java.sql.Timestamp(oraRegistrazione.getTime());
		}

		// Gestione stateless 
		boolean gestisciInfoGenericheDB = true;
		if( this.openspcoopstate instanceof OpenSPCoopStateless ){

			if (this.isRichiesta) 
				((OpenSPCoopStateless)this.openspcoopstate).getTempiAttraversamentoPDD().setRicezioneMsgIngresso(oraRegistrazioneT);
			else 
				((OpenSPCoopStateless)this.openspcoopstate).getTempiAttraversamentoPDD().setRicezioneMsgRisposta(oraRegistrazioneT);

			((OpenSPCoopStateless)this.openspcoopstate).setIDCorrelazioneApplicativa(correlazioneApplicativaRichiesta);
			((OpenSPCoopStateless)this.openspcoopstate).setIDCorrelazioneApplicativaRisposta(correlazioneApplicativaRisposta);
			((OpenSPCoopStateless)this.openspcoopstate).setPddContext(this.pddContext);

			if(!this.isRichiesta){
				((OpenSPCoopStateless)this.openspcoopstate).setRispostaMsg(message);
				return;
			}else{
				if( (this.oneWayVersione11==false) || this.routingStateless){
					gestisciInfoGenericheDB = false;
				}
			}
		}

		// stato
		StateMessage stato = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) : ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;

		// Gestione info DB
		if(gestisciInfoGenericheDB){


			Connection connectionDB = stato.getConnectionDB();  

			/* ----- Registrazione in contesto OpenSPCoop ------- */
			PreparedStatement pstmt = null;
			try {

				// PdDContext
				StringBuilder fieldNamesPdDContext = new StringBuilder();
				StringBuilder fieldValuesPdDContext = new StringBuilder();
				Hashtable<String, String> contextSerializerParameters = null;
				List<Object> objectSerializer = new ArrayList<Object>();
				if(GestoreMessaggi.pddContextSerializer!=null){
					contextSerializerParameters = GestoreMessaggi.pddContextSerializer.getGestoreMessaggiKeywords();
					if(contextSerializerParameters!=null && contextSerializerParameters.size()>0){
						Enumeration<String> keywordContext = contextSerializerParameters.keys();
						while(keywordContext.hasMoreElements()){
							String keyword =  keywordContext.nextElement();

							Object o = this.pddContext.getObject(keyword);
							if(o==null){
								continue; // un oggetto puo' essere opzionale in un context
							}

							fieldNamesPdDContext.append(" , ");
							fieldValuesPdDContext.append(" , ");
							fieldNamesPdDContext.append(contextSerializerParameters.get(keyword));
							fieldValuesPdDContext.append("?");

							if( !(o instanceof String) && !(o instanceof Long) && !(o instanceof Timestamp)){
								throw new Exception("Oggetto con chiave ["+keyword+"] e' di tipo ["+o.getClass().getName()+"], e' possibile serializzare solo i tipi java.lang.String/java.lang.Long/java.sql.Timestamp");
							}
							objectSerializer.add(o);
						}
					}
				}


				StringBuilder query = new StringBuilder();
				query.append("INSERT INTO ");

				query.append(GestoreMessaggi.MESSAGGI);
				query.append("(ID_MESSAGGIO,TIPO,ORA_REGISTRAZIONE,RISPEDIZIONE, REDELIVERY_DELAY, CLUSTER_ID, CORRELAZIONE_APPLICATIVA, CORRELAZIONE_RISPOSTA, PROTOCOLLO, id_transazione"+fieldNamesPdDContext.toString()
						+") VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ?"+fieldValuesPdDContext.toString()+")");
				//this.log.debug("[registraMessaggio] Aggiorno MSG["+this.tipo+"/"+this.idBusta+"] ORA_REGISTRAZIONE["+oraRegistrazioneT.toString()+"] e RISPEDIZIONE["+oraRegistrazioneT.toString()+"]");


				pstmt = connectionDB.prepareStatement(query.toString());
				int index = 1;
				pstmt.setString(index++, this.idBusta);
				pstmt.setString(index++, this.tipo);
				pstmt.setTimestamp(index++, oraRegistrazioneT);
				pstmt.setTimestamp(index++, oraRegistrazioneT);
				pstmt.setTimestamp(index++, oraRegistrazioneT);
				pstmt.setString(index++, GestoreMessaggi.cluster_id);
				pstmt.setString(index++, correlazioneApplicativaRichiesta);
				pstmt.setString(index++, correlazioneApplicativaRisposta);
				pstmt.setString(index++, this.protocolFactory.getProtocol());
				pstmt.setString(index++, this.idTransazione);
				for (int i = 0; i < objectSerializer.size(); i++) {
					Object o = objectSerializer.get(i);
					if(o instanceof String){
						pstmt.setString(index++, o.toString());
					} else if(o instanceof Long){
						pstmt.setLong(index++, (Long)o);
					} else if(o instanceof Timestamp){
						pstmt.setTimestamp(index++, (Timestamp)o);
					}
				}

				// Add PreparedStatement: 
				stato.getPreparedStatement().put("INSERT (MSG_OP_STEP1) registraMessaggioOpenSPCoop " + this.tipo + "/" + this.idBusta, pstmt);

				// Add Messaggio into table
				this.addMessaggioIntoTable(this.tipo, this.idBusta);

			} catch (Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore di registrazione " + this.tipo + "/" + this.idBusta + ": " + e.getMessage();
				this.log.error(errorMsg, e);
				try {
					if (pstmt != null) {
						pstmt.close();
					}
				} catch (Exception er) {
				}
				throw new GestoreMessaggiException(errorMsg, e);
			}
		}

		//  Salvo contenuto messaggio 
		if (message != null) {

			SavedMessage msgSoap = null;
			try {
				msgSoap = new SavedMessage(this.idBusta, this.openspcoopstate, this.tipo, this.workDir, GestoreMessaggi.adapter, this.log);
				boolean consume = true;
				msgSoap.save(message, this.isRichiesta, salvaNelloStateless, consume, oraRegistrazioneT);
			} catch (Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore di registrazione (SoapMessage) " + this.tipo + "/" + this.idBusta + ": " + e.getMessage();
				if (msgSoap != null) {
					stato.closePreparedStatement(); // Chiude le PreparedStatement aperte(e non eseguite) per il salvataggio del Msg
				}
				this.log.error(errorMsg, e);
				throw new GestoreMessaggiException(errorMsg, e);
			}
		}
	}

	public java.sql.Timestamp registraInformazioniMessaggio_statelessEngine(Date oraRegistrazione,String proprietarioMessaggio,
			String correlazioneApplicativaRichiesta) throws GestoreMessaggiException {
		return this.registraInformazioniMessaggio_statelessEngine(oraRegistrazione, proprietarioMessaggio, null,correlazioneApplicativaRichiesta,null);
	}
	public java.sql.Timestamp registraInformazioniMessaggio_statelessEngine(Date oraRegistrazione,String proprietarioMessaggio,String riferimentoMessaggio,
			String correlazioneApplicativaRichiesta,String correlazioneApplicativaRisposta) throws GestoreMessaggiException {

		// OraRegistrazione
		java.sql.Timestamp oraRegistrazioneT = null;
		if (oraRegistrazione == null) {
			oraRegistrazioneT = DateManager.getTimestamp();
		} else {
			oraRegistrazioneT = new java.sql.Timestamp(oraRegistrazione.getTime());
		}

		// stato
		StateMessage stato = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) : ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;

		// Gestione info DB
		Connection connectionDB = stato.getConnectionDB();  

		/* ----- Registrazione in contesto OpenSPCoop ------- */
		PreparedStatement pstmt = null;
		try {

			// PdDContext
			StringBuilder fieldNamesPdDContext = new StringBuilder();
			StringBuilder fieldValuesPdDContext = new StringBuilder();
			Hashtable<String, String> contextSerializerParameters = null;
			List<Object> objectSerializer = new ArrayList<Object>();
			if(GestoreMessaggi.pddContextSerializer!=null){
				contextSerializerParameters = GestoreMessaggi.pddContextSerializer.getGestoreMessaggiKeywords();
				if(contextSerializerParameters!=null && contextSerializerParameters.size()>0){
					Enumeration<String> keywordContext = contextSerializerParameters.keys();
					while(keywordContext.hasMoreElements()){
						String keyword =  keywordContext.nextElement();

						Object o = this.pddContext.getObject(keyword);
						if(o==null){
							continue; // un oggetto puo' essere opzionale in un context
						}

						fieldNamesPdDContext.append(" , ");
						fieldValuesPdDContext.append(" , ");
						fieldNamesPdDContext.append(contextSerializerParameters.get(keyword));
						fieldValuesPdDContext.append("?");

						if( !(o instanceof String) && !(o instanceof Long) && !(o instanceof Timestamp)){
							throw new Exception("Oggetto con chiave ["+keyword+"] e' di tipo ["+o.getClass().getName()+"], e' possibile serializzare solo i tipi java.lang.String/java.lang.Long/java.sql.Timestamp");
						}
						objectSerializer.add(o);
					}
				}
			}


			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO ");

			query.append(GestoreMessaggi.MESSAGGI);
			query.append("(ID_MESSAGGIO,TIPO,ORA_REGISTRAZIONE,RISPEDIZIONE, REDELIVERY_DELAY, CLUSTER_ID, PROPRIETARIO, CORRELAZIONE_APPLICATIVA, CORRELAZIONE_RISPOSTA, PROTOCOLLO, id_transazione ");
			if(riferimentoMessaggio!=null)
				query.append(", RIFERIMENTO_MSG");
			query.append(fieldNamesPdDContext.toString());
			query.append(") VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? ");
			if(riferimentoMessaggio!=null)
				query.append(", ?");
			query.append(fieldValuesPdDContext);
			query.append(" )");

			//this.log.debug("[registraMessaggio] Aggiorno MSG["+this.tipo+"/"+this.idBusta+"] ORA_REGISTRAZIONE["+oraRegistrazioneT.toString()+"] e RISPEDIZIONE["+oraRegistrazioneT.toString()+"]");


			pstmt = connectionDB.prepareStatement(query.toString());
			int index = 1;
			pstmt.setString(index++, this.idBusta);
			pstmt.setString(index++, this.tipo);
			pstmt.setTimestamp(index++, oraRegistrazioneT);
			pstmt.setTimestamp(index++, oraRegistrazioneT);
			pstmt.setTimestamp(index++, oraRegistrazioneT);
			pstmt.setString(index++, GestoreMessaggi.cluster_id);
			pstmt.setString(index++, proprietarioMessaggio);
			pstmt.setString(index++, correlazioneApplicativaRichiesta);
			pstmt.setString(index++, correlazioneApplicativaRisposta);
			pstmt.setString(index++, this.protocolFactory.getProtocol());
			pstmt.setString(index++, this.idTransazione);
			if(riferimentoMessaggio!=null){
				pstmt.setString(index++,riferimentoMessaggio);
			}
			for (int i = 0; i < objectSerializer.size(); i++) {
				Object o = objectSerializer.get(i);
				if(o instanceof String){
					pstmt.setString(index++, o.toString());
				} else if(o instanceof Long){
					pstmt.setLong(index++, (Long)o);
				} else if(o instanceof Timestamp){
					pstmt.setTimestamp(index++, (Timestamp)o);
				}
			}

			// Add PreparedStatement: 
			stato.getPreparedStatement().put("INSERT (MSG_OP_STEP1) registraMessaggioOpenSPCoop(stateless engine) " + this.tipo + "/" + this.idBusta, pstmt);

			// Add Messaggio into table
			this.addMessaggioIntoTable(this.tipo, this.idBusta);

		} catch (Exception e) {
			String errorMsg = "GESTORE_MESSAGGI, Errore di registrazione (stateless engine) " + this.tipo + "/" + this.idBusta + ": " + e.getMessage();
			this.log.error(errorMsg, e);
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception er) {
			}
			throw new GestoreMessaggiException(errorMsg, e);
		}

		return oraRegistrazioneT;
	}

	public void registraMessaggio_statelessEngine(OpenSPCoop2Message message, boolean consumeMessage, java.sql.Timestamp oraRegistrazioneT) throws GestoreMessaggiException {
		// Salvo contenuto messaggio 
		if (message != null) {

			// stato
			StateMessage stato = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) : ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;


			SavedMessage msgSoap = null;
			try {
				msgSoap = new SavedMessage(this.idBusta, this.openspcoopstate, this.tipo, this.workDir, GestoreMessaggi.adapter, this.log);
				msgSoap.save(message, this.isRichiesta, false, consumeMessage, oraRegistrazioneT);
			} catch (Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore di registrazione (SoapMessage) " + this.tipo + "/" + this.idBusta + ": " + e.getMessage();
				if (msgSoap != null) {
					stato.closePreparedStatement(); // Chiude le PreparedStatement aperte(e non eseguite) per il salvataggio del Msg
				}
				this.log.error(errorMsg, e);
				throw new GestoreMessaggiException(errorMsg, e);
			}
		}
	}

	/**
	 * Registra un destinatario (servizio applicativo) il quale dovra' ricevere un messaggio gestito da OpenSPCoop. 
	 *
	 * @param serv Servizio Applicativo da registrare come destinatario del messaggio
	 * @param sbustamentoSOAP Indicazione se al servizio applicativo deve essere ritornato un messaggio sbustato
	 * @param integrationManager Indiciazione se il servizio applicativo e' autorizzato a ricevere il contenuto anche attraverso il servizio integrationManager
	 * @param tipoConsegna Assume il valore 'Connettore' se la consegna avviente tramite un connettore, 'ConnectionReply' se viene ritornato tramite connectionReply, 'IntegrationManager' se e' solo ottenibile tramite IntegrationManager
	 * 
	 */
	public void registraDestinatarioMessaggio(String serv, 
			boolean sbustamentoSOAP,boolean sbustamentoInfoProtocol,
			boolean integrationManager,String tipoConsegna,Timestamp oraRegistrazioneMessaggio,
			String nomePorta, boolean attesaEsito, boolean impostaPerConsegnaTimerContenutiApplicativi,
			String coda, String priorita)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful || this.oneWayVersione11) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateMSG.getConnectionDB();
			PreparedStatement pstmt = null;
			try{	

				// Costruzione Query
				StringBuilder query = new StringBuilder();
				query.append("INSERT INTO ");
				query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
				String columnCoda = "";
				String valueCoda = "";
				String columnPriorita = "";
				String valuePriorita = "";
				String columnErroreProcessamento = "";
				String valueErroreProcessamento = "";
				if(coda!=null) {
					columnCoda=",CODA";
					valueCoda=", ?";
				}
				if(priorita!=null) {
					columnPriorita=",PRIORITA";
					valuePriorita=", ?";
				}
				if(impostaPerConsegnaTimerContenutiApplicativi) {
					columnErroreProcessamento = ",ERRORE_PROCESSAMENTO,ERRORE_PROCESSAMENTO_COMPACT";
					valueErroreProcessamento = ", ? , ?";
				}
				query.append("(ID_MESSAGGIO,SERVIZIO_APPLICATIVO,SBUSTAMENTO_SOAP,SBUSTAMENTO_INFO_PROTOCOL,INTEGRATION_MANAGER,TIPO_CONSEGNA,ORA_REGISTRAZIONE,RISPEDIZIONE,NOME_PORTA,ATTESA_ESITO"+
							columnCoda+columnPriorita+columnErroreProcessamento+
						") VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ?"+
							valueCoda+valuePriorita+valueErroreProcessamento+")");

				pstmt = connectionDB.prepareStatement(query.toString());
				int index = 1;
				pstmt.setString(index++,this.idBusta);
				pstmt.setString(index++,serv);
				if(sbustamentoSOAP)
					pstmt.setInt(index++,CostantiDB.TRUE);
				else
					pstmt.setInt(index++,CostantiDB.FALSE);
				if(sbustamentoInfoProtocol)
					pstmt.setInt(index++,CostantiDB.TRUE);
				else
					pstmt.setInt(index++,CostantiDB.FALSE);
				if(integrationManager)
					pstmt.setInt(index++,CostantiDB.TRUE);
				else
					pstmt.setInt(index++,CostantiDB.FALSE);
				pstmt.setString(index++,tipoConsegna);

				pstmt.setTimestamp(index++,oraRegistrazioneMessaggio);

				pstmt.setTimestamp(index++,oraRegistrazioneMessaggio); // rispedizione, verra aggiornata
				
				pstmt.setString(index++,nomePorta);
				
				if(attesaEsito)
					pstmt.setInt(index++,CostantiDB.TRUE);
				else
					pstmt.setInt(index++,CostantiDB.FALSE);
				
				if(coda!=null) {
					pstmt.setString(index++, coda);
				}
				if(priorita!=null) {
					pstmt.setString(index++, priorita);
				}
				
				if(impostaPerConsegnaTimerContenutiApplicativi) {
					pstmt.setString(index++, TimerConsegnaContenutiApplicativiThread.ID_MODULO);
					pstmt.setString(index++, TimerConsegnaContenutiApplicativiThread.ID_MODULO);
				}

				//this.log.debug("[registraDestinatarioMessaggio] Aggiorno MSG["+this.tipo+"/"+this.idBusta+"] RISPEDIZIONE["+oraRegistrazioneMessaggio.toString()+"]");

				// Add PreparedStatement 
				stateMSG.getPreparedStatement().put("INSERT (MSG_OP_STEP2) registraDestinatarioMessaggioOpenSPCoop["+serv+"]["+this.idBusta+"]",pstmt);

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore di registrazione destinatario Messaggio "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless && !this.oneWayVersione11){
			//NOP
		}else{
			throw new GestoreMessaggiException("Metodo invocato con IState non valido"); //CAMBIARE FIRMA PER TRATTARE ECCEZIONE?
		}
	}








	/* ********  A G G I O R N A M E N T I  ******** */



	/**
	 * Aggiorna lo stato di un messaggio gestito da OpenSPCoop, configurando il nodo dell'infrastruttura autorizzato a processarlo. 
	 *
	 * @param nodoOpenSPCoop  Nodo OpenSPCoop autorizzato a processare il messaggio
	 * 
	 */
	public void aggiornaProprietarioMessaggio(String nodoOpenSPCoop)throws GestoreMessaggiException{
		this.aggiornaProprietarioMessaggio_engine(nodoOpenSPCoop, false, false);
	}

	/**
	 * Aggiorna lo stato di un messaggio gestito da OpenSPCoop, configurando il nodo dell'infrastruttura autorizzato a processarlo. 
	 *
	 * @param nodoOpenSPCoop  Nodo OpenSPCoop autorizzato a processare il messaggio
	 * 
	 */
	public void aggiornaProprietarioMessaggio(String nodoOpenSPCoop,boolean searchForRiferimentoMsg)throws GestoreMessaggiException{
		this.aggiornaProprietarioMessaggio_engine(nodoOpenSPCoop, searchForRiferimentoMsg,false);
	}


	/**
	 * Aggiorna lo stato di un messaggio gestito da OpenSPCoop, configurando il nodo dell'infrastruttura autorizzato a processarlo. 
	 *
	 * @param nodoOpenSPCoop  Nodo OpenSPCoop autorizzato a processare il messaggio
	 * 
	 */
	private boolean deleted = false;
	private void aggiornaProprietarioMessaggio_engine(String nodoOpenSPCoop,boolean searchForRiferimentoMsg,boolean executePreparedStatement)throws GestoreMessaggiException{

		if(this.deleted){
			return; // gia' marcato per eliminazione
		}

		boolean stateful = this.openspcoopstate instanceof OpenSPCoopStateful;

		if(!stateful){
			if (this.isRichiesta) ((OpenSPCoopStateless)this.openspcoopstate).setDestinatarioRequestMsgLib(nodoOpenSPCoop);
			else ((OpenSPCoopStateless)this.openspcoopstate).setDestinatarioResponseMsgLib(nodoOpenSPCoop);
		}

		if( stateful || this.readyForDrop || this.oneWayVersione11) {
			StateMessage stateMsg = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateMsg.getConnectionDB();

			PreparedStatement pstmt = null;
			try{	

				// Costruzione Query
				StringBuilder query = new StringBuilder();
				query.append("UPDATE ");
				query.append(GestoreMessaggi.MESSAGGI);
				query.append(" SET PROPRIETARIO=? WHERE ");
				// Se devo aggiornare per riferimento, devo invertire il tipo del messaggio, e devo cercare i messaggi che possiedono l'id come riferimento
				String tipoMessaggio = null;
				if(searchForRiferimentoMsg){
					if(Costanti.OUTBOX.equals(this.tipo)){
						tipoMessaggio = Costanti.INBOX;
					}else{
						tipoMessaggio = Costanti.OUTBOX;
					}
					query.append(" RIFERIMENTO_MSG=? AND TIPO=? ");
				}else{
					tipoMessaggio = this.tipo;
					query.append(" ID_MESSAGGIO=? AND TIPO=? ");
				}
				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,nodoOpenSPCoop);
				pstmt.setString(2,this.idBusta);
				pstmt.setString(3,tipoMessaggio);

				// Add PreparedStatement
				if(executePreparedStatement){
					pstmt.executeUpdate();
					pstmt.close();
				}
				else{
					//System.out.println("UPDATE ["+query.toString()+"] 1["+nodoOpenSPCoop+"] 2["+this.idBusta+"] 3["+tipoMessaggio+"]");
					stateMsg.getPreparedStatement().put("UPDATE aggiornaProprietarioMessaggioOpenSPCoop "+this.tipo+"/"+this.idBusta,pstmt);
				}

				// Add Proprietario into table
				this.addProprietarioIntoTable(this.tipo, this.idBusta, nodoOpenSPCoop);

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore di aggiornamento proprietario Messaggio "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
		}

		if(TimerGestoreMessaggi.ID_MODULO.equals(nodoOpenSPCoop)){
			this.deleted = true;
		}
	}

	/**
	 * Aggiorna lo stato di un messaggio gestito da OpenSPCoop, configurando l'ID del campo riferimento messaggio di una busta gestita
	 *
	 * @param riferimentoMessaggio Riferimento Messaggio di una busta gestita
	 * 
	 */
	public void aggiornaRiferimentoMessaggio(String riferimentoMessaggio)throws GestoreMessaggiException{
		this.aggiornaRiferimentoMessaggio(riferimentoMessaggio, false);
	}
	public void aggiornaRiferimentoMessaggio(String riferimentoMessaggio,boolean saveIntoDB)throws GestoreMessaggiException{

		if((this.openspcoopstate instanceof OpenSPCoopStateful) || saveIntoDB) {
			StateMessage state = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = state.getConnectionDB();

			PreparedStatement pstmt = null;
			try{	

				// Costruzione Query
				StringBuilder query = new StringBuilder();
				query.append("UPDATE ");
				query.append(GestoreMessaggi.MESSAGGI);
				query.append(" SET RIFERIMENTO_MSG=? WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,riferimentoMessaggio);
				pstmt.setString(2,this.idBusta);
				pstmt.setString(3,this.tipo);

				// Add PreparedStatement
				state.getPreparedStatement().put("UPDATE aggiornaRiferimentoMessaggioOpenSPCoop "+this.tipo+"/"+this.idBusta,pstmt);

				// add in cache
				if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){
					try{
						this.msgDiag.highDebug("Aggiornamento riferimentoMessaggio per ["+this.tipo+"/"+riferimentoMessaggio+"] a ["+this.idBusta+"] ...");
						GestoreMessaggi.addIntoCache_MappingRifMsgToId(this.msgDiag,this.tipo,riferimentoMessaggio,this.idBusta);
						this.msgDiag.highDebug("Aggiornamento riferimentoMessaggio per ["+this.tipo+"/"+riferimentoMessaggio+"] a ["+this.idBusta+"] effettuata.");
					}catch(Exception e){
						this.msgDiag.logErroreGenerico(e,"GestoreMessaggi.addIntoCache_MappingRifMsgToId(Per:"+this.tipo+"/"+riferimentoMessaggio+",Value:"+this.idBusta+"");
					}
				}

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore di aggiornamento riferimento messaggio "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			// NOP
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido"); //cambiare la firma ?
		}
	}


	/**
	 * Aggiorna lo stato di un messaggio gestito da OpenSPCoop, configurando il motivo di un eventuale errore verificatosi
	 * durante la gestione del messaggio stesso. 
	 *
	 * @param motivoErrore Motivo dell'errore
	 * 
	 */
	public void aggiornaErroreProcessamentoMessaggio(String motivoErrore,String servizioApplicativo) throws GestoreMessaggiException{

		if( (this.openspcoopstate instanceof OpenSPCoopStateful) || this.oneWayVersione11) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();

			String motivoErroreGiaRegistrato = this.getErroreProcessamentoMessaggio(servizioApplicativo);
			String prefix = "";
			if(motivoErroreGiaRegistrato!=null){
				if( motivoErroreGiaRegistrato.startsWith(GestoreMessaggi.NUMERO_RISPEDIZIONE)){
					int index =  motivoErroreGiaRegistrato.indexOf("]");
					prefix = motivoErroreGiaRegistrato.substring(0,(index+2));
					motivoErroreGiaRegistrato = motivoErroreGiaRegistrato.substring(index+2);
				}
			}

			// aggiorno il motivo dell'errore solo se ho un errore diverso da quanto e' capitato precedentemente
			// o se non avevo ancora registrato un errore.
			if(motivoErroreGiaRegistrato==null || 
					(motivoErroreGiaRegistrato.equals(motivoErrore)==false)){

				PreparedStatement pstmt = null;
				try{	

					// Costruzione Query
					StringBuilder query = new StringBuilder();
					query.append("UPDATE ");
					if(servizioApplicativo==null) {
						query.append(GestoreMessaggi.MESSAGGI);
						query.append(" SET ERRORE_PROCESSAMENTO=? WHERE ID_MESSAGGIO = ?");
					}else {
						query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
						query.append(" SET ERRORE_PROCESSAMENTO=?, ERRORE_PROCESSAMENTO_COMPACT=? WHERE ID_MESSAGGIO = ?");
					}
					if(servizioApplicativo!=null)
						query.append(" AND SERVIZIO_APPLICATIVO=?");
					else
						query.append(" AND TIPO=?");
					pstmt = connectionDB.prepareStatement(query.toString());
					int index = 1;
					this.log.debug("UPDATE ERRORE PROCESSAMENTO TIPO-1 ["+prefix+"]["+motivoErrore+"] tipo["+this.tipo+"] sa["+servizioApplicativo+"]");
					pstmt.setString(index++,prefix+motivoErrore);
					if(servizioApplicativo!=null) {
						if(motivoErrore!=null && motivoErrore.length()>=200) { // per evitare anche caratteri strani che occupano maggiore spazio
							pstmt.setString(index++,motivoErrore.substring(0, 200)+" ...");
						}
						else {
							pstmt.setString(index++,motivoErrore);
						}
					}
					pstmt.setString(index++,this.idBusta);
					if(servizioApplicativo!=null)
						pstmt.setString(index++,servizioApplicativo);
					else
						pstmt.setString(index++,this.tipo);

					pstmt.execute();
					pstmt.close();

				} catch(Exception e) {
					String errorMsg = "GESTORE_MESSAGGI, Errore di aggiornamento errore processamento messaggio "+this.tipo+"/"+this.idBusta+" sa["+servizioApplicativo+"]: "+e.getMessage();		
					try{
						if(pstmt != null)
							pstmt.close();
					} catch(Exception er) {}
					this.log.error(errorMsg,e);
					throw new GestoreMessaggiException(errorMsg,e);
				}
			}
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//NOP
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	public void aggiungiMessaggioSerializzato(IJDBCAdapter adapter,byte [] msgByte) throws GestoreMessaggiException {

		StateMessage stateMsg = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
				: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
		Connection connectionDB = stateMsg.getConnectionDB();


		PreparedStatement pstmt = null;
		try{	

			// Costruzione Query
			StringBuilder query = new StringBuilder();
			query.append("UPDATE ");
			query.append(GestoreMessaggi.MESSAGGI);
			query.append(" SET msg_bytes=? WHERE id_messaggio= ? and tipo= ?");



			pstmt = connectionDB.prepareStatement(query.toString());
			adapter.setBinaryData(pstmt,1,msgByte);
			pstmt.setString(2,this.idBusta);
			pstmt.setString(3, this.tipo);


			// Add PreparedStatement
			stateMsg.getPreparedStatement().put("UPDATE aggiungiMessaggioSerializzatoInMessaggioOpenSPCoop "+this.tipo+"/"+this.idBusta,pstmt);
		}catch (Exception e){		
			String errorMsg = "GESTORE_MESSAGGI, Errore di aggiornamento errore processamento messaggio "+this.tipo+"/"+this.idBusta+" "+e.getMessage();		
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) { }
			this.log.error(errorMsg,e);
			throw new GestoreMessaggiException(errorMsg,e);
		}

	}

	public void ripristinaMessaggio() throws GestoreMessaggiException {

		StateMessage stateMsg = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
				: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
		Connection connectionDB = stateMsg.getConnectionDB();
		PreparedStatement pstmt = null;

		String sql = "UPDATE MESSAGGI SET scheduling=0 where id_messaggio=? AND tipo=?";

		try {
			pstmt = connectionDB.prepareStatement(sql);
			pstmt.setString(1,this.idBusta);
			pstmt.setString(2,this.tipo);
			pstmt.execute();
		}catch (Exception e){		
			String errorMsg = "GESTORE_MESSAGGI, Errore di aggiornamento errore processamento messaggio "+this.tipo+"/"+this.idBusta+" "+e.getMessage();		
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) { }
			this.log.error(errorMsg,e);
			throw new GestoreMessaggiException(errorMsg,e);
		}
	}









	public static final String NUMERO_RISPEDIZIONE = "[spedizione n.";

	/**
	 * Aggiorna la data di rispedizione di un messaggio gestito da OpenSPCoop. 
	 *
	 * @param dataRispedizione Data di rispedizione
	 * 
	 */
	public void aggiornaDataRispedizione(java.sql.Timestamp dataRispedizione,String servizioApplicativo)throws GestoreMessaggiException{

		if( (this.openspcoopstate instanceof OpenSPCoopStateful) || this.oneWayVersione11) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();

			String motivoErroreGiaRegistrato = this.getErroreProcessamentoMessaggio(servizioApplicativo);

			PreparedStatement pstmt = null;
			try{	
				boolean updateErroreProcessamento = true;
				
				int numeroSpedizione = 0;
				// Costruzione numero rispedizione
				if(motivoErroreGiaRegistrato==null){
					motivoErroreGiaRegistrato = GestoreMessaggi.NUMERO_RISPEDIZIONE + "1] ";
					numeroSpedizione = 1;
				}else{
					if( motivoErroreGiaRegistrato.startsWith(GestoreMessaggi.NUMERO_RISPEDIZIONE) == false ){
						motivoErroreGiaRegistrato = GestoreMessaggi.NUMERO_RISPEDIZIONE + "1] "+motivoErroreGiaRegistrato;
						numeroSpedizione = 1 ;
					}else{
						
						if(this.pddContext!=null && this.pddContext.containsKey(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_NUMERO_TENTATIVI_RICONSEGNA_UPDATED)) {
							updateErroreProcessamento = false;
							numeroSpedizione = (Integer) this.pddContext.getObject(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_NUMERO_TENTATIVI_RICONSEGNA_UPDATED);
						}
						if(updateErroreProcessamento) {
							int indexNumber = motivoErroreGiaRegistrato.indexOf("]");
							if(indexNumber == -1)
								throw new GestoreMessaggiException("Errore processamento corrotto (] non presente)");
							try{
								StringBuilder bf = new StringBuilder();
								int j = indexNumber - 1;
								while( motivoErroreGiaRegistrato.charAt(j)!='.' ){
									bf.append(motivoErroreGiaRegistrato.charAt(j));
									j = j - 1;
								}
								bf.reverse();
								numeroSpedizione = Integer.parseInt(bf.toString());
								numeroSpedizione = numeroSpedizione+1;
								String oldMotivo = motivoErroreGiaRegistrato.substring(indexNumber+2);
								motivoErroreGiaRegistrato = GestoreMessaggi.NUMERO_RISPEDIZIONE + numeroSpedizione + "] "+oldMotivo;
							}catch(Exception e){
								throw new GestoreMessaggiException("Aggiornamento numero spedizione non riuscito: "+e.getMessage(),e);
							}
						}
					}
				}

				// incremento dataSpedizione
				long spedizione = dataRispedizione.getTime();
				if(this.propertiesReader.isRitardoConsegnaAbilitato() && numeroSpedizione>1){
					long ritardoEsponenziale = this.propertiesReader.getRitardoConsegnaEsponenziale();
					long ritardo = 1;
					boolean moltiplicazione = this.propertiesReader.isRitardoConsegnaEsponenzialeConMoltiplicazione();
					if(moltiplicazione==false)
						ritardo = 0;
					long limite = (numeroSpedizione-1);
					if(limite > this.propertiesReader.getRitardoConsegnaEsponenzialeLimite())
						limite = this.propertiesReader.getRitardoConsegnaEsponenzialeLimite();
					for(int i=0; i<limite; i++){
						if(moltiplicazione){
							ritardo = ritardo * ritardoEsponenziale;
						}
						else{
							ritardo = ritardo + ritardoEsponenziale;
						}
					}
					spedizione = spedizione + (ritardo*1000);
				}


				// Costruzione Query
				StringBuilder query = new StringBuilder();
				query.append("UPDATE ");
				if(servizioApplicativo!=null)
					query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
				else
					query.append(GestoreMessaggi.MESSAGGI);
				query.append(" SET RISPEDIZIONE=?");
				if(updateErroreProcessamento) {
					query.append(", ERRORE_PROCESSAMENTO=?");
				}
				query.append(" WHERE  ID_MESSAGGIO = ?"); // non devo aggiornare ERRORE_PROCESSAMENTO_COMPACT poichè li non vi è l'informazione sulla rispedizione.
				if(servizioApplicativo!=null)
					query.append(" AND SERVIZIO_APPLICATIVO=?");
				else
					query.append(" AND TIPO=?");
				pstmt = connectionDB.prepareStatement(query.toString());
				Timestamp t = new Timestamp(spedizione);
				int index = 1;
				pstmt.setTimestamp(index++,t);
				if(updateErroreProcessamento) {
					this.log.debug("UPDATE ERRORE PROCESSAMENTO TIPO-2 ["+motivoErroreGiaRegistrato+"] tipo["+this.tipo+"] sa["+servizioApplicativo+"]");
					pstmt.setString(index++,motivoErroreGiaRegistrato);
				}
				pstmt.setString(index++,this.idBusta);
				if(servizioApplicativo!=null)
					pstmt.setString(index++,servizioApplicativo);
				else
					pstmt.setString(index++,this.tipo);

				/*
			if(servizioApplicativo!=null)
				this.log.debug("[aggiornaDataRispedizione SA("+servizioApplicativo+")] Aggiorno MSG["+this.tipo+"/"+this.idBusta+"] RISPEDIZIONE["+t.toString()+"]");
			else
				this.log.debug("[aggiornaDataRispedizione] Aggiorno MSG["+this.tipo+"/"+this.idBusta+"] RISPEDIZIONE["+t.toString()+"]");
				 */

				pstmt.execute();
				pstmt.close();
				
				if(updateErroreProcessamento) {
					if(this.pddContext!=null) {
						this.pddContext.addObject(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_NUMERO_TENTATIVI_RICONSEGNA_UPDATED, numeroSpedizione);
					}
				}

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore di aggiornamento data rispedizione Messaggio "+this.tipo+"/"+this.idBusta+" sa["+servizioApplicativo+"]: "+e.getMessage();		
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//NOP
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Aggiorna l'id JMS di un messaggio ricevuto da un nodo di OpenSPCoop. 
	 *
	 * @param nodoOpenSPCoop  Nodo OpenSPCoop che ha ricevuto il messaggio
	 * @param idHeaderJMS Identificativo JMS del messaggio ricevuto
	 * 
	 */
	public void aggiornaIDHeaderJMS(String nodoOpenSPCoop,String idHeaderJMS) throws GestoreMessaggiException{
		aggiornaIDHeaderJMS(nodoOpenSPCoop,idHeaderJMS,null);
	}
	/**
	 * Aggiorna l'id JMS di un messaggio ricevuto da un nodo di OpenSPCoop. 
	 *
	 * @param nodoOpenSPCoop  Nodo OpenSPCoop che ha ricevuto il messaggio
	 * @param idHeaderJMS Identificativo JMS del messaggio ricevuto
	 * @param servizioApplicativo Servizio Applicativo utilizzato come chiave insieme all'idBusta
	 * 
	 */
	public void aggiornaIDHeaderJMS(String nodoOpenSPCoop,String idHeaderJMS,String servizioApplicativo) throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			try{	

				// Costruzione Query
				String query = null;
				if( ConsegnaContenutiApplicativi.ID_MODULO.equals(nodoOpenSPCoop)  ){
					query = "UPDATE "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" SET "+getColonnaSQL(nodoOpenSPCoop)+
							"=? WHERE  ID_MESSAGGIO = ? AND SERVIZIO_APPLICATIVO=?";
				}else{
					query = "UPDATE "+GestoreMessaggi.MESSAGGI+" SET "+getColonnaSQL(nodoOpenSPCoop)+"=? WHERE  ID_MESSAGGIO = ? AND TIPO=?";
				}
				pstmt = connectionDB.prepareStatement(query);
				pstmt.setString(1,idHeaderJMS);
				pstmt.setString(2,this.idBusta);
				if( ConsegnaContenutiApplicativi.ID_MODULO.equals(nodoOpenSPCoop)  )
					pstmt.setString(3,servizioApplicativo);
				else
					pstmt.setString(3,this.tipo);
				pstmt.execute();
				pstmt.close();

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore di aggiornamento IDHeaderJMS "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//NOP
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}













	/* ********  R E A D    M E S S A G E   (Gestione Transazione Interna di OpenSPCoop) ******** */


	/**
	 * Ritorna il proprietario (nodo OpenSPCoop) di un messaggio gestito da OpenSPCoop. 
	 * La lettura del proprietario viene effettuata in Serializable Level
	 *
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * @return il proprietario di un messaggio
	 * 
	 */
	public String getProprietario_SerializableRead(String idModulo,long attesaAttiva,int checkInterval) throws GestoreMessaggiException{
		return getProprietario(idModulo,true,attesaAttiva,checkInterval,false);
	}
	/**
	 * Ritorna il proprietario (nodo OpenSPCoop) di un messaggio gestito da OpenSPCoop. 
	 * La lettura del proprietario viene effettuata in Serializable Level
	 *
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * @return il proprietario di un messaggio
	 * 
	 */
	public String getProprietario_SerializableRead(String idModulo,long attesaAttiva,int checkInterval,boolean onlyCache) throws GestoreMessaggiException{
		return getProprietario(idModulo,true,attesaAttiva,checkInterval,onlyCache);
	}
	/**
	 * Ritorna il proprietario (nodo OpenSPCoop) di un messaggio gestito da OpenSPCoop. 
	 *
	 * @return il proprietario di un messaggio
	 * 
	 */
	public String getProprietario(String idModulo) throws GestoreMessaggiException{
		return getProprietario(idModulo,false,-1,-1,false);
	}
	/**
	 * Ritorna il proprietario (nodo OpenSPCoop) di un messaggio gestito da OpenSPCoop. 
	 *
	 * @return il proprietario di un messaggio
	 * 
	 */
	public String getProprietario(String idModulo,boolean onlyCache) throws GestoreMessaggiException{
		return getProprietario(idModulo,false,-1,-1,onlyCache);
	}
	/**
	 * Ritorna il proprietario (nodo OpenSPCoop) di un messaggio gestito da OpenSPCoop. 
	 *
	 * @param serializable Indicazione se la ricerca deve avvenire in serializable mode
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * @return il proprietario di un messaggio
	 * 
	 */
	private String getProprietario(String idModulo,boolean serializable,long attesaAttiva,int checkInterval,boolean onlyCache) throws GestoreMessaggiException{

		if ( (this.openspcoopstate instanceof OpenSPCoopStateless) && (this.oneWayVersione11==false) ) { 
			return (this.isRichiesta) ? ((OpenSPCoopStateless)this.openspcoopstate).getDestinatarioRequestMsgLib() 
					: ((OpenSPCoopStateless)this.openspcoopstate).getDestinatarioResponseMsgLib();
		}
		else if ( (this.openspcoopstate instanceof OpenSPCoopStateful) || (this.oneWayVersione11)) {

			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateMSG.getConnectionDB();

			if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){

				try{
					// get from cache
					this.msgDiag.highDebug("getProprietario, read from cache...");
					String proprietario = GestoreMessaggi.getFromCache_Proprietario(idModulo,this.tipo,this.idBusta);
					if(proprietario!=null)
						return proprietario;
					else{
						if(onlyCache){
							return null;
						}
					}
				}catch(Exception e){
					String errorMsg = "GESTORE_MESSAGGI, error getProprietario (Cache) "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
					this.log.error(errorMsg,e);
					throw new GestoreMessaggiException(errorMsg,e);
				}

			}

			this.msgDiag.highDebug("getProprietario, read from DB...");


			if(serializable == false){
				// NON SERIALIZABLE MODE

				PreparedStatement pstmt = null;
				ResultSet rs = null;
				String value = null;
				try{
					String query = "SELECT PROPRIETARIO FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO=? AND TIPO=?";
					//log.debug("Query: "+query);
					pstmt = connectionDB.prepareStatement(query);
					pstmt.setString(1,this.idBusta);
					pstmt.setString(2,this.tipo);
					rs = pstmt.executeQuery();		
					if(rs.next()){
						value = rs.getString("PROPRIETARIO");
					}
					rs.close();
					pstmt.close();
				} catch(Exception e) {
					try{
						if(rs != null)
							rs.close();
					} catch(Exception er) {}
					try{
						if(pstmt != null)
							pstmt.close();
					} catch(Exception er) {}
				}
				return value;

			}else{

				// SERIALIZABLE MODE

				/*
	      Viene realizzato con livello di isolamento SERIALIZABLE, per essere sicuri
	      che esecuzioni parallele non leggano dati inconsistenti.
	      Con il livello SERIALIZABLE, se ritorna una eccezione, deve essere riprovato
	      La sincronizzazione e' necessaria per via del possibile accesso simultaneo del servizio Gop
	      e del servizio che si occupa di eliminare destinatari di messaggi
				 */
				// setAutoCommit e livello Isolamento
				int oldTransactionIsolation = -1;
				try{
					oldTransactionIsolation = connectionDB.getTransactionIsolation();
					connectionDB.setAutoCommit(false);
					JDBCUtilities.setTransactionIsolationSerializable(this.propertiesReader.getDatabaseType(), connectionDB);
				} catch(Exception er) {
					String errorMsg = "GESTORE_MESSAGGI, Errore getProprietario Messaggio(setIsolation) "+this.tipo+"/"+this.idBusta+": "+er.getMessage();		
					this.log.error(errorMsg,er);
					throw new GestoreMessaggiException(errorMsg,er);
				}

				PreparedStatement pstmt = null;
				ResultSet rs = null;
				boolean getProprietarioOK = false;
				String value = null;

				long scadenzaWhile = DateManager.getTimeMillis() + attesaAttiva;

				while(getProprietarioOK==false && DateManager.getTimeMillis() < scadenzaWhile){

					try{	
						// Costruzione Query
						String query = "SELECT PROPRIETARIO FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO=? AND TIPO=? FOR UPDATE";
						//log.debug("Query: "+query);
						pstmt = connectionDB.prepareStatement(query);
						pstmt.setString(1,this.idBusta);
						pstmt.setString(2,this.tipo);
						rs = pstmt.executeQuery();		
						if(rs.next()){
							value = rs.getString("PROPRIETARIO");
						}
						rs.close();
						pstmt.close();

						// Chiusura Transazione
						connectionDB.commit();

						// ID Costruito
						getProprietarioOK = true;

					} catch(Exception e) {
						try{
							if(rs != null)
								rs.close();
						} catch(Exception er) {}
						try{
							if(pstmt != null)
								pstmt.close();
						} catch(Exception er) {}
						try{
							connectionDB.rollback();
						} catch(Exception er) {}
					}

					if(getProprietarioOK == false){
						// Per aiutare ad evitare conflitti
						try{
							Utilities.sleep((new java.util.Random()).nextInt(checkInterval)); // random da 0ms a checkIntervalms
						}catch(Exception eRandom){}
					}
				}
				// Ripristino Transazione
				try{
					connectionDB.setTransactionIsolation(oldTransactionIsolation);
					connectionDB.setAutoCommit(true);
					stateMSG.updateConnection(connectionDB);
				} catch(Exception er) {
					String errorMsg = "GESTORE_MESSAGGI, Errore getProprietario Messaggio(ripristinoIsolation) "+this.tipo+"/"+this.idBusta+": "+er.getMessage();		
					this.log.error(errorMsg,er);
					throw new GestoreMessaggiException(errorMsg,er);

				}
				return value;
			}
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}


	/**
	 * Ritorna l'id JMS di un messaggio ricevuto da un nodo di OpenSPCoop. 
	 *
	 * @param nodoOpenSPCoop  Nodo OpenSPCoop desiderato
	 * 
	 */
	public String getIDJMSRicevuto(String nodoOpenSPCoop) throws GestoreMessaggiException{
		return getIDJMSRicevuto(nodoOpenSPCoop,null);
	}
	/**
	 * Ritorna l'id JMS di un messaggio ricevuto da un nodo di OpenSPCoop. 
	 *
	 * @param nodoOpenSPCoop  Nodo OpenSPCoop desiderato
	 * @param servizioApplicativo Servizio Applicativo utilizzato come chiave insieme all'id
	 * 
	 */
	public String getIDJMSRicevuto(String nodoOpenSPCoop,String servizioApplicativo) throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null; 
			try{	
				// Costruzione Query
				String query = null;
				if( ConsegnaContenutiApplicativi.ID_MODULO.equals(nodoOpenSPCoop)  ){
					query = "SELECT "+getColonnaSQL(nodoOpenSPCoop)+" FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+
							" WHERE ID_MESSAGGIO=? AND SERVIZIO_APPLICATIVO=?";
				}else{
					query = "SELECT "+getColonnaSQL(nodoOpenSPCoop)+" FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO=? AND TIPO=?";
				}
				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(query);
				pstmt.setString(1,this.idBusta);
				if( ConsegnaContenutiApplicativi.ID_MODULO.equals(nodoOpenSPCoop)  )
					pstmt.setString(2,servizioApplicativo);
				else
					pstmt.setString(2,this.tipo);
				rs = pstmt.executeQuery();
				String value = null;
				if(rs.next()){
					value = rs.getString(getColonnaSQL(nodoOpenSPCoop));
				}
				rs.close();
				pstmt.close();

				return value;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore get IDHeaderJMS "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}	
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

















	/* ********  R E A D    M E S S A G E   ******** */
	/**
	 * Ritorna true se il messaggi risulta gia' registrato
	 *
	 * @return true se il  messaggio risulta gia' registrato
	 * @throws GestoreMessaggiException 
	 * 
	 */
	public boolean existsMessage_onlyCache(boolean searchForRiferimentoMsg) throws GestoreMessaggiException{
		return existsMessage_engine(searchForRiferimentoMsg,true,false);
	}

	/**
	 * Ritorna true se il messaggi risulta gia' registrato
	 *
	 * @return true se il  messaggio risulta gia' registrato
	 * @throws GestoreMessaggiException 
	 * 
	 */
	public boolean existsMessage_onlyCache() throws GestoreMessaggiException{
		return existsMessage_engine(false,true,false);
	}

	/**
	 * Ritorna true se il messaggi risulta gia' registrato
	 *
	 * @return true se il  messaggio risulta gia' registrato
	 * @throws GestoreMessaggiException 
	 * 
	 */
	public boolean existsMessage_noCache(boolean searchForRiferimentoMsg) throws GestoreMessaggiException{
		return existsMessage_engine(searchForRiferimentoMsg,false,true);
	}

	/**
	 * Ritorna true se il messaggi risulta gia' registrato
	 *
	 * @return true se il  messaggio risulta gia' registrato
	 * @throws GestoreMessaggiException 
	 * 
	 */
	public boolean existsMessage_noCache() throws GestoreMessaggiException{
		return existsMessage_engine(false,false,true);
	}

	/**
	 * Ritorna true se il messaggi risulta gia' registrato
	 *
	 * @return true se il  messaggio risulta gia' registrato
	 * @throws GestoreMessaggiException 
	 * 
	 */
	public boolean existsMessage(boolean searchForRiferimentoMsg) throws GestoreMessaggiException{
		return existsMessage_engine(searchForRiferimentoMsg,false,false);
	}

	/**
	 * Ritorna true se il messaggi risulta gia' registrato
	 *
	 * @return true se il  messaggio risulta gia' registrato
	 * @throws GestoreMessaggiException 
	 * 
	 */
	public boolean existsMessage() throws GestoreMessaggiException{
		return existsMessage_engine(false,false,false);
	}

	/**
	 * Ritorna true se il messaggi risulta gia' registrato
	 *
	 * @return true se il  messaggio risulta gia' registrato
	 * @throws GestoreMessaggiException 
	 * 
	 */
	private boolean existsMessage_engine(boolean searchForRiferimentoMsg,boolean onlyCache,boolean noCache) throws GestoreMessaggiException{

		if( (this.openspcoopstate instanceof OpenSPCoopStateless) && 
				( (this.oneWayVersione11==false) || (this.routingStateless)  ) ){
			return false; // il messaggio non viene mai salvato
		}



		StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
				: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;

		Connection connectionDB = stateMSG.getConnectionDB();


		// Se devo cercare per riferimentoMessaggio, prima cerco un messaggio registrato, che possiede
		// come riferimento Messaggio l'ID del gestore.
		// In caso di esistenza uso l'ID del messaggio trovato e non quello del gestore
		String idBustaSearch = this.idBusta;
		if(searchForRiferimentoMsg){
			try{
				idBustaSearch = mapRiferimentoIntoIDBusta();
			}catch(Exception e){}
			//System.out.println("Cerco riferimento di id["+this.idBusta+"]: "+idBustaSearch);
			if(idBustaSearch==null){
				return false;
			}
		}

		if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null && (noCache==false) ){

			try{
				// get from cache
				this.msgDiag.highDebug("existsMessage, read from cache ID["+idBustaSearch+"]...");
				Object value = GestoreMessaggi.getFromCache_existsMessage(this.tipo,idBustaSearch);

				if((value!=null) && (((Boolean)value)==true)){
					return true;
				}else{
					if(onlyCache){
						return false;
					}
				}
			}catch(Exception e){
				String errorMsg = "GESTORE_MESSAGGI, error existsMessage (Cache) "+this.tipo+"/"+idBustaSearch+": "+e.getMessage();		
				this.log.error(errorMsg,e);
				return false;
			}

		}

		this.msgDiag.highDebug("existsMessage, read from DB ID["+idBustaSearch+"]...");

		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		try{	
			// Costruzione Query
			String query = "SELECT ID_MESSAGGIO FROM "+GestoreMessaggi.MESSAGGI+
					" WHERE ID_MESSAGGIO=? AND TIPO=?";
			//log.debug("Query: "+query);
			pstmt = connectionDB.prepareStatement(query);
			pstmt.setString(1,idBustaSearch);
			pstmt.setString(2,this.tipo);
			rs = pstmt.executeQuery();
			if(rs.next()){
				rs.close();
				pstmt.close();
				return true;
			}
			rs.close();
			pstmt.close();

			return false;

		} catch(Exception e) {
			String errorMsg = "GESTORE_MESSAGGI, Errore existsMessage "+this.tipo+"/"+idBustaSearch+": "+e.getMessage();		
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {}
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {}
			this.log.error(errorMsg,e);
			return false;
		}
	}


	/**
	 * Ritorna il messaggio gestito da OpenSPCoop. 
	 *
	 * @return il  messaggio
	 * 
	 */
	public OpenSPCoop2Message getMessage() throws GestoreMessaggiException{
		return getMessage(null);
	}
	public OpenSPCoop2Message getMessage(Date oraRegistrazione) throws GestoreMessaggiException{
		return getMessage(false, oraRegistrazione);
	}

	/**
	 * Ritorna il messaggio gestito da OpenSPCoop. 
	 *
	 * @param searchForRiferimentoMsg Se true, il messaggio viene ricercato per Riferimento Messaggio
	 * @return il  messaggio
	 * 
	 */
	public OpenSPCoop2Message getMessage(boolean searchForRiferimentoMsg) throws GestoreMessaggiException{
		return getMessage(searchForRiferimentoMsg, null);
	}
	public OpenSPCoop2Message getMessage(boolean searchForRiferimentoMsg, Date oraRegistrazione) throws GestoreMessaggiException{

		//		if (openspcoopstate instanceof OpenSPCoopStateless) {
		//			if (isRichiesta) return ((OpenSPCoopStateless)openspcoopstate).getRichiestaMsg() ;
		//			else return ((OpenSPCoopStateless)openspcoopstate).getRispostaMsg() ;
		//		}

		OpenSPCoop2Message msg = null;

		// Se devo cercare per riferimentoMessaggio, prima cerco un messaggio registrato, che possiede
		// come riferimento Messaggio l'ID del gestore.
		// In caso di esistenza uso l'ID del messaggio trovato e non quello del gestore
		String idBustaSearch = this.idBusta;
		if(searchForRiferimentoMsg){
			idBustaSearch = mapRiferimentoIntoIDBusta();
		}

		SavedMessage soapMsg = null;
		try{
			soapMsg = new SavedMessage(idBustaSearch, this.openspcoopstate, this.tipo,this.workDir,GestoreMessaggi.adapter,this.log);
			msg = soapMsg.read(this.isRichiesta, (this.portaDiTipoStateless || this.routingStateless), oraRegistrazione);
		}catch(Exception e){
			String errorMsg = "GESTORE_MESSAGGI, getMessage "+this.tipo+"/"+this.idBusta+": "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new GestoreMessaggiException(errorMsg,e);
		}
		return msg;
	}


	/**
	 * Ritorna l'idBusta di un messaggio che possiede come riferimento messaggio l'ID del gestore. 
	 *
	 * @return l'idBusta di un messaggio che possiede come riferimento messaggio l'ID del gestore in caso di esistenza del messaggio
	 *         cercato, null altrimenti
	 * 
	 */
	public String mapRiferimentoIntoIDBusta()throws GestoreMessaggiException{
		return mapRiferimentoIntoIDBusta(false);
	}

	/**
	 * Ritorna l'idBusta di un messaggio che possiede come riferimento messaggio l'ID del gestore. 
	 *
	 * @return l'idBusta di un messaggio che possiede come riferimento messaggio l'ID del gestore in caso di esistenza del messaggio
	 *         cercato, null altrimenti
	 * 
	 */
	public String mapRiferimentoIntoIDBusta(boolean onlyCache)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateful.getConnectionDB();

			if(GestoreMessaggi.cacheMappingGestoreMessaggi!=null){

				try{
					// get from cache
					this.msgDiag.highDebug("mapRiferimentoIntoIDBusta, read from cache ...");
					String id = GestoreMessaggi.getFromCache_idFromRifMsgMapping(this.tipo,this.idBusta);
					if(id!=null)
						return id;
					else{
						if(onlyCache){
							return null;
						}
					}
				}catch(Exception e){
					String errorMsg = "GESTORE_MESSAGGI, error mapRiferimentoIntoIDBusta (Cache) "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
					this.log.error(errorMsg,e);
					throw new GestoreMessaggiException(errorMsg,e);
				}

			}

			this.msgDiag.highDebug("mapRiferimentoIntoIDBusta, read from DB ...");

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{	
				// Costruzione Query
				String query = "SELECT ID_MESSAGGIO FROM "+GestoreMessaggi.MESSAGGI+" WHERE RIFERIMENTO_MSG=? AND TIPO=? AND PROPRIETARIO IS NOT NULL AND PROPRIETARIO != ? ORDER BY ORA_REGISTRAZIONE DESC";
				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(query);
				pstmt.setString(1,this.idBusta);
				pstmt.setString(2,this.tipo);
				pstmt.setString(3,TimerGestoreMessaggi.ID_MODULO);
				rs = pstmt.executeQuery();
				String value = null;
				if(rs.next()){
					value = rs.getString("ID_MESSAGGIO");
				}

				//System.out.println("GET FROM DB RIFERIMENTO RIF["+this.tipo+"_"+this.idBusta+"] ID TROVATO: ["+value+"]");

				rs.close();
				pstmt.close();

				return value;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, error mapRiferimentoIntoIDBusta "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}	
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}

	}

	public String getErroreProcessamentoMessaggio(String servizioApplicativo) throws GestoreMessaggiException{
		if( (this.openspcoopstate instanceof OpenSPCoopStateful) || this.oneWayVersione11) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();
			PreparedStatement pstmtRead = null;
			ResultSet rs = null; 
			String motivoErroreGiaRegistrato = null;
			try{	
				// Costruzione Query
				String query = null;
				if(servizioApplicativo==null)
					query = "SELECT ERRORE_PROCESSAMENTO FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO = ? AND TIPO=?";
				else
					query = "SELECT ERRORE_PROCESSAMENTO FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" WHERE ID_MESSAGGIO = ? AND SERVIZIO_APPLICATIVO=?";

				//log.debug("Query: "+query);
				pstmtRead = connectionDB.prepareStatement(query);
				pstmtRead.setString(1,this.idBusta);
				if(servizioApplicativo!=null)
					pstmtRead.setString(2,servizioApplicativo);
				else
					pstmtRead.setString(2,this.tipo);

				rs = pstmtRead.executeQuery();
				if(rs.next()){
					motivoErroreGiaRegistrato = rs.getString("ERRORE_PROCESSAMENTO");
				}
				rs.close();
				pstmtRead.close();

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore getErroreProcessamentoMessaggio "+this.tipo+"/"+this.idBusta+" sa["+servizioApplicativo+"]: "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtRead != null)
						pstmtRead.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}

			return motivoErroreGiaRegistrato;
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	public Timestamp getOraRegistrazioneMessaggio() throws GestoreMessaggiException{

		Timestamp t = null;

		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmtRead = null;
			ResultSet rs = null; 
			try{	
				// Costruzione Query
				String query = "SELECT ORA_REGISTRAZIONE FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO = ? AND TIPO=?";

				//log.debug("Query: "+query);
				pstmtRead = connectionDB.prepareStatement(query);
				pstmtRead.setString(1,this.idBusta);
				pstmtRead.setString(2,this.tipo);

				rs = pstmtRead.executeQuery();
				if(rs.next()){
					t = rs.getTimestamp("ORA_REGISTRAZIONE");
				}
				rs.close();
				pstmtRead.close();

				if(t==null)
					throw new Exception("Ora registrazione non registrata");
				return t;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore getOraRegistrazioneMessaggio "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtRead != null)
						pstmtRead.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}

		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){

			if (this.isRichiesta) 
				t = ((OpenSPCoopStateless)this.openspcoopstate).getTempiAttraversamentoPDD().getRicezioneMsgIngresso();
			else 
				t = ((OpenSPCoopStateless)this.openspcoopstate).getTempiAttraversamentoPDD().getRicezioneMsgRisposta();

			if (t == null) throw new GestoreMessaggiException("Ora registrazione non registrata");

			return t;

		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	public Timestamp getDataRispedizioneMessaggio() throws GestoreMessaggiException{

		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmtRead = null;
			ResultSet rs = null; 
			try{	
				// Costruzione Query
				String query = "SELECT RISPEDIZIONE FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO = ? AND TIPO=?";

				//log.debug("Query: "+query);
				pstmtRead = connectionDB.prepareStatement(query);
				pstmtRead.setString(1,this.idBusta);
				pstmtRead.setString(2,this.tipo);

				rs = pstmtRead.executeQuery();
				Timestamp t = null;
				if(rs.next()){
					t = rs.getTimestamp("RISPEDIZIONE");
				}
				rs.close();
				pstmtRead.close();

				if(t==null)
					throw new Exception("Ora rispedizione non registrata");
				return t;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore getDataRispedizioneMessaggio "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtRead != null)
						pstmtRead.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
			// do the right thing
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Controlla se il msg deve essere riconsegnato.
	 *
	 * @return true se la data di riconsegna del msg e' scaduta
	 * 
	 */
	public boolean isPrimaConsegna(String servizioApplicativo) throws GestoreMessaggiException{
		if( (this.openspcoopstate instanceof OpenSPCoopStateful) || (this.oneWayVersione11) ) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();
			PreparedStatement pstmtRead = null;
			ResultSet rs = null; 
			try{	

				// Costruzione Query
				String query = null;
				if(servizioApplicativo==null)
					query = "SELECT "+GestoreMessaggi.MESSAGGI+".RISPEDIZIONE,"+GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO = ? AND TIPO=?";
				else
					query = "SELECT "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".RISPEDIZIONE,"+GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE FROM "
							+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+","+GestoreMessaggi.MESSAGGI+" WHERE "
							+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ID_MESSAGGIO="+GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO AND "+
							""+GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO= ? AND "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SERVIZIO_APPLICATIVO=? AND "+GestoreMessaggi.MESSAGGI+".TIPO=?";

				//log.debug("Query: "+query);
				pstmtRead = connectionDB.prepareStatement(query);
				pstmtRead.setString(1,this.idBusta);
				if(servizioApplicativo!=null){
					pstmtRead.setString(2,servizioApplicativo);
					pstmtRead.setString(3, Costanti.INBOX);
				}
				else
					pstmtRead.setString(2,this.tipo);

				rs = pstmtRead.executeQuery();
				Timestamp oraRegistrazione = null;
				Timestamp rispedizione = null;
				if(rs.next()){
					oraRegistrazione = rs.getTimestamp("ORA_REGISTRAZIONE");
					rispedizione = rs.getTimestamp("RISPEDIZIONE");
				}
				rs.close();
				pstmtRead.close();

				//this.log.debug("[isPrimaConsegna] MSG["+this.tipo+"/"+this.ididBusta+"] ORA_REGISTRAZIONE["+oraRegistrazione.toString()+"] RISPEDIZIONE["+rispedizione.toString()+"]");

				if(oraRegistrazione==null)
					this.log.error("OraRegistrazione is null");
				if(rispedizione==null)
					this.log.error("Rispedizione is null");

				return  oraRegistrazione!=null && rispedizione!=null && (oraRegistrazione.compareTo(rispedizione) == 0) ;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore isPrimaConsegna "+this.tipo+"/"+this.idBusta+" sa["+servizioApplicativo+"]: "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtRead != null)
						pstmtRead.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
			// do the right thing
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Controlla se il msg deve essere riconsegnato.
	 *
	 * @return true se la data di riconsegna del msg e' scaduta
	 * 
	 */
	public boolean isRiconsegnaMessaggio(String servizioApplicativo) throws GestoreMessaggiException{

		if( (this.openspcoopstate instanceof OpenSPCoopStateful) || (this.oneWayVersione11) ) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();
			if(this.isPrimaConsegna(servizioApplicativo))
				return true; 

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{	
				// Costruzione Query
				StringBuilder bf = new StringBuilder();
				bf.append("SELECT RISPEDIZIONE FROM ");
				if(servizioApplicativo!=null)
					bf.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
				else
					bf.append(GestoreMessaggi.MESSAGGI);
				bf.append(" WHERE ID_MESSAGGIO=?");
				if(servizioApplicativo!=null)
					bf.append(" AND SERVIZIO_APPLICATIVO=?");
				else
					bf.append(" AND TIPO=?");
				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(bf.toString());
				pstmt.setString(1,this.idBusta);
				if(servizioApplicativo!=null)
					pstmt.setString(2,servizioApplicativo);
				else
					pstmt.setString(2,this.tipo);
				rs = pstmt.executeQuery();
				java.sql.Timestamp riconsegna = null;
				if(rs.next()){
					riconsegna = rs.getTimestamp("RISPEDIZIONE");
				}
				rs.close();
				pstmt.close();

				java.sql.Timestamp now = DateManager.getTimestamp();

				//this.log.debug("[isRiconsegnaMessaggio] MSG["+this.tipo+"/"+this.idBusta+"] NOW["+now.toString()+"] RISPEDIZIONE["+riconsegna.toString()+"]");


				// Eventuale msg Scaduto.
				if(riconsegna==null){
					//return false;
					// Ritorno false, almeno la riconsegna viene riprovata dopo un po'...
					// Il valore della riconsegna era null, poiche' il messaggio non era presente. Al prossimo giro l'algoritmo di TransactionManager
					// Rendera' invalido il messaggio.
					this.log.error("Calcolo riconsegna non riuscito ["+this.tipo+"/"+this.idBusta+"], riconsegna is null? query["+bf.toString()+"] sa["+servizioApplicativo+"]");
					throw new Exception("Calcolo riconsegna non riuscito ["+this.tipo+"/"+this.idBusta+"]");
				}


				if (riconsegna.before(now)) 
					return true;
				else{
					this.log.debug("Riconsegna messaggio prematura ["+this.tipo+"/"+this.idBusta+"] RICONSEGNA["+riconsegna.toString()+"] < NOW["+now.toString()+"] == false");
					return false;
				}

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore isRiconsegnaMessaggio "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}	
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			return true;
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Ritorna il riferimento messaggio associato al messaggio gestito da OpenSPCoop. 
	 *
	 * @return il riferimento messaggio associato al messaggio
	 * 
	 */
	public String getRiferimentoMessaggio() throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{	
				// Costruzione Query
				String query = "SELECT RIFERIMENTO_MSG FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO=? AND TIPO=?";
				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(query);
				pstmt.setString(1,this.idBusta);
				pstmt.setString(2,this.tipo);
				rs = pstmt.executeQuery();
				String value = null;
				if(rs.next()){
					value = rs.getString("RIFERIMENTO_MSG");
				}
				rs.close();
				pstmt.close();

				return value;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore get RIFERIMENTO_MSG "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}	
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
			// do the right thing
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	public String getIDCorrelazioneApplicativa() throws GestoreMessaggiException{

		String id = null;

		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmtRead = null;
			ResultSet rs = null; 
			try{	
				// Costruzione Query
				String query = "SELECT CORRELAZIONE_APPLICATIVA FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO = ? AND TIPO=?";

				//log.debug("Query: "+query);
				pstmtRead = connectionDB.prepareStatement(query);
				pstmtRead.setString(1,this.idBusta);
				pstmtRead.setString(2,this.tipo);

				rs = pstmtRead.executeQuery();
				if(rs.next()){
					id = rs.getString("CORRELAZIONE_APPLICATIVA");
				}
				rs.close();
				pstmtRead.close();

				if(id==null)
					throw new Exception("ID di CorrelazioneApplicativa non registrata");
				return id;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore getIDCorrelazioneApplicativa "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtRead != null)
						pstmtRead.close();
				} catch(Exception er) {}
				if(!e.getMessage().equals("ID di CorrelazioneApplicativa non registrata"))
					this.log.error(errorMsg,e);
				else
					this.log.debug(errorMsg);
				throw new GestoreMessaggiException(errorMsg,e);
			}

		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){

			return ((OpenSPCoopStateless)this.openspcoopstate).getIDCorrelazioneApplicativa();

		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	public String getIDCorrelazioneApplicativaRisposta() throws GestoreMessaggiException{

		String id = null;

		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmtRead = null;
			ResultSet rs = null; 
			try{	
				// Costruzione Query
				String query = "SELECT CORRELAZIONE_RISPOSTA FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO = ? AND TIPO=?";

				//log.debug("Query: "+query);
				pstmtRead = connectionDB.prepareStatement(query);
				pstmtRead.setString(1,this.idBusta);
				pstmtRead.setString(2,this.tipo);

				rs = pstmtRead.executeQuery();
				if(rs.next()){
					id = rs.getString("CORRELAZIONE_RISPOSTA");
				}
				rs.close();
				pstmtRead.close();

				if(id==null)
					throw new Exception("ID di CorrelazioneApplicativaRisposta non registrata");
				return id;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore getIDCorrelazioneApplicativaRisposta "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtRead != null)
						pstmtRead.close();
				} catch(Exception er) {}
				if(!e.getMessage().equals("ID di CorrelazioneApplicativaRisposta non registrata")){
					this.log.error(errorMsg,e);
					throw new GestoreMessaggiException(errorMsg,e);
				}
				else{
					this.log.debug(errorMsg);
					return null;
				}
			}

		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){

			return ((OpenSPCoopStateless)this.openspcoopstate).getIDCorrelazioneApplicativaRisposta();

		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	public PdDContext getPdDContext() throws GestoreMessaggiException{
		return this.getPdDContext(false);
	}
	public PdDContext getPdDContext(boolean saveInInstance) throws GestoreMessaggiException{

		if(this.openspcoopstate==null) {
			return this.pddContext;
		}
		
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmtRead = null;
			ResultSet rs = null; 
			try{	

				// PdDContext
				StringBuilder fieldNamesPdDContext_db = new StringBuilder();
				Hashtable<String, String> mapping = new Hashtable<String, String>();
				if(GestoreMessaggi.pddContextSerializer!=null){
					Hashtable<String, String> contextSerializerParameters = GestoreMessaggi.pddContextSerializer.getGestoreMessaggiKeywords();
					if(contextSerializerParameters!=null && contextSerializerParameters.size()>0){
						Enumeration<String> keywordContext = contextSerializerParameters.keys();
						while(keywordContext.hasMoreElements()){
							String keyword =  keywordContext.nextElement();
							if(fieldNamesPdDContext_db.length()>0)
								fieldNamesPdDContext_db.append(" , ");
							String nomeDB = contextSerializerParameters.get(keyword);
							fieldNamesPdDContext_db.append(nomeDB);
							mapping.put(nomeDB, keyword);
						}
					}
				}
				if(fieldNamesPdDContext_db.length() != 0) 
					fieldNamesPdDContext_db.append(" , ");
				fieldNamesPdDContext_db.append("PROTOCOLLO");
				mapping.put("PROTOCOLLO", org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME);
				PdDContext pddContext = new PdDContext();
				if(mapping.size()<=0){
					return pddContext;
				}

				// Costruzione Query
				String query = "SELECT id_transazione, "+fieldNamesPdDContext_db.toString()+" FROM "+GestoreMessaggi.MESSAGGI+" WHERE ID_MESSAGGIO = ? AND TIPO=?";

				//log.debug("Query: "+query);
				pstmtRead = connectionDB.prepareStatement(query);
				pstmtRead.setString(1,this.idBusta);
				pstmtRead.setString(2,this.tipo);

				rs = pstmtRead.executeQuery();
				if(rs.next()){

					Enumeration<String> keysDB = mapping.keys();
					while(keysDB.hasMoreElements()){
						String keyDB = keysDB.nextElement();
						Object object = rs.getObject(keyDB);
						pddContext.addObject(mapping.get(keyDB), object);
					}
					
					String idTransazione = rs.getString("id_transazione");
					if(idTransazione!=null) {
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, idTransazione);
					}

				}else{
					throw new Exception("Messaggio non trovato");
				}
				rs.close();
				pstmtRead.close();

				if(saveInInstance) {
					this.pddContext = pddContext;
				}
				return pddContext;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore getPdDContext "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtRead != null)
						pstmtRead.close();
				} catch(Exception er) {}
				if(!e.getMessage().equals("Messaggio non trovato"))
					this.log.error(errorMsg,e);
				else
					this.log.debug(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}

		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){

			return ((OpenSPCoopStateless)this.openspcoopstate).getPddContext();

		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}











	/* ********  R E A D    M E S S A G E   (Destinato ad un SIL)  ******** */

	/**
	 * Controlla l'esistenza di un messaggio gestito da OpenSPCoop, il quale e' stato associato ad un servizio applicativo. 
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @return true se il messaggio esiste
	 * 
	 */
	public MessaggioIM readMessageForSIL(String servizioApplicativo, Date oraRegistrazione)throws GestoreMessaggiException{
		return readMessageForSIL(servizioApplicativo,false,oraRegistrazione);
	}
	/**
	 * Controlla l'esistenza di un messaggio gestito da OpenSPCoop, il quale e' stato associato ad un servizio applicativo. 
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param searchForRiferimentoMsg Se true, il messaggio viene ricercato per Riferimento Messaggio
	 * @return true se il messaggio esiste
	 * 
	 */
	public MessaggioIM readMessageForSIL(String servizioApplicativo,boolean searchForRiferimentoMsg, Date oraRegistrazione)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{	

				// Se devo cercare per riferimentoMessaggio, prima cerco un messaggio registrato, che possiede
				// come riferimento Messaggio l'ID del gestore.
				// In caso di esistenza uso l'idBusta del messaggio trovato e non quello del gestore
				String idBustaSearch = this.idBusta;
				if(searchForRiferimentoMsg){
					idBustaSearch = mapRiferimentoIntoIDBusta();
				}

				// Ricerco messaggio assiciato
				String query = null;
				if(Configurazione.getSqlQueryObjectType()==null){
					query = "SELECT "+
							GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".INTEGRATION_MANAGER as authIM, "+
							GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SBUSTAMENTO_SOAP as sbSoap, "+
							GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SBUSTAMENTO_INFO_PROTOCOL as sbProtocol "+
							GestoreMessaggi.MESSAGGI+".RIFERIMENTO_MSG as rifMsg, "+
							GestoreMessaggi.MESSAGGI+".id_transazione as idtransazione, "+
							GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SERVIZIO_APPLICATIVO as sa, "+
							GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".NOME_PORTA as nomePorta, "+
							GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE as oramess "+
							" FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+","+GestoreMessaggi.MESSAGGI+
							" WHERE ";
					if(oraRegistrazione!=null) {
						query = query + "("+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ORA_REGISTRAZIONE BETWEEN ? AND ?) AND ";
						query = query + "("+GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE BETWEEN ? AND ?) AND ";
					}
					query = query + GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ID_MESSAGGIO=? AND " +
							GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SERVIZIO_APPLICATIVO=? AND "+
							GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ID_MESSAGGIO="+GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO AND "+
							GestoreMessaggi.MESSAGGI+".PROPRIETARIO=?";
				}
				else {
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
					sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI,"m");
					sqlQueryObject.addFromTable(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI,"sa");
					
					sqlQueryObject.addSelectAliasField("sa", "INTEGRATION_MANAGER", "authIM");
					sqlQueryObject.addSelectAliasField("sa", "SBUSTAMENTO_SOAP", "sbSoap");
					sqlQueryObject.addSelectAliasField("sa", "SBUSTAMENTO_INFO_PROTOCOL", "sbProtocol");
					sqlQueryObject.addSelectAliasField("m", "RIFERIMENTO_MSG", "rifMsg");
					sqlQueryObject.addSelectAliasField("m", "id_transazione", "idtransazione");
					sqlQueryObject.addSelectAliasField("sa", "SERVIZIO_APPLICATIVO", "sa");
					sqlQueryObject.addSelectAliasField("sa", "NOME_PORTA", "nomePorta");
					sqlQueryObject.addSelectAliasField("m", "ORA_REGISTRAZIONE", "oramess");
					
					if(oraRegistrazione!=null) {
						sqlQueryObject.addWhereBetweenCondition("sa.ORA_REGISTRAZIONE", false, "?", "?");
						sqlQueryObject.addWhereBetweenCondition("m.ORA_REGISTRAZIONE", false, "?", "?");
					}
										
					sqlQueryObject.addWhereCondition("sa.ID_MESSAGGIO=?");
					sqlQueryObject.addWhereCondition("sa.SERVIZIO_APPLICATIVO=?");
					
					// join
					sqlQueryObject.addWhereCondition("m.ID_MESSAGGIO=sa.ID_MESSAGGIO");
					
					sqlQueryObject.addWhereCondition("m.PROPRIETARIO=?");
					
					sqlQueryObject.setANDLogicOperator(true);
					
					query = sqlQueryObject.createSQLQuery();
				}
				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(query);
				int index = 1;
				
				Timestamp leftValue = null;
				Timestamp rightValue = null;
				if(oraRegistrazione!=null) {
					leftValue = new Timestamp(oraRegistrazione.getTime() - (1000*60*5));
					rightValue = new Timestamp(oraRegistrazione.getTime() + (1000*60*5));
					pstmt.setTimestamp(index++,leftValue);
					pstmt.setTimestamp(index++,rightValue);
					pstmt.setTimestamp(index++,leftValue);
					pstmt.setTimestamp(index++,rightValue);
				}
				
				pstmt.setString(index++,idBustaSearch);
				pstmt.setString(index++,servizioApplicativo);
				pstmt.setString(index++,ConsegnaContenutiApplicativi.ID_MODULO);
				rs = pstmt.executeQuery();
				if(rs.next()==true){				
					MessaggioIM messaggio = new MessaggioIM();
					messaggio.setAuthorized(rs.getInt("authIM") == CostantiDB.TRUE);
					messaggio.setSbustamentoSoap(rs.getInt("sbSoap") == CostantiDB.TRUE);
					messaggio.setSbustamentoInformazioniProtocollo(rs.getInt("sbProtocol") == CostantiDB.TRUE);
					messaggio.setRiferimentoMessaggio(rs.getString("rifMsg"));
					messaggio.setIdentificativoRichiesta(idBustaSearch);
					messaggio.setIdTransazione(rs.getString("idtransazione"));
					messaggio.setServizioApplicativo(rs.getString("sa"));
					messaggio.setOraRegistrazione(rs.getTimestamp("oramess"));
					messaggio.setNomePorta(rs.getString("nomePorta"));
					
					rs.close();
					pstmt.close();
					
					return messaggio;
				}
				rs.close();
				pstmt.close();

				return null;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, error readMessageForSIL "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}	
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
			// do the right thing
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}
	
	
	
	
	
	/**
	 * Controlla l'esistenza di un messaggio gestito da OpenSPCoop, il quale e' stato associato ad un servizio applicativo. 
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @return true se il messaggio esiste
	 * 
	 */
	public boolean existsMessageForSIL(String servizioApplicativo)throws GestoreMessaggiException{
		return existsMessageForSIL(servizioApplicativo,false);
	}
	/**
	 * Controlla l'esistenza di un messaggio gestito da OpenSPCoop, il quale e' stato associato ad un servizio applicativo. 
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param searchForRiferimentoMsg Se true, il messaggio viene ricercato per Riferimento Messaggio
	 * @return true se il messaggio esiste
	 * 
	 */
	public boolean existsMessageForSIL(String servizioApplicativo,boolean searchForRiferimentoMsg)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{	

				// Se devo cercare per riferimentoMessaggio, prima cerco un messaggio registrato, che possiede
				// come riferimento Messaggio l'ID del gestore.
				// In caso di esistenza uso l'idBusta del messaggio trovato e non quello del gestore
				String idBustaSearch = this.idBusta;
				if(searchForRiferimentoMsg){
					idBustaSearch = mapRiferimentoIntoIDBusta();
				}


				// Ricerco messaggio assiciato
				String query = "SELECT * FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+","+GestoreMessaggi.MESSAGGI+
						" WHERE ";
				query = query + GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ID_MESSAGGIO=? AND " +
						GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SERVIZIO_APPLICATIVO=? AND "+
						GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ID_MESSAGGIO="+GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO AND "+
						GestoreMessaggi.MESSAGGI+".PROPRIETARIO=?";
				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(query);
				int index = 1;
				pstmt.setString(index++,idBustaSearch);
				pstmt.setString(index++,servizioApplicativo);
				pstmt.setString(index++,ConsegnaContenutiApplicativi.ID_MODULO);
				rs = pstmt.executeQuery();
				if(rs.next()==true){
					rs.close();
					pstmt.close();
					return true;
				}
				rs.close();
				pstmt.close();

				return false;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, error existsMessage "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}	
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
			// do the right thing
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Ritorna true se il servizio applicativo e' autorizzato ad utilizzare il servizio IntegrationManager
	 * per ricevere il messaggio 
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @return Ritorna true se il servizio applicativo e' autorizzato ad utilizzare il servizio IntegrationManager
	 * 
	 */
	public boolean checkAutorizzazione(String servizioApplicativo)throws GestoreMessaggiException{
		return checkAutorizzazione(servizioApplicativo,false);
	}
	/**
	 * Ritorna true se il servizio applicativo e' autorizzato ad utilizzare il servizio IntegrationManager
	 * per ricevere il messaggio 
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param searchForRiferimentoMsg Se true, il messaggio viene ricercato per Riferimento Messaggio
	 * @return Ritorna true se il servizio applicativo e' autorizzato ad utilizzare il servizio IntegrationManager
	 * 
	 */
	public boolean checkAutorizzazione(String servizioApplicativo,boolean searchForRiferimentoMsg)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{   
				// Se devo cercare per riferimentoMessaggio, prima cerco un messaggio registrato, che possiede
				// come riferimento Messaggio l'ID del gestore.
				// In caso di esistenza uso l'idBusta del messaggio trovato e non quello del gestore
				String idBustaSearch = this.idBusta;
				if(searchForRiferimentoMsg){
					idBustaSearch = mapRiferimentoIntoIDBusta();
				}


				// Costruzione Query
				String query = "SELECT INTEGRATION_MANAGER FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI
						+" WHERE ID_MESSAGGIO=? AND SERVIZIO_APPLICATIVO=?";
				//log.debug("Query: "+query);
				pstmt =  connectionDB.prepareStatement(query);
				pstmt.setString(1,idBustaSearch);
				pstmt.setString(2,servizioApplicativo);
				rs = pstmt.executeQuery();
				boolean auth = false;
				if(rs.next()){
					if( rs.getInt("INTEGRATION_MANAGER") == 1)
						auth = true;
				}
				rs.close();
				pstmt.close();

				return auth;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, error existsMessage "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}					
			//}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
			// do the right thing
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Ritorna true se il servizio applicativo ha richiesto lo sbustamento per il messaggio a lui destinato
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @return Ritorna true se il servizio applicativo ha richiesto lo sbustamento soap
	 * 
	 */
	public boolean sbustamentoSOAP(String servizioApplicativo)throws GestoreMessaggiException{
		return sbustamentoSOAP(servizioApplicativo,false);
	}
	/**
	 * Ritorna true se il servizio applicativo ha richiesto lo sbustamento per il messaggio a lui destinato
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param searchForRiferimentoMsg Se true, il messaggio viene ricercato per Riferimento Messaggio
	 * @return Ritorna true se il servizio applicativo ha richiesto lo sbustamento soap
	 * 
	 */
	public boolean sbustamentoSOAP(String servizioApplicativo,boolean searchForRiferimentoMsg)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				// Se devo cercare per riferimentoMessaggio, prima cerco un messaggio registrato, che possiede
				// come riferimento Messaggio l'ID del gestore.
				// In caso di esistenza uso l'idBusta del messaggio trovato e non quello del gestore
				String idBustaSearch = this.idBusta;
				if(searchForRiferimentoMsg){
					idBustaSearch = mapRiferimentoIntoIDBusta();
				}


				// Costruzione Query
				String query = "SELECT SBUSTAMENTO_SOAP FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI
						+" WHERE ID_MESSAGGIO=? AND SERVIZIO_APPLICATIVO=?";
				//log.debug("Query: "+query);
				pstmt =  connectionDB.prepareStatement(query);
				pstmt.setString(1,idBustaSearch);
				pstmt.setString(2,servizioApplicativo);
				rs = pstmt.executeQuery();
				boolean sbustamento = false;
				if(rs.next()){
					if( rs.getInt("SBUSTAMENTO_SOAP") == 1)
						sbustamento = true;
				}
				rs.close();
				pstmt.close();

				return sbustamento;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, error getSbustamentoSoap "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}		
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
			// do the right thing
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Ritorna true se il servizio applicativo ha richiesto lo sbustamento per il messaggio a lui destinato
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @return Ritorna true se il servizio applicativo ha richiesto lo sbustamento soap
	 * 
	 */
	public boolean sbustamentoInformazioniProtocollo(String servizioApplicativo)throws GestoreMessaggiException{
		return sbustamentoInformazioniProtocollo(servizioApplicativo,false);
	}
	/**
	 * Ritorna true se il servizio applicativo ha richiesto lo sbustamento per il messaggio a lui destinato
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param searchForRiferimentoMsg Se true, il messaggio viene ricercato per Riferimento Messaggio
	 * @return Ritorna true se il servizio applicativo ha richiesto lo sbustamento soap
	 * 
	 */
	public boolean sbustamentoInformazioniProtocollo(String servizioApplicativo,boolean searchForRiferimentoMsg)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				// Se devo cercare per riferimentoMessaggio, prima cerco un messaggio registrato, che possiede
				// come riferimento Messaggio l'ID del gestore.
				// In caso di esistenza uso l'idBusta del messaggio trovato e non quello del gestore
				String idBustaSearch = this.idBusta;
				if(searchForRiferimentoMsg){
					idBustaSearch = mapRiferimentoIntoIDBusta();
				}


				// Costruzione Query
				String query = "SELECT SBUSTAMENTO_INFO_PROTOCOL FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI
						+" WHERE ID_MESSAGGIO=? AND SERVIZIO_APPLICATIVO=?";
				//log.debug("Query: "+query);
				pstmt =  connectionDB.prepareStatement(query);
				pstmt.setString(1,idBustaSearch);
				pstmt.setString(2,servizioApplicativo);
				rs = pstmt.executeQuery();
				boolean sbustamento = false;
				if(rs.next()){
					if( rs.getInt("SBUSTAMENTO_INFO_PROTOCOL") == 1)
						sbustamento = true;
				}
				rs.close();
				pstmt.close();

				return sbustamento;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, error getSbustamentoSoap "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}		
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
			// do the right thing
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Ritorna true se il messaggio risulta in gestione
	 *
	 * @return true se il  messaggio risulta in gestione
	 * @throws GestoreMessaggiException 
	 * 
	 */
	public boolean existsMessageInProcessamento() throws GestoreMessaggiException{
		return this.existsMessageInProcessamento(false);
	}

	/**
	 * Ritorna true se il messaggio con quel riferimento messaggio risulta in gestione
	 *
	 * @return true se il  messaggio con quel riferimento messaggio risulta in gestione
	 * @throws GestoreMessaggiException 
	 * 
	 */
	public boolean existsMessageInProcessamentoByReference() throws GestoreMessaggiException{
		return this.existsMessageInProcessamento(true);
	}

	/**
	 * Ritorna true se il messaggio risulta in gestione
	 *
	 * @return true se il  messaggio risulta in gestione
	 * @throws GestoreMessaggiException 
	 * 
	 */
	private boolean existsMessageInProcessamento(boolean rifMsg) throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null; 
			try{	
				// Costruzione Query
				StringBuilder bf = new StringBuilder();
				bf.append("SELECT ID_MESSAGGIO FROM ");
				bf.append(GestoreMessaggi.MESSAGGI);
				bf.append(" WHERE ");
				if(!rifMsg){
					bf.append("ID_MESSAGGIO=?");
				}else{
					bf.append("RIFERIMENTO_MSG=?");
				}
				bf.append(" AND TIPO=? AND PROPRIETARIO<>?");
				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(bf.toString());
				pstmt.setString(1,this.idBusta);
				pstmt.setString(2,this.tipo);
				pstmt.setString(3,TimerGestoreMessaggi.ID_MODULO);
				rs = pstmt.executeQuery();
				if(rs.next()){
					rs.close();
					pstmt.close();
					return true;
				}
				rs.close();
				pstmt.close();
				return false;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore existsMessageInProcessamento "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				return false;
			}
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
			// do the right thing
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

















	/* ********  GET ID MESSAGGI DESTINATI AD UN SERVIZIO APPLICATIVO   ******** */

	/**
	 * Ritorna gli ID dei messaggi destinati ad un servizio applicativo
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @return ID dei messaggi destinati ad un servizio applicativo
	 * 
	 */
	public List<IdentificativoIM> getIDMessaggi_ServizioApplicativo(String servizioApplicativo)throws GestoreMessaggiException{

		return this.getIDMessaggi_ServizioApplicativo_engine(servizioApplicativo, -1, -1, null, null, null);

	}


	/**
	 * Ritorna gli ID dei messaggi destinati ad un servizio applicativo
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param counter Indica il numero di id da ritornare
	 * @return ID dei messaggi destinati ad un servizio applicativo
	 * 
	 */
	public List<IdentificativoIM> getIDMessaggi_ServizioApplicativo(String servizioApplicativo,int counter)throws GestoreMessaggiException{

		return this.getIDMessaggi_ServizioApplicativo_engine(servizioApplicativo, counter, -1, null, null, null);

	}

	/**
	 * Ritorna gli ID dei messaggi destinati ad un servizio applicativo
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param counter Indica il numero di id da ritornare
	 * @param offset offset
	 * @return ID dei messaggi destinati ad un servizio applicativo
	 * 
	 */
	public List<IdentificativoIM> getIDMessaggi_ServizioApplicativo(String servizioApplicativo,int counter, int offset)throws GestoreMessaggiException{

		return this.getIDMessaggi_ServizioApplicativo_engine(servizioApplicativo, counter, offset, null, null, null);

	}


	/**
	 * Ritorna gli ID dei messaggi destinati ad un servizio applicativo i quali possiedono un determinato servizio
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param tipoServizio Filtro per TipoServizio
	 * @param servizio Filtro per Servizio
	 * @return ID dei messaggi destinati ad un servizio applicativo
	 * 
	 */
	public List<IdentificativoIM> getIDMessaggi_ServizioApplicativo(String servizioApplicativo,
			String tipoServizio,String servizio)throws GestoreMessaggiException{

		return this.getIDMessaggi_ServizioApplicativo_engine(servizioApplicativo, -1, -1, tipoServizio, servizio, null);

	}

	/**
	 * Ritorna gli ID dei messaggi destinati ad un servizio applicativo i quali possiedono un determinato servizio
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param tipoServizio Filtro per TipoServizio
	 * @param servizio Filtro per Servizio
	 * @param counter Indica il numero di id da ritornare
	 * @return ID dei messaggi destinati ad un servizio applicativo
	 * 
	 */
	public List<IdentificativoIM> getIDMessaggi_ServizioApplicativo(String servizioApplicativo,
			String tipoServizio,String servizio,int counter)throws GestoreMessaggiException{

		return this.getIDMessaggi_ServizioApplicativo_engine(servizioApplicativo, counter, -1, tipoServizio, servizio, null);

	}

	/**
	 * Ritorna gli ID dei messaggi destinati ad un servizio applicativo i quali possiedono un determinato servizio
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param tipoServizio Filtro per TipoServizio
	 * @param servizio Filtro per Servizio
	 * @param counter Indica il numero di id da ritornare
	 * @param offset offset
	 * @return ID dei messaggi destinati ad un servizio applicativo
	 * 
	 */
	public List<IdentificativoIM> getIDMessaggi_ServizioApplicativo(String servizioApplicativo,
			String tipoServizio,String servizio,int counter, int offset)throws GestoreMessaggiException{

		return this.getIDMessaggi_ServizioApplicativo_engine(servizioApplicativo, counter, offset, tipoServizio, servizio, null);

	}

	/**
	 * Ritorna gli ID dei messaggi destinati ad un servizio applicativo i quali possiedono un determinato servizio e azione
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param tipoServizio Filtro per TipoServizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @return ID dei messaggi destinati ad un servizio applicativo
	 * 
	 */
	public List<IdentificativoIM> getIDMessaggi_ServizioApplicativo(String servizioApplicativo,
			String tipoServizio,String servizio,String azione)throws GestoreMessaggiException{

		return this.getIDMessaggi_ServizioApplicativo_engine(servizioApplicativo, -1, -1, tipoServizio, servizio, azione);

	}

	/**
	 * Ritorna gli ID dei messaggi destinati ad un servizio applicativo i quali possiedono un determinato servizio e azione
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param tipoServizio Filtro per TipoServizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @param counter Indica il numero di id da ritornare
	 * @return ID dei messaggi destinati ad un servizio applicativo
	 * 
	 */
	public List<IdentificativoIM> getIDMessaggi_ServizioApplicativo(String servizioApplicativo,
			String tipoServizio,String servizio,String azione,int counter)throws GestoreMessaggiException{

		return this.getIDMessaggi_ServizioApplicativo_engine(servizioApplicativo, counter, -1, tipoServizio, servizio, azione);

	}

	/**
	 * Ritorna gli ID dei messaggi destinati ad un servizio applicativo i quali possiedono un determinato servizio e azione
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param tipoServizio Filtro per TipoServizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @param counter Indica il numero di id da ritornare
	 * @param offset offset
	 * @return ID dei messaggi destinati ad un servizio applicativo
	 * 
	 */
	public List<IdentificativoIM> getIDMessaggi_ServizioApplicativo(String servizioApplicativo,
			String tipoServizio,String servizio,String azione,int counter, int offset)throws GestoreMessaggiException{

		return this.getIDMessaggi_ServizioApplicativo_engine(servizioApplicativo, counter, offset, tipoServizio, servizio, azione);

	}





	/**
	 * Ritorna gli ID dei messaggi destinati ad un servizio applicativo
	 *
	 * @param servizioApplicativo Servizio Applicativo
	 * @param counter Indica il numero di id da ritornare
	 * @param tipoServizio Filtro per TipoServizio
	 * @param servizio Filtro per Servizio
	 * @param azione Filtro per Azione
	 * @return ID dei messaggi destinati ad un servizio applicativo
	 * 
	 */
	private List<IdentificativoIM> getIDMessaggi_ServizioApplicativo_engine(String servizioApplicativo,
			int counter,int offset,String tipoServizio,String servizio,String azione)throws GestoreMessaggiException{

		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			List<IdentificativoIM> ids = new ArrayList<IdentificativoIM>();
			String queryString = null;
			try{	
				// Calcolo data minima
				queryString = getQueryStringGetAllMessagesId(servizioApplicativo,
						counter, offset, tipoServizio, servizio, azione,
						true);
				pstmt = connectionDB.prepareStatement(queryString);
				setPreparedStatementGetAllMessagesId(pstmt,
						servizioApplicativo,
						tipoServizio,servizio,azione,
						null);
				//System.out.println("QUERY MIN DATE ID MESSAGGI IS: ["+queryString+"]");
				rs = pstmt.executeQuery();
				Timestamp minDate = null;
				if(rs.next()) {
					minDate = rs.getTimestamp("MinOraRegistrazione");
				}
				rs.close();
				pstmt.close();
				
				if(minDate!=null) {
				
					// Effettuo ricerca ID DEL SERVIZIO APPLICATIVO
	
					queryString = getQueryStringGetAllMessagesId(servizioApplicativo,
							counter, offset, tipoServizio, servizio, azione,
							false);
					//System.out.println("QUERY ID MESSAGGI IS: ["+queryString+"]");
	
					pstmt = connectionDB.prepareStatement(queryString);
					setPreparedStatementGetAllMessagesId(pstmt,
							servizioApplicativo,
							tipoServizio,servizio,azione,
							minDate);
					rs = pstmt.executeQuery();
	
	
					int countLimit = 0;
					while(rs.next()){
						
						IdentificativoIM idIM = new IdentificativoIM(rs.getString("IdMsgServizioApplicativo"), rs.getTimestamp("OraMessaggio"));
						ids.add(idIM);
						
						// LIMIT Applicativo
						if(counter>=0 && Configurazione.getSqlQueryObjectType()==null){
							countLimit++;
							if(countLimit==counter)
								break;
						}
					}
					rs.close();
					pstmt.close();
					
				}

			} catch(Exception e) {
				String errorMsg = "[GestoreMessaggi.getIDMessaggi_ServizioApplicativo_engine] errore, queryString["+queryString+"]: "+e.getMessage();		
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}

			return ids;
			//			}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//			//TODO Metodo stateful only
			//			return null;
			// do the right thing
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}
	
	private String getQueryStringGetAllMessagesId(String servizioApplicativo,
			int counter,int offset,String tipoServizio,String servizio,String azione,
			boolean findMinDate) throws Exception {
		String queryString = null;
		if(Configurazione.getSqlQueryObjectType()==null){

			if(offset>=0){
				throw new GestoreMessaggiException("La funzione di ricerca tramite offset e limit non e' utilizzabile se nella configurazione della PdD non viene specificato il tipo di database");
			}

			StringBuilder query = new StringBuilder();
			query.append("SELECT ");

			// Select
			if(findMinDate) {
				query.append("MIN(");
				query.append(GestoreMessaggi.MESSAGGI);
				query.append(".ORA_REGISTRAZIONE) as MinOraRegistrazione ");
			}
			else {
				query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
				query.append(".ID_MESSAGGIO as IdMsgServizioApplicativo, ");
				query.append(GestoreMessaggi.MESSAGGI);
				query.append(".ORA_REGISTRAZIONE as OraMessaggio ");
			}
			
			query.append(" FROM ");

			// FROM
			query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
			query.append(",");
			query.append(GestoreMessaggi.MESSAGGI);
			if(tipoServizio!=null || servizio!=null || azione!=null){
				query.append(",");
				query.append(Costanti.REPOSITORY);
			}
			query.append(" WHERE ");

			// join MSG_SERVIZI_APPLICATIVI con MESSAGGI
			query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
			query.append(".ID_MESSAGGIO=");
			query.append(GestoreMessaggi.MESSAGGI);
			query.append(".ID_MESSAGGIO AND ");

			if(tipoServizio!=null || servizio!=null || azione!=null){
				// join REPOSITORY_BUSTE con MESSAGGI
				query.append(Costanti.REPOSITORY);
				query.append(".ID_MESSAGGIO=");
				query.append(GestoreMessaggi.MESSAGGI);
				query.append(".ID_MESSAGGIO AND ");
				query.append(Costanti.REPOSITORY);
				query.append(".TIPO=");
				query.append(GestoreMessaggi.MESSAGGI);
				query.append(".TIPO AND ");
			}

			// Selezione messaggio del servizio applicativo
			query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
			query.append(".SERVIZIO_APPLICATIVO=? AND ");
			query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
			query.append(".INTEGRATION_MANAGER=1 AND ");
			query.append(GestoreMessaggi.MESSAGGI);
			query.append(".TIPO=? AND ");
			query.append(GestoreMessaggi.MESSAGGI);
			query.append(".PROPRIETARIO=? ");
			if(!findMinDate) {
				query.append("AND ");
				query.append(GestoreMessaggi.MESSAGGI);
				query.append(".ORA_REGISTRAZIONE>=? ");
			}

			// Filtro busta
			if(tipoServizio!=null){
				query.append("AND ");
				query.append(Costanti.REPOSITORY);
				query.append(".TIPO_SERVIZIO=? ");
			}
			if(servizio!=null){
				query.append("AND ");
				query.append(Costanti.REPOSITORY);
				query.append(".SERVIZIO=? ");
			}
			if(azione!=null){
				query.append("AND ");
				query.append(Costanti.REPOSITORY);
				query.append(".AZIONE=? ");
			}

			// Ordine risultato
			if(!findMinDate) {
				query.append("ORDER BY ");
				query.append(GestoreMessaggi.MESSAGGI);
				query.append(".ORA_REGISTRAZIONE,");
				query.append(GestoreMessaggi.MESSAGGI);
				query.append(".ID_MESSAGGIO");
			}
			queryString = query.toString();
		}else{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
			//	FROM
			sqlQueryObject.addFromTable(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI);
			if(tipoServizio!=null || servizio!=null || azione!=null){
				sqlQueryObject.addFromTable(Costanti.REPOSITORY);
			}
			
			// Select
			if(findMinDate) {
				sqlQueryObject.addSelectMinField(GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE", "MinOraRegistrazione");
			}
			else {
				sqlQueryObject.addSelectAliasField(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI,"ID_MESSAGGIO","IdMsgServizioApplicativo");
				sqlQueryObject.addSelectAliasField(GestoreMessaggi.MESSAGGI,"ID_MESSAGGIO","IdBustaMessaggio");
				sqlQueryObject.addSelectAliasField(GestoreMessaggi.MESSAGGI,"TIPO","TipoMessaggio");
				sqlQueryObject.addSelectAliasField(GestoreMessaggi.MESSAGGI,"ORA_REGISTRAZIONE","OraMessaggio");
				sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"PROPRIETARIO");
				sqlQueryObject.addSelectField(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI,"SERVIZIO_APPLICATIVO");
				sqlQueryObject.addSelectField(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI,"INTEGRATION_MANAGER");
			}

			sqlQueryObject.setANDLogicOperator(true);
						
			// join MSG_SERVIZI_APPLICATIVI con MESSAGGI
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ID_MESSAGGIO="+GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO");

			if(tipoServizio!=null || servizio!=null || azione!=null){
				// join REPOSITORY_BUSTE con MESSAGGI
				if(!findMinDate) {
					sqlQueryObject.addSelectAliasField(Costanti.REPOSITORY,"ID_MESSAGGIO","IdBustaRepositoryBuste");
					sqlQueryObject.addSelectAliasField(Costanti.REPOSITORY,"TIPO","TipoBustaRepositoryBuste");
					sqlQueryObject.addSelectAliasField(Costanti.REPOSITORY,"ORA_REGISTRAZIONE","OraRepositoryBuste"); // per risolvere ambiguita oracle con OraRegistrazione dei messaggi
				}
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".ID_MESSAGGIO="+GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO");
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO="+GestoreMessaggi.MESSAGGI+".TIPO");
			}

			//	Selezione messaggio del servizio applicativo
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SERVIZIO_APPLICATIVO=?");
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".INTEGRATION_MANAGER=1");
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".TIPO=?");
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".PROPRIETARIO=?");
			if(!findMinDate) {
				sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE>=?");
			}

			// Filtro busta
			if(tipoServizio!=null){
				if(!findMinDate) {
					sqlQueryObject.addSelectField(Costanti.REPOSITORY,"TIPO_SERVIZIO");
				}
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_SERVIZIO=?");
			}
			if(servizio!=null){
				if(!findMinDate) {
					sqlQueryObject.addSelectField(Costanti.REPOSITORY,"SERVIZIO");
				}
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".SERVIZIO=?");
			}
			if(azione!=null){
				if(!findMinDate) {
					sqlQueryObject.addSelectField(Costanti.REPOSITORY,"AZIONE");
				}
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".AZIONE=?");
			}

			if(!findMinDate) {
				// Ordine risultato
				sqlQueryObject.addOrderBy("OraMessaggio");
				sqlQueryObject.addOrderBy("IdBustaMessaggio");
				sqlQueryObject.setSortType(true);

				// Limit
				if(counter>=0)
					sqlQueryObject.setLimit(counter);

				// Offset
				if(offset>=0)
					sqlQueryObject.setOffset(offset);
			}

			queryString = sqlQueryObject.createSQLQuery();
		}
		return queryString;
	}
	
	private void setPreparedStatementGetAllMessagesId(PreparedStatement pstmt,
			String servizioApplicativo,
			String tipoServizio,String servizio,String azione,
			Timestamp minDate) throws Exception {
		pstmt.setString(1,servizioApplicativo);
		pstmt.setString(2,Costanti.INBOX);
		pstmt.setString(3,ConsegnaContenutiApplicativi.ID_MODULO);

		int indexPstmt = 4;
		if(minDate!=null) {
			pstmt.setTimestamp(indexPstmt,minDate);
			indexPstmt++;
		}
		if(tipoServizio!=null){
			pstmt.setString(indexPstmt,tipoServizio);
			indexPstmt++;
		}
		if(servizio!=null){
			pstmt.setString(indexPstmt,servizio);
			indexPstmt++;
		}
		if(azione!=null){
			pstmt.setString(indexPstmt,azione);
			indexPstmt++;
		}
	}








	/* ------------- ELIMINAZIONE DESTINATARIO MESSAGGI (SIL) -------------- */
	/**
	 * Elimina dalla Tabella MSG_SERVIZI_APPLICATIVI l'entry che possiede l'ID del messaggio e il servizioApplicativo
	 * passato come parametro.
	 * Effettua poi un controllo sulla tabella per verificare che esistano altri servizi applicativi che sono
	 * interessati al messaggio identificato dall'ID. Se ne esistono, il metodo termina.
	 * Se non vi sono altri servizi applicativi, il metodo elimina dalla Tabella MESSAGGIO il messaggio che possiede l'ID,
	 * ed inoltre effettua una deleteFromDB e deleteFromFileSystem del messaggio salvato precedentemente.
	 *
	 * @param servizioApplicativo ServizioApplicativo
	 * @param riferimentoMessaggio Eventuale messaggio riferito
	 * @deprecated utilizzare la versione non serializable
	 */
	@Deprecated
	public void eliminaDestinatarioMessaggio_serializable(String servizioApplicativo,String riferimentoMessaggio)throws GestoreMessaggiException{
		this.eliminaDestinatarioMessaggio_serializable(servizioApplicativo,riferimentoMessaggio,Costanti.GESTIONE_SERIALIZABLE_ATTESA_ATTIVA,Costanti.GESTIONE_SERIALIZABLE_CHECK_INTERVAL);
	}

	/**
	 * Elimina dalla Tabella MSG_SERVIZI_APPLICATIVI l'entry che possiede l'ID del messaggio e il servizioApplicativo
	 * passato come parametro.
	 * Effettua poi un controllo sulla tabella per verificare che esistano altri servizi applicativi che sono
	 * interessati al messaggio identificato dall'ID. Se ne esistono, il metodo termina.
	 * Se non vi sono altri servizi applicativi, il metodo elimina dalla Tabella MESSAGGIO il messaggio che possiede l'ID,
	 * ed inoltre effettua una deleteFromDB e deleteFromFileSystem del messaggio salvato precedentemente.
	 *
	 * @param servizioApplicativo ServizioApplicativo
	 * @param riferimentoMessaggio Eventuale messaggio riferito
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * @deprecated utilizzare la versione non serializable
	 */
	@Deprecated
	public void eliminaDestinatarioMessaggio_serializable(String servizioApplicativo,String riferimentoMessaggio,
			long attesaAttiva,int checkInterval)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			/*
	  Viene realizzato con livello di isolamento SERIALIZABLE, per essere sicuri
	  che esecuzioni parallele non leggano dati inconsistenti.
	  Con il livello SERIALIZABLE, se ritorna una eccezione, deve essere riprovato
			 */
			// setAutoCommit e livello Isolamento
			int oldTransactionIsolation = -1;
			try{
				oldTransactionIsolation = connectionDB.getTransactionIsolation();
				connectionDB.setAutoCommit(false);
				JDBCUtilities.setTransactionIsolationSerializable(this.propertiesReader.getDatabaseType(), connectionDB);
			} catch(Exception er) {
				String errorMsg = "GESTORE_MESSAGGI, error  eliminaDestinatarioMessaggio (setIsolation)"+this.idBusta+"/"+servizioApplicativo+": "+er.getMessage();		
				this.log.error(errorMsg,er);
				throw new GestoreMessaggiException(errorMsg,er);
			}


			// ELIMINAZIONE DESTINATARIO
			boolean deleteDestinatarioOK = false;
			//int silDelete = -1;

			long scadenzaWhile = DateManager.getTimeMillis() + attesaAttiva;

			while(deleteDestinatarioOK==false && DateManager.getTimeMillis() < scadenzaWhile){

				PreparedStatement pstmtDeleteSIL = null;
				try{

					// Costruzione Query
					String query = "DELETE FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" WHERE ID_MESSAGGIO=? AND SERVIZIO_APPLICATIVO=?";
					//log.debug("Query: "+query);
					pstmtDeleteSIL=connectionDB.prepareStatement(query);
					pstmtDeleteSIL.setString(1,this.idBusta);
					pstmtDeleteSIL.setString(2,servizioApplicativo);
					//silDelete = pstmtDeleteSIL.executeUpdate();
					pstmtDeleteSIL.executeUpdate();
					pstmtDeleteSIL.close();

					// Chiusura Transazione
					connectionDB.commit();
					stateful.updateConnection(connectionDB);
					if(this.isRichiesta) this.openspcoopstate.setStatoRichiesta(stateful);
					else this.openspcoopstate.setStatoRisposta(stateful);
					// Eliminazione Destinatario effettuata
					deleteDestinatarioOK = true;

				} catch(Exception e) {
					//log.error("ERROR ELIMINAZIONE DESTINATARIO MESSAGGIO ["+e.getMessage()+"]");
					try{
						if( pstmtDeleteSIL != null  )
							pstmtDeleteSIL.close();
					} catch(Exception er) {}
					try{
						connectionDB.rollback();
						stateful.updateConnection(connectionDB);
						if(this.isRichiesta) this.openspcoopstate.setStatoRichiesta(stateful);
						else this.openspcoopstate.setStatoRisposta(stateful);
					} catch(Exception er) {}
				}

				if(deleteDestinatarioOK == false){
					// Per aiutare ad evitare conflitti
					try{
						Utilities.sleep((new java.util.Random()).nextInt(checkInterval)); // random da 0ms a checkIntervalms
					}catch(Exception eRandom){}
				}
			}


			// Se ho eliminato un sil 
			// (puo' darsi che accessi sincronizzati di Gop e ConsegneContenutiApplicativi faccino uno il lavoro per l'altro)
			//if(silDelete>0){


			// ELIMINAZIONE RIFERIMENTO MSG
			boolean deleteRiferimentoMsgOK = false;
			boolean eliminazioneRifCompleta = false;

			scadenzaWhile = DateManager.getTimeMillis() + attesaAttiva;

			while(deleteRiferimentoMsgOK==false && DateManager.getTimeMillis() < scadenzaWhile){

				PreparedStatement pstmtUpdateMsg= null;
				PreparedStatement pstmt = null;
				ResultSet rs = null;
				try{


					// VERIFICA SE CI SONO ALTRI DESTINATARI
					String query = "SELECT  * FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" WHERE ID_MESSAGGIO=? FOR UPDATE";
					//log.debug("Query: "+query);
					pstmt = connectionDB.prepareStatement(query);
					pstmt.setString(1,this.idBusta);
					rs = pstmt.executeQuery();
					eliminazioneRifCompleta = !rs.next();

					/* 
		      if(eliminazioneRifCompleta==false){
		      log.info("---------------------");
		      log.info("-------ELIMINATO["+servizioApplicativo+"]--------------"); 
		      log.info("------- ["+rs.getString("SERVIZIO_APPLICATIVO")+"] ---------");
		      while(rs.next())
		      log.info("------- ["+rs.getString("SERVIZIO_APPLICATIVO")+"] ---------");
		      log.info("---------------------");
		      }else{
		      log.info("ELIMINAZIONE COMPLETA");
		      }
					 */

					rs.close();
					pstmt.close();


					// ELIMINAZIONE MESSAGGIO
					if (eliminazioneRifCompleta) {		

						// Imposto lo stato del messaggio ad 'eliminabile'
						// Costruzione Query
						StringBuilder queryUpdate = new StringBuilder();
						queryUpdate.append("UPDATE ");
						queryUpdate.append(GestoreMessaggi.MESSAGGI);
						queryUpdate.append(" SET PROPRIETARIO=? WHERE  ID_MESSAGGIO = ? AND TIPO=?");
						pstmtUpdateMsg =  connectionDB.prepareStatement(queryUpdate.toString());
						pstmtUpdateMsg.setString(1,TimerGestoreMessaggi.ID_MODULO);
						pstmtUpdateMsg.setString(2,this.idBusta);
						pstmtUpdateMsg.setString(3,Costanti.INBOX); // i messaggi con destinatari sono tutti di tipo 'INBOX'
						pstmtUpdateMsg.execute();
						pstmtUpdateMsg.close();

						// Add Proprietario into table
						this.addProprietarioIntoTable(Costanti.INBOX, this.idBusta, TimerGestoreMessaggi.ID_MODULO);

					}
					//else
					//  log.info("ELIMINAZIONE PARZIALE");


					// Chiusura Transazione
					connectionDB.commit();
					stateful.updateConnection(connectionDB);
					if(this.isRichiesta) this.openspcoopstate.setStatoRichiesta(stateful);
					else this.openspcoopstate.setStatoRisposta(stateful);

					// Eliminazione Destinatario effettuata
					deleteRiferimentoMsgOK = true;

				} catch(Exception e) {
					//log.error("ERROR ELIMINAZIONE DESTINATARIO MESSAGGIO ["+e.getMessage()+"]");
					try{
						if( pstmtUpdateMsg != null )
							pstmtUpdateMsg.close();
					} catch(Exception er) {}
					try{
						if( rs != null )
							rs.close();
					} catch(Exception er) {}
					try{
						if( pstmt != null )
							pstmt.close();
					} catch(Exception er) {}
					try{
						connectionDB.rollback();
						stateful.updateConnection(connectionDB);
						if(this.isRichiesta) this.openspcoopstate.setStatoRichiesta(stateful);
						else this.openspcoopstate.setStatoRisposta(stateful);
					} catch(Exception er) {}
				}

				if(deleteRiferimentoMsgOK == false){
					// Per aiutare ad evitare conflitti
					try{
						Utilities.sleep((new java.util.Random()).nextInt(checkInterval)); // random da 0ms a checkIntervalms
					}catch(Exception eRandom){}
				}

				if(eliminazioneRifCompleta){
					// Aggiornamento cache proprietario messaggio
					this.addProprietariIntoCache_readFromTable("GestoreMessaggi", "eliminaDestinatarioMessaggio_serializable",riferimentoMessaggio,false);
				}
			}
			//}

			// Ripristino Transazione
			try{
				connectionDB.setTransactionIsolation(oldTransactionIsolation);
				connectionDB.setAutoCommit(true);
				stateful.updateConnection(connectionDB);
				if(this.isRichiesta) this.openspcoopstate.setStatoRichiesta(stateful);
				else this.openspcoopstate.setStatoRisposta(stateful);
			} catch(Exception er) {
				String errorMsg = "GESTORE_MESSAGGI, error  eliminaDestinatarioMessaggio (ripristinoIsolation)"+this.idBusta+"/"+servizioApplicativo+": "+er.getMessage();		
				this.log.error(errorMsg,er);
				throw new GestoreMessaggiException(errorMsg,er);
			}
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//TODO checkme NOP
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Elimina dalla Tabella MSG_SERVIZI_APPLICATIVI l'entry che possiede l'ID del messaggio e il servizioApplicativo
	 * passato come parametro.
	 * Effettua poi un controllo sulla tabella per verificare che esistano altri servizi applicativi che sono
	 * interessati al messaggio identificato dall'ID. Se ne esistono, il metodo termina.
	 * Se non vi sono altri servizi applicativi, il metodo elimina dalla Tabella MESSAGGIO il messaggio che possiede l'ID,
	 * ed inoltre effettua una deleteFromDB e deleteFromFileSystem del messaggio salvato precedentemente.
	 *
	 * @param servizioApplicativo ServizioApplicativo
	 * @param riferimentoMessaggio Eventuale messaggio riferito
	 */
	public void eliminaDestinatarioMessaggio(String servizioApplicativo, String riferimentoMessaggio)throws GestoreMessaggiException{
		eliminaDestinatarioMessaggio(servizioApplicativo, riferimentoMessaggio, null);
	}
	public void eliminaDestinatarioMessaggio(String servizioApplicativo, String riferimentoMessaggio, Date oraRegistrazione)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful || this.oneWayVersione11) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();

			Timestamp leftValue = null;
			Timestamp rightValue = null;
			if(oraRegistrazione!=null) {
				leftValue = new Timestamp(oraRegistrazione.getTime() - (1000*60*5));
				rightValue = new Timestamp(oraRegistrazione.getTime() + (1000*60*5));
			}
			
			PreparedStatement pstmtDeleteSIL = null;
			try{

				//this.log.debug("DELETE MSG_SERVIZI_APPLICATIVI WHERE ID_MESSAGGIO='"+this.idBusta+"' AND SERVIZIO_APPLICATIVO='"+servizioApplicativo+"'");

				// Costruzione Query
				String query = "DELETE FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" WHERE ";
				if(oraRegistrazione!=null) {
					query = query +"( ORA_REGISTRAZIONE BETWEEN ? AND ?) AND ";
				}
				query = query +" ID_MESSAGGIO=? AND SERVIZIO_APPLICATIVO=?";
				
				//log.debug("Query: "+query);
				pstmtDeleteSIL= connectionDB.prepareStatement(query);
				int index = 1;
				
				if(oraRegistrazione!=null) {
					pstmtDeleteSIL.setTimestamp(index++,leftValue);
					pstmtDeleteSIL.setTimestamp(index++,rightValue);
				}
				
				pstmtDeleteSIL.setString(index++,this.idBusta);
				pstmtDeleteSIL.setString(index++,servizioApplicativo);

				pstmtDeleteSIL.executeUpdate();
				pstmtDeleteSIL.close();

			} catch(Exception e) {
				try{
					if( pstmtDeleteSIL != null  )
						pstmtDeleteSIL.close();
				} catch(Exception er) {}
				String msgError = "ERROR ELIMINAZIONE DESTINATARIO MESSAGGIO: "+e.getMessage();
				this.log.error(msgError,e);
				throw new GestoreMessaggiException(msgError,e);
			}

			PreparedStatement pstmtUpdateMsg= null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{


				// VERIFICA SE CI SONO ALTRI DESTINATARI
				String query = "SELECT  * FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" WHERE ";
				if(oraRegistrazione!=null) {
					query = query +"( ORA_REGISTRAZIONE BETWEEN ? AND ?) AND ";
				}
				query = query +"ID_MESSAGGIO=?";
							
				//log.debug("Query: "+query);
				pstmt =  connectionDB.prepareStatement(query);
				int index = 1;
				
				if(oraRegistrazione!=null) {
					pstmt.setTimestamp(index++,leftValue);
					pstmt.setTimestamp(index++,rightValue);
				}
				
				pstmt.setString(index++,this.idBusta);
				rs = pstmt.executeQuery();
				boolean eliminazioneRifCompleta = !rs.next();

				/*
	      if(eliminazioneRifCompleta==false){
	      log.info("---------------------");
	      log.info("-------ELIMINATO["+servizioApplicativo+"]--------------"); 
	      log.info("------- ["+rs.getString("SERVIZIO_APPLICATIVO")+"] ---------");
	      while(rs.next())
	      log.info("------- ["+rs.getString("SERVIZIO_APPLICATIVO")+"] ---------");
	      log.info("---------------------");
	      }else{
	      log.info("ELIMINAZIONE COMPLETA");
	      }
				 */

				rs.close();
				pstmt.close();


				// ELIMINAZIONE MESSAGGIO
				if (eliminazioneRifCompleta) {		

					// Elimino utilizzo 

					RepositoryBuste repositoryBuste= new RepositoryBuste(stateMSG, this.isRichiesta, this.protocolFactory);
					repositoryBuste.eliminaUtilizzoPdDFromInBox(this.idBusta, oraRegistrazione);

					if (this.oneWayVersione11) {
						repositoryBuste.eliminaBustaStatelessFromInBox(this.idBusta, oraRegistrazione);
					}

					// Imposto lo stato del messaggio ad 'eliminabile'
					// Costruzione Querya
					StringBuilder queryUpdate = new StringBuilder();
					queryUpdate.append("UPDATE ");
					queryUpdate.append(GestoreMessaggi.MESSAGGI);
					queryUpdate.append(" SET PROPRIETARIO=? WHERE ");
					if(oraRegistrazione!=null) {
						queryUpdate.append("(ORA_REGISTRAZIONE BETWEEN ? AND ?) AND ");
					}
					queryUpdate.append("ID_MESSAGGIO = ? AND TIPO=?");
					
					pstmtUpdateMsg = connectionDB.prepareStatement(queryUpdate.toString());
					index = 1;
					pstmtUpdateMsg.setString(index++,TimerGestoreMessaggi.ID_MODULO);
					
					if(oraRegistrazione!=null) {
						pstmtUpdateMsg.setTimestamp(index++,leftValue);
						pstmtUpdateMsg.setTimestamp(index++,rightValue);
					}
					
					pstmtUpdateMsg.setString(index++,this.idBusta);
					pstmtUpdateMsg.setString(index++,Costanti.INBOX); // i messaggi con destinatari sono tutti di tipo 'INBOX'

					stateMSG.getPreparedStatement().put("UPDATE aggiornaProprietarioMessaggioOpenSPCoop "+this.tipo+"/"+this.idBusta,pstmtUpdateMsg);

					// Add Proprietario into table
					this.addProprietarioIntoTable(Costanti.INBOX, this.idBusta, TimerGestoreMessaggi.ID_MODULO);

					// Commit
					boolean oldValue = false;
					if(this.oneWayVersione11){
						oldValue = ((OpenSPCoopStateless)this.openspcoopstate).isUseConnection();
						((OpenSPCoopStateless)this.openspcoopstate).setUseConnection(true);
					}
					this.openspcoopstate.commit();
					if(this.oneWayVersione11){
						((OpenSPCoopStateless)this.openspcoopstate).setUseConnection(oldValue);
					}


				}
				//else
				//  log.info("ELIMINAZIONE PARZIALE");


				if(eliminazioneRifCompleta){
					// Aggiornamento cache proprietario messaggio
					this.addProprietariIntoCache_readFromTable("GestoreMessaggi", "eliminaDestinatarioMessaggio",riferimentoMessaggio,false);
				}

				if(this.isRichiesta) this.openspcoopstate.setStatoRichiesta(stateMSG);
				else this.openspcoopstate.setStatoRisposta(stateMSG);


			} catch(Exception e) {
				try{
					if( pstmtUpdateMsg != null )
						pstmtUpdateMsg.close();
				} catch(Exception er) {}
				try{
					if( rs != null )
						rs.close();
				} catch(Exception er) {}
				try{
					if( pstmt != null )
						pstmt.close();
				} catch(Exception er) {}
				String msgError = "ERROR ELIMINAZIONE DESTINATARIO MESSAGGIO, STEP 2: "+e.getMessage();
				this.log.error(msgError,e);
				throw new GestoreMessaggiException(msgError,e);
			}
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless && !this.oneWayVersione11){
			//NOP
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}

	}











	/* ------------- LETTURA MESSAGGI DA RICONSEGNARE -------------- */

	public List<MessaggioServizioApplicativo> readMessaggiDaRiconsegnareIntoBoxByPriorita(int limit,
			boolean verificaPresenzaMessaggiDaRispedire, boolean calcolaDataMinimaMessaggiRispedire,
			Date riconsegna, int presaInConsegnaMaxLife, 
			boolean debug, RunnableLogger loggerSql,
			String coda, String priorita)throws GestoreMessaggiException{
		return  _readMessaggiDaRiconsegnareIntoBox(limit,
				verificaPresenzaMessaggiDaRispedire, calcolaDataMinimaMessaggiRispedire,
				riconsegna, presaInConsegnaMaxLife, 
				debug, loggerSql,
				coda, priorita);
	}
	public List<MessaggioServizioApplicativo> readMessaggiDaRiconsegnareIntoBoxByServiziApplicativPrioritari(int limit,
			boolean verificaPresenzaMessaggiDaRispedire, boolean calcolaDataMinimaMessaggiRispedire,
			Date riconsegna, int presaInConsegnaMaxLife, 
			boolean debug, RunnableLogger loggerSql,
			String coda, String ... serviziApplicativiPrioritari)throws GestoreMessaggiException{
		return  _readMessaggiDaRiconsegnareIntoBox(limit,
				verificaPresenzaMessaggiDaRispedire, calcolaDataMinimaMessaggiRispedire,
				riconsegna, presaInConsegnaMaxLife, 
				debug, loggerSql,
				coda, null,
				serviziApplicativiPrioritari);
	}
	
	private List<MessaggioServizioApplicativo> _readMessaggiDaRiconsegnareIntoBox(int limit,
			boolean verificaPresenzaMessaggiDaRispedire, boolean calcolaDataMinimaMessaggiRispedire,
			Date riconsegna, int presaInConsegnaMaxLife, 
			boolean debug, RunnableLogger loggerSql,
			String coda, String priorita,
			String ... serviziApplicativiPrioritari)throws GestoreMessaggiException{

		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta())
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();

			List<MessaggioServizioApplicativo> idMsg = new ArrayList<MessaggioServizioApplicativo>();

			Date dataRilascioLock = new Date( DateManager.getTimeMillis() - this.propertiesReader.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife() );
			
			String tipo = Costanti.INBOX;
						
			Timestamp dataMinima = null;
			if(verificaPresenzaMessaggiDaRispedire && calcolaDataMinimaMessaggiRispedire) {
				PreparedStatement pstmt = null;
				ResultSet rs = null;
				String queryString = null;
				try{ 

					if(Configurazione.getSqlQueryObjectType()==null){
						StringBuilder query = new StringBuilder();
						query.append("SELECT min("+GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE as minOraMess ");
						
						query.append(" FROM ");
						query.append(GestoreMessaggi.MESSAGGI);
						query.append(",");
						query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
						
						query.append(" WHERE ");
						
						// join
						query.append(" ");
						query.append(GestoreMessaggi.MESSAGGI);
						query.append(".ID_MESSAGGIO=");
						query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
						query.append(".ID_MESSAGGIO AND ");
						
						query.append(" ").append(GestoreMessaggi.MESSAGGI).append(".TIPO=? AND ").
							  append(GestoreMessaggi.MESSAGGI).append(".PROPRIETARIO=? ");
						query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".TIPO_CONSEGNA=? ");
						query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".ATTESA_ESITO=? ");
						query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".CODA=? ");
						if(serviziApplicativiPrioritari!=null && serviziApplicativiPrioritari.length>0) {
							query.append(" AND (");
							if(serviziApplicativiPrioritari.length==1) {
								query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".SERVIZIO_APPLICATIVO=?");
							}
							else {
								query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".SERVIZIO_APPLICATIVO IN (");
								for (int i = 0; i < serviziApplicativiPrioritari.length; i++) {
									if(i>0) {
										query.append(",");
									}
									query.append(serviziApplicativiPrioritari[i]);
								}
								query.append(") ");
							}
							query.append(")");
						}
						else if(priorita!=null) {
							query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".PRIORITA=? ");
						}
						query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".ERRORE_PROCESSAMENTO_COMPACT is not null "); // per non intralciare con la "prima" consegna
						query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".RISPEDIZIONE<=? ");
						query.append(" AND (").
							append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".LOCK_CONSEGNA is null ").
							append(" OR ").
							append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".LOCK_CONSEGNA < ? ").
							append(")");
						
						queryString = query.toString();
					}else{

						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
						sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI,"m");
						sqlQueryObject.addFromTable(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI,"sa");

						sqlQueryObject.addSelectMinField("m", "ORA_REGISTRAZIONE", "minOraMess");
						
						// join
						sqlQueryObject.addWhereCondition("m.ID_MESSAGGIO=sa.ID_MESSAGGIO");
						
						sqlQueryObject.addWhereCondition("m.TIPO=?");
						sqlQueryObject.addWhereCondition("m.PROPRIETARIO=?");
						sqlQueryObject.addWhereCondition("sa.TIPO_CONSEGNA=?");
						sqlQueryObject.addWhereCondition("sa.ATTESA_ESITO=?");
						sqlQueryObject.addWhereCondition("sa.CODA=?");
						if(serviziApplicativiPrioritari!=null && serviziApplicativiPrioritari.length>0) {
							if(serviziApplicativiPrioritari.length==1) {
								sqlQueryObject.addWhereCondition("sa.SERVIZIO_APPLICATIVO=?");
							}
							else {
								sqlQueryObject.addWhereINCondition("sa.SERVIZIO_APPLICATIVO", true, serviziApplicativiPrioritari);
							}
						}
						else if(priorita!=null) {
							sqlQueryObject.addWhereCondition("sa.PRIORITA=?");
						}
						sqlQueryObject.addWhereCondition("sa.ERRORE_PROCESSAMENTO_COMPACT is not null"); // per non intralciare con la "prima" consegna
						sqlQueryObject.addWhereCondition("sa.RISPEDIZIONE<=?");
						sqlQueryObject.addWhereCondition(false, "sa.LOCK_CONSEGNA is null", "sa.LOCK_CONSEGNA < ?");
						
						sqlQueryObject.setANDLogicOperator(true);
						
						queryString = sqlQueryObject.createSQLQuery();
					}
					
					pstmt = connectionDB.prepareStatement(queryString);
					int index = 1;
					List<Object> params = new ArrayList<Object>();
					
					pstmt.setString(index++,tipo); params.add(tipo);
					pstmt.setString(index++,ConsegnaContenutiApplicativi.ID_MODULO); params.add(ConsegnaContenutiApplicativi.ID_MODULO);
					pstmt.setString(index++,GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE); params.add(GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE);
					pstmt.setInt(index++,CostantiDB.FALSE); params.add(CostantiDB.FALSE);
					pstmt.setString(index++,coda); params.add(coda);
					
					if(serviziApplicativiPrioritari!=null && serviziApplicativiPrioritari.length>0) {
						if(serviziApplicativiPrioritari.length==1) {
							pstmt.setString(index++,serviziApplicativiPrioritari[0]);
							params.add(serviziApplicativiPrioritari[0]);
						}
					}
					else if(priorita!=null) {
						pstmt.setString(index++,priorita);
						params.add(priorita);
					}
					
					pstmt.setTimestamp(index++,new Timestamp(riconsegna.getTime()));
					params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(riconsegna));
					
					pstmt.setTimestamp(index++, new Timestamp(dataRilascioLock.getTime()));
					params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(dataRilascioLock));

					long startDateSQLCommand = DateManager.getTimeMillis();
					String comandoSql = null;
					if(debug) {
						comandoSql = DBUtils.formatSQLString(queryString, params.toArray());
						loggerSql.debug("[QUERY] (Messaggi.daRispedire.computeMinDate) ["+comandoSql+"] ...");
					}
					rs = pstmt.executeQuery();
					long endDateSQLCommand = DateManager.getTimeMillis();
					long diffSQLCommand = endDateSQLCommand - startDateSQLCommand;
					if(debug) {
						loggerSql.debug("[QUERY] (Messaggi.daRispedire.computeMinDate) ["+comandoSql+"] effettuata in "+Utilities.convertSystemTimeIntoString_millisecondi(diffSQLCommand, true));
					}
					
					if(rs.next()){
						dataMinima = rs.getTimestamp("minOraMess");
					}
					rs.close(); rs = null;
					pstmt.close(); pstmt=null;
					
					if(dataMinima==null) {
						// non esistono messaggi; e' inutile fare la query successiva
						return idMsg;
					}
					
				} catch(Throwable e) {
					String errorMsg = "[GestoreMessaggi.readMessaggiDaRiconsegnareIntoBox] errore durante il calcolo della data minima, queryString["+queryString+"] : "+e.getMessage();
					try{
						if(rs != null)
							rs.close();
					} catch(Exception er) {}
					try{
						if(pstmt != null)
							pstmt.close();
					} catch(Exception er) { }
					loggerSql.error(errorMsg,e);
					// Vado avanza senza data minima throw new GestoreMessaggiException(errorMsg,e);
				}
			}
			
			PreparedStatement pstmtMsgDaSpedire = null;
			ResultSet rs = null;
			String queryString = null;
			try{   
				
				// Query per Ricerca messaggi eliminati (proprietario:EliminatoreMesaggi)
				if(Configurazione.getSqlQueryObjectType()==null){
					StringBuilder query = new StringBuilder();
					query.append("SELECT "+GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO as idmess, ");
					query.append(" "+GestoreMessaggi.MESSAGGI+".id_transazione as idtransazione, ");
					query.append(" "+GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE as oramess, ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SERVIZIO_APPLICATIVO as sa, ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SBUSTAMENTO_SOAP as sbSoap, ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SBUSTAMENTO_INFO_PROTOCOL as sbProtocol ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".NOME_PORTA as nomePorta ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".LOCK_CONSEGNA as saLock "); // non usare lock e' una parola riservata di oracle
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".CLUSTER_ID as saCluster "); // non usare cluster e' una parola riservata di oracle
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ATTESA_ESITO as attesa ");
					
					query.append(" FROM ");
					query.append(GestoreMessaggi.MESSAGGI);
					query.append(",");
					query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
					
					query.append(" WHERE ");
					
					// Ottimizzazione per partizionamento
					if(verificaPresenzaMessaggiDaRispedire) {
						if(dataMinima!=null) {
							query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".ORA_REGISTRAZIONE>=? ").append(" AND ");
							query.append(GestoreMessaggi.MESSAGGI).append(".ORA_REGISTRAZIONE>=? ").append(" AND ");
						}
					}
					else {
						query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".ORA_REGISTRAZIONE>=? ").append(" AND ");
						query.append(GestoreMessaggi.MESSAGGI).append(".ORA_REGISTRAZIONE>=? ").append(" AND ");
					}
					
					// join
					query.append(" ");
					query.append(GestoreMessaggi.MESSAGGI);
					query.append(".ID_MESSAGGIO=");
					query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
					query.append(".ID_MESSAGGIO AND ");
					
					query.append(" ").append(GestoreMessaggi.MESSAGGI).append(".TIPO=? AND ").
						  append(GestoreMessaggi.MESSAGGI).append(".PROPRIETARIO=? ");
					query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".TIPO_CONSEGNA=? ");
					query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".ATTESA_ESITO=? ");
					query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".CODA=? ");
					if(serviziApplicativiPrioritari!=null && serviziApplicativiPrioritari.length>0) {
						query.append(" AND (");
						if(serviziApplicativiPrioritari.length==1) {
							query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".SERVIZIO_APPLICATIVO=?");
						}
						else {
							query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".SERVIZIO_APPLICATIVO IN (");
							for (int i = 0; i < serviziApplicativiPrioritari.length; i++) {
								if(i>0) {
									query.append(",");
								}
								query.append(serviziApplicativiPrioritari[i]);
							}
							query.append(") ");
						}
						query.append(")");
					}
					else if(priorita!=null) {
						query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".PRIORITA=? ");
					}
					if(verificaPresenzaMessaggiDaRispedire) {
						query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".ERRORE_PROCESSAMENTO_COMPACT is not null "); // per non intralciare con la "prima" consegna
					}
					else {
						query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".ERRORE_PROCESSAMENTO_COMPACT = ? "); // prima consegna
					}
					query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".RISPEDIZIONE<=? ");
					query.append(" AND (").
						append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".LOCK_CONSEGNA is null ").
						append(" OR ").
						append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".LOCK_CONSEGNA < ? ").
						append(")");
					
					queryString = query.toString();
				}else{

					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
					sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI,"m");
					sqlQueryObject.addFromTable(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI,"sa");
					
					sqlQueryObject.addSelectAliasField("m", "ID_MESSAGGIO", "idmess");
					sqlQueryObject.addSelectAliasField("m", "id_transazione", "idtransazione");
					sqlQueryObject.addSelectAliasField("m", "ORA_REGISTRAZIONE", "oramess");
					sqlQueryObject.addSelectAliasField("sa", "SERVIZIO_APPLICATIVO", "sa");
					sqlQueryObject.addSelectAliasField("sa", "SBUSTAMENTO_SOAP", "sbSoap");
					sqlQueryObject.addSelectAliasField("sa", "SBUSTAMENTO_INFO_PROTOCOL", "sbProtocol");
					sqlQueryObject.addSelectAliasField("sa", "NOME_PORTA", "nomePorta");
					sqlQueryObject.addSelectAliasField("sa", "LOCK_CONSEGNA", "saLock"); // non usare lock e' una parola riservata di oracle
					sqlQueryObject.addSelectAliasField("sa", "CLUSTER_ID", "saCluster"); // non usare cluster e' una parola riservata di oracle
					sqlQueryObject.addSelectAliasField("sa", "ATTESA_ESITO", "attesa");
					sqlQueryObject.addSelectAliasField("sa", "ID_MESSAGGIO", "saidmess");
					sqlQueryObject.addSelectAliasField("sa", "TIPO_CONSEGNA", "satipocons");
					sqlQueryObject.addSelectAliasField("sa", "ERRORE_PROCESSAMENTO_COMPACT", "saerrproccom");
					sqlQueryObject.addSelectAliasField("sa", "RISPEDIZIONE", "sarisp");
					sqlQueryObject.addSelectAliasField("sa", "CODA", "sacoda");
					sqlQueryObject.addSelectAliasField("sa", "PRIORITA", "saprio");
					sqlQueryObject.addSelectAliasField("m","ORA_REGISTRAZIONE", "oramsg");
					sqlQueryObject.addSelectAliasField("m","PROPRIETARIO", "propmsg");
					sqlQueryObject.addSelectAliasField("m","TIPO" , "tipomsg");
					
					// Ottimizzazione per partizionamento
					if(verificaPresenzaMessaggiDaRispedire) {
						if(dataMinima!=null) {
							sqlQueryObject.addWhereCondition("m.ORA_REGISTRAZIONE>=?");
							sqlQueryObject.addWhereCondition("sa.ORA_REGISTRAZIONE>=?");
						}
					}
					else {
						sqlQueryObject.addWhereCondition("m.ORA_REGISTRAZIONE>=?");
						sqlQueryObject.addWhereCondition("sa.ORA_REGISTRAZIONE>=?");
					}
					
					// join
					sqlQueryObject.addWhereCondition("m.ID_MESSAGGIO=sa.ID_MESSAGGIO");
					
					sqlQueryObject.addWhereCondition("m.TIPO=?");
					sqlQueryObject.addWhereCondition("m.PROPRIETARIO=?");
					sqlQueryObject.addWhereCondition("sa.TIPO_CONSEGNA=?");
					sqlQueryObject.addWhereCondition("sa.ATTESA_ESITO=?");
					sqlQueryObject.addWhereCondition("sa.CODA=?");
					if(serviziApplicativiPrioritari!=null && serviziApplicativiPrioritari.length>0) {
						if(serviziApplicativiPrioritari.length==1) {
							sqlQueryObject.addWhereCondition("sa.SERVIZIO_APPLICATIVO=?");
						}
						else {
							sqlQueryObject.addWhereINCondition("sa.SERVIZIO_APPLICATIVO", true, serviziApplicativiPrioritari);
						}
					}
					else if(priorita!=null) {
						sqlQueryObject.addWhereCondition("sa.PRIORITA=?");
					}
					if(verificaPresenzaMessaggiDaRispedire) {
						sqlQueryObject.addWhereCondition("sa.ERRORE_PROCESSAMENTO_COMPACT is not null"); // per non intralciare con la "prima" consegna
					}
					else {
						sqlQueryObject.addWhereCondition("sa.ERRORE_PROCESSAMENTO_COMPACT = ?"); // "prima" consegna
					}
					sqlQueryObject.addWhereCondition("sa.RISPEDIZIONE<=?");
					sqlQueryObject.addWhereCondition(false, "sa.LOCK_CONSEGNA is null", "sa.LOCK_CONSEGNA < ?");
					
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addOrderBy("oramsg");
					sqlQueryObject.setSortType(true);
					
					sqlQueryObject.setLimit(limit);
					queryString = sqlQueryObject.createSQLQuery();
				}
				//System.out.println("QUERY MESSAGGI ELIMINATI IS: ["+queryString+"]  1["+idModuloCleaner+"]  2["+tipo+"]");

				//log.debug("Query: "+query);
				pstmtMsgDaSpedire = connectionDB.prepareStatement(queryString);
				int index = 1;
				List<Object> params = new ArrayList<Object>();
				
				// Ottimizzazione per partizionamento
				if(verificaPresenzaMessaggiDaRispedire) {
					if(dataMinima!=null) {
						pstmtMsgDaSpedire.setTimestamp(index++, dataMinima);
						pstmtMsgDaSpedire.setTimestamp(index++, dataMinima);
						params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(dataMinima));
						params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(dataMinima));
					}
				}
				else {
					Timestamp now = new Timestamp(DateManager.getTimeMillis()-(1000*60*60*24)); // i messaggi presi in carico possono rimanere al massimo indietro di un giorno, senno rientrano in quelli da rispedire
					pstmtMsgDaSpedire.setTimestamp(index++, now);
					pstmtMsgDaSpedire.setTimestamp(index++, now);
					params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(now));
					params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(now));
				}
				
				pstmtMsgDaSpedire.setString(index++,tipo); params.add(tipo);
				pstmtMsgDaSpedire.setString(index++,ConsegnaContenutiApplicativi.ID_MODULO); params.add(ConsegnaContenutiApplicativi.ID_MODULO);
				pstmtMsgDaSpedire.setString(index++,GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE); params.add(GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE);
				pstmtMsgDaSpedire.setInt(index++,CostantiDB.FALSE); params.add(CostantiDB.FALSE);
				pstmtMsgDaSpedire.setString(index++,coda); params.add(coda);
				
				if(serviziApplicativiPrioritari!=null && serviziApplicativiPrioritari.length>0) {
					if(serviziApplicativiPrioritari.length==1) {
						pstmtMsgDaSpedire.setString(index++,serviziApplicativiPrioritari[0]);
						params.add(serviziApplicativiPrioritari[0]);
					}
				}
				else if(priorita!=null) {
					pstmtMsgDaSpedire.setString(index++,priorita);
					params.add(priorita);
				}
				
				if(!verificaPresenzaMessaggiDaRispedire) {
					pstmtMsgDaSpedire.setString(index++, TimerConsegnaContenutiApplicativiThread.ID_MODULO);
					params.add(TimerConsegnaContenutiApplicativiThread.ID_MODULO);
				}
				
				pstmtMsgDaSpedire.setTimestamp(index++,new Timestamp(riconsegna.getTime()));
				params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(riconsegna));
				
				pstmtMsgDaSpedire.setTimestamp(index++, new Timestamp(dataRilascioLock.getTime()));
				params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(dataRilascioLock));

				long startDateSQLCommand = DateManager.getTimeMillis();
				String comandoSql = null;
				if(debug) {
					comandoSql = DBUtils.formatSQLString(queryString, params.toArray());
					loggerSql.debug("[QUERY] (Messaggi.daRispedire) ["+comandoSql+"] ...");
				}
				rs = pstmtMsgDaSpedire.executeQuery();
				long endDateSQLCommand = DateManager.getTimeMillis();
				long diffSQLCommand = endDateSQLCommand - startDateSQLCommand;
				if(debug) {
					loggerSql.debug("[QUERY] (Messaggi.daRispedire) ["+comandoSql+"] effettuata in "+Utilities.convertSystemTimeIntoString_millisecondi(diffSQLCommand, true));
				}
				
				int countLimit = 0;
				while(rs.next()){
					MessaggioServizioApplicativo msg = new MessaggioServizioApplicativo();
					msg.setIdTransazione(rs.getString("idtransazione"));
					msg.setIdMessaggio(rs.getString("idmess"));
					msg.setServizioApplicativo(rs.getString("sa"));
					int sbSoap = rs.getInt("sbSoap");
					msg.setSbustamentoSoap(sbSoap==CostantiDB.TRUE);
					int sbInfoProt = rs.getInt("sbProtocol");
					msg.setSbustamentoInformazioniProtocollo(sbInfoProt==CostantiDB.TRUE);
					msg.setNomePorta(rs.getString("nomePorta"));
					
					Timestamp oraRegistrazione = rs.getTimestamp("oramess");
					msg.setOraRegistrazione(oraRegistrazione);
					
					Timestamp dataPresaInConsegna = rs.getTimestamp("saLock");
					msg.setDataPresaInConsegna(dataPresaInConsegna);
					msg.setClusterIdPresaInConsegna(rs.getString("saCluster"));
					int attesaTransazioneCapostipite = rs.getInt("attesa");
					msg.setAttesaEsitoTransazioneCapostipite(attesaTransazioneCapostipite==CostantiDB.TRUE);
					
					idMsg.add(msg);
					// LIMIT Applicativo
					if(Configurazione.getSqlQueryObjectType()==null){
						countLimit++;
						if(countLimit==limit)
							break;
					}
				}
				rs.close(); rs = null;
				pstmtMsgDaSpedire.close(); pstmtMsgDaSpedire=null;

				return idMsg;

			} catch(Exception e) {
				String errorMsg = "[GestoreMessaggi.readMessaggiDaRiconsegnareIntoBox] errore, queryString["+queryString+"]: "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtMsgDaSpedire != null)
						pstmtMsgDaSpedire.close();
				} catch(Exception er) { }
				loggerSql.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
			// else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			// NOP stateful only
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}

	}
	
	public MessaggioServizioApplicativo readInfoDestinatario(String applicativo, boolean debug, Logger log)throws GestoreMessaggiException{
		List<MessaggioServizioApplicativo> l = _readInfoDestinatari(debug, log, applicativo);
		if(l!=null && !l.isEmpty()) {
			if(l.size()==1) {
				return l.get(0);
			}
			else {
				throw new GestoreMessaggiException("More than one result ("+l.size()+")");
			}
		}
		return null;
	}
	public List<MessaggioServizioApplicativo> readInfoDestinatari(boolean debug, Logger log)throws GestoreMessaggiException{
		return _readInfoDestinatari(debug, log, null);
	}
	private List<MessaggioServizioApplicativo> _readInfoDestinatari(boolean debug, Logger log, String applicativo)throws GestoreMessaggiException{

		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta())
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();

			List<MessaggioServizioApplicativo> idMsg = new ArrayList<MessaggioServizioApplicativo>();

			PreparedStatement pstmtMsg = null;
			ResultSet rs = null;
			String queryString = null;
			String tipo = null;
			try{   
				// tipo
				tipo = Costanti.INBOX;

				// Query per Ricerca messaggi eliminati (proprietario:EliminatoreMesaggi)
				if(Configurazione.getSqlQueryObjectType()==null){
					StringBuilder query = new StringBuilder();
					query.append("SELECT "+GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO as idmess, ");
					query.append(" "+GestoreMessaggi.MESSAGGI+".id_transazione as idtransazione, ");
					query.append(" "+GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE as oramess, ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SERVIZIO_APPLICATIVO as sa, ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SBUSTAMENTO_SOAP as sbSoap, ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SBUSTAMENTO_INFO_PROTOCOL as sbProtocol ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".NOME_PORTA as nomePorta ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".LOCK_CONSEGNA as lock ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".CLUSTER_ID as cluster ");
					query.append(" "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ATTESA_ESITO as attesa ");
					
					query.append(" FROM ");
					query.append(GestoreMessaggi.MESSAGGI);
					query.append(",");
					query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
					
					query.append(" WHERE ");
					
					// join
					query.append(" ");
					query.append(GestoreMessaggi.MESSAGGI);
					query.append(".ID_MESSAGGIO=");
					query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
					query.append(".ID_MESSAGGIO AND ");
					
					query.append(" ").append(GestoreMessaggi.MESSAGGI).append(".ID_MESSAGGIO=? AND ");
					query.append(" ").append(GestoreMessaggi.MESSAGGI).append(".TIPO=?");
					if(applicativo!=null) {
						query.append(" AND ").append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI).append(".SERVIZIO_APPLICATIVO=?");
					}

					queryString = query.toString();
				}else{

					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
					sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI,"m");
					sqlQueryObject.addFromTable(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI,"sa");
					
					sqlQueryObject.addSelectAliasField("m", "ID_MESSAGGIO", "idmess");
					sqlQueryObject.addSelectAliasField("m", "ORA_REGISTRAZIONE", "oramess");
					sqlQueryObject.addSelectAliasField("m", "id_transazione", "idtransazione");
					sqlQueryObject.addSelectAliasField("sa", "SERVIZIO_APPLICATIVO", "sa");
					sqlQueryObject.addSelectAliasField("sa", "SBUSTAMENTO_SOAP", "sbSoap");
					sqlQueryObject.addSelectAliasField("sa", "SBUSTAMENTO_INFO_PROTOCOL", "sbProtocol");
					sqlQueryObject.addSelectAliasField("sa", "NOME_PORTA", "nomePorta");
					sqlQueryObject.addSelectAliasField("sa", "LOCK_CONSEGNA", "lock");
					sqlQueryObject.addSelectAliasField("sa", "CLUSTER_ID", "cluster");
					sqlQueryObject.addSelectAliasField("sa", "ATTESA_ESITO", "attesa");
					sqlQueryObject.addSelectField("m","ORA_REGISTRAZIONE");
					sqlQueryObject.addSelectField("m","PROPRIETARIO");
					sqlQueryObject.addSelectField("m","TIPO");
					
					// join
					sqlQueryObject.addWhereCondition("m.ID_MESSAGGIO=sa.ID_MESSAGGIO");
					
					sqlQueryObject.addWhereCondition("m.ID_MESSAGGIO=?");
					sqlQueryObject.addWhereCondition("m.TIPO=?");
					if(applicativo!=null) {
						sqlQueryObject.addWhereCondition("sa.SERVIZIO_APPLICATIVO=?");
					}
					
					sqlQueryObject.setANDLogicOperator(true);
					
					queryString = sqlQueryObject.createSQLQuery();
				}
				//System.out.println("QUERY MESSAGGI ELIMINATI IS: ["+queryString+"]  1["+idModuloCleaner+"]  2["+tipo+"]");

				//log.debug("Query: "+query);
				pstmtMsg = connectionDB.prepareStatement(queryString);
				int index = 1;
				pstmtMsg.setString(index++,this.idBusta);
				pstmtMsg.setString(index++,tipo);
				if(applicativo!=null) {
					pstmtMsg.setString(index++,applicativo);
				}
				
				String terzoArgomento = "";
				if(applicativo!=null) {
					terzoArgomento = " 3["+applicativo+"]";
				}
				
				long startDateSQLCommand = DateManager.getTimeMillis();
				if(debug)
					log.debug("[QUERY] (Messaggi.readDestinatari) ["+queryString+"] 1["+this.idBusta+"] 2["+tipo+"]"+terzoArgomento+" ...");
				rs = pstmtMsg.executeQuery();
				long endDateSQLCommand = DateManager.getTimeMillis();
				long diffSQLCommand = endDateSQLCommand - startDateSQLCommand;
				if(debug)
					log.debug("[QUERY] (Messaggi.readDestinatari) ["+queryString+"] 1["+this.idBusta+"] 2["+tipo+"]"+terzoArgomento+" effettuata in "+Utilities.convertSystemTimeIntoString_millisecondi(diffSQLCommand, true));

				while(rs.next()){
					MessaggioServizioApplicativo msg = new MessaggioServizioApplicativo();
					msg.setIdTransazione(rs.getString("idtransazione"));
					msg.setIdMessaggio(rs.getString("idmess"));
					msg.setServizioApplicativo(rs.getString("sa"));
					int sbSoap = rs.getInt("sbSoap");
					msg.setSbustamentoSoap(sbSoap==CostantiDB.TRUE);
					int sbInfoProt = rs.getInt("sbProtocol");
					msg.setSbustamentoInformazioniProtocollo(sbInfoProt==CostantiDB.TRUE);
					msg.setNomePorta(rs.getString("nomePorta"));
					
					Timestamp oraRegistrazione = rs.getTimestamp("oramess");
					msg.setOraRegistrazione(oraRegistrazione);
					
					Timestamp dataPresaInConsegna = rs.getTimestamp("lock");
					msg.setDataPresaInConsegna(dataPresaInConsegna);
					msg.setClusterIdPresaInConsegna(rs.getString("cluster"));
					int attesaTransazioneCapostipite = rs.getInt("attesa");
					msg.setAttesaEsitoTransazioneCapostipite(attesaTransazioneCapostipite==CostantiDB.TRUE);
					
					idMsg.add(msg);

				}
				rs.close();
				pstmtMsg.close();

				return idMsg;

			} catch(Exception e) {
				String errorMsg = "[GestoreMessaggi.readDestinatari] errore, queryString["+queryString+"]: "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtMsg != null)
						pstmtMsg.close();
				} catch(Exception er) { }
				log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
			// else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			// NOP stateful only
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}

	}

	public void updateMessaggioPresaInCosegna(String servizioApplicativo, String clusterId,
			boolean debug, RunnableLogger loggerSql) throws GestoreMessaggiException {
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();

			PreparedStatement pstmt = null;
			try{

				Timestamp now = DateManager.getTimestamp();
				
				
				// Costruzione Query Update
				String query = "UPDATE "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" SET LOCK_CONSEGNA=?, CLUSTER_ID=? WHERE ID_MESSAGGIO=? AND SERVIZIO_APPLICATIVO=?";
				//log.debug("Query: "+query);
				pstmt= connectionDB.prepareStatement(query);
				int index = 1;
				pstmt.setTimestamp(index++,now);
				pstmt.setString(index++,clusterId);
				pstmt.setString(index++,this.idBusta);
				pstmt.setString(index++,servizioApplicativo);

				int row = pstmt.executeUpdate();
				if(debug) {
					loggerSql.debug("(rows update: "+row+") UPDATE MSG_SERVIZI_APPLICATIVI SET LOCK_CONSEGNA='"+DateUtils.getSimpleDateFormatMs().format(now)+"', CLUSTER_ID='"+cluster_id+"' WHERE ID_MESSAGGIO='"+this.idBusta+"' AND SERVIZIO_APPLICATIVO='"+servizioApplicativo+"'");
				}
				
				pstmt.close();

			} catch(Exception e) {
				try{
					if( pstmt != null  )
						pstmt.close();
				} catch(Exception er) {}
				String msgError = "ERROR updateMessaggioPresaInCosegna: "+e.getMessage();
				loggerSql.error(msgError,e);
				throw new GestoreMessaggiException(msgError,e);
			}

		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}
	
	public void releaseMessaggioPresaInCosegna(String servizioApplicativo, String clusterId,
			boolean debug, RunnableLogger loggerSql) throws GestoreMessaggiException {
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();

			PreparedStatement pstmt = null;
			try{

				// Costruzione Query Update
				String query = "UPDATE "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" SET LOCK_CONSEGNA=?, CLUSTER_ID=? WHERE ID_MESSAGGIO=? AND SERVIZIO_APPLICATIVO=?";
				//log.debug("Query: "+query);
				pstmt= connectionDB.prepareStatement(query);
				int index = 1;
				pstmt.setTimestamp(index++,null);
				pstmt.setString(index++,null);
				pstmt.setString(index++,this.idBusta);
				pstmt.setString(index++,servizioApplicativo);

				int row = pstmt.executeUpdate();
				if(debug) {
					loggerSql.debug("(rows update: "+row+") UPDATE MSG_SERVIZI_APPLICATIVI SET LOCK_CONSEGNA=null, CLUSTER_ID=null WHERE ID_MESSAGGIO='"+this.idBusta+"' AND SERVIZIO_APPLICATIVO='"+servizioApplicativo+"'");
				}
				
				pstmt.close();

			} catch(Exception e) {
				try{
					if( pstmt != null  )
						pstmt.close();
				} catch(Exception er) {}
				String msgError = "ERROR releaseMessaggioPresaInCosegna: "+e.getMessage();
				loggerSql.error(msgError,e);
				throw new GestoreMessaggiException(msgError,e);
			}

		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}
	
	public void releaseMessaggiPresaInCosegna(String queue, String clusterId,
			boolean debug, RunnableLogger loggerSql) throws GestoreMessaggiException {
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();

			PreparedStatement pstmt = null;
			try{

				// Costruzione Query Update
				String condition = (clusterId==null || "".equals(clusterId)) ? "CLUSTER_ID is null" : " CLUSTER_ID=?";
				String query = "UPDATE "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" SET LOCK_CONSEGNA=?, CLUSTER_ID=? WHERE "+condition+" AND CODA=? AND LOCK_CONSEGNA is not null";
				//log.debug("Query: "+query);
				pstmt= connectionDB.prepareStatement(query);
				int index = 1;
				pstmt.setTimestamp(index++,null);
				pstmt.setString(index++,null);
				if((clusterId!=null && !"".equals(clusterId))) {
					pstmt.setString(index++,clusterId);
				}
				pstmt.setString(index++,queue);

				int row = pstmt.executeUpdate();
				if(debug) {
					loggerSql.debug("(rows update: "+row+") (clusterID: "+clusterId+") UPDATE MSG_SERVIZI_APPLICATIVI SET LOCK_CONSEGNA=null, CLUSTER_ID=null WHERE "+condition+" AND CODA="+queue+" AND LOCK_CONSEGNA is not null");
				}
				
				pstmt.close();

			} catch(Exception e) {
				try{
					if( pstmt != null  )
						pstmt.close();
				} catch(Exception er) {}
				String msgError = "ERROR releaseMessaggioPresaInCosegna: "+e.getMessage();
				loggerSql.error(msgError,e);
				throw new GestoreMessaggiException(msgError,e);
			}

		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	public void releaseAttesaEsiti(boolean debug, Logger log) throws GestoreMessaggiException {
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();

			PreparedStatement pstmt = null;
			try{

				// Costruzione Query Update
				String query = "UPDATE "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" SET ATTESA_ESITO=? WHERE ID_MESSAGGIO=?";
				//log.debug("Query: "+query);
				pstmt= connectionDB.prepareStatement(query);
				int index = 1;
				pstmt.setInt(index++,CostantiDB.FALSE);
				pstmt.setString(index++,this.idBusta);

				int row = pstmt.executeUpdate();
				if(debug) {
					log.debug("(rows update: "+row+") UPDATE MSG_SERVIZI_APPLICATIVI SET ATTESA_ESITO="+CostantiDB.FALSE+" WHERE ID_MESSAGGIO='"+this.idBusta+"'");
				}
				
				pstmt.close();

			} catch(Exception e) {
				try{
					if( pstmt != null  )
						pstmt.close();
				} catch(Exception er) {}
				String msgError = "ERROR releaseMessaggioPresaInCosegna: "+e.getMessage();
				log.error(msgError,e);
				throw new GestoreMessaggiException(msgError,e);
			}

		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}
	



	
	
	/* ------------- UTILITY PER LETTURA MESSAGGI DA ELIMINARE -------------- */
	
	public static Date getOraRegistrazioneMassima(Connection connectionDB , String tipoDatabase, boolean logQuery, Logger logger) throws GestoreMessaggiException{
		return _getOraRegistrazione(false, connectionDB , tipoDatabase, logQuery, logger);
	}
	public static Date getOraRegistrazioneMinima(Connection connectionDB , String tipoDatabase, boolean logQuery, Logger logger) throws GestoreMessaggiException{
		return _getOraRegistrazione(true, connectionDB , tipoDatabase, logQuery, logger);
	}
	private static Date _getOraRegistrazione(boolean min, Connection connectionDB , String tipoDatabase, boolean logQuery, Logger logger) throws GestoreMessaggiException{
	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String queryString = null;
		try{	
		
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			if(min) {
				sqlQueryObject.addSelectMinField(MESSAGGI_COLUMN_ORA_REGISTRAZIONE, "check_data");
			}
			else {
				sqlQueryObject.addSelectMaxField(MESSAGGI_COLUMN_ORA_REGISTRAZIONE, "check_data");
			}
			sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			if(logQuery) {
				logger.debug("Esecuzione query ["+queryString+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
			}
			
			pstmt = connectionDB.prepareStatement(queryString);
			rs = pstmt.executeQuery();
			
			if(logQuery) {
				logger.debug("Esecuzione query ["+queryString+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata");
			}
			
			if(rs.next()) {
				return rs.getTimestamp("check_data");
			}
			return null;
		
		} catch(Exception e) {
			String errorMsg = "[GestoreMessaggi.getOraRegistrazione] errore, queryString["+queryString+"]: "+e.getMessage();
			throw new GestoreMessaggiException(errorMsg,e);
		}
		finally {
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {}
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {	}
		}
	}
	
	public static int countMessaggiInutiliIntoInBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			Date leftDate, Date rightDate) throws GestoreMessaggiException{
		return _countMessaggiIntoBox(true, connectionDB, tipoDatabase, logQuery, logger, leftDate, rightDate, null);
	}
	public static int countMessaggiInutiliIntoOutBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			Date leftDate, Date rightDate) throws GestoreMessaggiException{
		return _countMessaggiIntoBox(false, connectionDB, tipoDatabase, logQuery, logger, leftDate, rightDate, null);
	}
	public static int countMessaggiScadutiIntoInBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			Date leftDate, Date rightDate,
			long scadenzaMsg) throws GestoreMessaggiException{
		return _countMessaggiIntoBox(true, connectionDB, tipoDatabase, logQuery, logger, leftDate, rightDate, scadenzaMsg);
	}
	public static int countMessaggiScadutiIntoOutBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			Date leftDate, Date rightDate,
			long scadenzaMsg) throws GestoreMessaggiException{
		return _countMessaggiIntoBox(false, connectionDB, tipoDatabase, logQuery, logger, leftDate, rightDate, scadenzaMsg);
	}
	private static int _countMessaggiIntoBox(boolean searchIntoInbox, Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			Date leftDate, Date rightDate,
			Long scadenzaMsg) throws GestoreMessaggiException{
		
		String tipo = null; 
		if(searchIntoInbox)
			tipo = Costanti.INBOX;
		else
			tipo = Costanti.OUTBOX;
		
		String idModuloCleaner = TimerGestoreMessaggi.ID_MODULO;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String queryString = null;
		try{	
		
			// Query per Ricerca messaggi scaduti
			// Algoritmo:
			//    if( (now-timeout) > oraRegistrazione )
			//       msgScaduto
			java.sql.Timestamp scandenzaT = null;
			if(scadenzaMsg!=null) {
				long scadenza = DateManager.getTimeMillis() - (scadenzaMsg * 60 * 1000);
				scandenzaT = new java.sql.Timestamp(scadenza);
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addSelectCountField("totale_msg");
			sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI);
			if(leftDate!=null) {
				sqlQueryObject.addWhereCondition(MESSAGGI_COLUMN_ORA_REGISTRAZIONE+">=?");
			}
			if(rightDate!=null) {
				sqlQueryObject.addWhereCondition(MESSAGGI_COLUMN_ORA_REGISTRAZIONE+"<=?");
			}
			if(scadenzaMsg!=null) {
				sqlQueryObject.addWhereCondition("? > "+MESSAGGI_COLUMN_ORA_REGISTRAZIONE);
				sqlQueryObject.addWhereCondition(MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
			}
			else {
				sqlQueryObject.addWhereCondition(MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
				sqlQueryObject.addWhereCondition(MESSAGGI_COLUMN_PROPRIETARIO+"=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();

			pstmt = connectionDB.prepareStatement(queryString);
			int index = 1;
			List<Object> objects = new ArrayList<Object>();
			if(leftDate!=null) {
				java.sql.Timestamp leftDateT = new java.sql.Timestamp(leftDate.getTime());
				pstmt.setTimestamp(index++, leftDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(leftDateT));
				}
			}
			if(rightDate!=null) {
				java.sql.Timestamp rightDateT = new java.sql.Timestamp(rightDate.getTime());
				pstmt.setTimestamp(index++, rightDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(rightDateT));
				}
			}
			if(scadenzaMsg!=null) {
				pstmt.setTimestamp(index++,scandenzaT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(scandenzaT));
				}
				
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			else {
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
				pstmt.setString(index++,idModuloCleaner);
				if(logQuery) {
					objects.add(idModuloCleaner);
				}
			}
			
			String query = null;
			if(logQuery) {
				query = DBUtils.formatSQLString(queryString, objects.toArray());
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
			}
			rs = pstmt.executeQuery();
			
			int res = 0;
			if(rs.next()) {
				res = rs.getInt("totale_msg");
			}
			
			if(logQuery) {
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata; trovati "+res+" risultati");
			}
			
			return res;
		
		} catch(Exception e) {
			String errorMsg = "[GestoreMessaggi.countMessaggiIntoBox] errore, queryString["+queryString+"]: "+e.getMessage();
			throw new GestoreMessaggiException(errorMsg,e);
		}
		finally {
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {}
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {	}
		}
	}
	
	public static SortedMap<Integer> deleteMessaggiInutiliIntoInBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			Date leftDate, Date rightDate, IGestoreRepository repository) throws GestoreMessaggiException{
		return _deleteMessaggiIntoBox(true, connectionDB, tipoDatabase, logQuery, logger, leftDate, rightDate, null, repository);
	}
	public static SortedMap<Integer> deleteMessaggiInutiliIntoOutBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			Date leftDate, Date rightDate, IGestoreRepository repository) throws GestoreMessaggiException{
		return _deleteMessaggiIntoBox(false, connectionDB, tipoDatabase, logQuery, logger, leftDate, rightDate, null, repository);
	}
	public static SortedMap<Integer> deleteMessaggiScadutiIntoInBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			Date leftDate, Date rightDate,
			long scadenzaMsg, IGestoreRepository repository) throws GestoreMessaggiException{
		return _deleteMessaggiIntoBox(true, connectionDB, tipoDatabase, logQuery, logger, leftDate, rightDate, scadenzaMsg, repository);
	}
	public static SortedMap<Integer> deleteMessaggiScadutiIntoOutBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			Date leftDate, Date rightDate,
			long scadenzaMsg, IGestoreRepository repository) throws GestoreMessaggiException{
		return _deleteMessaggiIntoBox(false, connectionDB, tipoDatabase, logQuery, logger, leftDate, rightDate, scadenzaMsg, repository);
	}
	private static SortedMap<Integer> _deleteMessaggiIntoBox(boolean searchIntoInbox, Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			Date leftDate, Date rightDate,
			Long scadenzaMsg, IGestoreRepository repository) throws GestoreMessaggiException{
		
		SortedMap<Integer> mapTabelleRigheEliminate = new SortedMap<Integer>();
		
		String tipo = null; 
		if(searchIntoInbox)
			tipo = Costanti.INBOX;
		else
			tipo = Costanti.OUTBOX;
		
		String idModuloCleaner = TimerGestoreMessaggi.ID_MODULO;
		
		PreparedStatement pstmt = null;
		String deleteString = null;
		try{	
		
			// Query per Ricerca messaggi scaduti
			// Algoritmo:
			//    if( (now-timeout) > oraRegistrazione )
			//       msgScaduto
			java.sql.Timestamp scandenzaT = null;
			if(scadenzaMsg!=null) {
				long scadenza = DateManager.getTimeMillis() - (scadenzaMsg * 60 * 1000);
				scandenzaT = new java.sql.Timestamp(scadenza);
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addSelectField(MESSAGGI_COLUMN_ID_MESSAGGIO);
			sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI);
			if(leftDate!=null) {
				sqlQueryObject.addWhereCondition(MESSAGGI_COLUMN_ORA_REGISTRAZIONE+">=?");
			}
			if(rightDate!=null) {
				sqlQueryObject.addWhereCondition(MESSAGGI_COLUMN_ORA_REGISTRAZIONE+"<=?");
			}
			if(scadenzaMsg!=null) {
				sqlQueryObject.addWhereCondition("? > "+MESSAGGI_COLUMN_ORA_REGISTRAZIONE);
				sqlQueryObject.addWhereCondition(MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
			}
			else {
				sqlQueryObject.addWhereCondition(MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
				sqlQueryObject.addWhereCondition(MESSAGGI_COLUMN_PROPRIETARIO+"=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			
			
			// **** Prima prova ad eliminare eventuali SIL rimasti appesi al messaggio, se il messaggio e' di tipo INBOX ****
			if(searchIntoInbox){
				
				ISQLQueryObject sqlQueryObjectMsgServiziApplicativi = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
				sqlQueryObjectMsgServiziApplicativi.addDeleteTable(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
				sqlQueryObjectMsgServiziApplicativi.addWhereINSelectSQLCondition(false, MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO, sqlQueryObject);
				sqlQueryObjectMsgServiziApplicativi.setANDLogicOperator(true);
				
				deleteString = sqlQueryObjectMsgServiziApplicativi.createSQLDelete();
				pstmt = connectionDB.prepareStatement(deleteString);
				int index = 1;
				List<Object> objects = new ArrayList<Object>();
				if(leftDate!=null) {
					java.sql.Timestamp leftDateT = new java.sql.Timestamp(leftDate.getTime());
					pstmt.setTimestamp(index++, leftDateT);
					if(logQuery) {
						objects.add(DateUtils.getSimpleDateFormatMs().format(leftDate));
					}
				}
				if(rightDate!=null) {
					java.sql.Timestamp rightDateT = new java.sql.Timestamp(rightDate.getTime());
					pstmt.setTimestamp(index++, rightDateT);
					if(logQuery) {
						objects.add(DateUtils.getSimpleDateFormatMs().format(rightDate));
					}
				}
				if(scadenzaMsg!=null) {
					pstmt.setTimestamp(index++,scandenzaT);
					if(logQuery) {
						objects.add(DateUtils.getSimpleDateFormatMs().format(scandenzaT));
					}
					
					pstmt.setString(index++,tipo);
					if(logQuery) {
						objects.add(tipo);
					}
				}
				else {
					pstmt.setString(index++,tipo);
					if(logQuery) {
						objects.add(tipo);
					}
					pstmt.setString(index++,idModuloCleaner);
					if(logQuery) {
						objects.add(idModuloCleaner);
					}
				}
				
				String query = null;
				if(logQuery) {
					query = DBUtils.formatSQLString(deleteString, objects.toArray());
					logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
				}
				int result = pstmt.executeUpdate();
				pstmt.close();
				pstmt = null;
				
				if(logQuery) {
					logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata; "+result+" righe eliminate");
				}
				mapTabelleRigheEliminate.add(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI, result);
			}
			
			
			// **** Prova poi ad eliminare il messaggio su DB ****
			
			ISQLQueryObject sqlQueryObjectDefinizioneMessaggi = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectDefinizioneMessaggi.addDeleteTable(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
			sqlQueryObjectDefinizioneMessaggi.addWhereCondition(DEFINIZIONE_MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
			sqlQueryObjectDefinizioneMessaggi.addWhereINSelectSQLCondition(false, DEFINIZIONE_MESSAGGI_COLUMN_ID_MESSAGGIO, sqlQueryObject);
			sqlQueryObjectDefinizioneMessaggi.setANDLogicOperator(true);
			
			deleteString = sqlQueryObjectDefinizioneMessaggi.createSQLDelete();
			pstmt = connectionDB.prepareStatement(deleteString);
			int index = 1;
			List<Object> objects = new ArrayList<Object>();
			pstmt.setString(index++,tipo);
			if(logQuery) {
				objects.add(tipo);
			}
			if(leftDate!=null) {
				java.sql.Timestamp leftDateT = new java.sql.Timestamp(leftDate.getTime());
				pstmt.setTimestamp(index++, leftDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(leftDate));
				}
			}
			if(rightDate!=null) {
				java.sql.Timestamp rightDateT = new java.sql.Timestamp(rightDate.getTime());
				pstmt.setTimestamp(index++, rightDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(rightDate));
				}
			}
			if(scadenzaMsg!=null) {
				pstmt.setTimestamp(index++,scandenzaT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(scandenzaT));
				}
				
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			else {
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
				pstmt.setString(index++,idModuloCleaner);
				if(logQuery) {
					objects.add(idModuloCleaner);
				}
			}
			
			String query = null;
			if(logQuery) {
				query = DBUtils.formatSQLString(deleteString, objects.toArray());
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
			}
			int result = pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			
			if(logQuery) {
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata; "+result+" righe eliminate");
			}
			mapTabelleRigheEliminate.add(GestoreMessaggi.DEFINIZIONE_MESSAGGI, result);
			
			
			
			if(repository!=null) {
				
				// **** Eliminazione buste (non possiedono l'accesso da history o l'accesso da profilo) ****
				
				
				// lista 
				ISQLQueryObject sqlQueryObjectRepositoryBusteJoin = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
				sqlQueryObjectRepositoryBusteJoin.addSelectField(org.openspcoop2.protocol.engine.constants.Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO);
				sqlQueryObjectRepositoryBusteJoin.addFromTable(org.openspcoop2.protocol.engine.constants.Costanti.REPOSITORY);
				sqlQueryObjectRepositoryBusteJoin.addWhereCondition(org.openspcoop2.protocol.engine.constants.Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
				sqlQueryObjectRepositoryBusteJoin.addWhereINSelectSQLCondition(false, org.openspcoop2.protocol.engine.constants.Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO, sqlQueryObject);
				sqlQueryObjectRepositoryBusteJoin.addWhereCondition(false, repository.createSQLCondition_enableOnlyPdd(), repository.createSQLCondition_disabledAll());
				sqlQueryObjectRepositoryBusteJoin.setANDLogicOperator(true);
				
				_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_RISCONTRI, 
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_RISCONTRI_COLUMN_TIPO_MESSAGGIO, 
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_RISCONTRI_COLUMN_ID_MESSAGGIO,
						tipo, leftDate, rightDate, scandenzaT, idModuloCleaner, sqlQueryObjectRepositoryBusteJoin,
						mapTabelleRigheEliminate);
				
				_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_TRASMISSIONI, 
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_TRASMISSIONI_COLUMN_TIPO_MESSAGGIO, 
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_TRASMISSIONI_COLUMN_ID_MESSAGGIO,
						tipo, leftDate, rightDate, scandenzaT, idModuloCleaner, sqlQueryObjectRepositoryBusteJoin,
						mapTabelleRigheEliminate);
				
				_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_ECCEZIONI, 
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_ECCEZIONI_COLUMN_TIPO_MESSAGGIO, 
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_ECCEZIONI_COLUMN_ID_MESSAGGIO,
						tipo, leftDate, rightDate, scandenzaT, idModuloCleaner, sqlQueryObjectRepositoryBusteJoin,
						mapTabelleRigheEliminate);
				
				_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_EXT_INFO, 
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_EXT_INFO_COLUMN_TIPO_MESSAGGIO, 
						org.openspcoop2.protocol.engine.constants.Costanti.LISTA_EXT_INFO_COLUMN_ID_MESSAGGIO,
						tipo, leftDate, rightDate, scandenzaT, idModuloCleaner, sqlQueryObjectRepositoryBusteJoin,
						mapTabelleRigheEliminate);
				
				_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
						org.openspcoop2.protocol.engine.constants.Costanti.PROFILO_ASINCRONO, 
						org.openspcoop2.protocol.engine.constants.Costanti.PROFILO_ASINCRONO_COLUMN_TIPO_MESSAGGIO, 
						org.openspcoop2.protocol.engine.constants.Costanti.PROFILO_ASINCRONO_COLUMN_ID_MESSAGGIO,
						tipo, leftDate, rightDate, scandenzaT, idModuloCleaner, sqlQueryObjectRepositoryBusteJoin,
						mapTabelleRigheEliminate);
				
				if(!searchIntoInbox){
					_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
							org.openspcoop2.protocol.engine.constants.Costanti.RISCONTRI_DA_RICEVERE, 
							null, 
							org.openspcoop2.protocol.engine.constants.Costanti.RISCONTRI_COLUMN_ID_MESSAGGIO,
							tipo, leftDate, rightDate, scandenzaT, idModuloCleaner, sqlQueryObjectRepositoryBusteJoin,
							mapTabelleRigheEliminate);
				}
				
				
				// Eliminazione busta reale
				
				ISQLQueryObject sqlQueryObjectRepositoryBuste = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
				sqlQueryObjectRepositoryBuste.addDeleteTable(org.openspcoop2.protocol.engine.constants.Costanti.REPOSITORY);
				sqlQueryObjectRepositoryBuste.addWhereCondition(org.openspcoop2.protocol.engine.constants.Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
				sqlQueryObjectRepositoryBuste.addWhereINSelectSQLCondition(false, org.openspcoop2.protocol.engine.constants.Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO, sqlQueryObject);
				sqlQueryObjectRepositoryBuste.addWhereCondition(false, repository.createSQLCondition_enableOnlyPdd(), repository.createSQLCondition_disabledAll());
				sqlQueryObjectRepositoryBuste.setANDLogicOperator(true);
				
				deleteString = sqlQueryObjectRepositoryBuste.createSQLDelete();
				pstmt = connectionDB.prepareStatement(deleteString);
				index = 1;
				objects = new ArrayList<Object>();
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
				if(leftDate!=null) {
					java.sql.Timestamp leftDateT = new java.sql.Timestamp(leftDate.getTime());
					pstmt.setTimestamp(index++, leftDateT);
					if(logQuery) {
						objects.add(DateUtils.getSimpleDateFormatMs().format(leftDate));
					}
				}
				if(rightDate!=null) {
					java.sql.Timestamp rightDateT = new java.sql.Timestamp(rightDate.getTime());
					pstmt.setTimestamp(index++, rightDateT);
					if(logQuery) {
						objects.add(DateUtils.getSimpleDateFormatMs().format(rightDate));
					}
				}
				if(scadenzaMsg!=null) {
					pstmt.setTimestamp(index++,scandenzaT);
					if(logQuery) {
						objects.add(DateUtils.getSimpleDateFormatMs().format(scandenzaT));
					}
					
					pstmt.setString(index++,tipo);
					if(logQuery) {
						objects.add(tipo);
					}
				}
				else {
					pstmt.setString(index++,tipo);
					if(logQuery) {
						objects.add(tipo);
					}
					pstmt.setString(index++,idModuloCleaner);
					if(logQuery) {
						objects.add(idModuloCleaner);
					}
				}
				
				query = null;
				if(logQuery) {
					query = DBUtils.formatSQLString(deleteString, objects.toArray());
					logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
				}
				result = pstmt.executeUpdate();
				pstmt.close();
				pstmt = null;
				
				if(logQuery) {
					logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata; "+result+" righe eliminate");
				}
				mapTabelleRigheEliminate.add(org.openspcoop2.protocol.engine.constants.Costanti.REPOSITORY, result);
			}
			
			
			
			
			// **** Eliminazione finale ****
			
			ISQLQueryObject sqlQueryObjectMessaggi = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectMessaggi.addDeleteTable(GestoreMessaggi.MESSAGGI);
			if(leftDate!=null) {
				sqlQueryObjectMessaggi.addWhereCondition(MESSAGGI_COLUMN_ORA_REGISTRAZIONE+">=?");
			}
			if(rightDate!=null) {
				sqlQueryObjectMessaggi.addWhereCondition(MESSAGGI_COLUMN_ORA_REGISTRAZIONE+"<=?");
			}
			if(scadenzaMsg!=null) {
				sqlQueryObjectMessaggi.addWhereCondition("? > "+MESSAGGI_COLUMN_ORA_REGISTRAZIONE);
				sqlQueryObjectMessaggi.addWhereCondition(MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
			}
			else {
				sqlQueryObjectMessaggi.addWhereCondition(MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
				sqlQueryObjectMessaggi.addWhereCondition(MESSAGGI_COLUMN_PROPRIETARIO+"=?");
			}
			sqlQueryObjectMessaggi.setANDLogicOperator(true);
			
			deleteString = sqlQueryObjectMessaggi.createSQLDelete();
			pstmt = connectionDB.prepareStatement(deleteString);
			index = 1;
			objects = new ArrayList<Object>();
			if(leftDate!=null) {
				java.sql.Timestamp leftDateT = new java.sql.Timestamp(leftDate.getTime());
				pstmt.setTimestamp(index++, leftDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(leftDate));
				}
			}
			if(rightDate!=null) {
				java.sql.Timestamp rightDateT = new java.sql.Timestamp(rightDate.getTime());
				pstmt.setTimestamp(index++, rightDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(rightDate));
				}
			}
			if(scadenzaMsg!=null) {
				pstmt.setTimestamp(index++,scandenzaT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(scandenzaT));
				}
				
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			else {
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
				pstmt.setString(index++,idModuloCleaner);
				if(logQuery) {
					objects.add(idModuloCleaner);
				}
			}
			
			query = null;
			if(logQuery) {
				query = DBUtils.formatSQLString(deleteString, objects.toArray());
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
			}
			result = pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			
			if(logQuery) {
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata; "+result+" righe eliminate");
			}
			mapTabelleRigheEliminate.add(GestoreMessaggi.MESSAGGI, result);
			
			
			
			return mapTabelleRigheEliminate;
		
		} catch(Exception e) {
			String errorMsg = "[GestoreMessaggi.deleteMessaggiIntoBox] errore, deleteString["+deleteString+"]: "+e.getMessage();
			throw new GestoreMessaggiException(errorMsg,e);
		} finally {
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {	}
		}
	}
	
	
	private static void _deleteListaRepositoryBuste(String tipoDatabase, Connection connectionDB, boolean logQuery, Logger logger,
			String nomeTabella, String nomeColonnaTipoMessaggio, String nomeColonnaIdMessaggio,
			String tipo, Date leftDate, Date rightDate, Timestamp scandenzaT, String idModuloCleaner, ISQLQueryObject sqlQueryObjectRepositoryBusteJoin,
			SortedMap<Integer> mapTabelleRigheEliminate) throws Exception {
		
		// Eliminazione busta reale
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObject.addDeleteTable(nomeTabella);
		if(nomeColonnaTipoMessaggio!=null) {
			sqlQueryObject.addWhereCondition(nomeColonnaTipoMessaggio+"=?");
		}
		sqlQueryObject.addWhereINSelectSQLCondition(false, nomeColonnaIdMessaggio, sqlQueryObjectRepositoryBusteJoin);
		sqlQueryObject.setANDLogicOperator(true);
		
		String deleteString = sqlQueryObject.createSQLDelete();
		PreparedStatement pstmt = null;
		try{	
			pstmt = connectionDB.prepareStatement(deleteString);
			int index = 1;
			List<Object> objects = new ArrayList<Object>();
			if(nomeColonnaTipoMessaggio!=null) {
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			pstmt.setString(index++,tipo); // due volte per via della ricerca ciclica
			if(logQuery) {
				objects.add(tipo);
			}
			if(leftDate!=null) {
				java.sql.Timestamp leftDateT = new java.sql.Timestamp(leftDate.getTime());
				pstmt.setTimestamp(index++, leftDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(leftDate));
				}
			}
			if(rightDate!=null) {
				java.sql.Timestamp rightDateT = new java.sql.Timestamp(rightDate.getTime());
				pstmt.setTimestamp(index++, rightDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(rightDate));
				}
			}
			if(scandenzaT!=null) {
				pstmt.setTimestamp(index++,scandenzaT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(scandenzaT));
				}
				
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			else {
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
				pstmt.setString(index++,idModuloCleaner);
				if(logQuery) {
					objects.add(idModuloCleaner);
				}
			}
			
			String query = null;
			if(logQuery) {
				query = DBUtils.formatSQLString(deleteString, objects.toArray());
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
			}
			int result = pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			
			if(logQuery) {
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata; "+result+" righe eliminate");
			}
			mapTabelleRigheEliminate.add(nomeTabella, result);
		} finally {
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {	}
		}
	}
	
	
	



	/* ------------- LETTURA MESSAGGI DA ELIMINARE -------------- */

	/**
	 * Cerca nella tabella MESSAGGI le entry che possiedono come proprietario l'ID del modulo utilizzato per l'eliminazione.
	 * Ritorna un array di Stringhe contenenti l'ID di messaggi presenti nella cartella INBOX 
	 * che possiedono come proprietario l'ID 
	 * del modulo utilizzato per l'eliminazione (parametro idModuloCleaner)
	 *
	 * @param idModuloCleaner ID del modulo utilizzato per l'eliminazione
	 * @return Nel caso l'operazione ha successo ritorna un List di stringhe, altrimenti null
	 * 
	 */
	public List<String> readMessaggiInutiliIntoInbox(String idModuloCleaner,int limit,boolean logQuery, boolean orderBy,
			Date data)throws GestoreMessaggiException{
		return this.readMessaggiInutiliIntoBox(true,idModuloCleaner,limit,logQuery,orderBy,data);
	}
	/**
	 * Cerca nella tabella MESSAGGI le entry che possiedono come proprietario l'ID del modulo utilizzato per l'eliminazione.
	 * Ritorna un array di Stringhe contenenti l'ID di messaggi presenti nella cartella OUTBOX 
	 * che possiedono come proprietario l'ID 
	 * del modulo utilizzato per l'eliminazione (parametro idModuloCleaner)
	 *
	 * @param idModuloCleaner ID del modulo utilizzato per l'eliminazione
	 * @return Nel caso l'operazione ha successo ritorna un List di stringhe, altrimenti null
	 * 
	 */
	public List<String> readMessaggiInutiliIntoOutbox(String idModuloCleaner,int limit,boolean logQuery, boolean orderBy,
			Date data)throws GestoreMessaggiException{
		return this.readMessaggiInutiliIntoBox(false,idModuloCleaner,limit,logQuery,orderBy,data);
	}

	/**
	 * Cerca nella tabella MESSAGGI le entry che possiedono come proprietario l'ID del modulo utilizzato per l'eliminazione.
	 * Ritorna un array di Stringhe contenenti l'ID di messaggi presenti nella cartella INBOX (se searchIntoInbox=true)
	 * o nella cartella OUTBOX (se searchIntoInbox=false) che possiedono come proprietario l'ID 
	 * del modulo utilizzato per l'eliminazione (parametro idModuloCleaner)
	 *
	 * @param searchIntoInbox Cerca nella cartella INBOX (se true) o nella cartella OUTBOX (se false)
	 * @param idModuloCleaner ID del modulo utilizzato per l'eliminazione
	 * @return Nel caso l'operazione ha successo ritorna un List di stringhe, altrimenti null
	 * 
	 */
	private List<String> readMessaggiInutiliIntoBox(boolean searchIntoInbox,
			String idModuloCleaner,int limit,boolean logQuery, boolean orderBy,
			Date data)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;			
			Connection connectionDB = stateful.getConnectionDB();

			List<String> idMsg = new ArrayList<String>();

			PreparedStatement pstmtMsgEliminati = null;
			ResultSet rs = null;
			String queryString = null;
			try{	
				// tipo
				String tipo = null; 
				if(searchIntoInbox)
					tipo = Costanti.INBOX;
				else
					tipo = Costanti.OUTBOX;

				// Query per Ricerca messaggi eliminati (proprietario:EliminatoreMesaggi)
				if(Configurazione.getSqlQueryObjectType()==null){
					StringBuilder query = new StringBuilder();
					query.append("SELECT ").append(GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO).append(" FROM ");
					query.append(GestoreMessaggi.MESSAGGI);
					query.append(" WHERE ").append(GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO).append("=? AND ").append(GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO).append("=? ");
					if(data!=null) {
						query.append(" AND ").append(GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE).append("<=?");
					}
					queryString = query.toString();
				}else{
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
					sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO);
					sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE);
					sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO);
					sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO);
					sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI);
					sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
					sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"=?");
					if(data!=null) {
						sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE+"<=?");
					}
					sqlQueryObject.setANDLogicOperator(true);
					if(orderBy){
						sqlQueryObject.addOrderBy(GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE);
						sqlQueryObject.setSortType(true);
					}
					sqlQueryObject.setLimit(limit);
					queryString = sqlQueryObject.createSQLQuery();
				}
				//System.out.println("QUERY MESSAGGI ELIMINATI IS: ["+queryString+"]  1["+idModuloCleaner+"]  2["+tipo+"]");

				//log.debug("Query: "+query);
				pstmtMsgEliminati = connectionDB.prepareStatement(queryString);
				pstmtMsgEliminati.setString(1,tipo);
				pstmtMsgEliminati.setString(2,idModuloCleaner);
				java.sql.Timestamp nowT = null;
				if(data!=null) {
					nowT = new java.sql.Timestamp(data.getTime());
					pstmtMsgEliminati.setTimestamp(3,nowT);
				}

				long startDateSQLCommand = DateManager.getTimeMillis();
				if(logQuery)
					this.log.debug("[QUERY] (Messaggi.eliminatiLogicamente) ["+queryString+"] 1["+tipo+"] 2["+idModuloCleaner+"] 3["+nowT+"] ...");
				rs = pstmtMsgEliminati.executeQuery();
				long endDateSQLCommand = DateManager.getTimeMillis();
				long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
				if(logQuery)
					this.log.debug("[QUERY] (Messaggi.eliminatiLogicamente) ["+queryString+"] 1["+tipo+"] 2["+idModuloCleaner+"] 3["+nowT+"] effettuata in "+secondSQLCommand+" secondi");

				int countLimit = 0;
				while(rs.next()){
					idMsg.add(rs.getString("ID_MESSAGGIO"));
					// LIMIT Applicativo
					if(Configurazione.getSqlQueryObjectType()==null){
						countLimit++;
						if(countLimit==limit)
							break;
					}
				}
				rs.close();
				pstmtMsgEliminati.close();

				return idMsg;

			} catch(Exception e) {
				String errorMsg = "[GestoreMessaggi.readMessaggiInutiliIntoBox] errore, queryString["+queryString+"]: "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtMsgEliminati != null)
						pstmtMsgEliminati.close();
				} catch(Exception er) {	}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
			// else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			// NOP stateful only
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}






	/**
	 * Cerca nella tabella MESSAGGI i messaggi che sono nelle cartelle da un intervallo di tempo definito da: <var>timeout<var>
	 *
	 * @param scadenzaMsg Intervallo di scadenza dei messaggi
	 * @return Nel caso l'operazione ha successo ritorna un List di stringhe, altrimenti null
	 * 
	 */
	public List<String> readMessaggiScadutiIntoInbox(long scadenzaMsg,int limit,boolean logQuery, boolean orderBy, Date data)throws GestoreMessaggiException{
		return this.readMessaggiScadutiIntoBox(true,scadenzaMsg,limit,logQuery,orderBy,data);
	}
	/**
	 * Cerca nella tabella MESSAGGI i messaggi che sono nelle cartelle da un intervallo di tempo definito da: <var>timeout<var>
	 *
	 * @param scadenzaMsg Intervallo di scadenza dei messaggi
	 * @return Nel caso l'operazione ha successo ritorna un List di stringhe, altrimenti null
	 * 
	 */
	public List<String> readMessaggiScadutiIntoOutbox(long scadenzaMsg,int limit,boolean logQuery, boolean orderBy, Date data)throws GestoreMessaggiException{
		return this.readMessaggiScadutiIntoBox(false,scadenzaMsg,limit,logQuery,orderBy,data);
	}

	/**
	 * Cerca nella tabella MESSAGGI i messaggi che sono nelle cartelle da un intervallo di tempo definito da: <var>timeout<var>
	 *
	 * @param searchIntoInbox Cerca nella cartella INBOX (se true) o nella cartella OUTBOX (se false)
	 * @param scadenzaMsg Intervallo di scadenza dei messaggi
	 * @return Nel caso l'operazione ha successo ritorna un List di stringhe, altrimenti null
	 * 
	 */
	private List<String> readMessaggiScadutiIntoBox(boolean searchIntoInbox,long scadenzaMsg,int limit,boolean logQuery, boolean orderBy, Date data)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateful.getConnectionDB();
			List<String> idMsg = new ArrayList<String>();

			PreparedStatement pstmtMsgScaduti = null;
			ResultSet rs = null;
			String queryString = null;
			try{	
				// tipo
				String tipo = null; 
				if(searchIntoInbox)
					tipo = Costanti.INBOX;
				else
					tipo = Costanti.OUTBOX;

				// Query per Ricerca messaggi scaduti
				// Algoritmo:
				//    if( (now-timeout) > oraRegistrazione )
				//       msgScaduto
				long scadenza = DateManager.getTimeMillis() - (scadenzaMsg * 60 * 1000);
				java.sql.Timestamp scandenzaT = new java.sql.Timestamp(scadenza);

				// Query per Ricerca messaggi eliminati (proprietario:EliminatoreMesaggi)
				if(Configurazione.getSqlQueryObjectType()==null){
					StringBuilder query = new StringBuilder();
					query.append("SELECT ").append(GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO).append(" FROM ");
					query.append(GestoreMessaggi.MESSAGGI);
					query.append(" WHERE ? > ").append(GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE).append(" AND ").append(GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO).append("=?");
					if(data!=null) {
						query.append(" AND ").append(GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE).append("<=?");
					}
					queryString = query.toString();
				}else{
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
					sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO);
					sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE);
					sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO);
					sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI);
					sqlQueryObject.addWhereCondition("? > "+GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE);
					sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?");
					if(data!=null) {
						sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE+"<=?");
					}
					sqlQueryObject.setANDLogicOperator(true);
					if(orderBy){
						sqlQueryObject.addOrderBy(GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE);
						sqlQueryObject.setSortType(true);
					}
					sqlQueryObject.setLimit(limit);
					queryString = sqlQueryObject.createSQLQuery();
				}
				//System.out.println("QUERY MESSAGGI SCADUTI IS: ["+queryString+"] ["+scandenzaT+"] ["+tipo+"]");

				pstmtMsgScaduti = connectionDB.prepareStatement(queryString);
				pstmtMsgScaduti.setTimestamp(1,scandenzaT);
				pstmtMsgScaduti.setString(2,tipo);
				java.sql.Timestamp nowT = null;
				if(data!=null) {
					nowT = new java.sql.Timestamp(data.getTime());
					pstmtMsgScaduti.setTimestamp(3,nowT);
				}

				long startDateSQLCommand = DateManager.getTimeMillis();
				if(logQuery)
					this.log.debug("[QUERY] (Messaggi.scaduti) ["+queryString+"] 1["+scandenzaT+"] 2["+tipo+"] 3["+nowT+"]...");
				rs = pstmtMsgScaduti.executeQuery();
				long endDateSQLCommand = DateManager.getTimeMillis();
				long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
				if(logQuery)
					this.log.debug("[QUERY] (Messaggi.scaduti) ["+queryString+"] 1["+scandenzaT+"] 2["+tipo+"] 3["+nowT+"] effettuata in "+secondSQLCommand+" secondi");

				int countLimit = 0;
				while(rs.next()){
					idMsg.add(rs.getString("ID_MESSAGGIO"));
					// LIMIT Applicativo
					if(Configurazione.getSqlQueryObjectType()==null){
						countLimit++;
						if(countLimit==limit)
							break;
					}
				}
				rs.close();
				pstmtMsgScaduti.close();

				return idMsg;

			} catch(Exception e) {
				String errorMsg = "[GestoreMessaggi.readMessaggiScadutiIntoBox] errore, queryString["+queryString+"]: "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtMsgScaduti != null)
						pstmtMsgScaduti.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}
		} // else if (this.openspcoopstate instanceof OpenSPCoopStateless){
		// NOP stateful only
		else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}








	public boolean existsServiziApplicativiDestinatariMessaggio()throws GestoreMessaggiException{

		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmtExistsAnotherServiziApplicativi = null;
			ResultSet rs = null;
			String queryString = null;
			try{	
				StringBuilder query = new StringBuilder();
				query.append("SELECT SERVIZIO_APPLICATIVO FROM ");
				query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
				query.append(" WHERE ID_MESSAGGIO=?");
				queryString = query.toString();

				pstmtExistsAnotherServiziApplicativi =  connectionDB.prepareStatement(queryString);
				pstmtExistsAnotherServiziApplicativi.setString(1,this.idBusta);
				rs = pstmtExistsAnotherServiziApplicativi.executeQuery();
				boolean risultato = rs.next();
				rs.close();
				pstmtExistsAnotherServiziApplicativi.close();

				return risultato;

			} catch(Exception e) {
				String errorMsg = "[GestoreMessaggi.existsServiziApplicativiDestinatariMessaggio] errore, queryString["+queryString+"]: "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtExistsAnotherServiziApplicativi != null)
						pstmtExistsAnotherServiziApplicativi.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}	
		}// else if (this.openspcoopstate instanceof OpenSPCoopStateless){
		// NOP stateful only
		else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Cerca nella tabella MESSAGGI i messaggi destinati al servizio RicezioneContenutiApplicativi che sono stati registrati dopo lo scadere
	 * del timeout per il servizio. Ritorna l'idBusta+'@'+nomeServizioApplicativo dove il servizio applicativo e' di tipo connectionReply
	 *
	 * @param timeout Intervallo di timeout del servizio
	 * @return Nel caso l'operazione ha successo ritorna un List di stringhe, altrimenti null
	 * 
	 */
	public List<String> readMsgForRicezioneContenutiApplicativiNonGestiti(long timeout,int limit,boolean logQuery, boolean orderBy)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();

			List<String> idMsg = new ArrayList<String>();

			PreparedStatement pstmtMsgScaduti = null;
			ResultSet rs = null;
			String queryString = null;
			try{	
				// Query per Ricerca messaggi scaduti
				// Algoritmo:
				//    if( (now-timeout) > oraRegistrazione )
				//       msgScaduto
				long scadenza = DateManager.getTimeMillis() - (timeout);
				java.sql.Timestamp scandenzaT = new java.sql.Timestamp(scadenza);
				//System.out.println("TIMEOUT RICEZIONE CONTENUTI APPLICATIVI["+scandenzaT+"]");

				// Query per Ricerca messaggi eliminati (proprietario:EliminatoreMesaggi)
				if(Configurazione.getSqlQueryObjectType()==null){
					StringBuilder query = new StringBuilder();
					query.append("SELECT "+GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO as identificativoBusta,"+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".SERVIZIO_APPLICATIVO FROM ");
					query.append(GestoreMessaggi.MESSAGGI);
					query.append(",");
					query.append(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
					query.append(" WHERE ? > "+GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE AND "
							+GestoreMessaggi.MESSAGGI+".RIFERIMENTO_MSG IS NOT NULL AND "
							+GestoreMessaggi.MESSAGGI+".TIPO=? AND " +
							GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".TIPO_CONSEGNA=? AND "+
							GestoreMessaggi.MESSAGGI+".PROPRIETARIO LIKE '"+RicezioneContenutiApplicativi.ID_MODULO+"%' AND "+
							GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".INTEGRATION_MANAGER=? AND "+
							GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ID_MESSAGGIO ");
					queryString = query.toString();
				}else{
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
					sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI);
					sqlQueryObject.addFromTable(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI);
					sqlQueryObject.addSelectAliasField(GestoreMessaggi.MESSAGGI,"ID_MESSAGGIO","identificativoBusta");
					sqlQueryObject.addSelectField("SERVIZIO_APPLICATIVO");
					sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"ORA_REGISTRAZIONE");
					sqlQueryObject.addSelectField("RIFERIMENTO_MSG");
					sqlQueryObject.addSelectAliasField(GestoreMessaggi.MESSAGGI,"TIPO","TipoMessaggio");
					sqlQueryObject.addSelectField("TIPO_CONSEGNA");
					sqlQueryObject.addSelectField("PROPRIETARIO");
					sqlQueryObject.addSelectField("INTEGRATION_MANAGER");
					sqlQueryObject.addWhereCondition("? > "+GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE");
					sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".RIFERIMENTO_MSG IS NOT NULL");
					sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".TIPO=?");
					sqlQueryObject.addWhereCondition(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".TIPO_CONSEGNA=?");
					sqlQueryObject.addWhereLikeCondition(GestoreMessaggi.MESSAGGI+".PROPRIETARIO", RicezioneContenutiApplicativi.ID_MODULO,true,true);
					sqlQueryObject.addWhereCondition(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".INTEGRATION_MANAGER=?");
					sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+".ID_MESSAGGIO");
					sqlQueryObject.setANDLogicOperator(true);
					if(orderBy){
						sqlQueryObject.addOrderBy(GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE");
						sqlQueryObject.setSortType(true);
					}
					sqlQueryObject.setLimit(limit);
					queryString = sqlQueryObject.createSQLQuery();
				}
				//System.out.println("QUERY MESSAGGI SCADUTI RICEZIONE CONTENUTI APPLICATIVI IS: ["+queryString+"] 1["+scandenzaT+"] 2["+Costanti.INBOX+"] 3["+"ConnectionReply"+"] 4["+0+"]");

				pstmtMsgScaduti =  connectionDB.prepareStatement(queryString);
				pstmtMsgScaduti.setTimestamp(1,scandenzaT);
				pstmtMsgScaduti.setString(2,Costanti.INBOX);
				pstmtMsgScaduti.setString(3,"ConnectionReply");
				pstmtMsgScaduti.setLong(4,0);

				long startDateSQLCommand = DateManager.getTimeMillis();
				if(logQuery)
					this.log.debug("[QUERY] (Messaggi.scadutiPerRicezioneContenutiApplicativi) ["+queryString+"] 1["+scandenzaT+"] 2["+Costanti.INBOX+"] 3["+"ConnectionReply"+"] 4["+0+"]...");
				rs = pstmtMsgScaduti.executeQuery();
				long endDateSQLCommand = DateManager.getTimeMillis();
				long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
				if(logQuery)
					this.log.debug("[QUERY] Messaggi.scadutiPerRicezioneContenutiApplicativi) ["+queryString+"] 1["+scandenzaT+"] 2["+Costanti.INBOX+"] 3["+"ConnectionReply"+"] 4["+0+"] effettuata in "+secondSQLCommand+" secondi");

				int countLimit = 0;
				while(rs.next()){
					idMsg.add(rs.getString("identificativoBusta")+"@"+rs.getString("SERVIZIO_APPLICATIVO"));
					// LIMIT Applicativo
					if(Configurazione.getSqlQueryObjectType()==null){
						countLimit++;
						if(countLimit==limit)
							break;
					}
				}
				rs.close();
				pstmtMsgScaduti.close();

				return idMsg;

			} catch(Exception e) {
				String errorMsg = "[GestoreMessaggi.readMsgForRicezioneContenutiApplicativiNonGestiti] errore, queryString["+queryString+"]: "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtMsgScaduti != null)
						pstmtMsgScaduti.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}	//else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//CHECKME stateful only
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}

	/**
	 * Cerca nella tabella MESSAGGI i messaggi destinati al servizio RicezioneBuste che sono stati registrati dopo lo scadere
	 * del timeout per il servizio. Ritorna l'idBusta
	 *
	 * @param timeout Intervallo di timeout del servizio
	 * @return Nel caso l'operazione ha successo ritorna un List di stringhe, altrimenti null
	 * 
	 */
	public List<String> readMsgForRicezioneBusteNonGestiti(long timeout,int limit,boolean logQuery, boolean orderBy)throws GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;

			Connection connectionDB = stateful.getConnectionDB();

			List<String> idMsg = new ArrayList<String>();

			PreparedStatement pstmtMsgScaduti = null;
			ResultSet rs = null;
			String queryString = null;
			try{	
				// Query per Ricerca messaggi scaduti
				// Algoritmo:
				//    if( (now-timeout) > oraRegistrazione )
				//       msgScaduto
				long scadenza = DateManager.getTimeMillis() - (timeout);
				java.sql.Timestamp scandenzaT = new java.sql.Timestamp(scadenza);
				//System.out.println("TIMEOUT RICEZIONE BUSTE["+scandenzaT+"]");

				// Query per Ricerca messaggi eliminati 
				if(Configurazione.getSqlQueryObjectType()==null){
					StringBuilder query = new StringBuilder();
					query.append("SELECT ID_MESSAGGIO FROM ");
					query.append(GestoreMessaggi.MESSAGGI);
					query.append(" WHERE ? > ORA_REGISTRAZIONE AND "+
							"RIFERIMENTO_MSG IS NOT NULL AND TIPO=? AND PROPRIETARIO LIKE '"+RicezioneBuste.ID_MODULO+"%'" );
					queryString = query.toString();
				}else{
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
					sqlQueryObject.addSelectField("ID_MESSAGGIO");
					sqlQueryObject.addSelectField("RIFERIMENTO_MSG");
					sqlQueryObject.addSelectField("TIPO");
					sqlQueryObject.addSelectField("PROPRIETARIO");
					sqlQueryObject.addSelectField("ORA_REGISTRAZIONE");
					sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI);
					sqlQueryObject.addWhereCondition("? > ORA_REGISTRAZIONE");
					sqlQueryObject.addWhereCondition("RIFERIMENTO_MSG IS NOT NULL");
					sqlQueryObject.addWhereCondition("TIPO=?");
					sqlQueryObject.addWhereLikeCondition("PROPRIETARIO", RicezioneBuste.ID_MODULO,true,true);
					sqlQueryObject.setANDLogicOperator(true);
					if(orderBy){
						sqlQueryObject.addOrderBy("ORA_REGISTRAZIONE");
						sqlQueryObject.setSortType(true);
					}
					sqlQueryObject.setLimit(limit);
					queryString = sqlQueryObject.createSQLQuery();
				}
				//System.out.println("QUERY MESSAGGI SCADUTI RICEZIONE BUSTE IS: ["+queryString+"] 1["+scandenzaT+"] 2["+Costanti.OUTBOX+"]");

				pstmtMsgScaduti = connectionDB.prepareStatement(queryString);
				pstmtMsgScaduti.setTimestamp(1,scandenzaT);
				pstmtMsgScaduti.setString(2,Costanti.OUTBOX);

				long startDateSQLCommand = DateManager.getTimeMillis();
				if(logQuery)
					this.log.debug("[QUERY] (Messaggi.scadutiPerRicezioneBuste) ["+queryString+"] 1["+scandenzaT+"] 2["+Costanti.OUTBOX+"]...");
				rs = pstmtMsgScaduti.executeQuery();
				long endDateSQLCommand = DateManager.getTimeMillis();
				long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
				if(logQuery)
					this.log.debug("[QUERY] Messaggi.scadutiPerRicezioneBuste) ["+queryString+"] 1["+scandenzaT+"] 2["+Costanti.OUTBOX+"] effettuata in "+secondSQLCommand+" secondi");


				int countLimit = 0;
				while(rs.next()){
					idMsg.add(rs.getString("ID_MESSAGGIO"));
					// LIMIT Applicativo
					if(Configurazione.getSqlQueryObjectType()==null){
						countLimit++;
						if(countLimit==limit)
							break;
					}
				}
				rs.close();
				pstmtMsgScaduti.close();

				return idMsg;

			} catch(Exception e) {
				String errorMsg = "[GestoreMessaggi.readMsgForRicezioneBusteNonGestiti] errore, queryString["+queryString+"]: "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmtMsgScaduti != null)
						pstmtMsgScaduti.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}	
		}//else if (this.openspcoopstate instanceof OpenSPCoopStateless){
		//CHECKME stateful only
		else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}
















	/**
	 * Ritorna le buste salvate scadute o inutilizzate (tutti gli accessi uguale a 0) nella INBOX
	 *
	 * @return List di stringhe contenenti gli ID delle buste scadute e/o inutilizzate con il tipo passato come parametro.
	 * @throws GestoreMessaggiException 
	 * 
	 */
	public List<String> readBusteNonRiferiteDaMessaggiFromInBox(int limit,boolean logQuery,boolean forceIndex,boolean orderBy) throws ProtocolException, GestoreMessaggiException{
		return this.readBusteNonRiferiteDaMessaggi(Costanti.INBOX,limit,logQuery,forceIndex,orderBy);
	}

	/**
	 * Ritorna le buste salvate scadute o inutilizzate (tutti gli accessi uguale a 0) nella OUTBOX
	 *
	 * @return List di stringhe contenenti gli ID delle buste scadute e/o inutilizzate con il tipo passato come parametro.
	 * @throws GestoreMessaggiException 
	 * 
	 */
	public List<String> readBusteNonRiferiteDaMessaggiFromOutBox(int limit,boolean logQuery,boolean forceIndex,boolean orderBy) throws ProtocolException, GestoreMessaggiException{
		return this.readBusteNonRiferiteDaMessaggi(Costanti.OUTBOX,limit,logQuery,forceIndex,orderBy);
	}

	private List<String> readBusteNonRiferiteDaMessaggi(String tipoBusta,int limit,boolean logQuery,boolean forceIndex,boolean orderBy) throws ProtocolException, GestoreMessaggiException{

		List<String> idBuste = new ArrayList<String>();
		int pdd = 0;
		int profilo = 0;
		int pddProfilo = 0;
		this._engine_ReadBusteNonRiferiteDaMessaggi(tipoBusta, limit, logQuery, forceIndex, orderBy, true, false, idBuste);
		pdd = idBuste.size();
		if(idBuste.size()<limit){
			this._engine_ReadBusteNonRiferiteDaMessaggi(tipoBusta, limit, logQuery, forceIndex, orderBy, false, true, idBuste);
			profilo = idBuste.size() - pdd;
		}
		if(idBuste.size()<limit){
			this._engine_ReadBusteNonRiferiteDaMessaggi(tipoBusta, limit, logQuery, forceIndex, orderBy, true, true, idBuste);
			pddProfilo = idBuste.size() - pdd - profilo;
		}
		if(logQuery){
			this.log.debug("["+tipoBusta+"] Trovati messaggi cosi suddivisi pdd["+pdd+"] profilo["+profilo+"] pddAndProfilo["+pddProfilo+"]");
		}
		return idBuste;

	}


	/**
	 * Ritorna le buste salvate scadute o inutilizzate (tutti gli accessi uguale a 0)
	 *
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta	
	 * @return List di stringhe contenenti gli ID delle buste scadute e/o inutilizzate con il tipo passato come parametro.
	 * @throws GestoreMessaggiException 
	 * 
	 */
	private void _engine_ReadBusteNonRiferiteDaMessaggi(String tipoBusta,int limit,boolean logQuery,boolean forceIndex,boolean orderBy,
			boolean pdd, boolean profilo,List<String> idBuste) throws ProtocolException, GestoreMessaggiException{
		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String queryString = null;
			try{	

				String tipoRovesciato = null;
				if(Costanti.INBOX.equals(tipoBusta))
					tipoRovesciato = Costanti.OUTBOX;
				else
					tipoRovesciato = Costanti.INBOX;

				IGestoreRepository gestoreRepositoryBuste = Configurazione.getGestoreRepositoryBuste();

				// Selezioni i messaggi in RepositoryBuste dove l'idBusta non e' associato 
				//   -- ad un messaggio con stesso tipo e identificativo
				//   -- ad un messaggio che possiede il riferimento msg come l'id della busta e con tipo invertito
				// e (dove Pdd=1 o Profilo=1: questo controllo serve per non fare andare in loopback il timer fino a che la busta non e' davvero eliminata 
				//                            (possiede tutti gli accessi a 0))
				// e dove la busta non e' utilizzata per il profilo Asincrono 

				// Gli identificativi ritornati devono essere utilizzati per effettuare il rollback SOLO di pdd e profilo
				// NOTA: non serve fare il rollback dell'history tanto se rimane solo quello, quando la busta e' scaduta viene eliminata.

				if(Configurazione.getSqlQueryObjectType()==null){
					StringBuilder query = new StringBuilder();
					query.append("SELECT ");
					if(forceIndex){
						query.append("/*+ index("+Costanti.REPOSITORY+" "+Costanti.REPOSITORY_INDEX_TIPO_SEARCH+") */");
					}
					query.append(" ID_MESSAGGIO FROM ");
					query.append(Costanti.REPOSITORY);
					query.append(" WHERE ");

					// tipo
					query.append(" TIPO=? AND ");

					// pdd=1 OR profilo=1
					if(pdd && profilo){
						query.append(gestoreRepositoryBuste.createSQLCondition_enableOnlyPddAndProfilo());
					}
					else if(pdd){
						query.append(gestoreRepositoryBuste.createSQLCondition_enableOnlyPdd());
					}
					else if(profilo){ 
						query.append(gestoreRepositoryBuste.createSQLCondition_enableOnlyProfilo());
					}
					else{
						throw new Exception("Utilizzo errato del metodo, almeno una indicazione tra pdd e profilo deve essere fornita");
					}
					query.append(" AND ");

					//					query.append("( ");
					//					query.append(gestoreRepositoryBuste.createSQLCondition_ProfiloCollaborazione(true));
					//					query.append(" OR ");
					//					query.append(gestoreRepositoryBuste.createSQLCondition_PdD(true));
					//					query.append(" ) AND ");

					// asincrono non utilizzato per profilo asincrono
					query.append("(");
					query.append("  NOT EXISTS (");
					query.append("  SELECT * FROM ");
					query.append(Costanti.PROFILO_ASINCRONO);
					query.append("  WHERE ");
					query.append("    ( ");
					query.append(Costanti.PROFILO_ASINCRONO+".ID_MESSAGGIO="+Costanti.REPOSITORY+".ID_MESSAGGIO AND ");
					query.append(Costanti.PROFILO_ASINCRONO+".TIPO="+Costanti.REPOSITORY+".TIPO");
					query.append("    ) ");
					query.append("  )");
					query.append(") AND ");

					// messaggi
					query.append("(");
					query.append(" NOT EXISTS (");
					query.append(" SELECT * FROM ");
					query.append(GestoreMessaggi.MESSAGGI);
					query.append(" WHERE ");
					query.append("( ");
					// Messaggi.idBusta
					query.append(GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+Costanti.REPOSITORY+".ID_MESSAGGIO AND ");
					query.append(GestoreMessaggi.MESSAGGI+".TIPO="+Costanti.REPOSITORY+".TIPO");
					query.append(" ) OR ( ");
					// RiferimentoMessaggi
					query.append(GestoreMessaggi.MESSAGGI+".RIFERIMENTO_MSG="+Costanti.REPOSITORY+".ID_MESSAGGIO AND ");
					query.append(GestoreMessaggi.MESSAGGI+".TIPO=?");
					query.append(" ) ");
					query.append(")");
					query.append(")");

					queryString = query.toString();
				}else{
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
					if(forceIndex){
						sqlQueryObject.addSelectForceIndex(Costanti.REPOSITORY, Costanti.REPOSITORY_INDEX_TIPO_SEARCH);
					}
					sqlQueryObject.addSelectField("ID_MESSAGGIO");
					sqlQueryObject.addSelectField("TIPO");
					sqlQueryObject.addSelectField(gestoreRepositoryBuste.createSQLFields());
					sqlQueryObject.addSelectField("ORA_REGISTRAZIONE");
					sqlQueryObject.addFromTable(Costanti.REPOSITORY);

					// tipo
					sqlQueryObject.addWhereCondition("TIPO=?");

					// pdd=1 OR profilo=1
					if(pdd && profilo){
						sqlQueryObject.addWhereCondition(gestoreRepositoryBuste.createSQLCondition_enableOnlyPddAndProfilo());
					}
					else if(pdd){
						sqlQueryObject.addWhereCondition(gestoreRepositoryBuste.createSQLCondition_enableOnlyPdd());
					}
					else if(profilo){ 
						sqlQueryObject.addWhereCondition(gestoreRepositoryBuste.createSQLCondition_enableOnlyProfilo());
					}
					else{
						throw new Exception("Utilizzo errato del metodo, almeno una indicazione tra pdd e profilo deve essere fornita");
					}
					//					sqlQueryObject.addWhereCondition(false,
					//							gestoreRepositoryBuste.createSQLCondition_ProfiloCollaborazione(true),
					//							gestoreRepositoryBuste.createSQLCondition_PdD(true));

					// asincrono non utilizzato per profilo asincrono
					ISQLQueryObject sqlQueryObjectAsincronoNotExists = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
					sqlQueryObjectAsincronoNotExists.addFromTable(Costanti.PROFILO_ASINCRONO);
					sqlQueryObjectAsincronoNotExists.addWhereCondition(true, Costanti.PROFILO_ASINCRONO+".ID_MESSAGGIO="+Costanti.REPOSITORY+".ID_MESSAGGIO",
							Costanti.PROFILO_ASINCRONO+".TIPO="+Costanti.REPOSITORY+".TIPO");
					sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectAsincronoNotExists);

					// Messaggi
					ISQLQueryObject sqlQueryObjectMessaggiNotExists = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
					sqlQueryObjectMessaggiNotExists.addFromTable(GestoreMessaggi.MESSAGGI);
					sqlQueryObjectMessaggiNotExists.addWhereCondition(true, GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+Costanti.REPOSITORY+".ID_MESSAGGIO",
							GestoreMessaggi.MESSAGGI+".TIPO="+Costanti.REPOSITORY+".TIPO");
					sqlQueryObjectMessaggiNotExists.addWhereCondition(true, GestoreMessaggi.MESSAGGI+".RIFERIMENTO_MSG="+Costanti.REPOSITORY+".ID_MESSAGGIO",
							GestoreMessaggi.MESSAGGI+".TIPO=?");
					sqlQueryObjectMessaggiNotExists.setANDLogicOperator(false);

					sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectMessaggiNotExists);
					sqlQueryObject.setANDLogicOperator(true);
					if(orderBy){
						sqlQueryObject.addOrderBy("ORA_REGISTRAZIONE");
						sqlQueryObject.setSortType(true);
					}
					sqlQueryObject.setLimit(limit);
					queryString = sqlQueryObject.createSQLQuery();
				}

				//System.out.println("STRING QUERY REPOSITORY1 ["+queryString+"] 1["+tipoBusta+"] 2["+tipoRovesciato+"]...");
				pstmt = connectionDB.prepareStatement(queryString);
				pstmt.setString(1,tipoBusta);
				pstmt.setString(2,tipoRovesciato);

				long startDateSQLCommand = DateManager.getTimeMillis();
				if(logQuery)
					this.log.debug("[QUERY] (Messaggi.busteNonRiferite) ["+queryString+"] 1["+tipoBusta+"] 2["+tipoRovesciato+"]...");
				rs = pstmt.executeQuery();
				long endDateSQLCommand = DateManager.getTimeMillis();
				long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
				if(logQuery)
					this.log.debug("[QUERY] (Messaggi.busteNonRiferite) ["+queryString+"] 1["+tipoBusta+"] 2["+tipoRovesciato+"] effettuata in "+secondSQLCommand+" secondi");

				int countLimit = 0;
				//System.out.println("STRING QUERY REPOSITORY1 ["+queryString+"] 1["+tipoBusta+"] 2["+tipoRovesciato+"] effettuata");
				while(rs.next()){
					String id = rs.getString("ID_MESSAGGIO");
					if(idBuste.contains(id)==false){
						idBuste.add(id);
						//System.out.println("TROVATO ["+rs.getString("ID_MESSAGGIO")+"]");
						// LIMIT Applicativo
						if(Configurazione.getSqlQueryObjectType()==null){
							countLimit++;
							if(countLimit==limit)
								break;
						}
					}
				}
				rs.close();
				pstmt.close();



				// Selezioni i messaggi in RepositoryBuste dove l'idBusta non e' associato 
				//   -- ad un messaggio con stesso tipo e identificativo
				//   -- ad un messaggio che possiede il riferimento msg come l'id della busta e con tipo invertito
				// e (dove Pdd=1 o Profilo=1: questo controllo serve per non fare andare in loopback il timer fino a che la busta non e' davvero eliminata 
				//                            (possiede tutti gli accessi a 0))
				// e dove la busta e' utilizzata per il profilo Asincrono con pdd=1 o profilo=1 pero' e' scaduta
				// Gli identificativi ritornati devono essere utilizzati per effettuare il rollback SOLO di pdd e profilo
				// NOTA: non serve fare il rollback dell'history tanto se rimane solo quello, quando la busta e' scaduta viene eliminata.

				if(Configurazione.getSqlQueryObjectType()==null){
					StringBuilder query = new StringBuilder();
					query.append("SELECT ");
					if(forceIndex){
						query.append("/*+ index("+Costanti.REPOSITORY+" "+Costanti.REPOSITORY_INDEX_TIPO_SEARCH+") */");
					}
					query.append(" ID_MESSAGGIO FROM ");
					query.append(Costanti.REPOSITORY);
					query.append(" WHERE ");

					// tipo
					query.append(" TIPO=? AND ");

					// pdd=1 OR profilo=1
					if(pdd && profilo){
						query.append(gestoreRepositoryBuste.createSQLCondition_enableOnlyPddAndProfilo());
					}
					else if(pdd){
						query.append(gestoreRepositoryBuste.createSQLCondition_enableOnlyPdd());
					}
					else if(profilo){ 
						query.append(gestoreRepositoryBuste.createSQLCondition_enableOnlyProfilo());
					}
					else{
						throw new Exception("Utilizzo errato del metodo, almeno una indicazione tra pdd e profilo deve essere fornita");
					}
					query.append(" AND ");
					//					query.append("( ");
					//					query.append(gestoreRepositoryBuste.createSQLCondition_ProfiloCollaborazione(true));
					//					query.append(" OR ");
					//					query.append(gestoreRepositoryBuste.createSQLCondition_PdD(true));
					//					query.append(" ) AND ");

					// asincrono  utilizzato per profilo asincrono ma scaduto
					query.append("(");
					query.append("  EXISTS (");
					query.append("  SELECT * FROM ");
					query.append(Costanti.PROFILO_ASINCRONO);
					query.append("  WHERE ");
					query.append("    ( ");
					query.append(Costanti.PROFILO_ASINCRONO+".ID_MESSAGGIO="+Costanti.REPOSITORY+".ID_MESSAGGIO AND ");
					query.append(Costanti.PROFILO_ASINCRONO+".TIPO="+Costanti.REPOSITORY+".TIPO AND ");
					query.append(Costanti.REPOSITORY+".SCADENZA_BUSTA < ?");
					query.append("    ) ");
					query.append("  )");
					query.append(") AND ");

					// messaggi
					query.append("(");
					query.append(" NOT EXISTS (");
					query.append(" SELECT * FROM ");
					query.append(GestoreMessaggi.MESSAGGI);
					query.append(" WHERE ");
					query.append("( ");
					// Messaggi.idBusta
					query.append(GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+Costanti.REPOSITORY+".ID_MESSAGGIO AND ");
					query.append(GestoreMessaggi.MESSAGGI+".TIPO="+Costanti.REPOSITORY+".TIPO");
					query.append(" ) OR ( ");
					// RiferimentoMessaggi
					query.append(GestoreMessaggi.MESSAGGI+".RIFERIMENTO_MSG="+Costanti.REPOSITORY+".ID_MESSAGGIO AND ");
					query.append(GestoreMessaggi.MESSAGGI+".TIPO=?");
					query.append(" ) ");
					query.append(")");
					query.append(")");

					queryString = query.toString();
				}else{
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
					if(forceIndex){
						sqlQueryObject.addSelectForceIndex(Costanti.REPOSITORY, Costanti.REPOSITORY_INDEX_TIPO_SEARCH);
					}
					sqlQueryObject.addSelectField("ID_MESSAGGIO");
					sqlQueryObject.addSelectField("TIPO");
					sqlQueryObject.addSelectField(gestoreRepositoryBuste.createSQLFields());
					sqlQueryObject.addSelectField("ORA_REGISTRAZIONE");
					sqlQueryObject.addFromTable(Costanti.REPOSITORY);

					// tipo
					sqlQueryObject.addWhereCondition("TIPO=?");

					// pdd=1 OR profilo=1
					if(pdd && profilo){
						sqlQueryObject.addWhereCondition(gestoreRepositoryBuste.createSQLCondition_enableOnlyPddAndProfilo());
					}
					else if(pdd){
						sqlQueryObject.addWhereCondition(gestoreRepositoryBuste.createSQLCondition_enableOnlyPdd());
					}
					else if(profilo){ 
						sqlQueryObject.addWhereCondition(gestoreRepositoryBuste.createSQLCondition_enableOnlyProfilo());
					}
					else{
						throw new Exception("Utilizzo errato del metodo, almeno una indicazione tra pdd e profilo deve essere fornita");
					}
					//					sqlQueryObject.addWhereCondition(false,
					//							gestoreRepositoryBuste.createSQLCondition_ProfiloCollaborazione(true),
					//							gestoreRepositoryBuste.createSQLCondition_PdD(true));

					// asincrono utilizzato per profilo asincrono ma scaduto
					ISQLQueryObject sqlQueryObjectAsincronoExists = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
					sqlQueryObjectAsincronoExists.addFromTable(Costanti.PROFILO_ASINCRONO);
					sqlQueryObjectAsincronoExists.addWhereCondition(Costanti.PROFILO_ASINCRONO+".ID_MESSAGGIO="+Costanti.REPOSITORY+".ID_MESSAGGIO");
					sqlQueryObjectAsincronoExists.addWhereCondition(Costanti.PROFILO_ASINCRONO+".TIPO="+Costanti.REPOSITORY+".TIPO");
					sqlQueryObjectAsincronoExists.addWhereCondition(Costanti.REPOSITORY+".SCADENZA_BUSTA < ?");
					sqlQueryObjectAsincronoExists.setANDLogicOperator(true);
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectAsincronoExists);

					// Messaggi
					ISQLQueryObject sqlQueryObjectMessaggiNotExists = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());	
					sqlQueryObjectMessaggiNotExists.addFromTable(GestoreMessaggi.MESSAGGI);
					sqlQueryObjectMessaggiNotExists.addWhereCondition(true, GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+Costanti.REPOSITORY+".ID_MESSAGGIO",
							GestoreMessaggi.MESSAGGI+".TIPO="+Costanti.REPOSITORY+".TIPO");
					sqlQueryObjectMessaggiNotExists.addWhereCondition(true, GestoreMessaggi.MESSAGGI+".RIFERIMENTO_MSG="+Costanti.REPOSITORY+".ID_MESSAGGIO",
							GestoreMessaggi.MESSAGGI+".TIPO=?");
					sqlQueryObjectMessaggiNotExists.setANDLogicOperator(false);

					sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectMessaggiNotExists);
					sqlQueryObject.setANDLogicOperator(true);
					if(orderBy){
						sqlQueryObject.addOrderBy("ORA_REGISTRAZIONE");
						sqlQueryObject.setSortType(true);
					}
					sqlQueryObject.setLimit(limit);
					queryString = sqlQueryObject.createSQLQuery();
				}

				Timestamp now = DateManager.getTimestamp();
				//System.out.println("STRING QUERY REPOSITORY2 ["+queryString+"] 1["+now+"] 2["+tipoBusta+"] 3["+tipoRovesciato+"] ...");
				pstmt = connectionDB.prepareStatement(queryString);
				pstmt.setString(1,tipoBusta);
				pstmt.setTimestamp(2,now);
				pstmt.setString(3,tipoRovesciato);

				startDateSQLCommand = DateManager.getTimeMillis();
				if(logQuery)
					this.log.debug("[QUERY] (Messaggi.busteNonRiferite_asincrono) ["+queryString+"] 1["+tipoBusta+"] 2["+now+"] 3["+tipoRovesciato+"]...");
				rs = pstmt.executeQuery();
				endDateSQLCommand = DateManager.getTimeMillis();
				secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
				if(logQuery)
					this.log.debug("[QUERY] (Messaggi.busteNonRiferite_asincrono) ["+queryString+"] 1["+tipoBusta+"] 2["+now+"] 3["+tipoRovesciato+"] effettuata in "+secondSQLCommand+" secondi");

				countLimit = 0;
				//System.out.println("STRING QUERY REPOSITORY2 ["+queryString+"] 1["+now+"] 2["+tipoBusta+"] 3["+tipoRovesciato+"] effettuata");
				while(rs.next()){
					String id = rs.getString("ID_MESSAGGIO");
					if(idBuste.contains(id)==false){
						idBuste.add(id);
						//System.out.println("TROVATO ["+rs.getString("ID_MESSAGGIO")+"]");
						// LIMIT Applicativo
						if(Configurazione.getSqlQueryObjectType()==null){
							countLimit++;
							if(countLimit==limit)
								break;
						}
					}
				}
				rs.close();
				pstmt.close();

			} catch(Exception e) {
				String errorMsg = "[RepositoryBuste.getBusteDaEliminare] errore, queryString["+queryString+"]: "+e.getMessage();
				this.log.error(errorMsg,e);
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg,e);
			}
		}//else if (this.openspcoopstate instanceof OpenSPCoopStateless){
		//CHECKME stateful only
		else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}
	}


















	/* ------------- ELIMINAZIONE MESSAGGI -------------- */
	public void logicDeleteMessage()throws GestoreMessaggiException{
		this.aggiornaProprietarioMessaggio_engine(TimerGestoreMessaggi.ID_MODULO, false, true);
	}

	/*
	 * !!! NOTA IL PROBLEMA DESCRITTO DI SEGUITO E' STATO RISOLTO UTILIZZANDO UN ALTRA SOLUZIONE descritta nel metodo deleteMessageByOraRegistrazione !!
	 * 
	 * Fix problem:
	 * I servizi RicezioneBuste e RicezioneContenutiApplicativi ed i Moduli InoltroBuste e InoltroRisposte, quando ricevono un messaggio controllano 
	 * se tale messaggio risulta gia' presente nella base dati.
	 * In particolare va fatta attenzione quando viene rilevata la presenza di tale messaggio con proprietario 'GestoreMessaggi' che rappresenta l'eliminazione logica.
	 * 
	 * Prima della gestione tramite lock poteva succedere che:
	 * - t1 il timer GestoreMessaggi raccoglie le liste dei messaggi da eliminare tra cui l'id ID_A
	 * - t2 il servizio es. RicezioneBuste rileva che l'id ID_A risulta esistente nella base dati con con proprietario 'GestoreMessaggi' e forza l'eliminazione fisica.
	 * - t3 il servizio es. RicezioneBuste continua poi il processamento registrando nuovamente il messaggio con proprietario il modulo successivo, ad es. Sbustamento.
	 * - t4 il servizio es. RicezioneBuste finisce correttamente l'elaborazione del messaggio, magari con presa in carico, avendolo consegnato alla coda ConsegnaContenutiApplicativi.
	 * - t5 il timer GestoreMessaggi al tempo t5 processa effettivamente il messaggio con tale id ID_A e lo elimina. Ma questa eliminazione e' ERRATA poiche il proprietario e' cambiato,
	 *      Tale id era pero' stato identificato al tempo t1.
	 * - t6 Il Modulo ConsegnaContenutiApplicativi viene attivato dal MDB, ma non trovando info su tale ID_A termina subito e quindi non effettua la consegna.
	 * 
	 * Con la gestione tramite lock il bug viene superato.
	 * 
	 * Il Timer GestoreMessaggi acquisisce il lock prima di identificare i messaggi da eliminare.
	 * Il Servizio puntuale, es. RicezioneBuste, acquisisce il lock quando rileva un msg con proprietario 'GestoreMessaggi' (eliminato logicamente) dovendo effettuare una eliminazione fisica forzata per poter proseguire con l'elaborazione.
	 * In questa maniera:
	 * - se il lock viene preso prima dal Timer, questo lo elimina effettivamente. 
	 *   Quando poi il lock viene preso dal servizio puntuale, l'eliminazione non apportera' modifiche effettive sulla basedati (gia eliminato dal timer).
	 *   Pero' poi quando il servizio puntuale ne registra una nuova copia, questo non subira' cancellazioni poiche' il timer ha gia finito l'elaborazione.
	 * - se il lock viene preso prima dal servizio puntuale,  questo lo elimina effettivamente e poi ne creera' eventualmente una nuova versione.
	 *   Quando il lock viene preso dal Timer, tale messaggio non viene rilevato o perche' non esiste proprio sulla base dati o perche' possiede un proprietario diverso da GestoreMessaggi.
	 *
	 * La gestione viene ampliata anche per quanto concerne il repository delle buste.
	 * Il Timer di GestoreRepositoryBuste lo stesso acquisira' il lock prima di esaminare se ci sono buste da eliminare.
	 * Rimane aperta la possibilita' di avere una inconsistenza nei punti (RicezioneBuste,RicezioneContenutiApplicativi) 
	 * in cui deve essere registrato il repository delle buste poiche' viene effettuato il seguente codice:
	 * if(busta.exists)
	 *     busta.update
	 * else
	 *         busta.create
	 * Quindi potrebbe succedere in un caso limite che quando si appresta ad effettuare l'update il timer elimina la busta e il mittente ottiene un errore.
	 * Per risolvere questo problema andrebbe messo in lock anche nel codice sopra riportato, pero' questo comporta un blocco su tutto quel punto.
	 * Comunque anche se avviene questo caso limite, non si ha inconsistenze del database ma solo un errore ritornato al mittente. 
	 * Il lock viene allora utilizzato SOLO se si ha un errore in update. Viene replicato il codice di update stavolta dentro il lock.
	 *  
	 **/

	/**
	 * Elimina il messaggio gestito da OpenSPCoop. 
	 *
	 * Deprecated: using deleteMessageByOraRegistrazione
	 */
	@Deprecated
	public void deleteMessageWithLock(String causa, long attesaAttivaLock, int checkIntervalLock) throws GestoreMessaggiException{
		_deleteMessageWithLock(causa, attesaAttivaLock, checkIntervalLock);
	}
	public void _deleteMessageWithLock(String causa, long attesaAttivaLock, int checkIntervalLock) throws GestoreMessaggiException{
		Connection connectionDB = null;
		if( (this.openspcoopstate instanceof OpenSPCoopStateful) || (this.oneWayVersione11)) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			connectionDB = stateMSG.getConnectionDB();
		}
		
		Semaphore semaphoreDB = null;
		TimerLock timerLock = null;
		try {
			timerLock = new TimerLock(TipoLock._getLockGestioneRepositoryMessaggi());
		}catch(Exception e){
			throw new GestoreMessaggiException(e.getMessage(),e);
		}
		try{
			
			try{
				semaphoreDB = this.getSemaphoreByInstance(timerLock.getIdLock());
			}catch(Exception e){
				throw new GestoreMessaggiException(e.getMessage(),e);
			}
			
			try{
				GestoreMessaggi.acquireLock(semaphoreDB, connectionDB, timerLock, 
						this.msgDiag, causa, attesaAttivaLock, checkIntervalLock);
			}catch(Exception e){
				throw new GestoreMessaggiException(e.getMessage(),e);
			}

			deleteMessage(null);

		}finally{
			try{
				GestoreMessaggi.releaseLock(semaphoreDB, connectionDB, timerLock,
						this.msgDiag, causa);
			}catch(Exception e){}
		}
	}
	/**
	 * Elimina il messaggio gestito da OpenSPCoop. 
	 *
	 * Deprecated: using deleteMessageByOraRegistrazione
	 */
	@Deprecated
	public void deleteMessageWithoutLock() throws GestoreMessaggiException{
		_deleteMessageWithoutLock();
	}
	public void _deleteMessageWithoutLock() throws GestoreMessaggiException{
		deleteMessage(null);
	}
	
	/*
	 * Fix problem:
	 * I servizi RicezioneBuste e RicezioneContenutiApplicativi ed i Moduli InoltroBuste e InoltroRisposte, quando ricevono un messaggio controllano 
	 * se tale messaggio risulta gia' presente nella base dati.
	 * In particolare va fatta attenzione quando viene rilevata la presenza di tale messaggio con proprietario 'GestoreMessaggi' che rappresenta l'eliminazione logica.
	 * 
	 * Prima della gestione tramite lock poteva succedere che:
	 * - t1 il timer GestoreMessaggi raccoglie le liste dei messaggi da eliminare tra cui l'id ID_A
	 * - t2 il servizio es. RicezioneBuste rileva che l'id ID_A risulta esistente nella base dati con con proprietario 'GestoreMessaggi' e forza l'eliminazione fisica.
	 * - t3 il servizio es. RicezioneBuste continua poi il processamento registrando nuovamente il messaggio con proprietario il modulo successivo, ad es. Sbustamento.
	 * - t4 il servizio es. RicezioneBuste finisce correttamente l'elaborazione del messaggio, magari con presa in carico, avendolo consegnato alla coda ConsegnaContenutiApplicativi.
	 * - t5 il timer GestoreMessaggi al tempo t5 processa effettivamente il messaggio con tale id ID_A e lo elimina. Ma questa eliminazione e' ERRATA poiche il proprietario e' cambiato,
	 *      Tale id era pero' stato identificato al tempo t1.
	 * - t6 Il Modulo ConsegnaContenutiApplicativi viene attivato dal MDB, ma non trovando info su tale ID_A termina subito e quindi non effettua la consegna.
	 * 
	 * Con la gestione tramite data il bug viene superato.
	 * 
	 * Il Timer GestoreMessaggi quando avvia l'esecuzione mantiene la DATA_CHECK e acquisisce i messaggi da eliminare la cui ora di registrazione è inferiore alla DATA_CHECK.
	 * Inoltre ogni delete di un messaggio avviene con le istruzioni su ogni tabella che verificano che l'ora di registrazione sia inferiore a DATA_CHECK.
	 * Il Servizio puntuale, es. RicezioneBuste, quando rileva un msg con proprietario 'GestoreMessaggi' (eliminato logicamente) deve effettuare una eliminazione fisica forzata per poter proseguire con l'elaborazione.
	 * In questa maniera:
	 * - se il messaggio rientra nella lista presa prima dal Timer, questo lo elimina effettivamente. 
	 *   Quando poi la "cpu" torna al servizio puntuale, l'eliminazione non apportera' modifiche effettive sulla basedati (gia eliminato dal timer).
	 *   Pero' poi quando il servizio puntuale ne registra una nuova copia, questo non subira' cancellazioni poiche' le eliminazioni del timer verificano che l'ora di registrazione sia inferiore a DATA_CHECK,
	 *   e invece la nuova copia avrà una ora di registrazione successiva.
	 * - se il lock viene preso prima dal servizio puntuale,  questo lo elimina effettivamente e poi ne creera' eventualmente una nuova versione.
	 *   Quando il Timer si attiva, tale messaggio non viene rilevato o perche' non esiste proprio sulla base dati o perche' possiede un proprietario diverso da GestoreMessaggi.
	 *
	 * La gestione viene ampliata anche per quanto concerne il repository delle buste.
	 * Il Timer di GestoreRepositoryBuste lo stesso mantiene una sua DATA_CHECK prima di esaminare se ci sono buste da eliminare.
	 * Inoltre ogni delete di una busta avviene con le istruzioni su ogni tabella che verificano che la data di registrazione sia inferiore a DATA_CHECK.
	 * Rimane aperta la possibilita' di avere una inconsistenza nei punti (RicezioneBuste,RicezioneContenutiApplicativi) 
	 * in cui deve essere registrato il repository delle buste poiche' viene effettuato il seguente codice:
	 * if(busta.exists)
	 *     busta.update
	 * else
	 *     busta.create
	 * Quindi potrebbe succedere in un caso limite che quando si appresta ad effettuare l'update il timer elimina la busta e il mittente ottiene un errore.
	 * Per risolvere questo problema andrebbe utilizzata la soluzione con lock deprecata e descritta in deleteMessageWithLock anche nel codice sopra riportato, pero' questo comporta un blocco su tutto quel punto.
	 * Comunque anche se avviene questo caso limite, non si ha inconsistenze del database ma solo un errore ritornato al mittente. 
	 *  
	 **/
	public void deleteMessageByOraRegistrazione(Date data) throws GestoreMessaggiException{
		deleteMessage(data);
	}
	public void deleteMessageByNow() throws GestoreMessaggiException{
		deleteMessage(DateManager.getDate());
	}
	
	private void deleteMessage(Date data)throws GestoreMessaggiException{
		if( (this.openspcoopstate instanceof OpenSPCoopStateful) || (this.oneWayVersione11)) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			Connection connectionDB = stateMSG.getConnectionDB();
			PreparedStatement pstmtDeleteSIL= null;
			PreparedStatement pstmtDeleteMSG= null;
			try{

				//this.log.debug("DELETE  ID_MESSAGGIO='"+this.idBusta+"' AND TIPO='"+this.tipo+"'");
				java.sql.Timestamp nowT = null;
				if(data!=null) {
					nowT = new java.sql.Timestamp(data.getTime());
				}

				// Prima prova ad eliminare eventuali SIL rimasti appesi al messaggio, se il messaggio e' di tipo INBOX
				if(Costanti.INBOX.equals(this.tipo)){
					String query = "DELETE FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI+" WHERE "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO+"=?";
					if(data!=null) {
						query = query + " AND "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ORA_REGISTRAZIONE+"<=?";
					}
					//log.debug("Query: "+query);
					pstmtDeleteSIL= connectionDB.prepareStatement(query);
					pstmtDeleteSIL.setString(1,this.idBusta);
					if(data!=null) {
						pstmtDeleteSIL.setTimestamp(2, nowT);
					}
					pstmtDeleteSIL.execute();
					pstmtDeleteSIL.close();
				}

				// Prova poi ad eliminare il messaggio su FileSystem/DB
				SavedMessage msgDelete = new SavedMessage(this.idBusta, this.openspcoopstate ,this.tipo,this.workDir,GestoreMessaggi.adapter,this.log); 
				msgDelete.delete(this.isRichiesta,this.oneWayVersione11, nowT);

				// Elimino il messaggio
				String query = "DELETE FROM "+GestoreMessaggi.MESSAGGI+" WHERE "+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+"=? AND "+GestoreMessaggi.MESSAGGI_COLUMN_TIPO_MESSAGGIO+"=?";
				if(data!=null) {
					query = query + " AND "+GestoreMessaggi.MESSAGGI_COLUMN_ORA_REGISTRAZIONE+"<=?";
				}
				//log.debug("Query: "+query);
				pstmtDeleteMSG= connectionDB.prepareStatement(query);
				pstmtDeleteMSG.setString(1,this.idBusta);
				pstmtDeleteMSG.setString(2,this.tipo);
				if(data!=null) {
					pstmtDeleteMSG.setTimestamp(3, nowT);
				}
				pstmtDeleteMSG.execute();
				pstmtDeleteMSG.close();

				// Elimino dalla cache
				GestoreMessaggi.deleteFromCache_Proprietario(TimerGestoreMessaggi.ID_MODULO,this.tipo, this.idBusta);

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, error deleteMessage "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				try{
					if(pstmtDeleteMSG != null)
						pstmtDeleteMSG.close();
				} catch(Exception er) {}
				try{
					if(pstmtDeleteSIL != null)
						pstmtDeleteSIL.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new GestoreMessaggiException(errorMsg,e);
			}	
		}else if (this.openspcoopstate instanceof OpenSPCoopStateless){
			//CHECKME NOP
		}else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}

	}

	/**
	 * Elimina il messaggio gestito da OpenSPCoop dal fileSystem. 
	 *
	 * 
	 */
	public void deleteMessageFromFileSystem(){
		SavedMessage msgDelete = null;
		try{
			//this.log.debug("DELETE FILE SYSTEM ID_MESSAGGIO='"+this.idBusta+"' AND TIPO='"+this.tipo+"'");
			msgDelete = new SavedMessage(this.idBusta, this.openspcoopstate ,this.tipo,this.workDir,GestoreMessaggi.adapter,this.log); 
			msgDelete.deleteMessageFromFileSystem();
		}catch(Exception e){
			String errorMsg = "GESTORE_MESSAGGI, error deleteMessage "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
			this.log.error(errorMsg,e);
		}
	}


	/**
	 * Effettua una validazione sui riscontri del messaggio, e dopodiche' lo elimina.
	 *
	 * 
	 */
	public void validateAndDeleteMsgOneWayRiscontrato() throws GestoreMessaggiException,ProtocolException{

		StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
				: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;

		// Validazione riscontro
		Riscontri gestoreRiscontri = null;

		// Commit JDBC
		try{
			gestoreRiscontri = new Riscontri(stateMSG);
			gestoreRiscontri.validazioneRiscontroRicevuto(this.idBusta);

			// Aggiorno proprietario
			this.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);

			this.openspcoopstate.commit();

		}catch (Exception e) {	
			String errorMsg = "GESTORE_MESSAGGI, error validateAndDeleteMsgRiscontrato "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new GestoreMessaggiException(errorMsg,e);
		}

		// Aggiornamento cache proprietario messaggio
		this.addProprietariIntoCache_readFromTable("GestoreMessaggi", "validateAndDeleteMsgOneWayRiscontrato",null,false);
	}




	/**
	 * Effettua una validazione di una ricevuta asincrona, e dopodiche' elimina la richiesta.
	 *
	 * @param bustaRicevuta Busta della ricevuta
	 * 
	 */
	public void validateAndDeleteMsgAsincronoRiscontrato(Busta bustaRicevuta) throws GestoreMessaggiException,ProtocolException{

		StateMessage state = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
				: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;

		// Validazione ricevuta
		ProfiloDiCollaborazione profiloCollaborazione = null;

		// Commit JDBC
		try{
			profiloCollaborazione = new ProfiloDiCollaborazione(state,this.protocolFactory);
			profiloCollaborazione.asincrono_valdazioneRicevuta(bustaRicevuta);

			// Aggiorno proprietario
			this.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);

			this.openspcoopstate.commit();

		}catch (Exception e) {	
			String errorMsg = "GESTORE_MESSAGGI, error validateAndDeleteMsgRiscontrato "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		

			this.log.error(errorMsg,e);
			throw new GestoreMessaggiException(errorMsg,e);
		}

		// Aggiornamento cache proprietario messaggio
		this.addProprietariIntoCache_readFromTable("GestoreMessaggi", "validateAndDeleteMsgAsincronoRiscontrato",null,false);

	}







	/**
	 * Elimina un messaggio dall'engine di OpenSPCoop. 
	 *
	 * 
	 */
	public boolean forcedDeleteMessage() throws GestoreMessaggiException{

		if(this.openspcoopstate instanceof OpenSPCoopStateful) {
			StatefulMessage stateful = (this.isRichiesta) ? ((StatefulMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StatefulMessage)this.openspcoopstate.getStatoRisposta()) ;

			RollbackRepositoryBuste rollbackMessaggio = null;
			RollbackRepositoryBuste rollbackRepository = null;
			try{	
				//this.log.debug("FORCED DELETE  ID_MESSAGGIO='"+this.idBusta+"' AND TIPO='"+this.tipo+"'");

				if(this.existsMessage_noCache()==false)
					return false;
				// Clean Rollback + JMS
				String rifMsg = this.getRiferimentoMessaggio();
				JMSReceiver receiverJMS = null;
				if(CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(this.propertiesReader.getNodeReceiver())){
					String idT = PdDContext.getValue(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE, this.pddContext);
					String protocol = null;
					if(this.protocolFactory!=null)
						protocol = this.protocolFactory.getProtocol();
					receiverJMS = new JMSReceiver(this.propertiesReader.getIdentitaPortaDefault(protocol),"ForcedDeleteMessage",this.propertiesReader.singleConnection_NodeReceiver(),this.log,idT);
				}
				if(Costanti.INBOX.equals(this.tipo)){
					//	rollback messaggio (eventuale profilo + accesso_pdd)
					rollbackMessaggio = new RollbackRepositoryBuste(this.idBusta, stateful,this.oneWayVersione11);
					rollbackMessaggio.rollbackBustaIntoInBox();
					if(rifMsg==null){
						if(receiverJMS!=null){
							// rollback jms receiver
							String strMessageSelector = "ID = '"+this.idBusta+"'";
							if (receiverJMS.clean(RicezioneBuste.ID_MODULO,strMessageSelector)){
								this.log.info("Trovato messaggio nella coda "+RicezioneBuste.ID_MODULO+" con id ["+this.idBusta+"] non consumato");
							}
						}
					}else{
						// rollback repository
						rollbackRepository = new RollbackRepositoryBuste(rifMsg,stateful,this.oneWayVersione11);
						rollbackRepository.rollbackBustaIntoOutBox(false); // non effettuo il rollback dell'history (riscontro/ricevuta arrivera...)

						if(receiverJMS!=null){
							// rollback jms receiver
							String strMessageSelector = "ID = '"+rifMsg+"'";
							if (receiverJMS.clean(RicezioneContenutiApplicativi.ID_MODULO,strMessageSelector)){
								this.log.info("Trovato messaggio (tramite riferimento) nella coda "+RicezioneContenutiApplicativi.ID_MODULO+" con id ["+rifMsg+"] non consumato");
							}
						}
					}
				}else{
					// rollback messaggio scaduto (eventuale profilo + accesso_pdd)
					rollbackMessaggio = new RollbackRepositoryBuste(this.idBusta, stateful,this.oneWayVersione11);
					rollbackMessaggio.rollbackBustaIntoOutBox();
					if(rifMsg==null){
						if(receiverJMS!=null){
							//	rollback jms receiver
							String strMessageSelector = "ID = '"+this.idBusta+"'";
							if (receiverJMS.clean(RicezioneContenutiApplicativi.ID_MODULO,strMessageSelector)){
								this.log.info("Trovato messaggio nella coda "+RicezioneContenutiApplicativi.ID_MODULO+" con id ["+this.idBusta+"] non consumato");
							}
						}
					}else{
						// rollback repository
						rollbackRepository = new RollbackRepositoryBuste(rifMsg, stateful,this.oneWayVersione11);
						rollbackRepository.rollbackBustaIntoInBox(false); // non effettuo il rollback dell'history (busta e' ricevuta)

						if(receiverJMS!=null){
							//	rollback jms receiver
							String strMessageSelector = "ID = '"+rifMsg+"'";
							if (receiverJMS.clean(RicezioneBuste.ID_MODULO,strMessageSelector)){
								this.log.info("Trovato messaggio (tramite riferimento) nella coda "+RicezioneBuste.ID_MODULO+" con id ["+rifMsg+"] non consumato");
							}
						}
					}
				}
				// Update proprietario messaggio
				this.aggiornaProprietarioMessaggio(TimerGestoreMessaggi.ID_MODULO);


				// esecuzione forced delete
				this.openspcoopstate.commit();

				//	Aggiornamento cache proprietario messaggio
				this.addProprietariIntoCache_readFromTable("GestoreMessaggi", "forcedDeleteMessage",this.getRiferimentoMessaggio(),false);

				return true;

			} catch(Exception e) {
				String errorMsg = "GESTORE_MESSAGGI, Errore forcedDeleteMessage "+this.tipo+"/"+this.idBusta+": "+e.getMessage();		
				this.log.error(errorMsg,e);

				throw new GestoreMessaggiException(errorMsg,e);
			}
		} // else if (this.openspcoopstate instanceof OpenSPCoopStateless){
		// Stateful only	
		else{
			throw new GestoreMessaggiException("Metodo invocato con OpenSPCoopState non valido");
		}

	}







	/** 
	 * Costruisce un Messaggio conforme all'interfaccia di pubblicazione del RepositoryMessaggi 
	 * 
	 * 
	 */
	public OpenSPCoop2Message buildRichiestaPubblicazioneMessaggio_RepositoryMessaggi(IDSoggetto soggettoMittente,String tipoServizio,String servizio,String azione) throws GestoreMessaggiException{

		try{
			String msg = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"+
					"<soapenv:Body>\n"+
					"<pubblicazioneMessaggio xmlns=\"http://gestoreeventi.openspcoop.org\">" +
					"<richiesta xmlns:types=\"http://types.gestoreeventi.openspcoop.org\"><types:idMessaggio>"+this.idBusta+"</types:idMessaggio>"+
					"<types:soggetto><types:nome>"+soggettoMittente.getNome()+"</types:nome><types:tipo>"+soggettoMittente.getTipo()+"</types:tipo></types:soggetto>"+
					"<types:servizio><types:nome>"+servizio+"</types:nome><types:tipo>"+tipoServizio+"</types:tipo></types:servizio>"+
					"<types:tipoEvento>"+azione+"</types:tipoEvento>"+
					"</richiesta></pubblicazioneMessaggio>\n"+
					"</soapenv:Body>\n"+
					"</soapenv:Envelope>";
			OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(MessageType.SOAP_11, MessageRole.REQUEST, 
					MessageUtilities.getDefaultContentType(MessageType.SOAP_11),
					msg.getBytes());
			return pr.getMessage_throwParseException();
		}catch(Exception e){
			String errorMsg = "GESTORE_MESSAGGI, error buildRichiestaPubblicazioneMessaggio_RepositoryMessaggi: "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new GestoreMessaggiException(errorMsg,e);
		}

	}

	/** 
	 * Costruisce un Messaggio conforme all'interfaccia di prelevamento dal RepositoryMessaggi 
	 * 
	 * 
	 */
	public OpenSPCoop2Message buildRispostaPrelevamentoMessaggio_RepositoryMessaggi(byte[] messaggio, MessageType messageType) throws GestoreMessaggiException{

		try{
			String namespace;
			if(MessageType.SOAP_11.equals(messageType)){
				namespace = "http://schemas.xmlsoap.org/soap/envelope/";
			} else {
				namespace = "http://www.w3.org/2003/05/soap-envelope";
			}
			String msgUpper = "<soapenv:Envelope xmlns:soapenv=\"" + namespace + "\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"+
					"<soapenv:Body>\n"+
					"<prelevaMessaggioResponse xmlns=\"http://gestoreeventi.openspcoop.org\">"+
					"<prelevaMessaggioReturn>";
			String msgDown ="</prelevaMessaggioReturn></prelevaMessaggioResponse>\n"+
					"</soapenv:Body>\n"+
					"</soapenv:Envelope>";
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(msgUpper.getBytes());
			bout.write(Base64Utilities.encode(messaggio));
			bout.write(msgDown.getBytes());
			bout.flush();
			bout.close();
			OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(messageType, MessageRole.RESPONSE, 
					MessageUtilities.getDefaultContentType(messageType),
					bout.toByteArray());
			return pr.getMessage_throwParseException();
		}catch(Exception e){
			String errorMsg = "GESTORE_MESSAGGI, error buildRispostaPrelevamentoMessaggio_RepositoryMessaggi: "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new GestoreMessaggiException(errorMsg,e);
		}

	}
	public void setReadyForDrop(boolean readyForDrop) {
		this.readyForDrop = readyForDrop;
	}
	public void setOneWayVersione11(boolean oneWay11) {
		this.oneWayVersione11 = oneWay11;
	}
	public boolean isOneWayVersione11() {
		return this.oneWayVersione11;
	}
	public void setPortaDiTipoStateless(boolean portaDiTipoStateless) {
		this.portaDiTipoStateless = portaDiTipoStateless;
	}
	public void setRoutingStateless(boolean routingStateless) {
		this.routingStateless = routingStateless;
	}
	public boolean isRichiesta() {
		return this.isRichiesta;
	}
	public void setRichiesta(boolean isRichiesta) {
		this.isRichiesta = isRichiesta;
	}


















	/* ------------- LOCK PER GESTIONE DELETE DI MESSAGGI DUPLICATI IN CONCORRENZA CON L'ELIMINAZIONE DEL TIMER -------------- */
	
	private Semaphore getSemaphoreByInstance(String idLock) throws UtilsException {
		if(this.propertiesReader.isTimerLockByDatabase()) {
			try{
				InfoStatistics semaphore_statistics = new InfoStatistics();
						
				SemaphoreConfiguration config = newSemaphoreConfiguration(this.propertiesReader.getTimerGestoreMessaggi_lockMaxLife(), 
						this.propertiesReader.getTimerGestoreMessaggi_lockIdleTime());
				
				TipiDatabase databaseType = TipiDatabase.toEnumConstant(this.propertiesReader.getDatabaseType());
				Semaphore semaphoreDB = new Semaphore(semaphore_statistics, SemaphoreMapping.newInstance(idLock), 
						config, databaseType, this.log);
				return semaphoreDB;
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}
		return null;
	}
	private Connection getConnectionByInstance() {
		Connection connectionDB = null;
		if( (this.openspcoopstate instanceof OpenSPCoopStateful) || (this.oneWayVersione11)) {
			StateMessage stateMSG = (this.isRichiesta) ? ((StateMessage)this.openspcoopstate.getStatoRichiesta()) 
					: ((StateMessage)this.openspcoopstate.getStatoRisposta()) ;
			connectionDB = stateMSG.getConnectionDB();
		}
		return connectionDB;
	}
	
	public static SemaphoreConfiguration newSemaphoreConfiguration(long maxLife,long idleTime) {
		
		OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
		Logger logTimers = OpenSPCoop2Logger.getLoggerOpenSPCoopTimers();
		
		SemaphoreConfiguration config = new SemaphoreConfiguration();
		config.setSerializableTimeWaitMs(propertiesReader.getGestioneSerializableDB_AttesaAttiva());
		config.setSerializableNextIntervalTimeMs(propertiesReader.getGestioneSerializableDB_CheckInterval());
		config.setMaxLife(maxLife);
		config.setMaxIdleTime(idleTime);
		boolean logEvent = propertiesReader.isTimerLockByDatabaseNotifyLogEnabled();
		config.setEmitEvent(logEvent);
		if(logEvent) {
			config.setEventGenerator(new SemaphoreLogEventGenerator(logTimers));
		}
		config.setIdNode(propertiesReader.getClusterId(true));
		config.setSerializableLevel(false); // in modo da non avere il lock esclusivo sulla tabella, cmq le entries sono inizializzate
		
		return config;
	}
	
	private static StringBuilder LOCK = new StringBuilder();
	private static String LOCK_MODULO = null;

	// I Metodi sotto riportati commentati funzionavano bene, ma non garantivano il fair dei lock, 
	// Inoltre alla scadenza del while non veniva lanciata una eccezione che indicava il timeout per l'acquisizione del lock
	// When fairness is set true, the semaphore guarantees that threads invoking any of the acquire methods are selected to obtain permits in the order in which their invocation of those methods was processed (first-in-first-out; FIFO)
	// NOTA: La sincronizzazione non servirebbe. Serve solo per avere maggiori informazioni diagnostiche
	
	// Con l'introduzione di più nodi il problema descritto più sopra nella sezione 'ELIMINAZIONE MESSAGGI'
	// si poteva ripresentare distribuito su più nodi. Per questo motivo l'implementazione del lock è stata distribuita grazie al semaforo implementato su database.
	// Tale semaforo e' stato poi implementato anche per gli altri timer che pero' non necessitano di una semaforizzazione nel caso non esistano piu' di una istanza di PdD
	// Questo comporta che la gestione tramite synchronized con lock viene attuata solamente per il timer di eliminazione messaggi che presentava tale problematica.
	
	private static final int LOCK_NUMBER_OF_PERMITS = 1;
	private static final boolean LOCK_FAIR_FIFO = true;
	private static java.util.concurrent.Semaphore LOCK_SEMAPHORE = new java.util.concurrent.Semaphore(LOCK_NUMBER_OF_PERMITS, LOCK_FAIR_FIFO);
	public static void acquireLock(GestoreMessaggi gestoreMessaggi, TimerLock timerLock,
			MsgDiagnostico msgDiag,String causa,long attesaAttivaLock,int checkIntervalLock) throws InterruptedException, UtilsException, TimerLockNotAvailableException{
		acquireLock(gestoreMessaggi.getSemaphoreByInstance(timerLock.getIdLock()), gestoreMessaggi.getConnectionByInstance(), timerLock, 
				msgDiag, causa, attesaAttivaLock, checkIntervalLock);
	}
	public static void acquireLock(Semaphore semaphoreDB, Connection connectionDB, TimerLock timerLock,
			MsgDiagnostico msgDiag,String causa,long attesaAttivaLock,int checkIntervalLock) throws InterruptedException, UtilsException, TimerLockNotAvailableException{

		msgDiag.addKeyword(CostantiPdD.KEY_LOCK_CAUSALE, causa);
		msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "acquisizioneLock.inCorso");

		if(semaphoreDB!=null) {
			
			if(connectionDB==null) {
				throw new UtilsException("Gestione Lock su db richiede una connessione");
			}
			try {
				if(connectionDB.isClosed()) {
					throw new UtilsException("Gestione Lock su db richiede una connessione attiva");
				}
			}catch(Exception e) {
				throw new UtilsException("Gestione Lock su db richiede una connessione attiva: "+e.getMessage(),e);
			}
			
			// La sleep ci vuole anche prima del primo lock, senno il timer che prende un lock continuera' a prenderlo.
			Utilities.sleep(checkIntervalLock);
			
			boolean lock = semaphoreDB.newLock(connectionDB, causa);
			int i=0;
			while(i<attesaAttivaLock && !lock){
				Utilities.sleep(checkIntervalLock);
				i=i+checkIntervalLock;
				lock = semaphoreDB.newLock(connectionDB, causa);
			}
			if(!lock) {
				String error = "Timeout: lock non disponibile dopo una attesa di "+attesaAttivaLock+"ms";
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, error);
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "acquisizioneLock.nonDisponibile");
				throw new TimerLockNotAvailableException(error);
			}

		}
		else {
		
			if(TipoLock._getLockGestioneRepositoryMessaggi().equals(timerLock.getTipoLock())) {
			
				synchronized (GestoreMessaggi.LOCK) {
		
					// NOTA: puo' comunque succedere che un proprietario non esista, quindi non viene stampato il log con proprietario, pero' due thread in parallelo poi
					//		 chiamano il tryAcquire e quindi solo uno dei due lo prende. L'altro rimane bloccato senza aver emesso il log con precedente proprietario.			
					if(GestoreMessaggi.LOCK.length()>0){
						msgDiag.addKeyword(CostantiPdD.KEY_LOCK_CAUSALE, GestoreMessaggi.LOCK.toString());
						msgDiag.addKeyword(CostantiPdD.KEY_LOCK_ID_MODULO, GestoreMessaggi.LOCK_MODULO);
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "acquisizioneLock.wait.existsOldOwner");
					}
					else{
						msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "acquisizioneLock.wait.withoutOwner");
					}
		
				}
		
				/*
				 *  Acquires a permit from this semaphore, if one becomes available within the given waiting time and the current thread has not been interrupted.
					Acquires a permit, if one is available and returns immediately/
		
					If no permit is available then the current thread becomes disabled for thread scheduling purposes and lies dormant until one of three things happens:
					- Some other thread invokes the release() method for this semaphore and the current thread is next to be assigned a permit; or
					- Some other thread interrupts the current thread; or
					- The specified waiting time elapses. 
		
					If a permit is acquired then the value true is returned.
		
					If the current thread:
					- has its interrupted status set on entry to this method; or
					- is interrupted while waiting to acquire a permit, 
					then InterruptedException is thrown and the current thread's interrupted status is cleared.
		
					If the specified waiting time elapses then the value false is returned. If the time is less than or equal to zero, the method will not wait at all. 
				 */
				boolean lockAcquired = LOCK_SEMAPHORE.tryAcquire(attesaAttivaLock, TimeUnit.MILLISECONDS);
				if(lockAcquired==false){
					throw new InterruptedException("Timeout: lock non disponibile dopo una attesa di "+attesaAttivaLock+"ms (attuale modulo proprietario: "+GestoreMessaggi.LOCK_MODULO+", causa: "+GestoreMessaggi.LOCK.toString()+")");
				}
		
				GestoreMessaggi.LOCK.append(causa);
				GestoreMessaggi.LOCK_MODULO = msgDiag.getFunzione();
				
			}
			
		}

		msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "acquisizioneLock.ok");

	}

	public static void updateLock(GestoreMessaggi gestoreMessaggi, TimerLock timerLock,
			MsgDiagnostico msgDiag,String causa) throws InterruptedException, UtilsException{
		updateLock(gestoreMessaggi.getSemaphoreByInstance(timerLock.getIdLock()), gestoreMessaggi.getConnectionByInstance(), timerLock, msgDiag, causa);
	}
	public static void updateLock(Semaphore semaphoreDB,  Connection connectionDB, TimerLock timerLock,
			MsgDiagnostico msgDiag,String causa) throws InterruptedException, UtilsException{
		
		msgDiag.addKeyword(CostantiPdD.KEY_LOCK_CAUSALE, causa);
		msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "updateLock.inCorso");

		if(semaphoreDB!=null) {
			
			if(connectionDB==null) {
				throw new UtilsException("Gestione Lock su db richiede una connessione");
			}
			try {
				if(connectionDB.isClosed()) {
					throw new UtilsException("Gestione Lock su db richiede una connessione attiva");
				}
			}catch(Exception e) {
				throw new UtilsException("Gestione Lock su db richiede una connessione attiva: "+e.getMessage(),e);
			}
			
			boolean unlock = semaphoreDB.updateLock(connectionDB, causa);
			if(!unlock) {
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "updateLock.ko");
				throw new InterruptedException(msgDiag.getMessaggio_replaceKeywords("all", "updateLock.ko"));
			}
		}
		else {
		
			// nop
			//aggiornamento non previsto con implementazione singleton
			
		}
		msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "updateLock.ok");
		
	}
	
	public static void releaseLock(GestoreMessaggi gestoreMessaggi, TimerLock timerLock,
			MsgDiagnostico msgDiag,String causa) throws InterruptedException, UtilsException{
		releaseLock(gestoreMessaggi.getSemaphoreByInstance(timerLock.getIdLock()), gestoreMessaggi.getConnectionByInstance(), timerLock, msgDiag, causa);
	}
	public static void releaseLock(Semaphore semaphoreDB,  Connection connectionDB, TimerLock timerLock,
			MsgDiagnostico msgDiag,String causa) throws InterruptedException, UtilsException{

		msgDiag.addKeyword(CostantiPdD.KEY_LOCK_CAUSALE, causa);
		msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "acquisizioneUnlock.inCorso");

		if(semaphoreDB!=null) {
			
			if(connectionDB==null) {
				throw new UtilsException("Gestione Lock su db richiede una connessione");
			}
			try {
				if(connectionDB.isClosed()) {
					throw new UtilsException("Gestione Lock su db richiede una connessione attiva");
				}
			}catch(Exception e) {
				throw new UtilsException("Gestione Lock su db richiede una connessione attiva: "+e.getMessage(),e);
			}
			
			boolean unlock = semaphoreDB.releaseLock(connectionDB, causa);
			if(!unlock) {
				msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "acquisizioneUnlock.ko");
				throw new InterruptedException(msgDiag.getMessaggio_replaceKeywords("all", "acquisizioneUnlock.ko"));
			}
		}
		else {
		
			if(TipoLock._getLockGestioneRepositoryMessaggi().equals(timerLock.getTipoLock())) {
			
				// Esiste solo un thread attivo, grazie al semaforo
				// metto il synchronized per garantire il log consistente sopra
				
				if( ! (GestoreMessaggi.LOCK.length()>0) ){
					msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "acquisizioneUnlock.ko");
					throw new InterruptedException(msgDiag.getMessaggio_replaceKeywords("all", "acquisizioneUnlock.ko"));
				}
		
				synchronized (GestoreMessaggi.LOCK) {	
					GestoreMessaggi.LOCK.delete(0, GestoreMessaggi.LOCK.length());
					GestoreMessaggi.LOCK_MODULO = null;
				}
				LOCK_SEMAPHORE.release();
				
			}
			
		}
		msgDiag.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL, "acquisizioneUnlock.ok");

	}

}
