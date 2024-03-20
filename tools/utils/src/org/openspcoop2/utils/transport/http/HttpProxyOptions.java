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

import java.net.Proxy;

/**
 * HttpProxyOptions
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpProxyOptions implements HttpOptions {
	
	private static final long serialVersionUID = 1L;
	
	private Proxy.Type proxyType = null;
	private String proxyHostname = null;
	private int proxyPort;
	private String proxyUsername;
	private String proxyPassword;
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("Proxy (").append(this.proxyType).append(") ");
		if(this.proxyHostname!=null) {
			bf.append(this.proxyHostname).append(":").append(this.proxyPort);
		}
		if(this.proxyUsername!=null) {
			bf.append(" [auth ").append(this.proxyUsername).append(":").append(this.proxyPassword).append("]");
		}
		return bf.toString();
	}
	
	@Override
	public void fill(HttpRequest request) {	
		request.setProxyType(this.proxyType);
		request.setProxyHostname(this.proxyHostname);
		request.setProxyPort(this.proxyPort);
		request.setProxyUsername(this.proxyUsername);
		request.setProxyPassword(this.proxyPassword);
	}
		
	public Proxy.Type getProxyType() {
		return this.proxyType;
	}

	public void setProxyType(Proxy.Type proxyType) {
		this.proxyType = proxyType;
	}

	public String getProxyHostname() {
		return this.proxyHostname;
	}

	public void setProxyHostname(String proxyHostname) {
		this.proxyHostname = proxyHostname;
	}

	public int getProxyPort() {
		return this.proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return this.proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return this.proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
}
