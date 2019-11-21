/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
package org.openspcoop2.pdd.core.behaviour.conditional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.CoreException;

/**
 * ConfigurazioneCondizionale
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneCondizionale {

	private boolean byFilter = true; 
	private ConfigurazioneSelettoreCondizione defaultConfig;
	private Map<String, ConfigurazioneSelettoreCondizione> actionConfigList = new HashMap<>();
	private Map<String, List<String>> actionConfigList_azioni = new HashMap<>();
	
	private IdentificazioneFallitaConfigurazione condizioneNonIdentificata;
	private IdentificazioneFallitaConfigurazione nessunConnettoreTrovato; 
	
	public boolean isByFilter() {
		return this.byFilter;
	}
	public void setByFilter(boolean byFilter) {
		this.byFilter = byFilter;
	}
	
	public ConfigurazioneSelettoreCondizione getDefaultConfig() {
		return this.defaultConfig;
	}
	public void setDefaultConfig(ConfigurazioneSelettoreCondizione defaultConfig) {
		this.defaultConfig = defaultConfig;
	}
	
	public void addActionConfig(String groupName, ConfigurazioneSelettoreCondizione config, String azione) throws CoreException {
		List<String> l = new ArrayList<String>();
		l.add(azione);
		this.addActionConfig(groupName, config, l);
	}
	public void addActionConfig(String groupName, ConfigurazioneSelettoreCondizione config, List<String> azioni) throws CoreException {
		// check che non un'azione non sia già registrata
		if(azioni==null || azioni.isEmpty()) {
			throw new CoreException("Azioni non indicate");
		}
		if(!this.actionConfigList_azioni.isEmpty()) {
			for (String azione : azioni) {
				for (String groupNameGiaRegistrate: this.actionConfigList_azioni.keySet()) {
					List<String> list = this.actionConfigList_azioni.get(groupNameGiaRegistrate);
					if(list.contains(azione)) {
						throw new CoreException("L'azione '"+azione+"' risulta già utilizzata nella configurazione relativa al gruppo '"+groupNameGiaRegistrate+"'");
					}
				}
			}
		}
		this.actionConfigList.put(groupName, config);
		this.actionConfigList_azioni.put(groupName, azioni);
	}
	
	public ConfigurazioneSelettoreCondizione getConfigurazioneSelettoreCondizioneByAzione(String azione) {
		if(!this.actionConfigList_azioni.isEmpty()) {
			for (String groupNameGiaRegistrate: this.actionConfigList_azioni.keySet()) {
				List<String> list = this.actionConfigList_azioni.get(groupNameGiaRegistrate);
				if(list.contains(azione)) {
					return this.actionConfigList.get(groupNameGiaRegistrate);
				}
			}
		}
		return null;
	}
	public ConfigurazioneSelettoreCondizione getConfigurazioneSelettoreCondizioneByGroupName(String groupName) {
		return this.actionConfigList.get(groupName);
	}
	public List<String> getAzioniByGroupName(String groupName) {
		return this.actionConfigList_azioni.get(groupName);
	}
	
	public String getGruppoByAzione(String azione) {
		if(!this.actionConfigList_azioni.isEmpty()) {
			for (String groupNameGiaRegistrate: this.actionConfigList_azioni.keySet()) {
				List<String> list = this.actionConfigList_azioni.get(groupNameGiaRegistrate);
				if(list.contains(azione)) {
					return groupNameGiaRegistrate;
				}
			}
		}
		return null;
	}
	public List<String> getGruppiConfigurazioneSelettoreCondizione(){
		List<String> l = new ArrayList<>();
		l.addAll(this.actionConfigList.keySet());
		return l;
	}
	
	public void removeAzioneConfigurazioneSelettoreCondizione(String azione){
		if(!this.actionConfigList_azioni.isEmpty()) {
			for (String groupNameGiaRegistrate: this.actionConfigList_azioni.keySet()) {
				List<String> list = this.actionConfigList_azioni.get(groupNameGiaRegistrate);
				if(list.contains(azione)) {
					for (int i = 0; i < list.size(); i++) {
						if(list.get(i).equals(azione)) {
							list.remove(i);
							break;
						}
					}
					if(list.isEmpty()) {
						this.actionConfigList.remove(groupNameGiaRegistrate);
						this.actionConfigList_azioni.remove(groupNameGiaRegistrate);
					}
				}
			}
		}
	}
	public void removeGruppoConfigurazioneSelettoreCondizione(String groupName){
		this.actionConfigList.remove(groupName);
		this.actionConfigList_azioni.remove(groupName);
	}
	
	public IdentificazioneFallitaConfigurazione getCondizioneNonIdentificata() {
		return this.condizioneNonIdentificata;
	}
	public void setCondizioneNonIdentificata(IdentificazioneFallitaConfigurazione condizioneNonIdentificata) {
		this.condizioneNonIdentificata = condizioneNonIdentificata;
	}
	public IdentificazioneFallitaConfigurazione getNessunConnettoreTrovato() {
		return this.nessunConnettoreTrovato;
	}
	public void setNessunConnettoreTrovato(IdentificazioneFallitaConfigurazione nessunConnettoreTrovato) {
		this.nessunConnettoreTrovato = nessunConnettoreTrovato;
	}
	
}
