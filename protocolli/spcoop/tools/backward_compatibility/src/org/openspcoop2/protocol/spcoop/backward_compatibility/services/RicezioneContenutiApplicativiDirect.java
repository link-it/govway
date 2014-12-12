/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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



package org.openspcoop2.protocol.spcoop.backward_compatibility.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.BackwardCompatibilityProperties;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.Costanti;

/**
 * Servlet di ingresso al servizio DirectPD che costruisce il SOAPMessage gli applica gli handlers
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public class RicezioneContenutiApplicativiDirect extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private org.openspcoop2.pdd.services.connector.RicezioneContenutiApplicativiSOAPConnector ricezioneContenutiApplicativiDirect = null;
	private BackwardCompatibilityProperties backwardCompatibilityProperties = null;
	
	@Override
	public void init() throws ServletException {
		try{
			this.ricezioneContenutiApplicativiDirect = new org.openspcoop2.pdd.services.connector.RicezioneContenutiApplicativiSOAPConnector();
			this.ricezioneContenutiApplicativiDirect.init();
			this.backwardCompatibilityProperties = new BackwardCompatibilityProperties(OpenSPCoop2Properties.getInstance().getRootDirectory());
						
		}catch(Exception e){
			throw new ServletException("Errore durante l'inizializzazione della servlet 'RicezioneContenutiApplicativiDirect' compatibile con le interfacce di OpenSPCoop v1: "+e.getMessage(),e);
		}
	}
	

	@Override public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		try{
			WrapperHttpServletRequest httpReqWrapper = new WrapperHttpServletRequest(req,this.backwardCompatibilityProperties);
			PdDContext pddContext = new PdDContext();
			pddContext.addObject(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY, Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY);
			httpReqWrapper.setAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP, pddContext);
			//System.out.println("getRequestURI=["+httpReqWrapper.getRequestURI()+"]");
			//System.out.println("getRequestURL=["+httpReqWrapper.getRequestURL()+"]");
			//System.out.println("getServletPath=["+httpReqWrapper.getServletPath()+"]");
			this.ricezioneContenutiApplicativiDirect.doPost(httpReqWrapper, res);
		}catch(Exception e){
			throw new ServletException(e.getMessage(),e);
		}

	}
	@Override public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		try{
			WrapperHttpServletRequest httpReqWrapper = new WrapperHttpServletRequest(req,this.backwardCompatibilityProperties);
			PdDContext pddContext = new PdDContext();
			pddContext.addObject(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY, Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY);
			httpReqWrapper.setAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP, pddContext);
			//System.out.println("getRequestURI=["+httpReqWrapper.getRequestURI()+"]");
			//System.out.println("getRequestURL=["+httpReqWrapper.getRequestURL()+"]");
			//System.out.println("getServletPath=["+httpReqWrapper.getServletPath()+"]");
			this.ricezioneContenutiApplicativiDirect.doGet(httpReqWrapper, res);
		}catch(Exception e){
			throw new ServletException(e.getMessage(),e);
		}
	}

}
