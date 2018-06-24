/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package it.gov.spcoop.sica.wsbl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for eventListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventListType">
 * 		&lt;sequence>
 * 			&lt;element name="event" type="{http://spcoop.gov.it/sica/wsbl}eventType" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "eventListType", 
  propOrder = {
  	"event"
  }
)

@XmlRootElement(name = "eventListType")

public class EventListType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public EventListType() {
  }

  public void addEvent(EventType event) {
    this.event.add(event);
  }

  public EventType getEvent(int index) {
    return this.event.get( index );
  }

  public EventType removeEvent(int index) {
    return this.event.remove( index );
  }

  public List<EventType> getEventList() {
    return this.event;
  }

  public void setEventList(List<EventType> event) {
    this.event=event;
  }

  public int sizeEventList() {
    return this.event.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="event",required=true,nillable=false)
  protected List<EventType> event = new ArrayList<EventType>();

  /**
   * @deprecated Use method getEventList
   * @return List<EventType>
  */
  @Deprecated
  public List<EventType> getEvent() {
  	return this.event;
  }

  /**
   * @deprecated Use method setEventList
   * @param event List<EventType>
  */
  @Deprecated
  public void setEvent(List<EventType> event) {
  	this.event=event;
  }

  /**
   * @deprecated Use method sizeEventList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeEvent() {
  	return this.event.size();
  }

}
