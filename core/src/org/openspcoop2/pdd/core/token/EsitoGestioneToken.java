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



package org.openspcoop2.pdd.core.token;

import org.openspcoop2.protocol.sdk.RestMessageSecurityToken;

/**
 * Esito di un processo di gestione token.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class EsitoGestioneToken extends AbstractEsitoValidazioneToken implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Informazioni sul token */
	private InformazioniToken informazioniToken;
	private RestMessageSecurityToken restSecurityToken;
	
	public InformazioniToken getInformazioniToken() {
		return this.informazioniToken;
	}
	public void setInformazioniToken(InformazioniToken informazioniToken) {
		this.informazioniToken = informazioniToken;
	}
	public RestMessageSecurityToken getRestSecurityToken() {
		return this.restSecurityToken;
	}
	public void setRestSecurityToken(RestMessageSecurityToken restSecurityToken) {
		this.restSecurityToken = restSecurityToken;
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		
		bf.append(super.toString());
		
		return bf.toString();
	}
}
