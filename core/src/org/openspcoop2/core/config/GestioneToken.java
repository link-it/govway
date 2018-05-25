/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import java.io.Serializable;


/** <p>Java class for gestione-token complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gestione-token">
 * 		&lt;attribute name="policy" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="validazione" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="introspection" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="userInfo" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="forward" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="options" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gestione-token")

@XmlRootElement(name = "gestione-token")

public class GestioneToken extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public GestioneToken() {
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

  public java.lang.String getPolicy() {
    return this.policy;
  }

  public void setPolicy(java.lang.String policy) {
    this.policy = policy;
  }

  public void set_value_validazione(String value) {
    this.validazione = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString(value);
  }

  public String get_value_validazione() {
    if(this.validazione == null){
    	return null;
    }else{
    	return this.validazione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning getValidazione() {
    return this.validazione;
  }

  public void setValidazione(org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning validazione) {
    this.validazione = validazione;
  }

  public void set_value_introspection(String value) {
    this.introspection = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString(value);
  }

  public String get_value_introspection() {
    if(this.introspection == null){
    	return null;
    }else{
    	return this.introspection.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning getIntrospection() {
    return this.introspection;
  }

  public void setIntrospection(org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning introspection) {
    this.introspection = introspection;
  }

  public void set_value_userInfo(String value) {
    this.userInfo = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString(value);
  }

  public String get_value_userInfo() {
    if(this.userInfo == null){
    	return null;
    }else{
    	return this.userInfo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning getUserInfo() {
    return this.userInfo;
  }

  public void setUserInfo(org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning userInfo) {
    this.userInfo = userInfo;
  }

  public void set_value_forward(String value) {
    this.forward = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_forward() {
    if(this.forward == null){
    	return null;
    }else{
    	return this.forward.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getForward() {
    return this.forward;
  }

  public void setForward(org.openspcoop2.core.config.constants.StatoFunzionalita forward) {
    this.forward = forward;
  }

  public java.lang.String getOptions() {
    return this.options;
  }

  public void setOptions(java.lang.String options) {
    this.options = options;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="policy",required=true)
  protected java.lang.String policy;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_validazione;

  @XmlAttribute(name="validazione",required=false)
  protected StatoFunzionalitaConWarning validazione = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_introspection;

  @XmlAttribute(name="introspection",required=false)
  protected StatoFunzionalitaConWarning introspection = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_userInfo;

  @XmlAttribute(name="userInfo",required=false)
  protected StatoFunzionalitaConWarning userInfo = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_forward;

  @XmlAttribute(name="forward",required=false)
  protected StatoFunzionalita forward = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="options",required=false)
  protected java.lang.String options;

}
