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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for attribute-authority complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="attribute-authority"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="attributo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attribute-authority", 
  propOrder = {
  	"attributo"
  }
)

@XmlRootElement(name = "attribute-authority")

public class AttributeAuthority extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AttributeAuthority() {
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

  public void addAttributo(java.lang.String attributo) {
    this.attributo.add(attributo);
  }

  public java.lang.String getAttributo(int index) {
    return this.attributo.get( index );
  }

  public java.lang.String removeAttributo(int index) {
    return this.attributo.remove( index );
  }

  public List<java.lang.String> getAttributoList() {
    return this.attributo;
  }

  public void setAttributoList(List<java.lang.String> attributo) {
    this.attributo=attributo;
  }

  public int sizeAttributoList() {
    return this.attributo.size();
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



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="attributo",required=true,nillable=false)
  protected List<java.lang.String> attributo = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getAttributoList
   * @return List&lt;java.lang.String&gt;
  */
  @Deprecated
  public List<java.lang.String> getAttributo() {
  	return this.attributo;
  }

  /**
   * @deprecated Use method setAttributoList
   * @param attributo List&lt;java.lang.String&gt;
  */
  @Deprecated
  public void setAttributo(List<java.lang.String> attributo) {
  	this.attributo=attributo;
  }

  /**
   * @deprecated Use method sizeAttributoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAttributo() {
  	return this.attributo.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

}
