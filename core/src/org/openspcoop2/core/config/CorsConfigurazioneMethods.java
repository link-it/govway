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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for cors-configurazione-methods complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cors-configurazione-methods"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="method" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "cors-configurazione-methods", 
  propOrder = {
  	"method"
  }
)

@XmlRootElement(name = "cors-configurazione-methods")

public class CorsConfigurazioneMethods extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public CorsConfigurazioneMethods() {
    super();
  }

  public void addMethod(java.lang.String method) {
    this.method.add(method);
  }

  public java.lang.String getMethod(int index) {
    return this.method.get( index );
  }

  public java.lang.String removeMethod(int index) {
    return this.method.remove( index );
  }

  public List<java.lang.String> getMethodList() {
    return this.method;
  }

  public void setMethodList(List<java.lang.String> method) {
    this.method=method;
  }

  public int sizeMethodList() {
    return this.method.size();
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="method",required=true,nillable=false)
  protected List<java.lang.String> method = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getMethodList
   * @return List&lt;java.lang.String&gt;
  */
  @Deprecated
  public List<java.lang.String> getMethod() {
  	return this.method;
  }

  /**
   * @deprecated Use method setMethodList
   * @param method List&lt;java.lang.String&gt;
  */
  @Deprecated
  public void setMethod(List<java.lang.String> method) {
  	this.method=method;
  }

  /**
   * @deprecated Use method sizeMethodList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeMethod() {
  	return this.method.size();
  }

}
