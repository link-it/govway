package org.openspcoop2.core.transazioni.ws.server.exception;

import java.io.Serializable;

/**     
 * TransazioniMultipleResultException_Exception (contains FaultInfo TransazioniMultipleResultException)
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@javax.xml.ws.WebFault(name = "transazioni-multiple-result-exception", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
public class TransazioniMultipleResultException_Exception extends Exception implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private org.openspcoop2.core.transazioni.ws.server.exception.TransazioniMultipleResultException faultInfo;

	// Deprecated: Costruttori utili se i beans vengono usati al posto di quelli generati tramite tools automatici
	@Deprecated
	public TransazioniMultipleResultException_Exception(String message, java.lang.Throwable t){
		super(message, t);
	}
	@Deprecated
	public TransazioniMultipleResultException_Exception(String message){
		super(message);
	}
	
	public TransazioniMultipleResultException_Exception(String message, org.openspcoop2.core.transazioni.ws.server.exception.TransazioniMultipleResultException faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
    }

	public TransazioniMultipleResultException_Exception(String message, org.openspcoop2.core.transazioni.ws.server.exception.TransazioniMultipleResultException faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public org.openspcoop2.core.transazioni.ws.server.exception.TransazioniMultipleResultException getFaultInfo() {
		return this.faultInfo;
	}

}