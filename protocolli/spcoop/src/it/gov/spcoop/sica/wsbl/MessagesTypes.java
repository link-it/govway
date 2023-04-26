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
package it.gov.spcoop.sica.wsbl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for messagesTypes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messagesTypes"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="message" type="{http://spcoop.gov.it/sica/wsbl}message" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "messagesTypes", 
  propOrder = {
  	"message"
  }
)

@XmlRootElement(name = "messagesTypes")

public class MessagesTypes extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public MessagesTypes() {
    super();
  }

  public void addMessage(Message message) {
    this.message.add(message);
  }

  public Message getMessage(int index) {
    return this.message.get( index );
  }

  public Message removeMessage(int index) {
    return this.message.remove( index );
  }

  public List<Message> getMessageList() {
    return this.message;
  }

  public void setMessageList(List<Message> message) {
    this.message=message;
  }

  public int sizeMessageList() {
    return this.message.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="message",required=true,nillable=false)
  private List<Message> message = new ArrayList<>();

  /**
   * Use method getMessageList
   * @return List&lt;Message&gt;
  */
  public List<Message> getMessage() {
  	return this.getMessageList();
  }

  /**
   * Use method setMessageList
   * @param message List&lt;Message&gt;
  */
  public void setMessage(List<Message> message) {
  	this.setMessageList(message);
  }

  /**
   * Use method sizeMessageList
   * @return lunghezza della lista
  */
  public int sizeMessage() {
  	return this.sizeMessageList();
  }

}
