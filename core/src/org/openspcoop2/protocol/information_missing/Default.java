/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for Default complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Default">
 * 		&lt;sequence>
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/protocol/information_missing}ProprietaDefault" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="valore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Default", 
  propOrder = {
  	"proprieta"
  }
)

@XmlRootElement(name = "Default")

public class Default extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Default() {
  }

  public void addProprieta(ProprietaDefault proprieta) {
    this.proprieta.add(proprieta);
  }

  public ProprietaDefault getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public ProprietaDefault removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<ProprietaDefault> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<ProprietaDefault> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
  }

  public java.lang.String getValore() {
    return this.valore;
  }

  public void setValore(java.lang.String valore) {
    this.valore = valore;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="proprieta",required=true,nillable=false)
  protected List<ProprietaDefault> proprieta = new ArrayList<ProprietaDefault>();

  /**
   * @deprecated Use method getProprietaList
   * @return List<ProprietaDefault>
  */
  @Deprecated
  public List<ProprietaDefault> getProprieta() {
  	return this.proprieta;
  }

  /**
   * @deprecated Use method setProprietaList
   * @param proprieta List<ProprietaDefault>
  */
  @Deprecated
  public void setProprieta(List<ProprietaDefault> proprieta) {
  	this.proprieta=proprieta;
  }

  /**
   * @deprecated Use method sizeProprietaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProprieta() {
  	return this.proprieta.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="valore",required=false)
  protected java.lang.String valore;

}
