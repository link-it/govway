/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;

/**     
 * RisultatoVerificaPolicy
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RisultatoVerificaPolicy {

	private TipoRisorsa risorsa;
	private boolean simultanee;
	
	private boolean violata;
	
	private boolean erroreGenerico;
	
	private boolean nonApplicabile;
	
	private String descrizione;
	
	private boolean applicabilitaCongestione;
	private boolean applicabilitaDegradoPrestazionale;
	private boolean applicabilitaStatoAllarme;
	
	private boolean warningOnly;
	
	private Long maxValue;
	private Long actualValue;
	private Long msBeforeResetCounters; // solo nel caso di finestra corrente
	private Long msWindow; // solo nel caso di finestra corrente
	
	public boolean isViolata() {
		return this.violata;
	}
	public void setViolata(boolean violata) {
		this.violata = violata;
	}
	public boolean isNonApplicabile() {
		return this.nonApplicabile;
	}
	public void setNonApplicabile(boolean nonApplicabile) {
		this.nonApplicabile = nonApplicabile;
	}
	public boolean isWarningOnly() {
		return this.warningOnly;
	}
	public void setWarningOnly(boolean warningOnly) {
		this.warningOnly = warningOnly;
	}
	public boolean isErroreGenerico() {
		return this.erroreGenerico;
	}
	public void setErroreGenerico(boolean erroreGenerico) {
		this.erroreGenerico = erroreGenerico;
	}
	public boolean isSimultanee() {
		return this.simultanee;
	}
	public void setSimultanee(boolean simultanee) {
		this.simultanee = simultanee;
	}
	public TipoRisorsa getRisorsa() {
		return this.risorsa;
	}
	public void setRisorsa(TipoRisorsa risorsa) {
		this.risorsa = risorsa;
	}
	public String getDescrizione() {
		return this.descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public boolean isApplicabilitaCongestione() {
		return this.applicabilitaCongestione;
	}
	public void setApplicabilitaCongestione(boolean applicabilitaCongestione) {
		this.applicabilitaCongestione = applicabilitaCongestione;
	}
	public boolean isApplicabilitaDegradoPrestazionale() {
		return this.applicabilitaDegradoPrestazionale;
	}
	public void setApplicabilitaDegradoPrestazionale(boolean applicabilitaDegradoPrestazionale) {
		this.applicabilitaDegradoPrestazionale = applicabilitaDegradoPrestazionale;
	}
	public boolean isApplicabilitaStatoAllarme() {
		return this.applicabilitaStatoAllarme;
	}
	public void setApplicabilitaStatoAllarme(boolean applicabilitaStatoAllarme) {
		this.applicabilitaStatoAllarme = applicabilitaStatoAllarme;
	}
	
	public Long getMaxValue() {
		return this.maxValue;
	}
	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}
	public Long getActualValue() {
		return this.actualValue;
	}
	public void setActualValue(Long actualValue) {
		this.actualValue = actualValue;
	}
	public Long getMsBeforeResetCounters() {
		return this.msBeforeResetCounters;
	}
	public void setMsBeforeResetCounters(Long secondBeforeResetCounters) {
		this.msBeforeResetCounters = secondBeforeResetCounters;
	}
	public Long getMsWindow() {
		return this.msWindow;
	}
	public void setMsWindow(Long msWindow) {
		this.msWindow = msWindow;
	}
}
