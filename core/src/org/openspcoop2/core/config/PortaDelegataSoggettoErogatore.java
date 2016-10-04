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
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;
import java.io.Serializable;


/** <p>Java class for porta-delegata-soggetto-erogatore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-delegata-soggetto-erogatore">
 * 		&lt;attribute name="identificazione" type="{http://www.openspcoop2.org/core/config}PortaDelegataSoggettoErogatoreIdentificazione" use="optional" default="static"/>
 * 		&lt;attribute name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-delegata-soggetto-erogatore")

@XmlRootElement(name = "porta-delegata-soggetto-erogatore")

public class PortaDelegataSoggettoErogatore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PortaDelegataSoggettoErogatore() {
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

  public void set_value_identificazione(String value) {
    this.identificazione = (PortaDelegataSoggettoErogatoreIdentificazione) PortaDelegataSoggettoErogatoreIdentificazione.toEnumConstantFromString(value);
  }

  public String get_value_identificazione() {
    if(this.identificazione == null){
    	return null;
    }else{
    	return this.identificazione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione getIdentificazione() {
    return this.identificazione;
  }

  public void setIdentificazione(org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione identificazione) {
    this.identificazione = identificazione;
  }

  public java.lang.String getPattern() {
    return this.pattern;
  }

  public void setPattern(java.lang.String pattern) {
    this.pattern = pattern;
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



  @XmlTransient
  protected java.lang.String _value_identificazione;

  @XmlAttribute(name="identificazione",required=false)
  protected PortaDelegataSoggettoErogatoreIdentificazione identificazione = (PortaDelegataSoggettoErogatoreIdentificazione) PortaDelegataSoggettoErogatoreIdentificazione.toEnumConstantFromString("static");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="pattern",required=false)
  protected java.lang.String pattern;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=false)
  protected java.lang.String nome;

}
