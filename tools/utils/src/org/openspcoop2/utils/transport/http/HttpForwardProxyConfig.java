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

package org.openspcoop2.utils.transport.http;

import java.io.Serializable;

/**
 * ForwardProxyConfigurazione
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpForwardProxyConfig implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String header;
	protected boolean headerBase64 = true;
	protected String query;
	protected boolean queryBase64 = true;
	
	public HttpForwardProxyConfig() {
		// nop
	}
	
	@Override
	public String toString(){
		return this.toString("\n");
	}
	public String toString(String separator){
		StringBuilder sb = new StringBuilder();
		if(this.header!=null) {
			sb.append("header: ").append(this.header);
			sb.append(separator).append("header-base64: ").append(this.headerBase64);
		}
		if(this.query!=null) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append("query: ").append(this.query);
			sb.append(separator).append("query-base64: ").append(this.queryBase64);
		}
		return sb.toString();
	}	
	
	public String getHeader() {
		return this.header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public boolean isHeaderBase64() {
		return this.headerBase64;
	}

	public void setHeaderBase64(boolean headerBase64) {
		this.headerBase64 = headerBase64;
	}

	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean isQueryBase64() {
		return this.queryBase64;
	}

	public void setQueryBase64(boolean queryBase64) {
		this.queryBase64 = queryBase64;
	}
	
}
