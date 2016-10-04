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

package org.openspcoop2.utils.jdbc;

/**
 * Gestione di default
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DefaultKeyGeneratorObject implements IKeyGeneratorObject {

	// ASSUME:
	// -- Le sequence inizino con il prefisso prefix
	// -- La tabella si chiama table
	// -- Eventuali tabelle di servizio iniziano con il prefix table
	
	private KeyGeneratorObjects keyGeneratorObject;
	private String table;
	private String prefix;
	
	public DefaultKeyGeneratorObject(String table,String prefix){
		this.table = table;
		this.prefix = prefix;
		this.keyGeneratorObject = KeyGeneratorObjects.DEFAULT;
	}
	
	@Override
	public KeyGeneratorObjects getType(){
		return this.keyGeneratorObject;
	}

	@Override
	public String getTable(){
		return this.table;
	}

	public String getPrefix() {
		return this.prefix;
	}

	
}
