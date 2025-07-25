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
package org.openspcoop2.core.controllo_traffico;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import java.io.Serializable;


/** <p>Java class for id-active-policy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="id-active-policy"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="posizione" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="continua-valutazione" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="id-policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="update-time" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="alias" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filtro-ruolo-porta" type="{http://www.openspcoop2.org/core/controllo_traffico}ruolo-policy" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "id-active-policy", 
  propOrder = {
  	"nome",
  	"posizione",
  	"continuaValutazione",
  	"idPolicy",
  	"enabled",
  	"updateTime",
  	"alias",
  	"filtroRuoloPorta",
  	"filtroNomePorta"
  }
)

@XmlRootElement(name = "id-active-policy")

public class IdActivePolicy extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public IdActivePolicy() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public int getPosizione() {
    return this.posizione;
  }

  public void setPosizione(int posizione) {
    this.posizione = posizione;
  }

  public boolean isContinuaValutazione() {
    return this.continuaValutazione;
  }

  public boolean getContinuaValutazione() {
    return this.continuaValutazione;
  }

  public void setContinuaValutazione(boolean continuaValutazione) {
    this.continuaValutazione = continuaValutazione;
  }

  public java.lang.String getIdPolicy() {
    return this.idPolicy;
  }

  public void setIdPolicy(java.lang.String idPolicy) {
    this.idPolicy = idPolicy;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public java.util.Date getUpdateTime() {
    return this.updateTime;
  }

  public void setUpdateTime(java.util.Date updateTime) {
    this.updateTime = updateTime;
  }

  public java.lang.String getAlias() {
    return this.alias;
  }

  public void setAlias(java.lang.String alias) {
    this.alias = alias;
  }

  public void setFiltroRuoloPortaRawEnumValue(String value) {
    this.filtroRuoloPorta = (RuoloPolicy) RuoloPolicy.toEnumConstantFromString(value);
  }

  public String getFiltroRuoloPortaRawEnumValue() {
    if(this.filtroRuoloPorta == null){
    	return null;
    }else{
    	return this.filtroRuoloPorta.toString();
    }
  }

  public org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy getFiltroRuoloPorta() {
    return this.filtroRuoloPorta;
  }

  public void setFiltroRuoloPorta(org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy filtroRuoloPorta) {
    this.filtroRuoloPorta = filtroRuoloPorta;
  }

  public java.lang.String getFiltroNomePorta() {
    return this.filtroNomePorta;
  }

  public void setFiltroNomePorta(java.lang.String filtroNomePorta) {
    this.filtroNomePorta = filtroNomePorta;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlElement(name="posizione",required=false,nillable=false)
  protected int posizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="continua-valutazione",required=false,nillable=false)
  protected boolean continuaValutazione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="id-policy",required=false,nillable=false)
  protected java.lang.String idPolicy;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="enabled",required=false,nillable=false)
  protected boolean enabled;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="update-time",required=false,nillable=false,type=java.lang.String.class)
  protected java.util.Date updateTime;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="alias",required=false,nillable=false)
  protected java.lang.String alias;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String filtroRuoloPortaRawEnumValue;

  @XmlElement(name="filtro-ruolo-porta",required=false,nillable=false)
  protected RuoloPolicy filtroRuoloPorta;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-nome-porta",required=false,nillable=false)
  protected java.lang.String filtroNomePorta;

}
