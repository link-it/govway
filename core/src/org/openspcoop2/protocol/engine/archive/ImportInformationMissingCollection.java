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

package org.openspcoop2.protocol.engine.archive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 *  ImportInformationMissingCollection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ImportInformationMissingCollection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,ImportInformationMissing> importInformationMissing = new HashMap<String, ImportInformationMissing>();


	public Map<String, ImportInformationMissing> getImportInformationMissing() {
		return this.importInformationMissing;
	}
	public void setImportInformationMissing(
			Map<String, ImportInformationMissing> importInformationMissing) {
		this.importInformationMissing = importInformationMissing;
	}
	
	public void add(String objectId,ImportInformationMissing importInformationMissing) throws ProtocolException{
		if(this.importInformationMissing.containsKey(objectId)){
			throw new ProtocolException("ImportInformationMissing with id ["+objectId+"] already exists");
		}
		this.importInformationMissing.put(objectId,importInformationMissing);
	}
	public ImportInformationMissing get(String objectId){
		return this.importInformationMissing.get(objectId);
	}
	public ImportInformationMissing remove(String objectId){
		return this.importInformationMissing.remove(objectId);
	}
	public boolean exists(String objectId){
		return this.importInformationMissing.containsKey(objectId);
	}
	public int size(){
		return this.importInformationMissing.size();
	}
	public List<String> keys(){
		List<String> keys = null;
		if(this.importInformationMissing!=null && !this.importInformationMissing.isEmpty()) {
			keys = new ArrayList<>();
			keys.addAll(this.importInformationMissing.keySet());
		}
		return keys;
	}
}
