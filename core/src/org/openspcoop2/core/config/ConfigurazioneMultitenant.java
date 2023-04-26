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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
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

public class ConfigurazioneMultitenant extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneMultitenant() {
    super();
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

  public void setFruizioneSceltaSoggettiErogatoriRawEnumValue(String value) {
    this.fruizioneSceltaSoggettiErogatori = (PortaDelegataSoggettiErogatori) PortaDelegataSoggettiErogatori.toEnumConstantFromString(value);
  }

  public String getFruizioneSceltaSoggettiErogatoriRawEnumValue() {
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

  public void setErogazioneSceltaSoggettiFruitoriRawEnumValue(String value) {
    this.erogazioneSceltaSoggettiFruitori = (PortaApplicativaSoggettiFruitori) PortaApplicativaSoggettiFruitori.toEnumConstantFromString(value);
  }

  public String getErogazioneSceltaSoggettiFruitoriRawEnumValue() {
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



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String fruizioneSceltaSoggettiErogatoriRawEnumValue;

  @XmlAttribute(name="fruizioneSceltaSoggettiErogatori",required=false)
  protected PortaDelegataSoggettiErogatori fruizioneSceltaSoggettiErogatori = (PortaDelegataSoggettiErogatori) PortaDelegataSoggettiErogatori.toEnumConstantFromString("soggettiEsterni");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String erogazioneSceltaSoggettiFruitoriRawEnumValue;

  @XmlAttribute(name="erogazioneSceltaSoggettiFruitori",required=false)
  protected PortaApplicativaSoggettiFruitori erogazioneSceltaSoggettiFruitori = (PortaApplicativaSoggettiFruitori) PortaApplicativaSoggettiFruitori.toEnumConstantFromString("soggettiEsterni");

}
