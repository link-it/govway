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
package org.openspcoop2.core.api;

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
 * &lt;complexType name="header-parameters">
 * 		&lt;sequence>
 * 			&lt;element name="header-parameter" type="{http://www.openspcoop2.org/core/api}header-parameter" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
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
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public void addHeaderParameter(HeaderParameter headerParameter) {
    this.headerParameter.add(headerParameter);
  }

  public HeaderParameter getHeaderParameter(int index) {
    return this.headerParameter.get( index );
  }

  public HeaderParameter removeHeaderParameter(int index) {
    return this.headerParameter.remove( index );
  }

  public List<HeaderParameter> getHeaderParameterList() {
    return this.headerParameter;
  }

  public void setHeaderParameterList(List<HeaderParameter> headerParameter) {
    this.headerParameter=headerParameter;
  }

  public int sizeHeaderParameterList() {
    return this.headerParameter.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="header-parameter",required=true,nillable=false)
  protected List<HeaderParameter> headerParameter = new ArrayList<HeaderParameter>();

  /**
   * @deprecated Use method getHeaderParameterList
   * @return List<HeaderParameter>
  */
  @Deprecated
  public List<HeaderParameter> getHeaderParameter() {
  	return this.headerParameter;
  }

  /**
   * @deprecated Use method setHeaderParameterList
   * @param headerParameter List<HeaderParameter>
  */
  @Deprecated
  public void setHeaderParameter(List<HeaderParameter> headerParameter) {
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
