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
package org.openspcoop2.core.diagnostica.ws.server.filter;

/**
 * <p>Java class for SearchFilterMessaggioDiagnostico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-messaggio-diagnostico">
 *     &lt;sequence>
 *         &lt;element name="dominio" type="{http://www.openspcoop2.org/core/diagnostica/management}dominio-diagnostico" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="codice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="severita" type="{http://www.openspcoop2.org/core/diagnostica}LivelloDiSeveritaType" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="protocollo" type="{http://www.openspcoop2.org/core/diagnostica/management}protocollo" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="filtro-informazione-protocollo" type="{http://www.openspcoop2.org/core/diagnostica/management}filtro-informazione-protocollo" minOccurs="0" maxOccurs="1" />
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
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.Protocollo;
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.FiltroInformazioneProtocollo;
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.DominioDiagnostico;
import java.util.Date;

/**     
 * SearchFilterMessaggioDiagnostico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "search-filter-messaggio-diagnostico", namespace="http://www.openspcoop2.org/core/diagnostica/management", propOrder = {
    "dominio",
    "identificativoRichiesta",
    "identificativoRisposta",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "codice",
    "messaggio",
    "severita",
    "protocollo",
    "filtroInformazioneProtocollo",
    "limit",
    "offset",
    "descOrder"
})
@javax.xml.bind.annotation.XmlRootElement(name = "search-filter-messaggio-diagnostico")
public class SearchFilterMessaggioDiagnostico extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="dominio",required=false,nillable=false)
	private DominioDiagnostico dominio;
	
	public void setDominio(DominioDiagnostico dominio){
		this.dominio = dominio;
	}
	
	public DominioDiagnostico getDominio(){
		return this.dominio;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-richiesta",required=false,nillable=false)
	private String identificativoRichiesta;
	
	public void setIdentificativoRichiesta(String identificativoRichiesta){
		this.identificativoRichiesta = identificativoRichiesta;
	}
	
	public String getIdentificativoRichiesta(){
		return this.identificativoRichiesta;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-risposta",required=false,nillable=false)
	private String identificativoRisposta;
	
	public void setIdentificativoRisposta(String identificativoRisposta){
		this.identificativoRisposta = identificativoRisposta;
	}
	
	public String getIdentificativoRisposta(){
		return this.identificativoRisposta;
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
  @XmlElement(name="codice",required=false,nillable=false)
	private String codice;
	
	public void setCodice(String codice){
		this.codice = codice;
	}
	
	public String getCodice(){
		return this.codice;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="messaggio",required=false,nillable=false)
	private String messaggio;
	
	public void setMessaggio(String messaggio){
		this.messaggio = messaggio;
	}
	
	public String getMessaggio(){
		return this.messaggio;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="positiveInteger")
  @XmlElement(name="severita",required=false,nillable=false)
	private Integer severita;
	
	public void setSeverita(Integer severita){
		this.severita = severita;
	}
	
	public Integer getSeverita(){
		return this.severita;
	}
	
	
	@XmlElement(name="protocollo",required=false,nillable=false)
	private Protocollo protocollo;
	
	public void setProtocollo(Protocollo protocollo){
		this.protocollo = protocollo;
	}
	
	public Protocollo getProtocollo(){
		return this.protocollo;
	}
	
	
	@XmlElement(name="filtro-informazione-protocollo",required=false,nillable=false)
	private FiltroInformazioneProtocollo filtroInformazioneProtocollo;
	
	public void setFiltroInformazioneProtocollo(FiltroInformazioneProtocollo filtroInformazioneProtocollo){
		this.filtroInformazioneProtocollo = filtroInformazioneProtocollo;
	}
	
	public FiltroInformazioneProtocollo getFiltroInformazioneProtocollo(){
		return this.filtroInformazioneProtocollo;
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