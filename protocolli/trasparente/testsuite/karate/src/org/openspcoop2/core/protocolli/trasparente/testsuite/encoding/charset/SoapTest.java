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
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@RunWith(Parameterized.class)
public class SoapTest extends ConfigLoader {

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
	public SoapTest(Charset charset) {
		this.charset = charset;
	}
	

	
	// STREAM
	@Test
	public void erogazione_soap11_stream() throws Exception {
		_test("stream",this.charset, true, false, false);
	}	
	@Test
	public void erogazione_soap12_stream() throws Exception {
		_test("stream",this.charset, false, false, false);
	}
	@Test
	public void erogazione_soap11withAttachments_stream() throws Exception {
		_test("stream",this.charset, true, true, false);
	}
	@Test
	public void erogazione_soap12withAttachments_stream() throws Exception {
		_test("stream",this.charset, false, true, false);
	}
	
	
	// STREAM + addHeader
	@Test
	public void erogazione_soap11_stream_addHeader() throws Exception {
		_test("stream",this.charset, true, false, true);
	}
	@Test
	public void erogazione_soap12_stream_addHeader() throws Exception {
		_test("stream",this.charset, false, false, true);
	}
	@Test
	public void erogazione_soap11withAttachments_stream_addHeader() throws Exception {
		_test("stream",this.charset, true, true, true);
	}
	@Test
	public void erogazione_soap12withAttachments_stream_addHeader() throws Exception {
		_test("stream",this.charset, false, true, true);
	}
	
	
	// DUMP
	@Test
	public void erogazione_soap11_dump() throws Exception {
		_test("dump",this.charset, true, false, false);
	}
	@Test
	public void erogazione_soap12_dump() throws Exception {
		_test("dump",this.charset, false, false, false);
	}
	@Test
	public void erogazione_soap11withAttachments_dump() throws Exception {
		_test("dump",this.charset, true, true, false);
	}
	@Test
	public void erogazione_soap12withAttachments_dump() throws Exception {
		_test("dump",this.charset, false, true, false);
	}
	
	
	
	// DUMP + addHeader
	@Test
	public void erogazione_soap11_dump_addHeader() throws Exception {
		_test("dump",this.charset, true, false, true);
	}
	@Test
	public void erogazione_soap12_dump_addHeader() throws Exception {
		_test("dump",this.charset, false, false, true);
	}
	@Test
	public void erogazione_soap11withAttachments_dump_addHeader() throws Exception {
		_test("dump",this.charset, true, true, true);
	}
	@Test
	public void erogazione_soap12withAttachments_dump_addHeader() throws Exception {
		_test("dump",this.charset, false, true, true);
	}
	

	
	
	// access_content_read_only
	@Test
	public void erogazione_soap11_access_content_read_only() throws Exception {
		_test("access_content_read_only",this.charset, true, false, false);
	}
	@Test
	public void erogazione_soap12_access_content_read_only() throws Exception {
		_test("access_content_read_only",this.charset, false, false, false);
	}
	@Test
	public void erogazione_soap11withAttachments_access_content_read_only() throws Exception {
		_test("access_content_read_only",this.charset, true, true, false);
	}
	@Test
	public void erogazione_soap12withAttachments_access_content_read_only() throws Exception {
		_test("access_content_read_only",this.charset, false, true, false);
	}
	
	
	
	
	// access_content_read_only + addHeader
	@Test
	public void erogazione_soap11_access_content_read_only_addHeader() throws Exception {
		_test("access_content_read_only",this.charset, true, false, true);
	}
	@Test
	public void erogazione_soap12_access_content_read_only_addHeader() throws Exception {
		_test("access_content_read_only",this.charset, false, false, true);
	}
	@Test
	public void erogazione_soap11withAttachments_access_content_read_only_addHeader() throws Exception {
		_test("access_content_read_only",this.charset, true, true, true);
	}
	@Test
	public void erogazione_soap12withAttachments_access_content_read_only_addHeader() throws Exception {
		_test("access_content_read_only",this.charset, false, true, true);
	}
	
	
	
	
	// modify_content
	@Test
	public void erogazione_soap11_modify_content() throws Exception {
		_test("modify_content",this.charset, true, false, false);
	}
	@Test
	public void erogazione_soap12_modify_content() throws Exception {
		_test("modify_content",this.charset, false, false, false);
	}
	@Test
	public void erogazione_soap11withAttachments_modify_content() throws Exception {
		_test("modify_content",this.charset, true, true, false);
	}
	@Test
	public void erogazione_soap12withAttachments_modify_content() throws Exception {
		_test("modify_content",this.charset, false, true, false);
	}
	
	
	
	
	// modify_content + addHeader
	@Test
	public void erogazione_soap11_modify_content_addHeader() throws Exception {
		_test("modify_content",this.charset, true, false, true);
	}
	@Test
	public void erogazione_soap12_modify_content_addHeader() throws Exception {
		_test("modify_content",this.charset, false, false, true);
	}
	
