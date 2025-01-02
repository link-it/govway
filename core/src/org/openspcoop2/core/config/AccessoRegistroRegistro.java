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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.RegistroTipo;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/** <p>Java class for accesso-registro-registro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accesso-registro-registro"&gt;
 * 		&lt;attribute name="tipo-database" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/config}RegistroTipo" use="optional" default="xml"/&gt;
 * 		&lt;attribute name="location" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="user" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="password" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
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

public class AccessoRegistroRegistro extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AccessoRegistroRegistro() {
    super();
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

  public Map<java.lang.String,String> getGenericPropertiesMap() {
    return this.genericProperties;
  }

  public void setGenericPropertiesMap(Map<java.lang.String,String> genericProperties) {
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

  public void setTipoRawEnumValue(String value) {
    this.tipo = (RegistroTipo) RegistroTipo.toEnumConstantFromString(value);
  }

  public String getTipoRawEnumValue() {
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



  private Map<java.lang.String,String> genericProperties = new HashMap<>();

  /**
   * Use method getGenericPropertiesMap
   * @return Map&lt;java.lang.String,String&gt;
  */
  public Map<java.lang.String,String> getGenericProperties() {
  	return this.getGenericPropertiesMap();
  }

  /**
   * Use method setGenericPropertiesMap
   * @param genericProperties Map&lt;java.lang.String,String&gt;
  */
  public void setGenericProperties(Map<java.lang.String,String> genericProperties) {
  	this.setGenericPropertiesMap(genericProperties);
  }

  /**
   * Use method sizeGenericPropertiesMap
   * @return lunghezza della mappa
  */
  public int sizeGenericProperties() {
  	return this.sizeGenericPropertiesMap();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-database",required=false)
  protected java.lang.String tipoDatabase;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoRawEnumValue;

  @XmlAttribute(name="tipo",required=false)
  protected RegistroTipo tipo = (RegistroTipo) RegistroTipo.toEnumConstantFromString("xml");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="location",required=true)
  protected java.lang.String location;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="user",required=false)
  protected java.lang.String user;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="password",required=false)
  protected java.lang.String password;

}
