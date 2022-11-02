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
package org.openspcoop2.pdd.core.handlers.transazioni;

import java.io.File;
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
import org.openspcoop2.core.constants.TipoPdD;
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
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.filetrace.FileTraceConfig;
import org.openspcoop2.pdd.logger.filetrace.FileTraceManager;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
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
	private boolean transazioniRegistrazioneAttributiInformazioniNormalizzate = false;
	private boolean transazioniRegistrazioneTempiElaborazione = false;
	private boolean transazioniRegistrazioneRetrieveToken_saveAsTokenInfo = false;
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
				this.transazioniRegistrazioneAttributiInformazioniNormalizzate = StatoFunzionalita.ABILITATO.equals(configTransazioni.getToken()) &&
						this.openspcoopProperties.isGestioneAttributeAuthority_transazioniRegistrazioneAttributiInformazioniNormalizzate(); // per adesso la configurazione avviene via govway.properties
				this.transazioniRegistrazioneRetrieveToken_saveAsTokenInfo = StatoFunzionalita.ABILITATO.equals(configTransazioni.getToken()) &&
						this.openspcoopProperties.isGestioneRetrieveToken_saveAsTokenInfo(); // per adesso la configurazione avviene via govway.properties
				
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
		
		TransazioniProcessTimes times = null;
		long timeStart = -1;
		if(this.openspcoopProperties.isTransazioniRegistrazioneSlowLog()) {
			times = new TransazioniProcessTimes();
			timeStart = DateManager.getTimeMillis();
		}
		try {
			invoke(context, times);
		}finally {
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				if(timeProcess>=this.openspcoopProperties.getTransazioniRegistrazioneSlowLogThresholdMs()) {
					StringBuilder sb = new StringBuilder();
					sb.append(timeProcess);
					if(times.idTransazione!=null) {
						sb.append(" <").append(times.idTransazione).append(">");
					}
					sb.append(" [PostOutResponse]");
					sb.append(" ").append(times.toString());
					OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSlowLog().info(sb.toString());
				}
			}
		}
		
	}
	public void invoke(PostOutResponseContext context, TransazioniProcessTimes times) throws HandlerException {
		


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
		if(times!=null) {
			times.idTransazione = idTransazione;
		}
		
		InformazioniToken informazioniToken = null;
		InformazioniAttributi informazioniAttributi = null;
		InformazioniNegoziazioneToken informazioniNegoziazioneToken = null;
		SecurityToken securityToken = null;
		if(transaction!=null) {
			informazioniToken = transaction.getInformazioniToken();
			informazioniNegoziazioneToken = transaction.getInformazioniNegoziazioneToken();
		}
		if(context.getPddContext()!=null) {
			Object oInformazioniAttributiNormalizzati = context.getPddContext().getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_ATTRIBUTI_INFORMAZIONI_NORMALIZZATE);
			if(oInformazioniAttributiNormalizzati!=null && oInformazioniAttributiNormalizzati instanceof InformazioniAttributi) {
				informazioniAttributi = (InformazioniAttributi) oInformazioniAttributiNormalizzati;
			}
			securityToken = SecurityTokenUtilities.readSecurityToken(context.getPddContext());
		}
		
		/* ---- Verifica Esito della Transazione per registrazione nello storico ----- */
		
		List<String> esitiDaRegistrare = null;
		boolean exitTransactionAfterRateLimitingRemoveThread = false;
		boolean fileTraceEnabled = false;
		File fileTraceConfig = null;
		boolean fileTraceConfigGlobal = true;
		try{
			Tracciamento configTracciamento = this.configPdDManager.getOpenSPCoopAppender_Tracciamento();
			StringBuilder bf = new StringBuilder();
			String esitiConfig = configTracciamento!=null ? configTracciamento.getEsiti() : null;
			fileTraceEnabled = this.openspcoopProperties.isTransazioniFileTraceEnabled();
			if(fileTraceEnabled) {
				fileTraceConfig = this.openspcoopProperties.getTransazioniFileTraceConfig();
			}
			try {
				if(transaction!=null && transaction.getRequestInfo()!=null && 
						transaction.getRequestInfo().getProtocolContext()!=null &&
						transaction.getRequestInfo().getProtocolContext().getInterfaceName()!=null) {
					switch (context.getTipoPorta()) {
					case DELEGATA:
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(transaction.getRequestInfo().getProtocolContext().getInterfaceName());
						PortaDelegata pd = this.configPdDManager.getPortaDelegata_SafeMethod(idPD, transaction.getRequestInfo());
						if(pd!=null && pd.getTracciamento()!=null && pd.getTracciamento().getEsiti()!=null) {
							esitiConfig = pd.getTracciamento().getEsiti();
						}
						try {
							fileTraceEnabled = this.configPdDManager.isTransazioniFileTraceEnabled(pd);
							if(fileTraceEnabled) {
								fileTraceConfig = this.configPdDManager.getFileTraceConfig(pd);
								fileTraceConfigGlobal = this.openspcoopProperties.isTransazioniFileTraceEnabled() && 
										this.openspcoopProperties.getTransazioniFileTraceConfig().getAbsolutePath().equals(fileTraceConfig.getAbsolutePath());
							}
							else {
								fileTraceConfig = null;
							}
						}catch(Throwable e) {
							this.log.debug("["+idTransazione+"] Errore avvenuto durante la lettura della configurazione file-trace: "+e.getMessage(),e); 
							fileTraceEnabled = false;
						}
						break;
					case APPLICATIVA:
						IDPortaApplicativa idPA = new IDPortaApplicativa();
						idPA.setNome(transaction.getRequestInfo().getProtocolContext().getInterfaceName());
						PortaApplicativa pa = this.configPdDManager.getPortaApplicativa_SafeMethod(idPA, transaction.getRequestInfo());
						if(pa!=null && pa.getTracciamento()!=null && pa.getTracciamento().getEsiti()!=null) {
							esitiConfig = pa.getTracciamento().getEsiti();
						}
						try {
							fileTraceEnabled = this.configPdDManager.isTransazioniFileTraceEnabled(pa);
							if(fileTraceEnabled) {
								fileTraceConfig = this.configPdDManager.getFileTraceConfig(pa);
								fileTraceConfigGlobal = this.openspcoopProperties.isTransazioniFileTraceEnabled() && 
										this.openspcoopProperties.getTransazioniFileTraceConfig().getAbsolutePath().equals(fileTraceConfig.getAbsolutePath());
							}
							else {
								fileTraceConfig = null;
							}
						}catch(Throwable e) {
							this.log.debug("["+idTransazione+"] Errore avvenuto durante la lettura della configurazione file-trace: "+e.getMessage(),e); 
							fileTraceEnabled = false;
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
					EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log, context.getProtocolFactory());
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
			this.log.error("Transazione ID["+idTransazione+"] errore durante l'identificazione degli esiti da registrare: "+e.getMessage(),e);
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
			
			RequestInfo requestInfo = null;
			if(context.getPddContext()!=null && context.getPddContext().containsKey(Costanti.REQUEST_INFO)){
				requestInfo = (RequestInfo) context.getPddContext().getObject(Costanti.REQUEST_INFO);
			}
			
			IDSoggetto idDominio = this.openspcoopProperties.getIdentitaPortaDefault(context.getProtocolFactory().getProtocol(), requestInfo); 
			if(context.getProtocollo()!=null && context.getProtocollo().getDominio()!=null && !context.getProtocollo().getDominio().equals(idDominio)){
				idDominio = context.getProtocollo().getDominio();
			}
			else if(requestInfo!=null){
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
			long timeStart = -1;
			try{
				if(times!=null) {
					timeStart = DateManager.getTimeMillis();
				}
				
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
						this.transazioniRegistrazioneAttributiInformazioniNormalizzate,
						this.transazioniRegistrazioneTempiElaborazione,
						this.transazioniRegistrazioneRetrieveToken_saveAsTokenInfo);
				transazioneDTO = transazioneUtilities.fillTransaction(context, transaction, idDominio,
						( (times!=null && this.openspcoopProperties.isTransazioniRegistrazioneSlowLogBuildTransactionDetails()) ? times : null)); // NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
	
			}catch (Throwable e) {
				try{
					exceptionSerializerFileSystem.registrazioneFileSystemDiagnosticiTracceDumpEmessiPdD(transaction, idTransazione, null,
							true, true, true, true);
				} catch (Exception eClose) {}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura della transazione sul database (Lettura dati Transazione): " + e.getLocalizedMessage();
				this.log.error("["+idTransazione+"] "+msg,e);
				he = new HandlerException(msg,e);
			}
			finally {	

				if(times!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					times.fillTransaction = timeProcess;
				}
				
				// ### Gestione Controllo Congestione ###
				// Nota: il motivo del perchè viene effettuato qua la "remove"
				// 	     risiede nel fatto che la risposta al client è già stata data
				//	     però il "thread occupato" dal client non è ancora stato liberato per una nuova richiesta
				//		 Se la remove viene messa nel finally del try-catch sottostante prima della remove si "paga" 
				//	     i tempi di attesa dell'inserimento della transazione sul database
				if(this.openspcoopProperties.isControlloTrafficoEnabled()){
					
					try {
						if(times!=null) {
							timeStart = DateManager.getTimeMillis();
						}
						PostOutResponseHandler_GestioneControlloTraffico outHandler = new PostOutResponseHandler_GestioneControlloTraffico();
						outHandler.process(controlloCongestioneMaxRequestThreadRegistrato, this.log, idTransazione, transazioneDTO, context,
								( (times!=null && this.openspcoopProperties.isTransazioniRegistrazioneSlowLogRateLimitingDetails()) ? times : null));
					}catch (Throwable e) {
						this.log.error("["+idTransazione+"] Errore durante la registrazione di terminazione del thread: "+e.getMessage(),e);
					}finally {
						if(times!=null) {
							long timeEnd =  DateManager.getTimeMillis();
							long timeProcess = timeEnd-timeStart;
							times.controlloTraffico = timeProcess;
						}
					}
				}
				
				// ### FileTrace ###
				if(fileTraceEnabled) {
					logWithFileTrace(fileTraceConfig, fileTraceConfigGlobal,
							times,
							transazioneDTO, transaction, 
							informazioniToken,
							informazioniAttributi,
							informazioniNegoziazioneToken,
							securityToken,
							context,
							idTransazione); // anche qua vi e' un try catch con Throwable
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
			
			
			
			
			
	
			
			
			// ### Gestione Transazione ###
	
			boolean autoCommit = true;
			DBTransazioniManager dbManager = null;
	    	Resource resource = null;
			Connection connection = null;
			boolean errore = false;
			boolean registraTracciaRichiesta = true;
			boolean registraTracciaRisposta = true;
			boolean registrazioneMessaggiDiagnostici = true;
			boolean registrazioneDumpMessaggi = true;
			try {
				
	
				/* ---- Recupero informazioni sulla modalita' di salvataggio delle tracce ----- */
	
				String informazioneTracciaRichiestaDaSalvare = null;
				String informazioneTracciaRispostaDaSalvare = null;
				HashMap<DiagnosticColumnType, String> informazioniDiagnosticiDaSalvare = null;
				try {
					if(times!=null) {
						timeStart = DateManager.getTimeMillis();
					}
					
					// TRACCIA RICHIESTA
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
				}finally {
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.processTransactionInfo = timeProcess;
					}
				}
				
				// IMPOSTO INFORMAZIONI IN TRANSAZIONE DTO
				
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
				try {
					if(times!=null) {
						timeStart = DateManager.getTimeMillis();
					}
					dbManager = DBTransazioniManager.getInstance();
					resource = dbManager.getResource(idDominio, modulo, idTransazione);
				}finally {
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.getConnection = timeProcess;
					}
				}
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
				
				try {
					if(times!=null) {
						timeStart = DateManager.getTimeMillis();
					}
					transazioneService.create(transazioneDTO);
				}finally {
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.insertTransaction = timeProcess;
					}
				}
				if(this.debug)
					this.log.debug("["+idTransazione+"] inserita transazione");
	
	
	
	
	
				/* ---- Inserimento dati tracce ----- */
				
				try {
					timeStart = -1;
					if( registraTracciaRichiesta && transaction.getTracciaRichiesta()!=null){
						if(times!=null) {
							timeStart = DateManager.getTimeMillis();
						}
						if(this.debug)
							this.log.debug("["+idTransazione+"] registrazione traccia richiesta...");
						this.tracciamentoOpenSPCoopAppender.log(connection, transaction.getTracciaRichiesta());
						if(this.debug)
							this.log.debug("["+idTransazione+"] registrazione traccia richiesta completata");
					}	
					if( registraTracciaRisposta && transaction.getTracciaRisposta()!=null){
						if(times!=null && timeStart==-1) {
							timeStart = DateManager.getTimeMillis();
						}
						if(this.debug)
							this.log.debug("["+idTransazione+"] registrazione traccia risposta...");
						this.tracciamentoOpenSPCoopAppender.log(connection, transaction.getTracciaRisposta());
						if(this.debug)
							this.log.debug("["+idTransazione+"] registrazione traccia risposta completata");
					}	
				}finally {
					if(times!=null && timeStart>0) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.insertTrace = timeProcess;
					}
				}
	
	
	
	
	
				/* ---- Inserimento messaggi diagnostici ----- */
				
				try {
					timeStart = -1;
					if(registrazioneMessaggiDiagnostici){
						if(times!=null && transaction!=null && transaction.sizeMsgDiagnostici()>0) {
							timeStart = DateManager.getTimeMillis();
						}
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
				}finally {
					if(times!=null && timeStart>0) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.insertDiagnostics = timeProcess;
					}
				}
	
				
				
				
				/* ---- Inserimento dump ----- */
				
				try {
					timeStart = -1;
					if(times!=null && transaction!=null && transaction.sizeMessaggi()>0) {
						timeStart = DateManager.getTimeMillis();
					}
					for(int i=0; i<transaction.sizeMessaggi(); i++){
						Messaggio messaggio = transaction.getMessaggio(i);
						try {
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
							this.dumpOpenSPCoopAppender.dump(connection,messaggio,this.transazioniRegistrazioneDumpHeadersCompactEnabled);
							if(this.debug)
								this.log.debug("["+idTransazione+"] registrazione di tipo ["+messaggio.getTipoMessaggio()+"] completata");
						}finally {
							try {
								if(messaggio.getBody()!=null) {
									messaggio.getBody().unlock();
									messaggio.getBody().clearResources();
								}
							}catch(Throwable t){
								this.log.error("["+idTransazione+"] errore durante il rilascio delle risorse del messaggio: "+t.getMessage(),t);
							}
						}
					}
				}finally {
					if(times!=null && timeStart>0) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.insertContents = timeProcess;
					}
				}

				
				
				
				/* ---- Inserimento risorse contenuti (library personalizzata) ----- */
				
				if(registrazioneRisorse){
					try {
						if(times!=null) {
							timeStart = DateManager.getTimeMillis();
						}
						IDumpMessaggioService dumpMessageService = jdbcServiceManager.getDumpMessaggioService();
						PostOutResponseHandler_ContenutiUtilities contenutiUtilities = new PostOutResponseHandler_ContenutiUtilities(this.log);
						contenutiUtilities.insertContenuti(transazioneDTO, 
								transaction.getTracciaRichiesta(), transaction.getTracciaRisposta(), 
								transaction.getMsgDiagnostici(),
								dumpMessageService, transaction.getRisorse(), transaction.getTransactionServiceLibrary(), this.daoFactory);
					}finally {
						if(times!=null) {
							long timeEnd =  DateManager.getTimeMillis();
							long timeProcess = timeEnd-timeStart;
							times.insertResources = timeProcess;
						}
					}
				}
	
				
	
				// COMMIT
				if(autoCommit==false) {
					try {
						if(times!=null) {
							timeStart = DateManager.getTimeMillis();
						}
						connection.commit();
					}finally {
						if(times!=null) {
							long timeEnd =  DateManager.getTimeMillis();
							long timeProcess = timeEnd-timeStart;
							times.commit = timeProcess;
						}
					}
				}
	
	
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
						exceptionSerializerFileSystem.registrazioneFileSystemDiagnosticiTracceDumpEmessiPdD(transaction, idTransazione, transazioneDTO,
								registraTracciaRichiesta, registraTracciaRisposta, registrazioneMessaggiDiagnostici, registrazioneDumpMessaggi);
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
			// Aggiunto check Applicativa e Delegata per evitare che comunicazioni dove i due domini sono entrambi gestiti sul solito GovWay si eliminano a vicenda gli id.
			if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta()) && context.getProtocollo().getIdRichiesta()!=null){
				try {
					TransactionContext.removeIdentificativoProtocollo(context.getProtocollo().getIdRichiesta());
				} catch (Throwable e) {
					this.log.error("["+idTransazione+"] Errore durante la rimozione della registrazione dell'identificativo di protocollo della richiesta ["+context.getProtocollo().getIdRichiesta()+"]",e);
				}
			}
			if(TipoPdD.DELEGATA.equals(context.getTipoPorta()) && context.getProtocollo().getIdRisposta()!=null){
				try {
					TransactionContext.removeIdentificativoProtocollo(context.getProtocollo().getIdRisposta());
				} catch (Throwable e) {
					this.log.error("["+idTransazione+"] Errore durante la rimozione della registrazione dell'identificativo di protocollo della risposta ["+context.getProtocollo().getIdRisposta()+"]",e);
				}
			}
		}
		
	}
	
	private void logWithFileTrace(File fileTraceConfig, boolean fileTraceConfigGlobal,
			TransazioniProcessTimes times,
			Transazione transazioneDTO, Transaction transaction, 
			InformazioniToken informazioniToken,
			InformazioniAttributi informazioniAttributi,
			InformazioniNegoziazioneToken informazioniNegoziazioneToken,
			SecurityToken securityToken,
			PostOutResponseContext context,
			String idTransazione) {
		FileTraceManager fileTraceManager = null;
		long timeStart = -1;
		try {
			if(times!=null) {
				timeStart = DateManager.getTimeMillis();
			}
			FileTraceConfig config = FileTraceConfig.getConfig(fileTraceConfig, fileTraceConfigGlobal);
			fileTraceManager = new FileTraceManager(this.log, config);
			fileTraceManager.buildTransazioneInfo(context.getProtocolFactory(), transazioneDTO, transaction, 
					informazioniToken,
					informazioniAttributi,
					informazioniNegoziazioneToken,
					securityToken,
					context.getPddContext());
			fileTraceManager.invoke(context.getTipoPorta(), context.getPddContext());
		}catch (Throwable e) {
			this.log.error("["+idTransazione+"] File trace fallito: "+e.getMessage(),e);
		}finally {
			try {
				if(fileTraceManager!=null) {
					fileTraceManager.cleanResourcesForOnlyFileTrace(transaction);
				}
			}catch(Throwable eClean) {
				this.log.error("["+idTransazione+"] File trace 'clean' fallito: "+eClean.getMessage(),eClean);
			}
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fileTrace = timeProcess;
			}
		}
	}
	
}

