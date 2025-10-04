/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import org.openspcoop2.utils.transport.http.HttpLibrary;
import org.openspcoop2.utils.transport.http.HttpLibraryConnection;

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
	private boolean httpsVerificaHostName;
	private boolean httpsAutenticazioneServer;
	private String httpsAutenticazioneServerTruststorePath;
	private String httpsAutenticazioneServerTruststoreType;
	private String httpsAutenticazioneServerTruststorePassword;
	private int connectionTimeout = 5000; /**org.openspcoop2.utils.transport.http.HttpUtilities.HTTP_CONNECTION_TIMEOUT;*/
	// Fix: uso un tempo più basso in modo da non bloccare la console
	private int readConnectionTimeout = 5000; /**org.openspcoop2.utils.transport.http.HttpUtilities.HTTP_READ_CONNECTION_TIMEOUT;*/
	private HttpLibrary httpLibrary = HttpLibraryConnection.getDefaultLibrary();
	
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

	public boolean isHttpsVerificaHostName() {
		return this.httpsVerificaHostName;
	}

	public void setHttpsVerificaHostName(boolean v) {
		this.httpsVerificaHostName = v;
	}

	public boolean isHttpsAutenticazioneServer() {
		return this.httpsAutenticazioneServer;
	}

	public void setHttpsAutenticazioneServer(boolean v) {
		this.httpsAutenticazioneServer = v;
	}

	public String getHttpsAutenticazioneServerTruststorePath() {
		return this.httpsAutenticazioneServerTruststorePath;
	}

	public void setHttpsAutenticazioneServerTruststorePath(String v) {
		this.httpsAutenticazioneServerTruststorePath = v;
	}

	public String getHttpsAutenticazioneServerTruststoreType() {
		return this.httpsAutenticazioneServerTruststoreType;
	}

	public void setHttpsAutenticazioneServerTruststoreType(String v) {
		this.httpsAutenticazioneServerTruststoreType = v;
	}

	public String getHttpsAutenticazioneServerTruststorePassword() {
		return this.httpsAutenticazioneServerTruststorePassword;
	}

	public void setHttpsAutenticazioneServerTruststorePassword(String v) {
		this.httpsAutenticazioneServerTruststorePassword = v;
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

	public HttpLibrary getHttpLibrary() {
		return this.httpLibrary;
	}

	public void setHttpLibrary(HttpLibrary httpLibrary) {
		this.httpLibrary = httpLibrary;
	}
}
