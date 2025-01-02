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


/** <p>Java class for cors-configurazione-origin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cors-configurazione-origin"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="origin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "cors-configurazione-origin", 
  propOrder = {
  	"origin"
  }
)

@XmlRootElement(name = "cors-configurazione-origin")

public class CorsConfigurazioneOrigin extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public CorsConfigurazioneOrigin() {
    super();
  }

  public void addOrigin(java.lang.String origin) {
    this.origin.add(origin);
  }

  public java.lang.String getOrigin(int index) {
    return this.origin.get( index );
  }

  public java.lang.String removeOrigin(int index) {
    return this.origin.remove( index );
  }

  public List<java.lang.String> getOriginList() {
    return this.origin;
  }

  public void setOriginList(List<java.lang.String> origin) {
    this.origin=origin;
  }

  public int sizeOriginList() {
    return this.origin.size();
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="origin",required=true,nillable=false)
  private List<java.lang.String> origin = new ArrayList<>();

  /**
   * Use method getOriginList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getOrigin() {
  	return this.getOriginList();
  }

  /**
   * Use method setOriginList
   * @param origin List&lt;java.lang.String&gt;
  */
  public void setOrigin(List<java.lang.String> origin) {
  	this.setOriginList(origin);
  }

  /**
   * Use method sizeOriginList
   * @return lunghezza della lista
  */
  public int sizeOrigin() {
  	return this.sizeOriginList();
  }

}
