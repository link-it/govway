/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.core.diagnostica.ws.client.messaggiodiagnostico.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;


/**
 * <p>Java class for findResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="messaggioDiagnostico" type="{http://www.openspcoop2.org/core/diagnostica}messaggio-diagnostico"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findResponse", propOrder = {
    "messaggioDiagnostico"
})
public class FindResponse {

    @XmlElement(required = true)
    protected MessaggioDiagnostico messaggioDiagnostico;

    /**
     * Gets the value of the messaggioDiagnostico property.
     * 
     * @return
     *     possible object is
     *     {@link MessaggioDiagnostico }
     *     
     */
    public MessaggioDiagnostico getMessaggioDiagnostico() {
        return this.messaggioDiagnostico;
    }

    /**
     * Sets the value of the messaggioDiagnostico property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessaggioDiagnostico }
     *     
     */
    public void setMessaggioDiagnostico(MessaggioDiagnostico value) {
        this.messaggioDiagnostico = value;
    }

}
