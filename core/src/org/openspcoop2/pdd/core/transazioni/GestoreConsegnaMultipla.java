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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.utils.OpenSPCoopAppenderUtilities;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerService;
import org.openspcoop2.core.transazioni.utils.TransactionServerUtils;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.handlers.transazioni.ExceptionSerialzerFileSystem;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.utils.EsitiProperties;
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
			
	public void safeSave(TransazioneApplicativoServer transazioneApplicativoServer) {
		
		getConnectionAndSave(transazioneApplicativoServer, transazioneApplicativoServer.getProtocollo());
		
	}
	
	public void safeSave(org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico diagnostico) {
		
		getConnectionAndSave(diagnostico, diagnostico.getProtocollo());
		
	}
	
	public void safeSave(Messaggio dumpMessaggio ) {
		
		getConnectionAndSave(dumpMessaggio, dumpMessaggio.getProtocollo());
		
	}
	
	private void getConnectionAndSave(Object o, String protocol) {
		
		Resource dbResource = null;
		DBTransazioniManager dbManager = DBTransazioniManager.getInstance();
		IDSoggetto idDominio = openspcoopProperties.getIdentitaPortaDefault(protocol);
		ExceptionSerialzerFileSystem exceptionSerializerFileSystem = new ExceptionSerialzerFileSystem(this.log);
		Connection con = null;
		try{
			dbResource = dbManager.getResource(idDominio, ID_MODULO, null);
			con = (Connection) dbResource.getResource();	
			
			boolean autoCommit = false;
			con.setAutoCommit(autoCommit);
			
			if(o instanceof TransazioneApplicativoServer) {
				
				org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
						(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
								con, autoCommit,
								daoFactoryServiceManagerPropertiesTransazioni, daoFactoryLoggerTransazioni);
				jdbcServiceManager.getJdbcProperties().setShowSql(this.debug);
				ITransazioneApplicativoServerService transazioneService = jdbcServiceManager.getTransazioneApplicativoServerService();
				
				// ** Aggiorno campi dipendenti da questa invocazione **
				// Campi che non possono essere gestiti a livello 'core'
				
				TransazioneApplicativoServer transazioneApplicativoServer = (TransazioneApplicativoServer) o;
				
				// cluster id
				transazioneApplicativoServer.setClusterId(openspcoopProperties.getClusterId(false));
				
				// dettaglio esito
				EsitiProperties esitiProperties = EsitiProperties.getInstance(this.log, transazioneApplicativoServer.getProtocollo());
				if(transazioneApplicativoServer.getFault()!=null) {
					transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.ERRORE_APPLICATIVO));
				}
				else if(transazioneApplicativoServer.isConsegnaSuccesso()) {
					transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.OK));
				}
				else {
					if(transazioneApplicativoServer.getCodiceRisposta()!=null) {
						transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE));
					}
					else {
						transazioneApplicativoServer.setDettaglioEsito(esitiProperties.convertoToCode(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX));
					}
				}
								
				TransactionServerUtils.save(transazioneService, (TransazioneApplicativoServer)o);
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
						exceptionSerializerFileSystem.registrazioneFileSystemDumpEmessoPdD(messaggio, messaggio.getIdTransazione(), messaggio.getServizioApplicativoErogatore());
					}
				} catch (Exception eClose) {}
				// Effettuo il log anche nel core per evitare che un eventuale filtro a OFF sul core della PdD eviti la scrittura di questi errori
				String msg = "Errore durante la scrittura del messaggio relativo al server '"+messaggio.getServizioApplicativoErogatore()+"' associato alla transazione sul database: " + e.getLocalizedMessage();
				this.log.error("["+messaggio.getIdTransazione()+"] "+msg,e);
			}

		}finally{
			try{
				con.setAutoCommit(true);
			}catch(Exception eRollback){}
			try {
				dbManager.releaseResource(idDominio, ID_MODULO, dbResource);
			} catch (Exception e) {}
		}
	}
	
}
