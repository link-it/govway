/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for configurazione-url-invocazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-url-invocazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="regola" type="{http://www.openspcoop2.org/core/config}configurazione-url-invocazione-regola" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="base-url" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="base-url-fruizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configurazione-url-invocazione", 
  propOrder = {
  	"regola"
  }
)

@XmlRootElement(name = "configurazione-url-invocazione")

public class ConfigurazioneUrlInvocazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneUrlInvocazione() {
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

  public void addRegola(ConfigurazioneUrlInvocazioneRegola regola) {
    this.regola.add(regola);
  }

  public ConfigurazioneUrlInvocazioneRegola getRegola(int index) {
    return this.regola.get( index );
  }

  public ConfigurazioneUrlInvocazioneRegola removeRegola(int index) {
    return this.regola.remove( index );
  }

  public List<ConfigurazioneUrlInvocazioneRegola> getRegolaList() {
    return this.regola;
  }

  public void setRegolaList(List<ConfigurazioneUrlInvocazioneRegola> regola) {
    this.regola=regola;
  }

  public int sizeRegolaList() {
    return this.regola.size();
  }

  public java.lang.String getBaseUrl() {
    return this.baseUrl;
  }

  public void setBaseUrl(java.lang.String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public java.lang.String getBaseUrlFruizione() {
    return this.baseUrlFruizione;
  }

  public void setBaseUrlFruizione(java.lang.String baseUrlFruizione) {
    this.baseUrlFruizione = baseUrlFruizione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="regola",required=true,nillable=false)
  protected List<ConfigurazioneUrlInvocazioneRegola> regola = new ArrayList<ConfigurazioneUrlInvocazioneRegola>();

  /**
   * @deprecated Use method getRegolaList
   * @return List&lt;ConfigurazioneUrlInvocazioneRegola&gt;
  */
  @Deprecated
  public List<ConfigurazioneUrlInvocazioneRegola> getRegola() {
  	return this.regola;
  }

  /**
   * @deprecated Use method setRegolaList
   * @param regola List&lt;ConfigurazioneUrlInvocazioneRegola&gt;
  */
  @Deprecated
  public void setRegola(List<ConfigurazioneUrlInvocazioneRegola> regola) {
  	this.regola=regola;
  }

  /**
   * @deprecated Use method sizeRegolaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRegola() {
  	return this.regola.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="base-url",required=true)
  protected java.lang.String baseUrl;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="base-url-fruizione",required=false)
  protected java.lang.String baseUrlFruizione;

}
