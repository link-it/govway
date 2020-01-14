/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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


/** <p>Java class for dump-configurazione-regola complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dump-configurazione-regola">
 * 		&lt;attribute name="body" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="attachments" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="headers" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * &lt;/complexType>
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

public class DumpConfigurazioneRegola extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DumpConfigurazioneRegola() {
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

  public void set_value_body(String value) {
    this.body = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_body() {
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

  public void set_value_attachments(String value) {
    this.attachments = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_attachments() {
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

  public void set_value_headers(String value) {
    this.headers = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_headers() {
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

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_body;

  @XmlAttribute(name="body",required=false)
  protected StatoFunzionalita body = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_attachments;

  @XmlAttribute(name="attachments",required=false)
  protected StatoFunzionalita attachments = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_headers;

  @XmlAttribute(name="headers",required=false)
  protected StatoFunzionalita headers = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
