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
package org.openspcoop2.core.plugins.utils.handlers;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.ConfigurazioneHandler;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.utils.beans.BeanUtils;
import org.openspcoop2.utils.beans.BlackListElement;

/**
 * PluginsDriverUtils
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneHandlerBean extends ConfigurazioneHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Plugin plugin;
	
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}
	public Plugin getPlugin() {
		return this.plugin;
	}

	public ConfigurazioneHandlerBean() {
		super();
	}
	
	public ConfigurazioneHandlerBean(ConfigurazioneHandler handler, Plugin plugin){
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setPlugin",
				Plugin.class));
		
		BeanUtils.copy(this, handler, metodiEsclusi);
		
		this.plugin = plugin;
	}
	
	private static final int LIMIT_NOME = 80;
	private static final int LIMIT = 100;
	
	public String getNome(){
		return this.plugin != null ? this.plugin.getLabel() : null;
	}
	
	public String getNomeAbbr(){
		String tmp = this.getNome();
			
		if(tmp != null){
			if(tmp.length() >= LIMIT_NOME)
				return tmp.substring(0, (LIMIT_NOME-3))+"...";
		}
		
		return tmp;
	}
	
	public String getDescrizione(){
		return this.plugin != null ? this.plugin.getDescrizione() : null;
	}
	
	public String getDescrizioneAbbr(){
		String tmp = this.getDescrizione();
		
		if(tmp != null){
			if(tmp.length() >= LIMIT)
				return tmp.substring(0, (LIMIT-3))+"...";
		}
		
		return tmp;
	}
}
