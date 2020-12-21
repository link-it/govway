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


/** <p>Java class for elenco-id-allarmi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-id-allarmi"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-allarme" type="{http://www.openspcoop2.org/core/allarmi}id-allarme" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-id-allarmi", 
  propOrder = {
  	"idAllarme"
  }
)

@XmlRootElement(name = "elenco-id-allarmi")

public class ElencoIdAllarmi extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoIdAllarmi() {
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

  public void addIdAllarme(IdAllarme idAllarme) {
    this.idAllarme.add(idAllarme);
  }

  public IdAllarme getIdAllarme(int index) {
    return this.idAllarme.get( index );
  }

  public IdAllarme removeIdAllarme(int index) {
    return this.idAllarme.remove( index );
  }

  public List<IdAllarme> getIdAllarmeList() {
    return this.idAllarme;
  }

  public void setIdAllarmeList(List<IdAllarme> idAllarme) {
    this.idAllarme=idAllarme;
  }

  public int sizeIdAllarmeList() {
    return this.idAllarme.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="id-allarme",required=true,nillable=false)
  protected List<IdAllarme> idAllarme = new ArrayList<IdAllarme>();

  /**
   * @deprecated Use method getIdAllarmeList
   * @return List&lt;IdAllarme&gt;
  */
  @Deprecated
  public List<IdAllarme> getIdAllarme() {
  	return this.idAllarme;
  }

  /**
   * @deprecated Use method setIdAllarmeList
   * @param idAllarme List&lt;IdAllarme&gt;
  */
  @Deprecated
  public void setIdAllarme(List<IdAllarme> idAllarme) {
  	this.idAllarme=idAllarme;
  }

  /**
   * @deprecated Use method sizeIdAllarmeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeIdAllarme() {
  	return this.idAllarme.size();
  }

}
