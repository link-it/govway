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



package org.openspcoop2.pdd.core.autorizzazione.pa;

import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;

/**
 * Interfaccia che definisce un processo di autorizzazione sui ruoli
 *
 * @author Andrea Poli <apoli@link.it>
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

    		IDSoggetto idSoggetto = datiInvocazione.getIdSoggettoFruitore();
    		IDServizio idServizio = datiInvocazione.getIdServizio();
    		errore = this.getErrorString(idSoggetto, idServizio);
    		
    		if( ConfigurazionePdDManager.getInstance(datiInvocazione.getState()).
    				autorizzazioneRoles(datiInvocazione.getPa(), datiInvocazione.getSoggettoFruitore(), 
    						datiInvocazione.getInfoConnettoreIngresso(), 
    						this.getPddContext(),
    						this.checkRuoloRegistro, this.checkRuoloEsterno)==false){
    			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
    			esito.setAutorizzato(false);
    		}else{
    			esito.setAutorizzato(true);
    		}
    	}catch(DriverConfigurazioneNotFound e){
    		if(errore!=null){
    			errore = errore + " ";
    		}
			errore = errore + "("+e.getMessage()+")";
			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
    	}catch(Exception e){
    		errore = "Errore durante l'autorizzazione di tipo '"+this.nomeAutorizzazione+"': "+e.getMessage();
    		esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
    	}
    	
    	return esito;
    }
	
}
