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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.information_missing.constants.ReplaceKeywordType;
import java.io.Serializable;


/** <p>Java class for ReplaceMatchFieldType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReplaceMatchFieldType">
 * 		&lt;attribute name="valore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/protocol/information_missing}ReplaceKeywordType" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReplaceMatchFieldType")

@XmlRootElement(name = "ReplaceMatchFieldType")

public class ReplaceMatchFieldType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ReplaceMatchFieldType() {
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

  public java.lang.String getValore() {
    return this.valore;
  }

  public void setValore(java.lang.String valore) {
    this.valore = valore;
  }

  public void set_value_tipo(String value) {
    this.tipo = (ReplaceKeywordType) ReplaceKeywordType.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.protocol.information_missing.constants.ReplaceKeywordType getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.protocol.information_missing.constants.ReplaceKeywordType tipo) {
    this.tipo = tipo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="valore",required=false)
  protected java.lang.String valore;

  @XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=true)
  protected ReplaceKeywordType tipo;

}
