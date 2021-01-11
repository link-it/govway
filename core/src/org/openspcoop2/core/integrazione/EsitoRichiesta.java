/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.integrazione;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for esito-richiesta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="esito-richiesta"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="state" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "esito-richiesta", 
  propOrder = {
  	"messageId"
  }
)

@XmlRootElement(name = "result")

public class EsitoRichiesta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public EsitoRichiesta() {
  }

  public java.lang.String getMessageId() {
    return this.messageId;
  }

  public void setMessageId(java.lang.String messageId) {
    this.messageId = messageId;
  }

  public java.lang.String getState() {
    return this.state;
  }

  public void setState(java.lang.String state) {
    this.state = state;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.integrazione.model.EsitoRichiestaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.integrazione.EsitoRichiesta.modelStaticInstance==null){
  			org.openspcoop2.core.integrazione.EsitoRichiesta.modelStaticInstance = new org.openspcoop2.core.integrazione.model.EsitoRichiestaModel();
	  }
  }
  public static org.openspcoop2.core.integrazione.model.EsitoRichiestaModel model(){
	  if(org.openspcoop2.core.integrazione.EsitoRichiesta.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.integrazione.EsitoRichiesta.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="messageId",required=true,nillable=false)
  protected java.lang.String messageId;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="state",required=true)
  protected java.lang.String state;

}
