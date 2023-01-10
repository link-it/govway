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

package org.openspcoop2.pdd.services.service;

import java.util.Map;

/**
 * RicezionePropertiesConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezionePropertiesConfig {

	private Map<String, String> apiImplementation;
	private Map<String, String> soggettoFruitore;
	private Map<String, String> soggettoErogatore;
	
	public Map<String, String> getApiImplementation() {
		return this.apiImplementation;
	}
	public void setApiImplementation(Map<String, String> apiImplementation) {
		this.apiImplementation = apiImplementation;
	}
	public Map<String, String> getSoggettoFruitore() {
		return this.soggettoFruitore;
	}
	public void setSoggettoFruitore(Map<String, String> soggettoFruitore) {
		this.soggettoFruitore = soggettoFruitore;
	}
	public Map<String, String> getSoggettoErogatore() {
		return this.soggettoErogatore;
	}
	public void setSoggettoErogatore(Map<String, String> soggettoErogatore) {
		this.soggettoErogatore = soggettoErogatore;
	}
}
