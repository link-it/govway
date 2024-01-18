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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import java.io.Serializable;


/** <p>Java class for cors-configurazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cors-configurazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="access-control-allow-origins" type="{http://www.openspcoop2.org/core/config}cors-configurazione-origin" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="access-control-allow-headers" type="{http://www.openspcoop2.org/core/config}cors-configurazione-headers" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="access-control-allow-methods" type="{http://www.openspcoop2.org/core/config}cors-configurazione-methods" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="access-control-expose-headers" type="{http://www.openspcoop2.org/core/config}cors-configurazione-headers" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/config}TipoGestioneCORS" use="optional" default="gateway"/&gt;
 * 		&lt;attribute name="access-control-all-allow-origins" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="access-control-all-allow-methods" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="access-control-all-allow-headers" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="access-control-allow-credentials" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="access-control-max-age" type="{http://www.w3.org/2001/XMLSchema}integer" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cors-configurazione", 
  propOrder = {
  	"accessControlAllowOrigins",
  	"accessControlAllowHeaders",
  	"accessControlAllowMethods",
  	"accessControlExposeHeaders"
  }
)

@XmlRootElement(name = "cors-configurazione")

public class CorsConfigurazione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public CorsConfigurazione() {
    super();
  }

  public CorsConfigurazioneOrigin getAccessControlAllowOrigins() {
    return this.accessControlAllowOrigins;
  }

  public void setAccessControlAllowOrigins(CorsConfigurazioneOrigin accessControlAllowOrigins) {
    this.accessControlAllowOrigins = accessControlAllowOrigins;
  }

  public CorsConfigurazioneHeaders getAccessControlAllowHeaders() {
    return this.accessControlAllowHeaders;
  }

  public void setAccessControlAllowHeaders(CorsConfigurazioneHeaders accessControlAllowHeaders) {
    this.accessControlAllowHeaders = accessControlAllowHeaders;
  }

  public CorsConfigurazioneMethods getAccessControlAllowMethods() {
    return this.accessControlAllowMethods;
  }

  public void setAccessControlAllowMethods(CorsConfigurazioneMethods accessControlAllowMethods) {
    this.accessControlAllowMethods = accessControlAllowMethods;
  }

  public CorsConfigurazioneHeaders getAccessControlExposeHeaders() {
    return this.accessControlExposeHeaders;
  }

  public void setAccessControlExposeHeaders(CorsConfigurazioneHeaders accessControlExposeHeaders) {
    this.accessControlExposeHeaders = accessControlExposeHeaders;
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

  public void setTipoRawEnumValue(String value) {
    this.tipo = (TipoGestioneCORS) TipoGestioneCORS.toEnumConstantFromString(value);
  }

  public String getTipoRawEnumValue() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.TipoGestioneCORS getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.config.constants.TipoGestioneCORS tipo) {
    this.tipo = tipo;
  }

  public void setAccessControlAllAllowOriginsRawEnumValue(String value) {
    this.accessControlAllAllowOrigins = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getAccessControlAllAllowOriginsRawEnumValue() {
    if(this.accessControlAllAllowOrigins == null){
    	return null;
    }else{
    	return this.accessControlAllAllowOrigins.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAccessControlAllAllowOrigins() {
    return this.accessControlAllAllowOrigins;
  }

  public void setAccessControlAllAllowOrigins(org.openspcoop2.core.config.constants.StatoFunzionalita accessControlAllAllowOrigins) {
    this.accessControlAllAllowOrigins = accessControlAllAllowOrigins;
  }

  public void setAccessControlAllAllowMethodsRawEnumValue(String value) {
    this.accessControlAllAllowMethods = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getAccessControlAllAllowMethodsRawEnumValue() {
    if(this.accessControlAllAllowMethods == null){
    	return null;
    }else{
    	return this.accessControlAllAllowMethods.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAccessControlAllAllowMethods() {
    return this.accessControlAllAllowMethods;
  }

  public void setAccessControlAllAllowMethods(org.openspcoop2.core.config.constants.StatoFunzionalita accessControlAllAllowMethods) {
    this.accessControlAllAllowMethods = accessControlAllAllowMethods;
  }

  public void setAccessControlAllAllowHeadersRawEnumValue(String value) {
    this.accessControlAllAllowHeaders = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getAccessControlAllAllowHeadersRawEnumValue() {
    if(this.accessControlAllAllowHeaders == null){
    	return null;
    }else{
    	return this.accessControlAllAllowHeaders.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAccessControlAllAllowHeaders() {
    return this.accessControlAllAllowHeaders;
  }

  public void setAccessControlAllAllowHeaders(org.openspcoop2.core.config.constants.StatoFunzionalita accessControlAllAllowHeaders) {
    this.accessControlAllAllowHeaders = accessControlAllAllowHeaders;
  }

  public void setAccessControlAllowCredentialsRawEnumValue(String value) {
    this.accessControlAllowCredentials = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getAccessControlAllowCredentialsRawEnumValue() {
    if(this.accessControlAllowCredentials == null){
    	return null;
    }else{
    	return this.accessControlAllowCredentials.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAccessControlAllowCredentials() {
    return this.accessControlAllowCredentials;
  }

  public void setAccessControlAllowCredentials(org.openspcoop2.core.config.constants.StatoFunzionalita accessControlAllowCredentials) {
    this.accessControlAllowCredentials = accessControlAllowCredentials;
  }

  public java.lang.Integer getAccessControlMaxAge() {
    return this.accessControlMaxAge;
  }

  public void setAccessControlMaxAge(java.lang.Integer accessControlMaxAge) {
    this.accessControlMaxAge = accessControlMaxAge;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="access-control-allow-origins",required=false,nillable=false)
  protected CorsConfigurazioneOrigin accessControlAllowOrigins;

  @XmlElement(name="access-control-allow-headers",required=false,nillable=false)
  protected CorsConfigurazioneHeaders accessControlAllowHeaders;

  @XmlElement(name="access-control-allow-methods",required=false,nillable=false)
  protected CorsConfigurazioneMethods accessControlAllowMethods;

  @XmlElement(name="access-control-expose-headers",required=false,nillable=false)
  protected CorsConfigurazioneHeaders accessControlExposeHeaders;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String tipoRawEnumValue;

  @XmlAttribute(name="tipo",required=false)
  protected TipoGestioneCORS tipo = (TipoGestioneCORS) TipoGestioneCORS.toEnumConstantFromString("gateway");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String accessControlAllAllowOriginsRawEnumValue;

  @XmlAttribute(name="access-control-all-allow-origins",required=false)
  protected StatoFunzionalita accessControlAllAllowOrigins = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String accessControlAllAllowMethodsRawEnumValue;

  @XmlAttribute(name="access-control-all-allow-methods",required=false)
  protected StatoFunzionalita accessControlAllAllowMethods = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String accessControlAllAllowHeadersRawEnumValue;

  @XmlAttribute(name="access-control-all-allow-headers",required=false)
  protected StatoFunzionalita accessControlAllAllowHeaders = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String accessControlAllowCredentialsRawEnumValue;

  @XmlAttribute(name="access-control-allow-credentials",required=false)
  protected StatoFunzionalita accessControlAllowCredentials = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlAttribute(name="access-control-max-age",required=false)
  protected java.lang.Integer accessControlMaxAge;

}
