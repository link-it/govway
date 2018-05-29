package org.openspcoop2.pdd.core.handlers.transazioni;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutResponseContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;

public class OutResponseHandler extends LastPositionHandler implements  org.openspcoop2.pdd.core.handlers.OutResponseHandler{

	@Override
	public void invoke(OutResponseContext context) throws HandlerException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		if(op2Properties.isTransazioniEnabled()==false) {
			return;
		}
		
		String idTransazione = (String) context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
		
		//System.out.println("------------- OutResponseHandler ("+idTransazione+")("+context.getTipoPorta().getTipo()+") -------------------");
		
		//if(context.getIntegrazione()!=null)
		//	System.out.println("GESTIONE STATELESS OutResponseHandler ["+context.getIntegrazione().isGestioneStateless()+"] ["+context.getTipoPorta()+"]");
		// NOTA: se l'integrazione e' null o l'indicazione se la gestione stateless e' null, significa che la PdD non e' ancora riuscita
		// a capire che tipo di gestione deve adottare. Queste transazioni devono essere sempre registrate perche' riguardano cooperazioni andate in errore all'inizio,
		// es. Porta Delegata non esistente, busta malformata....
		if(context.getIntegrazione()!=null && 
				context.getIntegrazione().isGestioneStateless()!=null &&
				!context.getIntegrazione().isGestioneStateless()){
			if(op2Properties.isTransazioniStatefulEnabled()==false){
				throw new HandlerException("Gestione delle transazioni stateful non abilita");
			}
		}
		
		Transaction tr = null;
		try{
		
			tr = TransactionContext.getTransaction(idTransazione,false);
			
			tr.setDataUscitaRisposta(context.getDataElaborazioneMessaggio());
			//System.out.println("SET DATA ("+context.getDataElaborazioneMessaggio().toString()+")");
		
		}catch(TransactionDeletedException e){
			throw new HandlerException(e);
			// Non dovrebbe avvenire in questo handler
		}catch(TransactionNotExistsException e){
			throw new HandlerException(e);
			// Non dovrebbe avvenire in questo handler
		}
//		catch(HandlerException e){
//			throw e;
//		}
		catch(Exception e){
			throw new HandlerException(e); 
		}

	}


}
