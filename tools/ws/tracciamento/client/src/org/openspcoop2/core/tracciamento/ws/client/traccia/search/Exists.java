/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.core.tracciamento.ws.client.traccia.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.tracciamento.IdTraccia;


/**
 * <p>Java class for exists complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="exists"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idTraccia" type="{http://www.openspcoop2.org/core/tracciamento}id-traccia"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "exists", propOrder = {
    "idTraccia"
})
public class Exists {

    @XmlElement(required = true)
    protected IdTraccia idTraccia;

    /**
     * Gets the value of the idTraccia property.
     * 
     * @return
     *     possible object is
     *     {@link IdTraccia }
     *     
     */
    public IdTraccia getIdTraccia() {
        return this.idTraccia;
    }

    /**
     * Sets the value of the idTraccia property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdTraccia }
     *     
     */
    public void setIdTraccia(IdTraccia value) {
        this.idTraccia = value;
    }

}
