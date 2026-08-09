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

package org.openspcoop2.web.lib.mvc.security;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * CssSanitizer
 *
 * Filtra il contenuto degli attributi 'style' presenti in un documento HTML, mantenendo
 * unicamente le proprietà CSS esplicitamente consentite dalla configurazione.
 *
 * La libreria jsoup, utilizzata per la sanificazione dell'HTML, non effettua alcun parsing
 * del CSS: le proprietà indicate all'interno di un attributo 'style' consentito da una
 * Safelist transitano quindi inalterate. Questa classe integra tale limite applicando,
 * dopo la sanificazione dell'HTML, un controllo sul nome e sul valore di ogni dichiarazione.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CssSanitizer {

	private CssSanitizer() {}

	private static final String ATTRIBUTE_STYLE = "style";

	/** Nome della proprietà CSS: solo caratteri alfanumerici e '-' */
	private static final Pattern CSS_PROPERTY_NAME = Pattern.compile("^[a-z][a-z0-9-]*$");

	/** Caratteri ammessi nel valore della proprietà CSS (colori, unità di misura, percentuali, funzioni tipo 'rgb(...)') */
	private static final Pattern CSS_VALUE_ALLOWED = Pattern.compile("^[a-zA-Z0-9#%.,()+/'\" -]+$");

	/** Costrutti non ammessi nel valore: consentono il caricamento di risorse esterne o l'esecuzione di codice */
	private static final Pattern CSS_VALUE_DENIED = Pattern.compile("(?i)url\\s*\\(|expression\\s*\\(|javascript:|@import|/\\*|\\\\");

	/** Suffisso '!important', rimosso prima della validazione del valore */
	private static final Pattern CSS_IMPORTANT = Pattern.compile("(?i)\\s*!\\s*important\\s*$");

	/**
	 * Filtra l'attributo 'style' di tutti gli elementi del documento, rimuovendolo se, dopo il
	 * filtro, non residua alcuna dichiarazione valida.
	 *
	 * @param doc documento HTML già sanificato tramite Safelist
	 * @param allowedProperties nomi delle proprietà CSS consentite (in minuscolo)
	 */
	public static void sanitize(Document doc, Set<String> allowedProperties) {
		if(doc == null || allowedProperties == null) {
			return;
		}
		for (Element el : doc.select("[" + ATTRIBUTE_STYLE + "]")) {
			String filtered = filterStyle(el.attr(ATTRIBUTE_STYLE), allowedProperties);
			if (filtered.isEmpty()) {
				el.removeAttr(ATTRIBUTE_STYLE);
			} else {
				el.attr(ATTRIBUTE_STYLE, filtered);
			}
		}
	}

	/**
	 * Filtra il contenuto di un attributo 'style', mantenendo le sole dichiarazioni il cui nome
	 * risulta consentito ed il cui valore non contiene costrutti pericolosi.
	 *
	 * @param style contenuto dell'attributo 'style'
	 * @param allowedProperties nomi delle proprietà CSS consentite (in minuscolo)
	 * @return le dichiarazioni superstiti, separate da '; ', oppure una stringa vuota
	 */
	public static String filterStyle(String style, Set<String> allowedProperties) {
		if(style == null || allowedProperties == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String declaration : style.split(";")) {
			int separator = declaration.indexOf(':');
			if (separator <= 0) {
				continue;
			}
			String name = declaration.substring(0, separator).trim().toLowerCase(Locale.ROOT);
			String value = CSS_IMPORTANT.matcher(declaration.substring(separator + 1).trim()).replaceAll("").trim();
			if (!CSS_PROPERTY_NAME.matcher(name).matches()
					|| !allowedProperties.contains(name)
					|| value.isEmpty()
					|| !CSS_VALUE_ALLOWED.matcher(value).matches()
					|| CSS_VALUE_DENIED.matcher(value).find()) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append("; ");
			}
			sb.append(name).append(":").append(value);
		}
		return sb.toString();
	}
}
