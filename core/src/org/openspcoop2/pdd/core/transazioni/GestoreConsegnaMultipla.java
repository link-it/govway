/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerService;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCTransazioneService;
import org.openspcoop2.core.transazioni.utils.TransactionServerUtils;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.handlers.transazioni.ExceptionSerialzerFileSystem;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
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
	private static ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
	private static Logger daoFactoryLoggerTransazioni = null;
	private static Logger daoFactoryLoggerTransazioniSql = null;

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

				DAOFactoryProperties daoFactoryProperties = null;
				daoFactoryLoggerTransazioni = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(debug);
				daoFactoryLoggerTransazioniSql = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSql(debug);
				daoFactory = DAOFactory.getInstance(daoFactoryLoggerTransazioniSql);
				daoFactoryProperties = DAOFactoryProperties.getInstance(daoFactoryLoggerTransazioniSql);
				daoFactoryServiceManagerPropertiesTransazioni = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				daoFactoryServiceManagerPropertiesTransazioni.setShowSql(debug);	
				daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());

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






	private Logger log = null;
	private Logger logSql = null;
	//	private String datasource = null;
	private String tipoDatabase = null;
	private boolean debug = false;

	private IDiagnosticProducer msgDiagnosticiOpenSPCoopAppender = null;
	private IDumpProducer dumpOpenSPCoopAppender = null;


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

		}catch(Exception e){
			throw new TransactionMultiDeliverException("Errore durante l'inizializzazione del DumpAppender: "+e.getMessage(),e);
		} 

	}


	// *** SAFE ***

	public void safeCreate(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPA) {

		// cluster id
		transazioneApplicativoServer.setClusterIdPresaInCarico(openspcoopProperties.getClusterId(false));
		
		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo(), false, false, idPA);

	}
	
	public void safeUpdateConsegna(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPA) {

		// cluster id
		transazioneApplicativoServer.setClusterIdConsegna(openspcoopProperties.getClusterId(false));
		
		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo(), true, true, idPA); // l'informazione dovrebbe esistere!

	}
	
	public void safeUpdatePrelievoIM(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPA) {

		// cluster id
		transazioneApplicativoServer.setClusterIdPrelievoIm(openspcoopProperties.getClusterId(false));
		
		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo(), true, false, idPA);

	}
	
	public void safeUpdateEliminazioneIM(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPA) {

		// cluster id
		transazioneApplicativoServer.setClusterIdEliminazioneIm(openspcoopProperties.getClusterId(false));
		
		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo(), true, false, idPA);

	}
	
	public void safeUpdateMessaggioScaduto(TransazioneApplicativoServer transazioneApplicativoServer, IDPortaApplicativa idPA) {

		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo(), true, false, idPA);

	}

	
	public void safeSave(org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico diagnostico, IDPortaApplicativa idPA) {

		getConnectionAndSave(diagnostico, diagnostico.getProtocollo(), false, false, idPA);

	}

	public void safeSave(Messaggio dumpMessaggio, IDPortaApplicativa idPA) {

		getConnectionAndSave(dumpMessaggio, dumpMessaggio.getProtocollo(), false, false, idPA);

	}

	private void getConnectionAndSave(Object o, String protocol, boolean update, boolean throwNotFoundIfNotExists, IDPortaApplicativa idPA) {

		EsitiProperties esitiProperties = null;
		try {
			esitiProperties = EsitiProperties.getInstance(this.log, protocol);
		}catch(Throwable e) {
			this.log.error("Errore avvenuto durante la lettura del gestore degli esiti: "+e.getMessage() ,e);
		}
		if(esitiProperties==null) {
			return;
		}
		
		try{
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(); 
			Tracciamento configTracciamento = configPdDManager.getOpenSPCoopAppender_Tracciamento();
			StringBuilder bf = new StringBuilder();
			String esitiConfig = configTracciamento!=null ? configTracciamento.getEsiti() : null;
			if(idPA!=null) {
				PortaApplicativa pa = configPdDManager.getPortaApplicativa_SafeMethod(idPA);
				if(pa!=null && pa.getTracciamento()!=null && pa.getTracciamento().getEsiti()!=null) {
					esitiConfig = pa.getTracciamento().getEsiti();
				}
			}
			List<String> esitiDaRegistrare = EsitiConfigUtils.getRegistrazioneEsiti(esitiConfig, this.log, bf);
			
			int code = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA);
			String codeAsString = code+"";
			if(esitiDaRegistrare!=null && !esitiDaRegistrare.isEmpty() && esitiDaRegistrare.contains(codeAsString)==false){
				this.log.debug("Non devo registrare l'informazione, poichè la transazione capostipite non è stata salvata");
			}
			
		}catch(Throwable e) {
			this.log.debug("Errore avvenuto durante la lettura della configurazione delle transazioni da salvare: "+e.getMessage(),e); 
		}
		
		Resource dbResource = null;
		DBTransazioniManager dbManager = DBTransazioniManager.getInstance();
		IDSoggetto idDominio = openspcoopProperties.getIdentitaPortaDefault(protocol);
		ExceptionSerialzerFileSystem exceptionSerializerFileSystem = new ExceptionSerialzerFileSystem(this.log);
		Connection con = null;
		boolean isMessaggioConsegnato = false;
		TransazioneApplicativoServer transazioneApplicativoServer = null;
		try{
			dbResource = dbManager.getResource(idDominio, ID_MODULO, null);
			con = (Connection) dbResource.getResource();	

			boolean autoCommit = false;
			con.setAutoCommit(autoCommit);

			if(o instanceof TransazioneApplicativoServer) {

				org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
						(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
								con, autoCommit,
								daoFactoryServiceManagerPropertiesTransazioni, daoFactoryLoggerTransazioniSql);
				jdbcServiceManager.getJdbcProperties().setShowSql(this.debug);
				ITransazioneApplicativoServerService transazioneService = jdbcServiceManager.getTransazioneApplicativoServerService();

				// ** Aggiorno campi dipendenti da questa invocazione **
				// Campi che non possono essere gestiti a livello 'core'

				transazioneApplicativoServer = (TransazioneApplicativoServer) o;

				// dettaglio esito
				this.setDettaglioEsito(transazioneApplicativoServer, esitiProperties);
				
				// consegna terminata
				if(transazioneApplicativoServer.isConsegnaTerminata()) {
					isMessaggioConsegnato = true;
				}
				else if(transazioneApplicativoServer.getDataEliminazioneIm()!=null) {
					isMessaggioConsegnato = true;
				}
				else if(transazioneApplicativoServer.getDataMessaggioScaduto()!=null) {
					isMessaggioConsegnato = true;
				}

				boolean transazioneAggiornata = TransactionServerUtils.save(transazioneService, (TransazioneApplicativoServer)o, update, throwNotFoundIfNotExists);
				if(!transazioneAggiornata) {
					isMessaggioConsegnato = false; // per gestire eventuali errori durante il  recupero da file system
				}
			}
			else if(o instanceof org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico) {

				this.msgDiagnosticiOpenSPCoopAppender.log(con, (org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico)o);

			}
			else if(o instanceof Messaggio) {

				this.dumpOpenSPCoopAppender.dump(con, (Messaggio)o);

			}

			con.commit();
		}catch(Throwable e){
			try{
				con.rollback();
			}catch(Exception eRollback){}

			if(o instanceof TransazioneApplicativoServer) {
				TransazioneApplicativoServer serverInfo = (TransazioneApplicativoServer) o;
				try{
					if(serverInfo.getIdTransazione()!=null && serverInfo.getServizioApplicativoErogatore()!=null) {
						// NOTA: volutamente salvo serverInfo per poter reimplementare la logica di cui sopra
						serverInfo.setProtocollo(protocol);
						exceptionSerializerFileSystem.registrazioneFileSystemTransazioneApplicativoServerEmessoPdD(serverInfo, serverInfo.getIdTransazione(), serverInfo.getServizioApplicativoErogatore());
					}
				} catch (Exception eClose) {}
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
				} catch (Exception eClose) {}
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
				} catch (Exception eClose) {}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura del messaggio relativo al server '"+messaggio.getServizioApplicativoErogatore()+"' associato alla transazione sul database: " + e.getLocalizedMessage();
				this.log.error("["+messaggio.getIdTransazione()+"] "+msg,e);
			}

		}finally{
			
			if(isMessaggioConsegnato) {

				// aggiorno esito transazione
				try{
					if(isMessaggioConsegnato){
						aggiornaInformazioneConsegnaTerminata(transazioneApplicativoServer, con, esitiProperties);
					}
				}catch(Throwable e){
					String msg = "Errore durante l'aggiornamento delle transazione relativamente all'informazione del server '"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"': " + e.getLocalizedMessage();
					this.log.error("["+transazioneApplicativoServer.getIdTransazione()+"] "+msg,e);
				}
			}
			
			try{
				con.setAutoCommit(true);
			}catch(Exception eRollback){}
			try {
				dbManager.releaseResource(idDominio, ID_MODULO, dbResource);
			} catch (Exception e) {}
		}


	}

	private void setDettaglioEsito(TransazioneApplicativoServer transazioneApplicativoServer, EsitiProperties esitiProperties) throws ProtocolException {
		
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
					}catch(Exception e) {}
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
							transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE));
						}
					}
					else {
						// altro tipo di codice
						if(transazioneApplicativoServer.isConsegnaTerminata()) {
							transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.OK));
						}
						else {
							transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE));
						}
					}
				}
				else {
					// senza codice di risposta
					if(transazioneApplicativoServer.isConsegnaTerminata()) {
						transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.OK));
					}
					else {
						transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE));
					}
				}
			}
		}
		
	}


	private void aggiornaInformazioneConsegnaTerminata(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			EsitiProperties esitiProperties) throws CoreException {
		
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
			// già effettuato fuori dal metodo connectionDB.setAutoCommit(false);
			JDBCUtilities.setTransactionIsolationSerializable(this.tipoDatabase, connectionDB);
		} catch(Exception er) {
			throw new CoreException("(setIsolation) "+er.getMessage(),er);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean updateEffettuato = false;
		
		long scadenzaWhile = DateManager.getTimeMillis() + openspcoopProperties.getGestioneSerializableDB_AttesaAttiva();

		int esitoConsegnaMultipla = -1;
		int esitoConsegnaMultiplaFallita = -1;
		int esitoConsegnaMultiplaCompletata = -1;
		int ok = -1;
		try {
			esitoConsegnaMultipla = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA);
			esitoConsegnaMultiplaFallita = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_FALLITA);
			esitoConsegnaMultiplaCompletata = esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA_COMPLETATA);
			ok = esitiProperties.convertoToCode(EsitoTransazioneName.OK);
		}catch(Exception er) {
			throw new CoreException("(readEsiti) "+er.getMessage(),er);
		}
		
		CoreException coreException = null;
		
		while(updateEffettuato==false && DateManager.getTimeMillis() < scadenzaWhile){

			try{	
				
				org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
						(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
								connectionDB, false,
								daoFactoryServiceManagerPropertiesTransazioni, daoFactoryLoggerTransazioniSql);
				jdbcServiceManager.getJdbcProperties().setShowSql(this.debug);
				JDBCTransazioneService transazioneService = (JDBCTransazioneService) jdbcServiceManager.getTransazioneService();
				
				transazioneService.enableSelectForUpdate();
				
				IPaginatedExpression pagExpression = transazioneService.newPaginatedExpression();
				pagExpression.equals(Transazione.model().ID_TRANSAZIONE, transazioneApplicativoServer.getIdTransazione());
				List<Map<String, Object>> l = null;
				try {
					l = transazioneService.select(pagExpression, Transazione.model().ESITO, Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO);
				}catch (NotFoundException notfound) {}
				if(l!=null && l.size()>0) {
					if(l.size()>1) {
						coreException = new CoreException("Trovata più di una transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"'");
					}
					else {
						
						Object oEsito = l.get(0).get(Transazione.model().ESITO.getFieldName());
						int esito = -1;
						if(oEsito!=null && oEsito instanceof Integer) {
							esito = (Integer) oEsito;
						}
						
						Object oConsegneMultiple = l.get(0).get(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO.getFieldName());
						int consegneMultiple = -1;
						if(oConsegneMultiple!=null && oConsegneMultiple instanceof Integer) {
							consegneMultiple = (Integer) oConsegneMultiple;
						}
						
						if(esitoConsegnaMultipla == esito || esitoConsegnaMultiplaFallita == esito) {
							int decrement = consegneMultiple-1;
							UpdateField uFieldConsegneMultipleInCorso = new UpdateField(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO, decrement);
							UpdateField uFieldEsito = null;
							if(esitoConsegnaMultipla == esito) {
								// non appena c'è un errore, marca la transazione come fallita
								if(ok != transazioneApplicativoServer.getDettaglioEsito() || transazioneApplicativoServer.getDataMessaggioScaduto()!=null) {
									uFieldEsito = new UpdateField(Transazione.model().ESITO, esitoConsegnaMultiplaFallita);
								}
								else if(decrement<=0) {
									uFieldEsito = new UpdateField(Transazione.model().ESITO, esitoConsegnaMultiplaCompletata);
								}
							}
							if(uFieldEsito!=null) {
								transazioneService.updateFields(transazioneApplicativoServer.getIdTransazione(), uFieldConsegneMultipleInCorso, uFieldEsito);
							}
							else {
								transazioneService.updateFields(transazioneApplicativoServer.getIdTransazione(), uFieldConsegneMultipleInCorso);
							}
						}
						else {
							coreException = new CoreException("Trovata transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"', con un esito '"+esito+"' non atteso");
						}

						
					}
				}

				transazioneService.disableSelectForUpdate();
				
				// Chiusura Transazione
				connectionDB.commit();

				// ID Costruito
				updateEffettuato = true;

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

			if(updateEffettuato == false){
				// Per aiutare ad evitare conflitti
				try{
					Utilities.sleep((new java.util.Random()).nextInt(openspcoopProperties.getGestioneSerializableDB_CheckInterval())); // random da 0ms a checkIntervalms
				}catch(Exception eRandom){}
			}
		}
		// Ripristino Transazione
		try{
			connectionDB.setTransactionIsolation(oldTransactionIsolation);
			// già effettuato fuori dal metodo connectionDB.setAutoCommit(true);
		} catch(Exception er) {
			throw new CoreException("(ripristinoIsolation) "+er.getMessage(),er);
		}

		if(coreException!=null) {
			throw coreException;
		}
	}
}
