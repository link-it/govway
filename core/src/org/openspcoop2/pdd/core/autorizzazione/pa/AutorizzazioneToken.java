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



package org.openspcoop2.pdd.core.autorizzazione.pa;

/**
 * Interfaccia che definisce un processo di autorizzazione sui token
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneToken extends AbstractAutorizzazioneBase {

	@Override
	public boolean saveAuthorizationResultInCache() {
		return false;
	}
	
    @Override
	public EsitoAutorizzazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione){
    	
    	// La logica di autorizzazione per token (richiedente, ruoli, scope e options) viene realizzata nel Gestore dell'Autorizzazione
    	
    	EsitoAutorizzazionePortaApplicativa esito = new EsitoAutorizzazionePortaApplicativa();
    	esito.setAutorizzato(true);
    	return esito;
    }
	
}
