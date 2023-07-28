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
package backend.ecodex.org._1_1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for LargePayloadType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LargePayloadType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="payloadId" type="{http://www.w3.org/2001/XMLSchema}token" use="required"/&gt;
 * 		&lt;attribute name="contentType" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LargePayloadType", 
  propOrder = {
  	"value"
  }
)

@XmlRootElement(name = "LargePayloadType")

public class LargePayloadType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public LargePayloadType() {
    super();
  }

  public byte[] getValue() {
    return this.value;
  }

  public void setValue(byte[] value) {
    this.value = value;
  }

  public java.lang.String getPayloadId() {
    return this.payloadId;
  }

  public void setPayloadId(java.lang.String payloadId) {
    this.payloadId = payloadId;
  }

  public java.lang.String getContentType() {
    return this.contentType;
  }

  public void setContentType(java.lang.String contentType) {
    this.contentType = contentType;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="value",required=true,nillable=false)
  protected byte[] value;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="token")
  @XmlAttribute(name="payloadId",required=true)
  protected java.lang.String payloadId;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="contentType",required=false)
  protected java.lang.String contentType;

}
