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


package org.openspcoop2.web.ctrlstat.dao;

import java.io.Serializable;

import org.openspcoop2.core.registry.PortaDominio;

/**
 * PdDControlStation
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PdDControlStation extends PortaDominio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ip;
	private int porta;
	private String protocollo;
	
	private String ipGestione;
	private int portaGestione;
	private String protocolloGestione;
	
	private String tipo;
	private String password;

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPorta() {
		return this.porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public String getIpGestione() {
		return this.ipGestione;
	}

	public void setIpGestione(String ipGestione) {
		this.ipGestione = ipGestione;
	}

	public int getPortaGestione() {
		return this.portaGestione;
	}

	public void setPortaGestione(int portaGestione) {
		this.portaGestione = portaGestione;
	}

	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	@Override
	public boolean equals(Object object) {
		return this.equals(object, true);
	}

	public String getProtocolloGestione() {
		return this.protocolloGestione;
	}

	public void setProtocolloGestione(String protocolloGestione) {
		this.protocolloGestione = protocolloGestione;
	}

}
