/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.security.cert.Certificate;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;

/**	
 * VerifySignature
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VerifySignature {

	private KeyStore keystore;
	private Certificate certificate;
	
	public VerifySignature(KeyStore keystore) throws UtilsException{
		this.keystore = keystore;
		this.certificate = this.keystore.getCertificate();
	}
	public VerifySignature(KeyStore keystore, String alias) throws UtilsException{
		this.keystore = keystore;
		this.certificate = this.keystore.getCertificate(alias);
	}
	
	public boolean verify(String data, String charsetName, byte[] signatureData, String algorithm) throws UtilsException{
		try{
			return this.verify(data.getBytes(charsetName), signatureData, algorithm);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public boolean verify(byte[] data, byte[] signatureData, String algorithm) throws UtilsException{
		try{
			java.security.Signature sig = java.security.Signature.getInstance(algorithm);
			sig.initVerify(this.certificate);
	        sig.update(data);
	        return sig.verify(signatureData);	
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
