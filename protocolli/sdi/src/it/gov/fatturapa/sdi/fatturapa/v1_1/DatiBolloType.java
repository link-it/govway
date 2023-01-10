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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import it.gov.fatturapa.sdi.fatturapa.v1_1.constants.BolloVirtualeType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiBolloType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiBolloType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="BolloVirtuale" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}BolloVirtualeType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ImportoBollo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}decimal" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "DatiBolloType", 
  propOrder = {
  	"bolloVirtuale",
  	"_decimalWrapper_importoBollo"
  }
)

@XmlRootElement(name = "DatiBolloType")

public class DatiBolloType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiBolloType() {
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

  public it.gov.fatturapa.sdi.fatturapa.v1_1.constants.BolloVirtualeType getBolloVirtuale() {
    return this.bolloVirtuale;
  }

  public void setBolloVirtuale(it.gov.fatturapa.sdi.fatturapa.v1_1.constants.BolloVirtualeType bolloVirtuale) {
    this.bolloVirtuale = bolloVirtuale;
  }

  public java.math.BigDecimal getImportoBollo() {
    if(this._decimalWrapper_importoBollo!=null){
		return (java.math.BigDecimal) this._decimalWrapper_importoBollo.getObject(java.math.BigDecimal.class);
	}else{
		return this.importoBollo;
	}
  }

  public void setImportoBollo(java.math.BigDecimal importoBollo) {
    if(importoBollo!=null){
		this._decimalWrapper_importoBollo = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,importoBollo);
	}
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_bolloVirtuale;

  @XmlElement(name="BolloVirtuale",required=true,nillable=false)
  protected BolloVirtualeType bolloVirtuale;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoBollo",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoBollo = null;

  @javax.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal importoBollo;

}
