/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


import java.util.List;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazioneContenutiBuiltIn;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**
 * Esempio di AutorizzazioneContenutoBuiltIn
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneContenutoBuiltIn extends AbstractAutorizzazioneContenutoBase {

	@Override
	public EsitoAutorizzazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione,OpenSPCoop2Message msg) throws AutorizzazioneException {

		EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
    	
    	// Autorizzazzione servizio applicativo
    	try{
    		if(datiInvocazione.getPd()==null) {
				throw new Exception("Porta Delegata non presente");
			}
    		List<Proprieta> regole = datiInvocazione.getPd().getProprietaAutorizzazioneContenutoList();
    		
    		GestoreAutorizzazioneContenutiBuiltIn gestore = new GestoreAutorizzazioneContenutiBuiltIn();
    		gestore.process(msg, datiInvocazione, this.getPddContext(), regole);
    		
    		if(!gestore.isAutorizzato()) {
    			
        		String servizioApplicativo = null;
        		if(datiInvocazione.getIdServizioApplicativo()!=null){
        			servizioApplicativo = datiInvocazione.getIdServizioApplicativo().getNome();
        		}
        		if(servizioApplicativo==null) {
        			servizioApplicativo = CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO;
        		}
        		
        		esito.setErroreIntegrazione(IntegrationFunctionError.CONTENT_AUTHORIZATION_DENY, ErroriIntegrazione.ERRORE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA.getErrore428_AutorizzazioneContenutoFallita(servizioApplicativo));
        		esito.setAutorizzato(false);
        		esito.setDetails(gestore.getErrorMessage());
    		}
    		else {
    			esito.setAutorizzato(true);
    		}

    		return esito;
    		
    	}catch(Exception e){
    		esito.setEccezioneProcessamento(e);
    		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione per contenuto non riuscita",e);
    		throw new AutorizzazioneException("Errore inatteso durante la gestione dell'autorizzazione per contenuti: "+e.getMessage(),e);
    	}
	

	}

}
