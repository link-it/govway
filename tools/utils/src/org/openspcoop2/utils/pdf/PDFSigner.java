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
package org.openspcoop2.utils.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.date.DateManager;

/**
* PDFSigner
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class PDFSigner extends AbstractPDFCore {

	private PDSignature signature;
	
	public PDFSigner(PDDocument doc, PDSignature signature) throws UtilsException {
		super(doc);
		this.signature = signature;
	}
	public PDFSigner(byte[] content, PDSignature signature) throws UtilsException {
		super(content, false);
		this.signature = signature;
	}
	public PDFSigner(File doc, PDSignature signature) throws UtilsException {
		super(doc, false);
		this.signature = signature;
	}
	public PDFSigner(InputStream is, PDSignature signature) throws UtilsException {
		super(is, false);
		this.signature = signature;
	}
	
	public PDFSigner(PDDocument doc, 
			String name,COSName filter,COSName subfilter, String location, String reason, String contactInfo) throws UtilsException {
		super(doc);
		init(name, filter, subfilter, location, reason, contactInfo);
	}
	public PDFSigner(byte[] content, 
			String name,COSName filter,COSName subfilter, String location, String reason, String contactInfo) throws UtilsException {
		super(content, false);
		init(name, filter, subfilter, location, reason, contactInfo);
	}
	public PDFSigner(File doc, 
			String name,COSName filter,COSName subfilter, String location, String reason, String contactInfo) throws UtilsException {
		super(doc, false);
		init(name, filter, subfilter, location, reason, contactInfo);
	}
	public PDFSigner(InputStream is, 
			String name,COSName filter,COSName subfilter, String location, String reason, String contactInfo) throws UtilsException {
		super(is, false);
		init(name, filter, subfilter, location, reason, contactInfo);
	}
	
	private void init(String name,COSName filter,COSName subfilter, String location, String reason, String contactInfo) throws UtilsException {
		try {
			if(name==null) {
				throw new UtilsException("Name undefined");
			}
			
			this.signature = new PDSignature();
			this.signature.setName(name);
			this.signature.setFilter(filter);
			this.signature.setSubFilter(subfilter);
			this.signature.setLocation(location);
			this.signature.setReason(reason);
			this.signature.setContactInfo(contactInfo);
			this.signature.setSignDate(DateManager.getCalendar());
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	public void sign(KeyStore keystore, String privateKeyAlias, String privateKeyPassword, File fileOutput) throws UtilsException {
		sign(keystore, privateKeyAlias, privateKeyPassword, null, fileOutput);
	}
	public void sign(KeyStore keystore, String privateKeyAlias, String privateKeyPassword, String algorithm, File fileOutput) throws UtilsException {
		if(fileOutput==null) {
			throw new UtilsException("Output file undefined");
		}
		try(FileOutputStream fout = new FileOutputStream(fileOutput)){
			sign(keystore, privateKeyAlias, privateKeyPassword, algorithm, fout);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public byte[] sign(KeyStore keystore, String privateKeyAlias, String privateKeyPassword) throws UtilsException {
		String algorithm = null;
		return sign(keystore, privateKeyAlias, privateKeyPassword, algorithm);
	}
	public byte[] sign(KeyStore keystore, String privateKeyAlias, String privateKeyPassword, String algorithm) throws UtilsException {
		try(ByteArrayOutputStream bout = new ByteArrayOutputStream()){
			sign(keystore, privateKeyAlias, privateKeyPassword, algorithm, bout);
			bout.flush();
			return bout.toByteArray();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public void sign(KeyStore keystore, String privateKeyAlias, String privateKeyPassword, OutputStream output) throws UtilsException {
		sign(keystore, privateKeyAlias, privateKeyPassword, null, output);
	}
	public void sign(KeyStore keystore, String privateKeyAlias, String privateKeyPassword, String algorithm, OutputStream output) throws UtilsException {
		try {
			this.document.addSignature(this.signature);
		    
			ExternalSigningSupport externalSigning = this.document.saveIncrementalForExternalSigning(output);
			byte[] cmsSignature = sign(keystore, privateKeyAlias, privateKeyPassword, algorithm, externalSigning.getContent());
			externalSigning.setSignature(cmsSignature);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private byte[] sign(KeyStore keystore, String privateKeyAlias, String privateKeyPassword, String algorithm, InputStream content) throws UtilsException
    {
		if(keystore==null) {
			throw new UtilsException("KeyStore undefined");
		}
		if(privateKeyAlias==null) {
			throw new UtilsException("privateKeyAlias undefined");
		}
		if(privateKeyPassword==null) {
			throw new UtilsException("privateKeyPassword undefined");
		}
		
         try{

        	Certificate[] certificateChain = keystore.getCertificateChain(privateKeyAlias);
        	
            CMSSignedDataGenerator cmsSignedDataGenerator = new CMSSignedDataGenerator();
            X509Certificate cert = (X509Certificate) certificateChain[0];
            if(algorithm==null) {
            	algorithm = "SHA256WithRSA";
            }
            ContentSigner signer = new JcaContentSignerBuilder(algorithm).build(keystore.getPrivateKey(privateKeyAlias, privateKeyPassword));
            cmsSignedDataGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(signer, cert));
            cmsSignedDataGenerator.addCertificates(new JcaCertStore(Arrays.asList(certificateChain)));
            PDFSignatureCMSTypedData signatureCMSTypedData = new PDFSignatureCMSTypedData(content, null);
            CMSSignedData signedData = cmsSignedDataGenerator.generate(signatureCMSTypedData, false);

            return signedData.getEncoded();
        }
        catch (Exception e) {
        	throw new UtilsException(e.getMessage(),e);
        }
    }
}
