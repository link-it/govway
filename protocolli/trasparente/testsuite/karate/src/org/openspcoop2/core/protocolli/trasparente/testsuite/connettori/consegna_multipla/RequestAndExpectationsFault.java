package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.consegna_multipla;

import java.util.Set;

import org.openspcoop2.utils.transport.http.HttpRequest;

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
