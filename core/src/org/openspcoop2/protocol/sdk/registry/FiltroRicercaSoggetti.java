/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk.registry;

import java.util.Map;

import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;

/**
 *  FiltroRicercaSoggetti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltroRicercaSoggetti {

	private String tipo;
	private String nome;
	private String nomePdd;
	private ProtocolProperties protocolProperties;
	private Map<String, String> proprieta;
	
	public String getTipo() {
		return this.tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getNomePdd() {
		return this.nomePdd;
	}
	public void setNomePdd(String nomePdd) {
		this.nomePdd = nomePdd;
	}
	public ProtocolProperties getProtocolProperties() {
		return this.protocolProperties;
	}
	public void setProtocolProperties(ProtocolProperties protocolProperties) {
		this.protocolProperties = protocolProperties;
	}
	public Map<String, String> getProprieta() {
		return this.proprieta;
	}
	public void setProprieta(Map<String, String> proprieta) {
		this.proprieta = proprieta;
	}
}
