/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
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

public class AutenticazionePrincipal extends AbstractAutenticazioneBase {

    @Override
    public EsitoAutenticazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaApplicativa esito = new EsitoAutenticazionePortaApplicativa();
    	
    	Credenziali credenziali = datiInvocazione.getInfoConnettoreIngresso().getCredenziali();
		
    	String principal = credenziali.getPrincipal();

		// Controllo credenziali fornite
    	if( principal==null ){
    		esito.setErroreCooperazione(ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE.getErroreCooperazione());
			esito.setClientIdentified(false);
			return esito;
		}
		
    	IDSoggetto idSoggetto = null;
		try{
			idSoggetto = RegistroServiziManager.getInstance(datiInvocazione.getState()).getIdSoggettoByCredenzialiPrincipal(principal, null); // all registry
		}
		catch(DriverRegistroServiziNotFound notFound){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("AutenticazionePrincipal non ha trovato risultati",notFound);
		}
		catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazionePrincipal non riuscita",e);
			esito.setErroreCooperazione(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione());
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idSoggetto == null){
			esito.setErroreCooperazione(ErroriCooperazione.AUTENTICAZIONE_FALLITA_CREDENZIALI_FORNITE_NON_CORRETTE.getErroreCooperazione());
			esito.setClientIdentified(false);
			return esito;
		}
		
		esito.setClientIdentified(true);
		esito.setIdSoggetto(idSoggetto);
		
		return esito;
		
    }

}

