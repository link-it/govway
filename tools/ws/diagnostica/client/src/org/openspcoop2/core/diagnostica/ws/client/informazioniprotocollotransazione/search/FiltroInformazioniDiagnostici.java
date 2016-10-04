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

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filtro-informazioni-diagnostici complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filtro-informazioni-diagnostici">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="severita" type="{http://www.openspcoop2.org/core/diagnostica}LivelloDiSeveritaType" minOccurs="0"/>
 *         &lt;element name="modulo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificativo-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filtro-informazioni-diagnostici", propOrder = {
    "codice",
    "messaggio",
    "severita",
    "modulo",
    "identificativoRisposta"
})
public class FiltroInformazioniDiagnostici {

    protected String codice;
    protected String messaggio;
    protected BigInteger severita;
    protected String modulo;
    @XmlElement(name = "identificativo-risposta")
    protected String identificativoRisposta;

    /**
     * Gets the value of the codice property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodice() {
        return this.codice;
    }

    /**
     * Sets the value of the codice property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodice(String value) {
        this.codice = value;
    }

    /**
     * Gets the value of the messaggio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessaggio() {
        return this.messaggio;
    }

    /**
     * Sets the value of the messaggio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessaggio(String value) {
        this.messaggio = value;
    }

    /**
     * Gets the value of the severita property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSeverita() {
        return this.severita;
    }

    /**
     * Sets the value of the severita property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSeverita(BigInteger value) {
        this.severita = value;
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

    /**
     * Gets the value of the identificativoRisposta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoRisposta() {
        return this.identificativoRisposta;
    }

    /**
     * Sets the value of the identificativoRisposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoRisposta(String value) {
        this.identificativoRisposta = value;
    }

}
