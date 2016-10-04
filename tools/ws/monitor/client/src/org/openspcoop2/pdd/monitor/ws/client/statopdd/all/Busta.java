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

package org.openspcoop2.pdd.monitor.ws.client.statopdd.all;

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
 *         &lt;element name="attesa-riscontro" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="mittente" type="{http://www.openspcoop2.org/pdd/monitor/management}busta-soggetto" minOccurs="0"/>
 *         &lt;element name="destinatario" type="{http://www.openspcoop2.org/pdd/monitor/management}busta-soggetto" minOccurs="0"/>
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/pdd/monitor/management}busta-servizio" minOccurs="0"/>
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profilo-collaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="collaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="riferimento-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "attesaRiscontro",
    "mittente",
    "destinatario",
    "servizio",
    "azione",
    "profiloCollaborazione",
    "collaborazione",
    "riferimentoMessaggio"
})
public class Busta {

    @XmlElement(name = "attesa-riscontro")
    protected Boolean attesaRiscontro;
    protected BustaSoggetto mittente;
    protected BustaSoggetto destinatario;
    protected BustaServizio servizio;
    protected String azione;
    @XmlElement(name = "profilo-collaborazione")
    protected String profiloCollaborazione;
    protected String collaborazione;
    @XmlElement(name = "riferimento-messaggio")
    protected String riferimentoMessaggio;

    /**
     * Gets the value of the attesaRiscontro property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAttesaRiscontro() {
        return this.attesaRiscontro;
    }

    /**
     * Sets the value of the attesaRiscontro property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAttesaRiscontro(Boolean value) {
        this.attesaRiscontro = value;
    }

    /**
     * Gets the value of the mittente property.
     * 
     * @return
     *     possible object is
     *     {@link BustaSoggetto }
     *     
     */
    public BustaSoggetto getMittente() {
        return this.mittente;
    }

    /**
     * Sets the value of the mittente property.
     * 
     * @param value
     *     allowed object is
     *     {@link BustaSoggetto }
     *     
     */
    public void setMittente(BustaSoggetto value) {
        this.mittente = value;
    }

    /**
     * Gets the value of the destinatario property.
     * 
     * @return
     *     possible object is
     *     {@link BustaSoggetto }
     *     
     */
    public BustaSoggetto getDestinatario() {
        return this.destinatario;
    }

    /**
     * Sets the value of the destinatario property.
     * 
     * @param value
     *     allowed object is
     *     {@link BustaSoggetto }
     *     
     */
    public void setDestinatario(BustaSoggetto value) {
        this.destinatario = value;
    }

    /**
     * Gets the value of the servizio property.
     * 
     * @return
     *     possible object is
     *     {@link BustaServizio }
     *     
     */
    public BustaServizio getServizio() {
        return this.servizio;
    }

    /**
     * Sets the value of the servizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link BustaServizio }
     *     
     */
    public void setServizio(BustaServizio value) {
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
     * Gets the value of the profiloCollaborazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfiloCollaborazione() {
        return this.profiloCollaborazione;
    }

    /**
     * Sets the value of the profiloCollaborazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfiloCollaborazione(String value) {
        this.profiloCollaborazione = value;
    }

    /**
     * Gets the value of the collaborazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCollaborazione() {
        return this.collaborazione;
    }

    /**
     * Sets the value of the collaborazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCollaborazione(String value) {
        this.collaborazione = value;
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

}
