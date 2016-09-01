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
import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.resources.ExceptionListenerJMS;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.config.DatasourceProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.core.QueueManager;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.OperationsParameter;
import org.openspcoop2.web.ctrlstat.costanti.TipoOggettoDaSmistare;
import org.openspcoop2.web.ctrlstat.dao.PoliticheSicurezza;
import org.openspcoop2.web.ctrlstat.dao.Ruolo;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.driver.IRepositoryAutorizzazioniDriverCRUD;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.lib.queue.ClassQueue;
import org.openspcoop2.web.lib.queue.ClassQueueException;
import org.openspcoop2.web.lib.queue.costanti.OperationStatus;
import org.openspcoop2.web.lib.queue.costanti.Operazione;
import org.openspcoop2.web.lib.queue.costanti.TipoOperazione;
import org.openspcoop2.web.lib.queue.dao.FilterParameter;
import org.openspcoop2.web.lib.queue.dao.Operation;
import org.openspcoop2.web.lib.queue.dao.Parameter;

/**
 * GestoreRepositoryAutorizzazioniThread
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GestoreRepositoryAutorizzazioniThread extends GestoreGeneral {

	private ConsoleProperties consoleProperties;
	private DatasourceProperties datasourceProperties;
	
	/** run */
	private boolean stop = false;
	private boolean isRunning = false;
	public boolean isRunning() {
		return this.isRunning;
	}
	
	private String name = null;

	private IRepositoryAutorizzazioniDriverCRUD gestoreRepositoryAutorizzazioni = null;
	private DriverControlStationDB driver = null;

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

	private Logger log;

	private ExceptionListenerJMS exceptionListenerJMS = new ExceptionListenerJMS();

	/** Costruttore 
	 * @throws OpenSPCoop2ConfigurationException */
	public GestoreRepositoryAutorizzazioniThread() throws OpenSPCoop2ConfigurationException {

		this.log = ControlStationLogger.getGestoreAutorizzazioneLogger();
		this.name = "GestoreRepositoryAutorizzazioni";

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
				this.log.warn("GestoreRepositoryAutorizzazioni non avviato: pddConsole avviata in singlePdD mode.");
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

				this.log.info("GestoreRepositoryAutorizzazioni: Ricezione operazione...");
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
					this.log.error("GestoreRepositoryAutorizzazioni: Ricevuta richiesta con tipo errato:" + e.toString(), e);
					this.qs.commit();
					continue;
				}
				String idOperazione = richiesta.getStringProperty("ID");

				int idOp = (int) Integer.parseInt(objOp.toString());
				if (idOp == 0) {
					this.log.error("GestoreRepositoryAutorizzazioni: Ricevuta richiesta con parametri scorretti.");
					this.qs.commit();
					continue;
				}

				this.log.info(CostantiControlStation.OPERATIONS_DELIMITER+"GestoreRepositoryAutorizzazioni: Ricevuta richiesta di operazione con ID: [" + idOperazione + "] id_operation [" + idOp + "]");

				// Connessione al db
				this.dbm = DBManager.getInstance();
				this.con = this.dbm.getConnection();

				this.driver = new DriverControlStationDB(this.con, null, this.datasourceProperties.getTipoDatabase());

				// Prendo i dati dell'operazione
				String tipoOperazioneParam = "", tipoOperazioneCRUDParam = "", singleSu = "";
				int deleted = 0;
				operationManager = new ClassQueue(this.con, this.driver.getTipoDatabase());
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
				GestioneOperazione operazioneInGestione = new GestioneOperazione(this.dbm, this.con, this.qs, operation, operationManager, this.log, tipoOperazioneCRUDParam, "GestoreRepositoryAutorizzazioni");

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



				if (!TipoOggettoDaSmistare.ruolo.equals(tipoOggettoDaSmistare) && 
						//!TipoOggettoDaSmistare.fruitore.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.politicheSicurezza.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.soggetto.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.servizio.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.pa.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.pd.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.servizioApplicativo.equals(tipoOggettoDaSmistare) && 
						!TipoOggettoDaSmistare.accordoRuolo.equals(tipoOggettoDaSmistare)) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (oggetto:" + tipoOggettoDaSmistare.name() + ")");
					continue;
				}

				if (!TipoOperazione.javaInterface.equals(tipoOperazione)) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (tipo operazione:" + tipoOperazione.name() + " non supportata dal gestore)");
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
				} else if (TipoOggettoDaSmistare.servizio.equals(tipoOggettoDaSmistare)) {

					// Chiave primaria che identifica un servizio e'
					// soggettoErogatore e servizio
					String tipoSoggFiltro = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
					String nomeSoggFiltro = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
					String tipoServFiltro = operation.getParameterValue(OperationsParameter.TIPO_SERVIZIO.getNome());
					String nomeServFiltro = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO.getNome());
					FilterParameter servizioFiltro = new FilterParameter();
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SERVIZIO.getNome(), tipoServFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SERVIZIO.getNome(), nomeServFiltro));
					filtroOrdine.add(servizioFiltro);

					// OLD valori per tipo e nome se abbiamo una operazione di
					// change del servizio o add/del di fruitore
					if ((Operazione.change.equals(tipoOperazioneCRUD)) ){// || (TipoOggettoDaSmistare.fruitore.equals(tipoOggettoDaSmistare))) {
						String oldtipoServizio = operation.getParameterValue(OperationsParameter.OLD_TIPO_SERVIZIO.getNome());
						String oldNomeServizio = operation.getParameterValue(OperationsParameter.OLD_NOME_SERVIZIO.getNome());
						FilterParameter servizioOldFiltro = new FilterParameter();
						servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
						servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
						servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SERVIZIO.getNome(), oldtipoServizio));
						servizioOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SERVIZIO.getNome(), oldNomeServizio));
						filtroOrdine.add(servizioOldFiltro);
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

					// OLD valori per tipo e nome soggetto proprietario
					// se abbiamo una operazione di change
					if ((Operazione.change.equals(tipoOperazioneCRUD))) {
						String oldTipoSoggettoFiltro = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSoggettoFiltro = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());
						FilterParameter saOldFiltro = new FilterParameter();
						saOldFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome(), nomeServizioApplicativoFiltro));
						saOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SOGGETTO.getNome(), oldTipoSoggettoFiltro));
						saOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SOGGETTO.getNome(), oldNomeSoggettoFiltro));
						filtroOrdine.add(saOldFiltro);
					}
				} else if (TipoOggettoDaSmistare.politicheSicurezza.equals(tipoOggettoDaSmistare)) {

					if (Operazione.del.equals(tipoOperazioneCRUD)) {
						String idFruitoreFiltro = operation.getParameterValue(OperationsParameter.PS_ID_FRUITORE.getNome());
						String idServizioFiltro = operation.getParameterValue(OperationsParameter.PS_ID_SERVIZIO.getNome());
						String idSA = operation.getParameterValue(OperationsParameter.PS_ID_SERVIZIO_APPLICATIVO.getNome());

						FilterParameter politicheSicurezzaFiltro = new FilterParameter();
						politicheSicurezzaFiltro.addFilterParameter(new Parameter(OperationsParameter.PS_ID_FRUITORE.getNome(), idFruitoreFiltro));
						politicheSicurezzaFiltro.addFilterParameter(new Parameter(OperationsParameter.PS_ID_SERVIZIO.getNome(), idServizioFiltro));
						politicheSicurezzaFiltro.addFilterParameter(new Parameter(OperationsParameter.PS_ID_SERVIZIO_APPLICATIVO.getNome(), idSA));
						filtroOrdine.add(politicheSicurezzaFiltro);
					}
				} 
				/*
				else if (TipoOggettoDaSmistare.fruitore.equals(tipoOggettoDaSmistare)) {

					String tipoSoggFiltro = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
					String nomeSoggFiltro = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
					String tipoServFiltro = operation.getParameterValue(OperationsParameter.TIPO_SERVIZIO.getNome());
					String nomeServFiltro = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO.getNome());
					String tipoFruitoreFiltro = operation.getParameterValue(OperationsParameter.TIPO_FRUITORE.getNome());
					String nomeFruitoreFiltro = operation.getParameterValue(OperationsParameter.NOME_FRUITORE.getNome());

					if (singleOp.equals("add") || singleOp.equals("change")) {
						String idFruitoreFiltro = operation.getParameterValue(OperationsParameter.PS_ID_FRUITORE.getNome());
						FilterParameter fruitoreIDFiltro = new FilterParameter();
						fruitoreIDFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
						fruitoreIDFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
						fruitoreIDFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SERVIZIO.getNome(), tipoServFiltro));
						fruitoreIDFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SERVIZIO.getNome(), nomeServFiltro));
						fruitoreIDFiltro.addFilterParameter(new Parameter(OperationsParameter.PS_ID_FRUITORE.getNome(), idFruitoreFiltro));
						filtroOrdine.add(fruitoreIDFiltro);
					}

					FilterParameter fruitoreFiltro = new FilterParameter();
					fruitoreFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
					fruitoreFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
					fruitoreFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SERVIZIO.getNome(), tipoServFiltro));
					fruitoreFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SERVIZIO.getNome(), nomeServFiltro));
					fruitoreFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_FRUITORE.getNome(), tipoFruitoreFiltro));
					fruitoreFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_FRUITORE.getNome(), nomeFruitoreFiltro));
					filtroOrdine.add(fruitoreFiltro);
				} */
				else if (TipoOggettoDaSmistare.ruolo.equals(tipoOggettoDaSmistare)) {
					if (Operazione.del.equals(tipoOperazioneCRUD)) {
						FilterParameter filtroRuolo = new FilterParameter();
						String nomeSA = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome());
						String tipoProprietarioSA = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeProprietarioSA = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());

						filtroRuolo.addFilterParameter(new Parameter(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome(), nomeSA));
						filtroRuolo.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoProprietarioSA));
						filtroRuolo.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeProprietarioSA));

						filtroOrdine.add(filtroRuolo);
					}
				} else if (TipoOggettoDaSmistare.accordo.equals(tipoOggettoDaSmistare) || TipoOggettoDaSmistare.accordoRuolo.equals(tipoOggettoDaSmistare)) {

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

					/*  SOGGETTI */
					if (TipoOggettoDaSmistare.soggetto.equals(tipoOggettoDaSmistare)) {

						String oldTipoSoggetto = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSoggetto = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());
						String tipoSogg = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSogg = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());

						// DELETE
						if (Operazione.del.equals(tipoOperazioneCRUD)) {
							try {

								Soggetto sogg = new Soggetto();
								sogg.setNome(nomeSogg);
								sogg.setTipo(tipoSogg);

								String pattern = "Soggetto per RepositoryAutorizzazioni :\n\t Nome [{0}] \n\tDescr[{1}] \n\tTipo[{2}] \n\tServer[{3}]";
								String info = MessageFormat.format(pattern, sogg.getNome(), sogg.getDescrizione(), sogg.getTipo(), sogg.getPortaDominio());

								this.log.info(info);
								if (this.gestoreRepositoryAutorizzazioni.existsSoggetto(new IDSoggetto(tipoSogg, nomeSogg))) {
									this.gestoreRepositoryAutorizzazioni.deleteSoggetto(sogg);
									this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");
								} else {
									this.log.info("Il soggetto non esiste nel repository RepositoryAutorizzazioni, non e' necessaria la cancellazione.");
								}

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di eliminazione soggetto [ " + tipoOperazioneCRUD + "]: " + e.toString(), e);
								continue;
							}

						} else {

							// ADD/CHANGE
							try {

								Soggetto sogg = this.driver.getDriverRegistroServiziDB().getSoggetto(idTable);
								sogg.setOldNomeForUpdate(oldNomeSoggetto);
								sogg.setOldTipoForUpdate(oldTipoSoggetto);

								String pattern = "Soggetto per RepositoryAutorizzazioni :\n\t Nome [{0}] \n\tDescr[{1}] \n\tTipo[{2}] \n\tServer[{3}]";
								String info = MessageFormat.format(pattern, sogg.getNome(), sogg.getDescrizione(), sogg.getTipo(), sogg.getPortaDominio());

								this.log.info(info);

								// create
								if (Operazione.change.equals(tipoOperazioneCRUD)) {
									this.gestoreRepositoryAutorizzazioni.createSoggetto(sogg);
								}
								// update
								else if (Operazione.change.equals(tipoOperazioneCRUD)) {
									this.gestoreRepositoryAutorizzazioni.updateSoggetto(sogg);
								}

								this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");

							} catch (DriverRegistroServiziNotFound de) {
								operazioneInGestione.waitBeforeInvalid("Soggetto non esistente nel database della pddConsole: " + de.toString());
								continue;
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore durante la create/update soggetto [ " + tipoOperazioneCRUD + "]: " + e.toString(), e);
								continue;
							}
						}
					}

					// SERVIZIO
					if (TipoOggettoDaSmistare.servizio.equals(tipoOggettoDaSmistare)) {

						String tipoSogg = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSogg = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
						String tipoServ = operation.getParameterValue(OperationsParameter.TIPO_SERVIZIO.getNome());
						String nomeServ = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO.getNome());
						String oldTipoServizio = operation.getParameterValue(OperationsParameter.OLD_TIPO_SERVIZIO.getNome());
						String oldNomeServizio = operation.getParameterValue(OperationsParameter.OLD_NOME_SERVIZIO.getNome());

						// DELETE
						if (Operazione.del.equals(tipoOperazioneCRUD)) {
							try {
								AccordoServizioParteSpecifica serv = new AccordoServizioParteSpecifica();
								serv.setServizio(new Servizio());

								serv.getServizio().setNome(nomeServ);
								serv.getServizio().setTipo(tipoServ);
								serv.getServizio().setNomeSoggettoErogatore(nomeSogg);
								serv.getServizio().setTipoSoggettoErogatore(tipoSogg);

								String pattern = "Servizio per RepositoryAutorizzazioni :" + "\n\t Nome [{0}] " + "\n\t Tipo[{1}] " + "\n\t AccordoServizio[{2}] " + "\n\t NomeSoggettoErogatore[{3}]" + "\n\t TipoSoggettoErogatore[{4}]" + "\n\t ServizioCorrelato[{5}]";
								String info = MessageFormat.format(pattern, serv.getServizio().getNome(), serv.getServizio().getTipo(), 
										serv.getAccordoServizioParteComune(), serv.getServizio().getNomeSoggettoErogatore(), 
										serv.getServizio().getTipoSoggettoErogatore(), TipologiaServizio.CORRELATO.equals(serv.getServizio().getTipologiaServizio()));

								this.log.info(info);

								// delete
								IDServizio idSE = new IDServizio(tipoSogg, nomeSogg);
								idSE.setTipoServizio(tipoServ);
								idSE.setServizio(nomeServ);
								if (this.gestoreRepositoryAutorizzazioni.existsServizio(idSE))
									this.gestoreRepositoryAutorizzazioni.deleteServizio(serv);
								else
									this.log.info("Servizio non esistente nel repository RepositoryAutorizzazioni, delete non necessaria.");

								this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di delete servizio: " + e.toString(), e);
								continue;
							}
						} else {

							// ADD/CHANGE

							AccordoServizioParteSpecifica serv = null;
							try {
								serv = this.driver.getDriverRegistroServiziDB().getAccordoServizioParteSpecifica(idTable);
							} catch (DriverRegistroServiziNotFound de) {
								operazioneInGestione.waitBeforeInvalid("Servizio non esistente nel database della pddConsole: " + de.toString());
								continue;
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del servizio: " + e.getMessage(), e);
								continue;
							}

							AccordoServizioParteComune as = null;
							try {
								as = this.driver.getDriverRegistroServiziDB().getAccordoServizioParteComune( IDAccordoFactory.getInstance().getIDAccordoFromUri(serv.getAccordoServizioParteComune()));
							} catch (DriverRegistroServiziNotFound de) {
								operazioneInGestione.waitBeforeInvalid("Accordo di Servizio non esistente nel database della pddConsole: " + de.toString());
								continue;
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore durante la get dell'immagine dell'accordo di servizio: " + e.getMessage(), e);
								continue;
							}

							try {

								serv.getServizio().setOldNomeForUpdate(oldNomeServizio);
								serv.getServizio().setOldTipoForUpdate(oldTipoServizio);

								// serv.setOldNomeSoggettoForUpdate(servizio.getNomeSoggetto());//questo
								// non viene modificato nel servizio
								// serv.setOldTipoSoggettoForUpdate(servizio.getTipoSoggetto());//quindi
								// va bene utilizzare gli stessi nome e tipo
								// sogg
								// erogatore

								String pattern = "Servizio per RepositoryAutorizzazioni :" + "\n\t Nome [{0}] " + "\n\t Tipo[{1}] " + "\n\t AccordoServizio[{2}] " + "\n\t NomeSoggettoErogatore[{3}]" + "\n\t TipoSoggettoErogatore[{4}]" + "\n\t ServizioCorrelato[{5}]";
								String info = MessageFormat.format(pattern, serv.getServizio().getNome(), serv.getServizio().getTipo(),
										serv.getAccordoServizioParteComune(), 
										serv.getServizio().getNomeSoggettoErogatore(), serv.getServizio().getTipoSoggettoErogatore(),
										TipologiaServizio.CORRELATO.equals(serv.getServizio().getTipologiaServizio()));

								this.log.info(info);

								this.log.info("\n\tAzioni presenti [" + as.sizeAzioneList() + "] ...");
								// aggiungo azioni
								for (int i = 0; i < as.sizeAzioneList(); i++) {
									pattern = "\n\tAzione [{0}] --> Nome: {1}";

									info = MessageFormat.format(pattern, i + "", as.getAzione(i).getNome());
									this.log.info(info);
								}

								// Controllo elementi obbligatori
								// if(validazioneServizio(servizio)==false){
								// qs.commit();
								// dbm.releaseConnection(con);
								// continue;
								// }

								// create
								if (Operazione.change.equals(tipoOperazioneCRUD)) {
									this.gestoreRepositoryAutorizzazioni.createServizio(as, serv);
								}
								// update
								else if (Operazione.change.equals(tipoOperazioneCRUD)) {
									this.gestoreRepositoryAutorizzazioni.updateServizio(as, serv);
								}

								this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di create/update servizio: " + e.toString(), e);
								continue;
							}
						}
					}

					// SERVIZIOAPPLICATIVO
					if (TipoOggettoDaSmistare.servizioApplicativo.equals(tipoOggettoDaSmistare)) {

						String nomeServizioApplicativo = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome());
						String tipoSoggetto = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSoggetto = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
						String oldTipoSoggetto = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSoggetto = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());

						// DELETE
						if (Operazione.del.equals(tipoOperazioneCRUD)) {
							try {
								ServizioApplicativo sa = new ServizioApplicativo();
								sa.setNome(nomeServizioApplicativo);
								sa.setTipoSoggettoProprietario(tipoSoggetto);
								sa.setNomeSoggettoProprietario(nomeSoggetto);

								String pattern = "ServizioApplicativo per RepositoryAutorizzazioni :" + "\n\t Nome [{0}] ";

								String info = MessageFormat.format(pattern, sa.getNome());

								this.log.info(info);
								if (this.gestoreRepositoryAutorizzazioni.existsServizioApplicativo(new IDSoggetto(tipoSoggetto, nomeSoggetto), nomeServizioApplicativo))
									this.gestoreRepositoryAutorizzazioni.deleteServizioApplicativo(sa);
								else
									this.log.debug("Servizio Applicativo non esiste nel repository RepositoryAutorizzazioni, delete non necessaria.");

								this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di delete servizio applicativo: " + e.toString(), e);
								continue;
							}
						} else {

							try {

								ServizioApplicativo sa = null;
								try {
									sa = this.driver.getDriverConfigurazioneDB().getServizioApplicativo(idTable);
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Servizio Applicativo non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del servizio applicativo: " + e.getMessage(), e);
									continue;
								}

								if ((sa.getDescrizione() == null) || sa.getDescrizione().equals("")) {
									sa.setDescrizione("ServizioApplicativo [" + sa.getNome() + "] di [" + sa.getTipoSoggettoProprietario() + sa.getNomeSoggettoProprietario() + "]");// la
									// descrizione
									// e'
									// vuota
								}

								String pattern = "ServizioApplicativo per RepositoryAutorizzazioni :" + "\n\t Nome [{0}] " + "\n\t Descrizione [{1}] " + "\n\t NomeSoggetto [{2}]" + "\n\t TipoSoggetto [{3}]";

								String info = MessageFormat.format(pattern, sa.getNome(), sa.getDescrizione(), sa.getNomeSoggettoProprietario(), sa.getTipoSoggettoProprietario());

								this.log.info(info);

								// Controllo elementi obbligatori
								// se la validazione va male effettuo rollback e
								// continuo a processare i messaggi
								// if (!validazioneServizioApplicativo(s)) {
								// qs.commit();
								// dbm.releaseConnection(con);
								// continue;
								// }

								// create
								if (Operazione.change.equals(tipoOperazioneCRUD)) {
									this.gestoreRepositoryAutorizzazioni.createServizioApplicativo(sa);
								}
								// update
								else if (Operazione.change.equals(tipoOperazioneCRUD)) {
									sa.setOldNomeForUpdate(sa.getNome());// il
									// nome
									// nn
									// puo
									// cambiare
									sa.setOldNomeSoggettoProprietarioForUpdate(oldNomeSoggetto);
									sa.setOldTipoSoggettoProprietarioForUpdate(oldTipoSoggetto);
									this.gestoreRepositoryAutorizzazioni.updateServizioApplicativo(sa);
								}

								this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di update/create servizio applicativo: " + e.toString(), e);
								continue;
							}
						}
					}

					// POLITICHE SICUREZZA
					if (TipoOggettoDaSmistare.politicheSicurezza.equals(tipoOggettoDaSmistare)) {

						// questo e' un caso particolare di operazione
						// 'fruitore'
						// in quanto viene utilizzata solo quando viene inserita
						// o cancellata
						// un solo servizio applicativo (politicaSicurezza)

						// delete
						// la delete qui nn viene gestita come caso particolare
						try {

							PoliticheSicurezza ps = this.driver.getPoliticheSicurezza(idTable);

							if (ps == null) {
								operazioneInGestione.waitBeforeInvalid("Politiche di Sicurezza con id[" + idTable + "] non esistente nel database della pddConsole");
								continue;
							}

							AccordoServizioParteSpecifica serv = null;
							ServizioApplicativo sa = null;
							Soggetto soggFruitore = null;

							if (Operazione.change.equals(tipoOperazioneCRUD)) {
								this.log.debug("Operazione add id PoliticheSicurezza: " + idTable);
								try {
									serv = this.driver.getDriverRegistroServiziDB().getAccordoServizioParteSpecifica(ps.getIdServizio());
								} catch (DriverRegistroServiziNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Servizio non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del servizio: " + e.getMessage(), e);
									continue;
								}
								try {
									sa = this.driver.getDriverConfigurazioneDB().getServizioApplicativo(ps.getIdServizioApplicativo());
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Servizio Applicativo non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del servizio applicativo: " + e.getMessage(), e);
									continue;
								}
								try {
									soggFruitore = this.driver.getDriverRegistroServiziDB().getSoggetto(ps.getIdFruitore());
								} catch (DriverRegistroServiziNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Fruitore non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del soggetto(fruitore): " + e.getMessage(), e);
									continue;
								}
							} else if (Operazione.del.equals(tipoOperazioneCRUD)) {

								int idFruitore = 0;
								try {
									idFruitore = Integer.parseInt(operation.getParameterValue(OperationsParameter.PS_ID_FRUITORE.getNome()));
								} catch (Exception e) {
									operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (idFruitore) non valida: " + e.getMessage());
									continue;
								}

								int idServizio = 0;
								try {
									idServizio = Integer.parseInt(operation.getParameterValue(OperationsParameter.PS_ID_SERVIZIO.getNome()));
								} catch (Exception e) {
									operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (idServizio) non valida: " + e.getMessage());
									continue;
								}

								int idSA = 0;
								try {
									idSA = Integer.parseInt(operation.getParameterValue(OperationsParameter.PS_ID_SERVIZIO_APPLICATIVO.getNome()));
								} catch (Exception e) {
									operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (idServizioApplicativo) non valida: " + e.getMessage());
									continue;
								}

								this.log.debug("Operazione del idServizio: " + idServizio + " idSA: " + idSA + " idFruitore: " + idFruitore);
								try {
									serv = this.driver.getDriverRegistroServiziDB().getAccordoServizioParteSpecifica(idServizio);
								} catch (DriverRegistroServiziNotFound de) {
									operazioneInGestione.invalid("Servizio non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del servizio: " + e.getMessage(), e);
									continue;
								}
								try {
									sa = this.driver.getDriverConfigurazioneDB().getServizioApplicativo(idSA);
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.invalid("Servizio Applicativo non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del servizio applicativo: " + e.getMessage(), e);
									continue;
								}
								try {
									soggFruitore = this.driver.getDriverRegistroServiziDB().getSoggetto(idFruitore);
								} catch (DriverRegistroServiziNotFound de) {
									operazioneInGestione.invalid("Fruitore non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del soggetto(fruitore): " + e.getMessage(), e);
									continue;
								}
							}

							// recupero il fruitore che mi interessa (e
							// solamente quello)
							// Fruitore
							// fru=backEndConnector.getFruitore(idServizio,
							// idFruitore);

							Fruitore fruitore = new Fruitore();

							fruitore.setNome(soggFruitore.getNome());
							fruitore.setTipo(soggFruitore.getTipo());

							// aggiungo il servizio applicativo
							// aggiungo il SA al fruitore
							fruitore.addServizioApplicativo(sa.getNome());
							// aggiungo il fruitore al ServizioSPcoop
							serv.addFruitore(fruitore);

							String pattern = "PoliticheSicurezza per RepositoryAutorizzazioni :" + "\n\t[SERVIZIO]" + "\n\t Nome [{0}] " + "\n\t Tipo [{1}] " + "\n\t AccordoServizio [{2}]" + "\n\t NomeSoggettoErogatore [{3}]" + "\n\t TipoSoggettoErogatore [{4}]" + "\n\t ServizioCorrelato [{5}]" + "\n\n\t[FRUITORE]" + "\n\tNome [{6}]" + "\n\tTipo [{7}]" + "\n\t[SERVIZIOAPPLICATIVO]" + "\n\tNome [{8}]";

							String info = MessageFormat.format(pattern, serv.getServizio().getNome(), serv.getServizio().getTipo(), 
									serv.getAccordoServizioParteComune(), 
									serv.getServizio().getNomeSoggettoErogatore(), serv.getServizio().getTipoSoggettoErogatore(), 
									TipologiaServizio.CORRELATO.equals(serv.getServizio().getTipologiaServizio()),
									fruitore.getNome(), fruitore.getTipo(), sa.getNome());

							this.log.info(info);

							// create
							if (Operazione.change.equals(tipoOperazioneCRUD)) {

								this.gestoreRepositoryAutorizzazioni.createFruitore(serv);
							}
							// update non available
							else if (Operazione.del.equals(tipoOperazioneCRUD)) {
								if (this.gestoreRepositoryAutorizzazioni.existsFruitore(new IDSoggetto(fruitore.getTipo(), fruitore.getNome()))) {
									this.gestoreRepositoryAutorizzazioni.deleteFruitore(serv);
								} else {
									this.log.debug("Fruitore non presente nel repository RepositoryAutorizzazioni, cancellazione non necessaria.");
								}

							}

							this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");
						} catch (Exception e) {
							operazioneInGestione.error("Riscontrato errore di creazione/update/delete politicheSicurezza: " + e.toString(), e);
							continue;
						}
					}

					// FRUITORE
					/*
					if (TipoOggettoDaSmistare.fruitore.equals(tipoOggettoDaSmistare)) {

						java.util.ArrayList<String> listaServiziApplicativi = new java.util.ArrayList<String>();

						String tipoSogg = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSogg = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
						String tipoServ = operation.getParameterValue(OperationsParameter.TIPO_SERVIZIO.getNome());
						String nomeServ = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO.getNome());
						String tipoFruitore = operation.getParameterValue(OperationsParameter.TIPO_FRUITORE.getNome());
						String nomeFruitore = operation.getParameterValue(OperationsParameter.NOME_FRUITORE.getNome());

						// aggiungo i nomi dei servizi applicativi
						String saValue = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome());
						if (saValue != null) {
							String[] sa = saValue.split(" ");
							if (sa != null) {
								for (int i = 0; i < sa.length; i++) {
									listaServiziApplicativi.add(sa[i]);
								}
							}
						}

						String toPrint = "Oggetto [{0}]\n" + "TipoOP  [{1}]\n" + "idTable [{2}]\n" + "nomeFru [{3}]\n" + "tipoFru [{4}]\n" + "sizeSA	 [{5}]";
						this.log.info(MessageFormat.format(toPrint, tipoOggettoDaSmistare.name(), tipoOperazioneCRUD, idTable, nomeFruitore, tipoFruitore, "" + listaServiziApplicativi.size()));

						// delete
						if (Operazione.del.equals(tipoOperazioneCRUD)) {

							try {
								AccordoServizioParteSpecifica serv = new AccordoServizioParteSpecifica();
								serv.setServizio(new Servizio());
								serv.getServizio().setNome(nomeServ);
								serv.getServizio().setTipo(tipoServ);
								serv.getServizio().setNomeSoggettoErogatore(nomeSogg);
								serv.getServizio().setTipoSoggettoErogatore(tipoSogg);

								Fruitore fruitore = new Fruitore();
								fruitore.setNome(nomeFruitore);
								fruitore.setTipo(tipoFruitore);
								fruitore.setServizioApplicativoList(listaServiziApplicativi);
								serv.addFruitore(fruitore);

								String pattern = "Fruitore per RepositoryAutorizzazioni :" + "\n\t[SERVIZIO]" + "\n\t Nome [{0}] " + "\n\t Tipo [{1}] " + "\n\t AccordoServizio [{2}]" + "\n\t NomeSoggettoErogatore [{3}]" + "\n\t TipoSoggettoErogatore [{4}]" + "\n\t ServizioCorrelato [{5}]" + "\n\n\t[FRUITORE]" + "\n\tNome [{6}]" + "\n\tTipo [{7}]";

								String info = MessageFormat.format(pattern, serv.getServizio().getNome(), serv.getServizio().getTipo(), 
										serv.getAccordoServizioParteComune(), 
										serv.getServizio().getNomeSoggettoErogatore(), serv.getServizio().getTipoSoggettoErogatore(), 
										TipologiaServizio.CORRELATO.equals(serv.getServizio().getTipologiaServizio()),
										fruitore.getNome(), fruitore.getTipo());

								this.log.info(info);

								this.log.info("\tCancello " + listaServiziApplicativi.size() + " ServiziApplicativi:");
								for (String nomeSA : listaServiziApplicativi) {
									this.log.info("\tServizioApplicativo [" + nomeSA + "]");

								}

								if (this.gestoreRepositoryAutorizzazioni.existsFruitore(new IDSoggetto(fruitore.getTipo(), fruitore.getNome()))) {
									this.gestoreRepositoryAutorizzazioni.deleteFruitore(serv);
								} else {
									this.log.debug("Fruitore non presente nel repository RepositoryAutorizzazioni, cancellazione non necessaria.");
								}

								this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di delete fruitore: " + e.toString(), e);
								continue;
							}

						} else {

							// ADD/CREATE
							try {
								AccordoServizioParteSpecifica tmpServ = this.driver.getDriverRegistroServiziDB().getAccordoServizioParteSpecifica(idTable);
								AccordoServizioParteSpecifica serv = new AccordoServizioParteSpecifica();
								serv.setServizio(new Servizio());

								serv.getServizio().setNome(tmpServ.getServizio().getNome());
								serv.getServizio().setTipo(tmpServ.getServizio().getTipo());
								serv.setAccordoServizioParteComune(tmpServ.getAccordoServizioParteComune());
								serv.getServizio().setNomeSoggettoErogatore(tmpServ.getServizio().getNomeSoggettoErogatore());
								serv.getServizio().setTipoSoggettoErogatore(tmpServ.getServizio().getTipoSoggettoErogatore());
								serv.getServizio().setTipologiaServizio(tmpServ.getServizio().getTipologiaServizio());

								int idFruitore = 0;
								try {
									idFruitore = Integer.parseInt(operation.getParameterValue(OperationsParameter.ID_FRUITORE.getNome()));
								} catch (Exception e) {
									operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (idFruitore) non valida: " + e.getMessage());
									continue;
								}

								// recupero il fruitore che ho aggiunto (e
								// solamente
								// quello)
								// Fruitore
								// fru=backEndConnector.getFruitore(idTable,
								// idFruitore);
								Soggetto soggFruitore = null;
								try {
									soggFruitore = this.driver.getDriverRegistroServiziDB().getSoggetto(idFruitore);
								} catch (DriverRegistroServiziNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Fruitore non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del soggetto(fruitore): " + e.getMessage(), e);
									continue;
								}

								Fruitore fruitore = new Fruitore();
								fruitore.setNome(soggFruitore.getNome());
								fruitore.setTipo(soggFruitore.getTipo());
								serv.addFruitore(fruitore);

								String pattern = "Fruitore per RepositoryAutorizzazioni :" + "\n\t[SERVIZIO]" + "\n\t Nome [{0}] " + "\n\t Tipo [{1}] " + "\n\t AccordoServizio [{2}]" + "\n\t NomeSoggettoErogatore [{3}]" + "\n\t TipoSoggettoErogatore [{4}]" + "\n\t ServizioCorrelato [{5}]" + "\n\n\t[FRUITORE]" + "\n\tNome [{6}]" + "\n\tTipo [{7}]";

								String info = MessageFormat.format(pattern, serv.getServizio().getNome(), serv.getServizio().getTipo(), 
										serv.getAccordoServizioParteComune(), 
										serv.getServizio().getNomeSoggettoErogatore(), serv.getServizio().getTipoSoggettoErogatore(), 
										TipologiaServizio.CORRELATO.equals(serv.getServizio().getTipologiaServizio()),
										fruitore.getNome(), fruitore.getTipo());

								this.log.info(info);

								// Controllo elementi obbligatori
								// if(validazioneServizio(servizio)==false){
								// qs.commit();
								// dbm.releaseConnection(con);
								// continue;
								// }

								// create
								if (Operazione.change.equals(tipoOperazioneCRUD)) {
									this.gestoreRepositoryAutorizzazioni.createFruitore(serv);
								}
								// update non available

								this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di create/update fruitore: " + e.toString(), e);
								continue;
							}
						}
					}
					*/

					// RUOLO
					if (TipoOggettoDaSmistare.ruolo.equals(tipoOggettoDaSmistare)) {

						this.log.debug("Gestione Ruolo [" + tipoOperazioneCRUD + "] id:" + idTable);

						// delete non gestita come caso particolare
						try {
							ServiziApplicativiCore core = new ServiziApplicativiCore();

							// Controllo elementi obbligatori
							// if(validazioneServizio(servizio)==false){
							// qs.commit();
							// dbm.releaseConnection(con);
							// continue;
							// }

							this.log.debug("Eseguo operazione di modifica Servizio Applicativo a seguito di " + tipoOperazioneCRUD + " Ruolo [" + idTable + "]");

							if (tipoOperazioneCRUD.equals(Operazione.add) || tipoOperazioneCRUD.equals(Operazione.change)) {
								Ruolo ruolo = null;
								try {
									ruolo = core.getRuolo(idTable);
								} catch (DriverControlStationNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Ruolo non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del Ruolo: " + e.getMessage(), e);
									continue;
								}
								// il servizio applicativo contiene la lista con
								// i ruoli
								ServizioApplicativo sa = null;
								try {
									sa = core.getServizioApplicativo(ruolo.getIdServizioApplicativo());
								} catch (DriverConfigurazioneNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Servizio Applicativo non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del Servizio Applicativo: " + e.getMessage(), e);
									continue;
								}
								// effettuo operazione di aggiornamento del
								// servizio applicativo
								this.gestoreRepositoryAutorizzazioni.updateServizioApplicativo(sa);

							} else if (tipoOperazioneCRUD.equals(Operazione.del)) {
								String nomeSA = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome());
								String tipoProprietarioSA = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
								String nomeProprietarioSA = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());

								this.log.debug("Eliminazione Servizio Applicativo [" + nomeSA + "] di Soggetto[" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
								if (this.gestoreRepositoryAutorizzazioni.existsServizioApplicativo(new IDSoggetto(tipoProprietarioSA, nomeProprietarioSA), nomeSA)) {
									ServizioApplicativo sa = new ServizioApplicativo();
									sa.setNome(nomeSA);
									sa.setTipoSoggettoProprietario(tipoProprietarioSA);
									sa.setNomeSoggettoProprietario(nomeProprietarioSA);
									this.gestoreRepositoryAutorizzazioni.updateServizioApplicativo(sa);
								} else
									this.log.debug("Servizio Applicativo non esiste nel repository RepositoryAutorizzazioni, delete ruolo non necessaria.");

							}

							this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");

						} catch (Exception e) {
							operazioneInGestione.error("Riscontrato errore durante il create/update/delete del ruolo: " + e.toString(), e);
							continue;
						}
					}

					// ACCORDO
					if (TipoOggettoDaSmistare.accordoRuolo.equals(tipoOggettoDaSmistare)) {

						String nomeAcc = operation.getParameterValue(OperationsParameter.NOME_ACCORDO.getNome());
						String versioneAcc = operation.getParameterValue(OperationsParameter.VERSIONE_ACCORDO.getNome());
						String tipoReferenteAcc = operation.getParameterValue(OperationsParameter.TIPO_REFERENTE.getNome());
						String nomeReferenteAcc = operation.getParameterValue(OperationsParameter.NOME_REFERENTE.getNome());
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAcc,tipoReferenteAcc,nomeReferenteAcc,versioneAcc);
						IDAccordo idAccordoCorrelato = IDAccordoFactory.getInstance().getIDAccordoFromValues(idAccordo.getNome()+"Correlato", idAccordo.getSoggettoReferente(), idAccordo.getVersione());
						
						this.log.debug("Gestione Ruolo id:" + idTable);

						// delete non gestita come caso particolare
						try {
							AccordiServizioParteComuneCore core = new AccordiServizioParteComuneCore();

							this.log.debug("Eseguo operazione di " + tipoOperazioneCRUD + " Ruolo a seguito di " + tipoOperazioneCRUD + " Accordo Servizio [" + idTable + "]");

							if (tipoOperazioneCRUD.equals(Operazione.add)) {
								// String correlato = "Correlato";
								AccordoServizioParteComune as = null;
								try {
									as = core.getAccordoServizio(idTable);
								} catch (DriverRegistroServiziNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Accordo di Servizio non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine dell'accordo: " + e.getMessage(), e);
									continue;
								}
								if (nomeAcc.contains("Correlato")) {
									this.gestoreRepositoryAutorizzazioni.createRuolo(Ruolo.getNomeRuoloByIDAccordo(idAccordoCorrelato), as.getDescrizione() + " [Correlato]");
								} else {
									this.gestoreRepositoryAutorizzazioni.createRuolo(Ruolo.getNomeRuoloByIDAccordo(idAccordo), as.getDescrizione());
								}
							} else if (tipoOperazioneCRUD.equals(Operazione.del)) {
								String nomeRuolo = null;
								if (nomeAcc.contains("Correlato")) {
									nomeRuolo = Ruolo.getNomeRuoloByIDAccordo(idAccordoCorrelato);
								}else{
									nomeRuolo = Ruolo.getNomeRuoloByIDAccordo(idAccordo);
								}								
								if (this.gestoreRepositoryAutorizzazioni.existsRuolo(nomeRuolo))
									this.gestoreRepositoryAutorizzazioni.deleteRuolo(nomeRuolo);
								else
									this.log.debug("Ruolo non presente nel repository RepositoryAutorizzazioni, delete non necessaria.");
							}

							this.log.info("GestoreRepositoryAutorizzazioni : Operazione [" + tipoOperazioneCRUD + "] Completata");

						} catch (Exception e) {
							operazioneInGestione.error("Riscontrato errore durante il create/update/delete dell'accordo: " + e.toString(), e);
							continue;
						}
					}

					// Se arrivo qui, significa che l'operazione non ha dato
					// errore,
					// aggiorno il db
					operazioneInGestione.success("Done.");

				}// chiudo else;

				this.log.info("Operazione [" + idOperazione + "] completata"+statoOperazioneCancellata);

			} catch (javax.jms.JMSException e) {
				try {
					this.qs.rollback();
				} catch (Exception er) {
				}
				this.log.error("GestoreRepositoryAutorizzazioni: Riscontrato errore durante la gestione di una richiesta: " + e.toString(), e);
				try {
					Thread.sleep(5000);
					this.log.debug("GestoreRepositoryAutorizzazioni: Re-Inizializzazione Receiver ...");
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
					this.log.debug("GestoreRepositoryAutorizzazioni: Re-Inizializzazione Receiver effettuata.");
					riconnessioneConErrore = false;
				} catch (Exception er) {
					this.log.error("GestoreRepositoryAutorizzazioni: Re-Inizializzazione Receiver non effettuata:" + er.toString());
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
				try {
					this.log.error("GestoreRepositoryAutorizzazioni: Riscontrato errore durante la gestione di una richiesta: " + e.toString(), e);
				} catch (Exception eLogger) {
				}
			} finally {
				try {
					this.dbm.releaseConnection(this.con);
				} catch (Exception eLogger) {
				}
			}
		}// chiudo while

		this.isRunning = false;
		this.log.debug("Thread terminato");
		
	}

	@Override
	public void initGestore() throws Exception {
		boolean trovato = false;

		// Controllo se dbmanager inizializzato
		// Il DBManager viene inizializzato nell'InitListener
		if (!DBManager.isInitialized()) {
			this.log.info("Inizializzazione di " + this.getClass().getSimpleName() + " non riuscito perche' DBManager non INIZIALIZZATO");
			throw new Exception("Inizializzazione di " + this.getClass().getSimpleName() + "FALLITA");
		}

		// lettura da infoGeneral.cfg
		this.log.debug("Lettura dei parametri da console.properties");
		String repositoryAutorizzazioniQueue = this.consoleProperties.getGestioneCentralizzata_NomeCodaRepositoryAutorizzazioni();
		String classGestoreRepositoryAutorizzazioni = this.consoleProperties.getGestioneCentralizzata_RepositoryAutorizzazioniClassName();
		boolean engineRepositoryAutorizzazioni = this.consoleProperties.isGestioneCentralizzata_SincronizzazioneRepositoryAutorizzazioni();
		this.singlePdD = this.consoleProperties.isSinglePdD();

		if (engineRepositoryAutorizzazioni == false) {
			//this.log.info("Motore di sincronizzazione verso il Repository RepositoryAutorizzazioni non attivo.");
			throw new GestoreNonAttivoException("Motore di sincronizzazione verso il Repository RepositoryAutorizzazioni non attivo.");
		}

		if( this.singlePdD ){
			throw new GestoreNonAttivoException("GestoreRepositoryAutorizzazioni non avviato: pddConsole avviata in singlePdD mode.");
		}
		
		if (repositoryAutorizzazioniQueue.equals("")) {
			this.log.error("GestoreRepositoryAutorizzazioni: Il gestore RepositoryAutorizzazioni non e' attivo.");
			throw new Exception("GestoreRepositoryAutorizzazioni: Il gestore RepositoryAutorizzazioni non e' attivo.");
		}

		if (classGestoreRepositoryAutorizzazioni.equals("")) {
			this.log.error("GestoreRepositoryAutorizzazioni: Il parametro RepositoryAutorizzazioniClassName non e' specificato il parametro e' necessario. RepositoryAutorizzazioni non e' attivo.");
			throw new Exception("GestoreRepositoryAutorizzazioni: Il parametro RepositoryAutorizzazioniClassName non e' specificato il parametro e' necessario. RepositoryAutorizzazioni non e' attivo.");
		}

		// istanzio il gestore RepositoryAutorizzazioni

		try {
			this.gestoreRepositoryAutorizzazioni = (IRepositoryAutorizzazioniDriverCRUD) Class.forName(classGestoreRepositoryAutorizzazioni).newInstance();
		} catch (Exception e) {
			this.log.error("GestoreRepositoryAutorizzazioni: Impossibile inizializzare l'istanza RepositoryAutorizzazioniClassName[" + classGestoreRepositoryAutorizzazioni + "] :" + e.getMessage(), e);

			this.log.error("GestoreRepositoryAutorizzazioni: RepositoryAutorizzazioni non Attivo.");
			throw new Exception("GestoreRepositoryAutorizzazioni: Impossibile inizializzare l'istanza RepositoryAutorizzazioniClassName[" + classGestoreRepositoryAutorizzazioni + "]", e);
		}

		// Configurazione JMS
		this.log.debug("GestoreRepositoryAutorizzazioni: Avvio Servizio di Gestione RepositoryAutorizzazioni");
		String pattern = "Parametri : \nRepositoryAutorizzazioniQueue: [{0}] \nRepositoryAutorizzazioniClassName:[{1}] \nsincronizzazioneRepositoryAutorizzazioni [{2}]";
		this.log.debug(MessageFormat.format(pattern, repositoryAutorizzazioniQueue, classGestoreRepositoryAutorizzazioni, engineRepositoryAutorizzazioni));
		// Leggo informazioni per queue.properties
		// Init JMS
		// readQueueProperties(cfName, cfProp);
		QueueManager queueMan = QueueManager.getInstance();

		if (queueMan == null) {
			this.log.debug("Impossibile avviare " + this.getClass().getSimpleName() + "QueueManager non inizializzato.");
			throw new Exception("Impossibile avviare " + this.getClass().getSimpleName() + "QueueManager non inizializzato.");
		}

		int i = 0;
		this.log.debug("GestoreRepositoryAutorizzazioni: Inizializzazione Receiver ...");
		while (!trovato && (i < 600000)) {
			try {
				// InitialContext ctx = new InitialContext(cfProp);
				// queue = (Queue) ctx.lookup(RepositoryAutorizzazioniQueue);
				// qcf = (QueueConnectionFactory) ctx.lookup(cfName);
				this.queue = queueMan.getQueue(repositoryAutorizzazioniQueue);
				this.qcf = queueMan.getQueueConnectionFactory();
				this.qc = this.qcf.createQueueConnection();
				this.qc.setExceptionListener(this.exceptionListenerJMS);
				this.qs = this.qc.createQueueSession(true, -1);
				this.receiver = this.qs.createReceiver(this.queue);
				this.qc.start();
				// ctx.close();
				this.log.debug("GestoreRepositoryAutorizzazioni: Inizializzazione Receiver effettuata.");
				trovato = true;
			} catch (Exception e) {
				i = i + 10000;
				try {
					Thread.sleep(10000);
					this.log.debug("Ritento Inizializzazione Receiver ... causa:" + e.getMessage());
				} catch (Exception et) {
				}
			}
		}

		if (!trovato || (this.receiver == null)) {
			this.log.error("GestoreRepositoryAutorizzazioni: Inizializzazione Receiver non effettuata");
			throw new Exception("GestoreRepositoryAutorizzazioni: Inizializzazione Receiver non effettuata");
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
}
