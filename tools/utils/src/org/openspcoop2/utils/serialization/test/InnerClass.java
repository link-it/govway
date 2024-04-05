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
package org.openspcoop2.utils.serialization.test;

import java.io.Serializable;
import java.util.Objects;

import org.bouncycastle.util.Arrays;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class InnerClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String str;
	private long lng;
	private byte[] bytea;
	public byte[] getBytea() {
		return this.bytea;
	}
	public void setBytea(byte[] bytea) {
		this.bytea = bytea;
	}
	public String getStr() {
		return this.str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public long getLng() {
		return this.lng;
	}
	public void setLng(long lng) {
		this.lng = lng;
	}
	/**
	 * 
	 */
	public void init() {
		this.str = Math.random() + "";
		this.lng = Math.round(Math.random());
		this.bytea = "TESTINTERNAL".getBytes(); 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof InnerClass))
			return false;
		
		InnerClass classObj = (InnerClass) obj;
		if(!Objects.equals(this.str,classObj.getStr()))
			return false;
		if(this.lng != classObj.getLng())
			return false;
		if(!Arrays.areEqual(this.bytea, classObj.getBytea()))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
