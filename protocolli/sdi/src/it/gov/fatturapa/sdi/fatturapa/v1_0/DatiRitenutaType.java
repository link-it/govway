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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.CausalePagamentoType;
import it.gov.fatturapa.sdi.fatturapa.v1_0.constants.TipoRitenutaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiRitenutaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiRitenutaType">
 * 		&lt;sequence>
 * 			&lt;element name="TipoRitenuta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}TipoRitenutaType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ImportoRitenuta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="AliquotaRitenuta" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="CausalePagamento" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}CausalePagamentoType" minOccurs="1" maxOccurs="1"/>
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

  public void set_value_tipoRitenuta(String value) {
    this.tipoRitenuta = (TipoRitenutaType) TipoRitenutaType.toEnumConstantFromString(value);
  }

  public String get_value_tipoRitenuta() {
    if(this.tipoRitenuta == null){
    	return null;
    }else{
    	return this.tipoRitenuta.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.TipoRitenutaType getTipoRitenuta() {
    return this.tipoRitenuta;
  }

  public void setTipoRitenuta(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.TipoRitenutaType tipoRitenuta) {
    this.tipoRitenuta = tipoRitenuta;
  }

  public java.lang.Double getImportoRitenuta() {
    if(this._decimalWrapper_importoRitenuta!=null){
		return (java.lang.Double) this._decimalWrapper_importoRitenuta.getObject(java.lang.Double.class);
	}else{
		return this.importoRitenuta;
	}
  }

  public void setImportoRitenuta(java.lang.Double importoRitenuta) {
    if(importoRitenuta!=null){
		this._decimalWrapper_importoRitenuta = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,importoRitenuta);
	}
  }

  public java.lang.Double getAliquotaRitenuta() {
    if(this._decimalWrapper_aliquotaRitenuta!=null){
		return (java.lang.Double) this._decimalWrapper_aliquotaRitenuta.getObject(java.lang.Double.class);
	}else{
		return this.aliquotaRitenuta;
	}
  }

  public void setAliquotaRitenuta(java.lang.Double aliquotaRitenuta) {
    if(aliquotaRitenuta!=null){
		this._decimalWrapper_aliquotaRitenuta = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,3,2,2,aliquotaRitenuta);
	}
  }

  public void set_value_causalePagamento(String value) {
    this.causalePagamento = (CausalePagamentoType) CausalePagamentoType.toEnumConstantFromString(value);
  }

  public String get_value_causalePagamento() {
    if(this.causalePagamento == null){
    	return null;
    }else{
    	return this.causalePagamento.toString();
    }
  }

  public it.gov.fatturapa.sdi.fatturapa.v1_0.constants.CausalePagamentoType getCausalePagamento() {
    return this.causalePagamento;
  }

  public void setCausalePagamento(it.gov.fatturapa.sdi.fatturapa.v1_0.constants.CausalePagamentoType causalePagamento) {
    this.causalePagamento = causalePagamento;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlTransient
  protected java.lang.String _value_tipoRitenuta;

  @XmlElement(name="TipoRitenuta",required=true,nillable=false)
  protected TipoRitenutaType tipoRitenuta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoRitenuta",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoRitenuta = null;

  @XmlTransient
  protected java.lang.Double importoRitenuta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="AliquotaRitenuta",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_aliquotaRitenuta = null;

  @XmlTransient
  protected java.lang.Double aliquotaRitenuta;

  @XmlTransient
  protected java.lang.String _value_causalePagamento;

  @XmlElement(name="CausalePagamento",required=true,nillable=false)
  protected CausalePagamentoType causalePagamento;

}
