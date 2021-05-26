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
package org.openspcoop2.core.controllo_traffico.beans;

import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoStatistico;
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
	private boolean builtIn;
	private TipoRisorsa tipoRisorsa;
	private Long valore;
	private Long valore2;
	private String descrizione;
	private boolean intervalloUtilizzaRisorseStatistiche;
	private boolean intervalloUtilizzaRisorseRealtime;
	private TipoPeriodoStatistico intervalloUtilizzaRisorseStatisticheTipoPeriodo;
	private TipoPeriodoRealtime intervalloUtilizzaRisorseRealtimeTipoPeriodo;
	private boolean controlloCongestione;
	private boolean degradoPrestazione;
	private boolean degradoPrestazionaleUtilizzaRisorseStatistiche;
	private boolean degradoPrestazionaleUtilizzaRisorseRealtime;
	private boolean checkRichiesteSimultanee;
	private boolean errorRate;
	
	
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
	public Long getValore2() {
		return this.valore2;
	}
	public void setValore2(Long valore2) {
		this.valore2 = valore2;
	}
	public boolean isControlloCongestione() {
		return this.controlloCongestione;
	}
	public void setControlloCongestione(boolean controlloCongestione) {
		this.controlloCongestione = controlloCongestione;
	}
	public boolean isDegradoPrestazione() {
		return this.degradoPrestazione;
	}
	public void setDegradoPrestazione(boolean degradoPrestazione) {
		this.degradoPrestazione = degradoPrestazione;
	}
	public boolean isErrorRate() {
		return this.errorRate;
	}
	public void setErrorRate(boolean errorRate) {
		this.errorRate = errorRate;
	}
	public TipoPeriodoStatistico getIntervalloUtilizzaRisorseStatisticheTipoPeriodo() {
		return this.intervalloUtilizzaRisorseStatisticheTipoPeriodo;
	}
	public void setIntervalloUtilizzaRisorseStatisticheTipoPeriodo(
			TipoPeriodoStatistico intervalloUtilizzaRisorseStatisticheTipoPeriodo) {
		this.intervalloUtilizzaRisorseStatisticheTipoPeriodo = intervalloUtilizzaRisorseStatisticheTipoPeriodo;
	}
	public TipoPeriodoRealtime getIntervalloUtilizzaRisorseRealtimeTipoPeriodo() {
		return this.intervalloUtilizzaRisorseRealtimeTipoPeriodo;
	}
	public void setIntervalloUtilizzaRisorseRealtimeTipoPeriodo(
			TipoPeriodoRealtime intervalloUtilizzaRisorseRealtimeTipoPeriodo) {
		this.intervalloUtilizzaRisorseRealtimeTipoPeriodo = intervalloUtilizzaRisorseRealtimeTipoPeriodo;
	}
	public boolean isBuiltIn() {
		return this.builtIn;
	}
	public void setBuiltIn(boolean builtIn) {
		this.builtIn = builtIn;
	}	
}