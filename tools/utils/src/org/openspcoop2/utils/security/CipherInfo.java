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

import java.security.Key;

import javax.crypto.spec.IvParameterSpec;

/**	
 * CipherInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CipherInfo {

	private byte[] salt;
	
	private byte[] iv;
	private IvParameterSpec ivParameterSpec;
	
	private byte[] encodedKey;
	private Key key;
	
	public byte[] getSalt() {
		return this.salt;
	}
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	public byte[] getIv() {
		return this.iv;
	}
	public void setIv(byte[] iv) {
		this.iv = iv;
	}
	public IvParameterSpec getIvParameterSpec() {
		return this.ivParameterSpec;
	}
	public void setIvParameterSpec(IvParameterSpec ivParameterSpec) {
		this.ivParameterSpec = ivParameterSpec;
	}
	public byte[] getEncodedKey() {
		return this.encodedKey;
	}
	public void setEncodedKey(byte[] encodedKey) {
		this.encodedKey = encodedKey;
	}
	public Key getKey() {
		return this.key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	
}
