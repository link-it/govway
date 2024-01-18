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
package org.openspcoop2.web.monitor.statistiche.mbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.statistiche.bean.AnalisiStatistica;
import org.openspcoop2.web.monitor.statistiche.bean.GruppoAnalisiStatistica;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePersonalizzateSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;

/**
 * AnalisiStatisticaBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class AnalisiStatisticaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BaseStatsMBean<?, ?, ?> mBean;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private List<SelectItem> tipiDistribuzione;
	private List<GruppoAnalisiStatistica> tipiAnalisiStatistica;

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

			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE, MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_KEY))); 
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI, MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_KEY)));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_ERRORI, MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_KEY)));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO, MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_KEY)));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE, MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_KEY)));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO, MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY)));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE, MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_KEY)));
			this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO, MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_APPLICATIVO_LABEL_KEY)));

			if(this.applicationBean.getShowStatistichePersonalizzate())
				this.tipiDistribuzione.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA,MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL_KEY)));
		}

		return this.tipiDistribuzione;
	}

	/*
 		La distribuzione per Soggetto Locale nelle statistiche deve sparire in uno dei seguenti casi:
		- è stato selezionato un soggetto
		- il profilo prevede solamente un soggetto
		- all'utente è stato associato solamente un soggetto
		- il multitenant non è abilitato (e quindi di conseguenza esiste solo un soggetto per profilo)
	 */
	public boolean disabilitaAnalisiStatisticaSoggettoLocale() {
		return !Utility.getLoginBean().isShowFiltroSoggettoLocale() || !Utility.isMultitenantAbilitato();
	}

	public List<GruppoAnalisiStatistica> getTipiAnalisiStatistica() {
		this.tipiAnalisiStatistica = new ArrayList<GruppoAnalisiStatistica>();

		GruppoAnalisiStatistica gruppoAndamentoTemporale = new GruppoAnalisiStatistica();
		gruppoAndamentoTemporale.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_KEY));
		List<AnalisiStatistica> listaAnalisiGruppoAndamentoTemporale = new ArrayList<>();
		listaAnalisiGruppoAndamentoTemporale.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE,CostantiGrafici.TIPO_REPORT_LINE_CHART,TipoReport.LINE_CHART));
		listaAnalisiGruppoAndamentoTemporale.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
		listaAnalisiGruppoAndamentoTemporale.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
		gruppoAndamentoTemporale.setListaAnalisiStatistica(listaAnalisiGruppoAndamentoTemporale);
		this.tipiAnalisiStatistica.add(gruppoAndamentoTemporale);

		GruppoAnalisiStatistica gruppoEsiti = new GruppoAnalisiStatistica();
		gruppoEsiti.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_KEY));
		List<AnalisiStatistica> listaAnalisiGruppoEsiti = new ArrayList<>();
		listaAnalisiGruppoEsiti.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
		listaAnalisiGruppoEsiti.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI,CostantiGrafici.TIPO_REPORT_LINE_CHART,TipoReport.LINE_CHART));
		listaAnalisiGruppoEsiti.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
		gruppoEsiti.setListaAnalisiStatistica(listaAnalisiGruppoEsiti);
		this.tipiAnalisiStatistica.add(gruppoEsiti);

		
		GruppoAnalisiStatistica gruppoErrori = new GruppoAnalisiStatistica();
		gruppoErrori.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_KEY));
		List<AnalisiStatistica> listaAnalisiGruppoErrori = new ArrayList<>();
		listaAnalisiGruppoErrori.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_ERRORI,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
		listaAnalisiGruppoErrori.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_ERRORI,CostantiGrafici.TIPO_REPORT_PIE_CHART,TipoReport.PIE_CHART));
		listaAnalisiGruppoErrori.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_ERRORI,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
		gruppoErrori.setListaAnalisiStatistica(listaAnalisiGruppoErrori);
		this.tipiAnalisiStatistica.add(gruppoErrori);

		GruppoAnalisiStatistica gruppoSoggettoRemoto = new GruppoAnalisiStatistica();
		gruppoSoggettoRemoto.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_KEY));
		List<AnalisiStatistica> listaAnalisiGruppoSoggettoRemoto = new ArrayList<>();
		listaAnalisiGruppoSoggettoRemoto.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
		listaAnalisiGruppoSoggettoRemoto.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO,CostantiGrafici.TIPO_REPORT_PIE_CHART,TipoReport.PIE_CHART));
		listaAnalisiGruppoSoggettoRemoto.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
		gruppoSoggettoRemoto.setListaAnalisiStatistica(listaAnalisiGruppoSoggettoRemoto);
		this.tipiAnalisiStatistica.add(gruppoSoggettoRemoto);

		if(!this.disabilitaAnalisiStatisticaSoggettoLocale()) {
			GruppoAnalisiStatistica gruppoSoggettoLocale = new GruppoAnalisiStatistica();
			gruppoSoggettoLocale.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_KEY));
			List<AnalisiStatistica> listaAnalisiGruppoSoggettoLocale = new ArrayList<>();
			listaAnalisiGruppoSoggettoLocale.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
			listaAnalisiGruppoSoggettoLocale.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE,CostantiGrafici.TIPO_REPORT_PIE_CHART,TipoReport.PIE_CHART));
			listaAnalisiGruppoSoggettoLocale.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
			gruppoSoggettoLocale.setListaAnalisiStatistica(listaAnalisiGruppoSoggettoLocale);
			this.tipiAnalisiStatistica.add(gruppoSoggettoLocale);
		}

		GruppoAnalisiStatistica gruppoServizio = new GruppoAnalisiStatistica();
		gruppoServizio.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY));
		List<AnalisiStatistica> listaAnalisiGrupposervizio = new ArrayList<>();
		listaAnalisiGrupposervizio.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
		listaAnalisiGrupposervizio.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO,CostantiGrafici.TIPO_REPORT_PIE_CHART,TipoReport.PIE_CHART));
		listaAnalisiGrupposervizio.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
		gruppoServizio.setListaAnalisiStatistica(listaAnalisiGrupposervizio);
		this.tipiAnalisiStatistica.add(gruppoServizio);

		GruppoAnalisiStatistica gruppoAzione = new GruppoAnalisiStatistica();
		gruppoAzione.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_KEY));
		List<AnalisiStatistica> listaAnalisiGruppoAzione = new ArrayList<>();
		listaAnalisiGruppoAzione.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
		listaAnalisiGruppoAzione.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE,CostantiGrafici.TIPO_REPORT_PIE_CHART,TipoReport.PIE_CHART));
		listaAnalisiGruppoAzione.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
		gruppoAzione.setListaAnalisiStatistica(listaAnalisiGruppoAzione);
		this.tipiAnalisiStatistica.add(gruppoAzione);

		GruppoAnalisiStatistica gruppoTokeniinfo = new GruppoAnalisiStatistica();
		gruppoTokeniinfo.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_LABEL_KEY));
		List<AnalisiStatistica> listaAnalisiGruppoTokeniInfo = new ArrayList<>();
		listaAnalisiGruppoTokeniInfo.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
		listaAnalisiGruppoTokeniInfo.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO,CostantiGrafici.TIPO_REPORT_PIE_CHART,TipoReport.PIE_CHART));
		listaAnalisiGruppoTokeniInfo.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
		gruppoTokeniinfo.setListaAnalisiStatistica(listaAnalisiGruppoTokeniInfo);
		this.tipiAnalisiStatistica.add(gruppoTokeniinfo);
		
		GruppoAnalisiStatistica gruppoApplicativo = new GruppoAnalisiStatistica();
		gruppoApplicativo.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_APPLICATIVO_LABEL_KEY));
		List<AnalisiStatistica> listaAnalisiGruppoApplicativo = new ArrayList<>();
		listaAnalisiGruppoApplicativo.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
		listaAnalisiGruppoApplicativo.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO,CostantiGrafici.TIPO_REPORT_PIE_CHART,TipoReport.PIE_CHART));
		listaAnalisiGruppoApplicativo.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
		gruppoApplicativo.setListaAnalisiStatistica(listaAnalisiGruppoApplicativo);
		this.tipiAnalisiStatistica.add(gruppoApplicativo);

		GruppoAnalisiStatistica gruppoIdentificativoAutenticato = new GruppoAnalisiStatistica();
		gruppoIdentificativoAutenticato.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY));
		List<AnalisiStatistica> listaAnalisiGruppoIdentificativoAutenticato = new ArrayList<>();
		listaAnalisiGruppoIdentificativoAutenticato.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
		listaAnalisiGruppoIdentificativoAutenticato.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO,CostantiGrafici.TIPO_REPORT_PIE_CHART,TipoReport.PIE_CHART));
		listaAnalisiGruppoIdentificativoAutenticato.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
		gruppoIdentificativoAutenticato.setListaAnalisiStatistica(listaAnalisiGruppoIdentificativoAutenticato);
		this.tipiAnalisiStatistica.add(gruppoIdentificativoAutenticato);
		
		GruppoAnalisiStatistica gruppoIndirizzoIP = new GruppoAnalisiStatistica();
		gruppoIndirizzoIP.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_INDIRIZZO_IP_LABEL_KEY));
		List<AnalisiStatistica> listaAnalisiGruppoIndirizzoIP = new ArrayList<>();
		listaAnalisiGruppoIndirizzoIP.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
		listaAnalisiGruppoIndirizzoIP.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP,CostantiGrafici.TIPO_REPORT_PIE_CHART,TipoReport.PIE_CHART));
		listaAnalisiGruppoIndirizzoIP.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO +"-" + org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
		gruppoIndirizzoIP.setListaAnalisiStatistica(listaAnalisiGruppoIndirizzoIP);
		this.tipiAnalisiStatistica.add(gruppoIndirizzoIP);

		if(this.applicationBean.getShowStatistichePersonalizzate()) {
			GruppoAnalisiStatistica gruppoPersonalizzate = new GruppoAnalisiStatistica();
			gruppoPersonalizzate.setLabel(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL_KEY));
			List<AnalisiStatistica> listaAnalisiGruppoPersonalizzate = new ArrayList<>();
			listaAnalisiGruppoPersonalizzate.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA,CostantiGrafici.TIPO_REPORT_BAR_CHART,TipoReport.BAR_CHART));
			listaAnalisiGruppoPersonalizzate.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA,CostantiGrafici.TIPO_REPORT_PIE_CHART,TipoReport.PIE_CHART));
			listaAnalisiGruppoPersonalizzate.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA,CostantiGrafici.TIPO_REPORT_ANDAMENTO_TEMPORALE,TipoReport.ANDAMENTO_TEMPORALE));
			listaAnalisiGruppoPersonalizzate.add(new AnalisiStatistica(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA,CostantiGrafici.TIPO_REPORT_TABELLA,TipoReport.TABELLA));
			gruppoPersonalizzate.setListaAnalisiStatistica(listaAnalisiGruppoPersonalizzate);
			this.tipiAnalisiStatistica.add(gruppoPersonalizzate);
		}

		return this.tipiAnalisiStatistica;

	}


	public BaseStatsMBean<?, ?, ?> getmBean() {
		return this.mBean;
	}


	public void setmBean(BaseStatsMBean<?, ?, ?> mBean) {
		this.mBean = mBean;
	}

	public void tipoDistribuzioneListener(ActionEvent ae) {
		if(this.tipoDistribuzione != null && this.mBean != null) {
			if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE)) {
				//this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_TEMPORALE, StatsSearchForm.class);
				((AndamentoTemporaleBean) this.mBean).initSearchListenerAndamentoTemporale(ae); 
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI)) {
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_ESITI, StatsSearchForm.class);
				((AndamentoTemporaleBean) this.mBean).initSearchListenerDistribuzionePerEsiti(ae); 
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_ERRORI)) {
				((DistribuzionePerErroriBean<?>) this.mBean).getSearch().initSearchListener(ae);
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_ERRORI, StatsSearchForm.class);
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
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_ERRORI)) {
				this.mBean = getBean(CostantiGrafici.MBEAN_DISTRIBUZIONE_ERRORI, DistribuzionePerErroriBean.class);
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_ERRORI, StatsSearchForm.class);
			}  else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO)) {
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
			} else if(this.tipoDistribuzione.startsWith(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO)) {
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

	public void setDistribuzione(String tipoDistribuzioneTmp) {
		if(this.tipoDistribuzione != null && this.mBean != null) {
			if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE)) {
				//this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_TEMPORALE, StatsSearchForm.class);
				((AndamentoTemporaleBean) this.mBean).initSearchListenerAndamentoTemporale(null); 
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI)) {
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_ESITI, StatsSearchForm.class);
				((AndamentoTemporaleBean) this.mBean).initSearchListenerDistribuzionePerEsiti(null); 
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_ERRORI)) {
				((DistribuzionePerErroriBean<?>) this.mBean).getSearch().initSearchListener(null);
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_ERRORI, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO)) {
				((DistribuzionePerSoggettoBean<?>) this.mBean).initSearchListenerRemoto(null);
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SOGGETTO_REMOTO, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE)) {
				((DistribuzionePerSoggettoBean<?>) this.mBean).initSearchListenerLocale(null); 
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SOGGETTO_LOCALE, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO)) {
				((DistribuzionePerServizioBean<?>) this.mBean).getSearch().initSearchListener(null); 
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SERVIZIO, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE)) {
				((DistribuzionePerAzioneBean<?>) this.mBean).getSearch().initSearchListener(null);
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_AZIONE, StatsSearchForm.class);
			} else if(this.tipoDistribuzione.startsWith(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO)) {
				((DistribuzionePerSABean<?>) this.mBean).getSearch().initSearchListener(null);
				String[] mittenteType = this.tipoDistribuzione.split("-");
				this.mBean.getSearch().setRiconoscimento(mittenteType[1]); 
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_SERVIZIO_APPLICATIVO, StatsSearchForm.class);
			} else if(this.applicationBean.getShowStatistichePersonalizzate() && this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA)) {
				((StatsPersonalizzateBean) this.mBean).getSearch().initSearchListener(null);
				//				this.search = getBean(CostantiGrafici.SEARCH_DISTRIBUZIONE_PERSONALIZZATA, StatsSearchForm.class);
			} else {
				//				this.mBean = null;
				//				this.search = null;
			}
		}
	}

	public void setTipoReport(String tipoReport) {
		if(tipoReport != null && this.mBean != null) {
			if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA)) {
				((StatistichePersonalizzateSearchForm)this.mBean.getSearch()).set_value_tipoReport(tipoReport);
			}else {
				((StatsSearchForm)this.mBean.getSearch()).set_value_tipoReport(tipoReport);	
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
			String tipoReport = null;
			if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA)) {
				tipoReport =((StatistichePersonalizzateSearchForm)this.mBean.getSearch()).get_value_tipoReport();
			}else {
				tipoReport = ((StatsSearchForm)this.mBean.getSearch()).get_value_tipoReport();	
			}
			String ripulisciOutcome = this.mBean.getSearch().ripulisci();

			if(this.tipoDistribuzione.startsWith(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO)) {
				String[] mittenteType = this.tipoDistribuzione.split("-");
				this.mBean.getSearch().setRiconoscimento(mittenteType[1]); 
			}

			if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA)) {
				((StatistichePersonalizzateSearchForm)this.mBean.getSearch()).set_value_tipoReport(tipoReport);
			}else {
				((StatsSearchForm)this.mBean.getSearch()).set_value_tipoReport(tipoReport);	
			}

			return ripulisciOutcome;
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

	public String getLabelFiltriRicerca() {
		if(this.tipoDistribuzione != null) {
			if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_KEY);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_KEY);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_ERRORI)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_KEY);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_KEY);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_KEY);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY);
			} else if(this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_KEY);
			} else if(this.tipoDistribuzione.startsWith(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO)) {
				String[] mittenteType = this.tipoDistribuzione.split("-");
				if(mittenteType[1].equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO)) {
					return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_APPLICATIVO_LABEL_KEY);
				} else if(mittenteType[1].equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO)) {
					return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY);
				} else if(mittenteType[1].equals(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP)) {
					return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_INDIRIZZO_IP_LABEL_KEY);
				} else {
					return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_LABEL_KEY);
				}
			} else if(this.applicationBean.getShowStatistichePersonalizzate() && this.tipoDistribuzione.equals(CostantiGrafici.TIPO_DISTRIBUZIONE_PERSONALIZZATA)) {
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL_KEY);
			}  
		}
		return "Filtri Ricerca";
	}
}
