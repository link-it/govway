/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for dump complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dump"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="configurazione" type="{http://www.openspcoop2.org/core/config}dump-configurazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="configurazione-porta-delegata" type="{http://www.openspcoop2.org/core/config}dump-configurazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="configurazione-porta-applicativa" type="{http://www.openspcoop2.org/core/config}dump-configurazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="openspcoop-appender" type="{http://www.openspcoop2.org/core/config}openspcoop-appender" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="dump-binario-porta-delegata" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="dump-binario-porta-applicativa" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dump", 
  propOrder = {
  	"configurazione",
  	"configurazionePortaDelegata",
  	"configurazionePortaApplicativa",
  	"openspcoopAppender"
  }
)

@XmlRootElement(name = "dump")

public class Dump extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Dump() {
    super();
  }

  @java.lang.Deprecated public DumpConfigurazione getConfigurazione() {
    return this.configurazione;
  }

  @java.lang.Deprecated public void setConfigurazione(DumpConfigurazione configurazione) {
    this.configurazione = configurazione;
  }

  public DumpConfigurazione getConfigurazionePortaDelegata() {
    return this.configurazionePortaDelegata;
  }

  public void setConfigurazionePortaDelegata(DumpConfigurazione configurazionePortaDelegata) {
    this.configurazionePortaDelegata = configurazionePortaDelegata;
  }

  public DumpConfigurazione getConfigurazionePortaApplicativa() {
    return this.configurazionePortaApplicativa;
  }

  public void setConfigurazionePortaApplicativa(DumpConfigurazione configurazionePortaApplicativa) {
    this.configurazionePortaApplicativa = configurazionePortaApplicativa;
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

  public void setDumpBinarioPortaDelegataRawEnumValue(String value) {
    this.dumpBinarioPortaDelegata = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getDumpBinarioPortaDelegataRawEnumValue() {
    if(this.dumpBinarioPortaDelegata == null){
    	return null;
    }else{
    	return this.dumpBinarioPortaDelegata.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getDumpBinarioPortaDelegata() {
    return this.dumpBinarioPortaDelegata;
  }

  public void setDumpBinarioPortaDelegata(org.openspcoop2.core.config.constants.StatoFunzionalita dumpBinarioPortaDelegata) {
    this.dumpBinarioPortaDelegata = dumpBinarioPortaDelegata;
  }

  public void setDumpBinarioPortaApplicativaRawEnumValue(String value) {
    this.dumpBinarioPortaApplicativa = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getDumpBinarioPortaApplicativaRawEnumValue() {
    if(this.dumpBinarioPortaApplicativa == null){
    	return null;
    }else{
    	return this.dumpBinarioPortaApplicativa.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getDumpBinarioPortaApplicativa() {
    return this.dumpBinarioPortaApplicativa;
  }

  public void setDumpBinarioPortaApplicativa(org.openspcoop2.core.config.constants.StatoFunzionalita dumpBinarioPortaApplicativa) {
    this.dumpBinarioPortaApplicativa = dumpBinarioPortaApplicativa;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="configurazione",required=false,nillable=false)
  protected DumpConfigurazione configurazione;

  @XmlElement(name="configurazione-porta-delegata",required=false,nillable=false)
  protected DumpConfigurazione configurazionePortaDelegata;

  @XmlElement(name="configurazione-porta-applicativa",required=false,nillable=false)
  protected DumpConfigurazione configurazionePortaApplicativa;

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

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String dumpBinarioPortaDelegataRawEnumValue;

  @XmlAttribute(name="dump-binario-porta-delegata",required=false)
  protected StatoFunzionalita dumpBinarioPortaDelegata = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String dumpBinarioPortaApplicativaRawEnumValue;

  @XmlAttribute(name="dump-binario-porta-applicativa",required=false)
  protected StatoFunzionalita dumpBinarioPortaApplicativa = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
