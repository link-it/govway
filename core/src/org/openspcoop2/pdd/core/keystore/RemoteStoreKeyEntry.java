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
package org.openspcoop2.pdd.core.keystore;

import java.util.Date;

/**
 * RemoteStoreKeyEntry
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RemoteStoreKeyEntry {

	private long id;
	private long idRemoteStore;
	private Date dataRegistrazione;
	
	private String kid;
	private byte[] contentKey;
	private Date dataAggiornamento;
	
	private String clientId;
	private String clientDetails;
	private String organizationDetails;
	private Date clientDataAggiornamento;
	
	private String organizationName;
	private String organizationExternalOrigin;
	private String organizationExternalId;
	private String organizationCategory;
	
	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getIdRemoteStore() {
		return this.idRemoteStore;
	}
	public void setIdRemoteStore(long idRemoteStore) {
		this.idRemoteStore = idRemoteStore;
	}
	public Date getDataRegistrazione() {
		return this.dataRegistrazione;
	}
	public void setDataRegistrazione(Date dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
	}
	public String getKid() {
		return this.kid;
	}
	public void setKid(String kid) {
		this.kid = kid;
	}
	public byte[] getContentKey() {
		return this.contentKey;
	}
	public void setContentKey(byte[] contentKey) {
		this.contentKey = contentKey;
	}
	public Date getDataAggiornamento() {
		return this.dataAggiornamento;
	}
	public void setDataAggiornamento(Date dataAggiornamento) {
		this.dataAggiornamento = dataAggiornamento;
	}
	public String getClientId() {
		return this.clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientDetails() {
		return this.clientDetails;
	}
	public void setClientDetails(String clientDetails) {
		this.clientDetails = clientDetails;
	}
	public String getOrganizationDetails() {
		return this.organizationDetails;
	}
	public void setOrganizationDetails(String organizationDetails) {
		this.organizationDetails = organizationDetails;
	}
	public Date getClientDataAggiornamento() {
		return this.clientDataAggiornamento;
	}
	public void setClientDataAggiornamento(Date clientDataAggiornamento) {
		this.clientDataAggiornamento = clientDataAggiornamento;
	}

	public String getOrganizationName() {
		return this.organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getOrganizationExternalOrigin() {
		return this.organizationExternalOrigin;
	}
	public void setOrganizationExternalOrigin(String organizationExternalOrigin) {
		this.organizationExternalOrigin = organizationExternalOrigin;
	}
	public String getOrganizationExternalId() {
		return this.organizationExternalId;
	}
	public void setOrganizationExternalId(String organizationExternalId) {
		this.organizationExternalId = organizationExternalId;
	}
	public String getOrganizationCategory() {
		return this.organizationCategory;
	}
	public void setOrganizationCategory(String organizationCategory) {
		this.organizationCategory = organizationCategory;
	}
}
