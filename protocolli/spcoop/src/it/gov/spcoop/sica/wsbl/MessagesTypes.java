/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package it.gov.spcoop.sica.wsbl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class MessagesTypes.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class MessagesTypes extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;


  public MessagesTypes() {
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

  public void addMessage(MessagesTypesMessage message) {
    this.message.add(message);
  }

  public MessagesTypesMessage getMessage(int index) {
    return this.message.get( index );
  }

  public MessagesTypesMessage removeMessage(int index) {
    return this.message.remove( index );
  }

  public List<MessagesTypesMessage> getMessageList() {
    return this.message;
  }

  public void setMessageList(List<MessagesTypesMessage> message) {
    this.message=message;
  }

  public int sizeMessageList() {
    return this.message.size();
  }

  private static final long serialVersionUID = 1L;

	@Override
	public String serialize(org.openspcoop2.utils.beans.WriteToSerializerType type) throws org.openspcoop2.utils.UtilsException {
		if(type!=null && org.openspcoop2.utils.beans.WriteToSerializerType.JAXB.equals(type)){
			throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
		}
		else{
			return super.serialize(type);
		}
	}
	@Override
	public String toXml_Jaxb() throws org.openspcoop2.utils.UtilsException {
		throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
	}

  protected List<MessagesTypesMessage> message = new ArrayList<MessagesTypesMessage>();

  /**
   * @deprecated Use method getMessageList
   * @return List<MessagesTypesMessage>
  */
  @Deprecated
  public List<MessagesTypesMessage> getMessage() {
  	return this.message;
  }

  /**
   * @deprecated Use method setMessageList
   * @param message List<MessagesTypesMessage>
  */
  @Deprecated
  public void setMessage(List<MessagesTypesMessage> message) {
  	this.message=message;
  }

  /**
   * @deprecated Use method sizeMessageList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeMessage() {
  	return this.message.size();
  }

  public static final String MESSAGE = "message";

}
