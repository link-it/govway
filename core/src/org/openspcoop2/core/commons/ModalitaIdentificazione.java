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


package org.openspcoop2.core.commons;

import java.util.ArrayList;
import java.util.List;

/**
 * ModalitaIdentificazione
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum ModalitaIdentificazione {

	STATIC("Informazione Statica", "Nome"),
	PROTOCOL_BASED("Profilo di Interoperabilità", null),
	REGISTER_BASED("Registro GovWay", null),
	COOKIE_BASED("Cookie", "Nome"),
	HEADER_BASED("Header HTTP", "Nome"),
	URL_BASED("Url di Invocazione", "Espressione Regolare"),
	FORM_BASED("Parametro della Url", "Nome"),
	CONTENT_BASED("Contenuto","Pattern"),
	INTERFACE_BASED("Specifica di Interfaccia dell'API",null),
	SOAP_ACTION_BASED("SOAPAction",null),
	INPUT_BASED("Header di Integrazione",null),
	CONTAINER_BASED("Container",null),
	INDIRIZZO_IP_BASED("Client IP",null),
	X_FORWARD_FOR_BASED("X-Forwarded-For",null),
	TOKEN("Token","Claim"),
	PLUGIN_BASED("Plugin", "Tipo"),
	GOVWAY_TEMPLATE("Template", "Template"),
	FREEMARKER_TEMPLATE("Freemarker Template", "Template"),
	VELOCITY_TEMPLATE("Velocity Template", "Template");
	
	private String label;
	private String labelParametro;
	
	ModalitaIdentificazione(String label, String labelParametro) {
		this.label = label;
		this.labelParametro = labelParametro;
	}

	public String getLabel() {
		return this.label;
	}

	public String getLabelParametro() {
		return this.labelParametro;
	}
	
	public static List<String> getLabels(ModalitaIdentificazione ... modes){
		List<String> l = new ArrayList<>();
		for (ModalitaIdentificazione m : modes) {
			l.add(m.getLabel());
		}
		return l;
	}
	
}

