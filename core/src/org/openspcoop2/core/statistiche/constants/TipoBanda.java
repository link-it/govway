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
 * TipoBanda
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoBanda  implements IEnumeration , Serializable , Cloneable{

	INTERNA ("Banda Interna"), ESTERNA ("Banda Esterna"), COMPLESSIVA ("Banda Complessiva");

	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoBanda(String value)
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
		for (TipoBanda tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoBanda tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoBanda tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static TipoBanda toEnumConstant(String value){
		TipoBanda res = null;
		if(TipoBanda.INTERNA.getValue().equals(value)){
			res = TipoBanda.INTERNA;
		}else if(TipoBanda.ESTERNA.getValue().equals(value)){
			res = TipoBanda.ESTERNA;
		}else if(TipoBanda.COMPLESSIVA.getValue().equals(value)){
			res = TipoBanda.COMPLESSIVA;
		}  


		return res;
	}

	public static IEnumeration toEnumConstantFromString(String value){
		TipoBanda res = null;
		if(TipoBanda.INTERNA.toString().equals(value)){
			res = TipoBanda.INTERNA;
		}else if(TipoBanda.ESTERNA.toString().equals(value)){
			res = TipoBanda.ESTERNA;
		}else if(TipoBanda.COMPLESSIVA.toString().equals(value)){
			res = TipoBanda.COMPLESSIVA;
		}  
		return res;
	}

}
