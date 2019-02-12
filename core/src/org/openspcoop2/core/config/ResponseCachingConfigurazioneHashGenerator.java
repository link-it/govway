/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for response-caching-configurazione-hash-generator complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="response-caching-configurazione-hash-generator">
 * 		&lt;sequence>
 * 			&lt;element name="header" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="request-uri" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="headers" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="payload" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "response-caching-configurazione-hash-generator", 
  propOrder = {
  	"header"
  }
)

@XmlRootElement(name = "response-caching-configurazione-hash-generator")

public class ResponseCachingConfigurazioneHashGenerator extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ResponseCachingConfigurazioneHashGenerator() {
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

  public void addHeader(java.lang.String header) {
    this.header.add(header);
  }

  public java.lang.String getHeader(int index) {
    return this.header.get( index );
  }

  public java.lang.String removeHeader(int index) {
    return this.header.remove( index );
  }

  public List<java.lang.String> getHeaderList() {
    return this.header;
  }

  public void setHeaderList(List<java.lang.String> header) {
    this.header=header;
  }

  public int sizeHeaderList() {
    return this.header.size();
  }

  public void set_value_requestUri(String value) {
    this.requestUri = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_requestUri() {
    if(this.requestUri == null){
    	return null;
    }else{
    	return this.requestUri.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRequestUri() {
    return this.requestUri;
  }

  public void setRequestUri(org.openspcoop2.core.config.constants.StatoFunzionalita requestUri) {
    this.requestUri = requestUri;
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

  public void set_value_payload(String value) {
    this.payload = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_payload() {
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="header",required=true,nillable=false)
  protected List<java.lang.String> header = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getHeaderList
   * @return List<java.lang.String>
  */
  @Deprecated
  public List<java.lang.String> getHeader() {
  	return this.header;
  }

  /**
   * @deprecated Use method setHeaderList
   * @param header List<java.lang.String>
  */
  @Deprecated
  public void setHeader(List<java.lang.String> header) {
  	this.header=header;
  }

  /**
   * @deprecated Use method sizeHeaderList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeHeader() {
  	return this.header.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_requestUri;

  @XmlAttribute(name="request-uri",required=false)
  protected StatoFunzionalita requestUri = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_headers;

  @XmlAttribute(name="headers",required=false)
  protected StatoFunzionalita headers = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_payload;

  @XmlAttribute(name="payload",required=false)
  protected StatoFunzionalita payload = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
