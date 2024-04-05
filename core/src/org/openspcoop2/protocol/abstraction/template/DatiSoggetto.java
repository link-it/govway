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

package org.openspcoop2.protocol.abstraction.template;

import java.io.Serializable;

/**     
 * DatiSoggetto
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiSoggetto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IdSoggetto id;
	private String endpoint;
	private String portaDominio;
	
	public IdSoggetto getId() {
		return this.id;
	}
	public void setId(IdSoggetto id) {
		this.id = id;
	}
	public String getEndpoint() {
		return this.endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getPortaDominio() {
		return this.portaDominio;
	}
	public void setPortaDominio(String portaDominio) {
		this.portaDominio = portaDominio;
	}
}
