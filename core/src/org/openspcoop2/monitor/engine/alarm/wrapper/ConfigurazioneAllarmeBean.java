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

package org.openspcoop2.monitor.engine.alarm.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.plugins.IAlarmProcessing;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.constants.TipoAllarme;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.beans.BeanUtils;
import org.openspcoop2.utils.beans.BlackListElement;
import org.slf4j.Logger;

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
		metodiEsclusi.add(new BlackListElement("setExistsAlmostOneManuallyUpdateState",
				boolean.class));
		
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
	
	public boolean isModalitaAttiva() {
		boolean attivo = TipoAllarme.ATTIVO.equals(this.getTipoAllarme());
		return attivo;
	}
	
	private Boolean manuallyUpdateState = null;
	public boolean isManuallyUpdateState() {
		if(this.manuallyUpdateState == null) {
			Logger log = LoggerWrapperFactory.getLogger(ConfigurazioneAllarmeBean.class);
			try {
				IDynamicLoader dl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, this.plugin.getTipo(), this.plugin.getClassName(), log);
				IAlarmProcessing alarm = (IAlarmProcessing) dl.newInstance();
				this.manuallyUpdateState = alarm.isManuallyUpdateState();
			}catch(Throwable t) {
				log.error(t.getMessage(),t);
				this.manuallyUpdateState = false;
			}
		}
		return this.manuallyUpdateState;
	}
	
	// Informazione necessaria per la visualizzazione nella lista della console di monitoraggio
	private boolean existsAlmostOneManuallyUpdateState = true;
	public void setExistsAlmostOneManuallyUpdateState(boolean existsAlmostOneManuallyUpdateState) {
		this.existsAlmostOneManuallyUpdateState = existsAlmostOneManuallyUpdateState;
	}
	public boolean isExistsAlmostOneManuallyUpdateState() {
		return this.existsAlmostOneManuallyUpdateState;
	}
	
 }
