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

package org.openspcoop2.pdd_test.message;

import org.openspcoop2.pdd_test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestSoapReader
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestXXE {

	private static final String ID_TEST = "XML-eXternalEntityInjection-XXE";
	
	@Test(groups={Costanti.GRUPPO_PDD,Costanti.GRUPPO_PDD+"."+ID_TEST})
	public void testXXE_xmlUtils() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"'.testXXE_xmlUtils ...");
		org.openspcoop2.message.xml.TestXXE.test();
		TestLogger.info("Run test '"+ID_TEST+"'.testXXE_xmlUtils ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_PDD,Costanti.GRUPPO_PDD+"."+ID_TEST})
	public void testXXE_soap() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"'.testXXE_soap ...");
		org.openspcoop2.message.soap.TestXXE.test();
		TestLogger.info("Run test '"+ID_TEST+"'.testXXE_soap ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_PDD,Costanti.GRUPPO_PDD+"."+ID_TEST})
	public void testXXE_rest() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"'.testXXE_rest ...");
		org.openspcoop2.message.rest.TestXXE.test();
		TestLogger.info("Run test '"+ID_TEST+"'.testXXE_rest ok");
		
	}
	
}
