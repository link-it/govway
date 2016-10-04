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

package org.openspcoop2.protocol.sdk.constants;

import org.apache.commons.lang.NotImplementedException;

/**
 * Enumerazioni per la modalità di Inoltro
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum Inoltro {
	CON_DUPLICATI("PIUDIUNAVOLTA"), 
	SENZA_DUPLICATI("ALPIUUNAVOLTA"), 
	UNKNOWN("Sconosciuto");
	
	private final String inoltro;

	Inoltro(String inoltro){
		this.inoltro = inoltro;
	}
	
	@Override
	public String toString() {
		throw new NotImplementedException("Use ProtocolFactory.createTraduttore().toString(inoltro) or getEngineValue()");
	}
	
	public boolean equals(Inoltro i){
		if(i==null){
			return false;
		}
		return this.inoltro.equals(i.inoltro);
	}
	
	public String getEngineValue(){
		return this.inoltro;
	}
	
	public static Inoltro toInoltro(String s) {
		if(CON_DUPLICATI.getEngineValue().equals(s))
			return CON_DUPLICATI;
		if(SENZA_DUPLICATI.getEngineValue().equals(s))
			return SENZA_DUPLICATI;
		return UNKNOWN;
	}
}
