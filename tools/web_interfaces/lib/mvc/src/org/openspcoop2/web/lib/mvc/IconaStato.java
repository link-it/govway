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

package org.openspcoop2.web.lib.mvc;

/**
 * Icone di stato condivise dalla console di gestione e dalla console di monitoraggio.
 *
 * Lo stato non e' affidato al solo colore (WCAG 1.4.1): ogni significato ha una forma
 * propria, e il colore si aggiunge. La convenzione e' unica per le due console:
 * <ul>
 * <li>stato di un'entita' (API, gruppo, connettore, utente, stato del gateway):
 *     cerchio con spunta = ok/abilitato, triangolo con '!' = avviso, cerchio con X = errore,
 *     cerchio pieno a meta' = parzialmente abilitato, cerchio con trattino = disabilitato;</li>
 * <li>singola funzionalita' configurata (righe della configurazione, policy, plugin, ...):
 *     il sigillo di sempre, con il simbolo interno della stessa convenzione;</li>
 * <li>stato di salute degli allarmi: le forme degli allarmi del monitor.</li>
 * </ul>
 * I disegni rispettano il rapporto di contrasto di 3:1 (WCAG 1.4.11): contorno contro lo
 * sfondo della pagina e simbolo interno contro il riempimento. Le icone dello stato di
 * un'entita' hanno un alone bianco, che le stacca anche dal blu dell'intestazione.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum IconaStato {

	OK("stato_ok.svg", "Ok"),
	AVVISO("stato_avviso.svg", "Attenzione"),
	ERRORE("stato_errore.svg", "Errore"),
	PARZIALE("stato_parziale.svg", "Parzialmente abilitato"),
	DISABILITATO("stato_disabilitato.svg", "Disabilitato"),
	NON_ATTIVO("stato_non_attivo.svg", "Non attivo"),

	FUNZIONALITA_ABILITATA("funzionalita_abilitata.svg", "Abilitata"),
	FUNZIONALITA_AVVISO("funzionalita_avviso.svg", "Abilitata, non bloccante"),
	FUNZIONALITA_ERRORE("funzionalita_errore.svg", "Configurazione errata"),
	FUNZIONALITA_DISABILITATA("funzionalita_disabilitata.svg", "Disabilitata"),

	ALLARME_OK("allarme_ok.svg", "Ok"),
	ALLARME_WARNING("allarme_warning.svg", "Warning"),
	ALLARME_ERROR("allarme_error.svg", "Error"),
	ALLARME_DISABILITATO("allarme_disabilitato.svg", "Disabilitato");

	/** Cartella delle immagini, relativa al contesto dell'applicazione */
	public static final String CARTELLA = "images/tema_link/";

	private final String file;
	private final String descrizione;

	IconaStato(String file, String descrizione){
		this.file = file;
		this.descrizione = descrizione;
	}

	/** Nome del file, da usare dopo {@link #CARTELLA} */
	public String getFile() {
		return this.file;
	}

	/** Percorso relativo al contesto, per le pagine JSP della console ('images/tema_link/...') */
	public String getPercorso() {
		return CARTELLA + this.file;
	}

	/** Percorso assoluto nel contesto, per le pagine JSF del monitor ('/images/tema_link/...') */
	public String getPercorsoAssoluto() {
		return "/" + CARTELLA + this.file;
	}

	/** Descrizione predefinita, usata come testo alternativo quando manca un testo specifico */
	public String getDescrizione() {
		return this.descrizione;
	}

	/**
	 * Testo alternativo dell'icona: il suggerimento specifico se presente, altrimenti la
	 * descrizione predefinita. Il testo non e' codificato per HTML.
	 */
	public String getTestoAlternativo(String suggerimento) {
		if(suggerimento != null && !suggerimento.trim().isEmpty()) {
			return suggerimento;
		}
		return this.descrizione;
	}

	/**
	 * Icona corrispondente al valore di stato di un elemento di tipo checkbox
	 * ({@link CheckboxStatusType} o i valori di {@link Costanti}).
	 * Un valore assente o non riconosciuto resta, come in passato, lo stato disabilitato.
	 */
	public static IconaStato fromValoreCheckbox(String valore) {
		if(valore == null) {
			return DISABILITATO;
		}
		switch (valore) {
		case Costanti.CHECK_BOX_ENABLED:
			return OK;
		case Costanti.CHECK_BOX_WARN:
			return AVVISO;
		case Costanti.CHECK_BOX_PARTIAL:
			return PARZIALE;
		case Costanti.CHECK_BOX_ERROR:
			return ERRORE;
		case Costanti.CHECK_BOX_OFF:
			return NON_ATTIVO;
		case Costanti.CHECK_BOX_CONFIG_ENABLE:
			return FUNZIONALITA_ABILITATA;
		case Costanti.CHECK_BOX_CONFIG_WARNING:
			return FUNZIONALITA_AVVISO;
		case Costanti.CHECK_BOX_CONFIG_ERROR:
			return FUNZIONALITA_ERRORE;
		case Costanti.CHECK_BOX_CONFIG_DISABLE:
			return FUNZIONALITA_DISABILITATA;
		case Costanti.CHECK_BOX_ALARM_OK:
			return ALLARME_OK;
		case Costanti.CHECK_BOX_ALARM_WARNING:
			return ALLARME_WARNING;
		case Costanti.CHECK_BOX_ALARM_ERROR:
			return ALLARME_ERROR;
		case Costanti.CHECK_BOX_ALARM_DISABLE:
			return ALLARME_DISABILITATO;
		default:
			return DISABILITATO;
		}
	}
}
