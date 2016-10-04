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

package org.openspcoop2.utils.id.serial;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * InfoStatistics
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InfoStatistics {

	private int errorSerializableAccess = 0;
	private List<Throwable> exceptionOccurs = new ArrayList<Throwable>();
	private List<String> _exceptionOccursDistincts = new ArrayList<String>();
	private Hashtable<String, Integer> _occurs = new Hashtable<String, Integer>(); 
	
	public int getErrorSerializableAccess() {
		return this.errorSerializableAccess;
	}
	public List<Throwable> getExceptionOccurs() {
		return this.exceptionOccurs;
	}
	public int getNumber(Throwable e){
		return this._occurs.get(e.getMessage());
	}
	
	protected synchronized void addErrorSerializableAccess(Throwable e){
		String msg = e.getMessage();
		this.errorSerializableAccess++;
		if(this._exceptionOccursDistincts.contains(msg)==false){
			this._exceptionOccursDistincts.add(msg);
			this.exceptionOccurs.add(e);
			this._occurs.put(msg, 1);
		}
		else{
			int occur = this._occurs.remove(msg);
			occur++;
			this._occurs.put(msg, occur);
		}
	}
	
	public void clear(){
		this.errorSerializableAccess = 0;
		this.exceptionOccurs.clear();
		this._exceptionOccursDistincts.clear();
	}
}
