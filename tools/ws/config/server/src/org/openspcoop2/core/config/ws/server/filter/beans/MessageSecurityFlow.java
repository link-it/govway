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
package org.openspcoop2.core.config.ws.server.filter.beans;

/**
 * <p>Java class for MessageSecurityFlow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="message-security-flow">
 *     &lt;sequence>
 *         &lt;element name="apply-to-mtom" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
 import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.constants.StatoFunzionalita;

/**     
 * MessageSecurityFlow
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "message-security-flow", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "applyToMtom"
})
@javax.xml.bind.annotation.XmlRootElement(name = "message-security-flow")
public class MessageSecurityFlow extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable  {
	
	private static final long serialVersionUID = -1L;

	
	
	@XmlElement(name="apply-to-mtom",required=false,nillable=false)
	private StatoFunzionalita applyToMtom;
	
	public void setApplyToMtom(StatoFunzionalita applyToMtom){
		this.applyToMtom = applyToMtom;
	}
	
	public StatoFunzionalita getApplyToMtom(){
		return this.applyToMtom;
	}
	
	
	
	
}