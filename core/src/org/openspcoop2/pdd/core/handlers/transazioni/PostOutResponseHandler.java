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
package org.openspcoop2.pdd.core.handlers.transazioni;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.utils.OpenSPCoopAppenderUtilities;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.DiagnosticColumnType;
import org.openspcoop2.core.transazioni.dao.IDumpMessaggioService;
import org.openspcoop2.core.transazioni.dao.ITransazioneService;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.filetrace.FileTraceConfig;
import org.openspcoop2.pdd.logger.filetrace.FileTraceManager;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.slf4j.Logger;

/**     
 * PostOutResponseHandler
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
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

	/**
	 * Tracciamento e MsgDiagnostici appender
	 */
	private ITracciaProducer tracciamentoOpenSPCoopAppender = null;
	private IDiagnosticProducer msgDiagnosticiOpenSPCoopAppender = null;
	private IDumpProducer dumpOpenSPCoopAppender = null;

	/**
	 * Informazioni tracce e diagnostici
	 */
	private boolean transazioniRegistrazioneTracceProtocolPropertiesEnabled = false;
	private boolean transazioniRegistrazioneTracceHeaderRawEnabled = false;
	private boolean transazioniRegistrazioneTracceDigestEnabled = false;
	private boolean transazioniRegistrazioneTokenInformazioniNormalizzate = false;
	private boolean transazioniRegistrazioneTempiElaborazione = false;
	private ISalvataggioTracceManager salvataggioTracceManager = null;
	private ISalvataggioDiagnosticiManager salvataggioDiagnosticiManager = null;
	private boolean transazioniRegistrazioneDumpHeadersCompactEnabled = false;

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
				this.daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());

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



			// Configurazione tracce e diagnostici
			try{

				// Indicazione se devo registrare tutte le tracce o solo quelle non ricostruibili
				this.transazioniRegistrazioneTracceProtocolPropertiesEnabled =  this.openspcoopProperties.isTransazioniRegistrazioneTracceProtocolPropertiesEnabled();
				this.transazioniRegistrazioneTracceHeaderRawEnabled = this.openspcoopProperties.isTransazioniRegistrazioneTracceHeaderRawEnabled();
				this.transazioniRegistrazioneTracceDigestEnabled = this.openspcoopProperties.isTransazioniRegistrazioneTracceDigestEnabled();
				
				// Indicazioni sulle modalita' di salvataggio degli header del dump
				this.transazioniRegistrazioneDumpHeadersCompactEnabled = this.openspcoopProperties.isTransazioniRegistrazioneDumpHeadersCompactEnabled();
				
				// Configurazione
				Transazioni configTransazioni = this.configPdDManager.getTransazioniConfigurazione();
				this.transazioniRegistrazioneTempiElaborazione = StatoFunzionalita.ABILITATO.equals(configTransazioni.getTempiElaborazione());
				this.transazioniRegistrazioneTokenInformazioniNormalizzate = StatoFunzionalita.ABILITATO.equals(configTransazioni.getToken());
				
				// salvataggio
				this.salvataggioTracceManager = this.openspcoopProperties.getTransazioniRegistrazioneTracceManager();
				this.salvataggioDiagnosticiManager = this.openspcoopProperties.getTransazioniRegistrazioneDiagnosticiManager();

			} catch (Exception e) {
				throw new HandlerException("Errore durante la lettura della configurazione della registrazione di tracce/diagnostici: " + e.getLocalizedMessage(), e);
			}

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

		Transaction transaction = TransactionContext.removeTransaction(idTransazione);
		
		
		/* ---- Verifica Esito della Transazione per registrazione nello storico ----- */
		
		List<String> esitiDaRegistrare = null;
		boolean exitTransactionAfterRateLimitingRemoveThread = false;
		try{
			Tracciamento configTracciamento = this.configPdDManager.getOpenSPCoopAppender_Tracciamento();
			StringBuilder bf = new StringBuilder();
			String esitiConfig = configTracciamento!=null ? configTracciamento.getEsiti() : null;
			try {
				if(transaction!=null && transaction.getRequestInfo()!=null && 
						transaction.getRequestInfo().getProtocolContext()!=null &&
						transaction.getRequestInfo().getProtocolContext().getInterfaceName()!=null) {
					switch (context.getTipoPorta()) {
					case DELEGATA:
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(transaction.getRequestInfo().getProtocolContext().getInterfaceName());
						PortaDelegata pd = this.configPdDManager.getPortaDelegata_SafeMethod(idPD);
						if(pd!=null && pd.getTracciamento()!=null && pd.getTracciamento().getEsiti()!=null) {
							esitiConfig = pd.getTracciamento().getEsiti();
						}
						break;
					case APPLICATIVA:
						IDPortaApplicativa idPA = new IDPortaApplicativa();
						idPA.setNome(transaction.getRequestInfo().getProtocolContext().getInterfaceName());
						PortaApplicativa pa = this.configPdDManager.getPortaApplicativa_SafeMethod(idPA);
						if(pa!=null && pa.getTracciamento()!=null && pa.getTracciamento().getEsiti()!=null) {
							esitiConfig = pa.getTracciamento().getEsiti();
						}
						break;
					default:
						break;
					}
				}
			}catch(Throwable e) {
				this.log.debug("["+idTransazione+"] Errore avvenuto durante la lettura della configurazione delle transazioni da salvare: "+e.getMessage(),e); 
			}
			esitiDaRegistrare = EsitiConfigUtils.getRegistrazioneEsiti(esitiConfig, this.log, bf);
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
					
					// EsitiProperties
					EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log, context.getProtocolFactory().getProtocol());
					List<Integer> tmpEsitiOk = esitiProperties.getEsitiCodeOk();
					List<String> esitiOk = new ArrayList<>();
					if(tmpEsitiOk!=null && tmpEsitiOk.size()>0){
						for (Integer esito : tmpEsitiOk) {
							esitiOk.add(esito+"");
						}
					}
					
					int code = context.getEsito().getCode();
					
					// ** Consegna Multipla **
					// NOTA: l'esito deve essere compreso solo dopo aver capito se le notifiche devono essere consegna o meno poichè le notifiche stesse si basano sullo stato di come è terminata la transazione sincrona
					boolean consegnaMultipla = PostOutResponseHandler_TransazioneUtilities.isConsegnaMultipla(context);
					if(consegnaMultipla) {
						code = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA);
					}
					
					String codeAsString = code+"";
					if(esitiDaRegistrare.contains(codeAsString)==false){
						String msg = "Transazione ID["+idTransazione+"] non salvata nello storico come richiesto dalla configurazione del tracciamento: esito [name:"+esitiProperties.getEsitoName(context.getEsito().getCode())+" code:"+codeAsString+"]";
						if(esitiOk.contains(codeAsString)){
							this.log.warn(msg);
						}
						else{
							this.log.error(msg);
						}
						// BUG OP-825
						// la gestione RateLimiting deve essere sempre fatta senno se si configurara di non registrare una transazione, poi si ha l'effetto che i contatori del Controllo del Traffico non vengono diminuiti.
						
//						int esitoViolazioneRateLimiting = esitiProperties.convertNameToCode(org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA.name());
//						int esitoViolazioneRateLimitingWarningOnly = esitiProperties.convertNameToCode(org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName.CONTROLLO_TRAFFICO_POLICY_VIOLATA_WARNING_ONLY.name());
//						int esitoMaxThreadsWarningOnly = esitiProperties.convertNameToCode(org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS_WARNING_ONLY.name());
//						if((esitoViolazioneRateLimiting == code) || //  Violazione Rate Limiting
//								(esitoViolazioneRateLimitingWarningOnly == code) ||  // Violazione Rate Limiting WarningOnly
//								(esitoMaxThreadsWarningOnly == code) // Superamento Limite Richieste WarningOnly
//								){
						exitTransactionAfterRateLimitingRemoveThread = true;
//						}
//						else{
//							this._releaseResources(null, idTransazione, context);
//							return;
//						}
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
		

			
		/* ---- Recupero dati della transazione dal contesto ----- */
		
		Boolean controlloCongestioneMaxRequestThreadRegistrato = null;
		if(this.openspcoopProperties.isControlloTrafficoEnabled()){
			Object objControlloCongestioneMaxRequestThreadRegistrato = context.getPddContext().getObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_THREAD_REGISTRATO);
			if(objControlloCongestioneMaxRequestThreadRegistrato!=null){
				controlloCongestioneMaxRequestThreadRegistrato = (Boolean) objControlloCongestioneMaxRequestThreadRegistrato;
				//System.out.println("CHECK POST OUT ["+context.getTipoPorta().name()+"] controlloCongestioneMaxRequestViolated["+controlloCongestioneMaxRequestViolated+"] controllo["+PddInterceptorConfig.isControlloCongestioneTraceTransazioneMaxThreadsViolated()+"]");
			}		
		}
		
		// Check Transaction
		if (transaction==null)
			throw new HandlerException("Dati della transazione assenti");
		transaction.setDeleted();


		Transazione transazioneDTO = null;
		try{
			
			IDSoggetto idDominio = this.openspcoopProperties.getIdentitaPortaDefault(context.getProtocolFactory().getProtocol());
			if(context.getProtocollo()!=null && context.getProtocollo().getDominio()!=null && !context.getProtocollo().getDominio().equals(idDominio)){
				idDominio = context.getProtocollo().getDominio();
			}
			else if(context.getPddContext()!=null && context.getPddContext().containsKey(Costanti.REQUEST_INFO)){
				RequestInfo requestInfo = (RequestInfo) context.getPddContext().getObject(Costanti.REQUEST_INFO);
				if(requestInfo!=null && requestInfo.getIdentitaPdD()!=null && !requestInfo.getIdentitaPdD().equals(idDominio)){
					idDominio = requestInfo.getIdentitaPdD();
				}
			}
	
			String modulo = "PostOutResponsePddOE";
	
			ExceptionSerialzerFileSystem exceptionSerializerFileSystem = new ExceptionSerialzerFileSystem(this.log);
	
			
			// ### Lettura dati Transazione ###
			
			boolean pddStateless = true;
			PostOutResponseHandler_TransazioneUtilities transazioneUtilities = null;
			HandlerException he = null;
			try{
				
				// Stateless
//				if ( (context.getIntegrazione()==null) ||
//						(context.getIntegrazione().isGestioneStateless()==null) ||
//						(context.getIntegrazione().isGestioneStateless()==false) 
//						){
//					pddStateless = false;
//				}
				// Cambio l'approccio per poter simulare anche gli errori nei diagnostici dove possibile
				// Tanto tutte le comunicazioni sono stateless a meno che non vengano tramutate in stateful
				if ( context.getIntegrazione()!=null && 
						context.getIntegrazione().isGestioneStateless()!=null &&
								!context.getIntegrazione().isGestioneStateless()){
					pddStateless = false;
				}
				
				/* ---- Salvo informazioni sulla transazioni nell'oggetto transazioniDTO ----- */
				transazioneUtilities = new PostOutResponseHandler_TransazioneUtilities(this.log, 
						this.transazioniRegistrazioneTracceHeaderRawEnabled,
						this.transazioniRegistrazioneTracceDigestEnabled,
						this.transazioniRegistrazioneTracceProtocolPropertiesEnabled,
						this.transazioniRegistrazioneTokenInformazioniNormalizzate,
						this.transazioniRegistrazioneTempiElaborazione);
				transazioneDTO = transazioneUtilities.fillTransaction(context, transaction, idDominio); // NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
	
			}catch (Throwable e) {
				try{
					exceptionSerializerFileSystem.registrazioneFileSystemDiagnosticiTracceDumpEmessiPdD(transaction, idTransazione, null);
				} catch (Exception eClose) {}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura della transazione sul database (Lettura dati Transazione): " + e.getLocalizedMessage();
				this.log.error("["+idTransazione+"] "+msg,e);
				he = new HandlerException(msg,e);
			}
			finally {	

				// ### Gestione Controllo Congestione ###
				// Nota: il motivo del perchè viene effettuato qua la "remove"
				// 	     risiede nel fatto che la risposta al client è già stata data
				//	     però il "thread occupato" dal client non è ancora stato liberato per una nuova richiesta
				//		 Se la remove viene messa nel finally del try-catch sottostante prima della remove si "paga" 
				//	     i tempi di attesa dell'inserimento della transazione sul database
				if(this.openspcoopProperties.isControlloTrafficoEnabled()){
					PostOutResponseHandler_GestioneControlloTraffico outHandler = new PostOutResponseHandler_GestioneControlloTraffico();
					outHandler.process(controlloCongestioneMaxRequestThreadRegistrato, this.log, idTransazione, transazioneDTO, context);
				}
				if(he!=null) {
					throw he;
				}
				else if(exitTransactionAfterRateLimitingRemoveThread){
					// La risorsa viene rilasciata nel finally
					//this.releaseResources(transaction, idTransazione, context);
					return;
				}
			}
			
			
			
			// Il controllo stateful è stato spostato sotto il blocco soprastante, per assicurare la gestione del traffico (decremento dei contatori)
			
			// NOTA: se l'integrazione e' null o l'indicazione se la gestione stateless e' null, significa che la PdD non e' ancora riuscita
			// a capire che tipo di gestione deve adottare. Queste transazioni devono essere sempre registrate perche' riguardano cooperazioni andate in errore all'inizio,
			// es. Porta Delegata non esistente, busta malformata....
			if(context.getIntegrazione()!=null && 
					context.getIntegrazione().isGestioneStateless()!=null &&
					!context.getIntegrazione().isGestioneStateless()){
				if(this.openspcoopProperties.isTransazioniStatefulEnabled()==false){
					//if(this.debug)
					this.log.error("["+idTransazione+"] Transazione non registrata, gestione stateful non abilitata");
					// NOTA: TODO thread che ripulisce header di trasporto o dump messaggi non associati a transazioni.
					// La risorsa viene rilasciata nel finally
					//this._releaseResources(transaction, idTransazione, context);
					return;
				}
			}
			
			
			
			
			
			
			// ### FileTrace ###
			
			if(this.openspcoopProperties.isTransazioniFileTraceEnabled()) {
				try {
					FileTraceConfig config = FileTraceConfig.getConfig(this.openspcoopProperties.getTransazioniFileTraceConfig());
					FileTraceManager	fileTraceManager = new FileTraceManager(this.log, config);
					fileTraceManager.buildTransazioneInfo(transazioneDTO, transaction);
					fileTraceManager.invoke(context.getTipoPorta(), context.getPddContext());
				}catch (Throwable e) {
					this.log.error("["+idTransazione+"] File trace fallito: "+e.getMessage(),e);
				}
			}

			
			
			
			
			
			// ### Gestione Transazione ###
	
			boolean autoCommit = true;
			DBTransazioniManager dbManager = null;
	    	Resource resource = null;
			Connection connection = null;
			boolean errore = false;
			try {
				
	
				/* ---- Recupero informazioni sulla modalita' di salvataggio delle tracce ----- */
	
				// TRACCIA RICHIESTA
				boolean registraTracciaRichiesta = true;
				String informazioneTracciaRichiestaDaSalvare = null;
				if(this.salvataggioTracceManager!=null) {
					StatoSalvataggioTracce statoTracciaRichiesta =  
							this.salvataggioTracceManager.getInformazioniSalvataggioTracciaRichiesta(this.log, context, transaction, transazioneDTO, pddStateless);
					if(statoTracciaRichiesta!=null) {
						registraTracciaRichiesta = (statoTracciaRichiesta.isCompresso()==false);
						informazioneTracciaRichiestaDaSalvare = statoTracciaRichiesta.getInformazioneCompressa();
					}
					if(this.debug){
						this.log.debug("["+idTransazione+"] Emissione traccia richiesta: "+registraTracciaRichiesta);
						if(statoTracciaRichiesta!=null) {
							this.log.debug("["+idTransazione+"] Informazioni Salvataggio traccia richiesta (compresso:"+statoTracciaRichiesta.isCompresso()+
									" errore:"+statoTracciaRichiesta.isErrore()+"): "+statoTracciaRichiesta.getInformazione());
						}
					}
					else{
						if(statoTracciaRichiesta!=null && statoTracciaRichiesta.isErrore()){
								this.log.warn("["+idTransazione+"] Informazioni Salvataggio traccia richiesta in errore: "+statoTracciaRichiesta.getInformazione());
						}
					}
				}
				
				// TRACCIA RISPOSTA
				boolean registraTracciaRisposta = true;
				String informazioneTracciaRispostaDaSalvare = null;
				if(this.salvataggioTracceManager!=null) {
					StatoSalvataggioTracce statoTracciaRisposta =  
							this.salvataggioTracceManager.getInformazioniSalvataggioTracciaRisposta(this.log, context, transaction, transazioneDTO, pddStateless);
					if(statoTracciaRisposta!=null) {
						registraTracciaRisposta = (statoTracciaRisposta.isCompresso()==false);
						informazioneTracciaRispostaDaSalvare = statoTracciaRisposta.getInformazioneCompressa();
					}
					if(this.debug){
						this.log.debug("["+idTransazione+"] Emissione traccia risposta: "+registraTracciaRisposta);
						if(statoTracciaRisposta!=null) {
							this.log.debug("["+idTransazione+"] Informazioni Salvataggio traccia risposta (compresso:"+statoTracciaRisposta.isCompresso()+
									" errore:"+statoTracciaRisposta.isErrore()+"): "+statoTracciaRisposta.getInformazione());
						}
					}
					else{
						if(statoTracciaRisposta!=null && statoTracciaRisposta.isErrore()){
								this.log.warn("["+idTransazione+"] Informazioni Salvataggio traccia risposta in errore: "+statoTracciaRisposta.getInformazione());
						}
					}
				}
	
				// MESSAGGI DIAGNOSTICI
				boolean registrazioneMessaggiDiagnostici = true;
				HashMap<DiagnosticColumnType, String> informazioniDiagnosticiDaSalvare = null;
				if(this.salvataggioDiagnosticiManager!=null) {
					StatoSalvataggioDiagnostici statoDiagnostici =  
							this.salvataggioDiagnosticiManager.getInformazioniSalvataggioDiagnostici(this.log, context, transaction, transazioneDTO, pddStateless);
					if(statoDiagnostici!=null) {
						registrazioneMessaggiDiagnostici = (statoDiagnostici.isCompresso()==false);
						informazioniDiagnosticiDaSalvare = statoDiagnostici.getInformazioneCompressa();
					}
					if(this.debug){
						this.log.debug("["+idTransazione+"] Emissione diagnostici: "+registrazioneMessaggiDiagnostici);
						if(statoDiagnostici!=null) {
							this.log.debug("["+idTransazione+"] Informazioni Salvataggio diagnostici (compresso:"+statoDiagnostici.isCompresso()+
									" errore:"+statoDiagnostici.isErrore()+"): "+statoDiagnostici.getInformazione());
						}
					}
					else{
						if(statoDiagnostici!=null && statoDiagnostici.isErrore()){
								this.log.warn("["+idTransazione+"] Informazioni Salvataggio diagnostici in errore: "+statoDiagnostici.getInformazione());
						}
					}
				}
	
				
				// CONTENUTI
				boolean registrazioneRisorse = transaction.getTransactionServiceLibrary()!=null || transaction.sizeMessaggi()>0;
				
	
				// AUTO-COMMIT
				if(registraTracciaRichiesta || registraTracciaRisposta || registrazioneMessaggiDiagnostici || registrazioneRisorse){
					autoCommit = false; // devo registrare piu' informazioni oltre alla transazione
				}
				if(this.debug)
					this.logSql.debug("["+idTransazione+"] AutoCommit: "+this.debug);
	
	
	
	
	
				/* ---- Connection/Service Manager ----- */
	
				if(this.debug)
					this.logSql.debug("["+idTransazione+"] recupero jdbcServiceManager in corso ...");
	
				// Ottiene la connessione al db
				dbManager = DBTransazioniManager.getInstance();
				resource = dbManager.getResource(idDominio, modulo, idTransazione);
				if(resource==null){
					throw new Exception("Risorsa al database non disponibile");
				}
				connection = (Connection) resource.getResource();
				if(connection == null)
					throw new Exception("Connessione non disponibile");	

				if(autoCommit==false){
					connection.setAutoCommit(false);
				}
				org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
						(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) 
						this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
								connection, autoCommit,
								this.daoFactoryServiceManagerPropertiesTransazioni, this.daoFactoryLoggerTransazioni);
				jdbcServiceManager.getJdbcProperties().setShowSql(this.debug);
				ITransazioneService transazioneService = jdbcServiceManager.getTransazioneService();
				if(this.debug)
					this.logSql.debug("["+idTransazione+"] recupero jdbcServiceManager effettuato");
	
	
	
	
				/* ---- Inserimento dati transazione ----- */
	
				// Inserisco transazione
				if(this.debug)
					this.log.debug("["+idTransazione+"] inserimento transazione in corso ...");
				
				// ** dati tracce **
				transazioneDTO.setTracciaRichiesta(informazioneTracciaRichiestaDaSalvare);
				transazioneDTO.setTracciaRisposta(informazioneTracciaRispostaDaSalvare);
				
				// ** dati diagnostica **
				if(informazioniDiagnosticiDaSalvare!=null){
					transazioneDTO.setDiagnostici(informazioniDiagnosticiDaSalvare.get(DiagnosticColumnType.META_INF));
					transazioneDTO.setDiagnosticiList1(informazioniDiagnosticiDaSalvare.get(DiagnosticColumnType.LIST1));
					transazioneDTO.setDiagnosticiList2(informazioniDiagnosticiDaSalvare.get(DiagnosticColumnType.LIST2));
					transazioneDTO.setDiagnosticiListExt(informazioniDiagnosticiDaSalvare.get(DiagnosticColumnType.LIST_EXT));
					transazioneDTO.setDiagnosticiExt(informazioniDiagnosticiDaSalvare.get(DiagnosticColumnType.EXT));
				}
				
				transazioneService.create(transazioneDTO);
				if(this.debug)
					this.log.debug("["+idTransazione+"] inserita transazione");
	
	
	
	
	
				/* ---- Inserimento dati tracce ----- */
				
				if( registraTracciaRichiesta && transaction.getTracciaRichiesta()!=null){
					if(this.debug)
						this.log.debug("["+idTransazione+"] registrazione traccia richiesta...");
					this.tracciamentoOpenSPCoopAppender.log(connection, transaction.getTracciaRichiesta());
					if(this.debug)
						this.log.debug("["+idTransazione+"] registrazione traccia richiesta completata");
				}	
				if( registraTracciaRisposta && transaction.getTracciaRisposta()!=null){
					if(this.debug)
						this.log.debug("["+idTransazione+"] registrazione traccia risposta...");
					this.tracciamentoOpenSPCoopAppender.log(connection, transaction.getTracciaRisposta());
					if(this.debug)
						this.log.debug("["+idTransazione+"] registrazione traccia risposta completata");
				}	
	
	
	
	
	
	
				/* ---- Inserimento messaggi diagnostici ----- */
				
				if(registrazioneMessaggiDiagnostici){
					for(int i=0; i<transaction.sizeMsgDiagnostici(); i++){
						MsgDiagnostico msgDiagnostico = transaction.getMsgDiagnostico(i);
						if(msgDiagnostico.getIdSoggetto()==null){
							msgDiagnostico.setIdSoggetto(idDominio);
						}
						if(this.debug)
							this.log.debug("["+idTransazione+"] registrazione diagnostico con codice ["+msgDiagnostico.getCodice()+"] ...");
						this.msgDiagnosticiOpenSPCoopAppender.log(connection,msgDiagnostico);
						if(this.debug)
							this.log.debug("["+idTransazione+"] registrazione diagnostico con codice ["+msgDiagnostico.getCodice()+"] completata");
					}
				}
	
	
				
				
				
				/* ---- Inserimento dump ----- */
				
				for(int i=0; i<transaction.sizeMessaggi(); i++){
					Messaggio messaggio = transaction.getMessaggio(i);
					if(messaggio.getProtocollo()==null) {
						messaggio.setProtocollo(transazioneDTO.getProtocollo());
					}
					if(messaggio.getDominio()==null) {
						messaggio.setDominio(idDominio);
					}
					if(messaggio.getTipoPdD()==null) {
						messaggio.setTipoPdD(context.getTipoPorta());
					}
					if(messaggio.getIdFunzione()==null) {
						messaggio.setIdFunzione(modulo);
					}
					if(messaggio.getIdBusta()==null) {
						if(context.getProtocollo()!=null) {
							messaggio.setIdBusta(context.getProtocollo().getIdRichiesta());
						}
					}
					if(messaggio.getFruitore()==null) {
						if(context.getProtocollo()!=null) {
							messaggio.setFruitore(context.getProtocollo().getFruitore());
						}
					}
					if(messaggio.getServizio()==null) {
						if(context.getProtocollo()!=null) {
							IDServizio idServizio = IDServizioFactory.getInstance().
									getIDServizioFromValuesWithoutCheck(context.getProtocollo().getTipoServizio(), 
											context.getProtocollo().getServizio(), 
											context.getProtocollo().getErogatore()!=null ? context.getProtocollo().getErogatore().getTipo() : null, 
											context.getProtocollo().getErogatore()!=null ? context.getProtocollo().getErogatore().getNome() : null,
											context.getProtocollo().getVersioneServizio()!=null ? context.getProtocollo().getVersioneServizio() : -1);
							messaggio.setServizio(idServizio);
						}
					}
					if(this.debug)
						this.log.debug("["+idTransazione+"] registrazione di tipo ["+messaggio.getTipoMessaggio()+"] ...");
					if(this.dumpOpenSPCoopAppender instanceof org.openspcoop2.pdd.logger.DumpOpenSPCoopProtocolAppender) {
						((org.openspcoop2.pdd.logger.DumpOpenSPCoopProtocolAppender)this.dumpOpenSPCoopAppender).dump(connection,messaggio,this.transazioniRegistrazioneDumpHeadersCompactEnabled);
					}
					else {
						this.dumpOpenSPCoopAppender.dump(connection,messaggio);
					}
					if(this.debug)
						this.log.debug("["+idTransazione+"] registrazione di tipo ["+messaggio.getTipoMessaggio()+"] completata");
				}
				
				
				
				
				
				/* ---- Inserimento risorse contenuti (library personalizzata) ----- */
				
				if(registrazioneRisorse){
					IDumpMessaggioService dumpMessageService = jdbcServiceManager.getDumpMessaggioService();
					PostOutResponseHandler_ContenutiUtilities contenutiUtilities = new PostOutResponseHandler_ContenutiUtilities(this.log);
					contenutiUtilities.insertContenuti(transazioneDTO, 
							transaction.getTracciaRichiesta(), transaction.getTracciaRisposta(), 
							transaction.getMsgDiagnostici(),
							dumpMessageService, transaction.getRisorse(), transaction.getTransactionServiceLibrary(), this.daoFactory);
				}
	
				
	
	
				// COMMIT
				if(autoCommit==false)
					connection.commit();
	
	
			} catch (SQLException sqlEx) {
				errore = true;
				try{
					if(autoCommit==false)
						connection.rollback();
				}catch(Exception eRollback){}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura della transazione sul database (sql): " + sqlEx.getLocalizedMessage();
				this.log.error("["+idTransazione+"] "+msg,sqlEx);
				throw new HandlerException(msg,sqlEx);			
			}  catch (Throwable e) {
				errore = true;
				try{
					if(autoCommit==false)
						connection.rollback();
				}catch(Exception eRollback){}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura della transazione sul database: " + e.getLocalizedMessage();
				this.log.error("["+idTransazione+"] "+msg,e);
				throw new HandlerException(msg,e);	
			} finally {
				
				// Ripristino Autocomit
				try {
					if(autoCommit==false)
						connection.setAutoCommit(true);
				} catch (Exception e) {}
									
				// Chiusura della connessione al database
				try {
					//this.dbManager.releaseResource(idDominio, modulo, dbResource);
					if(resource!=null)
						dbManager.releaseResource(idDominio, modulo, resource);
				} catch (Exception e) {}
				
				// Registrazione su FileSystem informazioni se la gestione e' andata in errore
				if(errore){
					try {
						exceptionSerializerFileSystem.registrazioneFileSystemDiagnosticiTracceDumpEmessiPdD(transaction, idTransazione, transazioneDTO);
					} catch (Exception e) {}
					try {
						exceptionSerializerFileSystem.registrazioneFileSystem(transazioneDTO, idTransazione);
					} catch (Exception e) {}
				}
								
			}
			
		}finally{

			this._releaseResources(transaction, idTransazione, context);
						
		}
	}

	private void _releaseResources(Transaction transactionParam, String idTransazione, PostOutResponseContext context) {
		
		Transaction transaction = null;
		try {
			transaction = TransactionContext.removeTransaction(idTransazione);
			if(transaction!=null) {
				transaction.setDeleted();
			}
			// altrimenti e' gia' stata eliminata
		} catch (Throwable e) {
			this.log.error("["+idTransazione+"] Errore durante la rimozione della registrazione delle transazione",e);
		}
			
		/* ---- Elimino informazione per filtro duplicati ----- */
		if(context!=null && context.getProtocollo()!=null){
			if(context.getProtocollo().getIdRichiesta()!=null){
				try {
					TransactionContext.removeIdentificativoProtocollo(context.getProtocollo().getIdRichiesta());
				} catch (Throwable e) {
					this.log.error("["+idTransazione+"] Errore durante la rimozione della registrazione dell'identificativo di protocollo della richiesta ["+context.getProtocollo().getIdRichiesta()+"]",e);
				}
			}
			if(context.getProtocollo().getIdRisposta()!=null){
				try {
					TransactionContext.removeIdentificativoProtocollo(context.getProtocollo().getIdRisposta());
				} catch (Throwable e) {
					this.log.error("["+idTransazione+"] Errore durante la rimozione della registrazione dell'identificativo di protocollo della risposta ["+context.getProtocollo().getIdRisposta()+"]",e);
				}
			}
		}
		
	}
	
	
}
