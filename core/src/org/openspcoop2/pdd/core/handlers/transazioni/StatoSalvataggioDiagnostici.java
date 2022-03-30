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
package org.openspcoop2.pdd.core.handlers.transazioni;

import java.util.HashMap;

import org.openspcoop2.core.transazioni.constants.DiagnosticColumnType;

/**     
 * StatoSalvataggioDiagnostici
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatoSalvataggioDiagnostici {

	private String informazione;
	private HashMap<DiagnosticColumnType, String> informazioneCompressa = new HashMap<>();
	private boolean errore;
	private boolean compresso;
	
	public String getInformazione() {
		return this.informazione;
	}

	public void setInformazione(String informazione) {
		this.informazione = informazione;
	}

	public boolean isErrore() {
		return this.errore;
	}

	public void setErrore(boolean errore) {
		this.errore = errore;
	}

	public boolean isCompresso() {
		return this.compresso;
	}

	public void setCompresso(boolean compresso) {
		this.compresso = compresso;
	}
	
	public HashMap<DiagnosticColumnType, String> getInformazioneCompressa() {
		return this.informazioneCompressa;
	}

	public void setInformazioneCompressa(HashMap<DiagnosticColumnType, String> informazioneCompressa) {
		this.informazioneCompressa = informazioneCompressa;
	}
}
