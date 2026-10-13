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
package org.openspcoop2.web.ctrlstat.servlet.signal_hub;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * SemiSignalHubCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemiSignalHubCostanti {

	private SemiSignalHubCostanti() {}
	
	public static final String OBJECT_NAME_SIGNAL_HUB_SEMI = "signalHubSemi";
	
	public static final String SERVLET_NAME_SIGNAL_HUB_SEMI_CHANGE = OBJECT_NAME_SIGNAL_HUB_SEMI+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_SIGNAL_HUB_SEMI_LIST = OBJECT_NAME_SIGNAL_HUB_SEMI+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_SIGNAL_HUB_SEMI = new ArrayList<>();
	public static List<String> getServletSemiSignalHub() {
		return SERVLET_SIGNAL_HUB_SEMI;
	}
	static{
		SERVLET_SIGNAL_HUB_SEMI.add(SERVLET_NAME_SIGNAL_HUB_SEMI_CHANGE);
		SERVLET_SIGNAL_HUB_SEMI.add(SERVLET_NAME_SIGNAL_HUB_SEMI_LIST);
	}
	
	public static final String LABEL_SIGNAL_HUB_SEMI = "Semi Signal Hub";
	
	// utilizzato solamente se non e' possibile leggere la configurazione del profilo ModI (stesso default del gateway)
	public static final int DEFAULT_SEED_CACHE_REFRESH_SECONDS = 300;
	public static final String LABEL_SEME_SIGNAL_HUB = "Seme Signal Hub";
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_SIGNAL_HUB_SEMI_ID_SERVIZIO = "signalHubSemiIdServizio";
	public static final String PARAMETRO_SIGNAL_HUB_SEMI_ID_SEME = "signalHubSemiIdSeme";
	public static final String PARAMETRO_SIGNAL_HUB_SEMI_ROTAZIONE = "signalHubSemiRotazione";
	
	/* LABEL */
	
	public static final String LABEL_EROGAZIONE = "Erogazione";
	public static final String LABEL_EROGATORE = "Erogatore";
	public static final String LABEL_DATA_GENERAZIONE = "Generato il";
	public static final String LABEL_DURATA = "Durata";
	public static final String LABEL_SCADENZA = "Scadenza";
	public static final String LABEL_STATO = "Stato";
	public static final String LABEL_SIGNAL_ID = "SignalId SEEDUPDATE";
	public static final String LABEL_ALGORITMO = "Algoritmo";
	public static final String LABEL_DIMENSIONE = "Dimensione";
	public static final String LABEL_DATA_RICHIESTA_ROTAZIONE = "Rotazione richiesta il";
	public static final String LABEL_SOSTITUITO_IL = "Sostituito il";
	public static final String LABEL_ESERVICE_ID = "e-service ID";
	public static final String LABEL_ULTIMO_SIGNAL_ID = "Ultimo signalId generato";
	public static final String LABEL_SORGENTE_DATI = "Sorgente Dati";
	
	public static final String LABEL_SEZIONE_SEME_IN_USO = "Seme in uso";
	public static final String LABEL_SEZIONE_SEMI_PRECEDENTI = "Semi precedenti";
	public static final String LABEL_SEME_PRECEDENTE_PREFIX = "Seme SEEDUPDATE ";
	public static final String LABEL_SEME_PRECEDENTE_PRIMO = "Primo seme";
	
	public static final String SUFFIX_GIORNI = " giorni";
	public static final String SUFFIX_BYTE = " byte";
	
	public static final String DESCRIZIONE_SEME_VALIDO = "Generato il %s, scadenza %s";
	public static final String DESCRIZIONE_SEME_SCADUTO = "Generato il %s, scaduto il %s: verrà sostituito alla pubblicazione del prossimo segnale";
	public static final String DESCRIZIONE_SEME_ROTAZIONE_RICHIESTA = "Generato il %s, rotazione richiesta: verrà sostituito al primo segnale successivo al %s";
	
	public static final String VALUE_PRIMO_SEME = "- (primo seme generato, nessun SEEDUPDATE)";
	public static final String VALUE_NON_PRESENTE = "-";
	public static final String VALUE_ULTIMO_SIGNAL_ID_NON_DISPONIBILE = "Non disponibile (lettura della base dati runtime non riuscita)";
	public static final String VALUE_ULTIMO_SIGNAL_ID_NON_GENERATO = "- (nessun segnale pubblicato)";
	
	/* STATI */
	
	public static final String LABEL_STATO_VALIDO = "Valido";
	public static final String LABEL_STATO_SCADUTO = "Scaduto";
	public static final String LABEL_STATO_ROTAZIONE_RICHIESTA = "Rotazione richiesta";
	
	public static final String LABEL_SOSTITUZIONE = "Sostituzione";
	public static final String DESCRIZIONE_SOSTITUZIONE_SCADUTO = "Alla pubblicazione del prossimo segnale";
	public static final String DESCRIZIONE_SOSTITUZIONE_ROTAZIONE_RICHIESTA = "Alla pubblicazione del primo segnale successivo al %s";
	public static final String TOOLTIP_STATO_ROTAZIONE_RICHIESTA = "Rotazione richiesta il %s: il seme verrà sostituito alla pubblicazione del primo segnale successivo al %s";
	public static final String TOOLTIP_STATO_SCADUTO = "Il seme verrà sostituito alla pubblicazione del prossimo segnale";
	public static final String LABEL_STATO_SOSTITUITO = "Sostituito";
	
	/* ROTAZIONE FORZATA */
	
	public static final String LABEL_FORZA_ROTAZIONE = "Forza rotazione seme";
	public static final String MESSAGGIO_CONFERMA_ROTAZIONE = "Forzare la rotazione del seme in uso per l'erogazione '%s'?<br/><br/>"+
			"Il nuovo seme verrà generato, e comunicato tramite il segnale SEEDUPDATE, alla pubblicazione del primo segnale successivo al %s.";
	public static final String MESSAGGIO_ROTAZIONE_RICHIESTA = "Rotazione del seme richiesta: il nuovo seme verrà generato alla pubblicazione del primo segnale successivo al %s";
	public static final String MESSAGGIO_ROTAZIONE_GIA_RICHIESTA = "La rotazione del seme in uso risulta già richiesta";
	public static final String MESSAGGIO_SEME_GIA_RUOTATO = "Il seme è stato nel frattempo sostituito; verificare il seme attualmente in uso prima di richiederne la rotazione";
	public static final String MESSAGGIO_SEME_NON_PRESENTE = "Per l'erogazione non risulta generato alcun seme";
	
}
