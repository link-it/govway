/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.util.Store;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;

/**
* PDFSignature
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class PDFSignature {

	private PDSignatureField signatureField;
	private PDSignature signature;
	private List<PDFSignerInformation> signers = new ArrayList<>();
	
	public PDFSignature(byte[] pdf, PDSignatureField signatureField, PDSignature signature) throws UtilsException {
		this.signatureField = signatureField;
		this.signature = signature;
		this.init(pdf);
	}

	private void init(byte[] pdf) throws UtilsException {
		
		try {

			byte[] signatureContent = this.signature.getContents(pdf);
	        byte[] signedContent = this.signature.getSignedContent(pdf);

	        CMSProcessable cmsProcessableInputStream = new CMSProcessableByteArray(signedContent);
            CMSSignedData cmsSignedData = new CMSSignedData(cmsProcessableInputStream, signatureContent);
            
            // get certificates
            Store<?> certStore = cmsSignedData.getCertificates();
            // get signers
            SignerInformationStore signerInfos = cmsSignedData.getSignerInfos();
            
            Iterator<?> it = signerInfos.getSigners().iterator();
            while (it.hasNext()) {
                SignerInformation signer = (SignerInformation) it.next();
                
                // get all certificates for a signer
                @SuppressWarnings("unchecked")
				Collection<?> certCollection = certStore.getMatches(signer.getSID());
                
                List<Certificate> certificates = new ArrayList<>();
	            
                // iterates all certificates of a signer
                Iterator<?> certIt = certCollection.iterator();
                while (certIt.hasNext()) {
                	X509CertificateHolder certificateHolder = (X509CertificateHolder) certIt.next();
                	Certificate cer = ArchiveLoader.load(certificateHolder.getEncoded());
    	            certificates.add(cer);
                }
                
                PDFSignerInformation info = new PDFSignerInformation();
                info.setCertificates(certificates);
                info.setSigner(signer);
                this.signers.add(info);

	        } 
		 } catch (Exception e) {
			 throw new UtilsException(e.getMessage(),e);
		 }
	}
	
	public PDSignatureField getSignatureField() {
		return this.signatureField;
	}

	public PDSignature getSignature() {
		return this.signature;
	}
	
	public String getName() {
		return this.signature.getName();
	}
	
	public List<PDFSignerInformation> getSigners() {
		return this.signers;
	}
}
