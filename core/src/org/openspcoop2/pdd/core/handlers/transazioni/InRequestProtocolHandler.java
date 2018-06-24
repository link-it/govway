/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.pdd.core.handlers.transazioni;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.credenziali.engine.GestoreCredenzialiEngine;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;

/**     
 * InRequestProtocolHandler
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InRequestProtocolHandler extends FirstPositionHandler implements  org.openspcoop2.pdd.core.handlers.InRequestProtocolHandler{

	@Override
	public void invoke(InRequestProtocolContext context) throws HandlerException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		if(op2Properties.isTransazioniEnabled()==false) {
			return;
		}
		
		String idTransazione = (String) context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
		
		//System.out.println("------------- InRequestHandler ("+idTransazione+")("+context.getTipoPorta().getTipo()+") -------------------");
		
		try{
		
			Transaction tr = TransactionContext.getTransaction(idTransazione);
			
			if(context.getConnettore()!=null){
				Credenziali credenziali = context.getConnettore().getCredenziali();
				String credenzialiFornite = "";
				if(credenziali!=null && !"".equals(credenziali.toString())){
					credenzialiFornite = credenziali.toString();
				}
					
				boolean credenzialiModificateTramiteGateway = false;
				if(tr.getCredenziali()!=null){
					if(tr.getCredenziali().equals(credenzialiFornite) == false){
						credenzialiModificateTramiteGateway=true;
					}
				}
				if(credenzialiModificateTramiteGateway==true){					
					tr.setCredenziali(GestoreCredenzialiEngine.KEYWORD_GATEWAY_CREDENZIALI+credenzialiFornite);
					//System.out.println("SET CREDENZIALI VIA GATEWAY ["+credenzialiFornite+"]");
				}
			}
			
			if(op2Properties.isControlloTrafficoEnabled()){
				InRequestProtocolHandler_GestioneControlloTraffico inRequestProtocolHandler_gestioneControlloTraffico = 
						new InRequestProtocolHandler_GestioneControlloTraffico();
				inRequestProtocolHandler_gestioneControlloTraffico.process(context, tr);
			}
			
			
		}catch(TransactionDeletedException e){
			throw new HandlerException(e);
			// Non dovrebbe avvenire in questo handler
		}catch(TransactionNotExistsException e){
			throw new HandlerException(e);
			// Non dovrebbe avvenire in questo handler
		}
		
	}

}
