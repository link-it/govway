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
package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.io.Serializable;

import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**     
 * PolicyDati
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyDati implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String profilo;
	
	private String nomePorta;
	private RuoloPolicy ruoloPorta;
	
	private IDSoggetto idFruitore;
	private IDServizio idServizio;
	private String gruppo;
	private String identificativo;
	
	private String connettore;
	
	private String tokenPolicyNegoziazione;
	
	private String tokenPolicyValidazioneDynamicDiscovery;
	private String tokenPolicyValidazioneJwt;
	private String tokenPolicyValidazioneIntrospection;
	private String tokenPolicyValidazioneUserInfo;
	
	private String attributeAuthority;
	private String attributeAuthorityResponseJwt;
	
	public String getProfilo() {
		return this.profilo;
	}
	public void setProfilo(String profilo) {
		this.profilo = profilo;
	}
	
	public String getNomePorta() {
		return this.nomePorta;
	}
	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}
	public RuoloPolicy getRuoloPorta() {
		return this.ruoloPorta;
	}
	public void setRuoloPorta(RuoloPolicy ruoloPorta) {
		this.ruoloPorta = ruoloPorta;
	}
	
	public String getIdentificativo() {
		return this.identificativo;
	}
	public void setIdentificativo(String identificativo) {
		this.identificativo = identificativo;
	}
	public IDSoggetto getIdFruitore() {
		return this.idFruitore;
	}
	public void setIdFruitore(IDSoggetto idFruitore) {
		this.idFruitore = idFruitore;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	public String getGruppo() {
		return this.gruppo;
	}
	public void setGruppo(String gruppo) {
		this.gruppo = gruppo;
	}

	public String getConnettore() {
		return this.connettore;
	}
	public void setConnettore(String connettore) {
		this.connettore = connettore;
	}
	
	
	public String getTokenPolicyNegoziazione() {
		return this.tokenPolicyNegoziazione;
	}
	public void setTokenPolicyNegoziazione(String tokenPolicyNegoziazione) {
		this.tokenPolicyNegoziazione = tokenPolicyNegoziazione;
	}
	
	public String getTokenPolicyValidazioneDynamicDiscovery() {
		return this.tokenPolicyValidazioneDynamicDiscovery;
	}
	public void setTokenPolicyValidazioneDynamicDiscovery(String tokenPolicyValidazioneDynamicDiscovery) {
		this.tokenPolicyValidazioneDynamicDiscovery = tokenPolicyValidazioneDynamicDiscovery;
	}
	public String getTokenPolicyValidazioneJwt() {
		return this.tokenPolicyValidazioneJwt;
	}
	public void setTokenPolicyValidazioneJwt(String tokenPolicyValidazioneJwt) {
		this.tokenPolicyValidazioneJwt = tokenPolicyValidazioneJwt;
	}
	public String getTokenPolicyValidazioneIntrospection() {
		return this.tokenPolicyValidazioneIntrospection;
	}
	public void setTokenPolicyValidazioneIntrospection(String tokenPolicyValidazioneIntrospection) {
		this.tokenPolicyValidazioneIntrospection = tokenPolicyValidazioneIntrospection;
	}
	public String getTokenPolicyValidazioneUserInfo() {
		return this.tokenPolicyValidazioneUserInfo;
	}
	public void setTokenPolicyValidazioneUserInfo(String tokenPolicyValidazioneUserInfo) {
		this.tokenPolicyValidazioneUserInfo = tokenPolicyValidazioneUserInfo;
	}
	
	public String getAttributeAuthority() {
		return this.attributeAuthority;
	}
	public void setAttributeAuthority(String attributeAuthority) {
		this.attributeAuthority = attributeAuthority;
	}
	public String getAttributeAuthorityResponseJwt() {
		return this.attributeAuthorityResponseJwt;
	}
	public void setAttributeAuthorityResponseJwt(String attributeAuthorityResponseJwt) {
		this.attributeAuthorityResponseJwt = attributeAuthorityResponseJwt;
	}
}
