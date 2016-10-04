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

package org.openspcoop2.core.diagnostica.ws.client.informazioniprotocollotransazione.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dominio-transazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dominio-transazione">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="soggetto" type="{http://www.openspcoop2.org/core/diagnostica/management}dominio-soggetto" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dominio-transazione", propOrder = {
    "identificativoPorta",
    "soggetto"
})
public class DominioTransazione {

    @XmlElement(name = "identificativo-porta")
    protected String identificativoPorta;
    protected DominioSoggetto soggetto;

    /**
     * Gets the value of the identificativoPorta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoPorta() {
        return this.identificativoPorta;
    }

    /**
     * Sets the value of the identificativoPorta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoPorta(String value) {
        this.identificativoPorta = value;
    }

    /**
     * Gets the value of the soggetto property.
     * 
     * @return
     *     possible object is
     *     {@link DominioSoggetto }
     *     
     */
    public DominioSoggetto getSoggetto() {
        return this.soggetto;
    }

    /**
     * Sets the value of the soggetto property.
     * 
     * @param value
     *     allowed object is
     *     {@link DominioSoggetto }
     *     
     */
    public void setSoggetto(DominioSoggetto value) {
        this.soggetto = value;
    }

}
