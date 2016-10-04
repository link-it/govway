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

package org.openspcoop2.web.ctrlstat.plugins;

import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**     
 * ExtendedConnettoreItem
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExtendedConnettoreItem {
	
	private String id;
	private String label;
	private String value;
	private boolean required;
	private String regularExpression;
	
	private static final String EXTENDED_PREFIX = "ExtCntItem";
	private static final int MAX_LENGTH = 95;
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) throws ExtendedException {
		if(id.length()>MAX_LENGTH){
			throw new ExtendedException("ExtendedInfoConnettore [id:"+id+"] troppo lungo (max-length:"+MAX_LENGTH+")");
		}
		try{
			if(!RegularExpressionEngine.isMatch(id,"^[0-9A-Za-z]+$")){
				throw new ExtendedException("ExtendedInfoConnettore [id:"+id+"] con caratteri non permessi. L'identificativo dev'essere formato solo da caratteri e cifre");
			}
		}catch(Exception e){
			throw new ExtendedException(e.getMessage(),e);
		}
		
		this.id = EXTENDED_PREFIX+id;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isRequired() {
		return this.required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getRegularExpression() {
		return this.regularExpression;
	}
	public void setRegularExpression(String regularExpression) {
		this.regularExpression = regularExpression;
	}


}
