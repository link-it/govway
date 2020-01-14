/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.semaphore;

import java.util.ArrayList;
import java.util.List;

/**
 * SemaphoreMapping
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemaphoreMapping {

	public static SemaphoreMapping newInstance(String applicativeId) {
		SemaphoreMapping mapping = new SemaphoreMapping();
		mapping.setTable("OP2_SEMAPHORE");
		mapping.setIdNode("node_id");
		mapping.setLockDate("creation_time");
		mapping.setUpdateDate("update_time");
		mapping.setDetails("details");
		mapping.addUniqueConditionValue("applicative_id", applicativeId, String.class);
		return mapping;
	}
	
	private String table;
	private String idNode;
	private String lockDate;
	private String updateDate;
	private String details;
	private List<String> uniqueConditions = new ArrayList<String>();
	private List<Object> uniqueConditionsValues = new ArrayList<Object>();
	private List<Class<?>> uniqueConditionsTypes = new ArrayList<Class<?>>();
	
	public String getTable() {
		return this.table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getIdNode() {
		return this.idNode;
	}
	public void setIdNode(String idNode) {
		this.idNode = idNode;
	}
	public String getLockDate() {
		return this.lockDate;
	}
	public void setLockDate(String lockDate) {
		this.lockDate = lockDate;
	}
	public String getUpdateDate() {
		return this.updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getDetails() {
		return this.details;
	}
	public void setDetails(String details) {
		this.details = details;
	}

	public void addUniqueConditionValue(String columnName, Object value,Class<?> classType) {
		this.uniqueConditions.add(columnName);
		this.uniqueConditionsValues.add(value);
		this.uniqueConditionsTypes.add(classType);
	}
	public int sizeUniqueConditionValues() {
		return this.uniqueConditionsValues.size();
	}
	public String getUniqueConditionColumnName(int index) {
		return this.uniqueConditions.get(index);
	}
	public Object getUniqueConditionValue(int index) {
		return this.uniqueConditionsValues.get(index);
	}
	public Class<?> getUniqueConditionType(int index) {
		return this.uniqueConditionsTypes.get(index);
	}
	public void removeUniqueConditionValue(int index) {
		this.uniqueConditions.remove(index);
		this.uniqueConditionsValues.remove(index);
		this.uniqueConditionsTypes.remove(index);
	}
	public void clearUniqueConditionValues() {
		this.uniqueConditions.clear();
		this.uniqueConditionsValues.clear();
		this.uniqueConditionsTypes.clear();
	}
}
