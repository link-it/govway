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

package org.openspcoop2.utils.certificate;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.openspcoop2.utils.UtilsException;

/**
 * Certificate
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveLoader {
	
	private ArchiveLoader() {}

	public static Certificate load(byte[] content) throws UtilsException {
		return loadEngine(ArchiveType.CER, content, -1, null, null, false);
	}
	public static Certificate loadChain(byte[] content) throws UtilsException {
		return loadEngine(ArchiveType.CER, content, -1, null, null, true);
	}
	public static Certificate load(byte[] content, boolean chain) throws UtilsException {
		return loadEngine(ArchiveType.CER, content, -1, null, null, chain);
	}
	
	public static Certificate loadFromKeystorePKCS12(byte[] content, int position, String password) throws UtilsException {
		return loadEngine(ArchiveType.PKCS12, content, position, null, password, false);
	}
	public static Certificate loadFromKeystorePKCS12(byte[] content, String alias, String password) throws UtilsException {
		return loadEngine(ArchiveType.PKCS12, content, -1, alias, password, false);
	}
	
	public static Certificate loadFromKeystoreJKS(byte[] content, int position, String password) throws UtilsException {
		return loadEngine(ArchiveType.JKS, content, position, null, password, false);
	}
	public static Certificate loadFromKeystoreJKS(byte[] content, String alias, String password) throws UtilsException {
		return loadEngine(ArchiveType.JKS, content, -1, alias, password, false);
	}
	
	public static Certificate load(ArchiveType type, byte[] content, int position, String password) throws UtilsException {
		return loadEngine(type, content, position, null, password, false);
	}
	public static Certificate load(ArchiveType type, byte[] content, String alias, String password) throws UtilsException {
		return loadEngine(type, content, -1, alias, password, false);
	}
	private static Certificate loadEngine(ArchiveType type, byte[] content, int position, String alias, String password, boolean chain) throws UtilsException {
		
		try {
		
			switch (type) {
			case JKS:
			case PKCS12:
				return buildCertificateFromKeyStore(type, content, position, alias, password);
			case CER:
				return buildCertificateFromCER(content, alias, chain);
			default:
				break;
			}
			
			throw new UtilsException("Certificate not found in archive (type: "+type+")");
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static Certificate buildCertificateFromKeyStore(ArchiveType type, byte[] content, int position, String alias, String password) throws UtilsException {
		try {
			KeyStore ks = KeystoreUtils.readKeystore(content, type.name(), password);
			
			Enumeration<String> en = ks.aliases();
			int index = -1;
			while (en.hasMoreElements()) {
				index ++;
				String aliasCheck = en.nextElement();
				java.security.cert.Certificate baseCert = ks.getCertificate(aliasCheck);
				if(!(baseCert instanceof X509Certificate)) {
					if(aliasCheck.equalsIgnoreCase(alias)) {
						throw new UtilsException("Certificate ["+alias+"] isn't X509");
					}else {
						continue;
					}
				}
				X509Certificate cert = (X509Certificate) baseCert;
				java.security.cert.Certificate[] baseCertChain = ks.getCertificateChain(aliasCheck);
				List<java.security.cert.X509Certificate> certChain = readCertificateChain(baseCertChain);
					
				if( 
					(aliasCheck.equalsIgnoreCase(alias))
					||
					(position>=0 &&	index==position)
					) {
					return new Certificate(aliasCheck, cert, certChain);
				}
			}
			
			if(alias!=null) {
				throw new UtilsException("Certificate ["+alias+"] not found");
			}
			else {
				throw new UtilsException("Certificate at position ["+position+"] not found");
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static List<java.security.cert.X509Certificate> readCertificateChain(java.security.cert.Certificate[] baseCertChain) throws UtilsException {
		try {
			List<java.security.cert.X509Certificate> certChain = null;
			if(baseCertChain!=null && baseCertChain.length>0) {
				for (int i = 0; i < baseCertChain.length; i++) {
					java.security.cert.Certificate check = baseCertChain[i];
					if(check instanceof X509Certificate) {
						if(certChain==null) {
							certChain = new ArrayList<>();
						}
						certChain.add((X509Certificate) check);
					}
				}
			}
			return certChain;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static Certificate buildCertificateFromCER( byte[] content, String alias, boolean chain) throws UtilsException {
		try {
			if(alias==null) {
				alias = "cert";
			}
			
			CertificateFactory fact = org.openspcoop2.utils.certificate.CertificateFactory.getCertificateFactory();
			if(chain) {
				try(ByteArrayInputStream bin = new ByteArrayInputStream(content)){
					Collection<? extends java.security.cert.Certificate> certs = fact.generateCertificates(bin);
					return loadCertificateChain(certs, alias);
				}
			}
			else {
				// provo prima a caricarlo come chain
				// I formati pkcs7 devono ad esempio essere caricati tramite la primitiva generateCertificates
				Collection<? extends java.security.cert.Certificate> certs = buildCollectionCertificate(fact, content);
				
				if(certs==null || certs.isEmpty()) {
					try(ByteArrayInputStream bin = new ByteArrayInputStream(content)){
						X509Certificate cer = (X509Certificate) fact.generateCertificate(bin);
						return new Certificate(alias, cer);
					}
				}
				else {
					return loadCertificateChain(certs, alias);
				}
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static Collection<? extends java.security.cert.Certificate> buildCollectionCertificate(CertificateFactory fact, byte[]content){
		Collection<? extends java.security.cert.Certificate> certs = null;
		try(ByteArrayInputStream bin = new ByteArrayInputStream(content)){
			certs = fact.generateCertificates(bin);
		}catch(Exception t) {
			// ignore
		}
		return certs;
	}
	
	private static Certificate loadCertificateChain(Collection<? extends java.security.cert.Certificate> certs, String alias) throws UtilsException {
		if(certs==null || certs.isEmpty()) {
			throw new UtilsException("Certificates not found");
		}
		int cList = 0;
		X509Certificate cer = null;
		List<X509Certificate> listChain = null;
		for (java.security.cert.Certificate c : certs) {
			if(c instanceof X509Certificate) {
				X509Certificate tmp = (X509Certificate) c;
				if(cList==0) {
					cer = tmp;
				}
				else {
					if(listChain==null) {
						listChain = new ArrayList<>();
					}
					listChain.add(tmp);
				}
				cList++;
			}
		}
		if(listChain!=null) {
			return new Certificate(alias, cer, listChain);
		}
		else {
			return new Certificate(alias, cer);
		}
	}
	
	public static List<String> readAliasesInKeystorePKCS12(byte[] content, String password) throws UtilsException {
		return readAliasesEngine(ArchiveType.PKCS12, content, password);
	}
	public static List<String> readAliasesInKeystoreJKS(byte[] content, String password) throws UtilsException {
		return readAliasesEngine(ArchiveType.JKS, content, password);
	}
	public static List<String> readAliases(ArchiveType type, byte[] content, String password) throws UtilsException {
		return readAliasesEngine(type, content, password);
	}
	private static List<String> readAliasesEngine(ArchiveType type, byte[] content, String password) throws UtilsException {
		
		try {
		
			switch (type) {
			case JKS:
			case PKCS12:
			
				KeyStore ks = KeystoreUtils.readKeystore(content, type.name(), password);
				
				Enumeration<String> en = ks.aliases();
				List<String> list = new ArrayList<>();
				while (en.hasMoreElements()) {
					String alias = en.nextElement();
					list.add(alias);
				}
				return list;
	
			case CER:
				
				throw new UtilsException("Type '"+type+"' hasn't alias");
				
			default:
				break;
			}
			
			throw new UtilsException("Certificate not found in archive (type: "+type+")");
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	} 	
}
