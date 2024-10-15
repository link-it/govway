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
package org.openspcoop2.web.monitor.core.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.mime.MimeTypes;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.lib.users.dao.RicercaUtente;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.bean.RicercaUtenteBean;
import org.openspcoop2.web.monitor.core.bean.RicercheUtenteSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.dao.IRicercheUtenteService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.ricerche.RicerchePersonalizzate;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * RicercheExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RicercheExporter extends HttpServlet {

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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.processRequest(req,resp);		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		this.processRequest(req,resp);		
	}


	private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		try{
			ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			if(context==null) {
				throw new CoreException("Context is null");
			}
			
			IRicercheUtenteService service = (IRicercheUtenteService)context.getBean("ricercheUtenteService");


			RicercheUtenteSearchForm sfInSession = (RicercheUtenteSearchForm)context.getBean("searchFormRicercheUtente");
			RicercheUtenteSearchForm searchForm = (RicercheUtenteSearchForm)sfInSession.clone();

			// prelevo le informazioni sull'utente loggato
			User utente =null;
			LoginBean lbInSession = (LoginBean) context.getBean(org.openspcoop2.web.monitor.core.bean.AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);
			if(lbInSession != null && lbInSession.isLoggedIn()) {
				utente = lbInSession.getUtente();
			}
			searchForm.setUser(utente);
			service.setSearch(searchForm);
			
			// Then we have to get the Response where to write our file
			HttpServletResponse response = resp;

			String isAllString = req.getParameter(Costanti.PARAMETER_IS_ALL);
			Boolean isAll = Boolean.parseBoolean(isAllString);
			String idRicerche=req.getParameter(Costanti.PARAMETER_IDS);
			String[] ids = StringUtils.split(idRicerche, ",");

			//Check Parametri di export
			HttpSession sessione = req.getSession();

			// Prelevo i parametri necessari
			Boolean isAllFromSession = (Boolean) sessione.getAttribute(Costanti.PARAMETER_IS_ALL_ORIGINALE);
			String idRicercheFromSession = (String) sessione.getAttribute(Costanti.PARAMETER_IDS_ORIGINALI);

			//Rimuovo i parametri utilizzati dalla sessione
			sessione.removeAttribute(Costanti.PARAMETER_IS_ALL_ORIGINALE);
			sessione.removeAttribute(Costanti.PARAMETER_IDS_ORIGINALI);

			String[] idsFromSession = StringUtils.split(idRicercheFromSession, ",");
			
			//I parametri in sessione devono coincidere con quelli della request
			boolean exportConsentito = RicercheExporter.checkParametri(isAll,isAllFromSession,ids,idsFromSession);					

			if(!exportConsentito){
				String msgErrore = "L'utente non dispone dei permessi necessari per effettuare l'export delle ricerche.";
				String redirectUrl = req.getContextPath()+"/public/error.jsf?msg_errore=" + msgErrore;

				response.sendRedirect(redirectUrl);
				return;
			}
			
			export(response, isAllFromSession, service, idsFromSession);
			
		}catch(Exception e){
			logError(e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}finally {
			// donothing
		}
	}
	
	public static void export(HttpServletResponse response, Boolean isAll, IRicercheUtenteService service,
			String[] ids) throws ServletException,IOException{
		try{
			
			// fase 1 lettura delle configurazioni
			
			List<RicercaUtente> lst = new ArrayList<>();
			if(isAll!=null && isAll.booleanValue()) { // findall
				int offset = 0;
				int letti = 0;
				int limit = 25;
				
				List<RicercaUtenteBean> lstTmp = null;
				do{
					lstTmp = service.findAll(offset, limit);
					letti = lstTmp.size();
					if(letti > 0){
						lst.addAll(lstTmp);
					}
				}while(letti > 0);
			} else {
				for (String idString : ids) {
					RicercaUtenteBean findById = service.findById(Long.parseLong(idString));
					lst.add(findById);
				}
			}

			if(!lst.isEmpty()) {
				String mimeType = MimeTypes.getInstance().getMimeType("json");
				String ext = MimeTypeUtils.fileExtensionForMIMEType(mimeType);
				String fileName = "ricerche_personalizzate" +"."+ ext;

				// Setto Proprietà Export File
				HttpUtilities.setOutputFile(response, true, fileName);

				// committing status and headers
				response.setStatus(200);
				response.flushBuffer();
				
				RicerchePersonalizzate ricerchePersonalizzate = new RicerchePersonalizzate();
				ricerchePersonalizzate.setRicerche(lst);
				
				JSONUtils jsonUtils = JSONUtils.getInstance();
				
				jsonUtils.writeTo(ricerchePersonalizzate, response.getOutputStream());
				
				// committing status and headers
				response.setStatus(200);
				response.flushBuffer();
			} else {
				String fileName = "Errors.txt";

				// Setto Proprietà Export File
				HttpUtilities.setOutputFile(response, true, fileName);

				// committing status and headers
				response.setStatus(200);
				response.flushBuffer();

				String msgErrore = "La configurazione di export selezionata non ha prodotto nessun json valido";
				response.getWriter().write(msgErrore);
			}

		}catch(Exception e){
			logError(e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
	}	
	
	private static boolean checkParametri(Boolean isAll, Boolean isAllFromSession, String[] ids, String[] idsFromSession) {
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
		
		return true;

	}
	
}
