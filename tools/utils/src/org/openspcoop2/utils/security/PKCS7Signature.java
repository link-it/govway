/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
		this.keystore = keystore;
		this.privateKey = this.keystore.getPrivateKey(alias, passwordPrivateKey);
		this.certificate = this.keystore.getCertificate(alias);
		
		this.provider = keystore.getKeystoreProvider();
	
		// Prendo il provider dal keystore per PKCS11
		// Per gli altri keystore, visto che la SUN non supporta l'algoritmo di firma è possibile usare bouncy castle nell'altro costruttore
		//	this.provider = new BouncyCastleProvider();
		//	Security.addProvider(this.provider);
	
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
			ContentSigner sha1Signer = new JcaContentSignerBuilder(algorithm).setProvider(this.provider)
					.build(this.privateKey);

			gen.addSignerInfoGenerator(
					new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider(this.provider).build())
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
