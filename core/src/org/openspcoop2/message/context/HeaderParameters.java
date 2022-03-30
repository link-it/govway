/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for header-parameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="header-parameters"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="header-parameter" type="{http://www.openspcoop2.org/message/context}string-parameter" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "header-parameters", 
  propOrder = {
  	"headerParameter"
  }
)

@XmlRootElement(name = "header-parameters")

public class HeaderParameters extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public HeaderParameters() {
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

  public void addHeaderParameter(StringParameter headerParameter) {
    this.headerParameter.add(headerParameter);
  }

  public StringParameter getHeaderParameter(int index) {
    return this.headerParameter.get( index );
  }

  public StringParameter removeHeaderParameter(int index) {
    return this.headerParameter.remove( index );
  }

  public List<StringParameter> getHeaderParameterList() {
    return this.headerParameter;
  }

  public void setHeaderParameterList(List<StringParameter> headerParameter) {
    this.headerParameter=headerParameter;
  }

  public int sizeHeaderParameterList() {
    return this.headerParameter.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="header-parameter",required=true,nillable=false)
  protected List<StringParameter> headerParameter = new ArrayList<StringParameter>();

  /**
   * @deprecated Use method getHeaderParameterList
   * @return List<StringParameter>
  */
  @Deprecated
  public List<StringParameter> getHeaderParameter() {
  	return this.headerParameter;
  }

  /**
   * @deprecated Use method setHeaderParameterList
   * @param headerParameter List<StringParameter>
  */
  @Deprecated
  public void setHeaderParameter(List<StringParameter> headerParameter) {
  	this.headerParameter=headerParameter;
  }

  /**
   * @deprecated Use method sizeHeaderParameterList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeHeaderParameter() {
  	return this.headerParameter.size();
  }

}
