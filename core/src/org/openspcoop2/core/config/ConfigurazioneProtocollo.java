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
import java.io.Serializable;


/** <p>Java class for configurazione-protocollo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-protocollo">
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="urlInvocazioneServizioPD" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="urlInvocazioneServizioPA" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="urlInvocazioneServizioRestPD" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="urlInvocazioneServizioRestPA" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="urlInvocazioneServizioSoapPD" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="urlInvocazioneServizioSoapPA" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configurazione-protocollo")

@XmlRootElement(name = "configurazione-protocollo")

public class ConfigurazioneProtocollo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneProtocollo() {
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

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getUrlInvocazioneServizioPD() {
    return this.urlInvocazioneServizioPD;
  }

  public void setUrlInvocazioneServizioPD(java.lang.String urlInvocazioneServizioPD) {
    this.urlInvocazioneServizioPD = urlInvocazioneServizioPD;
  }

  public java.lang.String getUrlInvocazioneServizioPA() {
    return this.urlInvocazioneServizioPA;
  }

  public void setUrlInvocazioneServizioPA(java.lang.String urlInvocazioneServizioPA) {
    this.urlInvocazioneServizioPA = urlInvocazioneServizioPA;
  }

  public java.lang.String getUrlInvocazioneServizioRestPD() {
    return this.urlInvocazioneServizioRestPD;
  }

  public void setUrlInvocazioneServizioRestPD(java.lang.String urlInvocazioneServizioRestPD) {
    this.urlInvocazioneServizioRestPD = urlInvocazioneServizioRestPD;
  }

  public java.lang.String getUrlInvocazioneServizioRestPA() {
    return this.urlInvocazioneServizioRestPA;
  }

  public void setUrlInvocazioneServizioRestPA(java.lang.String urlInvocazioneServizioRestPA) {
    this.urlInvocazioneServizioRestPA = urlInvocazioneServizioRestPA;
  }

  public java.lang.String getUrlInvocazioneServizioSoapPD() {
    return this.urlInvocazioneServizioSoapPD;
  }

  public void setUrlInvocazioneServizioSoapPD(java.lang.String urlInvocazioneServizioSoapPD) {
    this.urlInvocazioneServizioSoapPD = urlInvocazioneServizioSoapPD;
  }

  public java.lang.String getUrlInvocazioneServizioSoapPA() {
    return this.urlInvocazioneServizioSoapPA;
  }

  public void setUrlInvocazioneServizioSoapPA(java.lang.String urlInvocazioneServizioSoapPA) {
    this.urlInvocazioneServizioSoapPA = urlInvocazioneServizioSoapPA;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="urlInvocazioneServizioPD",required=false)
  protected java.lang.String urlInvocazioneServizioPD;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="urlInvocazioneServizioPA",required=false)
  protected java.lang.String urlInvocazioneServizioPA;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="urlInvocazioneServizioRestPD",required=false)
  protected java.lang.String urlInvocazioneServizioRestPD;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="urlInvocazioneServizioRestPA",required=false)
  protected java.lang.String urlInvocazioneServizioRestPA;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="urlInvocazioneServizioSoapPD",required=false)
  protected java.lang.String urlInvocazioneServizioSoapPD;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="urlInvocazioneServizioSoapPA",required=false)
  protected java.lang.String urlInvocazioneServizioSoapPA;

}
