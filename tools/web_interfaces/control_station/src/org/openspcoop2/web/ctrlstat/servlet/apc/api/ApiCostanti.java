/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.util.Vector;

import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCostanti;

/**
 * ApiCostanti
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiCostanti extends AccordiServizioParteComuneCostanti {
	
	
	public final static String OBJECT_NAME_APC_API = OBJECT_NAME_APC + "Api";
	
	public final static String SERVLET_NAME_APC_API_ADD = OBJECT_NAME_APC_API+"Add.do";
	public final static String SERVLET_NAME_APC_API_CHANGE = OBJECT_NAME_APC_API+"Change.do";
	public final static String SERVLET_NAME_APC_API_LIST = OBJECT_NAME_APC_API+"List.do";
	public final static String SERVLET_NAME_APC_API_DEL = OBJECT_NAME_APC_API+"Del.do";
	
	public final static Vector<String> SERVLET_APC_API = new Vector<String>();
	static{
		SERVLET_APC_API.add(SERVLET_NAME_APC_API_ADD);
		SERVLET_APC_API.add(SERVLET_NAME_APC_API_CHANGE);
		SERVLET_APC_API.add(SERVLET_NAME_APC_API_LIST);
		SERVLET_APC_API.add(SERVLET_NAME_APC_API_DEL);
	}
	
	public final static String SESSION_ATTRIBUTE_VISTA_APC_API = "vistaApi";

	public final static String APC_API_NOME_VISTA_CUSTOM_LISTA_API = "api";
	public final static String APC_API_NOME_VISTA_CUSTOM_FORM_API = "api";
	
	
	public final static String APC_API_PARAMETRO_STATO_SERVIZI = "statoServ";
	
	public final static String MESSAGE_METADATI_APC_API_EDIT = "{1} ({0})";
	public final static String MESSAGE_METADATI_APC_API_LIST = "API {0}";
	public final static String MESSAGE_METADATI_APC_API_CON_PROFILO = "API {0}, Profilo Interoperabilit&agrave;: {1}";
	public final static String MESSAGE_METADATI_APC_API_PROFILO = "Profilo Interoperabilit&agrave;: {0}";
	public final static String MESSAGE_METADATI_APC_API_LIST_CON_CANALE = "API {0}, Canale: {1}";
	public final static String MESSAGE_METADATI_APC_API_CON_CANALE_CON_PROFILO = "API {0}, Canale: {1}, Profilo Interoperabilit&agrave;: {2}";
	
	public final static String APC_API_ICONA_MODIFICA_API = "&#xE3C9;";
	public final static String APC_API_ICONA_MODIFICA_API_TOOLTIP = "Modifica";
	public final static String APC_API_ICONA_MODIFICA_API_TOOLTIP_CON_PARAMETRO = "Modifica {0}";
	
	public final static String APC_API_ICONA_VISUALIZZA = "&#xE89E;";
	public final static String APC_API_ICONA_VISUALIZZA_TOOLTIP = "Visualizza";
	public final static String APC_API_ICONA_VISUALIZZA_TOOLTIP_CON_PARAMETRO = "Visualizza {0}";
	
	public final static String APC_API_ICONA_DOWNLOAD_DOCUMENTO_INTERFACCIA = "&#xE884;";
	public final static String APC_API_ICONA_DOWNLOAD_DOCUMENTO_ARCHIVE = "&#xE149;";
	
	public final static String APC_API_ICONA_DOWNLOAD_DOCUMENTO_INTERFACCIA_TOOLTIP = "Download";
	public final static String APC_API_ICONA_DOWNLOAD_DOCUMENTO_INTERFACCIA_TOOLTIP_CON_PARAMETRO = "Download {0}";
	
	public final static String APC_API_ICONA_STATO_RISORSE_TUTTE_ABILITATE_TOOLTIP = "API correttamente configurata";
	public final static String APC_API_ICONA_STATO_RISORSE_TUTTE_DISABILITATE_TOOLTIP = "Nessuna risorsa configurata sull'API";
	
	public final static String APC_API_ICONA_STATO_SERVIZI_TUTTI_ABILITATI_TOOLTIP = "API correttamente configurata";
	public final static String APC_API_ICONA_STATO_SERVIZI_PARZIALMENTE_ABILITATI_TOOLTIP = "Per alcuni servizi dell'API non sono state configurate azioni";
	public final static String APC_API_ICONA_STATO_SERVIZI_TUTTI_DISABILITATI_TOOLTIP = "Nessun servizio configurato sull'API";
	public final static String APC_API_ICONA_STATO_SERVIZIO_PARZIALMENTE_CONFIGURATO_DISABILITATI_TOOLTIP = "Nessun'azione configurata sul servizio dell'API";
	
	public final static String APC_API_LABEL_APS_INFO_GENERALI = "Informazioni Generali";
	public final static String APC_API_LABEL_PARAMETRO_INTERFACCIA = "Interfaccia";
	public final static String APC_API_LABEL_GESTIONE_RISORSE = "Risorse";
	public final static String APC_API_LABEL_GESTIONE_SERVIZI = "Servizi";
	public final static String APC_API_LABEL_GESTIONE_ALLEGATI = "Allegati";
	public final static String APC_API_LABEL_GESTIONE_OPZIONI_AVANZATE = "Opzioni Avanzate";
	public final static String APC_API_LABEL_GESTIONE_GRUPPI = GruppiCostanti.LABEL_GRUPPI;
	public final static String APC_API_LABEL_NUOVA_VERSIONE_API = "Nuova Versione";
	
	public final static String PARAMETRO_APC_API_GESTIONE_GRUPPO = AccordiServizioParteComuneCostanti.PARAMETRO_APC_GRUPPO;
	public final static String PARAMETRO_APC_API_NUOVA_VERSIONE = AccordiServizioParteComuneCostanti.PARAMETRO_APC_API_NUOVA_VERSIONE;
	
	public final static String PARAMETRO_APC_API_GESTIONE_PARZIALE = Costanti.CONSOLE_PARAMETRO_APC_API_GESTIONE_PARZIALE;
	public final static String VALORE_PARAMETRO_APC_API_INFORMAZIONI_GENERALI = Costanti.CONSOLE_VALORE_PARAMETRO_APC_API_INFORMAZIONI_GENERALI;
	public final static String VALORE_PARAMETRO_APC_API_SOGGETTO_REFERENTE = "apiSoggettoReferente";
	public final static String VALORE_PARAMETRO_APC_API_PROFILO = "apiProfilo";
	public final static String VALORE_PARAMETRO_APC_API_DESCRIZIONE = "apiDescrizione";
	public final static String VALORE_PARAMETRO_APC_API_GRUPPI = "apiGruppi";
	public final static String VALORE_PARAMETRO_APC_API_CANALE = "apiCanale";
	public final static String VALORE_PARAMETRO_APC_API_GESTIONE_SPECIFICA_INTERFACCE = "apiGestioneSpecificaInterfacce";
	public final static String VALORE_PARAMETRO_APC_API_OPZIONI_AVANZATE = "apiOpzioniAvanzate";
	public final static String VALORE_PARAMETRO_APC_API_GESTIONE_PARZIALE_WSDL_CHANGE = "apiWsdlChange";
	
	public final static String APC_API_ICONA_GESTIONE_RISORSE = "&#xE896;";
	public final static String APC_API_ICONA_GESTIONE_SERVIZI = "&#xE896;";
	public final static String APC_API_ICONA_GESTIONE_ALLEGATI = "&#xE2BC;";
	public final static String APC_API_ICONA_NUOVA_VERSIONE_API = "&#xE02E;";
	public final static String APC_API_ICONA_GESTIONE_OPZIONI_AVANZATE = "&#xE8B8;";
	
	public final static String LABEL_IN_USO_BODY_HEADER_NESSUN_RISULTATO = "L'API non risulta utilizzata in alcuna configurazione";

}
