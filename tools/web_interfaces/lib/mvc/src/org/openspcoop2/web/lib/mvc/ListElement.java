/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * ImporterInformationMissingUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ListElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String oggetto; 
	private Map<String,Object> parameters = new HashMap<String,Object>();

	public String getOggetto() {
		return this.oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public void addParameter(String key, Object object) {
		this.getParameters().put(key, object);
	}
	public Object getParameter(String key) {
		if (this.getParameters() != null)
			return this.getParameters().get(key);
		else
			return null;
	}
	public Object removeParameter(String key) {
		if (this.getParameters() != null)
			return this.getParameters().remove(key);
		else
			return null;
	}
	public Object getParameter(int index) {
		if (this.getParameters() != null) {
			Set<?> keySet = this.getParameters().keySet();
			Iterator<?> it = keySet.iterator();
			int contatore = 0;
			while (it.hasNext()) {
				String key = (String) it.next();
				if (contatore == index)
					return this.getParameters().get(key);
				contatore++;
			}
		}
		return null;
	}
	public Object removeParameter(int index) {
		if (this.getParameters() != null) {
			Set<?> keySet = this.getParameters().keySet();
			Iterator<?> it = keySet.iterator();
			int contatore = 0;
			while (it.hasNext()) {
				String key = (String) it.next();
				if (contatore == index)
					return this.getParameters().remove(key);
				contatore++;
			}
		}
		return null;
	}
	public int sizeParameter() {
		return this.getParameters().size();
	}
	// Ritorna una stringa contenente tutti i parametri codificati
	// per essere inseriti come parametri di una url. Ad esempio:
	// nome1=valore1&nome2=valore2&.....
	public String formatParametersURL() {
		StringBuilder formatParBuf = new StringBuilder();
		if (this.getParameters() != null) {
			Set<?> keySet = this.getParameters().keySet();
			Iterator<?> it = keySet.iterator();
			boolean firstEl = true;
			while (it.hasNext()) {
				String key = (String) it.next();
				if (firstEl) {
					formatParBuf.append(key)
					.append("=")
					.append(this.getParameters().get(key));
					firstEl = false;
				} else {
					formatParBuf.append("&")
					.append(key)
					.append("=")
					.append(this.getParameters().get(key));
				}
			}
		}
		return formatParBuf.toString();
	}
	
	@Deprecated
	public Map<String,Object> getParameters() {
		return this.parameters;
	}
	@Deprecated
	public void setParameters(Map<String,Object> parameters) {
		this.parameters = parameters;
	}
	
}
