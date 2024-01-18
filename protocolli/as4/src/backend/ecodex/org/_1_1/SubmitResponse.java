/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package backend.ecodex.org._1_1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for submitResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="submitResponse"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="messageID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "submitResponse", 
  propOrder = {
  	"messageID"
  }
)

@XmlRootElement(name = "submitResponse")

public class SubmitResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public SubmitResponse() {
    super();
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



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="messageID",required=true,nillable=false)
  private List<java.lang.String> messageID = new ArrayList<>();

  /**
   * Use method getMessageIDList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getMessageID() {
  	return this.getMessageIDList();
  }

  /**
   * Use method setMessageIDList
   * @param messageID List&lt;java.lang.String&gt;
  */
  public void setMessageID(List<java.lang.String> messageID) {
  	this.setMessageIDList(messageID);
  }

  /**
   * Use method sizeMessageIDList
   * @return lunghezza della lista
  */
  public int sizeMessageID() {
  	return this.sizeMessageIDList();
  }

}
