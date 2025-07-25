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
package org.openspcoop2.core.tracciamento;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for protocollo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="protocollo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/core/tracciamento}proprieta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="identificativo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "protocollo", 
  propOrder = {
  	"proprieta"
  }
)

@XmlRootElement(name = "protocollo")

public class Protocollo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Protocollo() {
    super();
  }

  public void addProprieta(Proprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public Proprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public Proprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<Proprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<Proprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
  }

  public java.lang.String getIdentificativo() {
    return this.identificativo;
  }

  public void setIdentificativo(java.lang.String identificativo) {
    this.identificativo = identificativo;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="proprieta",required=true,nillable=false)
  private List<Proprieta> proprieta = new ArrayList<>();

  /**
   * Use method getProprietaList
   * @return List&lt;Proprieta&gt;
  */
  public List<Proprieta> getProprieta() {
  	return this.getProprietaList();
  }

  /**
   * Use method setProprietaList
   * @param proprieta List&lt;Proprieta&gt;
  */
  public void setProprieta(List<Proprieta> proprieta) {
  	this.setProprietaList(proprieta);
  }

  /**
   * Use method sizeProprietaList
   * @return lunghezza della lista
  */
  public int sizeProprieta() {
  	return this.sizeProprietaList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="identificativo",required=true)
  protected java.lang.String identificativo;

}
