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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ehcache.shadow.org.terracotta.utilities.io.Files;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * ConsoleUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConsoleUtils {
	
	private ConsoleUtils(){}

	public static String getScanId(ApiResponse resp) {
		return ((ApiResponseElement) resp).getValue();
	}
	
	
	// ** SPIDER ** 
	
	private static void waitSpiderScanInProgress(ClientApi api, String ... scanIds) throws NumberFormatException, ClientApiException {
		
		// Attende il completamento di tutti gli scan
		boolean allScansCompleted = false;
		while (!allScansCompleted) {
			Utilities.sleep(1000); // Attendere per un secondo
			allScansCompleted = true;
			for (String scanId : scanIds) {

				int progress =
						Integer.parseInt(
								((ApiResponseElement) api.spider.status(scanId)).getValue());
				LoggerManager.info("Spider progress (id:"+scanId+") : " + progress + "%");

				if (progress < 100) {
					allScansCompleted = false;
				}

			}
		}
		
		LoggerManager.info("Spider complete");
	}
	
	private static void waitPassiveScanInProgress(ClientApi api) throws NumberFormatException, ClientApiException {
		
		// Poll the number of records the passive scanner still has to scan until it completes
		while (true) {
			Utilities.sleep(1000);
			int progress =
					Integer.parseInt(
							((ApiResponseElement) api.pscan.recordsToScan()).getValue());
			LoggerManager.info("Passive Scan progress : " + progress + " records left");
			if (progress < 1) {
				break;
			}
		}
		
		LoggerManager.info("Passive Scan complete");
	}
	
	public static void spider(ZAPClienApi zapClientApi, String userId, 
			ZAPReport report, ConsolePathParams pathParams,
			List<String> urlVisitateSpiderScan) throws ClientApiException, UtilsException {
        		
		ClientApi api = zapClientApi.getClientApi();
		String contextName = zapClientApi.getContextName();
		String contextId = zapClientApi.getContextId();
				
		api.spider.setOptionParseRobotsTxt(false);
        
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
        LoggerManager.info(re.getName()+"="+re.getValue());
        api.spider.setOptionHandleParameters("IGNORE_VALUE");
        api.spider.optionHandleParameters();
        LoggerManager.info(re.getName()+"="+re.getValue());
		       
        LoggerManager.info("Spider ...");
        
        resp = api.spider.scanAsUser(contextId, userId, pathParams.getUrl(), "100000", ConsoleParams.RECURSE_ENABLED, ConsoleParams.SUBTREE_ONLY_DISABLED);
        	
        String scanid = ConsoleUtils.getScanId(resp);
        	
        ConsoleUtils.waitSpiderScanInProgress(api, scanid);
        	
        ConsoleUtils.waitPassiveScanInProgress(api);
        	
        ConsoleUtils.generateReport(ConsoleScanTypes.SCAN_TYPE_SPIDER, api, contextName, report, pathParams);
    
        /**ConsoleUtils.addVisitedUrls(api, urlVisitateSpiderScan);*/
        ConsoleUtils.addVisitedUrlsBySpider(api, urlVisitateSpiderScan, scanid);
	}
	
	
	
	
	// ** AJAX SPIDER **
	
	public static void ajaxSpider(String baseUrl, ZAPClienApi zapClientApi, String username,
			ZAPReport report, ConsolePathParams pathParams,
			List<String> urlVisitateIterazioneInCorso) throws ClientApiException, UtilsException {
		
		ClientApi api = zapClientApi.getClientApi();
		String contextName = zapClientApi.getContextName();
		
		int indexUrl = 0;
		
		api.ajaxSpider.setOptionClickDefaultElems(true); // valore di default
		ApiResponse resp = api.ajaxSpider.optionClickDefaultElems();
        ApiResponseElement re2 = (ApiResponseElement) resp;
        LoggerManager.info("optionClickDefaultElems:"+re2.getName()+"="+re2.getValue());
        
    	api.ajaxSpider.setOptionClickElemsOnce(true); // valore di default
    	resp = api.ajaxSpider.optionClickElemsOnce();
        re2 = (ApiResponseElement) resp;
        LoggerManager.info("optionClickElemsOnce:"+re2.getName()+"="+re2.getValue());
        
    	api.ajaxSpider.setOptionRandomInputs(true); // valore di default
		resp = api.ajaxSpider.optionRandomInputs();
        re2 = (ApiResponseElement) resp;
        LoggerManager.info("optionRandomInputs:"+re2.getName()+"="+re2.getValue());
        
    	api.ajaxSpider.setOptionMaxCrawlStates(10); // il default è 0, cioè infinito https://www.zaproxy.org/docs/desktop/addons/ajax-spider/options/
    	resp = api.ajaxSpider.optionMaxCrawlStates();
        re2 = (ApiResponseElement) resp;
        LoggerManager.info("optionMaxCrawlStates:"+re2.getName()+"="+re2.getValue());
        
        api.ajaxSpider.setOptionMaxCrawlDepth(10); // valore di default
		resp = api.ajaxSpider.optionMaxCrawlDepth();
        re2 = (ApiResponseElement) resp;
        LoggerManager.info("optionMaxCrawlDepth:"+re2.getName()+"="+re2.getValue());
        
        api.ajaxSpider.setOptionMaxDuration(2); // minuti, il default è 60 minuti
		resp = api.ajaxSpider.optionMaxDuration();
        re2 = (ApiResponseElement) resp;
        LoggerManager.info("optionMaxDuration:"+re2.getName()+"="+re2.getValue());
		
        resp = api.ajaxSpider.excludedElements(contextName);
        LoggerManager.info("excludedElements");
        print(resp);
		                 
    	int urls = urlVisitateIterazioneInCorso.size();
    	    	
    	for (String urlSpider : urlVisitateIterazioneInCorso) {
	        indexUrl++;
	        
	        String logPrefix = "("+indexUrl+"/"+urls+") Analizy url '"+urlSpider+"' con AjaxSpider ";
	        
    		LoggerManager.info(logPrefix+"...");
    		
    		if(!urlSpider.startsWith(baseUrl)) {
    			// escludo: http://127.0.0.1:8080
    			continue;
    		}
    		            		
			api.ajaxSpider.scanAsUser(contextName, username, urlSpider, ConsoleParams.SUBTREE_ONLY_ENABLED);
    		/**api.ajaxSpider.scan(urlSpider, ConsoleParams.INSCOPE_ONLY_ENABLED, contextName, ConsoleParams.SUBTREE_ONLY_ENABLED);*/
			
	        // Poll the status until it completes
            long startTime = System.currentTimeMillis();
            long timeout = 60000;
	        String status = null;
	        boolean run = true;
	        while (run) {
	        	Utilities.sleep(1000);
	            status = (((ApiResponseElement) api.ajaxSpider.status()).getValue());
	            LoggerManager.info(logPrefix+"status : " + status);
	            if ( "stopped".equals(status) ) {
	            	LoggerManager.info(logPrefix+"complete (STOP)");
	            	run = false;
	            	continue;
                }
	            LoggerManager.info("("+indexUrl+"/"+urls+") TIMEOUT ("+(System.currentTimeMillis() - startTime)+") < "+timeout+"_timeout ??? ");
	            if ( (System.currentTimeMillis() - startTime) > timeout) {
	            	ApiResponse stopResponse = api.ajaxSpider.stop();
	            	LoggerManager.info(stopResponse.getName()+"="+stopResponse.getClass().getName());
	            	org.zaproxy.clientapi.core.ApiResponseElement re = (org.zaproxy.clientapi.core.ApiResponseElement) stopResponse;
	            	LoggerManager.info(re.getName()+"="+re.getValue());
	            	LoggerManager.info(logPrefix+"complete (TIMEOUT)");
	            	Utilities.sleep(5000);
	            	run = false;
                }
	            
	            ApiResponseSet rSet = (ApiResponseSet) api.ajaxSpider.fullResults();
	            LoggerManager.info("inScope ---------");
	            print(rSet.getValue("inScope"));
	            LoggerManager.info("outOfScope ---------");
	            print(rSet.getValue("outOfScope"));
	            LoggerManager.info("ERROR ---------");
	            print(rSet.getValue("errors"));

	        }
		}
        LoggerManager.info("AjaxSpider complete");
        
        
        
        ConsoleUtils.generateReport(ConsoleScanTypes.SCAN_TYPE_AJAXSPIDER, api, contextName, report, pathParams);
	}
	
	
	
	
	// ** ACTIVE **
	
	private static void waitActiveScanInProgress(ClientApi api, String ... scanIds) throws NumberFormatException, ClientApiException {
		
		int maxSameProgress = 20;
		Map<String, Integer> mapScanProgress = new HashMap<>();
		List<String> scanIdSkipTimeout = new ArrayList<>();
				
		// Attende il completamento di tutti gli scan
		boolean allScansCompleted = false;
		while (!allScansCompleted) {
			Utilities.sleep(1000); // Attendere per un secondo
			allScansCompleted = true;
			
			for (String scanId : scanIds) {

				if(scanIdSkipTimeout.contains(scanId)) {
					continue;
				}
								
				int progress =
						Integer.parseInt(
								((ApiResponseElement) api.ascan.status(scanId)).getValue());
				LoggerManager.info("Active scan progress (id:"+scanId+") : " + progress + "%");

				int actual = readActualActiveProgress(scanId, progress, mapScanProgress, maxSameProgress);				
				if(actual==maxSameProgress) {
					LoggerManager.info("ERROR; max same progress timed out");
					scanIdSkipTimeout.add(scanId);
				}
				
				print(api.ascan.attackModeQueue());
				
				if (progress < 100) {
					allScansCompleted = false;
				}

			}
		}
		
		LoggerManager.info("Active complete");
	}
	private static int readActualActiveProgress(String scanId, int progress, Map<String, Integer> mapScanProgress, int maxSameProgress) {
		String idMap = scanId + "_"+ progress;
		Integer actual = mapScanProgress.remove(idMap);
		if(actual!=null) {
			actual = actual.intValue()+1;
		}
		else {
			actual=1;
		}
		mapScanProgress.put(idMap, actual);
		LoggerManager.info("Active check ["+idMap+"] "+actual+"/"+maxSameProgress+ " ...");
		return actual.intValue();
	}
	
	public static void active(String baseUrl, ZAPClienApi zapClientApi, String userId, 
			ZAPReport report, ConsolePathParams pathParams,
			List<String> urlVisitateActiveScan) throws ClientApiException, UtilsException {
        		
		ClientApi api = zapClientApi.getClientApi();
		String contextName = zapClientApi.getContextName();
		String contextId = zapClientApi.getContextId();
		
		/**ApiResponse res = api.ascan.excludedParamTypes();
		print(res);*/
		// types: QueryString, PostData, URLPath, Header, Cookie, JSON, 'GraphQL Inline Arguments', 
		//        'Parameter (non-file) (Multipart Form-Data)', 'File Name (Multipart Form-Data)', 'File Content (Multipart Form-Data)', 'Content-Type (Multipart Form-Data)'
		api.ascan.addExcludedParam("__prevTabKey__", "QueryString", baseUrl);
		
		/**~/.ZAP/policies/Default\ Policy.policy
		Se si carica un file .policy differente ? e si aumenta/diminuisce la strenth 

        // Raise less reliable alert (that is, prone to false positives) when in LOW alert threshold
        // Expected values: "LOW", "MEDIUM", "HIGH"
        if (as.getAlertThreshold() == "LOW") {
                // ...
        }
        
        // Do more tests in HIGH attack strength
        // Expected values: "LOW", "MEDIUM", "HIGH", "INSANE"
        if (as.getAttackStrength() == "HIGH") {
                // ...
        }
        TOOLS_ZAP/ZAP_2.13.0/scripts/templates/active/Active\ default\ template.js
        */
				
		LoggerManager.info("Active ...");
        
		/**ApiResponse resp = api.ascan.scan(url, ConsoleParams.RECURSE_ENABLED, ConsoleParams.INSCOPE_ONLY_ENABLED, null, null, null);*/
        ApiResponse resp = api.ascan.scanAsUser(pathParams.getUrl(), contextId, userId, ConsoleParams.RECURSE_ENABLED, null, null, null);
		
        String scanid = ConsoleUtils.getScanId(resp);
        	
        ConsoleUtils.waitActiveScanInProgress(api, scanid);
        	        	
        ConsoleUtils.generateReport(ConsoleScanTypes.SCAN_TYPE_ACTIVE, api, contextName, report, 
        		pathParams);
    
        ConsoleUtils.addVisitedUrls(api, urlVisitateActiveScan);

	}
	
	
	
	
	
	// ** REPORTS ** 
	
	private static void generateReport(String type, ClientApi api, String contextName, ZAPReport report, ConsolePathParams pathParams) throws ClientApiException, UtilsException {
        LoggerManager.info("Generate '"+type+"' report ...");
        
		File dirConfig = new File(report.getDir(), pathParams.getBaseConfigDirName());
		FileSystemUtilities.mkdir(dirConfig);
        
		for (ZAPReportTemplate rt : report.getTemplates()) {
						
			File dir = new File(dirConfig, pathParams.getTipoTestUrl());
			FileSystemUtilities.mkdir(dir);
			
			String fileScanTypeName = rt.fileName.replace(ConsoleScanTypes.SCAN_TYPE, type);
			
			api.reports.generate(report.getTitle(), 
					rt.template, 
					rt.theme,
					report.getDescription(),
					contextName, 
					pathParams.getUrl(),
					rt.sections,
					report.getIncludedConfidences(),
					report.getIncludedRisks(),
	        		null, 
	        		fileScanTypeName, 
	        		dir.getAbsolutePath(), 
	        		report.isDisplay()+"");
		}
		
		LoggerManager.info("Generation '"+type+"' report finished");
	}
	
	
	
	
	
	// ** VISITED URLS ** 
	
	private static void addVisitedUrls(ClientApi api, List<String> urlVisitate) throws ClientApiException {
        ApiResponse response1 = api.core.urls();

		List<ApiResponse> items = ((ApiResponseList) response1).getItems();

		// Stampa tutte le URL visitate
		for (ApiResponse item : items) {
			String urlVisitata = ((ApiResponseElement)item).getValue();
			LoggerManager.info("--- URL visitata: " + urlVisitata);
			if(!urlVisitate.contains(urlVisitata)) {
				urlVisitate.add(urlVisitata);
			}
		}
	}
	private static void addVisitedUrlsBySpider(ClientApi api, List<String> urlVisitate, String scanId) throws ClientApiException {
		List<ApiResponse> spiderResults = ((ApiResponseList) api.spider.results(scanId)).getItems();
		for (ApiResponse apiResponse : spiderResults) {
			if(apiResponse instanceof org.zaproxy.clientapi.core.ApiResponseElement apiresponseelement) {
				String urlSpider = apiresponseelement.getValue();
				if("url".equals(apiresponseelement.getName()) && !urlVisitate.contains(urlSpider)) {
					LoggerManager.info("Spider --- URL visitata: " + urlSpider);
					if(!urlVisitate.contains(urlSpider)) {
						urlVisitate.add(urlSpider);
					}
				}
			}
		}
	}
	public static void addVisitedUrls(List<String> urlVisitate, List<String> urlVisitateGlobali) {
		if(!urlVisitate.isEmpty()) {
			for (String url : urlVisitate) {
				if(!urlVisitateGlobali.contains(url)) {
					urlVisitateGlobali.add(url);
				}
			}
		}
	}
	
	public static void writeVisitedUrlsFile(String reportDir, String type, List<String> urlVisitate) throws UtilsException {
		File f = new File(reportDir,type+"_urlVisitate.txt");
		try {
			Files.delete(f.toPath());
		}catch(Exception e) {
			// ignore
		}
		StringBuilder sb = new StringBuilder();
		Collections.sort(urlVisitate);
		for (String urlVisitata : urlVisitate) {
			if(sb.length()>0) {
				sb.append("\n");
			}
			sb.append(urlVisitata);
		}
		FileSystemUtilities.writeFile(f, sb.toString().getBytes());
		LoggerManager.info("Scan '"+type+"'; URL visitate scritte in file: " + f.getAbsolutePath());
	}

	
	
	
	// ** UTILS **
	
	public static void printAllScannerActives(ClientApi api) throws ClientApiException {
		LoggerManager.info("PASSIVE: ");
		print(api.pscan.scanners());
		
		LoggerManager.info("ACTIVE: ");
		print(api.ascan.scanPolicyNames());
	}
	
	public static void print(ApiResponse res) {
		if(res instanceof ApiResponseList reslist) {
			LoggerManager.info("ApiResponseList; size:"+reslist.getItems().size());
	        int index = 0;
			for (ApiResponse apiResponse : reslist.getItems()) {
				LoggerManager.info("["+index+"] Name: "+apiResponse.getName()+" classe:"+apiResponse.getClass().getName());
				if(apiResponse instanceof org.zaproxy.clientapi.core.ApiResponseSet apiresponseset) {
					LoggerManager.info("["+index+"] keys: "+apiresponseset.getKeys());
					LoggerManager.info("["+index+"] values: "+apiresponseset.getValues());
				}
				else if(apiResponse instanceof org.zaproxy.clientapi.core.ApiResponseElement apiresponseelement) {
					LoggerManager.info("["+index+"] name: "+apiresponseelement.getName());
					LoggerManager.info("["+index+"] value: "+apiresponseelement.getValue());
				}
			}
		}
		else if(res instanceof ApiResponseElement reselement) {
			LoggerManager.info("ApiResponseElement; name:"+reselement.getName()+"  value:"+reselement.getValue());
		}
		else if(res instanceof ApiResponseSet resset) {
			LoggerManager.info("ApiResponseSet; name:"+resset.getName());
			LoggerManager.info("keys: "+resset.getKeys());
			LoggerManager.info("values: "+resset.getValues());
		}
		else {
			LoggerManager.info("Unknown response: "+res.getClass().getName());
		}
	}
}
