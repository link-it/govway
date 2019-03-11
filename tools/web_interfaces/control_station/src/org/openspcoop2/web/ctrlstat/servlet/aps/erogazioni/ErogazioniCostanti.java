/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.util.Vector;

import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;

/**
 * ErogazioniCostanti
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniCostanti extends AccordiServizioParteSpecificaCostanti {
	
	public final static String OBJECT_NAME_ASPS_EROGAZIONI = "aspsErogazioni";
	
	public final static String SERVLET_NAME_ASPS_EROGAZIONI_ADD = OBJECT_NAME_ASPS_EROGAZIONI+"Add.do";
	public final static String SERVLET_NAME_ASPS_EROGAZIONI_CHANGE = OBJECT_NAME_ASPS_EROGAZIONI+"Change.do";
	public final static String SERVLET_NAME_ASPS_EROGAZIONI_LIST = OBJECT_NAME_ASPS_EROGAZIONI+"List.do";
	public final static String SERVLET_NAME_ASPS_EROGAZIONI_DEL = OBJECT_NAME_ASPS_EROGAZIONI+"Del.do";
	
	public final static Vector<String> SERVLET_ASPS_EROGAZIONI = new Vector<String>();
	static{
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_ADD);
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_CHANGE);
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_LIST);
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_DEL);
	}
	
	public final static String LABEL_ASPS_EROGAZIONI = "Erogazioni";
	public final static String LABEL_ASPS_EROGAZIONE = "Erogazione";
	public final static String LABEL_ASPS_FRUIZIONI = "Fruizioni";
	public final static String LABEL_ASPS_FRUIZIONE = "Fruizione";
	public final static String LABEL_ASPS_RIEPILOGO = "Riepilogo";
	public final static String LABEL_ASPS_MODIFICA_SERVIZIO_NOME = "Nome";
	public final static String LABEL_ASPS_MODIFICA_SERVIZIO_INFO_GENERALI = AccordiServizioParteSpecificaCostanti.LABEL_APS_INFO_GENERALI;
	public final static String LABEL_ASPS_PORTE_DELEGATE_MODIFICA_DATI_INVOCAZIONE = PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_INVOCAZIONE;
	public final static String LABEL_ASPS_PORTE_DELEGATE_MODIFICA_CONNETTORE = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE;
	public final static String LABEL_ASPS_PORTE_APPLICATIVE_MODIFICA_DATI_INVOCAZIONE = PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_INVOCAZIONE;
	public final static String LABEL_ASPS_PORTE_APPLICATIVE_MODIFICA_CONNETTORE = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORE;
	public final static String LABEL_ASPS_GESTIONE_CONFIGURAZIONI = "Configurazione";
	public final static String LABEL_ASPS_GESTIONE_GRUPPI_CON_PARAMETRO = "Gruppi"; // il codice è già agganciato, riscommentare il seguente codice per avere risorse/azioni:  "Gruppi {0}";
	
	public final static String ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_ATTIVE = "nConfAttive";
	public final static String ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_DISPONIBILI = "nConfDisponibili";
	public final static String ASPS_EROGAZIONI_PARAMETRO_NUOVA_CONFIGURAZIONE = "nuovaConf";
	public final static String ASPS_EROGAZIONI_PARAMETRO_STATO_CONFIGURAZIONI = "statoConf";
	
	public final static String ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI = "vistaErogazioni";
	
	public final static String LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_SERVIZIO = "Servizio";
	public final static String LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_CONFIGURAZIONE = "Configurazione";
	public static final String LABEL_ASPS_ABILITA_CONFIGURAZIONE = "Abilita Configurazione";
	public final static String LABEL_ASPS_EROGAZIONI_PARAMETRO_NUOVA_CONFIGURAZIONE = "Configurazione";
	
	public final static String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_FRUIZIONI = "fruizioni";
	public final static String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_EROGAZIONI = "erogazioni";
	
	public final static String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_FORM_FRUIZIONE = "fruizione";
	public final static String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_FORM_EROGAZIONE = "erogazione";
	
	public final static String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_EDIT = "{1} ({0})";
	public final static String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_LIST = "API {0}: {1}";
	public final static String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_CON_PROFILO = "API {0}: {1}, Profilo Interoperabilit&agrave;: {2}";
	public final static String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_PROFILO = "Profilo Interoperabilit&agrave;: {0}";
	
	
	public final static String ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE = "&#xE3C9;";
	public final static String ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP = "Modifica";
	public final static String ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO = "Modifica {0}";
	
	public final static String ASPS_EROGAZIONI_ICONA_VISUALIZZA = "&#xE89E;";
	public final static String ASPS_EROGAZIONI_ICONA_VISUALIZZA_TOOLTIP = "Visualizza";
	public final static String ASPS_EROGAZIONI_ICONA_VISUALIZZA_TOOLTIP_CON_PARAMETRO = "Visualizza {0}";
	
	public final static String ASPS_EROGAZIONI_ICONA_VERIFICA_CONFIGURAZIONE = "&#xe8be;";
	public final static String ASPS_EROGAZIONI_ICONA_VERIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO = "Verifica {0}";
	
	public final static String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE = "check_green.png";
	public final static String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_PARZIALMENTE_ABILITATE = "check_yellow.png";
	public final static String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_DISABILITATE = "disabled_red.png";
	
	public final static String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE_TOOLTIP = "Abilitato";
	public final static String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_PARZIALMENTE_ABILITATE_TOOLTIP = "Alcuni gruppi sono disabilitati";
	public final static String ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_DISABILITATE_TOOLTIP = "Disabilitato";
	
	public final static String ASPS_EROGAZIONI_ICONA_GESTIONE_CONFIGURAZIONI = "&#xE8B8;";
	public final static String ASPS_EROGAZIONI_ICONA_GESTIONE_GRUPPI_CON_PARAMETRO = "&#xE02F;";
}

