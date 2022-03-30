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

package org.openspcoop2.core.config.ws.client.soggetto.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for objectId complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="objectId"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element ref="{http://www.openspcoop2.org/core/config/management}wrapperIdSoggetto"/&gt;
 *         &lt;element ref="{http://www.openspcoop2.org/core/config/management}wrapperIdPortaDelegata"/&gt;
 *         &lt;element ref="{http://www.openspcoop2.org/core/config/management}wrapperIdPortaApplicativa"/&gt;
 *         &lt;element ref="{http://www.openspcoop2.org/core/config/management}wrapperIdServizioApplicativo"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectId", propOrder = {
    "wrapperIdSoggetto",
    "wrapperIdPortaDelegata",
    "wrapperIdPortaApplicativa",
    "wrapperIdServizioApplicativo"
})
public class ObjectId {

    protected WrapperIdSoggetto wrapperIdSoggetto;
    protected WrapperIdPortaDelegata wrapperIdPortaDelegata;
    protected WrapperIdPortaApplicativa wrapperIdPortaApplicativa;
    protected WrapperIdServizioApplicativo wrapperIdServizioApplicativo;

    /**
     * Gets the value of the wrapperIdSoggetto property.
     * 
     * @return
     *     possible object is
     *     {@link WrapperIdSoggetto }
     *     
     */
    public WrapperIdSoggetto getWrapperIdSoggetto() {
        return this.wrapperIdSoggetto;
    }

    /**
     * Sets the value of the wrapperIdSoggetto property.
     * 
     * @param value
     *     allowed object is
     *     {@link WrapperIdSoggetto }
     *     
     */
    public void setWrapperIdSoggetto(WrapperIdSoggetto value) {
        this.wrapperIdSoggetto = value;
    }

    /**
     * Gets the value of the wrapperIdPortaDelegata property.
     * 
     * @return
     *     possible object is
     *     {@link WrapperIdPortaDelegata }
     *     
     */
    public WrapperIdPortaDelegata getWrapperIdPortaDelegata() {
        return this.wrapperIdPortaDelegata;
    }

    /**
     * Sets the value of the wrapperIdPortaDelegata property.
     * 
     * @param value
     *     allowed object is
     *     {@link WrapperIdPortaDelegata }
     *     
     */
    public void setWrapperIdPortaDelegata(WrapperIdPortaDelegata value) {
        this.wrapperIdPortaDelegata = value;
    }

    /**
     * Gets the value of the wrapperIdPortaApplicativa property.
     * 
     * @return
     *     possible object is
     *     {@link WrapperIdPortaApplicativa }
     *     
     */
    public WrapperIdPortaApplicativa getWrapperIdPortaApplicativa() {
        return this.wrapperIdPortaApplicativa;
    }

    /**
     * Sets the value of the wrapperIdPortaApplicativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link WrapperIdPortaApplicativa }
     *     
     */
    public void setWrapperIdPortaApplicativa(WrapperIdPortaApplicativa value) {
        this.wrapperIdPortaApplicativa = value;
    }

    /**
     * Gets the value of the wrapperIdServizioApplicativo property.
     * 
     * @return
     *     possible object is
     *     {@link WrapperIdServizioApplicativo }
     *     
     */
    public WrapperIdServizioApplicativo getWrapperIdServizioApplicativo() {
        return this.wrapperIdServizioApplicativo;
    }

    /**
     * Sets the value of the wrapperIdServizioApplicativo property.
     * 
     * @param value
     *     allowed object is
     *     {@link WrapperIdServizioApplicativo }
     *     
     */
    public void setWrapperIdServizioApplicativo(WrapperIdServizioApplicativo value) {
        this.wrapperIdServizioApplicativo = value;
    }

}
