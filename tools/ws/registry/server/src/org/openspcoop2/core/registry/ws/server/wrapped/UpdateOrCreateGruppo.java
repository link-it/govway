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
package org.openspcoop2.core.registry.ws.server.wrapped;

/**
 * <p>Java class for UpdateOrCreateGruppo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateOrCreate"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.openspcoop2.org/core/registry}id-gruppo" maxOccurs="1" /&gt;
 *         &lt;element name="obj" type="{http://www.openspcoop2.org/core/registry}gruppo" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.IdGruppo;

/**     
 * UpdateOrCreateGruppo
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
public class UpdateOrCreateGruppo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="id",required=true,nillable=false)
	private IdGruppo id;
	
	public void setId(IdGruppo id){
		this.id = id;
	}
	
	public IdGruppo getId(){
		return this.id;
	}
	
	
	@XmlElement(name="obj",required=true,nillable=false)
	private Gruppo obj;
	
	public void setObj(Gruppo obj){
		this.obj = obj;
	}
	
	public Gruppo getObj(){
		return this.obj;
	}
	
	
	
	
}