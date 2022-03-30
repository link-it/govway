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
 * RequestAndExpectations
 * 
 * @author Francesco Scarlato
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequestAndExpectations {
	
	public enum TipoFault {
		NESSUNO, REST, SOAP1_1, SOAP1_2;
	}

	public  HttpRequest request;
	public Set<String> connettoriSuccesso;
	public Set<String> connettoriFallimento;
	public int esitoPrincipale;										// E' l'esito della transazionePrincipale alla fine del test
	public int statusCodePrincipale; 							// Nel caso della consegna multipla è lo status code delle consegne.
	public boolean principaleSuperata;
	public TipoFault tipoFault;
	
	public int esitoSincrono = -1;								// Utilizzato in quei test in cui non dedurre l'esito sincrono a partire dallo statusCode principale e\o fault non è possibile
	
		
	public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito, int statusCodePrincipale) {		
		this(request, connettoriSuccesso, connettoriFallimento, esito, statusCodePrincipale, true);
	}
	
	/*
	 * Per la consegna multipla non c'è la transazione sincrona, per cui la "principale" si considera sempre come superata
	 */
	public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito, int statusCodePrincipale, TipoFault tipoFault) {
		this(request, connettoriSuccesso, connettoriFallimento, esito, statusCodePrincipale, true, tipoFault);
	}
	
	public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito, int statusCodePrincipale, boolean principaleSuperata) {
		this(request, connettoriSuccesso, connettoriFallimento, esito, statusCodePrincipale, principaleSuperata, TipoFault.NESSUNO);
	}
	
	
	public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito, int statusCodePrincipale, boolean principaleSuperata, TipoFault tipoFault) {
		this.request = request;
		this.connettoriSuccesso = connettoriSuccesso;
		this.connettoriFallimento = connettoriFallimento;
		this.esitoPrincipale = esito;
		this.statusCodePrincipale = statusCodePrincipale;
		this.principaleSuperata = principaleSuperata;
		this.tipoFault = tipoFault;
	}
	
}