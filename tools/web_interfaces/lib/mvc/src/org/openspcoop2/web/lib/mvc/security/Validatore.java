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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Safelist;
import org.openspcoop2.web.lib.mvc.security.exception.ValidationException;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Validatore
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Validatore {

	private static Logger log;
	private SecurityProperties sc;
	private InputSanitizerProperties isp;
	private static Validatore instance;
	private Safelist safelist;

	public static synchronized void init(SecurityProperties sc, InputSanitizerProperties isp,  Logger log) {
		if(Validatore.log == null) {
			Validatore.log = log;
		}
		if(Validatore.instance == null) {
			Validatore.instance = new Validatore(sc, isp);
		}
	}

	public static synchronized Validatore getInstance() {
		return instance;
	}

	private Validatore(SecurityProperties sc, InputSanitizerProperties isp) {
		this.sc = sc;
		this.isp = isp;
		this.safelist = this.isp.getSafelist();
	}

	public String validate(String oggetto, String valore, boolean nullable, String... pattern) throws ValidationException {
		return validate(oggetto, valore, nullable, true, pattern);
	}

	public String validate(String oggetto, String valore, boolean nullable, boolean checkSqlInjection, String... pattern) throws ValidationException {
		return validate(oggetto, valore, Integer.valueOf(0), null, nullable, checkSqlInjection, pattern);
	}

	public String validate(String oggetto, String valore, Integer maxLength, boolean nullable, String... pattern) throws ValidationException {
		return validate(oggetto, valore, maxLength, nullable, true, pattern);
	}

	public String validate(String oggetto, String valore, Integer maxLength, boolean nullable, boolean checkSqlInjection, String... pattern) throws ValidationException {
		return validate(oggetto, valore, Integer.valueOf(0), maxLength, nullable, checkSqlInjection, pattern);
	}

	public String validate(String oggetto, String valore, Integer minLength, Integer maxLength, boolean nullable, boolean checkSqlInjection, String... pattern) throws ValidationException {

		List<Pattern> patternsToCheck = new ArrayList<>();

		for (int i = 0; i < pattern.length; i++) {
			String pt = pattern[i];
			Pattern p = this.sc.getValidationPattern(pt);

			if ( p != null ) {
				patternsToCheck.add(p);
			} else {
				throw new ValidationException("Non e' stata trovato un pattern di validazione per il tipo [" + StringUtils.join(pattern,",") + "].");
			}
		}

		// check stringa vuota/null
		checkEmpty(oggetto, valore, nullable);

		// check lunghezza min/max
		checkLength(oggetto, valore, minLength, maxLength);

		// check regexpr
		checkPatterns(oggetto, valore, patternsToCheck);

		// check sqlInjection
		if(checkSqlInjection) {
			checkSqlInjection(oggetto, valore, this.sc.getValidationPattern(Costanti.PATTERN_SQL_INJECTION));
		}

		return valore;
	}

	public String validateTabId(String idTab) {
		return validateTabId("IdTab", idTab);
	}

	public String validatePrevTabId(String idTab) {
		return validateTabId("PrevIdTab", idTab);
	}

	private String validateTabId(String oggetto, String idTab) {
		if(idTab != null) {
			try {
				return this.validate(oggetto, idTab, Integer.valueOf(36), false, true, org.openspcoop2.web.lib.mvc.security.Costanti.PATTERN_ID_TAB); 
			} catch(ValidationException e) {
				log.warn("Valore ["+idTab+"] ricevuto per il parametro ["+oggetto+"] non valido: " + e.getMessage(),e);
			}
		}
		return null;
	}


	/***
	 * Controllo se l'input e' vuoto
	 * 
	 * @param oggetto
	 * @param valore
	 * @param nullable
	 * @return
	 * @throws ValidationException
	 */
	private String checkEmpty(String oggetto, String valore, boolean nullable) throws ValidationException
	{
		// se e' nullable il controllo non si deve fare
		if(nullable) return valore;

		if(!StringUtils.isEmpty(valore))
			return valore;

		throw new ValidationException(oggetto + " non puo' essere vuoto o null.");
	}

	/***
	 * Controllo sulla lunghezza dell'input
	 * 
	 * @param oggetto
	 * @param valore
	 * @param minLength
	 * @param maxLength
	 * @return
	 * @throws ValidationException
	 */
	private String checkLength(String oggetto, String valore, Integer minLength, Integer maxLength) throws ValidationException
	{
		if(minLength != null &&
			valore.length() < minLength) {
			throw new ValidationException( oggetto + " non rispetta la lunghezza minima prevista di " + minLength + " caratteri.");
		}

		if(maxLength != null &&
			valore.length() > maxLength) {
			throw new ValidationException( oggetto + " non rispetta la lunghezza massima prevista di " + maxLength + " caratteri.");
		}

		return valore;
	}

	/***
	 * Validazione dell'input secondo i pattern previsti.
	 * 
	 * @param oggetto
	 * @param valore
	 * @param patternsToCheck
	 * @return
	 * @throws ValidationException
	 */
	private String checkPatterns(String oggetto, String valore, List<Pattern> patternsToCheck) throws ValidationException
	{
		for (Pattern p : patternsToCheck) {
			if ( !p.matcher(valore).matches() ) {
				throw new ValidationException( oggetto + " non rispetta il pattern di validazione previsto [" + p.pattern() +  "].");
			}
		}

		return valore;
	}

	/***
	 * Validazione dell'input secondo i pattern previsti.
	 * 
	 * @param oggetto
	 * @param valore
	 * @param pattern
	 * @return
	 * @throws ValidationException
	 */
	private String checkSqlInjection(String oggetto, String valore, Pattern pattern) throws ValidationException
	{
		if (pattern.matcher(valore).find()) {
			throw new ValidationException( oggetto + " non rispetta il pattern di validazione previsto [" + pattern.pattern() +  "].");
		}

		return valore;
	}

	public boolean verificaEsistenzaParametroOriginale(HttpServletRequest request, String parametro) {
		return this.getParametroOriginale(request, parametro) != null;
	}

	public String getParametroOriginale(HttpServletRequest request, String parametro) {
		if(request instanceof SecurityWrappedHttpServletRequest secReq) {
			return secReq.getOriginalParameter(parametro);
		}

		return request.getParameter(parametro);
	}

	public String getParametroSanificato(String originalValue, boolean usaValidazioneTextArea) {
		if(originalValue == null) {
			return null;
		}

		// Escape delle '&' per evitare che Jsoup le interpreti come entità HTML.
		// Jsoup è "tollerante" e riconosce entità HTML anche senza il ';' finale (es. '&not' viene interpretato come '¬').
		// L'escape garantisce che il testo venga preservato esattamente come inserito dall'utente.
		// L'Entities.unescape() finale ripristinerà le '&' originali.
		String valueToClean = originalValue.replace("&", "&amp;");

		Document.OutputSettings os = new Document.OutputSettings().prettyPrint(false);
		if(usaValidazioneTextArea) {
			/**
			 * Questo codice serviva prima dell'intervento dell'issue 1707 che ha spostato l'escape html dalle pagine java (helper) alle jsp (edit-page.jsp)
			 * per quanto riguarda il tipo di campo textarea.
			 *
			 * Grazie a questo: 
			 * Quindi la sanificazione Jsoup in input è ridondante e causa solo problemi. Se l'utente inserisce <script>alert(1)</script>:                                                                           
  			1. Viene salvato così nel DB                                                                                                                                                                          
  			2. Quando viene visualizzato, escapeHtml4() lo trasforma in &lt;script&gt;alert(1)&lt;/script&gt;                                                                                                     
  			3. Il browser mostra il testo letterale, nessun XSS       
			 * 
			// pulizia del contenuto della textarea
			String cleaned = Jsoup.clean(valueToClean, "", this.safelist, os);

			// inserisco una root fittizia, così non viene toccato il body radice del Document
			Document tmp = Jsoup.parse("<x-root>" + cleaned + "</x-root>");
			tmp.outputSettings(os);

			// rimuovo il body di primo livello, se presente
			Element firstTopBody = tmp.selectFirst("#__root > body");
			if (firstTopBody != null) firstTopBody.unwrap();

			String result = tmp.selectFirst("x-root").html();

			// unescape delle entità HTML
			return Entities.unescape(result);
			*/
			return originalValue;
		}

		/** Rimane necessario il jsou.parse per gli altri parametri; valutare se spostare l'escape come fatto per le text area */
		return Entities.unescape(Jsoup.parse(Jsoup.clean(valueToClean, "", this.safelist, os)).body().html());
	}
}
