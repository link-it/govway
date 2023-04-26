/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for routing-table complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="routing-table"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="destinazione" type="{http://www.openspcoop2.org/core/config}routing-table-destinazione" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="default" type="{http://www.openspcoop2.org/core/config}routing-table-default" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="abilitata" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "routing-table", 
  propOrder = {
  	"destinazione",
  	"_default"
  }
)

@XmlRootElement(name = "routing-table")

public class RoutingTable extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public RoutingTable() {
    super();
  }

  public void addDestinazione(RoutingTableDestinazione destinazione) {
    this.destinazione.add(destinazione);
  }

  public RoutingTableDestinazione getDestinazione(int index) {
    return this.destinazione.get( index );
  }

  public RoutingTableDestinazione removeDestinazione(int index) {
    return this.destinazione.remove( index );
  }

  public List<RoutingTableDestinazione> getDestinazioneList() {
    return this.destinazione;
  }

  public void setDestinazioneList(List<RoutingTableDestinazione> destinazione) {
    this.destinazione=destinazione;
  }

  public int sizeDestinazioneList() {
    return this.destinazione.size();
  }

  public RoutingTableDefault getDefault() {
    return this._default;
  }

  public void setDefault(RoutingTableDefault _default) {
    this._default = _default;
  }

  public Boolean getAbilitata() {
    return this.abilitata;
  }

  public void setAbilitata(Boolean abilitata) {
    this.abilitata = abilitata;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="destinazione",required=true,nillable=false)
  private List<RoutingTableDestinazione> destinazione = new ArrayList<>();

  @XmlElement(name="default",required=true,nillable=false)
  protected RoutingTableDefault _default;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="abilitata",required=false)
  protected Boolean abilitata = Boolean.valueOf("false");

}
