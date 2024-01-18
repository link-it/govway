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
package org.openspcoop2.web.monitor.statistiche.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGenerale;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioneGeneralePK;
import org.openspcoop2.web.monitor.statistiche.bean.ConfigurazioniGeneraliSearchForm;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiConfigurazioni;
import org.openspcoop2.web.monitor.statistiche.constants.CostantiExporter;
import org.openspcoop2.web.monitor.statistiche.dao.IConfigurazioniGeneraliService;
import org.openspcoop2.web.monitor.statistiche.export.ConfigurazioniCsvExporter;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * ConfigurazioniExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ConfigurazioniExporter extends HttpServlet{

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
			ConfigurazioniExporter.log.error("Inizializzazione servlet fallita: "+e.getMessage(),e);
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
			
			IConfigurazioniGeneraliService service = (IConfigurazioniGeneraliService)context.getBean("configurazioniGeneraliService");
			ConfigurazioniGeneraliSearchForm sfInSession = (ConfigurazioniGeneraliSearchForm)context.getBean("configurazioniGeneraliSearchForm");
			ConfigurazioniGeneraliSearchForm searchForm = (ConfigurazioniGeneraliSearchForm)sfInSession.clone();
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

			String isAllString = req.getParameter(CostantiConfigurazioni.PARAMETER_IS_ALL);
			Boolean isAll = Boolean.parseBoolean(isAllString);
			String idtransazioni=req.getParameter(CostantiConfigurazioni.PARAMETER_IDS);
			String[] ids = StringUtils.split(idtransazioni, ",");
			String ruoloS =req.getParameter(CostantiConfigurazioni.PARAMETER_RUOLO);
			String formato =req.getParameter(CostantiExporter.TIPO_FORMATO_CONFIGURAZIONE);

			//Check Parametri di export
			HttpSession sessione = req.getSession();

			// Prelevo i parametri necessari
			Boolean isAllFromSession = (Boolean) sessione.getAttribute(CostantiConfigurazioni.PARAMETER_IS_ALL_ORIGINALE);
			String idTransazioniFromSession = (String) sessione.getAttribute(CostantiConfigurazioni.PARAMETER_IDS_ORIGINALI);
			String ruoloSFromSession = (String) sessione.getAttribute(CostantiConfigurazioni.PARAMETER_RUOLO_ORIGINALE);
			String formatoFromSession = (String) sessione.getAttribute(CostantiExporter.PARAMETER_FORMATO_EXPORT_ORIGINALE);

			//Rimuovo i parametri utilizzati dalla sessione
			sessione.removeAttribute(CostantiConfigurazioni.PARAMETER_IS_ALL_ORIGINALE);
			sessione.removeAttribute(CostantiConfigurazioni.PARAMETER_IDS_ORIGINALI);
			sessione.removeAttribute(CostantiConfigurazioni.PARAMETER_RUOLO_ORIGINALE);
			sessione.removeAttribute(CostantiExporter.PARAMETER_FORMATO_EXPORT_ORIGINALE); 

			String[] idsFromSession = StringUtils.split(idTransazioniFromSession, ",");
			PddRuolo ruolo = PddRuolo.toEnumConstant(ruoloS);

			//I parametri in sessione devono coincidere con quelli della request
			boolean exportConsentito = ConfigurazioniExporter.checkParametri(isAll,isAllFromSession,ids,idsFromSession,ruoloS,ruoloSFromSession)
					&& ConfigurazioniExporter.checkFormatoExport(formato, formatoFromSession);

			if(!exportConsentito){
				String msgErrore = "L'utente non dispone dei permessi necessari per effettuare l'export delle configurazioni.";
				String redirectUrl = req.getContextPath()+"/public/error.jsf?msg_errore=" + msgErrore;

				response.sendRedirect(redirectUrl);
				return;
			}
			
			export(req, response, isAllFromSession, service, idsFromSession, ruolo, formato);
			
		}catch(Exception e){
			logError(e.getMessage(),e);
		}
		
	}
	
	public static void export(HttpServletRequest req, HttpServletResponse response, Boolean isAll, IConfigurazioniGeneraliService service,
			String[] ids, PddRuolo ruolo, String formato) throws ServletException,IOException{
		try{
			
			// fase 1 lettura delle configurazioni
			
			List<ConfigurazioneGenerale> lst = new ArrayList<ConfigurazioneGenerale>();
			if(isAll!=null && isAll.booleanValue()) { // findall
				int offset = 0;
				int letti = 0;
				int limit = 25;
				
				List<ConfigurazioneGenerale> lstTmp = null;
				do{
					lstTmp = service.findAllDettagli(offset, limit);
					letti = lstTmp.size();
					if(letti > 0){
						lst.addAll(lstTmp);
						for (ConfigurazioneGenerale configurazioneGenerale : lstTmp) {
							if(configurazioneGenerale.isDatiPortaPrincipale()) {
								offset++;
							}
						}
					}
				}while(letti > 0);
			} else {
				for (String idString : ids) {
					ConfigurazioneGeneralePK key = new ConfigurazioneGeneralePK(idString);
					ConfigurazioneGenerale findById = service.findById(key);
					lst.add(findById);
					
					List<ConfigurazioneGenerale> findConfigurazioniFiglie = service.findConfigurazioniFiglie(findById.getNome(), findById.getRuolo());
					if(findConfigurazioniFiglie != null && findConfigurazioniFiglie.size() > 0)
						lst.addAll(findConfigurazioniFiglie);
						
				}
			}

			if(!lst.isEmpty()) {
				String mimeType = MimeTypes.getInstance().getMimeType(formato);
				String ext = MimeTypeUtils.fileExtensionForMIMEType(mimeType);
				String fileName = "ConfigurazioneServizi" +"."+ ext;

				// Setto Proprietà Export File
				HttpUtilities.setOutputFile(response, true, fileName);

				// committing status and headers
				response.setStatus(200);
				response.flushBuffer();
				
				ConfigurazioniCsvExporter exporter = new ConfigurazioniCsvExporter(log,ruolo, formato);
				
				exporter.exportConfigurazioni(lst, response.getOutputStream());
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

		}catch(Exception e){
			logError(e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
	}	

	private static boolean checkParametri(Boolean isAll, Boolean isAllFromSession,
			String[] ids, String[] idsFromSession, String ruoloS, String ruoloSFromSession) {
		// Il valore di is All deve essere coincidente
		if(isAll == null || isAllFromSession == null || isAll.booleanValue() != isAllFromSession.booleanValue())
			return false;

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
		
		if(StringUtils.isEmpty(ruoloS) || StringUtils.isEmpty(ruoloSFromSession))
			return false;
		
		return ruoloS.equals(ruoloSFromSession);

	}
	
	public static boolean checkFormatoExport(String formato, String formatoFromSession){
		
		if(formato == null ||  formatoFromSession == null)
			return false;
		
		return formato.equals(formatoFromSession);
	}
}
