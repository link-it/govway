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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.Severita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for messaggi-diagnostici complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messaggi-diagnostici"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="openspcoop-appender" type="{http://www.openspcoop2.org/core/config}openspcoop-appender" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="openspcoop-sorgente-dati" type="{http://www.openspcoop2.org/core/config}openspcoop-sorgente-dati" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="severita" type="{http://www.openspcoop2.org/core/config}Severita" use="optional" default="infoIntegration"/&gt;
 * 		&lt;attribute name="severita-log4j" type="{http://www.openspcoop2.org/core/config}Severita" use="optional" default="infoIntegration"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messaggi-diagnostici", 
  propOrder = {
  	"openspcoopAppender",
  	"openspcoopSorgenteDati"
  }
)

@XmlRootElement(name = "messaggi-diagnostici")

public class MessaggiDiagnostici extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public MessaggiDiagnostici() {
    super();
  }

  public void addOpenspcoopAppender(OpenspcoopAppender openspcoopAppender) {
    this.openspcoopAppender.add(openspcoopAppender);
  }

  public OpenspcoopAppender getOpenspcoopAppender(int index) {
    return this.openspcoopAppender.get( index );
  }

  public OpenspcoopAppender removeOpenspcoopAppender(int index) {
    return this.openspcoopAppender.remove( index );
  }

  public List<OpenspcoopAppender> getOpenspcoopAppenderList() {
    return this.openspcoopAppender;
  }

  public void setOpenspcoopAppenderList(List<OpenspcoopAppender> openspcoopAppender) {
    this.openspcoopAppender=openspcoopAppender;
  }

  public int sizeOpenspcoopAppenderList() {
    return this.openspcoopAppender.size();
  }

  public void addOpenspcoopSorgenteDati(OpenspcoopSorgenteDati openspcoopSorgenteDati) {
    this.openspcoopSorgenteDati.add(openspcoopSorgenteDati);
  }

  public OpenspcoopSorgenteDati getOpenspcoopSorgenteDati(int index) {
    return this.openspcoopSorgenteDati.get( index );
  }

  public OpenspcoopSorgenteDati removeOpenspcoopSorgenteDati(int index) {
    return this.openspcoopSorgenteDati.remove( index );
  }

  public List<OpenspcoopSorgenteDati> getOpenspcoopSorgenteDatiList() {
    return this.openspcoopSorgenteDati;
  }

  public void setOpenspcoopSorgenteDatiList(List<OpenspcoopSorgenteDati> openspcoopSorgenteDati) {
    this.openspcoopSorgenteDati=openspcoopSorgenteDati;
  }

  public int sizeOpenspcoopSorgenteDatiList() {
    return this.openspcoopSorgenteDati.size();
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

  public void setSeveritaLog4jRawEnumValue(String value) {
    this.severitaLog4j = (Severita) Severita.toEnumConstantFromString(value);
  }

  public String getSeveritaLog4jRawEnumValue() {
    if(this.severitaLog4j == null){
    	return null;
    }else{
    	return this.severitaLog4j.toString();
    }
  }

  public org.openspcoop2.core.config.constants.Severita getSeveritaLog4j() {
    return this.severitaLog4j;
  }

  public void setSeveritaLog4j(org.openspcoop2.core.config.constants.Severita severitaLog4j) {
    this.severitaLog4j = severitaLog4j;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="openspcoop-appender",required=true,nillable=false)
  private List<OpenspcoopAppender> openspcoopAppender = new ArrayList<>();

  /**
   * Use method getOpenspcoopAppenderList
   * @return List&lt;OpenspcoopAppender&gt;
  */
  public List<OpenspcoopAppender> getOpenspcoopAppender() {
  	return this.getOpenspcoopAppenderList();
  }

  /**
   * Use method setOpenspcoopAppenderList
   * @param openspcoopAppender List&lt;OpenspcoopAppender&gt;
  */
  public void setOpenspcoopAppender(List<OpenspcoopAppender> openspcoopAppender) {
  	this.setOpenspcoopAppenderList(openspcoopAppender);
  }

  /**
   * Use method sizeOpenspcoopAppenderList
   * @return lunghezza della lista
  */
  public int sizeOpenspcoopAppender() {
  	return this.sizeOpenspcoopAppenderList();
  }

  @XmlElement(name="openspcoop-sorgente-dati",required=true,nillable=false)
  private List<OpenspcoopSorgenteDati> openspcoopSorgenteDati = new ArrayList<>();

  /**
   * Use method getOpenspcoopSorgenteDatiList
   * @return List&lt;OpenspcoopSorgenteDati&gt;
  */
  public List<OpenspcoopSorgenteDati> getOpenspcoopSorgenteDati() {
  	return this.getOpenspcoopSorgenteDatiList();
  }

  /**
   * Use method setOpenspcoopSorgenteDatiList
   * @param openspcoopSorgenteDati List&lt;OpenspcoopSorgenteDati&gt;
  */
  public void setOpenspcoopSorgenteDati(List<OpenspcoopSorgenteDati> openspcoopSorgenteDati) {
  	this.setOpenspcoopSorgenteDatiList(openspcoopSorgenteDati);
  }

  /**
   * Use method sizeOpenspcoopSorgenteDatiList
   * @return lunghezza della lista
  */
  public int sizeOpenspcoopSorgenteDati() {
  	return this.sizeOpenspcoopSorgenteDatiList();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String severitaRawEnumValue;

  @XmlAttribute(name="severita",required=false)
  protected Severita severita = (Severita) Severita.toEnumConstantFromString("infoIntegration");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String severitaLog4jRawEnumValue;

  @XmlAttribute(name="severita-log4j",required=false)
  protected Severita severitaLog4j = (Severita) Severita.toEnumConstantFromString("infoIntegration");

}
