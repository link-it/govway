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
package org.openspcoop2.core.config.ws.server.wrapped;

/**
 * <p>Java class for UpdatePortaDelegata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="update"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.openspcoop2.org/core/config}id-porta-delegata" maxOccurs="1" /&gt;
 *         &lt;element name="obj" type="{http://www.openspcoop2.org/core/config}porta-delegata" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.IdPortaDelegata;

/**     
 * UpdatePortaDelegata
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "update", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "id",
    "obj"
})
@javax.xml.bind.annotation.XmlRootElement(name = "update")
public class UpdatePortaDelegata extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="id",required=true,nillable=false)
	private IdPortaDelegata id;
	
	public void setId(IdPortaDelegata id){
		this.id = id;
	}
	
	public IdPortaDelegata getId(){
		return this.id;
	}
	
	
	@XmlElement(name="obj",required=true,nillable=false)
	private PortaDelegata obj;
	
	public void setObj(PortaDelegata obj){
		this.obj = obj;
	}
	
	public PortaDelegata getObj(){
		return this.obj;
	}
	
	
	
	
}