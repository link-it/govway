/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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

package org.openspcoop2.generic_project.dao.jdbc.utils;

import java.util.List;
import java.util.Vector;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.EscapeSQLPattern;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectCore;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * JDBC_SQLQueryObject
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBC_SQLQueryObject implements ISQLQueryObject {

	private SQLQueryObjectCore sqlQueryObject;
	public JDBC_SQLQueryObject(ISQLQueryObject sqlQueryObject){
		this.sqlQueryObject = (SQLQueryObjectCore) sqlQueryObject;
	}
	

	
	/* ----------------  AGGIUNTA LOGICA DAO NEI SEGUENTI METODI ------------------ */
	
	@Override
	public String createSQLQuery() throws SQLQueryObjectException {
		
		if(this.sqlQueryObject.isSelectForUpdate()){
			if(this.sqlQueryObject.getGroupByConditions().size()>0){
				throw new SQLQueryObjectException("Cannot use selectForUpdate in group by methods");
			}
			else if(this.sqlQueryObject.isSelectDistinct()){
				throw new SQLQueryObjectException("Cannot use selectForUpdate in methods that use distinct clause");
			}
			else if(this.sqlQueryObject.getLimit()>=0){
				throw new SQLQueryObjectException("Cannot use selectForUpdate in methods that use PaginatedExpression with limit");
			}
			else if(this.sqlQueryObject.getOffset()>=0){
				throw new SQLQueryObjectException("Cannot use selectForUpdate in methods that use PaginatedExpression with offset");
			}
		}
		
		return this.sqlQueryObject.createSQLQuery();
	}
		
	@Override
	public String createSQLUnion(boolean unionAll,ISQLQueryObject... sqlQueryObject) throws SQLQueryObjectException{
		if(this.sqlQueryObject.isSelectForUpdate()){
			throw new SQLQueryObjectException("Cannot use selectForUpdate in union methods");
		}
		return this.sqlQueryObject.createSQLUnion(unionAll, sqlQueryObject);
	}
		
	@Override
	public String createSQLUnionCount(boolean unionAll,String aliasCount,ISQLQueryObject... sqlQueryObject) throws SQLQueryObjectException{
		if(this.sqlQueryObject.isSelectForUpdate()){
			throw new SQLQueryObjectException("Cannot use selectForUpdate in union count methods");
		}
		return this.sqlQueryObject.createSQLUnionCount(unionAll, aliasCount, sqlQueryObject);
	}
		
	@Override
	public String createSQLUpdate() throws SQLQueryObjectException{
		// Per permettere di usare i metodi di update, insert e delete dentro una transazione con select for update abilitato
		boolean oldValueSelectForUpdate = this.sqlQueryObject.isSelectForUpdate();
		try{
			this.sqlQueryObject.setSelectForUpdate(false);
			return this.sqlQueryObject.createSQLUpdate();
		}finally{
			this.sqlQueryObject.setSelectForUpdate(oldValueSelectForUpdate);
		}
	}
		
	@Override
	public String createSQLInsert() throws SQLQueryObjectException{
		// Per permettere di usare i metodi di update, insert e delete dentro una transazione con select for update abilitato
		boolean oldValueSelectForUpdate = this.sqlQueryObject.isSelectForUpdate();
		try{
			this.sqlQueryObject.setSelectForUpdate(false);
			return this.sqlQueryObject.createSQLInsert();
		}finally{
			this.sqlQueryObject.setSelectForUpdate(oldValueSelectForUpdate);
		}
	}
		
	@Override
	public String createSQLDelete() throws SQLQueryObjectException{
		// Per permettere di usare i metodi di update, insert e delete dentro una transazione con select for update abilitato
		boolean oldValueSelectForUpdate = this.sqlQueryObject.isSelectForUpdate();
		try{
			this.sqlQueryObject.setSelectForUpdate(false);
			return this.sqlQueryObject.createSQLDelete();
		}finally{
			this.sqlQueryObject.setSelectForUpdate(oldValueSelectForUpdate);
		}
	}
		
	@Override
	public String createSQLConditions() throws SQLQueryObjectException{
		// Per permettere di usare i metodi di getConditions internamente
		boolean oldValueSelectForUpdate = this.sqlQueryObject.isSelectForUpdate();
		try{
			this.sqlQueryObject.setSelectForUpdate(false);
			return this.sqlQueryObject.createSQLConditions();
		}finally{
			this.sqlQueryObject.setSelectForUpdate(oldValueSelectForUpdate);
		}
	}	
			
	@Override
	public ISQLQueryObject newSQLQueryObject() throws SQLQueryObjectException{
		ISQLQueryObject sqlQueryObjectNew = this.sqlQueryObject.newSQLQueryObject();
		sqlQueryObjectNew.setSelectForUpdate(this.sqlQueryObject.isSelectForUpdate());
		return new JDBC_SQLQueryObject(sqlQueryObjectNew);
	}	
	
	
	
	
	
	
	
	
	
	// ********** SEMPLICI METODI WRAPPED ***************** */
	
	
	// SELECT FIELDS NORMALI
	
	@Override
	public ISQLQueryObject addSelectField(String nomeField) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectField(nomeField);
	}
		
	@Override
	public ISQLQueryObject addSelectField(String nomeTabella,String nomeField) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectField(nomeTabella,nomeField);
	}
		
	@Override
	public ISQLQueryObject addSelectAliasField(String nomeField,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectAliasField(nomeField, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectAliasField(String nomeTabella,String nomeField,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectAliasField(nomeTabella,nomeField, alias);
	}
		
		
	// SELECT FIELDS COALESCE
		
	@Override
	public ISQLQueryObject addSelectCoalesceField(String nomeField,String alias,String valore) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectCoalesceField(nomeField, alias, valore);
	}
	
	@Override
	public ISQLQueryObject addSelectCoalesceField(String aliasTabella,String nomeField,String alias,String valore) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectCoalesceField(aliasTabella, nomeField, alias, valore);
	}
		
		
		
	// SELECT FIELDS COUNTS
		
	@Override
	public ISQLQueryObject addSelectCountField(String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectCountField(alias);
	}
		
	@Override
	public ISQLQueryObject addSelectCountField(String fieldCount,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectCountField(fieldCount, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectCountField(String fieldCount,String alias,boolean distinct) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectCountField(fieldCount, alias, distinct);
	}
		
	@Override
	public ISQLQueryObject addSelectCountField(String aliasTabella,String fieldCount,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectCountField(aliasTabella, fieldCount, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectCountField(String aliasTabella,String fieldCount,String alias,boolean distinct) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectCountField(aliasTabella, fieldCount, alias, distinct);
	}
		
		
		
	// SELECT FIELDS AVG
		
	@Override
	public ISQLQueryObject addSelectAvgField(String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectAvgField(field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectAvgField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectAvgField(aliasTabella, field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectAvgTimestampField(String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectAvgTimestampField(field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectAvgTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectAvgTimestampField(aliasTabella, field, alias);
	}
		
		
		
		
	// SELECT FIELDS MAX
		
	@Override
	public ISQLQueryObject addSelectMaxField(String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectMaxField(field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectMaxField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectMaxField(aliasTabella, field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectMaxTimestampField(String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectMaxTimestampField(field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectMaxTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectMaxTimestampField(aliasTabella, field, alias);
	}
		
		
		
		
	// SELECT FIELDS MIN
		
	@Override
	public ISQLQueryObject addSelectMinField(String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectMinField(field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectMinField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectMinField(aliasTabella, field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectMinTimestampField(String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectMinTimestampField(field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectMinTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectMinTimestampField(aliasTabella, field, alias);
	}
		
		
		
		
	// SELECT FIELDS SUM
		
	@Override
	public ISQLQueryObject addSelectSumField(String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectSumField(field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectSumField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectSumField(aliasTabella, field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectSumTimestampField(String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectSumTimestampField(field, alias);
	}
		
	@Override
	public ISQLQueryObject addSelectSumTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectSumTimestampField(aliasTabella, field, alias);
	}
		
		
		
	// SELECT FORCE INDEX
		
	@Override
	public ISQLQueryObject addSelectForceIndex(String nomeTabella,String indexName) throws SQLQueryObjectException{
		return this.sqlQueryObject.addSelectForceIndex(nomeTabella, indexName);
	}
		
		
		
	// SET DISTINCTS IN CIMA ALLA SELECT
		
	@Override
	public void setSelectDistinct(boolean value) throws SQLQueryObjectException{
		this.sqlQueryObject.setSelectDistinct(value);
	}
	
				
		
	// FIELDS/TABLE NAME
		
	@Override
	public Vector<String> getFieldsName() throws SQLQueryObjectException{
		return this.sqlQueryObject.getFieldsName();
	}
		
	@Override
	public Boolean isFieldNameForFunction(String fieldName) throws SQLQueryObjectException{
		return this.sqlQueryObject.isFieldNameForFunction(fieldName);
	}
		
	@Override
	public Vector<String> getTablesName() throws SQLQueryObjectException{
		return this.sqlQueryObject.getTablesName();
	}
		
			
		
	// FIELDS UNIX FUNCTIONS
		
	@Override
	public String getUnixTimestampConversion(String column){
		return this.sqlQueryObject.getUnixTimestampConversion(column);
	}
		
	@Override
	public String getDiffUnixTimestamp(String columnMax,String columnMin){
		return this.sqlQueryObject.getDiffUnixTimestamp(columnMax,columnMin);
	}
		
		
		
		
	// FIELDS/TABLE ALIAS

	// GESTIONE ALIAS 'as'
	
	@Override
	public String getDefaultAliasFieldKeyword(){
		return this.sqlQueryObject.getDefaultAliasFieldKeyword();
	}

	@Override
	public String getDefaultAliasTableKeyword(){
		return this.sqlQueryObject.getDefaultAliasTableKeyword();
	}

	@Override
	public List<String> getSupportedAliasesField(){
		return this.sqlQueryObject.getSupportedAliasesField();
	}
		
	@Override
	public List<String> getSupportedAliasesTable(){
		return this.sqlQueryObject.getSupportedAliasesTable();
	}
		
		

	// FROM
		
	@Override
	public ISQLQueryObject addFromTable(String tabella) throws SQLQueryObjectException{
		return this.sqlQueryObject.addFromTable(tabella);
	}
		
	@Override
	public ISQLQueryObject addFromTable(String tabella,String alias) throws SQLQueryObjectException{
		return this.sqlQueryObject.addFromTable(tabella, alias);
	}
		
	@Override
	public ISQLQueryObject addFromTable(ISQLQueryObject subSelect) throws SQLQueryObjectException{
		return this.sqlQueryObject.addFromTable(subSelect);
	}
		
		
			
	// WHERE
		
	@Override
	public ISQLQueryObject addWhereCondition(String condition) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereCondition(condition);
	}
		
	@Override
	public ISQLQueryObject addWhereCondition(boolean andLogicOperator,String... conditions) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereCondition(andLogicOperator, conditions);
	}
		
	@Override
	public ISQLQueryObject addWhereCondition(boolean andLogicOperator,boolean not,String... conditions) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereCondition(andLogicOperator, not, conditions);
	}
		
	@Override
	public ISQLQueryObject addWhereIsNullCondition(String field) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereIsNullCondition(field);
	}
		
	@Override
	public ISQLQueryObject addWhereIsNotNullCondition(String field) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereIsNotNullCondition(field);
	}
		
	@Override
	public ISQLQueryObject addWhereINCondition(String field,boolean stringValueType,String ... valore) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereINCondition(field, stringValueType, valore);
	}
		
	@Override
	public ISQLQueryObject addWhereBetweenCondition(String field,boolean stringValueType,String leftValue,String rightValue) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereBetweenCondition(field, stringValueType, leftValue, rightValue);
	}
		
	@Override
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereLikeCondition(columnName, searchPattern);
	}
		
	@Override
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern, boolean escape) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereLikeCondition(columnName, searchPattern, escape);
	}
			
	@Override
	public String getWhereLikeCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return this.sqlQueryObject.getWhereLikeCondition(columnName, searchPattern);
	}
		
	@Override
	public String getWhereLikeCondition(String columnName,String searchPattern,boolean escape) throws SQLQueryObjectException{
		return this.sqlQueryObject.getWhereLikeCondition(columnName, searchPattern, escape);
	}
		
	@Override
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereLikeCondition(columnName, searchPattern, contains, caseInsensitive);
	}
	
	@Override
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern, boolean escape, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereLikeCondition(columnName, searchPattern, escape, contains, caseInsensitive);
	}
		
	@Override
	public String getWhereLikeCondition(String columnName,String searchPattern, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException{
		return this.sqlQueryObject.getWhereLikeCondition(columnName, searchPattern, contains, caseInsensitive);
	}
	
	@Override
	public String getWhereLikeCondition(String columnName,String searchPattern, boolean escape, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException{
		return this.sqlQueryObject.getWhereLikeCondition(columnName, searchPattern, escape, contains, caseInsensitive);
	}
		
	@Override
	public ISQLQueryObject addWhereExistsCondition(boolean notExists,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereExistsCondition(notExists, sqlQueryObject);
	}
		
	@Override
	public String getWhereExistsCondition(boolean notExists,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException{
		return this.sqlQueryObject.getWhereExistsCondition(notExists, sqlQueryObject);
	}
		
	@Override
	public ISQLQueryObject addWhereSelectSQLCondition(boolean notExists,String field,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereSelectSQLCondition(notExists, field, sqlQueryObject);
	}
		
	@Override
	public ISQLQueryObject addWhereINSelectSQLCondition(boolean notExists,String field,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException{
		return this.sqlQueryObject.addWhereINSelectSQLCondition(notExists, field, sqlQueryObject);
	}
		
	@Override
	public void setANDLogicOperator(boolean andLogicOperator) throws SQLQueryObjectException{
		this.sqlQueryObject.setANDLogicOperator(andLogicOperator);
	}
		
	@Override
	public void setNOTBeforeConditions(boolean not) throws SQLQueryObjectException{
		this.sqlQueryObject.setNOTBeforeConditions(not);
	}
		
		
	// ESCAPE
	
	@Override
	public String escapeStringValue(String value) throws SQLQueryObjectException{
		return this.sqlQueryObject.escapeStringValue(value);
	}
		
	@Override
	public EscapeSQLPattern escapePatternValue(String pattern) throws SQLQueryObjectException{
		return this.sqlQueryObject.escapePatternValue(pattern);
	}
		
		
		
	// GROUP BY
		
	@Override
	public ISQLQueryObject addGroupBy(String groupByNomeField) throws SQLQueryObjectException{
		return this.sqlQueryObject.addGroupBy(groupByNomeField);
	}
		
		
	// ORDER BY
		
	@Override
	public ISQLQueryObject addOrderBy(String orderByNomeField) throws SQLQueryObjectException{
		return this.sqlQueryObject.addOrderBy(orderByNomeField);
	}
		
	@Override
	public ISQLQueryObject addOrderBy(String orderByNomeField, boolean asc) throws SQLQueryObjectException{
		return this.sqlQueryObject.addOrderBy(orderByNomeField, asc);
	}
		
	@Override
	public void setSortType(boolean asc) throws SQLQueryObjectException{
		this.sqlQueryObject.setSortType(asc);
	}
		
		
		
	// LIMIT E OFFSET
		
	@Override
	public void setLimit(int limit) throws SQLQueryObjectException{
		this.sqlQueryObject.setLimit(limit);
	}
		
	@Override
	public void setOffset(int offset) throws SQLQueryObjectException{
		this.sqlQueryObject.setOffset(offset);
	}
		
		
		
		
	// SELECT FOR UPDATE
		
	@Override
	public void setSelectForUpdate(boolean selectForUpdate) throws SQLQueryObjectException{
		this.sqlQueryObject.setSelectForUpdate(selectForUpdate);
	}
		
		
		
	
	/* ---------------- UPDATE ------------------ */
	
	@Override
	public ISQLQueryObject addUpdateTable(String nomeTabella) throws SQLQueryObjectException{
		return this.sqlQueryObject.addUpdateTable(nomeTabella);
	}
		
	@Override
	public ISQLQueryObject addUpdateField(String nomeField,String valueField) throws SQLQueryObjectException{
		return this.sqlQueryObject.addUpdateField(nomeField, valueField);
	}
	
	
	
	
	/* ---------------- INSERT ------------------ */
	
	@Override
	public ISQLQueryObject addInsertTable(String nomeTabella) throws SQLQueryObjectException{
		return this.sqlQueryObject.addInsertTable(nomeTabella);
	}
	
	@Override
	public ISQLQueryObject addInsertField(String nomeField,String valueField) throws SQLQueryObjectException{
		return this.sqlQueryObject.addInsertField(nomeField, valueField);
	}
	
	
	
	
	/* ---------------- DELETE ------------------ */
	
	@Override
	public ISQLQueryObject addDeleteTable(String tabella) throws SQLQueryObjectException{
		return this.sqlQueryObject.addDeleteTable(tabella);
	}
	
	
	
	/* ---------------- NEW SQL QUERY OBJECT ------------------ */
	
	@Override
	public String getTipoDatabase() throws SQLQueryObjectException{
		return this.sqlQueryObject.getTipoDatabase();
	}	
	
	@Override
	public TipiDatabase getTipoDatabaseOpenSPCoop2() throws SQLQueryObjectException{
		return this.sqlQueryObject.getTipoDatabaseOpenSPCoop2();
	}	
	
	

}
