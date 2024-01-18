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
package org.openspcoop2.testsuite.zap;

import org.openspcoop2.utils.random.RandomUtilities;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;


/**
 * ZAPClienApi
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ZAPClienApi {

	private ClientApi clientApi;
	private String contextName;
	private String contextId;
	
	public ZAPClienApi(String address, int port, String apiKey, boolean debug) throws ClientApiException {
		
		this.clientApi = new ClientApi(address, port, apiKey, debug);
		
		this.contextName = apiKey+RandomUtilities.getRandom().nextInt();
		this.clientApi.context.newContext(this.contextName);
		ApiResponse response = this.clientApi.context.context(this.contextName);
		ApiResponseSet responseSet = (ApiResponseSet) response;
		this.contextId = ((ApiResponseElement) responseSet.getValue("id")).getValue();
		if(debug) {
			LoggerManager.info("ContextName: "+this.contextName);
			LoggerManager.info("ContextId: "+this.contextId);
		}
	}
		
	public ClientApi getClientApi() {
		return this.clientApi;
	}
	public String getContextName() {
		return this.contextName;
	}
	public String getContextId() {
		return this.contextId;
	}
}
