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

package org.openspcoop2.core.protocolli.trasparente.testsuite.plugin.classes;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.Dati;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.PluginsException;
import org.slf4j.Logger;

/**
* RateLimitingCustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RateLimitingCustom implements org.openspcoop2.pdd.core.controllo_traffico.plugins.IRateLimiting {

	@Override
	public String estraiValoreFiltro(Logger log, Dati datiRichiesta) throws PluginsException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "rateLimiting-filtro";
			if(TipoPdD.APPLICATIVA.equals(datiRichiesta.getDatiTransazione().getTipoPdD())) {
				tipoTest = tipoTest + "-pa";
			}
			else {
				tipoTest = tipoTest + "-pd";
			}
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new PluginsException(e.getMessage(),e);
		}
		
		return "govway-testsuite-filtro";
	}

	@Override
	public String estraiValoreCollezionamentoDati(Logger log, Dati datiRichiesta) throws PluginsException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "rateLimiting-groupBy";
			if(TipoPdD.APPLICATIVA.equals(datiRichiesta.getDatiTransazione().getTipoPdD())) {
				tipoTest = tipoTest + "-pa";
			}
			else {
				tipoTest = tipoTest + "-pd";
			}
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new PluginsException(e.getMessage(),e);
		}
		
		return "govway-testsuite-groupBy";
	}



}
