/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for validazione-contenuti-applicativi-pattern-regola complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi-pattern-regola"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="regola" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="and" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/&gt;
 * 		&lt;attribute name="not" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validazione-contenuti-applicativi-pattern-regola", 
  propOrder = {
  	"regola"
  }
)

@XmlRootElement(name = "validazione-contenuti-applicativi-pattern-regola")

public class ValidazioneContenutiApplicativiPatternRegola extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ValidazioneContenutiApplicativiPatternRegola() {
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

  public java.lang.String getRegola() {
    return this.regola;
  }

  public void setRegola(java.lang.String regola) {
    this.regola = regola;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public boolean isAnd() {
    return this.and;
  }

  public boolean getAnd() {
    return this.and;
  }

  public void setAnd(boolean and) {
    this.and = and;
  }

  public boolean isNot() {
    return this.not;
  }

  public boolean getNot() {
    return this.not;
  }

  public void setNot(boolean not) {
    this.not = not;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="regola",required=true,nillable=false)
  protected java.lang.String regola;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="and",required=false)
  protected boolean and = true;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="not",required=false)
  protected boolean not = false;

}
