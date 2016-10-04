/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.web.lib.mvc;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * ImporterInformationMissingUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ListElement {

	private String oggetto; 
	private Hashtable<String,Object> parameters = new Hashtable<String,Object>();

	public String getOggetto() {
		return this.oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public void addParameter(String key, Object object) {
		this.parameters.put(key, object);
	}
	public Object getParameter(String key) {
		if (this.parameters != null)
			return this.parameters.get(key);
		else
			return null;
	}
	public Object removeParameter(String key) {
		if (this.parameters != null)
			return this.parameters.remove(key);
		else
			return null;
	}
	public Object getParameter(int index) {
		if (this.parameters != null) {
			Set<?> keySet = this.parameters.keySet();
			Iterator<?> it = keySet.iterator();
			int contatore = 0;
			while (it.hasNext()) {
				String key = (String) it.next();
				if (contatore == index)
					return this.parameters.get(key);
				contatore++;
			}
		}
		return null;
	}
	public Object removeParameter(int index) {
		if (this.parameters != null) {
			Set<?> keySet = this.parameters.keySet();
			Iterator<?> it = keySet.iterator();
			int contatore = 0;
			while (it.hasNext()) {
				String key = (String) it.next();
				if (contatore == index)
					return this.parameters.remove(key);
				contatore++;
			}
		}
		return null;
	}
	public int sizeParameter() {
		return this.parameters.size();
	}
	// Ritorna una stringa contenente tutti i parametri codificati
	// per essere inseriti come parametri di una url. Ad esempio:
	// nome1=valore1&nome2=valore2&.....
	public String formatParametersURL() {
		StringBuffer formatParBuf = new StringBuffer();
		if (this.parameters != null) {
			Set<?> keySet = this.parameters.keySet();
			Iterator<?> it = keySet.iterator();
			boolean firstEl = true;
			while (it.hasNext()) {
				String key = (String) it.next();
				if (firstEl) {
					formatParBuf.append(key)
					.append("=")
					.append(this.parameters.get(key));
					firstEl = false;
				} else {
					formatParBuf.append("&")
					.append(key)
					.append("=")
					.append(this.parameters.get(key));
				}
			}
		}
		return formatParBuf.toString();
	}
	
}
