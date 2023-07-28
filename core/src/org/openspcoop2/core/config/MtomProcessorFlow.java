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
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for mtom-processor-flow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mtom-processor-flow"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="parameter" type="{http://www.openspcoop2.org/core/config}mtom-processor-flow-parameter" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="mode" type="{http://www.openspcoop2.org/core/config}MTOMProcessorType" use="optional" default="disable"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mtom-processor-flow", 
  propOrder = {
  	"parameter"
  }
)

@XmlRootElement(name = "mtom-processor-flow")

public class MtomProcessorFlow extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public MtomProcessorFlow() {
    super();
  }

  public void addParameter(MtomProcessorFlowParameter parameter) {
    this.parameter.add(parameter);
  }

  public MtomProcessorFlowParameter getParameter(int index) {
    return this.parameter.get( index );
  }

  public MtomProcessorFlowParameter removeParameter(int index) {
    return this.parameter.remove( index );
  }

  public List<MtomProcessorFlowParameter> getParameterList() {
    return this.parameter;
  }

  public void setParameterList(List<MtomProcessorFlowParameter> parameter) {
    this.parameter=parameter;
  }

  public int sizeParameterList() {
    return this.parameter.size();
  }

  public void setModeRawEnumValue(String value) {
    this.mode = (MTOMProcessorType) MTOMProcessorType.toEnumConstantFromString(value);
  }

  public String getModeRawEnumValue() {
    if(this.mode == null){
    	return null;
    }else{
    	return this.mode.toString();
    }
  }

  public org.openspcoop2.core.config.constants.MTOMProcessorType getMode() {
    return this.mode;
  }

  public void setMode(org.openspcoop2.core.config.constants.MTOMProcessorType mode) {
    this.mode = mode;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="parameter",required=true,nillable=false)
  private List<MtomProcessorFlowParameter> parameter = new ArrayList<>();

  /**
   * Use method getParameterList
   * @return List&lt;MtomProcessorFlowParameter&gt;
  */
  public List<MtomProcessorFlowParameter> getParameter() {
  	return this.getParameterList();
  }

  /**
   * Use method setParameterList
   * @param parameter List&lt;MtomProcessorFlowParameter&gt;
  */
  public void setParameter(List<MtomProcessorFlowParameter> parameter) {
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
  protected java.lang.String modeRawEnumValue;

  @XmlAttribute(name="mode",required=false)
  protected MTOMProcessorType mode = (MTOMProcessorType) MTOMProcessorType.toEnumConstantFromString("disable");

}
