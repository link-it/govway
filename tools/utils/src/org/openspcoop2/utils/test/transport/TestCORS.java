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

package org.openspcoop2.utils.test.transport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.AbstractCORSFilter;
import org.openspcoop2.utils.transport.http.CORSFilterConfiguration;
import org.openspcoop2.utils.transport.http.CORSRequestType;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.WrappedHttpServletRequest;
import org.openspcoop2.utils.transport.http.WrappedHttpServletResponse;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestCORS
 * 
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestCORS {

	public static void main(String [] args) throws Exception {
		TestCORS test = new TestCORS();
		test.testCheckActualNotSimplePostWithoutContentType();
	}
	
	private static final String ID_TEST = "CORS";

	private static final String TEST_HTTPS_ORIGIN = "https://www.govway.org";
	private static final String TEST_HTTP_ORIGIN = "http://www.govway.org";
	private static final String TEST_HTTPS_ORIGIN_UPPER_CASE = "https://www.GOVWAY.org";
	private static final String TEST_HTTPS_ORIGIN_2 = "https://www.govway2.org";
	private static final String TEST_HTTPS_ORIGIN_PORT_DIFFERENT = "https://www.govway2.org:8443";
	
	private static final String TEST_FILE_ORIGIN = "file://";
	private static final String TEST_NULL_ORIGIN = "null";
	
	private static final String TEST_INVALID_ORIGIN_1 = "http://www.w3.org\r\n"; 
	private static final String TEST_INVALID_ORIGIN_2 = "http://www.w3.org%0d%0a"; 
	private static final String TEST_INVALID_ORIGIN_3 = "http://www.w3.org%0D%0A"; 
	private static final String TEST_INVALID_ORIGIN_4 = "http://www.w3.org%0%0d%0ad%0%0d%0aa";
	private static final String TEST_INVALID_ORIGIN_5 = "http://www.w3.org http://altraUrl"; 

	private static final List<String> ALLOW_METHOD_DEFAULT = new ArrayList<> ();
	static {
		ALLOW_METHOD_DEFAULT.add(HttpRequestMethod.GET.name());
		ALLOW_METHOD_DEFAULT.add(HttpRequestMethod.POST.name());
		ALLOW_METHOD_DEFAULT.add(HttpRequestMethod.PUT.name());
		ALLOW_METHOD_DEFAULT.add(HttpRequestMethod.DELETE.name());
		ALLOW_METHOD_DEFAULT.add(HttpRequestMethod.PATCH.name());
	}

	private static final List<String> ALLOW_HEADER_DEFAULT = new ArrayList<> ();
	static {
		ALLOW_HEADER_DEFAULT.add(HttpConstants.AUTHORIZATION);
		ALLOW_HEADER_DEFAULT.add(HttpConstants.CONTENT_TYPE);
		ALLOW_HEADER_DEFAULT.add(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION);
		ALLOW_HEADER_DEFAULT.add(HttpConstants.CACHE_STATUS_HTTP_1_1);
	}



	@DataProvider(name="simpleContentTypeProvider")
	public Object[][] simpleContentTypeProvider(){
		return new Object[][]{
			{HttpConstants.ACCESS_CONTROL_SIMPLE_REQUEST_CONTENT_TYPES.get(0)},
			{HttpConstants.ACCESS_CONTROL_SIMPLE_REQUEST_CONTENT_TYPES.get(1)},
			{HttpConstants.ACCESS_CONTROL_SIMPLE_REQUEST_CONTENT_TYPES.get(2)},
		};
	}	

	@DataProvider(name="wrongOriginProvider")
	public Object[][] wrongOriginProvider(){
		return new Object[][]{
			{TEST_HTTP_ORIGIN},
			{TEST_HTTPS_ORIGIN_UPPER_CASE},
			{TEST_HTTPS_ORIGIN_2},
			{TEST_HTTPS_ORIGIN_PORT_DIFFERENT},
		};
	}	
	
	@DataProvider(name="originProvider")
	public Object[][] originProvider(){
		return new Object[][]{
			{TEST_HTTPS_ORIGIN},
			{TEST_HTTP_ORIGIN},
			{TEST_HTTPS_ORIGIN_UPPER_CASE},
			{TEST_HTTPS_ORIGIN_2},
			{TEST_HTTPS_ORIGIN_PORT_DIFFERENT},
			{TEST_FILE_ORIGIN},
			{TEST_NULL_ORIGIN},
		};
	}	
	
	@DataProvider(name="invalidOriginProvider")
	public Object[][] invalidOriginProvider(){
		return new Object[][]{
			{TEST_INVALID_ORIGIN_1},
			{TEST_INVALID_ORIGIN_2},
			{TEST_INVALID_ORIGIN_3},
			{TEST_INVALID_ORIGIN_4},
			{TEST_INVALID_ORIGIN_5},
		};
	}	


	
	

	
	// *** SIMPLE ***

	/**
	 * Tests if a GET request is treated as simple request.
	 * 
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".simpleRequestGET"})
	public void testDoFilterSimpleGET() throws Exception {

		TestLogger.info("Run test '"+ID_TEST+".simpleRequestGET' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.GET.name());
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		this.checkSimple(request, response);

		TestLogger.info("Run test '"+ID_TEST+".simpleRequestGET' ok");

	}

	/**
	 * Tests if a POST request is treated as simple request.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".simpleRequestPOST"},dataProvider="simpleContentTypeProvider")
	public void testDoFilterSimplePOST(String contentType) throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".simpleRequestPOST' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.POST.name());
		request.setContentType(contentType);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		this.checkSimple(request, response);

		TestLogger.info("Run test '"+ID_TEST+".simpleRequestPOST' ok");

	}


	/**
	 * Tests if a HEAD request is treated as simple request.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".simpleRequestHEAD"})
	public void testDoFilterSimpleHEAD() throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".simpleRequestPOST' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.HEAD.name());
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		this.checkSimple(request, response);

		TestLogger.info("Run test '"+ID_TEST+".simpleRequestPOST' ok");

	}


	/**
	 * Test the presence of specific origin in response, when '*' is not used.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".simpleSpecificHeader"},dataProvider="simpleContentTypeProvider")
	public void testDoFilterSimpleSpecificHeader(String contentType) throws IOException,
	ServletException {

		TestLogger.info("Run test '"+ID_TEST+".simpleSpecificHeader' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.POST.name());
		request.setContentType(contentType);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		this.checkSimple(request, response);

		TestLogger.info("Run test '"+ID_TEST+".simpleSpecificHeader' ok");
	}


	/**
	 * Tests the presence of the origin (and not '*') in the response, when
	 * supports credentials is enabled along with any origin, '*'.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".simpleAnyOriginAndSupportsCredentials"})
	public void testDoFilterSimpleAnyOriginAndSupportsCredentials()
			throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".simpleAnyOriginAndSupportsCredentials' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.GET.name());
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(true);
		corsFilter.getConfig().setAllowCredentials(true);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		String trovatoCredentials = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS+"' ["+"true"+"] trovato ["+trovatoCredentials+"]");
		Assert.assertTrue(trovatoCredentials!=null);
		Assert.assertTrue(trovatoCredentials.equals("true"));

		this.checkSimple(request, response);

		TestLogger.info("Run test '"+ID_TEST+".simpleAnyOriginAndSupportsCredentials' ok");
	}

	/**
	 * Tests the presence of '*' in the response, when
	 * supports credentials is disabled along with any origin, '*'.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".simpleAnyOriginAndSupportsCredentialsDisabled"})
	public void testDoFilterSimpleAnyOriginAndSupportsCredentialsDisabled()
			throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".simpleAnyOriginAndSupportsCredentialsDisabled' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.GET.name());
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(true);
		corsFilter.getConfig().setAllowCredentials(false);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso ["+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE));

		String trovatoCredentials = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS+"' ["+"true"+"] trovato ["+trovatoCredentials+"]");
		Assert.assertTrue(trovatoCredentials == null);

		this.checkSimple(request, response);

		TestLogger.info("Run test '"+ID_TEST+".simpleAnyOriginAndSupportsCredentialsDisabled' ok");
	}

	/**
	 * Tests the presence of exposed headers in response, if configured.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".simpleWithExposedHeaders"},dataProvider="simpleContentTypeProvider")
	public void testDoFilterSimpleWithExposedHeaders(String contentType) throws IOException,
	ServletException {

		TestLogger.info("Run test '"+ID_TEST+".simpleWithExposedHeaders' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.POST.name());
		request.setContentType(contentType);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		String hdr1 = "X-GovWayTest-HDR1";
		corsFilter.getConfig().addExposeHeader(hdr1);
		String hdr2 = "X-GovWayTest-HDR2";
		corsFilter.getConfig().addExposeHeader(hdr2); 
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		String trovatoExpose = response.getHeader(HttpConstants.ACCESS_CONTROL_EXPOSE_HEADERS);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_EXPOSE_HEADERS+"' ["+hdr1+", "+hdr2+"] trovato ["+trovatoExpose+"]");
		Assert.assertTrue(trovatoExpose!=null);
		Assert.assertTrue(trovatoExpose.equals(hdr1+", "+hdr2));

		this.checkSimple(request, response);

		TestLogger.info("Run test '"+ID_TEST+"..simpleWithExposedHeaders' ok");
	}
	
	
	
	// *** PREFLIGHT ***
	
	/**
	 * Checks if an OPTIONS request is processed as pre-flight.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflight"})
	public void testDoFilterPreflight() throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".preflight' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		this.checkPreflight(request, response);

		TestLogger.info("Run test '"+ID_TEST+".preflight' ok");
	}
	
	/**
	 * Checks if an OPTIONS request is processed as pre-flight with more headers.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightMoreHeaders"})
	public void testDoFilterPreflightMoreHeaders() throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".preflightMoreHeaders' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,
						HttpConstants.CONTENT_TYPE+","+ 
						HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION+" ,"+ 
						HttpConstants.AUTHORIZATION+" , "+
						HttpConstants.CACHE_STATUS_HTTP_1_1);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		this.checkPreflight(request, response);

		TestLogger.info("Run test '"+ID_TEST+".preflightMoreHeaders' ok");
	}

	/**
	 * Checks if an OPTIONS request is processed as pre-flight where any origin
	 * is enabled.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightAnyOrigin"})
	public void testDoFilterPreflightAnyOrigin() throws IOException,
	ServletException {

		TestLogger.info("Run test '"+ID_TEST+".preflightAnyOrigin' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(true);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso ["+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE));

		this.checkPreflight(request, response);

		TestLogger.info("Run test '"+ID_TEST+".preflightAnyOrigin' ok");
	}



	/**
	 * Checks if an OPTIONS request is processed as pre-flight, with invalid origin.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightInvalidOrigin"},dataProvider="wrongOriginProvider")
	public void testDoFilterPreflightInvalidOrigin(String origin) throws IOException,
	ServletException {

		TestLogger.info("Run test '"+ID_TEST+".preflightInvalidOrigin' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,origin);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso ["+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato==null); // il browser in questo contesto non fa passare la richiesta poichè non ha trovato l'origin che combacia

		this.checkPreflight(request, response);

		TestLogger.info("Run test '"+ID_TEST+".preflightInvalidOrigin' ok");
	}

	/**
	 * Checks if an OPTIONS request is processed as pre-flight, with max age -1
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightNegativeMaxAge"})
	public void testDoFilterPreflightNegativeMaxAge() throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".preflightNegativeMaxAge' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.getConfig().setCachingAccessControl_disable(true);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		String trovatoMaxAge = response.getHeader(HttpConstants.ACCESS_CONTROL_MAX_AGE);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_MAX_AGE+"' ["+HttpConstants.ACCESS_CONTROL_MAX_AGE_DISABLE_CACHE+"] trovato ["+trovatoMaxAge+"]");
		Assert.assertTrue(trovatoMaxAge!=null);
		Assert.assertTrue(trovatoMaxAge.equals(HttpConstants.ACCESS_CONTROL_MAX_AGE_DISABLE_CACHE));

		this.checkPreflight(request, response);

		TestLogger.info("Run test '"+ID_TEST+".preflightNegativeMaxAge' ok");
	}

	/**
	 * Checks if an OPTIONS request is processed as pre-flight, with max age
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightMaxAge"})
	public void testDoFilterPreflightMaxAge() throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".preflightMaxAge' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();

		int maxAge = 345;
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.getConfig().setCachingAccessControl_maxAgeSeconds(maxAge);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		String trovatoMaxAge = response.getHeader(HttpConstants.ACCESS_CONTROL_MAX_AGE);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_MAX_AGE+"' ["+maxAge+"] trovato ["+trovatoMaxAge+"]");
		Assert.assertTrue(trovatoMaxAge!=null);
		Assert.assertTrue(trovatoMaxAge.equals((maxAge+"")));

		this.checkPreflight(request, response);

		TestLogger.info("Run test '"+ID_TEST+".preflightMaxAge' ok");
	}

	/**
	 * Checks if an OPTIONS request is processed as pre-flight.
	 * Tests the presence of the origin (and not '*') in the response, when
	 * supports credentials is enabled along with any origin, '*'.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightAnyOriginAndSupportsCredentials"})
	public void testDoFilterPreflightAnyOriginAndSupportsCredentials() throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".preflightAnyOriginAndSupportsCredentials' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(true);
		corsFilter.getConfig().setAllowCredentials(true);
		corsFilter.doFilter(request, response, new TestFilterChain());
		
		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));
		
		String trovatoCredentials = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS+"' ["+"true"+"] trovato ["+trovatoCredentials+"]");
		Assert.assertTrue(trovatoCredentials!=null);
		Assert.assertTrue(trovatoCredentials.equals("true"));
		
		this.checkPreflight(request, response);
		
		TestLogger.info("Run test '"+ID_TEST+".preflightAnyOriginAndSupportsCredentials' ok");
	}
	
	/**
	 * Checks if an OPTIONS request is processed as pre-flight.
	 * Tests the presence of '*' in the response, when
	 * supports credentials is disabled along with any origin, '*'.
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightAnyOriginAndSupportsCredentialsDisabled"})
	public void testDoFilterPreflightAnyOriginAndSupportsCredentialsDisabled() throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".preflightAnyOriginAndSupportsCredentialsDisabled' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(true);
		corsFilter.getConfig().setAllowCredentials(false);
		corsFilter.doFilter(request, response, new TestFilterChain());
		
		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso ["+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE));
		
		String trovatoCredentials = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS+"' ["+"null"+"] trovato ["+trovatoCredentials+"]");
		Assert.assertTrue(trovatoCredentials==null);
		
		this.checkPreflight(request, response);
		
		TestLogger.info("Run test '"+ID_TEST+".preflightAnyOriginAndSupportsCredentialsDisabled' ok");
	}
	
	

	

	
	
	
	// *** INVALID ***
	
	/**
	 * Checks a request in presence of null origin
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".nullOrigin"})
	public void testDoFilterNullOrigin() throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".nullOrigin' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setMethod(HttpRequestMethod.POST.name());
		request.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		this.checkInvalid(request, response);
        
        TestLogger.info("Run test '"+ID_TEST+".nullOrigin' ok");
    }
	
	/**
	 * Checks a request in presence of empty origin
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".emptyOrigin"})
	public void testDoFilterEmptyOrigin() throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".emptyOrigin' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,"");
		request.setMethod(HttpRequestMethod.POST.name());
		request.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		this.checkInvalid(request, response);
        
        TestLogger.info("Run test '"+ID_TEST+".emptyOrigin' ok");
    }
	
	/**
	 * Checks a request in presence of null request type
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".nullRequestType"})
	public void testDoFilterNullRequestType() throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".nullRequestType' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(null);
		request.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		this.checkInvalid(request, response);
        
        TestLogger.info("Run test '"+ID_TEST+".nullRequestType' ok");
    }
	
	/**
	 * Checks a request in presence of origin not allowed
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+"invalidCORSOriginNotAllowed"},dataProvider="wrongOriginProvider")
	public void testDoFilterInvalidCORSOriginNotAllowed(String origin) throws IOException,
            ServletException {

		TestLogger.info("Run test '"+ID_TEST+".invalidCORSOriginNotAllowed' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,origin);
		request.setMethod(HttpRequestMethod.POST.name());
		request.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());
		
		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Non Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' trovato ["+trovato+"]");
		Assert.assertTrue(trovato==null); // il browser in questo contesto non fa passare la richiesta poichè non ha trovato l'origin che combacia

		this.checkSimple(request, response);
    
        TestLogger.info("Run test '"+ID_TEST+".invalidCORSOriginNotAllowed' ok");
    }
	
	 /**
     * when a valid CORS Pre-flight request arrives, with no
     * Access-Control-Request-Method: non viene riconosciuta come preflight
     */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightNoAccessControlRequestMethod"})
	public void testCheckPreFlightNoACRM() throws ServletException,
            IOException {

		TestLogger.info("Run test '"+ID_TEST+".preflightNoAccessControlRequestMethod' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		//request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		this.checkActual(request, response);
        
        TestLogger.info("Run test '"+ID_TEST+".preflightNoAccessControlRequestMethod' ok");
    }
	
	/**
     * when a valid CORS Pre-flight request arrives, with empty
     * Access-Control-Request-Method
     */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightEmptyAccessControlRequestMethod"})
	public void testCheckPreFlightEmptyACRM() throws ServletException,
            IOException {

		TestLogger.info("Run test '"+ID_TEST+".preflightEmptyAccessControlRequestMethod' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,"");
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		this.checkInvalid(request, response);
        
        TestLogger.info("Run test '"+ID_TEST+".preflightEmptyAccessControlRequestMethod' ok");
    }
	
	/**
     * when a valid CORS Pre-flight request arrives, with invalid
     * Access-Control-Request-Method
     */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightInvalidAccessControlRequestMethod"})
	public void testCheckPreFlightInvalidACRM() throws ServletException,
            IOException {

		TestLogger.info("Run test '"+ID_TEST+".preflightInvalidAccessControlRequestMethod' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,"ERRATO");
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		if(corsFilter.getConfig().isGenerateListAllowIfNotMatchRequestMethod()) {
			String trovatoAllowMethods = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS);
			String attesiMetodi = ALLOW_METHOD_DEFAULT.toString();
			TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_METHODS+"' ["+attesiMetodi+"] trovato ["+trovatoAllowMethods+"]");
			Assert.assertTrue(trovatoAllowMethods!=null);
			Assert.assertTrue(("["+trovatoAllowMethods+"]").equals(attesiMetodi));
		}
		else {
			String trovatoAllowMethods = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS);
			TestLogger.info("Non atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_METHODS+"' trovato ["+trovatoAllowMethods+"]");
			Assert.assertTrue(trovatoAllowMethods==null); // sara il browser a bloccarlo
		}
		
		String trovatoAllowHeaders = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS);
		String attesiHeaders = ALLOW_HEADER_DEFAULT.toString();
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS+"' ["+attesiHeaders+"] trovato ["+trovatoAllowHeaders+"]");
		Assert.assertTrue(trovatoAllowHeaders!=null);
		Assert.assertTrue(("["+trovatoAllowHeaders+"]").equals(attesiHeaders));
		
		this.checkType(request, response, CORSRequestType.PRE_FLIGHT);
        
        TestLogger.info("Run test '"+ID_TEST+".preflightInvalidAccessControlRequestMethod' ok");
    }
	
	/**
     * when a valid CORS Pre-flight request arrives, with unsupported
     * Access-Control-Request-Method
     */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightUnsupportedAccessControlRequestMethod"})
	public void testCheckPreFlightUnsupportedACRM() throws ServletException,
            IOException {

		TestLogger.info("Run test '"+ID_TEST+".preflightUnsupportedAccessControlRequestMethod' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.TRACE.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		if(corsFilter.getConfig().isGenerateListAllowIfNotMatchRequestMethod()) {
			String trovatoAllowMethods = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS);
			String attesiMetodi = ALLOW_METHOD_DEFAULT.toString();
			TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_METHODS+"' ["+attesiMetodi+"] trovato ["+trovatoAllowMethods+"]");
			Assert.assertTrue(trovatoAllowMethods!=null);
			Assert.assertTrue(("["+trovatoAllowMethods+"]").equals(attesiMetodi));
		}
		else {
			String trovatoAllowMethods = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS);
			TestLogger.info("Non atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_METHODS+"' trovato ["+trovatoAllowMethods+"]");
			Assert.assertTrue(trovatoAllowMethods==null); // sara il browser a bloccarlo
		}
		
		String trovatoAllowHeaders = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS);
		String attesiHeaders = ALLOW_HEADER_DEFAULT.toString();
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS+"' ["+attesiHeaders+"] trovato ["+trovatoAllowHeaders+"]");
		Assert.assertTrue(trovatoAllowHeaders!=null);
		Assert.assertTrue(("["+trovatoAllowHeaders+"]").equals(attesiHeaders));
		
		this.checkType(request, response, CORSRequestType.PRE_FLIGHT);
        
        TestLogger.info("Run test '"+ID_TEST+".preflightUnsupportedAccessControlRequestMethod' ok");
    }
	
	/**
     * when a valid CORS Pre-flight request arrives, with no
     * Access-Control-Request-Headers viene comunque gestita
     */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightNoAccessControlRequestHeaders"})
	public void testCheckPreFlightNoACRH() throws ServletException,
            IOException {

		TestLogger.info("Run test '"+ID_TEST+".preflightNoAccessControlRequestHeaders' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		//request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE);
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		this.checkPreflight(request, response);
        
        TestLogger.info("Run test '"+ID_TEST+".preflightNoAccessControlRequestHeaders' ok");
    }
	
	/**
     * when a valid CORS Pre-flight request arrives, with empty
     * Access-Control-Request-Headers viene comunque gestita
     */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightEmptyAccessControlRequestHeaders"})
	public void testCheckPreFlightEmptyACRH() throws ServletException,
            IOException {

		TestLogger.info("Run test '"+ID_TEST+".preflightEmptyAccessControlRequestHeaders' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,"");
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		this.checkPreflight(request, response);
        
        TestLogger.info("Run test '"+ID_TEST+".preflightEmptyAccessControlRequestHeaders' ok");
    }
	
	/**
     * when a valid CORS Pre-flight request arrives, with unsupported
     * Access-Control-Request-Headers
     */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".preflightUnsupportedAccessControlRequestHeaders"})
	public void testCheckPreFlightUnsupportedACRH() throws ServletException,
            IOException {

		TestLogger.info("Run test '"+ID_TEST+".preflightUnsupportedAccessControlRequestHeaders' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.OPTIONS.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_METHOD,HttpRequestMethod.PUT.name());
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_HEADERS,HttpConstants.CONTENT_TYPE+", X-CUSTOM_NON_SUPPORTATO");
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));

		String trovatoAllowMethods = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS);
		String attesiMetodi = ALLOW_METHOD_DEFAULT.toString();
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_METHODS+"' ["+attesiMetodi+"] trovato ["+trovatoAllowMethods+"]");
		Assert.assertTrue(trovatoAllowMethods!=null);
		Assert.assertTrue(("["+trovatoAllowMethods+"]").equals(attesiMetodi));
		
		if(corsFilter.getConfig().isGenerateListAllowIfNotMatchRequestHeaders()) {
			String trovatoAllowHeaders = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS);
			String attesiHeaders = ALLOW_HEADER_DEFAULT.toString();
			TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS+"' ["+attesiHeaders+"] trovato ["+trovatoAllowHeaders+"]");
			Assert.assertTrue(trovatoAllowHeaders!=null);
			Assert.assertTrue(("["+trovatoAllowHeaders+"]").equals(attesiHeaders));
		}
		else {
			String trovatoAllowHeaders = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS);
			TestLogger.info("Non atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS+"' trovato ["+trovatoAllowHeaders+"]");
			Assert.assertTrue(trovatoAllowHeaders==null);  // sara il browser a bloccarlo
		}
		
		this.checkType(request, response, CORSRequestType.PRE_FLIGHT);
        
        TestLogger.info("Run test '"+ID_TEST+".preflightUnsupportedAccessControlRequestHeaders' ok");
    }
	
	/**
	 * Tests with different origin
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".differentOrigin"},dataProvider="originProvider")
	public void testDoFilterDifferentOrigin(String origin) throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".differentOrigin' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,origin);
		request.setMethod(HttpRequestMethod.GET.name());
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(true);
		corsFilter.doFilter(request, response, new TestFilterChain());

		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN_ALL_VALUE));

		this.checkSimple(request, response);

		TestLogger.info("Run test '"+ID_TEST+".differentOrigin' ok");

	}
	
	/**
	 * Tests with invalid origin
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".invalidOrigin"},dataProvider="invalidOriginProvider")
	public void testDoFilterInvalidOrigin(String origin) throws IOException, ServletException {

		TestLogger.info("Run test '"+ID_TEST+".invalidOrigin' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,origin);
		request.setMethod(HttpRequestMethod.GET.name());
		TestHttpServletResponse response = new TestHttpServletResponse();

		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(true);
		corsFilter.doFilter(request, response, new TestFilterChain());

		this.checkInvalid(request, response);

		TestLogger.info("Run test '"+ID_TEST+".invalidOrigin' ok");

	}
	
	

	
	// *** ACTUAL ***
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".actualRequestType"})
	public void testCheckActualRequestType() throws Exception {

		TestLogger.info("Run test '"+ID_TEST+".actualRequestType' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.PUT.name()); // put non e' più simple
		request.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());
        
		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));
		
		this.checkActual(request, response);
		
        TestLogger.info("Run test '"+ID_TEST+".actualRequestType' ok");
    }
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".actualNotSimpleHeaders"})
	public void testCheckActualNotSimpleHeaders() throws Exception {

		TestLogger.info("Run test '"+ID_TEST+".actualNotSimpleHeaders' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.POST.name());
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);  // json non e' più simple
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());
        
		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));
		
		this.checkActual(request, response);
		
        TestLogger.info("Run test '"+ID_TEST+".actualNotSimpleHeaders' ok");
    }
	
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".actualPostWithoutContentType"})
	public void testCheckActualNotSimplePostWithoutContentType() throws Exception {

		TestLogger.info("Run test '"+ID_TEST+".actualPostWithoutContentType' ...");

		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setHeader(HttpConstants.ACCESS_CONTROL_REQUEST_ORIGIN,TEST_HTTPS_ORIGIN);
		request.setMethod(HttpRequestMethod.POST.name());
		//request.setContentType(HttpConstants.CONTENT_TYPE_JSON);  // senza content-type non e' più simple
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		TestCORSFIlter corsFilter = new TestCORSFIlter();
		corsFilter.getConfig().setAllowAllOrigin(false);
		corsFilter.getConfig().addAllowOrigin(TEST_HTTPS_ORIGIN);
		corsFilter.doFilter(request, response, new TestFilterChain());
        
		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' ["+TEST_HTTPS_ORIGIN+"] trovato ["+trovato+"]");
		Assert.assertTrue(trovato!=null);
		Assert.assertTrue(trovato.equals(TEST_HTTPS_ORIGIN));
		
		this.checkActual(request, response);
		
        TestLogger.info("Run test '"+ID_TEST+".actualPostWithoutContentType' ok");
    }
	
	
	
	
	
	private void checkInvalid(TestHttpServletRequest request, TestHttpServletResponse response) {
		
		String trovato = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN);
		TestLogger.info("Non atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_ORIGIN+"' trovato ["+trovato+"]");
		Assert.assertTrue(trovato==null);
		
		checkUndefinedPreflightOptions(response);
		
		checkType(request, response, CORSRequestType.INVALID);
	}
	
	private void checkSimple(TestHttpServletRequest request, TestHttpServletResponse response) {
		
		checkUndefinedPreflightOptions(response);
		
		checkType(request, response, CORSRequestType.SIMPLE);
	}
	
	private void checkActual(TestHttpServletRequest request, TestHttpServletResponse response) {
		
		checkUndefinedPreflightOptions(response);
		
		checkType(request, response, CORSRequestType.ACTUAL);
	}
	
	private void checkPreflight(TestHttpServletRequest request, TestHttpServletResponse response) {
		String trovatoAllowMethods = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS);
		String attesiMetodi = ALLOW_METHOD_DEFAULT.toString();
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_METHODS+"' ["+attesiMetodi+"] trovato ["+trovatoAllowMethods+"]");
		Assert.assertTrue(trovatoAllowMethods!=null);
		Assert.assertTrue(("["+trovatoAllowMethods+"]").equals(attesiMetodi));

		String trovatoAllowHeaders = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS);
		String attesiHeaders = ALLOW_HEADER_DEFAULT.toString();
		TestLogger.info("Atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS+"' ["+attesiHeaders+"] trovato ["+trovatoAllowHeaders+"]");
		Assert.assertTrue(trovatoAllowHeaders!=null);
		Assert.assertTrue(("["+trovatoAllowHeaders+"]").equals(attesiHeaders));

		checkType(request, response, CORSRequestType.PRE_FLIGHT);
	}
	
	private void checkType(TestHttpServletRequest request, TestHttpServletResponse response, CORSRequestType type) {
		
		CORSRequestType requestTypeTrovato = (CORSRequestType) request.getAttribute(AbstractCORSFilter.CORS_REQUEST_TYPE);
		TestLogger.info("Atteso tipo richiesta' ["+type+"] trovato ["+requestTypeTrovato+"]");
		Assert.assertTrue(requestTypeTrovato!=null);
		Assert.assertTrue(requestTypeTrovato.equals(type));
	}

	private void checkUndefinedPreflightOptions(TestHttpServletResponse response) {
		String trovatoAllowMethods = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_METHODS);
		TestLogger.info("Non atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_METHODS+"' trovato ["+trovatoAllowMethods+"]");
		Assert.assertTrue(trovatoAllowMethods==null);
		
		String trovatoAllowHeaders = response.getHeader(HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS);
		TestLogger.info("Non atteso header '"+HttpConstants.ACCESS_CONTROL_ALLOW_HEADERS+"' trovato ["+trovatoAllowHeaders+"]");
		Assert.assertTrue(trovatoAllowHeaders==null);
	}
	
	private class TestCORSFIlter extends AbstractCORSFilter{

		CORSFilterConfiguration conf = new CORSFilterConfiguration();
		{
			this.conf.setAllowAllOrigin(true);
			this.conf.getAllowHeaders().addAll(ALLOW_HEADER_DEFAULT);
			this.conf.getAllowMethods().addAll(ALLOW_METHOD_DEFAULT);
			this.conf.setAllowCredentials(false);
		}

		@Override
		protected CORSFilterConfiguration getConfig() throws IOException {
			return this.conf;
		}

		@Override
		protected Logger getLog() {
			return LoggerWrapperFactory.getLogger(TestCORS.class);
		}

	}

	private class TestFilterChain implements FilterChain {

		@Override
		public void doFilter(ServletRequest request, ServletResponse response)
				throws IOException, ServletException {
			// NoOp
		}

	}

	private class TestHttpServletRequest extends WrappedHttpServletRequest{

		private Map<String, Object> attributes = new HashMap<>();
		private Map<String, List<String>> headers = new HashMap<>();
		private String method;
		private String contentType;

		public TestHttpServletRequest() {
			super(null);
		}

		@Override
		public String getContentType() {
			return this.contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		@Override
		public String getMethod() {
			return this.method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		@Override
		public String getHeader(String name) {
			return TransportUtils.getFirstValue(this.headers,name);
		}

		public void setHeader(String name, String value) {
			TransportUtils.setHeader(this.headers,name, value);
		}
		
		@Override
		public Enumeration<String> getHeaderNames() {
			if(this.headers!=null && !this.headers.isEmpty() && this.headers.keySet()!=null) {
				return Collections.enumeration(this.headers.keySet());
			}
			return null;
		}
		
		@Override
		public Enumeration<String> getHeaders(String name) {
			List<String> values = TransportUtils.getRawObject(this.headers, name);
			if(values!=null && !values.isEmpty()) {
				return Collections.enumeration(values); 
			}
			return null;
		}
		
		@Override
		public Object getAttribute(String name) {
			return this.attributes.get(name);
		}

		@Override
		public void setAttribute(String name, Object value) {
			this.attributes.put(name, value);
		}

	}

	private class TestHttpServletResponse extends WrappedHttpServletResponse{

		private HashMap<String, String> map = new HashMap<>();
		private int status;

		public TestHttpServletResponse() {
			super(null);
		}

		@Override
		public void setHeader(String name, String value) {
			this.map.put(name, value);
		}

		@Override
		public void addHeader(String name, String value) {
			this.map.put(name, value);
		}

		@Override
		public String getHeader(String name) {
			return this.map.get(name);
		}
		
		@Override
		public Collection<String> getHeaderNames() {
			if(this.map!=null && !this.map.isEmpty()) {
				return this.map.keySet();
			}
			return null;
		}
		
		@Override
		public void setStatus(int status) {
			this.status = status;
		}

		@Override
		public int getStatus() {
			return this.status;
		}

	}
}
