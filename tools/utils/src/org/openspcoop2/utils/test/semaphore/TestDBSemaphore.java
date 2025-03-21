/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.test.semaphore;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * TestDBSemaphore
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestDBSemaphore {

	private static final String ID_TEST = "DBSemaphore";
	
	@Test(groups={Costanti.GRUPPO_UTILS_SQL,Costanti.GRUPPO_UTILS_SQL+"."+ID_TEST})
	@Parameters({"tipoDatabase","connectionUrl","username","password","driverJdbc","testIdle"})
	public void testDBSemaphore(String tipoDatabase, String connectionUrl, String username, String password, String driverJdbc, String testIdle) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' ...");
		org.openspcoop2.utils.semaphore.test.ClientTest.test(new String[] {tipoDatabase,  connectionUrl, username, password, driverJdbc, testIdle},
				false); // con test ng non vengono prodotti i log
		TestLogger.info("Run test '"+ID_TEST+"' ok");
	
	}
	
}
