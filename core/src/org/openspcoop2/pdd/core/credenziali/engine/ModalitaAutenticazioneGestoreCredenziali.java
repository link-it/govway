/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.credenziali.engine;

import java.io.Serializable;

/**     
 * ModalitaAutenticazioneGestoreCredenziali
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ModalitaAutenticazioneGestoreCredenziali implements Serializable {

	NONE ("none"),
	AT_LEAST_ONE ("atLeastOne"),
	SSL ("ssl"),
	BASIC ("basic"),
	PRINCIPAL ("principal");

	private final String valore;

	ModalitaAutenticazioneGestoreCredenziali(String valore)
	{
		this.valore = valore;
	}

	public String getValore()
	{
		return this.valore;
	}

	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (ModalitaAutenticazioneGestoreCredenziali tmp : values()) {
			res[i]=tmp.getValore();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (ModalitaAutenticazioneGestoreCredenziali tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static ModalitaAutenticazioneGestoreCredenziali toEnumConstant(String val){
		ModalitaAutenticazioneGestoreCredenziali res = null;
		for (ModalitaAutenticazioneGestoreCredenziali tmp : values()) {
			if(tmp.getValore().equals(val)){
				res = tmp;
				break;
			}
		}
		return res;
	}

	
	@Override
	public String toString(){
		return this.valore;
	}
	public boolean equals(ModalitaAutenticazioneGestoreCredenziali esito){
		return this.toString().equals(esito.toString());
	}

}

