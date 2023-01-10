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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for url-parameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="url-parameters"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="url-parameter" type="{http://www.openspcoop2.org/message/context}string-parameter" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "url-parameters", 
  propOrder = {
  	"urlParameter"
  }
)

@XmlRootElement(name = "url-parameters")

public class UrlParameters extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public UrlParameters() {
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

  public void addUrlParameter(StringParameter urlParameter) {
    this.urlParameter.add(urlParameter);
  }

  public StringParameter getUrlParameter(int index) {
    return this.urlParameter.get( index );
  }

  public StringParameter removeUrlParameter(int index) {
    return this.urlParameter.remove( index );
  }

  public List<StringParameter> getUrlParameterList() {
    return this.urlParameter;
  }

  public void setUrlParameterList(List<StringParameter> urlParameter) {
    this.urlParameter=urlParameter;
  }

  public int sizeUrlParameterList() {
    return this.urlParameter.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="url-parameter",required=true,nillable=false)
  protected List<StringParameter> urlParameter = new ArrayList<StringParameter>();

  /**
   * @deprecated Use method getUrlParameterList
   * @return List&lt;StringParameter&gt;
  */
  @Deprecated
  public List<StringParameter> getUrlParameter() {
  	return this.urlParameter;
  }

  /**
   * @deprecated Use method setUrlParameterList
   * @param urlParameter List&lt;StringParameter&gt;
  */
  @Deprecated
  public void setUrlParameter(List<StringParameter> urlParameter) {
  	this.urlParameter=urlParameter;
  }

  /**
   * @deprecated Use method sizeUrlParameterList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeUrlParameter() {
  	return this.urlParameter.size();
  }

}
