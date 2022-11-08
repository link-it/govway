/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.ws.server.beans;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**     
 * UseInfo
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "use-info", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "inUseCondition",
    "used"
})
@javax.xml.bind.annotation.XmlRootElement(name = "use-info")
public class UseInfo extends org.openspcoop2.utils.beans.BaseBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="inUseCondition",required=false,nillable=false)
	private List<InUseCondition> inUseCondition = new ArrayList<InUseCondition>();
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
	@XmlElement(name="used",required=true,nillable=false)
	private boolean used;
	
	public void addInUseCondition(InUseCondition inUse){
		this.inUseCondition.add(inUse);
	}
	
	public InUseCondition getInUseCondition(int index){
		return this.inUseCondition.get(index);
	}
	
	public InUseCondition removeInUseCondition(int index){
		return this.inUseCondition.remove(index);
	}
	
	public int sizeInUseConditionList(){
		return this.inUseCondition.size();
	}

	public List<InUseCondition> getInUseCondition() {
		return this.inUseCondition;
	}

	public void setInUseCondition(List<InUseCondition> inUse) {
		this.inUseCondition = inUse;
	}

	public boolean isUsed() {
		return this.used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
	
}