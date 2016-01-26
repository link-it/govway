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

package org.openspcoop2.protocol.sdk.constants;

import org.apache.commons.lang.NotImplementedException;

/**
 * Enumarazione sul livello di rilevanza di una eccezione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum LivelloRilevanza {
	DEBUG("DEBUG"),
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR"),
    FATAL("FATAL"),
    UNKNOWN("Sconosciuto");

	private final String livello;

	LivelloRilevanza(String livello){
		this.livello = livello;
	}
	
	@Override
	public String toString() {
		throw new NotImplementedException("Use ProtocolFactory.createTraduttore().toString(livelloRilevanza) or getEngineValue()");
	}
	
	public boolean equals(LivelloRilevanza l){
		if(l==null){
			return false;
		}
		return this.livello.equals(l.livello);
	}
	
	public String getEngineValue(){
		return this.livello;
	}
	
	public static boolean isEccezioneLivelloGrave(LivelloRilevanza livello){
		switch (livello) {
		case ERROR:
		case FATAL:
			return true;
		default:
			return false;
		}
	}
	
	public static LivelloRilevanza toLivelloRilevanza(String metaValue){
		if(DEBUG.livello.equals(metaValue))
			return DEBUG;
		if(INFO.livello.equals(metaValue))
			return INFO;
		if(WARN.livello.equals(metaValue))
			return WARN;
		if(ERROR.livello.equals(metaValue))
			return ERROR;
		if(FATAL.livello.equals(metaValue))
			return FATAL;
		return UNKNOWN;
	}

}
