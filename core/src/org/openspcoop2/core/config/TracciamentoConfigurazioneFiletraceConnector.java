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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for tracciamento-configurazione-filetrace-connector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tracciamento-configurazione-filetrace-connector"&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="payload" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="header" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tracciamento-configurazione-filetrace-connector")

@XmlRootElement(name = "tracciamento-configurazione-filetrace-connector")

public class TracciamentoConfigurazioneFiletraceConnector extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TracciamentoConfigurazioneFiletraceConnector() {
    super();
  }

  public void setStatoRawEnumValue(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getStatoRawEnumValue() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalita stato) {
    this.stato = stato;
  }

  public void setPayloadRawEnumValue(String value) {
    this.payload = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getPayloadRawEnumValue() {
    if(this.payload == null){
    	return null;
    }else{
    	return this.payload.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getPayload() {
    return this.payload;
  }

  public void setPayload(org.openspcoop2.core.config.constants.StatoFunzionalita payload) {
    this.payload = payload;
  }

  public void setHeaderRawEnumValue(String value) {
    this.header = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getHeaderRawEnumValue() {
    if(this.header == null){
    	return null;
    }else{
    	return this.header.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getHeader() {
    return this.header;
  }

  public void setHeader(org.openspcoop2.core.config.constants.StatoFunzionalita header) {
    this.header = header;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String payloadRawEnumValue;

  @XmlAttribute(name="payload",required=false)
  protected StatoFunzionalita payload;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String headerRawEnumValue;

  @XmlAttribute(name="header",required=false)
  protected StatoFunzionalita header;

}
