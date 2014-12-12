/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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

import java.io.Serializable;


/**
 * Contiene i possibili esiti
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum Esito implements Serializable{

	OK ("0"),
	ERRORE_PROTOCOLLO ("1"),
	ERRORE_APPLICATIVO ("2"),
	ERRORE_GENERICO ("3"),
	ERRORE_PROCESSAMENTO_PDD_4XX ("4"),
	ERRORE_PROCESSAMENTO_PDD_5XX ("5");

	private final String valore;

	Esito(String valore)
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
		String[] res = new String[Esito.values().length];
		int i=0;
		for (Esito tmp : Esito.values()) {
			res[i]=tmp.getValore();
			i++;
		}
		return res;
	}
	public static int[] toIntArray(){
		int[] res = new int[Esito.values().length];
		int i=0;
		for (Esito tmp : Esito.values()) {
			res[i]=tmp.getValoreAsInt();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[Esito.values().length];
		int i=0;
		for (Esito tmp : Esito.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}


	public static Esito toEnumConstant(int val){
		return Esito.toEnumConstant(""+val);
	}
	public static Esito toEnumConstant(String val){

		Esito res = null;

		if(Esito.OK.toString().equals(val)){
			res = Esito.OK;
		}else if(Esito.ERRORE_PROTOCOLLO.toString().equals(val)){
			res = Esito.ERRORE_PROTOCOLLO;
		} else if(Esito.ERRORE_APPLICATIVO.toString().equals(val)){
			res = Esito.ERRORE_APPLICATIVO;
		}  else if(Esito.ERRORE_GENERICO.toString().equals(val)){
			res = Esito.ERRORE_GENERICO;
		}  else if(Esito.ERRORE_PROCESSAMENTO_PDD_4XX.toString().equals(val)){
			res = Esito.ERRORE_PROCESSAMENTO_PDD_4XX;
		}  else if(Esito.ERRORE_PROCESSAMENTO_PDD_5XX.toString().equals(val)){
			res = Esito.ERRORE_PROCESSAMENTO_PDD_5XX;
		} 
		return res;
	}

	
	@Override
	public String toString(){
		return this.valore;
	}
	public boolean equals(Esito esito){
		return this.toString().equals(esito.toString());
	}


}

