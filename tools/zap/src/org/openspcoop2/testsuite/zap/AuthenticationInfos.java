/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
public class AuthenticationInfos {

	/*
	 * Open URL: http://zap/xml/authentication/view/getSupportedAuthenticationMethods/?apikey=govway-test_20221201_161338075
        Methods: 5

	 'formBasedAuthentication'
       Open URL: http://zap/xml/authentication/view/getAuthenticationMethodConfigParams/?apikey=govway-test_20221201_161338075&authMethodName=formBasedAuthentication&
		params: 3
			param 1
				'name'='loginUrl'
				'mandatory'='true'
			param 2
				'name'='loginRequestData'
				'mandatory'='false'
			param 3
				'name'='loginPageUrl'
				'mandatory'='false'

	 'scriptBasedAuthentication'
		Open URL: http://zap/xml/authentication/view/getAuthenticationMethodConfigParams/?apikey=govway-test_20221201_161338075&authMethodName=scriptBasedAuthentication&
			params: 2
			param 1
				'name'='scriptName'
				'mandatory'='true'
			param 2
				'name'='scriptConfigParams'
				'mandatory'='false'

	 'httpAuthentication'
		Open URL: http://zap/xml/authentication/view/getAuthenticationMethodConfigParams/?apikey=govway-test_20221201_161338075&authMethodName=httpAuthentication&
			params: 3
			param 1
				'name'='hostname'
				'mandatory'='true'
			param 2
				'name'='realm'
				'mandatory'='false'
			param 3
				'name'='port'
				'mandatory'='false'

	 'manualAuthentication'
		Open URL: http://zap/xml/authentication/view/getAuthenticationMethodConfigParams/?apikey=govway-test_20221201_161338075&authMethodName=manualAuthentication&
			params: 0

	 'jsonBasedAuthentication'
		Open URL: http://zap/xml/authentication/view/getAuthenticationMethodConfigParams/?apikey=govway-test_20221201_161338075&authMethodName=jsonBasedAuthentication&
			params: 3
			param 1
				'name'='loginUrl'
				'mandatory'='true'
			param 2
				'name'='loginRequestData'
				'mandatory'='true'
			param 3
				'name'='loginPageUrl'
				'mandatory'='false'

	 */
	
	public static void main(String[] args) throws Exception {
				
		ZAPContext context = new ZAPContext(args, AuthenticationInfos.class.getName(), "");
		
		System.out.println("=======================");
		ApiResponse response = context.getClientApi().authentication.getSupportedAuthenticationMethods();
		ApiResponseList list = ((ApiResponseList) response);
		System.out.println("Methods: "+list.getItems().size());
		for (ApiResponse res : list.getItems()) {
			ApiResponseElement re = (ApiResponseElement) res;
			System.out.println("\n\t '"+re.getValue()+"'");
			
			ApiResponse responseParams = context.getClientApi().authentication.getAuthenticationMethodConfigParams(re.getValue());
			ApiResponseList responseParamsList = ((ApiResponseList) responseParams);
			System.out.println("\tparams: "+responseParamsList.getItems().size());
			int index = 1;
			for (ApiResponse resParam : responseParamsList.getItems()) {
				ApiResponseSet reParam = (ApiResponseSet) resParam;
				System.out.println("\tparam "+index);
				for (String key : reParam.getKeys()) {
					System.out.println("\t\t'"+key+"'='"+reParam.getValue(key)+"'");
				}
				index++;
			}
		}
		System.out.println("=======================\n\n");
		
		
	}
	
}
