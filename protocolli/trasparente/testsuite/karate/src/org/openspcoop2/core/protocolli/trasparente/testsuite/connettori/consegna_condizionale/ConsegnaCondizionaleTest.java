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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_condizionale;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;

/**
 * Classe che contiene utility per accedere a risorse http 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConsegnaCondizionaleTest extends ConfigLoader {
	
	
	@Test
	public void identificazioneCondizioneFallitaLogError() {
		// Il connettore di fallback è il 3
		// TODO Chiedi ad andrea come verificare il messaggio di log, suppongo la traccia?
		// TODO: Faccio anche il test per "disabilitato?" l'obbiettivo è sempre tenere basso il tempo totale..

		
	}
	
	@Test
	public void identificazioneCondizioneFallitaLogInfo() {
	}
	
	
	@Test
	public void nessunConnettoreUtilizzabileLogError() {
		
	}
	
	
	@Test
	public void nessunConnettoreUtilizzabileLogInfo() {
		
	}
	
	@Test
	public void filtroNomeHttp() {
		
	}
	
	@Test
	public void filtroNomeUrlInvocazione() {
		
	}
	
	// ecc..
	// ....
	
	@Test
	public void filtroFiltroHttp() {
		
	}
	
	
	@Test
	public void filtroFiltroUrlInvocazione() {
		
	}
	
	// ecc... Forse farli in una nuova classe a sto punto
	
	
	@Test
	public void filtroNomeHeaderHttpRegole() {
		
	}
	

}
