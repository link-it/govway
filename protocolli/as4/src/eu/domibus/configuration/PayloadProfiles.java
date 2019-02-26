/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for payloadProfiles complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="payloadProfiles">
 * 		&lt;sequence>
 * 			&lt;element name="payload" type="{http://www.domibus.eu/configuration}payload" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="payloadProfile" type="{http://www.domibus.eu/configuration}payloadProfile" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "payloadProfiles", 
  propOrder = {
  	"payload",
  	"payloadProfile"
  }
)

@XmlRootElement(name = "payloadProfiles")

public class PayloadProfiles extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PayloadProfiles() {
  }

  public void addPayload(Payload payload) {
    this.payload.add(payload);
  }

  public Payload getPayload(int index) {
    return this.payload.get( index );
  }

  public Payload removePayload(int index) {
    return this.payload.remove( index );
  }

  public List<Payload> getPayloadList() {
    return this.payload;
  }

  public void setPayloadList(List<Payload> payload) {
    this.payload=payload;
  }

  public int sizePayloadList() {
    return this.payload.size();
  }

  public void addPayloadProfile(PayloadProfile payloadProfile) {
    this.payloadProfile.add(payloadProfile);
  }

  public PayloadProfile getPayloadProfile(int index) {
    return this.payloadProfile.get( index );
  }

  public PayloadProfile removePayloadProfile(int index) {
    return this.payloadProfile.remove( index );
  }

  public List<PayloadProfile> getPayloadProfileList() {
    return this.payloadProfile;
  }

  public void setPayloadProfileList(List<PayloadProfile> payloadProfile) {
    this.payloadProfile=payloadProfile;
  }

  public int sizePayloadProfileList() {
    return this.payloadProfile.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="payload",required=true,nillable=false)
  protected List<Payload> payload = new ArrayList<Payload>();

  /**
   * @deprecated Use method getPayloadList
   * @return List<Payload>
  */
  @Deprecated
  public List<Payload> getPayload() {
  	return this.payload;
  }

  /**
   * @deprecated Use method setPayloadList
   * @param payload List<Payload>
  */
  @Deprecated
  public void setPayload(List<Payload> payload) {
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

  @XmlElement(name="payloadProfile",required=true,nillable=false)
  protected List<PayloadProfile> payloadProfile = new ArrayList<PayloadProfile>();

  /**
   * @deprecated Use method getPayloadProfileList
   * @return List<PayloadProfile>
  */
  @Deprecated
  public List<PayloadProfile> getPayloadProfile() {
  	return this.payloadProfile;
  }

  /**
   * @deprecated Use method setPayloadProfileList
   * @param payloadProfile List<PayloadProfile>
  */
  @Deprecated
  public void setPayloadProfile(List<PayloadProfile> payloadProfile) {
  	this.payloadProfile=payloadProfile;
  }

  /**
   * @deprecated Use method sizePayloadProfileList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePayloadProfile() {
  	return this.payloadProfile.size();
  }

}
