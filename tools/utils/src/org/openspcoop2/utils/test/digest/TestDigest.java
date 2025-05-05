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
package org.openspcoop2.utils.test.digest;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.digest.DigestType;
import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestDigest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestDigest {
	
	private static final String FORMAT_LOG_START = "Run tests '%s'... ";
	private static final String FORMAT_LOG_END = "Run tests '%s'... ";
	private static final String ID_TEST = "Digest";
	
	@DataProvider (name = "digestProvider")
    public Object[][] digestDP(){
	 return org.openspcoop2.utils.digest.test.DigestTest.getData();
    }
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST}, dataProvider = "digestProvider")
	public void testDigestBase64(DigestType type, String hashComposition, Object seed, String clear, String digest) throws UtilsException {
		
		TestLogger.info(String.format(FORMAT_LOG_START, ID_TEST));
		org.openspcoop2.utils.digest.test.DigestTest.testDigestBase64(type, hashComposition, seed, clear, digest);
		TestLogger.info(String.format(FORMAT_LOG_END, ID_TEST));
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST}, dataProvider = "digestProvider")
	public void testDigestBinary(DigestType type, String hashComposition,  Object seed, String clear, String digest) throws UtilsException {
		
		TestLogger.info(String.format(FORMAT_LOG_START, ID_TEST));
		org.openspcoop2.utils.digest.test.DigestTest.testDigestBinary(type, hashComposition, seed, clear, digest);
		TestLogger.info(String.format(FORMAT_LOG_END, ID_TEST));
		
	}

}
