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


package org.openspcoop2.protocol.sdk.tracciamento;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.SOAPElement;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.Allegato;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
import org.openspcoop2.utils.jaxb.DateTime2Date;

/**
 * Bean Contenente le informazioni relative alle tracce
 * 
 * <p>Java class for traccia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="traccia">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="busta" type="{http://sdk.protocol.openspcoop2.org}busta" minOccurs="0"/>
 *         &lt;element name="correlazioneApplicativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gdo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="idSoggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="properties">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="servizioApplicativoFruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoMessaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Stefano Corallo <corallo@link.it>
 * @author Nardi Lorenzo
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "traccia", propOrder = {
    "busta",
    "correlazioneApplicativa",
    "gdo",
    "idSoggetto",
    "location",
    "properties",
    "servizioApplicativoFruitore",
    "tipoMessaggio"
})

public class Traccia implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	
	// data
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(DateTime2Date .class)
    @XmlSchemaType(name = "dateTime")
    protected Date gdo;
	
    // dominio
    protected IDSoggetto idSoggetto;
    @XmlTransient
	private TipoPdD tipoPdD;
    
    // tipoTraccia
    protected TipoTraccia tipoMessaggio;
    
	// Busta
	protected Busta busta;
	@XmlTransient
	private byte[] bustaInByte;
	@XmlTransient
    private SOAPElement bustaInDom;
	@XmlTransient
	private String bustaAsString;
	
	// correlazione
    protected String correlazioneApplicativa;
    protected String correlazioneApplicativaRisposta;
    
    // location
    protected String location;
    
    // properties
    @XmlJavaTypeAdapter(Properties2Hashtable .class)
    protected Hashtable<String, String> properties;
    
    // protocollo
    @XmlTransient
    private String protocollo;
    
    // esito		
    @XmlTransient
    private EsitoElaborazioneMessaggioTracciato esitoElaborazioneMessaggioTracciato;
    
    // allegati
    @XmlElement(nillable = true)
    protected List<Allegato> listaAllegati;
	
    
    public Traccia() {
    	this.properties = new Hashtable<String, String>();
    	this.listaAllegati = new ArrayList<Allegato>();
	}
    /**
     * Gets the value of the busta property.
     * 
     * @return
     *     possible object is
     *     {@link Busta }
     *     
     */
    public Busta getBusta() {
        return this.busta;
    }

    /**
     * Sets the value of the busta property.
     * 
     * @param value
     *     allowed object is
     *     {@link Busta }
     *     
     */
    public void setBusta(Busta value) {
        this.busta = value;
    }

    /**
     * Gets the value of the correlazioneApplicativa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrelazioneApplicativa() {
        return this.correlazioneApplicativa;
    }

    /**
     * Sets the value of the correlazioneApplicativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrelazioneApplicativa(String value) {
        this.correlazioneApplicativa = value;
    }
    
    /**
     * Gets the value of the correlazioneApplicativaRisposta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrelazioneApplicativaRisposta() {
        return this.correlazioneApplicativaRisposta;
    }

    
    /**
     * Sets the value of the correlazioneApplicativaRisposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrelazioneApplicativaRisposta(String value) {
        this.correlazioneApplicativaRisposta = value;
    }
    
    /**
     * Gets the value of the gdo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public Date getGdo() {
        return this.gdo;
    }
    
    /**
     * Sets the value of the gdo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGdo(Date value) {
        this.gdo = value;
    }

    /**
     * Gets the value of the idporta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public IDSoggetto getIdSoggetto() {
        return this.idSoggetto;
    }

    /**
     * Sets the value of the idporta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdSoggetto(IDSoggetto value) {
        this.idSoggetto = value;
    }

	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
    
    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Gets the value of the tipoMessaggio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public TipoTraccia getTipoMessaggio() {
        return this.tipoMessaggio;
    }

    /**
     * Sets the value of the tipoMessaggio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoMessaggio(TipoTraccia value) {
        this.tipoMessaggio = value;
    }

    public void addProperty(String key,String value){
    	this.properties.put(key,value);
    }
    
    public int sizeProperties(){
    	return this.properties.size();
    }

    public String getProperty(String key){
    	return this.properties.get(key);
    }
    
    public String removeProperty(String key){
    	return this.properties.remove(key);
    }
    
    public String[] getPropertiesValues() {
    	return this.properties.values().toArray(new String[this.properties.size()]);
    }
    
    public String[] getPropertiesNames() {
    	return this.properties.keySet().toArray(new String[this.properties.size()]);
    }
    
    public void setProperties(Hashtable<String, String> params) {
    	this.properties = params;
    }
    
    public Hashtable<String, String> getProperties() {
    	return this.properties;
    }
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	public byte[] getBustaAsByteArray() {
		return this.bustaInByte;
	}
	public void setBustaAsByteArray(byte[] bustaInByte) {
		this.bustaInByte = bustaInByte;
	}
	public SOAPElement getBustaAsElement() {
		return this.bustaInDom;
	}
	public void setBustaAsElement(SOAPElement bustaInDom) {
		this.bustaInDom = bustaInDom;
	}
	public TipoPdD getTipoPdD() {
		return this.tipoPdD;
	}
	public void setTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}
	public EsitoElaborazioneMessaggioTracciato getEsitoElaborazioneMessaggioTracciato() {
		return this.esitoElaborazioneMessaggioTracciato;
	}
	public void setEsitoElaborazioneMessaggioTracciato(
			EsitoElaborazioneMessaggioTracciato esitoElaborazioneMessaggioTracciato) {
		this.esitoElaborazioneMessaggioTracciato = esitoElaborazioneMessaggioTracciato;
	}
	
	public List<Allegato> getListaAllegati() {
        if (this.listaAllegati == null) {
            this.listaAllegati = new ArrayList<Allegato>();
        }
        return this.listaAllegati;
    }
	public int sizeListaAllegati() {
		return this.listaAllegati.size();
	}
	public void addAllegato(Allegato a) {
		this.listaAllegati.add(a);
	}
	public Allegato getAllegato(int index) {
		return this.listaAllegati.get( index );
	}
	public Allegato removeAllegato(int index) {
		return this.listaAllegati.remove(index);
	}
	public String getBustaAsString() {
		return this.bustaAsString;
	}
	public void setBustaAsString(String bustaAsString) {
		this.bustaAsString = bustaAsString;
	}

	protected void setListaAllegati(List<Allegato> listaAllegati) {
		this.listaAllegati = listaAllegati;
	}
	
	@Override
	public Traccia clone(){
		Traccia clone = new Traccia();
		
		// data
		clone.setGdo(this.gdo!=null ? new Date(this.gdo.getTime()) : null);
		
		// dominio
		clone.setIdSoggetto(this.idSoggetto!=null ? this.idSoggetto.clone() : null);
		clone.setTipoPdD(this.tipoPdD);
		
	    // tipoTraccia
		clone.setTipoMessaggio(this.tipoMessaggio);
		
		// busta
		clone.setBusta(this.busta!=null ? this.busta.clone() : null);
		ByteArrayOutputStream bout = null;
		if(this.bustaInByte!=null){
			bout = new ByteArrayOutputStream();
			try{
				bout.write(this.bustaInByte);
				bout.flush();
				bout.close();
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
			clone.setBustaAsByteArray(bout.toByteArray());
		}
		clone.setBustaAsElement(this.bustaInDom); // non clonato, vedere se si trova un modo efficente se serve
		clone.setBustaAsString(this.bustaAsString!=null ? new String(this.bustaAsString) : null);
		
		// correlazione
		clone.setCorrelazioneApplicativa(this.correlazioneApplicativa!=null ? new String(this.correlazioneApplicativa) : null);
		clone.setCorrelazioneApplicativaRisposta(this.correlazioneApplicativaRisposta!=null ? new String(this.correlazioneApplicativaRisposta) : null);

		// location
		clone.setLocation(this.location!=null ? new String(this.location) : null);
		
		// properties
		if(this.properties!=null && this.properties.size()>0){
			Enumeration<String> keys = this.properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String value = this.properties.get(key);
				if(key!=null && value!=null){
					clone.addProperty(new String(key), new String(value));
				}
			}
		}
		
		// protocollo
		clone.setProtocollo(this.protocollo!=null ? new String(this.protocollo) : null);
		
		// esito
		clone.setEsitoElaborazioneMessaggioTracciato(this.esitoElaborazioneMessaggioTracciato!=null ? this.esitoElaborazioneMessaggioTracciato.clone() : null);
		
		// ListaAllegati
		for(int i=0; i<this.sizeListaAllegati(); i++){
			clone.addAllegato(this.getAllegato(i).clone());
		}
		
		return clone;
	}
}
