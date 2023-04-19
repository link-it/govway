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

package org.openspcoop2.core.controllo_traffico.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.constants.TipoBanda;
import org.openspcoop2.core.controllo_traffico.constants.TipoControlloPeriodo;
import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.core.controllo_traffico.constants.TipoLatenza;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.date.UnitaTemporale;
import org.slf4j.Logger;

/**
 * DatiCollezionati 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiCollezionati implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DatiCollezionati(Date updatePolicyDate, Date gestorePolicyConfigDate) {
		this.updatePolicyDate = updatePolicyDate;
		this.gestorePolicyConfigDate = gestorePolicyConfigDate;
	}

	// data di lettura delle informazioni
	private Date cloneDate = null; // non deve essere gestita. Viene solamente inizializzata ad ogni clone
	
	// tipo di risorsa
	protected TipoRisorsa tipoRisorsa = null;
	
	// threads
	protected Long activeRequestCounter = null; // tiene traccia del numero di richieste attive sempre. Utile in jmx
	
	// data di creazione
	protected Date creationDate = null;
	// data di registrazione/aggiornamento policy
	private Date updatePolicyDate = null;
	// dati impostazione gestore della policy
	protected Date gestorePolicyConfigDate = null;
	
	// dati di attuazione manuale di un reset dei contatori
	private Long manuallyResetPolicyTimeMillis = null;

	private static final MapKey<String> START_PROCESS_REQUEST_TIME_MS = Map.newMapKey("START_PROCESS_REQUEST_TIME_MS");
	
	// dati iniziali
	private UnitaTemporale policyDateTypeInterval = null;
	private Integer policyDateInterval = null;
	private Boolean policyDateCurrentInterval  = null;
	private TipoFinestra policyDateWindowInterval = null;
	protected Boolean policyRealtime  = null;
	// dati dinamici
	protected Date policyDate = null;
	protected Long policyRequestCounter = null;
	protected Long policyCounter = null; // utilizzato per tempi o banda
	protected Long policyDenyRequestCounter = null;
	private Date oldPolicyDate = null;
	private Long oldPolicyRequestCounter = null;
	private Long oldPolicyCounter = null; // utilizzato per tempi o banda
	
	// dati iniziali degrado prestazionale
	private UnitaTemporale policyDegradoPrestazionaleDateTypeInterval = null;
	private Integer policyDegradoPrestazionaleDateInterval  = null;
	private Boolean policyDegradoPrestazionaleDateCurrentInterval = null;
	private TipoFinestra policyDegradoPrestazionaleDateWindowInterval = null;
	protected  Boolean policyDegradoPrestazionaleRealtime  = null;
	// dati dinamici degrado prestazionale
	protected Date policyDegradoPrestazionaleDate = null;
	protected Long policyDegradoPrestazionaleRequestCounter = null;
	protected Long policyDegradoPrestazionaleCounter= null;
	private Date oldPolicyDegradoPrestazionaleDate = null;
	private Long oldPolicyDegradoPrestazionaleRequestCounter = null;
	private Long oldPolicyDegradoPrestazionaleCounter = null; // utilizzato per tempi o banda
	
		

	public void initDatiIniziali(ActivePolicy activePolicy){
		
		// tipo di risorsa
		this.tipoRisorsa = activePolicy.getTipoRisorsaPolicy();
		
		// threads
		this.activeRequestCounter = 0l;
		
		// data di creazione
		this.creationDate = DateManager.getDate();
		// data di registrazione policy
		this.updatePolicyDate = activePolicy.getInstanceConfiguration().getUpdateTime();
		
		// Policy
		
		if(!activePolicy.getConfigurazionePolicy().isSimultanee() 
				&&
				TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo())){
			
			this.policyDateCurrentInterval = true; // cablato
			
			this.policyDateWindowInterval = activePolicy.getConfigurazionePolicy().getFinestraOsservazione();
			
			switch (activePolicy.getConfigurazionePolicy().getTipoIntervalloOsservazioneRealtime()) {
			case SECONDI:
				this.policyDateTypeInterval = UnitaTemporale.SECONDI;
				break;
			case MINUTI:
				this.policyDateTypeInterval = UnitaTemporale.MINUTI;
				break;
			case ORARIO:
				this.policyDateTypeInterval = UnitaTemporale.ORARIO;
				break;
			case GIORNALIERO:
				this.policyDateTypeInterval = UnitaTemporale.GIORNALIERO;
				break;
			}
			
			this.policyDateInterval = activePolicy.getConfigurazionePolicy().getIntervalloOsservazione();
			
			this.policyRealtime = true;
			
		}
		else if(TipoControlloPeriodo.STATISTIC.equals(activePolicy.getConfigurazionePolicy().getModalitaControllo())){
			
			this.policyDateWindowInterval = activePolicy.getConfigurazionePolicy().getFinestraOsservazione();
			
			if(TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getFinestraOsservazione())){
				this.policyDateCurrentInterval = activePolicy.getConfigurazioneControlloTraffico().isElaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente();
				this.policyDateTypeInterval = UnitaTemporale.ORARIO; // USARE SEMPRE L'INTERVALLO PIU' STRETTO. Se si introdurranno i minuti si dovrà utilizzare i minuti.
				
				// E' però necessaria la conversione in ore.
				switch (activePolicy.getConfigurazionePolicy().getTipoIntervalloOsservazioneStatistico()) {
				case ORARIO:
					this.policyDateInterval = activePolicy.getConfigurazionePolicy().getIntervalloOsservazione();
					break;
				case GIORNALIERO:
					this.policyDateInterval = activePolicy.getConfigurazionePolicy().getIntervalloOsservazione() * 24;
					break;
				case SETTIMANALE:
					this.policyDateInterval = activePolicy.getConfigurazionePolicy().getIntervalloOsservazione() * 24 * 7;
					break;
				case MENSILE:
					this.policyDateInterval = activePolicy.getConfigurazionePolicy().getIntervalloOsservazione() * 24 * 30; // (utilizzo 30 come giorni medi di un mese)
					break;
				}
			}
			else{
				this.policyDateCurrentInterval = true; // cablato
				
				switch (activePolicy.getConfigurazionePolicy().getTipoIntervalloOsservazioneStatistico()) {
				case ORARIO:
					this.policyDateTypeInterval = UnitaTemporale.ORARIO;
					break;
				case GIORNALIERO:
					this.policyDateTypeInterval = UnitaTemporale.GIORNALIERO;
					break;
				case SETTIMANALE:
					this.policyDateTypeInterval = UnitaTemporale.SETTIMANALE;
					break;
				case MENSILE:
					this.policyDateTypeInterval = UnitaTemporale.MENSILE;
					break;
				}
				
				this.policyDateInterval = activePolicy.getConfigurazionePolicy().getIntervalloOsservazione();
			}
			
			this.policyRealtime = false;
			
		}
		
		
		// DegradoPrestazionale
		
		if(activePolicy.getConfigurazionePolicy().isApplicabilitaDegradoPrestazionale()){
		
			if(TipoControlloPeriodo.REALTIME.equals(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeModalitaControllo())){
				
				this.policyDegradoPrestazionaleDateCurrentInterval = true; // cablato
				
				this.policyDegradoPrestazionaleDateWindowInterval = activePolicy.getConfigurazionePolicy().getDegradoAvgTimeFinestraOsservazione();
				
				switch (activePolicy.getConfigurazionePolicy().getDegradoAvgTimeTipoIntervalloOsservazioneRealtime()) {
				case SECONDI:
					this.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.SECONDI;
					break;
				case MINUTI:
					this.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.MINUTI;
					break;
				case ORARIO:
					this.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.ORARIO;
					break;
				case GIORNALIERO:
					this.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.GIORNALIERO;
					break;
				}
				
				this.policyDegradoPrestazionaleDateInterval = activePolicy.getConfigurazionePolicy().getDegradoAvgTimeIntervalloOsservazione();
				
				this.policyDegradoPrestazionaleRealtime = true;
				
			}
			else if(TipoControlloPeriodo.STATISTIC.equals(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeModalitaControllo())){
				
				this.policyDegradoPrestazionaleDateWindowInterval = activePolicy.getConfigurazionePolicy().getDegradoAvgTimeFinestraOsservazione();
				
				if(TipoFinestra.SCORREVOLE.equals(activePolicy.getConfigurazionePolicy().getDegradoAvgTimeFinestraOsservazione())){
					this.policyDegradoPrestazionaleDateCurrentInterval = activePolicy.getConfigurazioneControlloTraffico().isElaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente();
					this.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.ORARIO; // USARE SEMPRE L'INTERVALLO PIU' STRETTO. Se si introdurranno i minuti si dovrà utilizzare i minuti.
					
					// E' però necessaria la conversione in ore.
					switch (activePolicy.getConfigurazionePolicy().getDegradoAvgTimeTipoIntervalloOsservazioneStatistico()) {
					case ORARIO:
						this.policyDegradoPrestazionaleDateInterval = activePolicy.getConfigurazionePolicy().getDegradoAvgTimeIntervalloOsservazione();
						break;
					case GIORNALIERO:
						this.policyDegradoPrestazionaleDateInterval = activePolicy.getConfigurazionePolicy().getDegradoAvgTimeIntervalloOsservazione() * 24;
						break;
					case SETTIMANALE:
						this.policyDegradoPrestazionaleDateInterval = activePolicy.getConfigurazionePolicy().getDegradoAvgTimeIntervalloOsservazione() * 24 * 7;
						break;
					case MENSILE:
						this.policyDegradoPrestazionaleDateInterval = activePolicy.getConfigurazionePolicy().getDegradoAvgTimeIntervalloOsservazione() * 24 * 30; // (utilizzo 30 come giorni medi di un mese)
						break;
					}
				}
				else{
					this.policyDegradoPrestazionaleDateCurrentInterval = true; // cablato
					
					switch (activePolicy.getConfigurazionePolicy().getDegradoAvgTimeTipoIntervalloOsservazioneStatistico()) {
					case ORARIO:
						this.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.ORARIO;
						break;
					case GIORNALIERO:
						this.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.GIORNALIERO;
						break;
					case SETTIMANALE:
						this.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.SETTIMANALE;
						break;
					case MENSILE:
						this.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.MENSILE;
						break;
					}
					
					this.policyDegradoPrestazionaleDateInterval = activePolicy.getConfigurazionePolicy().getDegradoAvgTimeIntervalloOsservazione();
				}
				
				this.policyDegradoPrestazionaleRealtime = false;
				
			}
			
		}
	}
	
	public DatiCollezionati newInstance() {
		return _newInstance(false); // se servisse avere una immagine con i dati remoti, usare la clone con il boolean.
	}
	public DatiCollezionati newInstance(boolean readRemoteInfo) {
		return _newInstance(readRemoteInfo); 
	}
	private DatiCollezionati _newInstance(boolean readRemoteInfo) {
		Date updatePolicyDate = new Date(this.updatePolicyDate.getTime());
		Date gestorePolicyConfigDate = null;
		if(this.gestorePolicyConfigDate!=null) {
			gestorePolicyConfigDate = new Date(this.gestorePolicyConfigDate.getTime());
		}
		
		DatiCollezionati dati = new DatiCollezionati(updatePolicyDate, gestorePolicyConfigDate);
		
		return setValuesIn(dati, readRemoteInfo);
	}

	public DatiCollezionati setValuesIn(DatiCollezionati dati, boolean readRemoteInfo){
		
		// data di registrazione/aggiornamento policy
		
		dati.cloneDate = DateManager.getDate();
		
		// tipo di risorsa
		if(this.tipoRisorsa!=null){
			dati.tipoRisorsa = TipoRisorsa.toEnumConstant(this.tipoRisorsa.getValue());
		}
		
		// threads
		Long getActiveRequestCounter = this.getActiveRequestCounter(readRemoteInfo);
		if(getActiveRequestCounter!=null) {
			dati.activeRequestCounter = Long.valueOf(getActiveRequestCounter.longValue()+"");
		}
		
		// data di creazione
		if(this.creationDate!=null) {
			dati.creationDate = new Date(this.creationDate.getTime());
		}
		
		// dati iniziali
		if(this.policyDateTypeInterval!=null){
			dati.policyDateTypeInterval = UnitaTemporale.toEnumConstant(this.policyDateTypeInterval.getValue());
		}
		if(this.policyDateInterval!=null){
			dati.policyDateInterval = Integer.valueOf(this.policyDateInterval.intValue()+"");
		}
		if(this.policyDateCurrentInterval!=null){
			dati.policyDateCurrentInterval = Boolean.valueOf(this.policyDateCurrentInterval.booleanValue()+"");
		}
		if(this.policyDateWindowInterval!=null){
			dati.policyDateWindowInterval = TipoFinestra.toEnumConstant(this.policyDateWindowInterval.getValue());
		}
		if(this.policyRealtime!=null){
			dati.policyRealtime = Boolean.valueOf(this.policyRealtime+"");
		}
		
		// dati dinamici	
		if(this.policyDate!=null){
			dati.policyDate = new Date(this.policyDate.getTime());
		}	
		Long getPolicyRequestCounter = this.getPolicyRequestCounter(readRemoteInfo);
		if(getPolicyRequestCounter!=null){
			dati.policyRequestCounter = Long.valueOf(getPolicyRequestCounter.longValue()+"");
		}
		Long getPolicyCounter = this.getPolicyCounter(readRemoteInfo);
		if(getPolicyCounter!=null){
			dati.policyCounter = Long.valueOf(getPolicyCounter.longValue()+"");
		}
		Long getPolicyDenyRequestCounter = this.getPolicyDenyRequestCounter(readRemoteInfo);
		if(getPolicyDenyRequestCounter!=null){
			dati.policyDenyRequestCounter = Long.valueOf(getPolicyDenyRequestCounter.longValue()+"");
		}
		if(this.oldPolicyDate!=null){
			dati.oldPolicyDate = new Date(this.oldPolicyDate.getTime());
		}	
		if(this.oldPolicyRequestCounter!=null){
			dati.oldPolicyRequestCounter = Long.valueOf(this.oldPolicyRequestCounter.longValue()+"");
		}
		if(this.oldPolicyCounter!=null){
			dati.oldPolicyCounter = Long.valueOf(this.oldPolicyCounter.longValue()+"");
		}
		
		// dati iniziali degrado prestazionale
		if(this.policyDegradoPrestazionaleDateTypeInterval!=null){
			dati.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.toEnumConstant(this.policyDegradoPrestazionaleDateTypeInterval.getValue());
		}
		if(this.policyDegradoPrestazionaleDateInterval!=null){
			dati.policyDegradoPrestazionaleDateInterval = Integer.valueOf(this.policyDegradoPrestazionaleDateInterval.intValue()+"");
		}
		if(this.policyDegradoPrestazionaleDateCurrentInterval!=null){
			dati.policyDegradoPrestazionaleDateCurrentInterval = Boolean.valueOf(this.policyDegradoPrestazionaleDateCurrentInterval.booleanValue()+"");
		}
		if(this.policyDegradoPrestazionaleDateWindowInterval!=null){
			dati.policyDegradoPrestazionaleDateWindowInterval = TipoFinestra.toEnumConstant(this.policyDegradoPrestazionaleDateWindowInterval.getValue());
		}
		if(this.policyDegradoPrestazionaleRealtime!=null){
			dati.policyDegradoPrestazionaleRealtime = Boolean.valueOf(this.policyDegradoPrestazionaleRealtime.booleanValue()+"");
		}
		
		// dati dinamici degrado prestazionale
		if(this.policyDegradoPrestazionaleDate!=null){
			dati.policyDegradoPrestazionaleDate = new Date(this.policyDegradoPrestazionaleDate.getTime());
		}	
		Long getPolicyDegradoPrestazionaleRequestCounter = this.getPolicyDegradoPrestazionaleRequestCounter(readRemoteInfo);
		if(getPolicyDegradoPrestazionaleRequestCounter!=null){
			dati.policyDegradoPrestazionaleRequestCounter = Long.valueOf(getPolicyDegradoPrestazionaleRequestCounter.longValue()+"");
		}
		Long getPolicyDegradoPrestazionaleCounter = this.getPolicyDegradoPrestazionaleCounter(readRemoteInfo);
		if(getPolicyDegradoPrestazionaleCounter!=null){
			dati.policyDegradoPrestazionaleCounter = Long.valueOf(getPolicyDegradoPrestazionaleCounter.longValue()+"");
		}
		if(this.oldPolicyDegradoPrestazionaleDate!=null){
			dati.oldPolicyDegradoPrestazionaleDate = new Date(this.oldPolicyDegradoPrestazionaleDate.getTime());
		}	
		if(this.oldPolicyDegradoPrestazionaleRequestCounter!=null){
			dati.oldPolicyDegradoPrestazionaleRequestCounter = Long.valueOf(this.oldPolicyDegradoPrestazionaleRequestCounter.longValue()+"");
		}
		if(this.oldPolicyDegradoPrestazionaleCounter!=null){
			dati.oldPolicyDegradoPrestazionaleCounter = Long.valueOf(this.oldPolicyDegradoPrestazionaleCounter.longValue()+"");
		}
		
		
		
		return dati;
	}
	
		
	@Override
	public String toString(){
		
		SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
		
		StringBuilder bf = new StringBuilder();
		
		bf.append("Dati Generali");
		
		// tipo di risorsa
		String nomeRisorsa = null;
		if(this.tipoRisorsa!=null){
//			bf.append("Risorsa: ");
//			bf.append(this.tipoRisorsa.getValue());
			nomeRisorsa = this.tipoRisorsa.getValue();
		}
		
		// threads
		if(bf.length()>0){
			bf.append("\n");
		}
		bf.append("\tRichieste Attive: ");
		bf.append(this.getActiveRequestCounter(true));
		
		// data di creazione
		bf.append("\n");
		bf.append("\tData Attivazione Policy: ");
		bf.append(dateformat.format(this.creationDate));
		
		// data di registrazione/aggiornamento policy
		bf.append("\n");
		bf.append("\tData Registrazione/Aggiornamento Policy: ");
		bf.append(dateformat.format(this.updatePolicyDate));
		
		// data di registrazione/aggiornamento policy
		if(this.gestorePolicyConfigDate!=null) {
			bf.append("\n");
			bf.append("\tData Configurazione Gestore Policy: ");
			bf.append(dateformat.format(this.gestorePolicyConfigDate));
		}
		
		// Data now
		Date now = null;
		
		// Policy
		
		Date leftPrecedente = null;
		Date rightPrecedente = null;
		
		if(this.policyDateWindowInterval!=null){
			bf.append("\nDati collezionati per la metrica '"+nomeRisorsa+"'");
			
			if(this.policyRealtime!=null){
				bf.append("\n");
				bf.append("\tModalità di Controllo: ");
				if(this.policyRealtime){
					bf.append("realtime");
				}
				else{
					bf.append("statistica");
				}
			}
			
			bf.append("\n");
			bf.append("\tFinestra Osservazione: ");
			bf.append(this.policyDateWindowInterval.getValue());
			
			// intervallo
			
			Date left = null;
			Date right = null;
			switch (this.policyDateWindowInterval) {
			case CORRENTE:
				left = this.getLeftDateWindowCurrentInterval();
				right = this.getRightDateWindowCurrentInterval();
				break;
			case PRECEDENTE:
				left = this.getLeftDateWindowCurrentInterval();
				right = this.getRightDateWindowCurrentInterval();
				
				leftPrecedente = this.getLeftDateWindowPrecedentInterval();
				rightPrecedente = this.getRightDateWindowPrecedentInterval();
				break;
			case SCORREVOLE:
				if(now==null){
					now = DateManager.getDate();
				}
				left = this.getLeftDateWindowSlidingInterval(now);
				right = this.getRightDateWindowSlidingInterval(now);
				break;
			}
			
			bf.append("\n");
			
			if(left!=null && right!=null){
				bf.append("\tIntervallo [");
				bf.append(dateformat.format(left));
				bf.append(" - ");
				bf.append(dateformat.format(right));
				bf.append("]");
			}
			else{
				if(this.policyDate!=null){
					bf.append("\tData: ");
					bf.append(dateformat.format(this.policyDate));
				}
			}
		}
					
		Long getPolicyRequestCounter = this.getPolicyRequestCounter(true);
		if(getPolicyRequestCounter!=null){
			bf.append("\n");
			bf.append("\tNumero Richieste Conteggiate: ");
			bf.append(getPolicyRequestCounter);
		}
		Long getPolicyCounter = this.getPolicyCounter(true);
		if(getPolicyCounter!=null){
			bf.append("\n");
			bf.append("\tContatore: ");
			if(this.tipoRisorsa!=null){
				switch (this.tipoRisorsa) {
				case NUMERO_RICHIESTE:
				case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
				case NUMERO_RICHIESTE_FALLITE:
				case NUMERO_FAULT_APPLICATIVI:
				case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
				case DIMENSIONE_MASSIMA_MESSAGGIO: // non viene usata
					bf.append(getPolicyCounter);
					break;
				case OCCUPAZIONE_BANDA:
					bf.append(DatiCollezionati.translateToKb(getPolicyCounter));
					bf.append(" kb (");
					bf.append(getPolicyCounter);
					bf.append(" bytes)");
					break;
				case TEMPO_COMPLESSIVO_RISPOSTA:
					bf.append(DatiCollezionati.translateToSeconds(getPolicyCounter));
					bf.append(" secondi (");
					bf.append(getPolicyCounter);
					bf.append(" ms)");
					break;
				case TEMPO_MEDIO_RISPOSTA:
					bf.append(getPolicyCounter);
					bf.append(" ms");
					break;
				}
			}
			else{
				bf.append(getPolicyCounter);
			}
		}
		Double avg = this.getPolicyAvgValue(true);
		if(avg!=null){
			bf.append("\n");
			bf.append("\tValore Medio: ");
			if(this.tipoRisorsa!=null){
				switch (this.tipoRisorsa) {
				case NUMERO_RICHIESTE:
				case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
				case NUMERO_RICHIESTE_FALLITE:
				case NUMERO_FAULT_APPLICATIVI:
				case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
				case DIMENSIONE_MASSIMA_MESSAGGIO: // non viene usata
					bf.append(avg.longValue());	
					break;
				case OCCUPAZIONE_BANDA:
					bf.append(DatiCollezionati.translateToKb(avg.longValue()));
					bf.append(" kb (");
					bf.append(avg.longValue());
					bf.append(" bytes)");
					break;
				case TEMPO_COMPLESSIVO_RISPOSTA:
					bf.append(DatiCollezionati.translateToSeconds(avg.longValue()));
					bf.append(" secondi (");
					bf.append(avg.longValue());
					bf.append(" ms)");
					break;
				case TEMPO_MEDIO_RISPOSTA:
					bf.append(avg.longValue());	
					bf.append(" ms");
					break;
				}
			}
			else{
				bf.append(avg.longValue());	
			}
		}
		Long getPolicyDenyRequestCounter = this.getPolicyDenyRequestCounter(true);
		if(getPolicyDenyRequestCounter!=null){
			bf.append("\n");
			bf.append("\tNumero Richieste Bloccate: ");
			bf.append(getPolicyDenyRequestCounter);
		}
		
		if(leftPrecedente!=null && rightPrecedente!=null){
			bf.append("\n");
			bf.append("\tIntervallo Precedente [");
			bf.append(dateformat.format(leftPrecedente));
			bf.append(" - ");
			bf.append(dateformat.format(rightPrecedente));
			bf.append("]");
		}
		
		boolean oldExists = false;
		if(this.oldPolicyRequestCounter!=null){
			oldExists = true;
			bf.append("\n");
			bf.append("\tIntervallo Precedente - Numero Richieste Conteggiate: ");
			bf.append(this.oldPolicyRequestCounter);
		}
		if(this.oldPolicyCounter!=null){
			oldExists = true;
			bf.append("\n");
			bf.append("\tIntervallo Precedente - Contatore: ");
			if(this.tipoRisorsa!=null){
				switch (this.tipoRisorsa) {
				case NUMERO_RICHIESTE:
				case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
				case NUMERO_RICHIESTE_FALLITE:
				case NUMERO_FAULT_APPLICATIVI:
				case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
				case DIMENSIONE_MASSIMA_MESSAGGIO: // non viene usata
					bf.append(this.oldPolicyCounter);
					break;
				case OCCUPAZIONE_BANDA:
					bf.append(DatiCollezionati.translateToKb(this.oldPolicyCounter));
					bf.append(" kb (");
					bf.append(this.oldPolicyCounter);
					bf.append(" bytes)");
					break;
				case TEMPO_COMPLESSIVO_RISPOSTA:
					bf.append(DatiCollezionati.translateToSeconds(this.oldPolicyCounter));
					bf.append(" secondi (");
					bf.append(this.oldPolicyCounter);
					bf.append(" ms)");
					break;
				case TEMPO_MEDIO_RISPOSTA:
					bf.append(this.oldPolicyCounter);
					bf.append(" ms");
					break;
				}
			}
			else{
				bf.append(this.oldPolicyCounter);
			}
		}
		Double oldAvg = this.getOldPolicyAvgValue();
		if(oldAvg!=null){
			oldExists = true;
			bf.append("\n");
			bf.append("\tIntervallo Precedente - Valore Medio: ");
			if(this.tipoRisorsa!=null){
				switch (this.tipoRisorsa) {
				case NUMERO_RICHIESTE:
				case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
				case NUMERO_RICHIESTE_FALLITE:
				case NUMERO_FAULT_APPLICATIVI:
				case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
				case DIMENSIONE_MASSIMA_MESSAGGIO: // non viene usata
					bf.append(oldAvg.longValue());	
					break;
				case OCCUPAZIONE_BANDA:
					bf.append(DatiCollezionati.translateToKb(oldAvg.longValue()));	
					bf.append(" kb (");
					bf.append(oldAvg.longValue());
					bf.append(" bytes)");
					break;
				case TEMPO_COMPLESSIVO_RISPOSTA:
					bf.append(DatiCollezionati.translateToSeconds(oldAvg.longValue()));	
					bf.append(" secondi (");
					bf.append(oldAvg.longValue());
					bf.append(" ms)");
					break;
				case TEMPO_MEDIO_RISPOSTA:
					bf.append(oldAvg.longValue());	
					bf.append(" ms");
					break;
				}
			}
			else{
				bf.append(oldAvg.longValue());	
			}
		}
		
		if(oldExists==false && 
				this.policyDateWindowInterval!=null &&
				TipoFinestra.PRECEDENTE.equals(this.policyDateWindowInterval)){
			if(this.policyRealtime!=null && this.policyRealtime){
				bf.append("\n");
				bf.append("\tIntervallo Precedente - Dati non ancora disponibili");
			}
		}
		
		
		// Degrado
		
		leftPrecedente = null;
		rightPrecedente = null;
		
		if(this.policyDegradoPrestazionaleDateWindowInterval!=null){
			bf.append("\nDati collezionati per Degrado Prestazionale");
						
			if(this.policyDegradoPrestazionaleRealtime!=null){
				bf.append("\n");
				bf.append("\tModalità di Controllo: ");
				if(this.policyDegradoPrestazionaleRealtime){
					bf.append("realtime");
				}
				else{
					bf.append("statistica");
				}
			}
			
			bf.append("\n");
			bf.append("\tFinestra Osservazione: ");
			bf.append(this.policyDegradoPrestazionaleDateWindowInterval.getValue());
			
			// intervallo
			
			Date left = null;
			Date right = null;
			switch (this.policyDegradoPrestazionaleDateWindowInterval) {
			case CORRENTE:
				left = this.getDegradoPrestazionaleLeftDateWindowCurrentInterval();
				right = this.getDegradoPrestazionaleRightDateWindowCurrentInterval();
				break;
			case PRECEDENTE:
				
				left = this.getDegradoPrestazionaleLeftDateWindowCurrentInterval();
				right = this.getDegradoPrestazionaleRightDateWindowCurrentInterval();
				
				leftPrecedente = this.getDegradoPrestazionaleLeftDateWindowPrecedentInterval();
				rightPrecedente = this.getDegradoPrestazionaleRightDateWindowPrecedentInterval();
				break;
			case SCORREVOLE:
				if(now==null){
					now = DateManager.getDate();
				}
				left = this.getDegradoPrestazionaleLeftDateWindowSlidingInterval(now);
				right = this.getDegradoPrestazionaleRightDateWindowSlidingInterval(now);
				break;
			}
			
			bf.append("\n");
			
			if(left!=null && right!=null){
				bf.append("\tIntervallo [");
				bf.append(dateformat.format(left));
				bf.append(" - ");
				bf.append(dateformat.format(right));
				bf.append("]");
			}
			else{
				if(this.policyDegradoPrestazionaleDate!=null){
					bf.append("\tData: ");
					bf.append(dateformat.format(this.policyDegradoPrestazionaleDate));
				}
			}
		}
				
		Long getPolicyDegradoPrestazionaleRequestCounter = this.getPolicyDegradoPrestazionaleRequestCounter(true);
		if(getPolicyDegradoPrestazionaleRequestCounter!=null){
			bf.append("\n");
			bf.append("\tNumeroRichieste: ");
			bf.append(getPolicyDegradoPrestazionaleRequestCounter);
		}
		Long getPolicyDegradoPrestazionaleCounter = this.getPolicyDegradoPrestazionaleCounter(true);
		if(getPolicyDegradoPrestazionaleCounter!=null){
			bf.append("\n");
			bf.append("\tContatore: ");
			bf.append(getPolicyDegradoPrestazionaleCounter);
			bf.append(" ms");
		}
		Double avgDegradoPrestazionale = this.getPolicyDegradoPrestazionaleAvgValue(true);
		if(avgDegradoPrestazionale!=null){
			bf.append("\n");
			bf.append("\tValore Medio: ");
			bf.append(avgDegradoPrestazionale.longValue());	
			bf.append(" ms");
		}
		
		if(leftPrecedente!=null && rightPrecedente!=null){
			bf.append("\n");
			bf.append("\tIntervallo Precedente [");
			bf.append(dateformat.format(leftPrecedente));
			bf.append(" - ");
			bf.append(dateformat.format(rightPrecedente));
			bf.append("]");
		}
		
		boolean oldDegradoPrestazionaleExists = false;
		if(this.oldPolicyDegradoPrestazionaleRequestCounter!=null){
			oldDegradoPrestazionaleExists = true;
			bf.append("\n");
			bf.append("\tIntervallo Precedente - Numero Richieste: ");
			bf.append(this.oldPolicyDegradoPrestazionaleRequestCounter);
		}
		if(this.oldPolicyDegradoPrestazionaleCounter!=null){
			oldDegradoPrestazionaleExists = true;
			bf.append("\n");
			bf.append("\tIntervallo Precedente - Contatore: ");
			bf.append(this.oldPolicyDegradoPrestazionaleCounter);
			bf.append(" ms");
		}
		Double oldAvgDegradoPrestazionale = this.getOldPolicyDegradoPrestazionaleAvgValue();
		if(oldAvgDegradoPrestazionale!=null){
			oldDegradoPrestazionaleExists = true;
			bf.append("\n");
			bf.append("\tIntervallo Precedente - Valore Medio: ");
			bf.append(oldAvgDegradoPrestazionale.longValue());	
			bf.append(" ms");
		}
		
		if(oldDegradoPrestazionaleExists==false && 
				this.policyDegradoPrestazionaleDateWindowInterval!=null &&
				TipoFinestra.PRECEDENTE.equals(this.policyDegradoPrestazionaleDateWindowInterval)){
			if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
				bf.append("\n");
				bf.append("\tIntervallo Precedente - Dati non ancora disponibili");
			}
		}
		

		
		return bf.toString();
	}
	
	

	protected boolean isRisorsaContaNumeroRichieste(TipoRisorsa tipoRisorsa) {
		switch (tipoRisorsa) {
		case NUMERO_RICHIESTE:
		case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
		case NUMERO_RICHIESTE_FALLITE:
		case NUMERO_FAULT_APPLICATIVI:
		case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
			return true;
		case OCCUPAZIONE_BANDA:
		case TEMPO_COMPLESSIVO_RISPOSTA:
		case TEMPO_MEDIO_RISPOSTA:
		case DIMENSIONE_MASSIMA_MESSAGGIO: // non viene usata
			return false;
		}
		return false;
	}
	
	
	protected boolean isRisorsaContaNumeroRichiesteDipendentiEsito(TipoRisorsa tipoRisorsa) {
		switch (tipoRisorsa) {
		case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
		case NUMERO_RICHIESTE_FALLITE:
		case NUMERO_FAULT_APPLICATIVI:
		case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
			return true;
		case NUMERO_RICHIESTE:
		case OCCUPAZIONE_BANDA:
		case TEMPO_COMPLESSIVO_RISPOSTA:
		case TEMPO_MEDIO_RISPOSTA:
		case DIMENSIONE_MASSIMA_MESSAGGIO: // non viene usata
			return false;
		}
		return false;
	}
	

	protected void resetPolicyCounterForDate(Date d) {
		this.policyDate = d;
		if(this.policyRealtime!=null && this.policyRealtime){
			this.policyRequestCounter = 0l;
			this.policyDenyRequestCounter = 0l;
			if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
				//SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
				//System.out.println("Resetto policy counter a zero per intervallo d["+dateformat.format(d)+"]");
				this.policyCounter = 0l;
			}
		}
	}
	
	
	protected void resetPolicyCounterForDateDegradoPrestazionale(Date d) {
		this.policyDegradoPrestazionaleDate = d;
		if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
			this.policyDegradoPrestazionaleRequestCounter = 0l;
			this.policyDegradoPrestazionaleCounter = 0l;
		}
		
	}
	

	protected void checkPolicyCounterForDate(Logger log, ActivePolicy activePolicy){
		
		if(this.policyDate==null){
			// first-init
			resetPolicyCounterForDate(DateUtils.convertToLeftInterval(DateManager.getDate(),this.policyDateTypeInterval));
			
			if(this.policyRealtime!=null && !this.policyRealtime){
				// statistico. Serve subito anche l'intervallo precedente
				this.oldPolicyDate = DateUtils.incrementDate(this.policyDate, this.policyDateTypeInterval, this.policyDateInterval*(-1));
			}
			
		}
		
		else{
			
			Date rightInterval = this.getRightDateWindowCurrentInterval();
			Date now = DateManager.getDate();

			boolean after = now.after(rightInterval);
			if(activePolicy.getConfigurazioneControlloTraffico().isDebug()){
				SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
				log.debug("checkPolicyCounterForDate now["+dateformat.format(now)+"] after policyDate["+dateformat.format(rightInterval)+"]: "+after+"");
			}
			if(after){
				
				if(TipoFinestra.PRECEDENTE.equals(this.getPolicyDateWindowInterval())){
					// Salvo old
					this.oldPolicyDate = this.policyDate;
					if(this.policyRealtime!=null && this.policyRealtime){
						this.oldPolicyRequestCounter = this.policyRequestCounter;
						if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
							this.oldPolicyCounter = this.policyCounter;
						}
					}
				}
				
				// Genero nuova finestra
				Date d = DateUtils.incrementDate(this.policyDate, this.policyDateTypeInterval, this.policyDateInterval);
				
				Date dRight = DateUtils.convertToRightInterval(d, this.policyDateTypeInterval);
				dRight = DatiCollezionati.incrementDate(dRight, this.policyDateTypeInterval, this.policyDateInterval, this.policyDateCurrentInterval);
				//Date dRight = DateUtils.incrementDate(d, this.policyDateTypeInterval, this.policyDateInterval);
				//dRight = DateUtils.convertToRightInterval(dRight, this.policyDateTypeInterval);
				
				boolean before = dRight.before(now);
				if(activePolicy.getConfigurazioneControlloTraffico().isDebug()){
					SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
					log.debug("checkPolicyCounterForDate Increment d["+dateformat.format(d)+"] dRight["+dateformat.format(dRight)+"] before now["+
							dateformat.format(now)+"]: "+before);
				}
				while(before){
					
					// la nuova richiesta non riguarda l'intervallo+1 rispetto al precedente, ma intervalli successivi.
					// Devo resettare anche i vecchi contatori
					if(TipoFinestra.PRECEDENTE.equals(this.getPolicyDateWindowInterval())){				
						// Salvo old
						this.oldPolicyDate = d;
						if(this.policyRealtime!=null && this.policyRealtime){
							this.oldPolicyRequestCounter = 0l;
							if(this.tipoRisorsa==null || !isRisorsaContaNumeroRichieste(this.tipoRisorsa)){
								this.oldPolicyCounter = 0l;
							}
						}
					}
					
					d = DateUtils.incrementDate(d, this.policyDateTypeInterval, this.policyDateInterval);
					
					dRight = DateUtils.convertToRightInterval(d, this.policyDateTypeInterval);
					dRight = DatiCollezionati.incrementDate(dRight, this.policyDateTypeInterval, this.policyDateInterval, this.policyDateCurrentInterval);
					//dRight = DateUtils.incrementDate(d, this.policyDateTypeInterval, this.policyDateInterval);
					//dRight = DateUtils.convertToRightInterval(dRight, this.policyDateTypeInterval);
					
					before = dRight.before(now);
					if(activePolicy.getConfigurazioneControlloTraffico().isDebug()){
						SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
						log.debug("checkPolicyCounterForDate Increment d["+dateformat.format(d)+"] dRight["+dateformat.format(dRight)+"] before now["+
								dateformat.format(now)+"]: "+before);
					}
				}
				
				resetPolicyCounterForDate(d);
				
			} 
			
		}
				
	}
	

	protected void checkPolicyCounterForDateDegradoPrestazionale(Logger log, ActivePolicy activePolicy){
		
		if(this.policyDegradoPrestazionaleDate==null){
			
			// first-init
			resetPolicyCounterForDateDegradoPrestazionale(DateUtils.convertToLeftInterval(DateManager.getDate(),this.policyDegradoPrestazionaleDateTypeInterval));
			
			if(this.policyDegradoPrestazionaleRealtime!=null && !this.policyDegradoPrestazionaleRealtime){
				// statistico. Serve subito anche l'intervallo precedente
				this.oldPolicyDegradoPrestazionaleDate = DateUtils.incrementDate(this.policyDegradoPrestazionaleDate, 
						this.policyDegradoPrestazionaleDateTypeInterval, this.policyDegradoPrestazionaleDateInterval*(-1));
			}
			
		}
		
		else{
			
			Date rightInterval = this.getDegradoPrestazionaleRightDateWindowCurrentInterval();
			Date now = DateManager.getDate();

			boolean after = now.after(rightInterval);
			if(activePolicy.getConfigurazioneControlloTraffico().isDebug()){
				SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
				log.debug("checkPolicyCounterForDateDegradoPrestazionale now["+dateformat.format(now)+"] after policyDate["+dateformat.format(rightInterval)+"]: "+after+"");
			}
			if(after){
				
				if(TipoFinestra.PRECEDENTE.equals(this.getPolicyDegradoPrestazionaleDateWindowInterval())){				
					// Salvo old
					this.oldPolicyDegradoPrestazionaleDate = this.policyDegradoPrestazionaleDate;
					if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
						this.oldPolicyDegradoPrestazionaleRequestCounter = this.policyDegradoPrestazionaleRequestCounter;
						this.oldPolicyDegradoPrestazionaleCounter = this.policyDegradoPrestazionaleCounter;
					}
				}
				
				// Genero nuova finestra
				Date d = DateUtils.incrementDate(this.policyDegradoPrestazionaleDate, this.policyDegradoPrestazionaleDateTypeInterval, this.policyDegradoPrestazionaleDateInterval);
				Date dRight = DateUtils.incrementDate(d, this.policyDegradoPrestazionaleDateTypeInterval, this.policyDegradoPrestazionaleDateInterval);
				dRight = DateUtils.convertToRightInterval(dRight, this.policyDegradoPrestazionaleDateTypeInterval);
				boolean before = dRight.before(now);
				if(activePolicy.getConfigurazioneControlloTraffico().isDebug()){
					SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
					log.debug("checkPolicyCounterForDateDegradoPrestazionale Increment d["+dateformat.format(d)+"] dRight["+dateformat.format(dRight)+"] before now["+
							dateformat.format(now)+"]: "+before);
				}
				while(before){
					
					// la nuova richiesta non riguarda l'intervallo+1 rispetto al precedente, ma intervalli successivi.
					// Devo resettare anche i vecchi contatori
					if(TipoFinestra.PRECEDENTE.equals(this.getPolicyDegradoPrestazionaleDateWindowInterval())){				
						// Salvo old
						this.oldPolicyDegradoPrestazionaleDate = d;
						if(this.policyDegradoPrestazionaleRealtime!=null && this.policyDegradoPrestazionaleRealtime){
							this.oldPolicyDegradoPrestazionaleRequestCounter = 0l;
							this.oldPolicyDegradoPrestazionaleCounter = 0l;
						}
					}
					
					d = DateUtils.incrementDate(d, this.policyDegradoPrestazionaleDateTypeInterval, this.policyDegradoPrestazionaleDateInterval);
					dRight = DateUtils.incrementDate(d, this.policyDegradoPrestazionaleDateTypeInterval, this.policyDegradoPrestazionaleDateInterval);
					dRight = DateUtils.convertToRightInterval(dRight, this.policyDegradoPrestazionaleDateTypeInterval);
					before = dRight.before(now);
					if(activePolicy.getConfigurazioneControlloTraffico().isDebug()){
						SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
						log.debug("checkPolicyCounterForDateDegradoPrestazionale Increment d["+dateformat.format(d)+"] dRight["+dateformat.format(dRight)+"] before now["+
								dateformat.format(now)+"]: "+before);
					}
				}
				resetPolicyCounterForDateDegradoPrestazionale(d);
			} 
			
		}
				
	}
	
	// Questo metodo viene chiamato dai metodi 'resetCounters' degli oggetti PolicyGroupByActiveThreads che a sua volta sono invocabili solamente via Jmx/Console
	public void resetCounters(){
		this.resetCounters(null);
		
		this.manuallyResetPolicyTimeMillis = DateManager.getTimeMillis();
		//System.out.println("IMPOSTO RESET MANUALE ("+this.manuallyResetPolicyTimeMillis+") IN ["+this.toString()+"]");
		
	}
	// Questo metodo viene invocato quando viene rilevata una modifica ai valori di soglia di una policy e quindi viene aggiornata l'updatePolicyDate
	// In questo metodo non serve registrare il manuallyResetPolicyTimeMillis poichè il metodo viene invocato sempre prima di una 'registerStartRequest' mentre la funzione del manuallyResetPolicyTimeMillis
	// serve ad ignorare la gestione successiva ad un reset counter per le transazioni in corso.
	public void resetCounters(Date updatePolicyDate){
		
		if(updatePolicyDate!=null) {
			this.updatePolicyDate = updatePolicyDate;
		}
		
		if(this.policyRequestCounter!=null){
			this.policyRequestCounter = 0l;
		}
		if(this.policyCounter!=null){
			this.policyCounter = 0l;
		}
		if(this.policyDenyRequestCounter!=null){
			this.policyDenyRequestCounter = 0l;
		}
		this.oldPolicyDate = null;
		if(this.oldPolicyRequestCounter!=null){
			this.oldPolicyRequestCounter = 0l;
		}
		if(this.oldPolicyCounter!=null){
			this.oldPolicyCounter = 0l;
		}
		
		if(this.policyDegradoPrestazionaleRequestCounter!=null){
			this.policyDegradoPrestazionaleRequestCounter = 0l;
		}
		if(this.policyDegradoPrestazionaleCounter!=null){
			this.policyDegradoPrestazionaleCounter = 0l;
		}
		this.oldPolicyDegradoPrestazionaleDate = null;
		if(this.oldPolicyDegradoPrestazionaleRequestCounter!=null){
			this.oldPolicyDegradoPrestazionaleRequestCounter = 0l;
		}
		if(this.oldPolicyDegradoPrestazionaleCounter!=null){
			this.oldPolicyDegradoPrestazionaleCounter = 0l;
		}
		
	}
	
	public void checkDate(Logger log, ActivePolicy activePolicy){
		
		if(this.creationDate==null){
			this.initDatiIniziali(activePolicy);
		}
		
		if(this.getPolicyDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDateWindowInterval())==false){
			
			this.checkPolicyCounterForDate(log,activePolicy);
			
		}
		
		if(this.getPolicyDegradoPrestazionaleDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDegradoPrestazionaleDateWindowInterval())==false){
			
			this.checkPolicyCounterForDateDegradoPrestazionale(log,activePolicy);

		}
		
	}
	
	
	private boolean skipBecauseManuallyResetAfterStartRequest(String source, Map<Object> ctx) {
		if(this.manuallyResetPolicyTimeMillis!=null && ctx!=null) {
			Object o = ctx.get(START_PROCESS_REQUEST_TIME_MS);
			if(o==null) {
				// transazione gestita precedentemente al reset dei contatori
				//System.out.println("@"+source+" RILEVATO RESET '"+this.manuallyResetPolicyTimeMillis+"', trovato in ctx 'NULL'");
				return true;
			}
			Long l = (Long) o;
			if(l.longValue() < this.manuallyResetPolicyTimeMillis.longValue()) {
				// transazione gestita precedentemente al reset dei contatori
				//System.out.println("@"+source+" RILEVATO RESET '"+this.manuallyResetPolicyTimeMillis+"', trovato in ctx una data minore '"+l+"'");
				return true;
			}
		}
		return false;
	}
	
	
	
	protected void _registerStartRequest_incrementActiveRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		this.activeRequestCounter++;
		if(datiCollezionatiPerPolicyVerifier!=null) {
			datiCollezionatiPerPolicyVerifier.activeRequestCounter = this.activeRequestCounter;
		}
	}
	public void registerStartRequest(Logger log, ActivePolicy activePolicy, Map<Object> ctx){
		registerStartRequest(log, activePolicy, ctx, null);
	}
	public void registerStartRequest(Logger log, ActivePolicy activePolicy, Map<Object> ctx, DatiCollezionati datiCollezionatiPerPolicyVerifier){
		
		if(this.creationDate==null){
			this.initDatiIniziali(activePolicy);
		}
		
		_registerStartRequest_incrementActiveRequestCounter(datiCollezionatiPerPolicyVerifier);
		
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
		
		if(this.manuallyResetPolicyTimeMillis!=null && ctx!=null) {
			ctx.put(START_PROCESS_REQUEST_TIME_MS, DateManager.getTimeMillis());
			//System.out.println("IMPOSTATO IN CONTESTO: '"+DateManager.getTimeMillis()+"'");
		}
	}
	
	protected void _updateDatiStartRequestApplicabile_incrementRequestCounter(DatiCollezionati datiCollezionatiPerPolicyVerifier) {
		this.policyRequestCounter++;
		if(datiCollezionatiPerPolicyVerifier!=null) {
			datiCollezionatiPerPolicyVerifier.policyRequestCounter = this.policyRequestCounter;
		}
	}
	public boolean updateDatiStartRequestApplicabile(Logger log, ActivePolicy activePolicy, Map<Object> ctx){
		return updateDatiStartRequestApplicabile(log, activePolicy, ctx, null);
	}
	public boolean updateDatiStartRequestApplicabile(Logger log, ActivePolicy activePolicy, Map<Object> ctx, DatiCollezionati datiCollezionatiPerPolicyVerifier){
		
		if(skipBecauseManuallyResetAfterStartRequest("updateDatiStartRequestApplicabile", ctx)) {
			return false;
		}
		
		if(this.getPolicyDateWindowInterval()!=null && 
				TipoFinestra.SCORREVOLE.equals(this.getPolicyDateWindowInterval())==false){
			
			if(this.policyRealtime){
				// il contatore delle richieste lo tengo per qualsiasi tipo di risorsa
				// Pero' per il tempo medio lo devo incrementare quando aggiungo anche la latenza, senno poi la divisione (per l'avg) rispetto al numero di richieste è falsata
				// poiche' tengo conto anche delle richieste in corso, nonostante per queste non disponga ancora nella latenza
				// Lo stesso per le richieste che dipendono dall'esito devo incrementarle solo quando conosco l'esito della transazione
				if( (TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(activePolicy.getTipoRisorsaPolicy()) == false) &&
						isRisorsaContaNumeroRichiesteDipendentiEsito(activePolicy.getTipoRisorsaPolicy())==false){
					_updateDatiStartRequestApplicabile_incrementRequestCounter(datiCollezionatiPerPolicyVerifier);
					return true;
				}
			}
			
		}
		
		return false;

	}
	
	protected void _registerEndRequest_decrementActiveRequestCounter() {
		this.activeRequestCounter--;
	}
	protected void _registerEndRequest_incrementDegradoPrestazionaleRequestCounter() {
		this.policyDegradoPrestazionaleRequestCounter++;
	}
	protected void _registerEndRequest_incrementDegradoPrestazionaleCounter(long latenza) {
		this.policyDegradoPrestazionaleCounter = this.policyDegradoPrestazionaleCounter + latenza;
	}
	public void registerEndRequest(Logger log, ActivePolicy activePolicy, Map<Object> ctx, MisurazioniTransazione dati){
		
		if(this.creationDate==null){
			return; // non inizializzato?
		}
		
		_registerEndRequest_decrementActiveRequestCounter();
				
		// Il decrement delle richieste attive deve comunque essere attuato poichè il reset dei contatori non insiste sul numero di richieste attive
		if(skipBecauseManuallyResetAfterStartRequest("registerEndRequest", ctx)) {
			return;
		}
		
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
				_registerEndRequest_incrementDegradoPrestazionaleRequestCounter();
				_registerEndRequest_incrementDegradoPrestazionaleCounter(latenza);
			}
			
		}
	}
	
	protected void _updateDatiEndRequestApplicabile_incrementRequestCounter() {
		this.policyRequestCounter++;
	}
	protected void _updateDatiEndRequestApplicabile_decrementRequestCounter() {
		this.policyRequestCounter--;
	}
	protected void _updateDatiEndRequestApplicabile_incrementDenyRequestCounter() {
		this.policyDenyRequestCounter++;
	}
	protected void _updateDatiEndRequestApplicabile_incrementCounter(long v) {
		this.policyCounter = this.policyCounter + v;
	}
	public void updateDatiEndRequestApplicabile(Logger log, ActivePolicy activePolicy, Map<Object> ctx,
			MisurazioniTransazione dati,
			List<Integer> esitiCodeOk, List<Integer> esitiCodeKo_senzaFaultApplicativo, List<Integer> esitiCodeFaultApplicativo, 
			boolean isViolata) throws PolicyException{
		
		if(skipBecauseManuallyResetAfterStartRequest("updateDatiEndRequestApplicabile", ctx)) {
			return;
		}
		
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
            		_updateDatiEndRequestApplicabile_decrementRequestCounter(); // l'avevo incrementato nello start
            	}
            	if(isViolata) {
            		// Aumento solamente il contatore della policy la quale ha bloccato la transazione
            		_updateDatiEndRequestApplicabile_incrementDenyRequestCounter();
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
						_updateDatiEndRequestApplicabile_incrementRequestCounter();
						break;
					}
				}
				break;
				
			case OCCUPAZIONE_BANDA:
				
				// viene misurata la banda "generata" dalle applicazioni
				//System.out.println("Incremento banda da "+this.policyCounter);
				long banda = this.getBanda(activePolicy.getConfigurazionePolicy().getValoreTipoBanda(), dati);
				_updateDatiEndRequestApplicabile_incrementCounter(banda);
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
						_updateDatiEndRequestApplicabile_incrementCounter(latenza);
						//System.out.println("Incremento tempo a "+this.policyCounter);
						found = true;
						break;
					}
				}
			
				if(found && TipoRisorsa.TEMPO_MEDIO_RISPOSTA.equals(activePolicy.getTipoRisorsaPolicy())){
					_updateDatiEndRequestApplicabile_incrementRequestCounter(); 
				}
				
				break;
			}
		}
			
					
	}
	
	protected long getLatenza(TipoLatenza tipoLatenza, MisurazioniTransazione dati){
		long latenza = 0;
		switch (tipoLatenza) {
		case TOTALE:
			if(dati.getDataUscitaRisposta()!=null && dati.getDataIngressoRichiesta()!=null){
				latenza = dati.getDataUscitaRisposta().getTime()-dati.getDataIngressoRichiesta().getTime();
			}
			break;
		case SERVIZIO:
			if(dati.getDataUscitaRichiesta()!=null && dati.getDataIngressoRisposta()!=null){
				latenza = dati.getDataIngressoRisposta().getTime()-dati.getDataUscitaRichiesta().getTime();
			}
			break;
		case PORTA:
			if(dati.getDataUscitaRichiesta()!=null && dati.getDataIngressoRichiesta()!=null){
				latenza = dati.getDataUscitaRichiesta().getTime()-dati.getDataIngressoRichiesta().getTime();
			}
			if(dati.getDataIngressoRisposta()!=null && dati.getDataUscitaRisposta()!=null){
				latenza = latenza + dati.getDataUscitaRisposta().getTime()-dati.getDataIngressoRisposta().getTime();
			}
			break;
		}
		return latenza;
	}
	
	protected long getBanda(TipoBanda tipoBanda, MisurazioniTransazione dati){
		
		long bandaInterna = 0;
		long bandaEsterna = 0;
		switch (dati.getTipoPdD()) {
		case DELEGATA:
			if(dati.getRichiestaIngressoBytes()!=null && dati.getRichiestaIngressoBytes()>0){
				bandaInterna = bandaInterna + dati.getRichiestaIngressoBytes().longValue();
			}
			if(dati.getRispostaUscitaBytes()!=null && dati.getRispostaUscitaBytes()>0){
				bandaInterna = bandaInterna + dati.getRispostaUscitaBytes().longValue();
			}
			
			if(dati.getRichiestaUscitaBytes()!=null && dati.getRichiestaUscitaBytes()>0){
				bandaEsterna = bandaEsterna + dati.getRichiestaUscitaBytes().longValue();
			}
			if(dati.getRispostaIngressoBytes()!=null && dati.getRispostaIngressoBytes()>0){
				bandaEsterna = bandaEsterna + dati.getRispostaIngressoBytes().longValue();
			}
			break;
		default:
			if(dati.getRichiestaIngressoBytes()!=null && dati.getRichiestaIngressoBytes()>0){
				bandaEsterna = bandaEsterna + dati.getRichiestaIngressoBytes().longValue();
			}
			if(dati.getRispostaUscitaBytes()!=null && dati.getRispostaUscitaBytes()>0){
				bandaEsterna = bandaEsterna + dati.getRispostaUscitaBytes().longValue();
			}
			
			if(dati.getRichiestaUscitaBytes()!=null && dati.getRichiestaUscitaBytes()>0){
				bandaInterna = bandaInterna + dati.getRichiestaUscitaBytes().longValue();
			}
			if(dati.getRispostaIngressoBytes()!=null && dati.getRispostaIngressoBytes()>0){
				bandaInterna = bandaInterna + dati.getRispostaIngressoBytes().longValue();
			}
			break;
		}
		
		
		switch (tipoBanda) {
		case COMPLESSIVA:
			return bandaEsterna + bandaInterna;
		case INTERNA:
			return bandaInterna;
		case ESTERNA:
			return bandaEsterna;
		}
		return 0;
	}
	
	

	
	// ****** GETTER ******

	public Date getCloneDate() {
		return this.cloneDate;
	}
	
	public TipoRisorsa getTipoRisorsa() {
		return this.tipoRisorsa;
	}
	
	public Long getActiveRequestCounter() {
		return getActiveRequestCounter(false);
	}
	protected Long getActiveRequestCounter(boolean readRemoteInfo) {
		return this.activeRequestCounter;
	}
	public Date getCreationDate() {
		return this.creationDate;
	}
	public Date getUpdatePolicyDate() {
		return this.updatePolicyDate;
	}
	public Date getGestorePolicyConfigDate() {
		return this.gestorePolicyConfigDate;
	}
	
	@Deprecated
	public Long getManuallyResetPolicyTimeMillis() {
		return this.manuallyResetPolicyTimeMillis;
	}

	public Date getPolicyDate() {
		return this.policyDate;
	}	
	public Long getPolicyRequestCounter() {
		return getPolicyRequestCounter(false);
	}
	protected Long getPolicyRequestCounter(boolean readRemoteInfo) {
		return this.policyRequestCounter;
	}
	public Long getPolicyCounter() {
		return getPolicyCounter(false);
	}
	protected Long getPolicyCounter(boolean readRemoteInfo) {
		return this.policyCounter;
	}
	@Deprecated
	public Long getPolicyDenyRequestCounter() {
		return this.policyDenyRequestCounter;
	}
	@Deprecated
	public Long getPolicyDegradoPrestazionaleRequestCounter() {
		return this.policyDegradoPrestazionaleRequestCounter;
	}
	@Deprecated
	public Long getPolicyDegradoPrestazionaleCounter() {
		return this.policyDegradoPrestazionaleCounter;
	}
	public Double getPolicyAvgValue(){
		return getPolicyAvgValue(false);
	}
	protected Double getPolicyAvgValue(boolean readRemoteInfo){
		Double doubleValue = null;
		Long getPolicyCounter = getPolicyCounter(readRemoteInfo);
		Long getPolicyRequestCounter = getPolicyRequestCounter(readRemoteInfo);
		if(getPolicyCounter!=null && getPolicyRequestCounter!=null){
			double c = getPolicyCounter.doubleValue();
			double n = getPolicyRequestCounter.doubleValue();
			doubleValue = c/n;
		}
		return doubleValue;
	}
	protected Long getPolicyDenyRequestCounter(boolean readRemoteInfo) {
		return this.policyDenyRequestCounter;
	}
	public Date getLeftDateWindowCurrentInterval() {
		if(this.policyDate!=null && this.policyDateTypeInterval!=null){
			return DateUtils.convertToLeftInterval(this.policyDate, this.policyDateTypeInterval);
		}
		return null;
	}
	public Date getRightDateWindowCurrentInterval() {
		if(this.policyDate!=null && this.policyDateTypeInterval!=null && this.policyDateInterval!=null && this.policyDateCurrentInterval!=null){
			Date d = DateUtils.convertToRightInterval(this.policyDate, this.policyDateTypeInterval);
			return DatiCollezionati.incrementDate(d, this.policyDateTypeInterval, this.policyDateInterval, this.policyDateCurrentInterval);
		}
		return null;
	}	
	
	public Date getOldPolicyDate() {
		return this.oldPolicyDate;
	}
	public Long getOldPolicyRequestCounter() {
		return this.oldPolicyRequestCounter;
	}
	public Long getOldPolicyCounter() {
		return this.oldPolicyCounter;
	}
	public Double getOldPolicyAvgValue(){
		Double doubleValue = null;
		if(this.oldPolicyCounter!=null && this.oldPolicyRequestCounter!=null){
			double c = this.oldPolicyCounter.doubleValue();
			double n = this.oldPolicyRequestCounter.doubleValue();
			doubleValue = c/n;
		}
		return doubleValue;
	}
	public Date getLeftDateWindowPrecedentInterval() {
		if(this.oldPolicyDate!=null && this.policyDateTypeInterval!=null){
			return DateUtils.convertToLeftInterval(this.oldPolicyDate, this.policyDateTypeInterval);
		}
		return null;
	}
	public Date getRightDateWindowPrecedentInterval() {
		if(this.oldPolicyDate!=null && this.policyDateTypeInterval!=null && this.policyDateInterval!=null && this.policyDateCurrentInterval!=null){
			Date d = DateUtils.convertToRightInterval(this.oldPolicyDate, this.policyDateTypeInterval);
			return DatiCollezionati.incrementDate(d, this.policyDateTypeInterval, this.policyDateInterval, this.policyDateCurrentInterval);
		}
		return null;
	}	
		
	
	public Integer getPolicyDateInterval() {
		return this.policyDateInterval;
	}
	public Boolean getPolicyDateCurrentInterval() {
		return this.policyDateCurrentInterval;
	}
	public UnitaTemporale getPolicyDateTypeInterval() {
		return this.policyDateTypeInterval;
	}
	public TipoFinestra getPolicyDateWindowInterval() {
		return this.policyDateWindowInterval;
	}
	public Boolean getPolicyRealtime() {
		return this.policyRealtime;
	}
	
	public Date getLeftDateWindowSlidingInterval(Date now) {
		if(this.policyDateTypeInterval!=null && this.policyDateInterval!=null && this.policyDateCurrentInterval!=null){
			Date d = DateUtils.convertToLeftInterval(now, this.policyDateTypeInterval);
			return DatiCollezionati.decrementDate(d, this.policyDateTypeInterval, this.policyDateInterval, this.policyDateCurrentInterval);
		}
		return null;
	}
	public Date getRightDateWindowSlidingInterval(Date now) {
		if(this.policyDateTypeInterval!=null && this.policyDateCurrentInterval!=null){
			Date d = DateUtils.convertToRightInterval(now, this.policyDateTypeInterval);
			if(this.policyDateCurrentInterval==false){
				d = DateUtils.incrementDate(d, this.policyDateTypeInterval, -1);
			}
			return d;
		}
		return null;
	}
	
		
	public Date getPolicyDegradoPrestazionaleDate() {
		return this.policyDegradoPrestazionaleDate;
	}
	protected Long getPolicyDegradoPrestazionaleRequestCounter(boolean readRemoteInfo) {
		return this.policyDegradoPrestazionaleRequestCounter;
	}
	protected Long getPolicyDegradoPrestazionaleCounter(boolean readRemoteInfo) {
		return this.policyDegradoPrestazionaleCounter;
	}
	public Double getPolicyDegradoPrestazionaleAvgValue(){
		return getPolicyDegradoPrestazionaleAvgValue(false);
	}
	protected Double getPolicyDegradoPrestazionaleAvgValue(boolean readRemoteInfo){
		Double doubleValue = null;
		Long getPolicyDegradoPrestazionaleRequestCounter = getPolicyDegradoPrestazionaleRequestCounter(readRemoteInfo);
		Long getPolicyDegradoPrestazionaleCounter = getPolicyDegradoPrestazionaleCounter(readRemoteInfo);
		if(getPolicyDegradoPrestazionaleCounter!=null && getPolicyDegradoPrestazionaleRequestCounter!=null){
			double c = getPolicyDegradoPrestazionaleCounter.doubleValue();
			double n = getPolicyDegradoPrestazionaleRequestCounter.doubleValue();
			doubleValue = c/n;
		}
		return doubleValue;
	}
	public Date getDegradoPrestazionaleLeftDateWindowCurrentInterval() {
		if(this.policyDegradoPrestazionaleDate!=null && this.policyDegradoPrestazionaleDateTypeInterval!=null){
			return DateUtils.convertToLeftInterval(this.policyDegradoPrestazionaleDate, this.policyDegradoPrestazionaleDateTypeInterval);
		}
		return null;
	}
	public Date getDegradoPrestazionaleRightDateWindowCurrentInterval() {
		if(this.policyDegradoPrestazionaleDate!=null && this.policyDegradoPrestazionaleDateTypeInterval!=null && 
				this.policyDegradoPrestazionaleDateInterval!=null && this.policyDegradoPrestazionaleDateCurrentInterval!=null){
			Date d = DateUtils.convertToRightInterval(this.policyDegradoPrestazionaleDate, this.policyDegradoPrestazionaleDateTypeInterval);
			return DatiCollezionati.incrementDate(d, this.policyDegradoPrestazionaleDateTypeInterval, this.policyDegradoPrestazionaleDateInterval, this.policyDegradoPrestazionaleDateCurrentInterval);
		}
		return null;
	}	
	public Date getOldPolicyDegradoPrestazionaleDate() {
		return this.oldPolicyDegradoPrestazionaleDate;
	}
	public Long getOldPolicyDegradoPrestazionaleRequestCounter() {
		return this.oldPolicyDegradoPrestazionaleRequestCounter;
	}
	public Long getOldPolicyDegradoPrestazionaleCounter() {
		return this.oldPolicyDegradoPrestazionaleCounter;
	}
	public Double getOldPolicyDegradoPrestazionaleAvgValue(){
		Double doubleValue = null;
		if(this.oldPolicyDegradoPrestazionaleCounter!=null && this.oldPolicyDegradoPrestazionaleRequestCounter!=null){
			double c = this.oldPolicyDegradoPrestazionaleCounter.doubleValue();
			double n = this.oldPolicyDegradoPrestazionaleRequestCounter.doubleValue();
			doubleValue = c/n;
		}
		return doubleValue;
	}
	public Date getDegradoPrestazionaleLeftDateWindowPrecedentInterval() {
		if(this.oldPolicyDegradoPrestazionaleDate!=null && this.policyDegradoPrestazionaleDateTypeInterval!=null){
			return DateUtils.convertToLeftInterval(this.oldPolicyDegradoPrestazionaleDate, this.policyDegradoPrestazionaleDateTypeInterval);
		}
		return null;
	}
	public Date getDegradoPrestazionaleRightDateWindowPrecedentInterval() {
		if(this.oldPolicyDegradoPrestazionaleDate!=null && this.policyDegradoPrestazionaleDateTypeInterval!=null && 
				this.policyDegradoPrestazionaleDateInterval!=null && this.policyDegradoPrestazionaleDateCurrentInterval!=null){
			Date d = DateUtils.convertToRightInterval(this.oldPolicyDegradoPrestazionaleDate, this.policyDegradoPrestazionaleDateTypeInterval);
			return DatiCollezionati.incrementDate(d, this.policyDegradoPrestazionaleDateTypeInterval, this.policyDegradoPrestazionaleDateInterval, this.policyDegradoPrestazionaleDateCurrentInterval);
		}
		return null;
	}
		
	public Integer getPolicyDegradoPrestazionaleDateInterval() {
		return this.policyDegradoPrestazionaleDateInterval;
	}
	public Boolean getPolicyDegradoPrestazionaleDateCurrentInterval() {
		return this.policyDegradoPrestazionaleDateCurrentInterval;
	}
	public UnitaTemporale getPolicyDegradoPrestazionaleDateTypeInterval() {
		return this.policyDegradoPrestazionaleDateTypeInterval;
	}
	public TipoFinestra getPolicyDegradoPrestazionaleDateWindowInterval() {
		return this.policyDegradoPrestazionaleDateWindowInterval;
	}
	public Boolean getPolicyDegradoPrestazionaleRealtime() {
		return this.policyDegradoPrestazionaleRealtime;
	}

	
	public Date getDegradoPrestazionaleLeftDateWindowSlidingInterval(Date now) {
		if(this.policyDegradoPrestazionaleDateTypeInterval!=null && this.policyDegradoPrestazionaleDateInterval!=null && this.policyDegradoPrestazionaleDateCurrentInterval!=null){
			Date d = DateUtils.convertToLeftInterval(now, this.policyDegradoPrestazionaleDateTypeInterval);
			return DatiCollezionati.decrementDate(d, this.policyDegradoPrestazionaleDateTypeInterval, this.policyDegradoPrestazionaleDateInterval, this.policyDegradoPrestazionaleDateCurrentInterval);
		}
		return null;
	}
	public Date getDegradoPrestazionaleRightDateWindowSlidingInterval(Date now) {
		if(this.policyDegradoPrestazionaleDateTypeInterval!=null && this.policyDegradoPrestazionaleDateCurrentInterval!=null){
			Date d = DateUtils.convertToRightInterval(now, this.policyDegradoPrestazionaleDateTypeInterval);
			if(this.policyDegradoPrestazionaleDateCurrentInterval==false){
				d = DateUtils.incrementDate(d, this.policyDegradoPrestazionaleDateTypeInterval, -1);
			}
			return d;
		}
		return null;
	}
	
	

	
	
	// ****** SETTER DEPRECATI ******

	@Deprecated
	public void setTipoRisorsa(TipoRisorsa tipoRisorsa) {
		this.tipoRisorsa = tipoRisorsa;
	}

	public void initActiveRequestCounter() {
		this.activeRequestCounter = 0l;
	}
	@Deprecated
	public void setActiveRequestCounter(long activeRequestCounter) {
		this.activeRequestCounter = activeRequestCounter;
	}
	public long setAndGetActiveRequestCounter(long activeRequestCounter) {
		this.activeRequestCounter = activeRequestCounter;
		return this.activeRequestCounter;
	}
	@Deprecated
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	@Deprecated
	public void setUpdatePolicyDate(Date updatePolicyDate) {
		this.updatePolicyDate = updatePolicyDate;
	}

	@Deprecated
	public void setPolicyDateInterval(Integer policyDateInterval) {
		this.policyDateInterval = policyDateInterval;
	}
	@Deprecated
	public void setPolicyDateCurrentInterval(Boolean policyDateCurrentInterval) {
		this.policyDateCurrentInterval = policyDateCurrentInterval;
	}
	@Deprecated
	public void setPolicyDateTypeInterval(UnitaTemporale policyDateTypeInterval) {
		this.policyDateTypeInterval = policyDateTypeInterval;
	}
	@Deprecated
	public void setPolicyDateWindowInterval(TipoFinestra policyDateWindowInterval) {
		this.policyDateWindowInterval = policyDateWindowInterval;
	}
	@Deprecated
	public void setPolicyRealtime(Boolean policyRealtime) {
		this.policyRealtime = policyRealtime;
	}
	@Deprecated
	public void setPolicyDate(Date policyDate) {
		this.policyDate = policyDate;
	}
	@Deprecated
	public void setPolicyRequestCounter(Long policyRequestCounter) {
		this.policyRequestCounter = policyRequestCounter;
	}
	public Long setAndGetPolicyRequestCounter(Long policyRequestCounter) {
		this.policyRequestCounter = policyRequestCounter;
		return this.policyRequestCounter;
	}
	@Deprecated
	public void setPolicyCounter(Long policyCounter) {
		this.policyCounter = policyCounter;
	}
	@Deprecated
	public void setPolicyDenyRequestCounter(Long policyDenyRequestCounter) {
		this.policyDenyRequestCounter = policyDenyRequestCounter;
	}
	
	@Deprecated
	public void setOldPolicyDate(Date oldPolicyDate) {
		this.oldPolicyDate = oldPolicyDate;
	}
	@Deprecated
	public void setOldPolicyRequestCounter(Long oldPolicyRequestCounter) {
		this.oldPolicyRequestCounter = oldPolicyRequestCounter;
	}
	@Deprecated
	public void setOldPolicyCounter(Long oldPolicyCounter) {
		this.oldPolicyCounter = oldPolicyCounter;
	}

	@Deprecated
	public void setPolicyDegradoPrestazionaleDateInterval(Integer policyDegradoPrestazionaleDateInterval) {
		this.policyDegradoPrestazionaleDateInterval = policyDegradoPrestazionaleDateInterval;
	}
	@Deprecated
	public void setPolicyDegradoPrestazionaleDateCurrentInterval(Boolean policyDegradoPrestazionaleDateCurrentInterval) {
		this.policyDegradoPrestazionaleDateCurrentInterval = policyDegradoPrestazionaleDateCurrentInterval;
	}
	@Deprecated
	public void setPolicyDegradoPrestazionaleDateTypeInterval(UnitaTemporale policyDegradoPrestazionaleDateTypeInterval) {
		this.policyDegradoPrestazionaleDateTypeInterval = policyDegradoPrestazionaleDateTypeInterval;
	}
	@Deprecated
	public void setPolicyDegradoPrestazionaleDateWindowInterval(TipoFinestra policyDegradoPrestazionaleDateWindowInterval) {
		this.policyDegradoPrestazionaleDateWindowInterval = policyDegradoPrestazionaleDateWindowInterval;
	}
	@Deprecated
	public void setPolicyDegradoPrestazionaleRealtime(Boolean policyDegradoPrestazionaleRealtime) {
		this.policyDegradoPrestazionaleRealtime = policyDegradoPrestazionaleRealtime;
	}
	
	@Deprecated
	public void setPolicyDegradoPrestazionaleDate(Date policyDegradoPrestazionaleDate) {
		this.policyDegradoPrestazionaleDate = policyDegradoPrestazionaleDate;
	}
	@Deprecated
	public void setPolicyDegradoPrestazionaleRequestCounter(Long policyDegradoPrestazionaleRequestCounter) {
		this.policyDegradoPrestazionaleRequestCounter = policyDegradoPrestazionaleRequestCounter;
	}
	@Deprecated
	public void setPolicyDegradoPrestazionaleCounter(Long policyDegradoPrestazionaleCounter) {
		this.policyDegradoPrestazionaleCounter = policyDegradoPrestazionaleCounter;
	}
	@Deprecated
	public void setOldPolicyDegradoPrestazionaleDate(Date oldPolicyDegradoPrestazionaleDate) {
		this.oldPolicyDegradoPrestazionaleDate = oldPolicyDegradoPrestazionaleDate;
	}
	@Deprecated
	public void setOldPolicyDegradoPrestazionaleRequestCounter(Long oldPolicyDegradoPrestazionaleRequestCounter) {
		this.oldPolicyDegradoPrestazionaleRequestCounter = oldPolicyDegradoPrestazionaleRequestCounter;
	}
	@Deprecated
	public void setOldPolicyDegradoPrestazionaleCounter(Long oldPolicyDegradoPrestazionaleCounter) {
		this.oldPolicyDegradoPrestazionaleCounter = oldPolicyDegradoPrestazionaleCounter;
	}


	



	
	// **** UTILITIES ****
		
	public static String serialize(DatiCollezionati dati){
		StringBuilder bf = new StringBuilder();
		
		// data di lettura delle informazioni
		if(dati.cloneDate!=null)
			bf.append(dati.cloneDate.getTime());
		else
			bf.append("-");
		bf.append("\n");
		
		
		// tipo di risorsa
		if(dati.tipoRisorsa!=null)
			bf.append(dati.tipoRisorsa.getValue());
		else
			bf.append("-");
		bf.append("\n");
		
		
		// threads
		Long getActiveRequestCounter = dati.getActiveRequestCounter(true);
		if(getActiveRequestCounter != null)
			bf.append(getActiveRequestCounter);
		else
			bf.append("-");
		bf.append("\n");
		
		
		// data di creazione
		if(dati.creationDate!=null)
			bf.append(dati.creationDate.getTime());
		else
			bf.append("-");
		bf.append("\n");
		
		
		// dati iniziali
		if(dati.policyDateTypeInterval!=null)
			bf.append(dati.policyDateTypeInterval.getValue());
		else
			bf.append("-");
		bf.append("\n");
		if(dati.policyDateInterval!=null)
			bf.append(dati.policyDateInterval);
		else
			bf.append("-");
		bf.append("\n");
		if(dati.policyDateCurrentInterval!=null)
			bf.append(dati.policyDateCurrentInterval);
		else
			bf.append("-");
		bf.append("\n");
		if(dati.policyDateWindowInterval!=null)
			bf.append(dati.policyDateWindowInterval.getValue());
		else
			bf.append("-");
		bf.append("\n");
		if(dati.policyRealtime!=null)
			bf.append(dati.policyRealtime);
		else
			bf.append("-");
		bf.append("\n");


		// dati dinamici
		if(dati.policyDate!=null)
			bf.append(dati.policyDate.getTime());
		else
			bf.append("-");
		bf.append("\n");
		Long getPolicyRequestCounter = dati.getPolicyRequestCounter(true);
		if(getPolicyRequestCounter!=null)
			bf.append(getPolicyRequestCounter);
		else
			bf.append("-");
		bf.append("\n");
		Long getPolicyCounter = dati.getPolicyCounter(true);
		if(getPolicyCounter!=null)
			bf.append(getPolicyCounter);
		else
			bf.append("-");
		bf.append("\n");
		Long getPolicyDenyRequestCounter = dati.getPolicyDenyRequestCounter(true);
		if(getPolicyDenyRequestCounter!=null)
			bf.append(getPolicyDenyRequestCounter);
		else
			bf.append("-");
		bf.append("\n");
		if(dati.oldPolicyDate!=null)
			bf.append(dati.oldPolicyDate.getTime());
		else
			bf.append("-");
		bf.append("\n");
		if(dati.oldPolicyRequestCounter!=null)
			bf.append(dati.oldPolicyRequestCounter);
		else
			bf.append("-");
		bf.append("\n");
		if(dati.oldPolicyCounter!=null)
			bf.append(dati.oldPolicyCounter);
		else
			bf.append("-");
		bf.append("\n");

		
		// dati iniziali degrado prestazionale
		if(dati.policyDegradoPrestazionaleDateTypeInterval!=null)
			bf.append(dati.policyDegradoPrestazionaleDateTypeInterval.getValue());
		else
			bf.append("-");
		bf.append("\n");
		if(dati.policyDegradoPrestazionaleDateInterval!=null)
			bf.append(dati.policyDegradoPrestazionaleDateInterval);
		else
			bf.append("-");
		bf.append("\n");
		if(dati.policyDegradoPrestazionaleDateCurrentInterval!=null)
			bf.append(dati.policyDegradoPrestazionaleDateCurrentInterval);
		else
			bf.append("-");
		bf.append("\n");
		if(dati.policyDegradoPrestazionaleDateWindowInterval!=null)
			bf.append(dati.policyDegradoPrestazionaleDateWindowInterval.getValue());
		else
			bf.append("-");
		bf.append("\n");
		if(dati.policyDegradoPrestazionaleRealtime!=null)
			bf.append(dati.policyDegradoPrestazionaleRealtime);
		else
			bf.append("-");
		bf.append("\n");


		// dati dinamici degrado prestazionale
		if(dati.policyDegradoPrestazionaleDate!=null)
			bf.append(dati.policyDegradoPrestazionaleDate.getTime());
		else
			bf.append("-");
		bf.append("\n");
		Long getPolicyDegradoPrestazionaleRequestCounter = dati.getPolicyDegradoPrestazionaleRequestCounter(true);
		if(getPolicyDegradoPrestazionaleRequestCounter!=null)
			bf.append(getPolicyDegradoPrestazionaleRequestCounter);
		else
			bf.append("-");
		bf.append("\n");
		Long getPolicyDegradoPrestazionaleCounter = dati.getPolicyDegradoPrestazionaleCounter(true);
		if(getPolicyDegradoPrestazionaleCounter!=null)
			bf.append(getPolicyDegradoPrestazionaleCounter);
		else
			bf.append("-");
		bf.append("\n");
		if(dati.oldPolicyDegradoPrestazionaleDate!=null)
			bf.append(dati.oldPolicyDegradoPrestazionaleDate.getTime());
		else
			bf.append("-");
		bf.append("\n");
		if(dati.oldPolicyDegradoPrestazionaleRequestCounter!=null)
			bf.append(dati.oldPolicyDegradoPrestazionaleRequestCounter);
		else
			bf.append("-");
		bf.append("\n");
		if(dati.oldPolicyDegradoPrestazionaleCounter!=null)
			bf.append(dati.oldPolicyDegradoPrestazionaleCounter);
		else
			bf.append("-");

		
		// data di registrazione/aggiornamento policy
		bf.append("\n");
		if(dati.updatePolicyDate!=null)
			bf.append(dati.updatePolicyDate.getTime());
		else
			bf.append("-");
		
		// data di configurazione gestore policy
		bf.append("\n");
		if(dati.gestorePolicyConfigDate!=null)
			bf.append(dati.gestorePolicyConfigDate.getTime());
		else
			bf.append("-");
		
		
		return bf.toString();
	}
	
	public static DatiCollezionati deserialize(String s) throws Exception{
		DatiCollezionati dati = new DatiCollezionati(null, null);
		String [] tmp = s.split("\n");
		if(tmp==null){
			throw new Exception("Wrong Format");
		}
		int oldLength = 27;
		int dataPolicyLength = oldLength +1;
		int dataConfigPolicyLength = dataPolicyLength +1;
		if(tmp.length!=oldLength && tmp.length!=dataPolicyLength && tmp.length!=dataConfigPolicyLength){
			throw new Exception("Wrong Format (size: "+tmp.length+")");
		}
		for (int i = 0; i < tmp.length; i++) {
			
			// data di lettura delle informazioni
			if(i==0){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.cloneDate = new Date(Long.parseLong(tmpValue));
				}
			}
			
			// tipo di risorsa
			else if(i==1){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.tipoRisorsa = TipoRisorsa.toEnumConstant(tmpValue);
				}
			}
			
			// threads
			else if(i==2){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.activeRequestCounter = Long.parseLong(tmpValue);
				}
			}
			
			// data di creazione
			else if(i==3){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.creationDate = new Date(Long.parseLong(tmpValue));
				}
			}
			
			
			// dati iniziali
			else if(i==4){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDateTypeInterval = UnitaTemporale.toEnumConstant(tmpValue);
				}
			}
			else if(i==5){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDateInterval = Integer.parseInt(tmpValue);
				}
			}
			else if(i==6){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDateCurrentInterval = Boolean.parseBoolean(tmpValue);
				}
			}
			else if(i==7){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDateWindowInterval = TipoFinestra.toEnumConstant(tmpValue);
				}
			}
			else if(i==8){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyRealtime = Boolean.parseBoolean(tmpValue);
				}
			}
			
			// dati dinamici
			else if(i==9){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDate = new Date(Long.parseLong(tmpValue));
				}
			}
			else if(i==10){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyRequestCounter = Long.parseLong(tmpValue);
				}
			}
			else if(i==11){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyCounter = Long.parseLong(tmpValue);
				}
			}
			else if(i==12){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDenyRequestCounter = Long.parseLong(tmpValue);
				}
			}
			else if(i==13){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.oldPolicyDate = new Date(Long.parseLong(tmpValue));
				}
			}
			else if(i==14){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.oldPolicyRequestCounter = Long.parseLong(tmpValue);
				}
			}
			else if(i==15){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.oldPolicyCounter = Long.parseLong(tmpValue);
				}
			}
			
			// dati iniziali degrado prestazionale
			else if(i==16){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDegradoPrestazionaleDateTypeInterval = UnitaTemporale.toEnumConstant(tmpValue);
				}
			}
			else if(i==17){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDegradoPrestazionaleDateInterval = Integer.parseInt(tmpValue);
				}
			}
			else if(i==18){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDegradoPrestazionaleDateCurrentInterval = Boolean.parseBoolean(tmpValue);
				}
			}
			else if(i==19){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDegradoPrestazionaleDateWindowInterval = TipoFinestra.toEnumConstant(tmpValue);
				}
			}
			else if(i==20){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDegradoPrestazionaleRealtime = Boolean.parseBoolean(tmpValue);
				}
			}
			
			// dati dinamici degrado prestazionale
			else if(i==21){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDegradoPrestazionaleDate = new Date(Long.parseLong(tmpValue));
				}
			}
			else if(i==22){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDegradoPrestazionaleRequestCounter = Long.parseLong(tmpValue);
				}
			}
			else if(i==23){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.policyDegradoPrestazionaleCounter = Long.parseLong(tmpValue);
				}
			}
			else if(i==24){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.oldPolicyDegradoPrestazionaleDate = new Date(Long.parseLong(tmpValue));
				}
			}
			else if(i==25){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.oldPolicyDegradoPrestazionaleRequestCounter = Long.parseLong(tmpValue);
				}
			}
			else if(i==26){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.oldPolicyDegradoPrestazionaleCounter = Long.parseLong(tmpValue);
				}
			}
			
			// data di registrazione/aggiornamento policy
			else if(i==27){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.updatePolicyDate = new Date(Long.parseLong(tmpValue));
				}
			}
			
			// data di configurazione gestore policy
			else if(i==28){
				String tmpValue = tmp[i].trim();
				if(tmpValue!=null && !"-".equals(tmpValue)){
					dati.gestorePolicyConfigDate = new Date(Long.parseLong(tmpValue));
				}
			}
		}
		
		if(dati.updatePolicyDate==null) {
			dati.updatePolicyDate = dati.creationDate; // per backward compatibility
		}
		
		return dati;
	}
	
	public static Long translateToKb(long counter) {
		return counter / 1024;
	}
	public static Long translateToSeconds(long counter) {
		return counter / 1000;
	}
	
	private static Date incrementDate(Date date,UnitaTemporale unitaTemporale,
			int intervallo, boolean consideraIntervalloAttuale) {
		
		int increment = 0;
		if(consideraIntervalloAttuale){
			if(intervallo>1){ // il fatto di riportare tutto allo ...59.999 vale già un intervallo se si deve considerare l'intervallo attuale.
				increment = (intervallo-1);
			}
		}
		else{
			increment = intervallo;
		}
		
		return DateUtils.incrementDate(date, unitaTemporale, increment);
	}
	
	private static Date decrementDate(Date date, UnitaTemporale unitaTemporale,
			int intervallo, boolean consideraIntervalloAttuale) {
		
		int increment = 0;
		if(consideraIntervalloAttuale){
			if(intervallo>1){ // il fatto di riportare tutto allo ...59.999 vale già un intervallo se si deve considerare l'intervallo attuale.
				increment = (intervallo-1);
			}
		}
		else{
			increment = intervallo;
		}
		if(increment!=0){
			increment = increment * (-1);
		}
		
		return DateUtils.incrementDate(date, unitaTemporale, increment);
	}
}
