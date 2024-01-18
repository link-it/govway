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
import org.openspcoop2.core.config.constants.TipoConnessioneRisposte;
import java.io.Serializable;


/** <p>Java class for risposte complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="risposte"&gt;
 * 		&lt;attribute name="connessione" type="{http://www.openspcoop2.org/core/config}TipoConnessioneRisposte" use="optional" default="reply"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "risposte")

@XmlRootElement(name = "risposte")

public class Risposte extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Risposte() {
    super();
  }

  public void setConnessioneRawEnumValue(String value) {
    this.connessione = (TipoConnessioneRisposte) TipoConnessioneRisposte.toEnumConstantFromString(value);
  }

  public String getConnessioneRawEnumValue() {
    if(this.connessione == null){
    	return null;
    }else{
    	return this.connessione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.TipoConnessioneRisposte getConnessione() {
    return this.connessione;
  }

  public void setConnessione(org.openspcoop2.core.config.constants.TipoConnessioneRisposte connessione) {
    this.connessione = connessione;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String connessioneRawEnumValue;

  @XmlAttribute(name="connessione",required=false)
  protected TipoConnessioneRisposte connessione = (TipoConnessioneRisposte) TipoConnessioneRisposte.toEnumConstantFromString("reply");

}
