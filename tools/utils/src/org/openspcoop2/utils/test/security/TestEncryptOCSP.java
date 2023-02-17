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

package org.openspcoop2.utils.test.security;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * TestEncryptOCSP
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestEncryptOCSP {

	private static final String ID_TEST = "EncryptOcsp";
	
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".properties"})
	@Parameters({"opensslCmd","waitStartupServerMs"})
	public void testEncryptOcsp_properties(String opensslCmd, String waitStartupServerMs) throws Exception{
		
		org.openspcoop2.utils.security.test.EncryptTest.TipoTest tipo = org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_OCSP;
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.security.test.EncryptTest.main(new String [] { tipo.name(), (false+""), opensslCmd, waitStartupServerMs } );
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}

	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".properties_header_custom"})
	@Parameters({"opensslCmd","waitStartupServerMs"})
	public void testEncryptOcsp_properties_header_custom(String opensslCmd, String waitStartupServerMs) throws Exception{
		
		org.openspcoop2.utils.security.test.EncryptTest.TipoTest tipo = org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_OCSP_HEADER_CUSTOM;
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.security.test.EncryptTest.main(new String [] { tipo.name(), (false+""), opensslCmd, waitStartupServerMs } );
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".properties_header_custom_kid_only"})
	@Parameters({"opensslCmd","waitStartupServerMs"})
	public void testEncryptOcsp_properties_header_custom_kid_only(String opensslCmd, String waitStartupServerMs) throws Exception{
		
		org.openspcoop2.utils.security.test.EncryptTest.TipoTest tipo = org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_PROPERTIES_OCSP_HEADER_CUSTOM_KID_ONLY;
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.security.test.EncryptTest.main(new String [] { tipo.name(), (false+""), opensslCmd, waitStartupServerMs } );
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".keystore"})
	@Parameters({"opensslCmd","waitStartupServerMs"})
	public void testEncryptOcsp_keystore(String opensslCmd, String waitStartupServerMs) throws Exception{
		
		org.openspcoop2.utils.security.test.EncryptTest.TipoTest tipo = org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_OCSP_KEYSTORE;
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.security.test.EncryptTest.main(new String [] { tipo.name(), (false+""), opensslCmd, waitStartupServerMs } );
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".keystore_header_custom"})
	@Parameters({"opensslCmd","waitStartupServerMs"})
	public void testEncryptOcsp_keystore_header_custom(String opensslCmd, String waitStartupServerMs) throws Exception{
		
		org.openspcoop2.utils.security.test.EncryptTest.TipoTest tipo = org.openspcoop2.utils.security.test.EncryptTest.TipoTest.JSON_ENCRYPT_OCSP_KEYSTORE_HEADER_CUSTOM;
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.security.test.EncryptTest.main(new String [] { tipo.name(), (false+""), opensslCmd, waitStartupServerMs } );
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
}
