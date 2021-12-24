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


package org.openspcoop2.utils.security;

import java.security.Security;
import java.security.cert.Certificate;
import java.util.Collection;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;

/**	
 * VerifySignature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VerifyPKCS7Signature {

	private KeyStore keystore;
	private Certificate certificate;
	private BouncyCastleProvider bouncyCastleProvider;
	private byte[] originalContent;
	
	public VerifyPKCS7Signature(KeyStore keystore) throws UtilsException{
		this.keystore = keystore;
		this.certificate = this.keystore.getCertificate();
	}
	public VerifyPKCS7Signature(KeyStore keystore, String alias) throws UtilsException{
		this.keystore = keystore;
		this.certificate = this.keystore.getCertificate(alias);
		
		this.bouncyCastleProvider = new BouncyCastleProvider();
		Security.addProvider(this.bouncyCastleProvider);
	}
	
	public boolean verify(byte[] signatureData, String algorithm) throws UtilsException{
		try{
			CMSSignedData cmsSignedData = new CMSSignedData(signatureData);
			
			try{
				Collection<SignerInformation> signers = cmsSignedData.getSignerInfos().getSigners();
		        X509CertificateHolder ch = new X509CertificateHolder(this.certificate.getEncoded());
		        for (SignerInformation si : signers)
		            if (si.getSID().match(ch))
		                if (si.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(this.bouncyCastleProvider).build(ch)))
		                    return true;
				
				return false;
			}finally {
				 //Retrieve the json content from pkcs7
		        CMSProcessable signedContent = cmsSignedData.getSignedContent();
		        this.originalContent = (byte[]) signedContent.getContent();
			}
		
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	public byte[] getOriginalContent() {
		return this.originalContent;
	}
}
