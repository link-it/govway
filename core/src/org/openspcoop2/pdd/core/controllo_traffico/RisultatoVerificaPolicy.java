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
}
