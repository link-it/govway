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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.redirect;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestTest extends ConfigLoader {

	// GET not follow
	@Test
	public void erogazione_get_not_follow() throws Exception {
		_test("not-follow",0,307,false, false);
	}

	@Test
	public void erogazione_get_301_not_follow() throws Exception {
		_test("not-follow",0,301,false, false);
	}
	
	@Test
	public void erogazione_get_relative_not_follow() throws Exception {
		_test("not-follow",0,307,true, false);
	}
	
	// GET follow
	@Test
	public void erogazione_get_follow() throws Exception {
		_test("follow",3,307,false, false);
	}

	@Test
	public void erogazione_get_301_follow() throws Exception {
		_test("follow",3,301,false, false);
	}
	
	@Test
	public void erogazione_get_relative_follow() throws Exception {
		_test("follow",3,307,true, false);
	}
	
	@Test
	public void erogazione_get_follow_superatiHopConsentiti() throws Exception {
		_test("follow-maxhop",3,307,false, false);
	}
	
	
	// POST not follow
	@Test
	public void erogazione_post_not_follow() throws Exception {
		_test("not-follow",0,307,false, false);
	}

	@Test
	public void erogazione_post_301_not_follow() throws Exception {
		_test("not-follow",0,301,false, false);
	}
	
	@Test
	public void erogazione_post_relative_not_follow() throws Exception {
		_test("not-follow",0,307,true, false);
	}
	
	// POST follow
	@Test
	public void erogazione_post_follow() throws Exception {
		_test("follow",3,307,false, true);
	}

	@Test
	public void erogazione_post_301_follow() throws Exception {
		_test("follow",3,301,false, true);
	}
	
	@Test
	public void erogazione_post_relative_follow() throws Exception {
		_test("follow",3,307,true, true);
	}
	
	@Test
	public void erogazione_post_follow_superatiHopConsentiti() throws Exception {
		_test("follow-maxhop",3,307,false, true);
	}
	
	
	private static void _test(
			String operazione, 
			int numeroRedirect, int httpRedirectStatus, boolean relative, 
			boolean withContent) throws Exception {

		String api = "TestRedirectREST";
		
		RedirectUtilities._test(logCore, api, operazione, 
				numeroRedirect, httpRedirectStatus, relative, 
				withContent, HttpConstants.CONTENT_TYPE_JSON, Bodies.getJson(Bodies.SMALL_SIZE));

	}
	

}
