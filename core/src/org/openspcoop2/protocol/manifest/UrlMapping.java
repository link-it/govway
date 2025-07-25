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
package org.openspcoop2.protocol.manifest;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.manifest.constants.UrlMappingSourceType;
import java.io.Serializable;


/** <p>Java class for urlMapping complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="urlMapping"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="file" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="type" type="{http://www.openspcoop2.org/protocol/manifest}urlMappingSourceType" use="required"/&gt;
 * &lt;/complexType&gt;
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
  	"file"
  }
)

@XmlRootElement(name = "urlMapping")

public class UrlMapping extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public UrlMapping() {
    super();
  }

  public java.lang.String getFile() {
    return this.file;
  }

  public void setFile(java.lang.String file) {
    this.file = file;
  }

  public void setTypeRawEnumValue(String value) {
    this.type = (UrlMappingSourceType) UrlMappingSourceType.toEnumConstantFromString(value);
  }

  public String getTypeRawEnumValue() {
    if(this.type == null){
    	return null;
    }else{
    	return this.type.toString();
    }
  }

  public org.openspcoop2.protocol.manifest.constants.UrlMappingSourceType getType() {
    return this.type;
  }

  public void setType(org.openspcoop2.protocol.manifest.constants.UrlMappingSourceType type) {
    this.type = type;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="file",required=true,nillable=false)
  protected java.lang.String file;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String typeRawEnumValue;

  @XmlAttribute(name="type",required=true)
  protected UrlMappingSourceType type;

}
