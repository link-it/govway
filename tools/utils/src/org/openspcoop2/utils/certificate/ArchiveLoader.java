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

package org.openspcoop2.utils.certificate;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
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

	public static Certificate load(byte[] content) throws UtilsException {
		return _load(ArchiveType.CER, content, -1, null, null);
	}
	
	public static Certificate loadFromKeystorePKCS12(byte[] content, int position, String password) throws UtilsException {
		return _load(ArchiveType.PKCS12, content, position, null, password);
	}
	public static Certificate loadFromKeystorePKCS12(byte[] content, String alias, String password) throws UtilsException {
		return _load(ArchiveType.PKCS12, content, -1, alias, password);
	}
	
	public static Certificate loadFromKeystoreJKS(byte[] content, int position, String password) throws UtilsException {
		return _load(ArchiveType.JKS, content, position, null, password);
	}
	public static Certificate loadFromKeystoreJKS(byte[] content, String alias, String password) throws UtilsException {
		return _load(ArchiveType.JKS, content, -1, alias, password);
	}
	
	public static Certificate load(ArchiveType type, byte[] content, int position, String password) throws UtilsException {
		return _load(type, content, position, null, password);
	}
	public static Certificate load(ArchiveType type, byte[] content, String alias, String password) throws UtilsException {
		return _load(type, content, -1, alias, password);
	}
	private static Certificate _load(ArchiveType type, byte[] content, int position, String alias, String password) throws UtilsException {
		
		try {
		
			switch (type) {
			case JKS:
			case PKCS12:
			
				KeyStore ks = KeyStore.getInstance(type.name());
				char[] pwd = null;
				if(password!=null) {
					pwd = password.toCharArray();
				}
				ks.load(new ByteArrayInputStream(content), pwd);
				
				Enumeration<String> en = ks.aliases();
				int index = -1;
				while (en.hasMoreElements()) {
					index ++;
					String aliasCheck = (String) en.nextElement();
					java.security.cert.Certificate baseCert = ks.getCertificate(aliasCheck);
					if(!(baseCert instanceof X509Certificate)) {
						if(aliasCheck.equalsIgnoreCase(alias)) {
							throw new Exception("Certificate ["+alias+"] isn't X509");
						}else {
							continue;
						}
					}
					X509Certificate cert = (X509Certificate) baseCert;
					java.security.cert.Certificate[] baseCertChain = ks.getCertificateChain(aliasCheck);
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
					
					
					if(aliasCheck.equalsIgnoreCase(alias)) {
						return new Certificate(aliasCheck, cert, certChain);
					}
					else if(position>=0) {
						if(index==position) {
							return new Certificate(aliasCheck, cert, certChain);
						}
					}
				}
				
				if(alias!=null) {
					throw new Exception("Certificate ["+alias+"] not found");
				}
				else {
					throw new Exception("Certificate at position ["+position+"] not found");
				}
	
			case CER:
				
				CertificateFactory fact = org.openspcoop2.utils.certificate.CertificateFactory.getCertificateFactory();
				X509Certificate cer = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(content));
				if(alias==null) {
					alias = "cert";
				}
				return new Certificate(alias, cer);
				
			default:
				break;
			}
			
			throw new Exception("Certificate not found in archive (type: "+type+")");
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	} 	
	
	
	public static List<String> readAliasesInKeystorePKCS12(byte[] content, String password) throws UtilsException {
		return _readAliases(ArchiveType.PKCS12, content, password);
	}
	public static List<String> readAliasesInKeystoreJKS(byte[] content, String password) throws UtilsException {
		return _readAliases(ArchiveType.JKS, content, password);
	}
	public static List<String> readAliases(ArchiveType type, byte[] content, String password) throws UtilsException {
		return _readAliases(type, content, password);
	}
	private static List<String> _readAliases(ArchiveType type, byte[] content, String password) throws UtilsException {
		
		try {
		
			switch (type) {
			case JKS:
			case PKCS12:
			
				KeyStore ks = KeyStore.getInstance(type.name());
				char[] pwd = null;
				if(password!=null) {
					pwd = password.toCharArray();
				}
				ks.load(new ByteArrayInputStream(content), pwd);
				
				Enumeration<String> en = ks.aliases();
				List<String> list = new ArrayList<>();
				while (en.hasMoreElements()) {
					String alias = (String) en.nextElement();
					list.add(alias);
				}
				return list;
	
			case CER:
				
				throw new UtilsException("Type '"+type+"' hasn't alias");
				
			default:
				break;
			}
			
			throw new Exception("Certificate not found in archive (type: "+type+")");
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	} 	
}
