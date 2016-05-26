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
import java.util.Date;

import org.openspcoop2.utils.logger.beans.BasicPaginatedSearchContext;

/**
 * ProxySearchContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProxySearchContext extends BasicPaginatedSearchContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date leftIntervalDate;
	private Date rightIntervalDate;
	
	private String state;
	
	private ResultSearch result;
	
	private String domain;
	private Role role;
	
	private Actor from;
	private Actor to;
	
	private Service service;
	private Operation operation;
	
	private BaseClient client;

	private BaseServer server;
	
	private IdentifierGroupSearch identifierSearch;

	
	public IdentifierGroupSearch getIdentifierSearch() {
		return this.identifierSearch;
	}

	public void setIdentifierSearch(IdentifierGroupSearch identifierSearch) {
		this.identifierSearch = identifierSearch;
	}

	public Date getLeftIntervalDate() {
		return this.leftIntervalDate;
	}

	public void setLeftIntervalDate(Date leftIntervalDate) {
		this.leftIntervalDate = leftIntervalDate;
	}

	public Date getRightIntervalDate() {
		return this.rightIntervalDate;
	}

	public void setRightIntervalDate(Date rightIntervalDate) {
		this.rightIntervalDate = rightIntervalDate;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public ResultSearch getResult() {
		return this.result;
	}

	public void setResult(ResultSearch result) {
		this.result = result;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Actor getFrom() {
		return this.from;
	}

	public void setFrom(Actor from) {
		this.from = from;
	}

	public Actor getTo() {
		return this.to;
	}

	public void setTo(Actor to) {
		this.to = to;
	}

	public Service getService() {
		return this.service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Operation getOperation() {
		return this.operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public BaseClient getClient() {
		return this.client;
	}

	public void setClient(BaseClient client) {
		this.client = client;
	}

	public BaseServer getServer() {
		return this.server;
	}

	public void setServer(BaseServer server) {
		this.server = server;
	}



}
