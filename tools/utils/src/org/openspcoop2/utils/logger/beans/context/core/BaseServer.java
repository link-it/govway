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
package org.openspcoop2.utils.logger.beans.context.core;

import java.io.Serializable;

/**
 * BaseServer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BaseServer extends BaseConnection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String endpoint;
	private String endpointType;
	private String idOperation;
	private ConnectionMessage request;
	private ConnectionMessage response;
		
	public String getEndpoint() {
		return this.endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getEndpointType() {
		return this.endpointType;
	}
	public void setEndpointType(String endpointType) {
		this.endpointType = endpointType;
	}
	
	public ConnectionMessage getRequest() {
		return this.request;
	}
	public void setRequest(ConnectionMessage request) {
		this.request = request;
	}
	public ConnectionMessage getResponse() {
		return this.response;
	}
	public void setResponse(ConnectionMessage response) {
		this.response = response;
	}
	
	public String getIdOperation() {
		return this.idOperation;
	}

	public void setIdOperation(String idOperation) {
		this.idOperation = idOperation;
	}
}

