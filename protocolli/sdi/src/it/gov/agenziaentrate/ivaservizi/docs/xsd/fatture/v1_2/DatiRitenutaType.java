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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.CausalePagamentoType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.TipoRitenutaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiRitenutaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiRitenutaType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="TipoRitenuta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}TipoRitenutaType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ImportoRitenuta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="AliquotaRitenuta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="CausalePagamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}CausalePagamentoType" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiRitenutaType", 
  propOrder = {
  	"tipoRitenuta",
  	"_decimalWrapper_importoRitenuta",
  	"_decimalWrapper_aliquotaRitenuta",
  	"causalePagamento"
  }
)

@XmlRootElement(name = "DatiRitenutaType")

public class DatiRitenutaType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiRitenutaType() {
    super();
  }

  public void setTipoRitenutaRawEnumValue(String value) {
    this.tipoRitenuta = (TipoRitenutaType) TipoRitenutaType.toEnumConstantFromString(value);
  }

  public String getTipoRitenutaRawEnumValue() {
    if(this.tipoRitenuta == null){
    	return null;
    }else{
    	return this.tipoRitenuta.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.TipoRitenutaType getTipoRitenuta() {
    return this.tipoRitenuta;
  }

  public void setTipoRitenuta(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.TipoRitenutaType tipoRitenuta) {
    this.tipoRitenuta = tipoRitenuta;
  }

  public java.math.BigDecimal getImportoRitenuta() {
    if(this._decimalWrapper_importoRitenuta!=null){
		return (java.math.BigDecimal) this._decimalWrapper_importoRitenuta.getObject(java.math.BigDecimal.class);
	}else{
		return this.importoRitenuta;
	}
  }

  public void setImportoRitenuta(java.math.BigDecimal importoRitenuta) {
    if(importoRitenuta!=null){
		this._decimalWrapper_importoRitenuta = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,importoRitenuta);
	}
  }

  public java.math.BigDecimal getAliquotaRitenuta() {
    if(this._decimalWrapper_aliquotaRitenuta!=null){
		return (java.math.BigDecimal) this._decimalWrapper_aliquotaRitenuta.getObject(java.math.BigDecimal.class);
	}else{
		return this.aliquotaRitenuta;
	}
  }

  public void setAliquotaRitenuta(java.math.BigDecimal aliquotaRitenuta) {
    if(aliquotaRitenuta!=null){
		this._decimalWrapper_aliquotaRitenuta = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,3,2,2,aliquotaRitenuta);
	}
  }

  public void setCausalePagamentoRawEnumValue(String value) {
    this.causalePagamento = (CausalePagamentoType) CausalePagamentoType.toEnumConstantFromString(value);
  }

  public String getCausalePagamentoRawEnumValue() {
    if(this.causalePagamento == null){
    	return null;
    }else{
    	return this.causalePagamento.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.CausalePagamentoType getCausalePagamento() {
    return this.causalePagamento;
  }

  public void setCausalePagamento(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.CausalePagamentoType causalePagamento) {
    this.causalePagamento = causalePagamento;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoRitenutaRawEnumValue;

  @XmlElement(name="TipoRitenuta",required=true,nillable=false)
  protected TipoRitenutaType tipoRitenuta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoRitenuta",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoRitenuta = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal importoRitenuta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AliquotaRitenuta",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_aliquotaRitenuta = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal aliquotaRitenuta;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String causalePagamentoRawEnumValue;

  @XmlElement(name="CausalePagamento",required=true,nillable=false)
  protected CausalePagamentoType causalePagamento;

}
