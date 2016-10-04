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

package org.openspcoop2.generic_project.expression.impl.sql;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.generic_project.beans.AliasField;
import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.ConstantField;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.Function;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.beans.UnixTimestampIntervalField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.generic_project.expression.impl.BetweenExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.Comparator;
import org.openspcoop2.generic_project.expression.impl.ComparatorExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.ConjunctionExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.ExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.InExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.LikeExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.OrderedField;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectAlreadyExistsException;
import org.openspcoop2.utils.sql.SQLQueryObjectCore;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * ExpressionSQL
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExpressionSQL extends ExpressionImpl {

	private boolean throwExpressionNotInitialized = false;
	
	private TipiDatabase databaseType;
	public TipiDatabase getDatabaseType() {
		return this.databaseType;
	}

	private ISQLFieldConverter sqlFieldConverter;
	public ISQLFieldConverter getSqlFieldConverter() {
		return this.sqlFieldConverter;
	}
	public void setSqlFieldConverter(ISQLFieldConverter sqlFieldConverter) {
		this.sqlFieldConverter = sqlFieldConverter;
	}
	
	private boolean usedForCountExpression = false;
	public boolean isUsedForCountExpression() {
		return this.usedForCountExpression;
	}
	public void setUsedForCountExpression(boolean usedForCountExpression) {
		this.usedForCountExpression = usedForCountExpression;
	}
	
	public ExpressionSQL(ISQLFieldConverter sqlFieldConverter) throws ExpressionException {
		super();
		this.sqlFieldConverter = sqlFieldConverter;
		if(this.sqlFieldConverter!=null){
			this.databaseType = this.sqlFieldConverter.getDatabaseType();
		}
	}
	public ExpressionSQL(ISQLFieldConverter sqlFieldConverter,IObjectFormatter objectFormatter) throws ExpressionException{
		super(objectFormatter);
		this.sqlFieldConverter = sqlFieldConverter;
		if(this.sqlFieldConverter!=null){
			this.databaseType = this.sqlFieldConverter.getDatabaseType();
		}
	}
	public ExpressionSQL(PaginatedExpressionSQL expression) throws ExpressionException{
		super(expression);
		this.sqlFieldConverter = expression.getSqlFieldConverter();
		this.fieldsManuallyAdd = expression.getFieldsManuallyAdd();
		//this.checkFieldManuallyAdd = expression.checkFieldManuallyAdd;
		if(this.sqlFieldConverter!=null){
			this.databaseType = this.sqlFieldConverter.getDatabaseType();
		}
	}

	private List<Object> fieldsManuallyAdd = new ArrayList<Object>();
	public List<Object> getFieldsManuallyAdd() {
		return this.fieldsManuallyAdd;
	}
	public void removeFieldManuallyAdd(Object o) {
		if(this.fieldsManuallyAdd.contains(o)){
			this.fieldsManuallyAdd.remove(o);
		}
	}
	
	private boolean checkFieldManuallyAdd = true;
	public void setCheckFieldManuallyAdd(boolean checkFieldManuallyAdd) {
		this.checkFieldManuallyAdd = checkFieldManuallyAdd;
	}
	@Override
	public boolean inUseField(IField field,boolean checkOnlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException {
		return ExpressionSQL.inUse(field, checkOnlyWhereCondition, super.inUseField(field,checkOnlyWhereCondition), this.getFieldsManuallyAdd(),this.checkFieldManuallyAdd);
	}
	@Override
	public boolean inUseModel(IModel<?> model,boolean checkOnlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException {
		return ExpressionSQL.inUse(model, checkOnlyWhereCondition, super.inUseModel(model,checkOnlyWhereCondition), this.getFieldsManuallyAdd(),this.checkFieldManuallyAdd);
	}
	@Override
	public List<IField> getFields(boolean onlyWhereCondition) throws ExpressionNotImplementedException, ExpressionException{
		return ExpressionSQL.getFields(onlyWhereCondition, super.getFields(onlyWhereCondition), this.getFieldsManuallyAdd(),this.checkFieldManuallyAdd);
	}
	
	
	
	/* ************ TO SQL ENGINE *********** */
	
	
	
	// SQL ORDER

	private String toSqlOrder()throws ExpressionException{
		
		return ExpressionSQL.sqlOrder(this.sqlFieldConverter, this.getSortOrder(), this.getOrderedFields());

	}
	
	protected static String sqlOrder(ISQLFieldConverter fieldConverter,SortOrder sortOrder,List<OrderedField> orderedFields) throws ExpressionException{
		try{
			
			StringBuffer bf = new StringBuffer();
			if(!SortOrder.UNSORTED.equals(sortOrder)){
					
				bf.append(" ORDER BY ");
				if(orderedFields.size()>0){
					int index = 0;
					for (Iterator<OrderedField> iterator =orderedFields.iterator(); iterator.hasNext();) {
						OrderedField orderedField = iterator.next();
						IField field = orderedField.getField();
						if(index>0){
							bf.append(" , ");
						}
						bf.append(fieldConverter.toColumn(field,true));
						bf.append(" ");
						bf.append(orderedField.getSortOrder().name());
						index++;
					}
				}else{
					bf.append(" id");
					bf.append(" ");
					bf.append(sortOrder.name());
				}
			}
				
			return bf.toString();
			
		}catch(Exception e){
			throw new ExpressionException(e);
		}
	}
	
	private void toSqlOrder(ISQLQueryObject sqlQueryObject)throws ExpressionException{
	
		ExpressionSQL.sqlOrder(this.sqlFieldConverter, sqlQueryObject,this.getSortOrder(), this.getOrderedFields(), this.getGroupByFields());
		
	}
	
	protected static void sqlOrder(ISQLFieldConverter fieldConverter, ISQLQueryObject sqlQueryObject,SortOrder sortOrder,List<OrderedField> orderedFields,
			List<IField> groupByFields) throws ExpressionException{
		try{
			
			if(!SortOrder.UNSORTED.equals(sortOrder)){
				
				if(orderedFields.size()>0){
					for (Iterator<OrderedField> iterator = orderedFields.iterator(); iterator.hasNext();) {
						OrderedField orderedField = iterator.next();
						IField field = orderedField.getField();
						
						String columnOrderAlias = fieldConverter.toColumn(field,true,true);
						String columnOrderBy = fieldConverter.toColumn(field,true);
						sqlQueryObject.addOrderBy(columnOrderBy,SortOrder.ASC.equals(orderedField.getSortOrder()));
						
						// Search by alias
						Vector<String> v = new Vector<String>();
						try{
							v = sqlQueryObject.getFieldsName();
						}catch(Exception e){}
						boolean contains = false;
						//System.out.println("SEARCH ALIAS ["+columnOrderAlias+"] ...");
						if(v.contains(columnOrderAlias)){
							contains = true;
							//System.out.println("SEARCH ALIAS ["+columnOrderAlias+"] FOUND");
						}
						
						
						// Search by column name
						if(!contains){
							try{
								v = ((SQLQueryObjectCore)sqlQueryObject).getFields();
							}catch(Exception e){}
							//System.out.println("SEARCH COLUMN ["+columnOrderBy+"] ...");
							if(v.contains(columnOrderBy)){
								contains = true;
								//System.out.println("SEARCH COLUMN ["+columnOrderBy+"] FOUND");
							}
						}
						
						// Search by column name (split alias)
						if(!contains){
							//System.out.println("SEARCH COLUMN SPLIT ["+columnOrderBy+"] ...");
							for (int i = 0; i < v.size(); i++) {
								String column = v.get(0);
								//System.out.println("SEARCH COLUMN SPLITi ["+columnOrderBy+"] ["+i+"]: "+column);
								if(column.contains(" as ")){
									String [] tmp = column.split(" as ");
									//System.out.println("SEARCH COLUMN SPLIT A ["+columnOrderBy+"] ["+i+"]: "+column+"   AS ["+tmp[0]+"]");
									if(tmp[0].equals(columnOrderBy)){
										contains = true;
										//System.out.println("FOUND COLUMN SPLIT A!!!");
										break;
									}
								}
								else if(column.contains(" ")){
									String [] tmp = column.split(" ");
									//System.out.println("SEARCH COLUMN SPLIT ["+columnOrderBy+"] ["+i+"]: "+column+"   AS2 ["+tmp[0]+"]");
									if(tmp[0].equals(columnOrderBy)){
										contains = true;
										//System.out.println("FOUND COLUMN SPLIT B!!!");
										break;
									}
								}
							}
						}
						
						//System.out.println("FIX ORACLE ["+columnOrderBy+"]: "+!contains);
						// Fix per oracle
						if(!contains){
							// Devo aggiungerlo solo se la colonna non fa gia' parte del group by condition, altrimenti tale colonna finira' comunque tra i select field.
							boolean add = true;
							if(groupByFields!=null){
								for (IField groupByField : groupByFields) {
									if(groupByField.equals(field)){
										add = false;
										break;
									}
								}
							}
							if(add){
								//System.out.println("ADD SELECT FIELD ["+field.getClass().getName()+"] ["+columnOrderBy+"]");
								if(field instanceof UnixTimestampIntervalField){
									UnixTimestampIntervalField unix = (UnixTimestampIntervalField) field;
									String alias = null;
									if(unix.existsAlias()==false){
										//System.out.println("NOT EXISTS");
										unix.buildAlias();
									}
									alias = unix.getAlias();
									//System.out.println("ALIAS ["+alias+"]");
									sqlQueryObject.addSelectAliasField(columnOrderBy, alias);
								}
								else{
									sqlQueryObject.addSelectField(columnOrderBy);
								}
							}
						}
					}
				}else{
					sqlQueryObject.addOrderBy("id");
				}
				if(SortOrder.ASC.equals(sortOrder)){
					sqlQueryObject.setSortType(true);
				}else{
					sqlQueryObject.setSortType(false);
				}
			}
			
		}catch(Exception e){
			throw new ExpressionException(e);
		}
	}
	
	
	
	
	// SQL GROUP BY
	
	private String toSqlGroupBy()throws ExpressionException{
		
		return ExpressionSQL.sqlGroupBy(this.sqlFieldConverter, this.groupByFields);

	}
	
	protected static String sqlGroupBy(ISQLFieldConverter fieldConverter,List<IField> groupByFields) throws ExpressionException{
		try{
			
			StringBuffer bf = new StringBuffer();
					
			if(groupByFields.size()>0){
				bf.append(" GROUP BY ");
				int index = 0;
				for (Iterator<IField> iterator =groupByFields.iterator(); iterator.hasNext();) {
					IField field = iterator.next();
					if(index>0){
						bf.append(" , ");
					}
					bf.append(fieldConverter.toColumn(field,true));
					index++;
				}
			}
				
			return bf.toString();
			
		}catch(Exception e){
			throw new ExpressionException(e);
		}
	}
	
	private void toSqlGroupBy(ISQLQueryObject sqlQueryObject)throws ExpressionException{
		
		ExpressionSQL.sqlGroupBy(this.sqlFieldConverter, sqlQueryObject, this.groupByFields);
		
	}
	
	protected static void sqlGroupBy(ISQLFieldConverter fieldConverter, ISQLQueryObject sqlQueryObject,List<IField> groupByFields) throws ExpressionException{
		try{
			
			if(groupByFields.size()>0){
				for (Iterator<IField> iterator = groupByFields.iterator(); iterator.hasNext();) {
					IField field = iterator.next();
					sqlQueryObject.addGroupBy(fieldConverter.toColumn(field,true));
				}
			}
			
		}catch(Exception e){
			throw new ExpressionException(e);
		}
	}
	
	private void toSqlGroupBySelectField(ISQLQueryObject sqlQueryObject)throws ExpressionException{
		
		ExpressionSQL.sqlGroupBySelectField(this.sqlFieldConverter, sqlQueryObject, this.fieldsManuallyAdd, 
				this.groupByFields, this.usedForCountExpression);
		
	}
	
	protected static void sqlGroupBySelectField(ISQLFieldConverter fieldConverter,  ISQLQueryObject sqlQueryObject, 
			List<Object> selectFieldsManuallyAdd, List<IField> groupByFields, boolean usedForCountExpression) throws ExpressionException{
		if(!usedForCountExpression){
			try{
				if(groupByFields!=null){
					for (IField iField : groupByFields) {
						
						//System.out.println("CHECK ["+iField.getFieldName()+"] ["+iField.getFieldType().getName()+"] ...");
						
						// check tra altri select field add manually
						boolean found = false;
						for (Object checkSelectFieldManuallyAdd : selectFieldsManuallyAdd) {
							if(checkSelectFieldManuallyAdd instanceof IField){		
								if(iField.equals(((IField)checkSelectFieldManuallyAdd))){
									found=true;
									break;
								}		
							}
						}
						//System.out.println("CHECK ["+iField.getFieldName()+"] ["+iField.getFieldType().getName()+"] found in selectFieldsManuallyAdd: "+found);
						if(found)
							continue;
						
						// check in sql Query Object
						String column1 = fieldConverter.toColumn(iField, true);
						String column2 = fieldConverter.toColumn(iField, false);
						//System.out.println("CHECK ["+iField.getFieldName()+"] ["+iField.getFieldType().getName()+"] COLUMN1["+column1+"] COLUMN2["+column2+"]");
						boolean insert = true;
						try{
							insert = sqlQueryObject.getFieldsName().contains(column1)==false && sqlQueryObject.getFieldsName().contains(column2)==false;
						}catch(org.openspcoop2.utils.sql.SQLQueryObjectException sql){}
						//System.out.println("CHECK ["+iField.getFieldName()+"] ["+iField.getFieldType().getName()+"] INSERT["+insert+"]");
						if(insert){
							ExpressionSQL.addField_engine(sqlQueryObject, fieldConverter, iField, null, true);
							selectFieldsManuallyAdd.add(iField);
						}
						
					}
				}
			}catch(Exception e){
				throw new ExpressionException(e.getMessage(),e);
			}
		}
	}
	
	
	
	// SQL FROM
	
	protected static void sqlFrom(ISQLQueryObject sqlQueryObject,List<IField> fields,ISQLFieldConverter sqlFieldConverter,String tableNamePrincipale,
			List<Object> fieldsManuallyAdd, List<OrderedField> orderByFields, List<IField> groupByFields) throws ExpressionException{
		sqlFrom(sqlQueryObject, fields, sqlFieldConverter, tableNamePrincipale, fieldsManuallyAdd, orderByFields, groupByFields, true);
	}
	protected static void sqlFrom(ISQLQueryObject sqlQueryObject,List<IField> fields,ISQLFieldConverter sqlFieldConverter,String tableNamePrincipale,
			List<Object> fieldsManuallyAdd, List<OrderedField> orderByFields, List<IField> groupByFields,
			boolean ignoreAlreadyExistsException) throws ExpressionException{
		try{
			List<String> tables = new ArrayList<String>();
			
			if(fields!=null){
				for (IField iField : fields) {
					String tableName = getTableName(iField,sqlFieldConverter);
					if(tables.contains(tableName)==false){
						tables.add(tableName);
					}
				}
			}
			
			if(fieldsManuallyAdd!=null){
				for (Object iField : fieldsManuallyAdd) {
					IField field = null;
					if(iField instanceof IField){
						field = (IField) iField;
						String tableName = getTableName(field,sqlFieldConverter);
						if(tables.contains(tableName)==false){
							tables.add(tableName);
						}
					}
					else if(iField instanceof FunctionField){
						List<IField> fieldsFF = ((FunctionField) iField).getFields();
						for (IField iFieldFF : fieldsFF) {
							String tableName = getTableName(iFieldFF,sqlFieldConverter);
							if(tables.contains(tableName)==false){
								tables.add(tableName);
							}
						}
					}
					else{
						throw new ExpressionException("Field type unknown ["+iField.getClass().getName()+"]");
					}
				}
			}
			
			if(orderByFields!=null){
				for (OrderedField orderedField : orderByFields) {
					IField iField = orderedField.getField();
					String tableName = getTableName(iField,sqlFieldConverter);
					if(tables.contains(tableName)==false){
						tables.add(tableName);
					}
				}
			}
			
			if(groupByFields!=null){
				for (IField iField : groupByFields) {
					String tableName = getTableName(iField,sqlFieldConverter);
					if(tables.contains(tableName)==false){
						tables.add(tableName);
					}
				}
			}
			
			if(tableNamePrincipale!=null && tables.contains(tableNamePrincipale)==false){
				tables.add(tableNamePrincipale);
			}
			
			for (String tableName : tables) {
				if(tableName==null || "".equals(tableName)){
					// la tabella "" puo' essere usato come workaround per le funzioni es. unixTimestamp in CustomField
					continue;
				}
				try{
					if(tableName.contains(_PREFIX_ALIASFIELD)){
						String originalTableName = tableName.split(_PREFIX_ALIASFIELD)[0];
						String aliasTable = tableName.split(_PREFIX_ALIASFIELD)[1];
						sqlQueryObject.addFromTable(originalTableName, aliasTable);
					}
					else{
						sqlQueryObject.addFromTable(tableName);
					}
				}
				catch(SQLQueryObjectAlreadyExistsException alreadyExists){
					if(!ignoreAlreadyExistsException){
						throw new ExpressionException(alreadyExists.getMessage(),alreadyExists);
					}
					else{
//						System.out.println("ALREADY EXISTS: "+alreadyExists.getMessage());
//						alreadyExists.printStackTrace(System.out);
					}
				}
			}
		}catch(Exception e){
			throw new ExpressionException(e.getMessage(),e);
		}
	}
	private static final String _PREFIX_ALIASFIELD = "_______ALIASFIELD_______";
	private static String getTableName(IField iField,ISQLFieldConverter sqlFieldConverter) throws ExpressionException{
		String tableName = null;
		if(iField instanceof AliasField){
			AliasField af = (AliasField) iField;
			if(af.getAlias().contains(".")){
				String originaleTableName = sqlFieldConverter.toTable(iField);
				tableName = originaleTableName+_PREFIX_ALIASFIELD+ af.getAlias().split("\\.")[0];
			}else{
				tableName = sqlFieldConverter.toTable(iField);
			}
		}
		else if(iField instanceof IAliasTableField){
			IAliasTableField atf = (IAliasTableField) iField;
			String originaleTableName = sqlFieldConverter.toTable(iField,false);
			tableName = originaleTableName+_PREFIX_ALIASFIELD+ atf.getAliasTable();
		}
		else{
			tableName = sqlFieldConverter.toTable(iField);
		}
		//System.out.println("ADD ["+tableName+"]");
		return tableName;
	}
	
	protected static void addField_engine(ISQLQueryObject sqlQueryObject, ISQLFieldConverter sqlFieldConverter, Object field, String aliasField, boolean appendTablePrefix)throws ExpressionException{
		addField_engine(sqlQueryObject, sqlFieldConverter, field, aliasField, appendTablePrefix, true);
	}
	protected static void addField_engine(ISQLQueryObject sqlQueryObject, ISQLFieldConverter sqlFieldConverter, Object field, String aliasField, boolean appendTablePrefix,
			boolean ignoreAlreadyExistsException)throws ExpressionException{
		try{		
					
			if(field == null){
				throw new ExpressionException("Field is null");
			}
			//System.out.println("ADD CLASS ["+field.getClass().getName()+"]...");
			if(field instanceof FunctionField){
				
				FunctionField ff = (FunctionField) field;
				
				boolean customFunction = ff.isCustomFunction();
				String prefixCustomFunction = ff.getPrefixFunctionCustom();
				String suffixCustomFunction = ff.getSuffixFunctionCustom();
				Function function = ff.getFunction();
				
				String functionValue = ff.getFunctionValue();
				String operator = ff.getOperator();
				List<IField> fields = ff.getFields();
				String column = null;
				
				boolean timestamp = false;
				if(ff.getFieldType().getName().equals(Timestamp.class.getName()) ||
						ff.getFieldType().getName().equals(java.util.Date.class.getName()) ||
						ff.getFieldType().getName().equals(java.sql.Date.class.getName()) ||
						ff.getFieldType().getName().equals(java.util.Calendar.class.getName())){
					timestamp = true;
				}
				
				String alias = ff.getAlias();
				
				// calcolo colonna
				if(functionValue!=null){
					column = functionValue;
				}
				else{
					if(operator!=null){
						if(fields.size()>1 && timestamp){
							throw new ExpressionException("Multiple fields with operator and \"time\" type ("+ff.getFieldType().getName()+
									") not supported. For Timestamp Interval use FunctionField(new UnixTimestampIntervalField(...), Function.TIPE, columnName)");
						}
						StringBuffer bf = new StringBuffer();
						for (int i = 0; i < fields.size(); i++) {
							if(i>0){
								bf.append(" ").append(operator).append(" ");
							}
							bf.append(sqlFieldConverter.toColumn(fields.get(i), appendTablePrefix));
						}
						column = bf.toString();
					}
					else{
						column = sqlFieldConverter.toColumn(fields.get(0), appendTablePrefix);
					}
				}
												
				if(customFunction){
					sqlQueryObject.addSelectAliasField(prefixCustomFunction+" "+column+" "+suffixCustomFunction, alias);
				}else{
					setFunction(function, timestamp, column, alias, sqlQueryObject);
				}
			}
			else if(field instanceof ConstantField){
				
				// Nelle costanti deve sempre essere usato l'alias.
				// Altrimenti nelle select annidate verrebbero dichiarate piu' volte le solite costanti.
				// Inoltre SQLServer pretende costanti con alias
				sqlQueryObject.addSelectAliasField(sqlFieldConverter.toColumn((ConstantField)field, appendTablePrefix), ((ConstantField)field).getAlias());
				
			}
			else if(field instanceof CustomField){
				
				if(aliasField!=null){
					sqlQueryObject.addSelectAliasField(sqlFieldConverter.toColumn((Field)field, appendTablePrefix), aliasField);
				}
				else{
					sqlQueryObject.addSelectField(sqlFieldConverter.toColumn((Field)field, appendTablePrefix));
				}
			}
			else if(field instanceof AliasField){
				
				AliasField af = (AliasField) field;
				IField afField = af.getField();
				sqlQueryObject.addSelectAliasField( sqlFieldConverter.toColumn(afField, appendTablePrefix) , af.getAlias() );
				//System.out.println("ADD ALIAS ["+sqlFieldConverter.toColumn(afField, appendTablePrefix)+"]["+af.getAlias()+"]...");
				
			}
			else if(field instanceof Field){
				
				if(aliasField!=null){
					sqlQueryObject.addSelectAliasField(sqlFieldConverter.toColumn((Field)field, appendTablePrefix), aliasField);
				}
				else{
					sqlQueryObject.addSelectField(sqlFieldConverter.toColumn((Field)field, appendTablePrefix));
				}
			}
			else if(field instanceof ComplexField){
				if(aliasField!=null){
					sqlQueryObject.addSelectAliasField(sqlFieldConverter.toColumn((ComplexField)field, appendTablePrefix), aliasField);
					//System.out.println("ADD ALIAS ["+sqlFieldConverter.toColumn((ComplexField)field, appendTablePrefix)+"]["+aliasField+"]...");
				}else{
					sqlQueryObject.addSelectField(sqlFieldConverter.toColumn((ComplexField)field, appendTablePrefix));
					//System.out.println("ADD ["+sqlFieldConverter.toColumn((ComplexField)field, appendTablePrefix)+"]...");
				}
			}
			else{
				throw new ExpressionException("Field unknown type: "+field.getClass().getName());
			}
		
		}
		catch(SQLQueryObjectAlreadyExistsException e){
			if(!ignoreAlreadyExistsException){
				throw new ExpressionException(e.getMessage(),e);
			}
			else{
//				System.out.println("ALREADY EXISTS: "+e.getMessage());
//				e.printStackTrace(System.out);
			}
		}
		catch(Exception e){
			throw new ExpressionException(e.getMessage(),e);
		}
	}
	
	protected static void addAliasField_engine(ISQLQueryObject sqlQueryObject, ISQLFieldConverter sqlFieldConverter, Object field, String aliasField, boolean appendTablePrefix)throws ExpressionException{
		addAliasField_engine(sqlQueryObject, sqlFieldConverter, field, aliasField, appendTablePrefix, true);
	}
	protected static void addAliasField_engine(ISQLQueryObject sqlQueryObject, ISQLFieldConverter sqlFieldConverter, Object field, String aliasField, boolean appendTablePrefix,
			boolean ignoreAlreadyExistsException)throws ExpressionException{
		try{		
					
			if(field == null){
				throw new ExpressionException("Field is null");
			}
			
			if(field instanceof FunctionField){
				
				FunctionField ff = (FunctionField) field;
				String alias = ff.getAlias();
				sqlQueryObject.addSelectField(alias);
				
			}
			else if(field instanceof ConstantField){
				
				sqlQueryObject.addSelectField(((ConstantField)field).getAlias());
				
			}
			else if(field instanceof CustomField){
				
				sqlQueryObject.addSelectField(((CustomField)field).getAliasColumnName());
				
			}
			else if(field instanceof AliasField){
				
				AliasField af = (AliasField) field;
				sqlQueryObject.addSelectField(af.getAlias());
				
			}
			else if(field instanceof Field){
				
				sqlQueryObject.addSelectField(sqlFieldConverter.toAliasColumn(((Field)field), false));
				
			}
			else if(field instanceof ComplexField){
				
				sqlQueryObject.addSelectField(sqlFieldConverter.toAliasColumn(((ComplexField)field), false));
				
			}
			else{
				throw new ExpressionException("Field unknown type: "+field.getClass().getName());
			}
		
		}
		catch(SQLQueryObjectAlreadyExistsException e){
			if(!ignoreAlreadyExistsException){
				throw new ExpressionException(e.getMessage(),e);
			}
			else{
//				System.out.println("ALREADY EXISTS: "+e.getMessage());
//				e.printStackTrace(System.out);
			}
		}
		catch(Exception e){
			throw new ExpressionException(e.getMessage(),e);
		}
	}
	
	public static void setFunction(Function function,boolean timestamp,String column,String alias,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException{
		switch (function) {
		case AVG:
		case AVG_DOUBLE:
			if(timestamp){
				sqlQueryObject.addSelectAvgTimestampField(column, alias);
			}
			else{
				sqlQueryObject.addSelectAvgField(column, alias);
			}
			break;
		case MAX:
			if(timestamp){
				sqlQueryObject.addSelectMaxTimestampField(column, alias);
			}
			else{
				sqlQueryObject.addSelectMaxField(column, alias);
			}
			break;
		case MIN:
			if(timestamp){
				sqlQueryObject.addSelectMinTimestampField(column, alias);
			}
			else{
				sqlQueryObject.addSelectMinField(column, alias);
			}
			break;
		case SUM:
			if(timestamp){
				sqlQueryObject.addSelectSumTimestampField(column, alias);
			}
			else{
				sqlQueryObject.addSelectSumField(column, alias);
			}
			break;
		case COUNT:
			sqlQueryObject.addSelectCountField(column, alias, false);
			break;
		case COUNT_DISTINCT:
			sqlQueryObject.addSelectCountField(column, alias, true);
			break;
		}
	}
	
	protected static boolean inUse(IField fieldParam,boolean checkOnlyWhereCondition, 
			boolean useFieldExpressionBase,List<Object> fieldsManuallyAdd,boolean checkFieldManuallyAdd) throws ExpressionNotImplementedException, ExpressionException {
		
		if(checkFieldManuallyAdd == false){
			return useFieldExpressionBase;
		}
		if(checkOnlyWhereCondition){
			return useFieldExpressionBase;
		}
		
		for (Object iField : fieldsManuallyAdd) {
			IField field = null;
			if(iField instanceof IField){
				field = (IField) iField;
				boolean inUse = fieldParam.equals(field);
				if(inUse){
					return true;
				}
			}
			else if(iField instanceof FunctionField){
				List<IField> fieldsFF = ((FunctionField) iField).getFields();
				for (IField iFieldFF : fieldsFF) {
					boolean inUse = fieldParam.equals(iFieldFF);
					if(inUse){
						return true;
					}
				}
			}
			else{
				throw new ExpressionException("Field type unknown ["+iField.getClass().getName()+"]");
			}
		}
		
		return useFieldExpressionBase;
		
	}
	
	protected static boolean inUse(IModel<?> model,boolean checkOnlyWhereCondition,
			boolean useModelExpressionBase,List<Object> fieldsManuallyAdd,boolean checkFieldManuallyAdd) throws ExpressionNotImplementedException, ExpressionException {
		
		if(checkFieldManuallyAdd == false){
			return useModelExpressionBase;
		}
		if(checkOnlyWhereCondition){
			return useModelExpressionBase;
		}
		
		for (Object iField : fieldsManuallyAdd) {
			IField field = null;
			if(iField instanceof IField){
				field = (IField) iField;
				if(_inUse(model, field)){
					return true;
				}
			}
			else if(iField instanceof FunctionField){
				List<IField> fieldsFF = ((FunctionField) iField).getFields();
				for (IField iFieldFF : fieldsFF) {
					if(_inUse(model, iFieldFF)){
						return true;
					}
				}
			}
			else{
				throw new ExpressionException("Field type unknown ["+iField.getClass().getName()+"]");
			}
		
			
		}
		
		return useModelExpressionBase;
		
	}
	private static boolean _inUse(IModel<?> model,IField field){
		boolean inUse = false;
		if(model.getBaseField()!=null){
			// Modello di un elemento non radice
			if(field instanceof ComplexField){
				ComplexField c = (ComplexField) field;
				inUse = c.getFather().equals(model.getBaseField());
			}else{
				inUse = model.getModeledClass().getName().equals(field.getClassType().getName());
			}
		}
		else{
			inUse = model.getModeledClass().getName().equals(field.getClassType().getName());
		}
		if(inUse){
			return true;
		}
		return false;
	}
	
	protected static List<IField> getFields(boolean onlyWhereCondition, 
			List<IField> getFieldExpressionBase,List<Object> fieldsManuallyAdd,boolean checkFieldManuallyAdd) throws ExpressionNotImplementedException, ExpressionException {
		if(checkFieldManuallyAdd == false){
			return getFieldExpressionBase;
		}
		if(onlyWhereCondition){
			return getFieldExpressionBase;
		}
		List<IField> newFields = new ArrayList<IField>();
		if(getFieldExpressionBase!=null){
			newFields.addAll(getFieldExpressionBase);
		}
		for (Object iField : fieldsManuallyAdd) {
			IField field = null;
			if(iField instanceof IField){
				field = (IField) iField;
				if(getFieldExpressionBase==null || (getFieldExpressionBase.contains(field)==false)){
					newFields.add(field);
				}
			}
			else if(iField instanceof FunctionField){
				List<IField> fieldsFF = ((FunctionField) iField).getFields();
				for (IField iFieldFF : fieldsFF) {
					if(getFieldExpressionBase==null || (getFieldExpressionBase.contains(iFieldFF)==false)){
						newFields.add(iFieldFF);
					}
				}
			}
			else{
				throw new ExpressionException("Field type unknown ["+iField.getClass().getName()+"]");
			}
		
		}	
		return newFields;
	}
	
	
	
	
	/* ************ TO SQL *********** */
	
	public String toSql() throws ExpressionException{
		if(this.expressionEngine==null){
			if(this.throwExpressionNotInitialized){
				throw new ExpressionException("Expression is not initialized");
			}
		}
		
		StringBuffer bf = null;
		if(this.expressionEngine==null){
			bf = new StringBuffer("");
		}
		else if(this.expressionEngine instanceof ISQLExpression){
			bf = new StringBuffer(((ISQLExpression)this.expressionEngine).toSql());
		}else{
			throw new ExpressionException("ExpressioneEngine (type:"+this.expressionEngine.getClass().getName()+") is not as cast with "+ISQLExpression.class.getName());
		}
		
		bf.append(toSqlGroupBy());
		bf.append(toSqlOrder());
		
		return bf.toString();
	}
	protected String toSqlPreparedStatement(List<Object> oggetti) throws ExpressionException {
		if(this.expressionEngine==null){
			if(this.throwExpressionNotInitialized){
				throw new ExpressionException("Expression is not initialized");
			}
		}
		
		StringBuffer bf = null;
		if(this.expressionEngine==null){
			bf = new StringBuffer("");
		}
		else if(this.expressionEngine instanceof ISQLExpression){
			bf = new StringBuffer(((ISQLExpression)this.expressionEngine).toSqlPreparedStatement(oggetti));
		}else{
			throw new ExpressionException("ExpressioneEngine (type:"+this.expressionEngine.getClass().getName()+") is not as cast with "+ISQLExpression.class.getName());
		}
		
		bf.append(toSqlGroupBy());
		bf.append(toSqlOrder());
		
		return bf.toString();
	}

	protected String toSqlJPA(Hashtable<String, Object> oggetti) throws ExpressionException {
		if(this.expressionEngine==null){
			if(this.throwExpressionNotInitialized){
				throw new ExpressionException("Expression is not initialized");
			}
		}
		
		StringBuffer bf = null;
		if(this.expressionEngine==null){
			bf = new StringBuffer("");
		}else if(this.expressionEngine instanceof ISQLExpression){
			bf = new StringBuffer(((ISQLExpression)this.expressionEngine).toSqlJPA(oggetti));
		}else{
			throw new ExpressionException("ExpressioneEngine (type:"+this.expressionEngine.getClass().getName()+") is not as cast with "+ISQLExpression.class.getName());
		}
		
		bf.append(toSqlGroupBy());
		bf.append(toSqlOrder());
		
		return bf.toString();
	}
	
	public void toSql(ISQLQueryObject sqlQueryObject)throws ExpressionException{
		if(this.expressionEngine==null){
			if(this.throwExpressionNotInitialized){
				throw new ExpressionException("Expression is not initialized");
			}
		}
		if(this.expressionEngine==null){			
		}
		else if(this.expressionEngine instanceof ISQLExpression){
			((ISQLExpression)this.expressionEngine).toSql(sqlQueryObject);
		}else{
			throw new ExpressionException("ExpressioneEngine (type:"+this.expressionEngine.getClass().getName()+") is not as cast with "+ISQLExpression.class.getName());
		}
		
		// GroupBy
		toSqlGroupBy(sqlQueryObject);
		
		// OrderBy
		toSqlOrder(sqlQueryObject);
		
		// Aggiungo select field relativi all'aggregazione
		toSqlGroupBySelectField(sqlQueryObject);
	}
	
	public void toSqlWithFromCondition(ISQLQueryObject sqlQueryObject,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		
		// preparo condizione di where
		this.toSql(sqlQueryObject);
		
		// aggiungo condizione di from
		sqlFrom(sqlQueryObject, this.getFields(false), this.getSqlFieldConverter(), tableNamePrincipale, this.getFieldsManuallyAdd(),
				this.getOrderedFields(),this.getGroupByFields());
	}
	
	protected void toSqlPreparedStatement(ISQLQueryObject sqlQueryObject,List<Object> oggetti)throws ExpressionException{
		if(this.expressionEngine==null){
			if(this.throwExpressionNotInitialized){
				throw new ExpressionException("Expression is not initialized");
			}
		}
		if(this.expressionEngine==null){			
		}
		else if(this.expressionEngine instanceof ISQLExpression){
			((ISQLExpression)this.expressionEngine).toSqlPreparedStatement(sqlQueryObject,oggetti);
		}else{
			throw new ExpressionException("ExpressioneEngine (type:"+this.expressionEngine.getClass().getName()+") is not as cast with "+ISQLExpression.class.getName());
		}
		
		// GroupBy
		toSqlGroupBy(sqlQueryObject);
		
		// OrderBy
		toSqlOrder(sqlQueryObject);
		
		// Aggiungo select field relativi all'aggregazione
		toSqlGroupBySelectField(sqlQueryObject);
	}
	
	protected void toSqlPreparedStatementWithFromCondition(ISQLQueryObject sqlQueryObject,List<Object> oggetti,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		
		// preparo condizione di where
		this.toSqlPreparedStatement(sqlQueryObject, oggetti);
		
		// aggiungo condizione di from
		sqlFrom(sqlQueryObject, this.getFields(false), this.getSqlFieldConverter(), tableNamePrincipale, this.getFieldsManuallyAdd(),
				this.getOrderedFields(),this.getGroupByFields());
	}
	
	protected void toSqlJPA(ISQLQueryObject sqlQueryObject,Hashtable<String, Object> oggetti)throws ExpressionException{
		if(this.expressionEngine==null){
			if(this.throwExpressionNotInitialized){
				throw new ExpressionException("Expression is not initialized");
			}
		}
		
		if(this.expressionEngine==null){	
		}
		else if(this.expressionEngine instanceof ISQLExpression){
			((ISQLExpression)this.expressionEngine).toSqlJPA(sqlQueryObject,oggetti);
		}else{
			throw new ExpressionException("ExpressioneEngine (type:"+this.expressionEngine.getClass().getName()+") is not as cast with "+ISQLExpression.class.getName());
		}
		
		// GroupBy
		toSqlGroupBy(sqlQueryObject);
		
		// OrderBy
		toSqlOrder(sqlQueryObject);
				
		// Aggiungo select field relativi all'aggregazione
		toSqlGroupBySelectField(sqlQueryObject);
	}
	
	protected void toSqlJPAWithFromCondition(ISQLQueryObject sqlQueryObject,Hashtable<String, Object> oggetti,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		
		// preparo condizione di where
		this.toSqlJPA(sqlQueryObject, oggetti);
		
		// aggiungo condizione di from
		sqlFrom(sqlQueryObject, this.getFields(false), this.getSqlFieldConverter(), tableNamePrincipale, this.getFieldsManuallyAdd(),
				this.getOrderedFields(),this.getGroupByFields());
	}
	
	public void addField(ISQLQueryObject sqlQueryObject, IField field, boolean appendTablePrefix)throws ExpressionException{
		ExpressionSQL.addField_engine(sqlQueryObject,this.getSqlFieldConverter(),field, null, appendTablePrefix);
		this.getFieldsManuallyAdd().add(field);
	}
	public void addField(ISQLQueryObject sqlQueryObject, IField field, String aliasField, boolean appendTablePrefix)throws ExpressionException{
		ExpressionSQL.addField_engine(sqlQueryObject,this.getSqlFieldConverter(),field, aliasField, appendTablePrefix);
		this.getFieldsManuallyAdd().add(field);
	}
	public void addAliasField(ISQLQueryObject sqlQueryObject, IField field, boolean appendTablePrefix)throws ExpressionException{
		ExpressionSQL.addAliasField_engine(sqlQueryObject,this.getSqlFieldConverter(),field, null, appendTablePrefix);
		this.getFieldsManuallyAdd().add(field);
	}
	public void addField(ISQLQueryObject sqlQueryObject, FunctionField field, boolean appendTablePrefix)throws ExpressionException{
		ExpressionSQL.addField_engine(sqlQueryObject,this.getSqlFieldConverter(),field, null, appendTablePrefix);
		this.getFieldsManuallyAdd().add(field);
	}
	
	
	/* ************ OBJECTS ************ */
	@Override
	protected ComparatorExpressionImpl getComparatorExpression(IField field, Object value, Comparator c)throws ExpressionException {
		return new ComparatorExpressionSQL(this.sqlFieldConverter,this.objectFormatter,field,value,c);
	}
	@Override
	protected BetweenExpressionImpl getBetweenExpression(IField field, Object lower, Object high)throws ExpressionException {
		return new BetweenExpressionSQL(this.sqlFieldConverter,this.objectFormatter,field,lower,high);
	}
	@Override
	protected InExpressionImpl getInExpression(IField field, Object... values) throws ExpressionException {
		List<Object> lista = new ArrayList<Object>();
		if(values!=null){
			for (int i = 0; i < values.length; i++) {
				lista.add(values[i]);
			}
		}
		return new InExpressionSQL(this.sqlFieldConverter,this.objectFormatter,field, lista);
	}
	@Override
	protected LikeExpressionImpl getLikeExpression(IField field, String value, LikeMode mode, boolean caseInsensitive) throws ExpressionException {
		return new LikeExpressionSQL(this.sqlFieldConverter,this.objectFormatter,field, value, mode, caseInsensitive);
	}
	@Override
	protected ConjunctionExpressionImpl getConjunctionExpression() throws ExpressionException {
		return new ConjunctionExpressionSQL(this.sqlFieldConverter,this.objectFormatter);
	}
	
}
