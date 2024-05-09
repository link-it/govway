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

package org.openspcoop2.utils.test.security;

import java.io.IOException;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestEncryptOpenSSL
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestEncryptOpenSSL {

	private static final String ID_TEST = "EncryptOpenSSL";
	
	@DataProvider(name="base64Provider")
	public Object[][] base64Provider(){
		return new Object[][]{
				{128, true},
				{128, false},
				{192, true},
				{192, false},
				{256, true},
				{256, false}
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testCifraturaConKey"},dataProvider="base64Provider")
	public void testCifraturaConKey(int size, boolean base64) throws UtilsException, IOException {
		
		TestLogger.info("Run test '"+ID_TEST+".testCifraturaConKey' (size: "+size+")(base64: "+base64+") ...");
		org.openspcoop2.utils.security.test.EncryptOpenSSLTest.testCifraturaConKey(size, base64);
		TestLogger.info("Run test '"+ID_TEST+".testCifraturaConKey' (size: "+size+")(base64: "+base64+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testDecifraturaConKey"},dataProvider="base64Provider")
	public void testDecifraturaConKey(int size, boolean base64) throws UtilsException, IOException {
		
		TestLogger.info("Run test '"+ID_TEST+".testDecifraturaConKey' (size: "+size+")(base64: "+base64+") ...");
		org.openspcoop2.utils.security.test.EncryptOpenSSLTest.testDecifraturaConKey(size, base64);
		TestLogger.info("Run test '"+ID_TEST+".testDecifraturaConKey' (size: "+size+")(base64: "+base64+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testCifraturaConPassword"},dataProvider="base64Provider")
	public void testCifraturaConPassword(int size, boolean base64) throws UtilsException, IOException {
		if(size==192 || size==128) {
			TestLogger.info("SKIP");
			return;
		}
		
		TestLogger.info("Run test '"+ID_TEST+".testCifraturaConPassword' (size: "+size+")(base64: "+base64+") ...");
		org.openspcoop2.utils.security.test.EncryptOpenSSLTest.testCifraturaConPassword(size, base64);
		TestLogger.info("Run test '"+ID_TEST+".testCifraturaConPassword' (size: "+size+")(base64: "+base64+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testDecifraturaConPassword"},dataProvider="base64Provider")
	public void testDecifraturaConPassword(int size, boolean base64) throws UtilsException, IOException {
		if(size==192 || size==128) {
			TestLogger.info("SKIP");
			return;
		}
		
		TestLogger.info("Run test '"+ID_TEST+".testDecifraturaConPassword' (size: "+size+")(base64: "+base64+") ...");
		org.openspcoop2.utils.security.test.EncryptOpenSSLTest.testDecifraturaConPassword(size, base64);
		TestLogger.info("Run test '"+ID_TEST+".testDecifraturaConPassword' (size: "+size+")(base64: "+base64+") ok");
		
	}
	
	
	@DataProvider(name="base64IterProvider")
	public Object[][] base64IterProvider(){
		return new Object[][]{
			{128, true, null},
			{128, false, null},
			{192, true, null},
			{192, false, null},
			{256, true, null},
			{256, false, null},
			
			{128, true, 65535},
			{128, false, 65535},
			{192, true, 65535},
			{192, false, 65535},
			{256, true, 65535},
			{256, false, 65535}
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testCifraturaConPasswordPBKDF2"},dataProvider="base64IterProvider")
	public void testCifraturaConPasswordPBKDF2(int size, boolean base64, Integer iteration) throws UtilsException, IOException {
		TestLogger.info("Run test '"+ID_TEST+".testCifraturaConPasswordPBKDF2' (size: "+size+")(base64: "+base64+")(iteration: "+iteration+") ...");
		org.openspcoop2.utils.security.test.EncryptOpenSSLTest.testCifraturaConPasswordPBKDF2(size, base64, iteration);
		TestLogger.info("Run test '"+ID_TEST+".testCifraturaConPasswordPBKDF2' (size: "+size+")(base64: "+base64+")(iteration: "+iteration+") ok");	
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".testDecifraturaConPasswordPBKDF2"},dataProvider="base64IterProvider")
	public void testDecifraturaConPasswordPBKDF2(int size, boolean base64, Integer iteration) throws UtilsException, IOException {
		TestLogger.info("Run test '"+ID_TEST+".testDecifraturaConPasswordPBKDF2' (size: "+size+")(base64: "+base64+")(iteration: "+iteration+") ...");
		org.openspcoop2.utils.security.test.EncryptOpenSSLTest.testDecifraturaConPasswordPBKDF2(size, base64, iteration);
		TestLogger.info("Run test '"+ID_TEST+".testDecifraturaConPasswordPBKDF2' (size: "+size+")(base64: "+base64+")(iteration: "+iteration+") ok");	
	}
}
