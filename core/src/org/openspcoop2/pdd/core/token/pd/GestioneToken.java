/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.token.EsitoDynamicDiscovery;
import org.openspcoop2.pdd.core.token.EsitoGestioneToken;
import org.openspcoop2.pdd.core.token.EsitoPresenzaToken;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
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
	
    public EsitoPresenzaTokenPortaDelegata verificaPresenzaToken(DatiInvocazionePortaDelegata datiInvocazione) {

    	EsitoPresenzaTokenPortaDelegata esito = (EsitoPresenzaTokenPortaDelegata) GestoreToken.verificaPosizioneToken(datiInvocazione, GestoreToken.PORTA_DELEGATA);
    	
    	if(esito.getEccezioneProcessamento()!=null) {
    		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN));
    	}
    	else if(!esito.isPresente() &&
    		esito.getErrorMessage()==null) {
    		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_443_TOKEN_NON_PRESENTE.getErroreIntegrazione());
    	}
    	
    	return esito;
    	
    }
    
    public EsitoDynamicDiscoveryPortaDelegata dynamicDiscovery(DatiInvocazionePortaDelegata datiInvocazione, EsitoPresenzaToken token) throws TokenException {
    	try {
        	
    		IDSoggetto soggettoFruitore = getDominio(datiInvocazione);
    		IDServizio idServizio = getServizio(datiInvocazione);
    		Busta busta = getBusta(datiInvocazione, soggettoFruitore, idServizio);
    		
    		EsitoDynamicDiscoveryPortaDelegata esito = (EsitoDynamicDiscoveryPortaDelegata) GestoreToken.dynamicDiscovery(this.log, datiInvocazione, 
    				this.pddContext, this.protocolFactory,
    				token, GestoreToken.PORTA_DELEGATA,
    				busta, soggettoFruitore, idServizio);
    		
    		if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN));
        	}
        	else if(!esito.isValido() &&
        		esito.getErrorMessage()==null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_444_TOKEN_NON_VALIDO.getErroreIntegrazione());
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
    
    public EsitoGestioneTokenPortaDelegata validazioneJWTToken(DatiInvocazionePortaDelegata datiInvocazione, EsitoPresenzaToken token, EsitoDynamicDiscovery esitoDynamicDiscovery) throws TokenException { 	
    	try {
    	
    		IDSoggetto soggettoFruitore = getDominio(datiInvocazione);
    		IDServizio idServizio = getServizio(datiInvocazione);
    		Busta busta = getBusta(datiInvocazione, soggettoFruitore, idServizio);
    		
    		EsitoGestioneTokenPortaDelegata esito = (EsitoGestioneTokenPortaDelegata) GestoreToken.validazioneJWTToken(this.log, datiInvocazione, 
    				this.pddContext, this.protocolFactory, 
    				token, esitoDynamicDiscovery, GestoreToken.PORTA_DELEGATA,
    				busta, soggettoFruitore, idServizio);
    		
        	if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN));
        	}
        	else if(!esito.isValido() &&
        		esito.getErrorMessage()==null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_444_TOKEN_NON_VALIDO.getErroreIntegrazione());
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}  	
    }
    
    public EsitoGestioneTokenPortaDelegata introspectionToken(DatiInvocazionePortaDelegata datiInvocazione, EsitoPresenzaToken token, EsitoDynamicDiscovery esitoDynamicDiscovery) throws TokenException {
    	try {
        	
    		IDSoggetto soggettoFruitore = getDominio(datiInvocazione);
    		IDServizio idServizio = getServizio(datiInvocazione);
    		Busta busta = getBusta(datiInvocazione, soggettoFruitore, idServizio);
    		
    		EsitoGestioneTokenPortaDelegata esito = (EsitoGestioneTokenPortaDelegata) GestoreToken.introspectionToken(this.log, datiInvocazione, 
    				this.pddContext, this.protocolFactory,
    				token, esitoDynamicDiscovery, GestoreToken.PORTA_DELEGATA,
    				busta, soggettoFruitore, idServizio);
    		
    		if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN));
        	}
        	else if(!esito.isValido() &&
        		esito.getErrorMessage()==null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_444_TOKEN_NON_VALIDO.getErroreIntegrazione());
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
	
	public EsitoGestioneTokenPortaDelegata userInfoToken(DatiInvocazionePortaDelegata datiInvocazione, EsitoPresenzaToken token, EsitoDynamicDiscovery esitoDynamicDiscovery) throws TokenException {
		try {
        	
    		IDSoggetto soggettoFruitore = getDominio(datiInvocazione);
    		IDServizio idServizio = getServizio(datiInvocazione);
    		Busta busta = getBusta(datiInvocazione, soggettoFruitore, idServizio);
			
			EsitoGestioneTokenPortaDelegata esito = (EsitoGestioneTokenPortaDelegata) GestoreToken.userInfoToken(this.log, datiInvocazione, 
					this.pddContext, this.protocolFactory,
    				token, esitoDynamicDiscovery, GestoreToken.PORTA_DELEGATA,
    				busta, soggettoFruitore, idServizio);
    		
			if(esito.getEccezioneProcessamento()!=null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_560_GESTIONE_TOKEN));
        	}
        	else if(!esito.isValido() &&
        		esito.getErrorMessage()==null) {
        		esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_444_TOKEN_NON_VALIDO.getErroreIntegrazione());
        	}
        	
        	return esito;
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
	
	public void forwardToken(DatiInvocazionePortaDelegata datiInvocazione, EsitoPresenzaTokenPortaDelegata esitoPresenzaToken,
			EsitoGestioneToken esitoValidazioneJWT, EsitoGestioneToken esitoIntrospection, EsitoGestioneToken esitoUserInfo,
			InformazioniToken informazioniTokenNormalizzate) throws TokenException {
		try {
        	
    		IDSoggetto soggettoFruitore = getDominio(datiInvocazione);
    		IDServizio idServizio = getServizio(datiInvocazione);
    		Busta busta = getBusta(datiInvocazione, soggettoFruitore, idServizio);
			
    		GestoreToken.forwardToken(this.log, this.idTransazione,
    				datiInvocazione, esitoPresenzaToken, 
    				esitoValidazioneJWT, esitoIntrospection, esitoUserInfo,
    				informazioniTokenNormalizzate,
    				GestoreToken.PORTA_DELEGATA,
    				this.pddContext, busta);
    		
    	}catch(Exception e) {
    		throw new TokenException(e.getMessage(),e); // errore di processamento
    	}
	}
	
	private IDSoggetto getDominio(DatiInvocazionePortaDelegata datiInvocazione) {
		IDSoggetto soggetto = null;
		if(datiInvocazione.getPd()!=null) {
			soggetto = new IDSoggetto(datiInvocazione.getPd().getTipoSoggettoProprietario(), datiInvocazione.getPd().getNomeSoggettoProprietario());
		}
		return soggetto;
	}
	private IDServizio getServizio(DatiInvocazionePortaDelegata datiInvocazione) throws DriverRegistroServiziException {
		IDServizio servizio = null;
		if(datiInvocazione.getPd()!=null) {
			servizio = IDServizioFactory.getInstance().getIDServizioFromValues(datiInvocazione.getPd().getServizio().getTipo(), datiInvocazione.getPd().getServizio().getNome(), 
					datiInvocazione.getPd().getSoggettoErogatore().getTipo(), datiInvocazione.getPd().getSoggettoErogatore().getNome(), 
					datiInvocazione.getPd().getServizio().getVersione());
		}
		return servizio;
	}
	
	private Busta getBusta(DatiInvocazionePortaDelegata datiInvocazione, IDSoggetto soggettoFruitore, IDServizio idServizio) {
		Busta busta = new Busta(this.protocolFactory.getProtocol());
		if(soggettoFruitore!=null) {
			busta.setTipoMittente(soggettoFruitore.getTipo());
			busta.setMittente(soggettoFruitore.getNome());
		}
		if(idServizio!=null) {
			if(idServizio.getSoggettoErogatore()!=null) {
				busta.setTipoDestinatario(idServizio.getSoggettoErogatore().getTipo());
				busta.setDestinatario(idServizio.getSoggettoErogatore().getNome());
			}
			busta.setTipoServizio(idServizio.getTipo());
			busta.setServizio(idServizio.getNome());
			busta.setVersioneServizio(idServizio.getVersione());
			if(datiInvocazione.getRequestInfo()!=null && datiInvocazione.getRequestInfo().getIdServizio()!=null) {
				busta.setAzione(datiInvocazione.getRequestInfo().getIdServizio().getAzione());
			}
		}
		return busta;
	}
  
}

