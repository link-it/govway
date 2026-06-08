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

package org.openspcoop2.utils;

import org.slf4j.Logger;

/**	
 * IVersionInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IVersionInfo {
	
	public void init(Logger log, Object ... objects) throws UtilsException;
	
	public String getInfo(Object ... params) throws UtilsException;
		
	public String getErrorMessage(Object ... params);
	
	public String getErrorTitleSuffix(Object ... params);
	
	public String getWarningMessage(Object ... params);
	
	public String getWarningTitleSuffix(Object ... params);
	
	public String getWebSite(Object ... params);

	public String getCopyright(Object ... params);

	public void set(byte[] info, Logger log, Object ... objects) throws UtilsException;

	/**
	 * Arricchimenti grafici opzionali dell'avviso (icona, call-to-action).
	 * Sono metodi 'default' che ritornano null: una implementazione del plugin li valorizza
	 * per attivare il rendering ricco lato console; se non valorizzati il comportamento resta invariato.
	 */

	/** Nome dell'icona (es. material icon: 'warning','error','schedule') da mostrare nel banner del messaggio. */
	public default String getMessageIcon(Object ... params) {
		return null;
	}

	/** Nome dell'icona (es. material icon) da mostrare accanto al suffisso del titolo della console. */
	public default String getTitleIcon(Object ... params) {
		return null;
	}

	/** Etichetta della call-to-action mostrata nel banner. */
	public default String getActionLabel(Object ... params) {
		return null;
	}

	/** URL della call-to-action mostrata nel banner. */
	public default String getActionUrl(Object ... params) {
		return null;
	}

	/**
	 * Popup informativo opzionale da mostrare dopo il login (una sola volta).
	 * Ritorna null se non c'e' nulla da mostrare. I testi del popup sono frammenti HTML fidati.
	 */
	public default VersionInfoPopup getPostLoginPopup(Object ... params) {
		return null;
	}

	/**
	 * Indica se le voci del menu di navigazione devono risultare disabilitate (visibili ma non cliccabili).
	 * Interrogato ad ogni pagina; ritorna false per default.
	 */
	public default boolean isMenuDisabled(Object ... params) {
		return false;
	}

	/**
	 * Etichetta del pulsante di invio della sezione di aggiornamento delle informazioni di versione (pagina Informazioni).
	 * Permette all'implementazione di adattare il testo allo stato corrente. Ritorna null per usare il default generico.
	 */
	public default String getUpdateInfoActionLabel(Object ... params) {
		return null;
	}

}
