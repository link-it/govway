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
 * HttpForwardProxyOptions
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpForwardProxyOptions implements HttpOptions {

	private static final long serialVersionUID = 1L;
	
	private String forwardProxyEndpoint;
	private HttpForwardProxyConfig forwardProxyConfig;
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		bf.append("ForwardProxy (").append(this.forwardProxyEndpoint).append(") ");
		if(this.forwardProxyConfig!=null) {
			bf.append(this.forwardProxyConfig.toString(" "));
		}
		return bf.toString();
	}
	
	@Override
	public void fill(HttpRequest request) {
		request.setForwardProxyEndpoint(this.forwardProxyEndpoint);
		request.setForwardProxyConfig(this.forwardProxyConfig);
	}
	
	public String getForwardProxyEndpoint() {
		return this.forwardProxyEndpoint;
	}

	public void setForwardProxyEndpoint(String forwardProxyEndpoint) {
		this.forwardProxyEndpoint = forwardProxyEndpoint;
	}
	
	public HttpForwardProxyConfig getForwardProxyConfig() {
		return this.forwardProxyConfig;
	}

	public void setForwardProxyConfig(HttpForwardProxyConfig forwardProxyConfig) {
		this.forwardProxyConfig = forwardProxyConfig;
	}
}
