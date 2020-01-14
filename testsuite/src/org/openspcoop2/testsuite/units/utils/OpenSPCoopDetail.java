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

package org.openspcoop2.testsuite.units.utils;

/**
 * OpenSPCoopDetail
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoopDetail {
	private String codice;
	private String descrizione;
	boolean checkDescrizioneTramiteMatchEsatto;
	
	public OpenSPCoopDetail(){}
	
	public OpenSPCoopDetail(String codice,String descrizione,boolean checkDescrizioneTramiteMatchEsatto){
		this.codice = codice;
		this.descrizione = descrizione;
		this.checkDescrizioneTramiteMatchEsatto = checkDescrizioneTramiteMatchEsatto;
	}
	
	public String getCodice() {
		return this.codice;
	}
	public void setCodice(String codice) {
		this.codice = codice;
	}
	public String getDescrizione() {
		return this.descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public boolean isCheckDescrizioneTramiteMatchEsatto() {
		return this.checkDescrizioneTramiteMatchEsatto;
	}
	public void setCheckDescrizioneTramiteMatchEsatto(
			boolean checkDescrizioneTramiteMatchEsatto) {
		this.checkDescrizioneTramiteMatchEsatto = checkDescrizioneTramiteMatchEsatto;
	}
}
