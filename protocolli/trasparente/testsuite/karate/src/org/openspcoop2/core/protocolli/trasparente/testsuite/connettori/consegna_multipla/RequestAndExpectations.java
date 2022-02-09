package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla;

import java.util.Set;

import org.openspcoop2.utils.transport.http.HttpRequest;

public class RequestAndExpectations {

	public  HttpRequest request;
	public Set<String> connettoriSuccesso;
	public Set<String> connettoriFallimento;
	public int esito;
	public int statusCodePrincipale; 
	public boolean principaleSuperata;
	
	public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito) {
		this(request, connettoriSuccesso, connettoriFallimento, esito, 200);	
	}
	
	public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito, int statusCodePrincipale) {
		this(request, connettoriSuccesso, connettoriFallimento, esito, statusCodePrincipale, true);
	}
	
	public RequestAndExpectations(HttpRequest request, Set<String> connettoriSuccesso, Set<String> connettoriFallimento, int esito, int statusCodePrincipale, boolean principaleSuperata) {
		this.request = request;
		this.connettoriSuccesso = connettoriSuccesso;
		this.connettoriFallimento = connettoriFallimento;
		this.esito = esito;
		this.statusCodePrincipale = statusCodePrincipale;
		this.principaleSuperata = principaleSuperata;
	}
	
}