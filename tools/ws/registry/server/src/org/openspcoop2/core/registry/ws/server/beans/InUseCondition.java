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
package org.openspcoop2.core.registry.ws.server.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

/**     
 * InUseCondition
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "in-use-condition", namespace="http://www.openspcoop2.org/core/registry/management", propOrder = {
	"name",
    "type",
    "id",
    "cause"
})

public class InUseCondition extends org.openspcoop2.utils.beans.BaseBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@javax.xml.bind.annotation.XmlSchemaType(name="string")
	@XmlElement(name="name",required=true,nillable=false)
	private String name;

	@XmlElement(name="type",required=false,nillable=false)
	private Identified type;
	
	@XmlElement(name="id",required=false,nillable=false)
	private List<IdEntity> id;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
	@XmlElement(name="cause",required=true,nillable=false)
	private String cause;

	public Identified getType() {
		return this.type;
	}

	public void setType(Identified type) {
		this.type = type;
	}

	public String getCause() {
		return this.cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IdEntity> getId() {
		return this.id;
	}

	public void setId(List<IdEntity> idList) {
		this.id = idList;
	}
	
	public void addId(IdEntity idEntity){
		if(this.id==null)
			this.id = new ArrayList<IdEntity>();
		
		this.id.add(idEntity);
		
	}
}