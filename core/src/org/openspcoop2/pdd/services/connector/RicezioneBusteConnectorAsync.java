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



package org.openspcoop2.pdd.services.connector;

import java.util.Date;

import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.services.service.IRicezioneService;
import org.openspcoop2.pdd.services.service.RicezioneBusteService;
import org.openspcoop2.pdd.services.service.RicezioneBusteServiceUtils;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletResponse;

/**
 * RicezioneBusteConnectorAsync
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public class RicezioneBusteConnectorAsync extends AbstractRicezioneConnectorAsync {


	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public static final IDService ID_SERVICE = getIdServiceFromProperties();
	public static final String ID_MODULO = ID_SERVICE.getValue();

	private static IDService getIdServiceFromProperties() {
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		if(op2Properties!=null && op2Properties.isEnabledPDChannelNIODefault()) {
			return IDService.PORTA_APPLICATIVA;
		}
		else {
			return IDService.PORTA_APPLICATIVA_NIO;
		}
	}
	@Override
	protected IDService getIdService() {
		return ID_SERVICE;
	}
	@Override
	protected String getIdModulo() {
		return ID_MODULO;
	}
	
	@Override
	protected AbstractErrorGenerator getErrorGenerator(Logger logCore, RequestInfo requestInfo) throws ConnectorException{
		try {
			return new RicezioneBusteExternalErrorGenerator(logCore, ID_MODULO, requestInfo, null);
		}catch(Exception e) {
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	@Override
	protected void doError(RequestInfo requestInfo, AbstractErrorGenerator generatoreErroreParam, 
			ErroreIntegrazione erroreIntegrazione, IntegrationFunctionError integrationFunctionError,
			Throwable t, HttpServletResponse res,  Logger logCore) {
		RicezioneBusteExternalErrorGenerator generatoreErrore = (RicezioneBusteExternalErrorGenerator) generatoreErroreParam;
		ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
				erroreIntegrazione, 
				integrationFunctionError, t, res, logCore);
	}
	@Override
	protected ConnectorDispatcherErrorInfo doError(RequestInfo requestInfo, AbstractErrorGenerator generatoreErroreParam, 
			ErroreIntegrazione erroreIntegrazione, IntegrationFunctionError integrationFunctionError, 
			Throwable t, ParseException parseException,
			ConnectorOutMessage res, Logger log, boolean clientError) throws ConnectorException{
		RicezioneBusteExternalErrorGenerator generatoreErrore = (RicezioneBusteExternalErrorGenerator) generatoreErroreParam;
		return ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
				erroreIntegrazione, integrationFunctionError, 
				t, null, res, log, clientError);
	}
	
	@Override
	protected void emitTransaction(Logger logCore, ConnectorInMessage req, PdDContext pddContext, Date dataAccettazioneRichiesta, ConnectorDispatcherInfo info) {
		RicezioneBusteServiceUtils.emitTransaction(logCore, req, null, dataAccettazioneRichiesta, info);
	}
	
	@Override
	protected long getTimeout() {
		return OpenSPCoop2Properties.getInstance().getNodeReceiverTimeoutRicezioneBuste();
	}
	
	@Override
	protected IRicezioneService newRicezioneService(AbstractErrorGenerator generatoreErroreParam) {
		RicezioneBusteExternalErrorGenerator generatoreErrore = (RicezioneBusteExternalErrorGenerator) generatoreErroreParam;
		return new RicezioneBusteService(generatoreErrore);
	}
	
}