class TransazioniProcessTimes{

	String idTransazione;
	long fillTransaction = -1;
	List<String> fillTransaction_details = null;
	long controlloTraffico = -1;
	long controlloTraffico_removeThread = -1;
	long controlloTraffico_preparePolicy = -1;
	List<String> controlloTraffico_policyTimes = null;
	long fileTrace = -1;
	long processTransactionInfo = -1;
	long getConnection = -1;
	long insertTransaction = -1;
	long insertDiagnostics = -1;
	long insertTrace = -1;
	long insertContents = -1;
	long insertResources = -1;
	long commit = -1;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this.fillTransaction>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("buildTransaction:").append(this.fillTransaction);
		}
		if(this.fillTransaction_details!=null && !this.fillTransaction_details.isEmpty()) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("buildTransactionDetails:{");
			boolean first = true;
			for (String det : this.fillTransaction_details) {
				if(!first) {
					sb.append(" ");
				}
				sb.append(det);
				first=false;
			}
			sb.append("}");
		}
		if(this.controlloTraffico>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("rateLimiting:").append(this.controlloTraffico);
		}
		if(this.controlloTraffico_removeThread>=0 || this.controlloTraffico_preparePolicy>=0 || 
				(this.controlloTraffico_policyTimes!=null && !this.controlloTraffico_policyTimes.isEmpty())) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("rateLimitingDetails:{");
			sb.append("del:").append(this.controlloTraffico_removeThread);
			sb.append(" init:").append(this.controlloTraffico_preparePolicy);
			if(this.controlloTraffico_policyTimes!=null && !this.controlloTraffico_policyTimes.isEmpty()) {
				sb.append(" policy:").append(this.controlloTraffico_policyTimes.toString());
			}
			else {
				sb.append(" policy:-");
			}
			sb.append("}");
		}
		if(this.fileTrace>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("fileTrace:").append(this.fileTrace);
		}
		if(this.processTransactionInfo>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("processTransactionInfo:").append(this.processTransactionInfo);
		}
		if(this.getConnection>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("getConnection:").append(this.getConnection);
		}
		if(this.insertTransaction>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insertTransaction:").append(this.insertTransaction);
		}
		if(this.insertDiagnostics>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insertDiagnostics:").append(this.insertDiagnostics);
		}
		if(this.insertTrace>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insertTrace:").append(this.insertTrace);
		}
		if(this.insertContents>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insertContents:").append(this.insertContents);
		}
		if(this.insertResources>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insertResources:").append(this.insertResources);
		}
		if(this.commit>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("commit:").append(this.commit);
		}
		return sb.toString();
	}
}
