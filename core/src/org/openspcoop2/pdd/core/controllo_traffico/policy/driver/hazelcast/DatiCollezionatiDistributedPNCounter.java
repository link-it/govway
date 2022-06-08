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
import com.hazelcast.crdt.pncounter.PNCounter;
/**
 * 
 * @author froggo
 *
 */
public class DatiCollezionatiDistributedPNCounter extends DatiCollezionati {

	
	private static final long serialVersionUID = 1L;
	
	// data di registrazione/aggiornamento policy
	protected final IAtomicLong updatePolicyDate;
	
	protected final PNCounter activeRequestCounter; // tiene traccia del numero di richieste attive sempre. Utile in jmx
	
	protected final IAtomicLong policyDate;
	protected final PNCounter policyRequestCounter;
	protected final PNCounter policyCounter;						 // utilizzato per tempi o banda
	protected final PNCounter policyDenyRequestCounter;
	
	protected final IAtomicLong policyDegradoPrestazionaleDate;
	protected final PNCounter policyDegradoPrestazionaleRequestCounter;
	protected final PNCounter policyDegradoPrestazionaleCounter;
	
	
	public DatiCollezionatiDistributedPNCounter(Date updatePolicyDate, HazelcastInstance hazelcast, String uniqueMapId) {
		super(updatePolicyDate);
		
		this.updatePolicyDate = hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-updatePolicyDate");
		this.activeRequestCounter = hazelcast.getPNCounter("pncounter-" + uniqueMapId + "-activeRequestCounter-rate-limiting");
		
		this.policyDate = hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-policyDate");
		this.policyRequestCounter = hazelcast.getPNCounter("pncounter-" + uniqueMapId + "-policyRequestCounter-rate-limiting");
		this.policyCounter = hazelcast.getPNCounter("pncounter-" + uniqueMapId + "-policyCounter-rate-limiting");
		this.policyDenyRequestCounter = hazelcast.getPNCounter("pncounter-" + uniqueMapId + "-policyDenyRequestCounter-rate-limiting");
		
		this.policyDegradoPrestazionaleDate = hazelcast.getCPSubsystem().getAtomicLong(uniqueMapId + "-policyDegradoPrestazionaleDate");
		this.policyDegradoPrestazionaleRequestCounter = hazelcast.getPNCounter("pncounter-" + uniqueMapId + "-policyDegradoPrestazionaleRequestCounter-rate-limiting");
		this.policyDegradoPrestazionaleCounter = hazelcast.getPNCounter("pncounter-" + uniqueMapId + "-policyDegradoPrestazionaleCounter-rate-limiting");
	}
	
	
	public DatiCollezionatiDistributedPNCounter(DatiCollezionati dati, HazelcastInstance hazelcast, String uniqueMapId) {
		this(dati.getUpdatePolicyDate(), hazelcast, uniqueMapId);
		
		// Inizializzo il padre
		dati.clone_impl(this);
		
		
		if (this.updatePolicyDate.get() == 0) {
			this.updatePolicyDate.set(dati.getUpdatePolicyDate().getTime());
		}
		
		// Se non ho la policyDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (dati.getPolicyDate() != null) {
			// Quando clono un DatiCollezionati e lo metto dentro una versione distribuita, Devo stare attento a non resettare
			// i contatori ogni volta perchè questa inizializzazione può essere fatta contemporaneamente e in momenti diversi
			// da più nodi. Se la policyDate è settata, assumo che l'oggetto distribuito sia stato già inizializzato e non lo resetto.
			
			if (this.policyDate.get() == 0) {
				
				if (this.policyDate.compareAndSet(0, dati.getPolicyDate().getTime())) {
					
					// Resetto prima i contatori visto che con i PNCounter non posso fare il set diretto
					this.policyRequestCounter.subtractAndGet(this.policyRequestCounter.get());
					this.policyDenyRequestCounter.subtractAndGet(this.policyDenyRequestCounter.get());
					this.policyCounter.subtractAndGet(this.policyCounter.get());
					
					if (dati.getPolicyRequestCounter() != null) {
						this.policyRequestCounter.addAndGet(dati.getPolicyRequestCounter());
					}
					
					if (dati.getPolicyCounter() != null) {
						this.policyCounter.addAndGet(dati.getPolicyCounter());
					}
					
					if (dati.getPolicyDenyRequestCounter() != null) {
						this.policyDenyRequestCounter.addAndGet(dati.getPolicyDenyRequestCounter());
					}
					
				}
			}
		}
	
		// Se non ho la policyDegradoPrestazionaleDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (dati.getPolicyDegradoPrestazionaleDate() != null) {
		
