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
import java.text.MessageFormat;
import java.util.ArrayList;
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
 * Oggetto contenente informazioni per la ricerca di loggedEntry
 * 
 * <p>Java class for FilterSearch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FilterSearch">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="correlatiOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="correlazioneApplicativa" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dataFine" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="dataInizio" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="delegata" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="busta" type="{http://logger.dto.openspcoop.org}Busta"/>
 *         &lt;element name="filtroSoggetti" type="{http://ws.monitor.openspcoop.org}ArrayOf_tns2_IDSoggetto"/>
 *         &lt;element name="filtroSoggetto" type="{http://commons.dao.openspcoop.org}IDSoggetto" maxOccurs="unbounded"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="idBusta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="idFunzione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificativoPorta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="nomePorta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="offsetMap" maxOccurs="unbounded" nillable="true" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="partial" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="pdd" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
 *         &lt;element name="servizioApplicativo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="severita" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "FilterSearch", propOrder = {
    "correlatiOnly",
    "correlazioneApplicativa",
    "dataFine",
    "dataInizio",
    "delegata",
    "busta",
    "filtroSoggetti",
    "id",
    "idBusta",
    "idFunzione",
    "identificativoPorta",
    "limit",
    "nomePorta",
    "offset",
    "offsetMap",
    "partial",
    "pdd",
    "properties",
    "servizioApplicativo",
    "severita"
})

public class FiltroRicercaDiagnostici implements Serializable{


	private static final long serialVersionUID = 2103096411857601491L;

    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(DateTime2Calendar .class)
    @XmlSchemaType(name = "dateTime")
    protected Calendar dataFine;
    @XmlElement(required = true, type = String.class, nillable = true)
    @XmlJavaTypeAdapter(DateTime2Calendar .class)
    @XmlSchemaType(name = "dateTime")
    protected Calendar dataInizio;
	
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean delegata;
    @XmlElement(required = true, nillable = true)
    protected String nomePorta; // nomePortaDelegata o Applicativa
    @XmlElement(required = true, nillable = true)
    protected String idFunzione;
    @XmlElement(required = true, nillable = true)
    protected IDSoggetto dominio;
    
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
	
    @XmlElement(required = true, nillable = true)
    protected String idBustaRichiesta;
    @XmlElement(required = true, nillable = true)
    protected String idBustaRisposta;
    @XmlElement(required = true, nillable = true)
    protected InformazioniProtocollo busta;
	
    @XmlElement(required = true, nillable = true)
    protected String servizioApplicativo;
    
    @XmlElement(required = true, nillable = true)
    protected String correlazioneApplicativa;
    @XmlElement(required = true, nillable = true)
    protected String correlazioneApplicativaRisposta;
    @XmlElement(required = true, nillable = true)
    protected boolean correlazioneApplicativaOrMatch = false;
		
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer severita;
    
    private String messaggioCercatoInternamenteTestoDiagnostico;
    
	@XmlTransient
    private String protocollo;
    
    @XmlJavaTypeAdapter(Properties2Hashtable .class)
    protected Hashtable<String, String> properties;
	
    @XmlElement(required = true, nillable = true)
    protected List<IDSoggetto> filtroSoggetti;
   
    
    


    
 
    public FiltroRicercaDiagnostici() {
    	this.properties = new Hashtable<String, String>();
    	this.filtroSoggetti = new ArrayList<IDSoggetto>();
	}
    
    public void addFiltroSoggetto(IDSoggetto soggetto){
    	this.filtroSoggetti.add(soggetto);
    }
    
    public int sizeFiltroSoggetti(){
    	return this.filtroSoggetti.size();
    }
    
    public IDSoggetto getFiltroSoggetto(int i){
    	return this.filtroSoggetti.get(i);
    }
    
    public IDSoggetto removeFiltroSoggetto(int i){
    	return this.filtroSoggetti.remove(i);
    }
    
    public List<IDSoggetto> getFiltroSoggetti() {
    	return this.filtroSoggetti;
    }
    
    public void setFiltroSoggetti(List<IDSoggetto> list) {
		this.filtroSoggetti = list;
    }
        
    public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
    
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
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
	
	public Integer getSeverita() {
		return this.severita;
	}
	
	public void setSeverita(Integer severita) {
		this.severita = severita;
	}
	
	public String getIdFunzione() {
		return this.idFunzione;
	}
	
	public void setIdFunzione(String idFunzione) {
		this.idFunzione = idFunzione;
	}
  
	
	public Calendar getDataInizio() {
		return this.dataInizio;
	}
	
