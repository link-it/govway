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



package org.openspcoop2.pdd.core.token.pa;

import org.openspcoop2.pdd.core.token.AbstractDatiInvocazione;
import org.openspcoop2.pdd.core.token.EsitoGestioneToken;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.slf4j.Logger;

/**
 * Classe che implementala gestione token
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 13726 $, $Date: 2018-03-12 17:01:57 +0100 (Mon, 12 Mar 2018) $
 */

public class GestioneToken {

	private Logger log;
	private String idTransazione;
	public GestioneToken(Logger log, String idTransazione) {
		this.log = log;
		this.idTransazione = idTransazione;
	}
	
    public EsitoPresenzaTokenPortaApplicativa verificaPresenzaToken(DatiInvocazionePortaApplicativa datiInvocazione) throws TokenException{

    	EsitoPresenzaTokenPortaApplicativa esito = (EsitoPresenzaTokenPortaApplicativa) GestoreToken.verificaPosizioneToken(this.log, datiInvocazione, GestoreToken.PORTA_APPLICATIVA);
    	
    	if(esito.getEccezioneProcessamento()!=null) {
    		esito.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
    	}
    	
    	return esito;
    	
    }
    
    public EsitoGestioneTokenPortaApplicativa validazioneJWTToken(AbstractDatiInvocazione datiInvocazione, String token) throws TokenException {   	
    	try {
    	
    		EsitoGestioneTokenPortaApplicativa esito = (EsitoGestioneTokenPortaApplicativa) GestoreToken.validazioneJWTToken(this.log, datiInvocazione, token, GestoreToken.PORTA_APPLICATIVA);
    		
        	if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	} 	
    }
    
    public EsitoGestioneTokenPortaApplicativa introspectionToken(AbstractDatiInvocazione datiInvocazione, String token) throws TokenException {
    	try {
        	
    		EsitoGestioneTokenPortaApplicativa esito = (EsitoGestioneTokenPortaApplicativa) GestoreToken.introspectionToken(this.log, datiInvocazione, token, GestoreToken.PORTA_APPLICATIVA);
    		
        	if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
	
	public EsitoGestioneTokenPortaApplicativa userInfoToken(AbstractDatiInvocazione datiInvocazione, String token) throws TokenException {
		try {
        	
    		EsitoGestioneTokenPortaApplicativa esito = (EsitoGestioneTokenPortaApplicativa) GestoreToken.userInfoToken(this.log, datiInvocazione, token, GestoreToken.PORTA_APPLICATIVA);
    		
        	if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
	
	public void forwardToken(AbstractDatiInvocazione datiInvocazione, EsitoPresenzaTokenPortaApplicativa esitoPresenzaToken,
			EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo) throws TokenException {
		try {
        	
    		GestoreToken.forwardToken(this.log, this.idTransazione,
    				datiInvocazione, esitoPresenzaToken, 
    				esitoValidazioneJWT, esitoIntrospection, esitoUserInfo,
    				GestoreToken.PORTA_APPLICATIVA);
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
}

