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
package org.openspcoop2.utils.oauth2;

import java.io.Serializable;
import java.util.Map;

/**
 * Classe base per le risposte delle chiamate verso i servizi OAuth2.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Oauth2BaseResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String raw;
	private Map<String,Serializable> map;
	private int returnCode;
	private String error;
	private String description;
	
	public String getRaw() {
		return this.raw;
	}
	public void setRaw(String raw) {
		this.raw = raw;
	}
	public Map<String, Serializable> getMap() {
		return this.map;
	}
	public void setMap(Map<String, Serializable> map) {
		this.map = map;
	}
	public int getReturnCode() {
		return this.returnCode;
	}
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}
	public String getError() {
		return this.error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
