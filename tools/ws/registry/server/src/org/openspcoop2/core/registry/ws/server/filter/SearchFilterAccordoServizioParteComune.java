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
package org.openspcoop2.core.registry.ws.server.filter;

/**
 * <p>Java class for SearchFilterAccordoServizioParteComune complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-accordo-servizio-parte-comune">
 *     &lt;sequence>
 *         &lt;element name="soggetto-referente" type="{http://www.openspcoop2.org/core/registry/management}id-soggetto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="servizio-composto" type="{http://www.openspcoop2.org/core/registry/management}accordo-servizio-parte-comune-servizio-composto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="stato-package" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="privato" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="orCondition" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;
import org.openspcoop2.core.registry.ws.server.filter.beans.IdSoggetto;
import org.openspcoop2.core.registry.ws.server.filter.beans.AccordoServizioParteComuneServizioComposto;

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
    "nome",
    "descrizione",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "versione",
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
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="versione",required=false,nillable=false)
	private String versione;
	
	public void setVersione(String versione){
		this.versione = versione;
	}
	
	public String getVersione(){
		return this.versione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="orCondition",required=false,nillable=false,defaultValue="false")
	private Boolean orCondition = new Boolean("false");
	
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