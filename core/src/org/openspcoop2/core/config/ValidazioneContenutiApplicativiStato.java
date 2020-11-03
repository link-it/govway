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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import java.io.Serializable;


/** <p>Java class for validazione-contenuti-applicativi-stato complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi-stato"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="configurazione-pattern" type="{http://www.openspcoop2.org/core/config}validazione-contenuti-applicativi-pattern" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/config}ValidazioneContenutiApplicativiTipo" use="optional" default="interface"/&gt;
 * 		&lt;attribute name="accept-mtom-message" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="soap-action" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="json-schema" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validazione-contenuti-applicativi-stato", 
  propOrder = {
  	"configurazionePattern"
  }
)

@XmlRootElement(name = "validazione-contenuti-applicativi-stato")

public class ValidazioneContenutiApplicativiStato extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ValidazioneContenutiApplicativiStato() {
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

  public ValidazioneContenutiApplicativiPattern getConfigurazionePattern() {
    return this.configurazionePattern;
  }

  public void setConfigurazionePattern(ValidazioneContenutiApplicativiPattern configurazionePattern) {
    this.configurazionePattern = configurazionePattern;
  }

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning stato) {
    this.stato = stato;
  }

  public void set_value_tipo(String value) {
    this.tipo = (ValidazioneContenutiApplicativiTipo) ValidazioneContenutiApplicativiTipo.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo tipo) {
    this.tipo = tipo;
  }

  public void set_value_acceptMtomMessage(String value) {
    this.acceptMtomMessage = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_acceptMtomMessage() {
    if(this.acceptMtomMessage == null){
    	return null;
    }else{
    	return this.acceptMtomMessage.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAcceptMtomMessage() {
    return this.acceptMtomMessage;
  }

  public void setAcceptMtomMessage(org.openspcoop2.core.config.constants.StatoFunzionalita acceptMtomMessage) {
    this.acceptMtomMessage = acceptMtomMessage;
  }

  public void set_value_soapAction(String value) {
    this.soapAction = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_soapAction() {
    if(this.soapAction == null){
    	return null;
    }else{
    	return this.soapAction.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getSoapAction() {
    return this.soapAction;
  }

  public void setSoapAction(org.openspcoop2.core.config.constants.StatoFunzionalita soapAction) {
    this.soapAction = soapAction;
  }

  public java.lang.String getJsonSchema() {
    return this.jsonSchema;
  }

  public void setJsonSchema(java.lang.String jsonSchema) {
    this.jsonSchema = jsonSchema;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="configurazione-pattern",required=false,nillable=false)
  protected ValidazioneContenutiApplicativiPattern configurazionePattern;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalitaConWarning stato = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=false)
  protected ValidazioneContenutiApplicativiTipo tipo = (ValidazioneContenutiApplicativiTipo) ValidazioneContenutiApplicativiTipo.toEnumConstantFromString("interface");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_acceptMtomMessage;

  @XmlAttribute(name="accept-mtom-message",required=false)
  protected StatoFunzionalita acceptMtomMessage = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_soapAction;

  @XmlAttribute(name="soap-action",required=false)
  protected StatoFunzionalita soapAction = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="json-schema",required=false)
  protected java.lang.String jsonSchema;

}
