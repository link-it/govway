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

package org.openspcoop2.core.config.rs.server.api.impl;

/**
 * ApiKeyInfo
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ApiKeyInfo {

	public final static String API_KEY = "X-Api-Key";
	public final static String APP_ID = "X-App-Id";
	
	private boolean multipleApiKeys = false;
	private String appId;
	private String apiKey;
	private String password;
	private boolean cifrata = false;
	
	public boolean isCifrata() {
		return this.cifrata;
	}
	public void setCifrata(boolean cifrata) {
		this.cifrata = cifrata;
	}
	public boolean isMultipleApiKeys() {
		return this.multipleApiKeys;
	}
	public void setMultipleApiKeys(boolean multipleApiKeys) {
		this.multipleApiKeys = multipleApiKeys;
	}
	public String getAppId() {
		return this.appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getApiKey() {
		return this.apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
