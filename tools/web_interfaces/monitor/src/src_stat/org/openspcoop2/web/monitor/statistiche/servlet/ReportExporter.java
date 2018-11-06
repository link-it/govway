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
package org.openspcoop2.web.monitor.statistiche.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoStatistica;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpServletCredential;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.dao.DBLoginDAO;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePersonalizzateSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiExporter;
import org.openspcoop2.web.monitor.statistiche.dao.ConfigurazioniGeneraliService;
import org.openspcoop2.web.monitor.statistiche.mbean.AndamentoTemporaleBean;
import org.openspcoop2.web.monitor.statistiche.mbean.BaseStatsMBean;
import org.openspcoop2.web.monitor.statistiche.mbean.DistribuzionePerAzioneBean;
import org.openspcoop2.web.monitor.statistiche.mbean.DistribuzionePerSABean;
import org.openspcoop2.web.monitor.statistiche.mbean.DistribuzionePerServizioBean;
import org.openspcoop2.web.monitor.statistiche.mbean.DistribuzionePerSoggettoBean;
import org.openspcoop2.web.monitor.statistiche.mbean.StatsPersonalizzateBean;
import org.slf4j.Logger;

/**
 * ReportExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ReportExporter extends HttpServlet{

	private static final long serialVersionUID = 1272767433184676700L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private boolean serviceEnabled = false;

	@Override
	public void init() throws ServletException {
		try{
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(ReportExporter.log);
			this.serviceEnabled = govwayMonitorProperties.isStatisticheAttivoServizioEsportazioneReport();
		}catch(Exception e){
			ReportExporter.log.warn("Inizializzazione servlet fallita, setto enableHeaderInfo=false",e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.processRequest(req,resp);		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		this.processRequest(req,resp);		
	}


	private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
		
		StringBuffer bfSource = new StringBuffer("");
		try{

			if(this.serviceEnabled==false){
				resp.sendError(CostantiExporter.ERRORE_SERVER, "Servizio non attivo");
				return;
			}
			
			bfSource.append("remoteAddress: "+req.getRemoteAddr());
			String headerAddress = HttpUtilities.getClientAddress(req);
			if(headerAddress!=null){
				bfSource.append(", X-Forwarded-For: "+headerAddress);
			}
			
			// Lettura utenza
			HttpServletCredential identity = new HttpServletCredential(req, log);
			String username = identity.getUsername();
			String password = identity.getPassword();
			String principal = identity.getPrincipal();
			if(username==null && principal==null){
				username = req.getParameter(CostantiExporter.USER);
				password = req.getParameter(CostantiExporter.PASSWORD);
			}
			if(username==null && password==null){
				log.error("Credenziali non fornita, mittente: "+bfSource.toString());
				resp.sendError(CostantiExporter.CREDENZIALI_NON_FORNITE, "Autenticazione richiesta");
				return;
			}
			
			DBLoginDAO loginService = new DBLoginDAO();
			UserDetailsBean user = null;
			if(principal!=null){
				try{
					user = loginService.loadUserByUsername(principal);
				}catch(NotFoundException notFound){
					log.error("[mittente: "+bfSource.toString()+"] Autenticazione fallita con il principal ottenuto ["+principal+"]: "+notFound.getMessage(),notFound);
					resp.sendError(CostantiExporter.AUTENTICAZIONE_FALLITA, "Autenticazione fallita");
					return;
				}
			}
			else{
				boolean check = loginService.login(username, password);
				if(check==false){
					log.error("[mittente: "+bfSource.toString()+"] Autenticazione fallita con le credenziali fornite user["+username+"] password["+password+"]");
					resp.sendError(CostantiExporter.AUTENTICAZIONE_FALLITA, "Autenticazione fallita");
					return;
				}
				else{
					try{
						user = loginService.loadUserByUsername(username);
					}catch(NotFoundException notFound){
						log.error("[mittente: "+bfSource.toString()+"] Autenticazione fallita con lo username ["+username+"]: "+notFound.getMessage(),notFound);
						resp.sendError(CostantiExporter.AUTENTICAZIONE_FALLITA, "Autenticazione fallita");
						return;
					}
				}
			}
			
			
			String r = getServletConfig().getInitParameter("resource");
			if("config".equals(r)){
				this.getConfigurazione(req, resp, user);
				ReportExporter.log.debug("[mittente: "+bfSource.toString()+"] richiesta report configurazione completata");
			}
			else if("stat".equals(r)){
				this.getStatistiche(req, resp, user);
				ReportExporter.log.debug("[mittente: "+bfSource.toString()+"] richiesta report statistica completata");
			}
			else{
				throw new Exception("InitParameter 'resource' with unknown resource ["+r+"]");
			}
			
		}
		catch(ParameterUncorrectException e){
			ReportExporter.log.error("[mittente: "+bfSource.toString()+"] "+e.getMessage(),e);
			//throw new ServletException(e.getMessage(),e);
			resp.sendError(CostantiExporter.DATI_NON_CORRETTI, e.getMessage());
		}
		catch(NotFoundException e){
			ReportExporter.log.error("[mittente: "+bfSource.toString()+"] "+e.getMessage(),e);
			//throw new ServletException(e.getMessage(),e);
			resp.sendError(CostantiExporter.DATI_NON_TROVATI, e.getMessage());
		}
		catch(Throwable e){
			ReportExporter.log.error("[mittente: "+bfSource.toString()+"] "+e.getMessage(),e);
			//throw new ServletException(e.getMessage(),e);
			resp.sendError(CostantiExporter.ERRORE_SERVER, e.getMessage());
		}
			
	}
	
	private void getConfigurazione(HttpServletRequest req, HttpServletResponse resp, UserDetailsBean user) throws Exception{
		
		try{	
			ConfigurazioniGeneraliSearchForm searchForm = new ConfigurazioniGeneraliSearchForm();
			searchForm.setUser(user.getUtente());
			
			ConfigurazioniGeneraliService service = new ConfigurazioniGeneraliService();
			service.setSearch(searchForm);
			
			searchForm.setTipologiaRicerca(CostantiExporter.RICERCA_INGRESSO); 
			
			// ** Soggetto / Servizio **
			
			String protocollo = req.getParameter(CostantiExporter.PROTOCOLLO);
			if(protocollo!=null){
				protocollo = protocollo.trim();
				try{
					ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
				}catch(Exception e){
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.PROTOCOLLO+"' fornito possiede un valore '"+protocollo
							+"' sconosciuto. I tipi supportati sono: "+ProtocolFactoryManager.getInstance().getProtocolNames());
				}
				searchForm.setProtocollo(protocollo);
			}
			
			String soggettoLocale = req.getParameter(CostantiExporter.SOGGETTO_LOCALE);
			if(soggettoLocale!=null){
				soggettoLocale = soggettoLocale.trim();
				searchForm.setTipoNomeSoggettoLocale(soggettoLocale);
			}
			
			String servizio = req.getParameter(CostantiExporter.SERVIZIO);
			if(servizio!=null){
				servizio = servizio.trim();
				searchForm.setNomeServizio(servizio);
			}
			
			// ** Tipologia Transazione **
			
			String tipologiaTransazione = req.getParameter(CostantiExporter.TIPOLOGIA);
			if(tipologiaTransazione!=null){
				tipologiaTransazione = tipologiaTransazione.trim();
				List<String> l = new ArrayList<String>();
				l.add(CostantiExporter.TIPOLOGIA_EROGAZIONE);
				l.add(CostantiExporter.TIPOLOGIA_FRUIZIONE);
				if(l.contains(tipologiaTransazione) == false){
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPOLOGIA+"' fornito possiede un valore '"+tipologiaTransazione
							+"' sconosciuto. I tipi supportati sono: "+l);
				}
			}
			else{
				throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.TIPOLOGIA+"' non fornito");
			}
			PddRuolo pddRuolo = null;
			if(CostantiExporter.TIPOLOGIA_EROGAZIONE.equals(tipologiaTransazione)){
				pddRuolo = PddRuolo.APPLICATIVA;
			}
			else if(CostantiExporter.TIPOLOGIA_FRUIZIONE.equals(tipologiaTransazione)){
				pddRuolo = PddRuolo.DELEGATA;
			}
			searchForm.setTipologiaRicerca(pddRuolo.getValue());
			searchForm.setTipologiaTransazioni(pddRuolo);
			
			
			
			StringBuffer bf = new StringBuffer();
			ReflectionToStringBuilder builder = new ReflectionToStringBuilder(searchForm, ToStringStyle.MULTI_LINE_STYLE, bf, null, false, false);
			builder.toString();
			ReportExporter.log.debug("Lettura parametri completata, search form: "+bf.toString());
			
			ConfigurazioniExporter.export(req, resp, true, service, null, pddRuolo);
			
		}catch(Throwable e){
			ReportExporter.log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	private void getStatistiche(HttpServletRequest req, HttpServletResponse resp, UserDetailsBean user) throws Exception{
		
		try{	
			
			// Lettura parametri comuni obbligatori:
			
			// Comprensione distribuzione del report
			String tipoDistribuzioneReport = req.getParameter(CostantiExporter.TIPO_DISTRIBUZIONE);
			if(tipoDistribuzioneReport==null){
				throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.TIPO_DISTRIBUZIONE+"' non fornito");
			}
			tipoDistribuzioneReport=tipoDistribuzioneReport.trim();
			if(CostantiExporter.TIPI_DISTRIBUZIONE.contains(tipoDistribuzioneReport) == false){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_DISTRIBUZIONE+"' fornito possiede un valore '"+tipoDistribuzioneReport
						+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.TIPI_DISTRIBUZIONE);
			}
			
			// Comprensione tipo documentato da esportare
			String tipoFormato = req.getParameter(CostantiExporter.TIPO_FORMATO);
			if(tipoFormato==null){
				throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.TIPO_FORMATO+"' non fornito");
			}
			tipoFormato=tipoFormato.trim();
			if(CostantiExporter.TIPI_FORMATO.contains(tipoFormato) == false){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_FORMATO+"' fornito possiede un valore '"+tipoFormato
						+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.TIPI_FORMATO);
			}
			
			StatsSearchForm statSearchForm = null;
			if(CostantiExporter.TIPO_DISTRIBUZIONE_PERSONALIZZATA.equals(tipoDistribuzioneReport)){
				statSearchForm = new StatistichePersonalizzateSearchForm();
			}else{
				statSearchForm = new StatsSearchForm();
			}
			statSearchForm.setUser(user.getUtente());
			statSearchForm.setUsaSVG(true);
			statSearchForm.setUseGraficiSVG(true);
			statSearchForm.setAction(tipoDistribuzioneReport);
			

					
			// Istanzio bean
			
			ReportExporter.log.debug("Inizializzazione bean ["+tipoDistribuzioneReport+"] in corso ...");
			
			org.openspcoop2.web.monitor.statistiche.dao.StatisticheGiornaliereService service =
					new org.openspcoop2.web.monitor.statistiche.dao.StatisticheGiornaliereService();
			
			BaseStatsMBean<?, ?, ?> bean = null;
			if(CostantiExporter.TIPO_DISTRIBUZIONE_TEMPORALE.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.ANDAMENTO_TEMPORALE);
				statSearchForm.setAndamentoTemporalePerEsiti(false);
				service.setAndamentoTemporaleSearch(statSearchForm);
				bean = new AndamentoTemporaleBean();
				((AndamentoTemporaleBean) bean).setStatisticheGiornaliereService(service);
				((AndamentoTemporaleBean) bean).setSearch(statSearchForm);
				((AndamentoTemporaleBean) bean).initSearchListenerAndamentoTemporale(null); 
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_ESITI.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.ANDAMENTO_TEMPORALE);
				statSearchForm.setAndamentoTemporalePerEsiti(true);
				service.setAndamentoTemporaleSearch(statSearchForm);
				bean = new AndamentoTemporaleBean();
				((AndamentoTemporaleBean) bean).setStatisticheGiornaliereService(service);
				((AndamentoTemporaleBean) bean).setSearch(statSearchForm);
				((AndamentoTemporaleBean) bean).initSearchListenerDistribuzionePerEsiti(null); 
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SOGGETTO);
				statSearchForm.setDistribuzionePerSoggettoRemota(true);
				service.setDistribSoggettoSearch(statSearchForm);
				bean = new DistribuzionePerSoggettoBean<>();
				((DistribuzionePerSoggettoBean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerSoggettoBean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerSoggettoBean<?>) bean).initSearchListenerRemoto(null);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SOGGETTO);
				statSearchForm.setDistribuzionePerSoggettoRemota(false);
				service.setDistribSoggettoSearch(statSearchForm);
				bean = new DistribuzionePerSoggettoBean<>();
				((DistribuzionePerSoggettoBean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerSoggettoBean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerSoggettoBean<?>) bean).initSearchListenerLocale(null);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_SERVIZIO.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SERVIZIO);
				service.setDistribServizioSearch(statSearchForm);
				bean = new DistribuzionePerServizioBean<>();
				((DistribuzionePerServizioBean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerServizioBean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerServizioBean<?>) bean).getSearch().initSearchListener(null); 
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_AZIONE.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_AZIONE);
				service.setDistribAzioneSearch(statSearchForm);
				bean = new DistribuzionePerAzioneBean<>();
				((DistribuzionePerAzioneBean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerAzioneBean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerAzioneBean<?>) bean).getSearch().initSearchListener(null);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO);
				service.setDistribSaSearch(statSearchForm);
				bean = new DistribuzionePerSABean<>();
				((DistribuzionePerSABean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerSABean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerSABean<?>) bean).getSearch().initSearchListener(null);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_PERSONALIZZATA.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.STATISTICA_PERSONALIZZATA);
				service.setStatistichePersonalizzateSearch((StatistichePersonalizzateSearchForm)statSearchForm);
				bean = new StatsPersonalizzateBean();
				((StatsPersonalizzateBean) bean).setStatisticheGiornaliereService(service);
				((StatsPersonalizzateBean) bean).setSearch(statSearchForm);
				((StatsPersonalizzateBean) bean).getSearch().initSearchListener(null);
			}
			
			ReportExporter.log.debug("Inizializzazione bean ["+tipoDistribuzioneReport+"] completata");
			
			
			ReportExporter.log.debug("Imposto parametri di ricerca nel search form ...");
			setParametersInSearchForm(req, statSearchForm);
			
			StringBuffer bf = new StringBuffer();
			ReflectionToStringBuilder builder = new ReflectionToStringBuilder(statSearchForm, ToStringStyle.MULTI_LINE_STYLE, bf, null, false, false);
			builder.toString();
			ReportExporter.log.debug("Lettura parametri completata, search form: "+bf.toString());
			
			
			ReportExporter.log.debug("Esportazione["+tipoFormato+"] tramite bean ["+tipoDistribuzioneReport+"] in corso ...");
			
			if(CostantiExporter.TIPO_FORMATO_CSV.equals(tipoFormato)){
				bean.esportaCsv(resp);
			}
			else if(CostantiExporter.TIPO_FORMATO_XLS.equals(tipoFormato)){
				bean.esportaXls(resp);
			}
			else if(CostantiExporter.TIPO_FORMATO_PDF.equals(tipoFormato)){
				bean.esportaPdf(resp);
			}
			
			ReportExporter.log.debug("Esportazione["+tipoFormato+"] tramite bean ["+tipoDistribuzioneReport+"] completata");
			
		}catch(Throwable e){
			ReportExporter.log.error(e.getMessage(),e);
			throw e;
		}
	}


	private void setParametersInSearchForm(HttpServletRequest req,StatsSearchForm statSearchForm ) throws Exception{
		// Andare in ordine con l'xhtml dei search form
		
		
		// ** Intervallo **
		
		String dataInizio = req.getParameter(CostantiExporter.DATA_INIZIO);
		if(dataInizio==null){
			throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.DATA_INIZIO+"' non fornito");
		}
		dataInizio=dataInizio.trim();
		Date dInizio = null;
		try{
			if(dataInizio.contains(":")){
				SimpleDateFormat sdf = new SimpleDateFormat(CostantiExporter.FORMAT_TIME);
				dInizio = sdf.parse(dataInizio);
			}
			else{
				SimpleDateFormat sdf = new SimpleDateFormat(CostantiExporter.FORMAT_DATE);
				dInizio = sdf.parse(dataInizio);
				Calendar c = DateManager.getCalendar();
				c.setTime(dInizio);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				dInizio = c.getTime();
			}
		}catch(Exception e){
			throw new ParameterUncorrectException("Parametro '"+CostantiExporter.DATA_INIZIO+"' fornito possiede un valore '"+dataInizio
					+"' non valido. I formati supportati sono '"+CostantiExporter.FORMAT_TIME+"' o '"+CostantiExporter.FORMAT_DATE+"'. Errore rilevato: "+e.getMessage(),e);
		}
		
		String dataFine = req.getParameter(CostantiExporter.DATA_FINE);
		if(dataFine==null){
			throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.DATA_FINE+"' non fornito");
		}
		dataFine=dataFine.trim();
		Date dFine = null;
		try{
			if(dataFine.contains(":")){
				SimpleDateFormat sdf = new SimpleDateFormat(CostantiExporter.FORMAT_TIME);
				dFine = sdf.parse(dataFine);
			}
			else{
				SimpleDateFormat sdf = new SimpleDateFormat(CostantiExporter.FORMAT_DATE);
				dFine = sdf.parse(dataFine);
				Calendar c = DateManager.getCalendar();
				c.setTime(dFine);
				c.set(Calendar.HOUR_OF_DAY, 23);
				c.set(Calendar.MINUTE, 59);
				c.set(Calendar.SECOND, 59);
				c.set(Calendar.MILLISECOND, 999);
				dFine = c.getTime();
			}
		}catch(Exception e){
			throw new ParameterUncorrectException("Parametro '"+CostantiExporter.DATA_FINE+"' fornito possiede un valore '"+dataFine
					+"' non valido. I formati supportati sono '"+CostantiExporter.FORMAT_TIME+"' o '"+CostantiExporter.FORMAT_DATE+"'. Errore rilevato: "+e.getMessage(),e);
		}
	
		statSearchForm.setDataInizio(dInizio);
		statSearchForm.setDataFine(dFine);
		

		
		// ** Soggetto / Servizio / Azione **
		
		String protocollo = req.getParameter(CostantiExporter.PROTOCOLLO);
		if(protocollo!=null){
			protocollo = protocollo.trim();
			try{
				ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			}catch(Exception e){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.PROTOCOLLO+"' fornito possiede un valore '"+protocollo
						+"' sconosciuto. I tipi supportati sono: "+ProtocolFactoryManager.getInstance().getProtocolNames());
			}
			statSearchForm.setProtocollo(protocollo);
		}
		else {
			throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.PROTOCOLLO+"' non fornito");
		}
		
		String mittente = req.getParameter(CostantiExporter.MITTENTE);
		if(mittente!=null){
			mittente = mittente.trim();
			statSearchForm.setTipoNomeMittente(mittente);
		}
		
		String destinatario = req.getParameter(CostantiExporter.DESTINATARIO);
		if(destinatario!=null){
			destinatario = destinatario.trim();
			statSearchForm.setTipoNomeDestinatario(destinatario);
		}
		
		String soggettoLocale = req.getParameter(CostantiExporter.SOGGETTO_LOCALE);
		if(soggettoLocale!=null){
			soggettoLocale = soggettoLocale.trim();
			statSearchForm.setTipoNomeSoggettoLocale(soggettoLocale);
		}
		
		String trafficoPerSoggetto = req.getParameter(CostantiExporter.TRAFFICO_PER_SOGGETTO);
		if(trafficoPerSoggetto!=null){
			trafficoPerSoggetto = trafficoPerSoggetto.trim();
			statSearchForm.setTipoNomeTrafficoPerSoggetto(trafficoPerSoggetto);
		}
		
		
		
		// ** Tipologia Transazione **
		
		String tipologiaTransazione = req.getParameter(CostantiExporter.TIPOLOGIA);
		if(tipologiaTransazione!=null){
			tipologiaTransazione = tipologiaTransazione.trim();
			if(CostantiExporter.TIPOLOGIE.contains(tipologiaTransazione) == false){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPOLOGIA+"' fornito possiede un valore '"+tipologiaTransazione
						+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.TIPOLOGIE);
			}
		}
		else{
			tipologiaTransazione = CostantiExporter.TIPOLOGIA_EROGAZIONE_FRUIZIONE;
		}
		if(CostantiExporter.TIPOLOGIA_EROGAZIONE_FRUIZIONE.equals(tipologiaTransazione)){
			statSearchForm.setTipologiaRicerca(CostantiExporter.RICERCA_ALL);
		}
		else if(CostantiExporter.TIPOLOGIA_EROGAZIONE.equals(tipologiaTransazione)){
			statSearchForm.setTipologiaRicerca(CostantiExporter.RICERCA_INGRESSO);
		}
		else if(CostantiExporter.TIPOLOGIA_FRUIZIONE.equals(tipologiaTransazione)){
			statSearchForm.setTipologiaRicerca(CostantiExporter.RICERCA_USCITA);
		}
		
		
		// ** Esito **
		
		EsitiProperties esitiProperties = EsitiProperties.getInstance(log, protocollo);

		statSearchForm.setEsitoGruppo(EsitoUtils.ALL_VALUE);
		statSearchForm.setEsitoDettaglio(EsitoUtils.ALL_VALUE);
		
		String esitoGruppo = req.getParameter(CostantiExporter.ESITO_GRUPPO);
		if(esitoGruppo!=null){
			esitoGruppo = esitoGruppo.trim();
			if(CostantiExporter.ESITI_GRUPPO.contains(esitoGruppo) == false){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.ESITO_GRUPPO+"' fornito possiede un valore '"+esitoGruppo
						+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.ESITO_GRUPPO);
			}
			if(CostantiExporter.ESITO_GRUPPO_OK.equals(esitoGruppo)){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_OK_VALUE);
			}
			else if(CostantiExporter.ESITO_GRUPPO_ERROR.equals(esitoGruppo)){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_ERROR_VALUE);
			}
			else if(CostantiExporter.ESITO_GRUPPO_FAULT_APPLICATIVO.equals(esitoGruppo)){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE);
			}
			else if(CostantiExporter.ESITO_GRUPPO_ERROR_FAULT_APPLICATIVO.equals(esitoGruppo)){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE);
			}
		}
			
		String esito = req.getParameter(CostantiExporter.ESITO);
		if(esito!=null){
			esito = esito.trim();
			String [] split = null;
			Integer [] esiti = null;
			if(esito.contains(",")){
				split = esito.split(",");
			}
			else{
				split = new String[1];
				split[0] = esito;
			}
			esiti = new Integer[split.length];
			for (int i = 0; i < split.length; i++) {
				split[i] = split[i].trim();
				int intValueEsito;
				try{
					intValueEsito = Integer.parseInt(split[i]);
				}catch(Exception e){
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.ESITO+"' fornito possiede un valore '"+split[i]
							+"' non corretto: "+e.getMessage(),e);
				}
				if(esitiProperties.existsEsitoCode(intValueEsito)==false){
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.ESITO+"' fornito possiede un valore '"+split[i]
							+"' sconosciuto. I valori supportati sono: "+esitiProperties.getEsitiCode());
				}	
				esiti[i]=intValueEsito;
			}
			if(esiti.length>1){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_PERSONALIZZATO_VALUE);
				statSearchForm.setEsitoDettaglioPersonalizzato(esiti);
			}
			else{
				statSearchForm.setEsitoDettaglio(esiti[0]);
			}
		}
		
		String esitoContesto = esitiProperties.getEsitoTransactionContextDefault();
		String esitoContestoTmp = req.getParameter(CostantiExporter.ESITO_CONTESTO);
		if(esitoContestoTmp!=null){
			esitoContestoTmp = esitoContestoTmp.trim();
			if(esitiProperties.getEsitiTransactionContextCode().contains(esitoContestoTmp)==false){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.ESITO_CONTESTO+"' fornito possiede un valore '"+esitoContestoTmp
						+"' sconosciuto. I tipi supportati sono: "+esitiProperties.getEsitiTransactionContextCode());
			}
			esitoContesto = esitoContestoTmp;
		}
		if(esitoContesto!=null){
			statSearchForm.setEsitoContesto(esitoContesto);
		}
		
		
		
		// ** Tipo Report **
		// NOTA: PARAMETRO INUTILIZZATO NELL'EXPORT
		TipoReport tipoReportEnum = CostantiExporter.TIPO_REPORT_DISTRIBUZIONE_OTHER_DEFAULT;
		if(CostantiExporter.TIPO_DISTRIBUZIONE_TEMPORALE.equals(statSearchForm.getAction())){
			tipoReportEnum = CostantiExporter.TIPO_REPORT_DISTRIBUZIONE_TEMPORALE_DEFAULT;
		}
//		String tipoReport = req.getParameter(CostantiExporter.TIPO_REPORT);
//		if(tipoReport!=null){
//			tipoReport = tipoReport.trim();
//			tipoReportEnum = TipoReport.toEnumConstant(tipoReport);
//			if(tipoReportEnum==null){
//				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_REPORT+"' fornito possiede un valore '"+tipoReportEnum
//						+"' sconosciuto. I tipi supportati sono: "+TipoReport.toStringArray());
//			}
//			if(CostantiExporter.TIPO_DISTRIBUZIONE_TEMPORALE.equals(statSearchForm.getAction()) ||
//					CostantiExporter.TIPO_DISTRIBUZIONE_ESITI.equals(statSearchForm.getAction())){
//				if(TipoReport.PIE_CHART.equals(tipoReportEnum) || TipoReport.ANDAMENTO_TEMPORALE.equals(tipoReportEnum)){
//					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_REPORT+"' fornito possiede un valore '"+tipoReportEnum
//							+"' non permesso per la distribuzione di report richiesta: "+statSearchForm.getAction());
//				}
//			}
//			else if(CostantiExporter.TIPO_DISTRIBUZIONE_PERSONALIZZATA.equals(statSearchForm.getAction()) ){
//				if(TipoReport.LINE_CHART.equals(tipoReportEnum)){
//					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_REPORT+"' fornito possiede un valore '"+tipoReportEnum
//							+"' non permesso per la distribuzione di report richiesta: "+statSearchForm.getAction());
//				}
//			}
//			else {
//				if(TipoReport.LINE_CHART.equals(tipoReportEnum) || TipoReport.ANDAMENTO_TEMPORALE.equals(tipoReportEnum)){
//					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_REPORT+"' fornito possiede un valore '"+tipoReportEnum
//							+"' non permesso per la distribuzione di report richiesta: "+statSearchForm.getAction());
//				}
//			}
//		}
		statSearchForm.setTipoReport(tipoReportEnum);
		
		
		
		// ** tipo informazione visualizzata **
		
		StatisticType modalitaTemporaleEnum = StatisticType.GIORNALIERA;
		String modalitaTemporale = req.getParameter(CostantiExporter.TIPO_UNITA_TEMPORALE);
		if(modalitaTemporale!=null){
			modalitaTemporale = modalitaTemporale.trim();
			modalitaTemporaleEnum = StatisticType.valueOf(modalitaTemporale.toUpperCase());
			if(modalitaTemporaleEnum==null){
				List<String> types = new ArrayList<String>();
				for (StatisticType type: StatisticType.values()) {
					types.add(type.name().toLowerCase());
				}
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_UNITA_TEMPORALE+"' fornito possiede un valore '"+modalitaTemporaleEnum
						+"' sconosciuto. I tipi supportati sono: "+types);
			}
		}
		statSearchForm.setModalitaTemporale(modalitaTemporaleEnum);
		
		
		// ** tipo informazione visualizzata **
		
		String tipoVisualizzazione = req.getParameter(CostantiExporter.TIPO_INFORMAZIONE_VISUALIZZATA);
		TipoVisualizzazione tipoVisualizzazioneEnum = CostantiExporter.TIPO_INFORMAZIONE_VISUALIZZATA_DEFAULT;
		if(tipoVisualizzazione!=null){
			tipoVisualizzazione = tipoVisualizzazione.trim();
			tipoVisualizzazioneEnum = TipoVisualizzazione.toEnumConstant(tipoVisualizzazione);
			if(tipoVisualizzazioneEnum==null){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_INFORMAZIONE_VISUALIZZATA+"' fornito possiede un valore '"+tipoVisualizzazione
						+"' sconosciuto. I tipi supportati sono: "+TipoVisualizzazione.toArray());
			}
		}
		statSearchForm.setTipoVisualizzazione(tipoVisualizzazioneEnum);
		
		if(TipoVisualizzazione.DIMENSIONE_TRANSAZIONI.equals(tipoVisualizzazioneEnum)){
			String tipoBanda = req.getParameter(CostantiExporter.TIPO_BANDA_VISUALIZZATA);
			TipoBanda tipoBandaEnum = CostantiExporter.TIPO_BANDA_VISUALIZZATA_DEFAULT;
			TipoBanda [] tipiBandaEnum = new TipoBanda[TipoBanda.values().length];
			tipiBandaEnum[0] = TipoBanda.COMPLESSIVA;
			tipiBandaEnum[1] = TipoBanda.INTERNA;
			tipiBandaEnum[2] = TipoBanda.ESTERNA;
			if(tipoBanda!=null){
				tipoBanda = tipoBanda.trim();
				String [] tmp = null;
				if(tipoBanda.contains(",")){
					tmp = tipoBanda.split(",");
					tipiBandaEnum = new TipoBanda[tmp.length];
				}
				else{
					tmp = new String[1];
					tipiBandaEnum = new TipoBanda[1];
					tmp[0] = tipoBanda;
				}
				for (int i = 0; i < tmp.length; i++) {
					tmp[i] = tmp[i].trim();
					TipoBanda tmpBanda = TipoBanda.toEnumConstant(tmp[i]); 
					if(tmpBanda==null){
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_BANDA_VISUALIZZATA+"' fornito possiede un valore '"+tmp[i]
								+"' sconosciuto. I tipi supportati sono: "+TipoBanda.toArray());
					}	
					if(i==0){
						tipoBandaEnum = tmpBanda;
					}	
					tipiBandaEnum[i]=tmpBanda;
				}
			}
			statSearchForm.setTipoBanda(tipoBandaEnum);
			String [] arrayBanda = new String[tipiBandaEnum.length];
			for (int i = 0; i < tipiBandaEnum.length; i++) {
				switch (tipiBandaEnum[i]) {
				case COMPLESSIVA:
					arrayBanda[i] = "0";
					break;
				case INTERNA:
					arrayBanda[i] = "1";
					break;
				case ESTERNA:
					arrayBanda[i] = "2";
					break;
				}
			}
			statSearchForm.setTipiBanda(arrayBanda);
		}

		if(TipoVisualizzazione.TEMPO_MEDIO_RISPOSTA.equals(tipoVisualizzazioneEnum)){
			String tipoLatenza = req.getParameter(CostantiExporter.TIPO_LATENZA_VISUALIZZATA);
			TipoLatenza tipoLatenzaEnum = CostantiExporter.TIPO_LATENZA_VISUALIZZATA_DEFAULT;
			TipoLatenza [] tipiLatenzaEnum = new TipoLatenza[TipoLatenza.values().length];
			tipiLatenzaEnum[0] = TipoLatenza.LATENZA_TOTALE;
			tipiLatenzaEnum[1] = TipoLatenza.LATENZA_SERVIZIO;
			tipiLatenzaEnum[2] = TipoLatenza.LATENZA_PORTA;
			if(tipoLatenza!=null){
				tipoLatenza = tipoLatenza.trim();
				String [] tmp = null;
				if(tipoLatenza.contains(",")){
					tmp = tipoLatenza.split(",");
					tipiLatenzaEnum = new TipoLatenza[tmp.length];
				}
				else{
					tmp = new String[1];
					tipiLatenzaEnum = new TipoLatenza[1];
					tmp[0] = tipoLatenza;
				}
				for (int i = 0; i < tmp.length; i++) {
					tmp[i] = tmp[i].trim();
					TipoLatenza tmpLatenza = TipoLatenza.toEnumConstant(tmp[i]); 
					if(tmpLatenza==null){
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_LATENZA_VISUALIZZATA+"' fornito possiede un valore '"+tmp[i]
								+"' sconosciuto. I tipi supportati sono: "+TipoBanda.toArray());
					}	
					if(i==0){
						tipoLatenzaEnum = tmpLatenza;
					}	
					tipiLatenzaEnum[i]=tmpLatenza;
				}
			}
			statSearchForm.setTipoLatenza(tipoLatenzaEnum);
			String [] arrayLatenza = new String[tipiLatenzaEnum.length];
			for (int i = 0; i < tipiLatenzaEnum.length; i++) {
				switch (tipiLatenzaEnum[i]) {
				case LATENZA_TOTALE:
					arrayLatenza[i] = "0";
					break;
				case LATENZA_SERVIZIO:
					arrayLatenza[i] = "1";
					break;
				case LATENZA_PORTA:
					arrayLatenza[i] = "2";
					break;
				}
			}
			statSearchForm.setTipiLatenza(arrayLatenza);
		}
	}
	
	
	private class ParameterUncorrectException extends Exception {

		/**     
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		public ParameterUncorrectException(String message, Throwable cause){
			super(message, cause);
		}

		public ParameterUncorrectException(String msg) {
			super(msg);
		}

	}
}
