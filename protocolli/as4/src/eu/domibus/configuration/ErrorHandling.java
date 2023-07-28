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
package eu.domibus.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for errorHandling complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="errorHandling"&gt;
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/&gt;
 * 		&lt;attribute name="errorAsResponse" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * 		&lt;attribute name="businessErrorNotifyProducer" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * 		&lt;attribute name="businessErrorNotifyConsumer" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * 		&lt;attribute name="deliveryFailureNotifyProducer" type="{http://www.w3.org/2001/XMLSchema}boolean" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "errorHandling")

@XmlRootElement(name = "errorHandling")

public class ErrorHandling extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ErrorHandling() {
    super();
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public boolean isErrorAsResponse() {
    return this.errorAsResponse;
  }

  public boolean getErrorAsResponse() {
    return this.errorAsResponse;
  }

  public void setErrorAsResponse(boolean errorAsResponse) {
    this.errorAsResponse = errorAsResponse;
  }

  public boolean isBusinessErrorNotifyProducer() {
    return this.businessErrorNotifyProducer;
  }

  public boolean getBusinessErrorNotifyProducer() {
    return this.businessErrorNotifyProducer;
  }

  public void setBusinessErrorNotifyProducer(boolean businessErrorNotifyProducer) {
    this.businessErrorNotifyProducer = businessErrorNotifyProducer;
  }

  public boolean isBusinessErrorNotifyConsumer() {
    return this.businessErrorNotifyConsumer;
  }

  public boolean getBusinessErrorNotifyConsumer() {
    return this.businessErrorNotifyConsumer;
  }

  public void setBusinessErrorNotifyConsumer(boolean businessErrorNotifyConsumer) {
    this.businessErrorNotifyConsumer = businessErrorNotifyConsumer;
  }

  public boolean isDeliveryFailureNotifyProducer() {
    return this.deliveryFailureNotifyProducer;
  }

  public boolean getDeliveryFailureNotifyProducer() {
    return this.deliveryFailureNotifyProducer;
  }

  public void setDeliveryFailureNotifyProducer(boolean deliveryFailureNotifyProducer) {
    this.deliveryFailureNotifyProducer = deliveryFailureNotifyProducer;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="errorAsResponse",required=true)
  protected boolean errorAsResponse;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="businessErrorNotifyProducer",required=true)
  protected boolean businessErrorNotifyProducer;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="businessErrorNotifyConsumer",required=true)
  protected boolean businessErrorNotifyConsumer;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="deliveryFailureNotifyProducer",required=true)
  protected boolean deliveryFailureNotifyProducer;

}
