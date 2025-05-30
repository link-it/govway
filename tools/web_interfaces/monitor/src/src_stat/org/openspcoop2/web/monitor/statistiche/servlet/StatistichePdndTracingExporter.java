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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean;
import org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;
import org.openspcoop2.web.monitor.statistiche.dao.IStatistichePdndTracingService;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * StatistichePdndTracingExporter
 * 
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatistichePdndTracingExporter extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerManager.getPddMonitorCoreLogger();
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

	@Override
	public void init() throws ServletException {
		try{
			// nop
		}catch(Exception e){
			StatistichePdndTracingExporter.log.error("Inizializzazione servlet fallita: "+e.getMessage(),e);
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

	private void processRequest(HttpServletRequest req, HttpServletResponse resp) {
		try{
			ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			if(context==null) {
				throw new CoreException("Param context is null");
			}

			IStatistichePdndTracingService service = (IStatistichePdndTracingService)context.getBean("statistichePdndTracingService");
			StatistichePdndTracingSearchForm sfInSession = (StatistichePdndTracingSearchForm)context.getBean("statistichePdndTracingSearchForm");
			StatistichePdndTracingSearchForm searchForm = (StatistichePdndTracingSearchForm)sfInSession.clone();
			// prelevo le informazioni sull'utente loggato
			User utente =null;
			String modalita = null;
			LoginBean lbInSession = (LoginBean) context.getBean(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);
			if(lbInSession != null && lbInSession.isLoggedIn()) {
				utente = lbInSession.getUtente();
				modalita = lbInSession.getModalita();
			}
			searchForm.setUser(utente);
			searchForm.setModalita(modalita);
			searchForm.saveProtocollo();
			service.setSearch(searchForm);


			// Then we have to get the Response where to write our file
			HttpServletResponse response = resp;

			String isAllString = req.getParameter(StatisticheCostanti.PARAMETER_IS_ALL);
			Boolean isAll = Boolean.parseBoolean(isAllString);
			String idtransazioni=req.getParameter(StatisticheCostanti.PARAMETER_IDS);
			String[] ids = StringUtils.split(idtransazioni, ",");

			//Check Parametri di export
			HttpSession sessione = req.getSession();

			// Prelevo i parametri necessari
			Boolean isAllFromSession = (Boolean) sessione.getAttribute(StatisticheCostanti.PARAMETER_IS_ALL_ORIGINALE);
			String idTransazioniFromSession = (String) sessione.getAttribute(StatisticheCostanti.PARAMETER_IDS_ORIGINALI);

			//Rimuovo i parametri utilizzati dalla sessione
			sessione.removeAttribute(StatisticheCostanti.PARAMETER_IS_ALL_ORIGINALE);
			sessione.removeAttribute(StatisticheCostanti.PARAMETER_IDS_ORIGINALI);

			String[] idsFromSession = StringUtils.split(idTransazioniFromSession, ",");

			//I parametri in sessione devono coincidere con quelli della request
			boolean exportConsentito = StatistichePdndTracingExporter.checkParametroIsAll(isAll,isAllFromSession)
					&& StatistichePdndTracingExporter.checkParametroIds(isAll,ids,idsFromSession);

			if(!exportConsentito){
				String msgErrore = "L'utente non dispone dei permessi necessari per effettuare l'export dei csv.";
				String redirectUrl = req.getContextPath()+"/public/error.jsf?msg_errore=" + msgErrore;

				response.sendRedirect(redirectUrl);
				return;
			}

			export(response, isAllFromSession, service, idsFromSession);

		}catch(Exception e){
			logError(e.getMessage(),e);
		}

	}

	public static void export(HttpServletResponse response, Boolean isAll, IStatistichePdndTracingService service,	String[] ids) throws ServletException,IOException{
		try{

			List<StatistichePdndTracingBean> lst = leggiCsvDaEsportare(isAll, service, ids);

			if(!lst.isEmpty()) {
				esportaRisultati(lst, response);
			} else {
				String fileName = "Errors.txt";

				// Setto Proprietà Export File
				HttpUtilities.setOutputFile(response, true, fileName);

				// committing status and headers
				response.setStatus(200);
				response.flushBuffer();

				String msgErrore = "La configurazione di export selezionata non ha prodotto nessun csv valido";
				response.getWriter().write(msgErrore);
			}

		}catch(IOException | UtilsException e){
			logError(e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
	}

	private static void esportaRisultati(List<StatistichePdndTracingBean> lst, HttpServletResponse response) throws IOException, UtilsException {
		String fileName = "StatisticheTracingPdnd.zip";
		String contentType = "text/csv";
		String rootDir = "StatisticheTracingPdnd"+File.separatorChar;
		try {
			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName, contentType);
			
			ZipOutputStream zip = new  ZipOutputStream(response.getOutputStream());
			
			for (StatistichePdndTracingBean statistichePdndTracingBean : lst) {
				String entryFileName = getCsvFileName(statistichePdndTracingBean);
				String soggettoDirName = getNomeDirectorySoggetto(statistichePdndTracingBean);
				
				String nomeEntry = rootDir + soggettoDirName+ File.separatorChar + entryFileName;
				zip.putNextEntry(new ZipEntry(nomeEntry));
				
				try (ByteArrayInputStream bais = new ByteArrayInputStream(statistichePdndTracingBean.getCsv())){
					CopyStream.copy(bais, zip);
				}

				zip.flush();
				zip.closeEntry();
			}
			
			zip.flush();
			zip.close();

			// committing status and headers
			response.setStatus(200);
			response.flushBuffer();

			// End of the method
		}catch (IOException | UtilsException e) {
			logError(e.getMessage(),e);
			throw e;
		}
	}

	private static List<StatistichePdndTracingBean> leggiCsvDaEsportare(Boolean isAll, IStatistichePdndTracingService service, String[] ids) {

		List<StatistichePdndTracingBean> lst = new ArrayList<>();
		if(isAll!=null && isAll.booleanValue()) { // findall
			int offset = 0;
			int letti = 0;
			int limit = 25;

			List<StatistichePdndTracingBean> lstTmp = null;
			do{
				lstTmp = service.findAll(offset, limit);
				letti = lstTmp.size();
				if(letti > 0){
					lst.addAll(lstTmp);
					offset++;
				}
			}while(letti > 0);
		} else {
			for (String idString : ids) {
				StatistichePdndTracingBean findById = service.findById(Long.parseLong(idString));
				lst.add(findById);
			}
		}
		return lst;
	}	

	private static boolean checkParametroIsAll(Boolean isAll, Boolean isAllFromSession) {
		// Il valore di is All deve essere coincidente
		return !(isAll == null || isAllFromSession == null || isAll.booleanValue() != isAllFromSession.booleanValue());
	}

	private static boolean checkParametroIds(Boolean isAll, String[] ids, String[] idsFromSession) {

		// Se l'array degli id ricevuto dalla richiesta e' vuoto o null non si puo' fare l'export
		// e se l'array degli id in sessione non e' presente o e' vuoto non si puo' fare export.
		if( 
				(ids == null || ids.length == 0 || idsFromSession ==null || idsFromSession.length == 0)
				&&
				(!isAll)
				) {
			return false;
		}

		// numero di id ricevuti non coincidente.
		if(ids!=null && idsFromSession!=null && ids.length != idsFromSession.length)
			return false;

		return checkMatchParametroIds(ids, idsFromSession);

	}

	private static boolean checkMatchParametroIds(String[] ids, String[] idsFromSession) {
		if(ids!=null) {
			for (String id : ids) {
				boolean found = false;
				for (String idFromSession : idsFromSession) {
					if(id.equals(idFromSession)){
						found = true;
						break;
					}
				}	

				// Se l'id ricevuto dalla richiesta non e' tra quelli consentiti allora non puoi accedere alla risorsa
				if(!found)
					return false;
			}
		}

		return true;
	}

	public static String getNomeDirectorySoggetto(org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean statistichePdndTracingBean) {
		return statistichePdndTracingBean.getSoggettoReadable().replace("/", "_");
	}
	
	public static String getCsvFileName(org.openspcoop2.web.monitor.statistiche.bean.StatistichePdndTracingBean statistichePdndTracingBean) {
		String dataTracciamentoAsString = DateUtils.getDefaultDateTimeFormatterDay().format(statistichePdndTracingBean.getDataTracciamento());

		String tracingId = StringUtils.isNotEmpty(statistichePdndTracingBean.getTracingId()) ? statistichePdndTracingBean.getTracingId() : "csv_"+ statistichePdndTracingBean.getId();
		return dataTracciamentoAsString + "_" + tracingId + ".csv";
	}
}
