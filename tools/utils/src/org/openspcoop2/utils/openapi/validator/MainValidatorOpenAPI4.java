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


package org.openspcoop2.utils.openapi.validator;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.IApiValidator;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.openspcoop2.utils.rest.api.ApiSchemaType;

/**
 * Test (Estende i test già effettuati in TestOpenApi3
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MainValidatorOpenAPI4 {

	public static void main(String[] args) throws Exception {
				
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		if(args==null || args.length<1) {
			throw new Exception("Usage: MainValidatorOpenAPI4 <file> [dirIncludes]");
		}
		
		String file = args[0].trim();
		File f = new File(file);
		if(f.exists()==false) {
			throw new Exception("File '"+f.getAbsolutePath()+"' not exists");
		}
		if(f.canRead()==false) {
			throw new Exception("File '"+f.getAbsolutePath()+"' cannot read");
		}
		
		File fDir = null;
		if(args.length>1) {
			fDir = new File(args[1].trim());
			if(fDir.exists()==false) {
				throw new Exception("File '"+fDir.getAbsolutePath()+"' not exists");
			}
			if(fDir.canRead()==false) {
				throw new Exception("File '"+fDir.getAbsolutePath()+"' cannot read");
			}
			if(fDir.isDirectory()==false) {
				throw new Exception("File '"+fDir.getAbsolutePath()+"' is not directory");
			}
		}
		
		boolean output = false;
		if(args.length>2) {
			output = Boolean.valueOf(args[2]);
		}
		
		try {
		
			URL url = f.toURI().toURL();
			
			List<ApiSchema> listApiSchema = null;
			if(fDir!=null) {
				for (File fChild : fDir.listFiles()) {
					if(fChild.exists()==false) {
						throw new Exception("File '"+fChild.getAbsolutePath()+"' not exists");
					}
					if(fChild.canRead()==false) {
						throw new Exception("File '"+fChild.getAbsolutePath()+"' cannot read");
					}
					
					// evito file master openapi se risiede nella stessa directory
					if(fChild.getName().equals(f.getName())){
						continue;
					}
					
					ApiSchema apiSchema = null;
					if(fChild.getName().toLowerCase().endsWith(".yaml")) {
						apiSchema= new ApiSchema(fChild.getName(), 
								FileSystemUtilities.readBytesFromFile(fChild),
								ApiSchemaType.YAML);
					}
					else if(fChild.getName().toLowerCase().endsWith(".json")) {
						apiSchema= new ApiSchema(fChild.getName(), 
								FileSystemUtilities.readBytesFromFile(fChild),
								ApiSchemaType.JSON);
					}
					if(apiSchema!=null) {
						if(listApiSchema==null) {
							listApiSchema = new ArrayList<ApiSchema>();
						}
						listApiSchema.add(apiSchema);
					}
				}
			}
			ApiSchema [] schemas = null;
			if(listApiSchema!=null && !listApiSchema.isEmpty()) {
				schemas = listApiSchema.toArray(new ApiSchema[1]);
			}
			
			IApiReader apiReader = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
			ApiReaderConfig config = new ApiReaderConfig();
			config.setProcessInclude(false);
			apiReader.init(LoggerWrapperFactory.getLogger(MainValidatorOpenAPI4.class), new File(url.toURI()), config, schemas);
			Api api = apiReader.read();
			IApiValidator apiValidator = ApiFactory.newApiValidator(ApiFormats.OPEN_API_3);
			OpenapiApiValidatorConfig configO = new OpenapiApiValidatorConfig();
			configO.setOpenApi4JConfig(new OpenapiApi4jValidatorConfig());
			configO.getOpenApi4JConfig().setUseOpenApi4J(true);
			
			apiValidator.init(LoggerWrapperFactory.getLogger(MainValidatorOpenAPI4.class), api, configO);
			
			System.err.println("Validazione effettuata con successo");
			
		}catch(Exception e) {
			if(output) {
				System.err.println("!ERRORE! Validazione fallita");
				e.printStackTrace(System.err);
			}
			else {
				throw e;
			}
		}
		
	}

}
