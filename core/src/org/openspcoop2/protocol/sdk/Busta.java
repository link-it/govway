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



package org.openspcoop2.protocol.sdk;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jaxb.DateTime2Date;

/**
 * Classe utilizzata per rappresentare una Busta.
 *
 * <p>Java class for busta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="busta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="azioneRichiedenteBustaDiServizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="collaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="confermaRicezione" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="destinatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificativoPortaDestinatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="indirizzoTelematicoDestinatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="indirizzoTelematicoMittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="inoltro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="listaEccezioni" type="{http://www.openspcoop2.org/engine/busta}eccezione" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="listaRiscontri" type="{http://www.openspcoop2.org/engine/busta}riscontro" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="listaTrasmissioni" type="{http://www.openspcoop2.org/engine/busta}trasmissione" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificativoPortaMittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oraRegistrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="profiloDiCollaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="riferimentoMessaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="riferimentoMsgBustaRichiedenteServizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scadenza" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="sequenza" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="versioneServizio" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="servizioCorrelato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="servizioRichiedenteBustaDiServizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoDestinatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoMittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoOraRegistrazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoServizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoServizioCorrelato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoServizioRichiedenteBustaDiServizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "busta", propOrder = {
		"azione",
		"azioneRichiedenteBustaDiServizio",
		"collaborazione",
		"confermaRicezione",
		"destinatario",
		"identificativoPortaDestinatario",
		"id",
		"indirizzoTelematicoDestinatario",
		"indirizzoTelematicoMittente",
		"inoltro",
		"listaEccezioni",
		"listaRiscontri",
		"listaTrasmissioni",
		"mittente",
		"identificativoPortaMittente",
		"oraRegistrazione",
		"profiloDiCollaborazione",
		"riferimentoMessaggio",
		"riferimentoMsgBustaRichiedenteServizio",
		"scadenza",
		"sequenza",
		"servizio",
		"versioneServizio",
		"servizioCorrelato",
		"servizioRichiedenteBustaDiServizio",
		"tipoDestinatario",
		"tipoMittente",
		"tipoOraRegistrazione",
		"tipoServizio",
		"tipoServizioCorrelato",
		"tipoServizioRichiedenteBustaDiServizio"
})
public class Busta implements java.io.Serializable { 

	private static final long serialVersionUID = 1L;


	// mittente
	protected String tipoMittente;
	protected String mittente;
	protected String identificativoPortaMittente;
	protected String indirizzoMittente;

	// destinatario
	protected String tipoDestinatario;
	protected String destinatario;
	protected String identificativoPortaDestinatario;
	protected String indirizzoDestinatario;

	// profilo di collaborazione
	@XmlTransient
	private ProfiloDiCollaborazione profiloDiCollaborazione;
	@XmlElement(name="profiloDiCollaborazione")
	protected String profiloDiCollaborazioneValue;
	protected String tipoServizioCorrelato;
	protected String servizioCorrelato;
	
	// collaborazione
	protected String collaborazione;

	// servizio
	protected String tipoServizio;
	protected String servizio;
	protected int versioneServizio = 1;
	protected String tipoServizioRichiedenteBustaDiServizio;
	protected String servizioRichiedenteBustaDiServizio;
	
	// azione
	protected String azione;
	protected String azioneRichiedenteBustaDiServizio;
	
	// identificativi
	@XmlElement(name = "ID")
	protected String id;
	protected String riferimentoMessaggio;
	protected String riferimentoMsgBustaRichiedenteServizio;
	
	// date
	@XmlTransient
	private TipoOraRegistrazione tipoOraRegistrazione;
	@XmlElement(name = "tipoOraRegistrazione")
	protected String tipoOraRegistrazioneValue;
	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(DateTime2Date .class)
	@XmlSchemaType(name = "dateTime")
	protected Date oraRegistrazione;
	@XmlElement(type = String.class)
	@XmlJavaTypeAdapter(DateTime2Date .class)
	@XmlSchemaType(name = "dateTime")
	protected Date scadenza;
	
	// profilo di trasmissione
	protected boolean confermaRicezione;
	@XmlTransient
	private Inoltro inoltro;
	@XmlElement
	protected String inoltroValue;

	// sequenza
	protected long sequenza = -1;

	// servizi applicativi
	private String servizioApplicativoFruitore;
	private String servizioApplicativoErogatore;

	// protocollo
	private String protocollo;
	
	// digest
	private String digest;
	
	// properties
	@XmlJavaTypeAdapter(Properties2Hashtable .class)
	protected Hashtable<String, String> properties;
	
	// ListaEccezioni
	@XmlElement(nillable = true)
	protected List<Eccezione> listaEccezioni;
	
	// ListaRiscontri
	@XmlElement(nillable = true)
	protected List<Riscontro> listaRiscontri;
	
	// ListaTrasmissioni
	@XmlElement(nillable = true)
	protected List<Trasmissione> listaTrasmissioni;
	


	public Busta(String protocollo){
		this.listaEccezioni = new ArrayList<Eccezione>();
		this.listaTrasmissioni = new ArrayList<Trasmissione>();
		this.listaRiscontri = new ArrayList<Riscontro>();
		this.sequenza = -1;
		this.properties = new Hashtable<String, String>();
		this.protocollo = protocollo;
	}

	public Busta(String protocollo,Servizio infoServizio, IDSoggetto mittente, IDSoggetto destinatario, String id) {
		this.listaEccezioni = new ArrayList<Eccezione>();
		this.listaTrasmissioni = new ArrayList<Trasmissione>();
		this.listaRiscontri = new ArrayList<Riscontro>();
		this.sequenza = -1;
		
		if(infoServizio!=null){
			this.profiloDiCollaborazione = infoServizio.getProfiloDiCollaborazione();
			this.confermaRicezione = infoServizio.getConfermaRicezione();
			this.scadenza = infoServizio.getScadenza();
			if(infoServizio.getIDServizio()!=null){
				this.tipoServizio = infoServizio.getIDServizio().getTipoServizio();
				this.servizio = infoServizio.getIDServizio().getServizio();
				this.azione = infoServizio.getIDServizio().getAzione();
			}
			this.tipoServizioCorrelato = infoServizio.getTipoServizioCorrelato();
			this.servizioCorrelato = infoServizio.getServizioCorrelato();
		}
		
		if(mittente!=null){
			this.tipoMittente = mittente.getTipo();
			this.mittente = mittente.getNome();
			this.identificativoPortaMittente = mittente.getCodicePorta();
		}
		
		if(destinatario!=null){
			this.tipoDestinatario = destinatario.getTipo();
			this.destinatario = destinatario.getNome();
			this.identificativoPortaDestinatario = destinatario.getCodicePorta();
		}
		
		this.id = id;
		this.properties = new Hashtable<String, String>();
		this.protocollo = protocollo;
	}

	
	
	protected void setTipoOraRegistrazione(TipoOraRegistrazione tipoOraRegistrazione) {
		this.tipoOraRegistrazione = tipoOraRegistrazione;
	}

	protected void setInoltro(Inoltro inoltro) {
		this.inoltro = inoltro;
	}

	public void addProperty(String key,String value){
		// Per evitare nullPointer durante la serializzazione
		// Non deve essere inserito nemmeno il valore ""
		if(value!=null && !"".equals(value)){
			this.properties.put(key,value);
		}
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
	
	public List<Map.Entry<String, String>> getPropertiesAsList(){
		List <Map.Entry<String, String>> toRet = new ArrayList<Map.Entry<String, String>>();
		toRet.addAll(this.properties.entrySet());
		
		return toRet;
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
	 * Gets the value of the azioneRichiedenteBustaDiServizio property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getAzioneRichiedenteBustaDiServizio() {
		return this.azioneRichiedenteBustaDiServizio;
	}

	/**
	 * Sets the value of the azioneRichiedenteBustaDiServizio property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setAzioneRichiedenteBustaDiServizio(String value) {
		this.azioneRichiedenteBustaDiServizio = value;
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
	 * Gets the value of the confermaRicezione property.
	 * 
	 */
	public boolean isConfermaRicezione() {
		return this.confermaRicezione;
	}

	/**
	 * Sets the value of the confermaRicezione property.
	 * 
	 */
	public void setConfermaRicezione(boolean value) {
		this.confermaRicezione = value;
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
	 * Gets the value of the indirizzoDestinatario property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getIndirizzoDestinatario() {
		return this.indirizzoDestinatario;
	}

	/**
	 * Sets the value of the indirizzoDestinatario property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setIndirizzoDestinatario(String value) {
		this.indirizzoDestinatario = value;
	}

	/**
	 * Gets the value of the indirizzoTelematicoMittente property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getIndirizzoMittente() {
		return this.indirizzoMittente;
	}

	/**
	 * Sets the value of the indirizzoMittente property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setIndirizzoMittente(String value) {
		this.indirizzoMittente = value;
	}

	/**
	 * Gets the value of the inoltro property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link Inoltro }
	 *     
	 */
	public Inoltro getInoltro() {
		return this.inoltro;
	}

	public void setInoltro(Inoltro inoltro, String value) {
		this.inoltro = inoltro;
		this.inoltroValue = value;
	}


	/**
	 * Gets the value of the listaEccezioni property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the listaEccezioni property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getListaEccezioni().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Eccezione }
	 * 
	 * 
	 */
	public List<Eccezione> getListaEccezioni() {
		if (this.listaEccezioni == null) {
			this.listaEccezioni = new ArrayList<Eccezione>();
		}
		return this.listaEccezioni;
	}

	/**
	 * Gets the value of the listaRiscontri property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the listaRiscontri property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getListaRiscontri().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Riscontro }
	 * 
	 * 
	 */
	public List<Riscontro> getListaRiscontri() {
		if (this.listaRiscontri == null) {
			this.listaRiscontri = new ArrayList<Riscontro>();
		}
		return this.listaRiscontri;
	}

	/**
	 * Gets the value of the listaTrasmissioni property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the listaTrasmissioni property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getListaTrasmissioni().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Trasmissione }
	 * 
	 * 
	 */
	public List<Trasmissione> getListaTrasmissioni() {
		if (this.listaTrasmissioni == null) {
			this.listaTrasmissioni = new ArrayList<Trasmissione>();
		}
		return this.listaTrasmissioni;
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
	 * Gets the value of the profiloDiCollaborazione property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public ProfiloDiCollaborazione getProfiloDiCollaborazione() {
		return this.profiloDiCollaborazione;
	}

	/**
	 * Sets the value of the profiloDiCollaborazione property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setProfiloDiCollaborazione(ProfiloDiCollaborazione value) {
		this.profiloDiCollaborazione = value;
	}
	
	
	public void setProfiloDiCollaborazione(ProfiloDiCollaborazione profiloDiCollaborazione, String value) {
		this.profiloDiCollaborazione = profiloDiCollaborazione;
		this.profiloDiCollaborazioneValue = value;
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
	 * Gets the value of the riferimentoMsgBustaRichiedenteServizio property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getRiferimentoMsgBustaRichiedenteServizio() {
		return this.riferimentoMsgBustaRichiedenteServizio;
	}

	/**
	 * Sets the value of the riferimentoMsgBustaRichiedenteServizio property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setRiferimentoMsgBustaRichiedenteServizio(String value) {
		this.riferimentoMsgBustaRichiedenteServizio = value;
	}

	/**
	 * Gets the value of the scadenza property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link XMLGregorianCalendar }
	 *     
	 */
	public Date getScadenza() {
		return this.scadenza;
	}

	/**
	 * Sets the value of the scadenza property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link XMLGregorianCalendar }
	 *     
	 */
	public void setScadenza(Date value) {
		this.scadenza = value;
	}

	/**
	 * Gets the value of the sequenza property.
	 * 
	 */
	public long getSequenza() {
		return this.sequenza;
	}

	/**
	 * Sets the value of the sequenza property.
	 * 
	 */
	public void setSequenza(long value) {
		this.sequenza = value;
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
	 * Gets the value of the servizioCorrelato property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getServizioCorrelato() {
		return this.servizioCorrelato;
	}

	/**
	 * Sets the value of the servizioCorrelato property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setServizioCorrelato(String value) {
		this.servizioCorrelato = value;
	}

	/**
	 * Gets the value of the servizioRichiedenteBustaDiServizio property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getServizioRichiedenteBustaDiServizio() {
		return this.servizioRichiedenteBustaDiServizio;
	}

	/**
	 * Sets the value of the servizioRichiedenteBustaDiServizio property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setServizioRichiedenteBustaDiServizio(String value) {
		this.servizioRichiedenteBustaDiServizio = value;
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
	 * Gets the value of the tipoOraRegistrazione property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public TipoOraRegistrazione getTipoOraRegistrazione() {
		return this.tipoOraRegistrazione;
	}

	public void setTipoOraRegistrazione(TipoOraRegistrazione tipo, String value) {
		this.tipoOraRegistrazione = tipo;
		this.tipoOraRegistrazioneValue = value;
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

	/**
	 * Gets the value of the tipoServizioCorrelato property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getTipoServizioCorrelato() {
		return this.tipoServizioCorrelato;
	}

	/**
	 * Sets the value of the tipoServizioCorrelato property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setTipoServizioCorrelato(String value) {
		this.tipoServizioCorrelato = value;
	}

	/**
	 * Gets the value of the tipoServizioRichiedenteBustaDiServizio property.
	 * 
	 * @return
	 *     possible object is
	 *     {@link String }
	 *     
	 */
	public String getTipoServizioRichiedenteBustaDiServizio() {
		return this.tipoServizioRichiedenteBustaDiServizio;
	}

	/**
	 * Sets the value of the tipoServizioRichiedenteBustaDiServizio property.
	 * 
	 * @param value
	 *     allowed object is
	 *     {@link String }
	 *     
	 */
	public void setTipoServizioRichiedenteBustaDiServizio(String value) {
		this.tipoServizioRichiedenteBustaDiServizio = value;
	}


	public String getProfiloDiCollaborazioneValue() {
		return this.profiloDiCollaborazioneValue;
	}

	public void setProfiloDiCollaborazioneValue(String profiloDiCollaborazioneValue) {
		this.profiloDiCollaborazioneValue = profiloDiCollaborazioneValue;
	}

	public String getTipoOraRegistrazioneValue() {
		return this.tipoOraRegistrazioneValue;
	}

	public void setTipoOraRegistrazioneValue(String tipoOraRegistrazioneValue) {
		this.tipoOraRegistrazioneValue = tipoOraRegistrazioneValue;
	}

	public String getInoltroValue() {
		return this.inoltroValue;
	}

	public void setInoltroValue(String inoltroValue) {
		this.inoltroValue = inoltroValue;
	}

	public String getIdentificativoPortaDestinatario() {
		return this.identificativoPortaDestinatario;
	}

	public void setIdentificativoPortaDestinatario(
			String identificativoPortaDestinatario) {
		this.identificativoPortaDestinatario = identificativoPortaDestinatario;
	}

	public String getIdentificativoPortaMittente() {
		return this.identificativoPortaMittente;
	}

	public void setIdentificativoPortaMittente(String identificativoPortaMittente) {
		this.identificativoPortaMittente = identificativoPortaMittente;
	}

	public int getVersioneServizio() {
		return this.versioneServizio;
	}

	public void setVersioneServizio(int versioneServizio) {
		this.versioneServizio = versioneServizio;
	}
	public void setVersioneServizio(String versioneServizio) {
		this.versioneServizio = Integer.parseInt(versioneServizio);
	}

	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}

	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}

	public String getServizioApplicativoErogatore() {
		return this.servizioApplicativoErogatore;
	}

	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		this.servizioApplicativoErogatore = servizioApplicativoErogatore;
	}

	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public String getDigest() {
		return this.digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}
	


	// Metodi aggiuntivi


	/**
	 * Ritorna, il numero di  oggetti {@link Eccezione} presenti nella busta.
	 *
	 * @return dimensione della lista di eccezioni presenti nella busta.
	 * 
	 */
	public int sizeListaEccezioni() {
		return this.listaEccezioni.size();
	}
	/**
	 * Ritorna, il numero di  oggetti {@link Trasmissione} presenti nella busta.
	 *
	 * @return dimensione della lista di trasmissioni presenti nella busta.
	 * 
	 */
	public int sizeListaTrasmissioni() {
		return this.listaTrasmissioni.size();
	}
	/**
	 * Ritorna, il numero di  oggetti {@link Riscontro} presenti nella busta.
	 *
	 * @return dimensione della lista di riscontri presenti nella busta.
	 * 
	 */
	public int sizeListaRiscontri() {
		return this.listaRiscontri.size();
	}

	/**
	 * Ritorna, le {@link Eccezione} presenti nella lista Eccezioni.
	 *
	 * @return la lista eccezioni
	 * 
	 */
	public List<Eccezione> cloneListaEccezioni() {
		if(this.listaEccezioni!=null){
			List<Eccezione> eccs = new ArrayList<Eccezione>();
			for(int i=0; i<this.sizeListaEccezioni(); i++){
				eccs.add(this.getEccezione(i).clone());
			}
			return eccs;
		}else{
			return null;
		}
	}
	/**
	 * Ritorna, l'{@link Eccezione} presente nella lista Eccezioni all'indice <var>index</var>.
	 *
	 * @param index indice della eccezione da ritornare.
	 * @return l'eccezione alla posizione <var>index</var>.
	 * 
	 */
	public Eccezione getEccezione(int index) {
		return this.listaEccezioni.get( index );
	}

	/**
	 * Ritorna, la {@link Trasmissione} presente nella lista Trasmissioni all'indice <var>index</var>.
	 *
	 * @param index indice della trasmissione da ritornare.
	 * @return la trasmissione alla posizione <var>index</var>.
	 * 
	 */
	public Trasmissione getTrasmissione(int index) {
		return this.listaTrasmissioni.get( index );
	}

	/**
	 * Ritorna, il {@link Riscontro} presente nella lista Riscontri all'indice <var>index</var>.
	 *
	 * @param index indice del riscontro da ritornare.
	 * @return il riscontro alla posizione <var>index</var>.
	 * 
	 */
	public Riscontro getRiscontro(int index) {
		return this.listaRiscontri.get( index );
	}

	/**
	 * Aggiunge, l'{@link Eccezione}, passata come parametro <var>e</var>,
	 * nella lista Eccezioni.
	 *
	 * @param e eccezione da aggiungere nella lista eccezioni.
	 * 
	 */
	public void addEccezione(Eccezione e) {
		this.listaEccezioni.add(e);
	}

	/**
	 * Aggiunge, la {@link Trasmissione}, passata come parametro <var>t</var>,
	 * nella lista Trasmissioni.
	 *
	 * @param t trasmissione da aggiungere nella lista trasmissioni.
	 * 
	 */
	public void addTrasmissione(Trasmissione t) {
		this.listaTrasmissioni.add(t);
	}

	/**
	 * Aggiunge, il {@link Riscontro}, passata come parametro <var>r</var>,
	 * nella lista Riscontri.
	 *
	 * @param r riscontro da aggiungere nella lista riscontri.
	 * 
	 */
	public void addRiscontro(Riscontro r) {
		this.listaRiscontri.add(r);
	}

	/**
	 * Ritorna, l'{@link Eccezione} presente nella lista Eccezioni all'indice <var>index</var>;
	 * Inoltre l'eccezione viene eliminata dalla lista eccezioni.
	 *
	 * @param index indice della eccezione da rimuovere.
	 * @return l'eccezione alla posizione <var>index</var>.
	 * 
	 */
	public Eccezione removeEccezione(int index) {
		return this.listaEccezioni.remove(index);
	}

	/**
	 * Ritorna, la {@link Trasmissione} presente nella lista Trasmissioni all'indice <var>index</var>;
	 * Inoltre la trasmissione viene eliminata dalla lista trasmissioni.
	 *
	 * @param index indice della trasmissione da rimuovere.
	 * @return la trasmissione alla posizione <var>index</var>.
	 * 
	 */
	public Trasmissione removeTrasmissione(int index) {
		return this.listaTrasmissioni.remove(index);
	}

	/**
	 * Ritorna, il {@link Riscontro} presente nella lista Riscontri all'indice <var>index</var>;
	 * Inoltre il riscontro viene eliminato dalla lista riscontri.
	 *
	 * @param index indice del riscontro da rimuovere.
	 * @return il riscontro alla posizione <var>index</var>.
	 * 
	 */
	public Riscontro removeRiscontro(int index) {
		return this.listaRiscontri.remove(index);
	}

	public String toStringListaEccezioni(IProtocolFactory protocolFactory) throws ProtocolException{
		if(this.sizeListaEccezioni()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<this.sizeListaEccezioni(); i++){
				if(i>0)
					bf.append("\n");
				bf.append(this.getEccezione(i).toString(protocolFactory));
			}
			return bf.toString();
		}else{
			return null;
		}
	}


	public String toStringListaEccezioni_erroriNonGravi(IProtocolFactory protocolFactory) throws ProtocolException{
		if(this.sizeListaEccezioni()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<this.sizeListaEccezioni(); i++){
				if(LivelloRilevanza.ERROR.equals(this.getEccezione(i).getRilevanza()) == false){
					if(i>0)
						bf.append("\n");
					bf.append(this.getEccezione(i).toString(protocolFactory));
				}
			}
			return bf.toString();
		}else{
			return null;
		}
	}

	public String toStringListaEccezioni_erroriGravi(IProtocolFactory protocolFactory) throws ProtocolException{
		if(this.sizeListaEccezioni()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<this.sizeListaEccezioni(); i++){
				if(LivelloRilevanza.ERROR.equals(this.getEccezione(i).getRilevanza())){
					if(i>0)
						bf.append("\n");
					bf.append(this.getEccezione(i).toString(protocolFactory));
				}
			}
			return bf.toString();
		}else{
			return null;
		}
	}


	public boolean containsEccezioniGravi(){
		for(int i=0; i<this.sizeListaEccezioni(); i++){
			if(LivelloRilevanza.ERROR.equals(this.getEccezione(i).getRilevanza())){
				return true;
			}
		}
		return false;
	}


	public static String toStringListaEccezioni(java.util.Vector<Eccezione> errors, IProtocolFactory protocolFactory) throws ProtocolException{
		if(errors.size()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<errors.size(); i++){
				if(i>0)
					bf.append("\n");
				bf.append(errors.get(i).toString(protocolFactory));
			}
			return bf.toString();
		}else{
			return null;
		}
	}


	public static String toStringListaEccezioni_erroriNonGravi(java.util.Vector<Eccezione> errors, IProtocolFactory protocolFactory) throws ProtocolException{
		if(errors.size()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<errors.size(); i++){
				if(LivelloRilevanza.ERROR.equals(errors.get(i).getRilevanza()) == false){
					if(i>0)
						bf.append("\n");
					bf.append(errors.get(i).toString(protocolFactory));
				}
			}
			return bf.toString();
		}else{
			return null;
		}
	}

	public static String toStringListaEccezioni_erroriGravi(java.util.Vector<Eccezione> errors, IProtocolFactory protocolFactory) throws ProtocolException{
		if(errors.size()>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i<errors.size(); i++){
				if(LivelloRilevanza.ERROR.equals(errors.get(i).getRilevanza())){
					if(i>0)
						bf.append("\n");
					bf.append(errors.get(i).toString(protocolFactory));
				}
			}
			return bf.toString();
		}else{
			return null;
		}
	}

	public static boolean containsEccezioniGravi(java.util.Vector<Eccezione> errors){
		for(int i=0; i<errors.size(); i++){
			if(LivelloRilevanza.ERROR.equals(errors.get(i).getRilevanza())){
				return true;
			}
		}
		return false;

	}





	/**
	 * Data una busta, genera la corrispondente busta di risposta
	 * Inverte mittente con destinatario.
	 * Setta una nuova ora di registrazione
	 *
	 * @return una busta utilizzabile come risposta
	 * 
	 */
	public Busta invertiBusta(TipoOraRegistrazione tipoOraRegistrazione, String tipoTempo){
		Busta bustaHTTPReply = null;
		bustaHTTPReply = new Busta(this.getProtocollo());

		bustaHTTPReply.setTipoDestinatario(this.getTipoMittente());
		bustaHTTPReply.setDestinatario(this.getMittente());
		bustaHTTPReply.setIdentificativoPortaDestinatario(this.getIdentificativoPortaMittente());
		bustaHTTPReply.setIndirizzoDestinatario(this.getIndirizzoMittente());

		bustaHTTPReply.setTipoMittente(this.getTipoDestinatario());
		bustaHTTPReply.setMittente(this.getDestinatario());
		bustaHTTPReply.setIndirizzoMittente(this.getIndirizzoDestinatario());
		bustaHTTPReply.setIdentificativoPortaMittente(this.getIdentificativoPortaDestinatario());

		bustaHTTPReply.setServizioApplicativoFruitore(this.getServizioApplicativoFruitore()); // lo mantengo, non deve essere invertito

		Date oraRegistrazione = DateManager.getDate();
		bustaHTTPReply.setOraRegistrazione(oraRegistrazione);
		bustaHTTPReply.setTipoOraRegistrazione(tipoOraRegistrazione,tipoTempo);
		return bustaHTTPReply;
	}

	/**
	 * Data una busta, ne ritorna una copia
	 *
	 * @return una busta
	 * 
	 */
	@Override 
	public Busta clone(){
		Busta clone = new Busta(this.getProtocollo());

		// mittente
		clone.setTipoMittente(this.tipoMittente!=null ? new String(this.tipoMittente) : null);
		clone.setIndirizzoMittente(this.indirizzoMittente!=null ? new String(this.indirizzoMittente) : null);
		clone.setMittente(this.mittente!=null ? new String(this.mittente) : null);
		clone.setIdentificativoPortaMittente(this.identificativoPortaMittente!=null ? new String(this.identificativoPortaMittente) : null);

		// destinatario
		clone.setTipoDestinatario(this.tipoDestinatario!=null ? new String(this.tipoDestinatario) : null);
		clone.setIndirizzoDestinatario(this.indirizzoDestinatario!=null ? new String(this.indirizzoDestinatario) : null);
		clone.setDestinatario(this.destinatario!=null ? new String(this.destinatario) : null);
		clone.setIdentificativoPortaDestinatario(this.identificativoPortaDestinatario!=null ? new String(this.identificativoPortaDestinatario) : null);

		// profilo di collaborazione
		clone.setProfiloDiCollaborazione(this.profiloDiCollaborazione);
		clone.setProfiloDiCollaborazioneValue(this.profiloDiCollaborazioneValue!=null ? new String(this.profiloDiCollaborazioneValue) : null);
		clone.setServizioCorrelato(this.servizioCorrelato!=null ? new String(this.servizioCorrelato) : null);
		clone.setTipoServizioCorrelato(this.tipoServizioCorrelato!=null ? new String(this.tipoServizioCorrelato) : null);
		
		// collaborazione
		clone.setCollaborazione(this.collaborazione!=null ? new String(this.collaborazione) : null);
		
		// servizio
		clone.setServizio(this.servizio!=null ? new String(this.servizio) : null);
		clone.setTipoServizio(this.tipoServizio!=null ? new String(this.tipoServizio) : null);
		clone.setVersioneServizio(new Integer(this.versioneServizio));
		clone.setServizioRichiedenteBustaDiServizio(this.servizioRichiedenteBustaDiServizio!=null ? new String(this.servizioRichiedenteBustaDiServizio) : null);
		clone.setTipoServizioRichiedenteBustaDiServizio(this.tipoServizioRichiedenteBustaDiServizio!=null ? new String(this.tipoServizioRichiedenteBustaDiServizio) : null);
		
		// azione
		clone.setAzione(this.azione!=null ? new String(this.azione) : null);
		clone.setAzioneRichiedenteBustaDiServizio(this.azioneRichiedenteBustaDiServizio!=null ? new String(this.azioneRichiedenteBustaDiServizio) : null);
		
		// identificativi
		clone.setID(this.id!=null ? new String(this.id) : null);
		clone.setRiferimentoMessaggio(this.riferimentoMessaggio!=null ? new String(this.riferimentoMessaggio) : null);
		clone.setRiferimentoMsgBustaRichiedenteServizio(this.riferimentoMsgBustaRichiedenteServizio!=null ? new String(this.riferimentoMsgBustaRichiedenteServizio) : null);
		
		// date
		clone.setOraRegistrazione(this.oraRegistrazione!=null ? new Date(this.oraRegistrazione.getTime()) : null);
		clone.setTipoOraRegistrazione(this.tipoOraRegistrazione,
				this.tipoOraRegistrazioneValue!=null ? new String(this.tipoOraRegistrazioneValue) : null);
		clone.setTipoOraRegistrazioneValue(this.tipoOraRegistrazioneValue!=null ? new String(this.tipoOraRegistrazioneValue) : null);
		clone.setScadenza(this.scadenza!=null ? new Date(this.scadenza.getTime()) : null);

		// profilo di trasmissione
		clone.setInoltro(this.inoltro,
				this.inoltroValue!=null ? new String(this.inoltroValue) : null);
		clone.setInoltroValue(this.inoltroValue!=null ? new String(this.inoltroValue) : null);
		clone.setConfermaRicezione(new Boolean(this.confermaRicezione));
		
		// sequenza
		clone.setSequenza(new Long(this.sequenza));
		
		// servizi applicativi
		clone.setServizioApplicativoFruitore(this.servizioApplicativoFruitore!=null ? new String(this.servizioApplicativoFruitore) : null);
		clone.setServizioApplicativoErogatore(this.servizioApplicativoErogatore!=null ? new String(this.servizioApplicativoErogatore) : null);

		// protocollo
		clone.setProtocollo(this.protocollo!=null ? new String(this.protocollo) : null);

		// digest
		clone.setDigest(this.digest!=null ? new String(this.digest) : null);

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

		// ListaEccezioni
		for(int i=0; i<this.sizeListaEccezioni(); i++){
			clone.addEccezione(this.getEccezione(i).clone());
		}
		
		// ListaRiscontri
		for(int i=0; i<this.sizeListaRiscontri(); i++){
			clone.addRiscontro(this.getRiscontro(i).clone());
		}
		
		// ListaTrasmissioni
		for(int i=0; i<this.sizeListaTrasmissioni(); i++){
			clone.addTrasmissione(this.getTrasmissione(i).clone());
		}



		return clone;
	}

}