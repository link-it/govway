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

package org.openspcoop2.protocol.sdk.state;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.beans.AbstractPolicyConfiguration;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * RequestRateLimitingConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequestRateLimitingConfig implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String key = null;

	private boolean cached = false;
	
	private TipoPdD tipoPdD;
	private String nomePorta;
	private IDServizio idServizio;
	private IDSoggetto idFruitore; // solo nelle porte delegate
	
	private Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttive_dimensioneMessaggi;
	private Map<String, AttivazionePolicy> attivazionePolicies_dimensioneMessaggi = new HashMap<String, AttivazionePolicy>();
	private Map<String, ConfigurazionePolicy> configurazionePolicies_dimensioneMessaggi = new HashMap<String, ConfigurazionePolicy>();
	
	private Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttive;
	private Map<String, AttivazionePolicy> attivazionePolicies = new HashMap<String, AttivazionePolicy>();
	private Map<String, ConfigurazionePolicy> configurazionePolicies = new HashMap<String, ConfigurazionePolicy>();
	
	private ConfigurazioneGenerale configurazioneGenerale;
	private AbstractPolicyConfiguration configurazionePolicyRateLimitingGlobali;
	
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public boolean isCached() {
		return this.cached;
	}
	public void setCached(boolean cached) {
		this.cached = cached;
	}
	public void setDatiPorta(TipoPdD tipoPdD, String nomePorta, IDServizio idServizio, IDSoggetto idSoggettoFrutore) {
		this.tipoPdD = tipoPdD;
		this.nomePorta = nomePorta;
		this.idServizio = idServizio;
		this.idFruitore = idSoggettoFrutore;
	}
	public TipoPdD getTipoPdD() {
		return this.tipoPdD;
	}
	public String getNomePorta() {
		return this.nomePorta;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public IDSoggetto getIdFruitore() {
		return this.idFruitore;
	}

	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getMapPolicyAttive_dimensioneMessaggi() {
		return this.mapPolicyAttive_dimensioneMessaggi;
	}
	public void setMapPolicyAttive_dimensioneMessaggi(
			Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttive_dimensioneMessaggi) {
		this.mapPolicyAttive_dimensioneMessaggi = mapPolicyAttive_dimensioneMessaggi;
	}
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getMapPolicyAttive() {
		return this.mapPolicyAttive;
	}
	public void setMapPolicyAttive(Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> mapPolicyAttive) {
		this.mapPolicyAttive = mapPolicyAttive;
	}
	
	public void addAttivazionePolicy(String id, AttivazionePolicy attivazionePolicy) {
		this.attivazionePolicies.put(id, attivazionePolicy);
	}
	public AttivazionePolicy getAttivazionePolicy(String id) {
		return this.attivazionePolicies.get(id);
	}
	public void addAttivazionePolicy_dimensioneMessaggi(String id, AttivazionePolicy attivazionePolicy) {
		this.attivazionePolicies_dimensioneMessaggi.put(id, attivazionePolicy);
	}
	public AttivazionePolicy getAttivazionePolicy_dimensioneMessaggi(String id) {
		return this.attivazionePolicies_dimensioneMessaggi.get(id);
	}
	
	public void addConfigurazionePolicy(String id, ConfigurazionePolicy configurazionePolicy) {
		this.configurazionePolicies.put(id, configurazionePolicy);
	}
	public ConfigurazionePolicy getConfigurazionePolicy(String id) {
		return this.configurazionePolicies.get(id);
	}
	public void addConfigurazionePolicy_dimensioneMessaggi(String id, ConfigurazionePolicy configurazionePolicy) {
		this.configurazionePolicies_dimensioneMessaggi.put(id, configurazionePolicy);
	}
	public ConfigurazionePolicy getConfigurazionePolicy_dimensioneMessaggi(String id) {
		return this.configurazionePolicies_dimensioneMessaggi.get(id);
	}

	public ConfigurazioneGenerale getConfigurazioneGenerale() {
		return this.configurazioneGenerale;
	}
	public void setConfigurazioneGenerale(ConfigurazioneGenerale configurazioneGenerale) {
		this.configurazioneGenerale = configurazioneGenerale;
	}
	public AbstractPolicyConfiguration getConfigurazionePolicyRateLimitingGlobali() {
		return this.configurazionePolicyRateLimitingGlobali;
	}
	public void setConfigurazionePolicyRateLimitingGlobali(AbstractPolicyConfiguration configurazionePolicyRateLimitingGlobali) {
		this.configurazionePolicyRateLimitingGlobali = configurazionePolicyRateLimitingGlobali;
	}
}
