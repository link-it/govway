/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
import java.io.Serializable;


/** <p>Java class for CollaborationProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CollaborationProfile">
 * 		&lt;attribute name="oneway" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * 		&lt;attribute name="inputOutput" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * 		&lt;attribute name="asyncInputOutput" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="polledInputOutput" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollaborationProfile")

@XmlRootElement(name = "CollaborationProfile")

public class CollaborationProfile extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CollaborationProfile() {
  }

  public boolean isOneway() {
    return this.oneway;
  }

  public boolean getOneway() {
    return this.oneway;
  }

  public void setOneway(boolean oneway) {
    this.oneway = oneway;
  }

  public boolean isInputOutput() {
    return this.inputOutput;
  }

  public boolean getInputOutput() {
    return this.inputOutput;
  }

  public void setInputOutput(boolean inputOutput) {
    this.inputOutput = inputOutput;
  }

  public boolean isAsyncInputOutput() {
    return this.asyncInputOutput;
  }

  public boolean getAsyncInputOutput() {
    return this.asyncInputOutput;
  }

  public void setAsyncInputOutput(boolean asyncInputOutput) {
    this.asyncInputOutput = asyncInputOutput;
  }

  public boolean isPolledInputOutput() {
    return this.polledInputOutput;
  }

  public boolean getPolledInputOutput() {
    return this.polledInputOutput;
  }

  public void setPolledInputOutput(boolean polledInputOutput) {
    this.polledInputOutput = polledInputOutput;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="oneway",required=false)
  protected boolean oneway = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="inputOutput",required=false)
  protected boolean inputOutput = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="asyncInputOutput",required=false)
  protected boolean asyncInputOutput = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="polledInputOutput",required=false)
  protected boolean polledInputOutput = false;

}
