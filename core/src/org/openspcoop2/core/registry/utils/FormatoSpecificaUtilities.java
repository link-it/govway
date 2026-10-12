/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.core.registry.utils;

import java.util.regex.Pattern;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;

/**
 * Gestione dei formati di specifica storici, non più previsti dall'enumeration {@link FormatoSpecifica}
 * ma ancora presenti nelle API registrate con versioni precedenti (es. 'wadl' registrato con la 3.3).
 *
 * Il valore storico viene conservato nel campo transient 'formatoSpecificaNonSupportato' dell'API,
 * mentre il formato specifica (enumeration) rimane non valorizzato.
 * Qualsiasi valore non previsto dall'enumeration viene gestito allo stesso modo: solo WADL ha una label dedicata,
 * per gli altri formati la label riporta il valore così come registrato nella base dati.
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FormatoSpecificaUtilities {

	private FormatoSpecificaUtilities() {}

	public static final String FORMATO_STORICO_WADL = "wadl";
	public static final String LABEL_FORMATO_STORICO_WADL = "WADL (deprecato, non supportato)";
	public static final String LABEL_FORMATO_NON_SUPPORTATO = "Formato non supportato";
	public static final String LABEL_FORMATO_NON_SUPPORTATO_SUFFIX = " (non supportato)";
	
	// dichiarazione di versione Swagger 2 in yaml o json (anche su singola riga): swagger: "2.0", swagger: 2.0, {"swagger":"2.0"
	private static final Pattern SWAGGER_2_PATTERN = Pattern.compile("(?m)(?:^|[{,]\\s*)[\"']?swagger[\"']?\\s*:\\s*[\"']?2");

	/**
	 * Indica se il valore letto dalla base dati corrisponde al formato storico WADL, l'unico con una label dedicata
	 */
	public static boolean isFormatoStoricoWadl(String value) {
		return value!=null && FORMATO_STORICO_WADL.equalsIgnoreCase(value.trim());
	}

	public static boolean isFormatoNonSupportato(AccordoServizioParteComune as) {
		return as!=null && as.getFormatoSpecifica()==null && as.getFormatoSpecificaNonSupportato()!=null;
	}
	public static boolean isFormatoNonSupportato(AccordoServizioParteComuneSintetico as) {
		return as!=null && as.getFormatoSpecifica()==null && as.getFormatoSpecificaNonSupportato()!=null;
	}

	/**
	 * Label del formato storico: dedicata per WADL, altrimenti il valore registrato seguito da ' (non supportato)' (es. 'raml (non supportato)')
	 */
	public static String getLabelFormatoNonSupportato(String formatoNonSupportato) {
		if(isFormatoStoricoWadl(formatoNonSupportato)) {
			return LABEL_FORMATO_STORICO_WADL;
		}
		if(formatoNonSupportato==null || formatoNonSupportato.trim().isEmpty()) {
			return LABEL_FORMATO_NON_SUPPORTATO;
		}
		return formatoNonSupportato.trim()+LABEL_FORMATO_NON_SUPPORTATO_SUFFIX;
	}
	public static String getLabelFormatoNonSupportato(AccordoServizioParteComune as) {
		return getLabelFormatoNonSupportato(as!=null ? as.getFormatoSpecificaNonSupportato() : null);
	}
	public static String getLabelFormatoNonSupportato(AccordoServizioParteComuneSintetico as) {
		return getLabelFormatoNonSupportato(as!=null ? as.getFormatoSpecificaNonSupportato() : null);
	}

	/**
	 * Valore del formato specifica da utilizzare nei messaggi di errore, nei log e nella colonna formato_specifica:
	 * il valore dell'enumeration se presente, altrimenti l'eventuale valore storico non supportato.
	 * In questo modo una modifica dell'API non sovrascrive il valore storico.
	 */
	public static String getValue(AccordoServizioParteComune as) {
		if(as==null) {
			return null;
		}
		if(as.getFormatoSpecifica()!=null) {
			return as.getFormatoSpecifica().getValue();
		}
		return as.getFormatoSpecificaNonSupportato();
	}

	/**
	 * Formato da assegnare ad un'API REST con formato storico non supportato quando ne viene sostituita l'interfaccia:
	 * Swagger 2 se il documento lo dichiara, altrimenti OpenAPI 3 (il documento viene poi validato rispetto al formato scelto)
	 */
	public static FormatoSpecifica getFormatoSpecificaRestFromDocument(String document) {
		if(document!=null && SWAGGER_2_PATTERN.matcher(document).find()) {
			return FormatoSpecifica.SWAGGER_2;
		}
		return FormatoSpecifica.OPEN_API_3;
	}

}
