/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.ctrlstat.costanti;

import java.util.Enumeration;
import java.util.Hashtable;

import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataServizioIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;

/**
 * 
 * Gestisce le costanti di identificazione visualizzate all'utente.
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public enum IdentificazioneView {

	USER_INPUT, REGISTER_INPUT, URL_BASED, CONTENT_BASED, INPUT_BASED, SOAP_ACTION_BASED, WSDL_BASED;

	static Hashtable<IdentificazioneView, String> table;
	static {
		IdentificazioneView.table = new Hashtable<IdentificazioneView, String>();

		IdentificazioneView.table.put(USER_INPUT, USER_INPUT.toString());
		IdentificazioneView.table.put(REGISTER_INPUT, REGISTER_INPUT.toString());
		IdentificazioneView.table.put(URL_BASED, URL_BASED.toString());
		IdentificazioneView.table.put(CONTENT_BASED, CONTENT_BASED.toString());
		IdentificazioneView.table.put(INPUT_BASED, INPUT_BASED.toString());
		IdentificazioneView.table.put(SOAP_ACTION_BASED, SOAP_ACTION_BASED.toString());
		IdentificazioneView.table.put(WSDL_BASED, WSDL_BASED.toString());
	}

	public static IdentificazioneView getFromString(String val) {
		Enumeration<IdentificazioneView> en = IdentificazioneView.table.keys();
		while (en.hasMoreElements()) {
			IdentificazioneView key = en.nextElement();
			if (IdentificazioneView.table.get(key).equals(val)) {
				return key;
			}
		}
		throw new IllegalArgumentException("No enum const " + IdentificazioneView.class.getName() + "." + val);
	}

	/**
	 * Converte la stringa in input come costante di Identificazione
	 */
	public static PortaDelegataSoggettoErogatoreIdentificazione view2db_soggettoErogatore(String viewConstat2Convert) {

		switch (IdentificazioneView.getFromString(viewConstat2Convert)) {
		case CONTENT_BASED:
			return PortaDelegataSoggettoErogatoreIdentificazione.CONTENT_BASED;

		case REGISTER_INPUT:
		case USER_INPUT:
			return PortaDelegataSoggettoErogatoreIdentificazione.STATIC;

		case URL_BASED:
			return PortaDelegataSoggettoErogatoreIdentificazione.URL_BASED;

		case INPUT_BASED:
			return PortaDelegataSoggettoErogatoreIdentificazione.INPUT_BASED;

		case SOAP_ACTION_BASED: // non previsto nel soggetto
		case WSDL_BASED:
			return PortaDelegataSoggettoErogatoreIdentificazione.STATIC;

		}

		return PortaDelegataSoggettoErogatoreIdentificazione.STATIC;

	}

	public static PortaDelegataServizioIdentificazione view2db_servizio(String viewConstat2Convert) {

		switch (IdentificazioneView.getFromString(viewConstat2Convert)) {
		case CONTENT_BASED:
			return PortaDelegataServizioIdentificazione.CONTENT_BASED;

		case REGISTER_INPUT:
		case USER_INPUT:
			return PortaDelegataServizioIdentificazione.STATIC;

		case URL_BASED:
			return PortaDelegataServizioIdentificazione.URL_BASED;

		case INPUT_BASED:
			return PortaDelegataServizioIdentificazione.INPUT_BASED;

		case SOAP_ACTION_BASED: // non previsto nel servizio
		case WSDL_BASED:
			return PortaDelegataServizioIdentificazione.STATIC;
		}

		return PortaDelegataServizioIdentificazione.STATIC;

	}

	public static PortaDelegataAzioneIdentificazione view2db_azione(String viewConstat2Convert) {

		switch (IdentificazioneView.getFromString(viewConstat2Convert)) {
		case CONTENT_BASED:
			return PortaDelegataAzioneIdentificazione.CONTENT_BASED;

		case REGISTER_INPUT:
		case USER_INPUT:
			return PortaDelegataAzioneIdentificazione.STATIC;

		case URL_BASED:
			return PortaDelegataAzioneIdentificazione.URL_BASED;

		case INPUT_BASED:
			return PortaDelegataAzioneIdentificazione.INPUT_BASED;

		case SOAP_ACTION_BASED:
			return PortaDelegataAzioneIdentificazione.SOAP_ACTION_BASED;
		case WSDL_BASED:
			return PortaDelegataAzioneIdentificazione.WSDL_BASED;
		}

		return PortaDelegataAzioneIdentificazione.STATIC;

	}

	@Override
	public String toString() {
		String res = "";
		switch (this) {
		case CONTENT_BASED:
			res = "content-based";
			break;
		case REGISTER_INPUT:
			res = "register-input";
			break;
		case URL_BASED:
			res = "url-based";
			break;
		case USER_INPUT:
			res = "user-input";
			break;
		case INPUT_BASED:
			res = "input-based";
			break;
		case SOAP_ACTION_BASED:
			res = "soap-action-based";
			break;
		case WSDL_BASED:
			res = "wsdl-based";
			break;
		}

		return res;
	}

}
