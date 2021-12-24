/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk.validator;

/**
 * ProprietaValidazioneErrori
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProprietaValidazioneErrori {

	private boolean ignoraEccezioniNonGravi;
	
	private String versioneProtocollo;

	public String getVersioneProtocollo() {
		return this.versioneProtocollo;
	}

	public void setVersioneProtocollo(String versioneProtocollo) {
		this.versioneProtocollo = versioneProtocollo;
	}

	public boolean isIgnoraEccezioniNonGravi() {
		return this.ignoraEccezioniNonGravi;
	}

	public void setIgnoraEccezioniNonGravi(boolean ignoraEccezioniNonGravi) {
		this.ignoraEccezioniNonGravi = ignoraEccezioniNonGravi;
	}
	
}
