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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.resources.ExceptionListenerJMS;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.config.DatasourceProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.core.OperazioneDaSmistare;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.OperationsParameter;
import org.openspcoop2.web.ctrlstat.costanti.TipoOggettoDaSmistare;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
import org.openspcoop2.web.lib.queue.ClassQueue;
import org.openspcoop2.web.lib.queue.QueueOperation;
import org.openspcoop2.web.lib.queue.QueueParameter;
import org.openspcoop2.web.lib.queue.config.QueueProperties;
import org.openspcoop2.web.lib.queue.costanti.Operazione;
import org.openspcoop2.web.lib.queue.costanti.TipoOperazione;

/**
 * SmistatoreThread
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SmistatoreThread extends Thread {

	/** Logger utilizzato per debug. */
	private static Logger log = null;
	
	/** run */
	private boolean stop = false;
	private boolean isRunning = false;
	public boolean isRunning() {
		return this.isRunning;
	}

	private DBManager dbm;
	private Connection con;

	private ExceptionListenerJMS exceptionListenerJMS = new ExceptionListenerJMS();

	private ConsoleProperties consoleProperties;
	
	private QueueProperties queueProperties;
	
	private DatasourceProperties datasourceProperties;
	
	/** Costruttore 
	 * @throws OpenSPCoop2ConfigurationException */
	public SmistatoreThread() throws OpenSPCoop2ConfigurationException {

		// configuro il logger
		SmistatoreThread.log = ControlStationLogger.getSmistatoreLogger();

		this.dbm = DBManager.getInstance();
		
		this.consoleProperties = ConsoleProperties.getInstance();
		
		this.queueProperties = QueueProperties.getInstance();
		
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
		
		// Controllo se dbmanager inizializzato
		if (!DBManager.isInitialized()) {
			SmistatoreThread.log.info("Inizializzazione di " + this.getClass().getSimpleName() + " non riuscito perche' DBManager non INIZIALIZZATO");
			SmistatoreThread.log.info(this.getClass().getName() + " Non AVVIATO!");
			return;
		}

		String jmsConnectionFactory = null;
		Properties jmsConnectionFactoryContext = null;
		
		String smistatoreQueue = null;
		String repositoryAutorizzazioniQueue = null;
		String registroServiziQueue = null;
		String gestoreEventiQueue = null;
		String pddQueuePrefix = null;
		
		boolean enginePDD = false;
		boolean engineRegistro = false;
		boolean engineRepositoryAutorizzazioni = false;
		boolean engineGestoreEventi = false;
		
		boolean singlePdD = true;
		String tipoDatabase = null;
		
		try{
			// Leggo le informazioni da queue.properties
			jmsConnectionFactory = this.queueProperties.getConnectionFactory();
			jmsConnectionFactoryContext = this.queueProperties.getConnectionFactoryContext();
			
			// Leggo le informazioni da console.properties
			
			// nomi code
			smistatoreQueue = this.consoleProperties.getGestioneCentralizzata_NomeCodaSmistatore();
			repositoryAutorizzazioniQueue = this.consoleProperties.getGestioneCentralizzata_NomeCodaRepositoryAutorizzazioni();
			registroServiziQueue = this.consoleProperties.getGestioneCentralizzata_NomeCodaRegistroServizi();
			gestoreEventiQueue = this.consoleProperties.getGestioneCentralizzata_NomeCodaGestoreEventi();
			pddQueuePrefix = this.consoleProperties.getGestioneCentralizzata_PrefissoNomeCodaConfigurazionePdd();
			
			// Abilitazione Engine
			enginePDD = this.consoleProperties.isGestioneCentralizzata_SincronizzazionePdd();
			engineRegistro = this.consoleProperties.isGestioneCentralizzata_SincronizzazioneRegistro();
			engineRepositoryAutorizzazioni = this.consoleProperties.isGestioneCentralizzata_SincronizzazioneRepositoryAutorizzazioni();
			engineGestoreEventi = this.consoleProperties.isGestioneCentralizzata_SincronizzazioneGestoreEventi();
			
			// Altre informazioni
			singlePdD = this.consoleProperties.isSinglePdD();
			
			// Database Info
			tipoDatabase = this.datasourceProperties.getTipoDatabase();
			
		}catch(Exception e){
			SmistatoreThread.log.info("Smistatore non avviato, sono stati rilevati errori durante la lettura delle configurazione: "+e.getMessage(),e);
			return;
		}
		
		if (singlePdD) {
			SmistatoreThread.log.info("Smistatore non avviato: pddConsole avviata in singlePdD mode.");
			return;
		}

		// Configurazione JMS
		SmistatoreThread.log.debug("Smistatore: Avvio Servizio di Gestione Operazioni, Registro[" + engineRegistro + "] Pdd[" + enginePDD + "] RepositoryAutorizzazioni[" + engineRepositoryAutorizzazioni + "] GestoreEventi[" + engineGestoreEventi + "]");
		QueueReceiver receiver = null;
		Queue queue = null;
		QueueConnectionFactory qcf = null;
		QueueConnection qc = null;
		QueueSession qs = null;
		boolean trovato = false;
		int i = 0;
		SmistatoreThread.log.debug("Smistatore: Inizializzazione Receiver ...");
		while (!trovato && (i < 600000)) {
			try {
				InitialContext ctx = new InitialContext(jmsConnectionFactoryContext);
				queue = (Queue) ctx.lookup(smistatoreQueue);
				qcf = (QueueConnectionFactory) ctx.lookup(jmsConnectionFactory);
				qc = qcf.createQueueConnection();
				qc.setExceptionListener(this.exceptionListenerJMS);
				qs = qc.createQueueSession(true, -1);
				receiver = qs.createReceiver(queue);
				qc.start();
				ctx.close();
				SmistatoreThread.log.debug("Smistatore: Inizializzazione Receiver effettuata.");
				trovato = true;
			} catch (Exception e) {
				i = i + 10000;
				try {
					Thread.sleep(10000);
				} catch (Exception et) {
				}
			}
		}

		if (!trovato) {
			SmistatoreThread.log.error("Smistatore: Inizializzazione Receiver non effettuata");
			return;
		}

		// Avvio Gestione Operazioni
		boolean riconnessioneConErrore = false;
		while (this.stop == false) {

			try {

				// riconnessione precedente non riuscita.....
				if (riconnessioneConErrore) {
					throw new JMSException("RiconnessioneJMS non riuscita...");
				}
				// Controllo ExceptionListenerJMS
				if (this.exceptionListenerJMS.isConnessioneCorrotta()) {
					SmistatoreThread.log.error("ExceptionJMSListener ha rilevato una connessione jms corrotta", this.exceptionListenerJMS.getException());
					throw new JMSException("ExceptionJMSListener ha rilevato una connessione jms corrotta: " + this.exceptionListenerJMS.getException().getMessage());
				}

				SmistatoreThread.log.info("Smistatore: Ricezione operazione...");
				ObjectMessage richiesta = null;
				while (this.stop == false) {
					richiesta = (ObjectMessage) receiver.receive(CostantiControlStation.INTERVALLO_RECEIVE);
					if (richiesta != null) {
						break;
					}
				}
				if (this.stop == true) {
					break;
				}

				// Ricezione Operazione
				OperazioneDaSmistare operazione = null;
				try {
					operazione = (OperazioneDaSmistare) richiesta.getObject();
				} catch (Exception e) {
					SmistatoreThread.log.error("Smistatore: Ricevuta richiesta con tipo errato:" + e.toString());
					qs.commit();
					continue;
				}

				String idOperazione = richiesta.getStringProperty("ID");

				SmistatoreThread.log.info(CostantiControlStation.OPERATIONS_DELIMITER+"Smistatore: Ricevuta richiesta di operazione con ID: " + idOperazione);
				SmistatoreThread.log.debug("Smistatore: Dati operazione ricevuta idTab[" + operazione.getIDTable() + "] operazione[" + operazione.getOperazione() + "] pdd[" + operazione.getPdd() + "] oggetto[" + operazione.getOggetto() + "]");

				if ((operazione.getOperazione() == null) || ("".equals(operazione.getOperazione()))) {
					SmistatoreThread.log.error("Smistatore: Ricevuta richiesta con parametri scorretti.");
					qs.commit();
					continue;
				}

				if ((Operazione.change.equals(operazione.getOperazione()) == false) && (Operazione.add.equals(operazione.getOperazione()) == false) && (Operazione.del.equals(operazione.getOperazione()) == false)) {
					SmistatoreThread.log.error("Smistatore: Operazione [" + operazione.getOperazione() + "] non supportata dal gestore");
					qs.commit();
					continue;
				}

				if ((operazione.getOggetto() == null) || ("".equals(operazione.getOggetto())) || (operazione.getIDTable() < 0)) {
					SmistatoreThread.log.error("Smistatore: Ricevuta richiesta con parametri scorretti.");
					qs.commit();
					continue;
				}

				// Guardo che tipo di operazione ho in coda...
				// Se e' un'operazione per il registro, la metto nella coda
				// OperazioniGestoreRegistroServizi
				// Se e' un'operazione per la pdd, la metto nella coda del pdd
				// interessato

				this.con = this.dbm.getConnection();

				Operazione operazioneTipologia = operazione.getOperazione();
				String su = operazione.getSuperuser();
				TipoOggettoDaSmistare tipoOggettoDaSmistare = operazione.getOggetto();
				String pdd = operazione.getPdd();

				// Preparo un oggetto di tipo PetraOperation e poi chiamo
				// la insertQueue
				QueueOperation queueOperation = new QueueOperation();
				queueOperation.setTipoOperazione(TipoOperazione.webService);
				queueOperation.setOperazione(operazioneTipologia);
				queueOperation.setSuperuser(su);

				// disabilito il commit
				this.con.setAutoCommit(false);

				// OggettoClassQueue
				ClassQueue cq = null;
				try {
					cq = new ClassQueue(this.con, tipoDatabase, qs);
				} catch (Exception e) {
					SmistatoreThread.log.error("Smistatore: Inizializzazione ClassQueue non effettuata: " + e.getMessage());
					qs.rollback();
					continue;
				}

				if (tipoOggettoDaSmistare != null) {
					int idTable = operazione.getIDTable();

					String filter = "[" + operazione.getIDTable() + "]";
					filter += "[" + tipoOggettoDaSmistare.name() + "]";
					filter += "[" + operazione.getOperazione() + "]";

					queueOperation.addParametro(new QueueParameter("Oggetto", tipoOggettoDaSmistare.name()));
					queueOperation.addParametro(new QueueParameter("IDTable", "" + idTable));

					Hashtable<OperationsParameter, Vector<String>> params = operazione.getParameters();
					Enumeration<OperationsParameter> keys = params.keys();

					// Per ogni parametro presente nell'operazione da smistare
					// creo un nuovo PetraParameter con nome key.getNome() e
					// valore il valore associato nella tabella
					while (keys.hasMoreElements()) {
						OperationsParameter key = keys.nextElement();

						Vector<String> values = params.get(key);
						for (String value : values) {
							queueOperation.addParametro(new QueueParameter(key.getNome(), value));
							filter += "[" + value + "]";
						}

					}

					// Smisto l'operazione

					if (tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.soggetto) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.servizio) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.accordo) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.accordoCooperazione) || 
//							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.fruitore) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.pdd)) {
						// Operazione per il registro
						if (engineRegistro) {
							QueueOperation queueOperationRegistro = (QueueOperation) queueOperation.clone();
							queueOperationRegistro.setTipoOperazione(TipoOperazione.webService);
							if (cq.insertQueue(registroServiziQueue, queueOperationRegistro, filter) == 0) {
								SmistatoreThread.log.error("Smistatore: Si e' verificato un problema durante l'inserimento in coda.");
								qs.rollback();
								this.con.rollback();
								this.dbm.releaseConnection(this.con);
								continue;
							}
						} else {
							SmistatoreThread.log.info("Smistatore: sincronizzazione Registro Servizi non abilitata.");
						}
					}

					if ((pdd != null) && !pdd.equals("") && !pdd.equals("-") && 
							!tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.politicheSicurezza)) {
						// Operazione per il pdd
						if (enginePDD) {
							// Qualcuno avra' provveduto a creare una coda per
							// il pdd, che si chiama come il pdd stesso
							PddCore pddCore = new PddCore();
							PdDControlStation myPdd = pddCore.getPdDControlStation(pdd);
							String tipoPdd = myPdd.getTipo();

							if (PddTipologia.OPERATIVO.toString().equals(tipoPdd)) {
								QueueOperation queueOperationPdD = (QueueOperation) queueOperation.clone();
								queueOperationPdD.setTipoOperazione(TipoOperazione.webService);
								if (cq.insertQueue(pddQueuePrefix + pdd, queueOperationPdD, filter) == 0) {
									SmistatoreThread.log.error("Smistatore: Si e' verificato un problema durante l'inserimento in coda.");
									qs.rollback();
									this.con.rollback();
									this.dbm.releaseConnection(this.con);
									continue;
								}
							} else {
								SmistatoreThread.log.warn("Smistatore: Inserimento in coda non effettuato causa NAL [" + pdd + "] Tipo [" + tipoPdd + "] ");
							}
						} else {
							SmistatoreThread.log.info("Smistatore: sincronizzazione Nal non abilitata.");
						}
					}

					// Inserisco l'operazione nella coda per il RepositoryAutorizzazioni
					// inserita gestione accordo e ruolo
					if ( !repositoryAutorizzazioniQueue.equals("") && 
							(
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.soggetto) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.ruolo) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.accordoRuolo) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.servizio) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.servizioApplicativo) || 
							//(tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.fruitore) && Operazione.del.equals(operazioneTipologia) ) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.politicheSicurezza)
							)
						){
						if (engineRepositoryAutorizzazioni) {
							QueueOperation queueOperationRepositoryAutorizzazioni = (QueueOperation) queueOperation.clone();
							queueOperationRepositoryAutorizzazioni.setTipoOperazione(TipoOperazione.javaInterface);
							if (cq.insertQueue(repositoryAutorizzazioniQueue, queueOperationRepositoryAutorizzazioni, filter) == 0) {
								SmistatoreThread.log.error("Smistatore: Si e' verificato un problema durante l'inserimento in coda.");
								qs.rollback();
								this.con.rollback();
								this.dbm.releaseConnection(this.con);
								continue;
							}
						} else {
							SmistatoreThread.log.info("Smistatore: sincronizzazione RepositoryAutorizzazioni non abilitata.");
						}
					}

					// Gestore Eventi
					if ( ((gestoreEventiQueue != null) && !gestoreEventiQueue.equals("")) && 
							(
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.soggetto) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.servizio) || 
							tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.politicheSicurezza)// || 
							//tipoOggettoDaSmistare.equals(TipoOggettoDaSmistare.fruitore) 
							)
						){
						// Operazione per il registro
						if (engineGestoreEventi) {
							QueueOperation queueOperationGestoreEventi= (QueueOperation) queueOperation.clone();
							queueOperationGestoreEventi.setTipoOperazione(TipoOperazione.webService);
							if (cq.insertQueue(gestoreEventiQueue, queueOperationGestoreEventi, filter) == 0) {
								SmistatoreThread.log.error("Smistatore: Si e' verificato un problema durante l'inserimento in coda.");
								qs.rollback();
								this.con.rollback();
								this.dbm.releaseConnection(this.con);
								continue;
							}
						} else {
							SmistatoreThread.log.info("Smistatore: sincronizzazione Gestore Eventi non abilitata.");
						}
					}
				}

				this.con.commit();
				this.con.setAutoCommit(true);
				this.dbm.releaseConnection(this.con);

				qs.commit();

				SmistatoreThread.log.info("Smistatore: Operazione [" + idOperazione + "] completata.");

			} catch (JMSException e) {
				try {
					qs.rollback();
					this.con.rollback();
					this.dbm.releaseConnection(this.con);
				} catch (Exception er) {
				}
				SmistatoreThread.log.error("Smistatore: Riscontrato erroreJMS durante la gestione di una richiesta: " + e.toString());
				try {
					Thread.sleep(5000);
					SmistatoreThread.log.debug("Smistatore: Re-Inizializzazione Receiver ...");
					try {
						receiver.close();
					} catch (Exception eclose) {
					}
					try {
						qs.close();
					} catch (Exception eclose) {
					}
					try {
						qc.close();
					} catch (Exception eclose) {
					}
					qc = qcf.createQueueConnection();
					// Ripristino stato Exception Listener
					if (this.exceptionListenerJMS.isConnessioneCorrotta()) {
						this.exceptionListenerJMS.setConnessioneCorrotta(false);
						this.exceptionListenerJMS.setException(null);
					}
					qc.setExceptionListener(this.exceptionListenerJMS);
					qs = qc.createQueueSession(true, -1);
					receiver = qs.createReceiver(queue);
					qc.start();
					SmistatoreThread.log.debug("Smistatore: Re-Inizializzazione Receiver effettuata.");
					riconnessioneConErrore = false;

				} catch (Exception er) {
					SmistatoreThread.log.error("Smistatore: Re-Inizializzazione Receiver non effettuata:" + er.toString());
					riconnessioneConErrore = true;
				}
			} catch (Exception e) {
				try {
					qs.rollback();
					this.con.rollback();
					this.dbm.releaseConnection(this.con);
				} catch (Exception er) {
				}
				SmistatoreThread.log.error("Smistatore: Riscontrato errore durante la gestione di una richiesta: " + e.toString(), e);
			} finally {

				try {
					this.dbm.releaseConnection(this.con);
				} catch (Exception e) {

				}
			}
		}

		// Chiusura connessione
		try {
			if (receiver != null) {
				receiver.close();
			}
			if (qs != null) {
				qs.rollback();
				qs.close();
			}
			if (qc != null) {
				qc.stop();
				qc.close();
			}
		} catch (Exception e) {
			try {
				SmistatoreThread.log.error("Smistatore: Riscontrato errore durante la chiusura del Thread: " + e.toString());
			} catch (Exception eLogger) {
			}
		}
		
		this.isRunning = false;
		log.debug("Thread terminato");
	}

	public void stopGestore() {
		this.stop = true;
		
		SmistatoreThread.log.debug("Fermo il thread ...");
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
			SmistatoreThread.log.debug("Sono trascorsi 60 secondi ed il thread non è ancora terminato??");
		}
	}
}
