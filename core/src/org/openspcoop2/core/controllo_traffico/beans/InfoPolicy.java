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
package org.openspcoop2.core.controllo_traffico.beans;

import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;


/**     
 * InfoPolicy
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InfoPolicy {

	private String idPolicy;
	private TipoRisorsa tipoRisorsa;
	private Long valore;
	private String descrizione;
	private boolean intervalloUtilizzaRisorseStatistiche;
	private boolean intervalloUtilizzaRisorseRealtime;
	private boolean degradoPrestazionaleUtilizzaRisorseStatistiche;
	private boolean degradoPrestazionaleUtilizzaRisorseRealtime;
	private boolean checkRichiesteSimultanee;
	
	
	public boolean isIntervalloUtilizzaRisorseStatistiche() {
		return this.intervalloUtilizzaRisorseStatistiche;
	}
	public void setIntervalloUtilizzaRisorseStatistiche(boolean intervalloUtilizzaRisorseStatistiche) {
		this.intervalloUtilizzaRisorseStatistiche = intervalloUtilizzaRisorseStatistiche;
	}
	public boolean isIntervalloUtilizzaRisorseRealtime() {
		return this.intervalloUtilizzaRisorseRealtime;
	}
	public void setIntervalloUtilizzaRisorseRealtime(boolean intervalloUtilizzaRisorseRealtime) {
		this.intervalloUtilizzaRisorseRealtime = intervalloUtilizzaRisorseRealtime;
	}
	public boolean isDegradoPrestazionaleUtilizzaRisorseStatistiche() {
		return this.degradoPrestazionaleUtilizzaRisorseStatistiche;
	}
	public void setDegradoPrestazionaleUtilizzaRisorseStatistiche(boolean degradoPrestazionaleUtilizzaRisorseStatistiche) {
		this.degradoPrestazionaleUtilizzaRisorseStatistiche = degradoPrestazionaleUtilizzaRisorseStatistiche;
	}
	public boolean isDegradoPrestazionaleUtilizzaRisorseRealtime() {
		return this.degradoPrestazionaleUtilizzaRisorseRealtime;
	}
	public void setDegradoPrestazionaleUtilizzaRisorseRealtime(boolean degradoPrestazionaleUtilizzaRisorseRealtime) {
		this.degradoPrestazionaleUtilizzaRisorseRealtime = degradoPrestazionaleUtilizzaRisorseRealtime;
	}
	public boolean isCheckRichiesteSimultanee() {
		return this.checkRichiesteSimultanee;
	}
	public void setCheckRichiesteSimultanee(boolean checkRichiesteSimultanee) {
		this.checkRichiesteSimultanee = checkRichiesteSimultanee;
	}
	public String getDescrizione() {
		return this.descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getIdPolicy() {
		return this.idPolicy;
	}
	public void setIdPolicy(String idPolicy) {
		this.idPolicy = idPolicy;
	}
	public TipoRisorsa getTipoRisorsa() {
		return this.tipoRisorsa;
	}
	public void setTipoRisorsa(TipoRisorsa tipoRisorsa) {
		this.tipoRisorsa = tipoRisorsa;
	}
	public Long getValore() {
		return this.valore;
	}
	public void setValore(Long valore) {
		this.valore = valore;
	}
	
}