/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.connettori;

import java.net.URI;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * CustomHttpCore5Entity
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CustomHttpCore5Entity extends HttpUriRequestBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HttpRequestMethod httpMethod;
	
    public CustomHttpCore5Entity(HttpRequestMethod httpMethod, final URI uri) {
        super(httpMethod.name(), uri);
        this.httpMethod = httpMethod;
    }

    public CustomHttpCore5Entity(HttpRequestMethod httpMethod, final String uri) {
    	super(httpMethod.name(), URI.create(uri));
        this.httpMethod = httpMethod;
    }
	
	@Override
	public String getMethod() {
		return this.httpMethod.name();
	}
	
}
