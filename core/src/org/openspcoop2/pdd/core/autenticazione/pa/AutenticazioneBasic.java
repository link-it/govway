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



package org.openspcoop2.pdd.core.autenticazione.pa;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneUtils;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;

/**
 * Classe che implementa una autenticazione BASIC.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutenticazioneBasic extends AbstractAutenticazioneBase {

	protected boolean cleanHeaderAuthorization = true;
	
    @Override
    public EsitoAutenticazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaApplicativa esito = new EsitoAutenticazionePortaApplicativa();
    	
    	Credenziali credenziali = datiInvocazione.getInfoConnettoreIngresso().getCredenziali();
		
		String user = credenziali.getUsername();
		String password = credenziali.getPassword();

		// NOTA: in http-basic il processo di autenticazione ed il processo di identificazione sono unito a differenza di ssl/principal
		//       le credenziali devono essere verificate all'interno della base dati del registro, che in ugual maniera permette anche di identificare l'attore
		//		 Nel caso optional, la transazione continuera' correttamente, ma verra' comunque segnalato le credenziali errate nei diagnostici.
		//		 a differenza dei casi ssl/principal dove credenziali che non corrispondono ad alcun attore, non comportano una segnalazione nei diagnostici.
		
		// Controllo credenziali fornite
		if( (user==null) || ("".equals(user)) || (password==null) || ("".equals(password)) ){
			esito.setErroreCooperazione(ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE.getErroreCooperazione());
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
		}
		esito.setCredential(user);
		
		IDSoggetto idSoggetto = null;
		try{
			idSoggetto = RegistroServiziManager.getInstance(datiInvocazione.getState()).getIdSoggettoByCredenzialiBasic(user, password, null); // all registry
		}
		catch(DriverRegistroServiziNotFound notFound){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("AutenticazioneBasic non ha trovato risultati",notFound);
		}
		catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneBasic non riuscita",e);
			esito.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idSoggetto == null){
			esito.setErroreCooperazione(ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_FORNITE_NON_CORRETTE.getErroreCooperazione());
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
		}
		else {
			esito.setClientAuthenticated(true);
			esito.setClientIdentified(true);
			esito.setIdSoggetto(idSoggetto);
		}
		
		return esito;
		
    }
    
    @Override
	public void cleanPostAuth(OpenSPCoop2Message message) throws AutenticazioneException {
    	if(this.cleanHeaderAuthorization) {
    		AutenticazioneUtils.cleanHeaderAuthorization(message);
    	}
    }

}

