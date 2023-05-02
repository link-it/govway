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
package org.openspcoop2.utils.certificate.remote;

import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.transport.http.ExternalResourceConfig;

/**
 * RemoteStoreConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreConfig extends ExternalResourceConfig {

	private String storeName;
	private String storeLabel;
	
	private String baseUrl;
	
	private RemoteKeyIdMode idMode;
	private String parameterName; // in caso di query o parameter
	private String keyAlgorithm = KeyUtils.ALGO_RSA;
	
	public RemoteStoreConfig(String storeName) {
		this.storeName = storeName;
		this.storeLabel = storeName; // inizialmente uguale
	}
	
	public String getStoreName() {
		return this.storeName;
	}
	
	public String getStoreLabel() {
		return this.storeLabel;
	}

	public void setStoreLabel(String storeLabel) {
		this.storeLabel = storeLabel;
	}
	
	public String getKeyAlgorithm() {
		return this.keyAlgorithm;
	}

	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	public String getBaseUrl() {
		return this.baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public RemoteKeyIdMode getIdMode() {
		return this.idMode;
	}

	public void setIdMode(RemoteKeyIdMode idMode) {
		this.idMode = idMode;
	}

	public String getParameterName() {
		return this.parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
}