	@Test
	public void erogazione_soap11withAttachments_modify_content_addHeader() throws Exception {
		_test("modify_content",this.charset, true, true, true);
	}
	@Test
	public void erogazione_soap12withAttachments_modify_content_addHeader() throws Exception {
		_test("modify_content",this.charset, false, true, true);
	}
	
	
	
	
	
	// modify_content con dump
	@Test
	public void erogazione_soap11_modify_content_con_dump() throws Exception {
		_test("modify_content_con_dump",this.charset, true, false, false);
	}
	@Test
	public void erogazione_soap12_modify_content_con_dump() throws Exception {
		_test("modify_content_con_dump",this.charset, false, false, false);
	}
	@Test
	public void erogazione_soap11withAttachments_modify_content_con_dump() throws Exception {
		_test("modify_content_con_dump",this.charset, true, true, false);
	}
	@Test
	public void erogazione_soap12withAttachments_modify_content_con_dump() throws Exception {
		_test("modify_content_con_dump",this.charset, false, true, false);
	}
	
	
	
	
	// modify_content con dump + addHeader
	@Test
	public void erogazione_soap11_modify_content_con_dump_addHeader() throws Exception {
		_test("modify_content_con_dump",this.charset, true, false, true);
	}
	@Test
	public void erogazione_soap12_modify_content_con_dump_addHeader() throws Exception {
		_test("modify_content_con_dump",this.charset, false, false, true);
	}
	@Test
	public void erogazione_soap11withAttachments_modify_content_con_dump_addHeader() throws Exception {
		_test("modify_content_con_dump",this.charset, true, true, true);
	}
	@Test
	public void erogazione_soap12withAttachments_modify_content_con_dump_addHeader() throws Exception {
		_test("modify_content_con_dump",this.charset, false, true, true);
	}
	
	
	
	
	private static void _test(
			String operazione, Charset charset, boolean soap11, boolean attachments, boolean addHeader) throws Exception {

//		if(Charset.UTF_16.equals(charset) || Charset.UTF_16BE.equals(charset) || Charset.UTF_16LE.equals(charset)) {
//			if(attachments) {
//				System.out.println("Skip test with attachments '"+operazione+"' per charset '"+charset.getValue()+"'; saaj builder non gestisce correttamente il charset");
//				return;
//			}
//		}
		
		String api = "TestCharsetSOAP";
		if(addHeader) {
			api = "TestCharsetSOAPAddHeader";
		}
		
		String contentType = null;
		if(soap11) {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_1;
		}
		else {
			contentType = HttpConstants.CONTENT_TYPE_SOAP_1_2;
		}
		String stringCaratteriParticolari = "<"+CharsetUtilities.caratteri_element_name+">"+CharsetUtilities.caratteriNonUTF_XML+"</"+CharsetUtilities.caratteri_element_name+">";
		if(attachments) {
			byte[] body = null;
			try {
				OpenSPCoop2Message msg = null;
				if(soap11) {
					msg = Bodies.getSOAP11WithAttachments(Bodies.SIZE_50K, stringCaratteriParticolari, charset,
							true, true, true, true, true);
				}
				else {
					msg = Bodies.getSOAP12WithAttachments(Bodies.SIZE_50K, stringCaratteriParticolari, charset,
							true, true, true, true, true);
				}
				body = Bodies.toByteArray(msg);
				contentType = msg.getContentType();
				
			}catch(Throwable t) {
				throw new Exception(t.getMessage(),t);
			}
			
			// unico invio con xml declaration
			CharsetUtilities._test(logCore, api, operazione, 
					contentType, null, body,
					charset, addHeader);
		}
		else {
			String body = null;
			if(soap11) {
				body = Bodies.getSOAPEnvelope11(Bodies.SIZE_50K, stringCaratteriParticolari);
			}
			else {
				body = Bodies.getSOAPEnvelope12(Bodies.SIZE_50K, stringCaratteriParticolari);
			}
			
			// senza xml declaration
			if( Charset.UTF_8.equals(charset) || (!operazione.startsWith("modify_content") && !addHeader)) {
				CharsetUtilities._test(logCore, api, operazione, 
						contentType, body, null,
						charset, addHeader);
			}
			else {
				// la libreria saaj serializza in maniera errata il messaggio
			}
			
			// con xml declaration
			body = "<?xml version=\"1.0\" encoding=\""+charset.getValue()+"\"?>"+"\n"+body;
			CharsetUtilities._test(logCore, api, operazione, 
					contentType, body, null,
					charset, addHeader);
		}
		
		
	}
	

}
