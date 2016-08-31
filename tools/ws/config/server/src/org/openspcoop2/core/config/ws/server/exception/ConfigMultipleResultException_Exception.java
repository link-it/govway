/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.core.config.ws.server.exception;

import java.io.Serializable;

/**     
 * ConfigMultipleResultException_Exception (contains FaultInfo ConfigMultipleResultException)
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@javax.xml.ws.WebFault(name = "config-multiple-result-exception", targetNamespace = "http://www.openspcoop2.org/core/config/management")
public class ConfigMultipleResultException_Exception extends Exception implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private org.openspcoop2.core.config.ws.server.exception.ConfigMultipleResultException faultInfo;

	// Deprecated: Costruttori utili se i beans vengono usati al posto di quelli generati tramite tools automatici
	@Deprecated
	public ConfigMultipleResultException_Exception(String message, java.lang.Throwable t){
		super(message, t);
	}
	@Deprecated
	public ConfigMultipleResultException_Exception(String message){
		super(message);
	}
	
	public ConfigMultipleResultException_Exception(String message, org.openspcoop2.core.config.ws.server.exception.ConfigMultipleResultException faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
    }

	public ConfigMultipleResultException_Exception(String message, org.openspcoop2.core.config.ws.server.exception.ConfigMultipleResultException faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public org.openspcoop2.core.config.ws.server.exception.ConfigMultipleResultException getFaultInfo() {
		return this.faultInfo;
	}

}