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




package org.openspcoop2.pdd.core.connettori;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.pdd.services.RicezioneBuste;
import org.openspcoop2.pdd.services.RicezioneBusteSOAP;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.DirectVMConnectorInMessage;
import org.openspcoop2.pdd.services.connector.DirectVMConnectorOutMessage;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;

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

	public final static String TIPO = "vmPA";
	
	private String pa;
	
	@Override
	public String getIdModulo(){
		return RicezioneBuste.ID_MODULO+"_VM";
	}
	@Override
	public IDService getIdModuloAsIDService() {
		return IDService.PORTA_APPLICATIVA_SOAP;
	}
	@Override
	public String getFunction(){
		return URLProtocolContext.PA_FUNCTION;
	}
	@Override
	public void process(DirectVMConnectorInMessage inMessage,DirectVMConnectorOutMessage outMessage) throws ConnectorException{
		RicezioneBusteSOAP soapConnector = new RicezioneBusteSOAP();
		soapConnector.process(inMessage, outMessage);
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





