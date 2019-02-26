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

package org.openspcoop2.utils.logger.beans.context.core.search;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.utils.logger.beans.BasicPaginatedSearchContext;
import org.openspcoop2.utils.logger.beans.context.core.Actor;
import org.openspcoop2.utils.logger.beans.context.core.Operation;
import org.openspcoop2.utils.logger.beans.context.core.Role;
import org.openspcoop2.utils.logger.beans.context.core.Service;

/**
 * SearchContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SearchContext extends BasicPaginatedSearchContext implements Serializable {

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
	
	private String clientName;
	private String clientPrincipal;

	private String serverName;
	
	private IdentifierGroupSearch identifierSearch;

	public String getClientName() {
		return this.clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientPrincipal() {
		return this.clientPrincipal;
	}

	public void setClientPrincipal(String clientPrincipal) {
		this.clientPrincipal = clientPrincipal;
	}

	public String getServerName() {
		return this.serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
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


}
