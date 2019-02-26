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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for transazioni complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transazioni">
 * 		&lt;attribute name="tempi-elaborazione" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="token" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transazioni")

@XmlRootElement(name = "transazioni")

public class Transazioni extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Transazioni() {
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

  public void set_value_tempiElaborazione(String value) {
    this.tempiElaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_tempiElaborazione() {
    if(this.tempiElaborazione == null){
    	return null;
    }else{
    	return this.tempiElaborazione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getTempiElaborazione() {
    return this.tempiElaborazione;
  }

  public void setTempiElaborazione(org.openspcoop2.core.config.constants.StatoFunzionalita tempiElaborazione) {
    this.tempiElaborazione = tempiElaborazione;
  }

  public void set_value_token(String value) {
    this.token = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_token() {
    if(this.token == null){
    	return null;
    }else{
    	return this.token.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getToken() {
    return this.token;
  }

  public void setToken(org.openspcoop2.core.config.constants.StatoFunzionalita token) {
    this.token = token;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tempiElaborazione;

  @XmlAttribute(name="tempi-elaborazione",required=false)
  protected StatoFunzionalita tempiElaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_token;

  @XmlAttribute(name="token",required=false)
  protected StatoFunzionalita token = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
