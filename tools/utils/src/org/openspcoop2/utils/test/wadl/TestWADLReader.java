/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

package org.openspcoop2.utils.test.wadl;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Test;

/**
 * TestWADLReader
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestWADLReader {

	private static final String ID_TEST = "WADL-Reader";
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST})
	public void testWadlReader() throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' ...");
		org.openspcoop2.utils.wadl.Test.main(null);
		TestLogger.info("Run test '"+ID_TEST+"' ok");
		
	}
	
}
