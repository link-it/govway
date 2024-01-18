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
package org.openspcoop2.pdd.core.transazioni;

import java.security.SecureRandom;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.utils.OpenSPCoopAppenderUtilities;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerService;
import org.openspcoop2.core.transazioni.utils.TransactionServerUtils;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.DBConsegneMessageBoxManager;
import org.openspcoop2.pdd.config.DBConsegnePreseInCaricoManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.handlers.transazioni.ExceptionSerialzerFileSystem;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateDBManager;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.slf4j.Logger;

/**     
 * GestoreConsegnaMultipla
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreConsegnaMultipla {

	private static final String ID_MODULO = "GestoreConsegnaMultipla";

	private static DAOFactory daoFactory = null;
	private static DAOFactory daoFactoryDevNull = null;
	private static ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
	private static ServiceManagerProperties daoFactoryDevNullServiceManagerPropertiesTransazioni = null;
	private static Logger daoFactoryLoggerTransazioni = null;
	private static Logger daoFactoryLoggerTransazioniSql = null;
	private static Logger daoFactoryLoggerTransazioniDevNull = null;

	private static OpenSPCoop2Properties openspcoopProperties = null;

	private static GestoreConsegnaMultipla gestoreConsegnaMultipla = null;
	private static synchronized void init() throws TransactionMultiDeliverException{
		if(gestoreConsegnaMultipla==null){
			String tipoDatabase = null;
			boolean debug = false;
			try{
				openspcoopProperties = OpenSPCoop2Properties.getInstance();

				tipoDatabase = openspcoopProperties.getDatabaseType();
				//System.out.println("DS["+this.datasource+"] TIPODB["+this.tipoDatabase+"]");

				if(tipoDatabase==null){
					throw new Exception("Tipo Database non definito");
				}

				openspcoopProperties = OpenSPCoop2Properties.getInstance();

				debug = openspcoopProperties.isTransazioniDebug();

				daoFactoryLoggerTransazioni = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(debug);
				daoFactoryLoggerTransazioniSql = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSql(debug);
				daoFactoryLoggerTransazioniDevNull = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniDevNull();
				
				daoFactory = DAOFactory.getInstance(daoFactoryLoggerTransazioniSql);
				DAOFactoryProperties daoFactoryProperties = DAOFactoryProperties.getInstance(daoFactoryLoggerTransazioniSql);
				daoFactoryServiceManagerPropertiesTransazioni = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				daoFactoryServiceManagerPropertiesTransazioni.setShowSql(debug);	
				daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());
				
				daoFactoryDevNull = DAOFactory.getInstance(daoFactoryLoggerTransazioniDevNull);
				DAOFactoryProperties daoFactoryPropertiesDevNull = DAOFactoryProperties.getInstance(daoFactoryLoggerTransazioniDevNull);
				daoFactoryDevNullServiceManagerPropertiesTransazioni = daoFactoryPropertiesDevNull.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				daoFactoryDevNullServiceManagerPropertiesTransazioni.setShowSql(debug);	
				daoFactoryDevNullServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());
				

			}catch(Exception e){
				throw new TransactionMultiDeliverException("Inizializzazione risorse database non riuscita: "+e.getMessage(),e);
			}
			try{
				gestoreConsegnaMultipla = new GestoreConsegnaMultipla(daoFactoryLoggerTransazioni, daoFactoryLoggerTransazioniSql,
						tipoDatabase,
						debug);
			}catch(Exception e){
				throw new TransactionMultiDeliverException("Inizializzazione GestoreConsegnaMultipla non riuscita: "+e.getMessage(),e);
			}
		}
	}
	public static GestoreConsegnaMultipla getInstance() throws TransactionMultiDeliverException {
		if(gestoreConsegnaMultipla==null){
			init();
		}
		return gestoreConsegnaMultipla;
	}


	
	
	private static java.util.Random _rnd = null;
	private static synchronized void initRandom() {
		if(_rnd==null) {
			_rnd = new SecureRandom();
		}
	}
	public static java.util.Random getRandom() {
		if(_rnd==null) {
			initRandom();
		}
		return _rnd;
	}
	




	private Logger log = null;
	private Logger logSql = null;
	//	private String datasource = null;
	private String tipoDatabase = null;
	private boolean debug = false;

	private IDiagnosticProducer msgDiagnosticiOpenSPCoopAppender = null;
	private IDumpProducer dumpOpenSPCoopAppender = null;
	private boolean transazioniRegistrazioneDumpHeadersCompactEnabled = false;

	public GestoreConsegnaMultipla(Logger log,Logger logSql,
			//String dataSource,
			String tipoDatabase,boolean debug) throws TransactionMultiDeliverException{

		this.log = log;
		this.logSql = logSql;
		this.tipoDatabase = tipoDatabase;
		//		this.datasource = dataSource;
		this.debug = debug;

		boolean usePdDConnection = true;

		try{

			// Init
			this.msgDiagnosticiOpenSPCoopAppender = new org.openspcoop2.pdd.logger.MsgDiagnosticoOpenSPCoopProtocolAppender();
			OpenspcoopAppender diagnosticoOpenSPCoopAppender = new OpenspcoopAppender();
			diagnosticoOpenSPCoopAppender.setTipo("__gestoreConsegnaMultipla");
			List<Property> diagnosticoOpenSPCoopAppenderProperties = new ArrayList<Property>();

			// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
			OpenSPCoopAppenderUtilities.addParameters(this.logSql, diagnosticoOpenSPCoopAppenderProperties, 
					null, // nessun datasource
					null, null, null, null,  // nessuna connection
					this.tipoDatabase,
					usePdDConnection, // viene usata la connessione della PdD
					this.debug
					);
			OpenSPCoopAppenderUtilities.addCheckProperties(diagnosticoOpenSPCoopAppenderProperties, false);

			diagnosticoOpenSPCoopAppender.setPropertyList(diagnosticoOpenSPCoopAppenderProperties);
			this.msgDiagnosticiOpenSPCoopAppender.initializeAppender(diagnosticoOpenSPCoopAppender);
			this.msgDiagnosticiOpenSPCoopAppender.isAlive();

		}catch(Exception e){
			throw new TransactionMultiDeliverException("Errore durante l'inizializzazione del DiagnosticoAppender: "+e.getMessage(),e);
		} 

		try{

			// Init
			this.dumpOpenSPCoopAppender = new org.openspcoop2.pdd.logger.DumpOpenSPCoopProtocolAppender();
			OpenspcoopAppender dumpOpenSPCoopAppender = new OpenspcoopAppender();
			dumpOpenSPCoopAppender.setTipo("__gestoreConsegnaMultipla");
			List<Property> dumpOpenSPCoopAppenderProperties = new ArrayList<Property>();

			// Verra poi utilizzata la connessione ottenuta ogni volta che il timer viene eseguito, infatti si usa usePdDConnection
			OpenSPCoopAppenderUtilities.addParameters(this.logSql, dumpOpenSPCoopAppenderProperties, 
					null, // nessun datasource
					null, null, null, null,  // nessuna connection
					this.tipoDatabase,
					usePdDConnection, // viene usata la connessione della PdD
					this.debug
					);
			OpenSPCoopAppenderUtilities.addCheckProperties(dumpOpenSPCoopAppenderProperties, false);

			dumpOpenSPCoopAppender.setPropertyList(dumpOpenSPCoopAppenderProperties);
			this.dumpOpenSPCoopAppender.initializeAppender(dumpOpenSPCoopAppender);
			this.dumpOpenSPCoopAppender.isAlive();
			
			// Indicazioni sulle modalita' di salvataggio degli header del dump
			this.transazioniRegistrazioneDumpHeadersCompactEnabled = openspcoopProperties.isTransazioniRegistrazioneDumpHeadersCompactEnabled();

		}catch(Exception e){
			throw new TransactionMultiDeliverException("Errore durante l'inizializzazione del DumpAppender: "+e.getMessage(),e);
		} 

	}


	// *** SAFE ***

	public void safeCreate(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPA, IOpenSPCoopState state, RequestInfo requestInfo, Context context) {

		// cluster id
		transazioneApplicativoServer.setClusterIdPresaInCarico(openspcoopProperties.getClusterId(false));
		
		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo(), false, false, idPA, state, null, requestInfo, context, "create",
				OpenSPCoopStateDBManager.runtime);

	}
	
	public void safeUpdateConsegna(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPA, IOpenSPCoopState state, RequestInfo requestInfo, Context context) {

		// cluster id
		transazioneApplicativoServer.setClusterIdConsegna(openspcoopProperties.getClusterId(false));
		
		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo(), true, true, idPA, state, null, requestInfo, context, "updateDeliveredMessage",
				OpenSPCoopStateDBManager.consegnePreseInCarico); // l'informazione dovrebbe esistere!

	}
	
	public void safeUpdatePrelievoIM(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPA, IOpenSPCoopState state, Context context) {

		// cluster id
		transazioneApplicativoServer.setClusterIdPrelievoIm(openspcoopProperties.getClusterId(false));
		
		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo(), true, false, idPA, state, null, null, context, "updateRetrieveMessageByMessageBox",
				OpenSPCoopStateDBManager.messageBox);

	}
	
	public void safeUpdateEliminazioneIM(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPA, IOpenSPCoopState state, Context context) {

		// cluster id
		transazioneApplicativoServer.setClusterIdEliminazioneIm(openspcoopProperties.getClusterId(false));
		
		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo(), true, false, idPA, state, null, null, context, "updateDeletedMessageByMessageBox",
				OpenSPCoopStateDBManager.messageBox);

	}
	
	public void safeUpdateMessaggioScaduto(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPA, IOpenSPCoopState state) {

		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo(), true, false, idPA, state, null, null, null, "updateExpiredMessage",
				OpenSPCoopStateDBManager.runtime);

	}

	
	public void safeSave(org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico diagnostico, IDPortaApplicativa idPA, IState state, RequestInfo requestInfo, Context context) {

		getConnectionAndSave(diagnostico, diagnostico.getProtocollo(), false, false, idPA, null, state, requestInfo, context, "saveDiagnostic",
				OpenSPCoopStateDBManager.consegnePreseInCarico);

	}

	public void safeSave(Messaggio dumpMessaggio, IDPortaApplicativa idPA, IState state, RequestInfo requestInfo, Context context) {

		getConnectionAndSave(dumpMessaggio, dumpMessaggio.getProtocollo(), false, false, idPA, null, state, requestInfo, context, "saveContent",
				OpenSPCoopStateDBManager.consegnePreseInCarico);

	}

	private void getConnectionAndSave(Object o, String protocol, boolean update, boolean throwNotFoundIfNotExists, IDPortaApplicativa idPA, 
			IOpenSPCoopState openspcoopState, IState state, RequestInfo requestInfo, Context context,
			String tipoOperazione,
			OpenSPCoopStateDBManager dbManagerSource) {
		TransazioniSAProcessTimes times = null;
		long timeStart = -1;
		boolean buildDetailsSA = false;
		boolean buildDetailsUpdateTransaction = false;
		if(openspcoopProperties.isTransazioniRegistrazioneSlowLog()) {
			times = new TransazioniSAProcessTimes();
			timeStart = DateManager.getTimeMillis();
			buildDetailsSA = openspcoopProperties.isTransazioniRegistrazioneSlowLogConnettoriMultipliProcessTransactionSADetails();
			buildDetailsUpdateTransaction = openspcoopProperties.isTransazioniRegistrazioneSlowLogConnettoriMultipliUpdateTransactionDetails();
		}
		try {
			getConnectionAndSave(o, protocol, update, throwNotFoundIfNotExists, idPA, 
					openspcoopState, state, requestInfo, context,
					tipoOperazione, times, buildDetailsSA,  buildDetailsUpdateTransaction,
					dbManagerSource);
		}finally {
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				if(timeProcess>=openspcoopProperties.getTransazioniRegistrazioneSlowLogThresholdMs()) {
					StringBuilder sb = new StringBuilder();
					sb.append(timeProcess);
					if(times.idTransazione!=null) {
						sb.append(" <").append(times.idTransazione).append(">");
					}
					if(times.servizioApplicativoErogatore!=null) {
						sb.append(" (sa:").append(times.servizioApplicativoErogatore).append(")");
					}
					sb.append(" [GestoreConsegnaMultipla."+tipoOperazione+"]");
					sb.append(" ").append(times.toString());
					OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSlowLog().info(sb.toString());
				}
			}
		}
	}
	@SuppressWarnings("resource")
	private void getConnectionAndSave(Object o, String protocol, boolean update, boolean throwNotFoundIfNotExists, IDPortaApplicativa idPA, 
			IOpenSPCoopState openspcoopState, IState state, RequestInfo requestInfo, Context context,
			String tipoOperazione, TransazioniSAProcessTimes times, boolean buildDetailsSA, boolean buildDetailsUpdateTransaction,
			OpenSPCoopStateDBManager dbManagerSource) {

		EsitiProperties esitiProperties = null;
		try {
			if(requestInfo!=null && requestInfo.getProtocolFactory()!=null) {
				esitiProperties = EsitiProperties.getInstance(this.log, requestInfo.getProtocolFactory());
				if(esitiProperties==null &&
					// per i casi di restart nodi in cui vengono modificati gli id delle chiavi, mentre serializzato nel messaggio e' rimasto il precedente
					requestInfo.getProtocolFactory().getProtocol()!=null) {
					esitiProperties = EsitiProperties.getInstanceFromProtocolName(this.log, requestInfo.getProtocolFactory().getProtocol());
				}
			}
			else {
				esitiProperties = EsitiProperties.getInstanceFromProtocolName(this.log, protocol);
			}
		}catch(Throwable e) {
			this.log.error("Errore avvenuto durante la lettura del gestore degli esiti: "+e.getMessage() ,e);
		}
		if(esitiProperties==null) {
			return;
		}
		
		try{
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(); 
			Tracciamento configTracciamento = configPdDManager.getOpenSPCoopAppenderTracciamento();
			StringBuilder bf = new StringBuilder();
			String esitiConfig = configTracciamento!=null ? configTracciamento.getEsiti() : null;
			if(idPA!=null) {
				PortaApplicativa pa = configPdDManager.getPortaApplicativaSafeMethod(idPA, requestInfo);
				if(pa!=null && pa.getTracciamento()!=null && pa.getTracciamento().getEsiti()!=null) {
					esitiConfig = pa.getTracciamento().getEsiti();
				}
			}
			List<String> esitiDaRegistrare = EsitiConfigUtils.getRegistrazioneEsiti(esitiConfig, this.log, bf);
			
			int code = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA);
			String codeAsString = code+"";
			if(esitiDaRegistrare!=null && !esitiDaRegistrare.isEmpty() && esitiDaRegistrare.contains(codeAsString)==false){
				this.log.debug("Non devo registrare l'informazione, esito '"+codeAsString+"' disabilitato nel tracciamento");
				return;
			}
			
		}catch(Throwable e) {
			this.log.debug("Errore avvenuto durante la lettura della configurazione delle transazioni da salvare: "+e.getMessage(),e); 
		}
		
		String richiedenteConnessione = null;
		if(o!=null) {
			if(o instanceof TransazioneApplicativoServer) {
				richiedenteConnessione = tipoOperazione+ "_" +((TransazioneApplicativoServer)o).getIdTransazione();
				richiedenteConnessione = richiedenteConnessione + "_" + ((TransazioneApplicativoServer)o).getServizioApplicativoErogatore();
				if(times!=null) {
					times.idTransazione = ((TransazioneApplicativoServer)o).getIdTransazione();
					times.servizioApplicativoErogatore = ((TransazioneApplicativoServer)o).getServizioApplicativoErogatore();
				}
			}
			else if(o instanceof org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico) {
				richiedenteConnessione = tipoOperazione+ "_" +((org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico)o).getIdTransazione();
				if(times!=null) {
					times.idTransazione = ((org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico)o).getIdTransazione();
				}
			}
			else if(o instanceof Messaggio) {
				richiedenteConnessione = tipoOperazione+ "_" +((Messaggio)o).getIdTransazione();
				if(times!=null) {
					times.idTransazione = ((Messaggio)o).getIdTransazione();
				}
			}
		}
		
		Resource dbResource = null;
		DBTransazioniManager dbManager_transazioni = null;
		DBConsegnePreseInCaricoManager dbConsegnePreseInCaricoManager = null;
		DBConsegneMessageBoxManager dbConsegneMessageBoxManager = null;
		if(dbManagerSource!=null) {
			switch (dbManagerSource) {
			case runtime:
				dbManager_transazioni = DBTransazioniManager.getInstance();
				break;
			case consegnePreseInCarico:
				dbConsegnePreseInCaricoManager = DBConsegnePreseInCaricoManager.getInstanceTransazioni();
				break;
			case smistatoreMessaggiPresiInCarico: // non utilizzato
				dbConsegnePreseInCaricoManager = DBConsegnePreseInCaricoManager.getInstanceTransazioni();
				break;
			case messageBox:
				dbConsegneMessageBoxManager = DBConsegneMessageBoxManager.getInstanceTransazioni();
				break;
			}
		}
		else {
			dbManager_transazioni = DBTransazioniManager.getInstance();
		}
		IDSoggetto idDominio = openspcoopProperties.getIdentitaPortaDefault(protocol, requestInfo);
		ExceptionSerialzerFileSystem exceptionSerializerFileSystem = new ExceptionSerialzerFileSystem(this.log);
		Connection con = null;
		boolean isMessaggioConsegnato = false;
		boolean possibileTerminazioneSingleIntegrationManagerMessage = false;
		boolean consegnaInErrore = false;
		TransazioneApplicativoServer transazioneApplicativoServer = null;
		boolean useConnectionRuntime = false;
		try{
			long timeStart = -1;
			try{
				if(times!=null) {
					timeStart = DateManager.getTimeMillis();
				}
				boolean checkPddRuntimeDatasource = false;
				if(dbManager_transazioni!=null) {
					checkPddRuntimeDatasource = dbManager_transazioni.useRuntimePdD();
				}
				else if(dbConsegnePreseInCaricoManager!=null) {
					checkPddRuntimeDatasource = dbConsegnePreseInCaricoManager.useRuntimePdD();
				}
				else if(dbConsegneMessageBoxManager!=null) {
					checkPddRuntimeDatasource = dbConsegneMessageBoxManager.useRuntimePdD();
				}
				
				if(checkPddRuntimeDatasource) {
					if(openspcoopState!=null) {
						if(openspcoopState instanceof OpenSPCoopState) {
							OpenSPCoopState s = (OpenSPCoopState) openspcoopState;
							if(s.getConnectionDB()!=null && !s.getConnectionDB().isClosed()) {
								con = s.getConnectionDB();
								useConnectionRuntime = true;
							}
						}
					}
					else if(state!=null) {
						if(state instanceof StateMessage) {
							StateMessage s = (StateMessage) state;
							if(s.getConnectionDB()!=null && !s.getConnectionDB().isClosed()) {
								con = s.getConnectionDB();
								useConnectionRuntime = true;
							}
						}
					}
				}
				
				if(useConnectionRuntime==false){
					if(dbManager_transazioni!=null) {
						dbResource = dbManager_transazioni.getResource(idDominio, ID_MODULO, richiedenteConnessione);
					}
					else if(dbConsegnePreseInCaricoManager!=null) {
						dbResource = dbConsegnePreseInCaricoManager.getResource(idDominio, ID_MODULO, richiedenteConnessione);
					}
					else if(dbConsegneMessageBoxManager!=null){
						dbResource = dbConsegneMessageBoxManager.getResource(idDominio, ID_MODULO, richiedenteConnessione);
					}
					
					if(dbResource!=null) {
						con = (Connection) dbResource.getResource();
					}
				}
				
			}finally {	
				if(times!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					times.getConnection = timeProcess;
				}
			}
				
			boolean autoCommit = false;
			if(con!=null) {
				con.setAutoCommit(autoCommit);
			}

			if(o instanceof TransazioneApplicativoServer) {
				int conflict = 0;
				int iteration = 1; // inizio da 1 per i log
				boolean updateEffettuato = false;
				try{
					if(times!=null) {
						timeStart = DateManager.getTimeMillis();
						if(buildDetailsSA) {
							times.sa_details = new ArrayList<>();
						}
					}
					
					org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
							(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
									con, autoCommit,
									daoFactoryServiceManagerPropertiesTransazioni,
									daoFactoryLoggerTransazioniDevNull);
									//daoFactoryLoggerTransazioniSql);
					jdbcServiceManager.getJdbcProperties().setShowSql(this.debug);
					ITransazioneApplicativoServerService transazioneService = jdbcServiceManager.getTransazioneApplicativoServerService();
	
					// ** Aggiorno campi dipendenti da questa invocazione **
					// Campi che non possono essere gestiti a livello 'core'
	
					transazioneApplicativoServer = (TransazioneApplicativoServer) o;
	
					// dettaglio esito
					this.setDettaglioEsito(transazioneApplicativoServer, esitiProperties, context);
					
					// consegna terminata
					if(transazioneApplicativoServer.isConsegnaTerminata()) {
						isMessaggioConsegnato = true;
					}
					else if(transazioneApplicativoServer.getDataEliminazioneIm()!=null) {
						isMessaggioConsegnato = true;
						possibileTerminazioneSingleIntegrationManagerMessage = true;
					}
					else if(transazioneApplicativoServer.getDataMessaggioScaduto()!=null) {
						isMessaggioConsegnato = true;
						possibileTerminazioneSingleIntegrationManagerMessage = true;
					}
					else if(transazioneApplicativoServer.isConsegnaTrasparente() && transazioneApplicativoServer.getDataUscitaRichiesta()!=null) {
						// !transazioneApplicativoServer.isConsegnaTerminata() altrimenti entrava nel primo if
						consegnaInErrore = true;
					}
	
					boolean useSelectForUpdate = true;
					/*
					 * Grazie alla select for update riesco a mettere il lock solamente sulla riga interessata
					 */
					
					int oldTransactionIsolation = -1;
					if(!useSelectForUpdate) {
						try{
							oldTransactionIsolation = con.getTransactionIsolation();
							// già effettuato fuori dal metodo connectionDB.setAutoCommit(false);
							JDBCUtilities.setTransactionIsolationSerializable(daoFactoryServiceManagerPropertiesTransazioni.getDatabase(), con);
						} catch(Exception er) {
							throw new CoreException("(setIsolation) "+er.getMessage(),er);
						}
					}
					
					long gestioneSerializableDB_AttesaAttiva = openspcoopProperties.getGestioneSerializableDB_AttesaAttiva();
					int gestioneSerializableDB_CheckInterval = openspcoopProperties.getGestioneSerializableDB_CheckInterval();
					
					long scadenzaWhile = DateManager.getTimeMillis() + gestioneSerializableDB_AttesaAttiva;
					
					Throwable lastT = null;
					while(updateEffettuato==false && DateManager.getTimeMillis() < scadenzaWhile){
	
						List<String> timeDetails = null;
						try{
							
							if(times!=null && buildDetailsSA) {
								timeDetails = new ArrayList<>();
							}
							
							boolean transazioneAggiornata = TransactionServerUtils.save(transazioneService, (TransazioneApplicativoServer)o, update, throwNotFoundIfNotExists, false, 
									useSelectForUpdate, timeDetails);
														
							long timeStartCommit = -1;
							if(timeDetails!=null) {
								timeStartCommit = DateManager.getTimeMillis();
							}
							if(con!=null) {
								con.commit();
							}
							if(timeDetails!=null) {
								long timeEnd =  DateManager.getTimeMillis();
								long timeProcess = timeEnd-timeStartCommit;
								timeDetails.add("commit:"+timeProcess);
							}
							
							if(!transazioneAggiornata) {
								isMessaggioConsegnato = false; // per gestire eventuali errori durante il  recupero da file system
							}
	
							updateEffettuato = true;
	
						} catch(Throwable e) {
							conflict++;
							lastT = e;
							if(timeDetails!=null) {
								String errorMsg = e.getMessage();
								if(errorMsg!=null) {
									if(errorMsg.length()>100) {
										errorMsg = errorMsg.substring(0, 97)+"...";
									}
									timeDetails.add("error:"+errorMsg);
								}
							}
							//System.out.println("Serializable error:"+e.getMessage());
							try{
								if(con!=null) {
									con.rollback();
								}
							} catch(Exception er) {}
						}finally {
							if(timeDetails!=null && !timeDetails.isEmpty()) {
								for (String detail : timeDetails) {
									times.sa_details.add("i"+iteration+"-"+detail);
								}
							}
						}
	
						if(updateEffettuato == false){
							// Per aiutare ad evitare conflitti
							try{
								Utilities.sleep(getRandom().nextInt(gestioneSerializableDB_CheckInterval)); // random da 0ms a checkIntervalms
							}catch(Exception eRandom){
								// ignore
							}
							iteration++;
						}
					}
					// Ripristino Transazione
					if(!useSelectForUpdate) {
						try{
							if(con!=null) {
								con.setTransactionIsolation(oldTransactionIsolation);
							}
							// già effettuato fuori dal metodo connectionDB.setAutoCommit(true);
						} catch(Exception er) {
							throw new CoreException("(ripristinoIsolation) "+er.getMessage(),er);
						}
					}
					if(lastT!=null && !updateEffettuato) {
						// registro ultimo errore avvenuto durante il ciclo
						String msgError = "[id:"+transazioneApplicativoServer.getIdTransazione()+"][sa:"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"]["+transazioneApplicativoServer.getConnettoreNome()+"] 'updateTransazioneSA' failed: "+lastT.getMessage();
						daoFactoryLoggerTransazioniSql.error(msgError,lastT);
						this.log.error(msgError,lastT);
						throw lastT;
					}
				}finally {	
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						if(update) {
							times.update = timeProcess;
						}
						else {
							times.insert = timeProcess;
						}
						if(buildDetailsSA) {
							times.sa_details.add("iteration:"+iteration);
							times.sa_details.add("conflicts:"+conflict);
							times.sa_details.add("updated:"+updateEffettuato);
						}
					}
				}
			}
			else if(o instanceof org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico) {
				try{
					if(times!=null) {
						timeStart = DateManager.getTimeMillis();
					}
					
					this.msgDiagnosticiOpenSPCoopAppender.log(con, (org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico)o);
					
					if(con!=null) {
						con.commit();
					}
				}finally {	
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.insert = timeProcess;
					}
				}
			}
			else if(o instanceof Messaggio) {
				try{
					if(times!=null) {
						timeStart = DateManager.getTimeMillis();
					}
					
					this.dumpOpenSPCoopAppender.dump(con, (Messaggio)o, this.transazioniRegistrazioneDumpHeadersCompactEnabled);
					
					if(con!=null) {
						con.commit();
					}
				}finally {	
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.insert = timeProcess;
					}
				}
			}

		}catch(Throwable e){
			try{
				if(con!=null) {
					con.rollback();
				}
			}catch(Exception eRollback){
				// ignore
			}

			if(o instanceof TransazioneApplicativoServer) {
				
				isMessaggioConsegnato = false; // per gestire eventuali errori durante il  recupero da file system
				
				TransazioneApplicativoServer serverInfo = (TransazioneApplicativoServer) o;
				try{
					if(serverInfo.getIdTransazione()!=null && serverInfo.getServizioApplicativoErogatore()!=null) {
						// NOTA: volutamente salvo serverInfo per poter reimplementare la logica di cui sopra
						serverInfo.setProtocollo(protocol);
						exceptionSerializerFileSystem.registrazioneFileSystemTransazioneApplicativoServerEmessoPdD(serverInfo, serverInfo.getIdTransazione(), serverInfo.getServizioApplicativoErogatore());
					}
				} catch (Throwable eClose) {}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura dell'informazione del server '"+serverInfo.getServizioApplicativoErogatore()+"' associato alla transazione sul database: " + e.getLocalizedMessage();
				this.log.error("["+serverInfo.getIdTransazione()+"] "+msg,e);
			}
			else if(o instanceof org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico) {
				org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = (org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico) o;
				try{
					if(msgDiag.getIdTransazione()!=null && msgDiag.getApplicativo()!=null) {
						exceptionSerializerFileSystem.registrazioneFileSystemDiagnosticoEmessoPdD(msgDiag, msgDiag.getIdTransazione(), msgDiag.getApplicativo());
					}
				} catch (Throwable eClose) {}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura del diagnostico relativo al server '"+msgDiag.getApplicativo()+"' associato alla transazione sul database: " + e.getLocalizedMessage();
				this.log.error("["+msgDiag.getIdTransazione()+"] "+msg,e);
			}
			else if(o instanceof Messaggio) {
				Messaggio messaggio = (Messaggio) o;
				try{
					if(messaggio.getIdTransazione()!=null && messaggio.getServizioApplicativoErogatore()!=null) {
						exceptionSerializerFileSystem.registrazioneFileSystemDumpEmessoPdD(messaggio, messaggio.getIdTransazione(), 
								messaggio.getServizioApplicativoErogatore(), messaggio.getDataConsegna());
					}
				} catch (Throwable eClose) {}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura del messaggio relativo al server '"+messaggio.getServizioApplicativoErogatore()+"' associato alla transazione sul database: " + e.getLocalizedMessage();
				this.log.error("["+messaggio.getIdTransazione()+"] "+msg,e);
			}

		}finally{
			
			if(isMessaggioConsegnato || consegnaInErrore) {

				long timeStart = -1;
				try{
					List<String> timeDetails = null;
					if(times!=null) {
						timeStart = DateManager.getTimeMillis();
						if(buildDetailsUpdateTransaction) {
							times.transazione_details = new ArrayList<>();
							timeDetails = times.transazione_details;
						}
					}
				
					// aggiorno esito transazione (non viene sollevata alcuna eccezione)
					boolean transazioneAggiornata = _safe_aggiornaInformazioneConsegnaTerminata(transazioneApplicativoServer, con, esitiProperties, possibileTerminazioneSingleIntegrationManagerMessage, consegnaInErrore, timeDetails);
					if(!transazioneAggiornata) {
						
						String prefix = "[id:"+transazioneApplicativoServer.getIdTransazione()+"][sa:"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"]["+transazioneApplicativoServer.getConnettoreNome()+"] 'gestisciErroreAggiornamentoInformazioneConsegnaTerminata'";
						
						boolean existsTransaction = false;
						try {
							existsTransaction = TransactionServerUtils.existsTransaction(transazioneApplicativoServer.getIdTransazione(), con, this.tipoDatabase, this.log, 
									true); // la sessione viene chiusa dopo
						}catch(Throwable t) {
							String msgError = prefix+" compresione esistenza transazione fallita";
							this.log.error(msgError, t);
						}
						
						boolean serializeTransactionInfo = false;
						try {
							serializeTransactionInfo = true;
							try{
								if(transazioneApplicativoServer.getIdTransazione()!=null && transazioneApplicativoServer.getServizioApplicativoErogatore()!=null) {
									// NOTA: volutamente salvo serverInfo per poter reimplementare la logica di cui sopra
									transazioneApplicativoServer.setProtocollo(protocol);
									exceptionSerializerFileSystem.registrazioneFileSystemTransazioneApplicativoServerConsegnaTerminata(transazioneApplicativoServer, transazioneApplicativoServer.getIdTransazione(), transazioneApplicativoServer.getServizioApplicativoErogatore());
								}
							} catch (Throwable eClose) {}
							
						}catch(Throwable t) {
							String msgError = prefix+" compresione esistenza transazione fallita";
							this.log.error(msgError,t );
						}
						
						String msgError = prefix+" effettuata gestione dell'errore (existsTransaction="+existsTransaction+" serializeTransactionInfo="+serializeTransactionInfo+")";
						this.log.error(msgError);
						
					}
				}finally {	
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.updateTransazione = timeProcess;
					}
				}
			}
			
			try{
				if(con!=null) {
					con.setAutoCommit(true);
				}
			}catch(Exception eRollback){
				// ignore
			}
			try {
				if(useConnectionRuntime==false) {
					if(dbManager_transazioni!=null) {
						dbManager_transazioni.releaseResource(idDominio, ID_MODULO, dbResource);
					}
					else if(dbConsegnePreseInCaricoManager!=null) {
						dbConsegnePreseInCaricoManager.releaseResource(idDominio, ID_MODULO, dbResource);
					}
					else if(dbConsegneMessageBoxManager!=null){
						dbConsegneMessageBoxManager.releaseResource(idDominio, ID_MODULO, dbResource);
					}
				}
			} catch (Exception e) {
				// ignore
			}
		}


	}

	private void setDettaglioEsito(TransazioneApplicativoServer transazioneApplicativoServer, EsitiProperties esitiProperties, Context context) throws ProtocolException {
		
		if(transazioneApplicativoServer.getDataMessaggioScaduto()!=null) {
			return; // non devo aggiornare l'esito
		}
		else if(transazioneApplicativoServer.getDataPrelievoIm()!=null) {
			return; // non devo aggiornare l'esito
		}
		else if(transazioneApplicativoServer.getDataEliminazioneIm()!=null) {
			transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.OK));
		}
		else {
			if(transazioneApplicativoServer.getDataUscitaRichiesta()!=null) {
				if(transazioneApplicativoServer.getFault()!=null) {
					transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO));
				}
				else if(transazioneApplicativoServer.getCodiceRisposta()!=null) {
					int code = -1;
					try {
						code = Integer.valueOf(transazioneApplicativoServer.getCodiceRisposta());
					}catch(Exception e) {
						// ignore
					}
					if(code>0) {
						if(code<=299) {
							transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.OK));
						}
						else if(transazioneApplicativoServer.isConsegnaTerminata()) {
							
							if(code>299 && code<=399) {
								transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.HTTP_3xx));
							}
							else if(code>399 && code<=499) {
								transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.HTTP_4xx));
							}
							else { //if(code>499) {
								transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.HTTP_5xx));
							}
							
						}
						else {
							setErroreInvocazione(transazioneApplicativoServer, esitiProperties, context);
						}
					}
					else {
						// altro tipo di codice
						if(transazioneApplicativoServer.isConsegnaTerminata()) {
							transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.OK));
						}
						else {
							setErroreInvocazione(transazioneApplicativoServer, esitiProperties, context);
						}
					}
				}
				else {
					// senza codice di risposta
					if(transazioneApplicativoServer.isConsegnaTerminata()) {
						transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.OK));
					}
					else {
						setErroreInvocazione(transazioneApplicativoServer, esitiProperties, context);
					}
				}
			}
		}
		
	}
	
	private void setErroreInvocazione(TransazioneApplicativoServer transazioneApplicativoServer, EsitiProperties esitiProperties, Context context) throws ProtocolException {
		EsitoTransazioneName name = EsitoTransazioneName.ERRORE_INVOCAZIONE;
		if(context!=null &&
				context.containsKey(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE)
			){

			String timeoutExceededMessage = null;
			if(context.containsKey(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE)) {
				timeoutExceededMessage = (String) context.get(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_NAME_CONTROLLO_TRAFFICO_VIOLAZIONE);
				if(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_CONNECTION_TIMEOUT.equals(timeoutExceededMessage)) {
					name = EsitoTransazioneName.ERRORE_CONNECTION_TIMEOUT;
				}
				else if(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_REQUEST_READ_TIMEOUT.equals(timeoutExceededMessage)) {
					name = EsitoTransazioneName.ERRORE_REQUEST_TIMEOUT;
				}
				else if(org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_RESPONSE_READ_TIMEOUT.equals(timeoutExceededMessage) ||
						org.openspcoop2.core.controllo_traffico.constants.Costanti.PDD_CONTEXT_VALUE_READ_TIMEOUT.equals(timeoutExceededMessage)) {
					name = EsitoTransazioneName.ERRORE_RESPONSE_TIMEOUT;
				}
			}
			
		}
		
		transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(name));
	}

	private static boolean _debug = true;
	public static boolean is_debug() {
		return _debug;
	}
	public static void set_debug(boolean _debug) {
		GestoreConsegnaMultipla._debug = _debug;
	}
	private boolean _safe_aggiornaInformazioneConsegnaTerminata(TransazioneApplicativoServer transazioneApplicativoServer, Connection con, EsitiProperties esitiProperties,
			 boolean possibileTerminazioneSingleIntegrationManagerMessage,boolean consegnaInErrore,
			 List<String> timeDetails) {
		
		//boolean debug = this.debug;
		boolean debug = _debug; // un eventuale errore deve essere sempre registrato
		
		DAOFactory daoF = debug ? daoFactory : daoFactoryDevNull;
		Logger logFactory = debug ? daoFactoryLoggerTransazioniSql : daoFactoryLoggerTransazioniDevNull;
		ServiceManagerProperties smp = debug ? daoFactoryServiceManagerPropertiesTransazioni : daoFactoryDevNullServiceManagerPropertiesTransazioni;
		
		int esitoConsegnaMultipla = -1;
		int esitoConsegnaMultiplaInCorso = -1;
		int esitoConsegnaMultiplaFallita = -1;
		int esitoConsegnaMultiplaCompletata = -1;
		int ok = -1;
		int esitoIntegrationManagerSingolo = -1;
		boolean esitiLetti = false;
		try {
			esitoConsegnaMultipla = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA);
			esitoConsegnaMultiplaInCorso = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_IN_CORSO);
			esitoConsegnaMultiplaFallita = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_FALLITA);
			esitoConsegnaMultiplaCompletata = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_COMPLETATA);
			ok = esitiProperties.convertoToCode(EsitoTransazioneName.OK);
			esitoIntegrationManagerSingolo = esitiProperties.convertoToCode(EsitoTransazioneName.MESSAGE_BOX);
			esitiLetti = true;			
		}catch(Exception er) {
			// errore che non dovrebbe succedere
			String msg = "Errore durante l'aggiornamento delle transazione relativamente all'informazione del server '"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"': (readEsiti) " + er.getMessage();
			this.log.error("[id:"+transazioneApplicativoServer.getIdTransazione()+"][sa:"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"]["+transazioneApplicativoServer.getConnettoreNome()+"] "+msg,er);
		}
		
		if(esitiLetti) {
			return TransactionServerUtils.safe_aggiornaInformazioneConsegnaTerminata(transazioneApplicativoServer, con, 
					this.tipoDatabase, this.log,
					daoF,logFactory,smp,
					this.debug,
					esitoConsegnaMultipla, esitoConsegnaMultiplaInCorso, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
					esitoIntegrationManagerSingolo, possibileTerminazioneSingleIntegrationManagerMessage, consegnaInErrore,
					openspcoopProperties.getGestioneSerializableDB_AttesaAttiva(),openspcoopProperties.getGestioneSerializableDB_CheckInterval(),
					timeDetails);
		}
		return false;
	}

}

