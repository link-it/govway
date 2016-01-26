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


package org.openspcoop2.core.constants;

/**
 * Contiene i tipi di connettori di default
 *
 * @author corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum TipiConnettore {

	DISABILITATO ("disabilitato"),
	HTTP ("http"),
	HTTPS ("https"),
	JMS ("jms"),
	NULL ("null"),
	NULLECHO("nullEcho"),
	CUSTOM("custom");
	
	
	private final String nome;

	TipiConnettore(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return this.nome;
	}
	
	@Override
	public String toString() {
		return this.nome;
	}
}

