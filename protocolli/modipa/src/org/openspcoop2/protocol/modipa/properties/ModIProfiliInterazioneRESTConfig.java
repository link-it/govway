/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.properties;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**	
 * ModIProfiliInterazioneRESTConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIProfiliInterazioneRESTConfig {

	private HttpRequestMethod httpRequest = null;
	
	private List<HttpRequestMethod> supportedHttpMethodBloccante = null;
	private List<HttpRequestMethod> supportedHttpMethodNonBloccantePush = null;
	private List<HttpRequestMethod> supportedHttpMethodNonBloccantePushRequest = null;
	private List<HttpRequestMethod> supportedHttpMethodNonBloccantePushResponse = null;
	private List<HttpRequestMethod> supportedHttpMethodNonBloccantePull = null;
	private List<HttpRequestMethod> supportedHttpMethodNonBloccantePullRequest = null;
	private List<HttpRequestMethod> supportedHttpMethodNonBloccantePullRequestState = null;
	private List<HttpRequestMethod> supportedHttpMethodNonBloccantePullResponse = null;
	
	boolean compatibileBloccante = true;
	boolean compatibileNonBloccantePush = true;
	boolean compatibileNonBloccantePushRequest = true;
	boolean compatibileNonBloccantePushResponse = true;
	boolean compatibileNonBloccantePull = true;
	boolean compatibileNonBloccantePullRequest = true;
	boolean compatibileNonBloccantePullRequestState = true;
	boolean compatibileNonBloccantePullResponse = true;
	
	public ModIProfiliInterazioneRESTConfig(ModIProperties modiProperties, String httpMethod, Resource resource) throws ProtocolException{
		
		if(!modiProperties.isRestProfiliInterazioneCheckCompatibility()) {
			return;
		}
		
		try {
			if(httpMethod!=null && !StringUtils.isEmpty(httpMethod)) {
				this.httpRequest = HttpRequestMethod.valueOf(httpMethod);
			}
		}catch(Throwable t) { // qualsiasi }
		}
		
		this.supportedHttpMethodBloccante = modiProperties.getRestBloccanteHttpMethod();
		if(this.supportedHttpMethodBloccante!=null && !this.supportedHttpMethodBloccante.isEmpty()) {
			this.compatibileBloccante = this.httpRequest!=null && this.supportedHttpMethodBloccante.contains(this.httpRequest);
		}
		
		this.supportedHttpMethodNonBloccantePush = modiProperties.getRestNonBloccantePushHttpMethod();
		if(this.supportedHttpMethodNonBloccantePush!=null && !this.supportedHttpMethodNonBloccantePush.isEmpty()) {
			this.compatibileNonBloccantePush = this.httpRequest!=null && this.supportedHttpMethodNonBloccantePush.contains(this.httpRequest);
		}
		this.supportedHttpMethodNonBloccantePushRequest = modiProperties.getRestNonBloccantePushRequestHttpMethod();
		if(this.supportedHttpMethodNonBloccantePushRequest!=null && !this.supportedHttpMethodNonBloccantePushRequest.isEmpty()) {
			this.compatibileNonBloccantePushRequest = this.httpRequest!=null && this.supportedHttpMethodNonBloccantePushRequest.contains(this.httpRequest);
		}
		this.supportedHttpMethodNonBloccantePushResponse = modiProperties.getRestNonBloccantePushResponseHttpMethod();
		if(this.supportedHttpMethodNonBloccantePushResponse!=null && !this.supportedHttpMethodNonBloccantePushResponse.isEmpty()) {
			this.compatibileNonBloccantePushResponse = this.httpRequest!=null && this.supportedHttpMethodNonBloccantePushResponse.contains(this.httpRequest);
		}
	
		this.supportedHttpMethodNonBloccantePull = modiProperties.getRestNonBloccantePullHttpMethod();
		if(this.supportedHttpMethodNonBloccantePull!=null && !this.supportedHttpMethodNonBloccantePull.isEmpty()) {
			this.compatibileNonBloccantePull = this.httpRequest!=null && this.supportedHttpMethodNonBloccantePull.contains(this.httpRequest);
		}
		this.supportedHttpMethodNonBloccantePullRequest = modiProperties.getRestNonBloccantePullRequestHttpMethod();
		if(this.supportedHttpMethodNonBloccantePullRequest!=null && !this.supportedHttpMethodNonBloccantePullRequest.isEmpty()) {
			this.compatibileNonBloccantePullRequest = this.httpRequest!=null && this.supportedHttpMethodNonBloccantePullRequest.contains(this.httpRequest);
		}
		this.supportedHttpMethodNonBloccantePullRequestState = modiProperties.getRestNonBloccantePullRequestStateHttpMethod();
		if(this.supportedHttpMethodNonBloccantePullRequestState!=null && !this.supportedHttpMethodNonBloccantePullRequestState.isEmpty()) {
			this.compatibileNonBloccantePullRequestState = this.httpRequest!=null && this.supportedHttpMethodNonBloccantePullRequestState.contains(this.httpRequest);
		}
		this.supportedHttpMethodNonBloccantePullResponse = modiProperties.getRestNonBloccantePullResponseHttpMethod();
		if(this.supportedHttpMethodNonBloccantePullResponse!=null && !this.supportedHttpMethodNonBloccantePullResponse.isEmpty()) {
			this.compatibileNonBloccantePullResponse = this.httpRequest!=null && this.supportedHttpMethodNonBloccantePullResponse.contains(this.httpRequest);
		}
		
		List<Integer> resourceHttpCodes = new ArrayList<Integer>();
		boolean checkHttpCodes = false;
		if(resource!=null && resource.sizeResponseList()>0) {
			checkHttpCodes = true; // caricato via interfaccia o comunque definite le risposte a mano
			for (ResourceResponse response : resource.getResponseList()) {
				if(response.getStatus()>0) {
					resourceHttpCodes.add(response.getStatus());
				}
			}
		}
		if(checkHttpCodes) {
			if(this.compatibileBloccante) {
				this.compatibileBloccante = isCompatible(resourceHttpCodes, modiProperties.getRestBloccanteHttpStatus());
			}
			if(this.compatibileNonBloccantePush) {
				if(this.compatibileNonBloccantePushRequest) {
					this.compatibileNonBloccantePushRequest = isCompatible(resourceHttpCodes, modiProperties.getRestNonBloccantePushRequestHttpStatus());
				}
				if(this.compatibileNonBloccantePushResponse) {
					this.compatibileNonBloccantePushResponse = isCompatible(resourceHttpCodes, modiProperties.getRestNonBloccantePushResponseHttpStatus());
				}
				if(!this.compatibileNonBloccantePushRequest && !this.compatibileNonBloccantePushResponse) {
					this.compatibileNonBloccantePush = false;
				}
			}
			if(this.compatibileNonBloccantePull) {
				if(this.compatibileNonBloccantePullRequest) {
					this.compatibileNonBloccantePullRequest = isCompatible(resourceHttpCodes, modiProperties.getRestNonBloccantePullRequestHttpStatus());
				}
				if(this.compatibileNonBloccantePullRequestState) {
					Integer [] ok = modiProperties.getRestNonBloccantePullRequestStateOkHttpStatus();
					Integer [] notReady = modiProperties.getRestNonBloccantePullRequestStateNotReadyHttpStatus();
					List<Integer> list = new ArrayList<Integer>();
					if(ok!=null && ok.length>0) {
						for (Integer v : ok) {
							list.add(v);
						}
					}
					if(notReady!=null && notReady.length>0) {
						for (Integer v : notReady) {
							list.add(v);
						}
					}
					this.compatibileNonBloccantePullRequestState = isCompatible(resourceHttpCodes, (!list.isEmpty()) ? list.toArray(new Integer[1]) : null );
				}
				if(this.compatibileNonBloccantePullResponse) {
					this.compatibileNonBloccantePullResponse = isCompatible(resourceHttpCodes, modiProperties.getRestNonBloccantePullResponseHttpStatus());
				}
				if(!this.compatibileNonBloccantePullRequest && !this.compatibileNonBloccantePullRequestState && !this.compatibileNonBloccantePullResponse) {
					this.compatibileNonBloccantePull = false;
				}
			}
		}
	}
	
	private static boolean isCompatible(List<Integer> resourceHttpCodes, Integer [] returnCodeAttesi) {
		if(resourceHttpCodes.isEmpty()) {
			return false;
		}
		else {
			if(returnCodeAttesi!=null) {
				boolean found = false;
				for (Integer integer : returnCodeAttesi) {
					if(integer.intValue() == ModICostanti.MODIPA_PROFILO_INTERAZIONE_HTTP_CODE_2XX_INT_VALUE) {
						for (Integer resourceHttpCode : resourceHttpCodes) {
							if((resourceHttpCode.intValue() >= 200) && (resourceHttpCode.intValue()<=299) ) {
								found = true;
								break;
							}
						}
						if(found) {
							break;
						}
					}
					else {
						for (Integer resourceHttpCode : resourceHttpCodes) {
							if( resourceHttpCode.intValue() == integer.intValue() ) {
								found = true;
								break;
							}
						}
						if(found) {
							break;
						}
					}
				}
				return found;
			}
		}
		
		return true;
	}
	
	public boolean isCompatibileBloccante() {
		return this.compatibileBloccante;
	}

	public boolean isCompatibileNonBloccantePush() {
		return this.compatibileNonBloccantePush;
	}

	public boolean isCompatibileNonBloccantePushRequest() {
		return this.compatibileNonBloccantePushRequest;
	}

	public boolean isCompatibileNonBloccantePushResponse() {
		return this.compatibileNonBloccantePushResponse;
	}

	public boolean isCompatibileNonBloccantePull() {
		return this.compatibileNonBloccantePull;
	}

	public boolean isCompatibileNonBloccantePullRequest() {
		return this.compatibileNonBloccantePullRequest;
	}

	public boolean isCompatibileNonBloccantePullRequestState() {
		return this.compatibileNonBloccantePullRequestState;
	}

	public boolean isCompatibileNonBloccantePullResponse() {
		return this.compatibileNonBloccantePullResponse;
	}
}
