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
package org.openspcoop2.core.allarmi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for elenco-allarmi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-allarmi"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="allarme" type="{http://www.openspcoop2.org/core/allarmi}allarme" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-allarmi", 
  propOrder = {
  	"allarme"
  }
)

@XmlRootElement(name = "elenco-allarmi")

public class ElencoAllarmi extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoAllarmi() {
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

  public void addAllarme(Allarme allarme) {
    this.allarme.add(allarme);
  }

  public Allarme getAllarme(int index) {
    return this.allarme.get( index );
  }

  public Allarme removeAllarme(int index) {
    return this.allarme.remove( index );
  }

  public List<Allarme> getAllarmeList() {
    return this.allarme;
  }

  public void setAllarmeList(List<Allarme> allarme) {
    this.allarme=allarme;
  }

  public int sizeAllarmeList() {
    return this.allarme.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="allarme",required=true,nillable=false)
  protected List<Allarme> allarme = new ArrayList<Allarme>();

  /**
   * @deprecated Use method getAllarmeList
   * @return List&lt;Allarme&gt;
  */
  @Deprecated
  public List<Allarme> getAllarme() {
  	return this.allarme;
  }

  /**
   * @deprecated Use method setAllarmeList
   * @param allarme List&lt;Allarme&gt;
  */
  @Deprecated
  public void setAllarme(List<Allarme> allarme) {
  	this.allarme=allarme;
  }

  /**
   * @deprecated Use method sizeAllarmeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAllarme() {
  	return this.allarme.size();
  }

}
