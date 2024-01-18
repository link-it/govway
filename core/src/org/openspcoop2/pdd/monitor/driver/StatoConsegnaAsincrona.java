/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.monitor.driver;

import java.util.Date;

/**
 * StatoConsegne
 * 
 * @author Andrea Poli (apoli@link.it)
 *
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class StatoConsegnaAsincrona {

	protected static final String ALIAS_NOW = "tempoAttuale";
	protected static final String ALIAS_SERVIZIO_APPLICATIVO = "nomeSA";
	
	protected static final String ALIAS_IN_CODA = "inCoda";
	protected static final String ALIAS_IN_CODA_VECCHIO = "vecchioInCoda";
	protected static final String ALIAS_IN_CODA_RECENTE = "recenteInCoda";
	
	protected static final String ALIAS_IN_RICONSEGNA = "inRiconsegna";
	protected static final String ALIAS_IN_RICONSEGNA_VECCHIO = "vecchioInRiconsegna";
	protected static final String ALIAS_IN_RICONSEGNA_RECENTE = "recenteInRiconsegna";
	
	protected static final String ALIAS_IN_MESSAGE_BOX = "inMessageBox";
	protected static final String ALIAS_IN_MESSAGE_BOX_VECCHIO = "vecchioInMessageBox";
	protected static final String ALIAS_IN_MESSAGE_BOX_RECENTE = "recenteInMessageBox";
	
	private Date now;
	private String servizioApplicativo;
	
	private long inCoda;
	private Date vecchioInCoda;
	private Date recenteInCoda;
	
	private long inRiconsegna;
	private Date vecchioInRiconsegna;
	private Date recenteInRiconsegna;
	
	private long inMessageBox;
	private Date vecchioInMessageBox;
	private Date recenteInMessageBox;
	
	public Date getNow() {
		return this.now;
	}
	public void setNow(Date now) {
		this.now = now;
	}
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	public long getInCoda() {
		return this.inCoda;
	}
	public void setInCoda(long inCoda) {
		this.inCoda = inCoda;
	}
	public Date getVecchioInCoda() {
		return this.vecchioInCoda;
	}
	public void setVecchioInCoda(Date vecchioInCoda) {
		this.vecchioInCoda = vecchioInCoda;
	}
	public Date getRecenteInCoda() {
		return this.recenteInCoda;
	}
	public void setRecenteInCoda(Date recenteInCoda) {
		this.recenteInCoda = recenteInCoda;
	}
	public long getInRiconsegna() {
		return this.inRiconsegna;
	}
	public void setInRiconsegna(long inRiconsegna) {
		this.inRiconsegna = inRiconsegna;
	}
	public Date getVecchioInRiconsegna() {
		return this.vecchioInRiconsegna;
	}
	public void setVecchioInRiconsegna(Date vecchioInRiconsegna) {
		this.vecchioInRiconsegna = vecchioInRiconsegna;
	}
	public Date getRecenteInRiconsegna() {
		return this.recenteInRiconsegna;
	}
	public void setRecenteInRiconsegna(Date recenteInRiconsegna) {
		this.recenteInRiconsegna = recenteInRiconsegna;
	}
	public long getInMessageBox() {
		return this.inMessageBox;
	}
	public void setInMessageBox(long inMessageBox) {
		this.inMessageBox = inMessageBox;
	}
	public Date getVecchioInMessageBox() {
		return this.vecchioInMessageBox;
	}
	public void setVecchioInMessageBox(Date vecchioInMessageBox) {
		this.vecchioInMessageBox = vecchioInMessageBox;
	}
	public Date getRecenteInMessageBox() {
		return this.recenteInMessageBox;
	}
	public void setRecenteInMessageBox(Date recenteInMessageBox) {
		this.recenteInMessageBox = recenteInMessageBox;
	}
	
}
