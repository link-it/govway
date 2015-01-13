/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.sdk.diagnostica;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.jaxb.DateTime2Calendar;

/**
 *
 * Bean Contenente le informazioni relative ai messaggi diagnostici di correlazione
 * 
 * <p>Java class for MsgDiagnosticoCorrelazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MsgDiagnosticoCorrelazione">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="correlazioneApplicativa" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="delegata" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="gdo" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="idBusta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="informazioniBusta" type="{http://logger.dto.openspcoop.org}Busta"/>
 *         &lt;element name="nomePorta" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
 *         &lt;element name="serviziApplicativiList" type="{http://ws.monitor.openspcoop.org}ArrayOf_xsd_string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Stefano Corallo <corallo@link.it>
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MsgDiagnosticoCorrelazione", propOrder = {
    "correlazioneApplicativa",
    "delegata",
    "gdo",
    "id",
    "idBusta",
    "informazioniBusta",
    "nomePorta",
    "properties",
    "serviziApplicativiList"
})

public class MsgDiagnosticoCorrelazione implements Serializable{

	private static final long serialVersionUID = 7616051015989365464L;

	@XmlElement(required = true, nillable = true)
    protected String correlazioneApplicativa;
	protected String correlazioneApplicativaRisposta;
    protected boolean delegata;
    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(DateTime2Calendar.class)
    @XmlSchemaType(name = "dateTime")
    protected Calendar gdo;
    protected long id;
    @XmlElement(required = true, nillable = true)
    protected String idBusta;
    @XmlElement(required = true, nillable = true)
    protected InformazioniProtocollo informazioniProtocollo;
    @XmlElement(required = true, nillable = true)
    protected String nomePorta;
    @XmlJavaTypeAdapter(Properties2Hashtable .class)
    protected Hashtable<String, String> properties;
    @XmlElement(required = true, nillable = true)
    protected List<String> serviziApplicativiList;
    @XmlTransient
    private String protocollo;
    protected IDSoggetto idSoggetto;
	
    public MsgDiagnosticoCorrelazione() {
    	this.properties = new Hashtable<String, String>();
	}
	
	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public InformazioniProtocollo getInformazioniProtocollo() {
		return this.informazioniProtocollo;
	}
	public void setInformazioniProtocollo(InformazioniProtocollo informazioniBusta) {
		this.informazioniProtocollo = informazioniBusta;
	}
	public String getIdBusta() {
		return this.idBusta;
	}
	public void setIdBusta(String idBusta) {
		this.idBusta = idBusta;
	}
	public Calendar getGdo() {
		return this.gdo;
	}
	public void setGdo(Calendar gdo) {
		this.gdo = gdo;
	}
	public String getNomePorta() {
		return this.nomePorta;
	}
	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}
	public boolean isDelegata() {
		return this.delegata;
	}
	public void setDelegata(boolean isDelegata) {
		this.delegata = isDelegata;
	}
	public List<String> getServiziApplicativiList() {
		return this.serviziApplicativiList;
	}
	public void setServiziApplicativiList(List<String> serviziApplicativiList) {
		this.serviziApplicativiList = serviziApplicativiList;
	}
	
	public int sizeServiziApplicativiList(){
		if(this.serviziApplicativiList==null) return 0;
		else return this.serviziApplicativiList.size();
	}
	public String getCorrelazioneApplicativa() {
		return this.correlazioneApplicativa;
	}
	public void setCorrelazioneApplicativa(String correlazioneApplicativa) {
		this.correlazioneApplicativa = correlazioneApplicativa;
	}
	public String getCorrelazioneApplicativaRisposta() {
		return this.correlazioneApplicativaRisposta;
	}
	public void setCorrelazioneApplicativaRisposta(String correlazioneApplicativaRisposta) {
		this.correlazioneApplicativaRisposta = correlazioneApplicativaRisposta;
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

	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public IDSoggetto getIdSoggetto() {
		return this.idSoggetto;
	}

	public void setIdSoggetto(IDSoggetto idSoggetto) {
		this.idSoggetto = idSoggetto;
	}
}


