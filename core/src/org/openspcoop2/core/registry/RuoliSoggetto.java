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


/** <p>Java class for ruoli-soggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ruoli-soggetto">
 * 		&lt;sequence>
 * 			&lt;element name="ruolo" type="{http://www.openspcoop2.org/core/registry}ruolo-soggetto" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "ruoli-soggetto", 
  propOrder = {
  	"ruolo"
  }
)

@XmlRootElement(name = "ruoli-soggetto")

public class RuoliSoggetto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RuoliSoggetto() {
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

  public void addRuolo(RuoloSoggetto ruolo) {
    this.ruolo.add(ruolo);
  }

  public RuoloSoggetto getRuolo(int index) {
    return this.ruolo.get( index );
  }

  public RuoloSoggetto removeRuolo(int index) {
    return this.ruolo.remove( index );
  }

  public List<RuoloSoggetto> getRuoloList() {
    return this.ruolo;
  }

  public void setRuoloList(List<RuoloSoggetto> ruolo) {
    this.ruolo=ruolo;
  }

  public int sizeRuoloList() {
    return this.ruolo.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="ruolo",required=true,nillable=false)
  protected List<RuoloSoggetto> ruolo = new ArrayList<RuoloSoggetto>();

  /**
   * @deprecated Use method getRuoloList
   * @return List<RuoloSoggetto>
  */
  @Deprecated
  public List<RuoloSoggetto> getRuolo() {
  	return this.ruolo;
  }

  /**
   * @deprecated Use method setRuoloList
   * @param ruolo List<RuoloSoggetto>
  */
  @Deprecated
  public void setRuolo(List<RuoloSoggetto> ruolo) {
  	this.ruolo=ruolo;
  }

  /**
   * @deprecated Use method sizeRuoloList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRuolo() {
  	return this.ruolo.size();
  }

}
