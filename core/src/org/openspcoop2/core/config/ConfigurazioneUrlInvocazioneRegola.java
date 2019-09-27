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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.ServiceBinding;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for configurazione-url-invocazione-regola complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-url-invocazione-regola">
 * 		&lt;sequence>
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/core/config}id-soggetto" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="posizione" type="{http://www.w3.org/2001/XMLSchema}int" use="required"/>
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="regexpr" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/>
 * 		&lt;attribute name="regola" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="contesto-esterno" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="base-url" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="protocollo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="ruolo" type="{http://www.openspcoop2.org/core/config}RuoloContesto" use="optional"/>
 * 		&lt;attribute name="service-binding" type="{http://www.openspcoop2.org/core/config}ServiceBinding" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configurazione-url-invocazione-regola", 
  propOrder = {
  	"soggetto"
  }
)

@XmlRootElement(name = "configurazione-url-invocazione-regola")

public class ConfigurazioneUrlInvocazioneRegola extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneUrlInvocazioneRegola() {
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

  public IdSoggetto getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(IdSoggetto soggetto) {
    this.soggetto = soggetto;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public int getPosizione() {
    return this.posizione;
  }

  public void setPosizione(int posizione) {
    this.posizione = posizione;
  }

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
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

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public boolean isRegexpr() {
    return this.regexpr;
  }

  public boolean getRegexpr() {
    return this.regexpr;
  }

  public void setRegexpr(boolean regexpr) {
    this.regexpr = regexpr;
  }

  public java.lang.String getRegola() {
    return this.regola;
  }

  public void setRegola(java.lang.String regola) {
    this.regola = regola;
  }

  public java.lang.String getContestoEsterno() {
    return this.contestoEsterno;
  }

  public void setContestoEsterno(java.lang.String contestoEsterno) {
    this.contestoEsterno = contestoEsterno;
  }

  public java.lang.String getBaseUrl() {
    return this.baseUrl;
  }

  public void setBaseUrl(java.lang.String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public java.lang.String getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(java.lang.String protocollo) {
    this.protocollo = protocollo;
  }

  public void set_value_ruolo(String value) {
    this.ruolo = (RuoloContesto) RuoloContesto.toEnumConstantFromString(value);
  }

  public String get_value_ruolo() {
    if(this.ruolo == null){
    	return null;
    }else{
    	return this.ruolo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.RuoloContesto getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(org.openspcoop2.core.config.constants.RuoloContesto ruolo) {
    this.ruolo = ruolo;
  }

  public void set_value_serviceBinding(String value) {
    this.serviceBinding = (ServiceBinding) ServiceBinding.toEnumConstantFromString(value);
  }

  public String get_value_serviceBinding() {
    if(this.serviceBinding == null){
    	return null;
    }else{
    	return this.serviceBinding.toString();
    }
  }

  public org.openspcoop2.core.config.constants.ServiceBinding getServiceBinding() {
    return this.serviceBinding;
  }

  public void setServiceBinding(org.openspcoop2.core.config.constants.ServiceBinding serviceBinding) {
    this.serviceBinding = serviceBinding;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="soggetto",required=false,nillable=false)
  protected IdSoggetto soggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlAttribute(name="posizione",required=true)
  protected int posizione;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="regexpr",required=false)
  protected boolean regexpr = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="regola",required=true)
  protected java.lang.String regola;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="contesto-esterno",required=true)
  protected java.lang.String contestoEsterno;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="base-url",required=false)
  protected java.lang.String baseUrl;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="protocollo",required=false)
  protected java.lang.String protocollo;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_ruolo;

  @XmlAttribute(name="ruolo",required=false)
  protected RuoloContesto ruolo;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_serviceBinding;

  @XmlAttribute(name="service-binding",required=false)
  protected ServiceBinding serviceBinding;

}