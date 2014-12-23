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



package org.openspcoop2.pdd.services.connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.RicezioneBusteSOAP;
import org.openspcoop2.utils.Utilities;
import org.w3c.dom.Document;


/**
 * Contiene la definizione di una servlet 'RicezioneBusteDirect'
 * Si occupa di creare il Messaggio, applicare la logica degli handlers 
 * e passare il messaggio ad OpenSPCoop per lo sbustamento e consegna.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneBusteSOAPConnector extends HttpServlet {

	/* ********  F I E L D S  P R I V A T I S T A T I C I  ******** */

	private static final long serialVersionUID = 1L;


	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public final static String ID_MODULO = "RicezioneBuste_PA";

	
	/* ********  M E T O D I   ******** */

	/**
	 * 
	 * 
	 * 
	 */
	@Override public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
		RicezioneBusteSOAP soapConnector = new RicezioneBusteSOAP();
		
		HttpServletConnectorInMessage httpIn = null;
		try{
			httpIn = new HttpServletConnectorInMessage(req, ID_MODULO);
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("HttpServletConnectorInMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
		HttpServletConnectorOutMessage httpOut = null;
		try{
			httpOut = new HttpServletConnectorOutMessage(res);
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("HttpServletConnectorOutMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
			
		try{
			soapConnector.process(httpIn, httpOut);
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("SoapConnector.process error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
	}
	
	@Override public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
		String versione = "Porta di Dominio "+OpenSPCoop2Properties.getInstance().getPddDetailsForServices();
		
		Enumeration<?> parameters = req.getParameterNames();
		while(parameters.hasMoreElements()){
			String key = (String) parameters.nextElement();
			String value = req.getParameter(key);
			if("wsdl".equalsIgnoreCase(key) && (value==null || "".equals(value)) ){
				InputStream is =null;
				try{
					OpenSPCoop2Properties props = new OpenSPCoop2Properties(null);
					is = RicezioneBusteSOAPConnector.class.getResourceAsStream("/PA.wsdl");
					ByteArrayOutputStream bout = new ByteArrayOutputStream();

					if(is!=null){
						int letti = 0;
						byte [] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
						while( (letti=is.read(buffer)) != -1 ){
							bout.write(buffer, 0, letti);
						}
						bout.flush();
						bout.close();
					}
					
					if(props.isGenerazioneWsdlPortaApplicativaEnabled()){
					
						if(bout.size()<=0){
							throw new Exception("WSDL Not Found");
						}
						
						byte[] b = bout.toByteArray();
						org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
						Document d = xmlUtils.newDocument(b);
						d.getFirstChild().appendChild(d.createComment(versione));
						xmlUtils.writeTo(d, res.getOutputStream());
						
					}
					else{
						
						if(bout.size()>0){
							res.getOutputStream().write(bout.toByteArray());
						}
						else{
							res.sendError(404);
						}
						
					}
					
				}catch(Exception e){
					res.setStatus(500);
					StringBuffer risposta = new StringBuffer();
					risposta.append("<html>\n");
					risposta.append("<body>\n");
					risposta.append("<h1>" + req.getContextPath() + req.getServletPath() +  "?wsdl</h1>\n");
					risposta.append("<p>Generazione WSDL non riuscita</p>\n");
					risposta.append("</body>\n");
					risposta.append("</html>\n");
					res.getOutputStream().write(risposta.toString().getBytes());
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Generazione WSDL PA non riuscita",e);	
				}finally{
					try{
						res.getOutputStream().flush();
					}catch(Exception eClose){}
					try{
						res.getOutputStream().close();
					}catch(Exception eClose){}
					try{
						if(is!=null)
							is.close();
					}catch(Exception eClose){}
				}
				return;
			}
		}
		
		res.setStatus(500);
		StringBuffer risposta = new StringBuffer();
		risposta.append("<html>\n");
		risposta.append("<body>\n");
		risposta.append("<h1>" + req.getContextPath() + req.getServletPath() +"</h1>\n");
		risposta.append("<p>"+versione+"</p>\n");
		risposta.append("<p>Method HTTP GET non supportato</p>\n");
		risposta.append("<i>Servizio utilizzabile per l'invocazione di Porte Applicative esposte dalla PdD OpenSPCoop v2</i>\n");
		risposta.append("</body>\n");
		risposta.append("</html>\n");

		res.getOutputStream().write(risposta.toString().getBytes());
		
		try{
			res.getOutputStream().flush();
		}catch(Exception eClose){}
		try{
			res.getOutputStream().close();
		}catch(Exception eClose){}
	}

}



