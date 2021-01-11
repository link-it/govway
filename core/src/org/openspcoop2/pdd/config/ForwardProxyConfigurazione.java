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

package org.openspcoop2.pdd.config;

import java.io.Serializable;

/**
 * ForwardProxyConfigurazione
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ForwardProxyConfigurazione implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String header;
	protected boolean headerBase64;
	protected String query;
	protected boolean queryBase64;
	
	public ForwardProxyConfigurazione() {
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(this.header!=null) {
			sb.append("header: ").append(this.header);
			sb.append("\n").append("header-base64: ").append(this.headerBase64);
		}
		if(this.query!=null) {
			if(sb.length()>0) {
				sb.append("\n");
			}
			sb.append("query: ").append(this.query);
			sb.append("\n").append("query-base64: ").append(this.queryBase64);
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
