/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.message.config;

import java.io.Serializable;

/**
 * RestBinding
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RestBinding implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean binding_xml = false;
	private boolean binding_json = false;
	private boolean binding_binary = false;	
	private boolean binding_mimeMultipart = false;
	
	public RestBinding(boolean binding_xml, boolean binding_json, 
			boolean binding_binary, boolean binding_mimeMultipart){
		this.binding_xml = binding_xml;
		this.binding_json = binding_json;
		this.binding_binary = binding_binary;	
		this.binding_mimeMultipart = binding_mimeMultipart;
	}
	
	public boolean isBinding_xml() {
		return this.binding_xml;
	}

	public boolean isBinding_json() {
		return this.binding_json;
	}

	public boolean isBinding_binary() {
		return this.binding_binary;
	}

	public boolean isBinding_mimeMultipart() {
		return this.binding_mimeMultipart;
	}
}
