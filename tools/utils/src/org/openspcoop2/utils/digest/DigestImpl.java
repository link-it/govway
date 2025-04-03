/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.Arrays;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * DigestImpl
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DigestImpl implements IDigest {

	protected DigestConfig config;
	protected Logger log;
	
	@Override
	public void init(Logger log, DigestConfig config) {
		this.config = config;
		this.log = log;
	}

	
	private byte[] standardDigest(byte[] msg) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(this.config.getDigestType().getAlgorithmName());
		return digest.digest(msg);
	}
	
	private byte[] shakeDigest(byte[] msg) throws NoSuchAlgorithmException {
		MessageDigest digest = null;
		
		if (this.config.getDigestType().equals(DigestType.SHAKE128)) {
			digest = new SHA3.DigestShake128_256();
		} else if (this.config.getDigestType().equals(DigestType.SHAKE256)) {
			digest = new SHA3.DigestShake256_512();
		} else {
			throw new NoSuchAlgorithmException("digest algorithm : " + this.config.getDigestType().getAlgorithmName() + " not found ");
		}
		return digest.digest(msg);
	}
	
	
	@Override
	public byte[] digest(byte[] input, byte[] salt) throws UtilsException {
		byte[] output = null;
		
		if (salt.length != this.config.getSaltLength())
			throw new UtilsException("lunghezza salt fornito: " + salt.length + ", lunghezza attesa: " + this.config.getSaltLength());
		
		byte[] msg = Arrays.concatenate(input, salt);
		
		try {
			switch (this.config.getDigestType()) {
			case SHAKE128:
			case SHAKE256:
				output = shakeDigest(msg);
				break;
			case SHA256:
			case SHA384:
			case SHA3_256:
			case SHA3_384:
			case SHA3_512:
			case SHA512:
			case SHA512_256:
				output = standardDigest(Arrays.concatenate(input, salt));
				break;
			}
		} catch (NoSuchAlgorithmException e) {
			throw new UtilsException("Algorithm not found: " + this.config.getDigestType().getAlgorithmName(), e);
		}
		
		if (this.config.isBase64Encode()) {
			output = Base64.getEncoder().encode(output);
		}
		
		return output;
	}

	@Override
	public boolean check(byte[] input, byte[] salt, byte[] digest) throws UtilsException {
		
		byte[] generated = this.digest(input, salt);
		if (generated.length != digest.length)
			return false;
		
		boolean isEqual = true;
		for (int i = 0; i < generated.length; i++) {
			isEqual &= (generated[i] == digest[i]);
		}
		return isEqual;
	}

}
