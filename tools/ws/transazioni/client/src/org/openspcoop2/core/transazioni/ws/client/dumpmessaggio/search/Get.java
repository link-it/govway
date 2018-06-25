/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

package org.openspcoop2.core.transazioni.ws.client.dumpmessaggio.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.transazioni.IdDumpMessaggio;


/**
 * <p>Java class for get complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idDumpMessaggio" type="{http://www.openspcoop2.org/core/transazioni}id-dump-messaggio"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get", propOrder = {
    "idDumpMessaggio"
})
public class Get {

    @XmlElement(required = true)
    protected IdDumpMessaggio idDumpMessaggio;

    /**
     * Gets the value of the idDumpMessaggio property.
     * 
     * @return
     *     possible object is
     *     {@link IdDumpMessaggio }
     *     
     */
    public IdDumpMessaggio getIdDumpMessaggio() {
        return this.idDumpMessaggio;
    }

    /**
     * Sets the value of the idDumpMessaggio property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdDumpMessaggio }
     *     
     */
    public void setIdDumpMessaggio(IdDumpMessaggio value) {
        this.idDumpMessaggio = value;
    }

}
