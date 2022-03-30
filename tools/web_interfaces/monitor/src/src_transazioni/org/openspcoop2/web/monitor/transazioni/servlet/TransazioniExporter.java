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
package org.openspcoop2.web.monitor.transazioni.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.exporter.CostantiExport;
import org.openspcoop2.web.monitor.transazioni.exporter.ExporterProperties;
import org.openspcoop2.web.monitor.transazioni.exporter.SingleFileExporter;

/**
 * TransazioniExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TransazioniExporter extends HttpServlet{

	private static final long serialVersionUID = 1272767433184676700L;
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private static Boolean enableHeaderInfo = false;
	private static Boolean enableConsegneInfo = false;
	private static Boolean mimeThrowExceptionIfNotFound = false;
	private boolean headersAsProperties = true;
	private boolean contenutiAsProperties = false;
	private transient ITracciaDriver tracciamentoService = null;
	private transient IDiagnosticDriver diagnosticiService = null; 

	@Override
	public void init() throws ServletException {
		try{
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(TransazioniExporter.log);

			TransazioniExporter.enableHeaderInfo = govwayMonitorProperties.isAttivoTransazioniExportHeader();
			TransazioniExporter.enableConsegneInfo = govwayMonitorProperties.isAttivoTransazioniExportConsegneMultiple();
			TransazioniExporter.mimeThrowExceptionIfNotFound=govwayMonitorProperties.isTransazioniDownloadThrowExceptionMimeTypeNotFound();
			this.headersAsProperties = govwayMonitorProperties.isAttivoTransazioniExportHeaderAsProperties();
			this.contenutiAsProperties = govwayMonitorProperties.isAttivoTransazioniExportContenutiAsProperties();
			this.tracciamentoService = govwayMonitorProperties.getDriverTracciamento();
			this.diagnosticiService = govwayMonitorProperties.getDriverMsgDiagnostici();
		}catch(Exception e){
			TransazioniExporter.log.error("Inizializzazione servlet fallita, setto enableHeaderInfo=false",e);
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
		try{
			ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

			ITransazioniService service = (ITransazioniService)context.getBean("transazioniService");


			TransazioniSearchForm sfInSession = (TransazioniSearchForm)context.getBean("searchFormTransazioni");
			TransazioniSearchForm searchForm = (TransazioniSearchForm)sfInSession.clone();

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

			String isAllString = req.getParameter(CostantiExport.PARAMETER_IS_ALL);
			Boolean isAll = Boolean.parseBoolean(isAllString);
			String idtransazioni=req.getParameter(CostantiExport.PARAMETER_IDS);
			String[] ids = StringUtils.split(idtransazioni, ",");
			String exporter=req.getParameter(CostantiExport.PARAMETER_EXPORTER);
			String[] ex = StringUtils.split(exporter,",");

			boolean exportTracce = false;
			boolean exportDiagnostici = false;
			boolean exportContenuti = false;

			if(ex!=null){
				for (String val : ex) {
					if(CostantiExport.ESPORTAZIONI_VALUE_TRACCE.equals(val))
						exportTracce=true;

					if(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI.equals(val))
						exportDiagnostici=true;

					if(CostantiExport.ESPORTAZIONI_VALUE_CONTENUTI.equals(val))
						exportContenuti=true;
				}
			}


			//Check Parametri di export
			HttpSession sessione = req.getSession();

			// Prelevo i parametri necessari
			Boolean isAllFromSession = (Boolean) sessione.getAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE);
			String idTransazioniFromSession = (String) sessione.getAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI);
			String exporterFromSession = (String) sessione.getAttribute(CostantiExport.PARAMETER_TIPI_EXPORT_ORIGINALI);

			//Rimuovo i parametri utilizzati dalla sessione
			sessione.removeAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE);
			sessione.removeAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI);
			sessione.removeAttribute(CostantiExport.PARAMETER_TIPI_EXPORT_ORIGINALI); 

			String[] idsFromSession = StringUtils.split(idTransazioniFromSession, ",");
			String[] exFromSession = StringUtils.split(exporterFromSession,",");



			//I parametri in sessione devono coincidere con quelli della request
			boolean exportConsentito = DiagnosticiExporter.checkParametri(isAll,ids,isAllFromSession,idsFromSession)
					&& TransazioniExporter.checkTipiExport(ex, exFromSession);

			if(!exportConsentito){

				String msg_errore = "L'utente non dispone dei permessi necessari per effettuare l'export delle transazioni.";
				String redirectUrl = req.getContextPath()+"/public/error.jsf?msg_errore=" + msg_errore;

				response.sendRedirect(redirectUrl);
				return;

				//				throw new ExportException("Errore durante l'export dei messaggi diagnostici: i parametri indicati non sono validi!");
			}
			// Be sure to retrieve the absolute path to the file with the required
			// method
			// filePath = pathToTheFile;

			// This is another important attribute for the header of the response
			// Here fileName, is a String with the name that you will suggest as a
			// name to save as
			// I use the same name as it is stored in the file system of the server.

			String fileName = "Transazioni.zip";

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName);

			// committing status and headers
			response.setStatus(200);
			response.flushBuffer();


			//TransazioniSearchForm search = (TransazioniSearchForm)context.getBean("searchFormTransazioni");

			Utility.setLoginMBean((LoginBean)context.getBean("loginBean"));

			ExporterProperties prop = new ExporterProperties();
			prop.setEnableHeaderInfo(TransazioniExporter.enableHeaderInfo);
			prop.setEnableConsegneInfo(TransazioniExporter.enableConsegneInfo);
			prop.setExportContenuti(exportContenuti);
			prop.setExportDiagnostici(exportDiagnostici);
			prop.setExportTracce(exportTracce);
			prop.setMimeThrowExceptionIfNotFound(TransazioniExporter.mimeThrowExceptionIfNotFound);
			prop.setHeadersAsProperties(this.headersAsProperties);
			prop.setContenutiAsProperties(this.contenutiAsProperties);
			prop.setUseCount(searchForm.isUseCount());

			SingleFileExporter sfe = new SingleFileExporter(response.getOutputStream(), prop, service,
					this.tracciamentoService, this.diagnosticiService,null);

			if(isAll){
				//transazioni = service.findAll(start, limit);
				sfe.export();
			}else{
				List<String> idTransazioni = new ArrayList<String>();
				for (int j = 0; j < ids.length; j++) {
					idTransazioni.add(ids[j]);					
				}
				sfe.export(idTransazioni);
			}		

		}catch(Throwable e){
			TransazioniExporter.log.error(e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}finally {
			// reset login bean statico
			Utility.setLoginMBean(null);
		}
	}

	public static boolean checkTipiExport(String[] ex, String[] exFromSession){

		//		if(ex == null || ex.length == 0 || exFromSession == null || exFromSession.length == 0)
		//			return false;

		boolean exportTracce = false;
		boolean exportDiagnostici = false;
		boolean exportContenuti = false;

		if(ex!=null){
			for (String val : ex) {
				if(CostantiExport.ESPORTAZIONI_VALUE_TRACCE.equals(val))
					exportTracce=true;

				if(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI.equals(val))
					exportDiagnostici=true;

				if(CostantiExport.ESPORTAZIONI_VALUE_CONTENUTI.equals(val))
					exportContenuti=true;
			}
		}

		boolean exportTracceFromSession = false;
		boolean exportDiagnosticiFromSession = false;
		boolean exportContenutiFromSession = false;

		if(exFromSession!=null){
			for (String val : exFromSession) {
				if(CostantiExport.ESPORTAZIONI_VALUE_TRACCE.equals(val))
					exportTracceFromSession=true;

				if(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI.equals(val))
					exportDiagnosticiFromSession=true;

				if(CostantiExport.ESPORTAZIONI_VALUE_CONTENUTI.equals(val))
					exportContenutiFromSession=true;
			}
		}

		// I valori dei parametri possibili devono coincidere.

		if(exportContenuti != exportContenutiFromSession)
			return false;

		if(exportDiagnostici != exportDiagnosticiFromSession)
			return false;

		if(exportTracce !=exportTracceFromSession)
			return false;

		return true;
	}
}
