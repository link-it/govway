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

import java.io.IOException;

import javax.net.ssl.SSLContext;

import org.openspcoop2.utils.UtilsException;

/**
 * HttpLibraryConnection
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class HttpLibraryConnection {
	
	public abstract HttpResponse send(HttpRequest req, SSLContext sslContext, OCSPTrustManager ocspTrustManager) throws UtilsException, IOException;
		
	public static HttpLibraryConnection fromLibrary(HttpLibrary lib) {
		if(lib==null) {
			return null;
		}
		switch (lib) {
			case HTTP_CORE5: return new HttpCoreConnection();
			case HTTP_URL_CONNECTION: return new UrlConnectionConnection();
			default: return null;
		}
	}
	
	private static HttpLibrary defaultLibrary = HttpLibrary.HTTP_CORE5; 
	public static HttpLibrary getDefaultLibrary() {
		return HttpLibraryConnection.defaultLibrary;
	}
	public static void setDefaultLibrary(HttpLibrary defaultLibrary) {
		if(defaultLibrary!=null) {
			HttpLibraryConnection.defaultLibrary = defaultLibrary;
		}
	}
}
