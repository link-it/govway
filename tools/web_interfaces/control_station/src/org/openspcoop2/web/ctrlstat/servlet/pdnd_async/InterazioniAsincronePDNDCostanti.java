/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.pdnd_async;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * InterazioniAsincronePDNDCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterazioniAsincronePDNDCostanti {

	private InterazioniAsincronePDNDCostanti() {}
	
	public static final String OBJECT_NAME_PDND_INTERAZIONI_ASYNC = "pdndInterazioniAsync";
	
	public static final String SERVLET_NAME_PDND_INTERAZIONI_ASYNC_CHANGE = OBJECT_NAME_PDND_INTERAZIONI_ASYNC+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_PDND_INTERAZIONI_ASYNC_DELETE = OBJECT_NAME_PDND_INTERAZIONI_ASYNC+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_PDND_INTERAZIONI_ASYNC_LIST = OBJECT_NAME_PDND_INTERAZIONI_ASYNC+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_PDND_INTERAZIONI_ASYNC = new ArrayList<>();
	public static List<String> getServletInterazioniAsincronePDND() {
		return SERVLET_PDND_INTERAZIONI_ASYNC;
	}
	static{
		SERVLET_PDND_INTERAZIONI_ASYNC.add(SERVLET_NAME_PDND_INTERAZIONI_ASYNC_CHANGE);
		SERVLET_PDND_INTERAZIONI_ASYNC.add(SERVLET_NAME_PDND_INTERAZIONI_ASYNC_DELETE);
		SERVLET_PDND_INTERAZIONI_ASYNC.add(SERVLET_NAME_PDND_INTERAZIONI_ASYNC_LIST);
	}
	
	public static final String LABEL_PDND_INTERAZIONI_ASYNC = "Interazioni Asincrone";
	
	public static final String LABEL_NESSUNA_SORGENTE_DATI = "Nessuna sorgente dati runtime disponibile";
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_PDND_INTERAZIONI_ASYNC_ID = "pdndAsyncId";
	public static final String PARAMETRO_PDND_INTERAZIONI_ASYNC_SORGENTE = "pdndAsyncSorgente";
	
	/* LABEL */
	
	public static final String LABEL_SORGENTE_DATI = "Sorgente Dati";
	public static final String LABEL_INTERACTION_ID = "Interaction ID";
	public static final String LABEL_RUOLO = "Ruolo";
	public static final String LABEL_RUOLO_FRUITORE = "Fruitore";
	public static final String LABEL_RUOLO_EROGATORE = "Erogatore";
	public static final String LABEL_FASE = "Ultima Fase";
	public static final String LABEL_STATO = "Stato";
	public static final String LABEL_API = "API";
	public static final String LABEL_FRUITORE = "Fruitore";
	public static final String LABEL_EROGATORE = "Erogatore";
	public static final String LABEL_API_CALLBACK = "API Callback";
	
	public static final String LABEL_SEZIONE_INTERAZIONE = "Interazione";
	public static final String LABEL_SEZIONE_TOKEN = "Informazioni Token";
	public static final String LABEL_SEZIONE_PARAMETRI_API = "Parametri API";
	public static final String LABEL_SEZIONE_DATE = "Date";
	
	public static final String LABEL_PURPOSE_ID = "PurposeId";
	public static final String LABEL_CONSUMER_ID = "ConsumerId";
	public static final String LABEL_CLIENT_ID = "ClientId";
	public static final String LABEL_URL_CALLBACK = "URL Callback";
	public static final String LABEL_ENTITY_NUMBER = "Numero Entità";
	// stesse etichette utilizzate nella configurazione ModI dell'API
	public static final String LABEL_TEMPO_MAX_CALLBACK = "Tempo massimo di risposta";
	public static final String LABEL_TEMPO_DISPONIBILITA = "Durata disponibilità dato";
	public static final String LABEL_CONFERMA_RICHIESTA = "Conferma recupero risposta";
	public static final String VALUE_CONFERMA_RICHIESTA_ABILITATA = "Richiesta al fruitore (confirmation)";
	public static final String VALUE_CONFERMA_RICHIESTA_DISABILITATA = "Non richiesta";
	public static final String LABEL_LIMITE_ENTITA = "Numero massimo risultati";
	public static final String SUFFIX_SECONDI = " secondi";
	// la PDND non riporta il numero di entità nel voucher della callback: il fruitore non può conoscerlo
	public static final String VALUE_ENTITY_NUMBER_NON_DISPONIBILE_FRUITORE = "Non presente nel voucher PDND della callback";
	public static final String LABEL_NUMERO_GET_RESOURCE = "Ottenimenti Risposta";
	public static final String LABEL_ID_TRANSAZIONE_START = "ID Transazione Inizio";
	public static final String LABEL_DATA_START = "Inizio Interazione";
	public static final String LABEL_DATA_CALLBACK = "Invocazione Callback";
	public static final String LABEL_DATA_GET_RESOURCE = "Ultimo Ottenimento";
	public static final String LABEL_DATA_CONFIRMATION = "Conferma Ricezione";
	public static final String LABEL_DATA_SCADENZA = "Scadenza";
	public static final String LABEL_DATA_AGGIORNAMENTO = "Ultimo Aggiornamento";
	
	/* STATI */
	
	public static final String VALUE_STATO_ATTIVE = "attive";
	public static final String LABEL_STATO_ATTIVE = "Non scadute";
	public static final String VALUE_STATO_SCADUTE = "scadute";
	public static final String LABEL_STATO_SCADUTE = "Scadute";
	
	public static final String LABEL_STATO_ATTESA_CALLBACK = "In attesa della callback";
	public static final String LABEL_STATO_RISORSA_DISPONIBILE = "Risposta disponibile";
	public static final String LABEL_STATO_CONFERMATA = "Conferma di ricezione effettuata";
	public static final String LABEL_STATO_SCADUTA = "Scaduta";
	
	public static final String VALUE_QUALSIASI = "";
	public static final String LABEL_QUALSIASI = "-";
	public static final String VALUE_NON_PRESENTE = "-";
	
}
