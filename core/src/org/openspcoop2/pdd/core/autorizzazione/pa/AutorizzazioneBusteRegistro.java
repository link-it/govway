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

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziServizioNotFound;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.protocol.registry.EsitoAutorizzazioneRegistro;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;

/**
 * Interfaccia che definisce un processo di autorizzazione per i soggetti.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneBusteRegistro extends AbstractCore implements IAutorizzazionePortaApplicativa {


    @Override
	public EsitoAutorizzazioneCooperazione process(DatiInvocazionePortaApplicativa datiInvocazione){
    	
    	EsitoAutorizzazioneCooperazione esito = new EsitoAutorizzazioneCooperazione();
    	
    	try{
    		RegistroServiziManager reg = RegistroServiziManager.getInstance(datiInvocazione.getState());
    		
    		Credenziali credenzialiPdDMittente = datiInvocazione.getCredenzialiPdDMittente();
    		String pdd = null;
    		if(credenzialiPdDMittente!=null){
    			if(credenzialiPdDMittente.getSubject()!=null){
    				pdd = credenzialiPdDMittente.getSubject();
    			}
    		}
    		
    		String identitaServizioApplicativoFruitore = null;
    		if(datiInvocazione.getIdentitaServizioApplicativoFruitore()!=null){
    			identitaServizioApplicativoFruitore = datiInvocazione.getIdentitaServizioApplicativoFruitore().getNome();
    		}
    		
    		IDSoggetto idSoggetto = datiInvocazione.getIdSoggettoFruitore();
    		IDServizio idServizio = datiInvocazione.getIdServizio();
    		
    		EsitoAutorizzazioneRegistro esitoAutorizzazione = reg.isFruitoreServizioAutorizzato(pdd, identitaServizioApplicativoFruitore, idSoggetto, idServizio);
    		if(esitoAutorizzazione.isServizioAutorizzato()==false){
    			String errore = "Il soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome() +" non e' autorizzato ad invocare il servizio "+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+" erogato da "+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome();
    			if(esitoAutorizzazione.getDetails()!=null){
    				errore = errore + " ("+esitoAutorizzazione.getDetails()+")";
    			}
    			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
    			esito.setServizioAutorizzato(false);
    		}else{
    			esito.setServizioAutorizzato(true);
    			if(esitoAutorizzazione.getDetails()!=null){
    				esito.setDetails(esitoAutorizzazione.getDetails());
    			}
    		}
    	}catch(DriverRegistroServiziServizioNotFound e){
    		esito.setErroreCooperazione(ErroriCooperazione.SERVIZIO_SCONOSCIUTO.
    				getErroreCooperazione("Errore durante il processo di autorizzazione (ServizioNotFound): "+e.getMessage()));
    		esito.setServizioAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
    	}catch(Exception e){
    		String errore = "Errore durante il processo di autorizzazione: "+e.getMessage();
    		esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
    		esito.setServizioAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
    	}
    	
    	return esito;
    }
    
    
	@Override
	public boolean saveAuthorizationResultInCache() {
		return true;
	}
	
}
