/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.logger.beans.context.core;

import java.io.Serializable;

/**
 * BaseClient
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BaseClient extends BaseConnection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String principal;
	private String interfaceName; // es. portaDelegata
	private String invocationEndpoint;
	
	public String getInterfaceName() {
		return this.interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getPrincipal() {
		return this.principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getInvocationEndpoint() {
		return this.invocationEndpoint;
	}
	public void setInvocationEndpoint(String invocationEndpoint) {
		this.invocationEndpoint = invocationEndpoint;
	}
	
}
