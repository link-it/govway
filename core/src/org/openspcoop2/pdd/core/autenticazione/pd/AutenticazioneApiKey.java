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



package org.openspcoop2.pdd.core.autenticazione.pd;

import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.ApiKeyUtilities;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneUtils;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.utils.crypt.CryptConfig;

/**
 * Classe che implementa una autenticazione API-Key.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutenticazioneApiKey extends AbstractAutenticazioneBase {

	private boolean header = true;
	private boolean cookie = true;
	private boolean queryParameter = true;
	
	private String nomeHeaderApiKey = null;
	private String nomeCookieApiKey = null;
	private String nomeQueryParameterApiKey = null;
	
	private boolean appId = false;
	
	private String nomeHeaderAppId = null;
	private String nomeCookieAppId = null;
	private String nomeQueryParameterAppId = null;
	
	private boolean cleanApiKey = true;
	private boolean cleanAppId = true;
	
	@Override
    public void initParametri(ParametriAutenticazione parametri) throws AutenticazioneException {
		super.initParametri(parametri);

		ParametriAutenticazioneApiKey authApiKey = new ParametriAutenticazioneApiKey(this.parametri);
		
		if(authApiKey.getHeader()!=null) {
			this.header = authApiKey.getHeader();
		}
		if(authApiKey.getCookie()!=null) {
			this.cookie = authApiKey.getCookie();
		}
		if(authApiKey.getQueryParameter()!=null) {
			this.queryParameter = authApiKey.getQueryParameter();
		}
		
		this.nomeHeaderApiKey = authApiKey.getNomeHeaderApiKey();
		this.nomeCookieApiKey = authApiKey.getNomeCookieApiKey();
		this.nomeQueryParameterApiKey = authApiKey.getNomeQueryParameterApiKey();
		
		if(authApiKey.getAppId()!=null) {
			this.appId = authApiKey.getAppId();
		}
		
		if(this.appId) {
			this.nomeHeaderAppId = authApiKey.getNomeHeaderAppId();
			this.nomeCookieAppId = authApiKey.getNomeCookieAppId();
			this.nomeQueryParameterAppId = authApiKey.getNomeQueryParameterAppId();
		}
		
		if(authApiKey.getCleanApiKey()!=null) {
			this.cleanApiKey = authApiKey.getCleanApiKey();
		}
		if(this.appId) {
			if(authApiKey.getCleanAppId()!=null) {
				this.cleanAppId = authApiKey.getCleanAppId();
			}
		}
		
	}
	
	@Override
	public String getSuffixKeyAuthenticationResultInCache(DatiInvocazionePortaDelegata datiInvocazione) {
		if(datiInvocazione==null) {
			return null;
		}
		try {
			String apiKey = ApiKeyUtilities.getKey(true, this.header, this.cookie, this.queryParameter, 
					this.nomeHeaderApiKey, this.nomeCookieApiKey, this.nomeQueryParameterApiKey, 
					datiInvocazione!=null ? datiInvocazione.getInfoConnettoreIngresso() : null, this.getPddContext(), false);
			if(apiKey==null) {
				return null;
			}
			if(this.appId) {
				String appId = ApiKeyUtilities.getKey(false, this.header, this.cookie, this.queryParameter, 
						this.nomeHeaderAppId, this.nomeCookieAppId, this.nomeQueryParameterAppId, 
						datiInvocazione!=null ? datiInvocazione.getInfoConnettoreIngresso() : null, this.getPddContext(), false);
				if(appId==null) {
					return null;
				}
				return "multipleApiKey-"+appId+"."+apiKey;
			}
			else {
				return "apiKey-"+apiKey;
			}
		}catch(Exception e) {
			return null;
		}
	}
	
    @Override
    public EsitoAutenticazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaDelegata esito = new EsitoAutenticazionePortaDelegata();
    	
    	IDSoggetto soggettoFruitore = null;
    	if(datiInvocazione!=null && datiInvocazione.getPd()!=null) {
    		soggettoFruitore = new IDSoggetto(datiInvocazione.getPd().getTipoSoggettoProprietario(), datiInvocazione.getPd().getNomeSoggettoProprietario());
    	}
    	
    	// Controllo apiKey fornite
    	String apiKey = null;
    	try {
    		apiKey = ApiKeyUtilities.getKey(true, this.header, this.cookie, this.queryParameter, 
					this.nomeHeaderApiKey, this.nomeCookieApiKey, this.nomeQueryParameterApiKey, 
					datiInvocazione!=null ? datiInvocazione.getInfoConnettoreIngresso() : null, this.getPddContext(), true); 
    	}catch(Exception e) {
    		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneApiKey non riuscita",e);
    		esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaPrincipal("credenziali non fornite",apiKey));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
    	}
    	if( apiKey==null || "".equals(apiKey) ){
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaPrincipal("credenziali non fornite",apiKey));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
		}
		
    	String appId = null;
    	if(this.appId) {
    		try {
    			appId = ApiKeyUtilities.getKey(false, this.header, this.cookie, this.queryParameter, 
						this.nomeHeaderAppId, this.nomeCookieAppId, this.nomeQueryParameterAppId, 
						datiInvocazione!=null ? datiInvocazione.getInfoConnettoreIngresso() : null, this.getPddContext(), true);
    		}catch(Exception e) {
        		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneApiKey (AppId) non riuscita",e);
        		esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaPrincipal("credenziali non fornite",appId));
    			esito.setClientAuthenticated(false);
    			esito.setClientIdentified(false);
    			return esito;
        	}
    		if( appId==null || "".equals(appId) ){
    			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaPrincipal("credenziali non fornite",appId));
    			esito.setClientAuthenticated(false);
    			esito.setClientIdentified(false);
    			return esito;
    		}
    	}
    	
    	String identitaAutenticata = null;
    	String password = null;
    	try {
	    	if(this.appId) {
	    		identitaAutenticata = appId;
	    		password = ApiKeyUtilities.decodeMultipleApiKey(apiKey);
	    	}
	    	else {
	    		String [] decodedApiKey = ApiKeyUtilities.decodeApiKey(apiKey);
	    		identitaAutenticata = decodedApiKey[0];
	    		password = decodedApiKey[1];
	    	}
    	}catch(Exception e) {
    		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneApiKey (appId:"+this.appId+") fallita",e);
    		esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaBasic("credenziali fornite non corrette",identitaAutenticata,apiKey));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
    	}
    	
    	OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		CryptConfig cryptConfigApplicativi = op2Properties.getCryptConfigAutenticazioneApplicativi();
		    		
    	// !NO!: Essendoci il principal del chiamante, il client e' stato autenticato dal container
    	//esito.setClientAuthenticated(true); come per il basic, per poter essere autenticato bisogna verificare che sia registrato sulla base dati
    	esito.setCredential(identitaAutenticata);
    	
		IDServizioApplicativo idServizioApplicativo = null;
		try{
			idServizioApplicativo = ConfigurazionePdDManager.getInstance(datiInvocazione.getState()).
						getIdServizioApplicativoByCredenzialiApiKey(identitaAutenticata, password, this.appId, cryptConfigApplicativi);
			if(idServizioApplicativo!=null && soggettoFruitore==null) {
				soggettoFruitore = idServizioApplicativo.getIdSoggettoProprietario();
			}
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneApiKey (appId:"+this.appId+") non riuscita",e);
			
			esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idServizioApplicativo == null){
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaBasic("credenziali fornite non corrette",identitaAutenticata,apiKey));
			esito.setClientAuthenticated(false);
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
		}
		else if(idServizioApplicativo.getIdSoggettoProprietario().equals(soggettoFruitore)==false) {
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaBasic("soggetto proprietario dell'applicativo identificato differente dal soggetto proprietario della porta invocata",identitaAutenticata,apiKey));
			esito.setClientAuthenticated(false);
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
		}
		else {
			esito.setClientAuthenticated(true);
			esito.setClientIdentified(true);
			esito.setIdServizioApplicativo(idServizioApplicativo);
		}
		
		return esito;
		
    }

    @Override
	public void cleanPostAuth(OpenSPCoop2Message message) throws AutenticazioneException {
    	
    	AutenticazioneUtils.finalizeProcessApiKey(message, 
    			this.header, this.cookie, this.queryParameter, 
				this.nomeHeaderApiKey, this.nomeCookieApiKey, this.nomeQueryParameterApiKey, 
				this.cleanApiKey); 
    	
    	if(this.appId) {
    		AutenticazioneUtils.finalizeProcessApiKey(message, 
        			this.header, this.cookie, this.queryParameter, 
    				this.nomeHeaderAppId, this.nomeCookieAppId, this.nomeQueryParameterAppId, 
    				this.cleanAppId); 
    	}
    	
    }
}

