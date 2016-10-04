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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for DatiBolloType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiBolloType">
 * 		&lt;sequence>
 * 			&lt;element name="NumeroBollo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ImportoBollo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}decimal" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "DatiBolloType", 
  propOrder = {
  	"numeroBollo",
  	"_decimalWrapper_importoBollo"
  }
)

@XmlRootElement(name = "DatiBolloType")

public class DatiBolloType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiBolloType() {
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

  public java.lang.String getNumeroBollo() {
    return this.numeroBollo;
  }

  public void setNumeroBollo(java.lang.String numeroBollo) {
    this.numeroBollo = numeroBollo;
  }

  public java.lang.Double getImportoBollo() {
    if(this._decimalWrapper_importoBollo!=null){
		return (java.lang.Double) this._decimalWrapper_importoBollo.getObject(java.lang.Double.class);
	}else{
		return this.importoBollo;
	}
  }

  public void setImportoBollo(java.lang.Double importoBollo) {
    if(importoBollo!=null){
		this._decimalWrapper_importoBollo = new org.openspcoop2.utils.jaxb.DecimalWrapper(1,11,2,2,importoBollo);
	}
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="NumeroBollo",required=true,nillable=false)
  protected java.lang.String numeroBollo;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.Decimal2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="decimal")
  @XmlElement(name="ImportoBollo",required=true,nillable=false)
  org.openspcoop2.utils.jaxb.DecimalWrapper _decimalWrapper_importoBollo = null;

  @XmlTransient
  protected java.lang.Double importoBollo;

}
