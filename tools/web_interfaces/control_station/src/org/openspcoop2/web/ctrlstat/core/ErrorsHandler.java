/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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


package org.openspcoop2.web.ctrlstat.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;

/**
 * ErrorsHandler
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErrorsHandler {

	private Hashtable<ErrorsHandlerCostant, ArrayList<String>> map;
	private String lineSeparator;
	private String customMessage;

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public ErrorsHandler() {
		this.map = new Hashtable<ErrorsHandlerCostant, ArrayList<String>>();
		this.lineSeparator = "<br>";
		this.customMessage = "Errori durante la rimozione :";
	}

	/**
	 * Aggiunge un messaggio di errore alla lista appartenete (indicata dal
	 * parametro key)
	 * 
	 * @param key
	 * @param errorMessage
	 */
	public void addError(ErrorsHandlerCostant key, String errorMessage) {
		// clean string che matcha [stringa] ritorna solo il contenuto di
		// stringa
		// il valore di stringa viene recuperato con $1
		String regex = "\\[(.*)\\]";
		if (errorMessage.matches(regex)) {
			errorMessage = errorMessage.replaceFirst(regex, "$1");
		}

		ArrayList<String> mex;
		if (this.map.containsKey(key)) {
			mex = this.map.get(key);
		} else {
			mex = new ArrayList<String>();
		}

		mex.add(errorMessage);
		this.map.put(key, mex);
	}

	public void addError(ErrorsHandlerCostant key, ArrayList<String> errorMessage) {
		ArrayList<String> mex;
		if (this.map.containsKey(key)) {
			mex = this.map.get(key);
			mex.addAll(errorMessage);
		} else {
			mex = errorMessage;
		}

		this.map.put(key, mex);
	}

	/**
	 * Ritorna una stringa ben formattata con il contenuto degli errori gestiti
	 * dall'handler
	 * 
	 * @return una stringa ben formattata con il contenuto degli errori gestiti
	 */
	public String formatErrorMessage() {
		String msg = this.customMessage == null ? "Errors :" : this.customMessage;

		Set<ErrorsHandlerCostant> keys = this.map.keySet();

		for (ErrorsHandlerCostant key : keys) {
			ArrayList<String> messages = this.map.get(key);

			// aggiungo una linea
			msg += this.lineSeparator;

			switch (key) {
				case IN_USO_IN_PORTE_APPLICATIVE:
					msg += " - in uso in Porte Applicative: " + messages.toString();
					break;

				case IN_USO_IN_SERVIZI_APPLICATIVI:
					msg += " - in uso in Servizi Applicativi: " + messages.toString();
					break;

				case IN_USO_IN_PORTE_DELEGATE:
					msg += " - in uso in Porte Delegate: " + messages.toString();
					break;

				case IN_USO_IN_SERVIZI:
					msg += " - in uso in Servizi: " + messages.toString();
					break;

				case POSSIEDE_RUOLI:
					msg += " - possiede i Ruoli: " + messages.toString();
					break;

				case POSSIEDE_FRUITORI:
					msg += " - possiede Soggetti come Fruitori: " + messages.toString();
					break;

				case UTILIZZATO_IN_POLITICHE_SICUREZZA:
					msg += " - utilizzato come Politica di Sicurezza : " + messages.toString();
					break;
				case IN_USO_IN_ACCORDI:
					msg += " - utilizzato in Accordo di Servizio : " + messages.toString();
					break;
				case IS_PARTECIPANTE_COOPERAZIONE:
					msg += " - partecipante in Cooperazione : " + messages.toString();
					break;
				case IS_REFERENTE:
					msg += " - referente : " + messages.toString();
					break;
				case IS_REFERENTE_COOPERAZIONE:
					msg += " - referente di cooperazione : " + messages.toString();
					break;
				case IS_SERVIZIO_COMPONENTE_IN_ACCORDI:
					msg += " - utilizzato come Servizio Componente in Accordi : " + messages.toString();
					break;
			}
		}
		msg += this.lineSeparator;

		return msg;
	}

	public void setCustomMessage(String customMessage) {
		this.customMessage = this.lineSeparator + customMessage;
	}
}
