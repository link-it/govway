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

package org.openspcoop2.utils.certificate.byok;

import java.util.Map;

/**
 * BYOKRequestParams
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKRequestParams {
			
	private Map<String,Object> dynamicMap; // non e' serializzabile; contiene oggetti non serializzabili
	
	private String keyIdentity; // local
	
	private BYOKConfig config;
	private Map<String,String> inputMap;
	
	public Map<String, Object> getDynamicMap() {
		return this.dynamicMap;
	}
	public void setDynamicMap(Map<String, Object> dynamicMap) {
		this.dynamicMap = dynamicMap;
	}
	
	public String getKeyIdentity() {
		return this.keyIdentity;
	}
	public void setKeyIdentity(String keyIdentity) {
		this.keyIdentity = keyIdentity;
	}
	
	public BYOKConfig getConfig() {
		return this.config;
	}
	public void setConfig(BYOKConfig config) {
		this.config = config;
	}
	public Map<String, String> getInputMap() {
		return this.inputMap;
	}
	public void setInputMap(Map<String, String> inputMap) {
		this.inputMap = inputMap;
	}
	
}
