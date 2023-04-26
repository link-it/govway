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

public class UrlParameters extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public UrlParameters() {
    super();
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



  @XmlElement(name="url-parameter",required=true,nillable=false)
  private List<StringParameter> urlParameter = new ArrayList<>();

}
