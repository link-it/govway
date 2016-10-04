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


import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autenticazione.IGestoreCredenziali;
import org.openspcoop2.pdd.core.autenticazione.IGestoreCredenzialiIM;
import org.openspcoop2.pdd.core.autorizzazione.pa.IAutorizzazionePortaApplicativa;
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
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePA;
import org.openspcoop2.pdd.core.integrazione.IGestoreIntegrazionePD;
import org.openspcoop2.pdd.core.node.INodeReceiver;
import org.openspcoop2.pdd.core.node.INodeSender;
import org.openspcoop2.pdd.core.threshold.IThreshold;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.Imbustamento;
import org.openspcoop2.pdd.mdb.ImbustamentoRisposte;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.mdb.Sbustamento;
import org.openspcoop2.pdd.mdb.SbustamentoRisposte;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.services.RicezioneBuste;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativi;
import org.openspcoop2.pdd.timers.TimerGestoreBusteNonRiscontrate;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomali;
import org.openspcoop2.pdd.timers.TimerGestoreRepositoryBuste;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.IFiltroDuplicati;
import org.openspcoop2.protocol.engine.driver.repository.GestoreRepositoryFactory;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.protocol.sdk.BypassMustUnderstandCheck;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.SOAPFaultIntegrationGenericInfoMode;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.date.IDate;
import org.openspcoop2.utils.digest.IDigestReader;
import org.openspcoop2.utils.id.IUniqueIdentifierGenerator;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.RFC2047Encoding;
import org.openspcoop2.utils.sql.ISQLQueryObject;

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

	/** Reader delle proprieta' impostate nel file 'openspcoop2.properties' */
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
			this.log = LoggerWrapperFactory.getLogger("openspcoop2.startup");

		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = OpenSPCoop2Properties.class.getResourceAsStream("/openspcoop2.properties");
			if(properties==null){
				throw new Exception("File '/openspcoop2.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'openspcoop2.properties': \n\n"+e.getMessage());
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
			
			// Repository
			String tipoRepository = getRepositoryType();
			if(CostantiConfigurazione.REPOSITORY_FILE_SYSTEM.equals(tipoRepository)){
				if (getRepositoryDirectory() == null)	{						
					return false;
				}
				if( (new File(getRepositoryDirectory())).exists() == false ){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.directory'. \n La directory indicata non esiste ["+getRepositoryDirectory()+"].");
					return false;
				}
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
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.jdbcAdapter'. \n L'adapter indicato non esiste ["+getRepositoryJDBCAdapter()+"]: "+e.getMessage());
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
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.jdbcAdapter'. \n L'adapter indicato non esiste ["+getRepositoryJDBCAdapter()+"]: "+e.getMessage());
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
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.threshold.tipo'. \n La classe di Threshold indicata non esiste ["+tipiThreshold[i]+"]: "+e.getMessage());
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
			}


			// Tipo di Configurazione
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
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.config.location', mentre veniva analizzato il prefisso tipoDatabase@datasource: "+e.getMessage());
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
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.nodeReceiver'. \n Il node receiver indicato non esiste ["+this.getNodeReceiver()+"]: "+e.getMessage());
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
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.nodeSender'. \n Il node sender indicato non esiste ["+this.getNodeSender()+"]: "+e.getMessage());
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
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.jdbcAdapter'. \n L'adapter indicato non esiste ["+getRepositoryJDBCAdapter()+"]: "+e.getMessage());
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
							this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.repository.jdbcAdapter'. \n L'adapter indicato non esiste ["+getRepositoryJDBCAdapter()+"]: "+e.getMessage());
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
			
			this.isFollowRedirects_consegnaContenutiApplicativi();
			this.isFollowRedirects_inoltroBuste();
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
			
			this.isTimerGestoreMessaggiAbilitato();
			this.isTimerGestoreMessaggiAbilitatoOrderBy();
			this.isTimerGestoreMessaggiAbilitatoLog();
			this.getTimerGestoreMessaggiLimit();
			this.isTimerGestoreMessaggiVerificaConnessioniAttive();
			
			this.isTimerGestorePuliziaMessaggiAnomaliAbilitato();
			this.isTimerGestorePuliziaMessaggiAnomaliAbilitatoOrderBy();
			this.isTimerGestorePuliziaMessaggiAnomaliAbilitatoLog();
			this.getTimerGestorePuliziaMessaggiAnomaliLimit();
			
			this.isTimerGestoreRepositoryBusteAbilitato();
			this.isTimerGestoreRepositoryBusteAbilitatoOrderBy();
			this.isTimerGestoreRepositoryBusteAbilitatoLog();
			this.getTimerGestoreRepositoryBusteLimit();
			
			this.isTimerConsegnaContenutiApplicativiAbilitato();
			this.isTimerConsegnaContenutiApplicativiAbilitatoOrderBy();
			this.isTimerConsegnaContenutiApplicativiAbilitatoLog();
			this.getTimerConsegnaContenutiApplicativiLimit();
			this.getTimerConsegnaContenutiApplicativiInterval();
				
			
			
			// Gestione Serializable DB (Warning)
			this.getGestioneSerializableDB_AttesaAttiva();
			this.getGestioneSerializableDB_CheckInterval();

			// GestioneErrore
			ProprietaErroreApplicativo paError = getProprietaGestioneErrorePD_engine(null,true);
			if( paError == null  ){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.erroreApplicativo'.");
				return false;
			}

			// IdentitaPdD
			if( this.getIdentitaPortaDefault() == null  ){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.identificativoPorta'.");
				return false;
			}

			// Integrazione tra Servizi Applicativi e Porta di Dominio
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

			// Integrazione tra Porta di Dominio e Servizi Applicativi
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
					String tipoClass = className.getAutorizzazioneBuste(this.getTipoAutorizzazioneBuste());
					if(tipoClass == null){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.autorizzazioneBuste'. \n L'autorizzazione delle buste indicata non esiste ["+this.getTipoAutorizzazioneBuste()+"] nelle classi registrate in OpenSPCoop");
						return false;
					}
					try{
						IAutorizzazionePortaApplicativa auth = (IAutorizzazionePortaApplicativa) loaderOpenSPCoop.newInstance(tipoClass);
						auth.toString();
					}catch(Exception e){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.autorizzazioneBuste'. \n L'autorizzazione delle buste indicata non esiste ["+this.getTipoAutorizzazioneBuste()+"]: "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.protocol.filtroDuplicati'. \n Il gestore filtro duplicati del repository buste indicato non esiste ["+this.getGestoreFiltroDuplicatiRepositoryBuste()+"]: "+e.getMessage());
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
			String tipoDateManger = className.getDateManager(this.getTipoDateManager());
			if(tipoDateManger == null){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.date.tipo'. \n Il DateManager indicato non esiste ["+this.getTipoDateManager()+"] nelle classi registrate in OpenSPCoop");
				return false;
			}
			try{
				IDate date = (IDate) loaderOpenSPCoop.newInstance(tipoDateManger);
				date.toString();
			}catch(Exception e){
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.date.tipo'. \n Il DateManager indicato non esiste ["+this.getTipoDateManager()+"]: "+e.getMessage());
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
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.services.pd.gestoriCredenziali'. \n La classe del GestoreCredenziali indicata non esiste ["+gestoriCredenzialiPD[i]+"]: "+e.getMessage());
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
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.services.pa.gestoriCredenziali'. \n La classe del GestoreCredenziali indicata non esiste ["+gestoriCredenzialiPA[i]+"]: "+e.getMessage());
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
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.services.integrationManager.gestoriCredenziali'. \n La classe del GestoreCredenziali indicata non esiste ["+gestoriCredenzialiIM[i]+"]: "+e.getMessage());
						return false;
					}
				}
			}
			
			// warning Risposta Asincrona
			this.getTimeoutBustaRispostaAsincrona();
			this.getCheckIntervalBustaRispostaAsincrona();

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

			this.isPrintInfoHandler();
			
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
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
								"'. \n La classe non esiste: "+e.getMessage());
						return false;
					}
				}
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
			
			// Dump
			this.isDumpAllAttachments();

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
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.idGenerator'. \n Il generatore di unique identifier non esiste ["+this.getTipoIDManager()+"]: "+e.getMessage());
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
							"'. \n La classe non esiste: "+e.getMessage());
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
							"'. \n La classe non esiste: "+e.getMessage());
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
							"'. \n La classe non esiste: "+e.getMessage());
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
			
			// Informazioni generazione errori
			this.isGenerazioneErroreProtocolloNonSupportato();
			this.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled();
			this.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataImbustamentoSOAPEnabled();
			this.isGenerazioneErroreHttpMethodUnsupportedPortaApplicativaEnabled();
			this.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled();
			this.isGenerazioneErroreHttpMethodUnsupportedCheckPdDEnabled();
			
			// Informazioni generazione WSDL
			this.isGenerazioneWsdlPortaDelegataEnabled();
			this.isGenerazioneWsdlPortaApplicativaEnabled();
			this.isGenerazioneWsdlIntegrationManagerEnabled();
			
			// CheckPdD
			this.isCheckPdDReadJMXResourcesEnabled();
			this.getCheckPdDReadJMXResourcesUsername();
			this.getCheckPdDReadJMXResourcesPassword();
			
			// API Services
			this.isAPIServicesEnabled();
			this.getAPIServicesWhiteListRequestHeaderList();
			this.getAPIServicesWhiteListResponseHeaderList();
			
			// Datasource Wrapped
			this.isDSOp2UtilsEnabled();
			
			// JminixConsole
			this.getPortJminixConsole();
			
			// NotifierInputStreamCallback
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
							"'. \n La classe non esiste: "+e.getMessage());
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
					this.log.error("La classe ["+extendedInfoConfigurazione+"], indicata nella proprieta' 'org.openspcoop2.pdd.config.extendedInfo.configurazione', non esiste: "+e.getMessage());
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
					this.log.error("La classe ["+extendedInfoPortaDelegata+"], indicata nella proprieta' 'org.openspcoop2.pdd.config.extendedInfo.portaDelegata', non esiste: "+e.getMessage());
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
					this.log.error("La classe ["+extendedInfoPortaApplicativa+"], indicata nella proprieta' 'org.openspcoop2.pdd.config.extendedInfo.portaApplicativa', non esiste: "+e.getMessage());
					return false;
				} 
			}
			
			return true;

		}catch(java.lang.Exception e) {
			this.log.error("Riscontrato errore durante la validazione lettura della proprieta' di openspcoop: "+e.getMessage(),e);
			return false;
		}
	}







	private boolean checkTipiIntegrazione(String[] tipiIntegrazione){
		// Check KeyWord per tipi 'trasporto' e 'urlBased' e 'soap'
		for(int i=0; i<tipiIntegrazione.length;i++){
			if(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO.equals(tipiIntegrazione[i]) ||
					CostantiConfigurazione.HEADER_INTEGRAZIONE_URL_BASED.equals(tipiIntegrazione[i]) ||
							CostantiConfigurazione.HEADER_INTEGRAZIONE_SOAP.equals(tipiIntegrazione[i]) ){
				
				java.util.Properties prop = null;
				String tipo = "";
				if(CostantiConfigurazione.HEADER_INTEGRAZIONE_TRASPORTO.equals(tipiIntegrazione[i])){
					if ( this.getKeyValue_HeaderIntegrazioneTrasporto() == null ){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.trasporto.keyword.*'.");
						return false;
					}
					prop = this.getKeyValue_HeaderIntegrazioneTrasporto();
					tipo="trasporto";
				}else if(CostantiConfigurazione.HEADER_INTEGRAZIONE_URL_BASED.equals(tipiIntegrazione[i])){
					if ( this.getKeyValue_HeaderIntegrazioneUrlBased() == null ){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.urlBased.keyword.*'.");
						return false;
					}
					prop = this.getKeyValue_HeaderIntegrazioneUrlBased();
					tipo="urlBased";
				}else if(CostantiConfigurazione.HEADER_INTEGRAZIONE_SOAP.equals(tipiIntegrazione[i])){
					if ( this.getKeyValue_HeaderIntegrazioneSoap() == null ){
						this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione.soap.keyword.*'.");
						return false;
					}
					prop = this.getKeyValue_HeaderIntegrazioneSoap();
					tipo="soap";
				}
				
				
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_MITTENTE) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_TIPO_MITTENTE+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_MITTENTE) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_MITTENTE+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_DESTINATARIO) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_TIPO_DESTINATARIO+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_DESTINATARIO) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_DESTINATARIO+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_SERVIZIO) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_TIPO_SERVIZIO+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_AZIONE) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_AZIONE+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_MESSAGGIO) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_ID_MESSAGGIO+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_COLLABORAZIONE) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_COLLABORAZIONE+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO_RICHIESTA) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO_RICHIESTA+"'.");
					return false;
				}
				if( prop.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO_APPLICATIVO) == null){
					this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop: 'org.openspcoop2.pdd.integrazione."+tipo+".keyword."+CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO_APPLICATIVO+"'.");
					return false;
				}
				break;
			}
			
			
			if(CostantiConfigurazione.HEADER_INTEGRAZIONE_SOAP.equals(tipiIntegrazione[i])){
				if ( this.getHeaderSoapNameIntegrazione() == null ){
					return false;
				}
				if ( this.getHeaderSoapActorIntegrazione() == null ){
					return false;
				}
				if ( this.getHeaderSoapPrefixIntegrazione() == null ){
					return false;
				}
				if ( this.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione() == null ){
					return false;
				}
				if ( this.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione() == null ){
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop, 'org.openspcoop2.pdd.confDirectory': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop, 'org.openspcoop2.pdd.server': "+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.classLoader' non impostata, errore:"+e.getMessage());
			}
			OpenSPCoop2Properties.getClassLoaderRead = true;
		}

		return OpenSPCoop2Properties.getClassLoader;
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
					OpenSPCoop2Properties.versione = CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
				}
			}catch(java.lang.Exception e) {
				OpenSPCoop2Properties.versione = CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
			}    
		}
		return OpenSPCoop2Properties.versione;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.config.location': "+e.getMessage());
				OpenSPCoop2Properties.pathConfigurazionePDD = null;
			}
		}

		return OpenSPCoop2Properties.pathConfigurazionePDD;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.config.tipo': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.config.db.condivisioneDBRegserv' (Viene utilizzato il default:false): "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI per la configurazione di openspcoop 'org.openspcoop2.pdd.config.property.*': "+e.getMessage());
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
	 * Restituisce l'indicazione se la configurazione della Porta di Dominio
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.config.refresh' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				OpenSPCoop2Properties.isConfigurazioneDinamica_value = true;
			}
		}

		return OpenSPCoop2Properties.isConfigurazioneDinamica_value;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.dataSource': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI per il datasource di openspcoop 'org.openspcoop2.pdd.dataSource.property.*': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.queueConnectionFactory': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI del ConnectionFactory di openspcoop 'org.openspcoop2.pdd.connectionFactory.property.*': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.queueConnectionFactory.session.AcknowledgeMode' non impostata, viene utilizzato il default=AUTO_ACKNOWLEDGE, errore:"+e.getMessage());
				OpenSPCoop2Properties.acknowledgeModeSessioneConnectionFactory = javax.jms.Session.AUTO_ACKNOWLEDGE; // Default
			}    
		}

		return OpenSPCoop2Properties.acknowledgeModeSessioneConnectionFactory;
	}








	/* ********  CODE JMS DI OPENSPCOOP  ******** */

	public java.util.Hashtable<String,String> getJNDIQueueName(boolean receiverJMSActive,boolean senderJMSActive){
		java.util.Hashtable<String,String> table = new java.util.Hashtable<String,String>();
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
			this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop 'org.openspcoop2.pdd.queue.property.*': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI delle code di openspcoop 'org.openspcoop2.pdd.queue.property.*': "+e.getMessage());
				OpenSPCoop2Properties.jndiContext_CodeInterne = null;
			}    
		}

		return OpenSPCoop2Properties.jndiContext_CodeInterne;
	}














	/* ********  Timer EJB DI OPENSPCOOP  ******** */

	public java.util.Hashtable<String,String> getJNDITimerEJBName(){
		java.util.Hashtable<String,String> table = new java.util.Hashtable<String,String>();
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
			this.log.error("Riscontrato errore durante la lettura dei nomi JNDI delle code di openspcoop 'org.openspcoop2.pdd.queue.property.*': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI dei timer di openspcoop 'org.openspcoop2.pdd.timer.property.*': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.autoStart.stop', viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.enable', viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.logQuery', viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreBusteNonRiscontrate.query.limit', viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI+", errore:"+e.getMessage());
				OpenSPCoop2Properties.getTimerGestoreRiscontriRicevuteLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreRiscontriRicevuteLimit;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.enable', viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.orderBy', viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.logQuery', viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.query.limit', viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreMessaggi.verificaConnessioniAttive', viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isTimerGestoreMessaggiVerificaConnessioniAttive = false;
			}
		}

		return OpenSPCoop2Properties.isTimerGestoreMessaggiVerificaConnessioniAttive;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.enable', viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.orderBy', viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.logQuery', viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestorePuliziaMessaggiAnomali.query.limit', viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI+", errore:"+e.getMessage());
				OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomaliLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
			}
		}

		return OpenSPCoop2Properties.getTimerGestorePuliziaMessaggiAnomaliLimit;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.enable', viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.orderBy', viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.logQuery', viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.gestoreRepositoryBuste.query.limit', viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI+", errore:"+e.getMessage());
				OpenSPCoop2Properties.getTimerGestoreRepositoryBusteLimit = CostantiPdD.LIMIT_MESSAGGI_GESTORI;
			}
		}

		return OpenSPCoop2Properties.getTimerGestoreRepositoryBusteLimit;
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
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.enable', viene utilizzato il default=true, errore:"+e.getMessage());
					OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitato = true;
				}
			}

		}
		
		return OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitato;
	}
	
	/**
	 * Restituisce l'indicazione se usare l'order by nelle queries
	 *
	 * @return Restituisce indicazione se usare l'order by nelle queries
	 * 
	 */
	private static Boolean isTimerConsegnaContenutiApplicativiAbilitatoOrderBy = null;
	public boolean isTimerConsegnaContenutiApplicativiAbilitatoOrderBy(){
		if(OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitatoOrderBy==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.orderBy"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitatoOrderBy = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.orderBy' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitatoOrderBy = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.orderBy', viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitatoOrderBy = false;
			}
		}

		return OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitatoOrderBy;
	}
	
	/**
	 * Restituisce l'indicazione se registrare su log le queries
	 *
	 * @return Restituisce indicazione se registrare su log le queries
	 * 
	 */
	private static Boolean isTimerConsegnaContenutiApplicativiAbilitatoLog = null;
	public boolean isTimerConsegnaContenutiApplicativiAbilitatoLog(){
		if(OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitatoLog==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.logQuery"); 
				if(value!=null){
					value = value.trim();
					OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitatoLog = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.logQuery' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitatoLog = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.logQuery', viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitatoLog = false;
			}
		}

		return OpenSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitatoLog;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.query.limit', viene utilizzato il default="+CostantiPdD.LIMIT_MESSAGGI_GESTORI+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.consegnaContenutiApplicativi.intervallo' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_INTERVAL+", errore:"+e.getMessage());
				OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiInterval = CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_INTERVAL;
			}  
		}

		return OpenSPCoop2Properties.getTimerConsegnaContenutiApplicativiInterval;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.tipo': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.tipoDatabase': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.tipo' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.directory': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.jdbcAdapter': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.forceIndex' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.forceIndex = false;
			}    
		}

		return OpenSPCoop2Properties.forceIndex;
	}
	
	

	/**
	 * restituisce le preferenze di gestione attachment per l'implementazione Axiom
	 *
	 * @return Restituisce le preferenze di gestione attachment per l'implementazione Axiom
	 * 
	 */
	
	private static Boolean isFileCacheEnable = null;
	public boolean isFileCacheEnable() {	
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.fileCacheEnable': "+e.getMessage());
				OpenSPCoop2Properties.isFileCacheEnable = false;
			}    
		}

		return OpenSPCoop2Properties.isFileCacheEnable;
	}


	private static String attachmentRepoDir = null;
	public String getAttachmentRepoDir() {	

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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.repositoryDir': "+e.getMessage());
				OpenSPCoop2Properties.attachmentRepoDir = null;
			}    
		}

		return OpenSPCoop2Properties.attachmentRepoDir;
	}
	
	private static String fileThreshold = null;
	public String getFileThreshold() {	

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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.fileThreshold': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.filePrefix': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.fileSuffix': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.attachment.deleteInterval': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.timer': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaMessaggio': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaMessaggio.filtraBusteScaduteRispettoOraRegistrazione' (Viene utilizzato il default:true): "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa': "+e.getMessage());
				OpenSPCoop2Properties.repositoryIntervalloScadenzaCorrelazioneApplicativa = -1L;
			}    
		}
		
		return OpenSPCoop2Properties.repositoryIntervalloScadenzaCorrelazioneApplicativa;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa.filtraCorrelazioniScaduteRispettoOraRegistrazione' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.scadenzaCorrelazioneApplicativa.filtraCorrelazioniScaduteRispettoOraRegistrazione.soloCorrelazioniSenzaScadenza' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioInProcessamento.attesaAttiva' non impostato, viene utilizzato il default="+CostantiPdD.MSG_GIA_IN_PROCESSAMENTO_ATTESA_ATTIVA+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioInProcessamento.check' non impostato, viene utilizzato il default="+CostantiPdD.MSG_GIA_IN_PROCESSAMENTO_CHECK_INTERVAL+", errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.threshold.tipi': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.threshold."+tipoThreshould+".*': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.threshold.checkInterval': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.config.xml.validazioneSemantica' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.config.validazioneSemantica' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.registri.xml.validazioneSemantica' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.registri.validazioneSemantica' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.startup.registri.validazioneSemantica.checkURI' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
			this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.risorse.check."+tipo+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.risorse.check.registri.tipo' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.risorse.checkInterval': "+e.getMessage());
				OpenSPCoop2Properties.controlloRisorseCheckInterval = -1L;
			}    
		}

		return OpenSPCoop2Properties.controlloRisorseCheckInterval;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.fault': "+e.getMessage());
				return null;
			}

			String faultActor = null;
			try{  
				faultActor = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.erroreApplicativo.faultActor"); 
				if(faultActor==null)
					throw new Exception("non definita");
				faultActor = faultActor.trim();		
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.faultActor': "+e.getMessage());
				return null;
			}

			String faultGeneric = null;
			try{  
				faultGeneric = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.erroreApplicativo.genericFaultCode"); 
				if(faultGeneric==null)
					throw new Exception("non definita");
				faultGeneric = faultGeneric.trim();		
			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.genericFaultCode': "+e.getMessage());
				return null;
			}

			String faultPrefix = null;
			try{  
				faultPrefix = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.erroreApplicativo.prefixFaultCode"); 
				if(faultPrefix!=null)
					faultPrefix = faultPrefix.trim();		
			}catch(java.lang.Exception e) {
				// proprieta' che puo' non essere definita (default OPENSPCOOP2_ORG_)
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
			SOAPFaultIntegrationGenericInfoMode sf = protocolManager.getModalitaGenerazioneInformazioniGeneriche_DetailsSOAPFaultIntegrazione();
			if(SOAPFaultIntegrationGenericInfoMode.SERVIZIO_APPLICATIVO.equals(sf)){
				pNew.setInformazioniGenericheDetailsOpenSPCoop(null);
			}
			else if(SOAPFaultIntegrationGenericInfoMode.ABILITATO.equals(sf)){
				pNew.setInformazioniGenericheDetailsOpenSPCoop(true);
			} 
			else if(SOAPFaultIntegrationGenericInfoMode.DISABILITATO.equals(sf)){
				pNew.setInformazioniGenericheDetailsOpenSPCoop(false);
			} 
			
			Boolean enrich = protocolManager.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
			if(enrich!=null){
				pNew.setAggiungiDetailErroreApplicativo_SoapFaultApplicativo(enrich);
			}
			
			enrich = protocolManager.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.fault.details' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = true;
			}
		}

		return OpenSPCoop2Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.identificativoPorta.dominio': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.identificativoPorta.nome': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.identificativoPorta.tipo': "+e.getMessage());
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
	
	private static Hashtable<String,String> identificativoPortaDefault_mappingProtocol = new Hashtable<String, String>();
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd."+protocol+".identificativoPorta.dominio': "+e.getMessage());
				return this.getIdentificativoPortaDefault();
			}
		}

		return OpenSPCoop2Properties.identificativoPortaDefault_mappingProtocol.get(protocol);
	}

	private static Hashtable<String,String> nomePortaDefault_mappingProtocol = new Hashtable<String, String>();
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd."+protocol+".identificativoPorta.nome': "+e.getMessage());
				return this.getNomePortaDefault();
			}
		}

		return OpenSPCoop2Properties.nomePortaDefault_mappingProtocol.get(protocol);
	}
	
	private static Hashtable<String,String> tipoPortaDefault_mappingProtocol = new Hashtable<String, String>();
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd."+protocol+".identificativoPorta.tipo': "+e.getMessage());
				return this.getTipoPortaDefault();
			}
		}

		return OpenSPCoop2Properties.tipoPortaDefault_mappingProtocol.get(protocol);
	}

	private static Hashtable<String,IDSoggetto> identitaPortaDefault_mappingProtocol = new Hashtable<String, IDSoggetto>();
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.autorizzazioneBuste.tipo': "+e.getMessage());
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
	private static Hashtable<String,List<NameValue>> mapGetBypassFilterMustUnderstandProperties = null;
	//private static java.util.Properties getBypassFilterMustUnderstandProperties = null;
	public List<NameValue> getBypassFilterMustUnderstandProperties(String protocol) {
		if(OpenSPCoop2Properties.mapGetBypassFilterMustUnderstandProperties==null){
			initBypassFilterMustUnderstandProperties();
		}
		return OpenSPCoop2Properties.mapGetBypassFilterMustUnderstandProperties.get(protocol);
	}
	public void initBypassFilterMustUnderstandProperties(){
		if(OpenSPCoop2Properties.mapGetBypassFilterMustUnderstandProperties==null){
			
			OpenSPCoop2Properties.mapGetBypassFilterMustUnderstandProperties = new Hashtable<String, List<NameValue>>();
			
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
					IProtocolFactory pf = ProtocolFactoryManager.getInstance().getProtocolFactories().get(protocollo);
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
				this.log.error("Riscontrato errore durante la lettura delle propriete' 'org.openspcoop2.pdd.services.BypassMustUnderstandHandler.header.*': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.BypassMustUnderstandHandler.allHeaders' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.contentType.checkEnabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.contentType.checkEnabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.certificate.printInfo' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isPrintInfoCertificate = false;
			}
		}
		return OpenSPCoop2Properties.isPrintInfoCertificate;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.timeout' non impostata, viene utilizzato il default="+CostantiPdD.NODE_RECEIVER_ATTESA_ATTIVA+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.ricezioneContenutiApplicativi.timeout' non impostata, viene utilizzato il default="+this.getNodeReceiverTimeout()+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.ricezioneBuste.timeout' non impostata, viene utilizzato il default="+this.getNodeReceiverTimeout()+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.check' non impostata, viene utilizzato il default="+CostantiPdD.NODE_RECEIVER_CHECK_INTERVAL+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.checkDB' non impostata, viene utilizzato il default="+CostantiPdD.NODE_RECEIVER_CHECK_DB_INTERVAL+", errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.nodeReceiver.singleConnection' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.nodeSender': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.timeout' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_EJB_ATTESA_ATTIVA+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.timer.check' non impostata, viene utilizzato il default="+CostantiPdD.TIMER_EJB_CHECK_INTERVAL+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.attesaAttiva' non impostata, viene utilizzato il default="+CostantiPdD.TRANSACTION_MANAGER_ATTESA_ATTIVA+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.check' non impostata, viene utilizzato il default="+CostantiPdD.TRANSACTION_MANAGER_CHECK_INTERVAL+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.checkDB' non impostata, viene utilizzato il default="+CostantiPdD.TRANSACTION_MANAGER_CHECK_DB_INTERVAL+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.TransactionManager.singleConnection' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.jdbc.serializable.attesaAttiva' non impostata, viene utilizzato il default="+Costanti.GESTIONE_SERIALIZABLE_ATTESA_ATTIVA+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.jdbc.serializable.check' non impostata, viene utilizzato il default="+Costanti.GESTIONE_SERIALIZABLE_CHECK_INTERVAL+", errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.repository.gestore': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.filtroDuplicati' non impostata, viene utilizzato il default="+CostantiConfigurazione.FILTRO_DUPLICATI_OPENSPCOOP+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.asincroni.attributiCorrelati.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.validazione.ignoraEccezioniNonGravi' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.backwardCompatibility.forceSoapPrefix' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasmissione.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.filtroduplicati.generazioneErrore' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.filtroDuplicati.letturaRegistro' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.confermaRicezione.letturaRegistro' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.consegnaInOrdine.letturaRegistro' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.collaborazione.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				OpenSPCoop2Properties.isGestioneElementoCollaborazione = true;
			}
		}

		return OpenSPCoop2Properties.isGestioneElementoCollaborazione;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.consegnaInOrdine.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.riscontri.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.validazione.readQualifiedAttribute' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.validazione.idbusta.validazioneCompleta' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
	 * Restituisce l'eventuale location del file openspcoop2.pdd.properties
	 * 
	 * @return Restituisce la location del file openspcoop2.pdd.properties
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.pddProperties': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.tipo.pd': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di integrazione tra porta di dominio e servizio applicativo 'org.openspcoop2.pdd.integrazione.tipo.pa': "+e.getMessage());
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
	private static Hashtable<String, String[]> tipoIntegrazionePD_perProtocollo = new Hashtable<String, String[]>();
	private static Hashtable<String, Boolean> tipoIntegrazionePD_perProtocollo_notExists = new Hashtable<String, Boolean>();
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.tipo.pd."+protocollo+"': "+e.getMessage());
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
	private static Hashtable<String, String[]> tipoIntegrazionePA_perProtocollo = new Hashtable<String, String[]>();
	private static Hashtable<String, Boolean> tipoIntegrazionePA_perProtocollo_notExists = new Hashtable<String, Boolean>();
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.tipo.pa."+protocollo+"': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.asincroni.idCollaborazione.enabled' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
	private static java.util.Properties keyValue_HeaderIntegrazioneTrasporto = null;
	public java.util.Properties getKeyValue_HeaderIntegrazioneTrasporto() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.trasporto.keyword.");
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.trasporto.keyword.*': "+e.getMessage());
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneTrasporto;
	}
	
	/**
	 * Restituisce le proprieta' che identificano gli header di integrazione in caso di 'urlBased'.
	 *
	 * @return Restituisce le proprieta' che identificano gli header di integrazione in caso di 'urlBased'.
	 *  
	 */
	private static java.util.Properties keyValue_HeaderIntegrazioneUrlBased = null;
	public java.util.Properties getKeyValue_HeaderIntegrazioneUrlBased() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.urlBased.keyword.");
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.urlBased.keyword.*': "+e.getMessage());
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneUrlBased;
	}
	
	/**
	 * Restituisce le proprieta' che identificano gli header di integrazione in caso di 'soap'.
	 *
	 * @return Restituisce le proprieta' che identificano gli header di integrazione in caso di 'soap'.
	 *  
	 */
	private static java.util.Properties keyValue_HeaderIntegrazioneSoap = null;
	public java.util.Properties getKeyValue_HeaderIntegrazioneSoap() {	
		if(OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap==null){

			java.util.Properties prop = new java.util.Properties();
			try{ 

				prop = this.reader.readProperties_convertEnvProperties("org.openspcoop2.pdd.integrazione.soap.keyword.");
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap = prop;

			}catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura delle proprieta' 'org.openspcoop2.pdd.integrazione.soap.keyword.*': "+e.getMessage());
				OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap = null;
			}    
		}

		return OpenSPCoop2Properties.keyValue_HeaderIntegrazioneSoap;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.pddDetails' non impostata correttamente: "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pd.request.readAndDelete' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pd.response.readAndDelete' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pd.response.process' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pa.request.readAndDelete' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pa.response.readAndDelete' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.pa.request.process' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.headerName': "+e.getMessage());
				OpenSPCoop2Properties.headerSoapNameIntegrazione = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapNameIntegrazione;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.headerActor': "+e.getMessage());
				OpenSPCoop2Properties.headerSoapActorIntegrazione = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapActorIntegrazione;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.headerPrefix': "+e.getMessage());
				OpenSPCoop2Properties.headerSoapPrefixIntegrazione = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapPrefixIntegrazione;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.extProtocolInfo.elemento.nome': "+e.getMessage());
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeElementoIntegrazione;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.integrazione.soap.extProtocolInfo.attributo.nome': "+e.getMessage());
				OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione = null;
			}    
		}

		return OpenSPCoop2Properties.headerSoapExtProtocolInfoNomeAttributoIntegrazione;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.connettore.ritardo.stato': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.connettore.ritardo.fattore': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.connettore.ritardo.operazione': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.connettore.ritardo.limite': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.enable' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.dimensione': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.algoritmo': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.itemIdleTime': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.repository.gestoreMessaggi.cache.itemLifeSecond': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.jmx.enable' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.core.jmx.jndi.mbeanServer': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura delle propriete' JNDI 'org.openspcoop2.pdd.core.jmx.jndi.property.*': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.connection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_TIMEOUT_INOLTRO_BUSTE+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.connection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.readConnection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_READ_CONNECTION_TIMEOUT_INOLTRO_BUSTE+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.readConnection.timeout' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_READ_CONNECTION_TIMEOUT_CONSEGNA_CONTENUTI_APPLICATIVI+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.connection.life' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_LIFE_INOLTRO_BUSTE+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.connection.life' non impostata, viene utilizzato il default="+CostantiPdD.CONNETTORE_CONNECTION_LIFE_CONSEGNA_CONTENUTI_APPLICATIVI+", errore:"+e.getMessage());
				OpenSPCoop2Properties.connectionLife_consegnaContenutiApplicativi = CostantiPdD.CONNETTORE_CONNECTION_LIFE_CONSEGNA_CONTENUTI_APPLICATIVI;
			}  
		}

		return OpenSPCoop2Properties.connectionLife_consegnaContenutiApplicativi;
	}


	
	
	
	
	/* ***************** Servizi HTTP  ************* */
	
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.WEBSERVER_DEFAULT+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.WEBSERVER_DEFAULT+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.CONTENT_LENGTH+", errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.httpTransferLength.chunkLength' posside un valore non corretto:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.httpTransferLength' non impostata, viene utilizzato il default="+TransferLengthModes.CONTENT_LENGTH+", errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.httpTransferLength.chunkLength' posside un valore non corretto:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.returnCode.2xx.acceptOnly_202_200' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.returnCode.2xx.acceptOnly_202_200' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.returnCode.3xx.acceptOnly_307' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.returnCode.3xx.acceptOnly_307' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi = false;
			}  
		}

		return OpenSPCoop2Properties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi;
	}
	
	private static Boolean isFollowRedirects_inoltroBuste = null;
	public boolean isFollowRedirects_inoltroBuste() {	
		if(OpenSPCoop2Properties.isFollowRedirects_inoltroBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isFollowRedirects_inoltroBuste = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isFollowRedirects_inoltroBuste = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isFollowRedirects_inoltroBuste = false;
			}  
		}

		return OpenSPCoop2Properties.isFollowRedirects_inoltroBuste;
	}
	
	private static Boolean isFollowRedirects_consegnaContenutiApplicativi = null;
	public boolean isFollowRedirects_consegnaContenutiApplicativi() {	
		if(OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects");
				if(name!=null){
					name = name.trim();
					OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi = Boolean.parseBoolean(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects' non impostata, viene utilizzato il default=false");
					OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi = false;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi = false;
			}  
		}

		return OpenSPCoop2Properties.isFollowRedirects_consegnaContenutiApplicativi;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects.maxHop' non impostata, viene utilizzato il default=5, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects.maxHop' non impostata, viene utilizzato il default=5, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.soapAction.checkQuotedString' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.soapAction.checkQuotedString' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
					OpenSPCoop2Properties.httpUserAgent = this.getVersione();
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.http.userAgent' non impostata, viene utilizzato il default="+this.getVersione()+", errore:"+e.getMessage());
				OpenSPCoop2Properties.httpUserAgent = this.getVersione();
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
					OpenSPCoop2Properties.httpServer = this.getHttpUserAgent();
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.http.xPdd' non impostata, viene utilizzato il default="+this.getHttpUserAgent()+", errore:"+e.getMessage());
				OpenSPCoop2Properties.httpServer = this.getHttpUserAgent();
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.http.xDetails' non impostata correttamente:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name()+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name()+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name()+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name()+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name()+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name()+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.charset' non impostata, viene utilizzato il default="+Charset.US_ASCII.name()+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerValue.encodingRFC2047.encoding' non impostata, viene utilizzato il default="+RFC2047Encoding.Q.name()+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneContenutiApplicativi.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.ricezioneBuste.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.inoltroBuste.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.headerNameValue.validazione.enabled' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi = true;
			}  
		}

		return OpenSPCoop2Properties.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.tempo.tipo' non impostata, viene utilizzato il default="+CostantiConfigurazione.TEMPO_TIPO_LOCALE+", errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.date.tipo': "+e.getMessage());
				OpenSPCoop2Properties.tipoDateManager = null;
			}    
		}

		return OpenSPCoop2Properties.tipoDateManager;
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
				this.log.error("Riscontrato errore durante la lettura delle propriete' 'org.openspcoop2.pdd.date.property.*': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.service.IntegrationManager.nomePortaDelegataUrlBased' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.service.IntegrationManager.infoTrasporto' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.deleteInstructionTargetMachineXml' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.mailcap.load' non impostata, viene utilizzato il default="+false+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.trasporto' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_ATTACHMENT+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.mimeType.trasporto' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_MIME_TYPE+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.urlBased' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_ATTACHMENT+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.core.soap.tunnelSOAP.mimeType.urlBased' non impostata, viene utilizzato il default="+CostantiPdD.IMBUSTAMENTO_MIME_TYPE+", errore:"+e.getMessage());
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
						", viene utilizzato il default="+(CostantiPdD.RISPOSTA_ASINCRONA_ATTESA_ATTIVA/1000)+", errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.repository.messaggioAsincronoInProcessamento.check' non impostata, viene utilizzato il default="+CostantiPdD.RISPOSTA_ASINCRONA_CHECK_INTERVAL+", errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.cluster_id': "+e.getMessage());
				OpenSPCoop2Properties.cluster_id = null;
			}  
		}

		return OpenSPCoop2Properties.cluster_id;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.contextSerializer': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.default.oneway': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.default.sincrono': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.default.asincroni': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.stateful.oneway' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.router': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.stateless.dataSource.rinegoziamentoConnessione' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.handler.printInfo' non impostata correttamente,  errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.init': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.exit': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.pre-in-request': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.in-request': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.in-protocol-request': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.out-request': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.post-out-request': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.pre-in-response': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.in-response': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.out-response': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.handler.post-out-response': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.integrationManager.handler.request': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura dei tipi di handler 'org.openspcoop2.pdd.integrationManager.handler.response': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.security.addBouncyCastleProvider' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.actorDefault.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.actorDefault.valore' non impostata, viene utilizzato il default=openspcoop, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.prefixWsuId' non impostata, viene utilizzato il default di MessageSecurity, errore:"+e.getMessage());
				OpenSPCoop2Properties.prefixWsuId = "";
			}
		}

		return OpenSPCoop2Properties.prefixWsuId;
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.externalPWCallback.propertiesFile' non impostata, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.keystore.cache.enable' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.keystore.cache.dimensione': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.keystore.cache.itemLifeSecond': "+e.getMessage());
				throw new OpenSPCoop2ConfigurationException("Riscontrato errore durante la lettura della proprieta' di openspcoop  'org.openspcoop2.pdd.messageSecurity.keystore.cache.itemLifeSecond'",e);
			}
		}

		return OpenSPCoop2Properties.itemLifeSecondCacheMessageSecurityKeystore_value;
	}
	
	
	
	
	
	
	/* ********  Gestore Credenziali  ******** */

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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.services.pd.gestoriCredenziali': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.services.pa.gestoriCredenziali': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.services.integrationManager.gestoriCredenziali': "+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.registroServizi.readObjectStatoBozza' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.tracciamento.registrazioneFallita.bloccaCooperazioneInCorso' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				OpenSPCoop2Properties.isTracciaturaFallita_BloccaCooperazioneInCorso = true;
			}
		}

		return OpenSPCoop2Properties.isTracciaturaFallita_BloccaCooperazioneInCorso;
	}
	/**
	 * Indica se in caso di rilevamento di un errore di tracciatura devono essere bloccati tutti i servizi esposti dalla Porta di Dominio, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
	 *   
	 * @return Indica se in caso di rilevamento di un errore di tracciatura devono essere bloccati tutti i servizi esposti dalla Porta di Dominio, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.tracciamento.registrazione.bloccoServiziPdD' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isTracciaturaFallita_BloccoServiziPdD = false;
			}
		}

		return OpenSPCoop2Properties.isTracciaturaFallita_BloccoServiziPdD;
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ----------- MsgDiagnostici --------------------- */
	
	/**
	 * Indica se in caso di rilevamento di un errore di emissione di un messaggio diagnostico (es. salvataggio su database non riuscito) devono essere bloccati tutti i servizi esposti dalla Porta di Dominio, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
	 *   
	 * @return Indica se in caso di rilevamento di un errore di emissione di un messaggio diagnostico (es. salvataggio su database non riuscito) devono essere bloccati tutti i servizi esposti dalla Porta di Dominio, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.msgDiagnostici.emissioneFallita.bloccoServiziPdD' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isRegistrazioneDiagnosticaFallita_BloccoServiziPdD = false;
			}
		}

		return OpenSPCoop2Properties.isRegistrazioneDiagnosticaFallita_BloccoServiziPdD;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.registroServizi.readObjectStatoBozza' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				OpenSPCoop2Properties.isDumpAllAttachments = true;
			}
		}

		return OpenSPCoop2Properties.isDumpAllAttachments;
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.dump.registrazioneFallita.bloccaCooperazioneInCorso' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isDumpFallito_BloccaCooperazioneInCorso = false;
			}
		}

		return OpenSPCoop2Properties.isDumpFallito_BloccaCooperazioneInCorso;
	}
	/**
	 * Indica se in caso di rilevamento di un errore di tracciatura devono essere bloccati tutti i servizi esposti dalla Porta di Dominio, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
	 *   
	 * @return Indica se in caso di rilevamento di un errore di tracciatura devono essere bloccati tutti i servizi esposti dalla Porta di Dominio, in modo da non permettere alla PdD di gestire ulteriori richieste fino ad un intervento sistemistico.
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.logger.dump.registrazione.bloccoServiziPdD' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				OpenSPCoop2Properties.isDumpFallito_BloccoServiziPdD = false;
			}
		}

		return OpenSPCoop2Properties.isDumpFallito_BloccoServiziPdD;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.idGenerator': "+e.getMessage());
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
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm"); // SimpleDateFormat non e' thread-safe
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
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.generazioneDateCasuali.enabled' non impostata correttamente,  errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messagefactory' non impostata correttamente,  errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.context' non impostata correttamente,  errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.digestReader' non impostata correttamente,  errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messagefactory.printInfo' non impostata correttamente,  errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.messageSecurity.printInfo' non impostata correttamente,  errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.defaultProtocol' non impostata correttamente,  errore:"+e.getMessage());
			} 
		}
		return OpenSPCoop2Properties.defaultProtocolName;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.protocolNotSupported.generateErrorMessage': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.pd.httpMethodUnsupported.generateErrorMessage': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.pdToSoap.httpMethodUnsupported.generateErrorMessage': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.pa.httpMethodUnsupported.generateErrorMessage': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.im.httpMethodUnsupported.generateErrorMessage': "+e.getMessage());
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled;
	}
	
	private static Boolean isGenerazioneErroreHttpMethodUnsupportedCheckPdDEnabled = null;
	public boolean isGenerazioneErroreHttpMethodUnsupportedCheckPdDEnabled() {	
		if(OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckPdDEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.checkPdD.httpMethodUnsupported.generateErrorMessage");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.checkPdD.httpMethodUnsupported.generateErrorMessage' non impostata, viene utilizzato il default=true");
					name="true";
				}
				name = name.trim();
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckPdDEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.checkPdD.httpMethodUnsupported.generateErrorMessage': "+e.getMessage());
				OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckPdDEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneErroreHttpMethodUnsupportedCheckPdDEnabled;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.pd.generateWsdl': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.pa.generateWsdl': "+e.getMessage());
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.im.generateWsdl': "+e.getMessage());
				OpenSPCoop2Properties.isGenerazioneWsdlIntegrationManagerEnabled = true;
			}    
		}

		return OpenSPCoop2Properties.isGenerazioneWsdlIntegrationManagerEnabled;
	}
	
	
	
	
	/* ------------- CheckPdD Reader Risorse JMX ---------------------*/
	
	private static Boolean isCheckPdDReadJMXResourcesEnabled = null;
	public boolean isCheckPdDReadJMXResourcesEnabled() {	
		if(OpenSPCoop2Properties.isCheckPdDReadJMXResourcesEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.checkPdD.readJMXResources.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.checkPdD.readJMXResources.enabled' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isCheckPdDReadJMXResourcesEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.checkPdD.readJMXResources.enabled': "+e.getMessage());
				OpenSPCoop2Properties.isCheckPdDReadJMXResourcesEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isCheckPdDReadJMXResourcesEnabled;
	}
	
	private static String getCheckPdDReadJMXResourcesUsername = null;
	private static Boolean getCheckPdDReadJMXResourcesUsername_read = null;
	public String getCheckPdDReadJMXResourcesUsername() {	
		if(OpenSPCoop2Properties.getCheckPdDReadJMXResourcesUsername_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.checkPdD.readJMXResources.username");
				if(name!=null){
					name = name.trim();
				}
				OpenSPCoop2Properties.getCheckPdDReadJMXResourcesUsername_read = true;
				OpenSPCoop2Properties.getCheckPdDReadJMXResourcesUsername = name;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.checkPdD.readJMXResources.username': "+e.getMessage());
			}    
		}
		return OpenSPCoop2Properties.getCheckPdDReadJMXResourcesUsername;
	}
	
	private static String getCheckPdDReadJMXResourcesPassword = null;
	private static Boolean getCheckPdDReadJMXResourcesPassword_read = null;
	public String getCheckPdDReadJMXResourcesPassword() {	
		if(OpenSPCoop2Properties.getCheckPdDReadJMXResourcesPassword_read==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.checkPdD.readJMXResources.password");
				if(name!=null){
					name = name.trim();
				}
				OpenSPCoop2Properties.getCheckPdDReadJMXResourcesPassword_read = true;
				OpenSPCoop2Properties.getCheckPdDReadJMXResourcesPassword = name;
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.checkPdD.readJMXResources.password': "+e.getMessage());
			}    
		}
		return OpenSPCoop2Properties.getCheckPdDReadJMXResourcesPassword;
	}
	
	
	
	
	/* ------------- API ---------------------*/
	
	private static Boolean isAPIServicesEnabled = null;
	public boolean isAPIServicesEnabled() {	
		if(OpenSPCoop2Properties.isAPIServicesEnabled==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.api.enabled");
				if(name==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.pdd.api.enabled' non impostata, viene utilizzato il default=false");
					name="false";
				}
				name = name.trim();
				OpenSPCoop2Properties.isAPIServicesEnabled = Boolean.parseBoolean(name);
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.api.enabled': "+e.getMessage());
				OpenSPCoop2Properties.isAPIServicesEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isAPIServicesEnabled;
	}
	
	private static Boolean getAPIServicesWhiteListRequestHeaderRead = null;
	private static List<String> getAPIServicesWhiteListRequestHeaderList = null;
	public List<String> getAPIServicesWhiteListRequestHeaderList() {	
		if(OpenSPCoop2Properties.getAPIServicesWhiteListRequestHeaderRead==null){
			try{ 
				getAPIServicesWhiteListRequestHeaderList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.api.headers.request.forward");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getAPIServicesWhiteListRequestHeaderList.add(split[i].trim());
						}
					}
				}
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.api.headers.request.forward': "+e.getMessage());
			}    
		}

		OpenSPCoop2Properties.getAPIServicesWhiteListRequestHeaderRead = true;
		
		return OpenSPCoop2Properties.getAPIServicesWhiteListRequestHeaderList;
	}
	
	private static Boolean getAPIServicesWhiteListResponseHeaderRead = null;
	private static List<String> getAPIServicesWhiteListResponseHeaderList = null;
	public List<String> getAPIServicesWhiteListResponseHeaderList() {	
		if(OpenSPCoop2Properties.getAPIServicesWhiteListResponseHeaderRead==null){
			try{ 
				getAPIServicesWhiteListResponseHeaderList = new ArrayList<String>();
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.pdd.api.headers.response.forward");
				if(name!=null){
					name = name.trim();
					String [] split = name.split(",");
					if(split!=null){
						for (int i = 0; i < split.length; i++) {
							getAPIServicesWhiteListResponseHeaderList.add(split[i].trim());
						}
					}
				}
				
			} catch(java.lang.Exception e) {
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.api.headers.response.forward': "+e.getMessage());
			}    
		}

		OpenSPCoop2Properties.getAPIServicesWhiteListResponseHeaderRead = true;
		
		return OpenSPCoop2Properties.getAPIServicesWhiteListResponseHeaderList;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.datasource.useDSUtils': "+e.getMessage());
				OpenSPCoop2Properties.isDSOp2UtilsEnabled = false;
			}    
		}

		return OpenSPCoop2Properties.isDSOp2UtilsEnabled;
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
				this.log.error("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.pdd.jminix.port': "+e.getMessage());
				OpenSPCoop2Properties.portJminixConsoleReaded = true;
			}    
		}

		return OpenSPCoop2Properties.portJminixConsole;
	}
	
	
	
	
	/* ------------- Notifier ---------------------*/
	
	private static String notifierInputStreamCallback = null;		
	private static Boolean notifierInputStreamCallbackRead = null;		
	public String getNotifierInputStreamCallback() throws Exception{
		if(OpenSPCoop2Properties.notifierInputStreamCallbackRead==null){
			try{ 
				OpenSPCoop2Properties.notifierInputStreamCallback = this.reader.getValue("org.openspcoop2.pdd.services.notifierInputStreamCallback");
				if(OpenSPCoop2Properties.notifierInputStreamCallback!=null){
					OpenSPCoop2Properties.notifierInputStreamCallback = OpenSPCoop2Properties.notifierInputStreamCallback.trim();
					OpenSPCoop2Properties.notifierInputStreamCallbackRead = true;
				}
			} catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.notifierInputStreamCallback' non impostata correttamente,  errore:"+e.getMessage());
				throw new Exception("Proprieta' di openspcoop 'org.openspcoop2.pdd.services.notifierInputStreamCallback' non impostata correttamente,  errore:"+e.getMessage());
			} 
		}
		return OpenSPCoop2Properties.notifierInputStreamCallback;
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
				this.log.error("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage());
				throw new Exception("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage());
				throw new Exception("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage());
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
				this.log.error("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage());
				throw new Exception("Proprieta' di openspcoop '"+pName+"' non impostata correttamente,  errore:"+e.getMessage());
			} 
		}
		return OpenSPCoop2Properties.extendedInfoPortaApplicativa;
	}
}

