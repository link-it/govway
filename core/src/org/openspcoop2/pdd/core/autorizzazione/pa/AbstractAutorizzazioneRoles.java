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



package org.openspcoop2.pdd.core.autorizzazione.pa;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**
 * Interfaccia che definisce un processo di autorizzazione sui ruoli
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
	public EsitoAutorizzazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione){
    	
    	EsitoAutorizzazionePortaApplicativa esito = new EsitoAutorizzazionePortaApplicativa();
    	
    	String errore = "";
    	try{

    		IDServizioApplicativo idSA = datiInvocazione.getIdentitaServizioApplicativoFruitore();
    		IDSoggetto idSoggetto = datiInvocazione.getIdSoggettoFruitore();
    		IDServizio idServizio = datiInvocazione.getIdServizio();
    		errore = AbstractAutorizzazioneBase.getErrorString(idSA, idSoggetto, idServizio);
    		
    		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState()); 
    		
    		ServizioApplicativo sa = null;
    		if(idSA!=null && idSA.getNome()!=null) {
    			sa = configurazionePdDManager.getServizioApplicativo(idSA, datiInvocazione.getRequestInfo());
    		}
    		
    		StringBuilder detailsBuffer = new StringBuilder();
    		if(configurazionePdDManager.
    				autorizzazioneTrasportoRoles(datiInvocazione.getPa(), datiInvocazione.getSoggettoFruitore(), sa,
    						datiInvocazione.getInfoConnettoreIngresso(), 
    						this.getPddContext(), datiInvocazione.getRequestInfo(),
    						this.checkRuoloRegistro, this.checkRuoloEsterno,
    						detailsBuffer)==false){
    			esito.setErroreCooperazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
    			esito.setAutorizzato(false);
    			if(detailsBuffer.length()>0) {
    				esito.setDetails(detailsBuffer.toString());
    			}
    		}else{
    			esito.setAutorizzato(true);
    		}
    	}catch(DriverConfigurazioneNotFound e){
    		if(errore!=null){
    			errore = errore + " ";
    		}
			errore = errore + "("+e.getMessage()+")";
			esito.setErroreCooperazione(IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE, ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
    	}catch(Exception e){
    		errore = "Errore durante l'autorizzazione di tipo '"+this.nomeAutorizzazione+"': "+e.getMessage();
    		esito.setErroreCooperazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
    	}
    	
    	return esito;
    }
	
}
