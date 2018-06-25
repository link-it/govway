/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.web.monitor.statistiche.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;

public class AnalisiStatisticaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BaseStatsMBean<?, ?, ?> mBean;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private List<SelectItem> tipiDistribuzione;

	private String tipoDistribuzione = CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE;

	private transient ApplicationBean applicationBean = null;

	public AnalisiStatisticaBean (){
		try{
			this.applicationBean = new ApplicationBean();
			this.applicationBean.setLoginBean(Utility.getLoginBean()); 
		}catch(Exception e){
			log.error("Errore durante la init di AnalisiStatisticaBean: "+ e.getMessage() ,e);
		}
	}


	public void menuActionListener(ActionEvent ae) {
		this.setTipoDistribuzione(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE); 
		this.tipoDistribuzioneListener(ae); 
	}


	public List<SelectItem> getTipiDistribuzione() {
		if(this.tipiDistribuzione == null){
			this.tipiDistribuzione = new ArrayList<SelectItem>();

			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE,CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE_LABEL));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI,CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI_LABEL));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO,CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE,CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO,CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_LABEL));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE,CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE_LABEL));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO,CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO_LABEL));
		
			if(this.applicationBean.getShowStatistichePersonalizzate())
				this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA,CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL));
		}

		return this.tipiDistribuzione;
	}


	public BaseStatsMBean<?, ?, ?> getmBean() {
		return this.mBean;
	}


	public void setmBean(BaseStatsMBean<?, ?, ?> mBean) {
		this.mBean = mBean;
	}


//	public StatsSearchForm getSearch() {
//		return this.search;
//	}
//
//
//	public void setSearch(StatsSearchForm search) {
//		this.search = search;
//	}


	public void tipoDistribuzioneListener(ActionEvent ae) {
		if(this.tipoDistribuzione != null && this.mBean != null) {
			if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE)) {
				//this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_TEMPORALE, StatsSearchForm.class);
				((AndamentoTemporaleBean) this.mBean).initSearchListenerAndamentoTemporale(ae); 
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI)) {
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_ESITI, StatsSearchForm.class);
				((AndamentoTemporaleBean) this.mBean).initSearchListenerDistribuzionePerEsiti(ae); 
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO)) {
				((DistribuzionePerSoggettoBean<?>) this.mBean).initSearchListenerRemoto(ae);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SOGGETTO_REMOTO, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE)) {
				((DistribuzionePerSoggettoBean<?>) this.mBean).initSearchListenerLocale(ae); 
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SOGGETTO_LOCALE, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO)) {
				((DistribuzionePerServizioBean<?>) this.mBean).getSearch().initSearchListener(ae); 
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SERVIZIO, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE)) {
				((DistribuzionePerAzioneBean<?>) this.mBean).getSearch().initSearchListener(ae);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_AZIONE, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO)) {
				((DistribuzionePerSABean<?>) this.mBean).getSearch().initSearchListener(ae);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SERVIZIO_APPLICATIVO, StatsSearchForm.class);
			} else if(this.applicationBean.getShowStatistichePersonalizzate() && this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA)) {
				((StatsPersonalizzateBean) this.mBean).getSearch().initSearchListener(ae);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_PERSONALIZZATA, StatsSearchForm.class);
			} else {
//				this.mBean = null;
//				this.search = null;
			}
		}
	}

	public String getTipoDistribuzione() {
		return this.tipoDistribuzione;
	}

	public void setTipoDistribuzione(String tipoDistribuzione) {
		this.tipoDistribuzione = tipoDistribuzione;

		if(this.tipoDistribuzione != null) {
			if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE)) {
				this.mBean = getBean(CostantiGrafici.MBEAN_DISTRIBUZIONE_TEMPORALE, AndamentoTemporaleBean.class);
				//this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_TEMPORALE, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI)) {
				this.mBean = getBean(CostantiGrafici.MBEAN_DISTRIBUZIONE_ESITI, AndamentoTemporaleBean.class);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_ESITI, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO)) {
				this.mBean = getBean(CostantiGrafici.MBEAN_DISTRIBUZIONE_SOGGETTO_REMOTO, DistribuzionePerSoggettoBean.class);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SOGGETTO_REMOTO, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE)) {
				this.mBean = getBean(CostantiGrafici.MBEAN_DISTRIBUZIONE_SOGGETTO_LOCALE, DistribuzionePerSoggettoBean.class);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SOGGETTO_LOCALE, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO)) {
				this.mBean = getBean(CostantiGrafici.MBEAN_DISTRIBUZIONE_SERVIZIO, DistribuzionePerServizioBean.class);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SERVIZIO, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE)) {
				this.mBean = getBean(CostantiGrafici.MBEAN_DISTRIBUZIONE_AZIONE, DistribuzionePerAzioneBean.class);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_AZIONE, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO)) {
				this.mBean = getBean(CostantiGrafici.MBEAN_DISTRIBUZIONE_SERVIZIO_APPLICATIVO, DistribuzionePerSABean.class);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SERVIZIO_APPLICATIVO, StatsSearchForm.class);
			} else if(this.applicationBean.getShowStatistichePersonalizzate() && this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA)) {
				this.mBean = getBean(CostantiGrafici.MBEAN_DISTRIBUZIONE_PERSONALIZZATA, StatsPersonalizzateBean.class);
//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_PERSONALIZZATA, StatsSearchForm.class);
			} else {
				this.mBean = null;
//				this.search = null;
			}
		}
	}
	
	public String submit(){
		if(this.mBean != null){
			return this.mBean.submit();
		}
		
		return null;
	}
	
	public void setTipiDistribuzione(List<SelectItem> listaTipiDistribuzione) {
		this.tipiDistribuzione = listaTipiDistribuzione;
	}

	public String ripulisci(){
		if(this.mBean != null && this.mBean.getSearch() != null){
			return this.mBean.getSearch().ripulisci();
		}
		
		return null;
	}
	
	public String filtra(){
		if(this.mBean != null && this.mBean.getSearch() != null){
			return this.mBean.getSearch().filtra();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> T getBean(String beanName, Class<T> beanClass) {
		T toRet = null;
		try{
			FacesContext context = FacesContext.getCurrentInstance();
			 toRet = (T) context.getApplication().getExpressionFactory()
			            .createValueExpression(context.getELContext(), "#{"+beanName+"}", beanClass)
			              .getValue(context.getELContext());
		}catch(Exception e){
			log.error("Errore durante la lettura del bean ["+beanName+"]: "+e.getMessage() ,e);
		}
		return toRet;
	}


	public StatsSearchForm getSearch() {
		if(this.mBean  == null && this.tipoDistribuzione != null)
			this.setTipoDistribuzione(this.tipoDistribuzione); 
		
		if(this.mBean != null && this.mBean.getSearch() != null)
			return (StatsSearchForm) this.mBean.getSearch();
		
		return null;
	}


	public void setSearch(StatsSearchForm search) {
	}
	
	
}
