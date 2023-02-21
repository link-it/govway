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
package org.openspcoop2.core.allarmi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import java.io.Serializable;


/** <p>Java class for id-allarme complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-allarme"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="alias" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-ruolo-porta" type="{http://www.openspcoop2.org/core/allarmi}ruolo-porta" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "id-allarme", 
  propOrder = {
  	"nome",
  	"tipo",
  	"enabled",
  	"alias",
  	"filtroRuoloPorta",
  	"filtroNomePorta"
  }
)

@XmlRootElement(name = "id-allarme")

public class IdAllarme extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public IdAllarme() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.Integer getEnabled() {
    return this.enabled;
  }

  public void setEnabled(java.lang.Integer enabled) {
    this.enabled = enabled;
  }

  public java.lang.String getAlias() {
    return this.alias;
  }

  public void setAlias(java.lang.String alias) {
    this.alias = alias;
  }

  public void set_value_filtroRuoloPorta(String value) {
    this.filtroRuoloPorta = (RuoloPorta) RuoloPorta.toEnumConstantFromString(value);
  }

  public String get_value_filtroRuoloPorta() {
    if(this.filtroRuoloPorta == null){
    	return null;
    }else{
    	return this.filtroRuoloPorta.toString();
    }
  }

  public org.openspcoop2.core.allarmi.constants.RuoloPorta getFiltroRuoloPorta() {
    return this.filtroRuoloPorta;
  }

  public void setFiltroRuoloPorta(org.openspcoop2.core.allarmi.constants.RuoloPorta filtroRuoloPorta) {
    this.filtroRuoloPorta = filtroRuoloPorta;
  }

  public java.lang.String getFiltroNomePorta() {
    return this.filtroNomePorta;
  }

  public void setFiltroNomePorta(java.lang.String filtroNomePorta) {
    this.filtroNomePorta = filtroNomePorta;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=false,nillable=false)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="enabled",required=false,nillable=false)
  protected java.lang.Integer enabled;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="alias",required=false,nillable=false)
  protected java.lang.String alias;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_filtroRuoloPorta;

  @XmlElement(name="filtro-ruolo-porta",required=false,nillable=false)
  protected RuoloPorta filtroRuoloPorta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-porta",required=false,nillable=false)
  protected java.lang.String filtroNomePorta;

}
