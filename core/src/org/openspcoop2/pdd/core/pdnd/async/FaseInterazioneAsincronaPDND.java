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

package org.openspcoop2.pdd.core.pdnd.async;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;

/**
 * Fasi di uno scambio di dati asincrono PDND; il valore corrisponde allo scope presente nei voucher emessi dalla PDND
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum FaseInterazioneAsincronaPDND {

	START_INTERACTION (CostantiDB.MODIPA_PDND_ASYNC_FASE_VALUE_START_INTERACTION, CostantiLabel.MODIPA_PDND_ASYNC_FASE_LABEL_START_INTERACTION),
	CALLBACK_INVOCATION (CostantiDB.MODIPA_PDND_ASYNC_FASE_VALUE_CALLBACK_INVOCATION, CostantiLabel.MODIPA_PDND_ASYNC_FASE_LABEL_CALLBACK_INVOCATION),
	GET_RESOURCE (CostantiDB.MODIPA_PDND_ASYNC_FASE_VALUE_GET_RESOURCE, CostantiLabel.MODIPA_PDND_ASYNC_FASE_LABEL_GET_RESOURCE),
	CONFIRMATION (CostantiDB.MODIPA_PDND_ASYNC_FASE_VALUE_CONFIRMATION, CostantiLabel.MODIPA_PDND_ASYNC_FASE_LABEL_CONFIRMATION);
	
	private final String valore;
	private final String label;
	
	FaseInterazioneAsincronaPDND(String valore, String label) {
		this.valore = valore;
		this.label = label;
	}
	
	public String getValore() {
		return this.valore;
	}
	public String getLabel() {
		return this.label;
	}
	public String getLabelConValore() {
		return this.label+" ("+this.valore+")";
	}
	
	@Override
	public String toString() {
		return this.valore;
	}
	
	public static FaseInterazioneAsincronaPDND toFase(String valore) {
		if(valore!=null) {
			for (FaseInterazioneAsincronaPDND fase : values()) {
				if(fase.valore.equals(valore)) {
					return fase;
				}
			}
		}
		return null;
	}
}
