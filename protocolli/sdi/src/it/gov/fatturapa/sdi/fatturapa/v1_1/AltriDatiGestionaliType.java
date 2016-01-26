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


/** <p>Java class for AltriDatiGestionaliType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AltriDatiGestionaliType">
 * 		&lt;sequence>
 * 			&lt;element name="TipoDato" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="RiferimentoTesto" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="RiferimentoNumero" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="RiferimentoData" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "AltriDatiGestionaliType", 
  propOrder = {
  	"tipoDato",
  	"riferimentoTesto",
  	"_decimalWrapper_riferimentoNumero",
  	"riferimentoData"
  }
)

@XmlRootElement(name = "AltriDatiGestionaliType")

public class AltriDatiGestionaliType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AltriDatiGestionaliType() {
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

  public java.lang.String getTipoDato() {
    return this.tipoDato;
  }

  public void setTipoDato(java.lang.String tipoDato) {
    this.tipoDato = tipoDato;
  }

  public java.lang.String getRiferimentoTesto() {
    return this.riferimentoTesto;
  }

  public void setRiferimentoTesto(java.lang.String riferimentoTesto) {
    this.riferimentoTesto = riferimentoTesto;
  }

  public java.lang.Double getRiferimentoNumero() {
    if(this._decimalWrapper_riferimentoNumero!=null){
		return (java.lang.Double) this._decimalWrapper_riferimentoNumero.getObject(java.lang.Double.class);
	}else{
		return this.riferimentoNumero;
	}
  }

  public void setRiferimentoNumero(java.lang.Double riferimentoNumero) {
    if(riferimentoNumero!=null){
		this._decimalWrapper_riferimentoNumero = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,8,riferimentoNumero);
	}
  }

  public java.util.Date getRiferimentoData() {
    return this.riferimentoData;
  }

  public void setRiferimentoData(java.util.Date riferimentoData) {
    this.riferimentoData = riferimentoData;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="TipoDato",required=true,nillable=false)
  protected java.lang.String tipoDato;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="RiferimentoTesto",required=false,nillable=false)
  protected java.lang.String riferimentoTesto;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="RiferimentoNumero",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_riferimentoNumero = null;

  @XmlTransient
  protected java.lang.Double riferimentoNumero;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="RiferimentoData",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date riferimentoData;

}
