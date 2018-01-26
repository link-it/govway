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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.RestMessageType;
import java.io.Serializable;


/** <p>Java class for RestMediaTypeMapping complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RestMediaTypeMapping">
 * 		<xsd:simpleContent>
 * 			<xsd:extension base="{http://www.w3.org/2001/XMLSchema}string">
 * 				&lt;attribute name="messageType" type="{http://www.openspcoop2.org/protocol/manifest}RestMessageType" use="required"/>
 * 				&lt;attribute name="regExpr" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 			</xsd:extension>
 * 		</xsd:simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RestMediaTypeMapping")

@XmlRootElement(name = "RestMediaTypeMapping")

public class RestMediaTypeMapping extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RestMediaTypeMapping() {
  }

  public String getBase() {
    if(this.base!=null && ("".equals(this.base)==false)){
		return this.base.trim();
	}else{
		return null;
	}

  }

  public void setBase(String base) {
    this.base=base;
  }

  public void set_value_messageType(String value) {
    this.messageType = (RestMessageType) RestMessageType.toEnumConstantFromString(value);
  }

  public String get_value_messageType() {
    if(this.messageType == null){
    	return null;
    }else{
    	return this.messageType.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.RestMessageType getMessageType() {
    return this.messageType;
  }

  public void setMessageType(org.openspcoop2.protocol.manifest.constants.RestMessageType messageType) {
    this.messageType = messageType;
  }

  public boolean isRegExpr() {
    return this.regExpr;
  }

  public boolean getRegExpr() {
    return this.regExpr;
  }

  public void setRegExpr(boolean regExpr) {
    this.regExpr = regExpr;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @javax.xml.bind.annotation.XmlValue()
  public String base;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_messageType;

  @XmlAttribute(name="messageType",required=true)
  protected RestMessageType messageType;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="regExpr",required=false)
  protected boolean regExpr = false;

}
