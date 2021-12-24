/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.encoding.charset;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* RestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@RunWith(Parameterized.class)
public class RestTest extends ConfigLoader {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ Charset.UTF_8 }, 
			{ Charset.UTF_16 },
			{ Charset.UTF_16BE },
			{ Charset.UTF_16LE },
			{ Charset.ISO_8859_1 }
		});
	}
	 
	private final Charset charset;
	public RestTest(Charset charset) {
		this.charset = charset;
	}
	
	
	
	// STREAM
	@Test
	public void erogazione_json_stream() throws Exception {
		_test("stream",this.charset, true);
	}
	@Test
	public void erogazione_xml_stream() throws Exception {
		_test("stream",this.charset, false);
	}
	
	
	// DUMP
	@Test
	public void erogazione_json_dump() throws Exception {
		_test("dump",this.charset, true);
	}
	@Test
	public void erogazione_xml_dump() throws Exception {
		_test("dump",this.charset, false);
	}
	
	
	// access_content_read_only
	@Test
	public void erogazione_json_access_content_read_only() throws Exception {
		_test("access_content_read_only_json",this.charset, true);
	}
	@Test
	public void erogazione_xml_access_content_read_only() throws Exception {
		_test("access_content_read_only_xml",this.charset, false);
	}
	
	
	
	// modify_content
	@Test
	public void erogazione_json_modify_content() throws Exception {
		_test("modify_content_json",this.charset, true);
	}
	@Test
	public void erogazione_xml_modify_content() throws Exception {
		_test("modify_content_xml",this.charset, false);
	}
	
	
	// modify_content con dump
	@Test
	public void erogazione_json_modify_content_con_dump() throws Exception {
		_test("modify_content_json_con_dump",this.charset, true);
	}
	@Test
	public void erogazione_xml_modify_content_con_dump() throws Exception {
		_test("modify_content_xml_con_dump",this.charset, false);
	}
	
	
	private static void _test(
			String operazione, Charset charset, boolean json) throws Exception {

		if(Charset.UTF_16BE.equals(charset) || Charset.UTF_16LE.equals(charset)) {
			if(!json && operazione.startsWith("modify")) {
				System.out.println("Skip test '"+operazione+"' per charset '"+charset.getValue()+"'; xml builder non gestisce correttamente il charset");
				return;
			}
		}
		
		String api = "TestCharsetREST";
		
		String body = null;
		String contentType = null;
		if(json) {
			contentType = HttpConstants.CONTENT_TYPE_JSON;
			String stringCaratteriParticolari = "\""+CharsetUtilities.caratteri_element_name+"\":\""+CharsetUtilities.caratteriNonUTF_JSON+"\"";
			body = Bodies.getJson(Bodies.SIZE_50K, stringCaratteriParticolari);
		}
		else {
			contentType = HttpConstants.CONTENT_TYPE_XML;
			String stringCaratteriParticolari = "<"+CharsetUtilities.caratteri_element_name+">"+CharsetUtilities.caratteriNonUTF_XML+"</"+CharsetUtilities.caratteri_element_name+">";
			body = Bodies.getXML(Bodies.SIZE_50K, stringCaratteriParticolari);
		}
		
		CharsetUtilities._test(logCore, api, operazione, 
				contentType, body, null,
				charset);

		if(!json) {
			body = "<?xml version=\"1.0\" encoding=\""+charset.getValue()+"\"?>"+"\n"+body;
			CharsetUtilities._test(logCore, api, operazione, 
					contentType, body, null,
					charset);
		}
	}
	

}
