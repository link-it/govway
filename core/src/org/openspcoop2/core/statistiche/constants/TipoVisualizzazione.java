/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
 * TipoVisualizzazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoVisualizzazione implements IEnumeration , Serializable , Cloneable{

	DIMENSIONE_TRANSAZIONI ("dimensioneTransazioni"), NUMERO_TRANSAZIONI ("numeroTransazioni"),
		TEMPO_MEDIO_RISPOSTA ("tempoMedioRisposta");

	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}

	/** Official Constructor */
	TipoVisualizzazione(String value)
	{
		this.value = value;
	}

	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoVisualizzazione object){
		if(object==null)
			return false;
		if(object.getValue()==null)
			return false;
		return object.getValue().equals(this.getValue());	
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
		for (TipoVisualizzazione tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoVisualizzazione tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoVisualizzazione tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static TipoVisualizzazione toEnumConstant(String value){
		TipoVisualizzazione res = null;
		if(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI.getValue().equals(value)){
			res = TipoVisualizzazione.DIMENSIONE_TRANSAZIONI;
		}else if(TipoVisualizzazione.NUMERO_TRANSAZIONI.getValue().equals(value)){
			res = TipoVisualizzazione.NUMERO_TRANSAZIONI;
		}else if(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA.getValue().equals(value)){
			res = TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA;
		}  


		return res;
	}

	public static IEnumeration toEnumConstantFromString(String value){
		TipoVisualizzazione res = null;
		if(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI.toString().equals(value)){
			res = TipoVisualizzazione.DIMENSIONE_TRANSAZIONI;
		}else if(TipoVisualizzazione.NUMERO_TRANSAZIONI.toString().equals(value)){
			res = TipoVisualizzazione.NUMERO_TRANSAZIONI;
		}else if(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA.toString().equals(value)){
			res = TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA;
		}  
		return res;
	}

}
