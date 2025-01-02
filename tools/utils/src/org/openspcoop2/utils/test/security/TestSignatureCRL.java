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

package org.openspcoop2.utils.test.security;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestSignatureCRL
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestSignatureCRL {

	private static final String ID_TEST = "SignatureCrl";
	
	@DataProvider(name="signatureCrlProvider")
	public Object[][] provider(){
		return new Object[][]{
				{org.openspcoop2.utils.security.test.SignatureTest.TipoTest.JSON_SIGNATURE_PROPERTIES_CRL, false},
				
				{org.openspcoop2.utils.security.test.SignatureTest.TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM, false},
				
				{org.openspcoop2.utils.security.test.SignatureTest.TipoTest.JSON_SIGNATURE_PROPERTIES_CRL_HEADER_CUSTOM_KID_ONLY, false},
				
				{org.openspcoop2.utils.security.test.SignatureTest.TipoTest.JSON_SIGNATURE_CRL_KEYSTORE, false},
				{org.openspcoop2.utils.security.test.SignatureTest.TipoTest.JSON_SIGNATURE_CRL_KEYSTORE_HEADER_CUSTOM, false},
		};
	}
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST},dataProvider="signatureCrlProvider")
	public void testSignatureCrl(org.openspcoop2.utils.security.test.SignatureTest.TipoTest tipo, boolean useP11asTrustStore) throws Exception{
		
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ...");
		org.openspcoop2.utils.security.test.SignatureTest.main(new String [] { tipo.name(), (useP11asTrustStore+"") } );
		TestLogger.info("Run test '"+ID_TEST+"' (tipo: "+tipo+") ok");
		
	}
	
}