class TransazioniSAProcessTimes{

	String idTransazione;
	String servizioApplicativoErogatore;
	long getConnection = -1;
	long insert = -1;
	long update = -1;
	List<String> sa_details = null;
	long updateTransazione = -1;
	List<String> transazione_details = null;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this.getConnection>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("getConnection:").append(this.getConnection);
		}
		if(this.insert>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insert:").append(this.insert);
		}
		if(this.update>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("update:").append(this.update);
		}
		if(this.sa_details!=null && !this.sa_details.isEmpty()) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			if(this.insert>=0) {
				sb.append("insertDetails:{");
			}
			else if(this.update>=0) {
				sb.append("updateDetails:{");
			}
			else {
				sb.append("details:{");
			}
			boolean first = true;
			for (String det : this.sa_details) {
				if(!first) {
					sb.append(" ");
				}
				sb.append(det);
				first=false;
			}
			sb.append("}");
		}
		if(this.updateTransazione>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("updateTransaction:").append(this.updateTransazione);
		}
		if(this.transazione_details!=null && !this.transazione_details.isEmpty()) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("updateTransactionDetails:{");
			boolean first = true;
			for (String det : this.transazione_details) {
				if(!first) {
					sb.append(" ");
				}
				sb.append(det);
				first=false;
			}
			sb.append("}");
		}
		return sb.toString();
	}
}
