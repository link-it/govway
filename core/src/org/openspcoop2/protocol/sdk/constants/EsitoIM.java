/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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
 * Contiene i possibili esiti di una invocazione verso il servizio di IntegrationManager
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum EsitoIM implements Serializable {

	OK ("0"),
	// Codice 1 utilizzato in Esito per ERRORE_PROTOCOLLO
	// Codice 2 utilizzato in Esito per ERRORE_APPLICATIVO
	ERRORE_GENERICO ("3"),
	// Codice 4 utilizzato in Esito per ERRORE_PROCESSAMENTO_PDD_4XX
	// Codice 5 utilizzato in Esito per ERRORE_PROCESSAMENTO_PDD_5XX
	AUTENTICAZIONE_FALLITA ("6"),
	AUTORIZZAZIONE_FALLITA ("7"),
	MESSAGGI_NON_PRESENTI ("8"),
	MESSAGGIO_NON_TROVATO ("9");
	 
	private final String valore;

	EsitoIM(String valore)
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
		String[] res = new String[EsitoIM.values().length];
		int i=0;
		for (EsitoIM tmp : EsitoIM.values()) {
			res[i]=tmp.getValore();
			i++;
		}
		return res;
	}
	public static int[] toIntArray(){
		int[] res = new int[EsitoIM.values().length];
		int i=0;
		for (EsitoIM tmp : EsitoIM.values()) {
			res[i]=tmp.getValoreAsInt();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[EsitoIM.values().length];
		int i=0;
		for (EsitoIM tmp : EsitoIM.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}


	public static EsitoIM toEnumConstant(int val){
		return EsitoIM.toEnumConstant(""+val);
	}
	public static EsitoIM toEnumConstant(String val){

		EsitoIM res = null;

		if(EsitoIM.OK.toString().equals(val)){
			res = EsitoIM.OK;
		}else if(EsitoIM.ERRORE_GENERICO.toString().equals(val)){
			res = EsitoIM.ERRORE_GENERICO;
		} 
		else if(EsitoIM.AUTENTICAZIONE_FALLITA.toString().equals(val)){
			res = EsitoIM.AUTENTICAZIONE_FALLITA;
		} 
		else if(EsitoIM.AUTORIZZAZIONE_FALLITA.toString().equals(val)){
			res = EsitoIM.AUTORIZZAZIONE_FALLITA;
		} 
		else if(EsitoIM.MESSAGGI_NON_PRESENTI.toString().equals(val)){
			res = EsitoIM.MESSAGGI_NON_PRESENTI;
		} 
		else if(EsitoIM.MESSAGGIO_NON_TROVATO.toString().equals(val)){
			res = EsitoIM.MESSAGGIO_NON_TROVATO;
		} 
		return res;
	}

	
	@Override
	public String toString(){
		return this.valore;
	}
	public boolean equals(EsitoIM esito){
		return this.toString().equals(esito.toString());
	}


}

