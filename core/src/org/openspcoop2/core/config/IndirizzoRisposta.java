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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for indirizzo-risposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="indirizzo-risposta"&gt;
 * 		&lt;attribute name="utilizzo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "indirizzo-risposta")

@XmlRootElement(name = "indirizzo-risposta")

public class IndirizzoRisposta extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public IndirizzoRisposta() {
    super();
  }

  public void setUtilizzoRawEnumValue(String value) {
    this.utilizzo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getUtilizzoRawEnumValue() {
    if(this.utilizzo == null){
    	return null;
    }else{
    	return this.utilizzo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getUtilizzo() {
    return this.utilizzo;
  }

  public void setUtilizzo(org.openspcoop2.core.config.constants.StatoFunzionalita utilizzo) {
    this.utilizzo = utilizzo;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String utilizzoRawEnumValue;

  @XmlAttribute(name="utilizzo",required=false)
  protected StatoFunzionalita utilizzo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
