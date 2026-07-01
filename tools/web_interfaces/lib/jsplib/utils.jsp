<%--
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
--%>

<%@ page session="true" pageEncoding="UTF-8" import="org.openspcoop2.web.lib.mvc.*, org.openspcoop2.web.lib.mvc.security.SecurityProperties" %>
<%
/* Stesso meccanismo CSP nonce di browserUtils.jsp: il nonce e' messo nella request da un Filter
   e va riportato in ogni tag <script> per essere accettato dalla Content-Security-Policy. */
String utilsRandomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);

/* Carico le 3 regex di validazione dei parametri HTTP (textarea, password, standard) dalla
   configurazione lato server (console.properties / monitor.properties). La stessa regex usata
   dal Validatore lato server viene cosi' replicata lato client-side per fornire feedback
   immediato all'utente prima del submit (cfr. SecurityWrappedHttpServletRequest). */
String patternHTTPParameterValue = SecurityProperties.getInstance().getPatternHTTPParameterValueAsString();
String patternHTTPParameterValueTextArea = SecurityProperties.getInstance().getPatternHTTPParameterValueTextAreaAsString();
String patternHTTPParameterValueTextAreaSingleLine = SecurityProperties.getInstance().getPatternHTTPParameterValueTextAreaSingleLineAsString();
String patternHTTPParameterValuePassword = SecurityProperties.getInstance().getPatternHTTPParameterValuePasswordAsString();

if(patternHTTPParameterValue == null) patternHTTPParameterValue = "";
if(patternHTTPParameterValueTextArea == null) patternHTTPParameterValueTextArea = "";
if(patternHTTPParameterValueTextAreaSingleLine == null) patternHTTPParameterValueTextAreaSingleLine = "";
if(patternHTTPParameterValuePassword == null) patternHTTPParameterValuePassword = "";
%>

<script type="text/javascript" nonce="<%= utilsRandomNonce %>">
/* Costanti di validazione esposte dal server: stesso regex pattern usato lato server da
   Validatore.validate() per ognuno dei 3 profili (standard, textarea, password). */
var GW_PATTERN_HTTP_PARAMETER_VALUE = "<%= patternHTTPParameterValue.replace("\\", "\\\\").replace("\"", "\\\"") %>";
var GW_PATTERN_HTTP_PARAMETER_VALUE_TEXTAREA = "<%= patternHTTPParameterValueTextArea.replace("\\", "\\\\").replace("\"", "\\\"") %>";
var GW_PATTERN_HTTP_PARAMETER_VALUE_TEXTAREA_SINGLELINE = "<%= patternHTTPParameterValueTextAreaSingleLine.replace("\\", "\\\\").replace("\"", "\\\"") %>";
var GW_PATTERN_HTTP_PARAMETER_VALUE_PASSWORD = "<%= patternHTTPParameterValuePassword.replace("\\", "\\\\").replace("\"", "\\\"") %>";

/* Nomi dei parametri hidden con cui la JSP edit-page.jsp espone gli identificativi dei campi
   "speciali" (textarea / password / lock / crypt) per cui la sanificazione di input lato server
   e' disabilitata. Le funzioni di validazione client-side li leggono in tempo reale dal DOM. */
var GW_PARAM_IDS_TA = "<%= Costanti.PARAMETRO_IDENTIFICATIVI_TEXT_AREA %>";
var GW_PARAM_IDS_TA_SINGLELINE = "<%= Costanti.PARAMETRO_IDENTIFICATIVI_TEXT_AREA_SINGLE_LINE %>";
var GW_PARAM_IDS_PWD = "<%= Costanti.PARAMETRO_IDENTIFICATIVI_PS %>";

/* Legge il valore di un hidden input identificato dal nome e lo split in lista CSV. */
function gwGetIdsList(hiddenName) {
	var el = document.querySelector('input[name="' + hiddenName + '"]');
	if (!el || !el.value) return [];
	return el.value.split(",").filter(function(s) { return s.length > 0; });
}

/* Validazione di un singolo valore di campo. Sceglie automaticamente il pattern in base alla
   classificazione del campo (textarea / password / standard) come fa il server. */
/* Sceglie il pattern di validazione in base alla classificazione del campo (single-line / textarea /
   password / standard) come fa il server. */
function gwGetFieldPattern(fieldName) {
	var idsTA = gwGetIdsList(GW_PARAM_IDS_TA);
	var idsTASingleLine = gwGetIdsList(GW_PARAM_IDS_TA_SINGLELINE);
	var idsPwd = gwGetIdsList(GW_PARAM_IDS_PWD);
	if (idsTASingleLine.indexOf(fieldName) !== -1) {
		return GW_PATTERN_HTTP_PARAMETER_VALUE_TEXTAREA_SINGLELINE;
	} else if (idsTA.indexOf(fieldName) !== -1) {
		return GW_PATTERN_HTTP_PARAMETER_VALUE_TEXTAREA;
	} else if (idsPwd.indexOf(fieldName) !== -1) {
		return GW_PATTERN_HTTP_PARAMETER_VALUE_PASSWORD;
	}
	return GW_PATTERN_HTTP_PARAMETER_VALUE;
}

