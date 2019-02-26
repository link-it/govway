/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.rest.entity;

/**
 * Cookie
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Cookie  {

	private String name;
	private String value;
	private String domain;
	private int maxAge;
	private String path;
	private boolean secure;
	private int version;
	
	public Cookie(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDomain() {
		return this.domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public int getMaxAge() {
		return this.maxAge;
	}
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}
	public String getPath() {
		return this.path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isSecure() {
		return this.secure;
	}
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	public int getVersion() {
		return this.version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
}
