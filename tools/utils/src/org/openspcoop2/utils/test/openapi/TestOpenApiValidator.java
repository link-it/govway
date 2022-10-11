/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
 * TestOpenApiValidator
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestOpenApiValidator {

	private static final String ID_TEST = "OpenAPI-Validator";
	private static final String ID_TEST_BIG = "OpenAPI-Validator-BIG";
	private static boolean mergeSpec = true;
	
	@DataProvider(name="openAPIValidatorProvider")
	public Object[][] provider(){
		return new Object[][]{
				{"json", OpenAPILibrary.json_schema, !mergeSpec},
				{"yaml", OpenAPILibrary.json_schema, !mergeSpec},
				
				{"json", OpenAPILibrary.openapi4j, mergeSpec},
				{"yaml", OpenAPILibrary.openapi4j, mergeSpec},
				{"json", OpenAPILibrary.openapi4j, !mergeSpec},
				{"yaml", OpenAPILibrary.openapi4j, !mergeSpec},
				
				{"json", OpenAPILibrary.swagger_request_validator, mergeSpec},
				{"yaml", OpenAPILibrary.swagger_request_validator, mergeSpec},
				{"json", OpenAPILibrary.swagger_request_validator, !mergeSpec},
				{"yaml", OpenAPILibrary.swagger_request_validator, !mergeSpec}
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="openAPIValidatorProvider")
	public void testOpenApiValidator(String tipoInterfaccia, OpenAPILibrary openAPILibrary, boolean mergeSpec) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' (interfaccia:"+tipoInterfaccia+" openAPILibrary:"+openAPILibrary+" mergeSpec:"+mergeSpec+") ...");
		org.openspcoop2.utils.openapi.validator.TestOpenApi3.main(new String[] {tipoInterfaccia, openAPILibrary.toString(), mergeSpec+""});
		TestLogger.info("Run test '"+ID_TEST+"' (interfaccia:"+tipoInterfaccia+" openAPILibrary:"+openAPILibrary+" mergeSpec:"+mergeSpec+") ok");
		
	}
	
	
	@DataProvider(name="openAPI3ExtendedValidatorProvider")
	public Object[][] extendedProvider(){
		return new Object[][]{
				{OpenAPILibrary.json_schema, !mergeSpec},
				{OpenAPILibrary.json_schema, mergeSpec},
				
				{OpenAPILibrary.openapi4j, !mergeSpec},
				{OpenAPILibrary.openapi4j, mergeSpec},
				
				{OpenAPILibrary.swagger_request_validator, !mergeSpec},
				{OpenAPILibrary.swagger_request_validator, mergeSpec},
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="openAPI3ExtendedValidatorProvider")
	public void testOpenApi3ExtendedValidator(OpenAPILibrary openAPILibrary, boolean mergeSpec) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' (openAPILibrary:"+openAPILibrary+" mergeSpec:"+mergeSpec+") ...");
		org.openspcoop2.utils.openapi.validator.TestOpenApi3Extended.main(new String[] {openAPILibrary.toString(), mergeSpec+""});
		TestLogger.info("Run test '"+ID_TEST+"' (openAPILibrary:"+openAPILibrary+" mergeSpec:"+mergeSpec+") ok");
		
	}
	
	
	
	@DataProvider(name="openAPI3BigInterfaceValidatorProvider")
	public Object[][] biggInterfaceProvider(){
		return new Object[][]{
				{OpenAPILibrary.swagger_request_validator, !mergeSpec},
				{OpenAPILibrary.swagger_request_validator, mergeSpec},
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="openAPI3BigInterfaceValidatorProvider")
	public void testOpenApi3BigInterfaceValidator(OpenAPILibrary openAPILibrary, boolean mergeSpec) throws Exception{
		
		boolean allTestExecution = true;
		
		TestLogger.info("Run test '"+ID_TEST+"' (openAPILibrary:"+openAPILibrary+" mergeSpec:"+mergeSpec+") ...");
		org.openspcoop2.utils.openapi.validator.TestInterfaceBigger.main(new String[] {openAPILibrary.toString(), mergeSpec+"", allTestExecution+""});
		TestLogger.info("Run test '"+ID_TEST+"' (openAPILibrary:"+openAPILibrary+" mergeSpec:"+mergeSpec+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS+"."+ID_TEST_BIG},dataProvider="openAPI3BigInterfaceValidatorProvider")
	public void testOpenApi3BigInterfaceValidatorSingleTest(OpenAPILibrary openAPILibrary, boolean mergeSpec) throws Exception{
		
		// Serve per i tempi attesi
		
		boolean allTestExecution = false;
		
		TestLogger.info("Run test '"+ID_TEST+"' (openAPILibrary:"+openAPILibrary+" mergeSpec:"+mergeSpec+") ...");
		org.openspcoop2.utils.openapi.validator.TestInterfaceBigger.main(new String[] {openAPILibrary.toString(), mergeSpec+"", allTestExecution+""});
		TestLogger.info("Run test '"+ID_TEST+"' (openAPILibrary:"+openAPILibrary+" mergeSpec:"+mergeSpec+") ok");
		
	}
	
	
	
}
