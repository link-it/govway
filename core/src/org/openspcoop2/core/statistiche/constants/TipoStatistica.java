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

package org.openspcoop2.core.statistiche.constants;

import java.io.Serializable;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * TipoStatistica
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoStatistica  implements IEnumeration , Serializable , Cloneable{

	ANDAMENTO_TEMPORALE ("andamentoTemporale"), 
	DISTRIBUZIONE_ERRORI ("distribuzioneErrori"),
	DISTRIBUZIONE_SOGGETTO ("distribuzioneSoggetto"),
	DISTRIBUZIONE_SERVIZIO ("distribuzioneServizio"), 
	DISTRIBUZIONE_AZIONE ("distribuzioneAzione"),
	DISTRIBUZIONE_SERVIZIO_APPLICATIVO ("distribuzioneSA"), 
	STATISTICA_PERSONALIZZATA ("statisticaPersonalizzata");

	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	TipoStatistica(String value)
	{
		this.value = value;
	}

	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(TipoStatistica object){
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
		for (TipoStatistica tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoStatistica tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoStatistica tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}

	public static TipoStatistica toEnumConstant(String value){
		TipoStatistica res = null;
		if(TipoStatistica.ANDAMENTO_TEMPORALE.getValue().equals(value)){
			res = TipoStatistica.ANDAMENTO_TEMPORALE;
		}else if(TipoStatistica.DISTRIBUZIONE_ERRORI.getValue().equals(value)){
			res = TipoStatistica.DISTRIBUZIONE_ERRORI;
		}else if(TipoStatistica.DISTRIBUZIONE_SERVIZIO.getValue().equals(value)){
			res = TipoStatistica.DISTRIBUZIONE_SERVIZIO;
		}else if(TipoStatistica.DISTRIBUZIONE_SOGGETTO.getValue().equals(value)){
			res = TipoStatistica.DISTRIBUZIONE_SOGGETTO;
		}else if(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO.getValue().equals(value)){
			res = TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO;
		}else if(TipoStatistica.STATISTICA_PERSONALIZZATA.getValue().equals(value)){
			res = TipoStatistica.STATISTICA_PERSONALIZZATA;
		}else if(TipoStatistica.DISTRIBUZIONE_AZIONE.getValue().equals(value)){
			res = TipoStatistica.DISTRIBUZIONE_AZIONE;
		}

		return res;
	}

	public static IEnumeration toEnumConstantFromString(String value){
		TipoStatistica res = null;
		if(TipoStatistica.ANDAMENTO_TEMPORALE.toString().equals(value)){
			res = TipoStatistica.ANDAMENTO_TEMPORALE;
		} else if(TipoStatistica.DISTRIBUZIONE_ERRORI.toString().equals(value)){
			res = TipoStatistica.DISTRIBUZIONE_ERRORI;
		} else if(TipoStatistica.DISTRIBUZIONE_SERVIZIO.toString().equals(value)){
			res = TipoStatistica.DISTRIBUZIONE_SERVIZIO;
		}else if(TipoStatistica.DISTRIBUZIONE_SOGGETTO.toString().equals(value)){
			res = TipoStatistica.DISTRIBUZIONE_SOGGETTO;
		}else if(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO.toString().equals(value)){
			res = TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO;
		}else if(TipoStatistica.STATISTICA_PERSONALIZZATA.toString().equals(value)){
			res = TipoStatistica.STATISTICA_PERSONALIZZATA;
		} else if(TipoStatistica.DISTRIBUZIONE_AZIONE.toString().equals(value)){
			res = TipoStatistica.DISTRIBUZIONE_AZIONE;
		}  
		return res;
	}

}
