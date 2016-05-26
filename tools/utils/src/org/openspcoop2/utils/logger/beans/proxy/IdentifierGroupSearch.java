/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.utils.logger.beans.proxy;

import java.io.Serializable;

import org.openspcoop2.utils.logger.beans.IdentifierSearch;

/**
 * IdentifierGroupSearch
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentifierGroupSearch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean and = true;
	
	private IdentifierSearch clusterId;
	
	private IdentifierSearch requestIdentifier;
	
	private boolean onlyDuplicateRequest;
	
	private IdentifierSearch requestCorrelationIdentifier;
	
	private IdentifierSearch responseIdentifier;
	
	private boolean onlyDuplicateResponse;
	
	private IdentifierSearch responseCorrelationIdentifier;
	
	public boolean isAnd() {
		return this.and;
	}

	public void setAnd(boolean and) {
		this.and = and;
	}
	
	public IdentifierSearch getClusterId() {
		return this.clusterId;
	}

	public void setClusterId(IdentifierSearch clusterId) {
		this.clusterId = clusterId;
	}

	public IdentifierSearch getRequestIdentifier() {
		return this.requestIdentifier;
	}

	public void setRequestIdentifier(IdentifierSearch requestIdentifier) {
		this.requestIdentifier = requestIdentifier;
	}

	public boolean isOnlyDuplicateRequest() {
		return this.onlyDuplicateRequest;
	}

	public void setOnlyDuplicateRequest(boolean onlyDuplicateRequest) {
		this.onlyDuplicateRequest = onlyDuplicateRequest;
	}

	public IdentifierSearch getRequestCorrelationIdentifier() {
		return this.requestCorrelationIdentifier;
	}

	public void setRequestCorrelationIdentifier(IdentifierSearch requestCorrelationIdentifier) {
		this.requestCorrelationIdentifier = requestCorrelationIdentifier;
	}

	public IdentifierSearch getResponseIdentifier() {
		return this.responseIdentifier;
	}

	public void setResponseIdentifier(IdentifierSearch responseIdentifier) {
		this.responseIdentifier = responseIdentifier;
	}

	public boolean isOnlyDuplicateResponse() {
		return this.onlyDuplicateResponse;
	}

	public void setOnlyDuplicateResponse(boolean onlyDuplicateResponse) {
		this.onlyDuplicateResponse = onlyDuplicateResponse;
	}

	public IdentifierSearch getResponseCorrelationIdentifier() {
		return this.responseCorrelationIdentifier;
	}

	public void setResponseCorrelationIdentifier(IdentifierSearch responseCorrelationIdentifier) {
		this.responseCorrelationIdentifier = responseCorrelationIdentifier;
	}
}
