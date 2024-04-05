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

package org.openspcoop2.core.transazioni.utils;

import java.io.Serializable;

import org.openspcoop2.core.transazioni.CredenzialeMittente;

/**     
 * CredenzialiMittente
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialiMittente implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CredenzialeMittente trasporto;
	
	private CredenzialeMittente tokenIssuer;
	private CredenzialeMittente tokenClientId;
	private CredenzialeMittente tokenSubject;
	private CredenzialeMittente tokenUsername;
	private CredenzialeMittente tokenEMail;
	
	private CredenzialeMittente tokenPdndClientJson;
	private CredenzialeMittente tokenPdndOrganizationJson;
	private CredenzialeMittente tokenPdndOrganizationName;
	
	public CredenzialeMittente getTrasporto() {
		return this.trasporto;
	}
	public void setTrasporto(CredenzialeMittente trasporto) {
		this.trasporto = trasporto;
	}
	
	public CredenzialeMittente getTokenIssuer() {
		return this.tokenIssuer;
	}
	public void setTokenIssuer(CredenzialeMittente tokenIssuer) {
		this.tokenIssuer = tokenIssuer;
	}
	public CredenzialeMittente getTokenClientId() {
		return this.tokenClientId;
	}
	public void setTokenClientId(CredenzialeMittente tokenClientId) {
		this.tokenClientId = tokenClientId;
	}
	public CredenzialeMittente getTokenSubject() {
		return this.tokenSubject;
	}
	public void setTokenSubject(CredenzialeMittente tokenSubject) {
		this.tokenSubject = tokenSubject;
	}
	public CredenzialeMittente getTokenUsername() {
		return this.tokenUsername;
	}
	public void setTokenUsername(CredenzialeMittente tokenUsername) {
		this.tokenUsername = tokenUsername;
	}
	public CredenzialeMittente getTokenEMail() {
		return this.tokenEMail;
	}
	public void setTokenEMail(CredenzialeMittente tokenEMail) {
		this.tokenEMail = tokenEMail;
	}
	
	public CredenzialeMittente getTokenPdndClientJson() {
		return this.tokenPdndClientJson;
	}
	public void setTokenPdndClientJson(CredenzialeMittente tokenPdndClientJson) {
		this.tokenPdndClientJson = tokenPdndClientJson;
	}
	public CredenzialeMittente getTokenPdndOrganizationJson() {
		return this.tokenPdndOrganizationJson;
	}
	public void setTokenPdndOrganizationJson(CredenzialeMittente tokenPdndOrganizationJson) {
		this.tokenPdndOrganizationJson = tokenPdndOrganizationJson;
	}
	public CredenzialeMittente getTokenPdndOrganizationName() {
		return this.tokenPdndOrganizationName;
	}
	public void setTokenPdndOrganizationName(CredenzialeMittente tokenPdndOrganizationName) {
		this.tokenPdndOrganizationName = tokenPdndOrganizationName;
	}
}
