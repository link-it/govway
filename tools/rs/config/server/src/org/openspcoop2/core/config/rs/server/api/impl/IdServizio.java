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
package org.openspcoop2.core.config.rs.server.api.impl;

import org.openspcoop2.core.id.IDServizio;

/**
 * IdServizio
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class IdServizio extends IDServizio {
	private static final long serialVersionUID = 1L;
	
	protected Long id;
	
	public IdServizio() {
		
	}
	
	@SuppressWarnings("deprecation")
	public IdServizio(IDServizio parent, Long id) {
		this.setAzione(parent.getAzione());
		this.setID(id);
		this.setNome(parent.getNome());
		this.setSoggettoErogatore(parent.getSoggettoErogatore());
		this.setTipo(parent.getTipo());
		this.setTipologia(parent.getTipologia());
		this.setUriAccordoServizioParteComune(parent.getUriAccordoServizioParteComune());
		this.setVersione(parent.getVersione());		
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setID(Long id) {
		this.id = id;
	}

}
