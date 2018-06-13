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



package org.openspcoop2.pdd.core.token.pd;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.token.AbstractDatiInvocazione;
import org.openspcoop2.pdd.core.token.EsitoGestioneToken;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.slf4j.Logger;

/**
 * Classe che implementala gestione token
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class GestioneToken {

	private Logger log;
	private String idTransazione;
	private PdDContext pddContext;
	private IProtocolFactory<?> protocolFactory;
	public GestioneToken(Logger log, String idTransazione,
			PdDContext pddContext, IProtocolFactory<?> protocolFactory) {
		this.log = log;
		this.idTransazione = idTransazione;
		this.pddContext = pddContext;
		this.protocolFactory = protocolFactory;
	}
	
    public EsitoPresenzaTokenPortaDelegata verificaPresenzaToken(DatiInvocazionePortaDelegata datiInvocazione) throws TokenException{

    	EsitoPresenzaTokenPortaDelegata esito = (EsitoPresenzaTokenPortaDelegata) GestoreToken.verificaPosizioneToken(this.log, datiInvocazione, GestoreToken.PORTA_DELEGATA);
    	
    	if(esito.getEccezioneProcessamento()!=null) {
    		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN));
    	}
    	else if(esito.isPresente()==false) {
    		if(esito.getErrorMessage()==null) {
    			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_443_TOKEN_NON_PRESENTE.getErroreIntegrazione());
    		}
    	}
    	
    	return esito;
    	
    }
    
    public EsitoGestioneTokenPortaDelegata validazioneJWTToken(AbstractDatiInvocazione datiInvocazione, String token) throws TokenException { 	
    	try {
    	
    		EsitoGestioneTokenPortaDelegata esito = (EsitoGestioneTokenPortaDelegata) GestoreToken.validazioneJWTToken(this.log, datiInvocazione, token, GestoreToken.PORTA_DELEGATA);
    		
        	if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN));
        	}
        	else if(esito.isValido()==false) {
        		if(esito.getErrorMessage()==null) {
        			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_444_TOKEN_NON_VALIDO.getErroreIntegrazione());
        		}
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}  	
    }
    
    public EsitoGestioneTokenPortaDelegata introspectionToken(AbstractDatiInvocazione datiInvocazione, String token) throws TokenException {
    	try {
        	
    		EsitoGestioneTokenPortaDelegata esito = (EsitoGestioneTokenPortaDelegata) GestoreToken.introspectionToken(this.log, datiInvocazione, 
    				this.pddContext, this.protocolFactory,
    				token, GestoreToken.PORTA_DELEGATA);
    		
    		if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN));
        	}
        	else if(esito.isValido()==false) {
        		if(esito.getErrorMessage()==null) {
        			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_444_TOKEN_NON_VALIDO.getErroreIntegrazione());
        		}
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
	
	public EsitoGestioneTokenPortaDelegata userInfoToken(AbstractDatiInvocazione datiInvocazione, String token) throws TokenException {
		try {
        	
			EsitoGestioneTokenPortaDelegata esito = (EsitoGestioneTokenPortaDelegata) GestoreToken.userInfoToken(this.log, datiInvocazione, 
					this.pddContext, this.protocolFactory,
    				token, GestoreToken.PORTA_DELEGATA);
    		
			if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN));
        	}
        	else if(esito.isValido()==false) {
        		if(esito.getErrorMessage()==null) {
        			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_444_TOKEN_NON_VALIDO.getErroreIntegrazione());
        		}
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
	
	public void forwardToken(AbstractDatiInvocazione datiInvocazione, EsitoPresenzaTokenPortaDelegata esitoPresenzaToken,
			EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo,
			InformazioniToken informazioniTokenNormalizzate) throws TokenException {
		try {
        	
    		GestoreToken.forwardToken(this.log, this.idTransazione,
    				datiInvocazione, esitoPresenzaToken, 
    				esitoValidazioneJWT, esitoIntrospection, esitoUserInfo,
    				informazioniTokenNormalizzate,
    				GestoreToken.PORTA_DELEGATA);
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
  
}

