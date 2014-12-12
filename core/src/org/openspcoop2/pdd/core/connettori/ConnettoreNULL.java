/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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

import org.apache.log4j.Logger;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;


/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreNULL extends ConnettoreBase {

	/** Logger utilizzato per debug. */
	private static Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	
	public final static String LOCATION = "openspcoop2://dev/null";
    
	



	/* ********  METODI  ******** */

	/**
	 * Si occupa di effettuare la consegna.
	 *
	 * @param request Messaggio da Consegnare
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	@Override
	public boolean send(ConnettoreMsg request){

		// Context per invocazioni handler
		this.outRequestContext = request.getOutRequestContext();
		this.msgDiagnostico = request.getMsgDiagnostico();
		
		this.codice = 200;
		
		try{
			
			// SIMULAZIONE WRITE_TO
			
			org.apache.commons.io.output.NullOutputStream nullOutputStream = new org.apache.commons.io.output.NullOutputStream();
			request.getRequestMessage().writeTo(nullOutputStream,true);
			nullOutputStream.flush();
			nullOutputStream.close();
		
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			ConnettoreNULL.log.error("Riscontrato errore durante la writeTo",e);
			this.errore = "Riscontrato errore durante la writeTo: " +e.getMessage();
			return false;
		}
		
		try{
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			ConnettoreNULL.log.error("Riscontrato errore durante la gestione PostOutRequestHandler",e);
			this.errore = "Riscontrato errore durante la gestione PostOutRequestHandler: " +e.getMessage();
			return false;
		}
			
//		org.openspcoop2.utils.io.notifier.NotifierInputStreamParams notifierInputStreamParams = null;
//		try{
//			
//			/* ------------  PreInResponseHandler ------------- */
//			this.preInResponse();
//			
//			// Lettura risposta parametri NotifierInputStream per la risposta
//			if(this.preInResponseContext!=null){
//				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
//			}
//			
//		}catch(Exception e){
//			this.eccezioneProcessamento = e;
//			ConnettoreNULL.log.error("Riscontrato errore durante la gestione PreInResponseHandler",e);
//			this.errore = "Riscontrato errore durante la gestione PreInResponseHandler: " +e.getMessage();
//			return false;
//		}
		
		try{
					
			this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(request.getRequestMessage().getVersioneSoap());
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			ConnettoreNULL.log.error("Riscontrato errore durante la generazione di un msg SoapVuoto",e);
			this.errore = "Riscontrato errore durante la generazione di un msg SoapVuoto: " +e.getMessage();
			return false;
		}
		
		return true;
	}

    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation(){
    	return LOCATION;
    }
    
}





