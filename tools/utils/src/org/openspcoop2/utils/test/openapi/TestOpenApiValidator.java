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

package org.openspcoop2.utils.test.openapi;

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
	private static final boolean useOpenApi4j = true;
	
	@DataProvider(name="openAPIValidatorProvider")
	public Object[][] provider(){
		return new Object[][]{
				{"json", !useOpenApi4j},
				{"yaml", !useOpenApi4j},
				{"json", useOpenApi4j},
				{"yaml", useOpenApi4j}
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="openAPIValidatorProvider")
	public void testOpenApiValidator(String tipoInterfaccia, boolean useOpenApi4j) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' (interfaccia:"+tipoInterfaccia+" useOpenApi4j:"+useOpenApi4j+") ...");
		org.openspcoop2.utils.openapi.validator.TestOpenApi3.main(new String[] {tipoInterfaccia, useOpenApi4j+""});
		TestLogger.info("Run test '"+ID_TEST+"' (interfaccia:"+tipoInterfaccia+" useOpenApi4j:"+useOpenApi4j+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST})
	public void testOpenApi4jValidator() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' (openapi4j) ...");
		org.openspcoop2.utils.openapi.validator.TestOpenApi4j.main(new String[] {});
		TestLogger.info("Run test '"+ID_TEST+"' (openapi4j) ok");
		
	}
	
	
	
}
