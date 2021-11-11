/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * WADLApiReader
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class WADLApiReader implements IApiReader {

	private ApplicationWrapper applicationWadlWrapper;
	private ApiSchema[] schemas;
	
	private Map<String, byte[]> convert(ApiSchema ... schema) {
		Map<String, byte[]> map = null;
		if(schema!=null && schema.length>0) {
			for (ApiSchema apiSchema : schema) {
				if(ApiSchemaType.XSD.equals(apiSchema.getType())) {
					if(map==null) {
						map = new HashMap<String, byte[]>();
					}
					map.put(apiSchema.getName(), apiSchema.getContent());
				}
			}
		}
		return map;
	}
	
	
	
	@Override
	public void init(Logger log, String content, ApiReaderConfig config) throws ProcessingException {
		this._init(log, content, config);
	}
	@Override
	public void init(Logger log, String content, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, content, config, schema);
	}
	private void _init(Logger log, String content, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromBytes(log, content.getBytes(config.getCharset().getValue()),
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema(),
					this.convert(schema));
			this.schemas = schema;
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}
	
	
	
	@Override
	public void init(Logger log, byte[] content, ApiReaderConfig config) throws ProcessingException {
		this._init(log, content, config);
	}
	@Override
	public void init(Logger log, byte[] content, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, content, config, schema);
	}
	private void _init(Logger log, byte[] content, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromBytes(log, content,
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema(),
					this.convert(schema));
			this.schemas = schema;
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}
	
	
	
	
	@Override
	public void init(Logger log, File file, ApiReaderConfig config) throws ProcessingException {
		this._init(log, file, config);
	}
	@Override
	public void init(Logger log, File file, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, file, config, schema);
	}
	private void _init(Logger log, File file, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromFile(log, file,
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema(),
					this.convert(schema));
			this.schemas = schema;
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}
	
	
	
	
	@Override
	public void init(Logger log, URI uri, ApiReaderConfig config) throws ProcessingException {
		this._init(log, uri, config);
	}
	@Override
	public void init(Logger log, URI uri, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, uri, config, schema);
	}
	private void _init(Logger log, URI uri, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromURI(log, uri,
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema(),
					this.convert(schema));
			this.schemas = schema;
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}




	@Override
	public void init(Logger log, Document doc, ApiReaderConfig config) throws ProcessingException {
		this._init(log, doc, config);
	}
	@Override
	public void init(Logger log, Document doc, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, doc, config, schema);
	}
	private void _init(Logger log, Document doc, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromDocument(log, doc,
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema(),
					this.convert(schema));
			this.schemas = schema;
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}

	
	
	
	@Override
	public void init(Logger log, Element element, ApiReaderConfig config) throws ProcessingException {
		this._init(log, element, config);
	}
	@Override
	public void init(Logger log, Element element, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, element, config, schema);
	}
	private void _init(Logger log, Element element, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		try{
			this.applicationWadlWrapper = WADLUtilities.getInstance(config.getXmlUtils()).readWADLFromDocument(log, element,
					config.isVerbose(),config.isProcessInclude(),config.isProcessInlineSchema(),
					this.convert(schema));
			this.schemas = schema;
		}catch(Exception e){
			throw new ProcessingException(e.getMessage(),e);
		}
	}




	@Override
	public Api read() throws ProcessingException {
		
		Api api = new WADLApi(this.applicationWadlWrapper);
		
		if(this.schemas!=null && this.schemas.length>0) {
			for (ApiSchema apiSchema : this.schemas) {
				api.addSchema(apiSchema);
			}
		}
		
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
									ApiHeaderParameter header = new ApiHeaderParameter(param.getName(),param.getType().toString());
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
					    			ApiBodyParameter query = new ApiBodyParameter(null);
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
										int httpStatus = listLong.get(i).intValue();
										RepresentationNode rNodeHttpStatus = representationNode.get(i);
										
										ApiResponse apiResponse = null;
										for (ApiResponse responseExists : operation.getResponses()) {
											if(responseExists.getHttpReturnCode() == httpStatus) {
												apiResponse = responseExists;
												break;
											}
										}
										
										if(apiResponse==null) {
											apiResponse=new ApiResponse();
											apiResponse.setHttpReturnCode(httpStatus);
											
											if(rNodeHttpStatus.getParam()!=null){
												for (Param param : rNodeHttpStatus.getParam()) {
													ApiHeaderParameter header = new ApiHeaderParameter(param.getName(),param.getType().toString());
													header.setRequired(param.isRequired());
													apiResponse.addHeaderParameter(header);
												}
							    			}
											
											operation.addResponse(apiResponse);
										}
										
										if(rNodeHttpStatus.getMediaType()!=null) {
											String name = null;
											if(rNodeHttpStatus.getElement()!=null) {
												name = rNodeHttpStatus.getElement().getLocalPart();
											}
											ApiBodyParameter bodyParameter = new ApiBodyParameter(name);
											bodyParameter.setMediaType(rNodeHttpStatus.getMediaType());
											bodyParameter.setElement(rNodeHttpStatus.getElement());
											apiResponse.addBodyParameter(bodyParameter);
										}
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
										int httpStatus = listLong.get(i).intValue();
										FaultNode rNodeHttpStatus = representationNode.get(i);
										
										ApiResponse apiResponse = null;
										for (ApiResponse responseExists : operation.getResponses()) {
											if(responseExists.getHttpReturnCode() == httpStatus) {
												apiResponse = responseExists;
												break;
											}
										}
										
										if(apiResponse==null) {
											apiResponse=new ApiResponse();
											apiResponse.setHttpReturnCode(httpStatus);
											
											if(rNodeHttpStatus.getParam()!=null){
												for (Param param : rNodeHttpStatus.getParam()) {
													ApiHeaderParameter header = new ApiHeaderParameter(param.getName(),param.getType().toString());
													header.setRequired(param.isRequired());
													apiResponse.addHeaderParameter(header);
												}
							    			}
											
											operation.addResponse(apiResponse);
										}
										
										if(rNodeHttpStatus.getMediaType()!=null) {
											String name = null;
											if(rNodeHttpStatus.getElement()!=null) {
												name = rNodeHttpStatus.getElement().getLocalPart();
											}
											ApiBodyParameter bodyParameter = new ApiBodyParameter(name);
											bodyParameter.setMediaType(rNodeHttpStatus.getMediaType());
											bodyParameter.setElement(rNodeHttpStatus.getElement());
											apiResponse.addBodyParameter(bodyParameter);
										}
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
