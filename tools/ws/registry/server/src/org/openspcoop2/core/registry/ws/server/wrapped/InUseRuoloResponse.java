/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.registry.ws.server.wrapped;

/**
 * <p>Java class for InUseRuoloResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="inUseResponse"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="useInfo" type="{http://www.openspcoop2.org/core/registry}in-use-ruolo" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.registry.ws.server.beans.UseInfo;

/**     
 * InUseRuoloResponse
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "inUseResponse", namespace="http://www.openspcoop2.org/core/registry/management", propOrder = {
    "useInfo"
})
@javax.xml.bind.annotation.XmlRootElement(name = "inUseResponse")
public class InUseRuoloResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="useInfo",required=true,nillable=false)
	private UseInfo useInfo;
	
	public void setUseInfo(UseInfo useInfo){
		this.useInfo = useInfo;
	}
	
	public UseInfo getUseInfo(){
		return this.useInfo;
	}
	
	
	
	
}