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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for response-caching-configurazione-hash-generator complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="response-caching-configurazione-hash-generator"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="header" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="query-parameter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="request-uri" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="query-parameters" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaCacheDigestQueryParameter" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="headers" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="payload" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * &lt;/complexType&gt;
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
  	"header",
  	"queryParameter"
  }
)

@XmlRootElement(name = "response-caching-configurazione-hash-generator")

public class ResponseCachingConfigurazioneHashGenerator extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ResponseCachingConfigurazioneHashGenerator() {
    super();
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

  public void addQueryParameter(java.lang.String queryParameter) {
    this.queryParameter.add(queryParameter);
  }

  public java.lang.String getQueryParameter(int index) {
    return this.queryParameter.get( index );
  }

  public java.lang.String removeQueryParameter(int index) {
    return this.queryParameter.remove( index );
  }

  public List<java.lang.String> getQueryParameterList() {
    return this.queryParameter;
  }

  public void setQueryParameterList(List<java.lang.String> queryParameter) {
    this.queryParameter=queryParameter;
  }

  public int sizeQueryParameterList() {
    return this.queryParameter.size();
  }

  public void setRequestUriRawEnumValue(String value) {
    this.requestUri = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getRequestUriRawEnumValue() {
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

  public void setQueryParametersRawEnumValue(String value) {
    this.queryParameters = (StatoFunzionalitaCacheDigestQueryParameter) StatoFunzionalitaCacheDigestQueryParameter.toEnumConstantFromString(value);
  }

  public String getQueryParametersRawEnumValue() {
    if(this.queryParameters == null){
    	return null;
    }else{
    	return this.queryParameters.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter getQueryParameters() {
    return this.queryParameters;
  }

  public void setQueryParameters(org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter queryParameters) {
    this.queryParameters = queryParameters;
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

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="header",required=true,nillable=false)
  private List<java.lang.String> header = new ArrayList<>();

  /**
   * Use method getHeaderList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getHeader() {
  	return this.getHeaderList();
  }

  /**
   * Use method setHeaderList
   * @param header List&lt;java.lang.String&gt;
  */
  public void setHeader(List<java.lang.String> header) {
  	this.setHeaderList(header);
  }

  /**
   * Use method sizeHeaderList
   * @return lunghezza della lista
  */
  public int sizeHeader() {
  	return this.sizeHeaderList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="query-parameter",required=true,nillable=false)
  private List<java.lang.String> queryParameter = new ArrayList<>();

  /**
   * Use method getQueryParameterList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getQueryParameter() {
  	return this.getQueryParameterList();
  }

  /**
   * Use method setQueryParameterList
   * @param queryParameter List&lt;java.lang.String&gt;
  */
  public void setQueryParameter(List<java.lang.String> queryParameter) {
  	this.setQueryParameterList(queryParameter);
  }

  /**
   * Use method sizeQueryParameterList
   * @return lunghezza della lista
  */
  public int sizeQueryParameter() {
  	return this.sizeQueryParameterList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String requestUriRawEnumValue;

  @XmlAttribute(name="request-uri",required=false)
  protected StatoFunzionalita requestUri = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String queryParametersRawEnumValue;

  @XmlAttribute(name="query-parameters",required=false)
  protected StatoFunzionalitaCacheDigestQueryParameter queryParameters = (StatoFunzionalitaCacheDigestQueryParameter) StatoFunzionalitaCacheDigestQueryParameter.toEnumConstantFromString("abilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String headersRawEnumValue;

  @XmlAttribute(name="headers",required=false)
  protected StatoFunzionalita headers = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String payloadRawEnumValue;

  @XmlAttribute(name="payload",required=false)
  protected StatoFunzionalita payload = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
