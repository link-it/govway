/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestContext;

/**     
 * InRequestHandler
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InRequestHandler extends FirstPositionHandler implements  org.openspcoop2.pdd.core.handlers.InRequestHandler{

	@Override
	public void invoke(InRequestContext context) throws HandlerException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		if(op2Properties.isTransazioniEnabled()==false) {
			return;
		}
		
		String idTransazione = (String) context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
		
		//System.out.println("------------- InRequestHandler ("+idTransazione+")("+context.getTipoPorta().getTipo()+") -------------------");
		
		try{
		
			Transaction tr = TransactionContext.getTransaction(idTransazione);
			
			tr.setDataAccettazioneRichiesta(context.getDataAccettazioneRichiesta());
			tr.setDataIngressoRichiesta(context.getDataElaborazioneMessaggio());
			//System.out.println("SET DATA ("+context.getDataElaborazioneMessaggio().toString()+")");
			
			if(context.getConnettore()!=null){
				
				// aggiorno valori rispetto a quelli raccolti in preInRequest
				
				Credenziali credenziali = context.getConnettore().getCredenziali();
				String credenzialiFornite = "";
				if(credenziali!=null){
					credenzialiFornite = credenziali.toString(!Credenziali.SHOW_BASIC_PASSWORD,
							Credenziali.SHOW_ISSUER,
							!Credenziali.SHOW_DIGEST_CLIENT_CERT,
							Credenziali.SHOW_SERIAL_NUMBER_CLIENT_CERT,
							"","","\n"); // riporto anche l'issuer ed il serial number del cert e formatto differentemente
				}
				tr.setCredenziali(credenzialiFornite);
				//System.out.println("SET CREDENZIALI ["+credenzialiFornite+"]");
				
				if(context.getConnettore().getUrlProtocolContext()!=null){
					String urlInvocazione = context.getConnettore().getUrlProtocolContext().getUrlInvocazione_formBased();
					if(context.getConnettore().getUrlProtocolContext().getFunction()!=null){
						urlInvocazione = "["+context.getConnettore().getUrlProtocolContext().getFunction()+"] "+urlInvocazione;
					}
					//System.out.println("SET URL INVOCAZIONE ["+urlInvocazione+"]");
					tr.setUrlInvocazione(urlInvocazione);
				}
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
