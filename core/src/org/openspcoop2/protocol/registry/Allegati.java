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

package org.openspcoop2.protocol.registry;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.registry.Documento;

/**
 * Allegati
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Allegati {

	private List<Documento> allegatiASParteComune = new ArrayList<Documento>();
	private List<Documento> specificheSemiformaliASParteComune = new ArrayList<Documento>();
	
	private List<Documento> allegatiASParteSpecifica = new ArrayList<Documento>();
	private List<Documento> specificheSemiformaliASParteSpecifica = new ArrayList<Documento>();
	private List<Documento> specificheSicurezzaASParteSpecifica = new ArrayList<Documento>();
	private List<Documento> specificheLivelloServizioASParteSpecifica = new ArrayList<Documento>();
	
	
	public List<Documento> getAllegatiASParteComune() {
		return this.allegatiASParteComune;
	}
	public void setAllegatiASParteComune(List<Documento> allegatiASParteComune) {
		this.allegatiASParteComune = allegatiASParteComune;
	}
	public List<Documento> getSpecificheSemiformaliASParteComune() {
		return this.specificheSemiformaliASParteComune;
	}
	public void setSpecificheSemiformaliASParteComune(
			List<Documento> specificheSemiformaliASParteComune) {
		this.specificheSemiformaliASParteComune = specificheSemiformaliASParteComune;
	}
	public List<Documento> getAllegatiASParteSpecifica() {
		return this.allegatiASParteSpecifica;
	}
	public void setAllegatiASParteSpecifica(List<Documento> allegatiASParteSpecifica) {
		this.allegatiASParteSpecifica = allegatiASParteSpecifica;
	}
	public List<Documento> getSpecificheSemiformaliASParteSpecifica() {
		return this.specificheSemiformaliASParteSpecifica;
	}
	public void setSpecificheSemiformaliASParteSpecifica(
			List<Documento> specificheSemiformaliASParteSpecifica) {
		this.specificheSemiformaliASParteSpecifica = specificheSemiformaliASParteSpecifica;
	}
	public List<Documento> getSpecificheSicurezzaASParteSpecifica() {
		return this.specificheSicurezzaASParteSpecifica;
	}
	public void setSpecificheSicurezzaASParteSpecifica(
			List<Documento> specificheSicurezzaASParteSpecifica) {
		this.specificheSicurezzaASParteSpecifica = specificheSicurezzaASParteSpecifica;
	}
	public List<Documento> getSpecificheLivelloServizioASParteSpecifica() {
		return this.specificheLivelloServizioASParteSpecifica;
	}
	public void setSpecificheLivelloServizioASParteSpecifica(
			List<Documento> specificheLivelloServizioASParteSpecifica) {
		this.specificheLivelloServizioASParteSpecifica = specificheLivelloServizioASParteSpecifica;
	}
}
