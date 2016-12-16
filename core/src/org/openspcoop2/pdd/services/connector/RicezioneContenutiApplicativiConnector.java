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

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.RequestInfo;
import org.openspcoop2.pdd.services.connector.messages.HttpServletConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.HttpServletConnectorOutMessage;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.pdd.services.service.RicezioneContenutiApplicativiService;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * RicezioneContenutiApplicativiConnector
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */



public class RicezioneContenutiApplicativiConnector {


	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public final static IDService ID_SERVICE = IDService.PORTA_DELEGATA;
	public final static String ID_MODULO = ID_SERVICE.getValue();

	
	public void doEngine(RequestInfo requestInfo, 
			HttpServletRequest req, HttpServletResponse res, HttpRequestMethod method) throws ServletException, IOException {
		
		if(HttpRequestMethod.GET.equals(method)){
			Enumeration<?> parameters = req.getParameterNames();
			while(parameters.hasMoreElements()){
				String key = (String) parameters.nextElement();
				String value = req.getParameter(key);
				if("wsdl".equalsIgnoreCase(key) && (value==null || "".equals(value)) ){
					ConnectorDispatcherUtils.doWsdl(req, res, method, ID_SERVICE);
					return;
				}
			}
		}
		
		if(ServiceBinding.SOAP.equals(requestInfo.getIntegrationServiceBinding()) && !HttpRequestMethod.POST.equals(method)){

			ConnectorDispatcherUtils.doMethodNotSupported(req, res, method, ID_SERVICE);
			return;
			
		}
		
		
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		
		RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore = null;
		try{
			generatoreErrore = 
					new RicezioneContenutiApplicativiInternalErrorGenerator(logCore, ID_MODULO, requestInfo);
		}catch(Exception e){
			String msg = "Inizializzazione Generatore Errore fallita: "+e.getMessage();
			logCore.error(msg,e);
			ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
					IntegrationError.INTERNAL_ERROR, e, res, logCore);
			return;
		}
		
		
		RicezioneContenutiApplicativiService ricezioneContenutiApplicativi = new RicezioneContenutiApplicativiService(generatoreErrore);
		
		HttpServletConnectorInMessage httpIn = null;
		try{
			httpIn = new HttpServletConnectorInMessage(requestInfo, req, ID_SERVICE, ID_MODULO);
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("HttpServletConnectorInMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
		IProtocolFactory<?> protocolFactory = null;
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
			ricezioneContenutiApplicativi.process(httpIn, httpOut);
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("RicezioneContenutiApplicativi.process error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
			
	}

	

}
