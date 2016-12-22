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
package backend.ecodex.org._1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for sendResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sendResponse">
 * 		&lt;sequence>
 * 			&lt;element name="messageID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "sendResponse", 
  propOrder = {
  	"messageID"
  }
)

@XmlRootElement(name = "sendResponse")

public class SendResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SendResponse() {
  }

  public void addMessageID(java.lang.String messageID) {
    this.messageID.add(messageID);
  }

  public java.lang.String getMessageID(int index) {
    return this.messageID.get( index );
  }

  public java.lang.String removeMessageID(int index) {
    return this.messageID.remove( index );
  }

  public List<java.lang.String> getMessageIDList() {
    return this.messageID;
  }

  public void setMessageIDList(List<java.lang.String> messageID) {
    this.messageID=messageID;
  }

  public int sizeMessageIDList() {
    return this.messageID.size();
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="messageID",required=true,nillable=false)
  protected List<java.lang.String> messageID = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getMessageIDList
   * @return List<java.lang.String>
  */
  @Deprecated
  public List<java.lang.String> getMessageID() {
  	return this.messageID;
  }

  /**
   * @deprecated Use method setMessageIDList
   * @param messageID List<java.lang.String>
  */
  @Deprecated
  public void setMessageID(List<java.lang.String> messageID) {
  	this.messageID=messageID;
  }

  /**
   * @deprecated Use method sizeMessageIDList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeMessageID() {
  	return this.messageID.size();
  }

}
