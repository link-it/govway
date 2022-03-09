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



package org.openspcoop2.pdd.core.token.pa;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.token.AbstractDatiInvocazione;
import org.openspcoop2.pdd.core.token.EsitoGestioneToken;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.slf4j.Logger;

/**
 * Classe che implementala gestione token
 *
 * @author Andrea Poli (apoli@link.it)
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
	
    public EsitoPresenzaTokenPortaApplicativa verificaPresenzaToken(DatiInvocazionePortaApplicativa datiInvocazione) throws TokenException{

    	EsitoPresenzaTokenPortaApplicativa esito = (EsitoPresenzaTokenPortaApplicativa) GestoreToken.verificaPosizioneToken(this.log, datiInvocazione, GestoreToken.PORTA_APPLICATIVA);
    	
    	if(esito.getEccezioneProcessamento()!=null) {
    		esito.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
    	}
    	else if(esito.isPresente()==false) {
    		if(esito.getErrorMessage()==null) {
    			esito.setErroreCooperazione(ErroriCooperazione.TOKEN_NON_PRESENTE.getErroreCooperazione());
    		}
    	}
    	
    	return esito;
    	
    }
    
    public EsitoGestioneTokenPortaApplicativa validazioneJWTToken(DatiInvocazionePortaApplicativa datiInvocazione, String token) throws TokenException {   	
    	try {
    	
    		EsitoGestioneTokenPortaApplicativa esito = (EsitoGestioneTokenPortaApplicativa) GestoreToken.validazioneJWTToken(this.log, datiInvocazione, 
    				this.pddContext, this.protocolFactory,
    				token, GestoreToken.PORTA_APPLICATIVA);
    		
        	if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
        	}
        	else if(esito.isValido()==false) {
        		if(esito.getErrorMessage()==null) {
        			esito.setErroreCooperazione(ErroriCooperazione.TOKEN_NON_VALIDO.getErroreCooperazione());
        		}
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	} 	
    }
    
    public EsitoGestioneTokenPortaApplicativa introspectionToken(DatiInvocazionePortaApplicativa datiInvocazione, String token) throws TokenException {
    	try {
        	
    		EsitoGestioneTokenPortaApplicativa esito = (EsitoGestioneTokenPortaApplicativa) GestoreToken.introspectionToken(this.log, datiInvocazione, 
    				this.pddContext, this.protocolFactory,
    				token, GestoreToken.PORTA_APPLICATIVA,
    				getDominio(datiInvocazione), getServizio(datiInvocazione));
    		
        	if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
        	}
        	else if(esito.isValido()==false) {
        		if(esito.getErrorMessage()==null) {
        			esito.setErroreCooperazione(ErroriCooperazione.TOKEN_NON_VALIDO.getErroreCooperazione());
        		}
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
	
	public EsitoGestioneTokenPortaApplicativa userInfoToken(DatiInvocazionePortaApplicativa datiInvocazione, String token) throws TokenException {
		try {
        	
    		EsitoGestioneTokenPortaApplicativa esito = (EsitoGestioneTokenPortaApplicativa) GestoreToken.userInfoToken(this.log, datiInvocazione, 
    				this.pddContext, this.protocolFactory,
    				token, GestoreToken.PORTA_APPLICATIVA,
    				getDominio(datiInvocazione), getServizio(datiInvocazione));
    		
        	if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
        	}
        	else if(esito.isValido()==false) {
        		if(esito.getErrorMessage()==null) {
        			esito.setErroreCooperazione(ErroriCooperazione.TOKEN_NON_VALIDO.getErroreCooperazione());
        		}
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
	
	public void forwardToken(AbstractDatiInvocazione datiInvocazione, EsitoPresenzaTokenPortaApplicativa esitoPresenzaToken,
			EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo,
			InformazioniToken informazioniTokenNormalizzate) throws TokenException {
		try {
        	
    		GestoreToken.forwardToken(this.log, this.idTransazione,
    				datiInvocazione, esitoPresenzaToken, 
    				esitoValidazioneJWT, esitoIntrospection, esitoUserInfo,
    				informazioniTokenNormalizzate,
    				GestoreToken.PORTA_APPLICATIVA);
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
	
	private IDSoggetto getDominio(DatiInvocazionePortaApplicativa datiInvocazione) {
		IDSoggetto soggetto = null;
		if(datiInvocazione.getPa()!=null) {
			soggetto = new IDSoggetto(datiInvocazione.getPa().getTipoSoggettoProprietario(), datiInvocazione.getPa().getNomeSoggettoProprietario());
		}
		else if(datiInvocazione.getPd()!=null) {
			soggetto = new IDSoggetto(datiInvocazione.getPd().getTipoSoggettoProprietario(), datiInvocazione.getPd().getNomeSoggettoProprietario());
		}
		return soggetto;
	}
	private IDServizio getServizio(DatiInvocazionePortaApplicativa datiInvocazione) throws DriverRegistroServiziException {
		IDServizio servizio = null;
		if(datiInvocazione.getPa()!=null) {
			servizio = IDServizioFactory.getInstance().getIDServizioFromValues(datiInvocazione.getPa().getServizio().getTipo(), datiInvocazione.getPa().getServizio().getNome(), 
					datiInvocazione.getPa().getTipoSoggettoProprietario(), datiInvocazione.getPa().getNomeSoggettoProprietario(), 
					datiInvocazione.getPa().getServizio().getVersione());
		}
		else if(datiInvocazione.getPd()!=null) {
			servizio = IDServizioFactory.getInstance().getIDServizioFromValues(datiInvocazione.getPd().getServizio().getTipo(), datiInvocazione.getPd().getServizio().getNome(), 
					datiInvocazione.getPd().getSoggettoErogatore().getTipo(), datiInvocazione.getPd().getSoggettoErogatore().getNome(), 
					datiInvocazione.getPd().getServizio().getVersione());
		}
		return servizio;
	}
}

