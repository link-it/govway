/*
 * AdroitLogic UltraESB Enterprise Service Bus
 *
 * Copyright (c) 2010 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 *
 * GNU Affero General Public License Usage
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program (See LICENSE-AGPL.TXT).
 * If not, see http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Commercial Usage
 *
 * Licensees holding valid UltraESB Commercial licenses may use this file in accordance with the UltraESB Commercial
 * License Agreement provided with the Software or, alternatively, in accordance with the terms contained in a written
 * agreement between you and AdroitLogic.
 *
 * If you are unsure which license is appropriate for your use, or have questions regarding the use of this file,
 * please contact AdroitLogic at info@adroitlogic.com
 */
/*
 * Modificato per supportare le seguenti funzionalita':
 * - firma e cifratura degli attachments
 * - cifratura con chiave simmetrica
 * - supporto CRL 
 * 
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, 
 * either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope 
 * that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.security.message.soapbox;

import java.security.cert.X509Certificate;

import org.adroitlogic.soapbox.CryptoUtil;
import org.adroitlogic.soapbox.MessageSecurityContext;
import org.adroitlogic.soapbox.Processor;
import org.adroitlogic.soapbox.SBConstants;
import org.adroitlogic.soapbox.SecurityFailureException;
import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.security.message.signature.SunEnvelopeIdResolver;
import org.openspcoop2.security.message.signature.XMLSecEnvelopeIdResolver;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.w3c.dom.Element;

/**
 * ProcessSignedMessage
 *
 * @author Andrea Poli <apoli@link.it>
 * @author Giovanni Bussu <bussu@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProcessSignedMessage implements Processor {

	private boolean useXMLSec = true;
	public boolean isUseXMLSec() {
		return this.useXMLSec;
	}
	public void setUseXMLSec(boolean useXMLSec) {
		this.useXMLSec = useXMLSec;
	}
	
	private OpenSPCoop2Message message;
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	private String actor;
	private boolean mustUnderstand;
	public void setActor(String actor) {
		this.actor = actor;
	}
	public void setMustUnderstand(boolean mustUnderstand) {
		this.mustUnderstand = mustUnderstand;
	}
	
	private X509Certificate [] certificates = null;
	public X509Certificate[] getCertificates() {
		return this.certificates;
	}
	
    private static final Logger logger = LoggerWrapperFactory.getLogger(ProcessSignedMessage.class);

    @Override
	public void process(org.adroitlogic.soapbox.SecurityConfig secConfig, MessageSecurityContext msgSecCtx) {

    	//System.out.println("PROCESS SIGN XMLSEC["+this.useXMLSec+"]");
    	
    	// ** Recupero header WSSecurity **
    	Element wsseSecurityElem = null;
        try{
        	wsseSecurityElem = WSSUtils.getWSSecurityHeader(msgSecCtx.getDocument(), this.actor, this.mustUnderstand);
        }catch(Exception e){
			throw new SecurityFailureException(e.getMessage(), e);
		}
    	
        
        
        // ** Recupero elemento Signaure **
        Element elemSignature = CryptoUtil.getSecurityProcessorElement(wsseSecurityElem, SBConstants.DS, "Signature");
        if (elemSignature == null) {
            if (ProcessSignedMessage.logger.isDebugEnabled()) {
                ProcessSignedMessage.logger.debug("Message is not signed - skipping ProcessSignedMessage");
            }
            throw new SecurityFailureException("WS-Security failure - Message is not signed");
        }

        
        
        // *** 1. Prepare to verify signature engine***
        // NOTA:
        // Vi sono fondamentalmente due versioni di XMLSignature con classi correlate.
        // - com.sun.org.apache.xml.internal.security.signature: presente nel runtime di java
        // - org.apache.xml.security.signature: presente in xmlsec-2.0.7.jar
        //
        // A seconda della versione utilizzata devono essere implementate delle classi a corredo:
        // - com.sun.org.apache.xml.internal.security.transforms.TransformSpi implementato tramite org.openspcoop2.security.message.signature.SunAttachmentContentTransform
        // - org.apache.xml.security.transforms.TransformSpi implementato tramite org.openspcoop2.security.message.signature.XMLSecAttachmentContentTransform
        // NOTA: L'implementazione del Transformer tramite le classi della Sun cosi come realizzate usano metodi diversi presenti su Java 1.6 patch 26 o maggiore rispetto a Java 7.
        //		 Java 1.7 ha modificato i metodi della classe astratta com.sun.org.apache.xml.internal.security.transforms.TransformSpi
        //	     Il codice seguente e' stato scritto per poter effettuare i test incrociati sulle due versioni adeguando le classi utilizzate rispetto ad una variabile cablata nel codice
        //		 definita in org.openspcoop2.security.message.soapbox.MesageSecurityContext_soapbox.USE_XMLSEC_IMPL
        // 
        // A seconda della versione utilizzata devono inoltre essere implementate le classe di risoluzione delle signature reference
        // - com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi implementata tramite org.openspcoop2.security.message.signature.SunEnvelopeIdResolver
        // - org.apache.xml.security.utils.resolver.ResourceResolverSpi implementata tramite org.openspcoop2.security.message.signature.XMLSecEnvelopeIdResolver
        com.sun.org.apache.xml.internal.security.signature.XMLSignature sigSUN = null;
        org.apache.xml.security.signature.XMLSignature sigXMLSec = null;
        //org.apache.xml.security.signature.XMLSignature sig2 = null;
        try {
        	if(this.useXMLSec){
        		sigXMLSec = new org.apache.xml.security.signature.XMLSignature(elemSignature, null);
        	    //sig2 = new org.apache.xml.security.signature.XMLSignature(msgSecCtx.getDocument(), null, "http://www.w3.org/2000/09/xmldsig#rsa-sha1", "http://www.w3.org/2001/10/xml-exc-c14n#");
        	}else{
        		sigSUN = new com.sun.org.apache.xml.internal.security.signature.XMLSignature(elemSignature, null);
        	}
        } catch (Exception e) {
            throw new SecurityFailureException("No signature or error in processing signature in document", e);
        }
        if(this.useXMLSec){
        	sigXMLSec.addResourceResolver(XMLSecEnvelopeIdResolver.getInstance(this.message));
        }
        else{
        	sigSUN.addResourceResolver(SunEnvelopeIdResolver.getInstance(this.message));
        }
        
        
                
        
        // *** 2. Extract certificate for the signature, from reference or other means ***
        X509Certificate[] certs = null;
        if(this.useXMLSec){
        	org.apache.xml.security.keys.KeyInfo keyInfo = sigXMLSec.getKeyInfo();
        	if (keyInfo != null && keyInfo.containsKeyValue()) {
        		throw new UnsupportedOperationException("Verification of signatures from PublicKeys not yet supported");
        	} else if (keyInfo != null) {
        		certs = CryptoUtil.getCertificatesFromSecurityTokenReference(secConfig, msgSecCtx,
        				CryptoUtil.getFirstChild(elemSignature, SBConstants.WSSE, SBConstants.SECURITY_TOKEN_REFERENCE));
        	} else {
        		throw new SecurityFailureException("No key information for signature was found");
        	}
        }else{
        	com.sun.org.apache.xml.internal.security.keys.KeyInfo keyInfo = sigSUN.getKeyInfo();
        	if (keyInfo != null && keyInfo.containsKeyValue()) {
        		throw new UnsupportedOperationException("Verification of signatures from PublicKeys not yet supported");
        	} else if (keyInfo != null) {
        		certs = CryptoUtil.getCertificatesFromSecurityTokenReference(secConfig, msgSecCtx,
        				CryptoUtil.getFirstChild(elemSignature, SBConstants.WSSE, SBConstants.SECURITY_TOKEN_REFERENCE));
        	} else {
        		throw new SecurityFailureException("No key information for signature was found");
        	}
        }
        this.certificates = certs;
        
        
       
        
        // *** 3. Check validity of certificate used and validate signature ***
        if (certs != null && certs.length != 0) {

            for (int i=0; i<certs.length; i++) {
                try {
                    certs[i].checkValidity();
                } catch (Exception e) {
                    throw new SecurityFailureException("Certificate used for signature with DN : " +
                        certs[i].getSubjectDN().toString() + " is not valid", e);
                }
            }

            try {
                ((SecurityConfig)secConfig).validateX509Certificate(certs);
            } catch (Exception e) {
                throw new SecurityFailureException("Certificate used for signature with DN : " +
                    certs[0].getSubjectDN().toString() + " or its issuer/s is/are not valid", e);
            }

            try {
            	boolean signValid = false;
            	if(this.useXMLSec){
            		signValid = sigXMLSec.checkSignatureValue(certs[0]);
            	}else{
            		signValid = sigSUN.checkSignatureValue(certs[0]);
            	}
            	
                if (!signValid) {
                	//System.out.println("TEST["+sig2.checkSignatureValue(certs[0])+"]");
                	throw new SecurityFailureException("Signature verification failed");
                } else {
                    msgSecCtx.setProperty(org.adroitlogic.soapbox.api.MessageSecurityContext.USER_CERTS, certs);
                }
            } catch (Exception e) {
                throw new SecurityFailureException("Signature verification failed", e);
            }
        } else {
            throw new SecurityFailureException("Certificate for signature was not found");
        }

        if (ProcessSignedMessage.logger.isDebugEnabled()) {
            ProcessSignedMessage.logger.debug("Signature verified successfully");
        }
    }

}
