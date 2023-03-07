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
package org.openspcoop2.message.context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for content-type-parameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="content-type-parameters"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="parameter" type="{http://www.openspcoop2.org/message/context}string-parameter" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "content-type-parameters", 
  propOrder = {
  	"parameter"
  }
)

@XmlRootElement(name = "content-type-parameters")

public class ContentTypeParameters extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ContentTypeParameters() {
    super();
  }

  public void addParameter(StringParameter parameter) {
    this.parameter.add(parameter);
  }

  public StringParameter getParameter(int index) {
    return this.parameter.get( index );
  }

  public StringParameter removeParameter(int index) {
    return this.parameter.remove( index );
  }

  public List<StringParameter> getParameterList() {
    return this.parameter;
  }

  public void setParameterList(List<StringParameter> parameter) {
    this.parameter=parameter;
  }

  public int sizeParameterList() {
    return this.parameter.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="parameter",required=true,nillable=false)
  protected List<StringParameter> parameter = new ArrayList<StringParameter>();

  /**
   * @deprecated Use method getParameterList
   * @return List&lt;StringParameter&gt;
  */
  @Deprecated
  public List<StringParameter> getParameter() {
  	return this.parameter;
  }

  /**
   * @deprecated Use method setParameterList
   * @param parameter List&lt;StringParameter&gt;
  */
  @Deprecated
  public void setParameter(List<StringParameter> parameter) {
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

}
