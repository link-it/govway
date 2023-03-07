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

package org.openspcoop2.utils.test.openapi;

import org.openspcoop2.utils.openapi.validator.OpenAPILibrary;
import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestSwaggerValidator
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestSwaggerValidator {

	private static final String ID_TEST = "Swagger-Validator";
	private static boolean mergeSpec = true;
	
	@DataProvider(name="swaggerValidatorProvider")
	public Object[][] provider(){
		return new Object[][]{
			{"json", OpenAPILibrary.json_schema, mergeSpec},
			{"yaml", OpenAPILibrary.json_schema, mergeSpec},
			{"json", OpenAPILibrary.json_schema, !mergeSpec},
			{"yaml", OpenAPILibrary.json_schema, !mergeSpec},
			
			//{"json", OpenAPILibrary.swagger_request_validator, mergeSpec},
			//{"yaml", OpenAPILibrary.swagger_request_validator, mergeSpec},
			{"json", OpenAPILibrary.swagger_request_validator, !mergeSpec},
			{"yaml", OpenAPILibrary.swagger_request_validator, !mergeSpec}
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="swaggerValidatorProvider")
	public void testSwaggerValidator(String tipoInterfaccia, OpenAPILibrary openAPILibrary, boolean mergeSpec) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' (interfaccia:"+tipoInterfaccia+" openAPILibrary:"+openAPILibrary+" mergeSpec:"+mergeSpec+") ...");
		org.openspcoop2.utils.openapi.validator.test.Swagger2Test.main(new String[] {tipoInterfaccia, openAPILibrary.toString(), mergeSpec+""});
		TestLogger.info("Run test '"+ID_TEST+"' (interfaccia:"+tipoInterfaccia+" openAPILibrary:"+openAPILibrary+" mergeSpec:"+mergeSpec+") ok");
		
	}
	
}
