package org.openspcoop2.pdd.core.handlers.transazioni;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.utils.OpenSPCoopAppenderUtilities;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.slf4j.Logger;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.IDumpMessaggioService;
import org.openspcoop2.core.transazioni.dao.ITransazioneService;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

public class PostOutResponseHandler extends LastPositionHandler implements  org.openspcoop2.pdd.core.handlers.PostOutResponseHandler{

	/**
	 * Indicazione sull'inizializzazione dell'handler
	 */
	private Boolean initialized = false;

	/**
	 * Database resources
	 */
//	private DBManager dbManager = null;
//	private String datasourceRuntime = null;
	private String tipoDatabaseRuntime = null; //tipoDatabase
	private DAOFactory daoFactory = null;
    private ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
    private Logger daoFactoryLoggerTransazioni = null;

	/**
	 * OpenSPCoop2Properties/ConfigurazionePdDManager resources
	 */
	private OpenSPCoop2Properties openspcoopProperties = null;
	private ConfigurazionePdDManager configPdDManager = null;
	private EsitiProperties esitiProperties = null;
	private List<String> esitiOk = new ArrayList<String>();
	private List<String> esitiKo = new ArrayList<String>();

	/**
	 * Tracciamento e MsgDiagnostici appender
	 */
	private ITracciaProducer tracciamentoOpenSPCoopAppender = null;
	private IDiagnosticProducer msgDiagnosticiOpenSPCoopAppender = null;
	private IDumpProducer dumpOpenSPCoopAppender = null;

	/**
	 * Informazioni simulazione tracce e diagnostici
	 */
	private boolean ottimizzazioneSimulazioneTracce = false;
	private boolean ottimizzazioneSimulazioneTracceRegistrazioneHeader = false;
	private boolean ottimizzazioneSimulazioneTracceRegistrazioneDigest = false;
	private boolean ottimizzazioneSimulazioneDiagnostici = false;

	/**
	 * Logger
	 */
	private Logger log = null;
	private Logger logSql = null;

	/**
	 * Indicazione se funzionare in modalita' debug
	 */
	private boolean debug = false;



