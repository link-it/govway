/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateUtils;
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
				
				Hashtable<String, List<String>> c1_valori = CertificateUtils.getPrincipalIntoHashtable(c1.getCertificate().getSubject().getName(), PrincipalType.subject);
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
}
