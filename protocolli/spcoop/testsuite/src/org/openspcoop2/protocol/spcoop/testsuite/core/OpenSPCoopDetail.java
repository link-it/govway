/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.protocol.spcoop.testsuite.core;

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
