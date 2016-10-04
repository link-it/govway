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


package org.openspcoop2.pdd.core.autorizzazione.pd;


import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

/**
 * Esempio di AutorizzazioneContenutoOK
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneContenutoOK extends AbstractCore implements IAutorizzazioneContenutoPortaDelegata {

	@Override
	public EsitoAutorizzazioneIntegrazione process(DatiInvocazionePortaDelegata datiInvocazione,OpenSPCoop2Message msg) throws AutorizzazioneException {

		EsitoAutorizzazioneIntegrazione esito = new EsitoAutorizzazioneIntegrazione();
    	
    	// Autorizzazzione servizio applicativo
    	try{
    		
    		byte[] msgBytes = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(msg.getVersioneSoap()).getAsByte(msg.getSOAPBody(), false);
    		System.out.println("(TestOK) Messaggio ricevuto: "+new String(msgBytes));
        	
    		esito.setServizioAutorizzato(true);
        	return esito;
    		
    	}catch(Exception e){
    		esito.setEccezioneProcessamento(e);
    		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione per contenuto non riuscita",e);
    		throw new AutorizzazioneException("Errore di processamento durante l'autorizzazione per contenuto: "+e.getMessage(),e);
    	}
	

	}

}
