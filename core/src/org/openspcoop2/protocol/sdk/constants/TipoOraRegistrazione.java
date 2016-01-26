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
 * TipoOraRegistrazione
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoOraRegistrazione {
	LOCALE("Locale"), 
	SINCRONIZZATO("Sincronizzato"), 
	UNKNOWN("Sconosciuto");
	
	private final String tipoOra;

	TipoOraRegistrazione(String tipoOra){
		this.tipoOra = tipoOra;
	}
	
	@Override
	public String toString() {
		throw new NotImplementedException("Use ProtocolFactory.createTraduttore().toString(tipoOra) or getEngineValue()");
	}
	
	public boolean equals(TipoOraRegistrazione t){
		if(t==null){
			return false;
		}
		return this.tipoOra.equals(t.tipoOra);
	}
	
	public String getEngineValue(){
		return this.tipoOra;
	}
	
	public static TipoOraRegistrazione toTipoOraRegistrazione(String metaValue){
		if(LOCALE.tipoOra.equals(metaValue))
			return LOCALE;
		if(SINCRONIZZATO.tipoOra.equals(metaValue))
			return SINCRONIZZATO;
		return UNKNOWN;
	}
	
}
