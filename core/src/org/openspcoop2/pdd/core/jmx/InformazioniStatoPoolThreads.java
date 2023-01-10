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

package org.openspcoop2.pdd.core.jmx;

/**
 * InformazioniStatoPoolThreads
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniStatoPoolThreads {

	private String nomeCoda = null;
	private String stato = null;
	private String configurazione = null;
	private String connettoriPrioritari = null;
	private String applicativiPrioritari = null;
	
	public InformazioniStatoPoolThreads(String nomeCoda,String stato,String configurazione,String connettoriPrioritari){
		this.nomeCoda = nomeCoda;
		this.stato = stato;
		this.configurazione = configurazione;
		this.connettoriPrioritari = connettoriPrioritari;
	}
	
	public String getNomeCoda() {
		return this.nomeCoda;
	}

	public void setNomeCoda(String nomeCoda) {
		this.nomeCoda = nomeCoda;
	}

	public String getStato() {
		return this.stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getConfigurazione() {
		return this.configurazione;
	}

	public void setConfigurazione(String configurazione) {
		this.configurazione = configurazione;
	}

	public String getConnettoriPrioritari() {
		return this.connettoriPrioritari;
	}

	public void setConnettoriPrioritari(String connettoriPrioritari) {
		this.connettoriPrioritari = connettoriPrioritari;
	}
	
	public String getApplicativiPrioritari() {
		return this.applicativiPrioritari;
	}

	public void setApplicativiPrioritari(String applicativiPrioritari) {
		this.applicativiPrioritari = applicativiPrioritari;
	}
}
