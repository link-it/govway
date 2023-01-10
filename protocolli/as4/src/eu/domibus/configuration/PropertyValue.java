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
package eu.domibus.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for PropertyValue complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyValue"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="url" type="{http://www.domibus.eu/configuration}PropertyValueUrl" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="header" type="{http://www.domibus.eu/configuration}PropertyValueHeader" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "PropertyValue", 
  propOrder = {
  	"url",
  	"header"
  }
)

@XmlRootElement(name = "PropertyValue")

public class PropertyValue extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PropertyValue() {
  }

  public PropertyValueUrl getUrl() {
    return this.url;
  }

  public void setUrl(PropertyValueUrl url) {
    this.url = url;
  }

  public PropertyValueHeader getHeader() {
    return this.header;
  }

  public void setHeader(PropertyValueHeader header) {
    this.header = header;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="url",required=false,nillable=false)
  protected PropertyValueUrl url;

  @XmlElement(name="header",required=false,nillable=false)
  protected PropertyValueHeader header;

}
