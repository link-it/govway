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
package org.openspcoop2.core.protocolli.trasparente.testsuite.trasformazione.form;

import static org.junit.Assert.assertEquals;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.mime.MimeTypeConstants;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
* SPIDProviderTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SPIDProviderTest extends ConfigLoader {

	private final static String PATH = "/org/openspcoop2/core/protocolli/trasparente/testsuite/trasformazione/form/";
	

	
	//  "name": "16. Response - InResponseTo non specificato",
    // "description": "Attributo InResponseTo non specificato. Risultato atteso: KO",
	@Test
	public void erogazione_test16() throws Exception {
		_test16(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test16() throws Exception {
		_test16(TipoServizio.FRUIZIONE);
	}
	private void _test16(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-16.xml"));
		String error = "Elemento Attribute dell'AttributeStatement mancante.";
		_test(tipo, saml, error);
	}
	
	
	
    // "name": "17. Response - InResponseTo mancante",
    // "description": "Attributo InResponseTo mancante. Risultato atteso: KO",
	@Test
	public void erogazione_test17() throws Exception {
		_test17(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test17() throws Exception {
		_test17(TipoServizio.FRUIZIONE);
	}
	private void _test17(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-17.xml"));
		String error = "Elemento Attribute dell'AttributeStatement mancante.";
		_test(tipo, saml, error);
	}
	
	
    // "name": "43. Assertion - Elemento NameID non specificato",
    // "description": "Elemento NameID dell'Assertion non specificato. Risultato atteso: KO",
	@Test
	public void erogazione_test43() throws Exception {
		_test43(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test43() throws Exception {
		_test43(TipoServizio.FRUIZIONE);
	}
	private void _test43(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-43.xml"));
		String error = "Elemento NameID dell'Assertion non specificato.";
		_test(tipo, saml, error);
	}
	
	
    // "name": "44. Assertion - Elemento NameID mancante",
    // "description": "Elemento NameID dell'Assertion mancante. Risultato atteso: KO",
	@Test
	public void erogazione_test44() throws Exception {
		_test44(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test44() throws Exception {
		_test44(TipoServizio.FRUIZIONE);
	}
	private void _test44(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-44.xml"));
		String error = "Elemento NameID dell'Assertion mancante.";
		_test(tipo, saml, error);
	}
	


    // "name": "45. Assertion - Attributo Format di NameID non specificato",
    // "description": "Attributo Format dell'elemento NameID dell'Assertion non specificato. Risultato atteso: KO",
	@Test
	public void erogazione_test45() throws Exception {
		_test45(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test45() throws Exception {
		_test45(TipoServizio.FRUIZIONE);
	}
	private void _test45(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-45.xml"));
		String error = "Attributo Format dell'elemento NameID dell'Assertion mancante.";
		_test(tipo, saml, error);
	}
	
	
	
    // "name": "46. Assertion - Attributo Format di NameID mancante",
    // "description": "Attributo Format dell'elemento NameID dell'Assertion mancante. Risultato atteso: KO",
	@Test
	public void erogazione_test46() throws Exception {
		_test46(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test46() throws Exception {
		_test46(TipoServizio.FRUIZIONE);
	}
	private void _test46(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-46.xml"));
		String error = "Attributo Format dell'elemento NameID dell'Assertion mancante.";
		_test(tipo, saml, error);
	}


    // "name": "47. Assertion - Attributo Format di NameID diverso",
    // "description": "Attributo Format di NameID dell'Assertion diverso da urn:oasis:names:tc:SAML:2.0:nameidformat:transient. Risultato atteso: KO",
	@Test
	public void erogazione_test47() throws Exception {
		_test47(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test47() throws Exception {
		_test47(TipoServizio.FRUIZIONE);
	}
	private void _test47(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-47.xml"));
		String error = "Attributo Format di NameID dell'Assertion diverso da urn:oasis:names:tc:SAML:2.0:nameidformat:transient.";
		_test(tipo, saml, error);
	}
	


    // "name": "48. Assertion - Attributo NameQualifier di NameID non specificato",
    // "description": "Attributo NameQualifier di NameID dell'Assertion non specificato. Risultato atteso: KO",
	@Test
	public void erogazione_test48() throws Exception {
		_test48(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test48() throws Exception {
		_test48(TipoServizio.FRUIZIONE);
	}
	private void _test48(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-48.xml"));
		String error = "Attributo NameQualifier di NameID dell'Assertion mancante.";
		_test(tipo, saml, error);
	}
	
	
	

    // "name": "49. Assertion - Attributo NameQualifier di NameID mancante",
    // "description": "Attributo NameQualifier di NameID dell'Assertion mancante. Risultato atteso: KO"
	@Test
	public void erogazione_test49() throws Exception {
		_test49(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test49() throws Exception {
		_test49(TipoServizio.FRUIZIONE);
	}
	private void _test49(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-49.xml"));
		String error = "Attributo NameQualifier di NameID dell'Assertion mancante.";
		_test(tipo, saml, error);
	}
	
		

    // "name": "60. Assertion - Attributo InResponseTo di SubjectConfirmationData non specificato",
    // "description": "Attributo InResponseTo di SubjectConfirmationData dell'Assertion non specificato. Risultato atteso: KO",
	@Test
	public void erogazione_test60() throws Exception {
		_test60(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test60() throws Exception {
		_test60(TipoServizio.FRUIZIONE);
	}
	private void _test60(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-60.xml"));
		String error = "Elemento Attribute dell'AttributeStatement mancante.";
		_test(tipo, saml, error);
	}
	
	

    // "name": "61. Assertion - Attributo InResponseTo di SubjectConfirmationData mancante",
    // "description": "Attributo InResponseTo di SubjectConfirmationData dell'Assertion mancante. Risultato atteso: KO",
	@Test
	public void erogazione_test61() throws Exception {
		_test61(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test61() throws Exception {
		_test61(TipoServizio.FRUIZIONE);
	}
	private void _test61(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-61.xml"));
		String error = "Elemento Attribute dell'AttributeStatement mancante.";
		_test(tipo, saml, error);
	}
	
	

    // "name": "62. Assertion - Attributo InResponseTo di SubjectConfirmationData diverso da ID request",
    // "description": "Attributo InResponseTo di SubjectConfirmationData dell'Assertion diverso da ID request. Risultato atteso: KO",
	@Test
	public void erogazione_test62() throws Exception {
		_test62(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test62() throws Exception {
		_test62(TipoServizio.FRUIZIONE);
	}
	private void _test62(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-62.xml"));
		String error = "Elemento Attribute dell'AttributeStatement mancante.";
		_test(tipo, saml, error);
	}
	
		
	

    // "name": "70. Assertion - Attributo Format di Issuer non specificato",
    // "description": "Attributo Format di Issuer dell'Assertion non specificato. Risultato atteso: KO",
	@Test
	public void erogazione_test70() throws Exception {
		_test70(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test70() throws Exception {
		_test70(TipoServizio.FRUIZIONE);
	}
	private void _test70(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-70.xml"));
		String error = "Attributo Format di Issuer dell'Assertion mancante.";
		_test(tipo, saml, error);
	}


    // "name": "71. Assertion - Attributo Format di Issuer mancante",
    // "description": "Attributo Format di Issuer dell'Assertion mancante. Risultato atteso: KO",
	@Test
	public void erogazione_test71() throws Exception {
		_test71(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test71() throws Exception {
		_test71(TipoServizio.FRUIZIONE);
	}
	private void _test71(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-71.xml"));
		String error = "Attributo Format di Issuer dell'Assertion mancante.";
		_test(tipo, saml, error);
	}
	
		
	

    // "name": "73. Assertion - Elemento Conditions non specificato",
    // "description": "Elemento Conditions dell'Assertion non specificato. Risultato atteso: KO",
	@Test
	public void erogazione_test73() throws Exception {
		_test73(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test73() throws Exception {
		_test73(TipoServizio.FRUIZIONE);
	}
	private void _test73(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-73.xml"));
		String error = "Elemento Conditions dell'Assertion non specificato.";
		_test(tipo, saml, error);
	}
	
	

    // "name": "76. Assertion - Attributo NotBefore di Condition mancante",
    // "description": "Attributo NotBefore di Condition dell'Assertion mancante. Risultato atteso: KO",
	@Test
	public void erogazione_test76() throws Exception {
		_test76(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test76() throws Exception {
		_test76(TipoServizio.FRUIZIONE);
	}
	private void _test76(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-76.xml"));
		String error = "Attributo NotBefore di Condition dell'Assertion mancante.";
		_test(tipo, saml, error);
	}

	

    // "name": "79. Assertion - Attributo NotOnOrAfter di Condition non specificato",
    // "description": "Attributo NotOnOrAfter di Condition dell'Assertion non specificato. Risultato atteso: KO",
	@Test
	public void erogazione_test79() throws Exception {
		_test79(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test79() throws Exception {
		_test79(TipoServizio.FRUIZIONE);
	}
	private void _test79(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-79.xml"));
		String error = "Attributo NotOnOrAfter di Condition dell'Assertion mancante.";
		_test(tipo, saml, error);
	}
	
	


    // "name": "80. Assertion - Attributo NotOnOrAfter di Condition mancante",
    // "description": "Attributo NotOnOrAfter di Condition dell'Assertion mancante. Risultato atteso: KO",
	@Test
	public void erogazione_test80() throws Exception {
		_test80(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test80() throws Exception {
		_test80(TipoServizio.FRUIZIONE);
	}
	private void _test80(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-80.xml"));
		String error = "Attributo NotOnOrAfter di Condition dell'Assertion mancante.";
		_test(tipo, saml, error);
	}
	
	

    // "name": "84. Assertion - Elemento AudienceRestriction di Condition mancante",
    // "description": "Elemento AudienceRestriction di Condition dell'Assertion mancante. Risultato atteso: KO",
	@Test
	public void erogazione_test84() throws Exception {
		_test84(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test84() throws Exception {
		_test84(TipoServizio.FRUIZIONE);
	}
	private void _test84(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-84.xml"));
		String error = "Elemento Conditions dell'Assertion non specificato.";
		_test(tipo, saml, error);
	}
	

    // "name": "99. Assertion - Elemento AttributeStatement presente, con sottoelemento Attribute non specificato",
    // "description": "Elemento AttributeStatement presente, ma sottoelemento Attribute non specificato. Risultato atteso: KO",
	@Test
	public void erogazione_test99() throws Exception {
		_test99(TipoServizio.EROGAZIONE);
	}
	@Test
	public void fruizione_test99() throws Exception {
		_test99(TipoServizio.FRUIZIONE);
	}
	private void _test99(TipoServizio tipo) throws Exception {
		byte[]saml = Utilities.getAsByteArray(SPIDProviderTest.class.getResourceAsStream(PATH+"case-99.xml"));
		String error = "Elemento Attribute dell'AttributeStatement mancante.";
		_test(tipo, saml, error);
	}
	
	
	
	
	static void _test(TipoServizio tipo, byte[]saml, String error) throws Exception {
		
		String samlBase64 = Base64Utilities.encodeAsString(saml);
		
		List<String> keys = new ArrayList<String>();
		keys.add("RelayState");
		keys.add("SAMLResponse");
		List<String> values = new ArrayList<String>();
		values.add("ss:mem:f2d69b87d9e2ce0fa6f5461e665116dcd0abf6373ca41432352eef9656aa5173");
		values.add(samlBase64);
		
		testParametro(tipo, keys, values, error);	
		
		Collections.reverse(keys);
		Collections.reverse(values);
		testParametro(tipo, keys, values, error);	
	}
	
	static void testParametro(TipoServizio tipoServizio, List<String> keys, List<String> values, String error) throws Exception {
		

		final String url = tipoServizio == TipoServizio.EROGAZIONE
				? System.getProperty("govway_base_path") + "/SoggettoInternoTest/TestSPIDProvider/v1"
				: System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestSPIDProvider/v1";
		
		
		HttpRequest request = new HttpRequest();
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_X_WWW_FORM_URLENCODED);
		
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < keys.size(); j++) {
			String key = keys.get(j);
			String value = values.get(j);
			if(sb.length()>0) {
				sb.append("&");
			}
			sb.append(key);
			sb.append("=");
			//sb.append(TransportUtils.urlEncodeParam(value,Charset.UTF_8.getValue()));
			sb.append(URLEncoder.encode(value,Charset.UTF_8.getValue()));
		}
		request.setContent(sb.toString().getBytes());
		
		request.setUrl(url);
		
		HttpResponse response = HttpUtilities.httpInvoke(request);
		assertEquals(500, response.getResultHTTPOperation());
		assertEquals(MimeTypeConstants.MEDIA_TYPE_HTML, response.getContentType());
		String sResponse = new String(response.getContent());
		assertEquals("<samlError>"+error+"</samlError>", sResponse);
		
	}
	
}
