/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.LikeMode;
import org.openspcoop2.generic_project.expression.impl.BetweenExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.Comparator;
import org.openspcoop2.generic_project.expression.impl.ComparatorExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.ConjunctionExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.InExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.LikeExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.PaginatedExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**
 * PaginatedExpressionSQL
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PaginatedExpressionSQL extends PaginatedExpressionImpl {

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
	
	public PaginatedExpressionSQL(ISQLFieldConverter sqlFieldConverter) throws ExpressionException {
		super();
		this.sqlFieldConverter = sqlFieldConverter;
		if(this.sqlFieldConverter!=null){
			this.databaseType = this.sqlFieldConverter.getDatabaseType();
		}
	}
	public PaginatedExpressionSQL(ISQLFieldConverter sqlFieldConverter,IObjectFormatter objectFormatter) throws ExpressionException{
		super(objectFormatter);
		this.sqlFieldConverter = sqlFieldConverter;
		if(this.sqlFieldConverter!=null){
			this.databaseType = this.sqlFieldConverter.getDatabaseType();
		}
	}
	public PaginatedExpressionSQL(ExpressionSQL expression) throws ExpressionException {
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
	

	
	/* ************ TO SQL *********** */
	
	private String toSql_engine(SQLMode mode,List<Object> oggettiPreparedStatement,Hashtable<String, Object> oggettiJPA)throws ExpressionException{
			
		ISQLExpression sqlExpression = null;
		if(this.expressionEngine!=null){
			if(this.expressionEngine instanceof ISQLExpression){
				sqlExpression = (ISQLExpression)this.expressionEngine;
			}else{
				throw new ExpressionException("ExpressioneEngine (type:"+this.expressionEngine.getClass().getName()+") is not as cast with "+ISQLExpression.class.getName());
			}
		}else{
			if(this.throwExpressionNotInitialized)
				throw new ExpressionException("Expression is not initialized");
		}
		
		String s = null;
		if(this.expressionEngine!=null){
			switch (mode) {
			case STANDARD:
				s = sqlExpression.toSql();
				break;
			case PREPARED_STATEMENT:
				s = sqlExpression.toSqlPreparedStatement(oggettiPreparedStatement);
				break;
			case JPA:
				s = sqlExpression.toSqlJPA(oggettiJPA);
				break;
			}
		}else{
			s = "";
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append(s);

		bf.append(ExpressionSQL.sqlGroupBy(this.sqlFieldConverter, this.getGroupByFields()));
		
		bf.append(ExpressionSQL.sqlOrder(this.sqlFieldConverter, this.getSortOrder(), this.getOrderedFields()));
				
		if(!SQLMode.JPA.equals(mode)){
			if(this.getLimit()!=null && this.getLimit()>=0){
				bf.append(" LIMIT "+this.getLimit());
			}
			if(this.getOffset()!=null && this.getOffset()>=0){
				bf.append(" OFFSET "+this.getOffset());
			}
		}
		
		return bf.toString();
		
	}
	
	private void toSql_engine(ISQLQueryObject sqlQueryObject,SQLMode mode,List<Object> oggettiPreparedStatement,Hashtable<String, Object> oggettiJPA)throws ExpressionException{
	
		ISQLExpression sqlExpression = null;
		if(this.expressionEngine!=null){
			if(this.expressionEngine instanceof ISQLExpression){
				sqlExpression = (ISQLExpression)this.expressionEngine;
			}else{
				throw new ExpressionException("ExpressioneEngine (type:"+this.expressionEngine.getClass().getName()+") is not as cast with "+ISQLExpression.class.getName());
			}
		}else{
			if(this.throwExpressionNotInitialized)
				throw new ExpressionException("Expression is not initialized");
		}
		
		if(this.expressionEngine!=null){
			switch (mode) {
			case STANDARD:
				sqlExpression.toSql(sqlQueryObject);
				break;
			case PREPARED_STATEMENT:
				sqlExpression.toSqlPreparedStatement(sqlQueryObject,oggettiPreparedStatement);
				break;
			case JPA:
				sqlExpression.toSqlJPA(sqlQueryObject,oggettiJPA);
				break;
			}
		}
		
		try{
				
			// GroupBy
			ExpressionSQL.sqlGroupBy(this.sqlFieldConverter, sqlQueryObject, this.getGroupByFields());
						
			// OrderBy
			ExpressionSQL.sqlOrder(this.sqlFieldConverter, sqlQueryObject,this.getSortOrder(), this.getOrderedFields(), this.getGroupByFields());
						
			// Aggiungo select field relativi all'aggregazione
			ExpressionSQL.sqlGroupBySelectField(this.sqlFieldConverter, sqlQueryObject, this.getFieldsManuallyAdd(), 
					this.getGroupByFields(), this.usedForCountExpression);
						
			if(!SQLMode.JPA.equals(mode)){
				if(this.getLimit()!=null && this.getLimit()>=0){
					sqlQueryObject.setLimit(this.getLimit());
				}
				
				if(this.getOffset()!=null && this.getOffset()>=0){
					sqlQueryObject.setOffset(this.getOffset());
				}
			}
			
		}catch(Exception e){
			throw new ExpressionException(e);
		}
		
	}
	
	public String toSql() throws ExpressionException{
		return toSql_engine(SQLMode.STANDARD, null, null);
	}
	protected String toSqlPreparedStatement(List<Object> oggetti) throws ExpressionException {
		return toSql_engine(SQLMode.PREPARED_STATEMENT, oggetti, null);
	}
	protected String toSqlJPA(Hashtable<String, Object> oggetti) throws ExpressionException {
		return toSql_engine(SQLMode.JPA, null, oggetti);
	}
	
	public void toSql(ISQLQueryObject sqlQueryObject)throws ExpressionException{
		toSql_engine(sqlQueryObject,SQLMode.STANDARD, null, null);
	}
	public void toSqlWithFromCondition(ISQLQueryObject sqlQueryObject,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		
		// preparo condizione di where
		this.toSql(sqlQueryObject);
		
		// aggiungo condizione di from
		ExpressionSQL.sqlFrom(sqlQueryObject, this.getFields(false), this.getSqlFieldConverter(), tableNamePrincipale, this.getFieldsManuallyAdd(),
				this.getOrderedFields(),this.getGroupByFields());
	}
	
	protected void toSqlPreparedStatement(ISQLQueryObject sqlQueryObject,List<Object> oggetti)throws ExpressionException{
		toSql_engine(sqlQueryObject,SQLMode.PREPARED_STATEMENT, oggetti, null);
	}
	protected void toSqlPreparedStatementWithFromCondition(ISQLQueryObject sqlQueryObject,List<Object> oggetti,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		
		// preparo condizione di where
		this.toSqlPreparedStatement(sqlQueryObject, oggetti);
		
		// aggiungo condizione di from
		ExpressionSQL.sqlFrom(sqlQueryObject, this.getFields(false), this.getSqlFieldConverter(), tableNamePrincipale, this.getFieldsManuallyAdd(),
				this.getOrderedFields(),this.getGroupByFields());
	}
	
	protected void toSqlJPA(ISQLQueryObject sqlQueryObject,Hashtable<String, Object> oggetti)throws ExpressionException{
		toSql_engine(sqlQueryObject,SQLMode.JPA, null, oggetti);
	}
	protected void toSqlJPAWithFromCondition(ISQLQueryObject sqlQueryObject,Hashtable<String, Object> oggetti,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		
		// preparo condizione di where
		this.toSqlJPA(sqlQueryObject, oggetti);
		
		// aggiungo condizione di from
		ExpressionSQL.sqlFrom(sqlQueryObject, this.getFields(false), this.getSqlFieldConverter(), tableNamePrincipale, this.getFieldsManuallyAdd(),
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
