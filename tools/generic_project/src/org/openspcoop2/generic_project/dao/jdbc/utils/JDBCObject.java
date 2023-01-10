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
package org.openspcoop2.generic_project.dao.jdbc.utils;

/**
 * JDBCObject
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCObject {

	private Object object;
	private Class<?> typeObject;
	
	public JDBCObject(Object object,Class<?> typeObject){
		this.object = object;
		this.typeObject = typeObject;
	}
	
	public Object getObject() {
		return this.object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public Class<?> getTypeObject() {
		return this.typeObject;
	}
	public void setTypeObject(Class<?> typeObject) {
		this.typeObject = typeObject;
	}
	
}
