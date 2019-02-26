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

package org.openspcoop2.core.config.ws.client.portaapplicativa.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.PortaApplicativa;


/**
 * <p>Java class for getResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="portaApplicativa" type="{http://www.openspcoop2.org/core/config}porta-applicativa"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getResponse", propOrder = {
    "portaApplicativa"
})
public class GetResponse {

    @XmlElement(required = true)
    protected PortaApplicativa portaApplicativa;

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
