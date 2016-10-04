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
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for mtom-processor-flow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mtom-processor-flow">
 * 		&lt;sequence>
 * 			&lt;element name="parameter" type="{http://www.openspcoop2.org/core/config}mtom-processor-flow-parameter" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="mode" type="{http://www.openspcoop2.org/core/config}MTOMProcessorType" use="optional" default="disable"/>
 * &lt;/complexType>
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

public class MtomProcessorFlow extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MtomProcessorFlow() {
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

  public void set_value_mode(String value) {
    this.mode = (MTOMProcessorType) MTOMProcessorType.toEnumConstantFromString(value);
  }

  public String get_value_mode() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="parameter",required=true,nillable=false)
  protected List<MtomProcessorFlowParameter> parameter = new ArrayList<MtomProcessorFlowParameter>();

  /**
   * @deprecated Use method getParameterList
   * @return List<MtomProcessorFlowParameter>
  */
  @Deprecated
  public List<MtomProcessorFlowParameter> getParameter() {
  	return this.parameter;
  }

  /**
   * @deprecated Use method setParameterList
   * @param parameter List<MtomProcessorFlowParameter>
  */
  @Deprecated
  public void setParameter(List<MtomProcessorFlowParameter> parameter) {
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
  protected java.lang.String _value_mode;

  @XmlAttribute(name="mode",required=false)
  protected MTOMProcessorType mode = (MTOMProcessorType) MTOMProcessorType.toEnumConstantFromString("disable");

}
