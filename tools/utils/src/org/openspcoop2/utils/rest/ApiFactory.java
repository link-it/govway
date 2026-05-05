/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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



package org.openspcoop2.utils.rest;

import java.util.regex.Pattern;

import org.openspcoop2.utils.resources.Loader;
/**	
 * ApiFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class ApiFactory {
	

	public static IApiReader newApiReader(ApiFormats format) throws ProcessingException{
		if(format==null) {
			throw new ProcessingException("ApiFormat undefined");
		}
		Loader loader = new Loader();
		try{
			switch (format) {
			case SWAGGER_2:
				return (IApiReader) loader.newInstance("org.openspcoop2.utils.openapi.SwaggerApiReader");
			case OPEN_API_3:
				return (IApiReader) loader.newInstance("org.openspcoop2.utils.openapi.OpenapiApiReader");
			}
		}
		catch(Throwable t){
			throw new ProcessingException(t.getMessage(),t);
		}
		throw new ProcessingException("ApiFormat ["+format+"] unsupported");
	}
	
	public static IApiValidator newApiValidator(ApiFormats format) throws ProcessingException{
		if(format==null) {
			throw new ProcessingException("ApiFormat undefined");
		}
		Loader loader = new Loader();
		try{
			switch (format) {
			case SWAGGER_2:
				return (IApiValidator) loader.newInstance("org.openspcoop2.utils.openapi.validator.Validator");
			case OPEN_API_3:
				return (IApiValidator) loader.newInstance("org.openspcoop2.utils.openapi.validator.Validator");
			}
		}
		catch(Throwable t){
			throw new ProcessingException(t.getMessage(),t);
		}
		throw new ProcessingException("ApiFormat ["+format+"] unsupported");
	}
	
	private static final Pattern PACKAGE_NAME_REGEX = Pattern.compile("[a-zA-Z]+");
	private static void checkPackageName(String packageName) throws ProcessingException {
		if(packageName == null)
			throw new ProcessingException("engine name undefined");
		if (!PACKAGE_NAME_REGEX.matcher(packageName).find())
			throw new ProcessingException("Invalid validator engine name");
	}
	
	public static IApiValidator newApiValidator(String engine) throws ProcessingException {
		checkPackageName(engine);
		
		Loader loader = new Loader();
		try{
			return switch(engine) {
			case "json_schema" -> (IApiValidator) loader.newInstance("org.openspcoop2.utils.openapi.validator.json_schema.Validator");
			case "openapi4j" -> (IApiValidator) loader.newInstance("org.openspcoop2.utils.openapi.validator.openapi4j.Validator");
			case "swagger_request_validator" -> (IApiValidator) loader.newInstance("org.openspcoop2.utils.openapi.validator.swagger.Validator");
			case "kappa" -> (IApiValidator) loader.newInstance("org.openspcoop2.utils.openapi.validator.kappa.Validator");
			default -> (IApiValidator) loader.newInstance("org.openspcoop2.utils.openapi.validator." + engine + ".Validator");
			};
		} catch(Exception t){
			throw new ProcessingException(t.getMessage(),t);
		}
	}
	
	public static ApiValidatorConfig newApiValidatorConfig(String engine) throws ProcessingException{
		checkPackageName(engine);
		
		Loader loader = new Loader();
		try{
			return switch(engine) {
			case "json_schema" -> (ApiValidatorConfig) loader.newInstance("org.openspcoop2.utils.openapi.validator.json_schema.ValidatorConfig");
			case "openapi4j" -> (ApiValidatorConfig) loader.newInstance("org.openspcoop2.utils.openapi.validator.openapi4j.ValidatorConfig");
			case "swagger_request_validator" -> (ApiValidatorConfig) loader.newInstance("org.openspcoop2.utils.openapi.validator.swagger.ValidatorConfig");
			case "kappa" -> (ApiValidatorConfig) loader.newInstance("org.openspcoop2.utils.openapi.validator.kappa.ValidatorConfig");
			default -> (ApiValidatorConfig) loader.newInstance("org.openspcoop2.utils.openapi.validator." + engine + ".ValidatorConfig");
			};
		} catch(Exception t){
			throw new ProcessingException(t.getMessage(),t);
		}
	}

}
