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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * OpenAPI
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenAPI {

	public static void main(String[] args) throws UtilsException, ClientApiException, IOException {
				
		String openApiUsage = "openapiPath|openapiUrl openapiTargetUrl falsePositives";
		int openApiArgs = 3;
		ZAPContext context = new ZAPContext(args, OpenAPI.class.getName(), openApiUsage+ZAPReport.SUFFIX);
		
		String usageMsg = ZAPContext.PREFIX+openApiUsage+ZAPReport.SUFFIX;
		
		String openapiPath = null;
		String openapiUrl = null;
		int index = ZAPContext.START_ARGS;
		if(args.length>(index)) {
			openapiPath = args[index+0];
		}
		if(openapiPath==null || StringUtils.isEmpty(openapiPath)) {
			throw new UtilsException("ERROR: argument 'openapiPath|openapiUrl' undefined"+usageMsg);
		}
		if(openapiPath.startsWith("http://") || openapiPath.startsWith("https://") || openapiPath.startsWith("file://")) {
			openapiUrl = openapiPath;
			openapiPath = null;
		}
		
		String targetUrlArg = null;
		if(args.length>(index+1)) {
			targetUrlArg = args[index+1];
		}
		if(targetUrlArg==null || StringUtils.isEmpty(targetUrlArg)) {
			throw new UtilsException("ERROR: argument 'openapiTargetUrl' undefined"+usageMsg);
		}
		String targetUrl = targetUrlArg;
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
		
		ZAPReport report = new ZAPReport(args, OpenAPI.class.getName(), ZAPContext.PREFIX+" "+openApiUsage, ZAPContext.START_ARGS+openApiArgs, api);
		
		/**api.context.excludeFromContext(contextName, ".*");*/
		api.context.includeInContext(contextName, targetUrl.substring(0, (targetUrl.length()-1))+".*");
		api.context.setContextInScope(contextName, "true");
		
		api.sessionManagement.setSessionManagementMethod(contextId, "httpAuthSessionManagement", "");
		
		if(openapiPath!=null) {
			api.openapi.importFile(openapiPath, targetUrl, contextId);
		}
		else {
			api.openapi.importUrl(openapiUrl, targetUrl, contextId);
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
