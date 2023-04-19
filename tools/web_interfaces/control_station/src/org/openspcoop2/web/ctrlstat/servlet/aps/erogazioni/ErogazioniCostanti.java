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
package org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni;

import java.util.List;
import java.util.ArrayList;

import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * ErogazioniCostanti
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniCostanti {
	
	private ErogazioniCostanti() {}
	
	public static final String OBJECT_NAME_ASPS_EROGAZIONI = "aspsErogazioni";
	public static final String OBJECT_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI = "aspsErogazioniVerificaCertificati";
	
	public static final String SERVLET_NAME_ASPS_EROGAZIONI_ADD = OBJECT_NAME_ASPS_EROGAZIONI+Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_ASPS_EROGAZIONI_CHANGE = OBJECT_NAME_ASPS_EROGAZIONI+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_ASPS_EROGAZIONI_LIST = OBJECT_NAME_ASPS_EROGAZIONI+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final String SERVLET_NAME_ASPS_EROGAZIONI_DEL = OBJECT_NAME_ASPS_EROGAZIONI+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	
	private static final List<String> SERVLET_ASPS_EROGAZIONI = new ArrayList<>();
	public static List<String> getServletAspsErogazioni() {
		return SERVLET_ASPS_EROGAZIONI;
	}
	static{
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_ADD);
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_CHANGE);
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_LIST);
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_DEL);
	}
	
	public static final String SERVLET_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI = OBJECT_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI+".do";
	
	public static final String LABEL_ASPS_EROGAZIONI = "Erogazioni";
	public static final String LABEL_ASPS_EROGAZIONE = "Erogazione";
	public static final String LABEL_ASPS_FRUIZIONI = "Fruizioni";
	public static final String LABEL_ASPS_FRUIZIONE = "Fruizione";
	public static final String LABEL_ASPS_RIEPILOGO = "Riepilogo";
	public static final String LABEL_ASPS_MODIFICA_SERVIZIO_NOME = "Nome";
	public static final String LABEL_ASPS_MODIFICA_SERVIZIO_INFO_GENERALI = AccordiServizioParteSpecificaCostanti.LABEL_APS_INFO_GENERALI;
	public static final String LABEL_ASPS_PORTE_DELEGATE_MODIFICA_DATI_INVOCAZIONE = PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_INVOCAZIONE;
	public static final String LABEL_ASPS_PORTE_DELEGATE_MODIFICA_CONNETTORE = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE;
	public static final String LABEL_ASPS_PORTE_APPLICATIVE_MODIFICA_DATI_INVOCAZIONE = PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_INVOCAZIONE;
	public static final String LABEL_ASPS_PORTE_APPLICATIVE_MODIFICA_CONNETTORE = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE;
	public static final String LABEL_ASPS_GESTIONE_CONFIGURAZIONI = "Configurazione";
	public static final String LABEL_ASPS_GESTIONE_CONFIGURAZIONI_CONFIGURA = "Configura";
	public static final String LABEL_ASPS_GESTIONE_GRUPPI_CON_PARAMETRO = "Gruppi"; /** il codice è già agganciato, riscommentare il seguente codice per avere risorse/azioni:  "Gruppi {0}"; */
	public static final String LABEL_ASPS_VERIFICA_CERTIFICATI = CostantiControlStation.LABEL_VERIFICA_CERTIFICATI;
	public static final String LABEL_ASPS_VERIFICA_CERTIFICATI_DI = CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_DI;
	
	public static final String ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_ATTIVE = "nConfAttive";
	public static final String ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_DISPONIBILI = "nConfDisponibili";
	public static final String ASPS_EROGAZIONI_PARAMETRO_NUOVA_CONFIGURAZIONE = "nuovaConf";
	public static final String ASPS_EROGAZIONI_PARAMETRO_STATO_CONFIGURAZIONI = "statoConf";
	
	public static final String ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI = Costanti.SESSION_ATTRIBUTE_TAB_KEY_PREFIX + "vistaErogazioni";
	
	public static final String LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_SERVIZIO = "Servizio";
	public static final String LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_CONFIGURAZIONE = "Configurazione";
	public static final String LABEL_ASPS_ABILITA_CONFIGURAZIONE = "Abilita Configurazione";
	public static final String LABEL_ASPS_EROGAZIONI_PARAMETRO_NUOVA_CONFIGURAZIONE = "Configurazione";
	
	public static final String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_FRUIZIONI = "fruizioni";
	public static final String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_EROGAZIONI = "erogazioni";
	
	public static final String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_FORM_FRUIZIONE = "fruizione";
	public static final String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_FORM_EROGAZIONE = "erogazione";
	
	public static final String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_CONFIGURAZIONE = "configurazione";
	
	public static final String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_EDIT = "{1} ({0})";
	public static final String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_LIST = "API {0}: {1}";
	public static final String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_CON_PROFILO = "API {0}: {1}, Profilo Interoperabilit&agrave;: {2}";
	public static final String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_PROFILO = "Profilo Interoperabilit&agrave;: {0}";
	public static final String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_LIST_CON_CANALE = "API {0}: {1}, Canale: {2}";
	public static final String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_CON_PROFILO_CON_CANALE = "API {0}: {1}, Canale: {2}, Profilo Interoperabilit&agrave;: {3}";
	
	public static final String ASPS_EROGAZIONI_ICONA_CAMBIA_API_TOOLTIP_CON_PARAMETRO = "Cambia {0}";
		
	public static final String ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE = CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE;
	public static final String ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP = CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP;
	public static final String ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO = CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO;
	
	public static final String ASPS_EROGAZIONI_ICONA_VISUALIZZA = CostantiControlStation.ICONA_VISUALIZZA;
	public static final String ASPS_EROGAZIONI_ICONA_VISUALIZZA_TOOLTIP = CostantiControlStation.ICONA_VISUALIZZA_TOOLTIP;
	public static final String ASPS_EROGAZIONI_ICONA_VISUALIZZA_TOOLTIP_CON_PARAMETRO = CostantiControlStation.ICONA_VISUALIZZA_TOOLTIP_CON_PARAMETRO;
	
	public static final String ASPS_EROGAZIONI_ICONA_VERIFICA_CONFIGURAZIONE = CostantiControlStation.ICONA_VERIFICA;
	public static final String ASPS_EROGAZIONI_ICONA_VERIFICA_CONFIGURAZIONE_TOOLTIP = CostantiControlStation.ICONA_VERIFICA_TOOLTIP;
	public static final String ASPS_EROGAZIONI_ICONA_VERIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO = CostantiControlStation.ICONA_VERIFICA_TOOLTIP_CON_PARAMETRO;
	
	public static final String ASPS_EROGAZIONI_ICONA_VERIFICA_CERTIFICATI = CostantiControlStation.ICONA_VERIFICA_CERTIFICATI;
	public static final String ASPS_EROGAZIONI_ICONA_VERIFICA_CERTIFICATI_TOOLTIP = CostantiControlStation.ICONA_VERIFICA_CERTIFICATI_TOOLTIP;
	public static final String ASPS_EROGAZIONI_ICONA_VERIFICA_CERTIFICATI_TOOLTIP_CON_PARAMETRO = CostantiControlStation.ICONA_VERIFICA_CERTIFICATI_TOOLTIP_CON_PARAMETRO;
	
	public static final String ASPS_EROGAZIONI_ICONA_CONFIGURAZIONE_CONNETTORI_MULTIPLI = CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_CONNETTORI_MULTIPLI;
	public static final String ASPS_EROGAZIONI_ICONA_CONFIGURAZIONE_CONNETTORI_MULTIPLI_TOOLTIP = CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_CONNETTORI_MULTIPLI_TOOLTIP;
	public static final String ASPS_EROGAZIONI_ICONA_CONFIGURAZIONE_CONNETTORI_MULTIPLI_TOOLTIP_CON_PARAMETRO = CostantiControlStation.ICONA_MODIFICA_CONFIGURAZIONE_CONNETTORI_MULTIPLI_TOOLTIP_CON_PARAMETRO;
	
	public static final String ASPS_EROGAZIONI_ICONA_ELENCO_CONNETTORI_MULTIPLI = CostantiControlStation.ICONA_ELENCO_CONNETTORI_MULTIPLI;
	public static final String ASPS_EROGAZIONI_ICONA_ELENCO_CONNETTORI_MULTIPLI_TOOLTIP = CostantiControlStation.ICONA_ELENCO_CONNETTORI_MULTIPLI_TOOLTIP;
	public static final String ASPS_EROGAZIONI_ICONA_ELENCO_CONNETTORI_MULTIPLI_TOOLTIP_CON_PARAMETRO = CostantiControlStation.ICONA_ELENCO_CONNETTORI_MULTIPLI_TOOLTIP_CON_PARAMETRO;
	
	public static final String ASPS_EROGAZIONI_ICONA_UPGRADE_CONFIGURAZIONE = CostantiControlStation.ICONA_UPGRADE_CONFIGURAZIONE;
	public static final String ASPS_EROGAZIONI_ICONA_UPGRADE_CONFIGURAZIONE_TOOLTIP = CostantiControlStation.ICONA_UPGRADE_CONFIGURAZIONE_TOOLTIP;
	public static final String ASPS_EROGAZIONI_ICONA_UPGRADE_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO = CostantiControlStation.ICONA_UPGRADE_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO;
		
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE_TOOLTIP = "API attiva";
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_PARZIALMENTE_ABILITATE_TOOLTIP = "Alcuni gruppi dell'API sono disabilitati";
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONNETTORI_MULTIPLI_SCHEDULING_DISABILITATO_TOOLTIP= "Rilevati connettori multipli dell'API in cui è stato disabilitato lo scheduling della consegna";
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_DISABILITATE_TOOLTIP = "API disabilitata";
	
	private static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_XXX_NO_FRUITORI = "Rilevato 'Controllo degli Accessi', con autorizzazione "+CostantiControlStation.MESSAGGIO_ERRORE_XXX+" per richiedente, senza alcun fruitore registrato";
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TRASPORTO_NO_FRUITORI = ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_XXX_NO_FRUITORI.replaceAll(CostantiControlStation.MESSAGGIO_ERRORE_XXX, CostantiControlStation.MESSAGGIO_ERRORE_TRASPORTO);
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_CANALE_NO_FRUITORI = ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_XXX_NO_FRUITORI.replaceAll(CostantiControlStation.MESSAGGIO_ERRORE_XXX, CostantiControlStation.MESSAGGIO_ERRORE_CANALE);
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TOKEN_NO_FRUITORI = ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_XXX_NO_FRUITORI.replaceAll(CostantiControlStation.MESSAGGIO_ERRORE_XXX, CostantiControlStation.MESSAGGIO_ERRORE_TOKEN);
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_MESSAGGIO_NO_FRUITORI = ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_XXX_NO_FRUITORI.replaceAll(CostantiControlStation.MESSAGGIO_ERRORE_XXX, CostantiControlStation.MESSAGGIO_ERRORE_MESSAGGIO);
	
	private static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_XXX_NO_RUOLI = "Rilevato 'Controllo degli Accessi', con autorizzazione "+CostantiControlStation.MESSAGGIO_ERRORE_XXX+" per ruoli, senza alcun ruolo registrato";
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TRASPORTO_NO_RUOLI = ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_XXX_NO_RUOLI.replaceAll(CostantiControlStation.MESSAGGIO_ERRORE_XXX, CostantiControlStation.MESSAGGIO_ERRORE_TRASPORTO);
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_CANALE_NO_RUOLI = ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_XXX_NO_RUOLI.replaceAll(CostantiControlStation.MESSAGGIO_ERRORE_XXX, CostantiControlStation.MESSAGGIO_ERRORE_CANALE);
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TOKEN_NO_RUOLI = ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_XXX_NO_RUOLI.replaceAll(CostantiControlStation.MESSAGGIO_ERRORE_XXX, CostantiControlStation.MESSAGGIO_ERRORE_TOKEN);
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_MESSAGGIO_NO_RUOLI = ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_XXX_NO_RUOLI.replaceAll(CostantiControlStation.MESSAGGIO_ERRORE_XXX, CostantiControlStation.MESSAGGIO_ERRORE_MESSAGGIO);
	
	public static final String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_CONTROLLO_ACCESSO_AUTORIZZAZIONE_TOKEN_NO_SCOPE = "Rilevato 'Controllo degli Accessi', con autorizzazione token per scope, senza alcun scope registrato";
	
	public static final String ASPS_EROGAZIONI_ICONA_GESTIONE_CONFIGURAZIONI = "&#xE8B8;";
	public static final String ASPS_EROGAZIONI_ICONA_GESTIONE_GRUPPI_CON_PARAMETRO = "&#xE02F;";
}

