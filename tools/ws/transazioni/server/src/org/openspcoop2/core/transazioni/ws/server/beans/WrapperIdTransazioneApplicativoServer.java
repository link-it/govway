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
package org.openspcoop2.core.transazioni.ws.server.beans;

/**
 * <p>Java class for WrapperIdTransazioneApplicativoServer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wrapperIdTransazioneApplicativoServer"&gt;
 *     &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.openspcoop2.org/core/transazioni}id-transazione-applicativo-server" maxOccurs="1" /&gt;
 *     &lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer;

/**     
 * WrapperIdTransazioneApplicativoServer
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "wrapperIdTransazioneApplicativoServer", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
    "id"
})
@javax.xml.bind.annotation.XmlRootElement(name = "wrapperIdTransazioneApplicativoServer")
public class WrapperIdTransazioneApplicativoServer extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="id",required=true,nillable=false)
	private IdTransazioneApplicativoServer id;
	
	public void setId(IdTransazioneApplicativoServer id){
		this.id = id;
	}
	
	public IdTransazioneApplicativoServer getId(){
		return this.id;
	}
	
	
	
	
}