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
package org.openspcoop2.core.registry.ws.server.beans;

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
@javax.xml.bind.annotation.XmlType(name = "id-entity", namespace="http://www.openspcoop2.org/core/registry/management", propOrder = {
	    "id"
	})
public class IdEntity extends org.openspcoop2.utils.beans.BaseBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlElements({
	@XmlElement(name="wrapperIdAccordoCooperazione", namespace="http://www.openspcoop2.org/core/registry/management", type=WrapperIdAccordoCooperazione.class),
	
	@XmlElement(name="wrapperIdAccordoServizioParteComune", namespace="http://www.openspcoop2.org/core/registry/management", type=WrapperIdAccordoServizioParteComune.class),
	
	@XmlElement(name="wrapperIdPortaDominio", namespace="http://www.openspcoop2.org/core/registry/management", type=WrapperIdPortaDominio.class),
	
	@XmlElement(name="wrapperIdRuolo", namespace="http://www.openspcoop2.org/core/registry/management", type=WrapperIdRuolo.class),
	
	@XmlElement(name="wrapperIdScope", namespace="http://www.openspcoop2.org/core/registry/management", type=WrapperIdScope.class),
	
	@XmlElement(name="wrapperIdGruppo", namespace="http://www.openspcoop2.org/core/registry/management", type=WrapperIdGruppo.class),
	
	@XmlElement(name="wrapperIdSoggetto", namespace="http://www.openspcoop2.org/core/registry/management", type=WrapperIdSoggetto.class),
	
	@XmlElement(name="wrapperIdAccordoServizioParteSpecifica", namespace="http://www.openspcoop2.org/core/registry/management", type=WrapperIdAccordoServizioParteSpecifica.class)
	})
	
	private Object id;
	
	public Object getId() {
		return this.id;
	}

	public void setId(WrapperIdAccordoCooperazione id) {	
		this.id = id;
	}
	public void setId(WrapperIdAccordoServizioParteComune id) {	
		this.id = id;
	}
	public void setId(WrapperIdPortaDominio id) {	
		this.id = id;
	}
	public void setId(WrapperIdRuolo id) {	
		this.id = id;
	}
	public void setId(WrapperIdScope id) {	
		this.id = id;
	}
	public void setId(WrapperIdGruppo id) {	
		this.id = id;
	}
	public void setId(WrapperIdSoggetto id) {	
		this.id = id;
	}
	public void setId(WrapperIdAccordoServizioParteSpecifica id) {	
		this.id = id;
	}
	
//	public void setId(Object id) {
//		this.id = id;
//	} 
	
}
