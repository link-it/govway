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

package org.openspcoop2.pdd.core.token.dpop;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.token.NegoziazioneTokenDynamicParameters;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.slf4j.Logger;

/**
 * DPoPParams - Parametri per la generazione del DPoP proof JWT
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DPoPParams {

	private PolicyNegoziazioneToken policyNegoziazioneToken;
	private String httpMethod;
	private String httpUri;
	private String accessToken; // for ath claim (backend DPoP only)
	private Busta busta;
	private RequestInfo requestInfo;
	private PdDContext pddContext;
	private Logger log;
	private NegoziazioneTokenDynamicParameters dynamicParameters; // for backend DPoP with applicativoModI/fruizioneModI keystores

	public PolicyNegoziazioneToken getPolicyNegoziazioneToken() {
		return this.policyNegoziazioneToken;
	}
	public void setPolicyNegoziazioneToken(PolicyNegoziazioneToken policyNegoziazioneToken) {
		this.policyNegoziazioneToken = policyNegoziazioneToken;
	}

	public String getHttpMethod() {
		return this.httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getHttpUri() {
		return this.httpUri;
	}
	public void setHttpUri(String httpUri) {
		this.httpUri = httpUri;
	}

	public String getAccessToken() {
		return this.accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Busta getBusta() {
		return this.busta;
	}
	public void setBusta(Busta busta) {
		this.busta = busta;
	}

	public RequestInfo getRequestInfo() {
		return this.requestInfo;
	}
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public PdDContext getPddContext() {
		return this.pddContext;
	}
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}

	public Logger getLog() {
		return this.log;
	}
	public void setLog(Logger log) {
		this.log = log;
	}

	public NegoziazioneTokenDynamicParameters getDynamicParameters() {
		return this.dynamicParameters;
	}
	public void setDynamicParameters(NegoziazioneTokenDynamicParameters dynamicParameters) {
		this.dynamicParameters = dynamicParameters;
	}
}
