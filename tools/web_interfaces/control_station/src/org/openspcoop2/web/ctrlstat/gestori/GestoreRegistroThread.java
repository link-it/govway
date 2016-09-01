/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.ctrlstat.gestori;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdAccordoCooperazione;
import org.openspcoop2.core.registry.IdAccordoServizioParteComune;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdPortaDominio;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.ws.client.accordocooperazione.all.AccordoCooperazioneSoap11Service;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.all.AccordoServizioParteComuneSoap11Service;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.all.AccordoServizioParteSpecificaSoap11Service;
import org.openspcoop2.core.registry.ws.client.portadominio.all.PortaDominioSoap11Service;
import org.openspcoop2.core.registry.ws.client.soggetto.all.SoggettoSoap11Service;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.resources.ExceptionListenerJMS;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.config.DatasourceProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationCoreException;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.core.QueueManager;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.OperationsParameter;
import org.openspcoop2.web.ctrlstat.costanti.TipoOggettoDaSmistare;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.queue.ClassQueue;
import org.openspcoop2.web.lib.queue.ClassQueueException;
import org.openspcoop2.web.lib.queue.costanti.OperationStatus;
import org.openspcoop2.web.lib.queue.costanti.Operazione;
import org.openspcoop2.web.lib.queue.costanti.TipoOperazione;
import org.openspcoop2.web.lib.queue.dao.FilterParameter;
import org.openspcoop2.web.lib.queue.dao.Operation;
import org.openspcoop2.web.lib.queue.dao.Parameter;

