/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
		
	private int [] calcoloLatenzaPortaDelegataEsitiConsiderati;
	
	private int [] calcoloLatenzaPortaApplicativaEsitiConsiderati;
	
	private boolean elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente;
		
	private int [] esitiPolicyViolate;
	
	private boolean elaborazioneRealtime_incrementaSoloPolicyApplicabile;
	

	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
				
		bf.append("debug="+this.debug);
		
		if(this.calcoloLatenzaPortaDelegataEsitiConsiderati!=null){
			bf.append(", ");
			bf.append("calcoloLatenzaPortaDelegataEsitiConsiderati(size)="+this.calcoloLatenzaPortaDelegataEsitiConsiderati.length);
			if(this.calcoloLatenzaPortaDelegataEsitiConsiderati.length>0){
				bf.append(", calcoloLatenzaPortaDelegataEsitiConsiderati:");
				for (int i = 0; i < this.calcoloLatenzaPortaDelegataEsitiConsiderati.length; i++) {
					if(i>0)
						bf.append("-");
					bf.append(this.calcoloLatenzaPortaDelegataEsitiConsiderati[i]);
				}
			}
		}
		
		if(this.calcoloLatenzaPortaApplicativaEsitiConsiderati!=null){
			bf.append(", ");
			bf.append("calcoloLatenzaPortaApplicativaEsitiConsiderati(size)="+this.calcoloLatenzaPortaApplicativaEsitiConsiderati.length);
			if(this.calcoloLatenzaPortaApplicativaEsitiConsiderati.length>0){
				bf.append(", calcoloLatenzaPortaApplicativaEsitiConsiderati:");
				for (int i = 0; i < this.calcoloLatenzaPortaApplicativaEsitiConsiderati.length; i++) {
					if(i>0)
						bf.append("-");
					bf.append(this.calcoloLatenzaPortaApplicativaEsitiConsiderati[i]);
				}
			}
		}
		
		bf.append(", ");
		bf.append("elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente="+this.elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente);
		
		if(this.esitiPolicyViolate!=null){
			bf.append(", ");
			bf.append("esitiPolicyViolate(size)="+this.esitiPolicyViolate.length);
			if(this.esitiPolicyViolate.length>0){
				bf.append(", esitiPolicyViolate:");
				for (int i = 0; i < this.esitiPolicyViolate.length; i++) {
					if(i>0)
						bf.append("-");
					bf.append(this.esitiPolicyViolate[i]);
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


	public int[] getCalcoloLatenzaPortaDelegataEsitiConsiderati() {
		return this.calcoloLatenzaPortaDelegataEsitiConsiderati;
	}


	public void setCalcoloLatenzaPortaDelegataEsitiConsiderati(int[] calcoloLatenzaPortaDelegataEsitiConsiderati) {
		this.calcoloLatenzaPortaDelegataEsitiConsiderati = calcoloLatenzaPortaDelegataEsitiConsiderati;
	}


	public int[] getCalcoloLatenzaPortaApplicativaEsitiConsiderati() {
		return this.calcoloLatenzaPortaApplicativaEsitiConsiderati;
	}


	public void setCalcoloLatenzaPortaApplicativaEsitiConsiderati(int[] calcoloLatenzaPortaApplicativaEsitiConsiderati) {
		this.calcoloLatenzaPortaApplicativaEsitiConsiderati = calcoloLatenzaPortaApplicativaEsitiConsiderati;
	}


	public boolean isElaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente() {
		return this.elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente;
	}


	public void setElaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente(
			boolean elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente) {
		this.elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente = elaborazioneStatistica_finestraScorrevole_gestioneIntervalloCorrente;
	}


	public int[] getEsitiPolicyViolate() {
		return this.esitiPolicyViolate;
	}


	public void setEsitiPolicyViolate(int[] esitiPolicyViolate) {
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
