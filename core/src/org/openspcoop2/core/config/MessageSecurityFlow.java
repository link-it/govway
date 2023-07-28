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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for message-security-flow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="message-security-flow"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="parameter" type="{http://www.openspcoop2.org/core/config}message-security-flow-parameter" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="apply-to-mtom" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="mode" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="default"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "message-security-flow", 
  propOrder = {
  	"parameter"
  }
)

@XmlRootElement(name = "message-security-flow")

public class MessageSecurityFlow extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public MessageSecurityFlow() {
    super();
  }

  public void addParameter(MessageSecurityFlowParameter parameter) {
    this.parameter.add(parameter);
  }

  public MessageSecurityFlowParameter getParameter(int index) {
    return this.parameter.get( index );
  }

  public MessageSecurityFlowParameter removeParameter(int index) {
    return this.parameter.remove( index );
  }

  public List<MessageSecurityFlowParameter> getParameterList() {
    return this.parameter;
  }

  public void setParameterList(List<MessageSecurityFlowParameter> parameter) {
    this.parameter=parameter;
  }

  public int sizeParameterList() {
    return this.parameter.size();
  }

  public void setApplyToMtomRawEnumValue(String value) {
    this.applyToMtom = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getApplyToMtomRawEnumValue() {
    if(this.applyToMtom == null){
    	return null;
    }else{
    	return this.applyToMtom.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getApplyToMtom() {
    return this.applyToMtom;
  }

  public void setApplyToMtom(org.openspcoop2.core.config.constants.StatoFunzionalita applyToMtom) {
    this.applyToMtom = applyToMtom;
  }

  public java.lang.String getMode() {
    return this.mode;
  }

  public void setMode(java.lang.String mode) {
    this.mode = mode;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="parameter",required=true,nillable=false)
  private List<MessageSecurityFlowParameter> parameter = new ArrayList<>();

  /**
   * Use method getParameterList
   * @return List&lt;MessageSecurityFlowParameter&gt;
  */
  public List<MessageSecurityFlowParameter> getParameter() {
  	return this.getParameterList();
  }

  /**
   * Use method setParameterList
   * @param parameter List&lt;MessageSecurityFlowParameter&gt;
  */
  public void setParameter(List<MessageSecurityFlowParameter> parameter) {
  	this.setParameterList(parameter);
  }

  /**
   * Use method sizeParameterList
   * @return lunghezza della lista
  */
  public int sizeParameter() {
  	return this.sizeParameterList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String applyToMtomRawEnumValue;

  @XmlAttribute(name="apply-to-mtom",required=false)
  protected StatoFunzionalita applyToMtom;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="mode",required=false)
  protected java.lang.String mode = "default";

}
