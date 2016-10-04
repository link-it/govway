/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.util.Vector;

import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * AccordiCooperazioneCostanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiCooperazioneCostanti {

	/* OBJECT NAME */
	
	public final static String OBJECT_NAME_ACCORDI_COOPERAZIONE = "accordiCooperazione";
	
	public final static String OBJECT_NAME_AC_ALLEGATI = "accordiCooperazioneAllegati";
	public final static ForwardParams TIPO_OPERAZIONE_VIEW = ForwardParams.OTHER("View");
	
	public final static String OBJECT_NAME_AC_PARTECIPANTI = "accordiCooperazionePartecipanti";
	
	
	/* SERVLET NAME */
	
	public final static String SERVLET_NAME_ACCORDI_COOPERAZIONE_ADD = OBJECT_NAME_ACCORDI_COOPERAZIONE+"Add.do";
	public final static String SERVLET_NAME_ACCORDI_COOPERAZIONE_CHANGE = OBJECT_NAME_ACCORDI_COOPERAZIONE+"Change.do";
	public final static String SERVLET_NAME_ACCORDI_COOPERAZIONE_DELETE = OBJECT_NAME_ACCORDI_COOPERAZIONE+"Del.do";
	public final static String SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST = OBJECT_NAME_ACCORDI_COOPERAZIONE+"List.do";
	public final static Vector<String> SERVLET_ACCORDI_COOPERAZIONE = new Vector<String>();
	static{
		SERVLET_ACCORDI_COOPERAZIONE.add(SERVLET_NAME_ACCORDI_COOPERAZIONE_ADD);
		SERVLET_ACCORDI_COOPERAZIONE.add(SERVLET_NAME_ACCORDI_COOPERAZIONE_CHANGE);
		SERVLET_ACCORDI_COOPERAZIONE.add(SERVLET_NAME_ACCORDI_COOPERAZIONE_DELETE);
		SERVLET_ACCORDI_COOPERAZIONE.add(SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST);
	}
	
	public final static String SERVLET_NAME_AC_ALLEGATI_ADD = OBJECT_NAME_AC_ALLEGATI+"Add.do";
	public final static String SERVLET_NAME_AC_ALLEGATI_CHANGE = OBJECT_NAME_AC_ALLEGATI+"Change.do";
	public final static String SERVLET_NAME_AC_ALLEGATI_DELETE = OBJECT_NAME_AC_ALLEGATI+"Del.do";
	public final static String SERVLET_NAME_AC_ALLEGATI_LIST = OBJECT_NAME_AC_ALLEGATI+"List.do";
	public final static String SERVLET_NAME_AC_ALLEGATI_VIEW = OBJECT_NAME_AC_ALLEGATI+"View.do";
	public final static Vector<String> SERVLET_AC_ALLEGATI = new Vector<String>();
	static{
		SERVLET_AC_ALLEGATI.add(SERVLET_NAME_AC_ALLEGATI_ADD);
		SERVLET_AC_ALLEGATI.add(SERVLET_NAME_AC_ALLEGATI_CHANGE);
		SERVLET_AC_ALLEGATI.add(SERVLET_NAME_AC_ALLEGATI_DELETE);
		SERVLET_AC_ALLEGATI.add(SERVLET_NAME_AC_ALLEGATI_LIST);
		SERVLET_AC_ALLEGATI.add(SERVLET_NAME_AC_ALLEGATI_VIEW);
	}
	
	public final static String SERVLET_NAME_AC_PARTECIPANTI_ADD = OBJECT_NAME_AC_PARTECIPANTI+"Add.do";
	public final static String SERVLET_NAME_AC_PARTECIPANTI_DELETE = OBJECT_NAME_AC_PARTECIPANTI+"Del.do";
	public final static String SERVLET_NAME_AC_PARTECIPANTI_LIST = OBJECT_NAME_AC_PARTECIPANTI+"List.do";
	public final static Vector<String> SERVLET_AC_PARTECIPANTI = new Vector<String>();
	static{
		SERVLET_AC_PARTECIPANTI.add(SERVLET_NAME_AC_PARTECIPANTI_ADD);
		SERVLET_AC_PARTECIPANTI.add(SERVLET_NAME_AC_PARTECIPANTI_DELETE);
		SERVLET_AC_PARTECIPANTI.add(SERVLET_NAME_AC_PARTECIPANTI_LIST);
	}
	

	
	
	
	
	/* LABEL GENERALI */
	
	public final static String LABEL_ACCORDI_COOPERAZIONE = "Accordi Cooperazione";
	public final static String LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI_DI = "Partecipanti di ";
	public final static String LABEL_ACCORDI_COOPERAZIONE_ALLEGATI_DI = "Allegati di ";
	public final static String LABEL_ACCORDI_COOPERAZIONE_NOME_ACCORDO_NECESSARIO = "Il nome dell'accordo &egrave; necessario!";
	public final static String LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTE  = "Partecipante";
	public final static String LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI  = "Partecipanti";
	public final static String LABEL_ACCORDI_COOPERAZIONE_ALLEGATI = "Allegati";
	public final static String LABEL_ACCORDI_COOPERAZIONE_ESPORTA_SELEZIONATI = "Esporta Selezionati";
	public final static String LABEL_ACCORDI_COOPERAZIONE_ESPORTA_SELEZIONATI_CLICK_EVENT ="Esporta('"+ArchiveType.ACCORDO_COOPERAZIONE.name()+"')";
	public final static String LABEL_ACCORDI_COOPERAZIONE_SOGGETTO_REFERENTE_12 = "referenteLabelAccordo1.2";
	public final static String LABEL_ACCORDI_COOPERAZIONE_SOGGETTI_PARTECIPANTI = "Soggetti Partecipanti";
	public final static String LABEL_ACCORDI_COOPERAZIONE_DOWNLOAD = "Download";
	
	public final static String LABEL_AC_MENU_VISUALE_AGGREGATA = "Accordi Cooperazione";
	
	/* PARAMETRI */
	
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_ID = "id";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO = "idAccordo";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO = "idAllegato";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_NOME = "nome";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE = "descr";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE = "referente";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE = "versione";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO = "privato";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_STATO = "stato";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA = "tipoSICA";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_PARTECIPANTE = "partecipante";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO = "nomeDoc";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_FILE = "tipoFile";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO = "ruolo";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_VERSION = "version";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO_LABEL = "privatoLabel";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO = "documento";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_THE_FILE = "theFile";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_DOCUMENTO = "tipoDocumento";
	public final static String PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO = "tipoProtocollo";
	
	
	
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME = "Nome";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE = "Referente";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE = "Versione";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO = "Privato";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_STATO = "Stato";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_SOGGETTO_REFERENTE = "Soggetto Referente";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO = "Visibilit&agrave; accordo";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO = "Ruolo";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO = "Documento";
	public final static String LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO = "Protocollo";
	
	
	/* DEFAULT VALUE PARAMETRI */

	public final static String DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VERSION = "required";
	public final static String DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO_PUBBLICA = "pubblica";
	public final static String DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO_PRIVATA = "privata";
}
