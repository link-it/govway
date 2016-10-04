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

package org.openspcoop2.core.tracciamento.ws.client.traccia.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for busta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="busta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mittente" type="{http://www.openspcoop2.org/core/tracciamento/management}soggetto" minOccurs="0"/>
 *         &lt;element name="destinatario" type="{http://www.openspcoop2.org/core/tracciamento/management}soggetto" minOccurs="0"/>
 *         &lt;element name="profilo-collaborazione" type="{http://www.openspcoop2.org/core/tracciamento/management}profilo-collaborazione" minOccurs="0"/>
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/core/tracciamento/management}servizio" minOccurs="0"/>
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="riferimento-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="servizio-applicativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protocollo" type="{http://www.openspcoop2.org/core/tracciamento/management}protocollo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "busta", propOrder = {
    "mittente",
    "destinatario",
    "profiloCollaborazione",
    "servizio",
    "azione",
    "identificativo",
    "riferimentoMessaggio",
    "servizioApplicativoFruitore",
    "servizioApplicativoErogatore",
    "protocollo"
})
public class Busta {

    protected Soggetto mittente;
    protected Soggetto destinatario;
    @XmlElement(name = "profilo-collaborazione")
    protected ProfiloCollaborazione profiloCollaborazione;
    protected Servizio servizio;
    protected String azione;
    protected String identificativo;
    @XmlElement(name = "riferimento-messaggio")
    protected String riferimentoMessaggio;
    @XmlElement(name = "servizio-applicativo-fruitore")
    protected String servizioApplicativoFruitore;
    @XmlElement(name = "servizio-applicativo-erogatore")
    protected String servizioApplicativoErogatore;
    protected Protocollo protocollo;

    /**
     * Gets the value of the mittente property.
     * 
     * @return
     *     possible object is
     *     {@link Soggetto }
     *     
     */
    public Soggetto getMittente() {
        return this.mittente;
    }

    /**
     * Sets the value of the mittente property.
     * 
     * @param value
     *     allowed object is
     *     {@link Soggetto }
     *     
     */
    public void setMittente(Soggetto value) {
        this.mittente = value;
    }

    /**
     * Gets the value of the destinatario property.
     * 
     * @return
     *     possible object is
     *     {@link Soggetto }
     *     
     */
    public Soggetto getDestinatario() {
        return this.destinatario;
    }

    /**
     * Sets the value of the destinatario property.
     * 
     * @param value
     *     allowed object is
     *     {@link Soggetto }
     *     
     */
    public void setDestinatario(Soggetto value) {
        this.destinatario = value;
    }

    /**
     * Gets the value of the profiloCollaborazione property.
     * 
     * @return
     *     possible object is
     *     {@link ProfiloCollaborazione }
     *     
     */
    public ProfiloCollaborazione getProfiloCollaborazione() {
        return this.profiloCollaborazione;
    }

    /**
     * Sets the value of the profiloCollaborazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfiloCollaborazione }
     *     
     */
    public void setProfiloCollaborazione(ProfiloCollaborazione value) {
        this.profiloCollaborazione = value;
    }

    /**
     * Gets the value of the servizio property.
     * 
     * @return
     *     possible object is
     *     {@link Servizio }
     *     
     */
    public Servizio getServizio() {
        return this.servizio;
    }

    /**
     * Sets the value of the servizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Servizio }
     *     
     */
    public void setServizio(Servizio value) {
        this.servizio = value;
    }

    /**
     * Gets the value of the azione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAzione() {
        return this.azione;
    }

    /**
     * Sets the value of the azione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAzione(String value) {
        this.azione = value;
    }

    /**
     * Gets the value of the identificativo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativo() {
        return this.identificativo;
    }

    /**
     * Sets the value of the identificativo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativo(String value) {
        this.identificativo = value;
    }

    /**
     * Gets the value of the riferimentoMessaggio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiferimentoMessaggio() {
        return this.riferimentoMessaggio;
    }

    /**
     * Sets the value of the riferimentoMessaggio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiferimentoMessaggio(String value) {
        this.riferimentoMessaggio = value;
    }

    /**
     * Gets the value of the servizioApplicativoFruitore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServizioApplicativoFruitore() {
        return this.servizioApplicativoFruitore;
    }

    /**
     * Sets the value of the servizioApplicativoFruitore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServizioApplicativoFruitore(String value) {
        this.servizioApplicativoFruitore = value;
    }

    /**
     * Gets the value of the servizioApplicativoErogatore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServizioApplicativoErogatore() {
        return this.servizioApplicativoErogatore;
    }

    /**
     * Sets the value of the servizioApplicativoErogatore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServizioApplicativoErogatore(String value) {
        this.servizioApplicativoErogatore = value;
    }

    /**
     * Gets the value of the protocollo property.
     * 
     * @return
     *     possible object is
     *     {@link Protocollo }
     *     
     */
    public Protocollo getProtocollo() {
        return this.protocollo;
    }

    /**
     * Sets the value of the protocollo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Protocollo }
     *     
     */
    public void setProtocollo(Protocollo value) {
        this.protocollo = value;
    }

}
