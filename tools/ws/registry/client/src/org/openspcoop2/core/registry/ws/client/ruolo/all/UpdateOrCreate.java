/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.core.registry.ws.client.ruolo.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.IdRuolo;
import org.openspcoop2.core.registry.Ruolo;


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
 *         &lt;element name="oldIdRuolo" type="{http://www.openspcoop2.org/core/registry}id-ruolo"/&gt;
 *         &lt;element name="ruolo" type="{http://www.openspcoop2.org/core/registry}ruolo"/&gt;
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
    "oldIdRuolo",
    "ruolo"
})
public class UpdateOrCreate {

    @XmlElement(required = true)
    protected IdRuolo oldIdRuolo;
    @XmlElement(required = true)
    protected Ruolo ruolo;

    /**
     * Gets the value of the oldIdRuolo property.
     * 
     * @return
     *     possible object is
     *     {@link IdRuolo }
     *     
     */
    public IdRuolo getOldIdRuolo() {
        return this.oldIdRuolo;
    }

    /**
     * Sets the value of the oldIdRuolo property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdRuolo }
     *     
     */
    public void setOldIdRuolo(IdRuolo value) {
        this.oldIdRuolo = value;
    }

    /**
     * Gets the value of the ruolo property.
     * 
     * @return
     *     possible object is
     *     {@link Ruolo }
     *     
     */
    public Ruolo getRuolo() {
        return this.ruolo;
    }

    /**
     * Sets the value of the ruolo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Ruolo }
     *     
     */
    public void setRuolo(Ruolo value) {
        this.ruolo = value;
    }

}
