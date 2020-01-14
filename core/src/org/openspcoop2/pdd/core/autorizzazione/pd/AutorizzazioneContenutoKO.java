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


package org.openspcoop2.pdd.core.autorizzazione.pd;


import java.io.ByteArrayOutputStream;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;

/**
 * Esempio di AutorizzazioneContenutoKO
 *
 * @author Andrea Poli <apoli@link.oit>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneContenutoKO extends AbstractAutorizzazioneContenutoBase {

	@Override
	public EsitoAutorizzazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione,OpenSPCoop2Message msg) throws AutorizzazioneException {

		EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
    	
    	// Autorizzazzione servizio applicativoo
    	try{
    		ByteArrayOutputStream bout = new ByteArrayOutputStream();
    		msg.writeTo(bout, false);
    		bout.flush();
    		bout.close();
    		
    		System.out.println("(TestKO) Messaggio ricevuto: "+bout.toString());
        	
    		String servizioApplicativo = null;
    		if(datiInvocazione.getIdServizioApplicativo()!=null){
    			servizioApplicativo = datiInvocazione.getIdServizioApplicativo().getNome();
    		}
    		
    		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA.getErrore428_AutorizzazioneContenutoFallita(servizioApplicativo));
    		esito.setAutorizzato(false);
    		return esito;
    		
    	}catch(Exception e){
    		esito.setEccezioneProcessamento(e);
    		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione per contenuto non riuscita",e);
    		throw new AutorizzazioneException("Errore di processamento durante l'autorizzazione per contenuto: "+e.getMessage(),e);
    	}
	

	}

}
