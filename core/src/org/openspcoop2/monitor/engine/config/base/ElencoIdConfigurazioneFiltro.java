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
package org.openspcoop2.monitor.engine.config.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for elenco-id-configurazione-filtro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-id-configurazione-filtro"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-configurazione-filtro" type="{http://www.openspcoop2.org/monitor/engine/config/base}id-configurazione-filtro" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-id-configurazione-filtro", 
  propOrder = {
  	"idConfigurazioneFiltro"
  }
)

@XmlRootElement(name = "elenco-id-configurazione-filtro")

public class ElencoIdConfigurazioneFiltro extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoIdConfigurazioneFiltro() {
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

  public void addIdConfigurazioneFiltro(IdConfigurazioneFiltro idConfigurazioneFiltro) {
    this.idConfigurazioneFiltro.add(idConfigurazioneFiltro);
  }

  public IdConfigurazioneFiltro getIdConfigurazioneFiltro(int index) {
    return this.idConfigurazioneFiltro.get( index );
  }

  public IdConfigurazioneFiltro removeIdConfigurazioneFiltro(int index) {
    return this.idConfigurazioneFiltro.remove( index );
  }

  public List<IdConfigurazioneFiltro> getIdConfigurazioneFiltroList() {
    return this.idConfigurazioneFiltro;
  }

  public void setIdConfigurazioneFiltroList(List<IdConfigurazioneFiltro> idConfigurazioneFiltro) {
    this.idConfigurazioneFiltro=idConfigurazioneFiltro;
  }

  public int sizeIdConfigurazioneFiltroList() {
    return this.idConfigurazioneFiltro.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="id-configurazione-filtro",required=true,nillable=false)
  protected List<IdConfigurazioneFiltro> idConfigurazioneFiltro = new ArrayList<IdConfigurazioneFiltro>();

  /**
   * @deprecated Use method getIdConfigurazioneFiltroList
   * @return List&lt;IdConfigurazioneFiltro&gt;
  */
  @Deprecated
  public List<IdConfigurazioneFiltro> getIdConfigurazioneFiltro() {
  	return this.idConfigurazioneFiltro;
  }

  /**
   * @deprecated Use method setIdConfigurazioneFiltroList
   * @param idConfigurazioneFiltro List&lt;IdConfigurazioneFiltro&gt;
  */
  @Deprecated
  public void setIdConfigurazioneFiltro(List<IdConfigurazioneFiltro> idConfigurazioneFiltro) {
  	this.idConfigurazioneFiltro=idConfigurazioneFiltro;
  }

  /**
   * @deprecated Use method sizeIdConfigurazioneFiltroList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeIdConfigurazioneFiltro() {
  	return this.idConfigurazioneFiltro.size();
  }

}
