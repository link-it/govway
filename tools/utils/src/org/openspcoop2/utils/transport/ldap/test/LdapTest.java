/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.transport.ldap.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import javax.security.auth.x500.X500Principal;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.ldap.LdapClientFactory;
import org.openspcoop2.utils.transport.ldap.LdapClientInterface;
import org.openspcoop2.utils.transport.ldap.LdapEngineType;
import org.openspcoop2.utils.transport.ldap.LdapFilter;
import org.openspcoop2.utils.transport.ldap.LdapQuery;
import org.openspcoop2.utils.transport.ldap.LdapUtility;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ldap.ldif.parser.LdifParser;

/**
 * Test
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$  
 * @version $Rev$, $Date$
 *
 */
public class LdapTest {
	
	private static final String USER_CERTIFICATE = "userCertificate";
	private static final String RISULTATO_QUERY_NON_EQUIVALENTE = "Il risultato della query non risulta equivalente a quello atteso";
	
	private static List<Attributes> attributes;
	private static final String USERNAME = "uid=admin,ou=system";
	private static final String PASSWORD = "secret";
	private static LdapServerTest server;
	
	public static void readLdif(Resource ldifResourse) throws IOException {
		LdifParser parser = new LdifParser(ldifResourse);
        parser.open();
        
        attributes = new ArrayList<>();
        while (parser.hasMoreRecords()) {
            attributes.add(parser.getRecord());
        }
	}
	
	private static List<Attributes> applyQuery(LdapQuery query) {
		List<Attributes> rv = new ArrayList<>();
		List<String> retAttributes = query.getAttributes();
		
		for (Attributes attrs : attributes) {
			if (query.getFilter().check(attrs)) {
				BasicAttributes newAttrs = new BasicAttributes(true);
				
				NamingEnumeration<? extends Attribute> names = attrs.getAll();
				while(names.hasMoreElements()) {
					Attribute name = names.nextElement();
					
					if (retAttributes.isEmpty() || retAttributes.contains(name.getID())) {
						newAttrs.put(name);
					}
				}
				rv.add(newAttrs);
			}
		}
		
		return rv;
	}
	
	private static boolean compareAttributes(List<Attributes> lst1, List<Attributes> lst2) {
		Map<Attributes, Integer> table = new HashMap<>();
		
		for (Attributes attrs : lst1) {
			int value = table.getOrDefault(attrs, 0);
			table.put(attrs, value + 1);
		}
		
		for (Attributes attrs : lst2) {
			int value = table.getOrDefault(attrs, 0);
			
			if (value > 1)
				table.put(attrs, value - 1);
			else if (value > 0)
				table.remove(attrs);
			else
				return false;
		}
		
		return table.isEmpty();
	}
	
	private static void printAttributes(OutputStream stream, List<Attributes> list) throws NamingException, IOException {
		
		for (Attributes attrs : list) {
			NamingEnumeration<? extends Attribute> names = attrs.getAll();
			
			stream.write("{\n".getBytes());
			while(names.hasMoreElements()) {
				Attribute attr = names.next();
				stream.write(attr.getID().getBytes());
				stream.write(": ".getBytes());
				
				NamingEnumeration<?> values = attr.getAll();
				
				while(values.hasMoreElements()) {
					Object value = values.nextElement();
					
					if (value instanceof byte[])
						stream.write(Base64.getEncoder().encode((byte[])value));
					else
						stream.write(((String)value).getBytes());
					stream.write(", ".getBytes());
				}
				stream.write("\n".getBytes());
			}
			stream.write("},\n".getBytes());
		}
		
	}
	
	public static List<Attributes> testQuery(
			LdapEngineType type,
			LdapQuery query) throws UtilsException, InvalidNameException, URISyntaxException {
		
		LdapClientInterface client = LdapClientFactory.getClient(type);
		client.uri(new URI(server.getURL()))
			.username(new LdapName(USERNAME))
			.password(PASSWORD);
		
		
		List<Attributes> res = client.search(query);
		List<Attributes> baseCase = applyQuery(query);
		
		print("elementi trovati: " + res.size() + ", elementi attesi: " + baseCase.size());
		
		if (!compareAttributes(res, baseCase)) 
			throw new UtilsException(RISULTATO_QUERY_NON_EQUIVALENTE);
		
		return res;
	}
	
