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
package org.openspcoop2.core.registry;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for configurazione-servizio-azione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-servizio-azione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "configurazione-servizio-azione", 
  propOrder = {
  	"connettore",
  	"azione"
  }
)

@XmlRootElement(name = "configurazione-servizio-azione")

public class ConfigurazioneServizioAzione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneServizioAzione() {
    super();
  }

  public Connettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(Connettore connettore) {
    this.connettore = connettore;
  }

  public void addAzione(java.lang.String azione) {
    this.azione.add(azione);
  }

  public java.lang.String getAzione(int index) {
    return this.azione.get( index );
  }

  public java.lang.String removeAzione(int index) {
    return this.azione.remove( index );
  }

  public List<java.lang.String> getAzioneList() {
    return this.azione;
  }

  public void setAzioneList(List<java.lang.String> azione) {
    this.azione=azione;
  }

  public int sizeAzioneList() {
    return this.azione.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="connettore",required=true,nillable=false)
  protected Connettore connettore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=true,nillable=false)
  private List<java.lang.String> azione = new ArrayList<>();

  /**
   * Use method getAzioneList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getAzione() {
  	return this.getAzioneList();
  }

  /**
   * Use method setAzioneList
   * @param azione List&lt;java.lang.String&gt;
  */
  public void setAzione(List<java.lang.String> azione) {
  	this.setAzioneList(azione);
  }

  /**
   * Use method sizeAzioneList
   * @return lunghezza della lista
  */
  public int sizeAzione() {
  	return this.sizeAzioneList();
  }

}
