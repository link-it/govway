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

package org.openspcoop2.core.registry.ws.client.portadominio.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdPortaDominio;


/**
 * <p>Java class for deleteById complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteById">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idPortaDominio" type="{http://www.openspcoop2.org/core/registry}id-porta-dominio"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteById", propOrder = {
    "idPortaDominio"
})
public class DeleteById {

    @XmlElement(required = true)
    protected IdPortaDominio idPortaDominio;

    /**
     * Gets the value of the idPortaDominio property.
     * 
     * @return
     *     possible object is
     *     {@link IdPortaDominio }
     *     
     */
    public IdPortaDominio getIdPortaDominio() {
        return this.idPortaDominio;
    }

    /**
     * Sets the value of the idPortaDominio property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdPortaDominio }
     *     
     */
    public void setIdPortaDominio(IdPortaDominio value) {
        this.idPortaDominio = value;
    }

}
