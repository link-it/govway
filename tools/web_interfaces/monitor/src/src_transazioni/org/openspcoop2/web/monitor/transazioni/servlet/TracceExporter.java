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
package org.openspcoop2.web.monitor.transazioni.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.XMLRootElement;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.bean.LoginBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.core.UtilityTransazioni;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.exporter.CostantiExport;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * TracceExporter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TracceExporter extends HttpServlet{

	private static final long serialVersionUID = 1272767433184676700L;
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	private static ITracciaDriver tracciamentoService = null;
		public static void setTracciamentoService(ITracciaDriver tracciamentoService) {
		TracceExporter.tracciamentoService = tracciamentoService;
	}


	private static Boolean enableHeaderInfo = false;
	public static void setEnableHeaderInfo(Boolean enableHeaderInfo) {
		TracceExporter.enableHeaderInfo = enableHeaderInfo;
	}

	@Override
	public void init() throws ServletException {
		try{
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(TracceExporter.log);
			TracceExporter.setEnableHeaderInfo(govwayMonitorProperties.isAttivoTransazioniExportHeader());
			
			TracceExporter.setTracciamentoService(govwayMonitorProperties.getDriverTracciamento());
		}catch(Exception e){
			TracceExporter.log.error("Inizializzazione servlet fallita, setto enableHeaderInfo=false",e);
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
				throw new CoreException("Context is null");
			}
			
			ITransazioniService service = (ITransazioniService)context.getBean("transazioniService");
			
			// Then we have to get the Response where to write our file
			HttpServletResponse response = resp;

			String isAllString = req.getParameter(CostantiExport.PARAMETER_IS_ALL);
			Boolean isAll = Boolean.parseBoolean(isAllString);
			String idtransazioni=req.getParameter(CostantiExport.PARAMETER_IDS);
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
				
				String msgErrore = "L'utente non dispone dei permessi necessari per effettuare l'export delle tracce.";
				String redirectUrl = req.getContextPath()+"/public/error.jsf?msg_errore=" + msgErrore;
				
				response.sendRedirect(redirectUrl);
				return;
				
				/**throw new ExportException("Errore durante l'export dei messaggi diagnostici: i parametri indicati non sono validi!");*/
			}
			
			String fileName = "Tracce.zip";

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName);
				        
	        // committing status and headers
	        response.setStatus(200);
	        response.flushBuffer();
			
			
			int start = 0;
			int limit = 25;
			List<TransazioneBean> transazioni = new ArrayList<>();
			
			Utility.setLoginMBean((LoginBean)context.getBean("loginBean"));
			
			
			if(isAll!=null && isAll.booleanValue())
				transazioni = service.findAll(start, limit);
			else{
				for (int j = 0; j < ids.length; j++) {
					transazioni.add(service.findByIdTransazione(ids[j]));					
				}
			}
				
			if(!transazioni.isEmpty()){
				//int i = 0;// progressivo per evitare entry duplicate nel file zip
				// Create a buffer for reading the files
				byte[] buf = new byte[1024];
				ZipOutputStream zip = new ZipOutputStream(response.getOutputStream());
				InputStream in = null;
			
				while(!transazioni.isEmpty()){

					for(TransazioneBean t: transazioni){
						//recupero le tracce per questa transazione

						Traccia tracciaRichiesta = getTraccia(t.getIdTransazione(), RuoloMessaggio.RICHIESTA);
						Traccia tracciaRisposta  = getTraccia(t.getIdTransazione(), RuoloMessaggio.RISPOSTA);
						ArrayList<Traccia> tracce = new ArrayList<>();
						if(tracciaRichiesta!=null) {
							tracce.add(tracciaRichiesta);
						}
						if(tracciaRisposta!=null) {
							tracce.add(tracciaRisposta);
						}
						if(!tracce.isEmpty()){
							// Add ZIP entry to output stream.
							zip.putNextEntry(new ZipEntry(/*++i + "_" + */t.getIdTransazione() + " (" + tracce.size() + " entries)" + ".xml"));
							if(TracceExporter.enableHeaderInfo!=null && TracceExporter.enableHeaderInfo.booleanValue()){
								zip.write(UtilityTransazioni.getHeaderTransazione(t).getBytes());
							}	
							
							String tail = null;			
							for (int j = 0; j < tracce.size(); j++) {
								Traccia tr = tracce.get(j);
								String newLine = j > 0 ? "\n\n" : "";
								
								IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tr.getProtocollo());
								ITracciaSerializer tracciaBuilder = pf.createTracciaSerializer();
								
								if(j==0){
									XMLRootElement xmlRootElement = tracciaBuilder.getXMLRootElement();
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
								
								tracciaBuilder.setOmitXmlDeclaration(true);
								String traccia = tracciaBuilder.toString(tr, TipoSerializzazione.DEFAULT);
								in = new ByteArrayInputStream((newLine + traccia).getBytes());
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
					}

					start+=limit;

					response.flushBuffer();

					if(isAll==null || !isAll.booleanValue())
						break;
					else
						transazioni = service.findAll(start, limit);
				}
				
				
				zip.flush();
				zip.close();
			}
			
			
		}catch(Throwable e){
			TracceExporter.log.error(e.getMessage(),e);
			/**throw new ServletException(e.getMessage(),e);*/
		}
	}
	
	private Traccia getTraccia(String idTransazione, RuoloMessaggio ruolo) {
		//devo impostare solo l'idtransazione
		/**filter.setIdEgov(this.diagnosticiBean.getIdEgov());*/	
		Map<String, String> properties = new HashMap<>();
		properties.put("id_transazione", idTransazione);
		
		try{
			return tracciamentoService.getTraccia(ruolo,properties);
		}catch(DriverTracciamentoException|DriverTracciamentoNotFoundException e){
			//ignore
		}
		
		return null;
	}
}
