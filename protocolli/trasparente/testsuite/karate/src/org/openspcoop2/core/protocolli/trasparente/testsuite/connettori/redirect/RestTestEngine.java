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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.redirect;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author Tommaso Burlon (tommas.burlon@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTestEngine extends ConfigLoader {

	private HttpLibraryMode mode;
	public RestTestEngine() {
		this.mode = null;
	}
	public RestTestEngine(HttpLibraryMode mode) {
		this.mode = mode;
	}
	
	// GET not follow
	@Test
	public void erogazioneGetNotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,307,false, false);
	}
	@Test
	public void erogazioneGet301NotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,301,false, false);
	}
	@Test
	public void erogazioneGetRelativeNotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,307,true, false);
	}
	
	// GET follow
	@Test
	public void erogazioneGetFollow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,307,false, false);
	}
	@Test
	public void erogazioneGet301Follow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,301,false, false);
	}
	@Test
	public void erogazioneGetRelativeFollow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,307,true, false);
	}
	@Test
	public void erogazioneGetFollowSuperatiHopConsentiti() throws Exception {
		test(RedirectUtilities.FOLLOW_MAXHOP,3,307,false, false);
	}
	
	
	// POST not follow
	@Test
	public void erogazionePostNotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,307,false, false);
	}
	@Test
	public void erogazionePost301NotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,301,false, false);
	}
	@Test
	public void erogazionePostRelativeNotFollow() throws Exception {
		test(RedirectUtilities.NOT_FOLLOW,0,307,true, false);
	}
	
	// POST follow
	@Test
	public void erogazionePostFollow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,307,false, true);
	}
	@Test
	public void erogazionePost301Follow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,301,false, true);
	}
	
	@Test
	public void erogazionePostRelativeFollow() throws Exception {
		test(RedirectUtilities.FOLLOW,3,307,true, true);
	}
	@Test
	public void erogazionePostFollowSuperatiHopConsentiti() throws Exception {
		test(RedirectUtilities.FOLLOW_MAXHOP,3,307,false, true);
	}
	
	
	private void test(
			String operazione, 
			int numeroRedirect, int httpRedirectStatus, boolean relative, 
			boolean withContent) throws Exception {

		String api = "TestRedirectREST";
		
		RedirectUtilities._test(logCore, api, operazione, 
				numeroRedirect, httpRedirectStatus, relative, 
				withContent, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE), this.mode);

	}
	

}
