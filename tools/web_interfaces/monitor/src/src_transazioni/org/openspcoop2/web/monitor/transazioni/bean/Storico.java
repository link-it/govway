/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.transazioni.bean;

import java.io.Serializable;

import org.openspcoop2.web.monitor.core.constants.ModalitaRicercaTransazioni;

/**
 * Storico
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Storico implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String value;
	private String icona;

	private ModalitaRicercaTransazioni modalitaRicercaStorico;
	private boolean rendered;
	
	public Storico(String value, String label, ModalitaRicercaTransazioni modalitaRicercaStorico, String icona) {
		this(value, label, modalitaRicercaStorico, true, icona);
	}
	
	public Storico(String value, String label, ModalitaRicercaTransazioni modalitaRicercaStorico, boolean rendered, String icona) {
		this.label = label;
		this.value = value;
		this.rendered = rendered;
		this.modalitaRicercaStorico = modalitaRicercaStorico;
		this.icona = icona;
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
	
	public String getModalitaRicercaStorico() {
		return this.modalitaRicercaStorico.getValue();
	}
}
