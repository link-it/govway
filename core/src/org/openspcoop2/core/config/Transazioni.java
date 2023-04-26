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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for transazioni complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transazioni"&gt;
 * 		&lt;attribute name="tempi-elaborazione" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="token" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * &lt;/complexType&gt;
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

public class Transazioni extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Transazioni() {
    super();
  }

  public void setTempiElaborazioneRawEnumValue(String value) {
    this.tempiElaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getTempiElaborazioneRawEnumValue() {
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

  public void setTokenRawEnumValue(String value) {
    this.token = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getTokenRawEnumValue() {
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



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String tempiElaborazioneRawEnumValue;

  @XmlAttribute(name="tempi-elaborazione",required=false)
  protected StatoFunzionalita tempiElaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String tokenRawEnumValue;

  @XmlAttribute(name="token",required=false)
  protected StatoFunzionalita token = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
