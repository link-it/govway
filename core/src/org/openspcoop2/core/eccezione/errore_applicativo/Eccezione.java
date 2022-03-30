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
package org.openspcoop2.core.eccezione.errore_applicativo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoEccezione;
import java.io.Serializable;


/** <p>Java class for eccezione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eccezione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="code" type="{http://govway.org/integration/fault}CodiceEccezione" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="type" type="{http://govway.org/integration/fault}TipoEccezione" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eccezione", 
  propOrder = {
  	"code",
  	"description"
  }
)

@XmlRootElement(name = "eccezione")

public class Eccezione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Eccezione() {
  }

  public CodiceEccezione getCode() {
    return this.code;
  }

  public void setCode(CodiceEccezione code) {
    this.code = code;
  }

  public java.lang.String getDescription() {
    return this.description;
  }

  public void setDescription(java.lang.String description) {
    this.description = description;
  }

  public void set_value_type(String value) {
    this.type = (TipoEccezione) TipoEccezione.toEnumConstantFromString(value);
  }

  public String get_value_type() {
    if(this.type == null){
    	return null;
    }else{
    	return this.type.toString();
    }
  }

  public org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoEccezione getType() {
    return this.type;
  }

  public void setType(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoEccezione type) {
    this.type = type;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="code",required=true,nillable=false)
  protected CodiceEccezione code;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="description",required=true,nillable=false)
  protected java.lang.String description;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_type;

  @XmlAttribute(name="type",required=true)
  protected TipoEccezione type;

}
