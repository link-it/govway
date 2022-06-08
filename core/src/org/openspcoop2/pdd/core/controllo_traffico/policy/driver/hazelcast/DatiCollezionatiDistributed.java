package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;

/**
 * Scelgo questa implementazione, dove la versione distribuita eredita da DatiCollezionati e replica
 * i dati da distribuitre.
 * In questo modo, le varie PolicyGroupByActiveThreads* continueranno a restituire un oggetto di tipo DatiCollezionati. Con dati che sono presenti
 * nella ram del nodo locale, evitando così che il PolicyVerifier o altre classi che utilizzano i DatiCollezionatiDistributed vadano 
 * a fare richieste remote, ottenendo valori che non sono quelli che ci si aspettava dopo la richiesta, perchè aggiornati eventualmente
 * da altre richieste nel nodo e nel cluster.
 * Si risparmia anche banda di rete e latenza.
 * 
 * TODO: Provare la versione con le getAndIncrementAsync, devo però fare l'override dei getPolicyDate ecc..
 * facendoli andare sul contatore anzichè sulla copia
 * @author Francesco Scarlato
 *
 */
public class DatiCollezionatiDistributed extends DatiCollezionati {

	private static final long serialVersionUID = 1L;
	
	// TODO: Rimuoverlo dall'oggetto, non serve, serve solo nel costruttore
	private final HazelcastInstance hazelcast;
	
	// data di registrazione/aggiornamento policy
	protected final IAtomicLong updatePolicyDate;
	
	protected final IAtomicLong activeRequestCounter; // tiene traccia del numero di richieste attive sempre. Utile in jmx
	
	protected final IAtomicLong policyDate;
	protected final IAtomicLong policyRequestCounter;
	protected final IAtomicLong policyCounter;						 // utilizzato per tempi o banda
	protected final IAtomicLong policyDenyRequestCounter;
	
	protected final IAtomicLong policyDegradoPrestazionaleDate;
	protected final IAtomicLong policyDegradoPrestazionaleRequestCounter;
	protected final IAtomicLong policyDegradoPrestazionaleCounter;
	
	
	public DatiCollezionatiDistributed(Date updatePolicyDate, HazelcastInstance hazelcast, String uniqueMapId) {
		super(updatePolicyDate);
		
		this.hazelcast = hazelcast;
		
		this.updatePolicyDate = this.hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-updatePolicyDate");
		this.activeRequestCounter = this.hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-activeRequestCounter");
		
		this.policyDate = this.hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-policyDate");
		this.policyRequestCounter = this.hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-policyRequestCounter");
		this.policyCounter = this.hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-policyCounter");
		this.policyDenyRequestCounter = this.hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-policyDenyRequestCounter");
		
		this.policyDegradoPrestazionaleDate = this.hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-policyDegradoPrestazionaleDate");
		this.policyDegradoPrestazionaleRequestCounter = this.hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-policyDegradoPrestazionaleRequestCounter");
		this.policyDegradoPrestazionaleCounter = this.hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-policyDegradoPrestazionaleCounter");
		
	}
	
	public DatiCollezionatiDistributed(DatiCollezionati dati, HazelcastInstance hazelcast, String uniqueMapId) {
		this(dati.getUpdatePolicyDate(), hazelcast, uniqueMapId);
		
		// Inizializzo il padre con i valori in RAM
		dati.clone_impl(this);
		
		if (this.updatePolicyDate.get() == 0) {
			this.updatePolicyDate.set(dati.getUpdatePolicyDate().getTime());
		}
		
		// Se non ho la policyDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (dati.getPolicyDate() != null) {
			
			if (this.policyDate.get() == 0) {

				// Quando clono un DatiCollezionati e lo metto dentro una versione distribuita, Devo stare attento a non resettare
				// i contatori ogni volta perchè questa inizializzazione può essere fatta contemporaneamente e in momenti diversi
				// da più nodi. Se la policyDate è settata, assumo che l'oggetto distribuito sia stato già inizializzato e non lo resetto
				
				if (this.policyDate.compareAndSet(0, dati.getPolicyDate().getTime())) {
					
					if (dati.getPolicyRequestCounter() != null) {
						this.policyRequestCounter.set(dati.getPolicyRequestCounter());
					}
					
					if (dati.getPolicyCounter() != null) {
						this.policyCounter.set(dati.getPolicyCounter());
					}
					
					if (dati.getPolicyDenyRequestCounter() != null) {
						this.policyDenyRequestCounter.set(dati.getPolicyDenyRequestCounter());
					}
				}
			}
		}
		
		
		// Se non ho la policyDegradoPrestazionaleDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (dati.getPolicyDegradoPrestazionaleDate() != null) {
			
