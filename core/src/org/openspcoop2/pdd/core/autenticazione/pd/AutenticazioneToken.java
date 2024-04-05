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



package org.openspcoop2.pdd.core.autenticazione.pd;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**
 * Classe che implementa una autenticazione token
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutenticazioneToken extends AbstractAutenticazioneBase {

	private boolean logError = true;
	@Override
	public void setLogError(boolean logError) {
		this.logError = logError;
	}
	
	private String tokenPolicy = null;
	private String clientId = null;
	
	private void readDati(DatiInvocazionePortaDelegata datiInvocazione) {
		if(this.clientId==null) {
			InformazioniToken informazioniTokenNormalizzate = null;
			if(this.getPddContext()!=null && this.getPddContext().containsKey(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE)) {
				informazioniTokenNormalizzate = (InformazioniToken) this.getPddContext().getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
			}
			if(informazioniTokenNormalizzate==null) {
				return;
			}
			this.clientId = informazioniTokenNormalizzate.getClientId();
			if(this.clientId==null || StringUtils.isEmpty(this.clientId)) {
				return;
			}
		}
		
		if(this.tokenPolicy==null) {
			if(datiInvocazione.getPd()!=null) {
				if(datiInvocazione.getPd().getGestioneToken()!=null) {
					this.tokenPolicy = datiInvocazione.getPd().getGestioneToken().getPolicy();
					if(this.tokenPolicy==null || StringUtils.isEmpty(this.tokenPolicy)) {
						return;
					}
				}
			}
		}
	}
	
	public String toStringCredentials() {
		if(this.clientId==null || this.tokenPolicy==null) {
			return null;
		}
		
		return "TokenPolicy-"+this.tokenPolicy+".clientId:"+this.clientId;
	}
	
	@Override
	public String getSuffixKeyAuthenticationResultInCache(DatiInvocazionePortaDelegata datiInvocazione) {
		readDati(datiInvocazione);
		
		return toStringCredentials();
	}
	
    @Override
    public EsitoAutenticazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaDelegata esito = new EsitoAutenticazionePortaDelegata();
    	
    	if(datiInvocazione==null) {
    		throw new AutenticazioneException("Param datiInvocazione is null");
    	}
    	
    	readDati(datiInvocazione);
		
		if(this.clientId==null || this.tokenPolicy==null) {
			// Nella richiesta non vi sono dati utili all'autenticazione token
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			esito.setNoCache(true);
			return esito;
		}
		
    	IDSoggetto soggettoFruitore = null;
    	if(datiInvocazione!=null && datiInvocazione.getPd()!=null) {
    		soggettoFruitore = new IDSoggetto(datiInvocazione.getPd().getTipoSoggettoProprietario(), datiInvocazione.getPd().getNomeSoggettoProprietario());
    	}
		
		// Essendoci l'identita' del chiamante, il client e' stato autenticato tramite il token
		esito.setClientAuthenticated(true);
    	esito.setCredential(toStringCredentials());
    	
    	IDServizioApplicativo idServizioApplicativo = null;
    	try {
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
			idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiToken(this.tokenPolicy, this.clientId);
		}
		catch(Exception e){
			if(this.logError) {
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneToken non riuscita",e);
			}
			esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idServizioApplicativo == null){
			// L'identificazione token non e' obbligatoria
			// esito.setErroreCooperazione(ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_FORNITE_NON_CORRETTE.getErroreCooperazione());
			esito.setClientIdentified(false);
			return esito;
		} 
		else {
			if(OpenSPCoop2Properties.getInstance().isAutenticazioneTokenPortaDelegataCheckSoggettiProprietari() && idServizioApplicativo.getIdSoggettoProprietario().equals(soggettoFruitore)==false) {
				esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, 
						ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.
							getErrore402_AutenticazioneFallitaToken("soggetto proprietario ("+idServizioApplicativo.getIdSoggettoProprietario()+") dell'applicativo token identificato ("+idServizioApplicativo.getNome()+") differente dal soggetto proprietario della porta invocata ("+soggettoFruitore+")",this.clientId));
				esito.setClientIdentified(false);
				return esito;
			}
			else {
				esito.setClientIdentified(true);
				esito.setIdServizioApplicativo(idServizioApplicativo);
			}
		}
		
		return esito;
		
    }

}

