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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for gruppi-accordo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gruppi-accordo">
 * 		&lt;sequence>
 * 			&lt;element name="gruppo" type="{http://www.openspcoop2.org/core/registry}gruppo-accordo" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
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

public class GruppiAccordo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public GruppiAccordo() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="gruppo",required=true,nillable=false)
  protected List<GruppoAccordo> gruppo = new ArrayList<GruppoAccordo>();

  /**
   * @deprecated Use method getGruppoList
   * @return List<GruppoAccordo>
  */
  @Deprecated
  public List<GruppoAccordo> getGruppo() {
  	return this.gruppo;
  }

  /**
   * @deprecated Use method setGruppoList
   * @param gruppo List<GruppoAccordo>
  */
  @Deprecated
  public void setGruppo(List<GruppoAccordo> gruppo) {
  	this.gruppo=gruppo;
  }

  /**
   * @deprecated Use method sizeGruppoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeGruppo() {
  	return this.gruppo.size();
  }

}
