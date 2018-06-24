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



package org.openspcoop2.utils.rest;

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
		Loader loader = new Loader();
		try{
			switch (format) {
			case WADL:
				return (IApiReader) loader.newInstance("org.openspcoop2.utils.wadl.WADLApiReader");
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
		Loader loader = new Loader();
		try{
			switch (format) {
			case WADL:
				return (IApiValidator) loader.newInstance("org.openspcoop2.utils.wadl.validator.Validator");
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

}
