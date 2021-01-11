/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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

package org.openspcoop2.protocol.utils;

/**
* EsitoIdentificationMode
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public enum EsitoTransportContextIdentificationMode {

	EXISTS ("exists"),
	MATCH ("match"),
	CONTAINS ("contains"),
	EQUALS ("equals");

	private final String valore;

	EsitoTransportContextIdentificationMode(String valore)
	{
		this.valore = valore;
	}

	public String getValore()
	{
		return this.valore;
	}

	public static String[] toStringArray(){
		String[] res = new String[EsitoTransportContextIdentificationMode.values().length];
		int i=0;
		for (EsitoTransportContextIdentificationMode tmp : EsitoTransportContextIdentificationMode.values()) {
			res[i]=tmp.getValore();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[EsitoTransportContextIdentificationMode.values().length];
		int i=0;
		for (EsitoTransportContextIdentificationMode tmp : EsitoTransportContextIdentificationMode.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}


	public static EsitoTransportContextIdentificationMode toEnumConstant(String val){

		EsitoTransportContextIdentificationMode res = null;

		if(EsitoTransportContextIdentificationMode.EXISTS.toString().equals(val)){
			res = EsitoTransportContextIdentificationMode.EXISTS;
		}else if(EsitoTransportContextIdentificationMode.MATCH.toString().equals(val)){
			res = EsitoTransportContextIdentificationMode.MATCH;
		}else if(EsitoTransportContextIdentificationMode.CONTAINS.toString().equals(val)){
			res = EsitoTransportContextIdentificationMode.CONTAINS;
		}else if(EsitoTransportContextIdentificationMode.EQUALS.toString().equals(val)){
			res = EsitoTransportContextIdentificationMode.EQUALS;
		}
		return res;
	}

	
	@Override
	public String toString(){
		return this.valore;
	}
	public boolean equals(EsitoTransportContextIdentificationMode esito){
		return this.toString().equals(esito.toString());
	}
	
}
