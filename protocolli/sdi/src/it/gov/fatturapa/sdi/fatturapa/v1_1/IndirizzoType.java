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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for IndirizzoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IndirizzoType">
 * 		&lt;sequence>
 * 			&lt;element name="Indirizzo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="NumeroCivico" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CAP" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Comune" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Provincia" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Nazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="1" maxOccurs="1" default="IT"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IndirizzoType", 
  propOrder = {
  	"indirizzo",
  	"numeroCivico",
  	"cap",
  	"comune",
  	"provincia",
  	"nazione"
  }
)

@XmlRootElement(name = "IndirizzoType")

public class IndirizzoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IndirizzoType() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public java.lang.String getIndirizzo() {
    return this.indirizzo;
  }

  public void setIndirizzo(java.lang.String indirizzo) {
    this.indirizzo = indirizzo;
  }

  public java.lang.String getNumeroCivico() {
    return this.numeroCivico;
  }

  public void setNumeroCivico(java.lang.String numeroCivico) {
    this.numeroCivico = numeroCivico;
  }

  public java.lang.String getCap() {
    return this.cap;
  }

  public void setCap(java.lang.String cap) {
    this.cap = cap;
  }

  public java.lang.String getComune() {
    return this.comune;
  }

  public void setComune(java.lang.String comune) {
    this.comune = comune;
  }

  public java.lang.String getProvincia() {
    return this.provincia;
  }

  public void setProvincia(java.lang.String provincia) {
    this.provincia = provincia;
  }

  public java.lang.String getNazione() {
    return this.nazione;
  }

  public void setNazione(java.lang.String nazione) {
    this.nazione = nazione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Indirizzo",required=true,nillable=false)
  protected java.lang.String indirizzo;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroCivico",required=false,nillable=false)
  protected java.lang.String numeroCivico;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="CAP",required=true,nillable=false)
  protected java.lang.String cap;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Comune",required=true,nillable=false)
  protected java.lang.String comune;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Provincia",required=false,nillable=false)
  protected java.lang.String provincia;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Nazione",required=true,nillable=false,defaultValue="IT")
  protected java.lang.String nazione = "IT";

}
