/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

/**
 * ErogazioniCostanti
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author: apoli $
 * @version $Rev: 14207 $, $Date: 2018-06-25 10:55:15 +0200 (Mon, 25 Jun 2018) $
 * 
 */
public class ErogazioniCostanti extends AccordiServizioParteSpecificaCostanti {
	
	public final static String OBJECT_NAME_ASPS_EROGAZIONI = "aspsErogazioni";
	
	public final static String SERVLET_NAME_ASPS_EROGAZIONI_CHANGE = OBJECT_NAME_ASPS_EROGAZIONI+"Change.do";
	public final static String SERVLET_NAME_ASPS_EROGAZIONI_LIST = OBJECT_NAME_ASPS_EROGAZIONI+"List.do";
	
	public final static Vector<String> SERVLET_ASPS_EROGAZIONI = new Vector<String>();
	static{
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_CHANGE);
		SERVLET_ASPS_EROGAZIONI.add(SERVLET_NAME_ASPS_EROGAZIONI_LIST);
	}
	
	public final static String LABEL_ASPS_EROGAZIONI = "Erogazioni";
	public final static String LABEL_ASPS_EROGAZIONE = "Erogazione";
	public final static String LABEL_ASPS_FRUIZIONI = "Fruizioni";
	public final static String LABEL_ASPS_FRUIZIONE = "Fruizione";
	public final static String LABEL_ASPS_RIEPILOGO = "Riepilogo";
	
	public final static String ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_ATTIVE = "nConfAttive";
	public final static String ASPS_EROGAZIONI_PARAMETRO_NUMERO_CONFIGURAZIONI_DISPONIBILI = "nConfDisponibili";
	
	public final static String LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_SERVIZIO = "Servizio";
	public final static String LABEL_ASPS_LISTA_EROGAZIONI_COLONNA_CONFIGURAZIONE = "Configurazione";
	public static final String LABEL_ASPS_ABILITA_CONFIGURAZIONE = "Abilita Configurazione";
	
	public final static String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_FRUIZIONI = "fruizioni";
	public final static String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_LISTA_EROGAZIONI = "erogazioni";
	
	public final static String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_FORM_FRUIZIONE = "fruizione";
	public final static String ASPS_EROGAZIONI_NOME_VISTA_CUSTOM_FORM_EROGAZIONE = "erogazione";
	
	public final static String MESSAGE_METADATI_SERVIZIO_EROGAZIONI = "API {0}: {1}";
	public final static String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_CON_PROFILO = "API {0}: {1}, Profilo Interoperabilit&agrave;: {2}";
	public final static String MESSAGE_METADATI_SERVIZIO_EROGAZIONI_PROFILO = "Profilo Interoperabilit&agrave;: {0}";
	
	
	public final static String ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE = "&#xE3C9;";
	public final static String ASPS_EROGAZIONI_ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP = "Modifica";
}

