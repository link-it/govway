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

package org.openspcoop2.pdd.config;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * SoggettoVirtualeServizioApplicativo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettoVirtualeServizioApplicativo {

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
