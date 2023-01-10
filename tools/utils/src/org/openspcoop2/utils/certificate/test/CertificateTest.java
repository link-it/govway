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

import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.AuthorityInformationAccess;
import org.openspcoop2.utils.certificate.AuthorityKeyIdentifier;
import org.openspcoop2.utils.certificate.BasicConstraints;
import org.openspcoop2.utils.certificate.CRLDistributionPoint;
import org.openspcoop2.utils.certificate.CRLDistributionPoints;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificatePolicy;
import org.openspcoop2.utils.certificate.CertificatePolicyEntry;
import org.openspcoop2.utils.certificate.CertificatePrincipal;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.ExtendedKeyUsage;
import org.openspcoop2.utils.certificate.Extensions;
import org.openspcoop2.utils.certificate.KeyUsage;
import org.openspcoop2.utils.certificate.OID;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.certificate.SubjectAlternativeNames;

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
	public final static String PREFIX = "/org/openspcoop2/utils/certificate/test/";
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
			else if(ArchiveType.CER.equals(srcType) && ((i%2)!=0)) {
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
				else if(ArchiveType.CER.equals(destType) && ((j%2)!=0)) {
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
		
		
		// *** OID (solo standard) ***
		
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
		
		
		
		// ***** ID OID (anche custom) ***********
		
		List<String> id_oid = cp.getIdOID();
		System.out.println("Subject con i seguenti elementi by id ["+id_oid+"]");
		String attesi_id = OID.C.getID()+", "+OID.ST.getID()+", "+OID.L.getID()+", "+OID.O.getID()+", "+OID.OU.getID()+", "+OID.CN.getID()+", "+OID.EMAIL_ADDRESS.getID()+", "+OID.ORGANIZATION_IDENTIFIER.getID()+
				", 2.9.9.98, 2.9.9.99";
		Map<String, String> valoriAttesiById = new HashMap<String, String>();
		valoriAttesiById.put(OID.C.getID(), "IT");
		valoriAttesiById.put(OID.ST.getID(), "Pisa");
		valoriAttesiById.put(OID.L.getID(), "Pisa");
		valoriAttesiById.put(OID.O.getID(), "Link.it");
		valoriAttesiById.put(OID.OU.getID(), "IPAIT-UO_TEST");
		valoriAttesiById.put(OID.CN.getID(), "Esempio di Test - Servizi di InteroperabilitÃ  (prova)");
		valoriAttesiById.put(OID.EMAIL_ADDRESS.getID(), "info@link.it");
		valoriAttesiById.put(OID.ORGANIZATION_IDENTIFIER.getID(), "CF:IT-00012345678");
		valoriAttesiById.put("2.9.9.98", "CF:IT-00099999999");
		valoriAttesiById.put("2.9.9.99", "Prova di un valore complesso - test");
		if(nomeCertificato.contains("multipleOU")) {
			attesi_id = OID.C.getID()+", "+OID.ST.getID()+", "+OID.L.getID()+", "+OID.O.getID()+", "+OID.OU.getID()+", "+OID.OU.getID()+", "+OID.OU.getID()+", "+OID.CN.getID();
			valoriAttesiById.put(OID.ST.getID(), "Italy");
			valoriAttesiById.put(OID.L.getID(), "govway_l");
			valoriAttesiById.put(OID.O.getID(), "govway_o");
			valoriAttesiById.put(OID.CN.getID(), "govway_test_multiple_out");
		}
		
		if(!id_oid.toString().equals("["+attesi_id+"]")) {
			throw new Exception("Attesi identificativi di OID seguenti '"+("["+attesi_id+"]")+"', rilevati '"+id_oid.toString()+"'");
		}
		
		tmp = attesi_id.split(",");
		for (String s : tmp) {
			s = s.trim();
			
			boolean casoSpecialeOU = nomeCertificato.contains("multipleOU") && OID.OU.getID().equals(s);
			
			int dimensioneAttesa = 1;
			if(casoSpecialeOU) {
				dimensioneAttesa = 3;
			}
			
			String valoreAtteso = null;
			if(!casoSpecialeOU) {
				valoreAtteso = valoriAttesiById.get(s);
				if(valoreAtteso==null) {
					throw new Exception("Non è stato definito un valore atteso per '"+s+"'");
				}
			}
			
			if(casoSpecialeOU) {
				if(cp.getInfosByIdOID(s)==null || cp.getInfosByIdOID(s).isEmpty()) {
					throw new Exception("Atteso elemento '"+s+"' non rilevato");
				}
				List<String> v = cp.getInfosByIdOID(s);
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
				
				String vPos = cp.getInfoByIdOID(s, 0);
				if(!"govway 1st OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway 1st OU'; è stato rilevato il valore '"+vPos+"'");
				}
				vPos = cp.getInfoByIdOID(s, 1);
				if(!"govway 2nd OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway 2nd OU'; è stato rilevato il valore '"+vPos+"'");
				}
				vPos = cp.getInfoByIdOID(s, 2);
				if(!"govway OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway OU'; è stato rilevato il valore '"+vPos+"'");
				}
			}
			else {
				if(cp.getInfoByIdOID(s)==null) {
					throw new Exception("Atteso elemento '"+s+"' non rilevato");
				}
				String v = cp.getInfoByIdOID(s);
				if(!valoreAtteso.equals(v)) {
					throw new Exception("Atteso elemento '"+s+"' con valore '"+valoreAtteso+"'; è stato rilevato invece il valore '"+v+"'");
				}
			}
			
			List<String> l = cp.getInfosByIdOID(s);
			if(l==null || l.size()!=dimensioneAttesa) {
				throw new Exception("Atteso elemento '"+s+"' con dimensione uguale a "+dimensioneAttesa);
			}
			
			org.bouncycastle.asn1.ASN1ObjectIdentifier asn1 = new org.bouncycastle.asn1.ASN1ObjectIdentifier(s);
			if(cp.getInfoByIdOID(asn1)==null) {
				throw new Exception("Atteso OID '"+asn1+"' non rilevato");
			}
			l = cp.getInfosByIdOID(asn1);
			if(l==null || l.size()!=dimensioneAttesa) {
				throw new Exception("Atteso OID '"+asn1+"' con dimensione uguale a "+dimensioneAttesa);
			}
			
			if(casoSpecialeOU) {
				if(cp.getInfosByIdOID(asn1)==null || cp.getInfosByIdOID(asn1).isEmpty()) {
					throw new Exception("Atteso elemento '"+s+"' non rilevato");
				}
				List<String> v = cp.getInfosByIdOID(asn1);
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
				
				String vPos = cp.getInfoByIdOID(asn1, 0);
				if(!"govway 1st OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway 1st OU'; è stato rilevato il valore '"+vPos+"'");
				}
				vPos = cp.getInfoByIdOID(asn1, 1);
				if(!"govway 2nd OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway 2nd OU'; è stato rilevato il valore '"+vPos+"'");
				}
				vPos = cp.getInfoByIdOID(asn1, 2);
				if(!"govway OU".equals(vPos)) {
					throw new Exception("Atteso elemento '"+s+"' con valore 'govway OU'; è stato rilevato il valore '"+vPos+"'");
				}
			}
			else {
				if(cp.getInfoByIdOID(asn1)==null) {
					throw new Exception("Atteso elemento '"+s+"' non rilevato");
				}
				String v = cp.getInfoByIdOID(asn1);
				if(!valoreAtteso.equals(v)) {
					throw new Exception("Atteso elemento '"+s+"' con valore '"+valoreAtteso+"'; è stato rilevato invece il valore '"+v+"'");
				}
			}
		}
		
		if(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID())!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID())!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfosByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID())!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfosByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID())!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID(),0)!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		if(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(),0)!=null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' rilevato ma non atteso");
		}
		
		defaultValue = "DEF_VALUE";
		if(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfosByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfosByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue).get(0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfosByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue).get(0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfosByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfosByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue).get(0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfosByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue).get(0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue,0)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue, 0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getID(), defaultValue, 0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		if(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue,0)==null) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value non rilevato");
		}
		else if(!defaultValue.equals(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue, 0))) {
			throw new Exception("OID '"+OID.COUNTRY_OF_CITIZENSHIP+"' con default value rilevato con un valore '"+defaultValue.equals(cp.getInfoByIdOID(OID.COUNTRY_OF_CITIZENSHIP.getOID(), defaultValue, 0)+"' differente da quello atteso '"+defaultValue+"'"));
		}
		
		
		// ***** KEY USAGE ********
		
		List<KeyUsage> keyUsage = cerInfo.getKeyUsage();
		System.out.println("\nKeyUsage: ["+keyUsage+"]");
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
				
				if(!cerInfo.hasKeyUsageByArrayBooleanPosition(k.getX509CertificatePosition())) {
					throw new Exception("Atteso KeyUsage '"+k+"' con position '"+k.getX509CertificatePosition()+"'; non è stato rilevato");
				}
				if(!cerInfo.hasKeyUsageByBouncycastleCode(k.getBouncyCastleCode())) {
					throw new Exception("Atteso KeyUsage '"+k+"' con bc code '"+k.getBouncyCastleCode()+"'; non è stato rilevato");
				}
			}
		}
		if(cerInfo.hasKeyUsage(KeyUsage.KEY_AGREEMENT)) {
			throw new Exception("KeyUsage '"+KeyUsage.KEY_AGREEMENT+"' rilevato ma non atteso");
		}
		if(cerInfo.hasKeyUsageByArrayBooleanPosition(KeyUsage.KEY_AGREEMENT.getX509CertificatePosition())) {
			throw new Exception("KeyUsage '"+KeyUsage.KEY_AGREEMENT+"' con position '"+KeyUsage.KEY_AGREEMENT.getX509CertificatePosition()+"' rilevato ma non atteso");
		}
		if(cerInfo.hasKeyUsageByBouncycastleCode(KeyUsage.KEY_AGREEMENT.getBouncyCastleCode())) {
			throw new Exception("KeyUsage '"+KeyUsage.KEY_AGREEMENT+"' con bc code '"+KeyUsage.KEY_AGREEMENT.getBouncyCastleCode()+"' rilevato ma non atteso");
		}
		
		
		
		// ***** EXTENDED KEY USAGE ********
		
		List<ExtendedKeyUsage> extendedKeyUsage = cerInfo.getExtendedKeyUsage();
		System.out.println("\nExtendedKeyUsage: ["+extendedKeyUsage+"]");
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
				
				if(!cerInfo.hasExtendedKeyUsageByOID(k.getId())) {
					throw new Exception("Atteso ExtendedKeyUsage '"+k+"' oid:'"+k.getId()+"'; non è stato rilevato");
				}
				if(!cerInfo.hasExtendedKeyUsageByBouncycastleKeyPurposeId(k.getPurposeId().getId())) {
					throw new Exception("Atteso ExtendedKeyUsage '"+k+"' bc id:'"+k.getPurposeId().getId()+"'; non è stato rilevato");
				}
			}
		}
		if(cerInfo.hasExtendedKeyUsage(ExtendedKeyUsage.DVCS)) {
			throw new Exception("ExtendedKeyUsage '"+ExtendedKeyUsage.DVCS+"' rilevato ma non atteso");
		}
		if(cerInfo.hasExtendedKeyUsageByOID(ExtendedKeyUsage.DVCS.getId())) {
			throw new Exception("ExtendedKeyUsage '"+ExtendedKeyUsage.DVCS+"' oid:'"+ExtendedKeyUsage.DVCS.getId()+"' rilevato ma non atteso");
		}
		if(cerInfo.hasExtendedKeyUsageByBouncycastleKeyPurposeId(ExtendedKeyUsage.DVCS.getPurposeId().getId())) {
			throw new Exception("ExtendedKeyUsage '"+ExtendedKeyUsage.DVCS+"' bc id:'"+ExtendedKeyUsage.DVCS.getPurposeId().getId()+"' rilevato ma non atteso");
		}
		
		List<String> extendedKeyUsageOID = cerInfo.getExtendedKeyUsageByOID();
		if(forSign || forAuth) {
			attesi = "1.3.6.1.5.5.7.3.1, 1.3.6.1.5.5.7.3.2, 1.2.3.4, 1.2.3.4.1";
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
			
			tmp = attesi.split(",");
			for (String s : tmp) {
				s = s.trim();
			
				if(!cerInfo.hasExtendedKeyUsageByOID(s)) {
					throw new Exception("Atteso ExtendedKeyUsage oid:'"+s+"'; non è stato rilevato");
				}
			}
		}
		
		
		
		// ***** CERTIFICATE POLICY ********
		
		List<CertificatePolicy> l = cerInfo.getCertificatePolicies();
		int sizeAttesi = 0;
		if(forSign || forAuth) {
			sizeAttesi = 3;
		}
		int sizeRiscontrati = l!=null ? l.size() : 0;
		if(sizeAttesi != sizeRiscontrati) {
			throw new Exception("Attesi "+sizeAttesi+" certificate policy, riscontrate: "+sizeRiscontrati);
		}
		if(l!=null) {
			for (int i = 0; i < l.size(); i++) {
				
				if(i==0) {
					attesi = "1.2.3.5";
				}
				else if(i==1) {
					attesi = "1.5.6.7.8";
				}
				else if(i==2) {
					attesi = "1.3.5.8";
				}
				
				CertificatePolicy certificatePolicy = l.get(i);
				System.out.println("\nCertificatePolicy: ");
				System.out.println(certificatePolicy);
				
				if(!attesi.equals(certificatePolicy.getOID())) {
					throw new Exception("Attesa certificate policy '"+attesi+"', riscontrata: "+certificatePolicy.getOID());
				}
				
				CertificatePolicy cerTest = cerInfo.getCertificatePolicy(attesi);
				if(cerTest==null) {
					throw new Exception("Attesa certificate policy '"+attesi+"', non riscontrata");
				}
				if(!attesi.equals(cerTest.getOID())) {
					throw new Exception("Attesa certificate policy '"+attesi+"', riscontrata: "+cerTest.getOID());
				}
				
				cerTest = cerInfo.getCertificatePolicyByOID(attesi);
				if(cerTest==null) {
					throw new Exception("Attesa certificate policy '"+attesi+"', non riscontrata");
				}
				if(!attesi.equals(cerTest.getOID())) {
					throw new Exception("Attesa certificate policy '"+attesi+"', riscontrata: "+cerTest.getOID());
				}
				
				boolean exists = cerInfo.hasCertificatePolicy(attesi);
				if(!exists) {
					throw new Exception("Attesa certificate policy '"+attesi+"', non riscontrata");
				}
				
				String qualifierId = "1.3.6.1.5.5.7.2.1";
				String qualifierId2 = "1.3.6.1.5.5.7.2.2";
				
				if(i==0 || i==1) {
					exists = certificatePolicy.hasCertificatePolicyQualifier(qualifierId);
					if(exists) {
						throw new Exception("Rilevato certificate policy qualifier non atteso '"+qualifierId+"'");
					}
					
					CertificatePolicyEntry ce = certificatePolicy.getQualifier(qualifierId);
					if(ce!=null) {
						throw new Exception("Rilevato certificate policy qualifier non atteso '"+ce.getOID()+"'");
					}
					
				}
				else {
					exists = certificatePolicy.hasCertificatePolicyQualifier(qualifierId);
					if(!exists) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"'; non rilevato");
					}
					
					String url1 = "http://my.host.example.com/";
					
					CertificatePolicyEntry ce = certificatePolicy.getQualifier(qualifierId);
					if(ce==null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"'; non rilevato");
					}
					if(!qualifierId.equals(ce.getOID())) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"'; rilevato '"+ce.getOID()+"'");
					}
					if(!ce.containsValue(url1)) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"' con url '"+url1+"'; non rilevato");
					}
					String value = ce.getValue(0);
					if(!url1.equals(value)) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"' con url '"+url1+"'; rilevato '"+value+"'");
					}
					
					ce = certificatePolicy.getQualifier(0);
					if(ce==null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"'; non rilevato");
					}
					if(!qualifierId.equals(ce.getOID())) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"'; rilevato '"+ce.getOID()+"'");
					}
					if(!ce.containsValue(url1)) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"' con url '"+url1+"'; non rilevato");
					}
					value = ce.getValue(0);
					if(!url1.equals(value)) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"' con url '"+url1+"'; rilevato '"+value+"'");
					}
					
					String url2 = "http://my.your.example.com/";
					
					ce = certificatePolicy.getQualifier(1);
					if(ce==null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"'; non rilevato");
					}
					if(!qualifierId.equals(ce.getOID())) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"'; rilevato '"+ce.getOID()+"'");
					}
					if(!ce.containsValue(url2)) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"' con url '"+url1+"'; non rilevato");
					}
					value = ce.getValue(0);
					if(!url2.equals(value)) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId+"' con url '"+url1+"'; rilevato '"+value+"'");
					}
					
					
					
					
					ce = certificatePolicy.getQualifier(qualifierId2);
					if(ce==null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"'; non rilevato");
					}
					if(!qualifierId2.equals(ce.getOID())) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"'; rilevato '"+ce.getOID()+"'");
					}
					value = ce.getValue(0);
					if(value!=null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con null value; rilevato '"+value+"'");
					}
					
					ce = certificatePolicy.getQualifier(2);
					if(ce==null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"'; non rilevato");
					}
					if(!qualifierId2.equals(ce.getOID())) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"'; rilevato '"+ce.getOID()+"'");
					}
					value = ce.getValue(0);
					if(value!=null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con null value; rilevato '"+value+"'");
					}
					
					String testo = "Explicit Text Here";
					
					ce = ce.getEntry(0);
					if(ce==null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con valore interno; non rilevato");
					}
					if(!ce.containsValue(testo)) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con valore interno '"+testo+"'; non rilevato");
					}
					value = ce.getValue(0);
					if(!testo.equals(value)) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con valore interno '"+testo+"'; rilevato '"+value+"'");
					}
					
					testo = "Organisation Name";

					ce = ce.getEntry(0);
					if(ce==null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con valore interno.interno; non rilevato");
					}
					if(!ce.containsValue(testo)) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con valore interno.interno '"+testo+"'; non rilevato");
					}
					value = ce.getValue(0);
					if(!testo.equals(value)) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con valore interno.interno '"+testo+"'; rilevato '"+value+"'");
					}
					
					int sizeAttesoInterno3Livello = 4;
					
					ce = ce.getEntry(0);
					if(ce==null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con valore interno.interno.interno; non rilevato");
					}
					if(ce.getValues()==null) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con valori interno.interno.interno.interno; non rilevato");
					}
					if(ce.getValues().size() != sizeAttesoInterno3Livello) {
						throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con "+sizeAttesoInterno3Livello+" valori interno.interno.interno.interno; rilevati: "+ce.getValues().size());
					}
					
					for (int j = 1; j < 5; j++) {
						String valoreAtteso = j+"";
						if(!ce.containsValue(valoreAtteso)) {
							throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con valore interno.interno.interno.interno '"+valoreAtteso+"'; non rilevato");
						}
						value = ce.getValue((j-1));
						if(!valoreAtteso.equals(value)) {
							throw new Exception("Atteso certificate policy qualifier '"+qualifierId2+"' con valore interno.interno.interno.interno '"+valoreAtteso+"'; rilevato '"+value+"'");
						}
					}
				}
			}
		}

		
		// ***** BASIC CONSTRAINTS ********
		
		System.out.println("\nBasicConstraints isCA:"+cerInfo.isCA()+" pathLen:"+cerInfo.getPathLen());
		BasicConstraints bc = cerInfo.getBasicConstraints();
		if(forSign || forAuth) {
			System.out.println("\t pathLen (from object):"+bc.getPathLen());
		}

		
		// ***** AUTHORITY KEY IDENTIFIER *****
		
		AuthorityKeyIdentifier aki = cerInfo.getAuthorityKeyIdentifier();
		if(aki!=null) {
			System.out.println("\nAuthorityKeyIdentifier: "+aki.getBase64KeyIdentifier());
		}
		
		
		// ***** AUTHORITY INFORMATION ACCESS *****
		
		AuthorityInformationAccess aia = cerInfo.getAuthorityInformationAccess();
		if(forSign || forAuth) {
			System.out.println("\nAuthorityInformationAccess");
			System.out.println("\tCAIssuers:");
			for (String ca : aia.getCAIssuers()) {
				System.out.println("\t\t"+ca);	
			}
			System.out.println("\tOCSP:");
			for (String ocsp : aia.getOCSPs()) {
				System.out.println("\t\t"+ocsp);	
			}
			
			
			// ### CAISSUER#
			
			String CAIssuer1 = "http://ca.govway.org/testCA1.crt";
			String CAIssuer2 = "https://ca.govway.org/testCA2.crt";
						
			sizeRiscontrati = aia.getCAIssuers().size();
			sizeAttesi = 2;
			if(sizeAttesi!=sizeRiscontrati) {
				throw new Exception("Attesi "+sizeAttesi+" CAIssuers, riscontrate: "+sizeRiscontrati);
			}
			
			for (int i = 0; i < aia.getCAIssuers().size(); i++) {
				String ca = aia.getCAIssuers().get(i);
				
				String attesa = null;
				if(i==0) {
					attesa = CAIssuer1;
				}
				else {
					attesa = CAIssuer2;
				}
				
				if(ca==null) {
					throw new Exception("Attesa CAIssuer '"+attesa+"'; non rilevata");
				}
				if(!attesa.equals(ca)) {
					throw new Exception("Atteso CAIssuer '"+attesa+"'; rilevata '"+ca+"'");
				}
							
				ca = aia.getCAIssuer(i);
				if(ca==null) {
					throw new Exception("Attesa CAIssuer '"+attesa+"' (index); non rilevata");
				}
				if(!attesa.equals(ca)) {
					throw new Exception("Atteso CAIssuer '"+attesa+"' (index); rilevata '"+ca+"'");
				}
				
				if(!aia.containsCAIssuer(attesa)) {
					throw new Exception("Atteso CAIssuer '"+attesa+"'; non rilevata (contains)");
				}
			}
			
			sizeRiscontrati = aia.getObjectCAIssuers().size();
			sizeAttesi = 2;
			if(sizeAttesi!=sizeRiscontrati) {
				throw new Exception("Attesi "+sizeAttesi+" CAIssuers (object), riscontrate: "+sizeRiscontrati);
			}
			
			for (int i = 0; i < aia.getObjectCAIssuers().size(); i++) {
				GeneralName caName = aia.getObjectCAIssuers().get(i);
				
				String attesa = null;
				int tagName = 6;
				if(i==0) {
					attesa = CAIssuer1;
				}
				else {
					attesa = CAIssuer2;
				}
				
				if(caName==null) {
					throw new Exception("Attesa CAIssuer '"+attesa+"'; non rilevata");
				}
				
				String ca=caName.getName()!=null ? caName.getName().toString() : null;
				if(ca==null) {
					throw new Exception("Attesa CAIssuer '"+attesa+"'; non rilevata");
				}
				if(!attesa.equals(ca)) {
					throw new Exception("Atteso CAIssuer '"+attesa+"'; rilevata '"+ca+"'");
				}
				
				int tagNameCa = caName.getTagNo();
				if(tagName!=tagNameCa) {
					throw new Exception("Atteso CAIssuer tagName '"+tagName+"'; rilevata '"+tagNameCa+"'");
				}
							
				caName = aia.getObjectCAIssuer(i);
				if(caName==null) {
					throw new Exception("Attesa CAIssuer '"+attesa+"'; non rilevata");
				}
				
				ca=caName.getName()!=null ? caName.getName().toString() : null;
				if(ca==null) {
					throw new Exception("Attesa CAIssuer '"+attesa+"' (index); non rilevata");
				}
				if(!attesa.equals(ca)) {
					throw new Exception("Atteso CAIssuer '"+attesa+"' (index); rilevata '"+ca+"'");
				}
				
				tagNameCa = caName.getTagNo();
				if(tagName!=tagNameCa) {
					throw new Exception("Atteso CAIssuer tagName '"+tagName+"'; rilevata '"+tagNameCa+"'");
				}
				
				if(!aia.containsCAIssuer(attesa)) {
					throw new Exception("Atteso CAIssuer '"+attesa+"'; non rilevata (contains)");
				}
				if(!aia.containsCAIssuer(tagName, attesa)) {
					throw new Exception("Atteso CAIssuer '"+attesa+"'; non rilevata (contains-tagName)");
				}
			}
			
			// ### OCSP#
			
			String OCSP1 = "http://ocsp.govway.org/test1";
			String OCSP2 = "https://ocsp.govway.org/test2";
			String OCSP3 = "https://ocsp.govway.org/test3";
			
			sizeRiscontrati = aia.getOCSPs().size();
			sizeAttesi = 3;
			if(sizeAttesi!=sizeRiscontrati) {
				throw new Exception("Attesi "+sizeAttesi+" OCSPs, riscontrate: "+sizeRiscontrati);
			}
			
			for (int i = 0; i < aia.getOCSPs().size(); i++) {
				String ca = aia.getOCSPs().get(i);
				
				String attesa = null;
				if(i==0) {
					attesa = OCSP1;
				}
				else if(i==1) {
					attesa = OCSP2;
				}
				else {
					attesa = OCSP3;
				}
				
				if(ca==null) {
					throw new Exception("Attesa OCSP '"+attesa+"'; non rilevata");
				}
				if(!attesa.equals(ca)) {
					throw new Exception("Atteso OCSP '"+attesa+"'; rilevata '"+ca+"'");
				}
							
				ca = aia.getOCSP(i);
				if(ca==null) {
					throw new Exception("Attesa OCSP '"+attesa+"' (index); non rilevata");
				}
				if(!attesa.equals(ca)) {
					throw new Exception("Atteso OCSP '"+attesa+"' (index); rilevata '"+ca+"'");
				}
				
				if(!aia.containsOCSP(attesa)) {
					throw new Exception("Atteso OCSP '"+attesa+"'; non rilevata (contains)");
				}
			}
			
			sizeRiscontrati = aia.getObjectOCSPs().size();
			sizeAttesi = 3;
			if(sizeAttesi!=sizeRiscontrati) {
				throw new Exception("Attesi "+sizeAttesi+" OCSPs (object), riscontrate: "+sizeRiscontrati);
			}
			
			for (int i = 0; i < aia.getObjectOCSPs().size(); i++) {
				GeneralName caName = aia.getObjectOCSPs().get(i);
				
				String attesa = null;
				int tagName = 6;
				if(i==0) {
					attesa = OCSP1;
				}
				else if(i==1) {
					attesa = OCSP2;
				}
				else {
					attesa = OCSP3;
				}
				
				if(caName==null) {
					throw new Exception("Attesa OCSP '"+attesa+"'; non rilevata");
				}
				
				String ca=caName.getName()!=null ? caName.getName().toString() : null;
				if(ca==null) {
					throw new Exception("Attesa OCSP '"+attesa+"'; non rilevata");
				}
				if(!attesa.equals(ca)) {
					throw new Exception("Atteso OCSP '"+attesa+"'; rilevata '"+ca+"'");
				}
				
				int tagNameCa = caName.getTagNo();
				if(tagName!=tagNameCa) {
					throw new Exception("Atteso OCSP tagName '"+tagName+"'; rilevata '"+tagNameCa+"'");
				}
							
				caName = aia.getObjectOCSP(i);
				if(caName==null) {
					throw new Exception("Attesa OCSP '"+attesa+"'; non rilevata");
				}
				
				ca=caName.getName()!=null ? caName.getName().toString() : null;
				if(ca==null) {
					throw new Exception("Attesa OCSP '"+attesa+"' (index); non rilevata");
				}
				if(!attesa.equals(ca)) {
					throw new Exception("Atteso OCSP '"+attesa+"' (index); rilevata '"+ca+"'");
				}
				
				tagNameCa = caName.getTagNo();
				if(tagName!=tagNameCa) {
					throw new Exception("Atteso OCSP tagName '"+tagName+"'; rilevata '"+tagNameCa+"'");
				}
				
				if(!aia.containsOCSP(attesa)) {
					throw new Exception("Atteso OCSP '"+attesa+"'; non rilevata (contains)");
				}
				if(!aia.containsOCSP(tagName, attesa)) {
					throw new Exception("Atteso OCSP '"+attesa+"'; non rilevata (contains-tagName)");
				}
			}
			
		}
		
		// ***** CRL *****
				
		CRLDistributionPoints points = CRLDistributionPoints.getCRLDistributionPoints(cerInfo.getEncoded());
		if(forSign || forAuth) {
			System.out.println("\nCRLDistributionPoints");
			
			String CRL1 = "http://crl.govway.org/test1.crl";
			String CRL2 = "https://crl.govway.org/test2.pem";
			
			for (int j = 0; j < points.getCRLDistributionPoints().size(); j++) {
				CRLDistributionPoint crl = points.getCRLDistributionPoints().get(j);
				System.out.println("\tPoint:");
				System.out.println("\t\tAddress (size:"+crl.getDistributionPointNames().size()+")");
				for (String point : crl.getDistributionPointNames()) {
					System.out.println("\t\t\t"+point);	
				}
				System.out.println("\t\tPointType:"+crl.getDistributionPointType());
				System.out.println("\t\tReason:"+crl.getReasonFlags());
				System.out.println("\t\tCAIssuers (size:"+crl.getCRLIssuers().size()+")");
				for (String crlIssuer : crl.getCRLIssuers()) {
					System.out.println("\t\t\t"+crlIssuer);	
				}
								
				sizeRiscontrati = crl.getDistributionPointNames().size();
				sizeAttesi = 1;
				if(sizeAttesi!=sizeRiscontrati) {
					throw new Exception("Attesi "+sizeAttesi+" OCSPs, riscontrate: "+sizeRiscontrati);
				}
				
				for (int i = 0; i < crl.getDistributionPointNames().size(); i++) {
					String ca = crl.getDistributionPointNames().get(i);
					
					String attesa = null;
					if(j==0) {
						attesa = CRL1;
					}
					else {
						attesa = CRL2;
					}
					
					if(ca==null) {
						throw new Exception("Attesa DistributionPointName '"+attesa+"'; non rilevata");
					}
					if(!attesa.equals(ca)) {
						throw new Exception("Atteso DistributionPointName '"+attesa+"'; rilevata '"+ca+"'");
					}
								
					ca = crl.getDistributionPointName(i);
					if(ca==null) {
						throw new Exception("Attesa DistributionPointName '"+attesa+"' (index); non rilevata");
					}
					if(!attesa.equals(ca)) {
						throw new Exception("Atteso DistributionPointName '"+attesa+"' (index); rilevata '"+ca+"'");
					}
					
					if(!crl.containsDistributionPointName(attesa)) {
						throw new Exception("Atteso DistributionPointName '"+attesa+"'; non rilevata (contains)");
					}
				}
				
				sizeRiscontrati = crl.getObjectDistributionPointNames().size();
				sizeAttesi = 3;
				if(sizeAttesi!=3) {
					throw new Exception("Attesi "+sizeAttesi+" DistributionPointNames (object), riscontrate: "+sizeRiscontrati);
				}
				
				for (int i = 0; i < crl.getObjectDistributionPointNames().size(); i++) {
					GeneralName caName = crl.getObjectDistributionPointNames().get(i);
					
					String attesa = null;
					int tagName = 6;
					if(j==0) {
						attesa = CRL1;
					}
					else {
						attesa = CRL2;
					}
					
					if(caName==null) {
						throw new Exception("Attesa DistributionPointName '"+attesa+"'; non rilevata");
					}
					
					String ca=caName.getName()!=null ? caName.getName().toString() : null;
					if(ca==null) {
						throw new Exception("Attesa DistributionPointName '"+attesa+"'; non rilevata");
					}
					if(!attesa.equals(ca)) {
						throw new Exception("Atteso DistributionPointName '"+attesa+"'; rilevata '"+ca+"'");
					}
					
					int tagNameCa = caName.getTagNo();
					if(tagName!=tagNameCa) {
						throw new Exception("Atteso DistributionPointName tagName '"+tagName+"'; rilevata '"+tagNameCa+"'");
					}
								
					caName = crl.getObjectDistributionPointName(i);
					if(caName==null) {
						throw new Exception("Attesa DistributionPointName '"+attesa+"'; non rilevata");
					}
					
					ca=caName.getName()!=null ? caName.getName().toString() : null;
					if(ca==null) {
						throw new Exception("Attesa DistributionPointName '"+attesa+"' (index); non rilevata");
					}
					if(!attesa.equals(ca)) {
						throw new Exception("Atteso DistributionPointName '"+attesa+"' (index); rilevata '"+ca+"'");
					}
					
					tagNameCa = caName.getTagNo();
					if(tagName!=tagNameCa) {
						throw new Exception("Atteso DistributionPointName tagName '"+tagName+"'; rilevata '"+tagNameCa+"'");
					}
					
					if(!crl.containsDistributionPointName(attesa)) {
						throw new Exception("Atteso DistributionPointName '"+attesa+"'; non rilevata (contains)");
					}
					if(!crl.containsDistributionPointName(tagName, attesa)) {
						throw new Exception("Atteso DistributionPointName '"+attesa+"'; non rilevata (contains-tagName)");
					}
				}
			}
		}
		
		// ***** SUBJECT ALTERNATIVE NAMES *****
		
		SubjectAlternativeNames san = cerInfo.getSubjectAlternativeNames();
		if(forSign || forAuth) {
			System.out.println("\nSubjectAlternativeNames");
			for (String an : san.getAlternativeNames()) {
				System.out.println("\t"+an);	
			}

			
			String AlternativeName1 = "esempio-domain1.com";
			String AlternativeName2 = "www.esempio-altro-dominio.com";
			String AlternativeName3 = "esempio-domain3.org";
					
			sizeRiscontrati = cerInfo.getAlternativeNames().size();
			sizeAttesi = 3;
			if(sizeAttesi!=sizeRiscontrati) {
				throw new Exception("Attesi "+sizeAttesi+" AlternativeNames, riscontrati: "+sizeRiscontrati);
			}
			
			for (int i = 0; i < cerInfo.getAlternativeNames().size(); i++) {
				String an = cerInfo.getAlternativeNames().get(i);
				
				String attesa = null;
				if(i==0) {
					attesa = AlternativeName1;
				}
				else if(i==1) {
					attesa = AlternativeName2;
				}
				else {
					attesa = AlternativeName3;
				}
				
				if(an==null) {
					throw new Exception("Atteso AlternativeName '"+attesa+"'; non rilevata");
				}
				if(!attesa.equals(an)) {
					throw new Exception("Atteso AlternativeName '"+attesa+"'; rilevata '"+an+"'");
				}
							
				an = cerInfo.getAlternativeNames().get(i);
				if(an==null) {
					throw new Exception("Atteso AlternativeName '"+attesa+"' (index); non rilevata");
				}
				if(!attesa.equals(an)) {
					throw new Exception("Atteso AlternativeName '"+attesa+"' (index); rilevata '"+an+"'");
				}
				
				if(!san.containsAlternativeName(attesa)) {
					throw new Exception("Atteso AlternativeName '"+attesa+"'; non rilevata (contains)");
				}
			}
			
			sizeRiscontrati = san.getAlternativeNames().size();
			sizeAttesi = 3;
			if(sizeAttesi!=sizeRiscontrati) {
				throw new Exception("Attesi "+sizeAttesi+" AlternativeNames, riscontrati: "+sizeRiscontrati);
			}
			
			for (int i = 0; i < san.getAlternativeNames().size(); i++) {
				String an = san.getAlternativeNames().get(i);
				
				String attesa = null;
				if(i==0) {
					attesa = AlternativeName1;
				}
				else if(i==1) {
					attesa = AlternativeName2;
				}
				else {
					attesa = AlternativeName3;
				}
				
				if(an==null) {
					throw new Exception("Atteso AlternativeName '"+attesa+"'; non rilevata");
				}
				if(!attesa.equals(an)) {
					throw new Exception("Atteso AlternativeName '"+attesa+"'; rilevata '"+an+"'");
				}
							
				an = san.getAlternativeName(i);
				if(an==null) {
					throw new Exception("Atteso AlternativeName '"+attesa+"' (index); non rilevata");
				}
				if(!attesa.equals(an)) {
					throw new Exception("Atteso AlternativeName '"+attesa+"' (index); rilevata '"+an+"'");
				}
				
				if(!san.containsAlternativeName(attesa)) {
					throw new Exception("Atteso AlternativeName '"+attesa+"'; non rilevata (contains)");
				}
			}
			
			sizeRiscontrati = san.getObjectAlternativeNames().size();
			sizeAttesi = 3;
			if(sizeAttesi!=sizeRiscontrati) {
				throw new Exception("Attesi "+sizeAttesi+" AlternativeNames (object), riscontrate: "+sizeRiscontrati);
			}
			
			for (int i = 0; i < san.getObjectAlternativeNames().size(); i++) {
				GeneralName caName = san.getObjectAlternativeNames().get(i);
				
				String attesa = null;
				int tagName = 2;
				if(i==0) {
					attesa = AlternativeName1;
				}
				else if(i==1) {
					attesa = AlternativeName2;
				}
				else {
					attesa = AlternativeName3;
				}
				
				if(caName==null) {
					throw new Exception("Attesa AlternativeName '"+attesa+"'; non rilevata");
				}
				
				String ca=caName.getName()!=null ? caName.getName().toString() : null;
				if(ca==null) {
					throw new Exception("Attesa AlternativeName '"+attesa+"'; non rilevata");
				}
				if(!attesa.equals(ca)) {
					throw new Exception("Atteso AlternativeName '"+attesa+"'; rilevata '"+ca+"'");
				}
				
				int tagNameCa = caName.getTagNo();
				if(tagName!=tagNameCa) {
					throw new Exception("Atteso AlternativeName tagName '"+tagName+"'; rilevata '"+tagNameCa+"'");
				}
							
				caName = san.getObjectAlternativeName(i);
				if(caName==null) {
					throw new Exception("Attesa AlternativeName '"+attesa+"'; non rilevata");
				}
				
				ca=caName.getName()!=null ? caName.getName().toString() : null;
				if(ca==null) {
					throw new Exception("Attesa AlternativeName '"+attesa+"' (index); non rilevata");
				}
				if(!attesa.equals(ca)) {
					throw new Exception("Atteso AlternativeName '"+attesa+"' (index); rilevata '"+ca+"'");
				}
				
				tagNameCa = caName.getTagNo();
				if(tagName!=tagNameCa) {
					throw new Exception("Atteso AlternativeName tagName '"+tagName+"'; rilevata '"+tagNameCa+"'");
				}
				
				if(!san.containsAlternativeName(attesa)) {
					throw new Exception("Atteso AlternativeName '"+attesa+"'; non rilevata (contains)");
				}
				if(!san.containsAlternativeName(tagName, attesa)) {
					throw new Exception("Atteso AlternativeName '"+attesa+"'; non rilevata (contains-tagName)");
				}
			}
		}
		
		// ***** EXTENSIONS *****
		
		Extensions exts = cerInfo.getExtensions();
		if(forSign || forAuth) {
			
			System.out.println("\nExtensions: "+exts.getOIDs());
			System.out.println("\nCriticalExtensions: "+exts.getCriticalOIDs());
			System.out.println("\nNonCriticalExtensions: "+exts.getNonCriticalOIDs());
			
			List<org.bouncycastle.asn1.ASN1ObjectIdentifier> lExpected = new ArrayList<>();
			lExpected.add(new org.bouncycastle.asn1.ASN1ObjectIdentifier("2.5.29.19")); // basicConstraints
			lExpected.add(new org.bouncycastle.asn1.ASN1ObjectIdentifier("2.5.29.14")); // subjectKeyIdentifier
			org.bouncycastle.asn1.ASN1ObjectIdentifier keyUsageID = new org.bouncycastle.asn1.ASN1ObjectIdentifier("2.5.29.15");
			lExpected.add(keyUsageID); // keyUsage
			lExpected.add(new org.bouncycastle.asn1.ASN1ObjectIdentifier("2.5.29.37")); // extendedKeyUsage
			lExpected.add(new org.bouncycastle.asn1.ASN1ObjectIdentifier("2.5.29.32")); // certificatePolicies
			lExpected.add(new org.bouncycastle.asn1.ASN1ObjectIdentifier("2.5.29.31")); // cRLDistributionPoints
			lExpected.add(new org.bouncycastle.asn1.ASN1ObjectIdentifier("1.3.6.1.5.5.7.1.1")); // authorityInfoAccess
			lExpected.add(new org.bouncycastle.asn1.ASN1ObjectIdentifier("2.5.29.17")); // subjectAlternativeName
			
			List<org.bouncycastle.asn1.ASN1ObjectIdentifier> lCriticalExpected = new ArrayList<>();
			lCriticalExpected.add(keyUsageID);
			
			List<org.bouncycastle.asn1.ASN1ObjectIdentifier> lNonCriticalExpected = new ArrayList<>();
			lNonCriticalExpected.addAll(lExpected);
			lNonCriticalExpected.remove(keyUsageID);
			
			List<org.bouncycastle.asn1.ASN1ObjectIdentifier> lFounded = exts.getASN1OIDs();
			if(lFounded==null || lFounded.isEmpty()) {
				throw new Exception("Attese extensions");
			}
			if(lFounded.size()!=lExpected.size()) {
				throw new Exception("Attese "+lExpected.size()+" extensions, founded "+lFounded.size());
			}
			for (org.bouncycastle.asn1.ASN1ObjectIdentifier asn1Id : lExpected) {
				
				if(!lFounded.contains(asn1Id)) {
					throw new Exception("Attesa extension asn1 id '"+asn1Id+"' non trovata");
				}
				
				if(!exts.hasExtension(asn1Id)) {
					throw new Exception("Attesa extension asn1 id '"+asn1Id+"' non trovata (hasExtensions(asn1))");
				}
				if(!exts.hasExtension(asn1Id.getId())) {
					throw new Exception("Attesa extension asn1 id '"+asn1Id+"' non trovata (hasExtensions(string))");
				}
				
				boolean isCritical = lCriticalExpected.contains(asn1Id);
				
				if(isCritical) {
					if(!exts.hasCriticalExtension(asn1Id)) {
						throw new Exception("Attesa critical extension asn1 id '"+asn1Id+"' non trovata (hasExtensions(asn1))");
					}
					if(!exts.hasCriticalExtension(asn1Id.getId())) {
						throw new Exception("Attesa critical extension asn1 id '"+asn1Id+"' non trovata (hasExtensions(string))");
					}
					if(exts.hasNonCriticalExtension(asn1Id)) {
						throw new Exception("Non attesa non-critical extension asn1 id '"+asn1Id+"' trovata (hasExtensions(asn1))");
					}
					if(exts.hasNonCriticalExtension(asn1Id.getId())) {
						throw new Exception("Non attesa non-critical extension asn1 id '"+asn1Id+"' trovata (hasExtensions(string))");
					}
				}
				else {
					if(!exts.hasNonCriticalExtension(asn1Id)) {
						throw new Exception("Attesa non-critical extension asn1 id '"+asn1Id+"' non trovata (hasExtensions(asn1))");
					}
					if(!exts.hasNonCriticalExtension(asn1Id.getId())) {
						throw new Exception("Attesa non-critical extension asn1 id '"+asn1Id+"' non trovata (hasExtensions(string))");
					}
					if(exts.hasCriticalExtension(asn1Id)) {
						throw new Exception("Non attesa critical extension asn1 id '"+asn1Id+"' trovata (hasExtensions(asn1))");
					}
					if(exts.hasCriticalExtension(asn1Id.getId())) {
						throw new Exception("Non attesa critical extension asn1 id '"+asn1Id+"' trovata (hasExtensions(string))");
					}
				}
				
				Extension ext = exts.getExtension(keyUsageID);
				if(ext==null) {
					throw new Exception("Attesa extension asn1 id '"+asn1Id+"' non trovata (getExtension(asn1))");
				}
				ext = exts.getExtension(keyUsageID.getId());
				if(ext==null) {
					throw new Exception("Attesa extension asn1 id '"+asn1Id+"' non trovata (getExtension(string))");
				}
				
			}
			
			List<String> lFoundedId = exts.getOIDs();
			if(lFoundedId==null || lFoundedId.isEmpty()) {
				throw new Exception("Attese extensions id");
			}
			if(lFoundedId.size()!=lExpected.size()) {
				throw new Exception("Attese "+lExpected.size()+" extensions, founded "+lFoundedId.size());
			}
			for (org.bouncycastle.asn1.ASN1ObjectIdentifier asn1Id : lExpected) {
				
				if(!lFoundedId.contains(asn1Id.getId())) {
					throw new Exception("Attesa extension id '"+asn1Id+"' non trovata");
				}
				
			}
			
			lFounded = exts.getCriticalASN1OIDs();
			if(lFounded==null || lFounded.isEmpty()) {
				throw new Exception("Attese critical extensions");
			}
			if(lFounded.size()!=lCriticalExpected.size()) {
				throw new Exception("Attese "+lCriticalExpected.size()+" critical extensions, founded "+lFounded.size());
			}
			for (org.bouncycastle.asn1.ASN1ObjectIdentifier asn1Id : lCriticalExpected) {
				if(!lFounded.contains(asn1Id)) {
					throw new Exception("Attesa critical extension asn1 id '"+asn1Id+"' non trovata");
				}
			}
			
			lFoundedId = exts.getCriticalOIDs();
			if(lFoundedId==null || lFoundedId.isEmpty()) {
				throw new Exception("Attese critical extensions id");
			}
			if(lFoundedId.size()!=lCriticalExpected.size()) {
				throw new Exception("Attese "+lCriticalExpected.size()+" critical extensions, founded "+lFoundedId.size());
			}
			for (org.bouncycastle.asn1.ASN1ObjectIdentifier asn1Id : lCriticalExpected) {
				
				if(!lFoundedId.contains(asn1Id.getId())) {
					throw new Exception("Attesa critical extension id '"+asn1Id+"' non trovata");
				}
				
			}
			
			lFounded = exts.getNonCriticalASN1OIDs();
			if(lFounded==null || lFounded.isEmpty()) {
				throw new Exception("Attese non-critical extensions");
			}
			if(lFounded.size()!=lNonCriticalExpected.size()) {
				throw new Exception("Attese "+lNonCriticalExpected.size()+" critical extensions, founded "+lFounded.size());
			}
			for (org.bouncycastle.asn1.ASN1ObjectIdentifier asn1Id : lNonCriticalExpected) {
				if(!lFounded.contains(asn1Id)) {
					throw new Exception("Attesa non-critical extension asn1 id '"+asn1Id+"' non trovata");
				}
			}
			
			lFoundedId = exts.getNonCriticalOIDs();
			if(lFoundedId==null || lFoundedId.isEmpty()) {
				throw new Exception("Attese non-critical extensions id");
			}
			if(lFoundedId.size()!=lNonCriticalExpected.size()) {
				throw new Exception("Attese "+lNonCriticalExpected.size()+" critical extensions, founded "+lFoundedId.size());
			}
			for (org.bouncycastle.asn1.ASN1ObjectIdentifier asn1Id : lNonCriticalExpected) {
				
				if(!lFoundedId.contains(asn1Id.getId())) {
					throw new Exception("Attesa non-critical extension id '"+asn1Id+"' non trovata");
				}
				
			}
		}
		
		System.out.println("");
	}
}
