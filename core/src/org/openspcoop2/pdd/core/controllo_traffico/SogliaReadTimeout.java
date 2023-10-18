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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.io.Serializable;

/**
 * SogliaReadTimeout
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SogliaReadTimeout implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean configurazioneGlobale = false;
	private int sogliaMs = -1;
	private String idConfigurazione;
	private String configurazione;
	
	public boolean isConfigurazioneGlobale() {
		return this.configurazioneGlobale;
	}
	public void setConfigurazioneGlobale(boolean configurazioneGlobale) {
		this.configurazioneGlobale = configurazioneGlobale;
	}
	public String getConfigurazione() {
		return this.configurazione;
	}
	public void setConfigurazione(String configurazione) {
		this.configurazione = configurazione;
	}
	public int getSogliaMs() {
		return this.sogliaMs;
	}
	public void setSogliaMs(int sogliaMs) {
		this.sogliaMs = sogliaMs;
	}
	public String getIdConfigurazione() {
		return this.idConfigurazione;
	}
	public void setIdConfigurazione(String idConfigurazione) {
		this.idConfigurazione = idConfigurazione;
	}
}
