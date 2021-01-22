package org.openspcoop2.monitor.engine.config.base.utils.handlers;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.ConfigurazioneHandler;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.utils.beans.BeanUtils;
import org.openspcoop2.utils.beans.BlackListElement;

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
