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
package it.gov.spcoop.sica.wsbl;

import it.gov.spcoop.sica.wsbl.constants.ActivationType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for transitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transitionType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="activationMode" type="{http://spcoop.gov.it/sica/wsbl}activationType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="completionMode" type="{http://spcoop.gov.it/sica/wsbl}completionModeType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="guard" type="{http://spcoop.gov.it/sica/wsbl}guardType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="events" type="{http://spcoop.gov.it/sica/wsbl}eventListType" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="temporalCondition" type="{http://spcoop.gov.it/sica/wsbl}temporalConditionType" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="name" type="{http://spcoop.gov.it/sica/wsbl}string" use="required"/&gt;
 * 		&lt;attribute name="source" type="{http://spcoop.gov.it/sica/wsbl}string" use="required"/&gt;
 * 		&lt;attribute name="target" type="{http://spcoop.gov.it/sica/wsbl}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transitionType", 
  propOrder = {
  	"activationMode",
  	"completionMode",
  	"guard",
  	"events",
  	"temporalCondition"
  }
)

@XmlRootElement(name = "transitionType")

public class TransitionType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TransitionType() {
  }

  public void set_value_activationMode(String value) {
    this.activationMode = (ActivationType) ActivationType.toEnumConstantFromString(value);
  }

  public String get_value_activationMode() {
    if(this.activationMode == null){
    	return null;
    }else{
    	return this.activationMode.toString();
    }
  }

  public it.gov.spcoop.sica.wsbl.constants.ActivationType getActivationMode() {
    return this.activationMode;
  }

  public void setActivationMode(it.gov.spcoop.sica.wsbl.constants.ActivationType activationMode) {
    this.activationMode = activationMode;
  }

  public CompletionModeType getCompletionMode() {
    return this.completionMode;
  }

  public void setCompletionMode(CompletionModeType completionMode) {
    this.completionMode = completionMode;
  }

  public GuardType getGuard() {
    return this.guard;
  }

  public void setGuard(GuardType guard) {
    this.guard = guard;
  }

  public EventListType getEvents() {
    return this.events;
  }

  public void setEvents(EventListType events) {
    this.events = events;
  }

  public TemporalConditionType getTemporalCondition() {
    return this.temporalCondition;
  }

  public void setTemporalCondition(TemporalConditionType temporalCondition) {
    this.temporalCondition = temporalCondition;
  }

  public java.lang.String getName() {
    return this.name;
  }

  public void setName(java.lang.String name) {
    this.name = name;
  }

  public java.lang.String getSource() {
    return this.source;
  }

  public void setSource(java.lang.String source) {
    this.source = source;
  }

  public java.lang.String getTarget() {
    return this.target;
  }

  public void setTarget(java.lang.String target) {
    this.target = target;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_activationMode;

  @XmlElement(name="activationMode",required=true,nillable=false)
  protected ActivationType activationMode;

  @XmlElement(name="completionMode",required=true,nillable=false)
  protected CompletionModeType completionMode;

  @XmlElement(name="guard",required=false,nillable=false)
  protected GuardType guard;

  @XmlElement(name="events",required=false,nillable=false)
  protected EventListType events;

  @XmlElement(name="temporalCondition",required=false,nillable=false)
  protected TemporalConditionType temporalCondition;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="name",required=true)
  protected java.lang.String name;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="source",required=true)
  protected java.lang.String source;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="target",required=true)
  protected java.lang.String target;

}
