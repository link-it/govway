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
package org.openspcoop2.web.monitor.eventi.mbean;

import org.openspcoop2.core.eventi.constants.TipoSeverita;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.PdDBaseBean;
import org.openspcoop2.web.monitor.eventi.bean.EventiSearchForm;
import org.openspcoop2.web.monitor.eventi.bean.EventoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;

/****
 * 
 * 
 * Bean web per la sezione degli eventi
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class EventiBean extends PdDBaseBean<EventoBean, Long, IService<EventoBean,Long>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();


	private transient EventiSearchForm search;

	private EventoBean evento;

	private List<SelectItem> tipiSeverita = null;

	private String eventoAction = "add";
	
	private boolean visualizzaIdCluster = false;
	private boolean visualizzaIdClusterAsSelectList = false;
	private List<String> listIdCluster;
	private List<String> listLabelIdCluster;
	
	private boolean visualizzaCanali = false;
	private List<String> listCanali;
	private Map<String,List<String>> mapCanaleToNodi;
	
	public EventiBean(){

		// inizializzazione
		try {
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(EventiBean.log);
			List<String> govwayMonitorare = govwayMonitorProperties.getListaPdDMonitorate_StatusPdD();
			this.setVisualizzaIdCluster(govwayMonitorare!=null && govwayMonitorare.size()>1);
			this.visualizzaIdClusterAsSelectList = govwayMonitorProperties.isAttivoTransazioniUtilizzoSondaPdDListAsClusterId();
			if(govwayMonitorare!=null && govwayMonitorare.size()>1){
				this.listIdCluster = new ArrayList<>();
				this.listIdCluster.add("--");
				this.listIdCluster.addAll(govwayMonitorare);
				
				this.listLabelIdCluster = new ArrayList<>();
				ConfigurazioneNodiRuntime config = govwayMonitorProperties.getConfigurazioneNodiRuntime();
				for (String nodoRun : this.listIdCluster) {
					String descrizione = null;
					if(config.containsNode(nodoRun)) {
						descrizione = config.getDescrizione(nodoRun);
					}
					this.listLabelIdCluster.add(descrizione!=null ? descrizione : nodoRun);
				}
			}
			
			
			this.visualizzaCanali = Utility.isCanaliAbilitato();
			if(this.visualizzaCanali) {
				List<String> canali = Utility.getCanali();
				this.listCanali = new ArrayList<>();
				this.listCanali.add("--");
				if(canali!=null && !canali.isEmpty()) {
					this.listCanali.addAll(canali);
					this.mapCanaleToNodi = new HashMap<>();
					for (String canale : canali) {
						List<String> nodi = Utility.getNodi(canale);
						if(nodi==null) {
							nodi = new ArrayList<>();
						}
						this.mapCanaleToNodi.put(canale, nodi);
					}
				}
			}
			
		} catch (Exception e) {
			EventiBean.log
			.error("Errore durante l'inizializzazione EventiBean.",
					e);
		}
	}



	public EventoBean getEvento() {
		if (this.evento == null){
			this.evento = new EventoBean();
		}
		return this.evento;
	}

	public void setEvento(EventoBean evento) {
		this.evento = evento;
	}

	public void setSearch(EventiSearchForm search) {
		this.search = search;
	}

	public EventiSearchForm getSearch() {
		return this.search;
	}

	public List<SelectItem> getTipiSeverita(){
		if(this.tipiSeverita!= null)
			return this.tipiSeverita;

		this.tipiSeverita = new ArrayList<SelectItem>();

		this.tipiSeverita.add(new SelectItem(EventiSearchForm.NON_SELEZIONATO));
		this.tipiSeverita.add(new SelectItem(TipoSeverita.FATAL.getValue()));
		this.tipiSeverita.add(new SelectItem(TipoSeverita.ERROR.getValue()));
		this.tipiSeverita.add(new SelectItem(TipoSeverita.WARN.getValue()));
		this.tipiSeverita.add(new SelectItem(TipoSeverita.INFO.getValue()));
		this.tipiSeverita.add(new SelectItem(TipoSeverita.DEBUG.getValue()));

		return this.tipiSeverita;
	}

	/**
	 * Listener eseguito prima di aggiungere una nuova sonda
	 * 
	 * @param ae
	 */
	@Override
	public void addNewListener(ActionEvent ae) {
		this.evento = null;
	}


	@Override
	public IService<EventoBean, Long> getService(){
		return this.service;
	}


	public String getEventoAction() {
		return this.eventoAction;
	}

	public void setEventoAction(String eventoAction) {
		this.eventoAction = eventoAction;

	}
	
	public boolean isVisualizzaIdCluster() {
		return this.visualizzaIdCluster;
	}

	public void setVisualizzaIdCluster(boolean visualizzaIdCluster) {
		this.visualizzaIdCluster = visualizzaIdCluster;
	}
	
	public boolean isVisualizzaIdClusterAsSelectList() {
		return this.visualizzaIdClusterAsSelectList;
	}

	public void setVisualizzaIdClusterAsSelectList(boolean visualizzaIdClusterAsSelectList) {
		this.visualizzaIdClusterAsSelectList = visualizzaIdClusterAsSelectList;
	}
	
	public List<SelectItem> getListIdCluster() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();
		if(this.listIdCluster!=null && this.listIdCluster.size()>0){
			for (int i = 0; i < this.listIdCluster.size(); i++) {
				String id = this.listIdCluster.get(i);
				if("--".equals(id)) {
					list.add(new SelectItem(id));
					continue;
				}
				String label = this.listLabelIdCluster.get(i);
				if(this.search!=null && this.search.getCanale()!=null && !"".equals(this.search.getCanale()) && !"--".equals(this.search.getCanale()) &&
						this.visualizzaCanali && this.mapCanaleToNodi!=null) {
					List<String> nodi = this.mapCanaleToNodi.get(this.search.getCanale());
					if(nodi==null || !nodi.contains(id)) {
						continue;
					}
				}
				list.add(new SelectItem(id,label));
			}
		}
		return list;
	}

	public boolean isVisualizzaCanali() {
		return this.visualizzaCanali;
	}

	public void setVisualizzaCanali(boolean visualizzaCanali) {
		this.visualizzaCanali = visualizzaCanali;
	}
	
	public List<SelectItem> getListCanali() {
		ArrayList<SelectItem> list = new ArrayList<SelectItem>();
		if(this.listCanali!=null && this.listCanali.size()>0){
			for (String id : this.listCanali) {
				list.add(new SelectItem(id));
			}
		}
		return list;
	}
	
	public List<String> getIdClusterByCanale(String canale){
		return this.mapCanaleToNodi.get(canale);
	}
}
