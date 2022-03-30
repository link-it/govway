/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for Integration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Integration"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="implementation" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationConfiguration" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="subscription" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationConfiguration" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "Integration", 
  propOrder = {
  	"implementation",
  	"subscription"
  }
)

@XmlRootElement(name = "Integration")

public class Integration extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Integration() {
  }

  public IntegrationConfiguration getImplementation() {
    return this.implementation;
  }

  public void setImplementation(IntegrationConfiguration implementation) {
    this.implementation = implementation;
  }

  public IntegrationConfiguration getSubscription() {
    return this.subscription;
  }

  public void setSubscription(IntegrationConfiguration subscription) {
    this.subscription = subscription;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="implementation",required=true,nillable=false)
  protected IntegrationConfiguration implementation;

  @XmlElement(name="subscription",required=true,nillable=false)
  protected IntegrationConfiguration subscription;

}
