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



package org.openspcoop2.pdd.core.autenticazione.pa;

import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.ApiKeyUtilities;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneUtils;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
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
	public String getSuffixKeyAuthenticationResultInCache(DatiInvocazionePortaApplicativa datiInvocazione) {
		
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
    public EsitoAutenticazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaApplicativa esito = new EsitoAutenticazionePortaApplicativa();
    	
		// Controllo apiKey fornite
    	String apiKey = null;
    	try {
    		apiKey = ApiKeyUtilities.getKey(true, this.header, this.cookie, this.queryParameter, 
					this.nomeHeaderApiKey, this.nomeCookieApiKey, this.nomeQueryParameterApiKey, 
					datiInvocazione!=null ? datiInvocazione.getInfoConnettoreIngresso() : null, this.getPddContext(), true); 
    	}catch(Exception e) {
    		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneApiKey non riuscita",e);
    		esito.setErroreCooperazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE.getErroreCooperazione());
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
    	}
    	if( apiKey==null || "".equals(apiKey) ){
    		esito.setErroreCooperazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE.getErroreCooperazione());
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
        		esito.setErroreCooperazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE.getErroreCooperazione());
    			esito.setClientAuthenticated(false);
    			esito.setClientIdentified(false);
    			return esito;
        	}
        	if( appId==null || "".equals(appId) ){
        		esito.setErroreCooperazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE.getErroreCooperazione());
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
    		esito.setErroreCooperazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_FORNITE_NON_CORRETTE.getErroreCooperazione());
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
    	}
    	
    	
    	OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		CryptConfig cryptConfigApplicativi = op2Properties.getCryptConfigAutenticazioneApplicativi();
		CryptConfig cryptConfigSoggetti = op2Properties.getCryptConfigAutenticazioneSoggetti();
		    		
    	// !NO!: Essendoci il principal del chiamante, il client e' stato autenticato dal container
    	//esito.setClientAuthenticated(true); come per il basic, per poter essere autenticato bisogna verificare che sia registrato sulla base dati
    	esito.setCredential(identitaAutenticata);

    	IDSoggetto idSoggetto = null;
		try{
			idSoggetto = RegistroServiziManager.getInstance(datiInvocazione.getState()).getIdSoggettoByCredenzialiApiKey(identitaAutenticata, password, this.appId, cryptConfigSoggetti, null); // all registry
		}
		catch(DriverRegistroServiziNotFound notFound){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("AutenticazioneApiKey (appId:"+this.appId+") non ha trovato risultati",notFound);
		}
		catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneApiKey (appId:"+this.appId+") non riuscita",e);
			esito.setErroreCooperazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		IDServizioApplicativo idServizioApplicativo = null;
		try {
			if(idSoggetto==null && this.getProtocolFactory().createProtocolConfiguration().isSupportoAutenticazioneApplicativiErogazioni()) {
				idServizioApplicativo = ConfigurazionePdDManager.getInstance(datiInvocazione.getState()).
						getIdServizioApplicativoByCredenzialiApiKey(identitaAutenticata, password, this.appId, cryptConfigApplicativi);
				if(idServizioApplicativo!=null) {
					if(idSoggetto==null) {
						idSoggetto = idServizioApplicativo.getIdSoggettoProprietario();
					}
					// Non ha senso poter identificare entrambi con le stesse credenziali
//					else if(idServizioApplicativo.getIdSoggettoProprietario().equals(idSoggetto)==false) {
//						throw new Exception("Identificato sia un soggetto che un applicativo. Il soggetto ["+idSoggetto+
//								"] identificato è differente dal proprietario dell'applicativo identificato ["+idServizioApplicativo.getIdSoggettoProprietario()+"]");
//					}
				}
			}
		}
		catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneApiKey (appId:"+this.appId+") (Applicativi) non riuscita",e);
			esito.setErroreCooperazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idSoggetto == null){
			esito.setErroreCooperazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_FORNITE_NON_CORRETTE.getErroreCooperazione());
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
		}
		else {
			esito.setClientAuthenticated(true);
			esito.setClientIdentified(true);
			esito.setIdSoggetto(idSoggetto);
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

