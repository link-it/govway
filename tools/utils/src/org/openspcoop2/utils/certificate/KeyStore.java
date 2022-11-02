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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.cxf.common.util.Base64UrlUtility;
import org.apache.cxf.rt.security.crypto.MessageDigestUtils;
import org.openspcoop2.utils.UtilsException;

/**	
 * Keystore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeyStore {

	private java.security.KeyStore keystore;
	private boolean keystoreHsm;
	
	public KeyStore(String keystorePath,String passwordKeystore) throws UtilsException{
		this(keystorePath,"JKS",passwordKeystore);
	}
	public KeyStore(String keystorePath,String tipoKeystore, String passwordKeystore) throws UtilsException{
		this(new File(keystorePath),tipoKeystore,passwordKeystore);
	}
	public KeyStore(File keystorePath,String passwordKeystore) throws UtilsException{
		this(keystorePath,"JKS",passwordKeystore);
	}
	public KeyStore(File keystorePath,String tipoKeystore, String passwordKeystore) throws UtilsException{
		
		if(keystorePath.exists()==false){
			throw new UtilsException("Keystore ["+keystorePath+"] not exists");
		}
		if(keystorePath.canRead()==false){
			throw new UtilsException("Keystore ["+keystorePath+"] cannot read");
		}
		
		InputStream fin = null;
		try{
			fin = new FileInputStream(keystorePath);
			_init(fin, tipoKeystore, passwordKeystore);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		finally{
			try{
				if(fin!=null){
					fin.close();
				}
			}catch(Exception eClose){
				// close
			}
		}
		
	}
	
	public KeyStore(byte[] keystore,String passwordKeystore) throws UtilsException{
		this(keystore,"JKS",passwordKeystore);
	}
	public KeyStore(byte[] keystore,String tipoKeystore, String passwordKeystore) throws UtilsException{
		
		if(keystore==null){
			throw new UtilsException("Keystore undefined");
		}
		
		InputStream fin = null;
		try{
			fin = new ByteArrayInputStream(keystore);
			_init(fin, tipoKeystore, passwordKeystore);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		finally{
			try{
				if(fin!=null){
					fin.close();
				}
			}catch(Exception eClose){
				// close
			}
		}
		
	}
	
	private void _init(InputStream is, String tipoKeystore, String passwordKeystore) throws UtilsException{
		try{
			java.security.KeyStore keystore = java.security.KeyStore.getInstance(tipoKeystore);
			keystore.load(is, passwordKeystore.toCharArray());
			this.keystore = keystore;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public KeyStore(java.security.KeyStore keystore) {
		this(keystore, false);
	}
	public KeyStore(java.security.KeyStore keystore, boolean keystoreHsm) {
		this.keystore = keystore;
		this.keystoreHsm = keystoreHsm;
	}
	
	private Map<String, Key> keys = new HashMap<String, Key>(); // effettuo il cache delle chiavi essendo costoso accederci tutte le volte
	private synchronized void initKey(String alias, String password) throws UtilsException {
		if(!this.keys.containsKey(alias)) {
			try{
				//System.out.println("******** AGGIUNGO CHIAVE '"+alias+"' IN CACHE!!!!!!!!");
				Key key = this.keystore.getKey(alias, password.toCharArray());
				if(key==null) {
					throw new Exception("Not found");
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
//			else {
//				System.out.println("GET KEY '"+alias+"' FROM CACHE");
//			}
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
			Enumeration<String> aliases = this.keystore.aliases();
			Certificate cer = null;
			while (aliases.hasMoreElements()) {
				String alias = (String) aliases.nextElement();
				if(cer!=null){
					throw new Exception("More than one certificate, use alias");
				}
				cer = this.keystore.getCertificate(alias);
			}
			return cer;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	public Certificate getCertificate(String alias) throws UtilsException{
		try{
			return this.keystore.getCertificate(alias);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	public Certificate[] getCertificateChain(String alias) throws UtilsException{
		try{
			return this.keystore.getCertificateChain(alias);
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
			Enumeration<String> aliases = this.keystore.aliases();
			while (aliases.hasMoreElements()) {
				String alias = (String) aliases.nextElement();
				Certificate cer = this.keystore.getCertificate(alias);
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
				throw new Exception("Certificate '"+alias+"' not exists");
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
			return this.keystore.containsAlias(alias);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}	
	}
	public Enumeration<String> aliases() throws UtilsException{
		try{
			return this.keystore.aliases();
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
		return this.keystore;
	}
	public boolean isKeystoreHsm() {
		return this.keystoreHsm;
	}
	public String getKeystoreType() {
		if(this.keystore==null) {
			return null;
		}
		return this.keystore.getType();
	}
	public java.security.Provider getKeystoreProvider() {
		return this.keystore.getProvider();
	}
}
