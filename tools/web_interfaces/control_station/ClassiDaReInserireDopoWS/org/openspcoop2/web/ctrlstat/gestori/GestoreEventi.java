/*
 * OpenSPCoop - Customizable API Gateway 
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
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
import org.openspcoop2.utils.log.LogUtilities;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ServizioSpcoop;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.gestoreeventi.GestoreEventiCRUDService;
import org.openspcoop2.gestoreeventi.types.EventoSPCoop;
import org.openspcoop2.gestoreeventi.types.ServizioSPCoop;
import org.openspcoop2.gestoreeventi.types.SoggettoSPCoop;
import org.openspcoop2.gestoreeventi.types.Sottoscrittore;
import org.openspcoop2.gestoreeventi.types.Sottoscrizione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCoreException;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.core.OperazioneDaSmistare.TipiOperazione;
import org.openspcoop2.web.ctrlstat.core.QueueManager;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.OperationsParameter;
import org.openspcoop2.web.ctrlstat.dao.PoliticheSicurezza;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.lib.queue.ClassQueue;
import org.openspcoop2.web.lib.queue.ClassQueueException;
import org.openspcoop2.web.lib.queue.costanti.OperationStatus;
import org.openspcoop2.web.lib.queue.dao.FilterParameter;
import org.openspcoop2.web.lib.queue.dao.Operation;
import org.openspcoop2.web.lib.queue.dao.Parameter;

/**
 * GestoreEventi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GestoreEventi extends GestoreGeneral {

	private String name = null;
	private String tipoDB = null;

	/** Sottoscrizione dinamica dell'azione */
	private static final String AZIONE_SOTTOSCRITTORE_DINAMICA = "#tipoEvento";

	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** run */
	private boolean stop = false;

	/** PddConsole Core */
	private ControlStationCore core;

	/** Nome della coda */
	private String queueName;

	// JMS
	private QueueReceiver receiver = null;
	private Queue queue = null;
	private QueueConnectionFactory qcf = null;
	private QueueConnection qc = null;
	private QueueSession qs = null;

	// DB
	// private String jndiName = "";
	// private Properties jndiProp = new Properties();
	// private String cfName = "";
	// private Properties cfProp = new Properties();
	private DBManager dbm;
	private Connection con;

	private boolean trovato = false;
	private String wsurl = "";
	private boolean engineGE = false;
	private boolean singlePdD = false;

	/** Web Service */
	private org.openspcoop2.gestoreeventi.GestoreEventiCRUDService locator = null;
	private org.openspcoop2.gestoreeventi.GestoreEventiCRUD gestore_eventi = null;

	/** Parametri evento */
	private String tipo_soggetto = null;
	private String nome_soggetto = null;
	private String nome_servizio_applicativo = null;

	private GestoreExceptionListenerJMS exceptionListenerJMS = new GestoreExceptionListenerJMS();

	public GestoreEventi(String tipoDB) {
		this.log = LogUtilities.getLogger("gestore_eventi");
		this.name = "GestoreEventi";
		this.tipoDB = tipoDB;
	}

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

	public void run() {
		this.log.debug("Gestore Eventi: Aspetto inizializzazione di JBoss...");
		try {
			this.initGestore();

			if (this.singlePdD) {
				this.log.warn("GestoreGE non avviato: pddConsole avviata in singlePdD mode.");
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

		// Avvio Gestione Eventi
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

				this.log.info("Gestore Eventi: Ricezione operazione...");
				ObjectMessage richiesta = null;
				while (this.stop == false) {
					richiesta = (ObjectMessage) this.receiver.receive();
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
					this.log.error("Gestore Eventi: Ricevuta richiesta con tipo errato:" + e.toString());
					this.qs.commit();
					continue;
				}
				String idOperazione = richiesta.getStringProperty("ID");

				int idOp = (int) Integer.parseInt(objOp.toString());
				if (idOp == 0) {
					this.log.error("Gestore Eventi: Ricevuta richiesta con parametri scorretti.");
					this.qs.commit();
					continue;
				}

				this.log.info(CostantiControlStation.OPERATIONS_DELIMITER+"Gestore Eventi: Ricevuta richiesta di operazione con ID: " + idOperazione + " id_operation [" + idOp + "]");

				// Connessione al db
				this.con = this.dbm.getConnection();

				// Prendo i dati dell'operazione
				String tipoOp = "", singleOp = "", singleSu = "";
				int deleted = 0;
				operationManager = new ClassQueue(this.con, this.core.getTipoDatabase());
				try {
					operation = operationManager.getOperation(idOp);
				} catch (Exception e) {
					this.log.error("Impossibile recuperare l'operation con id=" + idOp, e);
					this.qs.rollback();
					this.dbm.releaseConnection(this.con);
					continue;
				}
				tipoOp = operation.getTipo();
				singleOp = operation.getOperation();
				singleSu = operation.getSuperUser();
				deleted = operation.isDeleted() ? 1 : 0;

				// Operazione in gestione
				GestioneOperazione operazioneInGestione = new GestioneOperazione(this.dbm, this.con, this.qs, operation, operationManager, this.log, singleOp, "GestoreGE");

				// Oggetto in Gestione
				String oggetto = operation.getParameterValue(OperationsParameter.OGGETTO.getNome());
				if (oggetto == null || "".equals(oggetto)) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (oggetto:" + oggetto + ") non valida");
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

				// Check valori operazioni
				if ((tipoOp == null) || tipoOp.equals("") || (singleOp == null) || singleOp.equals("") || (singleSu == null) || singleSu.equals("")) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti :" + operation.toString());
					continue;
				}
				if (!oggetto.equals("soggetto") && !oggetto.equals("servizio") && !oggetto.equals("politicheSicurezza") && !oggetto.equals("fruitore")) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (oggetto:" + oggetto + ")");
					continue;
				}
				if (!tipoOp.equals("soap")) {
					operazioneInGestione.invalid("Ricevuta operazione con parametri scorretti (tipo operazione:" + tipoOp + " non supportata dal gestore)");
					continue;
				}

				/* ----- filtro per mantenimento ordine operazioni ----- */
				Vector<FilterParameter> filtroOrdine = new Vector<FilterParameter>();

				FilterParameter idTableFiltro = new FilterParameter();
				idTableFiltro.addFilterParameter(new Parameter(OperationsParameter.ID_TABLE.getNome(), idTable + ""));
				filtroOrdine.add(idTableFiltro);

				if (oggetto.equals("soggetto")) {

					// Chiave primaria per il soggetto e' tipo e nome
					String tipoSoggFiltro = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
					String nomeSoggFiltro = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());
					FilterParameter soggettoFiltro = new FilterParameter();
					soggettoFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SOGGETTO.getNome(), tipoSoggFiltro));
					soggettoFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SOGGETTO.getNome(), nomeSoggFiltro));
					filtroOrdine.add(soggettoFiltro);

					// OLD valori per tipo e nome se abbiamo una operazione di
					// change
					if (singleOp.equals("change")) {
						String oldTipoSogg = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSogg = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());
						FilterParameter soggettoOldFiltro = new FilterParameter();
						soggettoOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_TIPO_SOGGETTO.getNome(), oldTipoSogg));
						soggettoOldFiltro.addFilterParameter(new Parameter(OperationsParameter.OLD_NOME_SOGGETTO.getNome(), oldNomeSogg));
						filtroOrdine.add(soggettoOldFiltro);
					}
				} else if (oggetto.equals("servizio")) {
					// I servizi sono intesi come eventi (quindi disaccoppiati
					// dal soggetto erogatore)
					String tipoServFiltro = operation.getParameterValue(OperationsParameter.TIPO_SERVIZIO.getNome());
					String nomeServFiltro = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO.getNome());

					FilterParameter servizioFiltro = new FilterParameter();
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SERVIZIO.getNome(), tipoServFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SERVIZIO.getNome(), nomeServFiltro));
					filtroOrdine.add(servizioFiltro);
				} else if (oggetto.equals("fruitore")) {

					// I servizi sono intesi come eventi (quindi disaccoppiati
					// dal soggetto erogatore)
					String tipoServFiltro = operation.getParameterValue(OperationsParameter.TIPO_SERVIZIO.getNome());
					String nomeServFiltro = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO.getNome());

					// I fruitori sono i sottoscrittori
					String tipoFruitoreFiltro = operation.getParameterValue(OperationsParameter.TIPO_FRUITORE.getNome());
					String nomeFruitoreFiltro = operation.getParameterValue(OperationsParameter.NOME_FRUITORE.getNome());

					FilterParameter servizioFiltro = new FilterParameter();
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_SERVIZIO.getNome(), tipoServFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_SERVIZIO.getNome(), nomeServFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.TIPO_FRUITORE.getNome(), tipoFruitoreFiltro));
					servizioFiltro.addFilterParameter(new Parameter(OperationsParameter.NOME_FRUITORE.getNome(), nomeFruitoreFiltro));
					filtroOrdine.add(servizioFiltro);

				} else if (oggetto.equals("politicheSicurezza")) {

					if (singleOp.equals("del")) {
						String idFruitoreFiltro = operation.getParameterValue(OperationsParameter.PS_ID_FRUITORE.getNome());
						String idServizioFiltro = operation.getParameterValue(OperationsParameter.PS_ID_SERVIZIO.getNome());

						FilterParameter politicheSicurezzaFiltro = new FilterParameter();
						politicheSicurezzaFiltro.addFilterParameter(new Parameter(OperationsParameter.PS_ID_FRUITORE.getNome(), idFruitoreFiltro));
						politicheSicurezzaFiltro.addFilterParameter(new Parameter(OperationsParameter.PS_ID_SERVIZIO.getNome(), idServizioFiltro));
						filtroOrdine.add(politicheSicurezzaFiltro);
					}
				} else {
					operazioneInGestione.invalid("Ricevuta operazione con oggetto non conosciuto dal Gestore (oggetto:" + oggetto + ")");
					continue;
				}

				operationManager.setOperazioniPrecedentiByFilterSearch(operation, filtroOrdine.toArray(new FilterParameter[0]), false, oggetto);
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
					if (singleOp.equals("change")) {
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
					if (oggetto.equals("soggetto")) {

						String oldTipoSogg = operation.getParameterValue(OperationsParameter.OLD_TIPO_SOGGETTO.getNome());
						String oldNomeSogg = operation.getParameterValue(OperationsParameter.OLD_NOME_SOGGETTO.getNome());
						String tipoSogg = operation.getParameterValue(OperationsParameter.TIPO_SOGGETTO.getNome());
						String nomeSogg = operation.getParameterValue(OperationsParameter.NOME_SOGGETTO.getNome());

						// CREAZIONE/MODIFICA
						if (singleOp.equals("change") || singleOp.equals("add")) {

							// Ottengo nuova immagine del soggetto
							Soggetto soggetto = null;
							try {
								soggetto = this.core.getSoggettoRegistro(idTable);
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
								SoggettoSPCoop sog = new SoggettoSPCoop();
								sog.setNome(soggetto.getNome());
								sog.setTipo(soggetto.getTipo());

								// Effettuo operazione sul soggetto
								if (singleOp.equals(TipiOperazione.CREAZIONE.getNome())) {
									this.gestore_eventi.registraSoggetto(sog);
								} else if (singleOp.equals("change")) {
									SoggettoSPCoop oldSogg = new SoggettoSPCoop();
									if (oldNomeSogg != null && oldTipoSogg != null){
										oldSogg.setTipo(oldTipoSogg);
										oldSogg.setNome(oldNomeSogg);
									}else{
										oldSogg.setTipo(sog.getTipo());
										oldSogg.setNome(sog.getNome());
									}
									this.gestore_eventi.aggiornaSoggetto(oldSogg, sog);
								}

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di creazione/update soggetto: " + e.toString(), e);
								continue;
							}

						}
						// ELIMINAZIONE
						else if (singleOp.equals("del")) {
							try {

								// Effettuo operazione sul registro
								SoggettoSPCoop soggetto = new SoggettoSPCoop();
								soggetto.setNome(nomeSogg);
								soggetto.setTipo(tipoSogg);
								if (this.gestore_eventi.esisteSoggetto(soggetto))
									this.gestore_eventi.cancellaSoggetto(soggetto);

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di eliminazione soggetto: " + e.toString(), e);
								continue;
							}

						}
					}

					/*  SERVIZI/FRUITORI */
					if (oggetto.equals("servizio") || oggetto.equals("fruitore")) {
						ArrayList<String> nomiSA = null;
						ServizioSpcoop servizio = null;

						String tipoServ = operation.getParameterValue(OperationsParameter.TIPO_SERVIZIO.getNome());
						String nomeServ = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO.getNome());
						String tipoFruitore = operation.getParameterValue(OperationsParameter.TIPO_FRUITORE.getNome());
						String nomeFruitore = operation.getParameterValue(OperationsParameter.NOME_FRUITORE.getNome());

						// aggiungo i nomi dei servizi applicativi
						String saValue = operation.getParameterValue(OperationsParameter.NOME_SERVIZIO_APPLICATIVO.getNome());
						if (saValue != null) {
							String[] sa = saValue.split(" ");
							if (sa != null) {
								nomiSA = new ArrayList<String>();
								for (int i = 0; i < sa.length; i++) {
									nomiSA.add(sa[i]);
								}
							}
						}

						// CREAZIONE/MODIFICA
						if (oggetto.equals("fruitore") && singleOp.equals("del")) {
							this.log.debug("operazione di eliminazione fruitore");

							// Ottengo nuova immagine del servizio
							try {
								servizio = this.core.getServizioSpcoop(idTable);
							} catch (DriverRegistroServiziNotFound de) {
								operazioneInGestione.waitBeforeInvalid("Servizio non esistente nel database della pddConsole: " + de.toString());
								continue;
							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del servizio: " + e.getMessage(), e);
								continue;
							}

							// Propago in remoto sul registro
							try {
								this.log.debug("cerco fruitore " + this.tipo_soggetto + "/" + this.nome_soggetto + " per eliminazione");
								// se il fruitore del servizio e' quello
								// impostato nel file di configurazione
								// allora devo gestire l'evento
								// Fruitore[] fruitori =
								// servizio.getFruitoreList()

								String nome_fruitore = nomeFruitore; // fruitore.getNome();
								String tipo_fruitore = tipoFruitore; // fruitore.getTipo();

								if (this.nome_soggetto.equals(nome_fruitore) && this.tipo_soggetto.equals(tipo_fruitore)) {
									this.log.debug("trovato matching " + this.tipo_soggetto + "/" + this.nome_soggetto);
									this.log.debug("cerco servizio applicativo " + this.nome_servizio_applicativo + " per eliminazione...");
									// controllo se il fruitore ha un
									// servizio applicativo con nome
									// uguale a quello indicato nel file
									// String[] nomiSA =
									// fruitore.getServizioApplicativoList();
									if (nomiSA != null) {
										for (String nomeSA : nomiSA) {
											if (nomeSA.equals(this.nome_servizio_applicativo)) {
												this.log.debug("trovato servizio applicativo " + this.nome_servizio_applicativo);
												// trovato matching
												// tipo,nome soggetto
												// fruitore e nome
												// servizio applicativo
												// servizio
												ServizioSPCoop serv = new ServizioSPCoop();
												serv.setTipo(servizio.getTipo());
												serv.setNome(servizio.getNome());
												// evento
												EventoSPCoop evento = new EventoSPCoop();
												evento.setServizio(serv);
												evento.setDescrizione("Evento " + serv.getTipo() + serv.getNome());
												// sottoscrittore
												Sottoscrittore sottoscrittore = new Sottoscrittore();
												sottoscrittore.setId(serv.getTipo() + "/" + serv.getNome() + "@" + servizio.getTipoSoggettoErogatore() + "/" + servizio.getNomeSoggettoErogatore());
												sottoscrittore.setServizio(serv);
												sottoscrittore.setAzione(GestoreEventi.AZIONE_SOTTOSCRITTORE_DINAMICA);
												SoggettoSPCoop soggettoGE = new SoggettoSPCoop();
												soggettoGE.setNome(servizio.getNomeSoggettoErogatore());
												soggettoGE.setTipo(servizio.getTipoSoggettoErogatore());
												sottoscrittore.setSoggetto(soggettoGE);
												sottoscrittore.setTipoConsegna("CONSEGNA");
												sottoscrittore.setDescrizione("Sottoscrittore [" + servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore() + "] del Servizio [" + serv.getTipo() + serv.getNome() + "]");
												this.log.debug("Sottoscrittore [" + servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore() + "] del Servizio [" + serv.getTipo() + serv.getNome() + "]");
												// sottoscrizione
												Sottoscrizione sottoscrizione = new Sottoscrizione();
												sottoscrizione.setEvento(evento);
												sottoscrizione.setIdSottoscrittore(serv.getTipo() + "/" + serv.getNome() + "@" + servizio.getTipoSoggettoErogatore() + "/" + servizio.getNomeSoggettoErogatore());
												this.log.debug("Sottoscrizione:\n\tEvento [Evento " + serv.getTipo() + serv.getNome() + "]\n\tSottoscrittore [" + servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore() + "] del Servizio [" + serv.getTipo() + serv.getNome() + "]");

												if (this.gestore_eventi.esisteSottoscrizione(sottoscrizione)) {
													this.gestore_eventi.cancellaSottoscrizione(sottoscrizione);
													this.log.debug("Eliminata Sottoscrizione");
												}
												if (this.gestore_eventi.esisteSottoscrittore(sottoscrittore)) {
													this.gestore_eventi.cancellaSottoscrittore(sottoscrittore);
													this.log.debug("Eliminato Sottoscrittore");
												}
											}
										}
									}
								} else {
									this.log.debug("Il fruitore " + tipo_fruitore + "/" + nome_fruitore + " non matcha il fruitore cercato.");
								}

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di creazione/update servizio: " + e.toString(), e);
								continue;
							}

						}// ELIMINAZIONE servizio
						else if (oggetto.equals("servizio") && singleOp.equals("del")) {
							this.log.debug("operazione di eliminazione servizio");
							try {
								boolean esiste = false;
								// se non esistono altri servizi con tipo e nome
								// del servizi cancellato
								esiste = this.core.existsServizioSpcoopWithTipoAndNome(tipoServ, nomeServ);
								if (!esiste) {
									this.log.debug("Non esistono altri servizi con tipo e nome " + tipoServ + "/" + nomeServ + " procedo con eliminazione");
									// servizio
									ServizioSPCoop serv = new ServizioSPCoop();
									serv.setTipo(tipoServ);
									serv.setNome(nomeServ);
									// evento
									EventoSPCoop evento = new EventoSPCoop();
									evento.setServizio(serv);
									evento.setDescrizione("Evento " + serv.getTipo() + serv.getNome());

									// cancello evento
									if (this.gestore_eventi.esisteEvento(evento)) {
										this.gestore_eventi.cancellaEvento(evento);
										this.log.debug("Eliminato Evento");
									}

									if (this.gestore_eventi.esisteServizio(serv)) {
										this.gestore_eventi.cancellaServizio(serv);
										this.log.debug("Eliminata Servizio");
									}
								}

							} catch (Exception e) {
								operazioneInGestione.error("Riscontrato errore di eliminazione servizio: " + e.toString(), e);
								continue;
							}
						} else {
							this.log.info("Niente da propagare al Servizio di Gestione Eventi.");
						}
					}

					// * ***** POLITICHE DI SICUREZZA ***** */
					if (oggetto.equals("politicheSicurezza")) {

						// questo e' un caso particolare di operazione
						// 'fruitore'
						// in quanto viene utilizzata solo quando viene inserita
						// o cancellata
						// un solo servizio applicativo (politicaSicurezza)

						// delete
						// la delete qui nn viene gestita come caso particolare

						try {
							DriverControlStationDB driver = new DriverControlStationDB(this.con, null, this.tipoDB);

							// ServizioApplicativo sa = null;
							ServizioSpcoop servizio = null;
							Soggetto soggFruitore = null;
							if (singleOp.equals(TipiOperazione.CREAZIONE.getNome())) {
								this.log.debug("Operazione di creazione su politica di sicurezza recupero dati...");
								PoliticheSicurezza ps = driver.getPoliticheSicurezza(idTable);
								// recupero i dati del servizio applicativo
								// sa =
								// driver.getDriverConfigurazioneDB().getServizioApplicativo(ps.getIdServizioApplicativo());
								try {
									servizio = driver.getDriverRegistroServiziDB().getServizioSpcoop(ps.getIdServizio());
								} catch (DriverRegistroServiziNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Servizio non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del servizio: " + e.getMessage(), e);
									continue;
								}
								try {
									soggFruitore = driver.getDriverRegistroServiziDB().getSoggettoRegistro(ps.getIdFruitore());
								} catch (DriverRegistroServiziNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Fruitore non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del soggetto(fruitore): " + e.getMessage(), e);
									continue;
								}
								this.log.debug("dati recuperati.");
							} else if (singleOp.equals(TipiOperazione.ELIMINAZIONE.getNome())) {

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

								this.log.debug("Operazione di eliminazione su politica di sicurezza recupero dati...");
								// sa =
								// driver.getDriverConfigurazioneDB().getServizioApplicativo(idSA);
								try {
									servizio = driver.getDriverRegistroServiziDB().getServizioSpcoop(idServizio);
								} catch (DriverRegistroServiziNotFound de) {
									operazioneInGestione.waitBeforeInvalid("Servizio non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del servizio: " + e.getMessage(), e);
									continue;
								}
								// recupero il fruitore che mi interessa (e
								// solamente quello)
								try {
									soggFruitore = driver.getDriverRegistroServiziDB().getSoggettoRegistro(idFruitore);
								} catch (DriverRegistroServiziNotFound de) {
									operazioneInGestione.invalid("Fruitore non esistente nel database della pddConsole: " + de.toString());
									continue;
								} catch (Exception e) {
									operazioneInGestione.error("Riscontrato errore durante la get dell'immagine del soggetto(fruitore): " + e.getMessage(), e);
									continue;
								}
								this.log.debug("dati recuperati.");
							}

							// servizio
							this.log.debug("Inizializzo oggetti per propagazione:");
							ServizioSPCoop serv = new ServizioSPCoop();
							serv.setTipo(servizio.getTipo());
							serv.setNome(servizio.getNome());
							this.log.debug("Servizio : " + serv.getTipo() + "/" + serv.getNome());
							// evento
							EventoSPCoop evento = new EventoSPCoop();
							evento.setServizio(serv);
							evento.setDescrizione("Evento " + serv.getTipo() + serv.getNome());
							this.log.debug("Evento " + serv.getTipo() + serv.getNome());
							// sottoscrittore
							Sottoscrittore sottoscrittore = new Sottoscrittore();
							sottoscrittore.setServizio(serv);
							sottoscrittore.setAzione(GestoreEventi.AZIONE_SOTTOSCRITTORE_DINAMICA);
							SoggettoSPCoop soggettoGE = new SoggettoSPCoop();
							soggettoGE.setNome(servizio.getNomeSoggettoErogatore());
							soggettoGE.setTipo(servizio.getTipoSoggettoErogatore());
							sottoscrittore.setSoggetto(soggettoGE);
							sottoscrittore.setTipoConsegna("CONSEGNA");
							sottoscrittore.setDescrizione("Sottoscrittore [" + servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore() + "] del Servizio [" + serv.getTipo() + serv.getNome() + "]");
							sottoscrittore.setId(serv.getTipo() + "/" + serv.getNome() + "@" + servizio.getTipoSoggettoErogatore() + "/" + servizio.getNomeSoggettoErogatore());
							this.log.debug("Sottoscrittore [" + servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore() + "] del Servizio [" + serv.getTipo() + serv.getNome() + "]");
							// sottoscrizione
							Sottoscrizione sottoscrizione = new Sottoscrizione();
							sottoscrizione.setEvento(evento);
							sottoscrizione.setIdSottoscrittore(serv.getTipo() + "/" + serv.getNome() + "@" + servizio.getTipoSoggettoErogatore() + "/" + servizio.getNomeSoggettoErogatore());
							this.log.debug("Sottoscrizione:\n\tEvento [Evento " + serv.getTipo() + serv.getNome() + "]\n\tSottoscrittore [" + servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore() + "] del Servizio [" + serv.getTipo() + serv.getNome() + "]");

							Fruitore fruitore = new Fruitore();

							fruitore.setNome(soggFruitore.getNome());
							fruitore.setTipo(soggFruitore.getTipo());

							// Se il fruitore e il servizio applicativo sono
							// quelli impostati nel file di configurazioni
							// per cui devo gestire quest'evento, allora
							// gestisco l'evento
							if (this.tipo_soggetto.equals(fruitore.getTipo()) && this.nome_soggetto.equals(fruitore.getNome())) {
								this.log.debug("trovato match tipo/nome fruitore : " + this.tipo_soggetto + "/" + this.nome_soggetto);
								// create
								if (singleOp.equals("add")) {
									this.log.debug("Operazione ADD");

									if (!this.gestore_eventi.esisteServizio(serv)) {
										this.gestore_eventi.registraServizio(serv);
										this.log.debug("Registrato Servizio");
									}

									if (!this.gestore_eventi.esisteEvento(evento)) {
										this.gestore_eventi.registraEvento(evento);
										this.log.debug("Registrato Evento");
									}

									if (!this.gestore_eventi.esisteSottoscrittore(sottoscrittore)) {
										this.gestore_eventi.registraSottoscrittore(sottoscrittore);
										this.log.debug("Registrato Sottoscrittore");
									}

									if (!this.gestore_eventi.esisteSottoscrizione(sottoscrizione)) {
										this.gestore_eventi.registraSottoscrizione(sottoscrizione);
										this.log.debug("Registrato Sottoscrizione");
									}

								}
								// update non available
								else if (singleOp.equals("del")) {
									this.log.debug("Operazione DEL");

									if (this.gestore_eventi.esisteSottoscrizione(sottoscrizione)) {
										this.gestore_eventi.cancellaSottoscrizione(sottoscrizione);
										this.log.debug("Eliminata Sottoscrizione");
									}

									if (this.gestore_eventi.esisteSottoscrittore(sottoscrittore)) {
										this.gestore_eventi.cancellaSottoscrittore(sottoscrittore);
										this.log.debug("Eliminat Sottoscrittore");
									}

								}

								this.log.info("Gestore Eventi : Operazione  [" + singleOp + "] Completata");

							}

						} catch (Exception e) {
							operazioneInGestione.error("Riscontrato errore di creazione/update/delete politicheSicurezza: " + e.toString(), e);
							continue;
						}
					}

					// Se arrivo qui, significa che l'operazione non ha dato
					// errore,
					// aggiorno il db
					operazioneInGestione.success("Done.");
				}

				this.log.info("Gestore Eventi: Operazione [" + idOperazione + "] completata"+statoOperazioneCancellata);

			} catch (javax.jms.JMSException e) {
				try {
					this.qs.rollback();
				} catch (Exception er) {
				}
				this.log.error("Gestore Eventi: Riscontrato errore durante la gestione di una richiesta: " + e.toString(), e);
				try {
					Thread.sleep(5000);
					this.log.debug("Gestore Eventi: Re-Inizializzazione Receiver ...");
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
					this.log.debug("Gestore Eventi: Re-Inizializzazione Receiver effettuata.");
					riconnessioneConErrore = false;
				} catch (Exception er) {
					this.log.error("Gestore Eventi: Re-Inizializzazione Receiver non effettuata:" + er.toString());
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
				this.log.error("Gestore Eventi: Riscontrato errore durante la gestione di una richiesta: " + e.toString(), e);
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
			this.log.error("Riscontrato errore durante la chiusura del Gestore Eventi: " + e.toString());
		}
	}

	public void initGestore() throws Exception {

		// Controllo se dbmanager inizializzato
		// Il DBManager viene inizializzato nell'InitListener
		if (!DBManager.isInitialized()) {
			this.log.info("Inizializzazione di " + this.getClass().getSimpleName() + " non riuscito perche' DBManager non INIZIALIZZATO");
			throw new Exception("Inizializzazione di " + this.getClass().getSimpleName() + "FALLITA");
		}

		// Leggo infoGeneral
		Properties prop = GestoreGeneral.readProperties("/infoGeneral.cfg");
		Enumeration<?> en = prop.propertyNames();
		while (en.hasMoreElements()) {
			String property = (String) en.nextElement();
			this.log.info("Property from infoGeneral per url WebService: " + property);
			if (property.equals("UrlWebServiceGestoreEventi")) {
				String value = prop.getProperty(property);
				if (value != null) {
					value = value.trim();
					this.wsurl = value;
				}
			}
			if (property.equals("GestoreEventiQueue")) {
				String value = prop.getProperty(property);
				if (value != null) {
					value = value.trim();
					this.queueName = value;
				}
			}
			// Engine activation
			if (property.equals("sincronizzazioneGE")) {
				String value = prop.getProperty(property);
				if (value != null) {
					value = value.trim();
					try {
						this.engineGE = Boolean.parseBoolean(value);
					} catch (Exception e) {
						this.log.error("Errore durante la lettura della proprieta': sincronizzazioneGE");
					}
				}
			}

			// Parametri evento
			if (property.equals("tipo_soggetto")) {
				String value = prop.getProperty(property);
				if (value != null) {
					value = value.trim();
					this.tipo_soggetto = value;
				}
			}

			if (property.equals("nome_soggetto")) {
				String value = prop.getProperty(property);
				if (value != null) {
					value = value.trim();
					this.nome_soggetto = value;
				}
			}

			if (property.equals("nome_servizio_applicativo")) {
				String value = prop.getProperty(property);
				if (value != null) {
					value = value.trim();
					this.nome_servizio_applicativo = value;
				}
			}

			if (property.equals("singlePdD")) {
				String value = prop.getProperty(property);
				if (value != null) {
					value = value.trim();
					try {
						this.singlePdD = Boolean.parseBoolean(value);
					} catch (Exception e) {
						throw new Exception("Opzione singlePdD possiede un valore non corretto (" + value + "): " + e.getMessage());
					}
				}
			}
		}

		if (this.engineGE == false) {
			//this.log.info("Motore di sincronizzazione verso il Gestore Eventi non attivo.");
			throw new GestoreNonAttivoException("Motore di sincronizzazione verso il Gestore Eventi non attivo.");
		}

		// Leggo informazioni per queue.properties
		// Init JMS
		// readQueueProperties(cfName, cfProp);
		QueueManager queueMan = QueueManager.getInstance();
		if (queueMan == null) {
			this.log.debug("Impossibile avviare " + this.getClass().getSimpleName() + "QueueManager non inizializzato.");
			throw new Exception("Impossibile avviare " + this.getClass().getSimpleName() + "QueueManager non inizializzato.");
		}

		this.trovato = false;
		int i = 0;
		this.log.debug("Inizializzazione Receiver ...");
		while (!this.trovato && (i < 600000)) {
			try {
				this.queue = queueMan.getQueue(this.queueName);
				this.qcf = queueMan.getQueueConnectionFactory();
				this.qc = this.qcf.createQueueConnection();
				this.qc.setExceptionListener(this.exceptionListenerJMS);
				this.qs = this.qc.createQueueSession(true, -1);
				this.receiver = this.qs.createReceiver(this.queue);
				this.qc.start();
				this.log.debug("GestoreEventi: Inizializzazione Receiver effettuata.");
				this.trovato = true;
			} catch (Exception e) {
				i = i + 10000;
				try {
					Thread.sleep(10000);
					this.log.debug("Ritento Inizializzazione Receiver ... causa: " + e.getMessage());
				} catch (Exception et) {
				}
			}
		}

		if (!this.trovato) {
			this.log.error("Inizializzazione Receiver non effettuata");
			throw new Exception("Inizializzazione Receiver non effettuata");
		}

		// Init WebService
		try {
			// ws
			this.locator = new GestoreEventiCRUDService();
			this.gestore_eventi = this.locator.getGestoreEventiCRUDPort();
			((BindingProvider) this.gestore_eventi).getRequestContext().put(
			        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
			        this.wsurl);
			

		} catch (Exception e) {
			this.log.error("Riscontrato Errore durante la connessione al WebService.", e);
			this.log.info("GestoreEventi non avviato.");
			throw new Exception("Riscontrato Errore durante la connessione al WebService.", e);
		}

		// Init ControlStationCore
		try {
			this.core = new ControlStationCore();

		} catch (Exception e) {
			this.log.error("Riscontrato Errore durante l'inizializzazione di ControlStationCore.", e);
			this.log.info("GestoreEventi non avviato.");
			throw new ControlStationCoreException("Riscontrato Errore durante l'inizializzazione di ControlStationCore.", e);
		}

		this.log.debug("GestoreEventi: Inizializzato WebService. " + this.gestore_eventi.getClass().getSimpleName());
		this.log.debug("GestoreEventi: Inizializzato ControlStationCore. " + this.core.getClass().getSimpleName());
		this.log.debug("GestoreEventi: Parametri evento tipo_soggetto[" + this.tipo_soggetto + "] nome_soggetto[" + this.nome_soggetto + "] nome_servizio_applicativo[" + this.nome_servizio_applicativo + "]");

	}

	@Override
	protected String getName() {
		return this.name;
	}
}
