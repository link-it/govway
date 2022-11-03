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

package org.openspcoop2.utils.test.cache;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestCertificate
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestCache {

	private static final String ID_TEST = "Cache";
	private static final String ID_TEST_MISURAZIONE_TEMPI = "Cache-MisurazioneTempi";
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".performance"})
	public void testCache() throws Exception{
		
		boolean allTestExecution = true;
		
		TestLogger.info("Run test '"+ID_TEST+".performance' ...");
		org.openspcoop2.utils.cache.test.PerformanceTest.test(allTestExecution);
		TestLogger.info("Run test '"+ID_TEST+".performance' ok");
		
	}

	@Test(groups={Costanti.GRUPPO_UTILS+"."+ID_TEST_MISURAZIONE_TEMPI})
	public void testCacheSingleTest() throws Exception{
		
		// Serve per i tempi attesi
		
		boolean allTestExecution = false;
		
		TestLogger.info("Run test '"+ID_TEST+".performance' ...");
		org.openspcoop2.utils.cache.test.PerformanceTest.test(allTestExecution);
		TestLogger.info("Run test '"+ID_TEST+".performance' ok");
		
	}
}
