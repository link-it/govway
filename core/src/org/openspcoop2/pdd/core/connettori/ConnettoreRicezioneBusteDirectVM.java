/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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




package org.openspcoop2.pdd.core.connettori;

import java.util.Date;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.DirectVMConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.DirectVMConnectorOutMessage;
import org.openspcoop2.pdd.services.core.RicezioneBuste;
import org.openspcoop2.pdd.services.service.RicezioneBusteService;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.date.DateManager;

/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreRicezioneBusteDirectVM extends AbstractConnettoreDirectVM {

	public static final String TIPO = "vmPA";
	
	private String pa;
	
	@Override
	public String getIdModulo(){
		return RicezioneBuste.ID_MODULO+"_VM";
	}
	@Override
	public IDService getIdModuloAsIDService() {
		return IDService.PORTA_APPLICATIVA;
	}
	@Override
	public String getFunction(){
		return URLProtocolContext.PA_FUNCTION;
	}
	@Override
	public void process(DirectVMConnectorInMessage inMessage,DirectVMConnectorOutMessage outMessage) throws ConnectorException{
		RicezioneBusteService soapConnector = new RicezioneBusteService(null); // il generatore di errori verrà creato direttamente dal servizio
		Date dataAccettazioneRichiesta = DateManager.getDate();
		soapConnector.process(inMessage, outMessage, dataAccettazioneRichiesta);
	}
	@Override
	public boolean validate(ConnettoreMsg request) {
		
		if(request.getConnectorProperties().get(CostantiConnettori.CONNETTORE_DIRECT_VM_PA)==null){
			// La PA in alcuni protocolli puo' non essere fornita
//			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_DIRECT_VM_PA+"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
//			return false;
		}
		else{
			this.pa = request.getConnectorProperties().get(CostantiConnettori.CONNETTORE_DIRECT_VM_PA).trim();
		}
		
		return true;
	}
	@Override
	public String getFunctionParameters() {
		return super.normalizeFunctionParamters(this.pa);
	}

}





