/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**     
 * IdEntity
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "id-entity", namespace="http://www.openspcoop2.org/core/transazioni/management", propOrder = {
	    "id"
	})
public class IdEntity extends org.openspcoop2.utils.beans.BaseBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlElements({
	@XmlElement(name="wrapperIdTransazione", namespace="http://www.openspcoop2.org/core/transazioni/management", type=WrapperIdTransazione.class),
	
	@XmlElement(name="wrapperIdTransazioneApplicativoServer", namespace="http://www.openspcoop2.org/core/transazioni/management", type=WrapperIdTransazioneApplicativoServer.class),
	
	@XmlElement(name="wrapperIdDumpMessaggio", namespace="http://www.openspcoop2.org/core/transazioni/management", type=WrapperIdDumpMessaggio.class)
	})
	
	private Object id;
	
	public Object getId() {
		return this.id;
	}

	public void setId(WrapperIdTransazione id) {
		this.id = id;
	}
	public void setId(WrapperIdTransazioneApplicativoServer id) {	
		this.id = id;
	}
	public void setId(WrapperIdDumpMessaggio id) {	
		this.id = id;
	}
	
//	public void setId(Object id) {
//		this.id = id;
//	} 
	
}
