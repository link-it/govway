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



package org.openspcoop2.pdd.core.autorizzazione.pa;

/**
 * Interfaccia che definisce un processo di autorizzazione sui token
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 14113 $, $Date: 2018-06-08 12:32:37 +0200 (Fri, 08 Jun 2018) $
 */

public class AutorizzazioneToken extends AbstractAutorizzazioneBase {

	@Override
	public boolean saveAuthorizationResultInCache() {
		return false;
	}
	
    @Override
	public EsitoAutorizzazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione){
    	
    	// La logica di autorizzazione per token (scope e options) viene realizzata nel Gestore dell'Autorizzazione
    	
    	EsitoAutorizzazionePortaApplicativa esito = new EsitoAutorizzazionePortaApplicativa();
    	esito.setAutorizzato(true);
    	return esito;
    }
	
}
