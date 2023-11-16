/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.apc.api;

import java.util.List;
import java.util.ArrayList;

import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCostanti;

/**
 * ApiCostanti
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiCostanti {
	
	private ApiCostanti() {}
	
	public static final String OBJECT_NAME_APC_API = AccordiServizioParteComuneCostanti.OBJECT_NAME_APC + "Api";
	
	public static final String SERVLET_NAME_APC_API_ADD = OBJECT_NAME_APC_API+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_APC_API_CHANGE = OBJECT_NAME_APC_API+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_APC_API_LIST = OBJECT_NAME_APC_API+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final String SERVLET_NAME_APC_API_DEL = OBJECT_NAME_APC_API+org.openspcoop2.web.lib.mvc.Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	
	private static final List<String> SERVLET_APC_API = new ArrayList<>();
	public static List<String> getServletApcApi() {
		return SERVLET_APC_API;
	}
	static{
		SERVLET_APC_API.add(SERVLET_NAME_APC_API_ADD);
		SERVLET_APC_API.add(SERVLET_NAME_APC_API_CHANGE);
		SERVLET_APC_API.add(SERVLET_NAME_APC_API_LIST);
		SERVLET_APC_API.add(SERVLET_NAME_APC_API_DEL);
	}
	
	public static final String SESSION_ATTRIBUTE_VISTA_APC_API = "vistaApi";

	public static final String APC_API_NOME_VISTA_CUSTOM_LISTA_API = "api";
	public static final String APC_API_NOME_VISTA_CUSTOM_FORM_API = "api";
	
	
	public static final String APC_API_PARAMETRO_STATO_SERVIZI = "statoServ";
	
	public static final String MESSAGE_METADATI_APC_API_EDIT = "{1} ({0})";
	public static final String MESSAGE_METADATI_APC_API_LIST = "API {0}";
	public static final String MESSAGE_METADATI_APC_API_CON_PROFILO = "API {0}, Profilo Interoperabilit&agrave;: {1}";
	public static final String MESSAGE_METADATI_APC_API_PROFILO = "Profilo Interoperabilit&agrave;: {0}";
	public static final String MESSAGE_METADATI_APC_API_LIST_CON_CANALE = "API {0}, Canale: {1}";
	public static final String MESSAGE_METADATI_APC_API_CON_CANALE_CON_PROFILO = "API {0}, Canale: {1}, Profilo Interoperabilit&agrave;: {2}";
	
	public static final String APC_API_ICONA_MODIFICA_API = CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE;
	public static final String APC_API_ICONA_MODIFICA_API_TOOLTIP = CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP;
	public static final String APC_API_ICONA_MODIFICA_API_TOOLTIP_CON_PARAMETRO = CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO;
	
	public static final String APC_API_ICONA_AGGIUNGI_DESCRIZIONE = CostantiControlStation.ICONA_AGGIUNGI_DESCRIZIONE;
	public static final String APC_API_ICONA_AGGIUNGI_DESCRIZIONE_TOOLTIP_CON_PARAMETRO = CostantiControlStation.AGGIUNGI_DESCRIZIONE_TOOLTIP_CON_PARAMETRO;
	
	public static final String APC_API_ICONA_VISUALIZZA = CostantiControlStation.ICONA_VISUALIZZA;
	public static final String APC_API_ICONA_VISUALIZZA_TOOLTIP = CostantiControlStation.ICONA_VISUALIZZA_TOOLTIP;
	public static final String APC_API_ICONA_VISUALIZZA_TOOLTIP_CON_PARAMETRO = CostantiControlStation.ICONA_VISUALIZZA_TOOLTIP_CON_PARAMETRO;
	
	public static final String APC_API_ICONA_DOWNLOAD_DOCUMENTO_INTERFACCIA = "&#xE884;";
	public static final String APC_API_ICONA_DOWNLOAD_DOCUMENTO_ARCHIVE = "&#xE149;";
	
	public static final String APC_API_ICONA_DOWNLOAD_DOCUMENTO_INTERFACCIA_TOOLTIP = "Download";
	public static final String APC_API_ICONA_DOWNLOAD_DOCUMENTO_INTERFACCIA_TOOLTIP_CON_PARAMETRO = "Download {0}";
	
	public static final String APC_API_ICONA_STATO_RISORSE_TUTTE_ABILITATE_TOOLTIP = "API correttamente configurata";
	public static final String APC_API_ICONA_STATO_RISORSE_TUTTE_DISABILITATE_TOOLTIP = "Nessuna risorsa configurata sull'API";
	
	public static final String APC_API_ICONA_STATO_SERVIZI_TUTTI_ABILITATI_TOOLTIP = "API correttamente configurata";
	public static final String APC_API_ICONA_STATO_SERVIZI_PARZIALMENTE_ABILITATI_TOOLTIP = "Per alcuni servizi dell'API non sono state configurate azioni";
	public static final String APC_API_ICONA_STATO_SERVIZI_TUTTI_SENZA_AZIONE_TOOLTIP = "Tutti i servizi dell'API non possiedono azioni";
	public static final String APC_API_ICONA_STATO_SERVIZI_TUTTI_DISABILITATI_TOOLTIP = "Nessun servizio configurato sull'API";
	public static final String APC_API_ICONA_STATO_SERVIZIO_PARZIALMENTE_CONFIGURATO_DISABILITATI_TOOLTIP = "Nessun'azione configurata sul servizio dell'API";
	
	public static final String APC_API_LABEL_APS_INFO_GENERALI = "Informazioni Generali";
	public static final String APC_API_LABEL_PARAMETRO_INTERFACCIA = "Interfaccia";
	public static final String APC_API_LABEL_GESTIONE_RISORSE = "Risorse";
	public static final String APC_API_LABEL_GESTIONE_SERVIZI = "Servizi";
	public static final String APC_API_LABEL_GESTIONE_ALLEGATI = "Allegati";
	public static final String APC_API_LABEL_GESTIONE_OPZIONI_AVANZATE = "Opzioni Avanzate";
	public static final String APC_API_LABEL_GESTIONE_GRUPPI = GruppiCostanti.LABEL_GRUPPI;
	public static final String APC_API_LABEL_NUOVA_VERSIONE_API = "Nuova Versione";
	
	public static final String PARAMETRO_APC_API_GESTIONE_GRUPPO = AccordiServizioParteComuneCostanti.PARAMETRO_APC_GRUPPO;
	public static final String PARAMETRO_APC_API_NUOVA_VERSIONE = AccordiServizioParteComuneCostanti.PARAMETRO_APC_API_NUOVA_VERSIONE;
	
	public static final String PARAMETRO_APC_API_GESTIONE_PARZIALE = Costanti.CONSOLE_PARAMETRO_APC_API_GESTIONE_PARZIALE;
	public static final String VALORE_PARAMETRO_APC_API_INFORMAZIONI_GENERALI = Costanti.CONSOLE_VALORE_PARAMETRO_APC_API_INFORMAZIONI_GENERALI;
	public static final String VALORE_PARAMETRO_APC_API_SOGGETTO_REFERENTE = "apiSoggettoReferente";
	public static final String VALORE_PARAMETRO_APC_API_PROFILO = "apiProfilo";
	public static final String VALORE_PARAMETRO_APC_API_DESCRIZIONE = "apiDescrizione";
	public static final String VALORE_PARAMETRO_APC_API_GRUPPI = "apiGruppi";
	public static final String VALORE_PARAMETRO_APC_API_CANALE = "apiCanale";
	public static final String VALORE_PARAMETRO_APC_API_GESTIONE_SPECIFICA_INTERFACCE = "apiGestioneSpecificaInterfacce";
	public static final String VALORE_PARAMETRO_APC_API_OPZIONI_AVANZATE = "apiOpzioniAvanzate";
	public static final String VALORE_PARAMETRO_APC_API_GESTIONE_PARZIALE_WSDL_CHANGE = "apiWsdlChange";
	
	public static final String APC_API_ICONA_GESTIONE_RISORSE = "&#xE896;";
	public static final String APC_API_ICONA_GESTIONE_SERVIZI = "&#xE896;";
	public static final String APC_API_ICONA_GESTIONE_ALLEGATI = "&#xE2BC;";
	public static final String APC_API_ICONA_NUOVA_VERSIONE_API = "&#xE02E;";
	public static final String APC_API_ICONA_GESTIONE_OPZIONI_AVANZATE = "&#xE8B8;";
	
	public static final String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "L'API non risulta utilizzata in alcuna configurazione";

}
