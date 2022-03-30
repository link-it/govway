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

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**
 * Classe che implementa una autorizzazione basata sui ruoli
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

abstract class AbstractAutorizzazioneRoles extends AbstractAutorizzazioneBase {

	private boolean checkRuoloRegistro;
	private boolean checkRuoloEsterno;
	private String nomeAutorizzazione;
	
	protected AbstractAutorizzazioneRoles(boolean checkRuoloRegistro, boolean checkRuoloEsterno, String nomeAutorizzazione){
		this.checkRuoloRegistro = checkRuoloRegistro;
		this.checkRuoloEsterno = checkRuoloEsterno;
		this.nomeAutorizzazione = nomeAutorizzazione;
	}
	

    @Override
	public EsitoAutorizzazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutorizzazioneException{

    	EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
    	
    	String servizioApplicativo = null;
    	if(datiInvocazione.getIdServizioApplicativo()!=null){
    		servizioApplicativo = datiInvocazione.getIdServizioApplicativo().getNome();
    	}
    	
    	try{
    		StringBuilder detailsBuffer = new StringBuilder();
    		if( ConfigurazionePdDManager.getInstance(datiInvocazione.getState()).
    				autorizzazioneRoles(datiInvocazione.getPd(), datiInvocazione.getServizioApplicativo(), 
    						datiInvocazione.getInfoConnettoreIngresso(), 
    						this.getPddContext(),
    						this.checkRuoloRegistro, this.checkRuoloEsterno,
    						detailsBuffer)==false){
    			if(servizioApplicativo!=null){
	    			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
	    					getErrore404_AutorizzazioneFallitaServizioApplicativo(servizioApplicativo));
    			}
    			else{
    				esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
    						getErrore404_AutorizzazioneFallitaServizioApplicativoAnonimo());
    			}
    			esito.setAutorizzato(false);
    			if(detailsBuffer.length()>0) {
    				esito.setDetails(detailsBuffer.toString());
    			}
    			return esito;
    		}
    	}catch(DriverConfigurazioneNotFound e){
    		if(servizioApplicativo!=null){
    			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
    					getErrore404_AutorizzazioneFallitaServizioApplicativo(servizioApplicativo,e.getMessage()));
			}
			else{
				esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
						getErrore404_AutorizzazioneFallitaServizioApplicativoAnonimo(e.getMessage()));
			}
			esito.setAutorizzato(false);
			esito.setEccezioneProcessamento(e);
			return esito;
    	}catch(DriverConfigurazioneException e){
    		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione "+this.nomeAutorizzazione+" non riuscita",e);
    		esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    				get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setAutorizzato(false);
			esito.setEccezioneProcessamento(e);
			return esito;
    	}
	
    	esito.setAutorizzato(true);
    	return esito;
    }

}

