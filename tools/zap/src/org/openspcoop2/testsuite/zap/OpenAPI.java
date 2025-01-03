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

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ClientApi;

/**
 * OpenAPI
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenAPI {

	public static void main(String[] args) throws Exception {
				
		String openApiUsage = "openapiPath|openapiUrl openapiTargetUrl falsePositives";
		int openApiArgs = 3;
		ZAPContext context = new ZAPContext(args, OpenAPI.class.getName(), openApiUsage+ZAPReport.suffix);
		
		String usageMsg = ZAPContext.prefix+openApiUsage+ZAPReport.suffix;
		
		String openapiPath = null;
		String openapiUrl = null;
		int index = ZAPContext.startArgs;
		if(args.length>(index)) {
			openapiPath = args[index+0];
		}
		if(openapiPath==null || StringUtils.isEmpty(openapiPath)) {
			throw new Exception("ERROR: argument 'openapiPath|openapiUrl' undefined"+usageMsg);
		}
		if(openapiPath.startsWith("http://") || openapiPath.startsWith("https://") || openapiPath.startsWith("file://")) {
			openapiUrl = openapiPath;
			openapiPath = null;
		}
		
		String _targetUrl = null;
		if(args.length>(index+1)) {
			_targetUrl = args[index+1];
		}
		if(_targetUrl==null || StringUtils.isEmpty(_targetUrl)) {
			throw new Exception("ERROR: argument 'openapiTargetUrl' undefined"+usageMsg);
		}
		String targetUrl = _targetUrl;
		if(!targetUrl.endsWith("/")) {
			targetUrl=targetUrl+"/";
		}
		
		String falsePositivesArg = null;
		if(args.length>(index+2)) {
			falsePositivesArg = args[index+2];
		}
		if(falsePositivesArg==null || StringUtils.isEmpty(falsePositivesArg)) {
			throw new UtilsException("ERROR: argument 'falsePositives' undefined"+usageMsg);
		}
		
		File falsePositivesF = new File(falsePositivesArg);
		String prefix = "ERROR: file argument 'falsePositives="+falsePositivesF.getAbsolutePath()+"' ";
		if(!falsePositivesF.exists()) {
			throw new UtilsException(prefix+"not exists"+usageMsg);
		}
		if(!falsePositivesF.canRead()) {
			throw new UtilsException(prefix+"cannot read"+usageMsg);
		}
		
		String content = FileSystemUtilities.readFile(falsePositivesF);
		if(content==null || StringUtils.isEmpty(content)) {
			throw new UtilsException(prefix+"is empty"+usageMsg);
		}
		List<FalsePositive> falsePositives = FalsePositive.parse(content);
		
		ClientApi api = context.getClientApi();
		String contextName = context.getContextName();
		String contextId = context.getContextId();
		
		if(falsePositives!=null && !falsePositives.isEmpty()) {
			FalsePositive.addFalsePositives(falsePositives, api, contextId);
		}
		
		ZAPReport report = new ZAPReport(args, OpenAPI.class.getName(), ZAPContext.prefix+" "+openApiUsage, ZAPContext.startArgs+openApiArgs, api);
		
		//api.context.excludeFromContext(contextName, ".*");
		api.context.includeInContext(contextName, targetUrl.substring(0, (targetUrl.length()-1))+".*");
		api.context.setContextInScope(contextName, "true");
		
		api.sessionManagement.setSessionManagementMethod(contextId, "httpAuthSessionManagement", "");
		
		@SuppressWarnings("unused")
		ApiResponse response = null;
		if(openapiPath!=null) {
			response = api.openapi.importFile(openapiPath, targetUrl, contextId);
		}
		else {
			response = api.openapi.importUrl(openapiUrl, targetUrl, contextId);
		}
		
		for (ZAPReportTemplate rt : report.getTemplates()) {
			api.reports.generate(report.getTitle(), 
					rt.template, 
					rt.theme,
					report.getDescription(),
					contextName, 
					targetUrl, 
					rt.sections,
					report.getIncludedConfidences(),
					report.getIncludedRisks(),
	        		null, 
	        		rt.fileName, 
	        		report.getDir(), 
	        		report.isDisplay()+"");
			
		}
		
	}
	
}
