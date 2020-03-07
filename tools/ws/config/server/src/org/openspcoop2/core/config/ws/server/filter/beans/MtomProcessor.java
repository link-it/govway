/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
 * <p>Java class for MtomProcessor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mtom-processor"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="request-flow" type="{http://www.openspcoop2.org/core/config/management}mtom-processor-flow" minOccurs="0" maxOccurs="1" /&gt;
 *         &lt;element name="response-flow" type="{http://www.openspcoop2.org/core/config/management}mtom-processor-flow" minOccurs="0" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.ws.server.filter.beans.MtomProcessorFlow;

/**     
 * MtomProcessor
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "mtom-processor", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "requestFlow",
    "responseFlow"
})
@javax.xml.bind.annotation.XmlRootElement(name = "mtom-processor")
public class MtomProcessor extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="request-flow",required=false,nillable=false)
	private MtomProcessorFlow requestFlow;
	
	public void setRequestFlow(MtomProcessorFlow requestFlow){
		this.requestFlow = requestFlow;
	}
	
	public MtomProcessorFlow getRequestFlow(){
		return this.requestFlow;
	}
	
	
	@XmlElement(name="response-flow",required=false,nillable=false)
	private MtomProcessorFlow responseFlow;
	
	public void setResponseFlow(MtomProcessorFlow responseFlow){
		this.responseFlow = responseFlow;
	}
	
	public MtomProcessorFlow getResponseFlow(){
		return this.responseFlow;
	}
	
	
	
	
}