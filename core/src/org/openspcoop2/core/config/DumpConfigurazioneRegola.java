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


/** <p>Java class for dump-configurazione-regola complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dump-configurazione-regola"&gt;
 * 		&lt;attribute name="payload" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="payload-parsing" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="attachments" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="headers" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dump-configurazione-regola")

@XmlRootElement(name = "dump-configurazione-regola")

public class DumpConfigurazioneRegola extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public DumpConfigurazioneRegola() {
    super();
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

  public void setPayloadParsingRawEnumValue(String value) {
    this.payloadParsing = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getPayloadParsingRawEnumValue() {
    if(this.payloadParsing == null){
    	return null;
    }else{
    	return this.payloadParsing.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getPayloadParsing() {
    return this.payloadParsing;
  }

  public void setPayloadParsing(org.openspcoop2.core.config.constants.StatoFunzionalita payloadParsing) {
    this.payloadParsing = payloadParsing;
  }

  public void setBodyRawEnumValue(String value) {
    this.body = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getBodyRawEnumValue() {
    if(this.body == null){
    	return null;
    }else{
    	return this.body.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getBody() {
    return this.body;
  }

  public void setBody(org.openspcoop2.core.config.constants.StatoFunzionalita body) {
    this.body = body;
  }

  public void setAttachmentsRawEnumValue(String value) {
    this.attachments = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getAttachmentsRawEnumValue() {
    if(this.attachments == null){
    	return null;
    }else{
    	return this.attachments.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAttachments() {
    return this.attachments;
  }

  public void setAttachments(org.openspcoop2.core.config.constants.StatoFunzionalita attachments) {
    this.attachments = attachments;
  }

  public void setHeadersRawEnumValue(String value) {
    this.headers = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getHeadersRawEnumValue() {
    if(this.headers == null){
    	return null;
    }else{
    	return this.headers.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getHeaders() {
    return this.headers;
  }

  public void setHeaders(org.openspcoop2.core.config.constants.StatoFunzionalita headers) {
    this.headers = headers;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String payloadRawEnumValue;

  @XmlAttribute(name="payload",required=false)
  protected StatoFunzionalita payload = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String payloadParsingRawEnumValue;

  @XmlAttribute(name="payload-parsing",required=false)
  protected StatoFunzionalita payloadParsing = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String bodyRawEnumValue;

  @XmlAttribute(name="body",required=false)
  protected StatoFunzionalita body = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String attachmentsRawEnumValue;

  @XmlAttribute(name="attachments",required=false)
  protected StatoFunzionalita attachments = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String headersRawEnumValue;

  @XmlAttribute(name="headers",required=false)
  protected StatoFunzionalita headers = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
