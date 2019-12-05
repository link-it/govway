/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.transazioni.mbean;

import java.io.Serializable;

/**
 * DiagnosticiBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DiagnosticiBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String idTransazione;
	private String idEgov;
	private String identificativoPorta;
	private String protocollo;
	private String nomeServizioApplicativo;
	private Boolean forceNomeServizioApplicativoNull;
	
	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
	
	public String getIdTransazione() {
		return this.idTransazione;
	}
	
	public String getIdentificativoPorta() {
		return this.identificativoPorta;
	}
	
	public void setIdentificativoPorta(String identificativoPorta) {
		this.identificativoPorta = identificativoPorta;
	}
	
	public String getIdEgov() {
		return this.idEgov;
	}
	
	public void setIdEgov(String idEgov) {
		this.idEgov = idEgov;
	}
	
	public String submit(){
		return "success";
	}

	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public String getNomeServizioApplicativo() {
		return this.nomeServizioApplicativo;
	}

	public void setNomeServizioApplicativo(String nomeServizioApplicativo) {
		this.nomeServizioApplicativo = nomeServizioApplicativo;
	}

	public Boolean getForceNomeServizioApplicativoNull() {
		return this.forceNomeServizioApplicativoNull;
	}

	public void setForceNomeServizioApplicativoNull(Boolean forceNomeServizioApplicativoNull) {
		this.forceNomeServizioApplicativoNull = forceNomeServizioApplicativoNull;
	}
	
}
