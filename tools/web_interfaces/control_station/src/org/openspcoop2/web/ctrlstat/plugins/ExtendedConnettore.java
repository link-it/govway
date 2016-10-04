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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**     
 * ExtendedConnettore
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExtendedConnettore {

	private String id;
	private String idForOldValue;
	private String section;
	private String label;
	private boolean enabled;
	private List<ExtendedConnettoreItem> listItem = new ArrayList<ExtendedConnettoreItem>();
	
	private static final String EXTENDED_PREFIX_OLD = "ExtCntOld";
	private static final String EXTENDED_PREFIX = "ExtCnt";
	private static final int MAX_LENGTH = 90;
	
	public String getId() {
		return this.id;
	}
	public String getIdForOldValue() {
		return this.idForOldValue;
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
		this.idForOldValue = EXTENDED_PREFIX_OLD+id;
	}
	public String getSection() {
		return this.section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public boolean isEnabled() {
		return this.enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public List<ExtendedConnettoreItem> getListItem() {
		return this.listItem;
	}
	public void setListItem(List<ExtendedConnettoreItem> listItem) {
		this.listItem = listItem;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	
}
