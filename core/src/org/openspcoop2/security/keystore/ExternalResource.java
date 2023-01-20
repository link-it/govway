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

import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.transport.http.ExternalResourceConfig;
import org.openspcoop2.utils.transport.http.ExternalResourceUtils;

/**
 * ExternalResource
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExternalResource implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String EXTERNAL_RESOURCE_PROTOCOL = "external_resource://";
	
	private String id;
	private byte[] resource;
	
	public ExternalResource(String resource, ExternalResourceConfig externalConfig) throws SecurityException {
		this(generateId() ,resource, externalConfig);
	}
	public ExternalResource(String id, String resource, ExternalResourceConfig externalConfig) throws SecurityException {
		this.id = id;
		try {
			this.resource = ExternalResourceUtils.readResource(resource, externalConfig);
		}catch(Exception e) {
			throw new SecurityException(e.getMessage(),e);
		}
	}

	private static String generateId() throws SecurityException {
		String uniqueId = null;
		if(UniqueIdentifierManager.isInitialized()) {
			try {
				uniqueId = UniqueIdentifierManager.newUniqueIdentifier().getAsString();
			}catch (Throwable e) {
				throw new SecurityException(e.getMessage(),e);
			}
		}
		if(uniqueId==null) {
			uniqueId = IDUtilities.getUniqueSerialNumber()+"";
		}
		return uniqueId;
	}
		
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public byte[] getResource() {
		return this.resource;
	}
	public void setResource(byte[] resource) {
		this.resource = resource;
	}
	public String getPathId() {
		return EXTERNAL_RESOURCE_PROTOCOL+this.id;
	}
	
}
