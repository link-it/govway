/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreNIO_connectionConfig {

	private String proxyHost;
	private Integer proxyPort;

	private Integer connectionTimeout;
	private Integer readTimeout;
	
	private SSLConfig sslConfig;
	
	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer("nio");
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
		if(this.sslConfig!=null) {
			bf.append(this.sslConfig.toString());
		}
		return bf.toString();
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
	public SSLConfig getSslConfig() {
		return this.sslConfig;
	}
	public void setSslConfig(SSLConfig sslConfig) {
		this.sslConfig = sslConfig;
	}
}