			if (this.policyDegradoPrestazionaleDate.get() == 0) {
	
				if (this.policyDegradoPrestazionaleDate.compareAndSet(0, dati.getPolicyDegradoPrestazionaleDate().getTime())) {
	
					// Resetto prima i contatori visto che con i PNCounter non posso fare il set diretto
					this.policyDegradoPrestazionaleRequestCounter.subtractAndGet(this.policyDegradoPrestazionaleRequestCounter.get());
					this.policyDegradoPrestazionaleCounter.subtractAndGet(this.policyDegradoPrestazionaleCounter.get());
					
					if (dati.getPolicyDegradoPrestazionaleRequestCounter() != null) {
						this.policyDegradoPrestazionaleRequestCounter.addAndGet(dati.getPolicyDegradoPrestazionaleRequestCounter());
					}
					
					if (dati.getPolicyDegradoPrestazionaleCounter() != null) {
						this.policyDegradoPrestazionaleCounter.addAndGet(dati.getPolicyDegradoPrestazionaleCounter());
					}
				}
			}
		}
		
		
	}
	
	@Override
	protected void initDatiIniziali(ActivePolicy activePolicy){
		super.initDatiIniziali(activePolicy);
		
		// L'unico contatore distribuito inizializzato nel padre è questo
		this.activeRequestCounter.subtractAndGet(this.activeRequestCounter.get());
		this.activeRequestCounter.addAndGet(super.getActiveRequestCounter());
	}
	
	@Override
	protected void resetPolicyCounterForDate(Date d) {
		super.resetPolicyCounterForDate(d);
		
		this.policyDate.set(d.getTime());
		
		if(this.policyRealtime!=null && this.policyRealtime){
			this.policyRequestCounter.subtractAndGet(this.policyRequestCounter.get());
			this.policyDenyRequestCounter.subtractAndGet(this.policyDenyRequestCounter.get());
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.policyCounter.subtractAndGet(this.policyCounter.get());
			}
		}
	}
	
	
	@Override
	protected void resetPolicyCounterForDateDegradoPrestazionale(Date d) {
		super.resetPolicyCounterForDateDegradoPrestazionale(d);
		
		this.policyDegradoPrestazionaleDate.set(d.getTime());
		
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.policyDegradoPrestazionaleRequestCounter.subtractAndGet(this.policyDegradoPrestazionaleRequestCounter.get());
			this.policyDegradoPrestazionaleCounter.subtractAndGet(this.policyDegradoPrestazionaleCounter.get());
		}
	}
	
	
	@Override
	public void registerStartRequest(Logger log, ActivePolicy activePolicy){
		
		if(super.getCreationDate() ==null){
			this.initDatiIniziali(activePolicy);
		}
		
		this.activeRequestCounter.incrementAndGet();
		
		if(this.getPolicyDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDateWindowInterval())==false){
			
			this.checkPolicyCounterForDate(log,activePolicy);
		}

		if(this.getPolicyDegradoPrestazionaleDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDegradoPrestazionaleDateWindowInterval())==false){
			
			this.checkPolicyCounterForDateDegradoPrestazionale(log,activePolicy);
			
		}
		
	}
	
	
	@Override
	public void registerEndRequest(Logger log, ActivePolicy activePolicy, MisurazioniTransazione dati){
		
		if(super.getCreationDate()==null){
			return; // non inizializzato?
		}
		
		this.activeRequestCounter.decrementAndGet();
				
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
				this.policyDegradoPrestazionaleRequestCounter.incrementAndGet();
				this.policyDegradoPrestazionaleCounter.addAndGet(latenza);
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
					this.policyRequestCounter.incrementAndGet();
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
		
		this.policyRequestCounter.subtractAndGet(this.policyRequestCounter.get());
		this.policyDenyRequestCounter.subtractAndGet(this.policyDenyRequestCounter.get());
		this.policyCounter.subtractAndGet(this.policyCounter.get());
		
		this.policyDegradoPrestazionaleRequestCounter.subtractAndGet(this.policyDegradoPrestazionaleRequestCounter.get());
		this.policyDegradoPrestazionaleCounter.subtractAndGet(this.policyDegradoPrestazionaleCounter.get());
		
	}
	
	
	@Override
	public long getActiveRequestCounter() {
		return this.activeRequestCounter.get();
	}
	
	
	@Override
	public Date getUpdatePolicyDate() {
		return new Date(this.updatePolicyDate.get());
	}

	
	@Override
	public Date getPolicyDate() {
		return new Date(this.policyDate.get());
	}	
	

	@Override
	public Long getPolicyRequestCounter() {
		return this.policyRequestCounter.get();
	}


	@Override
	public Long getPolicyCounter() {
		return this.policyCounter.get();
	}	

	@Override
	public Long getPolicyDenyRequestCounter() {
		return this.policyDenyRequestCounter.get();
	}
	
	@Override
	public Date getPolicyDegradoPrestazionaleDate() {
		return new Date(this.policyDegradoPrestazionaleDate.get());
	}
	
	@Override
	public Long getPolicyDegradoPrestazionaleRequestCounter() {
		return this.policyDegradoPrestazionaleRequestCounter.get();
	}

	@Override
	public Long getPolicyDegradoPrestazionaleCounter() {
		return this.policyDegradoPrestazionaleCounter.get();
	}

}
