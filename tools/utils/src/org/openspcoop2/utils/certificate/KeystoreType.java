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


package org.openspcoop2.utils.certificate;

/**
 * Contiene i tipi di keystore
 *
 * @author corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public enum KeystoreType {

	JKS ("jks","JKS"),
	PKCS12 ("pkcs12","PKCS12"),
	PKCS11 ("pkcs11","PKCS11"),
	PKCS8 ("pkcs8","PKCS8"),
	PKCS1 ("pkcs1","PKCS1"),
	JCEKS ("jceks","JCEKS"),
	JWK_SET ("jwk","JWK Set"),
	PUBLIC_KEY ("public","Public Key"),
	KEY_PAIR ("keys","Key Pair");
	
	
	private final String nome;
	private final String label;

	KeystoreType(String nome, String label)
	{
		this.nome = nome;
		this.label = label;
	}

	public String getNome()
	{
		return this.nome;
	}

	public String getLabel() {
		if(this.label!=null) {
			return this.label;
		}
		return this.nome;
	}

	
	@Override
	public String toString() {
		return this.nome;
	}
	
	public static KeystoreType toEnumFromName(String name) {
		KeystoreType [] tipi = KeystoreType.values();
		for (int i = 0; i < tipi.length; i++) {
			KeystoreType tipo = tipi[i];
			if(tipo.getNome().equals(name)) {
				return tipo;
			}
		}
		return null;
	}
	public static KeystoreType toEnumFromLabel(String label) {
		KeystoreType [] tipi = KeystoreType.values();
		for (int i = 0; i < tipi.length; i++) {
			KeystoreType tipo = tipi[i];
			if(tipo.getLabel().equals(label)) {
				return tipo;
			}
		}
		return null;
	}
}

