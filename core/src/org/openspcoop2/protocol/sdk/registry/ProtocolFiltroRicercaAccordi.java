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

import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;

/**
 *  FiltroRicercaAccordi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolFiltroRicercaAccordi {

	private String nome;
	private Integer versione;
	private IDSoggetto soggetto;
	private IDGruppo idGruppo;
	private ServiceBinding serviceBinding;
	private ProtocolProperties protocolProperties;

	private Boolean escludiServiziComposti;
	private Boolean escludiServiziNonComposti;
	
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getVersione() {
		return this.versione;
	}
	public void setVersione(Integer versione) {
		this.versione = versione;
	}
	public IDSoggetto getSoggetto() {
		return this.soggetto;
	}
	public void setSoggetto(IDSoggetto soggetto) {
		this.soggetto = soggetto;
	}
	public ServiceBinding getServiceBinding() {
		return this.serviceBinding;
	}
	public void setServiceBinding(ServiceBinding serviceBinding) {
		this.serviceBinding = serviceBinding;
	}
	public IDGruppo getIdGruppo() {
		return this.idGruppo;
	}
	public void setIdGruppo(IDGruppo idGruppo) {
		this.idGruppo = idGruppo;
	}
	public ProtocolProperties getProtocolProperties() {
		return this.protocolProperties;
	}
	public void setProtocolProperties(ProtocolProperties protocolProperties) {
		this.protocolProperties = protocolProperties;
	}
	
	public Boolean getEscludiServiziComposti() {
		return this.escludiServiziComposti;
	}
	public void setEscludiServiziComposti(Boolean escludiServiziComposti) {
		this.escludiServiziComposti = escludiServiziComposti;
	}
	public Boolean getEscludiServiziNonComposti() {
		return this.escludiServiziNonComposti;
	}
	public void setEscludiServiziNonComposti(Boolean escludiServiziNonComposti) {
		this.escludiServiziNonComposti = escludiServiziNonComposti;
	}
}
