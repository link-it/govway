/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.utils.resources.MimeTypes;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;

/**
 * Questa servlet si occupa di esportare le tracce diagnostiche in formato xml
 * zippandole
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DocumentoExporter extends HttpServlet {

	private static final long serialVersionUID = -7341279067126334095L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	/**
	 * Processa la richiesta pervenuta e si occupa di fornire i dati richiesti
	 * in formato zip
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		ControlStationCore.logDebug("Ricevuta Richiesta di esportazione...");
		Enumeration<?> en = request.getParameterNames();
		ControlStationCore.logDebug("Parametri (nome = valore):\n-----------------");
		while (en.hasMoreElements()) {
			String param = (String) en.nextElement();
			String value = request.getParameter(param);
			ControlStationCore.logDebug(param + " = " + value);
		}
		ControlStationCore.logDebug("-----------------");

		String idAllegato = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ALLEGATO);
		String idAccordo = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO);
		String tipoDocumento = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO);
		int idAllegatoInt = Integer.parseInt(idAllegato);
		@SuppressWarnings("unused")
		int idAccordoInt = Integer.parseInt(idAccordo);
		byte[] docBytes = null;
		String fileName = null;
		
		try {
			ArchiviCore archiviCore = new ArchiviCore();
			
			if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE.equals(tipoDocumento)){
				Documento doc = archiviCore.getDocumento(idAllegatoInt,true);
				fileName = doc.getFile();
				docBytes = doc.getByteContenuto();
			}
			else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA.equals(tipoDocumento)){
				Documento doc = archiviCore.getDocumento(idAllegatoInt,true);
				fileName = doc.getFile();
				docBytes = doc.getByteContenuto();
			}
			else if(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_COOPERAZIONE.equals(tipoDocumento)){
				Documento doc = archiviCore.getDocumento(idAllegatoInt,true);
				fileName = doc.getFile();
				docBytes = doc.getByteContenuto();
			}
			else{
				throw new ServletException("Tipo documento ["+tipoDocumento+"] non gestito");
			}
		
			// setto content-type e header per gestire il download lato client
			String mimeType = null;
			if(fileName.contains(".")){
				String ext = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
				MimeTypes mimeTypes = MimeTypes.getInstance();
				if(mimeTypes.existsExtension(ext)){
					mimeType = mimeTypes.getMimeType(ext);
					//System.out.println("CUSTOM ["+mimeType+"]");		
				}
				else{
					mimeType = ArchiviCostanti.HEADER_X_DOWNLOAD;
				}
			}
			else{
				mimeType = ArchiviCostanti.HEADER_X_DOWNLOAD;
			}
			
			response.setContentType(mimeType);
			response.setHeader(ArchiviCostanti.HEADER_CONTENT_DISPOSITION, ArchiviCostanti.HEADER_ATTACH_FILE + fileName);
	
			OutputStream out = response.getOutputStream();	
			out.write(docBytes);
			out.flush();
			out.close();
		
		} catch (Exception e) {
			ControlStationCore.logError("Errore durante il download dei documenti "+e.getMessage(), e);
			throw new ServletException(e);
		} 
	}

}
