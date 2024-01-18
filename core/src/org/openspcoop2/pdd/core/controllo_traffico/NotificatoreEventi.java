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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import jakarta.activation.FileDataSource;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.constants.CodiceEventoControlloTraffico;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.eventi.GestoreEventi;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.WriteToSerializerType;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;

/**     
 * NotificatoreEventi
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotificatoreEventi {

	private static NotificatoreEventi staticInstance = null;
	private static synchronized void initialize() throws Exception{
		if(staticInstance==null){
			staticInstance = new NotificatoreEventi();
		}
	}
	public static NotificatoreEventi getInstance() throws Exception{
		if(staticInstance==null){
			initialize();
		}
		return staticInstance;
	}

	private GestoreEventi gestoreEventi;
		
	public NotificatoreEventi() throws Exception{
		this.gestoreEventi = GestoreEventi.getInstance();
	}
	
	/**
     * In memoria vengono collezionate (viene salvata la data):
     * Eventi causati durante la gestione di un thread (denominati TH_EVENTO)
     * - TH_lastMaxRequests. ultimo scarto di una richiesta per raggiungimento MaxThreads
     * - TH_lastPddCongestionata. ultimo evento di rilevamento congestione della PdD
     * - TH_lastPolicyViolated. ultimo scarto per violazione di una policy attiva
     * - TH_lastPolicyViolated_warningOnly. ultimo di scarto per violazione di una policy attiva configurata in warning only
     * 
     * Inoltre in memoria vengono collezionati gli ultimi eventi emessi realmente emessi sul database degli eventi
     * (viene salvato la tipologia dell'evento cioè se l'evento occorre 'INGRESSO', ad es. violazione,  
     * o se l'evento termina 'USCITA', ad es. violazione-risolta) 
     * per i quattro tipi sopra indicati (denominati con DB_EVENTO):
     * - DB_lastMaxRequests. ultimo evento emesso su db per raggiungimento (o ritorno nei livelli soglia) da MaxThreads
     * - DB_lastPddCongestionata.  ultimo evento di rilevamento congestione (fine congestione) della PdD
     * - DB_lastPolicyViolated. ultimo evento emesso per scarto per violazione (o ritorno nei livelli soglia) di una policy attiva
     * - DB_lastPolicyViolated_warningOnly. ultimo evento emesso per scarto per violazione (o ritorno nei livelli soglia) di una policy attiva in warning only
     * 
     * Esistera un timer che gira ogni x minuti.
     * Per ogni TH_<tipo> effettua il seguente algoritmo (dove tipo e' uno dei quattro descritti in precedenza).
     * if( esiste TH_<tipo> ultimo_intervallo_x_minuti ){
     * 		if( esiste DB_<tipo> ){
     * 			tipologia = DB_<tipo>.tipologia
     * 			if ( tipologia == "USCITA" ){
     * 				// emetto evento di ingresso, ho una variazione di stato
     * 				DB_<tipo> = emettoEventoSuDatabase(<tipo>,"INGRESSO")
     * 			}
     * 		}
     * 		else{
     * 			// primo evento
     * 			DB_<tipo> = emettoEventoSuDatabase(<tipo>,"INGRESSO")
     * 		}
     * }
     * else{
     * 		if( esiste DB_<tipo> ){
     * 			tipologia = DB_<tipo>.tipologia
     * 			if ( tipologia == "INGRESSO" ){
     * 				// emetto evento di uscita, ho una variazione di stato
     * 				DB_<tipo> = emettoEventoSuDatabase(<tipo>,"USCITA")
     * 			}
     * 		}	
     * }
     * 
     * NOTA: Non ci interessa la visione di dettaglio. E' chiaro che se rilevo un evento di scarto nell'intervallo di 5 minuti
     *       poi puo' darsi che nell'ultimo minuto il problema sia rientrato. Pero' io vorrei segnalare che c'e' stato il problema
     *       Poi nel prossimo controllo, indichero' che il problema e' rientrato, pero' almeno so' che nella finestra temporale dei 5 minuti e' accaduto un errore.
     *       
     * NOTA: L'analisi di dettaglio puo' poi essere effettuata sulle transazioni ricercando le transazioni che contengono quell'evento.
     */
    
	//private Boolean semaphore = true; // per gestire l'accesso alle risorse dal thread
	private static final org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("NotificatoreEventi");
	
	// TH_EVENTO
	private DatiEventoGenerico inMemoryLastMaxRequests = new DatiEventoGenerico();
	private DatiEventoGenerico inMemoryLastMaxRequestsWarningOnly = new DatiEventoGenerico();
	private DatiEventoGenerico inMemoryLastPddCongestionata = new DatiEventoGenerico();
	private Map<String, DatiEventoGenerico> inMemoryLastPolicyGlobaliViolated = new HashMap<>();
	private Map<String, DatiEventoGenerico> inMemoryLastPolicyGlobaliViolatedWarningOnly = new HashMap<>();
	private Map<String, DatiEventoGenerico> inMemoryLastPolicyAPIViolated = new HashMap<>();
	private Map<String, DatiEventoGenerico> inMemoryLastPolicyAPIViolatedWarningOnly = new HashMap<>();
	private Map<String, DatiEventoGenerico> inMemoryLastTimeoutConnessione = new HashMap<>();
	private Map<String, DatiEventoGenerico> inMemoryLastTimeoutRichiesta = new HashMap<>();
	private Map<String, DatiEventoGenerico> inMemoryLastTimeoutRisposta = new HashMap<>();
		
	// DB_EVENTO
	private DatabaseDatiEventoGenerico dbLastMaxRequests = new DatabaseDatiEventoGenerico();
	private DatabaseDatiEventoGenerico dbLastMaxRequestsWarningOnly = new DatabaseDatiEventoGenerico();
	private DatabaseDatiEventoGenerico dbLastPddCongestionata = new DatabaseDatiEventoGenerico();
	private Map<String, DatabaseDatiEventoGenerico> dbLastPolicyGlobaliViolated = new HashMap<>();
	private Map<String, DatabaseDatiEventoGenerico> dbLastPolicyGlobaliViolatedWarningOnly = new HashMap<>();
	private Map<String, DatabaseDatiEventoGenerico> dbLastPolicyAPIViolated = new HashMap<>();
	private Map<String, DatabaseDatiEventoGenerico> dbLastPolicyAPIViolatedWarningOnly = new HashMap<>();
	private Map<String, DatabaseDatiEventoGenerico> dbLastTimeoutConnessione = new HashMap<>();
	private Map<String, DatabaseDatiEventoGenerico> dbLastTimeoutRichiesta = new HashMap<>();
	private Map<String, DatabaseDatiEventoGenerico> dbLastTimeoutRisposta = new HashMap<>();
	
	private static final String ID_POLICY_NON_FORNITA = "IdPolicy non fornita";
	
	public void log(CategoriaEventoControlloTraffico evento, Date date, String descrizione) throws CoreException,UtilsException{
		log(evento, null, null, date, descrizione);
	}
	public void log(CategoriaEventoControlloTraffico evento, String idPolicy, String configurazione, Date date, String descrizione) throws CoreException,UtilsException{
		
		if(evento==null){
			throw new CoreException("Evento non definito");
		}
		
		switch (evento) {
		
		case LIMITE_GLOBALE_RICHIESTE_SIMULTANEE:
			lock.acquire("log_"+evento.name());
			try {
				if(this.inMemoryLastMaxRequests.datiConsumatiThread){
					if(date==null)
						this.inMemoryLastMaxRequests.data = DateManager.getDate();
					else
						this.inMemoryLastMaxRequests.data = date;
					this.inMemoryLastMaxRequests.descrizione = descrizione;
					this.inMemoryLastMaxRequests.configurazione = configurazione;
					/**System.out.println("@@LOG AGGIORNO DATA LIMITE_RICHIESTE_SIMULTANEE_VIOLAZIONE ["+
						newSimpleDateFormat().format(this.inMemoryLastMaxRequests.data)+"]");*/
					this.inMemoryLastMaxRequests.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case LIMITE_GLOBALE_RICHIESTE_SIMULTANEE_WARNING_ONLY:
			lock.acquire("log_"+evento.name());
			try {
				if(this.inMemoryLastMaxRequestsWarningOnly.datiConsumatiThread){
					if(date==null)
						this.inMemoryLastMaxRequestsWarningOnly.data = DateManager.getDate();
					else
						this.inMemoryLastMaxRequestsWarningOnly.data = date;
					this.inMemoryLastMaxRequestsWarningOnly.descrizione = descrizione;
					this.inMemoryLastMaxRequestsWarningOnly.configurazione = configurazione;
					/**System.out.println("@@LOG AGGIORNO DATA LIMITE_RICHIESTE_SIMULTANEE_VIOLAZIONE WARNING_ONLY ["+
						newSimpleDateFormat().format(this.inMemoryLastMaxRequestsWarningOnly.data)+"]");*/
					this.inMemoryLastMaxRequestsWarningOnly.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case CONGESTIONE_PORTA_DOMINIO:
			lock.acquire("log_"+evento.name());
			try {
				if(this.inMemoryLastPddCongestionata.datiConsumatiThread){
					if(date==null)
						this.inMemoryLastPddCongestionata.data = DateManager.getDate();
					else
						this.inMemoryLastPddCongestionata.data = date;
					this.inMemoryLastPddCongestionata.descrizione = descrizione;
					this.inMemoryLastPddCongestionata.configurazione = configurazione;
					/**System.out.println("@@LOG AGGIORNO DATA CONGESTIONE PDD ["+
						newSimpleDateFormat().format(this.inMemoryLastPddCongestionata.data)+"]");*/
					this.inMemoryLastPddCongestionata.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case POLICY_GLOBALE:
			if(idPolicy==null){
				throw new CoreException(ID_POLICY_NON_FORNITA);
			}
			lock.acquire("log_"+evento.name());
			try {
				if(!this.inMemoryLastPolicyGlobaliViolated.containsKey(idPolicy)){
					this.inMemoryLastPolicyGlobaliViolated.put(idPolicy, new DatiEventoGenerico());
				}
				if(!this.dbLastPolicyGlobaliViolated.containsKey(idPolicy)){
					this.dbLastPolicyGlobaliViolated.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemoryLastPolicyGlobaleViolatedPolicy = this.inMemoryLastPolicyGlobaliViolated.get(idPolicy);
				
				if(inMemoryLastPolicyGlobaleViolatedPolicy.datiConsumatiThread){
					if(date==null)
						inMemoryLastPolicyGlobaleViolatedPolicy.data = DateManager.getDate();
					else
						inMemoryLastPolicyGlobaleViolatedPolicy.data = date;
					inMemoryLastPolicyGlobaleViolatedPolicy.descrizione = descrizione;
					inMemoryLastPolicyGlobaleViolatedPolicy.configurazione = configurazione;
					/**System.out.println("@@LOG AGGIORNO DATA POLICY VIOLAZIONE ["+
							newSimpleDateFormat().format(inMemoryLastPolicyGlobaleViolatedPolicy.data)+"]");*/
					inMemoryLastPolicyGlobaleViolatedPolicy.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case POLICY_GLOBALE_WARNING_ONLY:
			if(idPolicy==null){
				throw new CoreException(ID_POLICY_NON_FORNITA);
			}
			lock.acquire("log_"+evento.name());
			try {
				if(!this.inMemoryLastPolicyGlobaliViolatedWarningOnly.containsKey(idPolicy)){
					this.inMemoryLastPolicyGlobaliViolatedWarningOnly.put(idPolicy, new DatiEventoGenerico());
				}
				if(!this.dbLastPolicyGlobaliViolatedWarningOnly.containsKey(idPolicy)){
					this.dbLastPolicyGlobaliViolatedWarningOnly.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemoryLastPolicyGlobaleViolatedWarningOnlyPolicy = this.inMemoryLastPolicyGlobaliViolatedWarningOnly.get(idPolicy);
				
				if(inMemoryLastPolicyGlobaleViolatedWarningOnlyPolicy.datiConsumatiThread){
					if(date==null)
						inMemoryLastPolicyGlobaleViolatedWarningOnlyPolicy.data = DateManager.getDate();
					else
						inMemoryLastPolicyGlobaleViolatedWarningOnlyPolicy.data = date;
					inMemoryLastPolicyGlobaleViolatedWarningOnlyPolicy.descrizione = descrizione;
					inMemoryLastPolicyGlobaleViolatedWarningOnlyPolicy.configurazione = configurazione;
					/**System.out.println("@@LOG AGGIORNO DATA POLICY VIOLAZIONE WARNING_ONLY ["+
							newSimpleDateFormat().format(inMemoryLastPolicyGlobaleViolatedWarningOnlyPolicy.data)+"]");*/
					inMemoryLastPolicyGlobaleViolatedWarningOnlyPolicy.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
			
		case POLICY_API:
			if(idPolicy==null){
				throw new CoreException(ID_POLICY_NON_FORNITA);
			}
			lock.acquire("log_"+evento.name());
			try {
				if(!this.inMemoryLastPolicyAPIViolated.containsKey(idPolicy)){
					this.inMemoryLastPolicyAPIViolated.put(idPolicy, new DatiEventoGenerico());
				}
				if(!this.dbLastPolicyAPIViolated.containsKey(idPolicy)){
					this.dbLastPolicyAPIViolated.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemoryLastPolicyAPIViolatedPolicy = this.inMemoryLastPolicyAPIViolated.get(idPolicy);
				
				if(inMemoryLastPolicyAPIViolatedPolicy.datiConsumatiThread){
					if(date==null)
						inMemoryLastPolicyAPIViolatedPolicy.data = DateManager.getDate();
					else
						inMemoryLastPolicyAPIViolatedPolicy.data = date;
					inMemoryLastPolicyAPIViolatedPolicy.descrizione = descrizione;
					inMemoryLastPolicyAPIViolatedPolicy.configurazione = configurazione;
					/**System.out.println("@@LOG AGGIORNO DATA POLICY VIOLAZIONE ["+
							newSimpleDateFormat().format(inMemoryLastPolicyAPIViolatedPolicy.data)+"]");*/
					inMemoryLastPolicyAPIViolatedPolicy.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case POLICY_API_WARNING_ONLY:
			if(idPolicy==null){
				throw new CoreException(ID_POLICY_NON_FORNITA);
			}
			lock.acquire("log_"+evento.name());
			try {
				if(!this.inMemoryLastPolicyAPIViolatedWarningOnly.containsKey(idPolicy)){
					this.inMemoryLastPolicyAPIViolatedWarningOnly.put(idPolicy, new DatiEventoGenerico());
				}
				if(!this.dbLastPolicyAPIViolatedWarningOnly.containsKey(idPolicy)){
					this.dbLastPolicyAPIViolatedWarningOnly.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemoryLastPolicyAPIViolatedWarningOnlyPolicy = this.inMemoryLastPolicyAPIViolatedWarningOnly.get(idPolicy);
				
				if(inMemoryLastPolicyAPIViolatedWarningOnlyPolicy.datiConsumatiThread){
					if(date==null)
						inMemoryLastPolicyAPIViolatedWarningOnlyPolicy.data = DateManager.getDate();
					else
						inMemoryLastPolicyAPIViolatedWarningOnlyPolicy.data = date;
					inMemoryLastPolicyAPIViolatedWarningOnlyPolicy.descrizione = descrizione;
					inMemoryLastPolicyAPIViolatedWarningOnlyPolicy.configurazione = configurazione;
					/**System.out.println("@@LOG AGGIORNO DATA POLICY VIOLAZIONE WARNING_ONLY ["+
							newSimpleDateFormat().format(inMemoryLastPolicyAPIViolatedWarningOnlyPolicy.data)+"]");*/
					inMemoryLastPolicyAPIViolatedWarningOnlyPolicy.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case TIMEOUT_CONNESSIONE:
			if(idPolicy==null){
				throw new CoreException(ID_POLICY_NON_FORNITA);
			}
			lock.acquire("log_"+evento.name());
			try {
				if(!this.inMemoryLastTimeoutConnessione.containsKey(idPolicy)){
					this.inMemoryLastTimeoutConnessione.put(idPolicy, new DatiEventoGenerico());
				}
				if(!this.dbLastTimeoutConnessione.containsKey(idPolicy)){
					this.dbLastTimeoutConnessione.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemoryLastTimeoutConnessioneRead = this.inMemoryLastTimeoutConnessione.get(idPolicy);
				
				if(inMemoryLastTimeoutConnessioneRead.datiConsumatiThread){
					if(date==null)
						inMemoryLastTimeoutConnessioneRead.data = DateManager.getDate();
					else
						inMemoryLastTimeoutConnessioneRead.data = date;
					inMemoryLastTimeoutConnessioneRead.descrizione = descrizione;
					inMemoryLastTimeoutConnessioneRead.configurazione = configurazione;
					/**System.out.println("@@LOG AGGIORNO DATA TIMEOUT CONNESSIONE VIOLAZIONE ["+
							newSimpleDateFormat().format(inMemoryLastTimeoutConnessioneRead.data)+"]");*/
					inMemoryLastTimeoutConnessioneRead.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case TIMEOUT_RICHIESTA:
			if(idPolicy==null){
				throw new CoreException(ID_POLICY_NON_FORNITA);
			}
			lock.acquire("log_"+evento.name());
			try {
				if(!this.inMemoryLastTimeoutRichiesta.containsKey(idPolicy)){
					this.inMemoryLastTimeoutRichiesta.put(idPolicy, new DatiEventoGenerico());
				}
				if(!this.dbLastTimeoutRichiesta.containsKey(idPolicy)){
					this.dbLastTimeoutRichiesta.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemoryLastTimeoutRichiestaRead = this.inMemoryLastTimeoutRichiesta.get(idPolicy);
				
				if(inMemoryLastTimeoutRichiestaRead.datiConsumatiThread){
					if(date==null)
						inMemoryLastTimeoutRichiestaRead.data = DateManager.getDate();
					else
						inMemoryLastTimeoutRichiestaRead.data = date;
					inMemoryLastTimeoutRichiestaRead.descrizione = descrizione;
					inMemoryLastTimeoutRichiestaRead.configurazione = configurazione;
					/**System.out.println("@@LOG AGGIORNO DATA TIMEOUT RICHIESTA VIOLAZIONE ["+
							newSimpleDateFormat().format(inMemoryLastTimeoutRichiestaRead.data)+"]");*/
					inMemoryLastTimeoutRichiestaRead.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case TIMEOUT_RISPOSTA:
			if(idPolicy==null){
				throw new CoreException(ID_POLICY_NON_FORNITA);
			}
			lock.acquire("log_"+evento.name());
			try {
				if(!this.inMemoryLastTimeoutRisposta.containsKey(idPolicy)){
					this.inMemoryLastTimeoutRisposta.put(idPolicy, new DatiEventoGenerico());
				}
				if(!this.dbLastTimeoutRisposta.containsKey(idPolicy)){
					this.dbLastTimeoutRisposta.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemoryLastTimeoutRispostaRead = this.inMemoryLastTimeoutRisposta.get(idPolicy);
				
				if(inMemoryLastTimeoutRispostaRead.datiConsumatiThread){
					if(date==null)
						inMemoryLastTimeoutRispostaRead.data = DateManager.getDate();
					else
						inMemoryLastTimeoutRispostaRead.data = date;
					inMemoryLastTimeoutRispostaRead.descrizione = descrizione;
					inMemoryLastTimeoutRispostaRead.configurazione = configurazione;
					/**System.out.println("@@LOG AGGIORNO DATA TIMEOUT RISPOSTA VIOLAZIONE ["+
							newSimpleDateFormat().format(inMemoryLastTimeoutRispostaRead.data)+"]");*/
					inMemoryLastTimeoutRispostaRead.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			


		default:
			throw new CoreException("Tipo di evento ["+evento.name()+"] non gestito con questo metodo");
		}
	}
	
	
	private static void logInfo(Logger log,boolean debug,String msg){
		if(debug){
			log.info(msg);
		}
	}
	private static void logDebug(Logger log,boolean debug,String msg){
		if(debug){
			log.debug(msg);
		}
	}
	
	private static final String SUFFIX_IN_CORSO_PARENTESI_QUADRE = "] ...";
	private static final String SUFFIX_IN_CORSO = ") ...";
	private static final String SUFFIX_WARNING_ONLY_IN_CORSO = ") (warning-only) ...";
	private static final String SUFFIX_TERMINATA = ") terminata";
	private static final String SUFFIX_WARNING_ONLY_TERMINATA = ") (warning-only) terminata";
	private static final String EVERY = " every:";
	private static final String SEPARATOR = "=================================================================================";
	
	private static String getSuffixDate(SimpleDateFormat df, Date newInterval, int secondi) {
		return "[next-interval: "+df.format(newInterval)+
			"] [Prossimo Controllo previsto tra "+secondi+" secondi: "+df.format(new Date(DateManager.getTimeMillis()+(secondi*1000)))+"]";
	}
		
	public Date process(Logger log, int secondi, Date lastInterval, Connection connection, boolean debug) throws UtilsException{
		
		SimpleDateFormat df = DateUtils.getSimpleDateFormatMs();
		
		logInfo(log,debug,SEPARATOR);
		
		logInfo(log,debug,"Analisi memoria per generazione eventi in corso [interval: "+df.format(lastInterval)+SUFFIX_IN_CORSO_PARENTESI_QUADRE);
				
		// Raccolgo informazioni in memoria per rilasciare il lock
		
		// TH_*
		DatiEventoGenerico localInMemoryLastMaxRequests = null;
		DatiEventoGenerico localInMemoryLastMaxRequestsWarningOnly = null;
		DatiEventoGenerico localInMemoryLastPddCongestionata = null;
		Map<String, DatiEventoGenerico> localInMemoryLastPolicyGlobaliViolated = new HashMap<>();
		Map<String, DatiEventoGenerico> localInMemoryLastPolicyGlobaliViolatedWarningOnly = new HashMap<>();	
		Map<String, DatiEventoGenerico> localInMemoryLastPolicyAPIViolated = new HashMap<>();
		Map<String, DatiEventoGenerico> localInMemoryLastPolicyAPIViolatedWarningOnly = new HashMap<>();
		Date newInterval = null;
		lock.acquire("process");
		try {
			
			localInMemoryLastMaxRequests = this.inMemoryLastMaxRequests.readAndConsume();
			localInMemoryLastMaxRequestsWarningOnly = this.inMemoryLastMaxRequestsWarningOnly.readAndConsume();
			localInMemoryLastPddCongestionata = this.inMemoryLastPddCongestionata.readAndConsume();
			
			if(this.inMemoryLastPolicyGlobaliViolated!=null && this.inMemoryLastPolicyGlobaliViolated.size()>0){
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastPolicyGlobaliViolated.entrySet()) {
					localInMemoryLastPolicyGlobaliViolated.put(entry.getKey(), entry.getValue().readAndConsume());
				}
			}
			if(this.inMemoryLastPolicyGlobaliViolatedWarningOnly!=null && this.inMemoryLastPolicyGlobaliViolatedWarningOnly.size()>0){
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastPolicyGlobaliViolatedWarningOnly.entrySet()) {
					localInMemoryLastPolicyGlobaliViolatedWarningOnly.put(entry.getKey(), entry.getValue().readAndConsume());
				}
			}
			
			if(this.inMemoryLastPolicyAPIViolated!=null && this.inMemoryLastPolicyAPIViolated.size()>0){
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastPolicyAPIViolated.entrySet()) {
					localInMemoryLastPolicyAPIViolated.put(entry.getKey(), entry.getValue().readAndConsume());
				}
			}
			if(this.inMemoryLastPolicyAPIViolatedWarningOnly!=null && this.inMemoryLastPolicyAPIViolatedWarningOnly.size()>0){
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastPolicyAPIViolatedWarningOnly.entrySet()) {
					localInMemoryLastPolicyAPIViolatedWarningOnly.put(entry.getKey(), entry.getValue().readAndConsume());
				}
			}

			newInterval = DateManager.getDate();
		}finally {
			lock.release("process");
		}

		
		// Gli eventi db_* sono acceduti solo dal thread non serve un synchronized
		
		// DB_*
		DatabaseDatiEventoGenerico localDbLastMaxRequests = null;
		DatabaseDatiEventoGenerico localDbLastMaxRequestsWarningOnly = null;
		DatabaseDatiEventoGenerico localDbLastPddCongestionata = null;
		Map<String, DatabaseDatiEventoGenerico> localDbLastPolicyGlobaliViolated = null;
		Map<String, DatabaseDatiEventoGenerico> localDbLastPolicyGlobaliViolatedWarningOnly = null;
		Map<String, DatabaseDatiEventoGenerico> localDbLastPolicyAPIViolated = null;
		Map<String, DatabaseDatiEventoGenerico> localDbLastPolicyAPIViolatedWarningOnly = null;
		localDbLastMaxRequests = this.dbLastMaxRequests;
		localDbLastMaxRequestsWarningOnly = this.dbLastMaxRequestsWarningOnly;
		localDbLastPddCongestionata = this.dbLastPddCongestionata;
		localDbLastPolicyGlobaliViolated = this.dbLastPolicyGlobaliViolated;
		localDbLastPolicyGlobaliViolatedWarningOnly = this.dbLastPolicyGlobaliViolatedWarningOnly;
		localDbLastPolicyAPIViolated = this.dbLastPolicyAPIViolated;
		localDbLastPolicyAPIViolatedWarningOnly = this.dbLastPolicyAPIViolatedWarningOnly;

		
		// Procedo ad effettuare l'elaborazione per emettere degli eventi
		
		logDebug(log,debug,"1. Analisi violazioni numero massimo richieste simultanee ...");
		processSingleEvent(log, 
				localInMemoryLastMaxRequests, 
				localDbLastMaxRequests, 
				this.dbLastMaxRequests, 
				TipoEvento.CONTROLLO_TRAFFICO_NUMERO_MASSIMO_RICHIESTE_SIMULTANEE,
				CodiceEventoControlloTraffico.VIOLAZIONE, 
				CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
				lastInterval, connection, this.gestoreEventi,
				null, debug);
		logDebug(log,debug,"1. Analisi violazioni numero massimo richieste simultanee terminata");
		
		logDebug(log,debug,"2. Analisi violazioni numero massimo richieste simultanee (warning-only) ...");
		processSingleEvent(log, 
				localInMemoryLastMaxRequestsWarningOnly, 
				localDbLastMaxRequestsWarningOnly, 
				this.dbLastMaxRequestsWarningOnly, 
				TipoEvento.CONTROLLO_TRAFFICO_NUMERO_MASSIMO_RICHIESTE_SIMULTANEE,
				CodiceEventoControlloTraffico.VIOLAZIONE_WARNING_ONLY, 
				CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA_WARNING_ONLY, 
				lastInterval, connection, this.gestoreEventi,
				null, debug);
		logDebug(log,debug,"2. Analisi violazioni numero massimo richieste simultanee (warning-only) terminata");
		
		logDebug(log,debug,"3. Analisi controllo della congestione ...");
		processSingleEvent(log, 
				localInMemoryLastPddCongestionata, 
				localDbLastPddCongestionata, 
				this.dbLastPddCongestionata, 
				TipoEvento.CONTROLLO_TRAFFICO_SOGLIA_CONGESTIONE,
				CodiceEventoControlloTraffico.VIOLAZIONE, 
				CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
				lastInterval, connection, this.gestoreEventi,
				null, debug);
		logDebug(log,debug,"3. Analisi controllo della congestione terminata");
				
		logDebug(log,debug,"4. Analisi policy globali violate (size:"+localInMemoryLastPolicyGlobaliViolated.size()+SUFFIX_IN_CORSO);
		if(!localInMemoryLastPolicyGlobaliViolated.isEmpty()) {
			for (Map.Entry<String,DatiEventoGenerico> entry : localInMemoryLastPolicyGlobaliViolated.entrySet()) {
				String idPolicy = entry.getKey();
				processSingleEvent(log, 
						localInMemoryLastPolicyGlobaliViolated.get(idPolicy), 
						localDbLastPolicyGlobaliViolated.get(idPolicy), 
						this.dbLastPolicyGlobaliViolated.get(idPolicy), 
						TipoEvento.RATE_LIMITING_POLICY_GLOBALE,
						CodiceEventoControlloTraffico.VIOLAZIONE, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);
			}
		}
		logDebug(log,debug,"4. Analisi policy globali violate (size:"+localInMemoryLastPolicyGlobaliViolated.size()+SUFFIX_TERMINATA);
		
		logDebug(log,debug,"5. Analisi policy globali violate (size:"+localInMemoryLastPolicyGlobaliViolatedWarningOnly.size()+SUFFIX_WARNING_ONLY_IN_CORSO);
		if(!localInMemoryLastPolicyGlobaliViolatedWarningOnly.isEmpty()) {
			for (Map.Entry<String,DatiEventoGenerico> entry : localInMemoryLastPolicyGlobaliViolatedWarningOnly.entrySet()) {
				String idPolicy = entry.getKey();
				processSingleEvent(log, 
						localInMemoryLastPolicyGlobaliViolatedWarningOnly.get(idPolicy), 
						localDbLastPolicyGlobaliViolatedWarningOnly.get(idPolicy), 
						this.dbLastPolicyGlobaliViolatedWarningOnly.get(idPolicy), 
						TipoEvento.RATE_LIMITING_POLICY_GLOBALE,
						CodiceEventoControlloTraffico.VIOLAZIONE_WARNING_ONLY, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA_WARNING_ONLY, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);
			}
		}
		logDebug(log,debug,"5. Analisi policy globali violate (size:"+localInMemoryLastPolicyGlobaliViolatedWarningOnly.size()+SUFFIX_WARNING_ONLY_TERMINATA);
		
		logDebug(log,debug,"6. Analisi policy API violate (size:"+localInMemoryLastPolicyAPIViolated.size()+SUFFIX_IN_CORSO);
		if(!localInMemoryLastPolicyAPIViolated.isEmpty()) {
			for (Map.Entry<String,DatiEventoGenerico> entry : localInMemoryLastPolicyAPIViolated.entrySet()) {
				String idPolicy = entry.getKey();
				processSingleEvent(log, 
						localInMemoryLastPolicyAPIViolated.get(idPolicy), 
						localDbLastPolicyAPIViolated.get(idPolicy), 
						this.dbLastPolicyAPIViolated.get(idPolicy), 
						TipoEvento.RATE_LIMITING_POLICY_API,
						CodiceEventoControlloTraffico.VIOLAZIONE, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);	
			}
		}
		logDebug(log,debug,"6. Analisi policy API violate (size:"+localInMemoryLastPolicyAPIViolated.size()+SUFFIX_TERMINATA);
		
		logDebug(log,debug,"7. Analisi policy API violate (size:"+localInMemoryLastPolicyAPIViolatedWarningOnly.size()+SUFFIX_WARNING_ONLY_IN_CORSO);
		if(!localInMemoryLastPolicyAPIViolatedWarningOnly.isEmpty()) {
			for (Map.Entry<String,DatiEventoGenerico> entry : localInMemoryLastPolicyAPIViolatedWarningOnly.entrySet()) {
				String idPolicy = entry.getKey();
				processSingleEvent(log, 
						localInMemoryLastPolicyAPIViolatedWarningOnly.get(idPolicy), 
						localDbLastPolicyAPIViolatedWarningOnly.get(idPolicy), 
						this.dbLastPolicyAPIViolatedWarningOnly.get(idPolicy), 
						TipoEvento.RATE_LIMITING_POLICY_API,
						CodiceEventoControlloTraffico.VIOLAZIONE_WARNING_ONLY, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA_WARNING_ONLY, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);	
			}
		}
		logDebug(log,debug,"7. Analisi policy API violate (size:"+localInMemoryLastPolicyAPIViolatedWarningOnly.size()+SUFFIX_WARNING_ONLY_TERMINATA);
		

		logInfo(log,debug,"Analisi memoria per generazione eventi terminata "+getSuffixDate(df, newInterval, secondi));
		
		return newInterval; // da usare per il prossimo intervallo
	}
	public Date processConnectionTimeout(Logger log, int secondi, Date lastInterval, Connection connection, boolean debug) throws UtilsException{
		
		SimpleDateFormat df = DateUtils.getSimpleDateFormatMs();
		
		logInfo(log,debug,SEPARATOR);
		
		logInfo(log,debug,"Analisi memoria per generazione eventi in corso 'ConnectionTimeout' [interval: "+df.format(lastInterval)+SUFFIX_IN_CORSO_PARENTESI_QUADRE);
				
		// Raccolgo informazioni in memoria per rilasciare il lock
		
		// TH_*
		Map<String, DatiEventoGenerico> localInMemoryLastTimeoutConnessione = new HashMap<>();
		Date newInterval = null;
		lock.acquire("processConnectionTimeout");
		try {
			if(this.inMemoryLastTimeoutConnessione!=null && this.inMemoryLastTimeoutConnessione.size()>0){
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastTimeoutConnessione.entrySet()) {
					localInMemoryLastTimeoutConnessione.put(entry.getKey(), entry.getValue().readAndConsume());
				}
			}

			newInterval = DateManager.getDate();
		}finally {
			lock.release("processConnectionTimeout");
		}

		
		// Gli eventi db_* sono acceduti solo dal thread non serve un synchronized
		
		// DB_*
		Map<String, DatabaseDatiEventoGenerico> localDbLastTimeoutConnessione = null;
		localDbLastTimeoutConnessione = this.dbLastTimeoutConnessione;

		
		// Procedo ad effettuare l'elaborazione per emettere degli eventi
		
		logDebug(log,debug,"8. Analisi eventi di Timeout durante la connessione (size:"+localInMemoryLastTimeoutConnessione.size()+SUFFIX_IN_CORSO);
		if(!localInMemoryLastTimeoutConnessione.isEmpty()) {
			for (Map.Entry<String,DatiEventoGenerico> entry : localInMemoryLastTimeoutConnessione.entrySet()) {
				String idPolicy = entry.getKey();
				processSingleEvent(log, 
						localInMemoryLastTimeoutConnessione.get(idPolicy), 
						localDbLastTimeoutConnessione.get(idPolicy), 
						this.dbLastTimeoutConnessione.get(idPolicy), 
						TipoEvento.CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT,
						CodiceEventoControlloTraffico.VIOLAZIONE, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);	
			}
		}
		logDebug(log,debug,"8. Analisi eventi di Timeout durante la connessione (size:"+localInMemoryLastTimeoutConnessione.size()+SUFFIX_TERMINATA);
		
		logInfo(log,debug,"Analisi memoria per generazione eventi terminata 'ConnectionTimeout' "+getSuffixDate(df, newInterval, secondi));
		
		return newInterval; // da usare per il prossimo intervallo
	}
	public void emitProcessConnectionTimeoutSkip(Logger log, boolean debug, int offsetConnectionTimeoutEveryXTimes, int checkConnectionTimeoutEveryXTimes) {
		logDebug(log,debug,"8. Analisi eventi di Timeout durante la connessione non abilitata in questa iterazione (offset: "+offsetConnectionTimeoutEveryXTimes+EVERY+checkConnectionTimeoutEveryXTimes+")");
	}
	public Date processRequestReadTimeout(Logger log, int secondi, Date lastInterval, Connection connection, boolean debug) throws UtilsException{
		
		SimpleDateFormat df = DateUtils.getSimpleDateFormatMs();
		
		logInfo(log,debug,SEPARATOR);
		
		logInfo(log,debug,"Analisi memoria per generazione eventi in corso 'RequestReadTimeout' [interval: "+df.format(lastInterval)+SUFFIX_IN_CORSO_PARENTESI_QUADRE);
				
		// Raccolgo informazioni in memoria per rilasciare il lock
		
		// TH_*
		Map<String, DatiEventoGenerico> localInMemoryLastTimeoutRichiesta = new HashMap<>();
		Date newInterval = null;
		lock.acquire("processRequestReadTimeout");
		try {
			if(this.inMemoryLastTimeoutRichiesta!=null && this.inMemoryLastTimeoutRichiesta.size()>0){
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastTimeoutRichiesta.entrySet()) {
					localInMemoryLastTimeoutRichiesta.put(entry.getKey(), entry.getValue().readAndConsume());
				}
			}

			newInterval = DateManager.getDate();
		}finally {
			lock.release("processRequestReadTimeout");
		}

		
		// Gli eventi db_* sono acceduti solo dal thread non serve un synchronized
		
		// DB_*
		Map<String, DatabaseDatiEventoGenerico> localDbLastTimeoutRichiesta = null;
		localDbLastTimeoutRichiesta = this.dbLastTimeoutRichiesta;

		
		// Procedo ad effettuare l'elaborazione per emettere degli eventi
				
		logDebug(log,debug,"9. Analisi eventi di Timeout durante la ricezione della richiesta (size:"+localInMemoryLastTimeoutRichiesta.size()+SUFFIX_IN_CORSO);
		if(!localInMemoryLastTimeoutRichiesta.isEmpty()) {
			for (Map.Entry<String,DatiEventoGenerico> entry : localInMemoryLastTimeoutRichiesta.entrySet()) {
				String idPolicy = entry.getKey();
				processSingleEvent(log, 
						localInMemoryLastTimeoutRichiesta.get(idPolicy), 
						localDbLastTimeoutRichiesta.get(idPolicy), 
						this.dbLastTimeoutRichiesta.get(idPolicy), 
						TipoEvento.CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT,
						CodiceEventoControlloTraffico.VIOLAZIONE, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);	
			}
		}
		logDebug(log,debug,"9. Analisi eventi di Timeout durante la ricezione della richiesta (size:"+localInMemoryLastTimeoutRichiesta.size()+SUFFIX_TERMINATA);
		
		logInfo(log,debug,"Analisi memoria per generazione eventi terminata 'RequestReadTimeout' "+getSuffixDate(df, newInterval, secondi));
		
		return newInterval; // da usare per il prossimo intervallo
	}
	public void emitProcessRequestReadTimeoutSkip(Logger log, boolean debug, int offsetRequestReadTimeoutEveryXTimes, int checkRequestReadTimeoutEveryXTimes) {
		logDebug(log,debug,"9. Analisi eventi di Timeout durante la ricezione della richiesta non abilitata in questa iterazione (offset: "+offsetRequestReadTimeoutEveryXTimes+EVERY+checkRequestReadTimeoutEveryXTimes+")");
	}
	public Date processReadTimeout(Logger log, int secondi, Date lastInterval, Connection connection, boolean debug) throws UtilsException{
		
		SimpleDateFormat df = DateUtils.getSimpleDateFormatMs();
		
		logInfo(log,debug,SEPARATOR);
		
		logInfo(log,debug,"Analisi memoria per generazione eventi in corso 'ReadTimeout' [interval: "+df.format(lastInterval)+SUFFIX_IN_CORSO_PARENTESI_QUADRE);
				
		// Raccolgo informazioni in memoria per rilasciare il lock
		
		// TH_*
		Map<String, DatiEventoGenerico> localInMemoryLastTimeoutRisposta = new HashMap<>();
		Date newInterval = null;
		lock.acquire("processReadTimeout");
		try {
			if(this.inMemoryLastTimeoutRisposta!=null && this.inMemoryLastTimeoutRisposta.size()>0){
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastTimeoutRisposta.entrySet()) {
					localInMemoryLastTimeoutRisposta.put(entry.getKey(), entry.getValue().readAndConsume());
				}
			}

			newInterval = DateManager.getDate();
		}finally {
			lock.release("processReadTimeout");
		}

		
		// Gli eventi db_* sono acceduti solo dal thread non serve un synchronized
		
		// DB_*
		Map<String, DatabaseDatiEventoGenerico> localDbLastTimeoutRisposta = null;
		localDbLastTimeoutRisposta = this.dbLastTimeoutRisposta;

		
		// Procedo ad effettuare l'elaborazione per emettere degli eventi
		
		logDebug(log,debug,"10. Analisi eventi di Timeout durante la ricezione della risposta (size:"+localInMemoryLastTimeoutRisposta.size()+SUFFIX_IN_CORSO);
		if(!localInMemoryLastTimeoutRisposta.isEmpty()) {
			for (Map.Entry<String,DatiEventoGenerico> entry : localInMemoryLastTimeoutRisposta.entrySet()) {
				String idPolicy = entry.getKey();
				processSingleEvent(log, 
						localInMemoryLastTimeoutRisposta.get(idPolicy), 
						localDbLastTimeoutRisposta.get(idPolicy), 
						this.dbLastTimeoutRisposta.get(idPolicy), 
						TipoEvento.CONTROLLO_TRAFFICO_READ_TIMEOUT,
						CodiceEventoControlloTraffico.VIOLAZIONE, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);	
			}
		}
		logDebug(log,debug,"10. Analisi eventi di Timeout durante la ricezione della risposta (size:"+localInMemoryLastTimeoutRisposta.size()+SUFFIX_TERMINATA);

		
		logInfo(log,debug,"Analisi memoria per generazione eventi terminata 'ReadTimeout' "+getSuffixDate(df, newInterval, secondi));
		
		return newInterval; // da usare per il prossimo intervallo
	}
	public void emitProcessReadTimeoutSkip(Logger log, boolean debug, int offsetReadTimeoutEveryXTimes, int checkReadTimeoutEveryXTimes) {
		logDebug(log,debug,"10. Analisi eventi di Timeout durante la ricezione della risposta non abilitata in questa iterazione (offset: "+offsetReadTimeoutEveryXTimes+EVERY+checkReadTimeoutEveryXTimes+")");
	}
	
	
	private static void processSingleEvent(Logger log, DatiEventoGenerico localInMemory,DatabaseDatiEventoGenerico localDb,
			DatabaseDatiEventoGenerico thisDb,
			TipoEvento tipoEvento,
			CodiceEventoControlloTraffico eventoViolazione, CodiceEventoControlloTraffico eventoViolazioneRisolta, 
			Date lastInterval, Connection connection, GestoreEventi gestoreEventi,
			String idPolicy, boolean debug) throws UtilsException {
		
		// Gestione violazione maxThreads
		
		boolean esisteTHultimoIntervallo = false;
		if(localInMemory!=null && 
				localInMemory.data!=null && 
				localInMemory.data.after(lastInterval)){
			esisteTHultimoIntervallo = true;
		}
		
		if(localDb!=null && 
				localDb.data!=null){
			logDebug(log,debug,"\tTH_ultimoIntervallo: "+esisteTHultimoIntervallo+
					" DB_last:"+(localDb!=null)+
					" DB_last.codiceEvento:"+localDb.codiceEvento.name());
		}
		else{
			logDebug(log,debug,"\tTH_ultimoIntervallo: "+esisteTHultimoIntervallo);
		}
	
		if(esisteTHultimoIntervallo){
			if(localDb!=null && 
					localDb.data!=null){
				if(eventoViolazioneRisolta.equals(localDb.codiceEvento)){
					// sono ritornato dentro una violazione di stato
					CodiceEventoControlloTraffico codice = eventoViolazione;
					Evento evento = buildEvento(tipoEvento,codice, idPolicy,
							localInMemory.descrizione,
							localInMemory.configurazione,
							localInMemory.data); // uso come data dell'evento la data in cui e' accaduta la segnalazione
					logEvento(gestoreEventi, connection, evento, log, debug);
					/**synchronized (this.semaphore) { // Gli eventi db_* sono acceduti solo dal thread non serve un synchronized*/
					thisDb.data = evento.getOraRegistrazione();
					thisDb.codiceEvento = codice;
					/**}*/
					if(idPolicy!=null)
						logDebug(log,debug,getProcessSingleEventMessage(tipoEvento, codice, idPolicy));
					else
						logDebug(log,debug,getProcessSingleEventMessage(tipoEvento, codice));
				}
			}
			else{
				// prima volta che succede il problema, emetto evento di violazione
				CodiceEventoControlloTraffico codice = eventoViolazione;
				Evento evento = buildEvento(tipoEvento,codice, idPolicy,
						localInMemory.descrizione,
						localInMemory.configurazione,
						localInMemory.data); // uso come data dell'evento la data in cui e' accaduta la segnalazione
				logEvento(gestoreEventi, connection, evento, log, debug);
				/** synchronized (this.semaphore) { // Gli eventi db_* sono acceduti solo dal thread non serve un synchronized*/
				thisDb.data = evento.getOraRegistrazione();
				thisDb.codiceEvento = codice;
				if(idPolicy!=null)
					logDebug(log,debug,getProcessSingleEventMessage(tipoEvento, codice, idPolicy));
				else
					logDebug(log,debug,getProcessSingleEventMessage(tipoEvento, codice));
				/**}*/
			}
		}
		else{
			if(localDb!=null && 
					localDb.data!=null &&
				eventoViolazione.equals(localDb.codiceEvento)){
				// emetto evento che non risulta piu' violato.
				CodiceEventoControlloTraffico codice = eventoViolazioneRisolta;
				Evento evento = buildEvento(tipoEvento,codice, idPolicy,
						localDb.descrizione, 
						localInMemory!=null && localInMemory.configurazione!=null ? localInMemory.configurazione : localDb.configurazione, // importante per far arrivare la configurazione usata nella gestione delle policy
						DateManager.getDate());
				logEvento(gestoreEventi, connection, evento, log, debug);
				/** synchronized (this.semaphore) { // Gli eventi db_* sono acceduti solo dal thread non serve un synchronized*/
				thisDb.data = evento.getOraRegistrazione();
				thisDb.codiceEvento = codice;
				if(idPolicy!=null)
					logDebug(log,debug,getProcessSingleEventMessage(tipoEvento, codice, idPolicy));
				else
					logDebug(log,debug,getProcessSingleEventMessage(tipoEvento, codice));
				/**}*/
			}
		}

	}
	
	private static String getProcessSingleEventMessage(TipoEvento tipoEvento, CodiceEventoControlloTraffico codice, String idPolicy) {
		return "\tEmetto Evento tipo["+tipoEvento+"] codice["+codice+"_"+idPolicy+"]";
	}
	private static String getProcessSingleEventMessage(TipoEvento tipoEvento, CodiceEventoControlloTraffico codice) {
		return "\tEmetto Evento tipo["+tipoEvento+"] codice["+codice+"]";
	}
	
	private static Evento buildEvento(TipoEvento tipoEvento, CodiceEventoControlloTraffico codice, String idPolicy, String descrizione, String configurazione, Date data) throws UtilsException {
		Evento evento = new Evento();
		evento.setTipo(tipoEvento.getValue());
		evento.setCodice(codice.getValue());
		if(idPolicy!=null){
			evento.setIdConfigurazione(idPolicy);
		}		
		evento.setDescrizione(descrizione);
		evento.setConfigurazione(configurazione);
		evento.setOraRegistrazione(data);
		
		switch (tipoEvento) {
		case CONTROLLO_TRAFFICO_NUMERO_MASSIMO_RICHIESTE_SIMULTANEE:
			switch (codice) {
			case VIOLAZIONE:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.ERROR));
				break;
			case VIOLAZIONE_RISOLTA:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.INFO));
				break;
			case VIOLAZIONE_WARNING_ONLY:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.WARN));
				break;
			case VIOLAZIONE_RISOLTA_WARNING_ONLY:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.INFO));
				break;
			default:
				// altri casi non previsti per CONTROLLO_TRAFFICO_NUMERO_MASSIMO_RICHIESTE_SIMULTANEE
				break;
			}
			break;
		case CONTROLLO_TRAFFICO_SOGLIA_CONGESTIONE:
			switch (codice) {
			case VIOLAZIONE:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.WARN));
				break;
			case VIOLAZIONE_RISOLTA:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.INFO));
				break;
			default:
				// altri casi non previsti per CONTROLLO_TRAFFICO_NUMERO_MASSIMO_RICHIESTE_SIMULTANEE
				break;
			}
			break;
		case RATE_LIMITING_POLICY_GLOBALE:
		case RATE_LIMITING_POLICY_API:
			switch (codice) {
			case VIOLAZIONE:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.ERROR));
				break;
			case VIOLAZIONE_RISOLTA:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.INFO));
				break;
			case VIOLAZIONE_WARNING_ONLY:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.WARN));
				break;
			case VIOLAZIONE_RISOLTA_WARNING_ONLY:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.INFO));
				break;
			}
			break;
		case CONTROLLO_TRAFFICO_CONNECTION_TIMEOUT:
		case CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT:
		case CONTROLLO_TRAFFICO_READ_TIMEOUT:
			switch (codice) {
			case VIOLAZIONE:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.ERROR));
				break;
			case VIOLAZIONE_RISOLTA:
				evento.setSeverita(SeveritaConverter.toIntValue(TipoSeverita.INFO));
				break;
			default:
				// altri casi non previsti per CONTROLLO_TRAFFICO_READ_TIMEOUT
				break;
			}
			break;
		default:
			// altri casi non previsti per questo notificatore eventi
			break;
		}
		
		evento.setClusterId(OpenSPCoop2Properties.getInstance().getClusterId(false));
		return evento;
	}
	
	private static void logEvento(GestoreEventi gestoreEventi, Connection connection, Evento evento, Logger log, boolean debug) throws UtilsException {
		
		// Fix evento per passarlo al notifier
		String configurazione = evento.getConfigurazione();
		if(PolicyUtilities.isConfigurazioneEventoPerPolicy(configurazione)) {
			evento.setConfigurazione(null); // serve solo per passarlo al notifier
		}
		
		try {
			gestoreEventi.log(evento, connection);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		
		if(properties.isControlloTrafficoEnabled()) {
			
			ConfigurazioneGatewayControlloTraffico config = properties.getConfigurazioneControlloTraffico();
			if(config.isNotifierEnabled()) {
				
				INotify notifier = config.getNotifier();
				if(notifier.isNotifichePassiveAttive()) {
					TipoEvento tipoEvento = TipoEvento.toEnumConstant(evento.getTipo());
					CodiceEventoControlloTraffico codiceEvento = CodiceEventoControlloTraffico.toEnumConstant(evento.getCodice());
					switch (tipoEvento) {
					case CONTROLLO_TRAFFICO_NUMERO_MASSIMO_RICHIESTE_SIMULTANEE:
					case CONTROLLO_TRAFFICO_SOGLIA_CONGESTIONE:
						notifier.updateStatoRilevamentoCongestione(log, debug, tipoEvento, codiceEvento, evento.getDescrizione());
						break;
					case RATE_LIMITING_POLICY_GLOBALE:
					case RATE_LIMITING_POLICY_API:
						notifier.updateStatoRilevamentoViolazionePolicy(log, debug, tipoEvento, codiceEvento, evento.getIdConfigurazione(), configurazione);
						break;
					case CONTROLLO_TRAFFICO_REQUEST_READ_TIMEOUT:
						notifier.updateStatoRilevamentoRequestReadTimeout(log, debug, tipoEvento, codiceEvento, evento.getIdConfigurazione(), configurazione);
						break;
					case CONTROLLO_TRAFFICO_READ_TIMEOUT:
						notifier.updateStatoRilevamentoReadTimeout(log, debug, tipoEvento, codiceEvento, evento.getIdConfigurazione(), configurazione);
						break;
					default:
						// altri casi non previsti per questo notificatore eventi
						break;
					}
				}
			}
			
		}
		
	}

	
	

	//---- Per salvare

	private static final String ZIP_IN_MEMORY = "memory";
	private static final String ZIP_DB = "db";
	private static final String ZIP_LAST_MAX_REQUESTS = "lastMaxRequests.xml";
	private static final String ZIP_LAST_MAX_REQUESTS_WARNING_ONLY = "lastMaxRequests_warningOnly.xml";
	private static final String ZIP_LAST_PDD_CONGESTIONATA = "lastPddCongestionata.xml";
	private static final String ZIP_POLICY_GLOBALE = "policyGlobale";
	private static final String ZIP_POLICY_GLOBALE_WARNING_ONLY = "policyGlobale_warningOnly";
	private static final String ZIP_POLICY_API = "policyAPI";
	private static final String ZIP_POLICY_API_WARNING_ONLY = "policyAPI_warningOnly";
	private static final String ZIP_EVENTO_TIMEOUT_CONNESSIONE = "connectionTimeout";
	private static final String ZIP_EVENTO_TIMEOUT_RICHIESTA = "requestReadTimeout";
	private static final String ZIP_EVENTO_TIMEOUT_RISPOSTA = "readTimeout";

	
	public void serialize(File file) throws UtilsException{
		try (FileOutputStream out = new FileOutputStream(file, false);){ // se già esiste lo sovrascrive
			this.serialize(out);
			out.flush();
		}
		catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	@Deprecated
	// Il meccanismo non funzionava, non si riusciva a riottenere uno stato corretto
	public void serialize(OutputStream out) throws UtilsException{
			
		try (ZipOutputStream zipOut = new ZipOutputStream(out);){
			
			String rootPackageDir = "";
			// Il codice dopo fissa il problema di inserire una directory nel package.
			// Commentare la riga sotto per ripristinare il vecchio comportamento.
			rootPackageDir = Costanti.OPENSPCOOP2_ARCHIVE_ROOT_DIR+File.separatorChar;
			
			
			// String inMemory Path
			String inMemoryDir = rootPackageDir + ZIP_IN_MEMORY + File.separatorChar;
			
			if(this.inMemoryLastMaxRequests!=null) {
				zipOut.putNextEntry(new ZipEntry(inMemoryDir+ZIP_LAST_MAX_REQUESTS));
				convertToDatiEventoGenericoSerializabled(this.inMemoryLastMaxRequests, "inMemory_lastMaxRequests").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.inMemoryLastMaxRequestsWarningOnly!=null) {
				zipOut.putNextEntry(new ZipEntry(inMemoryDir+ZIP_LAST_MAX_REQUESTS_WARNING_ONLY));
				convertToDatiEventoGenericoSerializabled(this.inMemoryLastMaxRequestsWarningOnly, "inMemory_lastMaxRequests_warningOnly").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.inMemoryLastPddCongestionata!=null) {
				zipOut.putNextEntry(new ZipEntry(inMemoryDir+ZIP_LAST_PDD_CONGESTIONATA));
				convertToDatiEventoGenericoSerializabled(this.inMemoryLastPddCongestionata, "inMemory_lastPddCongestionata").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.inMemoryLastPolicyGlobaliViolated!=null && this.inMemoryLastPolicyGlobaliViolated.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_POLICY_GLOBALE + File.separatorChar;
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastPolicyGlobaliViolated.entrySet()) {
					String idPolicy = entry.getKey();
					DatiEventoGenerico evento = this.inMemoryLastPolicyGlobaliViolated.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_POLICY_GLOBALE+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.inMemoryLastPolicyGlobaliViolatedWarningOnly!=null && this.inMemoryLastPolicyGlobaliViolatedWarningOnly.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_POLICY_GLOBALE_WARNING_ONLY + File.separatorChar;
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastPolicyGlobaliViolatedWarningOnly.entrySet()) {
					String idPolicy = entry.getKey();
					DatiEventoGenerico evento = this.inMemoryLastPolicyGlobaliViolatedWarningOnly.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_POLICY_GLOBALE_WARNING_ONLY+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.inMemoryLastPolicyAPIViolated!=null && this.inMemoryLastPolicyAPIViolated.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_POLICY_API + File.separatorChar;
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastPolicyAPIViolated.entrySet()) {
					String idPolicy = entry.getKey();
					DatiEventoGenerico evento = this.inMemoryLastPolicyAPIViolated.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_POLICY_API+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.inMemoryLastPolicyAPIViolatedWarningOnly!=null && this.inMemoryLastPolicyAPIViolatedWarningOnly.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_POLICY_API_WARNING_ONLY + File.separatorChar;
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastPolicyAPIViolatedWarningOnly.entrySet()) {
					String idPolicy = entry.getKey();
					DatiEventoGenerico evento = this.inMemoryLastPolicyAPIViolatedWarningOnly.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_POLICY_API_WARNING_ONLY+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.inMemoryLastTimeoutConnessione!=null && this.inMemoryLastTimeoutConnessione.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_EVENTO_TIMEOUT_CONNESSIONE + File.separatorChar;
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastTimeoutConnessione.entrySet()) {
					String idPolicy = entry.getKey();
					DatiEventoGenerico evento = this.inMemoryLastTimeoutConnessione.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_EVENTO_TIMEOUT_CONNESSIONE+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.inMemoryLastTimeoutRichiesta!=null && this.inMemoryLastTimeoutRichiesta.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_EVENTO_TIMEOUT_RICHIESTA + File.separatorChar;
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastTimeoutRichiesta.entrySet()) {
					String idPolicy = entry.getKey();
					DatiEventoGenerico evento = this.inMemoryLastTimeoutRichiesta.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_EVENTO_TIMEOUT_RICHIESTA+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.inMemoryLastTimeoutRisposta!=null && this.inMemoryLastTimeoutRisposta.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_EVENTO_TIMEOUT_RISPOSTA + File.separatorChar;
				for (Map.Entry<String,DatiEventoGenerico> entry : this.inMemoryLastTimeoutRisposta.entrySet()) {
					String idPolicy = entry.getKey();
					DatiEventoGenerico evento = this.inMemoryLastTimeoutRisposta.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_EVENTO_TIMEOUT_RISPOSTA+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			
			// String db Path
			String dbDir = rootPackageDir + ZIP_DB + File.separatorChar;
			
			if(this.dbLastMaxRequests!=null) {
				zipOut.putNextEntry(new ZipEntry(dbDir+ZIP_LAST_MAX_REQUESTS));
				convertDBToDatiEventoGenericoSerializabled(this.dbLastMaxRequests, "db_lastMaxRequests").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.dbLastMaxRequestsWarningOnly!=null) {
				zipOut.putNextEntry(new ZipEntry(dbDir+ZIP_LAST_MAX_REQUESTS_WARNING_ONLY));
				convertDBToDatiEventoGenericoSerializabled(this.dbLastMaxRequestsWarningOnly, "db_lastMaxRequests_warningOnly").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.dbLastPddCongestionata!=null) {
				zipOut.putNextEntry(new ZipEntry(dbDir+ZIP_LAST_PDD_CONGESTIONATA));
				convertDBToDatiEventoGenericoSerializabled(this.dbLastPddCongestionata, "db_lastPddCongestionata").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.dbLastPolicyGlobaliViolated!=null && this.dbLastPolicyGlobaliViolated.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_POLICY_GLOBALE + File.separatorChar;
				for (Map.Entry<String,DatabaseDatiEventoGenerico> entry : this.dbLastPolicyGlobaliViolated.entrySet()) {
					String idPolicy = entry.getKey();
					DatabaseDatiEventoGenerico evento = this.dbLastPolicyGlobaliViolated.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_POLICY_GLOBALE+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.dbLastPolicyGlobaliViolatedWarningOnly!=null && this.dbLastPolicyGlobaliViolatedWarningOnly.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_POLICY_GLOBALE_WARNING_ONLY + File.separatorChar;
				for (Map.Entry<String,DatabaseDatiEventoGenerico> entry : this.dbLastPolicyGlobaliViolatedWarningOnly.entrySet()) {
					String idPolicy = entry.getKey();
					DatabaseDatiEventoGenerico evento = this.dbLastPolicyGlobaliViolatedWarningOnly.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_POLICY_GLOBALE_WARNING_ONLY+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.dbLastPolicyAPIViolated!=null && this.dbLastPolicyAPIViolated.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_POLICY_API + File.separatorChar;
				for (Map.Entry<String,DatabaseDatiEventoGenerico> entry : this.dbLastPolicyAPIViolated.entrySet()) {
					String idPolicy = entry.getKey();
					DatabaseDatiEventoGenerico evento = this.dbLastPolicyAPIViolated.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_POLICY_API+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.dbLastPolicyAPIViolatedWarningOnly!=null && this.dbLastPolicyAPIViolatedWarningOnly.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_POLICY_API_WARNING_ONLY + File.separatorChar;
				for (Map.Entry<String,DatabaseDatiEventoGenerico> entry : this.dbLastPolicyAPIViolatedWarningOnly.entrySet()) {
					String idPolicy = entry.getKey();
					DatabaseDatiEventoGenerico evento = this.dbLastPolicyAPIViolatedWarningOnly.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_POLICY_API_WARNING_ONLY+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.dbLastTimeoutConnessione!=null && this.dbLastTimeoutConnessione.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_EVENTO_TIMEOUT_CONNESSIONE + File.separatorChar;
				for (Map.Entry<String,DatabaseDatiEventoGenerico> entry : this.dbLastTimeoutConnessione.entrySet()) {
					String idPolicy = entry.getKey();
					DatabaseDatiEventoGenerico evento = this.dbLastTimeoutConnessione.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_EVENTO_TIMEOUT_CONNESSIONE+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.dbLastTimeoutRichiesta!=null && this.dbLastTimeoutRichiesta.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_EVENTO_TIMEOUT_RICHIESTA + File.separatorChar;
				for (Map.Entry<String,DatabaseDatiEventoGenerico> entry : this.dbLastTimeoutRichiesta.entrySet()) {
					String idPolicy = entry.getKey();
					DatabaseDatiEventoGenerico evento = this.dbLastTimeoutRichiesta.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_EVENTO_TIMEOUT_RICHIESTA+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.dbLastTimeoutRisposta!=null && this.dbLastTimeoutRisposta.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_EVENTO_TIMEOUT_RISPOSTA + File.separatorChar;
				for (Map.Entry<String,DatabaseDatiEventoGenerico> entry : this.dbLastTimeoutRisposta.entrySet()) {
					String idPolicy = entry.getKey();
					DatabaseDatiEventoGenerico evento = this.dbLastTimeoutRisposta.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_EVENTO_TIMEOUT_RISPOSTA+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
		
			
			zipOut.flush();

		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}

	}
	
	private org.openspcoop2.core.eventi.DatiEventoGenerico convertToDatiEventoGenericoSerializabled(DatiEventoGenerico eventoGenerico, String id){
		org.openspcoop2.core.eventi.DatiEventoGenerico e = new org.openspcoop2.core.eventi.DatiEventoGenerico();
		e.setIdEvento(id);
		e.setData(eventoGenerico.data);
		e.setDatiConsumatiThread(eventoGenerico.datiConsumatiThread);
		e.setDescrizione(eventoGenerico.descrizione);
		return e;
	}
	private org.openspcoop2.core.eventi.DatiEventoGenerico convertDBToDatiEventoGenericoSerializabled(DatabaseDatiEventoGenerico eventoGenerico, String id){
		org.openspcoop2.core.eventi.DatiEventoGenerico e = this.convertToDatiEventoGenericoSerializabled(eventoGenerico, id);
		if(eventoGenerico.codiceEvento!=null) {
			e.setCodiceEvento(eventoGenerico.codiceEvento.getValue());
		}
		return e;
	}
	
	
	@Deprecated
	/** Il meccanismo non funzionava, non si riusciva a riottenere uno stato corretto */
	public void initialize(InputStream in) throws UtilsException{
			
		if(in==null){
			return;
		}
		
		File f = null;
		String entryName = null;
		try {
			// Leggo InputStream
			byte [] bytesIn = Utilities.getAsByteArray(in);
			in.close();
			in = null;
			if(bytesIn==null || bytesIn.length<=0){
				return;
			}
			f = FileSystemUtilities.createTempFile("controlloTraffico", ".tmp");
			FileSystemUtilities.writeFile(f, bytesIn);
		}catch(Exception e){
			try {
				if(f!=null) {
					java.nio.file.Files.delete(f.toPath());
				}
			}catch(Exception eClose) {
				// ignore
			}
			try{
				if(in!=null)
					in.close();
			}catch(Exception eClose){
				// close
			}
			throw new UtilsException("["+entryName+"] "+e.getMessage(),e);
		}
		
		try (ZipFile zipFile = new ZipFile(f);){
			// Leggo Struttura ZIP

			String rootPackageDir = Costanti.OPENSPCOOP2_ARCHIVE_ROOT_DIR+File.separatorChar;
			
			String rootDir = null;
			
			org.openspcoop2.core.eventi.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.eventi.utils.serializer.JaxbDeserializer();
			
			Iterator<ZipEntry> it = ZipUtilities.entries(zipFile, true);
			while (it.hasNext()) {
				ZipEntry zipEntry = it.next();
				entryName = ZipUtilities.operativeSystemConversion(zipEntry.getName());
				
				/**System.out.println("FILE NAME:  "+entryName);
				System.out.println("SIZE:  "+zipEntry.getSize());*/

				// Il codice dopo fissa il problema di inserire una directory nel package.
				// Commentare la riga sotto per ripristinare il vecchio comportamento.
				if(rootDir==null){
					// Calcolo ROOT DIR
					rootDir=ZipUtilities.getRootDir(entryName);
				}
				
				if(!zipEntry.isDirectory()) {

					FileDataSource fds = new FileDataSource(entryName);
					String nome = fds.getName();
					String tipo = nome.substring(nome.lastIndexOf(".")+1,nome.length()); 
					tipo = tipo.toUpperCase();
					/**System.out.println("VERIFICARE NAME["+nome+"] TIPO["+tipo+"]");*/
					
					try (InputStream inputStream = zipFile.getInputStream(zipEntry);){
					
						byte[]content = Utilities.getAsByteArray(inputStream);
						
						boolean inMemory = false;
						String prefix = null;
						if(entryName.startsWith((rootPackageDir+ZIP_IN_MEMORY)) ){
							inMemory = true;
							prefix = rootPackageDir+ZIP_IN_MEMORY+File.separatorChar;
						}
						else if(entryName.startsWith((rootPackageDir+ZIP_DB)) ){
							inMemory = false;
							prefix = rootPackageDir+ZIP_DB+File.separatorChar;
						}
						else {
							throw new UtilsException("Entry ["+entryName+"] sconosciuta (Tipologia)");
						}
						
						if(entryName.equals((prefix+ZIP_LAST_MAX_REQUESTS)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemoryLastMaxRequests = convertToDatiEventoGenerico(eventoGenericoSerialized);
							}
							else {
								this.dbLastMaxRequests = convertDBToDatiEventoGenerico(eventoGenericoSerialized);
							}
						}
						
						else if(entryName.equals((prefix+ZIP_LAST_MAX_REQUESTS_WARNING_ONLY)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemoryLastMaxRequestsWarningOnly = convertToDatiEventoGenerico(eventoGenericoSerialized);
							}
							else {
								this.dbLastMaxRequestsWarningOnly = convertDBToDatiEventoGenerico(eventoGenericoSerialized);
							}
						}
						
						else if(entryName.equals((prefix+ZIP_LAST_PDD_CONGESTIONATA)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemoryLastPddCongestionata = convertToDatiEventoGenerico(eventoGenericoSerialized);
							}
							else {
								this.dbLastPddCongestionata = convertDBToDatiEventoGenerico(eventoGenericoSerialized);
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_POLICY_GLOBALE)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemoryLastPolicyGlobaliViolated.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.dbLastPolicyGlobaliViolated.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_POLICY_GLOBALE_WARNING_ONLY)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemoryLastPolicyGlobaliViolatedWarningOnly.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.dbLastPolicyGlobaliViolatedWarningOnly.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_POLICY_API)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemoryLastPolicyAPIViolated.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.dbLastPolicyAPIViolated.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_POLICY_API_WARNING_ONLY)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemoryLastPolicyAPIViolatedWarningOnly.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.dbLastPolicyAPIViolatedWarningOnly.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_EVENTO_TIMEOUT_CONNESSIONE)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemoryLastTimeoutConnessione.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.dbLastTimeoutConnessione.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_EVENTO_TIMEOUT_RICHIESTA)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemoryLastTimeoutRichiesta.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.dbLastTimeoutRichiesta.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_EVENTO_TIMEOUT_RISPOSTA)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemoryLastTimeoutRisposta.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.dbLastTimeoutRisposta.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else{
							throw new UtilsException("Entry ["+entryName+"] sconosciuta");
						}
						
					}
				}
				
			}
			
		}catch(Exception e){
			throw new UtilsException("["+entryName+"] "+e.getMessage(),e);
		}
		finally{
			try {
				if(f!=null) {
					java.nio.file.Files.delete(f.toPath());
				}
			}catch(Exception eClose) {
				// ignore
			}
			try{
				if(in!=null)
					in.close();
			}catch(Exception eClose){
				// close
			}
		}
		
	}
	
	private DatiEventoGenerico convertToDatiEventoGenerico(org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized){
		DatiEventoGenerico e = new DatiEventoGenerico();
		e.data = eventoGenericoSerialized.getData();
		e.datiConsumatiThread = eventoGenericoSerialized.isDatiConsumatiThread();
		e.descrizione = eventoGenericoSerialized.getDescrizione();
		return e;
	}
	private DatabaseDatiEventoGenerico convertDBToDatiEventoGenerico(org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized){
		DatabaseDatiEventoGenerico e = new DatabaseDatiEventoGenerico();
		e.data = eventoGenericoSerialized.getData();
		e.datiConsumatiThread = eventoGenericoSerialized.isDatiConsumatiThread();
		e.descrizione = eventoGenericoSerialized.getDescrizione();
		if(eventoGenericoSerialized.getCodiceEvento()!=null) {
			e.codiceEvento = CodiceEventoControlloTraffico.toEnumConstant(eventoGenericoSerialized.getCodiceEvento());
		}
		return e;
	}
}


class DatiEventoGenerico{
	
	Date data;
	String descrizione;
	String configurazione;
	boolean datiConsumatiThread = true;
	
	public DatiEventoGenerico readAndConsume(){
		DatiEventoGenerico d = new DatiEventoGenerico();
		if(this.data!=null)
			d.data = new Date(this.data.getTime());
		if(this.descrizione!=null)
			d.descrizione = this.descrizione + "";
		if(this.configurazione!=null)
			d.configurazione = this.configurazione + "";
		d.datiConsumatiThread = this.datiConsumatiThread;
		
		this.datiConsumatiThread = true;
		
		return d;
	}
}

class DatabaseDatiEventoGenerico extends DatiEventoGenerico{
	
	CodiceEventoControlloTraffico codiceEvento;
	
}


