/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.protocol.information_missing.constants.ReplaceKeywordType;
import java.io.Serializable;


/** <p>Java class for ReplaceMatchFieldType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReplaceMatchFieldType"&gt;
 * 		&lt;attribute name="valore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/protocol/information_missing}ReplaceKeywordType" use="required"/&gt;
 * &lt;/complexType&gt;
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



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="valore",required=false)
  protected java.lang.String valore;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=true)
  protected ReplaceKeywordType tipo;

}