	private synchronized void init() throws HandlerException {

		if(this.initialized==false){

			// Logger
			try{
				// OpenSPCoop2Properties
				this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
				if(this.openspcoopProperties.isTransazioniEnabled()==false) {
					this.initialized = true;
					return;
				}
				
				// Debug
				this.debug = this.openspcoopProperties.isTransazioniDebug();
				
				// Log
				this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(this.debug);
				this.logSql = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSql(this.debug);
				
			}catch(Exception e){
				throw new HandlerException("Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
			}

			// Configurazione
			try{

				
				// configPdDManager
				this.configPdDManager = ConfigurazionePdDManager.getInstance();
				
				// EsitiProperties
				this.esitiProperties = EsitiProperties.getInstance(this.log);
				List<Integer> tmpEsitiOk = this.esitiProperties.getEsitiCodeOk();
				if(tmpEsitiOk!=null && tmpEsitiOk.size()>0){
					for (Integer esito : tmpEsitiOk) {
						this.esitiOk.add(esito+"");
					}
				}
				List<Integer> tmpEsitiKo = this.esitiProperties.getEsitiCodeKo();
				if(tmpEsitiKo!=null && tmpEsitiKo.size()>0){
					for (Integer esito : tmpEsitiKo) {
						this.esitiKo.add(esito+"");
					}
				}
				
			} catch (Exception e) {
				throw new HandlerException("Errore durante la lettura della configurazione: " + e.getLocalizedMessage(), e);
			}

			// DB
			try{
				this.tipoDatabaseRuntime = this.openspcoopProperties.getDatabaseType();
				if(this.tipoDatabaseRuntime==null){
					throw new Exception("Tipo Database non definito");
				}

				DAOFactoryProperties daoFactoryProperties = null;
				this.daoFactoryLoggerTransazioni = this.logSql;
				this.daoFactory = DAOFactory.getInstance(this.daoFactoryLoggerTransazioni);
				daoFactoryProperties = DAOFactoryProperties.getInstance(this.daoFactoryLoggerTransazioni);
				this.daoFactoryServiceManagerPropertiesTransazioni = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				this.daoFactoryServiceManagerPropertiesTransazioni.setShowSql(this.debug);	

			}catch(Exception e){
				throw new HandlerException("Errore durante l'inizializzazione delle risorse per l'accesso al database: "+e.getMessage(),e);
			}

			boolean usePdDConnection = true;
			
			try{
				
				// Init
				this.tracciamentoOpenSPCoopAppender = new org.openspcoop2.pdd.logger.TracciamentoOpenSPCoopProtocolAppender();
				OpenspcoopAppender tracciamentoOpenSPCoopAppender = new OpenspcoopAppender();
				tracciamentoOpenSPCoopAppender.setTipo("__timerFileSystemRecovery");
				List<Property> tracciamentoOpenSPCoopAppenderProperties = new ArrayList<Property>();
	
				// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
				OpenSPCoopAppenderUtilities.addParameters(this.daoFactoryLoggerTransazioni, tracciamentoOpenSPCoopAppenderProperties, 
						null, // nessun datasource
						null, null, null, null,  // nessuna connection
						this.tipoDatabaseRuntime,
						usePdDConnection, // viene usata la connessione della PdD 
						this.debug
						);
				OpenSPCoopAppenderUtilities.addCheckProperties(tracciamentoOpenSPCoopAppenderProperties, false);
	
				tracciamentoOpenSPCoopAppender.setPropertyList(tracciamentoOpenSPCoopAppenderProperties);
				this.tracciamentoOpenSPCoopAppender.initializeAppender(tracciamentoOpenSPCoopAppender);
				this.tracciamentoOpenSPCoopAppender.isAlive();
				
			}catch(Exception e){
				throw new HandlerException("Errore durante l'inizializzazione del TracciamentoAppender: "+e.getMessage(),e);
			} 
			
			try{
				
				// Init
				this.msgDiagnosticiOpenSPCoopAppender = new org.openspcoop2.pdd.logger.MsgDiagnosticoOpenSPCoopProtocolAppender();
				OpenspcoopAppender diagnosticoOpenSPCoopAppender = new OpenspcoopAppender();
				diagnosticoOpenSPCoopAppender.setTipo("__timerFileSystemRecovery");
				List<Property> diagnosticoOpenSPCoopAppenderProperties = new ArrayList<Property>();
	
				// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
				OpenSPCoopAppenderUtilities.addParameters(this.daoFactoryLoggerTransazioni, diagnosticoOpenSPCoopAppenderProperties, 
						null, // nessun datasource
						null, null, null, null,  // nessuna connection
						this.tipoDatabaseRuntime,
						usePdDConnection, // viene usata la connessione della PdD
						this.debug
						);
				OpenSPCoopAppenderUtilities.addCheckProperties(diagnosticoOpenSPCoopAppenderProperties, false);
	
				diagnosticoOpenSPCoopAppender.setPropertyList(diagnosticoOpenSPCoopAppenderProperties);
				this.msgDiagnosticiOpenSPCoopAppender.initializeAppender(diagnosticoOpenSPCoopAppender);
				this.msgDiagnosticiOpenSPCoopAppender.isAlive();
				
			}catch(Exception e){
				throw new HandlerException("Errore durante l'inizializzazione del DiagnosticoAppender: "+e.getMessage(),e);
			} 
			
			try{
				
				// Init
				this.dumpOpenSPCoopAppender = new org.openspcoop2.pdd.logger.DumpOpenSPCoopProtocolAppender();
				OpenspcoopAppender dumpOpenSPCoopAppender = new OpenspcoopAppender();
				dumpOpenSPCoopAppender.setTipo("__timerFileSystemRecovery");
				List<Property> dumpOpenSPCoopAppenderProperties = new ArrayList<Property>();
	
				// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
				OpenSPCoopAppenderUtilities.addParameters(this.daoFactoryLoggerTransazioni, dumpOpenSPCoopAppenderProperties, 
						null, // nessun datasource
						null, null, null, null,  // nessuna connection
						this.tipoDatabaseRuntime,
						usePdDConnection, // viene usata la connessione della PdD 
						this.debug
						);
				OpenSPCoopAppenderUtilities.addCheckProperties(dumpOpenSPCoopAppenderProperties, false);
	
				dumpOpenSPCoopAppender.setPropertyList(dumpOpenSPCoopAppenderProperties);
				this.dumpOpenSPCoopAppender.initializeAppender(dumpOpenSPCoopAppender);
				this.dumpOpenSPCoopAppender.isAlive();
				
			}catch(Exception e){
				throw new HandlerException("Errore durante l'inizializzazione del DumpAppender: "+e.getMessage(),e);
			} 

// **FINIRE
//
//			// Configurazione Simulazioni tracce e diagnostici
//			try{
//
//				// Indicazione se devo registrare tutte le tracce o solo quelle non ricostruibili
//				this.ottimizzazioneSimulazioneTracce =  PddInterceptorConfig.isOttimizzazioniSimulazioneTracceAbilitato();
//				this.ottimizzazioneSimulazioneTracceRegistrazioneHeader = PddInterceptorConfig.isOttimizzazioniSimulazioneTracceRegistrazioneHeaderAbilitato(); 
//				this.ottimizzazioneSimulazioneTracceRegistrazioneDigest = PddInterceptorConfig.isOttimizzazioniSimulazioneTracceRegistrazioneDigestAbilitato(); 
//				
//				// Indicazione se devo registrare tutti i diagnostici o solo quelli non ricostruibili
//				this.ottimizzazioneSimulazioneDiagnostici = PddInterceptorConfig.isOttimizzazioniSimulazioneMsgDiagnosticiAbilitato();
//
//			} catch (Exception e) {
//				throw new HandlerException("Errore durante la lettura della configurazione della simulazione tracce/diagnostici: " + e.getLocalizedMessage(), e);
//			}

			this.initialized = true;
		}

	}



	@Override
	public void invoke(PostOutResponseContext context) throws HandlerException {

		if(this.initialized==false){
			init();
		}
		if(this.openspcoopProperties.isTransazioniEnabled()==false) {
			return;
		}




		/* ---- Recupero contesto ----- */

		if (context==null)
			throw new HandlerException("Contesto is null");
		if (context.getPddContext()==null)
			throw new HandlerException("PddContext is null");

		if (context.getPddContext().getObject(Costanti.ID_TRANSAZIONE)==null)
			throw new HandlerException("Identificativo della transazione assente");
		String idTransazione = (String) context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
		if (idTransazione==null)
			throw new HandlerException("Identificativo della transazione assente");
		//System.out.println("------------- PostOutRequestHandler ("+idTransazione+")("+context.getTipoPorta().getTipo()+") -------------------");

		
		
		/* ---- Verifica Esito della Transazione per registrazione nello storico ----- */
		
		List<String> esitiDaRegistrare = null;
		boolean exitTransactionAfterRateLimitingRemoveThread = false;
		try{
			Tracciamento configTracciamento = this.configPdDManager.getOpenSPCoopAppender_Tracciamento();
			StringBuffer bf = new StringBuffer();
			esitiDaRegistrare = EsitiConfigUtils.getRegistrazioneEsiti(configTracciamento, this.log, bf);
			if(esitiDaRegistrare==null || esitiDaRegistrare.size()<=0){
				esitiDaRegistrare = null;
				if(this.debug){
					this.log.debug("["+idTransazione+"] Vengono registrati tutte le Transazioni indipendentemente dagli esiti"); 
				}
			}
			else{
				if(this.debug){
					this.log.debug("["+idTransazione+"] Esiti delle Transazioni da registare: "+bf.toString()); 
				}
				if(context.getEsito()!=null){
					int code = context.getEsito().getCode();
					String codeAsString = code+"";
					if(esitiDaRegistrare.contains(codeAsString)==false){
						String msg = "Transazione ID["+idTransazione+"] non salvata nello storico: esito [name:"+this.esitiProperties.getEsitoName(context.getEsito().getCode())+" code:"+codeAsString+"]";
						if(this.esitiOk.contains(codeAsString)){
							this.log.warn(msg);
						}
						else{
							this.log.error(msg);
						}
						int esitoViolazioneRateLimiting = this.esitiProperties.convertNameToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA.name());
						int esitoViolazioneRateLimitingWarningOnly = this.esitiProperties.convertNameToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA_WARNING_ONLY.name());
						int esitoMaxThreadsWarningOnly = this.esitiProperties.convertNameToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS_WARNING_ONLY.name());
						if((esitoViolazioneRateLimiting == code) || //  Violazione Rate Limiting
								(esitoViolazioneRateLimitingWarningOnly == code) ||  // Violazione Rate Limiting WarningOnly
								(esitoMaxThreadsWarningOnly == code) // Superamento Limite Richieste WarningOnly
								){
							exitTransactionAfterRateLimitingRemoveThread = true;
						}
						else{
							this._releaseResources(null, idTransazione, context);
							return;
						}
					}
				}
				else{
					this.log.error("Transazione ID["+idTransazione+"] senza un esito");
				}
			}
		}catch (Throwable e) {
			esitiDaRegistrare = null;
			this.log.error("Transazione ID["+idTransazione+"] errore durante la comprensione degli esiti da registrare: "+e.getMessage(),e);
		}
		
		// **FINIRE	
			
		/* ---- Recupero dati della transazione dal contesto ----- */
