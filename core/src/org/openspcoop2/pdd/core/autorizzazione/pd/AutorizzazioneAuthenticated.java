/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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



package org.openspcoop2.pdd.core.autorizzazione.pd;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;

/**
 * Classe che implementa una autorizzazione basata sui servizi applicativi autenticati.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneAuthenticated extends AbstractAutorizzazioneBase {

    @Override
	public EsitoAutorizzazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutorizzazioneException{

    	EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
    	
    	if(datiInvocazione.getIdServizioApplicativo()==null){
    		throw new AutorizzazioneException("Identità servizio applicativo non disponibile; tale informazione è richiesta dall'autorizzazione");
		}	
    	String servizioApplicativo = datiInvocazione.getIdServizioApplicativo().getNome();
    	
    	// Autorizzazzione servizio applicativo
    	try{
    		if( ConfigurazionePdDManager.getInstance(datiInvocazione.getState()).
    				autorizzazione(datiInvocazione.getPd(),servizioApplicativo) == false ){
    			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
    					getErrore404_AutorizzazioneFallitaServizioApplicativo(servizioApplicativo));
    			esito.setAutorizzato(false);
    			return esito;
    		}
    	}catch(DriverConfigurazioneNotFound e){
    		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.getErrore401_PortaInesistente(e.getMessage(), servizioApplicativo));
			esito.setAutorizzato(false);
			return esito;
    	}catch(DriverConfigurazioneException e){
    		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutorizzazioneOpenSPCoop non riuscita",e);
    		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    				get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setAutorizzato(false);
			esito.setEccezioneProcessamento(e);
			return esito;
    	}
	
    	esito.setAutorizzato(true);
    	return esito;
    }
   
}

