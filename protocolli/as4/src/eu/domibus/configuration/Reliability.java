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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for reliability complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reliability">
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/>
 * 		&lt;attribute name="replyPattern" type="{http://www.domibus.eu/configuration}string" use="required"/>
 * 		&lt;attribute name="nonRepudiation" type="{http://www.domibus.eu/configuration}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reliability")

@XmlRootElement(name = "reliability")

public class Reliability extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Reliability() {
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getReplyPattern() {
    return this.replyPattern;
  }

  public void setReplyPattern(java.lang.String replyPattern) {
    this.replyPattern = replyPattern;
  }

  public java.lang.String getNonRepudiation() {
    return this.nonRepudiation;
  }

  public void setNonRepudiation(java.lang.String nonRepudiation) {
    this.nonRepudiation = nonRepudiation;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="replyPattern",required=true)
  protected java.lang.String replyPattern;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nonRepudiation",required=true)
  protected java.lang.String nonRepudiation;

}
