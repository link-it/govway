/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for profilo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profilo">
 * 		&lt;attribute name="oneway" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * 		&lt;attribute name="sincrono" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * 		&lt;attribute name="asincronoAsimmetrico" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="asincronoSimmetrico" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profilo")

@XmlRootElement(name = "profilo")

public class Profilo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Profilo() {
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

  public boolean isOneway() {
    return this.oneway;
  }

  public boolean getOneway() {
    return this.oneway;
  }

  public void setOneway(boolean oneway) {
    this.oneway = oneway;
  }

  public boolean isSincrono() {
    return this.sincrono;
  }

  public boolean getSincrono() {
    return this.sincrono;
  }

  public void setSincrono(boolean sincrono) {
    this.sincrono = sincrono;
  }

  public boolean isAsincronoAsimmetrico() {
    return this.asincronoAsimmetrico;
  }

  public boolean getAsincronoAsimmetrico() {
    return this.asincronoAsimmetrico;
  }

  public void setAsincronoAsimmetrico(boolean asincronoAsimmetrico) {
    this.asincronoAsimmetrico = asincronoAsimmetrico;
  }

  public boolean isAsincronoSimmetrico() {
    return this.asincronoSimmetrico;
  }

  public boolean getAsincronoSimmetrico() {
    return this.asincronoSimmetrico;
  }

  public void setAsincronoSimmetrico(boolean asincronoSimmetrico) {
    this.asincronoSimmetrico = asincronoSimmetrico;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="oneway",required=false)
  protected boolean oneway = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="sincrono",required=false)
  protected boolean sincrono = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="asincronoAsimmetrico",required=false)
  protected boolean asincronoAsimmetrico = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="asincronoSimmetrico",required=false)
  protected boolean asincronoSimmetrico = false;

}
