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
package org.openspcoop2.core.allarmi;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for allarme-script complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allarme-script"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="invoca" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="invoca-warning" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="command" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="args" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "allarme-script", 
  propOrder = {
  	"invoca",
  	"invocaWarning",
  	"command",
  	"args"
  }
)

@XmlRootElement(name = "allarme-script")

public class AllarmeScript extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AllarmeScript() {
    super();
  }

  public java.lang.Integer getInvoca() {
    return this.invoca;
  }

  public void setInvoca(java.lang.Integer invoca) {
    this.invoca = invoca;
  }

  public java.lang.Integer getInvocaWarning() {
    return this.invocaWarning;
  }

  public void setInvocaWarning(java.lang.Integer invocaWarning) {
    this.invocaWarning = invocaWarning;
  }

  public java.lang.String getCommand() {
    return this.command;
  }

  public void setCommand(java.lang.String command) {
    this.command = command;
  }

  public java.lang.String getArgs() {
    return this.args;
  }

  public void setArgs(java.lang.String args) {
    this.args = args;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="invoca",required=false,nillable=false)
  protected java.lang.Integer invoca;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="invoca-warning",required=false,nillable=false)
  protected java.lang.Integer invocaWarning;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="command",required=false,nillable=false)
  protected java.lang.String command;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="args",required=false,nillable=false)
  protected java.lang.String args;

}
