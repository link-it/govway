/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import java.io.Serializable;


/** <p>Java class for validazione-contenuti-applicativi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-contenuti-applicativi">
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/config}ValidazioneContenutiApplicativiTipo" use="optional" default="xsd"/>
 * 		&lt;attribute name="accept-mtom-message" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validazione-contenuti-applicativi")

@XmlRootElement(name = "validazione-contenuti-applicativi")

public class ValidazioneContenutiApplicativi extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ValidazioneContenutiApplicativi() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalitaConWarning stato = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=false)
  protected ValidazioneContenutiApplicativiTipo tipo = (ValidazioneContenutiApplicativiTipo) ValidazioneContenutiApplicativiTipo.toEnumConstantFromString("xsd");

  @XmlTransient
  protected java.lang.String _value_acceptMtomMessage;

  @XmlAttribute(name="accept-mtom-message",required=false)
  protected StatoFunzionalita acceptMtomMessage = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
