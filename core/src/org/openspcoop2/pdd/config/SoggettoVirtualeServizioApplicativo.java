/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.config;

import java.io.Serializable;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * SoggettoVirtualeServizioApplicativo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettoVirtualeServizioApplicativo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IDSoggetto idSoggettoReale;
	private String nomeServizioApplicativo;
	private PortaApplicativa portaApplicativa;
	private String id;
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public IDSoggetto getIdSoggettoReale() {
		return this.idSoggettoReale;
	}
	public void setIdSoggettoReale(IDSoggetto idSoggettoReale) {
		this.idSoggettoReale = idSoggettoReale;
	}
	public String getNomeServizioApplicativo() {
		return this.nomeServizioApplicativo;
	}
	public void setNomeServizioApplicativo(String nomeServizioApplicativo) {
		this.nomeServizioApplicativo = nomeServizioApplicativo;
	}
	public PortaApplicativa getPortaApplicativa() {
		return this.portaApplicativa;
	}
	public void setPortaApplicativa(PortaApplicativa portaApplicativa) {
		this.portaApplicativa = portaApplicativa;
	}
}
