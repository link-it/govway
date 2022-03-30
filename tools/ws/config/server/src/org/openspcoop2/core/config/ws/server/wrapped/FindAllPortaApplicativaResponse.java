/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.ws.server.wrapped;

/**
 * <p>Java class for FindAllPortaApplicativaResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="findAllResponse"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="findReturn" type="{http://www.openspcoop2.org/core/config}porta-applicativa" maxOccurs="unbounded" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.config.PortaApplicativa;

/**     
 * FindAllPortaApplicativaResponse
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "findAllResponse", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "findReturn"
})
@javax.xml.bind.annotation.XmlRootElement(name = "findAllResponse")
public class FindAllPortaApplicativaResponse extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="findReturn",required=true,nillable=false)
	private java.util.List<PortaApplicativa> findReturn;
	
	public void setFindReturn(java.util.List<PortaApplicativa> findReturn){
		this.findReturn = findReturn;
	}
	
	public java.util.List<PortaApplicativa> getFindReturn(){
		return this.findReturn;
	}
	
	
	
	
}