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

package org.openspcoop2.utils.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Mac;

import org.apache.cxf.rs.security.jose.jwa.AlgorithmUtils;
import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsSignature;
import org.apache.cxf.rt.security.crypto.HmacUtils;

/**	
 * HmacJwsSignatureProvider
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HmacJwsSignatureProviderExtended extends org.apache.cxf.rs.security.jose.jws.HmacJwsSignatureProvider {

	public HmacJwsSignatureProviderExtended(byte[] key, AlgorithmParameterSpec spec, SignatureAlgorithm algo) {
		super(key, spec, algo);
	}

	public HmacJwsSignatureProviderExtended(byte[] key, SignatureAlgorithm algo) {
		super(key, algo);
	}

	public HmacJwsSignatureProviderExtended(String arg0, SignatureAlgorithm arg1) {
		super(arg0, arg1);
	}
	
	// METODI PER PKCS11
	private SecretKeyPkcs11 secretKey;
	private AlgorithmParameterSpec hmacSpec;
	public HmacJwsSignatureProviderExtended(SecretKeyPkcs11 secretKey, AlgorithmParameterSpec spec, SignatureAlgorithm algo) {
		super(secretKey.getSecretKey().getEncoded(), spec, algo);
		this.secretKey = secretKey;
		this.hmacSpec = spec;
	}
	public HmacJwsSignatureProviderExtended(SecretKeyPkcs11 secretKey, SignatureAlgorithm algo) {
		super(secretKey.getSecretKey().getEncoded(), algo);
		this.secretKey = secretKey;
	}
	// METODI PER PKCS11

    @Override
	protected JwsSignature doCreateJwsSignature(JwsHeaders headers) {
    	
    	if(this.secretKey==null) {
    		return super.doCreateJwsSignature(headers);
    	}
    	
        final String sigAlgo = headers.getSignatureAlgorithm().getJwaName();
        final Mac mac = HmacUtils.getMac(AlgorithmUtils.toJavaName(sigAlgo), this.secretKey.getProvider());
        try {
        	if (this.hmacSpec == null) {
        		mac.init(this.secretKey.getSecretKey());
        	}
        	else {
        		mac.init(this.secretKey.getSecretKey(), this.hmacSpec);
        	}
        } catch (InvalidKeyException e) {
            throw new SecurityException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new SecurityException(e);
        }
        
        return new JwsSignature() {

            @Override
            public void update(byte[] src, int off, int len) {
                mac.update(src, off, len);
            }

            @Override
            public byte[] sign() {
                return mac.doFinal();
            }

        };
    }

}
