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

package org.openspcoop2.utils.test.jdbc;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * TestKeyGenerator
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestKeyGenerator {

	private static final String ID_TEST = "KeyGenerator";
	
	@Test(groups={Costanti.GRUPPO_UTILS_SQL,Costanti.GRUPPO_UTILS_SQL+"."+ID_TEST})
	@Parameters({"tipoDatabase","connectionUrl","username","password","driverJdbc"})
	public void testKeyGenerator(String tipoDatabase, String connectionUrl, String username, String password, String driverJdbc) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' ...");
		org.openspcoop2.utils.jdbc.test.KeyGeneratorTest.main(new String[] {tipoDatabase,  connectionUrl, username, password, driverJdbc});
		TestLogger.info("Run test '"+ID_TEST+"' ok");
	
	}
	
}
