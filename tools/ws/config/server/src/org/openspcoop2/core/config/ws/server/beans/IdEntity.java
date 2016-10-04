/*
 * OpenSPCoop - Customizable API Gateway 
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
package org.openspcoop2.core.config.ws.server.beans;

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
@javax.xml.bind.annotation.XmlType(name = "id-entity", namespace="http://www.openspcoop2.org/core/config/management", propOrder = {
	    "id"
	})
public class IdEntity extends org.openspcoop2.utils.beans.BaseBean {
	@XmlElements({
	@XmlElement(name="wrapperIdSoggetto", namespace="http://www.openspcoop2.org/core/config/management", type=WrapperIdSoggetto.class),
	
	@XmlElement(name="wrapperIdPortaDelegata", namespace="http://www.openspcoop2.org/core/config/management", type=WrapperIdPortaDelegata.class),
	
	@XmlElement(name="wrapperIdPortaApplicativa", namespace="http://www.openspcoop2.org/core/config/management", type=WrapperIdPortaApplicativa.class),
	
	@XmlElement(name="wrapperIdServizioApplicativo", namespace="http://www.openspcoop2.org/core/config/management", type=WrapperIdServizioApplicativo.class)
	})
	
	private Object id;
	
	public Object getId() {
		return this.id;
	}

	public void setId(WrapperIdSoggetto id) {	
		this.id = id;
	}
	public void setId(WrapperIdPortaDelegata id) {	
		this.id = id;
	}
	public void setId(WrapperIdPortaApplicativa id) {	
		this.id = id;
	}
	public void setId(WrapperIdServizioApplicativo id) {	
		this.id = id;
	}
	
//	public void setId(Object id) {
//		this.id = id;
//	} 
	
}
