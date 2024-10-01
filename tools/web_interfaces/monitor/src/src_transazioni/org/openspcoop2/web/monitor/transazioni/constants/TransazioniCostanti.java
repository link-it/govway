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
package org.openspcoop2.web.monitor.transazioni.constants;

import org.openspcoop2.web.monitor.core.constants.Costanti;

/**
 * TransazioniCostanti
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TransazioniCostanti {
	
	public static final String NOME_ACTION_RICERCA = "transazioni";
	public static final String NOME_ACTION_RICERCA_LVL2 = "transazioni_lvl2";

	// Indica il numero delle possibili classi CSS per i tag dei gruppi, modificare questo valore se si vuole modificare il numero delle classi disponibili
	public static final Integer NUMERO_GRUPPI_CSS = 30;

	/* costanti properties file messages_it.properties*/ 
	public static final String TRANSAZIONI_SUMMARY_SOGGETTO_LOCALE_SERVIZIO_LABEL_KEY = "transazioni.summary.soggettoLocaleServizio.label";


	public static final String TRANSAZIONI_ELENCO_RUOLO_PDD_DELEGATA_LABEL_KEY = "transazioni.elenco.ruoloPdd.delegata.label";
	public static final String TRANSAZIONI_ELENCO_RUOLO_PDD_DELEGATA_ICON_KEY = "transazioni.elenco.ruoloPdd.delegata.icona";
	public static final String TRANSAZIONI_ELENCO_RUOLO_PDD_APPLICATIVA_LABEL_KEY = "transazioni.elenco.ruoloPdd.applicativa.label";
	public static final String TRANSAZIONI_ELENCO_RUOLO_PDD_APPLICATIVA_ICON_KEY = "transazioni.elenco.ruoloPdd.applicativa.icona";
	public static final String TRANSAZIONI_ELENCO_RUOLO_PDD_ROUTER_LABEL_KEY = "transazioni.elenco.ruoloPdd.router.label";
	public static final String TRANSAZIONI_ELENCO_RUOLO_PDD_ROUTER_ICON_KEY = "transazioni.elenco.ruoloPdd.router.icona";
	public static final String TRANSAZIONI_ELENCO_RUOLO_PDD_IM_LABEL_KEY = "transazioni.elenco.ruoloPdd.im.label";
	public static final String TRANSAZIONI_ELENCO_RUOLO_PDD_IM_ICON_KEY = "transazioni.elenco.ruoloPdd.im.icona";

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_LABEL_KEY;
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITTENTE_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITTENTE_LABEL_KEY;
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_LABEL_KEY;

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_ICON_KEY = "transazioni.search.tipoRicerca.temporale.ricercaTemporale.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_BREADCUMP_KEY = "transazioni.search.tipoRicerca.temporale.ricercaTemporale.breadcrumb"; 

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_LIBERA_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_LIBERA_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_LIBERA_ICON_KEY = "transazioni.search.tipoRicerca.temporale.ricercaLibera.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_LIBERA_BREADCUMP_KEY = "transazioni.search.tipoRicerca.temporale.ricercaLibera.breadcrumb"; 

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_TOKEN_INFO_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_TOKEN_INFO_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_TOKEN_INFO_ICON_KEY = "transazioni.search.tipoRicerca.mittente.ricercaTokenInfo.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_TOKEN_INFO_BREADCUMP_KEY = "transazioni.search.tipoRicerca.mittente.ricercaTokenInfo.breadcrumb"; 

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_SOGGETTO_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_SOGGETTO_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_SOGGETTO_ICON_KEY = "transazioni.search.tipoRicerca.mittente.ricercaSoggetto.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_SOGGETTO_BREADCUMP_KEY = "transazioni.search.tipoRicerca.mittente.ricercaSoggetto.breadcrumb"; 

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_APPLICATIVO_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_APPLICATIVO_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_APPLICATIVO_ICON_KEY = "transazioni.search.tipoRicerca.mittente.ricercaApplicativo.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_APPLICATIVO_BREADCUMP_KEY = "transazioni.search.tipoRicerca.mittente.ricercaApplicativo.breadcrumb"; 

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_IDENTIFICATIVO_AUTENTICATO_ICON_KEY = "transazioni.search.tipoRicerca.mittente.ricercaIdentificativoAutenticato.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_IDENTIFICATIVO_AUTENTICATO_BREADCUMP_KEY = "transazioni.search.tipoRicerca.mittente.ricercaIdentificativoAutenticato.breadcrumb"; 

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_INDIRIZZO_IP_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_INDIRIZZO_IP_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_INDIRIZZO_IP_ICON_KEY = "transazioni.search.tipoRicerca.mittente.ricercaIndirizzoIP.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_INDIRIZZO_IP_BREADCUMP_KEY = "transazioni.search.tipoRicerca.mittente.ricercaIndirizzoIP.breadcrumb"; 

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_ICON_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoApplicativo.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_BREADCUMP_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoApplicativo.breadcrumb";
	
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_BASE_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_BASE_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_BASE_ICON_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoApplicativo.lvl2.ricercaBase.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_BASE_BREADCRUMB_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoApplicativo.lvl2.ricercaBase.breadcrumb";
	
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_AVANZATA_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_AVANZATA_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_AVANZATA_ICON_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoApplicativo.lvl2.ricercaLibera.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_AVANZATA_BREADCRUMB_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoApplicativo.lvl2.ricercaLibera.breadcrumb";

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_MESSAGGIO_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_MESSAGGIO_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_MESSAGGIO_ICON_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoMessaggio.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_MESSAGGIO_BREADCUMP_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoMessaggio.breadcrumb"; 

	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_TRANSAZIONE_LABEL_KEY = Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_TRANSAZIONE_LABEL_KEY; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_TRANSAZIONE_ICON_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoTransazione.icona"; 
	public static final String TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_TRANSAZIONE_BREADCUMP_KEY = "transazioni.search.tipoRicerca.id.ricercaIdentificativoTransazione.breadcrumb"; 

	public static final String TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_LATENZA_ERRORE = "col-latenza-errore";
	public static final String TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_LATENZA_FAULT = "col-latenza-fault";
	public static final String TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_LATENZA_OK = "col-latenza-ok";
	public static final String TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_ERRORE = "col-esito-errore";
	public static final String TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_FAULT = "col-esito-fault";
	public static final String TRANSAZIONI_ELENCO_CUSTOM_CLASSE_CSS_COL_ESITO_OK = "col-esito-ok";

	public static final String DETTAGLIO_TRANSAZIONE_NOME_TAB_INFO_GENERALI = "infoGenerali"; 
	public static final String DETTAGLIO_TRANSAZIONE_NOME_TAB_DETTAGLI_MESSAGGIO = "dettagliMessaggio";
	public static final String DETTAGLIO_TRANSAZIONE_NOME_TAB_DIAGNOSTICI = "diagnostici";
	public static final String DETTAGLIO_TRANSAZIONE_NOME_TAB_INFO_AVANZATE = "infoAvanzate";
	public static final String DETTAGLIO_TRANSAZIONE_NOME_TAB_TEMPI_ELABORAZIONE = "tempiElaborazione";
	public static final String DETTAGLIO_TRANSAZIONE_NOME_TAB_CONSEGNE_MULTIPLE = "consegneMultiple";

	public static final String TRANSAZIONI_ELENCO_ESITO_SEND_ICON_KEY = "transazioni.elenco.esito.send.icon";
	public static final String TRANSAZIONI_ELENCO_ESITO_SEND_RESPONSE_ICON_KEY = "transazioni.elenco.esito.sendResponse.icon";
	public static final String TRANSAZIONI_ELENCO_ESITO_OK_ICON_KEY = "transazioni.elenco.esito.ok.icon";
	public static final String TRANSAZIONI_ELENCO_ESITO_ERROR_ICON_KEY = "transazioni.elenco.esito.error.icon";
	public static final String TRANSAZIONI_ELENCO_ESITO_WARNING_ICON_KEY = "transazioni.elenco.esito.warning.icon";
	

	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_ZERO_TENTATIVI_ICON_KEY = "transazioniApplicativoServer.elenco.applicativo.inCoda.zeroTentativi.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_IM_ZERO_PRELIEVI_ICON_KEY = "transazioniApplicativoServer.elenco.im.inCoda.zeroPrelievi.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_ZERO_TENTATIVI_ICON_KEY = "transazioniApplicativoServer.elenco.applicativoIm.inCoda.zeroTentativi.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_PIU_TENTATIVI_ICON_KEY = "transazioniApplicativoServer.elenco.applicativo.inCoda.piuTentativi.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_IM_PIU_PRELIEVI_ICON_KEY = "transazioniApplicativoServer.elenco.im.inCoda.piuPrelievi.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_PIU_TENTATIVI_ICON_KEY = "transazioniApplicativoServer.elenco.applicativoIm.inCoda.piuTentativi.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_CONSEGNA_APPLICATIVO_ICON_KEY = "transazioniApplicativoServer.elenco.applicativo.terminataConsegna.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_GESTIONE_IM_ICON_KEY = "transazioniApplicativoServer.elenco.im.terminataGestione.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_CONSEGNA_APPLICATIVO_IM_ICON_KEY = "transazioniApplicativoServer.elenco.applicativoIm.terminataConsegna.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_MESSAGGIO_SCADUTO_ICON_KEY = "transazioniApplicativoServer.elenco.messaggioScaduto.icon";

	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_ZERO_TENTATIVI_LABEL_KEY = "transazioniApplicativoServer.elenco.applicativo.inCoda.zeroTentativi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_IM_ZERO_PRELIEVI_LABEL_KEY = "transazioniApplicativoServer.elenco.im.inCoda.zeroPrelievi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_ZERO_TENTATIVI_LABEL_KEY = "transazioniApplicativoServer.elenco.applicativoIm.inCoda.zeroTentativi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_PIU_TENTATIVI_LABEL_KEY = "transazioniApplicativoServer.elenco.applicativo.inCoda.piuTentativi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_IM_PIU_PRELIEVI_LABEL_KEY = "transazioniApplicativoServer.elenco.im.inCoda.piuPrelievi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IN_CODA_APPLICATIVO_IM_PIU_TENTATIVI_LABEL_KEY = "transazioniApplicativoServer.elenco.applicativoIm.inCoda.piuTentativi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_CONSEGNA_APPLICATIVO_LABEL_KEY = "transazioniApplicativoServer.elenco.applicativo.terminataConsegna.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_GESTIONE_IM_LABEL_KEY = "transazioniApplicativoServer.elenco.im.terminataGestione.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_CONSEGNA_APPLICATIVO_IM_LABEL_KEY = "transazioniApplicativoServer.elenco.applicativoIm.terminataConsegna.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_TERMINATA_CONSEGNA_APPLICATIVO_IM_TERMINATA_IM_LABEL_KEY = "transazioniApplicativoServer.elenco.applicativoIm.terminataConsegna.im.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_MESSAGGIO_SCADUTO_LABEL_KEY = "transazioniApplicativoServer.elenco.messaggioScaduto.label";

	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IM_TITOLO_IN_PROGRESS_LABEL_KEY = "transazioniApplicativoServer.elenco.im.titolo.inProgress.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_IM_TITOLO_COMPLETATO_LABEL_KEY = "transazioniApplicativoServer.elenco.im.titolo.completato.label";
	
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_IN_CODA_LABEL_KEY = "transazioniApplicativoServer.elenco.data.inCoda.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ULTIMA_CONSEGNA_LABEL_KEY = "transazioniApplicativoServer.elenco.data.ultimaConsegna.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ULTIMO_PRELIEVO_LABEL_KEY = "transazioniApplicativoServer.elenco.data.ultimoPrelievo.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_ELIMINAZIONE_LABEL_KEY = "transazioniApplicativoServer.elenco.data.eliminazione.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_DATA_SCADUTO_LABEL_KEY = "transazioniApplicativoServer.elenco.data.scaduto.label";


	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_NUMERO_TENTATIVI_LABEL_KEY = "transazioniApplicativoServer.elenco.numeroTentativi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_NUMERO_PRELIEVI_LABEL_KEY = "transazioniApplicativoServer.elenco.numeroPrelievi.label";

	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IN_CORSO_ICON_KEY = "transazioniApplicativoServer.elenco.esito.inCorso.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_DONE_ICON_KEY = "transazioniApplicativoServer.elenco.esito.im.done.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_OK_ICON_KEY = "transazioniApplicativoServer.elenco.esito.ok.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_ERROR_ICON_KEY = "transazioniApplicativoServer.elenco.esito.error.icon";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_WARNING_ICON_KEY = "transazioniApplicativoServer.elenco.esito.warning.icon";


	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_LABEL_KEY = "transazioniApplicativoServer.elenco.esito.applicativo.inCoda.zeroTentativi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_IN_CODA_ZERO_TENTATIVI_LABEL_KEY = "transazioniApplicativoServer.elenco.esito.im.inCoda.zeroPrelievi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_IN_CODA_ZERO_TENTATIVI_LABEL_KEY = "transazioniApplicativoServer.elenco.esito.applicativoIm.inCoda.zeroTentativi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_PIU_TENTATIVI_LABEL_KEY = "transazioniApplicativoServer.elenco.esito.im.inCoda.piuPrelievi.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_TERMINATA_GESTIONE_LABEL_KEY = "transazioniApplicativoServer.elenco.esito.im.terminataGestione.label";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_TERMINATA_CONSEGNA_LABEL_KEY = "transazioniApplicativoServer.elenco.esito.applicativoIm.terminataConsegna.im.label";

	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY = "transazioniApplicativoServer.elenco.esito.applicativo.inCoda.zeroTentativi.tooltip";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY = "transazioniApplicativoServer.elenco.esito.im.inCoda.zeroPrelievi.tooltip";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_IN_CODA_ZERO_TENTATIVI_TOOLTIP_KEY = "transazioniApplicativoServer.elenco.esito.applicativoIm.inCoda.zeroTentativi.tooltip";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_PIU_TENTATIVI_TOOLTIP_KEY = "transazioniApplicativoServer.elenco.esito.im.inCoda.piuPrelievi.tooltip";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_IM_TERMINATA_GESTIONE_TOOLTIP_KEY = "transazioniApplicativoServer.elenco.esito.im.terminataGestione.tooltip";
	public static final String TRANSAZIONI_APPLICATIVO_SERVER_ELENCO_ESITO_APPLICATIVO_IM_TERMINATA_CONSEGNA_TOOLTIP_KEY = "transazioniApplicativoServer.elenco.esito.applicativoIm.terminataConsegna.im.tooltip";

	public static final String[] SEARCH_FORM_FIELDS_DA_NON_SALVARE= {
			"transazioniService",
			"ricercaPerIdApplicativo",
			"ricercaSelezionataParameters",
			"tabellaRicerchePersonalizzate",
			"default_modalitaRicercaStorico",
			"livelloRicerca",
			"backRicerca",
			"integrationManagerEnabled",
			"visualizzaStoricoCustomEnabled",
			"visualizzaStoricoCustomColonnaRuoloTransazioneEnabled",
			"visualizzaLiveCustomEnabled",
			"visualizzaLiveCustomColonnaRuoloTransazioneEnabled",
			"visualizzaConsegneMultipleCustomEnabled",
	};
}
