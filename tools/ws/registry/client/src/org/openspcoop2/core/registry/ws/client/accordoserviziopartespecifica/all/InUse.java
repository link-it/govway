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

package org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;


/**
 * <p>Java class for inUse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="inUse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idAccordoServizioParteSpecifica" type="{http://www.openspcoop2.org/core/registry}id-accordo-servizio-parte-specifica"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "inUse", propOrder = {
    "idAccordoServizioParteSpecifica"
})
public class InUse {

    @XmlElement(required = true)
    protected IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica;

    /**
     * Gets the value of the idAccordoServizioParteSpecifica property.
     * 
     * @return
     *     possible object is
     *     {@link IdAccordoServizioParteSpecifica }
     *     
     */
    public IdAccordoServizioParteSpecifica getIdAccordoServizioParteSpecifica() {
        return this.idAccordoServizioParteSpecifica;
    }

    /**
     * Sets the value of the idAccordoServizioParteSpecifica property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdAccordoServizioParteSpecifica }
     *     
     */
    public void setIdAccordoServizioParteSpecifica(IdAccordoServizioParteSpecifica value) {
        this.idAccordoServizioParteSpecifica = value;
    }

}
