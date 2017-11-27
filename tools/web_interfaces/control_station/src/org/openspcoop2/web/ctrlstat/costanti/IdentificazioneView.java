/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.costanti;

import java.util.Enumeration;
import java.util.Hashtable;

import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;

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

	USER_INPUT, REGISTER_INPUT, HEADER_BASED, URL_BASED, CONTENT_BASED, INPUT_BASED, SOAP_ACTION_BASED, INTERFACE_BASED, PROTOCOL_BASED;

	static Hashtable<IdentificazioneView, String> table;
	static {
		IdentificazioneView.table = new Hashtable<IdentificazioneView, String>();

		IdentificazioneView.table.put(USER_INPUT, USER_INPUT.toString());
		IdentificazioneView.table.put(REGISTER_INPUT, REGISTER_INPUT.toString());
		IdentificazioneView.table.put(HEADER_BASED, HEADER_BASED.toString());
		IdentificazioneView.table.put(URL_BASED, URL_BASED.toString());
		IdentificazioneView.table.put(CONTENT_BASED, CONTENT_BASED.toString());
		IdentificazioneView.table.put(INPUT_BASED, INPUT_BASED.toString());
		IdentificazioneView.table.put(SOAP_ACTION_BASED, SOAP_ACTION_BASED.toString());
		IdentificazioneView.table.put(INTERFACE_BASED, INTERFACE_BASED.toString());
		IdentificazioneView.table.put(PROTOCOL_BASED, PROTOCOL_BASED.toString());
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

	public static PortaDelegataAzioneIdentificazione view2db_azione_portaDelegata(String viewConstat2Convert) {

		switch (IdentificazioneView.getFromString(viewConstat2Convert)) {
		case CONTENT_BASED:
			return PortaDelegataAzioneIdentificazione.CONTENT_BASED;

		case REGISTER_INPUT:
		case USER_INPUT:
			return PortaDelegataAzioneIdentificazione.STATIC;

		case HEADER_BASED:
			return PortaDelegataAzioneIdentificazione.HEADER_BASED;
			
		case URL_BASED:
			return PortaDelegataAzioneIdentificazione.URL_BASED;

		case INPUT_BASED:
			return PortaDelegataAzioneIdentificazione.INPUT_BASED;

		case SOAP_ACTION_BASED:
			return PortaDelegataAzioneIdentificazione.SOAP_ACTION_BASED;
			
		case INTERFACE_BASED:
			return PortaDelegataAzioneIdentificazione.INTERFACE_BASED;
			
		case PROTOCOL_BASED:
			throw new RuntimeException("Tipo ["+viewConstat2Convert+"] non supportato nella Porta Delegata");
		}

		return PortaDelegataAzioneIdentificazione.STATIC;

	}
	
	public static PortaApplicativaAzioneIdentificazione view2db_azione_portaApplicativa(String viewConstat2Convert) {

		switch (IdentificazioneView.getFromString(viewConstat2Convert)) {
		case CONTENT_BASED:
			return PortaApplicativaAzioneIdentificazione.CONTENT_BASED;

		case REGISTER_INPUT:
		case USER_INPUT:
			return PortaApplicativaAzioneIdentificazione.STATIC;

		case HEADER_BASED:
			return PortaApplicativaAzioneIdentificazione.HEADER_BASED;
			
		case URL_BASED:
			return PortaApplicativaAzioneIdentificazione.URL_BASED;

		case INPUT_BASED:
			return PortaApplicativaAzioneIdentificazione.INPUT_BASED;

		case SOAP_ACTION_BASED:
			return PortaApplicativaAzioneIdentificazione.SOAP_ACTION_BASED;
			
		case INTERFACE_BASED:
			return PortaApplicativaAzioneIdentificazione.INTERFACE_BASED;
			
		case PROTOCOL_BASED:
			return PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED;
		}

		return PortaApplicativaAzioneIdentificazione.STATIC;

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
		case HEADER_BASED:
			res = "header-based";
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
		case INTERFACE_BASED:
			res = "interface-based";
			break;
		case PROTOCOL_BASED:
			res = "protocol-based";
			break;
		}

		return res;
	}

}
