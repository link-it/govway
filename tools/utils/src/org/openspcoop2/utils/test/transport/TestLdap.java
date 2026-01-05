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

package org.openspcoop2.utils.test.transport;


import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.openspcoop2.utils.test.Costanti;
import org.openspcoop2.utils.test.TestLogger;
import org.openspcoop2.utils.transport.ldap.LdapEngineType;
import org.openspcoop2.utils.transport.ldap.LdapFilter;
import org.openspcoop2.utils.transport.ldap.LdapQuery;
import org.openspcoop2.utils.transport.ldap.test.LdapTest;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestLdap
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TestLdap {
	
	private static final String ID_TEST = "LDAP";
	
	private static final String BASE = "dc=example,dc=com";
	// data provider
	@DataProvider(name="queryProvider")
	public Object[][] queryProvider() throws InvalidNameException{
		String url = LdapTest.getUrl();
		
		// query che dovrebbe ritornare tutti i valori contenuti dal server
		LdapQuery queryAll = new LdapQuery()
				.base(new LdapName(BASE))
				.filter(LdapFilter.isPresent("cn"));
		String urlAll = url + "/dc%3Dexample%2Cdc%3Dcom?%28cn%3D*%29";
		
		// query che ritorna solo alcuni attributi
		LdapQuery queryBase = new LdapQuery()
				.base(new LdapName(BASE))
				.attributes("cn", "mobile")
				.filter(LdapFilter.not(LdapFilter.isEqual("l", "*ondon")));
		String urlBase = url + "/dc%3Dexample%2Cdc%3Dcom?cn,mobile?%28%21%28l%3D*ondon%29%29";
		
		// query che testa filtri composti (minore uguale)
		LdapQuery queryLess = new LdapQuery()
				.base(new LdapName(BASE))
				.attributes("cn", "mobile")
				.filter(
						LdapFilter.or(
								LdapFilter.and(
										LdapFilter.isEqual("l", "*ondon"), 
										LdapFilter.isLessEqual("uid", "002000")),
								LdapFilter.isEqual("uid", "001377")
						));
		String urlLess = url + "/dc%3Dexample%2Cdc%3Dcom?cn,mobile?%28%7C%28%26%28l%3D*ondon%29%28uid%3C%3D002000%29%29%28uid%3D001377%29%29";

		// query che testa filtri composti (maggiore uguale)
		LdapQuery queryGreater = new LdapQuery()
				.base(new LdapName(BASE))
				.attributes("cn", "mobile")
				.filter(
						LdapFilter.or(
								LdapFilter.and(
										LdapFilter.isEqual("l", "*ondon"), 
										LdapFilter.isGreaterEqual("uid", "002000")),
								LdapFilter.isEqual("uid", "001377")
						));
		String urlGreater = url + "/dc%3Dexample%2Cdc%3Dcom?cn,mobile?%28%7C%28%26%28l%3D*ondon%29%28uid%3E%3D002000%29%29%28uid%3D001377%29%29";
		
		// query per fare il parsing di url in formato non encoded
		LdapQuery queryRaw = new LdapQuery()
				.base(new LdapName(BASE))
				.attributes("userCertificate");
		String urlRaw = url + "/dc=example,dc=com?userCertificate";
		
		
		Object[][] queries = new Object[][] {
				{queryAll, urlAll},
				{queryBase, urlBase},
				{queryLess, urlLess},
				{queryGreater, urlGreater},
				{queryRaw, urlRaw},
		};
		
		Object[][] rv = new Object[queries.length * LdapEngineType.class.getEnumConstants().length][3];
		
		int cnt = 0;
		for (LdapEngineType type : LdapEngineType.class.getEnumConstants()) {
			for (Object[] query : queries) {
				rv[cnt][0] = type;
				rv[cnt][1] = query[0];
				rv[cnt][2] = query[1];
				cnt++;
			}
		}
		
		return rv;
	}
	
	@DataProvider(name="engineProvider")
	public Object[][] engineProvider(){		
		Object[][] rv = new Object[LdapEngineType.class.getEnumConstants().length][1];
		
		int cnt = 0;
		for (LdapEngineType type : LdapEngineType.class.getEnumConstants()) {
			rv[cnt++][0] = type;
		}
		
		return rv;
	}
	
	
	
	/**
	 * Avvia l'embedded server (ApacheDS) LDAP per i successivi test
	 * @throws Exception
	 */
	@BeforeGroups(Costanti.GRUPPO_UTILS)
	public void startServer() throws Exception {
		startServerEngine();
	}
	@BeforeGroups(Costanti.GRUPPO_UTILS+"."+ID_TEST)
	public void startServerIdTest() throws Exception {
		startServerEngine();
	}
	@BeforeGroups(Costanti.GRUPPO_UTILS+"."+ID_TEST+".query")
	public void startServerIdTestQuery() throws Exception {
		startServerEngine();
	}
	@BeforeGroups(Costanti.GRUPPO_UTILS+"."+ID_TEST+".crl")
	public void startServerIdTestCrl() throws Exception {
		startServerEngine();
	}
	@BeforeGroups(Costanti.GRUPPO_UTILS+"."+ID_TEST+".parsing")
	public void startServerIdTestParsing() throws Exception {
		startServerEngine();
	}
	private void startServerEngine() throws Exception {
		TestLogger.info("Starting ldap server...");
		
		try {
			LdapTest.init();
			LdapTest.startServer();
		} catch (Exception e) {
			TestLogger.info(e.getMessage());
			throw e;
		}
		
		TestLogger.info("ldap server started.");
	}
	
	/**
	 * Spegne l'embedded server usato per i test
	 */
	@AfterGroups(Costanti.GRUPPO_UTILS)
	public void stopServer() {
		stopServerEngine();
	}
	@AfterGroups(Costanti.GRUPPO_UTILS+"."+ID_TEST)
	public void stopServerIdTest() {
		stopServerEngine();
	}
	@AfterGroups(Costanti.GRUPPO_UTILS+"."+ID_TEST+".query")
	public void stopServerIdTestQuery() {
		stopServerEngine();
	}
	@AfterGroups(Costanti.GRUPPO_UTILS+"."+ID_TEST+".crl")
	public void stopServerIdTestCrl() {
		stopServerEngine();
	}
	@AfterGroups(Costanti.GRUPPO_UTILS+"."+ID_TEST+".parsing")
	public void stopServerIdTestParsing() {
		stopServerEngine();
	}
	private void stopServerEngine() {
		TestLogger.info("...stopping ldap server");
		LdapTest.stopServer();
	}
	
	/**
	 * @throws Exception 
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".query"},dataProvider="queryProvider")
	public void testQuery(LdapEngineType type, LdapQuery query, String url) throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".query' ...");
		TestLogger.info("engine: " + type.toString() + ", url: " + url);
		
		LdapTest.testQuery(type, query);
	}
	
	/**
	 * @throws Exception 
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".crl"},dataProvider="engineProvider")
	public void testCRL(LdapEngineType type) throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".crl' ...");
		TestLogger.info("engine: " + type.toString());
		
		LdapTest.testCRL(type);
	}
	
	/**
	 * @throws Exception 
	 */
	@Test(groups={Costanti.GRUPPO_UTILS,Costanti.GRUPPO_UTILS+"."+ID_TEST,Costanti.GRUPPO_UTILS+"."+ID_TEST+".parsing"},dataProvider="queryProvider")
	public void testParsing(LdapEngineType type, LdapQuery query, String url) throws Exception {
		TestLogger.info("Run test '"+ID_TEST+".parsing' ...");
		TestLogger.info("engine: " + type.toString() + ", url: " + url);
		
		LdapTest.testParsing(type, query, url);
	}
}
