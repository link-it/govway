package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.singoli_contatori.IDatiCollezionatiDistributed;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLongAdder;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;

/**
 * Versione simile a quella del PNCounter per hazelcast. Abbiamo un contatore distribuito molto veloce, il LongAdder.
 * 
 * TODO: Parla con andrea, visto che per il PNCounter abbiamo le versioni incrementAndGet e addAndGet e in questo caso invece no,
 * forse è il caso di ottenere prima la sum() e poi lavorare in locale? Sempre per lo stesso motivo per il quale ho rimosso gli overload dei vari getPolicyCounter:
 * se prima aggiorno i contatori distribuiti e poi ne recupero il valore, il valore recuperato avrà contato anche numerose altre richieste distribuite che possono
 * non ancora essere state servite, e quando poi vengono servite, il PolicyVerifier le blocca.
 *
 */
public class DatiCollezionatiDistributedLongAdder  extends DatiCollezionati implements IDatiCollezionatiDistributed {
	
	private static final long serialVersionUID = 1L;
	
	private final RedissonClient redisson;
	private final String uniquePrefixId;

	protected final RAtomicLong distributedUpdatePolicyDate;
	protected final RAtomicLong distributedPolicyDate;	
	
	protected RLongAdder distributedPolicyRequestCounter;	
	protected RLongAdder distributedPolicyCounter;						 // utilizzato per tempi o banda
	
	protected final RAtomicLong distributedPolicyDegradoPrestazionaleDate;
	protected  RLongAdder distributedPolicyDegradoPrestazionaleRequestCounter;
	protected RLongAdder distributedPolicyDegradoPrestazionaleCounter;
	
	private String getDistributedName(String name) {
		return "longadder-"+this.uniquePrefixId+"-"+name+"-rl";
	}
	
	public DatiCollezionatiDistributedLongAdder(Date updatePolicyDate, RedissonClient  redisson, String uniquePrefixId) {
		super(updatePolicyDate);
	
		this.redisson = redisson;
		this.uniquePrefixId = uniquePrefixId;
		
		this.distributedPolicyDate = this.redisson.getAtomicLong(this.getDistributedName("distributedPolicyDate"));
		this.distributedUpdatePolicyDate = this.redisson.getAtomicLong(this.getDistributedName("distributedUpdatePolicyDate"));
		this.distributedPolicyDegradoPrestazionaleDate = this.redisson.getAtomicLong(this.getDistributedName("distributedPolicyDegradoPrestazionaleDate"));
		
		// Gestisco la updatePolicyDate qui.
		// se updatePolicyDate è > this.distributedUpdatePolicyDate.get() allora resetto i contatori del cluster e setto la nuova data distribuita.
		// Questo per via di come funziona l'aggiornamento delle policy: i datiCollezionati correnti per una map<IDUnivoco..., DatiCollezionati> vengono cancellati e reinizializzati.
		// Per gli altri nodi in esecuzione, la updatePolicyDate locale resta sempre la stessa, ma non viene usata.
		
		
		if (updatePolicyDate != null && this.distributedUpdatePolicyDate.get() < updatePolicyDate.getTime()) {
			this.resetCounters(updatePolicyDate);
		}
		
	}
	
	
	public DatiCollezionatiDistributedLongAdder(DatiCollezionati dati, RedissonClient redisson, String uniquePrefixId) {
		this(dati.getUpdatePolicyDate(), redisson, uniquePrefixId);
		
		// Inizializzo il padre
		dati.clone_impl(this);
		
		if (super.getUpdatePolicyDate() != null) {
			this.distributedUpdatePolicyDate.compareAndSet(0,super.getUpdatePolicyDate().getTime());
		}
		
		// Se non ho la policyDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (super.getPolicyDate() != null) {

			// Se ci sono altri nodi che stanno andando, la distributedPolicyDate DEVE essere != 0 
			// Se la policyDate è a zero vuol dire che questo è il primo nodo del cluster che sta inizializzando la policy per mezzo di un backup e che su tutto il cluster
			// non sono ancora transitate richieste.
			if (this.distributedPolicyDate.compareAndSet(0, super.getPolicyDate().getTime())) {
				// Se la data distribuita non era inizializzata e questo nodo l'ha settata, imposto i contatori come da immagine bin.
				//	Faccio la addAndGet, in quanto tutti valori positivi, non entriamo in conflitto con gli altri nodi che stanno effettuando lo startup nello stesso momento
				
				this.distributedPolicyCounter = this.redisson.getLongAdder(this.getDistributedName(
						"distributedPolicyCounter"+super.getPolicyDate().getTime()));
				
				this.distributedPolicyRequestCounter = this.redisson.getLongAdder(this.getDistributedName(
						"distributedPolicyRequestCounter"+super.getPolicyDate().getTime())) ;
				
				if (super.getPolicyRequestCounter() != null) {
					this.distributedPolicyRequestCounter.add(super.getPolicyRequestCounter());
				}
				
				if (super.getPolicyCounter() != null) {
					this.distributedPolicyCounter.add(super.getPolicyCounter());
				}
								
			} else {
				Long polDate = this.distributedPolicyDate.get();
				this.distributedPolicyCounter = this.redisson.getLongAdder(this.getDistributedName(
						"distributedPolicyCounter"+polDate));
				
				this.distributedPolicyRequestCounter = this.redisson.getLongAdder(this.getDistributedName(
						"distributedPolicyRequestCounter"+polDate));
			}
		}
		
