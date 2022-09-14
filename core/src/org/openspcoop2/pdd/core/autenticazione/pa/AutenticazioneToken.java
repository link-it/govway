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



package org.openspcoop2.pdd.core.autenticazione.pa;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
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
	
	private void readDati(DatiInvocazionePortaApplicativa datiInvocazione) {
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
			if(datiInvocazione.getPa()!=null) {
				if(datiInvocazione.getPa().getGestioneToken()!=null) {
					this.tokenPolicy = datiInvocazione.getPa().getGestioneToken().getPolicy();
					if(this.tokenPolicy==null || StringUtils.isEmpty(this.tokenPolicy)) {
						return;
					}
				}
			}
			else if(datiInvocazione.getPd()!=null) {
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
	public String getSuffixKeyAuthenticationResultInCache(DatiInvocazionePortaApplicativa datiInvocazione) {
		
		readDati(datiInvocazione);
		
		return toStringCredentials();
	}
	
    @Override
    public EsitoAutenticazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaApplicativa esito = new EsitoAutenticazionePortaApplicativa();
    	
    	readDati(datiInvocazione);
		
		if(this.clientId==null || this.tokenPolicy==null) {
			// Nella richiesta non vi sono dati utili all'autenticazione token
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			esito.setNoCache(true);
			return esito;
		}
		
		// Essendoci l'identita' del chiamante, il client e' stato autenticato tramite il token
		esito.setClientAuthenticated(true);
    	esito.setCredential(toStringCredentials());
    	
    	IDServizioApplicativo idServizioApplicativo = null;
    	IDSoggetto idSoggetto = null;
		try {
			ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance(datiInvocazione.getState());
			idServizioApplicativo = configurazionePdDManager.getIdServizioApplicativoByCredenzialiToken(this.tokenPolicy, this.clientId);
			if(idServizioApplicativo!=null) {
				idSoggetto = idServizioApplicativo.getIdSoggettoProprietario();
			}
		}
		catch(Exception e){
			if(this.logError) {
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneToken non riuscita",e);
			}
			esito.setErroreCooperazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
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
			esito.setClientIdentified(true);
			esito.setIdSoggetto(idSoggetto);
			esito.setIdServizioApplicativo(idServizioApplicativo);
		}
		
		return esito;
    }

}

