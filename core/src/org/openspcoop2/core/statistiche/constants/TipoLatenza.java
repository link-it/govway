/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.core.statistiche.constants;

import java.io.Serializable;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * TipoLatenza
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoLatenza  implements IEnumeration , Serializable , Cloneable{

	LATENZA_TOTALE ("Latenza Totale"), LATENZA_SERVIZIO ("Latenza Servizio"), LATENZA_PORTA ("Latenza Gateway");

	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoLatenza(String value)
	{
		this.value = value;
	}

	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}

	/** Utilities */

	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoLatenza tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoLatenza tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoLatenza tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static TipoLatenza toEnumConstant(String value){
		TipoLatenza res = null;
		if(TipoLatenza.LATENZA_TOTALE.getValue().equals(value)){
			res = TipoLatenza.LATENZA_TOTALE;
		}else if(TipoLatenza.LATENZA_SERVIZIO.getValue().equals(value)){
			res = TipoLatenza.LATENZA_SERVIZIO;
		}else if(TipoLatenza.LATENZA_PORTA.getValue().equals(value)){
			res = TipoLatenza.LATENZA_PORTA;
		}  


		return res;
	}

	public static IEnumeration toEnumConstantFromString(String value){
		TipoLatenza res = null;
		if(TipoLatenza.LATENZA_TOTALE.toString().equals(value)){
			res = TipoLatenza.LATENZA_TOTALE;
		}else if(TipoLatenza.LATENZA_SERVIZIO.toString().equals(value)){
			res = TipoLatenza.LATENZA_SERVIZIO;
		}else if(TipoLatenza.LATENZA_PORTA.toString().equals(value)){
			res = TipoLatenza.LATENZA_PORTA;
		}  
		return res;
	}

}
