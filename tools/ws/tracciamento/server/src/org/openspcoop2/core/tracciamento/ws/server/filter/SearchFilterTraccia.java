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
package org.openspcoop2.core.tracciamento.ws.server.filter;

/**
 * <p>Java class for SearchFilterTraccia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-traccia">
 *     &lt;sequence>
 *         &lt;element name="dominio" type="{http://www.openspcoop2.org/core/tracciamento/management}dominio" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo-correlazione-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo-correlazione-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="correlazione-applicativa-and-match" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *         &lt;element name="busta" type="{http://www.openspcoop2.org/core/tracciamento/management}busta" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ricerca-solo-buste-errore" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *         &lt;element name="tipo" type="{http://www.openspcoop2.org/core/tracciamento}TipoTraccia" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="descOrder" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.tracciamento.constants.TipoTraccia;
import java.util.Date;
import org.openspcoop2.core.tracciamento.ws.server.filter.beans.Busta;
import org.openspcoop2.core.tracciamento.ws.server.filter.beans.Dominio;

/**     
 * SearchFilterTraccia
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "search-filter-traccia", namespace="http://www.openspcoop2.org/core/tracciamento/management", propOrder = {
    "dominio",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "identificativoCorrelazioneRichiesta",
    "identificativoCorrelazioneRisposta",
    "correlazioneApplicativaAndMatch",
    "busta",
    "ricercaSoloBusteErrore",
    "tipo",
    "limit",
    "offset",
    "descOrder"
})
@javax.xml.bind.annotation.XmlRootElement(name = "search-filter-traccia")
public class SearchFilterTraccia extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="dominio",required=false,nillable=false)
	private Dominio dominio;
	
	public void setDominio(Dominio dominio){
		this.dominio = dominio;
	}
	
	public Dominio getDominio(){
		return this.dominio;
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
  @XmlElement(name="identificativo-correlazione-richiesta",required=false,nillable=false)
	private String identificativoCorrelazioneRichiesta;
	
	public void setIdentificativoCorrelazioneRichiesta(String identificativoCorrelazioneRichiesta){
		this.identificativoCorrelazioneRichiesta = identificativoCorrelazioneRichiesta;
	}
	
	public String getIdentificativoCorrelazioneRichiesta(){
		return this.identificativoCorrelazioneRichiesta;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-correlazione-risposta",required=false,nillable=false)
	private String identificativoCorrelazioneRisposta;
	
	public void setIdentificativoCorrelazioneRisposta(String identificativoCorrelazioneRisposta){
		this.identificativoCorrelazioneRisposta = identificativoCorrelazioneRisposta;
	}
	
	public String getIdentificativoCorrelazioneRisposta(){
		return this.identificativoCorrelazioneRisposta;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="correlazione-applicativa-and-match",required=false,nillable=false,defaultValue="false")
	private Boolean correlazioneApplicativaAndMatch = new Boolean("false");
	
	public void setCorrelazioneApplicativaAndMatch(Boolean correlazioneApplicativaAndMatch){
		this.correlazioneApplicativaAndMatch = correlazioneApplicativaAndMatch;
	}
	
	public Boolean getCorrelazioneApplicativaAndMatch(){
		return this.correlazioneApplicativaAndMatch;
	}
	
	
	@XmlElement(name="busta",required=false,nillable=false)
	private Busta busta;
	
	public void setBusta(Busta busta){
		this.busta = busta;
	}
	
	public Busta getBusta(){
		return this.busta;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="ricerca-solo-buste-errore",required=false,nillable=false,defaultValue="false")
	private Boolean ricercaSoloBusteErrore = new Boolean("false");
	
	public void setRicercaSoloBusteErrore(Boolean ricercaSoloBusteErrore){
		this.ricercaSoloBusteErrore = ricercaSoloBusteErrore;
	}
	
	public Boolean getRicercaSoloBusteErrore(){
		return this.ricercaSoloBusteErrore;
	}
	
	
	@XmlElement(name="tipo",required=false,nillable=false)
	private TipoTraccia tipo;
	
	public void setTipo(TipoTraccia tipo){
		this.tipo = tipo;
	}
	
	public TipoTraccia getTipo(){
		return this.tipo;
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
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="descOrder",required=false,nillable=false,defaultValue="false")
	private Boolean descOrder = new Boolean("false");
	
	public void setDescOrder(Boolean descOrder){
		this.descOrder = descOrder;
	}
	
	public Boolean getDescOrder(){
		return this.descOrder;
	}
	
	
	
	
}