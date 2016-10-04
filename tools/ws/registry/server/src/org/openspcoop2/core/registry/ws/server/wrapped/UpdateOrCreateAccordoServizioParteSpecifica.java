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
package org.openspcoop2.core.registry.ws.server.wrapped;

/**
 * <p>Java class for UpdateOrCreateAccordoServizioParteSpecifica complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateOrCreate">
 *     &lt;sequence>
 *         &lt;element name="id" type="{http://www.openspcoop2.org/core/registry}id-accordo-servizio-parte-specifica" maxOccurs="1" />
 *         &lt;element name="obj" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-specifica" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;

/**     
 * UpdateOrCreateAccordoServizioParteSpecifica
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "updateOrCreate", namespace="http://www.openspcoop2.org/core/registry/management", propOrder = {
    "id",
    "obj"
})
@javax.xml.bind.annotation.XmlRootElement(name = "updateOrCreate")
public class UpdateOrCreateAccordoServizioParteSpecifica extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="id",required=true,nillable=false)
	private IdAccordoServizioParteSpecifica id;
	
	public void setId(IdAccordoServizioParteSpecifica id){
		this.id = id;
	}
	
	public IdAccordoServizioParteSpecifica getId(){
		return this.id;
	}
	
	
	@XmlElement(name="obj",required=true,nillable=false)
	private AccordoServizioParteSpecifica obj;
	
	public void setObj(AccordoServizioParteSpecifica obj){
		this.obj = obj;
	}
	
	public AccordoServizioParteSpecifica getObj(){
		return this.obj;
	}
	
	
	
	
}