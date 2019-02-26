/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for serialized-parameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="serialized-parameter">
 * 		<xsd:simpleContent>
 * 			<xsd:extension base="{http://www.openspcoop2.org/message/context}byte[]">
 * 				&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 				&lt;attribute name="classe" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 			</xsd:extension>
 * 		</xsd:simpleContent>
 * &lt;/complexType>
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

public class SerializedParameter extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SerializedParameter() {
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

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlValue()
  public byte[] base;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="classe",required=true)
  protected java.lang.String classe;

}
