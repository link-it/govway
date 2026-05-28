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

	/* Engine names — usati come case label degli switch su String e come argomento di {@link #newApi*}. */
	private static final String ENGINE_JSON_SCHEMA = "json_schema";
	private static final String ENGINE_OPENAPI4J = "openapi4j";
	private static final String ENGINE_SWAGGER_REQUEST_VALIDATOR = "swagger_request_validator";
	private static final String ENGINE_KAPPA = "kappa";

	/* Package base condiviso da tutte le implementazioni engine-specific. */
	private static final String VALIDATOR_PACKAGE_PREFIX = "org.openspcoop2.utils.openapi.validator.";

	/* Legacy {@code Validator} multi-formato, usato dal vecchio {@link #newApiValidator(ApiFormats)} per entrambi i formati. */
	private static final String CLASS_LEGACY_VALIDATOR = VALIDATOR_PACKAGE_PREFIX + "Validator";


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
				return (IApiValidator) loader.newInstance(CLASS_LEGACY_VALIDATOR);
			case OPEN_API_3:
				return (IApiValidator) loader.newInstance(CLASS_LEGACY_VALIDATOR);
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
			switch(engine) {
			case ENGINE_JSON_SCHEMA:
				return (IApiValidator) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "json_schema.JsonSchemaRequestValidator");
			case ENGINE_OPENAPI4J:
				return (IApiValidator) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "openapi4j.Openapi4jRequestValidator");
			case ENGINE_SWAGGER_REQUEST_VALIDATOR:
				return (IApiValidator) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "swagger.SwaggerRequestValidatorEngine");
			case ENGINE_KAPPA:
				return (IApiValidator) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "kappa.KappaRequestValidator");
			default:
				return (IApiValidator) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + engine + ".Validator");
			}
		} catch(Exception t){
			throw new ProcessingException(t.getMessage(),t);
		}
	}

	public static ApiValidatorConfig newApiValidatorConfig(String engine) throws ProcessingException{
		checkPackageName(engine);

		Loader loader = new Loader();
		try{
			switch(engine) {
			case ENGINE_JSON_SCHEMA:
				return (ApiValidatorConfig) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "json_schema.ValidatorConfig");
			case ENGINE_OPENAPI4J:
				return (ApiValidatorConfig) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "openapi4j.ValidatorConfig");
			case ENGINE_SWAGGER_REQUEST_VALIDATOR:
				return (ApiValidatorConfig) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "swagger.ValidatorConfig");
			case ENGINE_KAPPA:
				return (ApiValidatorConfig) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "kappa.ValidatorConfig");
			default:
				return (ApiValidatorConfig) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + engine + ".ValidatorConfig");
			}
		} catch(Exception t){
			throw new ProcessingException(t.getMessage(),t);
		}
	}

	public static IApiSpecValidator newApiSpecValidator(String engine) {
		if (engine == null || BaseSpecConfig.ENGINE.equals(engine)) {
			return new BaseSpecValidator();
		}
		try {
			checkPackageName(engine);
			Loader loader = new Loader();
			switch (engine) {
			case ENGINE_OPENAPI4J:
				return (IApiSpecValidator) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "openapi4j.Openapi4jSpecValidator");
			case ENGINE_KAPPA:
				return (IApiSpecValidator) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "kappa.KappaSpecValidator");
			case ENGINE_SWAGGER_REQUEST_VALIDATOR:
				return (IApiSpecValidator) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "swagger.SwaggerSpecValidator");
			default:
				return (IApiSpecValidator) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + engine + ".SpecValidator");
			}
		} catch (Exception t) {
			return new BaseSpecValidator();
		}
	}

	public static IApiSpecConfig newApiSpecValidatorConfig(String engine) {
		if (engine == null || BaseSpecConfig.ENGINE.equals(engine)) {
			return new BaseSpecConfig();
		}
		try {
			checkPackageName(engine);
			Loader loader = new Loader();
			switch (engine) {
			case ENGINE_OPENAPI4J:
				return (IApiSpecConfig) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "openapi4j.Openapi4jSpecConfig");
			case ENGINE_KAPPA:
				return (IApiSpecConfig) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "kappa.KappaSpecConfig");
			case ENGINE_SWAGGER_REQUEST_VALIDATOR:
				return (IApiSpecConfig) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + "swagger.SwaggerSpecConfig");
			default:
				return (IApiSpecConfig) loader.newInstance(VALIDATOR_PACKAGE_PREFIX + engine + ".SpecConfig");
			}
		} catch (Exception t) {
			return new BaseSpecConfig();
		}
	}

}
