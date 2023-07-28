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
package org.openspcoop2.core.registry.ws.server.filter.beans;

/**
 * <p>Java class for AccordoServizioParteComuneServizioComposto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-comune-servizio-composto"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="accordo-cooperazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;

/**     
 * AccordoServizioParteComuneServizioComposto
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "accordo-servizio-parte-comune-servizio-composto", namespace="http://www.openspcoop2.org/core/registry/management", propOrder = {
    "accordoCooperazione"
})
@javax.xml.bind.annotation.XmlRootElement(name = "accordo-servizio-parte-comune-servizio-composto")
public class AccordoServizioParteComuneServizioComposto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="accordo-cooperazione",required=false,nillable=false)
	private String accordoCooperazione;
	
	public void setAccordoCooperazione(String accordoCooperazione){
		this.accordoCooperazione = accordoCooperazione;
	}
	
	public String getAccordoCooperazione(){
		return this.accordoCooperazione;
	}
	
	
	
	
}
