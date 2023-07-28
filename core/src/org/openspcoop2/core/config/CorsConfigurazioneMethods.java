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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
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



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="method",required=true,nillable=false)
  private List<java.lang.String> method = new ArrayList<>();

  /**
   * Use method getMethodList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getMethod() {
  	return this.getMethodList();
  }

  /**
   * Use method setMethodList
   * @param method List&lt;java.lang.String&gt;
  */
  public void setMethod(List<java.lang.String> method) {
  	this.setMethodList(method);
  }

  /**
   * Use method sizeMethodList
   * @return lunghezza della lista
  */
  public int sizeMethod() {
  	return this.sizeMethodList();
  }

}
