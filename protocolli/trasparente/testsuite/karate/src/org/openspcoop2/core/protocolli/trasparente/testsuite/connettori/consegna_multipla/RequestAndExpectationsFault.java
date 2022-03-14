/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla;

import java.util.Set;

import org.openspcoop2.utils.transport.http.HttpRequest;

/**
 * RequestAndExpectationsFault
 * 
 * @author Francesco Scarlato
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequestAndExpectationsFault extends RequestAndExpectations {

	public String faultMessage;
	public String faultType;
	
	public RequestAndExpectationsFault(HttpRequest request, Set<String> connettoriSuccesso, 
			Set<String> connettoriFallimento, int esito, int statusCodePrincipale, TipoFault tipoFault, String faultMessage, String faultType) {
		
		super(request, connettoriSuccesso, connettoriFallimento, esito, statusCodePrincipale, tipoFault);
		this.faultMessage = faultMessage;
		this.faultType = faultType;
	}

}
