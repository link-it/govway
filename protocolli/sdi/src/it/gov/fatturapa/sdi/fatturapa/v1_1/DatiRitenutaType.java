/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.CausalePagamentoType;
import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoRitenutaType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiRitenutaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiRitenutaType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="TipoRitenuta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}TipoRitenutaType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ImportoRitenuta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="AliquotaRitenuta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="CausalePagamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}CausalePagamentoType" minOccurs="1" maxOccurs="1"/&gt;
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

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoRitenutaType getTipoRitenuta() {
    return this.tipoRitenuta;
  }

  public void setTipoRitenuta(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.TipoRitenutaType tipoRitenuta) {
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

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.CausalePagamentoType getCausalePagamento() {
    return this.causalePagamento;
  }

  public void setCausalePagamento(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.CausalePagamentoType causalePagamento) {
    this.causalePagamento = causalePagamento;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoRitenutaRawEnumValue;

  @XmlElement(name="TipoRitenuta",required=true,nillable=false)
  protected TipoRitenutaType tipoRitenuta;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoRitenuta",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoRitenuta = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal importoRitenuta;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AliquotaRitenuta",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_aliquotaRitenuta = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal aliquotaRitenuta;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String causalePagamentoRawEnumValue;

  @XmlElement(name="CausalePagamento",required=true,nillable=false)
  protected CausalePagamentoType causalePagamento;

}
