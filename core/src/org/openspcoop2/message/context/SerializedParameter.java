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
package org.openspcoop2.message.context;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for serialized-parameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="serialized-parameter"&gt;
 * 		&lt;xsd:simpleContent&gt;
 * 			&lt;xsd:extension base="{http://www.openspcoop2.org/message/context}byte[]"&gt;
 * 				&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 				&lt;attribute name="classe" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 			&lt;/xsd:extension&gt;
 * 		&lt;/xsd:simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serialized-parameter")

@XmlRootElement(name = "serialized-parameter")

public class SerializedParameter extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public SerializedParameter() {
    super();
  }

  public byte[] getBase() {
    return this.base;
  }

  public void setBase(byte[] base) {
    this.base=base;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getClasse() {
    return this.classe;
  }

  public void setClasse(java.lang.String classe) {
    this.classe = classe;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlValue()
  public byte[] base;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="classe",required=true)
  protected java.lang.String classe;

}
