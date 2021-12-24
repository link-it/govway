/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.core.transazioni.constants;

import java.io.Serializable;


/**     
 * TipoAPI
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoAPI implements Serializable{

	REST ("1"),
	SOAP ("2");

	private final String valore;

	TipoAPI(String valore)
	{
		this.valore = valore;
	}

	public String getValore()
	{
		return this.valore;
	}

	public int getValoreAsInt()
	{
		return Integer.parseInt(this.valore);
	}

	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAPI tmp : values()) {
			res[i]=tmp.getValore();
			i++;
		}
		return res;
	}
	public static int[] toIntArray(){
		int[] res = new int[values().length];
		int i=0;
		for (TipoAPI tmp : values()) {
			res[i]=tmp.getValoreAsInt();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAPI tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}


	public static TipoAPI toEnumConstant(int val){
		return toEnumConstant(""+val);
	}
	public static TipoAPI toEnumConstant(String val){

		TipoAPI res = null;

		if(TipoAPI.REST.toString().equals(val)){
			res = TipoAPI.REST;
		}else if(TipoAPI.SOAP.toString().equals(val)){
			res = TipoAPI.SOAP;
		}
		return res;
	}

	
	@Override
	public String toString(){
		return this.valore;
	}
	public boolean equals(TipoAPI esito){
		return this.toString().equals(esito.toString());
	}



}

