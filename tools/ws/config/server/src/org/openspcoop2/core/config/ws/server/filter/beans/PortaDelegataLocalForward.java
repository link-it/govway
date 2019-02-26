/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
 * <p>Java class for PortaDelegataLocalForward complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-delegata-local-forward">
 *     &lt;sequence>
 *         &lt;element name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="porta-applicativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
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
 * PortaDelegataLocalForward
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "porta-delegata-local-forward", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
    "stato",
    "portaApplicativa"
})
@javax.xml.bind.annotation.XmlRootElement(name = "porta-delegata-local-forward")
public class PortaDelegataLocalForward extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="stato",required=false,nillable=false)
	private StatoFunzionalita stato;
	
	public void setStato(StatoFunzionalita stato){
		this.stato = stato;
	}
	
	public StatoFunzionalita getStato(){
		return this.stato;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="porta-applicativa",required=false,nillable=false)
	private String portaApplicativa;
	
	public void setPortaApplicativa(String portaApplicativa){
		this.portaApplicativa = portaApplicativa;
	}
	
	public String getPortaApplicativa(){
		return this.portaApplicativa;
	}
	
	
	
	
}