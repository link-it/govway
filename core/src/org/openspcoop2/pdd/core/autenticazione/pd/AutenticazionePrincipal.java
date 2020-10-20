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

import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneUtils;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazionePrincipal;
import org.openspcoop2.pdd.core.autenticazione.PrincipalUtilities;
import org.openspcoop2.pdd.core.autenticazione.WWWAuthenticateConfig;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**
 * Classe che implementa una autenticazione BASIC.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutenticazionePrincipal extends AbstractAutenticazioneBase {

	private TipoAutenticazionePrincipal tipoAutenticazionePrincipal = TipoAutenticazionePrincipal.CONTAINER;
	private String nome = null;
	private String pattern = null;
	private TipoCredenzialeMittente tipoTokenClaim = null; // uso trasporto come custom
	private boolean cleanPrincipal = true;
	
	private boolean logError = true;
	@Override
	public void setLogError(boolean logError) {
		this.logError = logError;
	}

	@Override
    public void initParametri(ParametriAutenticazione parametri) throws AutenticazioneException {
		super.initParametri(parametri);

		ParametriAutenticazionePrincipal authPrincipal = new ParametriAutenticazionePrincipal(this.parametri);
		if(authPrincipal.getTipoAutenticazione()!=null) {
			this.tipoAutenticazionePrincipal = authPrincipal.getTipoAutenticazione();
			
			switch (this.tipoAutenticazionePrincipal) {
			case CONTAINER:
			case INDIRIZZO_IP:
			case INDIRIZZO_IP_X_FORWARDED_FOR:
				break;
			case HEADER:
				this.nome = authPrincipal.getNome();
				if(this.nome==null) {
					throw new AutenticazioneException("Nome dell'header, da cui estrarre il principal, non indicato");
				}
				break;
			case FORM:
				this.nome = authPrincipal.getNome();
				if(this.nome==null) {
					throw new AutenticazioneException("Nome del parametro della query, da cui estrarre il principal, non indicato");
				}
				break;
			case URL:
				this.pattern = authPrincipal.getPattern();
				if(this.pattern==null) {
					throw new AutenticazioneException("Espressione Regolare, da utilizzare sulla url per estrarre il principal, non indicata");
				}
				break;
			// Ho levato il contenuto, poichè senno devo fare il digest per poterlo poi cachare
//				case CONTENT:
//					this.pattern = authPrincipal.getPattern();
//					if(this.pattern==null) {
//						throw new AutenticazioneException("Pattern, da utilizzare per estrarre dal contenuto il principal, non indicato");
//					}
//					break;
			case TOKEN:
				this.tipoTokenClaim = authPrincipal.getTokenClaim();
				if(this.tipoTokenClaim==null) {
					throw new AutenticazioneException("Token Claim, da cui estrarre il principal, non indicato");
				}
				if(TipoCredenzialeMittente.trasporto.equals(this.tipoTokenClaim)) {
					this.nome = authPrincipal.getNome();
					if(this.nome==null) {
						throw new AutenticazioneException("Nome del token claim, da cui estrarre il principal, non indicato");
					}
				}
				break;
			}
			
			if(authPrincipal.getCleanPrincipal()!=null) {
				this.cleanPrincipal = authPrincipal.getCleanPrincipal();
			}
		}
		
	}
	
	@Override
	public String getSuffixKeyAuthenticationResultInCache(DatiInvocazionePortaDelegata datiInvocazione) {
		switch (this.tipoAutenticazionePrincipal) {
		case CONTAINER:
			return null;
		case HEADER:
		case FORM:
		case URL:
		case INDIRIZZO_IP:
		case INDIRIZZO_IP_X_FORWARDED_FOR:
		case TOKEN:
			if(datiInvocazione==null) {
				return null;
			}
			try {
				return PrincipalUtilities.getPrincipal(this.tipoAutenticazionePrincipal, this.nome, this.pattern, this.tipoTokenClaim, 
						datiInvocazione.getInfoConnettoreIngresso(), this.getPddContext(), false,
						new StringBuilder());
			}catch(Exception e) {
				return null;
			}
		// Ho levato il contenuto, poichè senno devo fare il digest per poterlo poi cachare
//		case CONTENT:
		}
		return null;
	}
	
    @Override
    public EsitoAutenticazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaDelegata esito = new EsitoAutenticazionePortaDelegata();
    	
    	OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
    	WWWAuthenticateConfig wwwAuthenticateConfig = op2Properties.getRealmAutenticazionePrincipalWWWAuthenticateConfig(this.tipoAutenticazionePrincipal);
    	
    	IDSoggetto soggettoFruitore = null;
    	if(datiInvocazione!=null && datiInvocazione.getPd()!=null) {
    		soggettoFruitore = new IDSoggetto(datiInvocazione.getPd().getTipoSoggettoProprietario(), datiInvocazione.getPd().getNomeSoggettoProprietario());
    	}
    	
    	StringBuilder fullCredential= new StringBuilder();
    	
    	// Controllo credenziali fornite
    	String principal = null;
    	try {
    		principal = PrincipalUtilities.getPrincipal(this.tipoAutenticazionePrincipal, this.nome, this.pattern, this.tipoTokenClaim,  
    				datiInvocazione!=null ? datiInvocazione.getInfoConnettoreIngresso() : null, this.getPddContext(), true,
    				fullCredential);
    	}catch(Exception e) {
    		if(this.logError) {
    			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazionePrincipal non riuscita",e);
    		}
    		esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaPrincipal("credenziali non fornite",principal));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			if(wwwAuthenticateConfig!=null) {
				esito.setWwwAuthenticateErrorHeader(wwwAuthenticateConfig.buildWWWAuthenticateHeaderValue_notFound());
			}
			return esito;
    	}
    	if( principal==null || "".equals(principal) ){
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaPrincipal("credenziali non fornite",principal));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			if(wwwAuthenticateConfig!=null) {
				esito.setWwwAuthenticateErrorHeader(wwwAuthenticateConfig.buildWWWAuthenticateHeaderValue_notFound());
			}
			return esito;
		}
    	
    	// per conoscere la credenziale passata anche in caso di autenticazione fallita
    	esito.setFullCredential(fullCredential.toString());
		
    	// Essendoci il principal del chiamante, il client e' stato autenticato dal container
    	esito.setClientAuthenticated(true);
    	esito.setCredential(principal);
    	
		IDServizioApplicativo idServizioApplicativo = null;
		try{
			idServizioApplicativo = ConfigurazionePdDManager.getInstance(datiInvocazione.getState()).
						getIdServizioApplicativoByCredenzialiPrincipal(principal);
			if(idServizioApplicativo!=null && soggettoFruitore==null) {
				soggettoFruitore = idServizioApplicativo.getIdSoggettoProprietario();
			}
		}catch(Exception e){
			if(this.logError) {
    			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazionePrincipal non riuscita",e);
			}
			esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idServizioApplicativo == null){
			// L'identificazione in principal non e' obbligatoria
			//esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl("credenziali fornite non corrette",principal));
			esito.setClientIdentified(false);
			return esito;
		}
		else if(idServizioApplicativo.getIdSoggettoProprietario().equals(soggettoFruitore)==false) {
			// L'identificazione in principal non e' obbligatoria
			//esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl("soggetto proprietario dell'applicativo identificato differente dal soggetto proprietario della porta invocata",principal));
			esito.setClientIdentified(false);
			return esito;
		}
		else {
			esito.setClientIdentified(true);
			esito.setIdServizioApplicativo(idServizioApplicativo);
		}
		
		return esito;
		
    }

    @Override
	public void cleanPostAuth(OpenSPCoop2Message message) throws AutenticazioneException {
    	AutenticazioneUtils.finalizeProcessPrincipal(message, this.tipoAutenticazionePrincipal, this.nome, this.cleanPrincipal);
    }
}

