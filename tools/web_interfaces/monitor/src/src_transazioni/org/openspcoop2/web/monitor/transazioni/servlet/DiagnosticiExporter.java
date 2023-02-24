/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.core.UtilityTransazioni;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.exporter.CostantiExport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.XMLRootElement;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * DiagnosticiExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DiagnosticiExporter extends HttpServlet{

	private static final long serialVersionUID = 1272767433184676700L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	private static IDiagnosticDriver diagnosticiService = null;
	private static Boolean enableHeaderInfo = false;

	@Override
	public void init() throws ServletException {
		try{
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(DiagnosticiExporter.log);
			DiagnosticiExporter.enableHeaderInfo = govwayMonitorProperties.isAttivoTransazioniExportHeader();

			diagnosticiService = govwayMonitorProperties.getDriverMsgDiagnostici();
		}catch(Exception e){
			DiagnosticiExporter.log.error("Inizializzazione servlet fallita, setto enableHeaderInfo=false",e);
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
				throw new Exception("Context is null");
			}
			
			ITransazioniService service = (ITransazioniService)context.getBean("transazioniService");

			// Then we have to get the Response where to write our file
			HttpServletResponse response = resp;

			String isAllString = req.getParameter(CostantiExport.PARAMETER_IS_ALL);
			Boolean isAll = Boolean.parseBoolean(isAllString);
			String idtransazioni = req.getParameter(CostantiExport.PARAMETER_IDS);
			String[] ids = StringUtils.split(idtransazioni, ",");

			//Check Parametri di export
			HttpSession sessione = req.getSession();

			// Prelevo i parametri necessari
			Boolean isAllFromSession = (Boolean) sessione.getAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE);
			String idTransazioniFromSession = (String) sessione.getAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI);

			//Rimuovo i parametri utilizzati dalla sessione
			sessione.removeAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE);
			sessione.removeAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI);

			String[] idsFromSession = StringUtils.split(idTransazioniFromSession, ",");

			//I parametri in sessione devono coincidere con quelli della request
			boolean exportConsentito = DiagnosticiExporter.checkParametri(isAll,ids,isAllFromSession,idsFromSession);

			if(!exportConsentito){
				
				String msg_errore = "L'utente non dispone dei permessi necessari per effettuare l'export  dei messaggi diagnostici.";
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

			String fileName = "Diagnostici.zip";

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName);
			
			// committing status and headers
			response.setStatus(200);
			response.flushBuffer();

			//int i = 0;// progressivo per evitare entry duplicate nel file zip
			// Create a buffer for reading the files
			byte[] buf = new byte[1024];
			ZipOutputStream zip = new ZipOutputStream(response.getOutputStream());
			InputStream in = null;

			int start = 0;
			int limit = 25;
			List<TransazioneBean> transazioni = new ArrayList<TransazioneBean>();

			Utility.setLoginMBean((LoginBean)context.getBean("loginBean"));


			if(isAll)
				transazioni = service.findAll(start, limit);
			else{
				for (int j = 0; j < ids.length; j++) {
					transazioni.add(service.findByIdTransazione(ids[j]));					
				}
			}


			while(transazioni.size()>0){

				for(TransazioneBean t: transazioni){
					//recupero i diagnostici per questa transazione
					FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();


					//devo impostare solo l'idtransazione
					//filter.setIdEgov(this.diagnosticiBean.getIdEgov());	
					Map<String, String> properties = new HashMap<String, String>();
					properties.put("id_transazione", t.getIdTransazione());
					filter.setProperties(properties);


					List<MsgDiagnostico> list = diagnosticiService.getMessaggiDiagnostici(filter);
					// Add ZIP entry to output stream.
					zip.putNextEntry(new ZipEntry(/*++i + "_" + */t.getIdTransazione() + " (" + list.size() + " entries)" + ".xml"));
					if(DiagnosticiExporter.enableHeaderInfo){
						zip.write(UtilityTransazioni.getHeaderTransazione(t).getBytes());
					}
					
					String tail = null;
					for (int j = 0; j < list.size(); j++) {
						MsgDiagnostico msg = list.get(j);
						String newLine = j > 0 ? "\n\n" : "";

						IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(msg.getProtocollo());
						IDiagnosticSerializer diagnosticoBuilder = pf.createDiagnosticSerializer();
						
						if(j==0){
							XMLRootElement xmlRootElement = diagnosticoBuilder.getXMLRootElement();
							if(xmlRootElement!=null){
								String head = xmlRootElement.getAsStringStartTag();
								if(head!=null && !"".equals(head)){
									head = head +"\n\n";
									zip.write(head.getBytes(), 0, head.length());
									tail = xmlRootElement.getAsStringEndTag();
	    							if(tail!=null && !"".equals(tail)){
	    								tail = "\n\n" + tail;
	    							}
								}
							}
						}
						
						String msgDiagnostico = diagnosticoBuilder.toString(msg,TipoSerializzazione.DEFAULT);
						in = new ByteArrayInputStream((newLine + msgDiagnostico).getBytes());
						// Transfer bytes from the input stream to the ZIP file
						int len;
						while ((len = in.read(buf)) > 0) {
							zip.write(buf, 0, len);
						}
					}
					if(tail!=null && !"".equals(tail)){
						zip.write(tail.getBytes(), 0, tail.length());
					}
					
					// Complete the entry
					zip.closeEntry();
					zip.flush();
					in.close();
				}

				start+=limit;

				response.flushBuffer();

				if(!isAll)
					break;
				else
					transazioni = service.findAll(start, limit);
			}

			zip.flush();
			zip.close();
		}catch(Throwable e){
			DiagnosticiExporter.log.error(e.getMessage(),e);
			//throw new ServletException(e.getMessage(),e);
		}
	}

	public static boolean checkParametri(Boolean isAll,  String[]ids, Boolean isAllFromSession, String [] idsFromSession){
		// Il valore di is All deve essere coincidente
		if(isAll == null || isAllFromSession == null || isAll.booleanValue() != isAllFromSession.booleanValue())
			return false;

		// Se l'array degli id ricevuto dalla richiesta e' vuoto o null non si puo' fare l'export
		// e se l'array degli id in sessione non e' presente o e' vuoto non si puo' fare export.
		if(ids == null || ids.length == 0 || idsFromSession ==null || idsFromSession.length == 0){
			if(isAll==false)
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
