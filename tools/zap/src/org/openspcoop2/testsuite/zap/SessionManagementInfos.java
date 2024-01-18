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

import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;

/**
 * OpenAPI
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SessionManagementInfos {

	/*
	 *  Open URL: http://zap/xml/sessionManagement/view/getSupportedSessionManagementMethods/?apikey=govway-test_20221201_161531084
    	Methods: 3

	 'scriptBasedSessionManagement'
		Open URL: http://zap/xml/sessionManagement/view/getSessionManagementMethodConfigParams/?apikey=govway-test_20221201_161531084&methodName=scriptBasedSessionManagement&
			params: 2
			param 1
				'name'='scriptName'
				'mandatory'='true'
			param 2
				'name'='scriptConfigParams'
				'mandatory'='false'

	 'cookieBasedSessionManagement'
		Open URL: http://zap/xml/sessionManagement/view/getSessionManagementMethodConfigParams/?apikey=govway-test_20221201_161531084&methodName=cookieBasedSessionManagement&
			params: 0

	 'httpAuthSessionManagement'
		Open URL: http://zap/xml/sessionManagement/view/getSessionManagementMethodConfigParams/?apikey=govway-test_20221201_161531084&methodName=httpAuthSessionManagement&
			params: 0

	 */
	
	public static void main(String[] args) throws Exception {
				
		ZAPContext context = new ZAPContext(args, SessionManagementInfos.class.getName(), "");
		
		LoggerManager.info("=======================");
		ApiResponse response = context.getClientApi().sessionManagement.getSupportedSessionManagementMethods();
		ApiResponseList list = ((ApiResponseList) response);
		LoggerManager.info("Methods: "+list.getItems().size());
		for (ApiResponse res : list.getItems()) {
			ApiResponseElement re = (ApiResponseElement) res;
			LoggerManager.info("\n\t '"+re.getValue()+"'");
			
			ApiResponse responseParams = context.getClientApi().sessionManagement.getSessionManagementMethodConfigParams(re.getValue());
			ApiResponseList responseParamsList = ((ApiResponseList) responseParams);
			LoggerManager.info("\tparams: "+responseParamsList.getItems().size());
			int index = 1;
			for (ApiResponse resParam : responseParamsList.getItems()) {
				ApiResponseSet reParam = (ApiResponseSet) resParam;
				LoggerManager.info("\tparam "+index);
				for (String key : reParam.getKeys()) {
					LoggerManager.info("\t\t'"+key+"'='"+reParam.getValue(key)+"'");
				}
				index++;
			}
		}
		LoggerManager.info("=======================\n\n");
		
		
	}
	
}
