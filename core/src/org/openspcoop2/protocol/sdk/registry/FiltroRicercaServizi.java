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

package org.openspcoop2.protocol.sdk.registry;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;

/**
 *  FiltroRicercaServizi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltroRicercaServizi {

	private String tipoServizio;
	private String nomeServizio;
	private Integer versioneServizio;
	private IDSoggetto soggettoErogatore;
	private ProtocolProperties protocolPropertiesServizi;

	public String getTipoServizio() {
		return this.tipoServizio;
	}
	public void setTipoServizio(String tipo) {
		this.tipoServizio = tipo;
	}
	public String getNomeServizio() {
		return this.nomeServizio;
	}
	public void setNomeServizio(String nome) {
		this.nomeServizio = nome;
	}
	public Integer getVersioneServizio() {
		return this.versioneServizio;
	}
	public void setVersioneServizio(Integer versioneServizio) {
		this.versioneServizio = versioneServizio;
	}
	public IDSoggetto getSoggettoErogatore() {
		return this.soggettoErogatore;
	}
	public void setSoggettoErogatore(IDSoggetto soggetto) {
		this.soggettoErogatore = soggetto;
	}
	public ProtocolProperties getProtocolPropertiesServizi() {
		return this.protocolPropertiesServizi;
	}
	public void setProtocolPropertiesServizi(ProtocolProperties protocolProperties) {
		this.protocolPropertiesServizi = protocolProperties;
	}
	
}
	

