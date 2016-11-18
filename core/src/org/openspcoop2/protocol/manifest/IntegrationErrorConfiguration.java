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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for IntegrationErrorConfiguration complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationErrorConfiguration">
 * 		&lt;sequence>
 * 			&lt;element name="internal" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorCollection" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="external" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationErrorCollection" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "IntegrationErrorConfiguration", 
  propOrder = {
  	"internal",
  	"external"
  }
)

@XmlRootElement(name = "IntegrationErrorConfiguration")

public class IntegrationErrorConfiguration extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationErrorConfiguration() {
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

  public IntegrationErrorCollection getInternal() {
    return this.internal;
  }

  public void setInternal(IntegrationErrorCollection internal) {
    this.internal = internal;
  }

  public IntegrationErrorCollection getExternal() {
    return this.external;
  }

  public void setExternal(IntegrationErrorCollection external) {
    this.external = external;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="internal",required=true,nillable=false)
  protected IntegrationErrorCollection internal;

  @XmlElement(name="external",required=true,nillable=false)
  protected IntegrationErrorCollection external;

}
