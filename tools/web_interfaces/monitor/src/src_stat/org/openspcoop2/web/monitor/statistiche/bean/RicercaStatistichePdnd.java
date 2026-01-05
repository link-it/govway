/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;

import org.openspcoop2.web.monitor.statistiche.constants.ModalitaRicercaStatistichePdnd;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;

/**
 * RicercaStatistichePdnd
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RicercaStatistichePdnd implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String value;
	private String icona;

	private ModalitaRicercaStatistichePdnd modalitaRicercaStatichePdnd;
	private boolean rendered;
	private String action;
	private int livello;
	
	public RicercaStatistichePdnd(String value, String label, ModalitaRicercaStatistichePdnd modalitaRicercaStatichePdnd, String icona) {
		this(value, label, modalitaRicercaStatichePdnd, true, icona, 1);
	}
	
	public RicercaStatistichePdnd(String value, String label, ModalitaRicercaStatistichePdnd modalitaRicercaStatichePdnd, String icona, int livello) {
		this(value, label, modalitaRicercaStatichePdnd, true, icona, livello);
	}
	
	public RicercaStatistichePdnd(String value, String label, ModalitaRicercaStatistichePdnd modalitaRicercaStatichePdnd, boolean rendered, String icona, int livello) {
		this.label = label;
		this.value = value;
		this.rendered = rendered;
		this.modalitaRicercaStatichePdnd = modalitaRicercaStatichePdnd;
		this.icona = icona;
		this.action = StatisticheCostanti.STATS_PDND_TRACING_NOME_ACTION_RICERCA;
		this.livello = livello;
	}

	public String getLabel() {
		return this.label;
	}

	public String getValue() {
		return this.value;
	}

	public boolean isRendered() {
		return this.rendered;
	}

	public String getIcona() {
		return this.icona;
	}
	
	public String getModalitaRicercaStatistichePdnd() {
		return this.modalitaRicercaStatichePdnd.getValue();
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public String action() {
		return this.action;
	}
	
	public void setLivello(int livello) {
		this.livello = livello;
	}
	public int getLivello() {
		return this.livello;
	}
}
