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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for porta-tracciamento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-tracciamento"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="database" type="{http://www.openspcoop2.org/core/config}tracciamento-configurazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filetrace" type="{http://www.openspcoop2.org/core/config}tracciamento-configurazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="filetrace-config" type="{http://www.openspcoop2.org/core/config}tracciamento-configurazione-filetrace" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="transazioni" type="{http://www.openspcoop2.org/core/config}transazioni" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="severita" type="{http://www.openspcoop2.org/core/config}Severita" use="optional"/&gt;
 * 		&lt;attribute name="esiti" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-tracciamento", 
  propOrder = {
  	"database",
  	"filetrace",
  	"filetraceConfig",
  	"transazioni"
  }
)

@XmlRootElement(name = "porta-tracciamento")

public class PortaTracciamento extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortaTracciamento() {
    super();
  }

  public TracciamentoConfigurazione getDatabase() {
    return this.database;
  }

  public void setDatabase(TracciamentoConfigurazione database) {
    this.database = database;
  }

  public TracciamentoConfigurazione getFiletrace() {
    return this.filetrace;
  }

  public void setFiletrace(TracciamentoConfigurazione filetrace) {
    this.filetrace = filetrace;
  }

  public TracciamentoConfigurazioneFiletrace getFiletraceConfig() {
    return this.filetraceConfig;
  }

  public void setFiletraceConfig(TracciamentoConfigurazioneFiletrace filetraceConfig) {
    this.filetraceConfig = filetraceConfig;
  }

  public Transazioni getTransazioni() {
    return this.transazioni;
  }

  public void setTransazioni(Transazioni transazioni) {
    this.transazioni = transazioni;
  }

  public void setSeveritaRawEnumValue(String value) {
    this.severita = (Severita) Severita.toEnumConstantFromString(value);
  }

  public String getSeveritaRawEnumValue() {
    if(this.severita == null){
    	return null;
    }else{
    	return this.severita.toString();
    }
  }

  public org.openspcoop2.core.config.constants.Severita getSeverita() {
    return this.severita;
  }

  public void setSeverita(org.openspcoop2.core.config.constants.Severita severita) {
    this.severita = severita;
  }

  public java.lang.String getEsiti() {
    return this.esiti;
  }

  public void setEsiti(java.lang.String esiti) {
    this.esiti = esiti;
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

  private static final long serialVersionUID = 1L;



  @XmlElement(name="database",required=false,nillable=false)
  protected TracciamentoConfigurazione database;

  @XmlElement(name="filetrace",required=false,nillable=false)
  protected TracciamentoConfigurazione filetrace;

  @XmlElement(name="filetrace-config",required=false,nillable=false)
  protected TracciamentoConfigurazioneFiletrace filetraceConfig;

  @XmlElement(name="transazioni",required=false,nillable=false)
  protected Transazioni transazioni;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String severitaRawEnumValue;

  @XmlAttribute(name="severita",required=false)
  protected Severita severita;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="esiti",required=false)
  protected java.lang.String esiti;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
