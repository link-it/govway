/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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


package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**     
 * InformazioniNegoziazioneTokenSourceTransaction
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniNegoziazioneToken_DatiRichiesta  extends org.openspcoop2.utils.beans.BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InformazioniNegoziazioneToken_DatiRichiesta() {} // per serializzatore

	private String policy;
	private String transactionId;
	private String grantType;
	private Boolean refresh;
	private String clientId;
	private String clientToken;
	private InformazioniJWTClientAssertion jwtClientAssertion;
	private String username;
	private List<String> scope;
	private String audience;
	private String resource;
	private Map<String,String> parameters = null;
	private Map<String,String> httpHeaders = null;

	private String endpoint;
	
	private Date prepareRequest;
	private Date sendRequest;
	private Date receiveResponse;
	private Date parseResponse;
	private Date processComplete;

	public String getPolicy() {
		return this.policy;
	}
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	public String getTransactionId() {
		return this.transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getGrantType() {
		return this.grantType;
	}
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}
	public Boolean getRefresh() {
		return this.refresh;
	}
	public void setRefresh(Boolean refresh) {
		this.refresh = refresh;
	}
	public String getClientId() {
		return this.clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientToken() {
		return this.clientToken;
	}
	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}
	public InformazioniJWTClientAssertion getJwtClientAssertion() {
		return this.jwtClientAssertion;
	}
	public void setJwtClientAssertion(InformazioniJWTClientAssertion jwtClientAssertion) {
		this.jwtClientAssertion = jwtClientAssertion;
	}
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<String> getScope() {
		return this.scope;
	}
	public void setScope(List<String> scope) {
		this.scope = scope;
	}
	public String getAudience() {
		return this.audience;
	}
	public void setAudience(String audience) {
		this.audience = audience;
	}
	public String getResource() {
		return this.resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public Map<String, String> getParameters() {
		return this.parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	public Map<String, String> getHttpHeaders() {
		return this.httpHeaders;
	}
	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}
	
	public String getEndpoint() {
		return this.endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public Date getPrepareRequest() {
		return this.prepareRequest;
	}
	public void setPrepareRequest(Date prepareRequest) {
		this.prepareRequest = prepareRequest;
	}
	public Date getSendRequest() {
		return this.sendRequest;
	}
	public void setSendRequest(Date sendRequest) {
		this.sendRequest = sendRequest;
	}
	public Date getReceiveResponse() {
		return this.receiveResponse;
	}
	public void setReceiveResponse(Date receiveResponse) {
		this.receiveResponse = receiveResponse;
	}
	public Date getParseResponse() {
		return this.parseResponse;
	}
	public void setParseResponse(Date parseResponse) {
		this.parseResponse = parseResponse;
	}
	public Date getProcessComplete() {
		return this.processComplete;
	}
	public void setProcessComplete(Date processComplete) {
		this.processComplete = processComplete;
	}
}
