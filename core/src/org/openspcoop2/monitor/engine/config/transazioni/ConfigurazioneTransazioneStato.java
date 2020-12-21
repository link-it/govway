/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config.transazioni;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.monitor.engine.config.transazioni.constants.TipoControllo;
import org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMessaggio;
import java.io.Serializable;


/** <p>Java class for configurazione-transazione-stato complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-transazione-stato"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-controllo" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}tipo-controllo" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-messaggio" type="{http://www.openspcoop2.org/monitor/engine/config/transazioni}tipo-messaggio" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="valore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="xpath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configurazione-transazione-stato", 
  propOrder = {
  	"enabled",
  	"nome",
  	"tipoControllo",
  	"tipoMessaggio",
  	"valore",
  	"xpath"
  }
)

@XmlRootElement(name = "configurazione-transazione-stato")

public class ConfigurazioneTransazioneStato extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneTransazioneStato() {
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

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean getEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void set_value_tipoControllo(String value) {
    this.tipoControllo = (TipoControllo) TipoControllo.toEnumConstantFromString(value);
  }

  public String get_value_tipoControllo() {
    if(this.tipoControllo == null){
    	return null;
    }else{
    	return this.tipoControllo.toString();
    }
  }

  public org.openspcoop2.monitor.engine.config.transazioni.constants.TipoControllo getTipoControllo() {
    return this.tipoControllo;
  }

  public void setTipoControllo(org.openspcoop2.monitor.engine.config.transazioni.constants.TipoControllo tipoControllo) {
    this.tipoControllo = tipoControllo;
  }

  public void set_value_tipoMessaggio(String value) {
    this.tipoMessaggio = (TipoMessaggio) TipoMessaggio.toEnumConstantFromString(value);
  }

  public String get_value_tipoMessaggio() {
    if(this.tipoMessaggio == null){
    	return null;
    }else{
    	return this.tipoMessaggio.toString();
    }
  }

  public org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMessaggio getTipoMessaggio() {
    return this.tipoMessaggio;
  }

  public void setTipoMessaggio(org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMessaggio tipoMessaggio) {
    this.tipoMessaggio = tipoMessaggio;
  }

  public java.lang.String getValore() {
    return this.valore;
  }

  public void setValore(java.lang.String valore) {
    this.valore = valore;
  }

  public java.lang.String getXpath() {
    return this.xpath;
  }

  public void setXpath(java.lang.String xpath) {
    this.xpath = xpath;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="enabled",required=true,nillable=false)
  protected boolean enabled;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoControllo;

  @XmlElement(name="tipo-controllo",required=true,nillable=false)
  protected TipoControllo tipoControllo;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoMessaggio;

  @XmlElement(name="tipo-messaggio",required=true,nillable=false)
  protected TipoMessaggio tipoMessaggio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="valore",required=false,nillable=false)
  protected java.lang.String valore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="xpath",required=true,nillable=false)
  protected java.lang.String xpath;

}
