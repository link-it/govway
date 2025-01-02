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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;

import org.apache.commons.lang.StringUtils;
import org.ehcache.shadow.org.terracotta.utilities.io.Files;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.xml.XMLUtils;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * Soap
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Soap {

	public static void main(String[] args) throws UtilsException, ClientApiException, WSDLException, URISyntaxException, org.openspcoop2.utils.wsdl.WSDLException, IOException {
				
		String openApiUsage = "soapPath|soapUrl soapTargetUrl falsePositives";
		int openApiArgs = 3;
		ZAPContext context = new ZAPContext(args, Soap.class.getName(), openApiUsage+ZAPReport.SUFFIX);
		
		String usageMsg = ZAPContext.PREFIX+openApiUsage+ZAPReport.SUFFIX;
		
		String soapPath = null;
		String soapUrl = null;
		int index = ZAPContext.START_ARGS;
		if(args.length>(index)) {
			soapPath = args[index+0];
		}
		if(soapPath==null || StringUtils.isEmpty(soapPath)) {
			throw new UtilsException("ERROR: argument 'soapPath|soapUrl' undefined"+usageMsg);
		}
		if(soapPath.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX) || soapPath.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_HTTPS_PREFIX) || soapPath.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_FILE_PREFIX)) {
			soapUrl = soapPath;
			soapPath = null;
		}
		
		String targetUrlArg = null;
		if(args.length>(index+1)) {
			targetUrlArg = args[index+1];
		}
		if(targetUrlArg==null || StringUtils.isEmpty(targetUrlArg)) {
			throw new UtilsException("ERROR: argument 'soapTargetUrl' undefined"+usageMsg);
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
		
		ZAPReport report = new ZAPReport(args, Soap.class.getName(), ZAPContext.PREFIX+" "+openApiUsage, ZAPContext.START_ARGS+openApiArgs, api);
		
		/**api.context.excludeFromContext(contextName, ".*");*/
		api.context.includeInContext(contextName, targetUrl.substring(0, (targetUrl.length()-1))+".*");
		api.context.setContextInScope(contextName, "true");
		
		api.sessionManagement.setSessionManagementMethod(contextId, "httpAuthSessionManagement", "");
		
		DefinitionWrapper dw = null;
		if(soapPath!=null) {
			dw = new DefinitionWrapper(soapPath, XMLUtils.getInstance(), true, true);
		}
		else {
			URI uri = new URI(soapUrl);
			dw = new DefinitionWrapper(uri, XMLUtils.getInstance(), true, true);
		}
		
		java.util.Map<?,?> services = dw.getAllServices();
		if(services==null || services.size()==0){
			throw new org.openspcoop2.utils.wsdl.WSDLException("Services non presenti");
		}
		Iterator<?> itServices = services.keySet().iterator();
		while(itServices.hasNext()){
			javax.xml.namespace.QName keyService = (javax.xml.namespace.QName) itServices.next();
			int count = 0;
			Iterator<?> itServicesCheck = services.keySet().iterator();
			while(itServicesCheck.hasNext()){
				javax.xml.namespace.QName keyServiceCheck = (javax.xml.namespace.QName) itServicesCheck.next();
				if(keyServiceCheck.equals(keyService)){
					count++;
				}
			}
			if(count>1){
				throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:service "+keyService+" definito piu' di una volta");
			}
		}
		Iterator<?> it = services.keySet().iterator();
		while(it.hasNext()) {
			javax.xml.namespace.QName keyService = (javax.xml.namespace.QName) it.next();
			Service service = (Service) services.get(keyService);
			
			java.util.Map<?,?> ports = service.getPorts();
			if(ports==null || ports.size()==0){
				throw new org.openspcoop2.utils.wsdl.WSDLException("Ports non presenti per il service "+keyService);
			}
			
			Iterator<?> itPort = ports.keySet().iterator();
			while(itPort.hasNext()) {
				String keyPort = (String) itPort.next();
				Port port = (Port) ports.get(keyPort);
				
				if(port.getBinding()==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:port "+keyPort+" non possiede un binding associato (service "+keyService+")");
				}
				if(port.getBinding().getQName()==null){
					throw new org.openspcoop2.utils.wsdl.WSDLException("Wsdl:port "+keyPort+" non possiede un binding (QName) associato (service "+keyService+")");
				}
				
				dw.updateLocation(port, targetUrl);
			}
		}
		
		File fTmp = File.createTempFile("soap", ".wsdl");
		try {
			dw.writeTo(fTmp, true);
			
			@SuppressWarnings("unused")
			ApiResponse response = api.soap.importFile(fTmp.getAbsolutePath());
			
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
			
			
		}finally {
			try {
				Files.delete(fTmp.toPath());
			}catch(Exception e) {
				// ignore
			}
		}

	}
	
}
