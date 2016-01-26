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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for MessageSecurity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="message-security">
 *     &lt;sequence>
 *         &lt;element name="request-flow" type="{http://www.openspcoop2.org/core/config/management}message-security-flow" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="response-flow" type="{http://www.openspcoop2.org/core/config/management}message-security-flow" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
 import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.ws.server.filter.beans.MessageSecurityFlow;

/**     
 * MessageSecurity
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "message-security", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "requestFlow",
    "responseFlow"
})
@javax.xml.bind.annotation.XmlRootElement(name = "message-security")
public class MessageSecurity extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable  {
	
	private static final long serialVersionUID = -1L;

	
	
	@XmlElement(name="request-flow",required=false,nillable=false)
	private MessageSecurityFlow requestFlow;
	
	public void setRequestFlow(MessageSecurityFlow requestFlow){
		this.requestFlow = requestFlow;
	}
	
	public MessageSecurityFlow getRequestFlow(){
		return this.requestFlow;
	}
	
	
	@XmlElement(name="response-flow",required=false,nillable=false)
	private MessageSecurityFlow responseFlow;
	
	public void setResponseFlow(MessageSecurityFlow responseFlow){
		this.responseFlow = responseFlow;
	}
	
	public MessageSecurityFlow getResponseFlow(){
		return this.responseFlow;
	}
	
	
	
	
}