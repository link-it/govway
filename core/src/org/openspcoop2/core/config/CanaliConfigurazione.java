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


/** <p>Java class for canali-configurazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="canali-configurazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="canale" type="{http://www.openspcoop2.org/core/config}canale-configurazione" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="nodo" type="{http://www.openspcoop2.org/core/config}canale-configurazione-nodo" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "canali-configurazione", 
  propOrder = {
  	"canale",
  	"nodo"
  }
)

@XmlRootElement(name = "canali-configurazione")

public class CanaliConfigurazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public CanaliConfigurazione() {
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

  public void addCanale(CanaleConfigurazione canale) {
    this.canale.add(canale);
  }

  public CanaleConfigurazione getCanale(int index) {
    return this.canale.get( index );
  }

  public CanaleConfigurazione removeCanale(int index) {
    return this.canale.remove( index );
  }

  public List<CanaleConfigurazione> getCanaleList() {
    return this.canale;
  }

  public void setCanaleList(List<CanaleConfigurazione> canale) {
    this.canale=canale;
  }

  public int sizeCanaleList() {
    return this.canale.size();
  }

  public void addNodo(CanaleConfigurazioneNodo nodo) {
    this.nodo.add(nodo);
  }

  public CanaleConfigurazioneNodo getNodo(int index) {
    return this.nodo.get( index );
  }

  public CanaleConfigurazioneNodo removeNodo(int index) {
    return this.nodo.remove( index );
  }

  public List<CanaleConfigurazioneNodo> getNodoList() {
    return this.nodo;
  }

  public void setNodoList(List<CanaleConfigurazioneNodo> nodo) {
    this.nodo=nodo;
  }

  public int sizeNodoList() {
    return this.nodo.size();
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

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="canale",required=true,nillable=false)
  protected List<CanaleConfigurazione> canale = new ArrayList<CanaleConfigurazione>();

  /**
   * @deprecated Use method getCanaleList
   * @return List&lt;CanaleConfigurazione&gt;
  */
  @Deprecated
  public List<CanaleConfigurazione> getCanale() {
  	return this.canale;
  }

  /**
   * @deprecated Use method setCanaleList
   * @param canale List&lt;CanaleConfigurazione&gt;
  */
  @Deprecated
  public void setCanale(List<CanaleConfigurazione> canale) {
  	this.canale=canale;
  }

  /**
   * @deprecated Use method sizeCanaleList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeCanale() {
  	return this.canale.size();
  }

  @XmlElement(name="nodo",required=true,nillable=false)
  protected List<CanaleConfigurazioneNodo> nodo = new ArrayList<CanaleConfigurazioneNodo>();

  /**
   * @deprecated Use method getNodoList
   * @return List&lt;CanaleConfigurazioneNodo&gt;
  */
  @Deprecated
  public List<CanaleConfigurazioneNodo> getNodo() {
  	return this.nodo;
  }

  /**
   * @deprecated Use method setNodoList
   * @param nodo List&lt;CanaleConfigurazioneNodo&gt;
  */
  @Deprecated
  public void setNodo(List<CanaleConfigurazioneNodo> nodo) {
  	this.nodo=nodo;
  }

  /**
   * @deprecated Use method sizeNodoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeNodo() {
  	return this.nodo.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
