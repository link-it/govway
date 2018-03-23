package org.openspcoop2.core.transazioni.ws.server.exception;

import java.io.Serializable;

/**     
 * TransazioniNotFoundException_Exception (contains FaultInfo TransazioniNotFoundException)
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@javax.xml.ws.WebFault(name = "transazioni-not-found-exception", targetNamespace = "http://www.openspcoop2.org/core/transazioni/management")
public class TransazioniNotFoundException_Exception extends Exception implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotFoundException faultInfo;

	// Deprecated: Costruttori utili se i beans vengono usati al posto di quelli generati tramite tools automatici
	@Deprecated
	public TransazioniNotFoundException_Exception(String message, java.lang.Throwable t){
		super(message, t);
	}
	@Deprecated
	public TransazioniNotFoundException_Exception(String message){
		super(message);
	}
	
	public TransazioniNotFoundException_Exception(String message, org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotFoundException faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
    }

	public TransazioniNotFoundException_Exception(String message, org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotFoundException faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public org.openspcoop2.core.transazioni.ws.server.exception.TransazioniNotFoundException getFaultInfo() {
		return this.faultInfo;
	}

}