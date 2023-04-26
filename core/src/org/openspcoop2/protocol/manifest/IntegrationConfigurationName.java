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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for IntegrationConfigurationName complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationConfigurationName"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="param" type="{http://www.openspcoop2.org/protocol/manifest}IntegrationConfigurationElementName" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="useInUrl" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IntegrationConfigurationName", 
  propOrder = {
  	"param"
  }
)

@XmlRootElement(name = "IntegrationConfigurationName")

public class IntegrationConfigurationName extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IntegrationConfigurationName() {
    super();
  }

  public void addParam(IntegrationConfigurationElementName param) {
    this.param.add(param);
  }

  public IntegrationConfigurationElementName getParam(int index) {
    return this.param.get( index );
  }

  public IntegrationConfigurationElementName removeParam(int index) {
    return this.param.remove( index );
  }

  public List<IntegrationConfigurationElementName> getParamList() {
    return this.param;
  }

  public void setParamList(List<IntegrationConfigurationElementName> param) {
    this.param=param;
  }

  public int sizeParamList() {
    return this.param.size();
  }

  public boolean isUseInUrl() {
    return this.useInUrl;
  }

  public boolean getUseInUrl() {
    return this.useInUrl;
  }

  public void setUseInUrl(boolean useInUrl) {
    this.useInUrl = useInUrl;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="param",required=true,nillable=false)
  private List<IntegrationConfigurationElementName> param = new ArrayList<>();

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="useInUrl",required=false)
  protected boolean useInUrl = true;

}
