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

package org.openspcoop2.core.config.ws.client.portaapplicativa.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.IdPortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativa;


/**
 * <p>Java class for updateOrCreate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateOrCreate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="oldIdPortaApplicativa" type="{http://www.openspcoop2.org/core/config}id-porta-applicativa"/>
 *         &lt;element name="portaApplicativa" type="{http://www.openspcoop2.org/core/config}porta-applicativa"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateOrCreate", propOrder = {
    "oldIdPortaApplicativa",
    "portaApplicativa"
})
public class UpdateOrCreate {

    @XmlElement(required = true)
    protected IdPortaApplicativa oldIdPortaApplicativa;
    @XmlElement(required = true)
    protected PortaApplicativa portaApplicativa;

    /**
     * Gets the value of the oldIdPortaApplicativa property.
     * 
     * @return
     *     possible object is
     *     {@link IdPortaApplicativa }
     *     
     */
    public IdPortaApplicativa getOldIdPortaApplicativa() {
        return this.oldIdPortaApplicativa;
    }

    /**
     * Sets the value of the oldIdPortaApplicativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdPortaApplicativa }
     *     
     */
    public void setOldIdPortaApplicativa(IdPortaApplicativa value) {
        this.oldIdPortaApplicativa = value;
    }

    /**
     * Gets the value of the portaApplicativa property.
     * 
     * @return
     *     possible object is
     *     {@link PortaApplicativa }
     *     
     */
    public PortaApplicativa getPortaApplicativa() {
        return this.portaApplicativa;
    }

    /**
     * Sets the value of the portaApplicativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link PortaApplicativa }
     *     
     */
    public void setPortaApplicativa(PortaApplicativa value) {
        this.portaApplicativa = value;
    }

}
