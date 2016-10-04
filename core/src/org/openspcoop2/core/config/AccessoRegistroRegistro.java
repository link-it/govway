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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.RegistroTipo;
import java.io.Serializable;
import java.util.Hashtable;


/** <p>Java class for accesso-registro-registro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accesso-registro-registro">
 * 		&lt;attribute name="tipo-database" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/config}RegistroTipo" use="optional" default="xml"/>
 * 		&lt;attribute name="location" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="password" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accesso-registro-registro")

@XmlRootElement(name = "accesso-registro-registro")

public class AccessoRegistroRegistro extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccessoRegistroRegistro() {
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

  public void putGenericProperties(java.lang.String key, String value) {
    this.genericProperties.put(key,value);
  }

  public String getGenericProperties(String key) {
    return this.genericProperties.get( key );
  }

  public String removeGenericProperties(String key) {
    return this.genericProperties.remove( key );
  }

  public Hashtable<java.lang.String,String> getGenericPropertiesMap() {
    return this.genericProperties;
  }

  public void setGenericPropertiesMap(Hashtable<java.lang.String,String> genericProperties) {
    this.genericProperties=genericProperties;
  }

  public int sizeGenericPropertiesMap() {
    return this.genericProperties.size();
  }

  public java.lang.String getTipoDatabase() {
    return this.tipoDatabase;
  }

  public void setTipoDatabase(java.lang.String tipoDatabase) {
    this.tipoDatabase = tipoDatabase;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void set_value_tipo(String value) {
    this.tipo = (RegistroTipo) RegistroTipo.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.RegistroTipo getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.config.constants.RegistroTipo tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getLocation() {
    return this.location;
  }

  public void setLocation(java.lang.String location) {
    this.location = location;
  }

  public java.lang.String getUser() {
    return this.user;
  }

  public void setUser(java.lang.String user) {
    this.user = user;
  }

  public java.lang.String getPassword() {
    return this.password;
  }

  public void setPassword(java.lang.String password) {
    this.password = password;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  protected Hashtable<java.lang.String,String> genericProperties = new Hashtable<java.lang.String,String>();

  /**
   * @deprecated Use method getGenericPropertiesMap
   * @return Hashtable<java.lang.String,String>
  */
  @Deprecated
  public Hashtable<java.lang.String,String> getGenericProperties() {
  	return this.genericProperties;
  }

  /**
   * @deprecated Use method setGenericPropertiesMap
   * @param genericProperties Hashtable<java.lang.String,String>
  */
  @Deprecated
  public void setGenericProperties(Hashtable<java.lang.String,String> genericProperties) {
  	this.genericProperties=genericProperties;
  }

  /**
   * @deprecated Use method sizeGenericPropertiesMap
   * @return lunghezza della mappa
  */
  @Deprecated
  public int sizeGenericProperties() {
  	return this.genericProperties.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-database",required=false)
  protected java.lang.String tipoDatabase;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=false)
  protected RegistroTipo tipo = (RegistroTipo) RegistroTipo.toEnumConstantFromString("xml");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="location",required=true)
  protected java.lang.String location;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="user",required=false)
  protected java.lang.String user;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="password",required=false)
  protected java.lang.String password;

}
