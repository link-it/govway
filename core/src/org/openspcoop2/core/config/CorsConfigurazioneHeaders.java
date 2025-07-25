/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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


/** <p>Java class for cors-configurazione-headers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cors-configurazione-headers"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="header" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "cors-configurazione-headers", 
  propOrder = {
  	"header"
  }
)

@XmlRootElement(name = "cors-configurazione-headers")

public class CorsConfigurazioneHeaders extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public CorsConfigurazioneHeaders() {
    super();
  }

  public void addHeader(java.lang.String header) {
    this.header.add(header);
  }

  public java.lang.String getHeader(int index) {
    return this.header.get( index );
  }

  public java.lang.String removeHeader(int index) {
    return this.header.remove( index );
  }

  public List<java.lang.String> getHeaderList() {
    return this.header;
  }

  public void setHeaderList(List<java.lang.String> header) {
    this.header=header;
  }

  public int sizeHeaderList() {
    return this.header.size();
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="header",required=true,nillable=false)
  private List<java.lang.String> header = new ArrayList<>();

  /**
   * Use method getHeaderList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getHeader() {
  	return this.getHeaderList();
  }

  /**
   * Use method setHeaderList
   * @param header List&lt;java.lang.String&gt;
  */
  public void setHeader(List<java.lang.String> header) {
  	this.setHeaderList(header);
  }

  /**
   * Use method sizeHeaderList
   * @return lunghezza della lista
  */
  public int sizeHeader() {
  	return this.sizeHeaderList();
  }

}
