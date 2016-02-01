/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.utils.resources.HttpUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;

/**
 * ResocontoExporter
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ResocontoExporter extends HttpServlet {

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


		ControlStationCore.logDebug("Ricevuta Richiesta di esportazione resoconto...");
		
		try {
			
			Object valore = request.getSession().getAttribute(ArchiviCostanti.PARAMETRO_DOWNLOAD_RESOCONTO_VALORE);
			if(valore==null){
				throw new Exception("Resoconto da esportare non presente");
			}
			if(!(valore instanceof String)){
				throw new Exception("Resoconto da esportare non valido");
			}
			String contenuto = (String) valore;
			
			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, "Resoconto.txt");
	
			OutputStream out = response.getOutputStream();	
			out.write(contenuto.getBytes());
			out.flush();
			out.close();
		
		} catch (Exception e) {
			ControlStationCore.logError("Errore durante il download del resoconto: "+e.getMessage(), e);
			throw new ServletException(e);
		} 
	}

}
