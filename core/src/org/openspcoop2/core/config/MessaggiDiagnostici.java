/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
 * &lt;complexType name="messaggi-diagnostici">
 * 		&lt;sequence>
 * 			&lt;element name="openspcoop-appender" type="{http://www.openspcoop2.org/core/config}openspcoop-appender" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="openspcoop-sorgente-dati" type="{http://www.openspcoop2.org/core/config}openspcoop-sorgente-dati" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="severita" type="{http://www.openspcoop2.org/core/config}Severita" use="optional" default="infoIntegration"/>
 * 		&lt;attribute name="severita-log4j" type="{http://www.openspcoop2.org/core/config}Severita" use="optional" default="infoIntegration"/>
 * &lt;/complexType>
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

public class MessaggiDiagnostici extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MessaggiDiagnostici() {
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

  public void set_value_severita(String value) {
    this.severita = (Severita) Severita.toEnumConstantFromString(value);
  }

  public String get_value_severita() {
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

  public void set_value_severitaLog4j(String value) {
    this.severitaLog4j = (Severita) Severita.toEnumConstantFromString(value);
  }

  public String get_value_severitaLog4j() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="openspcoop-appender",required=true,nillable=false)
  protected List<OpenspcoopAppender> openspcoopAppender = new ArrayList<OpenspcoopAppender>();

  /**
   * @deprecated Use method getOpenspcoopAppenderList
   * @return List<OpenspcoopAppender>
  */
  @Deprecated
  public List<OpenspcoopAppender> getOpenspcoopAppender() {
  	return this.openspcoopAppender;
  }

  /**
   * @deprecated Use method setOpenspcoopAppenderList
   * @param openspcoopAppender List<OpenspcoopAppender>
  */
  @Deprecated
  public void setOpenspcoopAppender(List<OpenspcoopAppender> openspcoopAppender) {
  	this.openspcoopAppender=openspcoopAppender;
  }

  /**
   * @deprecated Use method sizeOpenspcoopAppenderList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeOpenspcoopAppender() {
  	return this.openspcoopAppender.size();
  }

  @XmlElement(name="openspcoop-sorgente-dati",required=true,nillable=false)
  protected List<OpenspcoopSorgenteDati> openspcoopSorgenteDati = new ArrayList<OpenspcoopSorgenteDati>();

  /**
   * @deprecated Use method getOpenspcoopSorgenteDatiList
   * @return List<OpenspcoopSorgenteDati>
  */
  @Deprecated
  public List<OpenspcoopSorgenteDati> getOpenspcoopSorgenteDati() {
  	return this.openspcoopSorgenteDati;
  }

  /**
   * @deprecated Use method setOpenspcoopSorgenteDatiList
   * @param openspcoopSorgenteDati List<OpenspcoopSorgenteDati>
  */
  @Deprecated
  public void setOpenspcoopSorgenteDati(List<OpenspcoopSorgenteDati> openspcoopSorgenteDati) {
  	this.openspcoopSorgenteDati=openspcoopSorgenteDati;
  }

  /**
   * @deprecated Use method sizeOpenspcoopSorgenteDatiList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeOpenspcoopSorgenteDati() {
  	return this.openspcoopSorgenteDati.size();
  }

  @XmlTransient
  protected java.lang.String _value_severita;

  @XmlAttribute(name="severita",required=false)
  protected Severita severita = (Severita) Severita.toEnumConstantFromString("infoIntegration");

  @XmlTransient
  protected java.lang.String _value_severitaLog4j;

  @XmlAttribute(name="severita-log4j",required=false)
  protected Severita severitaLog4j = (Severita) Severita.toEnumConstantFromString("infoIntegration");

}
