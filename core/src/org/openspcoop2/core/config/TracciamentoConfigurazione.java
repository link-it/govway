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
import org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione;
import java.io.Serializable;


/** <p>Java class for tracciamento-configurazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tracciamento-configurazione"&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConPersonalizzazione" use="optional"/&gt;
 * 		&lt;attribute name="filtro-esiti" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional"/&gt;
 * 		&lt;attribute name="request-in" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="request-out" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="response-out" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="response-out-complete" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tracciamento-configurazione")

@XmlRootElement(name = "tracciamento-configurazione")

public class TracciamentoConfigurazione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TracciamentoConfigurazione() {
    super();
  }

  public void setStatoRawEnumValue(String value) {
    this.stato = (StatoFunzionalitaConPersonalizzazione) StatoFunzionalitaConPersonalizzazione.toEnumConstantFromString(value);
  }

  public String getStatoRawEnumValue() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalitaConPersonalizzazione stato) {
    this.stato = stato;
  }

  public void setFiltroEsitiRawEnumValue(String value) {
    this.filtroEsiti = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getFiltroEsitiRawEnumValue() {
    if(this.filtroEsiti == null){
    	return null;
    }else{
    	return this.filtroEsiti.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getFiltroEsiti() {
    return this.filtroEsiti;
  }

  public void setFiltroEsiti(org.openspcoop2.core.config.constants.StatoFunzionalita filtroEsiti) {
    this.filtroEsiti = filtroEsiti;
  }

  public void setRequestInRawEnumValue(String value) {
    this.requestIn = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getRequestInRawEnumValue() {
    if(this.requestIn == null){
    	return null;
    }else{
    	return this.requestIn.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRequestIn() {
    return this.requestIn;
  }

  public void setRequestIn(org.openspcoop2.core.config.constants.StatoFunzionalita requestIn) {
    this.requestIn = requestIn;
  }

  public void setRequestOutRawEnumValue(String value) {
    this.requestOut = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getRequestOutRawEnumValue() {
    if(this.requestOut == null){
    	return null;
    }else{
    	return this.requestOut.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRequestOut() {
    return this.requestOut;
  }

  public void setRequestOut(org.openspcoop2.core.config.constants.StatoFunzionalita requestOut) {
    this.requestOut = requestOut;
  }

  public void setResponseOutRawEnumValue(String value) {
    this.responseOut = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getResponseOutRawEnumValue() {
    if(this.responseOut == null){
    	return null;
    }else{
    	return this.responseOut.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getResponseOut() {
    return this.responseOut;
  }

  public void setResponseOut(org.openspcoop2.core.config.constants.StatoFunzionalita responseOut) {
    this.responseOut = responseOut;
  }

  public void setResponseOutCompleteRawEnumValue(String value) {
    this.responseOutComplete = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getResponseOutCompleteRawEnumValue() {
    if(this.responseOutComplete == null){
    	return null;
    }else{
    	return this.responseOutComplete.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getResponseOutComplete() {
    return this.responseOutComplete;
  }

  public void setResponseOutComplete(org.openspcoop2.core.config.constants.StatoFunzionalita responseOutComplete) {
    this.responseOutComplete = responseOutComplete;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalitaConPersonalizzazione stato;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String filtroEsitiRawEnumValue;

  @XmlAttribute(name="filtro-esiti",required=false)
  protected StatoFunzionalita filtroEsiti;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String requestInRawEnumValue;

  @XmlAttribute(name="request-in",required=false)
  protected StatoFunzionalita requestIn = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String requestOutRawEnumValue;

  @XmlAttribute(name="request-out",required=false)
  protected StatoFunzionalita requestOut = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String responseOutRawEnumValue;

  @XmlAttribute(name="response-out",required=false)
  protected StatoFunzionalita responseOut = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String responseOutCompleteRawEnumValue;

  @XmlAttribute(name="response-out-complete",required=false)
  protected StatoFunzionalita responseOutComplete = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