			if (this.policyDegradoPrestazionaleDate.get() == 0) {
				// Imposto i contatori distribuiti solo se nel frattempo non l'ha fatto un altro thread del cluster.
				if (this.policyDegradoPrestazionaleDate.compareAndSet(0, super.getPolicyDegradoPrestazionaleDate().getTime())) {
					
					if (dati.getPolicyDegradoPrestazionaleRequestCounter() != null) {
						this.policyDegradoPrestazionaleRequestCounter.set(dati.getPolicyDegradoPrestazionaleRequestCounter());
					}
					
					if (dati.getPolicyDegradoPrestazionaleCounter() != null) {
						this.policyDegradoPrestazionaleCounter.set(dati.getPolicyDegradoPrestazionaleCounter());
					}
				}
			}
		}
		
	}
	

	@Override
	protected void initDatiIniziali(ActivePolicy activePolicy){
		super.initDatiIniziali(activePolicy);
		
		// L'unico contatore distribuito inizializzato nel padre è questo
		this.activeRequestCounter.set(super.getActiveRequestCounter());
	}
	

	@Override
	protected void resetPolicyCounterForDate(Date d) {
		super.resetPolicyCounterForDate(d);
		
		this.policyDate.set(d.getTime());
		
		if(this.policyRealtime!=null && this.policyRealtime){
			this.policyRequestCounter.set(0l);
			this.policyDenyRequestCounter.set(0l);
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.policyCounter.set(0l);
			}
		}
	}
	
	
	@Override
	protected void resetPolicyCounterForDateDegradoPrestazionale(Date d) {
		super.resetPolicyCounterForDateDegradoPrestazionale(d);
		
		this.policyDegradoPrestazionaleDate.set(d.getTime());
		
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.policyDegradoPrestazionaleRequestCounter.set(0l);
			this.policyDegradoPrestazionaleCounter.set(0l);
		}
	}
	
	
	@Override
	public void registerStartRequest(Logger log, ActivePolicy activePolicy){
		
		if(super.getCreationDate() ==null){
			this.initDatiIniziali(activePolicy);
		}
		
		super.setActiveRequestCounter(this.activeRequestCounter.incrementAndGet());
		
		if(this.getPolicyDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDateWindowInterval())==false){
			
			this.checkPolicyCounterForDate(log,activePolicy);
		}

		if(this.getPolicyDegradoPrestazionaleDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDegradoPrestazionaleDateWindowInterval())==false){
			
			this.checkPolicyCounterForDateDegradoPrestazionale(log,activePolicy);
			
			//if(this.policyDegradoPrestazionaleRealtime){
			// Essendo il degrado un tempo medio anche il numero di richieste lo devo incrementare quando aggiungo anche la latenza, 
			// senno poi la divisione (per l'avg) rispetto al numero di richieste è falsata
			// poiche' tengo conto anche delle richieste in corso, nonostante per queste non disponga ancora nella latenza
			// }
		}
		
	}
	
	@Override
	public void registerEndRequest(Logger log, ActivePolicy activePolicy, MisurazioniTransazione dati){
		
		if(super.getCreationDate()==null){
			return; // non inizializzato?
		}
		
		super.setActiveRequestCounter(this.activeRequestCounter.decrementAndGet());
				
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			
			long latenza = 0;
			
			int [] esitiValidi = null;
			
			if(TipoPdD.DELEGATA.equals(dati.getTipoPdD())){
				esitiValidi = activePolicy.getConfigurazioneControlloTraffico().getCalcoloLatenzaPortaDelegataEsitiConsiderati().get(dati.getProtocollo());
			}
			else{
				esitiValidi = activePolicy.getConfigurazioneControlloTraffico().getCalcoloLatenzaPortaApplicativaEsitiConsiderati().get(dati.getProtocollo());
			}
			boolean found = false;
			for (int esitoValido : esitiValidi) {
				if(dati.getEsitoTransazione() == esitoValido){
					latenza = this.getLatenza(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeTipoLatenza(), dati);
					found = true;
					break;
				}
			}
			
			if(found){
				super.setPolicyDegradoPrestazionaleRequestCounter(this.policyDegradoPrestazionaleRequestCounter.incrementAndGet());
				super.setPolicyDegradoPrestazionaleCounter(this.policyDegradoPrestazionaleCounter.addAndGet(latenza));
			}
			
		}
	}
	
	
	@Override
	public boolean updateDatiStartRequestApplicabile(Logger log, ActivePolicy activePolicy){
		
		if(this.getPolicyDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDateWindowInterval())==false){
			
			if(this.policyRealtime){
				// il contatore delle richieste lo tengo per qualsiasi tipo di risorsa
				// Pero' per il tempo medio lo devo incrementare quando aggiungo anche la latenza, senno poi la divisione (per l'avg) rispetto al numero di richieste è falsata
				// poiche' tengo conto anche delle richieste in corso, nonostante per queste non disponga ancora nella latenza
				// Lo stesso per le richieste che dipendono dall'esito devo incrementarle solo quando conosco l'esito della transazione
				if( (TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(activePolicy.getTipoRisorsaPolicy()) == false) &&
						isRisorsaContaNumeroRichiesteDipendentiEsito(activePolicy.getTipoRisorsaPolicy())==false){
					super.setPolicyRequestCounter(this.policyRequestCounter.incrementAndGet());
					return true;
				}
			}
			
		}
		
		return false;

	}
	
	
	@Override
	public void resetCounters(Date updatePolicyDate) {
		super.resetCounters(updatePolicyDate);
		
		if(updatePolicyDate!=null) {
			this.updatePolicyDate.set(updatePolicyDate.getTime());
		}
		
		this.policyRequestCounter.set(0l);
		this.policyCounter.set(0l);
		this.policyDenyRequestCounter.set(0l);
		
		this.policyDegradoPrestazionaleRequestCounter.set(0l);
		this.policyDegradoPrestazionaleCounter.set(0l);
	}

}
