/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.ApiFormats;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.callbacks.Callback;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.links.Link;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.converter.SwaggerConverter;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

/**
 * UniqueInterfaceGenerator
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UniqueInterfaceGenerator {

	public static void main(String[] args) throws Exception {
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		if(args==null || args.length<4) {
			throw new Exception("Use: UniqueInterfaceGenerator <versioneOpenAPI> <destFile> <master> <attachmentsDir>");
		}
		
		String tipo = args[0].trim();
		ApiFormats format = ApiFormats.valueOf(tipo);
		
		String fileDest = args[1].trim();
		
		UniqueInterfaceGeneratorConfig config = new UniqueInterfaceGeneratorConfig();
		config.format = format;
		String fileMaster = args[2].trim();
		config.master =  FileSystemUtilities.readFile(fileMaster);
		File fMaster = new File(fileMaster);
		String ext = null;
		try{
			ext = fileMaster.substring(fileMaster.lastIndexOf(".")+1,fileMaster.length());
		}catch(Exception e){}
		config.yaml = "yaml".equalsIgnoreCase(ext);
		HashMap<String,String> attachments = new HashMap<>();
		File fDir = new File(args[3].trim());
		if(fDir.isDirectory()==false) {
			throw new Exception("attachmentsDir ["+fDir.getAbsolutePath()+"] is not directory");
		}
		File[] files = fDir.listFiles();
		for (int j = 0; j < files.length; j++) {
			if(files[j].getName().equals(fMaster.getName())) {
				continue;
			}
			//System.out.println("READ ["+files[j]+"] ... ");
			attachments.put(files[j].getName(), FileSystemUtilities.readFile(files[j]));
			//System.out.println("READ ["+files[j]+"] ok");
		}
		config.attachments = attachments;
		
		List<String> blackListParameters = null;
		List<String> blackListComponents = null;
		if(args.length>5) {
			
			String blackListParametersArgs = args[4].trim();
			if(blackListParametersArgs!=null) {
				blackListParameters = new ArrayList<String>();
				if(blackListParametersArgs.contains(",")) {
					String [] tmp = blackListParametersArgs.split(",");
					for (String s : tmp) {
						blackListParameters.add(s);
					}
				}else {
					blackListParameters.add(blackListParametersArgs);
				}
			}
			
			String blackListComponentsArgs = args[5].trim();
			if(blackListComponentsArgs!=null) {
				blackListComponents = new ArrayList<String>();
				if(blackListComponentsArgs.contains(",")) {
					String [] tmp = blackListComponentsArgs.split(",");
					for (String s : tmp) {
						blackListComponents.add(s);
					}
				}else {
					blackListComponents.add(blackListComponentsArgs);
				}
			}
			
		}
		
		generate(fileDest, config, blackListParameters, blackListComponents);
	}

	private static void generate(String fileDest, UniqueInterfaceGeneratorConfig config, 
			List<String> blackListParameters, List<String> blackListComponents) throws Exception {
		
		SwaggerParseResult pr = null;
		ParseOptions parseOptions = new ParseOptions();
		
		if(ApiFormats.SWAGGER_2.equals(config.format)) {
			pr = new SwaggerConverter().readContents(config.master, null, parseOptions);	
		}
		else {
			pr = new OpenAPIV3Parser().readContents(config.master, null, parseOptions);
		}
		OpenAPI api = AbstractOpenapiApiReader.parseResult(LoggerWrapperFactory.getLogger(UniqueInterfaceGenerator.class), pr);
		if(api.getComponents()==null) {
			api.setComponents(new Components());
		}
		
		HashMap<String,String> attachments = config.attachments;
		Iterator<String> attachmentNames = attachments.keySet().iterator();
		while (attachmentNames.hasNext()) {
			String attachName = (String) attachmentNames.next();
			String attach = attachments.get(attachName);
		
			System.out.println("Merge ["+attachName+"] ...");
			if(ApiFormats.SWAGGER_2.equals(config.format)) {
				pr = new SwaggerConverter().readContents(attach, null, parseOptions);	
			}
			else {
				pr = new OpenAPIV3Parser().readContents(attach, null, parseOptions);
			}
			OpenAPI apiInternal = AbstractOpenapiApiReader.parseResult(LoggerWrapperFactory.getLogger(UniqueInterfaceGenerator.class), pr);
			if(apiInternal.getComponents()!=null) {
				if(apiInternal.getComponents().getCallbacks()!=null) {
					Map<String, Callback> maps = apiInternal.getComponents().getCallbacks();
					if(maps!=null && !maps.isEmpty()) {
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							Callback value = maps.get(key);
							api.getComponents().addCallbacks(key, value);
						}
					}
					System.out.println("\t"+maps.size()+" callback");
				}
				if(apiInternal.getComponents().getExamples()!=null) {
					Map<String, Example> maps = apiInternal.getComponents().getExamples();
					if(maps!=null && !maps.isEmpty()) {
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							Example value = maps.get(key);
							api.getComponents().addExamples(key, value);
						}
					}
					System.out.println("\t"+maps.size()+" example");
				}
				if(apiInternal.getComponents().getExtensions()!=null) {
					Map<String, Object> maps = apiInternal.getComponents().getExtensions();
					if(maps!=null && !maps.isEmpty()) {
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							Object value = maps.get(key);
							api.getComponents().addExtension(key, value);
						}
					}
					System.out.println("\t"+maps.size()+" extensions");
				}
				if(apiInternal.getComponents().getHeaders()!=null) {
					Map<String, Header> maps = apiInternal.getComponents().getHeaders();
					if(maps!=null && !maps.isEmpty()) {
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							Header value = maps.get(key);
							api.getComponents().addHeaders(key, value);
						}
					}
					System.out.println("\t"+maps.size()+" header");
				}
				if(apiInternal.getComponents().getLinks()!=null) {
					Map<String, Link> maps = apiInternal.getComponents().getLinks();
					if(maps!=null && !maps.isEmpty()) {
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							Link value = maps.get(key);
							api.getComponents().addLinks(key, value);
						}
					}
					System.out.println("\t"+maps.size()+" link");
				}
				if(apiInternal.getComponents().getParameters()!=null) {
					Map<String, Parameter> maps = apiInternal.getComponents().getParameters();
					if(maps!=null && !maps.isEmpty()) {
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							if(blackListParameters!=null && blackListParameters.contains(key)) {
								System.out.println("Parameter '"+key+"' skipped");
								continue;
							}
							Parameter value = maps.get(key);
							api.getComponents().addParameters(key, value);
						}
					}
					System.out.println("\t"+maps.size()+" parameter");
				}
				if(apiInternal.getComponents().getRequestBodies()!=null) {
					Map<String, RequestBody> maps = apiInternal.getComponents().getRequestBodies();
					if(maps!=null && !maps.isEmpty()) {
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							RequestBody value = maps.get(key);
							api.getComponents().addRequestBodies(key, value);
						}
					}
					System.out.println("\t"+maps.size()+" requestBody");
				}
				if(apiInternal.getComponents().getResponses()!=null) {
					Map<String, ApiResponse> maps = apiInternal.getComponents().getResponses();
					if(maps!=null && !maps.isEmpty()) {
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							ApiResponse value = maps.get(key);
							api.getComponents().addResponses(key, value);
						}
					}
					System.out.println("\t"+maps.size()+"] response");
				}
				if(apiInternal.getComponents().getSchemas()!=null) {
					@SuppressWarnings("rawtypes")
					Map<String, Schema> maps = apiInternal.getComponents().getSchemas();
					if(maps!=null && !maps.isEmpty()) {
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							if(blackListComponents!=null && blackListComponents.contains(key)) {
								System.out.println("Component '"+key+"' skipped");
								continue;
							}
							Schema<?> value = maps.get(key);
							api.getComponents().addSchemas(key, value);
						}
					}
					System.out.println("\t"+maps.size()+" schema");
				}
				if(apiInternal.getComponents().getSecuritySchemes()!=null) {
					Map<String, SecurityScheme> maps = apiInternal.getComponents().getSecuritySchemes();
					if(maps!=null && !maps.isEmpty()) {
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							SecurityScheme value = maps.get(key);
							api.getComponents().addSecuritySchemes(key, value);
						}
					}
					System.out.println("\t"+maps.size()+" security schema");
				}
			}
			System.out.println("Merge ["+attachName+"] ok");
		}
		
		// clean attributi non permessi in swagger editor
		api.setExtensions(null);
		api.getComponents().setExtensions(null);
		if(api.getComponents().getHeaders()!=null) {
			Map<String, Header> maps = api.getComponents().getHeaders();
			if(maps!=null && !maps.isEmpty()) {
				Iterator<String> keys = maps.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					Header value = maps.get(key);
					value.setExplode(null);
					value.setStyle(null);
				}
			}
		}
		if(api.getComponents().getParameters()!=null) {
			Map<String, Parameter> maps = api.getComponents().getParameters();
			if(maps!=null && !maps.isEmpty()) {
				Iterator<String> keys = maps.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					Parameter value = maps.get(key);
					value.setExplode(null);
					value.setStyle(null);
					//System.out.println("PARAMETRO *"+key+"* ["+value.getName()+"] ["+value.getExample()+"] ["+value.getExamples()+"] ref["+value.get$ref()+"] tipo["+value.getClass().getName()+"]");
					checkSchema(0,"Parameter-"+key, value.getSchema());
				}
			}
		}
		if(api.getComponents().getSchemas()!=null) {
			@SuppressWarnings("rawtypes")
			Map<String, Schema> maps = api.getComponents().getSchemas();
			if(maps!=null && !maps.isEmpty()) {
				Iterator<String> keys = maps.keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					Schema<?> value = maps.get(key);
					String sorgente = "";
					if(value.getName()!=null) {
						sorgente = sorgente + value.getName();
					}
					else {
						sorgente = sorgente + "RootSchema";
					}
					checkSchema(0, sorgente, value);	
				}
			}
		}
		
		JsonNode jsonNode = null;
		String s = null;
		if(config.yaml) {
			s = YAMLUtils.getObjectWriter().writeValueAsString(api);
			jsonNode = YAMLUtils.getInstance().getAsNode(s);
		}
		else {
			s = JSONUtils.getObjectWriter().writeValueAsString(api);
			jsonNode = JSONUtils.getInstance().getAsNode(s);
		}
		
		JsonPathExpressionEngine engine = new JsonPathExpressionEngine();
		List<String> refPath = engine.getStringMatchPattern(jsonNode, "$..$ref");
		String schemaRebuild = s;
		if(refPath!=null && !refPath.isEmpty()) {
			for (String ref : refPath) {
				if(schemaRebuild.contains(ref)) {
					if(ref.startsWith("#")==false) {
						String destra = ref.substring(ref.indexOf("#"));
						String refForReplace = ref;
						
						while(schemaRebuild.contains(refForReplace)) {
							schemaRebuild = schemaRebuild.replace(refForReplace, destra);
						}
						
						if(refForReplace.startsWith("./") && refForReplace.length()>2) {
							refForReplace = refForReplace.substring(2);
							while(schemaRebuild.contains(refForReplace)) {
								schemaRebuild = schemaRebuild.replace(refForReplace, destra);
							}
						}
						
					}
				}
			}
		}
			
		/*
		Object oDescr = engine.getMatchPattern(jsonNode, "$.info.description", JsonPathReturnType.NODE);
		if(oDescr!=null) {
			String descr = null;
			if(oDescr instanceof List<?>) {
				@SuppressWarnings("unchecked")
				List<String> l = (List<String>) oDescr;
				if(!l.isEmpty()) {
					descr = l.get(0);
				}
			}
			else if(oDescr instanceof String) {
				descr = (String) oDescr;
			}
			else if(oDescr instanceof JsonNode) {
				JsonNode jN = (JsonNode) oDescr;
				descr = jN.asText();
			}
			else {
				System.out.println("Description type unknown ["+oDescr.getClass().getName()+"]");
			}
			if(descr!=null && org.apache.commons.lang.StringUtils.isNotEmpty(descr)) {
				schemaRebuild = schemaRebuild.replace("info:", "info:\n  x-summary: \""+descr+"\"");
			}
		}
		*/
		
		if(schemaRebuild.startsWith("---")) {
			schemaRebuild = schemaRebuild.substring("---".length());
		}
		if(schemaRebuild.startsWith("\n")) {
			schemaRebuild = schemaRebuild.substring("\n".length());
		}
		
		String extensions = "extensions:\n" +"    ";
		schemaRebuild = schemaRebuild.replace(extensions, "");
		String ext = "    x-";
		String extCorrect = "  x-";
		while(schemaRebuild.contains(ext)) {
			schemaRebuild = schemaRebuild.replace(ext, extCorrect);
		}
		
		try(FileOutputStream fout = new FileOutputStream(fileDest)){
			fout.write(schemaRebuild.getBytes());
			fout.flush();
		}
		
	}
	
	private static void checkSchema(int profondita, String sorgente, Schema<?> schema) {
		
		if(profondita>1000) {
			return; // evitare stack overflow
		}
		
		@SuppressWarnings("rawtypes")
		Map<String, Schema> properties = schema.getProperties();
		if(properties!=null && !properties.isEmpty()) {
			for (String key : properties.keySet()) {
				Schema<?> value = properties.get(key);
				String sorgenteInterno = sorgente+".";
				if(value.getName()!=null) {
					sorgenteInterno = sorgenteInterno + value.getName();
				}
				else {
					sorgenteInterno = sorgenteInterno + "schemaProfondita"+profondita;
				}
				//System.out.println("SCHEMA ("+sorgente+") *"+key+"* ["+value.getName()+"] ["+value.getType()+"] ["+value.getFormat()+"] ["+value.getExample()+"] ref["+value.get$ref()+"] schema["+value.getClass().getName()+"]");
				checkSchema((profondita+1),sorgenteInterno,value);
			}
		}
	}
}

class UniqueInterfaceGeneratorConfig {
	
	protected ApiFormats format;
	protected boolean yaml;
	protected String master;
	protected HashMap<String, String> attachments;
	
}
