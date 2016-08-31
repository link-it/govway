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
 * <p>Java class for MtomProcessorFlow complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mtom-processor-flow">
 *     &lt;sequence>
 *         &lt;element name="mode" type="{http://www.openspcoop2.org/core/config}MTOMProcessorType" minOccurs="0" maxOccurs="1" default="(MTOMProcessorType) MTOMProcessorType.toEnumConstantFromString("disable")" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.constants.MTOMProcessorType;

/**     
 * MtomProcessorFlow
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "mtom-processor-flow", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "mode"
})
@javax.xml.bind.annotation.XmlRootElement(name = "mtom-processor-flow")
public class MtomProcessorFlow extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="mode",required=false,nillable=false,defaultValue="disable")
	private MTOMProcessorType mode = (MTOMProcessorType) MTOMProcessorType.toEnumConstantFromString("disable");
	
	public void setMode(MTOMProcessorType mode){
		this.mode = mode;
	}
	
	public MTOMProcessorType getMode(){
		return this.mode;
	}
	
	
	
	
}