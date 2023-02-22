/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import javax.activation.FileDataSource;

import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.constants.CodiceEventoControlloTraffico;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.core.eventi.dao.IEventoService;
import org.openspcoop2.core.eventi.utils.SeveritaConverter;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.eventi.GestoreEventi;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.utils.Utilities;
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
	private DatiEventoGenerico inMemory_lastMaxRequests = new DatiEventoGenerico();
	private DatiEventoGenerico inMemory_lastMaxRequests_warningOnly = new DatiEventoGenerico();
	private DatiEventoGenerico inMemory_lastPddCongestionata = new DatiEventoGenerico();
	private Map<String, DatiEventoGenerico> inMemory_lastPolicyGlobaliViolated = new HashMap<String, DatiEventoGenerico>();
	private Map<String, DatiEventoGenerico> inMemory_lastPolicyGlobaliViolated_warningOnly = new HashMap<String, DatiEventoGenerico>();
	private Map<String, DatiEventoGenerico> inMemory_lastPolicyAPIViolated = new HashMap<String, DatiEventoGenerico>();
	private Map<String, DatiEventoGenerico> inMemory_lastPolicyAPIViolated_warningOnly = new HashMap<String, DatiEventoGenerico>();
		
	// DB_EVENTO
	private DatabaseDatiEventoGenerico db_lastMaxRequests = new DatabaseDatiEventoGenerico();
	private DatabaseDatiEventoGenerico db_lastMaxRequests_warningOnly = new DatabaseDatiEventoGenerico();
	private DatabaseDatiEventoGenerico db_lastPddCongestionata = new DatabaseDatiEventoGenerico();
	private Map<String, DatabaseDatiEventoGenerico> db_lastPolicyGlobaliViolated = new HashMap<String, DatabaseDatiEventoGenerico>();
	private Map<String, DatabaseDatiEventoGenerico> db_lastPolicyGlobaliViolated_warningOnly = new HashMap<String, DatabaseDatiEventoGenerico>();
	private Map<String, DatabaseDatiEventoGenerico> db_lastPolicyAPIViolated = new HashMap<String, DatabaseDatiEventoGenerico>();
	private Map<String, DatabaseDatiEventoGenerico> db_lastPolicyAPIViolated_warningOnly = new HashMap<String, DatabaseDatiEventoGenerico>();
	
	
	public void log(CategoriaEventoControlloTraffico evento, Date date, String descrizione) throws Exception{
		log(evento, null, null, null, descrizione);
	}
	public void log(CategoriaEventoControlloTraffico evento, String idPolicy, String configurazione, Date date, String descrizione) throws Exception{
		
		if(evento==null){
			throw new Exception("Evento non definito");
		}
		
		switch (evento) {
		
		case LIMITE_GLOBALE_RICHIESTE_SIMULTANEE:
			//synchronized (this.semaphore) {
			lock.acquire("log_"+evento.name());
			try {
				if(this.inMemory_lastMaxRequests.datiConsumatiThread){
					if(date==null)
						this.inMemory_lastMaxRequests.data = DateManager.getDate();
					else
						this.inMemory_lastMaxRequests.data = date;
					this.inMemory_lastMaxRequests.descrizione = descrizione;
					this.inMemory_lastMaxRequests.configurazione = configurazione;
//					System.out.println("@@LOG AGGIORNO DATA LIMITE_RICHIESTE_SIMULTANEE_VIOLAZIONE ["+
//					newSimpleDateFormat().format(this.inMemory_lastMaxRequests.data)+"]");
					this.inMemory_lastMaxRequests.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case LIMITE_GLOBALE_RICHIESTE_SIMULTANEE_WARNING_ONLY:
			//synchronized (this.semaphore) {
			lock.acquire("log_"+evento.name());
			try {
				if(this.inMemory_lastMaxRequests_warningOnly.datiConsumatiThread){
					if(date==null)
						this.inMemory_lastMaxRequests_warningOnly.data = DateManager.getDate();
					else
						this.inMemory_lastMaxRequests_warningOnly.data = date;
					this.inMemory_lastMaxRequests_warningOnly.descrizione = descrizione;
					this.inMemory_lastMaxRequests_warningOnly.configurazione = configurazione;
//					System.out.println("@@LOG AGGIORNO DATA LIMITE_RICHIESTE_SIMULTANEE_VIOLAZIONE WARNING_ONLY ["+
//					newSimpleDateFormat().format(this.inMemory_lastMaxRequests_warningOnly.data)+"]");
					this.inMemory_lastMaxRequests_warningOnly.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case CONGESTIONE_PORTA_DOMINIO:
			//synchronized (this.semaphore) {
			lock.acquire("log_"+evento.name());
			try {
				if(this.inMemory_lastPddCongestionata.datiConsumatiThread){
					if(date==null)
						this.inMemory_lastPddCongestionata.data = DateManager.getDate();
					else
						this.inMemory_lastPddCongestionata.data = date;
					this.inMemory_lastPddCongestionata.descrizione = descrizione;
					this.inMemory_lastPddCongestionata.configurazione = configurazione;
//					System.out.println("@@LOG AGGIORNO DATA CONGESTIONE PDD ["+
//							newSimpleDateFormat().format(this.inMemory_lastPddCongestionata.data)+"]");
					this.inMemory_lastPddCongestionata.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case POLICY_GLOBALE:
			if(idPolicy==null){
				throw new Exception("IdPolicy non fornita");
			}
			//synchronized (this.semaphore) {
			lock.acquire("log_"+evento.name());
			try {
				if(this.inMemory_lastPolicyGlobaliViolated.containsKey(idPolicy)==false){
					this.inMemory_lastPolicyGlobaliViolated.put(idPolicy, new DatiEventoGenerico());
				}
				if(this.db_lastPolicyGlobaliViolated.containsKey(idPolicy)==false){
					this.db_lastPolicyGlobaliViolated.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemory_lastPolicyGlobaleViolated_policy = this.inMemory_lastPolicyGlobaliViolated.get(idPolicy);
				
				if(inMemory_lastPolicyGlobaleViolated_policy.datiConsumatiThread){
					if(date==null)
						inMemory_lastPolicyGlobaleViolated_policy.data = DateManager.getDate();
					else
						inMemory_lastPolicyGlobaleViolated_policy.data = date;
					inMemory_lastPolicyGlobaleViolated_policy.descrizione = descrizione;
					inMemory_lastPolicyGlobaleViolated_policy.configurazione = configurazione;
	//				System.out.println("@@LOG AGGIORNO DATA POLICY VIOLAZIONE ["+
	//						newSimpleDateFormat().format(inMemory_lastPolicyViolated_policy.data)+"]");
					inMemory_lastPolicyGlobaleViolated_policy.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case POLICY_GLOBALE_WARNING_ONLY:
			if(idPolicy==null){
				throw new Exception("IdPolicy non fornita");
			}
			//synchronized (this.semaphore) {
			lock.acquire("log_"+evento.name());
			try {
				if(this.inMemory_lastPolicyGlobaliViolated_warningOnly.containsKey(idPolicy)==false){
					this.inMemory_lastPolicyGlobaliViolated_warningOnly.put(idPolicy, new DatiEventoGenerico());
				}
				if(this.db_lastPolicyGlobaliViolated_warningOnly.containsKey(idPolicy)==false){
					this.db_lastPolicyGlobaliViolated_warningOnly.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemory_lastPolicyGlobaleViolated_warningOnly_policy = this.inMemory_lastPolicyGlobaliViolated_warningOnly.get(idPolicy);
				
				if(inMemory_lastPolicyGlobaleViolated_warningOnly_policy.datiConsumatiThread){
					if(date==null)
						inMemory_lastPolicyGlobaleViolated_warningOnly_policy.data = DateManager.getDate();
					else
						inMemory_lastPolicyGlobaleViolated_warningOnly_policy.data = date;
					inMemory_lastPolicyGlobaleViolated_warningOnly_policy.descrizione = descrizione;
					inMemory_lastPolicyGlobaleViolated_warningOnly_policy.configurazione = configurazione;
	//				System.out.println("@@LOG AGGIORNO DATA POLICY VIOLAZIONE WARNING_ONLY ["+
	//						newSimpleDateFormat().format(inMemory_lastPolicyViolated_warningOnly_policy.data)+"]");
					inMemory_lastPolicyGlobaleViolated_warningOnly_policy.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
			
		case POLICY_API:
			if(idPolicy==null){
				throw new Exception("IdPolicy non fornita");
			}
			//synchronized (this.semaphore) {
			lock.acquire("log_"+evento.name());
			try {
				if(this.inMemory_lastPolicyAPIViolated.containsKey(idPolicy)==false){
					this.inMemory_lastPolicyAPIViolated.put(idPolicy, new DatiEventoGenerico());
				}
				if(this.db_lastPolicyAPIViolated.containsKey(idPolicy)==false){
					this.db_lastPolicyAPIViolated.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemory_lastPolicyAPIViolated_policy = this.inMemory_lastPolicyAPIViolated.get(idPolicy);
				
				if(inMemory_lastPolicyAPIViolated_policy.datiConsumatiThread){
					if(date==null)
						inMemory_lastPolicyAPIViolated_policy.data = DateManager.getDate();
					else
						inMemory_lastPolicyAPIViolated_policy.data = date;
					inMemory_lastPolicyAPIViolated_policy.descrizione = descrizione;
					inMemory_lastPolicyAPIViolated_policy.configurazione = configurazione;
	//				System.out.println("@@LOG AGGIORNO DATA POLICY VIOLAZIONE ["+
	//						newSimpleDateFormat().format(inMemory_lastPolicyViolated_policy.data)+"]");
					inMemory_lastPolicyAPIViolated_policy.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			
		case POLICY_API_WARNING_ONLY:
			if(idPolicy==null){
				throw new Exception("IdPolicy non fornita");
			}
			//synchronized (this.semaphore) {
			lock.acquire("log_"+evento.name());
			try {
				if(this.inMemory_lastPolicyAPIViolated_warningOnly.containsKey(idPolicy)==false){
					this.inMemory_lastPolicyAPIViolated_warningOnly.put(idPolicy, new DatiEventoGenerico());
				}
				if(this.db_lastPolicyAPIViolated_warningOnly.containsKey(idPolicy)==false){
					this.db_lastPolicyAPIViolated_warningOnly.put(idPolicy, new DatabaseDatiEventoGenerico());
				}
				DatiEventoGenerico inMemory_lastPolicyAPIViolated_warningOnly_policy = this.inMemory_lastPolicyAPIViolated_warningOnly.get(idPolicy);
				
				if(inMemory_lastPolicyAPIViolated_warningOnly_policy.datiConsumatiThread){
					if(date==null)
						inMemory_lastPolicyAPIViolated_warningOnly_policy.data = DateManager.getDate();
					else
						inMemory_lastPolicyAPIViolated_warningOnly_policy.data = date;
					inMemory_lastPolicyAPIViolated_warningOnly_policy.descrizione = descrizione;
					inMemory_lastPolicyAPIViolated_warningOnly_policy.configurazione = configurazione;
	//				System.out.println("@@LOG AGGIORNO DATA POLICY VIOLAZIONE WARNING_ONLY ["+
	//						newSimpleDateFormat().format(inMemory_lastPolicyViolated_warningOnly_policy.data)+"]");
					inMemory_lastPolicyAPIViolated_warningOnly_policy.datiConsumatiThread=false;
				}
			}finally {
				lock.release("log_"+evento.name());
			}
			break;
			


		default:
			throw new Exception("Tipo di evento ["+evento.name()+"] non gestito con questo metodo");
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
	
	public Date process(Logger log, IEventoService eventoService, int secondi, Date lastInterval, Connection connection, boolean debug, boolean forceCheckPrimoAvvioConfig) throws Exception{
		
		SimpleDateFormat df = DateUtils.getSimpleDateFormatMs();
		
		logInfo(log,debug,"=================================================================================");
		
		logInfo(log,debug,"Analisi memoria per generazione eventi in corso [interval: "+df.format(lastInterval)+"] ...");
		
		// Raccolgo informazioni in memoria per rilasciare il lock
		
		// TH_*
		DatiEventoGenerico local_inMemory_lastMaxRequests = null;
		DatiEventoGenerico local_inMemory_lastMaxRequests_warningOnly = null;
		DatiEventoGenerico local_inMemory_lastPddCongestionata = null;
		Map<String, DatiEventoGenerico> local_inMemory_lastPolicyGlobaliViolated = new HashMap<String, DatiEventoGenerico>();
		Map<String, DatiEventoGenerico> local_inMemory_lastPolicyGlobaliViolated_warningOnly = new HashMap<String, DatiEventoGenerico>();	
		Map<String, DatiEventoGenerico> local_inMemory_lastPolicyAPIViolated = new HashMap<String, DatiEventoGenerico>();
		Map<String, DatiEventoGenerico> local_inMemory_lastPolicyAPIViolated_warningOnly = new HashMap<String, DatiEventoGenerico>();
		Date newInterval = null;
		//synchronized (this.semaphore) {
		lock.acquire("process");
		try {
			
			local_inMemory_lastMaxRequests = this.inMemory_lastMaxRequests.readAndConsume();
			local_inMemory_lastMaxRequests_warningOnly = this.inMemory_lastMaxRequests_warningOnly.readAndConsume();
			local_inMemory_lastPddCongestionata = this.inMemory_lastPddCongestionata.readAndConsume();
			
			if(this.inMemory_lastPolicyGlobaliViolated!=null && this.inMemory_lastPolicyGlobaliViolated.size()>0){
				for (String key : this.inMemory_lastPolicyGlobaliViolated.keySet()) {
					local_inMemory_lastPolicyGlobaliViolated.put(key, this.inMemory_lastPolicyGlobaliViolated.get(key).readAndConsume());
				}
			}
			if(this.inMemory_lastPolicyGlobaliViolated_warningOnly!=null && this.inMemory_lastPolicyGlobaliViolated_warningOnly.size()>0){
				for (String key : this.inMemory_lastPolicyGlobaliViolated_warningOnly.keySet()) {
					local_inMemory_lastPolicyGlobaliViolated_warningOnly.put(key, this.inMemory_lastPolicyGlobaliViolated_warningOnly.get(key).readAndConsume());
				}
			}
			
			if(this.inMemory_lastPolicyAPIViolated!=null && this.inMemory_lastPolicyAPIViolated.size()>0){
				for (String key : this.inMemory_lastPolicyAPIViolated.keySet()) {
					local_inMemory_lastPolicyAPIViolated.put(key, this.inMemory_lastPolicyAPIViolated.get(key).readAndConsume());
				}
			}
			if(this.inMemory_lastPolicyAPIViolated_warningOnly!=null && this.inMemory_lastPolicyAPIViolated_warningOnly.size()>0){
				for (String key : this.inMemory_lastPolicyAPIViolated_warningOnly.keySet()) {
					local_inMemory_lastPolicyAPIViolated_warningOnly.put(key, this.inMemory_lastPolicyAPIViolated_warningOnly.get(key).readAndConsume());
				}
			}

			newInterval = DateManager.getDate();
		}finally {
			lock.release("process");
		}

		
		// Gli eventi db_* sono acceduti solo dal thread non serve un synchronized
		
		// DB_*
		DatabaseDatiEventoGenerico local_db_lastMaxRequests = null;
		DatabaseDatiEventoGenerico local_db_lastMaxRequests_warningOnly = null;
		DatabaseDatiEventoGenerico local_db_lastPddCongestionata = null;
		Map<String, DatabaseDatiEventoGenerico> local_db_lastPolicyGlobaliViolated = new HashMap<String, DatabaseDatiEventoGenerico>();
		Map<String, DatabaseDatiEventoGenerico> local_db_lastPolicyGlobaliViolated_warningOnly = new HashMap<String, DatabaseDatiEventoGenerico>();	
		Map<String, DatabaseDatiEventoGenerico> local_db_lastPolicyAPIViolated = new HashMap<String, DatabaseDatiEventoGenerico>();
		Map<String, DatabaseDatiEventoGenerico> local_db_lastPolicyAPIViolated_warningOnly = new HashMap<String, DatabaseDatiEventoGenerico>();	
		local_db_lastMaxRequests = this.db_lastMaxRequests;
		local_db_lastMaxRequests_warningOnly = this.db_lastMaxRequests_warningOnly;
		local_db_lastPddCongestionata = this.db_lastPddCongestionata;
		local_db_lastPolicyGlobaliViolated = this.db_lastPolicyGlobaliViolated;
		local_db_lastPolicyGlobaliViolated_warningOnly = this.db_lastPolicyGlobaliViolated_warningOnly;
		local_db_lastPolicyAPIViolated = this.db_lastPolicyAPIViolated;
		local_db_lastPolicyAPIViolated_warningOnly = this.db_lastPolicyAPIViolated_warningOnly;

		
		// Procedo ad effettuare l'elaborazione per emettere degli eventi
		
		logDebug(log,debug,"1. Analisi violazioni numero massimo richieste simultanee ...");
		processSingleEvent(log, 
				local_inMemory_lastMaxRequests, 
				local_db_lastMaxRequests, 
				this.db_lastMaxRequests, 
				TipoEvento.CONTROLLO_TRAFFICO_NUMERO_MASSIMO_RICHIESTE_SIMULTANEE,
				CodiceEventoControlloTraffico.VIOLAZIONE, 
				CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
				lastInterval, connection, this.gestoreEventi,
				null, debug);
		logDebug(log,debug,"1. Analisi violazioni numero massimo richieste simultanee terminata");
		
		logDebug(log,debug,"2. Analisi violazioni numero massimo richieste simultanee (warning-only) ...");
		processSingleEvent(log, 
				local_inMemory_lastMaxRequests_warningOnly, 
				local_db_lastMaxRequests_warningOnly, 
				this.db_lastMaxRequests_warningOnly, 
				TipoEvento.CONTROLLO_TRAFFICO_NUMERO_MASSIMO_RICHIESTE_SIMULTANEE,
				CodiceEventoControlloTraffico.VIOLAZIONE_WARNING_ONLY, 
				CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA_WARNING_ONLY, 
				lastInterval, connection, this.gestoreEventi,
				null, debug);
		logDebug(log,debug,"2. Analisi violazioni numero massimo richieste simultanee (warning-only) terminata");
		
		logDebug(log,debug,"3. Analisi controllo della congestione ...");
		processSingleEvent(log, 
				local_inMemory_lastPddCongestionata, 
				local_db_lastPddCongestionata, 
				this.db_lastPddCongestionata, 
				TipoEvento.CONTROLLO_TRAFFICO_SOGLIA_CONGESTIONE,
				CodiceEventoControlloTraffico.VIOLAZIONE, 
				CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
				lastInterval, connection, this.gestoreEventi,
				null, debug);
		logDebug(log,debug,"3. Analisi controllo della congestione terminata");
		
		logDebug(log,debug,"4. Analisi policy globali violate (size:"+local_inMemory_lastPolicyGlobaliViolated.size()+") ...");
		if(!local_inMemory_lastPolicyGlobaliViolated.isEmpty()) {
			for (String idPolicy : local_inMemory_lastPolicyGlobaliViolated.keySet()) {
				processSingleEvent(log, 
						local_inMemory_lastPolicyGlobaliViolated.get(idPolicy), 
						local_db_lastPolicyGlobaliViolated.get(idPolicy), 
						this.db_lastPolicyGlobaliViolated.get(idPolicy), 
						TipoEvento.RATE_LIMITING_POLICY_GLOBALE,
						CodiceEventoControlloTraffico.VIOLAZIONE, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);
			}
		}
		logDebug(log,debug,"4. Analisi policy globali violate (size:"+local_inMemory_lastPolicyGlobaliViolated.size()+") terminata");
		
		logDebug(log,debug,"5. Analisi policy globali violate (size:"+local_inMemory_lastPolicyGlobaliViolated_warningOnly.size()+") (warning-only) ...");
		if(!local_inMemory_lastPolicyGlobaliViolated_warningOnly.isEmpty()) {
			for (String idPolicy : local_inMemory_lastPolicyGlobaliViolated_warningOnly.keySet()) {
				processSingleEvent(log, 
						local_inMemory_lastPolicyGlobaliViolated_warningOnly.get(idPolicy), 
						local_db_lastPolicyGlobaliViolated_warningOnly.get(idPolicy), 
						this.db_lastPolicyGlobaliViolated_warningOnly.get(idPolicy), 
						TipoEvento.RATE_LIMITING_POLICY_GLOBALE,
						CodiceEventoControlloTraffico.VIOLAZIONE_WARNING_ONLY, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA_WARNING_ONLY, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);
			}
		}
		logDebug(log,debug,"5. Analisi policy globali violate (size:"+local_inMemory_lastPolicyGlobaliViolated_warningOnly.size()+") (warning-only) terminata");
		
		logDebug(log,debug,"6. Analisi policy API violate (size:"+local_inMemory_lastPolicyAPIViolated.size()+") ...");
		if(!local_inMemory_lastPolicyAPIViolated.isEmpty()) {
			for (String idPolicy : local_inMemory_lastPolicyAPIViolated.keySet()) {
				processSingleEvent(log, 
						local_inMemory_lastPolicyAPIViolated.get(idPolicy), 
						local_db_lastPolicyAPIViolated.get(idPolicy), 
						this.db_lastPolicyAPIViolated.get(idPolicy), 
						TipoEvento.RATE_LIMITING_POLICY_API,
						CodiceEventoControlloTraffico.VIOLAZIONE, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);	
			}
		}
		logDebug(log,debug,"6. Analisi policy API violate (size:"+local_inMemory_lastPolicyAPIViolated.size()+") terminata");
		
		logDebug(log,debug,"7. Analisi policy API violate (size:"+local_inMemory_lastPolicyAPIViolated_warningOnly.size()+") (warning-only) ...");
		if(!local_inMemory_lastPolicyAPIViolated_warningOnly.isEmpty()) {
			for (String idPolicy : local_inMemory_lastPolicyAPIViolated_warningOnly.keySet()) {
				processSingleEvent(log, 
						local_inMemory_lastPolicyAPIViolated_warningOnly.get(idPolicy), 
						local_db_lastPolicyAPIViolated_warningOnly.get(idPolicy), 
						this.db_lastPolicyAPIViolated_warningOnly.get(idPolicy), 
						TipoEvento.RATE_LIMITING_POLICY_API,
						CodiceEventoControlloTraffico.VIOLAZIONE_WARNING_ONLY, 
						CodiceEventoControlloTraffico.VIOLAZIONE_RISOLTA_WARNING_ONLY, 
						lastInterval, connection, this.gestoreEventi,
						idPolicy,debug);	
			}
		}
		logDebug(log,debug,"7. Analisi policy API violate (size:"+local_inMemory_lastPolicyAPIViolated_warningOnly.size()+") (warning-only) terminata");
				
		logInfo(log,debug,"Analisi memoria per generazione eventi terminata [next-interval: "+df.format(newInterval)+
				"] [Prossimo Controllo previsto tra "+secondi+" secondi: "+df.format(new Date(DateManager.getTimeMillis()+(secondi*1000)))+"]");
		
		return newInterval; // da usare per il prossimo intervallo
	}
	
	
	private static void processSingleEvent(Logger log, DatiEventoGenerico local_inMemory,DatabaseDatiEventoGenerico local_db,
			DatabaseDatiEventoGenerico this_db,
			TipoEvento tipoEvento,
			CodiceEventoControlloTraffico eventoViolazione, CodiceEventoControlloTraffico eventoViolazioneRisolta, 
			Date lastInterval, Connection connection, GestoreEventi gestoreEventi,
			String idPolicy, boolean debug) throws Exception{
		
		// Gestione violazione maxThreads
		
		boolean esiste_TH_ultimo_intervallo = false;
		if(local_inMemory!=null && 
				local_inMemory.data!=null && 
				local_inMemory.data.after(lastInterval)){
			esiste_TH_ultimo_intervallo = true;
		}
		
		if(local_db!=null && 
				local_db.data!=null){
			logDebug(log,debug,"\tTH_ultimoIntervallo: "+esiste_TH_ultimo_intervallo+
					" DB_last:"+(local_db!=null)+
					" DB_last.codiceEvento:"+local_db.codiceEvento.name());
		}
		else{
			logDebug(log,debug,"\tTH_ultimoIntervallo: "+esiste_TH_ultimo_intervallo);
		}
	
		if(esiste_TH_ultimo_intervallo){
			if(local_db!=null && 
					local_db.data!=null){
				if(eventoViolazioneRisolta.equals(local_db.codiceEvento)){
					// sono ritornato dentro una violazione di stato
					CodiceEventoControlloTraffico codice = eventoViolazione;
					Evento evento = buildEvento(tipoEvento,codice, idPolicy,
							local_inMemory.descrizione,
							local_inMemory.configurazione,
							local_inMemory.data); // uso come data dell'evento la data in cui e' accaduta la segnalazione
							//DateManager.getDate());
					logEvento(gestoreEventi, connection, evento, log, debug);
					//synchronized (this.semaphore) { // Gli eventi db_* sono acceduti solo dal thread non serve un synchronized
					this_db.data = evento.getOraRegistrazione();
					this_db.codiceEvento = codice;
					//}
					if(idPolicy!=null)
						logDebug(log,debug,"\tEmetto Evento tipo["+tipoEvento+"] codice["+codice+"_"+idPolicy+"]");
					else
						logDebug(log,debug,"\tEmetto Evento tipo["+tipoEvento+"] codice["+codice+"]");
				}
			}
			else{
				// prima volta che succede il problema, emetto evento di violazione
				CodiceEventoControlloTraffico codice = eventoViolazione;
				Evento evento = buildEvento(tipoEvento,codice, idPolicy,
						local_inMemory.descrizione,
						local_inMemory.configurazione,
						local_inMemory.data); // uso come data dell'evento la data in cui e' accaduta la segnalazione
						//DateManager.getDate());
				logEvento(gestoreEventi, connection, evento, log, debug);
				// synchronized (this.semaphore) { // Gli eventi db_* sono acceduti solo dal thread non serve un synchronized
				this_db.data = evento.getOraRegistrazione();
				this_db.codiceEvento = codice;
				if(idPolicy!=null)
					logDebug(log,debug,"\tEmetto Evento tipo["+tipoEvento+"] codice["+codice+"_"+idPolicy+"]");
				else
					logDebug(log,debug,"\tEmetto Evento tipo["+tipoEvento+"] codice["+codice+"]");
				// }
			}
		}
		else{
			if(local_db!=null && 
					local_db.data!=null){
				if(eventoViolazione.equals(local_db.codiceEvento)){
					// emetto evento che non risulta piu' violato.
					CodiceEventoControlloTraffico codice = eventoViolazioneRisolta;
					Evento evento = buildEvento(tipoEvento,codice, idPolicy,
							local_db.descrizione, 
							local_inMemory!=null && local_inMemory.configurazione!=null ? local_inMemory.configurazione : local_db.configurazione, // importante per far arrivare la configurazione usata nella gestione delle policy
							DateManager.getDate());
					logEvento(gestoreEventi, connection, evento, log, debug);
					// synchronized (this.semaphore) { // Gli eventi db_* sono acceduti solo dal thread non serve un synchronized
					this_db.data = evento.getOraRegistrazione();
					this_db.codiceEvento = codice;
					if(idPolicy!=null)
						logDebug(log,debug,"\tEmetto Evento tipo["+tipoEvento+"] codice["+codice+"_"+idPolicy+"]");
					else
						logDebug(log,debug,"\tEmetto Evento tipo["+tipoEvento+"] codice["+codice+"]");
					//}
				}
			}
		}

	}
	
	private static Evento buildEvento(TipoEvento tipoEvento, CodiceEventoControlloTraffico codice, String idPolicy, String descrizione, String configurazione, Date data) throws Exception{
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
		default:
			// altri casi non previsti per questo notificatore eventi
			break;
		}
		
		evento.setClusterId(OpenSPCoop2Properties.getInstance().getClusterId(false));
		return evento;
	}
	
	private static void logEvento(GestoreEventi gestoreEventi, Connection connection, Evento evento, Logger log, boolean debug) throws Exception {
		
		// Fix evento per passarlo al notifier
		String configurazione = evento.getConfigurazione();
		if(PolicyUtilities.isConfigurazioneEventoPerPolicy(configurazione)) {
			evento.setConfigurazione(null); // serve solo per passarlo al notifier
		}
		
		gestoreEventi.log(evento, connection);
		
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
					default:
						// altri casi non previsti per questo notificatore eventi
						break;
					}
				}
			}
			
		}
		
	}

	
	

	//---- Per salvare

	private final static String ZIP_IN_MEMORY = "memory";
	private final static String ZIP_DB = "db";
	private final static String ZIP_LAST_MAX_REQUESTS = "lastMaxRequests.xml";
	private final static String ZIP_LAST_MAX_REQUESTS_WARNING_ONLY = "lastMaxRequests_warningOnly.xml";
	private final static String ZIP_LAST_PDD_CONGESTIONATA = "lastPddCongestionata.xml";
	private final static String ZIP_POLICY_GLOBALE = "policyGlobale";
	private final static String ZIP_POLICY_GLOBALE_WARNING_ONLY = "policyGlobale_warningOnly";
	private final static String ZIP_POLICY_API = "policyAPI";
	private final static String ZIP_POLICY_API_WARNING_ONLY = "policyAPI_warningOnly";

	
	public void serialize(File file) throws Exception{
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file, false); // se già esiste lo sovrascrive
			this.serialize(out);
		}finally {
			try {
				if(out!=null) {
					out.flush();
				}
			}catch(Exception e) {
				// ignore
			}
			try {
				if(out!=null) {
					out.close();
				}
			}catch(Exception e) {
				// close
			}
		}
	}
	
	@Deprecated
	// Il meccanismo non funzionava, non si riusciva a riottenere uno stato corretto
	public void serialize(OutputStream out) throws Exception{
			
		ZipOutputStream zipOut = null;
		try{
			zipOut = new ZipOutputStream(out);

			String rootPackageDir = "";
			// Il codice dopo fissa il problema di inserire una directory nel package.
			// Commentare la riga sotto per ripristinare il vecchio comportamento.
			rootPackageDir = Costanti.OPENSPCOOP2_ARCHIVE_ROOT_DIR+File.separatorChar;
			
			
			// String inMemory Path
			String inMemoryDir = rootPackageDir + ZIP_IN_MEMORY + File.separatorChar;
			
			if(this.inMemory_lastMaxRequests!=null) {
				zipOut.putNextEntry(new ZipEntry(inMemoryDir+ZIP_LAST_MAX_REQUESTS));
				convertToDatiEventoGenericoSerializabled(this.inMemory_lastMaxRequests, "inMemory_lastMaxRequests").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.inMemory_lastMaxRequests_warningOnly!=null) {
				zipOut.putNextEntry(new ZipEntry(inMemoryDir+ZIP_LAST_MAX_REQUESTS_WARNING_ONLY));
				convertToDatiEventoGenericoSerializabled(this.inMemory_lastMaxRequests_warningOnly, "inMemory_lastMaxRequests_warningOnly").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.inMemory_lastPddCongestionata!=null) {
				zipOut.putNextEntry(new ZipEntry(inMemoryDir+ZIP_LAST_PDD_CONGESTIONATA));
				convertToDatiEventoGenericoSerializabled(this.inMemory_lastPddCongestionata, "inMemory_lastPddCongestionata").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.inMemory_lastPolicyGlobaliViolated!=null && this.inMemory_lastPolicyGlobaliViolated.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_POLICY_GLOBALE + File.separatorChar;
				for (String idPolicy : this.inMemory_lastPolicyGlobaliViolated.keySet()) {
					DatiEventoGenerico evento = this.inMemory_lastPolicyGlobaliViolated.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_POLICY_GLOBALE+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.inMemory_lastPolicyGlobaliViolated_warningOnly!=null && this.inMemory_lastPolicyGlobaliViolated_warningOnly.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_POLICY_GLOBALE_WARNING_ONLY + File.separatorChar;
				for (String idPolicy : this.inMemory_lastPolicyGlobaliViolated_warningOnly.keySet()) {
					DatiEventoGenerico evento = this.inMemory_lastPolicyGlobaliViolated_warningOnly.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_POLICY_GLOBALE_WARNING_ONLY+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.inMemory_lastPolicyAPIViolated!=null && this.inMemory_lastPolicyAPIViolated.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_POLICY_API + File.separatorChar;
				for (String idPolicy : this.inMemory_lastPolicyAPIViolated.keySet()) {
					DatiEventoGenerico evento = this.inMemory_lastPolicyAPIViolated.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_POLICY_API+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.inMemory_lastPolicyAPIViolated_warningOnly!=null && this.inMemory_lastPolicyAPIViolated_warningOnly.size()>0) {
				int index = 1;
				String inMemoryPolicyDir = inMemoryDir + ZIP_POLICY_API_WARNING_ONLY + File.separatorChar;
				for (String idPolicy : this.inMemory_lastPolicyAPIViolated_warningOnly.keySet()) {
					DatiEventoGenerico evento = this.inMemory_lastPolicyAPIViolated_warningOnly.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(inMemoryPolicyDir+ZIP_POLICY_API_WARNING_ONLY+"_"+index+".xml"));
					convertToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			
			// String db Path
			String dbDir = rootPackageDir + ZIP_DB + File.separatorChar;
			
			if(this.db_lastMaxRequests!=null) {
				zipOut.putNextEntry(new ZipEntry(dbDir+ZIP_LAST_MAX_REQUESTS));
				convertDBToDatiEventoGenericoSerializabled(this.db_lastMaxRequests, "db_lastMaxRequests").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.db_lastMaxRequests_warningOnly!=null) {
				zipOut.putNextEntry(new ZipEntry(dbDir+ZIP_LAST_MAX_REQUESTS_WARNING_ONLY));
				convertDBToDatiEventoGenericoSerializabled(this.db_lastMaxRequests_warningOnly, "db_lastMaxRequests_warningOnly").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.db_lastPddCongestionata!=null) {
				zipOut.putNextEntry(new ZipEntry(dbDir+ZIP_LAST_PDD_CONGESTIONATA));
				convertDBToDatiEventoGenericoSerializabled(this.db_lastPddCongestionata, "db_lastPddCongestionata").
					writeTo(zipOut, WriteToSerializerType.XML_JAXB);
			}
			
			if(this.db_lastPolicyGlobaliViolated!=null && this.db_lastPolicyGlobaliViolated.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_POLICY_GLOBALE + File.separatorChar;
				for (String idPolicy : this.db_lastPolicyGlobaliViolated.keySet()) {
					DatabaseDatiEventoGenerico evento = this.db_lastPolicyGlobaliViolated.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_POLICY_GLOBALE+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.db_lastPolicyGlobaliViolated_warningOnly!=null && this.db_lastPolicyGlobaliViolated_warningOnly.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_POLICY_GLOBALE_WARNING_ONLY + File.separatorChar;
				for (String idPolicy : this.db_lastPolicyGlobaliViolated_warningOnly.keySet()) {
					DatabaseDatiEventoGenerico evento = this.db_lastPolicyGlobaliViolated_warningOnly.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_POLICY_GLOBALE_WARNING_ONLY+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.db_lastPolicyAPIViolated!=null && this.db_lastPolicyAPIViolated.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_POLICY_API + File.separatorChar;
				for (String idPolicy : this.db_lastPolicyAPIViolated.keySet()) {
					DatabaseDatiEventoGenerico evento = this.db_lastPolicyAPIViolated.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_POLICY_API+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
			
			if(this.db_lastPolicyAPIViolated_warningOnly!=null && this.db_lastPolicyAPIViolated_warningOnly.size()>0) {
				int index = 1;
				String dbPolicyDir = dbDir + ZIP_POLICY_API_WARNING_ONLY + File.separatorChar;
				for (String idPolicy : this.db_lastPolicyAPIViolated_warningOnly.keySet()) {
					DatabaseDatiEventoGenerico evento = this.db_lastPolicyAPIViolated_warningOnly.get(idPolicy);
					zipOut.putNextEntry(new ZipEntry(dbPolicyDir+ZIP_POLICY_API_WARNING_ONLY+"_"+index+".xml"));
					convertDBToDatiEventoGenericoSerializabled(evento, idPolicy).
						writeTo(zipOut, WriteToSerializerType.XML_JAXB);
					index++;
				}
			}
		
			
			zipOut.flush();

		}catch(Exception e){
			throw new Exception(e.getMessage(),e);
		}finally{
			try{
				if(zipOut!=null)
					zipOut.close();
			}catch(Exception eClose){
				// close
			}
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
	// Il meccanismo non funzionava, non si riusciva a riottenere uno stato corretto
	public void initialize(InputStream in) throws Exception{
			
		if(in==null){
			return;
		}
		
		File f = null;
		ZipFile zipFile = null;
		String entryName = null;
		try{
			
			// Leggo InputStream
			byte [] bytesIn = Utilities.getAsByteArray(in);
			in.close();
			in = null;
			if(bytesIn==null || bytesIn.length<=0){
				return;
			}
			f = File.createTempFile("controlloTraffico", ".tmp");
			FileSystemUtilities.writeFile(f, bytesIn);
			
			// Leggo Struttura ZIP
			zipFile = new ZipFile(f);
			
				String rootPackageDir = Costanti.OPENSPCOOP2_ARCHIVE_ROOT_DIR+File.separatorChar;
			
			String rootDir = null;
			
			org.openspcoop2.core.eventi.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.eventi.utils.serializer.JaxbDeserializer();
			
			Iterator<ZipEntry> it = ZipUtilities.entries(zipFile, true);
			while (it.hasNext()) {
				ZipEntry zipEntry = (ZipEntry) it.next();
				entryName = ZipUtilities.operativeSystemConversion(zipEntry.getName());
				
				//System.out.println("FILE NAME:  "+entryName);
				//System.out.println("SIZE:  "+entry.getSize());

				// Il codice dopo fissa il problema di inserire una directory nel package.
				// Commentare la riga sotto per ripristinare il vecchio comportamento.
				if(rootDir==null){
					// Calcolo ROOT DIR
					rootDir=ZipUtilities.getRootDir(entryName);
				}
				
				if(zipEntry.isDirectory()) {
					continue; // directory
				}
				else {
					FileDataSource fds = new FileDataSource(entryName);
					String nome = fds.getName();
					String tipo = nome.substring(nome.lastIndexOf(".")+1,nome.length()); 
					tipo = tipo.toUpperCase();
					//System.out.println("VERIFICARE NAME["+nome+"] TIPO["+tipo+"]");
					
					InputStream inputStream = zipFile.getInputStream(zipEntry);
					byte[]content = Utilities.getAsByteArray(inputStream);
					
					try{
						
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
							throw new Exception("Entry ["+entryName+"] sconosciuta (Tipologia)");
						}
						
						if(entryName.equals((prefix+ZIP_LAST_MAX_REQUESTS)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemory_lastMaxRequests = convertToDatiEventoGenerico(eventoGenericoSerialized);
							}
							else {
								this.db_lastMaxRequests = convertDBToDatiEventoGenerico(eventoGenericoSerialized);
							}
						}
						
						else if(entryName.equals((prefix+ZIP_LAST_MAX_REQUESTS_WARNING_ONLY)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemory_lastMaxRequests_warningOnly = convertToDatiEventoGenerico(eventoGenericoSerialized);
							}
							else {
								this.db_lastMaxRequests_warningOnly = convertDBToDatiEventoGenerico(eventoGenericoSerialized);
							}
						}
						
						else if(entryName.equals((prefix+ZIP_LAST_PDD_CONGESTIONATA)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemory_lastPddCongestionata = convertToDatiEventoGenerico(eventoGenericoSerialized);
							}
							else {
								this.db_lastPddCongestionata = convertDBToDatiEventoGenerico(eventoGenericoSerialized);
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_POLICY_GLOBALE)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemory_lastPolicyGlobaliViolated.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.db_lastPolicyGlobaliViolated.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_POLICY_GLOBALE_WARNING_ONLY)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemory_lastPolicyGlobaliViolated_warningOnly.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.db_lastPolicyGlobaliViolated_warningOnly.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_POLICY_API)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemory_lastPolicyAPIViolated.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.db_lastPolicyAPIViolated.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else if(entryName.startsWith((prefix+ZIP_POLICY_API_WARNING_ONLY)) ){
							org.openspcoop2.core.eventi.DatiEventoGenerico eventoGenericoSerialized = 
									deserializer.readDatiEventoGenerico(content);
							if(inMemory) {
								this.inMemory_lastPolicyAPIViolated_warningOnly.put(eventoGenericoSerialized.getIdEvento(), convertToDatiEventoGenerico(eventoGenericoSerialized));
							}
							else {
								this.db_lastPolicyAPIViolated_warningOnly.put(eventoGenericoSerialized.getIdEvento(), convertDBToDatiEventoGenerico(eventoGenericoSerialized));
							}
						}
						
						else{
							throw new Exception("Entry ["+entryName+"] sconosciuta");
						}
						
					}finally{
						try{
							if(inputStream!=null){
								inputStream.close();
							}
						}catch(Exception eClose){
							// close
						}
					}
				}
				
			}
			
		}catch(Exception e){
			throw new Exception("["+entryName+"] "+e.getMessage(),e);
		}
		finally{
			try{
				if(zipFile!=null)
					zipFile.close();
			}catch(Exception eClose){
				// close
			}
			try{
				if(f!=null) {
					if(!f.delete()) {
						// ignore
					}
				}
			}catch(Exception eClose){
				// close
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
			d.descrizione = new String(this.descrizione);
		if(this.configurazione!=null)
			d.configurazione = new String(this.configurazione);
		d.datiConsumatiThread = this.datiConsumatiThread;
		
		this.datiConsumatiThread = true;
		
		return d;
	}
}

class DatabaseDatiEventoGenerico extends DatiEventoGenerico{
	
	CodiceEventoControlloTraffico codiceEvento;
	
}


