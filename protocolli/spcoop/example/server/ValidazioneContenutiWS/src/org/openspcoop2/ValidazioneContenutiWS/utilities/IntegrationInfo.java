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


package org.openspcoop2.ValidazioneContenutiWS.utilities;

/**
*
* @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class IntegrationInfo {

	/**
	 * 
	 */
	String idEGov = null;
	String tipoMittente = null;
	String mittente = null;
	String tipoDestinatario = null;
	String destinatario = null;
	String tipoServizio = null;
	String servizio = null;
	String azione = null;

	public String getIdEGov() {
		return this.idEGov;
	}

	public void setIdEGov(String idEGov) {
		this.idEGov = idEGov;
	}

	public String getAzione() {
		return this.azione;
	}

	public void setAzione(String azione) {
		this.azione = azione;
	}

	public String getDestinatario() {
		return this.destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public String getMittente() {
		return this.mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
	}

	public String getServizio() {
		return this.servizio;
	}

	public void setServizio(String servizio) {
		this.servizio = servizio;
	}

	public String getTipoDestinatario() {
		return this.tipoDestinatario;
	}

	public void setTipoDestinatario(String tipoDestinatario) {
		this.tipoDestinatario = tipoDestinatario;
	}

	public String getTipoMittente() {
		return this.tipoMittente;
	}

	public void setTipoMittente(String tipoMittente) {
		this.tipoMittente = tipoMittente;
	}

	public String getTipoServizio() {
		return this.tipoServizio;
	}

	public void setTipoServizio(String tipoServizio) {
		this.tipoServizio = tipoServizio;
	}
	
}