/* Vero se il campo è classificato come textarea / single-line: per questi, come lato server in
   ItemBean, valgono i vincoli aggiuntivi sugli spazi/tab iniziali e finali. */
function gwIsTextAreaField(fieldName) {
	return gwGetIdsList(GW_PARAM_IDS_TA).indexOf(fieldName) !== -1
		|| gwGetIdsList(GW_PARAM_IDS_TA_SINGLELINE).indexOf(fieldName) !== -1;
}

/* 'leading' / 'trailing' se il valore inizia / finisce con spazio o tab, altrimenti null. */
function gwLeadingTrailingSpace(value) {
	var f = value.charAt(0), l = value.charAt(value.length - 1);
	if (f === ' ' || f === '\t') return 'leading';
	if (l === ' ' || l === '\t') return 'trailing';
	return null;
}

function gwValidateField(value, fieldName) {
	if (!value) return true;
	if (gwIsTextAreaField(fieldName) && gwLeadingTrailingSpace(value) !== null) return false;
	var pattern = gwGetFieldPattern(fieldName);
	if (!pattern) return true;
	try {
		return new RegExp(pattern, "u").test(value);
	} catch (e) {
		// se la regex non compila lato client (es. browser troppo vecchio), non blocchiamo il submit
		return true;
	}
}

/* Descrizione "umana" di un carattere non ammesso, per il messaggio di errore. */
function gwDescribeChar(ch) {
	var code = ch.codePointAt(0);
	switch (code) {
		case 0x0A: return 'a capo (LF)';
		case 0x0D: return 'ritorno a capo (CR)';
		case 0x09: return 'tabulazione (TAB)';
		case 0x00: return 'NUL';
	}
	if (code < 0x20 || (code >= 0x7F && code <= 0x9F)) {
		return 'carattere di controllo U+' + ('0000' + code.toString(16).toUpperCase()).slice(-4);
	}
	return '«' + ch + '»';
}

/* Ritorna il primo carattere del valore non ammesso per il campo (o null se tutti validi).
   Ogni carattere viene testato singolarmente contro il pattern del campo (i pattern sono nella
   forma ^[classe]*$, quindi un singolo carattere ammesso matcha, uno vietato no). */
function gwFirstInvalidChar(value, fieldName) {
	if (!value) return null;
	var pattern = gwGetFieldPattern(fieldName);
	if (!pattern) return null;
	var re;
	try {
		re = new RegExp(pattern, "u");
	} catch (e) {
		return null;
	}
	for (var ch of value) {
		if (!re.test(ch)) return ch;
	}
	return null;
}

/* Messaggio di errore per un campo non valido (o null se valido): controlla, nell'ordine, spazi/tab
   iniziali-finali (per textarea/single-line) e caratteri non ammessi dal pattern, indicando — se
   individuabile — il carattere specifico. */
function gwFieldError(el) {
	var value = el.value;
	if (!value) return null;
	var fieldName = el.name;
	var label = gwGetFieldLabel(el);
	if (gwIsTextAreaField(fieldName)) {
		var sp = gwLeadingTrailingSpace(value);
		if (sp === 'leading') return 'Il campo "' + label + '" non può iniziare con uno spazio';
		if (sp === 'trailing') return 'Il campo "' + label + '" non può terminare con uno spazio';
	}
	var bad = gwFirstInvalidChar(value, fieldName);
	if (bad !== null) {
		return 'Il campo "' + label + '" contiene un carattere non ammesso: ' + gwDescribeChar(bad);
	}
	return null;
}

/* Pulisce gli errori di validazione precedentemente segnalati. */
function gwClearValidationErrors(form) {
	var withErrors = form.querySelectorAll('.gw-validation-error');
	for (var i = 0; i < withErrors.length; i++) {
		withErrors[i].classList.remove('gw-validation-error');
		withErrors[i].removeAttribute('aria-invalid');
	}
	var messages = form.querySelectorAll('.gw-validation-error-message');
	for (var j = 0; j < messages.length; j++) {
		messages[j].parentNode.removeChild(messages[j]);
	}
}

/* Marca un campo come invalido (bordo rosso + messaggio inline). Il marcatore viene rimosso
   automaticamente quando l'utente corregge il valore. Replichiamo la struttura DOM/CSS
   esattamente come la "nota" di un DataElement (cfr. edit-page.jsp -> <p class="note ...">):
   il messaggio e' un <p class="note ..."> inserito come ultimo figlio del .prop, ereditando
   gli stessi margin-left per le varianti labelMedium / labelLong (cfr. linkit-base.css). */
