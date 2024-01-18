/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;

/**
 * CustomKeyGeneratorObject
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CustomKeyGeneratorObject implements IKeyGeneratorObject {

	private KeyGeneratorObjects keyGeneratorObject;
	private String columnNameId;
	private String table;
	private CustomKeyGeneratorOtherTableObjects defaultTableObjects;
	private Map<TipiDatabase,CustomKeyGeneratorOtherTableObjects> customTableObjects = 
			new HashMap<TipiDatabase, CustomKeyGeneratorOtherTableObjects>();
	
	
	public CustomKeyGeneratorObject(String table,String columnNameId,
			String sequenceName,String tableNameForInitSequence){
		this.keyGeneratorObject = KeyGeneratorObjects.CUSTOM;
		this.table = table;
		this.columnNameId = columnNameId;
		this.defaultTableObjects = new CustomKeyGeneratorOtherTableObjects(sequenceName,tableNameForInitSequence);
	}
	public CustomKeyGeneratorObject(String table,String columnNameId){
		this.keyGeneratorObject = KeyGeneratorObjects.CUSTOM;
		this.table = table;
		this.columnNameId = columnNameId;
		this.defaultTableObjects = new CustomKeyGeneratorOtherTableObjects("seq_"+this.table,this.table+"_init_seq");
	}
	public CustomKeyGeneratorObject(String table){
		this.keyGeneratorObject = KeyGeneratorObjects.CUSTOM;
		this.table = table;
		this.columnNameId = "id";
		this.defaultTableObjects = new CustomKeyGeneratorOtherTableObjects("seq_"+this.table,this.table+"_init_seq");
	}
	
	public String getColumnNameId() {
		return this.columnNameId;
	}
	public void setColumnNameId(String columnNameId) {
		this.columnNameId = columnNameId;
	}
	@Override
	public String getTable() {
		return this.table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	@Override
	public KeyGeneratorObjects getType(){
		return this.keyGeneratorObject;
	}
	public CustomKeyGeneratorOtherTableObjects getDefaultTableObjects() {
		return this.defaultTableObjects;
	}
	public void setDefaultTableObjects(
			CustomKeyGeneratorOtherTableObjects defaultTableObjects) {
		this.defaultTableObjects = defaultTableObjects;
	}
	public void addCustomTableObjectsForDatabaseType(TipiDatabase tipoDatabase,CustomKeyGeneratorOtherTableObjects custom){
		this.customTableObjects.put(tipoDatabase, custom);
	}
	public void addCustomTableObjectsForDatabaseType(TipiDatabase tipoDatabase,String sequenceName){
		this.customTableObjects.put(tipoDatabase, new CustomKeyGeneratorOtherTableObjects(sequenceName));
	}
	public void addCustomTableObjectsForDatabaseType(TipiDatabase tipoDatabase,String sequenceName,String tableNameForInitSequence){
		this.customTableObjects.put(tipoDatabase, new CustomKeyGeneratorOtherTableObjects(sequenceName, tableNameForInitSequence));
	}
	public boolean existsCustomTableObjectsForDatabaseType(TipiDatabase tipoDatabase){
		return this.customTableObjects.containsKey(tipoDatabase);
	}
	public CustomKeyGeneratorOtherTableObjects getCustomTableObjectsForDatabaseType(TipiDatabase tipoDatabase){
		return this.customTableObjects.get(tipoDatabase);
	}
	
}
