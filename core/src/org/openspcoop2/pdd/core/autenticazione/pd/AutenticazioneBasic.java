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
import org.openspcoop2.message.utils.WWWAuthenticateGenerator;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneUtils;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneBasic;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.utils.crypt.CryptConfig;

/**
 * Classe che implementa una autenticazione BASIC.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutenticazioneBasic extends AbstractAutenticazioneBase {

	protected boolean cleanHeaderAuthorization = true;
	
	@Override
    public void initParametri(ParametriAutenticazione parametri) throws AutenticazioneException {
		super.initParametri(parametri);

		ParametriAutenticazioneBasic authBasic = new ParametriAutenticazioneBasic(this.parametri);
		if(authBasic.getCleanHeaderAuthorization()!=null) {
			this.cleanHeaderAuthorization = authBasic.getCleanHeaderAuthorization();
		}
		
	}
	
    @Override
    public EsitoAutenticazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaDelegata esito = new EsitoAutenticazionePortaDelegata();
    	
    	Credenziali credenziali = datiInvocazione.getInfoConnettoreIngresso().getCredenziali();
		
    	IDSoggetto soggettoFruitore = null;
    	if(datiInvocazione!=null && datiInvocazione.getPd()!=null) {
    		soggettoFruitore = new IDSoggetto(datiInvocazione.getPd().getTipoSoggettoProprietario(), datiInvocazione.getPd().getNomeSoggettoProprietario());
    	}
    		
		String user = credenziali.getUsername();
		String password = credenziali.getPassword();

		// NOTA: in http-basic il processo di autenticazione ed il processo di identificazione sono unito a differenza di ssl/principal
		//       le credenziali devono essere verificate all'interno della base dati del registro, che in ugual maniera permette anche di identificare l'attore
		//		 Nel caso optional, la transazione continuera' correttamente, ma verra' comunque segnalato le credenziali errate nei diagnostici.
		//		 a differenza dei casi ssl/principal dove credenziali che non corrispondono ad alcun attore, non comportano una segnalazione nei diagnostici.
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		CryptConfig cryptConfigApplicativi = op2Properties.getCryptConfigAutenticazioneApplicativi();
		String realm = op2Properties.getRealmAutenticazioneBasic();
		
		// Controllo credenziali fornite
		if( (user==null) || ("".equals(user)) || (password==null) || ("".equals(password)) ){
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaBasic("credenziali non fornite",user,password));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			if(realm!=null) {
				esito.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildBasicHeaderValue(realm));
			}
			return esito;
		}
		esito.setCredential(user);
		
		IDServizioApplicativo idServizioApplicativo = null;
		try{
			idServizioApplicativo = ConfigurazionePdDManager.getInstance(datiInvocazione.getState()).
						getIdServizioApplicativoByCredenzialiBasic(user, password, cryptConfigApplicativi);
			if(idServizioApplicativo!=null && soggettoFruitore==null) {
				soggettoFruitore = idServizioApplicativo.getIdSoggettoProprietario();
			}
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneBasic non riuscita",e);
			
			esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idServizioApplicativo == null){
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaBasic("credenziali fornite non corrette",user,password));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			if(realm!=null) {
				esito.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildBasicHeaderValue(realm));
			}
			return esito;
		}
		else if(idServizioApplicativo.getIdSoggettoProprietario().equals(soggettoFruitore)==false) {
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaBasic("soggetto proprietario dell'applicativo identificato differente dal soggetto proprietario della porta invocata",user,password));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			if(realm!=null) {
				esito.setWwwAuthenticateErrorHeader(WWWAuthenticateGenerator.buildBasicHeaderValue(realm));
			}
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
    	AutenticazioneUtils.finalizeProcessHeaderAuthorization(message, this.cleanHeaderAuthorization);
    }

}

