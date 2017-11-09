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


package org.openspcoop2.utils.swagger;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.AbstractApiParameter;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiCookieParameter;
import org.openspcoop2.utils.rest.api.ApiHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiRequest;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiRequestFormParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.CookieParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.parameters.RefParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;


/**
 * SwaggerApiReader
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SwaggerApiReader implements IApiReader {

	private Swagger swagger;
	
	@Override
	public void init(Logger log, File file, ApiReaderConfig config)
			throws ProcessingException {
		try {
			this.swagger = new SwaggerParser().read(file.getAbsolutePath());
		} catch(Exception e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	public void init(Logger log, String content, String charsetName,
			ApiReaderConfig config) throws ProcessingException {
		try {
			this.init(log, content.getBytes(charsetName), config);
		} catch(UnsupportedEncodingException e) {
			throw new ProcessingException(e);
		}

	}

	@Override
	public void init(Logger log, byte[] content, ApiReaderConfig config)
			throws ProcessingException {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("swagger", ""+System.currentTimeMillis());
			FileSystemUtilities.writeFile(tempFile, content);
			this.init(log, tempFile, config);
		} catch(Exception e) {
			throw new ProcessingException(e);
		} finally {
			if(tempFile!=null)
				tempFile.delete();
		}

	}

	@Override
	public void init(Logger log, Document doc, ApiReaderConfig config)
			throws ProcessingException {
		throw new ProcessingException("Not implemented");
	}

	@Override
	public void init(Logger log, Element element, ApiReaderConfig config)
			throws ProcessingException {
		throw new ProcessingException("Not implemented");
	}

	@Override
	public void init(Logger log, URI uri, ApiReaderConfig config)
			throws ProcessingException {
		this.swagger = new SwaggerParser().read(uri.toString());

	}

	@Override
	public Api read() throws ProcessingException {
		if(this.swagger == null)
			throw new ProcessingException("Api non correttamente inizializzata");
		try {
			SwaggerApi api = new SwaggerApi(this.swagger);
			String protocol = !this.swagger.getSchemes().isEmpty() ? this.swagger.getSchemes().get(0).toValue() : "http";
			api.setBaseURL(new URL(protocol, this.swagger.getHost(), this.swagger.getBasePath()));
			if(this.swagger.getPaths() != null){
				for (String pathK : this.swagger.getPaths().keySet()) {
					Path path = this.swagger.getPaths().get(pathK);
					if(path.getGet() != null) {
						ApiOperation operation = getOperation(path.getGet(), HttpRequestMethod.GET, pathK);
						api.addOperation(operation);
					}
					if(path.getPost() != null) {
						ApiOperation operation = getOperation(path.getPost(), HttpRequestMethod.POST, pathK);
						api.addOperation(operation);
					}
					if(path.getPut() != null) {
						ApiOperation operation = getOperation(path.getPut(), HttpRequestMethod.PUT, pathK);
						api.addOperation(operation);
					}
					if(path.getDelete() != null) {
						ApiOperation operation = getOperation(path.getDelete(), HttpRequestMethod.DELETE, pathK);
						api.addOperation(operation);
					}
					if(path.getPatch() != null) {
						ApiOperation operation = getOperation(path.getPatch(), HttpRequestMethod.PATCH, pathK);
						api.addOperation(operation);
					}
					if(path.getOptions() != null) {
						ApiOperation operation = getOperation(path.getOptions(), HttpRequestMethod.OPTIONS, pathK);
						api.addOperation(operation);
					}
				}
			}
			return api;
		} catch(Exception e){
			throw new ProcessingException(e);
		}
	}

	private ApiOperation getOperation(Operation operation, HttpRequestMethod method, String pathK) {
		ApiOperation apiOperation = new ApiOperation(method, pathK);
		if(operation.getParameters() != null) {
			ApiRequest request = new ApiRequest();

			for(Parameter param: operation.getParameters()) {

				List<AbstractApiParameter> abstractParamList = createRequestParameters(param, operation.getConsumes());

				if(!abstractParamList.isEmpty()) {
					
					for(AbstractApiParameter abstractParam: abstractParamList) {
						if(abstractParam instanceof ApiBodyParameter) {
							request.addBodyParameter((ApiBodyParameter) abstractParam);
						} else if(abstractParam instanceof ApiRequestDynamicPathParameter) {
							request.addDynamicPathParameter((ApiRequestDynamicPathParameter) abstractParam);
						} else if(abstractParam instanceof ApiRequestQueryParameter) {
							request.addQueryParameter((ApiRequestQueryParameter) abstractParam);
						} else if(abstractParam instanceof ApiHeaderParameter) {
							request.addHeaderParameter((ApiHeaderParameter) abstractParam);
						} else if(abstractParam instanceof ApiCookieParameter) {
							request.addCookieParameter((ApiCookieParameter) abstractParam);
						} else if(abstractParam instanceof ApiRequestFormParameter) {
							request.addFormParameter((ApiRequestFormParameter) abstractParam);
						}
					}
				}
			}

			apiOperation.setRequest(request);
		}
		
		if(operation.getResponses()!= null && !operation.getResponses().isEmpty()) {
			List<ApiResponse> responses = new ArrayList<ApiResponse>();
			for(String responseK: operation.getResponses().keySet()) {
				responses.addAll(createResponses(responseK, operation.getResponses().get(responseK), operation.getProduces()));	
			}
			apiOperation.setResponses(responses);
		}
		
		return apiOperation;
	}

	/**
	 * @param param
	 * @param produces
	 * @return
	 */
	private List<AbstractApiParameter> createRequestParameters(Parameter param, List<String> consumes) {
		
		Parameter realParam = param;
		while (realParam instanceof RefParameter) {
			realParam = this.swagger.getParameter(realParam.getName());
		}
		
		List<AbstractApiParameter> lst = new ArrayList<AbstractApiParameter>();
		if(realParam instanceof BodyParameter) {
			if(consumes != null && !consumes.isEmpty()) {
				for(String consume: consumes) {
					String reference = ((BodyParameter) realParam).getSchema().getReference();
					String type = reference.replaceAll("#/definitions/", "");

					String name = this.swagger.getDefinitions().get(type).getTitle();

					if(name == null) {
						name = realParam.getName();
					}
					
					ApiBodyParameter bodyParam = new ApiBodyParameter(name);
					bodyParam.setMediaType(consume);
					bodyParam.setElement(type);
					lst.add(bodyParam);
				}
			} else {
				ApiBodyParameter bodyParam = new ApiBodyParameter(realParam.getName());
				lst.add(bodyParam);
			}
		} else if(realParam instanceof PathParameter) {
			lst.add(new ApiRequestDynamicPathParameter(param.getName(), ((PathParameter)realParam).getType()));
		} else if(realParam instanceof QueryParameter) {
			lst.add(new ApiRequestQueryParameter(param.getName(), ((QueryParameter)realParam).getType()));
		} else if(realParam instanceof HeaderParameter) {
			lst.add(new ApiHeaderParameter(realParam.getName(), ((HeaderParameter)realParam).getType()));
		} else if(realParam instanceof CookieParameter) {
			lst.add(new ApiCookieParameter(realParam.getName(), ((CookieParameter)realParam).getType()));
		} else if(realParam instanceof FormParameter) {
			lst.add(new ApiRequestFormParameter(realParam.getName(), ((FormParameter)realParam).getType()));
		}

		for(AbstractApiParameter abstractParam: lst) {
			abstractParam.setDescription(realParam.getDescription());
			abstractParam.setRequired(realParam.getRequired());
		}
		return lst;
	}

	private List<ApiResponse> createResponses(String responseK, Response response, List<String> produces) {
		List<ApiResponse> responses = new ArrayList<ApiResponse>();
		if(produces != null && !produces.isEmpty()) {
			
			ApiResponse apiResponse = new ApiResponse();
			int status = -1;
			try{
				status = Integer.parseInt(responseK);
			} catch(NumberFormatException e) {}
			
			apiResponse.setHttpReturnCode(status);
			apiResponse.setDescription(response.getDescription());
			
			if(response.getHeaders() != null) {
				for(String header: response.getHeaders().keySet()) {
					Property property = response.getHeaders().get(header);
					Property realProperty = property;
					ApiHeaderParameter parameter = new ApiHeaderParameter(realProperty.getName(), realProperty.getType());
					parameter.setDescription(realProperty.getDescription());
					parameter.setRequired(property.getRequired());
					apiResponse.addHeaderParameter(parameter);
				}
			}
			
			for(String prod: produces) {

				if(response.getSchema() != null) {
					
					Property responseSchema = response.getSchema();
					
					String type = null;
					String name = null;
					if(responseSchema instanceof RefProperty) {
						type = ((RefProperty)responseSchema).getSimpleRef();
						name = this.swagger.getDefinitions().get(type).getTitle();
					}else if(responseSchema instanceof ArrayProperty) {
						ArrayProperty schema = (ArrayProperty) responseSchema;
						
						if(schema.getItems() instanceof RefProperty) {
							RefProperty items = (RefProperty) schema.getItems();
							type = items.getSimpleRef();
							name = this.swagger.getDefinitions().get(type).getTitle();
						} else {
							type = schema.getName();
							name = this.swagger.getDefinitions().get(type).getTitle();
						}
					}
					
					if(name == null)
						name = responseSchema.getName();

					ApiBodyParameter bodyParameter = new ApiBodyParameter(name);
					bodyParameter.setMediaType(prod);
					bodyParameter.setElement(type);
					
					apiResponse.addBodyParameter(bodyParameter);
				}
					
			}
			
			responses.add(apiResponse);
		}
		return responses;
	}

}
