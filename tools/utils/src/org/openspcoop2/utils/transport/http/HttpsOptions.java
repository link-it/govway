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

package org.openspcoop2.utils.transport.http;

/**
 * HttpsOptions
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpsOptions implements HttpOptions {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("hostnameVerifier").append(":").append(this.hostnameVerifier);
		bf.append(" secureRandom").append(":").append(this.secureRandom);
		if(this.secureRandom) {
			bf.append(" secureRandomAlgo").append(":").append(this.secureRandomAlgorithm);
		}
		return bf.toString();
	}
	
	@Override
	public void fill(HttpRequest request) {	
		request.setHostnameVerifier(this.hostnameVerifier);
		request.setSecureRandom(this.secureRandom);
		request.setSecureRandomAlgorithm(this.secureRandomAlgorithm);
	}
	
	private boolean secureRandom = false;
	private String secureRandomAlgorithm = null;
	
	private boolean hostnameVerifier = false;

	public boolean isSecureRandom() {
		return this.secureRandom;
	}

	public void setSecureRandom(boolean secureRandom) {
		this.secureRandom = secureRandom;
	}

	public String getSecureRandomAlgorithm() {
		return this.secureRandomAlgorithm;
	}

	public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
		this.secureRandomAlgorithm = secureRandomAlgorithm;
	}

	public boolean isHostnameVerifier() {
		return this.hostnameVerifier;
	}

	public void setHostnameVerifier(boolean hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}
	
}
