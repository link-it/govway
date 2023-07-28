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

package org.openspcoop2.core.registry.ws.client.gruppo.crud;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.IdGruppo;


/**
 * <p>Java class for updateOrCreate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateOrCreate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="oldIdGruppo" type="{http://www.openspcoop2.org/core/registry}id-gruppo"/&gt;
 *         &lt;element name="gruppo" type="{http://www.openspcoop2.org/core/registry}gruppo"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateOrCreate", propOrder = {
    "oldIdGruppo",
    "gruppo"
})
public class UpdateOrCreate {

    @XmlElement(required = true)
    protected IdGruppo oldIdGruppo;
    @XmlElement(required = true)
    protected Gruppo gruppo;

    /**
     * Gets the value of the oldIdGruppo property.
     * 
     * @return
     *     possible object is
     *     {@link IdGruppo }
     *     
     */
    public IdGruppo getOldIdGruppo() {
        return this.oldIdGruppo;
    }

    /**
     * Sets the value of the oldIdGruppo property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdGruppo }
     *     
     */
    public void setOldIdGruppo(IdGruppo value) {
        this.oldIdGruppo = value;
    }

    /**
     * Gets the value of the gruppo property.
     * 
     * @return
     *     possible object is
     *     {@link Gruppo }
     *     
     */
    public Gruppo getGruppo() {
        return this.gruppo;
    }

    /**
     * Sets the value of the gruppo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gruppo }
     *     
     */
    public void setGruppo(Gruppo value) {
        this.gruppo = value;
    }

}
