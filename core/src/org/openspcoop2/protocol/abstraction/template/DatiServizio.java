/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.abstraction.template;

import java.io.Serializable;

/**     
 * DatiServizio
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiServizio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String uriAccordoServizioParteComune;
	private String portType;
	private String tipo;
	private String nome;
	private String endpoint;
	private IdSoggetto erogatore;
	private String tipologiaServizio;
	
	public String getTipologiaServizio() {
		return this.tipologiaServizio;
	}
	public void setTipologiaServizio(String tipologiaServizio) {
		this.tipologiaServizio = tipologiaServizio;
	}
	public String getUriAccordoServizioParteComune() {
		return this.uriAccordoServizioParteComune;
	}
	public void setUriAccordoServizioParteComune(String uriAccordoServizioParteComune) {
		this.uriAccordoServizioParteComune = uriAccordoServizioParteComune;
	}
	public String getPortType() {
		return this.portType;
	}
	public void setPortType(String portType) {
		this.portType = portType;
	}
	public String getTipo() {
		return this.tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEndpoint() {
		return this.endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public IdSoggetto getErogatore() {
		return this.erogatore;
	}
	public void setErogatore(IdSoggetto erogatore) {
		this.erogatore = erogatore;
	}
	
	
}
