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
package org.openspcoop2.example.pdd.server.testservice;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

/**
 * EchoServiceSoap12Stresstest
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Soap12StresstestService extends HttpServlet {

	private static final String SOAP_ENVELOPE_RISPOSTA = 
			"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
					"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
			"<soapenv:Body><prova2>RISPSTAT</prova2></soapenv:Body></soapenv:Envelope>";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		
		try{
	
			String contentType = "application/soap+xml";
			res.setContentType(contentType);
			
			
			String staticResponse = req.getParameter("staticResponse");
			boolean isStaticResponse = true;
			if(staticResponse!=null){
				isStaticResponse = Boolean.parseBoolean(staticResponse);
			}
			
			if(isStaticResponse){
				res.getOutputStream().write(SOAP_ENVELOPE_RISPOSTA.getBytes());
			}
			else{
				byte[]buffer = new byte[1024];
				int letti = 0;
				while( (letti=req.getInputStream().read(buffer))!=-1 ){
					res.getOutputStream().write(buffer,0,letti);	
				}
			}
			
			
			res.getOutputStream().flush();
			res.getOutputStream().close();
			
			
		}catch(Exception e){
			Logger log = Startup.logStressTest;
			if(log!=null){
				log.error("ERRORE Soap12StresstestService: "+e.toString(),e);
			}
			else{
				System.out.println("ERRORE Soap12StresstestService: "+e.toString());
				e.printStackTrace(System.out);
			}
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		doGet(req,res);
	}

}