//		
//		Boolean controlloCongestioneMaxRequestThreadRegistrato = null;
//		if(InitHandler.controlloCongestioneAttivo){
//			Object objControlloCongestioneMaxRequestThreadRegistrato = context.getPddContext().getObject(it.link.pdd.interceptor.Costanti.PDD_CONTEXT_MAX_REQUEST_THREAD_REGISTRATO);
//			if(objControlloCongestioneMaxRequestThreadRegistrato!=null){
//				controlloCongestioneMaxRequestThreadRegistrato = (Boolean) objControlloCongestioneMaxRequestThreadRegistrato;
//				//System.out.println("CHECK POST OUT ["+context.getTipoPorta().name()+"] controlloCongestioneMaxRequestViolated["+controlloCongestioneMaxRequestViolated+"] controllo["+PddInterceptorConfig.isControlloCongestioneTraceTransazioneMaxThreadsViolated()+"]");
//			}		
//		}
//		
//		Transaction transaction = TransactionContext.removeSPCoopTransaction(idTransazione);
//		if (transaction==null)
//			throw new HandlerException("Dati della transazione assenti");
//		transaction.setDeleted();
//
//		if(transaction.getMsgDiagnosticoCorrelazione()!=null){
//			String idBusta = transaction.getMsgDiagnosticoCorrelazione().getIdBusta();
//			if(idBusta!=null){
//				TransactionContext.removeIdTransazione(idBusta, transaction.getMsgDiagnosticoCorrelazione().isDelegata());
//			}
//		}
//
//		Transazione transazioneDTO = null;
//		try{
//		
//			// NOTA: se l'integrazione e' null o l'indicazione se la gestione stateless e' null, significa che la PdD non e' ancora riuscita
//			// a capire che tipo di gestione deve adottare. Queste transazioni devono essere sempre registrate perche' riguardano cooperazioni andate in errore all'inizio,
//			// es. Porta Delegata non esistente, busta malformata....
//			if(context.getIntegrazione()!=null && 
//					context.getIntegrazione().isGestioneStateless()!=null &&
//					!context.getIntegrazione().isGestioneStateless()){
//				if(PddInterceptorConfig.isEnabledGestioneTransazioniStateful()==false){
//					//if(this.debug)
//					this.logger.error("["+idTransazione+"] Transazione non registrata, gestione stateful non abilitata");
//					// NOTA: TODO thread che ripulisce header di trasporto o dump messaggi non associati a transazioni.
//					// La risorsa viene rilasciata nel finally
//					//this._releaseResources(transaction, idTransazione, context);
//					return;
//				}
//			}
//	
//			IDSoggetto idDominio = this.openspcoopProperties.getIdentitaPortaDefault(context.getProtocolFactory().getProtocol());
//			if(context.getProtocollo()!=null && context.getProtocollo().getDominio()!=null){
//				idDominio = context.getProtocollo().getDominio();
//			}
//			else if(context.getPddContext()!=null && context.getPddContext().containsKey(Costanti.REQUEST_INFO)){
//				RequestInfo requestInfo = (RequestInfo) context.getPddContext().getObject(Costanti.REQUEST_INFO);
//				if(requestInfo!=null){
//					idDominio = requestInfo.getIdentitaPdD();
//				}
//			}
//	
//			String modulo = "PostOutResponsePddOE";
//	
//			ExceptionSerialzerFileSystem exceptionSerializerFileSystem = new ExceptionSerialzerFileSystem(this.logger);
//	
//			
//			// ### Lettura dati Transazione ###
//			
//			boolean pddStateless = true;
//			PostOutResponseHandler_TransazioneUtilities transazioneUtilities = null;
//			HandlerException he = null;
//			try{
//				
//				// Stateless
////				if ( (context.getIntegrazione()==null) ||
////						(context.getIntegrazione().isGestioneStateless()==null) ||
////						(context.getIntegrazione().isGestioneStateless()==false) 
////						){
////					pddStateless = false;
////				}
//				// Cambio l'approccio per poter simulare anche gli errori nei diagnostici dove possibile
//				// Tanto tutte le comunicazioni sono stateless a meno che non vengano tramutate in stateful
//				if ( context.getIntegrazione()!=null && 
//						context.getIntegrazione().isGestioneStateless()!=null &&
//								!context.getIntegrazione().isGestioneStateless()){
//					pddStateless = false;
//				}
//				
//				/* ---- Salvo informazioni sulla transazioni nell'oggetto transazioniDTO ----- */
//				transazioneUtilities = new PostOutResponseHandler_TransazioneUtilities(this.logger, 
//						this.ottimizzazioneSimulazioneTracceRegistrazioneHeader,
//						this.ottimizzazioneSimulazioneTracceRegistrazioneDigest,
//						this.ottimizzazioneSimulazioneTracce);
//				transazioneDTO = transazioneUtilities.fillTransaction(context, transaction, idDominio); // NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
//							
//			}catch (Throwable e) {
//				try{
//					exceptionSerializerFileSystem.registrazioneFileSystemDiagnosticiTracceEmessePdD(transaction, idTransazione, null);
//				} catch (Exception eClose) {}
//				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
//				String msg = "Errore durante la scrittura della transazione sul database (Lettura dati Transazione): " + e.getLocalizedMessage();
//				this.logger.error("["+idTransazione+"] "+msg,e);
//				he = new HandlerException(msg,e);
//			}
//			finally {	
//
//				// ### Gestione Controllo Congestione ###
//				// Nota: il motivo del perchè viene effettuato qua la "remove"
//				// 	     risiede nel fatto che la risposta al client è già stata data
//				//	     però il "thread occupato" dal client non è ancora stato liberato per una nuova richiesta
//				//		 Se la remove viene messa nel finally del try-catch sottostante prima della remove si "paga" 
//				//	     i tempi di attesa dell'inserimento della transazione sul database
//				if(InitHandler.controlloCongestioneAttivo){
//					PostOutResponseHandler_GestioneControlloCongestione outHandler = new PostOutResponseHandler_GestioneControlloCongestione();
//					outHandler.process(controlloCongestioneMaxRequestThreadRegistrato, this.logger, idTransazione, transazioneDTO, context);
//				}
//				if(he!=null) {
//					throw he;
//				}
//				else if(exitTransactionAfterRateLimitingRemoveThread){
//					// La risorsa viene rilasciata nel finally
//					//this.releaseResources(transaction, idTransazione, context);
//					return;
//				}
//			}
//			
//			
//			
//			
//			
//			
//			// ### Gestione Transazione ###
//	
//			boolean autoCommit = true;
//			//Resource dbResource = null;
//			Connection dbConn = null;
//			boolean errore = false;
//			try {
//				
//	
//				/* ---- Recupero informazioni sulla modalita' di salvataggio delle tracce ----- */
//	
//				// TRACCIA RICHIESTA
//				boolean registraTracciaRichiesta = true;
//				InformazioniSalvataggioTraccia infoSalvataggioTracciaRichiesta = null;
//				Traccia tracciaRichiesta = transaction.getTracciaRichiesta();
//				if(this.ottimizzazioneSimulazioneTracce && pddStateless){
//					infoSalvataggioTracciaRichiesta = 
//							GestoreTraccia.getInformazioniSalvataggioTraccia(this.logger,transazioneDTO, tracciaRichiesta, 
//									RuoloMessaggio.RICHIESTA,context.getProtocolFactory(),
//									PddInterceptorConfig.isGenerazioneTransazioni_SimulazioneTracceFallita_truncErrorTo250Char());
//					if(infoSalvataggioTracciaRichiesta.isPresente() && infoSalvataggioTracciaRichiesta.isRicostruibile()){
//						registraTracciaRichiesta = false;
//					}
//				}
//				if(this.debug){
//					this.logger.debug("["+idTransazione+"] Emissione traccia richiesta: "+registraTracciaRichiesta);
//					if(infoSalvataggioTracciaRichiesta!=null){
//						this.logger.debug("["+idTransazione+"] Informazioni Salvataggio traccia richiesta: "+infoSalvataggioTracciaRichiesta.toString());
//					}
//				}
//				else{
//					if(infoSalvataggioTracciaRichiesta!=null){
//						if(infoSalvataggioTracciaRichiesta.isPresente() && infoSalvataggioTracciaRichiesta.isRicostruibile()==false){
//							this.logger.warn("["+idTransazione+"] Informazioni Salvataggio traccia richiesta non ricostruibile: "+infoSalvataggioTracciaRichiesta.toString());
//						}
//					}
//				}
//	
//				// TRACCIA RISPOSTA
//				boolean registraTracciaRisposta = true;
//				InformazioniSalvataggioTraccia infoSalvataggioTracciaRisposta = null;
//				Traccia tracciaRisposta = transaction.getTracciaRisposta();
//				if(this.ottimizzazioneSimulazioneTracce && pddStateless){
//					infoSalvataggioTracciaRisposta = 
//							GestoreTraccia.getInformazioniSalvataggioTraccia(this.logger,transazioneDTO, tracciaRisposta, 
//									RuoloMessaggio.RISPOSTA,context.getProtocolFactory(),
//									PddInterceptorConfig.isGenerazioneTransazioni_SimulazioneTracceFallita_truncErrorTo250Char());
//					if(infoSalvataggioTracciaRisposta.isPresente() && infoSalvataggioTracciaRisposta.isRicostruibile()){
//						registraTracciaRisposta = false;
//					}
//				}
//				if(this.debug){
//					this.logger.debug("["+idTransazione+"] Emissione traccia riposta: "+registraTracciaRisposta);
//					if(infoSalvataggioTracciaRisposta!=null){
//						this.logger.debug("["+idTransazione+"] Informazioni Salvataggio traccia risposta: "+infoSalvataggioTracciaRisposta.toString());
//					}
//				}
//				else{
//					if(infoSalvataggioTracciaRisposta!=null){
//						if(infoSalvataggioTracciaRisposta.isPresente() && infoSalvataggioTracciaRisposta.isRicostruibile()==false){
//							this.logger.warn("["+idTransazione+"] Informazioni Salvataggio traccia risposta non ricostruibile: "+infoSalvataggioTracciaRisposta.toString());
//						}
//					}
//				}
//	
//				// MESSAGGI DIAGNOSTICI
//				boolean registrazioneMessaggiDiagnostici = true;
//				InformazioniSalvataggioDiagnostici infoSalvataggioDiagnostici = null;
//				if(this.ottimizzazioneSimulazioneDiagnostici && pddStateless){
//					infoSalvataggioDiagnostici =
//							GestoreDiagnostici.getInformazioniSalvataggioTraccia(this.logger,context, 
//									transaction.getCodiceTrasportoRichiesta(), transaction.getTipoConnettore(),
//									tracciaRichiesta, tracciaRisposta, transaction.getMsgDiagnostici(), transazioneDTO,
//									PddInterceptorConfig.isGenerazioneTransazioni_SimulazioneDiagnosticiFallita_truncErrorTo250Char(),
//									PddInterceptorConfig.isOttimizzazioniSimulazioneMsgDiagnosticiAbilitato_serializzazioneMessaggiNonRicostruibili());
//					if(infoSalvataggioDiagnostici.isPresenti() && infoSalvataggioDiagnostici.isRicostruibili()){
//						registrazioneMessaggiDiagnostici = false;
//					}
//				}
//				if(this.debug){
//					this.logger.debug("["+idTransazione+"] Emissione diagnostici: "+registrazioneMessaggiDiagnostici);
//					if(infoSalvataggioDiagnostici!=null){
//						this.logger.debug("["+idTransazione+"] Informazioni Salvataggio diagnostici: "+infoSalvataggioDiagnostici.toString());
//					}
//				}
//				else{
//					if(infoSalvataggioDiagnostici!=null){
//						if(infoSalvataggioDiagnostici.isPresenti() && infoSalvataggioDiagnostici.isRicostruibili()==false){
//							this.logger.warn("["+idTransazione+"] Informazioni Salvataggio diagnostici non ricostruibili: "+infoSalvataggioDiagnostici.toString());
//						}
//					}
//				}
//	
//				
//				// CONTENUTI
//				boolean registrazioneRisorse = transaction.getTransactionServiceLibrary()!=null;
//				
//	
//				// AUTO-COMMIT
//				if(registraTracciaRichiesta || registraTracciaRisposta || registrazioneMessaggiDiagnostici || registrazioneRisorse){
//					autoCommit = false; // devo registrare piu' informazioni oltre alla transazione
//				}
//				if(this.debug)
//					this.logger.debug("["+idTransazione+"] AutoCommit: "+this.debug);
//	
//	
//	
//	
//	
//				/* ---- Connection/Service Manager ----- */
//	
//				if(this.debug)
//					this.logger.debug("["+idTransazione+"] recupero jdbcServiceManager in corso ...");
//	
//				// Ottiene la connessione al db
//				//dbResource = this.dbManager.getResource(idDominio, modulo, idTransazione);
//				//dbConn = (Connection) dbResource.getResource();
//				dbConn = PddInterceptorConfig.getConnectionRuntime(idDominio, modulo, idTransazione);
//				if(autoCommit==false){
//					dbConn.setAutoCommit(false);
//				}
//				org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
//						(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) this.daoFactory.getServiceManager(DAO.TRANSAZIONI, dbConn, autoCommit,
//								this.daoFactoryServiceManagerPropertiesTransazioni, this.daoFactoryLoggerTransazioni);
//				jdbcServiceManager.getJdbcProperties().setShowSql(this.debug);
//				ITransazioneService transazioneService = jdbcServiceManager.getTransazioneService();
//				if(this.debug)
//					this.logger.debug("["+idTransazione+"] recupero jdbcServiceManager effettuato");
//	
//	
//	
//	
//				/* ---- Inserimento dati transazione ----- */
//	
//				// Inserisco transazione
//				if(this.debug)
//					this.logger.debug("["+idTransazione+"] inserimento transazione in corso ...");
//				
//				// ** Simulazione dati tracce **
//				if(infoSalvataggioTracciaRichiesta!=null){
//					transazioneDTO.setTracciaRichiesta(infoSalvataggioTracciaRichiesta.convertToDBColumnValue());
//				}
//				if(infoSalvataggioTracciaRisposta!=null){
//					transazioneDTO.setTracciaRisposta(infoSalvataggioTracciaRisposta.convertToDBColumnValue());
//				}
//				
//				// ** Simulazione dati diagnostica **
//				if(infoSalvataggioDiagnostici!=null){
//					transazioneDTO.setDiagnostici(infoSalvataggioDiagnostici.convertToDBColumnValue(DiagnosticColumnType.META_INF));
//					transazioneDTO.setDiagnosticiList1(infoSalvataggioDiagnostici.convertToDBColumnValue(DiagnosticColumnType.LIST1));
//					transazioneDTO.setDiagnosticiList2(infoSalvataggioDiagnostici.convertToDBColumnValue(DiagnosticColumnType.LIST2));
//					transazioneDTO.setDiagnosticiListExt(infoSalvataggioDiagnostici.convertToDBColumnValue(DiagnosticColumnType.LIST_EXT));
//					transazioneDTO.setDiagnosticiExt(infoSalvataggioDiagnostici.convertToDBColumnValue(DiagnosticColumnType.EXT));
//				}
//				
//				transazioneService.create(transazioneDTO);
//				if(this.debug)
//					this.logger.debug("["+idTransazione+"] inserita transazione");
//	
//	
//	
//	
//	
//				/* ---- Inserimento dati tracce ----- */
//				if( registraTracciaRichiesta && tracciaRichiesta!=null){
//					if(this.debug)
//						this.logger.debug("["+idTransazione+"] registrazione traccia richiesta...");
//					this.tracciamentoOpenSPCoopAppender.log(dbConn, tracciaRichiesta);
//					if(this.debug)
//						this.logger.debug("["+idTransazione+"] registrazione traccia richiesta completata");
//				}	
//				if( registraTracciaRisposta && tracciaRisposta!=null){
//					if(this.debug)
//						this.logger.debug("["+idTransazione+"] registrazione traccia risposta...");
//					this.tracciamentoOpenSPCoopAppender.log(dbConn, tracciaRisposta);
//					if(this.debug)
//						this.logger.debug("["+idTransazione+"] registrazione traccia risposta completata");
//				}	
//	
//	
//	
//	
//	
//	
//				/* ---- Inserimento messaggi diagnostici ----- */
//				if(registrazioneMessaggiDiagnostici){
//					for(int i=0; i<transaction.sizeMsgDiagnostici(); i++){
//						MsgDiagnostico msgDiagnostico = transaction.getMsgDiagnostico(i);
//						if(msgDiagnostico.getIdSoggetto()==null){
//							msgDiagnostico.setIdSoggetto(idDominio);
//						}
//						if(this.debug)
//							this.logger.debug("["+idTransazione+"] registrazione diagnostico con codice ["+msgDiagnostico.getCodice()+"] ...");
//						this.msgDiagnosticiOpenSPCoopAppender.log(dbConn,msgDiagnostico);
//						if(this.debug)
//							this.logger.debug("["+idTransazione+"] registrazione diagnostico con codice ["+msgDiagnostico.getCodice()+"] completata");
//					}
//					if(transaction.getMsgDiagnosticoCorrelazione()!=null){
//						MsgDiagnosticoCorrelazione msgDiagCorrelazione = transaction.getMsgDiagnosticoCorrelazione();
//						if(this.debug)
//							this.logger.debug("["+idTransazione+"] registrazione informazioni di correlazione per i diagnostici ...");
//						this.msgDiagnosticiOpenSPCoopAppender.logCorrelazione(dbConn,msgDiagCorrelazione);
//						if(msgDiagCorrelazione.getServiziApplicativiList()!=null){
//							for(int j=0; j<msgDiagCorrelazione.getServiziApplicativiList().size(); j++){
//								MsgDiagnosticoCorrelazioneServizioApplicativo corr_sa = new MsgDiagnosticoCorrelazioneServizioApplicativo();
//								corr_sa.setDelegata(msgDiagCorrelazione.isDelegata());
//								corr_sa.setIdBusta(msgDiagCorrelazione.getIdBusta());
//								corr_sa.setProtocollo(msgDiagCorrelazione.getProtocollo());
//								corr_sa.setServizioApplicativo(msgDiagCorrelazione.getServiziApplicativiList().get(j));
//								this.msgDiagnosticiOpenSPCoopAppender.logCorrelazioneServizioApplicativo(dbConn,
//										corr_sa);
//							}
//						}
//						if(msgDiagCorrelazione.getCorrelazioneApplicativaRisposta()!=null){
//							MsgDiagnosticoCorrelazioneApplicativa corr = new MsgDiagnosticoCorrelazioneApplicativa();
//							corr.setDelegata(msgDiagCorrelazione.isDelegata());
//							corr.setIdBusta(msgDiagCorrelazione.getIdBusta());
//							corr.setProtocollo(msgDiagCorrelazione.getProtocollo());
//							corr.setCorrelazione(msgDiagCorrelazione.getCorrelazioneApplicativaRisposta());
//							this.msgDiagnosticiOpenSPCoopAppender.logCorrelazioneApplicativaRisposta(dbConn,
//									corr);
//						}
//						if(this.debug)
//							this.logger.debug("["+idTransazione+"] registrazione informazioni di correlazione per i diagnostici completata");
//					}
//				}
//	
//	
//				
//				
//				
//				/* ---- Inserimento risorse contenuti (library personalizzata) ----- */
//				if(registrazioneRisorse){
//					IDumpMessaggioService dumpMessageService = jdbcServiceManager.getDumpMessaggioService();
//					PostOutResponseHandler_ContenutiUtilities contenutiUtilities = new PostOutResponseHandler_ContenutiUtilities(this.logger);
//					contenutiUtilities.insertContenuti(transazioneDTO, tracciaRichiesta, tracciaRisposta, transaction.getMsgDiagnostici(),
//							dumpMessageService, transaction.getRisorse(), transaction.getTransactionServiceLibrary(), this.daoFactory);
//				}
//	
//				
//	
//	
//				// COMMIT
//				if(autoCommit==false)
//					dbConn.commit();
//	
//	
//			} catch (SQLException sqlEx) {
//				errore = true;
//				try{
//					if(autoCommit==false)
//						dbConn.rollback();
//				}catch(Exception eRollback){}
//				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
//				String msg = "Errore durante la scrittura della transazione sul database (sql): " + sqlEx.getLocalizedMessage();
//				this.logger.error("["+idTransazione+"] "+msg,sqlEx);
//				throw new HandlerException(msg,sqlEx);			
//			}  catch (Throwable e) {
//				errore = true;
//				try{
//					if(autoCommit==false)
//						dbConn.rollback();
//				}catch(Exception eRollback){}
//				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
//				String msg = "Errore durante la scrittura della transazione sul database: " + e.getLocalizedMessage();
//				this.logger.error("["+idTransazione+"] "+msg,e);
//				throw new HandlerException(msg,e);	
//			} finally {
//				
//				// Ripristino Autocomit
//				try {
//					if(autoCommit==false)
//						dbConn.setAutoCommit(true);
//				} catch (Exception e) {}
//									
//				// Chiusura della connessione al database
//				try {
//					//this.dbManager.releaseResource(idDominio, modulo, dbResource);
//					if(dbConn!=null){
//						dbConn.close();
//					}
//				} catch (Exception e) {}
//				
//				// Registrazione su FileSystem informazioni se la gestione e' andata in errore
//				if(errore){
//					try {
//						exceptionSerializerFileSystem.registrazioneFileSystemDiagnosticiTracceEmessePdD(transaction, idTransazione, transazioneDTO);
//					} catch (Exception e) {}
//					try {
//						exceptionSerializerFileSystem.registrazioneFileSystem(transazioneDTO, idTransazione);
//					} catch (Exception e) {}
//				}
//								
//			}
//			
//		}finally{
//
//			this._releaseResources(transaction, idTransazione, context);
//						
//		}
	}

	private void _releaseResources(Transaction transactionParam, String idTransazione, PostOutResponseContext context) {
//		
//		Transaction transaction = null;
//		try {
//			transaction = TransactionContext.removeSPCoopTransaction(idTransazione);
//			if(transaction!=null) {
//				transaction.setDeleted();
//			}
//			// altrimenti e' gia' stata eliminata
//		} catch (Throwable e) {
//			this.logger.error("["+idTransazione+"] Errore durante la rimozione della registrazione delle transazione",e);
//		}
//		
//		String idBusta = null;
//		Boolean isDelegata = null;
//		if (transaction!=null) {
//			if(transaction.getMsgDiagnosticoCorrelazione()!=null){
//				idBusta = transaction.getMsgDiagnosticoCorrelazione().getIdBusta();
//				isDelegata = transaction.getMsgDiagnosticoCorrelazione().isDelegata();
//			}
//		}
//		else if (transactionParam!=null) {
//			if(transactionParam.getMsgDiagnosticoCorrelazione()!=null){
//				idBusta = transactionParam.getMsgDiagnosticoCorrelazione().getIdBusta();
//				isDelegata = transactionParam.getMsgDiagnosticoCorrelazione().isDelegata();
//			}
//		}
//		if(idBusta!=null){
//			try {
//				TransactionContext.removeIdTransazione(idBusta, isDelegata);
//			} catch (Throwable e) {
//				this.logger.error("["+idTransazione+"] Errore durante la rimozione della registrazione dell'associazione idTransazione - idBusta",e);
//			}
//		}
//			
//		/* ---- Elimino informazione per filtro duplicati ----- */
//		if(context!=null && context.getProtocollo()!=null){
//			if(context.getProtocollo().getIdRichiesta()!=null){
//				try {
//					TransactionContext.removeIdentificativoProtocollo(context.getProtocollo().getIdRichiesta());
//				} catch (Throwable e) {
//					this.logger.error("["+idTransazione+"] Errore durante la rimozione della registrazione dell'identificativo di protocollo della richiesta ["+context.getProtocollo().getIdRichiesta()+"]",e);
//				}
//			}
//			if(context.getProtocollo().getIdRisposta()!=null){
//				try {
//					TransactionContext.removeIdentificativoProtocollo(context.getProtocollo().getIdRisposta());
//				} catch (Throwable e) {
//					this.logger.error("["+idTransazione+"] Errore durante la rimozione della registrazione dell'identificativo di protocollo della risposta ["+context.getProtocollo().getIdRisposta()+"]",e);
//				}
//			}
//		}
//		
//	}
//	
//
//	public String getClassNamePropertiesName(){
//		return "org.openspcoop2.pdd.handler.post-out-response.pddOE";
//	}
//
//	public String [] getOpenSPCoopPropertiesNames(){
//		return new String [] {"org.openspcoop2.pdd.handler.post-out-response"};
//	}
//	public String [] getOpenSPCoopPropertiesValues(){
//		return new String [] {"+,pddOE"};
	}


	
}
