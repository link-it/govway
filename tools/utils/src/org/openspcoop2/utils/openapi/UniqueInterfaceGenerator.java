/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import org.slf4j.Logger;

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
		}catch(Exception e){
			// ext undefined
		}
		config.yaml = "yaml".equalsIgnoreCase(ext);
		HashMap<String,String> attachments = new HashMap<>();
		File fDir = new File(args[3].trim());
		if(fDir.isDirectory()==false) {
			throw new Exception("attachmentsDir ["+fDir.getAbsolutePath()+"] is not directory");
		}
		File[] files = fDir.listFiles();
		if(files!=null) {
			for (int j = 0; j < files.length; j++) {
				if(files[j].getName().equals(fMaster.getName())) {
					continue;
				}
				if(files[j].isDirectory()) {
	                continue;
	            }
				//System.out.println("READ ["+files[j]+"] ... ");
				attachments.put(files[j].getName(), FileSystemUtilities.readFile(files[j]));
				//System.out.println("READ ["+files[j]+"] ok");
			}
		}
		config.attachments = attachments;
		
		List<String> blackListParameters = null;
		List<String> blackListComponents = null;
		if(args.length>5) {
			
			String blackListParametersArgs = args[4].trim();
			if(blackListParametersArgs!=null) {
				blackListParameters = new ArrayList<>();
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
				blackListComponents = new ArrayList<>();
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
		
		generate(fileDest, config, blackListParameters, blackListComponents, true, null);
	}

	private static void debug(boolean debug, Logger log, String msg) {
		if(debug) {
			if(log!=null) {
				log.debug(msg);	
			}
			else {
				System.out.println(msg);
			}
		}
	}
	
	public static void generate(String fileDest, UniqueInterfaceGeneratorConfig config, 
			List<String> blackListParameters, List<String> blackListComponents,
			boolean debug, Logger log) throws Exception {
		String schemaRebuild = generate(config, blackListParameters, blackListComponents, debug, log);
		try(FileOutputStream fout = new FileOutputStream(fileDest)){
			fout.write(schemaRebuild.getBytes());
			fout.flush();
		}
	}
	public static String generate(UniqueInterfaceGeneratorConfig config, 
			List<String> blackListParameters, List<String> blackListComponents,
			boolean debug, Logger log) throws Exception {
		
		SwaggerParseResult pr = null;
		ParseOptions parseOptions = new ParseOptions();
		
		if(ApiFormats.SWAGGER_2.equals(config.format)) {
			pr = new SwaggerConverter().readContents(config.master, null, parseOptions);	
		}
		else {
			pr = new OpenAPIV3Parser().readContents(config.master, null, parseOptions);
		}
		StringBuilder sbParseWarningResult = new StringBuilder();
		OpenAPI api = AbstractOpenapiApiReader.parseResult(LoggerWrapperFactory.getLogger(UniqueInterfaceGenerator.class), pr, sbParseWarningResult);
		if(api.getComponents()==null) {
			api.setComponents(new Components());
		}
		
		Map<String,String> attachments = config.attachments;
		Iterator<String> attachmentNames = attachments.keySet().iterator();
		while (attachmentNames.hasNext()) {
			String attachName = (String) attachmentNames.next();
			String attach = attachments.get(attachName);
		
			debug(debug,log,"Merge ["+attachName+"] ...");
			if(ApiFormats.SWAGGER_2.equals(config.format)) {
				pr = new SwaggerConverter().readContents(attach, null, parseOptions);	
			}
			else {
				pr = new OpenAPIV3Parser().readContents(attach, null, parseOptions);
			}
			OpenAPI apiInternal = AbstractOpenapiApiReader.parseResult(LoggerWrapperFactory.getLogger(UniqueInterfaceGenerator.class), pr, sbParseWarningResult);
			if(apiInternal.getComponents()!=null) {
				if(apiInternal.getComponents().getCallbacks()!=null) {
					Map<String, Callback> maps = apiInternal.getComponents().getCallbacks();
					int mapsSize = 0;
					if(maps!=null && !maps.isEmpty()) {
						mapsSize = maps.size();
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							Callback value = maps.get(key);
							api.getComponents().addCallbacks(key, value);
						}
					}
					debug(debug,log,"\t"+mapsSize+" callback");
				}
				if(apiInternal.getComponents().getExamples()!=null) {
					Map<String, Example> maps = apiInternal.getComponents().getExamples();
					int mapsSize = 0;
					if(maps!=null && !maps.isEmpty()) {
						mapsSize = maps.size();
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							Example value = maps.get(key);
							api.getComponents().addExamples(key, value);
						}
					}
					debug(debug,log,"\t"+mapsSize+" example");
				}
				if(apiInternal.getComponents().getExtensions()!=null) {
					Map<String, Object> maps = apiInternal.getComponents().getExtensions();
					int mapsSize = 0;
					if(maps!=null && !maps.isEmpty()) {
						mapsSize = maps.size();
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							Object value = maps.get(key);
							api.getComponents().addExtension(key, value);
						}
					}
					debug(debug,log,"\t"+mapsSize+" extensions");
				}
				if(apiInternal.getComponents().getHeaders()!=null) {
					Map<String, Header> maps = apiInternal.getComponents().getHeaders();
					int mapsSize = 0;
					if(maps!=null && !maps.isEmpty()) {
						mapsSize = maps.size();
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							Header value = maps.get(key);
							api.getComponents().addHeaders(key, value);
						}
					}
					debug(debug,log,"\t"+mapsSize+" header");
				}
				if(apiInternal.getComponents().getLinks()!=null) {
					Map<String, Link> maps = apiInternal.getComponents().getLinks();
					int mapsSize = 0;
					if(maps!=null && !maps.isEmpty()) {
						mapsSize = maps.size();
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							Link value = maps.get(key);
							api.getComponents().addLinks(key, value);
						}
					}
					debug(debug,log,"\t"+mapsSize+" link");
				}
				if(apiInternal.getComponents().getParameters()!=null) {
					Map<String, Parameter> maps = apiInternal.getComponents().getParameters();
					int mapsSize = 0;
					if(maps!=null && !maps.isEmpty()) {
						mapsSize = maps.size();
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							if(blackListParameters!=null && blackListParameters.contains(key)) {
								debug(debug,log,"Parameter '"+key+"' skipped");
								continue;
							}
							Parameter value = maps.get(key);
							api.getComponents().addParameters(key, value);
						}
					}
					debug(debug,log,"\t"+mapsSize+" parameter");
				}
				if(apiInternal.getComponents().getRequestBodies()!=null) {
					Map<String, RequestBody> maps = apiInternal.getComponents().getRequestBodies();
					int mapsSize = 0;
					if(maps!=null && !maps.isEmpty()) {
						mapsSize = maps.size();
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							RequestBody value = maps.get(key);
							api.getComponents().addRequestBodies(key, value);
						}
					}
					debug(debug,log,"\t"+mapsSize+" requestBody");
				}
				if(apiInternal.getComponents().getResponses()!=null) {
					Map<String, ApiResponse> maps = apiInternal.getComponents().getResponses();
					int mapsSize = 0;
					if(maps!=null && !maps.isEmpty()) {
						mapsSize = maps.size();
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							ApiResponse value = maps.get(key);
							api.getComponents().addResponses(key, value);
						}
					}
					debug(debug,log,"\t"+mapsSize+"] response");
				}
				if(apiInternal.getComponents().getSchemas()!=null) {
					@SuppressWarnings("rawtypes")
					Map<String, Schema> maps = apiInternal.getComponents().getSchemas();
					int mapsSize = 0;
					if(maps!=null && !maps.isEmpty()) {
						mapsSize = maps.size();
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							if(blackListComponents!=null && blackListComponents.contains(key)) {
								debug(debug,log,"Component '"+key+"' skipped");
								continue;
							}
							Schema<?> value = maps.get(key);
							api.getComponents().addSchemas(key, value);
						}
					}
					debug(debug,log,"\t"+mapsSize+" schema");
				}
				if(apiInternal.getComponents().getSecuritySchemes()!=null) {
					Map<String, SecurityScheme> maps = apiInternal.getComponents().getSecuritySchemes();
					int mapsSize = 0;
					if(maps!=null && !maps.isEmpty()) {
						mapsSize = maps.size();
						Iterator<String> keys = maps.keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							SecurityScheme value = maps.get(key);
							api.getComponents().addSecuritySchemes(key, value);
						}
					}
					debug(debug,log,"\t"+mapsSize+" security schema");
				}
			}
			debug(debug,log,"Merge ["+attachName+"] ok");
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
					//debug(debug,log,"PARAMETRO *"+key+"* ["+value.getName()+"] ["+value.getExample()+"] ["+value.getExamples()+"] ref["+value.get$ref()+"] tipo["+value.getClass().getName()+"]");
					checkSchema(0,("Parameter-"+key), value.getSchema());
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
		
		// Faccio due passate, prima con i caratteri " e ' in modo da risolvere le ref precisamente,
		// poiche' l'algoritmo e' soggetto a problemi quando ci sono nomi inclusi in altri ref. Es.:
		// test.yaml#...
		// http://test/test.yaml#....
		schemaRebuild = replace(refPath, schemaRebuild, true);
		schemaRebuild = replace(refPath, schemaRebuild, false);
			
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
				debug(debug,log,"Description type unknown ["+oDescr.getClass().getName()+"]");
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
		
		return schemaRebuild;
		
	}

	private static String replace(List<String> refPath, String schemaRebuild, boolean usePrefixChar) {
		if(refPath!=null && !refPath.isEmpty()) {
			for (String ref : refPath) {
				
				//System.out.println("...............ANALIZZO REF ["+ref+"]");
				
				if(schemaRebuild.contains(ref)) {
					
					//System.out.println(" PROCESS ["+ref+"]");
					
					if(ref.startsWith("#")==false) {
						
						//System.out.println(" PROCESS INTERNAL ["+ref+"]");
						
						String destra = ref.substring(ref.indexOf("#"));
						String refForReplace = ref;
						
						//System.out.println("destra ["+destra+"]");
						//System.out.println("destra ["+refForReplace+"]");
						
						if(usePrefixChar) {
							String rep = "\""+refForReplace+"\"";
							String destraRep = "\""+destra+"\"";
							while(schemaRebuild.contains(rep)) {
								schemaRebuild = schemaRebuild.replace(rep, destraRep);
							}
							rep = "'"+refForReplace+"'";
							destraRep = "'"+destra+"'";
							while(schemaRebuild.contains(rep)) {
								schemaRebuild = schemaRebuild.replace(rep, destraRep);
							}
						}
						else {
							while(schemaRebuild.contains(refForReplace)) {
								schemaRebuild = schemaRebuild.replace(refForReplace, destra);
							}
						}
						
						if(refForReplace.startsWith("./") && refForReplace.length()>2) {
							
							//System.out.println("CASO SPECIALE!");
							
							refForReplace = refForReplace.substring(2);
							
							if(usePrefixChar) {
								String rep = "\""+refForReplace+"\"";
								String destraRep = "\""+destra+"\"";
								while(schemaRebuild.contains(rep)) {
									schemaRebuild = schemaRebuild.replace(rep, destraRep);
								}
								rep = "'"+refForReplace+"'";
								destraRep = "'"+destra+"'";
								while(schemaRebuild.contains(rep)) {
									schemaRebuild = schemaRebuild.replace(rep, destraRep);
								}
							}
							else {
								while(schemaRebuild.contains(refForReplace)) {
									schemaRebuild = schemaRebuild.replace(refForReplace, destra);
								}
							}
						}
						
					}
				}
			}
		}
		return schemaRebuild;
	}
	
	private static int checkSchema(int profondita, String sorgente, Schema<?> schema) {
		
		if(profondita>1000) {
			return profondita; // evitare stack overflow
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
				//debug(debug,log,"SCHEMA ("+sorgente+") *"+key+"* ["+value.getName()+"] ["+value.getType()+"] ["+value.getFormat()+"] ["+value.getExample()+"] ref["+value.get$ref()+"] schema["+value.getClass().getName()+"]");
				@SuppressWarnings("unused")
				int p = checkSchema((profondita+1),sorgenteInterno,value);
			}
		}
		
		return profondita;
	}
	
}
