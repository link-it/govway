/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for message-security-flow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="message-security-flow">
 * 		&lt;sequence>
 * 			&lt;element name="parameter" type="{http://www.openspcoop2.org/core/config}message-security-flow-parameter" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="apply-to-mtom" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/>
 * &lt;/complexType>
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

public class MessageSecurityFlow extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MessageSecurityFlow() {
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

  public void set_value_applyToMtom(String value) {
    this.applyToMtom = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_applyToMtom() {
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="parameter",required=true,nillable=false)
  protected List<MessageSecurityFlowParameter> parameter = new ArrayList<MessageSecurityFlowParameter>();

  /**
   * @deprecated Use method getParameterList
   * @return List<MessageSecurityFlowParameter>
  */
  @Deprecated
  public List<MessageSecurityFlowParameter> getParameter() {
  	return this.parameter;
  }

  /**
   * @deprecated Use method setParameterList
   * @param parameter List<MessageSecurityFlowParameter>
  */
  @Deprecated
  public void setParameter(List<MessageSecurityFlowParameter> parameter) {
  	this.parameter=parameter;
  }

  /**
   * @deprecated Use method sizeParameterList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeParameter() {
  	return this.parameter.size();
  }

  @XmlTransient
  protected java.lang.String _value_applyToMtom;

  @XmlAttribute(name="apply-to-mtom",required=false)
  protected StatoFunzionalita applyToMtom;

}
