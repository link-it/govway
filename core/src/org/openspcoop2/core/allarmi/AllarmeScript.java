/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for allarme-script complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allarme-script"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="ack-mode" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="invoca-warning" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="invoca-alert" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
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
  	"ackMode",
  	"invocaWarning",
  	"invocaAlert",
  	"command",
  	"args"
  }
)

@XmlRootElement(name = "allarme-script")

public class AllarmeScript extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AllarmeScript() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public java.lang.Integer getAckMode() {
    return this.ackMode;
  }

  public void setAckMode(java.lang.Integer ackMode) {
    this.ackMode = ackMode;
  }

  public java.lang.Integer getInvocaWarning() {
    return this.invocaWarning;
  }

  public void setInvocaWarning(java.lang.Integer invocaWarning) {
    this.invocaWarning = invocaWarning;
  }

  public java.lang.Integer getInvocaAlert() {
    return this.invocaAlert;
  }

  public void setInvocaAlert(java.lang.Integer invocaAlert) {
    this.invocaAlert = invocaAlert;
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

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="ack-mode",required=false,nillable=false)
  protected java.lang.Integer ackMode;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="invoca-warning",required=false,nillable=false)
  protected java.lang.Integer invocaWarning;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="invoca-alert",required=false,nillable=false)
  protected java.lang.Integer invocaAlert;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="command",required=false,nillable=false)
  protected java.lang.String command;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="args",required=false,nillable=false)
  protected java.lang.String args;

}
