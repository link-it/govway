/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.autorizzazione.pa;

import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;

/**
 * Classe che implementa una autorizzazione di test che lancia sempre una eccezione.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneTestEccezioneProcessamento extends AbstractAutorizzazioneBase {


    @Override
	public EsitoAutorizzazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione) throws AutorizzazioneException{

    	Throwable t1 = new Throwable("Eccezione processamento Test Livello 3");
    	Throwable t2 = new Throwable("Eccezione processamento Test Livello 2",t1);
    	Exception e = new Exception("Eccezione processamento Test Livello 1",t2);
    	throw new AutorizzazioneException("Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)", e);
    	
    }
   
	@Override
	public boolean saveAuthorizationResultInCache() {
		return false;
	}
	
}