	public void setDataInizio(Calendar dataInizio) {
		this.dataInizio = dataInizio;
	}
	
	public Calendar getDataFine() {
		return this.dataFine;
	}
	
	public void setDataFine(Calendar dataFine) {
		this.dataFine = dataFine;
	}
	
	public InformazioniProtocollo getInformazioniProtocollo() {
		return this.busta;
	}
	
	public void setInformazioniProtocollo(InformazioniProtocollo busta) {
		this.busta = busta;
	}
	
	public String getNomePorta() {
		return this.nomePorta;
	}
	
	public void setNomePorta(String nomePorta) {
		this.nomePorta = nomePorta;
	}
	
	public Boolean isDelegata() {
		return this.delegata;
	}
	
	public void setDelegata(Boolean delegata) {
		this.delegata = delegata;
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

	public boolean isCorrelazioneApplicativaOrMatch() {
		return this.correlazioneApplicativaOrMatch;
	}

	public void setCorrelazioneApplicativaOrMatch(
			boolean correlazioneApplicativaOrMatch) {
		this.correlazioneApplicativaOrMatch = correlazioneApplicativaOrMatch;
	}

	public Boolean getRicercaSoloMessaggiCorrelatiInformazioniProtocollo() {
		return this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
	}

	public void setRicercaSoloMessaggiCorrelatiInformazioniProtocollo(Boolean value) {
		this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo = value;
	}

	public String getIdBustaRichiesta() {
		return this.idBustaRichiesta;
	}

	public void setIdBustaRichiesta(String idBustaRichiesta) {
		this.idBustaRichiesta = idBustaRichiesta;
	}

	public String getIdBustaRisposta() {
		return this.idBustaRisposta;
	}

	public void setIdBustaRisposta(String idBustaRisposta) {
		this.idBustaRisposta = idBustaRisposta;
	}

	public IDSoggetto getDominio() {
		return this.dominio;
	}

	public void setDominio(IDSoggetto dominio) {
		this.dominio = dominio;
	}
	
    public String getMessaggioCercatoInternamenteTestoDiagnostico() {
		return this.messaggioCercatoInternamenteTestoDiagnostico;
	}

	public void setMessaggioCercatoInternamenteTestoDiagnostico(
			String messaggioCercatoInternamenteTestoDiagnostico) {
		this.messaggioCercatoInternamenteTestoDiagnostico = messaggioCercatoInternamenteTestoDiagnostico;
	}
	
	@Override
	public String toString() {
				
		String pattern="idBustaRichiesta [{0}]" +
				"idBustaRisposta [{1}]" +
				" nomePorta [{2}]" +
				" isDelegata [{3}]" +
				(this.dataInizio!=null ? " dataInizio [{4,date} {4,time}]" : " dataInizio [{4}]") +
				(this.dataFine!=null   ? " dataFine   [{5,date} {5,time}]" : " dataFine [{5}]") + 
				" Informazioni Busta [{6}]" +
				" dominio  [{7}]" +
				" onlyMsgDiagWithProtocolInfo [{8}]"+
				" parametriEstensioneSize [{9}]"+
				" protocollo [{10}]"+
				" correlazioneApplicativaRichiesta [{11}]"+
				" correlazioneApplicativaRisposta [{12}]"+
				" correlazioneApplicativaOrMatch [{13}]"+
				" messaggioCercatoInternamenteTestoDiagnostico [{14}]";
		
		
		return MessageFormat.format(pattern, 
				this.idBustaRichiesta!=null ? this.idBustaRichiesta : "not set",
				this.idBustaRisposta!=null ? this.idBustaRisposta : "not set",
				this.nomePorta!=null ? this.nomePorta : "not set",
				this.delegata!=null ? this.delegata : "not set",
				this.dataInizio!=null ? this.dataInizio.getTime() : "not set",
				this.dataFine!=null ? this.dataFine.getTime() : "not set",
				this.busta!=null ? this.busta.toString() : "not set",
				this.dominio!=null ? this.dominio.toString() : "not set",
				this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo!=null ? this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo : "not set",
				this.properties!=null ? this.properties.size() : "not set",
				this.protocollo!=null ? this.protocollo : "not set",
				this.correlazioneApplicativa!=null ? this.correlazioneApplicativa : "not set",
				this.correlazioneApplicativaRisposta!=null ? this.correlazioneApplicativaRisposta : "not set",
				this.correlazioneApplicativaOrMatch,
				this.messaggioCercatoInternamenteTestoDiagnostico!=null ? this.messaggioCercatoInternamenteTestoDiagnostico : "not set"
				);
	}


}


