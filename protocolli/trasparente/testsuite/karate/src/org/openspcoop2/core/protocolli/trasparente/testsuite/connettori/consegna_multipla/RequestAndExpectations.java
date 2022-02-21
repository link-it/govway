package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla;

import java.util.Set;

import org.openspcoop2.utils.transport.http.HttpRequest;

public class RequestAndExpectations {
	
	public enum TipoFault {
		NESSUNO, REST, SOAP1_1, SOAP1_2;
	}

	public  HttpRequest request;
	public Set<String> connettoriSuccesso;
	public Set<String> connettoriFallimento;
	public int esitoPrincipale;
	public int statusCodePrincipale; 
	public boolean principaleSuperata;
	public TipoFault tipoFault;
	
	/*public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito) {
		this(request, connettoriSuccesso, connettoriFallimento, esito, 200);	
	}*/
	
	public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito, int statusCodePrincipale) {		
		this(request, connettoriSuccesso, connettoriFallimento, esito, statusCodePrincipale, true);
	}
	
	/*
	 * Per la consegna multipla non c'è la transazione sincrona, per cui la "principale" si considera sempre come superata
	 */
	public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito, int statusCodePrincipale, TipoFault tipoFault) {
		this(request, connettoriSuccesso, connettoriFallimento, esito, statusCodePrincipale, true, TipoFault.NESSUNO);
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