function gwMarkFieldInvalid(el, message) {
	el.classList.add('gw-validation-error');
	el.setAttribute('aria-invalid', 'true');
	var msgId = (el.id || el.name) + '_gw_validation_err';
	if (!document.getElementById(msgId)) {
		var container = el.closest ? el.closest('.prop') : null;
		var msg = document.createElement('p');
		msg.id = msgId;
		// "gw-validation-error-message" e' la classe che applica il colore rosso (override sopra .note)
		msg.className = 'note gw-validation-error-message';
		if (container) {
			var lbl = container.querySelector('label');
			if (lbl && lbl.classList.contains('labelLong')) msg.classList.add('labelLong');
			else if (lbl && lbl.classList.contains('labelMedium')) msg.classList.add('labelMedium');
		}
		msg.textContent = message;
		if (container) {
			container.appendChild(msg);
		} else if (el.parentNode) {
			el.parentNode.insertBefore(msg, el.nextSibling);
		}
	}
}

/* Rimuove la segnalazione di errore da un singolo campo. */
function gwClearFieldError(el) {
	el.classList.remove('gw-validation-error');
	el.removeAttribute('aria-invalid');
	var msgId = (el.id || el.name) + '_gw_validation_err';
	var m = document.getElementById(msgId);
	if (m && m.parentNode) m.parentNode.removeChild(m);
}

/* Calcola la label "umana" del campo da usare nel messaggio di errore.
   Esclude i marcatori "campo obbligatorio" resi come <em>*</em> dentro la label e
   fa il trim degli spazi che vengono dalla formattazione della JSP. */
function gwExtractLabelText(lbl) {
	if (!lbl) return "";
	var clone = lbl.cloneNode(true);
	var ems = clone.querySelectorAll('em');
	for (var i = 0; i < ems.length; i++) ems[i].parentNode.removeChild(ems[i]);
	return clone.textContent.replace(/\s+/g, ' ').trim();
}
function gwGetFieldLabel(el) {
	if (el.labels && el.labels[0]) {
		var txt = gwExtractLabelText(el.labels[0]);
		if (txt) return txt;
	}
	if (el.id) {
		var lbl = document.querySelector('label[for="' + el.id + '"]');
		var txt2 = gwExtractLabelText(lbl);
		if (txt2) return txt2;
	}
	return el.name;
}

/* Determina se un elemento del form va sottoposto a validazione client-side. */
function gwIsValidatableElement(el) {
	if (!el.name) return false;
	if (el.name === GW_PARAM_IDS_TA || el.name === GW_PARAM_IDS_TA_SINGLELINE || el.name === GW_PARAM_IDS_PWD) return false;
	if (el.name === "_csrf") return false;
	if (el.name.indexOf("__i_hidden_") === 0) return false;
	if (el.name === "__tabKey__" || el.name === "__prevTabKey__") return false;
	var t = el.type;
	if (t === "hidden" || t === "submit" || t === "button" || t === "reset" ||
		t === "checkbox" || t === "radio" || t === "file" ||
		t === "select-one" || t === "select-multiple") return false;
	return true;
}

/* Attacca i listener di validazione live (blur) ai campi rilevanti del form, cosi' l'utente
   vede subito l'errore quando esce dal campo, senza aspettare il click su Salva.
   IMPORTANTE: le mutazioni DOM (insert/rimozione del <p> di errore) sono posticipate via
   setTimeout(0). Senza questo, quando l'utente clicca sul bottone Salva mentre il focus e'
   su un campo invalido, il blur scatta tra mousedown e mouseup, l'insert del <p> sposta
   il bottone verso il basso, e mouseup cade in un punto vuoto, quindi il browser non
   triggera l'evento click. Era il sintomo "al primo click non succede niente, al secondo
   si'" (al secondo click il DOM e' gia' nello stato finale, niente movimento del bottone). */
