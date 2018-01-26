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


/** <p>Java class for receptionAwareness complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="receptionAwareness">
 * 		&lt;attribute name="name" type="{http://www.domibus.eu/configuration}string" use="required"/>
 * 		&lt;attribute name="retry" type="{http://www.domibus.eu/configuration}string" use="optional"/>
 * 		&lt;attribute name="duplicateDetection" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "receptionAwareness")

@XmlRootElement(name = "receptionAwareness")

public class ReceptionAwareness extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ReceptionAwareness() {
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getRetry() {
    return this.retry;
  }

  public void setRetry(java.lang.String retry) {
    this.retry = retry;
  }

  public java.lang.String getDuplicateDetection() {
    return this.duplicateDetection;
  }

  public void setDuplicateDetection(java.lang.String duplicateDetection) {
    this.duplicateDetection = duplicateDetection;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="retry",required=false)
  protected java.lang.String retry;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="duplicateDetection",required=false)
  protected java.lang.String duplicateDetection;

}
