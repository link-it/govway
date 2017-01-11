/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.core.registry;

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


/** <p>Java class for configurazione-servizio-azione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-servizio-azione">
 * 		&lt;sequence>
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="configurazione-fruitore" type="{http://www.openspcoop2.org/core/registry}configurazione-servizio-azione-fruitore" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configurazione-servizio-azione", 
  propOrder = {
  	"connettore",
  	"configurazioneFruitore"
  }
)

@XmlRootElement(name = "configurazione-servizio-azione")

public class ConfigurazioneServizioAzione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneServizioAzione() {
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

  public Connettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(Connettore connettore) {
    this.connettore = connettore;
  }

  public void addConfigurazioneFruitore(ConfigurazioneServizioAzioneFruitore configurazioneFruitore) {
    this.configurazioneFruitore.add(configurazioneFruitore);
  }

  public ConfigurazioneServizioAzioneFruitore getConfigurazioneFruitore(int index) {
    return this.configurazioneFruitore.get( index );
  }

  public ConfigurazioneServizioAzioneFruitore removeConfigurazioneFruitore(int index) {
    return this.configurazioneFruitore.remove( index );
  }

  public List<ConfigurazioneServizioAzioneFruitore> getConfigurazioneFruitoreList() {
    return this.configurazioneFruitore;
  }

  public void setConfigurazioneFruitoreList(List<ConfigurazioneServizioAzioneFruitore> configurazioneFruitore) {
    this.configurazioneFruitore=configurazioneFruitore;
  }

  public int sizeConfigurazioneFruitoreList() {
    return this.configurazioneFruitore.size();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="connettore",required=true,nillable=false)
  protected Connettore connettore;

  @XmlElement(name="configurazione-fruitore",required=true,nillable=false)
  protected List<ConfigurazioneServizioAzioneFruitore> configurazioneFruitore = new ArrayList<ConfigurazioneServizioAzioneFruitore>();

  /**
   * @deprecated Use method getConfigurazioneFruitoreList
   * @return List<ConfigurazioneServizioAzioneFruitore>
  */
  @Deprecated
  public List<ConfigurazioneServizioAzioneFruitore> getConfigurazioneFruitore() {
  	return this.configurazioneFruitore;
  }

  /**
   * @deprecated Use method setConfigurazioneFruitoreList
   * @param configurazioneFruitore List<ConfigurazioneServizioAzioneFruitore>
  */
  @Deprecated
  public void setConfigurazioneFruitore(List<ConfigurazioneServizioAzioneFruitore> configurazioneFruitore) {
  	this.configurazioneFruitore=configurazioneFruitore;
  }

  /**
   * @deprecated Use method sizeConfigurazioneFruitoreList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeConfigurazioneFruitore() {
  	return this.configurazioneFruitore.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

}
