/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.io.Serializable;

/**
 * HttpRequestMethod
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum HttpRequestMethod implements Serializable {

	// STANDARD
	GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, 
	
	// ADDITIONAL 
	// Servlet: Method PATCH is not defined in RFC 2068 and is not supported by the Servlet API
	// https://tools.ietf.org/html/rfc2068#section-19.6.1
	PATCH, LINK, UNLINK;
	
	// https://tools.ietf.org/html/rfc2068#section-9
	public boolean isStandardMethod() {
		if(HttpRequestMethod.GET.name().equals(this.name())
				||
			HttpRequestMethod.HEAD.name().equals(this.name())
			||
			HttpRequestMethod.POST.name().equals(this.name())
			||
			HttpRequestMethod.PUT.name().equals(this.name())
			||
			HttpRequestMethod.DELETE.name().equals(this.name())
			||
			HttpRequestMethod.OPTIONS.name().equals(this.name())
			||
			HttpRequestMethod.TRACE.name().equals(this.name())
				){
			return true;
		}
		return false;
	}
	
	// https://tools.ietf.org/html/rfc2068#section-19.6.1
	public boolean isAdditionalMethod() {
		if(HttpRequestMethod.PATCH.name().equals(this.name())
				||
			HttpRequestMethod.LINK.name().equals(this.name())
			||
			HttpRequestMethod.UNLINK.name().equals(this.name())
				){
			return true;
		}
		return false;
	}
	
	public boolean equals(HttpRequestMethod object){
		if(object==null)
			return false;
		if(object.name()==null)
			return false;
		return object.name().equals(this.name());	
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.name());	
	}
}
