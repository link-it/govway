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
 * RuoloTransazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum RuoloTransazione implements Serializable{

	INVOCAZIONE_ONEWAY ("1"),
	INVOCAZIONE_SINCRONA ("2"),
	INVOCAZIONE_ASINCRONA_SIMMETRICA ("3"),
	RISPOSTA_ASINCRONA_SIMMETRICA ("4"),
	INVOCAZIONE_ASINCRONA_ASIMMETRICA ("5"),
	RICHIESTA_STATO_ASINCRONA_ASIMMETRICA ("6"),
	INTEGRATION_MANAGER ("7");

	private final String valore;

	RuoloTransazione(String valore)
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
		for (RuoloTransazione tmp : values()) {
			res[i]=tmp.getValore();
			i++;
		}
		return res;
	}
	public static int[] toIntArray(){
		int[] res = new int[values().length];
		int i=0;
		for (RuoloTransazione tmp : values()) {
			res[i]=tmp.getValoreAsInt();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (RuoloTransazione tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}


	public static RuoloTransazione toEnumConstant(int val){
		return toEnumConstant(""+val);
	}
	public static RuoloTransazione toEnumConstant(String val){

		RuoloTransazione res = null;

		if(RuoloTransazione.INVOCAZIONE_ONEWAY.toString().equals(val)){
			res = RuoloTransazione.INVOCAZIONE_ONEWAY;
		}else if(RuoloTransazione.INVOCAZIONE_SINCRONA.toString().equals(val)){
			res = RuoloTransazione.INVOCAZIONE_SINCRONA;
		} else if(RuoloTransazione.INVOCAZIONE_ASINCRONA_SIMMETRICA.toString().equals(val)){
			res = RuoloTransazione.INVOCAZIONE_ASINCRONA_SIMMETRICA;
		}  else if(RuoloTransazione.RISPOSTA_ASINCRONA_SIMMETRICA.toString().equals(val)){
			res = RuoloTransazione.RISPOSTA_ASINCRONA_SIMMETRICA;
		}  else if(RuoloTransazione.INVOCAZIONE_ASINCRONA_ASIMMETRICA.toString().equals(val)){
			res = RuoloTransazione.INVOCAZIONE_ASINCRONA_ASIMMETRICA;
		}  else if(RuoloTransazione.RICHIESTA_STATO_ASINCRONA_ASIMMETRICA.toString().equals(val)){
			res = RuoloTransazione.RICHIESTA_STATO_ASINCRONA_ASIMMETRICA;
		} else if(RuoloTransazione.INTEGRATION_MANAGER.toString().equals(val)){
			res = RuoloTransazione.INTEGRATION_MANAGER;
		} 
		return res;
	}

	
	@Override
	public String toString(){
		return this.valore;
	}
	public boolean equals(RuoloTransazione esito){
		return this.toString().equals(esito.toString());
	}


	public static RuoloTransazione getEnumConstantFromOpenSPCoopValue(String val){
		
		//System.out.println("VALORE DA CONVERTIRE ["+val+"]");
		
		if(org.openspcoop2.core.constants.Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(val)){
			//System.out.println("RITORNO ["+RuoloTransazione.INVOCAZIONE_ONEWAY+"]");
			return RuoloTransazione.INVOCAZIONE_ONEWAY;
		}
		else if(org.openspcoop2.core.constants.Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO.equals(val)){
			//System.out.println("RITORNO ["+RuoloTransazione.INVOCAZIONE_SINCRONA+"]");
			return RuoloTransazione.INVOCAZIONE_SINCRONA;
		}
		else if(org.openspcoop2.core.constants.Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO.equals(val)){
			//System.out.println("RITORNO ["+RuoloTransazione.INVOCAZIONE_ASINCRONA_SIMMETRICA+"]");
			return RuoloTransazione.INVOCAZIONE_ASINCRONA_SIMMETRICA;
		}
		else if(org.openspcoop2.core.constants.Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA.equals(val)){
			//System.out.println("RITORNO ["+RuoloTransazione.RISPOSTA_ASINCRONA_SIMMETRICA+"]");
			return RuoloTransazione.RISPOSTA_ASINCRONA_SIMMETRICA;
		}
		else if(org.openspcoop2.core.constants.Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO.equals(val)){
			//System.out.println("RITORNO ["+RuoloTransazione.INVOCAZIONE_ASINCRONA_ASIMMETRICA+"]");
			return RuoloTransazione.INVOCAZIONE_ASINCRONA_ASIMMETRICA;
		}
		else if(org.openspcoop2.core.constants.Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING.equals(val)){
			//System.out.println("RITORNO ["+RuoloTransazione.RICHIESTA_STATO_ASINCRONA_ASIMMETRICA+"]");
			return RuoloTransazione.RICHIESTA_STATO_ASINCRONA_ASIMMETRICA;
		}
		else {
			//System.out.println("RITORNO NULL");
			return null;
		}
	}


}

