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


package org.openspcoop2.utils.openapi;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.json.YAMLUtils;
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
import org.openspcoop2.utils.rest.api.ApiParameterSchema;
import org.openspcoop2.utils.rest.api.ApiParameterSchemaComplexType;
import org.openspcoop2.utils.rest.api.ApiReference;
import org.openspcoop2.utils.rest.api.ApiRequest;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiRequestFormParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaTypeRestriction;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
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
	private String openApiRaw;
	private ApiFormats format;
	private ParseOptions parseOptions;
	private List<ApiSchema> schemas;
	private boolean resolveExternalRef = true;
	private String parseWarningResult;
	
	private boolean debug;
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public AbstractOpenapiApiReader(ApiFormats format) {
		this.format = format;
		this.parseOptions = new ParseOptions();
		this.schemas = new ArrayList<>();
	}

	protected static OpenAPI parseResult(Logger log, SwaggerParseResult pr, StringBuilder sbParseWarningResult) throws ProcessingException {
		if(pr==null) {
			throw new ProcessingException("Parse result undefined");
		}
		StringBuilder bfMessage = new StringBuilder();
		if(pr.getMessages()!=null && pr.getMessages().size()>0) {
			for (String msg : pr.getMessages()) {
				if(bfMessage.length()>0) {
					bfMessage.append("\n");
				}
				bfMessage.append("- ").append(msg);
			}
		}
		OpenAPI openApi = null;
		if(pr.getOpenAPI()!=null) {
			openApi = pr.getOpenAPI();
			if(bfMessage.length()>0) {
				log.debug(bfMessage.toString());
				sbParseWarningResult.append(bfMessage.toString());
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
		return openApi;
	}
	
	
	@Override
	public void init(Logger log, String content, ApiReaderConfig config) throws ProcessingException {
		this._init(log, content, config);
	}
	@Override
	public void init(Logger log, String content, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		this._init(log, content, config, schema);
	}
	private void _init(Logger log, String contentParam, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException {
		
		String content = contentParam;
		try {
			YAMLUtils yamlUtils = YAMLUtils.getInstance();
			boolean apiRawIsYaml = yamlUtils.isYaml(content);
			if(apiRawIsYaml) {
				// Fix merge key '<<: *'
				if(YAMLUtils.containsMergeKeyAnchor(content)) {
					// Risoluzione merge key '<<: *'
					String jsonRepresentation = YAMLUtils.resolveMergeKeyAndConvertToJson(content);
					if(jsonRepresentation!=null) {
						content = jsonRepresentation;
					}
				}
			}
		}catch(Throwable t) {
			log.error("Find and resolve merge key failed: "+t.getMessage(),t);
			content = contentParam;
		}
		
		try {
			SwaggerParseResult pr = null;
			if(ApiFormats.SWAGGER_2.equals(this.format)) {
				pr = new SwaggerConverter().readContents(content, null, this.parseOptions);	
			}
			else {
				pr = new OpenAPIV3Parser().readContents(content, null, this.parseOptions);
			}
			StringBuilder sbParseWarningResult = new StringBuilder();
			this.openApi = parseResult(log, pr, sbParseWarningResult);
			if(sbParseWarningResult.length()>0) {
				this.parseWarningResult = sbParseWarningResult.toString();
			}
			
			this.openApiRaw = content;
					
			if(schema!=null && schema.length>0) {
				for (int i = 0; i < schema.length; i++) {
					this.schemas.add(schema[i]);
				}
			}
			
			this.resolveExternalRef = config.isProcessInclude();
			
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
			OpenapiApi api = new OpenapiApi(this.format, this.openApi, this.openApiRaw, this.parseWarningResult);
			if(!this.schemas.isEmpty()) {
				for (ApiSchema apiSchema : this.schemas) {
					api.addSchema(apiSchema);
				}
			}
			if(!this.openApi.getServers().isEmpty()) {
				String server = this.openApi.getServers().get(0).getUrl();
				URL url = null;
				try {
					url = new URL(server);
				}catch(Exception e) {
					// provo a verificare se il problema è che non e' stato definito il protocollo (es. in swagger lo 'schemes')
					if(server!=null && server.startsWith("/")) {
						if(!server.equals("/")) {
							server = "http:"+server;
							try {
								url = new URL(server);
							}catch(Exception e2) {
								// nop
							}
						}
					}
				}
				if(url!=null) {
					api.setBaseURL(url);
				}
			}
			if(this.openApi.getInfo()!=null) {
				api.setDescription(this.openApi.getInfo().getDescription());
			}

			if(this.openApi.getPaths() != null){
				for (String pathK : this.openApi.getPaths().keySet()) {
					PathItem path = this.openApi.getPaths().get(pathK);
					if(path.getGet() != null) {
						ApiOperation operation = getOperation(path.getGet(), path.getParameters(), HttpRequestMethod.GET, pathK, api);
						api.addOperation(operation);
					}
					if(path.getHead() != null) {
						ApiOperation operation = getOperation(path.getHead(), path.getParameters(), HttpRequestMethod.HEAD, pathK, api);
						api.addOperation(operation);
					}
					if(path.getPost() != null) {
						ApiOperation operation = getOperation(path.getPost(), path.getParameters(), HttpRequestMethod.POST, pathK, api);
						api.addOperation(operation);
					}
					if(path.getPut() != null) {
						ApiOperation operation = getOperation(path.getPut(), path.getParameters(), HttpRequestMethod.PUT, pathK, api);
						api.addOperation(operation);
					}
					if(path.getDelete() != null) {
						ApiOperation operation = getOperation(path.getDelete(), path.getParameters(), HttpRequestMethod.DELETE, pathK, api);
						api.addOperation(operation);
					}
					if(path.getOptions() != null) {
						ApiOperation operation = getOperation(path.getOptions(), path.getParameters(), HttpRequestMethod.OPTIONS, pathK, api);
						api.addOperation(operation);
					}
					if(path.getTrace() != null) {
						ApiOperation operation = getOperation(path.getTrace(), path.getParameters(), HttpRequestMethod.TRACE, pathK, api);
						api.addOperation(operation);
					}
					if(path.getPatch() != null) {
						ApiOperation operation = getOperation(path.getPatch(), path.getParameters(), HttpRequestMethod.PATCH, pathK, api);
						api.addOperation(operation);
					}
				}
			}
			return api;
		} catch(Exception e){
			throw new ProcessingException(e);
		}
	}

	private ApiOperation getOperation(Operation operation, List<Parameter> listParameter, HttpRequestMethod method, String pathK, OpenapiApi api) {
		ApiOperation apiOperation = new ApiOperation(method, pathK);
		apiOperation.setDescription(operation.getDescription());
		if( (listParameter!=null && !listParameter.isEmpty())
				||
				(operation.getParameters() != null)
				|| 
				(operation.getRequestBody() != null)
			) {
			ApiRequest request = new ApiRequest();

			if(listParameter!=null && !listParameter.isEmpty()) {
				for(Parameter param: listParameter) {
					addRequestParameter(param, request, method, pathK, api);
				}
			}
			if(operation.getParameters() != null) {
				for(Parameter param: operation.getParameters()) {
					addRequestParameter(param, request, method, pathK, api);
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

		if(requestBody.get$ref()!=null) {
			
			String ref = requestBody.get$ref();
			boolean external = false;
			if(ref.contains("#")) {
				external = !ref.trim().startsWith("#");
				ref = ref.substring(ref.indexOf("#"));
			}
			ref = ref.trim().replaceAll("#/components/requestBodies/", "").replaceAll("#/definitions/", "");
			
			if(api.getApi()==null) {
				throw new RuntimeException("Richiesta non corretta: api da cui risolvere la ref '"+requestBody.get$ref()+"' non trovata");
			}
			if(api.getApi().getComponents()==null) {
				if(!external || this.resolveExternalRef) {
					throw new RuntimeException("Richiesta non corretta: componenti, sui cui risolvere la ref '"+requestBody.get$ref()+"', non presenti");
				}
			}
			else {
				if(api.getApi().getComponents().getResponses()==null || api.getApi().getComponents().getResponses().size()<=0) {
					if(!external || this.resolveExternalRef) {
						throw new RuntimeException("Richiesta non corretta: richieste definite come componenti, sui cui risolvere la ref '"+requestBody.get$ref()+"', non presenti");
					}
				}
				else {
					boolean find = false;
					Iterator<String> itKeys = api.getApi().getComponents().getRequestBodies().keySet().iterator();
					while (itKeys.hasNext()) {
						String key = (String) itKeys.next();
						if(key.equals(ref)) {
							requestBody = api.getApi().getComponents().getRequestBodies().get(key);
							find = true;
							break;
						}
					}
					if(!find) {
						if(!external || this.resolveExternalRef) {
							throw new RuntimeException("Richiesta non corretta: ref '"+requestBody.get$ref()+"' non presente tra le richieste definite come componenti");
						}
					}
				}
			}
		}
		
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

				bodyParam.setDescription(requestBody.getDescription());
				
				lst.add(bodyParam);

			}
		}

		return lst;
	}

	private void addRequestParameter(Parameter paramP, ApiRequest request, HttpRequestMethod method, String path, OpenapiApi api) {
		
		// resolve ref parameter
		Parameter param = this.resolveParameterRef(paramP, api);
		if(param==null) {
			param = paramP;
		}
		
		AbstractApiParameter abstractParam = null;
		String name = param.getName();
		if(name==null && param.get$ref()!=null) {
			// provo a risolvere il nome di un eventuale parametro riferito
			name = getRefParameterName(param.get$ref(), api);
		}
		
		ApiParameterSchema apiParameterSchema = getParameterSchema(param.getSchema(), param.get$ref(), name, 
				null,	
				param.getStyle()!=null ? param.getStyle().toString(): null,
				param.getExplode(),
				api);
		
		if(this.debug) {
			System.out.println("=======================================");
			System.out.println("REQUEST ("+method+" "+path+") name ["+name+"] required["+param.getRequired()+"] className["+param.getClass().getName()+"] ref["+param.get$ref()+"] apiParameterSchema["+apiParameterSchema.toString()+"]");
			System.out.println("=======================================");
		}
		
		if(param instanceof PathParameter) {
			abstractParam = new ApiRequestDynamicPathParameter(name, apiParameterSchema);
		} else if(param instanceof QueryParameter) {
			abstractParam = new ApiRequestQueryParameter(name, apiParameterSchema);
		} else if(param instanceof HeaderParameter) {
			abstractParam = new ApiHeaderParameter(name, apiParameterSchema);
		} else if(param instanceof CookieParameter) {
			abstractParam = new ApiCookieParameter(name, apiParameterSchema);
		}
		
		if(abstractParam == null) {
			if(param.getIn() != null) {
				if(param.getIn().equals("query")) {
					abstractParam = new ApiRequestQueryParameter(name, apiParameterSchema);
				} else if(param.getIn().equals("header")) {
					abstractParam = new ApiHeaderParameter(name, apiParameterSchema);
				} else if(param.getIn().equals("cookie")) {
					abstractParam = new ApiCookieParameter(name, apiParameterSchema);
				} else if(param.getIn().equals("path")) {
					abstractParam = new ApiRequestDynamicPathParameter(name, apiParameterSchema);
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

	private Parameter resolveParameterRef(Parameter p,  OpenapiApi api) {
		if(p.get$ref()==null || "".equals(p.get$ref())) {
			return p;
		}
		
		String ref = p.get$ref();
		boolean external = false;
		if(ref.contains("#")) {
			external = !ref.trim().startsWith("#");
			ref = ref.substring(ref.indexOf("#"));
		}
		boolean refParameters = ref.startsWith("#/components/parameters/");
		if(!refParameters) {
			return p;
		}
		
		if(api.getApi().getComponents().getParameters()==null || api.getApi().getComponents().getParameters().size()<=0) {
			if(!external || this.resolveExternalRef) {
				throw new RuntimeException("Parametro '"+p.getName()+"' non corretto: parametri definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
			}
			else {
				return null;
			}
		}
		else {
			String checkRef = ref.trim().replaceAll("#/components/parameters/", "");
			Parameter param = null;
			Iterator<String> itKeys = api.getApi().getComponents().getParameters().keySet().iterator();
			while (itKeys.hasNext()) {
				String key = (String) itKeys.next();
				if(key.equals(checkRef)) {
					param = api.getApi().getComponents().getParameters().get(key);
					break;
				}
			}
			if(param==null) {
				if(!external || this.resolveExternalRef) {
					throw new RuntimeException("Parametro '"+p.getName()+"' non corretto: ref '"+ref+"' non presente tra i parametri definiti come componenti");
				}
			}
			return param;
		}
	}
	
	private String getRefParameterName(String refParam, OpenapiApi api) {
		if(refParam != null) {
			String ref = refParam;
			boolean external = false;
			if(ref.contains("#")) {
				external = !ref.trim().startsWith("#");
				ref = ref.substring(ref.indexOf("#"));
			}
			
			boolean refParameters = ref.startsWith("#/components/parameters/"); 
			if(refParameters) {
				if(api.getApi()==null) {
					throw new RuntimeException("Parametro non corretto: api da cui risolvere la ref '"+ref+"' non trovata");
				}
				if(api.getApi().getComponents()==null) {
					if(!external || this.resolveExternalRef) {
						throw new RuntimeException("Parametro non corretto: componenti, sui cui risolvere la ref '"+ref+"', non presenti");
					}
					else {
						return refParam;
					}
				}
				else {
					if(api.getApi().getComponents().getParameters()==null || api.getApi().getComponents().getParameters().size()<=0) {
						if(!external || this.resolveExternalRef) {
							throw new RuntimeException("Parametro non corretto: parametri definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
						}
						else {
							return refParam;
						}
					}
					else {
						String checkRef = ref.trim().replaceAll("#/components/parameters/", "");
						Parameter param = null;
						Iterator<String> itKeys = api.getApi().getComponents().getParameters().keySet().iterator();
						while (itKeys.hasNext()) {
							String key = (String) itKeys.next();
							if(key.equals(checkRef)) {
								param = api.getApi().getComponents().getParameters().get(key);
								break;
							}
						}
						if(param==null) {
							if(!external || this.resolveExternalRef) {
								throw new RuntimeException("Parametro  non corretto: ref '"+ref+"' non presente tra i parametri definiti come componenti");
							}
							else {
								return refParam;
							}
						}
						else {
							return param.getName();
						}
					}
				}
			}					
		}

		return null;
	}
	
	/*
	private String getParameterType(Schema<?> schema, String refParam, String name, OpenapiApi api) {
		if(refParam != null) {
			String ref = refParam;
			boolean external = false;
			if(ref.contains("#")) {
				external = !ref.trim().startsWith("#");
				ref = ref.substring(ref.indexOf("#"));
			}
			
			boolean refHeaders = ref.startsWith("#/components/headers/");
			boolean refParameters = ref.startsWith("#/components/parameters/"); 
			boolean refSchema = ref.startsWith("#/components/schemas/"); 
			if(refHeaders || refParameters || refSchema) {
				if(api.getApi()==null) {
					throw new RuntimeException("Parametro '"+name+"' non corretto: api da cui risolvere la ref '"+ref+"' non trovata");
				}
				else {
					if(api.getApi().getComponents()==null) {
						if(!external || this.resolveExternalRef) {
							throw new RuntimeException("Parametro '"+name+"' non corretto: componenti, sui cui risolvere la ref '"+ref+"', non presenti");
						}
						else {
							return refParam;
						}
					}
					else {
						if(refHeaders) {
							if(api.getApi().getComponents().getHeaders()==null || api.getApi().getComponents().getHeaders().size()<=0) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: headers definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
								}
								else {
									return refParam;
								}
							}
							String checkRef = ref.trim().replaceAll("#/components/headers/", "");
							Header hdr = null;
							Iterator<String> itKeys = api.getApi().getComponents().getHeaders().keySet().iterator();
							while (itKeys.hasNext()) {
								String key = (String) itKeys.next();
								if(key.equals(checkRef)) {
									hdr = api.getApi().getComponents().getHeaders().get(key);
									break;
								}
							}
							if(hdr==null) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: ref '"+ref+"' non presente tra gli headers definiti come componenti");
								}
								else {
									return refParam;
								}
							}
							else {
								return getParameterType(hdr.getSchema(), hdr.get$ref(), name, api);
							}
						}
						else if(refParameters) {
							if(api.getApi().getComponents().getParameters()==null || api.getApi().getComponents().getParameters().size()<=0) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: parametri definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
								}
								else {
									return refParam;
								}
							}
							String checkRef = ref.trim().replaceAll("#/components/parameters/", "");
							Parameter param = null;
							Iterator<String> itKeys = api.getApi().getComponents().getParameters().keySet().iterator();
							while (itKeys.hasNext()) {
								String key = (String) itKeys.next();
								if(key.equals(checkRef)) {
									param = api.getApi().getComponents().getParameters().get(key);
									break;
								}
							}
							if(param==null) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: ref '"+ref+"' non presente tra i parametri definiti come componenti");
								}
								else {
									return refParam;
								}
							}
							else {
								if(name==null && param.getName()!=null) {
									name = param.getName();
								}
								return getParameterType(param.getSchema(), param.get$ref(), name, api);
							}
						}
						else {
							if(api.getApi().getComponents().getSchemas()==null || api.getApi().getComponents().getSchemas().size()<=0) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: schemi definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
								}
								else {
									return refParam;
								}
							}
							String checkRef = ref.trim().replaceAll("#/components/schemas/", "");
							Schema<?> schemaRiferito = null;
							Iterator<String> itKeys = api.getApi().getComponents().getSchemas().keySet().iterator();
							while (itKeys.hasNext()) {
								String key = (String) itKeys.next();
								if(key.equals(checkRef)) {
									schemaRiferito = api.getApi().getComponents().getSchemas().get(key);
									break;
								}
							}
							if(schemaRiferito==null) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: ref '"+ref+"' non presente tra gli schemi definiti come componenti");
								}
								else {
									return refParam;
								}
							}
							else {
								return getParameterType(schemaRiferito, null, name, api);
							}
						}
					}
				}
			}
			else {
				// i requestBodies e le response non dovrebbero rientrare in questo metodo
				return ref.replaceAll("#/components/schemas/", "").replaceAll("#/definitions/", "");
			}
						
		}

		if(schema==null) {
			throw new RuntimeException("Parametro '"+name+"' non corretto: schema non definito");
		}
		
		if(schema.get$ref() != null) {
			return getParameterType(schema, schema.get$ref(), name, api);
		}
		
		if(schema instanceof ArraySchema) {
			return getParameterType(((ArraySchema)schema).getItems(), null, name, api); 
		}
		
		if(schema instanceof ComposedSchema) {
			ComposedSchema cs = (ComposedSchema) schema;
			if(cs.getAnyOf()!=null && !cs.getAnyOf().isEmpty() && cs.getAnyOf().get(0)!=null) {
				// utilizzo il primo schema
				//NO NON VA BENE. DEVO STRUTTURARE L'INFORMAZIONE INSIEME ALLA RESTRIZIONE come una lista ???
				if(cs.getAnyOf().get(0).getFormat() != null) {
					return cs.getAnyOf().get(0).getFormat();
				} else {
					return cs.getAnyOf().get(0).getType();
				}
				// PRIMA TERMINARE COSI PER VEDERE SE FUNZIONA USANDO UN TIPO A CASO
			}
			else if(cs.getAllOf()!=null && !cs.getAllOf().isEmpty() && cs.getAllOf().get(0)!=null) {
				// utilizzo il primo schema
				//NO NON VA BENE. DEVO STRUTTURARE L'INFORMAZIONE INSIEME ALLA RESTRIZIONE come una lista ???
				if(cs.getAllOf().get(0).getFormat() != null) {
					return cs.getAllOf().get(0).getFormat();
				} else {
					return cs.getAllOf().get(0).getType();
				}
				// ALL OFF HA SENSO ??????????????? PROVARE COME SI COMPORTA OPENAPI
			}
		}
		
		if(schema.getFormat() != null) {
			return schema.getFormat();
		} else {
			return schema.getType();
		}
	}
	
	private ApiSchemaTypeRestriction getParameterSchemaTypeRestriction(Schema<?> schema, String ref, String name, 
			Boolean arrayParameter, String style, Boolean explode, OpenapiApi api) {
		if(ref != null) {
			boolean external = false;
			if(ref.contains("#")) {
				external = !ref.trim().startsWith("#");
				ref = ref.substring(ref.indexOf("#"));
			}
			
			boolean refHeaders = ref.startsWith("#/components/headers/");
			boolean refParameters = ref.startsWith("#/components/parameters/"); 
			boolean refSchema = ref.startsWith("#/components/schemas/"); 
			if(refHeaders || refParameters || refSchema) {
				if(api.getApi()==null) {
					throw new RuntimeException("Parametro '"+name+"' non corretto: api da cui risolvere la ref '"+ref+"' non trovata");
				}
				else {
					if(api.getApi().getComponents()==null) {
						if(!external || this.resolveExternalRef) {
							throw new RuntimeException("Parametro '"+name+"' non corretto: componenti, sui cui risolvere la ref '"+ref+"', non presenti");
						}
						else {
							return null;
						}
					}
					else {
						if(refHeaders) {
							if(api.getApi().getComponents().getHeaders()==null || api.getApi().getComponents().getHeaders().size()<=0) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: headers definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
								}
								else {
									return null;
								}
							}
							String checkRef = ref.trim().replaceAll("#/components/headers/", "");
							Header hdr = null;
							Iterator<String> itKeys = api.getApi().getComponents().getHeaders().keySet().iterator();
							while (itKeys.hasNext()) {
								String key = (String) itKeys.next();
								if(key.equals(checkRef)) {
									hdr = api.getApi().getComponents().getHeaders().get(key);
									break;
								}
							}
							if(hdr==null) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: ref '"+ref+"' non presente tra gli headers definiti come componenti");
								}
								else {
									return null;
								}
							}
							else {
								return getParameterSchemaTypeRestriction(hdr.getSchema(), hdr.get$ref(), name, 
													arrayParameter,
													hdr.getStyle()!=null ? hdr.getStyle().toString(): null,
													hdr.getExplode(),
													api);
							}
						}
						else if(refParameters) {
							if(api.getApi().getComponents().getParameters()==null || api.getApi().getComponents().getParameters().size()<=0) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: parametri definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
								}
								else {
									return null;
								}
							}
							String checkRef = ref.trim().replaceAll("#/components/parameters/", "");
							Parameter param = null;
							Iterator<String> itKeys = api.getApi().getComponents().getParameters().keySet().iterator();
							while (itKeys.hasNext()) {
								String key = (String) itKeys.next();
								if(key.equals(checkRef)) {
									param = api.getApi().getComponents().getParameters().get(key);
									break;
								}
							}
							if(param==null) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: ref '"+ref+"' non presente tra i parametri definiti come componenti");
								}
								else {
									return null;
								}
							}
							else {
								if(name==null && param.getName()!=null) {
									name = param.getName();
								}
								return getParameterSchemaTypeRestriction(param.getSchema(), param.get$ref(), name, 
												arrayParameter,
												param.getStyle()!=null ? param.getStyle().toString(): null,
												param.getExplode(),
												api);
							}
						}
						else {
							if(api.getApi().getComponents().getSchemas()==null || api.getApi().getComponents().getSchemas().size()<=0) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: schemi definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
								}
								else {
									return null;
								}
							}
							String checkRef = ref.trim().replaceAll("#/components/schemas/", "");
							Schema<?> schemaRiferito = null;
							Iterator<String> itKeys = api.getApi().getComponents().getSchemas().keySet().iterator();
							while (itKeys.hasNext()) {
								String key = (String) itKeys.next();
								if(key.equals(checkRef)) {
									schemaRiferito = api.getApi().getComponents().getSchemas().get(key);
									break;
								}
							}
							if(schemaRiferito==null) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: ref '"+ref+"' non presente tra gli schemi definiti come componenti");
								}
								else {
									return null;
								}
							}
							else {
								return getParameterSchemaTypeRestriction(schemaRiferito, null, name, 
										arrayParameter,
										style, 
										explode, 
										api);
							}
						}
					}
				}
			}
			else {
				return null; // schema non trovato.
			}
						
		}

		if(schema.get$ref() != null) {
			return getParameterSchemaTypeRestriction(schema, schema.get$ref(), name, 
					arrayParameter,
					style, 
					explode, 
					api);
		}
		
		if(schema instanceof ArraySchema) {
			return getParameterSchemaTypeRestriction(((ArraySchema)schema).getItems(), null, name, 
					true,
					style, 
					explode, 
					api); 
		}
		
		return this.convertTo(schema, arrayParameter, style, explode);
	}
	*/
	
	private ApiParameterSchema getParameterSchema(Schema<?> schema, String ref, String name, 
			Boolean arrayParameter, String style, Boolean explode, OpenapiApi api) {
		if(ref != null) {
			boolean external = false;
			if(ref.contains("#")) {
				external = !ref.trim().startsWith("#");
				ref = ref.substring(ref.indexOf("#"));
			}
			
			boolean refHeaders = ref.startsWith("#/components/headers/");
			boolean refParameters = ref.startsWith("#/components/parameters/"); 
			boolean refSchema = ref.startsWith("#/components/schemas/"); 
			if(refHeaders || refParameters || refSchema) {
				if(api.getApi()==null) {
					throw new RuntimeException("Parametro '"+name+"' non corretto: api da cui risolvere la ref '"+ref+"' non trovata");
				}
				else {
					if(api.getApi().getComponents()==null) {
						if(!external || this.resolveExternalRef) {
							throw new RuntimeException("Parametro '"+name+"' non corretto: componenti, sui cui risolvere la ref '"+ref+"', non presenti");
						}
						else {
							ApiParameterSchema aps = new ApiParameterSchema();
							aps.addType(ref, null);
							return aps;
						}
					}
					else {
						if(refHeaders) {
							if(api.getApi().getComponents().getHeaders()==null || api.getApi().getComponents().getHeaders().size()<=0) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: headers definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
								}
								else {
									ApiParameterSchema aps = new ApiParameterSchema();
									aps.addType(ref, null);
									return aps;
								}
							}
							String checkRef = ref.trim().replaceAll("#/components/headers/", "");
							Header hdr = null;
							Iterator<String> itKeys = api.getApi().getComponents().getHeaders().keySet().iterator();
							while (itKeys.hasNext()) {
								String key = (String) itKeys.next();
								if(key.equals(checkRef)) {
									hdr = api.getApi().getComponents().getHeaders().get(key);
									break;
								}
							}
							if(hdr==null) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: ref '"+ref+"' non presente tra gli headers definiti come componenti");
								}
								else {
									ApiParameterSchema aps = new ApiParameterSchema();
									aps.addType(ref, null);
									return aps;
								}
							}
							else {
								return getParameterSchema(hdr.getSchema(), hdr.get$ref(), name, 
													arrayParameter,
													hdr.getStyle()!=null ? hdr.getStyle().toString(): null,
													hdr.getExplode(),
													api);
							}
						}
						else if(refParameters) {
							if(api.getApi().getComponents().getParameters()==null || api.getApi().getComponents().getParameters().size()<=0) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: parametri definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
								}
								else {
									ApiParameterSchema aps = new ApiParameterSchema();
									aps.addType(ref, null);
									return aps;
								}
							}
							String checkRef = ref.trim().replaceAll("#/components/parameters/", "");
							Parameter param = null;
							Iterator<String> itKeys = api.getApi().getComponents().getParameters().keySet().iterator();
							while (itKeys.hasNext()) {
								String key = (String) itKeys.next();
								if(key.equals(checkRef)) {
									param = api.getApi().getComponents().getParameters().get(key);
									break;
								}
							}
							if(param==null) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: ref '"+ref+"' non presente tra i parametri definiti come componenti");
								}
								else {
									ApiParameterSchema aps = new ApiParameterSchema();
									aps.addType(ref, null);
									return aps;
								}
							}
							else {
								if(name==null && param.getName()!=null) {
									name = param.getName();
								}
								return getParameterSchema(param.getSchema(), param.get$ref(), name, 
												arrayParameter,
												param.getStyle()!=null ? param.getStyle().toString(): null,
												param.getExplode(),
												api);
							}
						}
						else {
							if(api.getApi().getComponents().getSchemas()==null || api.getApi().getComponents().getSchemas().size()<=0) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: schemi definiti come componenti, sui cui risolvere la ref '"+ref+"', non presenti");
								}
								else {
									ApiParameterSchema aps = new ApiParameterSchema();
									aps.addType(ref, null);
									return aps;
								}
							}
							String checkRef = ref.trim().replaceAll("#/components/schemas/", "");
							Schema<?> schemaRiferito = null;
							Iterator<String> itKeys = api.getApi().getComponents().getSchemas().keySet().iterator();
							while (itKeys.hasNext()) {
								String key = (String) itKeys.next();
								if(key.equals(checkRef)) {
									schemaRiferito = api.getApi().getComponents().getSchemas().get(key);
									break;
								}
							}
							if(schemaRiferito==null) {
								if(!external || this.resolveExternalRef) {
									throw new RuntimeException("Parametro '"+name+"' non corretto: ref '"+ref+"' non presente tra gli schemi definiti come componenti");
								}
								else {
									ApiParameterSchema aps = new ApiParameterSchema();
									aps.addType(ref, null);
									return aps;
								}
							}
							else {
								return getParameterSchema(schemaRiferito, null, name, 
										arrayParameter,
										style, 
										explode, 
										api);
							}
						}
					}
				}
			}
			else {
				// i requestBodies e le response non dovrebbero rientrare in questo metodo
				String _type = ref.replaceAll("#/components/schemas/", "").replaceAll("#/definitions/", "");
				ApiSchemaTypeRestriction _schema = null; // schema non trovato.
				ApiParameterSchema aps = new ApiParameterSchema();
				aps.addType(_type, _schema);
				return aps;
			}
						
		}

		if(schema==null) {
			throw new RuntimeException("Parametro '"+name+"' non corretto: schema non definito");
		}
		
		if(schema.get$ref() != null) {
			return getParameterSchema(schema, schema.get$ref(), name, 
					arrayParameter,
					style, 
					explode, 
					api);
		}
		
		if(schema instanceof ArraySchema) {
			return getParameterSchema(((ArraySchema)schema).getItems(), null, name, 
					true,
					style, 
					explode, 
					api); 
		}
		
		if(schema instanceof ComposedSchema) {
			ComposedSchema cs = (ComposedSchema) schema;
			if(cs.getAnyOf()!=null && !cs.getAnyOf().isEmpty()) {
				ApiParameterSchema aps = new ApiParameterSchema();
				aps.setComplexType(ApiParameterSchemaComplexType.anyOf);
				for (Schema<?> apiSchemaAnyOf : cs.getAnyOf()) {
					String _type = null;
					if(apiSchemaAnyOf.getFormat() != null) {
						_type = apiSchemaAnyOf.getFormat();
					} else {
						_type = apiSchemaAnyOf.getType();
					}
					ApiSchemaTypeRestriction _schema = this.convertTo(apiSchemaAnyOf, arrayParameter, style, explode);
					aps.addType(_type, _schema);
				}
				return aps;
			}
			else if(cs.getAllOf()!=null && !cs.getAllOf().isEmpty()) {
				ApiParameterSchema aps = new ApiParameterSchema();
				aps.setComplexType(ApiParameterSchemaComplexType.allOf);
				for (Schema<?> apiSchemaAllOf : cs.getAllOf()) {
					String _type = null;
					if(apiSchemaAllOf.getFormat() != null) {
						_type = apiSchemaAllOf.getFormat();
					} else {
						_type = apiSchemaAllOf.getType();
					}
					ApiSchemaTypeRestriction _schema = this.convertTo(apiSchemaAllOf, arrayParameter, style, explode);
					aps.addType(_type, _schema);
				}
				return aps;
			}
			else if(cs.getOneOf()!=null && !cs.getOneOf().isEmpty()) {
				ApiParameterSchema aps = new ApiParameterSchema();
				aps.setComplexType(ApiParameterSchemaComplexType.oneOf);
				for (Schema<?> apiSchemaOneOf : cs.getOneOf()) {
					String _type = null;
					if(apiSchemaOneOf.getFormat() != null) {
						_type = apiSchemaOneOf.getFormat();
					} else {
						_type = apiSchemaOneOf.getType();
					}
					ApiSchemaTypeRestriction _schema = this.convertTo(apiSchemaOneOf, arrayParameter, style, explode);
					aps.addType(_type, _schema);
				}
				return aps;
			}
		}
		
		String _type = null;
		if(schema.getFormat() != null) {
			_type = schema.getFormat();
		} else {
			_type = schema.getType();
		}
		if(_type==null && schema.getTypes()!=null && !schema.getTypes().isEmpty()) {
			_type=schema.getTypes().iterator().next();
		}
		ApiSchemaTypeRestriction _schema = this.convertTo(schema, arrayParameter, style, explode);
		ApiParameterSchema aps = new ApiParameterSchema();
		aps.addType(_type, _schema);
		return aps;
	}

	private ApiSchemaTypeRestriction convertTo(Schema<?> schema, Boolean arrayParameter, String style, Boolean explode) {
		ApiSchemaTypeRestriction schemaTypeRestriction = new ApiSchemaTypeRestriction();
		schemaTypeRestriction.setSchema(schema);
		schemaTypeRestriction.setType(schema.getType());
		schemaTypeRestriction.setFormat(schema.getFormat());
		
		schemaTypeRestriction.setMinimum(schema.getMinimum());
		schemaTypeRestriction.setExclusiveMinimum(schema.getExclusiveMinimum());
		schemaTypeRestriction.setMaximum(schema.getMaximum());
		schemaTypeRestriction.setExclusiveMaximum(schema.getExclusiveMaximum());
		
		schemaTypeRestriction.setMultipleOf(schema.getMultipleOf());

		schemaTypeRestriction.setMinLength(schema.getMinLength()!=null ? Long.valueOf(schema.getMinLength()) : null);
		schemaTypeRestriction.setMaxLength(schema.getMaxLength()!=null ? Long.valueOf(schema.getMaxLength()) : null);
	
		schemaTypeRestriction.setPattern(schema.getPattern());

		schemaTypeRestriction.setEnumValues(schema.getEnum());
		
		schemaTypeRestriction.setArrayParameter(arrayParameter);
		schemaTypeRestriction.setStyle(style);
		if(explode!=null) {
			schemaTypeRestriction.setExplode(explode.booleanValue()+"");
		}
		
		return schemaTypeRestriction;
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
		
		if(response.get$ref()!=null) {
			
			String ref = response.get$ref();
			boolean external = false;
			if(ref.contains("#")) {
				external = !ref.trim().startsWith("#");
				ref = ref.substring(ref.indexOf("#"));
			}
			ref = ref.trim().replaceAll("#/components/responses/", "").replaceAll("#/definitions/", "");
			
			if(api.getApi()==null) {
				throw new RuntimeException("Stato non corretto ["+responseK+"]: api da cui risolvere la ref '"+response.get$ref()+"' non trovata");
			}
			if(api.getApi().getComponents()==null) {
				if(!external || this.resolveExternalRef) {
					throw new RuntimeException("Stato non corretto ["+responseK+"]: componenti, sui cui risolvere la ref '"+response.get$ref()+"', non presenti");
				}
			}
			else {
				if(api.getApi().getComponents().getResponses()==null || api.getApi().getComponents().getResponses().size()<=0) {
					if(!external || this.resolveExternalRef) {
						throw new RuntimeException("Stato non corretto ["+responseK+"]: risposte definite come componenti, sui cui risolvere la ref '"+response.get$ref()+"', non presenti");
					}
				}
				else {
					boolean find = false;
					Iterator<String> itKeys = api.getApi().getComponents().getResponses().keySet().iterator();
					while (itKeys.hasNext()) {
						String key = (String) itKeys.next();
						if(key.equals(ref)) {
							response = api.getApi().getComponents().getResponses().get(key);
							find = true;
							break;
						}
					}
					if(!find) {
						if(!external || this.resolveExternalRef) {
							throw new RuntimeException("Stato non corretto ["+responseK+"]: ref '"+response.get$ref()+"' non presente tra le risposte definite come componenti");
						}
					}
				}
			}
		}
		
		apiResponse.setDescription(response.getDescription());

		if(response.getHeaders() != null) {
			for(String header: response.getHeaders().keySet()) {
				Header property = response.getHeaders().get(header);
				
				ApiParameterSchema apiParameterSchema = getParameterSchema(property.getSchema(), property.get$ref(), header, 
								null,
								property.getStyle()!=null ? property.getStyle().toString(): null,
								property.getExplode(),
								api);
				
				if(this.debug) {
					System.out.println("=======================================");
					System.out.println("RESPONSE ("+method+" "+path+") name ["+header+"] required["+property.getRequired()+"] className["+property.getClass().getName()+"] ref["+property.get$ref()+"] apiParameterSchema["+apiParameterSchema+"]");
					System.out.println("=======================================");
				}
				
				ApiHeaderParameter parameter = new ApiHeaderParameter(header, apiParameterSchema);
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
				if(schema!=null && schema.get$ref()!= null) {
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
