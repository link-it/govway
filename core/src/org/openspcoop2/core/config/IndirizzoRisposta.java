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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for indirizzo-risposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="indirizzo-risposta">
 * 		&lt;attribute name="utilizzo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * &lt;/complexType>
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

public class IndirizzoRisposta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public IndirizzoRisposta() {
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

  public void set_value_utilizzo(String value) {
    this.utilizzo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_utilizzo() {
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

  @XmlTransient
  private Long id;



  @XmlTransient
  protected java.lang.String _value_utilizzo;

  @XmlAttribute(name="utilizzo",required=false)
  protected StatoFunzionalita utilizzo = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
