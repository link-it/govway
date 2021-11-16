/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.redirect;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.RedirectEngineTest;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	// SOAP11 not follow
	@Test
	public void erogazione_soap11_not_follow() throws Exception {
		_test("not-follow",0,307,false, true, true);
	}

	@Test
	public void erogazione_soap11_301_not_follow() throws Exception {
		_test("not-follow",0,301,false, true, true);
	}
	
	@Test
	public void erogazione_soap11_relative_not_follow() throws Exception {
		_test("not-follow",0,307,true, true, true);
	}
	
	// SOAP11 follow
	@Test
	public void erogazione_soap11_follow() throws Exception {
		_test("follow",3,307,false, true, true);
	}

	@Test
	public void erogazione_soap11_301_follow() throws Exception {
		_test("follow",3,301,false, true, true);
	}
	
	@Test
	public void erogazione_soap11_relative_follow() throws Exception {
		_test("follow",3,307,true, true, true);
	}
	
	@Test
	public void erogazione_soap11_follow_superatiHopConsentiti() throws Exception {
		_test("follow-maxhop",3,307,false, true, true);
	}
	
	
	
	// SOAP12 not follow
	@Test
	public void erogazione_soap12_not_follow() throws Exception {
		_test("not-follow",0,307,false, true, true);
	}

	@Test
	public void erogazione_soap12_301_not_follow() throws Exception {
		_test("not-follow",0,301,false, true, true);
	}
	
	@Test
	public void erogazione_soap12_relative_not_follow() throws Exception {
		_test("not-follow",0,307,true, true, true);
	}
	
	// SOAP12 follow
	@Test
	public void erogazione_soap12_follow() throws Exception {
		_test("follow",3,307,false, true, true);
	}

	@Test
	public void erogazione_soap12_301_follow() throws Exception {
		_test("follow",3,301,false, true, true);
	}
	
	@Test
	public void erogazione_soap12_relative_follow() throws Exception {
		_test("follow",3,307,true, true, true);
	}
	
	@Test
	public void erogazione_soap12_follow_superatiHopConsentiti() throws Exception {
		_test("follow-maxhop",3,307,false, true, true);
	}
	
	
	private static void _test(
			String operazione, 
			int numeroRedirect, int httpRedirectStatus, boolean relative, 
			boolean withContent, boolean soap11) throws Exception {

		String api = "TestRedirectSOAP";
		
		RedirectEngineTest._test(logCore, api, operazione, 
				numeroRedirect, httpRedirectStatus, relative, 
				withContent, 
				soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2, 
				soap11 ? Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE) : Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE));

	}
	

}