/**
 * GestoreRegistroThread
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GestoreRegistroThread extends GestoreGeneral {

	private ConsoleProperties consoleProperties;
	private DatasourceProperties datasourceProperties;
	
	private String name = null;
	
	/** run */
	public boolean stop = false;
	private boolean isRunning = false;
	public boolean isRunning() {
		return this.isRunning;
	}
	
	/** Gestore Registro Servizi */
	private SoggettiCore soggettiCore;
	private PddCore pddCore;
	private AccordiServizioParteComuneCore apcCore;
	private AccordiServizioParteSpecificaCore apsCore;
	private AccordiCooperazioneCore acCore;

	// JMS
	private QueueReceiver receiver = null;
	private Queue queue = null;
	private QueueConnectionFactory qcf = null;
	private QueueConnection qc = null;
	private QueueSession qs = null;

	// //DB
	private DBManager dbm;
	private Connection con;

	/** Web Service */
	private org.openspcoop2.core.registry.ws.client.accordocooperazione.all.AccordoCooperazioneSoap11Service accordoCooperazioneService;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.all.AccordoServizioParteComuneSoap11Service accordoServizioParteComuneService;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.all.AccordoServizioParteSpecificaSoap11Service accordoServizioParteSpecificaService;
	private org.openspcoop2.core.registry.ws.client.soggetto.all.SoggettoSoap11Service soggettoService;
	private org.openspcoop2.core.registry.ws.client.portadominio.all.PortaDominioSoap11Service pddService;
	
	private org.openspcoop2.core.registry.ws.client.accordocooperazione.all.AccordoCooperazione accordoCooperazionePort;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.all.AccordoServizioParteComune accordoServizioParteComunePort;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.all.AccordoServizioParteSpecifica accordoServizioParteSpecificaPort;
	private org.openspcoop2.core.registry.ws.client.soggetto.all.Soggetto soggettoPort;
	private org.openspcoop2.core.registry.ws.client.portadominio.all.PortaDominio pddPort;

	private boolean singlePdD = false;

	// Logger
	private Logger log;

	private ExceptionListenerJMS exceptionListenerJMS = new ExceptionListenerJMS();

	/** Costruttore 
	 * @throws OpenSPCoop2ConfigurationException */
	public GestoreRegistroThread() throws OpenSPCoop2ConfigurationException {
		
		this.log = ControlStationLogger.getGestoreRegistroLogger();
		this.name = "GestoreRegistro";
		
		this.consoleProperties = ConsoleProperties.getInstance();
		this.datasourceProperties = DatasourceProperties.getInstance();
	}

	/**
	 * Metodo che fa partire il Thread.
	 * 
	 * @since 0.4
	 */
	@Override
	public void run() {

		this.isRunning = true;
		
		// effettuo inizializzazione
		try {
			this.initGestore();

			if (this.singlePdD) {
				this.log.warn("GestoreRegistroServizi non avviato: pddConsole avviata in singlePdD mode.");
				return;
			}

			this.dbm = DBManager.getInstance();

			this.log.info(this.getClass().getName() + " Inizializzato ...");
		} catch (GestoreNonAttivoException e) {
			this.log.warn("Inizializzazione Gestore non effettuata : " + e.getMessage());
			this.stop = true;
		} catch (Exception e) {
			this.log.error("Inizializzazione Gestore [" + this.getClass().getName() + "] Fallita : " + e.getMessage(), e);
			this.stop = true;
		}

		// Avvio Gestione Registro
		boolean riconnessioneConErrore = false;
		while (this.stop == false) {
			Operation operation = null;
			ClassQueue operationManager = null;
			try {

				// riconnessione precedente non riuscita.....
				if (riconnessioneConErrore) {
					throw new JMSException("RiconnessioneJMS non riuscita...");
				}

				// Controllo ExceptionListenerJMS
				if (this.exceptionListenerJMS.isConnessioneCorrotta()) {
					this.log.error("ExceptionJMSListener ha rilevato una connessione jms corrotta", this.exceptionListenerJMS.getException());
					throw new JMSException("ExceptionJMSListener ha rilevato una connessione jms corrotta: " + this.exceptionListenerJMS.getException().getMessage());
				}

				this.log.info("GestoreRegistro: Ricezione operazione...");
				ObjectMessage richiesta = null;
				while (this.stop == false) {
					richiesta = (ObjectMessage) this.receiver.receive(CostantiControlStation.INTERVALLO_RECEIVE);
					if (richiesta != null) {
						break;
					}
				}
				if (this.stop == true) {
					break;
				}

				// Attendo tempi di delay (TransazioneSimilXA)
				try {
					Thread.sleep(CostantiControlStation.INTERVALLO_TRANSAZIONE_XA);
				} catch (InterruptedException e) {
				}

				// Ricezione Operazione
				Object objOp;
				try {
					objOp = (Object) richiesta.getObject();
				} catch (Exception e) {
					this.log.error("GestoreRegistro: Ricevuta richiesta con tipo errato:" + e.toString());
					this.qs.commit();
					continue;
				}
				String idOperazione = richiesta.getStringProperty("ID");

				int idOp = (int) Integer.parseInt(objOp.toString());
				if (idOp == 0) {
					this.log.error("GestoreRegistro: Ricevuta richiesta con parametri scorretti.");
					this.qs.commit();
					continue;
				}

				this.log.info(CostantiControlStation.OPERATIONS_DELIMITER+"GestoreRegistro: Ricevuta richiesta di operazione con ID: " + idOperazione + " id_operation [" + idOp + "]");

				// Connessione al db
				this.con = this.dbm.getConnection();

				// Prendo i dati dell'operazione
				String tipoOperazioneParam = "", tipoOperazioneCRUDParam = "", singleSu = "";
				int deleted = 0;
				operationManager = new ClassQueue(this.con, this.datasourceProperties.getTipoDatabase());
				try {
					operation = operationManager.getOperation(idOp);
				} catch (Exception e) {
					this.log.error("Impossibile recuperare l'operation con id=" + idOp, e);
					this.qs.rollback();
					this.dbm.releaseConnection(this.con);
					continue;
				}
				tipoOperazioneParam = operation.getTipo();
				tipoOperazioneCRUDParam = operation.getOperation();
				singleSu = operation.getSuperUser();
				deleted = operation.isDeleted() ? 1 : 0;

				// Operazione in gestione
				GestioneOperazione operazioneInGestione = new GestioneOperazione(this.dbm, this.con, this.qs, operation, operationManager, 
						this.log, tipoOperazioneCRUDParam, "GestoreRegistro");

				// Oggetto in Gestione
				String oggettoDaSmistare = operation.getParameterValue(OperationsParameter.OGGETTO.getNome());
				if (oggettoDaSmistare == null || "".equals(oggettoDaSmistare)) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (oggetto:" + oggettoDaSmistare + ") non valida");
					continue;
				}
				TipoOggettoDaSmistare tipoOggettoDaSmistare = null;
				try{
					tipoOggettoDaSmistare = TipoOggettoDaSmistare.valueOf(oggettoDaSmistare);
					if(tipoOggettoDaSmistare==null){
						throw new Exception("Conversione in Enumeration non riuscita");
					}
				}catch(Exception e){
					this.log.error("Ricevuta operazione con parametri scorretti (oggetto:" + oggettoDaSmistare + ") non valida: "+e.getMessage(), e);
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (oggetto:" + oggettoDaSmistare + ") non valida: "+e.getMessage());
					continue;
				}

				// Check valori operazioni
				if ((tipoOperazioneParam == null) || tipoOperazioneParam.equals("") || (tipoOperazioneCRUDParam == null) || tipoOperazioneCRUDParam.equals("") || (singleSu == null) || singleSu.equals("")) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti :" + operation.toString());
					continue;
				}
				
				// Operazione CRUD		
				Operazione tipoOperazioneCRUD = null;
				try{
					tipoOperazioneCRUD = Operazione.valueOf(tipoOperazioneCRUDParam);
					if(tipoOperazioneCRUD==null){
						throw new Exception("Conversione in Enumeration non riuscita");
					}
				}catch(Exception e){
					this.log.error("Ricevuta operazione con parametri scorretti (tipoOperazioneCRUD:" + tipoOperazioneCRUDParam + ") non valida: "+e.getMessage(), e);
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (tipoOperazioneCRUD:" + tipoOperazioneCRUDParam + ") non valida: "+e.getMessage());
					continue;
				}
				
				// Operazione Queue
				TipoOperazione tipoOperazione = null;
				try{
					tipoOperazione = TipoOperazione.valueOf(tipoOperazioneParam);
					if(tipoOperazione==null){
						throw new Exception("Conversione in Enumeration non riuscita");
					}
				}catch(Exception e){
					this.log.error("Ricevuta operazione con parametri scorretti (tipoOperazione:" + tipoOperazioneParam + ") non valida: "+e.getMessage(), e);
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (tipoOperazione:" + tipoOperazioneParam + ") non valida: "+e.getMessage());
					continue;
				}
				
				this.log.debug("Ricevuta Operation:" + operation.toString());

				// ID Table
				int idTable = -1;
				String idTableString = null;
				try {
					idTableString = operation.getParameterValue(OperationsParameter.ID_TABLE.getNome());
					idTable = Integer.parseInt(idTableString);
					if (idTable <= 0)
						throw new Exception("IDTable <= 0");
				} catch (Exception e) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (idTable[" + idTableString + "]) non valida: " + e.getMessage());
					continue;
				}

				
				if (!TipoOggettoDaSmistare.soggetto.equals(tipoOggettoDaSmistare) && 
						//!TipoOggettoDaSmistare.fruitore.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.servizio.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.accordo.equals(tipoOggettoDaSmistare) &&
						!TipoOggettoDaSmistare.accordoCooperazione.equals(tipoOggettoDaSmistare) &&
						!TipoOggettoDaSmistare.pdd.equals(tipoOggettoDaSmistare)) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (oggetto:" + tipoOggettoDaSmistare.name() + ")");
					continue;
				}
				
				if (!TipoOperazione.webService.equals(tipoOperazione)) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (tipo operazione:" + tipoOperazione.name() + " non supportata dal gestore)");
					continue;
				}


				/* ----- filtro per mantenimento ordine operazioni ----- */
				Vector<FilterParameter> filtroOrdine = new Vector<FilterParameter>();

				FilterParameter idTableFiltro = new FilterParameter();
				idTableFiltro.addFilterParameter(new Parameter(OperationsParameter.ID_TABLE.getNome(), idTable + ""));
				filtroOrdine.add(idTableFiltro);

				if (TipoOggettoDaSmistare.pdd.equals(tipoOggettoDaSmistare)) {

					// Chiave primaria per il soggetto e' nome
					String nomePddFiltro = operation.getParameterValue(OperationsParameter.PORTA_DOMINIO.getNome());
					FilterParameter asPdd = new FilterParameter();
					asPdd.addFilterParameter(new Parameter(OperationsParameter.PORTA_DOMINIO.getNome(), nomePddFiltro));
					filtroOrdine.add(asPdd);

				} else if (TipoOggettoDaSmistare.soggetto.equals(tipoOggettoDaSmistare)) {

					// Chiave primaria per il soggetto e' tipo e nome
					String tipoSoggFiltro = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
					String nomeSoggFiltro = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
					FilterParameter soggettoFiltro = new FilterParameter();
					soggettoFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
					soggettoFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
					filtroOrdine.add(soggettoFiltro);

					// OLD valori per tipo e nome se abbiamo una operazione di
					// change
					if (Operazione.change.equals(tipoOperazioneCRUD)) {
						String oldTipoSogg = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSogg = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());
						FilterParameter soggettoOldFiltro = new FilterParameter();
						soggettoOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SOGGETTO.getNome(), oldTipoSogg));
						soggettoOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SOGGETTO.getNome(), oldNomeSogg));
						filtroOrdine.add(soggettoOldFiltro);
					}
				} else if (TipoOggettoDaSmistare.servizio.equals(tipoOggettoDaSmistare) 
					//	|| TipoOggettoDaSmistare.fruitore.equals(tipoOggettoDaSmistare)
						) {

					// Chiave primaria che identifica un servizio e'
					// soggettoErogatore e servizio
					String tipoSoggFiltro = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
					String nomeSoggFiltro = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
					String tipoServFiltro = operation.getParameterValue(OperationsParameter.TIPO_SERVIZIO.getNome());
					String nomeServFiltro = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO.getNome());
					String nomeAccordo = operation.getParameterValue(OperationsParameter.NOME_ACCORDO.getNome());
					String versioneAccordo = operation.getParameterValue(OperationsParameter.VERSIONE_ACCORDO.getNome());
					
					//System.out.println("SERVIZIO  TS["+tipoSoggFiltro+"] NS["+nomeSoggFiltro+"] TSV["+tipoServFiltro+"] NSV["+nomeServFiltro+"]");
					
					FilterParameter servizioFiltro = new FilterParameter();
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SERVIZIO.getNome(), tipoServFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SERVIZIO.getNome(), nomeServFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_ACCORDO.getNome(), nomeAccordo));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.VERSIONE_ACCORDO.getNome(), versioneAccordo));
					filtroOrdine.add(servizioFiltro);

					// OLD valori per tipo e nome se abbiamo una operazione di
					// change del servizio o add/del di fruitore
					if ((Operazione.change.equals(tipoOperazioneCRUD)) 
						//	|| (TipoOggettoDaSmistare.fruitore.equals(tipoOggettoDaSmistare))
							) {
						String oldtipoServizio = operation.getParameterValue(OperationsParameter.OLD_TIPO_SERVIZIO.getNome());
						String oldNomeServizio = operation.getParameterValue(OperationsParameter.OLD_NOME_SERVIZIO.getNome());

						String oldTipoSogg = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSogg = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());
						
						String oldNomeAccordo = operation.getParameterValue(OperationsParameter.OLD_NOME_ACCORDO.getNome());
						String oldVersioneAccordo = operation.getParameterValue(OperationsParameter.OLD_VERSIONE_ACCORDO.getNome());

						FilterParameter servizioOldFiltro = new FilterParameter();
						if( (oldTipoSogg!=null && oldNomeSogg!=null) || (oldtipoServizio!=null && oldNomeServizio!=null) || oldNomeAccordo!=null || oldVersioneAccordo!=null ){
							if(oldTipoSogg!=null && oldNomeSogg!=null){
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), oldTipoSogg));
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), oldNomeSogg));
							}else{
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
							}
							if(oldtipoServizio!=null && oldNomeServizio!=null){
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SERVIZIO.getNome(), oldtipoServizio));
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SERVIZIO.getNome(), oldNomeServizio));
							}else{
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SERVIZIO.getNome(), tipoServFiltro));
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SERVIZIO.getNome(), nomeServFiltro));
							}
							if(oldNomeAccordo!=null){
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_ACCORDO.getNome(), oldNomeAccordo));
							}
							else{
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_ACCORDO.getNome(), nomeAccordo));
							}
							if(oldVersioneAccordo!=null){
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_VERSIONE_ACCORDO.getNome(), oldVersioneAccordo));
							}
							else{
								servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_VERSIONE_ACCORDO.getNome(), versioneAccordo));
							}
							filtroOrdine.add(servizioOldFiltro);
						}
					}
				} else if (TipoOggettoDaSmistare.accordo.equals(tipoOggettoDaSmistare)) {

					// Chiave primaria che identifica l'accordo e' il nome
					String nomeAccFiltro = operation.getParameterValue(OperationsParameter.NOME_ACCORDO.getNome());
					String versioneAccFiltro = operation.getParameterValue(OperationsParameter.VERSIONE_ACCORDO.getNome());
					String tipoReferenteAccFiltro = operation.getParameterValue(OperationsParameter.TIPO_REFERENTE.getNome());
					String nomeReferenteAccFiltro = operation.getParameterValue(OperationsParameter.NOME_REFERENTE.getNome());
					//System.out.println("ACCORDO  ["+nomeAccFiltro+"] ["+versioneAccFiltro+"] ["+tipoReferenteAccFiltro+"] ["+nomeReferenteAccFiltro+"]");
					
					FilterParameter asFiltro = new FilterParameter();
					asFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_ACCORDO.getNome(), nomeAccFiltro));
					asFiltro.addFilterParameter(new Parameter(OperationsParameter.VERSIONE_ACCORDO.getNome(), versioneAccFiltro));
					asFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_REFERENTE.getNome(), tipoReferenteAccFiltro));
					asFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_REFERENTE.getNome(), nomeReferenteAccFiltro));
					filtroOrdine.add(asFiltro);
					
					// OLD valori se siamo in una operazione di 
					// change dell'accordo
					if ((Operazione.change.equals(tipoOperazioneCRUD))){
						// Chiave primaria che identifica l'accordo e' il nome,soggettoreferente,versione
						String nomeOLDAccFiltro = operation.getParameterValue(OperationsParameter.OLD_NOME_ACCORDO.getNome());
						String versioneOLDAccFiltro = operation.getParameterValue(OperationsParameter.OLD_VERSIONE_ACCORDO.getNome());
						String tipoReferenteOLDAccFiltro = operation.getParameterValue(OperationsParameter.OLD_TIPO_REFERENTE.getNome());
						String nomeReferenteOLDAccFiltro = operation.getParameterValue(OperationsParameter.OLD_NOME_REFERENTE.getNome());
						//System.out.println("OLD ACCORDO  ["+nomeOLDAccFiltro+"] ["+versioneOLDAccFiltro+"] ["+tipoReferenteOLDAccFiltro+"] ["+nomeReferenteOLDAccFiltro+"]");
						
						FilterParameter asOLDFiltro = new FilterParameter();
						asOLDFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_ACCORDO.getNome(), nomeOLDAccFiltro));
						asOLDFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_VERSIONE_ACCORDO.getNome(), versioneOLDAccFiltro));
						asOLDFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_REFERENTE.getNome(), tipoReferenteOLDAccFiltro));
						asOLDFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_REFERENTE.getNome(), nomeReferenteOLDAccFiltro));
						filtroOrdine.add(asOLDFiltro);
					}
				}else if (TipoOggettoDaSmistare.accordoCooperazione.equals(tipoOggettoDaSmistare)) {
					// Chiave primaria che identifica l'accordo e' il nome,versione
					String nomeAccFiltro = operation.getParameterValue(OperationsParameter.NOME_ACCORDO.getNome());
					String versioneAccFiltro = operation.getParameterValue(OperationsParameter.VERSIONE_ACCORDO.getNome());
					//System.out.println("ACCORDO COOPERAZIONE  ["+nomeAccFiltro+"] ["+versioneAccFiltro+"]");
						
					FilterParameter acFiltro = new FilterParameter();
					acFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_ACCORDO.getNome(), nomeAccFiltro));
					acFiltro.addFilterParameter(new Parameter(OperationsParameter.VERSIONE_ACCORDO.getNome(), versioneAccFiltro));
					filtroOrdine.add(acFiltro);
						
					// OLD valori se siamo in una operazione di 
					// change dell'accordo
					if ((Operazione.change.equals(tipoOperazioneCRUD))){
						// Chiave primaria che identifica l'accordo e' il nome,versione
						String nomeOLDAccFiltro = operation.getParameterValue(OperationsParameter.OLD_NOME_ACCORDO.getNome());
						String versioneOLDAccFiltro = operation.getParameterValue(OperationsParameter.OLD_VERSIONE_ACCORDO.getNome());
						//System.out.println("OLD ACCORDO COOPERAZIONE  ["+nomeOLDAccFiltro+"] ["+versioneOLDAccFiltro+"]");
						
						FilterParameter asOLDFiltro = new FilterParameter();
						asOLDFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_ACCORDO.getNome(), nomeOLDAccFiltro));
						asOLDFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_VERSIONE_ACCORDO.getNome(), versioneOLDAccFiltro));
						filtroOrdine.add(asOLDFiltro);
					}
				}else {
					operazioneInGestione.invalid("Ricevuta operazione con oggetto non conosciuto dal Gestore (oggetto:" + tipoOggettoDaSmistare.name() + ")");
					continue;
				}

				operationManager.setOperazioniPrecedentiByFilterSearch(operation, filtroOrdine.toArray(new FilterParameter[0]), false, tipoOggettoDaSmistare.name());
				if (operation.sizeOperazioniPrecedentiAncoraDaGestireList() > 0) {
					StringBuffer operazioniPrecedenti = new StringBuffer();
					for (int i = 0; i < operation.sizeOperazioniPrecedentiAncoraDaGestireList(); i++) {
						operazioniPrecedenti.append("\n" + operation.getOperazionePrecedenteAncoraDaGestire(i).toString());
					}
					operazioneInGestione.error("Ricevuta operazione su una entita' di cui esistono operazioni attivate precedentementi ancora in coda:\n" + operazioniPrecedenti.toString());
					continue;
				}

				/* ----- Fine filtro per mantenimento ordine operazioni ----- */

				// Se l'operazione e' stata marcata come deleted, la rimuovo
				// dalla coda (basta fare il commit)
				String statoOperazioneCancellata = "";
				if (deleted == 1) {

					operazioneInGestione.delete();
					statoOperazioneCancellata = " (L'operazione non e' stata propagata, possiede una stato 'deleted')";

				} else {

					// Effettuo filtro di altre modifiche con stesso
					// idOperazione
					if (Operazione.change.equals(tipoOperazioneCRUD)) {
						
						List<Operation> listFilteredOperations = filterOperations(idOperazione, operationManager, operation, this.qs, this.queue, this.log);

						if (listFilteredOperations.size() > 0) {
							StringBuffer bf = new StringBuffer();
							for (Operation operationFiltered : listFilteredOperations) {
								if(bf.length()>0){
									bf.append(",");
								}
								bf.append(operationFiltered.getId());
							}
							this.log.debug(getName() + ": Filtrate [" + listFilteredOperations.size() + "] operation con stessa property jms ['ID'="+idOperazione+"] (ids: "+bf.toString()+")");
						}
					}

					/*  SOGGETTI */
					if (TipoOggettoDaSmistare.soggetto.equals(tipoOggettoDaSmistare)) {

						String oldTipoSogg = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSogg = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());
						String tipoSogg = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSogg = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());

						// CREAZIONE/MODIFICA
						if (Operazione.change.equals(tipoOperazioneCRUD) || Operazione.add.equals(tipoOperazioneCRUD)) {

							// Ottengo nuova immagine del soggetto
							Soggetto soggetto = null;
							try {
								soggetto = this.soggettiCore.getSoggettoRegistro(idTable);
								this.log.debug("Caricato soggetto :" + soggetto.getNome());
							} catch (DriverRegistroServiziNotFound de) {
								operazioneInGestione.waitBeforeInvalid("Soggetto non esistente nel database della pddConsole: " + de.toString());
								continue;
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del soggetto: " + e.toString(), e);
								continue;
							}

							// Propago in remoto sul registro
							try {
								if (Operazione.add.equals(tipoOperazioneCRUD)) {
									this.soggettoPort.create(soggetto);
								} else if (Operazione.change.equals(tipoOperazioneCRUD)) {
									IdSoggetto oldIdSoggetto = new IdSoggetto();
									if(oldTipoSogg!=null)
										oldIdSoggetto.setTipo(oldTipoSogg);
									else
										oldIdSoggetto.setTipo(tipoSogg);
									if(oldNomeSogg!=null)
										oldIdSoggetto.setNome(oldNomeSogg);
									else
										oldIdSoggetto.setNome(nomeSogg);
									this.soggettoPort.update(oldIdSoggetto, soggetto);
								}
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di creazione/update soggetto: " + e.toString(), e);
								continue;
							}

						}
						// ELIMINAZIONE
						else if (Operazione.del.equals(tipoOperazioneCRUD)) {
							try {
								// Effettuo operazione sul registro
								IdSoggetto idSoggetto = new IdSoggetto();
								idSoggetto.setTipo(tipoSogg);
								idSoggetto.setNome(nomeSogg);
								this.soggettoPort.deleteById(idSoggetto);
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di eliminazione soggetto: " + e.toString(), e);
								continue;
							}

						}
					}

					/*  ACCORDO DI SERVIZIO PARTE SPECIFICA */
					if (TipoOggettoDaSmistare.servizio.equals(tipoOggettoDaSmistare) 
						//	|| TipoOggettoDaSmistare.fruitore.equals(tipoOggettoDaSmistare)
							) {

						AccordoServizioParteSpecifica accordoServizioParteSpecifica = null;
						
						@SuppressWarnings("unused")
						String tipoServ = operation.getParameterValue(OperationsParameter.TIPO_SERVIZIO.getNome());
						@SuppressWarnings("unused")
						String nomeServ = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO.getNome());
						String tipoSogg = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSogg = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
						
						String nomeAccordo = operation.getParameterValue(OperationsParameter.NOME_ACCORDO.getNome());
						String versioneAccordo = operation.getParameterValue(OperationsParameter.VERSIONE_ACCORDO.getNome());
						
						@SuppressWarnings("unused")
						String oldtipoServizio = operation.getParameterValue(OperationsParameter.OLD_TIPO_SERVIZIO.getNome());
						@SuppressWarnings("unused")
						String oldNomeServizio = operation.getParameterValue(OperationsParameter.OLD_NOME_SERVIZIO.getNome());
						String oldTipoSogg = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSogg = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());
						
						String oldnomeAccordo = operation.getParameterValue(OperationsParameter.OLD_NOME_ACCORDO.getNome());
						String oldversioneAccordo = operation.getParameterValue(OperationsParameter.OLD_VERSIONE_ACCORDO.getNome());

						// CREAZIONE/MODIFICA
						if (
								( 
										Operazione.change.equals(tipoOperazioneCRUD)
								) 
							|| 
								(
										Operazione.add.equals(tipoOperazioneCRUD)
								) 
//							|| 
//								(
//										TipoOggettoDaSmistare.fruitore.equals(tipoOggettoDaSmistare) && Operazione.del.equals(tipoOperazioneCRUD)
//								)
							) {

							// Ottengo nuova immagine del servizio
							try {
								accordoServizioParteSpecifica = this.apsCore.getAccordoServizioParteSpecifica(idTable,true);
							} catch (DriverRegistroServiziNotFound de) {
								operazioneInGestione.waitBeforeInvalid("Servizio non esistente nel database della pddConsole: " + de.toString());
								continue;
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del servizio: " + e.getMessage(), e);
								continue;
							}

							// Propago in remoto sul registro
							try {
								// Effettuo operazione sul registro
								if (TipoOggettoDaSmistare.servizio.equals(tipoOggettoDaSmistare) && Operazione.add.equals(tipoOperazioneCRUD)) {

									this.accordoServizioParteSpecificaPort.create(accordoServizioParteSpecifica);

									// se operazione change oppure add (e
									// ovviamente change) del fruitore
									// allora eseguo la modificaServizio
								} else if (TipoOggettoDaSmistare.servizio.equals(tipoOggettoDaSmistare) && Operazione.change.equals(tipoOperazioneCRUD)) {

									IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica = new IdAccordoServizioParteSpecifica();
									if(oldnomeAccordo!=null){
										idAccordoServizioParteSpecifica.setNome(oldnomeAccordo);
									}else{
										idAccordoServizioParteSpecifica.setNome(nomeAccordo);
									}
									if(oldversioneAccordo!=null){
										idAccordoServizioParteSpecifica.setVersione(oldversioneAccordo);
									}
									else{
										idAccordoServizioParteSpecifica.setVersione(versioneAccordo);
									}
									idAccordoServizioParteSpecifica.setSoggettoErogatore(new IdSoggetto());
									idAccordoServizioParteSpecifica.getSoggettoErogatore().setTipo(tipoSogg);
									idAccordoServizioParteSpecifica.getSoggettoErogatore().setNome(nomeSogg);
									
									if(oldTipoSogg!=null && oldNomeSogg!=null){
										// Check se operazione di change che  l'operazione di modifica del soggetto non sia ancora in rollback.
										// Se sussite, aspetto l'operazione.
										FilterParameter filtro = operazioneInGestione.getFilterChangeIDSoggetto(tipoSogg,nomeSogg,oldTipoSogg,oldNomeSogg);										
										if(operazioneInGestione.existsOperationNotCompleted("change", operation.getHostname(), filtro)){
											idAccordoServizioParteSpecifica.getSoggettoErogatore().setTipo(oldTipoSogg);
											idAccordoServizioParteSpecifica.getSoggettoErogatore().setNome(oldNomeSogg);
											this.log.debug("ChangeServizio: operazione change ID Soggetto non ancora completata: utilizzo OLD nome");
										}else{
											this.log.debug("ChangeServizio: operazione change ID Soggetto completata: utilizzo nome");
										}
									}
									
									// NOTA: Il soggetto del servizio, nella pddConsole non e' modificabile.
									this.accordoServizioParteSpecificaPort.update(idAccordoServizioParteSpecifica, accordoServizioParteSpecifica);

								} 
								/*
								else if (TipoOggettoDaSmistare.fruitore.equals(tipoOggettoDaSmistare)) {

									IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica = new IdAccordoServizioParteSpecifica();
									idAccordoServizioParteSpecifica.setNome(nomeAccordo);
									idAccordoServizioParteSpecifica.setVersione(versioneAccordo);
									idAccordoServizioParteSpecifica.setSoggettoErogatore(new IdSoggetto());
									idAccordoServizioParteSpecifica.getSoggettoErogatore().setTipo(tipoSogg);
									idAccordoServizioParteSpecifica.getSoggettoErogatore().setNome(nomeSogg);
									
									this.accordoServizioParteSpecificaPort.update(idAccordoServizioParteSpecifica, accordoServizioParteSpecifica);
								}
								*/
								
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di creazione/update servizio: " + e.toString(), e);
								continue;
							}
						}

						// ELIMINAZIONE servizio
						else if (TipoOggettoDaSmistare.servizio.equals(tipoOggettoDaSmistare) && Operazione.del.equals(tipoOperazioneCRUD)) {

							try {
								IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica = new IdAccordoServizioParteSpecifica();
								idAccordoServizioParteSpecifica.setNome(nomeAccordo);
								idAccordoServizioParteSpecifica.setVersione(versioneAccordo);
								idAccordoServizioParteSpecifica.setSoggettoErogatore(new IdSoggetto());
								idAccordoServizioParteSpecifica.getSoggettoErogatore().setTipo(tipoSogg);
								idAccordoServizioParteSpecifica.getSoggettoErogatore().setNome(nomeSogg);
								
								this.accordoServizioParteSpecificaPort.deleteById(idAccordoServizioParteSpecifica);
								
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di eliminazione servizio: " + e.toString(), e);
								continue;
							}
						}
					}

					// * ***** ACCORDI DI SERVIZIO PARTE COMUNE ***** */
					if (TipoOggettoDaSmistare.accordo.equals(tipoOggettoDaSmistare)) {

						String nomeAcc = operation.getParameterValue(OperationsParameter.NOME_ACCORDO.getNome());
						String versioneAcc = operation.getParameterValue(OperationsParameter.VERSIONE_ACCORDO.getNome());
						String tipoReferenteAcc = operation.getParameterValue(OperationsParameter.TIPO_REFERENTE.getNome());
						String nomeReferenteAcc = operation.getParameterValue(OperationsParameter.NOME_REFERENTE.getNome());
						IDAccordo idAccordoServizioParteComune = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAcc,tipoReferenteAcc,nomeReferenteAcc,versioneAcc);

						AccordoServizioParteComune accordoServizioParteComune = null;
						
						// CREAZIONE/MODIFICA
						if (Operazione.change.equals(tipoOperazioneCRUD) || Operazione.add.equals(tipoOperazioneCRUD)) {

							// Ottengo nuova immagine dell'accordo
							try {
								accordoServizioParteComune = this.apcCore.getAccordoServizio(idAccordoServizioParteComune,true);
							} catch (DriverRegistroServiziNotFound de) {
								operazioneInGestione.waitBeforeInvalid("Accordo di Servizio non esistente nel database della pddConsole: " + de.toString());
								continue;
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore durante la get dell'immagine dell'accordo: " + e.getMessage(), e);
								continue;
							}

							try {

								if (Operazione.add.equals(tipoOperazioneCRUD)) {
									
									this.accordoServizioParteComunePort.create(accordoServizioParteComune);
									
								} else if (Operazione.change.equals(tipoOperazioneCRUD)) {
									
									String nomeOLDAcc = operation.getParameterValue(OperationsParameter.OLD_NOME_ACCORDO.getNome());
									if(nomeOLDAcc!=null){
										String versioneOLDAcc = operation.getParameterValue(OperationsParameter.OLD_VERSIONE_ACCORDO.getNome());
										String tipoReferenteOLDAcc = operation.getParameterValue(OperationsParameter.OLD_TIPO_REFERENTE.getNome());
										String nomeReferenteOLDAcc = operation.getParameterValue(OperationsParameter.OLD_NOME_REFERENTE.getNome());
										IDAccordo oldIdAccordoServizioParteComune = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeOLDAcc,tipoReferenteOLDAcc,nomeReferenteOLDAcc,versioneOLDAcc);
										
										if(tipoReferenteOLDAcc!=null && nomeReferenteOLDAcc!=null){
											// Check se operazione di change che  l'operazione di modifica del soggetto non sia ancora in rollback.
											// Se sussite, aspetto l'operazione.
											FilterParameter filtro = operazioneInGestione.getFilterChangeIDSoggetto(tipoReferenteAcc,nomeReferenteAcc,tipoReferenteOLDAcc,nomeReferenteOLDAcc);										
											if(operazioneInGestione.existsOperationNotCompleted("change", operation.getHostname(), filtro)){
												this.log.debug("ChangeServizio: operazione change ID Soggetto non ancora completata: utilizzo OLD nome");
											}else{
												this.log.debug("ChangeServizio: operazione change ID Soggetto completata: utilizzo nome");
												oldIdAccordoServizioParteComune.getSoggettoReferente().setTipo(tipoReferenteAcc);
												oldIdAccordoServizioParteComune.getSoggettoReferente().setNome(nomeReferenteAcc);
											}
										}
										
										accordoServizioParteComune.setOldIDAccordoForUpdate(oldIdAccordoServizioParteComune);
										
										this.accordoServizioParteComunePort.update(new IdAccordoServizioParteComune(oldIdAccordoServizioParteComune), accordoServizioParteComune);
									}
									else{
										
										this.accordoServizioParteComunePort.update(new IdAccordoServizioParteComune(idAccordoServizioParteComune), accordoServizioParteComune);
										
									}
								}

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di creazione/update accordo servizio: " + e.toString(), e);
								continue;
							}
						}

						// ELIMINAZIONE
						else if (Operazione.del.equals(tipoOperazioneCRUD)) {
							try {

								this.accordoServizioParteComunePort.deleteById(new IdAccordoServizioParteComune(idAccordoServizioParteComune));

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di cancellazione accordo servizio: " + e.toString(), e);
								continue;
							}

						}
					}
					
					
					// * ***** ACCORDI DI COOPERAZIONE ***** */
					if (TipoOggettoDaSmistare.accordoCooperazione.equals(tipoOggettoDaSmistare)) {

						String nomeAcc = operation.getParameterValue(OperationsParameter.NOME_ACCORDO.getNome());
						String versioneAcc = operation.getParameterValue(OperationsParameter.VERSIONE_ACCORDO.getNome());
						IDAccordoCooperazione idAccordoCooperazione = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(nomeAcc,versioneAcc);

						AccordoCooperazione accordoCooperazione = null;
						
						// CREAZIONE/MODIFICA
						if (Operazione.change.equals(tipoOperazioneCRUD) || Operazione.add.equals(tipoOperazioneCRUD)) {

							// Ottengo nuova immagine dell'accordo
							try {
								accordoCooperazione = this.acCore.getAccordoCooperazione(idAccordoCooperazione,true);
							} catch (DriverRegistroServiziNotFound de) {
								operazioneInGestione.waitBeforeInvalid("Accordo di Cooperazione non esistente nel database della pddConsole: " + de.toString());
								continue;
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore durante la get dell'immagine dell'accordo di cooperazione: " + e.getMessage(), e);
								continue;
							}

							try {

								if (Operazione.add.equals(tipoOperazioneCRUD)) {
									
									this.accordoCooperazionePort.create(accordoCooperazione);
								
								} else if (Operazione.change.equals(tipoOperazioneCRUD)) { 
									
									String nomeOLDAcc = operation.getParameterValue(OperationsParameter.OLD_NOME_ACCORDO.getNome());
									if(nomeOLDAcc!=null){
										String versioneOLDAcc = operation.getParameterValue(OperationsParameter.OLD_VERSIONE_ACCORDO.getNome());
										IDAccordoCooperazione oldIdAccordoCooperazione = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(nomeOLDAcc,versioneOLDAcc);
										accordoCooperazione.setOldIDAccordoForUpdate(oldIdAccordoCooperazione);
										
										this.accordoCooperazionePort.update(new IdAccordoCooperazione(oldIdAccordoCooperazione), accordoCooperazione);
									}
									else{
										this.accordoCooperazionePort.update(new IdAccordoCooperazione(idAccordoCooperazione), accordoCooperazione);
									}
									
								}

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di creazione/update accordo di cooperazione: " + e.toString(), e);
								continue;
							}
						}

						// ELIMINAZIONE
						else if (Operazione.del.equals(tipoOperazioneCRUD)) {
							try {

								this.accordoCooperazionePort.deleteById(new IdAccordoCooperazione(idAccordoCooperazione));

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di cancellazione accordo di cooperazione: " + e.toString(), e);
								continue;
							}

						}
					}
					

					// Operazione che riguarda la pdd
					if (TipoOggettoDaSmistare.pdd.equals(tipoOggettoDaSmistare)) {

						String nomePortaDominio = operation.getParameterValue(OperationsParameter.PORTA_DOMINIO.getNome());
						IdPortaDominio idPortaDominio = new IdPortaDominio(nomePortaDominio);
						PortaDominio portaDominio = null;

						// CREAZIONE/MODIFICA
						if (Operazione.change.equals(tipoOperazioneCRUD) || Operazione.add.equals(tipoOperazioneCRUD)) {

							// Ottengo nuova immagine della porta di dominio
							try {
								portaDominio = this.pddCore.getPortaDominio(nomePortaDominio);
							} catch (DriverRegistroServiziNotFound de) {
								operazioneInGestione.waitBeforeInvalid("Porta di dominio non esistente nel database: " + de.toString());
								continue;
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore durante la get della porta di dominio: " + e.getMessage(), e);
								continue;
							}

							// Controllo elementi obbligatori
							try {
								this.validazionePortaDominio(portaDominio);
							} catch (Exception e) {
								operazioneInGestione.invalid(e.getMessage());
								continue;
							}

							if (Operazione.add.equals(tipoOperazioneCRUD)) {
								try {
									this.pddPort.create(portaDominio);
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore di creazione porta di dominio: " + e.getMessage(), e);
									continue;
								}
							} else if (Operazione.change.equals(tipoOperazioneCRUD)) {
								try {
									
									this.pddPort.update(idPortaDominio, portaDominio);
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore di update porta di dominio: " + e.getMessage(), e);
									continue;
								}
							}
						}

						// ELIMINAZIONE
						else if (Operazione.del.equals(tipoOperazioneCRUD)) {
							try {
								this.pddPort.deleteById(idPortaDominio);
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di eliminazione porta di dominio: " + e.getMessage(), e);
								continue;
							}
						}
					}

					// Se arrivo qui, significa che l'operazione non ha dato
					// errore,
					// aggiorno il db
					operazioneInGestione.success("Done.");

				}

				this.log.info("GestoreRegistro: Operazione [" + idOperazione + "] completata"+statoOperazioneCancellata);

			} catch (javax.jms.JMSException e) {
				try {

					this.qs.rollback();
				} catch (Exception er) {
				}
				this.log.error("GestoreRegistro: Riscontrato errore durante la gestione di una richiesta: " + e.toString(), e);
				try {
					Thread.sleep(5000);
					this.log.debug("GestoreRegistro: Re-Inizializzazione Receiver ...");
					try {
						this.receiver.close();
					} catch (Exception eclose) {
					}
					try {
						this.qs.close();
					} catch (Exception eclose) {
					}
					try {
						this.qc.close();
					} catch (Exception eclose) {
					}
					this.qc = this.qcf.createQueueConnection();
					// Ripristino stato Exception Listener
					if (this.exceptionListenerJMS.isConnessioneCorrotta()) {
						this.exceptionListenerJMS.setConnessioneCorrotta(false);
						this.exceptionListenerJMS.setException(null);
					}
					this.qc.setExceptionListener(this.exceptionListenerJMS);
					this.qs = this.qc.createQueueSession(true, -1);
					this.receiver = this.qs.createReceiver(this.queue);
					this.qc.start();
					this.log.debug("GestoreRegistro: Re-Inizializzazione Receiver effettuata.");
					riconnessioneConErrore = false;
				} catch (Exception er) {
					this.log.error("GestoreRegistro: Re-Inizializzazione Receiver non effettuata:" + er.toString());
					riconnessioneConErrore = true;
				}
			} catch (ClassQueueException e) {
				try {
					operation.setStatus(OperationStatus.ERROR);
					operation.setDetails(e.toString());
					operation.setTimeExecute(new Timestamp(System.currentTimeMillis()));
					operationManager.updateOperation(operation);
					this.qs.rollback();
				} catch (Exception er) {
				}
				this.log.error("GestoreRegistro: Riscontrato errore durante operazione sulla coda: " + e.toString(), e);
			} catch (Exception e) {
				try {
					this.qs.rollback();
				} catch (Exception er) {
				}
				this.log.error("GestoreRegistro: Riscontrato errore durante la gestione di una richiesta: " + e.toString(), e);
			} finally {
				try {
					this.dbm.releaseConnection(this.con);
				} catch (Exception eLogger) {
				}
			}

		}// CHIUDO WHILE

		// Chiusura connessione
		try {
			if (this.receiver != null) {
				this.receiver.close();
			}
			if (this.qs != null) {
				this.qs.rollback();
				this.qs.close();
			}
			if (this.qc != null) {
				this.qc.stop();
				this.qc.close();
			}
		} catch (Exception e) {
			try {
				this.log.error("GestoreRegistro: Riscontrato errore durante la chiusura del Thread: " + e.toString());
			} catch (Exception eLogger) {
			}
		}
		
		this.isRunning = false;
		this.log.debug("Thread terminato");
	}

	/**
	 * Inizializzazione Gestore
	 */
	@Override
	public void initGestore() throws Exception {
		
		// Controllo se dbmanager inizializzato
		// Il DBManager viene inizializzato nell'InitListener
		if (!DBManager.isInitialized()) {
			this.log.info("Inizializzazione di " + this.getClass().getSimpleName() + " non riuscito perche' DBManager non INIZIALIZZATO");
			throw new Exception("Inizializzazione di " + this.getClass().getSimpleName() + "FALLITA");
		}
		
		this.log.debug("Lettura dei parametri da console.properties");
		String registroServiziQueue = this.consoleProperties.getGestioneCentralizzata_NomeCodaRegistroServizi();
		boolean engineRegistro = this.consoleProperties.isGestioneCentralizzata_SincronizzazioneRegistro();
		this.singlePdD = this.consoleProperties.isSinglePdD();
		boolean trovato = false;

		if (engineRegistro == false) {
			//this.log.info("Motore di sincronizzazione verso il Registro dei Servizi non attivo.");
			throw new GestoreNonAttivoException("Motore di sincronizzazione verso il Registro dei Servizi non attivo.");
		}

		// Leggo informazioni per queue.properties
		// Init JMS
		// readQueueProperties(cfName, cfProp);
		QueueManager queueMan = QueueManager.getInstance();
		if (queueMan == null) {
			this.log.debug("Impossibile avviare " + this.getClass().getSimpleName() + "QueueManager non inizializzato.");
			throw new Exception("Impossibile avviare " + this.getClass().getSimpleName() + "QueueManager non inizializzato.");
		}

		trovato = false;
		int i = 0;
		this.log.debug("Inizializzazione Receiver ...");
		while (!trovato && (i < 600000)) {
			try {
				// InitialContext ctx = new InitialContext(cfProp);
				// queue = (Queue) ctx.lookup(registroServiziQueue);
				// qcf = (QueueConnectionFactory) ctx.lookup(cfName);
				this.qcf = queueMan.getQueueConnectionFactory();
				this.queue = queueMan.getQueue(registroServiziQueue);

				this.qc = this.qcf.createQueueConnection();
				this.qc.setExceptionListener(this.exceptionListenerJMS);
				this.qs = this.qc.createQueueSession(true, -1);
				this.receiver = this.qs.createReceiver(this.queue);
				this.qc.start();
				// ctx.close();
				this.log.debug("GestoreRegistro: Inizializzazione Receiver effettuata.");
				trovato = true;
			} catch (Exception e) {
				i = i + 10000;
				try {
					Thread.sleep(10000);
					this.log.debug("Ritento Inizializzazione Receiver ... causa: " + e.getMessage());
				} catch (Exception et) {
				}
			}
		}

		if (!trovato) {
			this.log.error("Inizializzazione Receiver non effettuata");
			throw new Exception("Inizializzazione Receiver non effettuata");
		}

		// Init WebServiceCore
		try {
			// ws
			this.accordoCooperazioneService = new AccordoCooperazioneSoap11Service();
			this.accordoServizioParteComuneService = new AccordoServizioParteComuneSoap11Service();
			this.accordoServizioParteSpecificaService = new AccordoServizioParteSpecificaSoap11Service();
			this.soggettoService = new SoggettoSoap11Service();
			this.pddService = new PortaDominioSoap11Service();
			
			this.accordoCooperazionePort = this.accordoCooperazioneService.getAccordoCooperazionePortSoap11();
			this.accordoServizioParteComunePort = this.accordoServizioParteComuneService.getAccordoServizioParteComunePortSoap11();
			this.accordoServizioParteSpecificaPort = this.accordoServizioParteSpecificaService.getAccordoServizioParteSpecificaPortSoap11();
			this.soggettoPort = this.soggettoService.getSoggettoPortSoap11();
			this.pddPort = this.pddService.getPortaDominioPortSoap11();
			
			((BindingProvider)this.accordoCooperazionePort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					this.consoleProperties.getGestioneCentralizzata_WSRegistroServizi_endpointAccordoCooperazione());
			((BindingProvider)this.accordoServizioParteComunePort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					this.consoleProperties.getGestioneCentralizzata_WSRegistroServizi_endpointAccordoServizioParteComune());
			((BindingProvider)this.accordoServizioParteSpecificaPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					this.consoleProperties.getGestioneCentralizzata_WSRegistroServizi_endpointAccordoServizioParteSpecifica());
			((BindingProvider)this.soggettoPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					this.consoleProperties.getGestioneCentralizzata_WSRegistroServizi_endpointSoggetto());
			((BindingProvider)this.pddPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					this.consoleProperties.getGestioneCentralizzata_WSRegistroServizi_endpointPdd());
			
			((BindingProvider)this.accordoCooperazionePort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.accordoServizioParteComunePort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.accordoServizioParteSpecificaPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.soggettoPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.pddPort).getRequestContext().put("schema-validation-enabled", true);
			
			String username = this.consoleProperties.getGestioneCentralizzata_WSRegistroServizi_credenzialiBasic_username();
			String password = this.consoleProperties.getGestioneCentralizzata_WSRegistroServizi_credenzialiBasic_password();
			if(username !=null && password!=null){
				// to use Basic HTTP Authentication: 
				
				((BindingProvider)this.accordoCooperazionePort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.accordoCooperazionePort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.accordoServizioParteComunePort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.accordoServizioParteComunePort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.accordoServizioParteSpecificaPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.accordoServizioParteSpecificaPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.soggettoPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.soggettoPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.pddPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.pddPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
			}
			
			this.log.debug("GestoreRegistro: Inizializzato WebService. AccordoCooperazione: " + this.accordoCooperazioneService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. AccordoServizioParteComune: " + this.accordoServizioParteComuneService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. AccordoServizioParteSpecifica: " + this.accordoServizioParteSpecificaService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. Soggetto: " + this.soggettoService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. PortaDominio: " + this.pddService.getClass().getSimpleName());
			
		} catch (Exception e) {
			this.log.error("Riscontrato Errore durante la connessione al WebService.", e);
			this.log.info("GestoreRegistro non avviato.");
			throw new Exception("Riscontrato Errore durante la connessione al WebService.", e);
		}

		// Init ControlStationCore
		try {
			this.soggettiCore = new SoggettiCore();
			this.pddCore = new PddCore(this.soggettiCore);
			this.apcCore = new AccordiServizioParteComuneCore(this.soggettiCore);
			this.apsCore = new AccordiServizioParteSpecificaCore(this.soggettiCore);
			this.acCore = new AccordiCooperazioneCore(this.soggettiCore);

			this.log.debug("GestoreRegistro: Inizializzato Core. ");
			
		} catch (Exception e) {
			this.log.error("Riscontrato Errore durante l'inizializzazione di ControlStationCore.", e);
			this.log.info("GestoreRegistro non avviato.");
			throw new ControlStationCoreException("Riscontrato Errore durante l'inizializzazione di ControlStationCore.", e);
		}

	}

	@Override
	public void stopGestore() {
		this.stop = true;
		
		this.log.debug("Fermo il thread ...");
		int timeout = 60;
		for (int i = 0; i < timeout; i++) {
			if(this.isRunning()){
				try{
					Thread.sleep(1000);
				}catch(Exception e){}
			}
			else{
				break;
			}
		}
		if(this.isRunning){
			this.log.debug("Sono trascorsi 60 secondi ed il thread non è ancora terminato??");
		}
	}

	@Override
	protected String getName() {
		return this.name;
	}

	private void validazionePortaDominio(PortaDominio pdd) throws Exception {

		if ((pdd.getNome() == null) || ("".equals(pdd.getNome()))) {
			throw new Exception("Riscontrato errore: Nome Porta di dominio, non definito");
		}

	}
}
