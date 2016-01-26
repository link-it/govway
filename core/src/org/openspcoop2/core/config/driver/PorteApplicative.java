/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.core.config.driver;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.config.PortaApplicativa;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="idSoggetto" type="{http://www.openspcoop2.org/core/id}IDSoggetto" minOccurs="0"/>
 *         			 &lt;element name="portaApplicativa" type="{http://www.openspcoop2.org/core/config}PortaApplicativa" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PorteApplicative", propOrder = {
    "entry"
})
public class PorteApplicative {

	@XmlElement(namespace="http://www.openspcoop2.org/core/config/driver")
    protected List<PorteApplicative.Entry> entry;

    /**
     * Gets the value of the entry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PorteApplicative.Entry }
     * 
     * 
     */
    public List<PorteApplicative.Entry> getEntry() {
        if (this.entry == null) {
            this.entry = new ArrayList<PorteApplicative.Entry>();
        }
        return this.entry;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="idSoggetto" type="{http://www.openspcoop2.org/core/id}IDSoggetto" minOccurs="0"/>
     *         &lt;element name="portaApplicativa" type="{http://www.openspcoop2.org/core/config}PortaApplicativa" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "idSoggetto",
        "portaApplicativa"
    })
    public static class Entry {

        protected IDSoggetto idSoggetto;
        protected PortaApplicativa portaApplicativa;

        public Entry() {		}
        
        public Entry(IDSoggetto idSoggetto, PortaApplicativa portaApplicativa) {
        	this.idSoggetto = idSoggetto;
        	this.portaApplicativa = portaApplicativa;
        }
        /**
         * Gets the value of the key property.
         * 
         * @return
         *     possible object is
         *     {@link IDSoggetto }
         *     
         */
        public IDSoggetto getIdSoggetto() {
            return this.idSoggetto;
        }

        /**
         * Sets the value of the key property.
         * 
         * @param idSoggetto
         *     allowed object is
         *     {@link IDSoggetto }
         *     
         */
        public void setIdSoggetto(IDSoggetto idSoggetto) {
            this.idSoggetto = idSoggetto;
        }

        /**
         * Gets the value of the value property.
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
         * Sets the value of the value property.
         * 
         * @param portaApplicativa
         *     allowed object is
         *     {@link PortaApplicativa }
         *     
         */
        public void setPortaApplicativa(PortaApplicativa portaApplicativa) {
            this.portaApplicativa = portaApplicativa;
        }

    }

}
