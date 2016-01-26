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
 * ProfiliDiCollaborazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ProfiloDiCollaborazione {
	ONEWAY("Oneway"), 
	SINCRONO("Sincrono"), 
	ASINCRONO_SIMMETRICO("AsincronoSimmetrico"), 
	ASINCRONO_ASIMMETRICO("AsincronoAsimmetrico"), 
	UNKNOWN("Sconosciuto");
	
	private final String profilo;

	ProfiloDiCollaborazione(String profilo){
		this.profilo = profilo;
	}
	
	@Override
	public String toString() {
		throw new NotImplementedException("Use ProtocolFactory.createTraduttore().toString(profilo) or getEngineValue()");
	}
	
	public boolean equals(ProfiloDiCollaborazione p){
		if(p==null){
			return false;
		}
		return this.profilo.equals(p.profilo);
	}
	
	public String getEngineValue(){
		return this.profilo;
	}
	
	public static ProfiloDiCollaborazione toProfiloDiCollaborazione(String p){
		if(ONEWAY.getEngineValue().equals(p))
			return ONEWAY;
		if(SINCRONO.getEngineValue().equals(p))
			return SINCRONO;
		if(ASINCRONO_ASIMMETRICO.getEngineValue().equals(p))
			return ASINCRONO_ASIMMETRICO;
		if(ASINCRONO_SIMMETRICO.getEngineValue().equals(p))
			return ASINCRONO_SIMMETRICO;
		return UNKNOWN;
	}


}
