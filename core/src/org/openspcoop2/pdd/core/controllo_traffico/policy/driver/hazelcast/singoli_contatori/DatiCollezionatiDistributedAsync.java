package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.singoli_contatori;

import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.slf4j.Logger;

import com.hazelcast.core.HazelcastInstance;

public class DatiCollezionatiDistributedAsync extends DatiCollezionatiDistributed {

	private static final long serialVersionUID = 1L;

	public DatiCollezionatiDistributedAsync(Date updatePolicyDate, HazelcastInstance hazelcast, String uniqueMapId) {
		super(updatePolicyDate, hazelcast, uniqueMapId);
	}
	
	
	public DatiCollezionatiDistributedAsync(DatiCollezionati dati, HazelcastInstance hazelcast, String uniqueMapId) {
		super(dati, hazelcast, uniqueMapId);
	}

	// TODO: Anche questo può sballare?
	/*@Override
	protected void initDatiIniziali(ActivePolicy activePolicy){
		super.initDatiIniziali(activePolicy);
		
		// L'unico contatore distribuito inizializzato nel padre è questo
		this.activeRequestCounter.setAsync(super.getActiveRequestCounter());
	}
	

	@Override
	protected void resetPolicyCounterForDate(Date d) {
		super.resetPolicyCounterForDate(d);
		
		this.policyDate.setAsync(d.getTime());
		
		if(this.policyRealtime!=null && this.policyRealtime){
			this.policyRequestCounter.setAsync(0l);
			this.policyDenyRequestCounter.setAsync(0l);
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.policyCounter.setAsync(0l);
			}
		}
	}
	
	
	@Override
	protected void resetPolicyCounterForDateDegradoPrestazionale(Date d) {
		super.resetPolicyCounterForDateDegradoPrestazionale(d);
		
		this.policyDegradoPrestazionaleDate.setAsync(d.getTime());
		
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.policyDegradoPrestazionaleRequestCounter.setAsync(0l);
			this.policyDegradoPrestazionaleCounter.setAsync(0l);
		}
	}
	
	
	@Override
	public void registerStartRequest(Logger log, ActivePolicy activePolicy){
		
		if(super.getCreationDate() ==null){
			this.initDatiIniziali(activePolicy);
		}
		
		this.activeRequestCounter.incrementAndGetAsync();
		
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
		
		this.activeRequestCounter.decrementAndGetAsync();
				
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
				this.policyDegradoPrestazionaleRequestCounter.incrementAndGetAsync();
				this.policyDegradoPrestazionaleCounter.addAndGetAsync(latenza);
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
					this.policyRequestCounter.incrementAndGetAsync();
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
			this.updatePolicyDate.setAsync(updatePolicyDate.getTime());
		}
		
		this.policyRequestCounter.setAsync(0l);
		this.policyCounter.setAsync(0l);
		this.policyDenyRequestCounter.setAsync(0l);
		
		this.policyDegradoPrestazionaleRequestCounter.setAsync(0l);
		this.policyDegradoPrestazionaleCounter.setAsync(0l);
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
*/

}
