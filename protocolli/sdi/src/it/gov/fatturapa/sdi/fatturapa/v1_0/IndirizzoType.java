/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for IndirizzoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IndirizzoType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="Indirizzo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="NumeroCivico" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="CAP" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Comune" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Provincia" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Nazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}string" minOccurs="1" maxOccurs="1" default="IT"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
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
    super();
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



  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Indirizzo",required=true,nillable=false)
  protected java.lang.String indirizzo;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroCivico",required=false,nillable=false)
  protected java.lang.String numeroCivico;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="CAP",required=true,nillable=false)
  protected java.lang.String cap;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Comune",required=true,nillable=false)
  protected java.lang.String comune;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Provincia",required=false,nillable=false)
  protected java.lang.String provincia;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Nazione",required=true,nillable=false,defaultValue="IT")
  protected java.lang.String nazione = "IT";

}
