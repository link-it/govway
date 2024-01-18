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


/** <p>Java class for gruppi-accordo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gruppi-accordo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="gruppo" type="{http://www.openspcoop2.org/core/registry}gruppo-accordo" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "gruppi-accordo", 
  propOrder = {
  	"gruppo"
  }
)

@XmlRootElement(name = "gruppi-accordo")

public class GruppiAccordo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public GruppiAccordo() {
    super();
  }

  public void addGruppo(GruppoAccordo gruppo) {
    this.gruppo.add(gruppo);
  }

  public GruppoAccordo getGruppo(int index) {
    return this.gruppo.get( index );
  }

  public GruppoAccordo removeGruppo(int index) {
    return this.gruppo.remove( index );
  }

  public List<GruppoAccordo> getGruppoList() {
    return this.gruppo;
  }

  public void setGruppoList(List<GruppoAccordo> gruppo) {
    this.gruppo=gruppo;
  }

  public int sizeGruppoList() {
    return this.gruppo.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="gruppo",required=true,nillable=false)
  private List<GruppoAccordo> gruppo = new ArrayList<>();

  /**
   * Use method getGruppoList
   * @return List&lt;GruppoAccordo&gt;
  */
  public List<GruppoAccordo> getGruppo() {
  	return this.getGruppoList();
  }

  /**
   * Use method setGruppoList
   * @param gruppo List&lt;GruppoAccordo&gt;
  */
  public void setGruppo(List<GruppoAccordo> gruppo) {
  	this.setGruppoList(gruppo);
  }

  /**
   * Use method sizeGruppoList
   * @return lunghezza della lista
  */
  public int sizeGruppo() {
  	return this.sizeGruppoList();
  }

}
