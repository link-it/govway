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
package org.openspcoop2.monitor.engine.status;

/**
 * PddStatus
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class GatewayStatus extends BaseStatus{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String url;
	private boolean https;
	private boolean https_verificaHostName;
	private boolean https_autenticazioneServer;
	private String https_autenticazioneServer_truststorePath;
	private String https_autenticazioneServer_truststoreType;
	private String https_autenticazioneServer_truststorePassword;
	private int connectionTimeout = 5000; //org.openspcoop2.utils.transport.http.HttpUtilities.HTTP_CONNECTION_TIMEOUT;
	// Fix: uso un tempo più basso in modo da non bloccare la console
	private int readConnectionTimeout = 5000; //org.openspcoop2.utils.transport.http.HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;
	
	public GatewayStatus(){
		super();
	}
	
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isHttps() {
		return this.https;
	}

	public void setHttps(boolean https) {
		this.https = https;
	}

	public boolean isHttps_verificaHostName() {
		return this.https_verificaHostName;
	}

	public void setHttps_verificaHostName(boolean https_verificaHostName) {
		this.https_verificaHostName = https_verificaHostName;
	}

	public boolean isHttps_autenticazioneServer() {
		return this.https_autenticazioneServer;
	}

	public void setHttps_autenticazioneServer(boolean https_autenticazioneServer) {
		this.https_autenticazioneServer = https_autenticazioneServer;
	}

	public String getHttps_autenticazioneServer_truststorePath() {
		return this.https_autenticazioneServer_truststorePath;
	}

	public void setHttps_autenticazioneServer_truststorePath(String https_autenticazioneServer_truststorePath) {
		this.https_autenticazioneServer_truststorePath = https_autenticazioneServer_truststorePath;
	}

	public String getHttps_autenticazioneServer_truststoreType() {
		return this.https_autenticazioneServer_truststoreType;
	}

	public void setHttps_autenticazioneServer_truststoreType(String https_autenticazioneServer_truststoreType) {
		this.https_autenticazioneServer_truststoreType = https_autenticazioneServer_truststoreType;
	}

	public String getHttps_autenticazioneServer_truststorePassword() {
		return this.https_autenticazioneServer_truststorePassword;
	}

	public void setHttps_autenticazioneServer_truststorePassword(String https_autenticazioneServer_truststorePassword) {
		this.https_autenticazioneServer_truststorePassword = https_autenticazioneServer_truststorePassword;
	}
	
	public int getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getReadConnectionTimeout() {
		return this.readConnectionTimeout;
	}

	public void setReadConnectionTimeout(int readConnectionTimeout) {
		this.readConnectionTimeout = readConnectionTimeout;
	}
}
