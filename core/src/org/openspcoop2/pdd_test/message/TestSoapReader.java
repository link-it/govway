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
public class TestSoapReader {

	private static final String ID_TEST = "SoapReader";
	
	@Test(groups={Costanti.GRUPPO_PDD,Costanti.GRUPPO_PDD+"."+ID_TEST})
	public void testReader() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"'.testReader ...");
		org.openspcoop2.message.soap.reader.test.ReaderTest.test();
		TestLogger.info("Run test '"+ID_TEST+"'.testReader ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_PDD,Costanti.GRUPPO_PDD+"."+ID_TEST})
	public void testOptimizedHeader() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"'.testOptimizedHeader ...");
		org.openspcoop2.message.soap.reader.test.OptimizedHeaderTest.test();
		TestLogger.info("Run test '"+ID_TEST+"'.testOptimizedHeader ok");
		
	}
	
}