		// Se non ho la policyDegradoPrestazionaleDate, non considero il resto delle informazioni, che senza di essa non hanno senso.
		if (super.getPolicyDegradoPrestazionaleDate() != null) {
			
			// Imposto i contatori distribuiti solo se nel frattempo non l'ha fatto un altro thread del cluster.
			if (this.distributedPolicyDegradoPrestazionaleDate.compareAndSet(0, super.getPolicyDegradoPrestazionaleDate().getTime())) {
				
				Long degradoPrestazionaleTime = super.getPolicyDegradoPrestazionaleDate().getTime();
				
				this.distributedPolicyDegradoPrestazionaleCounter = this.redisson.getLongAdder(this.getDistributedName(
						"distributedPolicyDegradoPrestazionaleCounter"+degradoPrestazionaleTime));
				
				this.distributedPolicyDegradoPrestazionaleRequestCounter = this.redisson.getLongAdder(this.getDistributedName(
						"distributedPolicyDegradoPrestazionaleRequestCounter"+degradoPrestazionaleTime));
				
				if (super.getPolicyDegradoPrestazionaleRequestCounter() != null) {
					this.distributedPolicyDegradoPrestazionaleRequestCounter.add(super.getPolicyDegradoPrestazionaleRequestCounter());
				}
				
				if (super.getPolicyDegradoPrestazionaleCounter() != null) {
					this.distributedPolicyDegradoPrestazionaleCounter.add(super.getPolicyDegradoPrestazionaleCounter());
				}
			}  else {
				Long degradoPrestazionaleTime = this.distributedPolicyDate.get();
				this.distributedPolicyDegradoPrestazionaleCounter = this.redisson.getLongAdder(this.getDistributedName(
						"distributedPolicyDegradoPrestazionaleCounter"+degradoPrestazionaleTime));
				
				this.distributedPolicyDegradoPrestazionaleRequestCounter = this.redisson.getLongAdder(this.getDistributedName(
						"distributedPolicyDegradoPrestazionaleRequestCounter"+degradoPrestazionaleTime));
			}
		}
		
	}
	
	@Override
	protected void initDatiIniziali(ActivePolicy activePolicy){
		super.initDatiIniziali(activePolicy);
	}
	
	
	/*
	 * Qui scegliamo lo stesso metodo di reset fatto per DatiCollezionatiDistribuiti, ovvero prendiamo dei nuovi contatori.
	 
	 *  
	 */
	@Override
	protected void resetPolicyCounterForDate(Date policyDate) {
		Long policyTime = policyDate.getTime();

		this.distributedPolicyDate.set(policyTime);
		
		// Prendo i nuovi contatori
		if(this.policyRealtime!=null && this.policyRealtime){
			this.distributedPolicyRequestCounter = this.redisson.getLongAdder(this.getDistributedName(
					"policyRequestCounter"+policyTime));
			
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				this.distributedPolicyCounter = this.redisson.getLongAdder(this.getDistributedName(
						"policyCounter"+policyTime));
			}
		}
				
		
		super.resetPolicyCounterForDate(policyDate);
	}
	
	
	@Override
	protected void resetPolicyCounterForDateDegradoPrestazionale(Date policyDate) {
		Long policyTime = policyDate.getTime();
		
		this.distributedPolicyDegradoPrestazionaleDate.set(policyDate.getTime());
		
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.distributedPolicyDegradoPrestazionaleCounter = this.redisson.getLongAdder(this.getDistributedName(
					"distributedPolicyDegradoPrestazionaleCounter"+policyTime));
			
			this.distributedPolicyDegradoPrestazionaleRequestCounter = this.redisson.getLongAdder(this.getDistributedName(
					"distributedPolicyDegradoPrestazionaleRequestCounter"+policyTime));
		}
		
		super.resetPolicyCounterForDateDegradoPrestazionale(policyDate);
	}
	
	
	/*@Override
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
		
	}*/
	
	
	@Override
	public void registerEndRequest(Logger log, ActivePolicy activePolicy, MisurazioniTransazione dati){
		
		if(super.getCreationDate()==null){
			return; // non inizializzato?
		}
		
		super.setActiveRequestCounter(super.getActiveRequestCounter()-1);
				
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
				// Questi potrei farli lavorare su un clone del this, in modo da rimuovere la lock su PolicyGroupByAttriveThreadsDistrtibutedNoLock
				this.distributedPolicyDegradoPrestazionaleRequestCounter.increment();
				this.setPolicyDegradoPrestazionaleRequestCounter(this.distributedPolicyDegradoPrestazionaleRequestCounter.sum());
				
				this.distributedPolicyDegradoPrestazionaleCounter.add(latenza);
				this.setPolicyDegradoPrestazionaleCounter(this.distributedPolicyDegradoPrestazionaleCounter.sum());
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
					this.distributedPolicyRequestCounter.increment();
					this.setPolicyRequestCounter(this.distributedPolicyRequestCounter.sum());
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	@Override
	public void updateDatiEndRequestApplicabile(Logger log, ActivePolicy activePolicy,
			MisurazioniTransazione dati,
			List<Integer> esitiCodeOk, List<Integer> esitiCodeKo_senzaFaultApplicativo, List<Integer> esitiCodeFaultApplicativo, 
			boolean isViolata) throws PolicyException{
		
		if(this.policyRealtime!=null && this.policyRealtime){
		
            int [] esitiPolicyViolate = activePolicy.getConfigurazioneControlloTraffico().getEsitiPolicyViolate().get(dati.getProtocollo());
            boolean foundEsitoDeny = false;
            for (int esitoViolato : esitiPolicyViolate) {
            	if(dati.getEsitoTransazione() == esitoViolato){
            		foundEsitoDeny = true;
            		break;
            	}
            }
            
            if(foundEsitoDeny){
            	if( (TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(activePolicy.getTipoRisorsaPolicy()) == false) &&
						isRisorsaContaNumeroRichiesteDipendentiEsito(activePolicy.getTipoRisorsaPolicy())==false){
            		this.distributedPolicyRequestCounter.decrement();
            		this.setPolicyRequestCounter(this.distributedPolicyRequestCounter.sum());
            	}
            	if(isViolata) {
            		// Aumento solamente il contatore della policy la quale ha bloccato la transazione
            		super.setPolicyDenyRequestCounter(super.getPolicyDenyRequestCounter()+1);
            	}
				return; // non incremento alcun contatore.
			}
            
			
			switch (activePolicy.getTipoRisorsaPolicy()) {
			
			case DIMENSIONE_MASSIMA_MESSAGGIO:
				
				// nop
				
				break;			
			
			case NUMERO_RICHIESTE:
				
				// nop
				
				break;
				
			case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
			case NUMERO_RICHIESTE_FALLITE:
			case NUMERO_FAULT_APPLICATIVI:
			case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
				
				List<Integer> esitiAppartenentiGruppo = null;
				try {
					if(TipoRisorsa.NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO.equals(activePolicy.getTipoRisorsaPolicy())) {
						//esitiAppartenentiGruppo = EsitiProperties.getInstance(log).getEsitiCodeOk();
						esitiAppartenentiGruppo = esitiCodeOk;
					}
					else if(TipoRisorsa.NUMERO_RICHIESTE_FALLITE.equals(activePolicy.getTipoRisorsaPolicy())) {
						//esitiAppartenentiGruppo = EsitiProperties.getInstance(log).getEsitiCodeKo_senzaFaultApplicativo();
						esitiAppartenentiGruppo = esitiCodeKo_senzaFaultApplicativo;
					}
					else if(TipoRisorsa.NUMERO_FAULT_APPLICATIVI.equals(activePolicy.getTipoRisorsaPolicy())) {
						//esitiAppartenentiGruppo = EsitiProperties.getInstance(log).getEsitiCodeFaultApplicativo();
						esitiAppartenentiGruppo = esitiCodeFaultApplicativo;
					}
					else {
						// NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI
						esitiAppartenentiGruppo = new ArrayList<Integer>();
						esitiAppartenentiGruppo.addAll(esitiCodeKo_senzaFaultApplicativo);
						esitiAppartenentiGruppo.addAll(esitiCodeFaultApplicativo);
					}
				}catch(Exception e) {
					throw new PolicyException(e.getMessage(),e);
				}
				for (int esitoAppartenenteGruppo : esitiAppartenentiGruppo) {
					if(dati.getEsitoTransazione() == esitoAppartenenteGruppo){
						this.distributedPolicyRequestCounter.increment();
						this.setPolicyRequestCounter(this.distributedPolicyRequestCounter.sum());
						break;
					}
				}
				break;
				
			case OCCUPAZIONE_BANDA:
				
				// viene misurata la banda "generata" dalle applicazioni
				//System.out.println("Incremento banda da "+this.policyCounter);
				this.distributedPolicyCounter.add(this.getBanda(activePolicy.getConfigurazionePolicy().getValoreTipoBanda(), dati));
				this.setPolicyCounter(this.distributedPolicyCounter.sum());
				//System.out.println("Incremento banda a "+this.policyCounter);
								
				break;
	
				
			case TEMPO_COMPLESSIVO_RISPOSTA:
			case TEMPO_MEDIO_RISPOSTA:
				
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
						long latenza = this.getLatenza(activePolicy.getConfigurazionePolicy().getValoreTipoLatenza(), dati);
						//System.out.println("Incremento tempo da "+this.policyCounter);
						this.distributedPolicyCounter.add(latenza);
						this.setPolicyCounter(this.distributedPolicyCounter.sum());
						//System.out.println("Incremento tempo a "+this.policyCounter);
						found = true;
						break;
					}
				}
			
				if(found && TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(activePolicy.getTipoRisorsaPolicy())){
					this.distributedPolicyRequestCounter.increment();
					this.setPolicyRequestCounter(this.distributedPolicyRequestCounter.sum());
				}
				
				break;
			}
		}
			
					
	}
	
	
	@Override
	public void resetCounters(Date updatePolicyDate) {
		super.resetCounters(updatePolicyDate);
		
		if(updatePolicyDate!=null) {
			this.distributedUpdatePolicyDate.set(updatePolicyDate.getTime());
		}
		
		if (this.distributedPolicyRequestCounter != null)  {
			this.distributedPolicyRequestCounter.reset();
		}
		
		if (this.distributedPolicyCounter != null) {
			this.distributedPolicyCounter.reset();
		}
		
		if (this.distributedPolicyDegradoPrestazionaleRequestCounter != null) {
			this.distributedPolicyDegradoPrestazionaleRequestCounter.reset();
		}
		
		if (this.distributedPolicyDegradoPrestazionaleCounter != null) {
			this.distributedPolicyDegradoPrestazionaleCounter.reset();
		}
		
	}
	
	
	@Override
	public void destroyDatiDistribuiti() {
		this.distributedUpdatePolicyDate.delete();
		
		this.distributedPolicyRequestCounter.destroy();	
		this.distributedPolicyCounter.destroy();
		
		this.distributedPolicyDegradoPrestazionaleDate.delete();	
		this.distributedPolicyDegradoPrestazionaleRequestCounter.destroy();
		this.distributedPolicyDegradoPrestazionaleCounter.destroy();
	}
	
	
}
