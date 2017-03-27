/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
import java.io.File;
import java.io.InputStream;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.security.KeyStore;
import org.openspcoop2.utils.security.Signature;
import org.openspcoop2.utils.security.VerifySignature;
import org.openspcoop2.utils.security.VerifyXmlSignature;
import org.openspcoop2.utils.security.XmlSignature;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Element;

/**	
 * TestSignature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12820 $, $Date: 2017-03-22 15:27:09 +0100 (Wed, 22 Mar 2017) $
 */
public class TestSignature {

	public static void main(String[] args) throws Exception {
		
		InputStream isKeystore = null;
		File fKeystore = null;
		InputStream isTruststore = null;
		File fTruststore = null;
		try{
			isKeystore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/keystore_example.jks");
			fKeystore = File.createTempFile("keystore", "jks");
			FileSystemUtilities.writeFile(fKeystore, Utilities.getAsByteArray(isKeystore));
			
			isTruststore = TestSignature.class.getResourceAsStream("/org/openspcoop2/utils/security/truststore_example.jks");
			fTruststore = File.createTempFile("truststore", "jks");
			FileSystemUtilities.writeFile(fTruststore, Utilities.getAsByteArray(isTruststore));
			
			String passwordChiavePrivata = "key123456";
			String passwordStore = "123456";
			String alias = "openspcoop";
			
			KeyStore keystore = new KeyStore(fKeystore.getAbsolutePath(), passwordStore);
			KeyStore truststore = new KeyStore(fTruststore.getAbsolutePath(), passwordStore);
			
			
			// 1. Esempio Signature Java 
			
			System.out.println("\n\n ================================");
			System.out.println("1. Example JavaSignature \n");
			
			String contenutoDaFirmare = "MarioRossi:23:05:1980";
			
			// Firma
			String signedAlgorithm = "SHA1WithRSA";
			Signature signature = new Signature(keystore, alias, passwordChiavePrivata);
			byte[] signed = signature.sign(contenutoDaFirmare.getBytes(), signedAlgorithm);
			System.out.println("1. JavaSignature Signed: "+new String(signed));
			
			// Verifica
			VerifySignature verify = new VerifySignature(truststore,alias);
			System.out.println("1. JavaSignature Verify: "+verify.verify(contenutoDaFirmare.getBytes(), signed, signedAlgorithm));
	
			
			
			
			// 2. Esempio Signature Xml 
			
			System.out.println("\n\n ================================");
			System.out.println("2. Example XmlSignature \n");
			
			String xmlInput = "<prova><test>VALORE</test></prova>";
			Element node = XMLUtils.getInstance().newElement(xmlInput.getBytes());
			
			// Firma
			XmlSignature xmlSignature = new XmlSignature(keystore, alias, passwordChiavePrivata);
			xmlSignature.addX509KeyInfo();
			xmlSignature.sign(node);
			System.out.println("2. XmlSignature Signed: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
			// Verifica
			VerifyXmlSignature xmlVerify = new VerifyXmlSignature(truststore,alias);
			System.out.println("2. XmlSignature Verify (no clean): "+xmlVerify.verify(node, false));
			System.out.println("2. XmlSignature Verify (no clean) xml: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			System.out.println("2. XmlSignature Verify (clean): "+xmlVerify.verify(node, true));
			System.out.println("2. XmlSignature Verify (clean) xml: "+PrettyPrintXMLUtils.prettyPrintWithTrAX(node));
			
		}finally{
			try{
				if(isKeystore!=null){
					isKeystore.close();
				}
			}catch(Exception e){}
			try{
				if(fKeystore!=null){
					fKeystore.delete();
				}
			}catch(Exception e){}
			try{
				if(isTruststore!=null){
					isTruststore.close();
				}
			}catch(Exception e){}
			try{
				if(fTruststore!=null){
					fTruststore.delete();
				}
			}catch(Exception e){}
		}
	}

}
