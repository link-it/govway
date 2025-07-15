/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.transport.http;


/**
 * HttpLibrary
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum HttpLibrary {
	
	HTTPCORE("core"),
	URLCONNECTION("http");
	
	private final String name;
	private HttpLibrary(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static HttpLibrary fromName(String name) {
		HttpLibrary[] libs = HttpLibrary.values();
		
		for (HttpLibrary lib : libs)
			if (lib.getName().equals(name))
				return lib;
		return null;
	}
}
