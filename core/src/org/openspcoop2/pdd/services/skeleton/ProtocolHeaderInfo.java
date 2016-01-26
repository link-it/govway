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



package org.openspcoop2.pdd.services.skeleton;



/**
 * <p>Java class for ProtocolHeaderInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProtocolHeaderInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destinatario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="idCollaborazione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mittente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="riferimentoMessaggio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="servizio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoDestinatario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoMittente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoServizio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

/**
 * Raccoglie informazioni sul protocollo associate ad un IntegrationManagerMessage
 *
 * @author Poli Andrea (apoli@link.it
 * @author Tronci Fabio (tronci@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "ProtocolHeaderInfo", propOrder = {
    "id",
    "azione",
    "destinatario",
    "idCollaborazione",
    "mittente",
    "riferimentoMessaggio",
    "servizio",
    "tipoDestinatario",
    "tipoMittente",
    "tipoServizio"
})

public class ProtocolHeaderInfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8286823907010830471L;
	
    @javax.xml.bind.annotation.XmlElement(name = "ID", required = true, nillable = true)
    protected String id;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String azione;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String destinatario;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String idCollaborazione;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String mittente;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String riferimentoMessaggio;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String servizio;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String tipoDestinatario;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String tipoMittente;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String tipoServizio;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return this.id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
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
     * Gets the value of the destinatario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinatario() {
        return this.destinatario;
    }

    /**
     * Sets the value of the destinatario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinatario(String value) {
        this.destinatario = value;
    }

    /**
     * Gets the value of the idCollaborazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCollaborazione() {
        return this.idCollaborazione;
    }

    /**
     * Sets the value of the idCollaborazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCollaborazione(String value) {
        this.idCollaborazione = value;
    }

    /**
     * Gets the value of the mittente property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMittente() {
        return this.mittente;
    }

    /**
     * Sets the value of the mittente property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMittente(String value) {
        this.mittente = value;
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
     * Gets the value of the servizio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServizio() {
        return this.servizio;
    }

    /**
     * Sets the value of the servizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServizio(String value) {
        this.servizio = value;
    }

    /**
     * Gets the value of the tipoDestinatario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDestinatario() {
        return this.tipoDestinatario;
    }

    /**
     * Sets the value of the tipoDestinatario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDestinatario(String value) {
        this.tipoDestinatario = value;
    }

    /**
     * Gets the value of the tipoMittente property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoMittente() {
        return this.tipoMittente;
    }

    /**
     * Sets the value of the tipoMittente property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoMittente(String value) {
        this.tipoMittente = value;
    }

    /**
     * Gets the value of the tipoServizio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoServizio() {
        return this.tipoServizio;
    }

    /**
     * Sets the value of the tipoServizio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoServizio(String value) {
        this.tipoServizio = value;
    }

}
