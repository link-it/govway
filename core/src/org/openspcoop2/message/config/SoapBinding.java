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

package org.openspcoop2.message.config;

import java.io.Serializable;

/**
 * SoapBinding
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
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
