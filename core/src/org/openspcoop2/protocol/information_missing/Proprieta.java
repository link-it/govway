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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for Proprieta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Proprieta">
 * 		&lt;sequence>
 * 			&lt;element name="header" type="{http://www.openspcoop2.org/protocol/information_missing}Description" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="footer" type="{http://www.openspcoop2.org/protocol/information_missing}Description" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="placeholder" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="use-in-delete" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="true"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Proprieta", 
  propOrder = {
  	"header",
  	"footer"
  }
)

@XmlRootElement(name = "Proprieta")

public class Proprieta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Proprieta() {
  }

  public Description getHeader() {
    return this.header;
  }

  public void setHeader(Description header) {
    this.header = header;
  }

  public Description getFooter() {
    return this.footer;
  }

  public void setFooter(Description footer) {
    this.footer = footer;
  }

  public java.lang.String getPlaceholder() {
    return this.placeholder;
  }

  public void setPlaceholder(java.lang.String placeholder) {
    this.placeholder = placeholder;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getDefault() {
    return this._default;
  }

  public void setDefault(java.lang.String _default) {
    this._default = _default;
  }

  public boolean isUseInDelete() {
    return this.useInDelete;
  }

  public boolean getUseInDelete() {
    return this.useInDelete;
  }

  public void setUseInDelete(boolean useInDelete) {
    this.useInDelete = useInDelete;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="header",required=false,nillable=false)
  protected Description header;

  @XmlElement(name="footer",required=false,nillable=false)
  protected Description footer;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="placeholder",required=true)
  protected java.lang.String placeholder;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="default",required=false)
  protected java.lang.String _default;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="use-in-delete",required=false)
  protected boolean useInDelete = true;

}
