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
import org.openspcoop2.protocol.manifest.constants.ServiceBinding;
import java.io.Serializable;


/** <p>Java class for binding complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="binding">
 * 		&lt;sequence>
 * 			&lt;element name="soap" type="{http://www.openspcoop2.org/protocol/manifest}SoapConfiguration" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="rest" type="{http://www.openspcoop2.org/protocol/manifest}RestConfiguration" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="default" type="{http://www.openspcoop2.org/protocol/manifest}ServiceBinding" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "binding", 
  propOrder = {
  	"soap",
  	"rest"
  }
)

@XmlRootElement(name = "binding")

public class Binding extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Binding() {
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

  public SoapConfiguration getSoap() {
    return this.soap;
  }

  public void setSoap(SoapConfiguration soap) {
    this.soap = soap;
  }

  public RestConfiguration getRest() {
    return this.rest;
  }

  public void setRest(RestConfiguration rest) {
    this.rest = rest;
  }

  public void set_value__default(String value) {
    this._default = (ServiceBinding) ServiceBinding.toEnumConstantFromString(value);
  }

  public String get_value__default() {
    if(this._default == null){
    	return null;
    }else{
    	return this._default.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.ServiceBinding getDefault() {
    return this._default;
  }

  public void setDefault(org.openspcoop2.protocol.manifest.constants.ServiceBinding _default) {
    this._default = _default;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="soap",required=false,nillable=false)
  protected SoapConfiguration soap;

  @XmlElement(name="rest",required=false,nillable=false)
  protected RestConfiguration rest;

  @XmlTransient
  protected java.lang.String _value__default;

  @XmlAttribute(name="default",required=false)
  protected ServiceBinding _default;

}
