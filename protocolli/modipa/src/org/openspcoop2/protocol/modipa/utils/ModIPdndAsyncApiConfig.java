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

package org.openspcoop2.protocol.modipa.utils;

import java.io.Serializable;

import org.openspcoop2.protocol.modipa.constants.ModICostanti;

/**
 * Configurazione dello scambio di dati asincrono PDND definita su un'API
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIPdndAsyncApiConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private boolean enabled = false;
	private String ruolo;
	private String uriApiCorrelata;
	private Long tempoMaxCallback;
	private Long tempoDisponibilita;
	private boolean confermaRicezione = false;
	private Long limiteEntita;
	
	public boolean isEnabled() {
		return this.enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getRuolo() {
		return this.ruolo;
	}
	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
	}
	public boolean isRuoloEService() {
		return this.enabled && (this.ruolo==null || ModICostanti.MODIPA_PDND_ASYNC_RUOLO_VALUE_ESERVICE.equals(this.ruolo));
	}
	public boolean isRuoloCallback() {
		return this.enabled && ModICostanti.MODIPA_PDND_ASYNC_RUOLO_VALUE_CALLBACK.equals(this.ruolo);
	}
	public String getUriApiCorrelata() {
		return this.uriApiCorrelata;
	}
	public void setUriApiCorrelata(String uriApiCorrelata) {
		this.uriApiCorrelata = uriApiCorrelata;
	}
	public Long getTempoMaxCallback() {
		return this.tempoMaxCallback;
	}
	public void setTempoMaxCallback(Long tempoMaxCallback) {
		this.tempoMaxCallback = tempoMaxCallback;
	}
	public Long getTempoDisponibilita() {
		return this.tempoDisponibilita;
	}
	public void setTempoDisponibilita(Long tempoDisponibilita) {
		this.tempoDisponibilita = tempoDisponibilita;
	}
	public boolean isConfermaRicezione() {
		return this.confermaRicezione;
	}
	public void setConfermaRicezione(boolean confermaRicezione) {
		this.confermaRicezione = confermaRicezione;
	}
	public Long getLimiteEntita() {
		return this.limiteEntita;
	}
	public void setLimiteEntita(Long limiteEntita) {
		this.limiteEntita = limiteEntita;
	}
}
