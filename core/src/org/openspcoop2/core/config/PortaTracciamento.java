/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.Severita;
import java.io.Serializable;


/** <p>Java class for porta-tracciamento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-tracciamento"&gt;
 * 		&lt;attribute name="severita" type="{http://www.openspcoop2.org/core/config}Severita" use="optional"/&gt;
 * 		&lt;attribute name="esiti" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-tracciamento")

@XmlRootElement(name = "porta-tracciamento")

public class PortaTracciamento extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortaTracciamento() {
    super();
  }

  public void setSeveritaRawEnumValue(String value) {
    this.severita = (Severita) Severita.toEnumConstantFromString(value);
  }

  public String getSeveritaRawEnumValue() {
    if(this.severita == null){
    	return null;
    }else{
    	return this.severita.toString();
    }
  }

  public org.openspcoop2.core.config.constants.Severita getSeverita() {
    return this.severita;
  }

  public void setSeverita(org.openspcoop2.core.config.constants.Severita severita) {
    this.severita = severita;
  }

  public java.lang.String getEsiti() {
    return this.esiti;
  }

  public void setEsiti(java.lang.String esiti) {
    this.esiti = esiti;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String severitaRawEnumValue;

  @XmlAttribute(name="severita",required=false)
  protected Severita severita;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="esiti",required=false)
  protected java.lang.String esiti;

}
