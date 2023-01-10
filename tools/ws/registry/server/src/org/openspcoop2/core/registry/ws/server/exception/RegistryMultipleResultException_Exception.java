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
package org.openspcoop2.core.registry.ws.server.exception;

import java.io.Serializable;

/**     
 * RegistryMultipleResultException_Exception (contains FaultInfo RegistryMultipleResultException)
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@javax.xml.ws.WebFault(name = "registry-multiple-result-exception", targetNamespace = "http://www.openspcoop2.org/core/registry/management")
public class RegistryMultipleResultException_Exception extends Exception implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private org.openspcoop2.core.registry.ws.server.exception.RegistryMultipleResultException faultInfo;

	// Deprecated: Costruttori utili se i beans vengono usati al posto di quelli generati tramite tools automatici
	@Deprecated
	public RegistryMultipleResultException_Exception(String message, java.lang.Throwable t){
		super(message, t);
	}
	@Deprecated
	public RegistryMultipleResultException_Exception(String message){
		super(message);
	}
	
	public RegistryMultipleResultException_Exception(String message, org.openspcoop2.core.registry.ws.server.exception.RegistryMultipleResultException faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
    }

	public RegistryMultipleResultException_Exception(String message, org.openspcoop2.core.registry.ws.server.exception.RegistryMultipleResultException faultInfo, Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public org.openspcoop2.core.registry.ws.server.exception.RegistryMultipleResultException getFaultInfo() {
		return this.faultInfo;
	}

}