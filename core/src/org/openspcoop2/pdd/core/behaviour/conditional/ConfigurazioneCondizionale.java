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
package org.openspcoop2.pdd.core.behaviour.conditional;

import java.util.Set;
import java.util.TreeMap;

import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.pdd.core.Utilities;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.utils.regexp.RegExpException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

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
	private TreeMap<String, ConfigurazioneSelettoreCondizioneRegola> regolaList = new TreeMap<String, ConfigurazioneSelettoreCondizioneRegola>();
	
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
	
	public void addRegola(ConfigurazioneSelettoreCondizioneRegola config) throws BehaviourException {
		// check che una regola non sia già registrata
		if(config.getRegola()==null) {
			throw new BehaviourException("Nome Regola non indicata");
		}
		if(!this.regolaList.isEmpty()) {
			for (String nomeRegola : this.regolaList.keySet()) {
				if(nomeRegola.equalsIgnoreCase(config.getRegola())) {
					throw new BehaviourException("Esiste già una regola con nome '"+nomeRegola+"'");
				}
			}
		}
		this.regolaList.put(config.getRegola(), config);
	}
	
	public ConfigurazioneSelettoreCondizioneRegola getRegolaByOperazione(String operazione, Resource restResource) throws RegExpException {
		if(!this.regolaList.isEmpty()) {
			for (String nomeRegola: getRegoleOrdinate()) {
				ConfigurazioneSelettoreCondizioneRegola config = this.regolaList.get(nomeRegola);
				
				boolean match = false;
				
				if(operazione.equals(config.getPatternOperazione())) {
					match=true;
				}
				
				if(!match && restResource!=null) {
					if(config.getPatternOperazione()!=null && !"".equals(config.getPatternOperazione())) {
						String [] parseResourceRest = Utilities.parseResourceRest(config.getPatternOperazione());
						if(parseResourceRest!=null) {
							match = Utilities.isRestResourceMatch(parseResourceRest, restResource);
						}
					}
				}
				
				if(!match) {
					boolean exprRegular = false;
					try {
						RegularExpressionEngine.validate(config.getPatternOperazione());
						exprRegular = true;
					}catch(Throwable e) {}
					if(exprRegular) {
						try {
							match = RegularExpressionEngine.isMatch(operazione, config.getPatternOperazione());
						}catch(RegExpNotFoundException notFound) {}
					}
				}
				
				if(match) {
					return config;
				}
			}
		}
		return null;
	}

	public String getNomeRegolaByOperazione(String operazione, Resource restResource) throws RegExpException {
		ConfigurazioneSelettoreCondizioneRegola c = this.getRegolaByOperazione(operazione, restResource);
		return c!=null ? c.getRegola() : null;
	}
	public ConfigurazioneSelettoreCondizioneRegola getRegola(String nomeRegola) {
		return this.regolaList.get(nomeRegola);
	}
	public Set<String> getRegoleOrdinate(){
		
		return this.regolaList.keySet();
	}
	public void removeRegola(String nomeRegola){
		this.regolaList.remove(nomeRegola);
	}
	public int sizeRegole() {
		return this.regolaList.size();
	}
	public TreeMap<String, ConfigurazioneSelettoreCondizioneRegola> getRegolaList() {
		return this.regolaList;
	}
	public void setRegolaList(TreeMap<String, ConfigurazioneSelettoreCondizioneRegola> regolaList) {
		this.regolaList = regolaList;
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
