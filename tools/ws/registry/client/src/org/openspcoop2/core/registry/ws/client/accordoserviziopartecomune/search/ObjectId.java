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

package org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for objectId complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="objectId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.openspcoop2.org/core/registry/management}wrapperIdAccordoCooperazione"/>
 *         &lt;element ref="{http://www.openspcoop2.org/core/registry/management}wrapperIdAccordoServizioParteComune"/>
 *         &lt;element ref="{http://www.openspcoop2.org/core/registry/management}wrapperIdPortaDominio"/>
 *         &lt;element ref="{http://www.openspcoop2.org/core/registry/management}wrapperIdSoggetto"/>
 *         &lt;element ref="{http://www.openspcoop2.org/core/registry/management}wrapperIdAccordoServizioParteSpecifica"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectId", propOrder = {
    "wrapperIdAccordoCooperazione",
    "wrapperIdAccordoServizioParteComune",
    "wrapperIdPortaDominio",
    "wrapperIdSoggetto",
    "wrapperIdAccordoServizioParteSpecifica"
})
public class ObjectId {

    protected WrapperIdAccordoCooperazione wrapperIdAccordoCooperazione;
    protected WrapperIdAccordoServizioParteComune wrapperIdAccordoServizioParteComune;
    protected WrapperIdPortaDominio wrapperIdPortaDominio;
    protected WrapperIdSoggetto wrapperIdSoggetto;
    protected WrapperIdAccordoServizioParteSpecifica wrapperIdAccordoServizioParteSpecifica;

    /**
     * Gets the value of the wrapperIdAccordoCooperazione property.
     * 
     * @return
     *     possible object is
     *     {@link WrapperIdAccordoCooperazione }
     *     
     */
    public WrapperIdAccordoCooperazione getWrapperIdAccordoCooperazione() {
        return this.wrapperIdAccordoCooperazione;
    }

    /**
     * Sets the value of the wrapperIdAccordoCooperazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link WrapperIdAccordoCooperazione }
     *     
     */
    public void setWrapperIdAccordoCooperazione(WrapperIdAccordoCooperazione value) {
        this.wrapperIdAccordoCooperazione = value;
    }

    /**
     * Gets the value of the wrapperIdAccordoServizioParteComune property.
     * 
     * @return
     *     possible object is
     *     {@link WrapperIdAccordoServizioParteComune }
     *     
     */
    public WrapperIdAccordoServizioParteComune getWrapperIdAccordoServizioParteComune() {
        return this.wrapperIdAccordoServizioParteComune;
    }

    /**
     * Sets the value of the wrapperIdAccordoServizioParteComune property.
     * 
     * @param value
     *     allowed object is
     *     {@link WrapperIdAccordoServizioParteComune }
     *     
     */
    public void setWrapperIdAccordoServizioParteComune(WrapperIdAccordoServizioParteComune value) {
        this.wrapperIdAccordoServizioParteComune = value;
    }

    /**
     * Gets the value of the wrapperIdPortaDominio property.
     * 
     * @return
     *     possible object is
     *     {@link WrapperIdPortaDominio }
     *     
     */
    public WrapperIdPortaDominio getWrapperIdPortaDominio() {
        return this.wrapperIdPortaDominio;
    }

    /**
     * Sets the value of the wrapperIdPortaDominio property.
     * 
     * @param value
     *     allowed object is
     *     {@link WrapperIdPortaDominio }
     *     
     */
    public void setWrapperIdPortaDominio(WrapperIdPortaDominio value) {
        this.wrapperIdPortaDominio = value;
    }

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
     * Gets the value of the wrapperIdAccordoServizioParteSpecifica property.
     * 
     * @return
     *     possible object is
     *     {@link WrapperIdAccordoServizioParteSpecifica }
     *     
     */
    public WrapperIdAccordoServizioParteSpecifica getWrapperIdAccordoServizioParteSpecifica() {
        return this.wrapperIdAccordoServizioParteSpecifica;
    }

    /**
     * Sets the value of the wrapperIdAccordoServizioParteSpecifica property.
     * 
     * @param value
     *     allowed object is
     *     {@link WrapperIdAccordoServizioParteSpecifica }
     *     
     */
    public void setWrapperIdAccordoServizioParteSpecifica(WrapperIdAccordoServizioParteSpecifica value) {
        this.wrapperIdAccordoServizioParteSpecifica = value;
    }

}
