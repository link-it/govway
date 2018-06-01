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
