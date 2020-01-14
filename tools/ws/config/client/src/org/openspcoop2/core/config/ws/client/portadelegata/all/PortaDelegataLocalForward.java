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

package org.openspcoop2.core.config.ws.client.portadelegata.all;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;


/**
 * <p>Java class for porta-delegata-local-forward complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-delegata-local-forward"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0"/&gt;
 *         &lt;element name="porta-applicativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-delegata-local-forward", propOrder = {
    "stato",
    "portaApplicativa"
})
public class PortaDelegataLocalForward {

    @XmlSchemaType(name = "string")
    protected StatoFunzionalita stato;
    @XmlElement(name = "porta-applicativa")
    protected String portaApplicativa;

    /**
     * Gets the value of the stato property.
     * 
     * @return
     *     possible object is
     *     {@link StatoFunzionalita }
     *     
     */
    public StatoFunzionalita getStato() {
        return this.stato;
    }

    /**
     * Sets the value of the stato property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatoFunzionalita }
     *     
     */
    public void setStato(StatoFunzionalita value) {
        this.stato = value;
    }

    /**
     * Gets the value of the portaApplicativa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortaApplicativa() {
        return this.portaApplicativa;
    }

    /**
     * Sets the value of the portaApplicativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortaApplicativa(String value) {
        this.portaApplicativa = value;
    }

}
