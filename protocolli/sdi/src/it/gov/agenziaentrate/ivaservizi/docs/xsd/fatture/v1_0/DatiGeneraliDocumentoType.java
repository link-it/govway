/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.BolloVirtualeType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.TipoDocumentoType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiGeneraliDocumentoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiGeneraliDocumentoType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="TipoDocumento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}TipoDocumentoType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Divisa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Data" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}date" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="Numero" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="BolloVirtuale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}BolloVirtualeType" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiGeneraliDocumentoType", 
  propOrder = {
  	"tipoDocumento",
  	"divisa",
  	"data",
  	"numero",
  	"bolloVirtuale"
  }
)

@XmlRootElement(name = "DatiGeneraliDocumentoType")

public class DatiGeneraliDocumentoType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiGeneraliDocumentoType() {
  }

  public void set_value_tipoDocumento(String value) {
    this.tipoDocumento = (TipoDocumentoType) TipoDocumentoType.toEnumConstantFromString(value);
  }

  public String get_value_tipoDocumento() {
    if(this.tipoDocumento == null){
    	return null;
    }else{
    	return this.tipoDocumento.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.TipoDocumentoType getTipoDocumento() {
    return this.tipoDocumento;
  }

  public void setTipoDocumento(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.TipoDocumentoType tipoDocumento) {
    this.tipoDocumento = tipoDocumento;
  }

  public java.lang.String getDivisa() {
    return this.divisa;
  }

  public void setDivisa(java.lang.String divisa) {
    this.divisa = divisa;
  }

  public java.util.Date getData() {
    return this.data;
  }

  public void setData(java.util.Date data) {
    this.data = data;
  }

  public java.lang.String getNumero() {
    return this.numero;
  }

  public void setNumero(java.lang.String numero) {
    this.numero = numero;
  }

  public void set_value_bolloVirtuale(String value) {
    this.bolloVirtuale = (BolloVirtualeType) BolloVirtualeType.toEnumConstantFromString(value);
  }

  public String get_value_bolloVirtuale() {
    if(this.bolloVirtuale == null){
    	return null;
    }else{
    	return this.bolloVirtuale.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.BolloVirtualeType getBolloVirtuale() {
    return this.bolloVirtuale;
  }

  public void setBolloVirtuale(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.constants.BolloVirtualeType bolloVirtuale) {
    this.bolloVirtuale = bolloVirtuale;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoDocumento;

  @XmlElement(name="TipoDocumento",required=true,nillable=false)
  protected TipoDocumentoType tipoDocumento;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Divisa",required=true,nillable=false)
  protected java.lang.String divisa;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Date2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="date")
  @XmlElement(name="Data",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date data;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Numero",required=true,nillable=false)
  protected java.lang.String numero;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_bolloVirtuale;

  @XmlElement(name="BolloVirtuale",required=false,nillable=false)
  protected BolloVirtualeType bolloVirtuale;

}
