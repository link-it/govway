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
import org.openspcoop2.core.config.IdPortaApplicativa;
import org.openspcoop2.core.config.IdPortaDelegata;
import org.openspcoop2.core.config.IdServizioApplicativo;
import org.openspcoop2.core.config.IdSoggetto;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.ws.client.portaapplicativa.all.PortaApplicativaSoap11Service;
import org.openspcoop2.core.config.ws.client.portadelegata.all.PortaDelegataSoap11Service;
import org.openspcoop2.core.config.ws.client.servizioapplicativo.all.ServizioApplicativoSoap11Service;
import org.openspcoop2.core.config.ws.client.soggetto.all.SoggettoSoap11Service;
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
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
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
 * GestorePdDThread
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GestorePdDThread extends GestoreGeneral {

	private ConsoleProperties consoleProperties;
	private DatasourceProperties datasourceProperties;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** run */
	private boolean stop = false;
	private boolean isRunning = false;
	public boolean isRunning() {
		return this.isRunning;
	}

	/** Back End Connector */
	// private BackendConnector backEndConnector;
	private PddCore pddCore;
	private SoggettiCore soggettiCore;
	private PorteApplicativeCore paCore;
	private PorteDelegateCore pdCore;
	private ServiziApplicativiCore saCore;

	/** Nome della coda (che corrisponde al nome del pdd) */
	private String pddName;

	private String nomeThread;

	// JMS
	private QueueReceiver receiver = null;
	private Queue queue = null;
	private QueueConnectionFactory qcf = null;
	private QueueConnection qc = null;
	private QueueSession qs = null;

	// DB
	private DBManager dbm;
	private Connection con;

	private boolean singlePdD = false;

	/** Web Service */
	private org.openspcoop2.core.config.ws.client.portaapplicativa.all.PortaApplicativaSoap11Service portaApplicativaService;
	private org.openspcoop2.core.config.ws.client.portadelegata.all.PortaDelegataSoap11Service portaDelegataService;
	private org.openspcoop2.core.config.ws.client.servizioapplicativo.all.ServizioApplicativoSoap11Service servizioApplicativoService;
	private org.openspcoop2.core.config.ws.client.soggetto.all.SoggettoSoap11Service soggettoService;
	
	private org.openspcoop2.core.config.ws.client.portaapplicativa.all.PortaApplicativa portaApplicativaPort;
	private org.openspcoop2.core.config.ws.client.portadelegata.all.PortaDelegata portaDelegataPort;
	private org.openspcoop2.core.config.ws.client.servizioapplicativo.all.ServizioApplicativo servizioApplicativoPort;
	private org.openspcoop2.core.config.ws.client.soggetto.all.Soggetto soggettoPort;

	private ExceptionListenerJMS exceptionListenerJMS = new ExceptionListenerJMS();

	/** Costruttore 
	 * @throws OpenSPCoop2ConfigurationException */
	public GestorePdDThread(String nomeCoda) throws OpenSPCoop2ConfigurationException {

		this.log = ControlStationLogger.getGestorePddLogger();

		// setto il nome della coda
		this.pddName = nomeCoda;

		this.nomeThread = "GestorePDD[" + this.pddName + "]";
		
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
		
		try {
			this.initGestore();

			if (this.singlePdD) {
				this.log.warn(this.nomeThread + " non avviato: pddConsole avviata in singlePdD mode.");
				return;
			}

			this.dbm = DBManager.getInstance();
		} catch (GestoreNonAttivoException e) {
			this.log.warn("Inizializzazione Gestore non effettuata : " + e.getMessage());
			this.stop = true;
		} catch (Exception e) {
			this.log.error("Inizializzazione Gestore Fallita : " + e.getMessage(), e);
			this.stop = true;
		}

		// Avvio Gestione Pdd
		boolean riconnessioneConErrore = false;
		while (this.stop == false) {
			
			ClassQueue operationManager = null;
			Operation operation = null;
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

				this.log.info(this.nomeThread + ": Ricezione operazione...");
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
					this.log.error(this.nomeThread + ": Ricevuta richiesta con tipo errato:" + e.toString());
					this.qs.commit();
					continue;
				}
				String idOperazione = richiesta.getStringProperty("ID");

				int idOp = (int) Integer.parseInt(objOp.toString());
				if (idOp == 0) {
					this.log.error(this.nomeThread + ": Ricevuta richiesta con parametri scorretti.");
					this.qs.commit();
					continue;
				}

				this.log.info(CostantiControlStation.OPERATIONS_DELIMITER+"GestorePDD: Ricevuta richiesta di operazione con ID: " + idOperazione + " id_operation [" + idOp + "]");

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
				GestioneOperazione operazioneInGestione = new GestioneOperazione(this.dbm, this.con, this.qs, operation, operationManager, this.log, tipoOperazioneCRUDParam, this.nomeThread);

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
						!TipoOggettoDaSmistare.pa.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.pd.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.servizioApplicativo.equals(tipoOggettoDaSmistare)  ) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (oggetto:" + tipoOggettoDaSmistare.name() + ")");
					continue;
				}
				

				/* ----- filtro per mantenimento ordine operazioni ----- */
				Vector<FilterParameter> filtroOrdine = new Vector<FilterParameter>();

				FilterParameter idTableFiltro = new FilterParameter();
				idTableFiltro.addFilterParameter(new Parameter(OperationsParameter.ID_TABLE.getNome(), idTable + ""));
				filtroOrdine.add(idTableFiltro);

				if (TipoOggettoDaSmistare.soggetto.equals(tipoOggettoDaSmistare)) {

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
				} else if (TipoOggettoDaSmistare.pa.equals(tipoOggettoDaSmistare)) {

					// Chiave primaria che identifica una pa sono il nome e il soggetto proprietario
					String nomePAFiltro = operation.getParameterValue(OperationsParameter.NOME_PA.getNome());
					String tipoSoggFiltro = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
					String nomeSoggFiltro = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
					FilterParameter paFiltro = new FilterParameter();
					paFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_PA.getNome(), nomePAFiltro));
					paFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
					paFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
					filtroOrdine.add(paFiltro);
					
					// OLD valori per tipo e nome soggetto proprietario o per
					// nome PA
					// se abbiamo una operazione di change
					if ((Operazione.change.equals(tipoOperazioneCRUD))) {
						String oldNomePAFiltro = operation.getParameterValue(OperationsParameter.OLD_NOME_PA.getNome());
						String oldTipoProprietarioFiltro = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeProprietarioFiltro = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());

						FilterParameter proprietarioOldFiltro = new FilterParameter();
						proprietarioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_PA.getNome(), nomePAFiltro));
						proprietarioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SOGGETTO.getNome(), oldTipoProprietarioFiltro));
						proprietarioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SOGGETTO.getNome(), oldNomeProprietarioFiltro));
						filtroOrdine.add(proprietarioOldFiltro);

						FilterParameter nomeOldFiltro = new FilterParameter();
						nomeOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_PA.getNome(), oldNomePAFiltro));
						nomeOldFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
						nomeOldFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
						filtroOrdine.add(nomeOldFiltro);

						FilterParameter oldFiltro = new FilterParameter();
						oldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_PA.getNome(), oldNomePAFiltro));
						oldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SOGGETTO.getNome(), oldTipoProprietarioFiltro));
						oldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SOGGETTO.getNome(), oldNomeProprietarioFiltro));
						filtroOrdine.add(oldFiltro);
					}
					
				} else if (TipoOggettoDaSmistare.pd.equals(tipoOggettoDaSmistare)) {

					// Chiave primaria che identifica una pd sono il nome e il soggetto proprietario
					String nomePDFiltro = operation.getParameterValue(OperationsParameter.NOME_PD.getNome());
					String tipoSoggFiltro = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
					String nomeSoggFiltro = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
					FilterParameter pdFiltro = new FilterParameter();
					pdFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_PD.getNome(), nomePDFiltro));
					pdFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
					pdFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
					filtroOrdine.add(pdFiltro);

					// OLD valori per tipo e nome soggetto proprietario o per
					// nome PD
					// se abbiamo una operazione di change
					if ((Operazione.change.equals(tipoOperazioneCRUD))) {
						String oldNomePDFiltro = operation.getParameterValue(OperationsParameter.OLD_NOME_PD.getNome());
						String oldTipoProprietarioFiltro = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeProprietarioFiltro = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());

						FilterParameter proprietarioOldFiltro = new FilterParameter();
						proprietarioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_PD.getNome(), nomePDFiltro));
						proprietarioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SOGGETTO.getNome(), oldTipoProprietarioFiltro));
						proprietarioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SOGGETTO.getNome(), oldNomeProprietarioFiltro));
						filtroOrdine.add(proprietarioOldFiltro);

						FilterParameter nomeOldFiltro = new FilterParameter();
						nomeOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_PD.getNome(), oldNomePDFiltro));
						nomeOldFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
						nomeOldFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
						filtroOrdine.add(nomeOldFiltro);

						FilterParameter oldFiltro = new FilterParameter();
						oldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_PD.getNome(), oldNomePDFiltro));
						oldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SOGGETTO.getNome(), oldTipoProprietarioFiltro));
						oldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SOGGETTO.getNome(), oldNomeProprietarioFiltro));
						filtroOrdine.add(oldFiltro);
					}
				} else if (TipoOggettoDaSmistare.servizioApplicativo.equals(tipoOggettoDaSmistare)) {

					// Chiave che indentifica il servizio applicativo sono nome
					// e soggetto proprietario
					String nomeServizioApplicativoFiltro = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome());
					String tipoSoggettoFiltro = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
					String nomeSoggettoFiltro = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
					FilterParameter saFiltro = new FilterParameter();
					saFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome(), nomeServizioApplicativoFiltro));
					saFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggettoFiltro));
					saFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggettoFiltro));
					filtroOrdine.add(saFiltro);

					// OLD valori per tipo e nome soggetto proprietario o per
					// nome ServizioApplicativo
					// se abbiamo una operazione di change
					if ((Operazione.change.equals(tipoOperazioneCRUD))) {
						String oldNomeSAFiltro = operation.getParameterValue(OperationsParameter.OLD_NOME_SERVIZIO_APPLICATIVO.getNome());
						String oldTipoProprietarioFiltro = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeProprietarioFiltro = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());

						FilterParameter proprietarioOldFiltro = new FilterParameter();
						proprietarioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome(), nomeServizioApplicativoFiltro));
						proprietarioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SOGGETTO.getNome(), oldTipoProprietarioFiltro));
						proprietarioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SOGGETTO.getNome(), oldNomeProprietarioFiltro));
						filtroOrdine.add(proprietarioOldFiltro);

						FilterParameter nomeOldFiltro = new FilterParameter();
						nomeOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SERVIZIO_APPLICATIVO.getNome(), oldNomeSAFiltro));
						nomeOldFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggettoFiltro));
						nomeOldFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggettoFiltro));
						filtroOrdine.add(nomeOldFiltro);

						FilterParameter oldFiltro = new FilterParameter();
						oldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SERVIZIO_APPLICATIVO.getNome(), oldNomeSAFiltro));
						oldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SOGGETTO.getNome(), oldTipoProprietarioFiltro));
						oldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SOGGETTO.getNome(), oldNomeProprietarioFiltro));
						filtroOrdine.add(oldFiltro);
					}
					
				} else {
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


					// OPERAZIONE CHE RIGUARDA IL SOGGETTO
					if (TipoOggettoDaSmistare.soggetto.equals(tipoOggettoDaSmistare)) {

						String oldTipoSoggetto = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSoggetto = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());
						String tipoSogg = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSogg = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());

						// Ottengo nuova immagine del soggetto
						// Soggetto soggetto = null;
						Soggetto soggetto = null;

						try {
							// soggetto =
							// backEndConnector.getDatiSoggetto(idTable);
							if (Operazione.add.equals(tipoOperazioneCRUD)) {

								try {
									soggetto = this.soggettiCore.getSoggetto(idTable);
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Soggetto non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del soggetto: " + e.toString(), e);
									continue;
								}

								this.soggettoPort.create(soggetto);

							} else if (Operazione.change.equals(tipoOperazioneCRUD)) {

								try {
									soggetto = this.soggettiCore.getSoggetto(idTable);
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Soggetto non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del soggetto: " + e.toString(), e);
									continue;
								}
								soggetto.setOldNomeForUpdate(oldNomeSoggetto);
								soggetto.setOldTipoForUpdate(oldTipoSoggetto);

								IdSoggetto idSoggetto = new IdSoggetto();
								if(oldTipoSoggetto!=null)
									idSoggetto.setTipo(oldTipoSoggetto);
								else
									idSoggetto.setTipo(tipoSogg);
								if(oldNomeSoggetto!=null)
									idSoggetto.setNome(oldNomeSoggetto);
								else
									idSoggetto.setNome(nomeSogg);
								
								this.soggettoPort.update(idSoggetto,soggetto);

							} else if (Operazione.del.equals(tipoOperazioneCRUD)) {
								
								IdSoggetto idSoggetto = new IdSoggetto();
								idSoggetto.setTipo(tipoSogg);
								idSoggetto.setNome(nomeSogg);

								this.soggettoPort.deleteById(idSoggetto);

							}

						} catch (Exception e) {
							soggetto = null;
							operazioneInGestione.error("Riscontrato errore durante operazione su soggetto: " + e.toString(), e);
							continue;
						}

					}

					// OPERAZIONE CHE RIGUARDA LA PORTA APPLICATIVA
					if (TipoOggettoDaSmistare.pa.equals(tipoOggettoDaSmistare)) {

						String nomePA = operation.getParameterValue(OperationsParameter.NOME_PA.getNome());
						String oldNomePA = operation.getParameterValue(OperationsParameter.OLD_NOME_PA.getNome()); 
						String tipoSogg = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSogg = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
						String oldTipoProprietario = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeProprietario = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());

						// Ottengo nuova immagine della porta applicativa
						PortaApplicativa pa = null;
						try {
							// pa = backEndConnector.getDatiPA(idTable);
							if (Operazione.add.equals(tipoOperazioneCRUD)) {

								try {
									pa = this.paCore.getPortaApplicativa(idTable);
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Porta Applicativa non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine della Porta Applicativa: " + e.toString(), e);
									continue;
								}

								this.portaApplicativaPort.create(pa);

							} else if (Operazione.change.equals(tipoOperazioneCRUD)) {

								try {
									pa = this.paCore.getPortaApplicativa(idTable);
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Porta Applicativa non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine della Porta Applicativa: " + e.toString(), e);
									continue;
								}

								// soggetto proprietario
								Soggetto soggettoProprietario = null;
								try {
									soggettoProprietario = this.soggettiCore.getSoggetto(pa.getIdSoggetto());
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Soggetto proprietario non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del Soggetto proprietario: " + e.toString(), e);
									continue;
								}
								pa.setTipoSoggettoProprietario(soggettoProprietario.getTipo());
								pa.setNomeSoggettoProprietario(soggettoProprietario.getNome());
								
								// vecchi dati per update
								pa.setOldNomeForUpdate(oldNomePA);
								
								if(oldTipoProprietario!=null && oldNomeProprietario!=null){
									// Check se operazione di change che  l'operazione di modifica del soggetto non sia ancora in rollback.
									// Se sussite, aspetto l'operazione.
									FilterParameter filtro = operazioneInGestione.getFilterChangeIDSoggetto(soggettoProprietario.getTipo(),soggettoProprietario.getNome(),
											oldTipoProprietario,oldNomeProprietario);										
									if(operazioneInGestione.existsOperationNotCompleted("change", operation.getHostname(), filtro)){
										pa.setOldNomeSoggettoProprietarioForUpdate(oldNomeProprietario);
										pa.setOldTipoSoggettoProprietarioForUpdate(oldTipoProprietario);
										this.log.debug("ChangePD: operazione change ID Soggetto non ancora completata: utilizzo OLD nome");
									}else{
										this.log.debug("ChangePD: operazione change ID Soggetto completata: utilizzo nome");
									}
								}
								
								IdPortaApplicativa idPA = new IdPortaApplicativa();
								if(pa.getOldNomeForUpdate()!=null){
									idPA.setNome(pa.getOldNomeForUpdate());
								}
								else{
									idPA.setNome(pa.getNome());
								}
								idPA.setIdSoggetto(new IdSoggetto());
								if(pa.getOldTipoSoggettoProprietarioForUpdate()!=null){
									idPA.getIdSoggetto().setTipo(pa.getOldTipoSoggettoProprietarioForUpdate());
								}
								else{
									idPA.getIdSoggetto().setTipo(pa.getTipoSoggettoProprietario());
								}
								if(pa.getOldNomeSoggettoProprietarioForUpdate()!=null){
									idPA.getIdSoggetto().setNome(pa.getOldNomeSoggettoProprietarioForUpdate());
								}
								else{
									idPA.getIdSoggetto().setNome(pa.getNomeSoggettoProprietario());
								}
								
								this.portaApplicativaPort.update(idPA, pa);
								
							} else if (Operazione.del.equals(tipoOperazioneCRUD)) {

								IdPortaApplicativa idPA = new IdPortaApplicativa();
								idPA.setNome(nomePA);
								idPA.setIdSoggetto(new IdSoggetto());
								idPA.getIdSoggetto().setTipo(tipoSogg);
								idPA.getIdSoggetto().setNome(nomeSogg);

								this.portaApplicativaPort.deleteById(idPA);

							}
						} catch (Exception e) {
							operazioneInGestione.error("Riscontrato errore durante operazione su Porta Applicativa: " + e.toString(), e);
							continue;
						}

					}

					// OPERAZIONE CHE RIGUARDA LA PORTA DELEGATA
					if (TipoOggettoDaSmistare.pd.equals(tipoOggettoDaSmistare)) {

						String nomePD = operation.getParameterValue(OperationsParameter.NOME_PD.getNome());
						String oldNomePD = operation.getParameterValue(OperationsParameter.OLD_NOME_PD.getNome());
						String tipoSogg = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSogg = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
						String oldTipoProprietario = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeProprietario = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());

						// Ottengo nuova immagine della porta delegata
						PortaDelegata pd = null;
						try {
							// pd = backEndConnector.getDatiPD(idTable);

							if (Operazione.add.equals(tipoOperazioneCRUD)) {

								try {
									pd = this.pdCore.getPortaDelegata(idTable);
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Porta Delegata non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine della Porta Delegata: " + e.toString(), e);
									continue;
								}

								Soggetto soggettoProprietario = null;
								try {
									soggettoProprietario = this.soggettiCore.getSoggetto(pd.getIdSoggetto());
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Soggetto proprietario non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del Soggetto proprietario: " + e.toString(), e);
									continue;
								}

								pd.setTipoSoggettoProprietario(soggettoProprietario.getTipo());
								pd.setNomeSoggettoProprietario(soggettoProprietario.getNome());

								this.portaDelegataPort.create(pd);

							} else if (Operazione.change.equals(tipoOperazioneCRUD)) {

								try {
									pd = this.pdCore.getPortaDelegata(idTable);
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Porta Delegata non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine della Porta Delegata: " + e.toString(), e);
									continue;
								}

								// soggetto proprietario
								Soggetto soggettoProprietario = null;
								try {
									soggettoProprietario = this.soggettiCore.getSoggetto(pd.getIdSoggetto());
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Soggetto proprietario non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del Soggetto proprietario: " + e.toString(), e);
									continue;
								}
								pd.setTipoSoggettoProprietario(soggettoProprietario.getTipo());
								pd.setNomeSoggettoProprietario(soggettoProprietario.getNome());
								
								// vecchi dati per update
								pd.setOldNomeForUpdate(oldNomePD);
								
								if(oldTipoProprietario!=null && oldNomeProprietario!=null){
									// Check se operazione di change che  l'operazione di modifica del soggetto non sia ancora in rollback.
									// Se sussite, aspetto l'operazione.
									FilterParameter filtro = operazioneInGestione.getFilterChangeIDSoggetto(soggettoProprietario.getTipo(),soggettoProprietario.getNome(),oldTipoProprietario,oldNomeProprietario);										
									if(operazioneInGestione.existsOperationNotCompleted("change", operation.getHostname(), filtro)){
										pd.setOldNomeSoggettoProprietarioForUpdate(oldNomeProprietario);
										pd.setOldTipoSoggettoProprietarioForUpdate(oldTipoProprietario);
										this.log.debug("ChangePD: operazione change ID Soggetto non ancora completata: utilizzo OLD nome");
									}else{
										this.log.debug("ChangePD: operazione change ID Soggetto completata: utilizzo nome");
									}
								}
								
								IdPortaDelegata idPD = new IdPortaDelegata();
								if(pd.getOldNomeForUpdate()!=null){
									idPD.setNome(pd.getOldNomeForUpdate());
								}
								else{
									idPD.setNome(pd.getNome());
								}
								idPD.setIdSoggetto(new IdSoggetto());
								if(pd.getOldTipoSoggettoProprietarioForUpdate()!=null){
									idPD.getIdSoggetto().setTipo(pd.getOldTipoSoggettoProprietarioForUpdate());
								}
								else{
									idPD.getIdSoggetto().setTipo(pd.getTipoSoggettoProprietario());
								}
								if(pd.getOldNomeSoggettoProprietarioForUpdate()!=null){
									idPD.getIdSoggetto().setNome(pd.getOldNomeSoggettoProprietarioForUpdate());
								}
								else{
									idPD.getIdSoggetto().setNome(pd.getNomeSoggettoProprietario());
								}
								
								this.portaDelegataPort.update(idPD, pd);


							} else if (Operazione.del.equals(tipoOperazioneCRUD)) {

								IdPortaDelegata idPD = new IdPortaDelegata();
								idPD.setNome(nomePD);
								idPD.setIdSoggetto(new IdSoggetto());
								idPD.getIdSoggetto().setTipo(tipoSogg);
								idPD.getIdSoggetto().setNome(nomeSogg);
								
								this.portaDelegataPort.deleteById(idPD);
								
							}

						} catch (Exception e) {
							operazioneInGestione.error("Riscontrato errore durante operazione su Porta Delegata: " + e.toString(), e);
							continue;
						}

					}

					// OPERAZIONE CHE RIGUARDA IL SERVIZIOAPPLICATIVO
					if (TipoOggettoDaSmistare.servizioApplicativo.equals(tipoOggettoDaSmistare)) {

						String nomeServizioApplicativo = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome());
						String oldNomeServizioApplicativo = operation.getParameterValue(OperationsParameter.OLD_NOME_SERVIZIO_APPLICATIVO.getNome());
						String tipoSoggetto = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSoggetto = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
						String oldTipoSoggetto = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSoggetto = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());

						// Ottengo nuova immagine del servizioApplicativo
						ServizioApplicativo servizioApplicativo = null;
						try {
							// s =
							// backEndConnector.getDatiServizioApplicativo(idTable);

							if (Operazione.add.equals(tipoOperazioneCRUD)) {

								try {
									servizioApplicativo = this.saCore.getServizioApplicativo(idTable);
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Servizio Applicativo non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del Servizio Applicativo: " + e.toString(), e);
									continue;
								}

								this.servizioApplicativoPort.create(servizioApplicativo);

							} else if (Operazione.change.equals(tipoOperazioneCRUD)) {

								try {
									servizioApplicativo = this.saCore.getServizioApplicativo(idTable);
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Servizio Applicativo non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del Servizio Applicativo: " + e.toString(), e);
									continue;
								}
								servizioApplicativo.setOldNomeForUpdate(nomeServizioApplicativo);
								if (tipoSoggetto != null && !tipoSoggetto.equals(""))
									servizioApplicativo.setTipoSoggettoProprietario(tipoSoggetto);
								if (nomeSoggetto != null && !nomeSoggetto.equals(""))
									servizioApplicativo.setNomeSoggettoProprietario(nomeSoggetto);
								
								// vecchi dati per update
								servizioApplicativo.setOldNomeForUpdate(oldNomeServizioApplicativo);
								
								if(oldTipoSoggetto!=null && oldNomeSoggetto!=null){
									// Check se operazione di change che  l'operazione di modifica del soggetto non sia ancora in rollback.
									// Se sussite, aspetto l'operazione.
									FilterParameter filtro = operazioneInGestione.getFilterChangeIDSoggetto(tipoSoggetto,
											nomeSoggetto,oldTipoSoggetto,oldNomeSoggetto);										
									if(operazioneInGestione.existsOperationNotCompleted("change", operation.getHostname(), filtro)){
										servizioApplicativo.setOldNomeSoggettoProprietarioForUpdate(oldNomeSoggetto);
										servizioApplicativo.setOldTipoSoggettoProprietarioForUpdate(oldTipoSoggetto);
										this.log.debug("ChangeServizioApplicativo: operazione change ID Soggetto non ancora completata: utilizzo OLD nome");
									}else{
										this.log.debug("ChangeServizioApplicativo: operazione change ID Soggetto completata: utilizzo nome");
									}
								}
								
								IdServizioApplicativo idSA = new IdServizioApplicativo();
								if(servizioApplicativo.getOldNomeForUpdate()!=null){
									idSA.setNome(servizioApplicativo.getOldNomeForUpdate());
								}
								else{
									idSA.setNome(servizioApplicativo.getNome());
								}
								idSA.setIdSoggetto(new IdSoggetto());
								if(servizioApplicativo.getOldTipoSoggettoProprietarioForUpdate()!=null){
									idSA.getIdSoggetto().setTipo(servizioApplicativo.getOldTipoSoggettoProprietarioForUpdate());
								}
								else{
									idSA.getIdSoggetto().setTipo(servizioApplicativo.getTipoSoggettoProprietario());
								}
								if(servizioApplicativo.getOldNomeSoggettoProprietarioForUpdate()!=null){
									idSA.getIdSoggetto().setNome(servizioApplicativo.getOldNomeSoggettoProprietarioForUpdate());
								}
								else{
									idSA.getIdSoggetto().setNome(servizioApplicativo.getNomeSoggettoProprietario());
								}
								
								this.servizioApplicativoPort.update(idSA, servizioApplicativo);

							} else if (Operazione.del.equals(tipoOperazioneCRUD)) {

								IdServizioApplicativo idSA = new IdServizioApplicativo();
								idSA.setNome(nomeServizioApplicativo);
								idSA.setIdSoggetto(new IdSoggetto());
								idSA.getIdSoggetto().setTipo(tipoSoggetto);
								idSA.getIdSoggetto().setNome(nomeSoggetto);
								
								this.servizioApplicativoPort.deleteById(idSA);

							}

						} catch (Exception e) {
							operazioneInGestione.error("Riscontrato errore durante operazione su Servizio Applicativo: " + e.toString(), e);
							continue;
						}

					}

					// Se arrivo qui, significa che l'operazione non ha dato
					// errore,
					// aggiorno il db
					operazioneInGestione.success("Done.");
				}

				this.log.info(this.nomeThread + ": Operazione [" + idOperazione + "] completata"+statoOperazioneCancellata);

			} catch (JMSException e) {
				try {
					this.qs.rollback();
				} catch (Exception er) {
				}

				this.log.error(this.nomeThread + ": Riscontrato errore durante la gestione di una richiesta: " + e.toString());
				this.log.debug(this.nomeThread + " : Eccezione :", e);
				try {
					Thread.sleep(5000);
					this.log.debug(this.nomeThread + ": Re-Inizializzazione Receiver ...");
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
					this.log.debug(this.nomeThread + ": Re-Inizializzazione Receiver effettuata.");
					riconnessioneConErrore = false;
				} catch (Exception er) {
					this.log.error(this.nomeThread + ": Re-Inizializzazione Receiver non effettuata:" + er.toString());
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
				this.log.error("GestorePdD-"+this.pddName+": Riscontrato errore durante operazione sulla coda: " + e.toString(), e);
			} catch (Exception e) {
				try {
					this.qs.rollback();
				} catch (Exception er) {
				}

				this.log.error(this.nomeThread + ": Riscontrato errore durante la gestione di una richiesta: " + e.toString());
				this.log.debug(this.nomeThread + " : Eccezione :", e);
			} finally {
				try {
					this.dbm.releaseConnection(this.con);
				} catch (Exception eLogger) {
				}
			}
		}

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
				this.log.error(this.nomeThread + ": Riscontrato errore durante la chiusura del Thread: " + e.toString());
			} catch (Exception eLogger) {
			}
		}

		this.isRunning = false;
		this.log.debug(this.nomeThread +" Thread terminato");
	}

	@Override
	public void initGestore() throws Exception {

		// Controllo se dbmanager inizializzato
		// Il DBManager viene inizializzato nell'InitListener
		if (!DBManager.isInitialized()) {
			this.log.info("Inizializzazione di " + this.getClass().getSimpleName() + " non riuscito perche' DBManager non INIZIALIZZATO");
			throw new Exception("Inizializzazione di " + this.getClass().getSimpleName() + "FALLITA");
		}
		
		String pddQueuePrefix = this.consoleProperties.getGestioneCentralizzata_PrefissoNomeCodaConfigurazionePdd();
		boolean enginePDD = this.consoleProperties.isGestioneCentralizzata_SincronizzazionePdd();
		this.singlePdD = this.consoleProperties.isSinglePdD();
		
		if (enginePDD == false) {
			//this.log.info("Motore di sincronizzazione verso le Porte di Dominio non attivo.");
			throw new GestoreNonAttivoException("Motore di sincronizzazione verso le Porte di Dominio non attivo.");
		}

		// Leggo informazioni per queue.properties
		// Init JMS
		// readQueueProperties(cfName, cfProp);

		QueueManager queueMan = QueueManager.getInstance();
		if (queueMan == null) {
			this.log.debug("Impossibile avviare " + this.getClass().getSimpleName() + "QueueManager non inizializzato.");
			throw new Exception("Impossibile avviare " + this.getClass().getSimpleName() + "QueueManager non inizializzato.");
		}
		boolean trovato = false;
		int i = 0;
		this.log.debug("Inizializzazione Receiver [" + this.nomeThread + "] ...");
		while (!trovato && (i < 600000)) {
			try {
				// InitialContext ctx = new InitialContext(cfProp);
				// queue = (Queue) ctx.lookup(pddQueuePrefix + queueName);
				// qcf = (QueueConnectionFactory) ctx.lookup(cfName);

				this.queue = queueMan.getQueue(pddQueuePrefix + this.pddName);
				this.qcf = queueMan.getQueueConnectionFactory();
				this.qc = this.qcf.createQueueConnection();
				this.qc.setExceptionListener(this.exceptionListenerJMS);
				this.qs = this.qc.createQueueSession(true, -1);
				this.receiver = this.qs.createReceiver(this.queue);
				this.qc.start();
				// ctx.close();
				this.log.debug("GestorePdD: Inizializzazione Receiver [" + this.nomeThread + "] effettuata.");
				trovato = true;
			} catch (Exception e) {
				i = i + 10000;
				try {
					Thread.sleep(10000);
					this.log.debug("Ritento Inizializzazione Receiver [" + this.nomeThread + "] ... causa: " + e.getMessage());
				} catch (Exception et) {
				}
			}
		}

		if (!trovato) {
			this.log.error("Inizializzazione Receiver [" + this.nomeThread + "] non effettuata");
			throw new Exception("Inizializzazione Receiver [" + this.nomeThread + "] non effettuata");
		}

		
		// Init ControlStationCore
		try {
			this.pddCore = new PddCore();
			this.soggettiCore = new SoggettiCore(this.pddCore);
			this.paCore = new PorteApplicativeCore(this.pddCore);
			this.pdCore = new PorteDelegateCore(this.pddCore);
			this.saCore = new ServiziApplicativiCore(this.pddCore);

			this.log.debug("GestorePdD-"+this.pddName+": Inizializzato Core. ");
			
		} catch (Exception e) {
			this.log.error("Riscontrato Errore durante l'inizializzazione di ControlStationCore.", e);
			this.log.info("GestorePdD-"+this.pddName+" non avviato.");
			throw new ControlStationCoreException("Riscontrato Errore durante l'inizializzazione di ControlStationCore.", e);
		}
		
		
		// Init WebServiceCore
		try {
			String ipPdd = null;
			String protocollo = null;
			int porta = 80;
			try {
				// ipPdd = backEndConnector.getIPPdd(queueName);
				PdDControlStation pdd = this.pddCore.getPdDControlStation(this.pddName);
				ipPdd = pdd.getIpGestione();
				protocollo = pdd.getProtocolloGestione();
				porta = pdd.getPortaGestione();

				if (ipPdd == null || protocollo == null || porta <= 0)
					throw new Exception("Parametri Porta di Dominio non validi.");

			} catch (Exception e) {
				this.log.error("Riscontrato errore durante la get dell'ip del pdd(" + this.pddName + "): " + e.toString(), e);
				throw new Exception("Riscontrato errore durante la get dell'ip del pdd(" + this.pddName + "): " + e.toString(), e);
			}
			String prefixUrl = protocollo + "://" + ipPdd + ":" + porta + "/";
			
			// ws
			this.portaApplicativaService = new PortaApplicativaSoap11Service();
			this.portaDelegataService = new PortaDelegataSoap11Service();
			this.servizioApplicativoService = new ServizioApplicativoSoap11Service();
			this.soggettoService = new SoggettoSoap11Service();
			
			this.portaApplicativaPort = this.portaApplicativaService.getPortaApplicativaPortSoap11();
			this.portaDelegataPort = this.portaDelegataService.getPortaDelegataPortSoap11();
			this.servizioApplicativoPort = this.servizioApplicativoService.getServizioApplicativoPortSoap11();
			this.soggettoPort = this.soggettoService.getSoggettoPortSoap11();
			
			((BindingProvider)this.portaApplicativaPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixUrl+this.consoleProperties.getGestioneCentralizzata_WSConfigurazione_endpointSuffixPortaApplicativa());
			((BindingProvider)this.portaDelegataPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixUrl+this.consoleProperties.getGestioneCentralizzata_WSConfigurazione_endpointSuffixPortaDelegata());
			((BindingProvider)this.servizioApplicativoPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixUrl+this.consoleProperties.getGestioneCentralizzata_WSConfigurazione_endpointSuffixServizioApplicativo());
			((BindingProvider)this.soggettoPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixUrl+this.consoleProperties.getGestioneCentralizzata_WSConfigurazione_endpointSuffixSoggetto());
			
			((BindingProvider)this.portaApplicativaPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.portaDelegataPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.servizioApplicativoPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.soggettoPort).getRequestContext().put("schema-validation-enabled", true);
			
			String username = this.consoleProperties.getGestioneCentralizzata_WSConfigurazione_credenzialiBasic_username();
			String password = this.consoleProperties.getGestioneCentralizzata_WSConfigurazione_credenzialiBasic_password();
			if(username !=null && password!=null){
				// to use Basic HTTP Authentication: 
				
				((BindingProvider)this.portaApplicativaPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.portaApplicativaPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.portaDelegataPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.portaDelegataPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.servizioApplicativoPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.servizioApplicativoPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.soggettoPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.soggettoPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
			}
			
			this.log.debug("GestorePdD-"+this.pddName+": Inizializzato WebService. PortaApplicativa: " + this.portaApplicativaService.getClass().getSimpleName());
			this.log.debug("GestorePdD-"+this.pddName+": Inizializzato WebService. PortaDelegata: " + this.portaDelegataService.getClass().getSimpleName());
			this.log.debug("GestorePdD-"+this.pddName+": Inizializzato WebService. ServizioApplicativo: " + this.servizioApplicativoService.getClass().getSimpleName());
			this.log.debug("GestorePdD-"+this.pddName+": Inizializzato WebService. Soggetto: " + this.soggettoService.getClass().getSimpleName());
			
		} catch (Exception e) {
			this.log.error("Riscontrato Errore durante la connessione al WebService.", e);
			this.log.info("GestorePdD-"+this.pddName+" non avviato.");
			throw new Exception("Riscontrato Errore durante la connessione al WebService.", e);
		}
		
	}

	@Override
	public void stopGestore() {
		this.stop = true;
	}

	@Override
	protected String getName() {
		return this.nomeThread;
	}
}
