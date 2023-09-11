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
package org.openspcoop2.testsuite.zap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.Charset;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * AbstractConsoleScan
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractConsoleScan {
	
	private String reportDir = null;
	
	public void scan(String[] args) throws UtilsException, ClientApiException, IOException {
				
		ZAPContext context = new ZAPContext(args, AbstractConsoleScan.class.getName(), ConsoleParams.CONSOLE_USAGE+ZAPReport.SUFFIX, false);
		
		ConsoleParams consoleParams = new ConsoleParams(args);
		
		List<String> urlVisitateSpiderScan = new ArrayList<>();
		List<String> urlVisitateActiveScan = new ArrayList<>();
		
		scan(args, context, consoleParams,
				urlVisitateSpiderScan, urlVisitateActiveScan);
		
		ConsoleUtils.writeVisitedUrlsFile(this.reportDir, ConsoleScanTypes.SCAN_TYPE_SPIDER, urlVisitateSpiderScan);
		if(!urlVisitateActiveScan.isEmpty()) {
			ConsoleUtils.writeVisitedUrlsFile(this.reportDir, ConsoleScanTypes.SCAN_TYPE_ACTIVE, urlVisitateActiveScan);
		}
	
		LoggerManager.info("Analisi complessiva terminata");
	}
	
	protected abstract String getLoginUrl(String baseUrl);
	protected abstract String getLogoutUrl(String baseUrl);
	protected abstract String getLoginRequestData();
	protected abstract String getLoggedInIndicator();
	protected abstract String getLoggedOutIndicator();
	
	private void scan(String[] args, ZAPContext context, ConsoleParams consoleParams,
			List<String> urlVisitateSpiderScan, List<String> urlVisitateActiveScan) throws UtilsException, ClientApiException, IOException {
		
		String baseUrl = consoleParams.getBaseUrl();
		String username = consoleParams.getUsername();
		String password = consoleParams.getPassword();
		ConsoleScanTypes scanTypes = consoleParams.getScanTypes();
		SortedMap<String> targetUrls = consoleParams.getScanUrls();
		
		int scanNum = 1;
				
		ConsolePathParams pathParams = new ConsolePathParams(consoleParams.getBaseConfigDirName());
		
		boolean debug = false;
		
		for (String tipoTestUrl : targetUrls.keys()) {
			
			Utilities.sleep(3000);
			
			String url = baseUrl + targetUrls.get(tipoTestUrl);
			pathParams.setTipoTestUrl(tipoTestUrl);
			pathParams.setUrl(url);
			
			LoggerManager.info("Avvio analisi "+scanNum+"/"+targetUrls.size()+" '"+tipoTestUrl+"': " + url);
			
			ZAPClienApi zapClientApi = context.newClienApi(); 
			
			ClientApi api = zapClientApi.getClientApi();
			String contextName = zapClientApi.getContextName();
			String contextId = zapClientApi.getContextId();
			
			if(consoleParams.getFalsePositives()!=null && !consoleParams.getFalsePositives().isEmpty()) {
				ConsoleFalsePositive.addFalsePositives(consoleParams.getFalsePositives(), api, contextId);
			}
			
			ZAPReport report = new ZAPReport(args, AbstractConsoleScan.class.getName(), ZAPContext.PREFIX+" "+ConsoleParams.CONSOLE_USAGE, ZAPContext.START_ARGS+consoleParams.getConsoleArgs(), api);
			if(this.reportDir==null) {
				this.reportDir = report.getDir();
			}	
			
			String loginUrl = this.getLoginUrl(baseUrl);
			String logoutUrl = this.getLogoutUrl(baseUrl);
			
			setIncludeAndExcludeInContext(api, contextName, baseUrl, logoutUrl);
			
			StringBuilder cookieValueHolder = new StringBuilder();
			String userId = setFormBasedAuthentication(api, loginUrl, contextId, 
					username, password, cookieValueHolder);
	      
			if(debug) {
				ConsoleUtils.printAllScannerActives(zapClientApi.getClientApi());
			}
			
			List<String> urlVisitateIterazioneInCorso = new ArrayList<>();
	        if(scanTypes.isSpider()) {
	        	ConsoleUtils.spider(zapClientApi, userId, 
	        			report, pathParams,
	        			urlVisitateIterazioneInCorso);
	        	ConsoleUtils.addVisitedUrls(urlVisitateIterazioneInCorso, urlVisitateSpiderScan);
	        }
			
	        if(scanTypes.isAjaxspider() && !urlVisitateIterazioneInCorso.isEmpty()) {
	        	ConsoleUtils.ajaxSpider(baseUrl, zapClientApi, username,
	        			report, pathParams,
	        			urlVisitateIterazioneInCorso);
	        }
	        
	        if(scanTypes.isActive()) {
	        	ConsoleUtils.active(baseUrl, zapClientApi, userId, 
	        			report, pathParams,
	        			urlVisitateActiveScan);
	        }
	        
			LoggerManager.info("Analisi "+scanNum+"/"+targetUrls.size()+" '"+tipoTestUrl+"' (url:"+url+") terminata");
			
			scanNum++;
		}

	}
	
	private void setIncludeAndExcludeInContext(ClientApi api, String contextName, String prefixUrl, String logoutUrl) throws ClientApiException {
		String include = prefixUrl.substring(0, (prefixUrl.length()-1))+".*";
		api.context.includeInContext(contextName, include);
		api.context.excludeFromContext(contextName, logoutUrl);
		api.context.setContextInScope(contextName, "true");
		LoggerManager.info("Include["+include+"] Exclude["+logoutUrl+"]");
	}
	
	private String setFormBasedAuthentication(ClientApi api, String loginUrl, String contextId, 
			String username, String password, StringBuilder tokenHolder) throws UnsupportedEncodingException, ClientApiException {
		
		// Prepare the configuration in a format similar to how URL parameters are formed. This
        // means that any value we add for the configuration values has to be URL encoded.
		String loginRequestData = this.getLoginRequestData();
			
        StringBuilder formBasedConfig = new StringBuilder();
        formBasedConfig.append("loginUrl=").append(URLEncoder.encode(loginUrl, Charset.UTF_8.getValue()));
        formBasedConfig.append("&loginPageUrl=").append(URLEncoder.encode(loginUrl, Charset.UTF_8.getValue()));
        formBasedConfig
                .append("&loginRequestData=")
                .append(URLEncoder.encode(loginRequestData, Charset.UTF_8.getValue()));
        LoggerManager.info(
                "Setting form based authentication configuration as: "
                        + formBasedConfig.toString());
        api.authentication.setAuthenticationMethod(
        		contextId, "formBasedAuthentication", formBasedConfig.toString());
		LoggerManager.info(
                "Authentication config: "
                        + api.authentication.getAuthenticationMethod(contextId).toString(0));
        
        // Indicazione di login/logout avvenuto
		String loggedInIndicator = this.getLoggedInIndicator();
		String loggedOutIndicator = this.getLoggedOutIndicator();
		api.authentication.setLoggedInIndicator(contextId, java.util.regex.Pattern.quote(loggedInIndicator));
		if(loggedOutIndicator!=null) {
			api.authentication.setLoggedOutIndicator(contextId, java.util.regex.Pattern.quote(loggedOutIndicator));
		}
		LoggerManager.info("Configured logged in indicator regex: "
				+ ((ApiResponseElement) api.authentication.getLoggedInIndicator(contextId)).getValue());
		ApiResponseElement reLogout = ((ApiResponseElement) api.authentication.getLoggedOutIndicator(contextId));
		LoggerManager.info("Configured logged out indicator regex: "
				+ reLogout!=null ? reLogout.getValue() : null);
        
        // Make sure we have at least one user
        ApiResponse  response = api.users.newUser(contextId, username);
        String userId = ((ApiResponseElement) response).getValue();

        // Prepare the configuration in a format similar to how URL parameters are formed. This
        // means that any value we add for the configuration values has to be URL encoded.
        StringBuilder userAuthConfig = new StringBuilder();
        userAuthConfig.append("username=").append(URLEncoder.encode(username, "UTF-8"));
        userAuthConfig.append("&password=").append(URLEncoder.encode(password, "UTF-8"));

        LoggerManager.info(
                "Setting user authentication configuration as: " + userAuthConfig.toString());
        api.users.setAuthenticationCredentials(contextId, userId, userAuthConfig.toString());
        api.users.setUserEnabled(contextId, userId, "true");
        api.forcedUser.setForcedUser(contextId, userId);
        api.forcedUser.setForcedUserModeEnabled(true);
        LoggerManager.info(
                "Authentication credentials: "
                        + api.users.getUserById(contextId, userId).toString(0));
                
        LoggerManager.info(
                "Session Management: "
                        + api.sessionManagement.getSessionManagementMethod(contextId).toString(0));
        
        // Effettuo login e registro avvenuto login
        ApiResponse resp =  api.users.authenticateAsUser(contextId, userId);
        /**LoggerManager.info(
				"RESPONSE: "+resp.getClass().getName());
        LoggerManager.info("keys: " + ((org.zaproxy.clientapi.core.ApiResponseSet)resp).getKeys());
    	LoggerManager.info("values: " + ((org.zaproxy.clientapi.core.ApiResponseSet)resp).getValues());*/
        // note, rtt, responseBody, cookieParams, requestBody, responseHeader, authSuccessful, requestHeader, id, type, timestamp, tags
        
        LoggerManager.info(
 				"authSuccessful: "+((org.zaproxy.clientapi.core.ApiResponseSet)resp).getStringValue("authSuccessful"));
        LoggerManager.info(
 				"cookieParams: "+((org.zaproxy.clientapi.core.ApiResponseSet)resp).getStringValue("cookieParams"));
        LoggerManager.info(
  				"type: "+((org.zaproxy.clientapi.core.ApiResponseSet)resp).getStringValue("type"));
        LoggerManager.info(
   			"note: "+((org.zaproxy.clientapi.core.ApiResponseSet)resp).getStringValue("note"));
        LoggerManager.info(
   			"rtt: "+((org.zaproxy.clientapi.core.ApiResponseSet)resp).getStringValue("rtt"));
        LoggerManager.info(
   			"responseBody: "+((org.zaproxy.clientapi.core.ApiResponseSet)resp).getStringValue("responseBody"));
        
        // Recupero token session value
        resp =  api.users.getAuthenticationSession(contextId, userId);
        /** LoggerManager.info(
 				"SESSIONE: "+resp.getClass().getName());*/
        List<ApiResponse> r = ((ApiResponseList) resp).getItems();
        String tokenValue = null;
        for (ApiResponse item : r) {
       	 
        	List<ApiResponse> r2 = ((ApiResponseList) item).getItems();
       	 	for (ApiResponse item2 : r2) {
       	 		/**LoggerManager.info("keys: " + ((org.zaproxy.clientapi.core.ApiResponseSet)item2).getKeys());
       		 	LoggerManager.info("values: " + ((org.zaproxy.clientapi.core.ApiResponseSet)item2).getValues());*/
       	 		for (String key : ((org.zaproxy.clientapi.core.ApiResponseSet)item2).getKeys()) {
       	 			if("value".equals(key)) {
       	 				tokenValue = ((org.zaproxy.clientapi.core.ApiResponseSet)item2).getStringValue("value");
       	 			}
       	 		}
       	 	}
        }
        LoggerManager.info("TOKEN = "+tokenValue);
        tokenHolder.append(tokenValue);
        
        return userId;
	}
	
}
