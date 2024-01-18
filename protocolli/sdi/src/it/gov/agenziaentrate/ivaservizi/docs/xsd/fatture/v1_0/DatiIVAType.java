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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiIVAType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiIVAType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="Imposta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}decimal" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="Aliquota" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.0}decimal" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiIVAType", 
  propOrder = {
  	"_decimalWrapper_imposta",
  	"_decimalWrapper_aliquota"
  }
)

@XmlRootElement(name = "DatiIVAType")

public class DatiIVAType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiIVAType() {
    super();
  }

  public java.math.BigDecimal getImposta() {
    if(this._decimalWrapper_imposta!=null){
		return (java.math.BigDecimal) this._decimalWrapper_imposta.getObject(java.math.BigDecimal.class);
	}else{
		return this.imposta;
	}
  }

  public void setImposta(java.math.BigDecimal imposta) {
    if(imposta!=null){
		this._decimalWrapper_imposta = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,imposta);
	}
  }

  public java.math.BigDecimal getAliquota() {
    if(this._decimalWrapper_aliquota!=null){
		return (java.math.BigDecimal) this._decimalWrapper_aliquota.getObject(java.math.BigDecimal.class);
	}else{
		return this.aliquota;
	}
  }

  public void setAliquota(java.math.BigDecimal aliquota) {
    if(aliquota!=null){
		this._decimalWrapper_aliquota = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,3,2,2,aliquota);
	}
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Imposta",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_imposta = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal imposta;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="Aliquota",required=false,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_aliquota = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal aliquota;

}
