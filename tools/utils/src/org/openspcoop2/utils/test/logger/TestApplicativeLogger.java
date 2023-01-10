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

package org.openspcoop2.utils.test.logger;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestApplicativeLogger
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestApplicativeLogger {

	private static final String ID_TEST = "ApplicativeLogger";
	
	@DataProvider(name="applicativeLoggerProvider")
	public Object[][] provider(){
		return new Object[][]{
				{"proxy"},
				{"application"},
				{"batch"}
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="applicativeLoggerProvider")
	public void testApplicativeLogger(String tipo) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' (tipo:"+tipo+") ...");
		org.openspcoop2.utils.logger.test.Test.main(new String[] {tipo});
		TestLogger.info("Run test '"+ID_TEST+"' (tipo:"+tipo+") ok");
		
	}
	
}
