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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.constants.BindingUse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for message complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="message">
 * 		&lt;sequence>
 * 			&lt;element name="part" type="{http://www.openspcoop2.org/core/registry}message-part" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="use" type="{http://www.openspcoop2.org/core/registry}BindingUse" use="optional" default="literal"/>
 * 		&lt;attribute name="soap-namespace" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "message", 
  propOrder = {
  	"part"
  }
)

@XmlRootElement(name = "message")

public class Message extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Message() {
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

  public void addPart(MessagePart part) {
    this.part.add(part);
  }

  public MessagePart getPart(int index) {
    return this.part.get( index );
  }

  public MessagePart removePart(int index) {
    return this.part.remove( index );
  }

  public List<MessagePart> getPartList() {
    return this.part;
  }

  public void setPartList(List<MessagePart> part) {
    this.part=part;
  }

  public int sizePartList() {
    return this.part.size();
  }

  public void set_value_use(String value) {
    this.use = (BindingUse) BindingUse.toEnumConstantFromString(value);
  }

  public String get_value_use() {
    if(this.use == null){
    	return null;
    }else{
    	return this.use.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.BindingUse getUse() {
    return this.use;
  }

  public void setUse(org.openspcoop2.core.registry.constants.BindingUse use) {
    this.use = use;
  }

  public java.lang.String getSoapNamespace() {
    return this.soapNamespace;
  }

  public void setSoapNamespace(java.lang.String soapNamespace) {
    this.soapNamespace = soapNamespace;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="part",required=true,nillable=false)
  protected List<MessagePart> part = new ArrayList<MessagePart>();

  /**
   * @deprecated Use method getPartList
   * @return List<MessagePart>
  */
  @Deprecated
  public List<MessagePart> getPart() {
  	return this.part;
  }

  /**
   * @deprecated Use method setPartList
   * @param part List<MessagePart>
  */
  @Deprecated
  public void setPart(List<MessagePart> part) {
  	this.part=part;
  }

  /**
   * @deprecated Use method sizePartList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePart() {
  	return this.part.size();
  }

  @XmlTransient
  protected java.lang.String _value_use;

  @XmlAttribute(name="use",required=false)
  protected BindingUse use = (BindingUse) BindingUse.toEnumConstantFromString("literal");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="soap-namespace",required=false)
  protected java.lang.String soapNamespace;

}
