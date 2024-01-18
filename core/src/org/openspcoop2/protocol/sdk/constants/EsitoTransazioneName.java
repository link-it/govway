/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.protocol.sdk.constants;

import java.io.Serializable;

import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;


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
	MESSAGGI_NON_PRESENTI,
	CONTROLLO_TRAFFICO_POLICY_VIOLATA_WARNING_ONLY,
	CONTROLLO_TRAFFICO_MAX_THREADS_WARNING_ONLY,
	HTTP_3xx,
	CONSEGNA_MULTIPLA,
	CONSEGNA_MULTIPLA_IN_CORSO,
	CONSEGNA_MULTIPLA_COMPLETATA,
	
	ERRORE_APPLICATIVO,
	
	ERRORE_PROTOCOLLO,
	ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE,
	ERRORE_PROCESSAMENTO_PDD_4XX,
	ERRORE_PROCESSAMENTO_PDD_5XX,
	AUTENTICAZIONE_FALLITA,
	AUTORIZZAZIONE_FALLITA,
	MESSAGGIO_NON_TROVATO,
	ERRORE_INVOCAZIONE,
	ERRORE_SERVER,
	CONTENUTO_RICHIESTA_NON_RICONOSCIUTO,
	CONTENUTO_RISPOSTA_NON_RICONOSCIUTO,
	TOKEN_NON_PRESENTE,
	ERRORE_AUTENTICAZIONE_TOKEN,
	ERRORE_TOKEN,
	ERRORE_AUTENTICAZIONE,
	ERRORE_AUTORIZZAZIONE,
	CONTROLLO_TRAFFICO_POLICY_VIOLATA,
	CONTROLLO_TRAFFICO_MAX_THREADS,
	ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA,
	ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA,
	ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA,
	ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA,
	ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA,
	ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA,
	HTTP_4xx,
	HTTP_5xx,
	ERRORE_VALIDAZIONE_RICHIESTA,
	ERRORE_VALIDAZIONE_RISPOSTA,
	ERRORE_SOSPENSIONE,
	CORS_PREFLIGHT_REQUEST_VIA_GATEWAY,
	CORS_PREFLIGHT_REQUEST_TRASPARENTE,
	ERRORE_TRASFORMAZIONE_RICHIESTA,
	ERRORE_TRASFORMAZIONE_RISPOSTA,
	CONSEGNA_MULTIPLA_FALLITA,
	API_NON_INDIVIDUATA,
	OPERAZIONE_NON_INDIVIDUATA,
	RICHIESTA_DUPLICATA,
	RISPOSTA_DUPLICATA,
	MESSAGE_BOX,
	ERRORE_RESPONSE_TIMEOUT,
	ERRORE_REQUEST_TIMEOUT,
	ERRORE_CONNECTION_TIMEOUT,
	ERRORE_NEGOZIAZIONE_TOKEN,
	
	CUSTOM;

	private MapKey<String> mapKey;
	
	EsitoTransazioneName()
	{
		this.mapKey = Map.newMapKey("EsitoTransazioneName."+this.name());
	}
	
	public MapKey<String> getMapKey() {
		return this.mapKey;
	}
	
	public static boolean isPddSpecific(EsitoTransazioneName esitoTransactionName){
		if(!EsitoTransazioneName.OK.equals(esitoTransactionName) 
				&&
				!isIntegrationManagerSpecific(esitoTransactionName)
				){
			return true;
		}
		return false;
	}
	public static  boolean isIntegrationManagerSpecific(EsitoTransazioneName esitoTransactionName){
		if(EsitoTransazioneName.MESSAGGI_NON_PRESENTI.equals(esitoTransactionName) || 
				EsitoTransazioneName.MESSAGGIO_NON_TROVATO.equals(esitoTransactionName) ||
				EsitoTransazioneName.AUTENTICAZIONE_FALLITA.equals(esitoTransactionName) ||
				EsitoTransazioneName.AUTORIZZAZIONE_FALLITA.equals(esitoTransactionName) 
				){
			return true;
		}
		return false;
	}
	public static  boolean isStatiConsegnaMultipla(EsitoTransazioneName esitoTransactionName){
		// Stati successivi al primo stato
		if(EsitoTransazioneName.CONSEGNA_MULTIPLA_IN_CORSO.equals(esitoTransactionName) || 
				EsitoTransazioneName.CONSEGNA_MULTIPLA_COMPLETATA.equals(esitoTransactionName) || 
				EsitoTransazioneName.CONSEGNA_MULTIPLA_FALLITA.equals(esitoTransactionName) 
				){
			return true;
		}
		return false;
	}
	public static boolean isConsegnaMultipla(EsitoTransazioneName esitoTransactionName){
		if(EsitoTransazioneName.CONSEGNA_MULTIPLA.equals(esitoTransactionName) || 
				EsitoTransazioneName.CONSEGNA_MULTIPLA_IN_CORSO.equals(esitoTransactionName) || 
				EsitoTransazioneName.CONSEGNA_MULTIPLA_COMPLETATA.equals(esitoTransactionName) || 
				EsitoTransazioneName.CONSEGNA_MULTIPLA_FALLITA.equals(esitoTransactionName) 
				){
			return true;
		}
		return false;
	}
	
	public static boolean isErroreRisposta(EsitoTransazioneName esitoTransactionName){
		// Vedi anche gruppo in esiti.properties
		if(EsitoTransazioneName.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO.equals(esitoTransactionName) || 
				EsitoTransazioneName.ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA.equals(esitoTransactionName) || 
				EsitoTransazioneName.ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA.equals(esitoTransactionName) || 
				EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA.equals(esitoTransactionName) || 
				EsitoTransazioneName.ERRORE_VALIDAZIONE_RISPOSTA.equals(esitoTransactionName) ||
				EsitoTransazioneName.ERRORE_TRASFORMAZIONE_RISPOSTA.equals(esitoTransactionName) ||
				EsitoTransazioneName.RISPOSTA_DUPLICATA.equals(esitoTransactionName)
				){
			return true;
		}
		return false;
	}
	
	public static boolean isSavedInMessageBox(EsitoTransazioneName esitoTransactionName){
		if(EsitoTransazioneName.MESSAGE_BOX.equals(esitoTransactionName)){
			return true;
		}
		return false;
	}

	public static EsitoTransazioneName convertoTo(String name){
		EsitoTransazioneName esitoTransactionName = null;
		try{
			esitoTransactionName = EsitoTransazioneName.valueOf(name);
		}catch(Exception e){
			// ignore
		}
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


}

