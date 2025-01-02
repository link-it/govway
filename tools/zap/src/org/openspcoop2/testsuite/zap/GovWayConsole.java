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
package org.openspcoop2.testsuite.zap;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.resources.Charset;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;

/**
 * OpenAPI
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GovWayConsole {

	public static void main(String[] args) throws Exception {
				
		String consoleUsage = "url username password scanTypes";
		int consoleArgs = 4;
		ZAPContext context = new ZAPContext(args, GovWayConsole.class.getName(), consoleUsage+ZAPReport.suffix);
		
		String usageMsg = ZAPContext.prefix+consoleUsage+ZAPReport.suffix;
		
		String _url = null;
		int index = ZAPContext.startArgs;
		if(args.length>(index)) {
			_url = args[index+0];
		}
		if(_url==null || StringUtils.isEmpty(_url)) {
			throw new Exception("ERROR: argument 'url' undefined"+usageMsg);
		}
		String url = _url;
		if(!url.endsWith("/")) {
			url=url+"/";
		}
		
		String username = null;
		if(args.length>(index+1)) {
			username = args[index+1];
		}
		if(username==null || StringUtils.isEmpty(username)) {
			throw new Exception("ERROR: argument 'username' undefined"+usageMsg);
		}
		
		String password = null;
		if(args.length>(index+2)) {
			password = args[index+2];
		}
		if(password==null || StringUtils.isEmpty(password)) {
			throw new Exception("ERROR: argument 'password' undefined"+usageMsg);
		}

		String scanTypes = null;
		if(args.length>(index+3)) {
			scanTypes = args[index+3];
		}
		if(scanTypes==null || StringUtils.isEmpty(scanTypes)) {
			throw new Exception("ERROR: argument 'scanTypes' undefined"+usageMsg);
		}
		List<String> scanTypesSupported = getAllScanTypes();
		//System.out.println("scanTypes: "+scanTypes);
		String [] split = scanTypes.split("\\|");
		
		boolean spider = false;
		boolean active = false;
		if(split!=null && split.length>0) {
			for (String s : split) {
				if(!scanTypesSupported.contains(s)) {
					throw new Exception("ERROR: argument 'scanTypes'='"+scanTypes+"' unknown confidence '"+s+"' ; usable: "+scanTypesSupported+usageMsg);
				}
				System.out.println("Scan '"+s+"' enabled");
				if(scan_spider.equalsIgnoreCase(s)) {
					spider=true;
				}
				else if(scan_active.equalsIgnoreCase(s)) {
					active=true;
				}
			}
		}
		
		
		
		ClientApi api = context.getClientApi();
		String contextName = context.getContextName();
		String contextId = context.getContextId();
		
		ZAPReport report = new ZAPReport(args, GovWayConsole.class.getName(), ZAPContext.prefix+" "+consoleUsage, ZAPContext.startArgs+consoleArgs, api);
		
		String loginUrl = url+"login.do";
		String logoutUrl = url+"logout.do";
		//String logoutUrl = url+"log.*.do";
		
		api.context.includeInContext(contextName, url.substring(0, (url.length()-1))+".*");
		api.context.excludeFromContext(contextName, logoutUrl);
		api.context.setContextInScope(contextName, "true");
		
		
        // Prepare the configuration in a format similar to how URL parameters are formed. This
        // means that any value we add for the configuration values has to be URL encoded.
		
		String loginRequestData = "login={%username%}&password={%password%}";
			
        StringBuilder formBasedConfig = new StringBuilder();
        formBasedConfig.append("loginUrl=").append(URLEncoder.encode(loginUrl, Charset.UTF_8.getValue()));
        formBasedConfig.append("&loginPageUrl=").append(URLEncoder.encode(loginUrl, Charset.UTF_8.getValue()));
        formBasedConfig
                .append("&loginRequestData=")
                .append(URLEncoder.encode(loginRequestData, Charset.UTF_8.getValue()));
        System.out.println(
                "Setting form based authentication configuration as: "
                        + formBasedConfig.toString());
        api.authentication.setAuthenticationMethod(
        		contextId, "formBasedAuthentication", formBasedConfig.toString());
        api.authentication.setLoggedInIndicator(contextId, "\\QProfilo:\\E");
        System.out.println(
                "Authentication config: "
                        + api.authentication.getAuthenticationMethod(contextId).toString(0));
        
        
        // Make sure we have at least one user
        ApiResponse  response = api.users.newUser(contextId, username);
        String userId = ((ApiResponseElement) response).getValue();

        // Prepare the configuration in a format similar to how URL parameters are formed. This
        // means that any value we add for the configuration values has to be URL encoded.
        StringBuilder userAuthConfig = new StringBuilder();
        userAuthConfig.append("username=").append(URLEncoder.encode(username, "UTF-8"));
        userAuthConfig.append("&password=").append(URLEncoder.encode(password, "UTF-8"));

        System.out.println(
                "Setting user authentication configuration as: " + userAuthConfig.toString());
        api.users.setAuthenticationCredentials(contextId, userId, userAuthConfig.toString());
        System.out.println(
                "Authentication credentials: "
                        + api.users.getUserById(contextId, userId).toString(0));
                
        System.out.println(
                "Session Management: "
                        + api.sessionManagement.getSessionManagementMethod(contextId).toString(0));
        
        
        String recurse = "True";
       
        if(spider) {
	        System.out.println("Spider scan: " + url);
	        
	        api.spider.setOptionParseRobotsTxt(false);
	        //api.spider.enableAllDomainsAlwaysInScope();
	        //api.spider.setOptionAcceptCookies(true);
	        //api.spider.setOptionMaxDepth(100);

	        //api.spider.setOptionSkipURLString(logoutUrl);
	        
	        // IGNORE_COMPLETELY, IGNORE_VALUE, USE_ALL
	        /*
	         * https://www.zaproxy.org/docs/desktop/addons/spider/options/#query-parameters-handling
	         * Ignore parameters completely - if www.example.org/?bar=456 is visited, then www.example.org/?foo=123 will not be visited
	         * Consider only parameter’s name (ignore parameter’s value) - if www.example.org/?foo=123 is visited, then www.example.org/?foo=456 will not be visited, but www.example.org/?bar=789 or www.example.org/?foo=456&bar=123 will be visited
	         * Consider both parameter’s name and value - if www.example.org/?123 is visited, any other uri that is different (including, for example, www.example.org/?foo=456 or www.example.org/?bar=abc) will be visited
	         */
	        api.spider.setOptionHandleODataParametersVisited(true);
	        ApiResponse resp = api.spider.optionHandleODataParametersVisited();
	        ApiResponseElement re = (ApiResponseElement) resp;
	        System.out.println(re.getName()+"="+re.getValue());
	        
	        api.spider.setOptionHandleParameters("IGNORE_VALUE");
	        resp = api.spider.optionHandleParameters();
	        System.out.println(re.getName()+"="+re.getValue());
	       
	        
	        api.forcedUser.setForcedUser(contextId, userId);
	        api.forcedUser.setForcedUserModeEnabled(true);
		       
	        //ApiResponse resp = api.spider.scan(url, "100000", recurse, contextName, "False");
	        resp = api.spider.scanAsUser(contextId, userId, url, "100000", recurse, "False");
	        
	        int progress;
	
	        // The scan now returns a scan id to support concurrent scanning
	        String scanid = ((ApiResponseElement) resp).getValue();
	
	        // Poll the status until it completes
	        while (true) {
	            Thread.sleep(1000);
	            progress =
	                    Integer.parseInt(
	                            ((ApiResponseElement) api.spider.status(scanid)).getValue());
	            System.out.println("Spider progress : " + progress + "%");
	            if (progress >= 100) {
	                break;
	            }
	        }
	        
            // Poll the number of records the passive scanner still has to scan until it completes
            while (true) {
                Thread.sleep(1000);
                progress =
                        Integer.parseInt(
                                ((ApiResponseElement) api.pscan.recordsToScan()).getValue());
                System.out.println("Passive Scan progress : " + progress + " records left");
                if (progress < 1) {
                    break;
                }
            }
	        
	        System.out.println("Spider complete");

	        System.out.println("Spider generate report ...");
	        
			for (ZAPReportTemplate rt : report.getTemplates()) {
				
				String fileScanTypeName = rt.fileName.replace("SCAN_TYPE", "active");
				
				api.reports.generate(report.getTitle(), 
						rt.template, 
						rt.theme,
						report.getDescription(),
						contextName, 
						url, 
						rt.sections,
						report.getIncludedConfidences(),
						report.getIncludedRisks(),
		        		null, 
		        		fileScanTypeName, 
		        		report.getDir(), 
		        		report.isDisplay()+"");
			}
			
			System.out.println("Spider generate report finished");
			
		}
        
        
        if(active) {
	        System.out.println("Active scan: " + url);
	        

	        api.forcedUser.setForcedUser(contextId, userId);
	        api.forcedUser.setForcedUserModeEnabled(true);
	        
	        //api.ascan.setOptionRescanInAttackMode(false);
	        
	        //api.ascan.addExcludedParam("__prevTabKey__", "Any", "*");
	        //api.ascan.addExcludedParam("__tabKey__", "Any", "*");
	        
	        //String inscopeonly = "True"; 
	        //ApiResponse resp = api.ascan.scan(url, recurse, inscopeonly, null, null, null);
	        ApiResponse resp =  api.ascan.scanAsUser(url, contextId, userId, recurse, null, null, null);
	        
	        
	        int progress;
	
	        // The scan now returns a scan id to support concurrent scanning
	        String scanid = ((ApiResponseElement) resp).getValue();
	
	        // Poll the status until it completes
	        while (true) {
	            Thread.sleep(1000);
	            progress =
	                    Integer.parseInt(
	                            ((ApiResponseElement) api.ascan.status(scanid)).getValue());
	            System.out.println("Active progress : " + progress + "%");
	            if (progress >= 100) {
	                break;
	            }
	        }
	        System.out.println("Active complete");

	        System.out.println("Active generate report ...");
	        
	        for (ZAPReportTemplate rt : report.getTemplates()) {
	        	
	        	String fileScanTypeName = rt.fileName.replace("SCAN_TYPE", "active");
	  	       	        	
				api.reports.generate(report.getTitle(), 
						rt.template, 
						rt.theme,
						report.getDescription(),
						contextName, 
						url, 
						rt.sections,
						report.getIncludedConfidences(),
						report.getIncludedRisks(),
		        		null, 
		        		fileScanTypeName, 
		        		report.getDir(), 
		        		report.isDisplay()+"");
			}
			
			System.out.println("Active generate report finished");
			
		}
		
	}
	
	public static final String FILE_REPORT_SCAN_TYPE = "SCAN_TYPE";
	public static final String scan_spider = "spider";
	public static final String scan_active = "active";
	public static List<String> getAllScanTypes(){
		List<String> l = new ArrayList<>();
		l.add(scan_spider);
		l.add(scan_active);
		return l;
	}
}
