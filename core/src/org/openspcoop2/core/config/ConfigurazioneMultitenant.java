/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettiErogatori;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for configurazione-multitenant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-multitenant"&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="fruizioneSceltaSoggettiErogatori" type="{http://www.openspcoop2.org/core/config}PortaDelegataSoggettiErogatori" use="optional" default="soggettiEsterni"/&gt;
 * 		&lt;attribute name="erogazioneSceltaSoggettiFruitori" type="{http://www.openspcoop2.org/core/config}PortaApplicativaSoggettiFruitori" use="optional" default="soggettiEsterni"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configurazione-multitenant")

@XmlRootElement(name = "configurazione-multitenant")

public class ConfigurazioneMultitenant extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneMultitenant() {
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

  public void set_value_fruizioneSceltaSoggettiErogatori(String value) {
    this.fruizioneSceltaSoggettiErogatori = (PortaDelegataSoggettiErogatori) PortaDelegataSoggettiErogatori.toEnumConstantFromString(value);
  }

  public String get_value_fruizioneSceltaSoggettiErogatori() {
    if(this.fruizioneSceltaSoggettiErogatori == null){
    	return null;
    }else{
    	return this.fruizioneSceltaSoggettiErogatori.toString();
    }
  }

  public org.openspcoop2.core.config.constants.PortaDelegataSoggettiErogatori getFruizioneSceltaSoggettiErogatori() {
    return this.fruizioneSceltaSoggettiErogatori;
  }

  public void setFruizioneSceltaSoggettiErogatori(org.openspcoop2.core.config.constants.PortaDelegataSoggettiErogatori fruizioneSceltaSoggettiErogatori) {
    this.fruizioneSceltaSoggettiErogatori = fruizioneSceltaSoggettiErogatori;
  }

  public void set_value_erogazioneSceltaSoggettiFruitori(String value) {
    this.erogazioneSceltaSoggettiFruitori = (PortaApplicativaSoggettiFruitori) PortaApplicativaSoggettiFruitori.toEnumConstantFromString(value);
  }

  public String get_value_erogazioneSceltaSoggettiFruitori() {
    if(this.erogazioneSceltaSoggettiFruitori == null){
    	return null;
    }else{
    	return this.erogazioneSceltaSoggettiFruitori.toString();
    }
  }

  public org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori getErogazioneSceltaSoggettiFruitori() {
    return this.erogazioneSceltaSoggettiFruitori;
  }

  public void setErogazioneSceltaSoggettiFruitori(org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori erogazioneSceltaSoggettiFruitori) {
    this.erogazioneSceltaSoggettiFruitori = erogazioneSceltaSoggettiFruitori;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_fruizioneSceltaSoggettiErogatori;

  @XmlAttribute(name="fruizioneSceltaSoggettiErogatori",required=false)
  protected PortaDelegataSoggettiErogatori fruizioneSceltaSoggettiErogatori = (PortaDelegataSoggettiErogatori) PortaDelegataSoggettiErogatori.toEnumConstantFromString("soggettiEsterni");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_erogazioneSceltaSoggettiFruitori;

  @XmlAttribute(name="erogazioneSceltaSoggettiFruitori",required=false)
  protected PortaApplicativaSoggettiFruitori erogazioneSceltaSoggettiFruitori = (PortaApplicativaSoggettiFruitori) PortaApplicativaSoggettiFruitori.toEnumConstantFromString("soggettiEsterni");

}
