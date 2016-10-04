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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.UrlMappingSourceType;
import java.io.Serializable;


/** <p>Java class for urlMapping complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="urlMapping">
 * 		&lt;sequence>
 * 			&lt;element name="file" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="tipo" type="{http://www.openspcoop2.org/protocol/manifest}urlMappingSourceType" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "urlMapping", 
  propOrder = {
  	"file",
  	"tipo"
  }
)

@XmlRootElement(name = "urlMapping")

public class UrlMapping extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public UrlMapping() {
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

  public java.lang.String getFile() {
    return this.file;
  }

  public void setFile(java.lang.String file) {
    this.file = file;
  }

  public void set_value_tipo(String value) {
    this.tipo = (UrlMappingSourceType) UrlMappingSourceType.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.UrlMappingSourceType getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.protocol.manifest.constants.UrlMappingSourceType tipo) {
    this.tipo = tipo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="file",required=true,nillable=false)
  protected java.lang.String file;

  @XmlTransient
  protected java.lang.String _value_tipo;

  @XmlElement(name="tipo",required=true,nillable=false)
  protected UrlMappingSourceType tipo;

}
