/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author Tommaso Burlon (tommas.burlon@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTestEngine extends ConfigLoader {

	private HttpLibraryMode mode;
	
	public SoapTestEngine() {
		this.mode = null;
	}
	
	public SoapTestEngine(HttpLibraryMode mode) {
		this.mode = mode;
	}
	
	// SOAP11 not follow
	@Test
	public void erogazioneSoap11NotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,307,false, true, true);
	}

	@Test
	public void erogazioneSoap11Err301NotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,301,false, true, true);
	}
	
	@Test
	public void erogazioneSoap11RelativeNotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,307,true, true, true);
	}
	
	// SOAP11 follow
	@Test
	public void erogazioneSoap11Follow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,307,false, true, true);
	}

	@Test
	public void erogazioneSoap11Err301Follow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,301,false, true, true);
	}
	
	@Test
	public void erogazioneSoap11RelativeFollow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,307,true, true, true);
	}
	
	@Test
	public void erogazioneSoap11FollowSuperatiHopConsentiti() throws Exception {
		test(RedirectUtilities.FOLLOW_MAXHOP,3,307,false, true, true);
	}
	
	
	
	// SOAP12 not follow
	@Test
	public void erogazioneSoap12NotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,307,false, true, true);
	}

	@Test
	public void erogazioneSoap12Err301NotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,301,false, true, true);
	}
	
	@Test
	public void erogazioneSoap12RelativeNotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,307,true, true, true);
	}
	
	// SOAP12 follow
	@Test
	public void erogazioneSoap12Follow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,307,false, true, true);
	}

	@Test
	public void erogazioneSoap12Err301Follow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,301,false, true, true);
	}
	
	@Test
	public void erogazioneSoap12RelativeFollow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,307,true, true, true);
	}
	
	@Test
	public void erogazioneSoap12FollowSuperatiHopConsentiti() throws Exception {
		test(RedirectUtilities.FOLLOW_MAXHOP,3,307,false, true, true);
	}
	
	
	private void test(
			String operazione, 
			int numeroRedirect, int httpRedirectStatus, boolean relative, 
			boolean withContent, boolean soap11) throws Exception {

		String api = "TestRedirectSOAP";
		
		RedirectUtilities._test(logCore, api, operazione, 
				numeroRedirect, httpRedirectStatus, relative, 
				withContent, 
				soap11 ? HttpConstants.CONTENT_TYPE_SOAP_1_1 : HttpConstants.CONTENT_TYPE_SOAP_1_2, 
				soap11 ? Bodies.getSOAPEnvelope11(Bodies.SMALL_SIZE) : Bodies.getSOAPEnvelope12(Bodies.SMALL_SIZE),
				this.mode);

	}
	

}
