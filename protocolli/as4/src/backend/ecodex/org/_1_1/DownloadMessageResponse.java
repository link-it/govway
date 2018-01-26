/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for downloadMessageResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="downloadMessageResponse">
 * 		&lt;sequence>
 * 			&lt;element name="bodyload" type="{http://org.ecodex.backend/1_1/}PayloadType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="payload" type="{http://org.ecodex.backend/1_1/}PayloadType" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "downloadMessageResponse", 
  propOrder = {
  	"bodyload",
  	"payload"
  }
)

@XmlRootElement(name = "downloadMessageResponse")

public class DownloadMessageResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DownloadMessageResponse() {
  }

  public PayloadType getBodyload() {
    return this.bodyload;
  }

  public void setBodyload(PayloadType bodyload) {
    this.bodyload = bodyload;
  }

  public void addPayload(PayloadType payload) {
    this.payload.add(payload);
  }

  public PayloadType getPayload(int index) {
    return this.payload.get( index );
  }

  public PayloadType removePayload(int index) {
    return this.payload.remove( index );
  }

  public List<PayloadType> getPayloadList() {
    return this.payload;
  }

  public void setPayloadList(List<PayloadType> payload) {
    this.payload=payload;
  }

  public int sizePayloadList() {
    return this.payload.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="bodyload",required=false,nillable=false)
  protected PayloadType bodyload;

  @XmlElement(name="payload",required=true,nillable=false)
  protected List<PayloadType> payload = new ArrayList<PayloadType>();

  /**
   * @deprecated Use method getPayloadList
   * @return List<PayloadType>
  */
  @Deprecated
  public List<PayloadType> getPayload() {
  	return this.payload;
  }

  /**
   * @deprecated Use method setPayloadList
   * @param payload List<PayloadType>
  */
  @Deprecated
  public void setPayload(List<PayloadType> payload) {
  	this.payload=payload;
  }

  /**
   * @deprecated Use method sizePayloadList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePayload() {
  	return this.payload.size();
  }

}
