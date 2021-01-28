/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.monitor.engine.alarm.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.utils.beans.BeanUtils;
import org.openspcoop2.utils.beans.BlackListElement;

/**
 * ConfigurazioneAllarmeBean 
 *
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneAllarmeBean extends Allarme{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Plugin plugin;
	private String dettaglioAPI = null;
	private String dettaglioFruitore = null;
	private String dettaglioErogatore = null;
	
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}
	public Plugin getPlugin() {
		return this.plugin;
	}

	public ConfigurazioneAllarmeBean() {
		super();
	}
	
	public ConfigurazioneAllarmeBean(Allarme allarme, Plugin plugin){
		List<BlackListElement> metodiEsclusi = new ArrayList<BlackListElement>(
				0);
		metodiEsclusi.add(new BlackListElement("setPlugin",
				Plugin.class));
		metodiEsclusi.add(new BlackListElement("setDettaglioAPI",
				String.class));
		metodiEsclusi.add(new BlackListElement("setDettaglioFruitore",
				String.class));
		metodiEsclusi.add(new BlackListElement("setDettaglioErogatore",
				String.class));
		
		BeanUtils.copy(this, allarme, metodiEsclusi);
		
		this.plugin = plugin;
	}
	
	private static final int LIMIT_NOME = 80;
	private static final int LIMIT = 100;
	
	public String getNomeAbbr(){
		String tmp = this.getNome();
			
		if(tmp != null){
			if(tmp.length() >= LIMIT_NOME)
				return tmp.substring(0, (LIMIT_NOME-3))+"...";
		}
		
		return tmp;
	}
	
	public String getDettaglioStatoAbbr(){
		String tmp = this.getDettaglioStato();
		
		if(tmp != null){
			if(tmp.length() >= LIMIT)
				return tmp.substring(0, (LIMIT-3))+"...";
		}
		
		return tmp;
	}
	
	public String getDettaglioStatoHtmlEscaped(){
		String tmp = this.getDettaglioStato();
		
		if(tmp != null){
			while(tmp.contains("\n")) {
				tmp = tmp.replace("\n", "<BR/>");
			}
		}
		
		return tmp;
	}
	
	public String getDescrizioneAbbr(){
		String tmp = this.getDescrizione();
		
		if(tmp != null){
			if(tmp.length() >= LIMIT)
				return tmp.substring(0, (LIMIT-3))+"...";
		}
		
		return tmp;
	}
	
	public String getDettaglioAPI() {
		if(this.dettaglioAPI == null) {
			this.dettaglioAPI = "-";
		}
		return this.dettaglioAPI;
	}
	
	public void setDettaglioAPI(String dettaglioAPI) {
		this.dettaglioAPI = dettaglioAPI;
	}
	
	public boolean isRuoloPortaApplicativa() {
		boolean applicativa = false;
		
		if(this.getFiltro() != null && this.getFiltro().getRuoloPorta() != null) {
			if(RuoloPorta.APPLICATIVA.equals(this.getFiltro().getRuoloPorta())) {
				applicativa = (this.getFiltro().getNomePorta()!=null);
			}
		}
		
		return applicativa;
	}
	
	public boolean isRuoloPortaDelegata() {
		boolean delegata = false;
		
		if(this.getFiltro() != null && this.getFiltro().getRuoloPorta() != null) {
			if(RuoloPorta.DELEGATA.equals(this.getFiltro().getRuoloPorta())) {
				delegata = (this.getFiltro().getNomePorta()!=null);
			}
		}
		
		return delegata;
	}
	
	public boolean isAllarmeConfigurazione() {
		return !this.isRuoloPortaDelegata() && !this.isRuoloPortaApplicativa();
	}
	public String getDettaglioFruitore() {
		return this.dettaglioFruitore;
	}
	public void setDettaglioFruitore(String dettaglioFruitore) {
		this.dettaglioFruitore = dettaglioFruitore;
	}
	public String getDettaglioErogatore() {
		return this.dettaglioErogatore;
	}
	public void setDettaglioErogatore(String dettaglioErogatore) {
		this.dettaglioErogatore = dettaglioErogatore;
	}
	
//	@Override
//	public String getTipo() {
//		if(this.plugin.getClassName()!=null){
//			return _getTipo(this.plugin);
//		}
//		return "Non Definito";
//	}
//
//	private static Hashtable<String, String> mapClassNameToTipo = new Hashtable<String, String>();
//	public static String _getTipo(Plugin plugin) {
//		String pluginClassName = plugin.getClassName();
//		if(mapClassNameToTipo.containsKey(pluginClassName)==false) {
//			_initTipo(plugin);
//		}
//		return mapClassNameToTipo.get(pluginClassName);
//	}
//	private static synchronized void _initTipo(Plugin plugin) {
//		String pluginClassName = plugin.getClassName();
//		if(mapClassNameToTipo.containsKey(pluginClassName)==false) {
//			String tipo = _getTipoFromInstance(plugin);
//			mapClassNameToTipo.put(pluginClassName, tipo);
//		}
//	}
//	private static String _getTipoFromInstance(Plugin plugin) {
//		Logger log = LoggerWrapperFactory.getLogger(ConfigurazioneAllarmeBean.class);
//		try {
//			IDynamicLoader dl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, plugin.getTipo(),plugin.getClassName(), log);
//			IAlarmProcessing alarm = (IAlarmProcessing) dl.newInstance();
//			switch (alarm.getAlarmType()) {
//			case ACTIVE:
//				return "Attivo";
//			case PASSIVE:
//				return "Passivo";
//			}
//		}catch(Exception e) {
//			log.error(e.getMessage(),e);
//		}
//		return "Non Definito";
//	}
 }
