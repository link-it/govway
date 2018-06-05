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

import java.util.Properties;

import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneUtils;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.GestoreToken;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;

/**
 * Classe che implementa una autenticazione BASIC.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 13726 $, $Date: 2018-03-12 17:01:57 +0100 (Mon, 12 Mar 2018) $
 */

public class GestioneToken {

	
    public EsitoPresenzaTokenPortaDelegata verificaPresenzaToken(DatiInvocazionePortaDelegata datiInvocazione) throws TokenException{

    	EsitoPresenzaTokenPortaDelegata esito = new EsitoPresenzaTokenPortaDelegata();
    	
    	GestoreToken.verificaPosizioneToken(datiInvocazione, esito);
    	
    	if(esito.getEccezioneProcessamento()!=null) {
    		// e' avvenuto un errore interno
    		//lanciare eccezione
    	}
    	else if(esito.isPresente()==false) {
    		
   	   
    		// FINIRE

    		
    	}
    	/*
    	
    	try{
    		Properties properties = datiInvocazione.getPolicyGestioneToken().getDefaultProperties();
    		String source = properties.getProperty(Costanti.POLICY_TOKEN_SOURCE);
    		
    	}catch(Exception e){
    		
    	}
    		
    	Credenziali credenziali = datiInvocazione.getInfoConnettoreIngresso().getCredenziali();
		
		String user = credenziali.getUsername();
		String password = credenziali.getPassword();

		// NOTA: in http-basic il processo di autenticazione ed il processo di identificazione sono unito a differenza di ssl/principal
		//       le credenziali devono essere verificate all'interno della base dati del registro, che in ugual maniera permette anche di identificare l'attore
		//		 Nel caso optional, la transazione continuera' correttamente, ma verra' comunque segnalato le credenziali errate nei diagnostici.
		//		 a differenza dei casi ssl/principal dove credenziali che non corrispondono ad alcun attore, non comportano una segnalazione nei diagnostici.
		
		// Controllo credenziali fornite
		if( (user==null) || ("".equals(user)) || (password==null) || ("".equals(password)) ){
			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaBasic("credenziali non fornite",user,password));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
		}
		
		IDServizioApplicativo idServizioApplicativo = null;
		try{
			idServizioApplicativo = ConfigurazionePdDManager.getInstance(datiInvocazione.getState()).
						getIdServizioApplicativoByCredenzialiBasic(user, password);
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneBasic non riuscita",e);
			
			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idServizioApplicativo == null){
			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaBasic("credenziali fornite non corrette",user,password));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
		}
		else {
			esito.setClientAuthenticated(true);
			esito.setClientIdentified(true);
			esito.setIdServizioApplicativo(idServizioApplicativo);
		}
		*/
		return esito;
		
    }
    
  
}

