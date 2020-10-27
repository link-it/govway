/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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
package org.openspcoop2.core.registry.ws.server.filter;

/**
 * <p>Java class for SearchFilterAccordoServizioParteComune complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-accordo-servizio-parte-comune"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="soggetto-referente" type="{http://www.openspcoop2.org/core/registry/management}id-soggetto" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="servizio-composto" type="{http://www.openspcoop2.org/core/registry/management}accordo-servizio-parte-comune-servizio-composto" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="stato-package" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="privato" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="service-binding" type="{http://www.openspcoop2.org/core/registry}ServiceBinding" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="message-type" type="{http://www.openspcoop2.org/core/registry}MessageType" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="formato-specifica" type="{http://www.openspcoop2.org/core/registry}FormatoSpecifica" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="canale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="orCondition" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="Boolean.valueOf("false")" /&gt;
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;
import org.openspcoop2.core.registry.ws.server.filter.beans.IdSoggetto;
import org.openspcoop2.core.registry.ws.server.filter.beans.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.MessageType;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;

/**     
 * SearchFilterAccordoServizioParteComune
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "search-filter-accordo-servizio-parte-comune", namespace="http://www.openspcoop2.org/core/registry/management", propOrder = {
    "soggettoReferente",
    "servizioComposto",
    "statoPackage",
    "privato",
    "serviceBinding",
    "messageType",
    "nome",
    "descrizione",
    "formatoSpecifica",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "versione",
    "canale",
    "orCondition",
    "limit",
    "offset"
})
@javax.xml.bind.annotation.XmlRootElement(name = "search-filter-accordo-servizio-parte-comune")
public class SearchFilterAccordoServizioParteComune extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="soggetto-referente",required=false,nillable=false)
	private IdSoggetto soggettoReferente;
	
	public void setSoggettoReferente(IdSoggetto soggettoReferente){
		this.soggettoReferente = soggettoReferente;
	}
	
	public IdSoggetto getSoggettoReferente(){
		return this.soggettoReferente;
	}
	
	
	@XmlElement(name="servizio-composto",required=false,nillable=false)
	private AccordoServizioParteComuneServizioComposto servizioComposto;
	
	public void setServizioComposto(AccordoServizioParteComuneServizioComposto servizioComposto){
		this.servizioComposto = servizioComposto;
	}
	
	public AccordoServizioParteComuneServizioComposto getServizioComposto(){
		return this.servizioComposto;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="stato-package",required=false,nillable=false)
	private String statoPackage;
	
	public void setStatoPackage(String statoPackage){
		this.statoPackage = statoPackage;
	}
	
	public String getStatoPackage(){
		return this.statoPackage;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="privato",required=false,nillable=false)
	private java.lang.Boolean privato;
	
	public void setPrivato(java.lang.Boolean privato){
		this.privato = privato;
	}
	
	public java.lang.Boolean getPrivato(){
		return this.privato;
	}
	
	
	@XmlElement(name="service-binding",required=false,nillable=false)
	private ServiceBinding serviceBinding;
	
	public void setServiceBinding(ServiceBinding serviceBinding){
		this.serviceBinding = serviceBinding;
	}
	
	public ServiceBinding getServiceBinding(){
		return this.serviceBinding;
	}
	
	
	@XmlElement(name="message-type",required=false,nillable=false)
	private MessageType messageType;
	
	public void setMessageType(MessageType messageType){
		this.messageType = messageType;
	}
	
	public MessageType getMessageType(){
		return this.messageType;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=false,nillable=false)
	private String nome;
	
	public void setNome(String nome){
		this.nome = nome;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=false,nillable=false)
	private String descrizione;
	
	public void setDescrizione(String descrizione){
		this.descrizione = descrizione;
	}
	
	public String getDescrizione(){
		return this.descrizione;
	}
	
	
	@XmlElement(name="formato-specifica",required=false,nillable=false)
	private FormatoSpecifica formatoSpecifica;
	
	public void setFormatoSpecifica(FormatoSpecifica formatoSpecifica){
		this.formatoSpecifica = formatoSpecifica;
	}
	
	public FormatoSpecifica getFormatoSpecifica(){
		return this.formatoSpecifica;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione-min",required=false,nillable=false)
	private Date oraRegistrazioneMin;
	
	public void setOraRegistrazioneMin(Date oraRegistrazioneMin){
		this.oraRegistrazioneMin = oraRegistrazioneMin;
	}
	
	public Date getOraRegistrazioneMin(){
		return this.oraRegistrazioneMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione-max",required=false,nillable=false)
	private Date oraRegistrazioneMax;
	
	public void setOraRegistrazioneMax(Date oraRegistrazioneMax){
		this.oraRegistrazioneMax = oraRegistrazioneMax;
	}
	
	public Date getOraRegistrazioneMax(){
		return this.oraRegistrazioneMax;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="versione",required=false,nillable=false)
	private Integer versione;
	
	public void setVersione(Integer versione){
		this.versione = versione;
	}
	
	public Integer getVersione(){
		return this.versione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="canale",required=false,nillable=false)
	private String canale;
	
	public void setCanale(String canale){
		this.canale = canale;
	}
	
	public String getCanale(){
		return this.canale;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="orCondition",required=false,nillable=false,defaultValue="false")
	private Boolean orCondition = Boolean.valueOf("false");
	
	public void setOrCondition(Boolean orCondition){
		this.orCondition = orCondition;
	}
	
	public Boolean getOrCondition(){
		return this.orCondition;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="limit",required=false,nillable=false)
	private Integer limit;
	
	public void setLimit(Integer limit){
		this.limit = limit;
	}
	
	public Integer getLimit(){
		return this.limit;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="offset",required=false,nillable=false)
	private Integer offset;
	
	public void setOffset(Integer offset){
		this.offset = offset;
	}
	
	public Integer getOffset(){
		return this.offset;
	}
	
	
	
	
}