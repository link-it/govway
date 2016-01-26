/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.TipoConnessioneRisposte;
import java.io.Serializable;


/** <p>Java class for risposte complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="risposte">
 * 		&lt;attribute name="connessione" type="{http://www.openspcoop2.org/core/config}TipoConnessioneRisposte" use="optional" default="reply"/>
 * &lt;/complexType>
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

public class Risposte extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Risposte() {
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

  public void set_value_connessione(String value) {
    this.connessione = (TipoConnessioneRisposte) TipoConnessioneRisposte.toEnumConstantFromString(value);
  }

  public String get_value_connessione() {
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

  @XmlTransient
  private Long id;



  @XmlTransient
  protected java.lang.String _value_connessione;

  @XmlAttribute(name="connessione",required=false)
  protected TipoConnessioneRisposte connessione = (TipoConnessioneRisposte) TipoConnessioneRisposte.toEnumConstantFromString("reply");

}
