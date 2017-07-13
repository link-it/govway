/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


package org.openspcoop2.utils.wadl;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.jvnet.ws.wadl.Param;
import org.jvnet.ws.wadl.ast.ApplicationNode;
import org.jvnet.ws.wadl.ast.FaultNode;
import org.jvnet.ws.wadl.ast.MethodNode;
import org.jvnet.ws.wadl.ast.PathSegment;
import org.jvnet.ws.wadl.ast.RepresentationNode;
import org.jvnet.ws.wadl.ast.ResourceNode;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiRequest;
import org.openspcoop2.utils.rest.api.ApiRequestBodyParameter;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiRequestHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * WADLApiReader
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 *
 */
public class WADLApiReader implements IApiReader {

	private ApplicationWrapper applicationWadlWrapper; 
	
	@Override
	public void init(Logger log, File file, ApiReaderConfig config) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromFile(log, file,
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema());
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}

	@Override
	public void init(Logger log, String content, String charsetName, ApiReaderConfig config) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromBytes(log, content.getBytes(charsetName),
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema());
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}

	@Override
	public void init(Logger log, byte[] content, ApiReaderConfig config) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromBytes(log, content,
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema());
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}

	@Override
	public void init(Logger log, Document doc, ApiReaderConfig config) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromDocument(log, doc,
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema());
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}

	@Override
	public void init(Logger log, Element element, ApiReaderConfig config) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromDocument(log, element,
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema());
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}

	@Override
	public void init(Logger log, URI uri, ApiReaderConfig config) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromURI(log, uri,
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema());
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}


	@Override
	public Api read() throws ProcessingException {
		
		Api api = new WADLApi(this.applicationWadlWrapper);
		
		ApplicationNode an = this.applicationWadlWrapper.getApplicationNode();
		List<ResourceNode> rs = an.getResources();
		if(rs.size()<=0){
			throw new ProcessingException("Non sono state trovate risorse registrate nel documento");
		}
		if(rs.size()>1){
			throw new ProcessingException("Trovato più di un elemento 'resources' nella radice del documento");
		}
		try{
			api.setBaseURL(new URL(rs.get(0).getUriTemplate()));
		}catch(Exception e){
			throw new ProcessingException("Trovata resources base url malformata: "+e.getMessage(),e);
		}
		
		this.readOperations(rs, api);
		
		return api;
	}
	private void readOperations(List<ResourceNode> rs,Api api) throws ProcessingException{
		try{	
			if(rs!=null && rs.size()>0){
				for (ResourceNode resourceNode : rs) {
					List<MethodNode> method = resourceNode.getMethods();
					if(method!=null && method.size()>0){
						for (MethodNode methodNode : method) {
							ApiOperation operation = new ApiOperation(HttpRequestMethod.valueOf(methodNode.getName().toUpperCase()), resourceNode.getAllResourceUriTemplate());
							
							List<Param> lHeaders = methodNode.getHeaderParameters();
							if(lHeaders!=null && lHeaders.size()>0){
								if(operation.getRequest()==null){
									operation.setRequest(new ApiRequest());
								}
								for (Param param : lHeaders) {
									ApiRequestHeaderParameter header = new ApiRequestHeaderParameter(param.getName(),param.getType().toString());
									header.setRequired(param.isRequired());
									operation.getRequest().addHeaderParameter(header);
								}
							}
							
							List<Param> lQuery = methodNode.getQueryParameters();
							if(lQuery!=null && lQuery.size()>0){
								if(operation.getRequest()==null){
									operation.setRequest(new ApiRequest());
								}
								for (Param param : lQuery) {
									ApiRequestQueryParameter query = new ApiRequestQueryParameter(param.getName(),param.getType().toString());
									query.setRequired(param.isRequired());
									operation.getRequest().addQueryParameter(query);
								}
							}

							List<PathSegment> lSegment = resourceNode.getPathSegments();
							if(lSegment!=null && lSegment.size()>0){
								for (PathSegment segment : lSegment) {
									List<Param> lDynamicPath = segment.getTemplateParameters();
									if(lDynamicPath!=null && lDynamicPath.size()>0){
										for (Param param : lDynamicPath) {
											if(operation.getRequest()==null){
												operation.setRequest(new ApiRequest());
											}
											ApiRequestDynamicPathParameter query = new ApiRequestDynamicPathParameter(param.getName(),param.getType().toString());
											//query.setRequired(param.isRequired());
											query.setRequired(true);
											operation.getRequest().addDynamicPathParameter(query);
										}
									}
								}
							}
							
							List<RepresentationNode> lInput = methodNode.getSupportedInputs();
							if(lInput!=null && lInput.size()>0){
					    		for (RepresentationNode representationNode : lInput) {
					    			if(operation.getRequest()==null){
										operation.setRequest(new ApiRequest());
									}
					    			ApiRequestBodyParameter query = new ApiRequestBodyParameter(null);
					    			query.setMediaType(representationNode.getMediaType());
					    			query.setElement(representationNode.getElement());
									operation.getRequest().addBodyParameter(query);
								}
							}
							
				    		MultivaluedMap<List<Long>, RepresentationNode> mapOutput = methodNode.getSupportedOutputs();
				    		if(mapOutput!=null && mapOutput.size()>0){
					    		Iterator<List<Long>> itOutput = mapOutput.keySet().iterator();
					    		while (itOutput.hasNext()) {
									List<java.lang.Long> listLong = (List<java.lang.Long>) itOutput.next();
									List<RepresentationNode> representationNode = mapOutput.get(listLong);
									for (int i = 0; i < listLong.size(); i++) {
										ApiResponse apiResponse = new ApiResponse();
										apiResponse.setHttpReturnCode(listLong.get(i).intValue());
										apiResponse.setMediaType(representationNode.get(i).getMediaType());
										apiResponse.setElement(representationNode.get(i).getElement());
										operation.addResponse(apiResponse);
									}
								}
				    		}
				    		
				    		MultivaluedMap<List<Long>, FaultNode> mapFault = methodNode.getFaults();
				    		if(mapFault!=null && mapFault.size()>0){
					    		Iterator<List<Long>> itFault = mapFault.keySet().iterator();
					    		while (itFault.hasNext()) {
									List<java.lang.Long> listLong = (List<java.lang.Long>) itFault.next();
									List<FaultNode> representationNode = mapFault.get(listLong);
									for (int i = 0; i < listLong.size(); i++) {
										ApiResponse apiResponse = new ApiResponse();
										apiResponse.setHttpReturnCode(listLong.get(i).intValue());
										apiResponse.setMediaType(representationNode.get(i).getMediaType());
										apiResponse.setElement(representationNode.get(i).getElement());
										operation.addResponse(apiResponse);
									}
								}
				    		}
				    		
							api.addOperation(operation);
						}
					}
					this.readOperations(resourceNode.getChildResources(), api);
				}
			}
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}
}
