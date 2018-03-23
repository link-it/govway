package org.openspcoop2.core.transazioni.ws.server.exception;

import java.io.Serializable;

/**     
 * TransazioniServiceException_Exception (contains FaultInfo TransazioniServiceException)
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@javax.xml.ws.WebFault(name = "transazioni-service-exception", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
public class TransazioniServiceException_Exception extends Exception implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private org.openspcoop2.core.transazioni.ws.server.exception.TransazioniServiceException faultInfo;

	// Deprecated: Costruttori utili se i beans vengono usati al posto di quelli generati tramite tools automatici
	@Deprecated
	public TransazioniServiceException_Exception(String message, java.lang.Throwable t){
		super(message, t);
	}
	@Deprecated
	public TransazioniServiceException_Exception(String message){
		super(message);
	}
	
	public TransazioniServiceException_Exception(String message, org.openspcoop2.core.transazioni.ws.server.exception.TransazioniServiceException faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
    }

	public TransazioniServiceException_Exception(String message, org.openspcoop2.core.transazioni.ws.server.exception.TransazioniServiceException faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public org.openspcoop2.core.transazioni.ws.server.exception.TransazioniServiceException getFaultInfo() {
		return this.faultInfo;
	}

}