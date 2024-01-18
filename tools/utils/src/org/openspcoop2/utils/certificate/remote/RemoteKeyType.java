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
package org.openspcoop2.utils.certificate.remote;

import java.io.Serializable;

/**
 * RemoteKeyType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum RemoteKeyType implements Serializable {

	PUBLIC_KEY ("public","Public Key"),
	JWK ("jwk","JWK (Json Web Key)"),
	X509 ("x509","X.509 Certificate");
	
	private final String nome;
	private final String label;

	RemoteKeyType(String nome, String label)
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
	
	public static RemoteKeyType toEnumFromName(String name) {
		RemoteKeyType [] tipi = RemoteKeyType.values();
		for (int i = 0; i < tipi.length; i++) {
			RemoteKeyType tipo = tipi[i];
			if(tipo.getNome().equals(name)) {
				return tipo;
			}
		}
		return null;
	}
	public static RemoteKeyType toEnumFromLabel(String label) {
		RemoteKeyType [] tipi = RemoteKeyType.values();
		for (int i = 0; i < tipi.length; i++) {
			RemoteKeyType tipo = tipi[i];
			if(tipo.getLabel().equals(label)) {
				return tipo;
			}
		}
		return null;
	}
	
}