function gwInitLiveValidation(form) {
	if (!form || !form.elements) return;
	for (var k = 0; k < form.elements.length; k++) {
		var el = form.elements[k];
		if (!gwIsValidatableElement(el)) continue;
		if (el.dataset && el.dataset.gwLiveValidationBound === "1") continue;
		el.addEventListener("blur", function(evt) {
			var target = evt.target;
			if (!gwIsValidatableElement(target)) return;
			/* Se l'utente sta cliccando su un bottone (submit/button/reset), NON mutiamo
			   il DOM qui: l'insert del <p> di errore sposterebbe il bottone verso il
			   basso, il mouseup cadrebbe fuori e il click NON scatterebbe. Sara' il
			   click handler del bottone (CheckDati -> gwValidateForm) a evidenziare
			   gli errori in modo coordinato. setTimeout(0) NON basta perche' il browser
			   processa la task queue tra mousedown e mouseup. */
			var related = evt.relatedTarget;
			if (related) {
				if (related.tagName === 'BUTTON') return;
				if (related.tagName === 'INPUT' &&
					(related.type === 'submit' || related.type === 'button' || related.type === 'reset')) {
					return;
				}
			}
			setTimeout(function() {
				var err = gwFieldError(target);
				if (!err) {
					gwClearFieldError(target);
				} else {
					gwMarkFieldInvalid(target, err);
				}
			}, 0);
		});
		el.addEventListener("input", function(evt) {
			// rimuove l'errore mentre l'utente sta digitando una volta che il valore torna valido,
			// senza pero' segnalare un nuovo errore (lo facciamo solo al blur per non essere invasivi)
			var target = evt.target;
			if (target.classList.contains('gw-validation-error') && gwValidateField(target.value, target.name)) {
				setTimeout(function() { gwClearFieldError(target); }, 0);
			}
		});
		if (el.dataset) el.dataset.gwLiveValidationBound = "1";
	}
}

/* Valida tutti i campi del form. Ritorna true se ok, false se almeno un campo non e' valido
   (in quel caso scrolla al primo errore e lo mette in focus). */
function gwValidateForm(form) {
	gwClearValidationErrors(form);
	var firstInvalid = null;
	for (var k = 0; k < form.elements.length; k++) {
		var el = form.elements[k];
		if (!el.name || !el.value) continue;
		// salta i parametri tecnici (CSRF, identificativi, tab key, hidden info dialog ...)
		if (el.name === GW_PARAM_IDS_TA || el.name === GW_PARAM_IDS_TA_SINGLELINE || el.name === GW_PARAM_IDS_PWD) continue;
		if (el.name === "_csrf") continue;
		if (el.name.indexOf("__i_hidden_") === 0) continue;
		if (el.name === "__tabKey__" || el.name === "__prevTabKey__") continue;
		var err = gwFieldError(el);
		if (err) {
			gwMarkFieldInvalid(el, err);
			if (!firstInvalid) firstInvalid = el;
		}
	}
	if (firstInvalid) {
		gwScrollFocusFirstInvalid(firstInvalid);
		return false;
	}
	return true;
}

/* Scroll + focus al primo campo invalido. Nello stesso tick del click sul bottone Salva
   competono diverse mutazioni del DOM (overlay #ajax_status_div show/hide, accordion
   slideUp/slideDown, postback ajax hook, insert del <p> di errore): un singolo scroll
   smooth viene "ucciso" da altri scroll che partono dopo. Strategia: polling della
   posizione del target via getBoundingClientRect ogni 50ms. Quando la rect e' identica
   per 2 letture consecutive il layout e' stabile, e a quel punto scrolliamo
   istantaneamente con window.scrollTo (uno scroll istantaneo non e' killabile). */
function gwScrollFocusFirstInvalid(target) {
	var lastSignature = null;
	var stableCount = 0;
	var attempts = 0;
	var maxAttempts = 20; // 50ms x 20 = 1s di budget totale
	var tick = function() {
		var rect = target.getBoundingClientRect();
		var signature = Math.round(rect.top) + ',' + Math.round(rect.left) + ',' + Math.round(rect.height);
		if (signature === lastSignature) {
			stableCount++;
		} else {
			stableCount = 0;
			lastSignature = signature;
		}
		attempts++;
		if (stableCount < 2 && attempts < maxAttempts) {
			setTimeout(tick, 50);
			return;
		}
		var viewportH = window.innerHeight || document.documentElement.clientHeight;
		var absoluteY = rect.top + (window.pageYOffset || document.documentElement.scrollTop);
		var targetScrollY = Math.max(0, absoluteY - (viewportH / 2) + (rect.height / 2));
		window.scrollTo(0, targetScrollY);
		if (target.focus) {
			try { target.focus({preventScroll: true}); }
			catch (e) { /* browser senza preventScroll: rinunciamo al focus per non riscrollare */ }
		}
	};
	setTimeout(tick, 0);
}

/* Avvia la validazione live su tutti i form della pagina appena il DOM e' pronto. Usiamo
   plain JS (no jQuery) per non dipendere dall'ordine di inclusione degli script. */
(function() {
	function gwBootstrapLiveValidation() {
		var forms = document.querySelectorAll("form");
		for (var i = 0; i < forms.length; i++) {
			gwInitLiveValidation(forms[i]);
		}
	}
	if (document.readyState === "loading") {
		document.addEventListener("DOMContentLoaded", gwBootstrapLiveValidation);
	} else {
		gwBootstrapLiveValidation();
	}
})();
</script>
