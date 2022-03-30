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


/**
 * <p>Java class for dominio-diagnostico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dominio-diagnostico"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="soggetto" type="{http://www.openspcoop2.org/core/diagnostica/management}dominio-soggetto" minOccurs="0"/&gt;
 *         &lt;element name="modulo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dominio-diagnostico", propOrder = {
    "identificativoPorta",
    "soggetto",
    "modulo"
})
public class DominioDiagnostico {

    @XmlElement(name = "identificativo-porta")
    protected String identificativoPorta;
    protected DominioSoggetto soggetto;
    protected String modulo;

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

    /**
     * Gets the value of the modulo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModulo() {
        return this.modulo;
    }

    /**
     * Sets the value of the modulo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModulo(String value) {
        this.modulo = value;
    }

}
