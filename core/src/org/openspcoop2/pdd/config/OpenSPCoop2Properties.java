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



package org.openspcoop2.pdd.config;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.message.AttachmentsProcessingMode;
import org.openspcoop2.message.ForwardConfig;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.monitor.engine.statistic.StatisticsForceIndexConfig;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autorizzazione.container.IAutorizzazioneSecurityContainer;
import org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazionePortaApplicativa;
import org.openspcoop2.pdd.core.controllo_traffico.ConfigurazioneControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.INotify;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.TipoGestorePolicy;
import org.openspcoop2.pdd.core.credenziali.IGestoreCredenziali;
import org.openspcoop2.pdd.core.credenziali.IGestoreCredenzialiIM;
import org.openspcoop2.pdd.core.credenziali.engine.TipoAutenticazioneGestoreCredenziali;
import org.openspcoop2.pdd.core.handlers.ExitHandler;
import org.openspcoop2.pdd.core.handlers.InRequestHandler;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolHandler;
import org.openspcoop2.pdd.core.handlers.InResponseHandler;
import org.openspcoop2.pdd.core.handlers.InitHandler;
import org.openspcoop2.pdd.core.handlers.IntegrationManagerRequestHandler;
import org.openspcoop2.pdd.core.handlers.IntegrationManagerResponseHandler;
import org.openspcoop2.pdd.core.handlers.OutRequestHandler;
import org.openspcoop2.pdd.core.handlers.OutResponseHandler;
import org.openspcoop2.pdd.core.handlers.PostOutRequestHandler;
import org.openspcoop2.pdd.core.handlers.PostOutResponseHandler;
import org.openspcoop2.pdd.core.handlers.PreInRequestHandler;
import org.openspcoop2.pdd.core.handlers.PreInResponseHandler;
import org.openspcoop2.pdd.core.handlers.notifier.INotifierCallback;
import org.openspcoop2.pdd.core.handlers.transazioni.ISalvataggioDiagnosticiManager;
import org.openspcoop2.pdd.core.handlers.transazioni.ISalvataggioTracceManager;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePA;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD;
import org.openspcoop2.pdd.core.integrazione.UtilitiesTemplate;
import org.openspcoop2.pdd.core.node.INodeReceiver;
import org.openspcoop2.pdd.core.node.INodeSender;
import org.openspcoop2.pdd.core.threshold.IThreshold;
import org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.filetrace.FileTraceGovWayState;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.Imbustamento;
import org.openspcoop2.pdd.mdb.ImbustamentoRisposte;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.mdb.Sbustamento;
import org.openspcoop2.pdd.mdb.SbustamentoRisposte;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.core.RicezioneBuste;
import org.openspcoop2.pdd.services.core.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.timers.TimerGestoreBusteNonRiscontrate;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomali;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBuste;
import org.openspcoop2.protocol.engine.FunctionContextCustom;
import org.openspcoop2.protocol.engine.FunctionContextsCustom;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.engine.driver.IFiltroDuplicati;
import org.openspcoop2.protocol.engine.driver.repository.GestoreRepositoryFactory;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.protocol.sdk.BypassMustUnderstandCheck;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.FaultIntegrationGenericInfoMode;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.VersionUtilities;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.date.DateEngineType;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.date.IDate;
import org.openspcoop2.utils.digest.IDigestReader;
import org.openspcoop2.utils.id.IUniqueIdentifierGenerator;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig.ADDITIONAL;
import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.transport.http.RFC2047Encoding;
import org.slf4j.Logger;

/**
 * Contiene un lettore del file di proprieta' di OpenSPCoop.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class OpenSPCoop2Properties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static OpenSPCoop2Properties openspcoopProperties = null;




	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'govway.properties' */
	private OpenSPCoop2InstanceProperties reader;
	private PddProperties pddReader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public OpenSPCoop2Properties(Properties localProperties) throws Exception{

		if(OpenSPCoop2Startup.initialize)
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		else
			this.log = LoggerWrapperFactory.getLogger("govway.startup");

		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = OpenSPCoop2Properties.class.getResourceAsStream("/govway.properties");
			if(properties==null){
				throw new Exception("File '/govway.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'govway.properties': \n\n"+e.getMessage(),e);
			throw new Exception("OpenSPCoopProperties initialize error: "+e.getMessage(),e);
		}finally{
		    try{
		    	if(properties!=null)
		    		properties.close();
		    }catch(Exception er){}
		}
		this.reader = new OpenSPCoop2InstanceProperties(propertiesReader, this.log, localProperties);

	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(Properties localProperties){

		try {
			OpenSPCoop2Properties.openspcoopProperties = new OpenSPCoop2Properties(localProperties);	
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di OpenSPCoopProperties
	 * 
	 */
	public static OpenSPCoop2Properties getInstance(){
		return OpenSPCoop2Properties.openspcoopProperties;
	}


	public static void updatePddPropertiesReader(PddProperties pddProperties){
		OpenSPCoop2Properties.openspcoopProperties.pddReader = pddProperties;
	}












	/* ********  VALIDATORE FILE PROPERTY  ******** */

	/**
	 * Effettua una validazione delle proprieta' impostate nel file OpenSPCoop.properties.   
	 *
	 * @return true se la validazione ha successo, false altrimenti.
	 * 
	 */
	public boolean validaConfigurazione(java.lang.ClassLoader loader) {	
		try{  
			ClassNameProperties className = ClassNameProperties.getInstance();

			// root Directory
			if (getRootDirectory() == null)		
				return false;
			if( (new File(getRootDirectory())).exists() == false ){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.confDirectory'. \n La directory indicata non esiste ["+getRootDirectory()+"].");
				return false;
			}
			
			// Tipo server
			Boolean serverJ2EE = isServerJ2EE();
			if(serverJ2EE==null){
				return false;
			}
			
			// Attachment
			getAttachmentsProcessingMode();
			// warning, default false.
			if(this.isFileCacheEnable()){
				// Se abilitato, deve essere specificato il repository
				this.getAttachmentRepoDir();
			}
			// warning, default 1024
			this.getFileThreshold(); 		
			
			// Versione
			this.getVersione();
			this.getDetails();
			// openspcoop home
			this.getCheckOpenSPCoopHome();

			// Loader
			Loader loaderOpenSPCoop = null;
			if(loader!=null){
				loaderOpenSPCoop = Loader.getInstance(); // gia inizializzato nello startup
			}else{
				String loaderOP = this.getClassLoader();
				if(loaderOP!=null){
					try{
						Class<?> c = Class.forName(loaderOP);
						Constructor<?> constructor = c.getConstructor(java.lang.ClassLoader.class);
						java.lang.ClassLoader test = (java.lang.ClassLoader) constructor.newInstance(this.getClass().getClassLoader());
						test.toString();
						loaderOpenSPCoop = new Loader(loader);
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura del class loader indicato nella proprieta' di openspcoop 'org.openspcoop2.pdd.classLoader': "+e.getMessage(),e);
						return false;
					}
				}else{
					loaderOpenSPCoop = Loader.getInstance();
				}
			}
			
			// Non posso inizializzarli durante la validazione poich' il ProtocolFactoryManager non e' ancora stato inizializzato
//			// EsitiProperties
//			EsitiProperties.initialize(getRootDirectory(), this.log, loaderOpenSPCoop);
			
			// Repository
			String tipoRepository = getRepositoryType();
			if(CostantiConfigurazione.REPOSITORY_FILE_SYSTEM.equals(tipoRepository)){
				if (getRepositoryDirectory() == null)	{						
					return false;
				}
				// Verra' creata se non esiste in openspcoop startup
//				if( (new File(getRepositoryDirectory())).exists() == false ){
//					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.directory'. \n La directory indicata non esiste ["+getRepositoryDirectory()+"].");
//					return false;
//				}
			}else if(CostantiConfigurazione.REPOSITORY_DB.equals(tipoRepository)){
				if (getRepositoryJDBCAdapter() == null)	{						
					return false;
				}
				String jdbcAdapter = this.getRepositoryJDBCAdapter();
				if(this.getDatabaseType()!=null && TipiDatabase.DEFAULT.equals(jdbcAdapter)){
					try{
						IJDBCAdapter adapter = JDBCAdapterFactory.createJDBCAdapter(OpenSPCoop2Properties.openspcoopProperties.getDatabaseType());
						adapter.toString();
						//System.out.println("PASSO DA FACTORY ");
						
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.jdbcAdapter'. \n L'adapter indicato non esiste ["+getRepositoryJDBCAdapter()+"]: "+e.getMessage(),e);
						return false;
					}
				}
				else{
					//	Ricerco connettore
					String adapterClass = className.getJDBCAdapter(jdbcAdapter);
					if(adapterClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.jdbcAdapter'. \n L'adapter indicato non esiste ["+getRepositoryJDBCAdapter()+"] nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						IJDBCAdapter adapter = (IJDBCAdapter) loaderOpenSPCoop.newInstance(adapterClass);
						adapter.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.jdbcAdapter'. \n L'adapter indicato non esiste ["+getRepositoryJDBCAdapter()+"]: "+e.getMessage(),e);
						return false;
					}
				}
			}else{
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.tipo'. \n Il tipo indicato non e' un tipo valido ["+getRepositoryType()+"].");
				return false;
			}
			// warning
			this.isRepositoryOnFS();
			this.isCondivisioneConfigurazioneRegistroDB();

			// EliminatoreMessaggi in Repository
			long intervalloEliminazione = getRepositoryIntervalloEliminazioneMessaggi();
			if(intervalloEliminazione<=0){
				if(intervalloEliminazione!=-1){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.timer'. \n Valore non valido ["+intervalloEliminazione+"].");			
				}
				return false;
			}
			long intervalloScadenza = getRepositoryIntervalloScadenzaMessaggi();
			if(intervalloScadenza<=0){
				if(intervalloScadenza!=-1){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.scadenzaMessaggio'. \n Valore non valido ["+intervalloScadenza+"].");			
				}
				return false;
			}
			this.isRepositoryBusteFiltraBusteScaduteRispettoOraRegistrazione();

			// EliminatoreCorrelazioniApplicative in Repository
			long intervalloScadenzaCorrelazioneApplicativa = getRepositoryIntervalloScadenzaCorrelazioneApplicativa();
			if(intervalloScadenzaCorrelazioneApplicativa<=0){
				if(intervalloScadenzaCorrelazioneApplicativa!=-1){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa'. \n Valore non valido ["+intervalloScadenzaCorrelazioneApplicativa+"].");			
				}
				return false;
			}
			this.getMaxLengthCorrelazioneApplicativa();
			this.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione();
			this.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione_EscludiConScadenzaImpostata();
			
			// Msg gia Processati (Warning)
			this.getMsgGiaInProcessamento_AttesaAttiva();
			this.getMsgGiaInProcessamento_CheckInterval();

			// Threshold per il Repository
			String [] tipiThreshold = this.getRepositoryThresholdTypes();
			if(tipiThreshold!=null){
				// CheckInterval in Repository
				long intervalloCheck = this.getRepositoryThresholdCheckInterval();
				if(intervalloCheck<=0){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.threshold.checkInterval', valore non impostato/valido.");			
					return false;
				}
				for(int i=0; i<tipiThreshold.length;i++){
					if(this.getRepositoryThresholdParameters(tipiThreshold[i])==null)
						return false;
					//	Ricerco connettore
					String tipoClass = className.getThreshold(tipiThreshold[i]);
					if(tipoClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.threshold.tipo'. \n La classe di Threshold indicata non esiste ["+tipiThreshold[i]+"] nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						IThreshold t = (IThreshold) loaderOpenSPCoop.newInstance(tipoClass);
						t.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.threshold.tipo'. \n La classe di Threshold indicata non esiste ["+tipiThreshold[i]+"]: "+e.getMessage(),e);
						return false;
					}
				}
			}

			// Check validazioneSemantica: warning
			this.isValidazioneSemanticaRegistroServiziStartupXML();
			this.isValidazioneSemanticaConfigurazioneStartupXML();
			this.isValidazioneSemanticaRegistroServiziStartup();
			this.isValidazioneSemanticaConfigurazioneStartup();
			this.isValidazioneSemanticaRegistroServiziCheckURI();
			
			// Controllo risorse
			if( this.isAbilitatoControlloRisorseConfigurazione() ||
					this.isAbilitatoControlloValidazioneSemanticaConfigurazione() ||
					this.isAbilitatoControlloRisorseDB() ||
					this.isAbilitatoControlloRisorseJMS() ||
					this.isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati() || 
					this.isAbilitatoControlloRisorseRegistriServizi() ||
					this.isAbilitatoControlloValidazioneSemanticaRegistriServizi() ||
					this.isAbilitatoControlloRisorseTracciamentiPersonalizzati()){
				// CheckInterval 
				long intervalloCheck = this.getControlloRisorseCheckInterval();
				if(intervalloCheck<=0){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.risorse.checkInterval', valore non impostato/valido.");			
					return false;
				}
				// Warning
				this.isControlloRisorseRegistriRaggiungibilitaTotale();
				this.isControlloRisorseRegistrazioneEvento();
			}


			// Tipo di Configurazione
			
			this.getConfigPreLoadingLocale();
			
			if (getTipoConfigurazionePDD() == null){		
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.config.tipo'. Proprieta' non impostata");
				return false;
			}
			if( (CostantiConfigurazione.CONFIGURAZIONE_XML.equalsIgnoreCase(getTipoConfigurazionePDD()) == false) &&
					(CostantiConfigurazione.CONFIGURAZIONE_DB.equalsIgnoreCase(getTipoConfigurazionePDD()) == false) ){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.config.tipo'. Tipo non Supportato");
				return false;
			}
			if( CostantiConfigurazione.CONFIGURAZIONE_DB.equalsIgnoreCase(getTipoConfigurazionePDD()) ){
				// Il tipo del DB e' obbligatorio.
				// Controllo che vi sia o
				// - come prefisso del datasource: tipoDatabase@datasource
				// - come tipo di database della porta di dominio.
				if(this.getPathConfigurazionePDD().indexOf("@")!=-1){
					// estrazione tipo database
					try{
						String tipoDatabase = DBUtils.estraiTipoDatabaseFromLocation(this.getPathConfigurazionePDD());
						tipoDatabase.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.config.location', mentre veniva analizzato il prefisso tipoDatabase@datasource: "+e.getMessage(),e);
						return false;
					}
				}else{
					if(this.getDatabaseType()==null){
						this.log.error("La configurazione della porta di dominio di tipo ["+getTipoConfigurazionePDD()
								+"] richiede la definizione del tipo di database indicato o come prefisso della location (tipoDB@datasource) o attraverso la proprieta' 'org.openspcoop2.pdd.repository.tipoDatabase'");
					}
				}
			}


			// Location della configurazione
			if (getPathConfigurazionePDD() == null){		
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.config.location'. Proprieta' non impostata");
				return false;
			}
			if( CostantiConfigurazione.CONFIGURAZIONE_XML.equalsIgnoreCase(getTipoConfigurazionePDD()) == true){

				String path = getPathConfigurazionePDD();
				if( (path.startsWith("http://")==false) && (path.startsWith("file://")==false) ){
					if( (new File(path)).exists() == false ){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.config.location'. \n Il file indicato non esiste ["+path+"].");
						return false;
					}
				}else{
					// validazione url
					try{
						URL v  = new URL(path);
						v.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.config.location'. \n La url indicata non e' corretta ["+path+"].");
						return false;
					}
				}
			}
			else if( CostantiConfigurazione.CONFIGURAZIONE_DB.equalsIgnoreCase(getTipoConfigurazionePDD()) == false){		
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.config.tipo'. \n Tipo non supportato ["+getTipoConfigurazionePDD()+"].");
				return false;
			}

			// (warning)
			this.isConfigurazioneDinamica();
			this.isConfigurazioneCache_ConfigPrefill();
			this.isConfigurazioneCache_RegistryPrefill();
			this.isConfigurazioneCache_accessiSynchronized();
			this.isConfigurazioneCache_transactionContext_accessiSynchronized();

			// DataSource
			if (getJNDIName_DataSource() == null){		
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.dataSource'. Proprieta' non impostata");
				return false;
			}
			if (getJNDIContext_DataSource() == null){		
				this.log.error("Riscontrato errore durante la lettura della proprieta' del contesto JNDI per il datasource: 'org.openspcoop2.pdd.dataSource.property.*'. Proprieta' definite in maniera errata?");
				return false;
			}

			// Comunicazioni infrastrutturali
			if( this.getNodeReceiver()==null ){
				return false;
			}else{
				//	Ricerco connettore
				String tipoClass = className.getNodeReceiver(this.getNodeReceiver());
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.nodeReceiver'. \n Il node receiver indicato non esiste ["+this.getNodeReceiver()+"] nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					INodeReceiver nodeReceiver = (INodeReceiver) loaderOpenSPCoop.newInstance(tipoClass);
					nodeReceiver.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.nodeReceiver'. \n Il node receiver indicato non esiste ["+this.getNodeReceiver()+"]: "+e.getMessage(),e);
					return false;
				}
			}
			// warning
			this.getNodeReceiverTimeout();
			this.getNodeReceiverTimeoutRicezioneContenutiApplicativi();
			this.getNodeReceiverTimeoutRicezioneBuste();
			this.getNodeReceiverCheckInterval();
			this.getNodeReceiverCheckDBInterval();
			this.singleConnection_NodeReceiver();

			if( this.getNodeSender()==null ){
				return false;
			}else{
				//	Ricerco connettore
				String tipoClass = className.getNodeSender(this.getNodeSender());
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.nodeSender'. \n Il node sender indicato non esiste ["+this.getNodeSender()+"] nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					INodeSender nodeSender = (INodeSender) loaderOpenSPCoop.newInstance(tipoClass);
					nodeSender.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.nodeSender'. \n Il node sender indicato non esiste ["+this.getNodeSender()+"]: "+e.getMessage(),e);
					return false;
				}


				if(CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_DB.equals(this.getNodeSender())){
					if (getRepositoryJDBCAdapter() == null)	{	
						this.log.error("Un JDBCAdapter deve essere definito in caso di NodeSender=db");
						return false;
					}
					//	Ricerco connettore
					String jdbcAdapter = this.getRepositoryJDBCAdapter();
					if(this.getDatabaseType()!=null && TipiDatabase.DEFAULT.equals(jdbcAdapter)){
						try{
							IJDBCAdapter adapter = JDBCAdapterFactory.createJDBCAdapter(OpenSPCoop2Properties.openspcoopProperties.getDatabaseType());
							adapter.toString();
						}catch(Exception e){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.jdbcAdapter'. \n L'adapter indicato non esiste ["+getRepositoryJDBCAdapter()+"]: "+e.getMessage(),e);
							return false;
						}
					}
					else{
						String adapterClass = className.getJDBCAdapter(jdbcAdapter);
						if(adapterClass == null){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.jdbcAdapter'. \n L'adapter indicato non esiste ["+getRepositoryJDBCAdapter()+"] nelle classi registrate in OpenSPCoop");
							return false;
						}
						try{
							IJDBCAdapter adapter = (IJDBCAdapter) loaderOpenSPCoop.newInstance(tipoClass);
							adapter.toString();
						}catch(Exception e){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.jdbcAdapter'. \n L'adapter indicato non esiste ["+getRepositoryJDBCAdapter()+"]: "+e.getMessage(),e);
							return false;
						}
					}
				}
			}

			
			// Servizi HTTP: warning
			TransferLengthModes modeConsegna = this.getTransferLengthModes_consegnaContenutiApplicativi();
			if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(modeConsegna)){
				this.getChunkLength_consegnaContenutiApplicativi();
			}
			TransferLengthModes modeInoltro = this.getTransferLengthModes_inoltroBuste();
			if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(modeInoltro)){
				this.getChunkLength_inoltroBuste();
			}
			this.getTransferLengthModes_ricezioneBuste();
			this.getTransferLengthModes_ricezioneContenutiApplicativi();
			
			this.isFollowRedirects_consegnaContenutiApplicativi_soap();
			this.isFollowRedirects_consegnaContenutiApplicativi_rest();
			this.isFollowRedirects_inoltroBuste_soap();
			this.isFollowRedirects_inoltroBuste_rest();
			this.getFollowRedirectsMaxHop_consegnaContenutiApplicativi();
			this.getFollowRedirectsMaxHop_inoltroBuste();
			this.isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi();
			this.isAcceptOnlyReturnCode_200_202_inoltroBuste();
			this.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi();
			this.isAcceptOnlyReturnCode_307_inoltroBuste();
			
			this.checkSoapActionQuotedString_ricezioneContenutiApplicativi();
			this.checkSoapActionQuotedString_ricezioneBuste();
			
			this.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi();
			this.isControlloContentTypeAbilitatoRicezioneBuste();
			this.isPrintInfoCertificate();
			
			if(this.isGestoreCredenzialiPortaDelegataEnabled()) {
				TipoAutenticazioneGestoreCredenziali tipo = this.getGestoreCredenzialiPortaDelegataTipoAutenticazioneCanale();
				switch (tipo) {
				case NONE:
					break;
				case BASIC:
					this.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicUsername();
					this.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicPassword();
					break;
				case SSL:
					this.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleSslSubject();
					break;
				case PRINCIPAL:
					this.getGestoreCredenzialiPortaDelegataAutenticazioneCanalePrincipal();
					break;
				}
				String bu = this.getGestoreCredenzialiPortaDelegataHeaderBasicUsername();
				String bp = this.getGestoreCredenzialiPortaDelegataHeaderBasicPassword();
				String ss = this.getGestoreCredenzialiPortaDelegataHeaderSslSubject();
				@SuppressWarnings("unused")
				String si = this.getGestoreCredenzialiPortaDelegataHeaderSslIssuer();
				String sc = this.getGestoreCredenzialiPortaDelegataHeaderSslCertificate();
				if(sc!=null) {
					isGestoreCredenzialiPortaDelegataHeaderSslCertificateUrlDecode();
					isGestoreCredenzialiPortaDelegataHeaderSslCertificateBase64Decode();
					isGestoreCredenzialiPortaDelegataHeaderSslCertificateEnrich_BEGIN_END();
					if(isGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters()) {
						getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_source();
						getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_dest();
					}
				}
				String p = this.getGestoreCredenzialiPortaDelegataHeaderPrincipal();
				if(bu==null && ss==null && sc==null && p==null) {
					this.log.error("L'abilitazione del gestore delle credenziali (PortaDelegata) richiede almeno la definizione di un header su cui vengono fornite le credenziali");
					return false;
				}
				if(bu!=null) {
					if(bp==null) {
						this.log.error("L'abilitazione del gestore delle credenziali (PortaDelegata) richiede la definizione di un header su cui viene indicata la password, se viene definito un header per l'username");
						return false;
					}
				}
			}
			
			if(this.isGestoreCredenzialiPortaApplicativaEnabled()) {
				TipoAutenticazioneGestoreCredenziali tipo = this.getGestoreCredenzialiPortaApplicativaTipoAutenticazioneCanale();
				switch (tipo) {
				case NONE:
					break;
				case BASIC:
					this.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicUsername();
					this.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicPassword();
					break;
				case SSL:
					this.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleSslSubject();
					break;
				case PRINCIPAL:
					this.getGestoreCredenzialiPortaApplicativaAutenticazioneCanalePrincipal();
					break;
				}
				String bu = this.getGestoreCredenzialiPortaApplicativaHeaderBasicUsername();
				String bp = this.getGestoreCredenzialiPortaApplicativaHeaderBasicPassword();
				String ss = this.getGestoreCredenzialiPortaApplicativaHeaderSslSubject();
				@SuppressWarnings("unused")
				String si = this.getGestoreCredenzialiPortaApplicativaHeaderSslIssuer();
				String sc = this.getGestoreCredenzialiPortaApplicativaHeaderSslCertificate();
				if(sc!=null) {
					isGestoreCredenzialiPortaApplicativaHeaderSslCertificateUrlDecode();
					isGestoreCredenzialiPortaApplicativaHeaderSslCertificateBase64Decode();
					isGestoreCredenzialiPortaApplicativaHeaderSslCertificateEnrich_BEGIN_END();
					if(isGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters()) {
						getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_source();
						getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_dest();
					}
				}
				String p = this.getGestoreCredenzialiPortaApplicativaHeaderPrincipal();
				if(bu==null && ss==null && sc==null && p==null) {
					this.log.error("L'abilitazione del gestore delle credenziali (PortaApplicativa) richiede almeno la definizione di un header su cui vengono fornite le credenziali");
					return false;
				}
				if(bu!=null) {
					if(bp==null) {
						this.log.error("L'abilitazione del gestore delle credenziali (PortaApplicativa) richiede la definizione di un header su cui viene indicata la password, se viene definito un header per l'username");
						return false;
					}
				}
			}
			
			this.getHttpUserAgent();
			this.getHttpServer();
			this.getHttpXPdDDetails();
			
			if(this.isEnabledEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi()){
				this.getCharsetEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi();
				this.getEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi();
			}
			if(this.isEnabledEncodingRFC2047HeaderValue_ricezioneBuste()){
				this.getCharsetEncodingRFC2047HeaderValue_ricezioneBuste();
				this.getEncodingRFC2047HeaderValue_ricezioneBuste();
			}
			if(this.isEnabledEncodingRFC2047HeaderValue_inoltroBuste()){
				this.getCharsetEncodingRFC2047HeaderValue_inoltroBuste();
				this.getEncodingRFC2047HeaderValue_inoltroBuste();
			}
			if(this.isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi()){
				this.getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
				this.getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
			}
			
			this.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneContenutiApplicativi();
			this.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneBuste();
			this.isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste();
			this.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi();
			
			this.getGestioneCORS_returnCode_ricezioneContenutiApplicativi();
			this.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneContenutiApplicativi();
			this.getGestioneCORS_returnCode_ricezioneBuste();
			this.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneBuste();
			
			

			// ConnectionFactory
			if( CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(this.getNodeReceiver()) 
					|| CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(this.getNodeSender()) ){
				if (getJNDIName_ConnectionFactory() == null){		
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.queueConnectionFactory'. Proprieta' non impostata");
					return false;
				}
				if (getJNDIContext_ConnectionFactory() == null){		
					this.log.error("Riscontrato errore durante la lettura della proprieta' del contesto JNDI del ConnectionFactory: 'org.openspcoop2.pdd.connectionFactory.property.*'. Proprieta' definite in maniera errata?");
					return false;
				}

				// Code Interne
				// TODO fare anche controllo per SenderJMS
				if(this.getJNDIQueueName( (CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(this.getNodeReceiver())),
						(CostantiConfigurazione.COMUNICAZIONE_INFRASTRUTTURALE_JMS.equals(this.getNodeSender())))==null){
					// log stampato dentro il metodo
					return false;
				}
				if (getJNDIContext_CodeInterne() == null){		
					this.log.error("Riscontrato errore durante la lettura della proprieta' del contesto JNDI delle code interne: 'org.openspcoop2.pdd.queue.property.*'. Proprieta' definite in maniera errata?");
					return false;
				}
				// warning
				this.getAcknowledgeModeSessioneConnectionFactory();

				// TransactionManager (Warning)
				this.getTransactionManager_AttesaAttiva();
				this.getTransactionManager_CheckDBInterval();
				this.getTransactionManager_CheckInterval();
				this.singleConnection_TransactionManager();
			}

			//	Timer EJB
			if(this.getJNDITimerEJBName()==null){
				// log stampato dentro il metodo
				return false;
			}
			if (getJNDIContext_TimerEJB() == null){		
				this.log.error("Riscontrato errore durante la lettura della proprieta' del contesto JNDI delle code interne: 'org.openspcoop2.pdd.queue.property.*'. Proprieta' definite in maniera errata?");
				return false;
			}
			// warning
			this.getTimerEJBDeployTimeout();
			this.getTimerEJBDeployCheckInterval();
			this.isTimerAutoStart_StopTimer();
			
			this.isTimerGestoreRiscontriRicevuteAbilitato();
			this.isTimerGestoreRiscontriRicevuteAbilitatoLog();
			this.getTimerGestoreRiscontriRicevuteLimit();
			if(this.isTimerLockByDatabase()) {
				this.getTimerGestoreRiscontriRicevute_lockMaxLife();
				this.getTimerGestoreRiscontriRicevute_lockIdleTime();
			}
			
			this.isTimerGestoreMessaggiAbilitato();
			this.isTimerGestoreMessaggiAbilitatoOrderBy();
			this.isTimerGestoreMessaggiAbilitatoLog();
			this.getTimerGestoreMessaggiLimit();
			this.isTimerGestoreMessaggiVerificaConnessioniAttive();
			if(this.isTimerLockByDatabase()) {
				this.getTimerGestoreMessaggi_lockMaxLife();
				this.getTimerGestoreMessaggi_lockIdleTime();
			}
			
			this.isTimerGestorePuliziaMessaggiAnomaliAbilitato();
			this.isTimerGestorePuliziaMessaggiAnomaliAbilitatoOrderBy();
			this.isTimerGestorePuliziaMessaggiAnomaliAbilitatoLog();
			this.getTimerGestorePuliziaMessaggiAnomaliLimit();
			if(this.isTimerLockByDatabase()) {
				this.getTimerGestorePuliziaMessaggiAnomali_lockMaxLife();
				this.getTimerGestorePuliziaMessaggiAnomali_lockIdleTime();
			}
			
			this.isTimerGestoreRepositoryBusteAbilitato();
			this.isTimerGestoreRepositoryBusteAbilitatoOrderBy();
			this.isTimerGestoreRepositoryBusteAbilitatoLog();
			this.getTimerGestoreRepositoryBusteLimit();
			if(this.isTimerLockByDatabase()) {
				this.getTimerGestoreRepositoryBuste_lockMaxLife();
				this.getTimerGestoreRepositoryBuste_lockIdleTime();
			}
			
			this.isTimerConsegnaContenutiApplicativiAbilitato();
			this.isTimerConsegnaContenutiApplicativiDebug();
			this.getTimerConsegnaContenutiApplicativiThreadsPoolSize();
			this.getTimerConsegnaContenutiApplicativiThreadsQueueSize();
			this.getTimerConsegnaContenutiApplicativiLimit();
			this.getTimerConsegnaContenutiApplicativiInterval();
			this.getTimerConsegnaContenutiApplicativiMinIntervalResend();
			this.isLoadBalancerDebug();
			if(this.isTimerLockByDatabase()) {
				this.getTimerConsegnaContenutiApplicativi_lockMaxLife();
				this.getTimerConsegnaContenutiApplicativi_lockIdleTime();
			}
			this.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife();
				
			
			
			// Gestione Serializable DB (Warning)
			this.getGestioneSerializableDB_AttesaAttiva();
			this.getGestioneSerializableDB_CheckInterval();

			// GestioneErrore
			ProprietaErroreApplicativo paError = getProprietaGestioneErrorePD_engine(null,true);
			if( paError == null  ){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.erroreApplicativo'.");
				return false;
			}
			
			// Errori http
			getErroriHttpHeaderGovWayStatus();
			getErroriHttpHeaderGovWayType();
			getErroriHttpHeaderGovWayCode();
			
			// Errori soap
			this.isErroriSoapUseGovWayStatusAsFaultCode();
			this.isErroriSoapHttpHeaderGovWayCodeEnabled();
			
			// Errori			
			this.isErroriGovWayStatusEnabled();
			this.isErroriGovWayInstanceEnabled();
			this.isErroriGovWayForceSpecificDetails();
			this.isErroriGovWayFaultDetailsWithProblemRFC7807();
			
			// Problem RFC 7807
			this.isProblemRFC7807_enrichTitleAsGovWayType();
			this.isProblemRFC7807_enrichTitleAsGovWayType_camelCaseDecode();
			this.isProblemRFC7807_enrichTitleAsGovWayType_customClaim();
			this.getProblemRFC7807_transactionId_claim();
			this.getProblemRFC7807_code_claim();
			this.getProblemRFC7807_type_claim();

			// IdentitaPdD
			if( this.getIdentitaPortaDefault() == null  ){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.identificativoPorta'.");
				return false;
			}

			// Check tipi di default: urlBased, trasporto, soap
			List<String> headerDefault = new ArrayList<>();
			headerDefault.add(CostantiConfigurazione.HEADER_INTEGRAZIONE_URL_BASED);
			headerDefault.add(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO);
			headerDefault.add(CostantiConfigurazione.HEADER_INTEGRAZIONE_SOAP);	
			if(checkTipiIntegrazione(headerDefault.toArray(new String[1]))==false)
				return false;
			
			// Integrazione tra Servizi Applicativi e GovWay
			if ( this.getTipoIntegrazionePD() == null ){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.tipo.pd'. Almeno un tipo di integrazione e' obbligatorio.");
				return false;
			}else{
				String[] tipiIntegrazionePD = this.getTipoIntegrazionePD();

				// Check tipi registrati
				for(int i=0; i<tipiIntegrazionePD.length;i++){
					String tipoClass = className.getIntegrazionePortaDelegata(tipiIntegrazionePD[i]);
					if(tipoClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.tipo.pd="+tipiIntegrazionePD[i]+
								"'. \n L'header di integrazione indicato non esiste nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						IGestoreIntegrazionePD gestore = (IGestoreIntegrazionePD) loaderOpenSPCoop.newInstance(tipoClass);
						if(gestore==null){
							throw new Exception("Oggetto non creato dopo aver chiamato la newInstance");
						}
						gestore.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.tipo.pd="+tipiIntegrazionePD[i]+"' (classe:"+tipoClass+"). \n Errore avvenuto: "+e.getMessage(),e);
						return false;
					}
				}

				if(checkTipiIntegrazione(tipiIntegrazionePD)==false)
					return false;
			}
			
			Properties integrazioneProtocol_PD = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.tipo.pd.");
			Enumeration<?> keys = integrazioneProtocol_PD.keys();
			while (keys.hasMoreElements()) {
				String protocollo = (String) keys.nextElement();
				
				if(this.getTipoIntegrazionePD(protocollo)!=null){
					
					String[] tipiIntegrazionePD_protocollo = this.getTipoIntegrazionePD(protocollo);

					// Check tipi registrati
					for(int i=0; i<tipiIntegrazionePD_protocollo.length;i++){
						String tipoClass = className.getIntegrazionePortaDelegata(tipiIntegrazionePD_protocollo[i]);
						if(tipoClass == null){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.tipo.pd."+protocollo+"="+tipiIntegrazionePD_protocollo[i]+
									"'. \n L'header di integrazione indicato non esiste nelle classi registrate in OpenSPCoop");
							return false;
						}
						try{
							IGestoreIntegrazionePD gestore = (IGestoreIntegrazionePD) loaderOpenSPCoop.newInstance(tipoClass);
							if(gestore==null){
								throw new Exception("Oggetto non creato dopo aver chiamato la newInstance");
							}
							gestore.toString();
						}catch(Exception e){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.tipo.pd."+protocollo+"="+tipiIntegrazionePD_protocollo[i]+"' (classe:"+tipoClass+"). \n Errore avvenuto: "+e.getMessage(),e);
							return false;
						}
					}

					if(checkTipiIntegrazione(tipiIntegrazionePD_protocollo)==false)
						return false;
				}
			}

			// Integrazione tra GovWay e Servizi Applicativi
			if ( this.getTipoIntegrazionePA() == null ){
				String[] tipiIntegrazionePA = this.getTipoIntegrazionePA();

				// Check tipi registrati
				for(int i=0; i<tipiIntegrazionePA.length;i++){
					String tipoClass = className.getIntegrazionePortaApplicativa(tipiIntegrazionePA[i]);
					if(tipoClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.tipo.pa="+tipiIntegrazionePA[i]+
								"'. \n L'header di integrazione indicato non esiste nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						IGestoreIntegrazionePA gestore = (IGestoreIntegrazionePA) loaderOpenSPCoop.newInstance(tipoClass);
						if(gestore==null){
							throw new Exception("Oggetto non creato dopo aver chiamato la newInstance");
						}
						gestore.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.tipo.pa="+tipiIntegrazionePA[i]+"' (classe:"+tipoClass+"). \n Errore avvenuto: "+e.getMessage(),e);
						return false;
					}
				}

				if(checkTipiIntegrazione(tipiIntegrazionePA)==false)
					return false;
			}
			
			Properties integrazioneProtocol_PA = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.tipo.pa.");
			keys = integrazioneProtocol_PA.keys();
			while (keys.hasMoreElements()) {
				String protocollo = (String) keys.nextElement();
				
				if(this.getTipoIntegrazionePA(protocollo)!=null){
					
					String[] tipiIntegrazionePA_protocollo = this.getTipoIntegrazionePA(protocollo);

					// Check tipi registrati
					for(int i=0; i<tipiIntegrazionePA_protocollo.length;i++){
						String tipoClass = className.getIntegrazionePortaApplicativa(tipiIntegrazionePA_protocollo[i]);
						if(tipoClass == null){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.tipo.pa."+protocollo+"="+tipiIntegrazionePA_protocollo[i]+
									"'. \n L'header di integrazione indicato non esiste nelle classi registrate in OpenSPCoop");
							return false;
						}
						try{
							IGestoreIntegrazionePA gestore = (IGestoreIntegrazionePA) loaderOpenSPCoop.newInstance(tipoClass);
							if(gestore==null){
								throw new Exception("Oggetto non creato dopo aver chiamato la newInstance");
							}
							gestore.toString();
						}catch(Exception e){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.tipo.pa."+protocollo+"="+tipiIntegrazionePA_protocollo[i]+"' (classe:"+tipoClass+"). \n Errore avvenuto: "+e.getMessage(),e);
							return false;
						}
					}

					if(checkTipiIntegrazione(tipiIntegrazionePA_protocollo)==false)
						return false;
				}
			}
			
			// Integrazione Template
			if(this.getIntegrazioneTemplateRequestPropertyTipo()==null) {
				return false;
			}
			if(this.getIntegrazioneTemplateRequestPropertyFile()==null) {
				return false;
			}
			if(this.getIntegrazioneTemplateResponsePropertyTipo()==null) {
				return false;
			}
			if(this.getIntegrazioneTemplateResponsePropertyFile()==null) {
				return false;
			}
			getIntegrazioneTemplatePortaDelegataRequestTipo();
			getIntegrazioneTemplatePortaDelegataRequestFile();
			getIntegrazioneTemplatePortaDelegataResponseTipo();
			getIntegrazioneTemplatePortaDelegataResponseFile();
			getIntegrazioneTemplatePortaApplicativaRequestTipo();
			getIntegrazioneTemplatePortaApplicativaRequestFile();
			getIntegrazioneTemplatePortaApplicativaResponseTipo();
			getIntegrazioneTemplatePortaApplicativaResponseFile();
			
			// Warning
			this.isIntegrazioneAsincroniConIdCollaborazioneEnabled();
			this.getHeaderIntegrazioneSOAPPdDVersione();
			this.getHeaderIntegrazioneSOAPPdDDetails();
			this.deleteHeaderIntegrazioneRequestPD();
			this.deleteHeaderIntegrazioneResponsePD();
			this.processHeaderIntegrazionePDResponse(false);
			this.deleteHeaderIntegrazioneRequestPA();
			this.deleteHeaderIntegrazioneResponsePA();
			this.processHeaderIntegrazionePARequest(false);

			//	TipoAutorizzazioneBuste
			if( this.getTipoAutorizzazioneBuste()==null ){
				return false;
			}else{
				if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(this.getTipoAutorizzazioneBuste())==false){
					//	Ricerco connettore
					String tipoClass = className.getAutorizzazionePortaApplicativa(this.getTipoAutorizzazioneBuste());
					if(tipoClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.autorizzazioneBuste'. \n L'autorizzazione delle buste indicata non esiste ["+this.getTipoAutorizzazioneBuste()+"] nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						IAutorizzazionePortaApplicativa auth = (IAutorizzazionePortaApplicativa) loaderOpenSPCoop.newInstance(tipoClass);
						auth.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.autorizzazioneBuste'. \n L'autorizzazione delle buste indicata non esiste ["+this.getTipoAutorizzazioneBuste()+"]: "+e.getMessage(),e);
						return false;
					}
				}
			}

			// GestoreRepositoryBuste
			if( this.getGestoreRepositoryBuste() == null  ){
				return false;
			}else{
				//	Ricerco
				if(this.getDatabaseType()!=null){
					try{
						IGestoreRepository repository = GestoreRepositoryFactory.createRepositoryBuste(this.getDatabaseType());
						repository.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante l'inizializzazione del gestore del repository buste associato dalla factory al tipo di database ["+this.getDatabaseType()+"]: "+e.getMessage(),e);
						return false;
					}
				}
				else{
					String tipoClass = className.getRepositoryBuste(this.getGestoreRepositoryBuste());
					if(tipoClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.repositoryBuste'. \n Il gestore del repository buste indicato non esiste ["+this.getGestoreRepositoryBuste()+"] nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						IGestoreRepository repository = (IGestoreRepository) loaderOpenSPCoop.newInstance(tipoClass);
						repository.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.repositoryBuste'. \n Il gestore del repository buste indicato non esiste ["+this.getGestoreRepositoryBuste()+"]: "+e.getMessage(),e);
						return false;
					}
				}
			}

			// Filtro duplicati (warning)
			// Ricerco
			String tipoClassFiltroDuplicati = className.getFiltroDuplicati(this.getGestoreFiltroDuplicatiRepositoryBuste());
			if(tipoClassFiltroDuplicati == null){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.protocol.filtroDuplicati'. \n Il gestore filtro duplicati del repository buste indicato non esiste ["+this.getGestoreFiltroDuplicatiRepositoryBuste()+"] nelle classi registrate in OpenSPCoop");
				return false;
			}
			try{
				IFiltroDuplicati duplicati = (IFiltroDuplicati) loaderOpenSPCoop.newInstance(tipoClassFiltroDuplicati);
				duplicati.toString();
			}catch(Exception e){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.protocol.filtroDuplicati'. \n Il gestore filtro duplicati del repository buste indicato non esiste ["+this.getGestoreFiltroDuplicatiRepositoryBuste()+"]: "+e.getMessage(),e);
				return false;
			}
			
			// SQLQueryObject
			if(this.getDatabaseType()!=null){
				if ( ! TipiDatabase.isAMember(this.getDatabaseType())){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.tipoDatabase', tipo di database non gestito");
					return false;
				}
				// Ricerco
				String tipoClass = className.getSQLQueryObject(this.getDatabaseType());
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.tipoDatabase'. \n L'oggetto SQLQuery indicato non esiste ["+this.getDatabaseType()+"] nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					ISQLQueryObject sqlQuery = (ISQLQueryObject) loaderOpenSPCoop.newInstance(tipoClass,TipiDatabase.DEFAULT);
					sqlQuery.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.tipoDatabase'. \n L'oggetto SQLQuery indicato non esiste ["+this.getDatabaseType()+"]: "+e.getMessage(),e);
					return false;
				}
			}

			// Connettore (Warning)
			this.getConnectionTimeout_consegnaContenutiApplicativi();
			this.getConnectionTimeout_inoltroBuste();
			this.getReadConnectionTimeout_consegnaContenutiApplicativi();
			this.getReadConnectionTimeout_inoltroBuste();
			this.getConnectionLife_consegnaContenutiApplicativi();
			this.getConnectionLife_inoltroBuste();
			
			// Connettore http (url https)
			if(this.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste()) {
				this.getConnettoreHttp_urlHttps_repository_inoltroBuste();
			}
			if(this.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi()) {
				this.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi();
			}
			if(this.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste() ||
					this.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi()) {
				if(this.isConnettoreHttp_urlHttps_cacheEnabled()) {
					this.getConnettoreHttp_urlHttps_cacheSize();
				}
			}

			// Contatore esponenziale per consegna
			if(this.isRitardoConsegnaAbilitato()){
				if( this.getRitardoConsegnaEsponenziale() <= 0 ){
					return false;
				}else if(this.getRitardoConsegnaEsponenziale() > 0){
					try{
						this.isRitardoConsegnaEsponenzialeConMoltiplicazione();
					}catch(Exception e){
						return false;
					}
					if( this.getRitardoConsegnaEsponenzialeLimite() <= 0 ){
						return false;
					}
				}
			}

			// Cache per gestore Messaggi
			try{
				if(this.isAbilitataCacheGestoreMessaggi()){
					String algoritmo = this.getAlgoritmoCacheGestoreMessaggi();
					if(algoritmo!=null &&
							CostantiConfigurazione.CACHE_LRU.equals(algoritmo)==false && 
							CostantiConfigurazione.CACHE_MRU.equals(algoritmo)==false){
						this.log.error("Algoritmo utilizzato con la cache (Gestore Messaggi) non conosciuto: "+algoritmo);
						throw new Exception("Algoritmo Cache (Gestore Messaggi) non conosciuto");
					}
					this.getDimensioneCacheGestoreMessaggi();
					this.getItemIdleTimeCacheGestoreMessaggi();
					this.getItemLifeSecondCacheGestoreMessaggi();
				}
			}catch(Exception e){
				// Il motivo dell'errore viene loggato dentro i metodi
				return false;
			}

			// Gestione JMX
			this.isRisorseJMXAbilitate();
			this.getJNDIName_MBeanServer();
			this.getJNDIContext_MBeanServer();

			// DateManager
			if(this.getTipoDateManager()==null)
				return false;
			if(getTipoDateTimeFormat()==null) {
				return false;
			}
			String tipoDateManger = className.getDateManager(this.getTipoDateManager());
			if(tipoDateManger == null){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.date.tipo'. \n Il DateManager indicato non esiste ["+this.getTipoDateManager()+"] nelle classi registrate in OpenSPCoop");
				return false;
			}
			try{
				IDate date = (IDate) loaderOpenSPCoop.newInstance(tipoDateManger);
				date.toString();
			}catch(Exception e){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.date.tipo'. \n Il DateManager indicato non esiste ["+this.getTipoDateManager()+"]: "+e.getMessage(),e);
				return false;
			}
			if (this.getDateManagerProperties() == null){		
				this.log.error("Riscontrato errore durante la lettura della proprieta' del DataManager: 'org.openspcoop2.pdd.date.property.*'. Proprieta' definite in maniera errata?");
				return false;
			}
			// Warning
			this.getTipoTempoBusta(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);

			// IntegrationManager (Warning)
			this.integrationManager_readInformazioniTrasporto();
			this.integrationManager_isNomePortaDelegataUrlBased();

			// Gestione Attachments (Warning)
			this.isDeleteInstructionTargetMachineXml();
			this.isTunnelSOAP_loadMailcap();
			this.getTunnelSOAPKeyWord_headerTrasporto();
			this.getTunnelSOAPKeyWordMimeType_headerTrasporto();
			this.getTunnelSOAPKeyWord_urlBased();
			this.getTunnelSOAPKeyWordMimeType_urlBased();

			// MustUnderstandHandler (warning)
			this.isBypassFilterMustUnderstandEnabledForAllHeaders();

			// Realm Autenticazione Basic
			if(this.getCryptConfigAutenticazioneApplicativi()==null) {
				return false;
			}
			if(this.getCryptConfigAutenticazioneSoggetti()==null) {
				return false;
			}
			this.getRealmAutenticazioneBasic();
			
			// Gestori Credenziali PD
			String [] gestoriCredenzialiPD = this.getTipoGestoreCredenzialiPD();
			if(gestoriCredenzialiPD!=null){
				for(int i=0; i<gestoriCredenzialiPD.length;i++){
					//	Ricerco
					String tipoClass = className.getGestoreCredenziali(gestoriCredenzialiPD[i]);
					if(tipoClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.services.pd.gestoriCredenziali'. \n La classe del GestoreCredenziali indicata non esiste ["+gestoriCredenzialiPD[i]+"] nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						IGestoreCredenziali g = (IGestoreCredenziali) loaderOpenSPCoop.newInstance(tipoClass);
						g.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.services.pd.gestoriCredenziali'. \n La classe del GestoreCredenziali indicata non esiste ["+gestoriCredenzialiPD[i]+"]: "+e.getMessage(),e);
						return false;
					}
				}
			}
			
			// Gestori Credenziali PA
			String [] gestoriCredenzialiPA = this.getTipoGestoreCredenzialiPA();
			if(gestoriCredenzialiPA!=null){
				for(int i=0; i<gestoriCredenzialiPA.length;i++){
					//	Ricerco
					String tipoClass = className.getGestoreCredenziali(gestoriCredenzialiPA[i]);
					if(tipoClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.services.pa.gestoriCredenziali'. \n La classe del GestoreCredenziali indicata non esiste ["+gestoriCredenzialiPA[i]+"] nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						IGestoreCredenziali g = (IGestoreCredenziali) loaderOpenSPCoop.newInstance(tipoClass);
						g.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.services.pa.gestoriCredenziali'. \n La classe del GestoreCredenziali indicata non esiste ["+gestoriCredenzialiPA[i]+"]: "+e.getMessage(),e);
						return false;
					}
				}
			}
			
			// Gestori Credenziali IntegrationManager
			String [] gestoriCredenzialiIM = this.getTipoGestoreCredenzialiIM();
			if(gestoriCredenzialiIM!=null){
				for(int i=0; i<gestoriCredenzialiIM.length;i++){
					//	Ricerco
					String tipoClass = className.getGestoreCredenzialiIM(gestoriCredenzialiIM[i]);
					if(tipoClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.services.integrationManager.gestoriCredenziali'. \n La classe del GestoreCredenziali indicata non esiste ["+gestoriCredenzialiIM[i]+"] nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						IGestoreCredenzialiIM g = (IGestoreCredenzialiIM) loaderOpenSPCoop.newInstance(tipoClass);
						g.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.services.integrationManager.gestoriCredenziali'. \n La classe del GestoreCredenziali indicata non esiste ["+gestoriCredenzialiIM[i]+"]: "+e.getMessage(),e);
						return false;
					}
				}
			}
			
			// warning Risposta Asincrona
			this.getTimeoutBustaRispostaAsincrona();
			this.getCheckIntervalBustaRispostaAsincrona();

			// Configurazione Cluster
			this.getClusterId(false);
			this.getClusterIdNumerico();
			if(this.isTimerLockByDatabase()) {
				this.isTimerLockByDatabaseNotifyLogEnabled();
			}
			this.getPddContextSerializer();
			
			// Warning
			this.isGenerazioneAttributiAsincroni(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.isGenerazioneListaTrasmissioni(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.isGenerazioneErroreProtocolloFiltroDuplicati(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.isCheckFromRegistroFiltroDuplicatiAbilitato(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.isCheckFromRegistroConfermaRicezioneAbilitato(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.isCheckFromRegistroConsegnaInOrdineAbilitato(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.isGestioneConsegnaInOrdine(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.isGestioneElementoCollaborazione(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.isGestioneRiscontri(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.ignoraEccezioniNonGravi_Validazione();
			this.isForceSoapPrefixCompatibilitaOpenSPCoopV1();
			this.isReadQualifiedAttribute(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.isValidazioneIDBustaCompleta(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);

			// Stateless
			if(this.getStatelessOneWay()==null)
				return false;
			if(this.getStatelessSincrono()==null)
				return false;
			if(this.getStatelessAsincroni()==null)
				return false;
			if(this.getStatelessRouting()==null)
				return false;

			// Warning
			this.isGestioneOnewayStateful_1_1();
			this.isRinegoziamentoConnessione();

			// Handlers
			this.isMergeHandlerBuiltInAndHandlerUser();
			
			// Handlers BuiltIn
			this.isPrintInfoHandlerBuiltIn();
			if(this._validateHandlersBuiltIn(className, loaderOpenSPCoop)==false) {
				return false;
			}
			
			// Handlers
			this.isPrintInfoHandler();
			if(this._validateHandlers(className, loaderOpenSPCoop)==false) {
				return false;
			}
			
			// MessageSecurity
			this.isLoadBouncyCastle();
			this.isGenerazioneActorDefault(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.getActorDefault(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			this.getPrefixWsuId();
			this.getExternalPWCallbackPropertyFile();
			this.isAbilitataCacheMessageSecurityKeystore();
			this.getDimensioneCacheMessageSecurityKeystore();
			this.getItemLifeSecondCacheMessageSecurityKeystore();

			// Accesso registro servizi
			this.isReadObjectStatoBozza();
			
			// Tracce
			this.isTracciaturaFallita_BloccaCooperazioneInCorso();
			this.isTracciaturaFallita_BloccoServiziPdD();
			
			// Diagnostici
			this.isRegistrazioneDiagnosticaFile_intestazione_formatValues();
			this.isRegistrazioneDiagnosticaFallita_BloccoServiziPdD();
			this.isLoggerSaajDisabilitato();
			
			// Dump
			this.isDumpAllAttachments();
			this.isDumpBinario_registrazioneDatabase();
			this.isDumpFallito_BloccaCooperazioneInCorso();
			this.isDumpFallito_BloccoServiziPdD();

			// DumpNotRealtime
			this.getDumpNonRealtime_inMemoryThreshold();
			this.getDumpNonRealtime_mode();
			if(this.isDumpNonRealtime_fileSystemMode()) {
				this.getDumpNonRealtime_repository();
			}
			this.isDumpNonRealtime_throwStreamingHandlerException();
			
			// Generatore di ID
			String tipoIDGenerator = this.getTipoIDManager();
			if(CostantiConfigurazione.NONE.equals(tipoIDGenerator)==false){
				String tipoIdManger = className.getUniqueIdentifier(tipoIDGenerator);
				if(tipoIdManger == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.idGenerator'. \n Il generatore di unique identifier indicato non esiste ["+this.getTipoIDManager()+"] nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					IUniqueIdentifierGenerator uniqueIdentifier = (IUniqueIdentifierGenerator) loaderOpenSPCoop.newInstance(tipoIdManger);
					uniqueIdentifier.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.idGenerator'. \n Il generatore di unique identifier non esiste ["+this.getTipoIDManager()+"]: "+e.getMessage(),e);
					return false;
				}
			}
			
			// InitOpenSPCoop2MessageFactory
			if ( this.getOpenspcoop2MessageFactory() != null ){
				String tipo = this.getOpenspcoop2MessageFactory();
				// Check tipi registrati
				String tipoClass = className.getOpenSPCoop2MessageFactory(tipo);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.messagefactory'=...,"+tipo+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					OpenSPCoop2MessageFactory test = (OpenSPCoop2MessageFactory) loaderOpenSPCoop.newInstance(tipoClass);
					test.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.messagefactory'=...,"+tipoClass+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				} 
			}
			
			// InitOpenSPCoop2MessageFactory
			if ( this.getMessageSecurityContext() != null ){
				String tipo = this.getMessageSecurityContext();
				// Check tipi registrati
				String tipoClass = className.getMessageSecurityContext(tipo);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.messageSecurity.context'=...,"+tipo+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					MessageSecurityContext test = (MessageSecurityContext) loaderOpenSPCoop.newInstance(tipoClass);
					test.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.messageSecurity.context'=...,"+tipoClass+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				} 
			}
			if ( this.getMessageSecurityDigestReader() != null ){
				String tipo = this.getMessageSecurityDigestReader();
				// Check tipi registrati
				String tipoClass = className.getMessageSecurityDigestReader(tipo);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.messageSecurity.digestReader'=...,"+tipo+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					IDigestReader test = (IDigestReader) loaderOpenSPCoop.newInstance(tipoClass);
					test.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.messageSecurity.digestReader'=...,"+tipoClass+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				} 
			}
			
			// test warning
			isPrintInfoFactory();
			isPrintInfoMessageSecurity();
			
			// FreeMemoryLog
			this.getFreeMemoryLog();
			
			// DefaultProtocol
			this.getDefaultProtocolName();
			
			// IntegrationManager
			this.isIntegrationManagerEnabled();
			
			// Informazioni generazione errori
			this.isGenerazioneErroreProtocolloNonSupportato();
			this.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled();
			this.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataImbustamentoSOAPEnabled();
			this.isGenerazioneErroreHttpMethodUnsupportedPortaApplicativaEnabled();
			this.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled();
			this.isGenerazioneErroreHttpMethodUnsupportedCheckEnabled();
			
			// Informazioni generazione WSDL
			this.isGenerazioneWsdlPortaDelegataEnabled();
			this.isGenerazioneWsdlPortaApplicativaEnabled();
			this.isGenerazioneWsdlIntegrationManagerEnabled();
			
			// Check
			this.isCheckEnabled();
			this.isCheckReadJMXResourcesEnabled();
			this.getCheckReadJMXResourcesUsername();
			this.getCheckReadJMXResourcesPassword();
						
			// Datasource Wrapped
			this.isDSOp2UtilsEnabled();
			
			// NotifierInputStreamEnabled
			if(this.isNotifierInputStreamEnabled()) {
				String notifierClass = null;
				try{
					notifierClass = this.getNotifierInputStreamCallback();
				}catch(Exception e){
					return false; // log registrato nel metodo
				}
				if(notifierClass!=null){
					String tipoClass = className.getNotifierCallback(notifierClass);
					if(tipoClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.notifierCallback'=...,"+notifierClass+
						"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						INotifierCallback test = (INotifierCallback) loaderOpenSPCoop.newInstance(tipoClass);
						test.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.notifierCallback'=...,"+tipoClass+
								"'. \n La classe non esiste: "+e.getMessage(),e);
						return false;
					} 
				}
			}
			
			// FormUrlEncodedFilter
			this.isFormUrlEncodedFilterEnabled();
			
			// Monitor SDK
			this.getMonitorSDK_repositoryJars();
			
			// JminixConsole
			this.getPortJminixConsole();
			
			// Custom Contexts
			this.isEnabledFunctionPD();
			this.isEnabledFunctionPDtoSOAP();
			this.isEnabledFunctionPA();
			this.getCustomContexts();
			
			// Custom Container
			if ( this.getRealContainerCustom() != null ){
				String tipo = this.getRealContainerCustom();
				// Check tipi registrati
				String tipoClass = className.getRealmContainerCustom(tipo);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.realmContainer.custom'="+tipo+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					IAutorizzazioneSecurityContainer test = (IAutorizzazioneSecurityContainer) loaderOpenSPCoop.newInstance(tipoClass);
					test.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.realmContainer.custom'="+tipoClass+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				} 
			}
			
			// ExtendedInfo (Configurazione)
			String extendedInfoConfigurazione = null;
			try{
				extendedInfoConfigurazione = this.getExtendedInfoConfigurazione();
			}catch(Exception e){
				return false; // log registrato nel metodo
			}
			if(extendedInfoConfigurazione!=null){
				try{
					IExtendedInfo test = (IExtendedInfo) loaderOpenSPCoop.newInstance(extendedInfoConfigurazione);
					test.toString();
				}catch(Exception e){
					this.log.error("La classe ["+extendedInfoConfigurazione+"], indicata nella proprieta' 'org.openspcoop2.pdd.config.extendedInfo.configurazione', non esiste: "+e.getMessage(),e);
					return false;
				} 
			}
			
			// ExtendedInfo (PortaDelegata)
			String extendedInfoPortaDelegata = null;
			try{
				extendedInfoPortaDelegata = this.getExtendedInfoPortaDelegata();
			}catch(Exception e){
				return false; // log registrato nel metodo
			}
			if(extendedInfoPortaDelegata!=null){
				try{
					IExtendedInfo test = (IExtendedInfo) loaderOpenSPCoop.newInstance(extendedInfoPortaDelegata);
					test.toString();
				}catch(Exception e){
					this.log.error("La classe ["+extendedInfoPortaDelegata+"], indicata nella proprieta' 'org.openspcoop2.pdd.config.extendedInfo.portaDelegata', non esiste: "+e.getMessage(),e);
					return false;
				} 
			}
			
			// ExtendedInfo (PortaApplicativa)
			String extendedInfoPortaApplicativa = null;
			try{
				extendedInfoPortaApplicativa = this.getExtendedInfoPortaApplicativa();
			}catch(Exception e){
				return false; // log registrato nel metodo
			}
			if(extendedInfoPortaApplicativa!=null){
				try{
					IExtendedInfo test = (IExtendedInfo) loaderOpenSPCoop.newInstance(extendedInfoPortaApplicativa);
					test.toString();
				}catch(Exception e){
					this.log.error("La classe ["+extendedInfoPortaApplicativa+"], indicata nella proprieta' 'org.openspcoop2.pdd.config.extendedInfo.portaApplicativa', non esiste: "+e.getMessage(),e);
					return false;
				} 
			}
			
			// ValidazioneContenutiApplicativi
			this.isValidazioneContenutiApplicativi_debug();
			this.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione();
			this.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione();
			this.isValidazioneContenutiApplicativi_checkSoapAction();
			this.getValidazioneContenutiApplicativi_json_policyAdditionalProperties();
			this.getValidazioneContenutiApplicativi_openApi_jsonValidator();
			this.isValidazioneContenutiApplicativi_openApi_useOpenApi4j();
			this.isValidazioneContenutiApplicativi_openApi_openApi4j_validateAPISpec();
			this.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestQuery();
			this.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestHeaders();
			this.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestCookie();
			this.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestBody();
			this.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseHeaders();
			this.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseBody();
			
			// CachingResponse
			this.getCachingResponseDigestAlgorithm();
			this.getCachingResponseHeaderCacheKey();
			
			// Gestione Token
			this.getGestioneToken_iatTimeCheck_milliseconds();
			this.isGestioneToken_saveSourceTokenInfo();
			this.getGestioneTokenFormatDate();
			this.getGestioneTokenHeaderTrasportoJSON();
			this.getGestioneTokenHeaderTrasportoJWT();
			if(this.checkTipiIntegrazioneGestioneToken() == false) {
				return false;
			}
			List<String> listCustomClaims = getCustomClaimsKeys_gestioneTokenForward();
			if(listCustomClaims!=null && !listCustomClaims.isEmpty()) {
				for (String claim : listCustomClaims) {
					// in caso non siano presenti viene sollevata una eccezione
					this.getCustomClaimsName_gestioneTokenHeaderIntegrazione(claim);
					this.getCustomClaimsHeaderName_gestioneTokenHeaderIntegrazioneTrasporto(claim);
					this.getCustomClaimsKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneTrasporto(claim);
					this.getCustomClaimsKeyPASetEnabled_gestioneTokenHeaderIntegrazioneTrasporto(claim);
					this.getCustomClaimsJsonPropertyName_gestioneTokenHeaderIntegrazioneJson(claim);
					this.getCustomClaimsKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneJson(claim);
					this.getCustomClaimsKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson(claim);
				}
			}
			this.getGestioneTokenHeaderIntegrazioneTrasporto_audienceSeparator();
			this.getGestioneTokenHeaderIntegrazioneTrasporto_scopeSeparator();
			this.getGestioneTokenHeaderIntegrazioneTrasporto_roleSeparator();
			
			// Statistiche via jmx Console
			this.isStatisticheViaJmx();
			
			// Forward Proxy
			this.isForwardProxyEnable();
			this.getForwardProxyConfigurazioneDefault();
			
			// Trasporto REST / SOAP
			
			this.getSOAPServicesUrlParametersForwardConfig();
			this.getSOAPServicesHeadersForwardConfig(true);
			this.getSOAPServicesHeadersForwardConfig(false);
			
			this.getRESTServicesUrlParametersForwardConfig();
			this.getRESTServicesHeadersForwardConfig(true);
			this.getRESTServicesHeadersForwardConfig(false);
			
			this.isRESTServices_inoltroBuste_proxyPassReverse();
			this.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse();
			this.isRESTServices_inoltroBuste_proxyPassReverse_useProtocolPrefix();
			this.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix();
			this.getRESTServices_inoltroBuste_proxyPassReverse_headers();
			this.getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_headers();
						
			// Transazioni
			if(this.isTransazioniEnabled()) {
				this.isTransazioniDebug();
				if(this.isTransazioniUsePddRuntimeDatasource()==false) {
					this.getTransazioniDatasource();
					this.getTransazioniDatasourceJndiContext();
					this.isTransazioniDatasourceUseDBUtils();
				}
				this.isTransazioniSaveTracceInUniqueTransaction();
				this.isTransazioniSaveDiagnosticiInUniqueTransaction();
				this.isTransazioniSaveDumpInUniqueTransaction();
				this.isTransazioniFaultPrettyPrint();
				
				this._isTransazioniFiltroDuplicatiSaveDateEnabled();
				if(this.isTransazioniFiltroDuplicatiTramiteTransazioniEnabled()) {
					this.isTransazioniFiltroDuplicatiTramiteTransazioniUsePdDConnection();
					this.isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex();
				}
				
				if(this.isTransazioniStatefulEnabled()) {
					this.isTransazioniStatefulDebug();
					this.getTransazioniStatefulTimerIntervalSeconds();
				}
				
				this.getTransazioniCredenzialiMittenteMaxLength();
				this.getTransazioniCredenzialiMittenteLifeSeconds();
				this.isTransazioniRegistrazioneTracceProtocolPropertiesEnabled();
				this.isTransazioniRegistrazioneTracceHeaderRawEnabled();
				this.isTransazioniRegistrazioneTracceDigestEnabled();
				this._getTransazioniRegistrazioneTracceManager(className, loaderOpenSPCoop);
				this._getTransazioniRegistrazioneDiagnosticiManager(className, loaderOpenSPCoop);
				this.isTransazioniRegistrazioneDumpHeadersCompactEnabled();
				
				if(this.isTransazioniFileTraceEnabled()) {
					this.isTransazioniFileTraceDumpBinarioPDEnabled();
					this.isTransazioniFileTraceDumpBinarioPDConnettoreEnabled();
					this.isTransazioniFileTraceDumpBinarioPAEnabled();
					this.isTransazioniFileTraceDumpBinarioPAConnettoreEnabled();
					this.getTransazioniFileTraceConfig();
				}
			}
			
			// Eventi
			if(this.isEventiEnabled()) {
				this.isEventiDebug();
				this.isEventiRegistrazioneStatoPorta();
				if(this.isEventiTimerEnabled()) {
					this.getEventiTimerIntervalSeconds();
				}
			}
			
			// FileSystemRecovery
			this.getFileSystemRecovery_repository();
			this.isFileSystemRecoveryDebug();
			if(this.isFileSystemRecoveryTimerEnabled()) {
				this.getFileSystemRecoveryTimerIntervalSeconds();
				this.getFileSystemRecoveryMaxAttempts();
				this.isFileSystemRecoveryTimerEventEnabled();
				this.isFileSystemRecoveryTimerTransactionEnabled();
			}
			
			// ControlloTraffico
			if(this.isControlloTrafficoEnabled()) {
				//this.initConfigurazioneControlloTraffico(loaderOpenSPCoop); invocato da OpenSPcoop2Startup
				TipoGestorePolicy tipo = this.getControlloTrafficoGestorePolicyTipo();
				if(TipoGestorePolicy.WS.equals(tipo)) {
					this.getControlloTrafficoGestorePolicyWSUrl();
				}
				this.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository();
				
				// header limit
				this.getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit();
				this.getControlloTrafficoNumeroRichiesteHeaderLimit();
				this.getControlloTrafficoOccupazioneBandaHeaderLimit();
				this.getControlloTrafficoTempoComplessivoRispostaHeaderLimit();
				this.getControlloTrafficoTempoMedioRispostaHeaderLimit();
				this.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit();
				this.getControlloTrafficoNumeroRichiesteFalliteHeaderLimit();
				this.getControlloTrafficoNumeroFaultApplicativiHeaderLimit();
				this.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit();
				
				// header remaining
				this.getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining();
				this.getControlloTrafficoNumeroRichiesteHeaderRemaining();
				this.getControlloTrafficoOccupazioneBandaHeaderRemaining();
				this.getControlloTrafficoTempoComplessivoRispostaHeaderRemaining();
				this.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining();
				this.getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining();
				this.getControlloTrafficoNumeroFaultApplicativiHeaderRemaining();
				this.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining();
				
				// header reset
				this.getControlloTrafficoNumeroRichiesteHeaderReset();
				this.getControlloTrafficoOccupazioneBandaHeaderReset();
				this.getControlloTrafficoTempoComplessivoRispostaHeaderReset();
				this.getControlloTrafficoTempoMedioRispostaHeaderReset();
				this.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset();
				this.getControlloTrafficoNumeroRichiesteFalliteHeaderReset();
				this.getControlloTrafficoNumeroFaultApplicativiHeaderReset();
				this.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset();
				
				this.getControlloTrafficoRetryAfterHeader();
				this.getControlloTrafficoRetryAfterHeader_randomBackoff();
			}
			
			// Service Unavailable
			this.getServiceUnavailableRetryAfterSeconds_pd();
			this.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd();
			this.getServiceUnavailableRetryAfterSeconds_pa();
			this.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa();
			if(this.isEnabledServiceUnavailableRetryAfter_pd_suspend()) {
				this.getServiceUnavailableRetryAfterSeconds_pd_suspend();
				this.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend();
			}
			if(this.isEnabledServiceUnavailableRetryAfter_pa_suspend()) {
				this.getServiceUnavailableRetryAfterSeconds_pa_suspend();
				this.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend();
			}
			if(this.isEnabledServiceUnavailableRetryAfter_pd_connectionFailed()) {
				this.getServiceUnavailableRetryAfterSeconds_pd_connectionFailed();
				this.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_connectionFailed();
			}
			if(this.isEnabledServiceUnavailableRetryAfter_pa_connectionFailed()) {
				this.getServiceUnavailableRetryAfterSeconds_pa_connectionFailed();
				this.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_connectionFailed();
			}
			this.isServiceUnavailable_ReadTimedOut("");
			
			// Statistiche
			if(this.isStatisticheGenerazioneEnabled()) {
				if(this.isStatisticheUsePddRuntimeDatasource()==false &&
						this.isStatisticheUseTransazioniDatasource()==false) {
					this.getStatisticheDatasource();
					this.getStatisticheDatasourceJndiContext();
					this.isStatisticheDatasourceUseDBUtils();
				}
				if(this.isStatisticheGenerazioneCustomEnabled()) {
					this.isStatisticheGenerazioneCustomSdkEnabled();
				}
				if(this.isStatisticheGenerazioneBaseOrariaEnabled()) {
					this.isStatisticheGenerazioneBaseOrariaEnabledUltimaOra();
				}
				if(this.isStatisticheGenerazioneBaseGiornalieraEnabled()) {
					this.isStatisticheGenerazioneBaseGiornalieraEnabledUltimoGiorno();
				}
				if(this.isStatisticheGenerazioneBaseSettimanaleEnabled()) {
					this.isStatisticheGenerazioneBaseSettimanaleEnabledUltimaSettimana();
				}
				if(this.isStatisticheGenerazioneBaseMensileEnabled()) {
					this.isStatisticheGenerazioneBaseMensileEnabledUltimoMese();
				}
				this.getStatisticheOrarieGenerazioneTimerIntervalSeconds();
				this.getStatisticheGiornaliereGenerazioneTimerIntervalSeconds();
				this.getStatisticheSettimanaliGenerazioneTimerIntervalSeconds();
				this.getStatisticheMensiliGenerazioneTimerIntervalSeconds();
				this.getStatisticheGenerazioneTimer_lockMaxLife();
				this.getStatisticheGenerazioneTimer_lockIdleTime();
				this.getStatisticheGenerazioneExternalForceIndexRepository();
			}
			
			return true;

		}catch(java.lang.Exception e) {
			this.log.error("Riscontrato errore durante la validazione lettura della proprieta' di openspcoop: "+e.getMessage(),e);
			return false;
		}
	}


	private boolean _validateHandlersBuiltIn(ClassNameProperties className, Loader loaderOpenSPCoop) {
		// InitHandlerBuiltIn
		if ( this.getInitHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getInitHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getInitHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.init'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					InitHandler handler = (InitHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.init'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		
		// ExitHandlerBuiltIn
		if ( this.getExitHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getExitHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getExitHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.exit'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					ExitHandler handler = (ExitHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.exit'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		
		// PreInRequestHandlerBuiltIn
		if ( this.getPreInRequestHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getPreInRequestHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getPreInRequestHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.pre-in-request'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					PreInRequestHandler handler = (PreInRequestHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.pre-in-request'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// InRequestHandlerBuiltIn
		if ( this.getInRequestHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getInRequestHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getInRequestHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.in-request'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					InRequestHandler handler = (InRequestHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.in-request'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// InRequestProtocolHandlerBuiltIn
		if ( this.getInRequestProtocolHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getInRequestProtocolHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getInRequestProtocolHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.in-protocol-request'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					InRequestProtocolHandler handler = (InRequestProtocolHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.in-protocol-request'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// OutRequestHandlerBuiltIn
		if ( this.getOutRequestHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getOutRequestHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getOutRequestHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.out-request'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					OutRequestHandler handler = (OutRequestHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.out-request'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// PostOutRequestHandlerBuiltIn
		if ( this.getPostOutRequestHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getPostOutRequestHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getPostOutRequestHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.post-out-request'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					PostOutRequestHandler handler = (PostOutRequestHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.post-out-request'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// PreInResponseHandlerBuiltIn
		if ( this.getPreInResponseHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getPreInResponseHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getPreInResponseHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.pre-in-response'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					PreInResponseHandler handler = (PreInResponseHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.pre-in-response'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// InResponseHandlerBuiltIn
		if ( this.getInResponseHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getInResponseHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getInResponseHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.in-response'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					InResponseHandler handler = (InResponseHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.in-response'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// OutResponseHandlerBuiltIn
		if ( this.getOutResponseHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getOutResponseHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getOutResponseHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.out-response'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					OutResponseHandler handler = (OutResponseHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.out-response'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// PostOutResponseHandlerBuiltIn
		if ( this.getPostOutResponseHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getPostOutResponseHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getPostOutResponseHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.post-out-response'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					PostOutResponseHandler handler = (PostOutResponseHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.built-in.post-out-response'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// IntegrationManagerRequestHandlerBuiltIn
		if ( this.getIntegrationManagerRequestHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getIntegrationManagerRequestHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getIntegrationManagerRequestHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrationManager.handler.built-in.request'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					IntegrationManagerRequestHandler handler = (IntegrationManagerRequestHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrationManager.handler.built-in.request'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// IntegrationManagerResponseHandlerBuiltIn
		if ( this.getIntegrationManagerResponseHandlerBuiltIn() != null ){
			String[] tipiHandlerBuiltIn = this.getIntegrationManagerResponseHandlerBuiltIn();
			// Check tipi registrati
			for(int i=0; i<tipiHandlerBuiltIn.length;i++){
				String tipoClass = className.getIntegrationManagerResponseHandlerBuiltIn(tipiHandlerBuiltIn[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrationManager.handler.built-in.response'=...,"+tipiHandlerBuiltIn[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					IntegrationManagerResponseHandler handler = (IntegrationManagerResponseHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrationManager.handler.built-in.response'=...,"+tipiHandlerBuiltIn[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		return true;
	}
	
	
	private boolean _validateHandlers(ClassNameProperties className, Loader loaderOpenSPCoop) {
		// InitHandler
		if ( this.getInitHandler() != null ){
			String[] tipiHandler = this.getInitHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getInitHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.init'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					InitHandler handler = (InitHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.init'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		
		// ExitHandler
		if ( this.getExitHandler() != null ){
			String[] tipiHandler = this.getExitHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getExitHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.exit'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					ExitHandler handler = (ExitHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.exit'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		
		// PreInRequestHandler
		if ( this.getPreInRequestHandler() != null ){
			String[] tipiHandler = this.getPreInRequestHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getPreInRequestHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.pre-in-request'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					PreInRequestHandler handler = (PreInRequestHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.pre-in-request'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// InRequestHandler
		if ( this.getInRequestHandler() != null ){
			String[] tipiHandler = this.getInRequestHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getInRequestHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.in-request'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					InRequestHandler handler = (InRequestHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.in-request'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// InRequestProtocolHandler
		if ( this.getInRequestProtocolHandler() != null ){
			String[] tipiHandler = this.getInRequestProtocolHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getInRequestProtocolHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.in-protocol-request'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					InRequestProtocolHandler handler = (InRequestProtocolHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.in-protocol-request'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// OutRequestHandler
		if ( this.getOutRequestHandler() != null ){
			String[] tipiHandler = this.getOutRequestHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getOutRequestHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.out-request'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					OutRequestHandler handler = (OutRequestHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.out-request'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// PostOutRequestHandler
		if ( this.getPostOutRequestHandler() != null ){
			String[] tipiHandler = this.getPostOutRequestHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getPostOutRequestHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.post-out-request'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					PostOutRequestHandler handler = (PostOutRequestHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.post-out-request'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// PreInResponseHandler
		if ( this.getPreInResponseHandler() != null ){
			String[] tipiHandler = this.getPreInResponseHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getPreInResponseHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.pre-in-response'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					PreInResponseHandler handler = (PreInResponseHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.pre-in-response'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// InResponseHandler
		if ( this.getInResponseHandler() != null ){
			String[] tipiHandler = this.getInResponseHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getInResponseHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.in-response'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					InResponseHandler handler = (InResponseHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.in-response'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// OutResponseHandler
		if ( this.getOutResponseHandler() != null ){
			String[] tipiHandler = this.getOutResponseHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getOutResponseHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.out-response'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					OutResponseHandler handler = (OutResponseHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.out-response'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// PostOutResponseHandler
		if ( this.getPostOutResponseHandler() != null ){
			String[] tipiHandler = this.getPostOutResponseHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getPostOutResponseHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.post-out-response'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					PostOutResponseHandler handler = (PostOutResponseHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.handler.post-out-response'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// IntegrationManagerRequestHandler
		if ( this.getIntegrationManagerRequestHandler() != null ){
			String[] tipiHandler = this.getIntegrationManagerRequestHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getIntegrationManagerRequestHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrationManager.handler.request'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					IntegrationManagerRequestHandler handler = (IntegrationManagerRequestHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrationManager.handler.request'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		// IntegrationManagerResponseHandler
		if ( this.getIntegrationManagerResponseHandler() != null ){
			String[] tipiHandler = this.getIntegrationManagerResponseHandler();
			// Check tipi registrati
			for(int i=0; i<tipiHandler.length;i++){
				String tipoClass = className.getIntegrationManagerResponseHandler(tipiHandler[i]);
				if(tipoClass == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrationManager.handler.response'=...,"+tipiHandler[i]+
					"'. \n Il tipo non esiste nelle classi registrate in OpenSPCoop");
					return false;
				}
				try{
					IntegrationManagerResponseHandler handler = (IntegrationManagerResponseHandler) loaderOpenSPCoop.newInstance(tipoClass);
					handler.toString();
				}catch(Exception e){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrationManager.handler.response'=...,"+tipiHandler[i]+
							"'. \n La classe non esiste: "+e.getMessage(),e);
					return false;
				}
			}
		}
		return true;
	}


	public List<String> getKeywordsIntegrazioneGestioneToken(){
		List<String> keywords = new ArrayList<>();
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_PROCESS_TIME);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUER);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SUBJECT);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_USERNAME);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_AUDIENCE);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_CLIENT_ID);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ISSUED_AT);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EXPIRED);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_NBF);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_ROLES);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_SCOPES);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FULL_NAME);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FIRST_NAME);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_MIDDLE_NAME);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_FAMILY_NAME);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TOKEN_EMAIL);
		return keywords;
	}

	private boolean checkTipiIntegrazioneGestioneToken(){
		
		java.util.concurrent.ConcurrentHashMap<?,?> prop = this.getKeyValue_gestioneTokenHeaderIntegrazioneTrasporto();
		if ( prop == null ){
			this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.keyword.*'.");
			return false;
		}
		
		java.util.concurrent.ConcurrentHashMap<String, Boolean> propSetPD_trasporto = null;
		try {
			propSetPD_trasporto = this.getKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneTrasporto();
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.pd.set.enabled.*'.");
			return false;
		}
		
		java.util.concurrent.ConcurrentHashMap<String, Boolean> propSetPD_json = null;
		try {
			propSetPD_json = this.getKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneJson();
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.gestioneToken.forward.json.pd.set.enabled.*'.");
			return false;
		}
		
		java.util.concurrent.ConcurrentHashMap<String, Boolean> propSetPA_trasporto = null;
		try {
			propSetPA_trasporto = this.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneTrasporto();
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.pa.set.enabled.*'.");
			return false;
		}
		
		java.util.concurrent.ConcurrentHashMap<String, Boolean> propSetPA_json = null;
		try {
			propSetPA_json = this.getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson();
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.gestioneToken.forward.json.pa.set.enabled.*'.");
			return false;
		}
		
		List<String> keywords = this.getKeywordsIntegrazioneGestioneToken();
		for (String keyword : keywords) {
			if( prop.get(keyword) == null){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.keyword."+
						keyword+"'.");
				return false;
			}
			if( propSetPD_trasporto.containsKey(keyword) == false){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.pd.set.enabled."+
						keyword+"'.");
				return false;
			}
			if( propSetPD_json.containsKey(keyword) == false){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.gestioneToken.forward.json.pd.set.enabled."+
						keyword+"'.");
				return false;
			}
			if( propSetPA_trasporto.containsKey(keyword) == false){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.pa.set.enabled."+
						keyword+"'.");
				return false;
			}
			if( propSetPA_json.containsKey(keyword) == false){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.gestioneToken.forward.json.pa.set.enabled."+
						keyword+"'.");
				return false;
			}
		}
				

		return true;
	}

	public List<String> getKeywordsIntegrazione(){
		List<String> keywords = new ArrayList<>();
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_MITTENTE);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_MITTENTE);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_DESTINATARIO);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_DESTINATARIO);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_SERVIZIO);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_VERSIONE_SERVIZIO);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_AZIONE);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_ID_MESSAGGIO);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_COLLABORAZIONE);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO_APPLICATIVO);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_ID_TRANSAZIONE);
		keywords.add(CostantiPdD.HEADER_INTEGRAZIONE_PROTOCOL_INFO);
		return keywords;
	}

	private boolean checkTipiIntegrazione(String[] tipiIntegrazione){
		// Check KeyWord per tipi 'trasporto' e 'urlBased' e 'soap'
		for(int i=0; i<tipiIntegrazione.length;i++){
			if(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO.equals(tipiIntegrazione[i]) ||
					CostantiConfigurazione.HEADER_INTEGRAZIONE_URL_BASED.equals(tipiIntegrazione[i]) ||
							CostantiConfigurazione.HEADER_INTEGRAZIONE_SOAP.equals(tipiIntegrazione[i]) ){
				
				java.util.concurrent.ConcurrentHashMap<?,?> propGovWay = null;
				java.util.concurrent.ConcurrentHashMap<?,?> propOpenSPCoop2 = null;
				java.util.concurrent.ConcurrentHashMap<?,?> propOpenSPCoop1 = null;
				java.util.concurrent.ConcurrentHashMap<String, Boolean> propSetRequestPD = null;
				java.util.concurrent.ConcurrentHashMap<String, Boolean> propSetResponsePD = null;
				java.util.concurrent.ConcurrentHashMap<String, Boolean> propReadPD = null;
				java.util.concurrent.ConcurrentHashMap<String, Boolean> propSetRequestPA = null;
				java.util.concurrent.ConcurrentHashMap<String, Boolean> propSetResponsePA = null;
				java.util.concurrent.ConcurrentHashMap<String, Boolean> propReadPA = null;
				String tipo = "";
				if(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO.equals(tipiIntegrazione[i])){
					if ( this.getKeyValue_HeaderIntegrazioneTrasporto() == null ){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.trasporto.keyword.*'.");
						return false;
					}
					propGovWay = this.getKeyValue_HeaderIntegrazioneTrasporto();
					propOpenSPCoop2 = this.getKeyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop2();
					propOpenSPCoop1 = this.getKeyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop1();
					try {
						propSetRequestPD = this.getKeyPDSetEnabled_HeaderIntegrazioneTrasporto(true);
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.trasporto.pd.set.request.enabled.*'.");
						return false;
					}
					try {
						propSetResponsePD = this.getKeyPDSetEnabled_HeaderIntegrazioneTrasporto(false);
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.trasporto.pd.set.response.enabled.*'.");
						return false;
					}
					try {
						propReadPD = this.getKeyPDReadEnabled_HeaderIntegrazioneTrasporto();
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.trasporto.pd.read.enabled.*'.");
						return false;
					}
					try {
						propSetRequestPA = this.getKeyPASetEnabled_HeaderIntegrazioneTrasporto(true);
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.trasporto.pa.set.request.enabled.*'.");
						return false;
					}
					try {
						propSetResponsePA = this.getKeyPASetEnabled_HeaderIntegrazioneTrasporto(false);
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.trasporto.pa.set.response.enabled.*'.");
						return false;
					}
					try {
						propReadPA = this.getKeyPAReadEnabled_HeaderIntegrazioneTrasporto();
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.trasporto.pa.read.enabled.*'.");
						return false;
					}
					tipo=CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO;
				}else if(CostantiConfigurazione.HEADER_INTEGRAZIONE_URL_BASED.equals(tipiIntegrazione[i])){
					if ( this.getKeyValue_HeaderIntegrazioneUrlBased() == null ){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.urlBased.keyword.*'.");
						return false;
					}
					propGovWay = this.getKeyValue_HeaderIntegrazioneUrlBased();
					propOpenSPCoop2 = this.getKeyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop2();
					propOpenSPCoop1 = this.getKeyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop1();
					try {
						propSetRequestPD = this.getKeyPDSetEnabled_HeaderIntegrazioneUrlBased();
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.urlBased.pd.set.enabled.*'.");
						return false;
					}
					try {
						propReadPD = this.getKeyPDReadEnabled_HeaderIntegrazioneUrlBased();
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.urlBased.pd.read.enabled.*'.");
						return false;
					}
					try {
						propSetRequestPA = this.getKeyPASetEnabled_HeaderIntegrazioneUrlBased();
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.urlBased.pa.set.enabled.*'.");
						return false;
					}
					try {
						propReadPA = this.getKeyPAReadEnabled_HeaderIntegrazioneUrlBased();
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.urlBased.pa.read.enabled.*'.");
						return false;
					}
					tipo=CostantiConfigurazione.HEADER_INTEGRAZIONE_URL_BASED;
				}else if(CostantiConfigurazione.HEADER_INTEGRAZIONE_SOAP.equals(tipiIntegrazione[i])){
					if ( this.getKeyValue_HeaderIntegrazioneSoap() == null ){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.soap.keyword.*'.");
						return false;
					}
					propGovWay = this.getKeyValue_HeaderIntegrazioneSoap();
					propOpenSPCoop2 = this.getKeyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop2();
					propOpenSPCoop1 = this.getKeyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop1();
					try {
						propSetRequestPD = this.getKeyPDSetEnabled_HeaderIntegrazioneSoap(true);
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.soap.pd.set.request.enabled.*'.");
						return false;
					}
					try {
						propSetResponsePD = this.getKeyPDSetEnabled_HeaderIntegrazioneSoap(false);
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.soap.pd.set.response.enabled.*'.");
						return false;
					}
					try {
						propReadPD = this.getKeyPDReadEnabled_HeaderIntegrazioneSoap();
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.soap.pd.read.enabled.*'.");
						return false;
					}
					try {
						propSetRequestPA = this.getKeyPASetEnabled_HeaderIntegrazioneSoap(true);
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.soap.pa.set.request.enabled.*'.");
						return false;
					}
					try {
						propSetResponsePA = this.getKeyPASetEnabled_HeaderIntegrazioneSoap(false);
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.soap.pa.set.response.enabled.*'.");
						return false;
					}
					try {
						propReadPA = this.getKeyPAReadEnabled_HeaderIntegrazioneSoap();
					}catch(Exception e) {
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.soap.pa.read.enabled.*'.");
						return false;
					}
					tipo=CostantiConfigurazione.HEADER_INTEGRAZIONE_SOAP;
				}
				
				List<String> keywords = this.getKeywordsIntegrazione();
				for (String keyword : keywords) {
					
					if(CostantiPdD.HEADER_INTEGRAZIONE_PROTOCOL_INFO.equals(keyword)) {
						continue; // gestito sotto come caso eccezionale
					}
					
					if( propGovWay.get(keyword) == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+
								keyword+"'.");
						return false;
					}
					if( propOpenSPCoop2.get(keyword) == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop2."+tipo+".keyword."+
								keyword+"'.");
						return false;
					}
					if( propOpenSPCoop1.get(keyword) == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop1."+tipo+".keyword."+
								keyword+"'.");
						return false;
					}
					if(CostantiConfigurazione.HEADER_INTEGRAZIONE_URL_BASED.equals(tipiIntegrazione[i])){
						if( propSetRequestPD.containsKey(keyword) == false){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.enabled."+
									keyword+"'.");
							return false;
						}
					}
					else {
						if( propSetRequestPD.containsKey(keyword) == false){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.request.enabled."+
									keyword+"'.");
							return false;
						}
						if( propSetResponsePD.containsKey(keyword) == false){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.response.enabled."+
									keyword+"'.");
							return false;
						}
					}
					if( propReadPD.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.read.enabled."+
								keyword+"'.");
						return false;
					}
					if(CostantiConfigurazione.HEADER_INTEGRAZIONE_URL_BASED.equals(tipiIntegrazione[i])){
						if( propSetRequestPA.containsKey(keyword) == false){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.enabled."+
									keyword+"'.");
							return false;
						}
					}
					else {
						if( propSetRequestPA.containsKey(keyword) == false){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.request.enabled."+
									keyword+"'.");
							return false;
						}
						if( propSetResponsePA.containsKey(keyword) == false){
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.response.enabled."+
									keyword+"'.");
							return false;
						}
					}
					if( propReadPA.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.read.enabled."+
								keyword+"'.");
						return false;
					}
				}
				
				String keyword = CostantiPdD.HEADER_INTEGRAZIONE_INFO;
				if(CostantiConfigurazione.HEADER_INTEGRAZIONE_URL_BASED.equals(tipiIntegrazione[i])){
					if( propSetRequestPD.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetRequestPA.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.enabled."+
								keyword+"'.");
						return false;
					}
				}
				else {
					if( propSetRequestPD.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.request.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetResponsePD.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.response.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetRequestPA.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.request.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetResponsePA.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.response.enabled."+
								keyword+"'.");
						return false;
					}
				}
				
				if(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO.equals(tipo)) {
					keyword = CostantiPdD.HEADER_INTEGRAZIONE_USER_AGENT;
					if( propSetRequestPD.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.request.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetResponsePD.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.response.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetRequestPA.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.request.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetResponsePA.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.response.enabled."+
								keyword+"'.");
						return false;
					}
				}
				
				keyword = CostantiPdD.HEADER_INTEGRAZIONE_PROTOCOL_INFO;
				if(CostantiConfigurazione.HEADER_INTEGRAZIONE_URL_BASED.equals(tipiIntegrazione[i])){
					if( propSetRequestPD.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetRequestPA.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.enabled."+
								keyword+"'.");
						return false;
					}
				}
				else {
					if( propSetRequestPD.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.request.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetResponsePD.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pd.set.response.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetRequestPA.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.request.enabled."+
								keyword+"'.");
						return false;
					}
					if( propSetResponsePA.containsKey(keyword) == false){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".pa.set.response.enabled."+
								keyword+"'.");
						return false;
					}
				}
				
				//break;
			}
			
			
			if(CostantiConfigurazione.HEADER_INTEGRAZIONE_SOAP.equals(tipiIntegrazione[i])){
				if ( this.getHeaderSoapNameIntegrazione() == null ){
					return false;
				}
				if ( this.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop2() == null ){
					return false;
				}
				if ( this.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop1() == null ){
					return false;
				}
				
				if ( this.getHeaderSoapActorIntegrazione() == null ){
					return false;
				}
				if ( this.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() == null ){
					return false;
				}
				if ( this.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1() == null ){
					return false;
				}
				
				if ( this.getHeaderSoapPrefixIntegrazione() == null ){
					return false;
				}
				if ( this.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop2() == null ){
					return false;
				}
				if ( this.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop1() == null ){
					return false;
				}
				
				if ( this.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione() == null ){
					return false;
				}
				if ( this.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2() == null ){
					return false;
				}
				if ( this.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1() == null ){
					return false;
				}
				
				if ( this.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione() == null ){
					return false;
				}
				if ( this.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2() == null ){
					return false;
				}
				if ( this.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1() == null ){
					return false;
				}
			}
			
		}
		return true;
	}














	/* ********  CONF DIRECTORY  ******** */

	/**
	 * Restituisce la directory di configurazione di OpenSPCoop.
	 *
	 * @return la directory di configurazione di OpenSPCoop.
	 * 
	 */
	private static String rootDirectory = null;
	public String getRootDirectory() {	
		if(OpenSPCoop2Properties.rootDirectory==null){
			try{ 
				String root = null;
				root = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.confDirectory");

				if(root==null)
					throw new Exception("non definita");

				root = root.trim();

				if(root.endsWith(File.separator) == false)
					root = root + File.separator;

				OpenSPCoop2Properties.rootDirectory = root;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop, 'org.openspcoop2.pdd.confDirectory': "+e.getMessage(),e);
				OpenSPCoop2Properties.rootDirectory = null;
			}    
		}

		return OpenSPCoop2Properties.rootDirectory;
	}

	/**
	 * Restituisce L'indicazione se il server è un server J2EE o WEB
	 *
	 * @return indicazione se il server è un server J2EE o WEB
	 * 
	 */
	private static Boolean serverJ2EE = null;
	public Boolean isServerJ2EE() {	
		if(OpenSPCoop2Properties.serverJ2EE==null){
			try{ 
				String server = null;
				server = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.server");

				if(server==null)
					throw new Exception("non definita");

				server = server.trim();

				if(CostantiConfigurazione.SERVER_J2EE.equalsIgnoreCase(server)){
					OpenSPCoop2Properties.serverJ2EE = true;
				}else if(CostantiConfigurazione.SERVER_WEB.equalsIgnoreCase(server)){
					OpenSPCoop2Properties.serverJ2EE = false;
				}else{
					throw new Exception("Valore ["+server+"] non conosciuto");
				}

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop, 'org.openspcoop2.pdd.server': "+e.getMessage(),e);
				OpenSPCoop2Properties.serverJ2EE = null;
			}    
		}

		return OpenSPCoop2Properties.serverJ2EE;
	}

	private static Boolean getClassLoaderRead = null;
	private static String getClassLoader = null;
	public String getClassLoader(){

		if(OpenSPCoop2Properties.getClassLoaderRead==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.classLoader"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.getClassLoader = value;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.classLoader' non impostata, errore:"+e.getMessage(),e);
			}
			OpenSPCoop2Properties.getClassLoaderRead = true;
		}

		return OpenSPCoop2Properties.getClassLoader;
	}

	private static String productName = null;
	public String getProductName() {	
		if(OpenSPCoop2Properties.productName==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.productName");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.productName = v;
				}else{
					OpenSPCoop2Properties.productName = CostantiPdD.OPENSPCOOP2_PRODUCT;
				}
			}catch(java.lang.Exception e) {
				OpenSPCoop2Properties.productName = CostantiPdD.OPENSPCOOP2_PRODUCT;
			}    
		}
		return OpenSPCoop2Properties.productName;
	}
	
	private static String versione = null;
	public String getVersione() {	
		if(OpenSPCoop2Properties.versione==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.versione");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.versione = v;
				}else{
					OpenSPCoop2Properties.versione = getVersionePdD(null);
				}
			}catch(java.lang.Exception e) {
				OpenSPCoop2Properties.versione = CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
			}    
		}
		return OpenSPCoop2Properties.versione;
	}
	public static String getVersionePdD(OpenSPCoop2Properties properties) throws Exception{
		String versione = CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
		if(properties!=null){
			versione = properties.getPddDetailsForServices();
		}
		
		try {
			String version = VersionUtilities.readVersion();
			if(version!=null && !StringUtils.isEmpty(version)) {
				versione = version;
			}
		}catch(Exception e) {}
		
		String buildVersion = null;
		try {
			buildVersion = VersionUtilities.readBuildVersion();
		}catch(Exception e) {}
		if(buildVersion!=null) {
			versione = versione + " (build "+buildVersion+")";
		}
		return versione;
	}
	
	private static String details = null;
	public String getDetails() {	
		if(OpenSPCoop2Properties.details==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.details");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.details = v;
				}else{
					OpenSPCoop2Properties.details = CostantiPdD.OPENSPCOOP2_DETAILS;
				}
			}catch(java.lang.Exception e) {
				OpenSPCoop2Properties.details = CostantiPdD.OPENSPCOOP2_DETAILS;
			}    
		}
		return OpenSPCoop2Properties.details;
	}
	
	private static String getPddDetailsForLog = null;
	public String getPddDetailsForLog() {	
		if(OpenSPCoop2Properties.getPddDetailsForLog==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.log.details");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.getPddDetailsForLog = v;
				}else{
					OpenSPCoop2Properties.getPddDetailsForLog = getDefaultLogVersionDetails();
				}
			}catch(java.lang.Exception e) {
				OpenSPCoop2Properties.getPddDetailsForLog = getDefaultLogVersionDetails();
			}    
		}
		return OpenSPCoop2Properties.getPddDetailsForLog;
	}
	
	private String getDefaultLogVersionDetails() {
		String d = this.getDetails();
		if(d!=null && !"".equals(d))
			return this.getVersione()+" ("+d+")";
		else
			return this.getVersione();
	}

	private static String getPddDetailsForServices = null;
	public String getPddDetailsForServices() {	
		if(OpenSPCoop2Properties.getPddDetailsForServices==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.details");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.getPddDetailsForServices = v;
				}else{
					OpenSPCoop2Properties.getPddDetailsForServices = this.getVersione();
				}
			}catch(java.lang.Exception e) {
				OpenSPCoop2Properties.getPddDetailsForServices = this.getVersione();
			}    
		}
		return OpenSPCoop2Properties.getPddDetailsForServices;
	}
	
	private static StatoFunzionalitaConWarning getCheckOpenSPCoopHome = null;
	public StatoFunzionalitaConWarning getCheckOpenSPCoopHome() {	
		if(OpenSPCoop2Properties.getCheckOpenSPCoopHome==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.checkHomeProperty");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.getCheckOpenSPCoopHome = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString(v);
					if(OpenSPCoop2Properties.getCheckOpenSPCoopHome==null){
						throw new Exception("Valore inatteso: "+v);
					}
				}else{
					OpenSPCoop2Properties.getCheckOpenSPCoopHome = StatoFunzionalitaConWarning.DISABILITATO;
				}
			}catch(java.lang.Exception e) {
				e.printStackTrace(System.out);
				OpenSPCoop2Properties.getCheckOpenSPCoopHome = StatoFunzionalitaConWarning.DISABILITATO;
			}    
		}
		return OpenSPCoop2Properties.getCheckOpenSPCoopHome;
	}
	
	public void checkOpenSPCoopHome() throws Exception{
		if(!StatoFunzionalitaConWarning.DISABILITATO.equals(this.getCheckOpenSPCoopHome())){
			Exception e = null;
			boolean foundSystem = false;
			try{
				String dir = System.getProperty(CostantiPdD.OPENSPCOOP2_LOCAL_HOME);
				if(dir==null || "".equals(dir)){
					throw new Exception("Variabile java ["+CostantiPdD.OPENSPCOOP2_LOCAL_HOME+"] non trovata");
				}
				foundSystem = true;
				File fDir = new File(dir);
				if(fDir.exists()==false){
					throw new Exception("File ["+fDir.getAbsolutePath()+"] indicato nella variabile java ["+CostantiPdD.OPENSPCOOP2_LOCAL_HOME+"] non esiste");
				}
				if(fDir.isDirectory()==false){
					throw new Exception("File ["+fDir.getAbsolutePath()+"] indicato nella variabile java ["+CostantiPdD.OPENSPCOOP2_LOCAL_HOME+"] non è una directory");
				}
				if(fDir.canRead()==false){
					throw new Exception("File ["+fDir.getAbsolutePath()+"] indicato nella variabile java ["+CostantiPdD.OPENSPCOOP2_LOCAL_HOME+"] non è accessibile in lettura");
				}
			}catch(Exception eTh){
				e = eTh;
			}
			try{
				// NOTA: nel caricamento la variabile di sistema vince sulla variabile java
				String dir = System.getenv(CostantiPdD.OPENSPCOOP2_LOCAL_HOME);
				if(dir==null || "".equals(dir)){
					if(!foundSystem){
						throw new Exception("Ne variabile java ne variabile di sistema ["+CostantiPdD.OPENSPCOOP2_LOCAL_HOME+"] trovata");
					}
				}
				else{
					File fDir = new File(dir);
					if(fDir.exists()==false){
						throw new Exception("File ["+fDir.getAbsolutePath()+"] indicato nella variabile di sistema ["+CostantiPdD.OPENSPCOOP2_LOCAL_HOME+"] non esiste");
					}
					if(fDir.isDirectory()==false){
						throw new Exception("File ["+fDir.getAbsolutePath()+"] indicato nella variabile di sistema ["+CostantiPdD.OPENSPCOOP2_LOCAL_HOME+"] non è una directory");
					}
					if(fDir.canRead()==false){
						throw new Exception("File ["+fDir.getAbsolutePath()+"] indicato nella variabile di sistema ["+CostantiPdD.OPENSPCOOP2_LOCAL_HOME+"] non è accessibile in lettura");
					}
					// trovata.
					// annullo una eventuale eccezione di sistema
					e = null;
				}
			}catch(Exception eTh){
				if(e==null)
					e = eTh;
				else{
					e = new Exception(e.getMessage()+" - "+eTh.getMessage(),eTh);
				}
			}
			if(e!=null){
				throw e;
			}
		}
	}
	
	




	/* ********  CONFIGURAZIONE DI OPENSPCOOP  ******** */

	/**
	 * Restituisce la location della configurazione della porta di dominio di OpenSPCoop,
	 *
	 * @return il path del file di configurazione della porta di dominio in caso di ricerca con successo, null altrimenti.
	 * 
	 */
	private static String pathConfigurazionePDD = null;
	public String getPathConfigurazionePDD() {	
		if(OpenSPCoop2Properties.pathConfigurazionePDD==null){
			try{  
				String indirizzo = this.reader.getValue("org.openspcoop2.pdd.config.location"); 
				if(indirizzo==null)
					throw new Exception("non definita");

				indirizzo = indirizzo.trim();

				if(CostantiConfigurazione.CONFIGURAZIONE_XML.equalsIgnoreCase(getTipoConfigurazionePDD())){

					if( (indirizzo.startsWith("http://")==false) && (indirizzo.startsWith("file://")==false) ){
						if(indirizzo.startsWith("${")==false){
							String root = getRootDirectory();
							indirizzo = root+indirizzo;
						}
						if(indirizzo.indexOf("${")!=-1){
							while (indirizzo.indexOf("${")!=-1){
								int indexStart = indirizzo.indexOf("${");
								int indexEnd = indirizzo.indexOf("}");
								if(indexEnd==-1){
									throw new Exception("errore durante l'interpretazione del path ["+indirizzo+"]: ${ utilizzato senza la rispettiva chiusura }");
								}
								String nameSystemProperty = indirizzo.substring(indexStart+"${".length(),indexEnd);
								String valueSystemProperty = System.getProperty(nameSystemProperty);
								if(valueSystemProperty==null){
									throw new Exception("errore durante l'interpretazione del path ["+indirizzo+"]: variabile di sistema ${"+nameSystemProperty+"} non esistente");
								}
								indirizzo = indirizzo.replace("${"+nameSystemProperty+"}", valueSystemProperty);
							}
						}
					}

				}

				OpenSPCoop2Properties.pathConfigurazionePDD = indirizzo;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.config.location': "+e.getMessage(),e);
				OpenSPCoop2Properties.pathConfigurazionePDD = null;
			}
		}

		return OpenSPCoop2Properties.pathConfigurazionePDD;
	} 

	private static List<byte[]> configPreLoadingLocale = null;
	private static Boolean configPreLoadingLocale_read = null;
	public List<byte[]> getConfigPreLoadingLocale() {	
		if(OpenSPCoop2Properties.configPreLoadingLocale_read==null){
			try{ 
				String resourceTmp = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.config.preLoading.locale");
				if(resourceTmp!=null){
					resourceTmp = resourceTmp.trim();
					
					List<String> lResources = new ArrayList<>();
					if(resourceTmp.contains(",")) {
						String [] tmp = resourceTmp.split(",");
						for (int i = 0; i < tmp.length; i++) {
							String r = tmp[i];
							if(r!=null) {
								r = r.trim();
								if(!"".equals(r)) {
									lResources.add(r);
								}
							}
						}
					}
					else {
						lResources.add(resourceTmp);
					}
					
					OpenSPCoop2Properties.configPreLoadingLocale = new ArrayList<>();
					
					for (String resource : lResources) {
						File f = new File(resource);
						if(f.exists()) {
							OpenSPCoop2Properties.configPreLoadingLocale.add(FileSystemUtilities.readBytesFromFile(f));
						}
						else {
							if(resource.startsWith("/")==false) {
								resource = "/" + resource;
							}
							InputStream is = OpenSPCoop2Properties.class.getResourceAsStream(resource);
							if(is!=null) {
								try {
									OpenSPCoop2Properties.configPreLoadingLocale.add(Utilities.getAsByteArray(is));
								}finally {
									try {
										is.close();
									}catch(Exception eClose) {}
								}
							}
						}	
					}
					
				}

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.config.preLoading.locale': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoConfigurazionePDD = null;
			}  
			
			OpenSPCoop2Properties.configPreLoadingLocale_read = true;
		}

		return OpenSPCoop2Properties.configPreLoadingLocale;
	}
	
	/**
	 * Restituisce il tipo di configurazione della porta di dominio di OpenSPCoop.
	 *
	 * @return il tipo di configurazione della porta di dominio, null altrimenti.
	 * 
	 */
	private static String tipoConfigurazionePDD = null;
	public String getTipoConfigurazionePDD() {	
		if(OpenSPCoop2Properties.tipoConfigurazionePDD==null){
			try{ 
				String tipo = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.config.tipo");
				if(tipo==null)
					throw new Exception("non definita");
				tipo = tipo.trim();

				OpenSPCoop2Properties.tipoConfigurazionePDD = tipo;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.config.tipo': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoConfigurazionePDD = null;
			}    
		}

		return OpenSPCoop2Properties.tipoConfigurazionePDD;
	}

	/**
	 * Restituisce l'indicazione Se si desidera condividere i due database config e regserv
	 *
	 * @return l'indicazione Se si desidera condividere i due database config e regserv
	 * 
	 */
	private static Boolean isCondivisioneConfigurazioneRegistroDB = null;
	public boolean isCondivisioneConfigurazioneRegistroDB() {	
		if(OpenSPCoop2Properties.isCondivisioneConfigurazioneRegistroDB==null){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.config.db.condivisioneDBRegserv");
				if(value==null)
					OpenSPCoop2Properties.isCondivisioneConfigurazioneRegistroDB = false;
				else{
					OpenSPCoop2Properties.isCondivisioneConfigurazioneRegistroDB = Boolean.parseBoolean(value);
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.config.db.condivisioneDBRegserv' (Viene utilizzato il default:false): "+e.getMessage(),e);
				OpenSPCoop2Properties.isCondivisioneConfigurazioneRegistroDB = false;
			}    
		}

		return OpenSPCoop2Properties.isCondivisioneConfigurazioneRegistroDB;
	}

	/**
	 * Restituisce le proprieta' da utilizzare con il contesto JNDI di lookup, se impostate.
	 *
	 * @return proprieta' da utilizzare con il contesto JNDI di lookup.
	 * 
	 */
	private static java.util.Properties jndiContext_Configurazione = null;
	public java.util.Properties getJNDIContext_Configurazione() {	
		if(OpenSPCoop2Properties.jndiContext_Configurazione==null){
			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.config.property.");
				OpenSPCoop2Properties.jndiContext_Configurazione = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI per la configurazione di openspcoop 'org.openspcoop2.pdd.config.property.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.jndiContext_Configurazione = null;
			}    
		}

		return OpenSPCoop2Properties.jndiContext_Configurazione;
	}

	
	private static AccessoConfigurazionePdD accessoConfigurazionePdD = null;
	public AccessoConfigurazionePdD getAccessoConfigurazionePdD() throws OpenSPCoop2ConfigurationException{ 
		if(OpenSPCoop2Properties.accessoConfigurazionePdD==null){
			try{  
				AccessoConfigurazionePdD conf = new AccessoConfigurazionePdD();
				conf.setTipo(this.getTipoConfigurazionePDD());
				if(CostantiConfigurazione.CONFIGURAZIONE_DB.equalsIgnoreCase(this.getTipoConfigurazionePDD())){	
					String tipoDatabase = null;
					String location = null;
					if(this.getPathConfigurazionePDD().indexOf("@")!=-1){
						// estrazione tipo database
						tipoDatabase = DBUtils.estraiTipoDatabaseFromLocation(this.getPathConfigurazionePDD());
						location = this.getPathConfigurazionePDD().substring(this.getPathConfigurazionePDD().indexOf("@")+1);
					}else{
						tipoDatabase = this.getDatabaseType();
						location =this.getPathConfigurazionePDD();
					}
					conf.setLocation(location);
					conf.setTipoDatabase(tipoDatabase);
				}else{
					conf.setLocation(this.getPathConfigurazionePDD());
				}
				conf.setContext(this.getJNDIContext_Configurazione());
				conf.setCondivisioneDatabasePddRegistro(this.isCondivisioneConfigurazioneRegistroDB());
				
				OpenSPCoop2Properties.accessoConfigurazionePdD = conf;
			}catch(java.lang.Exception e) {
				throw new OpenSPCoop2ConfigurationException("Riscontrato errore durante la lettura della modalita' di accesso alla configurazione della PdD OpenSPCoop",e);
			}
		}

		return OpenSPCoop2Properties.accessoConfigurazionePdD;
	}

	/**
	 * Restituisce l'indicazione se la configurazione di GovWay
	 * e' letta una sola volta (statica) o letta ai refresh della sorgente (dinamica) 
	 *
	 * @return Restituisce indicazione se la configurazione e' statica (false) o dinamica (true)
	 * 
	 */
	private static Boolean isConfigurazioneDinamica_value = null;
	public boolean isConfigurazioneDinamica(){
		if(OpenSPCoop2Properties.isConfigurazioneDinamica_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.config.refresh"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isConfigurazioneDinamica_value = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.config.refresh' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isConfigurazioneDinamica_value = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.config.refresh' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isConfigurazioneDinamica_value = true;
			}
		}

		return OpenSPCoop2Properties.isConfigurazioneDinamica_value;
	}
	
	private static Boolean isConfigurazioneCache_ConfigPrefill_value = null;
	public boolean isConfigurazioneCache_ConfigPrefill(){
		if(OpenSPCoop2Properties.isConfigurazioneCache_ConfigPrefill_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.cache.config.prefill"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isConfigurazioneCache_ConfigPrefill_value = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.cache.config.prefill' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isConfigurazioneCache_ConfigPrefill_value = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.cache.config.prefill' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isConfigurazioneCache_ConfigPrefill_value = false;
			}
		}

		return OpenSPCoop2Properties.isConfigurazioneCache_ConfigPrefill_value;
	}
	
	private static Boolean isConfigurazioneCache_RegistryPrefill_value = null;
	public boolean isConfigurazioneCache_RegistryPrefill(){
		if(OpenSPCoop2Properties.isConfigurazioneCache_RegistryPrefill_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.cache.registry.prefill"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isConfigurazioneCache_RegistryPrefill_value = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.cache.registry.prefill' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isConfigurazioneCache_RegistryPrefill_value = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.cache.registry.prefill' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isConfigurazioneCache_RegistryPrefill_value = false;
			}
		}

		return OpenSPCoop2Properties.isConfigurazioneCache_RegistryPrefill_value;
	}
	
	private static Boolean isConfigurazioneCache_accessiSynchronized = null;
	public boolean isConfigurazioneCache_accessiSynchronized(){
		if(OpenSPCoop2Properties.isConfigurazioneCache_accessiSynchronized==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.cache.get.synchronized"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isConfigurazioneCache_accessiSynchronized = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.cache.get.synchronized' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isConfigurazioneCache_accessiSynchronized = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.cache.get.synchronized' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isConfigurazioneCache_accessiSynchronized = false;
			}
		}

		return OpenSPCoop2Properties.isConfigurazioneCache_accessiSynchronized;
	}
	
	private static Boolean isConfigurazioneCache_transactionContext_accessiSynchronized = null;
	public boolean isConfigurazioneCache_transactionContext_accessiSynchronized(){
		if(OpenSPCoop2Properties.isConfigurazioneCache_transactionContext_accessiSynchronized==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.cache.transactionContext.synchronized"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isConfigurazioneCache_transactionContext_accessiSynchronized = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.cache.transactionContext.synchronized' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isConfigurazioneCache_transactionContext_accessiSynchronized = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.cache.transactionContext.synchronized' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isConfigurazioneCache_transactionContext_accessiSynchronized = true;
			}
		}

		return OpenSPCoop2Properties.isConfigurazioneCache_transactionContext_accessiSynchronized;
	}











	/* ********  DATASOURCE DI OPENSPCOOP  ******** */

	/**
	 * Restituisce il Nome JNDI del DataSource utilizzato da OpenSPCoop.
	 *
	 * @return il Nome JNDI del DataSource utilizzato da OpenSPCoop.
	 * 
	 */
	private static String jndiNameDatasource = null;
	public String getJNDIName_DataSource() {	
		if(OpenSPCoop2Properties.jndiNameDatasource==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.dataSource");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();

				OpenSPCoop2Properties.jndiNameDatasource = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.dataSource': "+e.getMessage(),e);
				OpenSPCoop2Properties.jndiNameDatasource = null;
			}    
		}

		return OpenSPCoop2Properties.jndiNameDatasource;
	}

	/**
	 * Restituisce le proprieta' da utilizzare con il contesto JNDI di lookup, se impostate.
	 *
	 * @return proprieta' da utilizzare con il contesto JNDI di lookup.
	 * 
	 */
	private static java.util.Properties jndiContextDatasource = null;
	public java.util.Properties getJNDIContext_DataSource() {
		if(OpenSPCoop2Properties.jndiContextDatasource == null){
			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.dataSource.property.");
				OpenSPCoop2Properties.jndiContextDatasource = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI per il datasource di openspcoop 'org.openspcoop2.pdd.dataSource.property.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.jndiContextDatasource = null;
			}   
		}

		return OpenSPCoop2Properties.jndiContextDatasource;
	}











	/* ********  CONNECTION FACTORY DI OPENSPCOOP  ******** */

	/**
	 * Restituisce il Nome JNDI del ConnectionFactory utilizzato da OpenSPCoop.
	 *
	 * @return il Nome JNDI del ConnectionFactory utilizzato da OpenSPCoop.
	 * 
	 */
	private static String jndiNameConnectionFactory = null;
	public String getJNDIName_ConnectionFactory() {	
		if(OpenSPCoop2Properties.jndiNameConnectionFactory==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.queueConnectionFactory");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();

				OpenSPCoop2Properties.jndiNameConnectionFactory = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.queueConnectionFactory': "+e.getMessage(),e);
				OpenSPCoop2Properties.jndiNameConnectionFactory = null;
			}    
		}

		return OpenSPCoop2Properties.jndiNameConnectionFactory;
	}

	/**
	 * Restituisce le proprieta' da utilizzare con il contesto JNDI di lookup, se impostate.
	 *
	 * @return proprieta' da utilizzare con il contesto JNDI di lookup.
	 * 
	 */
	private static java.util.Properties jndiContextConnectionFactory = null;
	public java.util.Properties getJNDIContext_ConnectionFactory() {
		if(OpenSPCoop2Properties.jndiContextConnectionFactory==null){
			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.connectionFactory.property.");
				OpenSPCoop2Properties.jndiContextConnectionFactory = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI del ConnectionFactory di openspcoop 'org.openspcoop2.pdd.connectionFactory.property.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.jndiContextConnectionFactory = null;
			}    
		}

		return OpenSPCoop2Properties.jndiContextConnectionFactory;
	}

	/**
	 * Restituisce acknowledgeMode della Sessione utilizzata da OpenSPCoop.
	 *
	 * @return acknowledgeMode della Sessione utilizzata da OpenSPCoop.
	 * 
	 */
	private static int acknowledgeModeSessioneConnectionFactory = -1;
	public int getAcknowledgeModeSessioneConnectionFactory() {	
		if(OpenSPCoop2Properties.acknowledgeModeSessioneConnectionFactory==-1){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.queueConnectionFactory.session.AcknowledgeMode");

				if(name!=null){
					name = name.trim();
					if(CostantiConfigurazione.AUTO_ACKNOWLEDGE.equals(name))
						OpenSPCoop2Properties.acknowledgeModeSessioneConnectionFactory = javax.jms.Session.AUTO_ACKNOWLEDGE;
					else if(CostantiConfigurazione.CLIENT_ACKNOWLEDGE.equals(name))
						OpenSPCoop2Properties.acknowledgeModeSessioneConnectionFactory = javax.jms.Session.CLIENT_ACKNOWLEDGE;
					else if(CostantiConfigurazione.DUPS_OK_ACKNOWLEDGE.equals(name))
						OpenSPCoop2Properties.acknowledgeModeSessioneConnectionFactory = javax.jms.Session.DUPS_OK_ACKNOWLEDGE;
					else
						throw new Exception("Tipo di acknowledgeModeSessione non conosciuto (viene utilizzato il default:AUTO_ACKNOWLEDGE)");
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.queueConnectionFactory.session.AcknowledgeMode' non impostata, viene utilizzato il default=AUTO_ACKNOWLEDGE");
					OpenSPCoop2Properties.acknowledgeModeSessioneConnectionFactory = javax.jms.Session.AUTO_ACKNOWLEDGE; // Default
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.queueConnectionFactory.session.AcknowledgeMode' non impostata, viene utilizzato il default=AUTO_ACKNOWLEDGE, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.acknowledgeModeSessioneConnectionFactory = javax.jms.Session.AUTO_ACKNOWLEDGE; // Default
			}    
		}

		return OpenSPCoop2Properties.acknowledgeModeSessioneConnectionFactory;
	}








	/* ********  CODE JMS DI OPENSPCOOP  ******** */

	public java.util.Map<String,String> getJNDIQueueName(boolean receiverJMSActive,boolean senderJMSActive){
		java.util.Map<String,String> table = new java.util.HashMap<String,String>();
		try{ 
			boolean ricezioneContenutiApplicativi = !receiverJMSActive;
			boolean ricezioneBuste = !receiverJMSActive;
			boolean consegnaContenutiApplicativi = !senderJMSActive;
			boolean inoltroBuste = !senderJMSActive;
			boolean inoltroRisposte = !senderJMSActive;
			boolean imbustamento = !senderJMSActive;
			boolean imbustamentoRisposte = !senderJMSActive;
			boolean sbustamento=!senderJMSActive;
			boolean sbustamentoRisposte=!senderJMSActive;
			java.util.Enumeration<?> en = this.reader.propertyNames();
			for (; en.hasMoreElements() ;) {
				String property = (String) en.nextElement();
				if(property.startsWith("org.openspcoop2.pdd.queue.")){
					String key = (property.substring("org.openspcoop2.pdd.queue.".length()));
					if(key != null)
						key = key.trim();
					String value = this.reader.getValue_convertEnvProperties(property);
					if(value!=null)
						value = value.trim();
					if(receiverJMSActive){
						if("ricezioneContenutiApplicativi".equals(key) && value!=null){
							table.put(RicezioneContenutiApplicativi.ID_MODULO, value);
							ricezioneContenutiApplicativi = true;
						}else if("ricezioneBuste".equals(key) && value!=null){
							table.put(RicezioneBuste.ID_MODULO, value);
							ricezioneBuste = true;
						}
					}
					if(senderJMSActive){
						if("inoltroBuste".equals(key) && value!=null){
							table.put(InoltroBuste.ID_MODULO, value);
							inoltroBuste = true;
						}else if("inoltroRisposte".equals(key) && value!=null){
							table.put(InoltroRisposte.ID_MODULO, value);
							inoltroRisposte = true;
						}else if("consegnaContenutiApplicativi".equals(key) && value!=null){
							table.put(ConsegnaContenutiApplicativi.ID_MODULO, value);
							consegnaContenutiApplicativi = true;
						}else if("imbustamento".equals(key) && value!=null){
							table.put(Imbustamento.ID_MODULO, value);
							imbustamento = true;
						}else if("imbustamentoRisposte".equals(key) && value!=null){
							table.put(ImbustamentoRisposte.ID_MODULO, value);
							imbustamentoRisposte = true;
						}else if("sbustamento".equals(key) && value!=null){
							table.put(Sbustamento.ID_MODULO, value);
							sbustamento = true;
						}else if("sbustamentoRisposte".equals(key) && value!=null){
							table.put(SbustamentoRisposte.ID_MODULO, value);
							sbustamentoRisposte = true;
						}
					}
				}
			}


			if(ricezioneContenutiApplicativi==false){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop: coda org.openspcoop2.pdd.queue.ricezioneContenutiApplicativi non definita");
				return null;
			}
			if(ricezioneBuste==false){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop: coda org.openspcoop2.pdd.queue.ricezioneBuste non definita");
				return null;
			}
			if(consegnaContenutiApplicativi==false){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop: coda org.openspcoop2.pdd.queue.consegnaContenutiApplicativi non definita");
				return null;
			}
			if(inoltroBuste==false){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop: coda org.openspcoop2.pdd.queue.inoltroBuste non definita");
				return null;
			}
			if(inoltroRisposte==false){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop: coda org.openspcoop2.pdd.queue.inoltroRisposte non definita");
				return null;
			}
			if(imbustamento==false){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop: coda org.openspcoop2.pdd.queue.imbustamento non definita");
				return null;
			}if(imbustamentoRisposte==false){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop: coda org.openspcoop2.pdd.queue.imbustamentoRisposte non definita");
				return null;
			}else if(sbustamentoRisposte==false){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop: coda org.openspcoop2.pdd.queue.sbustamentoRisposte non definita");
				return null;
			}
			if(sbustamento==false){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop: coda org.openspcoop2.pdd.queue.sbustamento non definita");
				return null;
			}
			return table;

		}catch(java.lang.Exception e) {
			this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop 'org.openspcoop2.pdd.queue.property.*': "+e.getMessage(),e);
			return null;
		}    
	}

	/**
	 * Restituisce le proprieta' da utilizzare nel contesto JNDI di lookup per localizzare le code.
	 *
	 * @return proprieta' da utilizzare con il contesto JNDI di lookup per localizzare le code.
	 * 
	 */
	private static java.util.Properties jndiContext_CodeInterne = null;
	public java.util.Properties getJNDIContext_CodeInterne() {	
		if(OpenSPCoop2Properties.jndiContext_CodeInterne==null){
			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.queue.property.");
				OpenSPCoop2Properties.jndiContext_CodeInterne = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI delle code di openspcoop 'org.openspcoop2.pdd.queue.property.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.jndiContext_CodeInterne = null;
			}    
		}

		return OpenSPCoop2Properties.jndiContext_CodeInterne;
	}














	/* ********  Timer EJB DI OPENSPCOOP  ******** */

	public java.util.Map<String,String> getJNDITimerEJBName(){
		java.util.Map<String,String> table = new java.util.HashMap<String,String>();
		try{ 
			boolean gestoreBusteNonRiscontrate = false;
			boolean gestoreMessaggi = false;
			boolean gestorePuliziaMessaggiAnomali = false;
			boolean gestoreRepositoryBuste = false;
			java.util.Enumeration<?> en = this.reader.propertyNames();
			for (; en.hasMoreElements() ;) {
				String property = (String) en.nextElement();
				if(property.startsWith("org.openspcoop2.pdd.timer.")){
					String key = (property.substring("org.openspcoop2.pdd.timer.".length()));
					if(key != null)
						key = key.trim();
					String value = this.reader.getValue_convertEnvProperties(property);
					if(value!=null)
						value = value.trim();
					if("gestoreBusteNonRiscontrate".equals(key) && value!=null){
						table.put(TimerGestoreBusteNonRiscontrate.ID_MODULO, value);
						gestoreBusteNonRiscontrate = true;
					}else if("gestoreMessaggi".equals(key) && value!=null){
						table.put(TimerGestoreMessaggi.ID_MODULO, value);
						gestoreMessaggi = true;
					}else if("gestorePuliziaMessaggiAnomali".equals(key) && value!=null){
						table.put(TimerGestorePuliziaMessaggiAnomali.ID_MODULO, value);
						gestorePuliziaMessaggiAnomali = true;
					}else if("gestoreRepositoryBuste".equals(key) && value!=null){
						table.put(TimerGestoreRepositoryBuste.ID_MODULO, value);
						gestoreRepositoryBuste = true;
					}
				}
			}


			if(gestoreBusteNonRiscontrate==false && this.isTimerGestoreRiscontriRicevuteAbilitato()){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI dei timer openspcoop: timer org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate non definito");
				return null;
			}
			if(gestoreMessaggi==false && this.isTimerGestoreMessaggiAbilitato()){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI dei timer openspcoop: timer org.openspcoop2.pdd.timer.gestoreMessaggi non definito");
				return null;
			}
			if(gestorePuliziaMessaggiAnomali==false && this.isTimerGestorePuliziaMessaggiAnomaliAbilitato()){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI dei timer openspcoop: timer org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali non definito");
				return null;
			}
			if(gestoreRepositoryBuste==false && this.isTimerGestoreRepositoryBusteAbilitato()){
				this.log.error("Riscontrato errore durante la lettura dei nomi JNDI dei timer openspcoop: timer org.openspcoop2.pdd.timer.gestoreRepositoryBuste  non definito");
				return null;
			}
			return table;

		}catch(java.lang.Exception e) {
			this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop 'org.openspcoop2.pdd.queue.property.*': "+e.getMessage(),e);
			return null;
		}    
	}

	/**
	 * Restituisce le proprieta' da utilizzare nel contesto JNDI di lookup per localizzare i timer.
	 *
	 * @return proprieta' da utilizzare con il contesto JNDI di lookup per localizzare i timer.
	 * 
	 */
	private static java.util.Properties jndiContext_TimerEJB = null;
	public java.util.Properties getJNDIContext_TimerEJB() {	
		if(OpenSPCoop2Properties.jndiContext_TimerEJB==null){
			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.timer.property.");
				OpenSPCoop2Properties.jndiContext_TimerEJB = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI dei timer di openspcoop 'org.openspcoop2.pdd.timer.property.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.jndiContext_TimerEJB = null;
			}    
		}

		return OpenSPCoop2Properties.jndiContext_TimerEJB;
	}
	
	private static Boolean isTimerAutoStart_StopTimer = null;
	public boolean isTimerAutoStart_StopTimer(){
		if(OpenSPCoop2Properties.isTimerAutoStart_StopTimer==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.autoStart.stop"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerAutoStart_StopTimer = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.autoStart.stop' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isTimerAutoStart_StopTimer = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.autoStart.stop', viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerAutoStart_StopTimer = true;
			}
		}

		return OpenSPCoop2Properties.isTimerAutoStart_StopTimer;
	}
	
	
	

	
	// GestoreRiscontriRicevute
	
	/**
	 * Restituisce l'indicazione se avviare il timer
	 *
	 * @return Restituisce indicazione se avviare il timer
	 * 
	 */
	private static Boolean isTimerGestoreRiscontriRicevuteAbilitato = null;
	public boolean isTimerGestoreRiscontriRicevuteAbilitato(){
		if(OpenSPCoop2Properties.isTimerGestoreRiscontriRicevuteAbilitato==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.enable"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestoreRiscontriRicevuteAbilitato = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isTimerGestoreRiscontriRicevuteAbilitato = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.enable', viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestoreRiscontriRicevuteAbilitato = true;
			}
		}

		return OpenSPCoop2Properties.isTimerGestoreRiscontriRicevuteAbilitato;
	}
	
	/**
	 * Restituisce l'indicazione se registrare su log le queries
	 *
	 * @return Restituisce indicazione se registrare su log le queries
	 * 
	 */
	private static Boolean isTimerGestoreRiscontriRicevuteAbilitatoLog = null;
	public boolean isTimerGestoreRiscontriRicevuteAbilitatoLog(){
		if(OpenSPCoop2Properties.isTimerGestoreRiscontriRicevuteAbilitatoLog==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.logQuery"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestoreRiscontriRicevuteAbilitatoLog = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.logQuery' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerGestoreRiscontriRicevuteAbilitatoLog = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.logQuery', viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestoreRiscontriRicevuteAbilitatoLog = false;
			}
		}

		return OpenSPCoop2Properties.isTimerGestoreRiscontriRicevuteAbilitatoLog;
	}
	
	/**
	 * Restituisce l'indicazione sul numero di messaggi alla volta processati
	 *
	 * @return Restituisce indicazione sul numero di messaggi alla volta processati
	 * 
	 */
	private static Integer getTimerGestoreRiscontriRicevuteLimit = null;
	public int getTimerGestoreRiscontriRicevuteLimit(){
		if(OpenSPCoop2Properties.getTimerGestoreRiscontriRicevuteLimit==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.query.limit"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getTimerGestoreRiscontriRicevuteLimit = Integer.parseInt(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.query.limit' non impostata, viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI);
					OpenSPCoop2Properties.getTimerGestoreRiscontriRicevuteLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.query.limit', viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestoreRiscontriRicevuteLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreRiscontriRicevuteLimit;
	}
	
	private static Integer getTimerGestoreRiscontriRicevute_lockMaxLife = null;
	public int getTimerGestoreRiscontriRicevute_lockMaxLife() {	
		if(OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockMaxLife==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.lock.maxLife");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockMaxLife = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE);
					OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
			}  
			if(OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockMaxLife!=null && OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockMaxLife>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockMaxLife = OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockMaxLife *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockMaxLife;
	}
	
	private static Integer getTimerGestoreRiscontriRicevute_lockIdleTime = null;
	public int getTimerGestoreRiscontriRicevute_lockIdleTime() {	
		if(OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockIdleTime==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.lock.idleTime");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockIdleTime = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME);
					OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
			} 
			if(OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockIdleTime!=null && OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockIdleTime>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockIdleTime = OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockIdleTime *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreRiscontriRicevute_lockIdleTime;
	}
	
	
	
	
	// GestoreMessaggi
	
	/**
	 * Restituisce l'indicazione se avviare il timer
	 *
	 * @return Restituisce indicazione se avviare il timer
	 * 
	 */
	private static Boolean isTimerGestoreMessaggiAbilitato = null;
	public boolean isTimerGestoreMessaggiAbilitato(){
		if(OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitato==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreMessaggi.enable"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitato = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitato = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.enable', viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitato = true;
			}
		}

		return OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitato;
	}
	
	/**
	 * Restituisce l'indicazione se usare l'order by nelle queries
	 *
	 * @return Restituisce indicazione se usare l'order by nelle queries
	 * 
	 */
	private static Boolean isTimerGestoreMessaggiAbilitatoOrderBy = null;
	public boolean isTimerGestoreMessaggiAbilitatoOrderBy(){
		if(OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitatoOrderBy==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreMessaggi.orderBy"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitatoOrderBy = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.orderBy' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitatoOrderBy = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.orderBy', viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitatoOrderBy = false;
			}
		}

		return OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitatoOrderBy;
	}
	
	/**
	 * Restituisce l'indicazione se registrare su log le queries
	 *
	 * @return Restituisce indicazione se registrare su log le queries
	 * 
	 */
	private static Boolean isTimerGestoreMessaggiAbilitatoLog = null;
	public boolean isTimerGestoreMessaggiAbilitatoLog(){
		if(OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitatoLog==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreMessaggi.logQuery"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitatoLog = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.logQuery' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitatoLog = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.logQuery', viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitatoLog = false;
			}
		}

		return OpenSPCoop2Properties.isTimerGestoreMessaggiAbilitatoLog;
	}
	
	/**
	 * Restituisce l'indicazione sul numero di messaggi alla volta processati
	 *
	 * @return Restituisce indicazione sul numero di messaggi alla volta processati
	 * 
	 */
	private static Integer getTimerGestoreMessaggiLimit = null;
	public int getTimerGestoreMessaggiLimit(){
		if(OpenSPCoop2Properties.getTimerGestoreMessaggiLimit==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreMessaggi.query.limit"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getTimerGestoreMessaggiLimit = Integer.parseInt(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.query.limit' non impostata, viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI);
					OpenSPCoop2Properties.getTimerGestoreMessaggiLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.query.limit', viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestoreMessaggiLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreMessaggiLimit;
	}
	
	/**
	 * Restituisce l'indicazione se devono essere verificate anche le connessioni rimaste attive
	 *
	 * @return Restituisce indicazione se devono essere verificate anche le connessioni rimaste attive
	 * 
	 */
	private static Boolean isTimerGestoreMessaggiVerificaConnessioniAttive = null;
	public boolean isTimerGestoreMessaggiVerificaConnessioniAttive(){
		if(OpenSPCoop2Properties.isTimerGestoreMessaggiVerificaConnessioniAttive==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreMessaggi.verificaConnessioniAttive"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestoreMessaggiVerificaConnessioniAttive = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.verificaConnessioniAttive' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerGestoreMessaggiVerificaConnessioniAttive = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.verificaConnessioniAttive', viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestoreMessaggiVerificaConnessioniAttive = false;
			}
		}

		return OpenSPCoop2Properties.isTimerGestoreMessaggiVerificaConnessioniAttive;
	}
		
	private static Integer getTimerGestoreMessaggi_lockMaxLife = null;
	public int getTimerGestoreMessaggi_lockMaxLife() {	
		if(OpenSPCoop2Properties.getTimerGestoreMessaggi_lockMaxLife==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreMessaggi.lock.maxLife");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerGestoreMessaggi_lockMaxLife = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE);
					OpenSPCoop2Properties.getTimerGestoreMessaggi_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestoreMessaggi_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
			}
			if(OpenSPCoop2Properties.getTimerGestoreMessaggi_lockMaxLife!=null && OpenSPCoop2Properties.getTimerGestoreMessaggi_lockMaxLife>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerGestoreMessaggi_lockMaxLife = OpenSPCoop2Properties.getTimerGestoreMessaggi_lockMaxLife *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreMessaggi_lockMaxLife;
	}
	
	private static Integer getTimerGestoreMessaggi_lockIdleTime = null;
	public int getTimerGestoreMessaggi_lockIdleTime() {	
		if(OpenSPCoop2Properties.getTimerGestoreMessaggi_lockIdleTime==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreMessaggi.lock.idleTime");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerGestoreMessaggi_lockIdleTime = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME);
					OpenSPCoop2Properties.getTimerGestoreMessaggi_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestoreMessaggi_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
			}
			if(OpenSPCoop2Properties.getTimerGestoreMessaggi_lockIdleTime!=null && OpenSPCoop2Properties.getTimerGestoreMessaggi_lockIdleTime>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerGestoreMessaggi_lockIdleTime = OpenSPCoop2Properties.getTimerGestoreMessaggi_lockIdleTime *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreMessaggi_lockIdleTime;
	}

	
	
	
	
	// GestorePuliziaMessaggiAnomali
	
	/**
	 * Restituisce l'indicazione se avviare il timer
	 *
	 * @return Restituisce indicazione se avviare il timer
	 * 
	 */
	private static Boolean isTimerGestorePuliziaMessaggiAnomaliAbilitato = null;
	public boolean isTimerGestorePuliziaMessaggiAnomaliAbilitato(){
		if(OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitato==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.enable"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitato = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitato = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.enable', viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitato = true;
			}
		}

		return OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitato;
	}
	
	/**
	 * Restituisce l'indicazione se usare l'order by nelle queries
	 *
	 * @return Restituisce indicazione se usare l'order by nelle queries
	 * 
	 */
	private static Boolean isTimerGestorePuliziaMessaggiAnomaliAbilitatoOrderBy = null;
	public boolean isTimerGestorePuliziaMessaggiAnomaliAbilitatoOrderBy(){
		if(OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitatoOrderBy==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.orderBy"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitatoOrderBy = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.orderBy' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitatoOrderBy = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.orderBy', viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitatoOrderBy = false;
			}
		}

		return OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitatoOrderBy;
	}
	
	/**
	 * Restituisce l'indicazione se registrare su log le queries
	 *
	 * @return Restituisce indicazione se registrare su log le queries
	 * 
	 */
	private static Boolean isTimerGestorePuliziaMessaggiAnomaliAbilitatoLog = null;
	public boolean isTimerGestorePuliziaMessaggiAnomaliAbilitatoLog(){
		if(OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitatoLog==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.logQuery"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitatoLog = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.logQuery' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitatoLog = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.logQuery', viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitatoLog = false;
			}
		}

		return OpenSPCoop2Properties.isTimerGestorePuliziaMessaggiAnomaliAbilitatoLog;
	}

	/**
	 * Restituisce l'indicazione sul numero di messaggi alla volta processati
	 *
	 * @return Restituisce indicazione sul numero di messaggi alla volta processati
	 * 
	 */
	private static Integer getTimerGestorePuliziaMessaggiAnomaliLimit = null;
	public int getTimerGestorePuliziaMessaggiAnomaliLimit(){
		if(OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomaliLimit==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.query.limit"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomaliLimit = Integer.parseInt(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.query.limit' non impostata, viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI);
					OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomaliLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.query.limit', viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomaliLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
			}
		}

		return OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomaliLimit;
	}
	
	private static Integer getTimerGestorePuliziaMessaggiAnomali_lockMaxLife = null;
	public int getTimerGestorePuliziaMessaggiAnomali_lockMaxLife() {	
		if(OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockMaxLife==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.lock.maxLife");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockMaxLife = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE);
					OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
			}  
			if(OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockMaxLife!=null && OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockMaxLife>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockMaxLife = OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockMaxLife *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockMaxLife;
	}
	
	private static Integer getTimerGestorePuliziaMessaggiAnomali_lockIdleTime = null;
	public int getTimerGestorePuliziaMessaggiAnomali_lockIdleTime() {	
		if(OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockIdleTime==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.lock.idleTime");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockIdleTime = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME);
					OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
			} 
			if(OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockIdleTime!=null && OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockIdleTime>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockIdleTime = OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockIdleTime *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomali_lockIdleTime;
	}
	
	
	
	// GestoreBuste
	
	/**
	 * Restituisce l'indicazione se avviare il timer
	 *
	 * @return Restituisce indicazione se avviare il timer
	 * 
	 */
	private static Boolean isTimerGestoreRepositoryBusteAbilitato = null;
	public boolean isTimerGestoreRepositoryBusteAbilitato(){
		if(OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitato==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreRepositoryBuste.enable"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitato = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitato = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.enable', viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitato = true;
			}
		}

		return OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitato;
	}

	/**
	 * Restituisce l'indicazione se usare l'order by nelle queries
	 *
	 * @return Restituisce indicazione se usare l'order by nelle queries
	 * 
	 */
	private static Boolean isTimerGestoreRepositoryBusteAbilitatoOrderBy = null;
	public boolean isTimerGestoreRepositoryBusteAbilitatoOrderBy(){
		if(OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitatoOrderBy==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreRepositoryBuste.orderBy"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitatoOrderBy = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.orderBy' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitatoOrderBy = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.orderBy', viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitatoOrderBy = false;
			}
		}

		return OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitatoOrderBy;
	}
	
	/**
	 * Restituisce l'indicazione se registrare su log le queries
	 *
	 * @return Restituisce indicazione se registrare su log le queries
	 * 
	 */
	private static Boolean isTimerGestoreRepositoryBusteAbilitatoLog = null;
	public boolean isTimerGestoreRepositoryBusteAbilitatoLog(){
		if(OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitatoLog==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreRepositoryBuste.logQuery"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitatoLog = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.logQuery' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitatoLog = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.logQuery', viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitatoLog = false;
			}
		}

		return OpenSPCoop2Properties.isTimerGestoreRepositoryBusteAbilitatoLog;
	}
	
	/**
	 * Restituisce l'indicazione sul numero di messaggi alla volta processati
	 *
	 * @return Restituisce indicazione sul numero di messaggi alla volta processati
	 * 
	 */
	private static Integer getTimerGestoreRepositoryBusteLimit = null;
	public int getTimerGestoreRepositoryBusteLimit(){
		if(OpenSPCoop2Properties.getTimerGestoreRepositoryBusteLimit==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreRepositoryBuste.query.limit"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getTimerGestoreRepositoryBusteLimit = Integer.parseInt(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.query.limit' non impostata, viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI);
					OpenSPCoop2Properties.getTimerGestoreRepositoryBusteLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.query.limit', viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestoreRepositoryBusteLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreRepositoryBusteLimit;
	}
	
	private static Integer getTimerGestoreRepositoryBuste_lockMaxLife = null;
	public int getTimerGestoreRepositoryBuste_lockMaxLife() {	
		if(OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockMaxLife==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreRepositoryBuste.lock.maxLife");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockMaxLife = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE);
					OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
			}
			if(OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockMaxLife!=null && OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockMaxLife>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockMaxLife = OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockMaxLife *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockMaxLife;
	}
	
	private static Integer getTimerGestoreRepositoryBuste_lockIdleTime = null;
	public int getTimerGestoreRepositoryBuste_lockIdleTime() {	
		if(OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockIdleTime==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.gestoreRepositoryBuste.lock.idleTime");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockIdleTime = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME);
					OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
			}
			if(OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockIdleTime!=null && OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockIdleTime>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockIdleTime = OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockIdleTime *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreRepositoryBuste_lockIdleTime;
	}

	


	
	
	// Gestore ConsegnaContenutiApplicativi
	
	/**
	 * Restituisce l'indicazione se avviare il timer
	 *
	 * @return Restituisce indicazione se avviare il timer
	 * 
	 */
	private static Boolean isTimerConsegnaContenutiApplicativiAbilitato = null;
	public boolean isTimerConsegnaContenutiApplicativiAbilitato(){
		
		if(OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitato==null){
			if(this.isServerJ2EE()){
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.enable' disabilitata poiche' il prodotto e' configurato in modalita' server j2ee");
				isTimerConsegnaContenutiApplicativiAbilitato = false;
			}
			else{			
				try{  
					String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.enable"); 
					if(value!=null){
						value = value.trim();
						OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitato = Boolean.parseBoolean(value);
					}else{
						this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.enable' non impostata, viene utilizzato il default=true");
						OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitato = true;
					}
	
				}catch(java.lang.Exception e) {
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.enable', viene utilizzato il default=true, errore:"+e.getMessage(),e);
					OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitato = true;
				}
			}

		}
		
		return OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitato;
	}
	
	/**
	 * Restituisce l'indicazione se attuare una gestione in debug mode
	 *
	 * @return Restituisce indicazione se attuare una gestione in debug mode
	 * 
	 */
	private static Boolean isTimerConsegnaContenutiApplicativiDebug = null;
	public boolean isTimerConsegnaContenutiApplicativiDebug(){
		if(OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiDebug==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.debug"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiDebug = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.debug' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiDebug = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.debug', viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiDebug = false;
			}
		}

		return OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiDebug;
	}


	private static Integer getTimerConsegnaContenutiApplicativiThreadsPoolSize = null;
	public int getTimerConsegnaContenutiApplicativiThreadsPoolSize(){
		if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiThreadsPoolSize==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.threads.poolSize"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiThreadsPoolSize = Integer.parseInt(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.threads.poolSize' non impostata, viene utilizzato il default="+CostantiPdD.CONSEGNA_CONTENUTI_THREADS_POOL_SIZE);
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiThreadsPoolSize = CostantiPdD.CONSEGNA_CONTENUTI_THREADS_POOL_SIZE;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.threads.poolSize', viene utilizzato il default="+CostantiPdD.CONSEGNA_CONTENUTI_THREADS_POOL_SIZE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiThreadsPoolSize = CostantiPdD.CONSEGNA_CONTENUTI_THREADS_POOL_SIZE;
			}
		}

		return OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiThreadsPoolSize;
	}
	
	private static Integer getTimerConsegnaContenutiApplicativiThreadsQueueSize = null;
	public int getTimerConsegnaContenutiApplicativiThreadsQueueSize(){
		if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiThreadsQueueSize==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.threads.queueSize"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiThreadsQueueSize = Integer.parseInt(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.threads.queueSize' non impostata, viene utilizzato il default="+CostantiPdD.CONSEGNA_CONTENUTI_THREADS_POOL_SIZE);
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiThreadsQueueSize = CostantiPdD.CONSEGNA_CONTENUTI_THREADS_POOL_SIZE;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.threads.queueSize', viene utilizzato il default="+CostantiPdD.CONSEGNA_CONTENUTI_THREADS_POOL_SIZE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiThreadsQueueSize = CostantiPdD.CONSEGNA_CONTENUTI_THREADS_POOL_SIZE;
			}
		}

		return OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiThreadsQueueSize;
	}
	
	/**
	 * Restituisce l'indicazione sul numero di messaggi alla volta processati
	 *
	 * @return Restituisce indicazione sul numero di messaggi alla volta processati
	 * 
	 */
	private static Integer getTimerConsegnaContenutiApplicativiLimit = null;
	public int getTimerConsegnaContenutiApplicativiLimit(){
		if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiLimit==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.query.limit"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiLimit = Integer.parseInt(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.query.limit' non impostata, viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI);
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.query.limit', viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
			}
		}

		return OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiLimit;
	}
	
	/**
	 * Restituisce la Frequenza di check 
	 *
	 * @return Restituisce la Frequenza di check 
	 * 
	 */
	private static Integer getTimerConsegnaContenutiApplicativiInterval = null;
	public int getTimerConsegnaContenutiApplicativiInterval() {	
		if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiInterval==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.intervallo");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiInterval = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.intervallo' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_INTERVAL);
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiInterval = CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_INTERVAL;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.intervallo' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_INTERVAL+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiInterval = CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_INTERVAL;
			}  
		}

		return OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiInterval;
	}
	
	private static Integer getTimerConsegnaContenutiApplicativiMinIntervalResend = null;
	public int getTimerConsegnaContenutiApplicativiMinIntervalResend() {	
		if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiMinIntervalResend==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.intervalloMinimoRiconsegna");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiMinIntervalResend = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.intervalloMinimoRiconsegna' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_INTERVAL);
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiMinIntervalResend = CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_INTERVAL;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.intervalloMinimoRiconsegna' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_INTERVAL+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiMinIntervalResend = CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_INTERVAL;
			}  
		}

		return OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiMinIntervalResend;
	}
	
	private static Boolean isLoadBalancerDebug = null;
	public boolean isLoadBalancerDebug(){
		if(OpenSPCoop2Properties.isLoadBalancerDebug==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.loadBalancer.debug"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isLoadBalancerDebug = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.loadBalancer.debug' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isLoadBalancerDebug = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.loadBalancer.debug', viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isLoadBalancerDebug = false;
			}
		}

		return OpenSPCoop2Properties.isLoadBalancerDebug;
	}
	
	private static Integer getTimerConsegnaContenutiApplicativi_lockMaxLife = null;
	public int getTimerConsegnaContenutiApplicativi_lockMaxLife() {	
		if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockMaxLife==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.lock.maxLife");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockMaxLife = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE);
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
			}  
			if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockMaxLife!=null && OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockMaxLife>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockMaxLife = OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockMaxLife *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockMaxLife;
	}
	
	private static Integer getTimerConsegnaContenutiApplicativi_lockIdleTime = null;
	public int getTimerConsegnaContenutiApplicativi_lockIdleTime() {	
		if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockIdleTime==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.lock.idleTime");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockIdleTime = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME);
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
			} 
			if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockIdleTime!=null && OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockIdleTime>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockIdleTime = OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockIdleTime *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_lockIdleTime;
	}
	
	private static Integer getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife = null;
	public int getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife() {	
		if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.presaInConsegna.maxLife");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.presaInConsegna.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_PRESA_IN_CONSEGNA_MAX_LIFE);
					OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife = CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_PRESA_IN_CONSEGNA_MAX_LIFE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.presaInConsegna.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_PRESA_IN_CONSEGNA_MAX_LIFE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife = CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_PRESA_IN_CONSEGNA_MAX_LIFE;
			}  
			if(OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife!=null && OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife = OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife *1000;
			}
		}

		return OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativi_presaInConsegnaMaxLife;
	}
	
	





	/* ********  REPOSITORY DI OPENSPCOOP  ******** */

	/**
	 * Restituisce il tipo di repository utilizzato da OpenSPCoop 
	 *
	 * @return Restituisce il tipo di repository utilizzato da OpenSPCoop 
	 * 
	 */
	private static String repositoryType = null;
	public String getRepositoryType() {	
		if(OpenSPCoop2Properties.repositoryType==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.tipo");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.repositoryType = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.tipo': "+e.getMessage(),e);
				OpenSPCoop2Properties.repositoryType = null;
			}    
		}

		return OpenSPCoop2Properties.repositoryType;
	}

	/**
	 * Restituisce il tipo di database utilizzato da OpenSPCoop 
	 *
	 * @return Restituisce il tipo di database utilizzato da OpenSPCoop 
	 * 
	 */
	private static String databaseType = null;
	public String getDatabaseType() {	
		if(OpenSPCoop2Properties.databaseType==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.tipoDatabase");
				if(name!=null)
					name = name.trim();
				OpenSPCoop2Properties.databaseType = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.tipoDatabase': "+e.getMessage(),e);
				OpenSPCoop2Properties.databaseType = null;
			}    
		}

		return OpenSPCoop2Properties.databaseType;
	}

	/**
	 * Restituisce true se il tipo di repository utilizzato da OpenSPCoop  e' fs
	 *
	 * @return Restituisce true se il tipo di repository utilizzato da OpenSPCoop  e' su file system
	 * 
	 */
	private static Boolean isRepositoryOnFS_value = null;
	public boolean isRepositoryOnFS() {	

		if(OpenSPCoop2Properties.isRepositoryOnFS_value==null){
			// DEFAULT is true!

			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.tipo");

				if(name!=null){
					name = name.trim();
					if(CostantiConfigurazione.REPOSITORY_DB.equals(name)){
						OpenSPCoop2Properties.isRepositoryOnFS_value = false;
					}else{
						OpenSPCoop2Properties.isRepositoryOnFS_value = true;
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.tipo' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isRepositoryOnFS_value = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.tipo' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isRepositoryOnFS_value = true;
			}    
		}

		return OpenSPCoop2Properties.isRepositoryOnFS_value;
	}

	/**
	 * Restituisce la working directory utilizzata da OpenSPCoop 
	 *
	 * @return Restituisce la working directory utilizzata da OpenSPCoop.
	 * 
	 */
	private static String repositoryDirectory = null;
	public String getRepositoryDirectory() {
		if(OpenSPCoop2Properties.repositoryDirectory==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.directory");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.repositoryDirectory = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.directory': "+e.getMessage(),e);
				OpenSPCoop2Properties.repositoryDirectory = null;
			}  
		}

		return OpenSPCoop2Properties.repositoryDirectory;
	}

	/**
	 * Restituisce il JDBC Adapter utilizzato da OpenSPCoop 
	 *
	 * @return Restituisce il JDBC Adapter utilizzato da OpenSPCoop.
	 * 
	 */
	private static String repositoryJDBCAdapter = null;
	public String getRepositoryJDBCAdapter() {	
		if(OpenSPCoop2Properties.repositoryJDBCAdapter==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.jdbcAdapter");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.repositoryJDBCAdapter = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.jdbcAdapter': "+e.getMessage(),e);
				OpenSPCoop2Properties.repositoryJDBCAdapter = null;
			}    
		}

		return OpenSPCoop2Properties.repositoryJDBCAdapter;
	}


	private static Boolean forceIndex = null;
	public boolean isForceIndex() {	

		if(OpenSPCoop2Properties.forceIndex==null){
			// DEFAULT is false

			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.forceIndex");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.forceIndex = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.forceIndex' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.forceIndex = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.forceIndex' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.forceIndex = false;
			}    
		}

		return OpenSPCoop2Properties.forceIndex;
	}
	
	

	private static AttachmentsProcessingMode attachmentsProcessingMode = null;
	public AttachmentsProcessingMode getAttachmentsProcessingMode() {	
		if(OpenSPCoop2Properties.attachmentsProcessingMode==null){
			if(this.isFileCacheEnable()){
				try{ 
					OpenSPCoop2Properties.attachmentsProcessingMode=AttachmentsProcessingMode.getFileCacheProcessingMode(getAttachmentRepoDir(), getFileThreshold());
				} catch(java.lang.Exception e) {
					this.log.error("Riscontrato errore durante la comprensione sulla modalità di processing degli attachments: "+e.getMessage(),e);
					OpenSPCoop2Properties.attachmentsProcessingMode=AttachmentsProcessingMode.getMemoryCacheProcessingMode();
				} 
			}else{
				OpenSPCoop2Properties.attachmentsProcessingMode=AttachmentsProcessingMode.getMemoryCacheProcessingMode();
			}
		}

		return OpenSPCoop2Properties.attachmentsProcessingMode;
	}

	private static Boolean isFileCacheEnable = null;
	private boolean isFileCacheEnable() {	
		if(OpenSPCoop2Properties.isFileCacheEnable==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.attachment.fileCacheEnable");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.fileCacheEnable' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isFileCacheEnable = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.fileCacheEnable': "+e.getMessage(),e);
				OpenSPCoop2Properties.isFileCacheEnable = false;
			}    
		}

		return OpenSPCoop2Properties.isFileCacheEnable;
	}


	private static String attachmentRepoDir = null;
	private String getAttachmentRepoDir() {	

		if(OpenSPCoop2Properties.attachmentRepoDir==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.attachment.repositoryDir");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.attachmentRepoDir = name;
				}else{
					// Se fileCacheEnable == false allora puo' essere null; 
					if(!isFileCacheEnable())
						return null;
					throw new Exception("non definita");
				}

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.repositoryDir': "+e.getMessage(),e);
				OpenSPCoop2Properties.attachmentRepoDir = null;
			}    
		}

		return OpenSPCoop2Properties.attachmentRepoDir;
	}
	
	private static String fileThreshold = null;
	private String getFileThreshold() {	

		if(OpenSPCoop2Properties.fileThreshold==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.attachment.fileThreshold");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.fileThreshold = name;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.fileThreshold' non impostata, viene utilizzato il default=1024");
					OpenSPCoop2Properties.fileThreshold = "1024";
				}

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.fileThreshold': "+e.getMessage(),e);
				OpenSPCoop2Properties.fileThreshold = "1024";
			}    
		}

		return OpenSPCoop2Properties.fileThreshold;
	}
	
	private static String filePrefix = null;
	public String getFilePrefix() {	

		if(OpenSPCoop2Properties.filePrefix==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.attachment.filePrefix");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.filePrefix = name;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.filePrefix' non impostata, viene utilizzato il default="+CostantiPdD.OPENSPCOOP2);
					OpenSPCoop2Properties.filePrefix = CostantiPdD.OPENSPCOOP2;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.filePrefix': "+e.getMessage(),e);
				OpenSPCoop2Properties.filePrefix = CostantiPdD.OPENSPCOOP2;
			}    
		}

		return OpenSPCoop2Properties.filePrefix;
	}
	
	private static String fileSuffix = null;
	public String getFileSuffix() {	

		if(OpenSPCoop2Properties.fileSuffix==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.attachment.fileSuffix");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.fileSuffix = name;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.fileSuffix' non impostata, viene utilizzato il default=.att");
					OpenSPCoop2Properties.fileSuffix = ".att";
				}

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.fileSuffix': "+e.getMessage(),e);
				OpenSPCoop2Properties.fileSuffix = ".att";
			}    
		}

		return OpenSPCoop2Properties.fileSuffix;
	}
	
	private static int deleteInterval = 0;
	public int getDeleteInterval() {	

		if(OpenSPCoop2Properties.deleteInterval==0){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.attachment.deleteInterval");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.deleteInterval = Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.deleteInterval' non impostata, viene utilizzato il default=300");
					OpenSPCoop2Properties.deleteInterval = 300;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.deleteInterval': "+e.getMessage(),e);
				OpenSPCoop2Properties.deleteInterval = 300;
			}    
		}

		return OpenSPCoop2Properties.deleteInterval;
	}

	/**
	 * Restituisce l'intervallo di pulizia del repository di OpenSPCoop 
	 *
	 * @return Restituisce l'intervallo di pulizia del repository di OpenSPCoo
	 * 
	 */
	private static Long repositoryIntervalloEliminazioneMessaggi = null;
	public long getRepositoryIntervalloEliminazioneMessaggi() {	
		if(OpenSPCoop2Properties.repositoryIntervalloEliminazioneMessaggi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.timer");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.repositoryIntervalloEliminazioneMessaggi = java.lang.Long.parseLong(name);
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.timer': "+e.getMessage(),e);
				OpenSPCoop2Properties.repositoryIntervalloEliminazioneMessaggi = -1L;
			}   
		}

		return OpenSPCoop2Properties.repositoryIntervalloEliminazioneMessaggi;
	}

	/**
	 * Restituisce l'intervallo di tempo che definisce un msg scaduto nel repository di OpenSPCoop 
	 *
	 * @return Restituisce l'intervallo di tempo che definisce un msg scaduto nel repository di OpenSPCoo
	 * 
	 */
	private static Long repositoryIntervalloScadenzaMessaggi = null;
	public long getRepositoryIntervalloScadenzaMessaggi() {	
		if(OpenSPCoop2Properties.repositoryIntervalloScadenzaMessaggi == null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.scadenzaMessaggio");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.repositoryIntervalloScadenzaMessaggi = java.lang.Long.parseLong(name);
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaMessaggio': "+e.getMessage(),e);
				OpenSPCoop2Properties.repositoryIntervalloScadenzaMessaggi = -1L;
			}    
		}

		return OpenSPCoop2Properties.repositoryIntervalloScadenzaMessaggi;
	}

	/**
	 * Restituisce L'indicazione se filtrare le buste rispetto alla scadenza della busta
	 *
	 * @return indicazione se filtrare le buste rispetto alla scadenza della busta
	 * 
	 */
	private static Boolean repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = null;
	public boolean isRepositoryBusteFiltraBusteScaduteRispettoOraRegistrazione() {	
		if(OpenSPCoop2Properties.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione==null){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.scadenzaMessaggio.filtraBusteScaduteRispettoOraRegistrazione");
				if(value==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaMessaggio.filtraBusteScaduteRispettoOraRegistrazione' non definita (Viene utilizzato il default:true)");
					OpenSPCoop2Properties.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = true;
				}else{
					OpenSPCoop2Properties.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = Boolean.parseBoolean(value);
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaMessaggio.filtraBusteScaduteRispettoOraRegistrazione' (Viene utilizzato il default:true): "+e.getMessage(),e);
				OpenSPCoop2Properties.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = true;
			}    
		}

		return OpenSPCoop2Properties.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione;
	}

	/**
	 * Restituisce l'intervallo di tempo che definisce una correlazione scaduta nel repository di OpenSPCoop 
	 *
	 * @return Restituisce l'intervallo di tempo che definisce una correlazione scaduta nel repository di OpenSPCoo
	 * 
	 */
	private static Long repositoryIntervalloScadenzaCorrelazioneApplicativa = null;
	public long getRepositoryIntervalloScadenzaCorrelazioneApplicativa() {	
		if(OpenSPCoop2Properties.repositoryIntervalloScadenzaCorrelazioneApplicativa == null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa");
				if(name==null){
					//throw new Exception("non definita");
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa' non definita, viene usato il valore impostato nella proprieta 'org.openspcoop2.pdd.repository.scadenzaMessaggio'");
					OpenSPCoop2Properties.repositoryIntervalloScadenzaCorrelazioneApplicativa = getRepositoryIntervalloScadenzaMessaggi();
				}
				else{
					name = name.trim();
					OpenSPCoop2Properties.repositoryIntervalloScadenzaCorrelazioneApplicativa = java.lang.Long.parseLong(name);
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa': "+e.getMessage(),e);
				OpenSPCoop2Properties.repositoryIntervalloScadenzaCorrelazioneApplicativa = -1L;
			}    
		}
		
		return OpenSPCoop2Properties.repositoryIntervalloScadenzaCorrelazioneApplicativa;
	}
	
	private static Integer maxLengthCorrelazioneApplicativa = null;
	public int getMaxLengthCorrelazioneApplicativa() {	
		if(OpenSPCoop2Properties.maxLengthCorrelazioneApplicativa == null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.correlazioneApplicativa.maxLength");
				if(name==null){
					//throw new Exception("non definita");
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.correlazioneApplicativa.maxLength' non definita, viene usato il valore di default: 255");
					OpenSPCoop2Properties.maxLengthCorrelazioneApplicativa = 255;
				}
				else{
					name = name.trim();
					OpenSPCoop2Properties.maxLengthCorrelazioneApplicativa = java.lang.Integer.parseInt(name);
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.correlazioneApplicativa.maxLength': "+e.getMessage(),e);
				OpenSPCoop2Properties.maxLengthCorrelazioneApplicativa = 255;
			}    
		}
		
		return OpenSPCoop2Properties.maxLengthCorrelazioneApplicativa;
	}

	
	private static Boolean isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione = null;
	public boolean isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione() {	
		
		if(OpenSPCoop2Properties.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa.filtraCorrelazioniScaduteRispettoOraRegistrazione");
				
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa.filtraCorrelazioniScaduteRispettoOraRegistrazione' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione = true;
				}
	
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa.filtraCorrelazioniScaduteRispettoOraRegistrazione' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione = true;
			}    
		}
		
		return OpenSPCoop2Properties.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione;
	}
	
	private static Boolean isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione_EscludiConScadenzaImpostata = null;
	public boolean isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione_EscludiConScadenzaImpostata() {	
		
		if(OpenSPCoop2Properties.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione_EscludiConScadenzaImpostata==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa.filtraCorrelazioniScaduteRispettoOraRegistrazione.soloCorrelazioniSenzaScadenza");
				
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione_EscludiConScadenzaImpostata = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa.filtraCorrelazioniScaduteRispettoOraRegistrazione.soloCorrelazioniSenzaScadenza' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione_EscludiConScadenzaImpostata = false;
				}
	
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa.filtraCorrelazioniScaduteRispettoOraRegistrazione.soloCorrelazioniSenzaScadenza' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione_EscludiConScadenzaImpostata = false;
			}    
		}
		
		return OpenSPCoop2Properties.isRepositoryScadenzaCorrelazioneApplicativaFiltraRispettoOraRegistrazione_EscludiConScadenzaImpostata;
	}
	

	/**
	 * Restituisce l'intervallo in millisecondi di attesa attiva per messaggi gia in processamento
	 * 
	 * @return Restituisce l'intervallo in millisecondi di attesa attiva per messaggi gia in processamento
	 *  * 
	 */
	private static Long msgGiaInProcessamento_AttesaAttiva = null;
	public long getMsgGiaInProcessamento_AttesaAttiva() {	
		if(OpenSPCoop2Properties.msgGiaInProcessamento_AttesaAttiva==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.messaggioInProcessamento.attesaAttiva");

				if(name!=null){
					name = name.trim();
					long time = java.lang.Long.parseLong(name);
					OpenSPCoop2Properties.msgGiaInProcessamento_AttesaAttiva = time*1000;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioInProcessamento.attesaAttiva' non impostato, viene utilizzato il default="+CostantiPdD.MSG_GIA_IN_PROCESSAMENTO_ATTESA_ATTIVA);
					OpenSPCoop2Properties.msgGiaInProcessamento_AttesaAttiva = CostantiPdD.MSG_GIA_IN_PROCESSAMENTO_ATTESA_ATTIVA;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioInProcessamento.attesaAttiva' non impostato, viene utilizzato il default="+CostantiPdD.MSG_GIA_IN_PROCESSAMENTO_ATTESA_ATTIVA+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.msgGiaInProcessamento_AttesaAttiva = CostantiPdD.MSG_GIA_IN_PROCESSAMENTO_ATTESA_ATTIVA;
			}    
		}

		return OpenSPCoop2Properties.msgGiaInProcessamento_AttesaAttiva;
	}

	/**
	 * Restituisce l'intervallo maggiore per frequenza di check nell'attesa attiva effettuata dal TransactionManager, in millisecondi
	 * 
	 * @return Restituisce Intervallo maggiore per frequenza di check nell'attesa attiva effettuata dal TransactionManager, in millisecondi
	 * 
	 */
	private static Integer msgGiaInProcessamento_CheckInterval = null;
	public int getMsgGiaInProcessamento_CheckInterval() {	
		if(OpenSPCoop2Properties.msgGiaInProcessamento_CheckInterval==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.messaggioInProcessamento.check");

				if(name!=null){
					name = name.trim();
					int time = java.lang.Integer.parseInt(name);
					OpenSPCoop2Properties.msgGiaInProcessamento_CheckInterval = time;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioInProcessamento.check' non impostato, viene utilizzato il default="+CostantiPdD.MSG_GIA_IN_PROCESSAMENTO_CHECK_INTERVAL);
					OpenSPCoop2Properties.msgGiaInProcessamento_CheckInterval = CostantiPdD.MSG_GIA_IN_PROCESSAMENTO_CHECK_INTERVAL;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioInProcessamento.check' non impostato, viene utilizzato il default="+CostantiPdD.MSG_GIA_IN_PROCESSAMENTO_CHECK_INTERVAL+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.msgGiaInProcessamento_CheckInterval = CostantiPdD.MSG_GIA_IN_PROCESSAMENTO_CHECK_INTERVAL;
			}  
		}

		return OpenSPCoop2Properties.msgGiaInProcessamento_CheckInterval;
	}

	/**
	 * Restituisce l'IThreshold utilizzato da OpenSPCoop 
	 *
	 * @return Restituisce l'IThreshold utilizzato da OpenSPCoop.
	 * 
	 */
	private static String[] repositoryThresholdTypes = null;
	private static boolean repositoryThresholdTypesRead = false;
	public String[] getRepositoryThresholdTypes() {	
		if(OpenSPCoop2Properties.repositoryThresholdTypesRead == false){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.threshold.tipi");
				if(name==null){
					OpenSPCoop2Properties.repositoryThresholdTypes = null;
				}else{
					String [] r = name.trim().split(",");
					for(int i=0; i<r.length; i++){
						r[i] = r[i].trim();
					}
					OpenSPCoop2Properties.repositoryThresholdTypes = r;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.threshold.tipi': "+e.getMessage(),e);
				OpenSPCoop2Properties.repositoryThresholdTypes = null;
			}  
			OpenSPCoop2Properties.repositoryThresholdTypesRead = true;
		}

		return OpenSPCoop2Properties.repositoryThresholdTypes;
	}
	/**
	 * Restituisce la soglia utilizzata dall'IThreshold utilizzato da OpenSPCoop 
	 *
	 * @return Restituisce la soglia utilizzata dall'IThreshold utilizzato da OpenSPCoop.
	 * 
	 */
	private static Properties repositoryThresholdParameters = null;
	private static boolean repositoryThresholdParametersRead = false;
	public Properties getRepositoryThresholdParameters(String tipoThreshould) {	
		if(OpenSPCoop2Properties.repositoryThresholdParametersRead==false){
			try{ 
				repositoryThresholdParameters = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.repository.threshold."+tipoThreshould+".");
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.threshold."+tipoThreshould+".*': "+e.getMessage(),e);
				OpenSPCoop2Properties.repositoryThresholdParameters = null;
			}  
			OpenSPCoop2Properties.repositoryThresholdParametersRead = true; 
		}

		return OpenSPCoop2Properties.repositoryThresholdParameters;
	}

	/**
	 * Restituisce l'intervallo per il timer di Threshold
	 *
	 * @return Restituisce l'intervallo per il timer di Threshold
	 * 
	 */
	private static Long repositoryThresholdCheckInterval = null;
	public long getRepositoryThresholdCheckInterval() {	
		if(OpenSPCoop2Properties.repositoryThresholdCheckInterval == null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.threshold.checkInterval");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.repositoryThresholdCheckInterval = java.lang.Long.parseLong(name);
				}else{
					OpenSPCoop2Properties.repositoryThresholdCheckInterval = 0L;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.threshold.checkInterval': "+e.getMessage(),e);
				OpenSPCoop2Properties.repositoryThresholdCheckInterval = -1L;
			}    
		}

		return OpenSPCoop2Properties.repositoryThresholdCheckInterval;
	}

	/**
	 * Indicazione se la porta di dominio deve attuare allo startup la validazione semantica della configurazione xml
	 *   
	 * @return Indicazione se la porta di dominio deve attuare allo startup la validazione semantica della configurazione xml
	 * 
	 */
	private static Boolean isValidazioneSemanticaConfigurazioneStartupXML = null;
	public boolean isValidazioneSemanticaConfigurazioneStartupXML(){

		if(OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartupXML==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.startup.config.xml.validazioneSemantica"); 

				if (value != null){
					value = value.trim();
					if(CostantiConfigurazione.ABILITATO.equals(value)){
						OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartupXML = true;
					}else if(CostantiConfigurazione.DISABILITATO.equals(value)){
						OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartupXML = false;
					}
					else{
						throw new Exception("Valore non corretto (abilitato/disabilitato): "+value);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.config.xml.validazioneSemantica' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartupXML = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.config.xml.validazioneSemantica' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartupXML = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartupXML;
	}
	/**
	 * Indicazione se la porta di dominio deve attuare allo startup la validazione semantica della configurazione
	 *   
	 * @return Indicazione se la porta di dominio deve attuare allo startup la validazione semantica della configurazione
	 * 
	 */
	private static Boolean isValidazioneSemanticaConfigurazioneStartup = null;
	public boolean isValidazioneSemanticaConfigurazioneStartup(){

		if(OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartup==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.startup.config.validazioneSemantica"); 

				if (value != null){
					value = value.trim();
					if(CostantiConfigurazione.ABILITATO.equals(value)){
						OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartup = true;
					}else if(CostantiConfigurazione.DISABILITATO.equals(value)){
						OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartup = false;
					}
					else{
						throw new Exception("Valore non corretto (abilitato/disabilitato): "+value);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.config.validazioneSemantica' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartup = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.config.validazioneSemantica' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartup = false;
			}
		}

		return OpenSPCoop2Properties.isValidazioneSemanticaConfigurazioneStartup;
	}
	/**
	 * Indicazione se la porta di dominio deve attuare allo startup la validazione semantica della registro servizi xml
	 *   
	 * @return Indicazione se la porta di dominio deve attuare allo startup la validazione semantica della registro servizi xml
	 * 
	 */
	private static Boolean isValidazioneSemanticaRegistroServiziStartupXML = null;
	public boolean isValidazioneSemanticaRegistroServiziStartupXML(){

		if(OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartupXML==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.startup.registri.xml.validazioneSemantica"); 

				if (value != null){
					value = value.trim();
					if(CostantiConfigurazione.ABILITATO.equals(value)){
						OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartupXML = true;
					}else if(CostantiConfigurazione.DISABILITATO.equals(value)){
						OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartupXML = false;
					}
					else{
						throw new Exception("Valore non corretto (abilitato/disabilitato): "+value);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.registri.xml.validazioneSemantica' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartupXML = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.registri.xml.validazioneSemantica' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartupXML = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartupXML;
	}
	/**
	 * Indicazione se la porta di dominio deve attuare allo startup la validazione semantica della registro servizi
	 *   
	 * @return Indicazione se la porta di dominio deve attuare allo startup la validazione semantica della registro servizi
	 * 
	 */
	private static Boolean isValidazioneSemanticaRegistroServiziStartup = null;
	public boolean isValidazioneSemanticaRegistroServiziStartup(){

		if(OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartup==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.startup.registri.validazioneSemantica"); 

				if (value != null){
					value = value.trim();
					if(CostantiConfigurazione.ABILITATO.equals(value)){
						OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartup = true;
					}else if(CostantiConfigurazione.DISABILITATO.equals(value)){
						OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartup = false;
					}
					else{
						throw new Exception("Valore non corretto (abilitato/disabilitato): "+value);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.registri.validazioneSemantica' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartup = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.registri.validazioneSemantica' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartup = false;
			}
		}

		return OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziStartup;
	}
	/**
	 * Indicazione se la porta di dominio deve attuare allo startup la validazione semantica della configurazione
	 *   
	 * @return Indicazione se la porta di dominio deve attuare allo startup la validazione semantica della configurazione
	 * 
	 */
	private static Boolean isValidazioneSemanticaRegistroServiziCheckURI = null;
	public boolean isValidazioneSemanticaRegistroServiziCheckURI(){

		if(OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziCheckURI==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.startup.registri.validazioneSemantica.checkURI"); 

				if (value != null){
					value = value.trim();
					if(CostantiConfigurazione.ABILITATO.equals(value)){
						OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziCheckURI = true;
					}else if(CostantiConfigurazione.DISABILITATO.equals(value)){
						OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziCheckURI = false;
					}
					else{
						throw new Exception("Valore non corretto (abilitato/disabilitato): "+value);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.registri.validazioneSemantica.checkURI' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziCheckURI = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.registri.validazioneSemantica.checkURI' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziCheckURI = false;
			}
		}

		return OpenSPCoop2Properties.isValidazioneSemanticaRegistroServiziCheckURI;
	}
	


	private static Boolean isAbilitatoControlloRisorseDB = null;
	public boolean isAbilitatoControlloRisorseDB() {	
		if(OpenSPCoop2Properties.isAbilitatoControlloRisorseDB==null){
			OpenSPCoop2Properties.isAbilitatoControlloRisorseDB = isAbilitatoControlloRisorse("db");
		}
		return OpenSPCoop2Properties.isAbilitatoControlloRisorseDB;
	}
	private static Boolean isAbilitatoControlloRisorseJMS = null;
	public boolean isAbilitatoControlloRisorseJMS() {	
		if(OpenSPCoop2Properties.isAbilitatoControlloRisorseJMS==null){
			OpenSPCoop2Properties.isAbilitatoControlloRisorseJMS = isAbilitatoControlloRisorse("jms");
		}
		return OpenSPCoop2Properties.isAbilitatoControlloRisorseJMS;
	}
	private static Boolean isAbilitatoControlloRisorseTracciamentiPersonalizzati = null;
	public boolean isAbilitatoControlloRisorseTracciamentiPersonalizzati() {	
		if(OpenSPCoop2Properties.isAbilitatoControlloRisorseTracciamentiPersonalizzati==null){
			OpenSPCoop2Properties.isAbilitatoControlloRisorseTracciamentiPersonalizzati = isAbilitatoControlloRisorse("tracciamento");
		}
		return OpenSPCoop2Properties.isAbilitatoControlloRisorseTracciamentiPersonalizzati;
	}
	private static Boolean isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati = null;
	public boolean isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati() {	
		if(OpenSPCoop2Properties.isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati==null){
			OpenSPCoop2Properties.isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati = isAbilitatoControlloRisorse("msgdiagnostici");
		}
		return OpenSPCoop2Properties.isAbilitatoControlloRisorseMsgDiagnosticiPersonalizzati;
	}
	private static Boolean isAbilitatoControlloRisorseConfigurazione = null;
	public boolean isAbilitatoControlloRisorseConfigurazione() {	
		if(OpenSPCoop2Properties.isAbilitatoControlloRisorseConfigurazione==null){
			OpenSPCoop2Properties.isAbilitatoControlloRisorseConfigurazione = isAbilitatoControlloRisorse("configurazione");
		}
		return OpenSPCoop2Properties.isAbilitatoControlloRisorseConfigurazione;
	}
	private static Boolean isAbilitatoControlloValidazioneSemanticaConfigurazione = null;
	public boolean isAbilitatoControlloValidazioneSemanticaConfigurazione() {	
		if(OpenSPCoop2Properties.isAbilitatoControlloValidazioneSemanticaConfigurazione==null){
			OpenSPCoop2Properties.isAbilitatoControlloValidazioneSemanticaConfigurazione = isAbilitatoControlloRisorse("configurazione.validazioneSemantica");
		}
		return OpenSPCoop2Properties.isAbilitatoControlloValidazioneSemanticaConfigurazione;
	}
	private static Boolean isAbilitatoControlloRisorseRegistriServizi = null;
	public boolean isAbilitatoControlloRisorseRegistriServizi() {	
		if(OpenSPCoop2Properties.isAbilitatoControlloRisorseRegistriServizi==null){
			OpenSPCoop2Properties.isAbilitatoControlloRisorseRegistriServizi = isAbilitatoControlloRisorse("registri");
		}
		return OpenSPCoop2Properties.isAbilitatoControlloRisorseRegistriServizi;
	}
	private static Boolean isAbilitatoControlloValidazioneSemanticaRegistriServizi = null;
	public boolean isAbilitatoControlloValidazioneSemanticaRegistriServizi() {	
		if(OpenSPCoop2Properties.isAbilitatoControlloValidazioneSemanticaRegistriServizi==null){
			OpenSPCoop2Properties.isAbilitatoControlloValidazioneSemanticaRegistriServizi = isAbilitatoControlloRisorse("registri.validazioneSemantica");
		}
		return OpenSPCoop2Properties.isAbilitatoControlloValidazioneSemanticaRegistriServizi;
	}
	private boolean isAbilitatoControlloRisorse(String tipo) {	
		try{  
			String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.risorse.check."+tipo); 
			if(value==null){
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.risorse.check."+tipo+"' non impostata, viene utilizzato il default=false");
				return false;
			}
			return CostantiConfigurazione.ABILITATO.equals(value);
		}catch(java.lang.Exception e) {
			this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.risorse.check."+tipo+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
			return false;
		}
	}
	private static Boolean isControlloRisorseRegistriRaggiungibilitaTotale = null;
	public boolean isControlloRisorseRegistriRaggiungibilitaTotale() {	
		if(OpenSPCoop2Properties.isControlloRisorseRegistriRaggiungibilitaTotale==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.risorse.check.registri.tipo"); 
				if(value==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.risorse.check.registri.tipo' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isControlloRisorseRegistriRaggiungibilitaTotale = false;
				}
				else
					OpenSPCoop2Properties.isControlloRisorseRegistriRaggiungibilitaTotale = "singolo".equals(value);
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.risorse.check.registri.tipo' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloRisorseRegistriRaggiungibilitaTotale = false;
			}
		}

		return OpenSPCoop2Properties.isControlloRisorseRegistriRaggiungibilitaTotale;
	}
	/**
	 * Restituisce l'intervallo per il timer di Threshold
	 *
	 * @return Restituisce l'intervallo per il timer di Threshold
	 * 
	 */
	private static Long controlloRisorseCheckInterval = null;
	public long getControlloRisorseCheckInterval() {	
		if(OpenSPCoop2Properties.controlloRisorseCheckInterval==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.risorse.checkInterval");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.controlloRisorseCheckInterval = java.lang.Long.parseLong(name);
				}else{
					OpenSPCoop2Properties.controlloRisorseCheckInterval = 0L;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.risorse.checkInterval': "+e.getMessage(),e);
				OpenSPCoop2Properties.controlloRisorseCheckInterval = -1L;
			}    
		}

		return OpenSPCoop2Properties.controlloRisorseCheckInterval;
	}

	private static Boolean isControlloRisorseRegistrazioneEvento = null;
	public boolean isControlloRisorseRegistrazioneEvento() {	
		if(OpenSPCoop2Properties.isControlloRisorseRegistrazioneEvento==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.risorse.registraEvento");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.risorse.registraEvento' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isControlloRisorseRegistrazioneEvento = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.risorse.registraEvento', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloRisorseRegistrazioneEvento = true;
			}    
		}

		return OpenSPCoop2Properties.isControlloRisorseRegistrazioneEvento;
	}






	/* ******** ERRORE APPLICATIVO  ******** */

	/**
	 * Restituisce le proprieta' di default utilizzate dalla porta di dominio per invocazioni di porte delegate che generano errori
	 *
	 * @return Restituisce le proprieta' di default utilizzate dalla porta di dominio per invocazioni di porte delegate che generano errori
	 * 
	 */
	private static ProprietaErroreApplicativo proprietaGestioneErrorePD = null;
	public ProprietaErroreApplicativo getProprietaGestioneErrorePD(IProtocolManager protocolManager){
		return getProprietaGestioneErrorePD_engine(protocolManager,false);
	}
	private ProprietaErroreApplicativo getProprietaGestioneErrorePD_engine(IProtocolManager protocolManager,boolean testPerValidazione){
		if(OpenSPCoop2Properties.proprietaGestioneErrorePD==null){
			String fault = null;
			try{  
				fault = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.erroreApplicativo.fault"); 
				if(fault==null)
					throw new Exception("non definita");
				fault = fault.trim();		
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.fault': "+e.getMessage(),e);
				return null;
			}

			String faultActor = null;
			try{  
				faultActor = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.erroreApplicativo.faultActor"); 
				if(faultActor==null)
					throw new Exception("non definita");
				faultActor = faultActor.trim();		
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.faultActor': "+e.getMessage(),e);
				return null;
			}

			String faultGeneric = null;
			try{  
				faultGeneric = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.erroreApplicativo.genericFaultCode"); 
				if(faultGeneric==null)
					throw new Exception("non definita");
				faultGeneric = faultGeneric.trim();		
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.genericFaultCode': "+e.getMessage(),e);
				return null;
			}

			String faultPrefix = null;
			try{  
				faultPrefix = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.erroreApplicativo.prefixFaultCode"); 
				if(faultPrefix!=null)
					faultPrefix = faultPrefix.trim();		
			}catch(java.lang.Exception e) {
				// proprieta' che puo' non essere definita (default GOVWAY-)
			}

			ProprietaErroreApplicativo gestione = new ProprietaErroreApplicativo();
			if(CostantiConfigurazione.ERRORE_APPLICATIVO_XML.equals(fault))
				gestione.setFaultAsXML(true);
			else
				gestione.setFaultAsXML(false); // default: ERRORE_APPLICATIVO_SOAP
			gestione.setFaultActor(faultActor);
			if(CostantiConfigurazione.ABILITATO.equals(faultGeneric))
				gestione.setFaultAsGenericCode(true);
			else
				gestione.setFaultAsGenericCode(false); // default: false
			gestione.setFaultPrefixCode(faultPrefix);

			gestione.setInsertAsDetails(this.isErroreApplicativoIntoDetails());
			
			gestione.setAggiungiDetailErroreApplicativo_SoapFaultApplicativo(this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo());
			
			gestione.setAggiungiDetailErroreApplicativo_SoapFaultPdD(this.isAggiungiDetailErroreApplicativo_SoapFaultPdD());

			OpenSPCoop2Properties.proprietaGestioneErrorePD = gestione;
		}

		ProprietaErroreApplicativo pNew = new ProprietaErroreApplicativo();
		pNew.setDominio(OpenSPCoop2Properties.proprietaGestioneErrorePD.getDominio());
		pNew.setFaultActor(OpenSPCoop2Properties.proprietaGestioneErrorePD.getFaultActor());
		pNew.setFaultAsGenericCode(OpenSPCoop2Properties.proprietaGestioneErrorePD.isFaultAsGenericCode());
		pNew.setFaultAsXML(OpenSPCoop2Properties.proprietaGestioneErrorePD.isFaultAsXML());
		pNew.setFaultPrefixCode(OpenSPCoop2Properties.proprietaGestioneErrorePD.getFaultPrefixCode());
		pNew.setIdModulo(OpenSPCoop2Properties.proprietaGestioneErrorePD.getIdModulo());
		pNew.setInsertAsDetails(OpenSPCoop2Properties.proprietaGestioneErrorePD.isInsertAsDetails());
		pNew.setAggiungiDetailErroreApplicativo_SoapFaultApplicativo(OpenSPCoop2Properties.proprietaGestioneErrorePD.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo());
		pNew.setAggiungiDetailErroreApplicativo_SoapFaultPdD(OpenSPCoop2Properties.proprietaGestioneErrorePD.isAggiungiDetailErroreApplicativo_SoapFaultPdD());
		if(protocolManager!=null){
			FaultIntegrationGenericInfoMode sf = protocolManager.getModalitaGenerazioneInformazioniGeneriche_DetailsFaultIntegrazione();
			if(FaultIntegrationGenericInfoMode.SERVIZIO_APPLICATIVO.equals(sf)){
				pNew.setInformazioniGenericheDetailsOpenSPCoop(null);
			}
			else if(FaultIntegrationGenericInfoMode.ABILITATO.equals(sf)){
				pNew.setInformazioniGenericheDetailsOpenSPCoop(true);
			} 
			else if(FaultIntegrationGenericInfoMode.DISABILITATO.equals(sf)){
				pNew.setInformazioniGenericheDetailsOpenSPCoop(false);
			} 
			
			Boolean enrich = protocolManager.isAggiungiDetailErroreApplicativo_FaultApplicativo();
			if(enrich!=null){
				pNew.setAggiungiDetailErroreApplicativo_SoapFaultApplicativo(enrich);
			}
			
			enrich = protocolManager.isAggiungiDetailErroreApplicativo_FaultPdD();
			if(enrich!=null){
				pNew.setAggiungiDetailErroreApplicativo_SoapFaultPdD(enrich);
			}
			
		}else{
			pNew.setInformazioniGenericheDetailsOpenSPCoop(null); // default
		}
		return pNew;
	}



	/**
	 * Indicazione se l'errore applicativo deve essere inserito in un details.
	 *   
	 * @return Indicazione se l'errore applicativo deve essere inserito in un details.
	 * 
	 */
	private static Boolean isErroreApplicativoIntoDetails = null;
	public boolean isErroreApplicativoIntoDetails(){
		if(OpenSPCoop2Properties.isErroreApplicativoIntoDetails==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.erroreApplicativo.fault.details"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isErroreApplicativoIntoDetails = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.fault.details' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isErroreApplicativoIntoDetails = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.fault.details' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isErroreApplicativoIntoDetails = true;
			}
		}

		return OpenSPCoop2Properties.isErroreApplicativoIntoDetails;
	}
	
	
	/**
	 * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
	 * 
	 * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
	 */
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
	public boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo(){
		if(OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.erroreApplicativo.faultApplicativo.enrichDetails"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = true;
			}
		}

		return OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo;
	}
	
	
	/**
	 * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
	 * 
	 * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
	 */
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
	public boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD(){
		if(OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.erroreApplicativo.faultPdD.enrichDetails"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = true;
			}
		}

		return OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}
	
	
	
	private static String getErroriHttpHeaderGovWayStatus = null;
	public String getErroriHttpHeaderGovWayStatus(){
		
		String pName = "org.openspcoop2.pdd.errori.http.govwayStatus";
		if(OpenSPCoop2Properties.getErroriHttpHeaderGovWayStatus==null){
			try{  
				String name = this.reader.getValue_convertEnvProperties(pName); 
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.getErroriHttpHeaderGovWayStatus = name;

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+org.openspcoop2.protocol.basic.Costanti._getHTTP_HEADER_GOVWAY_ERROR_STATUS()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getErroriHttpHeaderGovWayStatus = org.openspcoop2.protocol.basic.Costanti._getHTTP_HEADER_GOVWAY_ERROR_STATUS();
			}
		}

		return OpenSPCoop2Properties.getErroriHttpHeaderGovWayStatus;
	}
	
	private static String getErroriHttpHeaderGovWayType = null;
	public String getErroriHttpHeaderGovWayType(){
		
		String pName = "org.openspcoop2.pdd.errori.http.govwayType";
		if(OpenSPCoop2Properties.getErroriHttpHeaderGovWayType==null){
			try{  
				String name = this.reader.getValue_convertEnvProperties(pName); 
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.getErroriHttpHeaderGovWayType = name;

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+org.openspcoop2.protocol.basic.Costanti._getHTTP_HEADER_GOVWAY_ERROR_TYPE()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getErroriHttpHeaderGovWayType = org.openspcoop2.protocol.basic.Costanti._getHTTP_HEADER_GOVWAY_ERROR_TYPE();
			}
		}

		return OpenSPCoop2Properties.getErroriHttpHeaderGovWayType;
	}
	
	private static String getErroriHttpHeaderGovWayCode = null;
	public String getErroriHttpHeaderGovWayCode(){
		
		String pName = "org.openspcoop2.pdd.errori.http.govwayCode";
		if(OpenSPCoop2Properties.getErroriHttpHeaderGovWayCode==null){
			try{  
				String name = this.reader.getValue_convertEnvProperties(pName); 
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.getErroriHttpHeaderGovWayCode = name;

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+org.openspcoop2.protocol.basic.Costanti._getHTTP_HEADER_GOVWAY_ERROR_CODE()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getErroriHttpHeaderGovWayCode = org.openspcoop2.protocol.basic.Costanti._getHTTP_HEADER_GOVWAY_ERROR_CODE();
			}
		}

		return OpenSPCoop2Properties.getErroriHttpHeaderGovWayCode;
	}
	
	
	
	private static Boolean isErroriSoapUseGovWayStatusAsFaultCode = null;
	public boolean isErroriSoapUseGovWayStatusAsFaultCode(){
		if(OpenSPCoop2Properties.isErroriSoapUseGovWayStatusAsFaultCode==null){
			String pName = "org.openspcoop2.pdd.errori.soap.useGovWayStatusAsFaultCode";
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isErroriSoapUseGovWayStatusAsFaultCode = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isErroriSoapUseGovWayStatusAsFaultCode = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isErroriSoapUseGovWayStatusAsFaultCode = false;
			}
		}

		return OpenSPCoop2Properties.isErroriSoapUseGovWayStatusAsFaultCode;
	}
	
	private static Boolean isErroriSoapHttpHeaderGovWayCodeEnabled = null;
	public boolean isErroriSoapHttpHeaderGovWayCodeEnabled(){
		if(OpenSPCoop2Properties.isErroriSoapHttpHeaderGovWayCodeEnabled==null){
			String pName = "org.openspcoop2.pdd.errori.soap.HttpHeaderGovWayCode.enabled";
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isErroriSoapHttpHeaderGovWayCodeEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isErroriSoapHttpHeaderGovWayCodeEnabled = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isErroriSoapHttpHeaderGovWayCodeEnabled = false;
			}
		}

		return OpenSPCoop2Properties.isErroriSoapHttpHeaderGovWayCodeEnabled;
	}
	
	private static Boolean isErroriGovWayStatusEnabled = null;
	public boolean isErroriGovWayStatusEnabled(){
		if(OpenSPCoop2Properties.isErroriGovWayStatusEnabled==null){
			String pName = "org.openspcoop2.pdd.errori.status";
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isErroriGovWayStatusEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isErroriGovWayStatusEnabled = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isErroriGovWayStatusEnabled = false;
			}
		}

		return OpenSPCoop2Properties.isErroriGovWayStatusEnabled;
	}
	
	private static Boolean isErroriGovWayInstanceEnabled = null;
	public boolean isErroriGovWayInstanceEnabled(){
		if(OpenSPCoop2Properties.isErroriGovWayInstanceEnabled==null){
			String pName = "org.openspcoop2.pdd.errori.instance";
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isErroriGovWayInstanceEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isErroriGovWayInstanceEnabled = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isErroriGovWayInstanceEnabled = false;
			}
		}

		return OpenSPCoop2Properties.isErroriGovWayInstanceEnabled;
	}
	
	private static Boolean isErroriGovWayForceSpecificDetails = null;
	public boolean isErroriGovWayForceSpecificDetails(){
		if(OpenSPCoop2Properties.isErroriGovWayForceSpecificDetails==null){
			String pName = "org.openspcoop2.pdd.errori.forceSpecificDetails";
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isErroriGovWayForceSpecificDetails = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isErroriGovWayForceSpecificDetails = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isErroriGovWayForceSpecificDetails = false;
			}
		}

		return OpenSPCoop2Properties.isErroriGovWayForceSpecificDetails;
	}
	
	private static Boolean isErroriGovWayFaultDetailsWithProblemRFC7807 = null;
	public boolean isErroriGovWayFaultDetailsWithProblemRFC7807(){
		if(OpenSPCoop2Properties.isErroriGovWayFaultDetailsWithProblemRFC7807==null){
			String pName = "org.openspcoop2.pdd.errori.faultDetailsWithProblemRFC7807";
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isErroriGovWayFaultDetailsWithProblemRFC7807 = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isErroriGovWayFaultDetailsWithProblemRFC7807 = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isErroriGovWayFaultDetailsWithProblemRFC7807 = false;
			}
		}

		return OpenSPCoop2Properties.isErroriGovWayFaultDetailsWithProblemRFC7807;
	}
	
	
	
	
	private static Boolean isProblemRFC7807_enrichTitleAsGovWayType = null;
	public boolean isProblemRFC7807_enrichTitleAsGovWayType(){
		if(OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType==null){
			String pName = "org.openspcoop2.pdd.problemRFC7807.enrichTitleAsGovWayType";
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType = true;
			}
		}

		return OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType;
	}
	
	
	private static Boolean isProblemRFC7807_enrichTitleAsGovWayType_camelCaseDecode = null;
	public boolean isProblemRFC7807_enrichTitleAsGovWayType_camelCaseDecode(){
		if(OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType_camelCaseDecode==null){
			String pName = "org.openspcoop2.pdd.problemRFC7807.enrichTitleAsGovWayType.camelCaseDecode";
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType_camelCaseDecode = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType_camelCaseDecode = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType_camelCaseDecode = false;
			}
		}

		return OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType_camelCaseDecode;
	}
	
	
	private static Boolean isProblemRFC7807_enrichTitleAsGovWayType_customClaim = null;
	public boolean isProblemRFC7807_enrichTitleAsGovWayType_customClaim(){
		if(OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType_customClaim==null){
			String pName = "org.openspcoop2.pdd.problemRFC7807.enrichTitleAsGovWayType.customClaim";
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType_customClaim = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType_customClaim = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType_customClaim = false;
			}
		}

		return OpenSPCoop2Properties.isProblemRFC7807_enrichTitleAsGovWayType_customClaim;
	}
	

	private static String getProblemRFC7807_transactionId_claim = null;
	public String getProblemRFC7807_transactionId_claim(){
		
		String pName = "org.openspcoop2.pdd.problemRFC7807.transactionId.claim";
		if(OpenSPCoop2Properties.getProblemRFC7807_transactionId_claim==null){
			try{  
				String fault = this.reader.getValue_convertEnvProperties(pName); 
				if(fault==null)
					throw new Exception("non definita");
				fault = fault.trim();
				OpenSPCoop2Properties.getProblemRFC7807_transactionId_claim = fault;

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+org.openspcoop2.protocol.basic.Costanti._getPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getProblemRFC7807_transactionId_claim = org.openspcoop2.protocol.basic.Costanti._getPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID();
			}
		}

		return OpenSPCoop2Properties.getProblemRFC7807_transactionId_claim;
	}
	
	private static String getProblemRFC7807_code_claim = null;
	public String getProblemRFC7807_code_claim(){
		
		String pName = "org.openspcoop2.pdd.problemRFC7807.code.claim";
		if(OpenSPCoop2Properties.getProblemRFC7807_code_claim==null){
			try{  
				String fault = this.reader.getValue_convertEnvProperties(pName); 
				if(fault==null)
					throw new Exception("non definita");
				fault = fault.trim();
				OpenSPCoop2Properties.getProblemRFC7807_code_claim = fault;

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+org.openspcoop2.protocol.basic.Costanti._getPROBLEM_RFC7807_GOVWAY_CODE()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getProblemRFC7807_code_claim = org.openspcoop2.protocol.basic.Costanti._getPROBLEM_RFC7807_GOVWAY_CODE();
			}
		}

		return OpenSPCoop2Properties.getProblemRFC7807_code_claim;
	}
	
	private static String getProblemRFC7807_type_claim = null;
	public String getProblemRFC7807_type_claim(){
		
		String pName = "org.openspcoop2.pdd.problemRFC7807.type.claim";
		if(OpenSPCoop2Properties.getProblemRFC7807_type_claim==null){
			try{  
				String fault = this.reader.getValue_convertEnvProperties(pName); 
				if(fault==null)
					throw new Exception("non definita");
				fault = fault.trim();
				OpenSPCoop2Properties.getProblemRFC7807_type_claim = fault;

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+org.openspcoop2.protocol.basic.Costanti._getPROBLEM_RFC7807_GOVWAY_TYPE()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getProblemRFC7807_type_claim = org.openspcoop2.protocol.basic.Costanti._getPROBLEM_RFC7807_GOVWAY_TYPE();
			}
		}

		return OpenSPCoop2Properties.getProblemRFC7807_type_claim;
	}





	/* ******** PORTA DI DOMINIO  ******** */

	/**
	 * Restituisce il nome di default dell'identificativo porta utilizzato dalla porta di dominio
	 *
	 * @return Restituisce il nome di default dell'identificativo porta utilizzato dalla porta di dominio
	 * 
	 */
	private static String identificativoPortaDefault = null;
	private String getIdentificativoPortaDefault(){
		if(OpenSPCoop2Properties.identificativoPortaDefault==null){
			try{  
				String fault = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.identificativoPorta.dominio"); 
				if(fault==null)
					throw new Exception("non definita");
				fault = fault.trim();
				OpenSPCoop2Properties.identificativoPortaDefault = fault;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.identificativoPorta.dominio': "+e.getMessage(),e);
				OpenSPCoop2Properties.identificativoPortaDefault = null;
			}
		}

		return OpenSPCoop2Properties.identificativoPortaDefault;
	}
	/**
	 * Restituisce il nome di default della porta utilizzato dalla porta di dominio
	 *
	 * @return Restituisce il nome di default della porta utilizzato dalla porta di dominio
	 * 
	 */
	private static String nomePortaDefault = null;
	private String getNomePortaDefault(){
		if(OpenSPCoop2Properties.nomePortaDefault==null){
			try{  
				String fault = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.identificativoPorta.nome"); 
				if(fault==null)
					throw new Exception("non definita");
				fault = fault.trim();
				OpenSPCoop2Properties.nomePortaDefault = fault;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.identificativoPorta.nome': "+e.getMessage(),e);
				OpenSPCoop2Properties.nomePortaDefault = null;
			}
		}

		return OpenSPCoop2Properties.nomePortaDefault;
	}


	/**
	 * Restituisce il nome di default della porta utilizzato dalla porta di dominio
	 *
	 * @return Restituisce il nome di default della porta utilizzato dalla porta di dominio
	 * 
	 */
	private static String tipoPortaDefault = null;
	private String getTipoPortaDefault(){
		if(OpenSPCoop2Properties.tipoPortaDefault==null){
			try{  
				String fault = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.identificativoPorta.tipo"); 
				if(fault==null)
					throw new Exception("non definita");
				fault = fault.trim();
				OpenSPCoop2Properties.tipoPortaDefault = fault;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.identificativoPorta.tipo': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoPortaDefault = null;
			}
		}
		return OpenSPCoop2Properties.tipoPortaDefault;
	}

	/**
	 * Restituisce l'identita di default della porta utilizzato dalla porta di dominio
	 *
	 * @return Restituisce l'identita di default della porta utilizzato dalla porta di dominio
	 * 
	 */
	private static IDSoggetto identitaPortaDefault = null;
	private IDSoggetto getIdentitaPortaDefault(){
		if(OpenSPCoop2Properties.identitaPortaDefault==null){
			String pdd = getIdentificativoPortaDefault();
			String nome = getNomePortaDefault();
			String tipo = getTipoPortaDefault();
			if(tipo==null || nome==null || pdd==null)
				OpenSPCoop2Properties.identitaPortaDefault = null;
			else	
				OpenSPCoop2Properties.identitaPortaDefault = new IDSoggetto(tipo,nome,pdd);
		}

		if(OpenSPCoop2Properties.identitaPortaDefault!=null){
			IDSoggetto idNew = new IDSoggetto();
			idNew.setCodicePorta(OpenSPCoop2Properties.identitaPortaDefault.getCodicePorta());
			idNew.setNome(OpenSPCoop2Properties.identitaPortaDefault.getNome());
			idNew.setTipo(OpenSPCoop2Properties.identitaPortaDefault.getTipo());
			return idNew;
		}
		else{
			return null;
		}
	}


	// protocol mapping
	
	private static ConcurrentHashMap<String,String> identificativoPortaDefault_mappingProtocol = new ConcurrentHashMap<String, String>();
	public String getIdentificativoPortaDefault(String protocol){
		
		if(protocol==null){
			return getIdentificativoPortaDefault();
		}
		
		if(OpenSPCoop2Properties.identificativoPortaDefault_mappingProtocol.containsKey(protocol)==false){
			
			try{  
				
				String fault = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd."+protocol+".identificativoPorta.dominio"); 
				if(fault==null){
					fault = this.getIdentificativoPortaDefault();
				}else{
					fault = fault.trim();
				}
				OpenSPCoop2Properties.identificativoPortaDefault_mappingProtocol.put(protocol, fault);

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd."+protocol+".identificativoPorta.dominio': "+e.getMessage(),e);
				return this.getIdentificativoPortaDefault();
			}
		}

		return OpenSPCoop2Properties.identificativoPortaDefault_mappingProtocol.get(protocol);
	}

	private static ConcurrentHashMap<String,String> nomePortaDefault_mappingProtocol = new ConcurrentHashMap<String, String>();
	public String getNomePortaDefault(String protocol){
		
		if(protocol==null){
			return getNomePortaDefault();
		}
		
		if(OpenSPCoop2Properties.nomePortaDefault_mappingProtocol.containsKey(protocol)==false){
			
			try{  
				
				String nome = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd."+protocol+".identificativoPorta.nome"); 
				if(nome==null){
					nome = this.getNomePortaDefault();
				}else{
					nome = nome.trim();
				}
				OpenSPCoop2Properties.nomePortaDefault_mappingProtocol.put(protocol, nome);

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd."+protocol+".identificativoPorta.nome': "+e.getMessage(),e);
				return this.getNomePortaDefault();
			}
		}

		return OpenSPCoop2Properties.nomePortaDefault_mappingProtocol.get(protocol);
	}
	
	private static ConcurrentHashMap<String,String> tipoPortaDefault_mappingProtocol = new ConcurrentHashMap<String, String>();
	public String getTipoPortaDefault(String protocol){
		
		if(protocol==null){
			return getTipoPortaDefault();
		}
		
		if(OpenSPCoop2Properties.tipoPortaDefault_mappingProtocol.containsKey(protocol)==false){
			
			try{  
				
				String tipo = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd."+protocol+".identificativoPorta.tipo"); 
				if(tipo==null){
					tipo = this.getTipoPortaDefault();
				}else{
					tipo = tipo.trim();
				}
				OpenSPCoop2Properties.tipoPortaDefault_mappingProtocol.put(protocol, tipo);

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd."+protocol+".identificativoPorta.tipo': "+e.getMessage(),e);
				return this.getTipoPortaDefault();
			}
		}

		return OpenSPCoop2Properties.tipoPortaDefault_mappingProtocol.get(protocol);
	}

	private static ConcurrentHashMap<String,IDSoggetto> identitaPortaDefault_mappingProtocol = new ConcurrentHashMap<String, IDSoggetto>();
	public IDSoggetto getIdentitaPortaDefault(String protocol){
		
		if(protocol==null){
			return getIdentitaPortaDefault();
		}
		
		if(OpenSPCoop2Properties.identitaPortaDefault_mappingProtocol.containsKey(protocol)==false){
			String pdd = getIdentificativoPortaDefault(protocol);
			String nome = getNomePortaDefault(protocol);
			String tipo = getTipoPortaDefault(protocol);
			if(tipo!=null && nome!=null && pdd!=null){
				IDSoggetto identitaPortaDefault = new IDSoggetto(tipo,nome,pdd);
				identitaPortaDefault_mappingProtocol.put(protocol, identitaPortaDefault);
			}
		}
		
		IDSoggetto identitaPortaDefault = OpenSPCoop2Properties.identitaPortaDefault_mappingProtocol.get(protocol);
		
		if(identitaPortaDefault!=null){
			IDSoggetto idNew = new IDSoggetto();
			idNew.setCodicePorta(identitaPortaDefault.getCodicePorta());
			idNew.setNome(identitaPortaDefault.getNome());
			idNew.setTipo(identitaPortaDefault.getTipo());
			return idNew;
		}
		else{
			return null;
		}
	}






	/************ AUTORIZZAZIONE BUSTE *******************/

	/**
	 * Restituisce il tipo di autorizzazione delle buste effettuato dalla porta di dominio
	 *
	 * @return Restituisce il tipo di autorizzazione delle buste effettuato dalla porta di dominio
	 * 
	 */
	private static String tipoAutorizzazioneBuste = null;
	public String getTipoAutorizzazioneBuste(){
		if(OpenSPCoop2Properties.tipoAutorizzazioneBuste==null){
			try{  
				String autorizzazione = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.autorizzazioneBuste.tipo");
				if(autorizzazione==null)
					throw new Exception("non definita");
				autorizzazione = autorizzazione.trim();
				OpenSPCoop2Properties.tipoAutorizzazioneBuste = autorizzazione;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.autorizzazioneBuste.tipo': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoAutorizzazioneBuste = null;
			}
		}

		return OpenSPCoop2Properties.tipoAutorizzazioneBuste;
	}

	






	/* ********  Bypass Filtri  ******** */

	/**
	 * Restituisce le proprieta' che localizzano gli header element su cui deve essere applicato il filtro bypass.
	 *
	 * @return proprieta' che localizzano gli header element su cui deve essere applicato il filtro bypass.
	 * 
	 */
	private static java.util.concurrent.ConcurrentHashMap<String,List<NameValue>> mapGetBypassFilterMustUnderstandProperties = null;
	public List<NameValue> getBypassFilterMustUnderstandProperties(String protocol) {
		if(OpenSPCoop2Properties.mapGetBypassFilterMustUnderstandProperties==null){
			initBypassFilterMustUnderstandProperties();
		}
		return OpenSPCoop2Properties.mapGetBypassFilterMustUnderstandProperties.get(protocol);
	}
	public void initBypassFilterMustUnderstandProperties(){
		if(OpenSPCoop2Properties.mapGetBypassFilterMustUnderstandProperties==null){
			
			OpenSPCoop2Properties.mapGetBypassFilterMustUnderstandProperties = new java.util.concurrent.ConcurrentHashMap<String, List<NameValue>>();
			
			List<NameValue> resultList = new ArrayList<NameValue>();
			try{ 
				java.util.Properties tmpP = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.services.BypassMustUnderstandHandler.header.");
				if(tmpP!=null && tmpP.size()>0){
					Enumeration<Object> keys = tmpP.keys();
					while (keys.hasMoreElements()) {
						Object object = (Object) keys.nextElement();
						if(object instanceof String){
							String key = (String) object;
							String localName = key;
							String namespace = tmpP.getProperty(key);
							if(key.contains("!")){
								// serve a fornire più proprietà con stesso localName e namespace differente
								// tramite la formula 
								// org.openspcoop2.pdd.services.BypassMustUnderstandHandler.header.nomeHeader=http://prova
								// org.openspcoop2.pdd.services.BypassMustUnderstandHandler.header.nomeHeader!1=http://prova2
								// org.openspcoop2.pdd.services.BypassMustUnderstandHandler.header.nomeHeader!2=http://prova3
								localName = key.split("!")[0];
							}
							NameValue nameValue = new NameValue();
							nameValue.setName(localName);
							nameValue.setValue(namespace);
							resultList.add(nameValue);
						}
					}
				}
				
				
				// aggiungo i bypass specifici dei protocolli
				Enumeration<String> protocolli = ProtocolFactoryManager.getInstance().getProtocolFactories().keys();
				while (protocolli.hasMoreElements()) {
					String protocollo = (String) protocolli.nextElement();
					IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactories().get(protocollo);
					IProtocolConfiguration pc = pf.createProtocolConfiguration();
					List<BypassMustUnderstandCheck> list = pc.getBypassMustUnderstandCheck();
					
					List<NameValue> resultListForProtocol = new ArrayList<NameValue>();
					if(resultList!=null && resultList.size()>0){
						resultListForProtocol.addAll(resultList);
					}
					
					if(list!=null && list.size()>0){
						for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
							BypassMustUnderstandCheck bypassMustUnderstandCheck = (BypassMustUnderstandCheck) iterator.next();
							
							NameValue nameValue = new NameValue();
							nameValue.setName(bypassMustUnderstandCheck.getElementName());
							nameValue.setValue(bypassMustUnderstandCheck.getNamespace());
							resultListForProtocol.add(nameValue);
							
						}
					}
					
					for (NameValue nameValue : resultListForProtocol) {
						this.log.debug("["+protocollo+"] ["+nameValue.getName()+"]=["+nameValue.getValue()+"]");
					}
					
					OpenSPCoop2Properties.mapGetBypassFilterMustUnderstandProperties.put(protocollo, resultListForProtocol);
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle propriete' 'org.openspcoop2.pdd.services.BypassMustUnderstandHandler.header.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.mapGetBypassFilterMustUnderstandProperties = null;
			}    
			
		}

	}

	/**
	 * Restituisce l'indicazione se il BypassFilterMustUnderstand e' abilitato per tutti gli header
	 *
	 * @return Restituisce l'indicazione se il BypassFilterMustUnderstand e' abilitato per tutti gli header
	 * 
	 */
	private static Boolean isBypassFilterMustUnderstandEnabledForAllHeaders = null;
	public boolean isBypassFilterMustUnderstandEnabledForAllHeaders(){
		if(OpenSPCoop2Properties.isBypassFilterMustUnderstandEnabledForAllHeaders==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.BypassMustUnderstandHandler.allHeaders"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isBypassFilterMustUnderstandEnabledForAllHeaders = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.BypassMustUnderstandHandler.allHeaders' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isBypassFilterMustUnderstandEnabledForAllHeaders = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.BypassMustUnderstandHandler.allHeaders' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isBypassFilterMustUnderstandEnabledForAllHeaders = false;
			}
		}

		return OpenSPCoop2Properties.isBypassFilterMustUnderstandEnabledForAllHeaders;
	}





	
	
	/* ------------- ContentType ---------------------*/
	
	private static Boolean isControlloContentTypeAbilitatoRicezioneContenutiApplicativi= null;
	public boolean isControlloContentTypeAbilitatoRicezioneContenutiApplicativi(){
		if(OpenSPCoop2Properties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.contentType.checkEnabled"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.contentType.checkEnabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi = true;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.contentType.checkEnabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi = true;
			}
		}
		return OpenSPCoop2Properties.isControlloContentTypeAbilitatoRicezioneContenutiApplicativi;
	}

	private static Boolean isControlloContentTypeAbilitatoRicezioneBuste= null;
	public boolean isControlloContentTypeAbilitatoRicezioneBuste(){
		if(OpenSPCoop2Properties.isControlloContentTypeAbilitatoRicezioneBuste==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneBuste.contentType.checkEnabled"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isControlloContentTypeAbilitatoRicezioneBuste = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.contentType.checkEnabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isControlloContentTypeAbilitatoRicezioneBuste = true;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.contentType.checkEnabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloContentTypeAbilitatoRicezioneBuste = true;
			}
		}
		return OpenSPCoop2Properties.isControlloContentTypeAbilitatoRicezioneBuste;
	}
	
	private static Boolean isPrintInfoCertificate= null;
	public boolean isPrintInfoCertificate(){
		if(OpenSPCoop2Properties.isPrintInfoCertificate==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.certificate.printInfo"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isPrintInfoCertificate = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.certificate.printInfo' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isPrintInfoCertificate = false;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.certificate.printInfo' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isPrintInfoCertificate = false;
			}
		}
		return OpenSPCoop2Properties.isPrintInfoCertificate;
	}
	
	
	/* ------------- Gestore Credenziali (Porta Delegata) ---------------------*/

	private static Boolean isGestoreCredenzialiPortaDelegataEnabled= null;
	public boolean isGestoreCredenzialiPortaDelegataEnabled(){
		if(OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataEnabled==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.enabled"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.enabled' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataEnabled = false;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.enabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataEnabled = false;
			}
		}
		return OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataEnabled;
	}
	
	private static String getGestoreCredenzialiPortaDelegataNome = null;
	public String getGestoreCredenzialiPortaDelegataNome() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataNome==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.nome"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataNome = value;
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.nome' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataNome;
	}
	
	private static TipoAutenticazioneGestoreCredenziali getGestoreCredenzialiPortaDelegataTipoAutenticazioneCanale = null;
	public TipoAutenticazioneGestoreCredenziali getGestoreCredenzialiPortaDelegataTipoAutenticazioneCanale() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataTipoAutenticazioneCanale==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataTipoAutenticazioneCanale = TipoAutenticazioneGestoreCredenziali.toEnumConstant(value);
					if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataTipoAutenticazioneCanale==null) {
						throw new Exception("Valore indicato non gestito '"+value+"'");
					}
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataTipoAutenticazioneCanale;
	}
	
	private static String getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicUsername = null;
	public String getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicUsername() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicUsername==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale.basic.username"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicUsername = value;
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale.basic.username' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicUsername;
	}
	
	private static String getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicPassword = null;
	public String getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicPassword() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicPassword==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale.basic.password"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicPassword = value;
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale.basic.password' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleBasicPassword;
	}
	
	private static String getGestoreCredenzialiPortaDelegataAutenticazioneCanaleSslSubject = null;
	public String getGestoreCredenzialiPortaDelegataAutenticazioneCanaleSslSubject() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleSslSubject==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale.ssl.subject"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleSslSubject = value;
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale.ssl.subject' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanaleSslSubject;
	}
	
	private static String getGestoreCredenzialiPortaDelegataAutenticazioneCanalePrincipal = null;
	public String getGestoreCredenzialiPortaDelegataAutenticazioneCanalePrincipal() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanalePrincipal==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale.principal"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanalePrincipal = value;
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.autenticazioneCanale.principal' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataAutenticazioneCanalePrincipal;
	}
	
	private static Boolean getGestoreCredenzialiPortaDelegataHeaderBasicUsername_Read = null;
	private static String getGestoreCredenzialiPortaDelegataHeaderBasicUsername = null;
	public String getGestoreCredenzialiPortaDelegataHeaderBasicUsername() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderBasicUsername_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.basic.username"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderBasicUsername = value;
				}
				getGestoreCredenzialiPortaDelegataHeaderBasicUsername_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.basic.username' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderBasicUsername;
	}
	
	private static Boolean getGestoreCredenzialiPortaDelegataHeaderBasicPassword_Read = null;
	private static String getGestoreCredenzialiPortaDelegataHeaderBasicPassword = null;
	public String getGestoreCredenzialiPortaDelegataHeaderBasicPassword() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderBasicPassword_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.basic.password"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderBasicPassword = value;
				}
				getGestoreCredenzialiPortaDelegataHeaderBasicPassword_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.basic.password' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderBasicPassword;
	}
	
	private static Boolean getGestoreCredenzialiPortaDelegataHeaderSslSubject_Read = null;
	private static String getGestoreCredenzialiPortaDelegataHeaderSslSubject = null;
	public String getGestoreCredenzialiPortaDelegataHeaderSslSubject() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslSubject_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.subject"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslSubject = value;
				}
				getGestoreCredenzialiPortaDelegataHeaderSslSubject_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.subject' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslSubject;
	}
	
	private static Boolean getGestoreCredenzialiPortaDelegataHeaderSslIssuer_Read = null;
	private static String getGestoreCredenzialiPortaDelegataHeaderSslIssuer = null;
	public String getGestoreCredenzialiPortaDelegataHeaderSslIssuer() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslIssuer_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.issuer"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslIssuer = value;
				}
				getGestoreCredenzialiPortaDelegataHeaderSslIssuer_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.issuer' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslIssuer;
	}
	
	private static Boolean getGestoreCredenzialiPortaDelegataHeaderSslCertificate_Read = null;
	private static String getGestoreCredenzialiPortaDelegataHeaderSslCertificate = null;
	public String getGestoreCredenzialiPortaDelegataHeaderSslCertificate() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslCertificate_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslCertificate = value;
				}
				getGestoreCredenzialiPortaDelegataHeaderSslCertificate_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslCertificate;
	}
	
	private static Boolean isGestoreCredenzialiPortaDelegataHeaderSslCertificateUrlDecode= null;
	public boolean isGestoreCredenzialiPortaDelegataHeaderSslCertificateUrlDecode(){
		if(OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateUrlDecode==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.url_decode"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateUrlDecode = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.url_decode' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateUrlDecode = true;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.url_decode' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateUrlDecode = true;
			}
		}
		return OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateUrlDecode;
	}
	
	private static Boolean isGestoreCredenzialiPortaDelegataHeaderSslCertificateBase64Decode= null;
	public boolean isGestoreCredenzialiPortaDelegataHeaderSslCertificateBase64Decode(){
		if(OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateBase64Decode==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.base64_decode"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateBase64Decode = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.base64_decode' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateBase64Decode = true;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.base64_decode' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateBase64Decode = true;
			}
		}
		return OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateBase64Decode;
	}
	
	private static Boolean isGestoreCredenzialiPortaDelegataHeaderSslCertificateEnrich_BEGIN_END= null;
	public boolean isGestoreCredenzialiPortaDelegataHeaderSslCertificateEnrich_BEGIN_END(){
		if(OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateEnrich_BEGIN_END==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.enrich_BEGIN_END"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateEnrich_BEGIN_END = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.enrich_BEGIN_END' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateEnrich_BEGIN_END = false;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.enrich_BEGIN_END' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateEnrich_BEGIN_END = false;
			}
		}
		return OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateEnrich_BEGIN_END;
	}
	
	private static Boolean isGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters= null;
	public boolean isGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters(){
		if(OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.replaceCharacters"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.replaceCharacters' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters = false;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.replaceCharacters' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters = false;
			}
		}
		return OpenSPCoop2Properties.isGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters;
	}
	
	private static String getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_source = null;
	private static Boolean getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_source_read = null;
	public String getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_source() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_source_read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.replaceCharacters.source"); 

				if(value!=null){
					value = value.trim();
					if(StringUtils.isNotEmpty(value)) {
						if("\\t".equals(value)) {
							value = "\t";
						}
						else if("\\n".equals(value)) {
							value = "\n";
						}
						OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_source = value;
					}
				}
				
				getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_source_read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.replaceCharacters.source' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_source;
	}
	
	private static String getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_dest = null;
	private static Boolean getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_dest_read = null;
	public String getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_dest() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_dest_read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.replaceCharacters.dest"); 

				if(value!=null){
					value = value.trim();
					if(StringUtils.isNotEmpty(value)) {
						if("\\t".equals(value)) {
							value = "\t";
						}
						else if("\\n".equals(value)) {
							value = "\n";
						}
						OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_dest = value;
					}
				}
				
				getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_dest_read = true;
				
			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.ssl.certificate.replaceCharacters.dest' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderSslCertificateReplaceCharacters_dest;
	}
	
	private static Boolean getGestoreCredenzialiPortaDelegataHeaderPrincipal_Read = null;
	private static String getGestoreCredenzialiPortaDelegataHeaderPrincipal = null;
	public String getGestoreCredenzialiPortaDelegataHeaderPrincipal() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderPrincipal_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.principal"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderPrincipal = value;
				}
				getGestoreCredenzialiPortaDelegataHeaderPrincipal_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoreCredenziali.header.principal' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaDelegataHeaderPrincipal;
	}
	
	
	
	/* ------------- Gestore Credenziali (Porta Applicativa) ---------------------*/

	private static Boolean isGestoreCredenzialiPortaApplicativaEnabled= null;
	public boolean isGestoreCredenzialiPortaApplicativaEnabled(){
		if(OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaEnabled==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.enabled"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.enabled' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaEnabled = false;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.enabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaEnabled = false;
			}
		}
		return OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaEnabled;
	}
	
	private static String getGestoreCredenzialiPortaApplicativaNome = null;
	public String getGestoreCredenzialiPortaApplicativaNome() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaNome==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.nome"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaNome = value;
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.nome' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaNome;
	}
	
	private static TipoAutenticazioneGestoreCredenziali getGestoreCredenzialiPortaApplicativaTipoAutenticazioneCanale = null;
	public TipoAutenticazioneGestoreCredenziali getGestoreCredenzialiPortaApplicativaTipoAutenticazioneCanale() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaTipoAutenticazioneCanale==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaTipoAutenticazioneCanale = TipoAutenticazioneGestoreCredenziali.toEnumConstant(value);
					if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaTipoAutenticazioneCanale==null) {
						throw new Exception("Valore indicato non gestito '"+value+"'");
					}
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaTipoAutenticazioneCanale;
	}
	
	private static String getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicUsername = null;
	public String getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicUsername() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicUsername==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.basic.username"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicUsername = value;
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.basic.username' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicUsername;
	}
	
	private static String getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicPassword = null;
	public String getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicPassword() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicPassword==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.basic.password"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicPassword = value;
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.basic.password' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleBasicPassword;
	}
	
	private static String getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleSslSubject = null;
	public String getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleSslSubject() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleSslSubject==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.ssl.subject"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleSslSubject = value;
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.ssl.subject' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanaleSslSubject;
	}
	
	private static Boolean getGestoreCredenzialiPortaApplicativaHeaderSslIssuer_Read = null;
	private static String getGestoreCredenzialiPortaApplicativaHeaderSslIssuer = null;
	public String getGestoreCredenzialiPortaApplicativaHeaderSslIssuer() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslIssuer_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.issuer"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslIssuer = value;
				}
				getGestoreCredenzialiPortaApplicativaHeaderSslIssuer_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.issuer' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslIssuer;
	}
	
	private static Boolean getGestoreCredenzialiPortaApplicativaHeaderSslCertificate_Read = null;
	private static String getGestoreCredenzialiPortaApplicativaHeaderSslCertificate = null;
	public String getGestoreCredenzialiPortaApplicativaHeaderSslCertificate() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslCertificate_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslCertificate = value;
				}
				getGestoreCredenzialiPortaApplicativaHeaderSslCertificate_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslCertificate;
	}
	
	private static Boolean isGestoreCredenzialiPortaApplicativaHeaderSslCertificateUrlDecode= null;
	public boolean isGestoreCredenzialiPortaApplicativaHeaderSslCertificateUrlDecode(){
		if(OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateUrlDecode==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.url_decode"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateUrlDecode = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.url_decode' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateUrlDecode = true;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.url_decode' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateUrlDecode = true;
			}
		}
		return OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateUrlDecode;
	}
	
	private static Boolean isGestoreCredenzialiPortaApplicativaHeaderSslCertificateBase64Decode= null;
	public boolean isGestoreCredenzialiPortaApplicativaHeaderSslCertificateBase64Decode(){
		if(OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateBase64Decode==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.base64_decode"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateBase64Decode = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.base64_decode' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateBase64Decode = true;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.base64_decode' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateBase64Decode = true;
			}
		}
		return OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateBase64Decode;
	}
	
	private static Boolean isGestoreCredenzialiPortaApplicativaHeaderSslCertificateEnrich_BEGIN_END= null;
	public boolean isGestoreCredenzialiPortaApplicativaHeaderSslCertificateEnrich_BEGIN_END(){
		if(OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateEnrich_BEGIN_END==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.enrich_BEGIN_END"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateEnrich_BEGIN_END = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.enrich_BEGIN_END' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateEnrich_BEGIN_END = false;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.enrich_BEGIN_END' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateEnrich_BEGIN_END = false;
			}
		}
		return OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateEnrich_BEGIN_END;
	}
	
	private static Boolean isGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters= null;
	public boolean isGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters(){
		if(OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.replaceCharacters"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.replaceCharacters' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters = false;
				}

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.replaceCharacters' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters = false;
			}
		}
		return OpenSPCoop2Properties.isGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters;
	}
	
	private static String getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_source = null;
	private static Boolean getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_source_read = null;
	public String getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_source() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_source_read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.replaceCharacters.source"); 

				if(value!=null){
					value = value.trim();
					if(StringUtils.isNotEmpty(value)) {
						if("\\t".equals(value)) {
							value = "\t";
						}
						else if("\\n".equals(value)) {
							value = "\n";
						}
						OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_source = value;
					}
				}
				
				getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_source_read = true;
				
			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.replaceCharacters.source' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_source;
	}
	
	private static String getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_dest = null;
	private static Boolean getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_dest_read = null;
	public String getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_dest() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_dest_read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.replaceCharacters.dest"); 

				if(value!=null){
					value = value.trim();
					if(StringUtils.isNotEmpty(value)) {
						if("\\t".equals(value)) {
							value = "\t";
						}
						else if("\\n".equals(value)) {
							value = "\n";
						}
						OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_dest = value;
					}
				}
				
				getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_dest_read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.certificate.replaceCharacters.dest' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslCertificateReplaceCharacters_dest;
	}
	
	private static String getGestoreCredenzialiPortaApplicativaAutenticazioneCanalePrincipal = null;
	public String getGestoreCredenzialiPortaApplicativaAutenticazioneCanalePrincipal() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanalePrincipal==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.principal"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanalePrincipal = value;
				}else{
					throw new Exception("Proprieta' non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.autenticazioneCanale.principal' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaAutenticazioneCanalePrincipal;
	}
	
	private static Boolean getGestoreCredenzialiPortaApplicativaHeaderBasicUsername_Read = null;
	private static String getGestoreCredenzialiPortaApplicativaHeaderBasicUsername = null;
	public String getGestoreCredenzialiPortaApplicativaHeaderBasicUsername() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderBasicUsername_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.basic.username"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderBasicUsername = value;
				}
				getGestoreCredenzialiPortaApplicativaHeaderBasicUsername_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.basic.username' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderBasicUsername;
	}
	
	private static Boolean getGestoreCredenzialiPortaApplicativaHeaderBasicPassword_Read = null;
	private static String getGestoreCredenzialiPortaApplicativaHeaderBasicPassword = null;
	public String getGestoreCredenzialiPortaApplicativaHeaderBasicPassword() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderBasicPassword_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.basic.password"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderBasicPassword = value;
				}
				getGestoreCredenzialiPortaApplicativaHeaderBasicPassword_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.basic.password' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderBasicPassword;
	}
	
	private static Boolean getGestoreCredenzialiPortaApplicativaHeaderSslSubject_Read = null;
	private static String getGestoreCredenzialiPortaApplicativaHeaderSslSubject = null;
	public String getGestoreCredenzialiPortaApplicativaHeaderSslSubject() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslSubject_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.subject"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslSubject = value;
				}
				getGestoreCredenzialiPortaApplicativaHeaderSslSubject_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.ssl.subject' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderSslSubject;
	}
	
	private static Boolean getGestoreCredenzialiPortaApplicativaHeaderPrincipal_Read = null;
	private static String getGestoreCredenzialiPortaApplicativaHeaderPrincipal = null;
	public String getGestoreCredenzialiPortaApplicativaHeaderPrincipal() throws Exception{
		if(OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderPrincipal_Read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.principal"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderPrincipal = value;
				}
				getGestoreCredenzialiPortaApplicativaHeaderPrincipal_Read = true;

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoreCredenziali.header.principal' errata:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg,e);
			}
		}
		return OpenSPCoop2Properties.getGestoreCredenzialiPortaApplicativaHeaderPrincipal;
	}
	


	/* ********  NODE RECEIVER  ******** */


	/**
	 * Restituisce il Timeout di attesa di una risposta applicativa
	 *
	 * @return Restituisce il Timeout di attesa di una risposta applicativa
	 * 
	 */
	private static Long nodeReceiverTimeout = null;
	public long getNodeReceiverTimeout() {	
		if(OpenSPCoop2Properties.nodeReceiverTimeout == null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.nodeReceiver.timeout");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.nodeReceiverTimeout = java.lang.Long.parseLong(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.timeout' non impostata, viene utilizzato il default="+CostantiPdD.NODE_RECEIVER_ATTESA_ATTIVA);
					OpenSPCoop2Properties.nodeReceiverTimeout = CostantiPdD.NODE_RECEIVER_ATTESA_ATTIVA;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.timeout' non impostata, viene utilizzato il default="+CostantiPdD.NODE_RECEIVER_ATTESA_ATTIVA+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.nodeReceiverTimeout = CostantiPdD.NODE_RECEIVER_ATTESA_ATTIVA;
			}    
		}

		return OpenSPCoop2Properties.nodeReceiverTimeout;
	} 
	/**
	 * Restituisce il Timeout di attesa di una risposta applicativa
	 *
	 * @return Restituisce il Timeout di attesa di una risposta applicativa
	 * 
	 */
	private static Long nodeReceiverTimeoutRicezioneContenutiApplicativi = null;
	public long getNodeReceiverTimeoutRicezioneContenutiApplicativi() {	
		if(OpenSPCoop2Properties.nodeReceiverTimeoutRicezioneContenutiApplicativi == null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.nodeReceiver.ricezioneContenutiApplicativi.timeout");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.nodeReceiverTimeoutRicezioneContenutiApplicativi = java.lang.Long.parseLong(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.ricezioneContenutiApplicativi.timeout' non impostata, viene utilizzato il default="+this.getNodeReceiverTimeout());
					OpenSPCoop2Properties.nodeReceiverTimeoutRicezioneContenutiApplicativi = this.getNodeReceiverTimeout();
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.ricezioneContenutiApplicativi.timeout' non impostata, viene utilizzato il default="+this.getNodeReceiverTimeout()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.nodeReceiverTimeoutRicezioneContenutiApplicativi = this.getNodeReceiverTimeout();
			}    
		}

		return OpenSPCoop2Properties.nodeReceiverTimeoutRicezioneContenutiApplicativi;
	} 
	/**
	 * Restituisce il Timeout di attesa di una busta
	 *
	 * @return Restituisce il Timeout di attesa di una busta
	 * 
	 */
	private static Long nodeReceiverTimeoutRicezioneBuste = null;
	public long getNodeReceiverTimeoutRicezioneBuste() {	
		if(OpenSPCoop2Properties.nodeReceiverTimeoutRicezioneBuste == null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.nodeReceiver.ricezioneBuste.timeout");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.nodeReceiverTimeoutRicezioneBuste = java.lang.Long.parseLong(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.ricezioneBuste.timeout' non impostata, viene utilizzato il default="+this.getNodeReceiverTimeout());
					OpenSPCoop2Properties.nodeReceiverTimeoutRicezioneBuste = this.getNodeReceiverTimeout();
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.ricezioneBuste.timeout' non impostata, viene utilizzato il default="+this.getNodeReceiverTimeout()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.nodeReceiverTimeoutRicezioneBuste = this.getNodeReceiverTimeout();
			}    
		}

		return OpenSPCoop2Properties.nodeReceiverTimeoutRicezioneBuste;
	} 
	/**
	 * Restituisce la Frequenza di check sulla coda RicezioneBuste/RicezioneContenutiApplicativi
	 *
	 * @return Restituisce la Frequenza di check sulla coda RicezioneBuste/RicezioneContenutiApplicativi
	 * 
	 */
	private static Integer nodeReceiverCheckInterval = null;
	public int getNodeReceiverCheckInterval() {	
		if(OpenSPCoop2Properties.nodeReceiverCheckInterval==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.nodeReceiver.check");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.nodeReceiverCheckInterval = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.check' non impostata, viene utilizzato il default="+CostantiPdD.NODE_RECEIVER_CHECK_INTERVAL);
					OpenSPCoop2Properties.nodeReceiverCheckInterval = CostantiPdD.NODE_RECEIVER_CHECK_INTERVAL;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.check' non impostata, viene utilizzato il default="+CostantiPdD.NODE_RECEIVER_CHECK_INTERVAL+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.nodeReceiverCheckInterval = CostantiPdD.NODE_RECEIVER_CHECK_INTERVAL;
			}    
		}

		return OpenSPCoop2Properties.nodeReceiverCheckInterval;
	}

	/**
	 * Restituisce l'intervallo di check su DB effettuata dal TransactionManager, in caso di cache attiva
	 * 
	 * @return Restituisce intervallo di check su DB effettuata dal TransactionManager, in caso di cache attiva
	 * 
	 */
	private static Integer nodeReceiverCheckDBInterval = null;
	public int getNodeReceiverCheckDBInterval() {	
		if(OpenSPCoop2Properties.nodeReceiverCheckDBInterval==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.nodeReceiver.checkDB");

				if(name!=null){
					name = name.trim();
					int time = java.lang.Integer.parseInt(name);
					OpenSPCoop2Properties.nodeReceiverCheckDBInterval = time;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.checkDB' non impostata, viene utilizzato il default="+CostantiPdD.NODE_RECEIVER_CHECK_DB_INTERVAL);
					OpenSPCoop2Properties.nodeReceiverCheckDBInterval = CostantiPdD.NODE_RECEIVER_CHECK_DB_INTERVAL;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.checkDB' non impostata, viene utilizzato il default="+CostantiPdD.NODE_RECEIVER_CHECK_DB_INTERVAL+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.nodeReceiverCheckDBInterval = CostantiPdD.NODE_RECEIVER_CHECK_DB_INTERVAL;
			}  
		}

		return OpenSPCoop2Properties.nodeReceiverCheckDBInterval;
	}

	/**
	 * Restituisce il nodeReceiver da utilizzare per le comunicazioni infrastrutturali 
	 * 
	 * @return  il nodeReceiver da utilizzare per le comunicazioni infrastrutturali 
	 */
	private static String nodeReceiver = null;
	public String getNodeReceiver() {
		if(OpenSPCoop2Properties.nodeReceiver==null){
			try{ 
				OpenSPCoop2Properties.nodeReceiver = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.nodeReceiver");
				if(OpenSPCoop2Properties.nodeReceiver==null)
					throw new Exception("non definita");
				OpenSPCoop2Properties.nodeReceiver = OpenSPCoop2Properties.nodeReceiver.trim();

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver': "+e.getMessage(),e);
				OpenSPCoop2Properties.nodeReceiver = null;
			}    
		}

		return OpenSPCoop2Properties.nodeReceiver;
	}

	/**
	 * Restituisce l'indicazione se il NodeReceiver deve essere utilizzato in single connection
	 *
	 * @return Restituisce l'indicazione se il NodeReceiver deve essere utilizzato in single connection
	 * 
	 */
	private static Boolean singleConnection_nodeReceiver_value = null;
	public boolean singleConnection_NodeReceiver(){

		if(OpenSPCoop2Properties.singleConnection_nodeReceiver_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.nodeReceiver.singleConnection"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.singleConnection_nodeReceiver_value = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.singleConnection' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.singleConnection_nodeReceiver_value = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.singleConnection' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.singleConnection_nodeReceiver_value = false;
			}
		}

		return OpenSPCoop2Properties.singleConnection_nodeReceiver_value;

	}




	/* ********  NODE SENDER  ******** */

	/**
	 * Restituisce il nodeSender da utilizzare per le comunicazioni infrastrutturali 
	 * 
	 * @return  il nodeSender da utilizzare per le comunicazioni infrastrutturali 
	 */
	private static String nodeSender = null;
	public String getNodeSender() {
		if(OpenSPCoop2Properties.nodeSender==null){
			try{ 
				OpenSPCoop2Properties.nodeSender = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.nodeSender");
				if(OpenSPCoop2Properties.nodeSender==null)
					throw new Exception("non definita");
				OpenSPCoop2Properties.nodeSender = OpenSPCoop2Properties.nodeSender.trim();

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.nodeSender': "+e.getMessage(),e);
				OpenSPCoop2Properties.nodeSender = null;
			}    
		}

		return OpenSPCoop2Properties.nodeSender;
	}








	/* ********  EJB  ******** */

	/**
	 * Restituisce il Timeout di attesa per i deploy dei timer
	 *
	 * @return Restituisce il Timeout di attesa per i deploy dei timer
	 * 
	 */
	private static Long timerEJBDeployTimeout = null;
	public long getTimerEJBDeployTimeout() {	
		if(OpenSPCoop2Properties.timerEJBDeployTimeout==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.timeout");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.timerEJBDeployTimeout = java.lang.Long.parseLong(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.timeout' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_EJB_ATTESA_ATTIVA);
					OpenSPCoop2Properties.timerEJBDeployTimeout = CostantiPdD.TIMER_EJB_ATTESA_ATTIVA;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.timeout' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_EJB_ATTESA_ATTIVA+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.timerEJBDeployTimeout = CostantiPdD.TIMER_EJB_ATTESA_ATTIVA;
			}    
		}

		return OpenSPCoop2Properties.timerEJBDeployTimeout;
	} 
	/**
	 * Restituisce la Frequenza di check per il deploy dei timer
	 *
	 * @return Restituisce la Frequenza di check per il deploy dei timer
	 * 
	 */
	private static Integer timerEJBDeployCheckInterval = null;
	public int getTimerEJBDeployCheckInterval() {	
		if(OpenSPCoop2Properties.timerEJBDeployCheckInterval==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.check");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.timerEJBDeployCheckInterval = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.check' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_EJB_CHECK_INTERVAL);
					OpenSPCoop2Properties.timerEJBDeployCheckInterval = CostantiPdD.TIMER_EJB_CHECK_INTERVAL;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.check' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_EJB_CHECK_INTERVAL+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.timerEJBDeployCheckInterval = CostantiPdD.TIMER_EJB_CHECK_INTERVAL;
			}  
		}

		return OpenSPCoop2Properties.timerEJBDeployCheckInterval;
	}








	/* ********  TRANSACTION MANAGER  ******** */

	/**
	 * Restituisce l'intervallo in millisecondi di attesa attiva effettuato dal Transaction Manager (Default 2 minuti)
	 * 
	 * @return Restituisce l'intervallo in millisecondi di attesa attiva effettuato dal Transaction Manager (Default 2 minuti)
	 *  * 
	 */
	private static Long transactionManager_AttesaAttiva = null;
	public long getTransactionManager_AttesaAttiva() {	
		if(OpenSPCoop2Properties.transactionManager_AttesaAttiva==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.TransactionManager.attesaAttiva");

				if(name!=null){
					name = name.trim();
					long time = java.lang.Long.parseLong(name);
					OpenSPCoop2Properties.transactionManager_AttesaAttiva = time*1000;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.attesaAttiva' non impostata, viene utilizzato il default="+CostantiPdD.TRANSACTION_MANAGER_ATTESA_ATTIVA);
					OpenSPCoop2Properties.transactionManager_AttesaAttiva = CostantiPdD.TRANSACTION_MANAGER_ATTESA_ATTIVA;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.attesaAttiva' non impostata, viene utilizzato il default="+CostantiPdD.TRANSACTION_MANAGER_ATTESA_ATTIVA+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.transactionManager_AttesaAttiva = CostantiPdD.TRANSACTION_MANAGER_ATTESA_ATTIVA;
			}    
		}

		return OpenSPCoop2Properties.transactionManager_AttesaAttiva;
	}

	/**
	 * Restituisce l'intervallo maggiore per frequenza di check nell'attesa attiva effettuata dal TransactionManager, in millisecondi
	 * 
	 * @return Restituisce Intervallo maggiore per frequenza di check nell'attesa attiva effettuata dal TransactionManager, in millisecondi
	 * 
	 */
	private static Integer transactionManager_CheckInterval = null;
	public int getTransactionManager_CheckInterval() {	
		if(OpenSPCoop2Properties.transactionManager_CheckInterval==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.TransactionManager.check");

				if(name!=null){
					name = name.trim();
					int time = java.lang.Integer.parseInt(name);
					OpenSPCoop2Properties.transactionManager_CheckInterval = time;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.check' non impostata, viene utilizzato il default="+CostantiPdD.TRANSACTION_MANAGER_CHECK_INTERVAL);
					OpenSPCoop2Properties.transactionManager_CheckInterval = CostantiPdD.TRANSACTION_MANAGER_CHECK_INTERVAL;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.check' non impostata, viene utilizzato il default="+CostantiPdD.TRANSACTION_MANAGER_CHECK_INTERVAL+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.transactionManager_CheckInterval = CostantiPdD.TRANSACTION_MANAGER_CHECK_INTERVAL;
			}  
		}

		return OpenSPCoop2Properties.transactionManager_CheckInterval;
	}

	/**
	 * Restituisce l'intervallo di check su DB effettuata dal TransactionManager, in caso di cache attiva
	 * 
	 * @return Restituisce intervallo di check su DB effettuata dal TransactionManager, in caso di cache attiva
	 * 
	 */
	private static Integer transactionManager_CheckDBInterval = null;
	public int getTransactionManager_CheckDBInterval() {	
		if(OpenSPCoop2Properties.transactionManager_CheckDBInterval==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.TransactionManager.checkDB");

				if(name!=null){
					name = name.trim();
					int time = java.lang.Integer.parseInt(name);
					OpenSPCoop2Properties.transactionManager_CheckDBInterval = time;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.checkDB' non impostata, viene utilizzato il default="+CostantiPdD.TRANSACTION_MANAGER_CHECK_DB_INTERVAL);
					OpenSPCoop2Properties.transactionManager_CheckDBInterval = CostantiPdD.TRANSACTION_MANAGER_CHECK_DB_INTERVAL;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.checkDB' non impostata, viene utilizzato il default="+CostantiPdD.TRANSACTION_MANAGER_CHECK_DB_INTERVAL+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.transactionManager_CheckDBInterval = CostantiPdD.TRANSACTION_MANAGER_CHECK_DB_INTERVAL;
			}  
		}

		return OpenSPCoop2Properties.transactionManager_CheckDBInterval;
	}


	/**
	 * Restituisce l'indicazione se il TransactionManager deve essere utilizzato in single connection
	 *
	 * @return Restituisce l'indicazione se il TransactionManager deve essere utilizzato in single connection
	 * 
	 */
	private static Boolean singleConnection_TransactionManager_value = null;
	public boolean singleConnection_TransactionManager(){

		if(OpenSPCoop2Properties.singleConnection_TransactionManager_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.TransactionManager.singleConnection"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.singleConnection_TransactionManager_value = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.singleConnection' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.singleConnection_TransactionManager_value = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.singleConnection' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.singleConnection_TransactionManager_value = false;
			}
		}

		return OpenSPCoop2Properties.singleConnection_TransactionManager_value;

	}






	/* ********  SERIALIZABLE  ******** */

	/**
	 * Restituisce l'intervallo di Attesa attiva di default effettuata per la gestione del livello serializable nel DB, in millisecondi 
	 * 
	 * @return Restituisce l'intervallo di Attesa attiva di default effettuata per la gestione del livello serializable nel DB, in millisecondi 
	 * 
	 */
	private static Long gestioneSerializableDB_AttesaAttiva = null;
	public long getGestioneSerializableDB_AttesaAttiva() {	
		if(OpenSPCoop2Properties.gestioneSerializableDB_AttesaAttiva==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.jdbc.serializable.attesaAttiva");
				if (name != null) {
					name = name.trim();
					long time = java.lang.Long.parseLong(name);
					OpenSPCoop2Properties.gestioneSerializableDB_AttesaAttiva = time*1000;
				} else {
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.jdbc.serializable.attesaAttiva' non impostata, viene utilizzato il default="+Costanti.GESTIONE_SERIALIZABLE_ATTESA_ATTIVA);
					OpenSPCoop2Properties.gestioneSerializableDB_AttesaAttiva =  Costanti.GESTIONE_SERIALIZABLE_ATTESA_ATTIVA;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.jdbc.serializable.attesaAttiva' non impostata, viene utilizzato il default="+Costanti.GESTIONE_SERIALIZABLE_ATTESA_ATTIVA+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.gestioneSerializableDB_AttesaAttiva = Costanti.GESTIONE_SERIALIZABLE_ATTESA_ATTIVA;
			}   
		}

		return OpenSPCoop2Properties.gestioneSerializableDB_AttesaAttiva;
	}

	/**
	 * Restituisce l'intervallo maggiore per frequenza di check nell'attesa attiva effettuata per la gestione del livello serializable nel DB, in millisecondi
	 * 
	 * @return Restituisce Intervallo maggiore per frequenza di check nell'attesa attiva effettuata per la gestione del livello serializable nel DB, in millisecondi
	 */
	private static Integer gestioneSerializableDB_CheckInterval = null;
	public int getGestioneSerializableDB_CheckInterval() {	
		if(OpenSPCoop2Properties.gestioneSerializableDB_CheckInterval==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.jdbc.serializable.check");
				if (name != null){
					name = name.trim();
					int time = java.lang.Integer.parseInt(name);
					OpenSPCoop2Properties.gestioneSerializableDB_CheckInterval = time;
				} else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.jdbc.serializable.check' non impostata, viene utilizzato il default="+Costanti.GESTIONE_SERIALIZABLE_CHECK_INTERVAL);
					OpenSPCoop2Properties.gestioneSerializableDB_CheckInterval = Costanti.GESTIONE_SERIALIZABLE_CHECK_INTERVAL;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.jdbc.serializable.check' non impostata, viene utilizzato il default="+Costanti.GESTIONE_SERIALIZABLE_CHECK_INTERVAL+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.gestioneSerializableDB_CheckInterval = Costanti.GESTIONE_SERIALIZABLE_CHECK_INTERVAL;
			}    
		}

		return OpenSPCoop2Properties.gestioneSerializableDB_CheckInterval;
	}








	/* ********  LIBRERIA PROTOCOL  ******** */

	/**
	 * Restituisce il tipo di gestione del RepositoryBuste
	 * 
	 * @return Restituisce il tipo di gestione  del RepositoryBuste
	 */
	private static String gestoreRepositoryBuste = null;
	public String getGestoreRepositoryBuste() {
		if(OpenSPCoop2Properties.gestoreRepositoryBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.repository.gestore");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				
				if(CostantiConfigurazione.REPOSITORY_BUSTE_AUTO_BYTEWISE.equals(name)){
					if(this.getDatabaseType()!=null){
						name = GestoreRepositoryFactory.getTipoRepositoryBuste(this.getDatabaseType());
					}
					else{
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.repository.gestore': il valore '"+
								CostantiConfigurazione.REPOSITORY_BUSTE_AUTO_BYTEWISE+"' deve essere utilizzato in combinazione con la definizione del tipo di database del repository. Viene impostato come gestore il tipo di default: "+
								CostantiConfigurazione.REPOSITORY_BUSTE_DEFAULT);
						name = CostantiConfigurazione.REPOSITORY_BUSTE_DEFAULT;
					}
				}
				
				OpenSPCoop2Properties.gestoreRepositoryBuste = name;
			
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.repository.gestore': "+e.getMessage(),e);
				OpenSPCoop2Properties.gestoreRepositoryBuste = null;
			}    
		}

		return OpenSPCoop2Properties.gestoreRepositoryBuste;
	}

	/**
	 * Restituisce il tipo di filtro duplicati utilizzato dal RepositoryBuste
	 * 
	 * @return Restituisce il tipo di filtro duplicati utilizzato dal RepositoryBuste
	 */
	private static String gestoreFiltroDuplicatiRepositoryBuste = null;
	public String getGestoreFiltroDuplicatiRepositoryBuste() {
		if(OpenSPCoop2Properties.gestoreFiltroDuplicatiRepositoryBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.filtroDuplicati");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.filtroDuplicati' non impostata, viene utilizzato il default="+CostantiConfigurazione.FILTRO_DUPLICATI_OPENSPCOOP);
					OpenSPCoop2Properties.gestoreFiltroDuplicatiRepositoryBuste = CostantiConfigurazione.FILTRO_DUPLICATI_OPENSPCOOP;
				}else{
					name = name.trim();
					OpenSPCoop2Properties.gestoreFiltroDuplicatiRepositoryBuste = name;
				}
			
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.filtroDuplicati' non impostata, viene utilizzato il default="+CostantiConfigurazione.FILTRO_DUPLICATI_OPENSPCOOP+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.gestoreFiltroDuplicatiRepositoryBuste = CostantiConfigurazione.FILTRO_DUPLICATI_OPENSPCOOP;
			}    
		}

		return OpenSPCoop2Properties.gestoreFiltroDuplicatiRepositoryBuste;
	}

	/**
	 * Restituisce la Generazione attributi 'tipo' e 'servizioCorrelato' nell'elemento 'ProfiloCollaborazione' della richiesta asincrona simmetrica 
	 * e della ricevuta alla richiesta asincrona asimmetrica
	 *   
	 * @return Restituisce la Generazione attributi 'tipo' e 'servizioCorrelato' nell'elemento 'ProfiloCollaborazione' della richiesta asincrona simmetrica 
	 *         e della ricevuta alla richiesta asincrona asimmetrica
	 * 
	 */
	private static Boolean isGenerazioneAttributiAsincroni = null;
	public boolean isGenerazioneAttributiAsincroni(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getBusta_AsincroniAttributiCorrelatiEnable(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else 
					return false;
			}
		}

		if(OpenSPCoop2Properties.isGenerazioneAttributiAsincroni==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.asincroni.attributiCorrelati.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isGenerazioneAttributiAsincroni = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.asincroni.attributiCorrelati.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGenerazioneAttributiAsincroni = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.asincroni.attributiCorrelati.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneAttributiAsincroni = true;
			}
		}

		return OpenSPCoop2Properties.isGenerazioneAttributiAsincroni;
	}

	

	/**
	 * Indicazione se ritenere una busta malformata se la validazione ha rilevato eccezioni di livello non gravi
	 *   
	 * @return Indicazione se ritenere una busta malformata se la validazione ha rilevato eccezioni di livello non gravi
	 * 
	 */
	private static Boolean ignoraEccezioniNonGravi_Validazione = null;
	public boolean ignoraEccezioniNonGravi_Validazione(){
		if(OpenSPCoop2Properties.ignoraEccezioniNonGravi_Validazione==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.validazione.ignoraEccezioniNonGravi"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.ignoraEccezioniNonGravi_Validazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.validazione.ignoraEccezioniNonGravi' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.ignoraEccezioniNonGravi_Validazione = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.validazione.ignoraEccezioniNonGravi' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.ignoraEccezioniNonGravi_Validazione = false;
			}
		}

		return OpenSPCoop2Properties.ignoraEccezioniNonGravi_Validazione;
	}
	
	/**
	 * Indicazione se forzare la generazione del prefix 'soap' per i fault spcoop (compatibilita' con OpenSPCoop v1).
	 *   
	 * @return Indicazione se forzare la generazione del prefix 'soap' per i fault spcoop (compatibilita' con OpenSPCoop v1).
	 * 
	 */
	private static Boolean forceSoapPrefixCompatibilitaOpenSPCoopV1 = null;
	public boolean isForceSoapPrefixCompatibilitaOpenSPCoopV1(){
		if(OpenSPCoop2Properties.forceSoapPrefixCompatibilitaOpenSPCoopV1==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.backwardCompatibility.forceSoapPrefix"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.forceSoapPrefixCompatibilitaOpenSPCoopV1 = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.backwardCompatibility.forceSoapPrefix' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.forceSoapPrefixCompatibilitaOpenSPCoopV1 = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.backwardCompatibility.forceSoapPrefix' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.forceSoapPrefixCompatibilitaOpenSPCoopV1 = true;
			}
		}

		return OpenSPCoop2Properties.forceSoapPrefixCompatibilitaOpenSPCoopV1;
	}
	

	/**
	 * Indicazione se generare la lista Trasmissione
	 *   
	 * @return Indicazione se generare la lista Trasmissione
	 * 
	 */
	private static Boolean isGenerazioneListaTrasmissioni = null;
	public boolean isGenerazioneListaTrasmissioni(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){			
			String tipo = this.pddReader.getBusta_TrasmissioneEnable(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else if(CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo))
					return false;
			}
		}

		if(OpenSPCoop2Properties.isGenerazioneListaTrasmissioni==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasmissione.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isGenerazioneListaTrasmissioni = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasmissione.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGenerazioneListaTrasmissioni = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasmissione.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneListaTrasmissioni = true;
			}
		}

		return OpenSPCoop2Properties.isGenerazioneListaTrasmissioni;
	}

	/**
	 * Indicazione se generare un msg di Protocollo errore in caso di filtro duplicati anche per il profilo oneway
	 *   
	 * @return Indicazione se generare un msg di Protocollo errore in caso di filtro duplicati  anche per il profilo oneway
	 * 
	 */
	private static Boolean isGenerazioneErroreFiltroDuplicati = null;
	public boolean isGenerazioneErroreProtocolloFiltroDuplicati(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getBusta_FiltroduplicatiGenerazioneBustaErrore(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else if(CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo))
					return false;
			}
		}

		if(OpenSPCoop2Properties.isGenerazioneErroreFiltroDuplicati==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.filtroduplicati.generazioneErrore"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isGenerazioneErroreFiltroDuplicati = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.filtroduplicati.generazioneErrore' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isGenerazioneErroreFiltroDuplicati = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.filtroduplicati.generazioneErrore' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneErroreFiltroDuplicati = false;
			}
		}

		return OpenSPCoop2Properties.isGenerazioneErroreFiltroDuplicati;
	}

	/**
	 * Indicazione se leggere dal registro se abilitato il filtro duplicati
	 *   
	 * @return Indicazione se leggere dal registro se abilitato il filtro duplicati
	 * 
	 */
	private static Boolean isCheckFromRegistroFiltroDuplicatiAbilitato = null;
	public boolean isCheckFromRegistroFiltroDuplicatiAbilitato(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getValidazione_FiltroDuplicatiLetturaRegistro(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else if(CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo))
					return false;
			}
		}

		if(OpenSPCoop2Properties.isCheckFromRegistroFiltroDuplicatiAbilitato==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.filtroDuplicati.letturaRegistro"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isCheckFromRegistroFiltroDuplicatiAbilitato = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.filtroDuplicati.letturaRegistro' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isCheckFromRegistroFiltroDuplicatiAbilitato = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.filtroDuplicati.letturaRegistro' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isCheckFromRegistroFiltroDuplicatiAbilitato = true;
			}
		}

		return OpenSPCoop2Properties.isCheckFromRegistroFiltroDuplicatiAbilitato;
	}

	/**
	 * Indicazione se leggere dal registro se abilitato la gestione dei riscontri
	 *   
	 * @return Indicazione se leggere dal registro se abilitato la gestione dei riscontri
	 * 
	 */
	private static Boolean isCheckFromRegistroConfermaRicezioneAbilitato = null;
	public boolean isCheckFromRegistroConfermaRicezioneAbilitato(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getValidazione_ConfermaRicezioneLetturaRegistro(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else if(CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo))
					return false;
			}
		}

		if(OpenSPCoop2Properties.isCheckFromRegistroConfermaRicezioneAbilitato==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.confermaRicezione.letturaRegistro"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isCheckFromRegistroConfermaRicezioneAbilitato = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.confermaRicezione.letturaRegistro' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isCheckFromRegistroConfermaRicezioneAbilitato = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.confermaRicezione.letturaRegistro' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isCheckFromRegistroConfermaRicezioneAbilitato = true;
			}
		}

		return OpenSPCoop2Properties.isCheckFromRegistroConfermaRicezioneAbilitato;
	}

	/**
	 * Indicazione se leggere dal registro se abilitato la consegna in ordine
	 *   
	 * @return Indicazione se leggere dal registro se abilitato la gestione dei riscontri
	 * 
	 */
	private static Boolean isCheckFromRegistroConsegnaInOrdineAbilitato = null;
	public boolean isCheckFromRegistroConsegnaInOrdineAbilitato(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getValidazione_ConsegnaInOrdineLetturaRegistro(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else if(CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo))
					return false;
			}
		}

		if(OpenSPCoop2Properties.isCheckFromRegistroConsegnaInOrdineAbilitato==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.consegnaInOrdine.letturaRegistro"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isCheckFromRegistroConsegnaInOrdineAbilitato = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.consegnaInOrdine.letturaRegistro' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isCheckFromRegistroConsegnaInOrdineAbilitato = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.consegnaInOrdine.letturaRegistro' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isCheckFromRegistroConsegnaInOrdineAbilitato = true;
			}
		}

		return OpenSPCoop2Properties.isCheckFromRegistroConsegnaInOrdineAbilitato;
	}

	/**
	 * Indicazione se l'elemento collaborazione deve essere gestito
	 *   
	 * @return Indicazione se l'elemento collaborazione deve essere gestito
	 * 
	 */
	private static Boolean isGestioneElementoCollaborazione = null;
	public boolean isGestioneElementoCollaborazione(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getBusta_CollaborazioneEnable(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else 
					return false;
			}
		}

		if(OpenSPCoop2Properties.isGestioneElementoCollaborazione==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.collaborazione.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isGestioneElementoCollaborazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.collaborazione.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGestioneElementoCollaborazione = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.collaborazione.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestioneElementoCollaborazione = true;
			}
		}

		return OpenSPCoop2Properties.isGestioneElementoCollaborazione;
	}
	
	private static Boolean isGestioneElementoIdRiferimentoRichiesta = null;
	public boolean isGestioneElementoIdRiferimentoRichiesta(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getBusta_IdRiferimentoRichiestaEnable(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else 
					return false;
			}
		}

		if(OpenSPCoop2Properties.isGestioneElementoIdRiferimentoRichiesta==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.idRiferimentoRichiesta.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isGestioneElementoIdRiferimentoRichiesta = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.idRiferimentoRichiesta.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGestioneElementoIdRiferimentoRichiesta = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.idRiferimentoRichiesta.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestioneElementoIdRiferimentoRichiesta = true;
			}
		}

		return OpenSPCoop2Properties.isGestioneElementoIdRiferimentoRichiesta;
	}

	/**
	 * Indicazione se la funzionalita' di consegna in ordine deve essere gestita
	 *   
	 * @return Indicazione se la funzionalita' di consegna in ordine deve essere gestita
	 * 
	 */
	private static Boolean isGestioneConsegnaInOrdine = null;
	public boolean isGestioneConsegnaInOrdine(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getBusta_ConsegnaInOrdineEnable(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else 
					return false;
			}
		}

		if(OpenSPCoop2Properties.isGestioneConsegnaInOrdine==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.consegnaInOrdine.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isGestioneConsegnaInOrdine = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.consegnaInOrdine.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGestioneConsegnaInOrdine = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.consegnaInOrdine.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestioneConsegnaInOrdine = true;
			}
		}

		return OpenSPCoop2Properties.isGestioneConsegnaInOrdine;
	}

	/**
	 * Indicazione se la funzionalita' dei riscontri deve essere gestita
	 *   
	 * @return Indicazione se la funzionalita' dei riscontri deve essere gestita
	 * 
	 */
	private static Boolean isGestioneRiscontri = null;
	public boolean isGestioneRiscontri(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getBusta_RiscontriEnable(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else if(CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo))
					return false;
			}
		}

		if(OpenSPCoop2Properties.isGestioneRiscontri==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.riscontri.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isGestioneRiscontri = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.riscontri.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGestioneRiscontri = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.riscontri.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestioneRiscontri = true;
			}
		}

		return OpenSPCoop2Properties.isGestioneRiscontri;
	}
    
   

	
	/**
	 * Validazione buste che possiedono attributi qualificati (default:false)
	 * Lo schema della busta non permette attributi qualificati (attributeFormDefault="unqualified")
	 * Se non si abilita la validazione 'rigida' (tramite schema xsd) e si abilita la seguente opzione, e' possibile far accettare/processare
	 * alla porta di dominio anche buste che contengono attributi qualificati.
	 *   
	 * @return Indicazione se ritornare solo SoapFault o busteSPCoop in caso di buste con struttura malformata.
	 * 
	 */
	private static Boolean isReadQualifiedAttribute = null;
	public boolean isReadQualifiedAttribute(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getValidazione_readQualifiedAttribute(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else if(CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo))
					return false;
			}
		}

		if(OpenSPCoop2Properties.isReadQualifiedAttribute==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.validazione.readQualifiedAttribute"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isReadQualifiedAttribute = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.validazione.readQualifiedAttribute' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isReadQualifiedAttribute = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.validazione.readQualifiedAttribute' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isReadQualifiedAttribute = false;
			}
		}

		return OpenSPCoop2Properties.isReadQualifiedAttribute;
	}

	
	/**
	 * Validazione buste che possiedono attributi qualificati (default:false)
	 * Lo schema della busta non permette attributi qualificati (attributeFormDefault="unqualified")
	 * Se non si abilita la validazione 'rigida' (tramite schema xsd) e si abilita la seguente opzione, e' possibile far accettare/processare
	 * alla porta di dominio anche buste che contengono attributi qualificati.
	 *   
	 * @return Indicazione se ritornare solo SoapFault o busteSPCoop in caso di buste con struttura malformata.
	 * 
	 */
	private static Boolean isValidazioneIDBustaCompleta = null;
	public boolean isValidazioneIDBustaCompleta(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getValidazione_ValidazioneIDBustaCompleta(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else if(CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo))
					return false;
			}
		}

		if(OpenSPCoop2Properties.isValidazioneIDBustaCompleta==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.validazione.idbusta.validazioneCompleta"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneIDBustaCompleta = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.validazione.idbusta.validazioneCompleta' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneIDBustaCompleta = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.validazione.idbusta.validazioneCompleta' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneIDBustaCompleta = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneIDBustaCompleta;
	}
	
	public ProprietaManifestAttachments getProprietaManifestAttachments(String implementazionePdDSoggetto){
		ProprietaManifestAttachments properties = new ProprietaManifestAttachments();
		properties.setReadQualifiedAttribute(this.isReadQualifiedAttribute(implementazionePdDSoggetto));
		return properties;
	}


	/**
	 * Restituisce l'eventuale location del file govway.pdd.properties
	 * 
	 * @return Restituisce la location del file govway.pdd.properties
	 */
	private static String filePddProperties = null;
	private static boolean filePddPropertiesLetto = false;
	public String getLocationPddProperties() {
		if(OpenSPCoop2Properties.filePddPropertiesLetto==false){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.pddProperties");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.filePddProperties = name;
				}
				OpenSPCoop2Properties.filePddPropertiesLetto= true;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.pddProperties': "+e.getMessage(),e);
				OpenSPCoop2Properties.filePddProperties = null;
			}    			
		}

		return OpenSPCoop2Properties.filePddProperties;
	}






	/* ********  INTEGRAZIONE  ******** */

	/**
	 * Restituisce l'elenco dei tipi di integrazione da gestire lato PortaDelegata
	 * 
	 * @return  Restituisce l'elenco dei tipi di integrazione da gestire lato PortaDelegata
	 */
	private static String[] tipoIntegrazionePD = null;
	private static boolean tipoIntegrazionePDRead = false;
	public String[] getTipoIntegrazionePD() {
		if(OpenSPCoop2Properties.tipoIntegrazionePDRead == false){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.tipo.pd"); 
				if(value==null)
					throw new Exception("non definita");
				value = value.trim();
				String [] r = value.split(",");
				OpenSPCoop2Properties.tipoIntegrazionePD = r;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.tipo.pd': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoIntegrazionePD = null;
			}
			OpenSPCoop2Properties.tipoIntegrazionePDRead = true;
		}

		return OpenSPCoop2Properties.tipoIntegrazionePD;
	}
	/**
	 * Restituisce l'elenco dei tipi di integrazione da gestire lato PortaApplicativa
	 * 
	 * @return  Restituisce l'elenco dei tipi di integrazione da gestire lato PortaApplicativa
	 */
	private static String[] tipoIntegrazionePA = null;
	private static boolean tipoIntegrazionePARead = false;
	public String[] getTipoIntegrazionePA() {
		if(OpenSPCoop2Properties.tipoIntegrazionePARead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.tipo.pa");
				if(value==null)
					throw new Exception("non definita");
				value = value.trim();
				String [] r = value.split(",");
				OpenSPCoop2Properties.tipoIntegrazionePA = r;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di integrazione tra porta di dominio e servizio applicativo 'org.openspcoop2.pdd.integrazione.tipo.pa': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoIntegrazionePA = null;
			}   
			OpenSPCoop2Properties.tipoIntegrazionePARead = true;
		}

		return OpenSPCoop2Properties.tipoIntegrazionePA;
	}
	
	/**
	 * Restituisce l'elenco dei tipi di integrazione da gestire lato PortaDelegata specifici per protocollo
	 * 
	 * @return  Restituisce l'elenco dei tipi di integrazione da gestire lato PortaDelegata specifici per protocollo
	 */
	private static ConcurrentHashMap<String, String[]> tipoIntegrazionePD_perProtocollo = new ConcurrentHashMap<String, String[]>();
	private static ConcurrentHashMap<String, Boolean> tipoIntegrazionePD_perProtocollo_notExists = new ConcurrentHashMap<String, Boolean>();
	public String[] getTipoIntegrazionePD(String protocollo) {
		
		if( 
			(OpenSPCoop2Properties.tipoIntegrazionePD_perProtocollo.containsKey(protocollo)==false) 
			&&
			(OpenSPCoop2Properties.tipoIntegrazionePD_perProtocollo_notExists.containsKey(protocollo)==false) 
		){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.tipo.pd."+protocollo); 
				if(value==null){
					OpenSPCoop2Properties.tipoIntegrazionePD_perProtocollo_notExists.put(protocollo, false);
				}
				else{
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipoIntegrazionePD_perProtocollo.put(protocollo, r);
				}

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.tipo.pd."+protocollo+"': "+e.getMessage(),e);
			}
		}

		if(OpenSPCoop2Properties.tipoIntegrazionePD_perProtocollo.containsKey(protocollo)){
			return OpenSPCoop2Properties.tipoIntegrazionePD_perProtocollo.get(protocollo);
		}else{
			return null;
		}
	}
	
	/**
	 * Restituisce l'elenco dei tipi di integrazione da gestire lato PortaApplicativa specifici per protocollo
	 * 
	 * @return  Restituisce l'elenco dei tipi di integrazione da gestire lato PortaApplicativa specifici per protocollo
	 */
	private static ConcurrentHashMap<String, String[]> tipoIntegrazionePA_perProtocollo = new ConcurrentHashMap<String, String[]>();
	private static ConcurrentHashMap<String, Boolean> tipoIntegrazionePA_perProtocollo_notExists = new ConcurrentHashMap<String, Boolean>();
	public String[] getTipoIntegrazionePA(String protocollo) {
		
		if( 
			(OpenSPCoop2Properties.tipoIntegrazionePA_perProtocollo.containsKey(protocollo)==false) 
			&&
			(OpenSPCoop2Properties.tipoIntegrazionePA_perProtocollo_notExists.containsKey(protocollo)==false) 
		){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.tipo.pa."+protocollo); 
				if(value==null){
					OpenSPCoop2Properties.tipoIntegrazionePA_perProtocollo_notExists.put(protocollo, false);
				}
				else{
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipoIntegrazionePA_perProtocollo.put(protocollo, r);
				}

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.tipo.pa."+protocollo+"': "+e.getMessage(),e);
			}
		}

		if(OpenSPCoop2Properties.tipoIntegrazionePA_perProtocollo.containsKey(protocollo)){
			return OpenSPCoop2Properties.tipoIntegrazionePA_perProtocollo.get(protocollo);
		}else{
			return null;
		}
	}
	

	/**
	 * Compatibilita integrazione asincroni con versioni precedenti a 1.4
	 * L'indicazione dell'id per la correlazione asincrona poteva essere fornita, 
	 * oltre che tramite il riferimentoMessaggio, anche tramite l'elemento idCollaborazione.
	 * Tale funzionalita' e' disabilitata per default dalla versione 1.4
	 * Se si desidera riabilitarla e' possibile agire sulla proprieta': org.openspcoop2.pdd.integrazione.asincroni.idCollaborazione.enabled
	 * Questo metodo restituisce l'indicazione se tale proprieta' e' abilitata.
	 *  
	 * @return Restituisce l'indicazione se la proprieta' org.openspcoop2.pdd.integrazione.asincroni.idCollaborazione.enabled e' abilitata.
	 * 
	 */
	private static Boolean isIntegrazioneAsincroniConIdCollaborazioneEnabled = null;
	public boolean isIntegrazioneAsincroniConIdCollaborazioneEnabled(){
		if(OpenSPCoop2Properties.isIntegrazioneAsincroniConIdCollaborazioneEnabled==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.asincroni.idCollaborazione.enabled"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isIntegrazioneAsincroniConIdCollaborazioneEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.asincroni.idCollaborazione.enabled' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isIntegrazioneAsincroniConIdCollaborazioneEnabled = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.asincroni.idCollaborazione.enabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isIntegrazioneAsincroniConIdCollaborazioneEnabled = false;
			}
		}

		return OpenSPCoop2Properties.isIntegrazioneAsincroniConIdCollaborazioneEnabled;
	}
	
	/**
	 * Restituisce le proprieta' che identificano gli header di integrazione in caso di 'trasporto' 
	 *
	 * @return Restituisce le proprieta' che identificano gli header di integrazione in caso di 'trasporto'
	 *  
	 */
	private static java.util.concurrent.ConcurrentHashMap<String, String> keyValue_HeaderIntegrazioneTrasporto = null;
	public java.util.concurrent.ConcurrentHashMap<String, String> getKeyValue_HeaderIntegrazioneTrasporto() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto==null){

			try{ 

				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto = this.reader.readPropertiesAsConcurrentHashMap_convertEnvProperties("org.openspcoop2.pdd.integrazione.trasporto.keyword.");
			
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.trasporto.keyword.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, String> keyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop2 = null;
	public java.util.concurrent.ConcurrentHashMap<String, String> getKeyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop2() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop2==null){

			try{ 

				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop2 = this.reader.readPropertiesAsConcurrentHashMap_convertEnvProperties("org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop2.trasporto.keyword.");
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop2.trasporto.keyword.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop2 = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop2;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, String> keyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop1 = null;
	public java.util.concurrent.ConcurrentHashMap<String, String> getKeyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop1() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop1==null){

			try{ 

				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop1 = this.reader.readPropertiesAsConcurrentHashMap_convertEnvProperties("org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop1.trasporto.keyword.");
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop1.trasporto.keyword.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop1 = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_backwardCompatibility_openspcoop1;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneTrasporto_setPD_request = null;
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneTrasporto_setPD_response = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPDSetEnabled_HeaderIntegrazioneTrasporto(boolean request) throws Exception {	
				
		if( (request && OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_setPD_request==null)
			||
			(!request && OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_setPD_response==null)
			){

			String pName = "org.openspcoop2.pdd.integrazione.trasporto.pd.set.request.enabled.";
			if(!request) {
				pName = "org.openspcoop2.pdd.integrazione.trasporto.pd.set.response.enabled.";
			}
			
			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties(pName);
				if(request) {
					keyValue_HeaderIntegrazioneTrasporto_setPD_request = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				}
				else {
					keyValue_HeaderIntegrazioneTrasporto_setPD_response = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				}
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							if(request) {
								keyValue_HeaderIntegrazioneTrasporto_setPD_request.put(key, b);
							}
							else {
								keyValue_HeaderIntegrazioneTrasporto_setPD_response.put(key, b);
							}
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property '"+pName+"."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' '"+pName+".*': "+e.getMessage(),e);
				throw e;
			}    
		}

		if(request) {
			return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_setPD_request;
		}
		else {
			return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_setPD_response;
		}
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneTrasporto_readPD = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPDReadEnabled_HeaderIntegrazioneTrasporto() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_readPD==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.trasporto.pd.read.enabled.");
				keyValue_HeaderIntegrazioneTrasporto_readPD = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_HeaderIntegrazioneTrasporto_readPD.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.integrazione.trasporto.pd.read.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.trasporto.pd.read.enabled.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_readPD;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneTrasporto_setPA_request = null;
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneTrasporto_setPA_response = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPASetEnabled_HeaderIntegrazioneTrasporto(boolean request) throws Exception {
				
		if( (request && OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_setPA_request==null)
				||
				(!request && OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_setPA_response==null)
				){

			String pName = "org.openspcoop2.pdd.integrazione.trasporto.pa.set.request.enabled.";
			if(!request) {
				pName = "org.openspcoop2.pdd.integrazione.trasporto.pa.set.response.enabled.";
			}
			
			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties(pName);
				if(request) {
					keyValue_HeaderIntegrazioneTrasporto_setPA_request = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				}
				else {
					keyValue_HeaderIntegrazioneTrasporto_setPA_response = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				}
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							if(request) {
								keyValue_HeaderIntegrazioneTrasporto_setPA_request.put(key, b);
							}
							else {
								keyValue_HeaderIntegrazioneTrasporto_setPA_response.put(key, b);
							}
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property '"+pName+"."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' '"+pName+".*': "+e.getMessage(),e);
				throw e;
			}    
		}

		if(request) {
			return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_setPA_request;
		}
		else {
			return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_setPA_response;
		}
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneTrasporto_readPA = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPAReadEnabled_HeaderIntegrazioneTrasporto() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_readPA==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.trasporto.pa.read.enabled.");
				keyValue_HeaderIntegrazioneTrasporto_readPA = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_HeaderIntegrazioneTrasporto_readPA.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.integrazione.trasporto.pa.read.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.trasporto.pa.read.enabled.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto_readPA;
	}
	
	/**
	 * Restituisce le proprieta' che identificano gli header di integrazione in caso di 'urlBased'.
	 *
	 * @return Restituisce le proprieta' che identificano gli header di integrazione in caso di 'urlBased'.
	 *  
	 */
	private static java.util.concurrent.ConcurrentHashMap<String, String> keyValue_HeaderIntegrazioneUrlBased = null;
	public java.util.concurrent.ConcurrentHashMap<String, String> getKeyValue_HeaderIntegrazioneUrlBased() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased==null){

			try{ 

				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased = this.reader.readPropertiesAsConcurrentHashMap_convertEnvProperties("org.openspcoop2.pdd.integrazione.urlBased.keyword.");
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.urlBased.keyword.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, String> keyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop2 = null;
	public java.util.concurrent.ConcurrentHashMap<String, String> getKeyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop2() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop2==null){

			try{ 

				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop2 = this.reader.readPropertiesAsConcurrentHashMap_convertEnvProperties("org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop2.urlBased.keyword.");
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop2.urlBased.keyword.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop2 = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop2;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, String> keyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop1 = null;
	public java.util.concurrent.ConcurrentHashMap<String, String> getKeyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop1() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop1==null){

			try{ 

				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop1 = this.reader.readPropertiesAsConcurrentHashMap_convertEnvProperties("org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop1.urlBased.keyword.");
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop1.urlBased.keyword.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop1 = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_backwardCompatibility_openspcoop1;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneUrlBased_setPD = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPDSetEnabled_HeaderIntegrazioneUrlBased() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_setPD==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.urlBased.pd.set.request.enabled.");
				keyValue_HeaderIntegrazioneUrlBased_setPD = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_HeaderIntegrazioneUrlBased_setPD.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.integrazione.urlBased.pd.set.request.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.urlBased.pd.set.request.enabled*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_setPD;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneUrlBased_readPD = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPDReadEnabled_HeaderIntegrazioneUrlBased() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_readPD==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.urlBased.pd.read.enabled.");
				keyValue_HeaderIntegrazioneUrlBased_readPD = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_HeaderIntegrazioneUrlBased_readPD.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.integrazione.urlBased.pd.read.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.urlBased.pd.read.enabled.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_readPD;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneUrlBased_setPA = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPASetEnabled_HeaderIntegrazioneUrlBased() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_setPA==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.urlBased.pa.set.request.enabled.");
				keyValue_HeaderIntegrazioneUrlBased_setPA = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_HeaderIntegrazioneUrlBased_setPA.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.integrazione.urlBased.pa.set.request.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.urlBased.pa.set.request.enabled*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_setPA;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneUrlBased_readPA = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPAReadEnabled_HeaderIntegrazioneUrlBased() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_readPA==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.urlBased.pa.read.enabled.");
				keyValue_HeaderIntegrazioneUrlBased_readPA = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_HeaderIntegrazioneUrlBased_readPA.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.integrazione.urlBased.pa.read.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.urlBased.pa.read.enabled.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased_readPA;
	}
	
	/**
	 * Restituisce le proprieta' che identificano gli header di integrazione in caso di 'soap'.
	 *
	 * @return Restituisce le proprieta' che identificano gli header di integrazione in caso di 'soap'.
	 *  
	 */
	private static java.util.concurrent.ConcurrentHashMap<String, String> keyValue_HeaderIntegrazioneSoap = null;
	public java.util.concurrent.ConcurrentHashMap<String, String> getKeyValue_HeaderIntegrazioneSoap() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap==null){

			try{ 

				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap = this.reader.readPropertiesAsConcurrentHashMap_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.keyword.");
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.soap.keyword.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, String> keyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop2 = null;
	public java.util.concurrent.ConcurrentHashMap<String, String> getKeyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop2() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop2==null){

			try{ 

				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop2 = this.reader.readPropertiesAsConcurrentHashMap_convertEnvProperties("org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop2.soap.keyword.");
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop2.soap.keyword.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop2 = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop2;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, String> keyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop1 = null;
	public java.util.concurrent.ConcurrentHashMap<String, String> getKeyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop1() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop1==null){

			try{ 

				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop1 = this.reader.readPropertiesAsConcurrentHashMap_convertEnvProperties("org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop1.soap.keyword.");
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.backward_compatibility.openspcoop1.soap.keyword.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop1 = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_backwardCompatibility_openspcoop1;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneSoap_setPD_request = null;
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneSoap_setPD_response = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPDSetEnabled_HeaderIntegrazioneSoap(boolean request) throws Exception {
		
		if( (request && OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_setPD_request==null)
				||
				(!request && OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_setPD_response==null)
				){

			String pName = "org.openspcoop2.pdd.integrazione.soap.pd.set.request.enabled.";
			if(!request) {
				pName = "org.openspcoop2.pdd.integrazione.soap.pd.set.response.enabled.";
			}

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties(pName);
				if(request) {
					keyValue_HeaderIntegrazioneSoap_setPD_request = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				}
				else {
					keyValue_HeaderIntegrazioneSoap_setPD_response = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				}
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							if(request) {
								keyValue_HeaderIntegrazioneSoap_setPD_request.put(key, b);
							}
							else {
								keyValue_HeaderIntegrazioneSoap_setPD_response.put(key, b);
							}
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property '"+pName+"."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' '"+pName+".*': "+e.getMessage(),e);
				throw e;
			}    
		}

		if(request) {
			return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_setPD_request;
		}
		else {
			return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_setPD_response;
		}
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneSoap_readPD = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPDReadEnabled_HeaderIntegrazioneSoap() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_readPD==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.pd.read.enabled.");
				keyValue_HeaderIntegrazioneSoap_readPD = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_HeaderIntegrazioneSoap_readPD.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.integrazione.soap.pd.read.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.soap.pd.read.enabled.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_readPD;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneSoap_setPA_request = null;
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneSoap_setPA_response = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPASetEnabled_HeaderIntegrazioneSoap(boolean request) throws Exception {	
		if( (request && OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_setPA_request==null)
				||
				(!request && OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_setPA_response==null)
				){

			String pName = "org.openspcoop2.pdd.integrazione.soap.pa.set.request.enabled.";
			if(!request) {
				pName = "org.openspcoop2.pdd.integrazione.soap.pa.set.response.enabled.";
			}

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties(pName);
				if(request) {
					keyValue_HeaderIntegrazioneSoap_setPA_request = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				}
				else {
					keyValue_HeaderIntegrazioneSoap_setPA_response = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				}
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							if(request) {
								keyValue_HeaderIntegrazioneSoap_setPA_request.put(key, b);
							}
							else {
								keyValue_HeaderIntegrazioneSoap_setPA_response.put(key, b);
							}
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property '"+pName+"."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' '"+pName+".*': "+e.getMessage(),e);
				throw e;
			}    
		}

		if(request) {
			return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_setPA_request;
		}
		else {
			return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_setPA_response;
		}
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_HeaderIntegrazioneSoap_readPA = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPAReadEnabled_HeaderIntegrazioneSoap() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_readPA==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.pa.read.enabled.");
				keyValue_HeaderIntegrazioneSoap_readPA = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_HeaderIntegrazioneSoap_readPA.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.integrazione.soap.pa.read.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.soap.pa.read.enabled.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap_readPA;
	}
	
	private static String headerIntegrazioneSOAPPdDVersione = null;
	public String getHeaderIntegrazioneSOAPPdDVersione(){
		if(OpenSPCoop2Properties.headerIntegrazioneSOAPPdDVersione==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.pddVersion"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.headerIntegrazioneSOAPPdDVersione = value;
				}else{
					//NON EMETTO WARNING: this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.pddVersion' non impostata, viene utilizzato il default="+this.getVersione());
					OpenSPCoop2Properties.headerIntegrazioneSOAPPdDVersione = this.getVersione();
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.pddVersion' non impostata, viene utilizzato il default=true, errore:"+this.getVersione());
				OpenSPCoop2Properties.headerIntegrazioneSOAPPdDVersione = this.getVersione();
			}
		}

		return OpenSPCoop2Properties.headerIntegrazioneSOAPPdDVersione;
	}
	
	private static Boolean readHeaderIntegrazioneSOAPPdDDetails = null;
	private static String headerIntegrazioneSOAPPdDDetails = null;
	public String getHeaderIntegrazioneSOAPPdDDetails(){
		if(OpenSPCoop2Properties.readHeaderIntegrazioneSOAPPdDDetails==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.pddDetails"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.headerIntegrazioneSOAPPdDDetails = value;
				}else{
					OpenSPCoop2Properties.headerIntegrazioneSOAPPdDDetails = this.getDetails();
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.pddDetails' non impostata correttamente: "+e.getMessage(),e);
				OpenSPCoop2Properties.headerIntegrazioneSOAPPdDDetails = this.getDetails();
			}
		}

		OpenSPCoop2Properties.readHeaderIntegrazioneSOAPPdDDetails = true;
		return OpenSPCoop2Properties.headerIntegrazioneSOAPPdDDetails;
	}
	
	/**
	 * Restituisce l'indicazione l'header di integrazione letto
	 * durante l'integrazione tra servizio applicativo e PdD
	 * deve essere eliminato o meno
	 *  
	 * @return Restituisce l'indicazione sull'eventuale eliminazione dell'header di integrazione
	 * 
	 */
	private static Boolean deleteHeaderIntegrazioneRequestPD = null;
	public boolean deleteHeaderIntegrazioneRequestPD(){
		if(OpenSPCoop2Properties.deleteHeaderIntegrazioneRequestPD==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.pd.request.readAndDelete"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.deleteHeaderIntegrazioneRequestPD = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pd.request.readAndDelete' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.deleteHeaderIntegrazioneRequestPD = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pd.request.readAndDelete' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.deleteHeaderIntegrazioneRequestPD = true;
			}
		}

		return OpenSPCoop2Properties.deleteHeaderIntegrazioneRequestPD;
	}
	
	/**
	 * Restituisce l'indicazione l'header di integrazione letto
	 * durante l'integrazione tra servizio applicativo e PdD
	 * deve essere eliminato o meno
	 *  
	 * @return Restituisce l'indicazione sull'eventuale eliminazione dell'header di integrazione
	 * 
	 */
	private static Boolean deleteHeaderIntegrazioneResponsePD = null;
	public boolean deleteHeaderIntegrazioneResponsePD(){
		if(OpenSPCoop2Properties.deleteHeaderIntegrazioneResponsePD==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.pd.response.readAndDelete"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.deleteHeaderIntegrazioneResponsePD = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pd.response.readAndDelete' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.deleteHeaderIntegrazioneResponsePD = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pd.response.readAndDelete' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.deleteHeaderIntegrazioneResponsePD = true;
			}
		}

		return OpenSPCoop2Properties.deleteHeaderIntegrazioneResponsePD;
	}

	/**
	 * Restituisce l'indicazione l'header di integrazione letto
	 * durante l'integrazione tra servizio applicativo e PdD
	 * deve essere eliminato o meno
	 *  
	 * @return Restituisce l'indicazione sull'eventuale eliminazione dell'header di integrazione
	 * 
	 */
	private static Boolean processHeaderIntegrazionePDResponse = null;
	public boolean processHeaderIntegrazionePDResponse(boolean functionAsRouter){
		if(functionAsRouter){
			return false;
		}
		if(OpenSPCoop2Properties.processHeaderIntegrazionePDResponse==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.pd.response.process"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.processHeaderIntegrazionePDResponse = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pd.response.process' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.processHeaderIntegrazionePDResponse = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pd.response.process' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.processHeaderIntegrazionePDResponse = false;
			}
		}

		return OpenSPCoop2Properties.processHeaderIntegrazionePDResponse;
	}
	
	
	
	/**
	 * Restituisce l'indicazione l'header di integrazione letto
	 * durante l'integrazione tra servizio applicativo e PdD
	 * deve essere eliminato o meno
	 *  
	 * @return Restituisce l'indicazione sull'eventuale eliminazione dell'header di integrazione
	 * 
	 */
	private static Boolean deleteHeaderIntegrazioneRequestPA = null;
	public boolean deleteHeaderIntegrazioneRequestPA(){
		if(OpenSPCoop2Properties.deleteHeaderIntegrazioneRequestPA==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.pa.request.readAndDelete"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.deleteHeaderIntegrazioneRequestPA = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pa.request.readAndDelete' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.deleteHeaderIntegrazioneRequestPA = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pa.request.readAndDelete' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.deleteHeaderIntegrazioneRequestPA = true;
			}
		}

		return OpenSPCoop2Properties.deleteHeaderIntegrazioneRequestPA;
	}
	
	/**
	 * Restituisce l'indicazione l'header di integrazione letto
	 * durante l'integrazione tra servizio applicativo e PdD
	 * deve essere eliminato o meno
	 *  
	 * @return Restituisce l'indicazione sull'eventuale eliminazione dell'header di integrazione
	 * 
	 */
	private static Boolean deleteHeaderIntegrazioneResponsePA = null;
	public boolean deleteHeaderIntegrazioneResponsePA(){
		if(OpenSPCoop2Properties.deleteHeaderIntegrazioneResponsePA==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.pa.response.readAndDelete"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.deleteHeaderIntegrazioneResponsePA = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pa.response.readAndDelete' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.deleteHeaderIntegrazioneResponsePA = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pa.response.readAndDelete' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.deleteHeaderIntegrazioneResponsePA = true;
			}
		}

		return OpenSPCoop2Properties.deleteHeaderIntegrazioneResponsePA;
	}

	/**
	 * Restituisce l'indicazione l'header di integrazione letto
	 * durante l'integrazione tra servizio applicativo e PdD
	 * deve essere eliminato o meno
	 *  
	 * @return Restituisce l'indicazione sull'eventuale eliminazione dell'header di integrazione
	 * 
	 */
	private static Boolean processHeaderIntegrazionePARequest = null;
	public boolean processHeaderIntegrazionePARequest(boolean functionAsRouter){
		if(functionAsRouter){
			return false;
		}
		if(OpenSPCoop2Properties.processHeaderIntegrazionePARequest==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.pa.request.process"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.processHeaderIntegrazionePARequest = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pa.request.process' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.processHeaderIntegrazionePARequest = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pa.request.process' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.processHeaderIntegrazionePARequest = false;
			}
		}

		return OpenSPCoop2Properties.processHeaderIntegrazionePARequest;
	}
	

	/**
	 * Restituisce il nome dell'header Soap di integrazione di default
	 * 
	 * @return Restituisce il nome dell'header Soap di integrazione di default
	 */
	private static String headerSoapNameIntegrazione = null;
	public String getHeaderSoapNameIntegrazione() {
		if(OpenSPCoop2Properties.headerSoapNameIntegrazione==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.headerName");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapNameIntegrazione = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.headerName': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapNameIntegrazione = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapNameIntegrazione;
	}
	
	private static String headerSoapNameIntegrazione_backwardCompatibility_openspcoop2 = null;
	public String getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop2() {
		if(OpenSPCoop2Properties.headerSoapNameIntegrazione_backwardCompatibility_openspcoop2==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop2.headerName");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapNameIntegrazione_backwardCompatibility_openspcoop2 = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop2.headerName': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapNameIntegrazione_backwardCompatibility_openspcoop2 = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapNameIntegrazione_backwardCompatibility_openspcoop2;
	}
	
	private static String headerSoapNameIntegrazione_backwardCompatibility_openspcoop1 = null;
	public String getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop1() {
		if(OpenSPCoop2Properties.headerSoapNameIntegrazione_backwardCompatibility_openspcoop1==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop1.headerName");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapNameIntegrazione_backwardCompatibility_openspcoop1 = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop1.headerName': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapNameIntegrazione_backwardCompatibility_openspcoop1 = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapNameIntegrazione_backwardCompatibility_openspcoop1;
	}

	/**
	 * Restituisce l'actord dell'header Soap di integrazione di default
	 * 
	 * @return Restituisce l'actor dell'header Soap di integrazione di default
	 */
	private static String headerSoapActorIntegrazione = null;
	public String getHeaderSoapActorIntegrazione() {
		if(OpenSPCoop2Properties.headerSoapActorIntegrazione==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.headerActor");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapActorIntegrazione = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.headerActor': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapActorIntegrazione = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapActorIntegrazione;
	}
	
	private static String headerSoapActorIntegrazione_backwardCompatibility_openspcoop2 = null;
	public String getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() {
		if(OpenSPCoop2Properties.headerSoapActorIntegrazione_backwardCompatibility_openspcoop2==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop2.headerActor");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapActorIntegrazione_backwardCompatibility_openspcoop2 = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop2.headerActor': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapActorIntegrazione_backwardCompatibility_openspcoop2 = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapActorIntegrazione_backwardCompatibility_openspcoop2;
	}
	
	private static String headerSoapActorIntegrazione_backwardCompatibility_openspcoop1 = null;
	public String getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1() {
		if(OpenSPCoop2Properties.headerSoapActorIntegrazione_backwardCompatibility_openspcoop1==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop1.headerActor");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapActorIntegrazione_backwardCompatibility_openspcoop1 = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop1.headerActor': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapActorIntegrazione_backwardCompatibility_openspcoop1 = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapActorIntegrazione_backwardCompatibility_openspcoop1;
	}

	/**
	 * Restituisce il prefix dell'header Soap di integrazione di default
	 * 
	 * @return Restituisce il prefix dell'header Soap di integrazione di default
	 */
	private static String headerSoapPrefixIntegrazione = null;
	public String getHeaderSoapPrefixIntegrazione() {
		if(OpenSPCoop2Properties.headerSoapPrefixIntegrazione==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.headerPrefix");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapPrefixIntegrazione = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.headerPrefix': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapPrefixIntegrazione = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapPrefixIntegrazione;
	}
	
	private static String headerSoapPrefixIntegrazione_backwardCompatibility_openspcoop2 = null;
	public String getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop2() {
		if(OpenSPCoop2Properties.headerSoapPrefixIntegrazione_backwardCompatibility_openspcoop2==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop2.headerPrefix");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapPrefixIntegrazione_backwardCompatibility_openspcoop2 = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop2.headerPrefix': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapPrefixIntegrazione_backwardCompatibility_openspcoop2 = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapPrefixIntegrazione_backwardCompatibility_openspcoop2;
	}
	
	private static String headerSoapPrefixIntegrazione_backwardCompatibility_openspcoop1 = null;
	public String getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop1() {
		if(OpenSPCoop2Properties.headerSoapPrefixIntegrazione_backwardCompatibility_openspcoop1==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop1.headerPrefix");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapPrefixIntegrazione_backwardCompatibility_openspcoop1 = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop1.headerPrefix': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapPrefixIntegrazione_backwardCompatibility_openspcoop1 = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapPrefixIntegrazione_backwardCompatibility_openspcoop1;
	}
	
	/**
	 * Restituisce il nome dell'elemento che contiene le informazioni di protocollo
	 * 
	 * @return Restituisce il nome dell'elemento che contiene le informazioni di protocollo
	 */
	private static String headerSoapExtProtocolInfoNomeElementoIntegrazione = null;
	public String getHeaderSoapExtProtocolInfoNomeElementoIntegrazione() {
		if(OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.extProtocolInfo.elemento.nome");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.extProtocolInfo.elemento.nome': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione;
	}
	private static String headerSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2 = null;
	public String getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2() {
		if(OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop2.extProtocolInfo.elemento.nome");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2 = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop2.extProtocolInfo.elemento.nome': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2 = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2;
	}
	private static String headerSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1 = null;
	public String getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1() {
		if(OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop1.extProtocolInfo.elemento.nome");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1 = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop1.extProtocolInfo.elemento.nome': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1 = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1;
	}
	/**
	 * Restituisce il nome del tipo dell'elemento che contiene le informazioni di protocollo
	 * 
	 * @return Restituisce il nome del tipo dell'elemento che contiene le informazioni di protocollo
	 */
	private static String headerSoapExtProtocolInfoNomeAttributoIntegrazione = null;
	public String getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione() {
		if(OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.extProtocolInfo.attributo.nome");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.extProtocolInfo.attributo.nome': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione;
	}
	
	private static String headerSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2 = null;
	public String getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2() {
		if(OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop2.extProtocolInfo.attributo.nome");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2 = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop2.extProtocolInfo.attributo.nome': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2 = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2;
	}
	
	private static String headerSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1 = null;
	public String getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1() {
		if(OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop1.extProtocolInfo.attributo.nome");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1 = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.backward_compatibility.openspcoop1.extProtocolInfo.attributo.nome': "+e.getMessage(),e);
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1 = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1;
	}

	
	/* ********  INTEGRAZIONE TEMPLATE  ******** */
    
	private static String getIntegrazioneTemplateRequestPropertyTipo = null;
	public String getIntegrazioneTemplateRequestPropertyTipo() {
		String pName = "org.openspcoop2.pdd.integrazione.template.request.property.tipo";
		if(OpenSPCoop2Properties.getIntegrazioneTemplateRequestPropertyTipo==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.getIntegrazioneTemplateRequestPropertyTipo = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				OpenSPCoop2Properties.getIntegrazioneTemplateRequestPropertyTipo = null;
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplateRequestPropertyTipo;
	}
	
	private static String getIntegrazioneTemplateRequestPropertyFile = null;
	public String getIntegrazioneTemplateRequestPropertyFile() {
		String pName = "org.openspcoop2.pdd.integrazione.template.request.property.file";
		if(OpenSPCoop2Properties.getIntegrazioneTemplateRequestPropertyFile==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.getIntegrazioneTemplateRequestPropertyFile = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				OpenSPCoop2Properties.getIntegrazioneTemplateRequestPropertyFile = null;
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplateRequestPropertyFile;
	}
	
	private static String getIntegrazioneTemplateResponsePropertyTipo = null;
	public String getIntegrazioneTemplateResponsePropertyTipo() {
		String pName = "org.openspcoop2.pdd.integrazione.template.response.property.tipo";
		if(OpenSPCoop2Properties.getIntegrazioneTemplateResponsePropertyTipo==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.getIntegrazioneTemplateResponsePropertyTipo = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				OpenSPCoop2Properties.getIntegrazioneTemplateResponsePropertyTipo = null;
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplateResponsePropertyTipo;
	}
	
	private static String getIntegrazioneTemplateResponsePropertyFile = null;
	public String getIntegrazioneTemplateResponsePropertyFile() {
		String pName = "org.openspcoop2.pdd.integrazione.template.response.property.file";
		if(OpenSPCoop2Properties.getIntegrazioneTemplateResponsePropertyFile==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.getIntegrazioneTemplateResponsePropertyFile = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				OpenSPCoop2Properties.getIntegrazioneTemplateResponsePropertyFile = null;
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplateResponsePropertyFile;
	}
	
	
    
	private static Boolean getIntegrazioneTemplatePortaDelegataRequestTipo_read = null;
	private static TipoTrasformazione getIntegrazioneTemplatePortaDelegataRequestTipo = null;
	public TipoTrasformazione getIntegrazioneTemplatePortaDelegataRequestTipo() throws Exception{
		String pName = "org.openspcoop2.pdd.integrazione.template.pd.request.tipo";
		if(OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataRequestTipo_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name!=null) {
					name = name.trim();
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataRequestTipo = UtilitiesTemplate.convert(name);
					
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataRequestTipo_read = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				throw new Exception (e);
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataRequestTipo;
	}
	
	private static Boolean getIntegrazioneTemplatePortaDelegataRequestFile_read = null;
	private static String getIntegrazioneTemplatePortaDelegataRequestFile = null;
	public String getIntegrazioneTemplatePortaDelegataRequestFile() throws Exception{
		String pName = "org.openspcoop2.pdd.integrazione.template.pd.request.file";
		if(OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataRequestFile_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name!=null) {
					name = name.trim();
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataRequestFile = name;
					
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataRequestFile_read = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				throw new Exception (e);
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataRequestFile;
	}
	
	private static Boolean getIntegrazioneTemplatePortaDelegataResponseTipo_read = null;
	private static TipoTrasformazione getIntegrazioneTemplatePortaDelegataResponseTipo = null;
	public TipoTrasformazione getIntegrazioneTemplatePortaDelegataResponseTipo() throws Exception{
		String pName = "org.openspcoop2.pdd.integrazione.template.pd.response.tipo";
		if(OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataResponseTipo_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name!=null) {
					name = name.trim();
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataResponseTipo = UtilitiesTemplate.convert(name);

					OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataResponseTipo_read = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				throw new Exception (e);
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataResponseTipo;
	}
	
	private static Boolean getIntegrazioneTemplatePortaDelegataResponseFile_read = null;
	private static String getIntegrazioneTemplatePortaDelegataResponseFile = null;
	public String getIntegrazioneTemplatePortaDelegataResponseFile() throws Exception{
		String pName = "org.openspcoop2.pdd.integrazione.template.pd.response.file";
		if(OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataResponseFile_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name!=null) {
					name = name.trim();
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataResponseFile = name;
					
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataResponseFile_read = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				throw new Exception (e);
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplatePortaDelegataResponseFile;
	}
    
	
	private static Boolean getIntegrazioneTemplatePortaApplicativaRequestTipo_read = null;
	private static TipoTrasformazione getIntegrazioneTemplatePortaApplicativaRequestTipo = null;
	public TipoTrasformazione getIntegrazioneTemplatePortaApplicativaRequestTipo() throws Exception{
		String pName = "org.openspcoop2.pdd.integrazione.template.pa.request.tipo";
		if(OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaRequestTipo_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name!=null) {
					name = name.trim();
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaRequestTipo = UtilitiesTemplate.convert(name);
					
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaRequestTipo_read = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				throw new Exception (e);
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaRequestTipo;
	}
	
	private static Boolean getIntegrazioneTemplatePortaApplicativaRequestFile_read = null;
	private static String getIntegrazioneTemplatePortaApplicativaRequestFile = null;
	public String getIntegrazioneTemplatePortaApplicativaRequestFile() throws Exception{
		String pName = "org.openspcoop2.pdd.integrazione.template.pa.request.file";
		if(OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaRequestFile_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name!=null) {
					name = name.trim();
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaRequestFile = name;
					
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaRequestFile_read = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				throw new Exception (e);
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaRequestFile;
	}
	
	private static Boolean getIntegrazioneTemplatePortaApplicativaResponseTipo_read = null;
	private static TipoTrasformazione getIntegrazioneTemplatePortaApplicativaResponseTipo = null;
	public TipoTrasformazione getIntegrazioneTemplatePortaApplicativaResponseTipo() throws Exception{
		String pName = "org.openspcoop2.pdd.integrazione.template.pa.response.tipo";
		if(OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaResponseTipo_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name!=null) {
					name = name.trim();
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaResponseTipo = UtilitiesTemplate.convert(name);
					
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaResponseTipo_read = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				throw new Exception (e);
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaResponseTipo;
	}
	
	private static Boolean getIntegrazioneTemplatePortaApplicativaResponseFile_read = null;
	private static String getIntegrazioneTemplatePortaApplicativaResponseFile = null;
	public String getIntegrazioneTemplatePortaApplicativaResponseFile() throws Exception{
		String pName = "org.openspcoop2.pdd.integrazione.template.pa.response.file";
		if(OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaResponseFile_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name!=null) {
					name = name.trim();
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaResponseFile = name;
					
					OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaResponseFile_read = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				throw new Exception (e);
			}    
		}

		return OpenSPCoop2Properties.getIntegrazioneTemplatePortaApplicativaResponseFile;
	}
	
   




	/* ********  CONNETTORI  ******** */

	private static Boolean isRitardoConsegnaAbilitato = null;
	public boolean isRitardoConsegnaAbilitato() throws Exception{
		if(OpenSPCoop2Properties.isRitardoConsegnaAbilitato==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettore.ritardo.stato");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				OpenSPCoop2Properties.isRitardoConsegnaAbilitato = CostantiConfigurazione.ABILITATO.equals(name);

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.connettore.ritardo.stato': "+e.getMessage(),e);
				throw new Exception (e);
			}    
		}

		return OpenSPCoop2Properties.isRitardoConsegnaAbilitato;
	}

	private static Long ritardoConsegnaEsponenziale = null;
	public long getRitardoConsegnaEsponenziale() {	
		if(OpenSPCoop2Properties.ritardoConsegnaEsponenziale==null){
			try{ 
				long r = -1;
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettore.ritardo.fattore");
				if (name != null) {
					name = name.trim();
					r = java.lang.Long.parseLong(name);
				}
				if(r<0)
					throw new Exception("Il ritardo deve essere > 0");
				OpenSPCoop2Properties.ritardoConsegnaEsponenziale = r;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.connettore.ritardo.fattore': "+e.getMessage(),e);
				OpenSPCoop2Properties.ritardoConsegnaEsponenziale = -1L;
			}    
		}

		return OpenSPCoop2Properties.ritardoConsegnaEsponenziale;
	}

	private static Boolean isRitardoConsegnaEsponenzialeConMoltiplicazione = null;
	public boolean isRitardoConsegnaEsponenzialeConMoltiplicazione() throws Exception{
		if(OpenSPCoop2Properties.isRitardoConsegnaEsponenzialeConMoltiplicazione==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettore.ritardo.operazione");
				if (name != null) {
					name = name.trim();
					if(name.equals("+")){
						OpenSPCoop2Properties.isRitardoConsegnaEsponenzialeConMoltiplicazione = false;
					}else if(name.equals("*")){
						OpenSPCoop2Properties.isRitardoConsegnaEsponenzialeConMoltiplicazione = true;
					}else{
						throw new Exception("Tipo di operazione non conosciuta: "+name);
					}
				}else{
					throw new Exception("Tipo di operazione non definita");
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.connettore.ritardo.operazione': "+e.getMessage(),e);
				throw new Exception (e);
			}    
		}

		return OpenSPCoop2Properties.isRitardoConsegnaEsponenzialeConMoltiplicazione;
	}

	private static Long ritardoConsegnaEsponenzialeLimite = null;
	public long getRitardoConsegnaEsponenzialeLimite() {	
		if(OpenSPCoop2Properties.ritardoConsegnaEsponenzialeLimite==null){
			try{ 
				long r = 0;
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettore.ritardo.limite");
				if (name != null) {
					name = name.trim();
					r = java.lang.Long.parseLong(name);
				}
				if(r<=0)
					throw new Exception("Il limite deve essere > 0");
				OpenSPCoop2Properties.ritardoConsegnaEsponenzialeLimite = r;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.connettore.ritardo.limite': "+e.getMessage(),e);
				OpenSPCoop2Properties.ritardoConsegnaEsponenzialeLimite = -1L;
			}    
		}

		return OpenSPCoop2Properties.ritardoConsegnaEsponenzialeLimite;
	}














	/* ************* CACHE GESTORE MESSAGGIO *******************/

	/**
	 * Restituisce l'indicazione se la cache e' abilitata
	 *
	 * @return Restituisce l'indicazione se la cache e' abilitata
	 */
	private static Boolean isAbilitataCacheGestoreMessaggi_value = null;
	public boolean isAbilitataCacheGestoreMessaggi() {
		if(OpenSPCoop2Properties.isAbilitataCacheGestoreMessaggi_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.gestoreMessaggi.cache.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isAbilitataCacheGestoreMessaggi_value = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.enable' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isAbilitataCacheGestoreMessaggi_value = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.enable' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isAbilitataCacheGestoreMessaggi_value = false;
			}
		}

		return OpenSPCoop2Properties.isAbilitataCacheGestoreMessaggi_value;
	}

	/**
	 * Restituisce la dimensione della cache
	 *
	 * @return Restituisce la dimensione della cache
	 */
	private static Integer dimensioneCacheGestoreMessaggi_value = null;
	public int getDimensioneCacheGestoreMessaggi() throws OpenSPCoop2ConfigurationException{	
		if(OpenSPCoop2Properties.dimensioneCacheGestoreMessaggi_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.gestoreMessaggi.cache.dimensione"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.dimensioneCacheGestoreMessaggi_value = Integer.parseInt(value);
				}else{
					OpenSPCoop2Properties.dimensioneCacheGestoreMessaggi_value = -1;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.dimensione': "+e.getMessage(),e);
				throw new OpenSPCoop2ConfigurationException("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.dimensione'",e);
			}
		}

		return OpenSPCoop2Properties.dimensioneCacheGestoreMessaggi_value;
	}

	/**
	 * Restituisce l'algoritmo della cache
	 *
	 * @return Restituisce l'algoritmo della cache
	 */
	private static String algoritmoCacheGestoreMessaggi_value = null;
	public String getAlgoritmoCacheGestoreMessaggi() throws OpenSPCoop2ConfigurationException{	
		if(OpenSPCoop2Properties.algoritmoCacheGestoreMessaggi_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.gestoreMessaggi.cache.algoritmo"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.algoritmoCacheGestoreMessaggi_value = value;
				}else{
					OpenSPCoop2Properties.algoritmoCacheGestoreMessaggi_value = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.algoritmo': "+e.getMessage(),e);
				throw new OpenSPCoop2ConfigurationException("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.algoritmo'",e);
			}
		}

		return OpenSPCoop2Properties.algoritmoCacheGestoreMessaggi_value;
	}

	/**
	 * Restituisce la itemIdleTime della cache
	 *
	 * @return Restituisce la itemIdleTime della cache
	 */
	private static Integer itemIdleTimeCacheGestoreMessaggi_value = null;
	public int getItemIdleTimeCacheGestoreMessaggi() throws OpenSPCoop2ConfigurationException{	
		if(OpenSPCoop2Properties.itemIdleTimeCacheGestoreMessaggi_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.gestoreMessaggi.cache.itemIdleTime"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.itemIdleTimeCacheGestoreMessaggi_value = Integer.parseInt(value);
				}else{
					OpenSPCoop2Properties.itemIdleTimeCacheGestoreMessaggi_value = -1;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.itemIdleTime': "+e.getMessage(),e);
				throw new OpenSPCoop2ConfigurationException("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.itemIdleTime'",e);
			}
		}

		return OpenSPCoop2Properties.itemIdleTimeCacheGestoreMessaggi_value;
	}

	/**
	 * Restituisce la  itemLifeSecond della cache
	 *
	 * @return Restituisce la itemLifeSecond della cache
	 */
	private static Integer itemLifeSecondCacheGestoreMessaggi_value = null;
	public int getItemLifeSecondCacheGestoreMessaggi() throws OpenSPCoop2ConfigurationException{
		if(OpenSPCoop2Properties.itemLifeSecondCacheGestoreMessaggi_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.gestoreMessaggi.cache.itemLifeSecond"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.itemLifeSecondCacheGestoreMessaggi_value = Integer.parseInt(value);
				}else{
					OpenSPCoop2Properties.itemLifeSecondCacheGestoreMessaggi_value = -1;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.itemLifeSecond': "+e.getMessage(),e);
				throw new OpenSPCoop2ConfigurationException("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.itemLifeSecond'",e);
			}
		}

		return OpenSPCoop2Properties.itemLifeSecondCacheGestoreMessaggi_value;
	}





	/* ******* GESTORE JMX *********** */
	/**
	 * Restituisce l'indicazione se istanziare le risorse JMX
	 *
	 * @return Restituisce Restituisce l'indicazione se istanziare le risorse JMX
	 * 
	 */
	private static Boolean isRisorseJMXAbilitate = null;
	public boolean isRisorseJMXAbilitate(){
		if(OpenSPCoop2Properties.isRisorseJMXAbilitate==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.jmx.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isRisorseJMXAbilitate = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.jmx.enable' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isRisorseJMXAbilitate = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.jmx.enable' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isRisorseJMXAbilitate = false;
			}
		}

		return OpenSPCoop2Properties.isRisorseJMXAbilitate;
	}

	/**
	 * Restituisce il Nome JNDI del MBeanServer
	 *
	 * @return il Nome JNDI del MBeanServer
	 * 
	 */
	private static String jndiNameMBeanServer = null;
	public String getJNDIName_MBeanServer() {	
		if(OpenSPCoop2Properties.jndiNameMBeanServer==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.jmx.jndi.mbeanServer");
				if(name!=null)
					name = name.trim();
				OpenSPCoop2Properties.jndiNameMBeanServer = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.core.jmx.jndi.mbeanServer': "+e.getMessage(),e);
				OpenSPCoop2Properties.jndiNameMBeanServer = null;
			}    
		}

		return OpenSPCoop2Properties.jndiNameMBeanServer;
	}

	/**
	 * Restituisce le proprieta' da utilizzare con il contesto JNDI di lookup, se impostate.
	 *
	 * @return proprieta' da utilizzare con il contesto JNDI di lookup.
	 * 
	 */
	private static java.util.Properties jndiContextMBeanServer = null;
	public java.util.Properties getJNDIContext_MBeanServer() {
		if(OpenSPCoop2Properties.jndiContextMBeanServer==null){
			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.core.jmx.jndi.property.");
				OpenSPCoop2Properties.jndiContextMBeanServer = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI 'org.openspcoop2.pdd.core.jmx.jndi.property.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.jndiContextMBeanServer = null;
			}    
		}

		return OpenSPCoop2Properties.jndiContextMBeanServer;
	}









	/* ************* CONNETTORI ***************** */
	/**
	 * Restituisce timeout per la istanziazione della connessione
	 *
	 * @return Restituisce timeout per la istanziazione della connessione
	 * 
	 */
	private static Integer connectionTimeout_inoltroBuste = null;
	public int getConnectionTimeout_inoltroBuste() {	
		if(OpenSPCoop2Properties.connectionTimeout_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.connection.timeout");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.connectionTimeout_inoltroBuste = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.connection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_TIMEOUT_INOLTRO_BUSTE);
					OpenSPCoop2Properties.connectionTimeout_inoltroBuste = CostantiPdD.CONNETTORE_CONNECTION_TIMEOUT_INOLTRO_BUSTE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.connection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_TIMEOUT_INOLTRO_BUSTE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.connectionTimeout_inoltroBuste = CostantiPdD.CONNETTORE_CONNECTION_TIMEOUT_INOLTRO_BUSTE;
			}  
		}

		return OpenSPCoop2Properties.connectionTimeout_inoltroBuste;
	}


	/**
	 * Restituisce timeout per la istanziazione della connessione
	 *
	 * @return Restituisce timeout per la istanziazione della connessione
	 * 
	 */
	private static Integer connectionTimeout_consegnaContenutiApplicativi = null;
	public int getConnectionTimeout_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.connectionTimeout_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.connection.timeout");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.connectionTimeout_consegnaContenutiApplicativi = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.connection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI);
					OpenSPCoop2Properties.connectionTimeout_consegnaContenutiApplicativi = CostantiPdD.CONNETTORE_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.connection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.connectionTimeout_consegnaContenutiApplicativi = CostantiPdD.CONNETTORE_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI;
			}  
		}

		return OpenSPCoop2Properties.connectionTimeout_consegnaContenutiApplicativi;
	}


	/**
	 * Restituisce timeout per la lettura dalla connessione
	 *
	 * @return Restituisce timeout per la lettura dalla connessione
	 * 
	 */
	private static Integer readConnectionTimeout_inoltroBuste = null;
	public int getReadConnectionTimeout_inoltroBuste() {	
		if(OpenSPCoop2Properties.readConnectionTimeout_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.readConnection.timeout");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.readConnectionTimeout_inoltroBuste = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.readConnection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_READ_CONNECTION_TIMEOUT_INOLTRO_BUSTE);
					OpenSPCoop2Properties.readConnectionTimeout_inoltroBuste = CostantiPdD.CONNETTORE_READ_CONNECTION_TIMEOUT_INOLTRO_BUSTE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.readConnection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_READ_CONNECTION_TIMEOUT_INOLTRO_BUSTE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.readConnectionTimeout_inoltroBuste = CostantiPdD.CONNETTORE_READ_CONNECTION_TIMEOUT_INOLTRO_BUSTE;
			}  
		}

		return OpenSPCoop2Properties.readConnectionTimeout_inoltroBuste;
	}


	/**
	 * Restituisce timeout per la lettura dalla connessione
	 *
	 * @return Restituisce timeout per la lettura dalla connessione
	 * 
	 */
	private static Integer readConnectionTimeout_consegnaContenutiApplicativi = null;
	public int getReadConnectionTimeout_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.readConnectionTimeout_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.readConnection.timeout");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.readConnectionTimeout_consegnaContenutiApplicativi = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.readConnection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_READ_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI);
					OpenSPCoop2Properties.readConnectionTimeout_consegnaContenutiApplicativi = CostantiPdD.CONNETTORE_READ_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.readConnection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_READ_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.readConnectionTimeout_consegnaContenutiApplicativi = CostantiPdD.CONNETTORE_READ_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI;
			}  
		}

		return OpenSPCoop2Properties.readConnectionTimeout_consegnaContenutiApplicativi;
	}

	/**
	 * Restituisce timeout in millisecondi massimi durante il quale una connessione viene mantenuta aperta dalla PdD. 
	 *
	 * @return Restituisce timeout in millisecondi massimi durante il quale una connessione viene mantenuta aperta dalla PdD. 
	 * 
	 */
	private static Integer connectionLife_inoltroBuste = null;
	public int getConnectionLife_inoltroBuste() {	
		if(OpenSPCoop2Properties.connectionLife_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.connection.life");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.connectionLife_inoltroBuste = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.connection.life' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_LIFE_INOLTRO_BUSTE);
					OpenSPCoop2Properties.connectionLife_inoltroBuste = CostantiPdD.CONNETTORE_CONNECTION_LIFE_INOLTRO_BUSTE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.connection.life' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_LIFE_INOLTRO_BUSTE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.connectionLife_inoltroBuste = CostantiPdD.CONNETTORE_CONNECTION_LIFE_INOLTRO_BUSTE;
			}  
		}

		return OpenSPCoop2Properties.connectionLife_inoltroBuste;
	}


	/**
	 * Restituisce timeout in millisecondi massimi durante il quale una connessione viene mantenuta aperta dalla PdD. 
	 *
	 * @return Restituisce timeout in millisecondi massimi durante il quale una connessione viene mantenuta aperta dalla PdD. 
	 * 
	 */
	private static Integer connectionLife_consegnaContenutiApplicativi = null;
	public int getConnectionLife_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.connectionLife_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.connection.life");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.connectionLife_consegnaContenutiApplicativi = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.connection.life' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_LIFE_CONSEGNA_CONTENUTI_APPLICATIVI);
					OpenSPCoop2Properties.connectionLife_consegnaContenutiApplicativi = CostantiPdD.CONNETTORE_CONNECTION_LIFE_CONSEGNA_CONTENUTI_APPLICATIVI;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.connection.life' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_LIFE_CONSEGNA_CONTENUTI_APPLICATIVI+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.connectionLife_consegnaContenutiApplicativi = CostantiPdD.CONNETTORE_CONNECTION_LIFE_CONSEGNA_CONTENUTI_APPLICATIVI;
			}  
		}

		return OpenSPCoop2Properties.connectionLife_consegnaContenutiApplicativi;
	}


	/* ***************** HTTPS  ************* */
	
	private static Boolean isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste = null;
	public boolean isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste() {	
		if(OpenSPCoop2Properties.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.http.urlHttps.overrideDefaultConfiguration");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.http.urlHttps.overrideDefaultConfiguration' non impostata, viene utilizzato il default="+true);
					OpenSPCoop2Properties.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.http.urlHttps.overrideDefaultConfiguration' non impostata, viene utilizzato il default="+true+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste = true;
			}  
		}

		return OpenSPCoop2Properties.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste;
	}
	
	private static Boolean isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi = null;
	public boolean isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.http.urlHttps.overrideDefaultConfiguration");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.http.urlHttps.overrideDefaultConfiguration' non impostata, viene utilizzato il default="+true);
					OpenSPCoop2Properties.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.http.urlHttps.overrideDefaultConfiguration' non impostata, viene utilizzato il default="+true+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi = true;
			}  
		}

		return OpenSPCoop2Properties.isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi;
	}
	
	private static File getConnettoreHttp_urlHttps_repository_inoltroBuste = null;
	public File getConnettoreHttp_urlHttps_repository_inoltroBuste() throws Exception {	
		if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.http.urlHttps.repository");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste = new File(name);
				if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste.exists()) {
					if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste.isDirectory()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste.getAbsolutePath()+"] not dir");
					}
					if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste.canRead()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste.getAbsolutePath()+"] cannot read");
					}
					if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste.canWrite()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste.getAbsolutePath()+"] cannot write");
					}
				}
				else {
					// viene creata automaticamente
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.http.urlHttps.repository': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste;
	}
	
	private static File getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi = null;
	public File getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi() throws Exception {	
		if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.http.urlHttps.repository");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi = new File(name);
				if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi.exists()) {
					if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi.isDirectory()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi.getAbsolutePath()+"] not dir");
					}
					if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi.canRead()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi.getAbsolutePath()+"] cannot read");
					}
					if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi.canWrite()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi.getAbsolutePath()+"] cannot write");
					}
				}
				else {
					// viene creata automaticamente
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.http.urlHttps.repository': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi;
	}
	
	private static Boolean isConnettoreHttp_urlHttps_cacheEnabled = null;
	public boolean isConnettoreHttp_urlHttps_cacheEnabled() {	
		if(OpenSPCoop2Properties.isConnettoreHttp_urlHttps_cacheEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.http.urlHttps.cache.enabled");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isConnettoreHttp_urlHttps_cacheEnabled = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.http.urlHttps.cache.enabled' non impostata, viene utilizzato il default="+true);
					OpenSPCoop2Properties.isConnettoreHttp_urlHttps_cacheEnabled = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.http.urlHttps.cache.enabled' non impostata, viene utilizzato il default="+true+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isConnettoreHttp_urlHttps_cacheEnabled = true;
			}  
		}

		return OpenSPCoop2Properties.isConnettoreHttp_urlHttps_cacheEnabled;
	}
	
	private static Integer getConnettoreHttp_urlHttps_cacheSize = null;
	public int getConnettoreHttp_urlHttps_cacheSize() {	
		if(OpenSPCoop2Properties.getConnettoreHttp_urlHttps_cacheSize==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.http.urlHttps.cache.size");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getConnettoreHttp_urlHttps_cacheSize = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.http.urlHttps.cache.size' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_HTTP_URL_HTTPS_CACHE_SIZE);
					OpenSPCoop2Properties.getConnettoreHttp_urlHttps_cacheSize = CostantiPdD.CONNETTORE_HTTP_URL_HTTPS_CACHE_SIZE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.http.urlHttps.cache.size' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_HTTP_URL_HTTPS_CACHE_SIZE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getConnettoreHttp_urlHttps_cacheSize = CostantiPdD.CONNETTORE_HTTP_URL_HTTPS_CACHE_SIZE;
			}  
		}

		return OpenSPCoop2Properties.getConnettoreHttp_urlHttps_cacheSize;
	}
	
	
	
	
	/* ***************** Servizi HTTP  ************* */
	
	private static Boolean serviceRequestHttpMethodPatchEnabled = null;
	public boolean isServiceRequestHttpMethodPatchEnabled() {	
		if(OpenSPCoop2Properties.serviceRequestHttpMethodPatchEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.request.method.PATCH");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.serviceRequestHttpMethodPatchEnabled = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.request.method.PATCH' non impostata, viene utilizzato il default="+true);
					OpenSPCoop2Properties.serviceRequestHttpMethodPatchEnabled = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.request.method.PATCH' non impostata, viene utilizzato il default="+true+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.serviceRequestHttpMethodPatchEnabled = true;
			}  
		}

		return OpenSPCoop2Properties.serviceRequestHttpMethodPatchEnabled;
	}
	
	private static Boolean serviceRequestHttpMethodLinkEnabled = null;
	public boolean isServiceRequestHttpMethodLinkEnabled() {	
		if(OpenSPCoop2Properties.serviceRequestHttpMethodLinkEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.request.method.LINK");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.serviceRequestHttpMethodLinkEnabled = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.request.method.LINK' non impostata, viene utilizzato il default="+true);
					OpenSPCoop2Properties.serviceRequestHttpMethodLinkEnabled = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.request.method.LINK' non impostata, viene utilizzato il default="+true+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.serviceRequestHttpMethodLinkEnabled = true;
			}  
		}

		return OpenSPCoop2Properties.serviceRequestHttpMethodLinkEnabled;
	}
	
	private static Boolean serviceRequestHttpMethodUnlinkEnabled = null;
	public boolean isServiceRequestHttpMethodUnlinkEnabled() {	
		if(OpenSPCoop2Properties.serviceRequestHttpMethodUnlinkEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.request.method.UNLINK");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.serviceRequestHttpMethodUnlinkEnabled = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.request.method.UNLINK' non impostata, viene utilizzato il default="+true);
					OpenSPCoop2Properties.serviceRequestHttpMethodUnlinkEnabled = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.request.method.UNLINK' non impostata, viene utilizzato il default="+true+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.serviceRequestHttpMethodUnlinkEnabled = true;
			}  
		}

		return OpenSPCoop2Properties.serviceRequestHttpMethodUnlinkEnabled;
	}
	
	
	private static TransferLengthModes readTransferLengthModes_ricezioneContenutiApplicativi = null;
	public TransferLengthModes getTransferLengthModes_ricezioneContenutiApplicativi() {	
		if(OpenSPCoop2Properties.readTransferLengthModes_ricezioneContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.httpTransferLength");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.readTransferLengthModes_ricezioneContenutiApplicativi = TransferLengthModes.getTransferLengthModes(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.WEBSERVER_DEFAULT);
					OpenSPCoop2Properties.readTransferLengthModes_ricezioneContenutiApplicativi = TransferLengthModes.WEBSERVER_DEFAULT;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.WEBSERVER_DEFAULT+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.readTransferLengthModes_ricezioneContenutiApplicativi = TransferLengthModes.WEBSERVER_DEFAULT;
			}  
		}

		return OpenSPCoop2Properties.readTransferLengthModes_ricezioneContenutiApplicativi;
	}
	
	private static TransferLengthModes readTransferLengthModes_ricezioneBuste = null;
	public TransferLengthModes getTransferLengthModes_ricezioneBuste() {	
		if(OpenSPCoop2Properties.readTransferLengthModes_ricezioneBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneBuste.httpTransferLength");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.readTransferLengthModes_ricezioneBuste = TransferLengthModes.getTransferLengthModes(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.WEBSERVER_DEFAULT);
					OpenSPCoop2Properties.readTransferLengthModes_ricezioneBuste = TransferLengthModes.WEBSERVER_DEFAULT;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.WEBSERVER_DEFAULT+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.readTransferLengthModes_ricezioneBuste = TransferLengthModes.WEBSERVER_DEFAULT;
			}  
		}

		return OpenSPCoop2Properties.readTransferLengthModes_ricezioneBuste;
	}
	
	private static TransferLengthModes readTransferLengthModes_inoltroBuste = null;
	public TransferLengthModes getTransferLengthModes_inoltroBuste() {	
		if(OpenSPCoop2Properties.readTransferLengthModes_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.httpTransferLength");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.readTransferLengthModes_inoltroBuste = TransferLengthModes.getTransferLengthModes(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.CONTENT_LENGTH);
					OpenSPCoop2Properties.readTransferLengthModes_inoltroBuste = TransferLengthModes.CONTENT_LENGTH;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.CONTENT_LENGTH+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.readTransferLengthModes_inoltroBuste = TransferLengthModes.CONTENT_LENGTH;
			}  
		}

		return OpenSPCoop2Properties.readTransferLengthModes_inoltroBuste;
	}
	
	private static Integer getChunkLength_inoltroBuste = null;
	public int getChunkLength_inoltroBuste() {	
		if(OpenSPCoop2Properties.getChunkLength_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.httpTransferLength.chunkLength");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getChunkLength_inoltroBuste = Integer.parseInt(name);
				}else{
					int DEFAULT_CHUNKLEN = 0;
					OpenSPCoop2Properties.getChunkLength_inoltroBuste = DEFAULT_CHUNKLEN;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.httpTransferLength.chunkLength' posside un valore non corretto:"+e.getMessage(),e);
				OpenSPCoop2Properties.getChunkLength_inoltroBuste = -1;
			}  
		}

		return OpenSPCoop2Properties.getChunkLength_inoltroBuste;
	}
	
	private static TransferLengthModes readTransferLengthModes_consegnaContenutiApplicativi = null;
	public TransferLengthModes getTransferLengthModes_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.readTransferLengthModes_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.httpTransferLength");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.readTransferLengthModes_consegnaContenutiApplicativi = TransferLengthModes.getTransferLengthModes(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.CONTENT_LENGTH);
					OpenSPCoop2Properties.readTransferLengthModes_consegnaContenutiApplicativi = TransferLengthModes.CONTENT_LENGTH;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.CONTENT_LENGTH+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.readTransferLengthModes_consegnaContenutiApplicativi = TransferLengthModes.CONTENT_LENGTH;
			}  
		}

		return OpenSPCoop2Properties.readTransferLengthModes_consegnaContenutiApplicativi;
	}
	
	private static Integer getChunkLength_consegnaContenutiApplicativi = null;
	public int getChunkLength_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.getChunkLength_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.httpTransferLength.chunkLength");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getChunkLength_consegnaContenutiApplicativi = Integer.parseInt(name);
				}else{
					int DEFAULT_CHUNKLEN = 0;
					OpenSPCoop2Properties.getChunkLength_consegnaContenutiApplicativi = DEFAULT_CHUNKLEN;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.httpTransferLength.chunkLength' posside un valore non corretto:"+e.getMessage(),e);
				OpenSPCoop2Properties.getChunkLength_consegnaContenutiApplicativi = -1;
			}  
		}

		return OpenSPCoop2Properties.getChunkLength_consegnaContenutiApplicativi;
	}
	
	private static Boolean isAcceptOnlyReturnCode_200_202_inoltroBuste = null;
	public boolean isAcceptOnlyReturnCode_200_202_inoltroBuste() {	
		if(OpenSPCoop2Properties.isAcceptOnlyReturnCode_200_202_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.returnCode.2xx.acceptOnly_202_200");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isAcceptOnlyReturnCode_200_202_inoltroBuste = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.returnCode.2xx.acceptOnly_202_200' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isAcceptOnlyReturnCode_200_202_inoltroBuste = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.returnCode.2xx.acceptOnly_202_200' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isAcceptOnlyReturnCode_200_202_inoltroBuste = true;
			}  
		}

		return OpenSPCoop2Properties.isAcceptOnlyReturnCode_200_202_inoltroBuste;
	}
	
	private static Boolean isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi = null;
	public boolean isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.returnCode.2xx.acceptOnly_202_200");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.returnCode.2xx.acceptOnly_202_200' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.returnCode.2xx.acceptOnly_202_200' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi = true;
			}  
		}

		return OpenSPCoop2Properties.isAcceptOnlyReturnCode_200_202_consegnaContenutiApplicativi;
	}
	
	private static Boolean isAcceptOnlyReturnCode_307_inoltroBuste = null;
	public boolean isAcceptOnlyReturnCode_307_inoltroBuste() {	
		if(OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.returnCode.3xx.acceptOnly_307");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_inoltroBuste = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.returnCode.3xx.acceptOnly_307' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_inoltroBuste = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.returnCode.3xx.acceptOnly_307' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_inoltroBuste = false;
			}  
		}

		return OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_inoltroBuste;
	}
	
	private static Boolean isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi = null;
	public boolean isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.returnCode.3xx.acceptOnly_307");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.returnCode.3xx.acceptOnly_307' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.returnCode.3xx.acceptOnly_307' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi = false;
			}  
		}

		return OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi;
	}
	
	private static Boolean isFollowRedirects_inoltroBuste_soap = null;
	public boolean isFollowRedirects_inoltroBuste_soap() {	
		if(OpenSPCoop2Properties.isFollowRedirects_inoltroBuste_soap==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects.soap");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isFollowRedirects_inoltroBuste_soap = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects.soap' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isFollowRedirects_inoltroBuste_soap = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects.soap' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isFollowRedirects_inoltroBuste_soap = false;
			}  
		}

		return OpenSPCoop2Properties.isFollowRedirects_inoltroBuste_soap;
	}
	
	private static Boolean isFollowRedirects_inoltroBuste_rest = null;
	public boolean isFollowRedirects_inoltroBuste_rest() {	
		if(OpenSPCoop2Properties.isFollowRedirects_inoltroBuste_rest==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects.rest");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isFollowRedirects_inoltroBuste_rest = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects.rest' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isFollowRedirects_inoltroBuste_rest = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects.rest' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isFollowRedirects_inoltroBuste_rest = false;
			}  
		}

		return OpenSPCoop2Properties.isFollowRedirects_inoltroBuste_rest;
	}
	

	
	private static Boolean isFollowRedirects_consegnaContenutiApplicativi_soap = null;
	public boolean isFollowRedirects_consegnaContenutiApplicativi_soap() {	
		if(OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi_soap==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects.soap");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi_soap = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects.soap' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi_soap = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects.soap' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi_soap = false;
			}  
		}

		return OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi_soap;
	}
	
	private static Boolean isFollowRedirects_consegnaContenutiApplicativi_rest = null;
	public boolean isFollowRedirects_consegnaContenutiApplicativi_rest() {	
		if(OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi_rest==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects.rest");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi_rest = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects.rest' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi_rest = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects.rest' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi_rest = false;
			}  
		}

		return OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi_rest;
	}
		
	private static Integer getFollowRedirectsMaxHop_inoltroBuste = null;
	public int getFollowRedirectsMaxHop_inoltroBuste() {	
		if(OpenSPCoop2Properties.getFollowRedirectsMaxHop_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects.maxHop");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getFollowRedirectsMaxHop_inoltroBuste = Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects.maxHop' non impostata, viene utilizzato il default=5");
					OpenSPCoop2Properties.getFollowRedirectsMaxHop_inoltroBuste = 5;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects.maxHop' non impostata, viene utilizzato il default=5, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getFollowRedirectsMaxHop_inoltroBuste = 5;
			}  
		}

		return OpenSPCoop2Properties.getFollowRedirectsMaxHop_inoltroBuste;
	}
	
	private static Integer getFollowRedirectsMaxHop_consegnaContenutiApplicativi = null;
	public int getFollowRedirectsMaxHop_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.getFollowRedirectsMaxHop_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects.maxHop");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getFollowRedirectsMaxHop_consegnaContenutiApplicativi = Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects.maxHop' non impostata, viene utilizzato il default=5");
					OpenSPCoop2Properties.getFollowRedirectsMaxHop_consegnaContenutiApplicativi = 5;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects.maxHop' non impostata, viene utilizzato il default=5, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getFollowRedirectsMaxHop_consegnaContenutiApplicativi = 5;
			}  
		}

		return OpenSPCoop2Properties.getFollowRedirectsMaxHop_consegnaContenutiApplicativi;
	}

	
	private static Boolean checkSoapActionQuotedString_ricezioneContenutiApplicativi = null;
	public boolean checkSoapActionQuotedString_ricezioneContenutiApplicativi() {	
		if(OpenSPCoop2Properties.checkSoapActionQuotedString_ricezioneContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.soapAction.checkQuotedString");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.checkSoapActionQuotedString_ricezioneContenutiApplicativi = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.soapAction.checkQuotedString' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.checkSoapActionQuotedString_ricezioneContenutiApplicativi = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.soapAction.checkQuotedString' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.checkSoapActionQuotedString_ricezioneContenutiApplicativi = false;
			}  
		}

		return OpenSPCoop2Properties.checkSoapActionQuotedString_ricezioneContenutiApplicativi;
	}
	
	private static Boolean checkSoapActionQuotedString_ricezioneBuste = null;
	public boolean checkSoapActionQuotedString_ricezioneBuste() {	
		if(OpenSPCoop2Properties.checkSoapActionQuotedString_ricezioneBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneBuste.soapAction.checkQuotedString");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.checkSoapActionQuotedString_ricezioneBuste = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.soapAction.checkQuotedString' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.checkSoapActionQuotedString_ricezioneBuste = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.soapAction.checkQuotedString' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.checkSoapActionQuotedString_ricezioneBuste = false;
			}  
		}

		return OpenSPCoop2Properties.checkSoapActionQuotedString_ricezioneBuste;
	}
	
	private static String httpUserAgent = null;
	public String getHttpUserAgent() {	
		if(OpenSPCoop2Properties.httpUserAgent==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.http.userAgent");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.httpUserAgent = name;
				}else{
					//NON EMETTO WARNING: this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.http.userAgent' non impostata, viene utilizzato il default="+Costanti.OPENSPCOOP_PRODUCT_VERSION);
					OpenSPCoop2Properties.httpUserAgent = this.getProductName();
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.http.userAgent' non impostata, viene utilizzato il default="+this.getProductName()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.httpUserAgent = this.getProductName();
			}  
		}

		return OpenSPCoop2Properties.httpUserAgent;
	}
	
	private static String httpServer = null;
	public String getHttpServer() {	
		if(OpenSPCoop2Properties.httpServer==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.http.xPdd");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.httpServer = name;
				}else{
					//NON EMETTO WARNING: this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.http.xPdd' non impostata, viene utilizzato il default="+this.getHttpUserAgent());
					OpenSPCoop2Properties.httpServer = this.getVersione();
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.http.xPdd' non impostata, viene utilizzato il default="+this.getVersione()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.httpServer = this.getVersione();
			}  
		}

		return OpenSPCoop2Properties.httpServer;
	}
	
	private static Boolean readHttpXPdDDetails = null;
	private static String httpXPdDDetails = null;
	public String getHttpXPdDDetails() {	
		if(OpenSPCoop2Properties.readHttpXPdDDetails==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.http.xDetails");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.httpXPdDDetails = name;
				}else{
					OpenSPCoop2Properties.httpXPdDDetails = this.getDetails();
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.http.xDetails' non impostata correttamente:"+e.getMessage(),e);
				OpenSPCoop2Properties.httpXPdDDetails = this.getDetails();
			}  
		}
		OpenSPCoop2Properties.readHttpXPdDDetails = true;

		return OpenSPCoop2Properties.httpXPdDDetails;
	}
	
	
	private static Boolean isEnabledEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = null;
	public boolean isEnabledEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi() {	
		if(OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.enabled");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = true;
			}  
		}

		return OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi;
	}
	
	private static Charset getCharsetEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = null;
	public Charset getCharsetEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi() {	
		if(OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.charset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = Charset.toEnumConstant(name);
					if(OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi==null){
						throw new Exception("Valore fornito non valido: "+name);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name());
					OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = Charset.US_ASCII;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = Charset.US_ASCII;
			}  
		}

		return OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi;
	}
	
	private static RFC2047Encoding getEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = null;
	public RFC2047Encoding getEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi() {	
		if(OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.encoding");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = RFC2047Encoding.valueOf(name);
					if(OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi==null){
						throw new Exception("Valore fornito non valido: "+name);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name());
					OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = RFC2047Encoding.Q;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi = RFC2047Encoding.Q;
			}  
		}

		return OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneContenutiApplicativi;
	}
	
	private static Boolean isEnabledEncodingRFC2047HeaderValue_ricezioneBuste = null;
	public boolean isEnabledEncodingRFC2047HeaderValue_ricezioneBuste() {	
		if(OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_ricezioneBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.enabled");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_ricezioneBuste = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_ricezioneBuste = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_ricezioneBuste = true;
			}  
		}

		return OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_ricezioneBuste;
	}
	
	private static Charset getCharsetEncodingRFC2047HeaderValue_ricezioneBuste = null;
	public Charset getCharsetEncodingRFC2047HeaderValue_ricezioneBuste() {	
		if(OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.charset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneBuste = Charset.toEnumConstant(name);
					if(OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneBuste==null){
						throw new Exception("Valore fornito non valido: "+name);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name());
					OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneBuste = Charset.US_ASCII;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneBuste = Charset.US_ASCII;
			}  
		}

		return OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_ricezioneBuste;
	}
	
	private static RFC2047Encoding getEncodingRFC2047HeaderValue_ricezioneBuste = null;
	public RFC2047Encoding getEncodingRFC2047HeaderValue_ricezioneBuste() {	
		if(OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.encoding");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneBuste = RFC2047Encoding.valueOf(name);
					if(OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneBuste==null){
						throw new Exception("Valore fornito non valido: "+name);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name());
					OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneBuste = RFC2047Encoding.Q;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneBuste = RFC2047Encoding.Q;
			}  
		}

		return OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_ricezioneBuste;
	}
	
	private static Boolean isEnabledEncodingRFC2047HeaderValue_inoltroBuste = null;
	public boolean isEnabledEncodingRFC2047HeaderValue_inoltroBuste() {	
		if(OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.enabled");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_inoltroBuste = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_inoltroBuste = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_inoltroBuste = true;
			}  
		}

		return OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_inoltroBuste;
	}
	
	private static Charset getCharsetEncodingRFC2047HeaderValue_inoltroBuste = null;
	public Charset getCharsetEncodingRFC2047HeaderValue_inoltroBuste() {	
		if(OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.charset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_inoltroBuste = Charset.toEnumConstant(name);
					if(OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_inoltroBuste==null){
						throw new Exception("Valore fornito non valido: "+name);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name());
					OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_inoltroBuste = Charset.US_ASCII;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_inoltroBuste = Charset.US_ASCII;
			}  
		}

		return OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_inoltroBuste;
	}
	
	private static RFC2047Encoding getEncodingRFC2047HeaderValue_inoltroBuste = null;
	public RFC2047Encoding getEncodingRFC2047HeaderValue_inoltroBuste() {	
		if(OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.encoding");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_inoltroBuste = RFC2047Encoding.valueOf(name);
					if(OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_inoltroBuste==null){
						throw new Exception("Valore fornito non valido: "+name);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name());
					OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_inoltroBuste = RFC2047Encoding.Q;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_inoltroBuste = RFC2047Encoding.Q;
			}  
		}

		return OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_inoltroBuste;
	}
	
	private static Boolean isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = null;
	public boolean isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.enabled");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = true;
			}  
		}

		return OpenSPCoop2Properties.isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi;
	}
	
	private static Charset getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = null;
	public Charset getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.charset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = Charset.toEnumConstant(name);
					if(OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi==null){
						throw new Exception("Valore fornito non valido: "+name);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name());
					OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = Charset.US_ASCII;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = Charset.US_ASCII;
			}  
		}

		return OpenSPCoop2Properties.getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi;
	}
	
	private static RFC2047Encoding getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = null;
	public RFC2047Encoding getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.encoding");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = RFC2047Encoding.valueOf(name);
					if(OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi==null){
						throw new Exception("Valore fornito non valido: "+name);
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name());
					OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = RFC2047Encoding.Q;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name()+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi = RFC2047Encoding.Q;
			}  
		}

		return OpenSPCoop2Properties.getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi;
	}
	

	private static Boolean isEnabledValidazioneRFC2047HeaderNameValue_ricezioneContenutiApplicativi = null;
	public boolean isEnabledValidazioneRFC2047HeaderNameValue_ricezioneContenutiApplicativi() {	
		if(OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerNameValue.validazione.enabled");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneContenutiApplicativi = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneContenutiApplicativi = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneContenutiApplicativi = true;
			}  
		}

		return OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneContenutiApplicativi;
	}
	
	private static Boolean isEnabledValidazioneRFC2047HeaderNameValue_ricezioneBuste = null;
	public boolean isEnabledValidazioneRFC2047HeaderNameValue_ricezioneBuste() {	
		if(OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneBuste.headerNameValue.validazione.enabled");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneBuste = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneBuste = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneBuste = true;
			}  
		}

		return OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_ricezioneBuste;
	}
	
	private static Boolean isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste = null;
	public boolean isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste() {	
		if(OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.headerNameValue.validazione.enabled");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste = true;
			}  
		}

		return OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste;
	}
	
	private static Boolean isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi = null;
	public boolean isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerNameValue.validazione.enabled");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi = true;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi = true;
			}  
		}

		return OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi;
	}
	
	private static Integer getGestioneCORS_returnCode_ricezioneContenutiApplicativi = null;
	public int getGestioneCORS_returnCode_ricezioneContenutiApplicativi() {	
		if(OpenSPCoop2Properties.getGestioneCORS_returnCode_ricezioneContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.cors.returnCode");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getGestioneCORS_returnCode_ricezioneContenutiApplicativi = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.cors.returnCode' non impostata, viene utilizzato il default="+CostantiPdD.GESTIONE_CORS_RETURN_CODE);
					OpenSPCoop2Properties.getGestioneCORS_returnCode_ricezioneContenutiApplicativi = CostantiPdD.GESTIONE_CORS_RETURN_CODE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.cors.returnCode' non impostata, viene utilizzato il default="+CostantiPdD.GESTIONE_CORS_RETURN_CODE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getGestioneCORS_returnCode_ricezioneContenutiApplicativi = CostantiPdD.GESTIONE_CORS_RETURN_CODE;
			}    
		}

		return OpenSPCoop2Properties.getGestioneCORS_returnCode_ricezioneContenutiApplicativi;
	}
	
	private static Boolean isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneContenutiApplicativi = null;
	public boolean isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneContenutiApplicativi() {	
		if(OpenSPCoop2Properties.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneContenutiApplicativi==null){
			String pName = "org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.cors.resourceHttpMethodQualsiasi";
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneContenutiApplicativi = java.lang.Boolean.valueOf(name);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+false);
					OpenSPCoop2Properties.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneContenutiApplicativi = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+false+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneContenutiApplicativi = false;
			}    
		}

		return OpenSPCoop2Properties.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneContenutiApplicativi;
	}
	
	private static Integer getGestioneCORS_returnCode_ricezioneBuste = null;
	public int getGestioneCORS_returnCode_ricezioneBuste() {	
		if(OpenSPCoop2Properties.getGestioneCORS_returnCode_ricezioneBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.ricezioneBuste.cors.returnCode");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getGestioneCORS_returnCode_ricezioneBuste = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.cors.returnCode' non impostata, viene utilizzato il default="+CostantiPdD.GESTIONE_CORS_RETURN_CODE);
					OpenSPCoop2Properties.getGestioneCORS_returnCode_ricezioneBuste = CostantiPdD.GESTIONE_CORS_RETURN_CODE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.cors.returnCode' non impostata, viene utilizzato il default="+CostantiPdD.GESTIONE_CORS_RETURN_CODE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getGestioneCORS_returnCode_ricezioneBuste = CostantiPdD.GESTIONE_CORS_RETURN_CODE;
			}    
		}

		return OpenSPCoop2Properties.getGestioneCORS_returnCode_ricezioneBuste;
	}
	
	private static Boolean isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneBuste = null;
	public boolean isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneBuste() {	
		if(OpenSPCoop2Properties.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneBuste==null){
			String pName = "org.openspcoop2.pdd.services.ricezioneBuste.cors.resourceHttpMethodQualsiasi";
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneBuste = java.lang.Boolean.valueOf(name);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+false);
					OpenSPCoop2Properties.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneBuste = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+false+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneBuste = false;
			}    
		}

		return OpenSPCoop2Properties.isGestioneCORS_resourceHttpMethodQualsiasi_ricezioneBuste;
	}
	


	/* ***************** DATE ************* */
	/**
	 * Restituisce il tipo di tempo da utilizzare
	 *
	 * @return il tipo di tempo da utilizzare
	 * 
	 */
	private static TipoOraRegistrazione tipoTempo = null;
	public TipoOraRegistrazione getTipoTempoBusta(String implementazionePdDSoggetto) {
		
		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getBusta_TempoTipo(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TEMPO_TIPO_LOCALE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.TEMPO_TIPO_SINCRONIZZATO.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TEMPO_TIPO_LOCALE.equalsIgnoreCase(tipo))
					return TipoOraRegistrazione.LOCALE;
				else 
					return TipoOraRegistrazione.SINCRONIZZATO;
			}
		}

		if(OpenSPCoop2Properties.tipoTempo==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.tempo.tipo");
				if(name!=null){
					name = name.trim();
					if(CostantiConfigurazione.TEMPO_TIPO_LOCALE.equals(name))
						OpenSPCoop2Properties.tipoTempo = TipoOraRegistrazione.LOCALE;
					else if(CostantiConfigurazione.TEMPO_TIPO_SINCRONIZZATO.equals(name))
						OpenSPCoop2Properties.tipoTempo = TipoOraRegistrazione.SINCRONIZZATO;
					else
						throw new Exception("Tipo "+name+" non conosciuto");
				}
				else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.tempo.tipo' non impostata, viene utilizzato il default="+CostantiConfigurazione.TEMPO_TIPO_LOCALE);
					OpenSPCoop2Properties.tipoTempo = TipoOraRegistrazione.LOCALE;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.tempo.tipo' non impostata, viene utilizzato il default="+CostantiConfigurazione.TEMPO_TIPO_LOCALE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.tipoTempo = TipoOraRegistrazione.LOCALE;
			}    
		}

		return OpenSPCoop2Properties.tipoTempo;
	}

	/**
	 * Restituisce il tipo di Date da utilizzare
	 *
	 * @return il tipo di Date da utilizzare
	 * 
	 */
	private static String tipoDateManager = null;
	public String getTipoDateManager() {	
		if(OpenSPCoop2Properties.tipoDateManager==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.date.tipo");
				if(name!=null)
					name = name.trim();
				else
					throw new Exception("non definita");
				OpenSPCoop2Properties.tipoDateManager = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.date.tipo': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoDateManager = null;
			}    
		}

		return OpenSPCoop2Properties.tipoDateManager;
	}
	
	private static DateEngineType tipoDateTimeFormat = null;
	public DateEngineType getTipoDateTimeFormat() {	
		if(OpenSPCoop2Properties.tipoDateTimeFormat==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.dateTimeFormat");
				if(name!=null)
					name = name.trim();
				else
					throw new Exception("non definita");
				OpenSPCoop2Properties.tipoDateTimeFormat = DateEngineType.valueOf(name);
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.dateTimeFormat': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoDateTimeFormat = null;
			}    
		}

		return OpenSPCoop2Properties.tipoDateTimeFormat;
	}

	/**
	 * Restituisce le proprieta' da utilizzare sul tipo di Date da utilizzare
	 *
	 * @return proprieta' da utilizzare sul tipo di Date da utilizzare
	 * 
	 */
	private static java.util.Properties dateManagerProperties = null;
	public java.util.Properties getDateManagerProperties() {
		if(OpenSPCoop2Properties.dateManagerProperties==null){
			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.date.property.");
				OpenSPCoop2Properties.dateManagerProperties = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle propriete' 'org.openspcoop2.pdd.date.property.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.dateManagerProperties = null;
			}    
		}

		return OpenSPCoop2Properties.dateManagerProperties;
	}








	/* ************** INTEGRATION MANAGER ****************** */

	private static Boolean integrationManager_isNomePortaDelegataUrlBasedValue = null;
	public boolean integrationManager_isNomePortaDelegataUrlBased() {
		if(OpenSPCoop2Properties.integrationManager_isNomePortaDelegataUrlBasedValue==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.service.IntegrationManager.nomePortaDelegataUrlBased"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.integrationManager_isNomePortaDelegataUrlBasedValue = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.service.IntegrationManager.nomePortaDelegataUrlBased' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.integrationManager_isNomePortaDelegataUrlBasedValue = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.service.IntegrationManager.nomePortaDelegataUrlBased' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.integrationManager_isNomePortaDelegataUrlBasedValue = false;
			}
		}

		return OpenSPCoop2Properties.integrationManager_isNomePortaDelegataUrlBasedValue;
	}

	private static Boolean integrationManager_readInformazioniTrasportoValue = null;
	public boolean integrationManager_readInformazioniTrasporto() {
		if(OpenSPCoop2Properties.integrationManager_readInformazioniTrasportoValue==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.service.IntegrationManager.infoTrasporto"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.integrationManager_readInformazioniTrasportoValue = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.service.IntegrationManager.infoTrasporto' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.integrationManager_readInformazioniTrasportoValue = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.service.IntegrationManager.infoTrasporto' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.integrationManager_readInformazioniTrasportoValue = false;
			}
		}

		return OpenSPCoop2Properties.integrationManager_readInformazioniTrasportoValue;
	}









	/* ************** GESTIONE ATTACHMENTS *************** */
	/**
	 * Restituisce l'indicazione se cancellare l'istruzione <?xml
	 *
	 * @return Restituisce Restituisce l'indicazione se cancellare l'istruzione <?xml
	 * 
	 */
	private static Boolean isDeleteInstructionTargetMachineXml= null;
	public boolean isDeleteInstructionTargetMachineXml(){
		if(OpenSPCoop2Properties.isDeleteInstructionTargetMachineXml==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.soap.deleteInstructionTargetMachineXml"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isDeleteInstructionTargetMachineXml = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.deleteInstructionTargetMachineXml' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isDeleteInstructionTargetMachineXml = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.deleteInstructionTargetMachineXml' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isDeleteInstructionTargetMachineXml = false;
			}
		}

		return OpenSPCoop2Properties.isDeleteInstructionTargetMachineXml;
	}

	private static Boolean tunnelSOAP_loadMailcap = null;
	public boolean isTunnelSOAP_loadMailcap() {	
		if(OpenSPCoop2Properties.tunnelSOAP_loadMailcap==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.soap.tunnelSOAP.mailcap.load");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.tunnelSOAP_loadMailcap = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.mailcap.load' non impostata, viene utilizzato il default="+false);
					OpenSPCoop2Properties.tunnelSOAP_loadMailcap = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.mailcap.load' non impostata, viene utilizzato il default="+false+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.tunnelSOAP_loadMailcap = false;
			}    
		}

		return OpenSPCoop2Properties.tunnelSOAP_loadMailcap;
	}
	
	/**
	 * Restituisce il tipo di Date da utilizzare
	 *
	 * @return il tipo di Date da utilizzare
	 * 
	 */
	private static String tunnelSOAPKeyWord_headerTrasporto = null;
	public String getTunnelSOAPKeyWord_headerTrasporto() {	
		if(OpenSPCoop2Properties.tunnelSOAPKeyWord_headerTrasporto==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.soap.tunnelSOAP.trasporto");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.tunnelSOAPKeyWord_headerTrasporto = name;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.trasporto' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_ATTACHMENT);
					OpenSPCoop2Properties.tunnelSOAPKeyWord_headerTrasporto = CostantiPdD.IMBUSTAMENTO_ATTACHMENT;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.trasporto' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_ATTACHMENT+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.tunnelSOAPKeyWord_headerTrasporto = CostantiPdD.IMBUSTAMENTO_ATTACHMENT;
			}    
		}

		return OpenSPCoop2Properties.tunnelSOAPKeyWord_headerTrasporto;
	}

	/**
	 * Restituisce il tipo di Date da utilizzare
	 *
	 * @return il tipo di Date da utilizzare
	 * 
	 */
	private static String tunnelSOAPKeyWordMimeType_headerTrasporto = null;
	public String getTunnelSOAPKeyWordMimeType_headerTrasporto() {	
		if(OpenSPCoop2Properties.tunnelSOAPKeyWordMimeType_headerTrasporto==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.soap.tunnelSOAP.mimeType.trasporto");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.tunnelSOAPKeyWordMimeType_headerTrasporto = name;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.mimeType.trasporto' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_MIME_TYPE);
					OpenSPCoop2Properties.tunnelSOAPKeyWordMimeType_headerTrasporto = CostantiPdD.IMBUSTAMENTO_MIME_TYPE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.mimeType.trasporto' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_MIME_TYPE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.tunnelSOAPKeyWordMimeType_headerTrasporto = CostantiPdD.IMBUSTAMENTO_MIME_TYPE;
			}    
		}

		return OpenSPCoop2Properties.tunnelSOAPKeyWordMimeType_headerTrasporto;
	}

	/**
	 * Restituisce il tipo di Date da utilizzare
	 *
	 * @return il tipo di Date da utilizzare
	 * 
	 */
	private static String tunnelSOAPKeyWord_urlBased = null;
	public String getTunnelSOAPKeyWord_urlBased() {	
		if(OpenSPCoop2Properties.tunnelSOAPKeyWord_urlBased==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.soap.tunnelSOAP.urlBased");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.tunnelSOAPKeyWord_urlBased = name;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.urlBased' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_ATTACHMENT);
					OpenSPCoop2Properties.tunnelSOAPKeyWord_urlBased = CostantiPdD.IMBUSTAMENTO_ATTACHMENT;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.urlBased' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_ATTACHMENT+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.tunnelSOAPKeyWord_urlBased = CostantiPdD.IMBUSTAMENTO_ATTACHMENT;
			}    
		}

		return OpenSPCoop2Properties.tunnelSOAPKeyWord_urlBased;
	}

	/**
	 * Restituisce il tipo di Date da utilizzare
	 *
	 * @return il tipo di Date da utilizzare
	 * 
	 */
	private static String tunnelSOAPKeyWordMimeType_urlBased = null;
	public String getTunnelSOAPKeyWordMimeType_urlBased() {	
		if(OpenSPCoop2Properties.tunnelSOAPKeyWordMimeType_urlBased==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.soap.tunnelSOAP.mimeType.urlBased");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.tunnelSOAPKeyWordMimeType_urlBased = name;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.mimeType.urlBased' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_MIME_TYPE);
					OpenSPCoop2Properties.tunnelSOAPKeyWordMimeType_urlBased = CostantiPdD.IMBUSTAMENTO_MIME_TYPE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.mimeType.urlBased' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_MIME_TYPE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.tunnelSOAPKeyWordMimeType_urlBased = CostantiPdD.IMBUSTAMENTO_MIME_TYPE;
			}    
		}

		return OpenSPCoop2Properties.tunnelSOAPKeyWordMimeType_urlBased;
	}
	



	/* ***************** ASINCRONI ************/
	/**
	 * Restituisce il Timeout di attesa di una busta di richiesta/ricevutaRichiesta asincrona non completamente processata 
	 *
	 * @return Restituisce il Timeout di attesa di una busta
	 * 
	 */
	private static Long timeoutBustaRispostaAsincrona = null;
	public long getTimeoutBustaRispostaAsincrona() {	
		if(OpenSPCoop2Properties.timeoutBustaRispostaAsincrona == null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.messaggioAsincronoInProcessamento.attesaAttiva");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.timeoutBustaRispostaAsincrona = java.lang.Long.parseLong(name) * 1000;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioAsincronoInProcessamento.attesaAttiva' non impostata, viene utilizzato il default="+(CostantiPdD.RISPOSTA_ASINCRONA_ATTESA_ATTIVA/1000));
					OpenSPCoop2Properties.timeoutBustaRispostaAsincrona = CostantiPdD.RISPOSTA_ASINCRONA_ATTESA_ATTIVA;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioAsincronoInProcessamento.attesaAttiva' non impostata" +
						", viene utilizzato il default="+(CostantiPdD.RISPOSTA_ASINCRONA_ATTESA_ATTIVA/1000)+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.timeoutBustaRispostaAsincrona = CostantiPdD.RISPOSTA_ASINCRONA_ATTESA_ATTIVA;
			}    
		}

		return OpenSPCoop2Properties.timeoutBustaRispostaAsincrona;
	} 
	/**
	 * Restituisce la Frequenza di check di attesa di una busta di richiesta/ricevutaRichiesta asincrona non completamente processata 
	 *
	 * @return Restituisce la Frequenza di check di attesa di una busta di richiesta/ricevutaRichiesta asincrona non completamente processata 
	 * 
	 */
	private static Integer checkIntervalBustaRispostaAsincrona = null;
	public int getCheckIntervalBustaRispostaAsincrona() {	
		if(OpenSPCoop2Properties.checkIntervalBustaRispostaAsincrona==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.repository.messaggioAsincronoInProcessamento.check");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.checkIntervalBustaRispostaAsincrona = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioAsincronoInProcessamento.check' non impostata, viene utilizzato il default="+CostantiPdD.RISPOSTA_ASINCRONA_CHECK_INTERVAL);
					OpenSPCoop2Properties.checkIntervalBustaRispostaAsincrona = CostantiPdD.RISPOSTA_ASINCRONA_CHECK_INTERVAL;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioAsincronoInProcessamento.check' non impostata, viene utilizzato il default="+CostantiPdD.RISPOSTA_ASINCRONA_CHECK_INTERVAL+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.checkIntervalBustaRispostaAsincrona = CostantiPdD.RISPOSTA_ASINCRONA_CHECK_INTERVAL;
			}    
		}

		return OpenSPCoop2Properties.checkIntervalBustaRispostaAsincrona;
	}




	/*---------- Cluster ID -------------*/

	/**
	 * Restituisce il cluster id del nodo del cluster su cui gira la pdd 
	 *
	 * @return Il cluster id di questo nodo
	 * @added Fabio Tronci (tronci@link.it) 06/06/08 
	 */
	private static String cluster_id = null;
	public String getClusterId(boolean required) {
		if(OpenSPCoop2Properties.cluster_id==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.cluster_id");
				if(name==null && required)
					throw new Exception("non definita");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.cluster_id = name;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.cluster_id': "+e.getMessage(),e);
				OpenSPCoop2Properties.cluster_id = null;
			}  
		}

		return OpenSPCoop2Properties.cluster_id;
	}
	
	private static String cluster_id_numerico =null;
	private static Boolean cluster_id_numerico_read =null;
	public String getClusterIdNumerico() throws ProtocolException {
		if(OpenSPCoop2Properties.cluster_id_numerico_read==null){
			try{ 
				String name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.cluster_id.numeric");
				if(name!=null){
					int numero = Integer.parseInt(name.trim());
					if(numero<0 || numero>99){
						String msg = "Riscontrato errore durante la lettura della proprietà 'org.openspcoop2.pdd.cluster_id.numeric': il valore indicato ("+numero+") deve essere compreso tra 0 e 99";
						this.log.error(msg);
						throw new ProtocolException(msg);
					}
					OpenSPCoop2Properties.cluster_id_numerico = name.trim();
				}
				
				OpenSPCoop2Properties.cluster_id_numerico_read = true;
				
			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.cluster_id.numeric': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}   
		}
		return OpenSPCoop2Properties.cluster_id_numerico;
	}

	private static Boolean isTimerLockByDatabase = null;
	public boolean isTimerLockByDatabase(){
		if(OpenSPCoop2Properties.isTimerLockByDatabase==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.lockDatabase"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerLockByDatabase = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.lockDatabase' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isTimerLockByDatabase = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.lockDatabase' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerLockByDatabase = true;
			}
			
			if(OpenSPCoop2Properties.isTimerLockByDatabase) {
				// Richiede la definizione di un clusterId e di un tipo di database
				if(this.getClusterId(false)==null) {
					OpenSPCoop2Properties.isTimerLockByDatabase = false;
				}
				else if(this.getDatabaseType()==null) {
					OpenSPCoop2Properties.isTimerLockByDatabase = false;
				}
			}
		}
		return OpenSPCoop2Properties.isTimerLockByDatabase;
	}
	
	private static Boolean isTimerLockByDatabaseNotifyLogEnabled = null;
	public boolean isTimerLockByDatabaseNotifyLogEnabled(){
		if(OpenSPCoop2Properties.isTimerLockByDatabaseNotifyLogEnabled==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.lockDatabase.notify.log"); 

				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerLockByDatabaseNotifyLogEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.lockDatabase.notify.log' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isTimerLockByDatabaseNotifyLogEnabled = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.lockDatabase.notify.log' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTimerLockByDatabaseNotifyLogEnabled = true;
			}
		}
		return OpenSPCoop2Properties.isTimerLockByDatabaseNotifyLogEnabled;
	}
	
	private static String pddContextSerializer = null;
	public String getPddContextSerializer() {
		if(OpenSPCoop2Properties.pddContextSerializer==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.contextSerializer");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.pddContextSerializer = name;
				}else{
					OpenSPCoop2Properties.pddContextSerializer = CostantiConfigurazione.NONE;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.contextSerializer': "+e.getMessage(),e);
				OpenSPCoop2Properties.pddContextSerializer = CostantiConfigurazione.NONE;
			}  
		}

		return OpenSPCoop2Properties.pddContextSerializer;
	}




	/*---------- Stateless -------------*/

	/**
	 * Restituisce il comportamento di default (Stateless abilitato/disabilitato) per il profilo oneway
	 *
	 * @return il Restituisce il comportamento di default (Stateless abilitato/disabilitato) per il profilo oneway
	 * 
	 */
	private static String statelessOneWay = null;
	public String getStatelessOneWay() {	
		if(OpenSPCoop2Properties.statelessOneWay==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.stateless.default.oneway");
				if(name!=null){
					name = name.trim();
					if( (CostantiConfigurazione.ABILITATO.equals(name)==false) && (CostantiConfigurazione.DISABILITATO.equals(name)==false) ){
						throw new Exception("Valori ammessi sono abilitato/disabilito");
					}
				}
				else{
					throw new Exception("non definita");
				}
				OpenSPCoop2Properties.statelessOneWay = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.default.oneway': "+e.getMessage(),e);
				OpenSPCoop2Properties.statelessOneWay = null;
			}    
		}

		return OpenSPCoop2Properties.statelessOneWay;
	}

	/**
	 * Restituisce il comportamento di default (Stateless abilitato/disabilitato) per il profilo sincrono
	 *
	 * @return il Restituisce il comportamento di default (Stateless abilitato/disabilitato) per il profilo sincrono
	 * 
	 */
	private static String statelessSincrono = null;
	public String getStatelessSincrono() {
		if(OpenSPCoop2Properties.statelessSincrono==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.stateless.default.sincrono");
				if(name!=null){
					name = name.trim();
					if( (CostantiConfigurazione.ABILITATO.equals(name)==false) && (CostantiConfigurazione.DISABILITATO.equals(name)==false) ){
						throw new Exception("Valori ammessi sono abilitato/disabilito");
					}
				}
				else{
					throw new Exception("non definita");
				}
				OpenSPCoop2Properties.statelessSincrono = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.default.sincrono': "+e.getMessage(),e);
				OpenSPCoop2Properties.statelessSincrono = null;
			}    
		}
		//System.out.println("MODALITA default per sincrono: "+OpenSPCoopProperties.statelessSincrono);
		return OpenSPCoop2Properties.statelessSincrono;
	}

	/**
	 * Restituisce il comportamento di default (Stateless abilitato/disabilitato) per il profilo asincrono simmetrico e asimmetrico
	 *
	 * @return il Restituisce il comportamento di default (Stateless abilitato/disabilitato) per il profilo asincrono simmetrico e asimmetrico
	 * 
	 */
	private static String statelessAsincrono = null;
	public String getStatelessAsincroni() {	
		if(OpenSPCoop2Properties.statelessAsincrono==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.stateless.default.asincroni");
				if(name!=null){
					name = name.trim();
					if( (CostantiConfigurazione.ABILITATO.equals(name)==false) && (CostantiConfigurazione.DISABILITATO.equals(name)==false) ){
						throw new Exception("Valori ammessi sono abilitato/disabilito");
					}
				}
				else{
					throw new Exception("non definita");
				}
				OpenSPCoop2Properties.statelessAsincrono = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.default.asincroni': "+e.getMessage(),e);
				OpenSPCoop2Properties.statelessAsincrono = null;
			}    
		}
		//System.out.println("MODALITA default per asincroni: "+OpenSPCoopProperties.statelessAsincrono);
		return OpenSPCoop2Properties.statelessAsincrono;
	}

	/**
	 * Restituisce l'indicazione se gestire il oneway non stateless nella nuova modalita della versione 1.4 o come la vecchia 1.0
	 *
	 * @return Restituisce l'indicazione se gestire il oneway non stateless nella nuova modalita della versione 1.4 o come la vecchia 1.0
	 * 
	 */
	private static Boolean isGestioneOnewayStateful_1_1= null;
	public boolean isGestioneOnewayStateful_1_1(){
		if(OpenSPCoop2Properties.isGestioneOnewayStateful_1_1==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.stateful.oneway"); 

				if(value!=null){
					value = value.trim();
					if( (CostantiConfigurazione.ONEWAY_STATEFUL_1_0.equals(value)==false) && (CostantiConfigurazione.ONEWAY_STATEFUL_1_1.equals(value)==false) ){
						throw new Exception("Valori ammessi sono 1.0/1.1");
					}
					OpenSPCoop2Properties.isGestioneOnewayStateful_1_1 = CostantiConfigurazione.ONEWAY_STATEFUL_1_1.equals(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.stateful.oneway' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGestioneOnewayStateful_1_1 = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.stateful.oneway' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestioneOnewayStateful_1_1 = true;
			}
		}
		//System.out.println("MODALITA 11 per gestione Oneway; "+OpenSPCoopProperties.isGestioneOnewayStateful_1_1);
		return OpenSPCoop2Properties.isGestioneOnewayStateful_1_1;
	}

	/**
	 * Restituisce il comportamento per il routing
	 *
	 * @return il Restituisce il comportamento per il routing
	 * 
	 */
	private static String statelessRouting = null;
	public String getStatelessRouting() {	
		if(OpenSPCoop2Properties.statelessRouting==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.stateless.router");
				if(name!=null){
					name = name.trim();
					if( (CostantiConfigurazione.ABILITATO.equals(name)==false) && (CostantiConfigurazione.DISABILITATO.equals(name)==false) ){
						throw new Exception("Valori ammessi sono abilitato/disabilito");
					}
				}
				else{
					throw new Exception("non definita");
				}
				OpenSPCoop2Properties.statelessRouting = name;
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.router': "+e.getMessage(),e);
				OpenSPCoop2Properties.statelessRouting = null;
			}    
		}
		//System.out.println("MODALITA per routing: "+OpenSPCoopProperties.statelessRouting);
		return OpenSPCoop2Properties.statelessRouting;
	}


	/**
	 * Restituisce l'indicazione se una gestione stateless deve rinegoziare la connessione
	 *
	 * @return Restituisce l'indicazione se una gestione stateless deve rinegoziare la connessione
	 * 
	 */
	private static Boolean isGestioneStateful_RinegoziamentoConnessione= null;
	private boolean isRinegoziamentoConnessione(){
		if(OpenSPCoop2Properties.isGestioneStateful_RinegoziamentoConnessione==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.stateless.dataSource.rinegoziamentoConnessione"); 

				if(value!=null){
					value = value.trim();
					if( (CostantiConfigurazione.ABILITATO.equals(value)==false) && (CostantiConfigurazione.DISABILITATO.equals(value)==false) ){
						throw new Exception("Valori ammessi sono abilitato/disabilitato");
					}
					if( CostantiConfigurazione.DISABILITATO.equals(value) ){
						OpenSPCoop2Properties.isGestioneStateful_RinegoziamentoConnessione = false;
					}else{
						OpenSPCoop2Properties.isGestioneStateful_RinegoziamentoConnessione = true;
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.dataSource.rinegoziamentoConnessione' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGestioneStateful_RinegoziamentoConnessione = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.dataSource.rinegoziamentoConnessione' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestioneStateful_RinegoziamentoConnessione = true;
			}
		}
		return OpenSPCoop2Properties.isGestioneStateful_RinegoziamentoConnessione;
	}
	
	public boolean isRinegoziamentoConnessione(ProfiloDiCollaborazione profilo){
		if(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(profilo) || ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(profilo))
			return true;
		else
			return this.isRinegoziamentoConnessione();
	}




	private static Boolean mergeHandlerBuiltInAndHandlerUser = null;
	public boolean isMergeHandlerBuiltInAndHandlerUser() {	
		if(OpenSPCoop2Properties.mergeHandlerBuiltInAndHandlerUser==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.merge");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.mergeHandlerBuiltInAndHandlerUser = Boolean.parseBoolean(v);
				} 
				else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.handler.merge' non impostata, viene utilizzato il default="+false);
					OpenSPCoop2Properties.mergeHandlerBuiltInAndHandlerUser = false;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.handler.merge' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		return OpenSPCoop2Properties.mergeHandlerBuiltInAndHandlerUser;
	}
	
	/*---------- Gestori handler built-in -------------*/
	
	private static Boolean printInfoHandlerBuiltIn = null;
	public boolean isPrintInfoHandlerBuiltIn() {	
		if(OpenSPCoop2Properties.printInfoHandlerBuiltIn==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.printInfo");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.printInfoHandlerBuiltIn = Boolean.parseBoolean(v);
				} 
				else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.handler.built-in.printInfo' non impostata, viene utilizzato il default="+true);
					OpenSPCoop2Properties.printInfoHandlerBuiltIn = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.handler.built-in.printInfo' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		return OpenSPCoop2Properties.printInfoHandlerBuiltIn;
	}
	
	
	/**
	 * Restituisce l'elenco degli handlers di tipo InitHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo InitHandlerBuiltIn
	 */
	private static String[] tipiInitHandlerBuiltIn = null;
	private static boolean tipiInitHandlerBuiltInRead = false;
	public String[] getInitHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiInitHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.init");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiInitHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiInitHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.init': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiInitHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiInitHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiInitHandlerBuiltIn;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo ExitHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo ExitHandlerBuiltIn
	 */
	private static String[] tipiExitHandlerBuiltIn = null;
	private static boolean tipiExitHandlerBuiltInRead = false;
	public String[] getExitHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiExitHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.exit");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiExitHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiExitHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.exit': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiExitHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiExitHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiExitHandlerBuiltIn;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo PreInRequestHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo PreInRequestHandlerBuiltIn
	 */
	private static String[] tipiPreInRequestHandlerBuiltIn = null;
	private static boolean tipiPreInRequestHandlerBuiltInRead = false;
	public String[] getPreInRequestHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiPreInRequestHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.pre-in-request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiPreInRequestHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiPreInRequestHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.pre-in-request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiPreInRequestHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiPreInRequestHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiPreInRequestHandlerBuiltIn;
	}

	
	/**
	 * Restituisce l'elenco degli handlers di tipo InRequestHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo InRequestHandlerBuiltIn
	 */
	private static String[] tipiInRequestHandlerBuiltIn = null;
	private static boolean tipiInRequestHandlerBuiltInRead = false;
	public String[] getInRequestHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiInRequestHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.in-request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiInRequestHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiInRequestHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.in-request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiInRequestHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiInRequestHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiInRequestHandlerBuiltIn;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo InRequestProtocolHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo InRequestProtocolHandlerBuiltIn
	 */
	private static String[] tipiInRequestProtocolHandlerBuiltIn = null;
	private static boolean tipiInRequestProtocolHandlerBuiltInRead = false;
	public String[] getInRequestProtocolHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiInRequestProtocolHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.in-protocol-request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiInRequestProtocolHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiInRequestProtocolHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.in-protocol-request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiInRequestProtocolHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiInRequestProtocolHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiInRequestProtocolHandlerBuiltIn;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo OutRequestHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo OutRequestHandlerBuiltIn
	 */
	private static String[] tipiOutRequestHandlerBuiltIn = null;
	private static boolean tipiOutRequestHandlerBuiltInRead = false;
	public String[] getOutRequestHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiOutRequestHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.out-request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiOutRequestHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiOutRequestHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.out-request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiOutRequestHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiOutRequestHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiOutRequestHandlerBuiltIn;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo PostOutRequestHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo PostOutRequestHandlerBuiltIn
	 */
	private static String[] tipiPostOutRequestHandlerBuiltIn = null;
	private static boolean tipiPostOutRequestHandlerBuiltInRead = false;
	public String[] getPostOutRequestHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiPostOutRequestHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.post-out-request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiPostOutRequestHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiPostOutRequestHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.post-out-request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiPostOutRequestHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiPostOutRequestHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiPostOutRequestHandlerBuiltIn;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo PreInResponseHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo PreInResponseHandlerBuiltIn
	 */
	private static String[] tipiPreInResponseHandlerBuiltIn = null;
	private static boolean tipiPreInResponseHandlerBuiltInRead = false;
	public String[] getPreInResponseHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiPreInResponseHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.pre-in-response");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiPreInResponseHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiPreInResponseHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.pre-in-response': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiPreInResponseHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiPreInResponseHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiPreInResponseHandlerBuiltIn;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo InResponseHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo InResponseHandlerBuiltIn
	 */
	private static String[] tipiInResponseHandlerBuiltIn = null;
	private static boolean tipiInResponseHandlerBuiltInRead = false;
	public String[] getInResponseHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiInResponseHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.in-response");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiInResponseHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiInResponseHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.in-response': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiInResponseHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiInResponseHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiInResponseHandlerBuiltIn;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo OutResponseHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo OutResponseHandlerBuiltIn
	 */
	private static String[] tipiOutResponseHandlerBuiltIn = null;
	private static boolean tipiOutResponseHandlerBuiltInRead = false;
	public String[] getOutResponseHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiOutResponseHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.out-response");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiOutResponseHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiOutResponseHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.out-response': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiOutResponseHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiOutResponseHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiOutResponseHandlerBuiltIn;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo PostOutResponseHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo PostOutResponseHandlerBuiltIn
	 */
	private static String[] tipiPostOutResponseHandlerBuiltIn = null;
	private static boolean tipiPostOutResponseHandlerBuiltInRead = false;
	public String[] getPostOutResponseHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiPostOutResponseHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.built-in.post-out-response");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiPostOutResponseHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiPostOutResponseHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.built-in.post-out-response': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiPostOutResponseHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiPostOutResponseHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiPostOutResponseHandlerBuiltIn;
	}

	/**
	 * Restituisce l'elenco degli handlers di tipo IntegrationManagerRequestHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo IntegrationManagerRequestHandlerBuiltIn
	 */
	private static String[] tipiIntegrationManagerRequestHandlerBuiltIn = null;
	private static boolean tipiIntegrationManagerRequestHandlerBuiltInRead = false;
	public String[] getIntegrationManagerRequestHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiIntegrationManagerRequestHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrationManager.handler.built-in.request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiIntegrationManagerRequestHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiIntegrationManagerRequestHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.integrationManager.handler.built-in.request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiIntegrationManagerRequestHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiIntegrationManagerRequestHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiIntegrationManagerRequestHandlerBuiltIn;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo IntegrationManagerResponseHandlerBuiltIn
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo IntegrationManagerResponseHandlerBuiltIn
	 */
	private static String[] tipiIntegrationManagerResponseHandlerBuiltIn = null;
	private static boolean tipiIntegrationManagerResponseHandlerBuiltInRead = false;
	public String[] getIntegrationManagerResponseHandlerBuiltIn() {
		if(OpenSPCoop2Properties.tipiIntegrationManagerResponseHandlerBuiltInRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrationManager.handler.built-in.response");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiIntegrationManagerResponseHandlerBuiltIn = r;
				}else{
					OpenSPCoop2Properties.tipiIntegrationManagerResponseHandlerBuiltIn = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.integrationManager.handler.built-in.response': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiIntegrationManagerResponseHandlerBuiltIn = null;
			}   
			OpenSPCoop2Properties.tipiIntegrationManagerResponseHandlerBuiltInRead = true;
		}

		return OpenSPCoop2Properties.tipiIntegrationManagerResponseHandlerBuiltIn;
	}
	
	
	
	
	

	/*---------- Gestori handler -------------*/
	
	private static Boolean printInfoHandler = null;
	public boolean isPrintInfoHandler() {	
		if(OpenSPCoop2Properties.printInfoHandler==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.printInfo");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.printInfoHandler = Boolean.parseBoolean(v);
				} 
				else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.handler.printInfo' non impostata, viene utilizzato il default="+true);
					OpenSPCoop2Properties.printInfoHandler = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.handler.printInfo' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		return OpenSPCoop2Properties.printInfoHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo InitHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo InitHandler
	 */
	private static String[] tipiInitHandler = null;
	private static boolean tipiInitHandlerRead = false;
	public String[] getInitHandler() {
		if(OpenSPCoop2Properties.tipiInitHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.init");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiInitHandler = r;
				}else{
					OpenSPCoop2Properties.tipiInitHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.init': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiInitHandler = null;
			}   
			OpenSPCoop2Properties.tipiInitHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiInitHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo ExitHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo ExitHandler
	 */
	private static String[] tipiExitHandler = null;
	private static boolean tipiExitHandlerRead = false;
	public String[] getExitHandler() {
		if(OpenSPCoop2Properties.tipiExitHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.exit");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiExitHandler = r;
				}else{
					OpenSPCoop2Properties.tipiExitHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.exit': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiExitHandler = null;
			}   
			OpenSPCoop2Properties.tipiExitHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiExitHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo PreInRequestHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo PreInRequestHandler
	 */
	private static String[] tipiPreInRequestHandler = null;
	private static boolean tipiPreInRequestHandlerRead = false;
	public String[] getPreInRequestHandler() {
		if(OpenSPCoop2Properties.tipiPreInRequestHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.pre-in-request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiPreInRequestHandler = r;
				}else{
					OpenSPCoop2Properties.tipiPreInRequestHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.pre-in-request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiPreInRequestHandler = null;
			}   
			OpenSPCoop2Properties.tipiPreInRequestHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiPreInRequestHandler;
	}

	
	/**
	 * Restituisce l'elenco degli handlers di tipo InRequestHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo InRequestHandler
	 */
	private static String[] tipiInRequestHandler = null;
	private static boolean tipiInRequestHandlerRead = false;
	public String[] getInRequestHandler() {
		if(OpenSPCoop2Properties.tipiInRequestHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.in-request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiInRequestHandler = r;
				}else{
					OpenSPCoop2Properties.tipiInRequestHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.in-request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiInRequestHandler = null;
			}   
			OpenSPCoop2Properties.tipiInRequestHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiInRequestHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo InRequestProtocolHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo InRequestProtocolHandler
	 */
	private static String[] tipiInRequestProtocolHandler = null;
	private static boolean tipiInRequestProtocolHandlerRead = false;
	public String[] getInRequestProtocolHandler() {
		if(OpenSPCoop2Properties.tipiInRequestProtocolHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.in-protocol-request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiInRequestProtocolHandler = r;
				}else{
					OpenSPCoop2Properties.tipiInRequestProtocolHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.in-protocol-request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiInRequestProtocolHandler = null;
			}   
			OpenSPCoop2Properties.tipiInRequestProtocolHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiInRequestProtocolHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo OutRequestHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo OutRequestHandler
	 */
	private static String[] tipiOutRequestHandler = null;
	private static boolean tipiOutRequestHandlerRead = false;
	public String[] getOutRequestHandler() {
		if(OpenSPCoop2Properties.tipiOutRequestHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.out-request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiOutRequestHandler = r;
				}else{
					OpenSPCoop2Properties.tipiOutRequestHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.out-request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiOutRequestHandler = null;
			}   
			OpenSPCoop2Properties.tipiOutRequestHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiOutRequestHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo PostOutRequestHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo PostOutRequestHandler
	 */
	private static String[] tipiPostOutRequestHandler = null;
	private static boolean tipiPostOutRequestHandlerRead = false;
	public String[] getPostOutRequestHandler() {
		if(OpenSPCoop2Properties.tipiPostOutRequestHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.post-out-request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiPostOutRequestHandler = r;
				}else{
					OpenSPCoop2Properties.tipiPostOutRequestHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.post-out-request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiPostOutRequestHandler = null;
			}   
			OpenSPCoop2Properties.tipiPostOutRequestHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiPostOutRequestHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo PreInResponseHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo PreInResponseHandler
	 */
	private static String[] tipiPreInResponseHandler = null;
	private static boolean tipiPreInResponseHandlerRead = false;
	public String[] getPreInResponseHandler() {
		if(OpenSPCoop2Properties.tipiPreInResponseHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.pre-in-response");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiPreInResponseHandler = r;
				}else{
					OpenSPCoop2Properties.tipiPreInResponseHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.pre-in-response': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiPreInResponseHandler = null;
			}   
			OpenSPCoop2Properties.tipiPreInResponseHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiPreInResponseHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo InResponseHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo InResponseHandler
	 */
	private static String[] tipiInResponseHandler = null;
	private static boolean tipiInResponseHandlerRead = false;
	public String[] getInResponseHandler() {
		if(OpenSPCoop2Properties.tipiInResponseHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.in-response");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiInResponseHandler = r;
				}else{
					OpenSPCoop2Properties.tipiInResponseHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.in-response': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiInResponseHandler = null;
			}   
			OpenSPCoop2Properties.tipiInResponseHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiInResponseHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo OutResponseHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo OutResponseHandler
	 */
	private static String[] tipiOutResponseHandler = null;
	private static boolean tipiOutResponseHandlerRead = false;
	public String[] getOutResponseHandler() {
		if(OpenSPCoop2Properties.tipiOutResponseHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.out-response");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiOutResponseHandler = r;
				}else{
					OpenSPCoop2Properties.tipiOutResponseHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.out-response': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiOutResponseHandler = null;
			}   
			OpenSPCoop2Properties.tipiOutResponseHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiOutResponseHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo PostOutResponseHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo PostOutResponseHandler
	 */
	private static String[] tipiPostOutResponseHandler = null;
	private static boolean tipiPostOutResponseHandlerRead = false;
	public String[] getPostOutResponseHandler() {
		if(OpenSPCoop2Properties.tipiPostOutResponseHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.handler.post-out-response");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiPostOutResponseHandler = r;
				}else{
					OpenSPCoop2Properties.tipiPostOutResponseHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.post-out-response': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiPostOutResponseHandler = null;
			}   
			OpenSPCoop2Properties.tipiPostOutResponseHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiPostOutResponseHandler;
	}

	/**
	 * Restituisce l'elenco degli handlers di tipo IntegrationManagerRequestHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo IntegrationManagerRequestHandler
	 */
	private static String[] tipiIntegrationManagerRequestHandler = null;
	private static boolean tipiIntegrationManagerRequestHandlerRead = false;
	public String[] getIntegrationManagerRequestHandler() {
		if(OpenSPCoop2Properties.tipiIntegrationManagerRequestHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrationManager.handler.request");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiIntegrationManagerRequestHandler = r;
				}else{
					OpenSPCoop2Properties.tipiIntegrationManagerRequestHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.integrationManager.handler.request': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiIntegrationManagerRequestHandler = null;
			}   
			OpenSPCoop2Properties.tipiIntegrationManagerRequestHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiIntegrationManagerRequestHandler;
	}
	
	/**
	 * Restituisce l'elenco degli handlers di tipo IntegrationManagerResponseHandler
	 * 
	 * @return  Restituisce l'elenco degli handlers di tipo IntegrationManagerResponseHandler
	 */
	private static String[] tipiIntegrationManagerResponseHandler = null;
	private static boolean tipiIntegrationManagerResponseHandlerRead = false;
	public String[] getIntegrationManagerResponseHandler() {
		if(OpenSPCoop2Properties.tipiIntegrationManagerResponseHandlerRead == false){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrationManager.handler.response");
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipiIntegrationManagerResponseHandler = r;
				}else{
					OpenSPCoop2Properties.tipiIntegrationManagerResponseHandler = null;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.integrationManager.handler.response': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipiIntegrationManagerResponseHandler = null;
			}   
			OpenSPCoop2Properties.tipiIntegrationManagerResponseHandlerRead = true;
		}

		return OpenSPCoop2Properties.tipiIntegrationManagerResponseHandler;
	}









	/* ----------- MessageSecurity --------------------- */
	private static Boolean isLoadBouncyCastle = null;
	public boolean isLoadBouncyCastle(){

		if(OpenSPCoop2Properties.isLoadBouncyCastle==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.security.addBouncyCastleProvider"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isLoadBouncyCastle = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.security.addBouncyCastleProvider' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isLoadBouncyCastle = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.security.addBouncyCastleProvider' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isLoadBouncyCastle = true;
			}
		}

		return OpenSPCoop2Properties.isLoadBouncyCastle;
	}
	/**
	 * Indicazione se generare un actor di default
	 *   
	 * @return Indicazione se generare un actor di default
	 * 
	 */
	private static Boolean isGenerazioneActorDefault = null;
	public boolean isGenerazioneActorDefault(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String tipo = this.pddReader.getMessageSecurity_ActorDefaultEnable(implementazionePdDSoggetto);
			if(tipo!=null && ( 
					CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo) || 
					CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo)  ) 
			){
				if(CostantiConfigurazione.TRUE.equalsIgnoreCase(tipo))
					return true;
				else if(CostantiConfigurazione.FALSE.equalsIgnoreCase(tipo))
					return false;
			}
		}

		if(OpenSPCoop2Properties.isGenerazioneActorDefault==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.actorDefault.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isGenerazioneActorDefault = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.actorDefault.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isGenerazioneActorDefault = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.actorDefault.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneActorDefault = true;
			}
		}

		return OpenSPCoop2Properties.isGenerazioneActorDefault;
	}


	/**
	 * Actor di default
	 *   
	 * @return actor di default
	 * 
	 */
	private static String actorDefault = null;
	public String getActorDefault(String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		if(this.pddReader!=null){
			String valore = this.pddReader.getMessageSecurity_ActorDefaultValue(implementazionePdDSoggetto);
			if(valore!=null){
				return valore;
			}
		}

		if(OpenSPCoop2Properties.actorDefault==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.actorDefault.valore"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.actorDefault = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.actorDefault.valore' non impostata, viene utilizzato il default=openspcoop");
					OpenSPCoop2Properties.actorDefault = "openspcoop";
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.actorDefault.valore' non impostata, viene utilizzato il default=openspcoop, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.actorDefault = "openspcoop";
			}
		}

		return OpenSPCoop2Properties.actorDefault;
	}
	
	/**
	 * WsuId prefix associato agli id delle reference utilizzate dagli header di MessageSecurity
	 *   
	 * @return prefix
	 * 
	 */
	private static String prefixWsuId = null;
	public String getPrefixWsuId(){

		if(OpenSPCoop2Properties.prefixWsuId==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.prefixWsuId"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.prefixWsuId = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.prefixWsuId' non impostata, viene utilizzato il default di MessageSecurity");
					OpenSPCoop2Properties.prefixWsuId = "";
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.prefixWsuId' non impostata, viene utilizzato il default di MessageSecurity, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.prefixWsuId = "";
			}
		}

		return OpenSPCoop2Properties.prefixWsuId;
	}
	
	private static Boolean removeAllWsuIdRef = null;
	public boolean isRemoveAllWsuIdRef(){

		if(OpenSPCoop2Properties.removeAllWsuIdRef==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.removeAllWsuIdRef"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.removeAllWsuIdRef = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.removeAllWsuIdRef' non impostata, viene utilizzato il default 'false'");
					OpenSPCoop2Properties.removeAllWsuIdRef = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.removeAllWsuIdRef' non impostata, viene utilizzato il default 'false', errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.removeAllWsuIdRef = false;
			}
		}

		return OpenSPCoop2Properties.removeAllWsuIdRef;
	}
	
	private static String externalPWCallback = null;
	private static Boolean externalPWCallbackReaded = null;
	public String getExternalPWCallbackPropertyFile(){

		if(OpenSPCoop2Properties.externalPWCallbackReaded==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.externalPWCallback.propertiesFile"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.externalPWCallback = value;
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.externalPWCallback.propertiesFile' non impostata");
					OpenSPCoop2Properties.externalPWCallback = null;
				}
				OpenSPCoop2Properties.externalPWCallbackReaded = true;

			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.externalPWCallback.propertiesFile' non impostata, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.externalPWCallback = null;
				OpenSPCoop2Properties.externalPWCallbackReaded = true;
			}
		}

		return OpenSPCoop2Properties.externalPWCallback;
	}


	/**
	 * Restituisce l'indicazione se la cache messageSecurity e' abilitata
	 *
	 * @return Restituisce l'indicazione se la cache messageSecurity e' abilitata
	 */
	private static Boolean isAbilitataCacheMessageSecurityKeystore_value = null;
	public boolean isAbilitataCacheMessageSecurityKeystore() {
		if(OpenSPCoop2Properties.isAbilitataCacheMessageSecurityKeystore_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.keystore.cache.enable"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isAbilitataCacheMessageSecurityKeystore_value = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.keystore.cache.enable' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isAbilitataCacheMessageSecurityKeystore_value = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.keystore.cache.enable' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isAbilitataCacheMessageSecurityKeystore_value = false;
			}
		}

		return OpenSPCoop2Properties.isAbilitataCacheMessageSecurityKeystore_value;
	}

	/**
	 * Restituisce la dimensione della cache messageSecurity 
	 *
	 * @return Restituisce la dimensione della cache messageSecurity 
	 */
	private static Integer dimensioneCacheMessageSecurityKeystore_value = null;
	public int getDimensioneCacheMessageSecurityKeystore() throws OpenSPCoop2ConfigurationException{	
		if(OpenSPCoop2Properties.dimensioneCacheMessageSecurityKeystore_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.keystore.cache.dimensione"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.dimensioneCacheMessageSecurityKeystore_value = Integer.parseInt(value);
				}else{
					OpenSPCoop2Properties.dimensioneCacheMessageSecurityKeystore_value = -1;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.keystore.cache.dimensione': "+e.getMessage(),e);
				throw new OpenSPCoop2ConfigurationException("Riscontrato errore durante la lettura della proprieta' di openspcoop  'org.openspcoop2.pdd.messageSecurity.keystore.cache.dimensione'",e);
			}
		}

		return OpenSPCoop2Properties.dimensioneCacheMessageSecurityKeystore_value;
	}

	/**
	 * Restituisce la  itemLifeSecond della cache messageSecurity
	 *
	 * @return Restituisce la itemLifeSecond della cache messageSecurity
	 */
	private static Integer itemLifeSecondCacheMessageSecurityKeystore_value = null;
	public int getItemLifeSecondCacheMessageSecurityKeystore() throws OpenSPCoop2ConfigurationException{
		if(OpenSPCoop2Properties.itemLifeSecondCacheMessageSecurityKeystore_value==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.keystore.cache.itemLifeSecond"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.itemLifeSecondCacheMessageSecurityKeystore_value = Integer.parseInt(value);
				}else{
					OpenSPCoop2Properties.itemLifeSecondCacheMessageSecurityKeystore_value = -1;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.keystore.cache.itemLifeSecond': "+e.getMessage(),e);
				throw new OpenSPCoop2ConfigurationException("Riscontrato errore durante la lettura della proprieta' di openspcoop  'org.openspcoop2.pdd.messageSecurity.keystore.cache.itemLifeSecond'",e);
			}
		}

		return OpenSPCoop2Properties.itemLifeSecondCacheMessageSecurityKeystore_value;
	}
	
	
	
	
	
	
	/* ********  Gestore Credenziali  ******** */

	private static CryptConfig getCryptConfigAutenticazioneApplicativi = null;
	public CryptConfig getCryptConfigAutenticazioneApplicativi() {
		if(OpenSPCoop2Properties.getCryptConfigAutenticazioneApplicativi == null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.autenticazione.applicativi.password"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getCryptConfigAutenticazioneApplicativi = new CryptConfig(value);
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.core.autenticazione.applicativi.password': "+e.getMessage(),e);
				return null;
			}
		}

		return OpenSPCoop2Properties.getCryptConfigAutenticazioneApplicativi;
	}
	
	private static CryptConfig getCryptConfigAutenticazioneSoggetti = null;
	public CryptConfig getCryptConfigAutenticazioneSoggetti() {
		if(OpenSPCoop2Properties.getCryptConfigAutenticazioneSoggetti == null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.autenticazione.soggetti.password"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getCryptConfigAutenticazioneSoggetti = new CryptConfig(value);
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.core.autenticazione.soggetti.password': "+e.getMessage(),e);
				return null;
			}
		}

		return OpenSPCoop2Properties.getCryptConfigAutenticazioneSoggetti;
	}
	
	private static String getRealmAutenticazioneBasic = null;
	private static boolean getRealmAutenticazioneBasicRead = false;
	public String getRealmAutenticazioneBasic() {
		if(OpenSPCoop2Properties.getRealmAutenticazioneBasicRead == false){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.core.autenticazione.basic.realm"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.getRealmAutenticazioneBasic = value;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.core.autenticazione.basic.realm': "+e.getMessage(),e);
				OpenSPCoop2Properties.getRealmAutenticazioneBasic = null;
			}
			OpenSPCoop2Properties.getRealmAutenticazioneBasicRead = true;
		}

		return OpenSPCoop2Properties.getRealmAutenticazioneBasic;
	}
	
	/**
	 * Restituisce l'elenco dei tipi di gestori di credenziali da utilizzare lato Porta Delegata
	 * 
	 * @return Restituisce l'elenco dei tipi di gestori di credenziali da utilizzare lato Porta Delegata
	 */
	private static String[] tipoGestoreCredenzialiPD = null;
	private static boolean tipoGestoreCredenzialiPDRead = false;
	public String[] getTipoGestoreCredenzialiPD() {
		if(OpenSPCoop2Properties.tipoGestoreCredenzialiPDRead == false){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pd.gestoriCredenziali"); 
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipoGestoreCredenzialiPD = r;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoriCredenziali': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoGestoreCredenzialiPD = null;
			}
			OpenSPCoop2Properties.tipoGestoreCredenzialiPDRead = true;
		}

		return OpenSPCoop2Properties.tipoGestoreCredenzialiPD;
	}
	
	/**
	 * Restituisce l'elenco dei tipi di gestori di credenziali da utilizzare lato Porta Applicativa
	 * 
	 * @return Restituisce l'elenco dei tipi di gestori di credenziali da utilizzare lato Porta Applicativa
	 */
	private static String[] tipoGestoreCredenzialiPA = null;
	private static boolean tipoGestoreCredenzialiPARead = false;
	public String[] getTipoGestoreCredenzialiPA() {
		if(OpenSPCoop2Properties.tipoGestoreCredenzialiPARead == false){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.pa.gestoriCredenziali"); 
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipoGestoreCredenzialiPA = r;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoriCredenziali': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoGestoreCredenzialiPA = null;
			}
			OpenSPCoop2Properties.tipoGestoreCredenzialiPARead = true;
		}

		return OpenSPCoop2Properties.tipoGestoreCredenzialiPA;
	}
	
	/**
	 * Restituisce l'elenco dei tipi di gestori di credenziali da utilizzare sul servizio di IntegrationManager
	 * 
	 * @return Restituisce l'elenco dei tipi di gestori di credenziali da utilizzare sul servizio di IntegrationManager
	 */
	private static String[] tipoGestoreCredenzialiIM = null;
	private static boolean tipoGestoreCredenzialiIMRead = false;
	public String[] getTipoGestoreCredenzialiIM() {
		if(OpenSPCoop2Properties.tipoGestoreCredenzialiIMRead == false){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.services.integrationManager.gestoriCredenziali"); 
				if(value!=null){
					value = value.trim();
					String [] r = value.split(",");
					OpenSPCoop2Properties.tipoGestoreCredenzialiIM = r;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.services.integrationManager.gestoriCredenziali': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoGestoreCredenzialiIM = null;
			}
			OpenSPCoop2Properties.tipoGestoreCredenzialiIMRead = true;
		}

		return OpenSPCoop2Properties.tipoGestoreCredenzialiIM;
	}
	
	
	
	
	
	




	/* ----------- Accesso Registro Servizi --------------------- */
	/**
	 * Indicazione se la porta di dominio deve processare gli accordi di servizio, i servizi e i fruitori ancora in stato di bozza
	 *   
	 * @return Indicazione se la porta di dominio deve processare gli accordi di servizio, i servizi e i fruitori ancora in stato di bozza
	 * 
	 */
	private static Boolean isReadObjectStatoBozza = null;
	public boolean isReadObjectStatoBozza(){

		if(OpenSPCoop2Properties.isReadObjectStatoBozza==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.registroServizi.readObjectStatoBozza"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isReadObjectStatoBozza = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.registroServizi.readObjectStatoBozza' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isReadObjectStatoBozza = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.registroServizi.readObjectStatoBozza' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isReadObjectStatoBozza = false;
			}
		}

		return OpenSPCoop2Properties.isReadObjectStatoBozza;
	}
	
	
	
	
	
	
	/* ----------- Tracce --------------------- */
	/**
	 * Indicazione se la porta di dominio deve generare un errore in caso di tracciamento non riuscito
	 *   
	 * @return Indicazione se la porta di dominio deve generare un errore in caso di tracciamento non riuscito
	 * 
	 */
	private static Boolean isTracciaturaFallita_BloccaCooperazioneInCorso = null;
	public boolean isTracciaturaFallita_BloccaCooperazioneInCorso(){

		if(OpenSPCoop2Properties.isTracciaturaFallita_BloccaCooperazioneInCorso==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.logger.tracciamento.registrazioneFallita.bloccaCooperazioneInCorso"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isTracciaturaFallita_BloccaCooperazioneInCorso = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.tracciamento.registrazioneFallita.bloccaCooperazioneInCorso' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isTracciaturaFallita_BloccaCooperazioneInCorso = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.tracciamento.registrazioneFallita.bloccaCooperazioneInCorso' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTracciaturaFallita_BloccaCooperazioneInCorso = true;
			}
		}

		return OpenSPCoop2Properties.isTracciaturaFallita_BloccaCooperazioneInCorso;
	}
	/**
	 * Indica se in caso di rilevamento di un errore di tracciatura devono essere bloccati tutti i servizi esposti da GovWay, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
	 *   
	 * @return Indica se in caso di rilevamento di un errore di tracciatura devono essere bloccati tutti i servizi esposti da GovWay, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
	 * 
	 */
	private static Boolean isTracciaturaFallita_BloccoServiziPdD = null;
	public boolean isTracciaturaFallita_BloccoServiziPdD(){

		if(OpenSPCoop2Properties.isTracciaturaFallita_BloccoServiziPdD==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.logger.tracciamento.registrazione.bloccoServiziPdD"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isTracciaturaFallita_BloccoServiziPdD = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.tracciamento.registrazione.bloccoServiziPdD' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTracciaturaFallita_BloccoServiziPdD = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.tracciamento.registrazione.bloccoServiziPdD' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isTracciaturaFallita_BloccoServiziPdD = false;
			}
		}

		return OpenSPCoop2Properties.isTracciaturaFallita_BloccoServiziPdD;
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ----------- MsgDiagnostici --------------------- */
	
	private static Boolean isRegistrazioneDiagnosticaFile_intestazione_formatValues = null;
	public boolean isRegistrazioneDiagnosticaFile_intestazione_formatValues(){

		if(OpenSPCoop2Properties.isRegistrazioneDiagnosticaFile_intestazione_formatValues==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.logger.msgDiagnostici.file.header.formatValues"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isRegistrazioneDiagnosticaFile_intestazione_formatValues = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.msgDiagnostici.file.header.formatValues' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isRegistrazioneDiagnosticaFile_intestazione_formatValues = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.msgDiagnostici.file.header.formatValues' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isRegistrazioneDiagnosticaFile_intestazione_formatValues = true;
			}
		}

		return OpenSPCoop2Properties.isRegistrazioneDiagnosticaFile_intestazione_formatValues;
	}
	
	/**
	 * Indica se in caso di rilevamento di un errore di emissione di un messaggio diagnostico (es. salvataggio su database non riuscito) devono essere bloccati tutti i servizi esposti da GovWay, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
	 *   
	 * @return Indica se in caso di rilevamento di un errore di emissione di un messaggio diagnostico (es. salvataggio su database non riuscito) devono essere bloccati tutti i servizi esposti da GovWay, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
	 * 
	 */
	private static Boolean isRegistrazioneDiagnosticaFallita_BloccoServiziPdD = null;
	public boolean isRegistrazioneDiagnosticaFallita_BloccoServiziPdD(){

		if(OpenSPCoop2Properties.isRegistrazioneDiagnosticaFallita_BloccoServiziPdD==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.logger.msgDiagnostici.emissioneFallita.bloccoServiziPdD"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isRegistrazioneDiagnosticaFallita_BloccoServiziPdD = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.msgDiagnostici.emissioneFallita.bloccoServiziPdD' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isRegistrazioneDiagnosticaFallita_BloccoServiziPdD = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.msgDiagnostici.emissioneFallita.bloccoServiziPdD' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isRegistrazioneDiagnosticaFallita_BloccoServiziPdD = false;
			}
		}

		return OpenSPCoop2Properties.isRegistrazioneDiagnosticaFallita_BloccoServiziPdD;
	}
	
	private static Boolean isLoggerSaajDisabilitato = null;
	public boolean isLoggerSaajDisabilitato(){

		if(OpenSPCoop2Properties.isLoggerSaajDisabilitato==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.logger.com.sun.xml.messaging.saaj.disabled"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isLoggerSaajDisabilitato = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.com.sun.xml.messaging.saaj.disabled' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isLoggerSaajDisabilitato = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.com.sun.xml.messaging.saaj.disabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isLoggerSaajDisabilitato = true;
			}
		}

		return OpenSPCoop2Properties.isLoggerSaajDisabilitato;
	}
	
	
	
	
	
	
	
	
	/* ----------- Dump --------------------- */
	/**
	 * Indicazione se la porta di dominio deve registrare tutti gli attachments (in caso di dump abilitato) o solo quelli "visualizzabili"
	 *   
	 * @return Indicazione se la porta di dominio deve registrare tutti gli attachments (in caso di dump abilitato) o solo quelli "visualizzabili"
	 * 
	 */
	private static Boolean isDumpAllAttachments = null;
	public boolean isDumpAllAttachments(){

		if(OpenSPCoop2Properties.isDumpAllAttachments==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.logger.dump.allAttachments"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isDumpAllAttachments = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.dump.allAttachments' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isDumpAllAttachments = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.registroServizi.readObjectStatoBozza' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isDumpAllAttachments = true;
			}
		}

		return OpenSPCoop2Properties.isDumpAllAttachments;
	}
	
	private static Boolean isDumpBinario_registrazioneDatabase = null;
	public boolean isDumpBinario_registrazioneDatabase(){

		if(OpenSPCoop2Properties.isDumpBinario_registrazioneDatabase==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.logger.dumpBinario.registrazioneDatabase"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isDumpBinario_registrazioneDatabase = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.dumpBinario.registrazioneDatabase' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isDumpBinario_registrazioneDatabase = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.dumpBinario.registrazioneDatabase' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isDumpBinario_registrazioneDatabase = false;
			}
		}

		return OpenSPCoop2Properties.isDumpBinario_registrazioneDatabase;
	}
	
	/**
	 * Indica se in caso di errore di dump applicativo (es. salvataggio contenuto non riuscito) deve essere bloccata la gestione del messaggio e generato un errore al client
	 *   
	 * @return Indica se in caso di errore di dump applicativo (es. salvataggio contenuto non riuscito) deve essere bloccata la gestione del messaggio e generato un errore al client
	 * 
	 */
	private static Boolean isDumpFallito_BloccaCooperazioneInCorso = null;
	public boolean isDumpFallito_BloccaCooperazioneInCorso(){

		if(OpenSPCoop2Properties.isDumpFallito_BloccaCooperazioneInCorso==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.logger.dump.registrazioneFallita.bloccaCooperazioneInCorso"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isDumpFallito_BloccaCooperazioneInCorso = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.dump.registrazioneFallita.bloccaCooperazioneInCorso' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isDumpFallito_BloccaCooperazioneInCorso = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.dump.registrazioneFallita.bloccaCooperazioneInCorso' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isDumpFallito_BloccaCooperazioneInCorso = false;
			}
		}

		return OpenSPCoop2Properties.isDumpFallito_BloccaCooperazioneInCorso;
	}
	/**
	 * Indica se in caso di rilevamento di un errore di tracciatura devono essere bloccati tutti i servizi esposti da GovWay, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
	 *   
	 * @return Indica se in caso di rilevamento di un errore di tracciatura devono essere bloccati tutti i servizi esposti da GovWay, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
	 * 
	 */
	private static Boolean isDumpFallito_BloccoServiziPdD = null;
	public boolean isDumpFallito_BloccoServiziPdD(){

		if(OpenSPCoop2Properties.isDumpFallito_BloccoServiziPdD==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.logger.dump.registrazione.bloccoServiziPdD"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isDumpFallito_BloccoServiziPdD = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.dump.registrazione.bloccoServiziPdD' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isDumpFallito_BloccoServiziPdD = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.dump.registrazione.bloccoServiziPdD' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isDumpFallito_BloccoServiziPdD = false;
			}
		}

		return OpenSPCoop2Properties.isDumpFallito_BloccoServiziPdD;
	}

	
	
	
	/* ----------- Dump (NonRealtime) --------------------- */
	
	private static Integer getDumpNonRealtime_inMemoryThreshold = null;
	public int getDumpNonRealtime_inMemoryThreshold() {	
		if(OpenSPCoop2Properties.getDumpNonRealtime_inMemoryThreshold==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.dump.nonRealTime.inMemory.threshold");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getDumpNonRealtime_inMemoryThreshold = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.dump.nonRealTime.inMemory.threshold' non impostata, viene utilizzato il default="+CostantiPdD.DUMP_NON_REALTIME_THRESHOLD);
					OpenSPCoop2Properties.getDumpNonRealtime_inMemoryThreshold = CostantiPdD.DUMP_NON_REALTIME_THRESHOLD;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.dump.nonRealTime.inMemory.threshold' non impostata, viene utilizzato il default="+CostantiPdD.DUMP_NON_REALTIME_THRESHOLD+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getDumpNonRealtime_inMemoryThreshold = CostantiPdD.DUMP_NON_REALTIME_THRESHOLD;
			}  
		}

		return OpenSPCoop2Properties.getDumpNonRealtime_inMemoryThreshold;
	}
	
	private static String getDumpNonRealtime_mode = null;
	private String getDumpNonRealtime_mode() throws Exception {	
		if(OpenSPCoop2Properties.getDumpNonRealtime_mode==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.dump.nonRealTime.mode");
				if(name!=null){
					name = name.trim();
					if(!CostantiPdD.DUMP_NON_REALTIME_MODE_DB.equalsIgnoreCase(name) && 
							!CostantiPdD.DUMP_NON_REALTIME_MODE_FILE_SYSTEM.equalsIgnoreCase(name) && 
							!CostantiPdD.DUMP_NON_REALTIME_MODE_AUTO.equalsIgnoreCase(name) ) {
						throw new Exception("Modalità non supportata (attesi: "+CostantiPdD.DUMP_NON_REALTIME_MODE_DB+","+CostantiPdD.DUMP_NON_REALTIME_MODE_FILE_SYSTEM+","+CostantiPdD.DUMP_NON_REALTIME_MODE_AUTO+")");
					}
					OpenSPCoop2Properties.getDumpNonRealtime_mode = name;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.dump.nonRealTime.mode' non impostata, viene utilizzato il default="+CostantiPdD.DUMP_NON_REALTIME_MODE_AUTO);
					OpenSPCoop2Properties.getDumpNonRealtime_mode = CostantiPdD.DUMP_NON_REALTIME_MODE_AUTO;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop ''org.openspcoop2.pdd.dump.nonRealTime.mode' non impostata, errore:"+e.getMessage(),e);
				throw e;
			}  
			
			if(CostantiPdD.DUMP_NON_REALTIME_MODE_AUTO.equalsIgnoreCase(getDumpNonRealtime_mode)) {
				String databaseType = this.getDatabaseType();
				if(databaseType==null) {
					throw new Exception("Proprieta' 'org.openspcoop2.pdd.dump.nonRealTime.mode=auto' richiede che sia indicato un tipo di database nella proprieta' 'org.openspcoop2.pdd.repository.tipoDatabase'");
				}
				if(TipiDatabase.POSTGRESQL.getNome().equalsIgnoreCase(databaseType) || TipiDatabase.HSQL.getNome().equalsIgnoreCase(databaseType)) {
					// Allo stato attuale (13/02/2014) i sequenti sql server (postgresql e hsql) non supportano la funzionalita' jdbc per fare setStream senza fornire la lenght
					OpenSPCoop2Properties.getDumpNonRealtime_mode = CostantiPdD.DUMP_NON_REALTIME_MODE_FILE_SYSTEM;
				}
				else {
					OpenSPCoop2Properties.getDumpNonRealtime_mode = CostantiPdD.DUMP_NON_REALTIME_MODE_DB;
				}
			}
		}

		return OpenSPCoop2Properties.getDumpNonRealtime_mode;
	}
	
	public boolean isDumpNonRealtime_databaseMode() {
		return CostantiPdD.DUMP_NON_REALTIME_MODE_DB.equalsIgnoreCase(getDumpNonRealtime_mode);
	}
	public boolean isDumpNonRealtime_fileSystemMode() {
		return CostantiPdD.DUMP_NON_REALTIME_MODE_FILE_SYSTEM.equalsIgnoreCase(getDumpNonRealtime_mode);
	}
	
	private static File getDumpNonRealtime_repository = null;
	public File getDumpNonRealtime_repository() throws Exception {	
		if(OpenSPCoop2Properties.getDumpNonRealtime_repository==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.dump.nonRealTime.msgRepository");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getDumpNonRealtime_repository = new File(name);
				if(OpenSPCoop2Properties.getDumpNonRealtime_repository.exists()) {
					if(OpenSPCoop2Properties.getDumpNonRealtime_repository.isDirectory()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getDumpNonRealtime_repository.getAbsolutePath()+"] not dir");
					}
					if(OpenSPCoop2Properties.getDumpNonRealtime_repository.canRead()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getDumpNonRealtime_repository.getAbsolutePath()+"] cannot read");
					}
					if(OpenSPCoop2Properties.getDumpNonRealtime_repository.canWrite()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getDumpNonRealtime_repository.getAbsolutePath()+"] cannot write");
					}
				}
				else {
					// viene creata automaticamente
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.dump.nonRealTime.msgRepository': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getDumpNonRealtime_repository;
	}
	
	private static Boolean isDumpNonRealtime_throwStreamingHandlerException = null;
	public boolean isDumpNonRealtime_throwStreamingHandlerException(){

		if(OpenSPCoop2Properties.isDumpNonRealtime_throwStreamingHandlerException==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.dump.nonRealTime.throwStreamingHandlerException"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isDumpNonRealtime_throwStreamingHandlerException = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.dump.nonRealTime.throwStreamingHandlerException' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isDumpNonRealtime_throwStreamingHandlerException = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.dump.nonRealTime.throwStreamingHandlerException' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isDumpNonRealtime_throwStreamingHandlerException = true;
			}
		}

		return OpenSPCoop2Properties.isDumpNonRealtime_throwStreamingHandlerException;
	}
	
	
	
	
	
	/* ------------- ID ---------------------*/
	/**
	 * Restituisce il tipo di generatore id identificativi unici
	 *
	 * @return il tipo di generatore id identificativi unici
	 * 
	 */
	private static String tipoIDManager = null;
	public String getTipoIDManager() {	
		if(OpenSPCoop2Properties.tipoIDManager==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.idGenerator");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.tipoIDManager = name;
				}else{
					OpenSPCoop2Properties.tipoIDManager = CostantiConfigurazione.NONE;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.idGenerator': "+e.getMessage(),e);
				OpenSPCoop2Properties.tipoIDManager = CostantiConfigurazione.NONE;
			}    
		}

		return OpenSPCoop2Properties.tipoIDManager;
	}
	
	
	
	
	
	
	
	
	/* ------------- DEMO Mode ---------------------*/
	private static Boolean generazioneDateCasualiLogAbilitato = null;
	private static Date generazioneDateCasualiLog_dataInizioIntervallo = null;
	private static Date generazioneDateCasualiLog_dataFineIntervallo = null;
	public static Date getGenerazioneDateCasualiLog_dataInizioIntervallo() {
		return OpenSPCoop2Properties.generazioneDateCasualiLog_dataInizioIntervallo;
	}
	public static Date getGenerazioneDateCasualiLog_dataFineIntervallo() {
		return OpenSPCoop2Properties.generazioneDateCasualiLog_dataFineIntervallo;
	}
	public boolean generazioneDateCasualiLogAbilitato() {	
		if(OpenSPCoop2Properties.generazioneDateCasualiLogAbilitato==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.generazioneDateCasuali.enabled");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.generazioneDateCasualiLogAbilitato = Boolean.parseBoolean(name);
					
					if(OpenSPCoop2Properties.generazioneDateCasualiLogAbilitato){
						
						if(getTipoIDManager()==null || CostantiConfigurazione.NONE.equals(getTipoIDManager())){
							throw new Exception("Non e' possibile utilizzare la modalita' di generazione casuale delle date, se non si abilita la generazione di un ID");
						}
						
						String inizioIntervallo = this.reader.getValue_convertEnvProperties("org.openspcoop2.generazioneDateCasuali.inizioIntervallo");
						String fineIntervallo = this.reader.getValue_convertEnvProperties("org.openspcoop2.generazioneDateCasuali.fineIntervallo");
						SimpleDateFormat sdf = DateUtils.getDefaultDateTimeFormatter("yyyy-MM-dd hh:mm");
						if(inizioIntervallo==null){
							throw new Exception("Non e' stato definito l'intervallo di inizio per la modalita' di generazione casuale delle date");
						}
						else{inizioIntervallo=inizioIntervallo.trim();}
						OpenSPCoop2Properties.generazioneDateCasualiLog_dataInizioIntervallo = sdf.parse(inizioIntervallo);
						if(fineIntervallo==null){
							throw new Exception("Non e' stato definito l'intervallo di fine per la modalita' di generazione casuale delle date");
						}
						else{fineIntervallo=fineIntervallo.trim();}
						OpenSPCoop2Properties.generazioneDateCasualiLog_dataFineIntervallo = sdf.parse(fineIntervallo);
						
						if(OpenSPCoop2Properties.generazioneDateCasualiLog_dataInizioIntervallo.after(OpenSPCoop2Properties.generazioneDateCasualiLog_dataFineIntervallo)){
							throw new Exception("Non e' stato definito un intervallo di generazione casuale delle date corretto (inizioIntervallo>fineIntervallo)");
						}
					}
					
				}else{
					OpenSPCoop2Properties.generazioneDateCasualiLogAbilitato = false; //default, anche senza che sia definita la proprieta'
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.generazioneDateCasuali.enabled' non impostata correttamente,  errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.generazioneDateCasualiLogAbilitato = false;
			}    
		}

		return OpenSPCoop2Properties.generazioneDateCasualiLogAbilitato;
	}
	
	
	
	
	
	/* ------------- Factory ---------------------*/
	
	private static Boolean openspcoop2MessageFactoryRead = null;
	private static String openspcoop2MessageFactory = null;
	public String getOpenspcoop2MessageFactory() {	
		if(OpenSPCoop2Properties.openspcoop2MessageFactoryRead==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messagefactory");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.openspcoop2MessageFactory = v;
				} else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messagefactory' non impostata, viene utilizzato il default="+OpenSPCoop2MessageFactory.messageFactoryImpl);
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messagefactory' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		OpenSPCoop2Properties.openspcoop2MessageFactoryRead = true;
		return OpenSPCoop2Properties.openspcoop2MessageFactory;
	}
	
	private static Boolean messageSecurityContextRead = null;
	private static String messageSecurityContext = null;
	public String getMessageSecurityContext() {	
		if(OpenSPCoop2Properties.messageSecurityContextRead==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.context");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.messageSecurityContext = v;
				} else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.context' non impostata, viene utilizzato il default="+MessageSecurityFactory.messageSecurityContextImplClass);
				}
			} catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.context' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		OpenSPCoop2Properties.messageSecurityContextRead = true;
		return OpenSPCoop2Properties.messageSecurityContext;
	}
	
	private static Boolean messageSecurityDigestReaderRead = null;
	private static String messageSecurityDigestReader = null;
	public String getMessageSecurityDigestReader() {	
		if(OpenSPCoop2Properties.messageSecurityDigestReaderRead==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.digestReader");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.messageSecurityDigestReader = v;
				} else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.digestReader' non impostata, viene utilizzato il default="+MessageSecurityFactory.messageSecurityDigestReaderImplClass);
				}
			} catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.digestReader' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		OpenSPCoop2Properties.messageSecurityDigestReaderRead = true;
		return OpenSPCoop2Properties.messageSecurityDigestReader;
	}
	
	private static Boolean printInfoFactory = null;
	public boolean isPrintInfoFactory() {	
		if(OpenSPCoop2Properties.printInfoFactory==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messagefactory.printInfo");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.printInfoFactory = Boolean.parseBoolean(v);
				} 
				else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messagefactory.printInfo' non impostata, viene utilizzato il default="+true);
					OpenSPCoop2Properties.printInfoFactory = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messagefactory.printInfo' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		return OpenSPCoop2Properties.printInfoFactory;
	}
		
	private static Boolean printInfoMessageSecurity = null;
	public boolean isPrintInfoMessageSecurity() {	
		if(OpenSPCoop2Properties.printInfoMessageSecurity==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.messageSecurity.printInfo");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.printInfoMessageSecurity = Boolean.parseBoolean(v);
				} 
				else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.printInfo' non impostata, viene utilizzato il default="+true);
					OpenSPCoop2Properties.printInfoMessageSecurity = true;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.printInfo' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		return OpenSPCoop2Properties.printInfoMessageSecurity;
	}
	
	
	
	/* ------------- Utility ---------------------*/
	
	private static Boolean freeMemoryLog = null;
	public boolean getFreeMemoryLog() {	
		if(OpenSPCoop2Properties.freeMemoryLog==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.freememorylog");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.freeMemoryLog = Boolean.parseBoolean(v);
				} 
				else{
					OpenSPCoop2Properties.freeMemoryLog = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.freememorylog' non impostata correttamente. Assumo valore di default 'false'.");
				OpenSPCoop2Properties.freeMemoryLog = false;
			} 
		}
		return OpenSPCoop2Properties.freeMemoryLog;
	}
	
	
	
	
	
	
	/* ------------- Protocol ---------------------*/
	
	private static String defaultProtocolName = null;		
	public String getDefaultProtocolName(){
		if(OpenSPCoop2Properties.defaultProtocolName==null){
			try{ 
				OpenSPCoop2Properties.defaultProtocolName = this.reader.getValue("org.openspcoop2.pdd.services.defaultProtocol");
				if(OpenSPCoop2Properties.defaultProtocolName!=null){
					OpenSPCoop2Properties.defaultProtocolName = OpenSPCoop2Properties.defaultProtocolName.trim();
				}
			} catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.defaultProtocol' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		return OpenSPCoop2Properties.defaultProtocolName;
	}

	
	
	/* ------------- Integration Manager ---------------------*/
	
	private static Boolean isIntegrationManagerEnabled = null;
	public boolean isIntegrationManagerEnabled() {	
		if(OpenSPCoop2Properties.isIntegrationManagerEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.integrationManager.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrationManager.enabled' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isIntegrationManagerEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrationManager.enabled': "+e.getMessage(),e);
				OpenSPCoop2Properties.isIntegrationManagerEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isIntegrationManagerEnabled;
	}
	
	
	
	/* ------------- Generazione Errore Protocol non supportato ---------------------*/
	
	private static Boolean isGenerazioneErroreProtocolloNonSupportato = null;
	public boolean isGenerazioneErroreProtocolloNonSupportato() {	
		if(OpenSPCoop2Properties.isGenerazioneErroreProtocolloNonSupportato==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.protocolNotSupported.generateErrorMessage");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.protocolNotSupported.generateErrorMessage' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isGenerazioneErroreProtocolloNonSupportato = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.protocolNotSupported.generateErrorMessage': "+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneErroreProtocolloNonSupportato = false;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneErroreProtocolloNonSupportato;
	}
	
	
	
	
	/* ------------- Generazione Errore HttpMethodUnsupported ---------------------*/
	
	private static Boolean isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled = null;
	public boolean isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled() {	
		if(OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.pd.httpMethodUnsupported.generateErrorMessage");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.pd.httpMethodUnsupported.generateErrorMessage' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.pd.httpMethodUnsupported.generateErrorMessage': "+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled;
	}
	
	private static Boolean isGenerazioneErroreHttpMethodUnsupportedPortaDelegataImbustamentoSOAPEnabled = null;
	public boolean isGenerazioneErroreHttpMethodUnsupportedPortaDelegataImbustamentoSOAPEnabled() {	
		if(OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataImbustamentoSOAPEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.pdToSoap.httpMethodUnsupported.generateErrorMessage");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.pdToSoap.httpMethodUnsupported.generateErrorMessage' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataImbustamentoSOAPEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.pdToSoap.httpMethodUnsupported.generateErrorMessage': "+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataImbustamentoSOAPEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataImbustamentoSOAPEnabled;
	}
	
	private static Boolean isGenerazioneErroreHttpMethodUnsupportedPortaApplicativaEnabled = null;
	public boolean isGenerazioneErroreHttpMethodUnsupportedPortaApplicativaEnabled() {	
		if(OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaApplicativaEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.pa.httpMethodUnsupported.generateErrorMessage");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.pa.httpMethodUnsupported.generateErrorMessage' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaApplicativaEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.pa.httpMethodUnsupported.generateErrorMessage': "+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaApplicativaEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaApplicativaEnabled;
	}
	
	private static Boolean isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled = null;
	public boolean isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled() {	
		if(OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.im.httpMethodUnsupported.generateErrorMessage");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.im.httpMethodUnsupported.generateErrorMessage' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.im.httpMethodUnsupported.generateErrorMessage': "+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled;
	}
	
	private static Boolean isGenerazioneErroreHttpMethodUnsupportedCheckEnabled = null;
	public boolean isGenerazioneErroreHttpMethodUnsupportedCheckEnabled() {	
		if(OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.check.httpMethodUnsupported.generateErrorMessage");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.check.httpMethodUnsupported.generateErrorMessage' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.check.httpMethodUnsupported.generateErrorMessage': "+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckEnabled;
	}
	
	
	
	
	/* ------------- Generazione WSDL ---------------------*/
	
	private static Boolean isGenerazioneWsdlPortaDelegataEnabled = null;
	public boolean isGenerazioneWsdlPortaDelegataEnabled() {	
		if(OpenSPCoop2Properties.isGenerazioneWsdlPortaDelegataEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.pd.generateWsdl");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.pd.generateWsdl' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isGenerazioneWsdlPortaDelegataEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.pd.generateWsdl': "+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneWsdlPortaDelegataEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneWsdlPortaDelegataEnabled;
	}
	
	private static Boolean isGenerazioneWsdlPortaApplicativaEnabled = null;
	public boolean isGenerazioneWsdlPortaApplicativaEnabled() {	
		if(OpenSPCoop2Properties.isGenerazioneWsdlPortaApplicativaEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.pa.generateWsdl");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.pa.generateWsdl' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isGenerazioneWsdlPortaApplicativaEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.pa.generateWsdl': "+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneWsdlPortaApplicativaEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneWsdlPortaApplicativaEnabled;
	}
	
	private static Boolean isGenerazioneWsdlIntegrationManagerEnabled = null;
	public boolean isGenerazioneWsdlIntegrationManagerEnabled() {	
		if(OpenSPCoop2Properties.isGenerazioneWsdlIntegrationManagerEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.im.generateWsdl");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.im.generateWsdl' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isGenerazioneWsdlIntegrationManagerEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.im.generateWsdl': "+e.getMessage(),e);
				OpenSPCoop2Properties.isGenerazioneWsdlIntegrationManagerEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneWsdlIntegrationManagerEnabled;
	}
	
	
	
	
	/* ------------- Check Reader Risorse JMX ---------------------*/
	
	private static Boolean isCheckEnabled = null;
	public boolean isCheckEnabled() {	
		if(OpenSPCoop2Properties.isCheckEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.check.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.check.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isCheckEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.check.enabled': "+e.getMessage(),e);
				OpenSPCoop2Properties.isCheckEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isCheckEnabled;
	}
	
	private static Boolean isCheckReadJMXResourcesEnabled = null;
	public boolean isCheckReadJMXResourcesEnabled() {	
		if(OpenSPCoop2Properties.isCheckReadJMXResourcesEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.check.readJMXResources.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.check.readJMXResources.enabled' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isCheckReadJMXResourcesEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.check.readJMXResources.enabled': "+e.getMessage(),e);
				OpenSPCoop2Properties.isCheckReadJMXResourcesEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isCheckReadJMXResourcesEnabled;
	}
	
	private static String getCheckReadJMXResourcesUsername = null;
	private static Boolean getCheckReadJMXResourcesUsername_read = null;
	public String getCheckReadJMXResourcesUsername() {	
		if(OpenSPCoop2Properties.getCheckReadJMXResourcesUsername_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.check.readJMXResources.username");
				if(name!=null){
					name = name.trim();
				}
				OpenSPCoop2Properties.getCheckReadJMXResourcesUsername_read = true;
				OpenSPCoop2Properties.getCheckReadJMXResourcesUsername = name;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.check.readJMXResources.username': "+e.getMessage(),e);
			}    
		}
		return OpenSPCoop2Properties.getCheckReadJMXResourcesUsername;
	}
	
	private static String getCheckReadJMXResourcesPassword = null;
	private static Boolean getCheckReadJMXResourcesPassword_read = null;
	public String getCheckReadJMXResourcesPassword() {	
		if(OpenSPCoop2Properties.getCheckReadJMXResourcesPassword_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.check.readJMXResources.password");
				if(name!=null){
					name = name.trim();
				}
				OpenSPCoop2Properties.getCheckReadJMXResourcesPassword_read = true;
				OpenSPCoop2Properties.getCheckReadJMXResourcesPassword = name;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.check.readJMXResources.password': "+e.getMessage(),e);
			}    
		}
		return OpenSPCoop2Properties.getCheckReadJMXResourcesPassword;
	}
	
	
	
	

	
	/* -------------Datasource Wrapped  ---------------------*/
	
	private static Boolean isDSOp2UtilsEnabled = null;
	public boolean isDSOp2UtilsEnabled() {	
		if(OpenSPCoop2Properties.isDSOp2UtilsEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.datasource.useDSUtils");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.datasource.useDSUtils' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isDSOp2UtilsEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.datasource.useDSUtils': "+e.getMessage(),e);
				OpenSPCoop2Properties.isDSOp2UtilsEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isDSOp2UtilsEnabled;
	}
	

	
	
	/* ------------- NotifierInputStream  ---------------------*/
	
	private static Boolean isNotifierInputStreamEnabled = null;
	public boolean isNotifierInputStreamEnabled(){

		if(OpenSPCoop2Properties.isNotifierInputStreamEnabled==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.notifierInputStream.enabled"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isNotifierInputStreamEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.notifierInputStream.enabled' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isNotifierInputStreamEnabled = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.notifierInputStream.enabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isNotifierInputStreamEnabled = false;
			}
		}

		return OpenSPCoop2Properties.isNotifierInputStreamEnabled;
	}
	
	private static String notifierInputStreamCallback = null;		
	private static Boolean notifierInputStreamCallbackRead = null;		
	public String getNotifierInputStreamCallback() throws Exception{
		if(OpenSPCoop2Properties.notifierInputStreamCallbackRead==null){
			try{ 
				OpenSPCoop2Properties.notifierInputStreamCallback = this.reader.getValue("org.openspcoop2.pdd.notifierInputStream.tipo");
				if(OpenSPCoop2Properties.notifierInputStreamCallback!=null){
					OpenSPCoop2Properties.notifierInputStreamCallback = OpenSPCoop2Properties.notifierInputStreamCallback.trim();
					OpenSPCoop2Properties.notifierInputStreamCallbackRead = true;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.notifierInputStream.tipo' non impostata correttamente,  errore:"+e.getMessage(),e);
				throw new Exception("Proprieta' di openspcoop 'org.openspcoop2.pdd.notifierInputStream.tipo' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		return OpenSPCoop2Properties.notifierInputStreamCallback;
	}
	
	
	
	
	/* ------------- FORMURLEncoder  ---------------------*/
	
	private static Boolean isFormUrlEncodedFilterEnabled = null;
	public boolean isFormUrlEncodedFilterEnabled(){

		if(OpenSPCoop2Properties.isFormUrlEncodedFilterEnabled==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.formUrlEncodedFilter.enabled"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isFormUrlEncodedFilterEnabled = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.formUrlEncodedFilter.enabled' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isFormUrlEncodedFilterEnabled = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.formUrlEncodedFilter.enabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isFormUrlEncodedFilterEnabled = false;
			}
		}

		return OpenSPCoop2Properties.isFormUrlEncodedFilterEnabled;
	}
	
	
	
	
	/* ------------- MonitorSDK  ---------------------*/
	
	private static File getMonitorSDK_repositoryJars = null;
	public File getMonitorSDK_repositoryJars() throws Exception {	
		if(OpenSPCoop2Properties.getMonitorSDK_repositoryJars==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.monitor.sdk.repositoryJars");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getMonitorSDK_repositoryJars = new File(name);
					if(OpenSPCoop2Properties.getMonitorSDK_repositoryJars.exists()) {
						if(OpenSPCoop2Properties.getMonitorSDK_repositoryJars.isDirectory()==false) {
							throw new Exception("Dir ["+OpenSPCoop2Properties.getMonitorSDK_repositoryJars.getAbsolutePath()+"] not dir");
						}
						if(OpenSPCoop2Properties.getMonitorSDK_repositoryJars.canRead()==false) {
							throw new Exception("Dir ["+OpenSPCoop2Properties.getMonitorSDK_repositoryJars.getAbsolutePath()+"] cannot read");
						}
					}
					else {
						// viene creata automaticamente
					}
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.monitor.sdk.repositoryJars': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getMonitorSDK_repositoryJars;
	}
	
	
	
	/* ------------- JMINIX Console  ---------------------*/
	
	private static Integer portJminixConsole = null;
	private static Boolean portJminixConsoleReaded = null;
	public Integer getPortJminixConsole() {	
		if(OpenSPCoop2Properties.portJminixConsoleReaded==null){
			try{ 
				String p = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.jminix.port");
				if(p!=null){
					p = p.trim();
					OpenSPCoop2Properties.portJminixConsole = Integer.parseInt(p);
				}
				OpenSPCoop2Properties.portJminixConsoleReaded = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.jminix.port': "+e.getMessage(),e);
				OpenSPCoop2Properties.portJminixConsoleReaded = true;
			}    
		}

		return OpenSPCoop2Properties.portJminixConsole;
	}
	
	
	
	

	/* ------------- Custom Function ---------------------*/
	
	private static Boolean isEnabledFunctionPD = null;
	public boolean isEnabledFunctionPD() {	
		if(OpenSPCoop2Properties.isEnabledFunctionPD==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.function.pd.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.function.pd.enabled' non impostata, viene utilizzato il default=false");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEnabledFunctionPD = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.function.pd.enabled': "+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledFunctionPD = false;
			}    
		}

		return OpenSPCoop2Properties.isEnabledFunctionPD;
	}
	
	private static Boolean isEnabledFunctionPDtoSOAP = null;
	public boolean isEnabledFunctionPDtoSOAP() {	
		if(OpenSPCoop2Properties.isEnabledFunctionPDtoSOAP==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.function.pdToSoap.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.function.pdToSoap.enabled' non impostata, viene utilizzato il default=false");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEnabledFunctionPDtoSOAP = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.function.pdToSoap.enabled': "+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledFunctionPDtoSOAP = false;
			}    
		}

		return OpenSPCoop2Properties.isEnabledFunctionPDtoSOAP;
	}
	
	private static Boolean isEnabledFunctionPA = null;
	public boolean isEnabledFunctionPA() {	
		if(OpenSPCoop2Properties.isEnabledFunctionPA==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.function.pa.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.function.pa.enabled' non impostata, viene utilizzato il default=false");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEnabledFunctionPA = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.function.pa.enabled': "+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledFunctionPA = false;
			}    
		}

		return OpenSPCoop2Properties.isEnabledFunctionPA;
	}
	
	
	private static FunctionContextsCustom customContexts = null;
	public FunctionContextsCustom getCustomContexts() throws UtilsException {	
		if(OpenSPCoop2Properties.customContexts==null){
			try{ 
				Properties p = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.function.custom.");
				if(p!=null && p.size()>0) {
					Enumeration<Object> en = p.keys();
					while (en.hasMoreElements()) {
						Object object = (Object) en.nextElement();
						if(object instanceof String) {
							String s = (String) object;
							if(s.endsWith(".context")) {
								String alias = s.substring(0,s.indexOf(".context"));
								
								String context = p.getProperty(s);
								if(context==null || "".equals(context.trim())) {
									throw new Exception("Context not defined for alias '"+alias+"'");
								}
								context = context.trim();
								
								String service = p.getProperty(alias+".service");
								boolean serviceFound = false;
								FunctionContextCustom customWithoutSubContext = null;
								IDService idServiceCustomWithoutSubContext = null;
								if(service!=null && !"".equals(service.trim())) {
									idServiceCustomWithoutSubContext = IDService.toEnumConstant(service);
									if(idServiceCustomWithoutSubContext==null) {
										throw new Exception("Value '"+service+"' unsupported for service in alias '"+alias+"'");
									}
									customWithoutSubContext = new FunctionContextCustom(context, idServiceCustomWithoutSubContext);
									serviceFound = true;
									// lo registro dopo gli eventuali subcontext
								}

								Properties pSubContext = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.function.custom."+alias+".subcontext.");
								if(pSubContext==null || pSubContext.size()<=0) {
									if(!serviceFound) {
										throw new Exception("Service and SubContext undefined for alias '"+alias+"'");
									}
								}
								else {
									HashMap<String,IDService> subcontextMap = new HashMap<String,IDService>();
									Enumeration<Object> enSubContext = pSubContext.keys();
									while (enSubContext.hasMoreElements()) {
										Object objectSubContext = (Object) enSubContext.nextElement();
										if(objectSubContext instanceof String) {
											String subContextKey = (String) objectSubContext;
											if(subContextKey.endsWith(".url")) {
												String aliasSubContext = subContextKey.substring(0,subContextKey.indexOf(".url"));
												
												String subContext = pSubContext.getProperty(subContextKey);
												if(subContext==null || "".equals(subContext.trim())) {
													throw new Exception("Url not defined for alias '"+alias+"', subcontext '"+aliasSubContext+"'");
												}
												subContext = subContext.trim();
												
												String serviceSubContext = pSubContext.getProperty(aliasSubContext+".service");
												if(serviceSubContext!=null && !"".equals(serviceSubContext.trim())) {
													IDService idService = IDService.toEnumConstant(serviceSubContext);
													if(idService==null) {
														throw new Exception("Value '"+serviceSubContext+"' unsupported for service in alias '"+alias+"', subcontext '"+aliasSubContext+"'");
													}
													subcontextMap.put(subContext, idService);	
												}
												else {
													throw new Exception("Service not defined for alias '"+alias+"', subcontext '"+aliasSubContext+"'");
												}
											}
										}
									}
									if(subcontextMap.size()<=0) {
										if(!serviceFound) {
											throw new Exception("SubContext undefined (wrong configuration) for alias '"+alias+"'");
										}
									}
									else {
										FunctionContextCustom custom = new FunctionContextCustom(context, subcontextMap);
										if(OpenSPCoop2Properties.customContexts==null) {
											OpenSPCoop2Properties.customContexts = new FunctionContextsCustom();
										}
										OpenSPCoop2Properties.customContexts.getContexts().add(custom);
										Iterator<String> itS = subcontextMap.keySet().iterator();
										while (itS.hasNext()) {
											String subContext = (String) itS.next();
											this.log.info("Registrato context '"+context+"', subcontext '"+subContext+"',  per service '"+subcontextMap.get(subContext)+"'");		
										}
									}
								}
								
								if(customWithoutSubContext!=null) {
									if(OpenSPCoop2Properties.customContexts==null) {
										OpenSPCoop2Properties.customContexts = new FunctionContextsCustom();
									}
									OpenSPCoop2Properties.customContexts.getContexts().add(customWithoutSubContext);
									this.log.info("Registrato context '"+context+"' per service '"+idServiceCustomWithoutSubContext+"'");
								}
							}
						}
					}
				}
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura dei custom contexts: "+e.getMessage(),e);
				throw new UtilsException("Riscontrato errore durante la lettura dei custom contexts: "+e.getMessage(),e);
			}    
		}

		return OpenSPCoop2Properties.customContexts;
	}
	
	
	
	
	/* ------------- Custom Container ---------------------*/
	
	private static Boolean realContainerCustomRead = null;
	private static String realContainerCustom = null;
	public String getRealContainerCustom() {	
		if(OpenSPCoop2Properties.realContainerCustomRead==null){
			try{ 
				String v = null;
				v = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.realmContainer.custom");
				if(v!=null){
					v = v.trim();
					OpenSPCoop2Properties.realContainerCustom = v;
				} 
			} catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.realmContainer.custom' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		OpenSPCoop2Properties.realContainerCustomRead = true;
		return OpenSPCoop2Properties.realContainerCustom;
	}
	
	
	
	/* ------------- ExtendedInfo ---------------------*/
	
	private static String extendedInfoConfigurazione = null;		
	private static Boolean extendedInfoConfigurazioneRead = null;		
	public String getExtendedInfoConfigurazione() throws Exception{
		if(OpenSPCoop2Properties.extendedInfoConfigurazioneRead==null){
			String pName = "org.openspcoop2.pdd.config.extendedInfo.configurazione";
			try{ 
				OpenSPCoop2Properties.extendedInfoConfigurazione = this.reader.getValue(pName);
				if(OpenSPCoop2Properties.extendedInfoConfigurazione!=null){
					OpenSPCoop2Properties.extendedInfoConfigurazione = OpenSPCoop2Properties.extendedInfoConfigurazione.trim();
					OpenSPCoop2Properties.extendedInfoConfigurazioneRead = true;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage(),e);
				throw new Exception("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		return OpenSPCoop2Properties.extendedInfoConfigurazione;
	}
	
	private static String extendedInfoPortaDelegata = null;		
	private static Boolean extendedInfoPortaDelegataRead = null;		
	public String getExtendedInfoPortaDelegata() throws Exception{
		if(OpenSPCoop2Properties.extendedInfoPortaDelegataRead==null){
			String pName = "org.openspcoop2.pdd.config.extendedInfo.portaDelegata";
			try{ 
				OpenSPCoop2Properties.extendedInfoPortaDelegata = this.reader.getValue(pName);
				if(OpenSPCoop2Properties.extendedInfoPortaDelegata!=null){
					OpenSPCoop2Properties.extendedInfoPortaDelegata = OpenSPCoop2Properties.extendedInfoPortaDelegata.trim();
					OpenSPCoop2Properties.extendedInfoPortaDelegataRead = true;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage(),e);
				throw new Exception("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		return OpenSPCoop2Properties.extendedInfoPortaDelegata;
	}
	
	private static String extendedInfoPortaApplicativa = null;		
	private static Boolean extendedInfoPortaApplicativaRead = null;		
	public String getExtendedInfoPortaApplicativa() throws Exception{
		if(OpenSPCoop2Properties.extendedInfoPortaApplicativaRead==null){
			String pName = "org.openspcoop2.pdd.config.extendedInfo.portaApplicativa";
			try{ 
				OpenSPCoop2Properties.extendedInfoPortaApplicativa = this.reader.getValue(pName);
				if(OpenSPCoop2Properties.extendedInfoPortaApplicativa!=null){
					OpenSPCoop2Properties.extendedInfoPortaApplicativa = OpenSPCoop2Properties.extendedInfoPortaApplicativa.trim();
					OpenSPCoop2Properties.extendedInfoPortaApplicativaRead = true;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage(),e);
				throw new Exception("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage(),e);
			} 
		}
		return OpenSPCoop2Properties.extendedInfoPortaApplicativa;
	}
	
	
	
	
	
	
	
	/* ------------- Validazione Contenuti Applicativi ---------------------*/
	
	private static Boolean isValidazioneContenutiApplicativi_debug = null;
	public boolean isValidazioneContenutiApplicativi_debug(){

		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_debug==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.validazioneContenutiApplicativi.debug"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_debug = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.debug' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_debug = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.debug' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_debug = false;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_debug;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione = null;
	public boolean isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione(){

		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.gestione"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.gestione' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.gestione' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_gestione;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione = null;
	public boolean isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione(){

		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.ripulituraDopoValidazione"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.ripulituraDopoValidazione' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.rpcLiteral.xsiType.ripulituraDopoValidazione' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_rpcLiteral_xsiType_ripulituraDopoValidazione;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_checkSoapAction = null;
	public boolean isValidazioneContenutiApplicativi_checkSoapAction(){

		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_checkSoapAction==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.validazioneContenutiApplicativi.soapAction.check"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_checkSoapAction = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.soapAction.check' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_checkSoapAction = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.soapAction.check' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_checkSoapAction = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_checkSoapAction;
	}
	
	private static ADDITIONAL getValidazioneContenutiApplicativi_json_policyAdditionalProperties = null;
	public ADDITIONAL getValidazioneContenutiApplicativi_json_policyAdditionalProperties(){

		if(OpenSPCoop2Properties.getValidazioneContenutiApplicativi_json_policyAdditionalProperties==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.validazioneContenutiApplicativi.json.additionalProperties"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.getValidazioneContenutiApplicativi_json_policyAdditionalProperties = ADDITIONAL.valueOf(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.json.additionalProperties' non impostata, viene utilizzato il default");
					OpenSPCoop2Properties.getValidazioneContenutiApplicativi_json_policyAdditionalProperties = ADDITIONAL.DEFAULT;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.json.additionalProperties' con un valore non ammesso (valori consentiti: "+ADDITIONAL.DEFAULT.name()+","+ADDITIONAL.IF_NULL_DISABLE+","+ADDITIONAL.FORCE_DISABLE+"), viene utilizzato il default, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getValidazioneContenutiApplicativi_json_policyAdditionalProperties = ADDITIONAL.DEFAULT;
			}
		}

		return OpenSPCoop2Properties.getValidazioneContenutiApplicativi_json_policyAdditionalProperties;
	}
	
	
	private static ApiName getValidazioneContenutiApplicativi_openApi_jsonValidator = null;
	public ApiName getValidazioneContenutiApplicativi_openApi_jsonValidator(){

		if(OpenSPCoop2Properties.getValidazioneContenutiApplicativi_openApi_jsonValidator==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.json.validator"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.getValidazioneContenutiApplicativi_openApi_jsonValidator = ApiName.valueOf(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.json.validator' non impostata, viene utilizzato il default "+ApiName.NETWORK_NT);
					OpenSPCoop2Properties.getValidazioneContenutiApplicativi_openApi_jsonValidator = ApiName.NETWORK_NT;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.json.validator' con un valore non ammesso (valori consentiti: "+ApiName.NETWORK_NT.name()+","+ApiName.FGE+","+ApiName.EVERIT+"), viene utilizzato il default, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getValidazioneContenutiApplicativi_openApi_jsonValidator = ApiName.NETWORK_NT;
			}
		}

		return OpenSPCoop2Properties.getValidazioneContenutiApplicativi_openApi_jsonValidator;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_openApi_useOpenApi4j = null;
	public boolean isValidazioneContenutiApplicativi_openApi_useOpenApi4j(){

		String pName = "org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.useOpenApi4j";
		
		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_useOpenApi4j==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_useOpenApi4j = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_useOpenApi4j = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_useOpenApi4j = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_useOpenApi4j;
	}

	private static Boolean isValidazioneContenutiApplicativi_openApi_openApi4j_mergeAPISpec = null;
	public boolean isValidazioneContenutiApplicativi_openApi_openApi4j_mergeAPISpec(){

		String pName = "org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.openApi4j.mergeAPISpec";
		
		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_mergeAPISpec==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_mergeAPISpec = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_mergeAPISpec = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_mergeAPISpec = false;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_mergeAPISpec;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateAPISpec = null;
	public boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateAPISpec(){

		String pName = "org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.openApi4j.validateAPISpec";
		
		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateAPISpec==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateAPISpec = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateAPISpec = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateAPISpec = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateAPISpec;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestQuery = null;
	public boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestQuery(){

		String pName = "org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.openApi4j.validateRequestQuery";
		
		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestQuery==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestQuery = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestQuery = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestQuery = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestQuery;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestHeaders = null;
	public boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestHeaders(){

		String pName = "org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.openApi4j.validateRequestHeaders";
		
		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestHeaders==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestHeaders = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestHeaders = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestHeaders = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestHeaders;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestCookie = null;
	public boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestCookie(){

		String pName = "org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.openApi4j.validateRequestCookie";
		
		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestCookie==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestCookie = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestCookie = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestCookie = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestCookie;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestBody = null;
	public boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestBody(){

		String pName = "org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.openApi4j.validateRequestBody";
		
		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestBody==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestBody = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestBody = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestBody = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateRequestBody;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseHeaders = null;
	public boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseHeaders(){

		String pName = "org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.openApi4j.validateResponseHeaders";
		
		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseHeaders==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseHeaders = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseHeaders = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseHeaders = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseHeaders;
	}
	
	private static Boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseBody = null;
	public boolean isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseBody(){

		String pName = "org.openspcoop2.pdd.validazioneContenutiApplicativi.openApi.openApi4j.validateResponseBody";
		
		if(OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseBody==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseBody = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseBody = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseBody = true;
			}
		}

		return OpenSPCoop2Properties.isValidazioneContenutiApplicativi_openApi_openApi4j_validateResponseBody;
	}
	
	
	/* ------------- Caching Response ---------------------*/
	
	private static String getCachingResponseDigestAlgorithm = null;
	public String getCachingResponseDigestAlgorithm(){

		if(OpenSPCoop2Properties.getCachingResponseDigestAlgorithm==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.cachingResponse.digestAlgorithm"); 

				if (value != null){
					value = value.trim();
					MessageDigest.getInstance(value); // valida
					OpenSPCoop2Properties.getCachingResponseDigestAlgorithm = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.cachingResponse.digestAlgorithm' non impostata, viene utilizzato il default="+CostantiPdD.RESPONSE_CACHE_REQUEST_DIGEST_DEFAULT_ALGORITHM);
					OpenSPCoop2Properties.getCachingResponseDigestAlgorithm = CostantiPdD.RESPONSE_CACHE_REQUEST_DIGEST_DEFAULT_ALGORITHM;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.cachingResponse.digestAlgorithm' non impostata, viene utilizzato il default="+CostantiPdD.RESPONSE_CACHE_REQUEST_DIGEST_DEFAULT_ALGORITHM+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getCachingResponseDigestAlgorithm = CostantiPdD.RESPONSE_CACHE_REQUEST_DIGEST_DEFAULT_ALGORITHM;
			}
		}

		return OpenSPCoop2Properties.getCachingResponseDigestAlgorithm;
	}
	
	private static Boolean getCachingResponseHeaderCacheKey_read = null;
	private static String getCachingResponseHeaderCacheKey = null;
	public String getCachingResponseHeaderCacheKey(){

		if(OpenSPCoop2Properties.getCachingResponseHeaderCacheKey_read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.cachingResponse.header.cacheKey"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.getCachingResponseHeaderCacheKey = value;
				}

				OpenSPCoop2Properties.getCachingResponseHeaderCacheKey_read = true;
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.cachingResponse.header.cacheKey', errore:"+e.getMessage(),e);
				
			}
		}

		return OpenSPCoop2Properties.getCachingResponseHeaderCacheKey;
	}
	
	
	
	/* ------------- Gestione Token ---------------------*/
	
	private static Boolean getGestioneToken_iatTimeCheck_milliseconds_read = null;
	private static Integer getGestioneToken_iatTimeCheck_milliseconds = null;
	public Integer getGestioneToken_iatTimeCheck_milliseconds() throws Exception{

		if(OpenSPCoop2Properties.getGestioneToken_iatTimeCheck_milliseconds_read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.iat"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.getGestioneToken_iatTimeCheck_milliseconds = Integer.valueOf(value); // minuti
					OpenSPCoop2Properties.getGestioneToken_iatTimeCheck_milliseconds = OpenSPCoop2Properties.getGestioneToken_iatTimeCheck_milliseconds * 60 * 1000;
				}
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.iat' non impostata, errore:"+e.getMessage(),e);
				throw e;
			}
			
			OpenSPCoop2Properties.getGestioneToken_iatTimeCheck_milliseconds_read = true;
		}

		return OpenSPCoop2Properties.getGestioneToken_iatTimeCheck_milliseconds;
	}
	
	private static Boolean isGestioneToken_saveSourceTokenInfo = null;
	public boolean isGestioneToken_saveSourceTokenInfo(){

		if(OpenSPCoop2Properties.isGestioneToken_saveSourceTokenInfo==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.saveSourceTokenInfo"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isGestioneToken_saveSourceTokenInfo = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.saveSourceTokenInfo' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isGestioneToken_saveSourceTokenInfo = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.saveSourceTokenInfo' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isGestioneToken_saveSourceTokenInfo = false;
			}
		}

		return OpenSPCoop2Properties.isGestioneToken_saveSourceTokenInfo;
	}
	
	private static Boolean getGestioneTokenFormatDate_read = null;
	private static String getGestioneTokenFormatDate = null;
	public String getGestioneTokenFormatDate() throws Exception{

		if(OpenSPCoop2Properties.getGestioneTokenFormatDate_read==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.date.format"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.getGestioneTokenFormatDate = value;
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.forward.date.format' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg);
			}
			
			OpenSPCoop2Properties.getGestioneTokenFormatDate_read = true;
		}

		return OpenSPCoop2Properties.getGestioneTokenFormatDate;
	}
	
	private static String getGestioneTokenHeaderTrasportoJSON = null;
	public String getGestioneTokenHeaderTrasportoJSON() throws Exception{

		if(OpenSPCoop2Properties.getGestioneTokenHeaderTrasportoJSON==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.trasporto.jsonHeader"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.getGestioneTokenHeaderTrasportoJSON = value;
				}else{
					throw new Exception("Non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.jsonHeader' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg);
			}
		}

		return OpenSPCoop2Properties.getGestioneTokenHeaderTrasportoJSON;
	}
	
	private static String getGestioneTokenHeaderTrasportoJWT = null;
	public String getGestioneTokenHeaderTrasportoJWT() throws Exception{

		if(OpenSPCoop2Properties.getGestioneTokenHeaderTrasportoJWT==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.trasporto.jwsHeader"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.getGestioneTokenHeaderTrasportoJWT = value;
				}else{
					throw new Exception("Non impostata");
				}

			}catch(java.lang.Exception e) {
				String msg = "Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.jwsHeader' non impostata, errore:"+e.getMessage();
				this.log.error(msg,e);
				throw new Exception(msg);
			}
		}

		return OpenSPCoop2Properties.getGestioneTokenHeaderTrasportoJWT;
	}

	/**
	 * Restituisce le proprieta' che identificano gli header di integrazione in caso di 'trasporto' 
	 *
	 * @return Restituisce le proprieta' che identificano gli header di integrazione in caso di 'trasporto'
	 *  
	 */
	private static java.util.concurrent.ConcurrentHashMap<String, String> keyValue_gestioneTokenHeaderIntegrazioneTrasporto = null;
	public java.util.concurrent.ConcurrentHashMap<String, String> getKeyValue_gestioneTokenHeaderIntegrazioneTrasporto() {	
		if(OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneTrasporto==null){

			try{ 

				OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneTrasporto = this.reader.readPropertiesAsConcurrentHashMap_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.trasporto.keyword.");
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.keyword.*': "+e.getMessage(),e);
				OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneTrasporto = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneTrasporto;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPD = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneTrasporto() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPD==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.trasporto.pd.set.enabled.");
				keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPD = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPD.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.pd.set.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.pd.set.enabled.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPD;
	}

	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPA = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneTrasporto() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPA==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.trasporto.pa.set.enabled.");
				keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPA = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPA.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.pa.set.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.pa.set.enabled.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPA;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_gestioneTokenHeaderIntegrazioneJson_setPD = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneJson() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneJson_setPD==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.json.pd.set.enabled.");
				keyValue_gestioneTokenHeaderIntegrazioneJson_setPD = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_gestioneTokenHeaderIntegrazioneJson_setPD.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.gestioneToken.forward.json.pd.set.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.json.pd.set.enabled.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneJson_setPD;
	}

	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> keyValue_gestioneTokenHeaderIntegrazioneJson_setPA = null;
	public java.util.concurrent.ConcurrentHashMap<String, Boolean> getKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson() throws Exception {	
		if(OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneJson_setPA==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.json.pa.set.enabled.");
				keyValue_gestioneTokenHeaderIntegrazioneJson_setPA = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				Iterator<?> it = prop.keySet().iterator();
				while (it.hasNext()) {
					Object object = (Object) it.next();
					if(object instanceof String) {
						String key = (String) object;
						String value = prop.getProperty(key);
						try {
							boolean b = Boolean.parseBoolean(value);
							keyValue_gestioneTokenHeaderIntegrazioneJson_setPA.put(key, b);
						}catch(Exception e) {
							throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.gestioneToken.forward.json.pa.set.enabled."+key+"' (atteso: true/false): "+e.getMessage(),e);
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.json.pa.set.enabled.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.keyValue_gestioneTokenHeaderIntegrazioneJson_setPA;
	}
	
	private static List<String> getCustomClaimsKeys_gestioneTokenForward = null;
	public List<String> getCustomClaimsKeys_gestioneTokenForward() throws Exception {	
		if(OpenSPCoop2Properties.getCustomClaimsKeys_gestioneTokenForward==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				OpenSPCoop2Properties.getCustomClaimsKeys_gestioneTokenForward= new ArrayList<>();
				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.custom.");
				if(prop!=null && !prop.isEmpty()) {
					Enumeration<?> en = prop.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						if(key.contains(".")) {
							String keySub = key.substring(key.indexOf(".")+1); 
							if("name".equals(keySub)) {
								String tmp = key.substring(0, key.indexOf("."));
								if(tmp!=null) {
									OpenSPCoop2Properties.getCustomClaimsKeys_gestioneTokenForward.add(tmp.trim());
								}
							}
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.custom.CLAIM_NAME.name': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getCustomClaimsKeys_gestioneTokenForward;
	}
	
	private static Character gestioneTokenHeaderIntegrazioneTrasporto_audienceSeparator = null;
	public Character getGestioneTokenHeaderIntegrazioneTrasporto_audienceSeparator(){

		if(OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_audienceSeparator==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.trasporto.audience.separator"); 

				if (value != null){
					value = value.trim();
					if(value.length()<1 || value.length()>1) {
						throw new Exception("Atteso un carattere, trovati "+value.length());
					}
					OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_audienceSeparator = value.charAt(0);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.audience.separator' non impostata, viene utilizzato il default=,");
					OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_audienceSeparator = ',';
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.audience.separator' non impostata, viene utilizzato il default=, ; errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_audienceSeparator = ',';
			}
		}

		return OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_audienceSeparator;
	}
	
	private static Character gestioneTokenHeaderIntegrazioneTrasporto_scopeSeparator = null;
	public Character getGestioneTokenHeaderIntegrazioneTrasporto_scopeSeparator(){

		if(OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_scopeSeparator==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.trasporto.scopes.separator"); 

				if (value != null){
					value = value.trim();
					if(value.length()<1 || value.length()>1) {
						throw new Exception("Atteso un carattere, trovati "+value.length());
					}
					OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_scopeSeparator = value.charAt(0);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.scopes.separator' non impostata, viene utilizzato il default=,");
					OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_scopeSeparator = ',';
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.scopes.separator' non impostata, viene utilizzato il default=, ; errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_scopeSeparator = ',';
			}
		}

		return OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_scopeSeparator;
	}
	
	private static Character gestioneTokenHeaderIntegrazioneTrasporto_roleSeparator = null;
	public Character getGestioneTokenHeaderIntegrazioneTrasporto_roleSeparator(){

		if(OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_roleSeparator==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.trasporto.roles.separator"); 

				if (value != null){
					value = value.trim();
					if(value.length()<1 || value.length()>1) {
						throw new Exception("Atteso un carattere, trovati "+value.length());
					}
					OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_roleSeparator = value.charAt(0);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.roles.separator' non impostata, viene utilizzato il default=,");
					OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_roleSeparator = ',';
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.gestioneToken.forward.trasporto.roles.separator' non impostata, viene utilizzato il default=, ; errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_roleSeparator = ',';
			}
		}

		return OpenSPCoop2Properties.gestioneTokenHeaderIntegrazioneTrasporto_roleSeparator;
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, String> getCustomClaims_name_gestioneTokenHeaderIntegrazione = null;
	public String getCustomClaimsName_gestioneTokenHeaderIntegrazione(String claimName) throws Exception {	
		if(OpenSPCoop2Properties.getCustomClaims_name_gestioneTokenHeaderIntegrazione==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				getCustomClaims_name_gestioneTokenHeaderIntegrazione = new java.util.concurrent.ConcurrentHashMap<String, String>();
				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.custom.");
				if(prop!=null && !prop.isEmpty()) {
					Enumeration<?> en = prop.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						if(key.contains(".")) {
							String keySub = key.substring(key.indexOf(".")+1); 
							if("name".equals(keySub)) {
								String tmp = prop.getProperty(key);
								if(tmp!=null) {
									String claimNameTmp = key.substring(0, key.indexOf("."));
									OpenSPCoop2Properties.getCustomClaims_name_gestioneTokenHeaderIntegrazione.put(claimNameTmp, tmp.trim());
								}
								else {
									throw new Exception("Proprietà non definita");
								}
							}
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.custom."+claimName+".trasporto.name': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getCustomClaims_name_gestioneTokenHeaderIntegrazione.get(claimName);
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, String> getCustomClaims_headerName_gestioneTokenHeaderIntegrazioneTrasporto = null;
	public String getCustomClaimsHeaderName_gestioneTokenHeaderIntegrazioneTrasporto(String claimName) throws Exception {	
		if(OpenSPCoop2Properties.getCustomClaims_headerName_gestioneTokenHeaderIntegrazioneTrasporto==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				getCustomClaims_headerName_gestioneTokenHeaderIntegrazioneTrasporto = new java.util.concurrent.ConcurrentHashMap<String, String>();
				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.custom.");
				if(prop!=null && !prop.isEmpty()) {
					Enumeration<?> en = prop.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						if(key.contains(".")) {
							String keySub = key.substring(key.indexOf(".")+1); 
							if("trasporto.name".equals(keySub)) {
								String tmp = prop.getProperty(key);
								if(tmp!=null) {
									String claimNameTmp = key.substring(0, key.indexOf("."));
									OpenSPCoop2Properties.getCustomClaims_headerName_gestioneTokenHeaderIntegrazioneTrasporto.put(claimNameTmp, tmp.trim());
								}
								else {
									throw new Exception("Proprietà non definita");
								}
							}
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.custom."+claimName+".trasporto.name': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getCustomClaims_headerName_gestioneTokenHeaderIntegrazioneTrasporto.get(claimName);
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPD = null;
	public Boolean getCustomClaimsKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneTrasporto(String claimName) throws Exception {	
		if(OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPD==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPD = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.custom.");
				if(prop!=null && !prop.isEmpty()) {
					Enumeration<?> en = prop.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						if(key.contains(".")) {
							String keySub = key.substring(key.indexOf(".")+1); 
							if("trasporto.pd.set.enabled".equals(keySub)) {
								String tmp = prop.getProperty(key);
								if(tmp!=null) {
									try {
										boolean b = Boolean.parseBoolean(tmp.trim());
										String claimNameTmp = key.substring(0, key.indexOf("."));
										OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPD.put(claimNameTmp, b);
									}catch(Exception e) {
										throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.gestioneToken.forward.custom."+key+"' (atteso: true/false): "+e.getMessage(),e);
									}
								}
								else {
									throw new Exception("Proprietà non definita");
								}
							}
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.custom."+claimName+".trasporto.pd.set.enabled': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPD.get(claimName);
	}

	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPA = null;
	public Boolean getCustomClaimsKeyPASetEnabled_gestioneTokenHeaderIntegrazioneTrasporto(String claimName) throws Exception {	
		if(OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPA==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPA = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.custom.");
				if(prop!=null && !prop.isEmpty()) {
					Enumeration<?> en = prop.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						if(key.contains(".")) {
							String keySub = key.substring(key.indexOf(".")+1); 
							if("trasporto.pa.set.enabled".equals(keySub)) {
								String tmp = prop.getProperty(key);
								if(tmp!=null) {
									try {
										boolean b = Boolean.parseBoolean(tmp.trim());
										String claimNameTmp = key.substring(0, key.indexOf("."));
										OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPA.put(claimNameTmp, b);
									}catch(Exception e) {
										throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.gestioneToken.forward.custom."+key+"' (atteso: true/false): "+e.getMessage(),e);
									}
								}
								else {
									throw new Exception("Proprietà non definita");
								}
							}
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.custom."+claimName+".trasporto.pa.set.enabled': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneTrasporto_setPA.get(claimName);
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, String> getCustomClaims_jsonPropertyName_gestioneTokenHeaderIntegrazioneJson = null;
	public String getCustomClaimsJsonPropertyName_gestioneTokenHeaderIntegrazioneJson(String claimName) throws Exception {	
		if(OpenSPCoop2Properties.getCustomClaims_jsonPropertyName_gestioneTokenHeaderIntegrazioneJson==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				getCustomClaims_jsonPropertyName_gestioneTokenHeaderIntegrazioneJson = new java.util.concurrent.ConcurrentHashMap<String, String>();
				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.custom.");
				if(prop!=null && !prop.isEmpty()) {
					Enumeration<?> en = prop.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						if(key.contains(".")) {
							String keySub = key.substring(key.indexOf(".")+1); 
							if("json.name".equals(keySub)) {
								String tmp = prop.getProperty(key);
								if(tmp!=null) {
									String claimNameTmp = key.substring(0, key.indexOf("."));
									OpenSPCoop2Properties.getCustomClaims_jsonPropertyName_gestioneTokenHeaderIntegrazioneJson.put(claimNameTmp, tmp.trim());
								}
								else {
									throw new Exception("Proprietà non definita");
								}
							}
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.custom."+claimName+".json.name': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getCustomClaims_jsonPropertyName_gestioneTokenHeaderIntegrazioneJson.get(claimName);
	}
	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneJson_setPD = null;
	public Boolean getCustomClaimsKeyPDSetEnabled_gestioneTokenHeaderIntegrazioneJson(String claimName) throws Exception {	
		if(OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneJson_setPD==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneJson_setPD = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.custom.");
				if(prop!=null && !prop.isEmpty()) {
					Enumeration<?> en = prop.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						if(key.contains(".")) {
							String keySub = key.substring(key.indexOf(".")+1); 
							if("json.pd.set.enabled".equals(keySub)) {
								String tmp = prop.getProperty(key);
								if(tmp!=null) {
									try {
										boolean b = Boolean.parseBoolean(tmp.trim());
										String claimNameTmp = key.substring(0, key.indexOf("."));
										OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneJson_setPD.put(claimNameTmp, b);
									}catch(Exception e) {
										throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.gestioneToken.forward.custom."+key+"' (atteso: true/false): "+e.getMessage(),e);
									}
								}
								else {
									throw new Exception("Proprietà non definita");
								}
							}
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.custom."+claimName+".json.pd.set.enabled': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneJson_setPD.get(claimName);
	}

	
	private static java.util.concurrent.ConcurrentHashMap<String, Boolean> getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneJson_setPA = null;
	public Boolean getCustomClaimsKeyPASetEnabled_gestioneTokenHeaderIntegrazioneJson(String claimName) throws Exception {	
		if(OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneJson_setPA==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneJson_setPA = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.gestioneToken.forward.custom.");
				if(prop!=null && !prop.isEmpty()) {
					Enumeration<?> en = prop.keys();
					while (en.hasMoreElements()) {
						String key = (String) en.nextElement();
						if(key.contains(".")) {
							String keySub = key.substring(key.indexOf(".")+1); 
							if("json.pa.set.enabled".equals(keySub)) {
								String tmp = prop.getProperty(key);
								if(tmp!=null) {
									try {
										boolean b = Boolean.parseBoolean(tmp.trim());
										String claimNameTmp = key.substring(0, key.indexOf("."));
										OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneJson_setPA.put(claimNameTmp, b);
									}catch(Exception e) {
										throw new Exception("Rilevato errore durante il parsing della property 'org.openspcoop2.pdd.gestioneToken.forward.custom."+key+"' (atteso: true/false): "+e.getMessage(),e);
									}
								}
								else {
									throw new Exception("Proprietà non definita");
								}
							}
						}
					}
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.gestioneToken.forward.custom."+claimName+".json.pa.set.enabled': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getCustomClaims_keyValue_gestioneTokenHeaderIntegrazioneJson_setPA.get(claimName);
	}
	
	
	
	/* ------------- JMX Statistiche ---------------------*/
	
	private static Boolean isStatisticheViaJmx = null;
	public boolean isStatisticheViaJmx(){

		if(OpenSPCoop2Properties.isStatisticheViaJmx==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.jmx.statistiche"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isStatisticheViaJmx = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.jmx.statistiche' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isStatisticheViaJmx = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.jmx.statistiche' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheViaJmx = false;
			}
		}

		return OpenSPCoop2Properties.isStatisticheViaJmx;
	}
	
	
	
	/* ------------- REST / SOAP Trasporto Utils ---------------------*/
	
	private void _list_add(List<String> tmp, List<String> addList) {
		if(tmp!=null && tmp.size()>0) {
			for (String hdr : tmp) {
				if(addList.contains(hdr)==false) {
					addList.add(hdr);
				}
			}
		}
	}
	
	/* ------------- Forward Proxy ---------------------*/
	
	private static Boolean isForwardProxyEnable = null;
	public boolean isForwardProxyEnable(){

		String pName = "org.openspcoop2.pdd.connettori.govwayProxy.enable";
		if(OpenSPCoop2Properties.isForwardProxyEnable==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isForwardProxyEnable = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isForwardProxyEnable = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isForwardProxyEnable = false;
			}
		}

		return OpenSPCoop2Properties.isForwardProxyEnable;
	}
	
	
	private static Boolean isForwardProxyHeaderEnable = null;
	private boolean isForwardProxyHeaderEnable(){

		String pName = "org.openspcoop2.pdd.connettori.govwayProxy.header.enable";
		if(OpenSPCoop2Properties.isForwardProxyHeaderEnable==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isForwardProxyHeaderEnable = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isForwardProxyHeaderEnable = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isForwardProxyHeaderEnable = false;
			}
		}

		return OpenSPCoop2Properties.isForwardProxyHeaderEnable;
	}
	
	private static String getForwardProxyHeaderName = null;
	private String getForwardProxyHeaderName(){

		String pName = "org.openspcoop2.pdd.connettori.govwayProxy.header.nome";
		if(OpenSPCoop2Properties.getForwardProxyHeaderName==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.getForwardProxyHeaderName = value;
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER);
					OpenSPCoop2Properties.getForwardProxyHeaderName = ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getForwardProxyHeaderName = ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER;
			}
		}

		return OpenSPCoop2Properties.getForwardProxyHeaderName;
	}
	
	private static Boolean isForwardProxyHeaderBase64 = null;
	private boolean isForwardProxyHeaderBase64(){

		String pName = "org.openspcoop2.pdd.connettori.govwayProxy.header.base64";
		if(OpenSPCoop2Properties.isForwardProxyHeaderBase64==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isForwardProxyHeaderBase64 = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER_BASE64);
					OpenSPCoop2Properties.isForwardProxyHeaderBase64 = ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER_BASE64;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER_BASE64+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isForwardProxyHeaderBase64 = ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER_BASE64;
			}
		}

		return OpenSPCoop2Properties.isForwardProxyHeaderBase64;
	}
	
	
	private static Boolean isForwardProxyQueryParameterEnable = null;
	private boolean isForwardProxyQueryParameterEnable(){

		String pName = "org.openspcoop2.pdd.connettori.govwayProxy.urlParameter.enable";
		if(OpenSPCoop2Properties.isForwardProxyQueryParameterEnable==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isForwardProxyQueryParameterEnable = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isForwardProxyQueryParameterEnable = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isForwardProxyQueryParameterEnable = false;
			}
		}

		return OpenSPCoop2Properties.isForwardProxyQueryParameterEnable;
	}
	
	private static String getForwardProxyQueryParameterName = null;
	private String getForwardProxyQueryParameterName(){

		String pName = "org.openspcoop2.pdd.connettori.govwayProxy.urlParameter.nome";
		if(OpenSPCoop2Properties.getForwardProxyQueryParameterName==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.getForwardProxyQueryParameterName = value;
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER);
					OpenSPCoop2Properties.getForwardProxyQueryParameterName = ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getForwardProxyQueryParameterName = ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER;
			}
		}

		return OpenSPCoop2Properties.getForwardProxyQueryParameterName;
	}
	
	private static Boolean isForwardProxyQueryParameterBase64 = null;
	private boolean isForwardProxyQueryParameterBase64(){

		String pName = "org.openspcoop2.pdd.connettori.govwayProxy.urlParameter.base64";
		if(OpenSPCoop2Properties.isForwardProxyQueryParameterBase64==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties(pName); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isForwardProxyQueryParameterBase64 = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER_BASE64);
					OpenSPCoop2Properties.isForwardProxyQueryParameterBase64 = ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER_BASE64;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default="+ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER_BASE64+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isForwardProxyQueryParameterBase64 = ForwardProxy.DEFAULT_GOVWAY_PROXY_HEADER_BASE64;
			}
		}

		return OpenSPCoop2Properties.isForwardProxyQueryParameterBase64;
	}
	
	public ForwardProxyConfigurazione getForwardProxyConfigurazioneDefault() {
		ForwardProxyConfigurazione config = new ForwardProxyConfigurazione();
		if(this.isForwardProxyHeaderEnable()) {
			config.setHeader(this.getForwardProxyHeaderName());
			config.setHeaderBase64(this.isForwardProxyHeaderBase64());
		}
		if(this.isForwardProxyQueryParameterEnable()) {
			config.setQuery(this.getForwardProxyQueryParameterName());
			config.setQueryBase64(this.isForwardProxyQueryParameterBase64());
		}
		return config;
	}

	
	/* ------------- SOAP (Trasporto - URLParameters) ---------------------*/
	
	private static Boolean isSOAPServicesUrlParametersForward = null;
	private boolean isSOAPServicesUrlParametersForward(){

		if(OpenSPCoop2Properties.isSOAPServicesUrlParametersForward==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.urlParameters.forward.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isSOAPServicesUrlParametersForward = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.soap.urlParameters.forward.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isSOAPServicesUrlParametersForward = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.soap.urlParameters.forward.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isSOAPServicesUrlParametersForward = true;
			}
		}

		return OpenSPCoop2Properties.isSOAPServicesUrlParametersForward;
	}
	
	private static Boolean getSOAPServicesBlackListInternalUrlParametersRead = null;
	private static List<String> getSOAPServicesBlackListInternalUrlParametersList = null;
	private List<String> getSOAPServicesBlackListInternalUrlParameters() {	
		if(OpenSPCoop2Properties.getSOAPServicesBlackListInternalUrlParametersRead==null){
			try{ 
				getSOAPServicesBlackListInternalUrlParametersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.urlParameters.blackList.internal");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesBlackListInternalUrlParametersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesBlackListInternalUrlParametersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.urlParameters.blackList.internal': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesBlackListInternalUrlParametersList;
	}
	
	private static Boolean getSOAPServicesBlackListUrlParametersRead = null;
	private static List<String> getSOAPServicesBlackListUrlParametersList = null;
	private List<String> getSOAPServicesBlackListUrlParameters() {	
		if(OpenSPCoop2Properties.getSOAPServicesBlackListUrlParametersRead==null){
			try{ 
				getSOAPServicesBlackListUrlParametersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.urlParameters.blackList");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesBlackListUrlParametersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesBlackListUrlParametersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.urlParameters.blackList': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesBlackListUrlParametersList;
	}
	
	private static Boolean getSOAPServicesWhiteListUrlParametersRead = null;
	private static List<String> getSOAPServicesWhiteListUrlParametersList = null;
	private List<String> getSOAPServicesWhiteListUrlParameters() {	
		if(OpenSPCoop2Properties.getSOAPServicesWhiteListUrlParametersRead==null){
			try{ 
				getSOAPServicesWhiteListUrlParametersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.urlParameters.whiteList");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesWhiteListUrlParametersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesWhiteListUrlParametersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.urlParameters.whiteList': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesWhiteListUrlParametersList;
	}
	
	private List<String> _getSOAPServicesBlackListUrlParameters() {	
		
		List<String> blackList = new ArrayList<>();
		List<String> tmp = this.getSOAPServicesBlackListInternalUrlParameters();
		this._list_add(tmp, blackList);
		tmp = this.getSOAPServicesBlackListUrlParameters();
		this._list_add(tmp, blackList);
		
		return blackList;
	}
	private List<String> _getSOAPServicesWhiteListUrlParameters() {	
		
		List<String> whiteList = new ArrayList<>();
		List<String> tmp = this.getSOAPServicesWhiteListUrlParameters();
		this._list_add(tmp, whiteList);
		
		return whiteList;
	}
	public ForwardConfig getSOAPServicesUrlParametersForwardConfig() {
		ForwardConfig f = new ForwardConfig();
		f.setForwardEnable(this.isSOAPServicesUrlParametersForward());
		f.setBlackList(_getSOAPServicesBlackListUrlParameters());
		f.setWhiteList(_getSOAPServicesWhiteListUrlParameters());
		return f;
	}
	
	
	
	
	/* ------------- SOAP (Trasporto - Headers) ---------------------*/
	
	private static Boolean isSOAPServicesHeadersForward = null;
	private boolean isSOAPServicesHeadersForward(){

		if(OpenSPCoop2Properties.isSOAPServicesHeadersForward==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.headers.forward.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isSOAPServicesHeadersForward = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.forward.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isSOAPServicesHeadersForward = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.forward.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isSOAPServicesHeadersForward = true;
			}
		}

		return OpenSPCoop2Properties.isSOAPServicesHeadersForward;
	}
	
	private static Boolean getSOAPServicesBlackListBothInternalHeadersRead = null;
	private static List<String> getSOAPServicesBlackListBothInternalHeadersList = null;
	private List<String> getSOAPServicesBlackListBothInternalHeaders() {	
		if(OpenSPCoop2Properties.getSOAPServicesBlackListBothInternalHeadersRead==null){
			try{ 
				getSOAPServicesBlackListBothInternalHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.headers.blackList.internal.both");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesBlackListBothInternalHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesBlackListBothInternalHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.blackList.internal.both': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesBlackListBothInternalHeadersList;
	}
	
	private static Boolean getSOAPServicesBlackListRequestInternalHeadersRead = null;
	private static List<String> getSOAPServicesBlackListRequestInternalHeadersList = null;
	private List<String> getSOAPServicesBlackListRequestInternalHeaders() {	
		if(OpenSPCoop2Properties.getSOAPServicesBlackListRequestInternalHeadersRead==null){
			try{ 
				getSOAPServicesBlackListRequestInternalHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.headers.blackList.internal.request");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesBlackListRequestInternalHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesBlackListRequestInternalHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.blackList.internal.request': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesBlackListRequestInternalHeadersList;
	}
	
	private static Boolean getSOAPServicesBlackListResponseInternalHeadersRead = null;
	private static List<String> getSOAPServicesBlackListResponseInternalHeadersList = null;
	private List<String> getSOAPServicesBlackListResponseInternalHeaders() {	
		if(OpenSPCoop2Properties.getSOAPServicesBlackListResponseInternalHeadersRead==null){
			try{ 
				getSOAPServicesBlackListResponseInternalHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.headers.blackList.internal.response");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesBlackListResponseInternalHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesBlackListResponseInternalHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.blackList.internal.response': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesBlackListResponseInternalHeadersList;
	}
	
	private static Boolean getSOAPServicesBlackListBothHeadersRead = null;
	private static List<String> getSOAPServicesBlackListBothHeadersList = null;
	private List<String> getSOAPServicesBlackListBothHeaders() {	
		if(OpenSPCoop2Properties.getSOAPServicesBlackListBothHeadersRead==null){
			try{ 
				getSOAPServicesBlackListBothHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.headers.blackList.both");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesBlackListBothHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesBlackListBothHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.blackList.both': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesBlackListBothHeadersList;
	}
	
	private static Boolean getSOAPServicesBlackListRequestHeadersRead = null;
	private static List<String> getSOAPServicesBlackListRequestHeadersList = null;
	private List<String> getSOAPServicesBlackListRequestHeaders() {	
		if(OpenSPCoop2Properties.getSOAPServicesBlackListRequestHeadersRead==null){
			try{ 
				getSOAPServicesBlackListRequestHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.headers.blackList.request");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesBlackListRequestHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesBlackListRequestHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.blackList.request': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesBlackListRequestHeadersList;
	}
	
	private static Boolean getSOAPServicesBlackListResponseHeadersRead = null;
	private static List<String> getSOAPServicesBlackListResponseHeadersList = null;
	private List<String> getSOAPServicesBlackListResponseHeaders() {	
		if(OpenSPCoop2Properties.getSOAPServicesBlackListResponseHeadersRead==null){
			try{ 
				getSOAPServicesBlackListResponseHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.headers.blackList.response");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesBlackListResponseHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesBlackListResponseHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.blackList.response': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesBlackListResponseHeadersList;
	}
	
	private static Boolean getSOAPServicesWhiteListBothHeadersRead = null;
	private static List<String> getSOAPServicesWhiteListBothHeadersList = null;
	private List<String> getSOAPServicesWhiteListBothHeaders() {	
		if(OpenSPCoop2Properties.getSOAPServicesWhiteListBothHeadersRead==null){
			try{ 
				getSOAPServicesWhiteListBothHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.headers.whiteList.both");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesWhiteListBothHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesWhiteListBothHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.whiteList.both': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesWhiteListBothHeadersList;
	}
	
	private static Boolean getSOAPServicesWhiteListRequestHeadersRead = null;
	private static List<String> getSOAPServicesWhiteListRequestHeadersList = null;
	private List<String> getSOAPServicesWhiteListRequestHeaders() {	
		if(OpenSPCoop2Properties.getSOAPServicesWhiteListRequestHeadersRead==null){
			try{ 
				getSOAPServicesWhiteListRequestHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.headers.whiteList.request");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesWhiteListRequestHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesWhiteListRequestHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.whiteList.request': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesWhiteListRequestHeadersList;
	}
	
	private static Boolean getSOAPServicesWhiteListResponseHeadersRead = null;
	private static List<String> getSOAPServicesWhiteListResponseHeadersList = null;
	private List<String> getSOAPServicesWhiteListResponseHeaders() {	
		if(OpenSPCoop2Properties.getSOAPServicesWhiteListResponseHeadersRead==null){
			try{ 
				getSOAPServicesWhiteListResponseHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.soap.headers.whiteList.response");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getSOAPServicesWhiteListResponseHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getSOAPServicesWhiteListResponseHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.soap.headers.whiteList.response': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getSOAPServicesWhiteListResponseHeadersList;
	}
	
	private List<String> getSOAPServicesBlackListHeaders(boolean request) {	
		
		List<String> blackList = new ArrayList<>();
		List<String> tmp = this.getSOAPServicesBlackListBothInternalHeaders();
		this._list_add(tmp, blackList);
		tmp = this.getSOAPServicesBlackListBothHeaders();
		this._list_add(tmp, blackList);
		
		if(request) {
			tmp = this.getSOAPServicesBlackListRequestInternalHeaders();
			this._list_add(tmp, blackList);
			tmp = this.getSOAPServicesBlackListRequestHeaders();
			this._list_add(tmp, blackList);
		}
		else {
			tmp = this.getSOAPServicesBlackListResponseInternalHeaders();
			this._list_add(tmp, blackList);
			tmp = this.getSOAPServicesBlackListResponseHeaders();
			this._list_add(tmp, blackList);
		}
		
		return blackList;
	}
	private List<String> getSOAPServicesWhiteListHeaders(boolean request) {	
		
		List<String> whiteList = new ArrayList<>();
		List<String> tmp = this.getSOAPServicesWhiteListBothHeaders();
		this._list_add(tmp, whiteList);
		if(request) {
			tmp = this.getSOAPServicesWhiteListRequestHeaders();
			this._list_add(tmp, whiteList);
		}
		else {
			tmp = this.getSOAPServicesWhiteListResponseHeaders();
			this._list_add(tmp, whiteList);
		}
		
		return whiteList;
	}
	public ForwardConfig getSOAPServicesHeadersForwardConfig(boolean request) {
		ForwardConfig f = new ForwardConfig();
		f.setForwardEnable(this.isSOAPServicesHeadersForward());
		f.setBlackList(getSOAPServicesBlackListHeaders(request));
		f.setWhiteList(getSOAPServicesWhiteListHeaders(request));
		return f;
	}

	
	
	
	/* ------------- REST (Trasporto - URLParameters) ---------------------*/
	
	private static Boolean isRESTServicesUrlParametersForward = null;
	private boolean isRESTServicesUrlParametersForward(){

		if(OpenSPCoop2Properties.isRESTServicesUrlParametersForward==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.urlParameters.forward.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isRESTServicesUrlParametersForward = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.rest.urlParameters.forward.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isRESTServicesUrlParametersForward = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.rest.urlParameters.forward.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isRESTServicesUrlParametersForward = true;
			}
		}

		return OpenSPCoop2Properties.isRESTServicesUrlParametersForward;
	}
	
	private static Boolean getRESTServicesBlackListInternalUrlParametersRead = null;
	private static List<String> getRESTServicesBlackListInternalUrlParametersList = null;
	private List<String> getRESTServicesBlackListInternalUrlParameters() {	
		if(OpenSPCoop2Properties.getRESTServicesBlackListInternalUrlParametersRead==null){
			try{ 
				getRESTServicesBlackListInternalUrlParametersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.urlParameters.blackList.internal");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesBlackListInternalUrlParametersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesBlackListInternalUrlParametersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.urlParameters.blackList.internal': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesBlackListInternalUrlParametersList;
	}
	
	private static Boolean getRESTServicesBlackListUrlParametersRead = null;
	private static List<String> getRESTServicesBlackListUrlParametersList = null;
	private List<String> getRESTServicesBlackListUrlParameters() {	
		if(OpenSPCoop2Properties.getRESTServicesBlackListUrlParametersRead==null){
			try{ 
				getRESTServicesBlackListUrlParametersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.urlParameters.blackList");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesBlackListUrlParametersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesBlackListUrlParametersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.urlParameters.blackList': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesBlackListUrlParametersList;
	}
	
	private static Boolean getRESTServicesWhiteListUrlParametersRead = null;
	private static List<String> getRESTServicesWhiteListUrlParametersList = null;
	private List<String> getRESTServicesWhiteListUrlParameters() {	
		if(OpenSPCoop2Properties.getRESTServicesWhiteListUrlParametersRead==null){
			try{ 
				getRESTServicesWhiteListUrlParametersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.urlParameters.whiteList");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesWhiteListUrlParametersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesWhiteListUrlParametersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.urlParameters.whiteList': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesWhiteListUrlParametersList;
	}
		
	private List<String> _getRESTServicesBlackListUrlParameters() {	
		
		List<String> blackList = new ArrayList<>();
		List<String> tmp = this.getRESTServicesBlackListInternalUrlParameters();
		this._list_add(tmp, blackList);
		tmp = this.getRESTServicesBlackListUrlParameters();
		this._list_add(tmp, blackList);
		
		return blackList;
	}
	private List<String> _getRESTServicesWhiteListUrlParameters() {	
		
		List<String> whiteList = new ArrayList<>();
		List<String> tmp = this.getRESTServicesWhiteListUrlParameters();
		this._list_add(tmp, whiteList);
		
		return whiteList;
	}
	public ForwardConfig getRESTServicesUrlParametersForwardConfig() {
		ForwardConfig f = new ForwardConfig();
		f.setForwardEnable(this.isRESTServicesUrlParametersForward());
		f.setBlackList(_getRESTServicesBlackListUrlParameters());
		f.setWhiteList(_getRESTServicesWhiteListUrlParameters());
		return f;
	}
	
	
	
	
	/* ------------- REST (Trasporto - Headers) ---------------------*/
	
	private static Boolean isRESTServicesHeadersForward = null;
	private boolean isRESTServicesHeadersForward(){

		if(OpenSPCoop2Properties.isRESTServicesHeadersForward==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.headers.forward.enable"); 

				if (value != null){
					value = value.trim();
					OpenSPCoop2Properties.isRESTServicesHeadersForward = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.forward.enable' non impostata, viene utilizzato il default=true");
					OpenSPCoop2Properties.isRESTServicesHeadersForward = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.forward.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.isRESTServicesHeadersForward = true;
			}
		}

		return OpenSPCoop2Properties.isRESTServicesHeadersForward;
	}
	
	private static Boolean getRESTServicesBlackListBothInternalHeadersRead = null;
	private static List<String> getRESTServicesBlackListBothInternalHeadersList = null;
	private List<String> getRESTServicesBlackListBothInternalHeaders() {	
		if(OpenSPCoop2Properties.getRESTServicesBlackListBothInternalHeadersRead==null){
			try{ 
				getRESTServicesBlackListBothInternalHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.headers.blackList.internal.both");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesBlackListBothInternalHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesBlackListBothInternalHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.blackList.internal.both': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesBlackListBothInternalHeadersList;
	}
	
	private static Boolean getRESTServicesBlackListRequestInternalHeadersRead = null;
	private static List<String> getRESTServicesBlackListRequestInternalHeadersList = null;
	private List<String> getRESTServicesBlackListRequestInternalHeaders() {	
		if(OpenSPCoop2Properties.getRESTServicesBlackListRequestInternalHeadersRead==null){
			try{ 
				getRESTServicesBlackListRequestInternalHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.headers.blackList.internal.request");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesBlackListRequestInternalHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesBlackListRequestInternalHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.blackList.internal.request': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesBlackListRequestInternalHeadersList;
	}
	
	private static Boolean getRESTServicesBlackListResponseInternalHeadersRead = null;
	private static List<String> getRESTServicesBlackListResponseInternalHeadersList = null;
	private List<String> getRESTServicesBlackListResponseInternalHeaders() {	
		if(OpenSPCoop2Properties.getRESTServicesBlackListResponseInternalHeadersRead==null){
			try{ 
				getRESTServicesBlackListResponseInternalHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.headers.blackList.internal.response");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesBlackListResponseInternalHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesBlackListResponseInternalHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.blackList.internal.response': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesBlackListResponseInternalHeadersList;
	}
	
	private static Boolean getRESTServicesBlackListBothHeadersRead = null;
	private static List<String> getRESTServicesBlackListBothHeadersList = null;
	private List<String> getRESTServicesBlackListBothHeaders() {	
		if(OpenSPCoop2Properties.getRESTServicesBlackListBothHeadersRead==null){
			try{ 
				getRESTServicesBlackListBothHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.headers.blackList.both");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesBlackListBothHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesBlackListBothHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.blackList.both': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesBlackListBothHeadersList;
	}
	
	private static Boolean getRESTServicesBlackListRequestHeadersRead = null;
	private static List<String> getRESTServicesBlackListRequestHeadersList = null;
	private List<String> getRESTServicesBlackListRequestHeaders() {	
		if(OpenSPCoop2Properties.getRESTServicesBlackListRequestHeadersRead==null){
			try{ 
				getRESTServicesBlackListRequestHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.headers.blackList.request");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesBlackListRequestHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesBlackListRequestHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.blackList.request': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesBlackListRequestHeadersList;
	}
	
	private static Boolean getRESTServicesBlackListResponseHeadersRead = null;
	private static List<String> getRESTServicesBlackListResponseHeadersList = null;
	private List<String> getRESTServicesBlackListResponseHeaders() {	
		if(OpenSPCoop2Properties.getRESTServicesBlackListResponseHeadersRead==null){
			try{ 
				getRESTServicesBlackListResponseHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.headers.blackList.response");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesBlackListResponseHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesBlackListResponseHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.blackList.response': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesBlackListResponseHeadersList;
	}
	
	private static Boolean getRESTServicesWhiteListBothHeadersRead = null;
	private static List<String> getRESTServicesWhiteListBothHeadersList = null;
	private List<String> getRESTServicesWhiteListBothHeaders() {	
		if(OpenSPCoop2Properties.getRESTServicesWhiteListBothHeadersRead==null){
			try{ 
				getRESTServicesWhiteListBothHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.headers.whiteList.both");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesWhiteListBothHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesWhiteListBothHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.whiteList.both': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesWhiteListBothHeadersList;
	}
	
	private static Boolean getRESTServicesWhiteListRequestHeadersRead = null;
	private static List<String> getRESTServicesWhiteListRequestHeadersList = null;
	private List<String> getRESTServicesWhiteListRequestHeaders() {	
		if(OpenSPCoop2Properties.getRESTServicesWhiteListRequestHeadersRead==null){
			try{ 
				getRESTServicesWhiteListRequestHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.headers.whiteList.request");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesWhiteListRequestHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesWhiteListRequestHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.whiteList.request': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesWhiteListRequestHeadersList;
	}
	
	private static Boolean getRESTServicesWhiteListResponseHeadersRead = null;
	private static List<String> getRESTServicesWhiteListResponseHeadersList = null;
	private List<String> getRESTServicesWhiteListResponseHeaders() {	
		if(OpenSPCoop2Properties.getRESTServicesWhiteListResponseHeadersRead==null){
			try{ 
				getRESTServicesWhiteListResponseHeadersList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.headers.whiteList.response");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getRESTServicesWhiteListResponseHeadersList.add(split[i].trim());
						}
					}
				}
				OpenSPCoop2Properties.getRESTServicesWhiteListResponseHeadersRead = true;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.headers.whiteList.response': "+e.getMessage(),e);
			}    
		}
		
		return OpenSPCoop2Properties.getRESTServicesWhiteListResponseHeadersList;
	}
	
	private List<String> getRESTServicesBlackListHeaders(boolean request) {	
		
		List<String> blackList = new ArrayList<>();
		List<String> tmp = this.getRESTServicesBlackListBothInternalHeaders();
		this._list_add(tmp, blackList);
		tmp = this.getRESTServicesBlackListBothHeaders();
		this._list_add(tmp, blackList);
		
		if(request) {
			tmp = this.getRESTServicesBlackListRequestInternalHeaders();
			this._list_add(tmp, blackList);
			tmp = this.getRESTServicesBlackListRequestHeaders();
			this._list_add(tmp, blackList);
		}
		else {
			tmp = this.getRESTServicesBlackListResponseInternalHeaders();
			this._list_add(tmp, blackList);
			tmp = this.getRESTServicesBlackListResponseHeaders();
			this._list_add(tmp, blackList);
		}
		
		return blackList;
	}
	private List<String> getRESTServicesWhiteListHeaders(boolean request) {	
		
		List<String> whiteList = new ArrayList<>();
		List<String> tmp = this.getRESTServicesWhiteListBothHeaders();
		this._list_add(tmp, whiteList);
		if(request) {
			tmp = this.getRESTServicesWhiteListRequestHeaders();
			this._list_add(tmp, whiteList);
		}
		else {
			tmp = this.getRESTServicesWhiteListResponseHeaders();
			this._list_add(tmp, whiteList);
		}
		
		return whiteList;
	}
	public ForwardConfig getRESTServicesHeadersForwardConfig(boolean request) {
		ForwardConfig f = new ForwardConfig();
		f.setForwardEnable(this.isRESTServicesHeadersForward());
		f.setBlackList(getRESTServicesBlackListHeaders(request));
		f.setWhiteList(getRESTServicesWhiteListHeaders(request));
		return f;
	}


	/* ------------- REST (Trasporto - Headers - Proxy Pass Reverse) ---------------------*/
	
	private static Boolean isRESTServices_inoltroBuste_proxyPassReverse = null;
	public boolean isRESTServices_inoltroBuste_proxyPassReverse() {	
		if(OpenSPCoop2Properties.isRESTServices_inoltroBuste_proxyPassReverse==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.connettori.inoltroBuste.proxyPassReverse");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.rest.connettori.inoltroBuste.proxyPassReverse' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isRESTServices_inoltroBuste_proxyPassReverse = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.connettori.inoltroBuste.proxyPassReverse': "+e.getMessage(),e);
				OpenSPCoop2Properties.isRESTServices_inoltroBuste_proxyPassReverse = false;
			}    
		}

		return OpenSPCoop2Properties.isRESTServices_inoltroBuste_proxyPassReverse;
	}
	
	private static Boolean isRESTServices_consegnaContenutiApplicativi_proxyPassReverse = null;
	public boolean isRESTServices_consegnaContenutiApplicativi_proxyPassReverse() {	
		if(OpenSPCoop2Properties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.connettori.consegnaContenutiApplicativi.proxyPassReverse");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.rest.connettori.consegnaContenutiApplicativi.proxyPassReverse' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.connettori.consegnaContenutiApplicativi.proxyPassReverse': "+e.getMessage(),e);
				OpenSPCoop2Properties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse = false;
			}    
		}

		return OpenSPCoop2Properties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse;
	}
	
	private static Boolean isRESTServices_inoltroBuste_proxyPassReverse_useProtocolPrefix = null;
	public boolean isRESTServices_inoltroBuste_proxyPassReverse_useProtocolPrefix() {	
		if(OpenSPCoop2Properties.isRESTServices_inoltroBuste_proxyPassReverse_useProtocolPrefix==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.connettori.inoltroBuste.proxyPassReverse.useProtocolPrefix");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.rest.connettori.inoltroBuste.proxyPassReverse.useProtocolPrefix' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isRESTServices_inoltroBuste_proxyPassReverse_useProtocolPrefix = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.connettori.inoltroBuste.proxyPassReverse.useProtocolPrefix': "+e.getMessage(),e);
				OpenSPCoop2Properties.isRESTServices_inoltroBuste_proxyPassReverse_useProtocolPrefix = false;
			}    
		}

		return OpenSPCoop2Properties.isRESTServices_inoltroBuste_proxyPassReverse_useProtocolPrefix;
	}
	
	private static Boolean isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix = null;
	public boolean isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix() {	
		if(OpenSPCoop2Properties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.connettori.consegnaContenutiApplicativi.proxyPassReverse.useProtocolPrefix");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.rest.connettori.consegnaContenutiApplicativi.proxyPassReverse.useProtocolPrefix' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.connettori.consegnaContenutiApplicativi.proxyPassReverse.useProtocolPrefix': "+e.getMessage(),e);
				OpenSPCoop2Properties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix = false;
			}    
		}

		return OpenSPCoop2Properties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix;
	}
	
	private static List<String> getRESTServices_inoltroBuste_proxyPassReverse_headers = null;
	public List<String> getRESTServices_inoltroBuste_proxyPassReverse_headers() throws Exception {	
		if(OpenSPCoop2Properties.getRESTServices_inoltroBuste_proxyPassReverse_headers==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.connettori.inoltroBuste.proxyPassReverse.headers");
				if(name==null){
					throw new Exception("non presente");
				}
				name = name.trim();
				OpenSPCoop2Properties.getRESTServices_inoltroBuste_proxyPassReverse_headers = new ArrayList<String>();
				if(name.contains(",")) {
					String [] tmp = name.split(",");
					for (int i = 0; i < tmp.length; i++) {
						OpenSPCoop2Properties.getRESTServices_inoltroBuste_proxyPassReverse_headers.add(tmp[i].trim());
					}
				}
				else {
					OpenSPCoop2Properties.getRESTServices_inoltroBuste_proxyPassReverse_headers.add(name);
				}
			} catch(java.lang.Exception e) {
				String error = "Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.connettori.inoltroBuste.proxyPassReverse.headers': "+e.getMessage();
				this.log.error(error,e);
				throw new Exception(error);
			}    
		}

		return OpenSPCoop2Properties.getRESTServices_inoltroBuste_proxyPassReverse_headers;
	}
	
	private static List<String> getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_headers = null;
	public List<String> getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_headers() throws Exception {	
		if(OpenSPCoop2Properties.getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_headers==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.rest.connettori.consegnaContenutiApplicativi.proxyPassReverse.headers");
				if(name==null){
					throw new Exception("non presente");
				}
				name = name.trim();
				OpenSPCoop2Properties.getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_headers = new ArrayList<String>();
				if(name.contains(",")) {
					String [] tmp = name.split(",");
					for (int i = 0; i < tmp.length; i++) {
						OpenSPCoop2Properties.getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_headers.add(tmp[i].trim());
					}
				}
				else {
					OpenSPCoop2Properties.getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_headers.add(name);
				}
			} catch(java.lang.Exception e) {
				String error = "Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.rest.connettori.consegnaContenutiApplicativi.proxyPassReverse.headers': "+e.getMessage();
				this.log.error(error,e);
				throw new Exception(error);
			}    
		}

		return OpenSPCoop2Properties.getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_headers;
	}

	
	
	/* ------------- Transazioni ---------------------*/
	
	private static Boolean isTransazioniEnabled = null;
	public boolean isTransazioniEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniEnabled;
	}	
	
	private static Boolean isTransazioniDebug = null;
	public boolean isTransazioniDebug() {	
		if(OpenSPCoop2Properties.isTransazioniDebug==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.debug");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.debug' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniDebug = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.debug', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniDebug = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniDebug;
	}	
	
	private static Boolean isTransazioniUsePddRuntimeDatasource = null;
	public boolean isTransazioniUsePddRuntimeDatasource() {	
		if(OpenSPCoop2Properties.isTransazioniUsePddRuntimeDatasource==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.dataSource.usePddRuntime");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.dataSource.usePddRuntime' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniUsePddRuntimeDatasource = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.dataSource.usePddRuntime', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniUsePddRuntimeDatasource = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniUsePddRuntimeDatasource;
	}
	
	private static String getTransazioniDatasource = null;
	public String getTransazioniDatasource() throws Exception {	
		if(OpenSPCoop2Properties.getTransazioniDatasource==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.dataSource");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getTransazioniDatasource = name;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.dataSource': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getTransazioniDatasource;
	}
	
	private static Properties getTransazioniDatasourceJndiContext = null;
	public Properties getTransazioniDatasourceJndiContext() throws Exception {	
		if(OpenSPCoop2Properties.getTransazioniDatasourceJndiContext==null){
			try{ 
				OpenSPCoop2Properties.getTransazioniDatasourceJndiContext = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.transazioni.dataSource.property.");
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.dataSource.property.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getTransazioniDatasourceJndiContext;
	}
	
	private static Boolean isTransazioniDatasourceUseDBUtils = null;
	public boolean isTransazioniDatasourceUseDBUtils() {	
		if(OpenSPCoop2Properties.isTransazioniDatasourceUseDBUtils==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.datasource.useDSUtils");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.datasource.useDSUtils' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniDatasourceUseDBUtils = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.datasource.useDSUtils', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniDatasourceUseDBUtils = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniDatasourceUseDBUtils;
	}
	
	private static Boolean isTransazioniSaveTracceInUniqueTransaction = null;
	public boolean isTransazioniSaveTracceInUniqueTransaction() {	
		if(OpenSPCoop2Properties.isTransazioniSaveTracceInUniqueTransaction==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.tracce.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.tracce.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniSaveTracceInUniqueTransaction = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.tracce.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniSaveTracceInUniqueTransaction = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniSaveTracceInUniqueTransaction;
	}
	
	private static Boolean isTransazioniSaveDiagnosticiInUniqueTransaction = null;
	public boolean isTransazioniSaveDiagnosticiInUniqueTransaction() {	
		if(OpenSPCoop2Properties.isTransazioniSaveDiagnosticiInUniqueTransaction==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.diagnostici.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.diagnostici.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniSaveDiagnosticiInUniqueTransaction = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.diagnostici.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniSaveDiagnosticiInUniqueTransaction = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniSaveDiagnosticiInUniqueTransaction;
	}
	
	private static Boolean isTransazioniSaveDumpInUniqueTransaction = null;
	public boolean isTransazioniSaveDumpInUniqueTransaction() {	
		if(OpenSPCoop2Properties.isTransazioniSaveDumpInUniqueTransaction==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.dump.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.dump.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniSaveDumpInUniqueTransaction = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.dump.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniSaveDumpInUniqueTransaction = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniSaveDumpInUniqueTransaction;
	}
	
	private static Boolean isTransazioniFaultPrettyPrint = null;
	public boolean isTransazioniFaultPrettyPrint() {	
		if(OpenSPCoop2Properties.isTransazioniFaultPrettyPrint==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.fault.prettyPrint");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.fault.prettyPrint' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniFaultPrettyPrint = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.fault.prettyPrint', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniFaultPrettyPrint = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniFaultPrettyPrint;
	}
	
	// FiltroDuplicati
	
	private static Boolean isTransazioniFiltroDuplicatiSaveDateEnabled = null;
	private boolean _isTransazioniFiltroDuplicatiSaveDateEnabled() throws ProtocolException {	
		if(OpenSPCoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.duplicati.dateIdentificativiProtocolloInCampiSeparati");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.duplicati.dateIdentificativiProtocolloInCampiSeparati' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.duplicati.dateIdentificativiProtocolloInCampiSeparati', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled;
	}
	public boolean isTransazioniFiltroDuplicatiSaveDateEnabled(IProtocolFactory<?> protocolFactory) throws ProtocolException {	
		return _isTransazioniFiltroDuplicatiSaveDateEnabled() && protocolFactory.createProtocolConfiguration().isDataPresenteInIdentificativoMessaggio();
	}
	
	private static Boolean isTransazioniFiltroDuplicatiTramiteTransazioniEnabled = null;
	public boolean isTransazioniFiltroDuplicatiTramiteTransazioniEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.duplicati.filtroTramiteTransazioni.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.duplicati.filtroTramiteTransazioni.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.duplicati.filtroTramiteTransazioni.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniEnabled;
	}
	
	private static Boolean isTransazioniFiltroDuplicatiTramiteTransazioniUsePdDConnection = null;
	public boolean isTransazioniFiltroDuplicatiTramiteTransazioniUsePdDConnection() {	
		if(OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniUsePdDConnection==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.duplicati.filtroTramiteTransazioni.usePdDConnection");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.duplicati.filtroTramiteTransazioni.usePdDConnection' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniUsePdDConnection = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.duplicati.filtroTramiteTransazioni.usePdDConnection', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniUsePdDConnection = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniUsePdDConnection;
	}
	
	private static Boolean isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex = null;
	public boolean isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex() {	
		if(OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.duplicati.filtroTramiteTransazioni.forceIndex");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.duplicati.filtroTramiteTransazioni.forceIndex' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.duplicati.filtroTramiteTransazioni.forceIndex', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniFiltroDuplicatiTramiteTransazioniForceIndex;
	}
	
	
	// Stateful
	
	private static Boolean isTransazioniStatefulEnabled = null;
	public boolean isTransazioniStatefulEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniStatefulEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.stateful.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.stateful.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniStatefulEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.stateful.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniStatefulEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniStatefulEnabled;
	}
	
	private static Boolean isTransazioniStatefulDebug = null;
	public boolean isTransazioniStatefulDebug() {	
		if(OpenSPCoop2Properties.isTransazioniStatefulDebug==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.stateful.debug");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.stateful.debug' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniStatefulDebug = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.stateful.debug', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniStatefulDebug = true;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniStatefulDebug;
	}
	
	private static Integer getTransazioniStatefulTimerIntervalSeconds = null;
	public int getTransazioniStatefulTimerIntervalSeconds() throws Exception {	
		if(OpenSPCoop2Properties.getTransazioniStatefulTimerIntervalSeconds==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.stateful.seconds");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getTransazioniStatefulTimerIntervalSeconds = Integer.valueOf(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.stateful.seconds': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getTransazioniStatefulTimerIntervalSeconds;
	}
	
	
	// Salvataggio
	
	private static Integer getTransazioniCredenzialiMittenteMaxLength = null;
	public int getTransazioniCredenzialiMittenteMaxLength() throws Exception {	
		if(OpenSPCoop2Properties.getTransazioniCredenzialiMittenteMaxLength==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.credenzialiMittente.maxLength");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getTransazioniCredenzialiMittenteMaxLength = Integer.valueOf(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.credenzialiMittente.maxLength': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getTransazioniCredenzialiMittenteMaxLength;
	}
	
	private static Boolean getTransazioniCredenzialiMittenteLifeSeconds_read = null;
	private static Integer getTransazioniCredenzialiMittenteLifeSeconds = null;
	public Integer getTransazioniCredenzialiMittenteLifeSeconds() throws Exception {	
		if(OpenSPCoop2Properties.getTransazioniCredenzialiMittenteLifeSeconds_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.credenzialiMittente.updateAfterSeconds");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getTransazioniCredenzialiMittenteLifeSeconds = Integer.valueOf(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.credenzialiMittente.updateAfterSeconds': "+e.getMessage(),e);
				throw e;
			}    
			
			OpenSPCoop2Properties.getTransazioniCredenzialiMittenteLifeSeconds_read = true;
		}

		return OpenSPCoop2Properties.getTransazioniCredenzialiMittenteLifeSeconds;
	}
	
	private static Boolean isTransazioniRegistrazioneTracceProtocolPropertiesEnabled = null;
	public boolean isTransazioniRegistrazioneTracceProtocolPropertiesEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniRegistrazioneTracceProtocolPropertiesEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.tracce.protocolProperties.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.tracce.protocolProperties.enabled' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniRegistrazioneTracceProtocolPropertiesEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.tracce.protocolProperties.enabled', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniRegistrazioneTracceProtocolPropertiesEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniRegistrazioneTracceProtocolPropertiesEnabled;
	}
	
	private static Boolean isTransazioniRegistrazioneTracceHeaderRawEnabled = null;
	public boolean isTransazioniRegistrazioneTracceHeaderRawEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniRegistrazioneTracceHeaderRawEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.tracce.headerRaw.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.tracce.headerRaw.enabled' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniRegistrazioneTracceHeaderRawEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.tracce.headerRaw.enabled', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniRegistrazioneTracceHeaderRawEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniRegistrazioneTracceHeaderRawEnabled;
	}
	
	private static Boolean isTransazioniRegistrazioneTracceDigestEnabled = null;
	public boolean isTransazioniRegistrazioneTracceDigestEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniRegistrazioneTracceDigestEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.tracce.digest.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.tracce.digest.enabled' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniRegistrazioneTracceDigestEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.tracce.digest.enabled', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniRegistrazioneTracceDigestEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniRegistrazioneTracceDigestEnabled;
	}
	
	private static ISalvataggioTracceManager getTransazioniRegistrazioneTracceManager = null;
	private static Boolean getTransazioniRegistrazioneTracceManager_read = null;
	public ISalvataggioTracceManager getTransazioniRegistrazioneTracceManager() throws Exception {	
		return OpenSPCoop2Properties.getTransazioniRegistrazioneTracceManager;
	}
	private ISalvataggioTracceManager _getTransazioniRegistrazioneTracceManager(ClassNameProperties className, Loader loader) throws Exception {	
		if(OpenSPCoop2Properties.getTransazioniRegistrazioneTracceManager_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.tracce.salvataggio");
				if(name!=null){
					name = name.trim();
					
					String classe = className.getSalvataggioTracceManager(name);
					if(classe == null) {
						throw new Exception("Classe non trovata per il salvataggio delle tracce con tipo '"+name+"'");
					}
					
					OpenSPCoop2Properties.getTransazioniRegistrazioneTracceManager = (ISalvataggioTracceManager) loader.newInstance(classe);
				}
				
				getTransazioniRegistrazioneTracceManager_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.tracce.salvataggio': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getTransazioniRegistrazioneTracceManager;
	}
	
	private static ISalvataggioDiagnosticiManager getTransazioniRegistrazioneDiagnosticiManager = null;
	private static Boolean getTransazioniRegistrazioneDiagnosticiManager_read = null;
	public ISalvataggioDiagnosticiManager getTransazioniRegistrazioneDiagnosticiManager() throws Exception {	
		return OpenSPCoop2Properties.getTransazioniRegistrazioneDiagnosticiManager;
	}
	private ISalvataggioDiagnosticiManager _getTransazioniRegistrazioneDiagnosticiManager(ClassNameProperties className, Loader loader) throws Exception {	
		if(OpenSPCoop2Properties.getTransazioniRegistrazioneDiagnosticiManager_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.diagnostici.salvataggio");
				if(name!=null){
					name = name.trim();
					
					String classe = className.getSalvataggioDiagnosticiManager(name);
					if(classe == null) {
						throw new Exception("Classe non trovata per il salvataggio dei diagnostici con tipo '"+name+"'");
					}
					
					OpenSPCoop2Properties.getTransazioniRegistrazioneDiagnosticiManager = (ISalvataggioDiagnosticiManager) loader.newInstance(classe);
				}
				
				getTransazioniRegistrazioneDiagnosticiManager_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.diagnostici.salvataggio': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getTransazioniRegistrazioneDiagnosticiManager;
	}
	
	private static Boolean isTransazioniRegistrazioneDumpHeadersCompactEnabled = null;
	public boolean isTransazioniRegistrazioneDumpHeadersCompactEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniRegistrazioneDumpHeadersCompactEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.transazioni.dump.headers.compact.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.dump.headers.compact.enabled' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniRegistrazioneDumpHeadersCompactEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.transazioni.dump.headers.compact.enabled', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniRegistrazioneDumpHeadersCompactEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniRegistrazioneDumpHeadersCompactEnabled;
	}
	
	// Trace su File
	
	private static Boolean isTransazioniFileTraceEnabled = null;
	public boolean isTransazioniFileTraceEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniFileTraceEnabled==null){
			String pName = "org.openspcoop2.pdd.transazioni.fileTrace.enabled";
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name==null){
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniFileTraceEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniFileTraceEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniFileTraceEnabled;
	}
	
	private static Boolean isTransazioniFileTraceDumpBinarioPDEnabled = null;
	public boolean isTransazioniFileTraceDumpBinarioPDEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPDEnabled==null){
			String pName = "org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPD.enabled";
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name==null){
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPDEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPDEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPDEnabled;
	}
	
	private static Boolean isTransazioniFileTraceDumpBinarioPDConnettoreEnabled = null;
	public boolean isTransazioniFileTraceDumpBinarioPDConnettoreEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPDConnettoreEnabled==null){
			String pName = "org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPD.connettore.enabled";
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name==null){
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPDConnettoreEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPDConnettoreEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPDConnettoreEnabled;
	}
	
	private static Boolean isTransazioniFileTraceDumpBinarioPAEnabled = null;
	public boolean isTransazioniFileTraceDumpBinarioPAEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPAEnabled==null){
			String pName = "org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPA.enabled";
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name==null){
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPAEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPAEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPAEnabled;
	}
	
	private static Boolean isTransazioniFileTraceDumpBinarioPAConnettoreEnabled = null;
	public boolean isTransazioniFileTraceDumpBinarioPAConnettoreEnabled() {	
		if(OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPAConnettoreEnabled==null){
			String pName = "org.openspcoop2.pdd.transazioni.fileTrace.dumpBinarioPA.connettore.enabled";
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name==null){
					this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPAConnettoreEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPAConnettoreEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isTransazioniFileTraceDumpBinarioPAConnettoreEnabled;
	}

	private static File getTransazioniFileTraceConfig = null;
	public File getTransazioniFileTraceConfig() throws Exception {	
		if(OpenSPCoop2Properties.getTransazioniFileTraceConfig==null){
			String pName = "org.openspcoop2.pdd.transazioni.fileTrace.config";
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties(pName);
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				
				OpenSPCoop2Properties.getTransazioniFileTraceConfig = new File(name);
				if(!OpenSPCoop2Properties.getTransazioniFileTraceConfig.exists()) {
					String rootDir = this.getRootDirectory();
					if(rootDir!=null && !"".equals(rootDir)) {
						OpenSPCoop2Properties.getTransazioniFileTraceConfig = new File(rootDir, name);
					}
				}
				
				if(!OpenSPCoop2Properties.getTransazioniFileTraceConfig.exists()) {
					throw new Exception("Config file ["+OpenSPCoop2Properties.getTransazioniFileTraceConfig.getAbsolutePath()+"] not exists");
				}
				if(OpenSPCoop2Properties.getTransazioniFileTraceConfig.isDirectory()) {
					throw new Exception("Config file ["+OpenSPCoop2Properties.getTransazioniFileTraceConfig.getAbsolutePath()+"] is directory");
				}
				if(OpenSPCoop2Properties.getTransazioniFileTraceConfig.canRead()==false) {
					throw new Exception("Config file ["+OpenSPCoop2Properties.getTransazioniFileTraceConfig.getAbsolutePath()+"] cannot read");
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop '"+pName+"': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getTransazioniFileTraceConfig;
	}
	
	public FileTraceGovWayState getFileTraceGovWayState() {
		FileTraceGovWayState state = new FileTraceGovWayState();
		state.setEnabled(this.isTransazioniFileTraceEnabled());
		if(state.isEnabled()) {
			try {
				state.setPath(this.getTransazioniFileTraceConfig().getAbsolutePath());
			}catch(Exception e) {
				state.setPath("ERROR: "+e.getMessage());
			}
			state.setEnabledInErogazione(this.isTransazioniFileTraceDumpBinarioPAEnabled());
			state.setEnabledOutErogazione(this.isTransazioniFileTraceDumpBinarioPAConnettoreEnabled());
			state.setEnabledInFruizione(this.isTransazioniFileTraceDumpBinarioPDEnabled());
			state.setEnabledOutFruizione(this.isTransazioniFileTraceDumpBinarioPDConnettoreEnabled());
		}
		return state;
	}
	
	
	/* ------------- Eventi ---------------------*/
	
	private static Boolean isEventiEnabled = null;
	public boolean isEventiEnabled() {	
		if(OpenSPCoop2Properties.isEventiEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.eventi.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.eventi.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEventiEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.eventi.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isEventiEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isEventiEnabled;
	}
	
	private static Boolean isEventiDebug = null;
	public boolean isEventiDebug() {	
		if(OpenSPCoop2Properties.isEventiDebug==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.eventi.debug");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.eventi.debug' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEventiDebug = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.eventi.debug', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isEventiDebug = true;
			}    
		}

		return OpenSPCoop2Properties.isEventiDebug;
	}
	
	private static Boolean isEventiRegistrazioneStatoPorta = null;
	public boolean isEventiRegistrazioneStatoPorta() {	
		if(OpenSPCoop2Properties.isEventiRegistrazioneStatoPorta==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.eventi.registraStatoPorta");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.eventi.registraStatoPorta' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEventiRegistrazioneStatoPorta = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.eventi.registraStatoPorta', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isEventiRegistrazioneStatoPorta = true;
			}    
		}

		return OpenSPCoop2Properties.isEventiRegistrazioneStatoPorta;
	}
	
	private static Boolean isEventiTimerEnabled = null;
	public boolean isEventiTimerEnabled() {	
		if(OpenSPCoop2Properties.isEventiTimerEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.eventi.timer.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.eventi.timer.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEventiTimerEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.eventi.timer.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isEventiTimerEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isEventiTimerEnabled;
	}
	
	private static Integer getEventiTimerIntervalSeconds = null;
	public int getEventiTimerIntervalSeconds() throws Exception {	
		if(OpenSPCoop2Properties.getEventiTimerIntervalSeconds==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.eventi.timer.seconds");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getEventiTimerIntervalSeconds = Integer.valueOf(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.eventi.timer.seconds': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getEventiTimerIntervalSeconds;
	}
	
	
	
	
	/* ------------- Repository ---------------------*/
	
	private static File getFileSystemRecovery_repository = null;
	public File getFileSystemRecovery_repository() throws Exception {	
		if(OpenSPCoop2Properties.getFileSystemRecovery_repository==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.resources.fileSystemRecovery.repository");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getFileSystemRecovery_repository = new File(name);
				if(OpenSPCoop2Properties.getFileSystemRecovery_repository.exists()) {
					if(OpenSPCoop2Properties.getFileSystemRecovery_repository.isDirectory()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getFileSystemRecovery_repository.getAbsolutePath()+"] not dir");
					}
					if(OpenSPCoop2Properties.getFileSystemRecovery_repository.canRead()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getFileSystemRecovery_repository.getAbsolutePath()+"] cannot read");
					}
					if(OpenSPCoop2Properties.getFileSystemRecovery_repository.canWrite()==false) {
						throw new Exception("Dir ["+OpenSPCoop2Properties.getFileSystemRecovery_repository.getAbsolutePath()+"] cannot write");
					}
				}
				else {
					// viene creata automaticamente
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.repository': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getFileSystemRecovery_repository;
	}
	
	private static Boolean isFileSystemRecoveryDebug = null;
	public boolean isFileSystemRecoveryDebug() {	
		if(OpenSPCoop2Properties.isFileSystemRecoveryDebug==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.resources.fileSystemRecovery.debug");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.debug' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isFileSystemRecoveryDebug = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.debug', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isFileSystemRecoveryDebug = true;
			}    
		}

		return OpenSPCoop2Properties.isFileSystemRecoveryDebug;
	}
	
	private static Boolean isFileSystemRecoveryTimerEnabled = null;
	public boolean isFileSystemRecoveryTimerEnabled() {	
		if(OpenSPCoop2Properties.isFileSystemRecoveryTimerEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.resources.fileSystemRecovery.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isFileSystemRecoveryTimerEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isFileSystemRecoveryTimerEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isFileSystemRecoveryTimerEnabled;
	}
	
	private static Integer getFileSystemRecoveryTimerIntervalSeconds = null;
	public int getFileSystemRecoveryTimerIntervalSeconds() throws Exception {	
		if(OpenSPCoop2Properties.getFileSystemRecoveryTimerIntervalSeconds==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.resources.fileSystemRecovery.timeout");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getFileSystemRecoveryTimerIntervalSeconds = Integer.valueOf(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.timeout': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getFileSystemRecoveryTimerIntervalSeconds;
	}
		
	private static Integer getFileSystemRecoveryTimerMaxAttempts = null;
	public int getFileSystemRecoveryMaxAttempts() throws Exception {	
		if(OpenSPCoop2Properties.getFileSystemRecoveryTimerMaxAttempts==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.resources.fileSystemRecovery.maxAttempts");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getFileSystemRecoveryTimerMaxAttempts = Integer.valueOf(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.maxAttempts': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getFileSystemRecoveryTimerMaxAttempts;
	}
	
	private static Boolean isFileSystemRecoveryTimerEventEnabled = null;
	public boolean isFileSystemRecoveryTimerEventEnabled() {	
		if(OpenSPCoop2Properties.isFileSystemRecoveryTimerEventEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.resources.fileSystemRecovery.events.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.events.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isFileSystemRecoveryTimerEventEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.events.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isFileSystemRecoveryTimerEventEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isFileSystemRecoveryTimerEventEnabled;
	}
	
	private static Boolean isFileSystemRecoveryTimerTransactionEnabled = null;
	public boolean isFileSystemRecoveryTimerTransactionEnabled() {	
		if(OpenSPCoop2Properties.isFileSystemRecoveryTimerTransactionEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.resources.fileSystemRecovery.transaction.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.transaction.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isFileSystemRecoveryTimerTransactionEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.resources.fileSystemRecovery.transaction.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isFileSystemRecoveryTimerTransactionEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isFileSystemRecoveryTimerTransactionEnabled;
	}
	
	
	
	
	
	/* ------------- Controllo Traffico ---------------------*/
	
	private static Boolean isControlloTrafficoEnabled = null;
	public boolean isControlloTrafficoEnabled() {	
		if(OpenSPCoop2Properties.isControlloTrafficoEnabled==null){
			try{ 
				if(CostantiConfigurazione.CONFIGURAZIONE_XML.equalsIgnoreCase(getTipoConfigurazionePDD())){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.enabled' ignorata. Il controllo del traffico e' disabilitato sulla configurazione xml");
					isControlloTrafficoEnabled = false;
				}
				else {
					String name = null;
					name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.enabled");
					if(name==null){
						this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.enabled' non impostata, viene utilizzato il default=true");
						name="true";
					}
					name = name.trim();
					OpenSPCoop2Properties.isControlloTrafficoEnabled = Boolean.parseBoolean(name);
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloTrafficoEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isControlloTrafficoEnabled;
	}
	
	private static Boolean isControlloTrafficoDebug = null;
	public boolean isControlloTrafficoDebug() {	
		if(OpenSPCoop2Properties.isControlloTrafficoDebug==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.debug");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.debug' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isControlloTrafficoDebug = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.debug', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloTrafficoDebug = true;
			}    
		}

		return OpenSPCoop2Properties.isControlloTrafficoDebug;
	}
	
	private static Boolean isControlloTrafficoViolazioneGenerazioneErroreGenerico = null;
	private boolean isControlloTrafficoViolazioneGenerazioneErroreGenerico() {	
		if(OpenSPCoop2Properties.isControlloTrafficoViolazioneGenerazioneErroreGenerico==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.erroreGenerico");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.erroreGenerico' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isControlloTrafficoViolazioneGenerazioneErroreGenerico = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.erroreGenerico', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloTrafficoViolazioneGenerazioneErroreGenerico = true;
			}    
		}

		return OpenSPCoop2Properties.isControlloTrafficoViolazioneGenerazioneErroreGenerico;
	}
	
	private static Boolean isControlloTrafficoPolicyLetturaDaCacheDinamica = null;
	private boolean isControlloTrafficoPolicyLetturaDaCacheDinamica() {	
		if(OpenSPCoop2Properties.isControlloTrafficoPolicyLetturaDaCacheDinamica==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.policy.readWithDynamicCache");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.policy.readWithDynamicCache' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isControlloTrafficoPolicyLetturaDaCacheDinamica = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.policy.readWithDynamicCache', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloTrafficoPolicyLetturaDaCacheDinamica = true;
			}    
		}

		return OpenSPCoop2Properties.isControlloTrafficoPolicyLetturaDaCacheDinamica;
	}
	
	private static Map<String, int[]> getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaDelegata = new HashMap<>();
	private int[] getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaDelegata(String protocollo) throws Exception{
		if(OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaDelegata.containsKey(protocollo)==false){
			EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log,protocollo);
			try{
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.calcoloLatenza.portaDelegata.esitiConsiderati");
				if(value!=null){
					value = value.trim();
					String [] split = value.split(",");
					List<Integer> lInt = new ArrayList<>();
					for (int i = 0; i < split.length; i++) {
						String s = split[i].trim();
						int e = -1;
						try{
							e = Integer.parseInt(s);
						}catch(Exception eParse){
							throw new Exception("Valore ["+s+"] non riconosciuto come esito valido: "+eParse.getMessage(),eParse);
						}
						if(esitiProperties.existsEsitoCode(e)==false){
							continue; // non supportato dal protocollo
						}
						lInt.add(e);
					}
					if(lInt.isEmpty()) {
						throw new Exception("Nessun valore è utilizzabile per il protocollo '"+protocollo+"'");
					}
					int [] tmp = new int[lInt.size()];
					for (int i = 0; i < lInt.size(); i++) {
						tmp[i] = lInt.get(i);
					}
					OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaDelegata.put(protocollo, tmp);
				}
				else{
					// default
					int defaultEsito = esitiProperties.convertoToCode(EsitoTransazioneName.OK);
					this.log.warn("Proprieta' non presente [org.openspcoop2.pdd.controlloTraffico.calcoloLatenza.portaDelegata.esitiConsiderati] (Uso il default '"+EsitoTransazioneName.OK+"="+defaultEsito+"')");
					int [] tmp = new int[1];
					tmp[0] = defaultEsito;
					OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaDelegata.put(protocollo, tmp);
				}

			}catch(Exception e){
				// default
				int defaultEsito = esitiProperties.convertoToCode(EsitoTransazioneName.OK);
				this.log.error("Errore durante la lettura della proprieta' [org.openspcoop2.pdd.controlloTraffico.calcoloLatenza.portaDelegata.esitiConsideratii] (Uso il default '"+EsitoTransazioneName.OK+"="+defaultEsito+"'): "+e.getMessage(),e);
				int [] tmp = new int[1];
				tmp[0] = defaultEsito;
				OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaDelegata.put(protocollo, tmp);
			}
		}
		return OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaDelegata.get(protocollo);
	}
	
	private static Map<String, int[]> getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaApplicativa = new HashMap<>();
	private int[] getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaApplicativa(String protocollo) throws Exception{
		if(OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaApplicativa.containsKey(protocollo)==false){
			EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log,protocollo);
			try{
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.calcoloLatenza.portaApplicativa.esitiConsiderati");
				if(value!=null){
					value = value.trim();
					String [] split = value.split(",");
					List<Integer> lInt = new ArrayList<>();
					for (int i = 0; i < split.length; i++) {
						String s = split[i].trim();
						int e = -1;
						try{
							e = Integer.parseInt(s);
						}catch(Exception eParse){
							throw new Exception("Valore ["+s+"] non riconosciuto come esito valido: "+eParse.getMessage(),eParse);
						}
						if(esitiProperties.existsEsitoCode(e)==false){
							continue; // non supportato dal protocollo
						}
						lInt.add(e);
					}
					if(lInt.isEmpty()) {
						throw new Exception("Nessun valore è utilizzabile per il protocollo '"+protocollo+"'");
					}
					int [] tmp = new int[lInt.size()];
					for (int i = 0; i < lInt.size(); i++) {
						tmp[i] = lInt.get(i);
					}
					OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaApplicativa.put(protocollo, tmp);
				}
				else{
					// default
					int defaultEsito = esitiProperties.convertoToCode(EsitoTransazioneName.OK);
					this.log.warn("Proprieta' non presente [org.openspcoop2.pdd.controlloTraffico.calcoloLatenza.portaApplicativa.esitiConsiderati] (Uso il default '"+EsitoTransazioneName.OK+"="+defaultEsito+"')");
					int [] tmp = new int[1];
					tmp[0] = defaultEsito;
					OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaApplicativa.put(protocollo, tmp);
				}

			}catch(Exception e){
				// default
				int defaultEsito = esitiProperties.convertoToCode(EsitoTransazioneName.OK);
				this.log.error("Errore durante la lettura della proprieta' [org.openspcoop2.pdd.controlloTraffico.calcoloLatenza.portaApplicativa.esitiConsideratii] (Uso il default '"+EsitoTransazioneName.OK+"="+defaultEsito+"'): "+e.getMessage(),e);
				int [] tmp = new int[1];
				tmp[0] = defaultEsito;
				OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaApplicativa.put(protocollo, tmp);
			}
		}
		return OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaApplicativa.get(protocollo);
	}
	
	private static Boolean isControlloTrafficoStatisticheFinestraScorrevoleGestioneUltimoIntervallo = null;
	private boolean isControlloTrafficoStatisticheFinestraScorrevoleGestioneUltimoIntervallo() {	
		if(OpenSPCoop2Properties.isControlloTrafficoStatisticheFinestraScorrevoleGestioneUltimoIntervallo==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.statistiche.finestraScorrevole.gestioneUltimoIntervallo");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.statistiche.finestraScorrevole.gestioneUltimoIntervallo' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isControlloTrafficoStatisticheFinestraScorrevoleGestioneUltimoIntervallo = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.statistiche.finestraScorrevole.gestioneUltimoIntervallo', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloTrafficoStatisticheFinestraScorrevoleGestioneUltimoIntervallo = true;
			}    
		}

		return OpenSPCoop2Properties.isControlloTrafficoStatisticheFinestraScorrevoleGestioneUltimoIntervallo;
	}
	
	private static Map<String, int[]> getControlloTrafficoEsitiDaConsiderarePerViolazionePolicy = new HashMap<>();
	private int[] getControlloTrafficoEsitiDaConsiderarePerViolazionePolicy(String protocollo) throws Exception{
		if(getControlloTrafficoEsitiDaConsiderarePerViolazionePolicy.containsKey(protocollo) == false){
			EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log, protocollo);
			try{
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.violazionePolicy.esitiConsiderati");
				if(value!=null){
					value = value.trim();
					String [] split = value.split(",");
					int [] tmp = new int[split.length];
					for (int i = 0; i < split.length; i++) {
						String s = split[i].trim();
						int e = -1;
						try{
							e = Integer.parseInt(s);
						}catch(Exception eParse){
							throw new Exception("Valore ["+s+"] non riconosciuto come esito valido: "+eParse.getMessage(),eParse);
						}
						if(esitiProperties.existsEsitoCode(e)==false){
							throw new Exception("Valore ["+s+"] non riconosciuto come esito valido");
						}
						tmp[i] = e;
					}
					OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerViolazionePolicy.put(protocollo, tmp);
				}
				else{
					// default
					int defaultEsito =  esitiProperties.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA);
					this.log.warn("Proprieta' non presente [org.openspcoop2.pdd.controlloTraffico.violazionePolicy.esitiConsiderati] (Uso il default '"+defaultEsito+"')");
					int [] tmp = new int[1];
					tmp[0] = defaultEsito;
					OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerViolazionePolicy.put(protocollo, tmp);
				}

			}catch(Exception e){
				// default
				int defaultEsito =  esitiProperties.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA);
				this.log.error("Errore durante la lettura della proprieta' [org.openspcoop2.pdd.controlloTraffico.violazionePolicy.esitiConsiderati] (Uso il default '"+defaultEsito+"'): "+e.getMessage(),e);
				int [] tmp = new int[1];
				tmp[0] = defaultEsito;
				OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerViolazionePolicy.put(protocollo, tmp);
			}
		}
		return OpenSPCoop2Properties.getControlloTrafficoEsitiDaConsiderarePerViolazionePolicy.get(protocollo);
	}
	
	private static Boolean isControlloTrafficoRealtimeIncrementaSoloPolicyApplicabile = null;
	private boolean isControlloTrafficoRealtimeIncrementaSoloPolicyApplicabile() {	
		if(OpenSPCoop2Properties.isControlloTrafficoRealtimeIncrementaSoloPolicyApplicabile==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.realtime.incrementaSoloPolicyApplicabile");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.realtime.incrementaSoloPolicyApplicabile' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isControlloTrafficoRealtimeIncrementaSoloPolicyApplicabile = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.realtime.incrementaSoloPolicyApplicabile', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloTrafficoRealtimeIncrementaSoloPolicyApplicabile = false;
			}    
		}

		return OpenSPCoop2Properties.isControlloTrafficoRealtimeIncrementaSoloPolicyApplicabile;
	}
	
	private static ConfigurazioneControlloTraffico controlloTrafficoConfigurazione = null;
	public void initConfigurazioneControlloTraffico(Loader loaderOpenSPCoop, List<String> protocolli) throws Exception{
		if(controlloTrafficoConfigurazione==null){
			
			controlloTrafficoConfigurazione = new ConfigurazioneControlloTraffico();
						
			AccessoConfigurazionePdD config = this.getAccessoConfigurazionePdD();
			controlloTrafficoConfigurazione.setTipoDatabaseConfig(config.getTipoDatabase());
			
			controlloTrafficoConfigurazione.setDebug(this.isControlloTrafficoDebug());
			
			controlloTrafficoConfigurazione.setErroreGenerico(this.isControlloTrafficoViolazioneGenerazioneErroreGenerico());
			
			controlloTrafficoConfigurazione.setPolicyReadedWithDynamicCache(this.isControlloTrafficoPolicyLetturaDaCacheDinamica());
			
			for (String protocollo : protocolli) {
				getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaDelegata(protocollo);
				getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaApplicativa(protocollo);
			}
			controlloTrafficoConfigurazione.setCalcoloLatenzaPortaDelegataEsitiConsiderati(getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaDelegata);
			controlloTrafficoConfigurazione.setCalcoloLatenzaPortaApplicativaEsitiConsiderati(getControlloTrafficoEsitiDaConsiderarePerCalcoloLatenzaPortaApplicativa);
			
			controlloTrafficoConfigurazione.setElaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente(this.isControlloTrafficoStatisticheFinestraScorrevoleGestioneUltimoIntervallo());
			
			INotify notifier = this.getControlloTrafficoNotifyImpl(loaderOpenSPCoop);
			controlloTrafficoConfigurazione.setNotifierEnabled(notifier!=null);
			controlloTrafficoConfigurazione.setNotifier(notifier);
			
			for (String protocollo : protocolli) {
				getControlloTrafficoEsitiDaConsiderarePerViolazionePolicy(protocollo);
			}
			controlloTrafficoConfigurazione.setEsitiPolicyViolate(getControlloTrafficoEsitiDaConsiderarePerViolazionePolicy);
			
			controlloTrafficoConfigurazione.setElaborazioneRealtime_incrementaSoloPolicyApplicabile(this.isControlloTrafficoRealtimeIncrementaSoloPolicyApplicabile());
		}
	}
	public ConfigurazioneControlloTraffico getConfigurazioneControlloTraffico(){
		return OpenSPCoop2Properties.controlloTrafficoConfigurazione;
	}
	
	
	private static TipoGestorePolicy isControlloTrafficoGestorePolicyTipo = null;
	public TipoGestorePolicy getControlloTrafficoGestorePolicyTipo() {	
		if(OpenSPCoop2Properties.isControlloTrafficoGestorePolicyTipo==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.gestorePolicy.tipo");
				if(name==null){
					TipoGestorePolicy gestoreDefault = TipoGestorePolicy.IN_MEMORY;
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.gestorePolicy.tipo' non impostata, viene utilizzato il default="+gestoreDefault);
					OpenSPCoop2Properties.isControlloTrafficoGestorePolicyTipo = gestoreDefault;
				}
				else {
					name = name.trim();
					OpenSPCoop2Properties.isControlloTrafficoGestorePolicyTipo = TipoGestorePolicy.toEnumConstant(name, true);
				}
			} catch(java.lang.Exception e) {
				TipoGestorePolicy gestoreDefault = TipoGestorePolicy.IN_MEMORY;
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.gestorePolicy.tipo', viene utilizzato il default="+gestoreDefault+" : "+e.getMessage(),e);
				OpenSPCoop2Properties.isControlloTrafficoGestorePolicyTipo = gestoreDefault;
			}    
		}

		return OpenSPCoop2Properties.isControlloTrafficoGestorePolicyTipo;
	}
	
	private static String getControlloTrafficoGestorePolicyWSUrl = null;
	public String getControlloTrafficoGestorePolicyWSUrl() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoGestorePolicyWSUrl==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.gestorePolicy.ws.url");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				else {
					name = name.trim();
					(new URL(name)).toString();
					OpenSPCoop2Properties.getControlloTrafficoGestorePolicyWSUrl = name;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.gestorePolicy.ws.url': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoGestorePolicyWSUrl;
	}
		
	private static Boolean getControlloTrafficoNotifyImpl_read = null;
	private static INotify getControlloTrafficoNotifyImpl = null;
	public INotify getControlloTrafficoNotifyImpl(Loader loaderOpenSPCoop) throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNotifyImpl_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.notifier.implementation");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNotifyImpl = (INotify) loaderOpenSPCoop.newInstance(name);
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.notifier.implementation': "+e.getMessage(),e);
				throw e;
			}  
			OpenSPCoop2Properties.getControlloTrafficoNotifyImpl_read = true;
		}

		return OpenSPCoop2Properties.getControlloTrafficoNotifyImpl;
	}
	
	private static File getControlloTrafficoGestorePolicyFileSystemRecoveryRepository = null;
	public File getControlloTrafficoGestorePolicyFileSystemRecoveryRepository() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.gestorePolicy.fileSystemRecovery.repository");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository = new File(name);
					if(OpenSPCoop2Properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository.exists()) {
						if(OpenSPCoop2Properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository.isDirectory()==false) {
							throw new Exception("Dir ["+OpenSPCoop2Properties.getMonitorSDK_repositoryJars.getAbsolutePath()+"] not dir");
						}
						if(OpenSPCoop2Properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository.canRead()==false) {
							throw new Exception("Dir ["+OpenSPCoop2Properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository.getAbsolutePath()+"] cannot read");
						}
						if(OpenSPCoop2Properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository.canWrite()==false) {
							throw new Exception("Dir ["+OpenSPCoop2Properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository.getAbsolutePath()+"] cannot write");
						}
					}
					else {
						// viene creata automaticamente
					}
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.gestorePolicy.fileSystemRecovery.repository': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository;
	}
	
	// Limit
	
	private static String [] getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit = null;
	private static Boolean getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit_read = null;
	public String [] getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteSimultanee.header.limit");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteSimultanee.header.limit': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderLimit;
	}
	
	private static String [] getControlloTrafficoNumeroRichiesteHeaderLimit = null;
	private static Boolean getControlloTrafficoNumeroRichiesteHeaderLimit_read = null;
	public String [] getControlloTrafficoNumeroRichiesteHeaderLimit() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderLimit_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichieste.header.limit");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderLimit = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderLimit.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderLimit[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderLimit[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderLimit_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichieste.header.limit': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderLimit;
	}
	
	private static String [] getControlloTrafficoOccupazioneBandaHeaderLimit = null;
	private static Boolean getControlloTrafficoOccupazioneBandaHeaderLimit_read = null;
	public String [] getControlloTrafficoOccupazioneBandaHeaderLimit() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderLimit_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.occupazioneBanda.header.limit");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderLimit = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderLimit.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderLimit[i]=OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderLimit[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderLimit_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.occupazioneBanda.header.limit': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderLimit;
	}
	
	private static String [] getControlloTrafficoTempoComplessivoRispostaHeaderLimit = null;
	private static Boolean getControlloTrafficoTempoComplessivoRispostaHeaderLimit_read = null;
	public String [] getControlloTrafficoTempoComplessivoRispostaHeaderLimit() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderLimit_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.tempoComplessivoRisposta.header.limit");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderLimit = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderLimit.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderLimit[i]=OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderLimit[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderLimit_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.tempoComplessivoRisposta.header.limit': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderLimit;
	}
	
	private static String [] getControlloTrafficoTempoMedioRispostaHeaderLimit = null;
	private static Boolean getControlloTrafficoTempoMedioRispostaHeaderLimit_read = null;
	public String [] getControlloTrafficoTempoMedioRispostaHeaderLimit() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderLimit_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.tempoMedioRisposta.header.limit");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderLimit = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderLimit.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderLimit[i]=OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderLimit[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderLimit_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.tempoMedioRisposta.header.limit': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderLimit;
	}
	
	private static String [] getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit = null;
	private static Boolean getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit_read = null;
	public String [] getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteCompletateConSuccesso.header.limit");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteCompletateConSuccesso.header.limit': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderLimit;
	}
	
	private static String [] getControlloTrafficoNumeroRichiesteFalliteHeaderLimit = null;
	private static Boolean getControlloTrafficoNumeroRichiesteFalliteHeaderLimit_read = null;
	public String [] getControlloTrafficoNumeroRichiesteFalliteHeaderLimit() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderLimit_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFallite.header.limit");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderLimit = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderLimit.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderLimit[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderLimit[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderLimit_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFallite.header.limit': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderLimit;
	}
	
	private static String [] getControlloTrafficoNumeroFaultApplicativiHeaderLimit = null;
	private static Boolean getControlloTrafficoNumeroFaultApplicativiHeaderLimit_read = null;
	public String [] getControlloTrafficoNumeroFaultApplicativiHeaderLimit() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderLimit_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroFaultApplicativi.header.limit");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderLimit = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderLimit.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderLimit[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderLimit[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderLimit_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroFaultApplicativi.header.limit': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderLimit;
	}
	
	private static String [] getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit = null;
	private static Boolean getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit_read = null;
	public String [] getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFalliteOFaultApplicativi.header.limit");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFalliteOFaultApplicativi.header.limit': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderLimit;
	}	
	

	
	// Remaining
	
	private static String [] getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining = null;
	private static Boolean getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining_read = null;
	public String [] getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteSimultanee.header.remaining");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteSimultanee.header.remaining': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteSimultaneeHeaderRemaining;
	}
	
	private static String [] getControlloTrafficoNumeroRichiesteHeaderRemaining = null;
	private static Boolean getControlloTrafficoNumeroRichiesteHeaderRemaining_read = null;
	public String [] getControlloTrafficoNumeroRichiesteHeaderRemaining() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderRemaining_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichieste.header.remaining");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderRemaining = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderRemaining.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderRemaining[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderRemaining[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderRemaining_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichieste.header.remaining': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderRemaining;
	}
	
	private static String [] getControlloTrafficoOccupazioneBandaHeaderRemaining = null;
	private static Boolean getControlloTrafficoOccupazioneBandaHeaderRemaining_read = null;
	public String [] getControlloTrafficoOccupazioneBandaHeaderRemaining() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderRemaining_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.occupazioneBanda.header.remaining");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderRemaining = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderRemaining.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderRemaining[i]=OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderRemaining[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderRemaining_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.occupazioneBanda.header.remaining': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderRemaining;
	}
	
	private static String [] getControlloTrafficoTempoComplessivoRispostaHeaderRemaining = null;
	private static Boolean getControlloTrafficoTempoComplessivoRispostaHeaderRemaining_read = null;
	public String [] getControlloTrafficoTempoComplessivoRispostaHeaderRemaining() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderRemaining_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.tempoComplessivoRisposta.header.remaining");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderRemaining = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderRemaining.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderRemaining[i]=OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderRemaining[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderRemaining_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.tempoComplessivoRisposta.header.remaining': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderRemaining;
	}
		
	private static String [] getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining = null;
	private static Boolean getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining_read = null;
	public String [] getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteCompletateConSuccesso.header.remaining");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteCompletateConSuccesso.header.remaining': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderRemaining;
	}
	
	private static String [] getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining = null;
	private static Boolean getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining_read = null;
	public String [] getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFallite.header.remaining");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFallite.header.remaining': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderRemaining;
	}
	
	private static String [] getControlloTrafficoNumeroFaultApplicativiHeaderRemaining = null;
	private static Boolean getControlloTrafficoNumeroFaultApplicativiHeaderRemaining_read = null;
	public String [] getControlloTrafficoNumeroFaultApplicativiHeaderRemaining() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderRemaining_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroFaultApplicativi.header.remaining");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderRemaining = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderRemaining.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderRemaining[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderRemaining[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderRemaining_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroFaultApplicativi.header.remaining': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderRemaining;
	}
	
	private static String [] getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining = null;
	private static Boolean getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining_read = null;
	public String [] getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFalliteOFaultApplicativi.header.remaining");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteNonCompletateConSuccesso.header.remaining': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderRemaining;
	}
	

	
	
	
	// Reset
	
	private static String [] getControlloTrafficoNumeroRichiesteHeaderReset = null;
	private static Boolean getControlloTrafficoNumeroRichiesteHeaderReset_read = null;
	public String [] getControlloTrafficoNumeroRichiesteHeaderReset() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderReset_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichieste.header.reset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderReset = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderReset.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderReset[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderReset[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderReset_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichieste.header.reset': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteHeaderReset;
	}
	
	private static String [] getControlloTrafficoOccupazioneBandaHeaderReset = null;
	private static Boolean getControlloTrafficoOccupazioneBandaHeaderReset_read = null;
	public String [] getControlloTrafficoOccupazioneBandaHeaderReset() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderReset_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.occupazioneBanda.header.reset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderReset = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderReset.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderReset[i]=OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderReset[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderReset_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.occupazioneBanda.header.reset': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoOccupazioneBandaHeaderReset;
	}
	
	private static String [] getControlloTrafficoTempoComplessivoRispostaHeaderReset = null;
	private static Boolean getControlloTrafficoTempoComplessivoRispostaHeaderReset_read = null;
	public String [] getControlloTrafficoTempoComplessivoRispostaHeaderReset() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderReset_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.tempoComplessivoRisposta.header.reset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderReset = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderReset.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderReset[i]=OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderReset[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderReset_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.tempoComplessivoRisposta.header.reset': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoTempoComplessivoRispostaHeaderReset;
	}
	
	private static String [] getControlloTrafficoTempoMedioRispostaHeaderReset = null;
	private static Boolean getControlloTrafficoTempoMedioRispostaHeaderReset_read = null;
	public String [] getControlloTrafficoTempoMedioRispostaHeaderReset() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderReset_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.tempoMedioRisposta.header.reset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderReset = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderReset.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderReset[i]=OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderReset[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderReset_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.tempoMedioRisposta.header.reset': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoTempoMedioRispostaHeaderReset;
	}
	
	private static String [] getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset = null;
	private static Boolean getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset_read = null;
	public String [] getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteCompletateConSuccesso.header.reset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteCompletateConSuccesso.header.reset': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteCompletateConSuccessoHeaderReset;
	}
	
	private static String [] getControlloTrafficoNumeroRichiesteFalliteHeaderReset = null;
	private static Boolean getControlloTrafficoNumeroRichiesteFalliteHeaderReset_read = null;
	public String [] getControlloTrafficoNumeroRichiesteFalliteHeaderReset() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderReset_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFallite.header.reset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderReset = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderReset.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderReset[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderReset[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderReset_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFallite.header.reset': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteHeaderReset;
	}
	
	private static String [] getControlloTrafficoNumeroFaultApplicativiHeaderReset = null;
	private static Boolean getControlloTrafficoNumeroFaultApplicativiHeaderReset_read = null;
	public String [] getControlloTrafficoNumeroFaultApplicativiHeaderReset() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderReset_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroFaultApplicativi.header.reset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderReset = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderReset.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderReset[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderReset[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderReset_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroFaultApplicativi.header.reset': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroFaultApplicativiHeaderReset;
	}
	
	private static String [] getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset = null;
	private static Boolean getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset_read = null;
	public String [] getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFalliteOFaultApplicativi.header.reset");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset[i]=OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichiesteFalliteOFaultApplicativi.header.reset': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoNumeroRichiesteFalliteOFaultApplicativiHeaderReset;
	}	
	
	
	
	// Header Retry After
	
	private static String [] getControlloTrafficoRetryAfterHeader = null;
	private static Boolean getControlloTrafficoRetryAfterHeader_read = null;
	public String [] getControlloTrafficoRetryAfterHeader() throws Exception {	
		if(OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.policyViolate.retryAfter");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader = name.split(",");
					for (int i = 0; i < OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader.length; i++) {
						OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader[i]=OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader[i].trim();
					}
				}
				
				OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader_read = true;
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.numeroRichieste.header.limit': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader;
	}
		
	private static Integer getControlloTrafficoRetryAfterHeader_randomBackoff = null;
	public Integer getControlloTrafficoRetryAfterHeader_randomBackoff() {	
		if(OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader_randomBackoff==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.controlloTraffico.policyViolate.retryAfter.randomBackoff.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader_randomBackoff = Integer.parseInt(name);
				}
				else {
					OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader_randomBackoff = 0;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.controlloTraffico.policyViolate.retryAfter.randomBackoff.seconds': "+e.getMessage(),e);
				OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader_randomBackoff = 0;
			}    
		}

		return OpenSPCoop2Properties.getControlloTrafficoRetryAfterHeader_randomBackoff;
	}
	
	
	
	
	
	/* ------------- Service Unavailable ---------------------*/
	
	private static Integer getServiceUnavailableRetryAfterSeconds_pd = null;
	public Integer getServiceUnavailableRetryAfterSeconds_pd() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd = Integer.parseInt(name);
				}
				else {
					throw new Exception("Proprietà non definita");
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.seconds', default:60 : "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd = 60;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pd = null;
	public Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pd() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.randomBackoff.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd = Integer.parseInt(name);
				}
				else {
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd = 0;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.randomBackoff.seconds': "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd = 0;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_pa = null;
	public Integer getServiceUnavailableRetryAfterSeconds_pa() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa = Integer.parseInt(name);
				}
				else {
					throw new Exception("Proprietà non definita");
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.seconds', default:60 : "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa = 60;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pa = null;
	public Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pa() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.randomBackoff.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa = Integer.parseInt(name);
				}
				else {
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa = 0;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.randomBackoff.seconds': "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa = 0;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa;
	}
	
	
	
	private static Boolean isEnabledServiceUnavailableRetryAfter_pd_suspend = null;
	public boolean isEnabledServiceUnavailableRetryAfter_pd_suspend() {	
		if(OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pd_suspend==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.suspend.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.suspend.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pd_suspend = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.suspend.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pd_suspend = true;
			}    
		}

		return OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pd_suspend;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_pd_suspend = null;
	public Integer getServiceUnavailableRetryAfterSeconds_pd_suspend() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd_suspend==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.suspend.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd_suspend = Integer.parseInt(name);
				}
				else {
					throw new Exception("Proprietà non definita");
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.suspend.seconds', default:60 : "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd_suspend = 60;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd_suspend;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend = null;
	public Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.suspend.randomBackoff.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend = Integer.parseInt(name);
				}
				else {
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend = 0;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.suspend.randomBackoff.seconds': "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend = 0;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_suspend;
	}
	
	private static Boolean isEnabledServiceUnavailableRetryAfter_pa_suspend = null;
	public boolean isEnabledServiceUnavailableRetryAfter_pa_suspend() {	
		if(OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pa_suspend==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.suspend.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.suspend.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pa_suspend = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.suspend.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pa_suspend = true;
			}    
		}

		return OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pa_suspend;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_pa_suspend = null;
	public Integer getServiceUnavailableRetryAfterSeconds_pa_suspend() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa_suspend==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.suspend.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa_suspend = Integer.parseInt(name);
				}
				else {
					throw new Exception("Proprietà non definita");
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.suspend.seconds', default:60 : "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa_suspend = 60;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa_suspend;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend = null;
	public Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.suspend.randomBackoff.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend = Integer.parseInt(name);
				}
				else {
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend = 0;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.suspend.randomBackoff.seconds': "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend = 0;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_suspend;
	}
	
	private static Boolean isEnabledServiceUnavailableRetryAfter_pd_connectionFailed = null;
	public boolean isEnabledServiceUnavailableRetryAfter_pd_connectionFailed() {	
		if(OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pd_connectionFailed==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.connectionFailed.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.connectionFailed.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pd_connectionFailed = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.connectionFailed.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pd_connectionFailed = true;
			}    
		}

		return OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pd_connectionFailed;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_pd_connectionFailed = null;
	public Integer getServiceUnavailableRetryAfterSeconds_pd_connectionFailed() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd_connectionFailed==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.connectionFailed.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd_connectionFailed = Integer.parseInt(name);
				}
				else {
					throw new Exception("Proprietà non definita");
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.connectionFailed.seconds', default:60 : "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd_connectionFailed = 60;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pd_connectionFailed;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_connectionFailed = null;
	public Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_connectionFailed() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_connectionFailed==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.connectionFailed.randomBackoff.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_connectionFailed = Integer.parseInt(name);
				}
				else {
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_connectionFailed = 0;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pd.connectionFailed.randomBackoff.seconds': "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_connectionFailed = 0;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pd_connectionFailed;
	}
	
	private static Boolean isEnabledServiceUnavailableRetryAfter_pa_connectionFailed = null;
	public boolean isEnabledServiceUnavailableRetryAfter_pa_connectionFailed() {	
		if(OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pa_connectionFailed==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.connectionFailed.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.connectionFailed.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pa_connectionFailed = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.connectionFailed.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pa_connectionFailed = true;
			}    
		}

		return OpenSPCoop2Properties.isEnabledServiceUnavailableRetryAfter_pa_connectionFailed;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_pa_connectionFailed = null;
	public Integer getServiceUnavailableRetryAfterSeconds_pa_connectionFailed() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa_connectionFailed==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.connectionFailed.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa_connectionFailed = Integer.parseInt(name);
				}
				else {
					throw new Exception("Proprietà non definita");
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.connectionFailed.seconds', default:60 : "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa_connectionFailed = 60;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_pa_connectionFailed;
	}
	
	private static Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_connectionFailed = null;
	public Integer getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_connectionFailed() {	
		if(OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_connectionFailed==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.connectionFailed.randomBackoff.seconds");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_connectionFailed = Integer.parseInt(name);
				}
				else {
					OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_connectionFailed = 0;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.retryAfter.pa.connectionFailed.randomBackoff.seconds': "+e.getMessage(),e);
				OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_connectionFailed = 0;
			}    
		}

		return OpenSPCoop2Properties.getServiceUnavailableRetryAfterSeconds_randomBackoff_pa_connectionFailed;
	}
	
	
	private static List<String> isReadTimedOut = null;
	public boolean isServiceUnavailable_ReadTimedOut(String msg) {	
		if(OpenSPCoop2Properties.isReadTimedOut==null){
			try{ 
				Properties p = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.serviceUnavailable.readTimedOut.");
				isReadTimedOut = new ArrayList<String>();
				if(p!=null && !p.isEmpty()) {
					Iterator<Object> it = p.values().iterator();
					while (it.hasNext()) {
						Object o = (Object) it.next();
						if(o!=null && o instanceof String) {
							String s = (String) o;
							isReadTimedOut.add(s);
						}
					}
				}
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.serviceUnavailable.error.': "+e.getMessage(),e);
				isReadTimedOut = new ArrayList<String>();
			}    
		}

		if(isReadTimedOut!=null && !isReadTimedOut.isEmpty()) {
			for (String s : isReadTimedOut) {
				if(msg.contains(s)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	/* ------------- Statistiche ---------------------*/
	
	private static Boolean isStatisticheGenerazioneEnabled = null;
	public boolean isStatisticheGenerazioneEnabled() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneEnabled;
	}
	
	
	private static Boolean isStatisticheUsePddRuntimeDatasource = null;
	public boolean isStatisticheUsePddRuntimeDatasource() {	
		if(OpenSPCoop2Properties.isStatisticheUsePddRuntimeDatasource==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.dataSource.usePddRuntime");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.dataSource.usePddRuntime' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheUsePddRuntimeDatasource = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.dataSource.usePddRuntime', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheUsePddRuntimeDatasource = false;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheUsePddRuntimeDatasource;
	}
	
	private static Boolean isStatisticheUseTransazioniDatasource = null;
	public boolean isStatisticheUseTransazioniDatasource() {	
		if(OpenSPCoop2Properties.isStatisticheUseTransazioniDatasource==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.dataSource.useTransazioni");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.dataSource.useTransazioni' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheUseTransazioniDatasource = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.dataSource.useTransazioni', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheUseTransazioniDatasource = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheUseTransazioniDatasource;
	}
	
	private static String getStatisticheDatasource = null;
	public String getStatisticheDatasource() throws Exception {	
		if(OpenSPCoop2Properties.getStatisticheDatasource==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.dataSource");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getStatisticheDatasource = name;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.dataSource': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getStatisticheDatasource;
	}
	
	private static Properties getStatisticheDatasourceJndiContext = null;
	public Properties getStatisticheDatasourceJndiContext() throws Exception {	
		if(OpenSPCoop2Properties.getStatisticheDatasourceJndiContext==null){
			try{ 
				OpenSPCoop2Properties.getStatisticheDatasourceJndiContext = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.statistiche.dataSource.property.");
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.dataSource.property.*': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getStatisticheDatasourceJndiContext;
	}
	
	private static Boolean isStatisticheDatasourceUseDBUtils = null;
	public boolean isStatisticheDatasourceUseDBUtils() {	
		if(OpenSPCoop2Properties.isStatisticheDatasourceUseDBUtils==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.datasource.useDSUtils");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.datasource.useDSUtils' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheDatasourceUseDBUtils = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.datasource.useDSUtils', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheDatasourceUseDBUtils = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheDatasourceUseDBUtils;
	}
	
	
	private static Boolean isStatisticheGenerazioneCustomEnabled = null;
	public boolean isStatisticheGenerazioneCustomEnabled() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneCustomEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.custom.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.custom.enabled' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneCustomEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.custom.enabled', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneCustomEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneCustomEnabled;
	}
	
	private static Boolean isStatisticheGenerazioneCustomSdkEnabled = null;
	public boolean isStatisticheGenerazioneCustomSdkEnabled() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneCustomSdkEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.custom.transazioniSdk.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.custom.transazioniSdk.enabled' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneCustomSdkEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.custom.transazioniSdk.enabled', viene utilizzato il default=false : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneCustomSdkEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneCustomSdkEnabled;
	}
	
	private static Boolean isStatisticheGenerazioneDebug = null;
	public boolean isStatisticheGenerazioneDebug() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneDebug==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.debug");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.debug' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneDebug = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.debug', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneDebug = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneDebug;
	}
	
	private static Integer getStatisticheOrarieGenerazioneTimerIntervalSeconds = null;
	public int getStatisticheOrarieGenerazioneTimerIntervalSeconds() throws Exception {	
		if(OpenSPCoop2Properties.getStatisticheOrarieGenerazioneTimerIntervalSeconds==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseOraria.timer.intervalloSecondi");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getStatisticheOrarieGenerazioneTimerIntervalSeconds = Integer.valueOf(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseOraria.timer.intervalloSecondi': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getStatisticheOrarieGenerazioneTimerIntervalSeconds;
	}
	
	private static Integer getStatisticheGiornaliereGenerazioneTimerIntervalSeconds = null;
	public int getStatisticheGiornaliereGenerazioneTimerIntervalSeconds() throws Exception {	
		if(OpenSPCoop2Properties.getStatisticheGiornaliereGenerazioneTimerIntervalSeconds==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseGiornaliera.timer.intervalloSecondi");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getStatisticheGiornaliereGenerazioneTimerIntervalSeconds = Integer.valueOf(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseGiornaliera.timer.intervalloSecondi': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getStatisticheGiornaliereGenerazioneTimerIntervalSeconds;
	}
	
	private static Integer getStatisticheSettimanaliGenerazioneTimerIntervalSeconds = null;
	public int getStatisticheSettimanaliGenerazioneTimerIntervalSeconds() throws Exception {	
		if(OpenSPCoop2Properties.getStatisticheSettimanaliGenerazioneTimerIntervalSeconds==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseSettimanale.timer.intervalloSecondi");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getStatisticheSettimanaliGenerazioneTimerIntervalSeconds = Integer.valueOf(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseSettimanale.timer.intervalloSecondi': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getStatisticheSettimanaliGenerazioneTimerIntervalSeconds;
	}
	
	private static Integer getStatisticheMensiliGenerazioneTimerIntervalSeconds = null;
	public int getStatisticheMensiliGenerazioneTimerIntervalSeconds() throws Exception {	
		if(OpenSPCoop2Properties.getStatisticheMensiliGenerazioneTimerIntervalSeconds==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseMensile.timer.intervalloSecondi");
				if(name==null){
					throw new Exception("Proprieta' non impostata");
				}
				name = name.trim();
				OpenSPCoop2Properties.getStatisticheMensiliGenerazioneTimerIntervalSeconds = Integer.valueOf(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseMensile.timer.intervalloSecondi': "+e.getMessage(),e);
				throw e;
			}    
		}

		return OpenSPCoop2Properties.getStatisticheMensiliGenerazioneTimerIntervalSeconds;
	}
	
	private static Integer getStatisticheGenerazioneTimer_lockMaxLife = null;
	public int getStatisticheGenerazioneTimer_lockMaxLife() {	
		if(OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockMaxLife==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.timer.lock.maxLife");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockMaxLife = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.timer.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE);
					OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.timer.lock.maxLife' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_MAX_LIFE+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockMaxLife = CostantiPdD.TIMER_LOCK_MAX_LIFE;
			}
			if(OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockMaxLife!=null && OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockMaxLife>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockMaxLife = OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockMaxLife *1000;
			}
		}

		return OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockMaxLife;
	}
	
	private static Integer getStatisticheGenerazioneTimer_lockIdleTime = null;
	public int getStatisticheGenerazioneTimer_lockIdleTime() {	
		if(OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockIdleTime==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.timer.lock.idleTime");

				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockIdleTime = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.timer.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME);
					OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.timer.lock.idleTime' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_LOCK_IDLE_TIME+", errore:"+e.getMessage(),e);
				OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockIdleTime = CostantiPdD.TIMER_LOCK_IDLE_TIME;
			}
			if(OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockIdleTime!=null && OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockIdleTime>0) {
				// trasformo in millisecondi l'informazione fornita in secondi
				OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockIdleTime = OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockIdleTime *1000;
			}
		}

		return OpenSPCoop2Properties.getStatisticheGenerazioneTimer_lockIdleTime;
	}
	
	private static Boolean isStatisticheGenerazioneBaseOrariaEnabled = null;
	public boolean isStatisticheGenerazioneBaseOrariaEnabled() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneBaseOrariaEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseOraria.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseOraria.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseOrariaEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseOraria.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseOrariaEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneBaseOrariaEnabled;
	}
	
	private static Boolean isStatisticheGenerazioneBaseGiornalieraEnabled = null;
	public boolean isStatisticheGenerazioneBaseGiornalieraEnabled() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneBaseGiornalieraEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseGiornaliera.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseGiornaliera.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseGiornalieraEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseGiornaliera.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseGiornalieraEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneBaseGiornalieraEnabled;
	}
	
	private static Boolean isStatisticheGenerazioneBaseSettimanaleEnabled = null;
	public boolean isStatisticheGenerazioneBaseSettimanaleEnabled() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneBaseSettimanaleEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseSettimanale.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseSettimanale.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseSettimanaleEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseSettimanale.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseSettimanaleEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneBaseSettimanaleEnabled;
	}
	
	private static Boolean isStatisticheGenerazioneBaseMensileEnabled = null;
	public boolean isStatisticheGenerazioneBaseMensileEnabled() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneBaseMensileEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseMensile.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseMensile.enabled' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseMensileEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseMensile.enabled', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseMensileEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneBaseMensileEnabled;
	}
	
	
	private static Boolean isStatisticheGenerazioneBaseOrariaEnabledUltimaOra = null;
	public boolean isStatisticheGenerazioneBaseOrariaEnabledUltimaOra() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneBaseOrariaEnabledUltimaOra==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseOraria.gestioneUltimaOra");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseOraria.gestioneUltimaOra' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseOrariaEnabledUltimaOra = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseOraria.gestioneUltimaOra', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseOrariaEnabledUltimaOra = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneBaseOrariaEnabledUltimaOra;
	}
	
	private static Boolean isStatisticheGenerazioneBaseGiornalieraEnabledUltimoGiorno = null;
	public boolean isStatisticheGenerazioneBaseGiornalieraEnabledUltimoGiorno() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneBaseGiornalieraEnabledUltimoGiorno==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseGiornaliera.gestioneUltimoGiorno");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseGiornaliera.gestioneUltimoGiorno' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseGiornalieraEnabledUltimoGiorno = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseGiornaliera.gestioneUltimoGiorno', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseGiornalieraEnabledUltimoGiorno = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneBaseGiornalieraEnabledUltimoGiorno;
	}
	
	private static Boolean isStatisticheGenerazioneBaseSettimanaleEnabledUltimaSettimana = null;
	public boolean isStatisticheGenerazioneBaseSettimanaleEnabledUltimaSettimana() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneBaseSettimanaleEnabledUltimaSettimana==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseSettimanale.gestioneUltimaSettimana");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseSettimanale.gestioneUltimaSettimana' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseSettimanaleEnabledUltimaSettimana = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseSettimanale.gestioneUltimaSettimana', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseSettimanaleEnabledUltimaSettimana = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneBaseSettimanaleEnabledUltimaSettimana;
	}
	
	private static Boolean isStatisticheGenerazioneBaseMensileEnabledUltimoMese = null;
	public boolean isStatisticheGenerazioneBaseMensileEnabledUltimoMese() {	
		if(OpenSPCoop2Properties.isStatisticheGenerazioneBaseMensileEnabledUltimoMese==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.baseMensile.gestioneUltimoMese");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseMensile.gestioneUltimoMese' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseMensileEnabledUltimoMese = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.baseMensile.gestioneUltimoMese', viene utilizzato il default=true : "+e.getMessage(),e);
				OpenSPCoop2Properties.isStatisticheGenerazioneBaseMensileEnabledUltimoMese = true;
			}    
		}

		return OpenSPCoop2Properties.isStatisticheGenerazioneBaseMensileEnabledUltimoMese;
	}

	// NOTA: DEVE ESSERE RILETTO TUTTE LE VOLTE CHE VIENE CHIAMATO!!!!!!!
	public StatisticsForceIndexConfig getStatisticheGenerazioneExternalForceIndexRepository() throws Exception {	
		try{ 
			String name = null;
			name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.statistiche.generazione.forceIndex.repository");
			if(name!=null){
				name = name.trim();
				InputStream is = null;
				try {
					File f = new File(name);
					if(f.exists()) {
						if(f.isDirectory()) {
							throw new Exception("File ["+f+"] is directory");
						}
						if(f.canRead()==false) {
							throw new Exception("File ["+f+"] cannot read");
						}
						is = new FileInputStream(f);
					}
					else {
						// provo a cercarlo nel classpath
						is = OpenSPCoop2Properties.class.getResourceAsStream(name);
					}
					if(is!=null) {
						Properties p = new Properties();
						p.load(is);
						return new StatisticsForceIndexConfig(p);
					}
				}finally{
					try {
						if(is!=null) {
							is.close();
						}
					}catch(Exception eClose) {}
				}
			}
			
			return null;
			
		} catch(java.lang.Exception e) {
			this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.statistiche.generazione.forceIndex.repository': "+e.getMessage(),e);
			throw e;
		}    
		
	}
}