	public static List<Attributes> testCRL(LdapEngineType type) throws UtilsException, URISyntaxException, NamingException, CertificateException, CRLException {
		
		LdapClientInterface client = LdapClientFactory.getClient(type);
		client.uri(new URI(server.getURL()))
			.base(new LdapName("dc=example,dc=com"))
			.username(new LdapName(USERNAME))
			.password(PASSWORD);
		
		
		LdapQuery query = new LdapQuery()
				.filter(LdapFilter.isPresent(USER_CERTIFICATE))
				.attributes(USER_CERTIFICATE);
		List<Attributes> res = client.search(query);
		List<Attributes> baseCase = applyQuery(query);
		
		if (!compareAttributes(res, baseCase))
			throw new UtilsException(RISULTATO_QUERY_NON_EQUIVALENTE);
		
		byte[] crlContent = (byte[])res.get(0).get(USER_CERTIFICATE).get();
		
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509CRL crl = (X509CRL)cf.generateCRL(new ByteArrayInputStream(crlContent));
		X500Principal principal = crl.getIssuerX500Principal();
		
		print("issuer trovato: [" + principal.getName() + "], issuer atteso: [O=Link.it,L=Pisa,ST=PI,C=IT]");
		
		if (!principal.getName().equals("O=Link.it,L=Pisa,ST=PI,C=IT"))
			throw new UtilsException("certificato ottenuto non valido");
		
		return res;
	}
	
	public static List<Attributes> testParsing(LdapEngineType type, LdapQuery query, String url) throws UtilsException, URISyntaxException, InvalidNameException, ParseException {
		URI uri = new URI(url);
		LdapQuery parsedQuery = LdapUtility.getQueryFromURI(uri);
		
		LdapClientInterface client1 = LdapClientFactory.getClient(type);
		client1.uri(new URI(server.getURL()))
			.username(new LdapName(USERNAME))
			.password(PASSWORD);
		List<Attributes> list1 = client1.search(query);
		
		LdapClientInterface client2 = LdapClientFactory.getClient(type);
		client2.uri(LdapUtility.getBaseUrlFromURI(uri))
			.username(new LdapName(USERNAME))
			.password(PASSWORD);
		List<Attributes> list2 = client2.search(parsedQuery);
		
		print("elementi trovati: " + list2.size() + ", elementi attesi: " + list1.size());

		if (!compareAttributes(list1, list2))
			throw new UtilsException(RISULTATO_QUERY_NON_EQUIVALENTE);
		
		return list2;
	}
	
	public static void startServer() throws Exception {
		File dirTmp = File.createTempFile("ldapServer", "dat");
		FileSystemUtilities.deleteFile(dirTmp);
		FileSystemUtilities.mkdir(dirTmp);
		server.start(dirTmp.getPath());
	}
	
	public static void stopServer() {
		try {
			server.shutdown(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void init() throws IOException {
		Resource ldif = new ClassPathResource("server.ldif", LdapTest.class);
		server = new LdapServerTest(ldif);
		readLdif(ldif);
	}
	
	public static void main(String[] args) {
		try{			
			init();
			startServer();
			
			printAttributes(getPrintStream(), LdapTest.attributes);
			String baseUrl = getUrl();
			String base = "dc=example,dc=com";
			
			// query per fare il parsing di url in formato non encoded
			LdapQuery query = new LdapQuery()
					.base(new LdapName(base))
					.attributes(USER_CERTIFICATE);
			String url = baseUrl + "/dc=example,dc=com?userCertificate";
			
			print("query: "+query.getFilter().toString());
			
			print("**************** QUERY ****************");
			printAttributes(
					getPrintStream(),
					testQuery(
							LdapEngineType.SPRING_FRAMEWORK, 
							query));
			
			print("**************** CRL ****************");
			printAttributes(
					getPrintStream(),
					testCRL(LdapEngineType.SPRING_FRAMEWORK));
			
			print("**************** ATTRIBUTES ****************");
			printAttributes(
					getPrintStream(),
					testParsing(
					LdapEngineType.SPRING_FRAMEWORK, 
					query,
					url));
			
			stopServer();
			
			print("Testsuite terminata");
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			stopServer();
		}
	}
	
	public static String getUrl() {
		return server.getURL();
	}
	
	private static void print(String msg) {
		getPrintStream().println(msg);
	}
	private static PrintStream getPrintStream() {
		return System.out;
	}
}
