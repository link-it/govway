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

package org.openspcoop2.core.monitor.rs.server.api.impl.utils;

import org.joda.time.DateTime;
import org.openspcoop2.core.monitor.rs.server.config.ServerProperties;
import org.openspcoop2.core.monitor.rs.server.config.SoggettiConfig;
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.FormatoReportEnum;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.ModalitaRicercaTransazioni;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.eventi.bean.EventiSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiExporter;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;

/**
 * BaseSearchForm
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SearchFormUtilities {

	private ServerProperties serverProperties;
	private ProtocolFactoryManager protocolFactoryManager;
	
	public SearchFormUtilities() throws Exception {
		this.serverProperties = ServerProperties.getInstance();
		this.protocolFactoryManager = ProtocolFactoryManager.getInstance();
	}
	
	public User getUtente(IContext context) {
		User user = new User();
		user.setInterfaceType(InterfaceType.STANDARD);
		user.setLogin(context.getAuthentication().getName());
		PermessiUtente permessi = new PermessiUtente();
		permessi.setDiagnostica(true);
		permessi.setReportistica(true);
		user.setPermessi(permessi);
		user.setPermitAllServizi(true);
		user.setPermitAllSoggetti(true);
		//user.setProtocolliSupportati(protocolFactoryManager.getProtocolNamesAsList());
		return user;
	}
	
	private void initBaseInfo(BaseSearchForm searchForm, IContext context, ProfiloEnum profilo, String soggetto, FiltroRicercaRuoloTransazioneEnum ruolo) throws Exception {
		this.initBaseInfo(searchForm, context, profilo, soggetto);
		if(ruolo!=null) {
			switch (ruolo) {
			case FRUIZIONE:
				searchForm.setTipologiaRicerca(TipologiaRicerca.uscita);
				break;
			case EROGAZIONE:
				searchForm.setTipologiaRicerca(TipologiaRicerca.ingresso);	
				break;
			case QUALSIASI:
				searchForm.setTipologiaRicerca(TipologiaRicerca.all);	
				break;
			}
		}
	}
	
	private void initBaseInfo(BaseSearchForm searchForm, IContext context, ProfiloEnum profilo, String soggetto) throws Exception {
		searchForm.setUser(this.getUtente(context));
		String protocollo = Converter.toProtocollo(profilo);
		searchForm.setProtocollo(protocollo);
		String tipoSoggettoLocale = this.protocolFactoryManager.getDefaultOrganizationTypes().get(protocollo);
		String nomeSoggettoLocale = soggetto;
		if(nomeSoggettoLocale==null) {
			if(this.serverProperties.useSoggettoDefault()) {
				nomeSoggettoLocale = this.serverProperties.getSoggettoDefaultIfEnabled(protocollo);
			}
		}
		if(nomeSoggettoLocale!=null) {
			if(!SoggettiConfig.existsIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto '"+nomeSoggettoLocale+"' indicato non esiste");
			}
			searchForm.setTipoNomeSoggettoLocale(tipoSoggettoLocale+"/"+nomeSoggettoLocale);
			searchForm.setSoggettoPddMonitor(tipoSoggettoLocale+"/"+nomeSoggettoLocale);
		}
		else {
			searchForm.setCheckSoggettoPddMonitor(false);
		}
		searchForm.saveProtocollo();
	}
	
	public HttpRequestWrapper getHttpRequestWrapper(IContext context, ProfiloEnum profilo, String soggetto, FiltroRicercaRuoloTransazioneEnum ruolo, 
			FormatoReportEnum formato, TipoReport tipo) throws Exception {
		HttpRequestWrapper request = new HttpRequestWrapper(context.getServletRequest());
		request.overrideParameter(CostantiExporter.TIPO_DISTRIBUZIONE, tipo.getValue());
		String protocollo = Converter.toProtocollo(profilo);
		request.overrideParameter(CostantiExporter.PROTOCOLLO,protocollo);
		String tipoSoggettoLocale = this.protocolFactoryManager.getDefaultOrganizationTypes().get(protocollo);
		String nomeSoggettoLocale = soggetto;
		if(nomeSoggettoLocale==null) {
			if(this.serverProperties.useSoggettoDefault()) {
				nomeSoggettoLocale = this.serverProperties.getSoggettoDefaultIfEnabled(protocollo);
			}
		}
		if(nomeSoggettoLocale!=null) {
			if(!SoggettiConfig.existsIdentificativoPorta(tipoSoggettoLocale, nomeSoggettoLocale)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto '"+nomeSoggettoLocale+"' indicato non esiste");
			}
			request.overrideParameter(CostantiExporter.SOGGETTO_LOCALE,tipoSoggettoLocale+"/"+nomeSoggettoLocale);
		}
		if(ruolo!=null) {
			switch (ruolo) {
			case FRUIZIONE:
				request.overrideParameter(CostantiExporter.TIPOLOGIA,CostantiExporter.TIPOLOGIA_FRUIZIONE);
				break;
			case EROGAZIONE:
				request.overrideParameter(CostantiExporter.TIPOLOGIA,CostantiExporter.TIPOLOGIA_EROGAZIONE);
				break;
			case QUALSIASI:
				request.overrideParameter(CostantiExporter.TIPOLOGIA,CostantiExporter.TIPOLOGIA_EROGAZIONE_FRUIZIONE);
				break;
			}
		}
		switch (formato) {
		case CSV:
			request.overrideParameter(CostantiExporter.TIPO_FORMATO, CostantiExporter.TIPO_FORMATO_CSV);
			break;
		case PDF:
			request.overrideParameter(CostantiExporter.TIPO_FORMATO, CostantiExporter.TIPO_FORMATO_PDF);
			break;
		case XLS:
			request.overrideParameter(CostantiExporter.TIPO_FORMATO, CostantiExporter.TIPO_FORMATO_XLS);
			break;
		case XML:
			request.overrideParameter(CostantiExporter.TIPO_FORMATO, CostantiExporter.TIPO_FORMATO_XML);
			break;
		case JSON:
			request.overrideParameter(CostantiExporter.TIPO_FORMATO, CostantiExporter.TIPO_FORMATO_JSON);
			break;
		default:
			break;
		}
		return request;
	}
	public HttpRequestWrapper getHttpRequestWrapper(IContext context, ProfiloEnum profilo, String soggetto, TransazioneRuoloEnum ruolo, 
			FormatoReportEnum formato, TipoReport tipo) throws Exception {
		FiltroRicercaRuoloTransazioneEnum ruoloNull = null;
		HttpRequestWrapper wrapper = getHttpRequestWrapper(context, profilo, soggetto, ruoloNull, formato, tipo);
		if(ruolo!=null) {
			switch (ruolo) {
			case FRUIZIONE:
				wrapper.overrideParameter(CostantiExporter.TIPOLOGIA,CostantiExporter.TIPOLOGIA_FRUIZIONE);
				break;
			case EROGAZIONE:
				wrapper.overrideParameter(CostantiExporter.TIPOLOGIA,CostantiExporter.TIPOLOGIA_EROGAZIONE);
				break;
			}
		}
		return wrapper;
	}
	
	public TransazioniSearchForm getAndamentoTemporaleSearchForm(IContext context, ProfiloEnum profilo, String soggetto, FiltroRicercaRuoloTransazioneEnum ruolo,
			DateTime dataInizio, DateTime dataFine) throws Exception {
		TransazioniSearchForm searchForm = new TransazioniSearchForm();
		initBaseInfo(searchForm, context, profilo, soggetto, ruolo);
		searchForm.setModalitaRicercaStorico(ModalitaRicercaTransazioni.ANDAMENTO_TEMPORALE.getValue());
		if (dataInizio != null && dataFine != null) {
			searchForm.setDataInizio(dataInizio.toDate());
			searchForm.setDataFine(dataFine.toDate());
		}
		return searchForm;
	}
	
	public TransazioniSearchForm getIdMessaggioSearchForm(IContext context, ProfiloEnum profilo, String soggetto) throws Exception {
		TransazioniSearchForm searchForm = new TransazioniSearchForm();
		initBaseInfo(searchForm, context, profilo, soggetto, null);
		searchForm.setModalitaRicercaStorico(ModalitaRicercaTransazioni.ID_MESSAGGIO.getValue());
		
		return searchForm;
	}
	
	public TransazioniSearchForm getIdApplicativoBaseSearchForm(IContext context, ProfiloEnum profilo, String soggetto) throws Exception {
		TransazioniSearchForm searchForm = new TransazioniSearchForm();
		initBaseInfo(searchForm, context, profilo, soggetto, null);
		searchForm.setModalitaRicercaStorico(ModalitaRicercaTransazioni.ID_APPLICATIVO_BASE.getValue());
		
		return searchForm;
	}
	
	
	public EventiSearchForm getEventiSearchForm(IContext context, DateTime dataInizio, DateTime dataFine) throws Exception {
		EventiSearchForm searchForm = new EventiSearchForm();
		searchForm.setDataInizio(dataInizio.toDate());
		searchForm.setDataFine(dataFine.toDate());
		return searchForm;
	}
	
	public TransazioniSearchForm getIdApplicativoSearchForm(IContext context, ProfiloEnum profilo, String soggetto, FiltroRicercaRuoloTransazioneEnum ruolo,
			DateTime dataInizio, DateTime dataFine) throws Exception {
		TransazioniSearchForm searchForm = new TransazioniSearchForm();
		initBaseInfo(searchForm, context, profilo, soggetto, ruolo);
		searchForm.setModalitaRicercaStorico(ModalitaRicercaTransazioni.ID_APPLICATIVO_AVANZATA.getValue());
		if (dataInizio != null && dataFine != null) {
			searchForm.setDataInizio(dataInizio.toDate());
			searchForm.setDataFine(dataFine.toDate());
		}
		return searchForm;
	}
	
	public ConfigurazioniGeneraliSearchForm getConfigurazioniGeneraliSearchForm(IContext context, ProfiloEnum profilo, String soggetto, TransazioneRuoloEnum ruolo) throws Exception {
		ConfigurazioniGeneraliSearchForm searchForm = new ConfigurazioniGeneraliSearchForm();
		initBaseInfo(searchForm, context, profilo, soggetto);
		if(ruolo!=null) {
			switch (ruolo) {
			case FRUIZIONE:
				searchForm.setTipologiaTransazioni(PddRuolo.DELEGATA);
				break;
			case EROGAZIONE:
				searchForm.setTipologiaTransazioni(PddRuolo.APPLICATIVA);
				break;
			}
		}
		return searchForm;
	}
}
