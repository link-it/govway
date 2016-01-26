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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for stato-servizi-pdd complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stato-servizi-pdd">
 * 		&lt;sequence>
 * 			&lt;element name="porta-delegata" type="{http://www.openspcoop2.org/core/config}stato-servizi-pdd-porta-delegata" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="porta-applicativa" type="{http://www.openspcoop2.org/core/config}stato-servizi-pdd-porta-applicativa" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="integration-manager" type="{http://www.openspcoop2.org/core/config}stato-servizi-pdd-integration-manager" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "stato-servizi-pdd", 
  propOrder = {
  	"portaDelegata",
  	"portaApplicativa",
  	"integrationManager"
  }
)

@XmlRootElement(name = "stato-servizi-pdd")

public class StatoServiziPdd extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public StatoServiziPdd() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public StatoServiziPddPortaDelegata getPortaDelegata() {
    return this.portaDelegata;
  }

  public void setPortaDelegata(StatoServiziPddPortaDelegata portaDelegata) {
    this.portaDelegata = portaDelegata;
  }

  public StatoServiziPddPortaApplicativa getPortaApplicativa() {
    return this.portaApplicativa;
  }

  public void setPortaApplicativa(StatoServiziPddPortaApplicativa portaApplicativa) {
    this.portaApplicativa = portaApplicativa;
  }

  public StatoServiziPddIntegrationManager getIntegrationManager() {
    return this.integrationManager;
  }

  public void setIntegrationManager(StatoServiziPddIntegrationManager integrationManager) {
    this.integrationManager = integrationManager;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="porta-delegata",required=false,nillable=false)
  protected StatoServiziPddPortaDelegata portaDelegata;

  @XmlElement(name="porta-applicativa",required=false,nillable=false)
  protected StatoServiziPddPortaApplicativa portaApplicativa;

  @XmlElement(name="integration-manager",required=false,nillable=false)
  protected StatoServiziPddIntegrationManager integrationManager;

}
