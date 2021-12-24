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

package org.openspcoop2.utils.test.resource;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestProblemDetail
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestCopyStream {

	private static final String ID_TEST = "CopyStream";
	
	@DataProvider(name="copyStreamProvider")
	public Object[][] provider(){
		return new Object[][]{
			{1024, -1, false, -1, false}, // 1KB
			{(1024*1024), -1, false, -1, false}, // 1MB
			{1024*1024*10, -1, false, -1, false}, // 10MB
			// Jenkins va in OutOfMemory {1024*1024*1024, false, -1, false} // 1GB
			
			// errore timeout
			{1024*1024*10, 120000, false, -1, false}, // 10MB
			{1024*1024*10, 2, true, -1, false}, // 10MB
			{1024*1024*10, 2, true, 1024*1024*10, false}, // 10MB
			
			// errore limit exceeded
			{1024*1024*10, -1, false, 1024*1024*10, false}, // 10MB
			{1024*1024*10, -1, false, 1024*10, true}, // 10MB
			{1024*1024*10, 120000, false, 1024*10, true}, // 10MB
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="copyStreamProvider")
	public void testCopyStream(int size, 
			int timeoutMs, boolean expectedTimeout,
			long limitBytes, boolean expectedLimitExceeded) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' ...");
		org.openspcoop2.utils.TestCopyStream.test(size, 
				timeoutMs, expectedTimeout,
				limitBytes, expectedLimitExceeded);
		TestLogger.info("Run test '"+ID_TEST+"' ok");
		
	}
	
}
