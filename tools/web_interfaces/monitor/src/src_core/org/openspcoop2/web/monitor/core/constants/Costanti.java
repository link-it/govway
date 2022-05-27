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
package org.openspcoop2.web.monitor.core.constants;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * Costanti
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Costanti {

	public static final String LABEL_MENU_MODALITA_CORRENTE_WITH_PARAM = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_COMPACT+": {0}";
	public static final String LABEL_MENU_SOGGETTO_CORRENTE_WITH_PARAM = "Soggetto: {0}";
	
	public final static String LABEL_PARAMETRO_SOGGETTI_OPERATIVI = "Soggetti Operativi";
	public final static String LABEL_PARAMETRO_SOGGETTO_OPERATIVO = "Soggetto Operativo";
	public final static String LABEL_PARAMETRO_SOGGETTO_COMPACT = "Soggetto Operativo";
	public final static String LABEL_PARAMETRO_SOGGETTI_COMPACT = "Soggetti Operativi";
	public static final String LABEL_PROFILO = "Profilo";
	
	public static final String LABEL_PARAMETRO_MODALITA_ALL = "Tutti";
	public static final String VALUE_PARAMETRO_MODALITA_ALL = "qualsiasi";
	
	public static final String ICONS_BASE = "/images/tema_link/" ;
	
	public static final String ICONA_MENU_UTENTE_CHECKED = ICONS_BASE +"checkbox_checked_white.png";
	public static final String ICONA_MENU_UTENTE_UNCHECKED = ICONS_BASE + "checkbox_unchecked_white.png";
	
	public static final String DATASOURCE = "dataSource";
	
	public static final String TIPODATABASE ="tipoDatabase";
	
	public static final String SHOWSQL ="showSql";
	
	public static final String NON_SELEZIONATO = "--";
	
	public static final String SEPARATORE_TIPO_NOME = org.openspcoop2.web.monitor.core.utils.Costanti.SEPARATORE_TIPO_NOME;
	public static final String SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_APERTA = org.openspcoop2.web.monitor.core.utils.Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_APERTA;
	public static final String SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_CHIUSA = org.openspcoop2.web.monitor.core.utils.Costanti.SOGGETTO_SERVIZIO_SEPARATORE_CON_PARENTISI_CHIUSA;
	
	public static final String LABEL_PARAMETRO_RISORSA = "Risorsa";
	public static final String LABEL_PARAMETRO_AZIONE = "Azione";
	
	public static final String LABEL_INFORMAZIONE_NON_DISPONIBILE = "Informazione non disponibile";
	public static final String LABEL_INFORMAZIONE_NON_PIU_PRESENTE = "Informazione non piu' presente";
	
	public static final String VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO = "tokenInfo";
	public static final String VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO = "identificativoAutenticato";
	public static final String VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO = "applicativo";
	public static final String VALUE_TIPO_RICONOSCIMENTO_SOGGETTO = "soggetto";
	public static final String VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP = "ip";
	
	public static final String VALUE_CREDENZIALE_TRASPORTO_PREFIX = "trasporto";
	
	public static final String VALUE_CLIENT_ADDRESS_SOCKET = "socket";
	public static final String VALUE_CLIENT_ADDRESS_TRASPORTO = "transport";
	
	public static final String ICONA_ANDAMENTO_TEMPORALE = "timelapse";
	public static final String ICONA_ID_APPLICATIVO = "apps";
	public static final String ICONA_ID_MESSAGGIO = "message";
	public static final String ICONA_ID_TRANSAZIONE = "swap_horiz";
	public static final String ICONA_INFO_WHITE = "info_outline";

	public final static String LABEL_OPENSPCOOP2_WEB = "https://govway.org";
	
	public final static Integer SELECT_ITEM_VALORE_MASSIMO_ENTRIES = Integer.valueOf(1000);
	
	public final static List<SelectItem> SELECT_ITEM_ENTRIES = new ArrayList<>();
	static {
		SELECT_ITEM_ENTRIES.add(new SelectItem(Integer.valueOf(25), "25 Entries"));  
		SELECT_ITEM_ENTRIES.add(new SelectItem(Integer.valueOf(75), "75 Entries"));
		SELECT_ITEM_ENTRIES.add(new SelectItem(Integer.valueOf(125), "125 Entries"));
		SELECT_ITEM_ENTRIES.add(new SelectItem(Integer.valueOf(250), "250 Entries"));
		SELECT_ITEM_ENTRIES.add(new SelectItem(Integer.valueOf(500), "500 Entries"));
		SELECT_ITEM_ENTRIES.add(new SelectItem(SELECT_ITEM_VALORE_MASSIMO_ENTRIES, "1000 Entries"));
	}
	
	public static final String SELECT_ITEM_VALORE_MASSIMO_ENTRIES_LABEL_KEY = "commons.search.selezionaNumeroMassimoEntries.label";
	public static final String SELEZIONATI_PRIMI_X_ELEMENTI_LABEL_KEY = "commons.search.selezionatiPrimiElementi.label";
	
	
	
	/* costanti properties file messages_it.properties*/ 
	
	public static final String DATA_LABEL_KEY = "commons.data.label";
	public static final String ESITO_LABEL_KEY = "commons.esito.label";
	public static final String DESCRIZIONE_LABEL_KEY = "commons.descrizione.label";
	public static final String AZIONE_LABEL_KEY = "commons.azione.label";
	public static final String SERVIZIO_APPLICATIVO_LABEL_KEY = "commons.applicativo.label";
	public static final String API_LABEL_KEY = "commons.api.label";
	public static final String SERVIZIO_LABEL_KEY = "commons.servizio.label";
	public static final String SOGGETTO_LABEL_KEY = "commons.soggetto.label";
	public static final String SOGGETTO_LOCALE_LABEL_KEY = "commons.soggettoLocale.label";
	public static final String SOGGETTO_REMOTO_LABEL_KEY = "commons.soggettoRemoto.label";
	public static final String NOME_LABEL_KEY = "commons.nome.label";
	public static final String RICHIESTA_LABEL_KEY = "commons.richiesta.label";
	public static final String EROGATORE_LABEL_KEY = "commons.erogatore.label";
	public static final String FRUITORE_LABEL_KEY = "commons.fruitore.label";
	public static final String TOKEN_INFO_KEY = "commons.tokenInfo.label";
	public static final String IDENTIFICATIVO_AUTENTICATO_KEY = "commons.identificativoAutenticato.label";
	public static final String INDIRIZZO_IP_KEY = "commons.indirizzoIP.label";
	
	public static final String SEARCH_APPLICATIVO_DEFAULT_LABEL_KEY = "commons.search.applicativo.defaultLabel";
	public static final String SEARCH_APPLICATIVO_DEFAULT_LABEL_NO_SOGGETTO_LOCALE_KEY = "commons.search.applicativo.defaultLabelNoSoggettoLocal";
	public static final String SEARCH_APPLICATIVO_DEFAULT_LABEL_NO_SOGGETTO_FRUITORE_KEY = "commons.search.applicativo.defaultLabelNoSoggettoFruitore";
	
	/* Home page utente*/
	
	/* */
	public final static String OGGETTO_STATO_UTENTE_INTERVALLO_TEMPORALE_HOME_PAGE =  NomiTabelle.WELCOME_SCREEN.toString(); 
	public final static String OGGETTO_STATO_UTENTE_HOME_PAGE = "HOME_PAGE";
	
	public final static String LABEL_VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI_KEY = "utenti.profilo.homePage.transazioni";
	public final static String LABEL_VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE_KEY = "utenti.profilo.homePage.statistiche";
	
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO_KEY = "utenti.profilo.intervalloTemporaleHomePage.noGrafico";
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE_KEY = "utenti.profilo.intervalloTemporaleHomePage.ultime24ore";
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI_KEY = "utenti.profilo.intervalloTemporaleHomePage.ultimi7giorni";
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI_KEY = "utenti.profilo.intervalloTemporaleHomePage.ultimi30giorni";
	public final static String LABEL_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO_KEY = "utenti.profilo.intervalloTemporaleHomePage.ultimoAnno";
	
	public final static String VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI = "transazioni";
	public final static String VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_STATISTICHE = "summary";
	
	public final static String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_NO_GRAFICO = "--";
	public final static String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIME_24_ORE = "Ultime 24 ore";
	public final static String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI = "Ultimi 7 giorni";
	public final static String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_30_GIORNI = "Ultimi 30 giorni";
	public final static String VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMO_ANNO = "Ultimo anno";
	
}
