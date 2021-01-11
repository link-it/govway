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

package org.openspcoop2.utils.jdbc;

/**
 * Oggetto da inserire 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InsertAndGeneratedKeyObject {

	private String name;
	private Object value;
	private InsertAndGeneratedKeyJDBCType jdbcType;

	public InsertAndGeneratedKeyObject(String name,Object value,InsertAndGeneratedKeyJDBCType jdbcType){
		this.name = name;
		this.value = value;
		this.jdbcType = jdbcType;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return this.value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public InsertAndGeneratedKeyJDBCType getJdbcType() {
		return this.jdbcType;
	}
	public void setJdbcType(InsertAndGeneratedKeyJDBCType jdbcType) {
		this.jdbcType = jdbcType;
	}
}
