/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.statistiche.constants.TipoBanda;
import org.openspcoop2.core.statistiche.constants.TipoLatenza;
import org.openspcoop2.core.statistiche.constants.TipoReport;
import org.openspcoop2.core.statistiche.constants.TipoStatistica;
import org.openspcoop2.core.statistiche.constants.TipoVisualizzazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.regexp.RegExpException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegExpNotValidException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.http.HttpServletCredential;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.constants.CaseSensitiveMatch;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.TipoMatch;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.dao.DBLoginDAO;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.DimensioneCustom;
import org.openspcoop2.web.monitor.statistiche.bean.NumeroDimensioni;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePersonalizzateSearchForm;
import org.openspcoop2.web.monitor.statistiche.bean.StatsSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiExporter;
import org.openspcoop2.web.monitor.statistiche.dao.ConfigurazioniGeneraliService;
import org.openspcoop2.web.monitor.statistiche.mbean.AndamentoTemporaleBean;
import org.openspcoop2.web.monitor.statistiche.mbean.BaseStatsMBean;
import org.openspcoop2.web.monitor.statistiche.mbean.DistribuzionePerAzioneBean;
import org.openspcoop2.web.monitor.statistiche.mbean.DistribuzionePerErroriBean;
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
	private static void logError(String msg) {
		logError(msg, null);
	}
	private static void logError(String msg, Exception e) {
		if(log!=null) {
			if(e!=null) {
				log.error(msg,e);
			}
			else {
				log.error(msg);
			}
		}
	}
	private static void logDebug(String msg) {
		if(log!=null) {
			log.debug(msg);
		}
	}

	private static boolean serviceEnabled = false;
	public static void setServiceEnabled(boolean serviceEnabled) {
		ReportExporter.serviceEnabled = serviceEnabled;
	}
	@Override
	public void init() throws ServletException {
		try{
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(ReportExporter.log);
			ReportExporter.setServiceEnabled(govwayMonitorProperties.isStatisticheAttivoServizioEsportazioneReport());
		}catch(Exception e){
			ReportExporter.logError("Inizializzazione servlet fallita, setto enableHeaderInfo=false",e);
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

	private static final String AUTENTICAZIONE_FALLITA = "Autenticazione fallita";
	private static final String SEND_ERROR = "sendError '";

	private void processRequest(HttpServletRequest req, HttpServletResponse resp) {
		
		StringBuilder bfSource = new StringBuilder("");
		String mittentePrefix = "";
		try{

			if(!serviceEnabled){
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
				ReportExporter.logError("Credenziali non fornita, mittente: "+bfSource.toString());
				resp.sendError(CostantiExporter.CREDENZIALI_NON_FORNITE, "Autenticazione richiesta");
				return;
			}
			mittentePrefix = "[mittente: "+bfSource.toString()+"] ";
			
			DBLoginDAO loginService = new DBLoginDAO();
			UserDetailsBean user = null;
			if(principal!=null){
				try{
					user = loginService.loadUserByUsername(principal);
				}catch(NotFoundException notFound){
					ReportExporter.logError(mittentePrefix+"Autenticazione fallita con il principal ottenuto ["+principal+"]: "+notFound.getMessage(),notFound);
					resp.sendError(CostantiExporter.AUTENTICAZIONE_FALLITA, AUTENTICAZIONE_FALLITA);
					return;
				}
			}
			else{
				boolean check = loginService.login(username, password);
				if(!check){
					ReportExporter.logError(mittentePrefix+"Autenticazione fallita con le credenziali fornite user["+username+"] password["+password+"]");
					resp.sendError(CostantiExporter.AUTENTICAZIONE_FALLITA, AUTENTICAZIONE_FALLITA);
					return;
				}
				else{
					try{
						user = loginService.loadUserByUsername(username);
					}catch(NotFoundException notFound){
						ReportExporter.logError(mittentePrefix+"Autenticazione fallita con lo username ["+username+"]: "+notFound.getMessage(),notFound);
						resp.sendError(CostantiExporter.AUTENTICAZIONE_FALLITA, AUTENTICAZIONE_FALLITA);
						return;
					}
				}
			}
			
			
			String r = getServletConfig().getInitParameter("resource");
			if("config".equals(r)){
				ConfigurazioniGeneraliService service = new ConfigurazioniGeneraliService();
				generaConfigurazione(req, resp, user, service);
				ReportExporter.logDebug(mittentePrefix+"richiesta report configurazione completata");
			}
			else if("stat".equals(r)){
				org.openspcoop2.web.monitor.statistiche.dao.StatisticheGiornaliereService service =
						new org.openspcoop2.web.monitor.statistiche.dao.StatisticheGiornaliereService();
				generaStatistiche(req, resp, user, service);
				ReportExporter.logDebug(mittentePrefix+"richiesta report statistica completata");
			}
			else{
				throw new CoreException("InitParameter 'resource' with unknown resource ["+r+"]");
			}
			
		}
		catch(ParameterUncorrectException e){
			ReportExporter.logError(mittentePrefix+""+e.getMessage(),e);
			try {
				resp.sendError(CostantiExporter.DATI_NON_CORRETTI, e.getMessage());
			}catch(Exception t) {
				ReportExporter.logError(mittentePrefix+SEND_ERROR+CostantiExporter.DATI_NON_CORRETTI+"' "+e.getMessage(),e);
			}
		}
		catch(NotFoundException e){
			ReportExporter.logError(mittentePrefix+""+e.getMessage(),e);
			try {
				resp.sendError(CostantiExporter.DATI_NON_TROVATI, e.getMessage());
			}catch(Exception t) {
				ReportExporter.logError(mittentePrefix+SEND_ERROR+CostantiExporter.DATI_NON_TROVATI+"' "+e.getMessage(),e);
			}
		}
		catch(Exception e){
			ReportExporter.logError(mittentePrefix+""+e.getMessage(),e);
			try {
				resp.sendError(CostantiExporter.ERRORE_SERVER, e.getMessage());
			}catch(Exception t) {
				ReportExporter.logError(mittentePrefix+SEND_ERROR+CostantiExporter.ERRORE_SERVER+"' "+e.getMessage(),e);
			}
		}
			
	}
	
	public static void generaConfigurazione(HttpServletRequest req, HttpServletResponse resp, UserDetailsBean user,
			ConfigurazioniGeneraliService service) throws Exception{
		
		try{	
			ConfigurazioniGeneraliSearchForm searchForm = new ConfigurazioniGeneraliSearchForm();
			searchForm.setUser(user.getUtente());
			
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
				List<String> l = new ArrayList<>();
				l.add(CostantiExporter.TIPOLOGIA_EROGAZIONE);
				l.add(CostantiExporter.TIPOLOGIA_FRUIZIONE);
				if(!l.contains(tipologiaTransazione)){
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPOLOGIA+"' fornito possiede un valore '"+tipologiaTransazione
							+"' sconosciuto. I tipi supportati sono: "+l);
				}
			}
			else{
				throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.TIPOLOGIA+"' non fornito");
			}
			if(CostantiExporter.TIPOLOGIA_EROGAZIONE.equals(tipologiaTransazione)){
				searchForm.setTipologiaRicerca(CostantiExporter.RICERCA_INGRESSO);
				searchForm.setTipologiaTransazioni(PddRuolo.APPLICATIVA);
			}
			else if(CostantiExporter.TIPOLOGIA_FRUIZIONE.equals(tipologiaTransazione)){
				searchForm.setTipologiaRicerca(CostantiExporter.RICERCA_USCITA);
				searchForm.setTipologiaTransazioni(PddRuolo.DELEGATA);
			}
			searchForm.saveProtocollo();
			
			// Identificazione tipo documentato da esportare
			String formatoExport = req.getParameter(CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE);
			if(formatoExport == null){
				throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE+"' non fornito");
			}
			formatoExport=formatoExport.trim();
			if(!CostantiExporter.getTipiFormatoConfigurazione().contains(formatoExport)){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE+"' fornito possiede un valore '"+formatoExport
						+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getTipiFormatoConfigurazione());
			}
			
			StringBuffer bf = new StringBuffer();
			ReflectionToStringBuilder builder = new ReflectionToStringBuilder(searchForm, ToStringStyle.MULTI_LINE_STYLE, bf, null, false, false);
			builder.toString();
			ReportExporter.logDebug("Lettura parametri completata, search form: "+bf.toString());
			
			ConfigurazioniExporter.export(req, resp, true, service, null, searchForm.getTipologiaTransazioni(), formatoExport);
			
		}catch(Exception e){
			ReportExporter.logError(e.getMessage(),e);
			throw e;
		}
	}
	
	public static void generaStatistiche(HttpServletRequest req, HttpServletResponse resp, UserDetailsBean user,
			org.openspcoop2.web.monitor.statistiche.dao.StatisticheGiornaliereService service) throws Exception{
		
		try{	
			
			// Lettura parametri comuni obbligatori:
			
			// Identificazione distribuzione del report
			String tipoDistribuzioneReport = req.getParameter(CostantiExporter.TIPO_DISTRIBUZIONE);
			if(tipoDistribuzioneReport==null){
				throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.TIPO_DISTRIBUZIONE+"' non fornito");
			}
			tipoDistribuzioneReport=tipoDistribuzioneReport.trim();
			if(!CostantiExporter.getTipiDistribuzione().contains(tipoDistribuzioneReport)){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_DISTRIBUZIONE+"' fornito possiede un valore '"+tipoDistribuzioneReport
						+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getTipiDistribuzione());
			}
			
			// Identificazione tipo documentato da esportare
			String tipoFormato = req.getParameter(CostantiExporter.TIPO_FORMATO);
			if(tipoFormato==null){
				throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.TIPO_FORMATO+"' non fornito");
			}
			tipoFormato=tipoFormato.trim();
			if(!CostantiExporter.getTipiFormato().contains(tipoFormato)){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_FORMATO+"' fornito possiede un valore '"+tipoFormato
						+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getTipiFormato());
			}
			
			// Tipo applicativo
			String identificazioneApplicativo = null;
			if(CostantiExporter.TIPO_DISTRIBUZIONE_APPLICATIVO.equals(tipoDistribuzioneReport)){
				String tipoIdentificazioneApplicativo = req.getParameter(CostantiExporter.TIPO_IDENTIFICAZIONE_APPLICATIVO);
				if(tipoIdentificazioneApplicativo==null){
					throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.TIPO_IDENTIFICAZIONE_APPLICATIVO+"' non fornito");
				}
				tipoIdentificazioneApplicativo = tipoIdentificazioneApplicativo.trim();
				if(!CostantiExporter.getTipiIdentificazioneApplicativo().contains(tipoIdentificazioneApplicativo)){
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_IDENTIFICAZIONE_APPLICATIVO+"' fornito possiede un valore '"+tipoIdentificazioneApplicativo
							+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getTipiIdentificazioneApplicativo());
				}
				if(CostantiExporter.TIPO_IDENTIFICAZIONE_APPLICATIVO_TRASPORTO.equals(tipoIdentificazioneApplicativo)) {
					identificazioneApplicativo = Costanti.IDENTIFICAZIONE_TRASPORTO_KEY;	
				}
				else if(CostantiExporter.TIPO_IDENTIFICAZIONE_APPLICATIVO_TOKEN.equals(tipoIdentificazioneApplicativo)) {
					identificazioneApplicativo = Costanti.IDENTIFICAZIONE_TOKEN_KEY;		
				}
			}
			
			// Identificazione claim
			org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente tokenClaim = null;
			if(CostantiExporter.TIPO_DISTRIBUZIONE_TOKEN_INFO.equals(tipoDistribuzioneReport)){
				String claim = req.getParameter(CostantiExporter.CLAIM);
				if(claim==null){
					throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.CLAIM+"' non fornito");
				}
				claim=claim.trim();
				if(!CostantiExporter.getClaims().contains(claim)){
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.CLAIM+"' fornito possiede un valore '"+claim
							+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getClaims());
				}
				if(CostantiExporter.CLAIM_ISSUER.equals(claim)) {
					tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_ISSUER;
				}
				else if(CostantiExporter.CLAIM_SUBJECT.equals(claim)) {
					tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_SUBJECT;
				}
				else if(CostantiExporter.CLAIM_USERNAME.equals(claim)) {
					tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_USERNAME;
				}
				else if(CostantiExporter.CLAIM_CLIENT_ID.equals(claim)) {
					tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_CLIENT_ID;
				}
				else if(CostantiExporter.CLAIM_EMAIL.equals(claim)) {
					tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_EMAIL;
				}
				else if(CostantiExporter.CLAIM_PDND_ORGANIZATION_NAME.equals(claim)) {
					tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.PDND_ORGANIZATION_NAME;
				}
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
			
			String protocollo = setProtocolParametersInSearchForm(req, statSearchForm);
			if(protocollo!=null) {
				// nop
			}

			ReportExporter.logDebug("Inizializzazione bean ["+tipoDistribuzioneReport+"] in corso ...");
						
			BaseStatsMBean<?, ?, ?> bean = null;
			if(CostantiExporter.TIPO_DISTRIBUZIONE_TEMPORALE.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.ANDAMENTO_TEMPORALE);
				statSearchForm.setAndamentoTemporalePerEsiti(false);
				service.setAndamentoTemporaleSearch(statSearchForm);
				bean = new AndamentoTemporaleBean(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((AndamentoTemporaleBean) bean).setStatisticheGiornaliereService(service);
				((AndamentoTemporaleBean) bean).setSearch(statSearchForm);
				((AndamentoTemporaleBean) bean).initSearchListenerAndamentoTemporale(null); 
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_ESITI.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.ANDAMENTO_TEMPORALE);
				statSearchForm.setAndamentoTemporalePerEsiti(true);
				service.setAndamentoTemporaleSearch(statSearchForm);
				bean = new AndamentoTemporaleBean(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((AndamentoTemporaleBean) bean).setStatisticheGiornaliereService(service);
				((AndamentoTemporaleBean) bean).setSearch(statSearchForm);
				((AndamentoTemporaleBean) bean).initSearchListenerDistribuzionePerEsiti(null); 
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_ERRORI.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_ERRORI);
				service.setDistribErroriSearch(statSearchForm);
				bean = new DistribuzionePerErroriBean<>(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((DistribuzionePerErroriBean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerErroriBean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerErroriBean<?>) bean).getSearch().initSearchListener(null);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SOGGETTO);
				statSearchForm.setDistribuzionePerSoggettoRemota(true);
				service.setDistribSoggettoSearch(statSearchForm);
				bean = new DistribuzionePerSoggettoBean<>(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((DistribuzionePerSoggettoBean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerSoggettoBean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerSoggettoBean<?>) bean).initSearchListenerRemoto(null);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SOGGETTO);
				statSearchForm.setDistribuzionePerSoggettoRemota(false);
				service.setDistribSoggettoSearch(statSearchForm);
				bean = new DistribuzionePerSoggettoBean<>(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((DistribuzionePerSoggettoBean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerSoggettoBean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerSoggettoBean<?>) bean).initSearchListenerLocale(null);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_SERVIZIO.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SERVIZIO);
				service.setDistribServizioSearch(statSearchForm);
				bean = new DistribuzionePerServizioBean<>(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((DistribuzionePerServizioBean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerServizioBean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerServizioBean<?>) bean).getSearch().initSearchListener(null); 
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_AZIONE.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_AZIONE);
				service.setDistribAzioneSearch(statSearchForm);
				bean = new DistribuzionePerAzioneBean<>(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((DistribuzionePerAzioneBean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerAzioneBean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerAzioneBean<?>) bean).getSearch().initSearchListener(null);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_APPLICATIVO.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO);
				statSearchForm.setRiconoscimento(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO);
				statSearchForm.setIdentificazione(identificazioneApplicativo);
				service.setDistribSaSearch(statSearchForm);
				bean = new DistribuzionePerSABean<>(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((DistribuzionePerSABean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerSABean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerSABean<?>) bean).getSearch().initSearchListener(null);
				// initSearchListener riazzera
				statSearchForm.setRiconoscimento(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_APPLICATIVO);
				statSearchForm.setIdentificazione(identificazioneApplicativo);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO);
				statSearchForm.setRiconoscimento(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO);
				service.setDistribSaSearch(statSearchForm);
				bean = new DistribuzionePerSABean<>(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((DistribuzionePerSABean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerSABean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerSABean<?>) bean).getSearch().initSearchListener(null);
				// initSearchListener riazzera
				statSearchForm.setRiconoscimento(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_IDENTIFICATIVO_AUTENTICATO);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_INDIRIZZO_IP.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO);
				statSearchForm.setRiconoscimento(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP);
				service.setDistribSaSearch(statSearchForm);
				bean = new DistribuzionePerSABean<>(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((DistribuzionePerSABean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerSABean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerSABean<?>) bean).getSearch().initSearchListener(null);
				// initSearchListener riazzera
				statSearchForm.setRiconoscimento(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_INDIRIZZO_IP);
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_TOKEN_INFO.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.DISTRIBUZIONE_SERVIZIO_APPLICATIVO);
				statSearchForm.setRiconoscimento(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO);
				statSearchForm.setTokenClaim(tokenClaim.getRawValue());
				service.setDistribSaSearch(statSearchForm);
				bean = new DistribuzionePerSABean<>(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((DistribuzionePerSABean<?>) bean).setStatisticheGiornaliereService(service);
				((DistribuzionePerSABean<?>) bean).setSearch(statSearchForm);
				((DistribuzionePerSABean<?>) bean).getSearch().initSearchListener(null);
				// initSearchListener riazzera
				statSearchForm.setRiconoscimento(org.openspcoop2.web.monitor.core.constants.Costanti.VALUE_TIPO_RICONOSCIMENTO_TOKEN_INFO);
				statSearchForm.setTokenClaim(tokenClaim.getRawValue());
			}
			else if(CostantiExporter.TIPO_DISTRIBUZIONE_PERSONALIZZATA.equals(tipoDistribuzioneReport)){
				statSearchForm.setTipoStatistica(TipoStatistica.STATISTICA_PERSONALIZZATA);
				service.setStatistichePersonalizzateSearch((StatistichePersonalizzateSearchForm)statSearchForm);
				bean = new StatsPersonalizzateBean(service.getUtilsServiceManager(), service.getPluginsServiceManager(),
						service.getDriverRegistroServiziDB(), service.getDriverConfigurazioneDB());
				((StatsPersonalizzateBean) bean).setStatisticheGiornaliereService(service);
				((StatsPersonalizzateBean) bean).setSearch(statSearchForm);
				((StatsPersonalizzateBean) bean).getSearch().initSearchListener(null);
			}
			
			ReportExporter.logDebug("Inizializzazione bean ["+tipoDistribuzioneReport+"] completata");

			ReportExporter.logDebug("Imposto parametri di ricerca nel search form ...");
			protocollo = setProtocolParametersInSearchForm(req, statSearchForm);
			setParametersInSearchForm(req, statSearchForm, protocollo);
			
			StringBuffer bf = new StringBuffer();
			ReflectionToStringBuilder builder = new ReflectionToStringBuilder(statSearchForm, ToStringStyle.MULTI_LINE_STYLE, bf, null, false, false);
			builder.toString();
			ReportExporter.logDebug("Lettura parametri completata, search form: "+bf.toString());
			
			
			ReportExporter.logDebug("Esportazione["+tipoFormato+"] tramite bean ["+tipoDistribuzioneReport+"] in corso ...");
			
			if(CostantiExporter.TIPO_FORMATO_CSV.equals(tipoFormato)){
				bean.esportaCsv(resp);
			}
			else if(CostantiExporter.TIPO_FORMATO_XLS.equals(tipoFormato)){
				bean.esportaXls(resp);
			}
			else if(CostantiExporter.TIPO_FORMATO_PDF.equals(tipoFormato)){
				bean.esportaPdf(resp);
			}
			else if(CostantiExporter.TIPO_FORMATO_XML.equals(tipoFormato)){
				bean.esportaXml(resp);
			}
			else if(CostantiExporter.TIPO_FORMATO_JSON.equals(tipoFormato)){
				bean.esportaJson(resp);
			}
			
			ReportExporter.logDebug("Esportazione["+tipoFormato+"] tramite bean ["+tipoDistribuzioneReport+"] completata");
			
		}catch(Exception e){
			ReportExporter.logError(e.getMessage(),e);
			throw e;
		}
	}

	private static String setProtocolParametersInSearchForm(HttpServletRequest req,StatsSearchForm statSearchForm) throws Exception{
		
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
		
		String soggettoLocale = req.getParameter(CostantiExporter.SOGGETTO_LOCALE);
		if(soggettoLocale!=null){
			soggettoLocale = soggettoLocale.trim();
			statSearchForm.setTipoNomeSoggettoLocale(soggettoLocale);
		}
		
		statSearchForm.saveProtocollo();
		
		return protocollo;
	}
	
	private static void setParametersInSearchForm(HttpServletRequest req,StatsSearchForm statSearchForm, String protocollo) throws ParameterUncorrectException, RegExpException, RegExpNotValidException, RegExpNotFoundException, ProtocolException {
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
				SimpleDateFormat sdf = DateUtils.getSimpleDateFormatMs();
				dInizio = sdf.parse(dataInizio);
			}
			else{
				SimpleDateFormat sdf = DateUtils.getSimpleDateFormatDay();
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
					+"' non valido. I formati supportati sono '"+DateUtils.SIMPLE_DATE_FORMAT_MS+"' o '"+DateUtils.SIMPLE_DATE_FORMAT_DAY+"'. Errore rilevato: "+e.getMessage(),e);
		}
		
		String dataFine = req.getParameter(CostantiExporter.DATA_FINE);
		if(dataFine==null){
			throw new ParameterUncorrectException("Parametro obbligatorio '"+CostantiExporter.DATA_FINE+"' non fornito");
		}
		dataFine=dataFine.trim();
		Date dFine = null;
		try{
			if(dataFine.contains(":")){
				SimpleDateFormat sdf = DateUtils.getSimpleDateFormatMs();
				dFine = sdf.parse(dataFine);
			}
			else{
				SimpleDateFormat sdf = DateUtils.getSimpleDateFormatDay();
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
					+"' non valido. I formati supportati sono '"+DateUtils.SIMPLE_DATE_FORMAT_MS+"' o '"+DateUtils.SIMPLE_DATE_FORMAT_DAY+"'. Errore rilevato: "+e.getMessage(),e);
		}
	
		statSearchForm.setDataInizio(dInizio);
		statSearchForm.setDataFine(dFine);
		
		
		
		// ** Tipologia Transazione **
		
		String tipologiaTransazione = req.getParameter(CostantiExporter.TIPOLOGIA);
		if(tipologiaTransazione!=null){
			tipologiaTransazione = tipologiaTransazione.trim();
			if(!CostantiExporter.getTipologie().contains(tipologiaTransazione)){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPOLOGIA+"' fornito possiede un valore '"+tipologiaTransazione
						+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getTipologie());
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
		
		
		
		// ** Id Cluster **
		
		String idCluster = req.getParameter(CostantiExporter.ID_CLUSTER);
		if(idCluster!=null){
			idCluster = idCluster.trim();
			statSearchForm.setClusterId(idCluster);
		}

		
		// ** Soggetto / Tag / Servizio / Azione **
					
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
				
		String trafficoPerSoggetto = req.getParameter(CostantiExporter.TRAFFICO_PER_SOGGETTO);
		if(trafficoPerSoggetto!=null){
			trafficoPerSoggetto = trafficoPerSoggetto.trim();
			statSearchForm.setTipoNomeTrafficoPerSoggetto(trafficoPerSoggetto);
		}
		
		String gruppo = req.getParameter(CostantiExporter.GRUPPO);
		if(gruppo!=null){
			gruppo = gruppo.trim();
			statSearchForm.setGruppo(gruppo);
		}
		
		String api = req.getParameter(CostantiExporter.API);
		if(api!=null){
			api = api.trim();
			String pattern = "^[a-z]{2,20}/[0-9A-Za-z]+:[_A-Za-z][\\-\\._A-Za-z0-9]*:\\d$";
			if(!RegularExpressionEngine.isMatch(api, pattern)) {
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.API+"' fornito possiede un valore '"+api+"' che non rispetta il formato atteso '"+pattern+"'");
			}
			statSearchForm.setApi(api);
		}
		
		String apiDistinguiImplementazione = req.getParameter(CostantiExporter.API_DISTINGUI_IMPLEMENTAZIONE);
		if(apiDistinguiImplementazione!=null){
			apiDistinguiImplementazione = apiDistinguiImplementazione.trim();
			if(CostantiExporter.API_DISTINGUI_IMPLEMENTAZIONE_TRUE.equalsIgnoreCase(apiDistinguiImplementazione)) {
				statSearchForm.setDistribuzionePerImplementazioneApi(true);
			}
			else if(CostantiExporter.API_DISTINGUI_IMPLEMENTAZIONE_FALSE.equalsIgnoreCase(apiDistinguiImplementazione)) {
				statSearchForm.setDistribuzionePerImplementazioneApi(false);
			}
			else {
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.API_DISTINGUI_IMPLEMENTAZIONE+"' contiene un valore non atteso '"+apiDistinguiImplementazione+"' (atteso: "+
						CostantiExporter.API_DISTINGUI_IMPLEMENTAZIONE_TRUE+"/"+CostantiExporter.API_DISTINGUI_IMPLEMENTAZIONE_FALSE+")");
			}
		}
		
		String servizio = req.getParameter(CostantiExporter.SERVIZIO);
		if(servizio!=null){
			servizio = servizio.trim();
			statSearchForm.setNomeServizio(servizio);
		}
		
		String azione = req.getParameter(CostantiExporter.AZIONE);
		if(azione!=null){
			azione = azione.trim();
			statSearchForm.setNomeAzione(azione);
		}
		
		
		// ** Mittente **
		
		String tipoRicercaMittente = req.getParameter(CostantiExporter.TIPO_RICERCA_MITTENTE);
		if(tipoRicercaMittente!=null){
			tipoRicercaMittente = tipoRicercaMittente.trim();
			if(!CostantiExporter.getTipiRicercaMittente().contains(tipoRicercaMittente)){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE+"' fornito possiede un valore '"+tipoRicercaMittente
						+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getTipiRicercaMittente());
			}
			statSearchForm.setRiconoscimento(tipoRicercaMittente);
			
			if(CostantiExporter.TIPO_RICERCA_MITTENTE_SOGGETTO.equals(tipoRicercaMittente)) {
				if(mittente==null || "".equals(mittente)) {
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE+"' valorizzato con '"+tipoRicercaMittente
							+"' richiede la definizione del parametro '"+CostantiExporter.MITTENTE+"'");
				}
			}
			
			else if(CostantiExporter.TIPO_RICERCA_MITTENTE_APPLICATIVO.equals(tipoRicercaMittente)) {
				if(CostantiExporter.TIPOLOGIA_EROGAZIONE.equals(tipologiaTransazione)){
					if(mittente==null || "".equals(mittente)) {
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE+"' valorizzato con '"+tipoRicercaMittente
								+"' richiede la definizione del parametro '"+CostantiExporter.MITTENTE+"'");
					}
				}
				else {
					String soggettoLocale = req.getParameter(CostantiExporter.SOGGETTO_LOCALE); // impostato precedentemente in setProtocolParametersInSearchForm
					if(soggettoLocale==null || "".equals(soggettoLocale)) {
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE+"' valorizzato con '"+tipoRicercaMittente
								+"' richiede la definizione del parametro '"+CostantiExporter.SOGGETTO_LOCALE+"'");
					}
				}
				
				String tipoIdentificazioneApplicativo = req.getParameter(CostantiExporter.TIPO_IDENTIFICAZIONE_APPLICATIVO);
				if(tipoIdentificazioneApplicativo!=null){
					tipoIdentificazioneApplicativo = tipoIdentificazioneApplicativo.trim();
					if(!CostantiExporter.getTipiIdentificazioneApplicativo().contains(tipoIdentificazioneApplicativo)){
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_IDENTIFICAZIONE_APPLICATIVO+"' fornito possiede un valore '"+tipoIdentificazioneApplicativo
								+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getTipiIdentificazioneApplicativo());
					}
					if(CostantiExporter.TIPO_IDENTIFICAZIONE_APPLICATIVO_TRASPORTO.equals(tipoIdentificazioneApplicativo)) {
						statSearchForm.setIdentificazione(Costanti.IDENTIFICAZIONE_TRASPORTO_KEY);	
					}
					else if(CostantiExporter.TIPO_IDENTIFICAZIONE_APPLICATIVO_TOKEN.equals(tipoIdentificazioneApplicativo)) {
						statSearchForm.setIdentificazione(Costanti.IDENTIFICAZIONE_TOKEN_KEY);		
					}
				}
				else {
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE+"' valorizzato con '"+tipoRicercaMittente
							+"' richiede la definizione del parametro '"+CostantiExporter.TIPO_IDENTIFICAZIONE_APPLICATIVO+"'");
				}
				
				String applicativo = req.getParameter(CostantiExporter.APPLICATIVO);
				if(applicativo!=null){
					applicativo = applicativo.trim();
					statSearchForm.setServizioApplicativo(applicativo);
				}
				else {
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE+"' valorizzato con '"+tipoRicercaMittente
							+"' richiede la definizione del parametro '"+CostantiExporter.APPLICATIVO+"'");
				}
			}
			
			else if(CostantiExporter.TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO.equals(tipoRicercaMittente) ||
					CostantiExporter.TIPO_RICERCA_MITTENTE_TOKEN_INFO.equals(tipoRicercaMittente) ||
					CostantiExporter.TIPO_RICERCA_MITTENTE_INDIRIZZO_IP.equals(tipoRicercaMittente)) {
			
				if(CostantiExporter.TIPO_RICERCA_MITTENTE_IDENTIFICATIVO_AUTENTICATO.equals(tipoRicercaMittente)) {
					String tipoAutenticazione = req.getParameter(CostantiExporter.TIPO_AUTENTICAZIONE);
					if(tipoAutenticazione!=null){
						tipoAutenticazione = tipoAutenticazione.trim();
						if(!CostantiExporter.getTipiAutenticazione().contains(tipoAutenticazione)){
							throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_AUTENTICAZIONE+"' fornito possiede un valore '"+tipoAutenticazione
									+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getTipiAutenticazione());
						}
						statSearchForm.setAutenticazione(tipoAutenticazione);
					}
					else {
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE+"' valorizzato con '"+tipoRicercaMittente
								+"' richiede la definizione del parametro '"+CostantiExporter.TIPO_AUTENTICAZIONE+"'");
					}
				}
				else if(CostantiExporter.TIPO_RICERCA_MITTENTE_INDIRIZZO_IP.equals(tipoRicercaMittente)) {
					String tipoIndirizzoIP = req.getParameter(CostantiExporter.TIPO_INDIRIZZO_IP);
					if(tipoIndirizzoIP!=null){
						tipoIndirizzoIP = tipoIndirizzoIP.trim();
						if(!CostantiExporter.getTipiIndirizzoIp().contains(tipoIndirizzoIP)){
							throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_INDIRIZZO_IP+"' fornito possiede un valore '"+tipoIndirizzoIP
									+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getTipiIndirizzoIp());
						}
						statSearchForm.setClientAddressMode(tipoIndirizzoIP);
					}
					else {
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE+"' valorizzato con '"+tipoRicercaMittente
								+"' richiede la definizione del parametro '"+CostantiExporter.TIPO_AUTENTICAZIONE+"'");
					}
				}
				else {
					String tipoClaim = req.getParameter(CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM);
					if(tipoClaim!=null){
						tipoClaim = tipoClaim.trim();
						if(!CostantiExporter.getClaims().contains(tipoClaim)){
							throw new ParameterUncorrectException("Parametro '"+CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM+"' fornito possiede un valore '"+tipoClaim
									+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getClaims());
						}
						TipoCredenzialeMittente tokenClaim = null;
						if(CostantiExporter.CLAIM_ISSUER.equals(tipoClaim)) {
							tokenClaim  = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_ISSUER;
						}
						else if(CostantiExporter.CLAIM_SUBJECT.equals(tipoClaim)) {
							tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_SUBJECT;
						}
						else if(CostantiExporter.CLAIM_USERNAME.equals(tipoClaim)) {
							tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_USERNAME;
						}
						else if(CostantiExporter.CLAIM_CLIENT_ID.equals(tipoClaim)) {
							tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_CLIENT_ID;
						}
						else if(CostantiExporter.CLAIM_EMAIL.equals(tipoClaim)) {
							tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.TOKEN_EMAIL;
						}
						else if(CostantiExporter.CLAIM_PDND_ORGANIZATION_NAME.equals(tipoClaim)) {
							tokenClaim = org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente.PDND_ORGANIZATION_NAME;
						}
						else {
							TipoCredenzialeMittente [] values = TipoCredenzialeMittente.values();
							StringBuilder sb = new StringBuilder();
							if(values!=null && values.length>0) {
								for (TipoCredenzialeMittente tipoCredenzialeMittente : values) {
									if(sb.length()>0) {
										sb.append(",");
									}
									sb.append(tipoCredenzialeMittente);
								}
							}
							throw new ParameterUncorrectException("Parametro '"+CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM+"' fornito possiede un valore '"+tipoClaim
									+"' sconosciuto. I tipi supportati sono: "+ sb.toString());
						}

						statSearchForm.setTokenClaim(tokenClaim.getRawValue());
					}
					else {
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE+"' valorizzato con '"+tipoRicercaMittente
								+"' richiede la definizione del parametro '"+CostantiExporter.RICERCA_MITTENTE_TIPO_CLAIM+"'");
					}					
				}
				
				
				String tipoMatch = req.getParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA);
				TipoMatch tipoMatchEnum = TipoMatch.EQUALS;
				if(tipoMatch!=null){
					tipoMatch = tipoMatch.trim();
					if(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA_TRUE.equalsIgnoreCase(tipoMatch)) {
						tipoMatchEnum = TipoMatch.EQUALS;
					}
					else if(CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA_FALSE.equalsIgnoreCase(tipoMatch)) {
						tipoMatchEnum = TipoMatch.LIKE;
					}
					else {
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA+"' contiene un valore non atteso '"+tipoMatch+"' (atteso: "+
								CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA_TRUE+"/"+CostantiExporter.TIPO_RICERCA_MITTENTE_ESATTA_FALSE+")");
					}
				}
				statSearchForm.setMittenteMatchingType(tipoMatchEnum.name());
				
				String tipoCaseSensitive = req.getParameter(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE);
				CaseSensitiveMatch tipoCaseSensitiveEnum = CaseSensitiveMatch.SENSITIVE;
				if(tipoCaseSensitive!=null){
					tipoCaseSensitive = tipoCaseSensitive.trim();
					if(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE_TRUE.equalsIgnoreCase(tipoCaseSensitive)) {
						tipoCaseSensitiveEnum = CaseSensitiveMatch.SENSITIVE;
					}
					else if(CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE_FALSE.equalsIgnoreCase(tipoCaseSensitive)) {
						tipoCaseSensitiveEnum = CaseSensitiveMatch.INSENSITIVE;
					}
					else {
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE+"' contiene un valore non atteso '"+tipoCaseSensitive+"' (atteso: "+
								CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE_TRUE+"/"+CostantiExporter.TIPO_RICERCA_MITTENTE_CASE_SENSITIVE_FALSE+")");
					}
				}
				statSearchForm.setMittenteCaseSensitiveType(tipoCaseSensitiveEnum.name());
				
				String identificativoRicercato = req.getParameter(CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE);
				if(identificativoRicercato!=null){
					identificativoRicercato = identificativoRicercato.trim();
					statSearchForm.setValoreRiconoscimento(identificativoRicercato);
				}
				else {
					throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_RICERCA_MITTENTE+"' valorizzato con '"+tipoRicercaMittente
							+"' richiede la definizione del parametro '"+CostantiExporter.IDENTIFICATIVO_RICERCA_MITTENTE+"'");
				}

			}
		}
		
		
		
		
		
		
		// ** Esito **
		
		EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(log, protocollo);

		statSearchForm.setEsitoGruppo(EsitoUtils.ALL_VALUE);
		statSearchForm.setEsitoDettaglio(EsitoUtils.ALL_VALUE);
		statSearchForm.setEscludiRichiesteScartate(true);
		
		String esitoGruppo = req.getParameter(CostantiExporter.ESITO_GRUPPO);
		if(esitoGruppo!=null){
			esitoGruppo = esitoGruppo.trim();
			if(!CostantiExporter.getEsitiGruppo().contains(esitoGruppo)){
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.ESITO_GRUPPO+"' fornito possiede un valore '"+esitoGruppo
						+"' sconosciuto. I tipi supportati sono: "+CostantiExporter.getEsitiGruppo());
			}
			if(CostantiExporter.ESITO_GRUPPO_OK.equals(esitoGruppo)){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_OK_VALUE);
			}
			else if(CostantiExporter.ESITO_GRUPPO_FAULT_APPLICATIVO.equals(esitoGruppo)){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE);
			}
			else if(CostantiExporter.ESITO_GRUPPO_FALLITE.equals(esitoGruppo)){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_ERROR_VALUE);
			}
			else if(CostantiExporter.ESITO_GRUPPO_FALLITE_E_FAULT_APPLICATIVO.equals(esitoGruppo)){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE);
			}
			else if(CostantiExporter.ESITO_GRUPPO_ERRORI_CONSEGNA.equals(esitoGruppo)){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_ERROR_CONSEGNA_VALUE);
			}
			else if(CostantiExporter.ESITO_GRUPPO_RICHIESTE_SCARTATE.equals(esitoGruppo)){
				statSearchForm.setEsitoGruppo(EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE);
			}
		}
		
		String escludiRichiesteScartate = req.getParameter(CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE);
		if(escludiRichiesteScartate!=null){
			escludiRichiesteScartate = escludiRichiesteScartate.trim();
			if(!"false".equals(escludiRichiesteScartate) && !"true".equals(escludiRichiesteScartate)) {
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.ESCLUDI_RICHIESTE_SCARTATE+"' fornito possiede un valore '"+escludiRichiesteScartate
						+"' non valido. Atteso true/false");
			}
			statSearchForm.setEscludiRichiesteScartate(Boolean.valueOf(escludiRichiesteScartate));
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
				if(!esitiProperties.existsEsitoCode(intValueEsito)){
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
			if(!esitiProperties.getEsitiTransactionContextCode().contains(esitoContestoTmp)){
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
/**		String tipoReport = req.getParameter(CostantiExporter.TIPO_REPORT);
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
//		}*/
		statSearchForm.setTipoReport(tipoReportEnum);
		
		
		
		// ** tipo informazione visualizzata **
		
		StatisticType modalitaTemporaleEnum = StatisticType.GIORNALIERA;
		String modalitaTemporale = req.getParameter(CostantiExporter.TIPO_UNITA_TEMPORALE);
		if(modalitaTemporale!=null){
			modalitaTemporale = modalitaTemporale.trim();
			modalitaTemporaleEnum = StatisticType.valueOf(modalitaTemporale.toUpperCase());
			if(modalitaTemporaleEnum==null){
				List<String> types = new ArrayList<>();
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
				String [] values = TipoVisualizzazione.toArray();
				StringBuilder sb = getAsStringBuilder(values);
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_INFORMAZIONE_VISUALIZZATA+"' fornito possiede un valore '"+tipoVisualizzazione
						+"' sconosciuto. I tipi supportati sono: "+sb.toString());
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
						String [] values = TipoBanda.toArray();
						StringBuilder sb = getAsStringBuilder(values);
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_BANDA_VISUALIZZATA+"' fornito possiede un valore '"+tmp[i]
								+"' sconosciuto. I tipi supportati sono: "+sb.toString());
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
						String [] values = TipoBanda.toArray();
						StringBuilder sb = getAsStringBuilder(values);
						throw new ParameterUncorrectException("Parametro '"+CostantiExporter.TIPO_LATENZA_VISUALIZZATA+"' fornito possiede un valore '"+tmp[i]
								+"' sconosciuto. I tipi supportati sono: "+sb.toString());
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
		
		
		
		// ** dimensioni visualizzate **
		
		String dimensioniVisualizzate = req.getParameter(CostantiExporter.DIMENSIONI_VISUALIZZATE);
		NumeroDimensioni dimensioniVisualizzateEnum = CostantiExporter.DIMENSIONI_VISUALIZZATE_DEFAULT;
		if(dimensioniVisualizzate!=null){
			dimensioniVisualizzate = dimensioniVisualizzate.trim();
			dimensioniVisualizzateEnum = NumeroDimensioni.toEnumConstant(dimensioniVisualizzate);
			if(dimensioniVisualizzateEnum==null){
				String [] values = NumeroDimensioni.toArray();
				StringBuilder sb = getAsStringBuilder(values);
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.DIMENSIONI_VISUALIZZATE+"' fornito possiede un valore '"+dimensioniVisualizzate
						+"' sconosciuto. I tipi supportati sono: "+sb.toString());
			}
		}
		statSearchForm.setNumeroDimensioni(dimensioniVisualizzateEnum);
		
		
		String dimensioniInfoCustom = req.getParameter(CostantiExporter.DIMENSIONE_INFO_CUSTOM);
		DimensioneCustom dimensioniInfoCustomEnum = null;
		if(dimensioniInfoCustom!=null){
			dimensioniInfoCustom = dimensioniInfoCustom.trim();
			dimensioniInfoCustomEnum = DimensioneCustom.toEnumConstant(dimensioniInfoCustom);
			if(dimensioniInfoCustomEnum==null){
				String [] values = DimensioneCustom.toArray();
				StringBuilder sb = getAsStringBuilder(values);
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.DIMENSIONE_INFO_CUSTOM+"' fornito possiede un valore '"+dimensioniInfoCustom
						+"' sconosciuto. I tipi supportati sono: "+sb.toString());
			}
			statSearchForm.setNumeroDimensioniCustom(dimensioniInfoCustomEnum);
			try {
				SortedMap<String> map = statSearchForm.getDimensioniCustomDisponibiliAsMap();
				if(!map.containsKey(dimensioniInfoCustomEnum.toString())) {
					throw new ParameterUncorrectException("Informazione custom '"+dimensioniInfoCustomEnum.toString()+"' non supportata dal tipo di report");
				}
			}catch(Exception e) {
				throw new ParameterUncorrectException("Parametro '"+CostantiExporter.DIMENSIONE_INFO_CUSTOM+"' non corretto: "+e.getMessage(),e);
			}
		}
		statSearchForm.setNumeroDimensioniCustom(dimensioniInfoCustomEnum);
		
	}
	

	private static StringBuilder getAsStringBuilder(String [] values) {
		StringBuilder sb = new StringBuilder();
		if(values!=null && values.length>0) {
			for (String tipo : values) {
				if(sb.length()>0) {
					sb.append(",");
				}
				sb.append(tipo);
			}
		}
		return sb;
	}
}

class ParameterUncorrectException extends Exception {

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
