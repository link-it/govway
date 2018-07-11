/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.web.monitor.transazioni.bean;

import java.io.Serializable;

import org.openspcoop2.web.monitor.core.constants.ModalitaRicercaTransazioni;

/**
 * Storico
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author: apoli $
 * @version $Rev: 14213 $, $Date: 2018-06-25 12:27:04 +0200 (Mon, 25 Jun 2018) $
 *
 */
public class Storico implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String value;
	private ModalitaRicercaTransazioni modalitaRicercaStorico;
	private boolean rendered;
	
	public Storico(String value, String label, ModalitaRicercaTransazioni modalitaRicercaStorico) {
		this(value, label, modalitaRicercaStorico, true);
	}
	
	public Storico(String value, String label, ModalitaRicercaTransazioni modalitaRicercaStorico, boolean rendered) {
		this.label = label;
		this.value = value;
		this.rendered = rendered;
		this.modalitaRicercaStorico = modalitaRicercaStorico;
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
		switch (this.modalitaRicercaStorico) {
		case ANDAMENTO_TEMPORALE:
			return org.openspcoop2.web.monitor.core.constants.Costanti.ICONA_ANDAMENTO_TEMPORALE;
		case ID_APPLICATIVO:
			return org.openspcoop2.web.monitor.core.constants.Costanti.ICONA_ID_APPLICATIVO;
		case ID_MESSAGGIO:
			return org.openspcoop2.web.monitor.core.constants.Costanti.ICONA_ID_MESSAGGIO;
		case ID_TRANSAZIONE:
		default:
			return org.openspcoop2.web.monitor.core.constants.Costanti.ICONA_ID_TRANSAZIONE;
		}
	}
	
	public String getModalitaRicercaStorico() {
		return this.modalitaRicercaStorico.getValue();
	}
}
