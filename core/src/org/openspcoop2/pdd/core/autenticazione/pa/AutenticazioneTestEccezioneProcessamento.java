/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;

/**
 * Classe che implementa una autorizzazione di test che lancia sempre una eccezione.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutenticazioneTestEccezioneProcessamento extends AbstractAutenticazioneBase {

    @Override
    public EsitoAutenticazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione) throws AutenticazioneException{

    	Throwable t1 = new Throwable("Eccezione processamento Test Livello 3");
    	Throwable t2 = new Throwable("Eccezione processamento Test Livello 2",t1);
    	Exception e = new Exception("Eccezione processamento Test Livello 1",t2);
    	throw new AutenticazioneException("Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)", e);
		
    }

	@Override
	public boolean saveAuthenticationResultInCache() {
		return false;
	}
   
}

