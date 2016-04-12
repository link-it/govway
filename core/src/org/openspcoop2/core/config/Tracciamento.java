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
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for tracciamento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tracciamento">
 * 		&lt;sequence>
 * 			&lt;element name="openspcoop-appender" type="{http://www.openspcoop2.org/core/config}openspcoop-appender" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="openspcoop-sorgente-dati" type="{http://www.openspcoop2.org/core/config}openspcoop-sorgente-dati" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="buste" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/>
 * 		&lt;attribute name="dump" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="dump-binario-porta-delegata" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * 		&lt;attribute name="dump-binario-porta-applicativa" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tracciamento", 
  propOrder = {
  	"openspcoopAppender",
  	"openspcoopSorgenteDati"
  }
)

@XmlRootElement(name = "tracciamento")

public class Tracciamento extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Tracciamento() {
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

  public void set_value_buste(String value) {
    this.buste = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_buste() {
    if(this.buste == null){
    	return null;
    }else{
    	return this.buste.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getBuste() {
    return this.buste;
  }

  public void setBuste(org.openspcoop2.core.config.constants.StatoFunzionalita buste) {
    this.buste = buste;
  }

  public void set_value_dump(String value) {
    this.dump = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_dump() {
    if(this.dump == null){
    	return null;
    }else{
    	return this.dump.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getDump() {
    return this.dump;
  }

  public void setDump(org.openspcoop2.core.config.constants.StatoFunzionalita dump) {
    this.dump = dump;
  }

  public void set_value_dumpBinarioPortaDelegata(String value) {
    this.dumpBinarioPortaDelegata = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_dumpBinarioPortaDelegata() {
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

  public void set_value_dumpBinarioPortaApplicativa(String value) {
    this.dumpBinarioPortaApplicativa = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_dumpBinarioPortaApplicativa() {
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
  protected java.lang.String _value_buste;

  @XmlAttribute(name="buste",required=false)
  protected StatoFunzionalita buste = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @XmlTransient
  protected java.lang.String _value_dump;

  @XmlAttribute(name="dump",required=false)
  protected StatoFunzionalita dump = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_dumpBinarioPortaDelegata;

  @XmlAttribute(name="dump-binario-porta-delegata",required=false)
  protected StatoFunzionalita dumpBinarioPortaDelegata = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @XmlTransient
  protected java.lang.String _value_dumpBinarioPortaApplicativa;

  @XmlAttribute(name="dump-binario-porta-applicativa",required=false)
  protected StatoFunzionalita dumpBinarioPortaApplicativa = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
