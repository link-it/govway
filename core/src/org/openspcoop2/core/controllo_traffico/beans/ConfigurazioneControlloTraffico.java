/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.Iterator;
import java.util.Map;

/**
 * ConfigurazioneControlloTraffico 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneControlloTraffico extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean debug;
		
	private Map<String, int []> calcoloLatenzaPortaDelegataEsitiConsiderati;
	
	private Map<String, int []> calcoloLatenzaPortaApplicativaEsitiConsiderati;
	
	private boolean elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente;
		
	private Map<String, int []> esitiPolicyViolate;
	
	private boolean elaborazioneRealtime_incrementaSoloPolicyApplicabile;
	

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
				
		bf.append("debug="+this.debug);
		
		if(this.calcoloLatenzaPortaDelegataEsitiConsiderati!=null){
			bf.append(", ");
			Iterator<String> protocolli = this.calcoloLatenzaPortaDelegataEsitiConsiderati.keySet().iterator();
			while (protocolli.hasNext()) {
				String protocollo = (String) protocolli.next();
				int [] esiti = this.calcoloLatenzaPortaDelegataEsitiConsiderati.get(protocollo);
				bf.append("calcoloLatenzaPortaDelegataEsitiConsiderati(size)["+protocollo+"]="+esiti.length);
				if(esiti.length>0){
					bf.append(", calcoloLatenzaPortaDelegataEsitiConsiderati["+protocollo+"]:");
					for (int i = 0; i < esiti.length; i++) {
						if(i>0)
							bf.append("-");
						bf.append(esiti[i]);
					}
				}
			}
		}
		
		if(this.calcoloLatenzaPortaApplicativaEsitiConsiderati!=null){
			bf.append(", ");
			Iterator<String> protocolli = this.calcoloLatenzaPortaApplicativaEsitiConsiderati.keySet().iterator();
			while (protocolli.hasNext()) {
				String protocollo = (String) protocolli.next();
				int [] esiti = this.calcoloLatenzaPortaApplicativaEsitiConsiderati.get(protocollo);
				bf.append("calcoloLatenzaPortaApplicativaEsitiConsiderati(size)["+protocollo+"]="+esiti.length);
				if(esiti.length>0){
					bf.append(", calcoloLatenzaPortaApplicativaEsitiConsiderati["+protocollo+"]:");
					for (int i = 0; i < esiti.length; i++) {
						if(i>0)
							bf.append("-");
						bf.append(esiti[i]);
					}
				}
			}
		}
		
		bf.append(", ");
		bf.append("elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente="+this.elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente);
		
		if(this.esitiPolicyViolate!=null){
			bf.append(", ");
			Iterator<String> protocolli = this.esitiPolicyViolate.keySet().iterator();
			while (protocolli.hasNext()) {
				String protocollo = (String) protocolli.next();
				int [] esiti = this.esitiPolicyViolate.get(protocollo);
				bf.append("esitiPolicyViolate(size)["+protocollo+"]="+esiti.length);
				if(esiti.length>0){
					bf.append(", esitiPolicyViolate["+protocollo+"]:");
					for (int i = 0; i < esiti.length; i++) {
						if(i>0)
							bf.append("-");
						bf.append(esiti[i]);
					}
				}
			}
		}
		
		bf.append(", ");
		bf.append("elaborazioneRealtime_incrementaSoloPolicyApplicabile="+this.elaborazioneRealtime_incrementaSoloPolicyApplicabile);
		
		return bf.toString();
	}
	
	
	public boolean isDebug() {
		return this.debug;
	}


	public void setDebug(boolean debug) {
		this.debug = debug;
	}


	public Map<String, int []> getCalcoloLatenzaPortaDelegataEsitiConsiderati() {
		return this.calcoloLatenzaPortaDelegataEsitiConsiderati;
	}


	public void setCalcoloLatenzaPortaDelegataEsitiConsiderati(Map<String, int []> calcoloLatenzaPortaDelegataEsitiConsiderati) {
		this.calcoloLatenzaPortaDelegataEsitiConsiderati = calcoloLatenzaPortaDelegataEsitiConsiderati;
	}


	public Map<String, int []> getCalcoloLatenzaPortaApplicativaEsitiConsiderati() {
		return this.calcoloLatenzaPortaApplicativaEsitiConsiderati;
	}


	public void setCalcoloLatenzaPortaApplicativaEsitiConsiderati(Map<String, int []> calcoloLatenzaPortaApplicativaEsitiConsiderati) {
		this.calcoloLatenzaPortaApplicativaEsitiConsiderati = calcoloLatenzaPortaApplicativaEsitiConsiderati;
	}


	public boolean isElaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente() {
		return this.elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente;
	}


	public void setElaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente(
			boolean elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente) {
		this.elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente = elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente;
	}


	public Map<String, int []> getEsitiPolicyViolate() {
		return this.esitiPolicyViolate;
	}


	public void setEsitiPolicyViolate(Map<String, int []> esitiPolicyViolate) {
		this.esitiPolicyViolate = esitiPolicyViolate;
	}


	public boolean isElaborazioneRealtime_incrementaSoloPolicyApplicabile() {
		return this.elaborazioneRealtime_incrementaSoloPolicyApplicabile;
	}


	public void setElaborazioneRealtime_incrementaSoloPolicyApplicabile(
			boolean elaborazioneRealtime_incrementaSoloPolicyApplicabile) {
		this.elaborazioneRealtime_incrementaSoloPolicyApplicabile = elaborazioneRealtime_incrementaSoloPolicyApplicabile;
	}

}
