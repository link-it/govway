/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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



package org.openspcoop2.protocol.sdk;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.utils.jaxb.DateTime2Date;


/**
 * Classe utilizzata per rappresentare una Trasmissione.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo
 * @author $Author$
 * @version $Rev$, $Date$
 */

/**
 * <p>Java class for trasmissione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasmissione">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="destinazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="indirizzoTelematicoDestinazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="indirizzoTelematicoOrigine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oraRegistrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="origine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tempo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoDestinazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoOrigine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trasmissione", propOrder = {
    "destinazione",
    "indirizzoTelematicoDestinazione",
    "indirizzoTelematicoOrigine",
    "oraRegistrazione",
    "origine",
    "tempo",
    "tipoDestinazione",
    "tipoOrigine"
})

public class Trasmissione implements java.io.Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	// origine
	protected String origine;
	protected String tipoOrigine;
	protected String identificativoPortaOrigine;
	protected String indirizzoOrigine;
	 
	// destinazione
	protected String destinazione;
	protected String tipoDestinazione;
    protected String identificativoPortaDestinazione;
    protected String indirizzoDestinazione;
    
    // date
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(DateTime2Date .class)
    @XmlSchemaType(name = "dateTime")
    protected Date oraRegistrazione;
   
    @XmlTransient
    private TipoOraRegistrazione tempo;
    @XmlElement(name="tempo")
    protected String tempoValue;
        
    public Trasmissione() {}
    
    
    /**
     * Gets the value of the destinazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinazione() {
        return this.destinazione;
    }

    /**
     * Sets the value of the destinazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinazione(String value) {
        this.destinazione = value;
    }

    /**
     * Gets the value of the indirizzoDestinazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndirizzoDestinazione() {
        return this.indirizzoDestinazione;
    }

    /**
     * Sets the value of the indirizzoDestinazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndirizzoDestinazione(String value) {
        this.indirizzoDestinazione = value;
    }

    /**
     * Gets the value of the indirizzoOrigine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndirizzoOrigine() {
        return this.indirizzoOrigine;
    }

    /**
     * Sets the value of the indirizzoOrigine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndirizzoOrigine(String value) {
        this.indirizzoOrigine = value;
    }

    /**
     * Gets the value of the oraRegistrazione property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public Date getOraRegistrazione() {
        return this.oraRegistrazione;
    }
    
    /**
     * Sets the value of the oraRegistrazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOraRegistrazione(Date value) {
        this.oraRegistrazione = value;
    }
    /**
     * Gets the value of the origine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrigine() {
        return this.origine;
    }

    /**
     * Sets the value of the origine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrigine(String value) {
        this.origine = value;
    }

    /**
     * Gets the value of the tempo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public TipoOraRegistrazione getTempo() {
        return this.tempo;
    }

    /**
     * Sets the value of the tempo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTempo(TipoOraRegistrazione value) {
        this.tempo = value;
    }

    /**
     * Gets the value of the tipoDestinazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDestinazione() {
        return this.tipoDestinazione;
    }

    /**
     * Sets the value of the tipoDestinazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDestinazione(String value) {
        this.tipoDestinazione = value;
    }

    /**
     * Gets the value of the tipoOrigine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoOrigine() {
        return this.tipoOrigine;
    }

    /**
     * Sets the value of the tipoOrigine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoOrigine(String value) {
        this.tipoOrigine = value;
    }

	public String getTempoValue(IProtocolFactory protocolFactory) throws ProtocolException {
		return this.tempoValue == null ? protocolFactory.createTraduttore().toString(this.tempo) : this.tempoValue;
	}

	public void setTempoValue(String tempoValue) {
		this.tempoValue = tempoValue;
	}
	
	public String getIdentificativoPortaOrigine() {
		return this.identificativoPortaOrigine;
	}
	public void setIdentificativoPortaOrigine(String identificativoPortaOrigine) {
		this.identificativoPortaOrigine = identificativoPortaOrigine;
	}
	public String getIdentificativoPortaDestinazione() {
		return this.identificativoPortaDestinazione;
	}
	public void setIdentificativoPortaDestinazione(
			String identificativoPortaDestinazione) {
		this.identificativoPortaDestinazione = identificativoPortaDestinazione;
	}

	@Override
	public Trasmissione clone(){
		
		Trasmissione clone = new Trasmissione();
		
		// origine
		clone.setOrigine(this.origine!=null ? new String(this.origine) : null);
		clone.setTipoOrigine(this.tipoOrigine!=null ? new String(this.tipoOrigine) : null);
		clone.setIndirizzoOrigine(this.indirizzoOrigine!=null ? new String(this.indirizzoOrigine) : null);
		clone.setIdentificativoPortaOrigine(this.identificativoPortaOrigine!=null ? new String(this.identificativoPortaOrigine) : null);
		
		// destinazione
		clone.setDestinazione(this.destinazione!=null ? new String(this.destinazione) : null);
		clone.setTipoDestinazione(this.tipoDestinazione!=null ? new String(this.tipoDestinazione) : null);
		clone.setIndirizzoDestinazione(this.indirizzoDestinazione!=null ? new String(this.indirizzoDestinazione) : null);
		clone.setIdentificativoPortaDestinazione(this.identificativoPortaDestinazione!=null ? new String(this.identificativoPortaDestinazione) : null);

		// date
		clone.setOraRegistrazione(this.oraRegistrazione!=null ? new Date(this.oraRegistrazione.getTime()) : null);
		clone.setTempo(this.tempo);
		clone.setTempoValue(this.tempoValue!=null ? new String(this.tempoValue) : null);

		return clone;
	}

}





