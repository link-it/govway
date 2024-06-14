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

package org.openspcoop2.utils.certificate;

/**	
 * KeystoreParams
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class KeystoreParams {

	private byte[] store;
	private String path;
	private String type;
	private String password;
	private String crls;
	private String ocspPolicy;
	private String byokPolicy;
	private String keyAlias;
	private String keyPassword;
	
	private String keyPairPublicKeyPath;
	private byte[] keyPairPublicKey;
	private String keyPairAlgorithm;
	
	private String description;
	
	public byte[] getStore() {
		return this.store;
	}
	public void setStore(byte[] store) {
		this.store = store;
	}
	public String getPath() {
		return this.path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCrls() {
		return this.crls;
	}
	public void setCrls(String crls) {
		this.crls = crls;
	}
	public String getOcspPolicy() {
		return this.ocspPolicy;
	}
	public void setOcspPolicy(String ocspPolicy) {
		this.ocspPolicy = ocspPolicy;
	}
	public String getByokPolicy() {
		return this.byokPolicy;
	}
	public void setByokPolicy(String byokPolicy) {
		this.byokPolicy = byokPolicy;
	}
	public String getKeyAlias() {
		return this.keyAlias;
	}
	public void setKeyAlias(String keyAlias) {
		this.keyAlias = keyAlias;
	}
	public String getKeyPassword() {
		return this.keyPassword;
	}
	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

	public String getKeyPairPublicKeyPath() {
		return this.keyPairPublicKeyPath;
	}
	public void setKeyPairPublicKeyPath(String keyPairPublicKeyPath) {
		this.keyPairPublicKeyPath = keyPairPublicKeyPath;
	}
	public byte[] getKeyPairPublicKey() {
		return this.keyPairPublicKey;
	}
	public void setKeyPairPublicKey(byte[] keyPairPublicKey) {
		this.keyPairPublicKey = keyPairPublicKey;
	}
	public String getKeyPairAlgorithm() {
		return this.keyPairAlgorithm;
	}
	public void setKeyPairAlgorithm(String keyPairAlgorithm) {
		this.keyPairAlgorithm = keyPairAlgorithm;
	}
	
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
