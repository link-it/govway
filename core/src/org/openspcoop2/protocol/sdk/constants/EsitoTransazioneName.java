/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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

public enum EsitoTransazioneName implements Serializable{

	OK,
	OK_PRESENZA_ANOMALIE,
	ERRORE_PROTOCOLLO,
	ERRORE_APPLICATIVO,
	ERRORE_GENERICO,
	ERRORE_PROCESSAMENTO_PDD_4XX,
	ERRORE_PROCESSAMENTO_PDD_5XX,
	AUTENTICAZIONE_FALLITA,
	AUTORIZZAZIONE_FALLITA,
	MESSAGGI_NON_PRESENTI,
	MESSAGGIO_NON_TROVATO,
	ERRORE_INVOCAZIONE,
	CONTENUTO_RICHIESTA_NON_RICONOSCIUTO,
	CONTENUTO_RISPOSTA_NON_RICONOSCIUTO,
	ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE,
	ERRORE_SERVER,
	CUSTOM;

	EsitoTransazioneName()
	{
	}

	public static EsitoTransazioneName convertoTo(String name){
		EsitoTransazioneName esitoTransactionName = null;
		try{
			esitoTransactionName = EsitoTransazioneName.valueOf(name);
		}catch(Exception e){}
		if(esitoTransactionName==null){
			esitoTransactionName = EsitoTransazioneName.CUSTOM;
		}
		return esitoTransactionName;
	}
	
	public static String[] toEnumNameArray(){
		String[] res = new String[EsitoTransazioneName.values().length];
		int i=0;
		for (EsitoTransazioneName tmp : EsitoTransazioneName.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	
	@Override
	public String toString(){
		return this.name();
	}
	public boolean equals(EsitoTransazioneName esito){
		return this.toString().equals(esito.toString());
	}


}

