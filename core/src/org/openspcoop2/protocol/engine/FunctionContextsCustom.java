/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.engine;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.engine.constants.IDService;

/**
 * URL Protocol Context
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class FunctionContextsCustom implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<FunctionContextCustom> contexts = new ArrayList<FunctionContextCustom>(); 	
	public List<FunctionContextCustom> getContexts() {
		return this.contexts;
	}
	
	public boolean isMatch(String contextParam,String subContextParam) {
		for (int i = 0; i < this.contexts.size(); i++) {
			boolean match = this.contexts.get(i).isMatch(contextParam, subContextParam);
			if(match) {
				return true;
			}
		}
		return false;
	}
	
	public IDService getServiceMatch(String contextParam,String subContextParam) {
		for (int i = 0; i < this.contexts.size(); i++) {
			IDService idService = this.contexts.get(i).getServiceMatch(contextParam, subContextParam);
			if(idService!=null) {
				return idService;
			}
		}
		return null;
	}
	
	public String getFunctionMatch(String contextParam,String subContextParam) {
		for (int i = 0; i < this.contexts.size(); i++) {
			String function = this.contexts.get(i).getFunctionMatch(contextParam, subContextParam);
			if(function!=null) {
				return function;
			}
		}
		return null;
	}
	
}
