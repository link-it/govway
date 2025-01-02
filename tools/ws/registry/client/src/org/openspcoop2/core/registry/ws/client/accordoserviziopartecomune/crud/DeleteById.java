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

package org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.crud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdAccordoServizioParteComune;


/**
 * <p>Java class for deleteById complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteById"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idAccordoServizioParteComune" type="{http://www.openspcoop2.org/core/registry}id-accordo-servizio-parte-comune"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteById", propOrder = {
    "idAccordoServizioParteComune"
})
public class DeleteById {

    @XmlElement(required = true)
    protected IdAccordoServizioParteComune idAccordoServizioParteComune;

    /**
     * Gets the value of the idAccordoServizioParteComune property.
     * 
     * @return
     *     possible object is
     *     {@link IdAccordoServizioParteComune }
     *     
     */
    public IdAccordoServizioParteComune getIdAccordoServizioParteComune() {
        return this.idAccordoServizioParteComune;
    }

    /**
     * Sets the value of the idAccordoServizioParteComune property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdAccordoServizioParteComune }
     *     
     */
    public void setIdAccordoServizioParteComune(IdAccordoServizioParteComune value) {
        this.idAccordoServizioParteComune = value;
    }

}
