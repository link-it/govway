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

package org.openspcoop2.pdd.core.connettori;

import org.openspcoop2.utils.transport.http.SSLConfig;

/**
 * ConnettoreConnectionConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractConnettoreConnectionConfig {

	private String proxyHost;
	private Integer proxyPort;

	private Integer connectionTimeout;
	private Integer readTimeout;
	
	private boolean followRedirect = false;
	private int maxNumberRedirects = 5;
	
	private SSLConfig sslContextProperties;
	
	private boolean debug;
	
	private ConnettoreHttpPoolParams httpPoolParams;
	
	private String type;
	
	protected AbstractConnettoreConnectionConfig(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return this.toString(false);
	}
	public String toKeyConnectionManager() {
		return toString(true);
	}
	public String toString(boolean onlyForConnectionManager) {
		StringBuilder bf = new StringBuilder(this.type);
		if(onlyForConnectionManager) {
			if(this.connectionTimeout!=null) {
				bf.append(" ").append("connectionTimeout:").append(this.connectionTimeout);
			}
			if(this.httpPoolParams!=null) {
				bf.append(" ").append(this.httpPoolParams.toString());
			}
		}
		else {
			appendConfig(bf);
		}
		if(this.sslContextProperties!=null) {
			bf.append(" ");
			bf.append(this.sslContextProperties.toString(false));
			bf.append(" ");
			bf.append("debug:").append(this.debug); // serve a  WrappedLogSSLSocketFactory
		}
		return bf.toString();
	}
	private void appendConfig(StringBuilder bf) {
		
		if(this.proxyHost!=null) {
			bf.append(" ").append("proxyHost:").append(this.proxyHost);
		}
		if(this.proxyPort!=null) {
			bf.append(" ").append("proxyPort:").append(this.proxyPort);
		}
		if(this.connectionTimeout!=null) {
			bf.append(" ").append("connectionTimeout:").append(this.connectionTimeout);
		}
		if(this.readTimeout!=null) {
			bf.append(" ").append("readTimeout:").append(this.readTimeout);
		}
		if(this.followRedirect) {
			bf.append(" ").append("followRedirect max:").append(this.maxNumberRedirects);
		}
		if(this.httpPoolParams!=null) {
			bf.append(" ").append(this.httpPoolParams.toString());
		}
	}
	
	public String getProxyHost() {
		return this.proxyHost;
	}
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	public Integer getProxyPort() {
		return this.proxyPort;
	}
	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}
	public Integer getConnectionTimeout() {
		return this.connectionTimeout;
	}
	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public Integer getReadTimeout() {
		return this.readTimeout;
	}
	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}
	public SSLConfig getSslContextProperties() {
		return this.sslContextProperties;
	}
	public void setSslContextProperties(SSLConfig sslConfig) {
		this.sslContextProperties = sslConfig;
	}
	public boolean isFollowRedirect() {
		return this.followRedirect;
	}
	public void setFollowRedirect(boolean followRedirect) {
		this.followRedirect = followRedirect;
	}
	public int getMaxNumberRedirects() {
		return this.maxNumberRedirects;
	}
	public void setMaxNumberRedirects(int maxNumberRedirects) {
		this.maxNumberRedirects = maxNumberRedirects;
	}
	public boolean isDebug() {
		return this.debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public ConnettoreHttpPoolParams getHttpPoolParams() {
		return this.httpPoolParams;
	}
	public void setHttpPoolParams(ConnettoreHttpPoolParams httpPoolParams) {
		this.httpPoolParams = httpPoolParams;
	}
}
