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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiBolloType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiBolloType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="NumeroBollo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="ImportoBollo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/&gt;
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
  	"numeroBollo",
  	"_decimalWrapper_importoBollo"
  }
)

@XmlRootElement(name = "DatiBolloType")

public class DatiBolloType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiBolloType() {
    super();
  }

  public java.lang.String getNumeroBollo() {
    return this.numeroBollo;
  }

  public void setNumeroBollo(java.lang.String numeroBollo) {
    this.numeroBollo = numeroBollo;
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



  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroBollo",required=true,nillable=false)
  protected java.lang.String numeroBollo;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoBollo",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoBollo = null;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.math.BigDecimal importoBollo;

}
