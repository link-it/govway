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
import org.openspcoop2.utils.mime.MimeTypes;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.exporter.CostantiExport;
import org.openspcoop2.web.monitor.transazioni.exporter.ExporterCsvProperties;
import org.openspcoop2.web.monitor.transazioni.exporter.SingleCsvFileExporter;

/**
 * TransazioniCsvExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TransazioniCsvExporter extends HttpServlet{

	private static final long serialVersionUID = 1272767433184676700L;
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	private static Boolean enableHeaderInfo = false;
	private static Boolean mimeThrowExceptionIfNotFound = false;
	private boolean headersAsProperties = true;
	private boolean contenutiAsProperties = false;
	private transient ITracciaDriver tracciamentoService = null;
	private transient IDiagnosticDriver diagnosticiService = null; 
	private boolean exportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti = false;

	@Override
	public void init() throws ServletException {
		try{
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(TransazioniCsvExporter.log);

			TransazioniCsvExporter.enableHeaderInfo = govwayMonitorProperties.isAttivoTransazioniExportHeader();
			TransazioniCsvExporter.mimeThrowExceptionIfNotFound=govwayMonitorProperties.isTransazioniDownloadThrowExceptionMimeTypeNotFound();
			this.headersAsProperties = govwayMonitorProperties.isAttivoTransazioniExportHeaderAsProperties();
			this.contenutiAsProperties = govwayMonitorProperties.isAttivoTransazioniExportContenutiAsProperties();
			this.tracciamentoService = govwayMonitorProperties.getDriverTracciamento();
			this.diagnosticiService = govwayMonitorProperties.getDriverMsgDiagnostici();
			this.exportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti = govwayMonitorProperties.isExportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti();
		}catch(Exception e){
			TransazioniCsvExporter.log.error("Inizializzazione servlet fallita, setto enableHeaderInfo=false",e);
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


	@SuppressWarnings("unchecked")
	private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
		try{
			ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			if(context==null) {
				throw new Exception("Context is null");
			}

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
			String formato =req.getParameter(CostantiExport.PARAMETER_FORMATO_EXPORT);
			String idColonneSelezionate =req.getParameter(CostantiExport.PARAMETER_ID_SELEZIONI);
			

			boolean exportTracce = false;
			boolean exportDiagnostici = false;
			boolean exportContenuti = false;

			if(this.exportTransazioniCsvVisualizzaCheckBoxSelezioneContenuti && ex!=null){
				for (String val : ex) {
					if(CostantiExport.ESPORTAZIONI_VALUE_TRACCE.equals(val))
						exportTracce=true;

					if(CostantiExport.ESPORTAZIONI_VALUE_DIAGNOSTICI.equals(val))
						exportDiagnostici=true;

					// export dei contenuti non selezionabile dall'utente aggiungo un controllo di sicurezza sulla request
					if(CostantiExport.ESPORTAZIONI_VALUE_CONTENUTI.equals(val)){
						String msg_errore = "L'export dei contenuti non e' disponibile per la funzionalita' richiesta.";
						String redirectUrl = req.getContextPath()+"/public/error.jsf?msg_errore=" + msg_errore;
						
						response.sendRedirect(redirectUrl);
						return;
					}
				}
			}
			
			
			//Check Parametri di export
			HttpSession sessione = req.getSession();

			// Prelevo i parametri necessari
			Boolean isAllFromSession = (Boolean) sessione.getAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE);
			String idTransazioniFromSession = (String) sessione.getAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI);
			String exporterFromSession = (String) sessione.getAttribute(CostantiExport.PARAMETER_TIPI_EXPORT_ORIGINALI);
			String formatoFromSession = (String) sessione.getAttribute(CostantiExport.PARAMETER_FORMATO_EXPORT_ORIGINALE);
			String idColonneFromSession = (String) sessione.getAttribute(CostantiExport.PARAMETER_ID_SELEZIONI_ORIGINALI);
			List<String> colonneSelezionateFromSession = (List<String>) sessione.getAttribute(CostantiExport.PARAMETER_LISTA_SELEZIONI_ORIGINALI);

			//Rimuovo i parametri utilizzati dalla sessione
			sessione.removeAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE);
			sessione.removeAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI);
			sessione.removeAttribute(CostantiExport.PARAMETER_TIPI_EXPORT_ORIGINALI);
			sessione.removeAttribute(CostantiExport.PARAMETER_FORMATO_EXPORT_ORIGINALE); 
			sessione.removeAttribute(CostantiExport.PARAMETER_ID_SELEZIONI_ORIGINALI); 
			sessione.removeAttribute(CostantiExport.PARAMETER_LISTA_SELEZIONI_ORIGINALI); 

			String[] idsFromSession = StringUtils.split(idTransazioniFromSession, ",");
			String[] exFromSession = StringUtils.split(exporterFromSession,",");
			
			//I parametri in sessione devono coincidere con quelli della request
			boolean exportConsentito = DiagnosticiExporter.checkParametri(isAll,ids,isAllFromSession,idsFromSession)
						&& TransazioniExporter.checkTipiExport(ex, exFromSession)
						&& TransazioniCsvExporter.checkFormatoExport(formato, formatoFromSession)
						&& TransazioniCsvExporter.checkIdentificativoColonneSelezionate(idColonneSelezionate, idColonneFromSession);
						

			if(!exportConsentito){
				String msg_errore = "L'utente non dispone dei permessi necessari per effettuare l'export delle transazioni.";
				String redirectUrl = req.getContextPath()+"/public/error.jsf?msg_errore=" + msg_errore;
				
				response.sendRedirect(redirectUrl);
				return;
			}

			// calcolo di mime type e nome file
			String mimeType = MimeTypes.getInstance().getMimeType(formato);
			String ext = MimeTypeUtils.fileExtensionForMIMEType(mimeType);
			String fileName = "Transazioni" +"."+ext; 

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName,mimeType);
						
			// committing status and headers
			response.setStatus(200);
			response.flushBuffer();


			//TransazioniSearchForm search = (TransazioniSearchForm)context.getBean("searchFormTransazioni");

			Utility.setLoginMBean((LoginBean)context.getBean("loginBean"));

			ExporterCsvProperties prop = new ExporterCsvProperties();
			prop.setEnableHeaderInfo(TransazioniCsvExporter.enableHeaderInfo);
			prop.setExportContenuti(exportContenuti);
			prop.setExportDiagnostici(exportDiagnostici);
			prop.setExportTracce(exportTracce);
			prop.setMimeThrowExceptionIfNotFound(TransazioniCsvExporter.mimeThrowExceptionIfNotFound);
			prop.setFormato(formato);
			prop.setColonneSelezionate(colonneSelezionateFromSession);
			prop.setHeadersAsProperties(this.headersAsProperties);
			prop.setContenutiAsProperties(this.contenutiAsProperties);
			prop.setUseCount(searchForm.isUseCount());

			SingleCsvFileExporter sfe = new SingleCsvFileExporter(response.getOutputStream(), prop, service,
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
			TransazioniCsvExporter.log.error(e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		} finally {
			// reset login bean statico
			Utility.setLoginMBean(null);
		}
	}
	
	public static boolean checkFormatoExport(String formato, String formatoFromSession){
		
		if(formato == null ||  formatoFromSession == null)
			return false;
		
		return formato.equals(formatoFromSession);
	}
	
	public static boolean checkIdentificativoColonneSelezionate(String idColonne, String idColonneFromSession){
		
		if(idColonne == null ||  idColonneFromSession == null)
			return false;
		
		return idColonne.equals(idColonneFromSession);
	}
}
