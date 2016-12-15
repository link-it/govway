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


package org.openspcoop2.pdd.core.autorizzazione.pa;

import java.io.ByteArrayOutputStream;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;

/**
 * Esempio di AutorizzazioneContenutoBusteKO
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneContenutoBusteKO extends AbstractCore implements IAutorizzazioneContenutoPortaApplicativa {

	@Override
	public EsitoAutorizzazioneCooperazione process(DatiInvocazionePortaApplicativa datiInvocazione, OpenSPCoop2Message msg) throws AutorizzazioneException {
		
		EsitoAutorizzazioneCooperazione esito = new EsitoAutorizzazioneCooperazione();
    	
    	// Autorizzazzione servizio applicativo
    	try{
    		
    		ByteArrayOutputStream bout = new ByteArrayOutputStream();
    		msg.writeTo(bout, false);
    		bout.flush();
    		bout.close();
    		
    		System.out.println("(TestKO) Messaggio ricevuto (Ruolo busta: "+datiInvocazione.getRuoloBusta().toString()+"): "+bout.toString());
        	
    		IDSoggetto soggettoFruitore = datiInvocazione.getIdSoggettoFruitore();
    		IDServizio servizio = datiInvocazione.getIdServizio();
    		
    		String errore = "Il soggetto "+soggettoFruitore.getTipo()+"/"+soggettoFruitore.getNome() +" non e' autorizzato ad invocare il servizio "+
    				IDServizioFactory.getInstance().getUriFromIDServizio(servizio)+" con il contenuto applicativo fornito";
			
    		esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
			esito.setServizioAutorizzato(false);
        	return esito;
    		
    	}catch(Exception e){
    		esito.setEccezioneProcessamento(e);
    		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione per contenuto non riuscita",e);
    		throw new AutorizzazioneException("Errore di processamento durante l'autorizzazione per contenuto buste: "+e.getMessage(),e);
    	}
	}

	
	
}
