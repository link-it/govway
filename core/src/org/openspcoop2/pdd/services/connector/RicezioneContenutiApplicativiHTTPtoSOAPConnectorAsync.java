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



package org.openspcoop2.pdd.services.connector;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.pdd.services.service.IRicezioneService;
import org.openspcoop2.pdd.services.service.RicezioneContenutiApplicativiHTTPtoSOAPService;
import org.openspcoop2.pdd.services.service.RicezioneContenutiApplicativiServiceUtils;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * RicezioneContenutiApplicativiConnector
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public class RicezioneContenutiApplicativiHTTPtoSOAPConnectorAsync extends AbstractRicezioneConnectorAsync {


	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public final static IDService ID_SERVICE = IDService.PORTA_DELEGATA_XML_TO_SOAP_NIO;
	public final static String ID_MODULO = ID_SERVICE.getValue();

	@Override
	protected IDService getIdService() {
		return ID_SERVICE;
	}
	@Override
	protected String getIdModulo() {
		return ID_MODULO;
	}
	
	@Override
	public void doEngine(RequestInfo requestInfo, 
			HttpServletRequest req, HttpServletResponse res, HttpRequestMethod method) throws ServletException, IOException {
		
		if(!HttpRequestMethod.POST.equals(method)){

			ConnectorDispatcherUtils.doMethodNotSupported(req, res, method, ID_SERVICE);
			return;
			
		}
		
		super.doEngine(requestInfo, req, res, method);
	}
	
	@Override
	protected AbstractErrorGenerator getErrorGenerator(Logger logCore, RequestInfo requestInfo) throws Exception{
		RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore = new RicezioneContenutiApplicativiInternalErrorGenerator(logCore, ID_MODULO, requestInfo);
		RicezioneContenutiApplicativiHTTPtoSOAPService.forceXmlResponse(generatoreErrore);
		return generatoreErrore;
	}
	@Override
	protected void doError(RequestInfo requestInfo, AbstractErrorGenerator generatoreErroreParam, 
			ErroreIntegrazione erroreIntegrazione, IntegrationFunctionError integrationFunctionError,
			Throwable t, HttpServletResponse res,  Logger logCore) {
		RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore = (RicezioneContenutiApplicativiInternalErrorGenerator) generatoreErroreParam;
		ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
				erroreIntegrazione, 
				integrationFunctionError, t, res, logCore);
	}
	@Override
	protected ConnectorDispatcherErrorInfo doError(RequestInfo requestInfo, AbstractErrorGenerator generatoreErroreParam, 
			ErroreIntegrazione erroreIntegrazione, IntegrationFunctionError integrationFunctionError, 
			Throwable t, ParseException parseException,
			ConnectorOutMessage res, Logger log, boolean clientError) throws ConnectorException{
		RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore = (RicezioneContenutiApplicativiInternalErrorGenerator) generatoreErroreParam;
		return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
				erroreIntegrazione, integrationFunctionError, 
				t, null, res, log, clientError);
	}
	
	@Override
	protected void emitTransaction(Logger logCore, ConnectorInMessage req, PdDContext pddContext, Date dataAccettazioneRichiesta, ConnectorDispatcherInfo info) {
		RicezioneContenutiApplicativiServiceUtils.emitTransaction(logCore, req, pddContext, dataAccettazioneRichiesta, info);
	}
	
	@Override
	protected long getTimeout() {
		return OpenSPCoop2Properties.getInstance().getNodeReceiverTimeoutRicezioneContenutiApplicativi();
	}
	
	@Override
	protected IRicezioneService newRicezioneService(AbstractErrorGenerator generatoreErroreParam) {
		RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore = (RicezioneContenutiApplicativiInternalErrorGenerator) generatoreErroreParam;
		return new RicezioneContenutiApplicativiHTTPtoSOAPService(generatoreErrore);
	}

}
