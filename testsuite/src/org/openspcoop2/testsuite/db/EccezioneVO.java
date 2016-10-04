/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.testsuite.db;

/**
 * Eccezione salvata su database
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EccezioneVO {

	private String contestoCodifica;
	private String contestoCodificaSdk;
	
	private String codiceEccezione;
	private int codiceEccezioneSdk;
	private int subCodiceEccezioneSdk;
	
	private String rilevanza;
	private String rilevanzaSdk;
	
	private String posizione;
	
	
	public String getContestoCodifica() {
		return this.contestoCodifica;
	}
	public void setContestoCodifica(String contestoCodifica) {
		this.contestoCodifica = contestoCodifica;
	}
	public String getContestoCodificaSdk() {
		return this.contestoCodificaSdk;
	}
	public void setContestoCodificaSdk(String contestoCodificaSdk) {
		this.contestoCodificaSdk = contestoCodificaSdk;
	}
	public String getRilevanza() {
		return this.rilevanza;
	}
	public void setRilevanza(String rilevanza) {
		this.rilevanza = rilevanza;
	}
	public String getRilevanzaSdk() {
		return this.rilevanzaSdk;
	}
	public void setRilevanzaSdk(String rilevanzaSdk) {
		this.rilevanzaSdk = rilevanzaSdk;
	}
	public String getPosizione() {
		return this.posizione;
	}
	public void setPosizione(String posizione) {
		this.posizione = posizione;
	}
	
	public String getCodiceEccezione() {
		return this.codiceEccezione;
	}
	public void setCodiceEccezione(String codiceEccezione) {
		this.codiceEccezione = codiceEccezione;
	}
	public int getCodiceEccezioneSdk() {
		return this.codiceEccezioneSdk;
	}
	public void setCodiceEccezioneSdk(int codiceEccezioneSdk) {
		this.codiceEccezioneSdk = codiceEccezioneSdk;
	}
	public int getSubCodiceEccezioneSdk() {
		return this.subCodiceEccezioneSdk;
	}
	public void setSubCodiceEccezioneSdk(int subCodiceEccezioneSdk) {
		this.subCodiceEccezioneSdk = subCodiceEccezioneSdk;
	}
	
}
