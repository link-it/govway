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
package org.openspcoop2.web.ctrlstat.servlet.ac;

import java.util.List;
import java.util.ArrayList;

import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * AccordiCooperazioneCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiCooperazioneCostanti {
	
	private AccordiCooperazioneCostanti() {}

	/* OBJECT NAME */
	
	public static final String OBJECT_NAME_ACCORDI_COOPERAZIONE = "accordiCooperazione";
	
	public static final String OBJECT_NAME_AC_ALLEGATI = "accordiCooperazioneAllegati";
	public static final ForwardParams TIPO_OPERAZIONE_VIEW = ForwardParams.OTHER("View");
	
	public static final String OBJECT_NAME_AC_PARTECIPANTI = "accordiCooperazionePartecipanti";
	
	
	/* SERVLET NAME */
	
	public static final String SERVLET_NAME_ACCORDI_COOPERAZIONE_ADD = OBJECT_NAME_ACCORDI_COOPERAZIONE+Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_ACCORDI_COOPERAZIONE_CHANGE = OBJECT_NAME_ACCORDI_COOPERAZIONE+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_ACCORDI_COOPERAZIONE_DELETE = OBJECT_NAME_ACCORDI_COOPERAZIONE+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST = OBJECT_NAME_ACCORDI_COOPERAZIONE+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_ACCORDI_COOPERAZIONE = new ArrayList<>();
	public static List<String> getServletAccordiCooperazione() {
		return SERVLET_ACCORDI_COOPERAZIONE;
	}
	static{
		SERVLET_ACCORDI_COOPERAZIONE.add(SERVLET_NAME_ACCORDI_COOPERAZIONE_ADD);
		SERVLET_ACCORDI_COOPERAZIONE.add(SERVLET_NAME_ACCORDI_COOPERAZIONE_CHANGE);
		SERVLET_ACCORDI_COOPERAZIONE.add(SERVLET_NAME_ACCORDI_COOPERAZIONE_DELETE);
		SERVLET_ACCORDI_COOPERAZIONE.add(SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST);
	}
	
	public static final String SERVLET_NAME_AC_ALLEGATI_ADD = OBJECT_NAME_AC_ALLEGATI+Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_AC_ALLEGATI_CHANGE = OBJECT_NAME_AC_ALLEGATI+Costanti.STRUTS_ACTION_SUFFIX_CHANGE;
	public static final String SERVLET_NAME_AC_ALLEGATI_DELETE = OBJECT_NAME_AC_ALLEGATI+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_AC_ALLEGATI_LIST = OBJECT_NAME_AC_ALLEGATI+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	public static final String SERVLET_NAME_AC_ALLEGATI_VIEW = OBJECT_NAME_AC_ALLEGATI+"View.do";
	private static final List<String> SERVLET_AC_ALLEGATI = new ArrayList<>();
	public static List<String> getServletAcAllegati() {
		return SERVLET_AC_ALLEGATI;
	}
	static{
		SERVLET_AC_ALLEGATI.add(SERVLET_NAME_AC_ALLEGATI_ADD);
		SERVLET_AC_ALLEGATI.add(SERVLET_NAME_AC_ALLEGATI_CHANGE);
		SERVLET_AC_ALLEGATI.add(SERVLET_NAME_AC_ALLEGATI_DELETE);
		SERVLET_AC_ALLEGATI.add(SERVLET_NAME_AC_ALLEGATI_LIST);
		SERVLET_AC_ALLEGATI.add(SERVLET_NAME_AC_ALLEGATI_VIEW);
	}
	
	public static final String SERVLET_NAME_AC_PARTECIPANTI_ADD = OBJECT_NAME_AC_PARTECIPANTI+Costanti.STRUTS_ACTION_SUFFIX_ADD;
	public static final String SERVLET_NAME_AC_PARTECIPANTI_DELETE = OBJECT_NAME_AC_PARTECIPANTI+Costanti.STRUTS_ACTION_SUFFIX_DELETE;
	public static final String SERVLET_NAME_AC_PARTECIPANTI_LIST = OBJECT_NAME_AC_PARTECIPANTI+Costanti.STRUTS_ACTION_SUFFIX_LIST;
	private static final List<String> SERVLET_AC_PARTECIPANTI = new ArrayList<>();
	public static List<String> getServletAcPartecipanti() {
		return SERVLET_AC_PARTECIPANTI;
	}
	static{
		SERVLET_AC_PARTECIPANTI.add(SERVLET_NAME_AC_PARTECIPANTI_ADD);
		SERVLET_AC_PARTECIPANTI.add(SERVLET_NAME_AC_PARTECIPANTI_DELETE);
		SERVLET_AC_PARTECIPANTI.add(SERVLET_NAME_AC_PARTECIPANTI_LIST);
	}
	

	
	
	
	
	/* LABEL GENERALI */
	
	public static final String LABEL_ACCORDI_COOPERAZIONE = "Accordi Cooperazione";
	public static final String LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI_DI = "Partecipanti di ";
	public static final String LABEL_ACCORDI_COOPERAZIONE_ALLEGATI_DI = "Allegati di ";
	public static final String LABEL_ACCORDI_COOPERAZIONE_NOME_ACCORDO_NECESSARIO = "Il nome dell'accordo &egrave; necessario!";
	public static final String LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTE  = "Partecipante";
	public static final String LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI  = "Partecipanti";
	public static final String LABEL_ACCORDI_COOPERAZIONE_ALLEGATI = "Allegati";
	public static final String LABEL_ACCORDI_COOPERAZIONE_ALLEGATO = "Allegato";
	public static final String LABEL_ACCORDI_COOPERAZIONE_ESPORTA_SELEZIONATI = "Esporta";
	public static final String LABEL_ACCORDI_COOPERAZIONE_ESPORTA_SELEZIONATI_CLICK_EVENT ="Esporta('"+ArchiveType.ACCORDO_COOPERAZIONE.name()+"')";
	public static final String LABEL_ACCORDI_COOPERAZIONE_SOGGETTO_REFERENTE_12 = "referenteLabelAccordo1.2";
	public static final String LABEL_ACCORDI_COOPERAZIONE_SOGGETTI_PARTECIPANTI = "Soggetti Partecipanti";
	public static final String LABEL_ACCORDI_COOPERAZIONE_DOWNLOAD = "Download";
	
	public static final String LABEL_AC_MENU_VISUALE_AGGREGATA = "Accordi Cooperazione";
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_ID = "id";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO = "idAccordo";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO = "idAllegato";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_NOME = "nome";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE = "descr";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE = "referente";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE = "versione";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO = "privato";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_STATO = "stato";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA = "tipoSICA";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_PARTECIPANTE = "partecipante";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO = "nomeDoc";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_FILE = "tipoFile";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO = "ruolo";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO_LABEL = "privatoLabel";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO = "documento";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_THE_FILE = "theFile";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_DOCUMENTO = "tipoDocumento";
	public static final String PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO = "tipoProtocollo";
	
	
	
	
	/* LABEL PARAMETRI */
	
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME = "Nome";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE = "Descrizione";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE = "Referente";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE = "Versione";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO = "Privato";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_STATO = "Stato";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_SOGGETTO_REFERENTE = "Soggetto Referente";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO = "Visibilit&agrave; accordo";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO = "Ruolo";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_TIPO = "Tipo";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PARTECIPANTE = "Soggetto";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO = "Documento";
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO;
	public static final String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO_COMPACT = CostantiControlStation.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	
	
	/* DEFAULT VALUE PARAMETRI */

	public static final String DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO_PUBBLICA = "pubblica";
	public static final String DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO_PRIVATA = "privata";
}
