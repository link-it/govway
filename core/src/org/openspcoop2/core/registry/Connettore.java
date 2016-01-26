/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/** <p>Java class for connettore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="connettore">
 * 		&lt;sequence>
 * 			&lt;element name="property" type="{http://www.openspcoop2.org/core/registry}Property" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="custom" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="false"/>
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "connettore", 
  propOrder = {
  	"property"
  }
)

@XmlRootElement(name = "connettore")

public class Connettore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Connettore() {
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

  public void addProperty(Property property) {
    this.property.add(property);
  }

  public Property getProperty(int index) {
    return this.property.get( index );
  }

  public Property removeProperty(int index) {
    return this.property.remove( index );
  }

  public List<Property> getPropertyList() {
    return this.property;
  }

  public void setPropertyList(List<Property> property) {
    this.property=property;
  }

  public int sizePropertyList() {
    return this.property.size();
  }

  public Boolean getCustom() {
    return this.custom;
  }

  public void setCustom(Boolean custom) {
    this.custom = custom;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  public org.openspcoop2.core.config.Connettore mappingIntoConnettoreConfigurazione(){
  	org.openspcoop2.core.config.Connettore connettoreConfig = new org.openspcoop2.core.config.Connettore();
  	for(int i=0; i<this.property.size();i++){
  		org.openspcoop2.core.config.Property property = new org.openspcoop2.core.config.Property();
  		property.setNome(this.property.get(i).getNome());
  		property.setValore(this.property.get(i).getValore());
  		connettoreConfig.addProperty(property);
  	}
  	connettoreConfig.setId(this.id);
  	connettoreConfig.setNome(this.nome);
  	connettoreConfig.setTipo(this.tipo);
  	return connettoreConfig;
  }

  public Map<String,String> getProperties(){
  	Map<String, String> map = new HashMap<String, String>();
  	Iterator<Property> it = this.property.iterator();
  	while (it!=null && it.hasNext()){
  		Property elem = it.next();
  		map.put(elem.getNome(), elem.getValore());
  	}
  	return map;
  }

  public void setProperties(Map<String,String> newmap){
  	this.property.clear();
  	Set<String> keys = newmap.keySet();
  	Iterator<String> it = keys.iterator();
  	while(it.hasNext()){
  		String key = it.next();
  		Property cp = new Property();
  		cp.setNome(key);
  		cp.setValore(newmap.get(key));
  		this.property.add(cp);
  	}
  }

  @XmlElement(name="property",required=true,nillable=false)
  protected List<Property> property = new ArrayList<Property>();

  /**
   * @deprecated Use method getPropertyList
   * @return List<Property>
  */
  @Deprecated
  public List<Property> getProperty() {
  	return this.property;
  }

  /**
   * @deprecated Use method setPropertyList
   * @param property List<Property>
  */
  @Deprecated
  public void setProperty(List<Property> property) {
  	this.property=property;
  }

  /**
   * @deprecated Use method sizePropertyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProperty() {
  	return this.property.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="custom",required=false)
  protected Boolean custom = new Boolean("false");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=false)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

}
