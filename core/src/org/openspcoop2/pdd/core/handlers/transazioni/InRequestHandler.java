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
					credenzialiFornite = credenziali.toString();
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
