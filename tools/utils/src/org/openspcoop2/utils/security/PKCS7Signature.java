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


package org.openspcoop2.utils.security;

import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.dsig.SignatureMethod;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;

/**	
 * Signature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PKCS7Signature {

	public final static String DEFAULT_SIGNATURE_METHOD = SignatureMethod.RSA_SHA256;
	
	private KeyStore keystore;
	private PrivateKey privateKey;
	private Certificate certificate;
	private Provider provider;
	
	public PKCS7Signature(KeyStore keystore, String alias, String passwordPrivateKey) throws UtilsException{
		boolean useKeystoreProvider = false;
		if(keystore!=null && "pkcs11".equalsIgnoreCase(keystore.getKeystoreType())) {
			useKeystoreProvider = true;
			// Prendo il provider dal keystore per PKCS11, il cui provider implementa l'algoritmo di firma specifica per la chiave memorizzata nel dispositivo.
		}
		init(keystore, alias, passwordPrivateKey, useKeystoreProvider);
	}
	public PKCS7Signature(KeyStore keystore, String alias, String passwordPrivateKey, boolean useKeystoreProvider) throws UtilsException{
		init(keystore, alias, passwordPrivateKey, useKeystoreProvider);	
	}
	public PKCS7Signature(KeyStore keystore, String alias, String passwordPrivateKey, boolean useBouncyCastle, boolean addBouncyCastleProvider) throws UtilsException{
		this(keystore, alias, passwordPrivateKey);
		if(useBouncyCastle) {
			if(addBouncyCastleProvider) {
				this.provider = Security.getProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
				if(this.provider==null) {
					Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
					this.provider = Security.getProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
				}
			}
			else {
				this.provider = Security.getProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
			}
		}
	}
	private void init(KeyStore keystore, String alias, String passwordPrivateKey, boolean useKeystoreProvider) throws UtilsException {
		if(keystore==null) {
			throw new UtilsException("Keystore undefined");
		}
		this.keystore = keystore;
		this.privateKey = this.keystore.getPrivateKey(alias, passwordPrivateKey);
		this.certificate = this.keystore.getCertificate(alias);
		
		if(useKeystoreProvider) {
			this.provider = keystore.getKeystoreProvider();
		
			// Prendere il provider dal keystore serve per PKCS11 il cui provider implementa l'algoritmo di firma specifica per la chiave memorizzata nel dispositivo.
			// Per gli altri tipi di keystore, nei quali tipicamente il provider è la SUN, non deve essere usata questa opzione altrimenti si ottiene l'errore:
			//    no such algorithm: SHA256WITHRSA for provider SUN
			// Per quei tipi di keystore devono essere usati i costruttori in cui viene usato bouncy castle 
			// o il costruttore senza parametri relativi al provider, il quale utilizza quello il provider più corretto preente nella JVM.
		}
	}
	
	public byte[] sign(String data, String charsetName, String algorithm) throws UtilsException{
		try{
			return this.sign(data.getBytes(charsetName), algorithm);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public byte[] sign(byte[] data, String algorithm) throws UtilsException{
		try{
			if(algorithm==null) {
				algorithm = DEFAULT_SIGNATURE_METHOD;
			}
			
			List<X509CertificateHolder> certList = new ArrayList<>();
			CMSTypedData msg = new CMSProcessableByteArray(data); // Data to sign

			X509CertificateHolder cert = new X509CertificateHolder(this.certificate.getEncoded());
			certList.add(cert); // Adding the X509 Certificate

			Store<?> certs = new JcaCertStore(certList);

			CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
			// Initializing the the BC's Signer
			JcaContentSignerBuilder cs = new JcaContentSignerBuilder(algorithm);
			if(this.provider!=null) {
				cs.setProvider(this.provider);
			}
			ContentSigner sha1Signer = cs.build(this.privateKey);

			JcaDigestCalculatorProviderBuilder builder = new JcaDigestCalculatorProviderBuilder();
			if(this.provider!=null) {
				builder.setProvider(this.provider);
			}
			gen.addSignerInfoGenerator(
					new JcaSignerInfoGeneratorBuilder(builder.build())
							.build(sha1Signer, cert));
			// adding the certificate
			gen.addCertificates(certs);
			// Getting the signed data
			CMSSignedData sigData = gen.generate(msg, true);

			return sigData.getEncoded();
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
