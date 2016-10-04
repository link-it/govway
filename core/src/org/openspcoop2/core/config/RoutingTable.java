/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for routing-table complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="routing-table">
 * 		&lt;sequence>
 * 			&lt;element name="destinazione" type="{http://www.openspcoop2.org/core/config}routing-table-destinazione" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="default" type="{http://www.openspcoop2.org/core/config}routing-table-default" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="abilitata" type="{http://www.w3.org/2001/XMLSchema}string" use="optional" default="false"/>
 * &lt;/complexType>
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

public class RoutingTable extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RoutingTable() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="destinazione",required=true,nillable=false)
  protected List<RoutingTableDestinazione> destinazione = new ArrayList<RoutingTableDestinazione>();

  /**
   * @deprecated Use method getDestinazioneList
   * @return List<RoutingTableDestinazione>
  */
  @Deprecated
  public List<RoutingTableDestinazione> getDestinazione() {
  	return this.destinazione;
  }

  /**
   * @deprecated Use method setDestinazioneList
   * @param destinazione List<RoutingTableDestinazione>
  */
  @Deprecated
  public void setDestinazione(List<RoutingTableDestinazione> destinazione) {
  	this.destinazione=destinazione;
  }

  /**
   * @deprecated Use method sizeDestinazioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDestinazione() {
  	return this.destinazione.size();
  }

  @XmlElement(name="default",required=true,nillable=false)
  protected RoutingTableDefault _default;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="abilitata",required=false)
  protected Boolean abilitata = new Boolean("false");

}
