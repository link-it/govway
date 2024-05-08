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

package org.openspcoop2.web.ctrlstat.servlet.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * UtilsCostanti
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class UtilsCostanti {
	
	private UtilsCostanti() {}

	/* SERVLET NAME  */
	
	public static final String SERVLET_NAME_INFORMAZIONI_UTILIZZO_OGGETTO = "informazioniUtilizzoOggettoRegistro";
	
	public static final String SERVLET_NAME_PROPRIETA_OGGETTO = "proprietaOggettoRegistro";
	
	public static final String SERVLET_NAME_SECRET_DECODER = "secretDecoder";
	
	public static final String SERVLET_NAME_VERIFICA_CERTIFICATI = "verificaCertificati";
	
	public static final String SERVLET_NAME_CACHE_MANAGER = "cacheManager";
	
	private static final List<String> SERVLET_UTILS = new ArrayList<>();
	public static List<String> getServletUtils() {
		return SERVLET_UTILS;
	}
	static{
		SERVLET_UTILS.add(SERVLET_NAME_INFORMAZIONI_UTILIZZO_OGGETTO);
		SERVLET_UTILS.add(SERVLET_NAME_PROPRIETA_OGGETTO);
		SERVLET_UTILS.add(SERVLET_NAME_SECRET_DECODER);
		SERVLET_UTILS.add(SERVLET_NAME_VERIFICA_CERTIFICATI);
		SERVLET_UTILS.add(SERVLET_NAME_CACHE_MANAGER);
	}
	
	/* PARAMETRI */
	
	public static final String PARAMETRO_RESET_CACHE_TIPO_OGGETTO = Costanti.PARAMETRO_RESET_CACHE_TIPO_OGGETTO;
	
	public static final String PARAMETRO_VERIFICA_CERTIFICATI_TIPO_OGGETTO = Costanti.PARAMETRO_VERIFICA_CERTIFICATI_TIPO_OGGETTO;
	
	public static final String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_URL = Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_URL;
	public static final String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_ID_OGGETTO = Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_ID_OGGETTO;
	public static final String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO = Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO;
	public static final String PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA = Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA;
	
	public static final String VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_TEXT= Costanti.VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_TEXT;
	public static final String VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_JSON = Costanti.VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_JSON;
	
	
	
	public static final String PARAMETRO_PROPRIETA_OGGETTO_URL = Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_URL;
	public static final String PARAMETRO_PROPRIETA_OGGETTO_ID_OGGETTO = Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_ID_OGGETTO;
	public static final String PARAMETRO_PROPRIETA_OGGETTO_TIPO_OGGETTO = Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO;
	public static final String PARAMETRO_PROPRIETA_OGGETTO_TIPO_RISPOSTA = Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA;
	
	public static final String VALUE_PARAMETRO_PROPRIETA_OGGETTO_TIPO_RISPOSTA_TEXT= Costanti.VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_TEXT;
	public static final String VALUE_PARAMETRO_PROPRIETA_OGGETTO_TIPO_RISPOSTA_JSON = Costanti.VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_JSON;
	
	
	
	public static final String KEY_JSON_RISPOSTA_USO = "uso";
	
	
	public static final String PARAMETRO_SECRET_TO_UNWRAP = "secret";
	public static final String MESSAGGIO_ERRORE_UNWRAP = "Si &egrave; verificato un errore durante la decodifica. Si prega di riprovare pi&ugrave; tardi.";
	public static final String MESSAGGIO_ERRORE_ELIMINAZIONE_ELEMENTO_CACHE = "Si &egrave; verificato un errore durante la rimozione dell'elemento dalla cache. Si prega di riprovare pi&ugrave; tardi.";
	public static final String MESSAGGIO_ERRORE_VERIFICA_CERTIFICATI = "Si &egrave; verificato un errore durante la verifica dei certificati. Si prega di riprovare pi&ugrave; tardi.";
	public static final String MESSAGGIO_INFORMATIVO_VERIFICA_CERTIFICATI_NON_ESEGUITA = "Verifica dei certificati non eseguita";
	
	public static final String KEY_ESITO = Costanti.KEY_ESITO_JSON;
	public static final String KEY_DETTAGLIO_ESITO = Costanti.KEY_DETTAGLIO_ESITO_JSON;
}

