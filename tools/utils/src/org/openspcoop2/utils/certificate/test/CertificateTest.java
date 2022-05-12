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

package org.openspcoop2.utils.certificate.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificatePrincipal;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.ExtendedKeyUsage;
import org.openspcoop2.utils.certificate.KeyUsage;
import org.openspcoop2.utils.certificate.OID;
import org.openspcoop2.utils.certificate.PrincipalType;

/**
 * CertificateTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificateTest {

	private final static String ALIAS_1 = "govway_test";
	private final static String ALIAS_2 = "govway_test_2";
	private final static String ALIAS_3 = "govway_test_3";
	private final static String PASSWORD = "123456";
	private final static String PREFIX = "/org/openspcoop2/utils/certificate/test/";
	private final static boolean STRICT = true;

	public static void main(String[] args) throws Exception {

		testJKS();

		testPKCS12();

		testDER();

		testPEM();

		testTraFormatiDifferente();

		testMultiplePrivatePublicJKS();
		
		testMoreCertsJKS();
		
		testTraFormatiDifferente_MultipleOU();
		
		testTraFormatiDifferente_MultipleOU_specialChar();
		
		testOID_keyUsage_sign();
		
		testOID_keyUsage_auth();
		
		testOID_keyUsage_multipleOU();
				
	}

	public static void testJKS() throws Exception {

		System.out.println("========================= JKS ==============================");

		Certificate c1_loadByPosition = 
				ArchiveLoader.load(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test.jks")), 0, PASSWORD);
		Certificate c2_loadByAlias = 
				ArchiveLoader.load(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_2.jks")), ALIAS_2, PASSWORD);
		Certificate c3 =		
				ArchiveLoader.load(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_3.jks")), ALIAS_3, PASSWORD);
		_test(c1_loadByPosition, c2_loadByAlias, c3);

	}
	
	public static void testMultiplePrivatePublicJKS() throws Exception {

		System.out.println("========================= JKS Multiple PrivatePublic keys ==============================");

		Certificate c1 = 
				ArchiveLoader.load(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"test_multi_keys.jks")), ALIAS_1, PASSWORD);
		Certificate c2 = 
				ArchiveLoader.load(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"test_multi_keys.jks")), ALIAS_2, PASSWORD);
		Certificate c3 =		
				ArchiveLoader.load(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"test_multi_keys.jks")), ALIAS_3, PASSWORD);
		_test(c1, c2, c3);
		
		List<String> aliases = ArchiveLoader.readAliases(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"test_multi_keys.jks")), PASSWORD);
		System.out.println("\n");
		for (String alias : aliases) {
			if(ALIAS_1.equals(alias) || ALIAS_2.equals(alias) || ALIAS_3.equals(alias) ){
				System.out.println("alias ["+alias+"]");
			}
			else {
				throw new Exception("Alias ["+alias+"] non atteso");
			}
		}

	}
	
	public static void testMoreCertsJKS() throws Exception {

		System.out.println("========================= JKS Multiple Certificates ==============================");

		Certificate c1 = 
				ArchiveLoader.load(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_con_altri_certificati.jks")), ALIAS_1, PASSWORD);
		Certificate c2 = 
				ArchiveLoader.load(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_con_altri_certificati.jks")), ALIAS_2, PASSWORD);
		Certificate c3 =		
				ArchiveLoader.load(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_con_altri_certificati.jks")), ALIAS_3, PASSWORD);
		_test(c1, c2, c3);

		List<String> aliases = ArchiveLoader.readAliases(ArchiveType.JKS, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_con_altri_certificati.jks")), PASSWORD);
		System.out.println("\n");
		for (String alias : aliases) {
			if(ALIAS_1.equals(alias) || ALIAS_2.equals(alias) || ALIAS_3.equals(alias) ){
				System.out.println("alias ["+alias+"]");
			}
			else {
				throw new Exception("Alias ["+alias+"] non atteso");
			}
		}

	}

	public static void testPKCS12() throws Exception {

		System.out.println("========================= PKCS12 ==============================");

		Certificate c1_loadByPosition = 
				ArchiveLoader.load(ArchiveType.PKCS12, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test.p12")), 0, PASSWORD);
		Certificate c2_loadByAlias = 
				ArchiveLoader.load(ArchiveType.PKCS12, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_2.p12")), ALIAS_2, PASSWORD);
		Certificate c3 =		
				ArchiveLoader.load(ArchiveType.PKCS12, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_3.p12")), ALIAS_3, PASSWORD);
		_test(c1_loadByPosition, c2_loadByAlias, c3);

	}

	public static void testDER() throws Exception {

		System.out.println("========================= DER ==============================");

		Certificate c1_loadByPosition = 
				ArchiveLoader.load(ArchiveType.CER, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test.cer")), 0, null);
		Certificate c2_loadByAlias = 
				ArchiveLoader.load(ArchiveType.CER, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_2.cer")), 0, null);
		Certificate c3 =		
				ArchiveLoader.load(ArchiveType.CER, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_3.cer")), 0, null);
		_test(c1_loadByPosition, c2_loadByAlias, c3);

	}

	public static void testPEM() throws Exception {

		System.out.println("========================= PEM ==============================");

		Certificate c1_loadByPosition = 
				ArchiveLoader.load(ArchiveType.CER, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test.pem")), 0, null);
		Certificate c2_loadByAlias = 
				ArchiveLoader.load(ArchiveType.CER, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_2.pem")), 0, null);
		Certificate c3 =		
				ArchiveLoader.load(ArchiveType.CER, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+"govway_test_3.pem")), 0, null);
		_test(c1_loadByPosition, c2_loadByAlias, c3);

	}

	private static void _test(Certificate c1_loadByPosition, Certificate c2_loadByAlias, Certificate c3) throws Exception {

		System.out.println("C1:");
		System.out.println(c1_loadByPosition.toString());

		System.out.println("\n\n");

		System.out.println("C2:");
		System.out.println(c2_loadByAlias.toString());

		System.out.println("\n\n");

		System.out.println("C3:");
		System.out.println(c3.toString());

		System.out.println("\n\n");

		// c1 e c2 hanno lo stesso subject ma sono certificati differenti (serial number diversi)
		// c3 e' proprio diverso

		boolean equals = c1_loadByPosition.getCertificate().equals(c2_loadByAlias.getCertificate()); // default è strict
		if(equals) {
			throw new Exception("Atteso risultato 'false' per l'equals (con default value strict) dei certificati C1 e C2 (hanno lo stesso subject ma sono certificati differenti (serial number diversi))");
		}
		System.out.println("Compare C1 e C2 con equals ha dato risultato atteso false");

		equals = c1_loadByPosition.getCertificate().equals(c2_loadByAlias.getCertificate(), STRICT); 
		if(equals) {
			throw new Exception("Atteso risultato 'false' per l'equals (strict) dei certificati C1 e C2 (hanno lo stesso subject ma sono certificati differenti (serial number diversi))");
		}
		System.out.println("Compare C1 e C2 con equals strict ha dato risultato atteso false");

		equals = c1_loadByPosition.getCertificate().equals(c2_loadByAlias.getCertificate(), !STRICT); 
		if(!equals) {
			throw new Exception("Atteso risultato 'true' per l'equals (!strict) dei certificati C1 e C2 (hanno lo stesso subject ma sono certificati differenti (serial number diversi))");
		}
		System.out.println("Compare C1 e C2 con equals not strict ha dato risultato atteso true");


		equals = c1_loadByPosition.getCertificate().equals(c3.getCertificate());
		if(equals) {
			throw new Exception("Atteso risultato 'false' per l'equals (con default value strict) dei certificati C1 e C3");
		}
		System.out.println("Compare C1 e C3 con equals ha dato risultato atteso false");
		equals = c2_loadByAlias.getCertificate().equals(c3.getCertificate());
		if(equals) {
			throw new Exception("Atteso risultato 'false' per l'equals (con default value strict) dei certificati C2 e C3");
		}
		System.out.println("Compare C2 e C3 con equals ha dato risultato atteso false");

		equals = c1_loadByPosition.getCertificate().equals(c3.getCertificate(), STRICT); 
		if(equals) {
			throw new Exception("Atteso risultato 'false' per l'equals (strict) dei certificati C1 e C3");
		}
		System.out.println("Compare C1 e C3 con equals strict ha dato risultato atteso false");
		equals = c2_loadByAlias.getCertificate().equals(c3.getCertificate(), STRICT); 
		if(equals) {
			throw new Exception("Atteso risultato 'false' per l'equals (strict) dei certificati C2 e C3");
		}
		System.out.println("Compare C2 e C3 con equals strict ha dato risultato atteso false");

		equals = c1_loadByPosition.getCertificate().equals(c3.getCertificate(), !STRICT); 
		if(equals) {
			throw new Exception("Atteso risultato 'false' per l'equals (!strict) dei certificati C1 e C3");
		}
		System.out.println("Compare C1 e C3 con equals not strict ha dato risultato atteso false");
		equals = c2_loadByAlias.getCertificate().equals(c3.getCertificate(), !STRICT); 
		if(equals) {
			throw new Exception("Atteso risultato 'false' per l'equals (!strict) dei certificati C2 e C3");
		}
		System.out.println("Compare C2 e C3 con equals not strict ha dato risultato atteso false");

	}

	public static void testTraFormatiDifferente() throws Exception {
		_testTraFormatiDifferente("govway_test.", false, false);
	}
	public static void testTraFormatiDifferente_MultipleOU() throws Exception {
		_testTraFormatiDifferente("multipleOU/multipleOU.", true, false);
	}
	public static void testTraFormatiDifferente_MultipleOU_specialChar() throws Exception {
		_testTraFormatiDifferente("multipleOU_specialChar/multipleOU_specialChar.", true, true);
	}
	private static void _testTraFormatiDifferente(String prefix, boolean checkPresenzaMultiplaOU, boolean specialChar) throws Exception {

		List<ArchiveType> types = new ArrayList<>();
		types.add(ArchiveType.JKS);
		types.add(ArchiveType.PKCS12);
		types.add(ArchiveType.CER); // due volte per fare pem e der
		types.add(ArchiveType.CER);

		for (int i = 0; i < types.size(); i++) {

			ArchiveType srcType = types.get(i);
			String srcExt = "jks";
			if(ArchiveType.PKCS12.equals(srcType)) {
				srcExt = "p12";
			}
			else if(ArchiveType.CER.equals(srcType) && ((i%2)==0)) {
				srcExt = "cer";
			}
			else if(ArchiveType.CER.equals(srcType) && ((i%2)==1)) {
				srcExt = "pem";
			}

			for (int j = 0; j < types.size(); j++) {

				ArchiveType destType = types.get(j);
				String destExt = "jks";
				if(ArchiveType.PKCS12.equals(destType)) {
					destExt = "p12";
				}
				else if(ArchiveType.CER.equals(destType) && ((j%2)==0)) {
					destExt = "cer";
				}
				else if(ArchiveType.CER.equals(destType) && ((j%2)==1)) {
					destExt = "pem";
				}

				System.out.println("========================= Compare ["+prefix+srcExt+" - "+prefix+destExt+"] ==============================");

				Certificate c1 = 
						ArchiveLoader.load(srcType, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+prefix+srcExt)), 0, PASSWORD);
				Certificate c2 = 
						ArchiveLoader.load(destType, Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+prefix+destExt)), 0, PASSWORD);

				boolean equals = c1.getCertificate().equals(c2.getCertificate()); // default è strict
				if(!equals) {
					throw new Exception("Atteso risultato 'true' per l'equals (con default value strict)");
				}
				System.out.println("Compare con equals ha dato risultato atteso true");

				equals = c1.getCertificate().equals(c2.getCertificate(), STRICT); 
				if(!equals) {
					throw new Exception("Atteso risultato 'true' per l'equals (strict)");
				}
				System.out.println("Compare con equals strict ha dato risultato atteso true");

				equals = c1.getCertificate().equals(c2.getCertificate(), !STRICT); 
				if(!equals) {
					throw new Exception("Atteso risultato 'true' per l'equals (!strict)");
				}
				System.out.println("Compare con equals not strict ha dato risultato atteso true");
				
				Map<String, List<String>> c1_valori = CertificateUtils.getPrincipalIntoMap(c1.getCertificate().getSubject().getName(), PrincipalType.subject);
				if(!c1_valori.containsKey("ou")) {
					throw new Exception("OU non trovato");
				}
				System.out.println("OU size: "+c1_valori.get("ou").size());
				if(checkPresenzaMultiplaOU) {
					
					List<String> l = c1_valori.get("ou");
					String campione = "\\ Piano\\=2\\, Scala\\=B\\, porta\\=3";
					boolean found = false;
					for (String lS : l) {
						if(lS.equals(campione)) {
							found = true;
						}
						System.out.println("OU=["+lS+"]");
					}
					
					if(specialChar) {
						if(c1_valori.get("ou").size()!=6) {
							throw new Exception("Attesa presenza multipla (6) di ou");
						}
					}
					else {
						if(c1_valori.get("ou").size()!=5) {
							throw new Exception("Attesa presenza multipla (5) di ou");
						}
					}
					if(!found) {
						throw new Exception("Atteso valore di un ou uguale a '"+campione+"'");
					}
				}
				else {
					System.out.println("OU: "+c1_valori.get("ou"));
					
					if(c1_valori.get("ou").size()!=1) {
						throw new Exception("Attesa presenza singola di ou");
					}
				}
			}

		}

	}
	
	
	public static void testOID_keyUsage_sign() throws Exception {
		_testOID_keyUsage("govway_test_oid_sign.cer", true, false);
	}
	public static void testOID_keyUsage_auth() throws Exception {
		_testOID_keyUsage("govway_test_oid_auth.cer", false, true);
	}
	public static void testOID_keyUsage_multipleOU() throws Exception {
		_testOID_keyUsage("multipleOU.cer", false, false);
	}
	private static void _testOID_keyUsage(String nomeCertificato, boolean forSign, boolean forAuth) throws Exception {
		
		System.out.println("========================= Test OID KeyUsage ["+nomeCertificato+"] ==============================");
		
		Certificate cer = ArchiveLoader.load(Utilities.getAsByteArray(CertificateTest.class.getResourceAsStream(PREFIX+nomeCertificato)));
		
		CertificateInfo cerInfo = cer.getCertificate();
		CertificatePrincipal cp = cerInfo.getSubject();
		
		List<OID> oid = cp.getOID();
		System.out.println("Subject con i seguenti elementi: ["+oid+"]");
		String attesi = "C, ST, L, O, OU, CN, EMAIL_ADDRESS, ORGANIZATION_IDENTIFIER";
		Map<String, String> valoriAttesi = new HashMap<String, String>();
		valoriAttesi.put("C", "IT");
		valoriAttesi.put("ST", "Pisa");
		valoriAttesi.put("L", "Pisa");
		valoriAttesi.put("O", "Link.it");
		valoriAttesi.put("OU", "IPAIT-UO_TEST");
		valoriAttesi.put("CN", "Esempio di Test - Servizi di InteroperabilitÃ  (prova)");
		valoriAttesi.put("EMAIL_ADDRESS", "info@link.it");
		valoriAttesi.put("ORGANIZATION_IDENTIFIER", "CF:IT-00012345678");
		if(nomeCertificato.contains("multipleOU")) {
			attesi = "C, ST, L, O, OU, OU, OU, CN";
			valoriAttesi.put("ST", "Italy");
			valoriAttesi.put("L", "govway_l");
			valoriAttesi.put("O", "govway_o");
			valoriAttesi.put("CN", "govway_test_multiple_out");
		}
		if(!oid.toString().equals("["+attesi+"]")) {
			throw new Exception("Attesi OID seguenti '"+("["+attesi+"]")+"', rilevati '"+oid.toString()+"'");
		}
		String [] tmp = attesi.split(",");
		for (String s : tmp) {
			s = s.trim();
			
			boolean casoSpecialeOU = nomeCertificato.contains("multipleOU") && "OU".equals(s);
			
			int dimensioneAttesa = 1;
			if(casoSpecialeOU) {
				dimensioneAttesa = 3;
			}
			
			String valoreAtteso = null;
			if(!casoSpecialeOU) {
				valoreAtteso = valoriAttesi.get(s);
				if(valoreAtteso==null) {
					throw new Exception("Non è stato definito un valore atteso per '"+s+"'");
				}
			}
			
			if(casoSpecialeOU) {
				if(cp.getInfos(s)==null || cp.getInfos(s).isEmpty()) {
					throw new Exception("Atteso elemento '"+s+"' non rilevato");
				}
				List<String> v = cp.getInfos(s);
				if(v==null || v.size()!=3) {
					throw new Exception("Atteso elemento '"+s+"' con 3 valori, trovati:  "+v);
				}
				if(!v.contains("govway 1st OU")) {
					throw new Exception("Atteso elemento '"+s+"' con 3 valori di cui uno 'govway 1st OU'; non è stato rilevato tra i valori disponibili: "+v);
				}
				if(!v.contains("govway 2nd OU")) {
					throw new Exception("Atteso elemento '"+s+"' con 3 valori di cui uno 'govway 2nd OU'; non è stato rilevato tra i valori disponibili: "+v);
				}
				if(!v.contains("govway OU")) {
					throw new Exception("Atteso elemento '"+s+"' con 3 valori di cui uno 'govway OU'; non è stato rilevato tra i valori disponibili: "+v);
				}
				
				String vPos = cp.getInfo(s, 0);
				if(!"govway 1st OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway 1st OU'; è stato rilevato il valore '"+vPos+"'");
				}
				vPos = cp.getInfo(s, 1);
				if(!"govway 2nd OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway 2nd OU'; è stato rilevato il valore '"+vPos+"'");
				}
				vPos = cp.getInfo(s, 2);
				if(!"govway OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway OU'; è stato rilevato il valore '"+vPos+"'");
				}
			}
			else {
				if(cp.getInfo(s)==null) {
					throw new Exception("Atteso elemento '"+s+"' non rilevato");
				}
				String v = cp.getInfo(s);
				if(!valoreAtteso.equals(v)) {
					throw new Exception("Atteso elemento '"+s+"' con valore '"+valoreAtteso+"'; è stato rilevato invece il valore '"+v+"'");
				}
			}
			
			List<String> l = cp.getInfos(s);
			if(l==null || l.size()!=dimensioneAttesa) {
				throw new Exception("Atteso elemento '"+s+"' con dimensione uguale a "+dimensioneAttesa);
			}
			
			OID o = OID.valueOf(s.toUpperCase());
			if(o==null) {
				throw new Exception("Conversione '"+s+"' non riuscita");
			}
			if(cp.getInfoByOID(o)==null) {
				throw new Exception("Atteso OID '"+o+"' non rilevato");
			}
			l = cp.getInfosByOID(o);
			if(l==null || l.size()!=dimensioneAttesa) {
				throw new Exception("Atteso OID '"+o+"' con dimensione uguale a "+dimensioneAttesa);
			}
			
			org.bouncycastle.asn1.ASN1ObjectIdentifier asn1 = o.getOID();
			if(cp.getInfoByOID(asn1)==null) {
				throw new Exception("Atteso OID '"+asn1+"' non rilevato");
			}
			l = cp.getInfosByOID(asn1);
			if(l==null || l.size()!=dimensioneAttesa) {
				throw new Exception("Atteso OID '"+asn1+"' con dimensione uguale a "+dimensioneAttesa);
			}
			
			if(casoSpecialeOU) {
				if(cp.getInfosByOID(o)==null || cp.getInfosByOID(o).isEmpty()) {
					throw new Exception("Atteso elemento '"+s+"' non rilevato");
				}
				List<String> v = cp.getInfosByOID(o);
				if(v==null || v.size()!=3) {
					throw new Exception("Atteso elemento '"+s+"' con 3 valori, trovati:  "+v);
				}
				if(!v.contains("govway 1st OU")) {
					throw new Exception("Atteso elemento '"+s+"' con 3 valori di cui uno 'govway 1st OU'; non è stato rilevato tra i valori disponibili: "+v);
				}
				if(!v.contains("govway 2nd OU")) {
					throw new Exception("Atteso elemento '"+s+"' con 3 valori di cui uno 'govway 2nd OU'; non è stato rilevato tra i valori disponibili: "+v);
				}
				if(!v.contains("govway OU")) {
					throw new Exception("Atteso elemento '"+s+"' con 3 valori di cui uno 'govway OU'; non è stato rilevato tra i valori disponibili: "+v);
				}
				
				String vPos = cp.getInfoByOID(o, 0);
				if(!"govway 1st OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway 1st OU'; è stato rilevato il valore '"+vPos+"'");
				}
				vPos = cp.getInfoByOID(o, 1);
				if(!"govway 2nd OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway 2nd OU'; è stato rilevato il valore '"+vPos+"'");
				}
				vPos = cp.getInfoByOID(o, 2);
				if(!"govway OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway OU'; è stato rilevato il valore '"+vPos+"'");
				}
			}
			else {
				if(cp.getInfoByOID(o)==null) {
					throw new Exception("Atteso elemento '"+s+"' non rilevato");
				}
				String v = cp.getInfoByOID(o);
				if(!valoreAtteso.equals(v)) {
					throw new Exception("Atteso elemento '"+s+"' con valore '"+valoreAtteso+"'; è stato rilevato invece il valore '"+v+"'");
				}
			}
		}
		
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP)!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getID())!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID())!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfo(OID.COUNTRY_OF_CITIZENSHIP.name())!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP)!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP.getID())!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID())!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfos(OID.COUNTRY_OF_CITIZENSHIP.name())!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP,0)!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getID(),0)!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(),0)!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfo(OID.COUNTRY_OF_CITIZENSHIP.name(),0)!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		
		String defaultValue = "DEF_VALUE";
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP, defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP, defaultValue))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP, defaultValue)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfo(OID.COUNTRY_OF_CITIZENSHIP.name(), defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfo(OID.COUNTRY_OF_CITIZENSHIP.name(), defaultValue))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfo(OID.COUNTRY_OF_CITIZENSHIP.name(), defaultValue)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP, defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP, defaultValue).get(0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP, defaultValue).get(0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue).get(0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue).get(0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue).get(0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfosByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue).get(0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfos(OID.COUNTRY_OF_CITIZENSHIP.name(), defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfos(OID.COUNTRY_OF_CITIZENSHIP.name(), defaultValue).get(0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfos(OID.COUNTRY_OF_CITIZENSHIP.name(), defaultValue).get(0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP, defaultValue,0)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP, defaultValue, 0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP, defaultValue, 0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue,0)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue, 0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue, 0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue,0)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue, 0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfoByOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue, 0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfo(OID.COUNTRY_OF_CITIZENSHIP.name(), defaultValue,0)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfo(OID.COUNTRY_OF_CITIZENSHIP.name(), defaultValue, 0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfo(OID.COUNTRY_OF_CITIZENSHIP.name(), defaultValue, 0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		
		List<KeyUsage> keyUsage = cerInfo.getKeyUsage();
		System.out.println("KeyUsage: ["+keyUsage+"]");
		if(forSign) {
			attesi = "DIGITAL_SIGNATURE, NON_REPUDIATION"; 
		}
		else if(forAuth) {
			attesi = "KEY_ENCIPHERMENT";
		}
		else {
			attesi = "";
		}
		if(!keyUsage.toString().equals("["+attesi+"]")) {
			throw new Exception("Attesi KeyUsage seguenti '"+("["+attesi+"]")+"', rilevati '"+keyUsage.toString()+"'");
		}
		if(!"".equals(attesi)) {
			tmp = attesi.split(",");
			for (String s : tmp) {
				s = s.trim();
				
				if(!cerInfo.hasKeyUsage(s)) {
					throw new Exception("Atteso KeyUsage '"+s+"'; non è stato rilevato");
				}
				
				KeyUsage k = KeyUsage.valueOf(s);
				if(!cerInfo.hasKeyUsage(k)) {
					throw new Exception("Atteso KeyUsage '"+k+"'; non è stato rilevato");
				}
			}
		}
		if(cerInfo.hasKeyUsage(KeyUsage.KEY_AGREEMENT)) {
			throw new Exception("KeyUsage '"+KeyUsage.KEY_AGREEMENT+"' rilevato ma non atteso");
		}
		
		List<ExtendedKeyUsage> extendedKeyUsage = cerInfo.getExtendedKeyUsage();
		System.out.println("ExtendedKeyUsage: ["+extendedKeyUsage+"]");
		if(forSign || forAuth) {
			attesi = "SERVER_AUTH, CLIENT_AUTH"; 
		}
		else {
			attesi = "";
		}
		if(!extendedKeyUsage.toString().equals("["+attesi+"]")) {
			throw new Exception("Attesi ExtendedKeyUsage seguenti '"+("["+attesi+"]")+"', rilevati '"+extendedKeyUsage.toString()+"'");
		}
		if(!"".equals(attesi)) {
			tmp = attesi.split(",");
			for (String s : tmp) {
				s = s.trim();
				
				if(!cerInfo.hasExtendedKeyUsage(s)) {
					throw new Exception("Atteso KeyUsage '"+s+"'; non è stato rilevato");
				}
				
				ExtendedKeyUsage k = ExtendedKeyUsage.valueOf(s);
				if(!cerInfo.hasExtendedKeyUsage(k)) {
					throw new Exception("Atteso ExtendedKeyUsage '"+k+"'; non è stato rilevato");
				}
			}
		}
		if(cerInfo.hasExtendedKeyUsage(ExtendedKeyUsage.DVCS)) {
			throw new Exception("ExtendedKeyUsage '"+ExtendedKeyUsage.DVCS+"' rilevato ma non atteso");
		}
		
		List<String> extendedKeyUsageOID = cerInfo.getExtendedKeyUsageByOID();
		if(forSign || forAuth) {
			attesi = "1.3.6.1.5.5.7.3.1, 1.3.6.1.5.5.7.3.2";
		}
		else {
			attesi = "";
		}
		if("".equals(attesi)) {
			if(extendedKeyUsageOID!=null) {
				throw new Exception("ExtendedKeyUsage non attesi: "+extendedKeyUsageOID);
			}
		}
		else {
			if(!extendedKeyUsageOID.toString().equals("["+attesi+"]")) {
				throw new Exception("Attesi ExtendedKeyUsage OID seguenti '"+("["+attesi+"]")+"', rilevati '"+extendedKeyUsageOID.toString()+"'");
			}
		}
		
	}
}
