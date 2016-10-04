/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * InUseCondition
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InUseCondition<ID,OBJECT> {

	private Class<OBJECT> object;
	
	private String objectName; // Serve anche un objectName, poichè possono esistere elementi con lo stesso tipo, ma con nome differente
	
	private List<ID> ids = new ArrayList<ID>();
	
	private String cause;
	
	public String getCause() {
		return this.cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public Class<OBJECT> getObject() {
		return this.object;
	}

	public void setObject(Class<OBJECT> object) {
		this.object = object;
	}

	public List<ID> getIds() {
		return this.ids;
	}

	public void setIds(List<ID> ids) {
		this.ids = ids;
	}
	
	public void addId(ID id){
		this.ids.add(id);	
	}
	
	public String getObjectName() {
		return this.objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
}
