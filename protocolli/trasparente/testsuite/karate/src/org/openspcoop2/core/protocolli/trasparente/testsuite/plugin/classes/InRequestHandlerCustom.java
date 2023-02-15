/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestContext;

/**
* InRequestHandlerCustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class InRequestHandlerCustom implements org.openspcoop2.pdd.core.handlers.InRequestHandler {

	@Override
	public void invoke(InRequestContext context) throws HandlerException {
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = "in-request-handler";
			if(TipoPdD.APPLICATIVA.equals(context.getTipoPorta())) {
				tipoTest = tipoTest + "-pa";
			}
			else {
				tipoTest = tipoTest + "-pd";
			}
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Exception e) {
			throw new HandlerException(e.getMessage(),e);
		}
		
	}

}
