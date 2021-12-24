/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.id.serial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.Utilities;

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
	private Map<String, Integer> _occurs = new HashMap<String, Integer>(); 
	
	public int getErrorSerializableAccess() {
		return this.errorSerializableAccess;
	}
	public List<Throwable> getExceptionOccurs() {
		return this.exceptionOccurs;
	}
	public int getNumber(Throwable e){
		return this._occurs.get(e.getMessage());
	}
	
	private org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("InfoStatistics");
	public void addErrorSerializableAccess(Throwable e){
		this.semaphore.acquireThrowRuntime("addErrorSerializableAccess");
		try {
			Throwable msgE = Utilities.getInnerNotEmptyMessageException(e);
			String msg = null;
			if(msgE!=null) {
				msg = msgE.getMessage();
			}
			else {
				msg = e.toString();
			}
			if(msg==null) {
				msg = "NullException";
			}
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
		}finally {
			this.semaphore.release("addErrorSerializableAccess");
		}
	}
	
	public void clear(){
		this.errorSerializableAccess = 0;
		this.exceptionOccurs.clear();
		this._exceptionOccursDistincts.clear();
	}
}
