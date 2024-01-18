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
package org.openspcoop2.generic_project.expression.impl.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import org.openspcoop2.generic_project.expression.impl.DateTimePartExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.DayFormatExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.InExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.LikeExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.PaginatedExpressionImpl;
import org.openspcoop2.generic_project.expression.impl.formatter.IObjectFormatter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.DateTimePartEnum;
import org.openspcoop2.utils.sql.DayFormatEnum;
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
		/**this.checkFieldManuallyAdd = expression.checkFieldManuallyAdd;*/
		if(this.sqlFieldConverter!=null){
			this.databaseType = this.sqlFieldConverter.getDatabaseType();
		}
	}
	
	private List<Object> fieldsManuallyAdd = new ArrayList<>();
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
	
	
	/* ************ COMPARATOR *********** */
	
	@Override
	protected Comparator getCorrectComparator(Comparator comparator){
		return ExpressionSQL.getCorrectComparator(comparator, this.databaseType);
	}
	

	
	/* ************ TO SQL *********** */
	
	private String toSqlEngine(SQLMode mode,List<Object> oggettiPreparedStatement,Map<String, Object> oggettiJPA)throws ExpressionException{
			
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
		
		StringBuilder bf = new StringBuilder();
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
		
		bf.append(ExpressionSQL.sqlForceIndex(this.sqlFieldConverter, this.getForceIndexes())); // Lo metto in fondo tanto sono commenti
		
		return bf.toString();
		
	}
	
	private void toSqlEngine(ISQLQueryObject sqlQueryObject,SQLMode mode,List<Object> oggettiPreparedStatement,Map<String, Object> oggettiJPA)throws ExpressionException{
	
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
			
			// ForceIndex
			ExpressionSQL.sqlForceIndex(this.sqlFieldConverter, sqlQueryObject, this.getForceIndexes());
			
		}catch(Exception e){
			throw new ExpressionException(e);
		}
		
	}
	
	public String toSql() throws ExpressionException{
		return toSqlEngine(SQLMode.STANDARD, null, null);
	}
	protected String toSqlPreparedStatement(List<Object> oggetti) throws ExpressionException {
		return toSqlEngine(SQLMode.PREPARED_STATEMENT, oggetti, null);
	}
	protected String toSqlJPA(Map<String, Object> oggetti) throws ExpressionException {
		return toSqlEngine(SQLMode.JPA, null, oggetti);
	}
	
	public void toSql(ISQLQueryObject sqlQueryObject)throws ExpressionException{
		toSqlEngine(sqlQueryObject,SQLMode.STANDARD, null, null);
	}
	public void toSqlWithFromCondition(ISQLQueryObject sqlQueryObject,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		
		// preparo condizione di where
		this.toSql(sqlQueryObject);
		
		// aggiungo condizione di from
		ExpressionSQL.sqlFrom(sqlQueryObject, this.getFields(false), this.getSqlFieldConverter(), tableNamePrincipale, this.getFieldsManuallyAdd(),
				this.getOrderedFields(),this.getGroupByFields());
	}
	
	protected void toSqlPreparedStatement(ISQLQueryObject sqlQueryObject,List<Object> oggetti)throws ExpressionException{
		toSqlEngine(sqlQueryObject,SQLMode.PREPARED_STATEMENT, oggetti, null);
	}
	protected void toSqlPreparedStatementWithFromCondition(ISQLQueryObject sqlQueryObject,List<Object> oggetti,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		
		// preparo condizione di where
		this.toSqlPreparedStatement(sqlQueryObject, oggetti);
		
		// aggiungo condizione di from
		ExpressionSQL.sqlFrom(sqlQueryObject, this.getFields(false), this.getSqlFieldConverter(), tableNamePrincipale, this.getFieldsManuallyAdd(),
				this.getOrderedFields(),this.getGroupByFields());
	}
	
	protected void toSqlJPA(ISQLQueryObject sqlQueryObject,Map<String, Object> oggetti)throws ExpressionException{
		toSqlEngine(sqlQueryObject,SQLMode.JPA, null, oggetti);
	}
	protected void toSqlJPAWithFromCondition(ISQLQueryObject sqlQueryObject,Map<String, Object> oggetti,String tableNamePrincipale) throws ExpressionException, ExpressionNotImplementedException{
		
		// preparo condizione di where
		this.toSqlJPA(sqlQueryObject, oggetti);
		
		// aggiungo condizione di from
		ExpressionSQL.sqlFrom(sqlQueryObject, this.getFields(false), this.getSqlFieldConverter(), tableNamePrincipale, this.getFieldsManuallyAdd(),
				this.getOrderedFields(),this.getGroupByFields());
	}
	
	public void addField(ISQLQueryObject sqlQueryObject, IField field, boolean appendTablePrefix)throws ExpressionException{
		ExpressionSQL.addFieldEngine(sqlQueryObject,this.getSqlFieldConverter(),field, null, appendTablePrefix);
		this.getFieldsManuallyAdd().add(field);
	}
	public void addField(ISQLQueryObject sqlQueryObject, IField field, String aliasField, boolean appendTablePrefix)throws ExpressionException{
		ExpressionSQL.addFieldEngine(sqlQueryObject,this.getSqlFieldConverter(),field, aliasField, appendTablePrefix);
		this.getFieldsManuallyAdd().add(field);
	}
	public void addAliasField(ISQLQueryObject sqlQueryObject, IField field, boolean appendTablePrefix)throws ExpressionException{
		ExpressionSQL.addAliasFieldEngine(sqlQueryObject,this.getSqlFieldConverter(),field, null, appendTablePrefix);
		this.getFieldsManuallyAdd().add(field);
	}
	public void addField(ISQLQueryObject sqlQueryObject, FunctionField field, boolean appendTablePrefix)throws ExpressionException{
		ExpressionSQL.addFieldEngine(sqlQueryObject,this.getSqlFieldConverter(),field, null, appendTablePrefix);
		this.getFieldsManuallyAdd().add(field);
	}

	
	
	/* ************ OBJECTS ************ */
	@Override
	protected ComparatorExpressionImpl getComparatorExpression(IField field, Object value, Comparator c) {
		return new ComparatorExpressionSQL(this.sqlFieldConverter,this.objectFormatter,field,value,c);
	}
	@Override
	protected BetweenExpressionImpl getBetweenExpression(IField field, Object lower, Object high) {
		return new BetweenExpressionSQL(this.sqlFieldConverter,this.objectFormatter,field,lower,high);
	}
	@Override
	protected InExpressionImpl getInExpression(IField field, Object... values) {
		List<Object> lista = new ArrayList<>();
		if(values!=null && values.length>0){
			lista.addAll(Arrays.asList(values));
		}
		return new InExpressionSQL(this.sqlFieldConverter,this.objectFormatter,field, lista);
	}
	@Override
	protected LikeExpressionImpl getLikeExpression(IField field, String value, LikeMode mode, boolean caseInsensitive) {
		return new LikeExpressionSQL(this.sqlFieldConverter,this.objectFormatter,field, value, mode, caseInsensitive);
	}
	@Override
	protected DateTimePartExpressionImpl getDateTimePartExpression(IField field, String value, DateTimePartEnum dateTimePartEnum) {
		return new DateTimePartExpressionSQL(this.sqlFieldConverter,this.objectFormatter,field, value, dateTimePartEnum);
	}
	@Override
	protected DayFormatExpressionImpl getDayFormatExpression(IField field, String value, DayFormatEnum dayFormatEnum) {
		return new DayFormatExpressionSQL(this.sqlFieldConverter,this.objectFormatter,field, value, dayFormatEnum);
	}
	@Override
	protected ConjunctionExpressionImpl getConjunctionExpression() {
		return new ConjunctionExpressionSQL(this.sqlFieldConverter,this.objectFormatter);
	}
	
}
