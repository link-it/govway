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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.AlgoritmoCache;
import java.io.Serializable;


/** <p>Java class for cache complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cache"&gt;
 * 		&lt;attribute name="dimensione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="algoritmo" type="{http://www.openspcoop2.org/core/config}AlgoritmoCache" use="optional" default="lru"/&gt;
 * 		&lt;attribute name="item-idle-time" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="item-life-second" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cache")

@XmlRootElement(name = "cache")

public class Cache extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Cache() {
    super();
  }

  public java.lang.String getDimensione() {
    return this.dimensione;
  }

  public void setDimensione(java.lang.String dimensione) {
    this.dimensione = dimensione;
  }

  public void setAlgoritmoRawEnumValue(String value) {
    this.algoritmo = (AlgoritmoCache) AlgoritmoCache.toEnumConstantFromString(value);
  }

  public String getAlgoritmoRawEnumValue() {
    if(this.algoritmo == null){
    	return null;
    }else{
    	return this.algoritmo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.AlgoritmoCache getAlgoritmo() {
    return this.algoritmo;
  }

  public void setAlgoritmo(org.openspcoop2.core.config.constants.AlgoritmoCache algoritmo) {
    this.algoritmo = algoritmo;
  }

  public java.lang.String getItemIdleTime() {
    return this.itemIdleTime;
  }

  public void setItemIdleTime(java.lang.String itemIdleTime) {
    this.itemIdleTime = itemIdleTime;
  }

  public java.lang.String getItemLifeSecond() {
    return this.itemLifeSecond;
  }

  public void setItemLifeSecond(java.lang.String itemLifeSecond) {
    this.itemLifeSecond = itemLifeSecond;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="dimensione",required=false)
  protected java.lang.String dimensione;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String algoritmoRawEnumValue;

  @XmlAttribute(name="algoritmo",required=false)
  protected AlgoritmoCache algoritmo = (AlgoritmoCache) AlgoritmoCache.toEnumConstantFromString("lru");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="item-idle-time",required=false)
  protected java.lang.String itemIdleTime;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="item-life-second",required=false)
  protected java.lang.String itemLifeSecond;

}
