/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.utils.openapi;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.api.AbstractApiParameter;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiCookieParameter;
import org.openspcoop2.utils.rest.api.ApiHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiReference;
import org.openspcoop2.utils.rest.api.ApiRequest;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiRequestFormParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.CookieParameter;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.converter.SwaggerConverter;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;


/**
 * AbstractOpenapiApiReader
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public abstract class AbstractOpenapiApiReader implements IApiReader {

	private OpenAPI openApi;
	private ApiFormats format;
	private ParseOptions parseOptions;
	private List<ApiSchema> schemas;
	
	public AbstractOpenapiApiReader(ApiFormats format) {
		this.format = format;
		this.parseOptions = new ParseOptions();
		this.schemas = new ArrayList<>();
	}

	private void parseResult(Logger log, SwaggerParseResult pr) throws ProcessingException {
		if(pr==null) {
			throw new ProcessingException("Parse result undefined");
		}
		StringBuffer bfMessage = new StringBuffer();
		if(pr.getMessages()!=null && pr.getMessages().size()>0) {
			for (String msg : pr.getMessages()) {
				if(bfMessage.length()>0) {
					bfMessage.append("\n");
				}
				bfMessage.append(msg);
			}
		}
		if(pr.getOpenAPI()!=null) {
			this.openApi = pr.getOpenAPI();
			if(bfMessage.length()>0) {
				log.debug(bfMessage.toString());
			}
		}
		else {
			if(bfMessage.length()>0) {
				throw new ProcessingException("Parse failed: "+bfMessage.toString());
			}
			else {
				throw new ProcessingException("Parse failed");
			}
		}
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
		try {
			SwaggerParseResult pr = null;
			if(ApiFormats.SWAGGER_2.equals(this.format)) {
				pr = new SwaggerConverter().readContents(content, null, this.parseOptions);	
			}
			else {
				pr = new OpenAPIV3Parser().readContents(content, null, this.parseOptions);
			}
			this.parseResult(log, pr);
					
			if(schema!=null && schema.length>0) {
				for (int i = 0; i < schema.length; i++) {
					this.schemas.add(schema[i]);
				}
			}
			
		} catch(Exception e) {
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
		String s = null;
		try {
			String charset = config!=null?config.getCharset().getValue():Charset.UTF_8.getValue();
			s = new String(content,charset);
		} catch(Exception e) {
			throw new ProcessingException(e);
		}
		this._init(log, s, config, schema);
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
		byte[]c = null;
		try {
			c = FileSystemUtilities.readBytesFromFile(file);
		} catch(Exception e) {
			throw new ProcessingException(e);
		}
		this._init(log, c, config, schema);
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
		byte[]c = null;
		try {
			c = Utilities.getAsByteArray(uri.toURL().openStream());
		} catch(Exception e) {
			throw new ProcessingException(e);
		}
		this._init(log, c, config, schema);
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
	public Api read() throws ProcessingException {
		if(this.openApi == null)
			throw new ProcessingException("Api non correttamente inizializzata");
		try {
			OpenapiApi api = new OpenapiApi(this.openApi);
			if(!this.schemas.isEmpty()) {
				for (ApiSchema apiSchema : this.schemas) {
					api.addSchema(apiSchema);
				}
			}
			if(!this.openApi.getServers().isEmpty())
				api.setBaseURL(new URL(this.openApi.getServers().get(0).getUrl()));

			if(this.openApi.getPaths() != null){
				for (String pathK : this.openApi.getPaths().keySet()) {
					PathItem path = this.openApi.getPaths().get(pathK);
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

	private ApiOperation getOperation(Operation operation, HttpRequestMethod method, String pathK, OpenapiApi api) {
		ApiOperation apiOperation = new ApiOperation(method, pathK);
		if(operation.getParameters() != null || operation.getRequestBody() != null) {
			ApiRequest request = new ApiRequest();

			if(operation.getParameters() != null) {
				for(Parameter param: operation.getParameters()) {

					addRequestParameter(param, request);
				}
			}
			if(operation.getRequestBody() != null) {
				List<ApiBodyParameter> lst = createRequestBody(operation.getRequestBody(), method, pathK, api);
				for(ApiBodyParameter param: lst) {
					request.addBodyParameter(param);
				}
			}

			apiOperation.setRequest(request);
		}

		if(operation.getResponses()!= null && !operation.getResponses().isEmpty()) {
			List<ApiResponse> responses = new ArrayList<ApiResponse>();
			for(String responseK: operation.getResponses().keySet()) {
				responses.add(createResponses(responseK, operation.getResponses().get(responseK), method, pathK, api));	
			}
			apiOperation.setResponses(responses);
		}

		return apiOperation;
	}

	private List<ApiBodyParameter> createRequestBody(RequestBody requestBody, HttpRequestMethod method, String path, OpenapiApi api) {

		List<ApiBodyParameter> lst = new ArrayList<ApiBodyParameter>();

		if(requestBody.getContent() != null && !requestBody.getContent().isEmpty()) {
			for(String consume: requestBody.getContent().keySet()) {

				Schema<?> model = requestBody.getContent().get(consume).getSchema();

				String type = null;
				ApiReference apiRef = null;
				if(model.get$ref()!= null) {
					String href = model.get$ref().trim();
					if(href.contains("#") && !href.startsWith("#")) {
						type = href.substring(href.indexOf("#"), href.length());
						type = type.replaceAll("#/components/schemas/", "").replaceAll("#/definitions/", "");
						String ref = href.split("#")[0];
						File fRef = new File(ref);
						apiRef = new ApiReference(fRef.getName(), type);
					}
					else {
						type = href.replaceAll("#/components/schemas/", "").replaceAll("#/definitions/", "");
					}
				} else {
					type = ("request_" + method.toString() + "_" + path+ "_" + consume).replace("/", "_");
					api.getDefinitions().put(type, model);
				}
				
				ApiBodyParameter bodyParam = new ApiBodyParameter(type);
				bodyParam.setMediaType(consume);
				if(apiRef!=null) {
					bodyParam.setElement(apiRef);
				}else {
					bodyParam.setElement(type);
				}
				if(requestBody.getRequired() != null)
					bodyParam.setRequired(requestBody.getRequired());

				lst.add(bodyParam);

			}
		}

		return lst;
	}

	private void addRequestParameter(Parameter param, ApiRequest request) {

		AbstractApiParameter abstractParam = null;
		String type = getParameterType(param.getSchema(), param.get$ref()); 
		if(param instanceof PathParameter) {
			abstractParam = new ApiRequestDynamicPathParameter(param.getName(), type);
		} else if(param instanceof QueryParameter) {
			abstractParam = new ApiRequestQueryParameter(param.getName(), type);
		} else if(param instanceof HeaderParameter) {
			abstractParam = new ApiHeaderParameter(param.getName(), type);
		} else if(param instanceof CookieParameter) {
			abstractParam = new ApiCookieParameter(param.getName(), type);
		}

		if(abstractParam == null) {
			if(param.getIn() != null) {
				if(param.getIn().equals("query")) {
					abstractParam = new ApiRequestQueryParameter(param.getName(), type);
				} else if(param.getIn().equals("header")) {
					abstractParam = new ApiHeaderParameter(param.getName(), type);
				} else if(param.getIn().equals("cookie")) {
					abstractParam = new ApiCookieParameter(param.getName(), type);
				} else if(param.getIn().equals("path")) {
					abstractParam = new ApiRequestDynamicPathParameter(param.getName(), type);
				}
			}
		}

		if(abstractParam != null) {
			abstractParam.setDescription(param.getDescription());
			if(param.getRequired() != null)
				abstractParam.setRequired(param.getRequired());

			if(abstractParam instanceof ApiRequestDynamicPathParameter) {
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

	private String getParameterType(Schema<?> schema, String ref) {
		if(ref != null) {
			return ref.replaceAll("#/components/schemas/", "").replaceAll("#/definitions/", "");
		}

		if(schema.get$ref() != null) {
			return getParameterType(schema, schema.get$ref());
		}
		
		if(schema instanceof ArraySchema) {
			return getParameterType(((ArraySchema)schema).getItems(), null); 
		}
		
		if(schema.getFormat() != null) {
			return schema.getFormat();
		} else {
			return schema.getType();
		}
	}

	private ApiResponse createResponses(String responseK, io.swagger.v3.oas.models.responses.ApiResponse response, HttpRequestMethod method, String path, OpenapiApi api) {

		ApiResponse apiResponse= new ApiResponse();

		int status = -1;
		try{
			if("default".equals(responseK)) {
				apiResponse.setDefaultHttpReturnCode();
			}
			else {
				status = Integer.parseInt(responseK);
				apiResponse.setHttpReturnCode(status);
			}
		} catch(NumberFormatException e) {
			throw new RuntimeException("Stato non supportato ["+responseK+"]", e);
		}
//		if(status<=0) {
//			status = 200;
//		}
		apiResponse.setDescription(response.getDescription());

		if(response.getHeaders() != null) {
			for(String header: response.getHeaders().keySet()) {
				Header property = response.getHeaders().get(header);
				
				String type = getParameterType(property.getSchema(), property.get$ref());
				
				ApiHeaderParameter parameter = new ApiHeaderParameter(header, type);
				parameter.setDescription(property.getDescription());
				if(property.getRequired() != null)
					parameter.setRequired(property.getRequired());
				
				apiResponse.addHeaderParameter(parameter);
			}
		}
		
		if(response.getContent() != null && !response.getContent().isEmpty()) {
			for(String contentType: response.getContent().keySet()) {

				MediaType mediaType = response.getContent().get(contentType);
				Schema<?> schema = mediaType.getSchema();
				
				String name = ("response_" +method.toString() + "_" + path + "_" + responseK + "_" + contentType).replace("/", "_");

				String type = null;
				ApiReference apiRef = null;
				if(schema.get$ref()!= null) {
					String href = schema.get$ref().trim();
					if(href.contains("#") && !href.startsWith("#")) {
						type = href.substring(href.indexOf("#"), href.length());
						type = type.replaceAll("#/components/schemas/", "").replaceAll("#/definitions/", "");
						String ref = href.split("#")[0];
						File fRef = new File(ref);
						apiRef = new ApiReference(fRef.getName(), type);
					}
					else {
						type = href.replaceAll("#/components/schemas/", "").replaceAll("#/definitions/", "");
					}
				} else {
					type = ("response_" +method.toString() + "_" + path + "_" + responseK + "_" + contentType).replace("/", "_");
					api.getDefinitions().put(type, schema);
				}
				
				ApiBodyParameter bodyParam = new ApiBodyParameter(name);
				bodyParam.setMediaType(contentType);
				if(apiRef!=null) {
					bodyParam.setElement(apiRef);
				}else {
					bodyParam.setElement(type);
				}
				
//				String typeF = getParameterType(schema, null);
//				bodyParam.setElement(type);
				
				bodyParam.setRequired(true);
				
				apiResponse.addBodyParameter(bodyParam);
			}
		}

		return apiResponse;
	}
	
}
