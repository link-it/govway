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

import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;

/**
 * Classe che implementa una autorizzazione basata sui ruoli
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

abstract class AbstractAutorizzazioneAuthenticatedRoles extends AbstractAutorizzazioneBase {

	private boolean checkRuoloRegistro;
	private boolean checkRuoloEsterno;
	@SuppressWarnings("unused")
	private String nomeAutorizzazione;
	
	protected AbstractAutorizzazioneAuthenticatedRoles(boolean checkRuoloRegistro, boolean checkRuoloEsterno, String nomeAutorizzazione){
		this.checkRuoloRegistro = checkRuoloRegistro;
		this.checkRuoloEsterno = checkRuoloEsterno;
		this.nomeAutorizzazione = nomeAutorizzazione;
	}
	

    @Override
	public EsitoAutorizzazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutorizzazioneException{

    	EsitoAutorizzazionePortaDelegata esitoAuth = null;
    	if(datiInvocazione.getIdServizioApplicativo()!=null){
    		// Se il servizio applicativo non è stato identificato non è possibile effettuare il controllo 'authenticated'
	    	AutorizzazioneAuthenticated auth = new AutorizzazioneAuthenticated();
	    	auth.init(this.getPddContext(), this.getProtocolFactory(), this.getArgs());
	    	esitoAuth = auth.process(datiInvocazione);
	    	if(esitoAuth.isAutorizzato()){
	    		return esitoAuth;
	    	}
    	}
    	
    	IAutorizzazionePortaDelegata authRuoli = null;
    	if(this.checkRuoloRegistro && this.checkRuoloEsterno){
    		authRuoli = new AutorizzazioneRoles();
    	}
    	else if(this.checkRuoloRegistro){
    		authRuoli = new AutorizzazioneInternalRoles();
    	}
    	else {
    		authRuoli = new AutorizzazioneExternalRoles();
    	}
    	authRuoli.init(this.getPddContext(), this.getProtocolFactory(), this.getArgs());
    	EsitoAutorizzazionePortaDelegata esitoAuthRoles = authRuoli.process(datiInvocazione);
    	if(esitoAuthRoles.isAutorizzato()){
    		return esitoAuthRoles;
    	}
    	
    	// Se non è autorizzato in nessuno dei due modi ritorno l'errore del tipo authenticated
    	if(esitoAuth!=null && !CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(datiInvocazione.getIdServizioApplicativo().getNome())){
    		return esitoAuth;
    	}
    	else{
    		return esitoAuthRoles;
    	}
    }
   
}

