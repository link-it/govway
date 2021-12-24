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



package org.openspcoop2.pdd.core.autenticazione.pd;

import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.WWWAuthenticateConfig;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.utils.certificate.CertificateInfo;

/**
 * Classe che implementa una autenticazione BASIC.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutenticazioneSsl extends AbstractAutenticazioneBase {

	private boolean logError = true;
	@Override
	public void setLogError(boolean logError) {
		this.logError = logError;
	}
	
    @Override
    public EsitoAutenticazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaDelegata esito = new EsitoAutenticazionePortaDelegata();
    	
    	OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
    	WWWAuthenticateConfig wwwAuthenticateConfig = op2Properties.getRealmAutenticazioneHttpsWWWAuthenticateConfig();
    	
    	IDSoggetto soggettoFruitore = null;
    	if(datiInvocazione!=null && datiInvocazione.getPd()!=null) {
    		soggettoFruitore = new IDSoggetto(datiInvocazione.getPd().getTipoSoggettoProprietario(), datiInvocazione.getPd().getNomeSoggettoProprietario());
    	}
    	    	
    	Credenziali credenziali = datiInvocazione.getInfoConnettoreIngresso().getCredenziali();
		
    	String subject = credenziali.getSubject();
    	String issuer = credenziali.getIssuer();
    	CertificateInfo certificate = null;
    	if(credenziali.getCertificate()!=null) {
    		certificate = credenziali.getCertificate().getCertificate();
    	}
    	
		// Controllo credenziali fornite
    	if( subject==null || "".equals(subject) ){
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl("credenziali non fornite",subject));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			if(wwwAuthenticateConfig!=null) {
    			esito.setWwwAuthenticateErrorHeader(wwwAuthenticateConfig.buildWWWAuthenticateHeaderValue_notFound());
    		}
			return esito;
		}
    	if(credenziali.getCertificate()!=null) {
    		certificate = credenziali.getCertificate().getCertificate();
    		
    		try {
    			certificate.checkValid();
    		}catch(Exception e) {
    			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, 
    					ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl("credenziali fornite non corrette: "+e.getMessage(),subject));
    			esito.setClientAuthenticated(false);
    			esito.setClientIdentified(false);
    			if(wwwAuthenticateConfig!=null) {
    				esito.setWwwAuthenticateErrorHeader(wwwAuthenticateConfig.buildWWWAuthenticateHeaderValue_invalid());
    			}
    			return esito;
    		}
    	}
    	
    	// Essendoci l'identita' del chiamante, il client e' stato autenticato o da un frontend o dall'application server stesso
    	esito.setClientAuthenticated(true);
    	esito.setCredential(subject);
		
    	// Provo a identificare il chiamante rispetto ad una entita' del registro
		IDServizioApplicativo idServizioApplicativo = null;
		try{
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
			
			// NOTA: il fatto di essersi registrati come strict o come non strict è insito nella registrazione dell'applicativo
			
			// 1. Prima si cerca per certificato strict
			if(certificate!=null) {
				idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(certificate, true);
			}
			if(idServizioApplicativo==null) {
				// 2. Poi per certificato no strict
				if(certificate!=null) {
					idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(certificate, false);
				}	
			}
			if(idServizioApplicativo==null) {
				// 3. per subject/issuer
				idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(subject, issuer);	
			}
			if(idServizioApplicativo==null) {
				// 4. solo per subject
				idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiSsl(subject, null);	
			}
			
			if(idServizioApplicativo!=null && soggettoFruitore==null) {
				soggettoFruitore = idServizioApplicativo.getIdSoggettoProprietario();
			}
		}catch(Exception e){
			if(this.logError) {
    			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneSsl non riuscita",e);
			}
			esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idServizioApplicativo == null){
			// L'identificazione in ssl non e' obbligatoria
			//esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl("credenziali fornite non corrette",subject));
			esito.setClientIdentified(false);
		}
		else if(idServizioApplicativo.getIdSoggettoProprietario().equals(soggettoFruitore)==false) {
			// L'identificazione in ssl non e' obbligatoria
			//esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl("soggetto proprietario dell'applicativo identificato differente dal soggetto proprietario della porta invocata",subject));
			esito.setClientIdentified(false);
			return esito;
		}
		else {
			esito.setClientIdentified(true);
			esito.setIdServizioApplicativo(idServizioApplicativo);
		}
		
		return esito;
		
    }

}

