/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
 * SoapBinding
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapBinding implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean binding_soap11 = false;
	private boolean binding_soap11_withAttachments = false;
	private boolean binding_soap11_mtom = false;
	
	private boolean binding_soap12 = false;
	private boolean binding_soap12_withAttachments = false;
	private boolean binding_soap12_mtom = false;
	
	public SoapBinding(boolean binding_soap11, boolean binding_soap11_withAttachments, boolean binding_soap11_mtom,
			boolean binding_soap12, boolean binding_soap12_withAttachments, boolean binding_soap12_mtom){
		this.binding_soap11 = binding_soap11;
		this.binding_soap11_withAttachments = binding_soap11_withAttachments;
		this.binding_soap11_mtom = binding_soap11_mtom;
		
		this.binding_soap12 = binding_soap12;
		this.binding_soap12_withAttachments = binding_soap12_withAttachments;
		this.binding_soap12_mtom = binding_soap12_mtom;
	}
	
	public boolean isBinding_soap11() {
		return this.binding_soap11;
	}
	public boolean isBinding_soap11_withAttachments() {
		return this.binding_soap11_withAttachments;
	}
	public boolean isBinding_soap11_mtom() {
		return this.binding_soap11_mtom;
	}
	
	public boolean isBinding_soap12() {
		return this.binding_soap12;
	}
	public boolean isBinding_soap12_withAttachments() {
		return this.binding_soap12_withAttachments;
	}
	public boolean isBinding_soap12_mtom() {
		return this.binding_soap12_mtom;
	}
}
