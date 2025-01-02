/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.plugins;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for elenco-configurazione-filtro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-configurazione-filtro"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="configurazione-filtro" type="{http://www.openspcoop2.org/core/plugins}configurazione-filtro" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-configurazione-filtro", 
  propOrder = {
  	"configurazioneFiltro"
  }
)

@XmlRootElement(name = "elenco-configurazione-filtro")

public class ElencoConfigurazioneFiltro extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ElencoConfigurazioneFiltro() {
    super();
  }

  public void addConfigurazioneFiltro(ConfigurazioneFiltro configurazioneFiltro) {
    this.configurazioneFiltro.add(configurazioneFiltro);
  }

  public ConfigurazioneFiltro getConfigurazioneFiltro(int index) {
    return this.configurazioneFiltro.get( index );
  }

  public ConfigurazioneFiltro removeConfigurazioneFiltro(int index) {
    return this.configurazioneFiltro.remove( index );
  }

  public List<ConfigurazioneFiltro> getConfigurazioneFiltroList() {
    return this.configurazioneFiltro;
  }

  public void setConfigurazioneFiltroList(List<ConfigurazioneFiltro> configurazioneFiltro) {
    this.configurazioneFiltro=configurazioneFiltro;
  }

  public int sizeConfigurazioneFiltroList() {
    return this.configurazioneFiltro.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="configurazione-filtro",required=true,nillable=false)
  private List<ConfigurazioneFiltro> configurazioneFiltro = new ArrayList<>();

  /**
   * Use method getConfigurazioneFiltroList
   * @return List&lt;ConfigurazioneFiltro&gt;
  */
  public List<ConfigurazioneFiltro> getConfigurazioneFiltro() {
  	return this.getConfigurazioneFiltroList();
  }

  /**
   * Use method setConfigurazioneFiltroList
   * @param configurazioneFiltro List&lt;ConfigurazioneFiltro&gt;
  */
  public void setConfigurazioneFiltro(List<ConfigurazioneFiltro> configurazioneFiltro) {
  	this.setConfigurazioneFiltroList(configurazioneFiltro);
  }

  /**
   * Use method sizeConfigurazioneFiltroList
   * @return lunghezza della lista
  */
  public int sizeConfigurazioneFiltro() {
  	return this.sizeConfigurazioneFiltroList();
  }

}
