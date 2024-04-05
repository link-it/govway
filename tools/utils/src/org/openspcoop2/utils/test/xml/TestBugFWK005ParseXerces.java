/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.test.xml;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestBugFWK005ParseXerces
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestBugFWK005ParseXerces {

	private static final String ID_TEST = "TestBugFWK005ParseXerces";
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST})
	public void testBugFWK005ParseXerces_string() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' [string] ...");
		org.openspcoop2.utils.xml.test.TestBugFWK005ParseXerces.test(false);
		TestLogger.info("Run test '"+ID_TEST+"' [string] ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST})
	public void testBugFWK005ParseXerces_xml() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' [xml] ...");
		org.openspcoop2.utils.xml.test.TestBugFWK005ParseXerces.test(true);
		TestLogger.info("Run test '"+ID_TEST+"' [xml] ok");
		
	}
	
}
