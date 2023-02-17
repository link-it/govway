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

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestSwaggerReader
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestSwaggerReader {

	private static final String ID_TEST = "Swagger-Reader";
	
	@DataProvider(name="swaggerProvider")
	public Object[][] provider(){
		return new Object[][]{
				{"json"},
				{"yaml"}
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="swaggerProvider")
	public void testSwaggerReader(String tipoInterfaccia) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' (interfaccia:"+tipoInterfaccia+") ...");
		org.openspcoop2.utils.openapi.test.TestSwagger2.main(new String[] {tipoInterfaccia});
		TestLogger.info("Run test '"+ID_TEST+"' (interfaccia:"+tipoInterfaccia+") ok");
		
	}
	
}
