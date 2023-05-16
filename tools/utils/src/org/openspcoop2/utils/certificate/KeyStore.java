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

import java.io.File;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import org.apache.cxf.common.util.Base64UrlUtility;
import org.apache.cxf.rt.security.crypto.MessageDigestUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**	
 * Keystore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeyStore {
	
	private java.security.KeyStore keystoreArchive;
	private boolean keystoreHsm;
	
	public KeyStore(String keystorePath,String passwordKeystore) throws UtilsException{
		this(keystorePath,KeystoreType.JKS.getNome(),passwordKeystore);
	}
	public KeyStore(String keystorePath,String tipoKeystore, String passwordKeystore) throws UtilsException{
		this(new File(keystorePath),tipoKeystore,passwordKeystore);
	}
	public KeyStore(File keystorePath,String passwordKeystore) throws UtilsException{
		this(keystorePath,KeystoreType.JKS.getNome(),passwordKeystore);
	}
	public KeyStore(File keystorePath,String tipoKeystore, String passwordKeystore) throws UtilsException{
		
		if(!keystorePath.exists()){
			throw new UtilsException("Keystore ["+keystorePath+"] not exists");
		}
		if(!keystorePath.canRead()){
			throw new UtilsException("Keystore ["+keystorePath+"] cannot read");
		}
		
		byte [] keystore = null;
		try {
			keystore = FileSystemUtilities.readBytesFromFile(keystorePath);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		
		this.keystoreArchive = KeystoreUtils.readKeystore(keystore, tipoKeystore, passwordKeystore);
	}
	
	public KeyStore(byte[] keystore,String passwordKeystore) throws UtilsException{
		this(keystore,KeystoreType.JKS.getNome(),passwordKeystore);
	}
	public KeyStore(byte[] keystore,String tipoKeystore, String passwordKeystore) throws UtilsException{
		
		if(keystore==null){
			throw new UtilsException("Keystore undefined");
		}
		
		this.keystoreArchive = KeystoreUtils.readKeystore(keystore, tipoKeystore, passwordKeystore);
		
	}
	
	
	public KeyStore(java.security.KeyStore keystore) {
		this(keystore, false);
	}
	public KeyStore(java.security.KeyStore keystore, boolean keystoreHsm) {
		this.keystoreArchive = keystore;
		this.keystoreHsm = keystoreHsm;
	}
	
	private Map<String, Key> keys = new HashMap<>(); // effettuo il cache delle chiavi essendo costoso accederci tutte le volte
	private synchronized void initKey(String alias, String password) throws UtilsException {
		if(!this.keys.containsKey(alias)) {
			try{
				/** System.out.println("******** AGGIUNGO CHIAVE '"+alias+"' IN CACHE!!!!!!!!"); */
				Key key = null;
				if(password!=null) {
					key = this.keystoreArchive.getKey(alias, password.toCharArray());
				}
				else {
					key = this.keystoreArchive.getKey(alias, null);
				}
				if(key==null) {
					throw new UtilsException("Not found");
				}
				this.keys.put(alias, key);
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	
	public PrivateKey getPrivateKey(String alias,String passwordPrivateKey) throws UtilsException{
		try{
			if(!this.keys.containsKey(alias)) {
				initKey(alias, passwordPrivateKey);
			}
/**			else {
//				System.out.println("GET KEY '"+alias+"' FROM CACHE");
//			} */
			return (PrivateKey) this.keys.get(alias);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	public SecretKey getSecretKey(String alias,String passwordPrivateKey) throws UtilsException{
		try{
			if(!this.keys.containsKey(alias)) {
				initKey(alias, passwordPrivateKey);
			}
			return (SecretKey) this.keys.get(alias);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}

	public Certificate getCertificate() throws UtilsException{
		try{
			Enumeration<String> aliases = this.keystoreArchive.aliases();
			Certificate cer = null;
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				if(cer!=null){
					throw new UtilsException("More than one certificate, use alias");
				}
				cer = this.keystoreArchive.getCertificate(alias);
			}
			return cer;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	public Certificate getCertificate(String alias) throws UtilsException{
		try{
			return this.keystoreArchive.getCertificate(alias);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	public Certificate[] getCertificateChain(String alias) throws UtilsException{
		try{
			return this.keystoreArchive.getCertificateChain(alias);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	
	public Certificate getCertificateByDigestMD5UrlEncoded(String digest) throws UtilsException{
		return getCertificateByDigestUrlEncoded(digest, MessageDigestUtils.ALGO_MD5);
	}
	public Certificate getCertificateByDigestSHA1UrlEncoded(String digest) throws UtilsException{
		return getCertificateByDigestUrlEncoded(digest, MessageDigestUtils.ALGO_SHA_1);
	}
	public Certificate getCertificateByDigestSHA256UrlEncoded(String digest) throws UtilsException{
		return getCertificateByDigestUrlEncoded(digest, MessageDigestUtils.ALGO_SHA_256);
	}
	public Certificate getCertificateByDigestUrlEncoded(String digest, String digestAlgo) throws UtilsException{
		try{
			Enumeration<String> aliases = this.keystoreArchive.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				Certificate cer = this.keystoreArchive.getCertificate(alias);
				String digestCer = this.buildDigestUrlEncoded(cer, digestAlgo);
				if(digestCer.equals(digest)) {
					return cer;
				}
			}
			return null;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	
	public Certificate getCertificateBySubject(X500Principal principal) throws UtilsException{
		try{
			if(principal==null) {
				return null;
			}
			Enumeration<String> aliases = this.keystoreArchive.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				Certificate cer = this.keystoreArchive.getCertificate(alias);
				if(cer instanceof X509Certificate) {
					X509Certificate x509 = (X509Certificate) cer;
					X500Principal subject = x509.getSubjectX500Principal();
					if(principal.equals(subject)) {
						return cer;
					}
				}
			}
			return null;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	public boolean existsCertificateBySubject(X500Principal principal) throws UtilsException{
		try{
			return this.getCertificateBySubject(principal)!=null;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	
	public Certificate getCertificateByPublicKey(PublicKey publicKey) throws UtilsException{
		try{
			if(publicKey==null) {
				return null;
			}
			Enumeration<String> aliases = this.keystoreArchive.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				Certificate cer = this.keystoreArchive.getCertificate(alias);
				PublicKey pk = cer.getPublicKey();
				if(pk!=null && pk.equals(publicKey)) {
					return cer;
				}
			}
			return null;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	public boolean existsCertificateByPublicKey(PublicKey publicKey) throws UtilsException{
		try{
			return this.getCertificateByPublicKey(publicKey)!=null;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	
	public String getDigestMD5UrlEncoded(String alias) throws UtilsException{
		return this.getDigestUrlEncoded(alias, MessageDigestUtils.ALGO_MD5);
	}
	public String getDigestSHA1UrlEncoded(String alias) throws UtilsException{
		return this.getDigestUrlEncoded(alias, MessageDigestUtils.ALGO_SHA_1);
	}
	public String getDigestSHA256UrlEncoded(String alias) throws UtilsException{
		return this.getDigestUrlEncoded(alias, MessageDigestUtils.ALGO_SHA_256);
	}
	public String getDigestUrlEncoded(String alias, String digestAlgo) throws UtilsException{
		try{
			Certificate cer = getCertificate(alias);
			if(cer==null) {
				throw new UtilsException("Certificate '"+alias+"' not exists");
			}
			return this.buildDigestUrlEncoded(cer, digestAlgo);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	private String buildDigestUrlEncoded(Certificate cer, String digestAlgo) throws UtilsException{
		try{
			byte[] digestB = MessageDigestUtils.createDigest(cer.getEncoded(), digestAlgo);
			return Base64UrlUtility.encode(digestB);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public boolean existsAlias(String alias) throws UtilsException{
		try{
			return this.keystoreArchive.containsAlias(alias);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	public Enumeration<String> aliases() throws UtilsException{
		try{
			return this.keystoreArchive.aliases();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public PublicKey getPublicKey() throws UtilsException{
		return this.getCertificate().getPublicKey();
	}
	public PublicKey getPublicKey(String alias) throws UtilsException{
		return this.getCertificate(alias).getPublicKey();
	}
	
	public java.security.KeyStore getKeystore() {
		return this.keystoreArchive;
	}
	public boolean isKeystoreHsm() {
		return this.keystoreHsm;
	}
	public String getKeystoreType() {
		if(this.keystoreArchive==null) {
			return null;
		}
		return this.keystoreArchive.getType();
	}
	public java.security.Provider getKeystoreProvider() {
		return this.keystoreArchive.getProvider();
	}
	
	public void putCertificate(String alias, Certificate cert, boolean overwriteIfExists) throws UtilsException {
		if(this.existsAlias(alias)) {
			if(overwriteIfExists) {
				try {
					this.keystoreArchive.deleteEntry(alias);
				}catch(Exception t) {
					throw new UtilsException(t.getMessage(),t);
				}
			}
			else {
				return;
			}
		}
		try {
			this.keystoreArchive.setCertificateEntry(alias, cert);
		}catch(Exception t) {
			throw new UtilsException(t.getMessage(),t);
		}
	}
	public void putAllCertificate(KeyStore keystore, boolean overwriteIfExists) throws UtilsException {
		if(keystore!=null) {
			Enumeration<String> aliases = keystore.aliases();
			if(aliases!=null) {
				while (aliases.hasMoreElements()) {
					String alias = aliases.nextElement();
					Certificate cert = keystore.getCertificate(alias);
					putCertificate(alias, cert, overwriteIfExists);
				}
			}
		}
	}
	
}
