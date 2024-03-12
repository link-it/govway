/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.logger.transazioni;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.utils.OpenSPCoopAppenderUtilities;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiLabel;
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
import org.openspcoop2.core.transazioni.utils.TransazioneDaoExt;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.handler.PostOutResponseHandlerGestioneControlloTraffico;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.transazioni.ExceptionSerialzerFileSystem;
import org.openspcoop2.pdd.core.handlers.transazioni.ISalvataggioDiagnosticiManager;
import org.openspcoop2.pdd.core.handlers.transazioni.ISalvataggioTracceManager;
import org.openspcoop2.pdd.core.handlers.transazioni.StatoSalvataggioDiagnostici;
import org.openspcoop2.pdd.core.handlers.transazioni.StatoSalvataggioTracce;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.filetrace.FileTraceConfig;
import org.openspcoop2.pdd.logger.filetrace.FileTraceManager;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.protocol.engine.SecurityTokenUtilities;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.SecurityToken;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.date.UnitaTemporale;
import org.slf4j.Logger;

/**
 * TracciamentoManager
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class TracciamentoManager {

	private boolean transazioniEnabled = true;
	public boolean isTransazioniEnabled() {
		return this.transazioniEnabled;
	}
	
	/**
	 * Database resources
	 */
	private DAOFactory daoFactory = null;
    private ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
    private Logger daoFactoryLoggerTransazioni = null;

	/**
	 * OpenSPCoop2Properties/ConfigurazionePdDManager resources
	 */
	private OpenSPCoop2Properties openspcoopProperties = null;
	public OpenSPCoop2Properties getOpenspcoopProperties() {
		return this.openspcoopProperties;
	}
	private ConfigurazionePdDManager configPdDManager = null;
	
	/**
	 * Indicazione se funzionare in modalita' debug
	 */
	private boolean debug = false;

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
	private ISalvataggioTracceManager salvataggioTracceManager = null;
	private ISalvataggioDiagnosticiManager salvataggioDiagnosticiManager = null;
	private boolean transazioniRegistrazioneDumpHeadersCompactEnabled = false;
	
	private FaseTracciamento fase;
	private boolean usePdDConnection = true;
	private String tipoDatabaseRuntime = null;
	
	/**
	 * Logger
	 */
	private Logger log = null;
	public void logDebug(String msg) {
		if(this.log!=null) {
			this.log.debug(msg);
		}
	}
	public void logDebug(String msg, Exception e) {
		if(this.log!=null) {
			this.log.debug(msg,e);
		}
	}
	public void logWarn(String msg) {
		if(this.log!=null) {
			this.log.warn(msg);
		}
	}
	public void logError(String msg) {
		if(this.log!=null) {
			this.log.error(msg);
		}
	}
	public void logError(String msg, Throwable e) {
		if(this.log!=null) {
			this.log.error(msg,e);
		}
	}
	private Logger logSql = null;
	public void logSqlDebug(String msg) {
		if(this.logSql!=null) {
			this.logSql.debug(msg);
		}
	}
	
	private static final String TIPO_RECOVERY_FS = "__timerFileSystemRecovery";
	
	public TracciamentoManager(FaseTracciamento fase) throws CoreException{
		this.fase = fase;
		
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		this.transazioniEnabled = this.openspcoopProperties.isTransazioniEnabled();
		if(!this.transazioniEnabled) {
			return;
		}
		
		// Debug
		this.debug = this.openspcoopProperties.isTransazioniDebug();
		
		// Log
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(this.debug);
		this.logSql = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSql(this.debug);
		
		this.tipoDatabaseRuntime = this.openspcoopProperties.getDatabaseType();
		if(this.tipoDatabaseRuntime==null){
			throw new CoreException("Tipo Database non definito");
		}
		
		this.configPdDManager = ConfigurazionePdDManager.getInstance();
		
		this.initTransactionResources();
		
		this.usePdDConnection = true;
		
		if(FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)) {
			initTracciamento(this.tipoDatabaseRuntime, this.usePdDConnection);
		}
		
		if(FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)) {
			initDiagnostica(this.tipoDatabaseRuntime, this.usePdDConnection);
			initDump(this.tipoDatabaseRuntime, this.usePdDConnection);
		}
		
		initConfigurazioneTracciamento();
	}
	
	private void initTransactionResources() throws CoreException {
		// DB
		try{
			DAOFactoryProperties daoFactoryProperties = null;
			this.daoFactoryLoggerTransazioni = this.logSql;
			this.daoFactory = DAOFactory.getInstance(this.daoFactoryLoggerTransazioni);
			daoFactoryProperties = DAOFactoryProperties.getInstance(this.daoFactoryLoggerTransazioni);
			this.daoFactoryServiceManagerPropertiesTransazioni = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
			this.daoFactoryServiceManagerPropertiesTransazioni.setShowSql(this.debug);	
			this.daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());

		}catch(Exception e){
			throw new CoreException("Errore durante l'inizializzazione delle risorse per l'accesso al database: "+e.getMessage(),e);
		}
	}
	
	private void initTracciamento(String tipoDatabaseRuntime, boolean usePdDConnection) throws CoreException {
		try{
			
			// Init
			this.tracciamentoOpenSPCoopAppender = new org.openspcoop2.pdd.logger.TracciamentoOpenSPCoopProtocolAppender();
			OpenspcoopAppender tracciamentoOpenSPCoopAppenderEngine = new OpenspcoopAppender();
			tracciamentoOpenSPCoopAppenderEngine.setTipo(TIPO_RECOVERY_FS);
			List<Property> tracciamentoOpenSPCoopAppenderProperties = new ArrayList<>();

			// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
			OpenSPCoopAppenderUtilities.addParameters(this.daoFactoryLoggerTransazioni, tracciamentoOpenSPCoopAppenderProperties, 
					null, // nessun datasource
					null, null, null, null,  // nessuna connection
					tipoDatabaseRuntime,
					usePdDConnection, // viene usata la connessione della PdD 
					this.debug
					);
			OpenSPCoopAppenderUtilities.addCheckProperties(tracciamentoOpenSPCoopAppenderProperties, false);

			tracciamentoOpenSPCoopAppenderEngine.setPropertyList(tracciamentoOpenSPCoopAppenderProperties);
			this.tracciamentoOpenSPCoopAppender.initializeAppender(tracciamentoOpenSPCoopAppenderEngine);
			this.tracciamentoOpenSPCoopAppender.isAlive();
			
		}catch(Exception e){
			throw new CoreException("Errore durante l'inizializzazione del TracciamentoAppender: "+e.getMessage(),e);
		} 
	}
	
	private void initDiagnostica(String tipoDatabaseRuntime, boolean usePdDConnection) throws CoreException {
		try{
			
			// Init
			this.msgDiagnosticiOpenSPCoopAppender = new org.openspcoop2.pdd.logger.MsgDiagnosticoOpenSPCoopProtocolAppender();
			OpenspcoopAppender diagnosticoOpenSPCoopAppenderEngine = new OpenspcoopAppender();
			diagnosticoOpenSPCoopAppenderEngine.setTipo(TIPO_RECOVERY_FS);
			List<Property> diagnosticoOpenSPCoopAppenderProperties = new ArrayList<>();

			// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
			OpenSPCoopAppenderUtilities.addParameters(this.daoFactoryLoggerTransazioni, diagnosticoOpenSPCoopAppenderProperties, 
					null, // nessun datasource
					null, null, null, null,  // nessuna connection
					tipoDatabaseRuntime,
					usePdDConnection, // viene usata la connessione della PdD
					this.debug
					);
			OpenSPCoopAppenderUtilities.addCheckProperties(diagnosticoOpenSPCoopAppenderProperties, false);

			diagnosticoOpenSPCoopAppenderEngine.setPropertyList(diagnosticoOpenSPCoopAppenderProperties);
			this.msgDiagnosticiOpenSPCoopAppender.initializeAppender(diagnosticoOpenSPCoopAppenderEngine);
			this.msgDiagnosticiOpenSPCoopAppender.isAlive();
			
		}catch(Exception e){
			throw new CoreException("Errore durante l'inizializzazione del DiagnosticoAppender: "+e.getMessage(),e);
		} 
	}
	
	private void initDump(String tipoDatabaseRuntime, boolean usePdDConnection) throws CoreException {
		try{
			
			// Init
			this.dumpOpenSPCoopAppender = new org.openspcoop2.pdd.logger.DumpOpenSPCoopProtocolAppender();
			OpenspcoopAppender dumpOpenSPCoopAppenderEngine = new OpenspcoopAppender();
			dumpOpenSPCoopAppenderEngine.setTipo(TIPO_RECOVERY_FS);
			List<Property> dumpOpenSPCoopAppenderProperties = new ArrayList<>();

			// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
			OpenSPCoopAppenderUtilities.addParameters(this.daoFactoryLoggerTransazioni, dumpOpenSPCoopAppenderProperties, 
					null, // nessun datasource
					null, null, null, null,  // nessuna connection
					tipoDatabaseRuntime,
					usePdDConnection, // viene usata la connessione della PdD 
					this.debug
					);
			OpenSPCoopAppenderUtilities.addCheckProperties(dumpOpenSPCoopAppenderProperties, false);

			dumpOpenSPCoopAppenderEngine.setPropertyList(dumpOpenSPCoopAppenderProperties);
			this.dumpOpenSPCoopAppender.initializeAppender(dumpOpenSPCoopAppenderEngine);
			this.dumpOpenSPCoopAppender.isAlive();
			
		}catch(Exception e){
			throw new CoreException("Errore durante l'inizializzazione del DumpAppender: "+e.getMessage(),e);
		} 
	}
	
	private void initConfigurazioneTracciamento() throws CoreException {
		// Configurazione tracce e diagnostici
		try{

			// Indicazione se devo registrare tutte le tracce o solo quelle non ricostruibili
			this.transazioniRegistrazioneTracceProtocolPropertiesEnabled =  this.openspcoopProperties.isTransazioniRegistrazioneTracceProtocolPropertiesEnabled();
			this.transazioniRegistrazioneTracceHeaderRawEnabled = this.openspcoopProperties.isTransazioniRegistrazioneTracceHeaderRawEnabled();
			this.transazioniRegistrazioneTracceDigestEnabled = this.openspcoopProperties.isTransazioniRegistrazioneTracceDigestEnabled();
			
			// Indicazioni sulle modalita' di salvataggio degli header del dump
			this.transazioniRegistrazioneDumpHeadersCompactEnabled = this.openspcoopProperties.isTransazioniRegistrazioneDumpHeadersCompactEnabled();
			
			// salvataggio
			this.salvataggioTracceManager = this.openspcoopProperties.getTransazioniRegistrazioneTracceManager();
			this.salvataggioDiagnosticiManager = this.openspcoopProperties.getTransazioniRegistrazioneDiagnosticiManager();

		} catch (Exception e) {
			throw new CoreException("Errore durante la lettura della configurazione della registrazione di tracce/diagnostici: " + e.getLocalizedMessage(), e);
		}
	}
	
	
	private static final MapKey<String> DISABLE_TRANSACTION_FILTER = Map.newMapKey("DISABLE_TRANSACTION_FILTER");
	private boolean existsTransactionFilter(InformazioniTransazione info) {
		return info!=null && info.getContext()!=null && info.getContext().containsKey(DISABLE_TRANSACTION_FILTER);
	}
	private String getTransactionFilter(InformazioniTransazione info) {
		return (info!=null && info.getContext()!=null) ? (String) info.getContext().get(DISABLE_TRANSACTION_FILTER) : null;
	}
	private void disableTransactionFilter(InformazioniTransazione info) {
		if(!existsTransactionFilter(info) && info!=null && info.getContext()!=null) {
			info.getContext().put(DISABLE_TRANSACTION_FILTER, this.fase.name());
		}
	}
	
	private static final MapKey<String> DISABLE_TRANSACTION_FILTER_DB = Map.newMapKey("DISABLE_TRANSACTION_FILTER_DB");
	private boolean existsTransactionFilterDB(InformazioniTransazione info) {
		return info!=null && info.getContext()!=null && info.getContext().containsKey(DISABLE_TRANSACTION_FILTER_DB);
	}
	private void disableTransactionFilterDB(InformazioniTransazione info) {
		if(!existsTransactionFilter(info) && info!=null && info.getContext()!=null) {
			info.getContext().put(DISABLE_TRANSACTION_FILTER_DB, this.fase.name());
		}
	}
	
	private static final MapKey<String> DISABLE_TRANSACTION_FILTER_FILETRACE = Map.newMapKey("DISABLE_TRANSACTION_FILTER_FILETRACE");
	private boolean existsTransactionFilterFileTrace(InformazioniTransazione info) {
		return info!=null && info.getContext()!=null && info.getContext().containsKey(DISABLE_TRANSACTION_FILTER_FILETRACE);
	}
	private void disableTransactionFilterFileTrace(InformazioniTransazione info) {
		if(!existsTransactionFilter(info) && info!=null && info.getContext()!=null) {
			info.getContext().put(DISABLE_TRANSACTION_FILTER_FILETRACE, this.fase.name());
		}
	}
	
	
	public void invoke(InformazioniTransazione info, String esitoContext, java.util.Map<String, List<String>> headerInUscita,
			org.openspcoop2.pdd.logger.MsgDiagnostico msgdiagErrore) throws HandlerException {
		invokeEngine(info, null, esitoContext, headerInUscita,
				msgdiagErrore);
	}
	public void invoke(InformazioniTransazione info, String esitoContext,
			org.openspcoop2.pdd.logger.MsgDiagnostico msgdiagErrore) throws HandlerException {
		invokeEngine(info, null, esitoContext, null,
				msgdiagErrore);
	}
	public void invoke(InformazioniTransazione info, EsitoTransazione esito, java.util.Map<String, List<String>> headerInUscita,
			org.openspcoop2.pdd.logger.MsgDiagnostico msgdiagErrore) throws HandlerException {
		invokeEngine(info, esito, null, headerInUscita,
				msgdiagErrore);
	}
	public void invoke(InformazioniTransazione info, EsitoTransazione esito) throws HandlerException {
		invokeEngine(info, esito, null, null, null);
	}
	private void invokeEngine(InformazioniTransazione info, EsitoTransazione esito, String esitoContext,
			java.util.Map<String, List<String>> headerInUscita,
			org.openspcoop2.pdd.logger.MsgDiagnostico msgdiagErrore) throws HandlerException {
		
		if(!this.transazioniEnabled) {
			return;
		}
		
		TransazioniProcessTimes times = null;
		long timeStart = -1;
		if(this.openspcoopProperties.isTransazioniRegistrazioneSlowLog()) {
			times = new TransazioniProcessTimes();
			timeStart = DateManager.getTimeMillis();
		}
		try {
			invoke(info, esito, esitoContext, headerInUscita, 
					msgdiagErrore,
					times);
		}
		catch(HandlerException he) {
			he.setIntegrationFunctionError(IntegrationFunctionError.GOVWAY_RESOURCES_NOT_AVAILABLE);
			throw he;
		}
		finally {
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				if(timeProcess>=this.openspcoopProperties.getTransazioniRegistrazioneSlowLogThresholdMs()) {
					StringBuilder sb = new StringBuilder();
					sb.append(timeProcess);
					if(times.idTransazione!=null) {
						sb.append(" <").append(times.idTransazione).append(">");
					}
					sb.append(" ["+this.fase+"]");
					sb.append(" ").append(times.toString());
					String msg = sb.toString();
					OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSlowLog().info(msg);
				}
			}
		}
		
	}
	private void invoke(InformazioniTransazione info, 
			EsitoTransazione esito, String esitoContext, 
			java.util.Map<String, List<String>> headerInUscita,
			org.openspcoop2.pdd.logger.MsgDiagnostico msgdiagErrore,
			TransazioniProcessTimes times) throws HandlerException {

		if (info==null)
			throw new HandlerException("Informazioni transazione is null");
		
		Context context = info.getContext();
		TipoPdD tipoPorta = info.getTipoPorta();
		

		/* ---- Recupero contesto ----- */

		if (context==null)
			throw new HandlerException("Contesto is null");

		if (context.getObject(Costanti.ID_TRANSAZIONE)==null)
			throw new HandlerException("Identificativo della transazione assente");
		String idTransazione = (String) context.getObject(Costanti.ID_TRANSAZIONE);
		if (idTransazione==null)
			throw new HandlerException("Identificativo della transazione assente");
		/**System.out.println("------------- "+this.fase+" ("+idTransazione+")("+context.getTipoPorta().getTipo()+") -------------------");*/
		String prefixIdTransazione = "Transazione ID["+idTransazione+"] ";

		Transaction transaction = null;
		try {
			transaction = FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase) ? TransactionContext.removeTransaction(idTransazione) : TransactionContext.getTransaction(idTransazione);
		}catch(TransactionNotExistsException n) {
			// ignore
		}
		if(times!=null) {
			times.idTransazione = idTransazione;
		}
		
		/* ---- Verifica Configurazione della Transazione per registrazione nello storico ----- */
		
		ConfigurazioneTracciamento configurazioneTracciamento = null;
		boolean dbEnabledBloccante = true;
		boolean dbEnabledNonBloccante = false;
		boolean fileTraceEnabledBloccante = false;
		boolean fileTraceEnabledNonBloccante = false;
		File fileTraceConfig = null;
		boolean fileTraceConfigGlobal = true;
		try{
			if(msgdiagErrore==null) {
				String nomePorta = null;
				if(transaction!=null && transaction.getRequestInfo()!=null && 
						transaction.getRequestInfo().getProtocolContext()!=null &&
						transaction.getRequestInfo().getProtocolContext().getInterfaceName()!=null) {
					nomePorta = transaction.getRequestInfo().getProtocolContext().getInterfaceName();
				}
				RequestInfo requestInfo = transaction.getRequestInfo();
				msgdiagErrore = org.openspcoop2.pdd.logger.MsgDiagnostico.newInstance(info.getTipoPorta(),info.getIdModulo(), nomePorta, requestInfo, this.configPdDManager);
				msgdiagErrore.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
				msgdiagErrore.setPddContext(info.getContext(), info.getProtocolFactory());
			}
			
			configurazioneTracciamento = getConfigurazioneTracciamento(idTransazione, context, tipoPorta, transaction);	
			
			if(configurazioneTracciamento!=null) {
				dbEnabledBloccante = configurazioneTracciamento.isDbEnabled();
				if(dbEnabledBloccante) {
					switch (this.fase) {
					case IN_REQUEST:
						dbEnabledBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoDBRequestInEnabledBloccante();
						dbEnabledNonBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoDBRequestInEnabledNonBloccante();
						break;
					case OUT_REQUEST:
						dbEnabledBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoDBRequestOutEnabledBloccante();
						dbEnabledNonBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoDBRequestOutEnabledNonBloccante();
						break;
					case OUT_RESPONSE:
						dbEnabledBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoDBResponseOutEnabledBloccante();
						dbEnabledNonBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoDBResponseOutEnabledNonBloccante();
						break;
					case POST_OUT_RESPONSE:
						dbEnabledBloccante = configurazioneTracciamento.getRegole()==null || configurazioneTracciamento.getRegole().isTracciamentoDBResponseOutCompleteEnabled();
						break;
					}
				}
				
				fileTraceEnabledBloccante = configurazioneTracciamento.isFileTraceEnabled();
				if(fileTraceEnabledBloccante) {
					fileTraceConfig = configurazioneTracciamento.getFileTraceConfig();
					fileTraceConfigGlobal = configurazioneTracciamento.isFileTraceConfigGlobal();
				}
				if(fileTraceEnabledBloccante) {
					switch (this.fase) {
					case IN_REQUEST:
						fileTraceEnabledBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoFileTraceRequestInEnabledBloccante();
						fileTraceEnabledNonBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoFileTraceRequestInEnabledNonBloccante();
						break;
					case OUT_REQUEST:
						fileTraceEnabledBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoFileTraceRequestOutEnabledBloccante();
						fileTraceEnabledNonBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoFileTraceRequestOutEnabledNonBloccante();
						break;
					case OUT_RESPONSE:
						fileTraceEnabledBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoFileTraceResponseOutEnabledBloccante();
						fileTraceEnabledNonBloccante = configurazioneTracciamento.getRegole()!=null && configurazioneTracciamento.getRegole().isTracciamentoFileTraceResponseOutEnabledNonBloccante();
						break;
					case POST_OUT_RESPONSE:
						fileTraceEnabledBloccante = configurazioneTracciamento.getRegole()==null || configurazioneTracciamento.getRegole().isTracciamentoFileTraceResponseOutCompleteEnabled();
						break;
					}
				}
			}
			
		}catch (Exception e) {
			this.logError(prefixIdTransazione+"errore durante la lettura della configurazione del tracciamento da effettuare: "+e.getMessage(),e);
		}
		
		/* ---- Verifica Esito della Transazione per registrazione nello storico ----- */
		
		boolean exitTransactionAfterRateLimitingRemoveThreadNoTraceDB = false;
		boolean noFileTrace = false;
		if(!dbEnabledBloccante && !dbEnabledNonBloccante
				&&
				!fileTraceEnabledBloccante && !fileTraceEnabledNonBloccante
			) {
			noFileTrace = true;
			exitTransactionAfterRateLimitingRemoveThreadNoTraceDB = true;
		}
		else {
			try{
				boolean forceDisableTransactionFilter = false;
				if(FaseTracciamento.OUT_RESPONSE.equals(this.fase) || FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)) {
					forceDisableTransactionFilter = this.existsTransactionFilter(info);
				}
				else if(FaseTracciamento.IN_REQUEST.equals(this.fase) || FaseTracciamento.OUT_REQUEST.equals(this.fase)){
					this.disableTransactionFilter(info);
				}
				
				if(forceDisableTransactionFilter) {
					if(this.debug){
						this.logDebug("["+idTransazione+"] Vengono registrati tutte le Transazioni indipendentemente dagli esiti poichè è stata precedentente effettuato un tracciamento nella fase "+getTransactionFilter(info)); 
					}
				}
				else {
					StringBuilder bf = new StringBuilder();
					String tipoFiltroEsiti = null;
					boolean filtroEsitiDB = false;
					boolean filtroEsitiFileTrace = false;
					List<String> esitiDaRegistrare = null;
					if(configurazioneTracciamento!=null) {
						esitiDaRegistrare = configurazioneTracciamento.getEsitiDaRegistrare(bf);
						tipoFiltroEsiti = configurazioneTracciamento.getTipoFiltroEsiti();
						filtroEsitiDB = configurazioneTracciamento.isFiltroEsitiDB();
						filtroEsitiFileTrace = configurazioneTracciamento.isFiltroEsitiFileTrace();
					}
					
					if(FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)) {
						if(filtroEsitiDB && this.existsTransactionFilterDB(info)) {
							filtroEsitiDB=false;
						}
						if(filtroEsitiFileTrace && this.existsTransactionFilterFileTrace(info)) {
							filtroEsitiFileTrace=false;
						}
					}
					
					if(esitiDaRegistrare==null || esitiDaRegistrare.isEmpty() || (!filtroEsitiDB && !filtroEsitiFileTrace)){
						esitiDaRegistrare = null;
						if(this.debug){
							this.logDebug("["+idTransazione+"] Vengono registrati tutte le Transazioni indipendentemente dagli esiti"); 
						}
					}
					else{
						if(this.debug){
							this.logDebug("["+idTransazione+"] ("+tipoFiltroEsiti+") Esiti delle Transazioni da registare: "+bf.toString()); 
						}
						if(esito!=null){
							
							boolean esitoDaRegistrare = ConfigurazioneTracciamentoUtils.isEsitoDaRegistrare(this.log, info.getProtocolFactory(), context, 
									esitiDaRegistrare, esito);
							
							if(!esitoDaRegistrare){
								String msg = prefixIdTransazione+"non salvata nello storico "+tipoFiltroEsiti+" come richiesto dalla configurazione del tracciamento: "+
										ConfigurazioneTracciamentoUtils.getEsitoTransazionDetail(this.log, info.getProtocolFactory(),esito);
								if(ConfigurazioneTracciamentoUtils.isEsitoOk(this.log, info.getProtocolFactory(),esito)) {
									this.logWarn(msg);
								}
								else{
									this.logError(msg);
								}
								
								// BUG OP-825
								// la gestione RateLimiting deve essere sempre fatta senno se si configurara di non registrare una transazione, poi si ha l'effetto che i contatori del Controllo del Traffico non vengono diminuiti.
								if(filtroEsitiDB) {
									exitTransactionAfterRateLimitingRemoveThreadNoTraceDB = true;
								}
								else if(FaseTracciamento.OUT_RESPONSE.equals(this.fase) &&
									(dbEnabledBloccante || dbEnabledNonBloccante)) {
									this.disableTransactionFilterDB(info);
								}
								
								if(filtroEsitiFileTrace) {
									noFileTrace = true;
								}
								else if(FaseTracciamento.OUT_RESPONSE.equals(this.fase) &&
										(fileTraceEnabledBloccante || fileTraceEnabledNonBloccante)) {
									this.disableTransactionFilterFileTrace(info);
								}
							}
							else{
								this.disableTransactionFilter(info);
							}
						}
						else{
							this.logError(prefixIdTransazione+"senza un esito");
						}
					}
				}
			}catch (Exception e) {
				this.logError(prefixIdTransazione+"errore durante la lettura della configurazione del tracciamento da effettuare rispetto agli esiti: "+e.getMessage(),e);
			}
			
			if(!exitTransactionAfterRateLimitingRemoveThreadNoTraceDB && !dbEnabledBloccante && !dbEnabledNonBloccante) {
				exitTransactionAfterRateLimitingRemoveThreadNoTraceDB = true;
			}
			if(!noFileTrace && !fileTraceEnabledBloccante && !fileTraceEnabledNonBloccante) {
				noFileTrace = true;
			}
			
		}
		
		
		
		
		

			
		/* ---- Recupero dati della transazione dal contesto ----- */
		
		Boolean controlloCongestioneMaxRequestThreadRegistrato = null;
		if(FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase) && this.openspcoopProperties.isControlloTrafficoEnabled()){
			Object objControlloCongestioneMaxRequestThreadRegistrato = context.getObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_THREAD_REGISTRATO);
			if(objControlloCongestioneMaxRequestThreadRegistrato!=null){
				controlloCongestioneMaxRequestThreadRegistrato = (Boolean) objControlloCongestioneMaxRequestThreadRegistrato;
				/**System.out.println("CHECK POST OUT ["+context.getTipoPorta().name()+"] controlloCongestioneMaxRequestViolated["+controlloCongestioneMaxRequestViolated+"] controllo["+PddInterceptorConfig.isControlloCongestioneTraceTransazioneMaxThreadsViolated()+"]");*/
			}		
		}
		
		// Check Transaction
		if (transaction==null)
			throw new HandlerException("Dati della transazione assenti");
		if(FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)) {
			transaction.setDeleted();
		}


		Transazione transazioneDTO = null;
		HandlerException fileTraceException = null;
		try{
			
			RequestInfo requestInfo = null;
			if(context!=null && context.containsKey(Costanti.REQUEST_INFO)){
				requestInfo = (RequestInfo) context.getObject(Costanti.REQUEST_INFO);
			}
			
			IDSoggetto idDominio = this.openspcoopProperties.getIdentitaPortaDefault(info.getProtocolFactory().getProtocol(), requestInfo); 
			if(info.getProtocollo()!=null && info.getProtocollo().getDominio()!=null && !info.getProtocollo().getDominio().equals(idDominio)){
				idDominio = info.getProtocollo().getDominio();
			}
			else if(requestInfo!=null &&
				requestInfo.getIdentitaPdD()!=null && !requestInfo.getIdentitaPdD().equals(idDominio)){
				idDominio = requestInfo.getIdentitaPdD();
			}
	
			String modulo = "Tracciamento-"+this.fase;
	
			ExceptionSerialzerFileSystem exceptionSerializerFileSystem = new ExceptionSerialzerFileSystem(this.log);
	
			
			// ### Lettura dati Transazione ###
			
			boolean pddStateless = true;
			TransazioneUtilities transazioneUtilities = null;
			HandlerException he = null;
			long timeStart = -1;
			try{
				if(exitTransactionAfterRateLimitingRemoveThreadNoTraceDB && noFileTrace) {
					return; // entro nel finally e se sono post out rimuovo il thread per il controllo del traffico
				}
				
				if(times!=null) {
					timeStart = DateManager.getTimeMillis();
				}
				
				// Stateless
/**				if ( (context.getIntegrazione()==null) ||
//						(context.getIntegrazione().isGestioneStateless()==null) ||
//						(context.getIntegrazione().isGestioneStateless()==false) 
//						){
//					pddStateless = false;
//				}*/
				// Cambio l'approccio per poter simulare anche gli errori nei diagnostici dove possibile
				// Tanto tutte le comunicazioni sono stateless a meno che non vengano tramutate in stateful
				if ( info.getIntegrazione()!=null && 
						info.getIntegrazione().isGestioneStateless()!=null &&
								!info.getIntegrazione().isGestioneStateless().booleanValue()){
					pddStateless = false;
				}
				
				/* ---- Salvo informazioni sulla transazioni nell'oggetto transazioniDTO ----- */
				transazioneUtilities = new TransazioneUtilities(this.log, this.openspcoopProperties,
						this.transazioniRegistrazioneTracceHeaderRawEnabled,
						this.transazioniRegistrazioneTracceDigestEnabled,
						this.transazioniRegistrazioneTracceProtocolPropertiesEnabled,
						configurazioneTracciamento!=null ? configurazioneTracciamento.getInformazioniSalvareTransazioni() : null);
				transazioneDTO = transazioneUtilities.fillTransaction(info, transaction, idDominio,
						( (times!=null && this.openspcoopProperties.isTransazioniRegistrazioneSlowLogBuildTransactionDetails()) ? times : null),
						this.fase, 
						esito, esitoContext, 
						info.getTransazioneDaAggiornare()); // NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
				if(FaseTracciamento.OUT_RESPONSE.equals(this.fase)) {
					info.setTransazioneDaAggiornare(transazioneDTO);
				}
	
			}catch (Throwable e) {
				try{
					if(FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)) {
						exceptionSerializerFileSystem.registrazioneFileSystemDiagnosticiTracceDumpEmessiPdD(transaction, idTransazione, null,
								true, true, true, true);
					}
				} catch (Exception eClose) {
					// ignore
				}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura della transazione sul database (Lettura dati Transazione): " + e.getLocalizedMessage();
				this.logError("["+idTransazione+"] "+msg,e);
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
				if(FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase) && this.openspcoopProperties.isControlloTrafficoEnabled()){
					
					try {
						if(times!=null) {
							timeStart = DateManager.getTimeMillis();
						}
						PostOutResponseHandlerGestioneControlloTraffico outHandler = new PostOutResponseHandlerGestioneControlloTraffico();
						outHandler.process(controlloCongestioneMaxRequestThreadRegistrato, this.log, idTransazione, transazioneDTO, info, esito,
								( (times!=null && this.openspcoopProperties.isTransazioniRegistrazioneSlowLogRateLimitingDetails()) ? times : null));
					}catch (Throwable e) {
						this.logError("["+idTransazione+"] Errore durante la registrazione di terminazione del thread: "+e.getMessage(),e);
					}finally {
						if(times!=null) {
							long timeEnd =  DateManager.getTimeMillis();
							long timeProcess = timeEnd-timeStart;
							times.controlloTraffico = timeProcess;
						}
					}
				}
				
				// ### FileTrace ###
				if((fileTraceEnabledBloccante || fileTraceEnabledNonBloccante) && !noFileTrace) {
					
					InformazioniToken informazioniToken = null;
					InformazioniAttributi informazioniAttributi = null;
					InformazioniNegoziazioneToken informazioniNegoziazioneToken = null;
					SecurityToken securityToken = null;
					if(transaction!=null) {
						informazioniToken = transaction.getInformazioniToken();
						informazioniNegoziazioneToken = transaction.getInformazioniNegoziazioneToken();
					}
					if(context!=null) {
						Object oInformazioniAttributiNormalizzati = context.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_ATTRIBUTI_INFORMAZIONI_NORMALIZZATE);
						if(oInformazioniAttributiNormalizzati instanceof InformazioniAttributi) {
							informazioniAttributi = (InformazioniAttributi) oInformazioniAttributiNormalizzati;
						}
						securityToken = SecurityTokenUtilities.readSecurityToken(context);
					}
					
					fileTraceException = logWithFileTrace(fileTraceConfig, fileTraceConfigGlobal,
							times,
							transazioneDTO, transaction, esito,
							informazioniToken,
							informazioniAttributi,
							informazioniNegoziazioneToken,
							securityToken,
							info,
							headerInUscita,
							requestInfo,
							idTransazione,
							fileTraceEnabledBloccante,
							msgdiagErrore); // anche qua vi e' un try catch con Throwable
				}
				
			}
			
			if(he!=null) {
				throw he;
			}
			else if(exitTransactionAfterRateLimitingRemoveThreadNoTraceDB){
				// La risorsa viene rilasciata nel finally
				/**this.releaseResources(transaction, idTransazione, context);*/
				
				// se non devo tracciare su db ma ho avuto un errore su file trace devo registrarlo
				if(fileTraceException!=null) {
					throw fileTraceException;
				}
				
				return;
			}
			
			
			
			// Il controllo stateful è stato spostato sotto il blocco soprastante, per assicurare la gestione del traffico (decremento dei contatori)
			
			// NOTA: se l'integrazione e' null o l'indicazione se la gestione stateless e' null, significa che la PdD non e' ancora riuscita
			// a capire che tipo di gestione deve adottare. Queste transazioni devono essere sempre registrate perche' riguardano cooperazioni andate in errore all'inizio,
			// es. Porta Delegata non esistente, busta malformata....
			if(info.getIntegrazione()!=null && 
					info.getIntegrazione().isGestioneStateless()!=null &&
					!info.getIntegrazione().isGestioneStateless().booleanValue() &&
				!this.openspcoopProperties.isTransazioniStatefulEnabled()){
				/**if(this.debug)*/
				this.logError("["+idTransazione+"] Transazione non registrata, gestione stateful non abilitata");
				// NOTA: Da fare thread che ripulisce header di trasporto o dump messaggi non associati a transazioni.
				// La risorsa viene rilasciata nel finally
				/**this._releaseResources(transaction, idTransazione, context);*/
				
				// se non devo tracciare su db ma ho avuto un errore su file trace devo registrarlo
				if(fileTraceException!=null) {
					throw fileTraceException;
				}
				
				return;
			}
			
			
			
			
			
	
			
			
			// ### Gestione Transazione ###
	
			boolean autoCommit = true;
			DBTransazioniManager dbManager = null;
	    	Resource resource = null;
			Connection connection = null;
			boolean errore = false;
			boolean registraTracciaRichiesta = (FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase) && info.getTransazioneDaAggiornare()==null)
					||
					(FaseTracciamento.OUT_RESPONSE.equals(this.fase));
			boolean registraTracciaRichiestaInfoTransazione = false; 
			boolean registraTracciaRisposta = (FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase) && info.getTransazioneDaAggiornare()==null)
					||
					(FaseTracciamento.OUT_RESPONSE.equals(this.fase));
			boolean registraTracciaRispostaInfoTransazione = false; 
			boolean registrazioneMessaggiDiagnostici = true;
			boolean registrazioneDumpMessaggi = true;
			boolean insertTransazione = false;
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
					if( 
							registraTracciaRichiesta
							&& 
							this.salvataggioTracceManager!=null) {
						
						registraTracciaRichiestaInfoTransazione = true;
						
						StatoSalvataggioTracce statoTracciaRichiesta =  
								this.salvataggioTracceManager.getInformazioniSalvataggioTracciaRichiesta(this.log, info, transaction, transazioneDTO, pddStateless);
						if(statoTracciaRichiesta!=null) {
							registraTracciaRichiesta = (!statoTracciaRichiesta.isCompresso());
							informazioneTracciaRichiestaDaSalvare = statoTracciaRichiesta.getInformazioneCompressa();
						}
						if(this.debug){
							this.logDebug("["+idTransazione+"] Emissione traccia richiesta: "+registraTracciaRichiesta);
							if(statoTracciaRichiesta!=null) {
								this.logDebug("["+idTransazione+"] Informazioni Salvataggio traccia richiesta (compresso:"+statoTracciaRichiesta.isCompresso()+
										" errore:"+statoTracciaRichiesta.isErrore()+"): "+statoTracciaRichiesta.getInformazione());
							}
						}
						else{
							if(statoTracciaRichiesta!=null && statoTracciaRichiesta.isErrore()){
									this.logWarn("["+idTransazione+"] Informazioni Salvataggio traccia richiesta in errore: "+statoTracciaRichiesta.getInformazione());
							}
						}
					}
					
					// TRACCIA RISPOSTA
					if( 
							registraTracciaRisposta
							&& 
							this.salvataggioTracceManager!=null) {
						
						registraTracciaRispostaInfoTransazione = true;
						
						StatoSalvataggioTracce statoTracciaRisposta =  
								this.salvataggioTracceManager.getInformazioniSalvataggioTracciaRisposta(this.log, info, transaction, transazioneDTO, pddStateless);
						if(statoTracciaRisposta!=null) {
							registraTracciaRisposta = (!statoTracciaRisposta.isCompresso());
							informazioneTracciaRispostaDaSalvare = statoTracciaRisposta.getInformazioneCompressa();
						}
						if(this.debug){
							this.logDebug("["+idTransazione+"] Emissione traccia risposta: "+registraTracciaRisposta);
							if(statoTracciaRisposta!=null) {
								this.logDebug("["+idTransazione+"] Informazioni Salvataggio traccia risposta (compresso:"+statoTracciaRisposta.isCompresso()+
										" errore:"+statoTracciaRisposta.isErrore()+"): "+statoTracciaRisposta.getInformazione());
							}
						}
						else{
							if(statoTracciaRisposta!=null && statoTracciaRisposta.isErrore()){
									this.logWarn("["+idTransazione+"] Informazioni Salvataggio traccia risposta in errore: "+statoTracciaRisposta.getInformazione());
							}
						}
					}
		
					// MESSAGGI DIAGNOSTICI
					if(this.salvataggioDiagnosticiManager!=null) {
						StatoSalvataggioDiagnostici statoDiagnostici =  
								this.salvataggioDiagnosticiManager.getInformazioniSalvataggioDiagnostici(this.log, info, transaction, transazioneDTO, pddStateless);
						if(statoDiagnostici!=null) {
							registrazioneMessaggiDiagnostici = (!statoDiagnostici.isCompresso());
							informazioniDiagnosticiDaSalvare = statoDiagnostici.getInformazioneCompressa();
						}
						if(this.debug){
							this.logDebug("["+idTransazione+"] Emissione diagnostici: "+registrazioneMessaggiDiagnostici);
							if(statoDiagnostici!=null) {
								this.logDebug("["+idTransazione+"] Informazioni Salvataggio diagnostici (compresso:"+statoDiagnostici.isCompresso()+
										" errore :"+statoDiagnostici.isErrore()+"): "+statoDiagnostici.getInformazione());
							}
						}
						else{
							if(statoDiagnostici!=null && statoDiagnostici.isErrore()){
									this.logWarn("["+idTransazione+"] Informazioni Salvataggio diagnostici in errore: "+statoDiagnostici.getInformazione());
							}
						}
						if(registrazioneMessaggiDiagnostici && !FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)) {
							registrazioneMessaggiDiagnostici = false; // con salvataggioDiagnosticiManager si scrivono nella tabella msgdiagnostici solo in fondo
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
	
				
				// DUMP
				
				boolean registrazioneContenuti = false;
				for(int i=0; i<transaction.sizeMessaggi(); i++){
					Messaggio messaggio = transaction.getMessaggio(i);
					if(!messaggio.isStored()) {
						registrazioneContenuti=true;
						break;
					}
				}
				
				// CONTENUTI
				boolean registrazioneRisorse = transaction.getTransactionServiceLibrary()!=null || (transaction.getRisorse()!=null && !transaction.getRisorse().isEmpty()); /**transaction.sizeMessaggi()>0;*/
				
	
				// AUTO-COMMIT
				if(registraTracciaRichiesta || registraTracciaRisposta || registrazioneMessaggiDiagnostici ||  registrazioneContenuti || registrazioneRisorse){
					autoCommit = false; // devo registrare piu' informazioni oltre alla transazione
				}
				if(this.debug)
					this.logSqlDebug("["+idTransazione+"] AutoCommit: "+this.debug);
	
	
				
				
				
				
				
				// Dopo aver assegnato tutti i valori all'oggetto transazione, in modo da poterlo salvare su filesystem
				if(
						(FaseTracciamento.OUT_RESPONSE.equals(this.fase) && this.openspcoopProperties.isTransazioniTracciamentoDBOutResponseThrowRequestException() )
						||
						(FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase) && this.openspcoopProperties.isTransazioniTracciamentoDBPostOutResponseThrowRequestException() )
						) {
					throwTrackingExceptionIfExists(info);
				}
				
				
				
				
	
	
	
				/* ---- Connection/Service Manager ----- */
	
				if(this.debug)
					this.logSqlDebug("["+idTransazione+"] recupero jdbcServiceManager in corso ...");

				
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
					throw new CoreException("Risorsa al database non disponibile");
				}
				connection = (Connection) resource.getResource();
				if(connection == null)
					throw new CoreException("Connessione non disponibile");	

				if(!autoCommit){
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
					this.logSqlDebug("["+idTransazione+"] recupero jdbcServiceManager effettuato");
	
				/**System.out.println("\n\n\n****** ["+this.fase+"]["+transazioneDTO.getIdTransazione()+"] (autoCommit:"+autoCommit+") ["+transazioneDTO.getPddRuolo()+"]["+transazioneDTO.getPddCodice()+"]["+transazioneDTO.getNomeServizio()+"] *******");*/
				
	

	
				/* ---- Inserimento dati transazione ----- */
	
				// Inserisco transazione
				if(this.debug)
					this.logDebug("["+idTransazione+"] inserimento transazione in corso ...");
				
				try {
					if(times!=null) {
						timeStart = DateManager.getTimeMillis();
					}
					insertTransazione = registraTransazione(transazioneDTO,
							transazioneService, info,
							autoCommit, registraTracciaRichiestaInfoTransazione, registraTracciaRispostaInfoTransazione);
				}finally {
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.insertTransaction = timeProcess;
					}
				}
				if(this.debug)
					this.logDebug("["+idTransazione+"] inserita transazione");
	

	
	
	
				/* ---- Inserimento dati tracce ----- */
				
				List<Traccia> tracceSalvate = new ArrayList<>();
				try {
					timeStart = -1;
					
					if( 
							!FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase) 
							&&
							(
									(registraTracciaRichiesta && transaction.getTracciaRichiesta()!=null)
									||
									(registraTracciaRisposta && transaction.getTracciaRisposta()!=null)
							)
						){
						initTracciamento(this.tipoDatabaseRuntime, this.usePdDConnection);
					}
					
					if( registraTracciaRichiesta && transaction.getTracciaRichiesta()!=null){
						if(times!=null) {
							timeStart = DateManager.getTimeMillis();
						}
						if(transaction.getTracciaRichiesta().isStored()) {
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione traccia richiesta skipped");
						}
						else {
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione traccia richiesta...");
							/**System.out.println("["+this.fase+"]["+transazioneDTO.getIdTransazione()+"]["+transazioneDTO.getPddRuolo()+"]["+transazioneDTO.getPddCodice()+"]["+transazioneDTO.getNomeServizio()+"] INSERT TRACCIA RICHIESTA");*/
							this.tracciamentoOpenSPCoopAppender.log(connection, transaction.getTracciaRichiesta());
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione traccia richiesta completata");
							if(autoCommit) {
								transaction.getTracciaRichiesta().setStored(true);
							}
							else {
								tracceSalvate.add(transaction.getTracciaRichiesta());
							}
						}
					}	
					if( registraTracciaRisposta && transaction.getTracciaRisposta()!=null){
						if(times!=null && timeStart==-1) {
							timeStart = DateManager.getTimeMillis();
						}
						if(transaction.getTracciaRisposta().isStored()) {
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione traccia richiesta skipped");
						}
						else {
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione traccia risposta...");
							/**System.out.println("["+this.fase+"]["+transazioneDTO.getIdTransazione()+"]["+transazioneDTO.getPddRuolo()+"]["+transazioneDTO.getPddCodice()+"]["+transazioneDTO.getNomeServizio()+"] INSERT TRACCIA RISPOSTA");*/
							this.tracciamentoOpenSPCoopAppender.log(connection, transaction.getTracciaRisposta());
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione traccia risposta completata");
							if(autoCommit) {
								transaction.getTracciaRisposta().setStored(true);
							}
							else {
								tracceSalvate.add(transaction.getTracciaRisposta());
							}
						}
					}	
				}finally {
					if(times!=null && timeStart>0) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.insertTrace = timeProcess;
					}
				}
	
	

	
	
				/* ---- Inserimento messaggi diagnostici ----- */
				
				List<MsgDiagnostico> diagnosticiSalvati = new ArrayList<>();
				try {
					timeStart = -1;
					if(registrazioneMessaggiDiagnostici){
						
						if(!FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)) {
							initDiagnostica(this.tipoDatabaseRuntime, this.usePdDConnection);
						}
						
						if(times!=null && transaction!=null && transaction.sizeMsgDiagnostici()>0) {
							timeStart = DateManager.getTimeMillis();
						}
						for(int i=0; i<transaction.sizeMsgDiagnostici(); i++){
							MsgDiagnostico msgDiagnostico = transaction.getMsgDiagnostico(i);
							if(msgDiagnostico.isStored()) {
								if(this.debug)
									this.logDebug("["+idTransazione+"] registrazione diagnostico con codice ["+msgDiagnostico.getCodice()+"] skipped");
								continue;
							}
							if(msgDiagnostico.getIdSoggetto()==null){
								msgDiagnostico.setIdSoggetto(idDominio);
							}
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione diagnostico con codice ["+msgDiagnostico.getCodice()+"] ...");
							/**System.out.println("["+this.fase+"]["+transazioneDTO.getIdTransazione()+"]["+transazioneDTO.getPddRuolo()+"]["+transazioneDTO.getPddCodice()+"]["+transazioneDTO.getNomeServizio()+"] INSERT DIAG ["+msgDiagnostico.getCodice()+"]");*/
							this.msgDiagnosticiOpenSPCoopAppender.log(connection,msgDiagnostico);
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione diagnostico con codice ["+msgDiagnostico.getCodice()+"] completata");
							if(autoCommit) {
								msgDiagnostico.setStored(true);
							}
							else {
								diagnosticiSalvati.add(msgDiagnostico);
							}
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
				
				List<Messaggio> messaggiSalvati = new ArrayList<>();
				try {
					timeStart = -1;
					if(times!=null && transaction!=null && transaction.sizeMessaggi()>0) {
						timeStart = DateManager.getTimeMillis();
					}
					for(int i=0; i<transaction.sizeMessaggi(); i++){
						Messaggio messaggio = transaction.getMessaggio(i);
						if(messaggio.isStored()) {
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione messaggio di tipo ["+messaggio.getTipoMessaggio()+"] skipped");
							continue;
						}
						try {
							if(!FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)) {
								initDump(this.tipoDatabaseRuntime, this.usePdDConnection);
							}							
							
							if(messaggio.getProtocollo()==null) {
								messaggio.setProtocollo(transazioneDTO.getProtocollo());
							}
							if(messaggio.getDominio()==null) {
								messaggio.setDominio(idDominio);
							}
							if(messaggio.getTipoPdD()==null) {
								messaggio.setTipoPdD(info.getTipoPorta());
							}
							if(messaggio.getIdFunzione()==null) {
								messaggio.setIdFunzione(modulo);
							}
							if(messaggio.getIdBusta()==null &&
								info.getProtocollo()!=null) {
								messaggio.setIdBusta(info.getProtocollo().getIdRichiesta());
							}
							if(messaggio.getFruitore()==null &&
								info.getProtocollo()!=null) {
								messaggio.setFruitore(info.getProtocollo().getFruitore());
							}
							if(messaggio.getServizio()==null &&
								info.getProtocollo()!=null) {
								IDServizio idServizio = IDServizioFactory.getInstance().
										getIDServizioFromValuesWithoutCheck(info.getProtocollo().getTipoServizio(), 
												info.getProtocollo().getServizio(), 
												info.getProtocollo().getErogatore()!=null ? info.getProtocollo().getErogatore().getTipo() : null, 
												info.getProtocollo().getErogatore()!=null ? info.getProtocollo().getErogatore().getNome() : null,
												info.getProtocollo().getVersioneServizio()!=null ? info.getProtocollo().getVersioneServizio() : -1);
								messaggio.setServizio(idServizio);
							}
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione messaggio di tipo ["+messaggio.getTipoMessaggio()+"] ...");
							/**System.out.println("["+this.fase+"]["+transazioneDTO.getIdTransazione()+"]["+transazioneDTO.getPddRuolo()+"]["+transazioneDTO.getPddCodice()+"]["+transazioneDTO.getNomeServizio()+"] INSERT MESSAGGIO ["+messaggio.getTipoMessaggio()+"]");*/
							this.dumpOpenSPCoopAppender.dump(connection,messaggio,this.transazioniRegistrazioneDumpHeadersCompactEnabled);
							if(this.debug)
								this.logDebug("["+idTransazione+"] registrazione messaggio di tipo ["+messaggio.getTipoMessaggio()+"] completata");
							if(autoCommit) {
								messaggio.setStored(true);
							}
							else {
								messaggiSalvati.add(messaggio);
							}
						}finally {
							try {
								if(messaggio.getBody()!=null) {
									messaggio.getBody().unlock();
									messaggio.getBody().clearResources();
								}
							}catch(Exception t){
								this.logError("["+idTransazione+"] errore durante il rilascio delle risorse del messaggio: "+t.getMessage(),t);
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
					/**System.out.println("["+this.fase+"]["+transazioneDTO.getIdTransazione()+"]["+transazioneDTO.getPddRuolo()+"]["+transazioneDTO.getPddCodice()+"]["+transazioneDTO.getNomeServizio()+"] RISORSE");*/
					try {
						if(times!=null) {
							timeStart = DateManager.getTimeMillis();
						}
						IDumpMessaggioService dumpMessageService = jdbcServiceManager.getDumpMessaggioService();
						ContenutiUtilities contenutiUtilities = new ContenutiUtilities(this.log);
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
	
				
				String hdrTest = this.openspcoopProperties.getTransazioniTestsuiteManuallyFaultHeaderDBBeforeCommit();
				if(hdrTest!=null && requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getHeaderFirstValue(hdrTest)!=null && 
						this.fase.name().equals(requestInfo.getProtocolContext().getHeaderFirstValue(hdrTest))) {
					throw new CoreException("Test Manually Exception generated in phase '"+this.fase+"'");
				}
				
	
				// COMMIT
				if(!autoCommit) {
					try {
						if(times!=null) {
							timeStart = DateManager.getTimeMillis();
						}
						connection.commit();
						
						if(!tracceSalvate.isEmpty()) {
							for (Traccia traccia : tracceSalvate) {
								traccia.setStored(true);
							}
						}
						if(!diagnosticiSalvati.isEmpty()) {
							for (MsgDiagnostico msgDiagnostico : diagnosticiSalvati) {
								msgDiagnostico.setStored(true);
							}
						}
						if(!messaggiSalvati.isEmpty()) {
							for (Messaggio messaggio : messaggiSalvati) {
								messaggio.setStored(true);
							}
						}
						
					}finally {
						if(times!=null) {
							long timeEnd =  DateManager.getTimeMillis();
							long timeProcess = timeEnd-timeStart;
							times.commit = timeProcess;
						}
					}
				}
				
				
				// DOPO COMMIT RIUSCITO
				if(!autoCommit && insertTransazione &&
					!FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)){
					addTransactionAlreadyRegistered(info);
				}
	
	
			} catch (SQLException sqlEx) {
				errore = true;
				try{
					if(!autoCommit &&
						connection!=null) {
						connection.rollback();
					}
				}catch(Exception eRollback){
					// ignore
				}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura della transazione sul database (sql): " + sqlEx.getLocalizedMessage();
				this.logError("["+idTransazione+"] "+msg,sqlEx);
				if(!FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)){
					logRegistrazioneNonRiuscita(msgdiagErrore, false, sqlEx, idTransazione, 
							null, null);
				}
				HandlerException errorDB = new HandlerException(msg,sqlEx); 
				addTrackingException(info, errorDB);
				if(dbEnabledBloccante) {
					throw errorDB;
				}
			}  catch (Exception e) {
				errore = true;
				try{
					if(!autoCommit &&
						connection!=null) {
						connection.rollback();
					}
				}catch(Exception eRollback){
					// ignore
				}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura della transazione sul database: " + e.getLocalizedMessage();
				this.logError("["+idTransazione+"] "+msg,e);
				if(!FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)){
					logRegistrazioneNonRiuscita(msgdiagErrore, false, e, idTransazione, 
							null, null);
				}
				HandlerException errorDB = new HandlerException(msg,e); 
				addTrackingException(info, errorDB);
				if(dbEnabledBloccante) {
					throw errorDB;
				}
			} finally {
				
				// Ripristino Autocomit
				try {
					if(!autoCommit &&
						connection!=null) {
						connection.setAutoCommit(true);
					}
				} catch (Exception e) {
					// ignore
				}
									
				// Chiusura della connessione al database
				try {
					if(resource!=null)
						dbManager.releaseResource(idDominio, modulo, resource);
				} catch (Exception e) {
					// ignore
				}
				
				// Registrazione su FileSystem informazioni se la gestione e' andata in errore
				if(errore && FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)){
					
					try {
						exceptionSerializerFileSystem.registrazioneFileSystemDiagnosticiTracceDumpEmessiPdD(transaction, idTransazione, transazioneDTO,
								registraTracciaRichiesta, registraTracciaRisposta, registrazioneMessaggiDiagnostici, registrazioneDumpMessaggi);
					} catch (Exception e) {
						// ignore
					}
					try {
						exceptionSerializerFileSystem.registrazioneFileSystem(transazioneDTO, idTransazione);
					} catch (Exception e) {
						// ignore
					}
				}
								
			}
						
		}finally{

			this.releaseResourcesEngine(transaction, idTransazione, info);
						
		}
		
		
		if(fileTraceException!=null) {
			throw fileTraceException;
		}
	}

	private ConfigurazioneTracciamento getConfigurazioneTracciamento(String idTransazione, Context context, TipoPdD tipoPorta, Transaction transaction){
		
		if(context!=null) {
			// nop
		}
		
		ConfigurazioneTracciamento configurazioneTracciamento = null;
		try {
			if(transaction!=null && transaction.getRequestInfo()!=null && 
					transaction.getRequestInfo().getProtocolContext()!=null &&
					transaction.getRequestInfo().getProtocolContext().getInterfaceName()!=null) {
				switch (tipoPorta) {
				case DELEGATA:
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(transaction.getRequestInfo().getProtocolContext().getInterfaceName());
					PortaDelegata pd = this.configPdDManager.getPortaDelegataSafeMethod(idPD, transaction.getRequestInfo());
					configurazioneTracciamento = new ConfigurazioneTracciamento(this.log, this.configPdDManager, pd);
					break;
				case APPLICATIVA:
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(transaction.getRequestInfo().getProtocolContext().getInterfaceName());
					PortaApplicativa pa = this.configPdDManager.getPortaApplicativaSafeMethod(idPA, transaction.getRequestInfo());
					configurazioneTracciamento = new ConfigurazioneTracciamento(this.log, this.configPdDManager, pa);
					break;
				default:
					break;
				}
			}
			if(configurazioneTracciamento==null) {
				configurazioneTracciamento = new ConfigurazioneTracciamento(this.log, this.configPdDManager,tipoPorta);
			}
		}catch(Exception e) {
			this.logDebug("["+idTransazione+"] Errore avvenuto durante la lettura della configurazione del tracciamento: "+e.getMessage(),e); 
		}
		return configurazioneTracciamento;
	}
	
	private void releaseResourcesEngine(Transaction transactionParam, String idTransazione, InformazioniTransazione info) {
		
		if(!FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)) {
			return;
		}
		
		if(transactionParam!=null) {
			// nop
		}
		
		Transaction transaction = null;
		try {
			transaction = TransactionContext.removeTransaction(idTransazione);
			if(transaction!=null) {
				transaction.setDeleted();
			}
			// altrimenti e' gia' stata eliminata
		} catch (Exception e) {
			this.logError("["+idTransazione+"] Errore durante la rimozione della registrazione delle transazione",e);
		}
			
		/* ---- Elimino informazione per filtro duplicati ----- */
		if(info!=null && info.getProtocollo()!=null){
			// Aggiunto check Applicativa e Delegata per evitare che comunicazioni dove i due domini sono entrambi gestiti sul solito GovWay si eliminano a vicenda gli id.
			if(TipoPdD.APPLICATIVA.equals(info.getTipoPorta()) && info.getProtocollo().getIdRichiesta()!=null){
				try {
					TransactionContext.removeIdentificativoProtocollo(info.getProtocollo().getIdRichiesta());
				} catch (Exception e) {
					this.logError("["+idTransazione+"] Errore durante la rimozione della registrazione dell'identificativo di protocollo della richiesta ["+info.getProtocollo().getIdRichiesta()+"]",e);
				}
			}
			if(TipoPdD.DELEGATA.equals(info.getTipoPorta()) && info.getProtocollo().getIdRisposta()!=null){
				try {
					TransactionContext.removeIdentificativoProtocollo(info.getProtocollo().getIdRisposta());
				} catch (Exception e) {
					this.logError("["+idTransazione+"] Errore durante la rimozione della registrazione dell'identificativo di protocollo della risposta ["+info.getProtocollo().getIdRisposta()+"]",e);
				}
			}
		}
		
	}
	
	private HandlerException logWithFileTrace(File fileTraceConfig, boolean fileTraceConfigGlobal,
			TransazioniProcessTimes times,
			Transazione transazioneDTO, Transaction transaction, EsitoTransazione esito,
			InformazioniToken informazioniToken,
			InformazioniAttributi informazioniAttributi,
			InformazioniNegoziazioneToken informazioniNegoziazioneToken,
			SecurityToken securityToken,
			InformazioniTransazione info,
			java.util.Map<String, List<String>> headerInUscita,
			RequestInfo requestInfo,
			String idTransazione,
			boolean fileTraceEnabledBloccante,
			org.openspcoop2.pdd.logger.MsgDiagnostico msgdiagErrore) {
		FileTraceManager fileTraceManager = null;
		long timeStart = -1;
		try {
			if(times!=null) {
				timeStart = DateManager.getTimeMillis();
			}
			FileTraceConfig config = FileTraceConfig.getConfig(fileTraceConfig, fileTraceConfigGlobal);
			fileTraceManager = new FileTraceManager(this.log, config);
			fileTraceManager.buildTransazioneInfo(info.getProtocolFactory(), transazioneDTO, transaction, 
					informazioniToken,
					informazioniAttributi,
					informazioniNegoziazioneToken,
					securityToken,
					info.getContext(),
					headerInUscita,
					this.fase);
			
			String hdrTest = this.openspcoopProperties.getTransazioniTestsuiteManuallyFaultHeaderFileTraceBeforeLog();
			if(hdrTest!=null && requestInfo!=null && requestInfo.getProtocolContext()!=null && requestInfo.getProtocolContext().getHeaderFirstValue(hdrTest)!=null && 
					this.fase.name().equals(requestInfo.getProtocolContext().getHeaderFirstValue(hdrTest))) {
				throw new CoreException("Test Manually Exception generated (fileTrace) in phase '"+this.fase+"'");
			}
			
			fileTraceManager.invoke(info.getTipoPorta(), info.getContext(), requestInfo, this.fase);
						
		}catch (Throwable e) {
			String error = "["+idTransazione+"] File trace fallito: "+e.getMessage();
			this.logError(error,e);
			BooleanNullable changeEsito = BooleanNullable.NULL();
			logRegistrazioneNonRiuscita(msgdiagErrore, true, e, idTransazione,
					transaction, changeEsito);
			if(changeEsito.getValue()!=null && changeEsito.getValue().booleanValue()) {
				esito = ServicesUtils.updateEsitoConAnomalie(esito, this.log, info.getProtocolFactory());
				transazioneDTO.setEsito(esito.getCode());
			}
			if(fileTraceEnabledBloccante) {
				return new HandlerException(error,e);
			}
		}finally {
			try {
				if(fileTraceManager!=null) {
					fileTraceManager.cleanResourcesForOnlyFileTrace(transaction);
				}
			}catch(Exception eClean) {
				this.logError("["+idTransazione+"] File trace 'clean' fallito: "+eClean.getMessage(),eClean);
			}
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fileTrace = timeProcess;
			}
		}
		
		return null;
	}
	
	
	private static final MapKey<String> TRACKING_EXCEPTION = org.openspcoop2.utils.Map.newMapKey("TRACKING_EXCEPTION");
	private static final MapKey<String> TRACKING_EXCEPTION_PHASE = org.openspcoop2.utils.Map.newMapKey("TRACKING_EXCEPTION_PHASE");
	private void throwTrackingExceptionIfExists(InformazioniTransazione info) throws HandlerException {
		if(info!=null && info.getContext()!=null && info.getContext().containsKey(TRACKING_EXCEPTION)) {
			HandlerException e = (HandlerException) info.getContext().get(TRACKING_EXCEPTION);
			FaseTracciamento fase = (FaseTracciamento) info.getContext().get(TRACKING_EXCEPTION_PHASE);
			String label = FaseTracciamento.IN_REQUEST.equals(fase) ? CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_IN : CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_OUT;
			throw new HandlerException("database rilevato non disponibile nella fase '"+label+"': "+e.getMessage(),e);
		}
	}
	private void addTrackingException(InformazioniTransazione info, HandlerException e) {
		if(info!=null && info.getContext()!=null && 
				(FaseTracciamento.IN_REQUEST.equals(this.fase) || FaseTracciamento.OUT_REQUEST.equals(this.fase)) &&
				(this.openspcoopProperties.isTransazioniTracciamentoDBOutResponseThrowRequestException() || this.openspcoopProperties.isTransazioniTracciamentoDBPostOutResponseThrowRequestException())
			) {
			info.getContext().put(TRACKING_EXCEPTION, e);
			info.getContext().put(TRACKING_EXCEPTION_PHASE, this.fase);
		}
	}
	
	
	private static final MapKey<String> TRANSACTION_REGISTERED = org.openspcoop2.utils.Map.newMapKey("TRANSACTION_REGISTERED");
	private boolean isTransactionAlreadyRegistered(InformazioniTransazione info) {
		return info!=null && info.getContext()!=null && info.getContext().containsKey(TRANSACTION_REGISTERED);
	}
	private void addTransactionAlreadyRegistered(InformazioniTransazione info) {
		if(info!=null && info.getContext()!=null) {
			info.getContext().put(TRANSACTION_REGISTERED, "true");
		}
	}
	private boolean registraTransazione(Transazione transazioneDTO, 
			ITransazioneService transazioneService, InformazioniTransazione info,
			boolean autoCommit, boolean registraTracciaRichiestaInfoTransazione, boolean registraTracciaRispostaInfoTransazione) throws ServiceException, NotImplementedException, NotFoundException, ExpressionNotImplementedException, ExpressionException {
		if(FaseTracciamento.IN_REQUEST.equals(this.fase) 
				||
				!isTransactionAlreadyRegistered(info)) {
			/**System.out.println("["+this.fase+"]["+transazioneDTO.getIdTransazione()+"]["+transazioneDTO.getPddRuolo()+"]["+transazioneDTO.getPddCodice()+"]["+transazioneDTO.getNomeServizio()+"] INSERT con contesto["+transazioneDTO.getEsitoContesto()+"]");*/
			transazioneService.create(transazioneDTO);
			/**
			 * DEVO AGGIUNGERLO DOPO IL COMMIT */
			if(autoCommit && !FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)){
				addTransactionAlreadyRegistered(info);
			}
			return true;
		}
		else if(FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase) && info.getTransazioneDaAggiornare()!=null){
			/**System.out.println("["+this.fase+"]["+transazioneDTO.getIdTransazione()+"]["+transazioneDTO.getPddRuolo()+"]["+transazioneDTO.getPddCodice()+"]["+transazioneDTO.getNomeServizio()+"] UPDATE PUNTUALE con contesto["+transazioneDTO.getEsitoContesto()+"]");*/
			List<UpdateField> list = new ArrayList<>();
			list.add(new UpdateField(Transazione.model().ESITO, transazioneDTO.getEsito()));
			list.add(new UpdateField(Transazione.model().ESITO_SINCRONO, transazioneDTO.getEsitoSincrono()));
			list.add(new UpdateField(Transazione.model().ESITO_CONTESTO, transazioneDTO.getEsitoContesto()));
			list.add(new UpdateField(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO, transazioneDTO.getConsegneMultipleInCorso()));
			list.add(new UpdateField(Transazione.model().CODICE_RISPOSTA_USCITA, transazioneDTO.getCodiceRispostaUscita()));
			list.add(new UpdateField(Transazione.model().EVENTI_GESTIONE, transazioneDTO.getEventiGestione()));
			list.add(new UpdateField(Transazione.model().DATA_USCITA_RISPOSTA, transazioneDTO.getDataUscitaRisposta()));
			list.add(new UpdateField(Transazione.model().DATA_USCITA_RISPOSTA_STREAM, transazioneDTO.getDataUscitaRispostaStream()));
			list.add(new UpdateField(Transazione.model().RISPOSTA_INGRESSO_BYTES, transazioneDTO.getRispostaIngressoBytes()));
			list.add(new UpdateField(Transazione.model().RISPOSTA_USCITA_BYTES, transazioneDTO.getRispostaUscitaBytes()));
			list.add(new UpdateField(Transazione.model().FAULT_INTEGRAZIONE, transazioneDTO.getFaultIntegrazione()));
			list.add(new UpdateField(Transazione.model().FORMATO_FAULT_INTEGRAZIONE, transazioneDTO.getFormatoFaultIntegrazione()));
			list.add(new UpdateField(Transazione.model().FAULT_COOPERAZIONE, transazioneDTO.getFaultCooperazione()));
			list.add(new UpdateField(Transazione.model().FORMATO_FAULT_COOPERAZIONE, transazioneDTO.getFormatoFaultCooperazione()));
			list.add(new UpdateField(Transazione.model().ERROR_LOG, transazioneDTO.getErrorLog()));
			list.add(new UpdateField(Transazione.model().WARNING_LOG, transazioneDTO.getWarningLog()));
			
			// ** dati tracce **
			if(this.salvataggioTracceManager!=null) {
				if(registraTracciaRichiestaInfoTransazione) {
					list.add(new UpdateField(Transazione.model().TRACCIA_RICHIESTA, transazioneDTO.getTracciaRichiesta()));
				}
				if(registraTracciaRispostaInfoTransazione) {
					list.add(new UpdateField(Transazione.model().TRACCIA_RISPOSTA, transazioneDTO.getTracciaRisposta()));
				}
			}
			// ** dati diagnostica **
			if(this.salvataggioDiagnosticiManager!=null){
				list.add(new UpdateField(Transazione.model().DIAGNOSTICI, transazioneDTO.getDiagnostici()));
				list.add(new UpdateField(Transazione.model().DIAGNOSTICI_LIST_1, transazioneDTO.getDiagnosticiList1()));
				list.add(new UpdateField(Transazione.model().DIAGNOSTICI_LIST_2, transazioneDTO.getDiagnosticiList2()));
				list.add(new UpdateField(Transazione.model().DIAGNOSTICI_LIST_EXT, transazioneDTO.getDiagnosticiListExt()));
				list.add(new UpdateField(Transazione.model().DIAGNOSTICI_EXT, transazioneDTO.getDiagnosticiExt()));
			}
			
			boolean isTransazioniUpdateUseDayInterval = this.openspcoopProperties.isTransazioniUpdateUseDayInterval();
			if(isTransazioniUpdateUseDayInterval) {
				IExpression condition = transazioneService.newExpression();
				Date left = DateUtils.convertToLeftInterval(transazioneDTO.getDataIngressoRichiesta(), UnitaTemporale.GIORNALIERO);
				condition.greaterEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, left);
				Date right = DateUtils.convertToRightInterval(transazioneDTO.getDataIngressoRichiesta(), UnitaTemporale.GIORNALIERO);
				condition.lessEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, right);
				transazioneService.updateFields(transazioneDTO.getIdTransazione(), condition, list.toArray(new UpdateField[1]));
			}
			else {
				transazioneService.updateFields(transazioneDTO.getIdTransazione(), list.toArray(new UpdateField[1]));
			}
			return false;
		}
		else {
			/**System.out.println("["+this.fase+"]["+transazioneDTO.getIdTransazione()+"]["+transazioneDTO.getPddRuolo()+"]["+transazioneDTO.getPddCodice()+"]["+transazioneDTO.getNomeServizio()+"] UPDATE con contesto["+transazioneDTO.getEsitoContesto()+"]");*/
			
			boolean isTransazioniUpdateUseDayInterval = this.openspcoopProperties.isTransazioniUpdateUseDayInterval();
			if(isTransazioniUpdateUseDayInterval) {
				TransazioneDaoExt ext = new TransazioneDaoExt(transazioneDTO);
				ext.setUseDayIntervalForUpdate(true);
				transazioneService.update(transazioneDTO.getIdTransazione(), ext);
			}
			else {
				transazioneService.update(transazioneDTO.getIdTransazione(), transazioneDTO);
			}
			return false;
		}
	}
	
	private void logRegistrazioneNonRiuscita(org.openspcoop2.pdd.logger.MsgDiagnostico msgdiagErrore, boolean fileTrace, Throwable e, String idTransazione,
			Transaction transaction, BooleanNullable changeEsito) {
		if(fileTrace || !FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase)){
			try {
				msgdiagErrore.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE, e.getMessage());
				msgdiagErrore.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, fileTrace ? "fileTrace" : "database");
				switch (this.fase) {
				case IN_REQUEST:
					msgdiagErrore.addKeyword(CostantiPdD.KEY_FASE_TRACCIAMENTO, CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_IN);
					break;
				case OUT_REQUEST:
					msgdiagErrore.addKeyword(CostantiPdD.KEY_FASE_TRACCIAMENTO, CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_OUT);
					break;
				case OUT_RESPONSE:
					msgdiagErrore.addKeyword(CostantiPdD.KEY_FASE_TRACCIAMENTO, CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_RES_OUT);
					break;
				case POST_OUT_RESPONSE:
					msgdiagErrore.addKeyword(CostantiPdD.KEY_FASE_TRACCIAMENTO, CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_RES_OUT_COMPLETE);
					break;
				}
				if(FaseTracciamento.POST_OUT_RESPONSE.equals(this.fase) && transaction!=null) {
					// siamo per forza in fileTrace
					msgdiagErrore.addLogPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO, "registrazioneTransazioneNonRiuscita", transaction);
					if(changeEsito!=null) {
						changeEsito.setValue(true);
					}
				}
				else {
					msgdiagErrore.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO, "registrazioneTransazioneNonRiuscita");
				}
			}catch(Throwable t) {
				String error = "["+idTransazione+"] Registrazione dell'anomalia avvenuta durante il tracciamento non riuscita: "+t.getMessage();
				this.log.error(error,t);
			}
		}
	}
}
