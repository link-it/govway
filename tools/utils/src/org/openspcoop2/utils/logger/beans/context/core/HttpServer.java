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
package org.openspcoop2.utils.logger.beans.context.core;

import java.io.Serializable;

import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * Server
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServer extends BaseServer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HttpRequestMethod transportRequestMethod;
	private int responseStatusCode;
	
	public HttpRequestMethod getTransportRequestMethod() {
		return this.transportRequestMethod;
	}
	public void setTransportRequestMethod(HttpRequestMethod transportRequestMethod) {
		this.transportRequestMethod = transportRequestMethod;
	}
	public int getResponseStatusCode() {
		return this.responseStatusCode;
	}
	public void setResponseStatusCode(int responseStatusCode) {
		this.responseStatusCode = responseStatusCode;
	}
	
}

