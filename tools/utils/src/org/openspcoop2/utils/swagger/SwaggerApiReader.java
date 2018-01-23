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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.AbstractApiParameter;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiCookieParameter;
import org.openspcoop2.utils.rest.api.ApiHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiRequest;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiRequestFormParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import v2.io.swagger.models.ArrayModel;
import v2.io.swagger.models.ComposedModel;
import v2.io.swagger.models.Model;
import v2.io.swagger.models.ModelImpl;
import v2.io.swagger.models.Operation;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.RefModel;
import v2.io.swagger.models.Response;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.models.parameters.BodyParameter;
import v2.io.swagger.models.parameters.CookieParameter;
import v2.io.swagger.models.parameters.FormParameter;
import v2.io.swagger.models.parameters.HeaderParameter;
import v2.io.swagger.models.parameters.Parameter;
import v2.io.swagger.models.parameters.PathParameter;
import v2.io.swagger.models.parameters.QueryParameter;
import v2.io.swagger.models.parameters.RefParameter;
import v2.io.swagger.models.properties.AbstractNumericProperty;
import v2.io.swagger.models.properties.ArrayProperty;
import v2.io.swagger.models.properties.BinaryProperty;
import v2.io.swagger.models.properties.BooleanProperty;
import v2.io.swagger.models.properties.DateProperty;
import v2.io.swagger.models.properties.DateTimeProperty;
import v2.io.swagger.models.properties.MapProperty;
import v2.io.swagger.models.properties.ObjectProperty;
import v2.io.swagger.models.properties.PasswordProperty;
import v2.io.swagger.models.properties.Property;
import v2.io.swagger.models.properties.RefProperty;
import v2.io.swagger.models.properties.StringProperty;
import v2.io.swagger.models.properties.UUIDProperty;
import v2.io.swagger.parser.SwaggerParser;


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
	public void init(Logger log, File file, ApiReaderConfig config) throws ProcessingException {
		this._init(log, file, config);
	}
	@Override
	public void init(Logger log, File file, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, file, config, schema);
	}
	private void _init(Logger log, File file, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		try {
			this.swagger = new SwaggerParser().read(file.getAbsolutePath());
		} catch(Exception e) {
			throw new ProcessingException(e);
		}
	}

	@Override
	public void init(Logger log, String content, String charsetName, ApiReaderConfig config) throws ProcessingException {
		this._init(log, content, charsetName, config);
	}
	@Override
	public void init(Logger log, String content, String charsetName, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, content, charsetName, config, schema);
	}
	private void _init(Logger log, String content, String charsetName, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		try {
			this.init(log, content.getBytes(charsetName), config);
		} catch(UnsupportedEncodingException e) {
			throw new ProcessingException(e);
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
	public void init(Logger log, Document doc, ApiReaderConfig config) throws ProcessingException {
		this._init(log, doc, config);
	}
	@Override
	public void init(Logger log, Document doc, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, doc, config, schema);
	}
	private void _init(Logger log, Document doc, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		throw new ProcessingException("Not implemented");
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
		throw new ProcessingException("Not implemented");
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
						ApiOperation operation = getOperation(path.getGet(), HttpRequestMethod.GET, pathK, api);
						api.addOperation(operation);
					}
					if(path.getPost() != null) {
						ApiOperation operation = getOperation(path.getPost(), HttpRequestMethod.POST, pathK, api);
						api.addOperation(operation);
					}
					if(path.getPut() != null) {
						ApiOperation operation = getOperation(path.getPut(), HttpRequestMethod.PUT, pathK, api);
						api.addOperation(operation);
					}
					if(path.getDelete() != null) {
						ApiOperation operation = getOperation(path.getDelete(), HttpRequestMethod.DELETE, pathK, api);
						api.addOperation(operation);
					}
					if(path.getPatch() != null) {
						ApiOperation operation = getOperation(path.getPatch(), HttpRequestMethod.PATCH, pathK, api);
						api.addOperation(operation);
					}
					if(path.getOptions() != null) {
						ApiOperation operation = getOperation(path.getOptions(), HttpRequestMethod.OPTIONS, pathK, api);
						api.addOperation(operation);
					}
				}
			}
			return api;
		} catch(Exception e){
			throw new ProcessingException(e);
		}
	}

	private ApiOperation getOperation(Operation operation, HttpRequestMethod method, String pathK, SwaggerApi api) {
		ApiOperation apiOperation = new ApiOperation(method, pathK);
		if(operation.getParameters() != null) {
			ApiRequest request = new ApiRequest();

			for(Parameter param: operation.getParameters()) {

				List<AbstractApiParameter> abstractParamList = createRequestParameters(param, operation.getConsumes(), method, pathK, api);

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
				responses.addAll(createResponses(responseK, operation.getResponses().get(responseK), operation.getProduces(), method, pathK, api));	
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
	private List<AbstractApiParameter> createRequestParameters(Parameter param, List<String> consumes, HttpRequestMethod method, String path, SwaggerApi api) {
		
		String prefix = "["+method.name()+" "+path+"]";
		
		Parameter realParam = param;
		while (realParam instanceof RefParameter) {
			realParam = this.swagger.getParameter(realParam.getName());
		}
				
		List<AbstractApiParameter> lst = new ArrayList<AbstractApiParameter>();
		if(realParam instanceof BodyParameter) {
			
			if(consumes==null || consumes.isEmpty()) {
				consumes = new ArrayList<String>();
				consumes.add(HttpConstants.CONTENT_TYPE_JSON);
			}
			
			for(String consume: consumes) {
				
				Model model = ((BodyParameter) realParam).getSchema();
				
				String type = null;
				String name = null;
				String reference = null;
				if(model instanceof RefModel) {
					RefModel ref = (RefModel) model;
					reference = ref.getReference();

				}
				else if(model instanceof ModelImpl) {
					ModelImpl modelImpl = (ModelImpl) model;
					type = "request_" + method.toString() + "_" + path.replace("/", "_");
					name = modelImpl.getType();
					api.getDefinitions().put(type, modelImpl);
				}
				else if(model instanceof ComposedModel) {
					ComposedModel composedModel = (ComposedModel) model;
					type = "request_" + method.toString() + "_" + path.replace("/", "_");
					name = composedModel.getTitle();
					api.getDefinitions().put(type, composedModel);
				}
				else if(model instanceof ArrayModel) {
					ArrayModel arrayModel = (ArrayModel) model;
					Property p = arrayModel.getItems();
					if(p instanceof v2.io.swagger.models.properties.RefProperty) {
						v2.io.swagger.models.properties.RefProperty ref = (v2.io.swagger.models.properties.RefProperty) p;
						reference = ref.get$ref();
					}
					else {
						throw new RuntimeException("Not Implemented "+prefix+" (property): "+p.getClass().getName());
					}
				}
				else {
					throw new RuntimeException("Not Implemented "+prefix+" (model): "+model.getClass().getName());
				}
				
				if(type == null)
					type = reference.replaceAll("#/definitions/", "");

				if(name == null)
					name = this.swagger.getDefinitions().get(type).getTitle();

				if(name == null) {
					name = realParam.getName();
				}
				
				ApiBodyParameter bodyParam = new ApiBodyParameter(name);
				bodyParam.setMediaType(consume);
				bodyParam.setElement(type);
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

	private List<ApiResponse> createResponses(String responseK, Response response, List<String> produces, HttpRequestMethod method, String path, SwaggerApi api) {
		
		String prefix = "[status:"+responseK+" "+method.name()+" "+path+"]";
		
		List<ApiResponse> responses = new ArrayList<ApiResponse>();
		
		if(produces==null || produces.isEmpty()) {
			produces = new ArrayList<String>();
			produces.add(HttpConstants.CONTENT_TYPE_JSON);
		}
					
		ApiResponse apiResponse = new ApiResponse();
		int status = -1;
		try{
			status = Integer.parseInt(responseK);
		} catch(NumberFormatException e) {}
		if(status<=0) {
			status = 200;
		}
		
		apiResponse.setHttpReturnCode(status);
		apiResponse.setDescription(response.getDescription());
		
		if(response.getHeaders() != null) {
			for(String header: response.getHeaders().keySet()) {
				Property property = response.getHeaders().get(header);
				String name = property.getName();
				if(name==null) {
					name = header;
				}
				ApiHeaderParameter parameter = new ApiHeaderParameter(name, property.getType());
				parameter.setDescription(property.getDescription());
				parameter.setRequired(property.getRequired());
				apiResponse.addHeaderParameter(parameter);
			}
		}
		
		for(String prod: produces) {

			if(response.getSchema() != null) {
				
				Property responseSchema = response.getSchema();
				
				while(responseSchema instanceof ArrayProperty) {
					responseSchema = ((ArrayProperty)responseSchema).getItems();
				}
				String type = null;
				String name = null;
				if( (responseSchema instanceof StringProperty) ||
					(responseSchema instanceof AbstractNumericProperty) ||
					(responseSchema instanceof BinaryProperty) ||
					(responseSchema instanceof BooleanProperty) ||
					(responseSchema instanceof DateProperty) ||
					(responseSchema instanceof DateTimeProperty) ||
					(responseSchema instanceof PasswordProperty) ||
					(responseSchema instanceof UUIDProperty)
						){
					name = "response_" +responseK+"_" + method.toString() + "_" + path.replace("/", "_");
					Model value = new ModelImpl();
					Map<String, Property> prop = new HashMap<String, Property>();
					prop.put("type", responseSchema);
					value.setProperties(prop);
					value.setTitle(name);
					value.setDescription(response.getDescription());
					api.getDefinitions().put(name, value);
					type = name;
				}
				else if(responseSchema instanceof MapProperty) {
					MapProperty schema = (MapProperty) responseSchema;

					name = "response_" +responseK+"_" + method.toString() + "_" + path.replace("/", "_");
					type = name;
					ModelImpl value = new ModelImpl();
					value.setAdditionalProperties(schema.getAdditionalProperties());
					value.setTitle(name);
					value.setType("object");
					value.setVendorExtensions(new HashMap<>());
					value.setDescription(response.getDescription());
					api.getDefinitions().put(name, value);
				}
				else if(responseSchema instanceof ObjectProperty) {
					ObjectProperty schema = (ObjectProperty) responseSchema;
					
					name = "response_" +responseK+"_" + method.toString() + "_" + path.replace("/", "_");
					type = name;
					ModelImpl value = new ModelImpl();
					value.setProperties(schema.getProperties());
					value.setTitle(name);
					value.setType("object");
					value.setVendorExtensions(new HashMap<>());
					value.setDescription(response.getDescription());
					api.getDefinitions().put(name, value);
				}
				else if(responseSchema instanceof RefProperty) {
					type = ((RefProperty)responseSchema).getSimpleRef();
					name = this.swagger.getDefinitions().get(type).getTitle();
				}
				else {
					throw new RuntimeException("Not Implemented "+prefix+" (property): "+responseSchema.getClass().getName());
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

		return responses;
	}

}
