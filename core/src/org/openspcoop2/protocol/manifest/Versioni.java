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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for versioni complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="versioni">
 * 		&lt;sequence>
 * 			&lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "versioni", 
  propOrder = {
  	"versione"
  }
)

@XmlRootElement(name = "versioni")

public class Versioni extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Versioni() {
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

  public void addVersione(java.lang.String versione) {
    this.versione.add(versione);
  }

  public java.lang.String getVersione(int index) {
    return this.versione.get( index );
  }

  public java.lang.String removeVersione(int index) {
    return this.versione.remove( index );
  }

  public List<java.lang.String> getVersioneList() {
    return this.versione;
  }

  public void setVersioneList(List<java.lang.String> versione) {
    this.versione=versione;
  }

  public int sizeVersioneList() {
    return this.versione.size();
  }

  public java.lang.String getDefault() {
    return this._default;
  }

  public void setDefault(java.lang.String _default) {
    this._default = _default;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="versione",required=true,nillable=false)
  protected List<java.lang.String> versione = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getVersioneList
   * @return List<java.lang.String>
  */
  @Deprecated
  public List<java.lang.String> getVersione() {
  	return this.versione;
  }

  /**
   * @deprecated Use method setVersioneList
   * @param versione List<java.lang.String>
  */
  @Deprecated
  public void setVersione(List<java.lang.String> versione) {
  	this.versione=versione;
  }

  /**
   * @deprecated Use method sizeVersioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeVersione() {
  	return this.versione.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="default",required=false)
  protected java.lang.String _default;

}
