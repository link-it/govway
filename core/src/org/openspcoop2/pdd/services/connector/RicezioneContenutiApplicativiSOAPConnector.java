/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.pdd.services.connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.core.api.constants.MethodType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativiSOAP;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.Utilities;
import org.w3c.dom.Document;

/**
 * Servlet di ingresso al servizio DirectPD che costruisce il SOAPMessage gli applica gli handlers
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public class RicezioneContenutiApplicativiSOAPConnector extends HttpServlet {

		
	@Override
	public void init() throws ServletException {}

	  
	  /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	

	/* ********  F I E L D S  P R I V A T I S T A T I C I  ******** */

	
	
	
	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public final static IDService ID_SERVICE = IDService.PORTA_DELEGATA_SOAP;
	public final static String ID_MODULO = ID_SERVICE.getValue();


	@Override public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
		RicezioneContenutiApplicativiSOAP soapConnector = new RicezioneContenutiApplicativiSOAP();
		
		HttpServletConnectorInMessage httpIn = null;
		try{
			httpIn = new HttpServletConnectorInMessage(req, ID_SERVICE, ID_MODULO);
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("HttpServletConnectorInMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
		IProtocolFactory protocolFactory = null;
		try{
			protocolFactory = httpIn.getProtocolFactory();
		}catch(Throwable e){}
		
		HttpServletConnectorOutMessage httpOut = null;
		try{
			httpOut = new HttpServletConnectorOutMessage(protocolFactory, res, ID_SERVICE, ID_MODULO);
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("HttpServletConnectorOutMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
			
		try{
			soapConnector.process(httpIn, httpOut);
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("SoapConnector.process error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}

	}
	
	
	public void engine(HttpServletRequest req, HttpServletResponse res, MethodType method)
	throws ServletException, IOException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		String versione = "Porta di Dominio "+CostantiPdD.OPENSPCOOP2_PRODUCT_VERSION;
		if(op2Properties!=null){
			versione = "Porta di Dominio "+op2Properties.getPddDetailsForServices();
		}
		
		if(MethodType.GET.equals(method)){
			Enumeration<?> parameters = req.getParameterNames();
			while(parameters.hasMoreElements()){
				String key = (String) parameters.nextElement();
				String value = req.getParameter(key);
				if("wsdl".equalsIgnoreCase(key) && (value==null || "".equals(value)) ){
					InputStream is =null;
					try{
						
						is = RicezioneContenutiApplicativiSOAPConnector.class.getResourceAsStream("/PD.wsdl");
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
						
						if(op2Properties!=null && op2Properties.isGenerazioneWsdlPortaDelegataEnabled()){
						
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
							
	//						if(bout.size()>0){
	//							res.getOutputStream().write(bout.toByteArray());
	//						}
	//						else{
							res.sendError(404, ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeWsdlUnsupported(ID_SERVICE)));
	//						}
							
						}
						
					}catch(Exception e){					
						res.setStatus(500);
						
						ConnectorUtils.generateErrorMessage(ID_SERVICE,method,req,res, "Generazione WSDL non riuscita", false, true);
						
						ConnectorUtils.getErrorLog().error("Generazione WSDL PD non riuscita",e);	
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
		}
		
		// messaggio di errore
		boolean errore404 = false;
		if(op2Properties!=null && !op2Properties.isGenerazioneErroreHttpMethodUnsupportedPortaDelegataEnabled()){
			errore404 = true;
		}
		
		if(errore404){
			res.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeHttpMethodNotSupported(ID_SERVICE, method)));
		}
		else{
		
			res.setStatus(500);
			
			ConnectorUtils.generateErrorMessage(ID_SERVICE,method,req,res, ConnectorUtils.getMessageHttpMethodNotSupported(method), false, true);
			
			try{
				res.getOutputStream().flush();
			}catch(Exception eClose){}
			try{
				res.getOutputStream().close();
			}catch(Exception eClose){}
			
		}
	}

}
