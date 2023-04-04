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
package org.openspcoop2.security.keystore;

import java.io.Serializable;

import org.openspcoop2.core.commons.ConnettoreHTTPSProperties;
import org.openspcoop2.utils.transport.http.SSLConfig;

/**
 * SSLConfigProps
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLConfigProps implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private SSLConfig sslconfig;
	
	public SSLConfigProps(String id, boolean sslConfigRequired) throws SecurityException {
		this.id = id;
		try {
			this.sslconfig = ConnettoreHTTPSProperties.readPropertyFile(id, sslConfigRequired);
		}catch(Exception e) {
			throw new SecurityException(e.getMessage(),e);
		}
	}

	public String getId() {
		return this.id;
	}
	public SSLConfig getSslconfig() {
		return this.sslconfig;
	}
	
}
