/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.engine.config.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import java.io.Serializable;


/** <p>Java class for id-plugin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-plugin">
 * 		&lt;sequence>
 * 			&lt;element name="tipo" type="{http://www.openspcoop2.org/monitor/engine/config/base}tipo-plugin" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="class-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "id-plugin", 
  propOrder = {
  	"tipo",
  	"className"
  }
)

@XmlRootElement(name = "id-plugin")

public class IdPlugin extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IdPlugin() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public void set_value_tipo(String value) {
    this.tipo = (TipoPlugin) TipoPlugin.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getClassName() {
    return this.className;
  }

  public void setClassName(java.lang.String className) {
    this.className = className;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipo;

  @XmlElement(name="tipo",required=true,nillable=false)
  protected TipoPlugin tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="class-name",required=true,nillable=false)
  protected java.lang.String className;

}
