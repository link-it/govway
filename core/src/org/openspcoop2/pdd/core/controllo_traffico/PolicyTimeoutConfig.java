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
package org.openspcoop2.pdd.core.controllo_traffico;

/**
 * PolicyConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyTimeoutConfig {

	private String policyNegoziazione;
	
	private String policyValidazioneIntrospection;
	private String policyValidazioneUserInfo;
	
	private String attributeAuthority;
	
	public String getPolicyNegoziazione() {
		return this.policyNegoziazione;
	}
	public void setPolicyNegoziazione(String policyNegoziazione) {
		this.policyNegoziazione = policyNegoziazione;
	}
	public String getPolicyValidazioneIntrospection() {
		return this.policyValidazioneIntrospection;
	}
	public void setPolicyValidazioneIntrospection(String policyValidazioneIntrospection) {
		this.policyValidazioneIntrospection = policyValidazioneIntrospection;
	}
	public String getPolicyValidazioneUserInfo() {
		return this.policyValidazioneUserInfo;
	}
	public void setPolicyValidazioneUserInfo(String policyValidazioneUserInfo) {
		this.policyValidazioneUserInfo = policyValidazioneUserInfo;
	}
	
	public String getAttributeAuthority() {
		return this.attributeAuthority;
	}
	public void setAttributeAuthority(String attributeAuthority) {
		this.attributeAuthority = attributeAuthority;
	}
}
