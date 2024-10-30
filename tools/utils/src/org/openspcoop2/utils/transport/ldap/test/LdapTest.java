package org.openspcoop2.utils.transport.ldap.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import javax.security.auth.x500.X500Principal;

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
 * @version $Rev$, $Date$
 *
 */
public class LdapTest {
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
	
	public static void testQuery(
			LdapEngineType type,
			LdapQuery query) throws Exception {
		
		LdapClientInterface client = LdapClientFactory.getClient(type);
		client.uri(new URI(server.getURL()))
			.username(new LdapName(USERNAME))
			.password(PASSWORD);
		
		
		List<Attributes> res = client.search(query);
		List<Attributes> baseCase = applyQuery(query);
		
		System.out.println("elementi trovati: " + res.size() + ", elementi attesi: " + baseCase.size());
		
		if (!compareAttributes(res, baseCase)) {
			printAttributes(System.out, res);
			throw new Exception("Il risultato della query non risulta equivalente a quello atteso");
		}
	}
	
	public static void testCRL(LdapEngineType type) throws Exception {
		
		LdapClientInterface client = LdapClientFactory.getClient(type);
		client.uri(new URI(server.getURL()))
			.base(new LdapName("dc=example,dc=com"))
			.username(new LdapName(USERNAME))
			.password(PASSWORD);
		
		
		LdapQuery query = new LdapQuery()
				.filter(LdapFilter.isPresent("userCertificate"))
				.attributes("userCertificate");
		List<Attributes> res = client.search(query);
		List<Attributes> baseCase = applyQuery(query);
		
		if (!compareAttributes(res, baseCase))
			throw new Exception("Il risultato della query non risulta equivalente a quello atteso");
		
		byte[] crlContent = (byte[])res.get(0).get("userCertificate").get();
		
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509CRL crl = (X509CRL)cf.generateCRL(new ByteArrayInputStream(crlContent));
		X500Principal principal = crl.getIssuerX500Principal();
		
		System.out.println("issuer trovato: [" + principal.getName() + "], issuer atteso: [O=Link.it,L=Pisa,ST=PI,C=IT]");
		
		if (!principal.getName().equals("O=Link.it,L=Pisa,ST=PI,C=IT"))
			throw new Exception("certificato ottenuto non valido");
	}
	
	public static void testParsing(LdapEngineType type, LdapQuery query) throws Exception {
		URI uri = LdapUtility.getURIFromQuery(new URI(server.getURL()), query);
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
		
		System.out.println("elementi trovati: " + list2.size() + ", elementi attesi: " + list1.size());

		if (!compareAttributes(list1, list2))
			throw new Exception("Il risultato della query non risulta equivalente a quello atteso");
		
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
			
			printAttributes(System.out, LdapTest.attributes);
			
			LdapQuery query = new LdapQuery()
					.base(new LdapName("dc=example,dc=com"))
					.attributes("cn", "mobile")
					.filter(
							LdapFilter.or(
									LdapFilter.and(
											LdapFilter.isEqual("l", "*ondon"), 
											LdapFilter.isLessEqual("uid", "002000")),
									LdapFilter.isEqual("uid", "001377")
							));
			System.out.println("query: "+query.getFilter().toString());
			
			testQuery(
					LdapEngineType.SPRING_FRAMEWORK, 
					query);
			
			testCRL(LdapEngineType.SPRING_FRAMEWORK);
			
			testParsing(
					LdapEngineType.SPRING_FRAMEWORK, 
					query);
			
			stopServer();
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			stopServer();
		}
	}
	
	public static String getUrl() {
		return server.getURL();
	}
}
