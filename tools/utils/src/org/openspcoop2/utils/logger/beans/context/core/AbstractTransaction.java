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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.logger.constants.context.Context;
import org.openspcoop2.utils.logger.constants.context.Result;

/**
 * AbstractTransaction
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractTransaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String conversionId; // per marcare la transazione appartenente ad uno stato condiviso tra più transazioni
	
	private Result result;
	private Context context; // contesto della transazione
	
	private String domain;
	private Role role;
	
	private Actor from;
	private Actor to;
	
	private Service service;
	private Operation operation;
	
	private String clusterId;
	
	private String protocol; // indicare un protocollo applicativo a cui appartiene il messaggio
	
	private List<String> events = new ArrayList<String>(); // eventi a cui appartiene la transazione
		
	public String getConversationId() {
		return this.conversionId;
	}

	public void setConversationId(String conversionId) {
		this.conversionId = conversionId;
	}

	public Result getResult() {
		return this.result;
	}

	public void setResult(Result result) {
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

	public String getClusterId() {
		return this.clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}
	
	public Context getContext() {
		return this.context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public List<String> getEvents() {
		return this.events;
	}
	
	public void addEvent(String event){
		this.events.add(event);
	}
}
